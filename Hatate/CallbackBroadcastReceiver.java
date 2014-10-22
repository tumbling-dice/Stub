public abstract class CallbackBroadcastReceiver extends BroadcastReceiver {
	
	public static final String ACTION_CALLBACK = "twitter4j.auth.action.callback";
	public static final String KEY_DATA = "data";

	static class Data implements Serializable {
		AccessToken token;
		Exception exception;
		boolean isSuccess;

		static Data create(AccessToken token) {
			Data data = new Data();
			data.token = token;
			data.isSuccess = true;
			return data;
		}

		static Data create(Exception exception) {
			Data data = new Data();
			data.exception = exception;
			data.isSuccess = false;
			return data;
		}
	}

	/**
	 * CallbackBroadcastReceiverへのIntent作成
	 * @param data
	 * @return
	 */
	public static Intent createIntent(Data data) {
		Intent i = new Intent();
		i.setAction(ACTION_CALLBACK);
		i.putExtra(KEY_DATA, data);
		return i;
	}

	/**
	 * CallbackBroadcastReceiverを登録する際のIntentFilter作成
	 * @return
	 */
	public static IntentFilter createIntentFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_CALLBACK);
		return filter;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Data data = (Data) intent.getSerializableExtra(KEY_DATA);

		try {
			if(data.isSuccess) {
				onSuccess(data.token);
			} else {
				onError(data.exception);
			}
		} finally {
			getApplicationContext().unregisterReceiver(this);
		}
	}

	public abstract void onSuccess(AccessToken token);
	public abstract void onError(Exception exception);
}