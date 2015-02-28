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

package programs;


import biologic.Alignment;
import biologic.MultipleTrees;
import biologic.Tree;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;

/**
 * This is a dummy program so a program without a specific program
 * It won't display an error
 * @author Etienne Lord
 * @since Mai 2010
 */
public class dummy extends RunProgram {
 

    /**
     * Main contructor
     */
    public dummy(workflow_properties properties) {
        super(properties);
        execute();
    }


    @Override
    public boolean init_checkRequirements() {      
        return true;
    }



    @Override
    public boolean init_run() throws Exception {
        return true;
    }


    @Override
    public void init_createInput() {
    }


    @Override
    public String[] init_createCommandLine() {
         String[] com=new String[1];
         com[0]="";
        return com;
    }


    @Override
    public boolean do_run() throws Exception {
        return true;
    }


    @Override
    public void post_parseOutput() {
    
    }

    
    

  

    
   



}
