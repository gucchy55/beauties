package beauties.record;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import beauties.common.lib.DbUtil;
import beauties.common.lib.SystemData;
import beauties.common.lib.Util;
import beauties.common.model.Book;
import beauties.common.model.DateRange;
import beauties.common.view.IPeriodBookTabController;
import beauties.record.model.RecordTableItem;
import beauties.record.model.RecordTableItemCollection;
import beauties.record.model.SummaryTableItem;
import beauties.record.model.SummaryTableItemCollection;
import beauties.record.view.CompositeEntry;
import beauties.record.view.dialog.DialogPeriod;

public class RecordController implements IPeriodBookTabController {
	private Book mBook;
	private DateRange mDateRange;

	private boolean mMonthPeriod = true;
	private boolean mSearchResult = false;
	private boolean mHistoryResult = false;

	private CompositeEntry mCompositeEntry;

	private RecordTableItemCollection mRecordTableItems;
//	private RecordTableItem[] mRecordItemsUp;
//	private RecordTableItem[] mRecordItemsBottom;
	private SummaryTableItemCollection mSummaryTableItems;

	private static final DateFormat mDF_yyyymm = new SimpleDateFormat("yyyy/MM");

	public RecordController(CompositeEntry pCompositeEntry) {
		mCompositeEntry = pCompositeEntry;
		mBook = SystemData.getBooks(false).iterator().next();
		mDateRange = Util.getMonthDateRange(new Date(), SystemData.getCutOff());
		updateTableItems();
	}

	private void updateTableItems() {
		mRecordTableItems = DbUtil.getRecordTableItems(mDateRange, mBook);
//		RecordTableItem[][] wRecordTableItemAll = DbUtil.getRecordTableItems(mDateRange, mBook);
//		mRecordItemsUp = wRecordTableItemAll[0];
//		mRecordItemsBottom = wRecordTableItemAll[1];
		mSummaryTableItems = DbUtil.getSummaryTableItems(mBook, mDateRange);
	}

	private void updateTableItemsForBookChange() {
		mRecordTableItems = DbUtil.getRecordTableItems(mDateRange, mBook);
//		RecordTableItem[][] wRecordTableItemAll = DbUtil.getRecordTableItems(mDateRange, mBook);
//		mRecordItemsUp = wRecordTableItemAll[0];
//		mRecordItemsBottom = wRecordTableItemAll[1];
		mSummaryTableItems.setItemsNormal(DbUtil.getSummaryTableItemsNormal(mBook, mDateRange));
	}
	
	@Override
	public void updateTable() {
		updateTableItems();
		mCompositeEntry.updateView();
	}
	
	@Override
	public void changeBook() {
		updateTableItemsForBookChange();
		mCompositeEntry.updateView();
	}

	public void updateItemsForHistory(int pCnt) {
		mRecordTableItems  = DbUtil.getLatestRecordTableItems(pCnt);
		mSummaryTableItems = null;
	}

	public void updateItemsForSearch(String pQuery) {
//		RecordTableItem[][] wRecordTableItemAll = DbUtil.getSearchedRecordTableItemList(pQuery);
		mRecordTableItems  = DbUtil.getSearchedRecordTableItemList(pQuery);
//		mRecordItemsUp = wRecordTableItemAll[0];
//		mRecordItemsBottom = wRecordTableItemAll[1];
		mSummaryTableItems = null;
	}

//	public int getBookId() {
//		return mBookId;
//	}

	public DateRange getDateRange() {
		return mDateRange;
	}

	public boolean getSearchResult() {
		return mSearchResult;
	}

	public Collection<RecordTableItem> getRecordItemsUp() {
		return mRecordTableItems.getItemsPast();
	}

	public Collection<RecordTableItem> getRecordItemsBottom() {
		return mRecordTableItems.getItemsFuture();
	}

	public Collection<SummaryTableItem> getSummaryTableItems() {
		return mSummaryTableItems.getList();
	}

//	public void setBookId(int pBookId) {
//		this.mBookId = pBookId;
//	}

	public void setDateRange(DateRange pDateRange) {
		this.mDateRange = pDateRange;
	}

	public void setMonthPeriod(boolean pMonthPeriod) {
		this.mMonthPeriod = pMonthPeriod;
	}

	public void setSearchResult(boolean pSearchResult) {
		this.mSearchResult = pSearchResult;
		mCompositeEntry.updateViewForSearch(mSearchResult);
	}

	public void setHistoryResult(boolean pHistoryResult) {
		this.mHistoryResult = pHistoryResult;
		mCompositeEntry.updateViewForHistory(mHistoryResult);
	}

	public boolean showBookColumn() {
		return mBook.isAllBook() || getSearchResult();
	}

	public boolean showYear() {
		return !getMonthPeriod() || getSearchResult();
	}

	public Shell getShell() {
		return mCompositeEntry.getShell();
	}

	public RecordTableItem getSelectedRecordItem() {
		return mCompositeEntry.getSelectedRecordItem();
	}

	public boolean hasSelectedRecordTableItem() {
		return mCompositeEntry.hasSelectedRecordTableItem();
	}

	public boolean openSearchDialog() {
//		this.getShell().setImeInputMode(SWT.NATIVE);
		InputDialog wInputDialog = new InputDialog(getShell(), "検索", "キーワードを入力", "", null);
		if (wInputDialog.open() != Window.OK)
			return false;
		this.updateItemsForSearch(wInputDialog.getValue());
		this.setSearchResult(true);
		return true;
	}
	
	private IInputValidator generateNumValidator() {
		final IInputValidator wValidator = new IInputValidator() {
			public String isValid(String wNewText) {
				if (!Pattern.matches("[0-9]*", wNewText)) {
					return "半角数字を入力してください";
				}
				if ("".equals(wNewText)) {
					return "";
				}
				return null;
			}
		};
		return wValidator;
	}

	public boolean openHistoryDialog() {
		InputDialog wInputDialog = new InputDialog(getShell(), "履歴", "表示件数", "20", generateNumValidator());
		if (wInputDialog.open() != Window.OK)
			return false;

		this.updateItemsForHistory(Integer.parseInt(wInputDialog.getValue()));
		this.setHistoryResult(true);
		return true;
	}

	public boolean getMonthPeriod() {
		if (mMonthPeriod)
			return true;
		DateRange wMonthRange = Util.getMonthDateRange(mDateRange.getEndDate(), SystemData
				.getCutOff());
		mMonthPeriod = mDateRange.getStartDate().equals(wMonthRange.getStartDate())
				&& mDateRange.getEndDate().equals(wMonthRange.getEndDate());
		return mMonthPeriod;

	}

	@Override
	public Composite getComposite() {
		return mCompositeEntry;
	}

	@Override
	public String getPeriodLabelText() {
		if (getMonthPeriod())
			return mDF_yyyymm.format(mDateRange.getEndDate());
		return "期間指定";
	}

	@Override
	public void openDialogPeriod() {
//		getShell().setImeInputMode(SWT.NONE);
		DialogPeriod wDialogPeriod = new DialogPeriod(getShell(), mDateRange);
		if (wDialogPeriod.open() == IDialogConstants.OK_ID) { // Updated
			mDateRange = new DateRange(wDialogPeriod.getStartDate(), wDialogPeriod.getEndDate());
			mMonthPeriod = false;
			updateTable();
		}
	}

	@Override
	public void setNextPeriod() {
		mDateRange = Util.getMonthDateRange(Util.getAdjusentMonth(mDateRange
				.getEndDate(), 1), SystemData.getCutOff());
		updateTable();
	}

	@Override
	public void setPrevPeriod() {
		mDateRange = Util.getMonthDateRange(Util.getAdjusentMonth(mDateRange
				.getEndDate(), -1), SystemData.getCutOff());
		updateTable();
	}

	@Override
	public Book getBook() {
		return mBook;
	}

	@Override
	public void setBook(Book pBook) {
		mBook = pBook;
	}
	
//	public void addRecordTableListeners() {
//		mCompositeEntry.addRecordTableListeners();
//	}
//	
//	public void removeRecordTableListeners() {
//		mCompositeEntry.removeRecordTableListeners();
//	}
}
