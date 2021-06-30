package beauties.common.lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	private ResultSet mResultSet = null;
	private static DbAccess mInstance;

	private DbAccess() {
		mServer = SystemData.getDbHost();
		mDbPort = SystemData.getDbPort();
		mDb = SystemData.getDbName();
		mUser = SystemData.getDbUser();
		mPass = SystemData.getDbPass();
		mUrl = "jdbc:mysql://" + mServer + ":" + mDbPort + "/" + mDb + "?useServerPrepStmts=true&useSSL=false&allowPublicKeyRetrieval=true";

		try {
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
	
	static DbAccess updateInstance() {
		try {
			mInstance.mCon.setCatalog(SystemData.getDbName());
		} catch (SQLException e) {
			sqlStatementError(e);
		} catch (Exception e) {
		}
		return mInstance;
	}

	void executeUpdate(PreparedStatement pPreparedStatement) {
//		System.out.println(pPreparedStatement);
		try {
			pPreparedStatement.executeUpdate();
			if(!SystemData.getDbUpdated())
				SystemData.setDbUpdated(true);
		} catch (SQLException e) {
			sqlStatementError(e);
		} catch (Exception e) {
		}

	}
	
	ResultSet executeQuery(PreparedStatement pPreparedStatement) {
//		System.out.println(pPreparedStatement);
		try {
			mResultSet = pPreparedStatement.executeQuery();

		} catch (SQLException e) {
			sqlStatementError(e);
		} catch (Exception e) {
		}

		return mResultSet;

	}
	
	PreparedStatement getPreparedStatement(String pQuery) {
		try {
			return mCon.prepareStatement(pQuery);
		} catch (SQLException e) {
			sqlStatementError(e);
		} catch (Exception e) {
			
		}
		
		return null;
	}

	private void sqlConnectionError(SQLException e) {
		MessageDialog.openWarning(Display.getCurrent().getShells()[0], "SQL Connection Error", e.toString());
		System.err.println("SQL Connection Error: " + e.toString());
	}

	private static void sqlStatementError(SQLException e) {
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
	
	public void closeConnection() {
		try {
			mCon.close();
		} catch (SQLException e) {
			sqlStatementError(e);
		} catch (Exception e) {
		}
	}
	
	boolean isNull() {
		return mCon == null;
	}
}
