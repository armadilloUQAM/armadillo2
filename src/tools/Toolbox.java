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

import java.awt.Dimension;
import javax.swing.JInternalFrame;
import workflows.armadillo_workflow;

/**
 * This is a container to the ToolJInternalFrame to ensure there is only one
 * This is the displayed toolboix
 * @author Etienne Lord
 * @since June 2009
 */
public class Toolbox {
    public static ToolJInternalFrame toolbox=new ToolJInternalFrame();

    public JInternalFrame getJInternalFrame() {
        return toolbox;
    }

    public void setVisible(boolean b) {
        getJInternalFrame().setVisible(b);
    }

    public Dimension getDimension() {
        Dimension d=new Dimension();
        d.setSize(toolbox.getBounds().width, toolbox.getBounds().height);
        return d;
    }

     public void mazimizeSize() {
        toolbox.mazimizeSize();
    }

    /**
     * This reload the tool tree
     */
    public void reloadToolTree() {
        toolbox.loadProgram();
    }

    /**
     * Reload the entire database tree
     */
    public void reloadDatabaseTree() {
        //-- debug System.out.println("Reload database tree");
        toolbox.createDatabaseTree();
    }

    /**
     * Reload only the Workflow part of the Database
     */
    public void reloadWorkflowsTree() {
        System.out.println("Reload database tree");
        toolbox.createTreeToolbox();
    }

    public void reloadCurrentWorkflowsTree(armadillo_workflow workflow) {
        ToolJInternalFrame.setCurrent_workflow(workflow);
        toolbox.createCurrentWorkflowDatabaseTree();
    }

    public  javax.swing.JTree getApplicationTree() {
        return toolbox.getApplicationsTree();
    }
    
}
