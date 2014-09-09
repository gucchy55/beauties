package beauties.record.model;

import java.util.Calendar;
import java.util.Date;

import beauties.common.lib.DbUtil;
import beauties.common.lib.SystemData;
import beauties.common.model.Book;
import beauties.common.model.Category;
import beauties.common.model.Item;



public final class RecordTableItem {
	// 日付 項目 収支 残高 回数 備考
	private final int mId;
	private final Book mBook;
	private final Date mDate;
	private final Item mItem;
	private final int mIncome;
	private final int mExpense;
	private final int mGroupId;
	private final long mBalance;
	private final int mFrequency;
	private final String mNote;
	
	private static Calendar mCalendar = Calendar.getInstance();

	private static final String mBalanceItem = "繰越残高";

	private final boolean isBalanceRow;
	
	private RecordTableItem(Builder builder) {
		this.mId = builder.mActId;
		this.mBook = builder.mBook;
		this.mDate = builder.mDate;
		this.mItem = builder.mItem;
		this.mGroupId = builder.mGroupId;
		this.mIncome = builder.mIncome;
		this.mExpense = builder.mExpense;
		this.mBalance = builder.mBalance;
		this.mFrequency = builder.mFrequency;
		this.mNote = builder.mNote;
		this.isBalanceRow = false;
	}
	
	private RecordTableItem(Date pDate, long pBalance) {
		this.mId = SystemData.getUndefinedInt();
		if (Book.getBook(SystemData.getUndefinedInt()) == null) {
			Book.generateBook(SystemData.getUndefinedInt(), "");
		}
//		this.mBook = SystemData.getUndefinedInt();
		this.mBook = Book.getBook(SystemData.getUndefinedInt());
		this.mDate = pDate;
		if (Item.getItem(SystemData.getUndefinedInt()) == null) {
			Item.generateItem(SystemData.getUndefinedInt(), mBalanceItem, null);
		}
//		this.mItem = SystemData.getUndefinedInt();
		this.mItem = Item.getItem(SystemData.getUndefinedInt());
		this.mGroupId = SystemData.getUndefinedInt();
		this.mIncome = 0;
		this.mExpense = 0;
		this.mBalance = pBalance;
		this.mFrequency = 0;
		this.mNote = "";
		this.isBalanceRow = true;
	}
	
	// for balance row
	public static RecordTableItem createBalanceRowItem(Date pDate, long pBalance) {
		return new RecordTableItem(pDate, pBalance);
	}

	public int getId() {
		return mId;
	}

	public Book getBook() {
		return mBook;
	}

	public Date getDate() {
		return mDate;
	}

	public Item getItem() {
		return mItem;
	}

//	public String getItemName() {
//		if (this.isBalanceRow)
//			return mBalanceItem;
//		return SystemData.getItemName(mItem);
//	}

	public Category getCategory() {
		// return mCategoryId;
//		if (this.isBalanceRow)
//			return SystemData.getUndefinedInt();
//		return SystemData.getCategoryByItemId(mItem);
		return mItem.getCategory();
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

	public long getBalance() {
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
		return DbUtil.isMoveItem(mItem);
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
		return mBook.getName();
//		return DbUtil.getBookNameById(mBookId);
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
		private final Book mBook;
		private final Item mItem;
		private final Date mDate;
		
		private int mActId = SystemData.getUndefinedInt();
		private int mIncome = 0;
		private int mExpense = 0;
		private int mGroupId = 0;
		private long mBalance = 0;
		private int mFrequency = 0;
		private String mNote = "";

		
		public Builder(Book pBook, Item pItem, Date pDate) {
			mBook = pBook;
			mItem = pItem;
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
		
		public Builder balance(long pBalance) {
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
		
//		public boolean isMoveRecord() {
//			return DbUtil.isMoveRecord(this.mActId);
//		}
	}
	
}


