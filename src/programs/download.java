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

import biologic.Results;
import biologic.seqclasses.httpconnection;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;

/**
 * This is an Interface to run httpdownload
 * @author Etienne Lord
 */


public class download extends RunProgram {

    String outfile="download.txt";

    ////////////////////////////////////////////////////////////////////////////
    /// Variables
  

 public download(workflow_properties properties) {
        super(properties);
        execute();
 }

    @Override
    public boolean init_checkRequirements() {
        if (!properties.isSet("website")) {
            this.setStatus(this.status_BadRequirements, "No Website to download found.");
            return false;
        }
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
        this.setStatus(status_running, this.properties.getName());        
        httpconnection e=new httpconnection();
        e.setOutputToDisk(true);
        if (!e.download(properties.get("website"),outfile)) {
            setStatus(status_error,"Unable to download from "+properties.get("website"));
            return false;
        }
        //--Parse here       
        return true;
    }

    @Override
    public void post_parseOutput() { 
        Results outputtext=new Results(outfile);
        outputtext.setName("Download from "+properties.get("website")+" at "+Util.returnCurrentDateAndTime());
        outputtext.setNote("Results from "+properties.getName()+" at "+Util.returnCurrentDateAndTime());
        outputtext.saveToDatabase();        
        properties.put("output_results_id", outputtext.getId());
        Util.deleteFile(outfile);
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Functions

   


  }
    

