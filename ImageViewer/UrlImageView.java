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
		_maxSize.x = t.getDimensionPixelSize(0, ViewGroup.LayoutParams.WRAP_CONTENT);
		_maxSize.y = t.getDimensionPixelSize(1, ViewGroup.LayoutParams.WRAP_CONTENT);
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
		_maxSize.x = t.getDimensionPixelSize(0, ViewGroup.LayoutParams.WRAP_CONTENT);
		_maxSize.y = t.getDimensionPixelSize(1, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	/**
	 * <p>URLから画像を取得</p>
	 * <p>このメソッドを呼び出しても{@link ImageView#setImageBitmap(Bitmap) setImageBitmap}を呼び出すことはない。
	 * 実際に画像をセットするのはcallbackの役割となる。</p>
	 * @param url
	 * @param key callbackで対象のViewを特定するためのKey。このメソッドを呼び出すと{@link View#setTag(Object) setTag}にてセットされる。
	 * @param forceLoad trueの場合はキャッシュチェックを行わない。
	 * @param isSaveCache trueの場合はキャッシュに保存する。
	 * @param callback 画像取得後のコールバック。このメソッドに渡したkeyとHttpImageLoaderで読み込んだBitmapが渡されてくる。
	 */
	public synchronized void setUrlImage(String url, Object key, boolean forceLoad, boolean isSaveCache, ImageCallback callback) {
		if(key == null) throw new IllegalArgumentException(String.format("keyがnullです。 URL:%s", url));
		
		// タグ（key）が一致している場合は何もしない
		Object tag = super.getTag();
		if(tag != null && tag.equals(key)) return;
		
		// HttpImageLoaderが実行中 or 実行前の場合はキャンセルする
		// （実際にはキャッシュまでを行わせる）
		if(_loader != null) {
			if(_loader.getStatus() != Status.FINISHED) _loader.cancel(false);
			_loader = null;
		}
		
		super.setBitmapImage(null);
		super.setTag(key);
		
		// cache check
		if(!forceLoad) {
			Bitmap bitmap = ImageCache.get(url);
			if(bitmap != null) {
				callback.call(key, bitmap);
				return;
			}
		}
		
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