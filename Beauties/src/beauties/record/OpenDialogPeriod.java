package beauties.record;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;

import beauties.model.DateRange;
import beauties.record.view.CompositeEntry;
import beauties.record.view.dialog.DialogPeriod;


public class OpenDialogPeriod extends Action {

	private CompositeEntry mCompositeEntry;

	public OpenDialogPeriod(CompositeEntry pCompositeEntry) {
		mCompositeEntry = pCompositeEntry;
	}

	@Override
	public void run() {
		mCompositeEntry.getShell().setImeInputMode(SWT.NONE);
		DialogPeriod wDialogPeriod = new DialogPeriod(mCompositeEntry.getShell(), mCompositeEntry);
		int wRet = wDialogPeriod.open();

		if (wRet == IDialogConstants.OK_ID) { // Updated
//			mCompositeEntry.setStartDate(wDialogPeriod.getStartDate());
//			mCompositeEntry.setEndDate(wDialogPeriod.getEndDate());
			mCompositeEntry.setDateRange(new DateRange(wDialogPeriod.getStartDate(), wDialogPeriod.getEndDate()));
			mCompositeEntry.setMonthPeriod(false);
			new UpdateEntry(mCompositeEntry).run();
		}
	}
}
