/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
  *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.monitor.ui.internal;

import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.monitor.core.IRequest;
import org.osgi.framework.BundleContext;
/**
 * The TCP/IP monitor UI plugin.
 */
public class MonitorUIPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.eclipse.wst.monitor.ui";

	private static MonitorUIPlugin singleton;

	protected Map imageDescriptors = new HashMap();

	private static URL ICON_BASE_URL;
	private static final String URL_CLCL = "clcl16/";
	private static final String URL_ELCL = "elcl16/";
	private static final String URL_DLCL = "dlcl16/";
	private static final String URL_OBJ = "obj16/";

	public static final String IMG_ELCL_SORT_RESPONSE_TIME = "IMG_ELCL_SORT_RESPONSE_TIME";
	public static final String IMG_ELCL_CLEAR = "IMG_ELCL_CLEAR";
	public static final String IMG_ELCL_HTTP_HEADER = "IMG_ELCL_HTTP_HEADER";
	public static final String IMG_CLCL_SORT_RESPONSE_TIME = "IMG_CLCL_SORT_RESPONSE_TIME";
	public static final String IMG_CLCL_CLEAR = "IMG_CLCL_CLEAR";
	public static final String IMG_CLCL_HTTP_HEADER = "IMG_CLCL_HTTP_HEADER";
	public static final String IMG_DLCL_SORT_RESPONSE_TIME = "IMG_DLCL_SORT_RESPONSE_TIME";
	public static final String IMG_DLCL_CLEAR = "IMG_DLCL_CLEAR";
	public static final String IMG_DLCL_HTTP_HEADER = "IMG_DLCL_HTTP_HEADER";
	
	public static final String IMG_REQUEST_RESPONSE = "requestResponse";
	public static final String IMG_RESEND_REQUEST_RESPONSE = "resendRequestResponse";
	
	public static final String IMG_HOST = "host";
	public static final String IMG_MONITOR_ON = "monitorOn";
	public static final String IMG_MONITOR_OFF = "monitorOff";

	private static final String SHOW_VIEW_ON_ACTIVITY = "show-view";
	private static final String SHOW_HEADER = "show-header";

	/**
	 * MonitorUIPlugin constructor comment.
	 */
	public MonitorUIPlugin() {
		super();
		singleton = this;

		// Create an adapter factory to hold the adapter for IRequests
		IAdapterFactory adaptFact = new IAdapterFactory() {
			private RequestActionFilter reqActionFilter = null;

			public Class[] getAdapterList() {
				return new Class[] { IActionFilter.class };
			}

			public Object getAdapter(Object adaptableObject, Class adapterType) {
				if (adapterType == IActionFilter.class) {
					if (reqActionFilter == null) {
						reqActionFilter = new RequestActionFilter();
					}
					return reqActionFilter;
				}
				return null;
			}
		};

		Platform.getAdapterManager().registerAdapters(adaptFact, IRequest.class);
	}

	/**
	 * Creates and pre-loads the image registry.
	 * 
	 * @return ImageRegistry
	 */
	protected ImageRegistry createImageRegistry() {
		ImageRegistry registry = super.createImageRegistry();

		registerImage(registry, IMG_REQUEST_RESPONSE, URL_OBJ + "tcp.gif");
		registerImage(registry, IMG_RESEND_REQUEST_RESPONSE, URL_ELCL + "resendRequest.gif");
		
		registerImage(registry, IMG_HOST, URL_OBJ + "host.gif");
		registerImage(registry, IMG_MONITOR_ON, URL_OBJ + "monitorOn.gif");
		registerImage(registry, IMG_MONITOR_OFF, URL_OBJ + "monitorOff.gif");

		registerImage(registry, IMG_CLCL_CLEAR, URL_CLCL + "clear.gif");
		registerImage(registry, IMG_CLCL_SORT_RESPONSE_TIME, URL_CLCL + "sortResponseTime.gif");
		registerImage(registry, IMG_CLCL_HTTP_HEADER, URL_CLCL + "httpHeader.gif");

		registerImage(registry, IMG_ELCL_CLEAR, URL_ELCL + "clear.gif");
		registerImage(registry, IMG_ELCL_SORT_RESPONSE_TIME, URL_ELCL + "sortResponseTime.gif");
		registerImage(registry, IMG_ELCL_HTTP_HEADER, URL_ELCL + "httpHeader.gif");

		registerImage(registry, IMG_DLCL_CLEAR, URL_DLCL + "clear.gif");
		registerImage(registry, IMG_DLCL_SORT_RESPONSE_TIME, URL_DLCL + "sortResponseTime.gif");
		registerImage(registry, IMG_DLCL_HTTP_HEADER, URL_DLCL + "httpHeader.gif");

		return registry;
	}

	/**
	 * Return the image with the given key from the image registry.
	 * 
	 * @param key the key
	 * @return the image
	 */
	public static Image getImage(String key) {
		return getInstance().getImageRegistry().get(key);
	}

	/**
	 * Return the image with the given key from the image registry.
	 * 
	 * @param key the key
	 * @return an image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		try {
			getInstance().getImageRegistry();
			return (ImageDescriptor) getInstance().imageDescriptors.get(key);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the singleton instance of this plugin.
	 * 
	 * @return the plugin
	 */
	public static MonitorUIPlugin getInstance() {
		return singleton;
	}

	/**
	 * Returns the translated String found with the given key.
	 * 
	 * @param key the key
	 * @return the translated string
	 */
	public static String getResource(String key) {
		try {
			return Platform.getResourceString(getInstance().getBundle(), key);
		} catch (Exception e) {
			return key;
		}
	}

	/**
	 * Returns the translated String found with the given key, and formatted
	 * with the given object.
	 * 
	 * @param key the key
	 * @param obj substitution variables
	 * @return the translated string
	 */
	public static String getResource(String key, Object[] obj) {
		try {
			return MessageFormat.format(getResource(key), obj);
		} catch (Exception e) {
			return key;
		}
	}

	/**
	 * Returns the translated String found with the given key, and formatted
	 * with the given object.
	 * 
	 * @param key the key
	 * @param s substitution variable
	 * @return the translated string
	 */
	public static String getResource(String key, String s) {
		try {
			return MessageFormat.format(getResource(key), new String[] { s });
		} catch (Exception e) {
			return key;
		}
	}

	/**
	 * Register an image with the registry.
	 * 
	 * @param key the key
	 * @param partialURL
	 */
	private void registerImage(ImageRegistry registry, String key, String partialURL) {
		if (ICON_BASE_URL == null) {
			String pathSuffix = "icons/";
			ICON_BASE_URL = singleton.getBundle().getEntry(pathSuffix);
		}

		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(new URL(ICON_BASE_URL, partialURL));
			registry.put(key, id);
			imageDescriptors.put(key, id);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error registering image", e);
		}
	}

	/**
	 * Start this plug-in.
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		getPreferenceStore().setDefault(MonitorUIPlugin.SHOW_VIEW_ON_ACTIVITY, true);
	}

	public static boolean getDefaultShowOnActivityPreference() {
		return getInstance().getPreferenceStore().getDefaultBoolean(SHOW_VIEW_ON_ACTIVITY);
	}

	public static boolean getShowOnActivityPreference() {
		return getInstance().getPreferenceStore().getBoolean(SHOW_VIEW_ON_ACTIVITY);
	}

	public static void setShowOnActivityPreference(boolean b) {
		getInstance().getPreferenceStore().setValue(SHOW_VIEW_ON_ACTIVITY, b);
		getInstance().savePluginPreferences();
	}

	public static boolean getShowHeaderPreference() {
		return getInstance().getPreferenceStore().getBoolean(SHOW_HEADER);
	}

	public static void setShowHeaderPreference(boolean b) {
		getInstance().getPreferenceStore().setValue(SHOW_HEADER, b);
		getInstance().savePluginPreferences();
	}
}