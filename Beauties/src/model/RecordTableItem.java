package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.db.DbUtil;

import util.Util;

public class RecordTableItem {
	// 日付 項目 収支 残高 回数 備考
	private static final int mInitial = SystemData.getUndefinedInt();

	private int mId = mInitial;
	private int mBookId = mInitial;
	private Date mDate;
	private int mItemId = mInitial;
	private String mItemName;
	private int mCategoryId = mInitial;
	private double mIncome = mInitial;
	private double mExpense = mInitial;
	private int mGroupId = mInitial;
	private double mBalance;
	private int mFrequency = mInitial;
	private String mNote;

	private static final String mBalanceItem = "繰越残高";

	private boolean isBalanceRow = false;

	private DateFormat mDateFormat = new SimpleDateFormat("MM/dd");
	
	public RecordTableItem(int pId, int pBookId, Date pDate, int pItemId,
			String pItemName, int pCategoryId, int pGroupId, double pIncome,
			double pExpense, double pBalance, int pFrequency, String pNote) {
		this.mId = pId;
		this.mBookId = pBookId;
		this.mDate = pDate;
		this.mItemId = pItemId;
		this.mItemName = pItemName;
		this.mCategoryId = pCategoryId;
		this.mGroupId = pGroupId;
		this.mIncome = pIncome;
		this.mExpense = pExpense;
		this.mBalance = pBalance;
		this.mFrequency = pFrequency;
		this.mNote = pNote;
	}

	// for balance row
	public RecordTableItem(Date pDate, double pBalance) {
		this.mDate = pDate;
		this.mBalance = pBalance;
		this.isBalanceRow = true;
		this.mItemName = mBalanceItem;
	}

	public RecordTableItem() {
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
		return mItemName;
	}
	
	public int getCategoryId() {
		return mCategoryId;
	}

	public int getGroupId() {
		return mGroupId;
	}

	public double getIncome() {
		return mIncome;
	}

	public double getExpense() {
		return mExpense;
	}

	public double getBalance() {
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
	
	public String getDateString() {
		String wDateString = mDateFormat.format(mDate);
		wDateString += "(" + Util.getDayOfTheWeekShort(mDate) + ")";
		return wDateString;
	}
	
	public boolean isBalanceRow() {
		return isBalanceRow;
	}
	
	public boolean isMoveItem() {
		if (DbUtil.isMoveItem(mItemId)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isIncome() {
		return (mIncome > 0);
	}
	
	public boolean isExpense() {
		return (mExpense > 0);
	}

}
