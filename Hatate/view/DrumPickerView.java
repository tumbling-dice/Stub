public class DrumPickerView extends FrameLayout {
	
	private RowInfo _rowInfo;
	
	public DrumPickerView(Context context) {
		super(context);
	}
	
	public DrumPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public DrumPickerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	
	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		// if child is not first or TextView then avoid it.
		if(index != 0 || !(child instanceof TextView)) return;
		
		// get TextView infomations.
		if(_rowInfo == null) _rowInfo = new RowInfo();
		_rowInfo.textViewInfo = new InternalTextViewInfo((TextView) child, params);
	}
	
	
	/* internal structures */
	static class InternalTextViewInfo {
		final ViewGroup.LayoutParams layoutParams;
		final int textSize;
		final int textColor;
		final Typeface typeface;
		
		InternalTextViewInfo(TextView txtView, ViewGroup.LayoutParams params) {
			layoutParams = params;
			textSize = txtView.getTextSize();
			textColor = textView.getCurrentTextColor();
			typeface = txtView.getTypeface();
		}
	}
	
	static class RowInfo {
		InternalTextViewInfo textViewInfo;
		
	}
	
}