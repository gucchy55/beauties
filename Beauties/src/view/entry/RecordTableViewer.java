package view.entry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import model.RecordTableItem;
import model.SystemData;
import model.action.DeleteRecord;
import model.action.OpenDialogModifyMove;
import model.action.OpenDialogModifyRecord;
import model.action.OpenDialogNewMove;
import model.action.OpenDialogNewRecord;
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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import util.Util;
import view.util.MyGridData;

class RecordTableViewer extends TableViewer {

	private CompositeEntry mCompositeEntry;

	public RecordTableViewer(Composite pComp, CompositeEntry pCompositeEntry) {
		super(pComp, SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL);

		mCompositeEntry = pCompositeEntry;

		// テーブルの作成
		Table wTable = this.getTable();
		wTable.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData());
		// 線を表示する
		wTable.setLinesVisible(DbUtil.showGridLine());
		// ヘッダを可視にする
		wTable.setHeaderVisible(true);

		// 列のヘッダの設定
		TableColumn wActIdCol = new TableColumn(wTable, SWT.LEFT);
		wActIdCol.setText("帳簿");
		wActIdCol.setWidth(mCompositeEntry.showBookColumn() ? 60 : 0);
		wActIdCol.setResizable(mCompositeEntry.showBookColumn());

		TableColumn wDateCol = new TableColumn(wTable, SWT.CENTER);
		wDateCol.setText("日付");
		wDateCol.setWidth(mCompositeEntry.showYear() ? 80 : 62);

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
		this.setInput(pRecordTableItems);

		this.setLabelProvider(new TableLabelProvider(mCompositeEntry.showYear()));

		this.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				RecordTableItem wRecord = (RecordTableItem) sel.getFirstElement();
				if (wRecord.isBalanceRow())
					return;
				if (wRecord.isMoveItem()) {
					new OpenDialogModifyMove(mCompositeEntry).run();
				} else {
					new OpenDialogModifyRecord(mCompositeEntry).run();
				}
			}
		});

		wTable.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (!mCompositeEntry.hasSelectedRecordTableItem())
					return;

				// Enterキーが押されたら変更ダイアログ
				if (e.character == SWT.CR) {
					if (mCompositeEntry.getSelectedRecordItem().isMoveItem())
						new OpenDialogModifyMove(mCompositeEntry).run();
					else
						new OpenDialogModifyRecord(mCompositeEntry).run();
				}

				// DELキーが押されたら削除（確認ダイアログ）
				if (e.character == SWT.DEL)
					new DeleteRecord(mCompositeEntry).run();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.CTRL) {
					if (e.keyCode == 'i')
						new OpenDialogNewRecord(mCompositeEntry).run();
					if (e.keyCode == 'm')
						new OpenDialogNewMove(mCompositeEntry).run();
					if (e.keyCode == 'f')
						mCompositeEntry.openSearchDialog();
				}
			}

		});
	}

	boolean hasSelectedItem() {
		if (this.getTable().getItemCount() == 0 || this.getSelection().isEmpty())
			return false;
		return true;
	}
	
	void setForSearchResults() {
		
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
			if (wRecord.isBalanceRow() || wRecord.getIncome() == 0) {
				return "";
			} else {
				return SystemData.getFormatedFigures(wRecord.getIncome());
			}
		case 4:
			if (wRecord.isBalanceRow() || wRecord.getExpense() == 0) {
				return "";
			} else {
				return SystemData.getFormatedFigures(wRecord.getExpense());
			}
		case 5:
			return SystemData.getFormatedFigures(wRecord.getBalance());
		case 6:
			if (wRecord.getFrequency() == 0 || wRecord.isBalanceRow()) {
				return "";
			} else {
				return Integer.toString(wRecord.getFrequency());
			}
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

}

class IdFilter extends ViewerFilter {

	private CompositeEntry mCompositeEntry;

	public IdFilter(CompositeEntry pCompositeEntry) {
		mCompositeEntry = pCompositeEntry;
	}

	public boolean select(Viewer pViewer, Object pParent, Object pElement) {
		RecordTableItem wRecord = (RecordTableItem) pElement;
		if (mCompositeEntry.getCategoryId() != SystemData.getUndefinedInt()) {
			return (wRecord.getCategoryId() == mCompositeEntry.getCategoryId());
		} else if (mCompositeEntry.getItemId() != SystemData.getUndefinedInt()) {
			return (wRecord.getItemId() == mCompositeEntry.getItemId());
		} else if (mCompositeEntry.isAllIncome()) {
			return (wRecord.isIncome());
		} else if (mCompositeEntry.isAllExpense()) {
			return (wRecord.isExpense());
		} else {
			return true;
		}
	}

}
