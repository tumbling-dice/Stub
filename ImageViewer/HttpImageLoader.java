public class HttpImageLoader extends AsyncTask<Uri, Void, BitmapResult> {
	
	/**
	 * {@link HttpImageLoader HttpImageLoader}#onPostExecuteで呼び出されるコールバック
	 * @see HttpImageLoader
	 */
	public interface ImageCallback {
		/**
		 * Bitmap読み込み後のコールバック
		 * @param key {@link HttpImageLoader HttpImageLoader}に渡したKey。
		 * @param bitmap {@link HttpImageLoader HttpImageLoader}で読み込まれたBitmap。nullの場合がある。
		 */
		void call(Object, Bitmap);
	}
	
	private WeakReference<ImageCallback> _callback;
	private WeakReference<Action1<Exception>> _onError;
	private Point _maxSize;
	private Object _key;
	private boolean _isSaveCache;
	private boolean _isThrowException;
	private int _timeout = 30000;
	
	/**
	 * ImageLoader
	 * @param key callbackで対象のViewを特定するためのKey。
	 * @param maxSize 取得するBitmapのサイズ。
	 * @param callback Bitmap取得後のコールバック。
	 */
	public ImageLoader(Object key, Point maxSize, ImageCallback callback) {
		_key = key;
		_maxSize = maxSize;
		_callback = Util.toWeak(callback);
		_isThrowException = true;
	}
	
	/**
	 * ImageLoader
	 * @param key callbackで対象のViewを特定するためのKey。
	 * @param maxSize 取得するBitmapのサイズ。
	 * @param callback Bitmap取得後のコールバック。
	 * @param onError Bitmap取得時に例外が発生した場合のコールバック。
	 */
	public ImageLoader(Object key, Point maxSize, ImageCallback callback, Action1<Exception> onError) {
		_key = key;
		_maxSize = maxSize;
		_callback = Util.toWeak(callback);
		_onError = Util.toWeak(onError);
	}
	
	/**
	 * キャッシュ保存フラグ
	 * @param trueの場合は取得したBitmapをキャッシュに保存する。デフォルトではfalse。
	 */
	public void setSaveCache(boolean isSaveCache) {
		_isSaveCache = isSaveCache;
	}
	
	/**
	 * <p>例外無視フラグ</p>
	 * <p>例外が発生しても握りつぶす。ただし、コンストラクタでonErrorが設定されている場合はこのフラグを使用しない。</p>
	 */
	public void notThrowException() {
		_isThrowException = false;
	}
	
	/**
	 * タイムアウト設定
	 * @param timeout 読み込みタイムアウト値（ミリ秒）。デフォルトでは30秒。
	 */
	public void setTimeout(int timeout) {
		_timeout = timeout;
	}
	
	@Override
	protected BitmapResult doInBackground(Uri... param) {
		return BitmapUtil.getBitmapFromHttp(param[0], _maxSize, _timeout, _isSaveCache);
	}
	
	@Override
	protected void onPostExecute(BitmapResult result) {
		if(!result.hasError()) {
			_callback.get.call(_key, result.getBitmap());
		} else {
			if(_onError != null) {
				_onError.get().call(result.getError());
			} else {
				if(_isThrowException) {
					throw new RuntimeException(result.getError());
				} else {
					result.getError().printStacktrace();
				}
			}
		}
	}
	
	@SuppressLint("NewApi")
	public void execute(Uri param) {
		if (Build.VERSION.SDK_INT <= 12) {
			super.execute(param);
		} else {
			super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, param);
		}
	}
}