package view.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.ConfigItem;
import model.db.DbUtil;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import view.util.MyFillLayout;
import view.util.MyGridData;
import view.util.MyGridLayout;
import view.util.MyRowLayout;

class PreferencePageItem extends PreferencePage {

	private static final int mRightHint = 150;

	private Composite mMainComposite;
	private Composite mTreeComposite;
	private TreeViewerConfigItem mTreeViewerConfigItem;
	private ConfigItem mRootConfigItem;
	private boolean mTreeOrderChanged = false;

	private Composite mAttributeComposite;

	private Map<Button, Integer> mBookButtonMap;

	private Button mSpecialIncomeExpenseButton;
	private Button mTempIncomeExpenseButton;

	protected PreferencePageItem() {
		setTitle("項目設定");
	}

	protected Control createContents(Composite parent) {
		mMainComposite = new Composite(parent, SWT.NONE);

		mMainComposite.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, false)
				.getMyGridData();
		mMainComposite.setLayoutData(wGridData);

		Composite wTopComposite = new Composite(mMainComposite, SWT.NONE);
		wTopComposite.setLayout(new MyRowLayout().getMyRowLayout());
		wGridData.horizontalSpan = 2;
		wTopComposite.setLayoutData(wGridData);

		Button wCategoryAddButton = new Button(wTopComposite, SWT.NULL);
		wCategoryAddButton.setText("分類追加");
		wCategoryAddButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// MessageDialog.openConfirm(getShell(), "分類追加", "分類追加です");
				if (new DialogNewItem(getShell(), true).open() == 0) {
					updateTree();
				}
			}
		});

		Button wItemAddButton = new Button(wTopComposite, SWT.NULL);
		wItemAddButton.setText("項目追加");
		wItemAddButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (new DialogNewItem(getShell(), false).open() == 0) {
					updateTree();
				}
			}
		});

		Button wModifyButton = new Button(wTopComposite, SWT.NULL);
		wModifyButton.setText("変更");
		wModifyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ConfigItem wConfigItem = mTreeViewerConfigItem.getSelectedConfigItem();
				if (!wConfigItem.isSpecial()) {
					if (new DialogNewItem(getShell(), wConfigItem).open() == 0) {
						updateTree();
					}
				}
			}
		});

		Button wDeleteButton = new Button(wTopComposite, SWT.NULL);
		wDeleteButton.setText("削除");
		wDeleteButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ConfigItem wConfigItem = mTreeViewerConfigItem.getSelectedConfigItem();
				if (!wConfigItem.isSpecial()) {
					if (MessageDialog.openConfirm(getShell(), "削除", wConfigItem.getName()
							+ " - 本当に削除しますか？")) {
						DbUtil.deleteCategoryItem(wConfigItem);
						updateTree();
					}
				}
			}
		});

		Button wUpButton = new Button(wTopComposite, SWT.NULL);
		wUpButton.setText("↑");
		wUpButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ConfigItem wSelectedItem = mTreeViewerConfigItem.getSelectedConfigItem();
				wSelectedItem.moveUp();
				mTreeOrderChanged = true;
				updateTreeOrder();
			}
		});

		Button wDownButton = new Button(wTopComposite, SWT.NULL);
		wDownButton.setText("↓");
		wDownButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ConfigItem wSelectedItem = mTreeViewerConfigItem.getSelectedConfigItem();
				wSelectedItem.moveDown();
				mTreeOrderChanged = true;
				updateTreeOrder();
			}
		});

		// TreeViewer
		mTreeComposite = new Composite(mMainComposite, SWT.BORDER);
		mTreeComposite.setLayout(new MyFillLayout(SWT.VERTICAL).getMyFillLayout());
		wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData();
		mTreeComposite.setLayoutData(wGridData);

		mRootConfigItem = DbUtil.getRootConfigItem();
		mTreeViewerConfigItem = new TreeViewerConfigItem(mTreeComposite, mRootConfigItem);

		initAttributeComposite();
		addSelectionListenerToTree();

		return mMainComposite;
	}

	private void initAttributeComposite() {
		// 関連付け
		mAttributeComposite = new Composite(mMainComposite, SWT.BORDER);
		mAttributeComposite.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.FILL, GridData.FILL, false, true)
				.getMyGridData();
		wGridData.widthHint = mRightHint;
		mAttributeComposite.setLayoutData(wGridData);

		Label wLabel = new Label(mAttributeComposite, SWT.NONE);
		wLabel.setText("関連付け");

		Map<Integer, String> wBookNameMap = DbUtil.getBookNameMap();
		mBookButtonMap = new LinkedHashMap<Button, Integer>();
		for (Map.Entry<Integer, String> entry : wBookNameMap.entrySet()) {
			Button wButton = new Button(mAttributeComposite, SWT.CHECK);
			wButton.setText(entry.getValue());
			wButton.setVisible(false);
			mBookButtonMap.put(wButton, entry.getKey());

			wButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					ConfigItem wSelectedItem = mTreeViewerConfigItem.getSelectedConfigItem();
					Button wButton = (Button) e.getSource();
					DbUtil.updateItemRelation(wSelectedItem.getId(), mBookButtonMap.get(wButton),
							wButton.getSelection());
				}
			});
		}

		Label wSpaceLabel = new Label(mAttributeComposite, SWT.NONE);
		wSpaceLabel.setText("");

		Label wSpecialAttributeLabel = new Label(mAttributeComposite, SWT.NONE);
		wSpecialAttributeLabel.setText("特別収支系設定");

		mSpecialIncomeExpenseButton = new Button(mAttributeComposite, SWT.CHECK);
		mSpecialIncomeExpenseButton.setText("特別収支");
		mSpecialIncomeExpenseButton.setVisible(false);
		mSpecialIncomeExpenseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ConfigItem wSelectedItem = mTreeViewerConfigItem.getSelectedConfigItem();
				if (wSelectedItem.isCategory()) {
					Button wButton = (Button) e.getSource();
					DbUtil.updateSpecialCategory(wSelectedItem.getId(), wButton.getSelection());
				}
			}
		});

		mTempIncomeExpenseButton = new Button(mAttributeComposite, SWT.CHECK);
		mTempIncomeExpenseButton.setText("立替収支");
		mTempIncomeExpenseButton.setVisible(false);
		mTempIncomeExpenseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ConfigItem wSelectedItem = mTreeViewerConfigItem.getSelectedConfigItem();
				if (wSelectedItem.isCategory()) {
					Button wButton = (Button) e.getSource();
					DbUtil.updateTempCategory(wSelectedItem.getId(), wButton.getSelection());
				}
			}
		});
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

	private void addSelectionListenerToTree() {
		mTreeViewerConfigItem.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				ConfigItem wConfigItem = mTreeViewerConfigItem.getSelectedConfigItem();
				updateAttributeButtons(wConfigItem);
			}
		});
	}

	private void updateTreeOrder() {
		mTreeViewerConfigItem.getTree().setRedraw(false);

		try {
			Object[] elements = mTreeViewerConfigItem.getExpandedElements();
			ISelection selection = mTreeViewerConfigItem.getSelection();

			mTreeViewerConfigItem.getTree().dispose();
			mTreeViewerConfigItem = new TreeViewerConfigItem(mTreeComposite, mRootConfigItem);
			mTreeComposite.layout();

			addSelectionListenerToTree();

			mTreeViewerConfigItem.setExpandedElements(elements);
			mTreeViewerConfigItem.setSelection(selection);

		} finally {
			mTreeViewerConfigItem.getTree().setRedraw(true);
		}

	}

	private void updateTree() {
		mRootConfigItem = DbUtil.getRootConfigItem();
		mTreeViewerConfigItem.getTree().dispose();
		mTreeViewerConfigItem = new TreeViewerConfigItem(mTreeComposite, mRootConfigItem);
		mTreeComposite.layout();
		DbUtil.updateSortKeys(mRootConfigItem);
		updateAttributeButtons(mTreeViewerConfigItem.getSelectedConfigItem());
		mTreeOrderChanged = false;
	}

	private void updateAttributeButtons(ConfigItem pConfigItem) {
		if (pConfigItem.isSpecial() || pConfigItem.isCategory()) {
			for (Map.Entry<Button, Integer> entry : mBookButtonMap.entrySet()) {
				Button wButton = entry.getKey();
				wButton.setVisible(false);
			}
			if (pConfigItem.isCategory()) {
				mSpecialIncomeExpenseButton.setSelection(DbUtil.getSpecialCategoryIdList()
						.contains(pConfigItem.getId()));
				mTempIncomeExpenseButton.setSelection(DbUtil.getTempCategoryIdList().contains(
						pConfigItem.getId()));
			}

		} else {
			List<Integer> wBookIdList = DbUtil.getRelatedBookIdList(pConfigItem);
			for (Map.Entry<Button, Integer> entry : mBookButtonMap.entrySet()) {
				Button wButton = entry.getKey();
				wButton.setVisible(true);
				wButton.setSelection(wBookIdList.contains(entry.getValue()));
			}
		}

		mSpecialIncomeExpenseButton.setVisible(pConfigItem.isCategory());
		mTempIncomeExpenseButton.setVisible(pConfigItem.isCategory());

	}

}
