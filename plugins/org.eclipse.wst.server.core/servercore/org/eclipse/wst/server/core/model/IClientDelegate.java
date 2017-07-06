/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.wst.server.core.IServer;
/**
 * A launchable client is a client side application or test
 * harness that can be launched (run) against a resource
 * running on a server.
 * 
 * <p>This is the implementation of a launchableClient
 * extension point.</p>
 */
public interface IClientDelegate {
	/**
	 * Returns true if this launchable can be run by this client.
	 * 
	 * @param server org.eclipse.wst.server.core.model.IServer
	 * @param launchable org.eclipse.wst.server.core.model.ILaunchable
	 * @param launchMode String
	 * @return boolean
	 */
	public boolean supports(IServer server, ILaunchable launchable, String launchMode);

	/**
	 * Opens or executes on the launchable.
	 * 
	 * @param server org.eclipse.wst.server.core.model.IServer
	 * @param launchable org.eclipse.wst.server.core.model.ILaunchable
	 * @param launchMode String
	 * @param launch org.eclipse.debug.core.ILaunch
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public IStatus launch(IServer server, ILaunchable launchable, String launchMode, ILaunch launch);
}
