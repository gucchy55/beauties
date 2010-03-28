package view.config;

import model.ConfigItem;
import model.db.DbUtil;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

	private Composite mTreeComposite;
	private TreeViewerConfigItem mTreeViewerConfigItem;
	private ConfigItem mRootConfigItem;
	private boolean mTreeOrderChanged = false;
	
	private Composite mAttributeComposite;

	// private static final String[] mButtonNames = {"分類追加", "項目追加", "削除", "↑",
	// "↓"};

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

		Button wModifyButton = new Button(wTopComposite, SWT.NULL);
		wModifyButton.setText("変更");
		wModifyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (mTreeViewerConfigItem.getSelectedConfigItem() != null)
					System.out.println(mTreeViewerConfigItem.getSelectedConfigItem().getName());
			}
		});

		Button wDeleteButton = new Button(wTopComposite, SWT.NULL);
		wDeleteButton.setText("削除");

		Button wUpButton = new Button(wTopComposite, SWT.NULL);
		wUpButton.setText("↑");
		wUpButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ConfigItem wSelectedItem = mTreeViewerConfigItem.getSelectedConfigItem();
				if (wSelectedItem != null) {
					wSelectedItem.moveUp();
					mTreeOrderChanged = true;
					updateTree();
				}
			}
		});

		Button wDownButton = new Button(wTopComposite, SWT.NULL);
		wDownButton.setText("↓");
		wDownButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ConfigItem wSelectedItem = mTreeViewerConfigItem.getSelectedConfigItem();
				if (wSelectedItem != null) {
					wSelectedItem.moveDown();
					mTreeOrderChanged = true;
					updateTree();
				}
			}
		});

		// TreeViewer
		mTreeComposite = new Composite(wMainComposite, SWT.BORDER);
		mTreeComposite.setLayout(new MyFillLayout(SWT.VERTICAL).getMyFillLayout());
		wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData();
		mTreeComposite.setLayoutData(wGridData);

		mRootConfigItem = DbUtil.getRootConfigItem();
		mTreeViewerConfigItem = new TreeViewerConfigItem(mTreeComposite, mRootConfigItem);

		// 関連付け
		mAttributeComposite = new Composite(wMainComposite, SWT.BORDER);
		mAttributeComposite.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		wGridData = new MyGridData(GridData.FILL, GridData.FILL, false, true).getMyGridData();
		wGridData.widthHint = mRightHint;
		mAttributeComposite.setLayoutData(wGridData);

		return wMainComposite;
	}

	protected void performApply() {
		if (mTreeOrderChanged) {
			DbUtil.updateSortKeys(mRootConfigItem);
			mTreeOrderChanged = false;
		}
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

//	private void addNewCategory() {
//
//	}

	private void updateTree() {
		mTreeViewerConfigItem.getTree().setRedraw(true);

		try {
			Object[] elements = mTreeViewerConfigItem.getExpandedElements();
			ISelection selection = mTreeViewerConfigItem.getSelection();

			mTreeViewerConfigItem.getTree().dispose();
			mTreeViewerConfigItem = new TreeViewerConfigItem(mTreeComposite, mRootConfigItem);
			mTreeComposite.layout();

			mTreeViewerConfigItem.setExpandedElements(elements);
			mTreeViewerConfigItem.setSelection(selection);
		} finally {
			mTreeViewerConfigItem.getTree().setRedraw(true);
		}

	}


}
