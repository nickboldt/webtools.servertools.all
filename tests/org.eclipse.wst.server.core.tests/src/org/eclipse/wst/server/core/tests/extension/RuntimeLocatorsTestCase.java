/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.extension;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.internal.IRuntimeLocator;
import org.eclipse.wst.server.core.internal.ServerPlugin;

public class RuntimeLocatorsTestCase extends TestCase {
	public void testRuntimeLocatorsExtension() throws Exception {
		IRuntimeLocator[] runtimeLocators = ServerPlugin.getRuntimeLocators();
		if (runtimeLocators != null) {
			for (IRuntimeLocator rl : runtimeLocators)
				System.out.println(rl);
		}
	}
}