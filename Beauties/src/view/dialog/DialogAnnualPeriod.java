package view.dialog;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import model.SystemData;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import util.Util;

public class DialogAnnualPeriod extends Dialog {

	private Spinner mStartYearSpinner;
	private Spinner mStartMonthSpinner;
	private Spinner mEndYearSpinner;
	private Spinner mEndMonthSpinner;
	private Date mStartDate;
	private Date mEndDate;

	public DialogAnnualPeriod(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite wComp = (Composite) super.createDialogArea(parent);
		wComp.setLayout(new GridLayout(9, false));

		Label wLabel1 = new Label(wComp, SWT.NONE);
		wLabel1.setText("期間");

		mStartYearSpinner = new Spinner(wComp, SWT.BORDER);
		mStartYearSpinner.setValues(0, 0, 9999, 0, 1, 10);
		Label wLabel = new Label(wComp, SWT.NONE);
		wLabel.setText("年");

		mStartMonthSpinner = new Spinner(wComp, SWT.BORDER);
		mStartMonthSpinner.setValues(0, 1, 12, 0, 1, 10);
		wLabel = new Label(wComp, SWT.NONE);
		wLabel.setText("月 〜 ");

		Calendar wCal = Calendar.getInstance();
		wCal.setTime(Util.getPeriod(SystemData.getStartDate())[1]);
		mStartYearSpinner.setSelection(wCal.get(Calendar.YEAR));
		mStartYearSpinner.pack();
		mStartMonthSpinner.setSelection(wCal.get(Calendar.MONTH) + 1);

		mEndYearSpinner = new Spinner(wComp, SWT.BORDER);
		mEndYearSpinner.setValues(0, 0, 9999, 0, 1, 10);
		wLabel = new Label(wComp, SWT.NONE);
		wLabel.setText("年");

		mEndMonthSpinner = new Spinner(wComp, SWT.BORDER);
		mEndMonthSpinner.setValues(0, 1, 12, 0, 1, 12);
		wLabel = new Label(wComp, SWT.NONE);
		wLabel.setText("月");

		wCal.setTime(SystemData.getEndDate());
		mEndYearSpinner.setSelection(wCal.get(Calendar.YEAR));
		mEndYearSpinner.pack();
		mEndMonthSpinner.setSelection(wCal.get(Calendar.MONTH) + 1);

		return wComp;
	}

//	@Override
//	protected Point getInitialSize() {
//		return new Point(318, 126);
//	}

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
			Date wInputStartDate = new GregorianCalendar(mStartYearSpinner
					.getSelection(), mStartMonthSpinner.getSelection() - 1, 1)
					.getTime();
			Date wInputEndDate = new GregorianCalendar(mEndYearSpinner
					.getSelection(), mEndMonthSpinner.getSelection() - 1, 1)
					.getTime();
			mStartDate = Util.getPeriod(wInputStartDate)[0];
			mEndDate = Util.getPeriod(wInputEndDate)[1];
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
