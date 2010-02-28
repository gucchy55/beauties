package model;

import java.util.ArrayList;
import java.util.List;

public class ConfigItem {
	private int mId;
	private String mName;
	private boolean isCategory = false;
	private boolean hasItem = false;
	private boolean hasParent = false;
	
	private ConfigItem mParent;
	private List<ConfigItem> mItemList;

	public ConfigItem(int pId, String pName, boolean isCategory) {
		mId = pId;
		mName = pName;
		this.isCategory = isCategory;
	}

	public void addItem(ConfigItem pItem) {
		
		pItem.setParent(this);
		
		if (!hasItem) {
			hasItem = true;
			mItemList = new ArrayList<ConfigItem>();
		}
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
	
	public boolean hasItem() {
		return hasItem;
	}

	public ConfigItem[] getItems() {
		return (ConfigItem[])mItemList.toArray(new ConfigItem[0]);
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
	
}
