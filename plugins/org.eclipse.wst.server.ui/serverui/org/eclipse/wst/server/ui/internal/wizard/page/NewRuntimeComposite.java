/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.internal.ContextIds;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.SWTUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.viewers.RuntimeTypeComposite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
/**
 * 
 */
public class NewRuntimeComposite extends Composite {
	protected Tree tree;
	protected TreeViewer treeViewer;
	protected Button createServer;

	protected IRuntimeWorkingCopy runtime;

	// cache of created runtimes and servers
	protected Map<IRuntimeType, IRuntimeWorkingCopy> cache = new HashMap<IRuntimeType, IRuntimeWorkingCopy>();
	protected Map<IRuntime, IServerWorkingCopy> serverCache = new HashMap<IRuntime, IServerWorkingCopy>();

	protected TaskModel taskModel;
	protected IWizardHandle wizard;

	protected String type;
	protected String version;
	protected String runtimeTypeId;
	protected IServerType serverType;

	public NewRuntimeComposite(Composite parent, IWizardHandle wizard, TaskModel tm, String type, String version, String runtimeTypeId) {
		super(parent, SWT.NONE);
		
		this.wizard = wizard;
		this.taskModel = tm;
		this.type = type;
		this.version = version;
		this.runtimeTypeId = runtimeTypeId;
		
		createControl();
		
		wizard.setTitle(Messages.wizNewRuntimeTitle);
		wizard.setDescription(Messages.wizNewRuntimeDescription);
		wizard.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_NEW_RUNTIME));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl() {
		//initializeDialogUnits(parent);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		setLayout(layout);
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(this, ContextIds.NEW_RUNTIME_WIZARD);
		
		final RuntimeTypeComposite comp = new RuntimeTypeComposite(this, true, new RuntimeTypeComposite.RuntimeTypeSelectionListener() {
			public void runtimeTypeSelected(IRuntimeType runtimeType) {
				handleSelection(runtimeType);
			}
		}, type, version, runtimeTypeId);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 300;
		comp.setLayoutData(data);
		
		createServer = new Button(this, SWT.CHECK);
		createServer.setText(Messages.wizNewRuntimeCreateServer);
		createServer.setSelection(ServerUIPlugin.getPreferences().getCreateServerWithRuntime());
		createServer.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleServer();
				ServerUIPlugin.getPreferences().setCreateServerWithRuntime(createServer.getSelection());
			}
		});
	}

	protected void handleSelection(IRuntimeType runtimeType) {
		if (runtimeType == null)
			runtime = null;
		else {
			try {
				runtime = null;
				runtime = cache.get(runtimeType);
			} catch (Exception e) {
				// ignore
			}
			if (runtime == null) {
				try {
					runtime = runtimeType.createRuntime(null, null);
					ServerUtil.setRuntimeDefaultName(runtime);
					if (runtime != null)
						cache.put(runtimeType, runtime);
				} catch (Exception e) {
					// ignore
				}
			}
		}
		serverType = getCompatibleServerType(runtimeType);
		handleServer();
	}

	protected void handleServer() {
		boolean option = false;
		if (serverType != null && serverType.hasRuntime())
			option = true;
		createServer.setVisible(option);
		
		if (option && createServer.getSelection()) {
			IServerWorkingCopy server = getServer();
			taskModel.putObject(TaskModel.TASK_SERVER, server);
		} else
			taskModel.putObject(TaskModel.TASK_SERVER, null);
		
		taskModel.putObject(TaskModel.TASK_RUNTIME, runtime);
		wizard.update();
	}

	protected static IServerType getCompatibleServerType(IRuntimeType runtimeType) {
		List<IServerType> list = new ArrayList<IServerType>();
		IServerType[] serverTypes = ServerCore.getServerTypes();
		int size = serverTypes.length;
		for (int i = 0; i < size; i++) {
			IRuntimeType rt = serverTypes[i].getRuntimeType();
			if (rt != null && rt.equals(runtimeType))
				list.add(serverTypes[i]);
		}
		if (list.size() == 1)
			return list.get(0);
		return null;
	}

	/**
	 * Get a server of the given type.
	 */
	protected IServerWorkingCopy getServer() {
		if (serverType == null || runtime == null || !serverType.hasRuntime())
			return null;
		
		IServerWorkingCopy server = serverCache.get(runtime);
		if (server != null)
			return server;
		
		try {
			server = serverType.createServer(null, null, runtime, null);
			if (server != null) {
				server.setHost("localhost");
				ServerUtil.setServerDefaultName(server);
				serverCache.put(runtime, server);
				return server;
			}
		} catch (CoreException ce) {
			Trace.trace(Trace.SEVERE, "Error creating server", ce);
		}
		
		return null;
	}

	public IRuntimeWorkingCopy getRuntime() {
		return runtime;
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		Control[] c = getChildren();
		if (c != null) {
			int size = c.length;
			for (int i = 0; i < size; i++)
				if (c[i] != null)
					c[i].setVisible(visible);
		}
	}
}