package model;

public enum RightType {
	Main(0),
	Anual(1),
	Memo(2),
	Setting(3);
	
	public final int value;
	private RightType(int value) { this.value = value; }
	
	public static RightType valueOf(int i) {
	    return RightType.values()[i];
	}
}
