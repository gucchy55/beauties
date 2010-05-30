package util;

import java.util.Date;
import java.util.GregorianCalendar;

import model.DateRange;

import junit.framework.TestCase;

public class UtilTest extends TestCase {

	public void testGetAdjusentMonth() {
		fail("Not yet implemented");
	}

	public void testGetAdjusentDay() {
		fail("Not yet implemented");
	}

	public void testGetDayOfTheWeekShort() {
		fail("Not yet implemented");
	}

	public void testGetMonthDateRange() {
		int wCutOff = 24;
		// 2010年2月10日 --> 2010.1.25 - 2010.2.24
		Date wBaseDate = new GregorianCalendar(2010, 1, 10).getTime();
		DateRange wExpected = new DateRange(new GregorianCalendar(2010, 0, 25).getTime(),
				new GregorianCalendar(2010, 1, 24).getTime());
		DateRange wResult = Util.getMonthDateRange(wBaseDate, wCutOff);
		assertTrue(wExpected.getStartDate().equals(wResult.getStartDate())
				&& wExpected.getEndDate().equals(wResult.getEndDate()));
	}
	
	public void testGetMonthDateRange2() {
		int wCutOff = 24;
		// 2010年2月28日 --> 2010.2.25 - 2010.3.24
		Date wBaseDate = new GregorianCalendar(2010, 1, 28).getTime();
		DateRange wExpected = new DateRange(new GregorianCalendar(2010, 1, 25).getTime(),
				new GregorianCalendar(2010, 2, 24).getTime());
		DateRange wResult = Util.getMonthDateRange(wBaseDate, wCutOff);
		assertTrue(wExpected.getStartDate().equals(wResult.getStartDate())
				&& wExpected.getEndDate().equals(wResult.getEndDate()));
	}

	public void testGetMonthDateRange3() {
		int wCutOff = 30;
		// 2010年2月10日 --> 2010.1.31 - 2010.2.28
		Date wBaseDate = new GregorianCalendar(2010, 1, 10).getTime();
		DateRange wExpected = new DateRange(new GregorianCalendar(2010, 0, 31).getTime(),
				new GregorianCalendar(2010, 1, 28).getTime());
		DateRange wResult = Util.getMonthDateRange(wBaseDate, wCutOff);
		assertTrue(wExpected.getStartDate().equals(wResult.getStartDate())
				&& wExpected.getEndDate().equals(wResult.getEndDate()));
	}
	
	public void testGetMonthDateRange4() {
		int wCutOff = 30;
		// 2010年2月28日 --> 2010.1.31 - 2010.2.28
		Date wBaseDate = new GregorianCalendar(2010, 1, 10).getTime();
		DateRange wExpected = new DateRange(new GregorianCalendar(2010, 0, 31).getTime(),
				new GregorianCalendar(2010, 1, 28).getTime());
		DateRange wResult = Util.getMonthDateRange(wBaseDate, wCutOff);
		assertTrue(wExpected.getStartDate().equals(wResult.getStartDate())
				&& wExpected.getEndDate().equals(wResult.getEndDate()));
	}

	public void testGetDatePairsDateDate() {
		fail("Not yet implemented");
	}

	public void testGetDatePairsDateInt() {
		fail("Not yet implemented");
	}

	public void testGetFiscalPeriod() {
		fail("Not yet implemented");
	}

	public void testCreateProposals() {
		fail("Not yet implemented");
	}

	public void testGetSummationIndex() {
		fail("Not yet implemented");
	}

	public void testGetDatePeriodsWithSummaion() {
		fail("Not yet implemented");
	}

}
