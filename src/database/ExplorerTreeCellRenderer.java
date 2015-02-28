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

package database;

import configuration.Config;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

 /**
  * @author Leclercq Mickael and Etienne Lord
  * @since July 2009
 * This class is a JTree subclass that displays the tree of AWT or Swing
 * component that make up a GUI.
 */

  public class ExplorerTreeCellRenderer extends DefaultTreeCellRenderer {
    //TreeCellRenderer renderer;
    static Config config = new Config();
 

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean selected, boolean expanded, boolean leaf, int row,
        boolean hasFocus) {
       try {     
          super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
       } catch(Exception e2){}
        ExplorerTreeMutableTreeNode nodeObj = (ExplorerTreeMutableTreeNode)value;

        try {
         if (nodeObj!=null) {
             if (hasFocus){
                this.setForeground(Color.CYAN);
            }        
            if (nodeObj.getCountResult()>0) {
               if (nodeObj.isTable()) {
                   setText(nodeObj.getName()+" ["+nodeObj.getCountResult()+"]");              
               } else {               
                   setText(nodeObj.getName()+" ["+nodeObj.getCountResult()+"]");              
               }
            } else {         
                setText(nodeObj.getName());
            }

              //--Icon

                  setIcon(config.getIcon(nodeObj.getType()));
                  if (nodeObj.getType().equals("OutputText")) {
                      setIcon(config.getIcon("Text"));
                  }

                  //--Tooltip
                  setToolTipText(nodeObj.getTooltip());
          }

        } catch(Exception e) {}
        return this;
    }
  }


