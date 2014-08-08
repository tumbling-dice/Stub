public class UserOperationLoader extends ReactiveAsyncLoader<ArrayList<ProfileData>> {
	
	private static final int T_FOLLOW = 0;
	private static final int T_FOLLOWER = 1;
	private static final int T_BLOCKED = 2;
	private static final int T_MUTED = 3;
	
	private int _type;
	private boolean _isComplete;
	private ArrayDeque<Long> _userIds;
	private long _targetUserId;
	private Twitter _twitter;
	
	public UserOperationLoader(int type, long targetUserId, Context context) {
		super(context);
		_type = type;
		_targetUserId = targetUserId;
		_twitter = TwitterInstance.getInstance(context);
	}
	
	@Override
	public boolean isComplete() {
		return _isComplete;
	}
	
	@Override
	public ReactiveAsyncResult<ArrayList<ProfileData>> loadInBackground() {
		ReactiveAsyncResult<ArrayList<ProfileData>> r = new ReactiveAsyncResult<ArrayList<ProfileData>>();
		
		if(_userIds == null) {
			try {
				_userIds = getDataSource();
			} catch(RuntimeException e) {
				e.printStackTrace();
				r.setError((TwitterException) e.getCause());
				return r;
			}
		}
		
		long[] targets = new long[100];
		for(int i = 0; i < 100; i++) {
			if(!_userIds.isEmpty)
				targets[i] =  userIds.poll();
			else
				break;
		}
		
		try {
			r.setResult((ArrayList<ProfileData>) linq(_twitter.lookupUsers(targets))
				.select(new Func1<User, ProfileData>() {
					@Override
					public ProfileData call(User x) {
						return Profile.convert(x);
					}
				}).toList());
		} catch(TwitterException e) {
			e.printStackTrace();
			r.setError(e);
			for(long target : targets) {
				_userIds.push(target);
			}
		}
		
		if(_userIds.isEmpty) {
			_isComplete = true;
			_twitter = null;
		}
		
		return r;
	}
	
	private ArrayDeque<Long> getDataSource() {
		Linq<IDs> query = null;
			
		switch(_type) {
		case T_FOLLOW:
			query = linq(new Func1<Long, IDs>(){
				@Override
				public IDs call(Long x) {
					try {
						return _twitter.getFriendsIDs(_targetUserId, x);
					} catch(TwitterException e) {
						throw new RuntimeException(e);
					}
				}
			});
			break;
		case T_FOLLOWER:
			query = linq(new Func1<Long, IDs>(){
				@Override
				public IDs call(Long x) {
					try {
						return _twitter.getFollowersIDs(_targetUserId, x);
					} catch(TwitterException e) {
						throw new RuntimeException(e);
					}
				}
			});
			break;
		case T_BLOCKED:
			query = linq(new Func1<Long, IDs>(){
				@Override
				public IDs call(Long x) {
					try {
						return _twitter.getBlocksIDs(x);
					} catch(TwitterException e) {
						throw new RuntimeException(e);
					}
				}
			});
			break;
		case T_MUTED:
			query = linq(new Func1<Long, IDs>(){
				@Override
				public IDs call(Long x) {
					try {
						return _twitter.getMutesIDs(x);
					} catch(TwitterException e) {
						throw new RuntimeException(e);
					}
				}
			});
			break;
		}
		
		return new ArrayDeque(query.selectMany(new Func1<IDs, Long[]>() {
			@Override
			public Long[] call(IDs x) {
				return x.getIDs();
			}
		}).toList());
	}
	
}