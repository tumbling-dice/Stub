public final class TestPlugin extends Plugin {
	
	@Override
	public List<TwitterData> getResource(String screenName, long userId) {
		return null;
	}
	
	@Override
	public String getLayoutFileAuthorities() {
		return null;
	}
	
	@Override
	public ArrayList<String> getClickMenu() {
		return null;
	}
	
	@Override
	public boolean onClickItem(int position, TwitterData data) {
		return false;
	}
	
	@Override
	public List<TwitterData> onReload() {
		return null;
	}
	
	@Override
	public List<TwitterData> onMore() {
		return null;
	}
	
	@Override
	public boolean onPost() {
		return false;
	}
}