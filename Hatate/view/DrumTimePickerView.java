public class DrumTimePickerView extends DrumPickerView {
	
	/* enum */
	public static final int FORMAT_24HOUR = 0;
	public static final int FORMAT_12HOUR = 1;
	public static final int MERIDIEM_AM = 0;
	public static final int MERIDIEM_PM = 1;
	
	/* properties */
	private int _format;
	private int _meridiem = -1;
	private String _hour;
	private String _minute;
	
	public DrumTimePickerView(Context context, int format) {
		super(context);
		initByValue(format, 0, 0, 0, context);
	}
	
	public DrumTimePickerView(Context context, int format, int meridiem) {
		super(context);
		initByValue(format, meridiem, 0, 0, context);
	}
	
	public DrumTimePickerView(Context context, int format, int defaultHour, int defaultMinute) {
		super(context);
		initByValue(format, 0, defaultHour, defaultMinute, context);
	}
	
	public DrumTimePickerView(Context context, int format, int meridiem, int defaultHour, int defaultMinute) {
		super(context);
		initByValue(format, meridiem, defaultHour, defaultMinute, context);
	}
	
	public DrumTimePickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initByAttr(context, attrs);
	}
	
	public DrumTimePickerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initByAttr(context, attrs);
	}
	
	private void initByAttr(Context context, AttributeSet attrs) {
		TypedArray t = null;
		try {
			t = context.obtainStyledAttributes(attrs, R.styleable.DrumTimePickerView);
			
			_format = t.getInt(R.styleable.DrumTimePickerView_hour_format, 0);
			if(_format == FORMAT_12HOUR)
				_meridiem = t.getInt(R.styleable.DrumTimePickerView_meridiem, 0);
			
			_hour = String.format("02%d", t.getInt(R.styleable.DrumTimePickerView_hour, 0));
			_minute = String.format("02%d", t.getInt(R.styleable.DrumTimePickerView_minute, 0));
			
		} finally {
			if(t != null) {
				t.recycle();
				t = null;
			}
		}
		
		// FIXME:set drumtimepicker_view Resources to DrumPickerView.
		final Resources res = context.getResources();
		final int[] resourceIds = res.getIntArray(R.array.drumtimepicker_resources);
		
		
	}
	
	private static initByValue(int format, int meridiem, int defaultHour, int defaultMinute, Context context) {
		switch(format) {
			case FORMAT_24HOUR:
			case FORMAT_12HOUR:
				_format = format;
				break;
			default:
				_format = FORMAT_12HOUR;
				break;
		}
		
		switch(meridiem) {
			case MERIDIEM_AM:
			case MERIDIEM_PM:
				_meridiem = meridiem;
				break;
			default:
				_meridiem = MERIDIEM_AM;
				break;
		}
		
		
	}
	
}