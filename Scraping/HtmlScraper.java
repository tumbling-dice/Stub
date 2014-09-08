package inujini_.nuechin.test.scraping;

import java.io.IOException;
import java.util.List;

import lombok.NonNull;
import lombok.val;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Xml;

public final class HtmlScraper extends WebScraper {

	public HtmlScraper(String url) {
		super(url);
	}

	@Override
	protected XmlPullParser createParser() throws XmlPullParserException {
		val factory = XmlPullParserFactory.newInstance();
		factory.setValidating(false);
		factory.setFeature(Xml.FEATURE_RELAXED, true);
		factory.setNamespaceAware(true);

		return factory.newPullParser();
	}

	public List<XElement> extractByClass(@NonNull String tagName, @NonNull final String className) throws XmlPullParserException, IOException {
		return extract(tagName, new AttributeFilter() {
			@Override
			public boolean filter(XAttribute attr) {
				val attrName = attr.getName();
				val attrValue = attr.getValue();

				return "class".equals(attrName) && className.equals(attrValue);
			}
		});
	}

	public XElement specifyById(@NonNull String tagName, @NonNull final String id) throws XmlPullParserException, IOException {
		return specify(tagName, new AttributeFilter() {
			@Override
			public boolean filter(XAttribute attr) {
				val attrValue = attr.getValue();
				if(attrValue == null) return false;

				val attrName = attr.getName();

				return "id".equals(attrName) && attrValue.equals(id);
			}
		});
	}

}