/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.*;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.XMLUtil;
import org.eclipse.jst.server.tomcat.core.internal.xml.server32.*;
import org.eclipse.osgi.util.NLS;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import org.eclipse.wst.server.core.ServerPort;
/**
 * Tomcat v3.2 server configuration.
 */
public class Tomcat32Configuration extends TomcatConfiguration {
	protected static final String HTTP_HANDLER = "org.apache.tomcat.service.http.HttpConnectionHandler";
	protected static final String APACHE_HANDLER = "org.apache.tomcat.service.connector.Ajp12ConnectionHandler";
	protected static final String SSL_SOCKET_FACTORY = "org.apache.tomcat.net.SSLSocketFactory";

	protected Server server;
	protected Factory serverFactory;
	protected boolean isServerDirty;

	protected WebAppDocument webAppDocument;

	protected Document tomcatUsersDocument;

	protected String policyFile;

	/**
	 * Tomcat32Configuration constructor.
	 * 
	 * @param path a path
	 */
	public Tomcat32Configuration(IFolder path) {
		super(path);
	}

	/**
	 * Returns the main server port.
	 * @return TomcatServerPort
	 */
	public ServerPort getMainPort() {
		Iterator iterator = getServerPorts().iterator();
		while (iterator.hasNext()) {
			ServerPort port = (ServerPort) iterator.next();
			if (port.getName().equals("HTTP Connector"))
				return port;
		}
		return null;
	}

	/**
	 * Returns the prefix that is used in front of the
	 * web module path property. (e.g. "webapps")
	 *
	 * @return java.lang.String
	 */
	public String getDocBasePrefix() {
		return "webapps/";
	}

	/**
	 * Returns the mime mappings.
	 * @return java.util.List
	 */
	public List getMimeMappings() {
		if (webAppDocument == null)
			return new ArrayList(0);
		
		return webAppDocument.getMimeMappings();
	}

	/**
	 * Returns the server object (root of server.xml).
	 * @return org.eclipse.jst.server.tomcat.internal.xml.server32.Server
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * Returns a list of ServerPorts that this configuration uses.
	 *
	 * @return java.util.List
	 */
	public List getServerPorts() {
		List ports = new ArrayList();
	
		try {
			int count = server.getContextManager().getConnectorCount();
			for (int i = 0; i < count; i++) {
				Connector connector = server.getContextManager().getConnector(i);
				int paramCount = connector.getParameterCount();
				String handler = null;
				String name = Messages.portUnknown;
				String socketFactory = null;
				String protocol = "TCPIP";
				boolean advanced = true;
				String[] contentTypes = null;
				int port = -1;
				for (int j = 0; j < paramCount; j++) {
					Parameter p = connector.getParameter(j);
					if ("port".equals(p.getName())) {
						try {
							port = Integer.parseInt(p.getValue());
						} catch (Exception e) {
							// ignore
						}
					} else if ("handler".equals(p.getName()))
						handler = p.getValue();
					else if ("socketFactory".equals(p.getName()))
						socketFactory = p.getValue();
				}
				if (HTTP_HANDLER.equals(handler)) {
					protocol = "HTTP";
					contentTypes = new String[] { "web", "webservices" };
					if (SSL_SOCKET_FACTORY.equals(socketFactory)) {
						protocol = "SSL";
						name = "SSL Connector";
					} else {
						name = "HTTP Connector";
						advanced = false;
					}
				} else if (APACHE_HANDLER.equals(handler))
					name = "Apache Connector";
				if (handler != null)
					ports.add(new ServerPort(i + "", name, port, protocol, contentTypes, advanced));
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error getting server ports", e);
		}
	
		return ports;
	}
	
	/**
	 * Returns the tomcat-users.xml document.
	 *
	 * @return org.w3c.dom.Document
	 */
	public Document getTomcatUsersDocument() {
		return tomcatUsersDocument;
	}
	
	/**
	 * Return a list of the web modules in this server.
	 * @return java.util.List
	 */
	public List getWebModules() {
		List list = new ArrayList();
	
		try {
			ContextManager contextManager = server.getContextManager();
	
			int size = contextManager.getContextCount();
			for (int i = 0; i < size; i++) {
				Context context = contextManager.getContext(i);
				String reload = context.getReloadable();
				if (reload == null)
					reload = "false";
				WebModule module = new WebModule(context.getPath(), 
					context.getDocBase(), context.getSource(),
					reload.equalsIgnoreCase("true") ? true : false);
				list.add(module);
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error getting project refs", e);
		}
	
		return list;
	}
	
	/**
	 * @see TomcatConfiguration#load(IPath, IProgressMonitor)
	 */
	public void load(IPath path, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.loadingTask, 5);
	
			// check for tomcat.policy to verify that this is a v3.2 config
			InputStream in = new FileInputStream(path.append("tomcat.policy").toFile());
			in.read();
			in.close();
			monitor.worked(1);
			
			// create server.xml
			serverFactory = new Factory();
			serverFactory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server32");
			server = (Server) serverFactory.loadDocument(new FileInputStream(path.append("server.xml").toFile()));
			monitor.worked(1);
	
			webAppDocument = new WebAppDocument(path.append("web.xml"));
			monitor.worked(1);
			
			tomcatUsersDocument = XMLUtil.getDocumentBuilder().parse(new InputSource(new FileInputStream(path.append("tomcat-users.xml").toFile())));
			monitor.worked(1);
	
			// load policy file
			policyFile = TomcatVersionHelper.getFileContents(new FileInputStream(path.append("tomcat.policy").toFile()));
			monitor.worked(1);
	
			if (monitor.isCanceled())
				return;
	
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not load Tomcat v3.2 configuration from " + path.toOSString() + ": " + e.getMessage());
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotLoadConfiguration, path.toOSString()), e));
		}
	}
	
	/**
	 * @see TomcatConfiguration#load(IFolder, IProgressMonitor)
	 */
	public void load(IFolder folder, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.loadingTask, 800);
	
			// check for tomcat.policy to verify that this is a v3.2 config
			IFile file = folder.getFile("tomcat.policy");
			if (!file.exists())
				throw new CoreException(new Status(IStatus.WARNING, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotLoadConfiguration, folder.getFullPath().toOSString()), null));
	
			// load server.xml
			file = folder.getFile("server.xml");
			InputStream in = file.getContents();
			serverFactory = new Factory();
			serverFactory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server32");
			server = (Server) serverFactory.loadDocument(in);
			monitor.worked(200);
	
			// load web.xml
			file = folder.getFile("web.xml");
			webAppDocument = new WebAppDocument(file);
			monitor.worked(200);
	
			// load tomcat-users.xml
			file = folder.getFile("tomcat-users.xml");
			in = file.getContents();
			
			tomcatUsersDocument = XMLUtil.getDocumentBuilder().parse(new InputSource(in));
			monitor.worked(200);
	
			// load tomcat.policy
			file = folder.getFile("tomcat.policy");
			in = file.getContents();
			policyFile = TomcatVersionHelper.getFileContents(in);
			monitor.worked(200);
	
			if (monitor.isCanceled())
				throw new Exception("Cancelled");
	
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not load Tomcat v3.2 configuration from: " + folder.getFullPath() + ": " + e.getMessage());
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotLoadConfiguration, folder.getFullPath().toOSString()), e));
		}
	}
	
	/**
	 * Save the information held by this object to the given directory.
	 * 
	 * @param path a path
	 * @param forceDirty if true, the files will be saved, regardless
	 *  of whether they have been modified
	 * @param monitor a progress monitor
	 * @throws CoreException
	 */
	protected void save(IPath path, boolean forceDirty, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.savingTask, 5);
	
			// make sure directory exists
			if (!path.toFile().exists()) {
				forceDirty = true;
				path.toFile().mkdir();
			}
			monitor.worked(1);
	
			// save files
			if (forceDirty || isServerDirty)
				serverFactory.save(path.append("server.xml").toOSString());
			monitor.worked(1);
	
			webAppDocument.save(path.append("web.xml").toOSString(), forceDirty);
			monitor.worked(1);
	
			if (forceDirty)
				XMLUtil.save(path.append("tomcat-users.xml").toOSString(), tomcatUsersDocument);
			monitor.worked(1);
	
			if (forceDirty) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(path.append("tomcat.policy").toFile()));
				bw.write(policyFile);
				bw.close();
			}
			monitor.worked(1);
			isServerDirty = false;
	
			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save Tomcat v3.2 configuration to " + path, e);
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotSaveConfiguration, new String[] {e.getLocalizedMessage()}), e));
		}
	}
	
	/**
	 * Save the information held by this object to the given directory.
	 * All files are forced to be saved.
	 * 
	 * @param path desination path for the files
	 * @param monitor a progress monitor
	 * @exception CoreException
	 */
	public void save(IPath path, IProgressMonitor monitor) throws CoreException {
		save(path, true, monitor);
	}
	
	/**
	 * Save the information held by this object to the given directory.
	 * 
	 * @param folder a folder
	 * @param monitor a progress monitor
	 * @throws CoreException
	 */
	public void save(IFolder folder, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.savingTask, 900);
	
			if (!folder.exists())
				folder.create(true, true, ProgressUtil.getSubMonitorFor(monitor, 100));
			else
				monitor.worked(100);
	
			// save server.xml
			byte[] data = serverFactory.getContents();
			InputStream in = new ByteArrayInputStream(data);
			IFile file = folder.getFile("server.xml");
			if (file.exists()) {
				if (isServerDirty)
					file.setContents(in, true, true, ProgressUtil.getSubMonitorFor(monitor, 200));
				else
					monitor.worked(200);
			} else
				file.create(in, true, ProgressUtil.getSubMonitorFor(monitor, 200));
	
			// save web.xml
			file = folder.getFile("web.xml");
			webAppDocument.save(file, ProgressUtil.getSubMonitorFor(monitor, 200));
	
			// save tomcat-users.xml
			data = XMLUtil.getContents(tomcatUsersDocument);
			in = new ByteArrayInputStream(data);
			file = folder.getFile("tomcat-users.xml");
			if (file.exists())
				monitor.worked(200);
				//file.setContents(in, true, true, ProgressUtil.getSubMonitorFor(monitor, 200));
			else
				file.create(in, true, ProgressUtil.getSubMonitorFor(monitor, 200));
	
			// save tomcat.policy
			in = new ByteArrayInputStream(policyFile.getBytes());
			file = folder.getFile("tomcat.policy");
			if (file.exists())
				monitor.worked(200);
				//file.setContents(in, true, true, ProgressUtil.getSubMonitorFor(monitor, 200));
			else
				file.create(in, true, ProgressUtil.getSubMonitorFor(monitor, 200));
	
			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save Tomcat v3.2 configuration to " + folder.getFullPath(), e);
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotSaveConfiguration, new String[] {e.getLocalizedMessage()}), e));
		}
	}
	
	
	/**
	 * @see ITomcatConfigurationWorkingCopy#addMimeMapping(int, IMimeMapping)
	 */
	public void addMimeMapping(int index, IMimeMapping map) {
		webAppDocument.addMimeMapping(index, map);
		firePropertyChangeEvent(ADD_MAPPING_PROPERTY, new Integer(index), map);
	}

	/**
	 * @see ITomcatConfigurationWorkingCopy#addWebModule(int, ITomcatWebModule)
	 */
	public void addWebModule(int index, ITomcatWebModule module) {
		try {
			ContextManager contextManager = server.getContextManager();
			Context context = (Context) contextManager.createElement(index, "Context");
	
			context.setPath(module.getPath());
			context.setDocBase(module.getDocumentBase());
			context.setReloadable(module.isReloadable() ? "true" : "false");
			if (module.getMemento() != null && module.getMemento().length() > 0)
				context.setSource(module.getMemento());
			isServerDirty = true;
			firePropertyChangeEvent(ADD_WEB_MODULE_PROPERTY, null, module);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error adding web module", e);
		}
	}

	/**
	 * Localize the web projects in this configuration.
	 *
	 * @param path a path
	 * @param server2 a server type
	 * @param monitor a progress monitor
	 */
	public void localizeConfiguration(IPath path, TomcatServer server2, IProgressMonitor monitor) {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.updatingConfigurationTask, 100);
			
			Tomcat32Configuration config = new Tomcat32Configuration(null);
			config.load(path, ProgressUtil.getSubMonitorFor(monitor, 30));
			
			if (monitor.isCanceled())
				return;
			
			if (server2.isTestEnvironment()) {
				IPath tomcatPath = path.removeLastSegments(1);
				config.server.getContextManager().setHome(tomcatPath.toOSString());
				config.isServerDirty = true;
			}
			monitor.worked(40);
			
			if (monitor.isCanceled())
				return;
			
			config.save(path, false, ProgressUtil.getSubMonitorFor(monitor, 30));
			
			if (!monitor.isCanceled())
				monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error localizing configuration", e);
		}
	}

	/**
	 * Change the extension of a mime mapping.
	 * 
	 * @param index
	 * @param map
	 */
	public void modifyMimeMapping(int index, IMimeMapping map) {
		webAppDocument.modifyMimeMapping(index, map);
		firePropertyChangeEvent(MODIFY_MAPPING_PROPERTY, new Integer(index), map);
	}
	
	/**
	 * Modify the port with the given id.
	 *
	 * @param id java.lang.String
	 * @param port int
	 */
	public void modifyServerPort(String id, int port) {
		try {
			int con = Integer.parseInt(id);
			Connector connector = server.getContextManager().getConnector(con);
	
			int size = connector.getParameterCount();
			for (int i = 0; i < size; i++) {
				Parameter p = connector.getParameter(i);
				if ("port".equals(p.getName())) {
					p.setValue(port + "");
					isServerDirty = true;
					firePropertyChangeEvent(MODIFY_PORT_PROPERTY, id, new Integer(port));
					return;
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error modifying server port " + id, e);
		}
	}
	
	/**
	 * Change a web module.
	 * @param index int
	 * @param docBase java.lang.String
	 * @param path java.lang.String
	 * @param reloadable boolean
	 */
	public void modifyWebModule(int index, String docBase, String path, boolean reloadable) {
		try {
			ContextManager contextManager = server.getContextManager();
			Context context = contextManager.getContext(index);
			context.setPath(path);
			context.setDocBase(docBase);
			context.setReloadable(reloadable ? "true" : "false");
			isServerDirty = true;
			WebModule module = new WebModule(path, docBase, null, reloadable);
			firePropertyChangeEvent(MODIFY_WEB_MODULE_PROPERTY, new Integer(index), module);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error modifying web module " + index, e);
		}
	}
	
	/**
	 * Removes a mime mapping.
	 * @param index int
	 */
	public void removeMimeMapping(int index) {
		webAppDocument.removeMimeMapping(index);
		firePropertyChangeEvent(REMOVE_MAPPING_PROPERTY, null, new Integer(index));
	}
	
	/**
	 * Removes a web module.
	 * @param index int
	 */
	public void removeWebModule(int index) {
		try {
			ContextManager contextManager = server.getContextManager();
			contextManager.removeElement("Context", index);
			isServerDirty = true;
			firePropertyChangeEvent(REMOVE_WEB_MODULE_PROPERTY, null, new Integer(index));
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error removing web module " + index, e);
		}
	}
}