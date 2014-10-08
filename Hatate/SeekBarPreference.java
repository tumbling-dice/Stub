public class SeekBarPreference extends Preference implements OnSeekBarChangeListener {

	@Accessors(prefix="_") @Getter private int _max;
	@Accessors(prefix="_") @Getter private int _currentValue;
	
	private WeakReference<TextView> _txvValue;
	private WeakReference<SeekBar> _seekBar;

	public SeekBarPreference(Context context, int max) {
		super(context);
		_max = max;
	}
	
	public SeekBarPreference(Context context, int max, int currentValue) {
		super(context);
		_max = max;
		_currentValue = currentValue;
	}

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		
		// SeekBarPreference's attrs + android Preference's attrs
		val attrIds = new int[R.styleable.SeekBarPreference.length + android.R.styleable.Preference]
		System.arraycopy(R.styleable.SeekBarPreference, 0, attrIds, 0, R.styleable.SeekBarPreference.length);
		System.arraycopy(android.R.styleable.Preference, 0, attrIds
			, R.styleable.SeekBarPreference.length, android.R.styleable.Preference.length);
		
		@Cleanup("recycle") val t = context.obtainStyledAttributes(attrs, attrIds);
		
		// get max value
		_max = t.getInt(R.styleable.VolumePreference_max, 0);
		
		// get default value
		_currentValue = super.getPersistedInt(-1);
		_currentValue = _currentValue != -1
							? _currentValue
							: t.getInt(android.R.styleable.Preference_defaultValue, 0);
		
	}
	
	@Override
	protected View onCreateView(ViewGroup parent) {
		setWidgetLayoutResource(R.layout.seekbar_preference);
		return super.onCreateView(parent);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		
		val txtValue = (TextView) view.findViewById(R.id.txvValue);
		txtValue.setText(String.format("%d/%d", _currentValue, _max));
		if(_txvValue != null) _txvValue.clear();
		_txvValue = new WeakReference<TextView>(txtValue);

		val seekBar = (SeekBar) view.findViewById(R.id.skbVolume);
		seekBar.setMax(_max);
		seekBar.setProgress(_currentValue);
		seekBar.setOnSeekBarChangeListener(this);
		if(_seekBar != null) _seekBar.clear();
		_seekBar = new WeakReference<SeekBar>(seekBar);
	}
	
	public void setMax(int max) {
		_max = max;
		changeTextView();
		
		if(_seekBar == null) return;
		
		val seekBar = _seekBar.get();
		if(seekBar != null) seekBar.setMax(max);
	}
	
	public void setCurrentValue(int currentValue) {
		_currentValue = currentValue;
		changeTextView();
		
		if(_seekBar == null) return;
		
		val seekBar = _seekBar.get();
		if(seekBar != null) seekBar.setProgress(currentValue);
	}
	
	private void changeTextView() {
		if(_txvValue == null) return;
		
		val txtView = _txvValue.get();
		if(txtView != null) txtView.setText(String.format("%d/%d", _currentValue, _max));
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		_currentValue = progress
		changeTextView();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		_currentValue = seekBar.getProgress();
		
		// save value
		super.getEditor().putInt(super.getKey(), _currentValue).commit();
		
		val changedListener = super.getOnPreferenceChangeListener();
		if(changedListener != null)
			changedListener.onPreferenceChange(this, _currentValue);
	}

}
