package view.annual;

import java.util.Date;

import model.AnnualViewType;
import model.SystemData;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import view.util.MyGridData;
import view.util.MyGridLayout;

public class CompositeAnnualMain extends Composite {

	private int mBookId = SystemData.getAllBookInt();
	private Date mStartDate = null;
	private Date mEndDate = null;
	private boolean isAnnualPeriod = false;
	private AnnualViewType mAnnualViewType = AnnualViewType.Category;
	private int mMonthCount = 13;

	private CompositeAnnualTable mCompositeAnnualTable;

	public CompositeAnnualMain(Composite pParent) {
		super(pParent, SWT.NONE);
		init();
	}

	private void init() {
		// long wTime = System.currentTimeMillis();
		// if (SystemData.getBookId() == SystemData.getUndefinedInt()) {
		// SystemData.setBookId(SystemData.getAllBookInt());
		// }

		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());

		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData());

		new CompositeAnnualBookTab(this);
		new CompositeAnnualActionTab(this);

		mCompositeAnnualTable = new CompositeAnnualTable(this);

		GridData wGridData = new GridData(GridData.FILL_BOTH);
		wGridData.horizontalSpan = 2;
		mCompositeAnnualTable.setLayoutData(wGridData);

	}

	public void updateView() {

		for (Control wCtrl : this.getChildren()) {
			wCtrl.dispose();
		}
		
		this.init();
		this.layout();

	}

	public int getBookId() {
		return mBookId;
	}

	public Date getStartDate() {
		return mStartDate;
	}

	public Date getEndDate() {
		return mEndDate;
	}

	public boolean isAnnualPeriod() {
		return isAnnualPeriod;
	}

	public AnnualViewType getAnnualViewType() {
		return mAnnualViewType;
	}

	public int getMonthCount() {
		return mMonthCount;
	}

	public void setBookId(int pBookId) {
		this.mBookId = pBookId;
	}

	public void setStartDate(Date pStartDate) {
		this.mStartDate = pStartDate;
	}

	public void setEndDate(Date pEndDate) {
		this.mEndDate = pEndDate;
	}

	public void setAnnualPeriod(boolean isAnnualPeriod) {
		this.isAnnualPeriod = isAnnualPeriod;
	}

	public void setAnnualViewType(AnnualViewType pAnnualViewType) {
		this.mAnnualViewType = pAnnualViewType;
	}

	public void setMonthCount(int pMonthCount) {
		this.mMonthCount = pMonthCount;
	}

}
