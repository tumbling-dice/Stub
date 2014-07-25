public final class ImageUrlConverter {
	
	//http://blog.irons.jp/2009/12/23/twitter_thumb_url/
	//http://keagelog.blogspot.jp/2012/05/twitterurlurl.html
	
	public static final int T_UNKNOWN = -1;
	
	public static final int T_TWITPIC = 0;
	private static final String P_TWITPIC = "http://twitpic\.com/(\w{6,7})";
	
	public static final int T_YFROG = 1;
	
	public static final int T_PIXIV = 2;
	private static final String P_PIXIV = "";
	
	public static final int T_GYAZO = 3;
	private static final String P_GYAZO = "http://gyazo\.com/(\w{32})";
	
	public static final int T_TUMBLR = 4;
	private static final String P_TUMBLR = "";
	
	public static final int T_INSTAGRAM = 5;
	private static final String P_INSTAGRAM = "http://instagr\.am/p/(\w{10})/";
	
	public static final int T_TWIPPLE = 6;
		private static final String P_TWIPPLE = "http://p\.twipple\.jp/(\w{5})";
	
	public static final int T_IMGLY = 7;
	private static final String P_IMGLY = "http://img.ly/(\w{4})";
	
	private String _baseUrl;
	private String _convertedUrl;
	private int _type = -1;
	private boolean _isNeedScraping;
	
	public ImageUrlConverter(String baseUrl) {
		_baseUrl = baseUrl;
		_type = checkType(baseUrl);
	}
	
	public String getUrl() {
		if(_convertedUrl == null) _convertedUrl = convert(_baseUrl);
		return _convertedUrl;
	}
	
	public void getUrlFromScraping(Action1<String> callback) {
		scraping(_baseUri, callback);
	}
	
	public String getBaseUrl() {
		return _baseUrl;
	}
	
	public int getType() {
		return _type;
	}
	
	public boolean isNeedScraping() {
		return _isNeedScraping;
	}
	
	private String convert(final String url) {
		
		switch(_type) {
		case T_TWITPIC:
			// see:http://morizyun.github.io/blog/twitpic-full-image-download-script/
			return match(_type, P_TWITPIC, url, new Func1<Matcher, String>() {
				@Override
				public String call(Matcher m) {
					return "http://twitpic.com/show/thumb/" + m.group(1);
				}
			});
		case T_YFROG:
			return url + ":medium";
		case T_PIXIV:
			// TODO:pixiv
			_isNeedScraping = true;
			return url;
		case T_GYAZO:
			_isNeedScraping = true;
			return url
		case T_TUMBLR:
			// TODO:Tumblr
			_isNeedScraping = true;
			return url;
		case T_INSTAGRAM:
			// see:http://staku.designbits.jp/get-instagram-thumbnail-url/
			// TODO:リダイレクトで飛ばされる
			return url + "/media/?size=l";
		case T_TWIPPLE:
			return match(_type, P_TWIPPLE, url, new Func1<Matcher, String>() {
				@Override
				public String call(Matcher m) {
					return "http://p.twpl.jp/show/orig/" + m.group(1);
				}
			});
		case T_IMGLY:
			return match(_type, P_IMGLY, url, new Func1<Matcher, String>() {
				@Override
				public String call(Matcher m) {
					return "http://img.ly/show/full/" + m.group(1);
				}
			});
		case default:
			return url;
		}
	}
	
	private String scraping(String baseUrl, final Action1<String> callback) {
		switch(_type) {
		case T_PIXIV:
			break;
		case T_GYAZO:
			break;
		case T_TUMBLR:
			break;
		case default:
			_convertedUrl = convert(baseUrl);
			callback.call(_convertedUrl);
		}
	}
	
	private static String match(int type, String pattern, String expr, Func1<Matcher, String> onFind) {
		Matcher m = Pattern.compile(pattern).matcher(expr);
		return m.find() ? onFind.call(m) : expr;
	}
	
	private static int checkType(String url) {
		if(url.startsWith("http://twitpic.com/")) {
			return T_TWITPIC;
		} else if(url.startsWith("http://yfrog.com/")) {
			return T_YFROG;
		} else if(url.startsWith("http://gyazo.com/")) {
			return T_GYAZO;
		} else if(url.contains("tumblr.com/post")) {
			return T_TUMBLR;
		} else if(url.startsWith("http://instagr.am/p/")) {
			return T_INSTAGRAM;
		} else if(uri.startsWith("http://p.twipple.jp/")) {
			return T_TWIPPLE;
		} else if(uri.startsWith("http://img.ly/") {
			return T_IMGLY;
		} else {
			return T_UNKNOWN;
		}
	}
}