public abstract class Plugin extends BindingService {
	
	/* Request codes */
	private static final int REQ_GET_RESOURCE = 10000;
	private static final int REQ_GET_MENU = 10001;
	private static final int REQ_CLICK_EVENT = 10002;
	private static final int REQ_RELOAD = 10003;
	private static final int REQ_MORE = 10004;
	private static final int REQ_POST = 10005;
	
	/* From client arguments key */
	private static final String KEY_SCREEN_NAME = "screenName";
	private static final String KEY_USER_ID = "userId";
	private static final String KEY_POSITION = "position";
	
	/* To client arguments key */
	private static final String KEY_RESOURCE = "resource";
	private static final String KEY_GET_MENU = "getMenu";
	private static final String KEY_MENU_CLOSE = "isMenuClose";
	private static final String KEY_IMPL = "isImplement";
	private static final String KEY_REQ_NOT_DEFINED = "notDefined";
	
	protected Config _config;
	private List<TwitterData> _resource;
	private boolean _isImplement;
	
	@Override
	public void onCreate() {
		super.onCreate();
		_config = new Config(getApplicationContext());
	}
	
	@Override
	private final Bundle onProcess(Message msg) {
		Bundle args = msg.getData();
		
		if(args == null) throw new IllegalArgumentException("MessageにBundleが設定されていません。");
		
		switch(msg.what) {
		// get resources
		case REQ_GET_RESOURCE:
			return onGetResource(args);
			
		// get click menus
		case REQ_GET_MENU:
			return onGetClickMenues(args);
			
		// fire click event
		case REQ_CLICK_EVENT:
			return callOnMenuItemClick(args);
			
		// fire reload event
		case REQ_RELOAD:
			return callOnReload(args);
			
		// fire get more resources event
		case REQ_MORE:
			return callOnMore(args);
		
		// fire post event
		case REQ_POST:
			return callOnPost(args);
			
		// not defined request
		default:
			Log.w("Plugin", String.format("this request is not defined. sent request code is %d.", msg.what));
			Bundle ret = new Bundle();
			ret.putBoolean(KEY_REQ_NOT_DEFINED, true);
			return ret;
		}
	}
	
	@Override
	private final void callback(int what, Bundle args, Messenger replyTo) {
		args.putBoolean(KEY_IMPL, _isImplement);
		_isImplement = false;
		replyTo.setData(args);
		replyTo.send(Message.obtain(null, what));
	}
	
	public static ParcelTwitterData convertToParcel(TwitterData data) {
		ParcelTwitterData convertedData = new ParcelTwitterData();
		//TODO: implement
		
		return convertedData;
	}
	
	public static ArrayList<ParcelTwitterData> convertToParcelList(List<TwitterData> resource) {
		
		ArrayList<ParcelTwitterData> datas = new ArrayList<ParcelTwitterData>();
		
		for(TwitterData d : resource) {
			datas.add(convertToParcel(data));
		}
		
		return datas;
	}
	
	public final List<TwitterData> getResources() {
		return _resource != null ? new ArrayList<TwitterData>(_resource) : null;
	}
	
	private Bundle onGetResource(Bundle args) {
		_isImplement = true;
		Bundle ret = new Bundle();
		_resource = getResource(args.getString(KEY_SCREEN_NAME), args.getLong(KEY_USER_ID, 0));
		
		if(_resource == null) _resource = new ArrayList<TwitterData>();
		
		ret.putParcelableArrayList(KEY_RESOURCE, convertToParcelList(_resource));
		
		return ret;
	}
	
	private Bundle onGetClickMenues(Bundle args) {
		Bundle ret = new Bundle();
		
		_isImplement = _config.hasClickEvent();
		
		if(_isImplement && _resource != null && !_resource.isEmpty()) {
			int position = args.getInt(KEY_POSITION, -1);
			ret.putSerializable(KEY_GET_MENU, getClickMenu(position, _resource.get(position)));
		}
		
		return ret;
	}
	
	private Bundle callOnMenuItemClick(Bundle args) {
		Bundle ret = new Bundle();
		
		_isImplement = _config.hasClickEvent();
		
		if(_isImplement && _resource != null && !_resource.isEmpty()) {
			int position = args.getInt(KEY_POSITION, -1);
			ret.putBoolean(KEY_MENU_CLOSE, onMenuItemClick(position, _resource.get(position)));
		}
		
		return ret;
	}
	
	private Bundle callOnReload(Bundle args) {
		Bundle ret = new Bundle();
		
		_isImplement = _config.hasReloadEvent();
		
		if(_isImplement) {
			List<TwitterData> reloadResource = onReload(args.getString(KEY_SCREEN_NAME), args.getLong(KEY_USER_ID, 0));
			
			ret.putParcelableArrayList(KEY_RESOURCE, convertToParcelList(reloadResource));
			
			if(_resource == null) _resource = new ArrayList<TwitterData>();
			
			for(TwitterData newData : reloadResource) {
				_resource.add(newData, 0);
			}
			
		}
		
		return ret;
	}
	
	private Bundle callOnMore(Bundle args) {
		Bundle ret = new Bundle();
		
		_isImplement = _config.hasMoreEvent();
		
		if(_isImplement) {
			List<TwitterData> reloadResource = onMore(args.getString(KEY_SCREEN_NAME), args.getLong(KEY_USER_ID, 0));
			ret.putParcelableArrayList(KEY_RESOURCE, convertToParcelList(reloadResource));
			
			if(_resource == null) _resource = new ArrayList<TwitterData>();
			
			for(TwitterData newData : reloadResource) {
				_resource.add(newData);
			}
		}
		
		return ret;
	}
	
	private Bundle callOnPost(Bundle args) {
		Bundle ret = new Bundle();
		
		_isImplement = _config.hasPostEvent();
		
		if(_isImplement) {
			onPost(args.getString(KEY_SCREEN_NAME), args.getLong(KEY_USER_ID, 0));
		}
		
		return ret;
	}
	
	abstract List<TwitterData> getResource(String screenName, long userId);
	abstract ArrayList<String> getClickMenues(int resourcePosition, TwitterData data);
	abstract boolean onMenuItemClick(int menuItemPosition, TwitterData data);
	abstract List<TwitterData> onReload(String screenName, long userId);
	abstract List<TwitterData> onMore(String screenName, long userId);
	abstract void onPost(String screenName, long userId);
}