package beauties.common.view;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import beauties.common.lib.SystemData;
import beauties.common.model.Book;


class CompositeBookNames extends Composite {

	private Collection<Book> mBooks;
	private Map<Book, Button> mBookButtonMap;
	private Book mBook;

	CompositeBookNames(Composite pParent, Book pBook) {
		super(pParent, SWT.NONE);

		mBooks = SystemData.getBooks(true);
		mBookButtonMap = new LinkedHashMap<>();
		mBook = pBook;

		this.setLayout(new MyRowLayout().getMyRowLayout());
		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData());

		for (Book wBook : mBooks) {
//			int wBookId = wBook.getId();
			Button wBookButton = new Button(this, SWT.TOGGLE);
			wBookButton.setText(wBook.getName());
			mBookButtonMap.put(wBook, wBookButton);

			if (mBook.equals(wBook)) {
				wBookButton.setSelection(true);
			}
		}
	}
	
	Map<Book, Button> getBookButtonMap() {
		return mBookButtonMap;
	}

}
