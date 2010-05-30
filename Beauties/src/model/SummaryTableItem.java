package model;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Color;

public abstract class SummaryTableItem {
	private final String mName;
	private final int mValue;

	SummaryTableItem(String pName, int pValue) {
		mName = pName;
		mValue = pValue;
	}
	
	abstract public Color getEntryColor();
	
	abstract public ViewerFilter getRecordTableItemFilter();
	
	public String getName() {
		return mName;
	}
	public int getValue() {
		return mValue;
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

	@Override
	public Color getEntryColor() {
		return SystemData.getColorGray();
	}

	@Override
	public ViewerFilter getRecordTableItemFilter() {
		return RecordFilterPool.getItem(mItemId);
	}
	
}
