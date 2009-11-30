package model.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;

import model.AnnualHeaderItem;
import model.RecordTableItem;
import model.SummaryTableItem;
import model.SystemData;

public class DbUtil {

	// Systemテーブル関連
	private final static String mSystemTable = "SYSTEM";
	private final static String mSystemValueCol = "NUM_VALUE";
	private final static String mSystemIDCol = "SID";
	private final static String mCutOff = "CUTOFF_DT";
	private final static String mFiscalMonth = "FISCAL_MH";

	// Categoryテーブル関連
	private final static String mCategoryTable = "CBM_CATEGORY";
	private final static String mCategoryNameCol = "CATEGORY_NAME";
	private final static String mCategoryIdCol = "CATEGORY_ID";
	private final static String mCategoryRexpCol = "REXP_DIV"; // 1: Income, 2:
	private final static int mIncomeRexp = 1; // REXP_DIVの値（Income）
	private final static int mExpneseRexp = 2; // REXP_DIVの値（Expense）

	// BookとItemの関連テーブル
	private final static String mBookItemTable = "CBR_BOOK";

	// Itemテーブル関連
	private final static String mItemTable = "CBM_ITEM";
	private final static String mItemNameCol = "ITEM_NAME";
	private final static String mItemIdCol = "ITEM_ID";

	// ACTアイテム関連
	private final static String mActTable = "CBT_ACT";
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
	private final static String mBookTable = "CBM_BOOK";
	private final static String mBookIdCol = "BOOK_ID";
	private final static String mBookNameCol = "BOOK_NAME";
	private final static String mBookBalanceCol = "BALANCE";

	// Noteテーブル関連
	private final static String mNoteTable = "CBT_NOTE";
	private final static String mNoteIdCol = "NOTE_ID";
	private final static String mNoteNameCol = "NOTE_NAME";

	private final static int mAllBookId = SystemData.getAllBookInt();

	private final static int mSpecialIncomeCategoryId = 23;
	private final static int mSpecialExpenseCategoryId = 44;
	private final static int mTempIncomeCategoryId = 60;
	private final static int mTempExpenseCategoryId = 61;

	private DbUtil() {

	}

	public static int getCutOff() {
		int wCutOff = -1;

		DbAccess wDbAccess = new DbAccess();
		ResultSet wResultSet = wDbAccess.executeQuery("select "
				+ mSystemValueCol + " from " + mSystemTable + " where "
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
		ResultSet wResultSet = wDbAccess.executeQuery("select "
				+ mSystemValueCol + " from " + mSystemTable + " where "
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
		ResultSet wResultSet = wDbAccess.executeQuery("select "
				+ mCategoryIdCol + " from " + mItemTable + " where "
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
		ResultSet wResultSet = wDbAccess.executeQuery("select " + mItemNameCol
				+ " from " + mItemTable + " where " + mItemIdCol + " = "
				+ pItemId);

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

	public static RecordTableItem[][] getRecordTableItems(Date pStartDate,
			Date pEndDate, int pBookId) {
		List<RecordTableItem> wRecordTableItemListUp = new ArrayList<RecordTableItem>();
		List<RecordTableItem> wRecordTableItemListBottom = new ArrayList<RecordTableItem>();

		String wStart = getDateStrings(pStartDate);
		String wEnd = getDateStrings(pEndDate);

		String wBookWhere = getBookWhere(pBookId);

		String wMoveFlgWhere = "";
		if (pBookId == mAllBookId) {
			wMoveFlgWhere = " and " + mItemTable + "." + mMoveFlgCol
					+ " = b'0'";
		}

		DbAccess wDbAccess = new DbAccess();

		double wBalance = getBalance(wDbAccess, pStartDate, pBookId, false);
		RecordTableItem wBalanceRecord = new RecordTableItem(pStartDate,
				wBalance);
		wRecordTableItemListUp.add(wBalanceRecord);

		String wQuery = "select " + mActIdCol + ", " + mBookIdCol + ", "
				+ mActDtCol + ", " + mActTable + "." + mItemIdCol + ", "
				+ mItemTable + "." + mItemNameCol + ", " + mItemTable + "."
				+ mCategoryIdCol + ", " + mGroupIdCol + ", " + mActIncomeCol
				+ ", " + mActExpenseCol + ", " + mActFreqCol + ", "
				+ mNoteNameCol + " from " + mActTable + ", " + mItemTable
				+ ", " + mCategoryTable + " where " + mItemTable + "."
				+ mItemIdCol + " = " + mActTable + "." + mItemIdCol + " and "
				+ mItemTable + "." + mCategoryIdCol + " = " + mCategoryTable
				+ "." + mCategoryIdCol + " and " + mActDtCol + " between "
				+ wStart + " and " + wEnd + " and " + mActTable + "."
				+ mDelFlgCol + " = b'0' " + wMoveFlgWhere + " and "
				+ wBookWhere + " order by " + mActDtCol + ", " + mCategoryTable
				+ "." + mCategoryRexpCol + ", " + mCategoryTable + "."
				+ mSortKeyCol + ", " + mItemTable + "." + mSortKeyCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = wDbAccess.executeQuery(wQuery);

		try {
			Date wDateNow = new Date();
			while (wResultSet.next()) {

				int wId = wResultSet.getInt(mActIdCol);
				int wBookId = wResultSet.getInt(mBookIdCol);
				Date wDate = wResultSet.getDate(mActDtCol);
				int wItemId = wResultSet.getInt(mItemIdCol);
				String wItemName = wResultSet.getString(mItemTable + "."
						+ mItemNameCol);
				int wCategoryId = wResultSet.getInt(mItemTable + "."
						+ mCategoryIdCol);
				int wGroupId = wResultSet.getInt(mGroupIdCol);
				double wIncome = wResultSet.getDouble(mActIncomeCol);
				double wExpense = wResultSet.getDouble(mActExpenseCol);
				wBalance += wIncome - wExpense;
				int wFrequency = wResultSet.getInt(mActFreqCol);
				String wNote = wResultSet.getString(mNoteNameCol);
				RecordTableItem wRecord = new RecordTableItem(wId, wBookId,
						wDate, wItemId, wItemName, wCategoryId, wGroupId,
						wIncome, wExpense, wBalance, wFrequency, wNote);
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
		wRet[0] = (RecordTableItem[]) wRecordTableItemListUp
				.toArray(new RecordTableItem[0]);
		wRet[1] = (RecordTableItem[]) wRecordTableItemListBottom
				.toArray(new RecordTableItem[0]);

		return wRet;

	}

	public static Map<Integer, String> getBookNameMap() {
		Map<Integer, String> wBookMap = new LinkedHashMap<Integer, String>();

		DbAccess wDbAccess = new DbAccess();
		String wQuery = "select " + mBookIdCol + ", " + mBookNameCol + " from "
				+ mBookTable + " where " + mDelFlgCol + " = b'0' "
				+ " order by " + mSortKeyCol;

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

		String wQuery = "select " + mActIdCol + ", " + mBookIdCol + ", "
				+ mActDtCol + ", " + mActTable + "." + mItemIdCol + ", "
				+ mItemTable + "." + mItemNameCol + ", " + mItemTable + "."
				+ mCategoryIdCol + ", " + mGroupIdCol + ", " + mActIncomeCol
				+ ", " + mActExpenseCol + ", " + mActFreqCol + ", "
				+ mNoteNameCol + " from " + mActTable + ", " + mItemTable
				+ " where " + mActIdCol + " = " + pId + " and " + mActTable
				+ "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = pDbAccess.executeQuery(wQuery);

		try {
			wResultSet.next();

			int wId = wResultSet.getInt(mActIdCol);
			int wBookId = wResultSet.getInt(mBookIdCol);
			Date wDate = wResultSet.getDate(mActDtCol);
			int wItemId = wResultSet.getInt(mItemIdCol);
			String wItemName = wResultSet.getString(mItemTable + "."
					+ mItemNameCol);
			int wCategoryId = wResultSet.getInt(mItemTable + "."
					+ mCategoryIdCol);
			int wGroupId = wResultSet.getInt(mGroupIdCol);
			double wIncome = wResultSet.getDouble(mActIncomeCol);
			double wExpense = wResultSet.getDouble(mActExpenseCol);
			int wFrequency = wResultSet.getInt(mActFreqCol);
			String wNote = wResultSet.getString(mNoteNameCol);
			wRecordTableItem = new RecordTableItem(wId, wBookId, wDate,
					wItemId, wItemName, wCategoryId, wGroupId, wIncome,
					wExpense, 0, wFrequency, wNote);
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
		String wQuery = "select " + mNoteNameCol + " from " + mNoteTable
				+ " where " + mItemIdCol + " = " + pItemId + " and "
				+ mDelFlgCol + " = b'0' " + " order by " + mNoteIdCol
				+ " desc ";

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
	public static Map<Integer, String> getItemNameMap(int pBookId,
			boolean pIncome) {
		Map<Integer, String> wResultMap = new LinkedHashMap<Integer, String>();
		int wRexp = mIncomeRexp;
		if (!pIncome) {
			wRexp = mExpneseRexp;
		}

		DbAccess wDbAccess = new DbAccess();
		String wQuery = "select " + mItemTable + "." + mItemIdCol + ", "
				+ mItemTable + "." + mItemNameCol;
		wQuery += " from " + mItemTable + " , " + mBookItemTable + ", "
				+ mCategoryTable;
		wQuery += " where " + mItemTable + "." + mItemIdCol + " = "
				+ mBookItemTable + "." + mItemIdCol + " and " + mCategoryTable
				+ "." + mCategoryIdCol + " = " + mItemTable + "."
				+ mCategoryIdCol + " and " + mBookItemTable + "." + mBookIdCol
				+ " = " + pBookId + " and " + mCategoryTable + "."
				+ mCategoryRexpCol + " = " + wRexp + " and " + mBookItemTable
				+ "." + mDelFlgCol + " = b'0' " + " and " + mItemTable + "."
				+ mDelFlgCol + " = b'0' ";
		wQuery += " order by " + mItemTable + "." + mSortKeyCol;
		// System.out.println(wQuery);

		ResultSet wResultSet = wDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				wResultMap.put(wResultSet.getInt(mItemIdCol), wResultSet
						.getString(mItemNameCol));
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
	public static Map<Integer, String> getItemNameMap(int pBookId,
			int pCategoryId) {
		Map<Integer, String> wResultMap = new LinkedHashMap<Integer, String>();

		DbAccess wDbAccess = new DbAccess();
		String wQuery = "select " + mItemTable + "." + mItemIdCol + ", "
				+ mItemTable + "." + mItemNameCol;
		wQuery += " from " + mItemTable + " , " + mBookItemTable + ", "
				+ mCategoryTable;
		wQuery += " where " + mItemTable + "." + mItemIdCol + " = "
				+ mBookItemTable + "." + mItemIdCol + " and " + mCategoryTable
				+ "." + mCategoryIdCol + " = " + mItemTable + "."
				+ mCategoryIdCol + " and " + mBookItemTable + "." + mBookIdCol
				+ " = " + pBookId + " and " + mCategoryTable + "."
				+ mCategoryIdCol + " = " + pCategoryId + " and "
				+ mBookItemTable + "." + mDelFlgCol + " = b'0' " + " and "
				+ mItemTable + "." + mDelFlgCol + " = b'0' ";
		wQuery += " order by " + mItemTable + "." + mSortKeyCol;
		// System.out.println(wQuery);

		ResultSet wResultSet = wDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				wResultMap.put(wResultSet.getInt(mItemIdCol), wResultSet
						.getString(mItemNameCol));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
			wDbAccess.closeConnection();
		}

		return wResultMap;
	}

	public static Map<Integer, String> getCategoryNameMap(int pBookId,
			boolean pIncome) {
		Map<Integer, String> wResultMap = new LinkedHashMap<Integer, String>();
		int wRexp = mIncomeRexp;
		if (!pIncome) {
			wRexp = mExpneseRexp;
		}

		DbAccess wDbAccess = new DbAccess();
		String wQuery = "select count( " + mItemTable + "." + mItemNameCol
				+ " ), " + mCategoryTable + "." + mCategoryIdCol + ", "
				+ mCategoryTable + "." + mCategoryNameCol;
		wQuery += " from " + mItemTable + ", " + mBookItemTable + ", "
				+ mCategoryTable;
		wQuery += " where " + mItemTable + "." + mItemIdCol + " = "
				+ mBookItemTable + "." + mItemIdCol + " and " + mCategoryTable
				+ "." + mCategoryIdCol + " = " + mItemTable + "."
				+ mCategoryIdCol + " and " + mBookItemTable + "." + mBookIdCol
				+ " = " + pBookId + " and " + mCategoryTable + "."
				+ mCategoryRexpCol + " = " + wRexp + " and " + mBookItemTable
				+ "." + mDelFlgCol + " = b'0' " + " and " + mItemTable + "."
				+ mDelFlgCol + " = b'0' ";
		wQuery += " group by " + mCategoryTable + "." + mCategoryNameCol;
		wQuery += " order by " + mCategoryTable + "." + mSortKeyCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = wDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				wResultMap.put(wResultSet.getInt(mCategoryIdCol), wResultSet
						.getString(mCategoryNameCol));
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
		wQuery += " where " + mCategoryIdCol + " = " + pCategoryId + " and "
				+ mDelFlgCol + " = b'0' ";

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

	public static void insertNewRecord(int pBookId, int pItemId, int pYear,
			int pMonth, int pDay, int pIncome, int pExpense, int pFrequency,
			String pNote) {

		DbAccess wDbAccess = new DbAccess();
		String wQueryBase1 = "insert into  " + mActTable + " ( " + mBookIdCol
				+ "," + mItemIdCol + "," + mActDtCol + "," + mActIncomeCol
				+ "," + mActExpenseCol;
		String wQueryBase2 = " values(" + pBookId + "," + pItemId + ",'"
				+ pYear + "-" + pMonth + "-" + pDay + "'," + pIncome + ","
				+ pExpense;
		String wQueryNote1 = "";
		String wQueryNote2 = "";
		String wQuery = "";

		if (!"".equals(pNote)) {
			wQueryNote1 = "," + mNoteNameCol;
			wQueryNote2 = ",'" + pNote + "'";
		}

		if (pFrequency == 0) {
			wQuery = wQueryBase1 + wQueryNote1 + ") " + wQueryBase2
					+ wQueryNote2 + ")";
			// System.out.println(wQuery);
			wDbAccess.executeUpdate(wQuery);

		} else if (pFrequency > 0) {
			int wGroupId = getNewGroupId(wDbAccess);
			Calendar wCalBase = new GregorianCalendar(pYear, pMonth - 1, pDay);
			for (int i = 0; i < pFrequency + 1; i++) {
				Calendar wCal = (Calendar) wCalBase.clone();
				wCal.add(Calendar.MONTH, +i);
				String wDate = getDateStrings(wCal.getTime());

				wQueryBase1 = "insert into  " + mActTable + " ( " + mBookIdCol
						+ "," + mItemIdCol + "," + mActDtCol + ","
						+ mActIncomeCol + "," + mActExpenseCol + ","
						+ mGroupIdCol + "," + mActFreqCol;

				wQueryBase2 = " values(" + pBookId + "," + pItemId + ","
						+ wDate + "," + pIncome + "," + pExpense + ","
						+ wGroupId + "," + (pFrequency - i);

				wQuery = wQueryBase1 + wQueryNote1 + ") " + wQueryBase2
						+ wQueryNote2 + ")";
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

	public static void updateRecord(int pActId, int pBookId, int pItemId,
			int pYear, int pMonth, int pDay, int pIncome, int pExpense,
			int pFrequency, String pNote) {

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
			wQuery = "update " + mActTable + " set " + mBookIdCol + " = "
					+ pBookId + ", " + mItemIdCol + " = " + pItemId + ", "
					+ mActDtCol + " = " + wDate + ", " + mActIncomeCol + " = "
					+ pIncome + ", " + mActExpenseCol + " = " + pExpense + ", "
					+ mNoteNameCol + " = '" + pNote + "' ";
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
			if (pYear != wOldCal.get(Calendar.YEAR)
					|| pMonth != wOldCal.get(Calendar.MONTH) + 1
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

				String wQueryBase = "insert into  " + mActTable + " ( "
						+ mBookIdCol + "," + mItemIdCol + "," + mActDtCol + ","
						+ mActIncomeCol + "," + mActExpenseCol + ","
						+ mGroupIdCol + "," + mActFreqCol;

				String wQueryValues = " values(" + pBookId + "," + pItemId
						+ "," + wDate + "," + pIncome + "," + pExpense + ","
						+ wGroupId + "," + (pFrequency - i);

				if (!"".equals(pNote)) {
					wQuery = wQueryBase + "," + mNoteNameCol + ")"
							+ wQueryValues + ",'" + pNote + "')";
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

	public static void insertNewMoveRecord(int pBookFromId, int pBookToId,
			int pYear, int pMonth, int pDay, int pValue, int pFrequency,
			String pNote) {

		DbAccess wDbAccess = new DbAccess();
		int wGroupId = getNewGroupId(wDbAccess);

		String wQueryBase = "insert into  " + mActTable + " ( " + mBookIdCol
				+ "," + mItemIdCol + "," + mActIncomeCol + "," + mActExpenseCol
				+ "," + mGroupIdCol + "," + mActDtCol;
		String wQueryFromValues = " values(" + pBookFromId + ","
				+ getMoveExpenseItemId() + ",'0'," + pValue + "," + wGroupId;
		String wQueryToValues = " values(" + pBookToId + ","
				+ getMoveIncomeItemId() + "," + pValue + ",'0'," + wGroupId;

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
			wQueryFrom = wQueryBase + wQueryNote1 + ") " + wQueryFromValues
					+ wDate + wQueryNote2 + ")";
			// System.out.println(wQueryFrom);
			wDbAccess.executeUpdate(wQueryFrom);
			wQueryTo = wQueryBase + wQueryNote1 + ") " + wQueryToValues + wDate
					+ wQueryNote2 + ")";
			// System.out.println(wQueryTo);
			wDbAccess.executeUpdate(wQueryTo);
		} else { // pFrequency > 0

			Calendar wCalBase = new GregorianCalendar(pYear, pMonth - 1, pDay);
			String wQueryFreq = "," + mActFreqCol;
			for (int i = 0; i < pFrequency + 1; i++) {
				Calendar wCal = (Calendar) wCalBase.clone();
				wCal.add(Calendar.MONTH, +i);
				String wDate = getDateStrings(wCal.getTime());

				wQueryFrom = wQueryBase + wQueryNote1 + wQueryFreq + ") "
						+ wQueryFromValues + wDate + wQueryNote2 + ","
						+ (pFrequency - i) + ")";
				// System.out.println(wQueryFrom);
				wDbAccess.executeUpdate(wQueryFrom);
				wQueryTo = wQueryBase + wQueryNote1 + wQueryFreq + ") "
						+ wQueryToValues + wDate + wQueryNote2 + ","
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

	public static void updateMoveRecord(int pIncomeActId, int pBookFromId,
			int pBookToId, int pYear, int pMonth, int pDay, int pValue,
			int pFrequency, String pNote) {

		DbAccess wDbAccess = new DbAccess();

		RecordTableItem wOldIncomeRecord = getRecordByActId(wDbAccess,
				pIncomeActId);
		RecordTableItem wOldExpenseRecord = getMovePairRecord(wDbAccess,
				wOldIncomeRecord);
		int wExpenseActId = wOldExpenseRecord.getId();

		int wOldGroupId = wOldIncomeRecord.getGroupId();
		int wOldFromBookId = wOldExpenseRecord.getBookId();
		int wOldToBookId = wOldIncomeRecord.getBookId();

		String wDate;
		String wQueryFrom;
		String wQueryTo;

		// 　ともに繰り返し0ならUpdateのみ
		if (isSingleMoveRecordPair(wDbAccess, wOldGroupId)) {
			wDate = "'" + pYear + "-" + pMonth + "-" + pDay + "'";
			wQueryFrom = "update " + mActTable + " set " + mBookIdCol + " = "
					+ pBookFromId + ", " + mItemIdCol + " = "
					+ getMoveExpenseItemId() + ", " + mActDtCol + " = " + wDate
					+ ", " + mActIncomeCol + " = " + "'0', " + mActExpenseCol
					+ " = " + pValue + ", " + mNoteNameCol + " = '" + pNote
					+ "' " + " where " + mActIdCol + " = " + wExpenseActId;
			// System.out.println(wQueryFrom);

			wDbAccess.executeUpdate(wQueryFrom);
			wQueryTo = "update " + mActTable + " set " + mBookIdCol + " = "
					+ pBookToId + ", " + mItemIdCol + " = "
					+ getMoveIncomeItemId() + ", " + mActDtCol + " = " + wDate
					+ ", " + mActIncomeCol + " = " + +pValue + ", "
					+ mActExpenseCol + " = " + "'0'" + ", " + mNoteNameCol
					+ " = '" + pNote + "' " + " where " + mActIdCol + " = "
					+ pIncomeActId;
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
			if (pYear != wOldCal.get(Calendar.YEAR)
					|| pMonth != wOldCal.get(Calendar.MONTH) + 1
					|| pBookFromId != wOldFromBookId
					|| pBookToId != wOldToBookId) {
				wGroupId = getNewGroupId(wDbAccess);
			} else {
				wGroupId = wOldGroupId;
			}

			// 新規のレコードを追加
			Calendar wCalBase = new GregorianCalendar(pYear, pMonth - 1, pDay);
			for (int i = 0; i < pFrequency + 1; i++) {
				Calendar wCal = (Calendar) wCalBase.clone();
				wCal.add(Calendar.MONTH, +i);
				wDate = "'" + wCal.get(Calendar.YEAR) + "-"
						+ (wCal.get(Calendar.MONDAY) + 1) + "-"
						+ wCal.get(Calendar.DAY_OF_MONTH) + "'";

				String wQueryBase = "insert into " + mActTable + " ( "
						+ mBookIdCol + "," + mItemIdCol + "," + mActDtCol + ","
						+ mActIncomeCol + "," + mActExpenseCol + ","
						+ mGroupIdCol + "," + mActFreqCol;

				String wQueryFromValue = " values(" + pBookFromId + ","
						+ getMoveExpenseItemId() + "," + wDate + "," + "'0'"
						+ "," + pValue + "," + wGroupId + ","
						+ (pFrequency - i);

				String wQueryToValue = " values(" + pBookToId + ","
						+ getMoveIncomeItemId() + "," + wDate + "," + pValue
						+ "," + "'0'" + "," + wGroupId + "," + (pFrequency - i);

				String wQueryNote1 = ")";
				String wQueryNote2 = ")";

				if (!"".equals(pNote)) {
					wQueryNote1 = "," + mNoteNameCol + ")";
					wQueryNote2 = ",'" + pNote + "')";
				}

				wQueryFrom = wQueryBase + wQueryNote1 + wQueryFromValue
						+ wQueryNote2;
				// System.out.println(wQueryFrom);
				wDbAccess.executeUpdate(wQueryFrom);

				wQueryTo = wQueryBase + wQueryNote1 + wQueryToValue
						+ wQueryNote2;
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
			wQuery += mGroupIdCol + " = " + wGroupId + " and " + mActDtCol
					+ " >= " + wDate;
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
		return mExpneseRexp;
	}

	private static RecordTableItem getMovePairRecord(DbAccess pDbAccess,
			RecordTableItem pRecord) {

		int wActId = pRecord.getId();
		int wGroupId = pRecord.getGroupId();
		String wDate = getDateStrings(pRecord.getDate());

		int wPairActId = 0;

		String wQuery = "select " + mActIdCol + " from " + mActTable
				+ " where " + mGroupIdCol + " = " + wGroupId + " and "
				+ mActDtCol + " = " + wDate + " and " + mActIdCol + " <> "
				+ wActId;

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
		if (pItemId == getMoveIncomeItemId()
				|| pItemId == getMoveExpenseItemId()) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isMoveCategory(int pCategoryId) {
		if (pCategoryId == getMoveIncomeItemId()
				|| pCategoryId == getMoveExpenseItemId()) {
			return true;
		} else {
			return false;
		}
	}

	public static SummaryTableItem[] getSummaryTableItems(int pBookId,
			Date pStartDate, Date pEndDate) {
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
		double[] wTotalAppearedIncomeExpense = getAppearedIncomeExpense(
				wDbAccess, pStartDate, pEndDate, mAllBookId);
		wTotalAppearedProfit = wTotalAppearedIncomeExpense[0]
				- wTotalAppearedIncomeExpense[1];

		// 立替収支
		wTempProfit = getTotalTempProfit(wDbAccess, pStartDate, pEndDate);

		// 特別収支
		wSpecialProfit = getTotalSpecialProfit(wDbAccess, pStartDate, pEndDate);

		// 実質収支、営業収支
		wActualProfit = wTotalAppearedProfit - wTempProfit;
		wOperatingProfit = wActualProfit - wSpecialProfit;

		wSummaryTableItemList
				.add(new SummaryTableItem("営業収支", wOperatingProfit));
		wSummaryTableItemList.add(new SummaryTableItem("実質収支", wActualProfit));
		wSummaryTableItemList.add(new SummaryTableItem("実質残高", wActualBalance));
		wSummaryTableItemList.add(new SummaryTableItem("借入残高", wTempBalance));

		// みかけ収支（各Book）
		if (pBookId == mAllBookId) {
			wBookAppearedIncome = wTotalAppearedIncomeExpense[0];
			wBookAppearedExpense = wTotalAppearedIncomeExpense[1];
		} else {
			double[] wBookAppearedIncomeExpense = getAppearedIncomeExpense(
					wDbAccess, pStartDate, pEndDate, pBookId);
			wBookAppearedIncome = wBookAppearedIncomeExpense[0];
			wBookAppearedExpense = wBookAppearedIncomeExpense[1];
		}

		wBookAppearedProfit = wBookAppearedIncome - wBookAppearedExpense;

		SummaryTableItem wAppearedProfitItem = new SummaryTableItem("みかけ収支",
				wBookAppearedProfit);
		wAppearedProfitItem.setAppearedSum(true);
		wSummaryTableItemList.add(wAppearedProfitItem);

		SummaryTableItem wAppearedIncomeItem = new SummaryTableItem("みかけ収入",
				wBookAppearedIncome);
		wAppearedIncomeItem.setAppearedIncomeExpense(true);
		wSummaryTableItemList.add(wAppearedIncomeItem);

		SummaryTableItem wAppearedExpenseItem = new SummaryTableItem("みかけ支出",
				wBookAppearedExpense);
		wAppearedExpenseItem.setAppearedIncomeExpense(true);

		// カテゴリ集計
		// CategoryId-SummaryTableItemList
		Map<Integer, List<SummaryTableItem>> wSummaryTableMap = new LinkedHashMap<Integer, List<SummaryTableItem>>();

		wQuery = "select " + mCategoryTable + "." + mCategoryIdCol + ", "
				+ mCategoryTable + "." + mCategoryNameCol + ", sum("
				+ mActIncomeCol + ") as " + mActIncomeCol + ", sum("
				+ mActExpenseCol + ") as " + mActExpenseCol + " from "
				+ mActTable + ", " + mItemTable + ", " + mCategoryTable
				+ " where " + mActTable + "." + mItemIdCol + " = " + mItemTable
				+ "." + mItemIdCol + " and " + mCategoryTable + "."
				+ mCategoryIdCol + " = " + mItemTable + "." + mCategoryIdCol
				+ " and " + mActDtCol + " between " + wStartDateString
				+ " and " + wEndDateString + " and " + mActTable + "."
				+ mDelFlgCol + " = b'0'" + " and (" + mActTable + "."
				+ mActIncomeCol + " + " + mActTable + "." + mActExpenseCol
				+ ") > 0";
		if (pBookId != mAllBookId) {
			wQuery += " and " + mActTable + "." + mBookIdCol + " = " + pBookId;
		}
		wQuery += " group by " + mCategoryTable + "." + mCategoryIdCol
				+ " order by " + mCategoryTable + "." + mCategoryRexpCol + ", "
				+ mCategoryTable + "." + mSortKeyCol;
		// System.out.println(wQuery);

		wResultSet = wDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryTable + "."
						+ mCategoryIdCol);
				if (pBookId == mAllBookId
						&& (DbUtil.isMoveCategory(wCategoryId))) {

				} else {
					String wCategoryName = wResultSet.getString(mCategoryTable
							+ "." + mCategoryNameCol);
					double wIncome = wResultSet.getDouble(mActIncomeCol);
					double wExpense = wResultSet.getDouble(mActExpenseCol);
					// System.out.println(wCategoryName + ", " + wIncome + ", "
					// + wExpense);
					List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();
					if (wIncome > 0) {
						wList.add(new SummaryTableItem(wCategoryId,
								wCategoryName, wIncome, true));
					} else {
						wList.add(new SummaryTableItem(wCategoryId,
								wCategoryName, wExpense, false));
					}
					wSummaryTableMap.put(wCategoryId, wList);
				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		// Item集計
		wQuery = "select " + mCategoryTable + "." + mCategoryIdCol + ", "
				+ mActTable + "." + mItemIdCol + ", " + mItemTable + "."
				+ mItemNameCol + ", sum(" + mActIncomeCol + ") as "
				+ mActIncomeCol + ", sum(" + mActExpenseCol + ") as "
				+ mActExpenseCol + " from " + mActTable + ", " + mItemTable
				+ ", " + mCategoryTable + " where " + mActTable + "."
				+ mItemIdCol + " = " + mItemTable + "." + mItemIdCol + " and "
				+ mCategoryTable + "." + mCategoryIdCol + " = " + mItemTable
				+ "." + mCategoryIdCol + " and " + mActDtCol + " between "
				+ wStartDateString + " and " + wEndDateString + " and "
				+ mActTable + "." + mDelFlgCol + " = b'0'" + " and ("
				+ mActTable + "." + mActIncomeCol + " + " + mActTable + "."
				+ mActExpenseCol + ") > 0";
		if (pBookId != mAllBookId) {
			wQuery += " and " + mActTable + "." + mBookIdCol + " = " + pBookId;
		}
		wQuery += " group by " + mActTable + "." + mItemIdCol + " order by "
				+ mCategoryTable + "." + mCategoryRexpCol + ", "
				+ mCategoryTable + "." + mSortKeyCol + ", " + mItemTable + "."
				+ mSortKeyCol;

		// System.out.println(wQuery);
		wResultSet = wDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryTable + "."
						+ mCategoryIdCol);
				if (pBookId == mAllBookId && (isMoveCategory(wCategoryId))) {

				} else {
					List<SummaryTableItem> wList = wSummaryTableMap
							.get(wCategoryId);
					int wItemId = wResultSet.getInt(mActTable + "."
							+ mItemIdCol);
					String wItemName = wResultSet.getString(mItemTable + "."
							+ mItemNameCol);
					double wIncome = wResultSet.getDouble(mActIncomeCol);
					double wExpense = wResultSet.getDouble(mActExpenseCol);
					// System.out.println(wItemName + ", " + wIncome + ", "
					// + wExpense);
					if (wIncome > 0) {
						wList.add(new SummaryTableItem(wItemId, wItemName,
								wCategoryId, wIncome, true));
					} else {
						wList.add(new SummaryTableItem(wItemId, wItemName,
								wCategoryId, wExpense, false));
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

		return (SummaryTableItem[]) wSummaryTableItemList
				.toArray(new SummaryTableItem[0]);
	}

	// AnnualHeaderItemsの総計を値が0でも生成
	public static SummaryTableItem[] getAllSummaryTableItems(int pBookId,
			Date pStartDate, Date pEndDate,
			AnnualHeaderItem[] pAnnualHeaderItems) {
		List<SummaryTableItem> wSummaryTableItemList = new ArrayList<SummaryTableItem>();
		Map<String, SummaryTableItem> wAllItemMap = new HashMap<String, SummaryTableItem>();

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
		double[] wTotalAppearedIncomeExpense = getAppearedIncomeExpense(
				wDbAccess, pStartDate, pEndDate, mAllBookId);
		wTotalAppearedProfit = wTotalAppearedIncomeExpense[0]
				- wTotalAppearedIncomeExpense[1];

		// 立替収支
		wTempProfit = getTotalTempProfit(wDbAccess, pStartDate, pEndDate);

		// 特別収支
		wSpecialProfit = getTotalSpecialProfit(wDbAccess, pStartDate, pEndDate);

		// 実質収支、営業収支
		wActualProfit = wTotalAppearedProfit - wTempProfit;
		wOperatingProfit = wActualProfit - wSpecialProfit;

		wSummaryTableItemList
				.add(new SummaryTableItem("営業収支", wOperatingProfit));
		wSummaryTableItemList.add(new SummaryTableItem("実質収支", wActualProfit));
		wSummaryTableItemList.add(new SummaryTableItem("実質残高", wActualBalance));
		wSummaryTableItemList.add(new SummaryTableItem("借入残高", wTempBalance));

		// みかけ収支（各Book）
		if (pBookId == mAllBookId) {
			wBookAppearedIncome = wTotalAppearedIncomeExpense[0];
			wBookAppearedExpense = wTotalAppearedIncomeExpense[1];
		} else {
			double[] wBookAppearedIncomeExpense = getAppearedIncomeExpense(
					wDbAccess, pStartDate, pEndDate, pBookId);
			wBookAppearedIncome = wBookAppearedIncomeExpense[0];
			wBookAppearedExpense = wBookAppearedIncomeExpense[1];
		}

		wBookAppearedProfit = wBookAppearedIncome - wBookAppearedExpense;

		SummaryTableItem wAppearedProfitItem = new SummaryTableItem("みかけ収支",
				wBookAppearedProfit);
		wAppearedProfitItem.setAppearedSum(true);
		wSummaryTableItemList.add(wAppearedProfitItem);

		SummaryTableItem wAppearedIncomeItem = new SummaryTableItem("みかけ収入",
				wBookAppearedIncome);
		wAppearedIncomeItem.setAppearedIncomeExpense(true);
		wSummaryTableItemList.add(wAppearedIncomeItem);

		SummaryTableItem wAppearedExpenseItem = new SummaryTableItem("みかけ支出",
				wBookAppearedExpense);
		wAppearedExpenseItem.setAppearedIncomeExpense(true);

		// カテゴリ集計
		// CategoryId-SummaryTableItemList

		wQuery = "select " + mCategoryTable + "." + mCategoryIdCol + ", "
				+ mCategoryTable + "." + mCategoryNameCol + ", sum("
				+ mActIncomeCol + ") as " + mActIncomeCol + ", sum("
				+ mActExpenseCol + ") as " + mActExpenseCol + " from "
				+ mActTable + ", " + mItemTable + ", " + mCategoryTable
				+ " where " + mActTable + "." + mItemIdCol + " = " + mItemTable
				+ "." + mItemIdCol + " and " + mCategoryTable + "."
				+ mCategoryIdCol + " = " + mItemTable + "." + mCategoryIdCol
				+ " and " + mActDtCol + " between " + wStartDateString
				+ " and " + wEndDateString + " and " + mActTable + "."
				+ mDelFlgCol + " = b'0'" + " and (" + mActTable + "."
				+ mActIncomeCol + " + " + mActTable + "." + mActExpenseCol
				+ ") > 0";
		if (pBookId != mAllBookId) {
			wQuery += " and " + mActTable + "." + mBookIdCol + " = " + pBookId;
		}
		wQuery += " group by " + mCategoryTable + "." + mCategoryIdCol
				+ " order by " + mCategoryTable + "." + mCategoryRexpCol + ", "
				+ mCategoryTable + "." + mSortKeyCol;
		// System.out.println(wQuery);

		wResultSet = wDbAccess.executeQuery(wQuery);
		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryTable + "."
						+ mCategoryIdCol);
				if (pBookId == mAllBookId
						&& (DbUtil.isMoveCategory(wCategoryId))) {

				} else {
					String wCategoryName = wResultSet.getString(mCategoryTable
							+ "." + mCategoryNameCol);
					double wIncome = wResultSet.getDouble(mActIncomeCol);
					double wExpense = wResultSet.getDouble(mActExpenseCol);
					String wIndex = wCategoryId + " + "
							+ SystemData.getUndefinedInt();
					// System.out.println(wCategoryName + ", " + wIncome + ", "
					// + wExpense);
					if (wIncome > 0) {
						wAllItemMap.put(wIndex, new SummaryTableItem(
								wCategoryId, wCategoryName, wIncome, true));
					} else {
						wAllItemMap.put(wIndex, new SummaryTableItem(
								wCategoryId, wCategoryName, wExpense, false));
					}
				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		// Item集計
		wQuery = "select " + mCategoryTable + "." + mCategoryIdCol + ", "
				+ mActTable + "." + mItemIdCol + ", " + mItemTable + "."
				+ mItemNameCol + ", sum(" + mActIncomeCol + ") as "
				+ mActIncomeCol + ", sum(" + mActExpenseCol + ") as "
				+ mActExpenseCol + " from " + mActTable + ", " + mItemTable
				+ ", " + mCategoryTable + " where " + mActTable + "."
				+ mItemIdCol + " = " + mItemTable + "." + mItemIdCol + " and "
				+ mCategoryTable + "." + mCategoryIdCol + " = " + mItemTable
				+ "." + mCategoryIdCol + " and " + mActDtCol + " between "
				+ wStartDateString + " and " + wEndDateString + " and "
				+ mActTable + "." + mDelFlgCol + " = b'0'" + " and ("
				+ mActTable + "." + mActIncomeCol + " + " + mActTable + "."
				+ mActExpenseCol + ") > 0";
		if (pBookId != mAllBookId) {
			wQuery += " and " + mActTable + "." + mBookIdCol + " = " + pBookId;
		}
		wQuery += " group by " + mActTable + "." + mItemIdCol + " order by "
				+ mCategoryTable + "." + mCategoryRexpCol + ", "
				+ mCategoryTable + "." + mSortKeyCol + ", " + mItemTable + "."
				+ mSortKeyCol;

		// System.out.println(wQuery);
		wResultSet = wDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryTable + "."
						+ mCategoryIdCol);
				if (pBookId == mAllBookId && (isMoveCategory(wCategoryId))) {

				} else {
					int wItemId = wResultSet.getInt(mActTable + "."
							+ mItemIdCol);
					String wItemName = wResultSet.getString(mItemTable + "."
							+ mItemNameCol);
					double wIncome = wResultSet.getDouble(mActIncomeCol);
					double wExpense = wResultSet.getDouble(mActExpenseCol);

					String wIndex = wCategoryId + " + " + wItemId;

					// System.out.println(wItemName + ", " + wIncome + ", "
					// + wExpense);
					if (wIncome > 0) {
						wAllItemMap.put(wIndex, new SummaryTableItem(wItemId,
								wItemName, wCategoryId, wIncome, true));
					} else {
						wAllItemMap.put(wIndex, new SummaryTableItem(wItemId,
								wItemName, wCategoryId, wExpense, false));
					}
				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		wDbAccess.closeConnection();

		// リストへ全結果を挿入
		// Iterator<Integer> wIt = wSummaryTableMap.keySet().iterator();
		boolean wExpenseRow = false;

		// 値が0のアイテムも追加
		for (int i = 0; i < pAnnualHeaderItems.length; i++) {
			AnnualHeaderItem wAnnualHeaderItem = pAnnualHeaderItems[i];
			if (wAnnualHeaderItem.isSpecialHeader()) {
				continue;
			} else if (wAnnualHeaderItem.isCategory()) {
				if (!wExpenseRow && !wAnnualHeaderItem.isIncome()) {
					wExpenseRow = true;
					wSummaryTableItemList.add(wAppearedExpenseItem);
				}
				String wIndex = wAnnualHeaderItem.getCategoryId() + " + "
						+ SystemData.getUndefinedInt();
				if (wAllItemMap.containsKey(wIndex)) {
					wSummaryTableItemList.add(wAllItemMap.get(wIndex));
				} else {
					wSummaryTableItemList.add(new SummaryTableItem(
							wAnnualHeaderItem.getCategoryId(),
							wAnnualHeaderItem.getName(), 0, wAnnualHeaderItem
									.isIncome()));
				}
			} else if (wAnnualHeaderItem.isItem()) {
				if (!wExpenseRow && !wAnnualHeaderItem.isIncome()) {
					wExpenseRow = true;
					wSummaryTableItemList.add(wAppearedExpenseItem);
				}
				String wIndex = wAnnualHeaderItem.getCategoryId() + " + "
						+ wAnnualHeaderItem.getItemId();
				if (wAllItemMap.containsKey(wIndex)) {
					wSummaryTableItemList.add(wAllItemMap.get(wIndex));
				} else {
					wSummaryTableItemList.add(new SummaryTableItem(
							wAnnualHeaderItem.getItemId(), wAnnualHeaderItem
									.getName(), wAnnualHeaderItem
									.getCategoryId(), 0, wAnnualHeaderItem
									.isIncome()));
				}
			}

		}
		if (!wExpenseRow) {
			wSummaryTableItemList.add(wAppearedExpenseItem);
		}

		// for (SummaryTableItem wItem : wSummaryTableItemList) {
		// System.out.println(wItem.getItemName() + ", " + wItem.getValue()
		// + ", " + wItem.isIncome());
		// }

		// return wSummaryTableItemList;
		return (SummaryTableItem[]) wSummaryTableItemList
				.toArray(new SummaryTableItem[0]);
	}

	// 立替残高（借入残高）
	private static double getTempBalance(DbAccess pDbAccess, Date pEndDate) {

		double wTempBalance = 0;
		String wResultColName = "ResultCol";
		String wEndDateString = getDateStrings(pEndDate);

		String wQuery = "select sum(" + mActIncomeCol + " - " + mActExpenseCol
				+ ") as " + wResultColName + " from " + mActTable + ", "
				+ mItemTable + ", " + mCategoryTable + " where " + mActTable
				+ "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol
				+ " and " + mItemTable + "." + mCategoryIdCol + " = "
				+ mCategoryTable + "." + mCategoryIdCol + " and " + mActTable
				+ "." + mDelFlgCol + " = b'0' and " + mActDtCol + " <= "
				+ wEndDateString + " and (" + mCategoryTable + "."
				+ mCategoryIdCol + " = " + mTempIncomeCategoryId + " or "
				+ mCategoryTable + "." + mCategoryIdCol + " = "
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

	private static double getTotalTempProfit(DbAccess pDbAccess,
			Date pStartDate, Date pEndDate) {
		// 立替収支
		double wTotalTempProfit = 0;

		String wResultColName = "ResultCol";
		String wStartDateString = getDateStrings(pStartDate);
		String wEndDateString = getDateStrings(pEndDate);

		String wQuery = "select sum(" + mActIncomeCol + " - " + mActExpenseCol
				+ ") as " + wResultColName + " from " + mActTable + ", "
				+ mItemTable + ", " + mCategoryTable + " where " + mActTable
				+ "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol
				+ " and " + mItemTable + "." + mCategoryIdCol + " = "
				+ mCategoryTable + "." + mCategoryIdCol + " and " + mActTable
				+ "." + mDelFlgCol + " = b'0' and " + mActDtCol + " between "
				+ wStartDateString + " and " + wEndDateString + " and ("
				+ mCategoryTable + "." + mCategoryIdCol + " = "
				+ mTempIncomeCategoryId + " or " + mCategoryTable + "."
				+ mCategoryIdCol + " = " + mTempExpenseCategoryId + ")";

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

	private static double getTotalSpecialProfit(DbAccess pDbAccess,
			Date pStartDate, Date pEndDate) {
		// 特別収支
		double wSpecialProfit = 0;

		String wResultColName = "ResultCol";
		String wStartDateString = getDateStrings(pStartDate);
		String wEndDateString = getDateStrings(pEndDate);

		String wQuery = "select sum(" + mActIncomeCol + " - " + mActExpenseCol
				+ ") as " + wResultColName + " from " + mActTable + ", "
				+ mItemTable + ", " + mCategoryTable + " where " + mActTable
				+ "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol
				+ " and " + mItemTable + "." + mCategoryIdCol + " = "
				+ mCategoryTable + "." + mCategoryIdCol + " and " + mActTable
				+ "." + mDelFlgCol + " = b'0' and " + mActDtCol + " between "
				+ wStartDateString + " and " + wEndDateString + " and ("
				+ mCategoryTable + "." + mCategoryIdCol + " = "
				+ mSpecialIncomeCategoryId + " or " + mCategoryTable + "."
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

	private static double[] getAppearedIncomeExpense(DbAccess pDbAccess,
			Date pStartDate, Date pEndDate, int pBookId) {
		// みかけ収入・支出（各Book）
		double[] wAppearedIncomeExpense = new double[2];

		String wStartDateString = getDateStrings(pStartDate);
		String wEndDateString = getDateStrings(pEndDate);

		String wQuery = "select sum(" + mActIncomeCol + ") as " + mActIncomeCol
				+ ", sum(" + mActExpenseCol + ") as " + mActExpenseCol
				+ " from " + mActTable + ", " + mItemTable + ", "
				+ mCategoryTable + " where " + mActTable + "." + mItemIdCol
				+ " = " + mItemTable + "." + mItemIdCol + " and " + mItemTable
				+ "." + mCategoryIdCol + " = " + mCategoryTable + "."
				+ mCategoryIdCol + " and " + mActTable + "." + mDelFlgCol
				+ " = b'0' and " + mActDtCol + " between " + wStartDateString
				+ " and " + wEndDateString;

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
				wAppearedIncomeExpense[1] = wResultSet
						.getDouble(mActExpenseCol);
				// System.out.println(wTempBalanceName + " = " +
				// wTempBalance);
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wAppearedIncomeExpense;
	}

	public static AnnualHeaderItem[] getAnnualHeaderItem(int pBookId,
			Date pStartDate, Date pEndDate, boolean pWithCategory,
			boolean pWithItem) {
		List<AnnualHeaderItem> wList = new ArrayList<AnnualHeaderItem>();
		wList.add(new AnnualHeaderItem("営業収支"));
		wList.add(new AnnualHeaderItem("実質収支"));
		wList.add(new AnnualHeaderItem("実質残高"));
		wList.add(new AnnualHeaderItem("借入残高"));
		wList.add(new AnnualHeaderItem("みかけ収支"));
		wList.add(new AnnualHeaderItem("みかけ収入"));

		DbAccess wDbAccess = new DbAccess();

		// Integer: CategoryId
		Map<Integer, List<AnnualHeaderItem>> wMap = getAnnualHeaderCategoryMap(
				wDbAccess, pBookId, pStartDate, pEndDate);
		if (pWithItem) {
			wMap = getAnnualHeaderItemMap(wDbAccess, wMap, pBookId, pStartDate,
					pEndDate);
		}

		Iterator<Integer> wIt = wMap.keySet().iterator();
		boolean wExpenseRow = false;
		while (wIt.hasNext()) {
			Integer wKey = wIt.next();
			if (!wExpenseRow && !wMap.get(wKey).get(0).isIncome()) {
				wExpenseRow = true;
				wList.add(new AnnualHeaderItem("みかけ支出"));
			}
			if (!pWithCategory) {
				wMap.get(wKey).remove(0);
			}
			wList.addAll(wMap.get(wKey));
		}
		if (!wExpenseRow) {
			wList.add(new AnnualHeaderItem("みかけ支出"));
		}

		// for (AnnualHeaderItem wItem : wList) {
		// if (wItem.isItem()) {
		// System.out.print("   ");
		// }
		// System.out.println(wItem.getName());
		// }

		return (AnnualHeaderItem[]) wList.toArray(new AnnualHeaderItem[0]);

	}

	private static Map<Integer, List<AnnualHeaderItem>> getAnnualHeaderItemMap(
			DbAccess pDbAccess, Map<Integer, List<AnnualHeaderItem>> pMap,
			int pBookId, Date pStardDate, Date pEndDate) {

		String wStartDateString = getDateStrings(pStardDate);
		String wEndDateString = getDateStrings(pEndDate);

		String wQuery = "select " + mCategoryTable + "." + mCategoryRexpCol
				+ ", " + mCategoryTable + "." + mCategoryIdCol + ", "
				+ mItemTable + "." + mItemIdCol + "," + mItemTable + "."
				+ mItemNameCol + " from " + mCategoryTable + ", " + mItemTable
				+ ", " + mActTable;
		wQuery += " where " + mCategoryTable + "." + mCategoryIdCol + " = "
				+ mItemTable + "." + mCategoryIdCol + " and " + mItemTable
				+ "." + mItemIdCol + " = " + mActTable + "." + mItemIdCol
				+ " and " + mActTable + "." + mDelFlgCol + " = b'0' " + " and "
				+ getBookWhere(pBookId) + " and " + mActDtCol + " between "
				+ wStartDateString + " and " + wEndDateString;
		if (pBookId == mAllBookId) {
			wQuery += " and " + mItemTable + "." + mMoveFlgCol + " = b'0'";
		}
		wQuery += " group by " + mItemTable + "." + mItemIdCol + " order by "
				+ mCategoryTable + "." + mCategoryRexpCol + ", "
				+ mCategoryTable + "." + mSortKeyCol + ", " + mItemTable + "."
				+ mSortKeyCol;

		// System.out.println(wQuery);
		ResultSet wResultSet = pDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryTable + "."
						+ mCategoryIdCol);
				int wItemId = wResultSet.getInt(mItemTable + "." + mItemIdCol);
				String wItemName = wResultSet.getString(mItemTable + "."
						+ mItemNameCol);
				List<AnnualHeaderItem> wTempList = pMap.get(wCategoryId);

				boolean wIsIncome;
				if (wResultSet.getInt(mCategoryTable + "." + mCategoryRexpCol) == mIncomeRexp) {
					wIsIncome = true;
				} else {
					wIsIncome = false;
				}
				wTempList.add(new AnnualHeaderItem(wCategoryId, wItemId,
						wItemName, wIsIncome));
			}

			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return pMap;

	}

	private static Map<Integer, List<AnnualHeaderItem>> getAnnualHeaderCategoryMap(
			DbAccess pDbAccess, int pBookId, Date pStartDate, Date pEndDate) {

		Map<Integer, List<AnnualHeaderItem>> wMap = new LinkedHashMap<Integer, List<AnnualHeaderItem>>();
		String wStartDateString = getDateStrings(pStartDate);
		String wEndDateString = getDateStrings(pEndDate);

		String wQuery = "select " + mCategoryTable + "." + mCategoryRexpCol
				+ ", " + mCategoryTable + "." + mCategoryIdCol + ", "
				+ mCategoryTable + "." + mCategoryNameCol + " from "
				+ mCategoryTable + ", " + mItemTable + ", " + mActTable;
		wQuery += " where " + mCategoryTable + "." + mCategoryIdCol + " = "
				+ mItemTable + "." + mCategoryIdCol + " and " + mItemTable
				+ "." + mItemIdCol + " = " + mActTable + "." + mItemIdCol
				+ " and " + mActTable + "." + mDelFlgCol + " = b'0' " + " and "
				+ getBookWhere(pBookId) + " and " + mActDtCol + " between "
				+ wStartDateString + " and " + wEndDateString;
		if (pBookId == mAllBookId) {
			wQuery += " and " + mItemTable + "." + mMoveFlgCol + " = b'0'";
		}
		wQuery += " group by " + mCategoryTable + "." + mCategoryIdCol
				+ " order by " + mCategoryTable + "." + mCategoryRexpCol + ", "
				+ mCategoryTable + "." + mSortKeyCol;

		// System.out.println(wQuery);
		ResultSet wResultSet = pDbAccess.executeQuery(wQuery);

		try {
			while (wResultSet.next()) {
				List<AnnualHeaderItem> wList = new ArrayList<AnnualHeaderItem>();
				boolean wIsIncome;
				if (mIncomeRexp == wResultSet.getInt(mCategoryTable + "."
						+ mCategoryRexpCol)) {
					wIsIncome = true;
				} else {
					wIsIncome = false;
				}
				int wCategoryId = wResultSet.getInt(mCategoryTable + "."
						+ mCategoryIdCol);
				String wCategoryName = wResultSet.getString(mCategoryTable
						+ "." + mCategoryNameCol);
				// System.out.println(wCategoryId + ", " + wCategoryName + ", "
				// + wIsIncome);
				wList.add(new AnnualHeaderItem(wCategoryId, wCategoryName,
						wIsIncome));
				wMap.put(wCategoryId, wList);
			}

			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wMap;

	}

	private static double getBalance(DbAccess pDbAccess, Date pEndDate,
			int pBookId, boolean pIncludeEndDate) {

		double wBalance = getInitialBalance(pDbAccess, pBookId);

		String wEnd = getDateStrings(pEndDate);
		String wBookWhere = getBookWhere(pBookId);
		String wResultCol = "Value";

		String wQuery = "select SUM(" + mActIncomeCol + " - " + mActExpenseCol
				+ ") as " + wResultCol + " from " + mActTable + " where "
				+ mDelFlgCol + " = b'0' " + " and " + wBookWhere + " and "
				+ mActDtCol;

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
			wQuery = "select SUM(" + mBookBalanceCol + ") as " + wResultCol
					+ " from " + mBookTable + " where " + mDelFlgCol
					+ " = b'0' and " + wBookWhere;

		} else {
			wQuery = "select " + mBookBalanceCol + " as " + wResultCol
					+ " from " + mBookTable + " where " + wBookWhere;
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

		ResultSet wResultSet = pDbAccess.executeQuery("select max("
				+ mGroupIdCol + ") as " + wCol + " from " + mActTable);

		try {
			wResultSet.next();
			wRet = wResultSet.getInt(wCol);
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wRet + 1;

	}

	private static void updateNoteTable(DbAccess pDbAccess, int pItemId,
			String pNote) {
		String wQuery = "delete from " + mNoteTable + " where " + mItemIdCol
				+ " = " + pItemId + " and " + mNoteNameCol + " = '" + pNote
				+ "'";
		pDbAccess.executeUpdate(wQuery);

		wQuery = "insert into  " + mNoteTable + " (" + mNoteNameCol + ","
				+ mItemIdCol + ") values('" + pNote + "'," + pItemId + ")";
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

	private static boolean isSingleMoveRecordPair(DbAccess pDbAccess,
			int pGroupId) {
		String wResultCol = "COUNT";
		String wQuery = "select count(" + mActIdCol + ") as " + wResultCol
				+ " from " + mActTable + " where " + mGroupIdCol + " = "
				+ pGroupId;
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
		StringBuffer wStack = new StringBuffer();
		for (int i = 0; i < e.getStackTrace().length; i++) {
			if (i == 10) {
				wStack.append("...");
				break;
			}
			wStack.append(e.getStackTrace()[i] + "\n");
		}
		MessageDialog.openWarning(
				SystemData.getCompositeRightMain().getShell(),
				"SQL ResultSet Handling Error", e.toString() + "\n\n" + wStack);
		// System.err.println("ResultSet Handling Error: " + e.toString());
		e.printStackTrace();
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
	// DbAccess wDbAccess = new DbAccess();
	// getAnnualHeaderCategoryMap(wDbAccess, 2, (new GregorianCalendar(2009,
	// 0, 1)).getTime(), new Date());
	// getAnnualHeaderItem(SystemData.getAllBookInt(), (new
	// GregorianCalendar(2009, 0, 1)).getTime(), new
	// Date());
	//		
	// wDbAccess.closeConnection();
	// }

}
