package view.dialog;

import model.RecordTableItem;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DialogModifyRecord extends Dialog {

	private CompositeRecord mCompositeRecord;
	private RecordTableItem mRecordTableItem;

	public DialogModifyRecord(Shell parentShell, RecordTableItem pRecordTableItem) {
		super(parentShell);
		mRecordTableItem = pRecordTableItem;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		mCompositeRecord = new CompositeRecord(parent, mRecordTableItem);
		return mCompositeRecord;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(300, 380);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("変更");
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "登録", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "キャンセル", false);
	}

	@Override
	protected void buttonPressed(int pButtonId) {
		if (pButtonId == IDialogConstants.OK_ID) {
			setReturnCode(pButtonId);
			mCompositeRecord.insertRecord();
			close();
		} else {
			setReturnCode(pButtonId);
			close();
		}
		super.buttonPressed(pButtonId);
	}

}
