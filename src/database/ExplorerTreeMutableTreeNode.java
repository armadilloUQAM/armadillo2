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

import Class.Classdata;
import biologic.Biologic;
import configuration.Config;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Etienne Lord, Mickael Leclercq
 */
public class ExplorerTreeMutableTreeNode extends DefaultMutableTreeNode {
    private String name="";
    private String type = "";
    private int id = 0;
    private String tooltip="";
    private boolean isTable=false;
    private int countResult = 0;
    private boolean loaded=false;


    public ExplorerTreeMutableTreeNode(String name, String type, int id) {
     super(name);
    this.name=name;
     this.type=type;
     this.id=id;
    }

    @Override
    public String toString() {
        return this.getType()+"\t"+this.getId()+"\t"+this.getName();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the Biologic Object id from database
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Set the Biologic Object id from database
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the isTable
     */
    public boolean isTable() {
        return isTable;
    }

    /**
     * @param isTable the isTable to set
     */
    public void setIsTable(boolean isTable) {
        this.isTable = isTable;
    }

    /**
     * @return the countResult
     */
    public int getCountResult() {
        return countResult;
    }

    /**
     * @param countResult the countResult to set
     */
    public void setCountResult(int countResult) {
        this.countResult = countResult;
    }
    
    /**
     * This try to return a Biologic object from this node
     * @return
     */
    public Biologic getBiologic() {
     try {
        Classdata c=new Classdata("biologic."+this.getType());
        if (c==null) {
            return null;
        }
        Object o=c.newObject();
        if (o==null) {
            return null;
        }
        //--Load Object
        //--New: ensure the good type
        //if (this.getType().equals("Genome") )
        
        if (this.getId()!=0) {
            ((Biologic)o).loadFromDatabase(this.getId());
        }
        return  ((Biologic)o);
     } catch(Exception e) {
         Config.log("getBiologic: Unable to get Class "+this.getType()+" "+this.getId());
     }
     return  null;
    }

    /**
     * Helper function to load from database the Name associated with this biologic object
     * @param id
     * @return
     */
    public String getBiologicName() {
        if (this.getType().isEmpty()) return "";
        try {
        Classdata c=new Classdata("biologic."+this.getType());
        Object o=c.newObject();
        if (o==null) return "";
        if (this.getId()!=0) {
            this.setName(((Biologic)o).getNameId(this.getId()));
            return this.getName();
        } else {
            return "";
        }
        } catch(Exception e ){return "";}
    }

    /**
     * @return the loaded
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * @param loaded the loaded to set
     */
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public String getNote() {
         Classdata c=new Classdata("biologic."+this.getType());
        Object o=c.newObject();
        if (o==null) return "";
        if (this.getId()!=0) {
            ((Biologic)o).loadFromDatabase(this.getId());
            return ((Biologic)o).getNote();
        } else {
            return "";
        }
    }

    /**
     * @return the tooltip
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * @param tooltip the tooltip to set
     */
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
}
