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
/// Create a Thread to run 
///
/// Etienne Lord 2009

import biologic.Alignment;
import biologic.MultipleSequences;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;


public class findMIRNA extends RunProgram {
   
    private String infile="infile.fasta";               //Unique infile : Must be deleted at the end
    private String outfile="outfile.fasta";              //Unique outfile: Must be deleted at the end
    
    ////////////////////////////////////////////////////////////////////////////
    /// Input / Output

    private Alignment align;

    public findMIRNA(workflow_properties properties) {        
       super(properties);
        
        execute();
    }

    @Override
    public boolean init_checkRequirements() {
        int multiplesequences_id=properties.getInputID("multiplesequences");
        if (multiplesequences_id==0) {
            setStatus(status_BadRequirements,"No sequence found.");
            return false;
        } 
        return true;
    }

    @Override
    public void init_createInput() {
         int multiplesequences_id=properties.getInputID("multiplesequences");
         MultipleSequences multi=new MultipleSequences(multiplesequences_id);
         multi.outputFastaWithSequenceID(infile);
         addInput(multi);
    }

    
    @Override
    public void post_parseOutput() {
        align=new Alignment();
        align.loadFromFile(outfile);
        align.setName("Muscle ("+Util.returnCurrentDateAndTime()+")");
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
          int index=7;
          com[0]="cmd.exe";
           com[1]="/C";
           com[2]=properties.getExecutable();
           com[3]="-in";
           com[4]=infile;
           com[5]="-out";
           com[6]=outfile;
           if (properties.getBoolean("diags1")) {
               com[index++]="-diags1";
           }
           if (properties.getBoolean("sv")) {
               com[index++]="-sv";
           }
           if (properties.isSet("distance1")) {
               com[index++]="-distance1";
               com[index++]="kbit20_3";
           }
            if (properties.isSet("maxiters")) {
               com[index++]="-maxiters";
               com[index++]=properties.get("maxiters");
           }
           if (properties.isSet("maxmb")) {
               com[index++]="-maxmb";
               com[index++]=properties.get("maxmb");
           }
           if (properties.isSet("maxhours")) {
               com[index++]="-maxhours";
               com[index++]=properties.get("maxhours");
           }
           return com;
    }
    

    private String uniqueInfileOutfile(String filename) {
        if (infile.equals("")) this.infile=filename+this.hashCode();
        if (outfile.equals("")) this.outfile=filename+this.hashCode();
        return infile;
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
        final findMIRNA other = (findMIRNA) obj;        
        return true;
    }
 
}
