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

// For the moment, all programs should be in the programs package
package programs;

////////////////////////////////////////////////////////////////////////////////
///
/// This is an example program class to use with the Armadillo Workflow Platform
///
///
/// Etienne Lord 2010


 import biologic.Sequence;
 import biologic.Alignment;
 import biologic.MultipleSequences;

import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;


public class ExampleProgramClass extends RunProgram {  
  
    
    ////////////////////////////////////////////////////////////////////////////
    /// Input / Output
    ///
    /// All internal variables should be declared here for ease of use.

    private Alignment alignment;
    private MultipleSequences multiplesequences;
    private Sequence sequence;


    ////////////////////////////////////////////////////////////////////////////
    /// Constructor
    ///
    /// Current program class will be use only

    /**
     * Main default constructor
     * @param properties
     */
    public ExampleProgramClass(workflow_properties properties) {        
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
        return super.do_run();
    }


    @Override
    public boolean do_run_withoutWait() throws Exception {
        return super.do_run_withoutWait();
    }


    @Override
    public void post_parseOutput() {
    }
   
 
}
