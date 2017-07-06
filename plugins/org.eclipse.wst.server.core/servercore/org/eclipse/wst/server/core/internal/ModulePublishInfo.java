/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.model.IModuleFile;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.ModuleDelegate;
/**
 * Publish information for a specific module on a specific server.
 */
public class ModulePublishInfo {
	private static final String MODULE_ID = "module-ids";
	private static final String NAME = "name";
	private static final String MODULE_TYPE_ID = "module-type-id";
	private static final String MODULE_TYPE_VERSION = "module-type-version";
	private static final String STAMP = "stamp";
	private static final String FILE = "file";
	private static final String FOLDER = "folder";

	private String moduleId;
	private String name;
	private IModuleResource[] resources = new IModuleResource[0];
	private IModuleType moduleType;

	private boolean useCache;
	private IModuleResource[] currentResources = null;
	private IModuleResourceDelta[] delta = null;
	private boolean hasDelta;

	/**
	 * ModulePublishInfo constructor.
	 * 
	 * @param moduleId a module id
	 * @param name the module's name
	 * @param moduleType the module type
	 */
	public ModulePublishInfo(String moduleId, String name, IModuleType moduleType) {
		super();

		this.moduleId = moduleId;
		this.name = name;
		this.moduleType = moduleType;
	}

	/**
	 * ModulePublishInfo constructor.
	 * 
	 * @param memento a memento
	 */
	public ModulePublishInfo(IMemento memento) {
		super();
		
		load(memento);
	}

	/**
	 * ModulePublishInfo constructor.
	 * 
	 * @param in an input stream
	 * @throws IOException if the load fails
	 */
	public ModulePublishInfo(DataInput in) throws IOException {
		super();
		
		load(in);
	}

	public String getModuleId() {
		return moduleId;
	}

	public String getName() {
		return name;
	}

	public IModuleType getModuleType() {
		return moduleType;
	}

	public IModuleResource[] getResources() {
		return resources;
	}

	public void setResources(IModuleResource[] res) {
		resources = res;
	}

	/**
	 * Used only for reading from WTP 1.x workspaces.
	 */
	protected void load(IMemento memento) {
		Trace.trace(Trace.FINEST, "Loading module publish info for: " + memento);
		
		try {
			moduleId = memento.getString(MODULE_ID);
			name = memento.getString(NAME);
			String mt = memento.getString(MODULE_TYPE_ID);
			String mv = memento.getString(MODULE_TYPE_VERSION);
			if (mt != null && mt.length() > 0)
				moduleType = ModuleType.getModuleType(mt, mv);
			
			resources = loadResource(memento, new Path(""));
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load module publish info information: " + e.getMessage());
		}
	}

	/**
	 * Used only for reading from WTP 1.x workspaces.
	 */
	protected IModuleResource[] loadResource(IMemento memento, IPath path) {
		if (memento == null)
			return new IModuleResource[0];
		
		List list = new ArrayList(10);
		
		// load files
		IMemento[] children = memento.getChildren(FILE);
		if (children != null) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				String name2 = children[i].getString(NAME);
				long stamp = Long.parseLong(children[i].getString(STAMP));
				ModuleFile file = new ModuleFile(name2, path, stamp);
				list.add(file);
			}
		}
		
		// load folders
		children = memento.getChildren(FOLDER);
		if (children != null) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				String name2 = children[i].getString(NAME);
				ModuleFolder folder = new ModuleFolder(null, name2, path);
				folder.setMembers(loadResource(children[i], path.append(name2)));
				list.add(folder);
			}
		}
		
		IModuleResource[] resources2 = new IModuleResource[list.size()];
		list.toArray(resources2);
		return resources2;
	}

	protected void load(DataInput in) throws IOException {
		Trace.trace(Trace.FINEST, "Loading module publish info");
		
		moduleId = in.readUTF();
		byte b = in.readByte();
		
		if ((b & 1) != 0)
			name = in.readUTF();
		else
			name = null;
		
		if ((b & 2) != 0) {
			String mt = in.readUTF();
			String mv = in.readUTF();
			if (mt != null && mt.length() > 0)
				moduleType = ModuleType.getModuleType(mt, mv);
		} else
			moduleType = null;
		
		resources = loadResource(in, new Path(""));
	}

	private IModuleResource[] loadResource(DataInput in, IPath path) throws IOException {
		int size = in.readInt();
		IModuleResource[] resources2 = new IModuleResource[size];
		
		for (int i = 0; i < size; i++) {
			byte b = in.readByte();
			if (b == 0) {
				String name2 = in.readUTF();
				long stamp = in.readLong();
				resources2[i] = new ModuleFile(name2, path, stamp);
			} else if (b == 1) {
				String name2 = in.readUTF();
				ModuleFolder folder = new ModuleFolder(null, name2, path);
				folder.setMembers(loadResource(in, path.append(name2)));
				resources2[i] = folder;
			}
		}
		
		return resources2;
	}

	protected void save(DataOutput out) {
		try {
			out.writeUTF(moduleId);
			byte b = 0;
			if (name != null)
				b &= 1;
			if (moduleType != null)
				b &= 2;
			out.writeByte(b);
			
			if (name != null)
				out.writeUTF(name);
			
			if (moduleType != null) {
				out.writeUTF(moduleType.getId());
				out.writeUTF(moduleType.getVersion());
			}
			saveResource(out, resources);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save module publish info", e);
		}
	}

	protected void saveResource(DataOutput out, IModuleResource[] resources2) throws IOException {
		if (resources2 == null)
			return;
		int size = resources2.length;
		out.writeInt(0);
		for (int i = 0; i < size; i++) {
			if (resources2[i] instanceof IModuleFile) {
				IModuleFile file = (IModuleFile) resources2[i];
				out.writeByte(0);
				out.writeUTF(file.getName());
				out.writeLong(file.getModificationStamp());
			} else {
				IModuleFolder folder = (IModuleFolder) resources2[i];
				out.writeByte(1);
				out.writeUTF(folder.getName());
				IModuleResource[] resources3 = folder.members();
				saveResource(out, resources3);
			}
		}
	}

	/**
	 * Start using the module cache.
	 */
	protected void startCaching() {
		useCache = true;
		currentResources = null;
		delta = null;
		hasDelta = false;
	}

	/**
	 * Fill the module cache.
	 * 
	 * @param module
	 */
	private void fillCache(IModule[] module) {
		if (!useCache)
			return;
		
		if (currentResources != null)
			return;
		
		try {
			long time = System.currentTimeMillis();
			int size = module.length;
			ModuleDelegate pm = (ModuleDelegate) module[size - 1].loadAdapter(ModuleDelegate.class, null);
			if (pm != null)
				currentResources = pm.members();
			else
				currentResources = new IModuleResource[0];
			
			delta = ServerPublishInfo.getDelta(resources, currentResources);
			hasDelta = (delta != null && delta.length > 0);
			Trace.trace(Trace.PERFORMANCE, "Filling publish cache for " + module[size-1].getName() + ": " + (System.currentTimeMillis() - time));
		} catch (CoreException ce) {
			Trace.trace(Trace.WARNING, "Couldn't fill publish cache for " + module);
		}
		if (delta == null)
			delta = new IModuleResourceDelta[0];
	}

	protected void clearCache() {
		useCache = false;
		currentResources = null;
		delta = null;
		hasDelta = false;
	}

	protected IModuleResource[] getModuleResources(IModule[] module) {
		if (module == null)
			return new IModuleResource[0];
		
		if (useCache) {
			fillCache(module);
			return currentResources;
		}
		
		long time = System.currentTimeMillis();
		
		int size = module.length;
		ModuleDelegate pm = (ModuleDelegate) module[size - 1].loadAdapter(ModuleDelegate.class, null);
		IModuleResource[] x = new IModuleResource[0];
		try {
			if (pm != null)
				x = pm.members();
		} catch (CoreException ce) {
			// ignore
		}
		Trace.trace(Trace.PERFORMANCE, "Time to get members() for " + module[size - 1].getName() + ": " + (System.currentTimeMillis() - time));
		return x;
	}

	protected IModuleResourceDelta[] getDelta(IModule[] module) {
		if (module == null)
			return new IModuleResourceDelta[0];
		
		if (useCache) {
			fillCache(module);
			return delta;
		}
		
		int size = module.length;
		ModuleDelegate pm = (ModuleDelegate) module[size - 1].loadAdapter(ModuleDelegate.class, null);
		IModuleResource[] resources2 = null;
		try {
			if (pm != null)
				resources2 = pm.members();
		} catch (CoreException ce) {
			// ignore
		}
		if (resources2 == null)
			resources2 = new IModuleResource[0];
		return ServerPublishInfo.getDelta(getResources(), resources2);
	}

	protected boolean hasDelta(IModule[] module) {
		if (module == null)
			return false;
		
		if (useCache) {
			fillCache(module);
			return hasDelta;
		}
		
		int size = module.length;
		ModuleDelegate pm = (ModuleDelegate) module[size - 1].loadAdapter(ModuleDelegate.class, null);
		IModuleResource[] resources2 = null;
		try {
			if (pm != null)
				resources2 = pm.members();
		} catch (CoreException ce) {
			// ignore
		}
		if (resources2 == null)
			resources2 = new IModuleResource[0];
		return ServerPublishInfo.hasDelta(getResources(), resources2);
	}

	public void fill(IModule[] module) {
		if (module == null)
			return;
		
		if (useCache) {
			fillCache(module);
			setResources(currentResources);
			return;
		}
		
		int size = module.length;
		ModuleDelegate pm = (ModuleDelegate) module[size - 1].loadAdapter(ModuleDelegate.class, null);
		try {
			if (pm != null)
				setResources(pm.members());
		} catch (CoreException ce) {
			// ignore
		}
	}

	/**
	 * Return a deleted module that represents this module.
	 * 
	 * @return a module
	 */
	protected IModule getDeletedModule() {
		String id = moduleId;
		int index = id.lastIndexOf("#");
		if (index > 0)
			id = id.substring(index+1);
		return new DeletedModule(id, name, moduleType);
	}

	public String toString() {
		return "ModulePublishInfo [" + moduleId + "]";
	}
}