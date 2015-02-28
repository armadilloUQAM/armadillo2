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


import biologic.Biologic;
import biologic.Output;
import biologic.seqclasses.SimpleInputJDialog;
import configuration.Config;
import configuration.excelAdapterTable;
import database.databaseFunction;
import editor.EditorInterface;
import editor.ForMutableTreeNode;
import editor.ForTableModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import workflows.workflow_properties;
import workflows.armadillo_workflow;
import workflows.workflow_properties_dictionnary;

public class UndefinedEditorBiologic extends javax.swing.JDialog implements EditorInterface {

    databaseFunction df=new databaseFunction();
    Frame frame;
    workflow_properties properties;
    armadillo_workflow parent_workflow;
    workflow_properties_dictionnary dic=new workflow_properties_dictionnary();
    Vector<ForMutableTreeNode>dataTree=new Vector<ForMutableTreeNode>();
    private boolean stateSelected=true;             //do we Select or Unselect

    ////////////////////////////////////////////////////////////////////////////
    // Constante
    // Search
    String lastSearch="";
    static final int MODE_ID=0;
    static final int MODE_ACCESSION=1;
    static final int MODE_DESC=2;
    static final int MODE_ALIASES=3;
    static final int MODE_ALL=4;
    static final int MODE_LENMORE=6;
    static final int MODE_LENLESS=7;

    ////////////////////////////////////////////////////////////////////////////
    /// SELECTED VARIABLE

    ForMutableTreeNode selected=null;
    
   /////////////////////////////////////////////////////////////////////////////
   /// Constructor

    public UndefinedEditorBiologic(java.awt.Frame parent, armadillo_workflow parent_workflow) {
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

        jComboBox2 = new javax.swing.JComboBox();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        VariablejTextField = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        Filter_ComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        ClosejButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jStatusMessage = new javax.swing.JLabel();
        SelectUnselectSequence_jButton = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Please select one or multiple elements...");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Variable selected", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 153, 0))); // NOI18N

        jButton4.setText("Rename");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(VariablejTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4)
                .addGap(17, 17, 17))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(VariablejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton4))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Selection"));

        jTable1.setModel(new ForTableModel());
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jScrollPane2.setViewportView(jTable1);

        Filter_ComboBox.setEditable(true);
        Filter_ComboBox.setModel(new javax.swing.DefaultComboBoxModel(Config.ClusteringOption));
        Filter_ComboBox.setToolTipText("Filter your results. Enter a search string");
        Filter_ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter_ComboBoxActionPerformed(evt);
            }
        });

        jLabel1.setText("Filter Value");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Filter_ComboBox, 0, 560, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(504, Short.MAX_VALUE))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Filter_ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        ClosejButton.setText("Close");
        ClosejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClosejButtonActionPerformed(evt);
            }
        });

        jButton1.setText("<html><b>Ok</b></html>");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jStatusMessage.setText("Info");

        SelectUnselectSequence_jButton.setText("Select / Unselect");
        SelectUnselectSequence_jButton.setToolTipText("<html>Select or Unselect the current results selection. <br>If nothing is selected, select or unselect all the results</html>");
        SelectUnselectSequence_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SelectUnselectSequence_jButtonActionPerformed(evt);
            }
        });

        jButton2.setText("Insert New Data");
        jButton2.setToolTipText("Directyl create a new datasets of the specific type.");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(SelectUnselectSequence_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2))
                    .addComponent(jStatusMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ClosejButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jStatusMessage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SelectUnselectSequence_jButton)
                    .addComponent(jButton2)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ClosejButton)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ClosejButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClosejButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_ClosejButtonActionPerformed

    private void Filter_ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Filter_ComboBoxActionPerformed
//        Update the displayed data in the table
        ForTableModel tm=(ForTableModel)this.jTable1.getModel();
        int mode=this.Filter_ComboBox.getSelectedIndex();
        int count=0; //--ResultCound
        String searchString=(String)this.Filter_ComboBox.getSelectedItem();
        if (mode==0) {
            //--Reset the table
            tm.data.clear();
            tm.setData(dataTree);
            Message("Found "+dataTree.size()+" element(s)","");
        } else {            
                //--Normal search
                Vector<Integer> resultIndex=search(searchString, this.MODE_ALL);
                count=resultIndex.size();
                tm.data.clear();
                for (Integer index:resultIndex) {
                    tm.addData(dataTree.get(index));
                }
                Message("Found "+resultIndex.size()+" element(s)","");
        }
        

        tm.fireTableDataChanged();
        this.jTable1.setModel(tm);
}//GEN-LAST:event_Filter_ComboBoxActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ForTableModel model=(ForTableModel)this.jTable1.getModel();
         String outputType=properties.get("outputType").toLowerCase();
         int count=0;

         for (ForMutableTreeNode node:model.data) {
            properties.remove("For_"+node.getId());
             if (node.isSelected()) {
                properties.put("ForObjectID", properties.getID());
                properties.put("For_"+node.getId(),"output_"+outputType+"_id");
                properties.put("output_"+outputType+"_id",node.getId());
                properties.put("Description", node.getValue());
                count++;
            }            
        }
         System.out.println(count);
       if (count==1) {
            int id=properties.getOutputID(outputType);          
            properties.put("output_"+outputType+"_id",id);
            properties.remove("For_"+id);
            properties.remove("ForObjectID");
        } else
            if (count==0) {
            properties.put("Description", "Undefined");
            properties.put("output_"+outputType+"_id",0);

        } else  if (count>1) {
            properties.put("Description", "For each");
            properties.put("output_"+outputType+"_id",0);
            //properties.put("ForObjectID", properties.getID());
        } 
        setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        properties.put("Name", this.VariablejTextField.getText());
        parent_workflow.updateCurrentWorkflow(properties);
}//GEN-LAST:event_jButton4ActionPerformed

    private void SelectUnselectSequence_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectUnselectSequence_jButtonActionPerformed
        ForTableModel tm=(ForTableModel)this.jTable1.getModel();
        //What do we do?
        int[] index=this.jTable1.getSelectedRows();
        //CASE 1. Select/Unselect all
        if (index.length==0) {
            for (ForMutableTreeNode s:tm.data) s.setSelected(stateSelected);
            stateSelected=!stateSelected;
        } else {
            //CASE 2. Inverse selection
            for (int i:index) {
                i=this.jTable1.convertRowIndexToModel(i);
                tm.data.get(i).setSelected(!tm.data.get(i).isSelected());
            }
        }
        tm.fireTableDataChanged();
        this.jTable1.setModel(tm);
}//GEN-LAST:event_SelectUnselectSequence_jButtonActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       SimpleInputJDialog simpleInput=new SimpleInputJDialog(frame, true, properties.get("outputType"));
       if (simpleInput.getBiologic()!=null) {
           int id=simpleInput.getBiologic().getId();
           updateUI();
           ForTableModel tm=(ForTableModel)this.jTable1.getModel();
            //What do we do?
            for (ForMutableTreeNode s:tm.data) {
                if (s.getId()==id) s.setSelected(true);
            }
       }
    }//GEN-LAST:event_jButton2ActionPerformed

    void updateUI() {
         //Pattern output_pattern=Pattern.compile("output_(.*)_id");
         ForTableModel model=(ForTableModel)this.jTable1.getModel();
         String outputType=properties.get("outputType");
         this.VariablejTextField.setText(properties.getName());
         //Config.log("UpdateUI");
                Output out=new Output();
                out.setType(outputType);
                out.setTypeid(0);
                //--Initialize the object
               Object bio=out.getBiologic();
               ((Biologic)bio).hasNext();
               Vector<Integer> ids=((Biologic)bio).getAllId();
               //-- DEbugConfig.log(ids+" "+((Biologic)bio).getBiologicType());
               for (int id:ids) {                   
                   ForMutableTreeNode n=new ForMutableTreeNode(((Biologic)bio).getBiologicType(),((Biologic)bio).getNameId(id), id);
                   if (properties.isSet("For_"+id)) n.setSelected(true);
                   dataTree.add(n);
               }
             model.setData(dataTree);
             model.fireTableDataChanged();
             model.fireTableStructureChanged();
            this.jTable1.setModel(model);
        
    }

 



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ClosejButton;
    private javax.swing.JComboBox Filter_ComboBox;
    private javax.swing.JButton SelectUnselectSequence_jButton;
    private javax.swing.JTextField VariablejTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel jStatusMessage;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    
 
    public void display(workflow_properties properties) {
       this.properties=properties;
        initComponents();    
        
        Message("Please select some \""+properties.get("outputType")+"\" to iterate.","");
        setIconImage(Config.image);
        updateUI();
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

     /**
     * Affiche un message dans la status bar
     * La provenance peut être mise dans un tooltip
     * @param text Le texte
     * @param tooltip Le tooltip texte
     */
    void Message(String text, String tooltip) {
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
    void MessageError(String text, String tooltip) {
        this.jStatusMessage.setEnabled(true);
        this.jStatusMessage.setForeground(Color.RED);
        this.jStatusMessage.setBackground(Color.WHITE);
        this.jStatusMessage.setToolTipText(tooltip);
        this.jStatusMessage.setText(text);
    }

    Vector<Integer> search (String regex, int mode) {
        Vector<Integer> returnArray = new Vector<Integer>();
        Pattern p;
        try {
            p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        } catch(java.util.regex.PatternSyntaxException e) {return returnArray;}
        int search_len=0;
        try {
            search_len=Integer.valueOf(regex);
        } catch (Exception e) {search_len=0;}
        switch (mode) {
            case MODE_ID:       lastSearch="Id with: "+regex;
                                for (int i=0; i<dataTree.size();i++) {
                                      ForMutableTreeNode data=dataTree.get(i);
//                                      Matcher m = p.matcher(data.getGi());
//                                      Matcher m2 = p.matcher(data.getAccession());
//                                      if (m.find()||m2.find()) returnArray.add(i);
                                }
                                break;
            case MODE_ACCESSION:lastSearch="Accession with: "+regex;
//                                for (int i=0; i<MultipleInfoSequence.size();i++) {
//                                     InfoSequence data=MultipleInfoSequence.get(i);
//                                      Matcher m = p.matcher(data.getAccession());
//                                      if (m.find()) returnArray.add(i);
//                                }
                                break;
            case MODE_DESC:  lastSearch="Description with: "+regex;
//                                for (int i=0; i<MultipleInfoSequence.size();i++) {
//                                      InfoSequence data=MultipleInfoSequence.get(i);
//                                       Matcher m = p.matcher(data.getName());
//                                      if (m.find()) returnArray.add(i);
//                                }
                                break;
            case MODE_LENMORE: lastSearch="Len(bp) greater: "+regex;
//                                for (int i=0; i<MultipleInfoSequence.size();i++) {
//                                    InfoSequence data=MultipleInfoSequence.get(i);
//                                    try {
//                                        int len=data.getLen();
//                                        if (len>=search_len) returnArray.add(i);
//                                    } catch(Exception e) {}
//                                }
                                break;
             case MODE_LENLESS: lastSearch="Len(bp) greater: "+regex;
//                                for (int i=0; i<MultipleInfoSequence.size();i++) {
//                                    InfoSequence data=MultipleInfoSequence.get(i);
//                                    try {
//                                        int len=Integer.valueOf(data.getLen());
//                                        if (len<=search_len) returnArray.add(i);
//                                    } catch(Exception e) {}
//                                }
                                break;
            case MODE_ALL:      lastSearch="All with: "+regex;
                                for (int i=0; i<dataTree.size();i++) {
                                    ForMutableTreeNode data=dataTree.get(i);
                                    Matcher m1 = p.matcher(data.toString());
                                    
                                    if (m1.find()) returnArray.add(i);
                                }
        } //end switch
        Config.log("Searching for "+lastSearch);
        System.out.printf(" found %d result(s)\n", returnArray.size());
        return returnArray;
      }

}