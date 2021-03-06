package beauties.common.view;

import org.eclipse.swt.layout.FillLayout;

public class MyFillLayout {
	
	private FillLayout mFillLayout;
	private static final int mMarginHeight = 0;
	private static final int mMarginWidth = 0;
	private int mSpacing = 0;
	
	public MyFillLayout(int p){
	
		mFillLayout = new FillLayout(p);
		mFillLayout.marginHeight = mMarginHeight;
		mFillLayout.marginWidth = mMarginWidth;
		mFillLayout.spacing = mSpacing;

	}
	
	public FillLayout getLayout() {
		return mFillLayout;
	}
	
	public void setSpacing(int pSpacing) {
		mFillLayout.spacing = pSpacing;
	}
}
