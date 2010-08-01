package beauties.annual;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;

import beauties.annual.view.DialogAnnualPeriod;


public class OpenDialogAnnualPeriod extends Action {

	private AnnualController mCTL;

	public OpenDialogAnnualPeriod(AnnualController pCTL) {
		mCTL = pCTL;
	}

	@Override
	public void run() {
		mCTL.getShell().setImeInputMode(SWT.NONE);
		DialogAnnualPeriod wDialogAnnualPeriod = new DialogAnnualPeriod(mCTL);
		int wRet = wDialogAnnualPeriod.open();

		if (wRet == IDialogConstants.OK_ID) { // Updated
			mCTL.setDateRange(wDialogAnnualPeriod.getDateRange());
			mCTL.recreateMainTable();
		}
	}
}
