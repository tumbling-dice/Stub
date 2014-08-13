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
	
	private List<T> _originalDatas;
	private OnPublishResultListener _listener;
	private CharSequence _constraint;
	
	/**
	 * List用Filter
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
			if(test(d, constraint)) filtered.add(d);
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
	
	/**
	 * データ追加
	 * @param data 追加するデータ
	 * @param index 追加する位置（値が0未満の場合は末尾に追加）
	 * @return データがフィルタ条件に一致していればtrue
	 */
	public synchronized boolean add(T data, int index) {
		if(_originalDatas == null) return false;
		
		if(position >= 0) {
			_originalDatas.add(index, data);
		} else {
			_originalDatas.add(data);
		}
		
		return test(data, _constraint);
	}
	
	/**
	 * データ追加
	 * @param data 追加するデータ
	 * @return データがフィルタ条件に一致していればtrue
	 */
	public synchronized boolean add(T data) {
		return add(data, -1);
	}
	
	/**
	 * データ追加
	 * @param data 追加するデータ
	 * @param index 追加する位置（値が0未満の場合は末尾に追加）
	 * @return データがフィルタ条件に一致していればtrue
	 */
	public synchronized boolean addWhenNothing(T data, int index) {
		if(_originalDatas.contains(data)) return false;
		return add(data, index);
	}
	
	/**
	 * データ追加
	 * @param data 追加するデータ
	 * @return データがフィルタ条件に一致していればtrue
	 */
	public synchronized boolean addWhenNothing(T data) {
		return addWhenNothing(data, -1);
	}
	
	/**
	 * データ削除
	 * @param data 削除するデータ
	 */
	public synchronized void remove(T data) {
		if(_originalDatas == null) return;
		_originalDatas.remove(data);
	}
	
	/**
	 * データ削除
	 * @param index 削除するデータの位置
	 */
	public synchronized void remove(int index) {
		if(_originalDatas == null) return;
		_originalDatas.remove(index);
	}
	
	/**
	 * 全データ取得
	 * @return フィルタが適用されていないデータもすべて取得します。
	 */
	public synchronized List<TimelineData> getOriginalDatas() {
		return _originalDatas;
	}
	
	/**
	 * <p>フィルタ破棄</p>
	 * <p>コールバックリスナーとフィルタで所持している全データを破棄します。</p>
	 */
	protected synchronized void dispose() {
		_listener = null;
		_originalDatas = null;
	}
	
	public void setOnPublishResultListener(OnPublishResultListener listener) {
		_listener = listener;
	}
	
	public CharSequence getConstraint() {
		return _constraint;
	}
	
	/**
	 * フィルタリングルール
	 * @param data 検査するデータ
	 * @param constraint 検索内容
	 * @return フィルタ対象の場合はtrue
	 */
	abstract boolean test(T data, CharSequence constraint);
	
}