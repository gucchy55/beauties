package beauties.common.view;

import java.util.LinkedHashMap;
import java.util.Map;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import beauties.common.lib.SystemData;


class CompositeBookNames extends Composite {

	private Map<Integer, String> mBookNameMap;
	private Map<Integer, Button> mBookButtonMap;
	private int mBookId;

	CompositeBookNames(Composite pParent, int pBookId) {
		super(pParent, SWT.NONE);

		mBookNameMap = SystemData.getBookMap(true);
		mBookButtonMap = new LinkedHashMap<Integer, Button>();
		mBookId = pBookId;

		this.setLayout(new MyRowLayout().getMyRowLayout());
		this.setLayoutData(new MyGridData(GridData.FILL, GridData.FILL, true, true).getMyGridData());

		for (Map.Entry<Integer, String> entry : mBookNameMap.entrySet()) {
			int wBookId = entry.getKey();
			Button wBookButton = new Button(this, SWT.TOGGLE);
			wBookButton.setText(entry.getValue());
			mBookButtonMap.put(wBookId, wBookButton);

			if (mBookId == wBookId) {
				wBookButton.setSelection(true);
			}
		}
	}
	
	Map<Integer, Button> getBookButtonMap() {
		return mBookButtonMap;
	}

}
