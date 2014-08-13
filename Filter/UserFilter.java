public class UserFilter extends ListFilter<ProfileData> {
	
	public TimelineFilter(List<ProfileData> datas, OnPublishResultListener listener) {
		super(datas, listener);
	}
	
	@Override
	public boolean test(ProfileData data, CharSequence constraint) {
		
		if(data.getScreenName().equals(constraint)) return true;
		if(data.getName().equals(constraint)) return true;
		if(data.getDescription().equals(constraint)) return true;
		
		return false;
	}
	
}