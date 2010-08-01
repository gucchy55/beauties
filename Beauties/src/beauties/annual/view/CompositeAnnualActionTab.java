package beauties.annual.view;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import beauties.annual.AnnualController;
import beauties.annual.model.AnnualViewType;

import util.view.MyGridData;
import util.view.MyRowLayout;

class CompositeAnnualActionTab extends Composite {

	private AnnualController mCTL;
	private Map<AnnualViewType, Button> mAnnualViewTypeMap;
	private Button mFiscalButton;

	CompositeAnnualActionTab(Composite pParent, AnnualController pCTL) {
		super(pParent, SWT.NONE);
		mCTL = pCTL;
		mAnnualViewTypeMap = new HashMap<AnnualViewType, Button>();
		
		this.setLayout(new MyRowLayout().getMyRowLayout());
		this.setLayoutData(new MyGridData(GridData.END, GridData.BEGINNING, false, false)
				.getMyGridData());

		createCopyButton();

		createFiscalButton();
	
		createCategoryButton();

		createItemButton();

		createOriginalButton();
	}

	private void createCopyButton() {
		Button wCopyButton = new Button(this, SWT.PUSH);
		wCopyButton.setText("Copy");
		wCopyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				mCTL.copyToClipboard();
			}
		});
	}

	private void createFiscalButton() {
		mFiscalButton = new Button(this, SWT.TOGGLE);
		mFiscalButton.setText("年度表示");
		mFiscalButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mCTL.setFiscalPeriod(((Button) e.getSource()).getSelection());
				mCTL.recreateMainTable();
			}
		});
	}

	private void createCategoryButton() {
		Label wSpaceLabel = new Label(this, SWT.NONE);
		wSpaceLabel.setText("   ");

		Button wCategoryButton = new Button(this, SWT.TOGGLE);
		wCategoryButton.setText(" 分類別 ");
		wCategoryButton.setSelection(true);
		wCategoryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (mCTL.getAnnualViewType() == AnnualViewType.Category) {
					((Button) e.getSource()).setSelection(true);
					return;
				}
				mAnnualViewTypeMap.get(mCTL.getAnnualViewType()).setSelection(false);
				mCTL.setAnnualViewType(AnnualViewType.Category);
				mCTL.recreateMainTable();
			}
		});
		mAnnualViewTypeMap.put(AnnualViewType.Category, wCategoryButton);
	}

	private void createItemButton() {
		Button wItemButton = new Button(this, SWT.TOGGLE);
		wItemButton.setText(" 項目別 ");
		wItemButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (mCTL.getAnnualViewType() == AnnualViewType.Item) {
					((Button) e.getSource()).setSelection(true);
					return;
				}
				mAnnualViewTypeMap.get(mCTL.getAnnualViewType()).setSelection(false);
				mCTL.setAnnualViewType(AnnualViewType.Item);
				mCTL.recreateMainTable();
			}
		});
		mAnnualViewTypeMap.put(AnnualViewType.Item, wItemButton);
	}

	private void createOriginalButton() {
		Button wOriginalButton = new Button(this, SWT.TOGGLE);
		wOriginalButton.setText("特殊収支");
		wOriginalButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (mCTL.getAnnualViewType() == AnnualViewType.Original) {
					((Button) e.getSource()).setSelection(true);
				}
				mAnnualViewTypeMap.get(mCTL.getAnnualViewType()).setSelection(false);
				mCTL.setAnnualViewType(AnnualViewType.Original);
				mCTL.recreateMainTable();
			}
		});
		mAnnualViewTypeMap.put(AnnualViewType.Original, wOriginalButton);
	}
	
	void updateFiscalButton() {
		mFiscalButton.setSelection(mCTL.getFiscalPeriod());
	}
}
