package beauties.common.model;

import java.util.Date;
import java.util.List;

import beauties.common.lib.SystemData;
import beauties.common.lib.Util;


public class AnnualDateRange {
	private List<DateRange> mDateRangeList;
	private static final int mUndefined = -1;
	private int mSumIndex = mUndefined;
	private int mAveIndex = mUndefined;

	public AnnualDateRange(List<DateRange> pDateRangeList) {
		mDateRangeList = pDateRangeList;
		configureDateRangeList(SystemData.getCutOff());
	}
	public AnnualDateRange(List<DateRange> pDateRangeList, int pCutOff) {
		mDateRangeList = pDateRangeList;
		configureDateRangeList(pCutOff);
	}

	private void configureDateRangeList(int pCutOff) {
		Date wStartDateNow = Util.getMonthDateRange(new Date(), pCutOff).getStartDate();

		if (mDateRangeList.size() < 2 || wStartDateNow.before(mDateRangeList.get(1).getStartDate()))
			return;

		if (wStartDateNow.after(mDateRangeList.get(mDateRangeList.size() - 1).getStartDate())) {
			addSummationRanges(mDateRangeList.size());
			return;
		}

		for (int i = 0; i < mDateRangeList.size(); i++) {
			Date wStartDate = mDateRangeList.get(i).getStartDate();
			if (!wStartDate.equals(wStartDateNow))
				continue;

			if (i < 2)
				return;

			addSummationRanges(i);
			break;

		}
	}

	private void addSummationRanges(int pIndex) {
		DateRange wWholeRange = new DateRange(getStartDate(), mDateRangeList.get(pIndex - 1).getEndDate());
		mSumIndex = pIndex;
		mAveIndex = pIndex + 1;
		for (int i = 0; i < 2; i++)
			mDateRangeList.add(mSumIndex, wWholeRange);
	}

	public int getSumIndex() {
		return mSumIndex;
	}

	public int getAveIndex() {
		return mAveIndex;
	}

	public List<DateRange> getDateRangeList() {
		return mDateRangeList;
	}

	public boolean hasSumIndex() {
		return mSumIndex != mUndefined;
	}

	public int size() {
		return mDateRangeList.size();
	}

	public Date getStartDate() {
		return mDateRangeList.get(0).getStartDate();
	}

	public Date getEndDate() {
		return mDateRangeList.get(mDateRangeList.size() - 1).getEndDate();
	}
}
