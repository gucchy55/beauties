package model.action;

import java.util.Date;

import model.DateRange;
import model.db.DbUtil;

import org.eclipse.jface.action.Action;

import util.Util;
import view.entry.CompositeEntry;

public class UpdateEntry extends Action {

	private CompositeEntry mCompositeEntry;

	public UpdateEntry(CompositeEntry pCompositeEntry, Date pDate) {
		mCompositeEntry = (CompositeEntry) pCompositeEntry;
		DateRange wDateRange = Util.getMonthDateRange(pDate, DbUtil.getCutOff());
		mCompositeEntry.setDateRange(wDateRange);
//		mCompositeEntry.setStartDate(wDateRange.getStartDate());
//		mCompositeEntry.setEndDate(wDateRange.getEndDate());
	}

	public UpdateEntry(CompositeEntry pCompositeEntry) {
		mCompositeEntry = (CompositeEntry) pCompositeEntry;
	}

	@Override
	public void run() {
		mCompositeEntry.updateView();
	}

}
