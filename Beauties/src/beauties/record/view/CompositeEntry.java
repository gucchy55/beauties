package beauties.record.view;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import beauties.record.RecordController;
import beauties.record.model.RecordTableItem;

import util.view.MyGridData;
import util.view.MyGridLayout;

public class CompositeEntry extends Composite {

	private RecordController mCtl;
	
	private CompositeBookTab mCompositeBookTab;
	private CompositeRecordTable mCompositeRecordTable;
	private CompositeSummaryTable mCompositeSummaryTable;
	private CompositeActionTab mCompositeActionTab;

	public CompositeEntry(Composite pParent) {
		super(pParent, SWT.NONE);
		mCtl = new RecordController(this);
		init();
	}

	private void init() {
		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData());

		mCompositeBookTab = new CompositeBookTab(this, mCtl);
		mCompositeActionTab = new CompositeActionTab(this, mCtl);
		mCompositeRecordTable = new CompositeRecordTable(this, mCtl);
		mCompositeSummaryTable = new CompositeSummaryTable(this, mCtl);
	}

	public void updateView() {
		mCompositeRecordTable.updateTable();
		mCompositeSummaryTable.updateTable();
		mCompositeRecordTable.setFocus();
	}
	
	void updateForSearch() {
		mCompositeActionTab.updateForSearch();
		mCompositeRecordTable.updateForSearch();
	}

	public boolean openSearchDialog() {
		this.getShell().setImeInputMode(SWT.NATIVE);
		InputDialog wInputDialog = new InputDialog(getShell(), "検索", "キーワードを入力", "", null);
		if (wInputDialog.open() != Dialog.OK) 
			return false;
		mCompositeBookTab.setVisible(false);
		mCompositeSummaryTable.setVisible(false);
		mCtl.setSearchResult(true);
		mCtl.updateForSearch(wInputDialog.getValue());
		this.updateForSearch();
		return true;
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
