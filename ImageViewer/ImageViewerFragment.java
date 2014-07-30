public class ImageViewerFragment extends Fragment implements SurfaceHolder.Callback {
	
	public static final String KEY_URI = "uri";
	
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
	
	public static Bundle createArgument(String uri) {
		Bundle args = new Bundle();
		args.putString(KEY_URI, uri);
		return args;
	}
	
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
		
		if(args == null || !args.containsKey(KEY_URI)) throw new IllegalArgumentException("URIがありません");
		
		BitmapUtil bitmapUtil = null;
		Context context = getActivity().getApplicationContext();
		
		try {
			bitmapUtil = new BitmapUtil(args.getString(KEY_URI));
		} catch(IllegalArgumentException e) {
			Toast.makeText(context, "画像ではないURIが入力されました。", Toast.LENGTH_SHORT).show();
			return
		}
		
		final ProgressDialog prog = ActivityUtil.createProgress("画像を読み込んでいます...", getActivity());
		prog.setTitle("通信中");
		prog.show();
		
		bitmapUtil.getBitmapAsync(context, new Action1<Bitmap>(){
			@Override
			public void call(Bitmap bitmap){
				if(prog != null && prog.isShowing()) prog.dismiss();
				
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
				e.printStacktrace();
				if(prog != null && prog.isShowing()) prog.dismiss();
				Toast.makeText(getActivity().getApplicationContext(), "画像の取得に失敗しました。", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(_bitmap != null) {
			_bitmap.recycle();
			_bitmap = null;
		}
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
	
	public Bitmap getBitmap() {
		return _bitmap;
	}
	
	public String getUri() {
		return getArguments().getString(KEY_URI);
	}
	
}