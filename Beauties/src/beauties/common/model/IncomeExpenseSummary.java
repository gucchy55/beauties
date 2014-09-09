package beauties.common.model;

public class IncomeExpenseSummary {
	private final IncomeExpense mAppearedIncomeExpense;
	private final IncomeExpense mSpecialIncomeExpense;
	private final IncomeExpense mTempIncomeExpense;

	public IncomeExpenseSummary(IncomeExpense pAppearedIncomeExpense,
			IncomeExpense pSpecialIncomeExpense, IncomeExpense pTempIncomeExpense) {
		mAppearedIncomeExpense = pAppearedIncomeExpense;
		mSpecialIncomeExpense = pSpecialIncomeExpense;
		mTempIncomeExpense = pTempIncomeExpense;
	}

	public IncomeExpense getAppearedIncomeExpense() {
		return mAppearedIncomeExpense;
	}

	public IncomeExpense getSpecialIncomeExpense() {
		return mSpecialIncomeExpense;
	}

	public IncomeExpense getTempIncomeExpense() {
		return mTempIncomeExpense;
	}

	public long getAppearedIncome() {
		return mAppearedIncomeExpense.getIncome();
	}
	
	public long getAppearedExpense() {
		return mAppearedIncomeExpense.getExpense();
	}
	
	public long getAppearedProfit() {
		return mAppearedIncomeExpense.getProfit();
	}
	
	public long getSpecialIncome() {
		return mSpecialIncomeExpense.getIncome();
	}
	
	public long getSpecialExpense() {
		return mSpecialIncomeExpense.getExpense();
	}
	
	public long getSpecialProfit() {
		return mSpecialIncomeExpense.getProfit();
	}
	
	public long getTempIncome() {
		return mTempIncomeExpense.getIncome();
	}
	
	public long getTempExpense() {
		return mTempIncomeExpense.getExpense();
	}
	
	public long getTempProfit() {
		return mTempIncomeExpense.getProfit();
	}
	
	public long getActualProfit() {
		return getAppearedProfit() - getTempProfit();
	}
	
	public long getOperationProfit() {
		return getAppearedProfit() - getTempProfit() - getSpecialProfit();
	}
	
	public long getOperationIncome() {
		return getAppearedIncome() - getSpecialIncome() - getTempIncome();
	}
	
	public long getOperationExpense() {
		return getAppearedExpense() - getSpecialExpense() - getTempExpense();
	}
	
	public long getActualIncome() {
		return getAppearedIncome()	- getTempIncome();
	}
	
	public long getActualExpense() {
		return getAppearedExpense() - getTempExpense();
	}

}
