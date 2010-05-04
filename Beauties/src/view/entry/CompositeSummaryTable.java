package view.entry;

import model.SummaryTableItem;
import model.SystemData;
import model.db.DbUtil;

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

import view.util.MyGridData;
import view.util.MyGridLayout;

class CompositeSummaryTable extends Composite {

	private static final int mRightWidthHint = 200;

	private CompositeEntry mCompositeEntry;

	public CompositeSummaryTable(Composite pParent) {
		super(pParent, SWT.NONE);

		mCompositeEntry = (CompositeEntry) pParent;

		this.setLayout(new MyGridLayout(1, false).getMyGridLayout());

		GridData wGridData = new MyGridData(GridData.BEGINNING, GridData.FILL, false, true).getMyGridData();
		wGridData.widthHint = mRightWidthHint;
		this.setLayoutData(wGridData);

		TableViewer wTableViewer = new TableViewer(this, SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL);
		Table wTable = wTableViewer.getTable();

		wTable.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData());

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

		SummaryTableItem[] wSummaryTableItems = DbUtil.getSummaryTableItems(mCompositeEntry.getBookId(),
				mCompositeEntry.getStartDate(), mCompositeEntry.getEndDate());

		wTableViewer.setContentProvider(new SummaryTableContentProvider());
		wTableViewer.setInput(wSummaryTableItems);

		wTableViewer.setLabelProvider(new SummaryTableLabelProvider());

		wTableViewer.getTable().setSelection(0);

		wTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				SummaryTableItem wTableItem = (SummaryTableItem) sel.getFirstElement();
				CompositeEntry wParent = (CompositeEntry) getParent();
				if (wTableItem.isSpecial() && !wTableItem.isAppearedIncomeExpense()) {
					// CategoryId, ItemIdを初期化
					mCompositeEntry.setCategoryId(SystemData.getUndefinedInt());
					mCompositeEntry.setItemId(SystemData.getUndefinedInt());
					mCompositeEntry.setAllIncome(false);
					mCompositeEntry.setAllExpense(false);
				} else if (wTableItem.isAppearedIncomeExpense()) {
					mCompositeEntry.setAllIncome(wTableItem.isIncome());
					mCompositeEntry.setAllExpense(!wTableItem.isIncome());
					mCompositeEntry.setCategoryId(SystemData.getUndefinedInt());
					mCompositeEntry.setItemId(SystemData.getUndefinedInt());
				} else if (wTableItem.getItemId() != SystemData.getUndefinedInt()) {
					mCompositeEntry.setCategoryId(SystemData.getUndefinedInt());
					mCompositeEntry.setItemId(wTableItem.getItemId());
					mCompositeEntry.setAllIncome(false);
					mCompositeEntry.setAllExpense(false);

				} else if (wTableItem.getCategoryId() != SystemData.getUndefinedInt()) {
					mCompositeEntry.setCategoryId(wTableItem.getCategoryId());
					mCompositeEntry.setItemId(SystemData.getUndefinedInt());
					mCompositeEntry.setAllIncome(false);
					mCompositeEntry.setAllExpense(false);
				}
				wParent.removeFiltersFromRecord();
				wParent.addFiltersToRecord();
				// wParent.setStripToTable();

			}

		});
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
			if (wItem.getItemId() == SystemData.getUndefinedInt()) {
				return wItem.getItemName();
			} else {
				return ("  " + wItem.getItemName());
			}
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
		SummaryTableItem wItem = (SummaryTableItem) pElement;
		if (wItem.isAppearedSum()) {
			// みかけ収支（赤）
			return SystemData.getColorRed();
		} else if (wItem.isAppearedIncomeExpense()) {
			// みかけ収入、支出（緑）
			return SystemData.getColorGreen();
		} else if (wItem.isSpecial()) {
			// 残高、営業収支等（青）
			return SystemData.getColorBlue();
		} else if (wItem.isCategory()) {
			// カテゴリ（黄色）
			return SystemData.getColorYellow();
		} else {
			// アイテム（グレー）
			return SystemData.getColorGray();
		}
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
