package beauties.record.view;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import beauties.common.lib.SystemData;
import beauties.common.view.MyGridData;
import beauties.common.view.MyGridLayout;
import beauties.record.RecordController;
import beauties.record.model.SummaryTableItem;


class CompositeSummaryTable extends Composite {

	private static final int mRightWidthHint = 200;

	private RecordController mCTL;
	private TableViewer mSummaryTableViewer;
	private ISelectionChangedListener mSelectionChangedListener;

	CompositeSummaryTable(RecordController pCTL) {
		super(pCTL.getComposite(), SWT.NONE);
		mCTL = pCTL;
		mSelectionChangedListener = createSelectionChangedListener();
		
		initLayout();
		mSummaryTableViewer = createSummaryTable();

		mSummaryTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		mSummaryTableViewer.setInput(mCTL.getSummaryTableItems());
		mSummaryTableViewer.setLabelProvider(new SummaryTableLabelProvider());
		mSummaryTableViewer.getTable().setSelection(0);
		mSummaryTableViewer.addSelectionChangedListener(mSelectionChangedListener);
	}

	private TableViewer createSummaryTable() {
		TableViewer wSummaryTableViewer = new TableViewer(this, SWT.FULL_SELECTION | SWT.BORDER
				| SWT.VIRTUAL);
		Table wTable = wSummaryTableViewer.getTable();

		wTable.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true)
				.getMyGridData());

		// 線を表示する
		wTable.setLinesVisible(SystemData.showGridLine());
		// ヘッダを可視にする
		wTable.setHeaderVisible(true);

		// 列のヘッダの設定
		TableColumn wItemNameCol = new TableColumn(wTable, SWT.LEFT);
		wItemNameCol.setText("項目名");
		wItemNameCol.setWidth(100);

		TableColumn wValueCol = new TableColumn(wTable, SWT.RIGHT);
		wValueCol.setText("合計");
		wValueCol.setWidth(80);
		
		return wSummaryTableViewer;
	}

	private void initLayout() {
		this.setLayout(new MyGridLayout(1, false).getMyGridLayout());

		GridData wGridData = new MyGridData(GridData.BEGINNING, GridData.FILL, false, true)
				.getMyGridData();
		wGridData.widthHint = mRightWidthHint;
		this.setLayoutData(wGridData);
	}

	private ISelectionChangedListener createSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				SummaryTableItem wTableItem = (SummaryTableItem) sel.getFirstElement();
				CompositeEntry wParent = (CompositeEntry) getParent();
				wParent.updateRecordFilter(wTableItem.getRecordTableItemFilter());
			}
		};
	}

	void updateTable() {
		mSummaryTableViewer.removeSelectionChangedListener(mSelectionChangedListener);
//		mSummaryTableViewer.setContentProvider(new SummaryTableContentProvider());
//		mSummaryTableViewer.setInput(mCTL.getSummaryTableItems());
//		mSummaryTableViewer.setLabelProvider(new SummaryTableLabelProvider());
		mSummaryTableViewer.setInput(mCTL.getSummaryTableItems());
		mSummaryTableViewer.addSelectionChangedListener(mSelectionChangedListener);
		mSummaryTableViewer.refresh();
	}
}

//class SummaryTableContentProvider implements IStructuredContentProvider {
//	@Override
//	public Object[] getElements(Object inputElement) {
//		SummaryTableItem[] wSummaryTableItems = (SummaryTableItem[]) inputElement;
//		return wSummaryTableItems;
//	}
//
//	@Override
//	public void dispose() {
//	}
//
//	@Override
//	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//	}
//}

class SummaryTableLabelProvider implements ITableLabelProvider, ITableColorProvider {
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		SummaryTableItem wItem = (SummaryTableItem) element;
		switch (columnIndex) {
		case 0:
			return wItem.getName();
		case 1:
			return SystemData.getFormatedFigures(wItem.getValue());
		}
		return null;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Color getBackground(Object pElement, int pColumnIndex) {
		return ((SummaryTableItem) pElement).getEntryColor();
	}

	@Override
	public Color getForeground(Object pElement, int pColumnIndex) {
		SummaryTableItem wItem = (SummaryTableItem) pElement;
		if (pColumnIndex == 1 && wItem.getValue() < 0) {
			return new Color(Display.getCurrent(), 255, 0, 0);
		}
		return null;

	}

}
