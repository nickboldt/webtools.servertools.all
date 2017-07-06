/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
�*
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core;

import org.eclipse.jdt.launching.IVMInstall;
/**
 *
 */
public interface ITomcatRuntimeWorkingCopy extends ITomcatRuntime {
	/**
	 * Set the VM install (installed JRE) that this runtime is using.
	 * 
	 * @param vmInstall the VM install to use
	 */
	public void setVMInstall(IVMInstall vmInstall);
}