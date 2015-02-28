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
import biologic.Results;
import biologic.Text;
import biologic.Unknown;
import configuration.Config;
import configuration.Util;
import uk.ac.ebi.webservices.axis1.EBeyeClient;
import workflows.workflow_properties;
import configuration.Util.*;
import java.util.Vector;
import program.RunProgram;


public class ebeyeWebEBI extends RunProgram {
  
    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES 

    ////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTOR

    public ebeyeWebEBI(workflow_properties properties) {
        super(properties);       
        execute();
   }

     @Override
    public boolean init_checkRequirements() {
//         if (!properties.isSet("database")) {
//             setStatus(status_BadRequirements,"No database selected.");
//             return false;
//         }
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
      //--Variable
      Results results=new Results();
      Results results_download=new Results();
      setStatus(Config.status_running,"Running "+properties.getName());
        //--Initialize some variables here
       EBeyeClient client = new EBeyeClient();        
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

       //--Note: We must split by 200 elements each time.
        if (properties.getBoolean("AutomaticLoadID")) {
           try {
           String[] str=inputDOWN.getUnknown().split("\n");
           String ids="";
           for (String st:str) {
               ids+=st+", ";
           }
           properties.put("TERM", ids);
           } catch(Exception e){}
        }
//       String[] str=client.listDomains();
//       setStatus(Config.status_running,"Supported databases.");
//       for (String st:str) {
//          setStatus(Config.status_running,st);
//       }

//       String[] str2=client.getDomainsHierarchy();
//       setStatus(Config.status_running,"Supported databases.");
//       for (String st:str2) {
//          setStatus(Config.status_running,st);
//       }
       // fetchBatch(db, ids, format, style)
       String database=properties.get("database");
       String format=(properties.isSet("format")?properties.get("format"):"default");
//       //--etNumberOfResults(domain, query)

       if (database.equals("All")&&properties.isSet("TERM")) {
           setStatus(Config.status_running,"***Searching all databases to get number of results for "+properties.get("TERM")+" ***");
           for (String domain:client.listDomains()) {
            int results_nb=client.getNumberOfResults(domain, properties.get("TERM"));
            setStatus(Config.status_running,domain+"\t"+results_nb);
            results.getUnknownST().append(domain+"\t"+results_nb+"\n");
           }
           results.setName("Search All ebEye (EBI) for "+properties.get("TERM")+" at "+Util.returnCurrentDateAndTime());
           results.setNote("Created on "+Util.returnCurrentDateAndTime());
           results.saveToDatabase();
           properties.put("output_results_id",results.getId());
       } else if (properties.isSet("TERM")) {
            setStatus(Config.status_running,"***Searching "+database+" to get number of results for "+properties.get("TERM")+" ***");

            int results_nb=client.getNumberOfResults(database, properties.get("TERM"));
            setStatus(Config.status_running,database+"\t"+results_nb);
            setStatus(Config.status_running,"***Results ids***");
            //setStatus(Config.status_running,"***Note: If id number is high");
            String str[]={};
            if (properties.isSet("number_to_download")) {
                setStatus(Config.status_running,"***Note: Limited to "+properties.get("number_to_download")+" results ***");
                str=client.getResultsIds(database, properties.get("TERM"), 0, Math.min(properties.getInt("number_to_download"), results_nb));
            } else {
                str=client.getAllResultsIds(database, properties.get("TERM"));
            }
            for (String st:str) {
                 setStatus(Config.status_running,st);
                 results.getUnknownST().append(st+"\n");
            }
           results.setName("Search "+database+" at ebEye (EBI) for "+properties.get("TERM")+" at "+Util.returnCurrentDateAndTime());
           results.setNote("Created on "+Util.returnCurrentDateAndTime());
           results.saveToDatabase();
           properties.put("output_results_id",results.getId());
           if (properties.getBoolean("AutomaticLoadData")) {
               setStatus(Config.status_running,"***Downloading informations***");
               String[] fields=client.listFields(database);
              results_download.getUnknownST().append("#");
              for (String ret:fields) results_download.getUnknownST().append(ret+"\t");
                     results_download.getUnknownST().append("\n");
              String ids="";
               int count=0;
               for (String st:str) {
                   ids+=st.trim()+", ";
                     count++;
                   //--Note: max download = 200
                   
                     String[] retval=client.getEntry(database, st, fields);
                     for (String ret:retval) results_download.getUnknownST().append(ret+"\t");
                     results_download.getUnknownST().append("\n");
                     ids="";
                   
               }
             results_download.setName("Search "+database+" at ebEye (EBI) for "+properties.get("TERM")+" at "+Util.returnCurrentDateAndTime());
            results_download.setNote("Created on "+Util.returnCurrentDateAndTime());
            results_download.saveToDatabase();
            properties.put("output_text_id",results_download.getId());
           }


       }
//       String[] str=client.getSupportedDBs();
//       setStatus(Config.status_running,"Supported databases.");
//       for (String st:str) {
//          setStatus(Config.status_running,st);
//       }
//       String[] str2=client.getSupportedStyles();
//       setStatus(Config.status_running,"Supported styles.");
//       for (String st:str2) {
//          setStatus(Config.status_running,st);
//       }
//        String[] str3=client.getSupportedFormats();
//
//       setStatus(Config.status_running,"Supported formats.");
//       for (String st:str3) {
//          setStatus(Config.status_running,st);
//       }
       setStatus(Config.status_done,"");
  }
       
}
