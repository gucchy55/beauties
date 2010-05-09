package model.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;

import view.dialog.DialogMove;
import view.entry.CompositeEntry;

public class OpenDialogNewMove extends Action {

	private CompositeEntry mCompositeEntry;

	public OpenDialogNewMove(CompositeEntry pCompositeEntry) {
		super.setText("移動");
		mCompositeEntry = pCompositeEntry;
	}

	@Override
	public void run() {
		mCompositeEntry.getShell().setImeInputMode(SWT.NONE);
		DialogMove wDialogMove = new DialogMove(mCompositeEntry.getShell(), mCompositeEntry.getBookId());
		int wRet = wDialogMove.open();
		if (wRet == IDialogConstants.OK_ID) {
			new UpdateEntry(mCompositeEntry).run();
		}
	}
}
