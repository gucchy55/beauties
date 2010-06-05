package model;

import java.util.ArrayList;
import java.util.List;

public class ConfigItem {
	private int mId = SystemData.getUndefinedInt();
	private String mName;
	private boolean isSpecial = false;
	private boolean isCategory = false;
	private boolean hasParent = false;

	private ConfigItem mParent;
	private List<ConfigItem> mItemList = new ArrayList<ConfigItem>();

	public ConfigItem(int pId, String pName, boolean isCategory) {
		mId = pId;
		mName = pName;
		this.isCategory = isCategory;
	}

	public ConfigItem(String pName) {
		this.mName = pName;
		this.isSpecial = true;
	}

	public void addItem(ConfigItem pItem) {
		pItem.setParent(this);
		mItemList.add(pItem);
	}

	public int getId() {
		return mId;
	}

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

	public ConfigItem[] getChildren() {
		return (ConfigItem[]) mItemList.toArray(new ConfigItem[0]);
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

	public String toString() {
		return mId + "_" + mName;
	}

	public void setItems(ConfigItem[] pItems) {
		mItemList.clear();
		for (ConfigItem wItem : pItems) {
			mItemList.add(wItem);
		}
	}

	public void moveUp() {
		if (this.isSpecial)
			return;
		ConfigItem wParent = this.getParent();
		ConfigItem[] wChildren = wParent.getChildren();
		for (int i = 1; i < wChildren.length; i++) {
			ConfigItem ci = wChildren[i];
			if (ci.getId() != this.mId)
				continue;
			wChildren[i] = wChildren[i - 1];
			wChildren[i - 1] = this;
			wParent.setItems(wChildren);
			return;
		}
	}

	public void moveDown() {
		if (this.isSpecial)
			return;
		ConfigItem wParent = this.getParent();
		ConfigItem[] wChildren = wParent.getChildren();
		for (int i = 0; i < wChildren.length - 1; i++) {
			ConfigItem ci = wChildren[i];
			if (ci.getId() != this.mId)
				continue;
			wChildren[i] = wChildren[i + 1];
			wChildren[i + 1] = this;
			wParent.setItems(wChildren);
			return;
		}
	}

}
