package model.action;

import model.RightType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import view.CompositeRight2;
import view.MainJfaceWindow;
import view.annual.CompositeAnnualMain;
import view.entry.CompositeEntry;

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
		MainJfaceWindow wMainJfaceWindow = (MainJfaceWindow) mWindow;
		Composite wMainComposite = wMainJfaceWindow.getmMainComposite();

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

		default:
			new CompositeRight2(wMainComposite);

		}

		wMainComposite.layout();

		Button[] wLeftButtonArray = wMainJfaceWindow.getLeftButtonArray();
		wLeftButtonArray[mInputRightType.value].setSelection(true);

	}

}
