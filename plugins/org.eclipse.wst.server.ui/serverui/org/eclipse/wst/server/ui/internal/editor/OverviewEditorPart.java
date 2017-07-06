package org.eclipse.wst.server.ui.internal.editor;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. � This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.util.SocketUtil;
import org.eclipse.wst.server.ui.editor.*;
import org.eclipse.wst.server.ui.internal.ContextIds;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.command.SetServerConfigurationNameCommand;
import org.eclipse.wst.server.ui.internal.command.SetServerHostnameCommand;
import org.eclipse.wst.server.ui.internal.command.SetServerNameCommand;
import org.eclipse.wst.server.ui.internal.command.SetServerRuntimeCommand;
/**
 * Server general editor page.
 */
public class OverviewEditorPart extends ServerResourceEditorPart {
	protected Text serverName;
	protected Text serverConfigurationName;
	protected Text hostname;
	protected Combo runtimeCombo;
	
	protected boolean updating;
	
	protected List runtimes;

	protected PropertyChangeListener listener;

	/**
	 * OverviewEditorPart constructor comment.
	 */
	protected OverviewEditorPart() {
		super();
	}
	
	protected ICommandManager getCommandManager() {
		return commandManager;
	}
	
	protected IServerWorkingCopy getServer() {
		return server;
	}
	
	protected IServerConfigurationWorkingCopy getServerConfiguration() {
		return serverConfiguration;
	}

	/**
	 * 
	 */
	protected void addChangeListener() {
		listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (updating)
					return;
				updating = true;
				if (event.getPropertyName().equals("name"))
					updateNames();
				else if (event.getPropertyName().equals("hostname") && hostname != null) {
					hostname.setText((String) event.getNewValue());
				}
				updating = false;
			}
		};
		if (server != null)
			server.addPropertyChangeListener(listener);
		if (serverConfiguration != null)
			serverConfiguration.addPropertyChangeListener(listener);
	}

	protected void updateNames() {
		if (serverName != null)
			serverName.setText(server.getName());
		if (serverConfigurationName != null)
			serverConfigurationName.setText(serverConfiguration.getName());
	}

	/**
	 * Creates the SWT controls for this workbench part.
	 *
	 * @param parent the parent control
	 */
	public final void createPartControl(Composite parent) {
		FormToolkit toolkit = getFormToolkit(parent.getDisplay());
		
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText(ServerUIPlugin.getResource("%serverEditorOverviewPageTitle"));
		form.getBody().setLayout(new GridLayout());
		form.getBody().setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		
		Composite columnComp = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		//layout.marginHeight = 10;
		//layout.marginWidth = 10;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 10;
		columnComp.setLayout(layout);
		columnComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		
		// left column
		Composite leftColumnComp = toolkit.createComposite(columnComp);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		leftColumnComp.setLayout(layout);
		leftColumnComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		
		Section section = toolkit.createSection(leftColumnComp, ExpandableComposite.TWISTIE|ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);
		section.setText(ServerUIPlugin.getResource("%serverEditorOverviewSection"));
		section.setDescription(ServerUIPlugin.getResource("%serverEditorOverviewDescription"));
		section.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));

		Composite composite = toolkit.createComposite(section);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		WorkbenchHelp.setHelp(composite, ContextIds.EDITOR_OVERVIEW_PAGE);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);

		// server name
		if (server != null) {
			createLabel(toolkit, composite, ServerUIPlugin.getResource("%serverEditorOverviewServerName"));
			
			serverName = toolkit.createText(composite, server.getName());
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			serverName.setLayoutData(data);
			serverName.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (updating)
						return;
					updating = true;
					getCommandManager().executeCommand(new SetServerNameCommand(getServer(), serverName.getText()));
					updating = false;
				}
			});
		}
		
		// server configuration name
		if (serverConfiguration != null) {
			createLabel(toolkit, composite, ServerUIPlugin.getResource("%serverEditorOverviewServerConfigurationName"));
			
			serverConfigurationName = toolkit.createText(composite, serverConfiguration.getName());
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			serverConfigurationName.setLayoutData(data);
			serverConfigurationName.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (updating)
						return;
					updating = true;
					getCommandManager().executeCommand(new SetServerConfigurationNameCommand(getServerConfiguration(), serverConfigurationName.getText()));
					updating = false;
				}
			});
		}
		
		// hostname
		if (server != null) {
			createLabel(toolkit, composite, ServerUIPlugin.getResource("%serverEditorOverviewServerHostname"));
			
			hostname = toolkit.createText(composite, server.getHostname());
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			hostname.setLayoutData(data);
			hostname.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (updating)
						return;
					updating = true;
					getCommandManager().executeCommand(new SetServerHostnameCommand(getServer(), hostname.getText()));
					updating = false;
				}
			});
		}
		
		if (server != null && server.getServerType() != null && server.getServerType().hasRuntime()) {
			IRuntime runtime = server.getRuntime();
			createLabel(toolkit, composite, ServerUIPlugin.getResource("%serverEditorOverviewRuntime"));
			
			IRuntimeType runtimeType = server.getServerType().getRuntimeType();
			runtimes = ServerCore.getResourceManager().getRuntimes(runtimeType);
			
			if (runtimes.size() < 2) {
				if (runtime == null)
					toolkit.createLabel(composite, "");
				else
					toolkit.createLabel(composite, runtime.getName());
			} else {
				runtimeCombo = new Combo(composite, SWT.READ_ONLY);
				GridData data = new GridData(GridData.FILL_HORIZONTAL);
				runtimeCombo.setLayoutData(data);
				updateRuntimeCombo();
				
				int index = runtimes.indexOf(runtime);
				if (index >= 0)
					runtimeCombo.select(index);
				
				runtimeCombo.addSelectionListener(new SelectionListener() {
					public void widgetSelected(SelectionEvent e) {
						try {
							if (updating)
								return;
							updating = true;
							IRuntime newRuntime = (IRuntime) runtimes.get(runtimeCombo.getSelectionIndex());
							getCommandManager().executeCommand(new SetServerRuntimeCommand(getServer(), newRuntime));
							updating = false;
						} catch (Exception ex) { }
					}
					public void widgetDefaultSelected(SelectionEvent e) {
						widgetSelected(e);
					}
				});
			}
		}
		
		insertSections(leftColumnComp, "org.eclipse.wst.server.editor.overview.left");
		
		// left column
		Composite rightColumnComp = toolkit.createComposite(columnComp);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		rightColumnComp.setLayout(layout);
		rightColumnComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		
		insertSections(rightColumnComp, "org.eclipse.wst.server.editor.overview.right");

		initialize();
	}
	
	protected void updateRuntimeCombo() {
		IRuntimeType runtimeType = server.getServerType().getRuntimeType();
		runtimes = ServerCore.getResourceManager().getRuntimes(runtimeType);
		
		if (SocketUtil.isLocalhost(server.getHostname())) {
			int size = runtimes.size();
			int i = 0;
			while (i < size) {
				IRuntime runtime2 = (IRuntime) runtimes.get(i);
				if (runtime2.getAttribute("stub", false)) {
					size --;
					runtimes.remove(i);
				} else
					i++;
			}
		}
		
		int size = runtimes.size();
		String[] items = new String[size];
		for (int i = 0; i < size; i++) {
			IRuntime runtime2 = (IRuntime) runtimes.get(i);
			items[i] = runtime2.getName();
		}
		runtimeCombo.setItems(items);
	}

	protected Label createLabel(FormToolkit toolkit, Composite parent, String text) {
		Label label = toolkit.createLabel(parent, text);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		return label;
	}
	
	public void dispose() {
		super.dispose();
		
		if (server != null)
			server.removePropertyChangeListener(listener);
		if (serverConfiguration != null)
			serverConfiguration.removePropertyChangeListener(listener);
	}
	
	/* (non-Javadoc)
	 * Initializes the editor part with a site and input.
	 */
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		
		addChangeListener();
		initialize();
	}
	
	/**
	 * Initialize the fields in this editor.
	 */
	protected void initialize() {
		if (serverName == null)
			return;
		updating = true;
		
		if (server != null) {
			serverName.setText(server.getName());
			if (readOnly)
				serverName.setEnabled(false);
			else
				serverName.setEnabled(true);
		}
		if (serverConfiguration != null) {
			serverConfigurationName.setText(serverConfiguration.getName());
			if (readOnly)
				serverConfigurationName.setEnabled(false);
			else
				serverConfigurationName.setEnabled(true);
		}
		
		updating = false;
	}
	
	protected void validate() { }

	/**
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		if (serverName != null)
			serverName.setFocus();
		else if (serverConfigurationName != null)
			serverConfigurationName.setFocus();
	}
}