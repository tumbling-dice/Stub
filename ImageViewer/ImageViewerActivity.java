public class ImageViewerActivity extends FragmentActivity {
	
	public static final String KEY_URL = "url";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_viewer);
		
		Intent intent = getIntent();
		
		if(intent == null) {
			finish();
			return;
		}
		
		// urlが直接渡されてきたパターン
		if(intent.containsKey(KEY_URL)) {
			String url = intent.getStringExtra(KEY_URL);
			//TODO:fragmentにurlを渡す
			
			return;
		}
		
		String type = intent.getType();
		
		// MIME typeがimage
		if(type != null && type.startsWith("image/")) {
			Uri uri = intent.getData();
			
			return;
		}
	}
}