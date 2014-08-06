package inujini_.nuechin.test.util.image;

import inujini_.function.Function.Action1;
import inujini_.nuechin.test.util.image.ImageUtil.BitmapResult;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

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
		void call(Object key, Bitmap bitmap);
	}

	private ImageCallback _callback;
	private Action1<Exception> _onError;
	private Point _maxSize;
	private Object _key;
	private boolean _isSaveCache;
	private boolean _isThrowException;
	private boolean _isSaveFile;
	private int _timeout = 30000;

	/**
	 * HttpImageLoader
	 * @param key callbackで対象のViewを特定するためのKey。
	 * @param maxSize 取得するBitmapのサイズ。
	 * @param callback Bitmap取得後のコールバック。
	 */
	public HttpImageLoader(Object key, Point maxSize, ImageCallback callback) {
		_key = key;
		_maxSize = maxSize;
		_callback = callback;
		_isThrowException = true;
	}

	/**
	 * HttpImageLoader
	 * @param key callbackで対象のViewを特定するためのKey。
	 * @param maxSize 取得するBitmapのサイズ。
	 * @param callback Bitmap取得後のコールバック。
	 * @param onError Bitmap取得時に例外が発生した場合のコールバック。
	 */
	public HttpImageLoader(Object key, Point maxSize, ImageCallback callback, Action1<Exception> onError) {
		_key = key;
		_maxSize = maxSize;
		_callback = callback;
		_onError = onError;
	}

	/**
	 * キャッシュ保存フラグ
	 * @param isSaveCache trueの場合は取得したBitmapをキャッシュに保存する。デフォルトではfalse。
	 */
	public void setSaveCache(boolean isSaveCache) {
		_isSaveCache = isSaveCache;
	}
	
	/**
	 * キャッシュをローカルファイルとしても保存する
	 * @param isSaveFile <p>trueの場合は取得したBitmapをキャッシュに保存する。デフォルトではfalse。</p>
	 * <p>ただし、{@link setSaveCache(boolean) setSaveCache}でtrueがセットされていない場合はこの値は用いられない。</p>
	 */
	public void setSaveFile(boolean isSaveFile) {
		_isSaveFile = isSaveFile;
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
		return ImageUtil.getBitmapFromHttp(param[0], _maxSize, _timeout, _isSaveCache, _isSaveFile);
	}

	@Override
	protected void onPostExecute(BitmapResult result) {
		if(!result.hasError()) {
			_callback.call(_key, result.getBitmap());
		} else {
			if(_onError != null) {
				_onError.call(result.getError());
			} else {
				if(_isThrowException) {
					destroy();
					throw new RuntimeException(result.getError());
				} else {
					result.getError().printStackTrace();
				}
			}
		}
		
		destroy();
	}
	
	public void destroy() {
		_callback = null;
		_onError = null;
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