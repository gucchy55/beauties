package util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;

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
			
	 wCal.add(Calendar.DAY_OF_MONTH, i);
	 return wCal.getTime();
	 }

	public static String getDayOfTheWeekShort(Date pDate) {
		Calendar wCal = Calendar.getInstance();
		wCal.setTime(pDate);
		switch (wCal.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SUNDAY:
			return "日";
		case Calendar.MONDAY:
			return "月";
		case Calendar.TUESDAY:
			return "火";
		case Calendar.WEDNESDAY:
			return "水";
		case Calendar.THURSDAY:
			return "木";
		case Calendar.FRIDAY:
			return "金";
		case Calendar.SATURDAY:
			return "土";
		}
		throw new IllegalStateException();

	}

	// public static int[] getDateIntegersByDate(Date pDate) {
	// Calendar wCal = new GregorianCalendar();
	// wCal.setTime(pDate);
	// return getDateIntegersByCal(wCal);
	// }
	//	
	// public static int[] getDateIntegersByCal(Calendar pCal) {
	// int[] wRet = new int[3]; // Year, Month, Day
	// wRet[0] = pCal.get(Calendar.YEAR);
	// wRet[1] = pCal.get(Calendar.MONTH) + 1;
	// wRet[2] = pCal.get(Calendar.DAY_OF_MONTH);
	//
	// return wRet;
	//	
	// }

	public static Date[] getPeriod(Date pDate) {
		Date[] wDates = new Date[2];
		Calendar wStartCal;
		Calendar wEndCal;

		int wCutOff = SystemData.getCutOff();
		Calendar wInputCal = Calendar.getInstance();
		wInputCal.setTime(pDate);
		wStartCal = (Calendar) wInputCal.clone();
		wEndCal = (Calendar) wInputCal.clone();

		if (wInputCal.get(Calendar.DAY_OF_MONTH) <= wCutOff) {
			wStartCal.add(Calendar.MONTH, -1);
		} else {
			wEndCal.add(Calendar.MONTH, +1);
		}

		int wStartDay = Math.min(wStartCal.getActualMaximum(Calendar.DATE),
				wCutOff);
		int wEndDay = Math
				.min(wEndCal.getActualMaximum(Calendar.DATE), wCutOff);

		wStartCal = new GregorianCalendar(wStartCal.get(Calendar.YEAR),
				wStartCal.get(Calendar.MONTH), wStartDay);
		wStartCal.add(Calendar.DAY_OF_MONTH, +1);

		wEndCal = new GregorianCalendar(wEndCal.get(Calendar.YEAR), wEndCal
				.get(Calendar.MONTH), wEndDay);

		wDates[0] = wStartCal.getTime();
		wDates[1] = wEndCal.getTime();

		return wDates;

	}

	public static Date[][] getDatePairs(Date pStartDate, Date pEndDate) {
		List<Date[]> wDateList = new ArrayList<Date[]>();
		for (int i = 0;; i++) {
			Date[] wDates = getPeriod(getAdjusentMonth(pStartDate, i));
			if (wDates[1].after(pEndDate)) {
				break;
			} else {
				wDateList.add(wDates);
			}
		}

		return (Date[][]) wDateList.toArray(new Date[0][]);
	}

	// 過去pMonths分を返す
	public static Date[][] getDatePairs(Date pDate, int pMonths) {
		List<Date[]> wDateList = new ArrayList<Date[]>();
		for (int i = 0; i < pMonths; i++) {
			Date[] wDates = getPeriod(getAdjusentMonth(pDate, -(pMonths - i - 1)));
			wDateList.add(wDates);
		}

		return (Date[][]) wDateList.toArray(new Date[0][]);
	}
	
	// 年始を返す
	public static Date[] getFiscalPeriod() {
		int wFiscalMonth = DbUtil.getFisCalMonth();
		Calendar wCalNow = Calendar.getInstance();
		Calendar wFirstDate = new GregorianCalendar(wCalNow.get(Calendar.YEAR), wFiscalMonth - 1, 1);
		
		while (wFirstDate.after(wCalNow)) {
			wFirstDate.add(Calendar.YEAR, -1);
		}
		Date[] wFirstPeriod = getPeriod(wFirstDate.getTime());
		wFirstDate.setTime(wFirstPeriod[0]);
		
		Date wEndDate = getAdjusentMonth(wFirstPeriod[1], 11);
		wEndDate = getPeriod(wEndDate)[1];
		
		return new Date[]{wFirstDate.getTime(), wEndDate};
		
	}
	
	public static IContentProposal[] createProposals(final String pContent,
			final int pPosition, String[] pCandidates, int pMaxCount) {

		if (pContent.length() == 0 || pPosition < pContent.length()) {
//			|| !(0 < (pPosition - 3)
//				|| "".equals(pContent.substring(pPosition - 3, pPosition))) {
			return new IContentProposal[] {};
		}


		List<IContentProposal> wProposalList = new ArrayList<IContentProposal>();
		for (int i = 0; i < pCandidates.length; i++) {
			if (pCandidates[i].length() > pPosition) {
				final String wCandidate = pCandidates[i];
				if (wCandidate.startsWith(pContent)) {
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
			}
		}

		return (IContentProposal[])wProposalList.toArray(new IContentProposal[0]);
	}
	
	public static int getSummationIndex(Date[][] pDatePeriods) {
		Date wStartDateNow = Util.getPeriod(new Date())[0];
		
		if (pDatePeriods.length < 2) {
			return SystemData.getUndefinedInt();
		} else if (wStartDateNow.before(pDatePeriods[1][0])) {
			return SystemData.getUndefinedInt();
		}
		if (wStartDateNow.after(pDatePeriods[pDatePeriods.length - 1][0])) {
			return pDatePeriods.length;
		}

		
		for (int i=0; i < pDatePeriods.length; i++) {
			Date wStartDate = pDatePeriods[i][0];
			if (wStartDate.compareTo(wStartDateNow) == 0) {
				if (i < 2) {
					return SystemData.getUndefinedInt();
				} else {
					return i;
				}
			}
		}
		return SystemData.getUndefinedInt();
	}
	
	public static Date[][] getDatePeriodsWithSummaion(Date[][] pDatePeriods) {
		
		int wSummationIndex = getSummationIndex(pDatePeriods);

		if (wSummationIndex != SystemData.getUndefinedInt()) {
			List<Date[]> wDatePeriodList = new ArrayList<Date[]>(pDatePeriods.length + 1);
			for (Date[] wDatePeriod : pDatePeriods) {
				wDatePeriodList.add(wDatePeriod);
			}
			Date[] wSummationPeriod = { pDatePeriods[0][0], pDatePeriods[wSummationIndex - 1][1] };
			wDatePeriodList.add(wSummationIndex, wSummationPeriod);
			return (Date[][])wDatePeriodList.toArray(new Date[0][]);
		} else {
			return pDatePeriods;
		}
		
	}


//	public static void main(String[] args) {
//		
//		Date[][] wDatePeriods = getDatePeriodsWithSummaion(getDatePairs(new Date(), 3));
//		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
//		for (Date[] wDatePeriod : wDatePeriods) {
//			System.out.println(df.format(wDatePeriod[0]) + " - " + df.format(wDatePeriod[1]));
//		}
//	}

}
