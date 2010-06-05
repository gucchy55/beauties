package beauties.annual;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;

import beauties.annual.view.CompositeAnnualMain;
import beauties.annual.view.DialogAnnualPeriod;


public class OpenDialogAnnualPeriod extends Action {

	private CompositeAnnualMain mCompositeAnnualMain;

	public OpenDialogAnnualPeriod(CompositeAnnualMain pCompositeAnnualMain) {
		mCompositeAnnualMain = pCompositeAnnualMain;
	}

	@Override
	public void run() {
		mCompositeAnnualMain.getShell().setImeInputMode(SWT.NONE);
		DialogAnnualPeriod wDialogAnnualPeriod = new DialogAnnualPeriod(mCompositeAnnualMain.getShell(),
				mCompositeAnnualMain);
		int wRet = wDialogAnnualPeriod.open();

		if (wRet == IDialogConstants.OK_ID) { // Updated
			mCompositeAnnualMain.setDateRange(wDialogAnnualPeriod.getDateRange());
//			mCompositeAnnualMain.setStartDate(wDialogAnnualPeriod.getStartDate());
//			mCompositeAnnualMain.setEndDate(wDialogAnnualPeriod.getEndDate());
			mCompositeAnnualMain.setAnnualPeriod(false);
			new UpdateAnnual(mCompositeAnnualMain).run();
		}
	}
}
