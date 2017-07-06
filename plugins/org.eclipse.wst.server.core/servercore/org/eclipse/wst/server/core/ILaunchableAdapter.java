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
package org.eclipse.wst.server.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.model.*;
/**
 * This interface, typically implemented by the server
 * code, converts from an IModuleObject to an
 * ILaunchable.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface ILaunchableAdapter {
	/**
	 * Returns the id of the adapter.
	 *
	 * @return java.lang.String
	 */
	public String getId();

	/**
	 * Returns the delegate for this launchable adapter.
	 * 
	 * @return org.eclipse.wst.server.core.model.ILaunchableAdapterDelegate
	 */
	public ILaunchableAdapterDelegate getDelegate();

	/**
	 * Returns a launchable object from this module object.
	 * 
	 * @param server org.eclipse.wst.server.core.model.IServer
	 * @param moduleObject org.eclipse.wst.server.core.model.IModuleObject
	 * @param org.eclipse.wst.server.core.model.ILaunchable
	 * @exception org.eclipse.core.runtime.CoreException
	 */
	public ILaunchable getLaunchable(IServer server, IModuleObject moduleObject) throws CoreException;
}
