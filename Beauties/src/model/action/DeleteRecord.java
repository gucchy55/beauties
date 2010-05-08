package model.action;

import model.db.DbUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import view.entry.CompositeEntry;

public class DeleteRecord extends Action {

	private CompositeEntry mCompositeEntry;

	public DeleteRecord(CompositeEntry pCompositeEntry) {
		super.setText("削除");
		mCompositeEntry = pCompositeEntry;
	}

	@Override
	public void run() {
		if(!mCompositeEntry.hasSelectedRecordTableItem())
			return;
		if (MessageDialog.openConfirm(mCompositeEntry.getShell(), "確認",
				"削除していいですか？")) {
			DbUtil.deleteRecord(mCompositeEntry.getSelectedRecordItem());
			new UpdateEntry(mCompositeEntry).run();
		}
	}
}
