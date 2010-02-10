package view.entry;

import java.text.DecimalFormat;

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
import org.eclipse.swt.widgets.TableItem;

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
	}
	
	public void setRecordTableItem(final RecordTableItem[] pRecordTableItems) {

		final Table wTable = this.getTable();
		this.setContentProvider(new TableContentProvider());
		this.setInput(pRecordTableItems);

		this.setLabelProvider(new TableLabelProvider());

		this.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				RecordTableItem wRecord = (RecordTableItem) sel.getFirstElement();
				if (!wRecord.isBalanceRow()) {
					if (wRecord.isMoveItem()) {
						new OpenDialogModifyMove(mCompositeEntry).run();
					} else {
						new OpenDialogModifyRecord(mCompositeEntry).run();
					}
				}
			}
		});

		wTable.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				// Enterキーが押されたら変更ダイアログ
				if (e.character == SWT.CR) {
					int index = wTable.getSelectionIndex();
					TableItem wItem = wTable.getItem(index);
					if (!"".equals(wItem.getText(0))) {
						if (DbUtil.isMoveItem(Integer.parseInt(wItem.getText(2)))) {
							new OpenDialogModifyMove(mCompositeEntry).run();
						} else {
							new OpenDialogModifyRecord(mCompositeEntry).run();
						}
					}
				}

				// DELキーが押されたら削除（確認ダイアログ）
				if (e.character == SWT.DEL) {
					int index = wTable.getSelectionIndex();
					TableItem wItem = wTable.getItem(index);
					if (!"".equals(wItem.getText(0))) {
						new DeleteRecord(Integer.parseInt(wItem.getText(0)), mCompositeEntry).run();
					}
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.stateMask == SWT.CTRL) {
					if (e.keyCode == 'i') {
						new OpenDialogNewRecord(mCompositeEntry).run();
					} 
					if (e.keyCode == 'm') {
						new OpenDialogNewMove(mCompositeEntry).run();
					}
				}
			}

		});
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
