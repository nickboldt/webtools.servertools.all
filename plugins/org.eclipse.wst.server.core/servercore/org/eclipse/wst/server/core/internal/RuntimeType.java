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
package org.eclipse.wst.server.core.internal;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.server.core.*;
/**
 * 
 */
public class RuntimeType implements IRuntimeType {
	private IConfigurationElement element;
	private List moduleTypes;

	public RuntimeType(IConfigurationElement element) {
		super();
		this.element = element;
	}
	
	protected IConfigurationElement getElement() {
		return element;
	}

	/**
	 * 
	 * @return
	 */
	public String getId() {
		return element.getAttribute("id");
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return element.getAttribute("name");
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return element.getAttribute("description");
	}
	
	/**
	 * Returns the order.
	 *
	 * @return int
	 */
	public int getOrder() {
		try {
			String o = element.getAttribute("order");
			return Integer.parseInt(o);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public String getVendor() {
		String vendor = element.getAttribute("vendor");
		if (vendor == null)
			return ServerPlugin.getResource("%defaultVendor");
		else
			return vendor;
	}
	
	public String getVersion() {
		String version = element.getAttribute("version");
		if (version == null)
			return ServerPlugin.getResource("%defaultVersion");
		else
			return version;
	}
	
	/**
	 * 
	 * @return
	 */
	public List getModuleTypes() {
		if (moduleTypes == null)
			moduleTypes = ServerPlugin.getModuleTypes(element.getChildren("moduleType"));

		return moduleTypes;
	}
	
	public boolean canCreate() {
		String a = element.getAttribute("class");
		String b = element.getAttribute("workingCopyClass");
		return a != null && b != null && a.length() > 0 && b.length() > 0;
	}

	public IRuntimeWorkingCopy createRuntime(String id) {
		if (id == null || id.length() == 0)
			id = ServerPlugin.generateId();
		RuntimeWorkingCopy rwc = new RuntimeWorkingCopy(null, id, this);
		rwc.setDefaults();
		return rwc;
	}

	public String toString() {
		return "RuntimeType[" + getId() + ", " + getName() + "]";
	}
}