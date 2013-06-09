package beauties.common.model;

public enum IncomeExpenseType {
	
	INCOME(1, "収入"),
	EXPENCE(2, "支出");
	
	private int mCategoryRexpDiv;
	private String mName;
	
	private IncomeExpenseType(int pRexpDiv, String pName) {
		mCategoryRexpDiv = pRexpDiv;
		mName = pName;
	}
	
	public int getCategoryRexp() {
		return mCategoryRexpDiv;
	}
	
	public String getName() {
		return mName;
	}
}
