package view.entry;

import model.SystemData;
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

	public CompositeActionTab(Composite pParent) {
		super(pParent, SWT.NONE);
		mCompositeEntry = (CompositeEntry)pParent;
		
		this.setLayout(new MyRowLayout().getMyRowLayout());
		this.setLayoutData(new MyGridData(GridData.END, GridData.BEGINNING,
				false, false).getMyGridData());

		Button wMoveButton = new Button(this, SWT.NONE);
		wMoveButton.setText("移動");
		wMoveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new OpenDialogNewMove(mCompositeEntry).run();
			}
		});

		Button wAddButton = new Button(this, SWT.NONE);
		wAddButton.setText("追加");
		wAddButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new OpenDialogNewRecord(mCompositeEntry).run();
			}
		});

		Button wModifyButton = new Button(this, SWT.NONE);
		wModifyButton.setText("変更");
		wModifyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int wSelectedActId = mCompositeEntry.getSelectedActId();
				if (wSelectedActId != SystemData.getUndefinedInt()) {
					if (DbUtil.isMoveRecord(wSelectedActId)) {
						new OpenDialogModifyMove(mCompositeEntry).run();
					} else {
						new OpenDialogModifyRecord(mCompositeEntry).run();
					}
				}
			}
		});

		Button wDeleteButton = new Button(this, SWT.NONE);
		wDeleteButton.setText("削除");
		wDeleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CompositeEntry wCompositeEntry = (CompositeEntry)getParent();
				int wSelectedActId = wCompositeEntry.getSelectedActId();
				if (wSelectedActId != SystemData.getUndefinedInt()) {
					new DeleteRecord(wSelectedActId, mCompositeEntry).run();
				}
			}
		});
	}
}
