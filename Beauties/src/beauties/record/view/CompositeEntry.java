package beauties.record.view;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import beauties.common.view.CompositePeriodBookTab;
import beauties.common.view.MyGridData;
import beauties.common.view.MyGridLayout;
import beauties.record.RecordController;
import beauties.record.model.RecordTableItem;


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
		this.setLayout(new MyGridLayout(3, false).getMyGridLayout());
		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData());

		GridData wGridDataBook = new MyGridData(GridData.BEGINNING, GridData.BEGINNING, false, false).getMyGridData();
		wGridDataBook.horizontalSpan = 1;

		GridData wGridDataAction = new MyGridData(GridData.END, GridData.BEGINNING, false, false).getMyGridData();
		wGridDataAction.horizontalSpan = 2;

		GridData wGridDataRecord = new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData();
		wGridDataRecord.horizontalSpan = 2;

		GridData wGridDataSummary = new MyGridData(GridData.END, GridData.FILL, false, false)
				.getMyGridData();
		wGridDataSummary.horizontalSpan = 1;
		wGridDataSummary.widthHint = 230;

		mCompositePeriodBookTab = new CompositePeriodBookTab(mCTL);
		mCompositePeriodBookTab.setLayoutData(wGridDataBook);
		mCompositeActionTab = new CompositeActionTab(mCTL);
		mCompositeActionTab.setLayoutData(wGridDataAction);
		mCompositeRecordTable = new CompositeRecordTable(mCTL);
		mCompositeRecordTable.setLayoutData(wGridDataRecord);
		mCompositeSummaryTable = new CompositeSummaryTable(mCTL);
		mCompositeSummaryTable.setLayoutData(wGridDataSummary);
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
	
	public void updateViewForHistory(boolean pHistory) {
		mCompositePeriodBookTab.setVisible(!pHistory);
		mCompositeSummaryTable.setVisible(!pHistory);
		if (!pHistory)
			return;
		mCompositeActionTab.updateForHistory();
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
	
//	public void removeRecordTableListeners() {
//		mCompositeRecordTable.removeListers();
//	}
//	public void addRecordTableListeners() {
//		mCompositeRecordTable.addListeners();
//	}
	
}
