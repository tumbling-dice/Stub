package inujini_.nuechin.test.scraping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lombok.Cleanup;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@ExtensionMethod({XmlPullParserExtensions.class})
public abstract class WebScraper implements Scraper {

	protected XmlPullParser _parser;
	@Accessors(prefix = "_") @Getter protected final String _url;
	protected byte[] _cache = null;

	public WebScraper(String url) {
		_url = url;
	}

	protected abstract XmlPullParser createParser() throws XmlPullParserException;

	protected void init() throws IOException, XmlPullParserException {
		_parser = createParser();

		if(_cache != null) {
			_parser.setInput(new StringReader(new String(_cache)));
			return;
		}

		@Cleanup("disconnect") val con = (HttpURLConnection) new URL(_url).openConnection();
		con.setDoInput(true);
		con.setConnectTimeout(15000);
		con.setUseCaches(true);
		@Cleanup val in = con.getInputStream();

		val bos = new ByteArrayOutputStream();
		int read = 0;

		try {
			while((read = in.read()) != -1) {
				bos.write(read);
			}
			_cache = bos.toByteArray();
		} finally {
			bos.close();
		}

		val v = new String(_cache);
		_parser.setInput(new StringReader(v));
	}

	public List<XElement> extract(String tagName) throws XmlPullParserException, IOException {
		return extract(null, tagName, null);
	}

	public List<XElement> extract(String tagName, AttributeFilter attributeFilter) throws XmlPullParserException, IOException {
		return extract(null, tagName, attributeFilter);
	}

	@Override
	public List<XElement> extract(String namespace, @NonNull String tagName, AttributeFilter attributeFilter) throws XmlPullParserException, IOException {
		if(_parser == null) init();

		int ev = _parser.getEventType();
		val elements = new ArrayList<XElement>();

		while (ev != XmlPullParser.END_DOCUMENT) {

			switch(ev) {
			case XmlPullParser.START_TAG:
				if(tagName.equals(_parser.getName())
					&& (namespace == null || namespace.equals(_parser.getPrefix()))
					&& (attributeFilter == null || _parser.hasAttribute(attributeFilter))) {

					elements.add(createElement(_parser));
				}

				break;
			}

			ev = _parser.next();
		}

		_parser = null;
		return elements;
	}

	public XElement specify(String tagName, AttributeFilter attributeFilter) throws XmlPullParserException, IOException {
		return specify(null, tagName, attributeFilter);
	}

	@Override
	public XElement specify(String namespace, @NonNull String tagName, @NonNull AttributeFilter attributeFilter) throws XmlPullParserException, IOException {
		if(_parser == null) init();

		int ev = _parser.getEventType();

		while(ev != XmlPullParser.END_DOCUMENT) {
			switch(ev) {
			case XmlPullParser.START_TAG:
				if(tagName.equals(_parser.getName())
					&& (namespace == null || namespace.equals(_parser.getPrefix()))
					&& _parser.hasAttribute(attributeFilter)) {

					val element = createElement(_parser);
					close();
					return element;
				}

				break;
			}

			ev = _parser.next();
		}

		_parser = null;
		return null;
	}

	protected static XElement createElement(XmlPullParser parser) throws XmlPullParserException, IOException {

		int ev = parser.getEventType();

		val element = new XElement();
		val innerElements = new ArrayList<XElement>();
		boolean isInner = false;

		while(ev != XmlPullParser.END_TAG && ev != XmlPullParser.END_DOCUMENT) {

			switch(ev) {
			case XmlPullParser.START_TAG:
				if(isInner) {
					innerElements.add(createElement(parser));
					break;
				}

				element.setNamespace(parser.getPrefix());
				element.setTagName(parser.getName());
				element.setAttributes(parser.getAttributes());
				isInner = true;

				break;
			case XmlPullParser.TEXT:
				element.setText(parser.getText().trim());
				break;
			}

			ev = parser.next();
		}

		element.setInnerElements(innerElements);

		return element;
	}

	@Override
	public void close() {
		if(_parser == null) return;

		try {
			int ev = _parser.getEventType();
			while(ev != XmlPullParser.END_DOCUMENT) ev = _parser.next();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		_parser = null;
	}
}