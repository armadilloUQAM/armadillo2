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
import biologic.MultipleSequences;
import biologic.Sequence;
import biologic.seqclasses.GeneticCode;
import biologic.seqclasses.Translator;
import configuration.Util;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;

/**
 * A taks is not an external program by itself
 * but we can execute it
 *
 * Example: Search Ncbi, Search Pubmed, If, Not...
 * @author Etienne
 */


public class ProteinTocDNA extends RunProgram {

    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    Translator translator=new Translator();
    GeneticCode code;

 public ProteinTocDNA(workflow_properties properties) {
        super(properties);
        execute();
 }

    @Override
    public boolean init_checkRequirements() {
        Vector<Integer> multiplesequences_id=properties.getInputID("multiplesequences",null);
        Vector<Integer> alignment_id=properties.getInputID("alignment",null);
        code=translator.code.get(properties.getInt("genetic_code"));
        if (code==null) {
            setStatus(status_BadRequirements,"Unable to set the GeneticCode...");
            return false;
        }
        if (multiplesequences_id.size()==0&&alignment_id.size()==0) {
            setStatus(status_BadRequirements,"MultipleSequences or Alignment is empty...");
            return false;
        }
        //--Verification number


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
        this.setStatus(this.status_running, "Converting...Protein to cDNA");
        Vector<Integer> multiplesequences_id=properties.getInputID("multiplesequences",PortInputUP);
        Vector<Integer> alignment_id_up=properties.getInputID("alignment",PortInputUP);
        Vector<Integer> alignment_id=properties.getInputID("alignment",PortInputDOWN);
        Vector<Integer> multiplesequences_id_down=properties.getInputID("multiplesequences",PortInputDOWN);
        boolean output_multiplesequences=properties.getBoolean("output_ms");
        MultipleSequences protein=null;
        MultipleSequences original=null;
        try {
        //--Find the protein...
       for (int id:multiplesequences_id) {
           MultipleSequences m=new MultipleSequences(id);
           original=m;
       }
       if (original==null) {
          for (int id:alignment_id_up) {
           Alignment m=new Alignment(id);
           original=m;
          }
       }
       for (int id:alignment_id) {
           Alignment m=new Alignment(id);
           protein=m;
       }
       if (protein==null) {
          for (int id:multiplesequences_id_down) {
           MultipleSequences m=new MultipleSequences(id);
           protein=m;
          }
       }

       if (protein==null) {
            setStatus(this.status_error,"Unable to find Protein sequences");
            return false;
        }
       if (original==null) {
            setStatus(this.status_error,"Unable to find Original sequences");
            return false;
        }
       setStatus(status_running,"original sequences:"+original+"\n");
       setStatus(status_running," proteic sequences:"+protein+"\n");
       
       Alignment output=new Alignment();
        output.setName("ProteinTocDNA Created on "+Util.returnCurrentDateAndTime());
        for (int i=0; i<protein.getSequences().size();i++) {
            Sequence prot=protein.getSequences().get(i);
            for (Sequence s:original.getSequences()) {
                if (prot.getName().equals(s.getName())) {
                    Sequence n=new Sequence();
                    n.setName(prot.getName());
                    n.setSequence(translator.aa_to_dna(s, prot, code));
                    n.setSequence_type("dna");
                    output.add(n);
                }
            }
        }
        if (output_multiplesequences) {
            df.addMultipleSequences(output);
            properties.put("output_multiplesequences_id", output.getId());
        } else {
            output.saveToDatabase();
            properties.put("output_alignment_id", output.getId());
        }
        if (output.getNbSequence()!=protein.getNbSequence()) {
            setStatus(this.status_running,"Something was wrong.. Not the good number of output sequences..");
        }
        } catch(Exception e) {e.printStackTrace();return false;}
    return true;        
    }

    @Override
    public void post_parseOutput() {
        
    }



  }
    

