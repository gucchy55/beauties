package view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CompositeRight2 extends Composite {

	private GridData mGridData;

	public CompositeRight2(Composite pParent) {
		super(pParent, SWT.NONE);
		GridLayout wGridLayout = new GridLayout(1, false);
		wGridLayout.marginBottom = 0;
		wGridLayout.marginHeight = 0;
		wGridLayout.marginLeft = 0;
		wGridLayout.marginRight = 0;
		wGridLayout.horizontalSpacing = 0;
		wGridLayout.verticalSpacing = 0;
		this.setLayout(wGridLayout);

		mGridData = new GridData();
		mGridData.horizontalAlignment = GridData.FILL;
		mGridData.verticalAlignment = GridData.FILL;
		mGridData.grabExcessHorizontalSpace = true;
		mGridData.grabExcessVerticalSpace = true;
		this.setLayoutData(mGridData);

		new Label(this, SWT.BORDER).setText("test2");
		

	}

}
