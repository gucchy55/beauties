package util.view;

import org.eclipse.swt.layout.GridData;

public class MyGridData {
	
	private GridData mGridData;

	public MyGridData(int pHorizontalAlignment, 
			int pVerticalAlignment, 
			boolean pGrabExcessHorizontalSpace, 
			boolean pGrabExcessVerticalSpace) {
		
		mGridData = new GridData();
		mGridData.horizontalAlignment = pHorizontalAlignment;
		mGridData.verticalAlignment = pVerticalAlignment;
		mGridData.grabExcessHorizontalSpace = pGrabExcessHorizontalSpace;
		mGridData.grabExcessVerticalSpace = pGrabExcessVerticalSpace;
		
	}
	
	public GridData getMyGridData() {
		return mGridData;
	}
}
