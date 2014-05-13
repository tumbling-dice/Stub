public class TwitterListAdapter extends ArrayAdapter<TwitterList> {
	
	private LayoutInflater _layoutInflater;
	
	public TwitterListAdapter(List<TwitterList> list, Context context) {
		super(list, 0, context);
		_layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		TwitterListViewHolder vh = null;
		
		if(v == null) {
			vh = new TwitterListViewHolder(v);
			v.setTag(vh);
		} else {
			vh = (TwitterListViewHolder) v.getTag();
		}
		
		TwitterList item = getItem(position);
		
		vh.txvName.setText(item.getName());
		vh.txvOwnerScreenName.setText(item.getOwnerScreenName());
		vh.imgIcon.setUrl(item.getOwnerIconUrl());
		vh.txvDescription.setText(item.getDescription());
		vh.imgProtected.setVisibility((item.isProtected() ? View.VISIBLE : View.GONE));
		vh.txvMemberCount.setText(item.getMemberCount().toString());
		vh.txvSubscriberCount.setText(item.getSubscriberCount().toString());
		
		return v;
	}
	
}