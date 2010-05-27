package model;

public class Book {
	private int mBalance = 0;
	private int mId;
	private String mName;
	
	public Book(int pId, String pName) {
		mId = pId;
		mName = pName;
	}
	
	public void setBalance(int pBalance) {
		mBalance = pBalance;
	}
	
	public int getId() {
		return mId;
	}
	
	public String getName() {
		return mName;
	}
	
	public int getBalance() {
		return mBalance;
	}
	
}
