package model;

public class SummaryTableItem {
	private static final int mInitial = SystemData.getUndefinedInt();

	private int mItemId = mInitial;
	private String mName;
	private int mCategoryId = mInitial;
	private double mValue = mInitial;

	private boolean isCategory = false;
	private boolean isItem = false;

	private boolean isIncome = false;

	private boolean isSpecial = false; // 残高、営業収支等
	private boolean isAppearedSum = false; // みかけ収支
	private boolean isAppearedIncomeExpense = false; // みかけ収入・支出

	// for category
	public SummaryTableItem(int pCategoryId, String pCategoryName,
			double pValue, boolean pIsIncome) {
		this.mCategoryId = pCategoryId;
		this.mName = pCategoryName;
		this.mValue = pValue;
		this.isIncome = pIsIncome;
		this.isCategory = true;
	}
	
	// for item
	public SummaryTableItem(int pItemId, String pItemName, int pCategoryId,
			double pValue, boolean pIsIncome) {
		this.mItemId = pItemId;
		this.mName = pItemName;
		this.mCategoryId = pCategoryId;
		this.mValue = pValue;
		this.isIncome = pIsIncome;
		this.isItem = true;
	}

	// for special row
	public SummaryTableItem(String pItemName, double pValue) {
		this.mName = pItemName;
		this.mValue = pValue;
		this.isSpecial = true;
	}

	public int getItemId() {
		return mItemId;
	}

	public String getItemName() {
		return mName;
	}

	public int getCategoryId() {
		return mCategoryId;
	}

	public double getValue() {
		return mValue;
	}

	public boolean isSpecial() {
		return isSpecial;
	}

	public boolean isIncome() {
		return isIncome;
	}

	public boolean isAppearedSum() {
		return isAppearedSum;
	}

	public void setValue(double pValue) {
		mValue = pValue;
	}

	public boolean isCategory() {
		return isCategory;
	}

	public boolean isItem() {
		return isItem;
	}

	public boolean isAppearedIncomeExpense() {
		return isAppearedIncomeExpense;
	}

	public void setAppearedIncomeExpense(boolean isAppearedIncome) {
		this.isAppearedIncomeExpense = isAppearedIncome;
	}

	public void setAppearedSum(boolean isAppearedSum) {
		this.isAppearedSum = isAppearedSum;
	}
	
	public void setIncome(boolean isIncome) {
		this.isIncome = isIncome;
	}

}
