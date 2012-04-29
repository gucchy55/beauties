package beauties.record.view.dialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;


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
import beauties.common.view.MyGridData;
import beauties.common.view.MyGridLayout;
import beauties.record.model.RecordTableItem;
import beauties.record.model.RecordTableItemForMove;


class CompositeMove extends Composite {

	private int mBookId; // Selected on this Dialog
	private static final int mMoveIncomeItemId = DbUtil.getMoveIncomeItemId();

	private RecordTableItem mIncomeRecord;
	private RecordTableItem mExpenseRecord;

	// Map of ID & Name
	private Map<Integer, String> mBookNameMap;

	// Map of ComboIndex & ID
	private List<Integer> mBookIdList = new ArrayList<Integer>();

	private Combo mBookFromCombo;
	private Combo mBookToCombo;
	private DateTime mDateTime;
	private Spinner mValueSpinner;
	private Spinner mFrequencySpinner;
	private Combo mNoteCombo;
	private String[] mNoteItems;

	private static final int mVisibleComboItemCount = 10;

	public CompositeMove(Composite pParent, int pBookId) {
		super(pParent, SWT.NONE);

		mBookId = pBookId;
		if (mBookId == SystemData.getAllBookInt())
			mBookId = SystemData.getBookMap(false).keySet().iterator().next();
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
		mBookId = mIncomeRecord.getBookId();
		initLayout();
		initWidgets();
		setWidgets();
	}

	private void initLayout() {
		GridLayout wGridLayout = new GridLayout(2, false);
		wGridLayout.verticalSpacing = 10;
		this.setLayout(wGridLayout);
		mBookNameMap = SystemData.getBookMap(false);
		
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
//		mNoteCombo.addFocusListener(new FocusListener() {
//			public void focusGained(FocusEvent event) {
//				getShell().setImeInputMode(SWT.NATIVE);
//			}
//
//			public void focusLost(FocusEvent event) {
//				getShell().setImeInputMode(SWT.NONE);
//			}
//		});
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
		for (Map.Entry<Integer, String> wEntry : mBookNameMap.entrySet()) {
			mBookIdList.add(wEntry.getKey());
			mBookFromCombo.add(wEntry.getValue());
			mBookToCombo.add(wEntry.getValue());
		}
		mBookToCombo.select(mBookIdList.indexOf(mBookId));
		for (int wBookId : mBookIdList) {
			if (wBookId != mBookId) {
				mBookFromCombo.select(mBookIdList.indexOf(wBookId));
				break;
			}
		}

		mBookFromCombo.setVisibleItemCount(mVisibleComboItemCount);
		mBookToCombo.setVisibleItemCount(mVisibleComboItemCount);
	}

	private void createToBookCombo() {
		Label wBookToLabel = new Label(this, SWT.NONE);
		wBookToLabel.setText("移動先");

		mBookToCombo = new Combo(this, SWT.READ_ONLY);
	}

	private void createFromBookCombo() {
		Label wBookFromLabel = new Label(this, SWT.NONE);
		wBookFromLabel.setText("移動元");

		mBookFromCombo = new Combo(this, SWT.READ_ONLY);
	}

	private void createDateTime() {
		Label wDateLabel = new Label(this, SWT.NONE);
		wDateLabel.setText("日付");
		mDateTime = new DateTime(this, SWT.DATE | SWT.BORDER | SWT.DROP_DOWN);
//		mDateTime.addFocusListener(Util.getFocusListenerToDisableIme(getShell(), SWT.NONE));
	}

	private void initWidgets() {

		updateNoteCombo();

//		IControlContentAdapter wContentAdapter = new ComboContentAdapter();
//		IContentProposalProvider wContentProvider = new IContentProposalProvider() {
//			public IContentProposal[] getProposals(String contents, int position) {
//				return Util.createProposals(contents, position, mNoteCombo
//						.getItems(), mNoteCandidateCount);
//			}
//		};
//		new ContentProposalAdapter(mNoteCombo, wContentAdapter, wContentProvider, null,
//				null);
	}

	private void setWidgets() {
		Calendar wCal = Calendar.getInstance();
		wCal.setTime(mIncomeRecord.getDate());
		mDateTime.setYear(wCal.get(Calendar.YEAR));
		mDateTime.setMonth(wCal.get(Calendar.MONTH));
		mDateTime.setDay(wCal.get(Calendar.DAY_OF_MONTH));

		mBookFromCombo.select(mBookIdList.indexOf(mExpenseRecord.getBookId()));
		mBookToCombo.select(mBookIdList.indexOf(mIncomeRecord.getBookId()));

		mValueSpinner.setSelection((int) mIncomeRecord.getIncome());
		mFrequencySpinner.setSelection(mIncomeRecord.getFrequency());

		if (!"".equals(mIncomeRecord.getNote()))
			mNoteCombo.setItem(0, mIncomeRecord.getNote());
		
		mNoteCombo.select(0);

	}

	private void updateNoteCombo() {
		String wNote = mNoteCombo.getText();
		mNoteItems = DbUtil.getNotes(mMoveIncomeItemId);
		mNoteCombo.setItems(mNoteItems);
		mNoteCombo.add(wNote, 0);
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
		return (mValueSpinner.getSelection() > 0 && !mBookFromCombo
				.getText().equals(mBookToCombo.getText()));
	}

	private int getSelectedFromBookId() {
		return mBookIdList.get(mBookFromCombo.getSelectionIndex());
	}

	private int getSelectedToBookId() {
		return mBookIdList.get(mBookToCombo.getSelectionIndex());
	}

	private RecordTableItemForMove createNewMoveItem() {
		return new RecordTableItemForMove(getSelectedFromBookId(),
				new RecordTableItem.Builder(getSelectedToBookId(), SystemData.getUndefinedInt(),
						new GregorianCalendar(mDateTime.getYear(), mDateTime.getMonth(), mDateTime
								.getDay()).getTime())
						.income(mValueSpinner.getSelection())
						.frequency(mFrequencySpinner.getSelection())
						.note(mNoteCombo.getText())
						.build());
	}
}
