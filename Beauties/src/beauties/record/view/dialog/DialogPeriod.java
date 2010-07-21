package beauties.record.view.dialog;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import beauties.model.DateRange;


public class DialogPeriod extends Dialog {
	private DateTime mDateTimeFrom;
	private DateTime mDateTimeTo;
	private Date mStartDate;
	private Date mEndDate;

	private DateRange mDateRange;
	
	public DialogPeriod(Shell parentShell, DateRange pDateRange) {
		super(parentShell);
		mDateRange = pDateRange;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite wComp = (Composite) super.createDialogArea(parent);
		wComp.setLayout(new GridLayout(4, false));

		Label wLabel1 = new Label(wComp, SWT.NONE);
		wLabel1.setText("期間");

		mDateTimeFrom = new DateTime(wComp, SWT.DATE | SWT.BORDER);
		Calendar wCal = Calendar.getInstance();
		wCal.setTime(mDateRange.getStartDate());
		mDateTimeFrom.setDate(wCal.get(Calendar.YEAR),
				wCal.get(Calendar.MONTH), wCal.get(Calendar.DAY_OF_MONTH));

		Label wLabel2 = new Label(wComp, SWT.NONE);
		wLabel2.setText(" ~ ");

		mDateTimeTo = new DateTime(wComp, SWT.DATE | SWT.BORDER);
		wCal.setTime(mDateRange.getEndDate());
		mDateTimeTo.setDate(wCal.get(Calendar.YEAR), wCal.get(Calendar.MONTH),
				wCal.get(Calendar.DAY_OF_MONTH));

		return wComp;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(318, 126);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("期間設定");
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "登録", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "キャンセル", false);
	}

	@Override
	protected void buttonPressed(int pButtonId) {
		int wReturnCode = IDialogConstants.CANCEL_ID;
		if (pButtonId == IDialogConstants.OK_ID) { // 0
			mStartDate = (new GregorianCalendar(mDateTimeFrom.getYear(),
					mDateTimeFrom.getMonth(), mDateTimeFrom.getDay()))
					.getTime();
			mEndDate = (new GregorianCalendar(mDateTimeTo.getYear(),
					mDateTimeTo.getMonth(), mDateTimeTo.getDay()))
					.getTime();
			if (mStartDate.after(mEndDate)) {
				MessageDialog.openWarning(getShell(), "エラー", "不正な期間です");
				setReturnCode(IDialogConstants.CANCEL_ID);
				open();
			} else {
				wReturnCode = pButtonId;
				setReturnCode(pButtonId);
			}
		} else { // 1
			setReturnCode(pButtonId);
		}
		super.buttonPressed(wReturnCode);
	}

	public Date getStartDate() {
		return mStartDate;
	}
	public Date getEndDate() {
		return mEndDate;
	}
}
