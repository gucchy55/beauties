package beauties.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import beauties.common.lib.Util;
import beauties.common.model.DateRange;

import junit.framework.TestCase;

public class DateRangeTest extends TestCase {

	// EndDateと同じ日付、時刻は異なる、でTrueとなるか
	public void testIncludes1() {
		Calendar wCalendar = Calendar.getInstance();
		Date wEndDate = new GregorianCalendar(wCalendar.get(Calendar.YEAR), wCalendar
				.get(Calendar.MONTH), wCalendar.get(Calendar.DAY_OF_MONTH)).getTime();
		DateRange wDateRange = new DateRange(Util.getAdjusentMonth(wEndDate, -1), wEndDate);
		assertTrue(wDateRange.includes(wCalendar.getTime()));
	}
	
	// EndDateの翌日は含まない
	public void testIncludes2() {
		Calendar wCalendar = Calendar.getInstance();
		Date wEndDate = new GregorianCalendar(wCalendar.get(Calendar.YEAR), wCalendar
				.get(Calendar.MONTH), wCalendar.get(Calendar.DAY_OF_MONTH)).getTime();
		DateRange wDateRange = new DateRange(Util.getAdjusentMonth(wEndDate, -1), wEndDate);
		assertFalse(wDateRange.includes(Util.getAdjusentDay(wEndDate, 1)));
	}
	
	// StartDateが含まれる
	public void testIncludes3() {
		DateRange wDateRange = new DateRange(new Date(100), new Date(200));
		assertTrue(wDateRange.includes(wDateRange.getStartDate()));
	}
	
	// EndDateが含まれる
	public void testIncludes4() {
		DateRange wDateRange = new DateRange(new Date(100), new Date(200));
		assertTrue(wDateRange.includes(wDateRange.getEndDate()));
	}

}
