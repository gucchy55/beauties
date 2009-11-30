package model;

public enum AnnualViewType {
	Category(0),
	Item(1),
	Original(2),
	;
	
	public final int value;
	private AnnualViewType(int value) { this.value = value; }
	
	public static AnnualViewType valueOf(int i) {
	    return AnnualViewType.values()[i];
	}
}
