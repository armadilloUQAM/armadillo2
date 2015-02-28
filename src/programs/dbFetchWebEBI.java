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
import biologic.MultipleSequences;
import biologic.Results;
import biologic.Sequence;
import biologic.Unknown;
import configuration.Config;
import configuration.Util;
import uk.ac.ebi.webservices.WSDbfetchClient;
import workflows.workflow_properties;
import configuration.Util.*;
import java.util.Vector;
import program.RunProgram;


public class dbFetchWebEBI extends RunProgram {
  
    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES 

    ////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTOR

    public dbFetchWebEBI(workflow_properties properties) {
        super(properties);       
        execute();
   }

     @Override
    public boolean init_checkRequirements() {
         if (!properties.isSet("database")) {
             setStatus(status_BadRequirements,"No database selected.");
             return false;
         }
         return true;
    }

    @Override
    public String[] init_createCommandLine() {
          String[] com=new String[30];
          for (int i=0; i<com.length;i++) com[i]="";
          return com;
    }


     @Override
    public boolean do_run() throws Exception {                                                
            this.RunNormalWebService();        
        return true;
    }

  public void RunNormalWebService() throws Exception {
      setStatus(Config.status_running,"Running "+properties.getName());
        //--Initialize some variables here
       Results results=new Results();
       WSDbfetchClient client = new WSDbfetchClient();

       // fetchBatch(db, ids, format, style)
       String database=properties.get("database");
       String format=(properties.isSet("format")?properties.get("format"):"default");
       Vector<Integer> TextID_DOWN=properties.getInputID("Text", PortInputDOWN);
       Vector<Integer> ResultsID_DOWN=properties.getInputID("Results", PortInputDOWN);
       Unknown inputDOWN=new Unknown();
       if (TextID_DOWN.size()>0) {
            inputDOWN=new Unknown(TextID_DOWN.get(0));
        }
        if (ResultsID_DOWN.size()>0) {
            inputDOWN=new Unknown(ResultsID_DOWN.get(0));
        }
       if (TextID_DOWN.size()>0||ResultsID_DOWN.size()>0) {
           properties.put("AutomaticLoadID",true);
       }
        if (TextID_DOWN.size()==0&&ResultsID_DOWN.size()==0) {
           properties.put("AutomaticLoadID",false);
       }

        if (properties.getBoolean("AutomaticLoadID")) {          
           String[] str=inputDOWN.getUnknown().split("\n");
           String ids="";
           int count=0;
           for (String st:str) {
               ids+=st.trim()+" ";
               count++;                          
                 try {
                     setStatus(status_running, "Fetching data for "+ids+" in "+database);
                     String retval=client.fetchBatch(database, ids, format, "default");
                     results.getUnknownST().append(retval+"\n");
                 }catch(Exception e) {setStatus(status_error, e.getMessage());}
                 ids="";
               }                      
            results.setName("dbFetchWebEBI for "+count+" ids at "+Util.returnCurrentDateAndTime());
            results.setNote("Created on "+Util.returnCurrentDateAndTime());
            results.saveToDatabase();
            properties.put("output_results_id",results.getId());
            if (format.equalsIgnoreCase("fasta")) {
                results.Output("temp.txt");

                MultipleSequences multi=new MultipleSequences();
                multi.loadSequences("temp.txt");
                Util.deleteFile("temp.txt");
                multi.setName("dbFetchWebEBI for "+count+" ids at "+Util.returnCurrentDateAndTime());
                multi.setNote("Created on "+Util.returnCurrentDateAndTime());
                multi.saveToDatabase();
                properties.put("output_multiplesequences_id", multi.getId());
                //--Note: output first sequence
                Sequence s=multi.getSequences().get(0);
                properties.put("output_sequence_id", s.getId());
            }

           
        } else
       //--
       if (properties.isSet("ID")) {
           String[] str=properties.get("ID").split("\n");
           String ids="";
           int count=0;
           for (String st:str) {
               ids+=st.trim()+" ";
                 count++;
               //--Note: max download = 200
              
                 try {
                     setStatus(status_running, "Fetching data for "+ids+" in "+database);
                     String retval=client.fetchBatch(database, ids, format, "default");
                     results.getUnknownST().append(retval+"\n");
                 }catch(Exception e) {setStatus(status_error, e.getMessage());}
                 ids="";
               }           
           results.setName("dbFetchWebEBI for "+count+" ids at "+Util.returnCurrentDateAndTime());
            results.setNote("Created on "+Util.returnCurrentDateAndTime());
            results.saveToDatabase();
            properties.put("output_results_id",results.getId());
              if (format.equalsIgnoreCase("fasta")) {
                results.Output("temp.txt");

                MultipleSequences multi=new MultipleSequences();
                multi.loadSequences("temp.txt");
                Util.deleteFile("temp.txt");
                multi.setName("dbFetchWebEBI for at "+Util.returnCurrentDateAndTime());
                multi.setNote("Created on "+Util.returnCurrentDateAndTime());
                multi.saveToDatabase();
                properties.put("output_multiplesequences_id", multi.getId());
                //--Note: output first sequence
                Sequence s=multi.getSequences().get(0);
                properties.put("output_sequence_id", s.getId());
            }

       }
       setStatus(Config.status_done,"");
  }
       
}
