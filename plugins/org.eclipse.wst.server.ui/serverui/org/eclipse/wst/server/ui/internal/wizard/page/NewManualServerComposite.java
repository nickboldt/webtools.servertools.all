/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.page;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;
import org.eclipse.wst.server.core.util.SocketUtil;
import org.eclipse.wst.server.ui.internal.*;
import org.eclipse.wst.server.ui.internal.viewers.ServerTypeComposite;
/**
 * Wizard page used to create a server and configuration at the same time.
 */
public class NewManualServerComposite extends Composite {
	public interface ServerSelectionListener {
		public void serverSelected(IServerAttributes server);
		public void runtimeSelected(IRuntime runtime);
	}

	public interface IWizardHandle2 {
		public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InterruptedException, InvocationTargetException;
		public void update();
		public void setMessage(String newMessage, int newType);
	}
	protected IWizardHandle2 wizard;

	protected ServerTypeComposite serverTypeComposite;

	protected Label runtimeLabel;
	protected Combo runtimeCombo;
	protected Button runtimeButton;
	protected IRuntime[] runtimes;
	protected IRuntime newRuntime;

	protected IRuntime runtime;
	protected IServerWorkingCopy server;
	protected ServerSelectionListener listener;

	protected String host;

	protected IModuleType moduleType;
	protected String serverTypeId;
	protected boolean includeIncompatible;

	protected ServerCreationCache cache = new ServerCreationCache();

	/**
	 * Creates a new server and server configuration.  If the initial
	 * resource selection contains exactly one container resource then it will be
	 * used as the default container resource.
	 *
	 * @param parent a parent composite
	 * @param wizard a wizard handle
	 * @param moduleType a module type
	 * @param serverTypeId a server type id, or null
	 * @param includeIncompatible true to include incompatible servers that support similar module types
	 * @param listener a server selection listener
	 */
	public NewManualServerComposite(Composite parent, IWizardHandle2 wizard, IModuleType moduleType, String serverTypeId, boolean includeIncompatible, ServerSelectionListener listener) {
		super(parent, SWT.NONE);
		this.wizard = wizard;
		this.listener = listener;
		
		this.moduleType = moduleType;
		this.serverTypeId = serverTypeId;
		this.includeIncompatible = includeIncompatible;
		
		createControl();
		wizard.setMessage("", IMessageProvider.ERROR); //$NON-NLS-1$
	}

	/**
	 * Returns this page's initial visual components.
	 */
	protected void createControl() {
		// top level group
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);

		this.setFont(getParent().getFont());
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(this, ContextIds.NEW_SERVER_WIZARD);
		
		serverTypeComposite = new ServerTypeComposite(this, moduleType, serverTypeId, new ServerTypeComposite.ServerTypeSelectionListener() {
			public void serverTypeSelected(IServerType type2) {
				handleTypeSelection(type2);
				//WizardUtil.defaultSelect(parent, CreateServerWizardPage.this);
			}
		});
		serverTypeComposite.setIncludeIncompatibleVersions(includeIncompatible);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.horizontalSpan = 3;
		serverTypeComposite.setLayoutData(data);
		whs.setHelp(serverTypeComposite, ContextIds.NEW_SERVER_TYPE);
		
		runtimeLabel = new Label(this, SWT.NONE);
		runtimeLabel.setText(Messages.wizNewServerRuntime);
		
		runtimeCombo = new Combo(this, SWT.READ_ONLY);
		runtimeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		runtimeCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				try {
					setRuntime(runtimes[runtimeCombo.getSelectionIndex()]);
				} catch (Exception ex) {
					// ignore
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		runtimeButton = SWTUtil.createButton(this, Messages.installedRuntimes);
		runtimeButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		runtimeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (showPreferencePage()) {
					runtime = null;
					IServerType serverType = serverTypeComposite.getSelectedServerType();
					updateRuntimeCombo(serverType);
				}
			}
		});
		Dialog.applyDialogFont(this);
	}

	protected boolean showPreferencePage() {
		String id = "org.eclipse.wst.server.ui.preferencePage";
		String id2 = "org.eclipse.wst.server.ui.runtime.preferencePage";
		final PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getShell(), id2, new String[] { id, id2 }, null);
		return (dialog.open() == Window.OK);
	}

	public void setHost(String host) {
		this.host = host;
		if (serverTypeComposite == null)
			return;
		
		boolean changed = false;
		if (host == null) {
			changed = serverTypeComposite.setHost(true);
		} else if (SocketUtil.isLocalhost(host))
			changed = serverTypeComposite.setHost(true);
		else
			changed = serverTypeComposite.setHost(false);
		
		if (changed)
			handleTypeSelection(serverTypeComposite.getSelectedServerType());
		else if (server != null) {
			server.setHost(host);
			ServerUtil.setServerDefaultName(server);
		}
	}

	/**
	 * Load a server of the given type.
	 */
	protected void loadServerImpl(IServerType serverType) {
		server = null;
		
		if (serverType == null)
			return;
		
		boolean isLocalhost = SocketUtil.isLocalhost(host);
		
		server = cache.getCachedServer(serverType, isLocalhost);
		if (server != null) {
			server.setHost(host);
			ServerUtil.setServerDefaultName(server);
			runtime = server.getRuntime();
			listener.runtimeSelected(runtime);
			return;
		}
		
		try {
			server = cache.getServer(serverType, isLocalhost, null);
			if (server != null) {
				server.setHost(host);
				ServerUtil.setServerDefaultName(server);
				
				if (serverType.hasRuntime() && server.getRuntime() == null) {
					runtime = null;
					updateRuntimes(serverType);
					setRuntime(getDefaultRuntime());
					
					if (server.getServerType() != null && server.getServerType().hasServerConfiguration() && !runtime.getLocation().isEmpty())
						((ServerWorkingCopy)server).importRuntimeConfiguration(runtime, null);
				}
				((ServerWorkingCopy)server).setDefaults(null);
			}
		} catch (CoreException ce) {
			Trace.trace(Trace.SEVERE, "Error creating server", ce);
			server = null;
			runtime = null;
			wizard.setMessage(ce.getLocalizedMessage(), IMessageProvider.ERROR);
		}
		
		if (server == null)
			wizard.setMessage(Messages.wizErrorServerCreationError, IMessageProvider.ERROR);
	}

	/**
	 * Pick the first non-stub runtime first. Otherwise, just pick the first runtime.
	 * 
	 * @return the default runtime
	 */
	protected IRuntime getDefaultRuntime() {
		if (runtimes == null || runtimes.length == 0)
			return null;
		
		if (runtimes != null) {
			int size = runtimes.length;
			for (int i = 0; i < size; i++) {
				if (!runtimes[i].isStub())
					return runtimes[i];
			}
		}
		return runtimes[0];
	}

	protected void updateRuntimes(IServerType serverType) {
		if (serverType == null)
			return;
		
		IRuntimeType runtimeType = serverType.getRuntimeType();
		runtimes = ServerUIPlugin.getRuntimes(runtimeType);
		newRuntime = null;
		
		if (server != null && runtimes != null) {
			List runtimes2 = new ArrayList();
			int size = runtimes.length;
			for (int i = 0; i < size; i++) {
				IRuntime runtime2 = runtimes[i];
				if (!SocketUtil.isLocalhost(server.getHost()) || !runtime2.isStub())
					runtimes2.add(runtime2);
			}
			runtimes = new IRuntime[runtimes2.size()];
			runtimes2.toArray(runtimes);
			if (runtimes.length > 0)
				return;
		}
		
		// create a new runtime
		try {
			IRuntimeWorkingCopy runtimeWC = runtimeType.createRuntime(null, null);
			ServerUtil.setRuntimeDefaultName(runtimeWC);
			runtimes = new IRuntime[1];
			runtimes[0] = runtimeWC;
			newRuntime = runtimeWC;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Couldn't create runtime", e); //$NON-NLS-1$
		}
	}

	protected void updateRuntimeCombo(IServerType serverType) {
		if (serverType == null || !serverType.hasRuntime() || server == null) {
			if (runtimeLabel != null) {
				runtimeLabel.setEnabled(false);
				runtimeCombo.setItems(new String[0]);
				runtimeCombo.setEnabled(false);
				runtimeLabel.setVisible(false);
				runtimeCombo.setVisible(false);
				runtimeButton.setEnabled(false);
				runtimeButton.setVisible(false);
			}
			runtimes = new IRuntime[0];
			setRuntime(null);
			return;
		}
		
		updateRuntimes(serverType);
		
		int size = runtimes.length;
		String[] items = new String[size];
		for (int i = 0; i < size; i++) {
			if (runtimes[i].equals(newRuntime))
				items[i] = Messages.wizNewServerRuntimeCreate;
			else
				items[i] = runtimes[i].getName();
		}
		
		if (runtime == null)
			setRuntime(getDefaultRuntime());
		
		if (runtimeCombo != null) {
			runtimeCombo.setItems(items);
			if (runtimes.length > 0) {
				int sel = -1;
				for (int i = 0; i < size; i++) {
					if (runtimes[i].equals(runtime))
						sel = i;
				}
				if (sel < 0) {
					sel = 0;
					setRuntime(runtimes[0]);
				}
				
				runtimeCombo.select(sel);
			}
			
			IRuntimeType runtimeType = serverType.getRuntimeType();
			boolean showRuntime = ServerUIPlugin.getRuntimes(runtimeType).length >=1;
			runtimeCombo.setEnabled(showRuntime);
			runtimeLabel.setEnabled(showRuntime);
			runtimeButton.setEnabled(showRuntime);
			runtimeLabel.setVisible(showRuntime);
			runtimeCombo.setVisible(showRuntime);
			runtimeButton.setVisible(showRuntime);
		}
	}

	protected void setRuntime(IRuntime runtime2) {
		runtime = runtime2;
		if (server != null)
			server.setRuntime(runtime);
		listener.runtimeSelected(runtime);
	}

	/**
	 * Handle the server type selection.
	 */
	protected void handleTypeSelection(IServerType serverType) {
		boolean wrong = false;
		if (serverType != null && moduleType != null) {
			IRuntimeType runtimeType = serverType.getRuntimeType();
			if (!ServerUtil.isSupportedModule(runtimeType.getModuleTypes(), moduleType)) {
				serverType = null;
				wrong = true;
				//wizard.setMessage("Not the right spec level2", IMessageProvider.ERROR);
			}
		}
		
		if (wrong) {
			server = null;
			runtime = null;
			wizard.setMessage(NLS.bind(Messages.errorVersionLevel, new Object[] { moduleType.getName(), moduleType.getVersion() }), IMessageProvider.ERROR);
		} else if (serverType == null) {
			server = null;
			runtime = null;
			wizard.setMessage("", IMessageProvider.ERROR); //$NON-NLS-1$
		} else {
			wizard.setMessage(null, IMessageProvider.NONE);
			loadServerImpl(serverType);
		}
		updateRuntimeCombo(serverType);
		listener.serverSelected(server);
		wizard.update();
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
	
		if (visible) {
			/*if (defaultServerFactory != null) {
				tree.setSelection(new TreeItem[] { defaultServerFactory });
				tree.showItem(tree.getItems()[0]);
			}*/
			// force the focus to initially validate the fields
			handleTypeSelection(null);
		}
		
		Control[] c = getChildren();
		if (c != null) {
			int size = c.length;
			for (int i = 0; i < size; i++)
				if (c[i] != null && c[i] instanceof ServerTypeComposite)
					c[i].setVisible(visible);
		}
		if (visible)
			handleTypeSelection(serverTypeComposite.getSelectedServerType());
	}

	public void refresh() {
		serverTypeComposite.refresh();
	}

	public IRuntime getRuntime() {
		return runtime;
	}

	public IServerWorkingCopy getServer() {
		return server;
	}
}