package org.eclipse.wst.server.ui.internal;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. � This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.model.IProjectModule;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Dialog that prompts a user to change the target runtime.
 */
public class RuntimeTargetComposite {
	public interface RuntimeSelectionListener {
		public void runtimeSelected(IRuntime runtime);
	}

	protected IProject project;
	protected IProjectProperties props;
	protected IRuntime currentRuntime;
	protected IRuntime newRuntime;
	protected List targets;
	protected String[] items;
	protected RuntimeSelectionListener listener;
	
	protected List childProjects;
	protected boolean setChildren = true;
	protected int offset = 0;

	/**
	 * RuntimeTargetComposite constructor comment.
	 * @param parent Composite
	 * @param project IProject
	 */
	public RuntimeTargetComposite(Composite parent, IProject project) {
		this.project = project;
		props = ServerCore.getProjectProperties(project);
		currentRuntime = props.getRuntimeTarget();
		if (currentRuntime == null)
			offset = 1;
		
		// get child modules
		IProjectModule projectModule = ServerUtil.getModuleProject(project);
		List children = new ArrayList();
		IModule[] child = projectModule.getChildModules();
		if (child != null) {
			int size = child.length;
			for (int i = 0; i < size; i++)
				children.add(child[i]);
			int a = 0;
			while (a < children.size()) {
				IModule module = (IModule) children.get(a);
				IModule[] child2 = module.getChildModules();
				if (child2 != null) {
					size = child2.length;
					for (int i = 0; i < size; i++)
						children.add(child2[i]);
				}
				a++;
			}
		}
		
		childProjects = new ArrayList();
		Iterator iterator = children.iterator();
		while (iterator.hasNext()) {
			IModule module = (IModule) iterator.next();
			if (module instanceof IProjectModule)
				childProjects.add(module);
		}
		
		createContents(parent);
	}
	
	/**
	 * RuntimeTargetComposite constructor comment.
	 * @param parentShell org.eclipse.swt.widgets.Shell
	 * @param project IProject
	 */
	public RuntimeTargetComposite(Composite parent, IProject project, RuntimeSelectionListener listener) {
		this(parent, project);
		this.listener = listener;
	}

	/**
	 * 
	 */
	protected void createContents(final Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(ServerUIPlugin.getResource("%runtimeTargetCombo"));
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		label.setLayoutData(data);
		
		final Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL);
		combo.setLayoutData(data);
		
		int sel = updateRuntimes();
		combo.setItems(items);
		if (items.length == 0)
			combo.setEnabled(false);
		else {
			combo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					int select = combo.getSelectionIndex();
					if (offset > 0 && select == 0)
						newRuntime = null;
					else
						newRuntime = (IRuntime) targets.get(select - offset);
					if (listener != null)
						listener.runtimeSelected(newRuntime);
				}
			});
			if (sel >= 0) {
				combo.select(sel);
				if (offset == 0 || sel > 0)
					newRuntime = (IRuntime) targets.get(sel - offset);
			} else
				combo.select(0);
		}

		final IProjectModule projectModule = ServerUtil.getModuleProject(project);
		
		Button button = SWTUtil.createButton(parent, ServerUIPlugin.getResource("%runtimeTargetNewRuntime"));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String currentRuntime2 = combo.getText();
				String type = null;
				String version = null;
				if (projectModule != null) {
					type = projectModule.getType();
					version = projectModule.getVersion();
				}
				if (ServerUIUtil.showNewRuntimeWizard(parent.getShell(), type, version)) {
					int sel2 = updateRuntimes();
					combo.setItems(items);
					combo.setText(currentRuntime2);
					if (combo.getSelectionIndex() == -1)
						combo.select(sel2);
				}
			}
		});
		
		/*label = new Label(parent, SWT.NONE);
		label.setText("");
		
		label = new Label(parent, SWT.WRAP);
		label.setText(ServerUIPlugin.getResource("%runtimeTargetInformation"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		label.setLayoutData(data);*/
		
		// child module selection
		if (!childProjects.isEmpty()) {
			final Button includeChildren = new Button(parent, SWT.CHECK);
			includeChildren.setText(ServerUIPlugin.getResource("%runtimeTargetChildren"));
			data = new GridData();
			data.horizontalSpan = 3;
			includeChildren.setLayoutData(data);
			includeChildren.setSelection(true);
			
			includeChildren.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					setChildren = includeChildren.getSelection();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
		}
	}
	
	protected int updateRuntimes() {
		IProjectModule pm = ServerUtil.getModuleProject(project);
		if (pm != null)
			targets = ServerUtil.getRuntimes(pm.getType(), pm.getVersion());

		items = new String[0];
		int sel = -1;
		if (targets != null) {
			int size = targets.size();
			items = new String[size + offset];
			if (offset > 0) {
				items[0] = ServerUIPlugin.getResource("%runtimeTargetNone");
				sel = 0;
			}
			for (int i = 0; i < size; i++) {
				IRuntime target = (IRuntime) targets.get(i);
				items[i+offset] = target.getName();
				if (target.equals(currentRuntime))
					sel = i;
			}
		}
		return sel;
	}
	
	public IRuntime getSelectedRuntime() {
		return newRuntime;
	}
	
	public boolean hasChanged() {
		if (!childProjects.isEmpty())
			return true;
		if (newRuntime == null)
			return false;
		if (newRuntime.equals(currentRuntime))
			return false;
		return true;
	}
	
	public void apply(IProgressMonitor monitor) throws CoreException {
		props.setRuntimeTarget(newRuntime, monitor);
		
		if (setChildren) {
			Iterator iterator = childProjects.iterator();
			while (iterator.hasNext()) {
				IProjectModule module = (IProjectModule) iterator.next();
				IProject proj = module.getProject();
				props = ServerCore.getProjectProperties(proj);
				props.setRuntimeTarget(newRuntime, monitor);
			}
		}
	}
}