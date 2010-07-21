package beauties.record;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;

import beauties.record.view.dialog.DialogMove;


public class OpenDialogModifyMove extends Action {

	private RecordController mCtl;

	public OpenDialogModifyMove(RecordController pCtl) {
		mCtl = pCtl;
	}

	@Override
	public void run() {
		if (!mCtl.hasSelectedRecordTableItem())
			return;
		mCtl.getShell().setImeInputMode(SWT.NONE);
		DialogMove wDialogMove = new DialogMove(mCtl.getShell(), mCtl.getSelectedRecordItem());
		int wRet = wDialogMove.open();

		if (wRet == IDialogConstants.OK_ID) {
			mCtl.updateTable();
//			new UpdateEntry(mCtl).run();
		}
	}
}
