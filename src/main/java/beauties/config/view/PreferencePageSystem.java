package beauties.config.view;


import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import beauties.common.lib.DbUtil;
import beauties.common.lib.SystemData;
import beauties.common.view.MyGridLayout;


class PreferencePageSystem extends PreferencePage {

	private Spinner mCutOffSpinner;
	private Spinner mAnnualStartSpinner;
	private Button mLineGridCheckButton;

	protected PreferencePageSystem() {
		setTitle("その他");
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite wMainComposite = new Composite(parent, SWT.NONE);
		wMainComposite.setLayout(new MyGridLayout(3, false).getMyGridLayout());

		createCutoffSpinner(wMainComposite);

		new Label(wMainComposite, SWT.NONE);

		createFiscalStartSpinner(wMainComposite);

		createGridLineCheckButton(wMainComposite);

		return wMainComposite;
	}

	private void createCutoffSpinner(Composite wMainComposite) {
		new Label(wMainComposite, SWT.NONE).setText("締め日: ");
		mCutOffSpinner = new Spinner(wMainComposite, SWT.BORDER);
		mCutOffSpinner.setValues(SystemData.getCutOff(), 1, 31, 0, 1, 10);
//		mCutOffSpinner.addFocusListener(new FocusListener() {
//			public void focusGained(FocusEvent event) {
//				getShell().setImeInputMode(SWT.NONE);
//			}
//
//			public void focusLost(FocusEvent event) {
//			}
//		});
	}

	private void createFiscalStartSpinner(Composite wMainComposite) {
		new Label(wMainComposite, SWT.NONE).setText("年度期間: ");
		mAnnualStartSpinner = new Spinner(wMainComposite, SWT.BORDER);
		mAnnualStartSpinner.setValues(DbUtil.getFisCalMonth(), 1, 12, 0, 1, 10);
//		mAnnualStartSpinner.addFocusListener(Util.getFocusListenerToDisableIme(getShell(), SWT.NONE));
		new Label(wMainComposite, SWT.NONE).setText(" ～ 12月");
	}

	private void createGridLineCheckButton(Composite wMainComposite) {
		// チェックボックス用GridData
		GridData wGridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
		wGridData.horizontalSpan = 3;

		Label wLabelForSplit = new Label(wMainComposite, SWT.NONE);
		wLabelForSplit.setLayoutData(wGridData);

		// 罫線表示有無
		mLineGridCheckButton = new Button(wMainComposite, SWT.CHECK);
		mLineGridCheckButton.setSelection(SystemData.showGridLine());
		mLineGridCheckButton.setText("罫線を表示する");
		mLineGridCheckButton.setLayoutData(wGridData);
	}

	@Override
	protected void performApply() {
		if (!this.isControlCreated())
			return;
		DbUtil.updateCutOff(Integer.parseInt(mCutOffSpinner.getText()));
		DbUtil.updateFisCalMonth(Integer.parseInt(mAnnualStartSpinner.getText()));
		DbUtil.updateShowGridLine(mLineGridCheckButton.getSelection());
	}

	@Override
	public boolean performOk() {
		performApply();
		return true;
	}

}
