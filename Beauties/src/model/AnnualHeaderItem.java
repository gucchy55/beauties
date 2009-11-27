package model;

public class AnnualHeaderItem {
	private int mCategoryId;
	private int mItemId;
	private String mName;
	boolean mIsIncome = false;
	
	public AnnualHeaderItem(int pCategoryId, String pCategoryName, boolean pIsIncome) {
		mCategoryId = pCategoryId;
		mItemId = SystemData.getUndefinedInt();
		mName = pCategoryName;
		mIsIncome = pIsIncome;
	}
	
	public AnnualHeaderItem(int pCategoryId, int pItemId, String pItemName, boolean pIsIncome) {
		mCategoryId = pCategoryId;
		mItemId= pItemId;
		mName = pItemName;
		mIsIncome = pIsIncome;
	}
	
	public AnnualHeaderItem(String pName) {
		mCategoryId = SystemData.getUndefinedInt();
		mItemId = SystemData.getUndefinedInt();
		mName = pName;
	}

	public int getCategoryId() {
		return mCategoryId;
	}

	public int getItemId() {
		return mItemId;
	}

	public String getName() {
		return mName;
	}
	
	public boolean isIncome() {
		return mIsIncome;
	}

	public boolean isCategory() {
		return ((mCategoryId != SystemData.getUndefinedInt()) && (mItemId == SystemData.getUndefinedInt()));
	}
	
	public boolean isItem() {
		return (mItemId != SystemData.getUndefinedInt());
	}
	
	public boolean isSpecialHeader() {
		return ((mCategoryId == SystemData.getUndefinedInt()) && (mItemId == SystemData.getUndefinedInt()));
	}
	
}
