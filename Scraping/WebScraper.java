@ExtensionMethod({XmlPullParserExtensions.class})
public abstract class WebScraper implements Scraper {
	
	protected XmlPullParser _parser;
	@Accessors(prefix = "_") @Getter protected final String _url;
	@Accessors(prefix = "_") @Getter protected String _encoding;
	protected byte[] _cache = null;
	
	public WebScraper(String url) {
		_url = url;
	}
	
	public WebScraper(String url, String encoding) {
		_url = url;
		_encoding = encoding;
	}
	
	protected void init() throws IOException {
		_parser = createParser();
		
		if(_cache != null) {
			_parser.setInput(new StringReader(new String(_cache)), _encoding);
			return;
		}
		
		@Cleanup val con = (HttpURLConnection) new URL(_url).openConnection();
		con.setDoInput(true);
		con.setConnectTimeout(15000);
		con.setReadTimeout(timeout);
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
		
		_parser.setInput(new StringReader(new String(_cache)), _encoding);
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
		return specif(null, tagName, attributeFilter);
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
		}
		
		_parser = null;
		return null;
	}
	
	protected static XElement createElement(XmlPullParser parser) throws XmlPullParserException, IOException {
		
		int ev = parser.getEventType();
		
		val element = new XElement();
		val innerElements = new ArrayList<XElement>();
		boolean isInner = false;
		
		while(ev != XmlPullParser.END_TAG) {
			
			switch(ev) {
			case XmlPullParser.START_TAG:
				if(isInner) {
					innerElements.add(createElement(parser));
					break;
				}
				
				element.getNamespace(parser.getPrefix());
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
		
		int ev = _parser.getEventType();
		while(ev != XmlPullParser.END_DOCUMENT) ev = _parser.next();
		
		_parser = null;
	}
}