package beauties.record;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;

import beauties.record.view.dialog.DialogModifyRecord;


public class OpenDialogModifyRecord extends Action {

	private RecordController mCtl;

	public OpenDialogModifyRecord(RecordController pCtl) {
		mCtl = pCtl;
	}

	@Override
	public void run() {
		if (!mCtl.hasSelectedRecordTableItem())
			return;
		mCtl.getShell().setImeInputMode(SWT.NONE);
		DialogModifyRecord wDialogModifyRecord = new DialogModifyRecord(mCtl.getShell(), mCtl.getSelectedRecordItem());
		int wRet = wDialogModifyRecord.open();

		if (wRet == IDialogConstants.OK_ID) {
			mCtl.updateTable();
//			new UpdateEntry(mCtl).run();
		}
	}
}
