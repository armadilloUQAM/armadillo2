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

import biologic.Alignment;
import biologic.InfoMultipleSequences;
import biologic.InfoSequence;
import biologic.MultipleSequences;
import biologic.Sequence;
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
public class DatabaseSQLite3_cellRenderer_type extends JPanel implements ListCellRenderer{

    //GUI
    private javax.swing.JTextField TypejTextField;
    private javax.swing.JLabel SelectedElementJLabel;
    private javax.swing.JLabel jLabel2;
   
    ////////////////////////////////////////////////////////////////////////////
    //VARIABLES


    public DatabaseSQLite3_cellRenderer_type() {
        super();
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
       if (value instanceof Sequence) {
           TypejTextField.setText("Sequence");
           Sequence s=(Sequence)value;
           this.SelectedElementJLabel.setText(s.getName());
       }
       if (value instanceof InfoSequence) {
           TypejTextField.setText("Sequence");
           InfoSequence s=(InfoSequence)value;
           this.SelectedElementJLabel.setText(s.getName());
       }
       if (value instanceof MultipleSequences) {
           TypejTextField.setText("MultipleSequences");
           MultipleSequences s=(MultipleSequences)value;
           this.SelectedElementJLabel.setText(s.getName());
       }
       if (value instanceof InfoMultipleSequences) {
           TypejTextField.setText("MultipleSequences");
           InfoMultipleSequences s=(InfoMultipleSequences)value;
           this.SelectedElementJLabel.setText(s.getName());
       }
        if (value instanceof Tree) {
           TypejTextField.setText("Tree");
           Tree s=(Tree)value;
           this.SelectedElementJLabel.setText(s.getNote());
       }





       return this;
    }

    private void setupUI() {
      jLabel2 = new javax.swing.JLabel();
        TypejTextField = new javax.swing.JTextField();
        SelectedElementJLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setText(" ewaraw");

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
