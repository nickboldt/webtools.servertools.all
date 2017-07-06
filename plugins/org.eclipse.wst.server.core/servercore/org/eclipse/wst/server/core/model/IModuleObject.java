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

import org.eclipse.wst.server.core.model.IModule;
/**
 * A module object is a resource within a module,
 * which can be launched on the server. Examples of module
 * objects could include servlets, HTML pages, or EJB beans.
 */
public interface IModuleObject {
	/**
	 * Returns the id of this module object.
	 * 
	 * @return java.lang.String
	 */
	public String getId();

	/**
	 * Returns the module that this object is a part of.
	 * 
	 * @return org.eclipse.wst.server.core.model.IModule
	 */
	public IModule getModule();
}
