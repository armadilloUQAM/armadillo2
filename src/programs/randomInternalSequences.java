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
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;


public class randomInternalSequences extends RunProgram {

    
 public randomInternalSequences(workflow_properties properties) {
        super(properties);
        execute();        
  }

    @Override
    public boolean do_run() {
        setStatus(status_running,"Generating...");
        int mode=properties.getInt("mode");
        int seed=properties.getInt("seed");
        int replicate=properties.getInt("replicate");
        int type=properties.getInt("type");
        
        if (replicate<1) replicate=2;
        if (seed<1) seed=2;
        System.out.println("Generation of "+replicate+" DNA sequences of length: "+seed );
        Alignment a=new Alignment(seed, replicate, mode);
        a.setRunProgram_id(this.getId());
        a.setName("Generation of "+replicate+" DNA sequences of "+seed + " bp");
        a.setNote("Created on "+Util.returnCurrentDateAndTime());
        //--Note: we now generate all 3 case automaticaly 
        setStatus(status_running,"Generating... one sequence with length "+seed);
        setStatus(status_running,"Generating... "+replicate+" MultipleSequences with length "+seed);
        setStatus(status_running,"Generating... "+replicate+" Alignment with length "+seed);
        if (a.getSequences().size()==0) {
            setStatus(status_error,"Unable to generate the sequences. Probably the sequence length are smaller than 11.");
            return false;
        }
        
        Sequence s=a.getSequences().get(0);
        s.saveToDatabase();
         properties.put("output_sequence_id", s.getId());
         df.addMultipleSequences(a);
//         setStatus(status_running,"Generating... "+replicate+" MultipleSequences with length "+seed);
         properties.put("output_multiplesequences_id", a.getId());
         a.saveToDatabase();                            
 //        setStatus(status_running,"Generating... "+replicate+" Alignment with length "+seed);
         properties.put("output_alignment_id", a.getId());
         
         
//        switch(type) {
//            //Sequences
//            case 0:
//                   Sequence s=a.getSequences().get(0);
//                   s.saveToDatabase();
//                   setStatus(status_running,"Generating... one sequence with length "+seed);
//                   properties.put("output_sequence_id", s.getId());
//            break;
//            //MultipleSequences
//            case 1:
//                  df.addMultipleSequences(a);
//                  setStatus(status_running,"Generating... "+replicate+" MultipleSequences with length "+seed);
//                  properties.put("output_multiplesequences_id", a.getId());
//            break;
//            //Alignment
//            case 2:
//                 a.saveToDatabase();                            
//                 setStatus(status_running,"Generating... "+replicate+" Alignment with length "+seed);
//                 properties.put("output_alignment_id", a.getId());
//            break;
//        }        
        return true;
    }
   
}
