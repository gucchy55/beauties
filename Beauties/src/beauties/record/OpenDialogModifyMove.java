package beauties.record;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;

import beauties.record.view.CompositeEntry;
import beauties.record.view.dialog.DialogMove;


public class OpenDialogModifyMove extends Action {

	private CompositeEntry mCompositeEntry;

	public OpenDialogModifyMove(CompositeEntry pCompositeEntry) {
		mCompositeEntry = pCompositeEntry;
	}

	@Override
	public void run() {
		if (!mCompositeEntry.hasSelectedRecordTableItem())
			return;
		mCompositeEntry.getShell().setImeInputMode(SWT.NONE);
		DialogMove wDialogMove = new DialogMove(mCompositeEntry.getShell(), mCompositeEntry.getSelectedRecordItem());
		int wRet = wDialogMove.open();

		if (wRet == IDialogConstants.OK_ID) {
			new UpdateEntry(mCompositeEntry).run();
		}
	}
}
