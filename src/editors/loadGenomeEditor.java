/*
 *  Armadillo Workflow Platform v2.0
 *  A simple pipeline system for phylogenetic analysis
 *  
 *  Copyright (C) 2009-2014  Etienne Lord, Mickael Leclercq
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


import biologic.seqclasses.InformationJDialog;
import biologic.*;
import configuration.BlastDBFilter;
import javax.swing.SwingWorker;
import workflows.workflow_properties;
import configuration.Config;
import configuration.Util;
import database.databaseFunction;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import editor.EditorInterface;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import tools.Toolbox;
import workflows.armadillo_workflow;
import workflows.workflow_properties_dictionnary;

/**
 * Editor of the object properties in the Main Workflow
 * Note: Only called if object doesnt have a Custum Editor
 * @author Etienne Lord
 * @since July 2009
 */
public class loadGenomeEditor extends javax.swing.JDialog implements EditorInterface {

    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES

    Config config=new Config();
    //ConnectorInfoBox connectorinfobox;
    workflow_properties_dictionnary dict=new workflow_properties_dictionnary();
    String selected="";             // Selected properties
    Frame frame;
    workflow_properties properties;
    armadillo_workflow parent_workflow;
    SwingWorker<Integer, InfoSequence>  loadSwingWorker; //SwingWorker to import sequence
    SwingWorker<Integer, Object>  loadSwingWorker2;     //SwingWorker to import sequence
    InformationJDialog loading;                        //Loading sequence JDialog
    Vector<InfoSequence>MultipleInfoSequence=new Vector<InfoSequence>();
    databaseFunction df=new databaseFunction();
    String old_filename="";
    String old_filename2="";
    ////////////////////////////////////////////////////////////////////////////
    /// CONSTANT

    public final String defaultNameString="Name";
    public boolean changed=false;
    
    /** Creates new form propertiesJDialog */
    public loadGenomeEditor(java.awt.Frame parent, armadillo_workflow parent_workflow) {
        super(parent, false);
        this.parent_workflow=parent_workflow;
        //--Set variables and init
        frame=parent;
        //connectorinfobox=new ConnectorInfoBox(parent); //--Used to display Connector info
        //--Initialize component
       
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
        jProgressBar1 = new javax.swing.JProgressBar();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        Filename_jTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        Filename_jTextField2 = new javax.swing.JTextField();
        jButton_2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        NamejTextField = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        type_jComboBox1 = new javax.swing.JComboBox();
        OkjButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        CanceljButton = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        jLabel2.setText("jLabel2");

        setTitle("Properties");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTabbedPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jTabbedPane1ComponentShown(evt);
            }
        });

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        Filename_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                Filename_jTextFieldFocusLost(evt);
            }
        });

        jLabel3.setText("Directory or files");

        jButton6.setText("...");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        Filename_jTextField2.setEditable(false);
        Filename_jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                Filename_jTextField2FocusLost(evt);
            }
        });

        jButton_2.setText("...");
        jButton_2.setEnabled(false);
        jButton_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_2ActionPerformed(evt);
            }
        });

        jLabel4.setText("File");

        jLabel7.setText("paired-file");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(Filename_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(Filename_jTextField2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Filename_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Filename_jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_2)
                    .addComponent(jLabel7)))
        );

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
                .addComponent(NamejTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(NamejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton4))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel5.setText("Type");

        type_jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Fastq", "Fastq (paired)", "Solid", "Reference genome" }));
        type_jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(type_jComboBox1, 0, 468, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(type_jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel5))
        );

        OkjButton.setText("<html><b>Ok</b></hmll>");
        OkjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkjButtonActionPerformed(evt);
            }
        });

        jLabel6.setForeground(new java.awt.Color(255, 0, 0));
        jLabel6.setText("<html>Note: This will be converted to a \"Repeat\" workflow in multiples files are selected.</html>");

        CanceljButton.setText("Cancel");
        CanceljButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CanceljButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(CanceljButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(OkjButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(CanceljButton)
                        .addComponent(OkjButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Genome", jPanel9);

        jButton3.setText("?");
        jButton3.setToolTipText("Help / Informations");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton3)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTabbedPane1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentShown
      
}//GEN-LAST:event_jTabbedPane1ComponentShown

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        JFileChooser jf;
        if (this.Filename_jTextField.getText().isEmpty()) {
            jf=new JFileChooser(config.getExplorerPath());
        } else {
            jf=new JFileChooser(this.Filename_jTextField.getText());
        }
        jf.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jf.setAcceptAllFileFilterUsed(false);
        jf.setMultiSelectionEnabled(true);
        int result=jf.showOpenDialog(this);
        //CAS 1: On reussi a choisir un fichier
        if (result==JFileChooser.APPROVE_OPTION) {  
            changed=true;
              //--Remove previous files in object
              Vector<String>keys=new Vector<String>();
              for (Object k:properties.keySet()) keys.add((String)k);
               for(String key:keys) {
                   if (key.startsWith("For_")) properties.remove(key);
               }        
               properties.remove("ForObjectID");

              //--Save new filepath
              File[] files=jf.getSelectedFiles();
             if (files.length==1&&!files[0].isDirectory()) {
                  this.Filename_jTextField.setText(files[0].getAbsolutePath());
                  properties.put("Description", files[0].getAbsolutePath());
                  properties.put("inputname", files[0].getAbsolutePath());
              } else {
                  this.Filename_jTextField.setText("");
              }
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    public Vector<File> listDir(File f) {
        Vector<File> toReturn=new Vector<File>();
        for (File f2:f.listFiles()) {
            if (f2.isDirectory()) {
                toReturn.addAll(listDir(f2));
            } else {
                toReturn.add(f2);
            }
        }
        return toReturn;
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        HelpEditor help = new HelpEditor(this.frame, false, properties);
        help.setVisible(true);
}//GEN-LAST:event_jButton3ActionPerformed

    private void OkjButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkjButtonActionPerformed
        //properties.put("inputname", this.Filename_jTextField.getText());
         if (changed) saveGenome();
        this.setVisible(false);
    }//GEN-LAST:event_OkjButtonActionPerformed

    private void CanceljButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CanceljButtonActionPerformed
        properties.put("inputname", this.old_filename);
        properties.put("inputname2", this.old_filename2);
        this.setVisible(false);
    }//GEN-LAST:event_CanceljButtonActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        properties.put("Name", this.NamejTextField.getText());
        changed=true;        
        parent_workflow.updateCurrentWorkflow(properties);
}//GEN-LAST:event_jButton4ActionPerformed

    private void type_jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_jComboBox1ActionPerformed
        // 0. Fastq
        // 1. Fastq (pair)
        // 2. Solid
        // 3. Reference
        //. TO DO PUT NAME HERE
        properties.put("type", this.type_jComboBox1.getSelectedIndex());
        changed=true;
        if (this.type_jComboBox1.getSelectedIndex()==1) {
            Filename_jTextField2.setEditable(true);
            this.jButton_2.setEnabled(true);
        } else {
            Filename_jTextField2.setEditable(false);
            this.jButton_2.setEnabled(false);
        }
        properties.put("Description", this.Filename_jTextField.getText());
}//GEN-LAST:event_type_jComboBox1ActionPerformed

    private void Filename_jTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_Filename_jTextFieldFocusLost
        //this.Filename_jTextField.setText(properties.get("inputname"));        
        properties.put("inputname", this.Filename_jTextField.getText());
        properties.put("Description", this.Filename_jTextField.getText());  
        changed=true;
    }//GEN-LAST:event_Filename_jTextFieldFocusLost

    private void Filename_jTextField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_Filename_jTextField2FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_Filename_jTextField2FocusLost

    private void jButton_2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_2ActionPerformed
       JFileChooser jf;
        if (this.Filename_jTextField.getText().isEmpty()) {
            jf=new JFileChooser(config.getExplorerPath());
        } else {
            jf=new JFileChooser(this.Filename_jTextField2.getText());
        }
        jf.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jf.setAcceptAllFileFilterUsed(false);
        jf.setMultiSelectionEnabled(false);
        int result=jf.showOpenDialog(this);
        //CAS 1: On reussi a choisir un fichier
        if (result==JFileChooser.APPROVE_OPTION) {  
                changed=true;             
              Vector<String>keys=new Vector<String>();
              for (Object k:properties.keySet()) keys.add((String)k);
               for(String key:keys) {
                   if (key.startsWith("For_")) properties.remove(key);
               }        
               properties.remove("ForObjectID");
              //--Save new filepath
              File[] files=jf.getSelectedFiles();
               if (files.length==1&&!files[0].isDirectory()) {
                  this.Filename_jTextField2.setText(files[0].getAbsolutePath());                 
                  properties.put("inputname2", files[0].getAbsolutePath());
              }                else {
                  this.Filename_jTextField2.setText("");
              }
        }
    }//GEN-LAST:event_jButton_2ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
      
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
      if (changed) saveGenome();
    }//GEN-LAST:event_formWindowClosing

    public void saveGenome() {
         //--Save
       // debug  System.out.println("Saving genome to database..");
        if (properties.getOutputID("genome")==0) {            
            Genome genome=new Genome();
            //--Serialize the genome info 
            genome.setText(properties.serializeToString());
            genome.setName(properties.getDescription());
            genome.setGenomeFile(properties.get("inputname"));
            genome.setNote("Created on "+Util.returnCurrentDateAndTime());
            genome.saveToDatabase();
            properties.put("output_genome_id",genome.getId());
             Toolbox tool=new Toolbox();
            tool.reloadDatabaseTree();
        } else {
            Genome genome=new Genome(properties.getOutputID("genome"));
            genome.setText(properties.serializeToString());
            genome.setName(properties.getDescription());
            genome.setGenomeFile(properties.get("inputname"));
            genome.setNote(genome.getNote()+"\nUpdated on "+Util.returnCurrentDateAndTime());
            genome.updateDatabase();
        }
       
       //this.parent_workflow.
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
        setTitle(properties.getName());
        this.NamejTextField.setText(properties.getName());
        changed=false; //--For the serialization if needed
        //--Load properties
        //this.Message("Select some sequences files or directories...", "");
         //if (properties.isSet("Description")) this.Notice.setText(properties.get("Description"));
        
     
        
        
        if (properties.isSet("inputname")) {
           this.Filename_jTextField.setText(properties.get("inputname"));
           old_filename=properties.get("inputname");
        }
        if (properties.isSet("inputname2")) {
           this.Filename_jTextField2.setText(properties.get("inputname2"));
           old_filename2=properties.get("inputname2");
        }
        
         if (properties.isSet("type")) {
            this.type_jComboBox1.setSelectedIndex(properties.getInt("type"));
        }

        // Set position 
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = getSize();
        setLocation((screenSize.width-d.width)/2,
					(screenSize.height-d.height)/2);
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
    private javax.swing.JButton CanceljButton;
    private javax.swing.JTextField Filename_jTextField;
    private javax.swing.JTextField Filename_jTextField2;
    private javax.swing.JTextField NamejTextField;
    private javax.swing.JButton OkjButton;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton_2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox type_jComboBox1;
    // End of variables declaration//GEN-END:variables



}