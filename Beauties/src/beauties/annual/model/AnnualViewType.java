package beauties.annual.model;

public enum AnnualViewType {
	Category(" 分類別 "),
	Item(" 項目別 "),
	Original("特殊収支"),
	;
	
	private final String mName;
	private AnnualViewType(String pName) { this.mName = pName; }
	
	public String toString() {
		return mName;
	}
	
//	public static AnnualViewType valueOf(int i) {
//	    return AnnualViewType.values()[i];
//	}
}
