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

import model.ConfigItem;
import model.RecordTableItem;
import model.SummaryTableItem;
import model.SystemData;

public class DbUtil {

	// Systemテーブル関連
	private final static String mSystemTable = "system";
	private final static String mSystemValueCol = "NUM_VALUE";
	private final static String mSystemIDCol = "SID";
	private final static String mCutOff = "CUTOFF_DT";
	private final static String mFiscalMonth = "FISCAL_MH";

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

	private final static int mSpecialIncomeCategoryId = 23;
	private final static int mSpecialExpenseCategoryId = 44;
	private final static int mTempIncomeCategoryId = 60;
	private final static int mTempExpenseCategoryId = 61;

	// エスケープ文字
	private final static String mEscapeChar = "\\";
	private final static String[] mSpecialChars = { "\\", "\'", "\"" };

	private final static int mInitialSortKeyCategory = 1; // 0は現金移動用
	private final static int mInitialSortKeyItem = 2; // 1は現金移動用

	private DbUtil() {

	}

	public static int getCutOff() {
		int wCutOff = -1;

		DbAccess wDbAccess = new DbAccess();
		ResultSet wResultSet = wDbAccess.executeQuery("select " + mSystemValueCol + " from " + mSystemTable + " where "
				+ mSystemIDCol + " = '" + mCutOff + "'");

		try {
			wResultSet.next();
			wCutOff = wResultSet.getInt(mSystemValueCol);
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
			wDbAccess.closeConnection();
		}

		return wCutOff;

	}

	public static int getFisCalMonth() {
		int wFiscalMonth = -1;

		DbAccess wDbAccess = new DbAccess();
		ResultSet wResultSet = wDbAccess.executeQuery("select " + mSystemValueCol + " from " + mSystemTable + " where "
				+ mSystemIDCol + " = '" + mFiscalMonth + "'");

		try {
			wResultSet.next();
			wFiscalMonth = wResultSet.getInt(mSystemValueCol);
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
			wDbAccess.closeConnection();
		}

		return wFiscalMonth;
	}

	public static int getCategoryIdByItemId(int pItemId) {
		DbAccess wDbAccess = new DbAccess();
		ResultSet wResultSet = wDbAccess.executeQuery("select " + mCategoryIdCol + " from " + mItemTable + " where "
				+ mItemIdCol + " = " + pItemId);

		int wCategoryId = -1;

		try {
			wResultSet.next();
			wCategoryId = wResultSet.getInt(mCategoryIdCol);
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
			wDbAccess.closeConnection();
		}

		return wCategoryId;
	}

	public static String getItemNameById(int pItemId) {
		DbAccess wDbAccess = new DbAccess();
		ResultSet wResultSet = wDbAccess.executeQuery("select " + mItemNameCol + " from " + mItemTable + " where "
				+ mItemIdCol + " = " + pItemId);

		String wItemName = "";

		try {
			wResultSet.next();
			wItemName = wResultSet.getString(mItemNameCol);
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
			wDbAccess.closeConnection();
		}

		return wItemName;

	}

	public static RecordTableItem[][] getRecordTableItems(Date pStartDate, Date pEndDate, int pBookId) {
		List<RecordTableItem> wRecordTableItemListUp = new ArrayList<RecordTableItem>();
		List<RecordTableItem> wRecordTableItemListBottom = new ArrayList<RecordTableItem>();

		String wStart = getDateStrings(pStartDate);
		String wEnd = getDateStrings(pEndDate);

		String wBookWhere = getBookWhere(pBookId);

		String wMoveFlgWhere = "";
		if (pBookId == mAllBookId) {
			wMoveFlgWhere = " and " + mItemTable + "." + mMoveFlgCol + " = b'0'";
		}

		DbAccess wDbAccess = new DbAccess();

		double wBalance = getBalance(wDbAccess, pStartDate, pBookId, false);
		RecordTableItem wBalanceRecord = new RecordTableItem(pStartDate, wBalance);
		wRecordTableItemListUp.add(wBalanceRecord);

		String wQuery = "select " + mActIdCol + ", " + mBookIdCol + ", " + mActDtCol + ", " + mActTable + "."
				+ mItemIdCol + ", " + mItemTable + "." + mItemNameCol + ", " + mItemTable + "." + mCategoryIdCol + ", "
				+ mGroupIdCol + ", " + mActIncomeCol + ", " + mActExpenseCol + ", " + mActFreqCol + ", " + mNoteNameCol
				+ " from " + mActTable + ", " + mItemTable + ", " + mCategoryTable + " where " + mItemTable + "."
				+ mItemIdCol + " = " + mActTable + "." + mItemIdCol + " and " + mItemTable + "." + mCategoryIdCol
				+ " = " + mCategoryTable + "." + mCategoryIdCol + " and " + mActDtCol + " between " + wStart + " and "
				+ wEnd + " and " + mActTable + "." + mDelFlgCol + " = b'0' " + wMoveFlgWhere + " and " + wBookWhere
				+ " order by " + mActDtCol + ", " + mCategoryTable + "." + mCategoryRexpCol + ", " + mCategoryTable
				+ "." + mSortKeyCol + ", " + mItemTable + "." + mSortKeyCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = wDbAccess.executeQuery(wQuery);

		try {
			Date wDateNow = new Date();
			while (wResultSet.next()) {

				int wId = wResultSet.getInt(mActIdCol);
				int wBookId = wResultSet.getInt(mBookIdCol);
				Date wDate = wResultSet.getDate(mActDtCol);
				int wItemId = wResultSet.getInt(mItemIdCol);
				String wItemName = wResultSet.getString(mItemTable + "." + mItemNameCol);
				int wCategoryId = wResultSet.getInt(mItemTable + "." + mCategoryIdCol);
				int wGroupId = wResultSet.getInt(mGroupIdCol);
				double wIncome = wResultSet.getDouble(mActIncomeCol);
				double wExpense = wResultSet.getDouble(mActExpenseCol);
				wBalance += wIncome - wExpense;
				int wFrequency = wResultSet.getInt(mActFreqCol);
				String wNote = wResultSet.getString(mNoteNameCol);
				RecordTableItem wRecord = new RecordTableItem(wId, wBookId, wDate, wItemId, wItemName, wCategoryId,
						wGroupId, wIncome, wExpense, wBalance, wFrequency, wNote);
				if (wDate.after(wDateNow)) {
					wRecordTableItemListBottom.add(wRecord);
				} else {
					wRecordTableItemListUp.add(wRecord);
				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
			wDbAccess.closeConnection();
		}

		RecordTableItem[][] wRet = new RecordTableItem[2][];
		wRet[0] = (RecordTableItem[]) wRecordTableItemListUp.toArray(new RecordTableItem[0]);
		wRet[1] = (RecordTableItem[]) wRecordTableItemListBottom.toArray(new RecordTableItem[0]);

		return wRet;

	}

	public static Map<Integer, String> getBookNameMap() {
		Map<Integer, String> wBookMap = new LinkedHashMap<Integer, String>();

		DbAccess wDbAccess = new DbAccess();
		String wQuery = "select " + mBookIdCol + ", " + mBookNameCol + " from " + mBookTable + " where " + mDelFlgCol
				+ " = b'0' " + " order by " + mSortKeyCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = wDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				int wBookId = wResultSet.getInt(mBookIdCol);
				String wBookName = wResultSet.getString(mBookNameCol);
				wBookMap.put(wBookId, wBookName);
			}

			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
			wDbAccess.closeConnection();
		}

		return wBookMap;
	}

	private static RecordTableItem getRecordByActId(DbAccess pDbAccess, int pId) {
		RecordTableItem wRecordTableItem = new RecordTableItem();

		String wQuery = "select " + mActIdCol + ", " + mBookIdCol + ", " + mActDtCol + ", " + mActTable + "."
				+ mItemIdCol + ", " + mItemTable + "." + mItemNameCol + ", " + mItemTable + "." + mCategoryIdCol + ", "
				+ mGroupIdCol + ", " + mActIncomeCol + ", " + mActExpenseCol + ", " + mActFreqCol + ", " + mNoteNameCol
				+ " from " + mActTable + ", " + mItemTable + " where " + mActIdCol + " = " + pId + " and " + mActTable
				+ "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = pDbAccess.executeQuery(wQuery);

		try {
			wResultSet.next();

			int wId = wResultSet.getInt(mActIdCol);
			int wBookId = wResultSet.getInt(mBookIdCol);
			Date wDate = wResultSet.getDate(mActDtCol);
			int wItemId = wResultSet.getInt(mItemIdCol);
			String wItemName = wResultSet.getString(mItemTable + "." + mItemNameCol);
			int wCategoryId = wResultSet.getInt(mItemTable + "." + mCategoryIdCol);
			int wGroupId = wResultSet.getInt(mGroupIdCol);
			double wIncome = wResultSet.getDouble(mActIncomeCol);
			double wExpense = wResultSet.getDouble(mActExpenseCol);
			int wFrequency = wResultSet.getInt(mActFreqCol);
			String wNote = wResultSet.getString(mNoteNameCol);
			wRecordTableItem = new RecordTableItem(wId, wBookId, wDate, wItemId, wItemName, wCategoryId, wGroupId,
					wIncome, wExpense, 0, wFrequency, wNote);
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wRecordTableItem;

	}

	public static RecordTableItem getRecordByActId(int pId) {
		DbAccess wDbAccess = new DbAccess();
		RecordTableItem wRecord = getRecordByActId(wDbAccess, pId);
		wDbAccess.closeConnection();
		return wRecord;
	}

	public static String[] getNotes(int pItemId) {
		List<String> wResultList = new ArrayList<String>();

		DbAccess wDbAccess = new DbAccess();
		String wQuery = "select " + mNoteNameCol + " from " + mNoteTable + " where " + mItemIdCol + " = " + pItemId
				+ " and " + mDelFlgCol + " = b'0' " + " order by " + mNoteIdCol + " desc ";

		// System.out.println(wQuery);

		ResultSet wResultSet = wDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				wResultList.add(wResultSet.getString(mNoteNameCol));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
			wDbAccess.closeConnection();
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

		DbAccess wDbAccess = new DbAccess();
		String wQuery = "select " + mItemTable + "." + mItemIdCol + ", " + mItemTable + "." + mItemNameCol;
		wQuery += " from " + mItemTable + " , " + mBookItemTable + ", " + mCategoryTable;
		wQuery += " where " + mItemTable + "." + mItemIdCol + " = " + mBookItemTable + "." + mItemIdCol + " and "
				+ mCategoryTable + "." + mCategoryIdCol + " = " + mItemTable + "." + mCategoryIdCol + " and "
				+ mBookItemTable + "." + mBookIdCol + " = " + pBookId + " and " + mCategoryTable + "."
				+ mCategoryRexpCol + " = " + wRexp + " and " + mBookItemTable + "." + mDelFlgCol + " = b'0' " + " and "
				+ mItemTable + "." + mDelFlgCol + " = b'0' ";
		wQuery += " order by " + mItemTable + "." + mSortKeyCol;
		// System.out.println(wQuery);

		ResultSet wResultSet = wDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				wResultMap.put(wResultSet.getInt(mItemIdCol), wResultSet.getString(mItemNameCol));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
			wDbAccess.closeConnection();
		}

		return wResultMap;
	}

	// For each category
	public static Map<Integer, String> getItemNameMap(int pBookId, int pCategoryId) {
		Map<Integer, String> wResultMap = new LinkedHashMap<Integer, String>();

		DbAccess wDbAccess = new DbAccess();
		String wQuery = "select " + mItemTable + "." + mItemIdCol + ", " + mItemTable + "." + mItemNameCol;
		wQuery += " from " + mItemTable + " , " + mBookItemTable + ", " + mCategoryTable;
		wQuery += " where " + mItemTable + "." + mItemIdCol + " = " + mBookItemTable + "." + mItemIdCol + " and "
				+ mCategoryTable + "." + mCategoryIdCol + " = " + mItemTable + "." + mCategoryIdCol + " and "
				+ mBookItemTable + "." + mBookIdCol + " = " + pBookId + " and " + mCategoryTable + "." + mCategoryIdCol
				+ " = " + pCategoryId + " and " + mBookItemTable + "." + mDelFlgCol + " = b'0' " + " and " + mItemTable
				+ "." + mDelFlgCol + " = b'0' ";
		wQuery += " order by " + mItemTable + "." + mSortKeyCol;
		// System.out.println(wQuery);

		ResultSet wResultSet = wDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				wResultMap.put(wResultSet.getInt(mItemIdCol), wResultSet.getString(mItemNameCol));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
			wDbAccess.closeConnection();
		}

		return wResultMap;
	}

	public static Map<Integer, String> getCategoryNameMap(int pBookId, boolean pIncome) {
		Map<Integer, String> wResultMap = new LinkedHashMap<Integer, String>();
		int wRexp = mIncomeRexp;
		if (!pIncome) {
			wRexp = mExpenseRexp;
		}

		DbAccess wDbAccess = new DbAccess();
		String wQuery = "select count( " + mItemTable + "." + mItemNameCol + " ), " + mCategoryTable + "."
				+ mCategoryIdCol + ", " + mCategoryTable + "." + mCategoryNameCol;
		wQuery += " from " + mItemTable + ", " + mBookItemTable + ", " + mCategoryTable;
		wQuery += " where " + mItemTable + "." + mItemIdCol + " = " + mBookItemTable + "." + mItemIdCol + " and "
				+ mCategoryTable + "." + mCategoryIdCol + " = " + mItemTable + "." + mCategoryIdCol;
		if (pBookId != SystemData.getAllBookInt()) {
			wQuery += " and " + mBookItemTable + "." + mBookIdCol + " = " + pBookId;
		}
		wQuery += " and " + mCategoryTable + "." + mCategoryRexpCol + " = " + wRexp + " and " + mBookItemTable + "."
				+ mDelFlgCol + " = b'0' " + " and " + mItemTable + "." + mDelFlgCol + " = b'0' ";
		wQuery += " group by " + mCategoryTable + "." + mCategoryNameCol;
		wQuery += " order by " + mCategoryTable + "." + mSortKeyCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = wDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				wResultMap.put(wResultSet.getInt(mCategoryIdCol), wResultSet.getString(mCategoryNameCol));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
			wDbAccess.closeConnection();
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

		DbAccess wDbAccess = new DbAccess();
		String wQuery = "select " + mCategoryIdCol + ", " + mCategoryNameCol;
		wQuery += " from " + mCategoryTable;
		wQuery += " where " + mCategoryRexpCol + " = " + wRexp + " and " + mDelFlgCol + " = b'0' " + " and "
				+ mSortKeyCol + " > " + 0;
		wQuery += " order by " + mSortKeyCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = wDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				wResultMap.put(wResultSet.getInt(mCategoryIdCol), wResultSet.getString(mCategoryNameCol));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
			wDbAccess.closeConnection();
		}

		return wResultMap;
	}

	public static boolean isIncomeCategory(int pCategoryId) {

		DbAccess wDbAccess = new DbAccess();
		String wQuery = "select " + mCategoryRexpCol;
		wQuery += " from " + mCategoryTable;
		wQuery += " where " + mCategoryIdCol + " = " + pCategoryId + " and " + mDelFlgCol + " = b'0' ";

		// System.out.println(wQuery);

		ResultSet wResultSet = wDbAccess.executeQuery(wQuery);
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
		} finally {
			wDbAccess.closeConnection();
		}

		return false;

	}

	public static void insertNewRecord(int pBookId, int pItemId, int pYear, int pMonth, int pDay, int pIncome,
			int pExpense, int pFrequency, String pNote) {

		pNote = getNoteStringWithEscape(pNote);

		DbAccess wDbAccess = new DbAccess();
		String wQueryBase1 = "insert into  " + mActTable + " ( " + mBookIdCol + "," + mItemIdCol + "," + mActDtCol
				+ "," + mActIncomeCol + "," + mActExpenseCol;
		String wQueryBase2 = " values(" + pBookId + "," + pItemId + ",'" + pYear + "-" + pMonth + "-" + pDay + "',"
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
			wDbAccess.executeUpdate(wQuery);

		} else if (pFrequency > 0) {
			int wGroupId = getNewGroupId(wDbAccess);
			Calendar wCalBase = new GregorianCalendar(pYear, pMonth - 1, pDay);
			for (int i = 0; i < pFrequency + 1; i++) {
				Calendar wCal = (Calendar) wCalBase.clone();
				wCal.add(Calendar.MONTH, +i);
				String wDate = getDateStrings(wCal.getTime());

				wQueryBase1 = "insert into  " + mActTable + " ( " + mBookIdCol + "," + mItemIdCol + "," + mActDtCol
						+ "," + mActIncomeCol + "," + mActExpenseCol + "," + mGroupIdCol + "," + mActFreqCol;

				wQueryBase2 = " values(" + pBookId + "," + pItemId + "," + wDate + "," + pIncome + "," + pExpense + ","
						+ wGroupId + "," + (pFrequency - i);

				wQuery = wQueryBase1 + wQueryNote1 + ") " + wQueryBase2 + wQueryNote2 + ")";
				// System.out.println(wQuery);
				wDbAccess.executeUpdate(wQuery);

			}

		}

		// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
		if (!"".equals(pNote)) {
			updateNoteTable(wDbAccess, pItemId, pNote);
		}

		wDbAccess.closeConnection();

	}

	public static void updateRecord(int pActId, int pBookId, int pItemId, int pYear, int pMonth, int pDay, int pIncome,
			int pExpense, int pFrequency, String pNote) {

		pNote = getNoteStringWithEscape(pNote);

		DbAccess wDbAccess = new DbAccess();

		RecordTableItem wOldRecord = getRecordByActId(wDbAccess, pActId);
		int wOldGroupId = wOldRecord.getGroupId();
		int wOldItemId = wOldRecord.getItemId();
		int wOldBookId = wOldRecord.getBookId();

		String wDate = "'" + pYear + "-" + pMonth + "-" + pDay + "'";
		int wGroupId = 0;

		String wQuery = "";

		// 　ともに繰り返し0ならUpdateのみ
		if (pFrequency == 0 && wOldGroupId == 0) {
			wQuery = "update " + mActTable + " set " + mBookIdCol + " = " + pBookId + ", " + mItemIdCol + " = "
					+ pItemId + ", " + mActDtCol + " = " + wDate + ", " + mActIncomeCol + " = " + pIncome + ", "
					+ mActExpenseCol + " = " + pExpense + ", " + mNoteNameCol + " = '" + pNote + "' ";
			wQuery += " where " + mActIdCol + " = " + pActId;
			// System.out.println(wQuery);
			wDbAccess.executeUpdate(wQuery);

		} else {
			// どちらかが繰り返しありなら既存レコードを削除して新規追加

			// 既存のレコードを削除
			deleteRecord(wDbAccess, pActId);

			// 元の日付を取得
			Date wOldDate = wOldRecord.getDate();
			Calendar wOldCal = new GregorianCalendar();
			wOldCal.setTime(wOldDate);

			// 年月, BookId, ItemIdが変更された場合は新規のGroupIdを使用
			if (pYear != wOldCal.get(Calendar.YEAR) || pMonth != wOldCal.get(Calendar.MONTH) + 1
					|| pBookId != wOldBookId || pItemId != wOldItemId) {
				wGroupId = getNewGroupId(wDbAccess);
			} else {
				wGroupId = wOldGroupId;
			}

			// 新規のレコードを追加
			Calendar wCalBase = new GregorianCalendar(pYear, pMonth - 1, pDay);
			for (int i = 0; i < pFrequency + 1; i++) {
				Calendar wCal = (Calendar) wCalBase.clone();
				wCal.add(Calendar.MONTH, +i);
				wDate = getDateStrings(wCal.getTime());

				String wQueryBase = "insert into  " + mActTable + " ( " + mBookIdCol + "," + mItemIdCol + ","
						+ mActDtCol + "," + mActIncomeCol + "," + mActExpenseCol + "," + mGroupIdCol + ","
						+ mActFreqCol;

				String wQueryValues = " values(" + pBookId + "," + pItemId + "," + wDate + "," + pIncome + ","
						+ pExpense + "," + wGroupId + "," + (pFrequency - i);

				if (!"".equals(pNote)) {
					wQuery = wQueryBase + "," + mNoteNameCol + ")" + wQueryValues + ",'" + pNote + "')";
				} else {
					wQuery = wQueryBase + ") " + wQueryValues + ")";
				}

				// System.out.println(wQuery);

				wDbAccess.executeUpdate(wQuery);
			}

			// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
			if (!"".equals(pNote)) {
				updateNoteTable(wDbAccess, pItemId, pNote);
			}

			wDbAccess.closeConnection();

		}
	}

	public static void insertNewMoveRecord(int pBookFromId, int pBookToId, int pYear, int pMonth, int pDay, int pValue,
			int pFrequency, String pNote) {

		pNote = getNoteStringWithEscape(pNote);

		DbAccess wDbAccess = new DbAccess();
		int wGroupId = getNewGroupId(wDbAccess);

		String wQueryBase = "insert into  " + mActTable + " ( " + mBookIdCol + "," + mItemIdCol + "," + mActIncomeCol
				+ "," + mActExpenseCol + "," + mGroupIdCol + "," + mActDtCol;
		String wQueryFromValues = " values(" + pBookFromId + "," + getMoveExpenseItemId() + ",'0'," + pValue + ","
				+ wGroupId;
		String wQueryToValues = " values(" + pBookToId + "," + getMoveIncomeItemId() + "," + pValue + ",'0',"
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
			wQueryFrom = wQueryBase + wQueryNote1 + ") " + wQueryFromValues + wDate + wQueryNote2 + ")";
			// System.out.println(wQueryFrom);
			wDbAccess.executeUpdate(wQueryFrom);
			wQueryTo = wQueryBase + wQueryNote1 + ") " + wQueryToValues + wDate + wQueryNote2 + ")";
			// System.out.println(wQueryTo);
			wDbAccess.executeUpdate(wQueryTo);
		} else { // pFrequency > 0

			Calendar wCalBase = new GregorianCalendar(pYear, pMonth - 1, pDay);
			String wQueryFreq = "," + mActFreqCol;
			for (int i = 0; i < pFrequency + 1; i++) {
				Calendar wCal = (Calendar) wCalBase.clone();
				wCal.add(Calendar.MONTH, +i);
				String wDate = getDateStrings(wCal.getTime());

				wQueryFrom = wQueryBase + wQueryNote1 + wQueryFreq + ") " + wQueryFromValues + wDate + wQueryNote2
						+ "," + (pFrequency - i) + ")";
				// System.out.println(wQueryFrom);
				wDbAccess.executeUpdate(wQueryFrom);
				wQueryTo = wQueryBase + wQueryNote1 + wQueryFreq + ") " + wQueryToValues + wDate + wQueryNote2 + ","
						+ (pFrequency - i) + ")";
				// System.out.println(wQueryTo);
				wDbAccess.executeUpdate(wQueryTo);
			}

		}

		// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
		if (!"".equals(pNote)) {
			updateNoteTable(wDbAccess, getMoveIncomeItemId(), pNote);
		}

		wDbAccess.closeConnection();

	}

	public static void updateMoveRecord(int pIncomeActId, int pBookFromId, int pBookToId, int pYear, int pMonth,
			int pDay, int pValue, int pFrequency, String pNote) {

		pNote = getNoteStringWithEscape(pNote);

		DbAccess wDbAccess = new DbAccess();

		RecordTableItem wOldIncomeRecord = getRecordByActId(wDbAccess, pIncomeActId);
		RecordTableItem wOldExpenseRecord = getMovePairRecord(wDbAccess, wOldIncomeRecord);
		int wExpenseActId = wOldExpenseRecord.getId();

		int wOldGroupId = wOldIncomeRecord.getGroupId();
		int wOldFromBookId = wOldExpenseRecord.getBookId();
		int wOldToBookId = wOldIncomeRecord.getBookId();

		String wDate;
		String wQueryFrom;
		String wQueryTo;

		// ともに繰り返し0ならUpdateのみ
		if (isSingleMoveRecordPair(wDbAccess, wOldGroupId) && pFrequency == 0) {
			wDate = "'" + pYear + "-" + pMonth + "-" + pDay + "'";
			wQueryFrom = "update " + mActTable + " set " + mBookIdCol + " = " + pBookFromId + ", " + mItemIdCol + " = "
					+ getMoveExpenseItemId() + ", " + mActDtCol + " = " + wDate + ", " + mActIncomeCol + " = "
					+ "'0', " + mActExpenseCol + " = " + pValue + ", " + mNoteNameCol + " = '" + pNote + "' "
					+ " where " + mActIdCol + " = " + wExpenseActId;
			// System.out.println(wQueryFrom);

			wDbAccess.executeUpdate(wQueryFrom);
			wQueryTo = "update " + mActTable + " set " + mBookIdCol + " = " + pBookToId + ", " + mItemIdCol + " = "
					+ getMoveIncomeItemId() + ", " + mActDtCol + " = " + wDate + ", " + mActIncomeCol + " = " + +pValue
					+ ", " + mActExpenseCol + " = " + "'0'" + ", " + mNoteNameCol + " = '" + pNote + "' " + " where "
					+ mActIdCol + " = " + pIncomeActId;
			// System.out.println(wQueryTo);
			wDbAccess.executeUpdate(wQueryTo);

		} else {
			// どちらかが繰り返しありなら既存レコードを削除して新規追加

			// 既存のレコードを削除
			deleteRecord(wDbAccess, pIncomeActId);

			// 元の日付を取得
			int wGroupId;
			Date wOldDate = wOldIncomeRecord.getDate();
			Calendar wOldCal = new GregorianCalendar();
			wOldCal.setTime(wOldDate);

			// 年月, BookIdが変更された場合は新規のGroupIdを使用
			if (pYear != wOldCal.get(Calendar.YEAR) || pMonth != wOldCal.get(Calendar.MONTH) + 1
					|| pBookFromId != wOldFromBookId || pBookToId != wOldToBookId) {
				wGroupId = getNewGroupId(wDbAccess);
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

				String wQueryBase = "insert into " + mActTable + " ( " + mBookIdCol + "," + mItemIdCol + ","
						+ mActDtCol + "," + mActIncomeCol + "," + mActExpenseCol + "," + mGroupIdCol + ","
						+ mActFreqCol;

				String wQueryFromValue = " values(" + pBookFromId + "," + getMoveExpenseItemId() + "," + wDate + ","
						+ "'0'" + "," + pValue + "," + wGroupId + "," + (pFrequency - i);

				String wQueryToValue = " values(" + pBookToId + "," + getMoveIncomeItemId() + "," + wDate + ","
						+ pValue + "," + "'0'" + "," + wGroupId + "," + (pFrequency - i);

				String wQueryNote1 = ")";
				String wQueryNote2 = ")";

				if (!"".equals(pNote)) {
					wQueryNote1 = "," + mNoteNameCol + ")";
					wQueryNote2 = ",'" + pNote + "')";
				}

				wQueryFrom = wQueryBase + wQueryNote1 + wQueryFromValue + wQueryNote2;
				// System.out.println(wQueryFrom);
				wDbAccess.executeUpdate(wQueryFrom);

				wQueryTo = wQueryBase + wQueryNote1 + wQueryToValue + wQueryNote2;
				// System.out.println(wQueryTo);
				wDbAccess.executeUpdate(wQueryTo);
			}

			// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
			if (!"".equals(pNote)) {
				updateNoteTable(wDbAccess, getMoveIncomeItemId(), pNote);
			}

			wDbAccess.closeConnection();

		}
	}

	private static void deleteRecord(DbAccess pDbAccess, int pActId) {
		RecordTableItem wRecord = getRecordByActId(pDbAccess, pActId);
		int wGroupId = wRecord.getGroupId();

		String wQuery = "delete from " + mActTable + " where ";

		if (wGroupId == 0) {
			// 単一レコードの削除
			wQuery += mActIdCol + " = " + pActId;
			// System.out.println(wQuery);
			pDbAccess.executeUpdate(wQuery);
		} else {
			// 複数レコード（同一GroupId,対象日付以降）
			String wDate = getDateStrings(wRecord.getDate());
			wQuery += mGroupIdCol + " = " + wGroupId + " and " + mActDtCol + " >= " + wDate;
			// System.out.println(wQuery);
			pDbAccess.executeUpdate(wQuery);
		}

	}

	public static void deleteRecord(int pActId) {
		DbAccess wDbAccess = new DbAccess();
		deleteRecord(wDbAccess, pActId);
		wDbAccess.closeConnection();

	}

	public static int getMoveIncomeItemId() {
		return mIncomeRexp;
	}

	public static int getMoveExpenseItemId() {
		return mExpenseRexp;
	}

	private static RecordTableItem getMovePairRecord(DbAccess pDbAccess, RecordTableItem pRecord) {

		int wActId = pRecord.getId();
		int wGroupId = pRecord.getGroupId();
		String wDate = getDateStrings(pRecord.getDate());

		int wPairActId = 0;

		String wQuery = "select " + mActIdCol + " from " + mActTable + " where " + mGroupIdCol + " = " + wGroupId
				+ " and " + mActDtCol + " = " + wDate + " and " + mActIdCol + " <> " + wActId;

		// System.out.println(wQuery);
		ResultSet wResultSet = pDbAccess.executeQuery(wQuery);

		try {
			wResultSet.next();
			wPairActId = wResultSet.getInt(mActIdCol);
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		RecordTableItem wPairRecord = getRecordByActId(pDbAccess, wPairActId);

		return wPairRecord;

	}

	public static RecordTableItem getMovePairRecord(RecordTableItem pRecord) {
		DbAccess wDbAccess = new DbAccess();
		RecordTableItem wRecord = getMovePairRecord(wDbAccess, pRecord);
		wDbAccess.closeConnection();
		return wRecord;
	}

	public static boolean isMoveRecord(int pActId) {
		RecordTableItem wRecord = getRecordByActId(pActId);
		return (isMoveItem(wRecord.getItemId()));
	}

	public static boolean isMoveItem(int pItemId) {
		if (pItemId == getMoveIncomeItemId() || pItemId == getMoveExpenseItemId()) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isMoveCategory(int pCategoryId) {
		if (pCategoryId == getMoveIncomeItemId() || pCategoryId == getMoveExpenseItemId()) {
			return true;
		} else {
			return false;
		}
	}

	public static SummaryTableItem[] getSummaryTableItems(int pBookId, Date pStartDate, Date pEndDate) {
		List<SummaryTableItem> wSummaryTableItemList = new ArrayList<SummaryTableItem>();

		String wStartDateString = getDateStrings(pStartDate);
		String wEndDateString = getDateStrings(pEndDate);

		DbAccess wDbAccess = new DbAccess();
		ResultSet wResultSet;
		String wQuery;

		// for all books
		double wTempBalance = 0;
		double wAppearedBalance = 0;
		double wActualBalance = 0;

		double wTotalAppearedProfit = 0;
		double wSpecialProfit = 0;
		double wTempProfit = 0;
		double wActualProfit = 0;
		double wOperatingProfit = 0;

		// for each book
		double wBookAppearedProfit = 0;
		double wBookAppearedIncome = 0;
		double wBookAppearedExpense = 0;

		// 立替残高（借入残高）
		wTempBalance = getTempBalance(wDbAccess, pEndDate);

		// みかけ残高、実質残高
		wAppearedBalance = getBalance(wDbAccess, pEndDate, mAllBookId, true);
		wActualBalance = wAppearedBalance - wTempBalance;

		// みかけ収支（Total）
		double[] wTotalAppearedIncomeExpense = getAppearedIncomeExpense(wDbAccess, pStartDate, pEndDate, mAllBookId);
		wTotalAppearedProfit = wTotalAppearedIncomeExpense[0] - wTotalAppearedIncomeExpense[1];

		// 立替収支
		wTempProfit = getTotalTempProfit(wDbAccess, pStartDate, pEndDate);

		// 特別収支
		wSpecialProfit = getTotalSpecialProfit(wDbAccess, pStartDate, pEndDate);

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
			double[] wBookAppearedIncomeExpense = getAppearedIncomeExpense(wDbAccess, pStartDate, pEndDate, pBookId);
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

		wQuery = "select " + mCategoryTable + "." + mCategoryIdCol + ", " + mCategoryTable + "." + mCategoryNameCol
				+ ", sum(" + mActIncomeCol + ") as " + mActIncomeCol + ", sum(" + mActExpenseCol + ") as "
				+ mActExpenseCol + " from " + mActTable + ", " + mItemTable + ", " + mCategoryTable + " where "
				+ mActTable + "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol + " and " + mCategoryTable + "."
				+ mCategoryIdCol + " = " + mItemTable + "." + mCategoryIdCol + " and " + mActDtCol + " between "
				+ wStartDateString + " and " + wEndDateString + " and " + mActTable + "." + mDelFlgCol + " = b'0'"
				+ " and (" + mActTable + "." + mActIncomeCol + " + " + mActTable + "." + mActExpenseCol + ") > 0";
		if (pBookId != mAllBookId) {
			wQuery += " and " + mActTable + "." + mBookIdCol + " = " + pBookId;
		}
		wQuery += " group by " + mCategoryTable + "." + mCategoryIdCol + " order by " + mCategoryTable + "."
				+ mCategoryRexpCol + ", " + mCategoryTable + "." + mSortKeyCol;
		// System.out.println(wQuery);

		wResultSet = wDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryTable + "." + mCategoryIdCol);
				if (pBookId == mAllBookId && (DbUtil.isMoveCategory(wCategoryId))) {

				} else {
					String wCategoryName = wResultSet.getString(mCategoryTable + "." + mCategoryNameCol);
					double wIncome = wResultSet.getDouble(mActIncomeCol);
					double wExpense = wResultSet.getDouble(mActExpenseCol);
					// System.out.println(wCategoryName + ", " + wIncome + ", "
					// + wExpense);
					List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();
					if (wIncome > 0) {
						wList.add(new SummaryTableItem(wCategoryId, wCategoryName, wIncome, true));
					} else {
						wList.add(new SummaryTableItem(wCategoryId, wCategoryName, wExpense, false));
					}
					wSummaryTableMap.put(wCategoryId, wList);
				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		// Item集計
		wQuery = "select " + mCategoryTable + "." + mCategoryIdCol + ", " + mActTable + "." + mItemIdCol + ", "
				+ mItemTable + "." + mItemNameCol + ", sum(" + mActIncomeCol + ") as " + mActIncomeCol + ", sum("
				+ mActExpenseCol + ") as " + mActExpenseCol + " from " + mActTable + ", " + mItemTable + ", "
				+ mCategoryTable + " where " + mActTable + "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol
				+ " and " + mCategoryTable + "." + mCategoryIdCol + " = " + mItemTable + "." + mCategoryIdCol + " and "
				+ mActDtCol + " between " + wStartDateString + " and " + wEndDateString + " and " + mActTable + "."
				+ mDelFlgCol + " = b'0'" + " and (" + mActTable + "." + mActIncomeCol + " + " + mActTable + "."
				+ mActExpenseCol + ") > 0";
		if (pBookId != mAllBookId) {
			wQuery += " and " + mActTable + "." + mBookIdCol + " = " + pBookId;
		}
		wQuery += " group by " + mActTable + "." + mItemIdCol + " order by " + mCategoryTable + "." + mCategoryRexpCol
				+ ", " + mCategoryTable + "." + mSortKeyCol + ", " + mItemTable + "." + mSortKeyCol;

		// System.out.println(wQuery);
		wResultSet = wDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryTable + "." + mCategoryIdCol);
				if (pBookId == mAllBookId && (isMoveCategory(wCategoryId))) {

				} else {
					List<SummaryTableItem> wList = wSummaryTableMap.get(wCategoryId);
					int wItemId = wResultSet.getInt(mActTable + "." + mItemIdCol);
					String wItemName = wResultSet.getString(mItemTable + "." + mItemNameCol);
					double wIncome = wResultSet.getDouble(mActIncomeCol);
					double wExpense = wResultSet.getDouble(mActExpenseCol);
					// System.out.println(wItemName + ", " + wIncome + ", "
					// + wExpense);
					if (wIncome > 0) {
						wList.add(new SummaryTableItem(wItemId, wItemName, wCategoryId, wIncome, true));
					} else {
						wList.add(new SummaryTableItem(wItemId, wItemName, wCategoryId, wExpense, false));
					}
				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		wDbAccess.closeConnection();

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
	public static List<SummaryTableItem[]> getAnnualSummaryTableItemsCategory(int pBookId, Date[][] pDatePeriods) {

		String wTotalStartDateString = getDateStrings(pDatePeriods[0][0]);
		String wTotalEndDateString = getDateStrings(pDatePeriods[pDatePeriods.length - 1][1]);

		// 合計行が必要なら追加
		int wSummationIndex = Util.getSummationIndex(pDatePeriods);
		Date[][] wDatePeriods = Util.getDatePeriodsWithSummaion(pDatePeriods);
		// Summation + Average
		int wDatePeriodCount = pDatePeriods.length + 2 * (wDatePeriods.length - pDatePeriods.length);

		// 結果を挿入するリスト
		// List<List<SummaryTableItem>> wSummaryTableItemList = new
		// ArrayList<List<SummaryTableItem>>(wDatePeriodCount);
		Map<Double, List<SummaryTableItem>> wSummaryTableItemListMap = new TreeMap<Double, List<SummaryTableItem>>();

		// for (int i = 0; i < wDatePeriodCount; i++) {
		// List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();
		// wSummaryTableItemList.add(wList);
		// }

		DbAccess wDbAccess = new DbAccess();
		ResultSet wResultSet;
		String wQuery;
		String wPeriodName = "Period";

		// // for each book
		// double wBookAppearedProfit = 0;
		// double wBookAppearedIncome = 0;
		// double wBookAppearedExpense = 0;

		// カテゴリ集計
		// CategoryId-SummaryTableItemList

		wQuery = "select " + mCategoryTable + "." + mCategoryRexpCol + ", " + mCategoryTable + "." + mSortKeyCol + ", "
				+ mCategoryTable + "." + mCategoryIdCol + ", " + mCategoryTable + "." + mCategoryNameCol;

		for (int i = 0; i < wDatePeriods.length; i++) {
			Date[] wDates = wDatePeriods[i];
			String wStartDateString = getDateStrings(wDates[0]);
			String wEndDateString = getDateStrings(wDates[1]);
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString + " and "
					+ wEndDateString + " then " + mActIncomeCol + " end),0) '" + mActIncomeCol + wPeriodName + i + "'";
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString + " and "
					+ wEndDateString + " then " + mActExpenseCol + " end),0) '" + mActExpenseCol + wPeriodName + i
					+ "'";
		}

		wQuery += " from " + mActTable + ", " + mItemTable + ", " + mCategoryTable;
		wQuery += " where " + mActTable + "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol;
		wQuery += " and " + mItemTable + "." + mCategoryIdCol + " = " + mCategoryTable + "." + mCategoryIdCol;
		wQuery += " and " + mActIncomeCol + " + " + mActExpenseCol + " > 0 ";
		wQuery += " and " + mActTable + "." + mDelFlgCol + " = b'0'";
		wQuery += " and " + mActDtCol + " between " + wTotalStartDateString + " and " + wTotalEndDateString;
		if (pBookId != mAllBookId) {
			wQuery += " and " + mActTable + "." + mBookIdCol + " = " + pBookId;
		}
		wQuery += " group by " + mCategoryTable + "." + mCategoryIdCol + " with rollup";
		// wQuery += " order by " + mCategoryRexpCol + ", " + mCategoryTable +
		// "." + mSortKeyCol;
		// System.out.println(wQuery);

		wResultSet = wDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryTable + "." + mCategoryIdCol);
				if (pBookId == mAllBookId && (DbUtil.isMoveCategory(wCategoryId))) {

				} else {

					int wRexpDiv = wResultSet.getInt(mCategoryTable + "." + mCategoryRexpCol);
					int wCategorySortKey = wResultSet.getInt(mCategoryTable + "." + mSortKeyCol);
					List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();

					String wCategoryName = wResultSet.getString(mCategoryTable + "." + mCategoryNameCol);
					// boolean isSummationInput = false;

					// 集計行
					if (wCategoryId == 0) {
						List<SummaryTableItem> wListIncome = new ArrayList<SummaryTableItem>();
						List<SummaryTableItem> wListExpense = new ArrayList<SummaryTableItem>();
						for (int i = 0; i < wDatePeriods.length; i++) {
							double wIncome = wResultSet.getDouble(mActIncomeCol + wPeriodName + i);
							double wExpense = wResultSet.getDouble(mActExpenseCol + wPeriodName + i);

							wList.add(new SummaryTableItem("総収支", wIncome - wExpense, true));
							wListIncome.add(new SummaryTableItem("総収入", wIncome, true));
							wListExpense.add(new SummaryTableItem("総支出", wExpense, true));

							if (i == wSummationIndex) {
								wList.add(new SummaryTableItem("総収支", (wIncome - wExpense) / i, true));
								wListIncome.add(new SummaryTableItem("総収入", wIncome / i, true));
								wListExpense.add(new SummaryTableItem("総支出", wExpense / i, true));
							}
						}

						wSummaryTableItemListMap.put(0.0, wList);
						wSummaryTableItemListMap.put(1.0, wListIncome);
						double wKey = mExpenseRexp * Math.pow(10, 5);
						// System.out.println(wKey);
						wSummaryTableItemListMap.put(wKey, wListExpense);

					} else {

						for (int i = 0; i < wDatePeriods.length; i++) {
							double wIncome = wResultSet.getDouble(mActIncomeCol + wPeriodName + i);
							double wExpense = wResultSet.getDouble(mActExpenseCol + wPeriodName + i);

							if (wRexpDiv == mIncomeRexp) {
								wList.add(new SummaryTableItem(wCategoryId, wCategoryName, wIncome, true));
								if (i == wSummationIndex) {
									wList.add(new SummaryTableItem(wCategoryId, wCategoryName, wIncome / i, true));
								}
							} else {
								wList.add(new SummaryTableItem(wCategoryId, wCategoryName, wExpense, false));
								if (i == wSummationIndex) {
									wList.add(new SummaryTableItem(wCategoryId, wCategoryName, wExpense / i, false));
								}
							}

							// if (wIncome > 0) {
							// if (isSummationInput) {
							// wSummaryTableItemList.get(i + 1).add(
							// new SummaryTableItem(wCategoryId, wCategoryName,
							// wIncome, true));
							// } else {
							// wSummaryTableItemList.get(i).add(
							// new SummaryTableItem(wCategoryId, wCategoryName,
							// wIncome, true));
							// }
							// } else {
							// if (isSummationInput) {
							// wSummaryTableItemList.get(i + 1).add(
							// new SummaryTableItem(wCategoryId, wCategoryName,
							// wExpense, false));
							// } else {
							// wSummaryTableItemList.get(i).add(
							// new SummaryTableItem(wCategoryId, wCategoryName,
							// wExpense, false));
							// }
							// }
							// if (i == wSummationIndex) {
							// if (wIncome > 0) {
							// wSummaryTableItemList.get(i + 1).add(
							// new SummaryTableItem(wCategoryId, wCategoryName,
							// wIncome / i, true));
							// } else {
							// wSummaryTableItemList.get(i + 1).add(
							// new SummaryTableItem(wCategoryId, wCategoryName,
							// wExpense / i, false));
							// }
							// isSummationInput = true;
							// }
						}

						double wKey = wRexpDiv * Math.pow(10, 5) + wCategorySortKey;
						// System.out.println(wKey);
						wSummaryTableItemListMap.put(wKey, wList);

					}
				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		wDbAccess.closeConnection();

		List<SummaryTableItem[]> wReturnList = new ArrayList<SummaryTableItem[]>(wDatePeriodCount);

		for (int i = 0; i < wDatePeriodCount; i++) {
			List<SummaryTableItem> wRowList = new ArrayList<SummaryTableItem>();
			for (Map.Entry<Double, List<SummaryTableItem>> wEntry : wSummaryTableItemListMap.entrySet()) {
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
	public static List<SummaryTableItem[]> getAnnualSummaryTableItems(int pBookId, Date[][] pDatePeriods) {

		String wTotalStartDateString = getDateStrings(pDatePeriods[0][0]);
		String wTotalEndDateString = getDateStrings(pDatePeriods[pDatePeriods.length - 1][1]);

		// 合計行が必要なら追加
		int wSummationIndex = Util.getSummationIndex(pDatePeriods);
		Date[][] wDatePeriods = Util.getDatePeriodsWithSummaion(pDatePeriods);
		// Summation + Average
		int wDatePeriodCount = pDatePeriods.length + 2 * (wDatePeriods.length - pDatePeriods.length);

		// 結果を挿入するリスト
		// List<List<SummaryTableItem>> wSummaryTableItemList = new
		// ArrayList<List<SummaryTableItem>>(wDatePeriodCount);
		Map<Double, List<SummaryTableItem>> wSummaryTableItemListMap = new TreeMap<Double, List<SummaryTableItem>>();

		// for (int i = 0; i < wDatePeriodCount; i++) {
		// List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();
		// wSummaryTableItemList.add(wList);
		// }

		DbAccess wDbAccess = new DbAccess();
		ResultSet wResultSet;
		String wQuery;
		String wPeriodName = "Period";

		// // for each book
		// double wBookAppearedProfit = 0;
		// double wBookAppearedIncome = 0;
		// double wBookAppearedExpense = 0;

		// カテゴリ集計
		// CategoryId-SummaryTableItemList

		wQuery = "select " + mCategoryTable + "." + mCategoryRexpCol + ", " + mCategoryTable + "." + mCategoryIdCol
				+ ", " + mCategoryTable + "." + mSortKeyCol + ", " + mItemTable + "." + mSortKeyCol + ", " + mItemTable
				+ "." + mItemIdCol + ", " + mItemTable + "." + mItemNameCol;

		for (int i = 0; i < wDatePeriods.length; i++) {
			Date[] wDates = wDatePeriods[i];
			String wStartDateString = getDateStrings(wDates[0]);
			String wEndDateString = getDateStrings(wDates[1]);
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString + " and "
					+ wEndDateString + " then " + mActIncomeCol + " end),0) '" + mActIncomeCol + wPeriodName + i + "'";
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString + " and "
					+ wEndDateString + " then " + mActExpenseCol + " end),0) '" + mActExpenseCol + wPeriodName + i
					+ "'";
		}

		wQuery += " from " + mActTable + ", " + mItemTable + ", " + mCategoryTable;
		wQuery += " where " + mActTable + "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol;
		wQuery += " and " + mItemTable + "." + mCategoryIdCol + " = " + mCategoryTable + "." + mCategoryIdCol;
		wQuery += " and " + mActIncomeCol + " + " + mActExpenseCol + " > 0 ";
		wQuery += " and " + mActTable + "." + mDelFlgCol + " = b'0'";
		wQuery += " and " + mActDtCol + " between " + wTotalStartDateString + " and " + wTotalEndDateString;
		if (pBookId != mAllBookId) {
			wQuery += " and " + mActTable + "." + mBookIdCol + " = " + pBookId;
		}
		wQuery += " group by " + mItemTable + "." + mItemIdCol + " with rollup";
		// wQuery += " order by " + mCategoryRexpCol + ", " + mCategoryTable +
		// "."
		// + mSortKeyCol + ", " + mItemTable + "." + mSortKeyCol;
		// System.out.println(wQuery);

		wResultSet = wDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryTable + "." + mCategoryIdCol);
				int wItemId = wResultSet.getInt(mItemTable + "." + mItemIdCol);

				// 全帳簿なら移動項目は除く
				if (pBookId == mAllBookId && (DbUtil.isMoveCategory(wCategoryId))) {

				} else {

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
							double wIncome = wResultSet.getDouble(mActIncomeCol + wPeriodName + i);
							double wExpense = wResultSet.getDouble(mActExpenseCol + wPeriodName + i);

							wList.add(new SummaryTableItem("総収支", wIncome - wExpense, true));
							wListIncome.add(new SummaryTableItem("総収入", wIncome, true));
							wListExpense.add(new SummaryTableItem("総支出", wExpense, true));

							if (i == wSummationIndex) {
								wList.add(new SummaryTableItem("総収支", (wIncome - wExpense) / i, true));
								wListIncome.add(new SummaryTableItem("総収入", wIncome / i, true));
								wListExpense.add(new SummaryTableItem("総支出", wExpense / i, true));
							}
						}

						wSummaryTableItemListMap.put(0.0, wList);
						wSummaryTableItemListMap.put(1.0, wListIncome);
						double wKey = mExpenseRexp * Math.pow(10, 10);
						// System.out.println(wKey);
						wSummaryTableItemListMap.put(wKey, wListExpense);

					} else {

						for (int i = 0; i < wDatePeriods.length; i++) {
							double wIncome = wResultSet.getDouble(mActIncomeCol + wPeriodName + i);
							double wExpense = wResultSet.getDouble(mActExpenseCol + wPeriodName + i);

							if (wRexpDiv == mIncomeRexp) {
								wList.add(new SummaryTableItem(wItemId, wItemName, wCategoryId, wIncome, true));
								if (i == wSummationIndex) {
									wList.add(new SummaryTableItem(wItemId, wItemName, wCategoryId, wIncome / i, true));
								}
							} else {
								wList.add(new SummaryTableItem(wItemId, wItemName, wCategoryId, wExpense, false));
								if (i == wSummationIndex) {
									wList
											.add(new SummaryTableItem(wItemId, wItemName, wCategoryId, wExpense / i,
													false));
								}
							}

							// if (wIncome > 0) {
							// if (isSummationInput) {
							// wSummaryTableItemList.get(i + 1).add(
							// new SummaryTableItem(wItemId, wItemName,
							// wCategoryId, wIncome, true));
							// } else {
							// wSummaryTableItemList.get(i).add(
							// new SummaryTableItem(wItemId, wItemName,
							// wCategoryId, wIncome, true));
							// }
							// } else {
							// if (isSummationInput) {
							// wSummaryTableItemList.get(i + 1).add(
							// new SummaryTableItem(wItemId, wItemName,
							// wCategoryId, wExpense, false));
							// } else {
							// wSummaryTableItemList.get(i).add(
							// new SummaryTableItem(wItemId, wItemName,
							// wCategoryId, wExpense, false));
							// }
							// }
							// if (i == wSummationIndex) {
							// if (wIncome > 0) {
							// wSummaryTableItemList.get(i + 1).add(
							// new SummaryTableItem(wItemId, wItemName,
							// wCategoryId, wIncome / i, true));
							// } else {
							// wSummaryTableItemList.get(i + 1).add(
							// new SummaryTableItem(wItemId, wItemName,
							// wCategoryId, wExpense / i, false));
							// }
							// isSummationInput = true;
							// }
						}

						double wKey = wRexpDiv * Math.pow(10, 10) + wCategorySortKey * Math.pow(10, 5) + wItemSortKey;
						// System.out.println(wKey);
						wSummaryTableItemListMap.put(wKey, wList);

					}
				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		wDbAccess.closeConnection();

		// int wRowCount = wSummaryTableItemListMap.get(0.0).size();
		List<SummaryTableItem[]> wReturnList = new ArrayList<SummaryTableItem[]>(wDatePeriodCount);

		// for (int i=0; i < wRowCount; i++) {
		// wReturnList.add(new
		// SummaryTableItem[wSummaryTableItemListMap.keySet().size()]);
		// }

		for (int i = 0; i < wDatePeriodCount; i++) {
			List<SummaryTableItem> wRowList = new ArrayList<SummaryTableItem>();
			for (Map.Entry<Double, List<SummaryTableItem>> wEntry : wSummaryTableItemListMap.entrySet()) {
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
		int wDatePeriodCount = pDatePeriods.length + 2 * (wDatePeriods.length - pDatePeriods.length);

		// 結果を挿入するリスト
		List<List<SummaryTableItem>> wSummaryTableItemList = new ArrayList<List<SummaryTableItem>>(wDatePeriodCount);
		for (int i = 0; i < wDatePeriodCount; i++) {
			List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();
			wSummaryTableItemList.add(wList);
		}

		DbAccess wDbAccess = new DbAccess();
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

		double wAppearedBalance = getInitialBalance(wDbAccess, SystemData.getAllBookInt());
		double wActualBalance = 0;
		double wTempBalance = 0;

		// カテゴリ集計
		// CategoryId-SummaryTableItemList

		wQuery = "select COALESCE(sum(case when " + mActDtCol + " < " + wTotalStartDateString + " then "
				+ mActIncomeCol + " - " + mActExpenseCol + " end),0) " + wAppearedBalanceName;
		wQuery += ",  + COALESCE(sum(case when " + mActDtCol + " < " + wTotalStartDateString + " and ("
				+ mCategoryTable + "." + mCategoryIdCol + " = " + mTempIncomeCategoryId + " or " + mCategoryTable + "."
				+ mCategoryIdCol + " = " + mTempExpenseCategoryId + ") then " + mActIncomeCol + " - " + mActExpenseCol
				+ " end),0) " + wTempBalanceName;
		for (int i = 0; i < wDatePeriods.length; i++) {
			Date[] wDates = wDatePeriods[i];
			String wStartDateString = getDateStrings(wDates[0]);
			String wEndDateString = getDateStrings(wDates[1]);
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString + " and "
					+ wEndDateString + " then " + mActIncomeCol + " end),0) " + wAppearedIncomeName + i;
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString + " and "
					+ wEndDateString + " then " + mActExpenseCol + " end),0) " + wAppearedExpenseName + i;

			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString + " and "
					+ wEndDateString + " and " + mCategoryTable + "." + mCategoryIdCol + " = "
					+ mSpecialIncomeCategoryId + " then " + mActIncomeCol + " end),0) " + wSpecialIncomeName + i;
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString + " and "
					+ wEndDateString + " and " + mCategoryTable + "." + mCategoryIdCol + " = "
					+ mSpecialExpenseCategoryId + " then " + mActExpenseCol + " end),0) " + wSpecialExpenseName + i;

			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString + " and "
					+ wEndDateString + " and " + mCategoryTable + "." + mCategoryIdCol + " = " + mTempIncomeCategoryId
					+ " then " + mActIncomeCol + " end),0) " + wTempIncomeName + i;
			wQuery += ", COALESCE(sum(case when " + mActDtCol + " between " + wStartDateString + " and "
					+ wEndDateString + " and " + mCategoryTable + "." + mCategoryIdCol + " = " + mTempExpenseCategoryId
					+ " then " + mActExpenseCol + " end),0) " + wTempExpenseName + i;
		}

		wQuery += " from " + mActTable + ", " + mItemTable + ", " + mCategoryTable;
		wQuery += " where " + mActTable + "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol;
		wQuery += " and " + mItemTable + "." + mCategoryIdCol + " = " + mCategoryTable + "." + mCategoryIdCol;
		wQuery += " and " + mActTable + "." + mDelFlgCol + " = b'0'";
		wQuery += " and " + mItemTable + "." + mMoveFlgCol + " = b'0'";
		// System.out.println(wQuery);

		wResultSet = wDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				wAppearedBalance += wResultSet.getDouble(wAppearedBalanceName);
				wTempBalance += wResultSet.getDouble(wTempBalanceName);
				wActualBalance += wAppearedBalance - wTempBalance;
				boolean isSummationInput = false;

				for (int i = 0; i < wDatePeriods.length; i++) {
					double wAppearedIncome = wResultSet.getDouble(wAppearedIncomeName + i);
					double wAppearedExpense = wResultSet.getDouble(wAppearedExpenseName + i);
					double wSpecialIncome = wResultSet.getDouble(wSpecialIncomeName + i);
					double wSpecialExpense = wResultSet.getDouble(wSpecialExpenseName + i);
					double wTempIncome = wResultSet.getDouble(wTempIncomeName + i);
					double wTempExpense = wResultSet.getDouble(wTempExpenseName + i);
					int wListIndex;
					if (isSummationInput) {
						wListIndex = i + 1;
					} else {
						wListIndex = i;
					}
					double wAppearedProfit = wAppearedIncome - wAppearedExpense;
					double wTempProfit = wTempIncome - wTempExpense;
					double wSpecialProfit = wSpecialIncome - wSpecialExpense;
					double wActualProfit = wAppearedProfit - wTempProfit;
					double wOperationProfit = wAppearedProfit - wTempProfit - wSpecialProfit;

					if (i == wSummationIndex) {
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("繰越残高", SystemData.getUndefinedInt()));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("繰越残高", SystemData.getUndefinedInt()));

						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("みかけ収支", wAppearedProfit));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("みかけ収支", wAppearedProfit / i));

						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("営業収支", wOperationProfit));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("営業収支", wOperationProfit / i));

						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("実質収支", wActualProfit));
						wSummaryTableItemList.get(wListIndex + 1).add(new SummaryTableItem("実質収支", wActualProfit / i));

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
								new SummaryTableItem("営業収入", (wAppearedIncome - wSpecialIncome - wTempIncome)));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("営業収入", (wAppearedIncome - wSpecialIncome - wTempIncome) / i));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("営業支出", (wAppearedExpense - wSpecialExpense - wTempExpense)));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("営業支出", (wAppearedExpense - wSpecialExpense - wTempExpense) / i));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("実質収入", wAppearedIncome - wTempIncome));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("実質収入", (wAppearedIncome - wTempIncome) / i));

						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("実質支出", wAppearedExpense - wTempExpense));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("実質支出", (wAppearedExpense - wTempExpense) / i));

						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("みかけ収入", wAppearedIncome));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("みかけ収入", wAppearedIncome / i));

						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("みかけ支出", wAppearedExpense));
						wSummaryTableItemList.get(wListIndex + 1).add(
								new SummaryTableItem("みかけ支出", wAppearedExpense / i));

						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("特別収入", wSpecialIncome));
						wSummaryTableItemList.get(wListIndex + 1).add(new SummaryTableItem("特別収入", wSpecialIncome / i));

						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("特別支出", wSpecialExpense));
						wSummaryTableItemList.get(wListIndex + 1)
								.add(new SummaryTableItem("特別支出", wSpecialExpense / i));

						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("立替収入", wTempIncome));
						wSummaryTableItemList.get(wListIndex + 1).add(new SummaryTableItem("立替収入", wTempIncome / i));

						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("立替支出", wTempExpense));
						wSummaryTableItemList.get(wListIndex + 1).add(new SummaryTableItem("立替支出", wTempExpense / i));

						isSummationInput = true;

					} else { // SummationIndex以外
						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("繰越残高", wAppearedBalance));

						wAppearedBalance += wAppearedProfit;
						wActualBalance += wActualProfit;
						wTempBalance += wTempProfit;

						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("みかけ収支", wAppearedProfit));
						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("営業収支", wOperationProfit));
						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("実質収支", wActualProfit));
						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("実質残高", wActualBalance));
						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("みかけ残高", wAppearedBalance));
						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("立替累計", wTempBalance));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("営業収入", wAppearedIncome - wSpecialIncome - wTempIncome));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("営業支出", wAppearedExpense - wSpecialExpense - wTempExpense));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("実質収入", wAppearedIncome - wTempIncome));
						wSummaryTableItemList.get(wListIndex).add(
								new SummaryTableItem("実質支出", wAppearedExpense - wTempExpense));
						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("みかけ収入", wAppearedIncome));
						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("みかけ支出", wAppearedExpense));
						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("特別収入", wSpecialIncome));
						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("特別支出", wSpecialExpense));
						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("立替収入", wTempIncome));
						wSummaryTableItemList.get(wListIndex).add(new SummaryTableItem("立替支出", wTempExpense));

					}
				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		wDbAccess.closeConnection();

		List<SummaryTableItem[]> wReturnList = new ArrayList<SummaryTableItem[]>(wSummaryTableItemList.size());
		for (List<SummaryTableItem> wList : wSummaryTableItemList) {
			wReturnList.add((SummaryTableItem[]) wList.toArray(new SummaryTableItem[0]));
		}
		return wReturnList;
	}

	public static ConfigItem getRootConfigItem() {
		DbAccess wDbAccess = new DbAccess();
		ConfigItem wRootItem = new ConfigItem("");
		wRootItem.addItem(getEachConfigItem(true, wDbAccess));
		wRootItem.addItem(getEachConfigItem(false, wDbAccess));
		wDbAccess.closeConnection();

		return wRootItem;
	}

	public static ConfigItem getEachConfigItem(boolean pIsIncome, DbAccess pDbAccess) {

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
		String wQuery = "select " + mCategoryIdCol + ", " + mCategoryNameCol + " from " + mCategoryTable;
		wQuery += " where " + mDelFlgCol + " = b'0' and " + mCategoryRexpCol + " = " + wRexp;
		wQuery += " and " + mSortKeyCol + " > 0";
		wQuery += " order by " + mSortKeyCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = pDbAccess.executeQuery(wQuery);

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

		wQuery = "select " + mCategoryIdCol + ", " + mItemIdCol + ", " + mItemIdCol + ", " + mItemNameCol;
		wQuery += " from " + mItemTable;
		wQuery += " where " + mDelFlgCol + " = b'0' and " + mMoveFlgCol + " = b'0'";
		wQuery += " order by " + mSortKeyCol;

		// System.out.println(wQuery);
		wResultSet = pDbAccess.executeQuery(wQuery);

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

		DbAccess wDbAccess = new DbAccess();

		List<ConfigItem> wConfigItemList = new ArrayList<ConfigItem>();
		wConfigItemList.add(pConfigItem);

		while (wConfigItemList.size() > 0) {

			ConfigItem wCurrentItem = wConfigItemList.get(0);
			wConfigItemList.remove(0);

			if (!wCurrentItem.isSpecial()) {
				// 自身のアップデート

				if (wCurrentItem.isCategory()) {
					wQuery = "update " + mCategoryTable + " set " + mSortKeyCol + " = " + wSortKeyCategory + " where "
							+ mCategoryIdCol + " = " + wCurrentItem.getId();
					wSortKeyCategory++;
				} else {
					wQuery = "update " + mItemTable + " set " + mSortKeyCol + " = " + wSortKeyItem + " where "
							+ mItemIdCol + " = " + wCurrentItem.getId();
					wSortKeyItem++;
				}
				wDbAccess.executeUpdate(wQuery);
				// System.out.println(wQuery);
			}
			if (wCurrentItem.hasItem()) {
				// 子リストの追加
				for (ConfigItem wChildItem : wCurrentItem.getChildren()) {
					wConfigItemList.add(wChildItem);
				}
			}

		}

		wDbAccess.closeConnection();

	}

	public static void insertNewCategory(boolean isIncome, String pCategoryName) {
		DbAccess wDbAccess = new DbAccess();
		String wQuery = "insert into " + mCategoryTable + " (" + mCategoryRexpCol + ", " + mCategoryNameCol + ", "
				+ mSortKeyCol + ") values (";
		wQuery += (isIncome) ? mIncomeRexp : mExpenseRexp;
		wQuery += ", '" + pCategoryName + "', " + 9999 + ")";
		// System.out.println(wQuery);
		wDbAccess.executeUpdate(wQuery);
	}

	public static void insertNewItem(int pCategoryId, String pItemName) {
		DbAccess wDbAccess = new DbAccess();
		String wQuery = "insert into " + mItemTable + " (" + mCategoryIdCol + ", " + mItemNameCol + ", " + mSortKeyCol
				+ ") values (";
		wQuery += pCategoryId + ", '" + pItemName + "', " + 9999 + ")";
		// System.out.println(wQuery);
		wDbAccess.executeUpdate(wQuery);
	}

	public static void updateCategory(int pCategoryId, String pCategoryName) {
		DbAccess wDbAccess = new DbAccess();
		String wQuery = "update " + mCategoryTable + " set " + mCategoryNameCol + " = '" + pCategoryName + "' where "
				+ mCategoryIdCol + " = " + pCategoryId;
		// System.out.println(wQuery);
		wDbAccess.executeUpdate(wQuery);
	}

	public static void updateItem(int pCategoryId, int pItemId, String pItemName) {
		DbAccess wDbAccess = new DbAccess();
		String wQuery = "update " + mItemTable + " set " + mCategoryIdCol + " = " + pCategoryId + ", " + mItemNameCol
				+ " = '" + pItemName + "' " + " where " + mItemIdCol + " = " + pItemId;
		// System.out.println(wQuery);
		wDbAccess.executeUpdate(wQuery);
	}

	public static void deleteCategoryItem(ConfigItem pConfigItem) {
		DbAccess wDbAccess = new DbAccess();
		String wTableName = (pConfigItem.isCategory()) ? mCategoryTable : mItemTable;
		String wIdName = (pConfigItem.isCategory()) ? mCategoryIdCol : mItemIdCol;

		String wQuery = "update " + wTableName + " set " + mDelFlgCol + " = b'1' where " + wIdName + " = "
				+ pConfigItem.getId();
		// System.out.println(wQuery);
		wDbAccess.executeUpdate(wQuery);
	}

	public static List<Integer> getRelatedBookIdList(ConfigItem pConfigItem) {
		List<Integer> wList = new ArrayList<Integer>();
		DbAccess wDbAccess = new DbAccess();

		String wQuery = "select " + mBookIdCol + " from " + mBookItemTable + " where " + mItemIdCol + " = "
				+ pConfigItem.getId();
		ResultSet wResultSet = wDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				wList.add(wResultSet.getInt(mBookIdCol));
			}
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
			wDbAccess.closeConnection();
		}

		return wList;
	}

	public static void updateItemRelation(int pItemId, int pBookId, boolean isSelected) {
		DbAccess wDbAccess = new DbAccess();
		String wQuery;

		if (!isSelected) {
			// 削除
			wQuery = "delete from " + mBookItemTable + " where " + mItemIdCol + " = " + pItemId + " and " + mBookIdCol
					+ " = " + pBookId;
		} else {
			// 追加
			wQuery = "insert into " + mBookItemTable + " (" + mItemIdCol + ", " + mBookIdCol + ") values (" + pItemId
					+ ", " + pBookId + ")";
		}
		
		wDbAccess.executeUpdate(wQuery);
	}

	// 立替残高（借入残高）
	private static double getTempBalance(DbAccess pDbAccess, Date pEndDate) {

		double wTempBalance = 0;
		String wResultColName = "ResultCol";
		String wEndDateString = getDateStrings(pEndDate);

		String wQuery = "select sum(" + mActIncomeCol + " - " + mActExpenseCol + ") as " + wResultColName + " from "
				+ mActTable + ", " + mItemTable + ", " + mCategoryTable + " where " + mActTable + "." + mItemIdCol
				+ " = " + mItemTable + "." + mItemIdCol + " and " + mItemTable + "." + mCategoryIdCol + " = "
				+ mCategoryTable + "." + mCategoryIdCol + " and " + mActTable + "." + mDelFlgCol + " = b'0' and "
				+ mActDtCol + " <= " + wEndDateString + " and (" + mCategoryTable + "." + mCategoryIdCol + " = "
				+ mTempIncomeCategoryId + " or " + mCategoryTable + "." + mCategoryIdCol + " = "
				+ mTempExpenseCategoryId + ")";

		// System.out.println(wQuery);
		ResultSet wResultSet = pDbAccess.executeQuery(wQuery);

		try {
			if (wResultSet.next()) {
				wTempBalance = wResultSet.getDouble(wResultColName);
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wTempBalance;
	}

	private static double getTotalTempProfit(DbAccess pDbAccess, Date pStartDate, Date pEndDate) {
		// 立替収支
		double wTotalTempProfit = 0;

		String wResultColName = "ResultCol";
		String wStartDateString = getDateStrings(pStartDate);
		String wEndDateString = getDateStrings(pEndDate);

		String wQuery = "select sum(" + mActIncomeCol + " - " + mActExpenseCol + ") as " + wResultColName + " from "
				+ mActTable + ", " + mItemTable + ", " + mCategoryTable + " where " + mActTable + "." + mItemIdCol
				+ " = " + mItemTable + "." + mItemIdCol + " and " + mItemTable + "." + mCategoryIdCol + " = "
				+ mCategoryTable + "." + mCategoryIdCol + " and " + mActTable + "." + mDelFlgCol + " = b'0' and "
				+ mActDtCol + " between " + wStartDateString + " and " + wEndDateString + " and (" + mCategoryTable
				+ "." + mCategoryIdCol + " = " + mTempIncomeCategoryId + " or " + mCategoryTable + "." + mCategoryIdCol
				+ " = " + mTempExpenseCategoryId + ")";

		// System.out.println(wQuery);
		ResultSet wResultSet = pDbAccess.executeQuery(wQuery);

		try {
			if (wResultSet.next()) {
				wTotalTempProfit = wResultSet.getDouble(wResultColName);
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wTotalTempProfit;
	}

	private static double getTotalSpecialProfit(DbAccess pDbAccess, Date pStartDate, Date pEndDate) {
		// 特別収支
		double wSpecialProfit = 0;

		String wResultColName = "ResultCol";
		String wStartDateString = getDateStrings(pStartDate);
		String wEndDateString = getDateStrings(pEndDate);

		String wQuery = "select sum(" + mActIncomeCol + " - " + mActExpenseCol + ") as " + wResultColName + " from "
				+ mActTable + ", " + mItemTable + ", " + mCategoryTable + " where " + mActTable + "." + mItemIdCol
				+ " = " + mItemTable + "." + mItemIdCol + " and " + mItemTable + "." + mCategoryIdCol + " = "
				+ mCategoryTable + "." + mCategoryIdCol + " and " + mActTable + "." + mDelFlgCol + " = b'0' and "
				+ mActDtCol + " between " + wStartDateString + " and " + wEndDateString + " and (" + mCategoryTable
				+ "." + mCategoryIdCol + " = " + mSpecialIncomeCategoryId + " or " + mCategoryTable + "."
				+ mCategoryIdCol + " = " + mSpecialExpenseCategoryId + ")";

		// System.out.println(wQuery);
		ResultSet wResultSet = pDbAccess.executeQuery(wQuery);

		try {
			if (wResultSet.next()) {
				wSpecialProfit = wResultSet.getDouble(wResultColName);
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wSpecialProfit;
	}

	private static double[] getAppearedIncomeExpense(DbAccess pDbAccess, Date pStartDate, Date pEndDate, int pBookId) {
		// みかけ収入・支出（各Book）
		double[] wAppearedIncomeExpense = new double[2];

		String wStartDateString = getDateStrings(pStartDate);
		String wEndDateString = getDateStrings(pEndDate);

		String wQuery = "select sum(" + mActIncomeCol + ") as " + mActIncomeCol + ", sum(" + mActExpenseCol + ") as "
				+ mActExpenseCol + " from " + mActTable + ", " + mItemTable + ", " + mCategoryTable + " where "
				+ mActTable + "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol + " and " + mItemTable + "."
				+ mCategoryIdCol + " = " + mCategoryTable + "." + mCategoryIdCol + " and " + mActTable + "."
				+ mDelFlgCol + " = b'0' and " + mActDtCol + " between " + wStartDateString + " and " + wEndDateString;

		if (pBookId == mAllBookId) {
			// Moveを除く
			wQuery += " and " + mItemTable + "." + mMoveFlgCol + " = b'0'";

		} else {
			// BookIdの条件を追加
			wQuery += " and " + mBookIdCol + " = " + pBookId;
		}

		// System.out.println(wQuery);
		ResultSet wResultSet = pDbAccess.executeQuery(wQuery);

		try {
			if (wResultSet.next()) {
				wAppearedIncomeExpense[0] = wResultSet.getDouble(mActIncomeCol);
				wAppearedIncomeExpense[1] = wResultSet.getDouble(mActExpenseCol);
				// System.out.println(wTempBalanceName + " = " +
				// wTempBalance);
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wAppearedIncomeExpense;
	}

	private static double getBalance(DbAccess pDbAccess, Date pEndDate, int pBookId, boolean pIncludeEndDate) {

		double wBalance = getInitialBalance(pDbAccess, pBookId);

		String wEnd = getDateStrings(pEndDate);
		String wBookWhere = getBookWhere(pBookId);
		String wResultCol = "Value";

		String wQuery = "select SUM(" + mActIncomeCol + " - " + mActExpenseCol + ") as " + wResultCol + " from "
				+ mActTable + " where " + mDelFlgCol + " = b'0' " + " and " + wBookWhere + " and " + mActDtCol;

		if (pIncludeEndDate) {
			wQuery += " <= " + wEnd;
		} else {
			wQuery += " < " + wEnd;
		}

		// System.out.println(wQuery);

		ResultSet wResultSet = pDbAccess.executeQuery(wQuery);

		try {
			wResultSet.next();
			wBalance += wResultSet.getDouble(wResultCol);
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wBalance;
	}

	private static double getInitialBalance(DbAccess pDbAccess, int pBookId) {
		double wBalance = 0;
		String wBookWhere = getBookWhere(pBookId);
		String wResultCol = "VALUE";

		// DbAccess wDbAccess = new DbAccess();
		String wQuery = "";

		if (pBookId == mAllBookId) {
			wQuery = "select SUM(" + mBookBalanceCol + ") as " + wResultCol + " from " + mBookTable + " where "
					+ mDelFlgCol + " = b'0' and " + wBookWhere;

		} else {
			wQuery = "select " + mBookBalanceCol + " as " + wResultCol + " from " + mBookTable + " where " + wBookWhere;
		}

		// System.out.println(wQuery);
		ResultSet wResultSet = pDbAccess.executeQuery(wQuery);

		try {
			wResultSet.next();
			wBalance += wResultSet.getDouble(wResultCol);
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

	private static int getNewGroupId(DbAccess pDbAccess) {
		int wRet = 0;
		String wCol = "MaximumGroupId";

		ResultSet wResultSet = pDbAccess.executeQuery("select max(" + mGroupIdCol + ") as " + wCol + " from "
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

	private static void updateNoteTable(DbAccess pDbAccess, int pItemId, String pNote) {
		String wQuery = "delete from " + mNoteTable + " where " + mItemIdCol + " = " + pItemId + " and " + mNoteNameCol
				+ " = '" + pNote + "'";
		pDbAccess.executeUpdate(wQuery);

		wQuery = "insert into  " + mNoteTable + " (" + mNoteNameCol + "," + mItemIdCol + ") values('" + pNote + "',"
				+ pItemId + ")";
		pDbAccess.executeUpdate(wQuery);
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

	private static boolean isSingleMoveRecordPair(DbAccess pDbAccess, int pGroupId) {
		String wResultCol = "COUNT";
		String wQuery = "select count(" + mActIdCol + ") as " + wResultCol + " from " + mActTable + " where "
				+ mGroupIdCol + " = " + pGroupId;
		// System.out.println(wQuery);
		ResultSet wResultSet = pDbAccess.executeQuery(wQuery);

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
		MessageDialog.openWarning(Display.getCurrent().getShells()[0], "SQL ResultSet Handling Error", e.toString()
				+ "\n\n" + wStack);
		// System.err.println("ResultSet Handling Error: " + e.toString());
	}

	private static String getNoteStringWithEscape(String pNote) {
		for (String wString : mSpecialChars) {
			pNote = pNote.replace(wString, mEscapeChar + wString);
		}
		return pNote;
	}

	// public static String getCategoryNameById(int pCategoryId) {
	// DbAccess wDbAccess = new DbAccess();
	// ResultSet wResultSet = wDbAccess.executeQuery("select "
	// + mCategoryNameCol + " from " + mCategoryTable + " where "
	// + mCategoryIdCol + " = " + pCategoryId);
	//
	// String wCategoryName = "";
	//
	// try {
	// wResultSet.next();
	// wCategoryName = wResultSet.getString(mCategoryNameCol);
	//
	// wResultSet.close();
	// } catch (SQLException e) {
	// resultSetHandlingError(e);
	// } finally {
	// wDbAccess.closeConnection();
	// }
	// return wCategoryName;
	//
	// }
	//
	// public static String getCategoryNameByItemId(int pItemId) {
	// DbAccess wDbAccess = new DbAccess();
	// ResultSet wResultSet = wDbAccess.executeQuery("select "
	// + mCategoryTable + "." + mCategoryNameCol + " from "
	// + mCategoryTable + " inner join " + mItemTable + " on "
	// + mCategoryTable + "." + mCategoryIdCol + " = " + mItemTable
	// + "." + mCategoryIdCol + " where " + mItemTable + "."
	// + mItemIdCol + " = " + pItemId);
	//
	// String wCategoryName = "";
	//
	// try {
	// wResultSet.next();
	// wCategoryName = wResultSet.getString(mCategoryNameCol);
	// wResultSet.close();
	//
	// } catch (SQLException e) {
	// resultSetHandlingError(e);
	// } finally {
	// wDbAccess.closeConnection();
	// }
	//
	// return wCategoryName;
	// }
	//	
	// public static RecordTableItem[] getRecordTableItems(Date pDate,
	// int pBookId, boolean pUp) {
	// Date[] wDates = Util.getPeriod(pDate);
	// return getRecordTableItems(wDates[0], wDates[1], pBookId, pUp);
	// }
	// public static String getThisMonth(Date pDate) {
	// Date[] wDates = getPeriod(pDate);
	//
	// DateFormat df = new SimpleDateFormat("yyyy/MM");
	// return df.format(wDates[1]);
	// }

	// public static void main(String[] args) {
	// ConfigItem[] wConfigItems = getConfigItems(true);
	// for (ConfigItem c : wConfigItems) {
	// System.out.println(c);
	// if (c.hasItem()) {
	// for (ConfigItem ci : c.getItems()) {
	// System.out.println("  " + ci);
	// }
	// }
	// }
	// }

}
