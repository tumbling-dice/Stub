public interface Scraper implements Closeable {
	
	/* inner objects */
	@Data
	public static class XElement {
		private String namespace;
		private String tagName;
		private List<XAttribute> attributes;
		private String text;
		private List<XElement> innerElements;
		
		public XElement findInnerElement(String tagName) {
			return findInnerElement(null, tagName, null);
		}
		
		public XElement findInnerElement(String namespace, String tagName) {
			return findInnerElement(namespace, tagName, null);
		}
		
		public XElement findInnerElement(String namespace, @NonNull String tagName, AttributeFilter attributeFilter) {
			
			for(val e : this.innerElements) {
				if(!tagName.equals(element.getTagName())
					|| (namespace != null && !namespace.equals(e.getNamespace()))) {
					
					val tmp = seek(e, namespace, tagName, attributeFilter);
					if(tmp != null) return tmp else continue;
				}
				
				if(attributeFilter != null) {
					boolean isHit = false;
					for(val attr : element.getAttributes()) {
						if(attributeFilter.filter(attr)) {
							isHit = true;
							break;
						}
					}
					
					if(!isHit) {
						val tmp = seek(e, namespace, tagName, attributeFilter);
						if(tmp != null) return tmp else continue;
					}
				}
				
				return e;
			}
			
			return null;
		}
		
		private static XElement seek(XElement element, String namespace, String tagName, AttributeFilter attributeFilter) {
			val elements = element.getInnerElements();
			if(elements == null) return null;
			
			for(val e : elements) {
				val tmp = e.findInnerElement(namespace, tagName, attributeFilter);
				if(tmp != null) return tmp;
			}
			
			return null;
		}
		
	}
	
	@Data
	public static class XAttribute {
		private String namespace;
		private String name;
		private String value;
	}
	
	public interface AttributeFilter {
		boolean filter(XAttribute attribute);
	}
	
	/* methods */
	protected XmlPullParser createParser();
	List<XElement> extract(String namespace, String tagName, AttributeFilter attributeFilter) throws XmlPullParserException, IOException;
	XElement specify(String namespace, String tagName, AttributeFilter attributeFilter) throws XmlPullParserException, IOException;
	
}