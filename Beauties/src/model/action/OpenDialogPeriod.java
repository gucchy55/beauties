package model.action;

import model.SystemData;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import view.dialog.DialogPeriod;
import view.entry.CompositeEntry;

public class OpenDialogPeriod extends Action {

//	private Shell mShell;
	private CompositeEntry mCompositeEntry;

	public OpenDialogPeriod(CompositeEntry pCompositeEntry) {
//		mShell = pShell;
		mCompositeEntry = pCompositeEntry;
	}

	@Override
	public void run() {
		DialogPeriod wDialogPeriod = new DialogPeriod(mCompositeEntry.getShell());
		int wRet = wDialogPeriod.open();

		if (wRet == IDialogConstants.OK_ID) { // Updated
//			if (wDialogPeriod.getEndDate().after(wDialogPeriod.getStartDate())) {
				SystemData.setStartDate(wDialogPeriod.getStartDate());
				SystemData.setEndDate(wDialogPeriod.getEndDate());
				SystemData.setMonthPeriod(false);
				new UpdateEntry(mCompositeEntry).run();
//			}
		}
	}
}
