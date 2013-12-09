package beauties.record.view.dialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

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

	private Collection<Book> mBooks;
	private Collection<Category> mCategories;
	private Collection<Item> mItems;

	private MyComboViewer<Book> mBookCombo;
	private DateTime mDateTime;
	private MyComboViewer<IncomeExpenseType> mIncomeExpenseCombo;
	private MyComboViewer<Category> mCategoryCombo;
	private MyComboViewer<Item> mItemCombo;
	private Spinner mValueSpinner;
	private Spinner mFrequencySpinner;
	private Combo mNoteCombo;
	private List<String> mNoteItems;
	
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
		mIncomeExpenseType = pRecordTableItem.getItem().getCategory().getIncomeExpenseType();
		mItem = mRecordTableItem.getItem();
		initLayout();
//		initWidgets();
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

		Label wSpaceLabel = new Label(wValuesRowComp, SWT.NONE);
		wSpaceLabel.setText("    ");

		Label wFrequencyLabel = new Label(wValuesRowComp, SWT.NONE);
		wFrequencyLabel.setText("回数");

		mFrequencySpinner = new Spinner(wValuesRowComp, SWT.BORDER);
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
		mIncomeExpenseCombo.setSelection(mIncomeExpenseType);
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
		mBookCombo.setInput(mBooks);
		mBookCombo.getCombo().setVisibleItemCount(mVisibleComboItemCount);
		mBookCombo.setSelection(mBook);
	}

	private void initWidgets() {
		updateCategoryCombo();
		updateItemCombo();
		updateNoteCombo();
	}
	
	private void setListeners() {
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
				modifyCategory();
			}
		});
		mItemCombo.getCombo().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				modifyItem();
			}
		});
		
	}

	private void setWidgets() {

		Calendar wCal = Calendar.getInstance();
		wCal.setTime(mRecordTableItem.getDate());
		mDateTime.setYear(wCal.get(Calendar.YEAR));
		mDateTime.setMonth(wCal.get(Calendar.MONTH));
		mDateTime.setDay(wCal.get(Calendar.DAY_OF_MONTH));

		updateCategoryCombo();
		updateItemCombo();
		mItem = mRecordTableItem.getItem();
		if (!mItems.contains(mItem)) {
			mItems.add(mItem);
			mItemCombo.add(mRecordTableItem.getItem());
			mItemCombo.refresh();
		}
		mItemCombo.setSelection(mItem);
		updateNoteCombo();

		if (mIncomeExpenseType == IncomeExpenseType.INCOME) {
			mValueSpinner.setSelection(mRecordTableItem.getIncome());
		} else {
			mValueSpinner.setSelection(mRecordTableItem.getExpense());
		}

		mFrequencySpinner.setSelection(mRecordTableItem.getFrequency());

		if (!"".equals(mRecordTableItem.getNote())) {
			mNoteItems.set(0, mRecordTableItem.getNote());
			mNoteCombo.setItems(mNoteItems.toArray(new String[0]));
//			mNoteCombo.setItem(0, mRecordTableItem.getNote());
		}
		mNoteCombo.select(0);
		System.out.println(mNoteCombo.getItem(0));
	}

	private void modifyBook() {
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
		IncomeExpenseType wNewType = mIncomeExpenseCombo.getSelectedItem();
		if (mIncomeExpenseType == wNewType) {
			return;
		}
		mIncomeExpenseType = wNewType;
		updateCategoryCombo();
		updateItemCombo();
		updateNoteCombo();
	}

	private void modifyCategory() {
		Category wNewCategory = mCategoryCombo.getSelectedItem();
		if (mCategory.equals(wNewCategory)) {
			return;
		}
		mCategory = wNewCategory;
		updateItemCombo();
		updateNoteCombo();
	}

	private void modifyItem() {
		Item wNewItem = mItemCombo.getSelectedItem();
		if (mItem.equals(wNewItem)) {
			return;
		}
		mItem = wNewItem;
		updateNoteCombo();
	}

	private void updateCategoryCombo() {
		Category wCategoryAll = Category.getAllCategory();
		if (mCategories == null) {
			mCategories = new ArrayList<>();
		} else {
			mCategories.clear();
		}
		mCategories.add(wCategoryAll);
		mCategories.addAll(DbUtil.getCategories(mBook, mIncomeExpenseType));

		mCategoryCombo.setInput(mCategories);
		mCategoryCombo.getCombo().setVisibleItemCount(mVisibleComboItemCount);
		mCategoryCombo.getCombo().select(0);
		mCategory = mCategoryCombo.getSelectedItem();
		mCategoryCombo.getCombo().pack();
	}

	private void updateItemCombo() {
		if (mCategory.isAllCategory()) {
			mItems = DbUtil.getItems(mBook, mIncomeExpenseType);
		} else {
			mItems = DbUtil.getItems(mBook, mCategory);
		}
		mItemCombo.setInput(mItems);
		mItemCombo.getCombo().select(0);

		if (!mItems.isEmpty()) {
			mItem = mItemCombo.getSelectedItem();
		}

		mItemCombo.getCombo().pack();
		mItemCombo.getCombo().setVisibleItemCount(mVisibleComboItemCount);

	}

	private void updateNoteCombo() {
//		System.out.println("updateNoteCombo");
		String wNote = mNoteCombo.getText();
		mNoteItems = DbUtil.getNotes(mItem);
		mNoteItems.add(0, wNote);
		mNoteCombo.setItems(mNoteItems.toArray(new String[0]));
//		mNoteCombo.add(wNote, 0);
		mNoteCombo.select(0);
		mNoteCombo.setVisibleItemCount(mVisibleComboItemCount);
	}
	
	public void updateForNextInput() {
		mValueSpinner.setSelection(0);
		mFrequencySpinner.setSelection(0);
		if (mCategoryCombo.getCombo().getSelectionIndex() != 0) {
			mCategoryCombo.getCombo().select(0);
			modifyCategory();
		} else {
			mItemCombo.getCombo().select(0);
			modifyItem();
		}
		if (!"".equals(mNoteItems.get(0))) {
			mNoteItems = DbUtil.getNotes(mItem);
			mNoteItems.add(0, "");
			mNoteCombo.setItems(mNoteItems.toArray(new String[0]));
		}
//		mNoteCombo.setItem(0, "");
		mNoteCombo.select(0);
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
		return new RecordTableItem.Builder(mBook, mItem,
				new GregorianCalendar(mDateTime.getYear(), mDateTime.getMonth(), mDateTime.getDay()).getTime())
		.frequency(mFrequencySpinner.getSelection())
		.note(mNoteCombo.getText())
		.income(mIncomeExpenseType == IncomeExpenseType.INCOME ? mValueSpinner.getSelection() : 0)
		.expense(mIncomeExpenseType == IncomeExpenseType.INCOME ? 0 : mValueSpinner.getSelection()).build();
	}

}
