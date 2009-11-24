package model.action;

import model.SystemData;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import view.dialog.DialogNewRecord;

public class OpenDialogNewRecord extends Action {

	private Shell mShell;

	public OpenDialogNewRecord(Shell pShell) {
		super.setText("追加");
		mShell = pShell;
		this.setAccelerator(SWT.CTRL + 'I');
	}

	@Override
	public void run() {
		DialogNewRecord wDialogNewRecord = new DialogNewRecord(mShell);
		int wRet = wDialogNewRecord.open();
		if (wRet == IDialogConstants.OK_ID) { // Updated
			new UpdateEntry(SystemData.getCompositeRightMain()).run();
		}
	}
}
