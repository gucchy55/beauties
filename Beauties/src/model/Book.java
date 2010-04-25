package model;

public class Book {
	private double mBalance = 0;
	private int mId;
	private String mName;
	
	public Book(int pId, String pName) {
		mId = pId;
		mName = pName;
	}
	
	public void setBalance(double pBalance) {
		mBalance = pBalance;
	}
	
	public int getId() {
		return mId;
	}
	
	public String getName() {
		return mName;
	}
	
	public double getBalance() {
		return mBalance;
	}
	
}
