/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.model.ILaunchable;
/**
 * 
 */
public class NullLaunchable implements ILaunchable {
	public static final String ID = "null.launchable";

	public NullLaunchable() { }

	public String getId() {
		return ID;
	}

	public String toString() {
		return "NullLaunchable[id=" + getId() + "]";
	}
}