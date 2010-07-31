package beauties.model;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import beauties.model.db.DbUtil;

public class SystemData {

	private static final int mUndefined = -1;
	private static final int mAllBook = 0;

	private static String mDbHost = "localhost";
	private static int mDbPort = 3306;
	private static String mDbUser = "root";
	private static String mDbPass = "";
	private static String mDbName = "beauties";

	private static Rectangle mWindowRectangle = new Rectangle(0, 0, 1000, 700);
	private static Point mWindowPoint = new Point(1000, 1000);
	private static boolean mWindowMaximized = false;

	private static int[] mRecordTableWeights = { 80, 20 };

	private static DecimalFormat mDecimalFormat = new DecimalFormat("###,###");

	private static final Color wColorRed = new Color(Display.getCurrent(), 255, 200, 200);
	private static final Color wColorGreen = new Color(Display.getCurrent(), 200, 255, 200);
	private static final Color wColorBlue = new Color(Display.getCurrent(), 200, 200, 255);
	private static final Color wColorYellow = new Color(Display.getCurrent(), 255, 255, 176);
	private static final Color wColorGray = new Color(Display.getCurrent(), 238, 227, 251);

	private static String mPathMemoDir = "memo";

	private static boolean isDbUpdated = false;

	// for cache
	private static boolean showGridLine;
	private static boolean mGridLineIsCached = false;

	private static int mCutOff = mUndefined;

	private static Map<Integer, String> mItemNameMap = new HashMap<Integer, String>();
	private static Map<Integer, Integer> mCategoryIdMap = new HashMap<Integer, Integer>();
	private static Map<Integer, String> mCategoryNameMap = new HashMap<Integer, String>();

	private SystemData() {
	}

	public static int getAllBookInt() {
		return mAllBook;
	}

	public static int getUndefinedInt() {
		return mUndefined;
	}

	// BookMap (getter only)
	public static Map<Integer, String> getBookMap(boolean pWithAll) {
		Map<Integer, String> wBookNameMap = DbUtil.getBookNameMap();
		if (pWithAll) {
			Map<Integer, String> wMap = new LinkedHashMap<Integer, String>();
			wMap.put(mAllBook, "全て");
			wMap.putAll(wBookNameMap);
			return wMap;
		} else {
			return wBookNameMap;
		}
	}

	public static int[] getRecordTableWeights() {
		return mRecordTableWeights;
	}

	public static void setRecordTableWeights(int[] pRecordTableWeights) {
		SystemData.mRecordTableWeights = pRecordTableWeights;
	}

	public static String getDbHost() {
		return mDbHost;
	}

	public static int getDbPort() {
		return mDbPort;
	}

	public static String getDbUser() {
		return mDbUser;
	}

	public static String getDbPass() {
		return mDbPass;
	}

	public static String getDbName() {
		return mDbName;
	}

	public static boolean isWindowMaximized() {
		return mWindowMaximized;
	}

	public static void setDbHost(String pDbHost) {
		SystemData.mDbHost = pDbHost;
	}

	public static void setDbPort(int pDbPort) {
		SystemData.mDbPort = pDbPort;
	}

	public static void setDbUser(String pDbUser) {
		SystemData.mDbUser = pDbUser;
	}

	public static void setDbPass(String pDbPass) {
		SystemData.mDbPass = pDbPass;
	}

	public static void setDbName(String pDbName) {
		SystemData.mDbName = pDbName;
	}

	public static void setWindowMaximized(boolean pWindowMaximized) {
		SystemData.mWindowMaximized = pWindowMaximized;
	}

	public static Rectangle getWindowRectangle() {
		return mWindowRectangle;
	}

	public static void setWindowRectangle(Rectangle pWindowRectangle) {
		SystemData.mWindowRectangle = pWindowRectangle;
	}

	public static Point getWindowPoint() {
		return mWindowPoint;
	}

	public static void setWindowPoint(Point pWindowPoint) {
		SystemData.mWindowPoint = pWindowPoint;
	}

	public static String getFormatedFigures(double pValue) {
		return mDecimalFormat.format(pValue);
	}

	public static Color getColorRed() {
		return wColorRed;
	}

	public static Color getColorGreen() {
		return wColorGreen;
	}

	public static Color getColorBlue() {
		return wColorBlue;
	}

	public static Color getColorYellow() {
		return wColorYellow;
	}

	public static Color getColorGray() {
		return wColorGray;
	}

	public static String getPathMemoDir() {
		return mPathMemoDir;
	}

	public static void setPathMemoDir(String pPathMemoDir) {
		SystemData.mPathMemoDir = pPathMemoDir;
	}

	public static void setDbUpdated(boolean pDbUpdated) {
		isDbUpdated = pDbUpdated;
	}

	public static boolean getDbUpdated() {
		return isDbUpdated;
	}

	public static void dumpDb() {
		if (!isDbUpdated)
			return;
		Runtime wRuntime = Runtime.getRuntime();
		String wCommand = "mysqldump -u " + mDbUser + " "
				+ (("".equals(mDbPass)) ? "" : "-p" + mDbPass) + " " + mDbName + " > " + mDbName
				+ ".dump";
		String[] wCommands;
		if (System.getProperty("os.name").contains("Windows")) {
			wCommands = new String[] { "cmd", "/c", wCommand };
		} else {
			wCommands = new String[] { "sh", "-c", wCommand };
		}
		try {
			wRuntime.exec(wCommands);
		} catch (IOException e) {
			System.err.println("DB dump error: " + e.toString());
		}
	}

	public static boolean showGridLine() {
		if (!mGridLineIsCached) {
			showGridLine = DbUtil.showGridLine();
			mGridLineIsCached = true;
		}
		return showGridLine;
	}

	public static int getCutOff() {
		if (mCutOff == mUndefined)
			mCutOff = DbUtil.getCutOff();
		return mCutOff;
	}

	public static String getItemName(int pItemId) {
		if (mItemNameMap.get(pItemId) == null)
			mItemNameMap.put(pItemId, DbUtil.getItemNameById(pItemId));
		return mItemNameMap.get(pItemId);
	}

	public static int getCategoryByItemId(int pItemId) {
		if (mCategoryIdMap.get(pItemId) == null)
			mCategoryIdMap.put(pItemId, DbUtil.getCategoryIdByItemId(pItemId));
		return mCategoryIdMap.get(pItemId);
	}

	public static String getCategoryName(int pCategoryId) {
		if (mCategoryNameMap.get(pCategoryId) == null)
			mCategoryNameMap.put(pCategoryId, DbUtil.getCategoryNameById(pCategoryId));
		return mCategoryNameMap.get(pCategoryId);
	}

	public static void crearCache() {
		mGridLineIsCached = false;
		mCutOff = mUndefined;
		mItemNameMap.clear();
		mCategoryIdMap.clear();
		mCategoryNameMap.clear();
	}

}
