package beauties.annual.view;

import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn; //import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TableItem;

import beauties.annual.AnnualController;
import beauties.annual.model.AnnualHeaderItem;
import beauties.common.lib.SystemData;
import beauties.common.view.MyGridData;
import beauties.common.view.MyGridLayout;
import beauties.record.model.SummaryTableItem;


class CompositeAnnualTable extends Composite {

	private static final int mColumnWidth = SystemData.getAnnualWidth();

	private TableViewer mRowHeaderTableViewer;
	private TableViewer mMainTableViewer;
	private SelectionAdapter mSelectionAdapterForRowHeader;
	private SelectionAdapter mSelectionAdapterForMainTable;

	private AnnualController mCTL;

	CompositeAnnualTable(AnnualController pCTL) {
		super(pCTL.getComposite(), SWT.NONE);
		mCTL = pCTL;

		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, true)
				.getMyGridData();
		this.setLayoutData(wGridData);

		createRowHeaderTable();

		createMainTable();

		createListeners();
		addListeners();

	}

	private void createListeners() {
		mSelectionAdapterForRowHeader = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				mMainTableViewer.getTable().setSelection(
						mRowHeaderTableViewer.getTable().getSelectionIndices());
			}
		};
		mSelectionAdapterForMainTable = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				mRowHeaderTableViewer.getTable().setSelection(
						mMainTableViewer.getTable().getSelectionIndices());
			}
		};
	}

	private void removeListeners() {
		mRowHeaderTableViewer.getTable().removeSelectionListener(mSelectionAdapterForRowHeader);
		mMainTableViewer.getTable().removeSelectionListener(mSelectionAdapterForMainTable);
	}

	private void addListeners() {
		// 選択がシンクロするようリスナーを設定
		mRowHeaderTableViewer.getTable().addSelectionListener(mSelectionAdapterForRowHeader);
		mMainTableViewer.getTable().addSelectionListener(mSelectionAdapterForMainTable);
	}

	private void createMainTable() {
		// メインテーブル
		mMainTableViewer = new TableViewer(this, SWT.MULTI | SWT.FULL_SELECTION
				| SWT.BORDER);
		Table wMainTable = mMainTableViewer.getTable();

		wMainTable.setLayoutData(new MyGridData(GridData.BEGINNING, GridData.BEGINNING, true, true)
				.getMyGridData());

		// 線を表示する
		wMainTable.setLinesVisible(SystemData.showGridLine());
		// ヘッダを可視にする
		wMainTable.setHeaderVisible(true);

		for (AnnualHeaderItem wItem : mCTL.getAnnualHeaderItems()) {
			TableColumn wTableCol = new TableColumn(wMainTable, SWT.RIGHT);
			wTableCol.setText(wItem.getName());
			wTableCol.setWidth(mColumnWidth);
		}

		mMainTableViewer.setContentProvider(new SummaryTableContentProvider());
		mMainTableViewer.setInput((SummaryTableItem[][]) mCTL.getSummaryTableItems()
				.toArray(new SummaryTableItem[0][]));

		mMainTableViewer.setLabelProvider(new SummaryTableLabelProvider());
	}

	private void createRowHeaderTable() {
		// 年月列テーブル
		mRowHeaderTableViewer = new TableViewer(this, SWT.MULTI | SWT.BORDER | SWT.VIRTUAL);
		Table wRowHeaderTable = mRowHeaderTableViewer.getTable();
		wRowHeaderTable.setLinesVisible(SystemData.showGridLine());
		wRowHeaderTable.setHeaderVisible(true);

		MyGridData wRowHeaderGridData = new MyGridData(GridData.BEGINNING, GridData.BEGINNING,
				false, true);
		wRowHeaderGridData.getMyGridData().widthHint = mColumnWidth;
		wRowHeaderTable.setLayoutData(wRowHeaderGridData.getMyGridData());

		TableColumn wHeaderCol = new TableColumn(wRowHeaderTable, SWT.LEFT);
		wHeaderCol.setText("年月");
		wHeaderCol.setWidth(mColumnWidth);

		mRowHeaderTableViewer.setContentProvider(new HeaderTableContentProvider());
		mRowHeaderTableViewer.setInput(mCTL.getRowHeaderList());
		mRowHeaderTableViewer.setLabelProvider(new HeaderTableLabelProvider());
	}

	// void updateTable() {
	// removeListeners();
	// mRowHeaderTableViewer.setInput(mCTL.getRowHeaderList());
	// mRowHeaderTableViewer.refresh();
	// mMainTableViewer.setInput((SummaryTableItem[][])
	// mCTL.getSummaryTableItems()
	// .toArray(new SummaryTableItem[0][]));
	// mRowHeaderTableViewer.refresh();
	// addListeners();
	// }

	void recreateMainTable() {
		removeListeners();
		mRowHeaderTableViewer.setInput(mCTL.getRowHeaderList());
		mRowHeaderTableViewer.refresh();
		mMainTableViewer.getTable().dispose();
		createMainTable();
		addListeners();
		this.layout();
	}

	void copySelectedTextToClipboard() {
		TableItem[] wSelectedRowItems = mRowHeaderTableViewer.getTable().getSelection();
		TableItem[] wSelectedAnnualItems = mMainTableViewer.getTable().getSelection();
		if (wSelectedAnnualItems.length == 0)
			return;
		Clipboard wClipboard = new Clipboard(getDisplay());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < wSelectedRowItems.length; i++) {
			if (i > 0) {
				sb.append("\n");
			}
			sb.append(wSelectedRowItems[i].getText());
			for (int j = 0;; j++) {
				if ("".equals(wSelectedAnnualItems[i].getText(j)))
					break;
				sb.append("\t" + wSelectedAnnualItems[i].getText(j));
			}
		}
		wClipboard.setContents(new String[] { sb.toString() }, new Transfer[] { TextTransfer
				.getInstance() });

	}
}

class HeaderTableContentProvider implements IStructuredContentProvider {
	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {
		List<String> wRowHeaders = (List<String>) inputElement;
		return (String[]) wRowHeaders.toArray(new String[0]);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}

class HeaderTableLabelProvider implements ITableLabelProvider, ITableColorProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		String wRowHeader = (String) element;
		return wRowHeader;

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
		return null;
	}

	@Override
	public Color getForeground(Object arg0, int arg1) {
		return null;
	}

}

class SummaryTableContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object inputElement) {
		SummaryTableItem[][] wSummaryTableItems = (SummaryTableItem[][]) inputElement;
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
		SummaryTableItem[] wItem = (SummaryTableItem[]) element;
		if (wItem[columnIndex].getValue() == SystemData.getUndefinedInt()) {
			return "";
		} else {
			return SystemData.getFormatedFigures(wItem[columnIndex].getValue());
		}
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
		SummaryTableItem[] wItems = (SummaryTableItem[]) pElement;
		SummaryTableItem wItem = wItems[pColumnIndex];
		return wItem.getEntryColor();
	}

	@Override
	public Color getForeground(Object pElement, int pColumnIndex) {
		SummaryTableItem[] wItems = (SummaryTableItem[]) pElement;
		SummaryTableItem wItem = wItems[pColumnIndex];
		if (wItem.getValue() < 0)
			// 赤字
			return new Color(Display.getCurrent(), 255, 0, 0);

		return null;
	}

}
