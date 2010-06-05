package beauties.record;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;

import beauties.record.view.CompositeEntry;
import beauties.record.view.dialog.DialogNewRecord;


public class OpenDialogNewRecord extends Action {

	private CompositeEntry mCompositeEntry;

	public OpenDialogNewRecord(CompositeEntry pCompositeEntry) {
		super.setText("追加");
		mCompositeEntry = pCompositeEntry;
	}

	@Override
	public void run() {
		mCompositeEntry.getShell().setImeInputMode(SWT.NONE);
		DialogNewRecord wDialogNewRecord = new DialogNewRecord(mCompositeEntry.getShell(), mCompositeEntry.getBookId());
		int wRet = wDialogNewRecord.open();
		if (wRet == IDialogConstants.OK_ID) { // Updated
			new UpdateEntry(mCompositeEntry).run();
		}
	}
}
