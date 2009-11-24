package model.action;

import model.RightType;
import model.SystemData;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;

import view.dialog.DialogModifyRecord;

public class OpenDialogModifyRecord extends Action {
	
	private Shell mShell;
	private int mActId;
	
	public OpenDialogModifyRecord(Shell pShell, int pActId) {
		mShell = pShell;
		mActId = pActId;
	}
	
	@Override
	public void run() {
		if (SystemData.getRightType() == RightType.Main) {
			DialogModifyRecord wDialogModifyRecord = new DialogModifyRecord(mShell, mActId);
			int wRet = wDialogModifyRecord.open();
			
			if (wRet == IDialogConstants.OK_ID) {
				new UpdateEntry(SystemData.getCompositeRightMain()).run();
			}
		}
	}
}
