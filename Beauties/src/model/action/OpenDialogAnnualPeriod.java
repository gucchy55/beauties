package model.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import view.annual.CompositeAnnualMain;
import view.dialog.DialogAnnualPeriod;

public class OpenDialogAnnualPeriod extends Action {

	private CompositeAnnualMain mCompositeAnnualMain;

	public OpenDialogAnnualPeriod(CompositeAnnualMain pCompositeAnnualMain) {
		mCompositeAnnualMain = pCompositeAnnualMain;
	}

	@Override
	public void run() {
		DialogAnnualPeriod wDialogAnnualPeriod = new DialogAnnualPeriod(mCompositeAnnualMain.getShell(),
				mCompositeAnnualMain);
		int wRet = wDialogAnnualPeriod.open();

		if (wRet == IDialogConstants.OK_ID) { // Updated
			mCompositeAnnualMain.setStartDate(wDialogAnnualPeriod.getStartDate());
			mCompositeAnnualMain.setEndDate(wDialogAnnualPeriod.getEndDate());
			mCompositeAnnualMain.setAnnualPeriod(false);
			new UpdateAnnual(mCompositeAnnualMain).run();
		}
	}
}
