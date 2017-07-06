/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
/**
 * Notification manager for server.
 */
public class ServerNotificationManager {
	private List listenerList = new ArrayList();

	/**
	 * For masking event on all changes.
	 */
	public static final int ALL_EVENTS = 0xFFFF;

	private class ListenerEntry {
		private IServerListener listener;
		private int eventMask;

		protected ListenerEntry(IServerListener curListener, int curEventMask) {
			listener = curListener;
			eventMask = curEventMask;
		}

		protected IServerListener getListener() {
			return listener;
		}

		protected int getEventMask() {
			return eventMask;
		}
	}

	/**
	 * Create a new notification manager.
	 */
	public ServerNotificationManager() {
		super();
	}

	/**
	 * Add listener for all events.
	 * 
	 * @param curListener
	 */
	public void addListener(IServerListener curListener) {
		addListener(curListener, ALL_EVENTS);
	}
	
	/**
	 * Add listener for the events specified by the mask.
	 * 
	 * @param curListener
	 * @param eventMask
	 */
	public void addListener(IServerListener curListener, int eventMask) {
		Trace.trace(Trace.FINEST, "->- Adding server listener to notification manager: " + curListener + " " + eventMask + " ->-");
		if (curListener == null) {
			return;
		}
		
		synchronized (listenerList) {
			listenerList.add(new ListenerEntry(curListener, eventMask));
		}
	}

	protected void broadcastChange(ServerEvent event) {
		Trace.trace(Trace.FINEST, "->- Broadcasting server event: " + event + " ->-");
		if (event == null) {
			return;
		}
		int eventKind = event.getKind();
		Trace.trace(Trace.FINEST, "  Server event kind: " + eventKind + " ->-");
		
		// only notify listeners that listen to module event
		int size;
		ListenerEntry[] listeners;
		synchronized (listenerList) {
			size = listenerList.size();
			listeners = (ListenerEntry[]) listenerList.toArray(new ListenerEntry[size]);
		}
		for (int i = 0; i < size; i++) {
			ListenerEntry curEntry = listeners[i];
			int mask = curEntry.getEventMask();

			// check if the type of the event matches the mask, e.g. server or module change
			boolean isTypeMatch = ((mask & eventKind & ServerEvent.SERVER_CHANGE) != 0) 
					|| ((mask & eventKind & ServerEvent.MODULE_CHANGE) != 0);
			// check the kind of change
			boolean isKindMatch = (mask & eventKind ^ ServerEvent.SERVER_CHANGE ^ ServerEvent.MODULE_CHANGE) != 0;
			
			if (isTypeMatch && isKindMatch) {
				Trace.trace(Trace.FINEST, "->- Firing server event to listener: " + curEntry.getListener() + " ->-");
				try {
					Trace.trace(Trace.LISTENERS, "  Firing server event to listener: " + curEntry.getListener());
					curEntry.getListener().serverChanged(event);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "  Error firing server event: " + curEntry.getListener(), e);
				}
				Trace.trace(Trace.LISTENERS, "-<- Done Firing server event -<-");
			}
		}
		Trace.trace(Trace.FINEST, "-<- Done broadcasting server event -<-");
	}
	
	/**
	 * Returns true if the listener list is not empty; otherwise, returns false.
	 * 
	 * @return true if the listener list is not empty; otherwise, returns false
	 */
	protected boolean hasListenerEntries() {
		return listenerList.size() == 0;
	}
	
	/**
	 * Remove a listener from notification.
	 * 
	 * @param curListener
	 */
	public void removeListener(IServerListener curListener) {
		Trace.trace(Trace.FINEST, "->- Removing server listener from notification manager: " + curListener + " ->-");
		if (curListener == null) {
			return;
		}
		ListenerEntry matchedListenerEntry = null;
		Iterator listenerIter = listenerList.iterator();
		while (matchedListenerEntry == null && listenerIter.hasNext()) {
			ListenerEntry curEntry = (ListenerEntry)listenerIter.next();
			if (curListener.equals(curEntry.getListener())) {
				matchedListenerEntry = curEntry;
			}
		}
		if (matchedListenerEntry != null) {
			synchronized (listenerList) {
				listenerList.remove(matchedListenerEntry);
			}
		}
	}
}