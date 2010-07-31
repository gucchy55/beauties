package beauties.record.view.dialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import beauties.model.SystemData;
import beauties.model.db.DbUtil;
import beauties.record.model.RecordTableItem;

import util.Util;
import util.view.MyGridData;
import util.view.MyGridLayout;

class CompositeRecord extends Composite {

	private int mBookId; // Selected on this Dialog
	private boolean mIncome = false;
	private int mCategoryId;
	private int mItemId;

	private RecordTableItem mRecordTableItem;

	private static final String mCategoryAllName = "（すべて）";
	private static final int mCategoryAllId = SystemData.getUndefinedInt();

	// Map of ID & Name
	private Map<Integer, String> mBookNameMap;

	// Map of ComboIndex & ID
	private List<Integer> mBookIdList = new ArrayList<Integer>();
	private List<Integer> mCategoryIdList = new ArrayList<Integer>();
	private List<Integer> mItemIdList = new ArrayList<Integer>();

	private Combo mBookCombo;
	private DateTime mDateTime;
	private Combo mIncomeExpenseCombo;
	private Combo mCategoryCombo;
	private Combo mItemCombo;
	private Spinner mValueSpinner;
	private Spinner mFrequencySpinner;
	private Combo mNoteCombo;
	private String[] mNoteItems;

	private static final int mVisibleComboItemCount = 10;

	public CompositeRecord(Composite pParent, int pBookId) {
		super(pParent, SWT.NONE);

		mBookId = pBookId;

		if (mBookId == SystemData.getAllBookInt())
			mBookId = SystemData.getBookMap(false).keySet().iterator().next();

		mIncome = false;
		initLayout();
		initWidgets();
		mDateTime.setFocus();
	}

	// for modify
	public CompositeRecord(Composite pParent, RecordTableItem pRecordTableItem) {
		super(pParent, SWT.NONE);
		mRecordTableItem = pRecordTableItem;
		mBookId = mRecordTableItem.getBookId();
		mIncome = DbUtil.isIncomeCategory(SystemData.getCategoryByItemId(mRecordTableItem.getItemId()));
		initLayout();
		initWidgets();
		setWidgets();
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
		mNoteCombo.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent event) {
				getShell().setImeInputMode(SWT.NATIVE);
			}

			public void focusLost(FocusEvent event) {
				getShell().setImeInputMode(SWT.NONE);
			}
		});
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
		mValueSpinner.addFocusListener(Util.getFocusListenerToDisableIme(getShell(), SWT.NONE));

		Label wSpaceLabel = new Label(wValuesRowComp, SWT.NONE);
		wSpaceLabel.setText("    ");

		Label wFrequencyLabel = new Label(wValuesRowComp, SWT.NONE);
		wFrequencyLabel.setText("回数");

		mFrequencySpinner = new Spinner(wValuesRowComp, SWT.BORDER);
		mFrequencySpinner.addFocusListener(Util.getFocusListenerToDisableIme(getShell(), SWT.NONE));
	}

	private void initItemCombo() {
		Label wItemLabel = new Label(this, SWT.NONE);
		wItemLabel.setText("項目");
		mItemCombo = new Combo(this, SWT.READ_ONLY);
	}

	private void initCategoryCombo() {
		Label wCategoryLabel = new Label(this, SWT.NONE);
		wCategoryLabel.setText("分類");
		mCategoryCombo = new Combo(this, SWT.READ_ONLY);
	}

	private void initInExCombo() {
		Label wInExLabel = new Label(this, SWT.NONE);
		wInExLabel.setText("収支");

		mIncomeExpenseCombo = new Combo(this, SWT.READ_ONLY);
		mIncomeExpenseCombo.add("収入");
		mIncomeExpenseCombo.add("支出");
		if (mIncome) {
			mIncomeExpenseCombo.select(0);
		} else {
			mIncomeExpenseCombo.select(1);
		}
		mIncomeExpenseCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				modifyIncomeExpense();
			}
		});
	}

	private void initDateTime() {
		// DateTime
		Label wDateLabel = new Label(this, SWT.NONE);
		wDateLabel.setText("日付");
		mDateTime = new DateTime(this, SWT.DATE | SWT.BORDER | SWT.DROP_DOWN);
		mDateTime.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent event) {
				getShell().setImeInputMode(SWT.NONE);
			}

			public void focusLost(FocusEvent event) {
			}
		});
	}

	private void initBookCombo() {
		// BookName
		Label wBookLabel = new Label(this, SWT.NONE);
		wBookLabel.setText("帳簿");

		mBookCombo = new Combo(this, SWT.READ_ONLY);
		mBookNameMap = SystemData.getBookMap(false);
		mBookIdList.clear();
		mBookCombo.removeAll();

		Iterator<Integer> wKeyIt = mBookNameMap.keySet().iterator();
		while (wKeyIt.hasNext()) {
			int wBookId = wKeyIt.next();
			mBookIdList.add(wBookId);
			mBookCombo.add(mBookNameMap.get(wBookId));
		}

		mBookCombo.setVisibleItemCount(mVisibleComboItemCount);

		mBookCombo.select(mBookIdList.indexOf(mBookId));

		mBookCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				modifyBookId();
			}
		});
	}

	private void initWidgets() {

		updateCategoryCombo();

		updateItemCombo();

		updateNoteCombo();

		// IControlContentAdapter wContentAdapter = new ComboContentAdapter();
		// IContentProposalProvider wContentProvider = new
		// IContentProposalProvider() {
		// public IContentProposal[] getProposals(String contents, int position)
		// {
		// return Util.createProposals(contents, position, mNoteCombo
		// .getItems(), mNoteCandidateCount);
		// }
		// };
		// new ContentProposalAdapter(mNoteCombo, wContentAdapter,
		// wContentProvider, null,
		// null);
	}

	private void setWidgets() {
		Calendar wCal = Calendar.getInstance();
		wCal.setTime(mRecordTableItem.getDate());
		mDateTime.setYear(wCal.get(Calendar.YEAR));
		mDateTime.setMonth(wCal.get(Calendar.MONTH));
		mDateTime.setDay(wCal.get(Calendar.DAY_OF_MONTH));

		mItemId = mRecordTableItem.getItemId();
		if (!mItemIdList.contains(mItemId)) 
			mItemIdList.add(mItemId);

		mItemCombo.select(mItemIdList.indexOf(mItemId));

		if (mIncome)
			mValueSpinner.setSelection((int) mRecordTableItem.getIncome());
		else
			mValueSpinner.setSelection((int) mRecordTableItem.getExpense());

		mFrequencySpinner.setSelection(mRecordTableItem.getFrequency());

		if (!"".equals(mRecordTableItem.getNote()))
			mNoteCombo.setItem(0, mRecordTableItem.getNote());
		mNoteCombo.select(0);

	}

	private void modifyBookId() {
		mBookId = mBookIdList.get(mBookCombo.getSelectionIndex());
		updateCategoryCombo();
		updateItemCombo();
		updateNoteCombo();
	}

	private void modifyIncomeExpense() {
		mIncome = mIncomeExpenseCombo.getSelectionIndex() == 0;
		updateCategoryCombo();
		updateItemCombo();
		updateNoteCombo();
	}

	private void modifyCategoryId() {
		mCategoryId = mCategoryIdList.get(mCategoryCombo.getSelectionIndex());
		updateItemCombo();
		updateNoteCombo();
	}

	private void modifyItemId() {
		mItemId = mItemIdList.get(mItemCombo.getSelectionIndex());
		updateNoteCombo();
	}

	private void updateCategoryCombo() {
		for (Listener l : mCategoryCombo.getListeners(SWT.Modify))
			mCategoryCombo.removeListener(SWT.Modify, l);

		mCategoryCombo.removeAll();
		mCategoryIdList.clear();

		mCategoryCombo.add(mCategoryAllName);
		mCategoryIdList.add(mCategoryAllId);
		mCategoryIdList.addAll(DbUtil.getCategoryIdList(mBookId, mIncome));

		for (int wId : mCategoryIdList) {
			if (wId != mCategoryAllId)
				mCategoryCombo.add(SystemData.getCategoryName(wId));
		}

		mCategoryCombo.setVisibleItemCount(mVisibleComboItemCount);

		mCategoryCombo.select(0);
		mCategoryId = mCategoryIdList.get(0);

		mCategoryCombo.pack();
		mCategoryCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				modifyCategoryId();
			}
		});

	}

	private void updateItemCombo() {
		for (Listener l : mItemCombo.getListeners(SWT.Modify))
			mItemCombo.removeListener(SWT.Modify, l);

		if (mCategoryId == mCategoryAllId)
			mItemIdList = DbUtil.getItemIdList(mBookId, mIncome);
		else
			mItemIdList = DbUtil.getItemIdList(mBookId, mCategoryId);
		
		mItemCombo.removeAll();
		for (int wId : mItemIdList)
			mItemCombo.add(SystemData.getItemName(wId));

		mItemCombo.select(0);
		if (!mItemIdList.isEmpty())
			mItemId = mItemIdList.get(0);

		mItemCombo.pack();
		mItemCombo.setVisibleItemCount(10);

		mItemCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				modifyItemId();
			}
		});
	}

	private void updateNoteCombo() {
		String wNote = mNoteCombo.getText();
		mNoteItems = DbUtil.getNotes(mItemId);
		mNoteCombo.setItems(mNoteItems);
		mNoteCombo.add(wNote, 0);
		mNoteCombo.select(0);
		mNoteCombo.setVisibleItemCount(mVisibleComboItemCount);

	}

	public void updateForNextInput() {
		updateCategoryCombo();
		updateItemCombo();

		mNoteCombo.setItem(0, "");
		mNoteCombo.select(0);
		updateNoteCombo();

		mValueSpinner.setSelection(0);
		mFrequencySpinner.setSelection(0);
		mItemCombo.setFocus();
	}

	public void insertRecord() {
		// New/Update, Single/Multi records
		// Common
		if (mItemIdList.isEmpty()) {
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

	private int getSelectedBookId() {
		return mBookIdList.get(mBookCombo.getSelectionIndex());
	}

	private int getSelectedItemId() {
		return mItemIdList.get(mItemCombo.getSelectionIndex());
	}

	private RecordTableItem createNewRecordTableItem() {
		return new RecordTableItem.Builder(
				getSelectedBookId(),
				getSelectedItemId(),
				new GregorianCalendar(mDateTime.getYear(), mDateTime.getMonth(), mDateTime.getDay())
						.getTime()).frequency(mFrequencySpinner.getSelection()).note(
				mNoteCombo.getText()).income(mIncome ? mValueSpinner.getSelection() : 0).expense(
				mIncome ? 0 : mValueSpinner.getSelection()).build();
	}

}
