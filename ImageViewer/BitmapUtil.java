public final class BitmapUtil {
	
	private Uri _uri;
	private File _file;
	private Context _context;
	private Point _maxSize = new Point();
	
	/* Constractor */
	
	public BitmapUtil(String uri, Context context) {
		if(uri == null) throw new IllegalArgumentException("uriがnullです。");
		
		_uri = Uri.parse(uri);
		_file = getImageFileFromUri(_uri, context);
		
		if(_file == null && !isNeedHttp(_uri)) {
			throw new IllegalArgumentException(String.format("不明なUriです。:%s", uri));
		}
		
		_context = context;
		getMaxSize(context);
	}
	
	public BitmapUtil(Uri uri, Context context) {
		if(uri == null) throw new IllegalArgumentException("uriがnullです。");
		
		_uri = uri;
		_file = getImageFileFromUri(_uri, context);
		
		if(_file == null && !isNeedHttp(_uri)) {
			throw new IllegalArgumentException(String.format("不明なUriです。:%s", uri.toString()));
		}
		
		_context = context;
		getMaxSize(context);
	}
	
	public BitmapUtil(File file, Context context) {
		if(file == null) throw new IllegalArgumentException("fileがnullです。");
		
		_uri = Uri.fromFile(file);
		_file = getImageFileFromUri(_uri);
		
		if(_file == null && !isNeedHttp(_uri)) {
			throw new IllegalArgumentException(String.format("不明なUriです。:%s", _uri.toString()));
		}
		
		_context = context;
		getMaxSize(context);
	}
	
	/* Property */
	
	public Uri getUri() {
		return _uri;
	}
	
	public File getFile() {
		return _file;
	}
	
	public void setMaxSize(Point maxSize) {
		_maxSize = maxSize;
	}
	
	/* Instance Methods */
	
	public Bitmap getBitmap() {
		if(_file != null) {
			getBitmapFromFile(_file, _maxSize, _context)
		} else {
			ImageLoader.getBitmapFromUri(_uri, _maxSize).getResult();
		}
	}
	
	public void getBitmapAsync(Action1<Bitmap> callback) {
		return getBitmapAsync(callback, null);
	}
	
	public void getBitmapAsync(final Action1<Bitmap> callback, Action1<Exception> onError) {
		
		if(_file != null) {
			callback.call(getBitmapFromFile(_file, _maxSize, _context));
			return;
		}
		
		new ImageLoader(_maxSize, callback, onError).execute(uri);
	}
	
	public void saveOriginalFile(String fileName) {
		
	}
	
	public void saveOriginalFileAsync(String fileName, Action callback) {
		
	}
	
	private boolean isNeedHttp(Uri uri) {
		checkMimeType(uri.getPath());
		
		String scheme = uri.getScheme();
		return "http".equals(scheme) || "https".equals(scheme) || "ftp".equals(scheme);
	}
	
	private void getMaxSize(Context context) {
		Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		if(Build.VERSION.SDK_INT < 13) {
			_maxSize.width = display.getWidth();
			_maxSize.height = display.getHeight();
		} else {
			display.getSize(_maxSize);
		}
	}
	
	/* Public Static Methods */
	
	public static File getImageFileFromUri(Uri uri) {
		String scheme = uri.getScheme();
		
		if("content".equals(scheme)) {
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
		}
	}
	
	public static File saveBitmap(Bitmap bitmap, Context context) {
		return saveBitmap(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg", bitmap, CompressFormat.JPEG, context);
	}
	
	public static File saveBitmap(String fileName, Bitmap bitmap, Bitmap.CompressFormat format, Context context) {
		String mimeType = checkMimeType(fileName);
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + fileName;
		
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(path);
			if(bitmap.compress(format, 100, fos)) {
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
		
		ContentValues cv = new ContentValues();
		ContentResolver contentResolver = context.getContentResolver();
		cv.put(Images.Media.MIME_TYPE, mimeType);
		cv.put(Images.Media.TITLE, fileName); 
		cv.put("_data", path);
		contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
		
		return new File(path);
	}
	
	public static ReactiveAsyncResult<Bitmap> getBitmapFromFile(final File file, Point maxSize, final Context context) {
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
	
	public static void getBitmapFromFileAsync(final File file, Point maxSize, final Context context, Action<Bitmap> callback, Action<Exception> onError) {
		
	}
	
	public static ReactiveAsyncResult<Bitmap> getBitmapFromStream(Point maxSize, Func<InputStream> streamProvider) {
			
		InputStream in = null;
		ReactiveAsyncResult<Bitmap> r = new ReactiveAsyncResult<Bitmap>();
		
		BitmapFactory.Options opt = new BitmapFactory.Options();
		
		try {
			in = streamProvider.call();
			if(Build.VERSION.SDK_INT >= 11) opt.inMutable = true;
			opt.inJustDecodeBounds = true;
			
			BitmapFactory.decodeStream(in, opt);
			
			float scaleW = (float) opt.outWidth / maxSize.width;
			float scaleH = (float) opt.outHeight / maxSize.height + 1;
			
			if(scaleW > 2 && scaleW > 2) { 
				for (int i = 2, scale = (int) Math.floor((scaleW > scaleH ? scaleH : scaleW)); i <= scale; i *= 2) {
					opt.inSampleSize = i;
				}
			}
			
			opt.inJustDecodeBounds = false;
			
			if(Build.VERSION.SDK_INT >= 11) {
				r.setResult(BitmapFactory.decodeStream(in, opt));
			} else {
				Bitmap tmp = BitmapFactory.decodeStream(in, opt);
				r.setResult(tmp.copy(Config.ARGB_8888, true));
				tmp.recycle();
				tmp = null;
			}
		} catch(Exception e) {
			e.printStacktrace();
			r.setError(e);
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch(IOException e) {
					e.printStacktrace();
				}
			}
		}
		
		return r;
	}
	
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
	
	
}