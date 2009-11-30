package view.annual;

import model.SystemData;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import view.util.MyGridData;
import view.util.MyGridLayout;

public class CompositeAnnualMain extends Composite {

	public CompositeAnnualMain(Composite pParent) {
		super(pParent, SWT.NONE);
		init();
	}

	private void init() {
		// long wTime = System.currentTimeMillis();
		if (SystemData.getBookId() == SystemData.getUndefinedInt()) {
			SystemData.setBookId(SystemData.getAllBookInt());
		}

		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());

		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true,
				true).getMyGridData());

		new CompositeAnnualBookTab(this);
		new CompositeAnnualActionTab(this);
		CompositeAnnualTable wCompositeAnnualTable = new CompositeAnnualTable(
				this);

		GridData wGridData = new GridData(GridData.FILL_BOTH);
		wGridData.horizontalSpan = 2;
		wCompositeAnnualTable.setLayoutData(wGridData);

	}
	

}
