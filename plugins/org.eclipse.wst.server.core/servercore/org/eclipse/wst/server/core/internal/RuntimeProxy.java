/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

/**
 * 
 */
public class RuntimeProxy extends Runtime {

	public RuntimeProxy(RuntimeTypeProxy runtimeTypeProxy) {
		super(null, runtimeTypeProxy.getId(), runtimeTypeProxy);
	}
	
}