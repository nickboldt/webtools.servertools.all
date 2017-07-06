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

import org.eclipse.jst.server.tomcat.core.internal.ITomcatServerWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
/**
 * Command to change the server security option.
 */
public class SetSecureCommand extends ServerCommand {
	protected boolean secure;
	protected boolean oldSecure;

	/**
	 * SetSecureCommand constructor comment.
	 * 
	 * @param server a Tomcat server
	 * @param secure <code>true</code> for security on
	 */
	public SetSecureCommand(ITomcatServerWorkingCopy server, boolean secure) {
		super(server);
		this.secure = secure;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		oldSecure = server.isSecure();
		server.setSecure(secure);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return TomcatPlugin.getResource("%serverEditorActionSetSecureDescription");
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return TomcatPlugin.getResource("%serverEditorActionSetSecure");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setSecure(oldSecure);
	}
}