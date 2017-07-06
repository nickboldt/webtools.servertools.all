/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.xml.server32;

import org.eclipse.jst.server.tomcat.core.internal.xml.*;
/**
 * 
 */
public class Server extends XMLElement {
	public Server() {
		// do nothing
	}
	
	public ContextManager getContextManager() {
		return (ContextManager) findElement("ContextManager");
	}
	
	public int getLoggerCount() {
		return sizeOfElement("Logger");
	}

	public String getName() {
		return getAttributeValue("name");
	}
	
	public void setName(String name) {
		setAttributeValue("name", name);
	}
}