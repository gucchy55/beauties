package beauties.common.lib;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;


public class FileLoader {

	private static String mFileName;

	private static final String mDbHostKey = "dbHost";
	private static final String mDbPortKey = "dbPort";
	private static final String mDbUserKey = "dbUser";
	private static final String mDbPassKey = "dbPass";
	private static final String mDbNameKey = "dbName";

	private static final String mWindowXKey = "x";
	private static final String mWindowYKey = "y";
	private static final String mWindowWidthKey = "width";
	private static final String mWindowHeightKey = "height";
	private static final String mWindowMaximiedKey = "maximized";

	private static final String mEntryHeightWeightUpKey = "heightWeightUp";
	private static final String mEntryHeightWeightDownKey = "heightWeightDown";
	
	private static final String mEntryWidthBookKey ="widthBook";
	private static final String mEntryWidthDateYearKey = "widthDateYear";
	private static final String mEntryWidthDateKey = "widthDate";
	private static final String mEntryWidthItemKey = "widthItem";
	private static final String mEntryWidthIncomeKey = "widthIncome";
	private static final String mEntryWidthExpenseKey = "widthExpense";
	private static final String mEntryWidthBalanceKey = "widthBalance";
	private static final String mEntryWidthFreqKey = "widthFreq";
	private static final String mEntryWidthNoteKey = "widthNote";
	
	private static final String mAnnualWidthKey = "widthAnnual";
	
	private static final String mMemoDirKey = "memoDirName";
	
	private static final String mAutoDumpKey = "autoDump";

	public FileLoader(String pFileName) {
		mFileName = pFileName;

		try {
			java.util.Properties prop = new java.util.Properties();
			prop.load(new java.io.FileInputStream(mFileName));
			
			SystemData.setDbHost(prop.getProperty(mDbHostKey));
			SystemData.setDbPort(Integer.parseInt(prop.getProperty(mDbPortKey)));
			SystemData.setDbUser(prop.getProperty(mDbUserKey));
			SystemData.setDbPass(prop.getProperty(mDbPassKey));
			SystemData.setDbName(prop.getProperty(mDbNameKey));
			
			SystemData.setWindowRectangle(new Rectangle(
					Integer.parseInt(prop.getProperty(mWindowXKey)),
					Integer.parseInt(prop.getProperty(mWindowYKey)), 
					Integer.parseInt(prop.getProperty(mWindowWidthKey)), 
					Integer.parseInt(prop.getProperty(mWindowHeightKey))));
			SystemData.setWindowPoint(new Point(
					Integer.parseInt(prop.getProperty(mWindowWidthKey)), 
					Integer.parseInt(prop.getProperty(mWindowHeightKey))));
			
			if (Integer.parseInt(prop.getProperty(mWindowMaximiedKey)) == 1) {
				SystemData.setWindowMaximized(true);
			}
						
			SystemData.setRecordTableWeights(new int[] {
					Integer.parseInt(prop.getProperty(mEntryHeightWeightUpKey)),
					Integer.parseInt(prop.getProperty(mEntryHeightWeightDownKey))
			});
			
			SystemData.setRecordWidthBook(Integer.parseInt(prop.getProperty(mEntryWidthBookKey)));
			SystemData.setRecordWidthDateYear(Integer.parseInt(prop.getProperty(mEntryWidthDateYearKey)));
			SystemData.setRecordWidthDate(Integer.parseInt(prop.getProperty(mEntryWidthDateKey)));
			SystemData.setRecordWidthItem(Integer.parseInt(prop.getProperty(mEntryWidthItemKey)));
			SystemData.setRecordWidthIncome(Integer.parseInt(prop.getProperty(mEntryWidthIncomeKey)));
			SystemData.setRecordWidthExpense(Integer.parseInt(prop.getProperty(mEntryWidthExpenseKey)));
			SystemData.setRecordWidthBalance(Integer.parseInt(prop.getProperty(mEntryWidthBalanceKey)));
			SystemData.setRecordWidthFreq(Integer.parseInt(prop.getProperty(mEntryWidthFreqKey)));
			SystemData.setRecordWidthNote(Integer.parseInt(prop.getProperty(mEntryWidthNoteKey)));
			
			SystemData.setAnnualWidth(Integer.parseInt(prop.getProperty(mAnnualWidthKey)));
			
			if (prop.getProperty(mMemoDirKey) != null)
				SystemData.setPathMemoDir(prop.getProperty(mMemoDirKey));
			
			if (Integer.parseInt(prop.getProperty(mAutoDumpKey)) == 1)
				SystemData.setAutoSave(true);
			
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
}
