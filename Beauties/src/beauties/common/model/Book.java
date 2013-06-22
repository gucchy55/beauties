package beauties.common.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import beauties.common.lib.DbUtil;
import beauties.common.lib.SystemData;

public class Book implements IComboItem {
	private int mBalance = 0;
	private int mId;
	private String mName;
	private static Book mAllBook;
	
	private static Map<Integer, Book> mBookMap = new HashMap<>();
	private static Collection<Book> mBooks = new ArrayList<>();
	
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
	
	public static Collection<Book> getBooks(boolean pAllBook) {
		if (!pAllBook) {
			return getBooks();
		}
		Collection<Book> wBooks = new ArrayList<>();
		wBooks.add(getAllBook());
		wBooks.addAll(getBooks());
		return wBooks;
	}
	
	private static Collection<Book> getBooks() {
		if (mBooks.isEmpty()) {
			mBooks = DbUtil.getBooks();
		}
		return mBooks;
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
//		if (getBook(SystemData.getAllBookInt()) == null) {
		if (mAllBook == null) {
			mAllBook = new Book(SystemData.getAllBookInt(), "全て");
			mBookMap.put(mAllBook.getId(), mAllBook);
//			generateBook(SystemData.getAllBookInt(), "全て");
		}
		return mAllBook;
//		return getBook(SystemData.getAllBookInt());
	}
	
	public static void clear() {
		mBooks.clear();
		mBookMap.clear();
	}
	
}
