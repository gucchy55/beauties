package beauties.common.model;

public enum IncomeExpenseType {
	
	INCOME(1),
	EXPENCE(2);
	
	private int mCategoryRexpDiv;
	
	private IncomeExpenseType(int pRexpDiv) {
		mCategoryRexpDiv = pRexpDiv;
	}
	
	public int getCategoryRexp() {
		return mCategoryRexpDiv;
	}
}
