package beauties.common.model;

public class ItemCategory {
	private int mId;
	private String mName;
	
	public ItemCategory(int pId, String pName) {
		mId = pId;
		mName = pName;
	}
	
	public int getId() {
		return mId;
	}
	
	public String getName() {
		return mName;
	}
	
}
