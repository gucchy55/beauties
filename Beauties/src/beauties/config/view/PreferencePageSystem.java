package beauties.config.view;


import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import beauties.model.SystemData;
import beauties.model.db.DbUtil;

import util.view.MyGridLayout;

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

		// 締め日
		new Label(wMainComposite, SWT.NONE).setText("締め日: ");
		mCutOffSpinner = new Spinner(wMainComposite, SWT.BORDER);
		mCutOffSpinner.setValues(SystemData.getCutOff(), 1, 31, 0, 1, 10);
		mCutOffSpinner.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent event) {
				getShell().setImeInputMode(SWT.NONE);
			}

			public void focusLost(FocusEvent event) {
			}
		});
		new Label(wMainComposite, SWT.NONE);

		// 年度開始月
		new Label(wMainComposite, SWT.NONE).setText("年度期間: ");
		mAnnualStartSpinner = new Spinner(wMainComposite, SWT.BORDER);
		mAnnualStartSpinner.setValues(DbUtil.getFisCalMonth(), 1, 12, 0, 1, 10);
		mAnnualStartSpinner.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent event) {
				getShell().setImeInputMode(SWT.NONE);
			}

			public void focusLost(FocusEvent event) {
			}
		});
		new Label(wMainComposite, SWT.NONE).setText(" ～ 12月");

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

		return wMainComposite;
	}

	protected void performApply() {
		if (!this.isControlCreated())
			return;
		DbUtil.updateCutOff(Integer.parseInt(mCutOffSpinner.getText()));
		DbUtil.updateFisCalMonth(Integer.parseInt(mAnnualStartSpinner.getText()));
		DbUtil.updateShowGridLine(mLineGridCheckButton.getSelection());
	}

	public boolean performOk() {
		performApply();
		return true;
	}

}
