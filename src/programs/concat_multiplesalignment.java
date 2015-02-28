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
import biologic.MultipleAlignments;
import biologic.MultipleSequences;
import biologic.Sequence;
import configuration.Config;
import configuration.Util;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;

/**
 * Concatenate some sequence
 *
 * @author Etienne
 */
public class concat_multiplesalignment extends RunProgram {

    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    boolean done=false;    
    Alignment new_alignment=new Alignment();
    MultipleAlignments multi=new MultipleAlignments();
 
/**
 * Main Concat program
 * @param properties
 */
 public concat_multiplesalignment(workflow_properties properties) {
       super(properties);    
      execute();
    }


   @Override
    public boolean init_checkRequirements() {
       int multiplealignments_id=properties.getInputID("multiplealignments");
       if (multiplealignments_id==0) {
           setStatus(status_BadRequirements,"Error: No MultipleAlignments found..");
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
        int multiplealignments_id=properties.getInputID("multiplealignments");
        multi=new MultipleAlignments(multiplealignments_id);

        //--TO DO: OFFER CHOICE IN MENU HERE
        String note="Concatenation of "+multi.getNbAlignments()+" alignments:\n";
        for (Alignment a:multi.getAlignments()) {
            new_alignment.add(a);
            note+="\t"+a.getName()+"\n";
        }
        System.out.println(new_alignment);
        note+="Created on "+Util.returnCurrentDateAndTime();
        new_alignment.setName("Concatenation of "+multi.getNbAlignments()+" alignments");
        new_alignment.setNote(note);
        new_alignment.saveToDatabase();
        properties.put("output_alignment_id", new_alignment.getId());
        return true;
    }


    @Override
    public boolean do_run_withoutWait() throws Exception {
        return super.do_run_withoutWait();
    }


    @Override
    public void post_parseOutput() {
    }
}