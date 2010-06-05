package view.annual;

import model.AnnualViewType; //import model.SystemData;
import model.action.UpdateAnnual;
import model.db.DbUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import util.Util;
import view.util.MyGridData;
import view.util.MyRowLayout;

class CompositeAnnualActionTab extends Composite {

	private CompositeAnnualMain mCompositeAnnualMain;

	public CompositeAnnualActionTab(Composite pParent) {
		super(pParent, SWT.NONE);
		mCompositeAnnualMain = (CompositeAnnualMain) pParent;

		this.setLayout(new MyRowLayout().getMyRowLayout());
		this.setLayoutData(new MyGridData(GridData.END, GridData.BEGINNING, false, false).getMyGridData());

		
		Button wCopyButton = new Button(this, SWT.NULL);
		wCopyButton.setText("Copy");
		wCopyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				mCompositeAnnualMain.copyToClipboard();
			}
		});
		
		Button wAnnualPeriodButton = new Button(this, SWT.TOGGLE);
		wAnnualPeriodButton.setText("年度表示");
		if (mCompositeAnnualMain.isAnnualPeriod()) {
			wAnnualPeriodButton.setSelection(true);
			wAnnualPeriodButton.setEnabled(false);
		} else {
			wAnnualPeriodButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					mCompositeAnnualMain.setAnnualPeriod(true);
					mCompositeAnnualMain.setMonthCount(12);
					mCompositeAnnualMain.setDateRange(Util.getFiscalDateRange(DbUtil.getCutOff(), DbUtil.getFisCalMonth()));
//					DateRange wDateRange = Util.getFiscalPeriod(DbUtil.getCutOff());
//					mCompositeAnnualMain.setStartDate(wDateRange.getStartDate());
//					mCompositeAnnualMain.setEndDate(wDateRange.getEndDate());
					new UpdateAnnual(mCompositeAnnualMain).run();
				}
			});
		}

		Label wSpaceLabel = new Label(this, SWT.NONE);
		wSpaceLabel.setText("   ");

		Button wCategoryButton = new Button(this, SWT.TOGGLE);
		wCategoryButton.setText(" 分類別 ");
		if (mCompositeAnnualMain.getAnnualViewType() == AnnualViewType.Category) {
			wCategoryButton.setSelection(true);
			wCategoryButton.setEnabled(false);
		} else {
			wCategoryButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					mCompositeAnnualMain.setAnnualViewType(AnnualViewType.Category);
					new UpdateAnnual(mCompositeAnnualMain).run();
				}
			});
		}

		Button wItemButton = new Button(this, SWT.TOGGLE);
		wItemButton.setText(" 項目別 ");
		if (mCompositeAnnualMain.getAnnualViewType() == AnnualViewType.Item) {
			wItemButton.setSelection(true);
			wItemButton.setEnabled(false);
		} else {
			wItemButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					mCompositeAnnualMain.setAnnualViewType(AnnualViewType.Item);
					new UpdateAnnual(mCompositeAnnualMain).run();
				}
			});
		}

		Button wOriginalButton = new Button(this, SWT.TOGGLE);
		wOriginalButton.setText("特殊収支");
		if (mCompositeAnnualMain.getAnnualViewType() == AnnualViewType.Original) {
			wOriginalButton.setSelection(true);
			wOriginalButton.setEnabled(false);
		} else {
			wOriginalButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					mCompositeAnnualMain.setAnnualViewType(AnnualViewType.Original);
					new UpdateAnnual(mCompositeAnnualMain).run();
				}
			});
		}
	}
}
