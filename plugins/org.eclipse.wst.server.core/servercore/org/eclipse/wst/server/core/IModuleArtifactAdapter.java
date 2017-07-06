/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;
/**
 * A module object adapter converts from some view's model
 * object into a module object that is recognized by the
 * server.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IModuleArtifactAdapter {
	/**
	 * Returns the id of this adapter. Each known adapter has a distinct id. 
	 * Ids are intended to be used internally as keys; they are not
	 * intended to be shown to end users.
	 * 
	 * @return the adapter id
	 */
	public String getId();

	/**
	 * Returns the (super) class name that this adapter can work with.
	 *
	 * @return java.lang.String
	 */
	public String getObjectClassName();

	/**
	 * Returns true if the plugin that loaded this class has been loaded.
	 *
	 * @return boolean
	 */
	public boolean isPluginActivated();	

	/**
	 * Converts from a model object to an IModuleArtifact.
	 * 
	 * @param obj
	 * @return
	 */
	public IModuleArtifact getModuleObject(Object obj);
}