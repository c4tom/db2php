/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.afraid.poison.db2php;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

public final class PhpClassesFromXmlVisualPanel1 extends JPanel {

	
	/** Creates new form PhpClassesFromXmlVisualPanel1 */
	public PhpClassesFromXmlVisualPanel1() {
		initComponents();
	}

	@Override
	public String getName() {
		return "Choose XML control file";
	}

	public JFileChooser getXmlFileChooser() {
		return xmlFileChooser;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xmlFileChooser = new javax.swing.JFileChooser();
        fileChooserLabel = new javax.swing.JLabel();

        xmlFileChooser.setControlButtonsAreShown(false);

        org.openide.awt.Mnemonics.setLocalizedText(fileChooserLabel, org.openide.util.NbBundle.getMessage(PhpClassesFromXmlVisualPanel1.class, "PhpClassesFromXmlVisualPanel1.fileChooserLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xmlFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xmlFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fileChooserLabel;
    private javax.swing.JFileChooser xmlFileChooser;
    // End of variables declaration//GEN-END:variables
}
