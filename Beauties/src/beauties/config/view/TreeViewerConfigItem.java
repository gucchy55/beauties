package beauties.config.view;


import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import beauties.config.model.ConfigItem;

public class TreeViewerConfigItem extends TreeViewer {

	public TreeViewerConfigItem(Composite pParent, ConfigItem pConfigItem) {
		super(pParent, SWT.VIRTUAL);
		
		this.setContentProvider(new TreeContentProvider());

		this.setInput(pConfigItem);
		this.setLabelProvider(new TreeLabelProvider());

		this.setExpandedElements(pConfigItem.getChildren().toArray(new Object[0]));

		final Tree wTree = this.getTree();
		final TreeEditor wTreeEditor = new TreeEditor(wTree);
		wTreeEditor.grabHorizontal = true;
	}

	protected ConfigItem getSelectedConfigItem() {
		if (this.getSelection().isEmpty()) {
			return new ConfigItem("");
		}
		IStructuredSelection sel = (IStructuredSelection) this.getSelection();
		return (ConfigItem) sel.getFirstElement();
	}

}

class TreeContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object element) {
		ConfigItem wItem = (ConfigItem) element;
		if (wItem.hasItem()) {
			List<?> wList = wItem.getChildren();
			return wList.toArray();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		ConfigItem wItem = (ConfigItem) element;
		if (wItem.hasParent()) {
			return wItem.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		ConfigItem wItem = (ConfigItem) element;
		return wItem.hasItem();
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}

class TreeLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		ConfigItem wItem = (ConfigItem) element;
		return wItem.getName();
	}

}