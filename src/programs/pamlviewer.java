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

////////////////////////////////////////////////////////////////////////////////
///
/// Create a Thread to run  paml viewer
///
/// Etienne Lord 2009

import biologic.HTML;
import biologic.Results;
import biologic.seqclasses.LoadPAML;
import configuration.Config;
import configuration.Util;
import java.io.File;
import program.RunProgram;
import workflows.workflow_properties;

public class pamlviewer extends RunProgram {

    ////////////////////////////////////////////////////////////////////////////
    /// Input / Output
    String projectName="";
    String outfile="";

    public pamlviewer(workflow_properties properties) {
       super(properties);
        execute();
    }

    @Override
    public boolean init_checkRequirements() {
        int results_id=properties.getInputID("results");
        if (results_id==0) {
            setStatus(status_BadRequirements,"No results found.");
            return false;
        }       
        return true;
    }

    @Override
    public void init_createInput() {
        //--Create inputfile
        int results_id=properties.getInputID("results");
        Results r=new Results(results_id);
        this.projectName=r.getName();
        r.Output("infile");
        //--Create outputdir        
        this.createDir("ResultPAML_"+properties.getID());
        this.outfile=config.resultsDir()+File.separator+"ResultPAML_"+properties.getID()+File.separator+"outfile.html";
    }

    
    
    @Override
    public boolean do_run() throws Exception {                
        LoadPAML p=new LoadPAML();
        p.generateResultFor("infile",outfile, this.projectName);
        return true;
    }

  

    @Override
    public void post_parseOutput() {
       HTML h=new HTML(outfile);
       h.setName("PAML results (HTML) for "+this.projectName);      
       h.saveToDatabase();
       properties.put("output_html_id", h.getId());
    }

    @Override
    public String[] init_createCommandLine() {
          String[] com=new String[20];            
           return com;
    }


    public void createDir(String dirname) {

        config.createDir(config.resultsDir()+File.separator+dirname);
        config.createDir(config.resultsDir()+File.separator+dirname+File.separator+"images");
        config.createDir(config.resultsDir()+File.separator+dirname+File.separator+"css");
        try {
            Util.copy(new File(config.dataPath() + File.separator+"site"+File.separator+"images"+File.separator+"logo.png"), new File(
                    config.resultsDir() + File.separator + dirname + File.separator+"images"+File.separator+"logo.png"));

            Util.copy(new File(config.dataPath() + File.separator+"site"+File.separator+"images"+File.separator+"img01.jpg"), new File(
                    config.resultsDir() + File.separator + dirname + File.separator+"images"+File.separator+"img01.jpg"));

            Util.copy(new File(config.dataPath() + File.separator+"site"+File.separator+"images"+File.separator+"img02.jpg"), new File(
                    config.resultsDir() + File.separator + dirname + File.separator+"images"+File.separator+"img02.jpg"));

            Util.copy(new File(config.dataPath() + File.separator+"site"+File.separator+"images"+File.separator+"img03.jpg"), new File(
                    config.resultsDir() + File.separator + dirname + File.separator+"images"+File.separator+"img03.jpg"));

            Util.copy(new File(config.dataPath() + File.separator+"site"+File.separator+"images"+File.separator+"spacer.gif"), new File(
                    config.resultsDir() + File.separator + dirname + File.separator+"images"+File.separator+"spacer.gif"));

            Util.copy(new File(config.dataPath() + File.separator+"site"+File.separator+"css"+File.separator+"styles.css"), new File(
                    config.resultsDir() + File.separator + dirname + File.separator+"css"+File.separator+"styles.css"));

            Util.copy(new File(config.dataPath() + File.separator+"site"+File.separator+"favicon.ico"), new File(
                    config.resultsDir() + File.separator + dirname + File.separator+"favicon.ico"));
        } catch (Exception ex) {
            Config.log("Unable to copy files...");
        }    
    }

     @Override
    public int hashCode() {
         return Util.returnCount();
     }

     @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final muscle other = (muscle) obj;
        return true;
    }

}
