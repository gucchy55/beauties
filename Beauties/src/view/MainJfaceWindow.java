package view;

import io.FileLoader;
import model.RightType;
import model.SystemData;
import model.action.InitMainWindow;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent; //import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import view.util.MyFillLayout;
import view.util.MyGridData;
import view.util.MyGridLayout;

public class MainJfaceWindow extends ApplicationWindow {

	private static final String mWindowTitle = "家計簿";
	// private int mWindowWidth = 1000;
	// private int mWindowHeight = 700;

	private Composite mMainComposite;
	private Composite mLeftComposite;

	private RightType mRightType = RightType.Main;

	private static final int mLeftWidthHint = 100;
	private static final int mLeftHeightHint = 200;
	private static final String[] mLeftButtonNameArray = { "記帳", "年間一覧", "グラフ", "メモ帳", "設定" };
	private Button[] mLeftButtonArray = new Button[mLeftButtonNameArray.length];

	public MainJfaceWindow() {
		super(null); // トップレベル・シェルなので、親シェルはnull

	}

	protected void configureShell(final Shell pShell) {
		super.configureShell(pShell);
		setExceptionHandler(new IExceptionHandler() {
			public void handleException(Throwable e) {
				StringBuffer wStack = new StringBuffer();
				for (int i = 0; i < e.getStackTrace().length; i++) {
					if (i == 10) {
						wStack.append("...");
						break;
					}
					wStack.append(e.getStackTrace()[i] + "\n");
				}
				MessageDialog.openWarning(pShell, "Internal Error", e.toString() + "\n\n" + wStack);
				e.printStackTrace();
			}
		});

		pShell.setText(mWindowTitle);
		pShell.setSize(SystemData.getWindowPoint());
		pShell.setMaximized(SystemData.isWindowMaximized());

	}

	@Override
	protected Control createContents(Composite pParent) {
		mMainComposite = new Composite(pParent, SWT.FILL);
		mMainComposite.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		init();
		return pParent;
	}

	private void init() {
		SystemData.init();
		createLeftComposite(mMainComposite);
		new InitMainWindow(this).run();
	}

	private void init(RightType pRightType) {
		new InitMainWindow(this, pRightType).run();
	}

	public void createLeftComposite(Composite wParent) {

		mLeftComposite = new Composite(wParent, SWT.NONE);
		mLeftComposite.setLayout(new MyFillLayout(SWT.VERTICAL).getMyFillLayout());

		GridData wGridData = new MyGridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_END, false, false)
				.getMyGridData();
		wGridData.widthHint = mLeftWidthHint;
		wGridData.heightHint = mLeftHeightHint;
		mLeftComposite.setLayoutData(wGridData);

		for (int i = 0; i < mLeftButtonNameArray.length; i++) {
			String wButtonName = mLeftButtonNameArray[i];
			Button wButton = new Button(mLeftComposite, SWT.TOGGLE);
			wButton.setText(wButtonName);

			wButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Button wButton = (Button) e.getSource();
					String wButtonText = wButton.getText();
					for (int i = 0; i < mLeftButtonNameArray.length; i++) {
						if (wButtonText.equals(mLeftButtonNameArray[i]) && mRightType != RightType.valueOf(i)) {
							if (RightType.valueOf(i) != RightType.Setting) {
								mRightType = RightType.valueOf(i);
							} else {
								wButton.setSelection(false);
							}
							init(RightType.valueOf(i));
							break;
						}
					}
				}
			});
			mLeftButtonArray[i] = wButton;

		}
	}

	public Composite getmMainComposite() {
		return mMainComposite;
	}

	public Button[] getLeftButtonArray() {
		return mLeftButtonArray;
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			new FileLoader(args[0]);
		}

		MainJfaceWindow wWindow = new MainJfaceWindow();// トップレベル・シェルの作成
		wWindow.setBlockOnOpen(true); // ウィンドウが閉じられるまでopen()メソッドをブロック
		wWindow.open(); // 表示
		Display.getCurrent().dispose();
	}

}
