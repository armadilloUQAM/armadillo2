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


import biologic.FastaFile;
import biologic.Results;
import biologic.SOLIDFile;
import biologic.Text;
import biologic.TextFile;
import program.RunProgram;
import workflows.workflow_properties;

/**
 * Subprogram to load some files into Armadillo
 * @author Etienne Lord
 * @since Mars 2010
 */
public class loadAllFile extends RunProgram {
 

    /**
     * Main contructor
     */
    public loadAllFile(workflow_properties properties) {
        super(properties);       
        execute();
    }

    /**
     * Verify some of the requirement
     * @return
     */
    @Override
    public boolean init_checkRequirements() {
        int TextID=properties.getInputID("Text");
        if (!properties.isSet("inputname")&&TextID==0) {
            setStatus(status_BadRequirements,"No filename specified.");
            return false;
        }
        return true;
    }


    @Override
    public void init_createInput() {
        
    }


    @Override
    public String[] init_createCommandLine() {
         String[] com=new String[11];
           for (int i=0; i<com.length;i++) com[i]="";        
        return com;
    }



    @Override
    public boolean do_run() throws Exception {
        return true;
    }


    @Override
    public void post_parseOutput() {

      int TextID=properties.getInputID("Text");
      if (TextID!=0) {
          Text text=new Text(TextID);
          text.Output("outfile");
          properties.put("inputname","outfile");
      }

       int type=properties.getInt("type");
       // 0. TextFile
       // 1. SOLIDFile
       // 2. FastaFile
       // 3. Results
       // 4. Text
       switch(type) {
            case 0: TextFile.saveFile(properties,properties.get("inputname"),"loadAllFiles","TextFile");
                break;
            case 1: SOLIDFile.saveFile(properties,properties.get("inputname"),"loadAllFiles","SOLIDFile");
                break;
            case 2: FastaFile.saveFile(properties,properties.get("inputname"),"loadAllFiles","FastaFile");
                break;
            case 3: Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"loadAllFiles");
                break;
            case 4: Text.saveFile(properties,properties.get("inputname"),"loadAllFiles","Text");
                break;
       }
       
    }

}
