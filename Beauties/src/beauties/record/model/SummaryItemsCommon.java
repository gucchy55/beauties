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
	
	private void setOperatingProfit(int pOperatingProfit) {
		mOperatingProfit = SummaryTableItemFactory.createOriginal(wOperatingProfitName, pOperatingProfit);
	}
	private void setActualProfit(int pActualProft) {
		mActualProfit = SummaryTableItemFactory.createOriginal(wActualProfitName, pActualProft);
	}
	private void setActualBalance(int pActualBalance) {
		mActualBalance = SummaryTableItemFactory.createOriginal(wActualBalanceName, pActualBalance);
	}
	private void setTempBalance(int pTempBalance) {
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
		private int mOperatingProfit;
		private int mActualProfit;
		private int mActualBalance;
		private int mTempBalance;
		
		public Builder operationalProfit(int pOperationalProfit) {
			mOperatingProfit = pOperationalProfit;
			return this;
		}
		
		public Builder actualProfit(int pActualProfit) {
			mActualProfit = pActualProfit;
			return this;
		}
		
		public Builder actualBalance(int pActualBalance) {
			mActualBalance = pActualBalance;
			return this;
		}
		
		public Builder tempBalance(int pTempBalance) {
			mTempBalance = pTempBalance;
			return this;
		}
		
		public SummaryItemsCommon build() {
			return new SummaryItemsCommon(this);
		}
	}
}
