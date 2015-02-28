/*
 *  Armadillo Workflow Platform v1.0
 *  A simple pipeline system for phylogenetic analysis
 *  
 *  Copyright (C) 2009-2011  Etienne Lord, Mickael Leclercq
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package editor;


import editor.*;
import configuration.Config;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import workflows.workflow_properties;
import workflows.workflow_properties;

/**
 * RenameComboBox for propertiesName
 * Note: return the propertiesName, it will be empty if cancelled
 *
 * @author Etienne Lord
 * @since July 2009
 */
public class RenamePropertiesJDialog extends javax.swing.JDialog {

    ////////////////////////////////////////////////////////////////////////////
    /// Variables

    private String oldPropertiesName="";    //Old properties filename
    public String propertiesName="";        //New properties filename
    Config config=new Config();             //Configuration file
    Frame frame;                            //parent frame

    ////////////////////////////////////////////////////////////////////////////
    /// Constructor
   
    public RenamePropertiesJDialog(java.awt.Frame parent, String oldPropertiesName, String RenameButtonText) {
        super(parent, true);
        this.oldPropertiesName=oldPropertiesName;
        this.setAlwaysOnTop(true);
        this.frame=parent;
        initComponents();
        this.RenamejButton.setText(RenameButtonText);
        this.setTitle(RenameButtonText);
        this.properties_jTextField.setText(oldPropertiesName);
        Message("Note: The filename must end with '.properties' .","");
        // Set position
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = getSize();
        setLocation((screenSize.width-d.width)/2,
					(screenSize.height-d.height)/2);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        properties_jTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        CanceljButton = new javax.swing.JButton();
        RenamejButton = new javax.swing.JButton();
        jStatusMessage = new javax.swing.JLabel();

        setTitle("Rename properties");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        properties_jTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                properties_jTextFieldKeyPressed(evt);
            }
        });

        jLabel1.setText("New properties filename (Will be in the propertiesPath directory)");

        CanceljButton.setText("Cancel");
        CanceljButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CanceljButtonActionPerformed(evt);
            }
        });

        RenamejButton.setText("Create");
        RenamejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RenamejButtonActionPerformed(evt);
            }
        });

        jStatusMessage.setForeground(new java.awt.Color(51, 51, 255));
        jStatusMessage.setText("Info");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jStatusMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RenamejButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CanceljButton))
                    .addComponent(properties_jTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(321, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(properties_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CanceljButton)
                    .addComponent(RenamejButton)
                    .addComponent(jStatusMessage)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CanceljButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CanceljButtonActionPerformed
        propertiesName="";
        this.setVisible(false);
    }//GEN-LAST:event_CanceljButtonActionPerformed

    private void RenamejButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RenamejButtonActionPerformed
        renameProperties();
}//GEN-LAST:event_RenamejButtonActionPerformed

    private void properties_jTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_properties_jTextFieldKeyPressed
        char c=evt.getKeyChar(); //Hack to catch V_ENTER key;
       if (c==KeyEvent.VK_ENTER) {
            renameProperties();
        }
    }//GEN-LAST:event_properties_jTextFieldKeyPressed

    ////////////////////////////////////////////////////////////////////////////
    /// Rename

    private void renameProperties() {
        propertiesName=properties_jTextField.getText();
        //--Note: we exit only if everything is fine
        //CASE 1. Test if ist the old name?
        if (propertiesName.equals(oldPropertiesName)) {
            propertiesName=oldPropertiesName;
            MessageErreur("Warning, you must have a name different than "+propertiesName,"");
            return;
        }
        // CASE 2. Test if endsWith '.properties'
        if (!propertiesName.endsWith(".properties")) {
            MessageErreur("Warning, properties name must end with '.properties'","");
            return;
        }
        // CASE 3. Test if already in directory
        for (String filename:workflow_properties.loadPropertieslisting(config.get("propertiesPath"))) {
            if (filename.equalsIgnoreCase(propertiesName)) {
                JOptionPane.showMessageDialog(frame, "Warning! Properties already exists, rename!", "Warning", JOptionPane.WARNING_MESSAGE);
                MessageErreur("Warning, properties name already exists! ","");
                return;
            }
        }


        //--End dialog
         this.setVisible(false);
    }

    ///////////////////////////////////////////////////////////////////////////
    /// MESSAGE FONCTION

    /**
     * Affiche un message dans la status bar
     * La provenance peut être mise dans un tooltip
     * @param text Le texte
     * @param tooltip Le tooltip texte
     */
    public void Message(String text, String tooltip) {
        this.jStatusMessage.setEnabled(true);
        this.jStatusMessage.setForeground(new java.awt.Color(0, 51, 153));
        this.jStatusMessage.setBackground(Color.WHITE);
        this.jStatusMessage.setToolTipText(tooltip);
        this.jStatusMessage.setText(text);
    }

    /**
     * Affiche un message d'erreur en rouge dans la status bar
     * La provenance peut être mise dans un tooltip
     * @param text Le texte
     * @param tooltip Le tooltip texte
     */
    public void MessageErreur(String text, String tooltip) {
        this.jStatusMessage.setEnabled(true);
        this.jStatusMessage.setForeground(Color.RED);
        this.jStatusMessage.setBackground(Color.WHITE);
        this.jStatusMessage.setToolTipText(tooltip);
        this.jStatusMessage.setText(text);
    }

   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CanceljButton;
    private javax.swing.JButton RenamejButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jStatusMessage;
    private javax.swing.JTextField properties_jTextField;
    // End of variables declaration//GEN-END:variables

}