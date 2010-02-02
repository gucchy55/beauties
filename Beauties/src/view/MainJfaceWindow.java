package view;

import model.RightType;
import model.action.InitMainWindow;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import view.util.MyFillLayout;
import view.util.MyGridData;
import view.util.MyGridLayout;

public class MainJfaceWindow extends ApplicationWindow {

	private static final String mWindowTitle = "家計簿";
	private int mWindowWidth = 1000;
	private int mWindowHeight = 700;

	private Composite mMainComposite;
	private Composite mLeftComposite;
	private Composite mRightComposite;

	private static final int mLeftWidthHint = 100;
	private static final int mLeftHeightHint = 200;
	private static final String[] mLeftButtonNameArray = { "記帳", "年間一覧", "グラフ",
			"メモ帳", "設定" };
	private Button[] mLeftButtonArray = new Button[mLeftButtonNameArray.length];

	public MainJfaceWindow() {
		super(null); // トップレベル・シェルなので、親シェルはnull

	}

	@Override
	protected Control createContents(Composite pParent) {

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
				MessageDialog.openWarning(getShell(), "Internal Error", e.toString() + "\n\n"
						+ wStack);
				e.printStackTrace();
			}
		});

		Shell wShell = this.getShell();
		wShell.setText(mWindowTitle);
		wShell.setSize(mWindowWidth, mWindowHeight);

		mMainComposite = new Composite(pParent, SWT.FILL);
		mMainComposite.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		init();
		return pParent;
	}
//
//	@Override
//	protected MenuManager createMenuManager() {
//		MenuManager menubar = new MenuManager();
//		MenuManager menu = new MenuManager("アクション(&A)");
//		menubar.add(menu);
//
//		menu.add(new OpenDialogNewRecord(getShell()));
//		menu.add(new OpenDialogNewMove(getShell()));
//		return menubar;
//	}

	private void init() {
		new InitMainWindow(this).run();
	}

	private void init(RightType pRightType) {
		new InitMainWindow(this, pRightType).run();
	}

	public void createLeftComposite(Composite wParent) {
		mLeftComposite = new Composite(wParent, SWT.NONE);
		mLeftComposite.setLayout(new MyFillLayout(SWT.VERTICAL)
				.getMyFillLayout());

		GridData wGridData = new MyGridData(GridData.HORIZONTAL_ALIGN_FILL,
				GridData.VERTICAL_ALIGN_END, false, false).getMyGridData();
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
						if (wButtonText.equals(mLeftButtonNameArray[i])) {
							init(RightType.valueOf(i));
							break;
						}
					}
				}
			});
			mLeftButtonArray[i] = wButton;

		}
	}

	public Composite getLeftComposite() {
		return mLeftComposite;
	}

	public Composite getRightComposite() {
		return mRightComposite;
	}

	public static String[] getLeftButtonNameArray() {
		return mLeftButtonNameArray;
	}

	public Composite getmMainComposite() {
		return mMainComposite;
	}

	public Button[] getLeftButtonArray() {
		return mLeftButtonArray;
	}

	public static void main(String[] args) {
		MainJfaceWindow wWindow = new MainJfaceWindow();// トップレベル・シェルの作成
		wWindow.setBlockOnOpen(true); // ウィンドウが閉じられるまでopen()メソッドをブロック
		wWindow.addMenuBar();
		wWindow.open(); // 表示
	}

}
