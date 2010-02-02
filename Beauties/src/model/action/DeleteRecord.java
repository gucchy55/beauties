package model.action;

import model.db.DbUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import view.entry.CompositeEntry;

public class DeleteRecord extends Action {

//	private Shell mShell;
	private int mActId;
	private CompositeEntry mCompositeEntry;

	public DeleteRecord(int pActId, CompositeEntry pCompositeEntry) {
		super.setText("削除");
		mActId = pActId;
		mCompositeEntry = pCompositeEntry;
//		mShell = pShell;
	}

	@Override
	public void run() {
		if (MessageDialog.openConfirm(mCompositeEntry.getShell(), "確認",
				"削除していいですか？")) {
			DbUtil.deleteRecord(mActId);
//			new UpdateEntry(SystemData.getCompositeRightMain()).run();
			new UpdateEntry(mCompositeEntry).run();
		}
	}
}
