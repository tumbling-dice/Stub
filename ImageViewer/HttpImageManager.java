public final class HttpImageManager {
	
	private static final int MAX_THREAD = 128;
	private static final int MSG_CALLBACK = 0;
	private static final int MSG_ONERROR = 1;
	
	private static ThreadPoolExecutor _executor;
	
	static {
		int coreCount = Runtime.getRuntime().availableProcessors();
		
		_executor = new ThreadPoolExecutor(coreCount, MAX_THREAD, 1L, TimeUnit.SECOND
			, new LinkedBlockingQueue<ImageRunnable>(coreCount)
			, new ThreadFactory() {
				private final AtomicInteger _count = new AtomicInteger(1);
				
				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r, "HttpImageManager #" + _.getAndIncrement());
					t.setDaemon(true);
					return t;
				}
				
			}, new RejectedExecutionHandler(){
				@Override
				public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
					if(r instanceof ImageFuture) ((ImageFuture) r).cancel(true);
				}
			});
	}
	
	public interface ImageCallback {
		void call(Object key, Bitmap bitmap);
	}
	
	public interface ErrorHandler {
		void call(Exception e);
	}
	
	public static class ImageCaller implements Callable<BitmapResult> {
		
		private final String _url
		private final Point _maxSize;
		private boolean _isSaveCache;
		private boolean _isSaveFile;
		private int _timeout = 30000;
		
		public ImageCaller(String url, Point maxSize) {
			_url = url;
			_maxSize = maxSize;
		}
		
		public void setSaveCache(boolean isSaveCache) {
			_isSaveCache = isSaveCache;
		}
		
		public void setSaveFile(boolean isSaveFile) {
			_isSaveFile = isSaveFile;
		}
		
		public void setTimeout(int timeout) {
			_timeout = timeout;
		}
		
		@Override
		public BitmapResult call() throws Exception {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			return ImageUtil.getBitmapFromHttp(_url, _maxSize, _timeout, _isSaveCache, _isSaveFile);
		}
	}
	
	static class ImageHandler extends Handler {
		
		private Object _key;
		private ImageCallback _callback;
		private ErrorHandler _onError;
		private boolean _isThrowException;
		
		public ImageHandler(Looper looper, Object key, ImageCallback callback, boolean isThrowException) {
			super(looper);
			_key = key;
			_callback = callback;
			_isThrowException = isThrowException;
		}
		
		public ImageHandler(Looper looper, Object key, ImageCallback callback, ErrorHandler onError) {
			super(looper);
			_key = key;
			_callback = callback;
			_onError = onError;
		}
		
		public void setNotThrowException() {
			_isThrowException = false;
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_CALLBACK:
				_callback.call(_key, (Bitmap) msg.obj);
				break;
			case MSG_ONERROR:
				Exception e = (Exception) msg.obj;
				if(_onError != null) {
					_onError.call(e);
				} else if(_isThrowException) {
					_callback = null;
					new RuntimeException(e);
				} else {
					e.printStackTrace();
				}
				break;
			}
			
			_callback = null;
			_onError = null;
			_key = null;
		}
	}
	
	static class ImageFuture extends FutureTask<BitmapResult> {
		
		private ImageHandler _handler;
		
		public ImageFuture(Looper looper, Object key, ImageCaller caller, ImageCallback callback, boolean isThrowException) {
			super(caller);
			_handler = new ImageHandler(looper, key, callback, isThrowException);
		}
		
		public ImageFuture(Looper looper, Object key, ImageCaller caller, ImageCallback callback, ErrorHandler onError) {
			super(caller);
			_handler = new ImageHandler(looper, key, callback, onError);
		}
		
		@Override
		protected void done() {
			try {
				BitmapResult result = get();
				
				if(!result.hasError()) {
					_handler.obtainMessage(MSG_CALLBACK, result.getBitmap()).sendToTarget();
				} else {
					_handler.obtainMessage(MSG_ONERROR, result.getError()).sendToTarget();
				}
			} catch (InterruptedException e) {
				_handler.setNotThrowException();
				_handler.obtainMessage(MSG_ONERROR, e).sendToTarget();
			} catch (ExecutionException e) {
				_handler.obtainMessage(MSG_ONERROR, e).sendToTarget();
			} catch (CancellationException e) {
				_handler.setNotThrowException();
				_handler.obtainMessage(MSG_ONERROR, e).sendToTarget();
			} catch (Exception e) {
				_handler.setNotThrowException();
				_handler.obtainMessage(MSG_ONERROR, e).sendToTarget();
			} finally {
				_handler = null;
			}
		}
	}
	
	public static void execute(Object key, ImageCaller caller, ImageCallback callback, boolean isThrowException) {
		_executor.execute(new ImageFuture(Looper.getMainLooper(), key, caller, callback, isThrowException));
	}
	
	public static void execute(Object key, ImageCaller caller, ImageCallback callback, ErrorHandler onError) {
		_executor.execute(new ImageFuture(Looper.getMainLooper(), key, caller, callback, onError));
	}
	
	public static void shutdown() {
		try {
			_executor.shutdown();
			if(!_executor.awaitTermination(1L, TimeUnit.SECOND)) {
				_executor.shutdownNow();
			}
		} catch(InterruptedException e) {
			_executor.shutdownNow();
		}
	}
}