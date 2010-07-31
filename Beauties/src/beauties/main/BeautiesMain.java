package beauties.main;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent; //import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import beauties.model.FileLoader;
import beauties.model.RightType;
import beauties.model.SystemData;

import util.view.MyFillLayout;
import util.view.MyGridData;
import util.view.MyGridLayout;

public class BeautiesMain extends ApplicationWindow {

	private static final String mWindowTitle = "家計簿";

	private Composite mMainComposite;
	private Composite mLeftComposite;

	private RightType mRightType = RightType.Main;

	private static final int mLeftWidthHint = 100;
	private static final int mLeftHeightHint = 200;
	private static final String[] mLeftButtonNameArray = { "記帳", "年間一覧", "メモ帳", "設定" };
	Map<Button, RightType> mRightTypeMap;

	private Image mIcon;

	public BeautiesMain() {
		super(null);
	}

	protected void configureShell(final Shell pShell) {
		super.configureShell(pShell);
		pShell.setText(mWindowTitle);
		// pShell.setBounds(SystemData.getWindowRectangle());
		pShell.setSize(SystemData.getWindowPoint());
		pShell.setMaximized(SystemData.isWindowMaximized());

		ImageData wImageData = new ImageData("image/beauties.gif");
		mIcon = new Image(pShell.getDisplay(), wImageData);
		pShell.setImage(mIcon);
		
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
	}

	public boolean close() {
		// イメージの破棄
		if (!mIcon.isDisposed())
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
		createLeftComposite(mMainComposite);
		new InitMainWindow(this).run();
	}

	private void init(RightType pRightType) {
		new InitMainWindow(this, pRightType).run();
	}

	public void createLeftComposite(Composite wParent) {

		mLeftComposite = new Composite(wParent, SWT.NONE);
		mLeftComposite.setLayout(new MyFillLayout(SWT.VERTICAL).getMyFillLayout());

		GridData wGridData = new MyGridData(GridData.HORIZONTAL_ALIGN_FILL,
				GridData.VERTICAL_ALIGN_END, false, false).getMyGridData();
		wGridData.widthHint = mLeftWidthHint;
		wGridData.heightHint = mLeftHeightHint;
		mLeftComposite.setLayoutData(wGridData);

		mRightTypeMap = new LinkedHashMap<Button, RightType>();

		for (int i = 0; i < mLeftButtonNameArray.length; i++) {
			String wButtonName = mLeftButtonNameArray[i];
			Button wButton = new Button(mLeftComposite, SWT.TOGGLE);
			wButton.setText(wButtonName);
			mRightTypeMap.put(wButton, RightType.valueOf(i));
			if (mRightType == RightType.valueOf(i)) {
				wButton.setSelection(true);
			}
		}
		addListenerToLeftButtons();
	}

	private void addListenerToLeftButtons() {
		for (Map.Entry<Button, RightType> entry : mRightTypeMap.entrySet()) {
			Button wButton = entry.getKey();
			wButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Button wButton = (Button) e.getSource();
					if (mRightType.equals(mRightTypeMap.get(wButton))) {
						wButton.setSelection(true);
						return;
					}
					if (!mRightTypeMap.get(wButton).equals(RightType.Setting)) {
						mRightType = mRightTypeMap.get(wButton);
					} else {
						wButton.setSelection(false);
					}
					init(mRightTypeMap.get(wButton));
				}
			});
		}
	}

	public Composite getmMainComposite() {
		return mMainComposite;
	}

	public void setRightType(RightType pRightType) {
		this.mRightType = pRightType;
	}

	
	public static void main(String[] args) {
		if (args.length > 0) {
			new FileLoader(args[0]);
		}

		BeautiesMain wWindow = new BeautiesMain();// トップレベル・シェルの作成
		wWindow.setBlockOnOpen(true); // ウィンドウが閉じられるまでopen()メソッドをブロック
		wWindow.open(); // 表示
		Display.getCurrent().dispose();
		SystemData.dumpDb();
	}

}
