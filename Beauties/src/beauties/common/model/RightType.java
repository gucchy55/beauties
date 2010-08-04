package beauties.common.model;

public enum RightType {
	Main("記帳"),
	Annual("年間一覧"),
	Memo("メモ帳"),
	Setting("設定");
	
	private final String mName;
	private RightType(String pName) { this.mName = pName; }
	
	public String toString() {
		return mName;
	}
}
