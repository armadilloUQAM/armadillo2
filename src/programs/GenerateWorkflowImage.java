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

import biologic.Output;
import biologic.Results;
import configuration.Util;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.regexp.RE;
import program.RunProgram;
import workflows.workflow_properties;
import workflows.workflow_properties_dictionnary;

/**
 * This will generate an image (png) and a html page
 * @author Etienne Lord
 * @since July 2010
 */


public class GenerateWorkflowImage extends RunProgram {

    ////////////////////////////////////////////////////////////////////////////
    /// Variables


 public GenerateWorkflowImage(workflow_properties properties) {
        super(properties);
        execute();
 }

    @Override
    public boolean init_checkRequirements() {          
        return true;      
    }

    @Override
    public void init_createInput() {      
    }

    @Override
    public String[] init_createCommandLine() {
        return new String[0];
    }


    @Override
    public boolean do_run() throws Exception {               


        return true;
    }


    @Override
    public boolean do_run_withoutWait() throws Exception {
        return super.do_run_withoutWait();
    }


    @Override
    public void post_parseOutput() {
    }




  }
    

