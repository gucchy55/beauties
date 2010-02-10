package view.annual;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import model.action.OpenDialogAnnualPeriod;
import model.action.UpdateAnnual;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import util.Util;
import view.entry.CompositeBookNames;
import view.util.MyGridData;
import view.util.MyGridLayout;

class CompositeAnnualBookTab extends Composite {

	private CompositeAnnualMain mCompositeAnnualMain;

	private static final int mPeriodWidthHint = 130;
	private static final int mArrowWidthHint = 30;

	private Composite mPeriodComp;
	private CompositeBookNames mBookNameComp;

	public CompositeAnnualBookTab(Composite pParent) {
		super(pParent, SWT.NONE);

		mCompositeAnnualMain = (CompositeAnnualMain) pParent;

		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, false).getMyGridData());

		mPeriodComp = new Composite(this, SWT.NONE);
		mPeriodComp.setLayout(new MyGridLayout(3, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.BEGINNING, GridData.FILL, false, true).getMyGridData();
		wGridData.widthHint = mPeriodWidthHint;
		mPeriodComp.setLayoutData(wGridData);

		Button wPrevMonthButton = new Button(mPeriodComp, SWT.ARROW | SWT.LEFT);
		wPrevMonthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mCompositeAnnualMain.setStartDate(Util.getPeriod(Util.getAdjusentMonth(mCompositeAnnualMain
						.getStartDate(), -mCompositeAnnualMain.getMonthCount()))[0]);
				mCompositeAnnualMain.setEndDate(Util.getPeriod(Util.getAdjusentMonth(mCompositeAnnualMain.getEndDate(),
						-mCompositeAnnualMain.getMonthCount()))[1]);
				new UpdateAnnual(mCompositeAnnualMain).run();
			}
		});

		GridData wGridDataArrow = new MyGridData(GridData.FILL, GridData.FILL, false, true).getMyGridData();
		wGridDataArrow.widthHint = mArrowWidthHint;
		wPrevMonthButton.setLayoutData(wGridDataArrow);

		Label wThisMonthLabel = new Label(mPeriodComp, SWT.CENTER);
		if (mCompositeAnnualMain.isAnnualPeriod()) {
			DateFormat df = new SimpleDateFormat("yyyy年");
			wThisMonthLabel.setText(df.format(Util.getPeriod(mCompositeAnnualMain.getStartDate())[1]));
		} else {
			wThisMonthLabel.setText("期間指定");
		}

		wThisMonthLabel.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent arg0) {
				new OpenDialogAnnualPeriod(mCompositeAnnualMain).run();
			}
		});

		GridData wGridDataLabel = new MyGridData(GridData.FILL, GridData.CENTER, true, true).getMyGridData();
		wThisMonthLabel.setLayoutData(wGridDataLabel);

		Button wNextMonthButton = new Button(mPeriodComp, SWT.ARROW | SWT.RIGHT);
		wNextMonthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mCompositeAnnualMain.setStartDate(Util.getPeriod(Util.getAdjusentMonth(mCompositeAnnualMain
						.getStartDate(), mCompositeAnnualMain.getMonthCount()))[0]);
				mCompositeAnnualMain.setEndDate(Util.getPeriod(Util.getAdjusentMonth(mCompositeAnnualMain.getEndDate(),
						mCompositeAnnualMain.getMonthCount()))[1]);
				new UpdateAnnual(mCompositeAnnualMain).run();
			}
		});
		wNextMonthButton.setLayoutData(wGridDataArrow);

		if (mBookNameComp != null) {
			mBookNameComp.dispose();
		}

		mBookNameComp = new CompositeBookNames(this, mCompositeAnnualMain.getBookId());
		for (Map.Entry<Integer, Button> entry : mBookNameComp.getBookButtonMap().entrySet()) {
			final int wBookId = entry.getKey();
			Button wButton = entry.getValue();

			if (mCompositeAnnualMain.getBookId() == wBookId) {
				wButton.setSelection(true);
				wButton.setEnabled(false);
			} else {
				wButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						mCompositeAnnualMain.setBookId(wBookId);
						new UpdateAnnual(mCompositeAnnualMain).run();
					}
				});
			}
		}
	}
}
