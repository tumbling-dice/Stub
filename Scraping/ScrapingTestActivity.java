package inujini_.nuechin.test.scraping;

import inujini_.function.Function.Action;
import inujini_.function.Function.Action1;
import inujini_.function.Function.Func1;
import inujini_.linq.Linq;
import inujini_.nuechin.test.R;
import inujini_.nuechin.test.scraping.Scraper.AttributeFilter;
import inujini_.nuechin.test.scraping.Scraper.XAttribute;
import inujini_.nuechin.test.util.ActivityUtil;
import inujini_.nuechin.test.util.image.HttpImageManager.ImageCallback;
import inujini_.nuechin.test.util.reactive.ReactiveAsyncTask;
import inujini_.nuechin.test.util.view.UrlImageView;
import lombok.Cleanup;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

public class ScrapingTestActivity extends Activity {

	@Override
	public void onCreate(Bundle s) {
		super.onCreate(s);
		setContentView(R.layout.test);


		val prog = ActivityUtil.createLoadDialog("test", this);

		new ReactiveAsyncTask<String, Void, String>(new Func1<String, String>() {
			@Override
			public String call(String url) {

				try {
					@Cleanup val scraper = new HtmlScraper(url);

					// pixiv
					/*val contents = scraper.specify("meta", new AttributeFilter() {
						@Override
						public boolean filter(XAttribute x) {
							if(!"property".equals(x.getName())) return false;

							val v = x.getValue();
							return v.equals("og:image");
						}
					}).getAttributeValue("content").split("/");

					val fileName = contents[contents.length - 1];
					contents[contents.length - 1] = fileName.replace("s.", "128x128.");

					String scr = "";

					for(int i = 0; i < contents.length; i++) {
						if(contents.length != (i + 1)) {
							scr = scr.concat(contents[i]).concat("/");
						} else {
							scr = scr.concat(contents[i]);
						}
					}

					return scr;*/

					return scraper.specify("meta", new AttributeFilter() {
						@Override
						public boolean filter(XAttribute attribute) {
							return attribute.getName().equals("property") && attribute.getValue().equals("og:image");
						}
					}).getAttributeValue("content");


				} catch (Exception e) {
					e.printStackTrace();
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
				prog.dismiss();
				if(src == null) {
					Toast.makeText(getApplicationContext(), "URL取得に失敗しました", Toast.LENGTH_SHORT).show();
					return;
				}

				((UrlImageView) findViewById(R.id.imgIcon)).setImageUrl(src, src, new ImageCallback(){
					@Override
					public void call(Object key, Bitmap bitmap) {
						if(bitmap == null) {
							Toast.makeText(getApplicationContext()
								, String.format("画像取得に失敗しました\r\nURL:%s", key), Toast.LENGTH_SHORT).show();

							return;
						}
						((UrlImageView) findViewById(R.id.imgIcon)).setImageBitmap(bitmap);
					}
				});

			}
		}).setOnError(new Action1<Exception>() {
			@Override
			public void call(Exception e) {
				prog.dismiss();
				Toast.makeText(getApplicationContext(), "エラー発生", Toast.LENGTH_SHORT).show();
			}
		}).execute("");
	}

}