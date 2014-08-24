package beauties.common.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;


public class FileLoader {

	private static String mFileName;
	
	private static final String mWindowTitleKey = "windowTitle";

	private static final String mDbHostKey = "dbHost";
	private static final String mDbPortKey = "dbPort";
	private static final String mDbUserKey = "dbUser";
	private static final String mDbPassKey = "dbPass";
	private static final String mDbNameKey = "dbName";
	private static final String mDbNameMapKey = "dbNames";

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
	private static final String mMemoFontNameKey = "memoFontName";
	private static final String mMemoFontSizeKey = "memoFontSize";
	
	private static final String mAutoDumpKey = "autoDump";
	
	private static final String mHorizontalSpacing = "horizontalSpacing";
	private static final String mRecordWidthSummaryItem = "widthSummaryItem";
	private static final String mRecordWidthSummaryValue = "widthSummaryValue";

	public FileLoader(String pFileName) {
		mFileName = pFileName;

		try {
			Properties prop = new Properties();
			File wFile = new File(mFileName);
//			prop.load(new InputStreamReader(new FileInputStream(mFileName), "UTF-8"));
			prop.load(new InputStreamReader(new FileInputStream(wFile), "UTF-8"));
			
			String wMemoParentDirName = wFile.getAbsoluteFile().getParent();
			SystemData.setWorkDir(wMemoParentDirName);
			
			if (prop.getProperty(mWindowTitleKey) != null) {
				SystemData.setWindowTitle(prop.getProperty(mWindowTitleKey));
			}
			
			SystemData.setDbHost(prop.getProperty(mDbHostKey));
			SystemData.setDbPort(Integer.parseInt(prop.getProperty(mDbPortKey)));
			SystemData.setDbUser(prop.getProperty(mDbUserKey));
			SystemData.setDbPass(prop.getProperty(mDbPassKey));
			SystemData.setDbName(prop.getProperty(mDbNameKey));
			String wDbNamesString = prop.getProperty(mDbNameMapKey);
			for (String wDbString : wDbNamesString.split(",")) {
				String[] wDbName = wDbString.split(":");
				if (wDbName.length != 2) {
					break;
				}
				SystemData.addDbName(wDbName[1], wDbName[0]);
			}
			
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
			
			if (prop.getProperty(mMemoDirKey) != null && !"".equals(prop.getProperty(mMemoDirKey))) {
				SystemData.setPathMemoDir(prop.getProperty(mMemoDirKey));
			}
			if (prop.getProperty(mMemoFontNameKey) != null && !"".equals(prop.getProperty(mMemoFontNameKey))) {
				SystemData.setMemoFontName(prop.getProperty(mMemoFontNameKey));
			}
			if (prop.getProperty(mMemoFontSizeKey) != null && !"".equals(prop.getProperty(mMemoFontSizeKey))) {
				SystemData.setMemoFontSize(Integer.parseInt(prop.getProperty(mMemoFontSizeKey)));
			}
			
			if (prop.getProperty(mAutoDumpKey) != null && Integer.parseInt(prop.getProperty(mAutoDumpKey)) == 1) {
				SystemData.setAutoSave(true);
			}
			
			if (prop.getProperty(mHorizontalSpacing) != null) {
				SystemData.setHorizontalSpacing(Integer.parseInt(prop.getProperty(mHorizontalSpacing)));
			}
			if (prop.getProperty(mRecordWidthSummaryItem) != null) {
				SystemData.setRecordWidthSummaryItem(Integer.parseInt(prop.getProperty(mRecordWidthSummaryItem)));
			}
			if (prop.getProperty(mRecordWidthSummaryValue) != null) {
				SystemData.setRecordWidthSummaryValue(Integer.parseInt(prop.getProperty(mRecordWidthSummaryValue)));
			}

		} catch (IOException | NumberFormatException e) {
			MessageDialog.openWarning(Display.getCurrent().getShells()[0], "Properties File Error", e.toString());
			System.err.println("Properties File Error: " + e.toString());
			e.printStackTrace();
		}
	}
}
