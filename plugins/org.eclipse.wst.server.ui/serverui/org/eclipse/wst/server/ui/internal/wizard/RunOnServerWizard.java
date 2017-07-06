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
package org.eclipse.wst.server.ui.internal.wizard;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.core.internal.ILaunchableAdapter;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.wizard.fragment.RunOnServerWizardFragment;
/**
 * A wizard used for Run on Server.
 */
public class RunOnServerWizard extends TaskWizard {
	/**
	 * RunOnServerWizard constructor comment.
	 * 
	 * @param module a module
	 * @param launchMode a launch mode
	 * @param moduleArtifact a module artifact
	 */
	public RunOnServerWizard(IModule module, String launchMode, IModuleArtifact moduleArtifact) {
		super(Messages.wizRunOnServerTitle, new RunOnServerWizardFragment(module, launchMode, moduleArtifact));
		
		setNeedsProgressMonitor(true);
		if (ILaunchManager.DEBUG_MODE.equals(launchMode))
			setWindowTitle(Messages.wizDebugOnServerTitle);
		else if (ILaunchManager.PROFILE_MODE.equals(launchMode))
			setWindowTitle(Messages.wizProfileOnServerTitle);
		getTaskModel().putObject(TaskModel.TASK_LAUNCH_MODE, launchMode);
	}

	/**
	 * RunOnServerWizard constructor comment.
	 * 
	 * @param server a server
	 * @param launchMode a launch mode
	 * @param moduleArtifact a module artifact
	 */
	public RunOnServerWizard(IServer server, String launchMode, IModuleArtifact moduleArtifact) {
		super(Messages.wizRunOnServerTitle, new RunOnServerWizardFragment(server, launchMode, moduleArtifact));
		
		setNeedsProgressMonitor(true);
		if (ILaunchManager.DEBUG_MODE.equals(launchMode))
			setWindowTitle(Messages.wizDebugOnServerTitle);
		else if (ILaunchManager.PROFILE_MODE.equals(launchMode))
			setWindowTitle(Messages.wizProfileOnServerTitle);
		
		getTaskModel().putObject(TaskModel.TASK_SERVER, server);
		getTaskModel().putObject(TaskModel.TASK_LAUNCH_MODE, launchMode);
		addPages();
	}

	/**
	 * Return the server.
	 * 
	 * @return the server
	 */
	public IServer getServer() {
		try {
			return (IServer) getTaskModel().getObject(TaskModel.TASK_SERVER);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return if the user wants to use the server as a default.
	 * 
	 * @return true if the server should be the default
	 */
	public boolean isPreferredServer() {
		try {
			Boolean b = (Boolean) getTaskModel().getObject(WizardTaskUtil.TASK_DEFAULT_SERVER);
			return b.booleanValue();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Return the selected client.
	 * 
	 * @return the client
	 */
	public IClient getSelectedClient() {
		try {
			return (IClient) getTaskModel().getObject(WizardTaskUtil.TASK_CLIENT);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return the launchable adapter.
	 * 
	 * @return the adapter
	 */
	public ILaunchableAdapter getLaunchableAdapter() {
		try {
			return (ILaunchableAdapter) getTaskModel().getObject(WizardTaskUtil.TASK_LAUNCHABLE_ADAPTER);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns true if this wizard should be shown to the user.
	 * 
	 * @return <code>true</code> if this wizard should be shown, and <code>false</code>
	 *    otherwise
	 */
	public boolean shouldAppear() {
		return getServer() == null || hasTasks() || hasClients();
	}

	/**
	 * Return <code>true</code> if this wizard has tasks.
	 * 
	 * @return <code>true</code> if this wizard has tasks, and <code>false</code>
	 *    otherwise
	 */
	protected boolean hasTasks() {
		try {
			Boolean b = (Boolean) getTaskModel().getObject(WizardTaskUtil.TASK_HAS_TASKS);
			return b.booleanValue();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Return <code>true</code> if this wizard has multiple clients to show.
	 * 
	 * @return <code>true</code> if this wizard has multiple clients, and <code>false</code>
	 *    otherwise
	 */
	protected boolean hasClients() {
		try {
			Boolean b = (Boolean) getTaskModel().getObject(WizardTaskUtil.TASK_HAS_CLIENTS);
			return b.booleanValue();
		} catch (Exception e) {
			return false;
		}
	}
}