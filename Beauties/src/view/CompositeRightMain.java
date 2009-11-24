package view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import view.util.MyGridData;
import view.util.MyGridLayout;

public class CompositeRightMain extends Composite {

	public CompositeRightMain(Composite pParent) {
		super(pParent, SWT.NONE);
		this.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true,
				true).getMyGridData());

	}

}
