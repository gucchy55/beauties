package beauties.record.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import beauties.common.model.Category;
import beauties.common.model.IncomeExpenseType;

public class SummaryTableItemsNormal {
	
	private SummaryTableItem mAppearedProfit;
	private SummaryTableItem mAppearedIncome;
	private SummaryTableItem mAppearedExpense;
	private List<SummaryTableItem> mItems;
	private Map<Category, List<SummaryTableItem>> mCategoryItemMap;
	
	List<SummaryTableItem> mSummaryTableItems;
	
	public SummaryTableItemsNormal(int pAppearedIncome, int pAppearedExpense, List<SummaryTableItem> pItems) {
		mAppearedProfit = SummaryTableItemFactory.createAppearedProfit("みかけ収支", pAppearedIncome - pAppearedExpense); 
		mAppearedIncome = SummaryTableItemFactory.createAppearedIncome("みかけ収入", pAppearedIncome);
		mAppearedExpense = SummaryTableItemFactory.createAppearedExpense("みかけ支出", pAppearedExpense);
		mItems = pItems;
	}
	
	public void setCategoryItems(List<SummaryTableItem> pCategoryItems) {
		mCategoryItemMap = new LinkedHashMap<>();
		for (SummaryTableItem wCategoryItem : pCategoryItems) {
			mCategoryItemMap.put(wCategoryItem.getCategory(), new ArrayList<SummaryTableItem>());
			mCategoryItemMap.get(wCategoryItem.getCategory()).add(wCategoryItem);
		}
		for (SummaryTableItem wItem : mItems) {
			mCategoryItemMap.get(wItem.getCategory()).add(wItem);
		}
		generateList();
		mItems.clear();
		mCategoryItemMap.clear();
	}
	
	public List<SummaryTableItem> getItems() {
		return mSummaryTableItems;
	}
	
	private void generateList() {
		mSummaryTableItems = new ArrayList<>();
		mSummaryTableItems.add(mAppearedProfit);
		mSummaryTableItems.add(mAppearedIncome);
		addIncome();
		mSummaryTableItems.add(mAppearedExpense);
		addExpense();
	}
	
	private void addIncome() {
		for (Map.Entry<Category, List<SummaryTableItem>> wEntry : mCategoryItemMap.entrySet()) {
			if (wEntry.getKey().getIncomeExpenseType() == IncomeExpenseType.EXPENCE) {
				return;
			}
			mSummaryTableItems.addAll(wEntry.getValue());
		}
	}
	private void addExpense() {
		for (Map.Entry<Category, List<SummaryTableItem>> wEntry : mCategoryItemMap.entrySet()) {
			if (wEntry.getKey().getIncomeExpenseType() == IncomeExpenseType.INCOME) {
				continue;
			}
			mSummaryTableItems.addAll(wEntry.getValue());
		}
	}
}
