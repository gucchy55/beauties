package beauties.record.model;


import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Color;

import beauties.common.lib.SystemData;
import beauties.common.model.Category;
import beauties.common.model.Item;
import beauties.common.model.RightType;


public abstract class SummaryTableItem {
	private final String mName;
	private final long mValue;
	private Category mCategory;
	private Item mItem;

	SummaryTableItem(String pName, long pValue) {
		mName = pName;
		mValue = pValue;
	}
	
	SummaryTableItem(Category pCategory, long pValue) {
		mName = pCategory.getName();
		mValue = pValue;
		mCategory = pCategory;
	}
	SummaryTableItem(Item pItem, long pValue) {
		mName = "  " + pItem.getName();
		mValue = pValue;
		mItem = pItem;
	}
	
	abstract public Color getEntryColor();
	
	abstract public ViewerFilter getRecordTableItemFilter();
	
	public String getName() {
		return mName;
	}
	public long getValue() {
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

	SummaryTableItemOriginal(String pName, long pValue) {
		super(pName, pValue);
	}

	@Override
	public	Color getEntryColor() {
		if (SystemData.getRightType() == RightType.Main) {
			return SystemData.getColorBlue();
		}
		return null;
	}

	@Override
	public	ViewerFilter getRecordTableItemFilter() {
		return null;
	}
	
}

class SummaryTableItemAppearedProfit extends SummaryTableItem {

	SummaryTableItemAppearedProfit(String pName, long pValue) {
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

	SummaryTableItemAppearedIncome(String pName, long pValue) {
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


	SummaryTableItemAppearedExpense(String pName, long pValue) {
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
//	SummaryTableItemCategory(String pName, long pValue, Category pCategory) {
//		super(pName, pValue);
//		mCategory = pCategory;
//	}
	
	SummaryTableItemCategory(Category pCategory, long pValue) {
		super(pCategory, pValue);
		mCategory = pCategory;
	}

	@Override
	public Color getEntryColor() {
		if (SystemData.getRightType() == RightType.Main) {
			return SystemData.getColorYellow();
		}
		return null;
	}

	@Override
	public ViewerFilter getRecordTableItemFilter() {
		return RecordFilterPool.getCategory(mCategory);
	}
	
}

class SummaryTableItemNormalItem extends SummaryTableItem {
	
	private final Item mItem;

//	@Deprecated
//	SummaryTableItemNormalItem(String pName, long pValue, Item pItem) {
//		super("  " + pName, pValue);
//		mItem = pItem;
//	}
	public SummaryTableItemNormalItem(Item pItem, long pValue) {
		super(pItem, pValue);
		mItem = pItem;
	}

	@Override
	public Color getEntryColor() {
//		return SystemData.getColorGray();
		return null;
	}

	@Override
	public ViewerFilter getRecordTableItemFilter() {
		return RecordFilterPool.getItem(mItem);
	}
	
}
