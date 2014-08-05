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
	
	/** 読み込むURL */
	private String _url;
	
	private boolean _isForceLoad;
	private boolean _isSaveCache;
	private boolean _isSaveFile;
	
	/* Properties */
	
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
	
	/**
	 * URLの取得
	 * @return {@link setImageUrl(String, Object, boolean, boolean, boolean, ImageCallback) setImageUrl}で指定したURL
	 */
	public String getUrl() {
		return _url;
	}
	
	public void setForceLoad(boolean isForceLoad) {
		_isForceLoad = isForceLoad;
	}
	
	public void setSaveCache(boolean isSaveCache) {
		_isSaveCache = isSaveCache;
	}
	
	public void setSaveFile(boolean isSaveFile) {
		_isSaveFile = isSaveFile;
	}

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
		_isForceLoad = false;
		_isSaveCache = true;
		_isSaveFile = true;
	}

	/**
	 * UrlImageView
	 * @param context
	 * @param attrs
	 */
	public UrlImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		int[] attrsArray = new int[] {
			android.R.attr.layout_width,
			android.R.attr.layout_height,
			R.attr.force_load,
			R.attr.save_cache,
			R.attr.save_file,
		};
		
		TypedArray t = context.obtainStyledAttributes(attrs, attrsArray);
		
		_isForceLoad = t.getBoolean(2, false);
		_isSaveCache = t.getBoolean(3, true);
		_isSaveFile = t.getBoolean(4, true);
		
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
		
		int[] attrsArray = new int[] {
			android.R.attr.layout_width,
			android.R.attr.layout_height,
			R.attr.force_load,
			R.attr.save_cache,
			R.attr.save_file,
		};
		
		TypedArray t = context.obtainStyledAttributes(attrs, attrsArray, defStyle);

		_isForceLoad = t.getBoolean(2, false);
		_isSaveCache = t.getBoolean(3, true);
		_isSaveFile = t.getBoolean(4, true);
		
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
	 * @param callback 画像取得後のコールバック。このメソッドに渡したkeyとHttpImageLoaderで読み込んだBitmapが渡されてくる。
	 */
	public synchronized void setImageUrl(String url, Object key, ImageCallback callback) {
		if(key == null) throw new IllegalArgumentException(String.format("keyがnullです。 URL:%s", url));

		setTag(key);
		_url = url;

		// cache check
		if(!_forceLoad) {
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
		_loader.setSaveCache(_isSaveCache);
		_loader.setSaveFile(_isSaveFile);
		_loader.notThrowException();
		_loader.execute(Uri.parse(url));
	}

	

}