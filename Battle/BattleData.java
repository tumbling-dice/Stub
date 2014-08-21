public class BattleData implements Serializable {
	
	private String name;
	private String screenName;
	private long userId;
	private int tweetCount;
	private int followCount;
	private int followerCount;
	private int favoriteCount;
	private int blockCount;
	private int muteCount;
	private int level;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getScreenName() {
		return this.screenName();
	}
	
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	
	public long getUserId() {
		return this.userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public void setTweetCount(int tweetCount) {
		this.tweetCount = tweetCount;
	}
	
	public void setFollowCount(int followCount) {
		this.followCount = followCount;
	}
	
	public void setFollowerCount(int followerCount) {
		this.followerCount = followerCount;
	}
	
	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}
	
	public void setBlockCount(int blockCount) {
		this.blockCount = blockCount;
	}
	
	public void setMuteCount(int muteCount) {
		this.muteCount = muteCount;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getStrength() {
		
		int strength = 0;
		
		strength += tweetCount > 0 ? (int) (tweetCount / 10) : 0;
		strength += followCount + ((int) (followerCount * 1.5));
		strength += favoriteCount;
		strength += blockCount;
		strength += muteCount;
		
		return getLevelBonus(strength, level);
	}
	
	public int getOffense() {
		
		int offensive = 0;
		
		offensive += tweetCount > 0 ? (int) (tweetCount / 10000) : 0;
		offensive += followerCount;
		offensive += followCount > 0 ? (int) (followCount / 10) : 0;
		offensive += blockCount * 2;
		offensive += (int) muteCount * 1.5;
		offensive += favoriteCount;
		
		return getLevelBonus(offensive, level);
		
	}
	
	public int getDefense() {
		
		int defensive = 0;
		
		defensive += tweetCount > 0 ? (int) (tweetCount / 10000) : 0;
		defensive += followCount;
		defensive += followerCount > 0 ? (int) (followerCount / 10) : 0;
		defensive += (int) blockCount * 1.5;
		defensive += muteCount * 2;
		defensive += favoriteCount;
		
		return getLevelBonus(defensive, level);
	}
	
	private static int getLevelBonus(int base, int level) {
		return base * (1 + (level * 0.01));
	}
	
}