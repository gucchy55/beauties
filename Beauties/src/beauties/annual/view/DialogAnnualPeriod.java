package beauties.annual.view;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

import beauties.annual.AnnualController;
import beauties.common.lib.SystemData;
import beauties.common.lib.Util;
import beauties.common.model.DateRange;
import beauties.common.view.MyGridData;
import beauties.common.view.MyGridLayout;

public class DialogAnnualPeriod extends Dialog {

	private Spinner mStartYearSpinner;
	private Combo mStartMonthCombo;
	private Spinner mEndYearSpinner;
	private Combo mEndMonthCombo;
	private DateRange mDateRange;

	private AnnualController mCTL;

	public DialogAnnualPeriod(AnnualController pCTL) {
		super(pCTL.getShell());
		mCTL = pCTL;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite wComp = (Composite) super.createDialogArea(parent);
		wComp.setLayout(new MyGridLayout(9, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.BEGINNING, GridData.FILL, false, true)
				.getMyGridData();
		wComp.setLayoutData(wGridData);

		Label wLabel1 = new Label(wComp, SWT.NONE);
		wLabel1.setText("期間");

		createStartMonth(wComp);

		Label wLabel = new Label(wComp, SWT.NONE);
		wLabel.setText("月 〜 ");

		createEndMonth(wComp);
		
		wLabel = new Label(wComp, SWT.NONE);
		wLabel.setText("月");

		return wComp;
	}

	private void createStartMonth(Composite pComp) {
		mStartYearSpinner = new Spinner(pComp, SWT.BORDER);
		mStartYearSpinner.setValues(0, 0, 9999, 0, 1, 10);
		Label wLabel = new Label(pComp, SWT.NONE);
		wLabel.setText("年");

		mStartMonthCombo = new Combo(pComp, SWT.BORDER | SWT.READ_ONLY);
		for (int i = 0; i < 12; i++) {
			mStartMonthCombo.add(Integer.toString(i + 1));
		}
		
		Calendar wCal = Calendar.getInstance();
		wCal.setTime(Util.getMonthDateRange(mCTL.getAnnualDateRange().getStartDate(),
				SystemData.getCutOff()).getEndDate());
		mStartYearSpinner.setSelection(wCal.get(Calendar.YEAR));
		mStartMonthCombo.select(wCal.get(Calendar.MONTH));
	}
	
	private void createEndMonth(Composite pComp) {
		mEndYearSpinner = new Spinner(pComp, SWT.BORDER);
		mEndYearSpinner.setValues(0, 0, 9999, 0, 1, 10);
		Label wLabel = new Label(pComp, SWT.NONE);
		wLabel.setText("年");

		mEndMonthCombo = new Combo(pComp, SWT.BORDER | SWT.READ_ONLY);
		for (int i = 0; i < 12; i++) {
			mEndMonthCombo.add(Integer.toString(i + 1));
		}
		Calendar wCal = Calendar.getInstance();
		wCal.setTime(mCTL.getAnnualDateRange().getEndDate());
		mEndYearSpinner.setSelection(wCal.get(Calendar.YEAR));
		mEndMonthCombo.select(wCal.get(Calendar.MONTH));
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
			Date wInputStartDate = new GregorianCalendar(mStartYearSpinner.getSelection(),
					mStartMonthCombo.getSelectionIndex(), 1).getTime();
			Date wInputEndDate = new GregorianCalendar(mEndYearSpinner.getSelection(),
					mEndMonthCombo.getSelectionIndex(), 1).getTime();
			mDateRange = new DateRange(Util.getMonthDateRange(wInputStartDate,
					SystemData.getCutOff()).getStartDate(), Util.getMonthDateRange(wInputEndDate,
					SystemData.getCutOff()).getEndDate());
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

	public DateRange getDateRange() {
		return mDateRange;
	}
}
