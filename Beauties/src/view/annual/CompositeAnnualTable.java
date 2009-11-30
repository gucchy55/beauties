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
	private List<SummaryTableItem[]> mSummaryTableItems;

	public CompositeAnnualTable(Composite pParent) {
		super(pParent, SWT.NONE);
		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.FILL, GridData.FILL, true,
				true).getMyGridData();
		this.setLayoutData(wGridData);

		// 年月列テーブル
		TableViewer wRowHeader = new TableViewer(this, SWT.MULTI | SWT.BORDER
				| SWT.VIRTUAL);
		Table wRowHeaderTable = wRowHeader.getTable();
		wRowHeaderTable.setLinesVisible(true);
		wRowHeaderTable.setHeaderVisible(true);

		MyGridData wRowHeaderGridData = new MyGridData(GridData.BEGINNING,
				GridData.BEGINNING, false, true);
		wRowHeaderGridData.getMyGridData().widthHint = 60;
		wRowHeaderTable.setLayoutData(wRowHeaderGridData.getMyGridData());

		TableColumn wHeaderCol = new TableColumn(wRowHeaderTable, SWT.LEFT);
		wHeaderCol.setText("年月");
		wHeaderCol.setWidth(mColumnWidth);

		Date[][] wDatePeriods;

		if (SystemData.getEndDate() == null) {
			wDatePeriods = Util.getDatePairs(Util.getPeriod(new Date())[1],
					SystemData.getMonthCount());
			SystemData.setStartDate(wDatePeriods[0][0]);
			SystemData.setEndDate(wDatePeriods[wDatePeriods.length - 1][1]);
			
		} else {
			wDatePeriods = Util.getDatePairs(SystemData.getStartDate(),
					SystemData.getEndDate());
		}
		
		if (!SystemData.isAnnualPeriod()) {
			SystemData.setMonthCount(wDatePeriods.length);
		}

		DateFormat dftmp = new SimpleDateFormat("yyyy/MM/dd");
		System.out.println(SystemData.getMonthCount() + ": " + dftmp.format(SystemData.getStartDate()) + " - " + dftmp.format(SystemData.getEndDate()));

		List<String> wRowHeaders = new ArrayList<String>();
		DateFormat df = new SimpleDateFormat("yyyy年MM月");
		Date wDateNow = new Date();
		boolean wIsSummationAdded = false;
		int wSummationRowIndex = SystemData.getUndefinedInt();

		// DateNowが最初の月以前なら合計、平均は表示しない
		if (wDatePeriods[0][1].after(wDateNow)) {
			wIsSummationAdded = true;
		}
		for (int i = 0; i < wDatePeriods.length; i++) {
			Date wEndDate = wDatePeriods[i][1];
			if (wDateNow.after(wDatePeriods[i][0])
					&& Util.getAdjusentDay(wEndDate, 1).after(wDateNow)
					&& !wIsSummationAdded) {
				wIsSummationAdded = true;
				wSummationRowIndex = i;
				wRowHeaders.add("合計");
				wRowHeaders.add("平均");
			}
			wRowHeaders.add(df.format(wEndDate));
		}
		if (!wIsSummationAdded) {
			wRowHeaders.add("合計");
			wSummationRowIndex = wRowHeaders.size() - 1;
			wRowHeaders.add("平均");
		}

		wRowHeader.setContentProvider(new HeaderTableContentProvider());
		wRowHeader.setInput(wRowHeaders);
		wRowHeader.setLabelProvider(new HeaderTableLabelProvider(getDisplay()));

		// メインテーブル
		TableViewer wMainTableViewer = new TableViewer(this, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL);
		Table wMainTable = wMainTableViewer.getTable();

		wMainTable.setLayoutData(new MyGridData(GridData.BEGINNING,
				GridData.BEGINNING, true, true).getMyGridData());

		// 線を表示する
		wMainTable.setLinesVisible(true);
		// ヘッダを可視にする
		wMainTable.setHeaderVisible(true);

		// 列のヘッダの設定
		if (SystemData.getAnnualViewType() == AnnualViewType.Category) {
			mAnnualHeaderItems = DbUtil
					.getAnnualHeaderItem(SystemData.getBookId(), SystemData
							.getStartDate(), SystemData.getEndDate(), true, false);
		} else {
			mAnnualHeaderItems = DbUtil
			.getAnnualHeaderItem(SystemData.getBookId(), SystemData
					.getStartDate(), SystemData.getEndDate(), false, true);
		}

		// Win32だとなぜか先頭列が右寄せにならないので、空白列を挿入
		TableColumn wTableCol = new TableColumn(wMainTable, SWT.RIGHT);
		wTableCol.setWidth(0);
		wTableCol.setResizable(false);

		for (AnnualHeaderItem wItem : mAnnualHeaderItems) {
			wTableCol = new TableColumn(wMainTable, SWT.RIGHT);
			wTableCol.setText(wItem.getName());
			wTableCol.setWidth(mColumnWidth);
		}

		// 格納する値の取得
		mSummaryTableItems = new ArrayList<SummaryTableItem[]>();
		wIsSummationAdded = false;
		for (int i = 0; i < wDatePeriods.length; i++) {
			Date[] wDatePeriod = wDatePeriods[i];
			if (i == wSummationRowIndex) {
				wIsSummationAdded = true;
				// 合計、平均の作成
				SummaryTableItem[] wSummationItems = DbUtil
						.getAllSummaryTableItems(SystemData.getBookId(),
								wDatePeriods[0][0], wDatePeriods[i - 1][1],
								mAnnualHeaderItems);
				mSummaryTableItems.add(wSummationItems);

				// 平均
				SummaryTableItem[] wAverageItems = DbUtil
						.getAllSummaryTableItems(SystemData.getBookId(),
								wDatePeriods[0][0], wDatePeriods[i - 1][1],
								mAnnualHeaderItems);
				for (SummaryTableItem wAveItem : wAverageItems) {
					wAveItem.setValue(wAveItem.getValue() / i);
				}
				mSummaryTableItems.add(wAverageItems);

			}
			mSummaryTableItems.add(DbUtil.getAllSummaryTableItems(SystemData
					.getBookId(), wDatePeriod[0], wDatePeriod[1],
					mAnnualHeaderItems));
		}

		wMainTableViewer.setContentProvider(new SummaryTableContentProvider());
		wMainTableViewer.setInput((SummaryTableItem[][]) mSummaryTableItems
				.toArray(new SummaryTableItem[0][]));

		wMainTableViewer.setLabelProvider(new SummaryTableLabelProvider(
				getDisplay()));

		// wMainTableViewer
		// .addSelectionChangedListener(new ISelectionChangedListener() {
		// @Override
		// public void selectionChanged(SelectionChangedEvent event) {
		// IStructuredSelection sel = (IStructuredSelection) event
		// .getSelection();
		// SummaryTableItem wTableItem = (SummaryTableItem) sel
		// .getFirstElement();
		// CompositeAnnualMain wParent = (CompositeAnnualMain) getParent();
		// if (wTableItem.isSpecialRow()) {
		// // CategoryId, ItemIdを初期化
		// SystemData.setCategoryId(SystemData
		// .getUndefinedInt());
		// SystemData.setItemId(SystemData.getUndefinedInt());
		// SystemData.setAllIncome(false);
		// SystemData.setAllExpense(false);
		// } else if (wTableItem.isAppearedSum()) {
		// SystemData.setAllIncome(wTableItem.isIncome());
		// SystemData.setAllExpense(!wTableItem.isIncome());
		// SystemData.setCategoryId(SystemData
		// .getUndefinedInt());
		// SystemData.setItemId(SystemData.getUndefinedInt());
		// } else if (wTableItem.getItemId() != SystemData
		// .getUndefinedInt()) {
		// SystemData.setCategoryId(SystemData
		// .getUndefinedInt());
		// SystemData.setItemId(wTableItem.getItemId());
		// SystemData.setAllIncome(false);
		// SystemData.setAllExpense(false);
		//
		// } else if (wTableItem.getCategoryId() != SystemData
		// .getUndefinedInt()) {
		// SystemData
		// .setCategoryId(wTableItem.getCategoryId());
		// SystemData.setItemId(SystemData.getUndefinedInt());
		// SystemData.setAllIncome(false);
		// SystemData.setAllExpense(false);
		// }
		// // wParent.removeFiltersFromRecord();
		// // wParent.addFiltersToRecord();
		//
		// }
		//
		// });
	}

	public AnnualHeaderItem[] getmAnnualHeaderItems() {
		return mAnnualHeaderItems;
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
	private Display mDisplay;

	public SummaryTableLabelProvider(Display pDisplay) {
		this.mDisplay = pDisplay;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == 0) {
			return "";
		} else {
			SummaryTableItem[] wItem = (SummaryTableItem[]) element;
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
			if (wItem.isAppearedSum()) {
				// みかけ収支（赤）
				return new Color(mDisplay, 255, 200, 200);
			} else if (wItem.isAppearedIncomeExpense()) {
				// みかけ収入、支出（緑）
				return new Color(mDisplay, 200, 255, 200);
			} else if (wItem.isSpecial()) {
				// 残高、営業収支等（青）
				return new Color(mDisplay, 200, 200, 255);
			} else if (wItem.isCategory()) {
				// カテゴリ（黄色）
				return new Color(mDisplay, 255, 255, 176);
			} else {
				// アイテム（グレー）
				return new Color(mDisplay, 238, 227, 251);
			}
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
				return new Color(mDisplay, 255, 0, 0);
			}

			return null;
		}
	}

}
