public class LayoutUtil {
	
	private Context _context;
	private int _witdh;
	private int _height;
	
	public LayoutUtil(Context context) {
		_context = context;
		regetSize();
	}
	
	public void regetSize() {
		final View content = context.findViewById(android.R.id.content);
		if(content.getWidth == 0) {
			content.getViewTreeObserver().addGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					content.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					_witdh = content.getWidth();
					_height = content.getHeight();
				}
			});
		} else {
			_witdh = content.getWidth();
			_height = content.getHeight();
		}
	}
	
	public float getDisplayWitdh() {
		return _witdh;
	}
	
	public float getDisplayHeight() {
		return _height;
	}
	
	public void fitViewToDisplay(View v, float width, float height) {
		
		int newWidth = width != ViewGroup.LayoutParams.FILL_PARENT
								 && width != ViewGroup.LayoutParams.MATCH_PARENT
								 && width != ViewGroup.LayoutParams.WRAP_CONTENT
					 ? _witdh * (widthPercent * 0.01)
					 : width;
		
		int newHeight = height != ViewGroup.LayoutParams.FILL_PARENT
									&& height != ViewGroup.LayoutParams.MATCH_PARENT
									&& height != ViewGroup.LayoutParams.WRAP_CONTENT
					  ? _height * (heightPercent * 0.01)
					  : height;
		
		ViewGroup.LayoutParams p = v.getLayoutParams();
		p.witdh = newWidth;
		p.height = newHeight;
		v.setLayoutParams(p);
	}
	
	public boolean isTablet() {
		if(OrientationUtil.isPortrate(_context)) {
			return _witdh >= 480;
		} else {
			return _height >= 480;
		}
	}
	
}