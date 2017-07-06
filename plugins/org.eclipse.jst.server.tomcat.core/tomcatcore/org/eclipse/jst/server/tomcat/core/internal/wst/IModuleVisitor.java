/**********************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Igor Fedorenko & Fabrizio Giustina - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.wst;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * Visitor interface to process module components
 */
public interface IModuleVisitor {

	/**
	 * Process web component
	 * @param component web component to process
	 * @throws CoreException
	 */
	void visitWebComponent(IVirtualComponent component) throws CoreException;

	/**
	 * Post process web component
	 * @param component web componet to process
	 * @throws CoreException
	 */
	void endVisitWebComponent(IVirtualComponent component) throws CoreException;

	/**
	 * Process archive component.
	 * @param runtimePath path for component at runtime
	 * @param workspacePath path to component in workspace
	 */
	void visitArchiveComponent(IPath runtimePath, IPath workspacePath);

	/**
	 * Process dependent component.
	 * @param runtimePath path for component at runtime
	 * @param workspacePath path to component in workspace
	 */
	void visitDependentComponent(IPath runtimePath, IPath workspacePath);

	/**
	 * Process web resource.
	 * @param runtimePath path for resource at runtime
	 * @param workspacePath path to resource in workspace
	 */
	void visitWebResource(IPath runtimePath, IPath workspacePath);

	/**
	 * Process EAR resource.
	 * @param runtimePath path for resource at runtime
	 * @param workspacePath path to resource in workspace
	 */
	void visitEarResource(IPath runtimePath, IPath workspacePath);

	/**
	 * Post process EAR resource.
	 * @param component EAR componet to process
	 * @throws CoreException 
	 */
	void endVisitEarComponent(IVirtualComponent component) throws CoreException;

	/**
	 * Process a classpath entry.
	 * @param rtFolder path for class folder at runtime
	 * @param entry classpath entry
	 */
	void visitClasspathEntry(IPath rtFolder, IClasspathEntry entry);
}
