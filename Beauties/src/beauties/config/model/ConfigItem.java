package beauties.config.model;

import java.util.ArrayList;
import java.util.List;

import beauties.common.lib.SystemData;
import beauties.common.model.Category;
import beauties.common.model.Item;


public class ConfigItem {
//	private int mId = SystemData.getUndefinedInt();
	private String mName;
	private boolean isSpecial = false;
	private boolean isCategory = false;
	private boolean hasParent = false;
	private Category mCategory;
	private Item mItem;

	private ConfigItem mParent;
	private List<ConfigItem> mItemList = new ArrayList<ConfigItem>();

//	public ConfigItem(int pId, String pName, boolean isCategory) {
//		mId = pId;
//		mName = pName;
//		this.isCategory = isCategory;
//	}
	
	public ConfigItem(Item pItem) {
		mItem = pItem;
		mName = pItem.getName();
		isCategory = false;
	}
	
	public ConfigItem(Category pCategory) {
		mCategory = pCategory;
		mName = pCategory.getName();
		isCategory = true;
	}

	public ConfigItem(String pName) {
		this.mName = pName;
		this.isSpecial = true;
	}

	public void addItem(ConfigItem pItem) {
		pItem.setParent(this);
		mItemList.add(pItem);
	}

//	public int getId() {
//		return mId;
//	}

	public String getName() {
		return mName;
	}

	public boolean isCategory() {
		return isCategory;
	}

	public boolean isSpecial() {
		return isSpecial;
	}

	public boolean hasItem() {
		return (mItemList.size() > 0);
	}

	public List<ConfigItem> getChildren() {
		return mItemList;
	}
	
	public List<ConfigItem> getChildrenAsList() {
		return mItemList;
	}

	private void setParent(ConfigItem pParent) {
		hasParent = true;
		mParent = pParent;
	}

	public boolean hasParent() {
		return hasParent;
	}

	public ConfigItem getParent() {
		return mParent;
	}

	@Override
	public String toString() {
		if (isSpecial) {
			return SystemData.getUndefinedInt() + "_" + mName;
		}
		if (isCategory) {
			return mCategory.toString();
		}
		return mItem.toString();
	}

	public void setItems(List<ConfigItem> pItems) {
		mItemList = pItems;
//		mItemList.clear();
//		for (ConfigItem wItem : pItems) {
//			mItemList.add(wItem);
//		}
	}

	public void moveUp() {
		if (this.isSpecial) {
			return;
		}
		ConfigItem wParent = this.getParent();
		List<ConfigItem> wChildren = wParent.getChildren();
		int wThisIndex = wChildren.indexOf(this);
		if (wThisIndex == 0) {
			return;
		}
		ConfigItem wPre = wChildren.get(wThisIndex - 1);
		
		wChildren.set(wThisIndex, wPre);
		wChildren.set(wThisIndex - 1, this);
		wParent.setItems(wChildren);
//		for (int i = 1; i < wChildren.size(); i++) {
//			ConfigItem ci = wChildren.get(i);
////			if (ci.getId() != this.mId) {
//			if (ci.equals(this)) {
//				continue;
//			}
//			wChildren.set(i, wChildren.get(i - 1));
//			wChildren.set(i - 1, this);
//			wParent.setItems(wChildren);
//			return;
//		}
	}

	public void moveDown() {
		if (this.isSpecial) {
			return;
		}
		ConfigItem wParent = this.getParent();
		List<ConfigItem> wChildren = wParent.getChildren();
		int wThisIndex = wChildren.indexOf(this);
		if (wThisIndex == wChildren.size() - 1) {
			return;
		}
		ConfigItem wNext = wChildren.get(wThisIndex + 1);
		
		wChildren.set(wThisIndex, wNext);
		wChildren.set(wThisIndex + 1, this);
		wParent.setItems(wChildren);
//		for (int i = 0; i < wChildren.size() - 1; i++) {
//			ConfigItem ci = wChildren.get(i);
////			if (ci.getId() != this.mId)
//			if (ci.equals(this)) {
//				continue;
//			}
//			wChildren.set(i, wChildren.get(i + 1));
//			wChildren.set(i + 1, this);
//			wParent.setItems(wChildren);
//			return;
//		}
	}

	public Category getCategory() {
		return mCategory;
	}

	public Item getItem() {
		return mItem;
	}

}
