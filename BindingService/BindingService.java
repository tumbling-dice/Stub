public abstract class BindingService extends Service {
	
	@Override
	public IBinder onBind(Intent intent) {
		return new Messenger(new WrapedHandler()).getBinder();
	}
	
	private final class WrapedHandler {
		@Override
		public void handleMessage(Message msg) {
			Bundle ret = onProcess(msg);
			
			if(ret == null) ret = new Bundle();
			
			Messenger replyTo = msg.replyTo;
			if(replyTo != null) onCallback(msg.what, ret, replyTo);
		}
	}
	
	abstract Bundle onProcess(Message msg);
	abstract void callback(int what, Bundle ret, Messenger replyTo);
}