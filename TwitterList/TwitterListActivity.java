public class TwitterListActivity extends ActionBarActivity {
	
	public static final String SCREEN_NAME = "screenName";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super(savedInstanceState);
		setContentView(R.layout.activity_twitter_list);
		
		ActionBarUtil.upsideDown(this);
		
		final String screenName = getIntent().getString(SCREEN_NAME);
		
		//TODO: 1.FragmentPagerAdapterの作成
		//		2.ViewPagerの作成
		//		3.ActionBarがらみのあれこれ
	}
}