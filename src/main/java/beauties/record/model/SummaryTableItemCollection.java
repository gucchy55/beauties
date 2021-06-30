package beauties.record.model;

import java.util.Collection;

public class SummaryTableItemCollection {
	private SummaryItemsCommon mItemsCommon;
	private SummaryTableItemsNormal mItemsNormal;
	
	public SummaryTableItemCollection(SummaryItemsCommon pItemsCommon, SummaryTableItemsNormal pItemsNormal) {
		mItemsCommon = pItemsCommon;
		mItemsNormal = pItemsNormal;
	}
	
	public Collection<SummaryTableItem> getList() {
		Collection<SummaryTableItem> wList = mItemsCommon.getList();
		wList.addAll(mItemsNormal.getItems());
		return wList;
	}
	
	public void setItemsNormal(SummaryTableItemsNormal pItemsNormal) {
		mItemsNormal = pItemsNormal;
	}
}
