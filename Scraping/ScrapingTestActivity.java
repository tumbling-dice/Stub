@ExtensionMethod({Linq.class})
public class ScrapingTestActivity extends Activity {
	
	@Override
	public void onCreate(Bundle s) {
		super.onCreate(s);
		setContentView(R.layout.test);
		
		val prog = ActivityUtil.createProgress(this);
		
		new ReactiveAsyncTask<String, Void, String>(new Func1<String, List<XElement>>() {
			@Override
			public String call(String url) {
				
				try {
					@Cleanup val scraper = new HtmlScraper(url);
					
					return scraper.specify("div", new AttributeFilter() {
						@Override
						public boolean filter(XAttribute x) {
							return "class".equals(x.getName()) && "img-container".equals(x.getValue());
						}
					}).findInnerElement("img").getAttributeValue("src");
					
				} catch (Exception e) {
					throw new RuntimeException(e.getCause());
				}
				
			}
		}).setOnPreExecute(new Action(){
			@Override
			public void call() {
				prog.show();
			}
		}).setOnPostExecute(new Action1<String>() {
			@Override
			public void call(String src) {
				if(src == null) {
					Toast.makeText(getApplicationContext(), "URL取得に失敗しました", Toast.LENGTH_SHORT).show();
					return;
				}
				
				((UrlImageView) findViewById(R.id.urlImageView)).setImageUrl(src, src, new ImageCallback(){
					@Override
					public void call(Object key, Bitmap bitmap) {
						if(bitmap == null) {
							Toast.makeText(getApplicationContext()
								, String.format("画像取得に失敗しました\r\nURL:%s", key), Toast.LENGTH_SHORT).show();
							
							return;
						}
						((UrlImageView) findViewById(R.id.urlImageView)).setImageBitmap(bitmap);
					}
				});
				
			}
		}).execute("http://www.pixiv.net/member_illust.php?mode=medium&illust_id=6904619");
	}
	
}