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

	public int getAppearedIncome() {
		return mAppearedIncomeExpense.getIncome();
	}
	
	public int getAppearedExpense() {
		return mAppearedIncomeExpense.getExpense();
	}
	
	public int getAppearedProfit() {
		return mAppearedIncomeExpense.getProfit();
	}
	
	public int getSpecialIncome() {
		return mSpecialIncomeExpense.getIncome();
	}
	
	public int getSpecialExpense() {
		return mSpecialIncomeExpense.getExpense();
	}
	
	public int getSpecialProfit() {
		return mSpecialIncomeExpense.getProfit();
	}
	
	public int getTempIncome() {
		return mTempIncomeExpense.getIncome();
	}
	
	public int getTempExpense() {
		return mTempIncomeExpense.getExpense();
	}
	
	public int getTempProfit() {
		return mTempIncomeExpense.getProfit();
	}
	
	public int getActualProfit() {
		return getAppearedProfit() - getTempProfit();
	}
	
	public int getOperationProfit() {
		return getAppearedProfit() - getTempProfit() - getSpecialProfit();
	}
	
	public int getOperationIncome() {
		return getAppearedIncome() - getSpecialIncome() - getTempIncome();
	}
	
	public int getOperationExpense() {
		return getAppearedExpense() - getSpecialExpense() - getTempExpense();
	}
	
	public int getActualIncome() {
		return getAppearedIncome()	- getTempIncome();
	}
	
	public int getActualExpense() {
		return getAppearedExpense() - getTempExpense();
	}

}
