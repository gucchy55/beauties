package view.annual;

import java.util.Date;

import model.SystemData;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import util.Util;
import view.entry.CompositeActionTab;
import view.entry.CompositeBookTab;
import view.util.MyGridData;
import view.util.MyGridLayout;

public class CompositeAnnualMain extends Composite {

	public CompositeAnnualMain(Composite pParent) {
		super(pParent, SWT.NONE);
		init();
	}

	private void init() {
//		long wTime = System.currentTimeMillis();
		SystemData.setBookId(SystemData.getAllBookInt());
		
		if (SystemData.getStartDate() == null) {
			Date[] wDates = Util.getPeriod(new Date());
			SystemData.setStartDate(wDates[0]);
			SystemData.setEndDate(wDates[1]);
		}
		
		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());

		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true,
				true).getMyGridData());
	
		new CompositeBookTab(this);
		new CompositeActionTab(this);
		CompositeAnnualTable wCompositeAnnualTable = new CompositeAnnualTable(this);
		
		GridData wGridData = new GridData(GridData.FILL_BOTH);
		wGridData.horizontalSpan = 2;
		wCompositeAnnualTable.setLayoutData(wGridData);
		
//		mCompositeRecordTable = new CompositeRecordTable(this);
//		new CompositeSummaryTable(this);
//		System.out.println(System.currentTimeMillis() - wTime);
		
	}

}
