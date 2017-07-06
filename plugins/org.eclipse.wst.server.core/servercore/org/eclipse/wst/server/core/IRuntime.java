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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.wst.server.core.model.IRuntimeDelegate;
/**
 * Represents a runtime instance. Every runtime is an instance of a
 * particular, fixed runtime type.
 * <p>
 * Servers have a runtime. The server runtime corresponds to the
 * installed code base for the server. The main role played by the server
 * runtime is in identifying code libraries to compile or build against.
 * In the case of local servers, the server runtime may play a secondary role
 * of being used to launch the server for testing. Having the server runtimes
 * identified as an entity separate from the server itself facilitates sharing
 * server runtimes between several servers.
 * </p>
 * <p>
 * [issue: As mentioned in an issue on IRuntimeType, the term "runtime"
 * is misleading, given that the main reason is for build time classpath
 * contributions, not for actually running anything. "libraries" might be a
 * better choice.]
 * </p>
 * <p>
 * The resource manager maintains a global list of all known runtime instances
 * ({@link IResourceManager#getRuntimes()}).
 * </p>
 * <p>
 * [issue: Equality/identify for runtimes?]
 * </p>
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IRuntime extends IElement {
	
	/**
	 * Returns the type of this runtime instance.
	 * 
	 * @return the runtime type
	 */
	public IRuntimeType getRuntimeType();

	/**
	 * Returns the delegate for this runtime.
	 * The runtime delegate is a runtime-type-specific object.
	 * By casting the runtime delegate to the type prescribed in
	 * the API documentation for that particular runtime type, 
	 * the client can access runtime-type-specific properties and
	 * methods.
	 * <p>
	 * [issue: Exposing IRuntimeDelegate to clients of IRuntime
	 * is confusing and dangerous. Instead, replace this
	 * method with something like getRuntimeExtension() which
	 * returns an IRuntimeExtension. IRuntimeExtension is an
	 * "marker" interface that runtime providers would 
	 * implement or extend if they want to expose additional
	 * API for their runtime type. That way IRuntimeDelegate
	 * can be kept entirely on the SPI side, out of view from 
	 * clients.]
	 * </p>
	 * <p>
	 * [issue: runtimeTypes schema, class attribute is optional.
	 * This suggests that a server need not provide a delegate class.
	 * This seems implausible. I've spec'd this method as delegate optional.]
	 * </p>
	 * 
	 * @return the runtime delegate, or <code>null</code> if none
	 */
	public IRuntimeDelegate getDelegate();

	/**
	 * Returns a runtime working copy for modifying this runtime instance.
	 * If this instance is already a working copy, it is returned.
	 * If this instance is not a working copy, a new runtime working copy
	 * is created with the same id and attributes.
	 * Clients are responsible for saving or releasing the working copy when
	 * they are done with it.
	 * <p>
	 * The runtime working copy is related to this runtime instance
	 * in the following ways:
	 * <pre>
	 * this.getWorkingCopy().getId() == this.getId()
	 * this.getWorkingCopy().getOriginal() == this
	 * </pre>
	 * </p>
	 * <p>
	 * [issue: IRuntimeWorkingCopy extends IRuntime. 
	 * Runtime.getWorkingCopy() create a new working copy;
	 * RuntimeWorkingCopy.getWorkingCopy() returns this.
	 * This may be convenient in code that is ignorant of
	 * whether they are dealing with a working copy or not.
	 * However, it is hard for clients to manage working copies
	 * with this design.
	 * This method should be renamed "createWorkingCopy"
	 * or "newWorkingCopy" to make it clear to clients that it
	 * creates a new object, even for working copies.]
	 * </p>
	 * 
	 * @return a new working copy
	 */
	public IRuntimeWorkingCopy getWorkingCopy();
	
	/**
	 * Returns the location of this runtime.
	 * <p>
	 * [issue: Explain what this "location" is.]
	 * </p>
	 * 
	 * @return the location of this runtime, or <code>null</code> if none
	 */
	public IPath getLocation();
	
	/**
	 * Returns whether this runtime can be used as a test environment.
	 * <p>
	 * [issue: How does one explain what a "test environment" is?
	 * How does this property of runtime square with 
	 * IServerType.isTestEnvironment(), a *type-generic*
	 * property of a server type?]
	 * </p>
	 * 
	 * @return <code>true</code> if this runtime can be use as a
	 * test environment, and <code>false</code> if it cannot
	 */
	public boolean isTestEnvironment();
	
	/**
	 * Validates this runtime instance.
	 * <p>
	 * [issue: Need to explain what could be wrong with a runtime.]
	 * </p>
	 * <p>
	 * [issue: Would it make more sense to validate working copies
	 * before prior to save?]
	 * </p>
	 *
	 * @return a status object with code <code>IStatus.OK</code> if this
	 * runtime is valid, otherwise a status object indicating what is
	 * wrong with it
	 */
	public IStatus validate();
}