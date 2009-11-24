package model;

public class SummaryTableItem {
	private static final int mInitial = SystemData.getUndefinedInt();

	private int mItemId = mInitial;
	private String mItemName;
	private int mCategoryId = mInitial;
	private double mValue = mInitial;
	
	private boolean isIncome = false;
	private boolean isSpecialRow = false;	// 残高、営業収支等
	private boolean isAppearedSum = false;	// 

	public SummaryTableItem(int pItemId, String pItemName, int pCategoryId,
			double pValue, boolean pIsIncome) {
		this.mItemId = pItemId;
		this.mItemName = pItemName;
		this.mCategoryId = pCategoryId;
		this.mValue = pValue;
		this.isIncome = pIsIncome;
	}

	// for special row
	public SummaryTableItem(String pItemName, double pValue, boolean pIsSpecial, boolean pIsAppearedSum, boolean pIsIncome) {
		this.mItemName = pItemName;
		this.mValue = pValue;
		this.isSpecialRow = pIsSpecial;
		this.isAppearedSum = pIsAppearedSum;
		this.isIncome = pIsIncome;
	}

	public int getItemId() {
		return mItemId;
	}

	public String getItemName() {
		return mItemName;
	}

	public int getCategoryId() {
		return mCategoryId;
	}

	public double getValue() {
		return mValue;
	}

	public boolean isSpecialRow() {
		return isSpecialRow;
	}
	
	public boolean isIncome() {
		return isIncome;
	}
	
	public boolean isAppearedSum() {
		return isAppearedSum;
	}

}
