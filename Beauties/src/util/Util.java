package util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Shell;

import beauties.model.AnnualDateRange;
import beauties.model.DateRange;


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
		Calendar wStartCal = Calendar.getInstance();
		Calendar wEndCal = Calendar.getInstance();

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
	}

	private static List<DateRange> getMonthDateRangeListFromLongRange(DateRange pDateRange,
			int pCutOff) {
		List<DateRange> wDateRangeList = new ArrayList<DateRange>();
		for (int i = 0;; i++) {
			DateRange wDateRange = getMonthDateRange(
					getAdjusentMonth(pDateRange.getStartDate(), i), pCutOff);
			if (wDateRange.getEndDate().after(pDateRange.getEndDate()))
				break;
			wDateRangeList.add(wDateRange);
		}

		return wDateRangeList;
	}

	// 過去pMonths分を返す
	private static List<DateRange> getDateRangeListByMonthCnt(Date pDate, int pMonths, int pCutOff) {
		Date wBaseEndDate = getMonthDateRange(pDate, pCutOff).getEndDate();
		List<DateRange> wDateRangeList = new ArrayList<DateRange>();
		for (int i = 0; i < pMonths; i++) {
			DateRange wDateRange = getMonthDateRange(getAdjusentMonth(wBaseEndDate, -(pMonths - i - 1)),
					pCutOff);
			wDateRangeList.add(wDateRange);
		}

		return wDateRangeList;
	}
	
	public static AnnualDateRange getAnnualDateRange(Date pDate, int pMonths, int pCutOff) {
		return new AnnualDateRange(getDateRangeListByMonthCnt(pDate, pMonths, pCutOff));
	}
	
	public static AnnualDateRange getAnnualDateRangeFromDateRange(DateRange pDateRange,
			int pCutOff) {
		return new AnnualDateRange(getMonthDateRangeListFromLongRange(pDateRange, pCutOff));
	}

//	private static DateRange getFiscalDateRange(int pCutOff, int pFiscalMonth) {
//		return getFiscalDateRange(Calendar.getInstance(), pCutOff, pFiscalMonth);
//	}
	
	public static AnnualDateRange getAnnualDateRangeFiscal(Calendar pCal, int pCutOff, int pFiscalMonth) {
		return getAnnualDateRangeFromDateRange(getFiscalDateRange(pCal, pCutOff, pFiscalMonth), pCutOff);
	}

	public static AnnualDateRange getAnnualDateRangeFiscal(int pCutOff, int pFiscalMonth) {
		return getAnnualDateRangeFromDateRange(getFiscalDateRange(Calendar.getInstance(), pCutOff, pFiscalMonth), pCutOff);
	}
	
	private static DateRange getFiscalDateRange(Calendar pCal, int pCutOff, int pFiscalMonth) {
		Calendar wFirstDate = new GregorianCalendar(pCal.get(Calendar.YEAR), pFiscalMonth - 1, 1);

		while (wFirstDate.after(pCal))
			wFirstDate.add(Calendar.YEAR, -1);

		DateRange wFirstDateRange = getMonthDateRange(wFirstDate.getTime(), pCutOff);
		wFirstDate.setTime(wFirstDateRange.getStartDate());

		Date wEndDate = getAdjusentMonth(wFirstDateRange.getEndDate(), 11);
		wEndDate = getMonthDateRange(wEndDate, pCutOff).getEndDate();

		return new DateRange(wFirstDate.getTime(), wEndDate);
	}
	
	public static FocusListener getFocusListenerToDisableIme(final Shell pShell, final int pMode) {
		return new FocusListener() {
			public void focusGained(FocusEvent event) {
				pShell.setImeInputMode(pMode);
			}

			public void focusLost(FocusEvent event) {
			}
		};
	}

//	public static IContentProposal[] createProposals(final String pContent,
//			final int pPosition, String[] pCandidates, int pMaxCount) {
//
//		if (pContent.length() == 0 || pPosition < pContent.length()) {
//			return new IContentProposal[] {};
//		}
//
//		List<IContentProposal> wProposalList = new ArrayList<IContentProposal>();
//		for (int i = 0; i < pCandidates.length; i++) {
//			if (pCandidates[i].length() <= pPosition || !pCandidates[i].startsWith(pContent))
//				continue;
//			final String wCandidate = pCandidates[i];
//			// if (!wCandidate.startsWith(pContent))
//			// continue;
//			wProposalList.add(new IContentProposal() {
//
//				@Override
//				public String getLabel() {
//					return wCandidate;
//				}
//
//				@Override
//				public String getDescription() {
//					return null;
//				}
//
//				@Override
//				public int getCursorPosition() {
//					return wCandidate.length();
//				}
//
//				@Override
//				public String getContent() {
//					return wCandidate.substring(pPosition);
//				}
//			});
//			if (wProposalList.size() > pMaxCount)
//				break;
//		}
//
//		return (IContentProposal[]) wProposalList.toArray(new IContentProposal[0]);
//	}
}
