public class TabAddDialogFragment extends DialogFragment {
	
	public static final int TYPE_USER = 0;
	public static final int TYPE_LIST = 1;
	public static final int TYPE_SEARCH = 2;
	
	public static final String KEY_TYPE = "type";
	public static final String KEY_PARAM = "param";
	
	public interface TabAddedListener {
		void onTabAdded(String tabName, String tag);
	}
	
	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_tab_add, null);
		
		Button btnOK = (Button) v.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addTab();
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		return v;
	}
	
	private void addTab() {
		Bundle args = getArguments();
		final int type = args.getInt(KEY_TYPE, -1);
		if(type == -1) throw new IllegalArgumentsException("引数が不正です。(type)");
		
		final String param = args.getString(KEY_PARAM);
		if(param == null) throw new IllegalArgumentsException("引数が不正です。(param)");
		
		final String tabName = ((EditText) getView().findViewById(R.id.edtName)).getText().toString();
		
		// tag = type + "_" + param
		String tag = null;
		
		switch(type) {
			case TYPE_USER:
				tag = "user_" + param;
				break;
			case TYPE_LIST:
				tag = "list_" + param;
				break;
			case TYPE_SEARCH:
				tag = "search_" + param;
				break;
			default:
				throw new IllegalArgumentsException(String.format("引数が不正です。(type:%d)", type));
		}
		
		final ProgressDialog prog = new ProgressDialog(getActivity());
		
		new ReactiveAsyncTask<String, Void, String>(new Func1<String, String>() {
			@Override
			public String call(String tag) {
				//TODO:insert
				return tag;
			}
		}).setOnPreExecute(new Action() {
			@Override
			public void call() {
				prog.show();
			}
		}).setOnPostExecute(new Action1<String>() {
			@Override
			public void call(String tag) {
				prog.dismiss();
				
				Activity activity = getActivity();
				if(activity instanceof TabAddedListener) {
					((TabAddedListener)activity).onTabAdded(tabName, tag);
				} else {
					Toast.makeText(activity.getApplicationContext(), tabName + "を追加しました。", Toast.LENGTH_SHORT).show();
				}
				
				dismiss();
			}
		}).setOnError(new Action1<Exception>() {
			@Override
			public void call(Exception e) {
				prog.dismiss();
			}
		}).execute(tag);
		
		
	}
	
}