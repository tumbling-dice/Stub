public final class HttpImageManager {
	
	private static final int MAX_THREAD = 128;
	private static Handler _handler;
	private static ThreadPoolExecutor _executor;
	
	static {
		int coreCount = Runtime.getRuntime().availableProcessors();
		_executor = new ThreadPoolExecutor(coreCount, MAX_THREAD, 1L, TimeUnit.SECOND
			, new LinkedBlockingQueue<ImageRunnable>(coreCount));
		
		_executor.setRejectedExecutionHandler(new RejectedExecutionHandler(){
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				if(r instanceof ImageFuture) ((ImageFuture) r).cancel(true);
			}
		});
		
		_handler = new Handler(Looper.getMainLooper());
	}
	
	public static class ImageCaller extends Callable<BitmapResult> {
		
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
			return ImageUtil.getBitmapFromHttp(_url, _maxSize, _timeout, _isSaveCache, _isSaveFile);
		}
	}
	
	static class ImageFuture extends FutureTask<BitmapResult> {
		
		private Handler _handler;
		private ImageCallback _callback;
		private Action1<Exception> _onError;
		private boolean _isThrowException;
		
		public ImageFuture(Handler handler, ImageCaller caller, ImageCallback callback, boolean isThrowException) {
			super(caller);
			_handler = handler;
			_callback = callback;
			_isThrowException = isThrowException;
		}
		
		public ImageFuture(Handler handler, ImageCaller caller, ImageCallback callback, Action1<Exception> onError) {
			super(caller);
			_handler = handler;
			_callback = callback;
			_onError = onError;
		}
		
		public void notThrowException() {
			_isThrowException = false;
		}
		
		@Override
		protected void done() {
			try {
				BitmapResult result = get();
				
				if(!result.hasError() && _handler != null) {
					_handler.post(new Runnable() {
						@Override
						public void run() {
							if(_callback != null)
								_callback.call(_key, result.getBitmap());
							destroy();
						}
					});
				} else {
					onError(result.getError(), _isThrowException);
				}
			} catch (InterruptedException e) {
				onError(e, false);
			} catch (ExecutionException e) {
				onError(e, _isThrowException);
			} catch (CancellationException e) {
				onError(e, false);
			}
		}
		
		private void onError(final Exception e, boolean isThrowException) {
			if(_onError != null && _handler != null) {
				_handler.post(new Runnable() {
					@Override
					public void run() {
						if(_onError != null)
							_onError.call(e);
						destroy();
					}
				});
			} else {
				if(isThrowException) {
					destroy();
					throw new RuntimeException(e);
				} else {
					destroy();
					e.printStackTrace();
				}
			}
		}
		
		public void destroy() {
			_onError = null;
			_callback = null;
			_handler = null;
		}
	}
	
	public static void execute(ImageCaller caller, ImageCallback callback, boolean isThrowException) {
		_executor.execute(new ImageFuture(_handler, caller, callback, isThrowException));
	}
	
	public static void execute(ImageCaller caller, ImageCallback callback, Action1<Exception> onError) {
		_executor.execute(new ImageFuture(_handler, caller, callback, onError));
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