package model.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import util.Util;

import model.Book;
import model.ConfigItem;
import model.RecordTableItem;
import model.SummaryTableItem;
import model.SystemData;

public class DbUtil {

	// Systemテーブル関連
	private final static String mSystemTable = "system";
	private final static String mSystemValueCol = "NUM_VALUE";
	private final static String mSystemIDCol = "SID";
	private final static String mCutOff = "'CUTOFF_DT'";
	private final static String mFiscalMonth = "'FISCAL_MH'";
	private final static String mShowGridLine = "'SHOW_GRIDLINES'";

	// Categoryテーブル関連
	private final static String mCategoryTable = "cbm_category";
	private final static String mCategoryNameCol = "CATEGORY_NAME";
	private final static String mCategoryIdCol = "CATEGORY_ID";
	private final static String mCategoryRexpCol = "REXP_DIV"; // 1: Income, 2:
	private final static int mIncomeRexp = 1; // REXP_DIVの値（Income）
	private final static int mExpenseRexp = 2; // REXP_DIVの値（Expense）

	// BookとItemの関連テーブル
	private final static String mBookItemTable = "cbr_book";

	// Itemテーブル関連
	private final static String mItemTable = "cbm_item";
	private final static String mItemNameCol = "ITEM_NAME";
	private final static String mItemIdCol = "ITEM_ID";

	// ACTアイテム関連
	private final static String mActTable = "cbt_act";
	private final static String mActIdCol = "ACT_ID";
	private final static String mActDtCol = "ACT_DT";
	private final static String mActIncomeCol = "INCOME";
	private final static String mActExpenseCol = "EXPENSE";
	private final static String mActFreqCol = "FREQUENCY";
	private final static String mGroupIdCol = "GROUP_ID";

	// 共通フラグ等
	private final static String mDelFlgCol = "DEL_FLG";
	private final static String mMoveFlgCol = "MOVE_FLG";
	private final static String mSortKeyCol = "SORT_KEY";

	// Bookテーブル関連
	private final static String mBookTable = "cbm_book";
	private final static String mBookIdCol = "BOOK_ID";
	private final static String mBookNameCol = "BOOK_NAME";
	private final static String mBookBalanceCol = "BALANCE";

	// Noteテーブル関連
	private final static String mNoteTable = "cbt_note";
	private final static String mNoteIdCol = "NOTE_ID";
	private final static String mNoteNameCol = "NOTE_NAME";

	private final static int mAllBookId = SystemData.getAllBookInt();
	private final static String mCategorySpecialFlgCol = "SPECIAL_FLG";
	private final static String mCategoryTempFlgCol = "TEMP_FLG";

	// エスケープ文字
	private final static String mEscapeChar = "\\";
	private final static String[] mSpecialChars = { "\\", "\'", "\"" };

	private final static int mInitialSortKeyCategory = 1; // 0は現金移動用
	private final static int mInitialSortKeyItem = 2; // 1は現金移動用

	private static DbAccess mDbAccess = DbAccess.getInstance();

	private DbUtil() {

	}

	public static int getCutOff() {
		int wCutOff = SystemData.getUndefinedInt();
		ResultSet wResultSet = mDbAccess.executeQuery("select " + mSystemValueCol + " from "
				+ mSystemTable + " where "
				+ mSystemIDCol + " = " + mCutOff);
		try {
			wResultSet.next();
			wCutOff = wResultSet.getInt(mSystemValueCol);
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		return wCutOff;
	}

	public static void updateCutOff(int pCutOff) {
		String wQuery = "update " + mSystemTable + " set " + mSystemValueCol + " = " + pCutOff
				+ " where "
				+ mSystemIDCol + " = " + mCutOff;
		mDbAccess.executeUpdate(wQuery);
	}

	public static int getFisCalMonth() {
		int wFiscalMonth = SystemData.getUndefinedInt();
		ResultSet wResultSet = mDbAccess.executeQuery("select " + mSystemValueCol + " from "
				+ mSystemTable + " where "
				+ mSystemIDCol + " = " + mFiscalMonth);

		try {
			wResultSet.next();
			wFiscalMonth = wResultSet.getInt(mSystemValueCol);
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wFiscalMonth;
	}

	public static void updateFisCalMonth(int pFiscalMonth) {
		String wQuery = "update " + mSystemTable + " set " + mSystemValueCol + " = " + pFiscalMonth
				+ " where "
				+ mSystemIDCol + " = " + mFiscalMonth;
		mDbAccess.executeUpdate(wQuery);
	}

	public static boolean showGridLine() {
		int wShowGridLine = 0;
		ResultSet wResultSet = mDbAccess.executeQuery("select " + mSystemValueCol + " from "
				+ mSystemTable + " where "
				+ mSystemIDCol + " = " + mShowGridLine);

		try {
			wResultSet.next();
			wShowGridLine = wResultSet.getInt(mSystemValueCol);
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		return (wShowGridLine == 1);
	}

	public static void updateShowGridLine(boolean pShowGridLine) {
		String wQuery = "update " + mSystemTable + " set " + mSystemValueCol + " = "
				+ (pShowGridLine ? 1 : 0)
				+ " where " + mSystemIDCol + " = " + mShowGridLine;
		mDbAccess.executeUpdate(wQuery);
	}

	public static int getCategoryIdByItemId(int pItemId) {
		ResultSet wResultSet = mDbAccess.executeQuery("select " + mCategoryIdCol + " from "
				+ mItemTable + " where "
				+ mItemIdCol + " = " + pItemId);

		int wCategoryId = -1;

		try {
			wResultSet.next();
			wCategoryId = wResultSet.getInt(mCategoryIdCol);
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wCategoryId;
	}

	public static String getItemNameById(int pItemId) {
		ResultSet wResultSet = mDbAccess.executeQuery("select " + mItemNameCol + " from "
				+ mItemTable + " where "
				+ mItemIdCol + " = " + pItemId);

		String wItemName = "";

		try {
			wResultSet.next();
			wItemName = wResultSet.getString(mItemNameCol);
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wItemName;

	}

	public static RecordTableItem[][] getRecordTableItems(Date pStartDate, Date pEndDate,
			int pBookId) {
		List<RecordTableItem> wRecordTableItemListUp = new ArrayList<RecordTableItem>();
		List<RecordTableItem> wRecordTableItemListBottom = new ArrayList<RecordTableItem>();

		String wStart = getDateStrings(pStartDate);
		String wEnd = getDateStrings(pEndDate);

		String wBookWhere = getBookWhere(pBookId);

		String wMoveFlgWhere = "";
		if (pBookId == mAllBookId) {
			wMoveFlgWhere = " and " + mItemTable + "." + mMoveFlgCol + " = b'0'";
		}

		int wBalance = getBalance(pStartDate, pBookId, false);
		RecordTableItem wBalanceRecord = RecordTableItem.createBalanceRowItem(pStartDate, wBalance);
		wRecordTableItemListUp.add(wBalanceRecord);

		String wQuery = "select " + mActIdCol + ", " + mBookIdCol + ", " + mActDtCol + ", "
				+ mActTable + "."
				+ mItemIdCol + ", " + mGroupIdCol + ", " + mActIncomeCol + ", " + mActExpenseCol
				+ ", " + mActFreqCol
				+ ", " + mNoteNameCol + " from " + mActTable + ", " + mItemTable + ", "
				+ mCategoryTable + " where "
				+ mItemTable + "." + mItemIdCol + " = " + mActTable + "." + mItemIdCol + " and "
				+ mItemTable + "."
				+ mCategoryIdCol + " = " + mCategoryTable + "." + mCategoryIdCol + " and "
				+ mActDtCol + " between "
				+ wStart + " and " + wEnd + " and " + mActTable + "." + mDelFlgCol + " = b'0' "
				+ wMoveFlgWhere
				+ " and " + wBookWhere + " order by " + mActDtCol + ", " + mCategoryTable + "."
				+ mCategoryRexpCol
				+ ", " + mCategoryTable + "." + mSortKeyCol + ", " + mItemTable + "." + mSortKeyCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			Date wDateNow = new Date();
			while (wResultSet.next()) {
				wBalance += wResultSet.getInt(mActIncomeCol) - wResultSet.getInt(mActExpenseCol);
				
				RecordTableItem wRecord = new RecordTableItem.Builder(
						wResultSet.getInt(mBookIdCol), wResultSet.getInt(mItemIdCol), wResultSet
								.getDate(mActDtCol))
						.actId(wResultSet.getInt(mActIdCol))
						.balance(wBalance)
						.expense(wResultSet.getInt(mActExpenseCol))
						.frequency(wResultSet.getInt(mActFreqCol))
						.groupId(wResultSet.getInt(mGroupIdCol))
						.income(wResultSet.getInt(mActIncomeCol))
						.note(wResultSet.getString(mNoteNameCol))
						.build();
				if (wResultSet.getDate(mActDtCol).after(wDateNow)) {
					wRecordTableItemListBottom.add(wRecord);
				} else {
					wRecordTableItemListUp.add(wRecord);
				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		RecordTableItem[][] wRet = new RecordTableItem[2][];
		wRet[0] = (RecordTableItem[]) wRecordTableItemListUp.toArray(new RecordTableItem[0]);
		wRet[1] = (RecordTableItem[]) wRecordTableItemListBottom.toArray(new RecordTableItem[0]);

		return wRet;

	}

	public static Map<Integer, String> getBookNameMap() {
		Map<Integer, String> wBookMap = new LinkedHashMap<Integer, String>();

		String wQuery = "select " + mBookIdCol + ", " + mBookNameCol + " from " + mBookTable
				+ " where " + mDelFlgCol
				+ " = b'0' " + " order by " + mSortKeyCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				int wBookId = wResultSet.getInt(mBookIdCol);
				String wBookName = wResultSet.getString(mBookNameCol);
				wBookMap.put(wBookId, wBookName);
			}

			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		return wBookMap;
	}

	private static RecordTableItem getRecordByActId(int pId) {
		int wId = SystemData.getUndefinedInt();
		int wBookId = SystemData.getUndefinedInt();
		Date wDate = new Date();
		int wItemId = SystemData.getUndefinedInt();
		int wGroupId = SystemData.getUndefinedInt();
		int wIncome = SystemData.getUndefinedInt();
		int wExpense = SystemData.getUndefinedInt();
		int wFrequency = SystemData.getUndefinedInt();
		String wNote = "";

		String wQuery = "select " + mActIdCol + ", " + mBookIdCol + ", " + mActDtCol + ", "
				+ mActTable + "."
				+ mItemIdCol + ", " + mGroupIdCol + ", " + mActIncomeCol + ", " + mActExpenseCol
				+ ", " + mActFreqCol
				+ ", " + mNoteNameCol + " from " + mActTable + " where " + mActIdCol + " = " + pId;

		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			wResultSet.next();

			wId = wResultSet.getInt(mActIdCol);
			wBookId = wResultSet.getInt(mBookIdCol);
			wDate = wResultSet.getDate(mActDtCol);
			wItemId = wResultSet.getInt(mItemIdCol);
			wGroupId = wResultSet.getInt(mGroupIdCol);
			wIncome = wResultSet.getInt(mActIncomeCol);
			wExpense = wResultSet.getInt(mActExpenseCol);
			wFrequency = wResultSet.getInt(mActFreqCol);
			wNote = wResultSet.getString(mNoteNameCol);
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return new RecordTableItem.Builder(wBookId, wItemId, wDate)
				.actId(wId).expense(wExpense).frequency(wFrequency)
				.groupId(wGroupId).income(wIncome).note(wNote).build();
	}

	public static String[] getNotes(int pItemId) {
		List<String> wResultList = new ArrayList<String>();

		String wQuery = "select " + mNoteNameCol + " from " + mNoteTable + " where " + mItemIdCol
				+ " = " + pItemId
				+ " and " + mDelFlgCol + " = b'0' " + " order by " + mNoteIdCol + " desc ";

		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				wResultList.add(wResultSet.getString(mNoteNameCol));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wResultList.toArray(new String[0]);

	}

	// For all categories
	public static Map<Integer, String> getItemNameMap(int pBookId, boolean pIncome) {
		Map<Integer, String> wResultMap = new LinkedHashMap<Integer, String>();
		int wRexp = mIncomeRexp;
		if (!pIncome) {
			wRexp = mExpenseRexp;
		}

		String wQuery = "select " + mItemTable + "." + mItemIdCol + ", " + mItemTable + "."
				+ mItemNameCol;
		wQuery += " from " + mItemTable + " , " + mBookItemTable + ", " + mCategoryTable;
		wQuery += " where " + mItemTable + "." + mItemIdCol + " = " + mBookItemTable + "."
				+ mItemIdCol + " and "
				+ mCategoryTable + "." + mCategoryIdCol + " = " + mItemTable + "." + mCategoryIdCol;
		if (pBookId != SystemData.getAllBookInt()) {
			wQuery += " and " + mBookItemTable + "." + mBookIdCol + " = " + pBookId;
		}
		wQuery += " and " + mCategoryTable + "." + mCategoryRexpCol + " = " + wRexp + " and "
				+ mBookItemTable + "."
				+ mDelFlgCol + " = b'0' " + " and " + mItemTable + "." + mDelFlgCol + " = b'0' ";
		wQuery += " order by " + mItemTable + "." + mSortKeyCol;
		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				wResultMap.put(wResultSet.getInt(mItemIdCol), wResultSet.getString(mItemNameCol));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wResultMap;
	}

	// For each category
	public static Map<Integer, String> getItemNameMap(int pBookId, int pCategoryId) {
		Map<Integer, String> wResultMap = new LinkedHashMap<Integer, String>();

		String wQuery = "select " + mItemTable + "." + mItemIdCol + ", " + mItemTable + "."
				+ mItemNameCol;
		wQuery += " from " + mItemTable + " , " + mBookItemTable + ", " + mCategoryTable;
		wQuery += " where " + mItemTable + "." + mItemIdCol + " = " + mBookItemTable + "."
				+ mItemIdCol + " and "
				+ mCategoryTable + "." + mCategoryIdCol + " = " + mItemTable + "." + mCategoryIdCol
				+ " and "
				+ mBookItemTable + "." + mBookIdCol + " = " + pBookId + " and " + mCategoryTable
				+ "." + mCategoryIdCol
				+ " = " + pCategoryId + " and " + mBookItemTable + "." + mDelFlgCol + " = b'0' "
				+ " and " + mItemTable
				+ "." + mDelFlgCol + " = b'0' ";
		wQuery += " order by " + mItemTable + "." + mSortKeyCol;
		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				wResultMap.put(wResultSet.getInt(mItemIdCol), wResultSet.getString(mItemNameCol));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wResultMap;
	}

	public static Map<Integer, String> getCategoryNameMap(int pBookId, boolean pIncome) {
		Map<Integer, String> wResultMap = new LinkedHashMap<Integer, String>();
		int wRexp = mIncomeRexp;
		if (!pIncome) {
			wRexp = mExpenseRexp;
		}

		String wQuery = "select count( " + mItemTable + "." + mItemNameCol + " ), "
				+ mCategoryTable + "."
				+ mCategoryIdCol + ", " + mCategoryTable + "." + mCategoryNameCol;
		wQuery += " from " + mItemTable + ", " + mBookItemTable + ", " + mCategoryTable;
		wQuery += " where " + mItemTable + "." + mItemIdCol + " = " + mBookItemTable + "."
				+ mItemIdCol + " and "
				+ mCategoryTable + "." + mCategoryIdCol + " = " + mItemTable + "." + mCategoryIdCol;
		if (pBookId != SystemData.getAllBookInt()) {
			wQuery += " and " + mBookItemTable + "." + mBookIdCol + " = " + pBookId;
		}
		wQuery += " and " + mCategoryTable + "." + mCategoryRexpCol + " = " + wRexp + " and "
				+ mBookItemTable + "."
				+ mDelFlgCol + " = b'0' " + " and " + mItemTable + "." + mDelFlgCol + " = b'0' ";
		wQuery += " group by " + mCategoryTable + "." + mCategoryNameCol;
		wQuery += " order by " + mCategoryTable + "." + mSortKeyCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				wResultMap.put(wResultSet.getInt(mCategoryIdCol), wResultSet
						.getString(mCategoryNameCol));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wResultMap;
	}

	// 設定時に使用
	public static Map<Integer, String> getAllCategoryNameMap(boolean pIncome) {
		Map<Integer, String> wResultMap = new LinkedHashMap<Integer, String>();
		int wRexp = mIncomeRexp;
		if (!pIncome) {
			wRexp = mExpenseRexp;
		}
		String wQuery = "select " + mCategoryIdCol + ", " + mCategoryNameCol;
		wQuery += " from " + mCategoryTable;
		wQuery += " where " + mCategoryRexpCol + " = " + wRexp + " and " + mDelFlgCol + " = b'0' "
				+ " and "
				+ mSortKeyCol + " > " + 0;
		wQuery += " order by " + mSortKeyCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				wResultMap.put(wResultSet.getInt(mCategoryIdCol), wResultSet
						.getString(mCategoryNameCol));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wResultMap;
	}

	public static boolean isIncomeCategory(int pCategoryId) {
		String wQuery = "select " + mCategoryRexpCol;
		wQuery += " from " + mCategoryTable;
		wQuery += " where " + mCategoryIdCol + " = " + pCategoryId + " and " + mDelFlgCol
				+ " = b'0' ";

		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);
		try {
			wResultSet.next();
			int wRexp = wResultSet.getInt(mCategoryRexpCol);
			if (wRexp == mIncomeRexp) {
				wResultSet.close();
				return true;
			} else {
				wResultSet.close();
				return false;
			}

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return false;

	}

	public static void insertNewRecord(int pBookId, int pItemId, int pYear, int pMonth, int pDay,
			int pIncome,
			int pExpense, int pFrequency, String pNote) {

		pNote = getNoteStringWithEscape(pNote);

		String wQueryBase1 = "insert into  " + mActTable + " ( " + mBookIdCol + "," + mItemIdCol
				+ "," + mActDtCol
				+ "," + mActIncomeCol + "," + mActExpenseCol;
		String wQueryBase2 = " values(" + pBookId + "," + pItemId + ",'" + pYear + "-" + pMonth
				+ "-" + pDay + "',"
				+ pIncome + "," + pExpense;
		String wQueryNote1 = "";
		String wQueryNote2 = "";
		String wQuery = "";

		if (!"".equals(pNote)) {
			wQueryNote1 = "," + mNoteNameCol;
			wQueryNote2 = ",'" + pNote + "'";
		}

		if (pFrequency == 0) {
			wQuery = wQueryBase1 + wQueryNote1 + ") " + wQueryBase2 + wQueryNote2 + ")";
			// System.out.println(wQuery);
			mDbAccess.executeUpdate(wQuery);

		} else if (pFrequency > 0) {
			int wGroupId = getNewGroupId();
			Calendar wCalBase = new GregorianCalendar(pYear, pMonth - 1, pDay);
			for (int i = 0; i < pFrequency + 1; i++) {
				Calendar wCal = (Calendar) wCalBase.clone();
				wCal.add(Calendar.MONTH, +i);
				String wDate = getDateStrings(wCal.getTime());

				wQueryBase1 = "insert into  " + mActTable + " ( " + mBookIdCol + "," + mItemIdCol
						+ "," + mActDtCol
						+ "," + mActIncomeCol + "," + mActExpenseCol + "," + mGroupIdCol + ","
						+ mActFreqCol;

				wQueryBase2 = " values(" + pBookId + "," + pItemId + "," + wDate + "," + pIncome
						+ "," + pExpense + ","
						+ wGroupId + "," + (pFrequency - i);

				wQuery = wQueryBase1 + wQueryNote1 + ") " + wQueryBase2 + wQueryNote2 + ")";
				// System.out.println(wQuery);
				mDbAccess.executeUpdate(wQuery);

			}

		}

		// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
		if (!"".equals(pNote)) {
			updateNoteTable(pItemId, pNote);
		}

	}

	public static void updateRecord(int pActId, int pBookId, int pItemId, int pYear, int pMonth,
			int pDay, int pIncome,
			int pExpense, int pFrequency, String pNote) {

		pNote = getNoteStringWithEscape(pNote);

		RecordTableItem wOldRecord = getRecordByActId(pActId);
		int wOldGroupId = wOldRecord.getGroupId();
		int wOldItemId = wOldRecord.getItemId();
		int wOldBookId = wOldRecord.getBookId();

		String wDate = "'" + pYear + "-" + pMonth + "-" + pDay + "'";
		int wGroupId = 0;

		String wQuery = "";

		// 　ともに繰り返し0ならUpdateのみ
		if (pFrequency == 0 && wOldGroupId == 0) {
			wQuery = "update " + mActTable + " set " + mBookIdCol + " = " + pBookId + ", "
					+ mItemIdCol + " = "
					+ pItemId + ", " + mActDtCol + " = " + wDate + ", " + mActIncomeCol + " = "
					+ pIncome + ", "
					+ mActExpenseCol + " = " + pExpense + ", " + mNoteNameCol + " = '" + pNote
					+ "' ";
			wQuery += " where " + mActIdCol + " = " + pActId;
			// System.out.println(wQuery);
			mDbAccess.executeUpdate(wQuery);

		} else {
			// どちらかが繰り返しありなら既存レコードを削除して新規追加

			// 既存のレコードを削除
			deleteRecord(wOldRecord);

			// 元の日付を取得
			Date wOldDate = wOldRecord.getDate();
			Calendar wOldCal = new GregorianCalendar();
			wOldCal.setTime(wOldDate);

			// 年月, BookId, ItemIdが変更された場合は新規のGroupIdを使用
			if (pYear != wOldCal.get(Calendar.YEAR) || pMonth != wOldCal.get(Calendar.MONTH) + 1
					|| pBookId != wOldBookId || pItemId != wOldItemId) {
				wGroupId = getNewGroupId();
			} else {
				wGroupId = wOldGroupId;
			}

			// 新規のレコードを追加
			Calendar wCalBase = new GregorianCalendar(pYear, pMonth - 1, pDay);
			for (int i = 0; i < pFrequency + 1; i++) {
				Calendar wCal = (Calendar) wCalBase.clone();
				wCal.add(Calendar.MONTH, +i);
				wDate = getDateStrings(wCal.getTime());

				String wQueryBase = "insert into  " + mActTable + " ( " + mBookIdCol + ","
						+ mItemIdCol + ","
						+ mActDtCol + "," + mActIncomeCol + "," + mActExpenseCol + ","
						+ mGroupIdCol + ","
						+ mActFreqCol;

				String wQueryValues = " values(" + pBookId + "," + pItemId + "," + wDate + ","
						+ pIncome + ","
						+ pExpense + "," + wGroupId + "," + (pFrequency - i);

				if (!"".equals(pNote)) {
					wQuery = wQueryBase + "," + mNoteNameCol + ")" + wQueryValues + ",'" + pNote
							+ "')";
				} else {
					wQuery = wQueryBase + ") " + wQueryValues + ")";
				}

				// System.out.println(wQuery);

				mDbAccess.executeUpdate(wQuery);
			}

			// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
			if (!"".equals(pNote)) {
				updateNoteTable(pItemId, pNote);
			}
		}
	}

	public static void insertNewMoveRecord(int pBookFromId, int pBookToId, int pYear, int pMonth,
			int pDay, int pValue,
			int pFrequency, String pNote) {

		pNote = getNoteStringWithEscape(pNote);

		int wGroupId = getNewGroupId();

		String wQueryBase = "insert into  " + mActTable + " ( " + mBookIdCol + "," + mItemIdCol
				+ "," + mActIncomeCol
				+ "," + mActExpenseCol + "," + mGroupIdCol + "," + mActDtCol;
		String wQueryFromValues = " values(" + pBookFromId + "," + getMoveExpenseItemId() + ",'0',"
				+ pValue + ","
				+ wGroupId;
		String wQueryToValues = " values(" + pBookToId + "," + getMoveIncomeItemId() + "," + pValue
				+ ",'0',"
				+ wGroupId;

		String wQueryNote1 = "";
		String wQueryNote2 = "";
		String wQueryFrom = "";
		String wQueryTo = "";

		if (!"".equals(pNote)) {
			wQueryNote1 = "," + mNoteNameCol;
			wQueryNote2 = ",'" + pNote + "'";
		}

		if (pFrequency == 0) {
			String wDate = ",'" + pYear + "-" + pMonth + "-" + pDay + "'";
			wQueryFrom = wQueryBase + wQueryNote1 + ") " + wQueryFromValues + wDate + wQueryNote2
					+ ")";
			// System.out.println(wQueryFrom);
			mDbAccess.executeUpdate(wQueryFrom);
			wQueryTo = wQueryBase + wQueryNote1 + ") " + wQueryToValues + wDate + wQueryNote2 + ")";
			// System.out.println(wQueryTo);
			mDbAccess.executeUpdate(wQueryTo);
		} else { // pFrequency > 0

			Calendar wCalBase = new GregorianCalendar(pYear, pMonth - 1, pDay);
			String wQueryFreq = "," + mActFreqCol;
			for (int i = 0; i < pFrequency + 1; i++) {
				Calendar wCal = (Calendar) wCalBase.clone();
				wCal.add(Calendar.MONTH, +i);
				String wDate = getDateStrings(wCal.getTime());

				wQueryFrom = wQueryBase + wQueryNote1 + wQueryFreq + ") " + wQueryFromValues
						+ wDate + wQueryNote2
						+ "," + (pFrequency - i) + ")";
				// System.out.println(wQueryFrom);
				mDbAccess.executeUpdate(wQueryFrom);
				wQueryTo = wQueryBase + wQueryNote1 + wQueryFreq + ") " + wQueryToValues + wDate
						+ wQueryNote2 + ","
						+ (pFrequency - i) + ")";
				// System.out.println(wQueryTo);
				mDbAccess.executeUpdate(wQueryTo);
			}

		}

		// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
		if (!"".equals(pNote)) {
			updateNoteTable(getMoveIncomeItemId(), pNote);
		}
	}

	public static void updateMoveRecord(int pIncomeActId, int pBookFromId, int pBookToId,
			int pYear, int pMonth,
			int pDay, int pValue, int pFrequency, String pNote) {

		pNote = getNoteStringWithEscape(pNote);

		RecordTableItem wOldIncomeRecord = getRecordByActId(pIncomeActId);
		RecordTableItem wOldExpenseRecord = getMovePairRecord(wOldIncomeRecord);
		int wExpenseActId = wOldExpenseRecord.getId();

		int wOldGroupId = wOldIncomeRecord.getGroupId();
		int wOldFromBookId = wOldExpenseRecord.getBookId();
		int wOldToBookId = wOldIncomeRecord.getBookId();

		String wDate;
		String wQueryFrom;
		String wQueryTo;

		// ともに繰り返し0ならUpdateのみ
		if (isSingleMoveRecordPair(wOldGroupId) && pFrequency == 0) {
			wDate = "'" + pYear + "-" + pMonth + "-" + pDay + "'";
			wQueryFrom = "update " + mActTable + " set " + mBookIdCol + " = " + pBookFromId + ", "
					+ mItemIdCol + " = "
					+ getMoveExpenseItemId() + ", " + mActDtCol + " = " + wDate + ", "
					+ mActIncomeCol + " = "
					+ "'0', " + mActExpenseCol + " = " + pValue + ", " + mNoteNameCol + " = '"
					+ pNote + "' "
					+ " where " + mActIdCol + " = " + wExpenseActId;
			// System.out.println(wQueryFrom);

			mDbAccess.executeUpdate(wQueryFrom);
			wQueryTo = "update " + mActTable + " set " + mBookIdCol + " = " + pBookToId + ", "
					+ mItemIdCol + " = "
					+ getMoveIncomeItemId() + ", " + mActDtCol + " = " + wDate + ", "
					+ mActIncomeCol + " = " + +pValue
					+ ", " + mActExpenseCol + " = " + "'0'" + ", " + mNoteNameCol + " = '" + pNote
					+ "' " + " where "
					+ mActIdCol + " = " + pIncomeActId;
			// System.out.println(wQueryTo);
			mDbAccess.executeUpdate(wQueryTo);

		} else {
			// どちらかが繰り返しありなら既存レコードを削除して新規追加

			// 既存のレコードを削除
			deleteRecord(wOldIncomeRecord);

			// 元の日付を取得
			int wGroupId;
			Date wOldDate = wOldIncomeRecord.getDate();
			Calendar wOldCal = new GregorianCalendar();
			wOldCal.setTime(wOldDate);

			// 年月, BookIdが変更された場合は新規のGroupIdを使用
			if (pYear != wOldCal.get(Calendar.YEAR) || pMonth != wOldCal.get(Calendar.MONTH) + 1
					|| pBookFromId != wOldFromBookId || pBookToId != wOldToBookId) {
				wGroupId = getNewGroupId();
			} else {
				wGroupId = wOldGroupId;
			}

			// 新規のレコードを追加
			Calendar wCalBase = new GregorianCalendar(pYear, pMonth - 1, pDay);
			for (int i = 0; i < pFrequency + 1; i++) {
				Calendar wCal = (Calendar) wCalBase.clone();
				wCal.add(Calendar.MONTH, +i);
				wDate = "'" + wCal.get(Calendar.YEAR) + "-" + (wCal.get(Calendar.MONDAY) + 1) + "-"
						+ wCal.get(Calendar.DAY_OF_MONTH) + "'";

				String wQueryBase = "insert into " + mActTable + " ( " + mBookIdCol + ","
						+ mItemIdCol + ","
						+ mActDtCol + "," + mActIncomeCol + "," + mActExpenseCol + ","
						+ mGroupIdCol + ","
						+ mActFreqCol;

				String wQueryFromValue = " values(" + pBookFromId + "," + getMoveExpenseItemId()
						+ "," + wDate + ","
						+ "'0'" + "," + pValue + "," + wGroupId + "," + (pFrequency - i);

				String wQueryToValue = " values(" + pBookToId + "," + getMoveIncomeItemId() + ","
						+ wDate + ","
						+ pValue + "," + "'0'" + "," + wGroupId + "," + (pFrequency - i);

				String wQueryNote1 = ")";
				String wQueryNote2 = ")";

				if (!"".equals(pNote)) {
					wQueryNote1 = "," + mNoteNameCol + ")";
					wQueryNote2 = ",'" + pNote + "')";
				}

				wQueryFrom = wQueryBase + wQueryNote1 + wQueryFromValue + wQueryNote2;
				// System.out.println(wQueryFrom);
				mDbAccess.executeUpdate(wQueryFrom);

				wQueryTo = wQueryBase + wQueryNote1 + wQueryToValue + wQueryNote2;
				// System.out.println(wQueryTo);
				mDbAccess.executeUpdate(wQueryTo);
			}

			// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
			if (!"".equals(pNote)) {
				updateNoteTable(getMoveIncomeItemId(), pNote);
			}
		}
	}

	public static void deleteRecord(RecordTableItem pRecordTableItem) {
		int wGroupId = pRecordTableItem.getGroupId();

		String wQuery = "delete from " + mActTable + " where ";

		if (wGroupId == 0) {
			// 単一レコードの削除
			wQuery += mActIdCol + " = " + pRecordTableItem.getId();
			// System.out.println(wQuery);
			mDbAccess.executeUpdate(wQuery);
		} else {
			// 複数レコード（同一GroupId,対象日付以降）
			String wDate = getDateStrings(pRecordTableItem.getDate());
			wQuery += mGroupIdCol + " = " + wGroupId + " and " + mActDtCol + " >= " + wDate;
			// System.out.println(wQuery);
			mDbAccess.executeUpdate(wQuery);
		}
	}

	public static int getMoveIncomeItemId() {
		return mIncomeRexp;
	}

	public static int getMoveExpenseItemId() {
		return mExpenseRexp;
	}

	public static RecordTableItem getMovePairRecord(RecordTableItem pRecord) {

		int wActId = pRecord.getId();
		int wGroupId = pRecord.getGroupId();
		String wDate = getDateStrings(pRecord.getDate());

		int wPairActId = 0;

		String wQuery = "select " + mActIdCol + " from " + mActTable + " where " + mGroupIdCol
				+ " = " + wGroupId
				+ " and " + mActDtCol + " = " + wDate + " and " + mActIdCol + " <> " + wActId;

		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			wResultSet.next();
			wPairActId = wResultSet.getInt(mActIdCol);
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		RecordTableItem wPairRecord = getRecordByActId(wPairActId);

		return wPairRecord;

	}

	public static boolean isMoveRecord(int pActId) {
		RecordTableItem wRecord = getRecordByActId(pActId);
		return (isMoveItem(wRecord.getItemId()));
	}

	public static boolean isMoveItem(int pItemId) {
		return pItemId == getMoveIncomeItemId() || pItemId == getMoveExpenseItemId();
	}

	private static boolean isMoveCategory(int pCategoryId) {
		return pCategoryId == getMoveIncomeItemId() || pCategoryId == getMoveExpenseItemId();
	}

	public static SummaryTableItem[] getSummaryTableItems(int pBookId, Date pStartDate,
			Date pEndDate) {
		List<SummaryTableItem> wSummaryTableItemList = new ArrayList<SummaryTableItem>();

		String wStartDateString = getDateStrings(pStartDate);
		String wEndDateString = getDateStrings(pEndDate);

		ResultSet wResultSet;
		String wQuery;

		// for all books
		int wTempBalance = 0;
		int wAppearedBalance = 0;
		int wActualBalance = 0;

		int wTotalAppearedProfit = 0;
		int wSpecialProfit = 0;
		int wTempProfit = 0;
		int wActualProfit = 0;
		int wOperatingProfit = 0;

		// for each book
		int wBookAppearedProfit = 0;
		int wBookAppearedIncome = 0;
		int wBookAppearedExpense = 0;

		// 立替残高（借入残高）
		wTempBalance = getTempBalance(pEndDate);

		// みかけ残高、実質残高
		wAppearedBalance = getBalance(pEndDate, mAllBookId, true);
		wActualBalance = wAppearedBalance - wTempBalance;

		// みかけ収支（Total）
		int[] wTotalAppearedIncomeExpense = getAppearedIncomeExpense(pStartDate, pEndDate,
				mAllBookId);
		wTotalAppearedProfit = wTotalAppearedIncomeExpense[0] - wTotalAppearedIncomeExpense[1];

		// 立替収支
		wTempProfit = getTotalTempProfit(pStartDate, pEndDate);

		// 特別収支
		wSpecialProfit = getTotalSpecialProfit(pStartDate, pEndDate);

		// 実質収支、営業収支
		wActualProfit = wTotalAppearedProfit - wTempProfit;
		wOperatingProfit = wActualProfit - wSpecialProfit;

		wSummaryTableItemList.add(new SummaryTableItem("営業収支", wOperatingProfit));
		wSummaryTableItemList.add(new SummaryTableItem("実質収支", wActualProfit));
		wSummaryTableItemList.add(new SummaryTableItem("実質残高", wActualBalance));
		wSummaryTableItemList.add(new SummaryTableItem("借入残高", wTempBalance));

		// みかけ収支（各Book）
		if (pBookId == mAllBookId) {
			wBookAppearedIncome = wTotalAppearedIncomeExpense[0];
			wBookAppearedExpense = wTotalAppearedIncomeExpense[1];
		} else {
			int[] wBookAppearedIncomeExpense = getAppearedIncomeExpense(pStartDate, pEndDate,
					pBookId);
			wBookAppearedIncome = wBookAppearedIncomeExpense[0];
			wBookAppearedExpense = wBookAppearedIncomeExpense[1];
		}

		wBookAppearedProfit = wBookAppearedIncome - wBookAppearedExpense;

		SummaryTableItem wAppearedProfitItem = new SummaryTableItem("みかけ収支", wBookAppearedProfit);
		wAppearedProfitItem.setAppearedSum(true);
		wSummaryTableItemList.add(wAppearedProfitItem);

		SummaryTableItem wAppearedIncomeItem = new SummaryTableItem("みかけ収入", wBookAppearedIncome);
		wAppearedIncomeItem.setAppearedIncomeExpense(true);
		wAppearedIncomeItem.setIncome(true);
		wSummaryTableItemList.add(wAppearedIncomeItem);

		SummaryTableItem wAppearedExpenseItem = new SummaryTableItem("みかけ支出", wBookAppearedExpense);
		wAppearedExpenseItem.setAppearedIncomeExpense(true);
		wAppearedExpenseItem.setIncome(false);

		// カテゴリ集計
		// CategoryId-SummaryTableItemList
		Map<Integer, List<SummaryTableItem>> wSummaryTableMap = new LinkedHashMap<Integer, List<SummaryTableItem>>();

		wQuery = "select " + mCategoryTable + "." + mCategoryIdCol + ", " + mCategoryTable + "."
				+ mCategoryNameCol
				+ ", sum(" + mActIncomeCol + ") as " + mActIncomeCol + ", sum(" + mActExpenseCol
				+ ") as "
				+ mActExpenseCol + " from " + mActTable + ", " + mItemTable + ", " + mCategoryTable
				+ " where "
				+ mActTable + "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol + " and "
				+ mCategoryTable + "."
				+ mCategoryIdCol + " = " + mItemTable + "." + mCategoryIdCol + " and " + mActDtCol
				+ " between "
				+ wStartDateString + " and " + wEndDateString + " and " + mActTable + "."
				+ mDelFlgCol + " = b'0'"
				+ " and (" + mActTable + "." + mActIncomeCol + " + " + mActTable + "."
				+ mActExpenseCol + ") > 0";
		if (pBookId != mAllBookId) {
			wQuery += " and " + mActTable + "." + mBookIdCol + " = " + pBookId;
		}
		wQuery += " group by " + mCategoryTable + "." + mCategoryIdCol + " order by "
				+ mCategoryTable + "."
				+ mCategoryRexpCol + ", " + mCategoryTable + "." + mSortKeyCol;
		// System.out.println(wQuery);

		wResultSet = mDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryTable + "." + mCategoryIdCol);
				if (pBookId == mAllBookId && (DbUtil.isMoveCategory(wCategoryId))) {

				} else {
					String wCategoryName = wResultSet.getString(mCategoryTable + "."
							+ mCategoryNameCol);
					int wIncome = wResultSet.getInt(mActIncomeCol);
					int wExpense = wResultSet.getInt(mActExpenseCol);
					// System.out.println(wCategoryName + ", " + wIncome + ", "
					// + wExpense);
					List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();
					if (wIncome > 0) {
						wList.add(new SummaryTableItem(wCategoryId, wCategoryName, wIncome, true));
					} else {
						wList
								.add(new SummaryTableItem(wCategoryId, wCategoryName, wExpense,
										false));
					}
					wSummaryTableMap.put(wCategoryId, wList);
				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		// Item集計
		wQuery = "select " + mCategoryTable + "." + mCategoryIdCol + ", " + mActTable + "."
				+ mItemIdCol + ", "
				+ mItemTable + "." + mItemNameCol + ", sum(" + mActIncomeCol + ") as "
				+ mActIncomeCol + ", sum("
				+ mActExpenseCol + ") as " + mActExpenseCol + " from " + mActTable + ", "
				+ mItemTable + ", "
				+ mCategoryTable + " where " + mActTable + "." + mItemIdCol + " = " + mItemTable
				+ "." + mItemIdCol
				+ " and " + mCategoryTable + "." + mCategoryIdCol + " = " + mItemTable + "."
				+ mCategoryIdCol + " and "
				+ mActDtCol + " between " + wStartDateString + " and " + wEndDateString + " and "
				+ mActTable + "."
				+ mDelFlgCol + " = b'0'" + " and (" + mActTable + "." + mActIncomeCol + " + "
				+ mActTable + "."
				+ mActExpenseCol + ") > 0";
		if (pBookId != mAllBookId) {
			wQuery += " and " + mActTable + "." + mBookIdCol + " = " + pBookId;
		}
		wQuery += " group by " + mActTable + "." + mItemIdCol + " order by " + mCategoryTable + "."
				+ mCategoryRexpCol
				+ ", " + mCategoryTable + "." + mSortKeyCol + ", " + mItemTable + "." + mSortKeyCol;

		// System.out.println(wQuery);
		wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryTable + "." + mCategoryIdCol);
				if (pBookId == mAllBookId && (isMoveCategory(wCategoryId))) {

				} else {
					List<SummaryTableItem> wList = wSummaryTableMap.get(wCategoryId);
					int wItemId = wResultSet.getInt(mActTable + "." + mItemIdCol);
					String wItemName = wResultSet.getString(mItemTable + "." + mItemNameCol);
					int wIncome = wResultSet.getInt(mActIncomeCol);
					int wExpense = wResultSet.getInt(mActExpenseCol);
					// System.out.println(wItemName + ", " + wIncome + ", "
					// + wExpense);
					if (wIncome > 0) {
						wList.add(new SummaryTableItem(wItemId, wItemName, wCategoryId, wIncome,
								true));
					} else {
						wList.add(new SummaryTableItem(wItemId, wItemName, wCategoryId, wExpense,
								false));
					}
				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		// リストへ全結果を挿入
		Iterator<Integer> wIt = wSummaryTableMap.keySet().iterator();
		boolean wExpenseRow = false;

		while (wIt.hasNext()) {
			Integer wKey = wIt.next();
			if (!wExpenseRow && !wSummaryTableMap.get(wKey).get(0).isIncome()) {
				wExpenseRow = true;
				wSummaryTableItemList.add(wAppearedExpenseItem);
			}
			wSummaryTableItemList.addAll(wSummaryTableMap.get(wKey));
		}
		if (!wExpenseRow) {
			wSummaryTableItemList.add(wAppearedExpenseItem);
		}

		// for (SummaryTableItem wItem : wSummaryTableItemList) {
		// System.out.println(wItem.getItemName() + ", " + wItem.getValue()
		// + ", " + wItem.isIncome());
		// }

		return (SummaryTableItem[]) wSummaryTableItemList.toArray(new SummaryTableItem[0]);
	}

	// Categoryのみの年度集計を一括取得
	public static List<SummaryTableItem[]> getAnnualSummaryTableItemsCategory(int pBookId,
			Date[][] pDatePeriods) {

		String wTotalStartDateString = getDateStrings(pDatePeriods[0][0]);
		String wTotalEndDateString = getDateStrings(pDatePeriods[pDatePeriods.length - 1][1]);

		// 合計行が必要なら追加
		int wSummationIndex = Util.getSummationIndex(pDatePeriods);
		Date[][] wDatePeriods = Util.getDatePeriodsWithSummaion(pDatePeriods);
		// Summation + Average
		int wDatePeriodCount = pDatePeriods.length + 2
				* (wDatePeriods.length - pDatePeriods.length);

		// 結果を挿入するリスト
		// List<List<SummaryTableItem>> wSummaryTableItemList = new
		// ArrayList<List<SummaryTableItem>>(wDatePeriodCount);
		Map<Integer, List<SummaryTableItem>> wSummaryTableItemListMap = new TreeMap<Integer, List<SummaryTableItem>>();

		// for (int i = 0; i < wDatePeriodCount; i++) {
		// List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();
		// wSummaryTableItemList.add(wList);
		// }

		ResultSet wResultSet;
		String wQuery;
		String wPeriodName = "Period";

		// // for each book
		// int wBookAppearedProfit = 0;
		// int wBookAppearedIncome = 0;
		// int wBookAppearedExpense = 0;

		// カテゴリ集計
		// CategoryId-SummaryTableItemList

		wQuery = "select " + mCategoryTable + "." + mCategoryRexpCol + ", " + mCategoryTable + "."
				+ mSortKeyCol + ", "
				+ mCategoryTable + "." + mCategoryIdCol + ", " + mCategoryTable + "."
				+ mCategoryNameCol;

		for (int i = 0; i < wDatePeriods.length; i++) {
			Date[] wDates = wDatePeriods[i];
			String wStartDateString = getDateStrings(wDates[0]);
			String wEndDateString = getDateStrings(wDates[1]);
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString
					+ " and "
					+ wEndDateString + " then " + mActIncomeCol + " end),0) '" + mActIncomeCol
					+ wPeriodName + i + "'";
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString
					+ " and "
					+ wEndDateString + " then " + mActExpenseCol + " end),0) '" + mActExpenseCol
					+ wPeriodName + i
					+ "'";
		}

		wQuery += " from " + mActTable + ", " + mItemTable + ", " + mCategoryTable;
		wQuery += " where " + mActTable + "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol;
		wQuery += " and " + mItemTable + "." + mCategoryIdCol + " = " + mCategoryTable + "."
				+ mCategoryIdCol;
		wQuery += " and " + mActIncomeCol + " + " + mActExpenseCol + " > 0 ";
		wQuery += " and " + mActTable + "." + mDelFlgCol + " = b'0'";
		wQuery += " and " + mActDtCol + " between " + wTotalStartDateString + " and "
				+ wTotalEndDateString;

		if (pBookId == mAllBookId)
			wQuery += " and " + mItemTable + "." + mMoveFlgCol + " = b'0' ";
		else
			wQuery += " and " + mActTable + "." + mBookIdCol + " = " + pBookId;

		wQuery += " group by " + mCategoryTable + "." + mCategoryIdCol + " with rollup";
		// wQuery += " order by " + mCategoryRexpCol + ", " + mCategoryTable +
		// "." + mSortKeyCol;
		// System.out.println(wQuery);

		wResultSet = mDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryTable + "." + mCategoryIdCol);
				int wRexpDiv = wResultSet.getInt(mCategoryTable + "." + mCategoryRexpCol);
				int wCategorySortKey = wResultSet.getInt(mCategoryTable + "." + mSortKeyCol);
				List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();

				String wCategoryName = wResultSet.getString(mCategoryTable + "."
							+ mCategoryNameCol);
				// boolean isSummationInput = false;

				// 集計行
				if (wCategoryId == 0) {
					List<SummaryTableItem> wListIncome = new ArrayList<SummaryTableItem>();
					List<SummaryTableItem> wListExpense = new ArrayList<SummaryTableItem>();
					for (int i = 0; i < wDatePeriods.length; i++) {
						int wIncome = wResultSet.getInt(mActIncomeCol + wPeriodName + i);
						int wExpense = wResultSet.getInt(mActExpenseCol + wPeriodName + i);

						wList.add(new SummaryTableItem("総収支", wIncome - wExpense, true));
						wListIncome.add(new SummaryTableItem("総収入", wIncome, true));
						wListExpense.add(new SummaryTableItem("総支出", wExpense, true));

						if (i == wSummationIndex) {
							wList.add(new SummaryTableItem("総収支", (wIncome - wExpense) / i,
										true));
							wListIncome.add(new SummaryTableItem("総収入", wIncome / i, true));
							wListExpense.add(new SummaryTableItem("総支出", wExpense / i, true));
						}
					}

					wSummaryTableItemListMap.put(0, wList);
					wSummaryTableItemListMap.put(1, wListIncome);
					int wKey = mExpenseRexp * (int) Math.pow(10, 3);
					// System.out.println(wKey);
					wSummaryTableItemListMap.put(wKey, wListExpense);

				} else {

					for (int i = 0; i < wDatePeriods.length; i++) {
						int wIncome = wResultSet.getInt(mActIncomeCol + wPeriodName + i);
						int wExpense = wResultSet.getInt(mActExpenseCol + wPeriodName + i);

						if (wRexpDiv == mIncomeRexp) {
							wList.add(new SummaryTableItem(wCategoryId, wCategoryName, wIncome,
										true));
							if (i == wSummationIndex) {
								wList.add(new SummaryTableItem(wCategoryId, wCategoryName,
											wIncome / i, true));
							}
						} else {
							wList.add(new SummaryTableItem(wCategoryId, wCategoryName,
										wExpense, false));
							if (i == wSummationIndex) {
								wList.add(new SummaryTableItem(wCategoryId, wCategoryName,
											wExpense / i, false));
							}
						}
					}

					int wKey = wRexpDiv * (int) Math.pow(10, 3) + wCategorySortKey;
					// System.out.println(wKey);
					wSummaryTableItemListMap.put(wKey, wList);

				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		List<SummaryTableItem[]> wReturnList = new ArrayList<SummaryTableItem[]>(wDatePeriodCount);

		for (int i = 0; i < wDatePeriodCount; i++) {
			List<SummaryTableItem> wRowList = new ArrayList<SummaryTableItem>();
			for (Map.Entry<Integer, List<SummaryTableItem>> wEntry : wSummaryTableItemListMap
					.entrySet()) {
				// System.out.println(wEntry.getKey());
				List<SummaryTableItem> wColList = wEntry.getValue();
				wRowList.add(wColList.get(i));
			}
			wReturnList.add((SummaryTableItem[]) wRowList.toArray(new SummaryTableItem[0]));
		}

		// for (List<SummaryTableItem> wList : wSummaryTableItemList) {
		// wReturnList.add((SummaryTableItem[]) wList.toArray(new
		// SummaryTableItem[0]));
		// }
		return wReturnList;
	}

	// Itemのみの年度集計を一括取得
	public static List<SummaryTableItem[]> getAnnualSummaryTableItems(int pBookId,
			Date[][] pDatePeriods) {

		String wTotalStartDateString = getDateStrings(pDatePeriods[0][0]);
		String wTotalEndDateString = getDateStrings(pDatePeriods[pDatePeriods.length - 1][1]);

		// 合計行が必要なら追加
		int wSummationIndex = Util.getSummationIndex(pDatePeriods);
		Date[][] wDatePeriods = Util.getDatePeriodsWithSummaion(pDatePeriods);
		// Summation + Average
		int wDatePeriodCount = pDatePeriods.length + 2
				* (wDatePeriods.length - pDatePeriods.length);

		// 結果を挿入するリスト
		// List<List<SummaryTableItem>> wSummaryTableItemList = new
		// ArrayList<List<SummaryTableItem>>(wDatePeriodCount);
		Map<Integer, List<SummaryTableItem>> wSummaryTableItemListMap = new TreeMap<Integer, List<SummaryTableItem>>();

		// for (int i = 0; i < wDatePeriodCount; i++) {
		// List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();
		// wSummaryTableItemList.add(wList);
		// }

		ResultSet wResultSet;
		String wQuery;
		String wPeriodName = "Period";

		// // for each book
		// int wBookAppearedProfit = 0;
		// int wBookAppearedIncome = 0;
		// int wBookAppearedExpense = 0;

		// カテゴリ集計
		// CategoryId-SummaryTableItemList

		wQuery = "select " + mCategoryTable + "." + mCategoryRexpCol + ", " + mCategoryTable + "."
				+ mCategoryIdCol
				+ ", " + mCategoryTable + "." + mSortKeyCol + ", " + mItemTable + "." + mSortKeyCol
				+ ", " + mItemTable
				+ "." + mItemIdCol + ", " + mItemTable + "." + mItemNameCol;

		for (int i = 0; i < wDatePeriods.length; i++) {
			Date[] wDates = wDatePeriods[i];
			String wStartDateString = getDateStrings(wDates[0]);
			String wEndDateString = getDateStrings(wDates[1]);
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString
					+ " and "
					+ wEndDateString + " then " + mActIncomeCol + " end),0) '" + mActIncomeCol
					+ wPeriodName + i + "'";
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString
					+ " and "
					+ wEndDateString + " then " + mActExpenseCol + " end),0) '" + mActExpenseCol
					+ wPeriodName + i
					+ "'";
		}

		wQuery += " from " + mActTable + ", " + mItemTable + ", " + mCategoryTable;
		wQuery += " where " + mActTable + "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol;
		wQuery += " and " + mItemTable + "." + mCategoryIdCol + " = " + mCategoryTable + "."
				+ mCategoryIdCol;
		wQuery += " and " + mActIncomeCol + " + " + mActExpenseCol + " > 0 ";
		wQuery += " and " + mActTable + "." + mDelFlgCol + " = b'0'";
		wQuery += " and " + mActDtCol + " between " + wTotalStartDateString + " and "
				+ wTotalEndDateString;
		if (pBookId == mAllBookId)
			wQuery += " and " + mItemTable + "." + mMoveFlgCol + " = b'0' ";
		else
			wQuery += " and " + mActTable + "." + mBookIdCol + " = " + pBookId;
		wQuery += " group by " + mItemTable + "." + mItemIdCol + " with rollup";
		// wQuery += " order by " + mCategoryRexpCol + ", " + mCategoryTable +
		// "."
		// + mSortKeyCol + ", " + mItemTable + "." + mSortKeyCol;
		// System.out.println(wQuery);

		wResultSet = mDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryTable + "." + mCategoryIdCol);
				int wItemId = wResultSet.getInt(mItemTable + "." + mItemIdCol);
				int wRexpDiv = wResultSet.getInt(mCategoryTable + "." + mCategoryRexpCol);
				int wCategorySortKey = wResultSet.getInt(mCategoryTable + "." + mSortKeyCol);
				int wItemSortKey = wResultSet.getInt(mItemTable + "." + mSortKeyCol);
				List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();
				String wItemName = wResultSet.getString(mItemTable + "." + mItemNameCol);
				// boolean isSummationInput = false;

				// 集計行
				if (wItemId == 0) {
					List<SummaryTableItem> wListIncome = new ArrayList<SummaryTableItem>();
					List<SummaryTableItem> wListExpense = new ArrayList<SummaryTableItem>();
					for (int i = 0; i < wDatePeriods.length; i++) {
						int wIncome = wResultSet.getInt(mActIncomeCol + wPeriodName + i);
						int wExpense = wResultSet.getInt(mActExpenseCol + wPeriodName + i);

						wList.add(new SummaryTableItem("総収支", wIncome - wExpense, true));
						wListIncome.add(new SummaryTableItem("総収入", wIncome, true));
						wListExpense.add(new SummaryTableItem("総支出", wExpense, true));

						if (i == wSummationIndex) {
							wList.add(new SummaryTableItem("総収支", (wIncome - wExpense) / i,
										true));
							wListIncome.add(new SummaryTableItem("総収入", wIncome / i, true));
							wListExpense.add(new SummaryTableItem("総支出", wExpense / i, true));
						}
					}

					wSummaryTableItemListMap.put(0, wList);
					wSummaryTableItemListMap.put(1, wListIncome);
					int wKey = mExpenseRexp * (int) Math.pow(10, 6);
					// System.out.println(wKey);
					wSummaryTableItemListMap.put(wKey, wListExpense);

				} else {

					for (int i = 0; i < wDatePeriods.length; i++) {
						int wIncome = wResultSet.getInt(mActIncomeCol + wPeriodName + i);
						int wExpense = wResultSet.getInt(mActExpenseCol + wPeriodName + i);

						if (wRexpDiv == mIncomeRexp) {
							wList.add(new SummaryTableItem(wItemId, wItemName, wCategoryId,
										wIncome, true));
							if (i == wSummationIndex) {
								wList.add(new SummaryTableItem(wItemId, wItemName, wCategoryId,
											wIncome / i, true));
							}
						} else {
							wList.add(new SummaryTableItem(wItemId, wItemName, wCategoryId,
										wExpense, false));
							if (i == wSummationIndex) {
								wList
											.add(new SummaryTableItem(wItemId, wItemName,
													wCategoryId, wExpense / i,
													false));
							}
						}
					}

					int wKey = wRexpDiv * (int) Math.pow(10, 6) + wCategorySortKey
								* (int) Math.pow(10, 3) + wItemSortKey;
					// System.out.println(wKey);
					wSummaryTableItemListMap.put(wKey, wList);

				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		// int wRowCount = wSummaryTableItemListMap.get(0.0).size();
		List<SummaryTableItem[]> wReturnList = new ArrayList<SummaryTableItem[]>(wDatePeriodCount);

		// for (int i=0; i < wRowCount; i++) {
		// wReturnList.add(new
		// SummaryTableItem[wSummaryTableItemListMap.keySet().size()]);
		// }

		for (int i = 0; i < wDatePeriodCount; i++) {
			List<SummaryTableItem> wRowList = new ArrayList<SummaryTableItem>();
			for (Map.Entry<Integer, List<SummaryTableItem>> wEntry : wSummaryTableItemListMap
					.entrySet()) {
				// System.out.println(wEntry.getKey());
				List<SummaryTableItem> wColList = wEntry.getValue();
				wRowList.add(wColList.get(i));
			}
			wReturnList.add((SummaryTableItem[]) wRowList.toArray(new SummaryTableItem[0]));
		}

		// for (List<SummaryTableItem> wList : wSummaryTableItemList) {
		// wReturnList.add((SummaryTableItem[]) wList.toArray(new
		// SummaryTableItem[0]));
		// }
		return wReturnList;
	}

	// 特殊収支系の年度集計取得
	public static List<SummaryTableItem[]> getAnnualSummaryTableItemsOriginal(Date[][] pDatePeriods) {

		String wTotalStartDateString = getDateStrings(pDatePeriods[0][0]);

		// 合計行が必要なら追加
		int wSummationIndex = Util.getSummationIndex(pDatePeriods);
		Date[][] wDatePeriods = Util.getDatePeriodsWithSummaion(pDatePeriods);
		// Summation + Average
		int wDatePeriodCount = pDatePeriods.length + 2
				* (wDatePeriods.length - pDatePeriods.length);

		// 結果を挿入するリスト
		List<List<SummaryTableItem>> wSummaryTableItemList = new ArrayList<List<SummaryTableItem>>(
				wDatePeriodCount);
		for (int i = 0; i < wDatePeriodCount; i++) {
			List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();
			wSummaryTableItemList.add(wList);
		}

		ResultSet wResultSet;
		String wQuery;
		String wAppearedBalanceName = "AppearedBalance";
		String wTempBalanceName = "TempBalance";
		String wAppearedIncomeName = "AppearedIncome";
		String wAppearedExpenseName = "AppearedExpense";
		String wSpecialIncomeName = "SpecialIncome";
		String wSpecialExpenseName = "SpecialExpense";
		String wTempIncomeName = "TempIncome";
		String wTempExpenseName = "TempExpense";

		int wAppearedBalance = getInitialBalance(SystemData.getAllBookInt());
		int wActualBalance = 0;
		int wTempBalance = 0;

		// カテゴリ集計
		// CategoryId-SummaryTableItemList

		wQuery = "select COALESCE(sum(case when " + mActDtCol + " < " + wTotalStartDateString
				+ " then "
				+ mActIncomeCol + " - " + mActExpenseCol + " end),0) " + wAppearedBalanceName;
		wQuery += ",  + COALESCE(sum(case when " + mActDtCol + " < " + wTotalStartDateString
				+ " and ("
				+ mCategoryTable + "." + mCategoryTempFlgCol + " = b'1' ) then " + mActIncomeCol
				+ " - "
				+ mActExpenseCol + " end),0) " + wTempBalanceName;
		for (int i = 0; i < wDatePeriods.length; i++) {
			Date[] wDates = wDatePeriods[i];
			String wStartDateString = getDateStrings(wDates[0]);
			String wEndDateString = getDateStrings(wDates[1]);
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString
					+ " and "
					+ wEndDateString + " then " + mActIncomeCol + " end),0) " + wAppearedIncomeName
					+ i;
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString
					+ " and "
					+ wEndDateString + " then " + mActExpenseCol + " end),0) "
					+ wAppearedExpenseName + i;

			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString
					+ " and "
					+ wEndDateString + " and " + mCategoryTable + "." + mCategorySpecialFlgCol
					+ " = b'1' then "
					+ mActIncomeCol + " end),0) " + wSpecialIncomeName + i;
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString
					+ " and "
					+ wEndDateString + " and " + mCategoryTable + "." + mCategorySpecialFlgCol
					+ " = b'1' then "
					+ mActExpenseCol + " end),0) " + wSpecialExpenseName + i;

			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString
					+ " and "
					+ wEndDateString + " and " + mCategoryTable + "." + mCategoryTempFlgCol
					+ " = b'1' then "
					+ mActIncomeCol + " end),0) " + wTempIncomeName + i;
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString
					+ " and "
					+ wEndDateString + " and " + mCategoryTable + "." + mCategoryTempFlgCol
					+ " = b'1' then "
					+ mActExpenseCol + " end),0) " + wTempExpenseName + i;
		}

		wQuery += " from " + mActTable + ", " + mItemTable + ", " + mCategoryTable;
		wQuery += " where " + mActTable + "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol;
		wQuery += " and " + mItemTable + "." + mCategoryIdCol + " = " + mCategoryTable + "."
				+ mCategoryIdCol;
		wQuery += " and " + mActTable + "." + mDelFlgCol + " = b'0'";
		wQuery += " and " + mItemTable + "." + mMoveFlgCol + " = b'0'";
		// System.out.println(wQuery);

		wResultSet = mDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				wAppearedBalance += wResultSet.getInt(wAppearedBalanceName);
				wTempBalance += wResultSet.getInt(wTempBalanceName);
				wActualBalance += wAppearedBalance - wTempBalance;
				boolean isSummationInput = false;

				for (int i = 0; i < wDatePeriods.length; i++) {
					int wAppearedIncome = wResultSet.getInt(wAppearedIncomeName + i);
					int wAppearedExpense = wResultSet.getInt(wAppearedExpenseName + i);
					int wSpecialIncome = wResultSet.getInt(wSpecialIncomeName + i);
					int wSpecialExpense = wResultSet.getInt(wSpecialExpenseName + i);
					int wTempIncome = wResultSet.getInt(wTempIncomeName + i);
					int wTempExpense = wResultSet.getInt(wTempExpenseName + i);
					int wListIndex;
					if (isSummationInput) {
						wListIndex = i + 1;
					} else {
						wListIndex = i;
					}
					int wAppearedProfit = wAppearedIncome - wAppearedExpense;
					int wTempProfit = wTempIncome - wTempExpense;
					int wSpecialProfit = wSpecialIncome - wSpecialExpense;
					int wActualProfit = wAppearedProfit - wTempProfit;
					int wOperationProfit = wAppearedProfit - wTempProfit - wSpecialProfit;

					if (i == wSummationIndex) {
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("繰越残高", SystemData.getUndefinedInt()));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("繰越残高", SystemData.getUndefinedInt()));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("みかけ収支", wAppearedProfit));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("みかけ収支", wAppearedProfit / i));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("営業収支", wOperationProfit));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("営業収支", wOperationProfit / i));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("実質収支", wActualProfit));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("実質収支", wActualProfit / i));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("実質残高", SystemData.getUndefinedInt()));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("実質残高", SystemData.getUndefinedInt()));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("みかけ残高", SystemData.getUndefinedInt()));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("みかけ残高", SystemData.getUndefinedInt()));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("立替累計", SystemData.getUndefinedInt()));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("立替累計", SystemData.getUndefinedInt()));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("営業収入",
										(wAppearedIncome - wSpecialIncome - wTempIncome)));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("営業収入",
										(wAppearedIncome - wSpecialIncome - wTempIncome) / i));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("営業支出",
										(wAppearedExpense - wSpecialExpense - wTempExpense)));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("営業支出",
										(wAppearedExpense - wSpecialExpense - wTempExpense) / i));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("実質収入", wAppearedIncome - wTempIncome));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("実質収入", (wAppearedIncome - wTempIncome) / i));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("実質支出", wAppearedExpense - wTempExpense));
						wSummaryTableItemList.get(wListIndex + 1)
								.add(
										new SummaryTableItem("実質支出",
												(wAppearedExpense - wTempExpense) / i));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("みかけ収入", wAppearedIncome));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("みかけ収入", wAppearedIncome / i));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("みかけ支出", wAppearedExpense));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("みかけ支出", wAppearedExpense / i));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("特別収入", wSpecialIncome));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("特別収入", wSpecialIncome / i));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("特別支出", wSpecialExpense));
						wSummaryTableItemList.get(wListIndex + 1)
								.add(new SummaryTableItem("特別支出", wSpecialExpense / i));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("立替収入", wTempIncome));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("立替収入", wTempIncome / i));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("立替支出", wTempExpense));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("立替支出", wTempExpense / i));

						isSummationInput = true;

					} else { // SummationIndex以外
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("繰越残高", wAppearedBalance));

						wAppearedBalance += wAppearedProfit;
						wActualBalance += wActualProfit;
						wTempBalance += wTempProfit;

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("みかけ収支", wAppearedProfit));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("営業収支", wOperationProfit));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("実質収支", wActualProfit));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("実質残高", wActualBalance));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("みかけ残高", wAppearedBalance));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("立替累計", wTempBalance));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("営業収入", wAppearedIncome - wSpecialIncome
										- wTempIncome));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("営業支出", wAppearedExpense - wSpecialExpense
										- wTempExpense));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("実質収入", wAppearedIncome - wTempIncome));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("実質支出", wAppearedExpense - wTempExpense));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("みかけ収入", wAppearedIncome));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("みかけ支出", wAppearedExpense));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("特別収入", wSpecialIncome));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("特別支出", wSpecialExpense));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("立替収入", wTempIncome));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("立替支出", wTempExpense));

					}
				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		List<SummaryTableItem[]> wReturnList = new ArrayList<SummaryTableItem[]>(
				wSummaryTableItemList.size());
		for (List<SummaryTableItem> wList : wSummaryTableItemList) {
			wReturnList.add((SummaryTableItem[]) wList.toArray(new SummaryTableItem[0]));
		}
		return wReturnList;
	}

	public static ConfigItem getRootConfigItem() {
		ConfigItem wRootItem = new ConfigItem("");
		wRootItem.addItem(getEachConfigItem(true));
		wRootItem.addItem(getEachConfigItem(false));

		return wRootItem;
	}

	public static ConfigItem getEachConfigItem(boolean pIsIncome) {

		int wRexp = mIncomeRexp;
		String wRootLabel;
		if (!pIsIncome) {
			wRexp = mExpenseRexp;
			wRootLabel = "支出項目";
		} else {
			wRootLabel = "収入項目";
		}

		Map<Integer, ConfigItem> wResultMap = new LinkedHashMap<Integer, ConfigItem>();

		// Category一覧の取得
		String wQuery = "select " + mCategoryIdCol + ", " + mCategoryNameCol + " from "
				+ mCategoryTable;
		wQuery += " where " + mDelFlgCol + " = b'0' and " + mCategoryRexpCol + " = " + wRexp;
		wQuery += " and " + mSortKeyCol + " > 0";
		wQuery += " order by " + mSortKeyCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryIdCol);
				String wCategoryName = wResultSet.getString(mCategoryNameCol);
				wResultMap.put(wCategoryId, new ConfigItem(wCategoryId, wCategoryName, true));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		// Item一覧の取得

		wQuery = "select " + mCategoryIdCol + ", " + mItemIdCol + ", " + mItemIdCol + ", "
				+ mItemNameCol;
		wQuery += " from " + mItemTable;
		wQuery += " where " + mDelFlgCol + " = b'0' and " + mMoveFlgCol + " = b'0'";
		wQuery += " order by " + mSortKeyCol;

		// System.out.println(wQuery);
		wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryIdCol);
				int wItemId = wResultSet.getInt(mItemIdCol);
				String wItemName = wResultSet.getString(mItemNameCol);
				if (wResultMap.containsKey(wCategoryId)) {
					wResultMap.get(wCategoryId).addItem(new ConfigItem(wItemId, wItemName, false));
				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		// 結果をルートアイテムに格納
		ConfigItem wRootItem = new ConfigItem(wRootLabel);
		for (Map.Entry<Integer, ConfigItem> wEntrySet : wResultMap.entrySet()) {
			wRootItem.addItem(wEntrySet.getValue());
		}

		// return (ConfigItem[])wResultMap.values().toArray(new ConfigItem[0]);
		return wRootItem;
	}

	public static void updateSortKeys(ConfigItem pConfigItem) {

		int wSortKeyCategory = mInitialSortKeyCategory;
		int wSortKeyItem = mInitialSortKeyItem;
		String wQuery;

		List<ConfigItem> wConfigItemList = new ArrayList<ConfigItem>();
		wConfigItemList.add(pConfigItem);

		while (wConfigItemList.size() > 0) {

			ConfigItem wCurrentItem = wConfigItemList.get(0);
			wConfigItemList.remove(0);

			if (!wCurrentItem.isSpecial()) {
				// 自身のアップデート

				if (wCurrentItem.isCategory()) {
					wQuery = "update " + mCategoryTable + " set " + mSortKeyCol + " = "
							+ wSortKeyCategory + " where "
							+ mCategoryIdCol + " = " + wCurrentItem.getId();
					wSortKeyCategory++;
				} else {
					wQuery = "update " + mItemTable + " set " + mSortKeyCol + " = " + wSortKeyItem
							+ " where "
							+ mItemIdCol + " = " + wCurrentItem.getId();
					wSortKeyItem++;
				}
				mDbAccess.executeUpdate(wQuery);
				// System.out.println(wQuery);
			}
			if (wCurrentItem.hasItem()) {
				// 子リストの追加
				for (ConfigItem wChildItem : wCurrentItem.getChildren()) {
					wConfigItemList.add(wChildItem);
				}
			}

		}
	}

	public static void insertNewCategory(boolean isIncome, String pCategoryName) {
		String wQuery = "insert into " + mCategoryTable + " (" + mCategoryRexpCol + ", "
				+ mCategoryNameCol + ", "
				+ mSortKeyCol + ") values (";
		wQuery += (isIncome) ? mIncomeRexp : mExpenseRexp;
		wQuery += ", '" + pCategoryName + "', " + 9999 + ")";
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQuery);
	}

	public static void insertNewItem(int pCategoryId, String pItemName) {
		String wQuery = "insert into " + mItemTable + " (" + mCategoryIdCol + ", " + mItemNameCol
				+ ", " + mSortKeyCol
				+ ") values (";
		wQuery += pCategoryId + ", '" + pItemName + "', " + 9999 + ")";
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQuery);
	}

	public static void updateCategory(int pCategoryId, String pCategoryName) {
		String wQuery = "update " + mCategoryTable + " set " + mCategoryNameCol + " = '"
				+ pCategoryName + "' where "
				+ mCategoryIdCol + " = " + pCategoryId;
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQuery);
	}

	public static void updateItem(int pCategoryId, int pItemId, String pItemName) {
		String wQuery = "update " + mItemTable + " set " + mCategoryIdCol + " = " + pCategoryId
				+ ", " + mItemNameCol
				+ " = '" + pItemName + "' " + " where " + mItemIdCol + " = " + pItemId;
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQuery);
	}

	public static void deleteCategoryItem(ConfigItem pConfigItem) {
		String wTableName = (pConfigItem.isCategory()) ? mCategoryTable : mItemTable;
		String wIdName = (pConfigItem.isCategory()) ? mCategoryIdCol : mItemIdCol;

		String wQuery = "update " + wTableName + " set " + mDelFlgCol + " = b'1' where " + wIdName
				+ " = "
				+ pConfigItem.getId();
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQuery);
	}

	public static List<Integer> getRelatedBookIdList(ConfigItem pConfigItem) {
		List<Integer> wList = new ArrayList<Integer>();
		String wQuery = "select " + mBookIdCol + " from " + mBookItemTable + " where " + mItemIdCol
				+ " = "
				+ pConfigItem.getId();
		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				wList.add(wResultSet.getInt(mBookIdCol));
			}
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wList;
	}

	public static List<Integer> getRelatedItemIdList(int pBookId) {
		List<Integer> wList = new ArrayList<Integer>();
		String wQuery = "select " + mItemIdCol + " from " + mBookItemTable + " where " + mBookIdCol
				+ " = " + pBookId;
		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				wList.add(wResultSet.getInt(mItemIdCol));
			}
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wList;
	}

	public static void updateItemRelation(int pItemId, int pBookId, boolean isSelected) {
		String wQuery;

		if (!isSelected) {
			// 削除
			wQuery = "delete from " + mBookItemTable + " where " + mItemIdCol + " = " + pItemId
					+ " and " + mBookIdCol
					+ " = " + pBookId;
		} else {
			// 追加
			wQuery = "insert into " + mBookItemTable + " (" + mItemIdCol + ", " + mBookIdCol
					+ ") values (" + pItemId
					+ ", " + pBookId + ")";
		}
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQuery);
	}

	public static List<Integer> getSpecialCategoryIdList() {
		List<Integer> wList = new ArrayList<Integer>();
		String wQuery = "select " + mCategoryIdCol + " from " + mCategoryTable + " where "
				+ mCategorySpecialFlgCol
				+ " = b'1'" + " and " + mDelFlgCol + " = b'0'";

		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				wList.add(wResultSet.getInt(mCategoryIdCol));
			}
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wList;

	}

	public static List<Integer> getTempCategoryIdList() {
		List<Integer> wList = new ArrayList<Integer>();

		String wQuery = "select " + mCategoryIdCol + " from " + mCategoryTable + " where "
				+ mCategoryTempFlgCol
				+ " = b'1'" + " and " + mDelFlgCol + " = b'0'";

		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				wList.add(wResultSet.getInt(mCategoryIdCol));
			}
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wList;

	}

	public static void updateSpecialCategory(int pCategoryId, boolean isSelected) {
		String wQuery;
		wQuery = "update " + mCategoryTable + " set " + mCategorySpecialFlgCol + " = b'"
				+ (isSelected ? 1 : 0)
				+ "' where " + mCategoryIdCol + " = " + pCategoryId;
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQuery);
	}

	public static void updateTempCategory(int pCategoryId, boolean isSelected) {
		String wQuery;
		wQuery = "update " + mCategoryTable + " set " + mCategoryTempFlgCol + " = b'"
				+ (isSelected ? 1 : 0)
				+ "' where " + mCategoryIdCol + " = " + pCategoryId;
		mDbAccess.executeUpdate(wQuery);
	}

	public static String getBookNameById(int pBookId) {
		ResultSet wResultSet = mDbAccess.executeQuery("select " + mBookNameCol + " from "
				+ mBookTable + " where "
				+ mBookIdCol + " = " + pBookId);
		try {
			if (wResultSet.next()) {
				String result = wResultSet.getString(mBookNameCol);
				wResultSet.close();
				return result;
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		return "";
	}

	public static List<Book> getBookList() {
		List<Book> wBookList = new ArrayList<Book>();
		String wQuery = "select " + mBookIdCol + ", " + mBookNameCol + ", " + mBookBalanceCol;
		wQuery += " from " + mBookTable + " where " + mDelFlgCol + " = b'0' " + " order by "
				+ mSortKeyCol;
		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				Book wBook = new Book(wResultSet.getInt(mBookIdCol), wResultSet
						.getString(mBookNameCol));
				if (wResultSet.getInt(mBookBalanceCol) > 0) {
					wBook.setBalance(wResultSet.getInt(mBookBalanceCol));
				}
				wBookList.add(wBook);
			}
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		return wBookList;
	}

	public static void addNewBook(String pBookName) {
		String wQuery = "insert into " + mBookTable + " (" + mBookNameCol + ") values('"
				+ pBookName + "')";
		mDbAccess.executeUpdate(wQuery);
	}

	public static void updateBook(int pBookId, String pBookName, int pBalance) {
		String wQuery = "update " + mBookTable + " set " + mBookNameCol + " = '" + pBookName
				+ "', " + mBookBalanceCol
				+ " = " + pBalance;
		wQuery += " where " + mBookIdCol + " = " + pBookId;
		mDbAccess.executeUpdate(wQuery);
	}

	public static void removeBook(int pBookId) {
		String wQuery = "update " + mBookTable + " set " + mDelFlgCol + " = b'1' ";
		wQuery += " where " + mBookIdCol + " = " + pBookId;
		mDbAccess.executeUpdate(wQuery);
	}

	public static void updateBookSortKeys(List<Book> pBookList) {
		int wSortKey = 1;
		String wQuery;
		for (Book wBook : pBookList) {
			wQuery = "update " + mBookTable + " set " + mSortKeyCol + " = " + wSortKey++
					+ " where " + mBookIdCol
					+ " = " + wBook.getId();
			mDbAccess.executeUpdate(wQuery);
		}
	}

	public static void updateBalance(Book pBook) {
		String wQuery = "update " + mBookTable + " set " + mBookBalanceCol + " = "
				+ pBook.getBalance();
		wQuery += " where " + mBookIdCol + " = " + pBook.getId();
		mDbAccess.executeUpdate(wQuery);
	}

	public static RecordTableItem[][] getSearchedRecordTableItemList(String pQueryString) {
		RecordTableItem[][] result = new RecordTableItem[2][];
		Date wDate = new Date();
		String wQueryBase = "select " + mActIdCol + ", " + mBookIdCol + ", " + mActDtCol + ", "
				+ mActTable + "."
				+ mItemIdCol + ", " + mGroupIdCol + ", " + mActIncomeCol + ", " + mActExpenseCol
				+ ", " + mActFreqCol
				+ ", " + mNoteNameCol + " from " + mActTable + ", " + mItemTable + ", "
				+ mCategoryTable + " where "
				+ mItemTable + "." + mItemIdCol + " = " + mActTable + "." + mItemIdCol + " and "
				+ mItemTable + "."
				+ mCategoryIdCol + " = " + mCategoryTable + "." + mCategoryIdCol + " and "
				+ mActTable + "."
				+ mDelFlgCol + " = b'0' " + " and " + mNoteNameCol + " like '%" + pQueryString
				+ "%'";
		String wQueryPeriodBefore = " and " + mActDtCol + " <= " + getDateStrings(wDate);
		String wQueryPeriodAfter = " and " + mActDtCol + " > " + getDateStrings(wDate);
		String wQueryOrder = " order by " + mActDtCol + ", " + mCategoryTable + "."
				+ mCategoryRexpCol + ", "
				+ mCategoryTable + "." + mSortKeyCol + ", " + mItemTable + "." + mSortKeyCol;

		result[0] = getRecordTableItemFromResultSet(mDbAccess.executeQuery(wQueryBase
				+ wQueryPeriodBefore + wQueryOrder));
		result[1] = getRecordTableItemFromResultSet(mDbAccess.executeQuery(wQueryBase
				+ wQueryPeriodAfter + wQueryOrder));

		return result;
	}

	private static RecordTableItem[] getRecordTableItemFromResultSet(ResultSet pResultSet) {
		List<RecordTableItem> wList = new ArrayList<RecordTableItem>();
		try {
			while (pResultSet.next()) {
				wList.add(
						new RecordTableItem.Builder(pResultSet.getInt(mBookIdCol),
								pResultSet.getInt(mItemIdCol), pResultSet.getDate(mActDtCol))
								.actId(pResultSet.getInt(mActIdCol))
								.expense(pResultSet.getInt(mActExpenseCol))
								.frequency(pResultSet.getInt(mActFreqCol))
								.groupId(pResultSet.getInt(mGroupIdCol))
								.income(pResultSet.getInt(mActIncomeCol))
								.note(pResultSet.getString(mNoteNameCol))
								.build());
			}
			pResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
		}
		return (RecordTableItem[]) wList.toArray(new RecordTableItem[0]);
	}

	// 立替残高（借入残高）
	private static int getTempBalance(Date pEndDate) {

		int wTempBalance = 0;
		String wResultColName = "ResultCol";
		String wEndDateString = getDateStrings(pEndDate);

		String wQuery = "select sum(" + mActIncomeCol + " - " + mActExpenseCol + ") as "
				+ wResultColName + " from "
				+ mActTable + ", " + mItemTable + ", " + mCategoryTable + " where " + mActTable
				+ "." + mItemIdCol
				+ " = " + mItemTable + "." + mItemIdCol + " and " + mItemTable + "."
				+ mCategoryIdCol + " = "
				+ mCategoryTable + "." + mCategoryIdCol + " and " + mActTable + "." + mDelFlgCol
				+ " = b'0' and "
				+ mActDtCol + " <= " + wEndDateString + " and " + mCategoryTable + "."
				+ mCategoryTempFlgCol
				+ " = b'1'";

		// System.out.println(wQuery);
		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			if (wResultSet.next()) {
				wTempBalance = wResultSet.getInt(wResultColName);
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wTempBalance;
	}

	private static int getTotalTempProfit(Date pStartDate, Date pEndDate) {
		// 立替収支
		int wTotalTempProfit = 0;

		String wResultColName = "ResultCol";
		String wStartDateString = getDateStrings(pStartDate);
		String wEndDateString = getDateStrings(pEndDate);

		String wQuery = "select sum(" + mActIncomeCol + " - " + mActExpenseCol + ") as "
				+ wResultColName + " from "
				+ mActTable + ", " + mItemTable + ", " + mCategoryTable + " where " + mActTable
				+ "." + mItemIdCol
				+ " = " + mItemTable + "." + mItemIdCol + " and " + mItemTable + "."
				+ mCategoryIdCol + " = "
				+ mCategoryTable + "." + mCategoryIdCol + " and " + mActTable + "." + mDelFlgCol
				+ " = b'0' and "
				+ mActDtCol + " between " + wStartDateString + " and " + wEndDateString + " and "
				+ mCategoryTable
				+ "." + mCategoryTempFlgCol + " = b'1'";

		// System.out.println(wQuery);
		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			if (wResultSet.next()) {
				wTotalTempProfit = wResultSet.getInt(wResultColName);
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wTotalTempProfit;
	}

	private static int getTotalSpecialProfit(Date pStartDate, Date pEndDate) {
		// 特別収支
		int wSpecialProfit = 0;

		String wResultColName = "ResultCol";
		String wStartDateString = getDateStrings(pStartDate);
		String wEndDateString = getDateStrings(pEndDate);

		String wQuery = "select sum(" + mActIncomeCol + " - " + mActExpenseCol + ") as "
				+ wResultColName + " from "
				+ mActTable + ", " + mItemTable + ", " + mCategoryTable + " where " + mActTable
				+ "." + mItemIdCol
				+ " = " + mItemTable + "." + mItemIdCol + " and " + mItemTable + "."
				+ mCategoryIdCol + " = "
				+ mCategoryTable + "." + mCategoryIdCol + " and " + mActTable + "." + mDelFlgCol
				+ " = b'0' and "
				+ mActDtCol + " between " + wStartDateString + " and " + wEndDateString + " and "
				+ mCategoryTable
				+ "." + mCategorySpecialFlgCol + " = b'1'";

		// System.out.println(wQuery);
		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			if (wResultSet.next()) {
				wSpecialProfit = wResultSet.getInt(wResultColName);
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wSpecialProfit;
	}

	private static int[] getAppearedIncomeExpense(Date pStartDate, Date pEndDate, int pBookId) {
		// みかけ収入・支出（各Book）
		int[] wAppearedIncomeExpense = new int[2];

		String wStartDateString = getDateStrings(pStartDate);
		String wEndDateString = getDateStrings(pEndDate);

		String wQuery = "select sum(" + mActIncomeCol + ") as " + mActIncomeCol + ", sum("
				+ mActExpenseCol + ") as "
				+ mActExpenseCol + " from " + mActTable + ", " + mItemTable + ", " + mCategoryTable
				+ " where "
				+ mActTable + "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol + " and "
				+ mItemTable + "."
				+ mCategoryIdCol + " = " + mCategoryTable + "." + mCategoryIdCol + " and "
				+ mActTable + "."
				+ mDelFlgCol + " = b'0' and " + mActDtCol + " between " + wStartDateString
				+ " and " + wEndDateString;

		if (pBookId == mAllBookId) {
			// Moveを除く
			wQuery += " and " + mItemTable + "." + mMoveFlgCol + " = b'0'";

		} else {
			// BookIdの条件を追加
			wQuery += " and " + mBookIdCol + " = " + pBookId;
		}

		// System.out.println(wQuery);
		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			if (wResultSet.next()) {
				wAppearedIncomeExpense[0] = wResultSet.getInt(mActIncomeCol);
				wAppearedIncomeExpense[1] = wResultSet.getInt(mActExpenseCol);
				// System.out.println(wTempBalanceName + " = " +
				// wTempBalance);
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wAppearedIncomeExpense;
	}

	private static int getBalance(Date pEndDate, int pBookId, boolean pIncludeEndDate) {

		int wBalance = getInitialBalance(pBookId);

		String wEnd = getDateStrings(pEndDate);
		String wBookWhere = getBookWhere(pBookId);
		String wResultCol = "Value";

		String wQuery = "select SUM(" + mActIncomeCol + " - " + mActExpenseCol + ") as "
				+ wResultCol + " from "
				+ mActTable + " where " + mDelFlgCol + " = b'0' " + " and " + wBookWhere + " and "
				+ mActDtCol;

		if (pIncludeEndDate) {
			wQuery += " <= " + wEnd;
		} else {
			wQuery += " < " + wEnd;
		}

		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			wResultSet.next();
			wBalance += wResultSet.getInt(wResultCol);
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wBalance;
	}

	private static int getInitialBalance(int pBookId) {
		int wBalance = 0;
		String wBookWhere = getBookWhere(pBookId);
		String wResultCol = "VALUE";

		String wQuery = "";

		if (pBookId == mAllBookId) {
			wQuery = "select SUM(" + mBookBalanceCol + ") as " + wResultCol + " from " + mBookTable
					+ " where "
					+ mDelFlgCol + " = b'0' and " + wBookWhere;

		} else {
			wQuery = "select " + mBookBalanceCol + " as " + wResultCol + " from " + mBookTable
					+ " where " + wBookWhere;
		}

		// System.out.println(wQuery);
		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			wResultSet.next();
			wBalance += wResultSet.getInt(wResultCol);
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wBalance;
	}

	private static String getBookWhere(int pBookId) {
		String wWhereBook = "";
		if (pBookId == mAllBookId) {
			wWhereBook = mBookIdCol + " > " + mAllBookId;
		} else {
			wWhereBook = mBookIdCol + " = " + pBookId;
		}
		return wWhereBook;
	}

	private static int getNewGroupId() {
		int wRet = 0;
		String wCol = "MaximumGroupId";

		ResultSet wResultSet = mDbAccess.executeQuery("select max(" + mGroupIdCol + ") as " + wCol
				+ " from "
				+ mActTable);

		try {
			wResultSet.next();
			wRet = wResultSet.getInt(wCol);
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wRet + 1;

	}

	private static void updateNoteTable(int pItemId, String pNote) {
		String wQuery = "delete from " + mNoteTable + " where " + mItemIdCol + " = " + pItemId
				+ " and " + mNoteNameCol
				+ " = '" + pNote + "'";
		mDbAccess.executeUpdate(wQuery);

		wQuery = "insert into  " + mNoteTable + " (" + mNoteNameCol + "," + mItemIdCol
				+ ") values('" + pNote + "',"
				+ pItemId + ")";
		mDbAccess.executeUpdate(wQuery);
	}

	/**
	 * 
	 * @param pDate
	 * @return 'yyyy-m-d'
	 */
	private static String getDateStrings(Date pDate) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return "'" + df.format(pDate) + "'";
	}

	private static boolean isSingleMoveRecordPair(int pGroupId) {
		String wResultCol = "COUNT";
		String wQuery = "select count(" + mActIdCol + ") as " + wResultCol + " from " + mActTable
				+ " where "
				+ mGroupIdCol + " = " + pGroupId;
		// System.out.println(wQuery);
		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		int wCount = 0;

		try {
			wResultSet.next();
			wCount = wResultSet.getInt(wResultCol);
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		if (wCount == 2) {
			return true;
		} else {
			return false;
		}
	}

	private static void resultSetHandlingError(SQLException e) {
		e.printStackTrace();
		StringBuffer wStack = new StringBuffer();
		for (int i = 0; i < e.getStackTrace().length; i++) {
			if (i == 10) {
				wStack.append("...");
				break;
			}
			wStack.append(e.getStackTrace()[i] + "\n");
		}
		MessageDialog.openWarning(Display.getCurrent().getShells()[0],
				"SQL ResultSet Handling Error", e.toString()
						+ "\n\n" + wStack);
		// System.err.println("ResultSet Handling Error: " + e.toString());
	}

	private static String getNoteStringWithEscape(String pNote) {
		for (String wString : mSpecialChars) {
			pNote = pNote.replace(wString, mEscapeChar + wString);
		}
		return pNote;
	}

	// public static void main(String[] args) {
	// for (RecordTableItem[] items : getSearchedRecordTableItemList("牛乳"))
	// System.out.println(items.length);
	// }
}