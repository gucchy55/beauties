package view.config;

import model.ConfigItem;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

public class TreeViewerConfigItem extends TreeViewer {

//	private ConfigItem mSelectedConfigItem;
	
	public TreeViewerConfigItem(Composite pParent, ConfigItem pConfigItem) {
		super(pParent, SWT.NONE);
		
		this.setContentProvider(new TreeContentProvider());

		this.setInput(pConfigItem);
		this.setLabelProvider(new TreeLabelProvider());

		this.setExpandedElements(pConfigItem.getChildren());

		final Tree wTree = this.getTree();
		final TreeEditor wTreeEditor = new TreeEditor(wTree);
		wTreeEditor.grabHorizontal = true;
			
//		this.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent event) {
//				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
//				ConfigItem wConfigItem = (ConfigItem) sel.getFirstElement();
//				wConfigItem.moveUp();
//			}
//		});
		
//		this.addSelectionChangedListener(new ISelectionChangedListener() {
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
//				mSelectedConfigItem = (ConfigItem) sel.getFirstElement();
//				
//			}
//		});
		
		
	}

	protected ConfigItem getSelectedConfigItem() {
		IStructuredSelection sel = (IStructuredSelection) this.getSelection();
		return (ConfigItem) sel.getFirstElement();
	}

}

class TreeContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
		ConfigItem wItem = (ConfigItem) element;
		if (wItem.hasItem()) {
			return wItem.getChildren();
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