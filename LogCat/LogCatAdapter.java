public final class LogCatAdapter extends BaseExpandableListAdapter {
	
	private List<LogCatData> _datas;
	private LayoutInflater _layoutInflater;
	
	public LogCatAdapter(List<LogCatData> datas, Context cont) {
		_datas = datas;
		_layoutInflater = (LayoutInflater)cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return _datas.get(groupPosition);
	}
	
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		
		LogCatViewHolder vh = new LogCatViewHolder();
		View view = convertView;
		if (view == null) {
			view = _layoutInflater.inflate(R.layout.adapter_log_child, null);
			vh.txvStackTrace = (TextView)view.findViewById(R.id.txvStackTrace);
			view.setTag(vh);
		}
		else {
			vh = (LogCatViewHolder)view.getTag();
		}
		
		LogCatData data = _datas.get(groupPosition);
		vh.txvStackTrace.setText(data.getStackTrace());
		
		return view;
	}
	
	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}
	
	@Override
	public Object getGroup(int groupPosition) {
		return _datas.get(groupPosition);
	}
 
	@Override
	public int getGroupCount() {
		return _datas.size();
	}
 
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
 
	@Override
	public View getGroupView(int groupPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		LogCatViewHolder vh = new LogCatViewHolder();
		View view = convertView;
		
		if (view == null) {
			view = _layoutInflater.inflate(R.layout.adapter_log_parent, null);
			vh.txvDate = (TextView)view.findViewById(R.id.txvDate);
			vh.txvTitle = (TextView)view.findViewById(R.id.txvTitle);
			view.setTag(vh);
		}
		else {
			vh = (LogCatViewHolder)view.getTag();
		}
		
		LogCatData data = _datas.get(groupPosition);
		vh.txvDate.setText(data.getDate());
		vh.txvTitle.setText(data.getTitle());
		
		return view;
	}
 
	@Override
	public boolean hasStableIds() {
		return true;
	}
 
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
	
}