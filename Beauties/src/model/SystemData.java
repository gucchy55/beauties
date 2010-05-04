package model;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import model.db.DbUtil;

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
	
	private static int[] mRecordTableWeights = {80, 20};
	
	private static DecimalFormat mDecimalFormat = new DecimalFormat("###,###");
	
	private static final Color wColorRed = new Color(Display.getCurrent(), 255, 200, 200);
	private static final Color wColorGreen = new Color(Display.getCurrent(), 200, 255, 200);
	private static final Color wColorBlue = new Color(Display.getCurrent(), 200, 200, 255);
	private static final Color wColorYellow = new Color(Display.getCurrent(), 255, 255, 176);
	private static final Color wColorGray =	new Color(Display.getCurrent(), 238, 227, 251);
	
	private static String mPathMemoDir = "memo";
	
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
	
}
