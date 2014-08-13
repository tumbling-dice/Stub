public abstract class ListFilter<T> extends Filter {
	
	/** データをフィルタリングした後のコールバック用リスナー */
	public interface OnPublishResultListener<T> {
		
		/**
		 * データをフィルタリングした後のコールバック
		 * 
		 * @param constraint 検索単語
		 * @param results フィルタ後のデータ
		 * @param resultsCount フィルタ後のデータ件数
		 */
		void onPublishResult(CharSequence constraint, List<T> results, int resultsCount);
	}
	
	/** フィルタを適用していないデータ */
	private volatile List<T> _originalDatas;
	private OnPublishResultListener _listener;
	private CharSequence _constraint;
	
	/**
	 * 
	 * @param datas フィルタリングするデータ
	 * @param listener フィルタ後のコールバック
	 */
	public ListFilter(List<T> datas, OnPublishResultListener listener) {
		_originalDatas = datas;
		_listener = listener;
	}
	
	@Override
	private FilterResults performFiltering(CharSequence constraint) {
		_constraint = constraint;
		FilterResults results = new FilterResults();
		
		if(_originalDatas == null) return results;
		
		List<T> filtered = new ArrayList<T>();
		
		for(T d : _originalDatas) {
			if(test(d, constraint)) filterd.add(d);
		}
		
		results.count = filtered.size();
		results.value = filtered;
		
		return results;
	}
	
	@Override
	private void publishResults(CharSequence constraint, FilterResults results) {
		if(_listener == null) return;
		_listener.onPublishResult(constraint, (List<T>) results.value, results.count);
	}
	
	public synchronized boolean add(T data, int position) {
		if(_originalDatas.contains(data)) return false;
		
		if(position >= 0) {
			_originalDatas.add(position, data);
		} else {
			_originalDatas.add(data);
		}
		
		return test(data, _constraint);
	}
	
	public synchronized boolean add(T data) {
		return add(data, -1);
	}
	
	public synchronized void remove(T data) {
		_originalDatas.remove(data);
	}
	
	public List<TimelineData> getOriginalDatas() {
		return _originalDatas;
	}
	
	public CharSequence getConstraint() {
		return _constraint;
	}
	
	public void dispose() {
		_listener = null;
		_originalDatas = null;
	}
	
	abstract boolean test(T data, CharSequence constraint);
	
}