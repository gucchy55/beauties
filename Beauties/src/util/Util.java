package util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import model.SystemData;

public class Util {

	private Util() {
	}

	public static Date getAdjusentMonth(Date pDate, int i) {
		Calendar wCal = Calendar.getInstance();
		wCal.setTime(pDate);

		wCal.add(Calendar.MONTH, i);
		return wCal.getTime();
	}

	// public static Date getAdjusentDay(Date pDate, int i) {
	// Calendar wCal = Calendar.getInstance();
	// wCal.setTime(pDate);
	//		
	// wCal.add(Calendar.DAY_OF_MONTH, i);
	// return wCal.getTime();
	// }

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

//	public static void main(String[] args) {
//		SystemData.init();
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//		Date wStartDate = (new GregorianCalendar(2009, 0, 1)).getTime();
//		Date wEndDate = new Date();
//		Date[][] wDatePairs = getDatePairs(wEndDate, 25);
//		for (Date[] wDates : wDatePairs) {
//			System.out.println(df.format(wDates[0]) + " -- "
//					+ df.format(wDates[1]));
//		}
//	}

}
