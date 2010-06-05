package beauties.record;

import java.util.Date;


import org.eclipse.jface.action.Action;

import beauties.model.DateRange;
import beauties.model.db.DbUtil;
import beauties.record.view.CompositeEntry;

import util.Util;

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
