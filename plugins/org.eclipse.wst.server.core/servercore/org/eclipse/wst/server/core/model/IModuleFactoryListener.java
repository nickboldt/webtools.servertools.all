/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.model;
/**
 * 
 */
public interface IModuleFactoryListener {
	/**
	 * Fired when modules have been added or removed from this factory.
	 *
	 * @param event org.eclipse.wst.server.core.model.ModuleFactoryEvent
	 */
	public void moduleFactoryChanged(ModuleFactoryEvent event);
}