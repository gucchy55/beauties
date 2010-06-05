package model;

import java.util.Calendar;
import java.util.Date;

import model.db.DbUtil;


public final class RecordTableItem {
	// 日付 項目 収支 残高 回数 備考
	private final int mId;
	private final int mBookId;
	private final Date mDate;
	private final int mItemId;
	private final int mIncome;
	private final int mExpense;
	private final int mGroupId;
	private final int mBalance;
	private final int mFrequency;
	private final String mNote;
	
	private static Calendar mCalendar = Calendar.getInstance();

	private static final String mBalanceItem = "繰越残高";

	private final boolean isBalanceRow;
	
	private RecordTableItem(Builder builder) {
		this.mId = builder.mActId;
		this.mBookId = builder.mBookId;
		this.mDate = builder.mDate;
		this.mItemId = builder.mItemId;
		this.mGroupId = builder.mGroupId;
		this.mIncome = builder.mIncome;
		this.mExpense = builder.mExpense;
		this.mBalance = builder.mBalance;
		this.mFrequency = builder.mFrequency;
		this.mNote = builder.mNote;
		this.isBalanceRow = false;
	}
	
	private RecordTableItem(Date pDate, int pBalance) {
		this.mId = SystemData.getUndefinedInt();
		this.mBookId = SystemData.getUndefinedInt();
		this.mDate = pDate;
		this.mItemId = SystemData.getUndefinedInt();
		this.mGroupId = SystemData.getUndefinedInt();
		this.mIncome = 0;
		this.mExpense = 0;
		this.mBalance = pBalance;
		this.mFrequency = 0;
		this.mNote = "";
		this.isBalanceRow = true;
	}
	
	// for balance row
	public static RecordTableItem createBalanceRowItem(Date pDate, int pBalance) {
		return new RecordTableItem(pDate, pBalance);
	}

	public int getId() {
		return mId;
	}

	public int getBookId() {
		return mBookId;
	}

	public Date getDate() {
		return mDate;
	}

	public int getItemId() {
		return mItemId;
	}

	public String getItemName() {
		if (this.isBalanceRow)
			return mBalanceItem;
		return DbUtil.getItemNameById(mItemId);
	}

	public int getCategoryId() {
		// return mCategoryId;
		if (this.isBalanceRow)
			return SystemData.getUndefinedInt();
		return DbUtil.getCategoryIdByItemId(mItemId);
	}

	public int getGroupId() {
		return mGroupId;
	}

	public int getIncome() {
		return mIncome;
	}

	public int getExpense() {
		return mExpense;
	}

	public int getBalance() {
		return mBalance;
	}

	public int getFrequency() {
		return mFrequency;
	}

	public String getNote() {
		if (mNote == null) {
			return "";
		} else {
			return mNote;
		}
	}

	public boolean isBalanceRow() {
		return isBalanceRow;
	}

	public boolean isMoveItem() {
		return DbUtil.isMoveItem(mItemId);
	}

	public boolean isIncome() {
		return (mIncome > 0);
	}

	public boolean isExpense() {
		return (mExpense > 0);
	}

	public String getBookName() {
		if (isBalanceRow)
			return "";
		return DbUtil.getBookNameById(mBookId);
	}
	
	public int getYear() {
		mCalendar.setTime(this.mDate);
		return mCalendar.get(Calendar.YEAR);
	}
	
	public int getMonth() {
		mCalendar.setTime(this.mDate);
		return mCalendar.get(Calendar.MONTH);
	}
	
	public Calendar getCal() {
		mCalendar.setTime(this.mDate);
		return mCalendar;
	}
	
	public static class Builder {
		private final int mBookId;
		private final int mItemId;
		private final Date mDate;
		
		private int mActId = SystemData.getUndefinedInt();
		private int mIncome = 0;
		private int mExpense = 0;
		private int mGroupId = 0;
		private int mBalance = 0;
		private int mFrequency = 0;
		private String mNote = "";

		
		public Builder(int pBookId, int pItemId, Date pDate) {
			mBookId = pBookId;
			mItemId = pItemId;
			mDate = pDate;
		}
		
		public Builder actId(int pActId) {
			mActId = pActId;
			return this;
		}
		
		public Builder income(int pIncome) {
			mIncome = pIncome;
			return this;
		}
		
		public Builder expense(int pExpense) {
			mExpense = pExpense;
			return this;
		}
		
		public Builder groupId(int pGroupId) {
			mGroupId = pGroupId;
			return this;
		}
		
		public Builder balance(int pBalance) {
			mBalance = pBalance;
			return this;
		}
		
		public Builder frequency(int pFrequency) {
			mFrequency = pFrequency;
			return this;
		}
		
		public Builder note(String pNote) {
			mNote = pNote;
			return this;
		}
		
		public RecordTableItem build() {
			return new RecordTableItem(this);
		}
	}
	
}


