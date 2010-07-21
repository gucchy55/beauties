package beauties.record;

import org.eclipse.jface.action.Action;

public class UpdateEntry extends Action {

//	private CompositeEntry mCompositeEntry;
	private RecordController mCtl;

//	public UpdateEntry(RecordController pCtl, Date pDate) {
//		mCtl = pCtl;
////		mCompositeEntry = (CompositeEntry) pCompositeEntry;
//		mCtl.setDateRange(Util.getMonthDateRange(pDate, DbUtil.getCutOff()));
////		mCompositeEntry.setDateRange(wDateRange);
////		mCompositeEntry.setStartDate(wDateRange.getStartDate());
////		mCompositeEntry.setEndDate(wDateRange.getEndDate());
//	}

	public UpdateEntry(RecordController pCtl) {
		mCtl = pCtl;
	}

	@Override
	public void run() {
		mCtl.updateTable();
	}

}
