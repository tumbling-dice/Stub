public final class ListViewExtensions {
	
	public static <T extends ListAdapter> T getRawAdapter(ListView listView) {
		ListAdapter adapter = listView.getAdapter();
		if(adapter == null) return null;
		
		if(adapter instanceof WrapperListAdapter) {
			adapter = adapter.getWrappedAdapter();
		}
		
		return (T) adapter;
	}
	
	public static List<Integer> getCheckedPositons(AbsListView listView) {
		val checkedPositions = listView.getCheckedItemPositions();
		val pos = new ArrayList<Integer>();
	 
		if(checkedPositions == null) return pos;
	 
		for(int i = 0, size = checkedPositions.size(); i < size; i++) {
			if(checkedPositions.valueAt(i)) {
				pos.add(checkedPositions.keyAt(i));
			}
		}
	 
		return pos;
	}
	
	
	
}