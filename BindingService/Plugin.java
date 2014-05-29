public abstract class Plugin extends BindingService {
	
	/* Request codes */
	public static final int REQ_GET_RESOURCE = 10000;
	public static final int REQ_CLICK_MENU = 10001;
	public static final int REQ_CLICK_EVENT = 10002;
	public static final int REQ_RELOAD = 10003;
	public static final int REQ_POST = 10004;
	public static final int REQ_MORE = 10005;
	
	/* From client arguments key */
	public static final String KEY_SCREEN_NAME = "screenName";
	public static final String KEY_USER_ID = "userId";
	public static final String KEY_POSITION = "position";
	
	/* To client arguments key */
	public static final String KEY_RESOURCE = "resource";
	public static final String KEY_CLICK_MENU = "clickMenu";
	public static final String KEY_MENU_CLOSE = "isMenuClose";
	public static final String KEY_POST_SUCCESS = "isPostSuccess";
	public static final String KEY_IMPL = "isImplement";
	public static final String KEY_REQ_NOT_DEFINED = "notDefined";
	public static final String KEY_LAYOUT_AUTHORITIES = "layoutAuthorities";
	
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
		
		Bundle ret = new Bundle();
		
		switch(msg.what) {
			// get resources
			case REQ_GET_RESOURCE:
				_isImplement = true;
				_resource = getResource(args.getString(KEY_SCREEN_NAME), args.getLong(KEY_USER_ID, 0));
				ret.putParcelableArrayList(KEY_RESOURCE, convertToParcelList(_resource));
				
				
				if(_config.isAdvancedLayout()){
					ret.putString(KEY_LAYOUT_AUTHORITIES, getLayoutFileAuthorities());
				}
				
				break;
			// get click menus
			case REQ_CLICK_MENU:
				_isImplement = _config.hasClickEvent();
			
				if(_isImplement) {
					ret.putSerializable(KEY_CLICK_MENU, getClickMenu());
				}
				break;
			// fire click event
			case REQ_CLICK_EVENT:
				_isImplement = _config.hasClickEvent();
			
				if(_isImplement && (_resource != null && !_resource.isEmpty())) {
					int position = args.getInt(KEY_POSITION, -1);
					ret.putBoolean(KEY_MENU_CLOSE, onClickItem(position, _resource.get(position)));
				}
				break;
			// fire reload event
			case REQ_RELOAD:
				_isImplement = _config.hasReloadEvent();
			
				if(_isImplement) {
					List<TwitterData> reloadResource = onReload();
					
					ret.putParcelableArrayList(KEY_RESOURCE, convertToParcelList(reloadResource));
				
					for(TwitterData newData : reloadResource) {
						_resource.add(newData, 0);
					}
					
				}
				break;
			// fire post event
			case REQ_POST:
				_isImplement = _config.hasPostEvent();
			
				if(_isImplement) {
					ret.putBoolean(KEY_POST_SUCCESS, onPost());
				}
				break;
			// fire get more resources event
			case REQ_MORE:
				_isImplement = _config.hasMoreEvent();
			
				if(_isImplement) {
					List<TwitterData> reloadResource = onMore();
					ret.putParcelableArrayList(KEY_RESOURCE, convertToParcelList(reloadResource));
					
					for(TwitterData newData : reloadResource) {
						_resource.add(newData);
					}
				}
				break;
			// not defined request
			default:
				Log.w("Plugin", String.format("this request is not defined. sent request code is %d.", msg.what));
				ret.putBoolean(KEY_REQ_NOT_DEFINED, true);
				break;
		}
		
		return ret;
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
	
	abstract List<TwitterData> getResource(String screenName, long userId);
	abstract String getLayoutFileAuthorities();
	abstract ArrayList<String> getClickMenu();
	abstract boolean onClickItem(int position, TwitterData data);
	abstract List<TwitterData> onReload();
	abstract List<TwitterData> onMore();
	abstract boolean onPost();
}