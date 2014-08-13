public class TimelineFilter extends ListFilter<TimelineData> {
	
	public TimelineFilter(List<TimelineData> datas, OnPublishResultListener listener) {
		super(datas, listener);
	}
	
	@Override
	public boolean test(TimelineData data, CharSequence constraint) {
		
		if(data.getScreenName().equals(constraint)) return true;
		if(data.getName().equals(constraint)) return true;
		if(data.getText().equals(constraint)) return true;
		
		return false;
	}
	
}