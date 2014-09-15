package beauties.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent; //import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import beauties.annual.view.CompositeAnnualMain;
import beauties.common.lib.DbUtil;
import beauties.common.lib.FileLoader;
import beauties.common.lib.SystemData;
import beauties.common.model.RightType;
import beauties.common.view.MyFillLayout;
import beauties.common.view.MyGridData;
import beauties.common.view.MyGridLayout;
import beauties.config.view.MyPreferenceManager;
import beauties.memo.CompositeMemoMain;
import beauties.record.view.CompositeEntry;


public class BeautiesMain extends ApplicationWindow {


	private Composite mMainComposite;
	private Composite mLeftComposite;
	private Composite mRightComposite;

//	private RightType mRightType = RightType.Main;
	private EnumMap<RightType, Button> mRightTypeMap;

	private static final int mLeftWidthHint = 100;
	private static final int mLeftHeightHint = 200;
	
	private String mFileLoaderFileName = "beauties.properties";

	private Image mIcon;

	public BeautiesMain(String pFileName) {
		super(null);
		if (!"".equals(pFileName)) {
			mFileLoaderFileName = pFileName;
		}
	}

	@Override
	protected void configureShell(final Shell pShell) {
		super.configureShell(pShell);
		setExceptionHandler(new IExceptionHandler() {
			@Override
			public void handleException(Throwable e) {
				StringBuffer wStack = new StringBuffer();
				for (int i = 0; i < ((e.getStackTrace().length > 10) ? 10
						: e.getStackTrace().length); i++)
					wStack.append(e.getStackTrace()[i] + "\n");
				wStack.append("...");
				MessageDialog.openWarning(pShell, "Internal Error", e.toString() + "\n\n" + wStack);
				e.printStackTrace();
			}
		});
		new FileLoader(mFileLoaderFileName);

		pShell.setText(SystemData.getWindowTitle());
		pShell.setSize(SystemData.getWindowPoint());
		pShell.setMaximized(SystemData.isWindowMaximized());
		
		Menu wMenuBar = pShell.getDisplay().getMenuBar();
		if (wMenuBar == null) {
			wMenuBar = new Menu(pShell, SWT.BAR);
			pShell.setMenuBar(wMenuBar);
		}
		MenuItem wDbMenu = new MenuItem(wMenuBar, SWT.CASCADE);
		wDbMenu.setText("切り替え");
		Menu wDropdown = new Menu(wMenuBar);
		wDbMenu.setMenu(wDropdown);
		for (final String wDbName : SystemData.getDbNameMap().keySet()) {
			MenuItem wDbItem = new MenuItem(wDropdown, SWT.PUSH);
			wDbItem.setText(wDbName);
			wDbItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateDb(wDbName, pShell);
				}
			});
		}
		
		if (SystemData.getImageFileName() == null) {
			return;
		}
		String wParentDir = "".equals(SystemData.getWorkDir()) ? "" : SystemData.getWorkDir() + "/";
		ImageData wImageData = null;
		try {
			InputStream wImageFileStream = new FileInputStream(wParentDir + SystemData.getImageFileName());
			wImageData = new ImageData(wImageFileStream);
		} catch (IOException | SWTException e) {
			MessageDialog.openWarning(Display.getCurrent().getShells()[0], "Image file error", e.toString());
			System.err.println("Image file error: " + e.toString());
			e.printStackTrace();
		}
		if (wImageData == null) {
			return;
		}
		mIcon = new Image(pShell.getDisplay(), wImageData);
		pShell.setImage(mIcon);
	}

	@Override
	public boolean close() {
		// イメージの破棄
		if (mIcon != null && !mIcon.isDisposed())
			mIcon.dispose();
		return super.close();
	}

	@Override
	protected Control createContents(Composite pParent) {
		mMainComposite = new Composite(pParent, SWT.FILL);
		mMainComposite.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		init();
		return pParent;
	}

	private void init() {
		createLeftComposite();
		if (DbUtil.isDbNull()) {
			return;
		}
		createRightComposite();
	}

	private void createLeftComposite() {

		mLeftComposite = new Composite(mMainComposite, SWT.NONE);
		mLeftComposite.setLayout(new MyFillLayout(SWT.VERTICAL).getLayout());

		GridData wGridData = new MyGridData(GridData.HORIZONTAL_ALIGN_FILL,
				GridData.VERTICAL_ALIGN_END, false, false).getMyGridData();
		wGridData.widthHint = mLeftWidthHint;
		wGridData.heightHint = mLeftHeightHint;
		mLeftComposite.setLayoutData(wGridData);

		mRightTypeMap = new EnumMap<RightType, Button>(RightType.class);

		for (final RightType wType : RightType.values()) {
			Button wButton = new Button(mLeftComposite, (wType == RightType.Setting) ? SWT.PUSH : SWT.TOGGLE);
			wButton.setText(wType.toString());
			mRightTypeMap.put(wType, wButton);
			wButton.addSelectionListener(createSelectionAdapter(wType));
		}
		mRightTypeMap.get(SystemData.getRightType()).setSelection(true);
		mRightTypeMap.get(SystemData.getRightType()).setBackground(SystemData.getColorYellow());
	}

	private SelectionAdapter createSelectionAdapter(final RightType wType) {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button wButton = (Button) e.getSource();
				if (SystemData.getRightType().equals(wType)) {
					wButton.setSelection(true);
					return;
				}
				if (!wType.equals(RightType.Setting)) {
//					mRightTypeMap.get(mRightType).setBackground(null);
//					mRightTypeMap.get(mRightType).setSelection(false);
//					mRightType = wType;
					updateRight(wType);
//					wButton.setBackground(SystemData.getColorYellow());
//					mRightComposite.dispose();
//					createRightComposite();
				} else {
					openConfigDialog();
				}
			}
		};
	}

	private void createRightComposite() {
		switch (SystemData.getRightType()) {
		case Annual:
			mRightComposite = new CompositeAnnualMain(mMainComposite);
			break;
		case Memo:
			mRightComposite = new CompositeMemoMain(mMainComposite);
			break;
		default:
			mRightComposite = new CompositeEntry(mMainComposite);
		}
		mMainComposite.layout();
	}

	private void openConfigDialog() {
		new PreferenceDialog(getShell(), new MyPreferenceManager()).open();
		SystemData.crearCache();

//		mRightTypeMap.get(mRightType).setBackground(null);
//		mRightTypeMap.get(mRightType).setSelection(false);

		updateRight(RightType.Main);
	}

	private void updateRight(RightType pRightType) {
		mRightTypeMap.get(SystemData.getRightType()).setBackground(null);
		mRightTypeMap.get(SystemData.getRightType()).setSelection(false);
//		mRightType = pRightType;
		SystemData.setRightType(pRightType);

		mRightTypeMap.get(SystemData.getRightType()).setBackground(SystemData.getColorYellow());
		mRightTypeMap.get(SystemData.getRightType()).setSelection(true);
		
		if (mRightComposite != null) {
			mRightComposite.dispose();
		}
		if (DbUtil.isDbNull()) {
			return;
		}
		createRightComposite();
	}
	
	private void updateDb(String pNewDisplayName, Shell pShell) {
		if (SystemData.getDbName().equals(SystemData.getDbNameByDisplayName(pNewDisplayName))) {
			return;
		}
		SystemData.setDbName(SystemData.getDbNameByDisplayName(pNewDisplayName));
		SystemData.setWindowTitle(pNewDisplayName);
		DbUtil.updateDb();
		pShell.setText(pNewDisplayName);

		SystemData.crearCache();
		updateRight(RightType.Main);
	}

	public static void main(String[] args) {
//		if (args.length > 0) {
//			new FileLoader(args[0]);
//		}
		String wFileName = args.length > 0 ? args[0] : "";

		BeautiesMain wWindow = new BeautiesMain(wFileName);// トップレベル・シェルの作成
		wWindow.setBlockOnOpen(true); // ウィンドウが閉じられるまでopen()メソッドをブロック
		wWindow.open(); // 表示
		if (Display.getCurrent() != null) {
			Display.getCurrent().dispose();
		}
		SystemData.closeProcess();
	}

}
