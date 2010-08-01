package beauties.config.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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

import beauties.model.Book;
import beauties.model.SystemData;
import beauties.model.db.DbUtil;

import util.view.MyFillLayout;
import util.view.MyGridData;
import util.view.MyGridLayout;
import util.view.MyRowLayout;

class PreferencePageBook extends PreferencePage {

	private static final int mRightHint = 250;

	private Composite mMainComposite;
	private Composite mTableComposite;
	private TableViewer mTableViewerBooks;
	private boolean mOrderChanged = false;

	private Label mBalanceLabel;
	private Composite mAttributeComposite;

	private Map<Button, Integer> mItemButtonMap;
	private List<Book> mBookList;

	protected PreferencePageBook() {
		setTitle("帳簿設定");
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

		Button wBookAddButton = new Button(wTopComposite, SWT.NULL);
		wBookAddButton.setText("追加");
		wBookAddButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				InputDialog wInputDialog = new InputDialog(getShell(), "帳簿追加", "新しい帳簿名", "", null);
				if (wInputDialog.open() != Dialog.OK)
					return;
				DbUtil.addNewBook(wInputDialog.getValue());
				updateBookTableWithDb();
				mTableViewerBooks.getTable().setSelection(
						mTableViewerBooks.getTable().getItemCount() - 1);

			}
		});

		Button wBookModifyButton = new Button(wTopComposite, SWT.NULL);
		wBookModifyButton.setText("変更");
		wBookModifyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Book wBook = getSelectedBook();
				if (wBook == null)
					return;
				InputDialog wInputDialog = new InputDialog(getShell(), "帳簿名変更", "",
						wBook.getName(), null);
				if (wInputDialog.open() == Dialog.OK) {
					DbUtil.updateBook(wBook.getId(), wInputDialog.getValue(), wBook.getBalance());
					int wIndex = mTableViewerBooks.getTable().getSelectionIndex();
					updateBookTableWithDb();
					mTableViewerBooks.getTable().setSelection(wIndex);
				}

			}
		});

		Button wDeleteButton = new Button(wTopComposite, SWT.NULL);
		wDeleteButton.setText("削除");
		wDeleteButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Book wBook = getSelectedBook();
				if (wBook == null)
					return;
				if (MessageDialog
						.openConfirm(getShell(), "帳簿削除", wBook.getName() + " - 本当に削除しますか？")) {
					DbUtil.removeBook(wBook.getId());
					updateBookTableWithDb();
				}

			}
		});

		Button wUpButton = new Button(wTopComposite, SWT.NULL);
		wUpButton.setText("↑");
		wUpButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Book wBook = getSelectedBook();
				if (wBook == null)
					return;
				int wOldIndex = mBookList.indexOf(wBook);
				if (wOldIndex > 0) {
					mBookList.remove(wBook);
					mBookList.add(wOldIndex - 1, wBook);
					mOrderChanged = true;
					updateBookTable();
				}

			}
		});

		Button wDownButton = new Button(wTopComposite, SWT.NULL);
		wDownButton.setText("↓");
		wDownButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Book wBook = getSelectedBook();
				if (wBook == null)
					return;
				int wOldIndex = mBookList.indexOf(wBook);
				if (wOldIndex < mBookList.size() - 1) {
					mBookList.remove(wBook);
					mBookList.add(wOldIndex + 1, wBook);
					mOrderChanged = true;
					updateBookTable();
				}

			}
		});

		// BookNameTable
		mBookList = DbUtil.getBookList();
		mTableComposite = new Composite(mMainComposite, SWT.BORDER);
		mTableComposite.setLayout(new MyFillLayout(SWT.VERTICAL).getMyFillLayout());
		wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData();
		mTableComposite.setLayoutData(wGridData);
		initBookNameTable();
		mTableViewerBooks.getTable().setSelection(0);

		// 関連付け
		mAttributeComposite = new Composite(mMainComposite, SWT.BORDER);
		mAttributeComposite.setLayout(new MyGridLayout(2, true).getMyGridLayout());
		wGridData = new MyGridData(GridData.FILL, GridData.FILL, false, true).getMyGridData();
		wGridData.widthHint = mRightHint;
		mAttributeComposite.setLayoutData(wGridData);
		initAttributes();

		// 初期設定
		mTableViewerBooks.getTable().setFocus();
		updateAttributeButtons(mBookList.get(0).getId());

		return mMainComposite;
	}

	private void initBookNameTable() {
		mTableViewerBooks = new TableViewer(mTableComposite, SWT.FULL_SELECTION);
		Table wTable = mTableViewerBooks.getTable();
		wTable.setHeaderVisible(false);

		// 列のヘッダの設定
		TableColumn wBookNameCol = new TableColumn(wTable, SWT.LEFT);
		wBookNameCol.setText("BookName");
		wBookNameCol.pack();

		mTableViewerBooks.setContentProvider(new TableContentProvider());
		mTableViewerBooks.setInput((Book[]) mBookList.toArray(new Book[0]));

		mTableViewerBooks.setLabelProvider(new TableLabelProvider());
		addSelectionListenerToBookTable();
	}

	private void addSelectionListenerToBookTable() {
		mTableViewerBooks.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				Book wBook = getSelectedBook();
				if (wBook != null) {
					updateAttributeButtons(wBook.getId());
				}
			}
		});
	}

	private void initAttributes() {
		mBalanceLabel = new Label(mAttributeComposite, SWT.NONE);

		Button wBalanceModifyButton = new Button(mAttributeComposite, SWT.NONE);
		wBalanceModifyButton.setText("変更");
		wBalanceModifyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Book wBook = getSelectedBook();
				if (wBook == null)
					return;
				InputDialog dlg = new InputDialog(getShell(), "初期値変更", "初期値を入力してください", Integer
						.toString((int) wBook.getBalance()), new IInputValidator() {
					public String isValid(String newText) {
						if (newText.matches("[0-9]+"))
							return null; // Valid
						if (newText.length() == 0)
							return "Input figures"; // null
						return "Error: only figures are allowed";

					}
				});
				if (dlg.open() == Dialog.OK) {
					wBook.setBalance(Integer.parseInt(dlg.getValue()));
					DbUtil.updateBalance(wBook);
					updateBalanceLabel();
				}
			}
		});

		Label wLabel = new Label(mAttributeComposite, SWT.NONE);
		wLabel.setText("関連付け");
		GridData wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, false)
				.getMyGridData();
		wGridData.horizontalSpan = 2;
		wLabel.setLayoutData(wGridData);

		Composite wCompositeItemButtons = new Composite(mAttributeComposite, SWT.BORDER);
		wCompositeItemButtons.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData();
		wGridData.horizontalSpan = 2;
		wCompositeItemButtons.setLayoutData(wGridData);

		mItemButtonMap = new HashMap<Button, Integer>();
		Map<Integer, String> wItemNameMap = DbUtil.getItemNameMap(SystemData.getAllBookInt(), true);
		wItemNameMap.putAll(DbUtil.getItemNameMap(SystemData.getAllBookInt(), false));
		for (Map.Entry<Integer, String> entry : wItemNameMap.entrySet()) {
			Button wButton = new Button(wCompositeItemButtons, SWT.CHECK);
			wButton.setText(entry.getValue());
			mItemButtonMap.put(wButton, entry.getKey());

			wButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Book wBook = getSelectedBook();
					if (wBook == null)
						return;
					Button wButton = (Button) e.getSource();
					int wBookId = wBook.getId();
					int wItemId = mItemButtonMap.get(wButton);
					DbUtil.updateItemRelation(wItemId, wBookId, wButton.getSelection());
				}
			});
		}
	}

	protected void performApply() {
		if (mOrderChanged) {
			updateBookSortKeys();
		}
		if (getControl() == null) {
			return;
		}
	}

	public boolean performOk() {
		performApply();
		return true;
	}

	private void updateBookTable() {
		mTableViewerBooks.getTable().setRedraw(false);

		try {
			ISelection selection = mTableViewerBooks.getSelection();

			mTableViewerBooks.getTable().dispose();
			initBookNameTable();
			mTableComposite.layout();
			mTableViewerBooks.setSelection(selection);

		} finally {
			mTableViewerBooks.getTable().setRedraw(true);
		}
	}

	private void updateBookTableWithDb() {
		mBookList = DbUtil.getBookList();
		updateBookTable();
	}

	private void updateBookSortKeys() {
		DbUtil.updateBookSortKeys(mBookList);
		mOrderChanged = false;
	}

	private void updateAttributeButtons(int pBookId) {
		updateBalanceLabel();
		List<Integer> wItemIdList = DbUtil.getRelatedItemIdList(getSelectedBook().getId());
		for (Map.Entry<Button, Integer> entry : mItemButtonMap.entrySet()) {
			entry.getKey().setSelection(wItemIdList.contains(entry.getValue()));
		}
	}

	private void updateBalanceLabel() {
		mBalanceLabel.setText("初期値: "
				+ SystemData.getFormatedFigures(getSelectedBook().getBalance()) + "\t");
		mBalanceLabel.pack();
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
