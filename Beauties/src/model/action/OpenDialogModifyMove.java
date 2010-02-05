package model.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import view.dialog.DialogMove;
import view.entry.CompositeEntry;

public class OpenDialogModifyMove extends Action {
	
//	private Shell mShell;
	private int mActId;
	private CompositeEntry mCompositeEntry;
	
	public OpenDialogModifyMove(CompositeEntry pCompositeEntry) {
//		mShell = pShell;
		mCompositeEntry = pCompositeEntry;
		mActId = mCompositeEntry.getSelectedActId();		
	}
	
	@Override
	public void run() {
//		if (SystemData.getRightType() == RightType.Main) {
			DialogMove wDialogMove = new DialogMove(mCompositeEntry.getShell(), mActId);
			int wRet = wDialogMove.open();
			
			if (wRet == IDialogConstants.OK_ID) {
				new UpdateEntry(mCompositeEntry).run();
			}
//		}
	}
}
