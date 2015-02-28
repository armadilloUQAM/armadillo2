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
import biologic.Biologic;
import biologic.MultipleSequences;
import biologic.Output;
import biologic.Sequence;
import configuration.Config;
import database.databaseFunction;
import program.runningThreadInterface;
import java.util.Vector;
import org.apache.regexp.RE;
import program.RunProgram;
import workflows.workflow_properties;
import workflows.workflow_properties_dictionnary;

/**
 *
 * Example: Search Ncbi, Search Pubmed, If, Not...
 * @author Etienne
 */
public class splitSequencesGroup extends RunProgram {

    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    /// -- Desctiption
    private String filename="";             //Filename
    private String infile="";               //Unique infile : Must be deleted at the end
    private String outfile="";              //Unique outfile : Must be deleted at the end
    private boolean done=false;             //--Test if we did something

 public splitSequencesGroup(workflow_properties properties) {
       super(properties);              
       execute();
  }



    @Override
    public boolean init_checkRequirements() {
        int multiplesequences_id=properties.getInputID("input_multiplesequences_id");
        int alignment_id=properties.getInt("input_alignment_id");
        if (!properties.isSet("criteria")) {
            setStatus(splitSequencesGroup.status_BadRequirements,"No split criteria found...");
            return false;
        }

        if (multiplesequences_id==0&&alignment_id==0) {
            setStatus(splitSequencesGroup.status_BadRequirements,"No group to split found...");
            return false;
        }
        return true;
    }

    @Override
    public String[] init_createCommandLine() {
        return super.init_createCommandLine();
    }

    @Override
    public boolean do_run() throws Exception {
        //--Variables
        int multiplesequences_id=properties.getInputID("input_multiplesequences_id");
        int alignment_id=properties.getInt("input_alignment_id");
        String criteria=properties.get("criteria");
        String name="";
        //--Find name, if found
         for (String inputtype:workflow_properties_dictionnary.InputOutputType) {
        Vector<Integer>ids_name=properties.getInputID(inputtype, save_to_file.PortInputDOWN);
        if (ids_name.size()>0&&properties.getBoolean("preserve")) {
               int idt=ids_name.get(0); //--We care only for the first one
               Output out=new Output();
               out.setType(inputtype);
               out.setTypeid(0);
               name=((Biologic)out.getBiologic()).getNameId(idt);               
            }
         }
          if (properties.isSet("new_name")&&!properties.get("new_name").isEmpty()) {              
                name=name+properties.get("new_name");              
         }                  
        //--Work
        if (alignment_id!=0) {
            Alignment align=new Alignment(alignment_id);
            for (Sequence s:align.getSequences()) s.setId(0);
            Alignment NewGroup=new Alignment();
            RE regex=new RE(criteria,RE.MATCH_CASEINDEPENDENT);
            for (Sequence s:align.getSequences()) {
                if ((properties.getBoolean("search_sequence")&&regex.match(s.getSequence()))||
                    (properties.getBoolean("search_sequence_name")&&regex.match(s.getName()))) {
                        NewGroup.add(s);
                }
            }
            for (Sequence s:NewGroup.getSequences()) s.setId(0);
            if (name.isEmpty()) {
                NewGroup.setName(align.getName());
            } else {
                NewGroup.setName(name);
            }
            NewGroup.setNote("Spliting Alignment into Groups using criteria : "+criteria);
            NewGroup.saveToDatabase();
            properties.put("output_alignment_id", NewGroup.getId());
        } else {
            MultipleSequences multi=new MultipleSequences(multiplesequences_id);
            //--This is a custom splitter to split my sequence group into a new group 
            MultipleSequences NewGroup=new MultipleSequences();
            RE regex=new RE(criteria,RE.MATCH_CASEINDEPENDENT);            
            for (Sequence s:multi.getSequences()) {
                //--Normal match
                if (!properties.getBoolean("negative_match")) {
                    if ((properties.getBoolean("search_sequence")&&regex.match(s.getSequence()))||
                        (properties.getBoolean("search_sequence_name")&&regex.match(s.getName()))) {
                            NewGroup.add(s);
                    }
                } else {
                    //--Negative match (we do not want the sequence
                    if ((properties.getBoolean("search_sequence")&&!regex.match(s.getSequence()))||
                        (properties.getBoolean("search_sequence_name")&&!regex.match(s.getName()))) {
                            NewGroup.add(s);
                    }
                }
            }
            for (Sequence s:NewGroup.getSequences()) s.setId(0);
            if (name.isEmpty()) {
                NewGroup.setName(multi.getName());
            } else {
                NewGroup.setName(name);
            }
            NewGroup.setNote("Spliting MultipleSequences into Groups using criteria : "+criteria);
            NewGroup.saveToDatabase();
            properties.put("output_multiplesequences_id", NewGroup.getId());
            done=true;
        }
        setStatus(status_done,(done?"done":"problem?"));
        return done;
    }

    @Override
    public void post_parseOutput() {

    }




    }
