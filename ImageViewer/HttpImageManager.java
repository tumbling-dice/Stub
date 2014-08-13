public final class HttpImageManager {
	
	private static final int MAX_THREAD = 128;
	private static final int MSG_CALLBACK = 0;
	private static final int MSG_ONERROR = 1;
	
	private static ThreadPoolExecutor _executor;
	/** 処理中のkeyを保存しておくList */
	private static final ArrayList<Object> _keyHolder = new ArrayList<Object>();
	
	static {
		init();
	}
	
	public static void execute(Object key, ImageCaller caller, ImageCallback callback, boolean isThrowException) {
		
		synchronized(_keyHolder) {
			if(_keyHolder.contains(key)) return;
			_keyHolder.add(key);
			if(_executor == null) init();
		}
		
		_executor.execute(new ImageFuture(key, caller, callback, isThrowException));
	}
	
	public static void execute(Object key, ImageCaller caller, ImageCallback callback, ErrorHandler onError) {
		
		synchronized(_keyHolder) {
			if(_keyHolder.contains(key)) return;
			_keyHolder.add(key);
			if(_executor == null) init();
		}
		
		_executor.execute(new ImageFuture(key, caller, callback, onError));
	}
	
	public static void shutdown() {
		
		if(_executor == null) return;
		
		try {
			_executor.shutdown();
			if(!_executor.awaitTermination(1L, TimeUnit.SECONDS)) {
				_executor.shutdownNow();
			}
		} catch(InterruptedException e) {
			_executor.shutdownNow();
		} finally {
			_executor = null;
			_keyHolder.clear();
		}
	}
	
	private static void init() {
		_executor = new ThreadPoolExecutor(3, MAX_THREAD
			, 1L, TimeUnit.SECONDS
			, new LinkedBlockingQueue<Runnable>(20)
			, new ThreadFactory() {
				private final AtomicInteger _count = new AtomicInteger(1);
				
				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r, "HttpImageManager #" + _count.getAndIncrement());
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
	
	/** Handler内で使用するコールバック */
	public interface ImageCallback {
		void call(Object key, Bitmap bitmap);
	}
	
	/** Handler内で使用するエラーハンドラ */
	public interface ErrorHandler {
		void call(Exception e);
	}
	
	/** バックグラウンドスレッドで実行する内容（Caller） */
	public static class ImageCaller implements Callable<BitmapResult> {
		
		private final String _url;
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
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			return ImageUtil.getBitmapFromHttp(_url, _maxSize, _timeout, _isSaveCache, _isSaveFile);
		}
	}
	
	/** ImageCallerの処理結果を受け取り、ImageHandlerに渡すFutureTask */
	static class ImageFuture extends FutureTask<BitmapResult> {
		
		private ImageHandler _handler;
		
		public ImageFuture(Object key, ImageCaller caller, ImageCallback callback, boolean isThrowException) {
			super(caller);
			_handler = new ImageHandler(key, callback, isThrowException);
		}
		
		public ImageFuture(Object key, ImageCaller caller, ImageCallback callback, ErrorHandler onError) {
			super(caller);
			_handler = new ImageHandler(key, callback, onError);
		}
		
		@Override
		protected void done() {
			// 例外が発生してなければMessage#whatにMSG_CALLBACKを、objにbitmapを渡す
			// （Executor関連も含む）例外が発生した場合はMessage#whatにMSG_ONERRORを、objにExceptionを渡す
			
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
	
	/** UIスレッド内で実行する内容（Handler） */
	static class ImageHandler extends Handler {
		
		private Object _key;
		private ImageCallback _callback;
		private ErrorHandler _onError;
		private boolean _isThrowException;
		
		// コンストラクタはHandler(Looper)を継承する
		// UIスレッドで確実に実行するため、LooperはLooper#getMainLooper()のみを使用する
		
		public ImageHandler(Object key, ImageCallback callback, boolean isThrowException) {
			super(Looper.getMainLooper());
			_key = key;
			_callback = callback;
			_isThrowException = isThrowException;
		}
		
		public ImageHandler(Object key, ImageCallback callback, ErrorHandler onError) {
			super(Looper.getMainLooper());
			_key = key;
			_callback = callback;
			_onError = onError;
		}
		
		public void setNotThrowException() {
			_isThrowException = false;
		}
		
		@Override
		public void handleMessage(Message msg) {
			synchronized(_keyHolder) {
				_keyHolder.remove(_key);
			}
			
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
					_onError = null;
					_key = null;
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
	
}