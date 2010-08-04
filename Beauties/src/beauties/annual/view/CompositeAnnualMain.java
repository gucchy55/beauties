package beauties.annual.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import beauties.annual.AnnualController;
import beauties.annual.model.AnnualViewType;
import beauties.common.view.CompositePeriodBookTab;
import beauties.common.view.MyGridData;
import beauties.common.view.MyGridLayout;


public class CompositeAnnualMain extends Composite {

	private AnnualController mCTL;
	private CompositeAnnualTable mCompositeAnnualTable;
	private CompositeAnnualActionTab mCompositeAnnualActionTab;
	private CompositePeriodBookTab mCompositeAnnualBookTab;
	
	public CompositeAnnualMain(Composite pParent) {
		super(pParent, SWT.NONE);
		mCTL = new AnnualController(this);
		init();
	}

	private void init() {

		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());

		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData());

		mCompositeAnnualBookTab = new CompositePeriodBookTab(mCTL);
		mCompositeAnnualActionTab = new CompositeAnnualActionTab(mCTL);

		mCompositeAnnualTable = new CompositeAnnualTable(mCTL);

		GridData wGridData = new GridData(GridData.FILL_BOTH);
		wGridData.horizontalSpan = 2;
		mCompositeAnnualTable.setLayoutData(wGridData);

	}

	public void updateTable() {
		mCompositeAnnualBookTab.setVisible(mCTL.getAnnualViewType() != AnnualViewType.Original);
		mCompositeAnnualActionTab.updateFiscalButton();
		mCompositeAnnualTable.recreateMainTable();
	}
	
	public void copyToClipboard() {
		mCompositeAnnualTable.copySelectedTextToClipboard();
	}
}
