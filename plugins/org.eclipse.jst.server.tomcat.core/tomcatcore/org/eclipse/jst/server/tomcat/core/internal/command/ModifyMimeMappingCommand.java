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
package org.eclipse.jst.server.tomcat.core.internal.command;

import org.eclipse.jst.server.tomcat.core.internal.ITomcatConfigurationWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.MimeMapping;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
/**
 * Command to change a mime type extension.
 */
public class ModifyMimeMappingCommand extends ConfigurationCommand {
	protected int index;
	protected MimeMapping oldMap;
	protected MimeMapping newMap;

	/**
	 * A command to modify a mime mapping.
	 * 
	 * @param configuration a tomcat configuration
	 * @param index an index
	 * @param map a mime mapping
	 */
	public ModifyMimeMappingCommand(ITomcatConfigurationWorkingCopy configuration, int index, MimeMapping map) {
		super(configuration);
		this.index = index;
		newMap = map;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		oldMap = (MimeMapping) configuration.getMimeMappings().get(index);
		configuration.modifyMimeMapping(index, newMap);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return TomcatPlugin.getResource("%configurationEditorActionModifyMimeMappingDescription");
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return TomcatPlugin.getResource("%configurationEditorActionModifyMimeMapping");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		configuration.modifyMimeMapping(index, oldMap);
	}
}