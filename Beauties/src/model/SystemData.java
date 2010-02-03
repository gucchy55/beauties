package model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import model.db.DbUtil;

public class SystemData {

	private static RightType mRightType;

	private static final int mUndefined = -1;
	private static final int mAllBook = 0;

	// For common
	private static int mCutOff;
	private static Map<Integer, String> mBookMap = new LinkedHashMap<Integer, String>();
	private static int mBookId = mUndefined;
	private static Date mStartDate = null;
	private static Date mEndDate = null;

	// For entry
	private static boolean isMonthPeriod = true;
	private static int mItemId = mUndefined;
	private static int mCategoryId = mUndefined;
	private static boolean mAllIncome = false;
	private static boolean mAllExpense = false;
	private static int[] mRecordTableWeights = {80, 20};
	
	// For Annual
//	private static boolean isAnnualPeriod = false;
//	private static AnnualViewType mAnnualViewType;
//	private static int mMonthCount;

//	private static CompositeRightMain mCompositeRightMain;

	private SystemData() {
	}

	public static void init() {
		// RightType変更時に初期化
		mStartDate = null;
		mEndDate = null;
		isMonthPeriod = true;
		mItemId = mUndefined;
		mCategoryId = mUndefined;
		mAllIncome = false;
		mAllExpense = false;
//		isAnnualPeriod = false;
//		mAnnualViewType = AnnualViewType.Category;
//		mMonthCount = 13;	// 直近13ヶ月

		// System設定変更後のみ更新で充分
		mCutOff = DbUtil.getCutOff();
		mBookMap = DbUtil.getBookNameMap();
		switch (mRightType) {
		case Main:
			mBookId = mBookMap.keySet().iterator().next();
			break;
		case Anual:
			mBookId = mAllBook;
			break;
		default:
			break;
		}
	}

	public static boolean isBookIdAll() {
		return (mBookId == mAllBook);
	}

	public static int getAllBookInt() {
		return mAllBook;
	}

	public static int getUndefinedInt() {
		return mUndefined;
	}

	// RightType
	public static void setRightType(RightType pRightType) {
		mRightType = pRightType;
	}

	public static RightType getRightType() {
		return mRightType;
	}

	// BookId
	public static void setBookId(int pBookId) {
		mBookId = pBookId;
	}

	public static int getBookId() {
		return mBookId;
	}

	// Start/End Dates
	public static void setStartDate(Date pStartDate) {
		mStartDate = pStartDate;
	}

	public static Date getStartDate() {
		return mStartDate;
	}

	public static void setEndDate(Date pEndDate) {
		mEndDate = pEndDate;
	}

	public static Date getEndDate() {
		return mEndDate;
	}

	// isMonthPeriod
	public static void setMonthPeriod(boolean p) {
		isMonthPeriod = p;
	}

	public static boolean isMonthPeriod() {
		return isMonthPeriod;
	}

	// CompositeRightMain
//	public static void setCompositeRightMain(
//			CompositeRightMain pCompositeRightMain) {
//		mCompositeRightMain = pCompositeRightMain;
//	}

//	public static CompositeRightMain getCompositeRightMain() {
//		return mCompositeRightMain;
//	}

	// ItemId
	public static void setItemId(int pItemId) {
		mItemId = pItemId;
	}

	public static int getItemId() {
		return mItemId;
	}

	// isAllIncome, isAllExpense
	public static void setAllIncome(boolean pAllIncome) {
		mAllIncome = pAllIncome;
	}

	public static boolean isAllIncome() {
		return mAllIncome;
	}

	public static void setAllExpense(boolean pAllExpense) {
		mAllExpense = pAllExpense;
	}

	public static boolean isAllExpense() {
		return mAllExpense;
	}

	// CategoryId
	public static void setCategoryId(int pCategoryId) {
		SystemData.mCategoryId = pCategoryId;
	}

	public static int getCategoryId() {
		return mCategoryId;
	}

	// CutOff (getter only)
	public static int getCutOff() {
		return mCutOff;
	}

	// BookMap (getter only)
	public static Map<Integer, String> getBookMap(boolean pWithAll) {
		if (pWithAll) {
			Map<Integer, String> wMap = new LinkedHashMap<Integer, String>();
			wMap.put(mAllBook, "全て");
			wMap.putAll(mBookMap);
			return wMap;
		} else {
			return mBookMap;
		}
	}

//	public static boolean isAnnualPeriod() {
//		return isAnnualPeriod;
//	}
//
//	public static void setAnnualPeriod(boolean isAnnualPeriod) {
//		SystemData.isAnnualPeriod = isAnnualPeriod;
//	}
//
//	public static AnnualViewType getAnnualViewType() {
//		return mAnnualViewType;
//	}
//
//	public static void setmAnnualViewType(AnnualViewType pAnnualViewType) {
//		SystemData.mAnnualViewType = pAnnualViewType;
//	}
//
//	public static int getMonthCount() {
//		return mMonthCount;
//	}
//
//	public static void setMonthCount(int pMonthCount) {
//		SystemData.mMonthCount = pMonthCount;
//	}

	public static int[] getRecordTableWeights() {
		return mRecordTableWeights;
	}

	public static void setRecordTableWeights(int[] pRecordTableWeights) {
		SystemData.mRecordTableWeights = pRecordTableWeights;
	}

}
