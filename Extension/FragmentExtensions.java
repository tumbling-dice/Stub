public final class FragmentExtensions {
	
	public static Context getApplicationContext(Fragment f) {
		if(f.getActivity() == null) throw new IllegalStatementException("Activity has not created yet.");
		return f.getActivity().getApplicationContext();
	}
	
	public static <T extends View> T findViewById(Fragment f, int resourceId) {
		if(f.getView() == null) throw new IllegalStatementException("Fragment's View has not created yet.");
		return (T) v.findViewById(resourceId);
	}
	
}