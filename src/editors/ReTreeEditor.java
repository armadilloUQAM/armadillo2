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

package editors;


import editor.DatabaseSQLite3_cellRenderer;
import workflows.workflow_properties;
import configuration.Config;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.File;
import editor.EditorInterface;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import program.programs;
import workflows.armadillo_workflow;
import workflows.workflow_properties_dictionnary;

/**
 * Editor of the object properties in the Main Workflow
 * Note: Only called if object doesnt have a Custum Editor
 * @author Etienne Lord
 * @since July 2009
 */
public class ReTreeEditor extends javax.swing.JDialog implements EditorInterface {

    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES

    Config config=new Config();
    //ConnectorInfoBox connectorinfobox;
    workflow_properties_dictionnary dict=new workflow_properties_dictionnary();
    String selected="";             // Selected properties
    Frame frame;
    workflow_properties properties;
    armadillo_workflow parent_workflow;

    ///////////////////////////////////////////////////////////////////////////
    //// Default Values
    static final String default_dnadist_gamma="No";
    static final boolean default_dnadist_gamma_varCoef=false;
    static final double default_dnadist_trans_transv_ratio=2;
    static final boolean default_dnadist_gamma_coefficient=false;
    static final double default_dnadist_gamma_coefficient_value=4;
    static final String default_dnadist_empirical_bases_choice="Yes";
    static final double default_dnadist_empirical_baseA=0.25;
    static final double default_dnadist_empirical_baseC=0.25;
    static final double default_dnadist_empirical_baseG=0.25;
    static final double default_dnadist_empirical_baseT=0.25;
    static final String default_Neighbor_Choice="Neighbor-joining";
   
    ////////////////////////////////////////////////////////////////////////////
    /// CONSTANT

    public final String defaultNameString=" Name";

    /** Creates new form propertiesJDialog */
    public ReTreeEditor(java.awt.Frame parent, armadillo_workflow parent_workflow) {
        super(parent, false);
        this.parent_workflow=parent_workflow;
        //--Set variables and init
        frame=parent;
        //connectorinfobox=new ConnectorInfoBox(parent); //--Used to display Connector info
        //--Initialize component
       
    }

    public void setDefaultValues() {
      
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        NamejTextField = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        OutgroupjComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        MethodljComboBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        CustomPhylipjTextArea = new javax.swing.JTextArea();
        ClosejButton = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();

        jLabel2.setText("jLabel2");

        setTitle("Properties");
        setResizable(false);

        jTabbedPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jTabbedPane1ComponentShown(evt);
            }
        });

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setText("Name");

        jButton4.setText("Rename");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NamejTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(NamejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton4))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Outgroup", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 153, 51))); // NOI18N

        OutgroupjComboBox.setEditable(true);
        OutgroupjComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30" }));
        OutgroupjComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OutgroupjComboBoxActionPerformed(evt);
            }
        });
        OutgroupjComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                OutgroupjComboBoxFocusLost(evt);
            }
        });

        jLabel3.setText("Outgroup sequence number");

        MethodljComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Outgroup", "Midpoint" }));
        MethodljComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MethodljComboBoxActionPerformed(evt);
            }
        });
        MethodljComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                MethodljComboBoxFocusLost(evt);
            }
        });

        jLabel5.setText("Rooting method");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(77, 77, 77)))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(MethodljComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(OutgroupjComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 118, Short.MAX_VALUE)))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(MethodljComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(OutgroupjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jCheckBox1.setText("Rooted Tree");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setText("Additionnal custom Phylip command");

        CustomPhylipjTextArea.setColumns(20);
        CustomPhylipjTextArea.setRows(5);
        CustomPhylipjTextArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CustomPhylipjTextAreaFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(CustomPhylipjTextArea);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addContainerGap(237, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1))
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jCheckBox1)
                .addGap(4, 4, 4)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        ClosejButton.setText("<html><b>Close</b></html>");
        ClosejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClosejButtonActionPerformed(evt);
            }
        });

        jButton5.setText("Run");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton8.setText("Stop");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton6.setText("Reset default values");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ClosejButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ClosejButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5)
                    .addComponent(jButton8)
                    .addComponent(jButton6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("ReTree (Phylip)", jPanel9);

        jButton7.setText("?");
        jButton7.setToolTipText("Help / Informations");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(494, Short.MAX_VALUE)
                .addComponent(jButton7)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ClosejButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClosejButtonActionPerformed
        //-- 1. Did we change the properties? If, yes ask for direction        
        this.setVisible(false);
}//GEN-LAST:event_ClosejButtonActionPerformed

    private void jTabbedPane1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentShown
      
}//GEN-LAST:event_jTabbedPane1ComponentShown

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       properties.put("Name", this.NamejTextField.getText());
       parent_workflow.updateCurrentWorkflow(properties);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        properties.load();             //--reload current properties from file
        this.setProperties(properties);//--Update current field
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
       HelpEditor help = new HelpEditor(this.frame, false, properties);
        help.setVisible(true);
}//GEN-LAST:event_jButton7ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
         if(properties.isSet("ClassName")) {
           this.parent_workflow.workflow.updateDependance();
           programs prog=new programs(parent_workflow.workbox.getCurrentWorkflows());
           prog.Run(properties);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        properties.put("Status", Config.status_nothing);
        properties.killThread();
}//GEN-LAST:event_jButton8ActionPerformed

    private void OutgroupjComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OutgroupjComboBoxActionPerformed
       properties.put("outgroup", (String)this.OutgroupjComboBox.getSelectedItem());
    }//GEN-LAST:event_OutgroupjComboBoxActionPerformed

    private void OutgroupjComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_OutgroupjComboBoxFocusLost
      properties.put("outgroup", (String)this.OutgroupjComboBox.getSelectedItem());
    }//GEN-LAST:event_OutgroupjComboBoxFocusLost

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
       properties.put("rooted", this.jCheckBox1.isSelected());
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void MethodljComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MethodljComboBoxActionPerformed
        properties.put("method", (String)this.MethodljComboBox.getSelectedItem());
    }//GEN-LAST:event_MethodljComboBoxActionPerformed

    private void MethodljComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_MethodljComboBoxFocusLost
       properties.put("method", (String)this.MethodljComboBox.getSelectedItem());
    }//GEN-LAST:event_MethodljComboBoxFocusLost

    private void CustomPhylipjTextAreaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CustomPhylipjTextAreaFocusLost
        properties.put("CustomPhylipCommand", this.CustomPhylipjTextArea.getText());
}//GEN-LAST:event_CustomPhylipjTextAreaFocusLost

   
    /**
     * This set the Properties
     * @param properties
     */
    public void setProperties(workflow_properties properties) {
        this.properties=properties;
        setTitle(properties.getName());
         this.NamejTextField.setText(properties.getName());
       //if (properties.isSet("Description")) this.Notice.setText(properties.get("Description"));
        this.NamejTextField.setText(properties.getName());
        if (properties.isSet("method")) {
            this.MethodljComboBox.setSelectedItem(properties.get("method"));
        }        
         if (properties.isSet("outgroup")) {
            this.OutgroupjComboBox.setSelectedItem(properties.get("outgroup"));
        }
        this.jCheckBox1.setSelected(properties.getBoolean("rooted"));
    }

      

    /**
     * This set the different setting corresponding to the current properties
     */
    public void setSettingForProperties() {
           this.NamejTextField.setText(properties.getName());
     }

    ///////////////////////////////////////////////////////////////////////////
    /// DISPLAY MAIN FUNCTION

    public void display(workflow_properties properties) {
        this.properties=properties;
        initComponents();
        setIconImage(Config.image);

        // Set position 
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = getSize();
        setLocation((screenSize.width-d.width)/2,
					(screenSize.height-d.height)/2);
        this.setProperties(properties);
         this.setAlwaysOnTop(true);
        this.setVisible(true);
    }
   
    public void saveImage(String filename) {
        BufferedImage bi;
        try {
            bi = new Robot().createScreenCapture(this.getBounds()); 
            ImageIO.write(bi, "png", new File(filename));
            this.setVisible(false);
        } catch (Exception ex) {
           Config.log("Unable to save "+filename+" dialog image");
        }            
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ClosejButton;
    private javax.swing.JTextArea CustomPhylipjTextArea;
    private javax.swing.JComboBox MethodljComboBox;
    private javax.swing.JTextField NamejTextField;
    private javax.swing.JComboBox OutgroupjComboBox;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables



}
