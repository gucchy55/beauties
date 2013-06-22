package beauties.record.model;


import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Color;

import beauties.common.lib.SystemData;
import beauties.common.model.Category;
import beauties.common.model.Item;


public abstract class SummaryTableItem {
	private final String mName;
	private final int mValue;
	private Category mCategory;
	private Item mItem;

	SummaryTableItem(String pName, int pValue) {
		mName = pName;
		mValue = pValue;
	}
	
	SummaryTableItem(Category pCategory, int pValue) {
		mName = pCategory.getName();
		mValue = pValue;
		mCategory = pCategory;
	}
	SummaryTableItem(Item pItem, int pValue) {
		mName = "  " + pItem.getName();
		mValue = pValue;
		mItem = pItem;
	}
	
	abstract public Color getEntryColor();
	
	abstract public ViewerFilter getRecordTableItemFilter();
	
	public String getName() {
		return mName;
	}
	public int getValue() {
		return mValue;
	}
	public boolean isCategory() {
		return mCategory != null;
	}
	public boolean isItem() {
		return mItem != null;
	}
	public Category getCategory() {
		if (mItem != null) {
			return mItem.getCategory();
		}
		return mCategory;
	}
	public Item getItem() {
		return mItem;
	}
}

class SummaryTableItemOriginal extends SummaryTableItem {

	SummaryTableItemOriginal(String pName, int pValue) {
		super(pName, pValue);
	}

	@Override
	public	Color getEntryColor() {
		return SystemData.getColorBlue();
	}

	@Override
	public	ViewerFilter getRecordTableItemFilter() {
		return null;
	}
	
}

class SummaryTableItemAppearedProfit extends SummaryTableItem {

	SummaryTableItemAppearedProfit(String pName, int pValue) {
		super(pName, pValue);
	}

	@Override
	public Color getEntryColor() {
		return SystemData.getColorRed();
	}

	@Override
	public ViewerFilter getRecordTableItemFilter() {
		return null;
	}
	
}

class SummaryTableItemAppearedIncome extends SummaryTableItem {

	SummaryTableItemAppearedIncome(String pName, int pValue) {
		super(pName, pValue);
	}
	
	@Override
	public Color getEntryColor() {
		return SystemData.getColorGreen();
	}

	@Override
	public ViewerFilter getRecordTableItemFilter() {
		return RecordFilterPool.getAllIncome();
	}
	
}

class SummaryTableItemAppearedExpense extends SummaryTableItem {


	SummaryTableItemAppearedExpense(String pName, int pValue) {
		super(pName, pValue);
	}
	
	@Override
	public Color getEntryColor() {
		return SystemData.getColorGreen();
	}

	@Override
	public ViewerFilter getRecordTableItemFilter() {
		return RecordFilterPool.getAllExpense();
	}
	
}

class SummaryTableItemCategory extends SummaryTableItem {
	
	private final Category mCategory;

//	@Deprecated
//	SummaryTableItemCategory(String pName, int pValue, Category pCategory) {
//		super(pName, pValue);
//		mCategory = pCategory;
//	}
	
	SummaryTableItemCategory(Category pCategory, int pValue) {
		super(pCategory, pValue);
		mCategory = pCategory;
	}

	@Override
	public Color getEntryColor() {
		return SystemData.getColorYellow();
	}

	@Override
	public ViewerFilter getRecordTableItemFilter() {
		return RecordFilterPool.getCategory(mCategory);
	}
	
}

class SummaryTableItemNormalItem extends SummaryTableItem {
	
	private final Item mItem;

//	@Deprecated
//	SummaryTableItemNormalItem(String pName, int pValue, Item pItem) {
//		super("  " + pName, pValue);
//		mItem = pItem;
//	}
	public SummaryTableItemNormalItem(Item pItem, int pValue) {
		super(pItem, pValue);
		mItem = pItem;
	}

	@Override
	public Color getEntryColor() {
		return SystemData.getColorGray();
	}

	@Override
	public ViewerFilter getRecordTableItemFilter() {
		return RecordFilterPool.getItem(mItem);
	}
	
}
