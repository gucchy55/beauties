package model.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import view.dialog.DialogMove;
import view.entry.CompositeEntry;

public class OpenDialogModifyMove extends Action {

	private CompositeEntry mCompositeEntry;

	public OpenDialogModifyMove(CompositeEntry pCompositeEntry) {
		mCompositeEntry = pCompositeEntry;
	}

	@Override
	public void run() {
		if (!mCompositeEntry.hasSelectedRecordTableItem())
			return;
		DialogMove wDialogMove = new DialogMove(mCompositeEntry.getShell(), mCompositeEntry.getSelectedRecordItem());
		int wRet = wDialogMove.open();

		if (wRet == IDialogConstants.OK_ID) {
			new UpdateEntry(mCompositeEntry).run();
		}
	}
}
