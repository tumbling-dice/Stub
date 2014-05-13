public class TwitterListFragment extends ListFragment {
	
	public static final String SCREEN_NAME = "screenName";
	public static final String TYPE = "type";
	
	private ViewGroup _footer;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		Bundle args = getArguments();
		final int type = args.getInt(TYPE, -1);
		final String screenName = args.getString(SCREEN_NAME);
		ProgressDialog prog = new ProgressDialog(getActivity());
		prog.setMessage("読み込み中...");
		
		getLoaderManager().initLoader(type, null, new LoaderObserver<List<TwitterList>>(prog, new Action1<List<TwitterList>>(){
			@Override
			public void call(List<TwitterList>> data) {
				// TODO:set footer
				
				// on First
				setListAdapter(new TwitterListAdapter(data, getActivity().getApplicationContext()));
			}
		}) {
			@Override
			public ReactiveAsyncLoader<List<TwitterList>> onCreate(int id, Bundle args) {
				return new TwitterListLoader(screenName, id, getActivity().getApplicationContext());
			}
			
			@Override
			public void onNext(List<TwitterList> data) {
				TwitterListAdapter adapter = (TwitterListAdapter) getListAdapter();
				for(TwitterList d :data) {
					adapter.add(d);
				}
				
				adapter.notifyDataSetChanged();
			}
			
			@Override
			public void onComplete() {
				Toast.makeText(getActivity().getApplicationContext()
					, "すべてのデータの読み込みが完了しました。", Toast.LENGTH_SHORT).show();
				
			}
			
			@Override
			public void onError(Exception e) {
				
			}
			
			@Override
			public void onReset(ReactiveAsyncLoader<List<TwitterList>> loader) {
				
			}
		});
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if(v.equals(_footer)){
			// TODO: footer event
			return;
		}
		
		TwitterList item = ((TwitterListAdapter)getListAdapter()).getItemAtPosition(position);
		Intent i = new Intent(getActivity().getApplicationContext(), TwitterListStatusesActivity.class);
		i.putExtra(TwitterListStatusesActivity.TWITTER_LIST, item);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getActivity().startActivity(i);
	}
}