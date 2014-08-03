package inujini_.nuechin.test.util.view;

import inujini_.nuechin.test.util.LayoutUtil;
import inujini_.nuechin.test.util.image.HttpImageLoader;
import inujini_.nuechin.test.util.image.HttpImageLoader.ImageCallback;
import inujini_.nuechin.test.util.image.ImageCache;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class UrlImageView extends ImageView {

	/** width / height */
	private Point _maxSize;

	/** Urlから画像を読み込むローダ */
	private HttpImageLoader _loader;


	/**
	 * UrlImageView
	 * @param context
	 * @param width
	 * @param height
	 */
	public UrlImageView(Context context, int width, int height) {
		super(context);
		_maxSize = new Point();
		_maxSize.x = width;
		_maxSize.y = height;
	}

	/**
	 * UrlImageView
	 * @param context
	 * @param attrs
	 */
	public UrlImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		int[] attrsArray = new int[] { android.R.attr.layout_width, android.R.attr.layout_height };
		TypedArray t = context.obtainStyledAttributes(attrs, attrsArray);
		_maxSize = new Point();
		try {
			_maxSize.x = t.getDimensionPixelSize(0, ViewGroup.LayoutParams.WRAP_CONTENT);
			_maxSize.y = t.getDimensionPixelSize(1, ViewGroup.LayoutParams.WRAP_CONTENT);
		} catch(UnsupportedOperationException e) {
			_maxSize = LayoutUtil.getDisplaySize(getContext());
		}
		t.recycle();
	}

	/**
	 * UrlImageView
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public UrlImageView (Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		int[] attrsArray = new int[] { android.R.attr.layout_width, android.R.attr.layout_height };
		TypedArray t = context.obtainStyledAttributes(attrs, attrsArray);
		_maxSize = new Point();
		try {
			_maxSize.x = t.getDimensionPixelSize(0, ViewGroup.LayoutParams.WRAP_CONTENT);
			_maxSize.y = t.getDimensionPixelSize(1, ViewGroup.LayoutParams.WRAP_CONTENT);
		} catch(UnsupportedOperationException e) {
			_maxSize = LayoutUtil.getDisplaySize(getContext());
		}
		t.recycle();
	}

	/**
	 * <p>URLから画像を取得</p>
	 * <p>キャッシュにヒットしなかった場合、実際に画像をセットするのはcallbackの役割となる。</p>
	 * @param url
	 * @param key callbackで対象のViewを特定するためのKey。このメソッドを呼び出すと{@link View#setTag(Object) setTag}にてセットされる。
	 * @param forceLoad trueの場合はキャッシュチェックを行わない。
	 * @param isSaveCache trueの場合はキャッシュに保存する。
	 * @param callback 画像取得後のコールバック。このメソッドに渡したkeyとHttpImageLoaderで読み込んだBitmapが渡されてくる。
	 */
	public synchronized void setUrlImage(final String url, final Object key, final boolean forceLoad
			, final boolean isSaveCache, final ImageCallback callback) {
		if(key == null) throw new IllegalArgumentException(String.format("keyがnullです。 URL:%s", url));

		setTag(key);

		// cache check
		if(!forceLoad) {
			Bitmap bitmap = ImageCache.get(url);

			if(bitmap != null
				&& _maxSize.x < (bitmap.getWidth() * 2)
				&& _maxSize.y < (bitmap.getHeight() * 2) ) {
				setImageBitmap(bitmap);
				return;
			}
		}

		setImageBitmap(null);

		_loader = new HttpImageLoader(key, _maxSize, callback);
		_loader.setSaveCache(isSaveCache);
		_loader.notThrowException();
		_loader.execute(Uri.parse(url));
	}

	/**
	 * 指定された最大サイズの取得
	 * @return maxSize
	 */
	public Point getMaxSize() {
		return _maxSize;
	}

	/**
	 * 最大サイズの指定
	 * @param maxSize
	 */
	public void setMaxSize(Point maxSize) {
		_maxSize = maxSize;
	}

}