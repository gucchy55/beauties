package view.entry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import model.action.OpenDialogPeriod;
import model.action.UpdateEntry;
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
import view.util.MyGridData;
import view.util.MyGridLayout;

public class CompositeBookTab extends Composite {

	// private CompositeRightMain mCompositeRightMain;
	private CompositeEntry mCompositeEntry;
	private Date mEndDate;

	private static final int mPeriodWidthHint = 130;
	private static final int mArrowWidthHint = 30;

	private Composite mPeriodComp;
	private CompositeBookNames mBookNameComp;

	public CompositeBookTab(Composite pParent) {
		super(pParent, SWT.NONE);

		mCompositeEntry = (CompositeEntry) pParent;
		mEndDate = mCompositeEntry.getEndDate();

		init();
	}

	private void init() {
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
				mCompositeEntry.setMonthPeriod(true);
				UpdateEntry wAdjusentEntry = new UpdateEntry(mCompositeEntry, Util.getAdjusentMonth(mEndDate, -1));
				wAdjusentEntry.run();
			}
		});

		GridData wGridDataArrow = new MyGridData(GridData.FILL, GridData.FILL, false, true).getMyGridData();
		wGridDataArrow.widthHint = mArrowWidthHint;
		wPrevMonthButton.setLayoutData(wGridDataArrow);

		Label wThisMonthLabel = new Label(mPeriodComp, SWT.CENTER);
		if (mCompositeEntry.isMonthPeriod()) {
			DateFormat df = new SimpleDateFormat("yyyy/MM");
			wThisMonthLabel.setText(df.format(mEndDate));
		} else {
			wThisMonthLabel.setText("期間指定");
		}

		wThisMonthLabel.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent arg0) {
				new OpenDialogPeriod(mCompositeEntry).run();
			}
		});

		GridData wGridDataLabel = new MyGridData(GridData.FILL, GridData.CENTER, true, true).getMyGridData();
		wThisMonthLabel.setLayoutData(wGridDataLabel);

		Button wNextMonthButton = new Button(mPeriodComp, SWT.ARROW | SWT.RIGHT);
		wNextMonthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mCompositeEntry.setMonthPeriod(true);
				UpdateEntry wAdjusentEntry = new UpdateEntry(mCompositeEntry, Util.getAdjusentMonth(mEndDate, 1));
				wAdjusentEntry.run();
			}
		});
		wNextMonthButton.setLayoutData(wGridDataArrow);

		mBookNameComp = new CompositeBookNames(this, mCompositeEntry.getBookId());
		// mBookNameComp.setLayout(new MyRowLayout().getMyRowLayout());
		// mBookNameComp.setLayoutData(new MyGridData(GridData.FILL,
		// GridData.FILL, true, true).getMyGridData());

		for (Map.Entry<Integer, Button> entry : mBookNameComp.getBookButtonMap().entrySet()) {
			final int wBookId = entry.getKey();
			Button wButton = entry.getValue();
//			for (Listener l : wButton.getListeners(SWT.Selection)) {
//				wButton.removeListener(SWT.Selection, l);
//			}

			if (mCompositeEntry.getBookId() == wBookId) {
				wButton.setSelection(true);
				wButton.setEnabled(false);
			} else {
//				wButton.setEnabled(true);
//				wButton.setSelection(false);
				wButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						mCompositeEntry.setBookId(wBookId);
						new UpdateEntry(mCompositeEntry).run();
					}
				});
			}
		}
	}

}
