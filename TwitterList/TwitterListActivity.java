public class TwitterListActivity extends ActionBarActivity {
	
	public static final String SCREEN_NAME = "screenName";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super(savedInstanceState);
		setContentView(R.layout.activity_twitter_list);
		
		ActionBarUtil.upsideDown(this);
		
		final String screenName = getIntent().getString(SCREEN_NAME);
		
		//TODO: 1.FragmentPagerAdapter�̍쐬
		//		2.ViewPager�̍쐬
		//		3.ActionBar����݂̂��ꂱ��
	}
}