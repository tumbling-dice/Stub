public class TwitterList implements Serializable {
	
	private Long id;
	private String name;
	private Long ownerId;
	private String ownerScreenName;
	private String ownerIconUrl;
	private String description;
	private boolean isProtected;
	private Integer memberCount;
	private Integer subscriberCount;
	
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getOwnerId() {
		return this.ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public String getOwnerScreenName() {
		return this.ownerScreenName;
	}
	public void setOwnerScreenName(String ownerScreenName) {
		this.ownerScreenName = ownerScreenName;
	}
	public String getOwnerIconUrl() {
		return this.ownerIconUrl;
	}
	public void setOwnerIconUrl(String ownerIconUrl) {
		this.ownerIconUrl = ownerIconUrl;
	}
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isProtected() {
		return this.isProtected;
	}
	public void setProtected(boolean isProtected) {
		this.isProtected = isProtected;
	}
	public Integer getMemberCount() {
		return this.memberCount;
	}
	public void setMemberCount(Integer memberCount) {
		this.memberCount = memberCount;
	}
	public Integer getSubscriberCount() {
		return this.subscriberCount;
	}
	public void setSubscriberCount(Integer subscriberCount) {
		this.subscriberCount = subscriberCount;
	}
	public boolean isMyList() {
		return this.isMyList;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof TwitterList)) return false;
		if(obj == this) return true;
		
		return ((TwitterList) obj).getId().equals(this.id);
	}
	
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
}