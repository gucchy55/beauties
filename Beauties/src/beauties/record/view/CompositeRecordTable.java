package beauties.record.view;

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

import beauties.common.lib.SystemData;
import beauties.common.view.MyGridData;
import beauties.common.view.MyGridLayout;
import beauties.record.RecordController;
import beauties.record.model.RecordTableItem;


class CompositeRecordTable extends Composite {

	private RecordController mCTL;

	private RecordTableViewer mTableUp;
	private RecordTableViewer mTableBottom;
	private SashForm mSashForm;

	CompositeRecordTable(RecordController pCTL) {
		super(pCTL.getComposite(), SWT.NONE);
		mCTL = pCTL;
		create();
	}

	void updateTable() {
		mTableUp.updateTableItem(mCTL.getRecordItemsUp());
		mTableBottom.updateTableItem(mCTL.getRecordItemsBottom());
	}

	private void create() {
		initLayout();

		createTableUp();

		Composite wBottomComp = createBottomComp();

		createMiddleLabel(wBottomComp);

		createTableButtom(wBottomComp);

		mSashForm.setWeights(SystemData.getRecordTableWeights());

		addFocusListenerToTableUp();
		addFocusListenerToTableBottom();
	}

	private void createTableButtom(Composite wBottomComp) {
		mTableBottom = new RecordTableViewer(wBottomComp, mCTL);
		mTableBottom.setRecordTableItem(mCTL.getRecordItemsBottom());
	}

	private void createMiddleLabel(Composite wBottomComp) {
		Label wLabel = new Label(wBottomComp, SWT.NONE);
		wLabel.setText("当月の収支予定");
		wLabel.setLayoutData(new MyGridData(GridData.FILL, GridData.CENTER, true, false)
				.getMyGridData());
	}

	private void addFocusListenerToTableBottom() {
		mTableBottom.getTable().addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				mTableUp.getTable().deselectAll();
			}
		});
	}

	private void addFocusListenerToTableUp() {
		mTableUp.getTable().addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				mTableBottom.getTable().deselectAll();
			}
		});
	}

	private Composite createBottomComp() {
		Composite wBottomComp = new Composite(mSashForm, SWT.NONE);
		wBottomComp.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		wBottomComp.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true)
				.getMyGridData());
		wBottomComp.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent arg0) {
				SystemData.setRecordTableWeights(mSashForm.getWeights());
			}

			@Override
			public void controlMoved(ControlEvent arg0) {

			}
		});
		return wBottomComp;
	}

	private void createTableUp() {
		mTableUp = new RecordTableViewer(mSashForm, mCTL);
		mTableUp.setRecordTableItem(mCTL.getRecordItemsUp());
		mTableUp.getTable().setSelection(0);
		mTableUp.getTable().setFocus();
	}

	private void initLayout() {
		this.setLayout(new MyGridLayout(1, false).getMyGridLayout());
//		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true)
//						.getMyGridData());

		mSashForm = new SashForm(this, SWT.VERTICAL);
		mSashForm.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		mSashForm.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true)
				.getMyGridData());

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

	void updateRecordFilter(ViewerFilter pFilter) {
		removeFilter();
		if (pFilter != null) {
			mTableUp.addFilter(pFilter);
			mTableBottom.addFilter(pFilter);
		}
	}

	private void removeFilter() {
		if (mTableUp.getFilters().length > 0)
			for (ViewerFilter vf : mTableUp.getFilters())
				mTableUp.removeFilter(vf);
		if (mTableBottom.getFilters().length > 0)
			for (ViewerFilter vf : mTableBottom.getFilters())
				mTableBottom.removeFilter(vf);
	}

//	void updateForSearch() {
//		updateTable();
//	}
	
//	void removeListers() {
//		mTableUp.removeListeners();
//		mTableBottom.removeListeners();
//	}
//	void addListeners() {
//		mTableUp.addListeners();
//		mTableBottom.addListeners();
//	}
	
}