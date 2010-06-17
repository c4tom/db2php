/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.afraid.poison.db2php;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;

public final class PhpClassesFromXmlWizardIterator implements WizardDescriptor.InstantiatingIterator {

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
						new PhpClassesFromXmlWizardPanel1()
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

	@Override
	public Set instantiate() throws IOException {
		if (wizard.getValue()==WizardDescriptor.FINISH_OPTION) {
			PhpClassesFromXmlWizardPanel1 panel1=(PhpClassesFromXmlWizardPanel1) getPanels()[0];
			
		}
		return Collections.EMPTY_SET;
	}

	@Override
	public void initialize(WizardDescriptor wizard) {
		this.wizard=wizard;
	}

	@Override
	public void uninitialize(WizardDescriptor wizard) {
		panels=null;
	}

	@Override
	public WizardDescriptor.Panel current() {
		return getPanels()[index];
	}

	@Override
	public String name() {
		return index+1+". from "+getPanels().length;
	}

	@Override
	public boolean hasNext() {
		return index<getPanels().length-1;
	}

	@Override
	public boolean hasPrevious() {
		return index>0;
	}

	@Override
	public void nextPanel() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		index++;
	}

	@Override
	public void previousPanel() {
		if (!hasPrevious()) {
			throw new NoSuchElementException();
		}
		index--;
	}
	// If nothing unusual changes in the middle of the wizard, simply:
	// If something changes dynamically (besides moving between panels), e.g.
	// the number of panels changes in response to user input, then uncomment
	// the following and call when needed: fireChangeEvent();
	private Set<ChangeListener> listeners=new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0

	@Override
	public final void addChangeListener(ChangeListener l) {
		synchronized (listeners) {
			listeners.add(l);
		}
	}

	@Override
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
