public final class TwitterListViewHolder {
	
	public TwitterListViewHolder(View v) {
		this.txvName = (TextView) v.findViewById(R.id.txvName);
		this.txvOwnerScreenName = (TextView) v.findViewById(R.id.txvOwnerScreenName);
		this.imgIcon = (UrlImageView) v.findViewById(R.id.imgIcon);
		this.txvDescription = (TextView) v.findViewById(R.id.txvDescription);
		this.imgProtected = (ImageView) v.findViewById(R.id.imgProtected);
		this.txvMemberCount = (TextView) v.findViewById(R.id.txvMemberCount);
		this.txvSubscriberCount = (TextView) v.findViewById(R.id.txvSubscriberCount);
	}
	
	public TextView txvName;
	public TextView txvOwnerScreenName;
	public UrlImageView imgIcon;
	public TextView txvDescription;
	public ImageView imgProtected;
	public TextView txvMemberCount;
	public TextView txvSubscriberCount;
}