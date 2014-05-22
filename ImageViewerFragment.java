public class ImageViewerFragment extends Fragment implements SurfaceHolder.Callback {
	
	public static final String KEY_URL = "url";
	
	private Bitmap _bitmap;
	private SurfaceHolder _holder;
	private Matrix _matrix;
	private float _x, _y, _scale;
	private ScaleGestureDetector _scaleGestureDetector;
	private SimpleOnScaleGestureListener _scaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			_scale *= detector.getScaleFactor();
			draw();
			return true;
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_image_viewer, null);
		
		_scale = 1.0f;
		_scaleGestureDetector = new ScaleGestureDetector(v.getContext(), _scaleListener);
		
		SurfaceView sv = (SurfaceView) getView().findViewById(R.id.sfvImage);
		// callbackをセットする
		sv.getHolder().addCallback(this);
		sv.setOnTouchListener(new View.OnTouchListenser() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				_scaleGestureDetector.onTouchEvent(event);
				return true;
			}
		});
		
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle args = getArguments();
		
		if(args == null || !args.containsKey(KEY_URL)) throw new IllegalArgumentException("URLがありません");
		
		String url = args.getString(KEY_URL);
		
		new ImageLoader(60000, new Action1<Bitmap>(){
			@Override
			public void call(Bitmap bitmap){
				
				if(bitmap == null) {
					Toast.makeText(getActivity().getApplicationContext()
						, "画像の取得に失敗しました。", Toast.LENGTH_SHORT).show();
					return;
				}
				
				_bitmap = bitmap;
				_x = bitmap.getWidth();
				_y = bitmap.getHeight();
				_matrix = new Matrix();
				
				draw();
			}
		}, new Action1<Exception>(){
			@Override
			public void call(Exception e) {
				Toast.makeText(getActivity().getApplicationContext(), "画像の取得に失敗しました。", Toast.LENGTH_SHORT).show();
			}
		})
		.execute(url);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if(_bitmap == null) return;
		_holder = holder;
		draw();
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) { }
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) { }
	
	private void draw() {
		Canvas canvas = _holer.lockCanvas();
		
		_matrix.reset();
		
		// 画像を中央に寄せる
		_matrix.postTranslate(_scale, _scale);
		_matrix.postTranslate(-_x / 2 * _scale, -_y / 2 * _scale);
		_matrix.postTranslate(_x, _y);
		
		canvas.drawBitmap(_bitmap, _matrix, null);
		
		_holder.unlockCanvasAndPost(canvas);
	}
	
}