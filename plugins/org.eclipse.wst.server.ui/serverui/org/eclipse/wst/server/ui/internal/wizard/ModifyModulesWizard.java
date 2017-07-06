/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
�*
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.wizard;

import java.util.List;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.task.SaveServerTask;
import org.eclipse.wst.server.ui.internal.wizard.fragment.ModifyModulesWizardFragment;
import org.eclipse.wst.server.ui.internal.wizard.fragment.TasksWizardFragment;
import org.eclipse.wst.server.ui.wizard.TaskWizard;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * A wizard used to add and remove modules.
 */
public class ModifyModulesWizard extends TaskWizard {
	static class ModifyModulesWizard2 extends WizardFragment {
		protected IServerWorkingCopy server;
		
		public ModifyModulesWizard2(IServerWorkingCopy server) {
			this.server = server;
		}

		protected void createChildFragments(List list) {
			list.add(new WizardFragment() {
				public void enter() {
					getTaskModel().putObject(ITaskModel.TASK_SERVER, server);
				}
			});
			list.add(new ModifyModulesWizardFragment());
			list.add(new TasksWizardFragment());
			list.add(new WizardFragment() {
				public ITask createFinishTask() {
					return new SaveServerTask();
				}
			});
		}
	}

	/**
	 * ModifyModulesWizard constructor comment.
	 */
	public ModifyModulesWizard(IServer server) {
		super(ServerUIPlugin.getResource("%wizModuleWizardTitle"), new ModifyModulesWizard2(server.createWorkingCopy()));
	
		setNeedsProgressMonitor(true);
	}
}