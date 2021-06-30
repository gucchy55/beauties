package beauties.common.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import beauties.common.model.Book;
import beauties.common.model.Category;
import beauties.common.model.Item;
import beauties.common.model.RightType;


public class SystemData {

	private static final int mUndefined = -1;
	private static final int mAllBook = 0;

	private static String mImageFileName;
	private static RightType mRightType = RightType.Main;

	private static String mWindowTitle = "家計簿";
	private static String mDbHost;
	private static int mDbPort;
	private static String mDbUser;
	private static String mDbPass;
	private static String mDbName;
	private static Map<String, String> mDbNameMap = new LinkedHashMap<>();

	private static Rectangle mWindowRectangle = new Rectangle(0, 0, 1000, 700);
	private static Point mWindowPoint = new Point(1000, 1000);
	private static boolean mWindowMaximized = false;

	private static int[] mRecordTableWeights = { 80, 20 };
	private static int mRecordWidthBook = 60;
	private static int mRecordWidthDateYear = 80;
	private static int mRecordWidthDate = 62;
	private static int mRecordWidthItem = 70;
	private static int mRecordWidthIncome = 60;
	private static int mRecordWidthExpense = 60;
	private static int mRecordWidthBalance = 80;
	private static int mRecordWidthFreq = 50;
	private static int mRecordWidthNote = 250;
	private static int mRecordWidthSummaryItem = 100;
	private static int mRecordWidthSummaryValue = 100;
	
	private static int mAnnualWidth = 75;
	

	private static DecimalFormat mDecimalFormat = new DecimalFormat("###,###");
	
	private static boolean isDarkMode = isDarkMode();

	private static final Color wColorRed = isDarkMode ? 
			new Color(Display.getCurrent(), 100, 42, 46) : 
			new Color(Display.getCurrent(), 255, 200, 200);
	private static final Color wColorGreen = isDarkMode ? 
			new Color(Display.getCurrent(), 41, 64, 39) :
			new Color(Display.getCurrent(), 200, 255, 200);
	private static final Color wColorBlue = isDarkMode ? 
			new Color(Display.getCurrent(), 42, 61, 100) :
			new Color(Display.getCurrent(), 200, 200, 255);
	private static final Color wColorYellow = isDarkMode ? 
			new Color(Display.getCurrent(), 107, 73, 61) :
			new Color(Display.getCurrent(), 255, 255, 176);
	private static final Color wColorRedFore = new Color(Display.getCurrent(), 255, 0, 204);

	private static String mWorkDir = "";
	private static String mPathMemoDir = "memo";
	private static String mMemoFontName = "";
	private static int mMemoFontSize = 14;
	
	private static boolean mAutoDump = false;

	private static boolean isDbUpdated = false;
	
	private static int mNoteLimit = 50;
	
	private static int mHorizontalSpacing = 0;

	// for cache
	private static boolean showGridLine;
	private static boolean mGridLineIsCached = false;

	private static int mCutOff = mUndefined;

	private SystemData() {
	}

	public static int getAllBookInt() {
		return mAllBook;
	}

	public static int getUndefinedInt() {
		return mUndefined;
	}
	
	// BookMap (getter only)
	public static Collection<Book> getBooks(boolean pWithAll) {
		return Book.getBooks(pWithAll);
	}

	public static int[] getRecordTableWeights() {
		return mRecordTableWeights;
	}

	public static void setRecordTableWeights(int[] pRecordTableWeights) {
		SystemData.mRecordTableWeights = pRecordTableWeights;
	}

	public static int getRecordWidthBook() {
		return mRecordWidthBook;
	}

	public static void setRecordWidthBook(int pRecordWidthBook) {
		SystemData.mRecordWidthBook = pRecordWidthBook;
	}

	public static int getRecordWidthDateYear() {
		return mRecordWidthDateYear;
	}

	public static void setRecordWidthDateYear(int pRecordWidthDateYear) {
		SystemData.mRecordWidthDateYear = pRecordWidthDateYear;
	}

	public static int getRecordWidthDate() {
		return mRecordWidthDate;
	}

	public static void setRecordWidthDate(int pRecordWidthDate) {
		SystemData.mRecordWidthDate = pRecordWidthDate;
	}

	public static int getRecordWidthItem() {
		return mRecordWidthItem;
	}

	public static void setRecordWidthItem(int pRecordWidthItem) {
		SystemData.mRecordWidthItem = pRecordWidthItem;
	}

	public static int getRecordWidthIncome() {
		return mRecordWidthIncome;
	}

	public static void setRecordWidthIncome(int pRecordWidthIncome) {
		SystemData.mRecordWidthIncome = pRecordWidthIncome;
	}

	public static int getRecordWidthExpense() {
		return mRecordWidthExpense;
	}

	public static void setRecordWidthExpense(int pRecordWidthExpense) {
		SystemData.mRecordWidthExpense = pRecordWidthExpense;
	}

	public static int getRecordWidthBalance() {
		return mRecordWidthBalance;
	}

	public static void setRecordWidthBalance(int pRecordWidthBalance) {
		SystemData.mRecordWidthBalance = pRecordWidthBalance;
	}

	public static int getRecordWidthFreq() {
		return mRecordWidthFreq;
	}

	public static void setRecordWidthFreq(int pRecordWidthFreq) {
		SystemData.mRecordWidthFreq = pRecordWidthFreq;
	}

	public static int getRecordWidthNote() {
		return mRecordWidthNote;
	}

	public static void setRecordWidthNote(int pRecordWidthNote) {
		SystemData.mRecordWidthNote = pRecordWidthNote;
	}
	
	public static int getAnnualWidth() {
		return mAnnualWidth;
	}
	
	public static void setAnnualWidth(int pAnnualWidth) {
		SystemData.mAnnualWidth = pAnnualWidth;
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
	
	public static String getDbNameByDisplayName(String pDisplayName) {
		return mDbNameMap.get(pDisplayName);
	}
	
	public static Map<String, String> getDbNameMap() {
		return mDbNameMap;
	}

	public static boolean isWindowMaximized() {
		return mWindowMaximized;
	}
	
	public static boolean isAutoDump() {
		return mAutoDump;
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
	
	public static void addDbName(String pDisplayName, String pDbName) {
		mDbNameMap.put(pDisplayName, pDbName);
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

	public static String getFormatedFigures(long pValue) {
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
	
	public static Color getColorRedFore() {
		return wColorRedFore;
	}

	public static String getPathMemoDir() {
		return mPathMemoDir;
	}

	public static String getWorkDir() {
		return mWorkDir;
	}

	public static void setPathMemoDir(String pPathMemoDir) {
		SystemData.mPathMemoDir = pPathMemoDir;
	}
	
	public static void setWorkDir(String pPathMemoParentDir) {
		SystemData.mWorkDir = pPathMemoParentDir;
	}
	
	public static String getMemoFontName() {
		return mMemoFontName;
	}
	
	public static void setMemoFontName(String pName) {
		mMemoFontName = pName;
	}
	
	public static int getMemoFontSize() {
		return mMemoFontSize;
	}
	
	public static void setMemoFontSize(int pSize) {
		mMemoFontSize = pSize;
	}
	
	public static void setAutoSave(boolean pAutoDump) {
		mAutoDump = pAutoDump;
	}

	public static void setDbUpdated(boolean pDbUpdated) {
		isDbUpdated = pDbUpdated;
	}

	public static boolean getDbUpdated() {
		return isDbUpdated;
	}

	public static void closeProcess() {
		DbUtil.closeConnection();
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

	public static String getWindowTitle() {
		return mWindowTitle;
	}
	
	public static void setWindowTitle(String pTitle) {
		mWindowTitle = pTitle;
	}

	public static void crearCache() {
		mGridLineIsCached = false;
		mCutOff = mUndefined;
		Book.clear();
		Item.clear();
		Category.clear();
	}
	
	public static int getNoteLimit() {
		return mNoteLimit;
	}

	public static int getHorizontalSpacing() {
		return mHorizontalSpacing;
	}

	public static void setHorizontalSpacing(int pHorizontalSpacing) {
		SystemData.mHorizontalSpacing = pHorizontalSpacing;
	}

	public static int getRecordWidthSummaryItem() {
		return mRecordWidthSummaryItem;
	}

	public static void setRecordWidthSummaryItem(int pRecordWidthSummaryItem) {
		mRecordWidthSummaryItem = pRecordWidthSummaryItem;
	}

	public static int getRecordWidthSummaryValue() {
		return mRecordWidthSummaryValue;
	}

	public static void setRecordWidthSummaryValue(int pRecordWidthSummaryValue) {
		mRecordWidthSummaryValue = pRecordWidthSummaryValue;
	}
	
	public static void setImageFileName(String pName) {
		mImageFileName = pName;
	}
	public static String getImageFileName() {
		return mImageFileName;
	}

	public static RightType getRightType() {
		return mRightType;
	}

	public static void setRightType(RightType pRightType) {
		mRightType = pRightType;
	}
	
	private static boolean isDarkMode() {
		return "Dark".equals(execCommand("defaults read -g AppleInterfaceStyle"));
	}

	public static String execCommand(String cmds) {
		try {
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(cmds);
			
			try (InputStream in = p.getInputStream(); BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
				StringBuffer out = new StringBuffer();
				String line;
				while ((line = br.readLine()) != null) {
					out.append(line);
				}
				return out.toString();
			}
		} catch(IOException e) {
			return "";
		}
	}
}
