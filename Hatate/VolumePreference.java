package inujini_.hatate.preference;

import inujini_.hatate.R;

import lombok.Cleanup;
import lombok.Getter;
import lombok.val;
import lombok.experimental.Accessors;
import android.content.Context;
import android.media.AudioManager;
import android.preference.Preference;
import android.util.AttributeSet;

public class VolumePreference extends SeekBarPreference {
	
	@Accessors(prefix="_") @Getter private final int _volumeType;
	@Accessors(prefix="_") @Getter @Setter private OnPreferenceChangeListener _onPreferenceChangeListener;
	
	public VolumePreference(Context context, int volumeType) {
		if(volumeType < 0 || volumeType > 5)
			throw new IllegalArgumentException(String.format("you should use AudioManager's STREAM enums.\n volumeType:%d"
					, volumeType));
		
		super(context, 0);
		
		_volumeType = volumeType;
		
		init(_volumeType, context);
	}

	public VolumePreference(Context context, AttributeSet attrs) {
		@Cleanup("recycle") val t = context.obtainStyledAttributes(attrs, R.styleable.VolumePreference);
		_volumeType = t.getInt(R.styleable.VolumePreference_type, -1);

		if(_volumeType == -1)
			throw new IllegalStateException("type must not be null.");

		super(context, attrs);
		
		init(_volumeType, context);
	}

	public VolumePreference(Context context, AttributeSet attrs, int defStyle) {
		@Cleanup("recycle") val t = context.obtainStyledAttributes(attrs, R.styleable.VolumePreference);
		_volumeType = t.getInt(R.styleable.VolumePreference_type, -1);

		if(_volumeType == -1)
			throw new IllegalStateException("type must not be null.");
		
		super(context, attrs, defStyle);

		init(_volumeType, context);
	}
	
	private void init(int volumeType, Context context) {
		val am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		super.setMax(am.getStreamMaxVolume(volumeType));
		super.setCurrentValue(am.getStreamVolume(volumeType));
		
		super.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				((AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE))
					.setStreamVolume(_volumeType, (int) newValue, 0);
				
				if(_onPreferenceChangeListener != null)
					_onPreferenceChangeListener.onPreferenceChange(preference, newValue);
			}
		});
	}
	
	@Override
	public void setCurrentValue(int currentValue) {
		super.setCurrentValue(currentValue);
		((AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE))
					.setStreamVolume(_volumeType, currentValue, 0);
	}
	
	@Override
	public void setMax(int max) {
		throw new UnsupportedOperationException("VolumePreference's max value is defined by volume type and device.");
	}
	
	public void setVolumeType(int volumeType) {
		if(volumeType < 0 || volumeType > 5)
			throw new IllegalArgumentException(String.format("you should use AudioManager's STREAM enums.\n volumeType:%d"
					, volumeType));
		
		_volumeType = volumeType;
		val am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		super.setMax(am.getStreamMaxVolume(volumeType));
		super.setCurrentValue(am.getStreamVolume(volumeType));
	}
}
