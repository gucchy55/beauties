package beauties.record.view;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import beauties.common.view.CompositePeriodBookTab;
import beauties.record.RecordController;
import beauties.record.model.RecordTableItem;

import util.view.MyGridData;
import util.view.MyGridLayout;

public class CompositeEntry extends Composite {

	private RecordController mCTL;
	
	private CompositePeriodBookTab mCompositePeriodBookTab;
	private CompositeRecordTable mCompositeRecordTable;
	private CompositeSummaryTable mCompositeSummaryTable;
	private CompositeActionTab mCompositeActionTab;

	public CompositeEntry(Composite pParent) {
		super(pParent, SWT.NONE);
		mCTL = new RecordController(this);
		init();
	}

	private void init() {
		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData());

		mCompositePeriodBookTab = new CompositePeriodBookTab(mCTL);
		mCompositeActionTab = new CompositeActionTab(mCTL);
		mCompositeRecordTable = new CompositeRecordTable(mCTL);
		mCompositeSummaryTable = new CompositeSummaryTable(mCTL);
	}

	public void updateView() {
		mCompositePeriodBookTab.updateMonthLabel();
		mCompositeRecordTable.updateTable();
		mCompositeSummaryTable.updateTable();
		mCompositeRecordTable.setFocus();
	}
	
	public void updateViewForSearch(boolean pSearch) {
		mCompositePeriodBookTab.setVisible(!pSearch);
		mCompositeSummaryTable.setVisible(!pSearch);
		if (!pSearch)
			return;
		mCompositeActionTab.updateForSearch();
		mCompositeRecordTable.updateForSearch();
		mCompositeRecordTable.setFocus();
	}
	
	public RecordTableItem getSelectedRecordItem() {
		return mCompositeRecordTable.getSelectedRecordItem();
	}
	
	public boolean hasSelectedRecordTableItem() {
		return mCompositeRecordTable.hasSelectedItem();
	}
	
	public void updateRecordFilter(ViewerFilter pFilter) {
		mCompositeRecordTable.updateRecordFilter(pFilter);
	}
}
