package beauties.common.view;

import org.eclipse.swt.layout.GridLayout;

public class MyGridLayout  {
	
	private GridLayout mGridLayout;
	private static final int mMarginBotton = 0;
	private static final int mMarginHeight = 0;
	private static final int mMarginLeft = 0;
	private static final int mMarginRight = 0;
	private static final int mMarginTop = 0;
	private static final int mMarginWidth = 0;
	private static final int mHorizontalSpacing = 0;
	private static final int mVerticalSpacing = 0;
	
	public MyGridLayout(int pColNum, boolean pSameSpacing) {
		mGridLayout = new GridLayout(pColNum, pSameSpacing); 
		mGridLayout.marginBottom = mMarginBotton;
		mGridLayout.marginHeight = mMarginHeight;
		mGridLayout.marginLeft = mMarginLeft;
		mGridLayout.marginRight = mMarginRight;
		mGridLayout.horizontalSpacing = mHorizontalSpacing;
		mGridLayout.verticalSpacing = mVerticalSpacing;
		mGridLayout.marginTop = mMarginTop;
		mGridLayout.marginWidth = mMarginWidth;
	}
	
	public GridLayout getMyGridLayout() {
		return mGridLayout;
	}
	
}
