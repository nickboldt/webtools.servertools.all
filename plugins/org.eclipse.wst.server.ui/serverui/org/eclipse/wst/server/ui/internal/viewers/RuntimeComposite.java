/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.internal.ResourceManager;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * 
 */
public class RuntimeComposite extends AbstractTableComposite {
	protected IRuntime selection;
	protected IRuntime defaultRuntime;
	protected RuntimeSelectionListener listener;
	
	public interface RuntimeSelectionListener {
		public void runtimeSelected(IRuntime runtime);
	}
	
	class RuntimeViewerSorter extends ViewerSorter {
		boolean sortByName;
		public RuntimeViewerSorter(boolean sortByName) {
			this.sortByName = sortByName;
		}
		
		public int compare(Viewer viewer, Object e1, Object e2) {
			IRuntime r1 = (IRuntime) e1;
			IRuntime r2 = (IRuntime) e2;
			if (sortByName)
				return collator.compare(notNull(r1.getName()), notNull(r2.getName()));
			
			if (r1.getRuntimeType() == null)
				return -1;
			if (r2.getRuntimeType() == null)
				return 1;
			return collator.compare(notNull(r1.getRuntimeType().getName()), notNull(r2.getRuntimeType().getName()));
		}
		
		protected String notNull(String s) {
			if (s == null)
				return "";
			return s;
		}
	}
	
	public RuntimeComposite(Composite parent, int style, RuntimeSelectionListener listener2) {
		super(parent, style);
		this.listener = listener2;
		
		TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);
		table.setHeaderVisible(true);

		tableLayout.addColumnData(new ColumnWeightData(60, 160, true));
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText(Messages.columnName);
		col.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new RuntimeViewerSorter(true));
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		tableLayout.addColumnData(new ColumnWeightData(45, 125, true));
		col = new TableColumn(table, SWT.NONE);
		col.setText(Messages.columnType);
		col.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new RuntimeViewerSorter(false));
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		tableViewer.setContentProvider(new RuntimeContentProvider());
		tableViewer.setLabelProvider(new RuntimeTableLabelProvider());
		tableViewer.setInput(AbstractTreeContentProvider.ROOT);
		tableViewer.setColumnProperties(new String[] {"name", "type"});
		tableViewer.setSorter(new RuntimeViewerSorter(true));

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object obj = getSelection(event.getSelection());
				if (obj instanceof IRuntime)
					selection = (IRuntime) obj;
				else
					selection = null;
				listener.runtimeSelected(selection);
			}
		});
		
		table.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.character == 'l') {
					try {
						IRuntime runtime = getSelectedRuntime();
						IRuntimeWorkingCopy wc = runtime.createWorkingCopy();
						wc.setReadOnly(!runtime.isReadOnly());
						wc.save(false, null);
						refresh(runtime);
					} catch (Exception ex) {
						// ignore
					}
				}
			}

			public void keyReleased(KeyEvent e) {
				// do nothing
			}
		});
		
		final ResourceManager rm = ResourceManager.getInstance();
		defaultRuntime = rm.getDefaultRuntime();
		if (defaultRuntime != null)
			((CheckboxTableViewer)tableViewer).setChecked(defaultRuntime, true);

		((CheckboxTableViewer)tableViewer).addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				try {
					IRuntime runtime = (IRuntime) event.getElement();
					if (event.getChecked()) {
						if (defaultRuntime != null && !runtime.equals(defaultRuntime))
							((CheckboxTableViewer)tableViewer).setChecked(defaultRuntime, false);
						rm.setDefaultRuntime(runtime);
						defaultRuntime = runtime;
					} else
						rm.setDefaultRuntime(null);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error setting default runtime", e);
				}
			}
		});
	}

	protected void createTable() {
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.CHECK);
	}

	protected void createTableViewer() {
		tableViewer = new LockedCheckboxTableViewer(table);
	}

	public IRuntime getSelectedRuntime() {
		return selection;
	}
}