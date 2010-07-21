package beauties.record.view;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import beauties.model.SystemData;
import beauties.model.db.DbUtil;
import beauties.record.RecordController;
import beauties.record.model.SummaryTableItem;

import util.view.MyGridData;
import util.view.MyGridLayout;

class CompositeSummaryTable extends Composite {

	private static final int mRightWidthHint = 200;

	private RecordController mCtl;
	private SummaryTableItem[] mSummaryTableItems;
	private TableViewer mSummaryTableViewer;
	private ISelectionChangedListener mSelectionChangedListener;

	public CompositeSummaryTable(Composite pParent, RecordController pCtl) {
		super(pParent, SWT.NONE);
		mCtl = pCtl;
		mSummaryTableItems = mCtl.getSummaryTableItems();
		
		mSelectionChangedListener = new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				SummaryTableItem wTableItem = (SummaryTableItem) sel.getFirstElement();
				CompositeEntry wParent = (CompositeEntry) getParent();

				wParent.updateRecordFilter(wTableItem.getRecordTableItemFilter());
			}
		};
		
		this.setLayout(new MyGridLayout(1, false).getMyGridLayout());

		GridData wGridData = new MyGridData(GridData.BEGINNING, GridData.FILL, false, true)
				.getMyGridData();
		wGridData.widthHint = mRightWidthHint;
		this.setLayoutData(wGridData);

		mSummaryTableViewer = new TableViewer(this, SWT.FULL_SELECTION | SWT.BORDER
				| SWT.VIRTUAL);
		Table wTable = mSummaryTableViewer.getTable();

		wTable.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true)
				.getMyGridData());

		// 線を表示する
		wTable.setLinesVisible(DbUtil.showGridLine());
		// ヘッダを可視にする
		wTable.setHeaderVisible(true);

		// 列のヘッダの設定
		TableColumn wItemNameCol = new TableColumn(wTable, SWT.LEFT);
		wItemNameCol.setText("項目名");
		wItemNameCol.setWidth(100);

		TableColumn wValueCol = new TableColumn(wTable, SWT.RIGHT);
		wValueCol.setText("合計");
		wValueCol.setWidth(80);

		mSummaryTableViewer.setContentProvider(new SummaryTableContentProvider());
		mSummaryTableViewer.setInput(mSummaryTableItems);

		mSummaryTableViewer.setLabelProvider(new SummaryTableLabelProvider());

		mSummaryTableViewer.getTable().setSelection(0);

		mSummaryTableViewer.addSelectionChangedListener(mSelectionChangedListener);
	}

	public void updateTable() {
		mSummaryTableItems = mCtl.getSummaryTableItems();
		
		mSummaryTableViewer.removeSelectionChangedListener(mSelectionChangedListener);
		mSummaryTableViewer.setContentProvider(new SummaryTableContentProvider());
		mSummaryTableViewer.setInput(mSummaryTableItems);
		mSummaryTableViewer.setLabelProvider(new SummaryTableLabelProvider());
		mSummaryTableViewer.setInput(mSummaryTableItems);
		mSummaryTableViewer.addSelectionChangedListener(mSelectionChangedListener);
		mSummaryTableViewer.refresh();
	}
}

class SummaryTableContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object inputElement) {
		SummaryTableItem[] wSummaryTableItems = (SummaryTableItem[]) inputElement;
		return wSummaryTableItems;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}

class SummaryTableLabelProvider implements ITableLabelProvider, ITableColorProvider {
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		SummaryTableItem wItem = (SummaryTableItem) element;
		switch (columnIndex) {
		case 0:
			return wItem.getName();
			// if (wItem.getItemId() == SystemData.getUndefinedInt()) {
			// return wItem.getItemName();
			// } else {
			// return ("  " + wItem.getItemName());
			// }
		case 1:
			return SystemData.getFormatedFigures(wItem.getValue());
		}
		return null;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Color getBackground(Object pElement, int pColumnIndex) {
		return ((SummaryTableItem) pElement).getEntryColor();
		// SummaryTableItem wItem = (SummaryTableItem) pElement;
		//		
		// if (wItem.isAppearedSum()) {
		// // みかけ収支（赤）
		// return SystemData.getColorRed();
		// } else if (wItem.isAppearedIncomeExpense()) {
		// // みかけ収入、支出（緑）
		// return SystemData.getColorGreen();
		// } else if (wItem.isSpecial()) {
		// // 残高、営業収支等（青）
		// return SystemData.getColorBlue();
		// } else if (wItem.isCategory()) {
		// // カテゴリ（黄色）
		// return SystemData.getColorYellow();
		// } else {
		// // アイテム（グレー）
		// return SystemData.getColorGray();
		// }
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
