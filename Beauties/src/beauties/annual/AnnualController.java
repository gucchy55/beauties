package beauties.annual;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import util.Util;
import beauties.annual.model.AnnualHeaderItem;
import beauties.annual.model.AnnualViewType;
import beauties.annual.view.CompositeAnnualMain;
import beauties.model.AnnualDateRange;
import beauties.model.DateRange;
import beauties.model.SystemData;
import beauties.model.db.DbUtil;
import beauties.record.model.SummaryTableItem;

public class AnnualController {
	private int mBookId = SystemData.getAllBookInt();
	private AnnualDateRange mAnnualDateRange;
	private boolean mFiscalPeriod = false;
	private AnnualViewType mAnnualViewType = AnnualViewType.Category;
	private int mMonthCount = 13;

	private CompositeAnnualMain mCompositeAnnualMain;

	private List<SummaryTableItem[]> mSummaryTableItems;
	private AnnualHeaderItem[] mAnnualHeaderItems;
	private List<String> mRowHeaderList;

	private static final DateFormat mDF = new SimpleDateFormat("yyyy年MM月");

	public AnnualController(CompositeAnnualMain pCompositeAnnualMain) {
		mCompositeAnnualMain = pCompositeAnnualMain;
		mAnnualDateRange = Util.getAnnualDateRange(new Date(), mMonthCount, SystemData.getCutOff());
		mRowHeaderList = new ArrayList<String>();
		updateTableItems();
	}

	private void updateTableItems() {
		// RowHeaders
		mRowHeaderList.clear();
		for (DateRange wDateRange : mAnnualDateRange.getDateRangeList())
			mRowHeaderList.add(mDF.format(wDateRange.getEndDate()));
		if (mAnnualDateRange.hasSumIndex()) {
			mRowHeaderList.set(mAnnualDateRange.getSumIndex(), "合計");
			mRowHeaderList.set(mAnnualDateRange.getAveIndex(), "平均");
		}

		// SummaryTableItems
		switch (mAnnualViewType) {
		case Original:
			mSummaryTableItems = DbUtil.getAnnualSummaryTableItemsOriginal(mAnnualDateRange);
			break;
		case Category:
			mSummaryTableItems = DbUtil.getAnnualSummaryTableItems(
					mBookId, mAnnualDateRange, false);
			break;
		default:
			mSummaryTableItems = DbUtil.getAnnualSummaryTableItems(
						mBookId, mAnnualDateRange, true);
		}

		// 列のヘッダの設定
		mAnnualHeaderItems = new AnnualHeaderItem[mSummaryTableItems.get(0).length];
		for (int i = 0; i < mSummaryTableItems.get(0).length; i++)
			mAnnualHeaderItems[i] = new AnnualHeaderItem(mSummaryTableItems.get(0)[i].getName());

	}

//	public void updateTable() {
//		updateTableItems();
//		mCompositeAnnualMain.updateTable();
//	}
	
	public void recreateMainTable() {
		updateTableItems();
		mCompositeAnnualMain.updateTable();
	}

	public int getBookId() {
		return mBookId;
	}

	public AnnualDateRange getAnnualDateRange() {
		return mAnnualDateRange;
	}

	public void setBookId(int pBookId) {
		this.mBookId = pBookId;
	}

//	public void setAnnualDateRange(AnnualDateRange pAnnualDateRange) {
//		this.mAnnualDateRange = pAnnualDateRange;
//		updateMonthCount();
//	}

	public void setFiscalPeriod(boolean pFiscalPeriod) {
		this.mFiscalPeriod = pFiscalPeriod;
		if (mFiscalPeriod)
			mAnnualDateRange = Util.getAnnualDateRangeFiscal(SystemData.getCutOff(), DbUtil.getFisCalMonth());
		else
			mAnnualDateRange = Util.getAnnualDateRange(new Date(), mMonthCount, SystemData.getCutOff());
		updateMonthCount();
	}

	public Shell getShell() {
		return mCompositeAnnualMain.getShell();
	}

	public boolean getFiscalPeriod() {
		return mFiscalPeriod;
	}

	public List<SummaryTableItem[]> getSummaryTableItems() {
		return mSummaryTableItems;
	}

	public AnnualHeaderItem[] getAnnualHeaderItems() {
		return mAnnualHeaderItems;
	}

	public List<String> getRowHeaderList() {
		return mRowHeaderList;
	}
	
	public AnnualViewType getAnnualViewType() {
		return mAnnualViewType;
	}

	public void setPrevPeriod() {
		Date wStartDate = Util.getMonthDateRange(
				Util.getAdjusentMonth(mAnnualDateRange.getStartDate(), -mMonthCount),
						SystemData.getCutOff()).getStartDate();
		Date wEndDate = Util.getMonthDateRange(
				Util.getAdjusentMonth(mAnnualDateRange.getEndDate(), -mMonthCount),
				SystemData.getCutOff()).getEndDate();
		mAnnualDateRange = Util.getAnnualDateRangeFromDateRange(
				new DateRange(wStartDate, wEndDate), SystemData.getCutOff());
		updateMonthCount();
	}

	public void setNextPeriod() {
		Date wStartDate = Util.getMonthDateRange(
				Util.getAdjusentMonth(mAnnualDateRange.getStartDate(), mMonthCount),
						SystemData.getCutOff()).getStartDate();
		Date wEndDate = Util.getMonthDateRange(
				Util.getAdjusentMonth(mAnnualDateRange.getEndDate(), mMonthCount),
				SystemData.getCutOff()).getEndDate();
		mAnnualDateRange = Util.getAnnualDateRangeFromDateRange(
				new DateRange(wStartDate, wEndDate), SystemData.getCutOff());
		updateMonthCount();
	}

	private void updateMonthCount() {
		mMonthCount = mAnnualDateRange.hasSumIndex() ? mAnnualDateRange.getDateRangeList().size() - 2
				: mAnnualDateRange.getDateRangeList().size();
	}
	
	public void setAnnualViewType(AnnualViewType pAnnualViewType) {
		mAnnualViewType = pAnnualViewType;
	}
	
	public void setDateRange(DateRange pDateRange) {
		mAnnualDateRange = Util.getAnnualDateRangeFromDateRange(pDateRange, SystemData.getCutOff());
		mFiscalPeriod = false;
		updateMonthCount();
	}
	
	public void copyToClipboard() {
		mCompositeAnnualMain.copyToClipboard();
	}
	
	public Composite getComposite() {
		return mCompositeAnnualMain;
	}
}
