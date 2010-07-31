package beauties.main;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import beauties.annual.view.CompositeAnnualMain;
import beauties.config.view.MyPreferenceManager;
import beauties.memo.CompositeMemoMain;
import beauties.model.RightType;
import beauties.model.SystemData;
import beauties.record.view.CompositeEntry;

public class InitMainWindow extends Action {

	private ApplicationWindow mWindow;
	private RightType mInputRightType = RightType.Main; // 初期値

	public InitMainWindow(ApplicationWindow pWindow) {
		mWindow = pWindow;
	}

	public InitMainWindow(ApplicationWindow pWindow, RightType pRightType) {
		mWindow = pWindow;
		mInputRightType = pRightType;
	}

	@Override
	public void run() {
		BeautiesMain wMainJfaceWindow = (BeautiesMain) mWindow;
		Composite wMainComposite = wMainJfaceWindow.getmMainComposite();
		if (mInputRightType == RightType.Setting) {
			new PreferenceDialog(wMainJfaceWindow.getShell(), new MyPreferenceManager()).open();
			SystemData.crearCache();
			mInputRightType = RightType.Main;
			wMainJfaceWindow.setRightType(RightType.Main);
		}
		for (Control wControl : wMainComposite.getChildren()) {
			wControl.dispose();
		}
		wMainJfaceWindow.createLeftComposite(wMainComposite);

		switch (mInputRightType) {

		case Main:
			new CompositeEntry(wMainComposite);
			break;

		case Anual:
			new CompositeAnnualMain(wMainComposite);
			break;
			
		case Memo:
			new CompositeMemoMain(wMainComposite);
			break;
		}
		wMainComposite.layout();
	}

}
