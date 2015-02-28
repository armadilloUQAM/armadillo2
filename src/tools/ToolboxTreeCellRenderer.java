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

package tools;

import configuration.Config;
import database.ExplorerTreeMutableTreeNode;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author Leclercq Mickael
 */

 /**
 * This class is a JTree subclass that displays the tree of AWT or Swing
 * component that make up a GUI.
 */

  public class ToolboxTreeCellRenderer extends DefaultTreeCellRenderer {
    TreeCellRenderer renderer;
    Config config = new Config();


    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean selected, boolean expanded, boolean leaf, int row,
        boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        if (value instanceof ToolboxMutableTreeNode) {
            ToolboxMutableTreeNode nodeObj = (ToolboxMutableTreeNode)value;
            setText(nodeObj.properties.getName());
            this.setToolTipText(nodeObj.getTooltip());
            if (nodeObj.isProgramFound()) {
                 this.setForeground(Color.BLACK);
             } else this.setForeground(Color.GRAY);
        } else if (value instanceof ExplorerTreeMutableTreeNode ) {
            ExplorerTreeMutableTreeNode nodeObj=(ExplorerTreeMutableTreeNode)value;
            setText(nodeObj.getName());
           setIcon(config.getIcon(nodeObj.getType()));
        } else {
            setText(value.toString());
        }
        if (hasFocus){
            this.setForeground(Color.CYAN);
            this.setBackground(Color.LIGHT_GRAY);
        }

        return this;
    }
  }


