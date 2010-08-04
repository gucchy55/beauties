package beauties.record.view;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import beauties.common.lib.SystemData;
import beauties.common.view.MyGridData;
import beauties.common.view.MyRowLayout;
import beauties.record.DeleteRecord;
import beauties.record.OpenDialogModifyMove;
import beauties.record.OpenDialogModifyRecord;
import beauties.record.OpenDialogNewMove;
import beauties.record.OpenDialogNewRecord;
import beauties.record.RecordController;


class CompositeActionTab extends Composite {

	private Button mSearchButton;
	private RecordController mCTL;

	public CompositeActionTab(RecordController pCTL) {
		super(pCTL.getComposite(), SWT.NONE);
		mCTL = pCTL;
		
		this.setLayout(new MyRowLayout().getMyRowLayout());
		this.setLayoutData(new MyGridData(GridData.END, GridData.BEGINNING, false, false).getMyGridData());
		
		createSearchButton();

		createMoveButton();

		createAddButton();

		createModifyButton();

		createDeleteButton();
	}

	private void createSearchButton() {
		mSearchButton = new Button(this, SWT.TOGGLE);
		mSearchButton.setText("検索");
		mSearchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button wButton = (Button)e.getSource();
				wButton.setBackground(wButton.getSelection() ? SystemData.getColorYellow() : null);
				if(wButton.getSelection()) 
					wButton.setSelection(mCTL.openSearchDialog());
				else {
					mCTL.setSearchResult(false);
					mCTL.updateTable();
				}
			}
		});
	}

	private void createMoveButton() {
		Button wMoveButton = new Button(this, SWT.PUSH);
		wMoveButton.setText("移動");
		wMoveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new OpenDialogNewMove(mCTL).run();
			}
		});
	}

	private void createAddButton() {
		Button wAddButton = new Button(this, SWT.PUSH);
		wAddButton.setText("追加");
		wAddButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new OpenDialogNewRecord(mCTL).run();
			}
		});
	}

	private void createModifyButton() {
		Button wModifyButton = new Button(this, SWT.PUSH);
		wModifyButton.setText("変更");
		wModifyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!mCTL.hasSelectedRecordTableItem())
					return;
				if (mCTL.getSelectedRecordItem().isMoveItem()) {
					new OpenDialogModifyMove(mCTL).run();
				} else {
					new OpenDialogModifyRecord(mCTL).run();
				}
			}
		});
	}

	private void createDeleteButton() {
		Button wDeleteButton = new Button(this, SWT.PUSH);
		wDeleteButton.setText("削除");
		wDeleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ((!mCTL.hasSelectedRecordTableItem()))
					return;
				new DeleteRecord(mCTL).run();
			}
		});
	}
	
	void updateForSearch() {
		mSearchButton.setSelection(true);
	}

}
