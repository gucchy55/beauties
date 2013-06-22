package beauties.common.model;

import java.util.HashMap;
import java.util.Map;

import beauties.common.lib.SystemData;

public class Category implements IComboItem {
	private int mId;
	private String mName;
	private IncomeExpenseType mIncomeExpenseType;
	
	private static Map<Integer, Category> mCategoryMap = new HashMap<>();
	
	private Category(int pId, String pName, IncomeExpenseType pType) {
		mId = pId;
		mName = pName;
		mIncomeExpenseType = pType;
	}
	
	public int getId() {
		return mId;
	}
	
	@Override
	public String getName() {
		return mName;
	}

	public IncomeExpenseType getIncomeExpenseType() {
		return mIncomeExpenseType;
	}

	public static Category getCategory(int pId) {
		return mCategoryMap.get(pId);
	}
	
	public static Category generateCategory(int pId, String pName, IncomeExpenseType pType) {
		Category wCategory = new Category(pId, pName, pType);
		mCategoryMap.put(pId, wCategory);
		return wCategory;
	}
	
	public static void clear() {
		mCategoryMap.clear();
	}
	
	public static Category getAllCategory() {
		if (getCategory(SystemData.getUndefinedInt()) == null) {
			generateCategory(SystemData.getUndefinedInt(), "（すべて）", null);
		}
		return getCategory(SystemData.getUndefinedInt());
	}
	
	public boolean isAllCategory() {
		return mId == SystemData.getUndefinedInt();
	}
	
	@Override
	public String toString() {
		return mId + "_" + mName;
	}
	
}
