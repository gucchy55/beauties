package beauties.record.view;

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

import beauties.model.SystemData;
import beauties.record.OpenDialogPeriod;
import beauties.record.RecordController;
import util.Util;
import util.view.MyGridData;
import util.view.MyGridLayout;

class CompositeBookTab extends Composite {

	private static final int mPeriodWidthHint = 130;
	private static final int mArrowWidthHint = 30;

	private Composite mPeriodComp;
	private CompositeBookNames mBookNameComp;
	private RecordController mCTL;

	private Label mThisMonthLabel;

	CompositeBookTab(RecordController pCTL) {
		super(pCTL.getComposite(), SWT.NONE);
		mCTL = pCTL;

		create();
	}

	private void create() {
		initLayout();
		createPrevMonthButton();
		createThisMonthLabel();
		createNextMonthButton();
		createBookNameComp();
	}

	private void createNextMonthButton() {
		Button wNextMonthButton = new Button(mPeriodComp, SWT.ARROW | SWT.RIGHT);
		wNextMonthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mCTL.setDateRange(Util.getMonthDateRange(Util.getAdjusentMonth(mCTL.getDateRange()
						.getEndDate(), 1), SystemData.getCutOff()));
				mCTL.updateTable();
			}
		});
		GridData wGridDataArrow = new MyGridData(GridData.FILL, GridData.FILL, false, true)
				.getMyGridData();
		wGridDataArrow.widthHint = mArrowWidthHint;
		wNextMonthButton.setLayoutData(wGridDataArrow);
	}

	private void createThisMonthLabel() {
		mThisMonthLabel = new Label(mPeriodComp, SWT.CENTER);
		updateMonthLabel();

		mThisMonthLabel.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent arg0) {
				new OpenDialogPeriod(mCTL).run();
			}
		});

		GridData wGridDataLabel = new MyGridData(GridData.FILL, GridData.CENTER, true, true)
				.getMyGridData();
		mThisMonthLabel.setLayoutData(wGridDataLabel);
	}

	private void createPrevMonthButton() {
		Button wPrevMonthButton = new Button(mPeriodComp, SWT.ARROW | SWT.LEFT);
		wPrevMonthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mCTL.setDateRange(Util.getMonthDateRange(Util.getAdjusentMonth(mCTL.getDateRange()
						.getEndDate(), -1), SystemData.getCutOff()));
				mCTL.updateTable();
			}
		});

		GridData wGridDataArrow = new MyGridData(GridData.FILL, GridData.FILL, false, true)
				.getMyGridData();
		wGridDataArrow.widthHint = mArrowWidthHint;
		wPrevMonthButton.setLayoutData(wGridDataArrow);
	}

	private void initLayout() {
		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, false)
				.getMyGridData());

		mPeriodComp = new Composite(this, SWT.NONE);
		mPeriodComp.setLayout(new MyGridLayout(3, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.BEGINNING, GridData.FILL, false, true)
				.getMyGridData();
		wGridData.widthHint = mPeriodWidthHint;
		mPeriodComp.setLayoutData(wGridData);
	}

	private void createBookNameComp() {
		mBookNameComp = new CompositeBookNames(this, mCTL.getBookId());

		mBookNameComp.getBookButtonMap().get(mCTL.getBookId()).setSelection(true);
		mBookNameComp.getBookButtonMap().get(mCTL.getBookId()).setBackground(SystemData.getColorYellow());
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
					mBookNameComp.getBookButtonMap().get(mCTL.getBookId()).setBackground(null);
					mCTL.setBookId(wBookId);
					mBookNameComp.getBookButtonMap().get(mCTL.getBookId()).setBackground(SystemData.getColorYellow());
					mCTL.updateTable();
				}
			});
		}
	}

	void updateMonthLabel() {
		if (mCTL.getMonthPeriod()) {
			DateFormat df = new SimpleDateFormat("yyyy/MM");
			mThisMonthLabel.setText(df.format(mCTL.getDateRange().getEndDate()));
		} else {
			mThisMonthLabel.setText("期間指定");
		}
	}

}
