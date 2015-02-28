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
import program.RunProgram;
import workflows.workflow_properties;

/**
 * TO DO
 * 
 * @author Etienne Lord
 * @since September 2010
 */


public class RemoveSequences extends RunProgram {

    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    Translator translator=new Translator();
    GeneticCode code;

 public RemoveSequences(workflow_properties properties) {
        super(properties);
        execute();
 }

    @Override
    public boolean init_checkRequirements() {
       int multiplesequences_id=properties.getInputID("multiplesequences");
       int alignment_id=properties.getInputID("alignment");
       if (multiplesequences_id==0&&alignment_id==0) {
           setStatus(this.status_BadRequirements,"No sequences found...");
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
        this.setStatus(this.status_running, "Renaming sequences");
        int multiplesequences_id=properties.getInputID("multiplesequences");
        int alignment_id=properties.getInputID("alignment");
        MultipleSequences m=new MultipleSequences(multiplesequences_id);
        Alignment a=new Alignment(alignment_id);


       //--Use Accession
       if (properties.getBoolean("use_accession")) {
           if (m.getId()!=0)
           for (Sequence s:m.getSequences()) {
               s.setName(s.getAccession());
           }
           if (a.getId()!=0)
               for (Sequence s:a.getSequences()) {
                   s.setName(s.getAccession());
               }
       }
        //--Add letter
       if (properties.getBoolean("letter_before_name")&&properties.isSet("letter")) {
           String letter=properties.get("letter");
           if (m.getId()!=0)
           for (Sequence s:m.getSequences()) {
               s.setName(letter+s.getName());
           }
           if (a.getId()!=0)
               for (Sequence s:a.getSequences()) {
                   s.setName(letter+s.getName());
               }
       }
          //--Add letter
       if (properties.getBoolean("letter_after_name")&&properties.isSet("letter_after")) {
           String letter=properties.get("letter_after");
           if (m.getId()!=0)
           for (Sequence s:m.getSequences()) {
               s.setName(s.getName()+letter);
           }
           if (a.getId()!=0)
               for (Sequence s:a.getSequences()) {
                   s.setName(s.getName()+letter);
               }
       }
        //--Space to underscore
       if (properties.getBoolean("space_to_underscore")) {
           if (m.getId()!=0)
               for (Sequence s:m.getSequences()) {
                   String name=s.getName();
                   name=Sequence.replaceChar(name, ' ', '_');
                   s.setName(name);
               }
           if (a.getId()!=0)
               for (Sequence s:a.getSequences()) {
                   String name=s.getName();
                   name=Sequence.replaceChar(name, ' ', '_');
                   s.setName(name);
               }
       }

         //--rename_to_8letters
       if (properties.getBoolean("rename_to_8letters")) {
           if (m.getId()!=0)
               for (Sequence s:m.getSequences()) {
                    String name=s.getName();
                    String id=""+s.getId();
                    int toCut=7-id.length();
                    if (name.length()>toCut) {
                        name=name.substring(0, toCut);
                    }
                    s.setName(name+"_"+id);
               }
           if (a.getId()!=0)
               for (Sequence s:a.getSequences()) {
                    String name=s.getName();
                    String id=""+s.getId();
                    int toCut=7-id.length();
                    if (name.length()>toCut) {
                        name=name.substring(0, toCut);
                    }
                    s.setName(name+"_"+id);
               }
       }

           if (m.getId()!=0) {
               for (Sequence s:m.getSequences()) s.setId(0);//--Force recreate
               m.setId(0);
               m.saveToDatabase();
               //Config.log(m);
               properties.put("output_multiplesequences_id", m.getId());
           }
           if (a.getId()!=0) {
                for (Sequence s:a.getSequences()) s.setId(0);//--Force recreate
               a.setId(0);
               a.saveToDatabase();
               //Config.log(a);
               properties.put("output_alignment_id", a.getId());
           }
//           Config.log(m);
//           Config.log(a);
          
           

    return true;        
    }

    @Override
    public void post_parseOutput() {
        
    }



  }
    

