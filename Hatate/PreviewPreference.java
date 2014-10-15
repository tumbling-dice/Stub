package inujini_.hatate.preference;

public class PreviewPreference extends Preference {

	public PreviewPreference(Context context) {
		super(context);
	}

	public PreviewPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PreviewPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onClick() {
		val intent = new Intent();
		intent.putExtra(Houtyou.KEY_IS_PREVIEW, true);
		new Houtyou().onReceive(getApplicationContext(), intent);
	}

}
