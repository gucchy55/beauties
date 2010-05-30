package model;

import java.util.Date;
import util.Util;

public class DateRange {

	private final Date mStartDate;
	private final Date mEndDate;

	public DateRange(Date pStartDate, Date pEndDate) {
		assert pEndDate.after(pStartDate);
		this.mStartDate = pStartDate;
		this.mEndDate = pEndDate;
	}

	public Date getStartDate() {
		return mStartDate;
	}

	public Date getEndDate() {
		return mEndDate;
	}

	public boolean includes(Date wDate) {
		return (wDate.after(mStartDate) || wDate.equals(mStartDate))
				&& Util.getAdjusentDay(mEndDate, 1).after(wDate);
	}

	// public static void main (String[] args) {
	// Calendar wCalendar = Calendar.getInstance();
	// Date wEndDate = new GregorianCalendar(wCalendar.get(Calendar.YEAR),
	// wCalendar
	// .get(Calendar.MONTH), wCalendar.get(Calendar.DAY_OF_MONTH)).getTime();
	// DateRange wDateRange = new DateRange(Util.getAdjusentMonth(wEndDate, -1),
	// wEndDate);
	// System.out.println(wDateRange.includes(wCalendar.getTime()));
	// }

}
