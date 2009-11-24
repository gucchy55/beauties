package model.action;

import model.SystemData;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import view.dialog.DialogMove;

public class OpenDialogNewMove extends Action {

	private Shell mShell;

	public OpenDialogNewMove(Shell pShell) {
		super.setText("移動");
		mShell = pShell;
		this.setAccelerator(SWT.CTRL + 'M');
	}

	@Override
	public void run() {
		DialogMove wDialogMove = new DialogMove(mShell);
		int wRet = wDialogMove.open();
		if (wRet == IDialogConstants.OK_ID) {
			new UpdateEntry(SystemData.getCompositeRightMain()).run();
		}
	}
}
