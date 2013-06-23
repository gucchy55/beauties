package beauties.config.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
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

import beauties.common.lib.DbUtil;
import beauties.common.lib.SystemData;
import beauties.common.model.Book;
import beauties.common.model.IncomeExpenseType;
import beauties.common.model.Item;
import beauties.common.view.MyFillLayout;
import beauties.common.view.MyGridData;
import beauties.common.view.MyGridLayout;
import beauties.common.view.MyRowLayout;

class PreferencePageBook extends PreferencePage {

	private static final int mRightHint = 250;
	
	private MyPreferenceManager mManager;

	private Composite mMainComposite;
	private Composite mTableComposite;
	private TableViewer mTableViewerBooks;
	private boolean mOrderChanged = false;

	private Label mBalanceLabel;
	private Composite mAttributeComposite;
	private Composite mCompositeItemButtons;
	private Map<Button, Item> mItemButtonMap;
	private List<Book> mBookList;

	PreferencePageBook(MyPreferenceManager pManager) {
		mManager = pManager;
		mBookList = new ArrayList<>(DbUtil.getBooks());
		mItemButtonMap = new HashMap<>();
		setTitle("帳簿設定");
	}
	
	@Override
	protected Control createContents(Composite parent) {
		mMainComposite = new Composite(parent, SWT.NONE);

		mMainComposite.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, false)
				.getMyGridData();
		mMainComposite.setLayoutData(wGridData);

		createTopComposite(wGridData);

		createTableComposite();

		createAttributeComposite();

		// 初期設定
		mTableViewerBooks.getTable().setFocus();
		updateAttributeButtons(mBookList.get(0));

		return mMainComposite;
	}

	private void createTopComposite(GridData wGridData) {
		Composite wTopComposite = new Composite(mMainComposite, SWT.NONE);
		wTopComposite.setLayout(new MyRowLayout().getMyRowLayout());
		wGridData.horizontalSpan = 2;
		wTopComposite.setLayoutData(wGridData);

		createAddButton(wTopComposite);

		createModifyButton(wTopComposite);

		createDeleteButton(wTopComposite);

		createTopButton(wTopComposite);

		createDownButton(wTopComposite);
	}

	private void createAddButton(Composite wTopComposite) {
		Button wBookAddButton = new Button(wTopComposite, SWT.NULL);
		wBookAddButton.setText("追加");
		wBookAddButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog wInputDialog = new InputDialog(getShell(), "帳簿追加", "新しい帳簿名", "", null);
				if (wInputDialog.open() != Window.OK)
					return;
				DbUtil.addNewBook(wInputDialog.getValue());
				updateBookTableWithDb();
				mTableViewerBooks.getTable().setSelection(
						mTableViewerBooks.getTable().getItemCount() - 1);
				if (getSelectedBook() == null)
					return;
				updateAttributeButtons(getSelectedBook());

			}
		});
	}

	private void createModifyButton(Composite wTopComposite) {
		Button wBookModifyButton = new Button(wTopComposite, SWT.NULL);
		wBookModifyButton.setText("変更");
		wBookModifyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Book wBook = getSelectedBook();
				if (wBook == null)
					return;
				InputDialog wInputDialog = new InputDialog(getShell(), "帳簿名変更", "",
						wBook.getName(), null);
				if (wInputDialog.open() == Window.OK) {
					DbUtil.updateBookName(wBook, wInputDialog.getValue());
					int wIndex = mTableViewerBooks.getTable().getSelectionIndex();
					updateBookTableWithDb();
					mTableViewerBooks.getTable().setSelection(wIndex);
					updateAttributeButtons(wBook);
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
				Book wBook = getSelectedBook();
				if (wBook == null)
					return;
				if (!MessageDialog
						.openConfirm(getShell(), "帳簿削除", wBook.getName() + " - 本当に削除しますか？"))
					return;
				
				DbUtil.removeBook(wBook);
				updateBookTableWithDb();
				if (getSelectedBook() == null)
					return;
				updateAttributeButtons(getSelectedBook());

			}
		});
	}

	private void createTopButton(Composite wTopComposite) {
		Button wUpButton = new Button(wTopComposite, SWT.NULL);
		wUpButton.setText("↑");
		wUpButton.addSelectionListener(new SelectionAdapter() {
			@Override
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
	}

	private void createDownButton(Composite wTopComposite) {
		Button wDownButton = new Button(wTopComposite, SWT.NULL);
		wDownButton.setText("↓");
		wDownButton.addSelectionListener(new SelectionAdapter() {
			@Override
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
	}

	private void createTableComposite() {
		GridData wGridData;
		// BookNameTable
		mTableComposite = new Composite(mMainComposite, SWT.BORDER);
		mTableComposite.setLayout(new MyFillLayout(SWT.VERTICAL).getMyFillLayout());
		wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData();
		mTableComposite.setLayoutData(wGridData);
		initBookNameTable();
		mTableViewerBooks.getTable().setSelection(0);
	}

	private void createAttributeComposite() {
		GridData wGridData;
		// 関連付け
		mAttributeComposite = new Composite(mMainComposite, SWT.BORDER);
		mAttributeComposite.setLayout(new MyGridLayout(2, true).getMyGridLayout());
		wGridData = new MyGridData(GridData.FILL, GridData.FILL, false, true).getMyGridData();
		wGridData.widthHint = mRightHint;
		mAttributeComposite.setLayoutData(wGridData);
		initAttributes();
	}

	private void initBookNameTable() {
		mTableViewerBooks = new TableViewer(mTableComposite, SWT.FULL_SELECTION);
		Table wTable = mTableViewerBooks.getTable();
		wTable.setHeaderVisible(false);

		// 列のヘッダの設定
		TableColumn wBookNameCol = new TableColumn(wTable, SWT.LEFT);
		wBookNameCol.setText("BookName");
		wBookNameCol.pack();

		mTableViewerBooks.setContentProvider(ArrayContentProvider.getInstance());
		mTableViewerBooks.setInput(mBookList);

		mTableViewerBooks.setLabelProvider(new TableLabelProvider());
		addSelectionListenerToBookTable();
	}

	private void addSelectionListenerToBookTable() {
		mTableViewerBooks.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				Book wBook = getSelectedBook();
				if (wBook != null) {
					updateAttributeButtons(wBook);
				}
			}
		});
	}

	private void initAttributes() {
		mBalanceLabel = new Label(mAttributeComposite, SWT.NONE);

		createBalanceModifyButton();

		createRelationLabel();

		createCompositeItemButtons();
	}

	private void createBalanceModifyButton() {
		Button wBalanceModifyButton = new Button(mAttributeComposite, SWT.NONE);
		wBalanceModifyButton.setText("変更");
		wBalanceModifyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Book wBook = getSelectedBook();
				if (wBook == null)
					return;
				InputDialog dlg = new InputDialog(getShell(), "初期値変更", "初期値を入力してください", Integer
						.toString(wBook.getBalance()), new IInputValidator() {
					@Override
					public String isValid(String newText) {
						if (newText.matches("[0-9]+"))
							return null; // Valid
						if (newText.length() == 0)
							return "Input figures"; // null
						return "Error: only figures are allowed";

					}
				});
				if (dlg.open() == Window.OK) {
					wBook.setBalance(Integer.parseInt(dlg.getValue()));
					DbUtil.updateBalance(wBook);
					updateBalanceLabel();
				}
			}
		});
	}

	private void createRelationLabel() {
		Label wLabel = new Label(mAttributeComposite, SWT.NONE);
		wLabel.setText("関連付け");
		GridData wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, false)
				.getMyGridData();
		wGridData.horizontalSpan = 2;
		wLabel.setLayoutData(wGridData);
	}

	private void createCompositeItemButtons() {
		GridData wGridData;
		mCompositeItemButtons = new Composite(mAttributeComposite, SWT.BORDER);
		mCompositeItemButtons.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData();
		wGridData.horizontalSpan = 2;
		mCompositeItemButtons.setLayoutData(wGridData);
		
		Map<IncomeExpenseType, List<Item>> wItemMap = DbUtil.getAllItems();
		Label wIncomeLabel = new Label(mCompositeItemButtons, SWT.NONE);
		wIncomeLabel.setText("収入");
		createEachItemButtons(mCompositeItemButtons, wItemMap.get(IncomeExpenseType.INCOME));
		
		new Label(mCompositeItemButtons, SWT.NONE);
		Label wExpenseLabel = new Label(mCompositeItemButtons, SWT.NONE);
		wExpenseLabel.setText("支出");
		
		createEachItemButtons(mCompositeItemButtons, wItemMap.get(IncomeExpenseType.EXPENCE));
	}
	
	private void createEachItemButtons(Composite pCompositeItemButtons, List<Item> pItems) {
		for (Item wItem : pItems) {
			Button wButton = new Button(pCompositeItemButtons, SWT.CHECK);
			wButton.setText(wItem.getName());
			mItemButtonMap.put(wButton, wItem);

			wButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Book wBook = getSelectedBook();
					if (wBook == null)
						return;
					Button wButton = (Button) e.getSource();
					DbUtil.updateItemRelation(mItemButtonMap.get(wButton), wBook, wButton.getSelection());
				}
			});
		}
	}

	@Override
	protected void performApply() {
		if (mOrderChanged) {
			updateBookSortKeys();
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

	private void updateBookTable() {
		mTableViewerBooks.getTable().setRedraw(false);

		try {
			ISelection selection = mTableViewerBooks.getSelection();
			mTableViewerBooks.setInput(mBookList);
			mTableViewerBooks.refresh();
			mTableViewerBooks.setSelection(selection);
		} finally {
			mTableViewerBooks.getTable().setRedraw(true);
		}
	}

	private void updateBookTableWithDb() {
		mBookList.clear();
		mBookList.addAll(DbUtil.getBooks());
		updateBookTable();
		mManager.updateBooks(mBookList);
	}

	private void updateBookSortKeys() {
		DbUtil.updateBookSortKeys(mBookList);
		mOrderChanged = false;
	}

	private void updateAttributeButtons(Book pBook) {
		updateBalanceLabel();
		Collection<Item> wItems = DbUtil.getRelatedItems(pBook);
		for (Map.Entry<Button, Item> entry : mItemButtonMap.entrySet()) {
			entry.getKey().setSelection(wItems.contains(entry.getValue()));
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

class TableLabelProvider implements ITableLabelProvider {
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Book wBook = (Book) element;

		if (columnIndex == 0) {
			return wBook.getName();
		}
		return null;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

}
