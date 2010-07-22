package beauties.record;

import java.util.Date;

import org.eclipse.swt.widgets.Shell;

import util.Util;
import beauties.model.DateRange;
import beauties.model.SystemData;
import beauties.model.db.DbUtil;
import beauties.record.model.RecordTableItem;
import beauties.record.model.SummaryTableItem;
import beauties.record.view.CompositeEntry;

public class RecordController {
	private int mBookId;
	private DateRange mDateRange;

	private boolean mMonthPeriod = true;
	private boolean mSearchResult = false;

	private CompositeEntry mCompositeEntry;

	private RecordTableItem[] mRecordItemsUp;
	private RecordTableItem[] mRecordItemsBottom;
	private SummaryTableItem[] mSummaryTableItems;

	public RecordController(CompositeEntry pCompositeEntry) {
		mCompositeEntry = pCompositeEntry;
		mBookId = SystemData.getBookMap(false).keySet().iterator().next();
		mDateRange = Util.getMonthDateRange(new Date(), DbUtil.getCutOff());
		updateTableItems();
	}

	private void updateTableItems() {
		RecordTableItem[][] wRecordTableItemAll = DbUtil.getRecordTableItems(mDateRange, mBookId);
		mRecordItemsUp = wRecordTableItemAll[0];
		mRecordItemsBottom = wRecordTableItemAll[1];
		mSummaryTableItems = DbUtil.getSummaryTableItems(mBookId, mDateRange);
	}

	public void updateTable() {
		updateTableItems();
		mCompositeEntry.updateView();
	}

	public void updateForSearch(String pQuery) {
		RecordTableItem[][] wRecordTableItemAll = DbUtil.getSearchedRecordTableItemList(pQuery);
		mRecordItemsUp = wRecordTableItemAll[0];
		mRecordItemsBottom = wRecordTableItemAll[1];
		mSummaryTableItems = null;
	}

	public int getBookId() {
		return mBookId;
	}

	public DateRange getDateRange() {
		return mDateRange;
	}
//
//	public boolean getMonthPeriod() {
//		return mMonthPeriod;
//	}

	public boolean getSearchResult() {
		return mSearchResult;
	}

	public RecordTableItem[] getRecordItemsUp() {
		return mRecordItemsUp;
	}

	public RecordTableItem[] getRecordItemsBottom() {
		return mRecordItemsBottom;
	}

	public SummaryTableItem[] getSummaryTableItems() {
		return mSummaryTableItems;
	}

	public void setBookId(int pBookId) {
		this.mBookId = pBookId;
	}

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

	public void setRecordItemsUp(RecordTableItem[] pRecordItemsUp) {
		this.mRecordItemsUp = pRecordItemsUp;
	}

	public void setRecordItemsBottom(RecordTableItem[] pRecordItemsBottom) {
		this.mRecordItemsBottom = pRecordItemsBottom;
	}

	public void setSummaryTableItems(SummaryTableItem[] pSummaryTableItems) {
		this.mSummaryTableItems = pSummaryTableItems;
	}

	public boolean showBookColumn() {
		return getBookId() == SystemData.getAllBookInt() || getSearchResult();
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
		return mCompositeEntry.openSearchDialog();
	}

	public boolean getMonthPeriod() {
		if (mMonthPeriod)
			return true;
		DateRange wMonthRange = Util.getMonthDateRange(mDateRange.getEndDate(), DbUtil.getCutOff());
		mMonthPeriod = mDateRange.getStartDate().equals(wMonthRange.getStartDate())
				&& mDateRange.getEndDate().equals(wMonthRange.getEndDate());
		return mMonthPeriod;
			
	}
}
