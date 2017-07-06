/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
�* 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.*;
/**
 * 
 */
public class LaunchClientJob extends Job {
	protected IServer server;
	protected IModule module;
	protected IClient client;
	protected ILaunchableAdapter launchableAdapter;
	protected String launchMode;
	protected IModuleArtifact moduleArtifact;

	public static void launchClient(IServer server, IModule module, String launchMode, IModuleArtifact moduleArtifact, ILaunchableAdapter launchableAdapter, IClient client) {
		LaunchClientJob job = new LaunchClientJob(server, module, launchMode, moduleArtifact, launchableAdapter, client);
		//job.setUser(true);
		job.schedule();
	}

	public LaunchClientJob(IServer server, IModule module, String launchMode, IModuleArtifact moduleArtifact, ILaunchableAdapter launchableAdapter, IClient client) {
		super("Launch client");
		this.server = server;
		this.module = module;
		this.launchMode = launchMode;
		this.moduleArtifact = moduleArtifact;
		this.launchableAdapter = launchableAdapter;
		this.client = client;
		setRule(new ServerSchedulingRule(server));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		Trace.trace(Trace.FINER, "LaunchClient job");
		IStatus status = new Status(IStatus.OK, ServerUIPlugin.PLUGIN_ID, 0, "", null);

		// wait for up to 5 minutes
		int state = server.getModuleState(module);
		int count = ServerPreferences.getInstance().getModuleStartTimeout();
		while (state == IServer.STATE_STARTING && count > 0) {
			if (monitor.isCanceled())
				return status;
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				// ignore
			}
			count -= 2000;
			state = server.getModuleState(module);
		}
		
		Trace.trace(Trace.FINER, "LaunchClient job 2 " + state);
		
		if (monitor.isCanceled())
			return status;
		
		if (state == IServer.STATE_STARTING)
			return status;
		
		Trace.trace(Trace.FINER, "LaunchClient job 3");
		
		// display client on UI thread
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Trace.trace(Trace.FINEST, "Attempting to load client: " + client);
				try {
					ILaunchable launchable = launchableAdapter.getLaunchable(server, moduleArtifact);
					client.launch(server, launchable, launchMode, server.getExistingLaunch());
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Server client failed", e);
				}
			}
		});
		Trace.trace(Trace.FINER, "LaunchClient job 4");
		return status;
	}
}