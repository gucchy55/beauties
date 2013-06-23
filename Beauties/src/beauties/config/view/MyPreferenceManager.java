package beauties.config.view;

import java.util.Collection;

import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;

import beauties.common.model.Book;

public class MyPreferenceManager extends PreferenceManager {
	
	private PreferenceNode mItemNode;
	private PreferenceNode mBookNode;
	private PreferenceNode mOtherNode;

	public MyPreferenceManager() {
		
		mItemNode = new PreferenceNode("項目設定");
		mItemNode.setPage(new PreferencePageItem(this));
		this.addToRoot(mItemNode);

		mBookNode = new PreferenceNode("帳簿設定");
		mBookNode.setPage(new PreferencePageBook(this));
		this.addToRoot(mBookNode);
		
		mOtherNode = new PreferenceNode("その他");
		mOtherNode.setPage(new PreferencePageSystem());
		this.addToRoot(mOtherNode);
	}
	
	public void updateBooks(Collection<Book> pBooks) {
		PreferencePageItem wItemPage = (PreferencePageItem) mItemNode.getPage();
		wItemPage.dispose();
		mItemNode.setPage(new PreferencePageItem(this));
	}
	
	public void updateItems() {
		PreferencePageBook wBookPage = (PreferencePageBook) mBookNode.getPage();
		wBookPage.dispose();
		mBookNode.setPage(new PreferencePageBook(this));
	}
}
