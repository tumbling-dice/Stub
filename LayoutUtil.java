public class LayoutUtil {

	private Context _context;
	private int _witdh;
	private int _height;

	public LayoutUtil(final View content, Context context) {
		_context = context;

		if(content.getWidth() == 0) {
			content.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
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

	public static int getContentViewId() {
		return android.R.id.content;
	}

	public int getDisplayWitdh() {
		return _witdh;
	}

	public int getDisplayHeight() {
		return _height;
	}

	public void fitViewToDisplay(View v, int width, int height) {

		int newWidth = (int) (width != ViewGroup.LayoutParams.MATCH_PARENT
									&& width != ViewGroup.LayoutParams.WRAP_CONTENT
					 ? _witdh * (width * 0.01)
					 : width);

		int newHeight = (int) (height != ViewGroup.LayoutParams.MATCH_PARENT
										&& height != ViewGroup.LayoutParams.WRAP_CONTENT
					  ? _height * (height * 0.01)
					  : height);

		ViewGroup.LayoutParams p = v.getLayoutParams();
		p.width = newWidth;
		p.height = newHeight;
		v.setLayoutParams(p);
	}

	public boolean isTablet() {
		if(OrientationUtil.isPortrait(_context)) {
			return _witdh >= 480;
		} else {
			return _height >= 480;
		}
	}

}