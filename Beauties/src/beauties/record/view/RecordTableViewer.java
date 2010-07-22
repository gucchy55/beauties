package beauties.record.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import beauties.model.SystemData;
import beauties.model.db.DbUtil;
import beauties.record.DeleteRecord;
import beauties.record.OpenDialogModifyMove;
import beauties.record.OpenDialogModifyRecord;
import beauties.record.OpenDialogNewMove;
import beauties.record.OpenDialogNewRecord;
import beauties.record.RecordController;
import beauties.record.model.RecordTableItem;

import util.Util;
import util.view.MyGridData;

class RecordTableViewer extends TableViewer {

	private RecordController mCtl;
	private RecordTableItem[] mRecordTableItems;
	
	private TableColumn mBookCol;
	private TableColumn mDateCol;

	public RecordTableViewer(Composite pComp, RecordController pCtl) {
		super(pComp, SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL);

		mCtl = pCtl;

		// テーブルの作成
		Table wTable = this.getTable();
		wTable.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData());
		// 線を表示する
		wTable.setLinesVisible(DbUtil.showGridLine());
		// ヘッダを可視にする
		wTable.setHeaderVisible(true);

		// 列のヘッダの設定
		mBookCol = new TableColumn(wTable, SWT.LEFT);
		mBookCol.setText("帳簿");
		

		mDateCol = new TableColumn(wTable, SWT.CENTER);
		mDateCol.setText("日付");
		
		updateColumnWidths();

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
	}

	public void setRecordTableItem(RecordTableItem[] pRecordTableItems) {

		final Table wTable = this.getTable();
		this.setContentProvider(new TableContentProvider());
		mRecordTableItems = pRecordTableItems;
		this.setInput(mRecordTableItems);

		this.setLabelProvider(new TableLabelProvider(mCtl.showYear()));

		this.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				RecordTableItem wRecord = (RecordTableItem) sel.getFirstElement();
				if (wRecord.isBalanceRow())
					return;
				if (wRecord.isMoveItem()) {
					new OpenDialogModifyMove(mCtl).run();
				} else {
					new OpenDialogModifyRecord(mCtl).run();
				}
			}
		});

		wTable.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (!mCtl.hasSelectedRecordTableItem())
					return;

				// Enterキーが押されたら変更ダイアログ
				if (e.character == SWT.CR) {
					if (mCtl.getSelectedRecordItem().isMoveItem())
						new OpenDialogModifyMove(mCtl).run();
					else
						new OpenDialogModifyRecord(mCtl).run();
				}

				// DELキーが押されたら削除（確認ダイアログ）
				if (e.character == SWT.DEL)
					new DeleteRecord(mCtl).run();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.CTRL) {
					if (e.keyCode == 'i')
						new OpenDialogNewRecord(mCtl).run();
					if (e.keyCode == 'm')
						new OpenDialogNewMove(mCtl).run();
					if (e.keyCode == 'f')
						mCtl.openSearchDialog();
				}
			}

		});
	}

	boolean hasSelectedItem() {
		return this.getTable().getItemCount() != 0 && !this.getSelection().isEmpty();
	}
	
	void updateColumnWidths() {
		mBookCol.setWidth(mCtl.showBookColumn() ? 60 : 0);
		mBookCol.setResizable(mCtl.showBookColumn());
		mDateCol.setWidth(mCtl.showYear() ? 80 : 62);
	}
	
	void updateTableItem(RecordTableItem[] pRecordTableItems) {
//		this.remove(mRecordTableItems);
//		this.setContentProvider(new TableContentProvider());
		mRecordTableItems = pRecordTableItems;
		this.setContentProvider(new TableContentProvider());
		this.setInput(mRecordTableItems);
		this.setLabelProvider(new TableLabelProvider(mCtl.showYear()));
		this.setInput(mRecordTableItems);
		updateColumnWidths();
		this.refresh();
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
	private static DateFormat mDateFormat = new SimpleDateFormat("MM/dd");
	private static DateFormat mDateFormatLong = new SimpleDateFormat("yyyy/MM/dd");
	private boolean showYear;

	public TableLabelProvider(boolean showYear) {
		this.showYear = showYear;
	}
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		RecordTableItem wRecord = (RecordTableItem) element;
		switch (columnIndex) {
		case 0:
			return wRecord.getBookName();
		case 1:
			if (this.showYear)
				return mDateFormatLong.format(wRecord.getDate());
			else
				return mDateFormat.format(wRecord.getDate()) + "(" + Util.getDayOfTheWeekShort(wRecord.getDate()) + ")";
		case 2:
			return wRecord.getItemName();
		case 3:
			return getNumerical(wRecord.getIncome());
		case 4:
			return getNumerical(wRecord.getExpense());
		case 5:
			return SystemData.getFormatedFigures(wRecord.getBalance());
		case 6:
			return getNumerical(wRecord.getFrequency());
		case 7:
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
	
	private String getNumerical(int pValue) {
		if (pValue == 0)
			return "";
		return SystemData.getFormatedFigures(pValue);
	}

}

//class IdFilter extends ViewerFilter {
//
//	private CompositeEntry mCompositeEntry;
//
//	public IdFilter(CompositeEntry pCompositeEntry) {
//		mCompositeEntry = pCompositeEntry;
//	}
//
//	public boolean select(Viewer pViewer, Object pParent, Object pElement) {
//		RecordTableItem wRecord = (RecordTableItem) pElement;
//		if (mCompositeEntry.getCategoryId() != SystemData.getUndefinedInt()) {
//			return (wRecord.getCategoryId() == mCompositeEntry.getCategoryId());
//		} else if (mCompositeEntry.getItemId() != SystemData.getUndefinedInt()) {
//			return (wRecord.getItemId() == mCompositeEntry.getItemId());
//		} else if (mCompositeEntry.isAllIncome()) {
//			return (wRecord.isIncome());
//		} else if (mCompositeEntry.isAllExpense()) {
//			return (wRecord.isExpense());
//		} else {
//			return true;
//		}
//	}
//
//}
