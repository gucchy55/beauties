package beauties.common.model;

public class Item {
	private int mId;
	private String mName;
	private ItemCategory mCategory;
	
	public Item(int pId, String pName) {
		mId = pId;
		mName = pName;
	}
	
	public int getId() {
		return mId;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setCategory(ItemCategory pCategory) {
		mCategory = pCategory;
	}
	
	public ItemCategory getCategory() {
		return mCategory;
	}
	
}
