package view.entry;

import model.DateRange;
import model.RecordTableItem;
import model.SystemData;
import model.db.DbUtil;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import view.util.MyGridData;
import view.util.MyGridLayout;

class CompositeRecordTable extends Composite {

	private CompositeEntry mCompositeEntry;

	// private Date mStartDate;
	// private Date mEndDate;
	private DateRange mDateRange;

	private RecordTableItem[] mRecordItemsUp;
	private RecordTableItem[] mRecordItemsBottom;

	private RecordTableViewer mTableUp;
	private RecordTableViewer mTableBottom;
	private SashForm mSashForm;

	public CompositeRecordTable(Composite pParent) {
		super(pParent, SWT.NONE);
		initParam((CompositeEntry) pParent);
		RecordTableItem[][] wRecordTableItemAll = DbUtil.getRecordTableItems(mDateRange,
				mCompositeEntry.getBookId());
		mRecordItemsUp = wRecordTableItemAll[0];
		mRecordItemsBottom = wRecordTableItemAll[1];

		initLayout();
	}

	public CompositeRecordTable(Composite pParent, RecordTableItem[][] pRecordTableItems) {
		super(pParent, SWT.NONE);
		initParam((CompositeEntry) pParent);
		mRecordItemsUp = pRecordTableItems[0];
		mRecordItemsBottom = pRecordTableItems[1];
		initLayout();
	}

	private void initParam(CompositeEntry pCompositeEntry) {
		mCompositeEntry = (CompositeEntry) pCompositeEntry;
		this.mDateRange = mCompositeEntry.getDateRange();
		// this.mStartDate = mCompositeEntry.getStartDate();
		// this.mEndDate = mCompositeEntry.getEndDate();
	}

	private void initLayout() {
		this.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		this
				.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true)
						.getMyGridData());

		mSashForm = new SashForm(this, SWT.VERTICAL);
		mSashForm.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		mSashForm.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true)
				.getMyGridData());

		mTableUp = new RecordTableViewer(mSashForm, mCompositeEntry);
		mTableUp.setRecordTableItem(mRecordItemsUp);
		mTableUp.getTable().setSelection(0);
		mTableUp.getTable().setFocus();

		Composite wBottomComp = new Composite(mSashForm, SWT.NONE);
		wBottomComp.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		wBottomComp.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true)
				.getMyGridData());

		mSashForm.setWeights(SystemData.getRecordTableWeights());

		Label wLabel = new Label(wBottomComp, SWT.NONE);
		wLabel.setText("当月の収支予定");
		wLabel.setLayoutData(new MyGridData(GridData.FILL, GridData.CENTER, true, false)
				.getMyGridData());

		mTableBottom = new RecordTableViewer(wBottomComp, mCompositeEntry);

		if (mRecordItemsBottom.length > 0) {
			mTableBottom.setRecordTableItem(mRecordItemsBottom);
		}

		wBottomComp.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent arg0) {
				SystemData.setRecordTableWeights(mSashForm.getWeights());
			}

			@Override
			public void controlMoved(ControlEvent arg0) {

			}
		});

		mTableUp.getTable().addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent arg0) {
			}

			public void focusGained(FocusEvent arg0) {
				mTableBottom.getTable().deselectAll();
			}
		});
		mTableBottom.getTable().addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent arg0) {
			}

			public void focusGained(FocusEvent arg0) {
				mTableUp.getTable().deselectAll();
			}
		});

	}

	RecordTableItem getSelectedRecordItem() {
		if (!mTableUp.getSelection().isEmpty())
			return (RecordTableItem) (((IStructuredSelection) mTableUp.getSelection())
					.getFirstElement());
		else
			return (RecordTableItem) (((IStructuredSelection) mTableBottom.getSelection())
					.getFirstElement());
	}

	boolean hasSelectedItem() {
		if (!mTableUp.hasSelectedItem() && !mTableBottom.hasSelectedItem())
			return false;
		return !getSelectedRecordItem().isBalanceRow();
	}

	public void updateRecordFilter(ViewerFilter pFilter) {
		removeFilter();
		if (pFilter != null) {
			mTableUp.addFilter(pFilter);
			mTableBottom.addFilter(pFilter);
		}
	}

	public void removeFilter() {
		if (mTableUp.getFilters().length > 0)
			for (ViewerFilter vf : mTableUp.getFilters())
				mTableUp.removeFilter(vf);
		if (mTableBottom.getFilters().length > 0)
			for (ViewerFilter vf : mTableBottom.getFilters())
				mTableBottom.removeFilter(vf);
	}

	void updateForSearch(RecordTableItem[][] pRecordTableItems) {
		mRecordItemsUp = pRecordTableItems[0];
		mRecordItemsBottom = pRecordTableItems[1];
		mSashForm.dispose();
		initLayout();
		this.layout();
	}
}