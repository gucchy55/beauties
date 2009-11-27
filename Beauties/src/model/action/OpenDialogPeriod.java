package model.action;

import model.SystemData;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;

import view.dialog.DialogPeriod;

public class OpenDialogPeriod extends Action {

	private Shell mShell;

	public OpenDialogPeriod(Shell pShell) {
		mShell = pShell;
	}

	@Override
	public void run() {
		DialogPeriod wDialogPeriod = new DialogPeriod(mShell);
		int wRet = wDialogPeriod.open();

		if (wRet == IDialogConstants.OK_ID) { // Updated
//			if (wDialogPeriod.getEndDate().after(wDialogPeriod.getStartDate())) {
				SystemData.setStartDate(wDialogPeriod.getStartDate());
				SystemData.setEndDate(wDialogPeriod.getEndDate());
				SystemData.setMonthPeriod(false);
				new UpdateEntry(SystemData.getCompositeRightMain()).run();
//			}
		}
	}
}
