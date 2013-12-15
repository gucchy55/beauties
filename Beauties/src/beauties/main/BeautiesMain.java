package beauties.main;

import java.util.EnumMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
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

import beauties.annual.view.CompositeAnnualMain;
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

	private RightType mRightType = RightType.Main;
	private EnumMap<RightType, Button> mRightTypeMap;

	private static final int mLeftWidthHint = 100;
	private static final int mLeftHeightHint = 200;

	private Image mIcon;

	public BeautiesMain() {
		super(null);
	}

	@Override
	protected void configureShell(final Shell pShell) {
		super.configureShell(pShell);
		pShell.setText(SystemData.getWindowTitle());
		pShell.setSize(SystemData.getWindowPoint());
		pShell.setMaximized(SystemData.isWindowMaximized());

		ImageData wImageData = new ImageData("image/beauties.gif");
		mIcon = new Image(pShell.getDisplay(), wImageData);
		pShell.setImage(mIcon);

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
	}

	@Override
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
		createLeftComposite();
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
		mRightTypeMap.get(mRightType).setSelection(true);
		mRightTypeMap.get(mRightType).setBackground(SystemData.getColorYellow());
	}

	private SelectionAdapter createSelectionAdapter(final RightType wType) {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button wButton = (Button) e.getSource();
				if (mRightType.equals(wType)) {
					wButton.setSelection(true);
					return;
				}
				if (!wType.equals(RightType.Setting)) {
					mRightTypeMap.get(mRightType).setBackground(null);
					mRightTypeMap.get(mRightType).setSelection(false);
					mRightType = wType;
					wButton.setBackground(SystemData.getColorYellow());
					mRightComposite.dispose();
					createRightComposite();
				} else {
					openConfigDialog();
				}
			}
		};
	}

	private void createRightComposite() {
		switch (mRightType) {
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

		mRightTypeMap.get(mRightType).setBackground(null);
		mRightTypeMap.get(mRightType).setSelection(false);

		mRightType = RightType.Main;
		mRightTypeMap.get(mRightType).setBackground(SystemData.getColorYellow());
		mRightTypeMap.get(mRightType).setSelection(true);
		
		mRightComposite.dispose();
		createRightComposite();
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			new FileLoader(args[0]);
		}

		BeautiesMain wWindow = new BeautiesMain();// トップレベル・シェルの作成
		wWindow.setBlockOnOpen(true); // ウィンドウが閉じられるまでopen()メソッドをブロック
		wWindow.open(); // 表示
		Display.getCurrent().dispose();
		SystemData.closeProcess();
	}

}
