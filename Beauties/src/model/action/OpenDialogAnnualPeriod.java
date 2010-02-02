package model.action;

import model.SystemData;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import view.annual.CompositeAnnualMain;
import view.dialog.DialogAnnualPeriod;

public class OpenDialogAnnualPeriod extends Action {

//	private Shell mShell;
	private CompositeAnnualMain mCompositeAnnualMain;

	public OpenDialogAnnualPeriod(CompositeAnnualMain pCompositeAnnualMain) {
//		mShell = pShell;
		mCompositeAnnualMain = pCompositeAnnualMain;
	}

	@Override
	public void run() {
		DialogAnnualPeriod wDialogAnnualPeriod = new DialogAnnualPeriod(mCompositeAnnualMain.getShell());
		int wRet = wDialogAnnualPeriod.open();

		if (wRet == IDialogConstants.OK_ID) { // Updated
//			if (wDialogPeriod.getEndDate().after(wDialogPeriod.getStartDate())) {
				SystemData.setStartDate(wDialogAnnualPeriod.getStartDate());
				SystemData.setEndDate(wDialogAnnualPeriod.getEndDate());
				SystemData.setAnnualPeriod(false);
				new UpdateAnnual(mCompositeAnnualMain).run();
//			}
		}
	}
}
