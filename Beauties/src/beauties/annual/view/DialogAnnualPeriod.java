package beauties.annual.view;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//import model.SystemData;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import beauties.model.DateRange;
import beauties.model.db.DbUtil;

import util.Util;
import util.view.MyGridData;
import util.view.MyGridLayout;

public class DialogAnnualPeriod extends Dialog {

	private Spinner mStartYearSpinner;
	private Combo mStartMonthCombo;
	private Spinner mEndYearSpinner;
	private Combo mEndMonthCombo;
//	private Date mStartDate;
//	private Date mEndDate;
	private DateRange mDateRange;

	private CompositeAnnualMain mCompositeAnnualMain;

	public DialogAnnualPeriod(Shell parentShell, CompositeAnnualMain pCompositeAnnualMain) {
		super(parentShell);
		mCompositeAnnualMain = pCompositeAnnualMain;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite wComp = (Composite) super.createDialogArea(parent);
		wComp.setLayout(new MyGridLayout(9, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.BEGINNING, GridData.FILL, false, true).getMyGridData();
		wComp.setLayoutData(wGridData);

		Label wLabel1 = new Label(wComp, SWT.NONE);
		wLabel1.setText("期間");

		mStartYearSpinner = new Spinner(wComp, SWT.BORDER);
		mStartYearSpinner.setValues(0, 0, 9999, 0, 1, 10);
		Label wLabel = new Label(wComp, SWT.NONE);
		wLabel.setText("年");

		mStartMonthCombo = new Combo(wComp, SWT.BORDER | SWT.READ_ONLY);
		for (int i = 0; i < 12; i++) {
			mStartMonthCombo.add(Integer.toString(i + 1));
		}

		wLabel = new Label(wComp, SWT.NONE);
		wLabel.setText("月 〜 ");

		Calendar wCal = Calendar.getInstance();
		wCal.setTime(Util.getMonthDateRange(mCompositeAnnualMain.getStartDate(), DbUtil.getCutOff()).getEndDate());
		mStartYearSpinner.setSelection(wCal.get(Calendar.YEAR));
		mStartMonthCombo.select(wCal.get(Calendar.MONTH));

		mEndYearSpinner = new Spinner(wComp, SWT.BORDER);
		mEndYearSpinner.setValues(0, 0, 9999, 0, 1, 10);
		wLabel = new Label(wComp, SWT.NONE);
		wLabel.setText("年");

		mEndMonthCombo = new Combo(wComp, SWT.BORDER | SWT.READ_ONLY);
		for (int i = 0; i < 12; i++) {
			mEndMonthCombo.add(Integer.toString(i + 1));
		}
		wLabel = new Label(wComp, SWT.NONE);
		wLabel.setText("月");

		wCal.setTime(mCompositeAnnualMain.getEndDate());
		mEndYearSpinner.setSelection(wCal.get(Calendar.YEAR));
		mEndMonthCombo.select(wCal.get(Calendar.MONTH));

		return wComp;
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
			Date wInputStartDate = new GregorianCalendar(mStartYearSpinner.getSelection(), mStartMonthCombo
					.getSelectionIndex(), 1).getTime();
			Date wInputEndDate = new GregorianCalendar(mEndYearSpinner.getSelection(), mEndMonthCombo
					.getSelectionIndex(), 1).getTime();
//			mStartDate = Util.getMonthDateRange(wInputStartDate, DbUtil.getCutOff()).getStartDate();
//			mEndDate = Util.getMonthDateRange(wInputEndDate, DbUtil.getCutOff()).getEndDate();
			mDateRange = new DateRange(Util.getMonthDateRange(wInputStartDate, DbUtil.getCutOff()).getStartDate(), Util.getMonthDateRange(wInputEndDate, DbUtil.getCutOff()).getEndDate());
			if (mDateRange.getStartDate().after(mDateRange.getEndDate())) {
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

//	public Date getStartDate() {
//		return mStartDate;
//	}
//
//	public Date getEndDate() {
//		return mEndDate;
//	}
	public DateRange getDateRange() {
		return mDateRange;
	}
}