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


import biologic.Alignment;
import biologic.Sequence;
import biologic.Tree;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;

/**
 * Create a random sequence using SeqGen
 * @author Etienne Lord
 * @since Mars 2010
 */
public class seqgen extends RunProgram {
 


    /**
     * Main contructor
     */
    public seqgen(workflow_properties properties) {
        super(properties);       
        execute();
    }


    @Override
    public boolean init_checkRequirements() {
        int tree_id=properties.getInputID("tree");
        if (tree_id==0) {
            setStatus(status_BadRequirements,"No tree found.");
            return false;
        }
        return true;
    }


    @Override
    public void init_createInput() {
        int tree_id=properties.getInputID("tree");
        Tree tree=new Tree(tree_id);
        tree.outputNewickWithSequenceID("tree.nh");
    }


    @Override
    public String[] init_createCommandLine() {
         String[] com=new String[11];
           for (int i=0; i<com.length;i++) com[i]="";
           int index=3;
           com[0]="cmd.exe";
           com[1]="/C";
           com[2]=properties.getExecutable();
           if (properties.isSet("model")) {
               com[index++]="-m"+properties.get("model");
           }
           if (properties.isSet("seed")) {
               com[index++]="-l"+properties.get("seed"); //length?
           }
           if (properties.isSet("datasets")) {
               com[index++]="-n"+properties.get("datasets");
           }
           if (properties.isSet("scaling")) {
               com[index++]="-s"+properties.get("scaling");
           }
           if (properties.isSet("alpha")) {
               com[index++]="-a"+properties.get("alpha");
           }
           if (properties.isSet("invariable")) {
               com[index++]="-i"+properties.get("invariable");
           }
           if (properties.isSet("ts_tv_ratio")) {
               com[index++]="-t"+properties.get("ts_tv_ratio");
           }
           com[index++]="<"+"tree.nh";
           com[index++]=">"+"outfile";
        return com;
    }

    @Override
    public void post_parseOutput() {
       Alignment a=new Alignment();
       a.readSequenceFromPhylip("outfile");       
       int type=properties.getInt("type"); //--Output type
       String model=properties.get("model");
       if (model.equalsIgnoreCase("F84")||model.equalsIgnoreCase("HKY")||model.equalsIgnoreCase("GTR")) {
           a.setName("Seq-Gen of "+a.getNbSequence()+" DNA sequences of "+a.getSequenceSize()+" bp");
       } else {      
           a.setName("Seq-Gen of "+a.getNbSequence()+" Amino acid sequences of "+a.getSequenceSize()+" aa");
       }       
       a.setNote("Generated on "+Util.returnCurrentDateAndTime());
        a.setRunProgram_id(this.getId());
        if (a.getNbSequence()>0) { 
            switch(type) {
                //Sequences
                case 0:
                       Sequence s=a.getSequences().get(0);
                       s.setRunProgram_id(this.getId());
                       s.saveToDatabase();
                       setStatus(status_running,"Generating... one sequence with length "+properties.get("seed"));
                       properties.put("output_sequence_id", s.getId());
                break;
                //MultipleSequences
                case 1:
                      df.addMultipleSequences(a);
                      setStatus(status_running,"Generating... MultipleSequences with length "+properties.get("seed"));
                      properties.put("output_multiplesequences_id", a.getId());
                break;
                //Alignment
                case 2:
                     a.saveToDatabase();                 
                     setStatus(status_running,"Generating... Alignment with length "+properties.get("seed"));
                     properties.put("output_alignment_id", a.getId());
                break;
            }
        } else {
            setStatus(status_error,"No sequences were generated with Seq-Gen. Verify the input tree... ");
        }
    }

    
    

  

    
   



}
