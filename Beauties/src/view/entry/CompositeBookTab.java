package view.entry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import model.SystemData;
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
import view.CompositeRightMain;
import view.util.MyGridData;
import view.util.MyGridLayout;
import view.util.MyRowLayout;

public class CompositeBookTab extends Composite {

	private CompositeRightMain mCompositeRightMain;
	private Date mEndDate;
	private Map<Integer, String> mBookMap;
	
	private static final int mPeriodWidthHint = 130;
	private static final int mArrowWidthHint = 30;

	private Composite mPeriodComp;
	private Composite mBookNameComp;

	public CompositeBookTab(Composite pParent) {
		super(pParent, SWT.NONE);

		mCompositeRightMain = (CompositeRightMain) pParent.getParent();
		mEndDate = SystemData.getEndDate();
		mBookMap = SystemData.getBookMap(true);

		init();
	}

	private void init() {
		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true,
				false).getMyGridData());

		mPeriodComp = new Composite(this, SWT.NONE);
		mPeriodComp.setLayout(new MyGridLayout(3, false).getMyGridLayout());
		GridData wGridData = new MyGridData(GridData.BEGINNING, GridData.FILL,
				false, true).getMyGridData();
		wGridData.widthHint = mPeriodWidthHint;
		mPeriodComp.setLayoutData(wGridData);

		Button wPrevMonthButton = new Button(mPeriodComp, SWT.ARROW | SWT.LEFT);
		wPrevMonthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SystemData.setMonthPeriod(true);
				UpdateEntry wAdjusentEntry = new UpdateEntry(
						mCompositeRightMain, Util
								.getAdjusentMonth(mEndDate, -1));
				wAdjusentEntry.run();
			}
		});

		GridData wGridDataArrow = new MyGridData(GridData.FILL, GridData.FILL,
				false, true).getMyGridData();
		wGridDataArrow.widthHint = mArrowWidthHint;
		wPrevMonthButton.setLayoutData(wGridDataArrow);

		Label wThisMonthLabel = new Label(mPeriodComp, SWT.CENTER);
		if (SystemData.isMonthPeriod()) {
			DateFormat df = new SimpleDateFormat("yyyy/MM");
			wThisMonthLabel.setText(df.format(mEndDate));
		} else {
			wThisMonthLabel.setText("期間指定");
		}

		wThisMonthLabel.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent arg0) {
				new OpenDialogPeriod(getShell()).run();
			}
		});

		GridData wGridDataLabel = new MyGridData(GridData.FILL,
				GridData.CENTER, true, true).getMyGridData();
		wThisMonthLabel.setLayoutData(wGridDataLabel);

		Button wNextMonthButton = new Button(mPeriodComp, SWT.ARROW | SWT.RIGHT);
		wNextMonthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SystemData.setMonthPeriod(true);
				UpdateEntry wAdjusentEntry = new UpdateEntry(
						mCompositeRightMain, Util.getAdjusentMonth(mEndDate, 1));
				wAdjusentEntry.run();
			}
		});
		wNextMonthButton.setLayoutData(wGridDataArrow);

		mBookNameComp = new Composite(this, SWT.NONE);
		mBookNameComp.setLayout(new MyRowLayout().getMyRowLayout());
		wGridData.widthHint = mPeriodWidthHint;
		mBookNameComp.setLayoutData(new MyGridData(GridData.FILL,
				GridData.FILL, true, true).getMyGridData());

		for (int wBookId : mBookMap.keySet()) {
			Button wBookButton = new Button(mBookNameComp, SWT.TOGGLE);
			wBookButton.setText(mBookMap.get(wBookId));
			if (wBookId == SystemData.getBookId()) {
				wBookButton.setSelection(true);
				wBookButton.setEnabled(false);
			} else {
				wBookButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Button wBookButton = (Button) e.getSource();
						String wBookName = wBookButton.getText();
						for (int wBookId : mBookMap.keySet()) {
							if (wBookName.equals(mBookMap.get(wBookId))) {
								SystemData.setBookId(wBookId);
								new UpdateEntry(mCompositeRightMain)
										.run();
								break;
							}
						}

					}
				});
			}

		}
	}


}
