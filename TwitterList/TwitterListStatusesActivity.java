public class TwitterListStatusesActivity extends ActionBarActivity {
	
	public static final String TWITTER_LIST = "twitterList";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super(savedInstanceState);
		setContentView(R.layout.activity_twitter_list_statuses);
		
		final TwitterList twitterList = (TwitterList) getIntent().getSerializableExtra(TWITTER_LIST);
		
		
		//TODO: TimelineFragment�̍쐬
		//		twitterList��id��n���΂悢�H
	}
}