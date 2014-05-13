public class TwitterListLoader extends ReactiveAsyncLoader<List<TwitterList>> {
	
	public static final int TYPE_SUBSCRIBE = 0;
	public static final int TYPE_BELONG = 1;
	public static final int TYPE_OWN = 2;
	
	private Twitter _twitter;
	private String _screenName;
	private int _type;
	private long _cursor = -1;
	private boolean isComplete;
	
	public TwitterListLoader(String screenName, int type, Context context) {
		super(context);
		_twitter = TwitterInstance.getTwitter(context);
		_screenName = screenName;
		_type = type;
	}
	
	@Override
	public boolean isComplete(){
		return this.isComplete;
	}
	
	@Override
	public ReactiveAsyncResult<List<TwitterList>> loadInBackground() {
		ReactiveAsyncResult<List<TwitterList>> r = new ReactiveAsyncResult<List<TwitterList>>();
		
		if(isComplete) {
			r.setError(new IllegalStateException("これ以上データはありません。"));
			return r;
		}
		
		try {
			switch(_type) {
				case TYPE_SUBSCRIBE:
					r.setResult(getSubscribedList());
					break;
				case TYPE_BELONG:
					r.setResult(getBelongedList());
					break;
				case TYPE_OWN:
					r.setResult(getOwnedList());
					break;
				default:
					r.setError(new IllegalStateException(String.format("typeが不正です。\d"), _type));
			}
		} catch(TwitterException e) {
			r.setError(e);
		}
		
		return r;
	}
	
	private Func1<UserList, TwitterList> convert = new Func1<UserList, TwitterList> {
		@Override
		public TwitterList call(UserList list) {
			TwitterList twitterList = new TwitterList();
			twitterList.setId(list.getId());
			twitterList.setName(list.getName());
			User user = list.getUser();
			twitterList.setOwnerId(user.getId());
			twitterList.setOwnerScreenName(user.getScreenName());
			String iconUrl = user.getProfileImageURL();
			//TODO: set cache
			twitterList.setOwnerIconUrl(iconUrl);
			twitterList.setDescription(list.getDescription());
			twitterList.setProtected(!list.isPublic());
			twitterList.setMemberCount(list.getMemberCount());
			twitterList.subscriberCount(list.getSubscriberCount());
			
			return twitterList;
		}
	}
	
	private List<TwitterList> getSubscribedList() throws TwitterException {
		PagableResponseList<UserList> list = _twitter.getUserListSubscriptions(_screenName, _cursor);
		
		if(list.hasNextCursor()) {
			_cursor = list.getNextCursor();
		} else {
			isComplete = true;
		}
		
		return linq(list).select(convert).toList();
	}

	private List<TwitterList> getBelongedList() throws TwitterException {
		PagableResponseList<UserList> list = _twitter.getUserListMemberships(_screenName, _cursor);
		
		if(list.hasNextCursor()) {
			_cursor = list.getNextCursor();
		} else {
			isComplete = true;
		}
		
		return linq(list).select(convert).toList();
	}

	private List<TwitterList> getOwnedList() throws TwitterException {
		PagableResponseList<UserList> list = _twitter.getUserListsOwnerships(_screenName, 20, _cursor);
		
		if(list.hasNextCursor()) {
			_cursor = list.getNextCursor();
		} else {
			isComplete = true;
		}
		
		return linq(list).select(convert).toList();
	}
	
}