/*
 * Copyright (C) 2008 Andreas Schnaiter
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */
package org.afraid.poison.db2php;

import java.awt.Component;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.afraid.poison.db2php.generator.CodeGeneratorSettings;
import org.afraid.poison.db2php.generator.PhpCodeGenerator;
import org.afraid.poison.db2php.generator.Table;
import org.afraid.poison.db2php.generator.databaselayer.DatabaseLayer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;

public final class PhpClassWizardIterator implements WizardDescriptor.InstantiatingIterator {

	private int index;
	private WizardDescriptor wizard;
	private WizardDescriptor.Panel[] panels;

	/**
	 * Initialize panels representing individual wizard's steps and sets
	 * various properties for them influencing wizard appearance.
	 */
	private WizardDescriptor.Panel[] getPanels() {
		if (panels==null) {
			panels=new WizardDescriptor.Panel[]{
						new PhpClassWizardPanel1(wizard),
						new PhpClassWizardPanel2(wizard)
					};
			String[] steps=createSteps();
			for (int i=0; i<panels.length; i++) {
				Component c=panels[i].getComponent();
				if (steps[i]==null) {
					// Default step name to component name of panel. Mainly
					// useful for getting the name of the target chooser to
					// appear in the list of steps.
					steps[i]=c.getName();
				}
				if (c instanceof JComponent) { // assume Swing components
					JComponent jc=(JComponent) c;
					// Sets step number of a component
					// TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
					jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
					// Sets steps names for a panel
					jc.putClientProperty("WizardPanel_contentData", steps);
					// Turn on subtitle creation on each step
					jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
					// Show steps on the left side with the image on the background
					jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
					// Turn on numbering of all steps
					jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
				}
			}

		}
		return panels;
	}

	private Set<Table> writeCode(Set<Table> tables, CodeGeneratorSettings settings) {
		Set<Table> failed=new LinkedHashSet<Table>();
		PhpCodeGenerator generator;
		for (Table t : tables) {
			generator=new PhpCodeGenerator(t);
			generator.setSettings(settings);
			try {
				generator.writeCode();
			} catch (IOException ex) {
				failed.add(t);
			}
		}
		return failed;
	}

	public Set instantiate() throws IOException {
		if (wizard.getValue()==WizardDescriptor.FINISH_OPTION) {
			PhpClassVisualPanel1 p1=(PhpClassVisualPanel1) getPanels()[0].getComponent();
			Set<Table> tables=p1.getSelected();

			PhpClassVisualPanel2 p2=(PhpClassVisualPanel2) getPanels()[1].getComponent();
			CodeGeneratorSettings settings=new CodeGeneratorSettings();
			settings.setDatabaseLayer((DatabaseLayer) p2.getDatabaseLayerSelection().getSelectedItem());
			settings.setGenerateChecks(p2.getGenerateChecksSelection().isSelected());
			settings.setTrackFieldModifications(p2.getTrackModificationsSelection().isSelected());
			settings.setClassNamePrefix(p2.getClassNamePrefix().getText());
			settings.setClassNamePrefix(p2.getClassNameSuffix().getText());
			settings.setOutputDirectory(p2.getDirectory());

			Set<Table> failed=writeCode(tables, settings);
			DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(settings));

		}
		return Collections.EMPTY_SET;
	}

	public void initialize(WizardDescriptor wizard) {
		this.wizard=wizard;

	}

	public void uninitialize(WizardDescriptor wizard) {
		panels=null;
	}

	public WizardDescriptor.Panel current() {
		return getPanels()[index];
	}

	public String name() {
		return index+1+". from "+getPanels().length;
	}

	public boolean hasNext() {
		return index<getPanels().length-1;
	}

	public boolean hasPrevious() {
		return index>0;
	}

	public void nextPanel() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		index++;
	}

	public void previousPanel() {
		if (!hasPrevious()) {
			throw new NoSuchElementException();
		}
		index--;
	}
	// If something changes dynamically (besides moving between panels), e.g.
	// the number of panels changes in response to user input, then uncomment
	// the following and call when needed: fireChangeEvent();
	private Set<ChangeListener> listeners=new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0

	public final void addChangeListener(ChangeListener l) {
		synchronized (listeners) {
			listeners.add(l);
		}
	}

	public final void removeChangeListener(ChangeListener l) {
		synchronized (listeners) {
			listeners.remove(l);
		}
	}

	protected final void fireChangeEvent() {
		Iterator<ChangeListener> it;
		synchronized (listeners) {
			it=new HashSet<ChangeListener>(listeners).iterator();
		}
		ChangeEvent ev=new ChangeEvent(this);
		while (it.hasNext()) {
			it.next().stateChanged(ev);
		}
	}

	// You could safely ignore this method. Is is here to keep steps which were
	// there before this wizard was instantiated. It should be better handled
	// by NetBeans Wizard API itself rather than needed to be implemented by a
	// client code.
	private String[] createSteps() {
		String[] beforeSteps=null;
		Object prop=wizard.getProperty("WizardPanel_contentData");
		if (prop!=null&&prop instanceof String[]) {
			beforeSteps=(String[]) prop;
		}

		if (beforeSteps==null) {
			beforeSteps=new String[0];
		}

		String[] res=new String[(beforeSteps.length-1)+panels.length];
		for (int i=0; i<res.length; i++) {
			if (i<(beforeSteps.length-1)) {
				res[i]=beforeSteps[i];
			} else {
				res[i]=panels[i-beforeSteps.length+1].getComponent().getName();
			}
		}
		return res;
	}
}
