package model;

public class IncomeExpense {

	private final int mIncome;
	private final int mExpense;
	
	public IncomeExpense(int pIncome, int pExpense) {
		this.mIncome = pIncome;
		this.mExpense = pExpense;
	}
	
	public int getIncome() {
		return mIncome;
	}
	
	public int getExpense() {
		return mExpense;
	}
	
	public int getProfit() {
		return mIncome - mExpense;
	}
	
}
