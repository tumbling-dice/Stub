/**
 * <p>Bitmap及び画像ファイルに関するUtilityクラス。</p>
 * <p>基本的にはstaticメソッドで全ての処理が可能だが、
 * URIのスキーマやMIME-TYPEが不明な場合はインスタンスを作成することで安全に操作できる。</p>
 */
public final class BitmapUtil {
	
	/** 非同期でBitmapを取得する場合のOption */
	public static final class BitmapResult {
		private WeakReference<Bitmap> bitmap;
		private Exception error;
		
		/**
		 * Bitmap取得
		 * @return bitmap
		 * @throws IllegalStateException {@link #hasError() hasError}でtrueが返された場合に発生。
		 */
		public Bitmap getBitmap() {
			if(hasError()) throw new IllegalStateException("Bitmap取得時に例外が発生しています。", error);
			return this.bitmap.get();
		}
		
		/**
		 * Bitmapセット
		 * @param bitmap
		 */
		public void setBitmap(Bitmap bitmap) {
			if(bitmap != null) bitmap.clear();
			bitmap = Util.toWeak(bitmap);
		}
		
		/**
		 * 例外取得
		 * @return exception
		 */
		public Exception getError() {
			return this.error;
		}
		
		/**
		 * 例外セット
		 * @param exception
		 */
		public void setError(Exception e) {
			this.error = e;
		}
		
		/**
		 * 例外発生チェック
		 * @return Bitmap取得時に例外が発生していたらtrue
		 */
		public boolean hasError() {
			return this.error != null;
		}
	}
	
	private Uri _uri;
	private File _file;
	private Context _context;
	private Point _maxSize = new Point();
	
	/**
	 * BitmapUtil
	 * @param uri
	 * @param context
	 * @throws IllegalArgumentException 以下の場合に発生。
	 * <ul>
	 * <li>uriがnull</li>
	 * <li>解決できないScheme（file、content、http、https、ftp以外）</li>
	 * <li>MIME-TYPEがImage以外</li>
	 * </ul>
	 */
	public BitmapUtil(String uri, Context context) {
		if(uri == null) throw new IllegalArgumentException("uriがnullです。");
		
		_uri = Uri.parse(uri);
		_file = getImageFileFromUri(_uri, context);
		
		if(_file == null && !isNeedHttp(_uri)) {
			throw new IllegalArgumentException(String.format("不明なUriです。:%s", uri));
		}
		
		_context = context;
		getMaxDisplaySize(context);
	}
	
	/**
	 * BitmapUtil
	 * @param uri
	 * @param context
	 * @throws IllegalArgumentException 以下の場合に発生。
	 * <ul>
	 * <li>uriがnull</li>
	 * <li>解決できないScheme（file、content、http、https、ftp以外）</li>
	 * <li>MIME-TYPEがImage以外</li>
	 * </ul>
	 */
	public BitmapUtil(Uri uri, Context context) {
		if(uri == null) throw new IllegalArgumentException("uriがnullです。");
		
		_uri = uri;
		_file = getImageFileFromUri(_uri, context);
		
		if(_file == null && !isNeedHttp(_uri)) {
			throw new IllegalArgumentException(String.format("不明なUriです。:%s", uri.toString()));
		}
		
		_context = context;
		getMaxDisplaySize(context);
	}
	
	/**
	 * BitmapUtil
	 * @param file
	 * @param context
	 * @throws IllegalArgumentException 以下の場合に発生。
	 * <ul>
	 * <li>fileがnull</li>
	 * <li>解決できないScheme（file、content、http、https、ftp以外）</li>
	 * <li>MIME-TYPEがImage以外</li>
	 * </ul>
	 */
	public BitmapUtil(File file, Context context) {
		if(file == null) throw new IllegalArgumentException("fileがnullです。");
		
		_uri = Uri.fromFile(file);
		_file = getImageFileFromUri(_uri);
		
		if(_file == null && !isNeedHttp(_uri)) {
			throw new IllegalArgumentException(String.format("不明なUriです。:%s", _uri.toString()));
		}
		
		_context = context;
		getMaxDisplaySize(context);
	}
	
	/**
	 * URI取得
	 * @return uri
	 */
	public Uri getUri() {
		return _uri;
	}
	
	/**
	 * File取得
	 * @return file URIのschemeがfile or contentでない場合、
	 * {@link #saveOriginalFile(String, Bitmap.CompressFormat, Context) saveOriginalFile}が呼び出されていない場合はnullとなる。
	 */
	public File getFile() {
		return _file;
	}
	
	/**
	 * 最大サイズ設定
	 * @param maxSize デフォルトでは端末のサイズ。
	 */
	public void setMaxSize(Point maxSize) {
		_maxSize = maxSize;
	}
	
	/* Public Instance Methods */
	
	/**
	 * <p>Bitmap取得</p>
	 * <p>コンストラクタで渡したパラメータによって取得する方法が変わる。</p>
	 * @param timeout HTTP接続時のタイムアウト値（ミリ秒）。
	 * @param isSaveCache HTTP接続が発生する場合にキャッシュを保存するフラグ。
	 * @retrun BitmapResult 
	 */
	public BitmapResult getBitmap(int timeout, boolean isSaveCache) {
		if(_file != null) {
			return getBitmapFromFile(_file, _maxSize, _context);
		} else {
			return getBitmapFromHttp(_uri, _maxSize, _timeout, isSaveCache);
		}
	}
	
	/**
	 * <p>Bitmap取得（非同期）</p>
	 * <p>コンストラクタで渡したパラメータによって取得する方法が変わる。</p>
	 * @param key callbackに渡すkey。
	 * @param isSaveCache HTTP接続が発生する場合にキャッシュを保存するフラグ。
	 * @param callback Bitmap取得後のコールバック。
	 */
	public void getBitmapAsync(Object key, boolean isSaveCache, ImageCallback callback) {
		return getBitmapAsync(key, isSaveCache, callback, null);
	}
	
	/**
	 * <p>Bitmap取得（非同期）</p>
	 * <p>コンストラクタで渡したパラメータによって取得する方法が変わる。</p>
	 * @param key callbackに渡すkey。
	 * @param isSaveCache HTTP接続が発生する場合にキャッシュを保存するフラグ。
	 * @param callback Bitmap取得後のコールバック。
	 * @param onError Bitmap取得時に例外が発生した場合のコールバック。
	 */
	public void getBitmapAsync(Object key, boolean isSaveCache, ImageCallback callback, Action1<Exception> onError) {
		
		if(_file != null) {
			BitmapResult r = getBitmapFromFile(_file, _maxSize, _context);
			if(!r.hasError()) {
				callback.call(key, r.getBitmap());
			} else {
				onError.call(r.getError());
			}
			return;
		}
		
		HttpImageLoader loader = new HttpImageLoader(key, _maxSize, callback, onError);
		loader.setSaveCache(isSaveCache);
		loader.execute(uri)
		
	}
	
	/**
	 * ファイルの保存
	 * @param fileName ファイル名
	 * @param format Bitmapの圧縮方法
	 * @paran context
	 * @retrun ファイルの保存に成功したらtrue
	 * @throws IllegalStateException コンストラクタのURIがファイルの場合に発生
	 * @see #isNeedHttp() isNeedHttp
	 */
	public boolean saveOriginalFile(String fileName, final Bitmap.CompressFormat format, Context context) {
		
		if(!isNeedHttp(_uri)) throw new IllegalStateException("このURIは既にファイルとして保存されています。");
		
		_file = outputFile(fileName, context, new Func1<FileOutputStream, Boolean>() {
			@Override
			public Boolean call(FileOutputStream fos) {
				return openHttpStream(_uri, 30000, new Action1<InputStream>() {
					@Override
					public void call(InputStream in) {
						BitmapFactory.decodeStream(in).compress(format, 100, fos);
					}
				}, null);
			}
		});
			
		return _file != null;
	}
	
	/**
	 * URIがHTTP通信の必要なものなのかをチェックする。
	 * @return URIのSchemeがhttp、https、ftpならtrue
	 */
	public boolean isNeedHttp() {
		return isNeedHttp(_uri);
	}
	
	/* Private Instance Methods */
	
	private static boolean isNeedHttp(Uri uri) {
		checkMimeType(uri.getPath());
		
		String scheme = uri.getScheme();
		return "http".equals(scheme) || "https".equals(scheme) || "ftp".equals(scheme);
	}
	
	
	private void getMaxDisplaySize(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		if(Build.VERSION.SDK_INT < 13) {
			_maxSize.x = display.getWidth();
			_maxSize.y = display.getHeight();
		} else {
			@SuppressLint("NewApi")
			display.getSize(_maxSize);
		}
	}
	
	/* Public Static Methods */
	
	/**
	 * URIからファイルを特定する
	 * @param uri
	 * @return URIから特定したFile
	 * @see isNeedHttp() isNeedHttp
	 * @throws IllegalArgumentException MIME-TYPEがimageではない、
	 * もしくはURIのスキーマがファイル形式でない場合に発生。
	 */
	public static File getImageFileFromUri(Uri uri) {
		String scheme = uri.getScheme();
		
		if("content".equals(scheme)) {
			// schemeがcontentの場合はContentResolverから本当のファイルパスを取得する。
			ContentResolver contentResolver = context.getContentResolver();
			String mimeType = contentResolver.getType(uri);
			if(mimeType == null || !mimeType.startsWith("image/")) {
				throw new IllegalArgumentException("このURIのMIME-TYPEは画像ではありません");
			}
			
			File file = null;
			Cursor c = null;
			
			try {
				c = contentResolver.query(uri, new String[] { MediaColumns.DATA }, null, null, null);
				if(c != null) {
					c.moveToFirst();
					file = new File(c.getString(0));
				}
			} finally {
				if(c != null) c.close();
			}
			
			if(file == null) throw new IllegalArgumentException("ContentResolverからFileを取得できませんでした。");
			
			return file;
			
		} else if("file".equals(scheme)) {
			String path = uri.getPath();
			
			checkMimeType(path);
			
			return new File(path);
		} else {
			throw new IllegalArgumentException(String.format("Schemeがファイルではありません。:%s", scheme));
		}
	}
	
	public static File saveBitmap(Bitmap bitmap, Context context) {
		return saveBitmap(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg", bitmap, CompressFormat.JPEG, context);
	}
	
	public static File saveBitmap(String fileName, final Bitmap bitmap, final Bitmap.CompressFormat format, Context context) {
		return outputFile(fileName, context, new Func1<FileOutputStream, Boolean>(){
			@Override
			public Boolean call(FileOutputStream fos) {
				return bitmap.compress(format, 100, fos);
			}
		});
	}
	
	public static BitmapResult getBitmapFromFile(final File file, Point maxSize, final Context context) {
		return getBitmapFromStream(maxSize, new Func<InputStream>() {
			@Override
			public InputStream call() {
				try {
					return context.getContentResolver().openInputStream(Uri.fromFile(file));
				} catch(FileNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
	
	public static BitmapResult getBitmapFromStream(Point maxSize, Func<InputStream> streamProvider) {
			
		InputStream in = null;
		BitmapResult r = new BitmapResult();
		
		try {
			in = streamProvider.call();
			r.setBitmap(decode(in));
		} catch(Exception e) {
			e.printStacktrace();
			r.setError(e);
		} finally {
			if(in != null) {
				try { in.close(); } catch(IOException e) { e.printStacktrace(); }
			}
		}
		
		return r;
	}
	
	public static BitmapResult getBitmapFromHttp(final Uri uri, final Point maxSize, int timeout, final boolean isSaveCache) {
		
		final BitmapResult r = new BitmapResult();
		
		openHttpStream(uri, timeout, new Action1<InputStream>(){
			@Override
			public void call(InputStream in) {
				Bitmap bitmap = decode(in, maxSize);
				
				if(bitmap != null && isSaveCache) {
					ImageCache.put(uri.toString(), bitmap);
				}
				
				r.setResult(bitmap);
			}
		}, new Action1<Exception>() {
			@Override
			public void call(Exception e) {
				r.setError(e);
			}
		});
		
		return r;
	}
	
	/* Utility Methods */
	
	public static Bitmap resourceToBitmap(Context context, int resourceId) {
		return BitmapFactory.decodeResource(context.getResources(), resourceId);
	}
	
	public static Drawable getDrawableFromResource(Context context, int resourceId) {
		return context.getResources().getDrawable(resourceId);
	}
	
	public static Bitmap drawableToBitmap(Drawable drawable) {
		return ((BitmapDrawable) drawable).getBitmap();
	}
	
	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		return new BitmapDrawable(bitmap);
	}
	
	public static int convertDipToPx(float dip, Context context) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (dip * density + 0.5f);
	}
	
	/* Private Static Methods */
	
	private static String checkMimeType(String path) {
		String extention = null;
		int i = path.lastIndexOf(".");
		extension = i > 0 ? filePath.substring(i + 1) : null;
		
		if(extention == null) throw new IllegalArgumentException("拡張子が不明です。");
		
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		
		if(mimeType == null || !mimeType.startsWith("image/")) throw new IllegalArgumentException("このURIのMIME-TYPEは画像ではありません");
		
		return mimeType;
	}
	
	
	private static Bitmap decode(InputStream in, Point maxSize) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		if(Build.VERSION.SDK_INT >= 11) @SuppressLint("NewApi") opt.inMutable = true;
		opt.inJustDecodeBounds = true;
		
		BitmapFactory.decodeStream(in, null, opt);
		
		float scaleW = (float) opt.outWidth / maxSize.x + 1;
		float scaleH = (float) opt.outHeight / maxSize.y + 1;
		
		if(scaleW > 2 && scaleW > 2) { 
			for (int i = 2, scale = (int) Math.floor((scaleW > scaleH ? scaleH : scaleW)); i <= scale; i *= 2) {
				opt.inSampleSize = i;
			}
		}
		
		opt.inJustDecodeBounds = false;
		
		if(Build.VERSION.SDK_INT >= 11) {
			return BitmapFactory.decodeStream(in, null, opt);
		} else {
			Bitmap tmp = BitmapFactory.decodeStream(in, null, opt);
			Bitmap bitmap = tmp.copy(Config.ARGB_8888, true);
			tmp.recycle();
			tmp = null;
			
			return bitmap;
		}
		
	}
	
	private static File outputFile(String fileName, Context context, Func1<FileOutputStream, Boolean> save) {
		String mimeType = checkMimeType(fileName);
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + fileName;
		
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(path);
			
			if(!save.call(fos)) {
				fos.flush();
			} else {
				return null;
			}
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch(IOException e) {
					e.printStacktrace();
					return null;
				}
			} else {
				return null;
			}
		}
		
		File f = new File(path);
		
		ContentValues cv = new ContentValues();
		ContentResolver contentResolver = context.getContentResolver();
		cv.put(Images.Media.MIME_TYPE, mimeType);
		cv.put(Images.Media.TITLE, fileName);
		cv.put(Images.Media.DISPLAY_NAME, fileName);
		cv.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
		cv.put(Images.Media.DATA, path);
		cv.put(Images.Media.SIZE, f.length());
		contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
		
		return f;
	}
	
	private static boolean openHttpStream(Uri uri, int timeout, Action1<InputStream> input, Action1<Exception> onError) {
		HttpURLConnection con = null;
		InputStream in = null;
		
		try {
			URL url = new URL(uri.toString());
			con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setConnectTimeout(3000);
			con.setReadTimeout(timeout);
			con.setUseCaches(true);
			
			con.connect();
			in = con.getInputStream();
			
			input.call(in);
			
			return true;
			
		} catch(Exception e) {
			e.printStacktrace();
			if(onError != null) onError.call(e);
			
			return false;
			
		} finally {
			if(in != null) {
				try { in.close(); } catch(IOException e) { e.printStacktrace(); }
			}
			
			if(con != null) {
				con.disconnect()
			}
		}
	}
	
}