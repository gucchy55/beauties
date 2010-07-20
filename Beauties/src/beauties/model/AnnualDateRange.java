package beauties.model;

import java.util.Date;
import java.util.List;

import beauties.model.db.DbUtil;

import util.Util;

public class AnnualDateRange {
	private List<DateRange> mDateRangeList;
	private int mSumIndex = SystemData.getUndefinedInt();
	private int mAveIndex = SystemData.getUndefinedInt();

	public AnnualDateRange(List<DateRange> pDateRangeList) {
		mDateRangeList = pDateRangeList;
		configureDateRangeList();
	}

	private void configureDateRangeList() {
		Date wStartDateNow = Util.getMonthDateRange(new Date(), DbUtil.getCutOff()).getStartDate();

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
		DateRange wWholeRange = new DateRange(getStartDate(), getEndDate());
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
		return mSumIndex != SystemData.getUndefinedInt();
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
