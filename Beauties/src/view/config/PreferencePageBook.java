package view.config;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

class PreferencePageBook extends PreferencePage{

	public PreferencePageBook() {
		setTitle("帳簿設定");
		setMessage("メッセージ");
	}
	
	protected Control createContents(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, true));
		new Label(c, SWT.NONE).setText("パラメータ２：");
//		Text text = new Text(c, SWT.SINGLE | SWT.BORDER);
		return c;
	}

	protected void performApply() {
		if (getControl() == null) {
			return;
		}
	}

	public boolean performOk() {
		performApply();
		return true;
	}
	
}
