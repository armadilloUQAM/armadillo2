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
@Deprecated
public class DatabaseSQLite3_cellRenderer_sequence extends JPanel implements ListCellRenderer{

    //GUI
    private javax.swing.JTextField jTextField1;
    databaseFunction df=new databaseFunction();
    public static int w;
    public static int h;

    public DatabaseSQLite3_cellRenderer_sequence() {
        super();
        setupUI();
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
       this.setForeground(Color.BLACK);
       this.setBackground(Color.WHITE);

       //Default color and type
       if (isSelected) {
           setBackground(Color.LIGHT_GRAY);
           
       } else {
           setBackground(Color.WHITE);
           
       }
       String type=(String)value;
      this.jTextField1.setText(type);
      h=this.jTextField1.getHeight();
      w=this.jTextField1.getWidth();
      this.jTextField1.getHorizontalVisibility().setMaximum(20000);
              //Display information for each type
        return this;
    }

    private void setupUI() {
      jTextField1 = new javax.swing.JTextField();

        jTextField1.setText("jTextField1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }

}
