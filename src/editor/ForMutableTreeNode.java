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

import javax.swing.tree.DefaultMutableTreeNode;
import workflows.workflow_properties;

/**
 * Tree Node for the For-Loop Tree selection...
 * @author Etienne Lord, Mickael Leclercq
 */
public class ForMutableTreeNode extends DefaultMutableTreeNode implements Comparable {
    private workflow_properties properties;
    private String name = "";
    private String value = "";
    private int id=0;
    private boolean leaf=true;
    private boolean selected=false;

    /**
     * Defautl constructor for title
     * @param name
     */
    public ForMutableTreeNode(String name,String value) {
        super(name);
        this.name=name;
        this.value=value;
    }

    public ForMutableTreeNode(String name,String value,int id) {
        super(name);
        this.name=name;
        this.value=value;
        this.id=id;
    }

    public ForMutableTreeNode(workflow_properties properties, String name, String value) {
        super(name);
        this.name=name;
        this.value=value;
        this.properties=properties;
    }

    @Override
    public String toString() {
        if (isLeaf()&&!getValue().isEmpty()) return getName()+" = "+getValue();
        return getName();
    }
   
    public int compareTo(Object o) {
        try {
            ForMutableTreeNode N=(ForMutableTreeNode)o;
            return this.getProperties().get("Type").compareTo(N.getProperties().get("Type"));
        } catch(Exception e) {e.printStackTrace();return 0;}
    }

    @Override
    public boolean equals(Object obj) {
        try {
        ForMutableTreeNode N=(ForMutableTreeNode)obj;
        //Config.log(this.properties.get("Type")+" "+N.properties.get("Type")+"->"+(this.properties.get("Type").equals(N.properties.get("Type"))));
        if (getProperties()==null) return false;
        return this.getProperties().get("Type").equals(N.getProperties().get("Type"));
        }catch(Exception e) {e.printStackTrace();return false;}
    }

    /**
     * @return the properties
     */
    public workflow_properties getProperties() {
        return properties;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
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
     * @param properties the properties to set
     */
    public void setProperties(workflow_properties properties) {
        this.properties = properties;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }


}
