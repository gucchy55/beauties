package model.action;

import model.SystemData;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;

import view.dialog.DialogAnnualPeriod;

public class OpenDialogAnnualPeriod extends Action {

	private Shell mShell;

	public OpenDialogAnnualPeriod(Shell pShell) {
		mShell = pShell;
	}

	@Override
	public void run() {
		DialogAnnualPeriod wDialogAnnualPeriod = new DialogAnnualPeriod(mShell);
		int wRet = wDialogAnnualPeriod.open();

		if (wRet == IDialogConstants.OK_ID) { // Updated
//			if (wDialogPeriod.getEndDate().after(wDialogPeriod.getStartDate())) {
				SystemData.setStartDate(wDialogAnnualPeriod.getStartDate());
				SystemData.setEndDate(wDialogAnnualPeriod.getEndDate());
//				SystemData.setMonthPeriod(false);
				new UpdateAnnual(SystemData.getCompositeRightMain()).run();
//			}
		}
	}
}
