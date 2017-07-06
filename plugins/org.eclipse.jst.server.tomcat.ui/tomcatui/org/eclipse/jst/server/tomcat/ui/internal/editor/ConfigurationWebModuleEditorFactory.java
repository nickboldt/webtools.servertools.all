/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.ui.internal.editor;

import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.ui.IEditorPart;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.editor.ServerEditorPartFactoryDelegate;
/**
 *
 */
public class ConfigurationWebModuleEditorFactory extends ServerEditorPartFactoryDelegate {
	/*
	 * @see ServerEditorPartFactoryDelegate#shouldDisplay(IServer)
	 */
	public boolean shouldCreatePage(IServerWorkingCopy server) {
		TomcatServer tomcatServer = (TomcatServer) server.getAdapter(TomcatServer.class);
		return tomcatServer.getServerConfiguration() != null;
	}

	/*
	 * @see ServerEditorPartFactoryDelegate#createPage()
	 */
	public IEditorPart createPage() {
		return new ConfigurationWebModuleEditorPart();
	}
}