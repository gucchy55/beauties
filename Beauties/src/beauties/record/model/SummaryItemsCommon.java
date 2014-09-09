package beauties.record.model;

import java.util.ArrayList;
import java.util.Collection;

public class SummaryItemsCommon {
	private static final String wOperatingProfitName = "営業収支";
	private static final String wActualProfitName = "実質収支";
	private static final String wActualBalanceName = "実質残高";
	private static final String wTempBalanceName = "借入残高";
	
	private SummaryTableItem mOperatingProfit;
	private SummaryTableItem mActualProfit;
	private SummaryTableItem mActualBalance;
	private SummaryTableItem mTempBalance;
	
	private Collection<SummaryTableItem> mList;
	
	private void setOperatingProfit(long pOperatingProfit) {
		mOperatingProfit = SummaryTableItemFactory.createOriginal(wOperatingProfitName, pOperatingProfit);
	}
	private void setActualProfit(long pActualProft) {
		mActualProfit = SummaryTableItemFactory.createOriginal(wActualProfitName, pActualProft);
	}
	private void setActualBalance(long pActualBalance) {
		mActualBalance = SummaryTableItemFactory.createOriginal(wActualBalanceName, pActualBalance);
	}
	private void setTempBalance(long pTempBalance) {
		mTempBalance = SummaryTableItemFactory.createOriginal(wTempBalanceName, pTempBalance);
	}
	public SummaryTableItem getOperatingProfit() {
		return mOperatingProfit;
	}
	public SummaryTableItem getActualProfit() {
		return mActualProfit;
	}
	public SummaryTableItem getActualBalance() {
		return mActualBalance;
	}
	public SummaryTableItem getTempBalance() {
		return mTempBalance;
	}
	
	public Collection<SummaryTableItem> getList() {
		if (mList == null) {
			mList = new ArrayList<>();
			mList.add(mOperatingProfit);
			mList.add(mActualProfit);
			mList.add(mActualBalance);
			mList.add(mTempBalance);
		}
		return mList;
	}
	
	private SummaryItemsCommon(Builder pBuilder) {
		setOperatingProfit(pBuilder.mOperatingProfit);
		setActualProfit(pBuilder.mActualProfit);
		setActualBalance(pBuilder.mActualBalance);
		setTempBalance(pBuilder.mTempBalance);
	}
	
	public static class Builder {
		private long mOperatingProfit;
		private long mActualProfit;
		private long mActualBalance;
		private long mTempBalance;
		
		public Builder operationalProfit(long pOperationalProfit) {
			mOperatingProfit = pOperationalProfit;
			return this;
		}
		
		public Builder actualProfit(long pActualProfit) {
			mActualProfit = pActualProfit;
			return this;
		}
		
		public Builder actualBalance(long pActualBalance) {
			mActualBalance = pActualBalance;
			return this;
		}
		
		public Builder tempBalance(long pTempBalance) {
			mTempBalance = pTempBalance;
			return this;
		}
		
		public SummaryItemsCommon build() {
			return new SummaryItemsCommon(this);
		}
	}
}
