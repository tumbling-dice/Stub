package inujini_.nuechin.test.util.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

public final class ImageCache {

	/** LruCache */
	private static LruCache<String, Bitmap> _cache;
	/** キャッシュ用ディレクトリパス */
	private static String CACHE_DIR;

	/**
	 * LruCacheの初期化及びキャッシュ用ディレクトリの決定
	 * @param context MemoryClassの取得とキャッシュ用ディレクトリのパスを取得
	 */
	public static void init(Context context) {

		// LruCache初期化
		if(_cache == null) {
			int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
			int cacheSize = memClass * 1024 * 1024 / 8;

			_cache = new LruCache<String, Bitmap>(cacheSize) {
				@Override
				protected int sizeOf(String key, Bitmap value) {
					return value.getRowBytes();
				}
			};
		}

		CACHE_DIR = context.getCacheDir() + "/";
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
	 * @param isSaveFile
	 */
	public static void put(String key, Bitmap bitmap, boolean isSaveFile) {
		if(key == null || bitmap == null) return;
		_cache.put(key, bitmap);

		// キャッシュファイルに登録しておく（全上書き）
		if(isSaveFile) putCacheFile(key, bitmap);
	}

	/**
	 * キャッシュファイルからBitmapを生成
	 * @param key
	 * @return ファイルがあればそれをデコードしたもの。なければnull。
	 */
	private static Bitmap getFromCacheFile(String key) {
		File cacheFile = new File(CACHE_DIR + getPath(key));
		if(!cacheFile.exists()) return null;

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(cacheFile);
			return BitmapFactory.decodeStream(fis);
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if(fis != null) {
				try { fis.close(); } catch(IOException e) { e.printStackTrace(); }
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
			fos = new FileOutputStream(CACHE_DIR + getPath(key));
			bitmap.compress(CompressFormat.JPEG, 100, fos);
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try { fos.close(); } catch(IOException e) { e.printStackTrace(); }
		}
	}

	private static String getPath(String key) {
		if(!key.contains("/")) return key;

		String[] keys = key.split("/");
		int length = keys.length;

		switch (length) {
		case 1:
			return keys[0];
		default:
			return keys[length - 2] + "_" + keys[length - 1];
		}
	}

}