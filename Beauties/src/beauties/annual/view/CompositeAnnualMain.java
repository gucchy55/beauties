package beauties.annual.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import beauties.annual.AnnualController;

import util.view.MyGridData;
import util.view.MyGridLayout;

public class CompositeAnnualMain extends Composite {

	private AnnualController mCTL;
	private CompositeAnnualTable mCompositeAnnualTable;
	private CompositeAnnualActionTab mCompositeAnnualActionTab;
	private CompositeAnnualBookTab mCompositeAnnualBookTab;
	
	public CompositeAnnualMain(Composite pParent) {
		super(pParent, SWT.NONE);
		mCTL = new AnnualController(this);
		init();
	}

	private void init() {

		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());

		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData());

		mCompositeAnnualBookTab = new CompositeAnnualBookTab(mCTL);
		mCompositeAnnualActionTab = new CompositeAnnualActionTab(mCTL);

		mCompositeAnnualTable = new CompositeAnnualTable(mCTL);

		GridData wGridData = new GridData(GridData.FILL_BOTH);
		wGridData.horizontalSpan = 2;
		mCompositeAnnualTable.setLayoutData(wGridData);

	}

	public void updateTable() {
		mCompositeAnnualBookTab.updateView();
		mCompositeAnnualActionTab.updateFiscalButton();
		mCompositeAnnualTable.recreateMainTable();
	}
	
	public void copyToClipboard() {
		mCompositeAnnualTable.copySelectedTextToClipboard();
	}
}
