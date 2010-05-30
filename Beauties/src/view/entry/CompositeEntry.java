package view.entry;

import java.util.Date;

import model.DateRange;
import model.RecordTableItem;
import model.SystemData;
import model.db.DbUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import util.Util;
import view.util.MyGridData;
import view.util.MyGridLayout;

public class CompositeEntry extends Composite {

	private int mBookId;
	private DateRange mDateRange = null;

	private boolean isMonthPeriod = true;
	private boolean isSearchResult = false;
	
	private CompositeBookTab mCompositeBookTab;
	private CompositeRecordTable mCompositeRecordTable;
	private CompositeSummaryTable mCompositeSummaryTable;
	private CompositeActionTab mCompositeActionTab;

	public CompositeEntry(Composite pParent) {
		super(pParent, SWT.NONE);
		mBookId = SystemData.getBookMap(false).keySet().iterator().next();
		init();
	}

	private void init() {
		if (mDateRange == null)
			mDateRange = Util.getMonthDateRange(new Date(), DbUtil.getCutOff());

		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData());

		mCompositeBookTab = new CompositeBookTab(this);
		mCompositeActionTab = new CompositeActionTab(this);
		mCompositeRecordTable = new CompositeRecordTable(this);
		mCompositeSummaryTable = new CompositeSummaryTable(this);
	}

	public void updateView() {
		for (Control wCtrl : this.getChildren()) {
			wCtrl.dispose();
		}

		this.init();

		this.layout();

	}
	
	void updateForSearch(RecordTableItem[][] pRecordTableItems) {
		mCompositeActionTab.updateForSearch();
		mCompositeRecordTable.updateForSearch(pRecordTableItems);
	}

	boolean openSearchDialog() {
		this.getShell().setImeInputMode(SWT.NATIVE);
		InputDialog wInputDialog = new InputDialog(getShell(), "検索", "キーワードを入力", "", null);
		if (wInputDialog.open() != Dialog.OK) 
			return false;
		mCompositeBookTab.setVisible(false);
		mCompositeSummaryTable.setVisible(false);
		this.isSearchResult = true;
		this.updateForSearch(DbUtil.getSearchedRecordTableItemList(wInputDialog.getValue()));
		return true;
	}
	
	public RecordTableItem getSelectedRecordItem() {
		return mCompositeRecordTable.getSelectedRecordItem();
	}
	
	public boolean hasSelectedRecordTableItem() {
		return mCompositeRecordTable.hasSelectedItem();
	}
	
	public void updateRecordFilter(ViewerFilter pFilter) {
		mCompositeRecordTable.updateRecordFilter(pFilter);
	}

	public int getBookId() {
		return mBookId;
	}

	public Date getStartDate() {
		return mDateRange.getStartDate();
	}

	public Date getEndDate() {
		return mDateRange.getEndDate();
	}
	
	public DateRange getDateRange() {
		return mDateRange;
	}

	public boolean isMonthPeriod() {
		return isMonthPeriod;
	}

	public void setBookId(int pBookId) {
		mBookId = pBookId;
	}

	public void setDateRange(DateRange pDateRange) {
		mDateRange = pDateRange;
	}

	public void setMonthPeriod(boolean pMonthPeriod) {
		isMonthPeriod = pMonthPeriod;
	}
	
	void setIsSearchResult(boolean pIsSearchResult) {
		this.isSearchResult = pIsSearchResult;
	}
	
	boolean isSearchResult() {
		return this.isSearchResult;
	}
	
	boolean showBookColumn() {
		return this.getBookId() == SystemData.getAllBookInt() || this.isSearchResult;
	}
	
	boolean showYear() {
		return !this.isMonthPeriod || this.isSearchResult;
	}
}
