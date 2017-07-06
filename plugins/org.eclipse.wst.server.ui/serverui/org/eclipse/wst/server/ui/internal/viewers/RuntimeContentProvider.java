package org.eclipse.wst.server.ui.internal.viewers;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
/**
 * Runtime content provider.
 */
public class RuntimeContentProvider implements IStructuredContentProvider {
	/**
	 * RuntimeContentProvider constructor comment.
	 */
	public RuntimeContentProvider() {
		super();
	}

	/**
	 * Disposes of this content provider.  
	 * This is called by the viewer when it is disposed.
	 */
	public void dispose() { }

	/**
	 * Returns the elements to display in the viewer 
	 * when its input is set to the given element. 
	 */
	public Object[] getElements(Object inputElement) {
		List list = new ArrayList();
		Iterator iterator = ServerCore.getResourceManager().getRuntimes().iterator();
		while (iterator.hasNext()) {
			IRuntime runtime = (IRuntime) iterator.next();
			if (!runtime.isPrivate())
				list.add(runtime);
		}
		return list.toArray();
	}

	/**
	 * Notifies this content provider that the given viewer's input
	 * has been switched to a different element.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
}
