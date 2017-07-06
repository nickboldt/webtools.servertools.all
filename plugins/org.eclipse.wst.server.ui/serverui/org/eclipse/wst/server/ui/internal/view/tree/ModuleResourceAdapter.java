package org.eclipse.wst.server.ui.internal.view.tree;
/**
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. � This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *    IBM - Initial API and implementation
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.util.MissingModule;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.ServerLabelProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 *
 */
public class ModuleResourceAdapter implements IAdaptable, IWorkbenchAdapter, IServerElementTag {
	private static final Object[] NO_CHILDREN = new Object[0];

	protected IServer server;
	protected IModule module;
	protected Object parent;

	public ModuleResourceAdapter(Object parent, IServer server, IModule module) {
		super();
		this.parent = parent;
		this.server = server;
		this.module = module;
	}

	/*
	 * @see IAdaptable#getAdapter(Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter.equals(IAdaptable.class))
			return this;
		else if (adapter.equals(IWorkbenchAdapter.class))
			return this;
		else
			return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/*
	 * @see IWorkbenchAdapter#getChildren(Object)
	 */
	public Object[] getChildren(Object o) {
		List childModules = server.getChildModules(module);
		if (childModules == null)
			return NO_CHILDREN;
		Iterator iterator = childModules.iterator();

		List child = new ArrayList();
		while (iterator.hasNext()) {
			IModule module2 = (IModule) iterator.next();
			child.add(new ModuleResourceAdapter(this, server, module2));
		}

		ModuleResourceAdapter[] adapters = new ModuleResourceAdapter[child.size()];
		child.toArray(adapters);
		return adapters;
	}

	/*
	 * @see IWorkbenchAdapter#getImageDescriptor(Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		return ((ServerLabelProvider)ServerUICore.getLabelProvider()).getImageDescriptor(module);
	}

	/*
	 * @see IWorkbenchAdapter#getLabel(Object)
	 */
	public String getLabel(Object o) {
		if (module == null)
			return "";
		else if (module instanceof MissingModule)
			return "(" + module.getName() + ")";
		else
			return module.getName();
	}

	/*
	 * @see IWorkbenchAdapter#getParent(Object)
	 */
	public Object getParent(Object o) {
		return parent;
	}
	
	public IModule getModules() {
		return module;
	}
	
	public IServer getServer() {
		return server;
	}
	
	public int hashCode() {
		int hash = 0;
		if (server != null) {
			IFile file = server.getFile();
			if (file != null)
				hash = file.hashCode();
		}
		if (module != null)
			hash += module.hashCode();
		return hash;
	}
}
