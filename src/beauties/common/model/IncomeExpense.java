package beauties.common.model;

public class IncomeExpense {

	private final long mIncome;
	private final long mExpense;
	
	public IncomeExpense(long pIncome, long pExpense) {
		this.mIncome = pIncome;
		this.mExpense = pExpense;
	}
	
	public long getIncome() {
		return mIncome;
	}
	
	public long getExpense() {
		return mExpense;
	}
	
	public long getProfit() {
		return mIncome - mExpense;
	}
	
}
