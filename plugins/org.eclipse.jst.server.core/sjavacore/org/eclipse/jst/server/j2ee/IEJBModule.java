/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.j2ee;

public interface IEJBModule extends IJ2EEModule {
	/**
	 * Returns a version number in the form "x.y.z".
	 *
	 * @return java.lang.String
	 */
	public String getEJBSpecificationVersion();
}