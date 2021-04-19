package beauties.record.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import beauties.common.lib.SystemData;
import beauties.common.lib.Util;
import beauties.common.view.MyGridData;
import beauties.record.DeleteRecord;
import beauties.record.OpenDialogModifyMove;
import beauties.record.OpenDialogModifyRecord;
import beauties.record.OpenDialogNewMove;
import beauties.record.OpenDialogNewRecord;
import beauties.record.RecordController;
import beauties.record.model.RecordTableItem;

class RecordTableViewer extends TableViewer {

	private RecordController mCTL;
	private Collection<RecordTableItem> mRecordTableItems;

	private TableColumn mBookCol;
	private TableColumn mDateCol;

	private KeyListener mKeyListener;
//	private IDoubleClickListener mDoubleClickListener;
	private MouseListener mDoubleClickListener;
	
	RecordTableViewer(Composite pComp, RecordController pCTL) {
		super(pComp, SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL);

		mCTL = pCTL;
		mKeyListener = getKeyListener();
		mDoubleClickListener = getDoubleClickListener();

		// テーブルの作成
		Table wTable = this.getTable();
		wTable.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true)
				.getMyGridData());
		// 線を表示する
		wTable.setLinesVisible(SystemData.showGridLine());
		// ヘッダを可視にする
		wTable.setHeaderVisible(true);

		// 列のヘッダの設定
		mBookCol = new TableColumn(wTable, SWT.LEFT);
		mBookCol.setText("帳簿");

		mDateCol = new TableColumn(wTable, SWT.CENTER);
		mDateCol.setText("日付");

		TableColumn wItemNameCol = new TableColumn(wTable, SWT.LEFT);
		wItemNameCol.setText("項目");
		wItemNameCol.setWidth(SystemData.getRecordWidthItem());

		TableColumn wIncomeCol = new TableColumn(wTable, SWT.RIGHT);
		wIncomeCol.setText("収入");
		wIncomeCol.setWidth(SystemData.getRecordWidthIncome());

		TableColumn wExpenseCol = new TableColumn(wTable, SWT.RIGHT);
		wExpenseCol.setText("支出");
		wExpenseCol.setWidth(SystemData.getRecordWidthExpense());

		TableColumn wBalanceCol = new TableColumn(wTable, SWT.RIGHT);
		wBalanceCol.setText("残高");
		wBalanceCol.setWidth(SystemData.getRecordWidthBalance());

		TableColumn wFreqCol = new TableColumn(wTable, SWT.RIGHT);
		wFreqCol.setText("残回数");
		wFreqCol.setWidth(SystemData.getRecordWidthFreq());

		TableColumn wNoteCol = new TableColumn(wTable, SWT.LEFT);
		wNoteCol.setText("備考");
		wNoteCol.setWidth(SystemData.getRecordWidthNote());

		addListeners();
	}

	void setRecordTableItem(Collection<RecordTableItem> pRecordTableItems) {

		mRecordTableItems = pRecordTableItems;

//		this.setContentProvider(new TableContentProvider());
		this.setContentProvider(ArrayContentProvider.getInstance());
		this.setInput(mRecordTableItems);

		this.setLabelProvider(new TableLabelProvider(mCTL));
		this.setInput(mRecordTableItems);
		updateColumnWidths();

	}

	private void addListeners() {
		this.getTable().addKeyListener(mKeyListener);
		this.getTable().addMouseListener(mDoubleClickListener);
//		addDoubleClickListener(mDoubleClickListener);
	}
	
//	void removeListeners() {
//		removeDoubleClickListener(mDoubleClickListener);
//		this.getTable().removeKeyListener(mKeyListener);
//	}

	private MouseListener getDoubleClickListener() {
		return new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent event) {
				RecordTableItem wRecord = mCTL.getSelectedRecordItem();
				if (wRecord == null || wRecord.isBalanceRow()) {
					return;
				}
				if (wRecord.isMoveItem()) {
					new OpenDialogModifyMove(mCTL).run();
				} else {
					new OpenDialogModifyRecord(mCTL).run();
				}
			}
		};
	}

	private KeyListener getKeyListener() {
		return new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				// 新規追加関連
				if (keyEventForNew(e))
					return;

				// 変更関連
				if (keyEventForModify(e))
					return;
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

		};
	}
	
	private boolean keyEventForNew(KeyEvent e) {
		if (e.stateMask == SWT.CTRL && e.keyCode == 'i'
			|| e.stateMask == SWT.COMMAND && e.keyCode == 'i') {
			new OpenDialogNewRecord(mCTL).run();
			return true;
		}
		if (e.stateMask == SWT.CTRL && e.keyCode == 'm'
			|| e.stateMask == SWT.COMMAND && e.keyCode == 'm') {
			new OpenDialogNewMove(mCTL).run();
			return true;
		}
		if (e.stateMask == SWT.CTRL && e.keyCode == 'f'
			|| e.stateMask == SWT.COMMAND && e.keyCode == 'f') {
			mCTL.openSearchDialog();
			return true;
		}
		
		return false;
	}
	
	private boolean keyEventForModify(KeyEvent e) {
		if (!mCTL.hasSelectedRecordTableItem())
			return false;
		// Enterキーが押されたら変更ダイアログ
//		if (e.character == SWT.CR) {
//			if (mCTL.getSelectedRecordItem().isMoveItem())
//				new OpenDialogModifyMove(mCTL).run();
//			else
//				new OpenDialogModifyRecord(mCTL).run();
//			return true;
//		}

		// DELキーが押されたら削除（確認ダイアログ）
		if (e.character == SWT.DEL) {
			new DeleteRecord(mCTL).run();
			return true;
		}
		
		return false;
	}

	boolean hasSelectedItem() {
		return this.getTable().getItemCount() != 0 && !this.getSelection().isEmpty();
	}

	void updateColumnWidths() {
		mBookCol.setWidth(mCTL.showBookColumn() ? SystemData.getRecordWidthBook() : 0);
		mBookCol.setResizable(mCTL.showBookColumn());
		mDateCol.setWidth(mCTL.showYear() ? SystemData.getRecordWidthDateYear() : SystemData.getRecordWidthDate());
		// for (TableColumn wColumn : this.getTable().getColumns()) {
		// if (!wColumn.getResizable())
		// continue;
		// wColumn.pack();
		// }
	}

	void updateTableItem(Collection<RecordTableItem> pRecordTableItems) {
		mRecordTableItems = pRecordTableItems;
		this.setInput(mRecordTableItems);
		this.refresh();
		updateColumnWidths();
	}
}

class TableLabelProvider implements ITableLabelProvider, ITableFontProvider {
	private static DateFormat mDateFormat = new SimpleDateFormat("MM/dd");
	private static DateFormat mDateFormatLong = new SimpleDateFormat("yyyy/MM/dd");
	private RecordController mCTL;

	public TableLabelProvider(RecordController pCTL) {
		mCTL = pCTL;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		RecordTableItem wRecord = (RecordTableItem) element;
		switch (columnIndex) {
		case 0:
			return wRecord.getBookName();
		case 1:
			if (mCTL.showYear())
				return mDateFormatLong.format(wRecord.getDate());
			else
				return mDateFormat.format(wRecord.getDate()) + "("
						+ Util.getDayOfTheWeekShort(wRecord.getDate()) + ")";
		case 2:
			return wRecord.getItem().getName();
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

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	private String getNumerical(int pValue) {
		if (pValue == 0)
			return "";
		return SystemData.getFormatedFigures(pValue);
	}

	@Override
	public Font getFont(Object arg0, int pColumnIndex) {
		if (pColumnIndex >= 2 && pColumnIndex <= 5) return JFaceResources.getFont(JFaceResources.TEXT_FONT);
		return null;
	}
}
