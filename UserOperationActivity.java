public class UserOperationActivity extends ActionBarActivity implements OnItemClickListener {
	
	private static final int T_FOLLOW = 0;
	private static final int T_FOLLOWER = 1;
	private static final int T_BLOCKED = 2;
	private static final int T_MUTED = 3;
	private static final String K_FOLLOW = "follow";
	private static final String K_FOLLOWER = "follower";
	private static final String K_BLOCKED = "blocked";
	private static final String K_MUTED = "muted";
	private static final String K_CURRENT_TYPE = "currentType";
	
	private SparseArray<ArrayList<ProfileData>> _cachedData = new SparseArray<ArrayList<ProfileData>>();
	private int _currentType;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_operation);
		
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		for(int i = 0, size = _cachedData.size(); i < size; i++) {
			int key = _cachedData.keyAt(i);
			String bundleKey = null;
			switch(key) {
			case T_FOLLOW:
				bundleKey = K_FOLLOW;
				break;
			case T_FOLLOWER:
				bundleKey = K_FOLLOWER;
				break;
			case T_BLOCKED:
				bundleKey = K_BLOCKED;
				break;
			case T_MUTED:
				bundleKey = K_MUTED;
				break;
			}
			
			outState.putSerializable(bundleKey, _cachedData.get(key));
		}
		
		outState.putInt(K_CURRENT_TYPE, _currentType);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		if(savedInstanceState != null) {
			if(_cachedData == null) _cachedData = new SparseArray<ArrayList<ProfileData>>();
			
			if(savedInstanceState.containsKey(K_FOLLOW)) 
				_cachedData.put(T_FOLLOW, (ArrayList<ProfileData>) savedInstanceState.getSerializable(K_FOLLOW));
			
			if(savedInstanceState.containsKey(K_FOLLOWER)) 
				_cachedData.put(T_FOLLOWER, (ArrayList<ProfileData>) savedInstanceState.getSerializable(K_FOLLOWER));
			
			if(savedInstanceState.containsKey(K_BLOCKED)) 
				_cachedData.put(T_BLOCKED, (ArrayList<ProfileData>) savedInstanceState.getSerializable(K_BLOCKED));
			
			if(savedInstanceState.containsKey(K_MUTED)) 
				_cachedData.put(T_MUTED, (ArrayList<ProfileData>) savedInstanceState.getSerializable(K_MUTED));
			
			_currentType = savedInstanceState.getInt(K_CURRENT_TYPE, T_FOLLOW);
			ArrayList<ProfileData> datas = _cachedData.get(_currentType);
			
			ListView lsvUsers = (ListView) findViewById(R.id.lsvUsers);
			lsvUsers.setOnItemClickListener(this);
			lsvUsers.setAdapter(new UsersAdapter(datas, getApplicationContext()));
		}
	}
	
}