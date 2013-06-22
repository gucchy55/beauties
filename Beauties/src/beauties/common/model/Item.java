package beauties.common.model;

import java.util.HashMap;
import java.util.Map;

import beauties.common.lib.SystemData;

public class Item implements IComboItem {
	private int mId;
	private String mName;
	private Category mCategory;
	
	private static Map<Integer, Item> mItemMap = new HashMap<>();
	
	private Item(int pId, String pName, Category pCategory) {
		mId = pId;
		mName = pName;
		mCategory = pCategory;
	}
	
	public int getId() {
		return mId;
	}
	
	@Override
	public String getName() {
		return mName;
	}
	
	public Category getCategory() {
		return mCategory;
	}
	
	public static Item getItem(int pId) {
		return mItemMap.get(pId);
	}

	public static Item generateItem(int pId, String pName, Category pCategory) {
		Item wItem = new Item(pId, pName, pCategory);
		mItemMap.put(pId, wItem);
		return wItem;
	}
	
	public static void clear() {
		mItemMap.clear();
	}

	public static Item getUndefinedItem() {
		if (Item.getItem(SystemData.getUndefinedInt()) == null) {
			Item.generateItem(SystemData.getUndefinedInt(), "", null);
		}
		return Item.getItem(SystemData.getUndefinedInt());
	}
	
	@Override
	public String toString() {
		return mId + "_" + mName;
	}

}
