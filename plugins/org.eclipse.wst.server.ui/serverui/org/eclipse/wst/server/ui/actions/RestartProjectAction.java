package org.eclipse.wst.server.ui.actions;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.actions.RestartProjectActionDelegate;
/**
 * Action to restart an individual project on servers that
 * support it.
 */
public class RestartProjectAction extends Action {
	protected RestartProjectActionDelegate delegate;

	/**
	 * RestartProjectAction constructor comment.
	 */
	public RestartProjectAction(IProject project) {
		super(ServerUIPlugin.getResource("%actionRestartProject"));
	
		delegate = new RestartProjectActionDelegate();
		StructuredSelection sel = new StructuredSelection(project);
		delegate.selectionChanged(this, sel);
	}

	/**
	 * Implementation of method defined on <code>IAction</code>.
	 */
	public void run() {
		delegate.run(this);
	}
}
