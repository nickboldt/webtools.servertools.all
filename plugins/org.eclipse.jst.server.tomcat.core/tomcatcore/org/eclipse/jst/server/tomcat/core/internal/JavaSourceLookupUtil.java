/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
�*
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.ExternalArchiveSourceContainer;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaProjectSourceContainer;
import org.eclipse.jdt.launching.sourcelookup.containers.PackageFragmentRootSourceContainer;
/**
 * Java source lookup utility. Taken directly from
 * org.eclipse.jdt.internal.launching.JavaSourceLookupUtil since it is private.
 */
public class JavaSourceLookupUtil {
	/**
	 * Translates the given runtime classpath entries into associated source
	 * containers.
	 * 
	 * @param entries entries to translate
	 * @param considerSourceAttachments whether to consider source attachments
	 *  when comparing against existing packagr fragment roots
	 */
	public static ISourceContainer[] translate(IRuntimeClasspathEntry[] entries, boolean considerSourceAttachments) {
		List containers = new ArrayList(entries.length);
		for (int i = 0; i < entries.length; i++) {
			IRuntimeClasspathEntry entry = entries[i];
			switch (entry.getType()) {
				case IRuntimeClasspathEntry.ARCHIVE:
					IPackageFragmentRoot root = getPackageFragmentRoot(entry, considerSourceAttachments);
					String path = entry.getSourceAttachmentLocation();
					if (root == null && path == null && considerSourceAttachments) {
						// use the pkg frag root it there is no source attachment
						root = getPackageFragmentRoot(entry, false);
					}
					if (root == null) {
						ISourceContainer container = null;
						if (path == null) {
							// use the archive itself
							container = new ExternalArchiveSourceContainer(entry.getLocation(), true);
						} else {
							container = new ExternalArchiveSourceContainer(path, true);

						}
						if (!containers.contains(container)) {
							containers.add(container);
						}
					} else {
						ISourceContainer container = new PackageFragmentRootSourceContainer(root);
						if (!containers.contains(container)) {
							containers.add(container);
						}
					}
					break;
				case IRuntimeClasspathEntry.PROJECT:
					IResource resource = entry.getResource();
					if (resource != null && resource.getType() == IResource.PROJECT) {
						ISourceContainer container = new JavaProjectSourceContainer(JavaCore.create((IProject)resource));
						if (!containers.contains(container)) {
							containers.add(container);
						}
					}
					break;
				default:
					// no other classpath types are valid in a resolved path
					break;
			}
		}
		return (ISourceContainer[]) containers.toArray(new ISourceContainer[containers.size()]);
	}
	
	/**
	 * Returns whether the given objects are equal, allowing
	 * for <code>null</code>.
	 * 
	 * @param a
	 * @param b
	 * @return whether the given objects are equal, allowing
	 *   for <code>null</code>
	 */
	private static boolean equalOrNull(Object a, Object b) {
		if (a == null) {
			return b == null;
		}
		if (b == null) {
			return false;
		}
		return a.equals(b);
	}
	
	/**
	 * Returns whether the source attachments of the given package fragment
	 * root and runtime classpath entry are equal.
	 * 
	 * @param root package fragment root
	 * @param entry runtime classpath entry
	 * @return whether the source attachments of the given package fragment
	 * root and runtime classpath entry are equal
	 * @throws JavaModelException 
	 */
	private static boolean isSourceAttachmentEqual(IPackageFragmentRoot root, IRuntimeClasspathEntry entry) throws JavaModelException {
		return equalOrNull(root.getSourceAttachmentPath(), entry.getSourceAttachmentPath());
	}
	
	/**
	 * Determines if the given archive runtime classpath entry exists
	 * in the workspace as a package fragment root. Returns the associated
	 * package fragment root possible, otherwise
	 * <code>null</code>.
	 *  
	 * @param entry archive runtime classpath entry
	 * @param considerSourceAttachment whether the source attachments should be
	 *  considered comparing against package fragment roots
	 * @return package fragment root or <code>null</code>
	 */
	private static IPackageFragmentRoot getPackageFragmentRoot(IRuntimeClasspathEntry entry, boolean considerSourceAttachment) {
		IResource resource = entry.getResource();
		if (resource == null) { 
			// Check all package fragment roots for case of external archive.
			// External jars are shared, so it does not matter which project it
			// originates from
			IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
			try {
				IJavaProject[] jps = model.getJavaProjects();
				for (int i = 0; i < jps.length; i++) {
					IJavaProject jp = jps[i];
					IProject p =  jp.getProject();
					if (p.isOpen()) {
						IPackageFragmentRoot[] allRoots = jp.getPackageFragmentRoots();
						for (int j = 0; j < allRoots.length; j++) {
							IPackageFragmentRoot root = allRoots[j];
							if (root.isExternal() && root.getPath().equals(new Path(entry.getLocation()))) {
								if (!considerSourceAttachment || isSourceAttachmentEqual(root, entry)) {
									// use package fragment root
									return root;
								}							
							}
						}
					}
				}
			} catch (JavaModelException e) {
				TomcatPlugin.log(e);
			}
		} else {
			// check if the archive is a package fragment root
			IProject project = resource.getProject();
			IJavaProject jp = JavaCore.create(project);
			try {
				if (project.isOpen() && jp.exists()) {
					IPackageFragmentRoot root = jp.getPackageFragmentRoot(resource);
					IPackageFragmentRoot[] allRoots = jp.getPackageFragmentRoots();
					for (int j = 0; j < allRoots.length; j++) {
						if (allRoots[j].equals(root)) {
							// ensure source attachment paths match
							if (!considerSourceAttachment || isSourceAttachmentEqual(root, entry)) {
								// use package fragment root
								return root;
							}
						}
					}

				}
				// check all other java projects to see if another project references
				// the archive
				IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
				IJavaProject[] jps = model.getJavaProjects();
				for (int i = 0; i < jps.length; i++) {
					IJavaProject jp1 = jps[i];
					IProject p = jp1.getProject();
					if (p.isOpen()) {
						IPackageFragmentRoot[] allRoots = jp1.getPackageFragmentRoots();
						for (int j = 0; j < allRoots.length; j++) {
							IPackageFragmentRoot root = allRoots[j];
							if (!root.isExternal() && root.getPath().equals(entry.getPath())) {
								if (!considerSourceAttachment || isSourceAttachmentEqual(root, entry)) {
									// use package fragment root
									return root;
								}							
							}
						}
					}
				}
			} catch (JavaModelException e) {
				TomcatPlugin.log(e);
			}		
		}		
		return null;
	}
}