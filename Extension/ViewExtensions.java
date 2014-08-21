public final class ViewExtensions {
	
	public static <T extends View> T findView(View v, int resourceId) {
		return (T) v.findViewById(resourceId);
	}
	
	public static <T extends View> T findViewTag(View v, Object tag) {
		return (T) v.findViewWIthTag(tag);
	}
	
}