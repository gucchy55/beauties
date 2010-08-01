package beauties.annual.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import beauties.annual.AnnualController;
import beauties.annual.OpenDialogAnnualPeriod;
import beauties.annual.model.AnnualViewType;
import beauties.model.SystemData;
import beauties.record.view.CompositeBookNames;
import util.Util;
import util.view.MyGridData;
import util.view.MyGridLayout;

class CompositeAnnualBookTab extends Composite {

	private static final int mPeriodWidthHint = 130;
	private static final int mArrowWidthHint = 30;
	private final DateFormat mDF = new SimpleDateFormat("yyyy年");

	private Label mThisMonthLabel;

	private Composite mPeriodComp;
	private CompositeBookNames mBookNameComp;
	private AnnualController mCTL;

	CompositeAnnualBookTab(Composite pParent, AnnualController pCTL) {
		super(pParent, SWT.NONE);
		mCTL = pCTL;

		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, false)
				.getMyGridData());

		createPeriodComp();

		GridData wGridDataArrow = new MyGridData(GridData.FILL, GridData.FILL, false, true)
				.getMyGridData();
		wGridDataArrow.widthHint = mArrowWidthHint;
		
		createPrevArrow(wGridDataArrow);

		createThisMonthLabel();

		createNextArrow(wGridDataArrow);

		createBookNameComp();
	}

	private void createPeriodComp() {
		mPeriodComp = new Composite(this, SWT.NONE);
		mPeriodComp.setLayout(new MyGridLayout(3, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.BEGINNING, GridData.FILL, false, true)
				.getMyGridData();
		wGridData.widthHint = mPeriodWidthHint;
		mPeriodComp.setLayoutData(wGridData);
	}

	private void createPrevArrow(GridData pGridData) {
		Button wPrevMonthButton = new Button(mPeriodComp, SWT.ARROW | SWT.LEFT);
		wPrevMonthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mCTL.setPrevPeriod();
				mCTL.recreateMainTable();
			}
		});
		wPrevMonthButton.setLayoutData(pGridData);
	}

	private void createThisMonthLabel() {
		mThisMonthLabel = new Label(mPeriodComp, SWT.CENTER);
		updatePeriodLabel();
		mThisMonthLabel.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent arg0) {
				new OpenDialogAnnualPeriod(mCTL).run();
			}
		});

		GridData wGridDataLabel = new MyGridData(GridData.FILL, GridData.CENTER, true, true)
				.getMyGridData();
		mThisMonthLabel.setLayoutData(wGridDataLabel);
	}

	private void createNextArrow(GridData pGridData) {
		Button wNextMonthButton = new Button(mPeriodComp, SWT.ARROW | SWT.RIGHT);
		wNextMonthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mCTL.setNextPeriod();
				mCTL.recreateMainTable();
			}
		});
		wNextMonthButton.setLayoutData(pGridData);
	}

	private void createBookNameComp() {
		mBookNameComp = new CompositeBookNames(this, mCTL.getBookId());
		for (Map.Entry<Integer, Button> entry : mBookNameComp.getBookButtonMap().entrySet()) {
			final int wBookId = entry.getKey();
			Button wButton = entry.getValue();
			wButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (mCTL.getBookId() == wBookId) {
						((Button) e.getSource()).setSelection(true);
						return;
					}
					mBookNameComp.getBookButtonMap().get(mCTL.getBookId()).setSelection(false);
					mCTL.setBookId(wBookId);
					mCTL.recreateMainTable();
				}
			});
		}
	}

	private void updatePeriodLabel() {
		if (mCTL.getFiscalPeriod()) {
			mThisMonthLabel
					.setText(mDF.format(Util.getMonthDateRange(
							mCTL.getAnnualDateRange().getStartDate(), SystemData.getCutOff())
							.getEndDate()));
		} else {
			mThisMonthLabel.setText("期間指定");
		}
	}
	
	void updateView() {
		this.setVisible(mCTL.getAnnualViewType() != AnnualViewType.Original);
	}
}
