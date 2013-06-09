package beauties.config.view;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import beauties.common.lib.DbUtil;
import beauties.common.model.IncomeExpenseType;
import beauties.config.model.ConfigItem;

class DialogNewItem extends Dialog {

	private CompositeNewItem mCompositeNewItem;
	private boolean isCategory;
	private ConfigItem mConfigItem;

	public DialogNewItem(Shell parentShell, boolean isCategory) {
		super(parentShell);
		this.isCategory = isCategory;
	}

	public DialogNewItem(Shell parentShell, ConfigItem pConfigItem) {
		super(parentShell);
		this.isCategory = pConfigItem.isCategory();
		this.mConfigItem = pConfigItem;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		if (mConfigItem == null) {
			mCompositeNewItem = new CompositeNewItem(parent, isCategory);
		} else {
			mCompositeNewItem = new CompositeNewItem(parent, mConfigItem);
		}
		return mCompositeNewItem;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(300, 200);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(isCategory ? "分類追加" : "項目追加");
	}

	@Override
	protected void buttonPressed(int pButtonId) {
		if (pButtonId == IDialogConstants.OK_ID) { // 0
			if (mConfigItem == null) {
				mCompositeNewItem.insertItem();
			} else {
				mCompositeNewItem.updateItem();
			}
			close();
		} else if (pButtonId == IDialogConstants.CANCEL_ID) { // 1
			close();
		}
	}

}

class CompositeNewItem extends Composite {

	private boolean isCategory;
	private ConfigItem mConfigItem;

	private IncomeExpenseType mIncomeExpenseType = IncomeExpenseType.EXPENCE;
//	private boolean mIncome = false;

	// Map of ID & Name
	private Map<Integer, String> mCategoryNameMap;

	// Map of ComboIndex & ID
	private List<Integer> mCategoryIdList = new ArrayList<Integer>();

	private ComboViewer mIncomeExpenseComboViewer;
	private Combo mCategoryCombo;
	private Text mNameText;

	private static final int mVisibleComboItemCount = 10;

	public CompositeNewItem(Composite pParent, boolean isCategory) {
		super(pParent, SWT.NONE);

		this.isCategory = isCategory;
		initLayout();
		initWidgets();
		mNameText.setFocus();
	}

	public CompositeNewItem(Composite pParent, ConfigItem pConfigItem) {
		super(pParent, SWT.NONE);

		this.isCategory = pConfigItem.isCategory();
		mConfigItem = pConfigItem;
		initLayout();
		initWidgets();
		setWidgets();
		mNameText.setFocus();
	}

	private void initLayout() {
		GridLayout wGridLayout = new GridLayout(2, false);
		wGridLayout.verticalSpacing = 10;
		this.setLayout(wGridLayout);

		// InExLabel
		Label wInExLabel = new Label(this, SWT.NONE);
		wInExLabel.setText("収支");

		mIncomeExpenseComboViewer = new ComboViewer(this, SWT.READ_ONLY);
		mIncomeExpenseComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		mIncomeExpenseComboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				IncomeExpenseType wType = (IncomeExpenseType) element;
				return wType.getName();
			}
		});
		mIncomeExpenseComboViewer.setInput(EnumSet.allOf(IncomeExpenseType.class));
		mIncomeExpenseComboViewer.setSelection(new StructuredSelection(mIncomeExpenseType));
		
		mIncomeExpenseComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
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
			mCategoryCombo.setVisible(false);
		} else {
			updateCategoryCombo();
		}
	}

	private void setWidgets() {
		if (!isCategory) {
			int wCategoryId = mConfigItem.getParent().getId();

			if (!mCategoryIdList.contains(wCategoryId)) {
				mIncomeExpenseType = mIncomeExpenseType == IncomeExpenseType.INCOME ? IncomeExpenseType.EXPENCE : IncomeExpenseType.INCOME;
				updateCategoryCombo();
			}
			mCategoryCombo.select(mCategoryIdList.indexOf(wCategoryId));
		}
		mIncomeExpenseComboViewer.getCombo().setVisible(false);

		mNameText.setText(mConfigItem.getName());
	}

	private void modifyIncomeExpense() {
		mIncomeExpenseType = (IncomeExpenseType) ((IStructuredSelection) mIncomeExpenseComboViewer.getSelection()).getFirstElement();
		if (!isCategory) {
			updateCategoryCombo();
		}
	}

	private void updateCategoryCombo() {
		mCategoryNameMap = DbUtil.getAllCategoryNameMap(mIncomeExpenseType);
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

		mCategoryCombo.pack();

	}

	protected void insertItem() {
		// アイテム追加
		if ("".equals(mNameText.getText())) {
			MessageDialog.openWarning(getShell(), "Empty Name", "Input Name");
			return;
		}
		if (isCategory) {
			DbUtil.insertNewCategory(mIncomeExpenseType, mNameText.getText());
			return;
		}
		int wCategoryId = mCategoryIdList.get(mCategoryCombo.getSelectionIndex());
		DbUtil.insertNewItem(wCategoryId, mNameText.getText());
	}

	protected void updateItem() {
		if ("".equals(mNameText.getText())) {
			MessageDialog.openWarning(getShell(), "Empty Name", "Input Name");
			return;
		}
		if (isCategory) {
			DbUtil.updateCategory(mConfigItem.getId(), mNameText.getText());
			return;
		}
		DbUtil.updateItem(mCategoryIdList.get(mCategoryCombo.getSelectionIndex()), mConfigItem.getId(),
				mNameText.getText());
	}

}
