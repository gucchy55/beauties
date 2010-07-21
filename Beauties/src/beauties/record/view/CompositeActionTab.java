package beauties.record.view;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import beauties.model.db.DbUtil;
import beauties.record.DeleteRecord;
import beauties.record.OpenDialogModifyMove;
import beauties.record.OpenDialogModifyRecord;
import beauties.record.OpenDialogNewMove;
import beauties.record.OpenDialogNewRecord;
import beauties.record.RecordController;

import util.view.MyGridData;
import util.view.MyRowLayout;

class CompositeActionTab extends Composite {

	private Button mSearchButton;
	private RecordController mCtl;

	public CompositeActionTab(Composite pParent, RecordController pCtl) {
		super(pParent, SWT.NONE);
		mCtl = pCtl;
		
		this.setLayout(new MyRowLayout().getMyRowLayout());
		this.setLayoutData(new MyGridData(GridData.END, GridData.BEGINNING, false, false).getMyGridData());
		
		mSearchButton = new Button(this, SWT.TOGGLE);
		mSearchButton.setText("検索");
		mSearchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button wButton = (Button)e.getSource();
				if(wButton.getSelection()) 
					wButton.setSelection(mCtl.openSearchDialog());
				else {
					mCtl.setSearchResult(false);
					mCtl.updateTable();
				}
			}
		});

		Button wMoveButton = new Button(this, SWT.PUSH);
		wMoveButton.setText("移動");
		wMoveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new OpenDialogNewMove(mCtl).run();
			}
		});

		Button wAddButton = new Button(this, SWT.PUSH);
		wAddButton.setText("追加");
		wAddButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new OpenDialogNewRecord(mCtl).run();
			}
		});

		Button wModifyButton = new Button(this, SWT.PUSH);
		wModifyButton.setText("変更");
		wModifyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!mCtl.hasSelectedRecordTableItem())
					return;
				if (DbUtil.isMoveRecord(mCtl.getSelectedRecordItem().getId())) {
					new OpenDialogModifyMove(mCtl).run();
				} else {
					new OpenDialogModifyRecord(mCtl).run();
				}
			}
		});

		Button wDeleteButton = new Button(this, SWT.PUSH);
		wDeleteButton.setText("削除");
		wDeleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ((!mCtl.hasSelectedRecordTableItem()))
					return;
				new DeleteRecord(mCtl).run();
			}
		});
	}
	
	void updateForSearch() {
		mSearchButton.setSelection(true);
	}

}
