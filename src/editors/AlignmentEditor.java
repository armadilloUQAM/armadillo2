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

import biologic.Alignment;
import biologic.seqclasses.ModelTestJDialog;
import biologic.MultipleSequences;
import biologic.Sequence;
import configuration.Config;
import configuration.ImageFilter;

import configuration.SequenceFilter;
import database.databaseFunction;
import editor.AlignmentViewer;
import editor.EditorInterface;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.net.URI;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import workflows.armadillo_workflow;
import workflows.workflow_properties;

/**
 *
 * @author Lorde
 */
public class AlignmentEditor extends javax.swing.JFrame implements EditorInterface {

    Frame frame;
    workflow_properties properties;
    armadillo_workflow parent_workflow;
    AlignmentViewer align=new AlignmentViewer();
    databaseFunction df=new databaseFunction();
    Config config=new Config();

    /** Creates new form SimplePhyloJFrame */
    public AlignmentEditor(java.awt.Frame parent, armadillo_workflow parent_workflow) {
       
        this.parent_workflow=parent_workflow;
        frame=parent;
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
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        jSlider1 = new javax.swing.JSlider();
        scrollPane1 = new java.awt.ScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        TypejComboBox2 = new javax.swing.JComboBox();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jPanel3PropertyChange(evt);
            }
        });

        jSlider1.setValue(0);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        scrollPane1.add(align);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jButton2.setText("Save Image");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Save Fasta/Phylip");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Display Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(255, 153, 0))); // NOI18N

        jLabel1.setText("View Mode");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Small", "Sequence" }));
        jComboBox1.setSelectedIndex(2);
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Type");

        TypejComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nucleic", "Protein" }));
        TypejComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TypejComboBox2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addContainerGap(78, Short.MAX_VALUE))
            .addComponent(TypejComboBox2, 0, 129, Short.MAX_VALUE)
            .addComponent(jComboBox1, 0, 129, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TypejComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(105, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)
                    .addComponent(scrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(scrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                        .addGap(1, 1, 1)
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
      
    }//GEN-LAST:event_jSlider1StateChanged

    private void jPanel3PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jPanel3PropertyChange
        if (align!=null) {
            align.force_redraw=true;
            align.redraw();
        }
    }//GEN-LAST:event_jPanel3PropertyChange

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
         JFileChooser jf=new JFileChooser(config.getExplorerPath());
       jf.setFileFilter(new SequenceFilter());
       jf.setName("Saving as Fasta or Phylip...");
        int result=jf.showSaveDialog(this);
           if (result==JFileChooser.APPROVE_OPTION) {
               String filename=jf.getSelectedFile().getAbsolutePath();
               config.setExplorerPath(jf.getSelectedFile().getPath());
               String upfilename=filename.toUpperCase();
               int Alignment_id=properties.getInt("output_alignment_id");
               int MultipleSequences_id=properties.getInt("output_multiplesequences_id");
               int Sequence_id=properties.getInt("output_sequence_id");
               boolean phylip=false;
               if (upfilename.endsWith("PHY")||upfilename.endsWith("PHYLIP")) phylip=true;
               Alignment align=new Alignment();
               MultipleSequences multi=new MultipleSequences();
              if (Alignment_id>0) {
                  align=new Alignment(Alignment_id);
                  if (phylip) {
                      align.outputPhylip(filename);
                  } else align.outputFasta(filename);
              } else if (MultipleSequences_id>0) {
                    multi=new MultipleSequences(MultipleSequences_id);
                   if (phylip) {
                      multi.outputPhylip(filename);
                  } else multi.outputFasta(filename);
              } else if (Sequence_id>0) {
                   Sequence seq=new Sequence(Sequence_id);
                   multi.add(seq);
                   if (phylip) {
                      multi.outputPhylip(filename);
                  } else multi.outputFasta(filename);
               }
            } //--End file selected
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
       
        switch (this.jComboBox1.getSelectedIndex()) {
            case 0: align.setViewDetails(AlignmentViewer.VIEW_NORMAL);
                    break;
            case 1: align.setViewDetails(AlignmentViewer.VIEW_SMALL);
                    break;
            case 2: align.setViewDetails(AlignmentViewer.VIEW_BIG);
                    break;
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void TypejComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TypejComboBox2ActionPerformed
       switch (this.TypejComboBox2.getSelectedIndex()) {
           case 0: align.setProtein(false);
                   break;
           case 1: align.setProtein(true);
                    break;
       }
    }//GEN-LAST:event_TypejComboBox2ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        JFileChooser jf=new JFileChooser(config.getExplorerPath());
        jf.addChoosableFileFilter(new ImageFilter());
        jf.setAcceptAllFileFilterUsed(false);
        jf.setName("Saving Image to ...");
        int result=jf.showSaveDialog(this);
           if (result==JFileChooser.APPROVE_OPTION) {
               String filename=jf.getSelectedFile().getAbsolutePath();
               String path=jf.getSelectedFile().getPath();
               config.setExplorerPath(path);
               config.Save();
               if (filename.toLowerCase().endsWith("pdf")) {
                   System.out.println("Saving to pdf in alignment view is not implemented...");
                   saveImage_toFile(filename+".png");
                  //savePDF(filename);  
               } else 
                saveImage_toFile(filename);
            }
    }//GEN-LAST:event_jButton2ActionPerformed

  

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox TypejComboBox2;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSlider jSlider1;
    private java.awt.ScrollPane scrollPane1;
    // End of variables declaration//GEN-END:variables

    public void display(workflow_properties properties) {
        try {
        this.properties=properties;
        initComponents();
        if (frame==null) frame=this;

         int Alignment_id=properties.getInt("output_alignment_id");
         int MultipleSequences_id=properties.getInt("output_multiplesequences_id");
         int Sequence_id=properties.getInt("output_sequence_id");

         int w=frame.getSize().width;
         
         if (Alignment_id>0) {
              Alignment al=df.getAlignment(Alignment_id);             
              setTitle("Alignment [ID "+Alignment_id+" ] "+al.getName());
              //this.ModelTest_jMenuItem.setEnabled(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE));
              align.init(al, w);
              //while(!align.isInitialized()){}
              this.jSlider1.setMaximum(al.getSequenceSize());
            
         } else if (MultipleSequences_id>0) {
              MultipleSequences al=new MultipleSequences(MultipleSequences_id);
              setTitle("MultipleSequences [ID "+MultipleSequences_id+" ] "+al.getName());
              //this.ModelTest_jMenuItem.setEnabled(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE));
              align.init(al, w);
              //while(!align.isInitialized()){}
              this.jSlider1.setMaximum(al.getSequenceSize());

         } else if (Sequence_id>0) {
             MultipleSequences al=new MultipleSequences();
             Sequence seq=new Sequence(Sequence_id);
             al.setName(seq.getName());
             setTitle("Sequence [ID "+Sequence_id+" ] "+al.getName());
             al.add(seq);
             align.init(al,w);
             //this.ModelTest_jMenuItem.setEnabled(false);
              //while(!align.isInitialized()){}
              
              this.jSlider1.setMaximum(al.getSequenceSize());
         }
         align.validate();
         if (align.isProtein()) {
             this.TypejComboBox2.setSelectedIndex(1);
         }
         this.jSlider1.addChangeListener(align);
         Dimension d=new Dimension();
         d.setSize(w, 400);
         setSize(d);
         setPreferredSize(d);

         this.setIconImage(Config.image);
         this.setAlwaysOnTop(true);
         this.setVisible(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAlignment(final Alignment align) {
    java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    AlignmentEditor a=new AlignmentEditor(null, null);
                    a.setDefaultCloseOperation(AlignmentEditor.HIDE_ON_CLOSE);
                    a.display(align.returnProperties());
                    //a.ModelTest_jMenuItem.setEnabled(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE));
                }
            });
    }

    public static void showSequencet(final Sequence sequence) {
    java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    AlignmentEditor a=new AlignmentEditor(null, null);
                    a.setDefaultCloseOperation(AlignmentEditor.HIDE_ON_CLOSE);
                    a.display(sequence.returnProperties());
                    //a.ModelTest_jMenuItem.setEnabled(false);
                }

            });
    }

    public static void showAlignment(final MultipleSequences multi) {
    java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    AlignmentEditor a=new AlignmentEditor(null, null);
                    a.setDefaultCloseOperation(AlignmentEditor.HIDE_ON_CLOSE);
                    a.display(multi.returnProperties());
                    //a.ModelTest_jMenuItem.setEnabled(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE));
                }
            });
    }
    
    
    public void saveImage_toFile(String filename) {
        align.saveImage(filename);        
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