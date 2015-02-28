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

import biologic.Tree;
import database.databaseFunction;
import javax.swing.JList;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import workflows.workflow_properties;

/**
 *
 * @author Etienne Lord
 */
public class DatabaseSQLite3_cellRendererOutputOnly extends JPanel implements ListCellRenderer{

    //GUI
    private javax.swing.JTextField TypejTextField;
    private javax.swing.JLabel SelectedElementJLabel;
    private javax.swing.JLabel jLabel2;
    private workflow_properties properties;
    databaseFunction df=new databaseFunction();

    ////////////////////////////////////////////////////////////////////////////
    //VARIABLES


    public DatabaseSQLite3_cellRendererOutputOnly(workflow_properties properties) {
        super();
        this.properties=properties;
        setupUI();
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
       this.setForeground(Color.BLACK);
       this.setBackground(Color.WHITE);

       //Default color and type
       if (isSelected) {
           setBackground(Color.LIGHT_GRAY);
           TypejTextField.setBackground(Color.LIGHT_GRAY);
       } else {
           setBackground(Color.WHITE);
           TypejTextField.setBackground(Color.WHITE);
       }
       String type=(String)value;
       TypejTextField.setText(type);

       //Display information for each type
       if (type.equals("Sequence")) {
           if (properties.getInt("output_sequence_id")==0) {
               this.SelectedElementJLabel.setText("No Elements.");
           } else {
               int sequence_id=properties.getInt("output_sequence_id");
               
               this.SelectedElementJLabel.setText(df.getInfoSequence(sequence_id).getName());
           }
       } else 
         if (type.equals("MultipleSequences")) {
           if (properties.getInt("output_multiplesequences_id")==0) {
               this.SelectedElementJLabel.setText("No Elements.");
           } else {
               int multiplesequences_id=properties.getInt("output_multiplesequences_id");
               
               //this.SelectedElementJLabel.setText(df.getMultipleSequenceName(multiplesequences_id));
           }
       } else 
         if (type.equals("Outgroup")) {
           if (properties.getInt("output_outgroup_id")==0) {
               this.SelectedElementJLabel.setText("No Elements.");
           } else {
               int sequence_id=properties.getInt("output_outgroup_id");
               
               this.SelectedElementJLabel.setText(df.getInfoSequence(sequence_id).getName());
           }
       }  else
         if (type.equals("Tree")) {
           if (properties.getInt("output_tree_id")==0) {
               this.SelectedElementJLabel.setText("No Elements.");
           } else {
               int tree_id=properties.getInt("output_tree_id");
               Tree tree=new Tree(tree_id);
               this.SelectedElementJLabel.setText(tree.getNote());
           }
       }else
         if (type.equals("Alignment")) {
           if (properties.getInt("output_alignment_id")==0) {
               this.SelectedElementJLabel.setText("No Elements.");
           } else {
               int alignment_id=properties.getInt("output_alignment_id");
               
               //this.SelectedElementJLabel.setText(df.getAlignmentName(alignment_id));
           }
       }
         else this.SelectedElementJLabel.setText("No Elements.");


       return this;
    }

    private void setupUI() {
      jLabel2 = new javax.swing.JLabel();
        TypejTextField = new javax.swing.JTextField();
        SelectedElementJLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setText(" ");

        TypejTextField.setEditable(false);
        TypejTextField.setBorder(null);

        SelectedElementJLabel.setText("No Selected Elements.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(TypejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SelectedElementJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(TypejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(SelectedElementJLabel)
                .addComponent(jLabel2))
        );
    }

}
