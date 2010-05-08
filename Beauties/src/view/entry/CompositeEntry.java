package view.entry;

import java.util.Date;

import model.RecordTableItem;
import model.SystemData;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import util.Util;
import view.util.MyGridData;
import view.util.MyGridLayout;

public class CompositeEntry extends Composite {

	private int mBookId;
	private Date mStartDate = null;
	private Date mEndDate = null;

	private boolean isMonthPeriod = true;
	private int mItemId;
	private int mCategoryId;
	private boolean mAllIncome = false;
	private boolean mAllExpense = false;
	private CompositeRecordTable mCompositeRecordTable;

	public CompositeEntry(Composite pParent) {
		super(pParent, SWT.NONE);
		mBookId = SystemData.getBookMap(false).keySet().iterator().next();
		init();
	}

	private void init() {
		if (mStartDate == null) {
			Date[] wDates = Util.getPeriod(new Date());
			mStartDate = wDates[0];
			mEndDate = wDates[1];
		}

		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());

		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData());

		new CompositeBookTab(this);
		new CompositeActionTab(this);
		mCompositeRecordTable = new CompositeRecordTable(this);
		new CompositeSummaryTable(this);
	}

	public void updateView() {
		for (Control wCtrl : this.getChildren()) {
			wCtrl.dispose();
		}

		this.init();

		this.layout();

	}

//	public int getSelectedActId() {
//		return mCompositeRecordTable.getSelectedActId();
//	}
	
	public RecordTableItem getSelectedRecordItem() {
		return mCompositeRecordTable.getSelectedRecordItem();
	}
	
	public boolean hasSelectedRecordTableItem() {
		return mCompositeRecordTable.hasSelectedItem();
	}

	public void addFiltersToRecord() {
		mCompositeRecordTable.addFilter();
	}

	public void removeFiltersFromRecord() {
		mCompositeRecordTable.removeFilter();
	}

	public int getBookId() {
		return mBookId;
	}

	public Date getStartDate() {
		return mStartDate;
	}

	public Date getEndDate() {
		return mEndDate;
	}

	public boolean isMonthPeriod() {
		return isMonthPeriod;
	}

	public int getItemId() {
		return mItemId;
	}

	public int getCategoryId() {
		return mCategoryId;
	}

	public boolean isAllIncome() {
		return mAllIncome;
	}

	public boolean isAllExpense() {
		return mAllExpense;
	}

	public void setBookId(int pBookId) {
		mBookId = pBookId;
	}

	public void setStartDate(Date pStartDate) {
		mStartDate = pStartDate;
	}

	public void setEndDate(Date pEndDate) {
		mEndDate = pEndDate;
	}

	public void setMonthPeriod(boolean pMonthPeriod) {
		isMonthPeriod = pMonthPeriod;
	}

	public void setItemId(int pItemId) {
		mItemId = pItemId;
	}

	public void setCategoryId(int pCategoryId) {
		mCategoryId = pCategoryId;
	}

	public void setAllIncome(boolean pAllIncome) {
		mAllIncome = pAllIncome;
	}

	public void setAllExpense(boolean pAllExpense) {
		mAllExpense = pAllExpense;
	}
}
