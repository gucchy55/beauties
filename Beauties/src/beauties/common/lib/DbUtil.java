package beauties.common.lib;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import java.sql.PreparedStatement;

import beauties.common.model.AnnualDateRange;
import beauties.common.model.Book;
import beauties.common.model.Category;
import beauties.common.model.DateRange;
import beauties.common.model.IncomeExpense;
import beauties.common.model.IncomeExpenseSummary;
import beauties.common.model.IncomeExpenseType;
import beauties.common.model.Item;
import beauties.config.model.ConfigItem;
import beauties.record.model.RecordTableItem;
import beauties.record.model.RecordTableItemCollection;
import beauties.record.model.RecordTableItemForMove;
import beauties.record.model.SummaryItemsCommon;
import beauties.record.model.SummaryTableItem;
import beauties.record.model.SummaryTableItemCollection;
import beauties.record.model.SummaryTableItemFactory;
import beauties.record.model.SummaryTableItemsNormal;

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

//	private final static int mAllBookId = SystemData.getAllBookInt();
	private final static String mCategorySpecialFlgCol = "SPECIAL_FLG";
	private final static String mCategoryTempFlgCol = "TEMP_FLG";

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
	
	private final static DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private DbUtil() {

	}
	
	public static void closeConnection() {
		mDbAccess.closeConnection();
	}
	
	private static int getSystemValue(String pWhereVal) {
		int wResult = SystemData.getUndefinedInt();
		
		// select NUM_VALUE from system where SID = ?
		String wQuery = "select " + mSystemValueCol + " from " + mSystemTable + " where " + mSystemIDCol +" = ?";
		
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setString(1, pWhereVal);
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				wResultSet.next();
				wResult = wResultSet.getInt(mSystemValueCol);
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
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
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			int i=1;
			wStatement.setInt(i++, pVal);
			wStatement.setString(i++, pWhereVal);
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

	public static RecordTableItemCollection getRecordTableItems(DateRange pDateRange, Book pBook) {
		List<RecordTableItem> wRecordTableItemListUp = new ArrayList<RecordTableItem>();
		List<RecordTableItem> wRecordTableItemListBottom = new ArrayList<RecordTableItem>();

		int wBalance = getBalance(pDateRange.getStartDate(), pBook, false);
		RecordTableItem wBalanceRecord = RecordTableItem.createBalanceRowItem(pDateRange
				.getStartDate(), wBalance);

		wRecordTableItemListUp.add(wBalanceRecord);
		
		// select ACT_ID, BOOK_ID, ACT_DT, cbt_act.ITEM_ID, ITEM_NAME, GROUP_ID, INCOME, EXPENSE, FREQUENCY, NOTE_NAME
		// from cbt_act, cbm_item, cbm_category
		// where cbm_item.ITEM_ID = cbt_act.ITEM_ID
		//	and cbm_item.CATEGORY_ID = cbm_category.CATEGORY_ID
		//	and ACT_DT between ? and ?		// 1&2 (string)
		//	and cbt_act.DEL_FLG = b?		// 3 (String:0)
		//	and (BOOK_ID = ?) // 4 (int) or (cbm_item.MOVE_FLG = b?)	// 4 (String:0)
		// order by ACT_DT, cbm_category.REXP_DIV, cbm_category.SORT_KEY, cbm_item.SORT_KEY
		
		StringBuilder wQueryBuilder = new StringBuilder()
			.append("select ").append(mActIdCol).append(", ")
				.append(mBookIdCol).append(", ").append(mActDtCol).append(", ")
				.append(mActTable).append(".").append(mItemIdCol).append(", ")
				.append(mItemTable).append(".").append(mItemNameCol).append(", ")
				.append(mCategoryTable).append(".").append(mCategoryIdCol).append(", ")
				.append(mCategoryTable).append(".").append(mCategoryNameCol).append(", ")
				.append(mCategoryTable).append(".").append(mCategoryRexpCol).append(", ")
				.append(mGroupIdCol).append(", ").append(mActIncomeCol).append(", ")
				.append(mActExpenseCol).append(", ").append(mActFreqCol).append(", ")
				.append(mNoteNameCol)
			.append(" from ").append(mActTable).append(", ").append(mItemTable).append(", ")
				.append(mCategoryTable)
			.append(" where ").append(mItemTable).append(".").append(mItemIdCol)
				.append(" = ").append(mActTable).append(".").append(mItemIdCol)
				.append(" and ").append(mItemTable).append(".").append(mCategoryIdCol)
				.append(" = ").append(mCategoryTable).append(".").append(mCategoryIdCol)
				.append(" and ").append(mActDtCol).append(" between ? and ?")
				.append(" and ").append(mActTable).append(".")
				.append(mDelFlgCol).append(" = b?")
			;
		if (pBook.isAllBook()) {
			wQueryBuilder.append(" and ").append(mItemTable).append(".")
					.append(mMoveFlgCol).append(" = b?");
		} else {
			wQueryBuilder.append(" and ").append(mBookIdCol).append(" = ?"); //wBookWhere);
		}
		wQueryBuilder.append(" order by ").append(mActDtCol).append(", ")
				.append(mCategoryTable).append(".").append(mCategoryRexpCol).append(", ")
				.append(mCategoryTable).append(".").append(mSortKeyCol).append(", ")
				.append(mItemTable).append(".").append(mSortKeyCol);
		
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			int i=1;
			wStatement.setString(i++, mDateFormat.format(pDateRange.getStartDate()));
			wStatement.setString(i++, mDateFormat.format(pDateRange.getEndDate()));
			wStatement.setString(i++, "0");	// DEL_FLG
			if (pBook.isAllBook()) {
				wStatement.setString(i++, "0"); // MOVE_FLG
			} else {
				wStatement.setInt(i++, pBook.getId());
			}
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				Date wDateNow = new Date();
				while (wResultSet.next()) {
					wBalance += wResultSet.getInt(mActIncomeCol) - wResultSet.getInt(mActExpenseCol);

					RecordTableItem wRecord = generateRecordTableItem(wBalance, wResultSet);
					if (wResultSet.getDate(mActDtCol).after(wDateNow)) {
						wRecordTableItemListBottom.add(wRecord);
					} else {
						wRecordTableItemListUp.add(wRecord);
					}
				}
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		
		return new RecordTableItemCollection(wRecordTableItemListUp, wRecordTableItemListBottom);
//
//		RecordTableItem[][] wRet = new RecordTableItem[2][];
//		wRet[0] = wRecordTableItemListUp.toArray(new RecordTableItem[0]);
//		wRet[1] = wRecordTableItemListBottom.toArray(new RecordTableItem[0]);
//
//		return wRet;

	}

	private static RecordTableItem generateRecordTableItem(int wBalance, ResultSet wResultSet)
			throws SQLException {
		Book wBook = Book.getBook(wResultSet.getInt(mBookIdCol));
		Category wCategory = getCategory(wResultSet);
		Item wItem = getItem(wResultSet, wCategory);
		RecordTableItem wRecord = new RecordTableItem.Builder(
				wBook, wItem,
				wResultSet.getDate(mActDtCol))
				.actId(wResultSet.getInt(mActIdCol)).balance(wBalance)
				.expense(wResultSet.getInt(mActExpenseCol))
				.frequency(wResultSet.getInt(mActFreqCol))
				.groupId(wResultSet.getInt(mGroupIdCol))
				.income(wResultSet.getInt(mActIncomeCol))
				.note(wResultSet.getString(mNoteNameCol)).build();
		return wRecord;
	}
	
	private static RecordTableItem generateRecordTableItem(ResultSet wResultSet)
			throws SQLException {
		Book wBook = Book.getBook(wResultSet.getInt(mBookIdCol));
		Category wCategory = getCategory(wResultSet);
		Item wItem = getItem(wResultSet, wCategory);
		RecordTableItem wRecord = new RecordTableItem.Builder(
				wBook, wItem,
				wResultSet.getDate(mActDtCol))
				.actId(wResultSet.getInt(mActIdCol))
				.expense(wResultSet.getInt(mActExpenseCol))
				.frequency(wResultSet.getInt(mActFreqCol))
				.groupId(wResultSet.getInt(mGroupIdCol))
				.income(wResultSet.getInt(mActIncomeCol))
				.note(wResultSet.getString(mNoteNameCol)).build();
		return wRecord;
	}

	public static Collection<Book> getBooks() {
		Book.clear();
		Collection<Book> wBooks = new ArrayList<>();

		// select BOOK_ID, BOOK_NAME from cbm_book where DEL_FLG = b? order by SORT_KEY
		String wQuery = new StringBuilder().append("select ").append(mBookIdCol).append(", ")
				.append(mBookNameCol).append(" from ").append(mBookTable)
				.append(" where ").append(mDelFlgCol).append(" = b?").append(" order by ")
				.append(mSortKeyCol).toString();
		
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setString(1, "0");
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				while (wResultSet.next()) {
					int wBookId = wResultSet.getInt(mBookIdCol);
//					String wBookName = wResultSet.getString(mBookNameCol);
					if (Book.getBook(wBookId) == null) {
						Book.generateBook(wBookId, wResultSet.getString(mBookNameCol));
					}
					wBooks.add(Book.getBook(wBookId));
				}
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		return wBooks;
	}
	
	public static String[] getNoteMove() {
		return getNotes(getMoveIncomeItemId());
	}
	
	public static String[] getNotes(Item pItem) {
		return getNotes(pItem.getId());
	}
	
	private static String[] getNotes(int pItemId) {
		List<String> wResultList = new ArrayList<String>();

		// select NOTE_NAME from cbt_note 
		// where ITEM_ID = ? and DEL_FLG = b?	// 1 (int), 2 (String 0)
		// order by NOTE_ID desc
		String wQuery = new StringBuilder().append("select ").append(mNoteNameCol)
				.append(" from ").append(mNoteTable)
				.append(" where ").append(mItemIdCol).append(" = ?")
				.append(" and ").append(mDelFlgCol).append(" = b?")
				.append(" order by ").append(mNoteIdCol).append(" desc")
				.append(" limit ").append(SystemData.getNoteLimit()).toString();
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			int i=1;
			wStatement.setInt(i++, pItemId);
			wStatement.setString(i++, "0");
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				while (wResultSet.next()) {
					wResultList.add(wResultSet.getString(mNoteNameCol));
				}
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wResultList.toArray(new String[0]);

	}

	public static Map<IncomeExpenseType, List<Item>> getAllItems() {
		Map<IncomeExpenseType, List<Item>> wResultMap = new EnumMap<>(IncomeExpenseType.class);
		wResultMap.put(IncomeExpenseType.INCOME, new ArrayList<Item>());
		wResultMap.put(IncomeExpenseType.EXPENCE, new ArrayList<Item>());

		StringBuilder wQueryBuilder = new StringBuilder();
		
		// select cbm_item.ITEM_ID, cbm_item.ITEM_NAME from cbm_item, cbm_category
		// where cbm_category.CATEGORY_ID = cbm_item.CATEGORY_ID
		//	and cbm_item.DEL_FLG = b?		// 1 (String:0)
		// order by cbm_item.SORT_KEY
		wQueryBuilder.append("select distinct ").append(mItemTable).append(".").append(mItemIdCol)
				.append(", ").append(mItemTable).append(".").append(mItemNameCol)
				.append(", ").append(mCategoryTable).append(".").append(mCategoryIdCol)
				.append(", ").append(mCategoryTable).append(".").append(mCategoryNameCol)
				.append(", ").append(mCategoryTable).append(".").append(mCategoryRexpCol)
			.append(" from ").append(mItemTable)//.append(" , ").append(mBookItemTable)
				.append(", ").append(mCategoryTable)
			.append(" where ")
			.append(mCategoryTable).append(".").append(mCategoryIdCol)
				.append(" = ").append(mItemTable).append(".").append(mCategoryIdCol)
			.append(" and ").append(mItemTable).append(".").append(mDelFlgCol).append(" = b?")
			.append(" order by ").append(mItemTable).append(".").append(mSortKeyCol);
		
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			wStatement.setString(1, "0");
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				while (wResultSet.next()) {
					Item wItem = getItem(wResultSet, getCategory(wResultSet));
					wResultMap.get(wItem.getCategory().getIncomeExpenseType()).add(wItem);
				}
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wResultMap;
	}

	public static Collection<Item> getItems(Book pBook, IncomeExpenseType pIncomeExpenseType) {
		Collection<Item> wResultList = new ArrayList<>();
		
		// select cbm_item.ITEM_ID from cbm_item, cbr_book, cbm_category
		// where cbm_item.ITEM_ID = cbr_book.ITEM_ID
		//	and cbm_category.CATEGORY_ID = cbm_item.CATEGORY_ID
		//	and cbm_category.REXP_DIV = ?	// 1 int
		//	(and cbr_book.BOOK_ID = ?)		// 2 int
		//	and cbr_book.DEL_FLG = b'0'
		//	and cbm_item.DEL_FLG = b'0'
		// order by cbm_item.SORT_KEY
		
		StringBuilder wQueryBuilder = new StringBuilder()
			.append("select ").append(mItemTable).append(".").append(mItemIdCol).append(",")
			.append(mItemTable).append(".").append(mItemNameCol).append(",")
			.append(mCategoryTable).append(".").append(mCategoryIdCol).append(",")
			.append(mCategoryTable).append(".").append(mCategoryNameCol)
			.append(" from ").append(mItemTable).append(" , ").append(mBookItemTable)
				.append(", ").append(mCategoryTable)
			.append(" where ").append(mItemTable).append(".").append(mItemIdCol)
				.append(" = ").append(mBookItemTable).append(".").append(mItemIdCol)
			.append(" and ").append(mCategoryTable).append(".").append(mCategoryIdCol)
				.append(" = ").append(mItemTable).append(".").append(mCategoryIdCol)
			.append(" and ").append(mCategoryTable).append(".").append(mCategoryRexpCol)
				.append(" = ?");
		if (!pBook.isAllBook()) {
			wQueryBuilder.append(" and ").append(mBookItemTable).append(".").append(mBookIdCol)
					.append(" = ?");
		}
		wQueryBuilder.append(" and ").append(mBookItemTable).append(".").append(mDelFlgCol)
				.append(" = b'0' ")
			.append(" and ").append(mItemTable).append(".").append(mDelFlgCol).append(" = b'0' ")
			.append(" order by ").append(mItemTable).append(".").append(mSortKeyCol);
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			int i=1;
			wStatement.setInt(i++, pIncomeExpenseType.getCategoryRexp());
			if (!pBook.isAllBook()) {
				wStatement.setInt(i++, pBook.getId());
			}
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				while (wResultSet.next()) {
//					wResultList.add(wResultSet.getInt(mItemIdCol));
					wResultList.add(getItem(wResultSet, getCategory(wResultSet, pIncomeExpenseType)));
				}
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wResultList;
	}

	public static Collection<Item> getItems(Book pBook, Category pCategory) {
		Collection<Item> wResultList = new ArrayList<>();

		// select cbm_item.ITEM_ID from cbm_item, cbr_book, cbm_category
		// where cbm_item.ITEM_ID = cbr_book.ITEM_ID
		//	and cbm_category.CATEGORY_ID = cbm_item.CATEGORY_ID
		//	and cbr_book.BOOK_ID = ?				// 1 (int)
		//	(and cbm_category.CATEGORY_ID = ?)		// 2 (int)
		//	and cbr_book.DEL_FLG = b'0'
		//	and cbm_item.DEL_FLG = b'0'
		// order by cbm_item.SORT_KEY
		StringBuilder wQueryBuilder = new StringBuilder().append("select ")
					.append(mItemTable).append(".").append(mItemIdCol).append(",")
					.append(mItemTable).append(".").append(mItemNameCol).append(",")
					.append(mCategoryTable).append(".").append(mCategoryIdCol).append(",")
					.append(mCategoryTable).append(".").append(mCategoryNameCol)
				.append(" from ").append(mItemTable).append(" , ").append(mBookItemTable)
					.append(", ").append(mCategoryTable)
				.append(" where ").append(mItemTable).append(".").append(mItemIdCol)
					.append(" = ").append(mBookItemTable).append(".").append(mItemIdCol)
				.append(" and ").append(mCategoryTable).append(".").append(mCategoryIdCol)
					.append(" = ").append(mItemTable).append(".").append(mCategoryIdCol)
				.append(" and ").append(mBookItemTable).append(".").append(mBookIdCol).append(" = ?");
		if (!pCategory.isAllCategory()) {
			wQueryBuilder.append(" and ").append(mCategoryTable).append(".").append(mCategoryIdCol)
					.append(" = ?");
		}
		wQueryBuilder.append(" and ").append(mBookItemTable).append(".").append(mDelFlgCol)
					.append(" = b'0'")
				.append(" and ").append(mItemTable).append(".").append(mDelFlgCol).append(" = b'0'")
				.append(" order by ").append(mItemTable).append(".").append(mSortKeyCol);
		
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			int i=1;
			wStatement.setInt(i++, pBook.getId());
			if (!pCategory.isAllCategory()) {
				wStatement.setInt(i++, pCategory.getId());
			}
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				while (wResultSet.next()) {
//					wResultList.add(wResultSet.getInt(mItemIdCol));
					wResultList.add(getItem(wResultSet, pCategory));
				}
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wResultList;
	}

	public static Collection<Category> getCategories(Book pBook, IncomeExpenseType pIncomeExpenseType) {
		Collection<Category> wResultList = new ArrayList<>();
		// select count(cbm_item.ITEM_NAME), cbm_category.CATEGORY_ID
		// from cbm_item, cbr_book, cbm_category
		// where cbm_item.ITEM_ID = cbr_book.ITEM_ID
		//	and cbm_category.CATEGORY_ID = cbm_item.CATEGORY_ID
		//	and cbm_category.REXP_DIV = ?	// 1 (int)
		//	(and cbr_book.BOOK_ID = ?)		// 2 (int)
		//	and cbr_book.DEL_FLG = b'0'
		//	and cbm_item.DEL_FLG = b'0'
		// group by cbm_category.CATEGORY_ID
		// order by cbm_category.SORT_KEY
		StringBuilder wQueryBuilder = new StringBuilder()
				.append("select count( ").append(mItemTable).append(".").append(mItemNameCol).append(" ), ")
					.append(mCategoryTable).append(".").append(mCategoryIdCol).append(",")
					.append(mCategoryTable).append(".").append(mCategoryNameCol)
				.append(" from ").append(mItemTable).append(", ").append(mBookItemTable)
					.append(", ").append(mCategoryTable)
				.append(" where ").append(mItemTable).append(".").append(mItemIdCol).append(" = ")
					.append(mBookItemTable).append(".").append(mItemIdCol)
				.append(" and ").append(mCategoryTable).append(".").append(mCategoryIdCol)
					.append(" = ").append(mItemTable).append(".").append(mCategoryIdCol);
		wQueryBuilder.append(" and ").append(mCategoryTable).append(".").append(mCategoryRexpCol)
				.append(" = ?");
		if (!pBook.isAllBook()) {
			wQueryBuilder.append(" and ").append(mBookItemTable).append(".").append(mBookIdCol)
					.append(" = ?");
		}
		wQueryBuilder.append(" and ").append(mBookItemTable).append(".").append(mDelFlgCol).append(" = b'0' ")
				.append(" and ").append(mItemTable).append(".").append(mDelFlgCol).append(" = b'0' ")
				.append(" group by ").append(mCategoryTable).append(".").append(mCategoryIdCol)
				.append(" order by ").append(mCategoryTable).append(".").append(mSortKeyCol);
		
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			int i=1;
			wStatement.setInt(i++, pIncomeExpenseType.getCategoryRexp());
			if (!pBook.isAllBook()) {
				wStatement.setInt(i++, pBook.getId());
			}
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				while (wResultSet.next()) {
//					wResultList.add(wResultSet.getInt(mCategoryIdCol));
					wResultList.add(getCategory(wResultSet, pIncomeExpenseType));
				}
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wResultList;
	}

	// 設定時に使用
	public static Collection<Category> getAllCategorys(IncomeExpenseType pType) {
		Collection<Category> wResultList = new LinkedList<>();
		// select CATEGORY_ID, CATEGORY_NAME from cbm_category
		// where REXP_DIV = ?	// 1 (int)
		//	and DEL_FLG = b'0' and SORT_KEY > 0 order by SORT_KEY
		StringBuilder wQueryBuilder = new StringBuilder().append("select ")
					.append(mCategoryIdCol).append(", ").append(mCategoryNameCol)
				.append(" from ").append(mCategoryTable)
				.append(" where ").append(mCategoryRexpCol).append(" = ?")
				.append(" and ").append(mDelFlgCol).append(" = b'0' ")
				.append(" and ").append(mSortKeyCol).append(" > 0")
				.append(" order by ").append(mSortKeyCol);

		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			wStatement.setInt(1, pType.getCategoryRexp());
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				while (wResultSet.next()) {
//					}
					wResultList.add(getCategory(wResultSet, pType));
				}
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wResultList;
	}

//	@Deprecated
//	public static boolean isIncomeCategory(int pCategoryId) {
//		// select REXP_DIV from cbm_category
//		// where CATEGORY_ID = ?	// 1 (int)
//		StringBuilder wQueryBuilder = new StringBuilder()
//				.append("select ").append(mCategoryRexpCol).append(" from ").append(mCategoryTable)
//				.append(" where ").append(mCategoryIdCol).append(" = ?");
//		
//		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
//			wStatement.setInt(1, pCategoryId);
//			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
//				wResultSet.next();
//				return wResultSet.getInt(mCategoryRexpCol) == mIncomeRexp;
//			} catch (SQLException e) {
//				resultSetHandlingError(e);
//			}
//		} catch (SQLException e) {
//			resultSetHandlingError(e);
//		}
//
//		return false;
//
//	}

	public static void insertNewRecord(RecordTableItem pRecord) {
		if (pRecord.getFrequency() == 0) {
			insertNewRecordNoFreq(pRecord);
		} else if (pRecord.getFrequency() > 0) {
			insertNewRecordFreq(pRecord);
		}

		// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
		if (!"".equals(pRecord.getNote())) {
			updateNoteTable(pRecord.getItem(), pRecord.getNote());
		}

	}

	private static void insertNewRecordFreq(RecordTableItem pRecord) {
		int wGroupId = getNewGroupId();
		
		// insert into cbt_act
		// (BOOK_ID, ITEM_ID, ACT_DT, INCOME, EXPENSE, GROUP_ID, FREQUENCY, NOTE_NAME)
		// values(?,?,?,?,?,?,?,?)
		StringBuilder wQueryBuilder = new StringBuilder().append("insert into ").append(mActTable)
				.append(" (").append(mBookIdCol).append(",")
				.append(mItemIdCol).append(",")
				.append(mActDtCol).append(",")
				.append(mActIncomeCol).append(",")
				.append(mActExpenseCol).append(",")
				.append(mGroupIdCol).append(",")
				.append(mActFreqCol).append(",")
				.append(mNoteNameCol)
				.append(") values(?,?,?,?,?,?,?,?)");
					// 1 (int), 2 (int), 3 (String), 4 (int), 5 (int), 6 (int), 7 (int), 8 (String)
		
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			wStatement.setInt(1, pRecord.getBook().getId());
			wStatement.setInt(2, pRecord.getItem().getId());
//				wStatement.setString(3, wDate);
			wStatement.setInt(4, pRecord.getIncome());
			wStatement.setInt(5, pRecord.getExpense());
			wStatement.setInt(6, wGroupId);
//				wStatement.setInt(7, pRecord.getFrequency() - i);
			wStatement.setString(8, pRecord.getNote());

			for (int i = 0; i < pRecord.getFrequency() + 1; i++) {
				Calendar wCal = pRecord.getCal();
				wCal.add(Calendar.MONTH, +i);
				wStatement.setString(3, mDateFormat.format(wCal.getTime()));
				wStatement.setInt(7, pRecord.getFrequency() - i);
				mDbAccess.executeUpdate(wStatement);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	private static void insertNewRecordNoFreq(RecordTableItem pRecord) {
		// insert into cbt_act (BOOK_ID,ITEM_ID,ACT_DT,INCOME,EXPENSE,NOTE_NAME)
		// values(?,?,?,?,?,?)	// 1 (int), 2 (int), 3 (String), 4 (int), 5 (int), 6 (String)
		StringBuilder wQueryBuilder = new StringBuilder().append("insert into ").append(mActTable)
				.append(" (").append(mBookIdCol).append(",").append(mItemIdCol)
				.append(",").append(mActDtCol).append(",").append(mActIncomeCol)
				.append(",").append(mActExpenseCol).append(",").append(mNoteNameCol)
				.append(") values(?,?,?,?,?,?)");
		
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			int i = 1;
			wStatement.setInt(i++, pRecord.getBook().getId());
			wStatement.setInt(i++, pRecord.getItem().getId());
			wStatement.setString(i++, mDateFormat.format(pRecord.getDate()));
			wStatement.setInt(i++, pRecord.getIncome());
			wStatement.setInt(i++, pRecord.getExpense());
			wStatement.setString(i++, pRecord.getNote());
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	private static boolean groupShouldBeChanged(RecordTableItem pBeforeRecord,
			RecordTableItem pAfterRecord) {
		if (pBeforeRecord.getGroupId() == 0 && pAfterRecord.getFrequency() > 0)
			return true;
		return pAfterRecord.getYear() != pBeforeRecord.getYear()
				|| pAfterRecord.getMonth() != pBeforeRecord.getMonth()
				|| !pAfterRecord.getBook().equals(pBeforeRecord.getBook())
				|| !pAfterRecord.getItem().equals(pBeforeRecord.getItem());
	}

	public static void updateRecord(RecordTableItem pBeforeRecord, RecordTableItem pAfterRecord) {
		String wNote = pAfterRecord.getNote();

		// 　ともに繰り返し0ならUpdateのみ
		if (pAfterRecord.getFrequency() == 0 && pBeforeRecord.getGroupId() == 0) {
			updateRecordNoFreq(pBeforeRecord, pAfterRecord, wNote);
			
		} else {
			// どちらかが繰り返しありなら既存レコードを削除して新規追加
			updateRecordFreq(pBeforeRecord, pAfterRecord, wNote);
		}

		// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
		if (!"".equals(wNote)) {
			updateNoteTable(pAfterRecord.getItem(), wNote);
		}
	}

	private static void updateRecordFreq(RecordTableItem pBeforeRecord,
			RecordTableItem pAfterRecord, String pNote) {
		// 既存のレコードを削除
		deleteRecord(pBeforeRecord);

		int wGroupId;
		if (pBeforeRecord.getGroupId() > 0 && pAfterRecord.getFrequency() == 0) {
			wGroupId = 0;
		} else {
			wGroupId = groupShouldBeChanged(pBeforeRecord, pAfterRecord) ? getNewGroupId()
					: pBeforeRecord.getGroupId();
		}

		// 新規のレコードを追加
		Calendar wCalBase = Calendar.getInstance();
		wCalBase.setTime(pAfterRecord.getDate());
		// insert into cbt_act
		// (BOOK_ID, ITEM_ID, ACT_DT, INCOME, EXPENSE, GROUP_ID, FREQUENCY, NOTE_NAME)
		// values(?,?,?,?,?,?,?,?)
		StringBuilder wQueryBuilder = new StringBuilder().append("insert into ").append(mActTable)
				.append(" (").append(mBookIdCol).append(",")
				.append(mItemIdCol).append(",").append(mActDtCol).append(",")
				.append(mActIncomeCol).append(",").append(mActExpenseCol).append(",")
				.append(mGroupIdCol).append(",").append(mActFreqCol).append(",").append(mNoteNameCol)
				.append(") values(?,?,?,?,?,?,?,?)");
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			wStatement.setInt(1, pAfterRecord.getBook().getId());
			wStatement.setInt(2, pAfterRecord.getItem().getId());
//				wStatement.setString(3, wDate);
			wStatement.setInt(4, pAfterRecord.getIncome());
			wStatement.setInt(5, pAfterRecord.getExpense());
			wStatement.setInt(6, wGroupId);
//				wStatement.setInt(7, pAfterRecord.getFrequency());
			wStatement.setString(8, pNote);
			int wFrequency = pAfterRecord.getFrequency();
			for (int i = 0; i < pAfterRecord.getFrequency() + 1; i++) {
				Calendar wCal = (Calendar) wCalBase.clone();
				wCal.add(Calendar.MONTH, +i);
				String wDate = mDateFormat.format(wCal.getTime());

				wStatement.setString(3, wDate);
				wStatement.setInt(7, wFrequency--);

				mDbAccess.executeUpdate(wStatement);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	private static void updateRecordNoFreq(RecordTableItem pBeforeRecord,
			RecordTableItem pAfterRecord, String pNote) {
		String wDate = mDateFormat.format(pAfterRecord.getDate());
		// update cbt_act set 
		//	BOOK_ID = ?,	// 1 (int)
		//	ITEM_ID = ?,	// 2 (int)
		//	ACT_DT = ?,		// 3 (String)
		//	INCOME = ?,		// 4 (int)
		//	EXPENSE = ?,	// 5 (int)
		//	NOTE_NAME = ?	// 6 (String)
		// where ACT_ID = ? // 7 (int)
		StringBuilder wQueryBuilder = new StringBuilder()
				.append("update ").append(mActTable).append(" set ")
				.append(mBookIdCol).append(" = ?, ")
				.append(mItemIdCol).append(" = ?, ")
				.append(mActDtCol).append(" = ?, ")
				.append(mActIncomeCol).append(" = ?, ")
				.append(mActExpenseCol).append(" = ?, ")
				.append(mNoteNameCol).append(" = ?")
				.append(" where ").append(mActIdCol).append(" = ?");
		
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			int i = 1;
			wStatement.setInt(i++, pAfterRecord.getBook().getId());
			wStatement.setInt(i++, pAfterRecord.getItem().getId());
			wStatement.setString(i++, wDate);
			wStatement.setInt(i++, pAfterRecord.getIncome());
			wStatement.setInt(i++, pAfterRecord.getExpense());
			wStatement.setString(i++, pNote);
			wStatement.setInt(i++, pBeforeRecord.getId());
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	public static void insertNewMoveRecord(RecordTableItemForMove pMoveItem) {
		int wGroupId = getNewGroupId();

		if (pMoveItem.getFrequency() == 0) {
			insertNewMoveRecordNoFreq(pMoveItem, wGroupId);
		} else { // pFrequency > 0
			insertNewMoveRecordFreq(pMoveItem, wGroupId);
		}

		// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
		if (!"".equals(pMoveItem.getNote())) {
			updateNoteTable(getMoveIncomeItemId(), pMoveItem.getNote());
		}
	}

	private static void insertNewMoveRecordFreq(RecordTableItemForMove pMoveItem, 
			int pGroupId) {
		// insert into cbt_act (BOOK_ID, ITEM_ID, INCOME, EXPENSE, GROUP_ID, ACT_DT, NOTE_NAME, FREQUENCY)
		// values(?,?,?,?,?,?,?,?),	// From
		// values(?,?,?,?,?,?,?,?)	// To
		StringBuilder wQueryBuilder = new StringBuilder()
				.append("insert into ").append(mActTable).append(" (")
				.append(mBookIdCol).append(",").append(mItemIdCol).append(",")
				.append(mActIncomeCol).append(",").append(mActExpenseCol).append(",")
				.append(mGroupIdCol).append(",").append(mActDtCol).append(",")
				.append(mNoteNameCol).append(",").append(mActFreqCol)
				.append(") values(?,?,?,?,?,?,?,?), (?,?,?,?,?,?,?,?)");

		int wFrequency = pMoveItem.getFrequency();
		for (int i = 0; i < pMoveItem.getFrequency() + 1; i++) {
			Calendar wCal = pMoveItem.getCal();
			wCal.add(Calendar.MONTH, +i);
			String wDate = mDateFormat.format(wCal.getTime());

			try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
				int j=1;
				wStatement.setInt(j++, pMoveItem.getFromBook().getId());
				wStatement.setInt(j++, getMoveExpenseItemId());
				wStatement.setInt(j++, 0);
				wStatement.setInt(j++, pMoveItem.getValue());
				wStatement.setInt(j++, pGroupId);
				wStatement.setString(j++, wDate);
				wStatement.setString(j++, pMoveItem.getNote());
				wStatement.setInt(j++, wFrequency);
				
				wStatement.setInt(j++, pMoveItem.getToBook().getId());
				wStatement.setInt(j++, getMoveIncomeItemId());
				wStatement.setInt(j++, pMoveItem.getValue());
				wStatement.setInt(j++, 0);
				wStatement.setInt(j++, pGroupId);
				wStatement.setString(j++, wDate);
				wStatement.setString(j++, pMoveItem.getNote());
				wStatement.setInt(j++, wFrequency--);
				mDbAccess.executeUpdate(wStatement);
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		}
	}

	private static void insertNewMoveRecordNoFreq(RecordTableItemForMove pMoveItem,
			int pGroupId) {
		// insert into cbt_act (BOOK_ID, ITEM_ID, INCOME, EXPENSE, GROUP_ID, ACT_DT, NOTE_NAME)
		// values(?,?,?,?,?,?,?),	// From
		// values(?,?,?,?,?,?,?)	// To
		StringBuilder wQueryBuilder = new StringBuilder()
				.append("insert into ").append(mActTable).append(" (")
				.append(mBookIdCol).append(",").append(mItemIdCol).append(",")
				.append(mActIncomeCol).append(",").append(mActExpenseCol).append(",")
				.append(mGroupIdCol).append(",").append(mActDtCol).append(",").append(mNoteNameCol)
				.append(") values(?,?,?,?,?,?,?), (?,?,?,?,?,?,?)");
		String wDate = mDateFormat.format(pMoveItem.getDate());
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			int i=1;
			wStatement.setInt(i++, pMoveItem.getFromBook().getId());
			wStatement.setInt(i++, getMoveExpenseItemId());
			wStatement.setInt(i++, 0);
			wStatement.setInt(i++, pMoveItem.getValue());
			wStatement.setInt(i++, pGroupId);
			wStatement.setString(i++, wDate);
			wStatement.setString(i++, pMoveItem.getNote());
			
			wStatement.setInt(i++, pMoveItem.getToBook().getId());
			wStatement.setInt(i++, getMoveIncomeItemId());
			wStatement.setInt(i++, pMoveItem.getValue());
			wStatement.setInt(i++, 0);
			wStatement.setInt(i++, pGroupId);
			wStatement.setString(i++, wDate);
			wStatement.setString(i++, pMoveItem.getNote());
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	private static boolean groupShouldBeChanged(RecordTableItemForMove pBeforeItem, RecordTableItemForMove pAfterItem) {
		// if (pBeforeItem.getGroupId() == 0 && pAfterItem.getFrequency() > 0)
		// return true;
		return pAfterItem.getYear() != pBeforeItem.getYear()
				|| pAfterItem.getMonth() != pBeforeItem.getMonth()
				|| !pAfterItem.getFromBook().equals(pBeforeItem.getFromBook())
				|| !pAfterItem.getToBook().equals(pBeforeItem.getToBook());
	}

	public static void updateMoveRecord(RecordTableItemForMove pBeforeItem,
			RecordTableItemForMove pAfterItem) {
		if (pBeforeItem.getGroupId() == 0) {
			MessageDialog.openError(Display.getCurrent().getShells()[0], "Error", "GroupIdが0です");
			return;
		}

		String wNote = pAfterItem.getNote();

		// ともに繰り返し0ならUpdateのみ
		if (pBeforeItem.getFrequency() == 0 && pAfterItem.getFrequency() == 0) {
			updateMoveRecordNoFreq(pBeforeItem, pAfterItem, wNote);

		} else {
			// どちらかが繰り返しありなら既存レコードを削除して新規追加
			updateMoveRecordFreq(pBeforeItem, pAfterItem, wNote);
		}

		// Noteが空でない場合はNoteTableに追加（同じ名前の既存レコードは削除）
		if (!"".equals(wNote))
			updateNoteTable(getMoveIncomeItemId(), wNote);
	}

	private static void updateMoveRecordFreq(RecordTableItemForMove pBeforeItem,
			RecordTableItemForMove pAfterItem, String pNote) {
		// 既存のレコードを削除
		deleteGroupRecord(pBeforeItem.getDate(), pBeforeItem.getGroupId());

		int wGroupId = groupShouldBeChanged(pBeforeItem, pAfterItem) ? getNewGroupId()
				: pBeforeItem.getGroupId();
		
		int wFrequency = pAfterItem.getFrequency();

		// 新規のレコードを追加
		// insert into cbt_act 
		// (BOOK_ID, ITEM_ID, ACT_DT, INCOME, EXPENSE, GROUP_ID, FREQUENCY, NOTE_NAME)
		// values(?,?,?,?,?,?,?,?), (?,?,?,?,?,?,?,?) 
		StringBuilder wQueryBuilder = new StringBuilder("insert into ").append(mActTable).append(" ( ")
					.append(mBookIdCol).append(",").append(mItemIdCol).append(",")
					.append(mActDtCol).append(",").append(mActIncomeCol).append(",")
					.append(mActExpenseCol).append(",").append(mGroupIdCol).append(",")
					.append(mActFreqCol).append(",").append(mNoteNameCol)
					.append(") values(?,?,?,?,?,?,?,?), (?,?,?,?,?,?,?,?)");
		for (int i = 0; i < pAfterItem.getFrequency() + 1; i++) {
			Calendar wCal = pAfterItem.getCal();
			wCal.add(Calendar.MONTH, +i);
			String wDate = mDateFormat.format(wCal.getTime());

			try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
				int j=1;
				wStatement.setInt(j++, pAfterItem.getFromBook().getId());
				wStatement.setInt(j++, getMoveExpenseItemId());
				wStatement.setString(j++, wDate);
				wStatement.setInt(j++, 0);
				wStatement.setInt(j++, pAfterItem.getValue());
				wStatement.setInt(j++, wGroupId);
				wStatement.setInt(j++, wFrequency);
				wStatement.setString(j++, pNote);

				wStatement.setInt(j++, pAfterItem.getToBook().getId());
				wStatement.setInt(j++, getMoveIncomeItemId());
				wStatement.setString(j++, wDate);
				wStatement.setInt(j++, pAfterItem.getValue());
				wStatement.setInt(j++, 0);
				wStatement.setInt(j++, wGroupId);
				wStatement.setInt(j++, wFrequency--);
				wStatement.setString(j++, pNote);
				mDbAccess.executeUpdate(wStatement);
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		}
	}

	private static void updateMoveRecordNoFreq(RecordTableItemForMove pBeforeItem,
			RecordTableItemForMove pAfterItem, String pNote) {
					String wDate = mDateFormat.format(pAfterItem.getDate());
					
					// update cbt_act set 
					//	BOOK_ID = ?,	// 1 (int)
					//	ITEM_ID = ?,	// 2 (int)
					//	ACT_DT = ?,		// 3 (String)
					//	INCOME = ?,		// 4 (int)
					//	EXPENSE = ?,	// 5 (int)
					//	NOTE_NAME = ?	// 6 (String)
					// where ACT_ID = ?	// 7 (int)
					StringBuilder wQueryBuilder = new StringBuilder()
							.append("update ").append(mActTable).append(" set ")
							.append(mBookIdCol).append(" = ?,").append(mItemIdCol).append(" = ?,")
							.append(mActDtCol).append(" = ?,").append(mActIncomeCol).append(" = ?,")
							.append(mActExpenseCol).append(" = ?,").append(mNoteNameCol).append(" = ?")
							.append(" where ").append(mActIdCol).append(" = ?");
					try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
						// From
						int i=1;
						wStatement.setInt(i++, pAfterItem.getFromBook().getId());
						wStatement.setInt(i++, getMoveExpenseItemId());
						wStatement.setString(i++, wDate);
						wStatement.setInt(i++, 0);
						wStatement.setInt(i++, pAfterItem.getValue());
						wStatement.setString(i++, pNote);
						wStatement.setInt(i++, pBeforeItem.getFromActId());
						mDbAccess.executeUpdate(wStatement);
						
						// To
						i=1;
						wStatement.setInt(i++, pAfterItem.getToBook().getId());
						wStatement.setInt(i++, getMoveIncomeItemId());
						wStatement.setString(i++, wDate);
						wStatement.setInt(i++, pAfterItem.getValue());
						wStatement.setInt(i++, 0);
						wStatement.setString(i++, pNote);
						wStatement.setInt(i++, pBeforeItem.getToActId());
						mDbAccess.executeUpdate(wStatement);
					} catch (SQLException e) {
						resultSetHandlingError(e);
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
		// delete from cbt_act where ACT_ID = ?
		StringBuilder wQueryBuilder = new StringBuilder("delete from ").append(mActTable).append(" where ")
				.append(mActIdCol).append(" = ?");
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			wStatement.setInt(1, pRecordTableItem.getId());
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	// 複数レコード（同一GroupId,対象日付以降）削除
	private static void deleteGroupRecord(Date pDate, int pGroupId) {
		if (pGroupId == 0) {
			MessageDialog.openError(Display.getCurrent().getShells()[0], "Error", "GroupIdが0です");
			return;
		}
		
		// delete from cbt_act where GROUP_ID = ? and ACT_DT >= ?
		StringBuilder wQueryBuilder = new StringBuilder("delete from ").append(mActTable)
				.append(" where ").append(mGroupIdCol).append(" = ?")
				.append(" and ").append(mActDtCol).append(" >= ?");
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			wStatement.setInt(1, pGroupId);
			wStatement.setString(2, mDateFormat.format(pDate));
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	private static int getMoveIncomeItemId() {
		return mIncomeRexp;
	}

	private static int getMoveExpenseItemId() {
		return mExpenseRexp;
	}

	public static RecordTableItem getMovePairRecord(RecordTableItem pRecord) {

		// select ACT_ID, BOOK_ID, ACT_DT, cbt_act.ITEM_ID, GROUP_ID, INCOME, EXPENSE, FREQUENCY, NOTE_NAME
		// from cbt_act, cbm_item, cbm_category
		// where cbm_item.ITEM_ID = cbt_act.ITEM_ID
		//	and cbm_item.CATEGORY_ID = cbm_category.CATEGORY_ID
		//	and GROUP_ID = ?	// 1 (int)
		//	and ACT_DT = ?		// 2 (String)
		//	and ACT_ID <> ?		// 3 (int)
		StringBuilder wQueryBuilder = new StringBuilder()
			.append("select ").append(mActIdCol).append(", ")
				.append(mBookIdCol).append(", ").append(mActDtCol).append(", ")
				.append(mActTable).append(".").append(mItemIdCol).append(", ")
				.append(mItemTable).append(".").append(mItemNameCol).append(", ")
				.append(mCategoryTable).append(".").append(mCategoryIdCol).append(", ")
				.append(mCategoryTable).append(".").append(mCategoryNameCol).append(", ")
				.append(mCategoryTable).append(".").append(mCategoryRexpCol).append(", ")
				.append(mGroupIdCol).append(", ").append(mActIncomeCol).append(", ")
				.append(mActExpenseCol).append(", ").append(mActFreqCol).append(", ")
				.append(mNoteNameCol)
			.append(" from ").append(mActTable).append(", ").append(mItemTable).append(", ")
				.append(mCategoryTable)
			.append(" where ").append(mItemTable).append(".").append(mItemIdCol)
				.append(" = ").append(mActTable).append(".").append(mItemIdCol)
				.append(" and ").append(mItemTable).append(".").append(mCategoryIdCol)
				.append(" = ").append(mCategoryTable).append(".").append(mCategoryIdCol)
				.append(" and ").append(mGroupIdCol).append(" = ?")
				.append(" and ").append(mActDtCol).append(" = ?")
				.append(" and ").append(mActIdCol).append(" <> ?");

		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			int i=1;
			wStatement.setInt(i++, pRecord.getGroupId());
			wStatement.setString(i++, mDateFormat.format(pRecord.getDate()));
			wStatement.setInt(i++, pRecord.getId());
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				wResultSet.next();
				return generateRecordTableItem(wResultSet);
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		return null;
	}

	public static boolean isMoveItem(Item pItem) {
		return pItem.getId() == getMoveIncomeItemId() || pItem.getId() == getMoveExpenseItemId();
	}

	private static SummaryItemsCommon getSummaryItemsCommon(DateRange pDateRange) {
		// select TOP.OP, TAP.AP, TAB.AB, TTB.TB from
		//	(select sum(cbt_act.INCOME - cbt_act.EXPENSE) as OP
//				from cbm_item inner join cbt_act on cbt_act.ITEM_ID = cbm_item.ITEM_ID
//					inner join cbm_category on cbm_category.CATEGORY_ID = cbm_item.CATEGORY_ID
//				where cbt_act.DEL_FLG = b'0' and cbt_act.ACT_DT between ?' and ?	// 1,2 (String)
//					and cbm_category.TEMP_FLG = b'0' and cbm_category.SPECIAL_FLG = b'0') as TOP,
		//	(select sum(cbt_act.INCOME - cbt_act.EXPENSE) as AP
//				from cbm_item inner join cbt_act on cbt_act.ITEM_ID = cbm_item.ITEM_ID
//					inner join cbm_category on cbm_category.CATEGORY_ID = cbm_item.CATEGORY_ID
//				where cbt_act.DEL_FLG = b'0' and cbt_act.ACT_DT between ? and ?		// 3,4 (String)
//					and cbm_category.TEMP_FLG = b'0') as TAP,
		//	(select COALESCE(sum(cbt_act.INCOME - cbt_act.EXPENSE),0) + (select sum(BALANCE) from cbm_book) as AB
//				from cbm_item inner join cbt_act on cbt_act.ITEM_ID = cbm_item.ITEM_ID
			//		inner join cbm_category on cbm_category.CATEGORY_ID = cbm_item.CATEGORY_ID
//				where cbt_act.DEL_FLG = b'0' and cbt_act.ACT_DT <= ? and cbm_category.TEMP_FLG = b'0') as TAB,
								// 5 (String)
		//	(select sum(cbt_act.INCOME - cbt_act.EXPENSE) as TB
//				from cbm_item inner join cbt_act on cbt_act.ITEM_ID = cbm_item.ITEM_ID
//					inner join cbm_category on cbm_category.CATEGORY_ID = cbm_item.CATEGORY_ID
//				where cbt_act.DEL_FLG = b'0' and cbt_act.ACT_DT <= ? and cbm_category.TEMP_FLG = b'1') as TTB
								// 6 (Strng)
		
		String wStartDate = mDateFormat.format(pDateRange.getStartDate());
		String wEndDate = mDateFormat.format(pDateRange.getEndDate());
		
		String wSumProfit = " sum(" + mActTable + "." + mActIncomeCol 
				+ " - " + mActTable + "." + mActExpenseCol + ")";
		String wFrom = " from " + mItemTable + " inner join " + mActTable 
				+ " on " + mActTable + "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol
				+ " inner join " + mCategoryTable
				+ " on " + mCategoryTable + "." + mCategoryIdCol + " = " + mItemTable + "." + mCategoryIdCol;
		
		StringBuilder wQueryBuilder = new StringBuilder("select TOP.OP, TAP.AP, TAB.AB, TTB.TB from")
			.append(" (select").append(wSumProfit).append(" as OP").append(wFrom)
				.append(" where ").append(mActTable).append(".").append(mDelFlgCol).append(" = b'0'")
				.append(" and ").append(mActTable).append(".").append(mActDtCol).append(" between ? and ?")
				.append(" and ").append(mCategoryTable).append(".").append(mCategoryTempFlgCol).append(" = b'0'")
				.append(" and ").append(mCategoryTable).append(".").append(mCategorySpecialFlgCol).append(" = b'0') as TOP,")
			.append(" (select").append(wSumProfit).append(" as AP").append(wFrom)
				.append(" where ").append(mActTable).append(".").append(mDelFlgCol).append(" = b'0'")
				.append(" and ").append(mActTable).append(".").append(mActDtCol).append(" between ? and ?")
				.append(" and ").append(mCategoryTable).append(".").append(mCategoryTempFlgCol).append(" = b'0') as TAP,")
			.append(" (select COALESCE(").append(wSumProfit).append(",0)")
				.append(" + (select sum(").append(mBookBalanceCol).append(") from ").append(mBookTable).append(")")
				.append(" as AB").append(wFrom)
				.append(" where ").append(mActTable).append(".").append(mDelFlgCol).append(" = b'0'")
				.append(" and ").append(mActTable).append(".").append(mActDtCol).append(" <= ?")
				.append(" and ").append(mCategoryTable).append(".").append(mCategoryTempFlgCol).append(" = b'0') as TAB,")
			.append(" (select").append(wSumProfit).append(" as TB").append(wFrom)
				.append(" where ").append(mActTable).append(".").append(mDelFlgCol).append(" = b'0'")
				.append(" and ").append(mActTable).append(".").append(mActDtCol).append(" <= ?")
				.append(" and ").append(mCategoryTable).append(".").append(mCategoryTempFlgCol).append(" = b'1') as TTB");
		
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			int i=1;
			for (int j=0; j < 2; j++) {
				wStatement.setString(i++, wStartDate);
				wStatement.setString(i++, wEndDate);
			}
			for (int j=0; j < 2; j++) {
				wStatement.setString(i++, wEndDate);
			}
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				wResultSet.next();
				int wOperationalProfit = wResultSet.getInt("TOP.OP");
				int wActualProfit = wResultSet.getInt("TAP.AP");
				int wActualBalance = wResultSet.getInt("TAB.AB");
				int wTempBalance = wResultSet.getInt("TTB.TB");
				return new SummaryItemsCommon.Builder().operationalProfit(wOperationalProfit)
						.actualProfit(wActualProfit).actualBalance(wActualBalance).tempBalance(wTempBalance).build();
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		
		return null;
		
	}

	private static List<SummaryTableItem> getSummaryTableItemsOfCategory(Book pBook,
			DateRange pDateRange) {

		// select cbm_category.REXP_DIV, cbm_category.CATEGORY_ID, cbm_category.CATEGORY_NAME, 
		//	sum(cbt_act.INCOME) INCOME, sum(cbt_act.EXPENSE) EXPENSE
		// from cbm_item inner join cbt_act on cbt_act.ITEM_ID = cbm_item.ITEM_ID
		//	inner join cbm_category on cbm_category.CATEGORY_ID = cbm_item.CATEGORY_ID
		// where ACT_DT between ? and ?
		//	(and cbt_act.BOOK_ID = ?)		// For book
		//  (and cbm_item.MOVE_FLG = b'0')	// For all books
		//	and cbt_act.DEL_FLG = b'0'
		//	and (cbt_act.INCOME > 0 or cbt_act.EXPENSE > 0)
		// group by cbm_category.CATEGORY_ID
		// order by cbm_category.REXP_DIV, cbm_category.TEMP_FLG, cbm_category.SPECIAL_FLG,
		//	cbm_category.SORT_KEY
		String wFrom = " from " + mItemTable + " inner join " + mActTable 
				+ " on " + mActTable + "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol
				+ " inner join " + mCategoryTable
				+ " on " + mCategoryTable + "." + mCategoryIdCol + " = " + mItemTable + "." + mCategoryIdCol;
		StringBuilder wQueryBuilder = new StringBuilder("select ")
					.append(mCategoryTable).append(".").append(mCategoryRexpCol).append(", ")
					.append(mCategoryTable).append(".").append(mCategoryIdCol).append(", ")
					.append(mCategoryTable).append(".").append(mCategoryNameCol).append(", ")
					.append("sum(").append(mActTable).append(".").append(mActIncomeCol).append(") INCOME, ")
					.append("sum(").append(mActTable).append(".").append(mActExpenseCol).append(") EXPENSE")
				.append(wFrom)
				.append(" where ").append(mActTable).append(".").append(mActDtCol).append(" between ? and ?")
					.append(" and ").append(mActTable).append(".").append(mDelFlgCol).append(" = b'0'")
					.append(" and (").append(mActTable).append(".").append(mActIncomeCol).append(" > 0")
					.append(" or ").append(mActTable).append(".").append(mActExpenseCol).append(" > 0)");
		if (pBook.isAllBook()) {
			wQueryBuilder.append(" and ").append(mItemTable).append(".").append(mMoveFlgCol).append(" = b'0'");
		} else {
			wQueryBuilder.append(" and ").append(mActTable).append(".").append(mBookIdCol).append(" = ?");
		}
		wQueryBuilder.append(" group by ").append(mCategoryTable).append(".").append(mCategoryIdCol)
				.append(" order by ").append(mCategoryTable).append(".").append(mCategoryRexpCol).append(", ")
					.append(mCategoryTable).append(".").append(mCategoryTempFlgCol).append(", ")
					.append(mCategoryTable).append(".").append(mCategorySpecialFlgCol).append(", ")
					.append(mCategoryTable).append(".").append(mSortKeyCol);
		
		List<SummaryTableItem> wSummaryTableItems = new ArrayList<>();

		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())){
			wStatement.setString(1, mDateFormat.format(pDateRange.getStartDate()));
			wStatement.setString(2, mDateFormat.format(pDateRange.getEndDate()));
			if (!pBook.isAllBook()) {
				wStatement.setInt(3, pBook.getId());
			}
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				while (wResultSet.next()) {
					Category wCategory = getCategory(wResultSet);
					int wValue = wCategory.getIncomeExpenseType() == IncomeExpenseType.INCOME ?
							wResultSet.getInt("INCOME") : wResultSet.getInt("EXPENSE");
					wSummaryTableItems.add(SummaryTableItemFactory.createCategory(wCategory, wValue));
				}
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wSummaryTableItems;
	}
	
	private static SummaryTableItemsNormal getSummaryTableItemsOfNormalItem(Book pBook, DateRange pDateRange) {
		// select cbm_category.CATEGORY_ID, cbm_item.ITEM_ID, cbm_item.ITEM_NAME, cbm_item.SORT_KEY, 
		//	sum(cbt_act.INCOME) INCOME, sum(cbt_act.EXPENSE) EXPENSE
		// from cbm_item inner join cbt_act on cbt_act.ITEM_ID = cbm_item.ITEM_ID
		//	inner join cbm_category on cbm_category.CATEGORY_ID = cbm_item.CATEGORY_ID
		// where ACT_DT between ? and ?
		//	(and cbt_act.BOOK_ID = ?)		// For book
		//  (and cbm_item.MOVE_FLG = b'0')	// For all books
		//	and cbt_act.DEL_FLG = b'0'
		//	and (cbt_act.INCOME > 0 or cbt_act.EXPENSE > 0)
		// group by cbm_item.ITEM_ID with rollup
		
		String wFrom = " from " + mItemTable + " inner join " + mActTable 
				+ " on " + mActTable + "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol
				+ " inner join " + mCategoryTable
				+ " on " + mCategoryTable + "." + mCategoryIdCol + " = " + mItemTable + "." + mCategoryIdCol;
		
		StringBuilder wQueryBuilder = new StringBuilder("select ")
				.append(mCategoryTable).append(".").append(mCategoryIdCol).append(", ")
				.append(mItemTable).append(".").append(mItemIdCol).append(", ")
				.append(mItemTable).append(".").append(mItemNameCol).append(", ")
				.append(mItemTable).append(".").append(mSortKeyCol).append(", ")
				.append("sum(").append(mActTable).append(".").append(mActIncomeCol).append(") INCOME, ")
				.append("sum(").append(mActTable).append(".").append(mActExpenseCol).append(") EXPENSE")
			.append(wFrom)
			.append(" where ").append(mActTable).append(".").append(mActDtCol).append(" between ? and ?")
				.append(" and ").append(mActTable).append(".").append(mDelFlgCol).append(" = b'0'")
				.append(" and (").append(mActTable).append(".").append(mActIncomeCol).append(" > 0 or ")
				.append(mActTable).append(".").append(mActExpenseCol).append(" > 0)");
		if (pBook.isAllBook()) {
			wQueryBuilder.append(" and ").append(mItemTable).append(".").append(mMoveFlgCol).append(" = b'0'");
		} else {
			wQueryBuilder.append(" and ").append(mActTable).append(".").append(mBookIdCol).append(" = ?");
		}
		wQueryBuilder.append(" group by ").append(mItemTable).append(".").append(mItemIdCol).append(" with rollup");

		Map<Integer, SummaryTableItem> wSummaryTableMap = new TreeMap<>();
		int wAppearedIncome = 0;
		int wAppearedExpense = 0;
		
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())){
			wStatement.setString(1, mDateFormat.format(pDateRange.getStartDate()));
			wStatement.setString(2, mDateFormat.format(pDateRange.getEndDate()));
			if (!pBook.isAllBook()) {
				wStatement.setInt(3, pBook.getId());
			}
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				while (wResultSet.next()) {
					wResultSet.getInt(mItemTable + "." + mItemIdCol);
					if (wResultSet.wasNull()) {
						wAppearedIncome = wResultSet.getInt("INCOME");
						wAppearedExpense = wResultSet.getInt("EXPENSE");
						continue;
					}
					int wSortKey = wResultSet.getInt(mItemTable + "." + mSortKeyCol);
					int wCategoryId = wResultSet.getInt(mCategoryTable + "." + mCategoryIdCol);
					Category wCategory = Category.getCategory(wCategoryId);
					Item wItem = getItem(wResultSet, wCategory);
//					String wItemName = wResultSet
//							.getString(mItemTable + "." + mItemNameCol);
					int wValue = wCategory.getIncomeExpenseType() == IncomeExpenseType.INCOME ? wResultSet.getInt("INCOME") : wResultSet.getInt("EXPENSE");
//					if (Item.getItem(wItemId) == null) {
//						Item.generateItem(wItemId, wItemName, wCategory);
//					}
					while (wSummaryTableMap.get(wSortKey) != null) {
						wSortKey--;
					}
					wSummaryTableMap.put(wSortKey, SummaryTableItemFactory.createItem(wItem, wValue));
				}
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return new SummaryTableItemsNormal(wAppearedIncome, wAppearedExpense, new ArrayList<SummaryTableItem>(wSummaryTableMap.values()));
	}

	public static SummaryTableItemCollection getSummaryTableItems(Book pBook, DateRange pDateRange) {
		return new SummaryTableItemCollection(getSummaryItemsCommon(pDateRange), getSummaryTableItemsNormal(pBook, pDateRange));
	}
	
	public static SummaryTableItemsNormal getSummaryTableItemsNormal(Book pBook, DateRange pDateRange) {
		// Category
		List<SummaryTableItem> wSummaryTableItemsCategory = getSummaryTableItemsOfCategory(pBook, pDateRange);
		
		// Item
		SummaryTableItemsNormal wSummaryTableItemsNormal = getSummaryTableItemsOfNormalItem(pBook, pDateRange);
		wSummaryTableItemsNormal.setCategoryItems(wSummaryTableItemsCategory);

		return wSummaryTableItemsNormal;
	}
	
	private static String getQueryStringForAnnualSummaryTableItems(Book pBook,
			AnnualDateRange pAnnualDateRange, boolean pItem) {

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
			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ? and ?")
					.append(" then ")
					.append(mActIncomeCol).append(" end),0) ").append(mActIncomeCol)
					.append(mPeriodName).append(i);
			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ? and ?")
					.append(" then ")
					.append(mActExpenseCol).append(" end),0) ").append(mActExpenseCol)
					.append(mPeriodName).append(i);
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
		wQueryBuilder.append(" and ").append(mActDtCol).append(" between ? and ?");

		if (pBook.isAllBook()) {
			wQueryBuilder.append(" and ").append(mItemTable).append(".").append(mMoveFlgCol)
					.append(" = b'0' ");
		} else {
			wQueryBuilder.append(" and ").append(mActTable).append(".").append(mBookIdCol)
					.append(" = ?");
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

//		String wNameKey = pItem ? (mItemTable + "." + mItemNameCol)
//				: (mCategoryTable + "." + mCategoryNameCol);
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
				if (pItem) {
					wList.add(SummaryTableItemFactory.createItem(getItem(pResultSet, getCategory(pResultSet)),
							(wRexpDiv == mIncomeRexp) ? wIncome : wExpense));
//					wList.add(SummaryTableItemFactory.createItem(pResultSet.getString(wNameKey),
//							(wRexpDiv == mIncomeRexp) ? wIncome : wExpense, Item.getItem(pId)));
				} else {
					wList.add(SummaryTableItemFactory.createCategory(getCategory(pResultSet),
							wRexpDiv == mIncomeRexp ? wIncome : wExpense));
//					wList.add(SummaryTableItemFactory.createCategory(
//							pResultSet.getString(wNameKey), (wRexpDiv == mIncomeRexp) ? wIncome
//									: wExpense, Category.getCategory(pId)));
				}
			}

			wKey = wRexpDiv * 1000000 + wCategorySortKey * 1000
					+ (pItem ? pResultSet.getInt(mItemTable + "." + mSortKeyCol) : 0);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		pSummaryTableItemListMap.put(wKey, wList);
	}

	public static List<SummaryTableItem[]> getAnnualSummaryTableItems(Book pBook,
			AnnualDateRange pAnnualDateRange, boolean pItem) {

		Map<Integer, List<SummaryTableItem>> wSummaryTableItemListMap = new TreeMap<Integer, List<SummaryTableItem>>();

		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(
				getQueryStringForAnnualSummaryTableItems(pBook, pAnnualDateRange, pItem))) {
			
			int j=1;
			for (int i = 0; i < pAnnualDateRange.size(); i++) {
				DateRange wDateRange = pAnnualDateRange.getDateRangeList().get(i);
				String wStartDateString = mDateFormat.format(wDateRange.getStartDate());
				String wEndDateString = mDateFormat.format(wDateRange.getEndDate());
				for (int k=0; k < 2; k++) {
					wStatement.setString(j++, wStartDateString);
					wStatement.setString(j++, wEndDateString);
				}
			}
			wStatement.setString(j++, mDateFormat.format(pAnnualDateRange.getStartDate()));
			wStatement.setString(j++, mDateFormat.format(pAnnualDateRange.getEndDate()));
			if (!pBook.isAllBook()) {
				wStatement.setInt(j++, pBook.getId());
			}
			
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
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
				
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
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
		StringBuilder wQueryBuilder = new StringBuilder("select (select COALESCE(sum(")
				.append(mActIncomeCol).append(" - ").append(mActExpenseCol).append("),0)")
				.append(" from ").append(mActTable).append(", ").append(mItemTable)
				.append(", ").append(mCategoryTable)
				.append(" where ").append(mActTable).append(".").append(mItemIdCol)
				.append(" = ").append(mItemTable).append(".").append(mItemIdCol)
				.append(" and ").append(mItemTable).append(".").append(mCategoryIdCol)
				.append(" = ").append(mCategoryTable).append(".").append(mCategoryIdCol)
				.append(" and ").append(mActTable).append(".").append(mDelFlgCol)
				.append(" = b'0'")
				.append(" and ").append(mItemTable).append(".").append(mMoveFlgCol)
				.append(" = b'0'")
				.append(" and ").append(mActDtCol).append(" < ?")//.append(wTotalStartDateString)
				.append(") ").append(mAppearedBalanceName);
		wQueryBuilder.append(", (select COALESCE(sum(")
				.append(mActIncomeCol).append(" - ").append(mActExpenseCol).append("),0)")
				.append(" from ").append(mActTable).append(", ").append(mItemTable)
				.append(", ").append(mCategoryTable)
				.append(" where ").append(mActTable).append(".").append(mItemIdCol)
				.append(" = ").append(mItemTable).append(".").append(mItemIdCol)
				.append(" and ").append(mItemTable).append(".").append(mCategoryIdCol)
				.append(" = ").append(mCategoryTable).append(".").append(mCategoryIdCol)
				.append(" and ").append(mActTable).append(".").append(mDelFlgCol)
				.append(" = b'0'")
				.append(" and ").append(mItemTable).append(".").append(mMoveFlgCol)
				.append(" = b'0'")
				.append(" and ").append(mActDtCol).append(" < ?")//.append(wTotalStartDateString)
				.append(" and ").append(mCategoryTable).append(".").append(mCategoryTempFlgCol)
				.append(" = b'1') ").append(mTempBalanceName);
		for (int i = 0; i < pAnnualDateRange.size(); i++) {
			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ? and ?")
					.append(" then ").append(mActIncomeCol).append(" end),0) ")
					.append(mAppearedIncomeName).append(i);
			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ? and ?")
					.append(" then ")
					.append(mActExpenseCol).append(" end),0) ").append(mAppearedExpenseName)
					.append(i);

			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ? and ?")
					.append(" and ").append(mCategoryTable).append(".")
					.append(mCategorySpecialFlgCol).append(" = b'1' then ").append(mActIncomeCol)
					.append(" end),0) ").append(mSpecialIncomeName).append(i);
			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ? and ?")
					.append(" and ")
					.append(mCategoryTable).append(".").append(mCategorySpecialFlgCol)
					.append(" = b'1' then ").append(mActExpenseCol).append(" end),0) ")
					.append(mSpecialExpenseName).append(i);

			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ? and ?")
					.append(" and ").append(mCategoryTable).append(".").append(mCategoryTempFlgCol)
					.append(" = b'1' then ").append(mActIncomeCol).append(" end),0) ")
					.append(mTempIncomeName).append(i);
			wQueryBuilder.append(", COALESCE(sum(case when ").append(mActDtCol).append(" between ? and ?")
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
		wQueryBuilder.append(" and ").append(mActTable).append(".").append(mActDtCol)
				.append(" between ? and ?");
		
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

		int wAppearedBalance = getInitialBalance(Book.getAllBook());
		int wActualBalance = 0;
		int wTempBalance = 0;
		
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(
				getQueryStringForAnnualSummaryTableItemsOriginal(pAnnualDateRange))) {
			int j = 1;
			String wTotalStartDateString = mDateFormat.format(pAnnualDateRange.getStartDate());
			for (int i=0; i < 2; i++) {
				wStatement.setString(j++, wTotalStartDateString);
			}
			for (int i = 0; i < pAnnualDateRange.size(); i++) {
				DateRange wDateRange = pAnnualDateRange.getDateRangeList().get(i);
				String wStartDateString = mDateFormat.format(wDateRange.getStartDate());
				String wEndDateString = mDateFormat.format(wDateRange.getEndDate());
				for (int k=0; k < 6; k++) {
					wStatement.setString(j++, wStartDateString);
					wStatement.setString(j++, wEndDateString);
				}
			}
			wStatement.setString(j++, mDateFormat.format(pAnnualDateRange.getStartDate()));
			wStatement.setString(j++, mDateFormat.format(pAnnualDateRange.getEndDate()));
			
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
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
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
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

		EnumMap<IncomeExpenseType, Map<Integer, ConfigItem>> wResultMap = new EnumMap<>(IncomeExpenseType.class);
		wResultMap.put(IncomeExpenseType.INCOME, new LinkedHashMap<Integer, ConfigItem>());
		wResultMap.put(IncomeExpenseType.EXPENCE, new LinkedHashMap<Integer, ConfigItem>());

		// Category一覧の取得
		// select CATEGORY_ID, CATEGORY_NAME, REXP_DIV
		// from cbm_category
		// where DEL_FLG = b'0' and SORT_KEY > 0
		// order by SORT_KEY
		StringBuilder wQueryBuilder = new StringBuilder("select ").append(mCategoryIdCol)
				.append(", ").append(mCategoryNameCol).append(", ").append(mCategoryRexpCol)
			.append(" from ").append(mCategoryTable)
			.append(" where ").append(mDelFlgCol).append(" = b'0' and ")
				.append(mSortKeyCol).append(" > 0")
				.append(" order by ").append(mSortKeyCol);

		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				while (wResultSet.next()) {
					Category wCategory = getCategory(wResultSet);
					wResultMap.get(wCategory.getIncomeExpenseType())
					.put(wCategory.getId(), new ConfigItem(wCategory));
				}
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
			
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		// Item一覧の取得
		// select CATEGORY_ID, ITEM_ID, ITEM_NAME from cbm_item
		// where DEL_FLG = b'0' and MOVE_FLG = b'0'
		// order by SORT_KEY
		wQueryBuilder = new StringBuilder("select ").append(mCategoryIdCol).append(", ")
				.append(mItemIdCol).append(", ").append(mItemNameCol)
			.append(" from ").append(mItemTable)
			.append(" where ").append(mDelFlgCol).append(" = b'0' and ")
				.append(mMoveFlgCol).append(" = b'0'")
			.append(" order by ").append(mSortKeyCol);

		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				while (wResultSet.next()) {
					int wCategoryId = wResultSet.getInt(mCategoryIdCol);
					Category wCategory = Category.getCategory(wCategoryId);
//					int wItemId = wResultSet.getInt(mItemIdCol);
//					if (Item.getItem(wItemId) == null) {
//						Item.generateItem(wItemId, wResultSet.getString(mItemNameCol), wCategory);
//					}
					Item wItem = getItem(wResultSet, wCategory);
//					String wItemName = wResultSet.getString(mItemNameCol);
					if (wResultMap.get(IncomeExpenseType.INCOME).get(wCategoryId) != null) {
						wResultMap.get(IncomeExpenseType.INCOME).get(wCategoryId).addItem(new ConfigItem(wItem));
						continue;
					}
					if (wResultMap.get(IncomeExpenseType.EXPENCE).get(wCategoryId) != null) {
						wResultMap.get(IncomeExpenseType.EXPENCE).get(wCategoryId).addItem(new ConfigItem(wItem));
						continue;
					}
				}
			
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		// 結果をルートアイテムに格納
		ConfigItem wRootItem = new ConfigItem("");
		ConfigItem wRootIncomeItem = new ConfigItem("収入項目");
		ConfigItem wRootExpenseItem = new ConfigItem("支出項目");
		wRootItem.addItem(wRootIncomeItem);
		wRootItem.addItem(wRootExpenseItem);
		for (ConfigItem wConfigItem : wResultMap.get(IncomeExpenseType.INCOME).values()) {
			wRootIncomeItem.addItem(wConfigItem);
		}
		for (ConfigItem wConfigItem : wResultMap.get(IncomeExpenseType.EXPENCE).values()) {
			wRootExpenseItem.addItem(wConfigItem);
		}
		return wRootItem;
	}

	public static void updateSortKeys(ConfigItem pConfigItem) {

		int wSortKeyCategory = mInitialSortKeyCategory;
		int wSortKeyItem = mInitialSortKeyItem;
		// update cbm_category set SORT_KEY = ? where CATEGORY_ID = ?
		String wQueryCategory = new StringBuilder("update ").append(mCategoryTable)
				.append(" set ").append(mSortKeyCol).append(" = ?").append(wSortKeyCategory)
							.append(" where ").append(mCategoryIdCol).append(" = ?").toString();
		// update cbm_item set SORT_KEY = ? where ITEM_ID = ?
		String wQueryItem = new StringBuilder("update ").append(mItemTable).append(" set ")
				.append(mSortKeyCol).append(" = ?")
				.append(" where ").append(mItemIdCol).append(" = ?").toString();

		List<ConfigItem> wConfigItemList = new LinkedList<ConfigItem>();
		wConfigItemList.add(pConfigItem);

		try (PreparedStatement wStatementCategory = mDbAccess.getPreparedStatement(wQueryCategory);
				PreparedStatement wStatementItem = mDbAccess.getPreparedStatement(wQueryItem)) {
			while (wConfigItemList.size() > 0) {
				ConfigItem wCurrentItem = wConfigItemList.remove(0);
				if (wCurrentItem.hasItem()) {
					// 子リストの追加
					wConfigItemList.addAll(wCurrentItem.getChildrenAsList());
				}
				if (wCurrentItem.isSpecial()) {
					continue;
				}
				// 自身のアップデート
				if (wCurrentItem.isCategory()) {
					wStatementCategory.setInt(1, wSortKeyCategory++);
					wStatementCategory.setInt(2, wCurrentItem.getCategory().getId());
					mDbAccess.executeUpdate(wStatementCategory);
				} else {
					wStatementItem.setInt(1, wSortKeyItem++);
					wStatementItem.setInt(2, wCurrentItem.getItem().getId());
					mDbAccess.executeUpdate(wStatementItem);
				}
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	public static void insertNewCategory(IncomeExpenseType pType, String pCategoryName) {
		// insert into cbm_category (REXP_DIV, CATEGORY_NAME, SORT_KEY) values (?, ?, ?)
		StringBuilder wQueryBuilder = new StringBuilder("insert into ").append(mCategoryTable)
				.append(" (").append(mCategoryRexpCol).append(", ").append(mCategoryNameCol).append(", ")
				.append(mSortKeyCol).append(") values (?, ?, ?)");
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			wStatement.setInt(1, pType.getCategoryRexp());
			wStatement.setString(2, pCategoryName);
			wStatement.setInt(3, 9999);
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	public static void insertNewItem(Category pCategory, String pItemName) {
		// insert into cbm_item (CATEGORY_ID, ITEM_NAME, SORT_KEY) values (?, ?, ?)
		String wQuery = new StringBuilder("insert into ").append(mItemTable).append(" (")
				.append(mCategoryIdCol).append(", ").append(mItemNameCol).append(", ")
				.append(mSortKeyCol)
				.append(") values (?, ?, ?)").toString();
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setInt(1, pCategory.getId());
			wStatement.setString(2, pItemName);
			wStatement.setInt(3, 9999);
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	public static void updateCategory(Category pCategory, String pNewCategoryName) {
		Category.clear();
		// update cbm_category set CATEGORY_NAME = ? where CATEGORY_ID = ?
		String wQuery = new StringBuilder("update ").append(mCategoryTable)
				.append(" set ").append(mCategoryNameCol).append(" = ?")
				.append(" where ").append(mCategoryIdCol).append(" = ?").toString();
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setString(1, pNewCategoryName);
			wStatement.setInt(2, pCategory.getId());
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	public static void updateItem(Category pCategory, Item pItem, String pNewItemName) {
		Item.clear();
		// update cbm_item set CATEGORY_ID = ?, ITEM_NAME = ? where ITEM_ID = ?
		String wQuery = new StringBuilder("update ").append(mItemTable).append(" set ")
				.append(mCategoryIdCol).append(" = ?, ").append(mItemNameCol).append(" = ?")
				.append(" where ").append(mItemIdCol).append(" = ?").toString();
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setInt(1, pCategory.getId());
			wStatement.setString(2, pNewItemName);
			wStatement.setInt(3, pItem.getId());
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	public static void deleteCategoryItem(ConfigItem pConfigItem) {
		String wTableName = (pConfigItem.isCategory()) ? mCategoryTable : mItemTable;
		String wIdName = (pConfigItem.isCategory()) ? mCategoryIdCol : mItemIdCol;
		int wId = pConfigItem.isCategory() ? pConfigItem.getCategory().getId() : pConfigItem.getItem().getId();

		// update wTableName set DEL_FLG = b'1' where wIdName = ?
		String wQuery = new StringBuilder("update ").append(wTableName).append(" set ")
				.append(mDelFlgCol).append(" = b'1' where ").append(wIdName).append(" = ?")
				.toString();
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setInt(1, wId);
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		
		if (!pConfigItem.isCategory()) {
			return;
		}
		// update cbm_item set DEL_FLG = b'1' where CATEGORY_ID = ?
		wQuery = new StringBuilder("update ").append(mItemTable).append(" set ")
				.append(mDelFlgCol).append(" = b'1' where ").append(mCategoryIdCol).append(" = ?")
				.toString();
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setInt(1, pConfigItem.getCategory().getId());
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	public static Collection<Book> getRelatedBooks(ConfigItem pConfigItem) {
		Collection<Book> wList = new ArrayList<>();
		// select BOOK_ID from cbr_book where ITEM_ID = ?
		String wQuery = new StringBuilder("select ").append(mBookIdCol).append(" from ")
				.append(mBookItemTable).append(" where ").append(mItemIdCol).append(" = ?")
				.toString();
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setInt(1, pConfigItem.getItem().getId());
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				while (wResultSet.next()) {
					int wBookId = wResultSet.getInt(mBookIdCol); 
					wList.add(Book.getBook(wBookId));
				}
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		return wList;
	}

	public static Collection<Item> getRelatedItems(Book pBook) {
		Collection<Item> wList = new ArrayList<>();
		// select cbm_item.ITEM_ID from cbr_book inner join cbm_item on cbr_book.ITEM_ID = cbm_item.ITEM_ID
		// where cbr_book.BOOK_ID = ? and cbm_item.DEL_FLG = b'0'
		String wQuery = new StringBuilder("select ").append(mItemTable).append(".").append(mItemIdCol)
				.append(" from ").append(mBookItemTable).append(" inner join ").append(mItemTable).append(" on ")
					.append(mBookItemTable).append(".").append(mItemIdCol).append(" = ")
					.append(mItemTable).append(".").append(mItemIdCol)
				.append(" where ").append(mBookItemTable).append(".").append(mBookIdCol).append(" = ?")
					.append(" and ").append(mItemTable).append(".").append(mDelFlgCol).append(" = b'0'")
				.toString();
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setInt(1, pBook.getId());
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				while (wResultSet.next()) {
					wList.add(getItem(wResultSet, null));
				}
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		return wList;
	}

	public static void updateItemRelation(Item pItem, Book pBook, boolean isSelected) {
		String wQuery;

		if (!isSelected) {
			// 削除
			// delete from cbr_book where ITEM_ID = ? and BOOK_ID = ?
			wQuery = new StringBuilder("delete from ").append(mBookItemTable).append(" where ")
					.append(mItemIdCol).append(" = ? and ").append(mBookIdCol).append(" = ?").toString();
		} else {
			// 追加
			// insert into cbr_book (ITEM_ID, BOOK_ID) values (?, ?)
			wQuery = new StringBuilder("insert into ").append(mBookItemTable).append(" (")
					.append(mItemIdCol).append(", ").append(mBookIdCol).append(") values (?, ?)")
					.toString();
		}
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setInt(1, pItem.getId());
			wStatement.setInt(2, pBook.getId());
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	public static Collection<Category> getSpecialCategories() {
		Collection<Category> wList = new ArrayList<>();
		// select CATEGORY_ID from cbm_category where SPECIAL_FLG = b'1' and DEL_FLG = b'0'
		String wQuery = new StringBuilder("select ").append(mCategoryIdCol)
				.append(", ").append(mCategoryNameCol).append(", ").append(mCategoryRexpCol)
				.append(" from ").append(mCategoryTable)
				.append(" where ").append(mCategorySpecialFlgCol).append(" = b'1'")
				.append(" and ").append(mDelFlgCol).append(" = b'0'").toString();
		
//		ResultSet wResultSet = mDbAccess.executeQuery(wQuery);

		try (ResultSet wResultSet = mDbAccess.executeQuery(mDbAccess.getPreparedStatement(wQuery))) {
			while (wResultSet.next()) {
				wList.add(getCategory(wResultSet));
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wList;

	}

	public static Collection<Category> getTempCategories() {
		Collection<Category> wList = new ArrayList<>();

		String wQuery = new StringBuilder("select ").append(mCategoryIdCol).append(",")
				.append(mCategoryNameCol).append(",").append(mCategoryRexpCol)
				.append(" from ").append(mCategoryTable)
				.append(" where ").append(mCategoryTempFlgCol).append(" = b'1'")
				.append(" and ").append(mDelFlgCol).append(" = b'0'").toString();

		try (ResultSet wResultSet = mDbAccess.executeQuery(mDbAccess.getPreparedStatement(wQuery))) {
			while (wResultSet.next()) {
				wList.add(getCategory(wResultSet));
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wList;

	}

	private static Item getItem(ResultSet pResultSet, Category pCategory) throws SQLException {
		int wItemId = pResultSet.getInt(mItemIdCol);
		if (Item.getItem(wItemId) == null) {
			assert pCategory != null;
			Item.generateItem(wItemId, pResultSet.getString(mItemNameCol), pCategory);
		}
		return Item.getItem(wItemId);
	}
	
//	private static Item getItem(ResultSet pResultSet) throws SQLException {
//		return getItem(pResultSet, getCategory(pResultSet));
//	}
	
	private static Category getCategory(ResultSet pResultSet) throws SQLException {
		int wCategoryId = pResultSet.getInt(mCategoryIdCol);
		if (Category.getCategory(wCategoryId) == null) {
			String wCategoryName = pResultSet.getString(mCategoryNameCol);
			IncomeExpenseType wType =
					pResultSet.getInt(mCategoryRexpCol) == IncomeExpenseType.INCOME.getCategoryRexp() ?
							IncomeExpenseType.INCOME : IncomeExpenseType.EXPENCE;
			Category.generateCategory(wCategoryId, wCategoryName, wType);
		}
		return Category.getCategory(wCategoryId);
	}
	
	private static Category getCategory(ResultSet pResultSet, IncomeExpenseType pType) throws SQLException {
		int wCategoryId = pResultSet.getInt(mCategoryIdCol);
		if (Category.getCategory(wCategoryId) == null) {
			String wCategoryName = pResultSet.getString(mCategoryNameCol);
			Category.generateCategory(wCategoryId, wCategoryName, pType);
		}
		return Category.getCategory(wCategoryId);
	}

	public static void updateSpecialCategory(Category pCategory, boolean isSelected) {
		// update cbm_category set SPECIAL_FLG = b? where CATEGORY_ID = ?
		String wQuery = new StringBuilder("update ").append(mCategoryTable).append(" set ")
				.append(mCategorySpecialFlgCol).append(" = b?")
				.append(" where ").append(mCategoryIdCol).append(" = ?").toString();
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setString(1, isSelected ? "1" : "0");
			wStatement.setInt(2, pCategory.getId());
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	public static void updateTempCategory(Category pCategory, boolean isSelected) {
		// update cbm_category set TEMP_FLG = b? where CATEGORY_ID = ?
		String wQuery = new StringBuilder("update ").append(mCategoryTable).append(" set ")
				.append(mCategoryTempFlgCol).append(" = b?")
				.append(" where ").append(mCategoryIdCol).append(" = ?").toString();
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setString(1, isSelected ? "1" : "0");
			wStatement.setInt(2, pCategory.getId());
//			System.out.println(wStatement);
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

//	@Deprecated
//	public static String getBookNameById(int pBookId) {
//		String wResult = "";
//		// select BOOK_NAME from cbm_book where BOOK_ID = ?
//		String wQuery = new StringBuilder("select ").append(mBookNameCol)
//				.append(" from ").append(mBookTable).append(" where ")
//				.append(mBookIdCol).append(" = ?").toString();
//		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)){
//			wStatement.setInt(1, pBookId);
//			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
//				if (wResultSet.next()) {
//					wResult = wResultSet.getString(mBookNameCol);
//				}
//			} catch (SQLException e) {
//				resultSetHandlingError(e);
//			}
//		} catch (SQLException e) {
//			resultSetHandlingError(e);
//		}
//		return wResult;
//	}

//	public static List<Book> getBookList() {
//		List<Book> wBookList = new ArrayList<>();
//		// select BOOK_ID, BOOK_NAME, BALANCE from cbm_book where DEL_FLG = b'0'
//		// order by SORT_KEY
//		String wQuery = new StringBuilder("select ").append(mBookIdCol).append(", ")
//				.append(mBookNameCol).append(", ").append(mBookBalanceCol).append(" from ")
//				.append(mBookTable).append(" where ").append(mDelFlgCol).append(" = b'0' ")
//				.append(" order by ").append(mSortKeyCol).toString();
//
//		try (ResultSet wResultSet = mDbAccess.executeQuery(mDbAccess.getPreparedStatement(wQuery))) {
//			while (wResultSet.next()) {
//				int wBookId = wResultSet.getInt(mBookIdCol);
//				String wBookName = wResultSet.getString(mBookNameCol);
//				if (Book.getBook(wBookId) == null) {
//					Book.generateBook(wBookId, wBookName);
//				}
//				Book wBook = Book.getBook(wBookId);
////				Book wBook = new Book(wResultSet.getInt(mBookIdCol), wResultSet
////						.getString(mBookNameCol));
//				if (wResultSet.getInt(mBookBalanceCol) > 0) {
//					wBook.setBalance(wResultSet.getInt(mBookBalanceCol));
//				}
//				wBookList.add(wBook);
//			}
//		} catch (SQLException e) {
//			resultSetHandlingError(e);
//		}
//		return wBookList;
//	}

	public static void addNewBook(String pBookName) {
		// insert into cbm_book (BOOK_NAME) values (?)
		String wQuery = new StringBuilder("insert into ").append(mBookTable).append(" (")
				.append(mBookNameCol).append(") values (?)").toString();
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setString(1, pBookName);
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	public static void updateBookName(Book pBook, String pBookName) {
		Book.clear();
		// update cbm_book set BOOK_NAME = ? where BOOK_ID = ?
		String wQuery = new StringBuilder("update ").append(mBookTable).append(" set ")
				.append(mBookNameCol).append(" = ? ")
				.append(" where ").append(mBookIdCol).append(" = ?").toString();
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setString(1, pBookName);
			wStatement.setInt(2, pBook.getId());
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	public static void removeBook(Book pBook) {
		// update cbm_book set DEL_FLG = b'1' where BOOK_ID = ?
		String wQuery = new StringBuilder("update ").append(mBookTable).append(" set ")
				.append(mDelFlgCol).append(" = b'1' ").append(" where ").append(mBookIdCol)
				.append(" = ?").toString();
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setInt(1, pBook.getId());
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	public static void updateBookSortKeys(List<Book> pBookList) {
		int wSortKey = 1;
		// update cbm_book set SORT_KEY = ? where BOOK_ID = ?
		String wQuery = new StringBuilder("update ").append(mBookTable).append(" set ")
					.append(mSortKeyCol).append(" = ?").append(" where ")
					.append(mBookIdCol).append(" = ?").toString();
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			for (Book wBook : pBookList) {
				wStatement.setInt(1, wSortKey++);
				wStatement.setInt(2, wBook.getId());
				mDbAccess.executeUpdate(wStatement);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	public static void updateBalance(Book pBook) {
		// update cbm_book set BALANCE = ? where BOOK_ID = ?
		String wQuery = new StringBuilder("update ").append(mBookTable).append(" set ")
				.append(mBookBalanceCol).append(" = ?")
				.append(" where ").append(mBookIdCol).append(" = ?").toString();
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQuery)) {
			wStatement.setInt(1, pBook.getBalance());
			wStatement.setInt(2, pBook.getId());
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
	}

	public static RecordTableItemCollection getSearchedRecordTableItemList(String pQueryString) {
		Collection<RecordTableItem> wRecordsPast = Collections.emptyList();
		Collection<RecordTableItem> wRecordsFuture = Collections.emptyList();
//		RecordTableItem[][] result = new RecordTableItem[2][];
		
		Date wDate = new Date();
		pQueryString = pQueryString.replaceAll("%","\\\\%").replaceAll("_","\\\\_").replaceAll("\\\\","\\\\\\\\");
		String wFrom = " from " + mItemTable + " inner join " + mActTable 
				+ " on " + mActTable + "." + mItemIdCol + " = " + mItemTable + "." + mItemIdCol
				+ " inner join " + mCategoryTable
				+ " on " + mCategoryTable + "." + mCategoryIdCol + " = " + mItemTable + "." + mCategoryIdCol;
		String wQueryBase = new StringBuilder("select ").append(mActIdCol).append(", ")
				.append(mBookIdCol).append(", ").append(mActDtCol).append(", ")
				.append(mActTable).append(".").append(mItemIdCol).append(", ")
				.append(mItemTable).append(".").append(mItemNameCol).append(", ")
				.append(mCategoryTable).append(".").append(mCategoryIdCol).append(", ")
				.append(mCategoryTable).append(".").append(mCategoryNameCol).append(", ")
				.append(mCategoryTable).append(".").append(mCategoryRexpCol).append(", ")
				.append(mGroupIdCol).append(", ").append(mActIncomeCol).append(", ")
				.append(mActExpenseCol).append(", ").append(mActFreqCol).append(", ")
				.append(mNoteNameCol).append(wFrom)
				.append(" where ").append(mActTable).append(".").append(mDelFlgCol).append(" = b'0'")
				.append(" and ").append(mNoteNameCol).append(" like ?")
				.toString();
		String wQueryPeriodBefore = new StringBuilder(" and ").append(mActDtCol).append(" <= ?")
				.toString();
		String wQueryPeriodAfter = new StringBuilder(" and ").append(mActDtCol).append(" > ?")
				.toString();
		String wQueryOrder = new StringBuilder(" order by ").append(mActDtCol).append(", ")
				.append(mCategoryTable).append(".").append(mCategoryRexpCol).append(", ")
				.append(mCategoryTable).append(".").append(mSortKeyCol).append(", ")
				.append(mItemTable).append(".").append(mSortKeyCol).toString();
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBase + wQueryPeriodBefore + wQueryOrder)) {
			wStatement.setString(1, "%" + pQueryString + "%");
			wStatement.setString(2, mDateFormat.format(wDate));
//			System.out.println(wStatement);
//			result[0] = getRecordTableItemFromResultSet(mDbAccess.executeQuery(wStatement));
			wRecordsPast = getRecordTableItemFromResultSet(mDbAccess.executeQuery(wStatement));

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBase + wQueryPeriodAfter + wQueryOrder)) {
			wStatement.setString(1, "%" + pQueryString + "%");
			wStatement.setString(2, mDateFormat.format(wDate));
//			result[1] = getRecordTableItemFromResultSet(mDbAccess.executeQuery(wStatement));
			wRecordsFuture = getRecordTableItemFromResultSet(mDbAccess.executeQuery(wStatement));
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return new RecordTableItemCollection(wRecordsPast, wRecordsFuture);
	}

	private static Collection<RecordTableItem> getRecordTableItemFromResultSet(ResultSet pResultSet) {
		Collection<RecordTableItem> wList = new ArrayList<RecordTableItem>();
		try {
			while (pResultSet.next()) {
				wList.add(generateRecordTableItem(pResultSet));
//				wList.add(new RecordTableItem.Builder(pResultSet.getInt(mBookIdCol), pResultSet
//						.getInt(mItemIdCol), pResultSet.getDate(mActDtCol)).actId(
//						pResultSet.getInt(mActIdCol)).expense(pResultSet.getInt(mActExpenseCol))
//						.frequency(pResultSet.getInt(mActFreqCol)).groupId(
//								pResultSet.getInt(mGroupIdCol)).income(
//								pResultSet.getInt(mActIncomeCol)).note(
//								pResultSet.getString(mNoteNameCol)).build());
			}
			pResultSet.close();
		} catch (SQLException e) {
			resultSetHandlingError(e);
		} finally {
		}
//		return wList.toArray(new RecordTableItem[0]);
		return wList;
	}

	private static int getBalance(Date pEndDate, Book pBook, boolean pIncludeEndDate) {

		int wBalance = getInitialBalance(pBook);
		String wResultCol = "Value";

		// select SUM(INCOME - EXPENSE) as Value from cbt_act
		// where DEL_FLG = b?		// 1 (String "0")
		//	and ACT_DT (<= or <) ?	// 2 (String)
		//	(and BOOK_ID = ?)		// 3 (int)
		StringBuilder wQueryBuilder = new StringBuilder("select SUM(").append(mActIncomeCol)
				.append(" - ").append(mActExpenseCol).append(") as ").append(wResultCol)
				.append(" from ").append(mActTable)
				.append(" where ").append(mDelFlgCol).append(" = b?")
				.append(" and ").append(mActDtCol).append(pIncludeEndDate ? " <= ?" : " < ?")
				;
		if (!pBook.isAllBook()) {
				wQueryBuilder.append(" and ").append(mBookIdCol).append(" = ?"); //getBookWherePrepared(pBookId));
		}
		try(PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			int i=1;
			wStatement.setString(i++, "0");
			wStatement.setString(i++, mDateFormat.format(pEndDate));
			if (!pBook.isAllBook()) {
				wStatement.setInt(i++, pBook.getId());
			}
			ResultSet wResultSet = mDbAccess.executeQuery(wStatement);
			wResultSet.next();
			wBalance += wResultSet.getInt(wResultCol);
			wResultSet.close();

		} catch (SQLException e) {
			resultSetHandlingError(e);
		}

		return wBalance;
	}

	public static int getInitialBalance(Book pBook) {
		int wBalance = 0;
		StringBuilder wQueryBuilder = new StringBuilder("select SUM(").append(mBookBalanceCol).append(") ")
					.append(mBookBalanceCol).append(" from ").append(mBookTable).append(" where ");
		if (pBook.isAllBook()) {
			wQueryBuilder.append(mDelFlgCol).append(" = b'0'");
		} else {
			wQueryBuilder.append(mBookIdCol).append(" = ?");
		}

		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			if (!pBook.isAllBook()) {
				wStatement.setInt(1, pBook.getId());
			}
			try (ResultSet wResultSet = mDbAccess.executeQuery(wStatement)) {
				wResultSet.next();
				wBalance = wResultSet.getInt(mBookBalanceCol);
			} catch (SQLException e) {
				resultSetHandlingError(e);
			}
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		return wBalance;
	}
	
	private static int getNewGroupId() {
		int wRet = 0;
		String wCol = "MaximumGroupId";

		// select max(GROUP_ID) MaximumGroupId from cbt_act
		String wQuery = new StringBuilder("select max(").append(mGroupIdCol).append(") ").append(wCol)
						.append(" from ").append(mActTable).toString().toString();
		try (ResultSet wResultSet = mDbAccess.executeQuery(mDbAccess.getPreparedStatement(wQuery))) {
			wResultSet.next();
			wRet = wResultSet.getInt(wCol);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		return wRet + 1;

	}
	
	private static void updateNoteTable(Item pItem, String pNote) {
		updateNoteTable(pItem.getId(), pNote);
	}

	private static void updateNoteTable(int pItemId, String pNote) {
		// delete from cbt_note
		// where ITEM_ID = ? and NOTE_NAME = ?
		StringBuilder wQueryBuilder = new StringBuilder("delete from ").append(mNoteTable)
				.append(" where ").append(mItemIdCol).append(" = ? and ")
				.append(mNoteNameCol).append(" = ?");
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			wStatement.setInt(1, pItemId);
			wStatement.setString(2, pNote);
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
		}
		
		// insert into cbt_note (NOTE_NAME,ITEM_ID) values(?,?)
		wQueryBuilder = new StringBuilder("insert into  ").append(mNoteTable)
				.append(" (").append(mNoteNameCol).append(",").append(mItemIdCol).append(") values(?,?)");
		try (PreparedStatement wStatement = mDbAccess.getPreparedStatement(wQueryBuilder.toString())) {
			wStatement.setString(1, pNote);
			wStatement.setInt(2, pItemId);
			mDbAccess.executeUpdate(wStatement);
		} catch (SQLException e) {
			resultSetHandlingError(e);
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
				"SQL ResultSet Handling Error", e.toString() + "\n\n" + wStack);
	}
}