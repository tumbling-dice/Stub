public class OnGestureListenerAdapter implements OnGestureListener, OnDoubleTapListener {
	
	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return true;
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return true;
	}
	
	@Override
	public void onShowPress(MotionEvent e) {}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return true;
	}
	
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		return true;
	}
	
	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return true;
	}
	
	@Override
	public onSingleTapConfirmed(MotionEvent e) {
		return true;
	}
	
}