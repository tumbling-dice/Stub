public class OauthService extends IntentService {

	public static final String KEY_CONSUMER_KEY = "consumerKey";
	public static final String KEY_CONSUMER_SECRET = "consumerSecret";

	public OauthService() {
		super("OauthService");
	}

	public OauthService(String name) {
		super(name);
	}
	
	public static Intent createIntent(String consumerKey, String consumerSecret, Context context) {
		Intent intent = new Intent(context, OauthService.class);
		intent.putExtra(KEY_CONSUMER_KEY, consumerKey);
		intent.putExtra(KEY_CONSUMER_SECRET, consumerSecret);

		return intent;
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		if(intent.hasExtra(KEY_CONSUMER_KEY)){
			startOauth(intent);
		} else {
			if(intent.getData == null) {
				throw new IllegalStateException();
			}
			getAccessToken(intent);
		}
	}

	/**
	 * Oauth認証開始
	 * @param intent
	 */
	private void startOauth(Intent intent) {
		String consumerKey = intent.getStringExtra(KEY_CONSUMER_KEY);
		String consumerSecret = intent.getStringExtra(KEY_CONSUMER_SECRET);
		String callbackUri = "oauth://callback";

		Configuration conf = new ConfigurationBuilder()
									.setOAuthConsumerKey(consumerKey)
									.setOAuthConsumerSecret(consumerSecret)
									.build();

		OAuthAuthorization oauth = new OAuthAuthorization(conf);
		oauth.setOAuthAccessToken(null);

		String uri;
		try {
			uri = oauth.getOAuthRequestToken(callbackUri).getAuthorizationURL();
		} catch (TwitterException e) {
			e.printStackTrace();
			CallbackBroadcastReceiver.Data data = CallbackBroadcastReceiver.Data.create(e);
			sendBroadcast(CallbackBroadcastReceiver.createIntent(data));
			return;
		}

		try {
			serialize(oauth, "oauth.dat");
		} catch (IOException e) {
			e.printStackTrace();
			CallbackBroadcastReceiver.Data data = CallbackBroadcastReceiver.Data.create(e);
			sendBroadcast(CallbackBroadcastReceiver.createIntent(data));
			return;
		}

		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
	}

	/**
	 * AccessToken取得
	 * @param intent
	 */
	private void getAccessToken(Intent intent) {
		OAuthAuthorization oauth = null;

		try {
			oauth = deserialize("oauth.dat");
		} catch(Exception e) {
			e.printStackTrace();
			CallbackBroadcastReceiver.Data data = CallbackBroadcastReceiver.Data.create(e);
			sendBroadcast(CallbackBroadcastReceiver.createIntent(data));
			return;
		}
		
		String verifier = intent.getData().getQueryParameter("oauth_verifier");
		AccessToken accessToken;
		try {
			accessToken = oauth.getOAuthAccessToken(verifier);
		} catch (TwitterException e) {
			e.printStackTrace();
			CallbackBroadcastReceiver.Data data = CallbackBroadcastReceiver.Data.create(e);
			sendBroadcast(CallbackBroadcastReceiver.createIntent(data));
			return;
		}

		CallbackBroadcastReceiver.Data data = CallbackBroadcastReceiver.Data.create(accessToken);
		sendBroadcast(CallbackBroadcastReceiver.createIntent(data));
	}

	private void serialize(OAuthAuthorization obj, String fileName) throws IOException {
		try {
			FileOutputStream fos = openFileOutput(fileName, 0);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
 			oos.writeObject(statuses)
		} finally {
			if(fos != null) {
				if(oos != null) oos.close();
				fos.close();
			}
		}
	}

	private OAuthAuthorization deserialize(String fileName) throws Exception {
		try {
			FileInputStream fis = openFileInput(path);
			ObjectInputStream ois = new ObjectInputStream(fis);
			return (OAuthAuthorization) ois.readObject();
		} finally {
			if(fis != null) {
				if(ois != null) ois.close();
				fis.close();
			}

			deleteFile(fileName);
		}
	}
}