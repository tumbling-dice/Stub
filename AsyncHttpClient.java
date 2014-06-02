//@see http://d.hatena.ne.jp/ryopei/20091201/1259668685
public class AsyncHttpClient<TReturn> extends AsyncTask<Uri, Void, TReturn> {
	
	public static final int HTTP_GET = 0;
	public static final int HTTP_POST = 1;
	
	private int _method = -1;
	private HttpSettings _settings;
	private DefaultHttpClient _httpClient:
	private MultipartEntity _entity;
	private List<Pair<String, String>> _headers;
	
	private int _statusCode = -1;
	private boolean _isFailed;
	private Exception _responseException;
	
	private OnSuccessListener _onSuccess;
	private OnFailedListener _onFailed;
	private OnPostListener _onPost;
	
	
	static class HttpSettings {
	
		int timeout = 60000;
		SSLSessionCache sessionCache;
		String contentCharset = "UTF-8";
		String userAgent;
		CookieStore cookies;
		
	}
	
	public AsyncHttpClient(int method, OnSuccessListener onSuccess) {
		_method = method;
		_onSuccess = onSuccess;
		_settings = new HttpSettings();
	};
	
	public AsyncHttpClient<TReturn> setOnPost(OnPostListener onPost) {
		_onPost = onPost;
		return this;
	}
	
	public AsyncHttpClient<TReturn> setOnFailed(OnFailedListener onFailed) {
		_onFailed = onFailed;
		return this;
	}
	
	public AsyncHttpClient<TReturn> addEntity(String key, String value) {
		if(_method != HTTP_POST) throw new IllegalStateException("method is not POST.");
		
		if(_entity == null) _entity = new MultipartEntity();
		_entity.addPart(key, new StringBody(value));
		
		return this;
	}
	
	public AsyncHttpClient<TReturn> addEntity(String key, File value) {
		if(_method != HTTP_POST) throw new IllegalStateException("method is not POST.");
		
		if(_entity == null) _entity = new MultipartEntity();
		_entity.addPart(key, new FileBody(value));
		
		return this;
	}
	
	public AsyncHttpClient<TReturn> setSSLSessionCache(Context context) {
		_settings.sessionCache = new SSLSessionCache(context);
		return this;
	}
	
	public AsyncHttpClient<TReturn> setTimeout(int timeout) {
		_settings.timeout = timeout;
		return this;
	}
	
	public AsyncHttpClient<TReturn> setContentCharset(String contentCharset) {
		_settings.contentCharset = contentCharset;
		return this;
	}
	
	public AsyncHttpClient<TReturn> setUserAgent(String userAgent) {
		_settings.userAgent = userAgent;
		return this;
	}
	
	public AsyncHttpClient<TReturn> addHeader(String name, String value) {
		if(_headers == null) _headers = new ArrayList<Pair<String, String>>();
		_headers.add(Pair.create(name, value));
		return this;
	}
	
	public AsyncHttpClient<TReturn> addCookie(String domain, String path, String key, String value) {
		if(_settings.cookies == null) _settings.cookies = new BasicCookieStore();
		
		BasicClientCookie cookie = new BasicClientCookie(key, value);
		cookie.setDomain(domain);
		cookie.setPath(path);
		
		_settings.cookies.addCookies(cookie);
		
		return this;
	}
	
	@Override
	protected void onPreExecute() {
		
		int timeout = _settings.timeout;
		
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		
		if(_settings.userAgent != null) HttpProtocolParams.setUserAgent(params, _settings.userAgent);
		HttpProtocolParams.setContentCharset(params, _settings.contentCharset);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		
		SchemeRegistry schreg = new SchemeRegistry();
		schreg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schreg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(timeout, _settings.sessionCache), 443));
		
		ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(params, schreg);
		
		_httpClient = new DefaultHttpClient(connManager, params);
		
		if(_settings.cookies != null) _httpClient.setCookieStore(_settings.cookies);
	}
	
	@Override
	protected TReturn doInBackground(Uri... params) {
		for(Uri uri : params) {
			switch(_method) {
				case HTTP_GET:
					return doGet(new HttpGet(uri.toString()));
				case HTTP_POST:
					return doPost(new HttpPost(uri.toString()));
				default:
					throw new IllegalStateException("Not Defined Method.");
			}
		}
	}
	
	private TReturn doGet(HttpGet req) {
		
		if(_headers != null) {
			for(Pair<String, String> p : _headers) {
				req.setHeader(p.first, p.second);
			}
		}
		
		return executeHttp(req);
	}
	
	private TReturn doPost(HttpPost req) {
		
		if(_headers != null) {
			for(Pair<String, String> p : _headers) {
				req.setHeader(p.first, p.second);
			}
		}
		
		if(_entity != null) req.setEntity(_entity);
		
		return executeHttp(req);
	}
	
	private TReturn executeHttp(HttpUriRequest req) {
		try {
			return _httpClient.execute(req, new ResponseHandler<TReturn>(){
				@Override
				public TReturn handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					_statusCode = response.getStatusLine().getStatusCode();
					switch(_statusCode) {
						case HttpStatus.SC_OK:
							return _onSuccess.call(response);
						default:
							_isFailed = true;
							return null;
					}
				}
			});
		} catch(ClientProtocolException e) {
			_isFailed = true;
			e.printStacktrace();
			_responseException = e;
			return null;
		} catch(IOException e) {
			_isFailed = true;
			e.printStacktrace();
			_responseException = e;
			return null;
		} finally {
			_httpClient.getConnectionManager().shutdown();
		}
	}
	
	@Override
	protected void onPostExecute(TReturn r) {
		if(_isFailed) {
			if(_onFailed != null) {
				_onFailed.call(_statusCode, _responseException);
				return;
			} else {
				throw new RuntimeException(_responseException);
			}
		}
		
		if(_onPost != null) _onPost.call(r);
	}
	
	public interface OnSuccessListener {
		public TReturn call(HttpResponse response);
	}
	
	public interface OnFailedListener {
		public void call(int statusCode, Exception responseException);
	}
	
	public interface OnPostListener {
		public void call(TReturn r);
	}
}