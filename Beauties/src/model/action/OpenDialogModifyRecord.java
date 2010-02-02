package model.action;

import model.RightType;
import model.SystemData;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import view.dialog.DialogModifyRecord;
import view.entry.CompositeEntry;

public class OpenDialogModifyRecord extends Action {
	
//	private Shell mShell;
	private int mActId;
	private CompositeEntry mCompositeEntry;
	
	public OpenDialogModifyRecord(CompositeEntry pCompositeEntry) {
//		mShell = pShell;
		mCompositeEntry = pCompositeEntry;
		mActId = mCompositeEntry.getSelectedActId();
	}
	
	@Override
	public void run() {
		if (SystemData.getRightType() == RightType.Main) {
			DialogModifyRecord wDialogModifyRecord = new DialogModifyRecord(mCompositeEntry.getShell(), mActId);
			int wRet = wDialogModifyRecord.open();
			
			if (wRet == IDialogConstants.OK_ID) {
				new UpdateEntry(mCompositeEntry).run();
			}
		}
	}
}
