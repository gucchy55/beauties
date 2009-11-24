package model.action;

import model.SystemData;
import model.db.DbUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class DeleteRecord extends Action {

	private Shell mShell;
	private int mActId;

	public DeleteRecord(Shell pShell, int pActId) {
		super.setText("削除");
		mActId = pActId;
		mShell = pShell;
	}

	@Override
	public void run() {
		if (MessageDialog.openConfirm(mShell, "確認",
				"削除していいですか？")) {
			DbUtil.deleteRecord(mActId);
			new UpdateEntry(SystemData.getCompositeRightMain()).run();
		}
	}
}
