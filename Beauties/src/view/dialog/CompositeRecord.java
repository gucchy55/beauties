package view.dialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.RecordTableItem;
import model.SystemData;
import model.db.DbUtil;

import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
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

import util.Util;
import view.util.MyGridData;
import view.util.MyGridLayout;

public class CompositeRecord extends Composite {

	private int mBookId; // Selected on this Dialog
	private boolean mIncome = false;
	private int mCategoryId;
	private int mItemId;

	private RecordTableItem mRecordTableItem;

	private static final String mCategoryAllName = "（すべて）";
	private static final int mCategoryAllId = -1;

	// Map of ID & Name
	private Map<Integer, String> mBookNameMap;
	private Map<Integer, String> mCategoryNameMap;
	private Map<Integer, String> mItemNameMap;

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

	private static final int mNoteCandidateCount = 10;
	private static final int mVisibleComboItemCount = 10;

	public CompositeRecord(Composite pParent) {
		super(pParent, SWT.NONE);

		mBookId = SystemData.getBookId();
		if (SystemData.isBookIdAll()) {
			mBookId = SystemData.getBookMap(false).keySet().iterator().next();
		}

		mIncome = false;
		initLayout();
		initWidgets();
		mDateTime.setFocus();
	}

	// for modify
	public CompositeRecord(Composite pParent, int pActId) {
		super(pParent, SWT.NONE);
		mRecordTableItem = DbUtil.getRecordByActId(pActId);
		mBookId = mRecordTableItem.getBookId();
		if (DbUtil.isIncomeCategory(DbUtil
				.getCategoryIdByItemId(mRecordTableItem.getItemId()))) {
			mIncome = true;
		} else {
			mIncome = false;
		}
		initLayout();
		initWidgets();
		setWidgets();

	}

	private void initLayout() {
		GridData wGridData;

		GridLayout wGridLayout = new GridLayout(2, false);
		wGridLayout.verticalSpacing = 10;
		this.setLayout(wGridLayout);

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

		// DateTime
		Label wDateLabel = new Label(this, SWT.NONE);
		wDateLabel.setText("日付");
		mDateTime = new DateTime(this, SWT.DATE | SWT.BORDER);

		// InExLabel
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

		// Category
		Label wCategoryLabel = new Label(this, SWT.NONE);
		wCategoryLabel.setText("分類");

		mCategoryCombo = new Combo(this, SWT.READ_ONLY);

		// Item
		Label wItemLabel = new Label(this, SWT.NONE);
		wItemLabel.setText("項目");

		mItemCombo = new Combo(this, SWT.READ_ONLY);

		// Value & Frequency
		Label wValueLabel = new Label(this, SWT.NONE);
		wValueLabel.setText("金額");

		Composite wValuesRowComp = new Composite(this, SWT.NONE);
		wGridData = new GridData(GridData.FILL_HORIZONTAL);
		wValuesRowComp.setLayoutData(wGridData);

		wValuesRowComp.setLayout(new MyGridLayout(4, false).getMyGridLayout());
		this.setLayoutData(new MyGridData(GridData.BEGINNING,
				GridData.BEGINNING, true, false).getMyGridData());

		mValueSpinner = new Spinner(wValuesRowComp, SWT.BORDER);
		mValueSpinner.setValues(0, 0, Integer.MAX_VALUE, 0, 100, 10);
		mValueSpinner.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent event) {
				getShell().setImeInputMode(SWT.NONE);
			}

			public void focusLost(FocusEvent event) {

			}
		});

		Label wSpaceLabel = new Label(wValuesRowComp, SWT.NONE);
		wSpaceLabel.setText("    ");

		Label wFrequencyLabel = new Label(wValuesRowComp, SWT.NONE);
		wFrequencyLabel.setText("回数");

		mFrequencySpinner = new Spinner(wValuesRowComp, SWT.BORDER);
		mFrequencySpinner.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent event) {
				getShell().setImeInputMode(SWT.NONE);
			}

			public void focusLost(FocusEvent event) {

			}
		});

		// Note
		Label wNoteLabel = new Label(this, SWT.NONE);
		wNoteLabel.setText("備考");

		mNoteCombo = new Combo(this, SWT.DROP_DOWN | SWT.FILL);
		wGridData = new GridData(GridData.FILL_HORIZONTAL);
		mNoteCombo.setLayoutData(wGridData);

	}

	private void initWidgets() {

		updateCategoryCombo();

		updateItemCombo();

		updateNoteCombo();
		
		IControlContentAdapter wContentAdapter = new ComboContentAdapter();
		IContentProposalProvider wContentProvider = new IContentProposalProvider() {
			public IContentProposal[] getProposals(String contents, int position) {
				return Util.createProposals(contents, position, mNoteCombo
						.getItems(), mNoteCandidateCount);
			}
		};
		new ContentProposalAdapter(mNoteCombo, wContentAdapter, wContentProvider, null,
				null);
	}

	private void setWidgets() {
		Calendar wCal = Calendar.getInstance();
		wCal.setTime(mRecordTableItem.getDate());
		mDateTime.setYear(wCal.get(Calendar.YEAR));
		mDateTime.setMonth(wCal.get(Calendar.MONTH));
		mDateTime.setDay(wCal.get(Calendar.DAY_OF_MONTH));

		mItemId = mRecordTableItem.getItemId();
		if (!mItemIdList.contains(mItemId)) {
			mItemIdList.add(mItemId);
			mItemNameMap.put(mItemId, DbUtil.getItemNameById(mItemId));
		}
		mItemCombo.select(mItemIdList.indexOf(mItemId));

		if (mIncome) {
			mValueSpinner.setSelection((int) mRecordTableItem.getIncome());
		} else {
			mValueSpinner.setSelection((int) mRecordTableItem.getExpense());
		}
		mFrequencySpinner.setSelection(mRecordTableItem.getFrequency());

		if (!"".equals(mRecordTableItem.getNote())) {
			mNoteCombo.setItem(0, mRecordTableItem.getNote());
		}
		mNoteCombo.select(0);

	}

	private void modifyBookId() {
		mBookId = mBookIdList.get(mBookCombo.getSelectionIndex());
		updateCategoryCombo();
		updateItemCombo();
		updateNoteCombo();
	}

	private void modifyIncomeExpense() {
		if (mIncomeExpenseCombo.getSelectionIndex() == 0) {
			mIncome = true;
		} else {
			mIncome = false;
		}
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
		for (Listener l : mCategoryCombo.getListeners(SWT.Modify)) {
			mCategoryCombo.removeListener(SWT.Modify, l);
		}
		mCategoryNameMap = DbUtil.getCategoryNameMap(mBookId, mIncome);
		mCategoryCombo.removeAll();
		mCategoryIdList.clear();

		mCategoryCombo.add(mCategoryAllName);
		mCategoryIdList.add(mCategoryAllId);

		Iterator<Integer> wKeyIt = mCategoryNameMap.keySet().iterator();
		while (wKeyIt.hasNext()) {
			int wCategoryId = wKeyIt.next();
			mCategoryIdList.add(wCategoryId);
			mCategoryCombo.add(mCategoryNameMap.get(wCategoryId));
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
		for (Listener l : mItemCombo.getListeners(SWT.Modify)) {
			mItemCombo.removeListener(SWT.Modify, l);
		}
		if (mCategoryId == mCategoryAllId) {
			mItemNameMap = DbUtil.getItemNameMap(mBookId, mIncome);
		} else {
			mItemNameMap = DbUtil.getItemNameMap(mBookId, mCategoryId);
		}

		mItemCombo.removeAll();
		mItemIdList.clear();

		Iterator<Integer> wKeyIt = mItemNameMap.keySet().iterator();
		while (wKeyIt.hasNext()) {
			int wItemId = wKeyIt.next();
			mItemIdList.add(wItemId);
			mItemCombo.add(mItemNameMap.get(wItemId));
		}

		mItemCombo.select(0);
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
	}

	public void insertRecord() {
		// New/Update, Single/Multi records
		// Common
		int wBookId = mBookIdList.get(mBookCombo.getSelectionIndex());
		int wItemId = mItemIdList.get(mItemCombo.getSelectionIndex());
		int wYear = mDateTime.getYear();
		int wMonth = mDateTime.getMonth() + 1;
		int wDay = mDateTime.getDay();
		int wIncome = 0;
		int wExpense = 0;
		if (mIncome) {
			wIncome = mValueSpinner.getSelection();
		} else {
			wExpense = mValueSpinner.getSelection();
		}
		String wNote = mNoteCombo.getText();
		int wFrequency = mFrequencySpinner.getSelection();

		if (mRecordTableItem == null) {
			// New record
			DbUtil.insertNewRecord(wBookId, wItemId, wYear, wMonth, wDay,
					wIncome, wExpense, wFrequency, wNote);

		} else {
			// Update existing
			DbUtil.updateRecord(mRecordTableItem.getId(), wBookId, wItemId,
					wYear, wMonth, wDay, wIncome, wExpense, wFrequency, wNote);

		}

	}

	public int getValue() {
		return mValueSpinner.getSelection();
	}

}
