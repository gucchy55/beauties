package model;

public enum RightType {
	Main(0),
	Anual(1),
	Graph(2),
	Memo(3),
	Setting(4);
	
	public final int value;
	private RightType(int value) { this.value = value; }
	
	public static RightType valueOf(int i) {
	    return RightType.values()[i];
	}
}
