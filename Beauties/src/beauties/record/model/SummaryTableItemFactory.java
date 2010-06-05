package beauties.record.model;


public class SummaryTableItemFactory {

	public static SummaryTableItem createOriginal(String pName, int pValue) {
		return new SummaryTableItemOriginal(pName, pValue);
	}
	
	public static SummaryTableItem createAppearedProfit(String pName, int pValue) {
		return new SummaryTableItemAppearedProfit(pName, pValue);
	}

	public static SummaryTableItem createAppearedIncome(String pName, int pValue) {
		return new SummaryTableItemAppearedIncome(pName, pValue);
	}

	public static SummaryTableItem createAppearedExpense(String pName, int pValue) {
		return new SummaryTableItemAppearedExpense(pName, pValue);
	}
	
	public static SummaryTableItem createCategory(String pName, int pValue, int pCategoryId) {
		return new SummaryTableItemCategory(pName, pValue, pCategoryId);
	}
	
	public static SummaryTableItem createItem(String pName, int pValue, int pItemId) {
		return new SummaryTableItemNormalItem(pName, pValue, pItemId);
	}
	
}
