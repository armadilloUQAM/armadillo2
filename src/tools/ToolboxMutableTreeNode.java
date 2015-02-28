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

import configuration.Util;
import database.*;
import javax.swing.tree.DefaultMutableTreeNode;
import program.RunProgram;
import workflows.workflow_properties;

/**
 *
 * @author Etienne Lord, Mickael Leclercq
 */
public class ToolboxMutableTreeNode extends DefaultMutableTreeNode implements Comparable {

    workflow_properties properties;
    private boolean programFound=false; //--Flag to indicate if the program is found

    public ToolboxMutableTreeNode(workflow_properties properties) {
        super(properties.getName());
        this.properties=properties;
        if (!properties.isSet("ObjectType")) {
            programFound=true;
        } else {    
           programFound=RunProgram.isExecutableFound(properties);
        }
    }
    
    @Override
    public String toString() {
        return properties.toString();
    }
   
    public int compareTo(Object o) {
        try {
            ToolboxMutableTreeNode N=(ToolboxMutableTreeNode)o;
            return this.properties.get("Type").compareTo(N.properties.get("Type"));
        } catch(Exception e) {e.printStackTrace();return 0;}
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if (obj instanceof ToolboxMutableTreeNode) {
            ToolboxMutableTreeNode N=(ToolboxMutableTreeNode)obj;
                //Config.log(this.properties.get("Type")+" "+N.properties.get("Type")+"->"+(this.properties.get("Type").equals(N.properties.get("Type"))));
                return this.properties.get("Type").equals(N.properties.get("Type"));
            } else {
                return false;
            }
        }catch(Exception e) {return false;}
    }

    /**
     * @return the programFound
     */
    public boolean isProgramFound() {
        return programFound;
    }

    /**
     * @param programFound the programFound to set
     */
    public void setProgramFound(boolean programFound) {
        this.programFound = programFound;
    }

    /**
     * Get the tooltip associated with this program
     * @return
     */
    public String getTooltip() {
        //--Note(if the program is not found, change the tooltip
        if (!isProgramFound()) {
            return "<html>"+
                    this.properties.getTooltip()+"<br>"+
                    "<b>Warning. Executable not found for this tool.</b>"+
                    "</html>";
            
        } else
            //--default
            return properties.getTooltip();
    }

    /**
     * Return this properties
     * @return 
     */
    public workflow_properties getProperties() {
        return this.properties;
    }
    
}
