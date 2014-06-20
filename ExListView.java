public class ExListView extends ListView {

	public interface OnFixedViewClickListener {
		void onClick(View v, int position);
	}

	public interface OnFixedViewLongClickListener {
		void onLongClick(View v, int position);
	}

	public interface OnFixedViewSelectedListener {
		void onSelected(View v, int position);
	}
	
	private ArrayList<FiexdViewEvent> mHeaders;
	private ArrayList<FiexdViewEvent> mFooters;
	
	private static final class FiexdViewEvent {
		private View mView;
		private OnFixedViewClickListener mOnClickListener;
		private OnFixedViewLongClickListener mOnLongClickListener;
		private OnFixedViewSelectedListener mOnSelectedListener;
		
		public FixedViewEvent(View view) {
			mView = view;
		}
		
		public FixedViewEvent(View view, OnFixedViewClickListener onClickListener) {
			mView = view;
			mOnClickListener = onClickListener;
		}
		
		public FixedViewEvent(View view, OnFixedViewLongClickListener mOnLongClickListener) {
			mView = view;
			mOnLongClickListener = onLongClickListener;
		}
		
		public FixedViewEvent(View view, OnFixedViewSelectedListener mOnSelectedListener) {
			mView = view;
			mOnSelectedListener = onSelectedListener;
		}
		
		public void setOnClickListener(OnFixedViewClickListener onClickListener) {
			mOnClickListener = onClickListener;
		}
		
		public void setOnLongClickListener(OnFixedViewLongClickListener onLongClickListener) {
			mOnLongClickListener = onLongClickListener;
		}
		
		public void setOnSelectedListener(OnFixedViewSelectedListener onSelectedListener) {
			mOnSelectedListener = onSelectedListener;
		}
		
		public void onClick(int position) {
			if(mOnClickListener != null) mOnClickListener.onClick(mView, position);
		}
		
		public void OnLongClick(int position) {
			if(mOnLongClickListener != null) mOnLongClickListener.onLongClick(mView, position);
		}
		
		public void OnSelected(int position) {
			if(mOnSelectedListener != null) mOnSelectedListener.onSelected(mView, position);
		}
	}
	
	private static final class OnItemClickListenerProxy implements OnItemClickListener {
	
		private OnItemClickListener mOriginalListener;
		private ArrayList<FiexdViewEvent> mHeaders;
		private ArrayList<FiexdViewEvent> mFooters;
		
		private static final int NONE = -1;
		
		public OnItemClickListenerProxy(ArrayList<FiexdViewEvent> headers
										, ArrayList<FiexdViewEvent> footers
										, OnItemClickListener originalListener) {
			mHeaders = headers;
			mFooters = footers;
			mOriginalListener = originalListener;
			
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			// ヘッダチェック
			int headerCount = getFixedEventCount(mHeader);
			
			if(headerCount != NONE && position < headerCount) {
				mHeaders.get(position).onClick(view, position);
				return;
			}
			
			int footerCount = getFixedEventCount(mHeader);
			
			// この時点でフッタがなければそのまま元のOnItemClickListenerのイベントを呼び出す
			if(footerCount == NONE) {
				mOriginalListener.onItemClick(parent, view, position, id);
				return;
			}
			
			// 元のOnItemClickListenerのイベントを呼ぶのかフッタのイベントを呼ぶのか判定する
			int adapterCount = parent.getCount() - footerCount;
			
			if(position > adapterCount) {
				int footerPosition = position - adapterCount;
				mFooters.get(footerPosition).onClick(footerPosition);
				return;
			}
			
			mOriginalListener.onItemClick(parent, view, position, id);
		}
		
		private static int getFixedEventCount(ArrayList<FiexdViewEvent> list) {
			return list != null ? list.size() : NONE;
		}
	}

	public ExListView(Context context) {
		super(context);
	}
	
	public ExListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ExListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void addHeaderView(View v, Object data, boolean isSelectable) {
		super.addHeaderView(v, data, isSelectable);
		
		if(mHeaders == null) mHeaders = new ArrayList<FixedViewEvent>();
		mHeaders.add(new FiexdViewEvent(v));
	}
	
	@Override
	public void addHeaderView(View v) {
		super.addHeaderView(v);
		
		if(mHeaders == null) mHeaders = new ArrayList<FixedViewEvent>();
		mHeaders.add(new FiexdViewEvent(v));
	}
	
	public void addHeaderView(View v, OnFixedViewClickListener onCickListener) {
		super.addHeaderView(v);
		
		if(mHeaders == null) mHeaders = new ArrayList<FixedViewEvent>();
		mHeaders.add(new FiexdViewEvent(v, onCickListener));
	}
	
	public void setHeaderEvent(int headerPosition, OnFixedViewClickListener onCickListener) {
		mHeaders.get(headerPosition).setOnClickListener(onCickListener);
	}
	
	// 以下Footerも同じような処理
	
	
	@Override
	public void setOnItemClickListener(OnItemClickListener listener) {
		super.setOnItemClickListener(new OnItemClickListenerProxy(mHeaders, mFooters, listener));
	}
}