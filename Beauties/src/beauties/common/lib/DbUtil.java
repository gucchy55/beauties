package beauties.common.lib;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import java.sql.PreparedStatement;

import beauties.common.model.AnnualDateRange;
import beauties.common.model.Book;
import beauties.common.model.DateRange;
import beauties.common.model.IncomeExpense;
import beauties.common.model.IncomeExpenseSummary;
import beauties.config.model.ConfigItem;
import beauties.record.model.RecordTableItem;
import beauties.record.model.RecordTableItemForMove;
import beauties.record.model.SummaryTableItem;
import beauties.record.model.SummaryTableItemFactory;

public class DbUtil {

	// Systemテーブル関連
	private final static String mSystemTable = "system";
	private final static String mSystemValueCol = "NUM_VALUE";
	private final static String mSystemIDCol = "SID";
	private final static String mCutOff = "CUTOFF_DT";
	private final static String mFiscalMonth = "FISCAL_MH";
	private final static String mShowGridLine = "SHOW_GRIDLINES";

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

	private final static String mPeriodName = "Period";
	private final static String mAppearedBalanceName = "みかけ残高";
	private final static String mTempBalanceName = "立替累計";
	private final static String mAppearedIncomeName = "みかけ収入";
	private final static String mAppearedExpenseName = "みかけ支出";
	private final static String mSpecialIncomeName = "特別収入";
	private final static String mSpecialExpenseName = "特別支出";
	private final static String mTempIncomeName = "立替収入";
	private final static String mTempExpenseName = "立替支出";

	private static DbAccess mDbAccess = DbAccess.getInstance();

	private DbUtil() {

	}
	
	private static int getSystemValue(String pWhereVal) {
		int wResult = SystemData.getUndefinedInt();
		
		// select NUM_VALUE from system where SID = ?
		String wQuery = "select " + mSystemValueCol + " from " + mSystemTable + " where " + mSystemIDCol +" = ?";
		PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery);
		
		try {
			wStatement.setString(1, pWhereVal);
			ResultSet wResultSet = mDbAccess.executeQuery(wStatement);
			wResultSet.next();
			wResult = wResultSet.getInt(mSystemValueCol);
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		return wResult;
		
	}
	
	private static void updateSystemValue(String pWhereVal, int pVal) {
		StringBuilder wQueryBuilder = new StringBuilder();
		
		// update system set NUM_VALUE = ? where SID = ?
		wQueryBuilder.append("update ").append(mSystemTable).append(" set ")
				.append(mSystemValueCol)
				.append(" = ? where ").append(mSystemIDCol).append(" = ? ");
		PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString());
		try {
			wStatement.setInt(1, pVal);
			wStatement.setString(2, pWhereVal);
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}
	
	public static int getCutOff() {
		return getSystemValue(mCutOff);
	}

	public static void updateCutOff(int pCutOff) {
		updateSystemValue(mCutOff, pCutOff);
	}

	public static int getFisCalMonth() {
		return getSystemValue(mFiscalMonth);
	}

	public static void updateFisCalMonth(int pFiscalMonth) {
		updateSystemValue(mFiscalMonth, pFiscalMonth);
	}

	public static boolean showGridLine() {
		return getSystemValue(mShowGridLine) == 1;
	}

	public static void updateShowGridLine(boolean pShowGridLine) {
		updateSystemValue(mShowGridLine, pShowGridLine ? 1 : 0);
	}

	public static int getCategoryIdByItemId(int pItemId) {
		StringBuilder wQueryBuilder = new StringBuilder();
		
		// select CATEGORY_ID from cbm_item where ITEM_ID = ?
//		wQueryBuilder.append("select ").append(mCategoryIdCol).append(" from ")
//				.append(mItemTable).append(" where ").append(mItemIdCol).append(" = ")
//				.append(pItemId);
//		ResultSet wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());
		
		wQueryBuilder.append("select ").append(mCategoryIdCol).append(" from ")
				.append(mItemTable).append(" where ").append(mItemIdCol).append(" = ? ");
		PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString());

		int wCategoryId = SystemData.getUndefinedInt();

		try {
			wStatement.setInt(1, pItemId);
			ResultSet wResultSet = mDbAccess.executeQuery(wStatement);
			wResultSet.next();
			wCategoryId = wResultSet.getInt(mCategoryIdCol);
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wCategoryId;
	}

	public static String getItemNameById(int pItemId) {
		StringBuilder wQueryBuilder = new StringBuilder();
		wQueryBuilder.append("select ").append(mItemNameCol).append(" from ")
				.append(mItemTable).append(" where ").append(mItemIdCol).append(" = ")
				.append(pItemId);
		ResultSet wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());
		// ResultSet wResultSet = mDbAccess.executeQuery("select " +
		// mItemNameCol + " from "
		// + mItemTable + " where " + mItemIdCol + " = " + pItemId);

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

	public static String getCategoryNameById(int pCategoryId) {
		StringBuilder wQueryBuilder = new StringBuilder();
		wQueryBuilder.append("select ").append(mCategoryNameCol).append(" from ")
				.append(mCategoryTable).append(" where ").append(mCategoryIdCol).append(" = ")
				.append(pCategoryId);
		ResultSet wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());
		// ResultSet wResultSet = mDbAccess.executeQuery("select " +
		// mCategoryNameCol + " from "
		// + mCategoryTable + " where " + mCategoryIdCol + " = " + pCategoryId);

		String wName = "";

		try {
			wResultSet.next();
			wName = wResultSet.getString(mCategoryNameCol);
			wResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wName;

	}

	public static RecordTableItem[][] getRecordTableItems(DateRange pDateRange, int pBookId) {
		List<RecordTableItem> wRecordTableItemListUp = new ArrayList<RecordTableItem>();
		List<RecordTableItem> wRecordTableItemListBottom = new ArrayList<RecordTableItem>();

		String wStart = getDateStrings(pDateRange.getStartDate());
		String wEnd = getDateStrings(pDateRange.getEndDate());

		String wBookWhere = getBookWhere(pBookId);

		String wMoveFlgWhere = "";
		if (pBookId == mAllBookId) {
			wMoveFlgWhere = new StringBuilder().append(" and ").append(mItemTable).append(".")
					.append(mMoveFlgCol).append(" = b'0'").toString();
		}

		int wBalance = getBalance(pDateRange.getStartDate(), pBookId, false);
		RecordTableItem wBalanceRecord = RecordTableItem.createBalanceRowItem(pDateRange
				.getStartDate(), wBalance);

		wRecordTableItemListUp.add(wBalanceRecord);
		String wQuery = new StringBuilder().append("select ").append(mActIdCol).append(", ")
				.append(mBookIdCol).append(", ").append(mActDtCol).append(", ").append(mActTable)
				.append(".").append(mItemIdCol).append(", ").append(mGroupIdCol).append(", ")
				.append(mActIncomeCol).append(", ").append(mActExpenseCol).append(", ")
				.append(mActFreqCol).append(", ").append(mNoteNameCol).append(" from ")
				.append(mActTable).append(", ").append(mItemTable).append(", ")
				.append(mCategoryTable).append(" where ").append(mItemTable).append(".")
				.append(mItemIdCol).append(" = ").append(mActTable).append(".").append(mItemIdCol)
				.append(" and ").append(mItemTable).append(".").append(mCategoryIdCol)
				.append(" = ").append(mCategoryTable).append(".").append(mCategoryIdCol)
				.append(" and ").append(mActDtCol).append(" between ").append(wStart)
				.append(" and ").append(wEnd).append(" and ").append(mActTable).append(".")
				.append(mDelFlgCol).append(" = b'0' ").append(wMoveFlgWhere).append(" and ")
				.append(wBookWhere).append(" order by ").append(mActDtCol).append(", ")
				.append(mCategoryTable).append(".").append(mCategoryRexpCol).append(", ")
				.append(mCategoryTable).append(".").append(mSortKeyCol).append(", ")
				.append(mItemTable).append(".").append(mSortKeyCol).toString();

		// String wQuery = "select " + mActIdCol + ", " + mBookIdCol + ", " +
		// mActDtCol + ", "
		// + mActTable + "." + mItemIdCol + ", " + mGroupIdCol + ", " +
		// mActIncomeCol + ", "
		// + mActExpenseCol + ", " + mActFreqCol + ", " + mNoteNameCol +
		// " from " + mActTable
		// + ", " + mItemTable + ", " + mCategoryTable + " where " + mItemTable
		// + "."
		// + mItemIdCol + " = " + mActTable + "." + mItemIdCol + " and " +
		// mItemTable + "."
		// + mCategoryIdCol + " = " + mCategoryTable + "." + mCategoryIdCol +
		// " and "
		// + mActDtCol + " between " + wStart + " and " + wEnd + " and " +
		// mActTable + "."
		// + mDelFlgCol + " = b'0' " + wMoveFlgWhere + " and " + wBookWhere +
		// " order by "
		// + mActDtCol + ", " + mCategoryTable + "." + mCategoryRexpCol + ", "
		// + mCategoryTable + "." + mSortKeyCol + ", " + mItemTable + "." +
		// mSortKeyCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try {
			Date wDateNow = new Date();
			while (wResultSet.next()) {
				wBalance += wResultSet.getInt(mActIncomeCol) - wResultSet.getInt(mActExpenseCol);

				RecordTableItem wRecord = new RecordTableItem.Builder(
						wResultSet.getInt(mBookIdCol), wResultSet.getInt(mItemIdCol), wResultSet
								.getDate(mActDtCol)).actId(wResultSet.getInt(mActIdCol)).balance(
						wBalance).expense(wResultSet.getInt(mActExpenseCol)).frequency(
						wResultSet.getInt(mActFreqCol)).groupId(wResultSet.getInt(mGroupIdCol))
						.income(wResultSet.getInt(mActIncomeCol)).note(
								wResultSet.getString(mNoteNameCol)).build();
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
		wRet[0] = wRecordTableItemListUp.toArray(new RecordTableItem[0]);
		wRet[1] = wRecordTableItemListBottom.toArray(new RecordTableItem[0]);

		return wRet;

	}

	public static Map<Integer, String> getBookNameMap() {
		Map<Integer, String> wBookMap = new LinkedHashMap<Integer, String>();

		String wQuery = new StringBuilder().append("select ").append(mBookIdCol).append(", ")
				.append(mBookNameCol).append(" from ").append(mBookTable)
				.append(" where ").append(mDelFlgCol).append(" = b'0' ").append(" order by ")
				.append(mSortKeyCol).toString();
		// String wQuery = "select " + mBookIdCol + ", " + mBookNameCol +
		// " from " + mBookTable
		// + " where " + mDelFlgCol + " = b'0' " + " order by " + mSortKeyCol;

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

		String wQuery = new StringBuilder().append("select ").append(mActIdCol).append(", ")
				.append(mBookIdCol).append(", ").append(mActDtCol).append(", ")
				.append(mActTable).append(".").append(mItemIdCol).append(", ").append(mGroupIdCol)
				.append(", ").append(mActIncomeCol).append(", ")
				.append(mActExpenseCol).append(", ").append(mActFreqCol).append(", ")
				.append(mNoteNameCol)
				.append(" from ").append(mActTable)
				.append(" where ").append(mActIdCol).append(" = ").append(pId).toString();
		// String wQuery = "select " + mActIdCol + ", " + mBookIdCol + ", " +
		// mActDtCol + ", "
		// + mActTable + "." + mItemIdCol + ", " + mGroupIdCol + ", " +
		// mActIncomeCol + ", "
		// + mActExpenseCol + ", " + mActFreqCol + ", " + mNoteNameCol +
		// " from " + mActTable
		// + " where " + mActIdCol + " = " + pId;

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

		return new RecordTableItem.Builder(wBookId, wItemId, wDate).actId(wId).expense(wExpense)
				.frequency(wFrequency).groupId(wGroupId).income(wIncome).note(wNote).build();
	}

	public static String[] getNotes(int pItemId) {
		List<String> wResultList = new ArrayList<String>();

		String wQuery = new StringBuilder().append("select ").append(mNoteNameCol).append(" from ")
				.append(mNoteTable).append(" where ").append(mItemIdCol)
				.append(" = ").append(pItemId).append(" and ").append(mDelFlgCol)
				.append(" = b'0' ")
				.append(" order by ").append(mNoteIdCol)
				.append(" desc ").toString();
		// String wQuery = "select " + mNoteNameCol + " from " + mNoteTable +
		// " where " + mItemIdCol
		// + " = " + pItemId + " and " + mDelFlgCol + " = b'0' " + " order by "
		// + mNoteIdCol
		// + " desc ";

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

	// For all books
	public static Map<Integer, String> getItemNameMap(boolean pIncome) {
		Map<Integer, String> wResultMap = new LinkedHashMap<Integer, String>();
		int wRexp = mIncomeRexp;
		if (!pIncome) {
			wRexp = mExpenseRexp;
		}

		StringBuilder wQueryBuilder = new StringBuilder();
		wQueryBuilder.append("select ").append(mItemTable).append(".").append(mItemIdCol)
				.append(", ")
				.append(mItemTable).append(".").append(mItemNameCol);
		wQueryBuilder.append(" from ").append(mItemTable).append(" , ").append(mBookItemTable)
				.append(", ").append(mCategoryTable);
		wQueryBuilder.append(" where ").append(mItemTable).append(".").append(mItemIdCol)
				.append(" = ")
				.append(mBookItemTable).append(".")
				.append(mItemIdCol).append(" and ").append(mCategoryTable).append(".")
				.append(mCategoryIdCol).append(" = ").append(mItemTable)
				.append(".").append(mCategoryIdCol);
		wQueryBuilder.append(" and ").append(mCategoryTable).append(".").append(mCategoryRexpCol)
				.append(" = ").append(wRexp).append(" and ")
				.append(mBookItemTable).append(".").append(mDelFlgCol).append(" = b'0' ")
				.append(" and ")
				.append(mItemTable).append(".")
				.append(mDelFlgCol).append(" = b'0' ");
		wQueryBuilder.append(" order by ").append(mItemTable).append(".").append(mSortKeyCol);

		// String wQuery = "select " + mItemTable + "." + mItemIdCol + ", " +
		// mItemTable + "."
		// + mItemNameCol;
		// wQuery += " from " + mItemTable + " , " + mBookItemTable + ", " +
		// mCategoryTable;
		// wQuery += " where " + mItemTable + "." + mItemIdCol + " = " +
		// mBookItemTable + "."
		// + mItemIdCol + " and " + mCategoryTable + "." + mCategoryIdCol +
		// " = " + mItemTable
		// + "." + mCategoryIdCol;
		// // if (pBookId != SystemData.getAllBookInt()) {
		// // wQuery += " and " + mBookItemTable + "." + mBookIdCol + " = " +
		// // pBookId;
		// // }
		// wQuery += " and " + mCategoryTable + "." + mCategoryRexpCol + " = " +
		// wRexp + " and "
		// + mBookItemTable + "." + mDelFlgCol + " = b'0' " + " and " +
		// mItemTable + "."
		// + mDelFlgCol + " = b'0' ";
		// wQuery += " order by " + mItemTable + "." + mSortKeyCol;
		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());
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

	// For all categories
	public static List<Integer> getItemIdList(int pBookId, boolean pIncome) {
		List<Integer> wResultList = new ArrayList<Integer>();
		int wRexp = mIncomeRexp;
		if (!pIncome)
			wRexp = mExpenseRexp;

		StringBuilder wQueryBuilder = new StringBuilder().append("select ").append(mItemTable)
				.append(".").append(mItemIdCol);
		wQueryBuilder.append(" from ").append(mItemTable).append(" , ").append(mBookItemTable)
				.append(", ").append(mCategoryTable);
		wQueryBuilder.append(" where ").append(mItemTable).append(".").append(mItemIdCol)
				.append(" = ").append(mBookItemTable).append(".")
				.append(mItemIdCol).append(" and ").append(mCategoryTable).append(".")
				.append(mCategoryIdCol).append(" = ").append(mItemTable)
				.append(".").append(mCategoryIdCol);
		if (pBookId != SystemData.getAllBookInt()) {
			wQueryBuilder.append(" and ").append(mBookItemTable).append(".").append(mBookIdCol)
					.append(" = ").append(pBookId);
		}
		wQueryBuilder.append(" and ").append(mCategoryTable).append(".").append(mCategoryRexpCol)
				.append(" = ").append(wRexp).append(" and ")
				.append(mBookItemTable).append(".").append(mDelFlgCol).append(" = b'0' ")
				.append(" and ").append(mItemTable).append(".")
				.append(mDelFlgCol).append(" = b'0' ");
		wQueryBuilder.append(" order by ").append(mItemTable).append(".").append(mSortKeyCol);

		// String wQuery = "select " + mItemTable + "." + mItemIdCol;
		// wQuery += " from " + mItemTable + " , " + mBookItemTable + ", " +
		// mCategoryTable;
		// wQuery += " where " + mItemTable + "." + mItemIdCol + " = " +
		// mBookItemTable + "."
		// + mItemIdCol + " and " + mCategoryTable + "." + mCategoryIdCol +
		// " = " + mItemTable
		// + "." + mCategoryIdCol;
		// if (pBookId != SystemData.getAllBookInt()) {
		// wQuery += " and " + mBookItemTable + "." + mBookIdCol + " = " +
		// pBookId;
		// }
		// wQuery += " and " + mCategoryTable + "." + mCategoryRexpCol + " = " +
		// wRexp + " and "
		// + mBookItemTable + "." + mDelFlgCol + " = b'0' " + " and " +
		// mItemTable + "."
		// + mDelFlgCol + " = b'0' ";
		// wQuery += " order by " + mItemTable + "." + mSortKeyCol;
		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());
		try {
			while (wResultSet.next()) {
				wResultList.add(wResultSet.getInt(mItemIdCol));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wResultList;
	}

	public static List<Integer> getItemIdList(int pBookId, int pCategoryId) {
		List<Integer> wResultList = new ArrayList<Integer>();

		StringBuilder wQueryBuilder = new StringBuilder().append("select ").append(mItemTable)
				.append(".").append(mItemIdCol);
		wQueryBuilder.append(" from ").append(mItemTable).append(" , ").append(mBookItemTable)
				.append(", ").append(mCategoryTable);
		wQueryBuilder.append(" where ").append(mItemTable).append(".").append(mItemIdCol)
				.append(" = ").append(mBookItemTable).append(".")
				.append(mItemIdCol).append(" and ").append(mCategoryTable).append(".")
				.append(mCategoryIdCol).append(" = ").append(mItemTable)
				.append(".").append(mCategoryIdCol).append(" and ").append(mBookItemTable)
				.append(".").append(mBookIdCol).append(" = ").append(pBookId);
		if (pCategoryId > SystemData.getUndefinedInt()) {
			wQueryBuilder.append(" and ").append(mCategoryTable).append(".").append(mCategoryIdCol)
					.append(" = ").append(pCategoryId);
		}
		wQueryBuilder.append(" and ").append(mBookItemTable).append(".").append(mDelFlgCol)
				.append(" = b'0' ").append(" and ").append(mItemTable)
				.append(".").append(mDelFlgCol).append(" = b'0' ");
		wQueryBuilder.append(" order by ").append(mItemTable).append(".").append(mSortKeyCol);
		// String wQuery = "select " + mItemTable + "." + mItemIdCol;
		// wQuery += " from " + mItemTable + " , " + mBookItemTable + ", " +
		// mCategoryTable;
		// wQuery += " where " + mItemTable + "." + mItemIdCol + " = " +
		// mBookItemTable + "."
		// + mItemIdCol + " and " + mCategoryTable + "." + mCategoryIdCol +
		// " = " + mItemTable
		// + "." + mCategoryIdCol + " and " + mBookItemTable + "." + mBookIdCol
		// + " = "
		// + pBookId;
		// if (pCategoryId > SystemData.getUndefinedInt())
		// wQuery += " and " + mCategoryTable + "." + mCategoryIdCol + " = " +
		// pCategoryId;
		// wQuery += " and " + mBookItemTable + "." + mDelFlgCol + " = b'0' " +
		// " and " + mItemTable
		// + "." + mDelFlgCol + " = b'0' ";
		// wQuery += " order by " + mItemTable + "." + mSortKeyCol;
		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());
		try {
			while (wResultSet.next()) {
				wResultList.add(wResultSet.getInt(mItemIdCol));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wResultList;
	}

	public static List<Integer> getCategoryIdList(int pBookId, boolean pIncome) {
		List<Integer> wResultList = new ArrayList<Integer>();
		int wRexp = mIncomeRexp;
		if (!pIncome)
			wRexp = mExpenseRexp;

		StringBuilder wQueryBuilder = new StringBuilder().append("select count( ")
				.append(mItemTable).append(".")
				.append(mItemNameCol).append(" ), ")
				.append(mCategoryTable).append(".").append(mCategoryIdCol);
		wQueryBuilder.append(" from ").append(mItemTable).append(", ").append(mBookItemTable)
				.append(", ").append(mCategoryTable);
		wQueryBuilder.append(" where ").append(mItemTable).append(".").append(mItemIdCol)
				.append(" = ")
				.append(mBookItemTable).append(".")
				.append(mItemIdCol).append(" and ").append(mCategoryTable).append(".")
				.append(mCategoryIdCol).append(" = ").append(mItemTable)
				.append(".").append(mCategoryIdCol);
		if (pBookId != SystemData.getAllBookInt()) {
			wQueryBuilder.append(" and ").append(mBookItemTable).append(".").append(mBookIdCol)
					.append(" = ").append(pBookId);
		}
		wQueryBuilder.append(" and ").append(mCategoryTable).append(".").append(mCategoryRexpCol)
				.append(" = ").append(wRexp).append(" and ")
				.append(mBookItemTable).append(".").append(mDelFlgCol).append(" = b'0' ")
				.append(" and ").append(mItemTable).append(".")
				.append(mDelFlgCol).append(" = b'0' ");
		wQueryBuilder.append(" group by ").append(mCategoryTable).append(".")
				.append(mCategoryNameCol);
		wQueryBuilder.append(" order by ").append(mCategoryTable).append(".").append(mSortKeyCol);

		// String wQuery = "select count( " + mItemTable + "." + mItemNameCol +
		// " ), "
		// + mCategoryTable + "." + mCategoryIdCol;
		// wQuery += " from " + mItemTable + ", " + mBookItemTable + ", " +
		// mCategoryTable;
		// wQuery += " where " + mItemTable + "." + mItemIdCol + " = " +
		// mBookItemTable + "."
		// + mItemIdCol + " and " + mCategoryTable + "." + mCategoryIdCol +
		// " = " + mItemTable
		// + "." + mCategoryIdCol;
		// if (pBookId != SystemData.getAllBookInt()) {
		// wQuery += " and " + mBookItemTable + "." + mBookIdCol + " = " +
		// pBookId;
		// }
		// wQuery += " and " + mCategoryTable + "." + mCategoryRexpCol + " = " +
		// wRexp + " and "
		// + mBookItemTable + "." + mDelFlgCol + " = b'0' " + " and " +
		// mItemTable + "."
		// + mDelFlgCol + " = b'0' ";
		// wQuery += " group by " + mCategoryTable + "." + mCategoryNameCol;
		// wQuery += " order by " + mCategoryTable + "." + mSortKeyCol;

		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());
		try {
			while (wResultSet.next()) {
				wResultList.add(wResultSet.getInt(mCategoryIdCol));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wResultList;
	}

	// 設定時に使用
	public static Map<Integer, String> getAllCategoryNameMap(boolean pIncome) {
		Map<Integer, String> wResultMap = new LinkedHashMap<Integer, String>();
		int wRexp = mIncomeRexp;
		if (!pIncome) {
			wRexp = mExpenseRexp;
		}

		StringBuilder wQueryBuilder = new StringBuilder().append("select ").append(mCategoryIdCol)
				.append(", ").append(mCategoryNameCol);
		wQueryBuilder.append(" from ").append(mCategoryTable);
		wQueryBuilder.append(" where ").append(mCategoryRexpCol).append(" = ").append(wRexp)
				.append(" and ").append(mDelFlgCol).append(" = b'0' ")
				.append(" and ").append(mSortKeyCol).append(" > ").append(0);
		wQueryBuilder.append(" order by ").append(mSortKeyCol);

		// String wQuery = "select " + mCategoryIdCol + ", " + mCategoryNameCol;
		// wQuery += " from " + mCategoryTable;
		// wQuery += " where " + mCategoryRexpCol + " = " + wRexp + " and " +
		// mDelFlgCol + " = b'0' "
		// + " and " + mSortKeyCol + " > " + 0;
		// wQuery += " order by " + mSortKeyCol;
		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());
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
		StringBuilder wQueryBuilder = new StringBuilder().append("select ")
				.append(mCategoryRexpCol);
		wQueryBuilder.append(" from ").append(mCategoryTable);
		wQueryBuilder.append(" where ").append(mCategoryIdCol).append(" = ").append(pCategoryId)
				.append(" and ").append(mDelFlgCol).append(" = b'0' ");

		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());
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

	public static void insertNewRecord(RecordTableItem pRecord) {
		String wNote = getNoteStringWithEscape(pRecord.getNote());

		String wQueryNote1 = "";
		String wQueryNote2 = "";

		if (!"".equals(wNote)) {
			wQueryNote1 = "," + mNoteNameCol;
			wQueryNote2 = ",'" + wNote + "'";
		}

		if (pRecord.getFrequency() == 0) {
			String wQueryBase1 = new StringBuilder().append("insert into ").append(mActTable)
					.append(" ( ").append(mBookIdCol).append(",").append(mItemIdCol)
					.append(",").append(mActDtCol).append(",").append(mActIncomeCol)
					.append(",").append(mActExpenseCol).toString();
			String wQueryBase2 = new StringBuilder().append(" values(").append(pRecord.getBookId())
					.append(",").append(pRecord.getItemId()).append(",")
					.append(getDateStrings(pRecord.getDate())).append(",")
					.append(pRecord.getIncome())
					.append(",").append(pRecord.getExpense()).toString();
			String wQuery = new StringBuilder().append(wQueryBase1).append(wQueryNote1)
					.append(") ")
					.append(wQueryBase2).append(wQueryNote2).append(")").toString();
			// String wQueryBase1 = "insert into  " + mActTable + " ( " +
			// mBookIdCol + ","
			// + mItemIdCol
			// + "," + mActDtCol + "," + mActIncomeCol + "," + mActExpenseCol;
			// String wQueryBase2 = " values(" + pRecord.getBookId() + "," +
			// pRecord.getItemId() + ","
			// + getDateStrings(pRecord.getDate()) + "," + pRecord.getIncome() +
			// ","
			// + pRecord.getExpense();
			// String wQuery = wQueryBase1 + wQueryNote1 + ") " + wQueryBase2 +
			// wQueryNote2 + ")";
			// System.out.println(wQuery);
			mDbAccess.executeUpdate(wQuery);
		} else if (pRecord.getFrequency() > 0) {
			int wGroupId = getNewGroupId();
			String wQueryBase1 = new StringBuilder().append("insert into ").append(mActTable)
					.append(" ( ").append(mBookIdCol).append(",").append(mItemIdCol)
					.append(",").append(mActDtCol).append(",").append(mActIncomeCol).append(",")
					.append(mActExpenseCol).append(",").append(mGroupIdCol).append(",")
					.append(mActFreqCol).toString();

			for (int i = 0; i < pRecord.getFrequency() + 1; i++) {
				Calendar wCal = pRecord.getCal();
				wCal.add(Calendar.MONTH, +i);
				String wDate = getDateStrings(wCal.getTime());

				String wQueryBase2 = new StringBuilder().append(" values(")
						.append(pRecord.getBookId())
						.append(",").append(pRecord.getItemId()).append(",")
						.append(wDate).append(",").append(pRecord.getIncome()).append(",")
						.append(pRecord.getExpense()).append(",")
						.append(wGroupId).append(",").append((pRecord.getFrequency() - i))
						.toString();

				String wQuery = new StringBuilder().append(wQueryBase1).append(wQueryNote1)
						.append(") ").append(wQueryBase2).append(wQueryNote2).append(")")
						.toString();
				// System.out.println(wQuery);
				mDbAccess.executeUpdate(wQuery);
			}
		}

		// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
		if (!"".equals(wNote)) {
			updateNoteTable(pRecord.getItemId(), wNote);
		}

	}

	private static boolean groupShouldBeChanged(RecordTableItem pBeforeRecord,
			RecordTableItem pAfterRecord) {
		if (pBeforeRecord.getGroupId() == 0 && pAfterRecord.getFrequency() > 0)
			return true;
		return pAfterRecord.getYear() != pBeforeRecord.getYear()
				|| pAfterRecord.getMonth() != pBeforeRecord.getMonth()
				|| pAfterRecord.getBookId() != pBeforeRecord.getBookId()
				|| pAfterRecord.getItemId() != pBeforeRecord.getItemId();
	}

	public static void updateRecord(RecordTableItem pBeforeRecord, RecordTableItem pAfterRecord) {
		Assert.isTrue(pAfterRecord.getId() == SystemData.getUndefinedInt());

		String wNote = getNoteStringWithEscape(pAfterRecord.getNote());
		String wDate = getDateStrings(pAfterRecord.getDate());
		String wQuery = "";

		// 　ともに繰り返し0ならUpdateのみ
		if (pAfterRecord.getFrequency() == 0 && pBeforeRecord.getGroupId() == 0) {
			wQuery = new StringBuilder().append("update ").append(mActTable).append(" set ")
					.append(mBookIdCol).append(" = ").append(pAfterRecord.getBookId()).append(", ")
					.append(mItemIdCol).append(" = ").append(pAfterRecord.getItemId()).append(", ")
					.append(mActDtCol).append(" = ").append(wDate).append(", ")
					.append(mActIncomeCol).append(" = ").append(pAfterRecord.getIncome())
					.append(", ").append(mActExpenseCol).append(" = ")
					.append(pAfterRecord.getExpense()).append(", ").append(mNoteNameCol)
					.append(" = '").append(wNote).append("' ").append(" where ").append(mActIdCol)
					.append(" = ").append(pBeforeRecord.getId()).toString();
			// wQuery = "update " + mActTable + " set " + mBookIdCol + " = "
			// + pAfterRecord.getBookId() + ", " + mItemIdCol + " = "
			// + pAfterRecord.getItemId() + ", " + mActDtCol + " = " + wDate +
			// ", "
			// + mActIncomeCol + " = " + pAfterRecord.getIncome() + ", " +
			// mActExpenseCol
			// + " = " + pAfterRecord.getExpense() + ", " + mNoteNameCol +
			// " = '" + wNote
			// + "' " + " where " + mActIdCol + " = " + pBeforeRecord.getId();
			// System.out.println(wQuery);
			mDbAccess.executeUpdate(wQuery);

		} else {
			// どちらかが繰り返しありなら既存レコードを削除して新規追加

			// 既存のレコードを削除
			deleteRecord(pBeforeRecord);

			int wGroupId;
			if (pBeforeRecord.getGroupId() > 0 && pAfterRecord.getFrequency() == 0)
				wGroupId = 0;
			else
				wGroupId = groupShouldBeChanged(pBeforeRecord, pAfterRecord) ? getNewGroupId()
						: pBeforeRecord.getGroupId();

			// 新規のレコードを追加
			Calendar wCalBase = Calendar.getInstance();
			wCalBase.setTime(pAfterRecord.getDate());
			for (int i = 0; i < pAfterRecord.getFrequency() + 1; i++) {
				Calendar wCal = (Calendar) wCalBase.clone();
				wCal.add(Calendar.MONTH, +i);
				wDate = getDateStrings(wCal.getTime());

				String wQueryBase = new StringBuilder().append("insert into ").append(mActTable)
						.append(" ( ").append(mBookIdCol).append(",")
						.append(mItemIdCol).append(",").append(mActDtCol).append(",")
						.append(mActIncomeCol)
						.append(",").append(mActExpenseCol)
						.append(",").append(mGroupIdCol).append(",").append(mActFreqCol).toString();

				String wQueryValues = new StringBuilder().append(" values(")
						.append(pAfterRecord.getBookId())
						.append(",").append(pAfterRecord.getItemId()).append(",").append(wDate)
						.append(",").append(pAfterRecord.getIncome()).append(",")
						.append(pAfterRecord.getExpense()).append(",").append(wGroupId).append(",")
						.append(pAfterRecord.getFrequency() - i).toString();

				// String wQueryBase = "insert into  " + mActTable + " ( " +
				// mBookIdCol + ","
				// + mItemIdCol + "," + mActDtCol + "," + mActIncomeCol + "," +
				// mActExpenseCol
				// + "," + mGroupIdCol + "," + mActFreqCol;
				//
				// String wQueryValues = " values(" + pAfterRecord.getBookId() +
				// ","
				// + pAfterRecord.getItemId() + "," + wDate + "," +
				// pAfterRecord.getIncome()
				// + "," + pAfterRecord.getExpense() + "," + wGroupId + ","
				// + (pAfterRecord.getFrequency() - i);

				if (!"".equals(wNote))
					wQuery = new StringBuilder().append(wQueryBase).append(",")
							.append(mNoteNameCol)
							.append(")").append(wQueryValues).append(",'").append(wNote)
							.append("')").toString();
				else
					wQuery = new StringBuilder().append(wQueryBase).append(") ")
							.append(wQueryValues)
							.append(")").toString();

				// System.out.println(wQuery);

				mDbAccess.executeUpdate(wQuery);
			}

			// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
			if (!"".equals(wNote))
				updateNoteTable(pAfterRecord.getItemId(), wNote);
		}
	}

	public static void insertNewMoveRecord(RecordTableItemForMove pMoveItem) {

		String wNote = getNoteStringWithEscape(pMoveItem.getNote());
		int wGroupId = getNewGroupId();

		String wQueryBase = new StringBuilder().append("insert into ").append(mActTable)
				.append(" ( ").append(mBookIdCol).append(",").append(mItemIdCol)
				.append(",").append(mActIncomeCol).append(",").append(mActExpenseCol)
				.append(",").append(mGroupIdCol).append(",").append(mActDtCol).toString();
		String wQueryFromValues = new StringBuilder().append(" values(")
				.append(pMoveItem.getFromBookId())
				.append(",").append(getMoveExpenseItemId()).append(",'0',")
				.append(pMoveItem.getValue())
				.append(",").append(wGroupId).toString();
		String wQueryToValues = new StringBuilder().append(" values(")
				.append(pMoveItem.getToBookId())
				.append(",").append(getMoveIncomeItemId()).append(",").append(pMoveItem.getValue())
				.append(",'0',").append(wGroupId).toString();

		String wQueryNote1 = "";
		String wQueryNote2 = "";
		String wQueryFrom = "";
		String wQueryTo = "";

		if (!"".equals(wNote)) {
			wQueryNote1 = "," + mNoteNameCol;
			wQueryNote2 = ",'" + wNote + "'";
		}

		if (pMoveItem.getFrequency() == 0) {
			String wDate = getDateStrings(pMoveItem.getDate());
			wQueryFrom = new StringBuilder().append(wQueryBase).append(wQueryNote1).append(") ")
					.append(wQueryFromValues).append(", ").append(wDate).append(wQueryNote2)
					.append(")").toString();
			// System.out.println(wQueryFrom);
			mDbAccess.executeUpdate(wQueryFrom);
			wQueryTo = new StringBuilder().append(wQueryBase).append(wQueryNote1).append(") ")
					.append(wQueryToValues).append(", ").append(wDate).append(wQueryNote2)
					.append(")").toString();
			// System.out.println(wQueryTo);
			mDbAccess.executeUpdate(wQueryTo);
		} else { // pFrequency > 0

			String wQueryFreq = "," + mActFreqCol;
			for (int i = 0; i < pMoveItem.getFrequency() + 1; i++) {
				Calendar wCal = pMoveItem.getCal();
				wCal.add(Calendar.MONTH, +i);
				String wDate = getDateStrings(wCal.getTime());

				wQueryFrom = new StringBuilder().append(wQueryBase).append(wQueryNote1)
						.append(wQueryFreq)
						.append(") ").append(wQueryFromValues).append(", ")
						.append(wDate).append(wQueryNote2).append(",")
						.append(pMoveItem.getFrequency() - i).append(")").toString();
				// System.out.println(wQueryFrom);
				mDbAccess.executeUpdate(wQueryFrom);
				wQueryTo = new StringBuilder().append(wQueryBase).append(wQueryNote1)
						.append(wQueryFreq)
						.append(") ").append(wQueryToValues).append(", ")
						.append(wDate).append(wQueryNote2).append(",")
						.append(pMoveItem.getFrequency() - i)
						.append(")").toString();
				// System.out.println(wQueryTo);
				mDbAccess.executeUpdate(wQueryTo);
			}

		}

		// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
		if (!"".equals(wNote)) {
			updateNoteTable(getMoveIncomeItemId(), wNote);
		}
	}

	private static boolean groupShouldBeChanged(RecordTableItemForMove pBeforeItem,
			RecordTableItemForMove pAfterItem) {
		// if (pBeforeItem.getGroupId() == 0 && pAfterItem.getFrequency() > 0)
		// return true;
		return pAfterItem.getYear() != pBeforeItem.getYear()
				|| pAfterItem.getMonth() != pBeforeItem.getMonth()
				|| pAfterItem.getFromBookId() != pBeforeItem.getFromBookId()
				|| pAfterItem.getToBookId() != pBeforeItem.getToBookId();
	}

	public static void updateMoveRecord(RecordTableItemForMove pBeforeItem,
			RecordTableItemForMove pAfterItem) {
		if (pBeforeItem.getGroupId() == 0) {
			MessageDialog.openError(Display.getCurrent().getShells()[0], "Error", "GroupIdが0です");
			return;
		}

		String wNote = getNoteStringWithEscape(pAfterItem.getNote());

		// ともに繰り返し0ならUpdateのみ
		if (pBeforeItem.getFrequency() == 0 && pAfterItem.getFrequency() == 0) {
			String wDate = getDateStrings(pAfterItem.getDate());
			String wQueryFrom = new StringBuilder().append("update ").append(mActTable)
					.append(" set ")
					.append(mBookIdCol).append(" = ").append(pAfterItem.getFromBookId())
					.append(", ")
					.append(mItemIdCol).append(" = ").append(getMoveExpenseItemId()).append(", ")
					.append(mActDtCol).append(" = ").append(wDate).append(", ")
					.append(mActIncomeCol)
					.append(" = ").append("'0', ").append(mActExpenseCol).append(" = ")
					.append(pAfterItem.getValue()).append(", ").append(mNoteNameCol).append(" = '")
					.append(wNote).append("' ").append(" where ").append(mActIdCol).append(" = ")
					.append(pBeforeItem.getFromActId()).toString();
			// System.out.println(wQueryFrom);

			mDbAccess.executeUpdate(wQueryFrom);
			String wQueryTo = new StringBuilder().append("update ").append(mActTable)
					.append(" set ")
					.append(mBookIdCol).append(" = ").append(pAfterItem.getToBookId()).append(", ")
					.append(mItemIdCol).append(" = ").append(getMoveIncomeItemId()).append(", ")
					.append(mActDtCol).append(" = ").append(wDate).append(", ")
					.append(mActIncomeCol)
					.append(" = ").append(pAfterItem.getValue()).append(", ")
					.append(mActExpenseCol)
					.append(" = ").append("'0'").append(", ").append(mNoteNameCol).append(" = '")
					.append(wNote).append("' ").append(" where ").append(mActIdCol).append(" = ")
					.append(pBeforeItem.getToActId()).toString();
			// System.out.println(wQueryTo);
			mDbAccess.executeUpdate(wQueryTo);

		} else {
			// どちらかが繰り返しありなら既存レコードを削除して新規追加

			// 既存のレコードを削除
			deleteGroupRecord(pBeforeItem.getDate(), pBeforeItem.getGroupId());

			int wGroupId = groupShouldBeChanged(pBeforeItem, pAfterItem) ? getNewGroupId()
					: pBeforeItem.getGroupId();

			// 新規のレコードを追加
			for (int i = 0; i < pAfterItem.getFrequency() + 1; i++) {
				Calendar wCal = pAfterItem.getCal();
				wCal.add(Calendar.MONTH, +i);
				String wDate = getDateStrings(wCal.getTime());

				String wQueryBase = new StringBuilder("insert into ").append(mActTable)
						.append(" ( ")
						.append(mBookIdCol).append(",").append(mItemIdCol).append(",")
						.append(mActDtCol)
						.append(",").append(mActIncomeCol).append(",").append(mActExpenseCol)
						.append(",").append(mGroupIdCol).append(",").append(mActFreqCol).toString();

				String wQueryFromValue = new StringBuilder(" values(")
						.append(pAfterItem.getFromBookId())
						.append(",").append(getMoveExpenseItemId()).append(",").append(wDate)
						.append(",")
						.append("'0'").append(",").append(pAfterItem.getValue()).append(",")
						.append(wGroupId).append(",").append(pAfterItem.getFrequency() - i)
						.toString();

				String wQueryToValue = new StringBuilder(" values(")
						.append(pAfterItem.getToBookId())
						.append(",").append(getMoveIncomeItemId()).append(",").append(wDate)
						.append(",")
						.append(pAfterItem.getValue()).append(",").append("'0'").append(",")
						.append(wGroupId).append(",").append(pAfterItem.getFrequency() - i)
						.toString();

				String wQueryNote1 = ")";
				String wQueryNote2 = ")";

				if (!"".equals(wNote)) {
					wQueryNote1 = "," + mNoteNameCol + ")";
					wQueryNote2 = ",'" + wNote + "')";
				}

				String wQueryFrom = new StringBuilder(wQueryBase).append(wQueryNote1)
						.append(wQueryFromValue).append(wQueryNote2).toString();
				// System.out.println(wQueryFrom);
				mDbAccess.executeUpdate(wQueryFrom);

				String wQueryTo = new StringBuilder(wQueryBase).append(wQueryNote1)
						.append(wQueryToValue)
						.append(wQueryNote2).toString();
				// System.out.println(wQueryTo);
				mDbAccess.executeUpdate(wQueryTo);
			}

			// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
			if (!"".equals(wNote))
				updateNoteTable(getMoveIncomeItemId(), wNote);
		}
	}

	public static void deleteRecord(RecordTableItem pRecordTableItem) {
		int wGroupId = pRecordTableItem.getGroupId();

		if (wGroupId > 0) {
			// 複数レコード（同一GroupId,対象日付以降）
			deleteGroupRecord(pRecordTableItem.getDate(), wGroupId);
			return;
		}
		// 単一レコードの削除
		String wQuery = new StringBuilder("delete from ").append(mActTable).append(" where ")
				.append(mActIdCol).append(" = ").append(pRecordTableItem.getId()).toString();
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQuery);
	}

	// 複数レコード（同一GroupId,対象日付以降）削除
	private static void deleteGroupRecord(Date pDate, int pGroupId) {
		if (pGroupId == 0) {
			MessageDialog.openError(Display.getCurrent().getShells()[0], "Error", "GroupIdが0です");
			return;
		}
		String wQuery = new StringBuilder("delete from ").append(mActTable).append(" where ")
				.append(mGroupIdCol).append(" = ").append(pGroupId).append(" and ")
				.append(mActDtCol)
				.append(" >= ").append(getDateStrings(pDate)).toString();
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQuery);
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

		String wQuery = new StringBuilder("select ").append(mActIdCol).append(" from ")
				.append(mActTable).append(" where ").append(mGroupIdCol)
				.append(" = ").append(wGroupId).append(" and ").append(mActDtCol).append(" = ")
				.append(wDate).append(" and ").append(mActIdCol).append(" <> ").append(wActId)
				.toString();

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

	private static List<SummaryTableItem> getSummaryTableItemListOriginalForAllBooks(
			DateRange pDateRange) {
		List<SummaryTableItem> wSummaryTableItemList = new ArrayList<SummaryTableItem>();

		// 立替残高（借入残高）
		int wTempBalance = getTempBalance(pDateRange.getEndDate());

		// みかけ残高
		int wAppearedBalance = getBalance(pDateRange.getEndDate(), mAllBookId, true);
		// wActualBalance = wAppearedBalance - wTempBalance;

		// みかけ収支（Total）
		IncomeExpense wTotalAppearedIncomeExpense = getAppearedIncomeExpense(pDateRange, mAllBookId);

		// 立替収支
		int wTempProfit = getTotalTempProfit(pDateRange);

		// 特別収支
		int wSpecialProfit = getTotalSpecialProfit(pDateRange);

		wSummaryTableItemList.add(SummaryTableItemFactory.createOriginal("営業収支",
				wTotalAppearedIncomeExpense.getProfit() - wTempProfit - wSpecialProfit));
		wSummaryTableItemList.add(SummaryTableItemFactory.createOriginal("実質収支",
				wTotalAppearedIncomeExpense.getProfit() - wTempProfit));
		wSummaryTableItemList.add(SummaryTableItemFactory.createOriginal("実質残高", wAppearedBalance
				- wTempBalance));
		wSummaryTableItemList.add(SummaryTableItemFactory.createOriginal("借入残高", wTempBalance));

		return wSummaryTableItemList;
	}

	private static Map<Integer, List<SummaryTableItem>> getSummaryTableItemsOfCategory(int pBookId,
			DateRange pDateRange, Map<Integer, List<SummaryTableItem>> pSummaryTableMap,
			boolean pIncome) {

		final String wTargetColumn = pIncome ? mActIncomeCol : mActExpenseCol;

		StringBuilder wQueryBuilder = new StringBuilder("select ").append(mCategoryTable)
				.append(".")
				.append(mCategoryIdCol).append(", ").append(mCategoryTable).append(".")
				.append(mCategoryNameCol).append(", ").append(" sum(").append(wTargetColumn)
				.append(") as ").append(wTargetColumn).append(" from ").append(mActTable)
				.append(", ").append(mItemTable).append(", ").append(mCategoryTable)
				.append(" where ")
				.append(mActTable).append(".").append(mItemIdCol).append(" = ").append(mItemTable)
				.append(".").append(mItemIdCol).append(" and ").append(mCategoryTable).append(".")
				.append(mCategoryIdCol).append(" = ").append(mItemTable).append(".")
				.append(mCategoryIdCol)
				.append(" and ").append(mActDtCol).append(" between ")
				.append(getDateStrings(pDateRange.getStartDate())).append(" and ")
				.append(getDateStrings(pDateRange.getEndDate())).append(" and ").append(mActTable)
				.append(".").append(mDelFlgCol).append(" = b'0'").append(" and ")
				.append(wTargetColumn)
				.append(" > 0");
		if (pBookId != mAllBookId) {
			wQueryBuilder.append(" and ").append(mActTable).append(".").append(mBookIdCol)
					.append(" = ")
					.append(pBookId);
		}
		wQueryBuilder.append(" group by ").append(mCategoryTable).append(".")
				.append(mCategoryIdCol)
				.append(" order by ").append(mCategoryTable).append(".").append(mCategoryRexpCol)
				.append(", ").append(mCategoryTable).append(".").append(mSortKeyCol);
		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());
		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryTable + "." + mCategoryIdCol);
				if (pBookId == mAllBookId && (DbUtil.isMoveCategory(wCategoryId)))
					continue;
				String wCategoryName = wResultSet
						.getString(mCategoryTable + "." + mCategoryNameCol);
				int wValue = wResultSet.getInt(wTargetColumn);
				List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();
				wList.add(SummaryTableItemFactory
						.createCategory(wCategoryName, wValue, wCategoryId));
				pSummaryTableMap.put(wCategoryId, wList);
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return pSummaryTableMap;
	}

	/**
	 * 
	 * @param pBookId
	 * @param pDateRange
	 * @param pSummaryTableMap
	 * @param pIncome
	 * @return
	 */
	private static Map<Integer, List<SummaryTableItem>> getSummaryTableItemsOfNormalItem(
			int pBookId, DateRange pDateRange,
			Map<Integer, List<SummaryTableItem>> pSummaryTableMap, boolean pIncome) {

		String wTargetColumn = pIncome ? mActIncomeCol : mActExpenseCol;

		StringBuilder wQueryBuilder = new StringBuilder("select ").append(mCategoryTable)
				.append(".")
				.append(mCategoryIdCol).append(", ").append(mActTable).append(".")
				.append(mItemIdCol)
				.append(", ").append(mItemTable).append(".").append(mItemNameCol).append(", sum(")
				.append(wTargetColumn).append(") as ").append(wTargetColumn).append(" from ")
				.append(mActTable).append(", ").append(mItemTable).append(", ")
				.append(mCategoryTable)
				.append(" where ").append(mActTable).append(".").append(mItemIdCol).append(" = ")
				.append(mItemTable).append(".").append(mItemIdCol).append(" and ")
				.append(mCategoryTable)
				.append(".").append(mCategoryIdCol).append(" = ").append(mItemTable).append(".")
				.append(mCategoryIdCol).append(" and ").append(mActDtCol).append(" between ")
				.append(getDateStrings(pDateRange.getStartDate())).append(" and ")
				.append(getDateStrings(pDateRange.getEndDate())).append(" and ").append(mActTable)
				.append(".").append(mDelFlgCol).append(" = b'0'").append(" and ").append(mActTable)
				.append(".").append(wTargetColumn).append(" > 0");
		if (pBookId != mAllBookId) {
			wQueryBuilder.append(" and ").append(mActTable).append(".").append(mBookIdCol)
					.append(" = ")
					.append(pBookId);
		}
		wQueryBuilder.append(" group by ").append(mActTable).append(".").append(mItemIdCol)
				.append(" order by ").append(mCategoryTable).append(".").append(mCategoryRexpCol)
				.append(", ").append(mCategoryTable).append(".").append(mSortKeyCol).append(", ")
				.append(mItemTable).append(".").append(mSortKeyCol);

		// System.out.println(wQuery);
		ResultSet wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());

		try {
			while (wResultSet.next()) {
				int wCategoryId = wResultSet.getInt(mCategoryTable + "." + mCategoryIdCol);

				if (pBookId == mAllBookId && (isMoveCategory(wCategoryId)))
					continue;

				List<SummaryTableItem> wList = pSummaryTableMap.get(wCategoryId);
				int wItemId = wResultSet.getInt(mActTable + "." + mItemIdCol);
				String wItemName = wResultSet.getString(mItemTable + "." + mItemNameCol);
				int wValue = wResultSet.getInt(wTargetColumn);
				wList.add(SummaryTableItemFactory.createItem(wItemName, wValue, wItemId));
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return pSummaryTableMap;
	}

	public static SummaryTableItem[] getSummaryTableItems(int pBookId, DateRange pDateRange) {

		// 全帳簿共通の項目（特殊収支）
		List<SummaryTableItem> wSummaryTableItemList = getSummaryTableItemListOriginalForAllBooks(pDateRange);

		// みかけ収支（各Book）
		IncomeExpense wBookAppearedIncomeExpense = getAppearedIncomeExpense(pDateRange, pBookId);

		wSummaryTableItemList.add(SummaryTableItemFactory.createAppearedProfit("みかけ収支",
				wBookAppearedIncomeExpense.getProfit()));

		wSummaryTableItemList.add(SummaryTableItemFactory.createAppearedIncome(mAppearedIncomeName,
				wBookAppearedIncomeExpense.getIncome()));

		// リストへは追加（収入項目の後ろ）
		SummaryTableItem wAppearedExpenseItem = SummaryTableItemFactory.createAppearedExpense(
				mAppearedExpenseName, wBookAppearedIncomeExpense.getExpense());

		// CategoryId-SummaryTableItemList
		Map<Integer, List<SummaryTableItem>> wSummaryTableMap = new LinkedHashMap<Integer, List<SummaryTableItem>>();

		// Income-Category
		wSummaryTableMap = getSummaryTableItemsOfCategory(pBookId, pDateRange, wSummaryTableMap,
				true);

		// Income-Item
		wSummaryTableMap = getSummaryTableItemsOfNormalItem(pBookId, pDateRange, wSummaryTableMap,
				true);

		// リストへIncome全結果を挿入
		for (Map.Entry<Integer, List<SummaryTableItem>> entry : wSummaryTableMap.entrySet())
			wSummaryTableItemList.addAll(entry.getValue());
		wSummaryTableMap.clear();

		// みかけ支出を追加
		wSummaryTableItemList.add(wAppearedExpenseItem);

		// Expense-Category
		wSummaryTableMap = getSummaryTableItemsOfCategory(pBookId, pDateRange, wSummaryTableMap,
				false);

		// Expense-Item
		wSummaryTableMap = getSummaryTableItemsOfNormalItem(pBookId, pDateRange, wSummaryTableMap,
				false);

		// リストへIncome全結果を挿入
		for (Map.Entry<Integer, List<SummaryTableItem>> entry : wSummaryTableMap.entrySet())
			wSummaryTableItemList.addAll(entry.getValue());

		return wSummaryTableItemList.toArray(new SummaryTableItem[0]);
	}

	private static String getQueryStringForAnnualSummaryTableItems(int pBookId,
			AnnualDateRange pAnnualDateRange, boolean pItem) {

		String wTotalStartDateString = getDateStrings(pAnnualDateRange.getStartDate());
		String wTotalEndDateString = getDateStrings(pAnnualDateRange.getEndDate());

		StringBuilder wQueryBuilder = new StringBuilder("select ").append(mCategoryTable)
				.append(".").append(mCategoryRexpCol).append(", ").append(mCategoryTable)
				.append(".").append(mCategoryIdCol).append(", ").append(mCategoryTable).append(".")
				.append(mSortKeyCol);
		if (pItem)
			wQueryBuilder.append(", ").append(mItemTable).append(".").append(mSortKeyCol)
					.append(", ")
					.append(mItemTable).append(".").append(mItemIdCol).append(", ")
					.append(mItemTable)
					.append(".").append(mItemNameCol);
		else
			wQueryBuilder.append(", ").append(mCategoryTable).append(".").append(mCategoryNameCol);

		for (int i = 0; i < pAnnualDateRange.size(); i++) {
			DateRange wDateRange = pAnnualDateRange.getDateRangeList().get(i);
			String wStartDateString = getDateStrings(wDateRange.getStartDate());
			String wEndDateString = getDateStrings(wDateRange.getEndDate());
			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ")
					.append(wStartDateString).append(" and ").append(wEndDateString)
					.append(" then ")
					.append(mActIncomeCol).append(" end),0) '").append(mActIncomeCol)
					.append(mPeriodName)
					.append(i).append("'");
			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ")
					.append(wStartDateString).append(" and ").append(wEndDateString)
					.append(" then ")
					.append(mActExpenseCol).append(" end),0) '").append(mActExpenseCol)
					.append(mPeriodName).append(i).append("'");
		}

		wQueryBuilder.append(" from ").append(mActTable).append(", ").append(mItemTable)
				.append(", ").append(mCategoryTable);
		wQueryBuilder.append(" where ").append(mActTable).append(".").append(mItemIdCol)
				.append(" = ").append(mItemTable).append(".").append(mItemIdCol);
		wQueryBuilder.append(" and ").append(mItemTable).append(".").append(mCategoryIdCol)
				.append(" = ").append(mCategoryTable).append(".").append(mCategoryIdCol);
		wQueryBuilder.append(" and ").append(mActIncomeCol).append(" + ").append(mActExpenseCol)
				.append(" > 0 ");
		wQueryBuilder.append(" and ").append(mActTable).append(".").append(mDelFlgCol)
				.append(" = b'0'");
		wQueryBuilder.append(" and ").append(mActDtCol).append(" between ")
				.append(wTotalStartDateString).append(" and ").append(wTotalEndDateString);

		if (pBookId == mAllBookId) {
			wQueryBuilder.append(" and ").append(mItemTable).append(".").append(mMoveFlgCol)
					.append(" = b'0' ");
		} else {
			wQueryBuilder.append(" and ").append(mActTable).append(".").append(mBookIdCol)
					.append(" = ").append(pBookId);
		}

		if (pItem) {
			wQueryBuilder.append(" group by ").append(mItemTable).append(".").append(mItemIdCol)
					.append(" with rollup");
		} else {
			wQueryBuilder.append(" group by ").append(mCategoryTable).append(".")
					.append(mCategoryIdCol).append(" with rollup");
		}

		return wQueryBuilder.toString();

	}

	private static Map<Integer, List<SummaryTableItem>> createSummaryTableItemsInCaseOfNoRecord(
			Map<Integer, List<SummaryTableItem>> pSummaryTableItemListMap,
			AnnualDateRange pAnnualDateRange) {
		List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();
		List<SummaryTableItem> wListIncome = new ArrayList<SummaryTableItem>();
		List<SummaryTableItem> wListExpense = new ArrayList<SummaryTableItem>();

		for (int i = 0; i < pAnnualDateRange.size(); i++) {
			wList.add(SummaryTableItemFactory.createAppearedProfit("総収支", 0));
			wListIncome.add(SummaryTableItemFactory.createAppearedIncome("総収入", 0));
			wListExpense.add(SummaryTableItemFactory.createAppearedExpense("総支出", 0));
		}
		pSummaryTableItemListMap.put(0, wList);
		pSummaryTableItemListMap.put(1, wListIncome);
		int wKey = mExpenseRexp * 1000000;
		pSummaryTableItemListMap.put(wKey, wListExpense);

		return pSummaryTableItemListMap;
	}

	private static void addAnnualSummaryTableItemsForSumAve(AnnualDateRange pAnnualDateRange,
			ResultSet pResultSet, Map<Integer, List<SummaryTableItem>> pSummaryTableItemListMap) {

		List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();
		List<SummaryTableItem> wListIncome = new ArrayList<SummaryTableItem>();
		List<SummaryTableItem> wListExpense = new ArrayList<SummaryTableItem>();

		try {
			for (int i = 0; i < pAnnualDateRange.size(); i++) {
				int wIncome = pResultSet.getInt(mActIncomeCol + mPeriodName + i);
				int wExpense = pResultSet.getInt(mActExpenseCol + mPeriodName + i);
				if (i == pAnnualDateRange.getAveIndex()) {
					wIncome = wIncome / (i - 1);
					wExpense = wExpense / (i - 1);
				}

				wList.add(SummaryTableItemFactory.createAppearedProfit("総収支", wIncome - wExpense));
				wListIncome.add(SummaryTableItemFactory.createAppearedIncome("総収入", wIncome));
				wListExpense.add(SummaryTableItemFactory.createAppearedExpense("総支出", wExpense));
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		pSummaryTableItemListMap.put(0, wList);
		pSummaryTableItemListMap.put(1, wListIncome);
		int wKey = mExpenseRexp * 1000000;
		pSummaryTableItemListMap.put(wKey, wListExpense);
	}

	private static void addAnnualSummaryTableItemsForNormal(AnnualDateRange pAnnualDateRange,
			ResultSet pResultSet, Map<Integer, List<SummaryTableItem>> pSummaryTableItemListMap,
			boolean pItem, int pId) {

		String wNameKey = pItem ? (mItemTable + "." + mItemNameCol)
				: (mCategoryTable + "." + mCategoryNameCol);
		List<SummaryTableItem> wList = new ArrayList<SummaryTableItem>();

		int wKey = 0;

		try {
			int wRexpDiv = pResultSet.getInt(mCategoryTable + "." + mCategoryRexpCol);
			int wCategorySortKey = pResultSet.getInt(mCategoryTable + "." + mSortKeyCol);
			for (int i = 0; i < pAnnualDateRange.size(); i++) {
				int wIncome = pResultSet.getInt(mActIncomeCol + mPeriodName + i);
				int wExpense = pResultSet.getInt(mActExpenseCol + mPeriodName + i);
				if (i == pAnnualDateRange.getAveIndex()) {
					wIncome = wIncome / (i - 1);
					wExpense = wExpense / (i - 1);
				}
				if (pItem)
					wList.add(SummaryTableItemFactory.createItem(pResultSet.getString(wNameKey),
							(wRexpDiv == mIncomeRexp) ? wIncome : wExpense, pId));
				else
					wList.add(SummaryTableItemFactory.createCategory(
							pResultSet.getString(wNameKey), (wRexpDiv == mIncomeRexp) ? wIncome
									: wExpense, pId));
			}

			wKey = wRexpDiv * 1000000 + wCategorySortKey * 1000
					+ (pItem ? pResultSet.getInt(mItemTable + "." + mSortKeyCol) : 0);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		pSummaryTableItemListMap.put(wKey, wList);
	}

	public static List<SummaryTableItem[]> getAnnualSummaryTableItems(int pBookId,
			AnnualDateRange pAnnualDateRange, boolean pItem) {

		Map<Integer, List<SummaryTableItem>> wSummaryTableItemListMap = new TreeMap<Integer, List<SummaryTableItem>>();

		ResultSet wResultSet = mDbAccess.executeQuery(getQueryStringForAnnualSummaryTableItems(
				pBookId, pAnnualDateRange, pItem));

		try {
			boolean hasResults = true;
			if (!wResultSet.next()) {
				wSummaryTableItemListMap = createSummaryTableItemsInCaseOfNoRecord(
						wSummaryTableItemListMap, pAnnualDateRange);
				hasResults = false;
			}

			while (hasResults) {
				int wId = pItem ? wResultSet.getInt(mItemTable + "." + mItemIdCol) : wResultSet
						.getInt(mCategoryTable + "." + mCategoryIdCol);

				// 集計行
				if (wId == 0)
					addAnnualSummaryTableItemsForSumAve(pAnnualDateRange, wResultSet,
							wSummaryTableItemListMap);
				else
					addAnnualSummaryTableItemsForNormal(pAnnualDateRange, wResultSet,
							wSummaryTableItemListMap, pItem, wId);

				if (!wResultSet.next())
					break;

			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		List<SummaryTableItem[]> wReturnList = new ArrayList<SummaryTableItem[]>(pAnnualDateRange
				.size());

		for (int i = 0; i < pAnnualDateRange.size(); i++) {
			List<SummaryTableItem> wRowList = new ArrayList<SummaryTableItem>();
			for (Map.Entry<Integer, List<SummaryTableItem>> wEntry : wSummaryTableItemListMap
					.entrySet()) {
				List<SummaryTableItem> wColList = wEntry.getValue();
				wRowList.add(wColList.get(i));
			}
			wReturnList.add(wRowList.toArray(new SummaryTableItem[0]));
		}
		return wReturnList;
	}

	private static String getQueryStringForAnnualSummaryTableItemsOriginal(
			AnnualDateRange pAnnualDateRange) {
		String wTotalStartDateString = getDateStrings(pAnnualDateRange.getStartDate());
		StringBuilder wQueryBuilder = new StringBuilder("select COALESCE(sum(case when ")
				.append(mActDtCol).append(" < ").append(wTotalStartDateString).append(" then ")
				.append(mActIncomeCol).append(" - ").append(mActExpenseCol).append(" end),0) ")
				.append(mAppearedBalanceName);
		wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" < ")
				.append(wTotalStartDateString).append(" and (").append(mCategoryTable).append(".")
				.append(mCategoryTempFlgCol).append(" = b'1' ) then ").append(mActIncomeCol)
				.append(" - ")
				.append(mActExpenseCol).append(" end),0) ").append(mTempBalanceName);
		for (int i = 0; i < pAnnualDateRange.size(); i++) {
			DateRange wDateRange = pAnnualDateRange.getDateRangeList().get(i);
			String wStartDateString = getDateStrings(wDateRange.getStartDate());
			String wEndDateString = getDateStrings(wDateRange.getEndDate());
			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ")
					.append(wStartDateString).append(" and ").append(wEndDateString)
					.append(" then ").append(mActIncomeCol).append(" end),0) ")
					.append(mAppearedIncomeName).append(i);
			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ")
					.append(wStartDateString).append(" and ").append(wEndDateString)
					.append(" then ")
					.append(mActExpenseCol).append(" end),0) ").append(mAppearedExpenseName)
					.append(i);

			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ")
					.append(wStartDateString).append(" and ").append(wEndDateString)
					.append(" and ").append(mCategoryTable).append(".")
					.append(mCategorySpecialFlgCol).append(" = b'1' then ").append(mActIncomeCol)
					.append(" end),0) ").append(mSpecialIncomeName).append(i);
			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ")
					.append(wStartDateString).append(" and ").append(wEndDateString)
					.append(" and ")
					.append(mCategoryTable).append(".").append(mCategorySpecialFlgCol)
					.append(" = b'1' then ").append(mActExpenseCol).append(" end),0) ")
					.append(mSpecialExpenseName).append(i);

			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ")
					.append(wStartDateString).append(" and ").append(wEndDateString)
					.append(" and ").append(mCategoryTable).append(".").append(mCategoryTempFlgCol)
					.append(" = b'1' then ").append(mActIncomeCol).append(" end),0) ")
					.append(mTempIncomeName).append(i);
			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ")
					.append(wStartDateString).append(" and ").append(wEndDateString)
					.append(" and ").append(mCategoryTable).append(".").append(mCategoryTempFlgCol)
					.append(" = b'1' then ").append(mActExpenseCol).append(" end),0) ")
					.append(mTempExpenseName).append(i);
		}

		wQueryBuilder.append(" from ").append(mActTable).append(", ").append(mItemTable)
				.append(", ").append(mCategoryTable);
		wQueryBuilder.append(" where ").append(mActTable).append(".").append(mItemIdCol)
				.append(" = ").append(mItemTable).append(".").append(mItemIdCol);
		wQueryBuilder.append(" and ").append(mItemTable).append(".").append(mCategoryIdCol)
				.append(" = ").append(mCategoryTable).append(".").append(mCategoryIdCol);
		wQueryBuilder.append(" and ").append(mActTable).append(".").append(mDelFlgCol)
				.append(" = b'0'");
		wQueryBuilder.append(" and ").append(mItemTable).append(".").append(mMoveFlgCol)
				.append(" = b'0'");
		// System.out.println(wQuery);

		return wQueryBuilder.toString();
	}

	private static IncomeExpenseSummary getIncomeExpenseSummary(AnnualDateRange pAnnualDateRange,
			int pIndex, ResultSet pResultSet) {
		IncomeExpenseSummary wSummary = null;
		try {
			if (pIndex == pAnnualDateRange.getAveIndex()) {
				wSummary = new IncomeExpenseSummary(new IncomeExpense(pResultSet
						.getInt(mAppearedIncomeName + pIndex)
						/ (pIndex - 1), pResultSet.getInt(mAppearedExpenseName + pIndex)
						/ (pIndex - 1)), new IncomeExpense(pResultSet.getInt(mSpecialIncomeName
						+ pIndex)
						/ (pIndex - 1), pResultSet.getInt(mSpecialExpenseName + pIndex)
						/ (pIndex - 1)),
						new IncomeExpense(pResultSet.getInt(mTempIncomeName + pIndex)
								/ (pIndex - 1), pResultSet.getInt(mTempExpenseName + pIndex)
								/ (pIndex - 1)));
			} else {
				wSummary = new IncomeExpenseSummary(new IncomeExpense(pResultSet
						.getInt(mAppearedIncomeName + pIndex), pResultSet
						.getInt(mAppearedExpenseName + pIndex)), new IncomeExpense(pResultSet
						.getInt(mSpecialIncomeName + pIndex), pResultSet.getInt(mSpecialExpenseName
						+ pIndex)), new IncomeExpense(pResultSet.getInt(mTempIncomeName + pIndex),
						pResultSet.getInt(mTempExpenseName + pIndex)));
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wSummary;

	}

	private static void addProfits(List<List<SummaryTableItem>> pSummaryTableItemList, int pIndex,
			IncomeExpenseSummary pSummary) {
		pSummaryTableItemList.get(pIndex).add(
				SummaryTableItemFactory.createOriginal("みかけ収支", pSummary.getAppearedProfit()));
		pSummaryTableItemList.get(pIndex).add(
				SummaryTableItemFactory.createOriginal("営業収支", pSummary.getOperationProfit()));
		pSummaryTableItemList.get(pIndex).add(
				SummaryTableItemFactory.createOriginal("実質収支", pSummary.getActualProfit()));
	}

	private static void addIncomeExpenses(List<List<SummaryTableItem>> pSummaryTableItemList,
			int pIndex, IncomeExpenseSummary pSummary) {
		pSummaryTableItemList.get(pIndex).add(
				SummaryTableItemFactory.createOriginal("営業収入", pSummary.getOperationIncome()));
		pSummaryTableItemList.get(pIndex).add(
				SummaryTableItemFactory.createOriginal("営業支出", pSummary.getOperationExpense()));
		pSummaryTableItemList.get(pIndex).add(
				SummaryTableItemFactory.createOriginal("実質収入", pSummary.getActualIncome()));
		pSummaryTableItemList.get(pIndex).add(
				SummaryTableItemFactory.createOriginal("実質支出", pSummary.getActualExpense()));
		pSummaryTableItemList.get(pIndex).add(
				SummaryTableItemFactory.createOriginal(mAppearedIncomeName, pSummary
						.getAppearedIncome()));
		pSummaryTableItemList.get(pIndex).add(
				SummaryTableItemFactory.createOriginal(mAppearedExpenseName, pSummary
						.getAppearedExpense()));
		pSummaryTableItemList.get(pIndex).add(
				SummaryTableItemFactory.createOriginal(mSpecialIncomeName, pSummary
						.getSpecialIncome()));
		pSummaryTableItemList.get(pIndex).add(
				SummaryTableItemFactory.createOriginal(mSpecialExpenseName, pSummary
						.getSpecialExpense()));
		pSummaryTableItemList.get(pIndex).add(
				SummaryTableItemFactory.createOriginal(mTempIncomeName, pSummary.getTempIncome()));
		pSummaryTableItemList.get(pIndex)
				.add(
						SummaryTableItemFactory.createOriginal(mTempExpenseName, pSummary
								.getTempExpense()));
	}

	// 特殊収支系の年度集計取得
	public static List<SummaryTableItem[]> getAnnualSummaryTableItemsOriginal(
			AnnualDateRange pAnnualDateRange) {

		// 結果を挿入するリスト
		List<List<SummaryTableItem>> wSummaryTableItemList = new ArrayList<List<SummaryTableItem>>(
				pAnnualDateRange.size());
		for (int i = 0; i < pAnnualDateRange.size(); i++)
			wSummaryTableItemList.add(new ArrayList<SummaryTableItem>());

		int wAppearedBalance = getInitialBalance(SystemData.getAllBookInt());
		int wActualBalance = 0;
		int wTempBalance = 0;

		ResultSet wResultSet = mDbAccess
				.executeQuery(getQueryStringForAnnualSummaryTableItemsOriginal(pAnnualDateRange));
		try {
			while (wResultSet.next()) {
				wAppearedBalance += wResultSet.getInt(mAppearedBalanceName);
				wTempBalance += wResultSet.getInt(mTempBalanceName);
				wActualBalance += wAppearedBalance - wTempBalance;

				for (int i = 0; i < pAnnualDateRange.size(); i++) {
					IncomeExpenseSummary wSummary = getIncomeExpenseSummary(pAnnualDateRange, i,
							wResultSet);
					boolean isSumAveIndex = (i == pAnnualDateRange.getSumIndex())
							|| (i == pAnnualDateRange.getAveIndex());
					wSummaryTableItemList.get(i).add(
							SummaryTableItemFactory
									.createOriginal("繰越残高", isSumAveIndex ? SystemData
											.getUndefinedInt() : wAppearedBalance));

					if (!isSumAveIndex) {
						wAppearedBalance += wSummary.getAppearedProfit();
						wActualBalance += wSummary.getActualProfit();
						wTempBalance += wSummary.getTempProfit();
					}

					addProfits(wSummaryTableItemList, i, wSummary);

					wSummaryTableItemList.get(i).add(
							SummaryTableItemFactory.createOriginal("実質残高",
									isSumAveIndex ? SystemData.getUndefinedInt() : wActualBalance));
					wSummaryTableItemList.get(i).add(
							SummaryTableItemFactory
									.createOriginal(mAppearedBalanceName,
											isSumAveIndex ? SystemData.getUndefinedInt()
													: wAppearedBalance));
					wSummaryTableItemList.get(i).add(
							SummaryTableItemFactory.createOriginal(mTempBalanceName,
									isSumAveIndex ? SystemData.getUndefinedInt() : wTempBalance));

					addIncomeExpenses(wSummaryTableItemList, i, wSummary);

				}
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		List<SummaryTableItem[]> wReturnList = new ArrayList<SummaryTableItem[]>(
				wSummaryTableItemList.size());
		for (List<SummaryTableItem> wList : wSummaryTableItemList) {
			wReturnList.add(wList.toArray(new SummaryTableItem[0]));
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
		StringBuilder wQueryBuilder = new StringBuilder("select ").append(mCategoryIdCol)
				.append(", ")
				.append(mCategoryNameCol).append(" from ").append(mCategoryTable);
		wQueryBuilder.append(" where ").append(mDelFlgCol).append(" = b'0' and ")
				.append(mCategoryRexpCol)
				.append(" = ").append(wRexp);
		wQueryBuilder.append(" and ").append(mSortKeyCol).append(" > 0");
		wQueryBuilder.append(" order by ").append(mSortKeyCol);

		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());

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

		wQueryBuilder = new StringBuilder("select ").append(mCategoryIdCol).append(", ")
				.append(mItemIdCol).append(", ").append(mItemIdCol).append(", ")
				.append(mItemNameCol);
		wQueryBuilder.append(" from ").append(mItemTable);
		wQueryBuilder.append(" where ").append(mDelFlgCol).append(" = b'0' and ")
				.append(mMoveFlgCol)
				.append(" = b'0'");
		wQueryBuilder.append(" order by ").append(mSortKeyCol);

		// System.out.println(wQuery);
		wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());

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
		StringBuilder wQueryBuilder;

		List<ConfigItem> wConfigItemList = new ArrayList<ConfigItem>();
		wConfigItemList.add(pConfigItem);

		while (wConfigItemList.size() > 0) {

			ConfigItem wCurrentItem = wConfigItemList.get(0);
			wConfigItemList.remove(0);

			if (!wCurrentItem.isSpecial()) {
				// 自身のアップデート

				if (wCurrentItem.isCategory()) {
					wQueryBuilder = new StringBuilder("update ").append(mCategoryTable)
							.append(" set ")
							.append(mSortKeyCol).append(" = ").append(wSortKeyCategory)
							.append(" where ")
							.append(mCategoryIdCol).append(" = ").append(wCurrentItem.getId());
					wSortKeyCategory++;
				} else {
					wQueryBuilder = new StringBuilder("update ").append(mItemTable).append(" set ")
							.append(mSortKeyCol).append(" = ").append(wSortKeyItem)
							.append(" where ")
							.append(mItemIdCol).append(" = ").append(wCurrentItem.getId());
					wSortKeyItem++;
				}
				mDbAccess.executeUpdate(wQueryBuilder.toString());
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
		StringBuilder wQueryBuilder = new StringBuilder("insert into ").append(mCategoryTable)
				.append(" (")
				.append(mCategoryRexpCol).append(", ").append(mCategoryNameCol).append(", ")
				.append(mSortKeyCol).append(") values (");
		wQueryBuilder.append((isIncome) ? mIncomeRexp : mExpenseRexp);
		wQueryBuilder.append(", '").append(pCategoryName).append("', ").append(9999).append(")");
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQueryBuilder.toString());
	}

	public static void insertNewItem(int pCategoryId, String pItemName) {
		String wQuery = new StringBuilder("insert into ").append(mItemTable).append(" (")
				.append(mCategoryIdCol).append(", ").append(mItemNameCol).append(", ")
				.append(mSortKeyCol)
				.append(") values (").append(pCategoryId).append(", '").append(pItemName)
				.append("', ")
				.append(9999).append(")").toString();
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQuery);
	}

	public static void updateCategory(int pCategoryId, String pCategoryName) {
		String wQuery = new StringBuilder("update ").append(mCategoryTable).append(" set ")
				.append(mCategoryNameCol).append(" = '").append(pCategoryName).append("' where ")
				.append(mCategoryIdCol).append(" = ").append(pCategoryId).toString();
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQuery);
	}

	public static void updateItem(int pCategoryId, int pItemId, String pItemName) {
		String wQuery = new StringBuilder("update ").append(mItemTable).append(" set ")
				.append(mCategoryIdCol).append(" = ").append(pCategoryId).append(", ")
				.append(mItemNameCol).append(" = '").append(pItemName).append("' ")
				.append(" where ")
				.append(mItemIdCol).append(" = ").append(pItemId).toString();
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQuery);
	}

	public static void deleteCategoryItem(ConfigItem pConfigItem) {
		String wTableName = (pConfigItem.isCategory()) ? mCategoryTable : mItemTable;
		String wIdName = (pConfigItem.isCategory()) ? mCategoryIdCol : mItemIdCol;

		String wQuery = new StringBuilder("update ").append(wTableName).append(" set ")
				.append(mDelFlgCol).append(" = b'1' where ").append(wIdName).append(" = ")
				.append(pConfigItem.getId()).toString();
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQuery);
	}

	public static List<Integer> getRelatedBookIdList(ConfigItem pConfigItem) {
		List<Integer> wList = new ArrayList<Integer>();
		String wQuery = new StringBuilder("select ").append(mBookIdCol).append(" from ")
				.append(mBookItemTable).append(" where ").append(mItemIdCol).append(" = ")
				.append(pConfigItem.getId()).toString();
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
		String wQuery = new StringBuilder("select ").append(mItemIdCol).append(" from ")
				.append(mBookItemTable).append(" where ").append(mBookIdCol).append(" = ")
				.append(pBookId).toString();
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
			wQuery = new StringBuilder("delete from ").append(mBookItemTable).append(" where ")
					.append(mItemIdCol).append(" = ").append(pItemId).append(" and ")
					.append(mBookIdCol)
					.append(" = ").append(pBookId).toString();
		} else {
			// 追加
			wQuery = new StringBuilder("insert into ").append(mBookItemTable).append(" (")
					.append(mItemIdCol).append(", ").append(mBookIdCol).append(") values (")
					.append(pItemId).append(", ").append(pBookId).append(")").toString();
		}
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQuery);
	}

	public static List<Integer> getSpecialCategoryIdList() {
		List<Integer> wList = new ArrayList<Integer>();
		String wQuery = new StringBuilder("select ").append(mCategoryIdCol).append(" from ")
				.append(mCategoryTable).append(" where ").append(mCategorySpecialFlgCol)
				.append(" = b'1'").append(" and ").append(mDelFlgCol).append(" = b'0'").toString();

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

		String wQuery = new StringBuilder("select ").append(mCategoryIdCol).append(" from ")
				.append(mCategoryTable).append(" where ").append(mCategoryTempFlgCol)
				.append(" = b'1'").append(" and ").append(mDelFlgCol).append(" = b'0'").toString();

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
		wQuery = new StringBuilder("update ").append(mCategoryTable).append(" set ")
				.append(mCategorySpecialFlgCol).append(" = b'").append(isSelected ? 1 : 0)
				.append("' where ").append(mCategoryIdCol).append(" = ").append(pCategoryId)
				.toString();
		// System.out.println(wQuery);
		mDbAccess.executeUpdate(wQuery);
	}

	public static void updateTempCategory(int pCategoryId, boolean isSelected) {
		String wQuery = new StringBuilder("update ").append(mCategoryTable).append(" set ")
				.append(mCategoryTempFlgCol).append(" = b'").append(isSelected ? 1 : 0)
				.append("' where ").append(mCategoryIdCol).append(" = ").append(pCategoryId)
				.toString();
		mDbAccess.executeUpdate(wQuery);
	}

	public static String getBookNameById(int pBookId) {
		ResultSet wResultSet = mDbAccess.executeQuery(new StringBuilder("select ")
				.append(mBookNameCol).append(" from ").append(mBookTable).append(" where ")
				.append(mBookIdCol).append(" = ").append(pBookId).toString());
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
		String wQuery = new StringBuilder("select ").append(mBookIdCol).append(", ")
				.append(mBookNameCol).append(", ").append(mBookBalanceCol).append(" from ")
				.append(mBookTable).append(" where ").append(mDelFlgCol).append(" = b'0' ")
				.append(" order by ").append(mSortKeyCol).toString();
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
		String wQuery = new StringBuilder("insert into ").append(mBookTable).append(" (")
				.append(mBookNameCol).append(") values('").append(pBookName).append("')")
				.toString();
		mDbAccess.executeUpdate(wQuery);
	}

	public static void updateBook(int pBookId, String pBookName, int pBalance) {
		String wQuery = new StringBuilder("update ").append(mBookTable).append(" set ")
				.append(mBookNameCol).append(" = '").append(pBookName).append("', ")
				.append(mBookBalanceCol).append(" = ").append(pBalance).append(" where ")
				.append(mBookIdCol).append(" = ").append(pBookId).toString();
		mDbAccess.executeUpdate(wQuery);
	}

	public static void removeBook(int pBookId) {
		String wQuery = new StringBuilder("update ").append(mBookTable).append(" set ")
				.append(mDelFlgCol).append(" = b'1' ").append(" where ").append(mBookIdCol)
				.append(" = ").append(pBookId).toString();
		mDbAccess.executeUpdate(wQuery);
	}

	public static void updateBookSortKeys(List<Book> pBookList) {
		int wSortKey = 1;
		String wQuery;
		for (Book wBook : pBookList) {
			wQuery = new StringBuilder("update ").append(mBookTable).append(" set ")
					.append(mSortKeyCol).append(" = ").append(wSortKey++).append(" where ")
					.append(mBookIdCol).append(" = ").append(wBook.getId()).toString();
			mDbAccess.executeUpdate(wQuery);
		}
	}

	public static void updateBalance(Book pBook) {
		String wQuery = new StringBuilder("update ").append(mBookTable).append(" set ")
				.append(mBookBalanceCol).append(" = ").append(pBook.getBalance())
				.append(" where ").append(mBookIdCol).append(" = ").append(pBook.getId())
				.toString();
		mDbAccess.executeUpdate(wQuery);
	}

	public static RecordTableItem[][] getSearchedRecordTableItemList(String pQueryString) {
		RecordTableItem[][] result = new RecordTableItem[2][];
		Date wDate = new Date();
		String wQueryBase = new StringBuilder("select ").append(mActIdCol).append(", ")
				.append(mBookIdCol).append(", ").append(mActDtCol).append(", ")
				.append(mActTable).append(".").append(mItemIdCol).append(", ").append(mGroupIdCol)
				.append(", ").append(mActIncomeCol).append(", ").append(mActExpenseCol)
				.append(", ").append(mActFreqCol).append(", ")
				.append(mNoteNameCol).append(" from ").append(mActTable).append(", ")
				.append(mItemTable).append(", ").append(mCategoryTable)
				.append(" where ").append(mItemTable).append(".").append(mItemIdCol).append(" = ")
				.append(mActTable).append(".").append(mItemIdCol)
				.append(" and ").append(mItemTable).append(".").append(mCategoryIdCol)
				.append(" = ").append(mCategoryTable).append(".").append(mCategoryIdCol)
				.append(" and ").append(mActTable).append(".").append(mDelFlgCol)
				.append(" = b'0' ")
				.append(" and ").append(mNoteNameCol).append(" like '%").append(pQueryString)
				.append("%'").toString();
		String wQueryPeriodBefore = new StringBuilder(" and ").append(mActDtCol).append(" <= ")
				.append(getDateStrings(wDate)).toString();
		String wQueryPeriodAfter = new StringBuilder(" and ").append(mActDtCol).append(" > ")
				.append(getDateStrings(wDate)).toString();
		String wQueryOrder = new StringBuilder(" order by ").append(mActDtCol).append(", ")
				.append(mCategoryTable).append(".").append(mCategoryRexpCol).append(", ")
				.append(mCategoryTable).append(".").append(mSortKeyCol).append(", ")
				.append(mItemTable).append(".").append(mSortKeyCol).toString();

		result[0] = getRecordTableItemFromResultSet(mDbAccess.executeQuery(
				new StringBuilder(wQueryBase).append(wQueryPeriodBefore).append(wQueryOrder)
						.toString()));
		result[1] = getRecordTableItemFromResultSet(mDbAccess.executeQuery(
				new StringBuilder(wQueryBase).append(wQueryPeriodAfter).append(wQueryOrder)
						.toString()));

		return result;
	}

	private static RecordTableItem[] getRecordTableItemFromResultSet(ResultSet pResultSet) {
		List<RecordTableItem> wList = new ArrayList<RecordTableItem>();
		try {
			while (pResultSet.next()) {
				wList.add(new RecordTableItem.Builder(pResultSet.getInt(mBookIdCol), pResultSet
						.getInt(mItemIdCol), pResultSet.getDate(mActDtCol)).actId(
						pResultSet.getInt(mActIdCol)).expense(pResultSet.getInt(mActExpenseCol))
						.frequency(pResultSet.getInt(mActFreqCol)).groupId(
								pResultSet.getInt(mGroupIdCol)).income(
								pResultSet.getInt(mActIncomeCol)).note(
								pResultSet.getString(mNoteNameCol)).build());
			}
			pResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
		}
		return wList.toArray(new RecordTableItem[0]);
	}

	// 立替残高（借入残高）
	private static int getTempBalance(Date pEndDate) {

		int wTempBalance = 0;
		String wResultColName = "ResultCol";
		String wEndDateString = getDateStrings(pEndDate);

		String wQuery = new StringBuilder("select sum(").append(mActIncomeCol).append(" - ")
				.append(mActExpenseCol).append(") as ").append(wResultColName).append(" from ")
				.append(mActTable).append(", ").append(mItemTable).append(", ")
				.append(mCategoryTable).append(" where ").append(mActTable).append(".")
				.append(mItemIdCol).append(" = ").append(mItemTable).append(".").append(mItemIdCol)
				.append(" and ").append(mItemTable).append(".").append(mCategoryIdCol)
				.append(" = ").append(mCategoryTable).append(".").append(mCategoryIdCol)
				.append(" and ").append(mActTable).append(".").append(mDelFlgCol)
				.append(" = b'0' and ").append(mActDtCol).append(" <= ").append(wEndDateString)
				.append(" and ").append(mCategoryTable).append(".").append(mCategoryTempFlgCol)
				.append(" = b'1'").toString();

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

	private static int getTotalTempProfit(DateRange pDateRange) {
		// 立替収支
		int wTotalTempProfit = 0;

		String wResultColName = "ResultCol";
		String wStartDateString = getDateStrings(pDateRange.getStartDate());
		String wEndDateString = getDateStrings(pDateRange.getEndDate());

		String wQuery = new StringBuilder("select sum(").append(mActIncomeCol).append(" - ")
				.append(mActExpenseCol).append(") as ").append(wResultColName).append(" from ")
				.append(mActTable).append(", ").append(mItemTable).append(", ")
				.append(mCategoryTable).append(" where ").append(mActTable).append(".")
				.append(mItemIdCol).append(" = ").append(mItemTable).append(".").append(mItemIdCol)
				.append(" and ").append(mItemTable).append(".").append(mCategoryIdCol)
				.append(" = ").append(mCategoryTable).append(".").append(mCategoryIdCol)
				.append(" and ").append(mActTable).append(".").append(mDelFlgCol)
				.append(" = b'0' and ").append(mActDtCol).append(" between ")
				.append(wStartDateString).append(" and ").append(wEndDateString).append(" and ")
				.append(mCategoryTable).append(".").append(mCategoryTempFlgCol).append(" = b'1'")
				.toString();

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

	private static int getTotalSpecialProfit(DateRange pDateRange) {
		// 特別収支
		int wSpecialProfit = 0;

		String wResultColName = "ResultCol";
		String wStartDateString = getDateStrings(pDateRange.getStartDate());
		String wEndDateString = getDateStrings(pDateRange.getEndDate());

		String wQuery = new StringBuilder("select sum(").append(mActIncomeCol).append(" - ")
				.append(mActExpenseCol).append(") as ").append(wResultColName).append(" from ")
				.append(mActTable).append(", ").append(mItemTable).append(", ")
				.append(mCategoryTable).append(" where ").append(mActTable).append(".")
				.append(mItemIdCol).append(" = ").append(mItemTable).append(".").append(mItemIdCol)
				.append(" and ").append(mItemTable).append(".").append(mCategoryIdCol)
				.append(" = ").append(mCategoryTable).append(".").append(mCategoryIdCol)
				.append(" and ").append(mActTable).append(".").append(mDelFlgCol)
				.append(" = b'0' and ").append(mActDtCol).append(" between ")
				.append(wStartDateString).append(" and ").append(wEndDateString).append(" and ")
				.append(mCategoryTable).append(".").append(mCategorySpecialFlgCol)
				.append(" = b'1'").toString();

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

	private static IncomeExpense getAppearedIncomeExpense(DateRange pDateRange, int pBookId) {
		// みかけ収入・支出（各Book）
		int wIncome = 0;
		int wExpense = 0;
		String wStartDateString = getDateStrings(pDateRange.getStartDate());
		String wEndDateString = getDateStrings(pDateRange.getEndDate());

		StringBuilder wQueryBuilder = new StringBuilder("select sum(").append(mActIncomeCol)
				.append(") as ").append(mActIncomeCol).append(", sum(").append(mActExpenseCol)
				.append(") as ").append(mActExpenseCol).append(" from ")
				.append(mActTable).append(", ").append(mItemTable).append(", ")
				.append(mCategoryTable).append(" where ").append(mActTable).append(".")
				.append(mItemIdCol).append(" = ").append(mItemTable).append(".").append(mItemIdCol)
				.append(" and ").append(mItemTable).append(".").append(mCategoryIdCol)
				.append(" = ").append(mCategoryTable).append(".").append(mCategoryIdCol)
				.append(" and ").append(mActTable).append(".").append(mDelFlgCol)
				.append(" = b'0' and ").append(mActDtCol).append(" between ")
				.append(wStartDateString).append(" and ").append(wEndDateString);

		if (pBookId == mAllBookId) {
			// Moveを除く
			wQueryBuilder.append(" and ").append(mItemTable).append(".").append(mMoveFlgCol)
					.append(" = b'0'");

		} else {
			// BookIdの条件を追加
			wQueryBuilder.append(" and ").append(mBookIdCol).append(" = ").append(pBookId);
		}

		// System.out.println(wQuery);
		ResultSet wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());

		try {
			if (wResultSet.next()) {
				wIncome = wResultSet.getInt(mActIncomeCol);
				wExpense = wResultSet.getInt(mActExpenseCol);
				// System.out.println(wTempBalanceName + " = " +
				// wTempBalance);
			}
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return new IncomeExpense(wIncome, wExpense);
	}

	private static int getBalance(Date pEndDate, int pBookId, boolean pIncludeEndDate) {

		int wBalance = getInitialBalance(pBookId);

		String wEnd = getDateStrings(pEndDate);
		String wBookWhere = getBookWhere(pBookId);
		String wResultCol = "Value";

		StringBuilder wQueryBuilder = new StringBuilder("select SUM(").append(mActIncomeCol)
				.append(" - ").append(mActExpenseCol).append(") as ").append(wResultCol)
				.append(" from ").append(mActTable).append(" where ").append(mDelFlgCol)
				.append(" = b'0' ").append(" and ").append(wBookWhere).append(" and ")
				.append(mActDtCol);

		if (pIncludeEndDate) {
			wQueryBuilder.append(" <= ").append(wEnd);
		} else {
			wQueryBuilder.append(" < ").append(wEnd);
		}

		// System.out.println(wQuery);

		ResultSet wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());

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

		StringBuilder wQueryBuilder = new StringBuilder();

		if (pBookId == mAllBookId) {
			wQueryBuilder.append("select SUM(").append(mBookBalanceCol).append(") as ")
					.append(wResultCol).append(" from ").append(mBookTable).append(" where ")
					.append(mDelFlgCol).append(" = b'0' and ").append(wBookWhere);

		} else {
			wQueryBuilder.append("select ").append(mBookBalanceCol).append(" as ")
					.append(wResultCol).append(" from ").append(mBookTable).append(" where ")
					.append(wBookWhere);
		}

		// System.out.println(wQuery);
		ResultSet wResultSet = mDbAccess.executeQuery(wQueryBuilder.toString());

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

		ResultSet wResultSet = mDbAccess.executeQuery(
				new StringBuilder("select max(").append(mGroupIdCol).append(") as ").append(wCol)
						.append(" from ").append(mActTable).toString());

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
		String wQuery = new StringBuilder("delete from ").append(mNoteTable).append(" where ")
				.append(mItemIdCol).append(" = ").append(pItemId).append(" and ")
				.append(mNoteNameCol).append(" = '").append(pNote).append("'").toString();
		mDbAccess.executeUpdate(wQuery);

		wQuery = new StringBuilder("insert into  ").append(mNoteTable).append(" (")
				.append(mNoteNameCol).append(",").append(mItemIdCol).append(") values('")
				.append(pNote).append("',").append(pItemId).append(")").toString();
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
				"SQL ResultSet Handling Error", e.toString() + "\n\n" + wStack);
		// System.err.println("ResultSet Handling Error: " + e.toString());
	}

	private static String getNoteStringWithEscape(String pNote) {
		for (String wString : mSpecialChars) {
			pNote = pNote.replace(wString, mEscapeChar + wString);
		}
		return pNote;
	}
}