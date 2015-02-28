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
import configuration.Config;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;

/**
 * A taks is not an external program by itself
 * but we can execute it
 *
 * Example: Search Ncbi, Search Pubmed, If, Not...
 * @author Etienne
 */


public class Convert_to_Protein extends RunProgram {

    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    Translator translator=new Translator();
    GeneticCode code;

 public Convert_to_Protein(workflow_properties properties) {
        super(properties);
        execute();
 }

    @Override
    public boolean init_checkRequirements() {
        int multiplesequences_id=properties.getInputID("multiplesequences");
        int alignment_id=properties.getInputID("alignment");
        int sequence_id=properties.getInputID("sequence");

        if (sequence_id==0&&multiplesequences_id==0&&alignment_id==0) {
            setStatus(status_BadRequirements,"Nothing to convert...");
            return false;
        }
        if (properties.isSet("start")) {
            int start=properties.getInt("start");
            translator.start=start;
        }
        code=translator.code.get(properties.getInt("genetic_code"));
        if (code==null) {
            setStatus(status_BadRequirements,"Unable to set the GeneticCode...");
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
        this.setStatus(this.status_running, "Converting...");
        int multiplesequences_id=properties.getInputID("multiplesequences");
        int alignment_id=properties.getInputID("alignment");
        int sequence_id=properties.getInputID("sequence");
        boolean remove_stop=properties.getBoolean("remove_stop");
        Config.log("Alignment: "+alignment_id+" Sequence: "+sequence_id+"multiplesequences: "+multiplesequences_id);
        if (alignment_id!=0) {
            Alignment align=new Alignment(alignment_id);
            Alignment newalign=new Alignment();
            newalign.setName("Protein from "+align.getName());
            newalign.setNote("Convertion of Alignment to Protein at "+Util.returnCurrentDateAndTime());
            for (Sequence s:align.getSequences()) {
                String aa="";
                if (remove_stop)  {
                     aa=translator.translate_without_stop(s, code);
                } else {
                    aa=translator.translate(s, code);
                }
                Sequence s2=s.clone();
                s2.setId(0);
                s2.setSequence(aa);
                s2.setSequence_type("aa");
                if (s2.getLen()==0) {
                    setStatus(this.status_error,"Unable to find start codon for "+s.getName());
                    return false;
                }
                newalign.add(s2);
            }
            newalign.saveToDatabase();
            properties.put("output_alignment_id", newalign.getId());
        }
        if (sequence_id!=0) {
            Sequence s=new Sequence(sequence_id);
            Sequence newsequence=s.clone();
            newsequence.setId(0);
            String aa="";
                if (remove_stop)  {
                    aa=translator.translate_without_stop(s, code);
                } else {
                    aa=translator.translate(s, code);
                }
                //--Remove last start if found...                       
                newsequence.setSequence(aa);
                newsequence.setSequence_type("aa");
                if (newsequence.getLen()==0) {
                    setStatus(this.status_error,"Unable to find start codon for "+s.getName());
                    return false;
                }
                newsequence.saveToDatabase();
                properties.put("output_sequence_id", newsequence.getId());
        }

        if (multiplesequences_id!=0) {
            MultipleSequences multi=new MultipleSequences(multiplesequences_id);
            MultipleSequences newmulti=new MultipleSequences();
            newmulti.setName("Protein from "+multi.getName());
            newmulti.setNote("Convertion of MultipleSequences to Protein at "+Util.returnCurrentDateAndTime());
            for (Sequence s:multi.getSequences()) {
                String aa="";
                if (remove_stop)  {
                     aa=translator.translate_without_stop(s, code);
                } else {
                    aa=translator.translate(s, code);
                }
                Sequence s2=s.clone();
                s2.setId(0);
                s2.setSequence(aa);
                s2.setSequence_type("aa");
                if (s2.getLen()==0) {
                    setStatus(this.status_error,"Unable to find start codon for "+s.getName());
                    return false;
                }
                newmulti.add(s2);
            }            
            newmulti.saveToDatabase();            
            properties.put("output_multiplesequences_id", newmulti.getId());
        }
        return true;
    }

    @Override
    public void post_parseOutput() {
        
    }



  }
    

