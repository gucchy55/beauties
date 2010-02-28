package view.config;

import model.ConfigItem;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class TreeViewerConfigItem extends TreeViewer {

	public TreeViewerConfigItem(Composite pParent, ConfigItem pConfigItem) {
		super(pParent, SWT.NONE);

		this.setContentProvider(new TreeContentProvider());
		
		this.setInput(pConfigItem);
		this.setLabelProvider(new TreeLabelProvider());

	}

}

class TreeContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
		ConfigItem wItem = (ConfigItem) element;
		if (wItem.hasItem()) {
			return wItem.getItems();
		}
		return new Object[0];
	}

	public Object getParent(Object element) {
		ConfigItem wItem = (ConfigItem) element;
		if (wItem.hasParent()) {
			return wItem.getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		ConfigItem wItem = (ConfigItem) element;
		return wItem.hasItem();
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}

class TreeLabelProvider extends LabelProvider {
	public String getText(Object element) {
		ConfigItem wItem = (ConfigItem) element;
		return wItem.getName();
	}

}