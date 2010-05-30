package util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.fieldassist.IContentProposal;

import model.DateRange;
import model.SystemData;
import model.db.DbUtil;

public class Util {

	private Util() {
	}

	public static Date getAdjusentMonth(Date pDate, int i) {
		Calendar wCal = Calendar.getInstance();
		wCal.setTime(pDate);

		wCal.add(Calendar.MONTH, i);
		return wCal.getTime();
	}

	public static Date getAdjusentDay(Date pDate, int i) {
		Calendar wCal = Calendar.getInstance();
		wCal.setTime(pDate);
		wCal = new GregorianCalendar(wCal.get(Calendar.YEAR), wCal.get(Calendar.MONTH), wCal
				.get(Calendar.DAY_OF_MONTH));

		wCal.add(Calendar.DAY_OF_MONTH, i);
		return wCal.getTime();
	}

	public static String getDayOfTheWeekShort(Date pDate) {
		Calendar wCal = Calendar.getInstance();
		wCal.setTime(pDate);
		return wCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
	}

	public static DateRange getMonthDateRange(Date pDate, int pCutOff) {
		// Date[] wDates = new Date[2];
		Calendar wStartCal = Calendar.getInstance();
		Calendar wEndCal = Calendar.getInstance();

		// int pCutOff;
		// Calendar wInputCal = Calendar.getInstance();
		// wInputCal.setTime(pDate);
		// wStartCal = (Calendar) wInputCal.clone();
		// wEndCal = (Calendar) wInputCal.clone();

		wStartCal.setTime(pDate);
		wEndCal.setTime(pDate);

		if (wStartCal.get(Calendar.DAY_OF_MONTH) <= pCutOff) {
			wStartCal.add(Calendar.MONTH, -1);
		} else {
			wEndCal.add(Calendar.MONTH, +1);
		}

		int wStartDay = Math.min(wStartCal.getActualMaximum(Calendar.DATE),
				pCutOff);
		int wEndDay = Math
				.min(wEndCal.getActualMaximum(Calendar.DATE), pCutOff);

		wStartCal = new GregorianCalendar(wStartCal.get(Calendar.YEAR),
				wStartCal.get(Calendar.MONTH), wStartDay);
		wStartCal.add(Calendar.DAY_OF_MONTH, +1);

		wEndCal = new GregorianCalendar(wEndCal.get(Calendar.YEAR), wEndCal
				.get(Calendar.MONTH), wEndDay);

		return new DateRange(wStartCal.getTime(), wEndCal.getTime());

		// wDates[0] = wStartCal.getTime();
		// wDates[1] = wEndCal.getTime();
		//
		// return wDates;

	}

	public static List<DateRange> getDatePairs(DateRange pDateRange, int pCutOff) {
		List<DateRange> wDateRangeList = new ArrayList<DateRange>();
		for (int i = 0;; i++) {
			DateRange wDateRange = getMonthDateRange(
					getAdjusentMonth(pDateRange.getStartDate(), i), pCutOff);
			if (wDateRange.getEndDate().after(pDateRange.getEndDate()))
				break;
			else
				wDateRangeList.add(wDateRange);
		}

		return wDateRangeList;
		// return (Date[][]) wDateList.toArray(new Date[0][]);
	}

	// 過去pMonths分を返す
	public static List<DateRange> getDatePairs(Date pDate, int pMonths, int pCutOff) {
		List<DateRange> wDateRangeList = new ArrayList<DateRange>();
		for (int i = 0; i < pMonths; i++) {
			DateRange wDateRange = getMonthDateRange(getAdjusentMonth(pDate, -(pMonths - i - 1)),
					pCutOff);
			wDateRangeList.add(wDateRange);
		}

		return wDateRangeList;
		// return (Date[][]) wDateList.toArray(new Date[0][]);
	}

	// 年始を返す
	public static DateRange getFiscalPeriod(int pCutOff) {
		int wFiscalMonth = DbUtil.getFisCalMonth();
		Calendar wCalNow = Calendar.getInstance();
		Calendar wFirstDate = new GregorianCalendar(wCalNow.get(Calendar.YEAR), wFiscalMonth - 1, 1);

		while (wFirstDate.after(wCalNow))
			wFirstDate.add(Calendar.YEAR, -1);

		DateRange wFirstDateRange = getMonthDateRange(wFirstDate.getTime(), pCutOff);
		wFirstDate.setTime(wFirstDateRange.getStartDate());

		Date wEndDate = getAdjusentMonth(wFirstDateRange.getEndDate(), 11);
		wEndDate = getMonthDateRange(wEndDate, pCutOff).getEndDate();

		return new DateRange(wFirstDate.getTime(), wEndDate);
	}

	public static IContentProposal[] createProposals(final String pContent,
			final int pPosition, String[] pCandidates, int pMaxCount) {

		if (pContent.length() == 0 || pPosition < pContent.length()) {
			return new IContentProposal[] {};
		}

		List<IContentProposal> wProposalList = new ArrayList<IContentProposal>();
		for (int i = 0; i < pCandidates.length; i++) {
			if (pCandidates[i].length() > pPosition) {
				final String wCandidate = pCandidates[i];
				if (!wCandidate.startsWith(pContent))
					continue;
				wProposalList.add(new IContentProposal() {

					@Override
					public String getLabel() {
						return wCandidate;
					}

					@Override
					public String getDescription() {
						return null;
					}

					@Override
					public int getCursorPosition() {
						return wCandidate.length();
					}

					@Override
					public String getContent() {
						return wCandidate.substring(pPosition);
					}
				});
				if (wProposalList.size() > pMaxCount)
					break;
			}
		}

		return (IContentProposal[]) wProposalList.toArray(new IContentProposal[0]);
	}

	public static int getSummationIndex(List<DateRange> pDateRangeList, int pCutOff) {
		Date wStartDateNow = Util.getMonthDateRange(new Date(), pCutOff).getStartDate();

		if (pDateRangeList.size() < 2 || wStartDateNow.before(pDateRangeList.get(1).getStartDate()))
			return SystemData.getUndefinedInt();

		if (wStartDateNow.after(pDateRangeList.get(pDateRangeList.size() - 1).getStartDate()))
			return pDateRangeList.size();

		for (int i = 0; i < pDateRangeList.size(); i++) {
			Date wStartDate = pDateRangeList.get(i).getStartDate();
			if (!wStartDate.equals(wStartDateNow))
				continue;

			if (i < 2)
				return SystemData.getUndefinedInt();

			return i;

		}
		return SystemData.getUndefinedInt();
	}

	public static List<DateRange> getDatePeriodsWithSummaion(List<DateRange> pDateRangeList,
			int pCutOff) {

		int wSummationIndex = getSummationIndex(pDateRangeList, pCutOff);

		if (wSummationIndex == SystemData.getUndefinedInt())
			return pDateRangeList;

		DateRange wWholeRange = new DateRange(pDateRangeList.get(0).getStartDate(), pDateRangeList
				.get(pDateRangeList.size() - 1).getEndDate());
		pDateRangeList.add(wSummationIndex, wWholeRange);
		return pDateRangeList;
	}
}
