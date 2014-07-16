public final class OrientationUtil {
	
	public static final int O_NOTHING = 0;
	public static final int O_PORTRAIT = 1;
	public static final int O_LANDSCAPE = 2;
	public static final int O_REVERSE_PORTRAIT = 3;
	public static final int O_REVERSE_LANDSCAPE = 4;
	
	public static final int getCurrentOrientationLock(Context context) {
		return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("orientationLockValues", "0"));
	}
	
	public static final void setLockOrientation(Activity activity, int orientation) {
		switch(orientation) {
		case O_NOTHING:
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			break;
		case O_PORTRAIT:
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case O_LANDSCAPE:
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		case O_REVERSE_PORTRAIT:
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
			break;
		case O_REVERSE_LANDSCAPE:
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
			break;
		default:
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
	}
	
	public static final int getCurrentOrientation(Context context) {
		switch(((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRotation()) {
		case Surface.ROTATION_90:
			return O_LANDSCAPE;
		case Surface.ROTATION_270:
			return O_REVERSE_LANDSCAPE;
		case Surface.ROTATION_180:
			return O_REVERSE_PORTRAIT;
		default:
			return O_PORTRAIT;
		}
	}
	
}