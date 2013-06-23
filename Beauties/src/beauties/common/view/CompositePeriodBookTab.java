package beauties.common.view;

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

import beauties.common.lib.SystemData;
import beauties.common.model.Book;

public class CompositePeriodBookTab extends Composite {

	private static final int mPeriodWidthHint = 130;
	private static final int mArrowWidthHint = 30;

	private Label mPeriodLabel;

	private Composite mPeriodComp;
	private CompositeBookNames mBookNameComp;
	private IPeriodBookTabController mCTL;

	public CompositePeriodBookTab(IPeriodBookTabController pCTL) {
		super(pCTL.getComposite(), SWT.NONE);
		mCTL = pCTL;

		this.setLayout(new MyGridLayout(2, false).getMyGridLayout());
		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, false)
				.getMyGridData());

		createPeriodComp();

		GridData wGridDataArrow = new MyGridData(GridData.FILL, GridData.FILL, false, true)
				.getMyGridData();
		wGridDataArrow.widthHint = mArrowWidthHint;

		createPrevArrow(wGridDataArrow);

		createPeriodLabel();

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
		wPrevMonthButton.setLayoutData(pGridData);
		wPrevMonthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mCTL.setPrevPeriod();
			}
		});
	}

	private void createPeriodLabel() {
		mPeriodLabel = new Label(mPeriodComp, SWT.CENTER);
		mPeriodLabel.setText(mCTL.getPeriodLabelText());
		GridData wGridDataLabel = new MyGridData(GridData.FILL, GridData.CENTER, true, true)
				.getMyGridData();
		mPeriodLabel.setLayoutData(wGridDataLabel);
		mPeriodLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				mCTL.openDialogPeriod();
			}
		});

	}

	private void createNextArrow(GridData pGridData) {
		Button wNextMonthButton = new Button(mPeriodComp, SWT.ARROW | SWT.RIGHT);
		wNextMonthButton.setLayoutData(pGridData);
		wNextMonthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mCTL.setNextPeriod();
			}
		});
	}

	private void createBookNameComp() {
		mBookNameComp = new CompositeBookNames(this, mCTL.getBook());
		mBookNameComp.getBookButtonMap().get(mCTL.getBook()).setSelection(true);
		mBookNameComp.getBookButtonMap().get(mCTL.getBook()).setBackground(
				SystemData.getColorYellow());

		for (Map.Entry<Book, Button> entry : mBookNameComp.getBookButtonMap().entrySet()) {
			final Book wBook = entry.getKey();
			Button wButton = entry.getValue();
			wButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (mCTL.getBook().equals(wBook)) {
						((Button) e.getSource()).setSelection(true);
						return;
					}
					mBookNameComp.getBookButtonMap().get(mCTL.getBook()).setSelection(false);
					mBookNameComp.getBookButtonMap().get(mCTL.getBook()).setBackground(null);
					mCTL.setBook(wBook);
					mBookNameComp.getBookButtonMap().get(mCTL.getBook()).setBackground(
							SystemData.getColorYellow());
					mCTL.updateTable();
				}
			});
		}
	}
	
	public void updateMonthLabel() {
		mPeriodLabel.setText(mCTL.getPeriodLabelText());
	}
	
	public Composite getBookNameComposite() {
		return mBookNameComp;
	}
}
