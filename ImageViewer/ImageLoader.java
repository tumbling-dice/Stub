public class ImageLoader extends AsyncTask<Uri, Void, ReactiveAsyncResult<Bitmap>> {
	
	private Action1<Bitmap> _callback;
	private Action1<Exception> _onError;
	private Point _maxSize;
	
	public ImageLoader(Point maxSize, Action1<Bitmap> callback) {
		_maxSize = maxSize;
		_callback = callback;
	}
	
	public ImageLoader(Point maxSize, Action1<Bitmap> callback, Action1<Exception> onError) {
		_maxSize = maxSize;
		_callback = callback;
		_onError = onError;
	}
	
	@Override
	protected ReactiveAsyncResult<Bitmap> doInBackground(Uri... param) {
		return getBitmapFromUri(param[0], _maxSize);
	}
	
	public static ReactiveAsyncResult<Bitmap> getBitmapFromUri(final Uri uri, Point maxSize) {
		return BitmapUtil.getBitmapFromStream(maxSize, new Func<InputStream>(){
			@Override
			public InputStream call() {
				HttpURLConnection connection = null;
				
				try {
					URL url = new URL(uri.toString());
					connection = (HttpURLConnection) url.openConnection();
					connection.setDoInput(true);
					connection.connect();
					return connection.getInputStream();
				} catch(Exception e) {
					throw new RuntimeException(e);
				} finally {
					if(connection != null) {
						connection.disconnect()
					}
				}
			}
		});
	}
	
	@Override
	protected void onPostExecute(ReactiveAsyncResult<Bitmap> result) {
		if(!result.hasError()) {
			_callback.call(result.getResult());
		} else {
			if(_onError != null) {
				_onError.call(result.getError());
			} else {
				throw new RuntimeException(result.getError());
			}
		}
	}
	
	public void execute(Uri param) {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1) {
			super.execute(param);
		} else {
			super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, param);
		}
	}
}