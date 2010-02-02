package model.action;

import java.util.Date;

import model.SystemData;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import util.Util;
import view.CompositeRightMain;
import view.entry.CompositeEntry;

public class UpdateEntry extends Action {

	private Composite mCompositeRightMain;

	public UpdateEntry(CompositeEntry pCompositeEntry, Date pDate) {
		mCompositeRightMain = pCompositeEntry.getParent();
		Date[] wDates = Util.getPeriod(pDate);
		SystemData.setStartDate(wDates[0]);
		SystemData.setEndDate(wDates[1]);
	}

	public UpdateEntry(CompositeEntry pCompositeEntry) {
		mCompositeRightMain = pCompositeEntry.getParent();
	}

	@Override
	public void run() {
		CompositeRightMain wComp = (CompositeRightMain) mCompositeRightMain;
		for (Control wChild : wComp.getChildren()) {
			wChild.dispose();
		}
		new CompositeEntry(wComp);
		wComp.layout();
	}

}
