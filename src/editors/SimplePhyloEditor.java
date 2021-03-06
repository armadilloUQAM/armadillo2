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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SimplePhyloJFrame.java
 *
 * Created on 2009-08-15, 12:42:11
 */

package editors;

import biologic.Tree;
import configuration.Config;
import configuration.ImageFilter;
import configuration.TreeFilter;
import configuration.Util;
import database.databaseFunction;
import editor.EditorInterface;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import org.phylowidget.PhyloWidget;
import workflows.armadillo_workflow;
import workflows.workflow_properties;

/**
 *
 * @author Lorde
 */
public class SimplePhyloEditor extends javax.swing.JFrame implements EditorInterface {

    Frame frame;
    workflow_properties properties;
    armadillo_workflow parent_workflow;
    PhyloWidget phylo1=new PhyloWidget();
    databaseFunction df=new databaseFunction();
    Config config=new Config();
    static boolean initialized=false;
    Tree tree=null;

    /** Creates new form SimplePhyloJFrame */
    public SimplePhyloEditor(java.awt.Frame parent, armadillo_workflow parent_workflow) {
       
        this.parent_workflow=parent_workflow;
        frame=parent;
        phylo1.setSize(800, 600);
        phylo1.init();
         initComponents();        
         phylo1.setSize(this.jPanel1.getSize());
         phylo1.invalidate();
         phylo1.validate();
          if (!initialized) {
             initialized=true;
         }
         this.setIconImage(Config.image);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        Save_jMenuItem = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem3 = new javax.swing.JRadioButtonMenuItem();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.add(phylo1);
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 600));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 798, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        jMenu1.setText("File");

        Save_jMenuItem.setText("Save Newick tree");
        Save_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Save_jMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(Save_jMenuItem);

        jMenuItem3.setText("Close");
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Display");

        buttonGroup1.add(jRadioButtonMenuItem1);
        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("Full name");
        jRadioButtonMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jRadioButtonMenuItem1);

        buttonGroup1.add(jRadioButtonMenuItem2);
        jRadioButtonMenuItem2.setText("Abbreviate");
        jRadioButtonMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jRadioButtonMenuItem2);

        buttonGroup1.add(jRadioButtonMenuItem3);
        jRadioButtonMenuItem3.setText("Id");
        jRadioButtonMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jRadioButtonMenuItem3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Save_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Save_jMenuItemActionPerformed
        JFileChooser jf=new JFileChooser(config.getExplorerPath());
        jf.setFileFilter(new TreeFilter());
        jf.setName("Saving as Newick...");
        int result=jf.showSaveDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            String filename=jf.getSelectedFile().getAbsolutePath();
            config.setExplorerPath(jf.getSelectedFile().getPath());
            if (this.jRadioButtonMenuItem1.isSelected()) tree.outputNewick(filename);
            if (this.jRadioButtonMenuItem2.isSelected()) tree.outputNewickAbbreviate(filename);
            if (this.jRadioButtonMenuItem3.isSelected()) tree.outputNewickWithSequenceID(filename);
        } //--End file selected
}//GEN-LAST:event_Save_jMenuItemActionPerformed

    private void jRadioButtonMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ActionPerformed
         if (initialized) phylo1.updateTree(tree.getTree());
    }//GEN-LAST:event_jRadioButtonMenuItem1ActionPerformed

    private void jRadioButtonMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem2ActionPerformed
        if (initialized) phylo1.updateTree(tree.getTreeAbbreviate());
    }//GEN-LAST:event_jRadioButtonMenuItem2ActionPerformed

    private void jRadioButtonMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem3ActionPerformed
       if (initialized) phylo1.updateTree(tree.getTreeSequenceID());
    }//GEN-LAST:event_jRadioButtonMenuItem3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Save_jMenuItem;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem3;
    // End of variables declaration//GEN-END:variables

    public void display(workflow_properties properties) {
         
         this.properties=properties;
         if (properties.isSet("output_tree_id")) {
             int tree_id=properties.getInt("output_tree_id");
             tree= df.getTree(tree_id);             
             //while(!phylo1.isEnabled()) {} //--Wait for initialization
             phylo1.validate();
             phylo1.updateTree(tree.getTree());
             this.setTitle("PhyloWidget (Copyright © 2008 Gregory Jordan) - "+tree.getName());
               // Set position 
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension d = getSize();
            setLocation((screenSize.width-d.width)/2,
					(screenSize.height-d.height)/2);
             this.setVisible(true);
         }
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

}
