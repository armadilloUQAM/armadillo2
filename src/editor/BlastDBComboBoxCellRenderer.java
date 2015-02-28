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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Custom Renderer for Blast database
 * @author Etienne Lord
 */
public class BlastDBComboBoxCellRenderer extends JLabel implements ListCellRenderer {

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
       
       this.setForeground(Color.BLACK);
       this.setBackground(Color.WHITE);
       //Default color and type
       if (isSelected||cellHasFocus) {
           setBackground(Color.LIGHT_GRAY);
       } else {
           setBackground(Color.WHITE);
       }
        String str=(String)value;
        
        if (str.startsWith("-")||str.isEmpty()) {
           setText(str);
           setFont(list.getFont().deriveFont(Font.BOLD));
        } else {
            setFont(list.getFont());
            String s[]=str.split("\t");
            if (s.length==2) {
                setText(s[0]+"        "+s[1]);
            } else setText(str);
        }
        

        return this;
    }

}
