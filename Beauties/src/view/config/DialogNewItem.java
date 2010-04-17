package view.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.SystemData;
import model.db.DbUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class DialogNewItem extends Dialog {

	private CompositeNewItem mCompositeNewItem;
	private boolean isCategory;
	
	public DialogNewItem(Shell parentShell, boolean isCategory) {
		super(parentShell);
		this.isCategory = isCategory;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		mCompositeNewItem = new CompositeNewItem(parent, isCategory);
		return mCompositeNewItem;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 200);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(isCategory ? "分類追加" : "項目追加");
	}
	
	protected void buttonPressed(int pButtonId) {
		if (pButtonId == IDialogConstants.OK_ID) {	//0
			mCompositeNewItem.insertItem();
			close();
		} else if (pButtonId == IDialogConstants.CANCEL_ID){	//1
			close();
		}
	}

}

class CompositeNewItem extends Composite {
	
	private boolean isCategory;
	
	public CompositeNewItem(Composite pParent, boolean isCategory) {
		super(pParent, SWT.NONE);
		
		this.isCategory = isCategory;
		initLayout();
		initWidgets();
		mNameText.setFocus();
	}
	
	private boolean mIncome = false;
	private int mCategoryId;
	private int mItemId;

	// Map of ID & Name
	private Map<Integer, String> mCategoryNameMap;

	// Map of ComboIndex & ID
	private List<Integer> mCategoryIdList = new ArrayList<Integer>();

	private Combo mIncomeExpenseCombo;
	private Combo mCategoryCombo;
	private Text mNameText;

	private static final int mVisibleComboItemCount = 10;

	private void initLayout() {
		GridLayout wGridLayout = new GridLayout(2, false);
		wGridLayout.verticalSpacing = 10;
		this.setLayout(wGridLayout);

		// InExLabel
		Label wInExLabel = new Label(this, SWT.NONE);
		wInExLabel.setText("収支");

		mIncomeExpenseCombo = new Combo(this, SWT.READ_ONLY);
		mIncomeExpenseCombo.add("収入");
		mIncomeExpenseCombo.add("支出");
		if (mIncome) {
			mIncomeExpenseCombo.select(0);
		} else {
			mIncomeExpenseCombo.select(1);
		}
		mIncomeExpenseCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				modifyIncomeExpense();
			}
		});

		// Category
		Label wCategoryLabel = new Label(this, SWT.NONE);
		wCategoryLabel.setText("分類");

		mCategoryCombo = new Combo(this, SWT.READ_ONLY);

		// Item
		Label wItemLabel = new Label(this, SWT.NONE);
		wItemLabel.setText("名前");

		mNameText = new Text(this, SWT.BORDER);


	}

	private void initWidgets() {

		if (isCategory) {
			mCategoryCombo.setEnabled(false);
		} else {
			updateCategoryCombo();
		}
	}

	private void setWidgets() {

	}

	private void modifyIncomeExpense() {
		mIncome = (mIncomeExpenseCombo.getSelectionIndex() == 0)? true : false;
		if (!isCategory) {
			updateCategoryCombo();
		}
	}

	private void updateCategoryCombo() {
		for (Listener l : mCategoryCombo.getListeners(SWT.Modify)) {
			mCategoryCombo.removeListener(SWT.Modify, l);
		}
		mCategoryNameMap = DbUtil.getAllCategoryNameMap(mIncome);
		mCategoryCombo.removeAll();
		mCategoryIdList.clear();

		Iterator<Integer> wKeyIt = mCategoryNameMap.keySet().iterator();
		while (wKeyIt.hasNext()) {
			int wCategoryId = wKeyIt.next();
			mCategoryIdList.add(wCategoryId);
			mCategoryCombo.add(mCategoryNameMap.get(wCategoryId));
		}
		
		mCategoryCombo.setVisibleItemCount(mVisibleComboItemCount);

		mCategoryCombo.select(0);
		mCategoryId = mCategoryIdList.get(0);

		mCategoryCombo.pack();

	}

	public void insertItem() {
		// アイテム追加
		if ("".equals(mNameText.getText())) {
			MessageDialog.openWarning(getShell(), "Empty Name", "Input Name");
		} else {
			if (isCategory) {
				DbUtil.insertNewCategory(mIncome, mNameText.getText());
			} else {
				mCategoryId = mCategoryIdList.get(mCategoryCombo.getSelectionIndex());
				DbUtil.insertNewItem(mCategoryId, mNameText.getText());
			}
		}
	}


}
