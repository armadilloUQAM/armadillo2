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
import database.databaseFunction;
import program.runningThreadInterface;
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
public class mutiplesequences_to_alignment extends RunProgram {

    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    /// -- Desctiption
    private String filename="";             //Filename
    private String infile="";               //Unique infile : Must be deleted at the end
    private String outfile="";              //Unique outfile : Must be deleted at the end
    public String name="";                  //This is this program name
    public String desc="";                  //This is a description of the program (might change at runtime)
    public int paramID=0;                   //Param_id associated in the sqlite database
    public int programID=0;                 //Program_ID associated in the sqlite database
    public int runprogramID=0;              //runprogram_ID associated in the sqlite database
    // -- Debug
    public static boolean debug=false;
    // -- For the Thread version
    private Vector<String> output=new Vector<String>(); //Program output
    private int exitVal=0;          //Program exit value
    private boolean done=false;     //Is it done
    private int status=0;           //status code
 

 public mutiplesequences_to_alignment(workflow_properties properties) {
       super(properties);
      if (debug) Config.log(name);
        int multiplesequences_id=properties.getInputID("input_multiplesequences_id");
        int alignment_id=properties.getInt("input_alignment_id");
        
        if (alignment_id!=0) {
            Alignment align=new Alignment(alignment_id);
            for (Sequence s:align.getSequences()) s.setId(0);
            align.setNote("Convertion of Alignment to multipleSequences: "+align.getNote());
            df.addMultipleSequences(align);
            properties.put("output_multiplesequences_id", align.getId());
            done=true;
            
        } else {
            MultipleSequences multi=new MultipleSequences(multiplesequences_id);
            Alignment align=new Alignment();
            align.getSequences().addAll(multi.getSequences());
            for (Sequence s:align.getSequences()) s.setId(0);
            align.setName(multi.getName());
            align.setNote("Convertion of multipleSequences to Alignment: "+multi.getNote());
            align.saveToDatabase();
            properties.put("output_alignment_id", align.getId());
            done=true;
        } 
        setStatus(status_done,(done?"done":"problem?"));

  }

    ////////////////////////////////////////////////////////////////////////////
    /// THREAD

    
    

    private String uniqueInfileOutfile(String filename) {
        if (infile.equals("")) this.infile=filename+this.hashCode();
        if (outfile.equals("")) this.outfile=filename+"outfile"+this.hashCode();
        return infile;
    }

    @Override
    public String toString() {
         return this.name;
     }

   

    public String getDesc() {
        return desc;
    }

    }
