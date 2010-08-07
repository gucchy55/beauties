package beauties.record;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;

import beauties.record.view.dialog.DialogModifyRecord;


public class OpenDialogModifyRecord extends Action {

	private RecordController mCTL;

	public OpenDialogModifyRecord(RecordController pCtl) {
		mCTL = pCtl;
	}

	@Override
	public void run() {
		if (!mCTL.hasSelectedRecordTableItem())
			return;
		
		mCTL.removeRecordTableListeners();
		
		mCTL.getShell().setImeInputMode(SWT.NONE);
		DialogModifyRecord wDialogModifyRecord = new DialogModifyRecord(mCTL.getShell(), mCTL.getSelectedRecordItem());
		if (wDialogModifyRecord.open() == IDialogConstants.OK_ID)
			mCTL.updateTable();
		
		mCTL.addRecordTableListeners();
	}
}
