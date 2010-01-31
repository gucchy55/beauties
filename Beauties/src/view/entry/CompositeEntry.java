package view.entry;

import java.util.Date;
import model.SystemData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import util.Util;
import view.util.MyGridData;
import view.util.MyGridLayout;

public class CompositeEntry extends Composite {

	private CompositeRecordTable mCompositeRecordTable;
	
	public CompositeEntry(Composite pParent) {
		super(pParent, SWT.NONE);
		init();
	}

	private void init() {
//		long wTime = System.currentTimeMillis();
		
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
		mCompositeRecordTable = new CompositeRecordTable(this);
		new CompositeSummaryTable(this);
//		System.out.println(System.currentTimeMillis() - wTime);
		
	}

	public int getSelectedActId() {
		return mCompositeRecordTable.getSelectedActId();
	}
	
	public void addFiltersToRecord() {
		mCompositeRecordTable.addFilter();
	}
	public void removeFiltersFromRecord() {
		mCompositeRecordTable.removeFilter();
	}
	
//	public void setStripToTable() {
//		mCompositeRecordTable.setStripeToTable();
//	}

}
