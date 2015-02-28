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


package tools;

import javax.swing.JList;
import workflows.*;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Etienne Lord
 * @since July 2009
 */
public class Toolbox_ListCellRenderer extends JTextField implements ListCellRenderer{

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
           //Valeurs par défaut
       this.setForeground(Color.BLACK);
       this.setBackground(Color.WHITE);
       this.setBorder(null);
       if (isSelected) {
            //this.setForeground(Color.WHITE);
            this.setBackground(Color.LIGHT_GRAY);
       }
       //--Handle the different type
       String V="";
       if (value==null) return this; //We don't handle null value
       if (value instanceof String) V=(String)value;
       if (value instanceof workflow_properties) {
           V=((workflow_properties)value).getName();
           this.setForeground(Color.BLUE);
       }

       //--Handle different Object Color
       //--TO DO: Put in a dictionnary (???)
       if (V.equals("If")||V.equals("While")||V.equals("For")||V.equals("Begin")||V.equals("End")) {
           this.setForeground(Color.RED);
       }

       this.setText(V);
       return this;
    }
    

}
