package beauties.record.model;


import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

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
	
	public static ViewerFilter getCategory(int pCategoryId) {
		if (mRecordFilterCategory == null)
			mRecordFilterCategory = new RecordFilterCategory();
		mRecordFilterCategory.setCategoryId(pCategoryId);
		return mRecordFilterCategory;
	}
	
	public static ViewerFilter getItem(int pItemId) {
		if (mRecordFilterItem == null)
			mRecordFilterItem = new RecordFilterItem();
		mRecordFilterItem.setItemId(pItemId);
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
	private int mCategoryId;

	void setCategoryId(int pCategoryId) {
		mCategoryId = pCategoryId;
	}

	@Override
	public boolean select(Viewer arg0, Object arg1, Object arg2) {
		RecordTableItem wItem = (RecordTableItem) arg2;
		return wItem.getCategoryId() == mCategoryId;
	}
}

class RecordFilterItem extends ViewerFilter {
	private int mItemId;

	void setItemId(int pItemId) {
		mItemId = pItemId;
	}

	@Override
	public boolean select(Viewer arg0, Object arg1, Object arg2) {
		RecordTableItem wItem = (RecordTableItem) arg2;
		return wItem.getItem().getId() == mItemId;
	}
}
