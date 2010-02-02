package model.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import view.dialog.DialogMove;
import view.entry.CompositeEntry;

public class OpenDialogNewMove extends Action {

//	private Shell mShell;
	private CompositeEntry mCompositeEntry;

	public OpenDialogNewMove(CompositeEntry pCompositeEntry) {
		super.setText("移動");
		mCompositeEntry = pCompositeEntry;
//		mShell = pShell;
//		this.setAccelerator(SWT.CTRL + 'M');
	}

	@Override
	public void run() {
		DialogMove wDialogMove = new DialogMove(mCompositeEntry.getShell());
		int wRet = wDialogMove.open();
		if (wRet == IDialogConstants.OK_ID) {
			new UpdateEntry(mCompositeEntry).run();
		}
	}
}
