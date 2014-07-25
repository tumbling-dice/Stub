public class ImageViewerActivity extends FragmentActivity {
	
	// TODO:画像を保存するかどうかの判定に使用
	private boolean _isNetwork;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_viewer);
		
		Intent intent = getIntent();
		
		if(intent == null) {
			Toast.makeText(getApplicationContext(), "intentがありません。", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		ImageViewerFragment f = new ImageViewerFragment();
		
		if(intent.containsKey(ImageViewerFragment.KEY_URI)) {
			// Uriが直接渡されてきたパターン
			String uri = intent.getStringExtra(ImageViewerFragment.KEY_URI);
			_isNetwork = intent.getBooleanExtra(ImageViewerFragment.KEY_IS_NETWORK, false);
			f.setArguments(ImageViewerFragment.createArgument(uri, isNetwork));
			
		} else {
			String type = intent.getType();
			
			if(type == null) {
				Toast.makeText(getApplicationContext(), "typeがありません。", Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			
			if(!type.startsWith("image/")) {
				Toast.makeText(getApplicationContext(), "MIME-Typeがimageではありません。", Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			
			Uri uri = intent.getData();
			// Uriのschemeから内部ストレージかどうか判定
			_isNetwork = uri.getScheme().startsWith("http");
			f.setArguments(ImageViewerFragment.createArgument(uri.toString(), isNetwork));
		}
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// TODO:Fragmentの生成
		
		ft.commit();
	}
}