package view.annual;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.AnnualHeaderItem;
import model.AnnualViewType;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn; //import org.eclipse.swt.widgets.TableItem;

import util.Util;
import view.util.MyGridData;
import view.util.MyGridLayout;

public class CompositeAnnualTable extends Composite {

	private static final int mColumnWidth = 75;
	private AnnualHeaderItem[] mAnnualHeaderItems;
	private List<SummaryTableItem[]> mSummaryTableItems;

	private Table mRowHeaderTable;
	private Table mMainTable;

	// private Color mColor1 = new Color(Display.getCurrent(), 255, 255, 255);
	// private Color mColor2 = new Color(Display.getCurrent(), 255, 255, 234);

	public CompositeAnnualTable(Composite pParent) {
		super(pParent, SWT.NONE);
		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData();
		this.setLayoutData(wGridData);

		// 年月列テーブル
		TableViewer wRowHeader = new TableViewer(this, SWT.MULTI | SWT.BORDER | SWT.VIRTUAL);
		mRowHeaderTable = wRowHeader.getTable();
		mRowHeaderTable.setLinesVisible(true);
		mRowHeaderTable.setHeaderVisible(true);

		MyGridData wRowHeaderGridData = new MyGridData(GridData.BEGINNING, GridData.BEGINNING, false, true);
		wRowHeaderGridData.getMyGridData().widthHint = 60;
		mRowHeaderTable.setLayoutData(wRowHeaderGridData.getMyGridData());

		TableColumn wHeaderCol = new TableColumn(mRowHeaderTable, SWT.LEFT);
		wHeaderCol.setText("年月");
		wHeaderCol.setWidth(mColumnWidth);

		Date[][] wDatePeriods;

		if (SystemData.getEndDate() == null) {
			wDatePeriods = Util.getDatePairs(Util.getPeriod(new Date())[1], SystemData.getMonthCount());
			SystemData.setStartDate(wDatePeriods[0][0]);
			SystemData.setEndDate(wDatePeriods[wDatePeriods.length - 1][1]);

		} else {
			wDatePeriods = Util.getDatePairs(SystemData.getStartDate(), SystemData.getEndDate());
		}

		if (!SystemData.isAnnualPeriod()) {
			SystemData.setMonthCount(wDatePeriods.length);
		}

		List<String> wRowHeaders = new ArrayList<String>();
		DateFormat df = new SimpleDateFormat("yyyy年MM月");
		Date wDateNow = new Date();
		boolean wIsSummationAdded = false;

		// DateNowが最初の月以前なら合計、平均は表示しない
		if (wDatePeriods[0][1].after(wDateNow)) {
			wIsSummationAdded = true;
		}
		for (int i = 0; i < wDatePeriods.length; i++) {
			Date wEndDate = wDatePeriods[i][1];
			if (wDateNow.after(wDatePeriods[i][0]) && Util.getAdjusentDay(wEndDate, 1).after(wDateNow)
					&& !wIsSummationAdded) {
				wIsSummationAdded = true;
				wRowHeaders.add("合計");
				wRowHeaders.add("平均");
			}
			wRowHeaders.add(df.format(wEndDate));
		}
		if (!wIsSummationAdded) {
			wRowHeaders.add("合計");
			wRowHeaders.add("平均");
		}

		wRowHeader.setContentProvider(new HeaderTableContentProvider());
		wRowHeader.setInput(wRowHeaders);
		wRowHeader.setLabelProvider(new HeaderTableLabelProvider(getDisplay()));

		// メインテーブル
		TableViewer wMainTableViewer = new TableViewer(this, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		mMainTable = wMainTableViewer.getTable();

		mMainTable.setLayoutData(new MyGridData(GridData.BEGINNING, GridData.BEGINNING, true, true).getMyGridData());

		// 線を表示する
		mMainTable.setLinesVisible(true);
		// ヘッダを可視にする
		mMainTable.setHeaderVisible(true);

		// 格納する値の取得
		mSummaryTableItems = new ArrayList<SummaryTableItem[]>();
		if (SystemData.getAnnualViewType() == AnnualViewType.Original) {
			mSummaryTableItems = DbUtil.getAnnualSummaryTableItemsOriginal(wDatePeriods);
		} else if (SystemData.getAnnualViewType() == AnnualViewType.Category) {
			mSummaryTableItems = DbUtil.getAnnualSummaryTableItemsCategory(SystemData.getBookId(), wDatePeriods);
		} else { // ITEM
			mSummaryTableItems = DbUtil.getAnnualSummaryTableItems(SystemData.getBookId(), wDatePeriods);
		}

		// 列のヘッダの設定
		mAnnualHeaderItems = new AnnualHeaderItem[mSummaryTableItems.get(0).length];
		for (int i = 0; i < mSummaryTableItems.get(0).length; i++) {
			mAnnualHeaderItems[i] = new AnnualHeaderItem(mSummaryTableItems.get(0)[i].getItemName());
		}

		// Win32だとなぜか先頭列が右寄せにならないので、空白列を挿入
		TableColumn wTableCol = new TableColumn(mMainTable, SWT.RIGHT);
		wTableCol.setWidth(0);
		wTableCol.setResizable(false);

		for (AnnualHeaderItem wItem : mAnnualHeaderItems) {
			wTableCol = new TableColumn(mMainTable, SWT.RIGHT);
			wTableCol.setText(wItem.getName());
			wTableCol.setWidth(mColumnWidth);
		}

		wMainTableViewer.setContentProvider(new SummaryTableContentProvider());
		wMainTableViewer.setInput((SummaryTableItem[][]) mSummaryTableItems.toArray(new SummaryTableItem[0][]));

		wMainTableViewer.setLabelProvider(new SummaryTableLabelProvider());

		// for (TableViewer wTableViewer : new TableViewer[] { wRowHeader,
		// wMainTableViewer }) {
		// TableItem[] wItems = wTableViewer.getTable().getItems();
		// for (int i = 0; i < wItems.length; i++) {
		// if (i % 2 == 0) {
		// wItems[i].setBackground(mColor1);
		// } else {
		// wItems[i].setBackground(mColor2);
		// }
		// }
		//
		// }

		// 選択がシンクロするようリスナーを設定
		mRowHeaderTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				mMainTable.setSelection(mRowHeaderTable.getSelectionIndices());
			}
		});
		mMainTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				mRowHeaderTable.setSelection(mMainTable.getSelectionIndices());
			}
		});

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
	// private Display mDisplay;

	public HeaderTableLabelProvider(Display pDisplay) {
		// this.mDisplay = pDisplay;
	}

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
//		SummaryTableItem wItem = (SummaryTableItem) pElement;
//		if (wItem.isSpecial() && SystemData.getAnnualViewType() != AnnualViewType.Original) {
//			// 残高、営業収支等（赤）
//			return new Color(Display.getCurrent(), 255, 176, 176);
//		}
		// } else if (wItem.isAppearedSum()) {
		// // みかけ収支等（緑）
		// return new Color(mDisplay, 176, 255, 176);
		// } else if (wItem.getItemId() == SystemData.getUndefinedInt()) {
		// // カテゴリ（黄色）
		// return new Color(mDisplay, 255, 255, 176);
		// } else {
		// アイテム（グレー）
		// return new Color(mDisplay, 238, 227, 251);
		// }
		// return new Color(mDisplay, 200, 200, 255);
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
	private DecimalFormat mDecimalFormat = new DecimalFormat("###,###");

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		SummaryTableItem[] wItem = (SummaryTableItem[]) element;
		if (columnIndex == 0 || wItem[columnIndex - 1].getValue() == SystemData.getUndefinedInt()) {
			return "";
		} else {
			return mDecimalFormat.format(wItem[columnIndex - 1].getValue());
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
		if (pColumnIndex == 0) {
			return null;
		} else {
			SummaryTableItem[] wItems = (SummaryTableItem[]) pElement;
			SummaryTableItem wItem = wItems[pColumnIndex - 1];
			if (wItem.isSpecial() && SystemData.getAnnualViewType() != AnnualViewType.Original) {
				// 赤
				// return new Color(Display.getCurrent(), 255, 200, 200);
				// 黄色
				return new Color(Display.getCurrent(), 255, 255, 176);
			}
			// } else if (wItem.isAppearedIncomeExpense()) {
			// // みかけ収入、支出（緑）
			// return new Color(mDisplay, 200, 255, 200);
			// } else if (wItem.isSpecial()) {
			// // 残高、営業収支等（青）
			// return new Color(mDisplay, 200, 200, 255);
			// } else if (wItem.isCategory()) {
			// // カテゴリ（黄色）
			// return new Color(mDisplay, 255, 255, 176);
			// } else {
			// // アイテム（グレー）
			// return new Color(mDisplay, 238, 227, 251);
			// }
			return null;
		}
	}

	@Override
	public Color getForeground(Object pElement, int pColumnIndex) {
		if (pColumnIndex == 0) {
			return null;
		} else {
			SummaryTableItem[] wItems = (SummaryTableItem[]) pElement;
			SummaryTableItem wItem = wItems[pColumnIndex - 1];
			if (wItem.getValue() < 0) {
				// 赤字
				return new Color(Display.getCurrent(), 255, 0, 0);
			}

			return null;
		}
	}

}
