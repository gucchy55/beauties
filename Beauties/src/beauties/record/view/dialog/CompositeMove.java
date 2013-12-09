package beauties.record.view.dialog;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.swt.SWT;
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
import beauties.common.model.Item;
import beauties.common.view.MyComboViewer;
import beauties.common.view.MyGridData;
import beauties.common.view.MyGridLayout;
import beauties.record.model.RecordTableItem;
import beauties.record.model.RecordTableItemForMove;


class CompositeMove extends Composite {

	private Book mBook; // Selected on this Dialog
//	private static final int mMoveIncomeItemId = DbUtil.getMoveIncomeItemId();

	private RecordTableItem mIncomeRecord;
	private RecordTableItem mExpenseRecord;

	// Map of ID & Name
	private Collection<Book> mBooks;

	// Map of ComboIndex & ID
//	private List<Integer> mBookIdList = new ArrayList<Integer>();

	private MyComboViewer<Book> mBookFromCombo;
	private MyComboViewer<Book> mBookToCombo;
	private DateTime mDateTime;
	private Spinner mValueSpinner;
	private Spinner mFrequencySpinner;
	private Combo mNoteCombo;
	private List<String> mNoteItems;

	private static final int mVisibleComboItemCount = 10;

	public CompositeMove(Composite pParent, Book pBook) {
		super(pParent, SWT.NONE);

		mBook = pBook;
		if (mBook.isAllBook())
			mBook = SystemData.getBooks(false).iterator().next();
		initLayout();
		initWidgets();
		mDateTime.setFocus();
	}

	// for modify
	public CompositeMove(Composite pParent, RecordTableItem pRecordTableItem) {
		super(pParent, SWT.NONE);

		if (pRecordTableItem.getIncome() > 0) {
			mIncomeRecord = pRecordTableItem;
			mExpenseRecord = DbUtil.getMovePairRecord(pRecordTableItem);
		} else {
			mExpenseRecord = pRecordTableItem;
			mIncomeRecord = DbUtil.getMovePairRecord(pRecordTableItem);
		}
		mBook = mIncomeRecord.getBook();
		initLayout();
		initWidgets();
		setWidgets();
	}

	private void initLayout() {
		GridLayout wGridLayout = new GridLayout(2, false);
		wGridLayout.verticalSpacing = 10;
		this.setLayout(wGridLayout);
		mBooks = SystemData.getBooks(false);
		
		createDateTime();

		createFromBookCombo();

		createToBookCombo();

		setBookCombos();

		createValueSpinners();

		createNoteCombo();
	}

	private void createNoteCombo() {
		Label wNoteLabel = new Label(this, SWT.NONE);
		wNoteLabel.setText("備考");

		mNoteCombo = new Combo(this, SWT.DROP_DOWN | SWT.FILL);
		GridData wGridData = new GridData(GridData.FILL_HORIZONTAL);
		mNoteCombo.setLayoutData(wGridData);
	}

	private void createValueSpinners() {
		Label wValueLabel = new Label(this, SWT.NONE);
		wValueLabel.setText("金額");

		Composite wValuesRowComp = new Composite(this, SWT.NONE);
		GridData wGridData = new GridData(GridData.FILL_HORIZONTAL);
		wValuesRowComp.setLayoutData(wGridData);

		wValuesRowComp.setLayout(new MyGridLayout(4, false).getMyGridLayout());
		this.setLayoutData(new MyGridData(GridData.BEGINNING,
				GridData.BEGINNING, true, false).getMyGridData());

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

	private void setBookCombos() {
		mBookFromCombo.setInput(mBooks);
		mBookToCombo.setInput(mBooks);
//		for (Book wBook : mBooks) {
//			mBookIdList.add(wBook.getId());
//			mBookFromCombo.add(wBook.getName());
//			mBookToCombo.add(wBook.getName());
//		}
		mBookToCombo.setSelection(mBook);
		for (Book wBook : mBooks) {
			if (!wBook.equals(mBook)) {
				mBookFromCombo.setSelection(wBook);
				break;
			}
		}
		mBookFromCombo.getCombo().setVisibleItemCount(mVisibleComboItemCount);
		mBookToCombo.getCombo().setVisibleItemCount(mVisibleComboItemCount);
	}

	private void createToBookCombo() {
		Label wBookToLabel = new Label(this, SWT.NONE);
		wBookToLabel.setText("移動先");

		mBookToCombo = new MyComboViewer<>(this, SWT.READ_ONLY);
	}

	private void createFromBookCombo() {
		Label wBookFromLabel = new Label(this, SWT.NONE);
		wBookFromLabel.setText("移動元");

		mBookFromCombo = new MyComboViewer<>(this, SWT.READ_ONLY);
	}

	private void createDateTime() {
		Label wDateLabel = new Label(this, SWT.NONE);
		wDateLabel.setText("日付");
		mDateTime = new DateTime(this, SWT.DATE | SWT.BORDER | SWT.DROP_DOWN);
//		mDateTime.addFocusListener(Util.getFocusListenerToDisableIme(getShell(), SWT.NONE));
	}

	private void initWidgets() {
		updateNoteCombo();
	}

	private void setWidgets() {
		Calendar wCal = Calendar.getInstance();
		wCal.setTime(mIncomeRecord.getDate());
		mDateTime.setYear(wCal.get(Calendar.YEAR));
		mDateTime.setMonth(wCal.get(Calendar.MONTH));
		mDateTime.setDay(wCal.get(Calendar.DAY_OF_MONTH));

		mBookFromCombo.setSelection(mExpenseRecord.getBook());
		mBookToCombo.setSelection(mIncomeRecord.getBook());

		mValueSpinner.setSelection(mIncomeRecord.getIncome());
		mFrequencySpinner.setSelection(mIncomeRecord.getFrequency());

		if (!"".equals(mIncomeRecord.getNote())) {
			mNoteItems.set(0, mIncomeRecord.getNote());
			mNoteCombo.setItems(mNoteItems.toArray(new String[0]));
//			mNoteCombo.setItem(0, mIncomeRecord.getNote());
		}
		
		mNoteCombo.select(0);

	}

	private void updateNoteCombo() {
//		String wNote = mNoteCombo.getText();
		mNoteItems = DbUtil.getNoteMove();
		mNoteItems.add(0, "");
		mNoteCombo.setItems(mNoteItems.toArray(new String[0]));
//		mNoteCombo.add(wNote, 0);
		mNoteCombo.select(0);
		mNoteCombo.setVisibleItemCount(mVisibleComboItemCount);
	}

	public void insertRecord() {
		// New/Update, Single/Multi records
		if (mIncomeRecord == null)
			DbUtil.insertNewMoveRecord(createNewMoveItem());
		else
			DbUtil.updateMoveRecord(
					new RecordTableItemForMove(mExpenseRecord, mIncomeRecord), createNewMoveItem());
	}

	public boolean isValidInput() {
		return mValueSpinner.getSelection() > 0 
				&& !mBookFromCombo.getSelectedItem().equals(mBookToCombo.getSelectedItem());
	}

//	private int getSelectedFromBookId() {
//		return mBookIdList.get(mBookFromCombo.getSelectionIndex());
//	}
//
//	private int getSelectedToBookId() {
//		return mBookIdList.get(mBookToCombo.getSelectionIndex());
//	}

	private RecordTableItemForMove createNewMoveItem() {
//		Book wBook = Book.getBook(getSelectedToBookId());
		
		return new RecordTableItemForMove(mBookFromCombo.getSelectedItem(),
				new RecordTableItem.Builder(mBookToCombo.getSelectedItem(), Item.getUndefinedItem(),
						new GregorianCalendar(mDateTime.getYear(), mDateTime.getMonth(), mDateTime
								.getDay()).getTime())
						.income(mValueSpinner.getSelection())
						.frequency(mFrequencySpinner.getSelection())
						.note(mNoteCombo.getText())
						.build());
	}
}
