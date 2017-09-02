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
/// Create a Thread to run Gblocks
///
/// Etienne Lord 2010

import biologic.Alignment;
import biologic.MultipleSequences;
import biologic.Results;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;


public class gblocks extends RunProgram {

    private String infile="infile.fasta";                 //Unique infile : Must be deleted at the end
    private String outfile="infile.fasta-gb";             //Unique outfile containing the new fasta
    private String outfile_results="infile.fasta-gb.txt"; //Unique outifle containing the results
    
    ////////////////////////////////////////////////////////////////////////////
    /// Input / Output

    private Alignment align;
    private Results results;

    public gblocks(workflow_properties properties) {        
       super(properties);        
        execute();
    }

    @Override
    public boolean init_checkRequirements() {
        int alignment_id=properties.getInputID("alignment");
        if (alignment_id==0) {
            setStatus(status_BadRequirements,"No sequence found.");
            return false;
        }        
        return true;
    }

    @Override
    public void init_createInput() {
         int alignment_id=properties.getInputID("alignment");
         Alignment align=new Alignment(alignment_id);
         align.outputFastaWithSequenceID(infile);
    }

    
    @Override
    public void post_parseOutput() {
             results=new Results(outfile_results);
             results.setName("Gblocks results("+Util.returnCurrentDateAndTime()+")");
             results.saveToDatabase();
             addOutput(results);
              properties.put("output_results_id", results.getId());
             
             align=new Alignment();
             align.loadFromFile(outfile);
             align.setName("Gblocks ("+Util.returnCurrentDateAndTime()+")");
             align.saveToDatabase();
             properties.put("output_alignment_id", align.getId());
             addOutput(align);
             
             
        Util.deleteFile(infile);
        Util.deleteFile(outfile);
        Util.deleteFile(outfile_results);
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
           com[index++]="-p=t"; //--Output the text results
           
           if (properties.isSet("moltype")) {
                // 0 DNA
                // 1 Protein
                // 2 Codons 
               switch(properties.getInt("moltype")) {
                   case 0:com[index++]="-t=d"; break;
                   case 1:com[index++]="-t=p"; break;
                   case 2:com[index++]="-t=c"; break;                     
               }
           }
           // Minimum Number Of Sequences For A Conserved Position
           if (properties.isSet("Minimum_Number_Of_Sequences_For_A_Conserved_Position")) {
                com[index++]="-b1="+properties.get("Minimum_Number_Of_Sequences_For_A_Conserved_Position");
           }
           // Minimum Number Of Sequences For A Flank Position
           if (properties.isSet("Minimum_Number_Of_Sequences_For_A_Flank_Position")) {
                com[index++]="-b2="+properties.get("Minimum_Number_Of_Sequences_For_A_Flank_Position");
           }
           //Maximum Number Of Contiguous Nonconserved Positions
           if (properties.isSet("Maximum_Number_Of_Contiguous_Nonconserved_Positions")) {
               com[index++]="-b3="+properties.get("Maximum_Number_Of_Contiguous_Nonconserved_Positions");
           }
           // Minimum Length Of A Block
           if (properties.isSet("Minimum_Length_Of_A_Block")) {
               com[index++]="-b4="+properties.get("Minimum_Length_Of_A_Block");
           }
           if (properties.isSet("Allowed_Gap_Positions")) {
               if (properties.get("Allowed_Gap_Positions").equalsIgnoreCase("None")) com[index++]="-b5=n";
               if (properties.get("Allowed_Gap_Positions").equalsIgnoreCase("With Half")) com[index++]="-b5=h";
               if (properties.get("Allowed_Gap_Positions").equalsIgnoreCase("All")) com[index++]="-b5=a";
           }
           if (properties.isSet("Use_Similarity_Matrices")&&!properties.getBoolean("Use_Similarity_Matrices")) com[index++]="-b6=n";
           return com;
    }
    
   
 
}
