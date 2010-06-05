package beauties.config.view;

import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;

public class MyPreferenceManager extends PreferenceManager {
	
	private PreferenceNode mItemNode;
	private PreferenceNode mBookNode;
	private PreferenceNode mOtherNode;

	public MyPreferenceManager() {
		
		mItemNode = new PreferenceNode("項目設定");
		mItemNode.setPage(new PreferencePageItem());
		this.addToRoot(mItemNode);

		mBookNode = new PreferenceNode("帳簿設定");
		mBookNode.setPage(new PreferencePageBook());
		this.addToRoot(mBookNode);
		
		mOtherNode = new PreferenceNode("その他");
		mOtherNode.setPage(new PreferencePageSystem());
		this.addToRoot(mOtherNode);
	}
	
}
