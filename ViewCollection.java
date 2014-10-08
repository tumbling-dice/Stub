public class ViewCollection {
	
	private final static String TAG = "ViewCollection";
	private final WeakReference<ViewGroup> _viewGroup;
	
	public static class ViewQuery {
		
		private final WeakReference<List<View>> _views;
		
		public interface Callback {
			void call(View view, int id);
		}
		
		public interface CastedCallback<T extends View> {
			void call(T view, int id);
		}
		
		public ViewQuery(List<View> views) {
			_views = views != null
					? new WeakReference<List<View>>(_views)
					: new WeakReference<List<View>>(new ArrayList<View>());
		}
		
		public void forEach(Callback callback) {
			val views = _views.get();
			if(views == null) return;
			
			for(val v : views) {
				callback.call(v, v.getId());
			}
		}
		
		public <T extends View> void forEach(Class<T> clazz, CastedCallback<T> callback) {
			val views = _views.get();
			if(views == null) return;
			
			for(val v : views) {
				if(clazz.isInstance(v)) {
					callback.call(clazz.cast(v), v.getId());
				}
			}
		}
		
		public List<View> getViewList() {
			val v = _views.get();
			return v != null ? v : new ArrayList<View>();
		}
		
		@Override
		protected void finalize() throws Throwable {
			try {
				super.finalize();
			} finally {
				if(_views != null) {
					val v = _views.get();
					if(v == null) return;
					
					v.clear();
					_views.clear();
					_views = null;
				}
			}
		}

		
	}
	
	public ViewCollection(View view) {
		if(!isViewGroup(view)) throw new IllegalArgumentException("");
		_viewGroup = toWeak(v);
	}
	
	public ViewCollection(View view, int rootViewId) {
		val v = view.findViewById(rootViewId);
		if(!isViewGroup(v)) throw new IllegalArgumentException("");
		_viewGroup = toWeak(v);
	}
	
	public ViewCollection(View view, Object rootViewTag) {
		val v = view.findViewWithTag(rootViewTag);
		if(!isViewGroup(v)) throw new IllegalArgumentException("");
		_viewGroup = toWeak(v);
	}
	
	public ViewCollection(Activity activity) {
		val v = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
		if(!isViewGroup(v)) throw new IllegalArgumentException("");
		_viewGroup = toWeak(v);
	}
	
	public ViewCollection(Fragment fragment) {
		val v = fragment.getView();
		
		if(!isViewGroup(v)) throw new IllegalArgumentException("");
		
		_viewGroup = toWeak(v);
	}
	
	public List<View> flatten() {
		return toQuery().getViewList();
	}
	
	public ViewQuery toQuery() {
		return filter(new Predicate<View>() {
			@Override
			public boolean test(View v) {
				return true;
			}
		});
	}
	
	public ViewQuery filter(Predicate<View> condition) {
		
		val viewList = new ArrayList<View>();
		seekInnerViews(viewList, _viewGroup.get(), condition);
		
		return new ViewQuery(viewList);
	}
	
	public ViewQuery filter(final int... ids) {
		
		if(ids == null || ids.length == 0) throw new IllegalArgumentException("");
		
		val viewList = new ArrayList<View>();
		
		seekInnerViews(viewList, _viewGroup.get(), new Predicate<View>() {
			@Override
			public boolean test(View v) {
				val viewId = v.getId();
				
				for(val id : ids) {
					if(id == viewId) return true;
				}
				
				return false;
			}
		});
		
		return new ViewQuery(viewList);
	}
	
	public <T extends View> T getView(int id) {
		val v = _viewGroup.get();
		if(v == null) return null;
		
		return (T) v.findViewById(id);
	}
	
	public <T extends View> T getView(Object tag) {
		val v = _viewGroup.get();
		if(v == null) return null;
		
		return (T) v.findViewWithTag(tag);
	}
	
	private static void seekInnerViews(List<View> list, ViewGroup viewGroup, Predicate<View> condition) {
		if(viewGroup == null) {
			Log.w(TAG, String.format("ViewCollection called seekInnerViews but viewGroup was null."));
			return;
		}
		
		for(int i = 0, size = viewGroup.getChildCount(); i < size; i++) {
			val v = viewGroup.getChildAt(i);
			
			if(condition.test(v)) list.add(v);
			
			if(isViewGroup(v)) {
				seekInnerViews(list, ((ViewGroup) v), condition);
			}
		}
	}
	
	private static WeakReference<ViewGroup> toWeak(ViewGroup v) {
		return new WeakReference<ViewGroup>(v);
	}
	
	private static WeakReference<ViewGroup> toWeak(View v) {
		return new WeakReference<ViewGroup>((ViewGroup) v);
	}
	
	private static boolean isViewGroup(@NonNull View v) {
		return v instanceof ViewGroup;
	}
	
}

