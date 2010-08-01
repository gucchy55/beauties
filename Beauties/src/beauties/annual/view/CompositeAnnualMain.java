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
	
	public CompositeAnnualMain(Composite pParent) {
		super(pParent, SWT.NONE);
		mCTL = new AnnualController(this);
		init();
	}

	private void init() {

		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());

		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData());

		new CompositeAnnualBookTab(this, mCTL);
		new CompositeAnnualActionTab(this, mCTL);

		mCompositeAnnualTable = new CompositeAnnualTable(this, mCTL);

		GridData wGridData = new GridData(GridData.FILL_BOTH);
		wGridData.horizontalSpan = 2;
		mCompositeAnnualTable.setLayoutData(wGridData);

	}

//	public void updateTable() {
//		mCompositeAnnualTable.updateTable();
//	}
	
	public void recreateMainTable() {
		mCompositeAnnualTable.recreateMainTable();
	}
	
	public void copyToClipboard() {
		mCompositeAnnualTable.copySelectedTextToClipboard();
	}

}
