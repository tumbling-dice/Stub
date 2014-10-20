public class AccountAdapter extends ArrayAdapter<TwitterAccount> {
	
	static class ViewHolder {
		CheckedTextView name;
		
		ViewHolder(View v) {
			name = (CheckedTextView) v.findViewById(android.R.id.text1);
		}
	}
	
	private LayoutInflater _inflater;
	
	public AccountAdapter(Context context, List<TwitterAccount> list) {
		super(context, 0, list);
		_inflater = LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder vh = null;
		
		if(view == null) {
			view = _inflater.inflate(R.layout.adapter_account_list);
			vh = new ViewHolder(view);
			view.setTag(vh);
		} else {
			vh = (ViewHolder) view.getTag();
		}
		
		val item = getItem(position);
		
		vh.name.setText(item.getScreenName());
		vh.name.setChecked(item.isUse());
		
		return view;
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).getUserId();
	}
}