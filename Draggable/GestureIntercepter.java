public class GestureIntercepter {
	
	public static void interceptGesture(View v, final GestureDetector detector) {
		v.setOnTouchLister(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});
	}
	
	public static void interceptGesture(View v, boolean isLongpressEnabled, OnGestureListener listener) {
		val detector = new GestureDetector(v.getContext(), listener);
		detector.setIsLongpressEnabled(isLongpressEnabled);
		interceptGesture(v, detector);
	}
	
	public static void interceptGesture(View v, OnGestureListener listener, OnDoubleTapListener doubleTapListener) {
		val detector = new GestureDetector(v.getContext(), listener);
		detector.setOnDoubleTapListener(doubleTapListener);
		interceptGesture(v, detector);
	}
	
	public static void interceptGesture(View v, boolean isLongpressEnabled, OnGestureListenerAdapter adapter) {
		val detector = new GestureDetector(v.getContext(), adapter);
		detector.setIsLongpressEnabled(isLongpressEnabled);
		if(isLongpressEnabled) detector.setOnDoubleTapListener(adapter);
		interceptGesture(v, detector);
	}
	
}