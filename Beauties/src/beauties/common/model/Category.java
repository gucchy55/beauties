package beauties.common.model;

import java.util.HashMap;
import java.util.Map;

public class Category {
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
	
}
