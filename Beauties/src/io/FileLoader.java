package io;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import model.SystemData;

public class FileLoader {

	private static String mFileName;

	private static final String mDbHostKey = "dbHost";
	private static final String mDbPortKey = "dbPort";
	private static final String mDbUserKey = "dbUser";
	private static final String mDbPassKey = "dbPass";
	private static final String mDbNameKey = "dbName";

//	private static final String mWindowXKey = "x";
//	private static final String mWindowYKey = "y";
	private static final String mWindowWidthKey = "width";
	private static final String mWindowHeightKey = "height";
	private static final String mWindowMaximiedKey = "maximized";

	private static final String mEntryHeightWeightUpKey = "heightWeightUp";
	private static final String mEntryHeightWeightDownKey = "heightWeightDown";

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
			
			SystemData.setWindowPoint(new Point(
//					Integer.parseInt(prop.getProperty(mWindowXKey)),
//					Integer.parseInt(prop.getProperty(mWindowYKey)), 
					Integer.parseInt(prop.getProperty(mWindowWidthKey)), 
					Integer.parseInt(prop.getProperty(mWindowHeightKey))));

			if (Integer.parseInt(prop.getProperty(mWindowMaximiedKey)) == 1) {
				SystemData.setWindowMaximized(true);
			}
						
			SystemData.setRecordTableWeights(new int[] {
					Integer.parseInt(prop.getProperty(mEntryHeightWeightUpKey)),
					Integer.parseInt(prop.getProperty(mEntryHeightWeightDownKey))
			});
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
}
