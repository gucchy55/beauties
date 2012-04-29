package beauties.record;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;

import beauties.record.view.dialog.DialogNewRecord;


public class OpenDialogNewRecord extends Action {

	private RecordController mCtl;

	public OpenDialogNewRecord(RecordController pCtl) {
		super.setText("追加");
		mCtl = pCtl;
	}

	@Override
	public void run() {
//		mCtl.getShell().setImeInputMode(SWT.NONE);
		DialogNewRecord wDialogNewRecord = new DialogNewRecord(mCtl.getShell(), mCtl.getBookId());
		int wRet = wDialogNewRecord.open();
		if (wRet == IDialogConstants.OK_ID) { // Updated
			mCtl.updateTable();
//			new UpdateEntry(mCompositeEntry).run();
		}
	}
}
