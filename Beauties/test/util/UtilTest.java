package util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import beauties.common.lib.Util;
import beauties.model.AnnualDateRange;
import beauties.model.DateRange;


import junit.framework.TestCase;

public class UtilTest extends TestCase {

	public void testGetAdjusentMonth1() {
		// 2010.1.31 +1Month --> 2010.2.28
		Date wBaseDate = new GregorianCalendar(2010, 0, 31).getTime();
		Date wExpected = new GregorianCalendar(2010, 1, 28).getTime();
		assertTrue(wExpected.equals(Util.getAdjusentMonth(wBaseDate, 1)));
	}

	public void testGetAdjusentMonth2() {
		// 2010.1.31 -2Months --> 2009.11.30
		Date wBaseDate = new GregorianCalendar(2010, 0, 31).getTime();
		Date wExpected = new GregorianCalendar(2009, 10, 30).getTime();
		assertTrue(wExpected.equals(Util.getAdjusentMonth(wBaseDate, -2)));
	}

	public void testGetAdjusentDay1() {
		// 2010.1.31 +1Day --> 2010.2.1
		Date wBaseDate = new GregorianCalendar(2010, 0, 31).getTime();
		Date wExpected = new GregorianCalendar(2010, 1, 1).getTime();
		assertTrue(wExpected.equals(Util.getAdjusentDay(wBaseDate, 1)));
	}

	public void testGetAdjusentDay2() {
		// 2010.3.1 -1Day --> 2010.2.28
		Date wBaseDate = new GregorianCalendar(2010, 2, 1).getTime();
		Date wExpected = new GregorianCalendar(2010, 1, 28).getTime();
		assertTrue(wExpected.equals(Util.getAdjusentDay(wBaseDate, -1)));
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
		// 2010年2月28日12時 --> 2010.1.31 - 2010.2.28
		Date wBaseDate = new GregorianCalendar(2010, 1, 28, 12, 0, 0).getTime();
		DateRange wExpected = new DateRange(new GregorianCalendar(2010, 0, 31).getTime(),
				new GregorianCalendar(2010, 1, 28).getTime());
		DateRange wResult = Util.getMonthDateRange(wBaseDate, wCutOff);
		assertTrue(wExpected.getStartDate().equals(wResult.getStartDate())
				&& wExpected.getEndDate().equals(wResult.getEndDate()));
	}

	public void testGetMonthDateRange5() {
		int wCutOff = 24;
		// 2010年2月25日 --> 2010.2.25 - 2010.3.24
		Date wBaseDate = new GregorianCalendar(2010, 1, 25).getTime();
		DateRange wExpected = new DateRange(new GregorianCalendar(2010, 1, 25).getTime(),
				new GregorianCalendar(2010, 2, 24).getTime());
		DateRange wResult = Util.getMonthDateRange(wBaseDate, wCutOff);
		assertTrue(wExpected.getStartDate().equals(wResult.getStartDate())
				&& wExpected.getEndDate().equals(wResult.getEndDate()));
	}

	public void testGetMonthDateRange6() {
		int wCutOff = 31;
		// 2010年2月25日 --> 2010.2.1 - 2010.2.28
		Date wBaseDate = new GregorianCalendar(2010, 1, 25).getTime();
		DateRange wExpected = new DateRange(new GregorianCalendar(2010, 1, 1).getTime(),
				new GregorianCalendar(2010, 1, 28).getTime());
		DateRange wResult = Util.getMonthDateRange(wBaseDate, wCutOff);
		assertTrue(wExpected.getStartDate().equals(wResult.getStartDate())
				&& wExpected.getEndDate().equals(wResult.getEndDate()));
	}

	public void testGetMonthDateRange7() {
		int wCutOff = 31;
		// 2010年2月1日 --> 2010.2.1 - 2010.2.28
		Date wBaseDate = new GregorianCalendar(2010, 1, 1).getTime();
		DateRange wExpected = new DateRange(new GregorianCalendar(2010, 1, 1).getTime(),
				new GregorianCalendar(2010, 1, 28).getTime());
		DateRange wResult = Util.getMonthDateRange(wBaseDate, wCutOff);
		assertTrue(wExpected.getStartDate().equals(wResult.getStartDate())
				&& wExpected.getEndDate().equals(wResult.getEndDate()));
	}

	public void testGetAnnualDateRangeFromDateRange1() {
		int wCutOff = 24;
		// 2009.11.1 --> 2009.10.25
		Date wStartDate = Util.getMonthDateRange(new GregorianCalendar(2009, 10, 1).getTime(),
				wCutOff).getStartDate();
		// 2010.12.30 --> 2011.1.24
		Date wEndDate = Util.getMonthDateRange(new GregorianCalendar(2010, 11, 30).getTime(),
				wCutOff).getEndDate();
		// 2009.10.25
		Date wExpectedStart = new GregorianCalendar(2009, 9, 25).getTime();
		// 2011.1.24
		Date wExpectedEnd = new GregorianCalendar(2011, 0, 24).getTime();
		DateRange wDateRange = new DateRange(wStartDate, wEndDate);
		AnnualDateRange wResult = Util.getAnnualDateRangeFromDateRange(wDateRange, wCutOff);
		assertTrue(wExpectedStart.equals(wResult.getStartDate())
				&& wExpectedEnd.equals(wResult.getEndDate()));
		assertTrue(wResult.size() == (wResult.hasSumIndex() ? 17 : 15));
	}
	
	public void testAnnualDateRangeFromDateRange2() {
		int wCutOff = 31;
		// 2009.11.1 --> 2009.11.1
		Date wStartDate = Util.getMonthDateRange(new GregorianCalendar(2009, 10, 1).getTime(),
				wCutOff).getStartDate();
		// 2010.12.30 --> 2010.12.31
		Date wEndDate = Util.getMonthDateRange(new GregorianCalendar(2010, 11, 30).getTime(),
				wCutOff).getEndDate();
		// 2009.11.1
		Date wExpectedStart = new GregorianCalendar(2009, 10, 1).getTime();
		// 2010.12.31
		Date wExpectedEnd = new GregorianCalendar(2010, 11, 31).getTime();
		DateRange wDateRange = new DateRange(wStartDate, wEndDate);
		AnnualDateRange wResult = Util.getAnnualDateRangeFromDateRange(wDateRange, wCutOff);
		assertTrue(wExpectedStart.equals(wResult.getStartDate())
				&& wExpectedEnd.equals(wResult.getEndDate()));
		assertTrue(wResult.size() == 14);
	}

	public void testGetAnnualDateRange1() {
		int wCutOff = 24;
		// 2010.12.30
		Date wEndDate = new GregorianCalendar(2010, 11, 30).getTime();
		// 2009.10.25
		Date wExpectedStart = new GregorianCalendar(2009, 9, 25).getTime();
		// 2011.1.24
		Date wExpectedEnd = new GregorianCalendar(2011, 0, 24).getTime();
		AnnualDateRange wResult = Util.getAnnualDateRange(wEndDate, 15, wCutOff);
		
		assertTrue(wExpectedStart.equals(wResult.getStartDate())
				&& wExpectedEnd.equals(wResult.getEndDate()));
	}
	
	public void testGetAnnualDateRange2() {
		int wCutOff = 31;
		// 2010.12.30
		Date wEndDate = new GregorianCalendar(2010, 11, 30).getTime();
		// 2009.11.1
		Date wExpectedStart = new GregorianCalendar(2009, 10, 1).getTime();
		// 2010.12.31
		Date wExpectedEnd = new GregorianCalendar(2010, 11, 31).getTime();

		AnnualDateRange wResultList = Util.getAnnualDateRange(wEndDate, 14, wCutOff);
		
		assertTrue(wExpectedStart.equals(wResultList.getStartDate())
				&& wExpectedEnd.equals(wResultList.getEndDate()));
	}


	public void testGetAnnualDateRangeFiscal1() {
		int wCutOff = 24;
		int wFiscalMonth = 1;
		// 2010.12.24-12:00 --> 2009.12.25 - 2010.12.24
		Calendar wCal = new GregorianCalendar(2010,11,24,12,0,0);
		Date wExpectedStart = new GregorianCalendar(2009, 11, 25).getTime();
		Date wExpectedEnd = new GregorianCalendar(2010,11,24).getTime();
		AnnualDateRange wResult = Util.getAnnualDateRangeFiscal(wCal, wCutOff, wFiscalMonth);
		assertTrue(wExpectedStart.equals(wResult.getStartDate()) && wExpectedEnd.equals(wResult.getEndDate()));
	}
	
	public void testGetAnnualDateRangeFiscal2() {
		int wCutOff = 24;
		int wFiscalMonth = 4;
		// 2010.3.24-12:00 --> 2009.3.25 - 2010.3.24
		Calendar wCal = new GregorianCalendar(2010,2,24,12,0,0);
		Date wExpectedStart = new GregorianCalendar(2009, 2, 25).getTime();
		Date wExpectedEnd = new GregorianCalendar(2010,2,24).getTime();
		AnnualDateRange wResult = Util.getAnnualDateRangeFiscal(wCal, wCutOff, wFiscalMonth);
		assertTrue(wExpectedStart.equals(wResult.getStartDate()) && wExpectedEnd.equals(wResult.getEndDate()));
	}
}
