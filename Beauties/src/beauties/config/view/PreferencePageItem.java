package beauties.config.view;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
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

import beauties.common.lib.DbUtil;
import beauties.common.model.Book;
import beauties.common.view.MyFillLayout;
import beauties.common.view.MyGridData;
import beauties.common.view.MyGridLayout;
import beauties.common.view.MyRowLayout;
import beauties.config.model.ConfigItem;


class PreferencePageItem extends PreferencePage {

	private static final int mRightHint = 150;

	private Composite mMainComposite;
	private Composite mTreeComposite;
	private TreeViewerConfigItem mTreeViewerConfigItem;
	private ConfigItem mRootConfigItem;
	private boolean mTreeOrderChanged = false;

	private Composite mAttributeComposite;

	private Map<Button, Book> mBookButtonMap;

	private Button mSpecialIncomeExpenseButton;
	private Button mTempIncomeExpenseButton;

	protected PreferencePageItem() {
		setTitle("項目設定");
		mBookButtonMap = new LinkedHashMap<>();
	}

	@Override
	protected Control createContents(Composite parent) {
		mMainComposite = new Composite(parent, SWT.NONE);

		mMainComposite.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, false)
				.getMyGridData();
		mMainComposite.setLayoutData(wGridData);

		createTopComposite();

		createTreeComposite();

		initAttributeComposite();
		addSelectionListenerToTree();

		return mMainComposite;
	}

	private void createTopComposite() {
		Composite wTopComposite = new Composite(mMainComposite, SWT.NONE);
		wTopComposite.setLayout(new MyRowLayout().getMyRowLayout());
		GridData wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, false)
		.getMyGridData();
		wGridData.horizontalSpan = 2;
		wTopComposite.setLayoutData(wGridData);

		createNewCategoryButton(wTopComposite);

		createNewItemButton(wTopComposite);

		createModifyButton(wTopComposite);

		createDeleteButton(wTopComposite);

		createUpButton(wTopComposite);

		createDownButton(wTopComposite);
	}

	private void createNewCategoryButton(Composite wTopComposite) {
		Button wCategoryAddButton = new Button(wTopComposite, SWT.NULL);
		wCategoryAddButton.setText("分類追加");
		wCategoryAddButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (new DialogNewItem(getShell(), true).open() == 0) {
					updateTree();
				}
			}
		});
	}

	private void createNewItemButton(Composite wTopComposite) {
		Button wItemAddButton = new Button(wTopComposite, SWT.NULL);
		wItemAddButton.setText("項目追加");
		wItemAddButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (new DialogNewItem(getShell(), false).open() == 0) {
					updateTree();
				}
			}
		});
	}

	private void createModifyButton(Composite wTopComposite) {
		Button wModifyButton = new Button(wTopComposite, SWT.NULL);
		wModifyButton.setText("変更");
		wModifyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigItem wConfigItem = mTreeViewerConfigItem.getSelectedConfigItem();
				if (wConfigItem.isSpecial())
					return;
				if (new DialogNewItem(getShell(), wConfigItem).open() == 0) {
					updateTree();
				}
			}
		});
	}

	private void createDeleteButton(Composite wTopComposite) {
		Button wDeleteButton = new Button(wTopComposite, SWT.NULL);
		wDeleteButton.setText("削除");
		wDeleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigItem wConfigItem = mTreeViewerConfigItem.getSelectedConfigItem();
				if (wConfigItem.isSpecial())
					return;
				if (MessageDialog.openConfirm(getShell(), "削除", wConfigItem.getName()
							+ " - 本当に削除しますか？")) {
					DbUtil.deleteCategoryItem(wConfigItem);
					updateTree();
				}
			}
		});
	}

	private void createUpButton(Composite wTopComposite) {
		Button wUpButton = new Button(wTopComposite, SWT.NULL);
		wUpButton.setText("↑");
		wUpButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigItem wSelectedItem = mTreeViewerConfigItem.getSelectedConfigItem();
				wSelectedItem.moveUp();
				mTreeOrderChanged = true;
				mTreeViewerConfigItem.refresh();
			}
		});
	}

	private void createDownButton(Composite wTopComposite) {
		Button wDownButton = new Button(wTopComposite, SWT.NULL);
		wDownButton.setText("↓");
		wDownButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigItem wSelectedItem = mTreeViewerConfigItem.getSelectedConfigItem();
				wSelectedItem.moveDown();
				mTreeOrderChanged = true;
				mTreeViewerConfigItem.refresh();
			}
		});
	}

	private void createTreeComposite() {
		GridData wGridData;
		// TreeViewer
		mTreeComposite = new Composite(mMainComposite, SWT.BORDER);
		mTreeComposite.setLayout(new MyFillLayout(SWT.VERTICAL).getMyFillLayout());
		wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData();
		mTreeComposite.setLayoutData(wGridData);

		mRootConfigItem = DbUtil.getRootConfigItem();
		mTreeViewerConfigItem = new TreeViewerConfigItem(mTreeComposite, mRootConfigItem);
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

		createBookButtons();

		Label wSpaceLabel = new Label(mAttributeComposite, SWT.NONE);
		wSpaceLabel.setText("");

		Label wSpecialAttributeLabel = new Label(mAttributeComposite, SWT.NONE);
		wSpecialAttributeLabel.setText("特別収支系設定");

		createSpecialIncomeExpenseButton();

		createTempIncomeExpenseButton();
	}

	private void createBookButtons() {
		for (Book wBook : DbUtil.getBooks()) {
			Button wButton = new Button(mAttributeComposite, SWT.CHECK);
			wButton.setText(wBook.getName());
			wButton.setVisible(false);
			mBookButtonMap.put(wButton, wBook);

			wButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					ConfigItem wSelectedItem = mTreeViewerConfigItem.getSelectedConfigItem();
					Button wButton = (Button) e.getSource();
					DbUtil.updateItemRelation(wSelectedItem.getItem(), mBookButtonMap.get(wButton),
							wButton.getSelection());
				}
			});
		}
	}

	private void createSpecialIncomeExpenseButton() {
		mSpecialIncomeExpenseButton = new Button(mAttributeComposite, SWT.CHECK);
		mSpecialIncomeExpenseButton.setText("特別収支");
		mSpecialIncomeExpenseButton.setVisible(false);
		mSpecialIncomeExpenseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigItem wSelectedItem = mTreeViewerConfigItem.getSelectedConfigItem();
				if (wSelectedItem.isCategory()) {
					Button wButton = (Button) e.getSource();
					DbUtil.updateSpecialCategory(wSelectedItem.getCategory(), wButton.getSelection());
				}
			}
		});
	}

	private void createTempIncomeExpenseButton() {
		mTempIncomeExpenseButton = new Button(mAttributeComposite, SWT.CHECK);
		mTempIncomeExpenseButton.setText("立替収支");
		mTempIncomeExpenseButton.setVisible(false);
		mTempIncomeExpenseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigItem wSelectedItem = mTreeViewerConfigItem.getSelectedConfigItem();
				if (wSelectedItem.isCategory()) {
					Button wButton = (Button) e.getSource();
					DbUtil.updateTempCategory(wSelectedItem.getCategory(), wButton.getSelection());
				}
			}
		});
	}

	@Override
	protected void performApply() {
		if (mTreeOrderChanged) {
			DbUtil.updateSortKeys(mRootConfigItem);
			mTreeOrderChanged = false;
		}
		if (getControl() == null) {
			return;
		}
	}

	@Override
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

//	private void updateTreeOrder() {
//		mTreeViewerConfigItem.getTree().setRedraw(false);
//
//		try {
//			Object[] elements = mTreeViewerConfigItem.getExpandedElements();
//			ISelection selection = mTreeViewerConfigItem.getSelection();
//
////			mTreeViewerConfigItem.getTree().dispose();
////			mTreeViewerConfigItem = new TreeViewerConfigItem(mTreeComposite,
////					mRootConfigItem);
////			mTreeComposite.layout();
//
//			addSelectionListenerToTree();
//
//			mTreeViewerConfigItem.setExpandedElements(elements);
//			mTreeViewerConfigItem.setSelection(selection);
//
//		} finally {
//			mTreeViewerConfigItem.getTree().setRedraw(true);
//		}
//
//	}

	private void updateTree() {
		mRootConfigItem = DbUtil.getRootConfigItem();
		mTreeViewerConfigItem.setInput(mRootConfigItem);
		mTreeViewerConfigItem.setExpandedElements(mRootConfigItem.getChildren().toArray(new Object[0]));
		mTreeViewerConfigItem.refresh();
		DbUtil.updateSortKeys(mRootConfigItem);
		updateAttributeButtons(mTreeViewerConfigItem.getSelectedConfigItem());
		mTreeOrderChanged = false;
	}

	private void updateAttributeButtons(ConfigItem pConfigItem) {
		if (pConfigItem.isSpecial() || pConfigItem.isCategory()) {
			for (Map.Entry<Button, Book> entry : mBookButtonMap.entrySet()) {
				Button wButton = entry.getKey();
				wButton.setVisible(false);
			}
			if (pConfigItem.isCategory()) {
				mSpecialIncomeExpenseButton.setSelection(DbUtil.getSpecialCategorys()
						.contains(pConfigItem.getCategory()));
				mTempIncomeExpenseButton.setSelection(DbUtil.getTempCategoryList().contains(
						pConfigItem.getCategory()));
			}

		} else {
			List<Book> wBookList = DbUtil.getRelatedBookList(pConfigItem);
			for (Map.Entry<Button, Book> entry : mBookButtonMap.entrySet()) {
				Button wButton = entry.getKey();
				wButton.setVisible(true);
				wButton.setSelection(wBookList.contains(entry.getValue()));
			}
		}

		mSpecialIncomeExpenseButton.setVisible(pConfigItem.isCategory());
		mTempIncomeExpenseButton.setVisible(pConfigItem.isCategory());

	}

}
