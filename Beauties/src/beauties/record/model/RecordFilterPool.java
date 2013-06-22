package beauties.record.model;


import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import beauties.common.model.Category;
import beauties.common.model.Item;

public class RecordFilterPool {
	private static RecordFilterDefault mRecordFilterDefault;
	private static RecordFilterAllIncome mRecordFilterAllIncome;
	private static RecordFilterAllExpense mRecordFilterAllExpense;
	private static RecordFilterCategory mRecordFilterCategory;
	private static RecordFilterItem mRecordFilterItem;
	
	
	public static ViewerFilter getDefault() {
		if (mRecordFilterDefault == null)
			mRecordFilterDefault = new RecordFilterDefault();
		return mRecordFilterDefault;
	}
	
	public static ViewerFilter getAllIncome() {
		if (mRecordFilterAllIncome == null)
			mRecordFilterAllIncome = new RecordFilterAllIncome();
		return mRecordFilterAllIncome;
	}
	
	public static ViewerFilter getAllExpense() {
		if (mRecordFilterAllExpense == null)
			mRecordFilterAllExpense = new RecordFilterAllExpense();
		return mRecordFilterAllExpense;
	}
	
	public static ViewerFilter getCategory(Category pCategory) {
		if (mRecordFilterCategory == null)
			mRecordFilterCategory = new RecordFilterCategory();
		mRecordFilterCategory.setCategory(pCategory);
		return mRecordFilterCategory;
	}
	
	public static ViewerFilter getItem(Item pItem) {
		if (mRecordFilterItem == null)
			mRecordFilterItem = new RecordFilterItem();
		mRecordFilterItem.setItem(pItem);
		return mRecordFilterItem;
	}

}

class RecordFilterDefault extends ViewerFilter {
	@Override
	public boolean select(Viewer arg0, Object arg1, Object arg2) {
		return true;
	}
}

class RecordFilterAllIncome extends ViewerFilter {
	@Override
	public boolean select(Viewer arg0, Object arg1, Object arg2) {
		RecordTableItem wItem = (RecordTableItem) arg2;
		return wItem.isIncome();
	}
}

class RecordFilterAllExpense extends ViewerFilter {
	@Override
	public boolean select(Viewer arg0, Object arg1, Object arg2) {
		RecordTableItem wItem = (RecordTableItem) arg2;
		return wItem.isExpense();
	}
}

class RecordFilterCategory extends ViewerFilter {
	private Category mCategory;

	void setCategory(Category pCategory) {
		mCategory = pCategory;
	}

	@Override
	public boolean select(Viewer arg0, Object arg1, Object arg2) {
		RecordTableItem wItem = (RecordTableItem) arg2;
		if (wItem.getCategory() == null) {
			return false;
		}
		return wItem.getCategory().equals(mCategory);
	}
}

class RecordFilterItem extends ViewerFilter {
	private Item mItem;

	void setItem(Item pItem) {
		mItem = pItem;
	}

	@Override
	public boolean select(Viewer arg0, Object arg1, Object arg2) {
		RecordTableItem wItem = (RecordTableItem) arg2;
		return wItem.getItem().equals(mItem);
	}
}
