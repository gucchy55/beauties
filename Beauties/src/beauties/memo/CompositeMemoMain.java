package beauties.memo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import beauties.common.lib.SystemData;
import beauties.common.view.MyGridData;
import beauties.common.view.MyGridLayout;


public class CompositeMemoMain extends Composite {

	private File[] mFiles;
	private CTabFolder mCTabFolder;
	private boolean[] mModified;

	public CompositeMemoMain(Composite parent) {
		super(parent, SWT.NONE);
		this.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, true)
				.getMyGridData();
		this.setLayoutData(wGridData);

		mCTabFolder = new CTabFolder(this, SWT.BORDER);
		mCTabFolder.setSimple(false);
		mCTabFolder.setLayoutData(wGridData);

		mFiles = new File(SystemData.getPathMemoDir()).listFiles();
		mModified = new boolean[mFiles.length];
		for (int i = 0; i < mFiles.length; i++)
			mModified[i] = false;
		for (File wFile : mFiles) {
			CTabItem wItem = new CTabItem(mCTabFolder, SWT.NONE);
			wItem.setText(wFile.getName());
			Text wText = new Text(mCTabFolder, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
			wText.setText(getFileContent(wFile));
			addListeners(wText);
			wItem.setControl(wText);
		}
		mCTabFolder.setSelection(0);
	}

	private String getFileContent(File pFile) {
		StringBuffer sb = new StringBuffer();
		try {
			FileInputStream is = new FileInputStream(pFile);
			InputStreamReader in = new InputStreamReader(is, "UTF8");
			int ch;
			while ((ch = in.read()) != -1) {
				sb.append((char) ch);
			}
			in.close();
		} catch (IOException e) {
			fileHandlingError(e);
		}
		return sb.toString();
	}

	private void writeToFile(String pString) {
		File wFile = mFiles[mCTabFolder.getSelectionIndex()];
		try {
			FileOutputStream os = new FileOutputStream(wFile);
			OutputStreamWriter out = new OutputStreamWriter(os, "UTF8");
			BufferedWriter bw = new BufferedWriter(out);
			bw.write(pString);
			bw.close();
			out.close();
			os.close();
		} catch (IOException e) {
			fileHandlingError(e);
		}
	}

	private void addListeners(Text pText) {
		pText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.CTRL && e.keyCode == 's'
						&& mModified[mCTabFolder.getSelectionIndex()]) {
					writeToFile(((Text) e.getSource()).getText());
					mCTabFolder.getSelection().setText(mFiles[mCTabFolder.getSelectionIndex()].getName());
					mModified[mCTabFolder.getSelectionIndex()] = false;
				}
				if (e.stateMask == SWT.CTRL && e.keyCode == 'a')
					((Text) e.getSource()).selectAll();
			}

			public void keyReleased(KeyEvent arg0) {
			}
		});
		pText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				if (mModified[mCTabFolder.getSelectionIndex()])
					return;
				mCTabFolder.getSelection().setText(mCTabFolder.getSelection().getText() + "*");
				mModified[mCTabFolder.getSelectionIndex()] = true;
			}
		});
	}

	private void fileHandlingError(IOException e) {
		MessageDialog.openWarning(Display.getCurrent().getShells()[0], "File Open Error", e
				.toString());
		System.err.println("File Open Error: " + e.toString());
	}

}
