package beauties.record;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;

import beauties.record.view.CompositeEntry;
import beauties.record.view.dialog.DialogModifyRecord;


public class OpenDialogModifyRecord extends Action {

	private CompositeEntry mCompositeEntry;

	public OpenDialogModifyRecord(CompositeEntry pCompositeEntry) {
		mCompositeEntry = pCompositeEntry;
	}

	@Override
	public void run() {
		if (!mCompositeEntry.hasSelectedRecordTableItem())
			return;
		mCompositeEntry.getShell().setImeInputMode(SWT.NONE);
		DialogModifyRecord wDialogModifyRecord = new DialogModifyRecord(mCompositeEntry.getShell(), mCompositeEntry.getSelectedRecordItem());
		int wRet = wDialogModifyRecord.open();

		if (wRet == IDialogConstants.OK_ID) {
			new UpdateEntry(mCompositeEntry).run();
		}
	}
}
