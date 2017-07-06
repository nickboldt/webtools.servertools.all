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
package org.eclipse.jst.server.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.core.ClasspathRuntimeTargetHandler;

import org.eclipse.wst.server.core.IRuntime;
/**
 * 
 */
public class GenericRuntimeTargetHandler extends ClasspathRuntimeTargetHandler {
	public IClasspathEntry[] getDelegateClasspathEntries(IRuntime runtime) {
		GenericRuntime genericRuntime = (GenericRuntime) runtime.getDelegate();
		IVMInstall vmInstall = genericRuntime.getVMInstall();
		if (vmInstall != null) {
			String name = vmInstall.getName();
			return new IClasspathEntry[] { JavaCore.newContainerEntry(new Path(JavaRuntime.JRE_CONTAINER).append("org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType").append(name)) };
		}
		return null;
	}

	public String getClasspathContainerLabel(IRuntime runtime, String id) {
		return JavaServerPlugin.getResource("%runtimeTargetContainer");
	}
	
	public String[] getClasspathEntryIds(IRuntime runtime) {
		return new String[1];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.target.IServerTargetDelegate#getClasspathEntries()
	 */
	public IClasspathEntry[] resolveClasspathContainer(IRuntime runtime, String id) {
		IPath installPath = runtime.getLocation();
		
		if (installPath == null)
			return new IClasspathEntry[0];
		
		List list = new ArrayList();
		addLibraryEntries(list, installPath.toFile(), false);
		return resolveList(list);
	}

	public String getId() {
		return "org.eclipse.jst.server.core.runtimeTarget";
	}
}
