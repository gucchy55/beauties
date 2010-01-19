package view.entry;

import java.text.DecimalFormat;
import java.util.Date;

import model.RecordTableItem;
import model.SystemData;
import model.action.DeleteRecord;
import model.action.OpenDialogModifyMove;
import model.action.OpenDialogModifyRecord;
import model.db.DbUtil;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import view.util.MyGridData;
import view.util.MyGridLayout;

public class CompositeRecordTable extends Composite {

	private Date mStartDate;
	private Date mEndDate;

	private RecordTableItem[] mRecordItemsUp;
	private RecordTableItem[] mRecordItemsBottom;

	private TableViewer mTableUp;
	private TableViewer mTableBottom;

	private Color mColor1 = new Color(Display.getCurrent(), 255, 255, 255);
	private Color mColor2 = new Color(Display.getCurrent(), 255, 255, 234);

	public CompositeRecordTable(Composite pParent) {
		super(pParent, SWT.NONE);
		this.mStartDate = SystemData.getStartDate();
		this.mEndDate = SystemData.getEndDate();

		this.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true,
				true).getMyGridData());

		SashForm wSashForm = new SashForm(this, SWT.VERTICAL);
		wSashForm.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		wSashForm.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL,
				true, true).getMyGridData());

		RecordTableItem[][] wRecordTableItemAll = DbUtil.getRecordTableItems(
				mStartDate, mEndDate, SystemData.getBookId());

		mRecordItemsUp = wRecordTableItemAll[0];
		mRecordItemsBottom = wRecordTableItemAll[1];

		mTableUp = setTableHeader(wSashForm, new MyGridData(GridData.FILL,
				GridData.FILL, true, true).getMyGridData());
		mTableUp = setRecordTableItem(mTableUp, mRecordItemsUp);
		mTableUp.getTable().setSelection(0);
		mTableUp.getTable().setFocus();

		Composite wBottomComp = new Composite(wSashForm, SWT.NONE);
		wBottomComp.setLayout(new MyGridLayout(1, false).getMyGridLayout());
		wBottomComp.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL,
				true, true).getMyGridData());
		wSashForm.setWeights(new int[] { 80, 20 });

		Label wLabel = new Label(wBottomComp, SWT.NONE);
		wLabel.setText("当月の収支予定");
		wLabel.setLayoutData(new MyGridData(GridData.FILL, GridData.CENTER,
				true, false).getMyGridData());

		GridData wGridData = new MyGridData(GridData.FILL, GridData.FILL, true,
				true).getMyGridData();
		// wGridData.heightHint = 100;
		mTableBottom = setTableHeader(wBottomComp, wGridData);

		if (mRecordItemsBottom.length > 0) {
			mTableBottom = setRecordTableItem(mTableBottom, mRecordItemsBottom);
		}

		setStripeToTable();

	}

	private TableViewer setTableHeader(Composite pComp, GridData pGridData) {
		// テーブルの作成
		TableViewer wTableViewer = new TableViewer(pComp, SWT.FULL_SELECTION
				| SWT.BORDER);
		Table wTable = wTableViewer.getTable();
		wTable.setLayoutData(pGridData);
		// 線を表示する
		wTable.setLinesVisible(true);
		// ヘッダを可視にする
		wTable.setHeaderVisible(true);

		// 列のヘッダの設定
		TableColumn wActIdCol = new TableColumn(wTable, SWT.LEFT);
		wActIdCol.setText("ActID");
		wActIdCol.setWidth(0);
		wActIdCol.setResizable(false);

		TableColumn wCategoryIdCol = new TableColumn(wTable, SWT.LEFT);
		wCategoryIdCol.setText("CategoryID");
		wCategoryIdCol.setWidth(0);
		wCategoryIdCol.setResizable(false);

		TableColumn wItemIdCol = new TableColumn(wTable, SWT.LEFT);
		wItemIdCol.setText("ItemID");
		wItemIdCol.setWidth(0);
		wItemIdCol.setResizable(false);

		TableColumn wDateCol = new TableColumn(wTable, SWT.CENTER);
		wDateCol.setText("日付");
		wDateCol.setWidth(62);

		TableColumn wItemNameCol = new TableColumn(wTable, SWT.LEFT);
		wItemNameCol.setText("項目");
		wItemNameCol.setWidth(70);

		TableColumn wIncomeCol = new TableColumn(wTable, SWT.RIGHT);
		wIncomeCol.setText("収入");
		wIncomeCol.setWidth(60);

		TableColumn wExpenseCol = new TableColumn(wTable, SWT.RIGHT);
		wExpenseCol.setText("支出");
		wExpenseCol.setWidth(60);

		TableColumn wBalanceCol = new TableColumn(wTable, SWT.RIGHT);
		wBalanceCol.setText("残高");
		wBalanceCol.setWidth(80);

		TableColumn wFreqCol = new TableColumn(wTable, SWT.RIGHT);
		wFreqCol.setText("残回数");
		wFreqCol.setWidth(50);

		TableColumn wNoteCol = new TableColumn(wTable, SWT.LEFT);
		wNoteCol.setText("備考");
		wNoteCol.setWidth(250);

		return wTableViewer;
	}

	private TableViewer setRecordTableItem(final TableViewer pTableViewer,
			final RecordTableItem[] pRecordTableItems) {

		final Table wTable = pTableViewer.getTable();
		pTableViewer.setContentProvider(new TableContentProvider());
		pTableViewer.setInput(pRecordTableItems);

		pTableViewer.setLabelProvider(new TableLabelProvider());

		pTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event
						.getSelection();
				RecordTableItem wRecord = (RecordTableItem) sel
						.getFirstElement();
				if (!wRecord.isBalanceRow()) {
					if (wRecord.isMoveItem()) {
						new OpenDialogModifyMove(getShell(), wRecord.getId())
								.run();
					} else {
						new OpenDialogModifyRecord(getShell(), wRecord.getId())
								.run();
					}
				}
			}
		});

		wTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				// Enterキーが押されたら変更ダイアログ
				if (e.character == SWT.CR) {
					int index = wTable.getSelectionIndex();
					TableItem wItem = wTable.getItem(index);
					if (!"".equals(wItem.getText(0))) {
						if (DbUtil.isMoveItem(Integer
								.parseInt(wItem.getText(2)))) {
							new OpenDialogModifyMove(getShell(), Integer
									.parseInt(wItem.getText(0))).run();
						} else {
							new OpenDialogModifyRecord(getShell(), Integer
									.parseInt(wItem.getText(0))).run();
						}
					}
				}

				// DELキーが押されたら削除（確認ダイアログ）
				if (e.character == SWT.DEL) {
					int index = wTable.getSelectionIndex();
					TableItem wItem = wTable.getItem(index);
					if (!"".equals(wItem.getText(0))) {
						new DeleteRecord(getShell(), Integer.parseInt(wItem
								.getText(0))).run();
					}
				}
			}
		});

		return pTableViewer;
	}

	public int getSelectedActId() {
		Table wTable = mTableUp.getTable();

		if (mTableBottom.getTable().getSelectionIndex() > 0) {
			wTable = mTableBottom.getTable();
		} else if (mTableUp.getTable().getSelectionIndex() < 0) {
			return SystemData.getUndefinedInt();
		}
		int index = wTable.getSelectionIndex();
		TableItem wItem = wTable.getItem(index);
		if (!"".equals(wItem.getText(0))) {
			return Integer.parseInt(wItem.getText(0));
		} else {
			return SystemData.getUndefinedInt();
		}

	}

	public void addFilter() {
		mTableUp.addFilter(new IdFilter());
		mTableBottom.addFilter(new IdFilter());
	}

	public void removeFilter() {
		for (ViewerFilter vf : mTableUp.getFilters()) {
			mTableUp.removeFilter(vf);
		}
		for (ViewerFilter vf : mTableBottom.getFilters()) {
			mTableBottom.removeFilter(vf);
		}
	}

	public void setStripeToTable() {
		for (Table wTable : new Table[] { mTableUp.getTable(),
				mTableBottom.getTable() }) {
			TableItem[] wItems = wTable.getItems();
			for (int i = 0; i < wItems.length; i++) {
				if (i % 2 == 0) {
					wItems[i].setBackground(mColor1);
				} else {
					wItems[i].setBackground(mColor2);
				}
			}
		}
	}

}

class TableContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object inputElement) {
		RecordTableItem[] wRecordTableItems = (RecordTableItem[]) inputElement;
		return wRecordTableItems;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}

class TableLabelProvider implements ITableLabelProvider {
	private DecimalFormat mDecimalFormat = new DecimalFormat("###,###");

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		RecordTableItem wRecord = (RecordTableItem) element;
		switch (columnIndex) {
		case 0:
			if (wRecord.isBalanceRow()) {
				return "";
			} else {
				return Integer.toString(wRecord.getId());
			}
		case 1:
			return Integer.toString(wRecord.getCategoryId());
		case 2:
			return Integer.toString(wRecord.getItemId());
		case 3:
			return wRecord.getDateString();
		case 4:
			return wRecord.getItemName();
		case 5:
			if (wRecord.isBalanceRow() || wRecord.getIncome() == 0) {
				return "";
			} else {
				return mDecimalFormat.format(wRecord.getIncome());
			}
		case 6:
			if (wRecord.isBalanceRow() || wRecord.getExpense() == 0) {
				return "";
			} else {
				return mDecimalFormat.format(wRecord.getExpense());
			}
		case 7:
			return mDecimalFormat.format(wRecord.getBalance());
		case 8:
			if (wRecord.getFrequency() == 0 || wRecord.isBalanceRow()) {
				return "";
			} else {
				return Integer.toString(wRecord.getFrequency());
			}
		case 9:
			return wRecord.getNote();

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

}

class IdFilter extends ViewerFilter {
	public boolean select(Viewer pViewer, Object pParent, Object pElement) {
		RecordTableItem wRecord = (RecordTableItem) pElement;
		if (SystemData.getCategoryId() != SystemData.getUndefinedInt()) {
			return (wRecord.getCategoryId() == SystemData.getCategoryId());
		} else if (SystemData.getItemId() != SystemData.getUndefinedInt()) {
			return (wRecord.getItemId() == SystemData.getItemId());
		} else if (SystemData.isAllIncome()) {
			return (wRecord.isIncome());
		} else if (SystemData.isAllExpense()) {
			return (wRecord.isExpense());
		} else {
			return true;
		}
	}

}
