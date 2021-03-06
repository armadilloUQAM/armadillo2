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
/// Create a Thread to run dca
///
/// Etienne Lord 2010

import biologic.Alignment;
import biologic.MultipleSequences;
import biologic.Results;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;


public class dca extends RunProgram {

    private String infile="infile.fasta";               //Unique infile : Must be deleted at the end
    private String outfile="outfile";              //Unique outfile: Must be deleted at the end
    
    ////////////////////////////////////////////////////////////////////////////
    /// Input / Output

    private Alignment align;

    public dca(workflow_properties properties) {        
       super(properties);        
        execute();
    }

    @Override
    public boolean init_checkRequirements() {
        int multioplesequences_id=properties.getInputID("multiplesequences");
        if (multioplesequences_id==0) {
            setStatus(status_BadRequirements,"No sequence found.");
            return false;
        }        
        return true;
    }

    @Override
    public void init_createInput() {
         int MultipleSequences_id=properties.getInputID("multiplesequences");
         MultipleSequences multi=new MultipleSequences(MultipleSequences_id);
         multi.outputFastaWithSequenceID(infile);
    }

    
    @Override
    public void post_parseOutput() {
             align=new Alignment();
             align.loadFromFile(outfile);
             align.setName("DCA ("+Util.returnCurrentDateAndTime()+")");
             align.saveToDatabase();
             properties.put("output_alignment_id", align.getId());
             addOutput(align);
        Util.deleteFile(infile);
        Util.deleteFile(outfile);
    }

    @Override
    public String[] init_createCommandLine() {
          String[] com=new String[20];
           for (int i=0; i<com.length;i++) com[i]="";
          int index=3;
          com[0]="cmd.exe";
           com[1]="/C";
           com[2]=properties.getExecutable();           
           com[index++]=infile;
           
           

//           if (properties.getBoolean("verbose")) com[index++]="-v";
//           if (properties.isSet("pre_training")) {
//               com[index++]="-pre";
//               com[index++]=properties.get("pre_training");
//           }
//            if (properties.isSet("consistency")) {
//               com[index++]="-c";
//               com[index++]=properties.get("consistency");
//           }
//            if (properties.isSet("refinements")) {
//               com[index++]="-ir";
//               com[index++]=properties.get("refinements");
//           }
//           com[index++]=infile;
//           com[index++]=">"+outfile;
           return com;
    }
    
   
 
}
