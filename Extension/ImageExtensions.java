public final class ImageExtensions {
	
	/**
	 * リソースをBitmapに変換します
	 * @param context
	 * @param resourceId
	 * @return resourceIdで指定されたリソースのBitmap
	 */
	public static Bitmap getBitmapResource(Context context, int resourceId) {
		return BitmapFactory.decodeResource(context.getResources(), resourceId);
	}

	/**
	 * リソースからDrawableを取得します
	 * @param context
	 * @param resourceId
	 * @return resourceIdで指定されたリソースのDrawable
	 */
	public static Drawable getDrawableFromResource(Context context, int resourceId) {
		return context.getResources().getDrawable(resourceId);
	}

	/**
	 * DrawableをBitmapに変換します
	 * @param drawable
	 * @return drawableをBitmapに変換したもの
	 */
	public static Bitmap toBitmap(Drawable drawable) {
		return ((BitmapDrawable) drawable).getBitmap();
	}

	/**
	 * BitmapをDrawableに変換します
	 * @param bitmap
	 * @param context
	 * @return BitmapをDrawableに変換したもの
	 */
	public static Drawable toDrawable(Bitmap bitmap, Context context) {
		return new BitmapDrawable(context.getResources(), bitmap);
	}

	/**
	 * 
	 * @return
	 */
	public static int convertDipToPx(Context context, float dip) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (dip * density + 0.5f);
	}

	/**
	 * 透過度の変更
	 * @param bmp bitmap
	 * @param alpha 0～255
	 * @return 
	 */
	public static Bitmap changeAlpha(Bitmap bmp, int alpha) {

		if(bmp == null) return null;

		val bitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		// Bitmapのピクセルデータを取得
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// 透過率をintとして取得
				int a = pixels[x + y * width];
				a = a >>> 24;

				// 透過率の変更
				if (a != 0) {
					a -= alpha; //加える透過率の値、0-255
					if (a < 0) {
						a = 0;
					}
				}
				a = a << 24;

				// ピクセルの色情報から透過率の削除
				int b = pixels[x + y * width];
				b = b << 8;
				b = b >>> 8;

				// 透過情報と色情報の合成
				pixels[x + y * width] = a ^ b;
			}
		}

		// 透過度が変わったBitmapデータの作成
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

		return bitmap;
	}
}