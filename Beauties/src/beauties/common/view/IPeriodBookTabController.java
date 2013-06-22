package beauties.common.view;

import org.eclipse.swt.widgets.Composite;

import beauties.common.model.Book;

public interface IPeriodBookTabController {
	
	void setPrevPeriod();
	
	void setNextPeriod();
	
	void openDialogPeriod();
	
	String getPeriodLabelText();
	
//	int getBookId();
	
	Book getBook();
	
//	void setBookId(int pId);
	
	void setBook(Book pBook);

	void updateTable();
	
	void changeBook();
	
	Composite getComposite();
}
