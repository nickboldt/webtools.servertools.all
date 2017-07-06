package org.eclipse.jst.server.tomcat.core;
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
import org.eclipse.wst.server.core.model.IServerConfigurationWorkingCopyDelegate;

public interface ITomcatConfigurationWorkingCopy extends ITomcatConfiguration, IServerConfigurationWorkingCopyDelegate {
	/**
	 * Add a web module.
	 *
	 * @param index int
	 * @param module org.eclipse.jst.server.tomcat.WebModule
	 */
	public void addWebModule(int index, ITomcatWebModule module);
	
	/**
	 * Change a web module.
	 * @param index int
	 * @param docBase java.lang.String
	 * @param path java.lang.String
	 * @param reloadable boolean
	 */
	public void modifyWebModule(int index, String docBase, String path, boolean reloadable);

	/**
	 * Removes a web module.
	 * @param index int
	 */
	public void removeWebModule(int index);

	/**
	 * Adds a mime mapping.
	 *
	 * @param index int
	 * @param map MimeMapping
	 */
	public void addMimeMapping(int index, IMimeMapping map);

	/**
	 * Change a mime mapping.
	 * @param index int
	 * @param map MimeMapping
	 */
	public void modifyMimeMapping(int index, IMimeMapping map);

	/**
	 * Modify the port with the given id.
	 *
	 * @param id java.lang.String
	 * @param port int
	 */
	public void modifyServerPort(String id, int port);

	/**
	 * Removes a mime mapping.
	 * @param index int
	 */
	public void removeMimeMapping(int index);
}
