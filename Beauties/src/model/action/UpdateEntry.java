package model.action;

import java.util.Date;
import org.eclipse.jface.action.Action;

import util.Util;
import view.entry.CompositeEntry;

public class UpdateEntry extends Action {

	private CompositeEntry mCompositeEntry;

	public UpdateEntry(CompositeEntry pCompositeEntry, Date pDate) {
		mCompositeEntry = (CompositeEntry)pCompositeEntry;
		Date[] wDates = Util.getPeriod(pDate);
		mCompositeEntry.setStartDate(wDates[0]);
		mCompositeEntry.setEndDate(wDates[1]);
	}

	public UpdateEntry(CompositeEntry pCompositeEntry) {
		mCompositeEntry = (CompositeEntry)pCompositeEntry;
	}

	@Override
	public void run() {
		mCompositeEntry.updateView();
//		CompositeRightMain wComp = (CompositeRightMain) mCompositeEntry;
//		for (Control wChild : wComp.getChildren()) {
//			wChild.dispose();
//		}
//		new CompositeEntry(wComp);
//		wComp.layout();
	}

}
