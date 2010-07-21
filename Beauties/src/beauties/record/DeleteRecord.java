package beauties.record;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;

import beauties.model.db.DbUtil;

public class DeleteRecord extends Action {

	private RecordController mCtl;

	public DeleteRecord(RecordController pCtl) {
		super.setText("削除");
		mCtl = pCtl;
	}

	@Override
	public void run() {
		if(!mCtl.hasSelectedRecordTableItem())
			return;
		if (MessageDialog.openConfirm(mCtl.getShell(), "確認",
				"削除していいですか？")) {
			DbUtil.deleteRecord(mCtl.getSelectedRecordItem());
			mCtl.updateTable();
//			new UpdateEntry(mCtl).run();
		}
	}
}
