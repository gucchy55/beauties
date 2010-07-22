package beauties.record;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;

import beauties.model.DateRange;
import beauties.record.view.dialog.DialogPeriod;


public class OpenDialogPeriod extends Action {

//	private CompositeEntry mCompositeEntry;
	private RecordController mCtl; 

	public OpenDialogPeriod(RecordController pCtl) {
		mCtl = pCtl;
	}

	@Override
	public void run() {
		mCtl.getShell().setImeInputMode(SWT.NONE);
		DialogPeriod wDialogPeriod = new DialogPeriod(mCtl.getShell(), mCtl.getDateRange());
		int wRet = wDialogPeriod.open();

		if (wRet == IDialogConstants.OK_ID) { // Updated
//			mCompositeEntry.setStartDate(wDialogPeriod.getStartDate());
//			mCompositeEntry.setEndDate(wDialogPeriod.getEndDate());
			mCtl.setDateRange(new DateRange(wDialogPeriod.getStartDate(), wDialogPeriod.getEndDate()));
			mCtl.setMonthPeriod(false);
			mCtl.updateTable();
//			new UpdateEntry(mCompositeEntry).run();
		}
	}
}
