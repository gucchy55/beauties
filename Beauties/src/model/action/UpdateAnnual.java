package model.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import view.CompositeRightMain;
import view.annual.CompositeAnnualMain;

public class UpdateAnnual extends Action {

	private Composite mParent;

	public UpdateAnnual(Composite pComposite) {
		mParent = pComposite;
	}

	@Override
	public void run() {
		CompositeRightMain wComp = (CompositeRightMain) mParent;
		for (Control wChild : wComp.getChildren()) {
			wChild.dispose();
		}
		new CompositeAnnualMain(wComp);
		wComp.layout();
	}

}
