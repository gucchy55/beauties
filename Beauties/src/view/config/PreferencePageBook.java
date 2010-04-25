package view.config;

import java.util.List;
import java.util.Map;

import model.Book;
import model.ConfigItem;
import model.db.DbUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import view.util.MyFillLayout;
import view.util.MyGridData;
import view.util.MyGridLayout;
import view.util.MyRowLayout;

class PreferencePageBook extends PreferencePage {

	private static final int mRightHint = 250;

	private Composite mMainComposite;
	private Composite mTableComposite;
	private TableViewer mTableViewerBooks;
	private boolean mOrderChanged = false;

	private Composite mAttributeComposite;

	private Map<Integer, Button> mBookButtonMap;
	private List<Book> mBookList;
	
	public PreferencePageBook() {
		setTitle("帳簿設定");
	}

	protected Control createContents(Composite parent) {
		mMainComposite = new Composite(parent, SWT.NONE);

		mMainComposite.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, false).getMyGridData();
		mMainComposite.setLayoutData(wGridData);

		Composite wTopComposite = new Composite(mMainComposite, SWT.NONE);
		wTopComposite.setLayout(new MyRowLayout().getMyRowLayout());
		wGridData.horizontalSpan = 2;
		wTopComposite.setLayoutData(wGridData);

		Button wCategoryAddButton = new Button(wTopComposite, SWT.NULL);
		wCategoryAddButton.setText("追加");
		wCategoryAddButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				InputDialog wInputDialog = new InputDialog(getShell(), "test", "追加する帳簿名", "", null);
				if (wInputDialog.open() == Dialog.OK) {
					System.out.println(wInputDialog.getValue());
				}
			}
		});

		Button wDeleteButton = new Button(wTopComposite, SWT.NULL);
		wDeleteButton.setText("削除");
//		wDeleteButton.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				if (mTreeViewerConfigItem.getSelectedConfigItem() != null) {
//					ConfigItem wConfigItem = mTreeViewerConfigItem.getSelectedConfigItem();
//					if (!wConfigItem.isSpecial()) {
//						if (MessageDialog.openConfirm(getShell(), "削除", wConfigItem.getName() + " - 本当に削除しますか？")) {
////							DbUtil.deleteCategoryItem(wConfigItem);
//							updateTree();
//						}
//					}
//				}
//			}
//		});

		Button wUpButton = new Button(wTopComposite, SWT.NULL);
		wUpButton.setText("↑");
		wUpButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Book wBook = getSelectedBook();
				if (wBook != null) {
					int wOldIndex = mBookList.indexOf(wBook);
					if (wOldIndex > 0) {
						mBookList.remove(wBook);
						mBookList.add(wOldIndex - 1, wBook);
						mOrderChanged = true;
						updateBookOrder();
					}
				}
			}
		});

		Button wDownButton = new Button(wTopComposite, SWT.NULL);
		wDownButton.setText("↓");
		wDownButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Book wBook = getSelectedBook();
				if (wBook != null) {
					int wOldIndex = mBookList.indexOf(wBook);
					if (wOldIndex < mBookList.size() - 1) {
						mBookList.remove(wBook);
						mBookList.add(wOldIndex + 1, wBook);
						mOrderChanged = true;
						updateBookOrder();
					}
				}
			}
		});

		// TreeViewer
		mBookList = DbUtil.getBookList();
		mTableComposite = new Composite(mMainComposite, SWT.BORDER);
		mTableComposite.setLayout(new MyFillLayout(SWT.VERTICAL).getMyFillLayout());
		wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData();
		mTableComposite.setLayoutData(wGridData);
		initBookNameTable();

		// 関連付け
		mAttributeComposite = new Composite(mMainComposite, SWT.BORDER);
		mAttributeComposite.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		wGridData = new MyGridData(GridData.FILL, GridData.FILL, false, true).getMyGridData();
		wGridData.widthHint = mRightHint;
		mAttributeComposite.setLayoutData(wGridData);

		Label wLabel = new Label(mAttributeComposite, SWT.NONE);
		wLabel.setText("関連付け");
//		
//		Map<Integer, String> wBookNameMap = DbUtil.getBookNameMap();
//		mBookButtonMap = new LinkedHashMap<Integer, Button>();
//		for (Map.Entry<Integer, String> entry : wBookNameMap.entrySet()) {
//			Button wButton = new Button(mAttributeComposite, SWT.CHECK);
//			wButton.setText(entry.getValue());
//			wButton.setVisible(false);
//			mBookButtonMap.put(entry.getKey(), wButton);
//
//			wButton.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					ConfigItem wSelectedItem = mTreeViewerConfigItem.getSelectedConfigItem();
//					if (wSelectedItem != null) {
//						Button wButton = (Button) e.getSource();
//						int wBookId = SystemData.getUndefinedInt();
//						for (Map.Entry<Integer, Button> entry : mBookButtonMap.entrySet()) {
//							if (wButton == entry.getValue()) {
//								wBookId = entry.getKey();
//								break;
//							}
//						}
//						DbUtil.updateItemRelation(wSelectedItem.getId(), wBookId, wButton.getSelection());
//					}
//				}
//			});
//		}
//		
//		Label wSpaceLabel = new Label(mAttributeComposite, SWT.NONE);
//		wSpaceLabel.setText("");
//		
//		Label wSpecialAttributeLabel = new Label(mAttributeComposite, SWT.NONE);
//		wSpecialAttributeLabel.setText("特別収支系設定");
//		
//		mTreeViewerConfigItem.addSelectionChangedListener(new ISelectionChangedListener() {
//			@Override
//			public void selectionChanged(SelectionChangedEvent arg0) {
//				ConfigItem wConfigItem = mTreeViewerConfigItem.getSelectedConfigItem();
//				updateAttributeButtons(wConfigItem);
//			}
//		});

		return mMainComposite;
	}
	
	private void initBookNameTable() {
		
		mTableViewerBooks = new TableViewer(mTableComposite, SWT.NONE);
		Table wTable = mTableViewerBooks.getTable();
		wTable.setHeaderVisible(false);
		
		// 列のヘッダの設定
		TableColumn wBookNameCol = new TableColumn(wTable, SWT.LEFT);
		wBookNameCol.setText("BookName");
		wBookNameCol.pack();
		
		mTableViewerBooks.setContentProvider(new TableContentProvider());
		mTableViewerBooks.setInput((Book[])mBookList.toArray(new Book[0]));

		mTableViewerBooks.setLabelProvider(new TableLabelProvider());
	}

	protected void performApply() {
		if (mOrderChanged) {
//			DbUtil.updateSortKeys(mRootConfigItem);
			mOrderChanged = false;
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

	private void updateBookOrder() {
		mTableViewerBooks.getTable().setRedraw(false);

		try {
			ISelection selection = mTableViewerBooks.getSelection();

			mTableViewerBooks.getTable().dispose();
			initBookNameTable();
			mTableComposite.layout();

//			addSelectionListenerToTree();

			mTableViewerBooks.setSelection(selection);

		} finally {
			mTableViewerBooks.getTable().setRedraw(true);
		}

	}

//	private void updateTree() {
//		mRootConfigItem = DbUtil.getRootConfigItem();
//		mTreeViewerConfigItem.getTree().dispose();
//		mTreeViewerConfigItem = new TreeViewerConfigItem(mTableComposite, mRootConfigItem);
//		mTableComposite.layout();
//
//		DbUtil.updateSortKeys(mRootConfigItem);
//		mOrderChanged = false;
//	}

	protected void updateAttributeButtons(ConfigItem pConfigItem) {
		if (pConfigItem == null || pConfigItem.isSpecial() || pConfigItem.isCategory()) {
			for (Map.Entry<Integer, Button> entry : mBookButtonMap.entrySet()) {
				Button wButton = entry.getValue();
				wButton.setVisible(false);
			}
		} else {
			List<Integer> wBookIdList = DbUtil.getRelatedBookIdList(pConfigItem);
			for (Map.Entry<Integer, Button> entry : mBookButtonMap.entrySet()) {
				Button wButton = entry.getValue();
				wButton.setVisible(true);
				wButton.setSelection(wBookIdList.contains(entry.getKey()));
			}
		}

	}
	
	private Book getSelectedBook() {
		IStructuredSelection sel = (IStructuredSelection) mTableViewerBooks.getSelection();
		return (Book) sel.getFirstElement();
	}

}

class TableContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object inputElement) {
		Book[] wBooks = (Book[]) inputElement;
		return wBooks;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}

class TableLabelProvider implements ITableLabelProvider {
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Book wBook = (Book) element;
		
		if (columnIndex == 0) {
			return wBook.getName();
		}
		return null;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

}

