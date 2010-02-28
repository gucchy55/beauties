package view.config;

import model.db.DbUtil;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import view.util.MyFillLayout;
import view.util.MyGridData;
import view.util.MyGridLayout;
import view.util.MyRowLayout;

class PreferencePageItem extends PreferencePage {
	
	private static final int mRightHint = 150;
	
//	private static final String[] mButtonNames = {"分類追加", "項目追加", "削除", "↑", "↓"};

	public PreferencePageItem() {
		setTitle("項目設定");
	}
	
	protected Control createContents(Composite parent) {
		Composite wMainComposite = new Composite(parent, SWT.NONE);
		
		wMainComposite.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, false).getMyGridData();
		wMainComposite.setLayoutData(wGridData);

		Composite wTopComposite = new Composite(wMainComposite, SWT.NONE);
		wTopComposite.setLayout(new MyRowLayout().getMyRowLayout());
		wGridData.horizontalSpan = 2;
		wTopComposite.setLayoutData(wGridData);
		
		Button wCategoryAddButton = new Button(wTopComposite, SWT.NULL);
		wCategoryAddButton.setText("分類追加");

		Button wItemAddButton = new Button(wTopComposite, SWT.NULL);
		wItemAddButton.setText("項目追加");

		Button wDeleteButton = new Button(wTopComposite, SWT.NULL);
		wDeleteButton.setText("削除");

		Button wUpButton = new Button(wTopComposite, SWT.NULL);
		wUpButton.setText("↑");

		Button wDownButton = new Button(wTopComposite, SWT.NULL);
		wDownButton.setText("↓");
		
		// TreeViewer
		Composite wTreeComposite = new Composite(wMainComposite, SWT.BORDER);
		wTreeComposite.setLayout(new MyFillLayout(SWT.VERTICAL).getMyFillLayout());
		wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData();
		wTreeComposite.setLayoutData(wGridData);
	
		TreeViewerConfigItem wTreeViewer = new TreeViewerConfigItem(wTreeComposite, DbUtil.getRootConfigItem());
		
//		TreeViewerConfigItem wTreeViewerIncome = new TreeViewerConfigItem(wTreeComposite, DbUtil.getConfigItem(true));
//		TreeViewerConfigItem wTreeViewerExpense = new TreeViewerConfigItem(wTreeComposite, DbUtil.getConfigItem(false));

		
		// 関連付け
		Composite wAttributeComposite = new Composite(wMainComposite, SWT.BORDER);
		wAttributeComposite.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		wGridData = new MyGridData(GridData.FILL, GridData.FILL, false, true).getMyGridData();
		wGridData.widthHint = mRightHint;
		wAttributeComposite.setLayoutData(wGridData);
		
		return wMainComposite;
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
	
	protected void performDefaults() {
		
	}
	
	
}
