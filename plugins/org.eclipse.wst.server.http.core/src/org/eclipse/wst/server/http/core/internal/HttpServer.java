/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.http.core.internal;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.core.model.IURLProvider;
import org.eclipse.wst.server.core.model.ServerDelegate;
/**
 * Generic HTTP server.
 */
public class HttpServer extends ServerDelegate implements IURLProvider {
	public static final String PROPERTY_BASE_URL = "baseUrl";
	public static final String PROPERTY_URL_PREFIX = "urlPrefix";
	// public static final String DOCUMENT_ROOT = "document_root";
	// public static final String PUBLISH = "publish";
	public static final String PROPERTY_PORT = "port";
	//public static final String PROPERTY_PUB_DIR = "publish_directory";
	public static final String PROPERTY_IS_PUBLISHING = "isPublishing";

	public static final String ID = "org.eclipse.wst.server.http.server";

	/**
	 * HttpServer.
	 */
	public HttpServer() {
		super();
	}

	protected void initialize() {
		// do nothing
	}

	/*
	 * @see RuntimeDelegate#setDefaults(IProgressMonitor)
	 */
	public void setDefaults(IProgressMonitor monitor) {
		setPort(80);
		setURLPrefix("");
		setPublishing(true);
	}

	public HttpRuntime getHttpRuntime() {
		if (getServer().getRuntime() == null)
			return null;

		return (HttpRuntime) getServer().getRuntime().loadAdapter(HttpRuntime.class, null);
	}

	/*public void importRuntimeConfiguration(IRuntime arg0, IProgressMonitor arg1) throws CoreException {
		if (getHttpRuntime() != null) {
			if (!getHttpRuntime().publishToDirectory()) {
				setAttribute("auto-publish-setting", 1);
			} else {
				setAttribute("auto-publish-setting", 2);
				setAttribute("auto-publish-time", 1);
			}
			//setPublishDirectory(getHttpRuntime().getPublishLocation());
		}
	}*/

	public String getBaseURL() {
		return getAttribute(HttpServer.PROPERTY_BASE_URL, "");
	}

	public boolean dontPublish() {
		return getAttribute("auto-publish-setting", "2").equals("1");
	}

	public void setBaseURL(String url) {
		setAttribute(HttpServer.PROPERTY_BASE_URL, url);
	}

	// public void setDocumentRoot(String docRoot) {
	// setAttribute(HttpServer.DOCUMENT_ROOT, docRoot);
	// }

	// public String getDocumentRoot() {
	// return getAttribute(HttpServer.DOCUMENT_ROOT, "");
	// }

	// public boolean canPublish() {
	// return getAttribute(HttpServer.PUBLISH, true);
	// }

	// public void setPublish(boolean publish) {
	// setAttribute(HttpServer.PUBLISH, publish);
	// }

	/**
	 * Return the root URL of this module.
	 * 
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return java.net.URL
	 */
	public URL getModuleRootURL(IModule module) {
		try {
			String base = getBaseURL();
			if (base.equals(""))
				base = "http://" + getServer().getHost();

			int port = getPort();
			if (port == 80)
				return new URL(base + "/");
			
			return new URL(base + ":" + port + "/");
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not get root URL", e);
			return null;
		}
	}

	/*
	 * Returns the child module(s) of this module.
	 */
	public IModule[] getChildModules(IModule[] module) {
		return new IModule[0];
	}

	/*
	 * Returns the root module(s) of this module.
	 */
	public IModule[] getRootModules(IModule module) throws CoreException {
		return new IModule[] { module };
	}

	/**
	 * Returns true if the given project is supported by this server, and false
	 * otherwise.
	 * 
	 * @param add modules
	 * @param remove modules
	 * @return the status
	 */
	public IStatus canModifyModules(IModule[] add, IModule[] remove) {
		return new Status(IStatus.OK, HttpCorePlugin.PLUGIN_ID, 0, Messages.canModifyModules, null);
	}

	public ServerPort[] getServerPorts() {
		int port = getPort();
		ServerPort[] ports = { new ServerPort("http", Messages.httpPort, port, "http") };
		return ports;
	}

	public int getPort() {
		return getAttribute(HttpServer.PROPERTY_PORT, 80);
	}

	public void setPort(int port) {
		setAttribute(HttpServer.PROPERTY_PORT, port);
	}

	public void setURLPrefix(String prefix) {
		setAttribute(HttpServer.PROPERTY_URL_PREFIX, prefix);
	}

	public String getURLPrefix() {
		return getAttribute(HttpServer.PROPERTY_URL_PREFIX, "");
	}

	public boolean isPublishing() {
		return getAttribute(PROPERTY_IS_PUBLISHING, false);
	}

	public void setPublishing(boolean shouldPublish) {
		setAttribute(PROPERTY_IS_PUBLISHING, shouldPublish);
	}

	public static IServer createHttpServer(String serverName, String baseURL) {
		String host = baseURL;
		if (baseURL.startsWith("http://"))
			host = host.substring(7);
		int index = host.indexOf("/");
		if (index != -1)
			host = host.substring(0, index - 1);
		index = host.indexOf(":");
		if (index != -1)
			host = host.substring(0, index - 1);

		try {
			NullProgressMonitor monitor = new NullProgressMonitor();
			IRuntimeType runtimeType = ServerCore.findRuntimeType(HttpRuntime.ID);
			IRuntimeWorkingCopy runtimeCopy = runtimeType.createRuntime(HttpRuntime.ID, monitor);
			IRuntime runtime = runtimeCopy.save(true, monitor);
			
			IServerType serverType = ServerCore.findServerType(ID);
			IServerWorkingCopy workingCopy = serverType.createServer(ID, null, runtime, monitor);
			workingCopy.setName(serverName);
			workingCopy.setHost(host);
			
			HttpServer hs = (HttpServer) workingCopy.loadAdapter(HttpServer.class, null);

			hs.setBaseURL(baseURL);
			hs.saveConfiguration(null);
			return workingCopy.save(true, monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error creating server", e);
		}
		
		return null;
	}

	public static IServer findHttpServer(String id) {
		IServer[] servers = ServerCore.getServers();
		for (int i = 0; i < servers.length; i++) {
			if (servers[i].getId().equals(id)) {
				return servers[i];
			}
		}
		return null;
	}

	/*public void setPublishDirectory(String pubDir) {
		setAttribute(PROPERTY_PUB_DIR, pubDir);
	}

	public String getPublishDirectory() {
		if (getHttpRuntime() != null)
			return getAttribute(PROPERTY_PUB_DIR, getHttpRuntime().getPublishLocation());
		return getAttribute(PROPERTY_PUB_DIR, "");
	}*/

	/*
	 * public static void updateBaseURL(String id, String baseURL) {
	 * updateBaseURL(checkForHttpServer(id), baseURL); }
	 */

	/*
	 * public static void updateBaseURL(IServer server, String baseURL) { if
	 * (server == null) return;
	 * 
	 * IServerWorkingCopy workingCopy = server.createWorkingCopy();
	 * 
	 * HttpServer as = (HttpServer) workingCopy.getAdapter(HttpServer.class); if
	 * (as == null) as = (HttpServer) workingCopy.loadAdapter(HttpServer.class,
	 * null);
	 * 
	 * String currentURL = as.getBaseURL();
	 * 
	 * if (currentURL.equals(baseURL)) return; as.setBaseURL(baseURL); try {
	 * as.saveConfiguration(null); workingCopy.save(true, null); } catch
	 * (CoreException e) { e.printStackTrace(); } }
	 */

	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {
		// do nothing
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "HttpServer";
	}
}