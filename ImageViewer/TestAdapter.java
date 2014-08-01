public class TestAdapter extends ArrayAdapter<TimelineData> {
	
	static class ViewHolder {
		UrlImageView imgIcon;
		UrlImageView imgRtIcon;
		
		public ViewHolder(View v, LayoutInflater inflater) {
			v = inflater.inflate(R.layout.test);
			imgIcon = (UrlImageView) v.findViewById(R.id.imgIcon);
			imgRtIcon = (UrlImageView) v.findViewById(R.id.imgRtIcon);
		}
		
	}
	
	private LayoutInflater _layoutInflater;
	
	public TestAdapter(Context context, List<TimelineData> datas) {
		super(context, 0, datas);
		_layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		
		View v = convertView;
		ViewHolder vh = null;
		
		if(v == null) {
			vh = new ViewHolder(v, _layoutInflater);
			v.setTag(vh);
		} else {
			vh = (ViewHolder) v.getTag();
		}
		
		TimelineData data = getItem(position);
		
		ImageCallback imgCallback = new ImageCallback() {
			@Override
			public void call(Object key, Bitmap bitmap) {
				if(bitmap == null) return;
				
				View target = parent.findViewWithTag(key);
				if(target != null && target instanceof UrlImageView) {
					((UrlImageView) target).setImageBitmap(bitmap);
				}
			}
		};
		
		vh.imgIcon.setUrlImageQue(data.getIcon(), data.getId(), false, true, imgCallback);
		vh.imgRtIcon.setUrlImageQue(data.getRetweeter().getIcon(), String.format("RT:%d", data.getId()), false, false, imgCallback);
		
		return v;
	}
	
}