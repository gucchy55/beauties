package beauties.annual;

import org.eclipse.jface.action.Action;

import beauties.annual.view.CompositeAnnualMain;

public class UpdateAnnual extends Action {

	private CompositeAnnualMain mCompositeAnnualMain;

	public UpdateAnnual(CompositeAnnualMain pCompositeAnnualMain) {
		mCompositeAnnualMain = pCompositeAnnualMain;
	}

	@Override
	public void run() {
		mCompositeAnnualMain.updateView();

	}

}
