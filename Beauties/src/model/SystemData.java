package model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import model.db.DbUtil;

public class SystemData {

	private static final int mUndefined = -1;
	private static final int mAllBook = 0;

	// For common
	private static int mCutOff;
	private static Map<Integer, String> mBookMap = new LinkedHashMap<Integer, String>();
	
	private static String mDbHost = "localhost";
	private static int mDbPort = 3306;
	private static String mDbUser = "root";
	private static String mDbPass = "";
	private static String mDbName = "beauties";
	
	private static Rectangle mWindowRectangle = new Rectangle(0, 0, 1000, 700);
	private static Point mWindowPoint = new Point(1000, 1000);
	private static boolean mWindowMaximized = false;
	
	private static int[] mRecordTableWeights = {80, 20};
	
	private SystemData() {
	}

	public static void init() {
		// System設定変更後のみ更新で充分
		mCutOff = DbUtil.getCutOff();
		mBookMap = DbUtil.getBookNameMap();
	}

	public static int getAllBookInt() {
		return mAllBook;
	}

	public static int getUndefinedInt() {
		return mUndefined;
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
	
}
