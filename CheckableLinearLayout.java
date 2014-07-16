public class CheckableLinearLayout extends LinearLayout implements Checkable {
	
	private static final int[] CHECKED_STATE = { android.R.attr.state_checked, };
	
	private boolean _isChecked;
	
	public CheckedLinearLayout(Context context) {
		super(context);
	}
	
	public CheckedLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CheckedLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if(_isChecked) {
			mergeDrawableStates(drawableState, CHECKED_STATE);
		}
		
		return drawableState;
	}
	
	/* Checkable methods */
	
	@Override
	public boolean isChecked() {
		return _isChecked;
	}
	
	@Override
	public void setChecked(boolean checked) {
		if(_isChecked != checked) {
			_isChecked = checked;
			refreshDrawableState();
		}
	}
	
	@Override
	public void toggle() {
		setChecked(!_isChecked);
	}
	
}