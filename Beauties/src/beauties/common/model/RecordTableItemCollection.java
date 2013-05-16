package beauties.common.model;

import java.util.Collection;

import beauties.record.model.RecordTableItem;

public class RecordTableItemCollection {
	private Collection<RecordTableItem> mItemsPast;
	private Collection<RecordTableItem> mItemsFuture;
	
	public RecordTableItemCollection(Collection<RecordTableItem> pItemsPast, Collection<RecordTableItem> pItemsFuture) {
		mItemsPast = pItemsPast;
		mItemsFuture = pItemsFuture;
	}
	public Collection<RecordTableItem> getItemsPast() {
		return mItemsPast;
	}
	
	public Collection<RecordTableItem> getItemsFuture() {
		return mItemsFuture;
	}
}
