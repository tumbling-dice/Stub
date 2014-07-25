public final class TestPlugin extends Plugin {
	
	@Override
	public List<TwitterData> getResource(String screenName, long userId) {
		return null;
	}
	
	@Override
	public ArrayList<String> getClickMenues(int resourcePosition, TwitterData data) {
		return null;
	}
	
	@Override
	public boolean onMenuItemClick(int menuItemPosition, TwitterData data) {
		return false;
	}
	
	@Override
	public List<TwitterData> onReload(String screenName, long userId) {
		return null;
	}
	
	@Override
	public List<TwitterData> onMore(String screenName, long userId) {
		return null;
	}
	
	@Override
	public void onPost(String screenName, long userId) {
		
	}
}