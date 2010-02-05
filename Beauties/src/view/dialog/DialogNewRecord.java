package view.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DialogNewRecord extends Dialog {

	private CompositeRecord mCompositeRecord;
	private int mReturnCode = IDialogConstants.CANCEL_ID;
	
	private int mBookId;
	
	public DialogNewRecord(Shell parentShell, int pBookId) {
		super(parentShell);
		mBookId = pBookId;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		mCompositeRecord = new CompositeRecord(parent, mBookId, true);
		return mCompositeRecord;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 380);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("追加");
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.NEXT_ID, "続けて入力", false);
		createButton(parent, IDialogConstants.OK_ID, "登録", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "キャンセル", false);
	}

	@Override
	protected void buttonPressed(int pButtonId) {
		if (pButtonId == IDialogConstants.NEXT_ID) {	//15

			if (registerRecord()) {
				mCompositeRecord.updateForNextInput();
				this.open();
			} else {
				this.open();
			}

		} else if (pButtonId == IDialogConstants.OK_ID) {	//0
			if (registerRecord()) {
				setReturnCode(mReturnCode);
				close();
			} else {
				this.open();
			}
		} else if (pButtonId == IDialogConstants.CANCEL_ID){	//1
			setReturnCode(mReturnCode);
			close();
		}
		super.buttonPressed(mReturnCode);
	}

	private boolean registerRecord() {
		if (mCompositeRecord.getValue() > 0
				|| (mCompositeRecord.getValue() == 0 && MessageDialog
						.openConfirm(getShell(), "確認", "金額が0ですが、いいですか？"))) {
			mReturnCode = IDialogConstants.OK_ID;
			mCompositeRecord.insertRecord();
			return true;
		} else {
			return false;
		}
	}
	
	public int getBookId() {
		return mBookId;
	}

}
