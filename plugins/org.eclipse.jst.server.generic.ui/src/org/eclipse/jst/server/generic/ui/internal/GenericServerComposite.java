package org.eclipse.jst.server.generic.ui.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class GenericServerComposite extends Composite {

	private GenericServerCompositeDecorator[] fDecorators;
	public GenericServerComposite(Composite parent, GenericServerCompositeDecorator[] decorators) {
		super(parent, SWT.NONE);
		fDecorators = decorators;
        setLayoutData(new GridData(GridData.FILL_BOTH));
        setLayout(new GridLayout(3,false));
		createControl();
	}

	private void createControl(){
		for (int i = 0; fDecorators!= null && i < fDecorators.length; i++) {
			fDecorators[i].decorate(this);
		}
	}
}