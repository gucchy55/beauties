package model.action;

import model.RightType;
import model.SystemData;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import view.CompositeRight2;
import view.CompositeRightMain;
import view.MainJfaceWindow;
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
		if (SystemData.getRightType() != mInputRightType) {
			SystemData.setRightType(mInputRightType);
			Composite wMainComposite = wMainJfaceWindow.getmMainComposite();
			
			for (Control wControl : wMainComposite.getChildren()) {
				wControl.dispose();
			}
				
			wMainJfaceWindow.createLeftComposite(wMainComposite);
			CompositeRightMain wRightComposite = new CompositeRightMain(wMainComposite);
			SystemData.setCompositeRightMain(wRightComposite);
			
			switch (mInputRightType) {
			
			case Main:
				mWindow.getMenuBarManager().getMenu().getItems()[0].setEnabled(true);
				SystemData.init();
				new CompositeEntry(wRightComposite);
				break;
				
			default: 
				mWindow.getMenuBarManager().getMenu().getItems()[0].setEnabled(false);
				SystemData.init();
				new CompositeRight2(wRightComposite);
				
			}
			
			wMainComposite.layout();
		}
		
		Button[] wLeftButtonArray = wMainJfaceWindow.getLeftButtonArray();
		wLeftButtonArray[mInputRightType.value].setSelection(true);
		
	}

}
