package util.view;

import org.eclipse.swt.layout.RowLayout;


public class MyRowLayout {

	private RowLayout mRowLayout;
	private static final int mMarginBotton = 0;
	private static final int mMarginHeight = 0;
	private static final int mMarginLeft = 0;
	private static final int mMarginRight = 0;
	private static final int mMarginTop = 0;
	private static final int mMarginWidth = 0;
	private static final int mSpacing = 0;

	public MyRowLayout() {
		mRowLayout = new RowLayout();
		mRowLayout.marginBottom = mMarginBotton;
		mRowLayout.marginHeight = mMarginHeight;
		mRowLayout.marginLeft = mMarginLeft;
		mRowLayout.marginRight = mMarginRight;
		mRowLayout.marginTop = mMarginTop;
		mRowLayout.marginWidth = mMarginWidth;
		mRowLayout.spacing = mSpacing;
	}
	
	public RowLayout getMyRowLayout() {
		return mRowLayout;
	}
	
}
