package beauties.common.view;

import org.eclipse.swt.widgets.Composite;

public interface IPeriodBookTabController {
	
	public void setPrevPeriod();
	
	public void setNextPeriod();
	
	public void openDialogPeriod();
	
	public String getPeriodLabelText();
	
	public int getBookId();
	
	public void setBookId(int pId);
	
	public void updateTable();
	
	public Composite getComposite();
}
