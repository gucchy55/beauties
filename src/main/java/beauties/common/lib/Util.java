package beauties.common.lib;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.widgets.Combo;

import beauties.common.model.AnnualDateRange;
import beauties.common.model.DateRange;


public class Util {
	
	private static Collection<KeyStroke> mProposalKeyStrokes;
	private static IControlContentAdapter mControlContentAdapter;

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
        String[] daysOfWeek = {"日", "月", "火", "水", "木", "金", "土"};

        Calendar cal = Calendar.getInstance();
        cal.setTime(pDate);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        return daysOfWeek[dayOfWeek - 1];
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
		return new AnnualDateRange(getDateRangeListByMonthCnt(pDate, pMonths, pCutOff), pCutOff);
	}
	
	public static AnnualDateRange getAnnualDateRangeFromDateRange(DateRange pDateRange,
			int pCutOff) {
		return new AnnualDateRange(getMonthDateRangeListFromLongRange(pDateRange, pCutOff), pCutOff);
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
	
	private static IContentProposal[] createProposals(final String pContent,
			final int pPosition, String[] pCandidates, int pMaxCount) {

		if (pContent.length() == 0 || pPosition < pContent.length()) {
			return new IContentProposal[] {};
		}

		List<IContentProposal> wProposalList = new ArrayList<IContentProposal>();
		for (String wCandidate : pCandidates) {
			if (wCandidate.length() <= pPosition || !wCandidate.startsWith(pContent)) {
				continue;
			}
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
			if (wProposalList.size() > pMaxCount) {
				break;
			}
		}
		return wProposalList.toArray(new IContentProposal[0]);
	}
	
	private static Collection<KeyStroke> getProposalKeyStrokes () {
		if (mProposalKeyStrokes == null) {
			mProposalKeyStrokes = new HashSet<>();
			try {
				mProposalKeyStrokes.add(KeyStroke.getInstance("Ctrl+;"));
				mProposalKeyStrokes.add(KeyStroke.getInstance("COMMAND+;"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			mProposalKeyStrokes.remove(null);
		}
		return mProposalKeyStrokes;
	}
	
	public static void generateContentProposal(Combo pCombo, int pMaxCount) {
		IContentProposalProvider wContentProvider = 
				(contents, position) -> createProposals(contents, position, pCombo.getItems(), pMaxCount);
		if (mControlContentAdapter == null) {
			mControlContentAdapter = new ComboContentAdapter();
		}
		for (KeyStroke wKeyStroke : Util.getProposalKeyStrokes()) {
			new ContentProposalAdapter(pCombo, mControlContentAdapter, wContentProvider, wKeyStroke, null);
		}
	}
	
}
