/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.ui.internal;

import org.eclipse.osgi.util.NLS;
/**
 * Translated messages.
 */
public class Messages extends NLS {
	public static String wizardTitle;
	public static String wizardDescription;
	public static String runtimeName;
	public static String installDir;
	public static String browse;
	public static String install;
	public static String selectInstallDir;
	public static String installedJRE;
	public static String installedJREs;
	public static String runtimeDefaultJRE;
	public static String editorAdd;
	public static String editorEdit;
	public static String editorRemove;
	public static String editorBrowse;
	public static String errorMissingWebModule;
	public static String configurationEditorMimeMappingsSection;
	public static String configurationEditorMimeMappingsDescription;
	public static String configurationEditorPortsSection;
	public static String configurationEditorPortsDescription;
	public static String configurationEditorPortNameColumn;
	public static String configurationEditorPortValueColumn;
	public static String configurationEditorWebModulesPageTitle;
	public static String configurationEditorWebModulesSection;
	public static String configurationEditorWebModulesDescription;
	public static String configurationEditorPathColumn;
	public static String configurationEditorDocBaseColumn;
	public static String configurationEditorProjectColumn;
	public static String configurationEditorReloadColumn;
	public static String configurationEditorAddProjectModule;
	public static String configurationEditorAddExternalModule;
	public static String configurationEditorProjectMissing;
	public static String configurationEditorReloadEnabled;
	public static String configurationEditorReloadDisabled;
	public static String configurationEditorMimeMapppingDialogTitleEdit;
	public static String configurationEditorMimeMapppingDialogTitleAdd;
	public static String configurationEditorMimeMapppingDialogMimeType;
	public static String configurationEditorMimeMapppingDialogMimeExtension;
	public static String serverEditorLocationsSection;
	public static String serverEditorLocationsDescription;
	public static String serverEditorLocationsDescription2;
	public static String serverEditorGeneralSection;
	public static String serverEditorGeneralDescription;
	public static String serverEditorSetInternalServerDirLink;
	public static String serverEditorSetInternalServerDirLink2;
	public static String serverEditorSetInstallServerDirLink;
	public static String serverEditorSetInstallServerDirLink2;
	public static String serverEditorSetDefaultDeployDirLink;
	public static String serverEditorSetDefaultDeployDirLink2;
	public static String serverEditorServerDir;
	public static String serverEditorDeployDir;
	public static String serverEditorTestEnvironment;
	public static String serverEditorNoPublish;
	public static String serverEditorSecure;
	public static String serverEditorDebugMode;
	public static String serverEditorNotSupported;
	public static String errorNoPublishServerMustBeStopped;
	public static String errorServerDirIsRoot;
	public static String errorServerDirUnderRoot;
	public static String errorDeployDirNotSpecified;
	public static String serverEditorBrowseServerMessage;
	public static String serverEditorBrowseDeployMessage;
	public static String configurationEditorWebModuleDialogTitleEdit;
	public static String configurationEditorWebModuleDialogTitleAdd;
	public static String configurationEditorWebModuleDialogProjects;
	public static String configurationEditorWebModuleDialogDocumentBase;
	public static String configurationEditorWebModuleDialogSelectDirectory;
	public static String configurationEditorWebModuleDialogPath;
	public static String configurationEditorWebModuleDialogReloadEnabled;
	public static String errorDefaultDialogTitle;
	public static String confirmCleanWorkDirTitle;
	public static String cleanServerStateChanging;
	public static String cleanModuleWorkDir;
	public static String cleanServerWorkDir;
	public static String cleanServerRunning;
	public static String cleanServerTask;
	public static String errorCleanModuleTitle;
	public static String errorCleanServerTitle;
	public static String errorCouldNotCleanModule;
	public static String errorCouldNotCleanServer;
	public static String errorCouldNotCleanStateChange;
	public static String errorCouldNotCleanCantStop;
	public static String errorCouldNotCleanStopFailed;
	public static String errorCleanFailedModule;
	public static String errorCleanFailedServer;
	public static String errorCantIdentifyWebApp;
	public static String errorCantIdentifyWebAppWasRunning;
	public static String errorErrorDuringClean;
	public static String errorErrorDuringCleanWasRunning;
	public static String errorCleanNoRestartModule;
	public static String errorCleanNoRestartServer;
	public static String errorCleanCantRestart;
	public static String errorCleanRestartFailed;

	static {
		NLS.initializeMessages(TomcatUIPlugin.PLUGIN_ID + ".internal.Messages", Messages.class);
	}
}