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
/// Create a Thread to run bali-phy concat
///
/// Etienne Lord 2009

import biologic.Alignment;
import biologic.MultipleSequences;
import configuration.Util;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;


public class concat_baliphy extends RunProgram {
   
    private String infile="infile.fasta";               //Unique infile : Must be deleted at the end
    private String outfile="outfile.fasta";              //Unique outfile: Must be deleted at the end
    
    ////////////////////////////////////////////////////////////////////////////
    /// Input / Output

    private Alignment align;

    public concat_baliphy(workflow_properties properties) {        
       super(properties);        
        execute();
    }

    @Override
    public boolean init_checkRequirements() {
        Vector<Integer> multiplesequences_id_top=properties.getInputID("multiplesequences", this.PortInputUP);
        Vector<Integer> multiplesequences_id_down=properties.getInputID("multiplesequences", this.PortInputDOWN);
        Vector<Integer> alignment_id_top=properties.getInputID("alignment", this.PortInputUP);
        Vector<Integer> alignment_id_down=properties.getInputID("alignment", this.PortInputDOWN);
//
//
//         int alignment_id=properties.getInputID("alignment");
//        if (multiplesequences_id_top==0&&alignment_id==0) {
//            setStatus(status_BadRequirements,"No sequence found.");
//            return false;
//        }
        return true;
    }

    @Override
    public void init_createInput() {
         int multiplesequences_id=properties.getInputID("multiplesequences");
         int alignment_id=properties.getInputID("alignment");
         if (multiplesequences_id!=0) {
         MultipleSequences multi=new MultipleSequences(multiplesequences_id);
         multi.outputFastaWithSequenceID(infile);
         } else {


         }       
    }

    
    @Override
    public void post_parseOutput() {
        align=new Alignment();
        align.loadFromFile(outfile);
        align.setName("Probcons ("+Util.returnCurrentDateAndTime()+")");
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
           if (properties.getBoolean("verbose")) com[index++]="-v";
           if (properties.isSet("pre_training")) {
               com[index++]="-pre";
               com[index++]=properties.get("pre_training");
           }
            if (properties.isSet("consistency")) {
               com[index++]="-c";
               com[index++]=properties.get("consistency");
           }
            if (properties.isSet("refinements")) {
               com[index++]="-ir";
               com[index++]=properties.get("refinements");
           }
           com[index++]=infile;
           com[index++]=">"+outfile;
           return com;
    }
    
   
 
}
