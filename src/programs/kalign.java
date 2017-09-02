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

//////////////////////////////////////////////////////////////////////////////////////////////33
///
/// Create a Thread to run kalign
///
/// Etienne Lord 2009


import biologic.Alignment;
import biologic.MultipleSequences;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;


public class kalign extends RunProgram {

   
    public kalign(workflow_properties properties) {
        super(properties);                      
        this.execute();
    }

    @Override
    public boolean init_checkRequirements() {
           int multiplesequences_id=properties.getInputID("multiplesequences");
           if (multiplesequences_id==0) {
            setStatus(status_error,"Error: No MultipleSequences found");
            return false;
           }
          MultipleSequences multitmp=new MultipleSequences(multiplesequences_id);
          if (multitmp.getNbSequence()==0) {
            setStatus(status_BadRequirements,"Error: No Sequence found...");
            return false;
           }
        return true;
    }



    @Override
    public void init_createInput() {
        int multiplesequences_id=properties.getInputID("multiplesequences");
        MultipleSequences multi=new MultipleSequences(multiplesequences_id);
        multi.outputFastaWithSequenceID("input.fasta");
    }


    @Override
    public String[] init_createCommandLine() {
         String[] com=new String[20];
           for (int i=0; i<com.length;i++) com[i]="";           
           com[0]="cmd.exe";
           com[1]="/C";
           com[2]=properties.getExecutable();
           com[3]="-f";
           com[4]="fasta";
           int index=5;
           if (properties.isSet("gop")) {
                com[index++]="-s";
                com[index++]=properties.get("gop");
           }
           if (properties.isSet("gep")) {
                com[index++]="-e";
                com[index++]=properties.get("gep");
           }
           if (properties.isSet("tgp")) {
                com[index++]="-t";
                com[index++]=properties.get("tgp");
           }
           if (properties.isSet("bonus")) {
                com[index++]="-m";
                com[index++]=properties.get("bonus");
           }
           if (properties.isSet("tree_building")) {
                com[index++]="-b";
                com[index++]=properties.get("tree_building").toLowerCase();
           }
           if (properties.isSet("distance")) {
                com[index++]="-d";
                com[index++]=properties.get("distance").toLowerCase();
           }
           com[index++]="<input.fasta";
           com[index++]=">outfile.fasta";

          return com;
    }

    @Override
    public void post_parseOutput() {
        Alignment align=new Alignment();
        align.loadFromFile("outfile.fasta");
        align.setName("Kalign ("+Util.returnCurrentDateAndTime()+")");
        align.saveToDatabase();
        properties.put("output_alignment_id", align.getId());
        addOutput(align);
        if (!properties.getBoolean("debug")) {
            Util.deleteFile("input.fasta");
            Util.deleteFile("outfile.fasta");
        }
    }
     
}

