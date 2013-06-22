package beauties.record.view.dialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import beauties.common.lib.DbUtil;
import beauties.common.lib.SystemData;
import beauties.common.model.Book;
import beauties.common.model.Category;
import beauties.common.model.IncomeExpenseType;
import beauties.common.model.Item;
import beauties.common.view.MyComboViewer;
import beauties.common.view.MyGridData;
import beauties.common.view.MyGridLayout;
import beauties.record.model.RecordTableItem;


class CompositeRecord extends Composite {

	private Book mBook; // Selected on this Dialog
	private IncomeExpenseType mIncomeExpenseType = IncomeExpenseType.EXPENCE;
	private Category mCategory;
	private Item mItem;

	private RecordTableItem mRecordTableItem;

//	private static final String mCategoryAllName = "（すべて）";
//	private static final int mCategoryAllId = SystemData.getUndefinedInt();

	// Map of ID & Name
	private Collection<Book> mBooks;
	private Collection<Category> mCategories;
	private Collection<Item> mItems;

	// Map of ComboIndex & ID
//	private List<Integer> mBookIdList = new ArrayList<Integer>();
//	private List<Integer> mCategoryIdList = new ArrayList<Integer>();
//	private List<Integer> mItemIdList = new ArrayList<Integer>();

	private MyComboViewer<Book> mBookCombo;
	private DateTime mDateTime;
	private MyComboViewer<IncomeExpenseType> mIncomeExpenseCombo;
	private MyComboViewer<Category> mCategoryCombo;
	private MyComboViewer<Item> mItemCombo;
	private Spinner mValueSpinner;
	private Spinner mFrequencySpinner;
	private Combo mNoteCombo;
	private String[] mNoteItems;
	
	private static final int mVisibleComboItemCount = 10;

	public CompositeRecord(Composite pParent, Book pBook) {
		super(pParent, SWT.NONE);

		mBook = pBook;

		if (mBook.isAllBook())
			mBook = SystemData.getBooks(false).iterator().next();

		mIncomeExpenseType = IncomeExpenseType.EXPENCE;
		initLayout();
		initWidgets();
		setListeners();
		mDateTime.setFocus();
	}

	// for modify
	public CompositeRecord(Composite pParent, RecordTableItem pRecordTableItem) {
		super(pParent, SWT.NONE);
		mRecordTableItem = pRecordTableItem;
		mBook = mRecordTableItem.getBook();
//		mIncomeExpenseType = DbUtil.isIncomeCategory(SystemData.getCategoryByItemId(mRecordTableItem.getItem().getId()));
		mIncomeExpenseType = pRecordTableItem.getItem().getCategory().getIncomeExpenseType();
		initLayout();
		initWidgets();
		setWidgets();
		setListeners();
	}

	private void initLayout() {
		GridLayout wGridLayout = new GridLayout(2, false);
		wGridLayout.verticalSpacing = 10;
		this.setLayout(wGridLayout);
		
		initBookCombo();

		initDateTime();

		initInExCombo();

		initCategoryCombo();

		initItemCombo();

		initValueSpinners();

		initNoteCombo();

	}

	private void initNoteCombo() {
		// Note
		Label wNoteLabel = new Label(this, SWT.NONE);
		wNoteLabel.setText("備考");

		mNoteCombo = new Combo(this, SWT.DROP_DOWN | SWT.FILL);
		GridData wGridData = new GridData(GridData.FILL_HORIZONTAL);
		mNoteCombo.setLayoutData(wGridData);
	}

	private void initValueSpinners() {
		Label wValueLabel = new Label(this, SWT.NONE);
		wValueLabel.setText("金額");

		Composite wValuesRowComp = new Composite(this, SWT.NONE);
		GridData wGridData = new GridData(GridData.FILL_HORIZONTAL);
		wValuesRowComp.setLayoutData(wGridData);

		wValuesRowComp.setLayout(new MyGridLayout(4, false).getMyGridLayout());
		this.setLayoutData(new MyGridData(GridData.BEGINNING, GridData.BEGINNING, true, false)
				.getMyGridData());

		mValueSpinner = new Spinner(wValuesRowComp, SWT.BORDER);
		mValueSpinner.setValues(0, 0, Integer.MAX_VALUE, 0, 100, 10);
//		mValueSpinner.addFocusListener(Util.getFocusListenerToDisableIme(getShell(), SWT.NONE));

		Label wSpaceLabel = new Label(wValuesRowComp, SWT.NONE);
		wSpaceLabel.setText("    ");

		Label wFrequencyLabel = new Label(wValuesRowComp, SWT.NONE);
		wFrequencyLabel.setText("回数");

		mFrequencySpinner = new Spinner(wValuesRowComp, SWT.BORDER);
//		mFrequencySpinner.addFocusListener(Util.getFocusListenerToDisableIme(getShell(), SWT.NONE));
	}

	private void initItemCombo() {
		Label wItemLabel = new Label(this, SWT.NONE);
		wItemLabel.setText("項目");
		mItemCombo = new MyComboViewer<>(this, SWT.READ_ONLY);
	}

	private void initCategoryCombo() {
		Label wCategoryLabel = new Label(this, SWT.NONE);
		wCategoryLabel.setText("分類");
		mCategoryCombo = new MyComboViewer<>(this, SWT.READ_ONLY);
	}

	private void initInExCombo() {
		Label wInExLabel = new Label(this, SWT.NONE);
		wInExLabel.setText("収支");

		mIncomeExpenseCombo = new MyComboViewer<>(this, SWT.READ_ONLY);
		mIncomeExpenseCombo.setInput(IncomeExpenseType.values());
//		mIncomeExpenseCombo.add("収入");
//		mIncomeExpenseCombo.add("支出");
		mIncomeExpenseCombo.setSelection(mIncomeExpenseType);
//		if (mIncomeExpenseType) {
//			mIncomeExpenseCombo.select(0);
//		} else {
//			mIncomeExpenseCombo.select(1);
//		}
//		mIncomeExpenseCombo.addModifyListener(new ModifyListener() {
//			@Override
//			public void modifyText(ModifyEvent e) {
//				modifyIncomeExpense();
//			}
//		});
	}

	private void initDateTime() {
		// DateTime
		Label wDateLabel = new Label(this, SWT.NONE);
		wDateLabel.setText("日付");
		mDateTime = new DateTime(this, SWT.DATE | SWT.BORDER | SWT.DROP_DOWN);
	}

	private void initBookCombo() {
		// BookName
		Label wBookLabel = new Label(this, SWT.NONE);
		wBookLabel.setText("帳簿");

		mBookCombo = new MyComboViewer<>(this, SWT.READ_ONLY);
		mBooks = SystemData.getBooks(false);
//		mBookIdList.clear();
//		mBookCombo.removeAll();

//		Iterator<Integer> wKeyIt = mBookNameMap.keySet().iterator();
//		for (Book wBook : mBooks) {
//		while (wKeyIt.hasNext()) {
//			int wBookId = wBook.getId();
//			mBookIdList.add(wBookId);
//			mBookCombo.add(wBook.getName());
//		}
		mBookCombo.setInput(mBooks);

		mBookCombo.getCombo().setVisibleItemCount(mVisibleComboItemCount);

//		mBookCombo.select(mBookIdList.indexOf(mBook));
		mBookCombo.setSelection(mBook);

//		mBookCombo.addModifyListener(new ModifyListener() {
//			@Override
//			public void modifyText(ModifyEvent e) {
//				modifyBookId();
//			}
//		});
	}

	private void initWidgets() {
		updateCategoryCombo();
		updateItemCombo();
		updateNoteCombo();
	}
	
	private void setListeners() {
//		mBookCombo.addModifyListener(new ModifyListener() {
//			@Override
//			public void modifyText(ModifyEvent e) {
//				modifyBookId();
//			}
//		});
//		mIncomeExpenseCombo.addModifyListener(new ModifyListener() {
//			@Override
//			public void modifyText(ModifyEvent e) {
//				modifyIncomeExpense();
//			}
//		});
		mBookCombo.getCombo().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				modifyBook();
			}
		});
		mIncomeExpenseCombo.getCombo().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				modifyIncomeExpense();
			}
		});
		mCategoryCombo.getCombo().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				modifyCategoryId();
			}
		});
		mItemCombo.getCombo().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				modifyItemId();
			}
		});
		
	}

	private void setWidgets() {
		Calendar wCal = Calendar.getInstance();
		wCal.setTime(mRecordTableItem.getDate());
		mDateTime.setYear(wCal.get(Calendar.YEAR));
		mDateTime.setMonth(wCal.get(Calendar.MONTH));
		mDateTime.setDay(wCal.get(Calendar.DAY_OF_MONTH));

		mItem = mRecordTableItem.getItem();
		if (!mItems.contains(mItem)) {
			mItems.add(mItem);
//			mItemCombo.add(mRecordTableItem.getItem().getName());
			mItemCombo.add(mRecordTableItem.getItem());
			mItemCombo.refresh();
		}

//		mItemCombo.select(mItemIdList.indexOf(mItem));
		mItemCombo.setSelection(mItem);

		if (mIncomeExpenseType == IncomeExpenseType.INCOME) {
			mValueSpinner.setSelection(mRecordTableItem.getIncome());
		} else {
			mValueSpinner.setSelection(mRecordTableItem.getExpense());
		}

		mFrequencySpinner.setSelection(mRecordTableItem.getFrequency());

		if (!"".equals(mRecordTableItem.getNote()))
			mNoteCombo.setItem(0, mRecordTableItem.getNote());
		mNoteCombo.select(0);

	}

	private void modifyBook() {
//		mBook = mBookIdList.get(mBookCombo.getSelectionIndex());
		Book wNewBook = mBookCombo.getSelectedItem();
		if (mBook.equals(wNewBook)) {
			return;
		}
		mBook = wNewBook;
		updateCategoryCombo();
		updateItemCombo();
		updateNoteCombo();
	}

	private void modifyIncomeExpense() {
//		mIncomeExpenseType = mIncomeExpenseCombo.getSelectionIndex() == 0;
		IncomeExpenseType wNewType = mIncomeExpenseCombo.getSelectedItem();
		if (mIncomeExpenseType == wNewType) {
			return;
		}
		mIncomeExpenseType = wNewType;
		updateCategoryCombo();
		updateItemCombo();
		updateNoteCombo();
	}

	private void modifyCategoryId() {
//		mCategory = mCategoryIdList.get(mCategoryCombo.getSelectionIndex());
		Category wNewCategory = mCategoryCombo.getSelectedItem();
		if (mCategory.equals(wNewCategory)) {
			return;
		}
		mCategory = wNewCategory;
		updateItemCombo();
		updateNoteCombo();
	}

	private void modifyItemId() {
//		mItem = mItemIdList.get(mItemCombo.getSelectionIndex());
		Item wNewItem = mItemCombo.getSelectedItem();
		if (mItem.equals(wNewItem)) {
			return;
		}
		mItem = wNewItem;
		updateNoteCombo();
	}

	private void updateCategoryCombo() {
//		System.out.println("updateCategoryCombo");
//		for (Listener l : mCategoryCombo.getListeners(SWT.Modify))
//			mCategoryCombo.removeListener(SWT.Modify, l);
//		mCategoryCombo.removeSelectionListener(mCategorySelectionAdapter);

//		mCategoryCombo.removeAll();
//		mCategoryIdList.clear();

		Category wCategoryAll = Category.getAllCategory();
//		mCategoryCombo.add(wCategoryAll.getName());
		if (mCategories == null) {
			mCategories = new ArrayList<>();
		} else {
			mCategories.clear();
		}
		mCategories.add(wCategoryAll);
		mCategories.addAll(DbUtil.getCategoryList(mBook, mIncomeExpenseType));

//		for (int wId : mCategoryIdList) {
//			if (wId != wCategoryAll.getId())
//				mCategoryCombo.add(SystemData.getCategoryName(wId));
//		}
		mCategoryCombo.setInput(mCategories);

		mCategoryCombo.getCombo().setVisibleItemCount(mVisibleComboItemCount);

		mCategoryCombo.getCombo().select(0);
//		mCategory = mCategoryIdList.get(0);
		mCategory = mCategoryCombo.getSelectedItem();

		mCategoryCombo.getCombo().pack();
//		mCategoryCombo.addSelectionListener(mCategorySelectionAdapter);
//		mCategoryCombo.addModifyListener(new ModifyListener() {
//			@Override
//			public void modifyText(ModifyEvent e) {
//				modifyCategoryId();
//			}
//		});

	}

	private void updateItemCombo() {
//		System.out.println("updateItemCombo");
//		for (Listener l : mItemCombo.getListeners(SWT.Modify))
//			mItemCombo.removeListener(SWT.Modify, l);
//		mItemCombo.removeSelectionListener(mItemSelectionAdapter);

		if (mCategory.isAllCategory()) {
			mItems = DbUtil.getItemList(mBook, mIncomeExpenseType);
		} else {
			mItems = DbUtil.getItemList(mBook, mCategory);
		}
		mItemCombo.setInput(mItems);
		
//		mItemCombo.removeAll();
//		for (int wId : mItemIdList)
//			mItemCombo.add(SystemData.getItemName(wId));

//		mItem = mItems.iterator().next();
//		mItemCombo.setSelection(mItem);
		mItemCombo.getCombo().select(0);

//		if (!mItemIdList.isEmpty())
		if (!mItems.isEmpty()) {
			mItem = mItemCombo.getSelectedItem();
		}

		mItemCombo.getCombo().pack();
		mItemCombo.getCombo().setVisibleItemCount(10);

//		mItemCombo.addSelectionListener(mItemSelectionAdapter);
//		mItemCombo.addModifyListener(new ModifyListener() {
//			@Override
//			public void modifyText(ModifyEvent e) {
//				modifyItemId();
//			}
//		});
	}

	private void updateNoteCombo() {
//		System.out.println("updateNoteCombo");
		String wNote = mNoteCombo.getText();
		mNoteItems = DbUtil.getNotes(mItem);
		mNoteCombo.setItems(mNoteItems);
		mNoteCombo.add(wNote, 0);
		mNoteCombo.select(0);
		mNoteCombo.setVisibleItemCount(mVisibleComboItemCount);

	}

	public void updateForNextInput() {
//		updateCategoryCombo();
//		updateItemCombo();

//		mNoteCombo.setItem(0, "");
//		updateNoteCombo();
//		mNoteCombo.select(0);
//		updateNoteCombo();

		mValueSpinner.setSelection(0);
		mFrequencySpinner.setSelection(0);
		if (mCategoryCombo.getCombo().getSelectionIndex() != 0) {
			mCategoryCombo.getCombo().select(0);
			modifyCategoryId();
		} else {
			mItemCombo.getCombo().select(0);
			modifyItemId();
//			updateNoteCombo();
		}
//		updateNoteCombo();
		mNoteCombo.setItem(0, "");
		mNoteCombo.select(0);
//		mCategoryCombo.select(0);
//		if (mItemCombo.getSelectionIndex() == 0) {
//			updateNoteCombo();
//		} else {
//			mItemCombo.select(0);
//		}
		
		mItemCombo.getCombo().setFocus();
	}

	public void insertRecord() {
		// New/Update, Single/Multi records
		// Common
		if (mItems.isEmpty()) {
			MessageDialog.openError(getShell(), "設定された項目がありません", "設定画面から関連付ける項目を設定してください");
			return;
		}
		if (mRecordTableItem == null)
			DbUtil.insertNewRecord(createNewRecordTableItem());
		else
			DbUtil.updateRecord(mRecordTableItem, createNewRecordTableItem());
	}

	public int getValue() {
		return mValueSpinner.getSelection();
	}

	private RecordTableItem createNewRecordTableItem() {
		return new RecordTableItem.Builder(
				mBook, mItem,
				new GregorianCalendar(mDateTime.getYear(), mDateTime.getMonth(), mDateTime.getDay())
						.getTime()).frequency(mFrequencySpinner.getSelection()).note(
				mNoteCombo.getText()).income(mIncomeExpenseType == IncomeExpenseType.INCOME ? mValueSpinner.getSelection() : 0)
				.expense(mIncomeExpenseType == IncomeExpenseType.INCOME ? 0 : mValueSpinner.getSelection()).build();
	}

}
