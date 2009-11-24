package model.action;

import model.RightType;
import model.SystemData;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;

import view.dialog.DialogMove;

public class OpenDialogModifyMove extends Action {
	
	private Shell mShell;
	private int mActId;
	
	public OpenDialogModifyMove(Shell pShell, int pActId) {
		mShell = pShell;
		mActId = pActId;
	}
	
	@Override
	public void run() {
		if (SystemData.getRightType() == RightType.Main) {
			DialogMove wDialogMove = new DialogMove(mShell, mActId);
			int wRet = wDialogMove.open();
			
			if (wRet == IDialogConstants.OK_ID) {
				new UpdateEntry(SystemData.getCompositeRightMain()).run();
			}
		}
	}
}
