package org.eclipse.wst.server.ui.internal.actions;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. � This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.IProjectModule;
import org.eclipse.wst.server.core.model.IRestartableModule;
import org.eclipse.wst.server.core.model.IServerDelegate;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;

/**
 * Action delegate for restarting a project within a running
 * server.
 */
public class RestartProjectActionDelegate implements IActionDelegate {
	protected IProject project;

	/**
	 * RestartProjectAction constructor comment.
	 */
	public RestartProjectActionDelegate() {
		super();
	}
	
	/**
	 * Performs this action.
	 * <p>
	 * This method is called when the delegating action has been triggered.
	 * Implement this method to do the actual work.
	 * </p>
	 *
	 * @param action the action proxy that handles the presentation portion of the
	 *   action
	 */
	public void run(IAction action) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Shell shell = EclipseUtil.getShell();
				MessageDialog dialog = new MessageDialog(shell, ServerUIPlugin.getResource("%defaultDialogTitle"), null, ServerUIPlugin.getResource("%dialogRestartingProject", project.getName()), MessageDialog.INFORMATION, new String[0], 0);
				dialog.setBlockOnOpen(false);
				dialog.open();
	
				IProjectModule module = ServerUtil.getModuleProject(project);
				if (module != null) {
					IServer[] servers = ServerUtil.getServersByModule(module);
					if (servers != null) {
						int size2 = servers.length;
						for (int j = 0; j < size2; j++) {
							byte state = servers[j].getServerState();
							IServerDelegate delegate = servers[j].getDelegate();
							if ((state == IServer.SERVER_STARTED || state == IServer.SERVER_STARTED_DEBUG) &&
								(delegate instanceof IRestartableModule)) {
								IRestartableModule restartable = (IRestartableModule)delegate;
								if (restartable.canRestartModule(module)) {
									try {
										restartable.restartModule(module, new NullProgressMonitor());
									} catch (Exception e) {
										Trace.trace("Error restarting project", e);
									}
								}
							}
						}
					}
				}
				dialog.close();
			}
		});
	}
	
	/**
	 * Notifies this action delegate that the selection in the workbench has changed.
	 * <p>
	 * Implementers can use this opportunity to change the availability of the
	 * action or to modify other presentation properties.
	 * </p>
	 *
	 * @param action the action proxy that handles presentation portion of the action
	 * @param selection the current selection in the workbench
	 */
	public void selectionChanged(IAction action, ISelection sel) {
		if (sel.isEmpty() || !(sel instanceof IStructuredSelection)) {
			action.setEnabled(false);
			return;
		}
	
		IStructuredSelection select = (IStructuredSelection) sel;
		Iterator iterator = select.iterator();
		Object selection = iterator.next();
		if (iterator.hasNext() || selection == null) {
			// more than one selection (should never happen)
			action.setEnabled(false);
			return;
		}
	
		if (!(selection instanceof IProject)) {
			action.setEnabled(false);
			return;
		}
	
		project = (IProject) selection;
		if (!project.isOpen()) {
			action.setEnabled(false);
			return;
		}
	
		IProjectModule module = ServerUtil.getModuleProject(project);
		if (module != null) {
			IServer[] servers = ServerUtil.getServersByModule(module);
			if (servers != null) {
				int size2 = servers.length;
				for (int j = 0; j < size2; j++) {
					byte state = servers[j].getServerState();
					IServerDelegate delegate = servers[j].getDelegate();
					if ((state == IServer.SERVER_STARTED || state == IServer.SERVER_STARTED_DEBUG) &&
						(delegate instanceof IRestartableModule)) {
						IRestartableModule restartable = (IRestartableModule)delegate;
						if (restartable.canRestartModule(module)) {
							action.setEnabled(true);
							return;
						}
					}
				}
			}
		}

		action.setEnabled(false);
	}
}
