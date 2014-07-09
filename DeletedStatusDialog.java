public class DeletedStatusDialog extends DialogFragment {
	
	private static final String KEY_TIMELINE_DATA = "timelineData";
	
	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle args = getArguments();
		
		if(args == null || !args.containsKey(KEY_TIMELINE_DATA)) throw new IllegalArgumentException("データがありません。");
		
		final TimelineData data = (TimelineData) args.getSerializable(KEY_TIMELINE_DATA);
		View v = inflater.inflate(R.layout.adapter_timeline);
		
		//TODO: TimelineAdapterのgetViewと同期する
		
		return v;
	}
	
	// TODO:onActivityCreated
	
}