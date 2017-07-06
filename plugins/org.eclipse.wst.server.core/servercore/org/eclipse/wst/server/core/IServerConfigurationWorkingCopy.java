/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.model.IServerConfigurationWorkingCopyDelegate;
/**
 * A working copy server object used for formulating changes
 * to a server configuration instance
 * ({@link org.eclipse.wst.server.core.IServerConfiguration}).
 * <p>
 * [issue: There can be other server-configuration-type-specific properties.
 * The default values for these need to be specified somewhere
 * too (probably in the API subclass of IServerConfigurationWorkingCopyDelegate).]
 * </p>
 * <p>
 * [issue: IElementWorkingCopy and IElement support an open-ended set
 * of attribute-value pairs. What is relationship between these
 * attributes and (a) the get/setXXX methods found on this interface,
 * and (b) get/setXXX methods provided by specific server types?
 * Is it the case that these attribute-values pairs are the only
 * information about a server instance that can be preserved
 * between workbench sessions? That is, any information recorded
 * just in instance fields of an IServerConfigurationDelegate implementation
 * will be lost when the session ends.]
 * </p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IServerConfigurationWorkingCopy extends IServerConfiguration, IElementWorkingCopy {
	
	/**
	 * Returns the server configuration instance that this working copy is
	 * associated with.
	 * <p>
	 * For a server configuration working copy created by a call to
	 * {@link IServerConfiguration#getWorkingCopy()},
	 * <code>this.getOriginal()</code> returns the original
	 * server configuration object. For a server configuration working copy
	 * just created by a call to
	 * {@link IServerConfigurationType#createServerConfiguration(String, IFile, IProgressMonitor)},
	 * <code>this.getOriginal()</code> returns <code>null</code>.
	 * </p>
	 * 
	 * @return the associated server configuration instance, or <code>null</code> if none
	 */
	public IServerConfiguration getOriginal();
	
	/**
	 * Returns the delegate for this server configuration working copy.
	 * The server configuration working copy delegate is a
	 * server-configuration-type-specific object. By casting the server
	 * configuration working copy delegate to the type prescribed in the API
	 * documentation for that particular server configuration working copy type,
	 * the client can access server-configuration-type-specific properties and
	 * methods.
	 * <p>
	 * [issue: Exposing IServerConfigurationWorkingCopyDelegate to clients
	 * of IServerConfigurationWorkingCopy is same problem as exposing
	 * IServerConfigurationDelegate to clients of IServerConfiguration.
	 * The suggested fix is to replace this method with something like
	 * getServerConfigurationWorkingCopyExtension() which
	 * returns an IServerConfigurationWorkingCopyExtension.]
	 * </p>
	 * <p>
	 * [issue: serverConfigurationTypes schema, workingCopyClass attribute is
	 * optional. This suggests that a server configuration need not provide a
	 * working copy delegate class. Like the class attribute, this seems
	 * implausible. I've spec'd this method as if working copy delegate is
	 * mandatory.]
	 * </p>
	 * 
	 * @return the delegate for the server configuration working copy
	 */
	public IServerConfigurationWorkingCopyDelegate getWorkingCopyDelegate();
	
	/**
	 * Commits the changes made in this working copy. If there is
	 * no extant server configuration instance with a matching id and
	 * server configuration type, this will create a server configuration
	 * instance with attributes taken from this working copy.
	 * If there an existing server configuration instance with a matching id
	 * and server configuration type, this will change the server configuration
	 * instance accordingly.
	 * <p>
	 * [issue: What is relationship to 
	 * this.getOriginal() and the IServerConfiguration returned by this.save()?
	 * The answer should be: they're the same server configuration, for an
	 * appropriate notion of "same". As currently implemented, they
	 * are different IServerConfiguration instances but have the same
	 * ids and types. Client that are hanging on to the old server configuration
	 * instance will not see the changes. 
	 * If IServerConfiguration were some kind of handle object as elsewhere in 
	 * Eclipse Platform, this kind of change could be done much
	 * more smoothly.]
	 * </p>
	 * <p>
	 * [issue: What if this object has already been saved
	 * or released?]
	 * </p>
	 * <p>
	 * [issue: What is lifecycle for IServerConfigurationWorkingCopyDelegate
	 * associated with this working copy?]
	 * </p>
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a new server configuration instance
	 * @throws CoreException [missing]
	 */
	public IServerConfiguration save(IProgressMonitor monitor) throws CoreException;
}