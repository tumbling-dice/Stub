public class EventableListPreference extends ListPreference {
	
	public interface OnChosenListener {
		public void onChosen(int index, String entry, String entryValue);
	}
	
	private int _selectedEntryIndex;
	@Accessors(prefix="_") @Setter private OnChosenListener _onChosenListener;
	
	public EventableListPreference(Context context) {
		super(context);
	}

	public EventableListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EventableListPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		
		val entries = super.getEntries();
		val entryValues = super.getEntryValues();
		
		if (entries == null || entryValues == null) {
			throw new IllegalStateException(
					"EventableListPreference requires an entries array and an entryValues array.");
		}
		
		val entryIndex = super.findIndexOfValue(super.getValue());

		builder.setSingleChoiceItems(entries, super.findIndexOfValue(super.getValue())
			, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					_selectedEntryIndex = which;
					if(_onChosenListener != null)
						_onChosenListener.onChosen(which, super.getEntries()[which], super.getEntryValues()[which]);
				}
		});
		
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EventableListPreference.this.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
				dialog.dismiss();
			}
		});
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		_onChosenListener = null;
		val entryValues = super.getEntryValues();
		
		if (positiveResult && _selectedEntryIndex >= 0 && entryValues != null) {
			val value = entryValues[_selectedEntryIndex].toString();
			if (callChangeListener(value)) {
				super.setValue(value);
			}
		}
	}
}