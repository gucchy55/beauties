package view.annual;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.AnnualHeaderItem;
import model.SummaryTableItem;
import model.SystemData;
import model.db.DbUtil;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
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

import util.Util;
import view.util.MyGridData;
import view.util.MyGridLayout;

public class CompositeAnnualTable extends Composite {

	private static final int mColumnWidth = 75;
	private AnnualHeaderItem[] mAnnualHeaderItems;
	private SummaryTableItem[][] mSummaryTableItems;

	public CompositeAnnualTable(Composite pParent) {
		super(pParent, SWT.NONE);
		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.FILL,
				GridData.FILL, true, true).getMyGridData();
		this.setLayoutData(wGridData);

		// 年月列テーブル
		TableViewer wRowHeader = new TableViewer(this, SWT.MULTI | SWT.BORDER
				| SWT.VIRTUAL);
		Table wRowHeaderTable = wRowHeader.getTable();
		wRowHeaderTable.setLinesVisible(true);
		wRowHeaderTable.setHeaderVisible(true);
		
		wRowHeaderTable.setLayoutData(new MyGridData(GridData.BEGINNING, GridData.BEGINNING,
				false, true).getMyGridData());

		TableColumn wHeaderCol = new TableColumn(wRowHeaderTable, SWT.LEFT);
		wHeaderCol.setText("年月");
		wHeaderCol.setWidth(mColumnWidth);

		Date[][] wDatePeriods = Util.getDatePairs(SystemData.getEndDate(), 13);
		Date[] wEndDates = new Date[wDatePeriods.length];
		for (int i = 0; i < wDatePeriods.length; i++) {
			wEndDates[i] = wDatePeriods[i][1];
		}
		SystemData.setStartDate(wDatePeriods[0][0]);
		SystemData.setEndDate(wDatePeriods[wDatePeriods.length - 1][1]);

		wRowHeader.setContentProvider(new HeaderTableContentProvider());
		wRowHeader.setInput(wEndDates);
		wRowHeader.setLabelProvider(new HeaderTableLabelProvider(getDisplay()));

		// メインテーブル
		TableViewer wMainTableViewer = new TableViewer(this, SWT.MULTI
				| SWT.BORDER | SWT.VIRTUAL);
		Table wMainTable = wMainTableViewer.getTable();

		wMainTable.setLayoutData(new MyGridData(GridData.FILL, GridData.BEGINNING,
				true, true).getMyGridData());
		
		// 線を表示する
		wMainTable.setLinesVisible(true);
		// ヘッダを可視にする
		wMainTable.setHeaderVisible(true);

		// 列のヘッダの設定
		mAnnualHeaderItems = DbUtil
				.getAnnualHeaderItem(SystemData.getBookId(), SystemData.getStartDate(), SystemData.getEndDate());
		for (AnnualHeaderItem wItem : mAnnualHeaderItems) {
			TableColumn wTableCol = new TableColumn(wMainTable, SWT.NONE);
			wTableCol.setText(wItem.getName());
			wTableCol.setWidth(mColumnWidth);
		}
		
		// 格納する値の取得
		mSummaryTableItems = new SummaryTableItem[wDatePeriods.length][mAnnualHeaderItems.length];
		for (int i=0; i < wDatePeriods.length; i++) {
			Date[] wDatePeriod = wDatePeriods[i];
			mSummaryTableItems[i] = DbUtil.getAllSummaryTableItems(SystemData.getBookId(), wDatePeriod[0], wDatePeriod[1], mAnnualHeaderItems);
		}
		
		wMainTableViewer.setContentProvider(new SummaryTableContentProvider());
		wMainTableViewer.setInput(mSummaryTableItems);

		wMainTableViewer.setLabelProvider(new SummaryTableLabelProvider(
				getDisplay()));

//		wMainTableViewer
//				.addSelectionChangedListener(new ISelectionChangedListener() {
//					@Override
//					public void selectionChanged(SelectionChangedEvent event) {
//						IStructuredSelection sel = (IStructuredSelection) event
//								.getSelection();
//						SummaryTableItem wTableItem = (SummaryTableItem) sel
//								.getFirstElement();
//						CompositeAnnualMain wParent = (CompositeAnnualMain) getParent();
//						if (wTableItem.isSpecialRow()) {
//							// CategoryId, ItemIdを初期化
//							SystemData.setCategoryId(SystemData
//									.getUndefinedInt());
//							SystemData.setItemId(SystemData.getUndefinedInt());
//							SystemData.setAllIncome(false);
//							SystemData.setAllExpense(false);
//						} else if (wTableItem.isAppearedSum()) {
//							SystemData.setAllIncome(wTableItem.isIncome());
//							SystemData.setAllExpense(!wTableItem.isIncome());
//							SystemData.setCategoryId(SystemData
//									.getUndefinedInt());
//							SystemData.setItemId(SystemData.getUndefinedInt());
//						} else if (wTableItem.getItemId() != SystemData
//								.getUndefinedInt()) {
//							SystemData.setCategoryId(SystemData
//									.getUndefinedInt());
//							SystemData.setItemId(wTableItem.getItemId());
//							SystemData.setAllIncome(false);
//							SystemData.setAllExpense(false);
//
//						} else if (wTableItem.getCategoryId() != SystemData
//								.getUndefinedInt()) {
//							SystemData
//									.setCategoryId(wTableItem.getCategoryId());
//							SystemData.setItemId(SystemData.getUndefinedInt());
//							SystemData.setAllIncome(false);
//							SystemData.setAllExpense(false);
//						}
//						// wParent.removeFiltersFromRecord();
//						// wParent.addFiltersToRecord();
//
//					}
//
//				});
	}

}

class HeaderTableContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object inputElement) {
		Date[] wDates = (Date[]) inputElement;
		return wDates;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}

class HeaderTableLabelProvider implements ITableLabelProvider,
		ITableColorProvider {
	private Display mDisplay;

	public HeaderTableLabelProvider(Display pDisplay) {
		this.mDisplay = pDisplay;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		DateFormat df = new SimpleDateFormat("yyyy年MM月");
		Date wDate = (Date) element;
		switch (columnIndex) {
		case 0:
			return df.format(wDate);
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
		// SummaryTableItem wItem = (SummaryTableItem) pElement;
		// if (wItem.isSpecialRow()) {
		// // 残高、営業収支等（赤）
		// return new Color(mDisplay, 255, 176, 176);
		// } else if (wItem.isAppearedSum()) {
		// // みかけ収支等（緑）
		// return new Color(mDisplay, 176, 255, 176);
		// } else if (wItem.getItemId() == SystemData.getUndefinedInt()) {
		// // カテゴリ（黄色）
		// return new Color(mDisplay, 255, 255, 176);
		// } else {
		// アイテム（グレー）
		return new Color(mDisplay, 238, 227, 251);
		// }
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

class SummaryTableLabelProvider implements ITableLabelProvider,
		ITableColorProvider {
	private DecimalFormat mDecimalFormat = new DecimalFormat("###,###");
//	private Display mDisplay;

	public SummaryTableLabelProvider(Display pDisplay) {
//		this.mDisplay = pDisplay;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		SummaryTableItem[] wItem = (SummaryTableItem[]) element;
		return mDecimalFormat.format(wItem[columnIndex].getValue());
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
//		SummaryTableItem[] wItem = (SummaryTableItem[]) pElement;
//		if (wItem.isSpecialRow()) {
//			// 残高、営業収支等（赤）
//			return new Color(mDisplay, 255, 176, 176);
//		} else if (wItem.isAppearedSum()) {
//			// みかけ収支等（緑）
//			return new Color(mDisplay, 176, 255, 176);
//		} else if (wItem.getItemId() == SystemData.getUndefinedInt()) {
//			// カテゴリ（黄色）
//			return new Color(mDisplay, 255, 255, 176);
//		} else {
//			// アイテム（グレー）
//			return new Color(mDisplay, 238, 227, 251);
//		}
	}

	@Override
	public Color getForeground(Object arg0, int arg1) {
		return null;
	}

}
