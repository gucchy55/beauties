package beauties.record;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;

import beauties.record.view.dialog.DialogMove;


public class OpenDialogModifyMove extends Action {

	private RecordController mCTL;

	public OpenDialogModifyMove(RecordController pCtl) {
		mCTL = pCtl;
	}

	@Override
	public void run() {
		if (!mCTL.hasSelectedRecordTableItem())
			return;
		
//		mCTL.removeRecordTableListeners();
		
//		mCTL.getShell().setImeInputMode(SWT.NONE);
		DialogMove wDialogMove = new DialogMove(mCTL.getShell(), mCTL.getSelectedRecordItem());
		if (wDialogMove.open() == IDialogConstants.OK_ID)
			mCTL.updateTable();
		
//		mCTL.addRecordTableListeners();
	}
}
