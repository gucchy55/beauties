package beauties.record.model;

import beauties.common.model.Category;
import beauties.common.model.Item;


public class SummaryTableItemFactory {

	public static SummaryTableItem createOriginal(String pName, long pValue) {
		return new SummaryTableItemOriginal(pName, pValue);
	}
	
	public static SummaryTableItem createAppearedProfit(String pName, long pValue) {
		return new SummaryTableItemAppearedProfit(pName, pValue);
	}

	public static SummaryTableItem createAppearedIncome(String pName, long pValue) {
		return new SummaryTableItemAppearedIncome(pName, pValue);
	}
	
	public static SummaryTableItem createAppearedExpense(String pName, long pValue) {
		return new SummaryTableItemAppearedExpense(pName, pValue);
	}
	
	public static SummaryTableItem createCategory(Category pCategory, long pValue) {
		return new SummaryTableItemCategory(pCategory, pValue);
	}
	
	public static SummaryTableItem createItem(Item pItem, long pValue) {
		return new SummaryTableItemNormalItem(pItem, pValue);
	}
	
//	@Deprecated
//	public static SummaryTableItem createCategory(String pName, long pValue, Category pCategory) {
//		return new SummaryTableItemCategory(pName, pValue, pCategory);
//	}
//	
//	@Deprecated
//	public static SummaryTableItem createItem(String pName, long pValue, Item pItem) {
//		return new SummaryTableItemNormalItem(pName, pValue, pItem);
//	}
	
}
