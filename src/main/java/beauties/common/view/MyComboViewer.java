package beauties.common.view;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import beauties.common.model.IComboItem;

public class MyComboViewer<T extends IComboItem> extends ComboViewer {

	public MyComboViewer(Composite parent, int style) {
		super(parent, style);
		super.setContentProvider(ArrayContentProvider.getInstance());
		super.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object pElement) {
				@SuppressWarnings("unchecked")
				T wElement = (T) pElement;
				return wElement.getName();
			}
		});
	}
	
	public void setInput(List<T> pList) {
		super.setInput(pList);
	}
	
	public void setSelection(T pElement) {
		super.setSelection(new StructuredSelection(pElement));
	}
	
	@SuppressWarnings("unchecked")
	public T getSelectedItem() {
		IStructuredSelection wSelection = (IStructuredSelection)super.getSelection();
		T wSelectedItem = (T) wSelection.getFirstElement();
		return wSelectedItem;
	}

}
