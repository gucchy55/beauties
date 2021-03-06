package beauties.annual.view;

import java.util.EnumMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import beauties.annual.AnnualController;
import beauties.annual.model.AnnualViewType;
import beauties.common.lib.SystemData;
import beauties.common.view.MyGridData;
import beauties.common.view.MyRowLayout;


class CompositeAnnualActionTab extends Composite {

	private AnnualController mCTL;
	private EnumMap<AnnualViewType, Button> mAnnualViewTypeMap;
	private Button mFiscalButton;

	CompositeAnnualActionTab(AnnualController pCTL) {
		super(pCTL.getComposite(), SWT.NONE);
		mCTL = pCTL;
		mAnnualViewTypeMap = new EnumMap<AnnualViewType, Button>(AnnualViewType.class);

		MyRowLayout wLayout = new MyRowLayout();
		wLayout.setSpacing(SystemData.getHorizontalSpacing());
		this.setLayout(wLayout.getLayout());
		this.setLayoutData(new MyGridData(GridData.END, GridData.BEGINNING, false, false)
				.getMyGridData());

		createCopyButton();

		createFiscalButton();

		createAnnualViewTypeButtons();

	}

	private void createCopyButton() {
		Button wCopyButton = new Button(this, SWT.PUSH);
		wCopyButton.setText("Copy");
		wCopyButton.addSelectionListener(new SelectionAdapter() {
			@Override
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
				mCTL.updateTable();
			}
		});
	}

	private void createAnnualViewTypeButtons() {
		Label wSpaceLabel = new Label(this, SWT.NONE);
		wSpaceLabel.setText("   ");

		for (final AnnualViewType wType : AnnualViewType.values()) {
			Button wCategoryButton = new Button(this, SWT.TOGGLE);
			wCategoryButton.setText(wType.toString());
			mAnnualViewTypeMap.put(wType, wCategoryButton);
			wCategoryButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (mCTL.getAnnualViewType() == wType) {
						((Button) e.getSource()).setSelection(true);
						return;
					}
					mAnnualViewTypeMap.get(mCTL.getAnnualViewType()).setSelection(false);
					mCTL.setAnnualViewType(wType);
					mCTL.updateTable();
				}
			});
		}
		mAnnualViewTypeMap.get(AnnualViewType.Category).setSelection(true);
	}

	void updateFiscalButton() {
		mFiscalButton.setSelection(mCTL.getFiscalPeriod());
	}
}
