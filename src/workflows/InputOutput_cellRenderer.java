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


package workflows;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Etienne
 */
public class InputOutput_cellRenderer extends JTextField implements TableCellRenderer{

    

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
       //Valeurs par défaut
       this.setForeground(Color.BLACK);
       this.setBackground(Color.WHITE);
       this.setBorder(null);
       if (isSelected) {
            //this.setForeground(Color.WHITE);
            this.setBackground(Color.LIGHT_GRAY);
       }

       if (value==null) return this; //We don't handle null value
       String V=(String)value;
       this.setText(V);
//       //--Special case
//       if (((String)value).equals("OutputOutputText"))      {
//           this.setText("Software's output");
//       }
       if (V.startsWith("Connector")) {
            setForeground(Color.BLUE);
       }
      
       return this;
    }

}
