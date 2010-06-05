package beauties.record.view.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import beauties.record.model.RecordTableItem;

public class DialogMove extends Dialog {

	private CompositeMove mCompositeMove;
	private int mBookId;
	private RecordTableItem mRecordTableItem;
	
	public DialogMove(Shell parentShell, int pBookId) {
		super(parentShell);
		mBookId = pBookId;
	}
	
	public DialogMove(Shell parentShell, RecordTableItem pRecordTableItem) {
		super(parentShell);
		mRecordTableItem = pRecordTableItem;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		if (mRecordTableItem == null) {
			mCompositeMove = new CompositeMove(parent, mBookId);
			return mCompositeMove;
		} else {
			mCompositeMove = new CompositeMove(parent, mRecordTableItem);
			return mCompositeMove;
		}
	}

	@Override
	protected Point getInitialSize() {
		return new Point(363, 300);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("現金移動");
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "登録", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "キャンセル", false);
	}

	@Override
	protected void buttonPressed(int pButtonId) {
		if (pButtonId == IDialogConstants.OK_ID) {
			if (mCompositeMove.isValidInput()) {
				setReturnCode(pButtonId);
				mCompositeMove.insertRecord();
				close();
			} else {
				MessageDialog.openWarning(getShell(), "Error", "金額が0、または入出力帳簿が同じです");
				open();
			}
		} else {
			setReturnCode(pButtonId);
			close();
		}
		super.buttonPressed(pButtonId);
	}
	
}