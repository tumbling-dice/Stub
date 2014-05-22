public final class ImageUrlConverter {
	
	//http://blog.irons.jp/2009/12/23/twitter_thumb_url/
	
	public static final int T_UNKNOWN = -1;
	
	public static final int T_TWITTER = 0;
	private static final String P_TWITTER = "";
	
	public static final int T_TWITPIC = 1;
	private static final String P_TWITPIC = "http://twitpic\.com/(\w{6,7})";
	
	public static final int T_YFROG = 2;
	private static final String P_YFROG = "";
	
	public static final int T_PIXIV = 3;
	private static final String P_PIXIV = "";
	
	public static final int T_GYAZO = 4;
	private static final String P_GYAZO = "http://gyazo\.com/\w{32}";
	
	public static final int T_TUMBLR = 5;
	private static final String P_TUMBLR = "";
	
	public static final int T_INSTAGRAM = 6;
	private static final String P_INSTAGRAM = "http://instagr\.am/p/\w{10}/";
	
	private String baseUrl;
	private String convertedUrl;
	private int type = -1;
	
	public ImageUrlConverter(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public String getUrl() {
		if(convertedUrl == null) convertedUrl = convert(baseUrl);
		return convertedUrl;
	}
	
	public int getType() {
		return type;
	}
	
	private String convert(final String url) {
		
		if(url.startsWith("https://twitter.com/")) {
			// Twitter
			return match(T_TWITTER, P_TWITTER, url, new Func1<Matcher, String>() {
				@Override
				public String call(Matcher m) {
					//TODO:twitter
					return null;
				}
			});
		} else if(url.startsWith("http://twitpic.com/")) {
			// Twitpic
			// see:http://morizyun.github.io/blog/twitpic-full-image-download-script/
			return match(T_TWITPIC, P_TWITPIC, url, new Func1<Matcher, String>() {
				@Override
				public String call(Matcher m) {
					return "http://twitpic.com/show/thumb/" + m.group(1);
				}
			});
		} else if(url.startsWith("")) {
			// yfrog
			return match(T_YFROG, P_YFROG, url, new Func1<Matcher, String>() {
				@Override
				public String call(Matcher m) {
					//TODO:yfrog
					return null;
				}
			});
		} else if(url.startsWith("http://gyazo.com/")) {
			// gyazo
			return match(T_GYAZO, P_GYAZO, url, new Func1<Matcher, String>() {
				@Override
				public String call(Matcher m) {
					//TODO:gyazo
					return null;
				}
			});
		} else if(url.startsWith("http://instagr.am/p/")) {
			// instagram
			// see:http://staku.designbits.jp/get-instagram-thumbnail-url/
			// TODO:リダイレクトで飛ばされる
			return match(T_INSTAGRAM, P_INSTAGRAM, url, new Func1<Matcher, String>() {
				return url + "/media/?size=t";
			});
		}
		
		return url;
	}
	
	private String match(int type, String pattern, String expr, Func1<Matcher, String> onFind) {
		Matcher m = Pattern.compile(pattern).matcher(expr);
		
		if(m.find()){
			this.type = type;
			return onFind.call(m);
		}
		
		return expr;
	}
}