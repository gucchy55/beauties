package beauties.common.lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;


class DbAccess {

	private static String mServer;
	private static String mDb;
	private static String mUser;
	private static String mPass;
	private static String mUrl;
	private static int mDbPort;
	private Connection mCon = null;
	private Statement mStmt = null;
	private ResultSet mResultSet = null;
	private static DbAccess mInstance;

	private DbAccess() {
		mServer = SystemData.getDbHost();
		mDbPort = SystemData.getDbPort();
		mDb = SystemData.getDbName();
		mUser = SystemData.getDbUser();
		mPass = SystemData.getDbPass();
		mUrl = "jdbc:mysql://" + mServer + ":" + mDbPort + "/" + mDb;
		try {
//			Class.forName("org.gjt.mm.mysql.Driver");
			mCon = DriverManager.getConnection(mUrl, mUser, mPass);
		} catch (SQLException e) {
			sqlConnectionError(e);
		} catch (Exception e) {
		}
	}
	
	static synchronized DbAccess getInstance() {
	    if (mInstance == null) {
	      mInstance = new DbAccess();
	    }
	    return mInstance;
	  }

	void executeUpdate(String pQuery) {
		try {
			mStmt = mCon.createStatement();
			mStmt.executeUpdate(pQuery);
			if(!SystemData.getDbUpdated())
				SystemData.setDbUpdated(true);
		} catch (SQLException e) {
			sqlStatementError(e);
		} catch (Exception e) {
		}

	}

	ResultSet executeQuery(String pQuery) {

		try {
			// Statementオブジェクトの生成
			mStmt = mCon.createStatement();
			mResultSet = mStmt.executeQuery(pQuery);

		} catch (SQLException e) {
			sqlStatementError(e);
		} catch (Exception e) {
		}

		return mResultSet;

	}

	private void sqlConnectionError(SQLException e) {
		MessageDialog.openWarning(Display.getCurrent().getShells()[0], "SQL Connection Error", e.toString());
		System.err.println("SQL Connection Error: " + e.toString());
	}

	private void sqlStatementError(SQLException e) {
		StringBuffer wStack = new StringBuffer();
		for (int i = 0; i < e.getStackTrace().length; i++) {
			if (i == 20) {
				wStack.append("...");
				break;
			}
			wStack.append(e.getStackTrace()[i] + "\n");
		}
		MessageDialog.openWarning(Display.getCurrent().getShells()[0], "SQL Statement Error", e.toString() + "\n\n"
				+ wStack);
		System.err.println("ResultSet Handling Error: " + e.toString());
	}

}