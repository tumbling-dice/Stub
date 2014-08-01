public final class ImageCache {
	
	/** LruCache */
	private static LruCache<String, Bitmap> _cache;
	/** キャッシュ用ディレクトリパス */
	private static String CACHE_DIR;
	
	//TODO: Applicationで呼ぶ
	
	/**
	 * LruCacheの初期化及びキャッシュ用ディレクトリの決定
	 * @param context MemoryClassの取得とキャッシュ用ディレクトリのパスを取得
	 */
	public static void init(Context context) {
		
		// LruCache初期化
		int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		int cacheSize = memClass * 1024 * 1024 / 8;
		
		_cache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
			}
		};
		
		CACHE_DIR = context.getExternalCacheDir() + "\\";
	}
	
	/**
	 * 取得
	 * @param key
	 * @return Bitmap
	 */
	public static Bitmap get(String key) {
		if(key == null) return null;
		
		Bitmap b = _cache.get(key);
		
		// LruCacheになかったらキャッシュファイルをチェック
		if(b == null) {
			b = getFromCacheFile(key);
			// キャッシュファイルに存在した場合はLruCacheに登録する
			if(b != null) _cache.put(key, b);
		}
		
		return b;
	}
	
	/**
	 * 登録
	 * @param key
	 * @param bitmap
	 */
	public static void put(String key, Bitmap bitmap) {
		if(key == null || bitmap == null) return;
		_cache.put(key, bitmap);
		
		// キャッシュファイルに登録しておく（全上書き）
		putCacheFile(key, bitmap);
	}
	
	/**
	 * キャッシュファイルからBitmapを生成
	 * @param key
	 * @return ファイルがあればそれをデコードしたもの。なければnull。
	 */
	private static Bitmap getFromCacheFile(String key) {
		File cacheFile = new File(CACHE_DIR + key);
		if(!cacheFile.exists()) return null;
		
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(cacheFile);
			return BitmapFactory.decodeStream(fis);
		} catch(IOException e) {
			e.printStacktrace();
			return null;
		} finally {
			if(fis != null) {
				try { fis.close(); } catch(IOException e) { e.printStacktrace(); }
			}
		}
	}
	
	/**
	 * キャッシュファイル登録
	 * @param key
	 * @param bitmap
	 */
	private static void putCacheFile(String key, Bitmap bitmap) {
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(CACHE_DIR + key);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
		} catch(IOException e) {
			e.printStacktrace();
		} finally {
			try { fos.close(); } catch(IOException e) { e.printStacktrace(); }
		}
	}
	
}