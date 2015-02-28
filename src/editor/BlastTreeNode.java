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

import biologic.Blast;
import biologic.BlastHit;
import javax.swing.tree.DefaultMutableTreeNode;
import workflows.workflow_properties;

/**
 * Tree Node for the For-Loop Tree selection...
 * @author Etienne Lord, Mickael Leclercq
 */
public class BlastTreeNode extends DefaultMutableTreeNode {
    public Blast blast=null;
    private BlastHit bh=null;
    private String name = "";        
    private boolean leaf=true;
    private boolean selected=false;

    /**
     * Defautl constructor for title
     * @param name
     */
    public BlastTreeNode(String name) {
        super(name);
        this.name=name;        
    }

    public BlastTreeNode(BlastHit bh, String name) {
        super(name);
        this.name=name;
        this.bh=bh;
    }

    public BlastTreeNode(Blast blast, String name) {
        super(name);
        this.name=name;
        this.blast=blast;
    }

    @Override
    public String toString() {
        //if (bh!=null) return name;
        return getName();
    }
   
    

   
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

   

    /**
     * @return the leaf
     */
    @Override
    public boolean isLeaf() {
        return leaf;
    }

    /**
     * @param leaf the leaf to set
     */
    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


}
