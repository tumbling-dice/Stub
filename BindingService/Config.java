public final class Config {
	
	private Resource _resource;
	
	public Config(Context context) {
		_resource = context.getResources();
	}
	
	public boolean hasClickEvent() {
		return _resource.getBoolean(R.bool.click);
	}
	
	public boolean hasReloadEvent() {
		return _resource.getBoolean(R.bool.reload);
	}
	
	public boolean hasPostEvent() {
		return _resource.getBoolean(R.bool.post);
	}
	
	public boolean hasMoreEvent() {
		return _resource.getBoolean(R.bool.more);
	}
	
}