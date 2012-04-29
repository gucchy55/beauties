package beauties.record;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;

import beauties.record.view.dialog.DialogMove;


public class OpenDialogNewMove extends Action {

	private RecordController mCtl;

	public OpenDialogNewMove(RecordController pCtl) {
		super.setText("移動");
		mCtl = pCtl;
	}

	@Override
	public void run() {
//		mCtl.getShell().setImeInputMode(SWT.NONE);
		DialogMove wDialogMove = new DialogMove(mCtl.getShell(), mCtl.getBookId());
		int wRet = wDialogMove.open();
		if (wRet == IDialogConstants.OK_ID) {
			mCtl.updateTable();
//			new UpdateEntry(mCtl).run();
		}
	}
}
