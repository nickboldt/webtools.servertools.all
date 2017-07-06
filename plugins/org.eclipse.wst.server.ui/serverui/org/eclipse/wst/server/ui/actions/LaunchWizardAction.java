package org.eclipse.wst.server.ui.actions;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. � This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
/**
 * An abstract action that opens up a workbench wizard when run.
 */
abstract class LaunchWizardAction extends Action {
	/**
	 * NewServerAction constructor comment.
	 */
	public LaunchWizardAction() {
		super();
	}

	/**
	 * Return the wizard that should be opened.
	 *
	 * @return org.eclipse.ui.IWorkbenchWizard
	 */
	public abstract IWorkbenchWizard getWizard();

	/**
	 * Implementation of method defined on <code>IAction</code>.
	 */
	public void run() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		ISelection selection = workbenchWindow.getSelectionService().getSelection();
	
		IStructuredSelection selectionToPass = null;
		if (selection instanceof IStructuredSelection)
			selectionToPass = (IStructuredSelection) selection;
		else
			selectionToPass = StructuredSelection.EMPTY;
	
		IWorkbenchWizard wizard = getWizard();
		wizard.init(workbench, selectionToPass);
		WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
		dialog.open();
	}
}
