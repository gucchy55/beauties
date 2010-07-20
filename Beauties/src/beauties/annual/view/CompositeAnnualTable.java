package beauties.annual.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import beauties.annual.model.AnnualHeaderItem;
import beauties.annual.model.AnnualViewType;
import beauties.model.AnnualDateRange;
import beauties.model.DateRange;
import beauties.model.SystemData;
import beauties.model.db.DbUtil;
import beauties.record.model.SummaryTableItem;

import util.Util;
import util.view.MyGridData;
import util.view.MyGridLayout;

class CompositeAnnualTable extends Composite {

	private static final int mColumnWidth = 75;
	private AnnualHeaderItem[] mAnnualHeaderItems;
	private List<SummaryTableItem[]> mSummaryTableItems;

	private Table mRowHeaderTable;
	private Table mMainTable;

	private CompositeAnnualMain mCompositeAnnualMain;

	public CompositeAnnualTable(Composite pParent) {
		super(pParent, SWT.NONE);
		mCompositeAnnualMain = (CompositeAnnualMain) pParent;
		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.FILL, GridData.FILL, true, true)
				.getMyGridData();
		this.setLayoutData(wGridData);

		// 年月列テーブル
		TableViewer wRowHeader = new TableViewer(this, SWT.MULTI | SWT.BORDER | SWT.VIRTUAL);
		mRowHeaderTable = wRowHeader.getTable();
		mRowHeaderTable.setLinesVisible(DbUtil.showGridLine());
		mRowHeaderTable.setHeaderVisible(true);

		MyGridData wRowHeaderGridData = new MyGridData(GridData.BEGINNING, GridData.BEGINNING,
				false, true);
		wRowHeaderGridData.getMyGridData().widthHint = 60;
		mRowHeaderTable.setLayoutData(wRowHeaderGridData.getMyGridData());

		TableColumn wHeaderCol = new TableColumn(mRowHeaderTable, SWT.LEFT);
		wHeaderCol.setText("年月");
		wHeaderCol.setWidth(mColumnWidth);

		// Date[][] wDatePeriods;
		List<DateRange> wDateRangeList;

		if (mCompositeAnnualMain.getDateRange() == null) {
			wDateRangeList = Util.getDateRangeListByMonthCnt(Util.getMonthDateRange(new Date(),
					DbUtil.getCutOff()).getEndDate(), mCompositeAnnualMain.getMonthCount(), DbUtil
					.getCutOff());
			mCompositeAnnualMain.setDateRange(new DateRange(wDateRangeList.get(0).getStartDate(),
					wDateRangeList.get(wDateRangeList.size() - 1).getEndDate()));
			// mCompositeAnnualMain.setStartDate(wDatePeriods[0][0]);
			// mCompositeAnnualMain.setEndDate(wDatePeriods[wDatePeriods.length
			// - 1][1]);

		} else {
			wDateRangeList = Util.getMonthDateRangeListFromLongRange(mCompositeAnnualMain
					.getDateRange(), DbUtil.getCutOff());
		}

		if (!mCompositeAnnualMain.isAnnualPeriod()) {
			mCompositeAnnualMain.setMonthCount(wDateRangeList.size());
		}

		List<String> wRowHeaders = new ArrayList<String>();
		DateFormat df = new SimpleDateFormat("yyyy年MM月");

		// for (int i = 0; i < wDatePeriods.length; i++) {
		// Date wEndDate = wDatePeriods[i][1];
		// wRowHeaders.add(df.format(wEndDate));
		// }
		for (DateRange wDateRange : wDateRangeList)
			wRowHeaders.add(df.format(wDateRange.getEndDate()));

		AnnualDateRange wAnnualDateRange = new AnnualDateRange(wDateRangeList);
		// int wSummationIndex = Util.getSummationIndex(wDateRangeList,
		// DbUtil.getCutOff());
		if (wAnnualDateRange.hasSumIndex()) {
			wRowHeaders.add(wAnnualDateRange.getSumIndex(), "合計");
			wRowHeaders.add(wAnnualDateRange.getAveIndex(), "平均");
		}

		wRowHeader.setContentProvider(new HeaderTableContentProvider());
		wRowHeader.setInput(wRowHeaders);
		wRowHeader.setLabelProvider(new HeaderTableLabelProvider());

		// メインテーブル
		TableViewer wMainTableViewer = new TableViewer(this, SWT.MULTI | SWT.FULL_SELECTION
				| SWT.BORDER);
		mMainTable = wMainTableViewer.getTable();

		mMainTable.setLayoutData(new MyGridData(GridData.BEGINNING, GridData.BEGINNING, true, true)
				.getMyGridData());

		// 線を表示する
		mMainTable.setLinesVisible(DbUtil.showGridLine());
		// ヘッダを可視にする
		mMainTable.setHeaderVisible(true);

		// 格納する値の取得
		mSummaryTableItems = new ArrayList<SummaryTableItem[]>();
		if (mCompositeAnnualMain.getAnnualViewType() == AnnualViewType.Original) {
			mSummaryTableItems = DbUtil.getAnnualSummaryTableItemsOriginal(wAnnualDateRange);
		} else if (mCompositeAnnualMain.getAnnualViewType() == AnnualViewType.Category) {
			mSummaryTableItems = DbUtil.getAnnualSummaryTableItems(
					mCompositeAnnualMain.getBookId(), wAnnualDateRange, false);
		} else { // ITEM
			mSummaryTableItems = DbUtil.getAnnualSummaryTableItems(
					mCompositeAnnualMain.getBookId(), wAnnualDateRange, true);
		}

		// 列のヘッダの設定
		mAnnualHeaderItems = new AnnualHeaderItem[mSummaryTableItems.get(0).length];
		for (int i = 0; i < mSummaryTableItems.get(0).length; i++) {
			mAnnualHeaderItems[i] = new AnnualHeaderItem(mSummaryTableItems.get(0)[i].getName());
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
		wMainTableViewer.setInput((SummaryTableItem[][]) mSummaryTableItems
				.toArray(new SummaryTableItem[0][]));

		wMainTableViewer.setLabelProvider(new SummaryTableLabelProvider());

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

	protected void copySelectedTextToClipboard() {
		TableItem[] wSelectedRowItems = mRowHeaderTable.getSelection();
		TableItem[] wSelectedAnnualItems = mMainTable.getSelection();
		if (wSelectedAnnualItems.length == 0)
			return;
		Clipboard wClipboard = new Clipboard(getDisplay());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < wSelectedRowItems.length; i++) {
			if (i > 0) {
				sb.append("\n");
			}
			sb.append(wSelectedRowItems[i].getText());
			for (int j = 1;; j++) {
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
		if (columnIndex == 0 || wItem[columnIndex - 1].getValue() == SystemData.getUndefinedInt()) {
			return "";
		} else {
			return SystemData.getFormatedFigures(wItem[columnIndex - 1].getValue());
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
			// if (wItem.isAppearedIncomeExpense() || wItem.isAppearedSum()) {
			// // 黄色
			// return SystemData.getColorYellow();
			// }
			return wItem.getEntryColor();
			// return null;
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
