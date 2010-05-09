package model.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;

import view.dialog.DialogPeriod;
import view.entry.CompositeEntry;

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
			mCompositeEntry.setStartDate(wDialogPeriod.getStartDate());
			mCompositeEntry.setEndDate(wDialogPeriod.getEndDate());
			mCompositeEntry.setMonthPeriod(false);
			new UpdateEntry(mCompositeEntry).run();
		}
	}
}
