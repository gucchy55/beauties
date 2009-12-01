package io;

public class FileLoader {

	private static final String mFileName = "beauties.ini";
//	private static final String mLeftWidthKey = "Left";
//	private static final String mRightWidthKey = "Right";

	public FileLoader() {
		try {
			java.util.Properties prop = new java.util.Properties();
			prop.load(new java.io.FileInputStream(mFileName));
			String key1 = prop.getProperty("key1");
			String key2 = prop.getProperty("key2");
			System.out.println("key1=" + key1);
			System.out.println("key2=" + key2);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
}
