package model.action;

import model.SystemData;
import model.db.DbUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import view.entry.CompositeEntry;

public class DeleteRecord extends Action {

	private int mActId;
	private CompositeEntry mCompositeEntry;

	public DeleteRecord(int pActId, CompositeEntry pCompositeEntry) {
		super.setText("削除");
		mActId = pActId;
		mCompositeEntry = pCompositeEntry;
	}

	@Override
	public void run() {
		if(mCompositeEntry.getSelectedActId() == SystemData.getUndefinedInt())
			return;
		if (MessageDialog.openConfirm(mCompositeEntry.getShell(), "確認",
				"削除していいですか？")) {
			DbUtil.deleteRecord(mActId);
			new UpdateEntry(mCompositeEntry).run();
		}
	}
}
