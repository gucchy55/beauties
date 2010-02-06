package model.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;

import view.dialog.DialogNewRecord;
import view.entry.CompositeEntry;

public class OpenDialogNewRecord extends Action {

	private CompositeEntry mCompositeEntry;

	public OpenDialogNewRecord(CompositeEntry pCompositeEntry) {
		super.setText("追加");
		mCompositeEntry = pCompositeEntry;
	}

	@Override
	public void run() {
		DialogNewRecord wDialogNewRecord = new DialogNewRecord(mCompositeEntry.getShell(), mCompositeEntry.getBookId());
		int wRet = wDialogNewRecord.open();
		if (wRet == IDialogConstants.OK_ID) { // Updated
			new UpdateEntry(mCompositeEntry).run();
		}
	}
}
