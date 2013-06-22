package beauties.common.model;

import java.util.HashMap;
import java.util.Map;

import beauties.common.lib.SystemData;

public class Book implements IComboItem {
	private int mBalance = 0;
	private int mId;
	private String mName;
	
	private static Map<Integer, Book> mBookMap = new HashMap<>();
	
	private Book(int pId, String pName) {
		mId = pId;
		mName = pName;
	}
	
	public boolean isAllBook() {
		return mId == SystemData.getAllBookInt();
	}
	
	public void setBalance(int pBalance) {
		mBalance = pBalance;
	}
	
	public int getId() {
		return mId;
	}
	
	@Override
	public String getName() {
		return mName;
	}
	
	public int getBalance() {
		return mBalance;
	}

	public static Book getBook(int pId) {
		return mBookMap.get(pId);
	}

	public static Book generateBook(int pId, String pName) {
		Book wBook = new Book(pId, pName);
		mBookMap.put(pId, wBook);
		return wBook;
	}
	
	public static Book getAllBook() {
		if (getBook(SystemData.getAllBookInt()) == null) {
			generateBook(SystemData.getAllBookInt(), "全て");
		}
		return getBook(SystemData.getAllBookInt());
	}
	
	public static void clear() {
		mBookMap.clear();
	}
	
}
