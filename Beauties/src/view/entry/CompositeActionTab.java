package view.entry;

import model.action.DeleteRecord;
import model.action.OpenDialogModifyMove;
import model.action.OpenDialogModifyRecord;
import model.action.OpenDialogNewMove;
import model.action.OpenDialogNewRecord;
import model.db.DbUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import view.util.MyGridData;
import view.util.MyRowLayout;

class CompositeActionTab extends Composite {

	private CompositeEntry mCompositeEntry;
	private Button mSearchButton;

	public CompositeActionTab(Composite pParent) {
		super(pParent, SWT.NONE);
		mCompositeEntry = (CompositeEntry) pParent;

		this.setLayout(new MyRowLayout().getMyRowLayout());
		this.setLayoutData(new MyGridData(GridData.END, GridData.BEGINNING, false, false).getMyGridData());
		
		mSearchButton = new Button(this, SWT.TOGGLE);
		mSearchButton.setText("検索");
		mSearchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button wButton = (Button)e.getSource();
				if(wButton.getSelection()) 
					wButton.setSelection(mCompositeEntry.openSearchDialog());
				else {
					mCompositeEntry.setIsSearchResult(false);
					mCompositeEntry.updateView();
				}
			}
		});

		Button wMoveButton = new Button(this, SWT.PUSH);
		wMoveButton.setText("移動");
		wMoveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new OpenDialogNewMove(mCompositeEntry).run();
			}
		});

		Button wAddButton = new Button(this, SWT.PUSH);
		wAddButton.setText("追加");
		wAddButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new OpenDialogNewRecord(mCompositeEntry).run();
			}
		});

		Button wModifyButton = new Button(this, SWT.PUSH);
		wModifyButton.setText("変更");
		wModifyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!mCompositeEntry.hasSelectedRecordTableItem())
					return;
				if (DbUtil.isMoveRecord(mCompositeEntry.getSelectedRecordItem().getId())) {
					new OpenDialogModifyMove(mCompositeEntry).run();
				} else {
					new OpenDialogModifyRecord(mCompositeEntry).run();
				}
			}
		});

		Button wDeleteButton = new Button(this, SWT.PUSH);
		wDeleteButton.setText("削除");
		wDeleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ((!mCompositeEntry.hasSelectedRecordTableItem()))
					return;
				new DeleteRecord(mCompositeEntry).run();
			}
		});
	}
	
	void updateForSearch() {
		mSearchButton.setSelection(true);
	}

}
