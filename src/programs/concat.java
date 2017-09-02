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
public class concat extends RunProgram {

    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    boolean done=false;
    Alignment first=new Alignment();
    Alignment last=new Alignment();
    Alignment new_alignment=new Alignment();

 
/**
 * Main Concat program
 * @param properties
 */
 public concat(workflow_properties properties) {
       super(properties);
    
        Vector<Integer>SequenceDOWN=properties.getInputID("sequence",PortInputDOWN);
        Vector<Integer>SequenceUP=properties.getInputID("sequence",PortInputUP);

        Vector<Integer>MultipleSequencesDOWN=properties.getInputID("multiplesequences",PortInputDOWN);
        Vector<Integer>MultipleSequencesUP=properties.getInputID("multiplesequences",PortInputUP);

        Vector<Integer>AlignmentDOWN=properties.getInputID("alignment",PortInputDOWN);
        Vector<Integer>AlignmentUP=properties.getInputID("alignment",PortInputUP);
        //--Concatenate UP + DOWN
        //--2. Test if we have more than 1 up?
        if (SequenceUP.size()<1
            &&MultipleSequencesUP.size()<1
            &&AlignmentUP.size()<1) {
            setStatus(status_error, "Error. No First sequences found");
            return;
        }
         if (SequenceDOWN.size()>1
            &&MultipleSequencesDOWN.size()>1
            &&AlignmentDOWN.size()>1) {
            setStatus(status_error, "Error. More than one End sequences found");
            return;
        }
        
        //Do the concatenation
        try {
        //--Find the output type (the DOWN type)
        // CASE 1. We create a MultipleSequences as output
        if (SequenceDOWN.size()>1||MultipleSequencesDOWN.size()>1) {




        // CASE 2. We create an Alignment
        } else {



        }
        for (Sequence s:first.getSequences()) {            
            //--Ugly. but effective
            s.setSequence(s.getSequence()+last.getSequences().get(0).getSequence());
            new_alignment.add(s);
        }
        new_alignment.setName("Concatenation of "+first.getName()+"+"+last.getName());
        new_alignment.setNote("Created on "+Util.returnCurrentDateAndTime());
        } catch(Exception e) {
            done=false;
             setStatus(status_error, "Error. Unable to concatenate sequence. Not the same number of first and last sequences.");
        }



//        if (SequenceDOWN.size()>1||SequenceUP.size()>1
//            ||MultipleSequencesDOWN.size()>1||MultipleSequencesUP.size()>1
//            ||AlignmentDOWN.size()>1||AlignmentUP.size()>1) {
//            setStatus(status_error, "Error. More than one destination sequences found");
//        } else {
//            if (SequenceUP)
//        }


        setStatus(status_done,(done?"Done":"Problem?"));

  }

    ////////////////////////////////////////////////////////////////////////////
    /// THREAD


    }
