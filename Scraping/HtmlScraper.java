public final class HtmlScraper extends WebScraper {
	
	@Override
	protected XmlPullParser createParser() {
		val factory = XmlPullParserFactory.newInstance();
		factory.setValidating(false);
		factory.setFeature(Xml.FEATURE_RELAXED, true); 
		factory.setNamespaceAware(true);
		
		return factory.newPullParser();
	}
	
	public List<XElement> extractByClass(@NonNull String tagName, @NonNull final String className) {
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