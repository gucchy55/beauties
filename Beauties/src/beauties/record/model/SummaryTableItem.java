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
	
	private final int mCategoryId;

	SummaryTableItemCategory(String pName, int pValue, int pCategoryId) {
		super(pName, pValue);
		mCategoryId = pCategoryId;
	}
	
	SummaryTableItemCategory(Category pCategory, int pValue) {
		super(pCategory, pValue);
		mCategoryId = pCategory.getId();
	}

	@Override
	public Color getEntryColor() {
		return SystemData.getColorYellow();
	}

	@Override
	public ViewerFilter getRecordTableItemFilter() {
		return RecordFilterPool.getCategory(mCategoryId);
	}
	
}

class SummaryTableItemNormalItem extends SummaryTableItem {
	
	private final int mItemId;

	SummaryTableItemNormalItem(String pName, int pValue, int pItemId) {
		super("  " + pName, pValue);
		mItemId = pItemId;
	}
	public SummaryTableItemNormalItem(Item pItem, int pValue) {
		super(pItem, pValue);
		mItemId = pItem.getId();
	}

	@Override
	public Color getEntryColor() {
		return SystemData.getColorGray();
	}

	@Override
	public ViewerFilter getRecordTableItemFilter() {
		return RecordFilterPool.getItem(mItemId);
	}
	
}
