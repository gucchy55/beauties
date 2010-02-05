package model.action;

import org.eclipse.jface.action.Action;
import view.annual.CompositeAnnualMain;

public class UpdateAnnual extends Action {

	private CompositeAnnualMain mCompositeAnnualMain;

	public UpdateAnnual(CompositeAnnualMain pCompositeAnnualMain) {
		mCompositeAnnualMain = pCompositeAnnualMain;
	}

	@Override
	public void run() {
		mCompositeAnnualMain.updateView();
		// mCompositeAnnualMain.layout();
		// System.out.println(mCompositeAnnualMain.getBookId());
		// CompositeRightMain wComp = (CompositeRightMain) mCompositeAnnualMain;
		// for (Control wChild : wComp.getChildren()) {
		// wChild.dispose();
		// }
		// new CompositeAnnualMain(wComp);
		// wComp.layout();
	}

}
