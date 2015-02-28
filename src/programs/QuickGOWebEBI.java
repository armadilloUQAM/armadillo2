/*
 *  Armadillo Workflow Platform v2.0
 *  A simple pipeline system for phylogenetic analysis
 *  
 *  Copyright (C) 2009-2014  Etienne Lord, Mickael Leclercq
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

import biologic.Blast;
import biologic.BlastHit;
import biologic.Results;
import biologic.Sequence;
import biologic.Text;
import biologic.seqclasses.blastParser;
import biologic.seqclasses.qblast;
import configuration.Config;
import configuration.Util;
 import java.net.*;
import java.io.*;
import java.util.*;
import program.RunProgram;
import static program.RunProgram.status_done;
import static program.RunProgram.status_running;
import workflows.workflow_properties;

/**
 * Get information about Gene Onthology from EBI
 * See; http://www.ebi.ac.uk/QuickGO/WebServices.html
 * 
 * @author Etienne Lord
 * @since June 2014
 */
public class QuickGOWebEBI extends RunProgram {
      
    private ArrayList<String>term_go=new ArrayList<String>();
    private ArrayList<String>term_go_info=new ArrayList<String>();
    private String infofile="";
    
    ////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTOR

    public QuickGOWebEBI(workflow_properties properties) {
        super(properties);
        execute();
    }
    
      @Override
    public boolean init_checkRequirements() {
            Vector<Integer>text_id=properties.getInputID("Text",PortInputDOWN);            
            Vector<Integer>BlastHit_id=properties.getInputID("BlastHit",PortInputDOWN);
            
          if (text_id.size()==0&&BlastHit_id.size()==0) {
            setStatus(status_BadRequirements, "Error: no input found.");
            return false;
        }
        
          
       if (text_id.size()>0) {
           Text text=new Text(text_id.get(0));
           String[] data=text.getText().split("\n");
           setStatus(status_running, "Preparing data to search...");
           for (String s:data) {
               
               if (s.indexOf(".")>-1) {
                   String ts=s.substring(0,s.indexOf("."));
                   setStatus(status_running, "Warning. Removing version for "+s+" -> "+ts);
                   s=ts;
               }
               if (!s.isEmpty()) this.term_go.add(s);
           }
           infofile=text.getName();
           this.addInput(text);
       } else if (BlastHit_id.size()>0) {
            //System.out.println(BlastHit_id.get(0));
           Text blast=new Text(BlastHit_id.get(0));
           infofile=blast.getName();
           String[] stri=blast.getText().split("\n");
           int index_accession=5;
           for (String str:stri) {
               if (!str.isEmpty()&&!str.startsWith("Query=")&&!str.startsWith("Total")&&!str.startsWith("qstrand")) {
                   //--Split by \t
                   System.out.println(index_accession);
                   String [] data=str.split("\t");                                    
                   String accession=data[index_accession];
                   System.out.println(accession);
                   if (accession.indexOf(".")>-1&&!accession.isEmpty()) { 
                        term_go.add(accession.substring(0,accession.indexOf(".")));
                        term_go_info.add(str);             
                   } else if (!accession.isEmpty()) {
                       term_go.add(accession);
                        term_go_info.add(str);             
                   }
               }
               if (str.startsWith("qstrand")||str.contains("subject_accession")) {
                   
                   String [] data=str.split("\t"); 
                   for (int i=0; i<data.length;i++ ){
                       if (data[i].contentEquals("subject_accession")) index_accession=i;
                   }
               }
           }            
       }
        return true;
    }
    
    @Override
    public void init_createInput() {
    }
      
     @Override
    public boolean do_run() throws Exception {
            return this.RunNormalWebService();
    }

  public boolean RunNormalWebService() {
    if (term_go.size()==0) {
         setStatus(status_error,"No term found to search for Gene Onthology...");
        return false;
    }
      
    setStatus(status_running,"Running "+properties.getName());
    Results result=new Results();
    result.setName("QuickGO for: "+infofile+" - "+Util.returnCurrentDateAndTime());
    StringBuilder tmp=new StringBuilder();
    int index=0;
    for (String go:this.term_go) {
    // Split sequences in multipleBlast here
    try {
         setStatus(status_running,"Searching Gene Onthology for : "+go);
         URL u=new URL("http://www.ebi.ac.uk/QuickGO/GAnnotation?protein="+go+"&format=tsv");
         setStatus(status_running,u.toString());
        // Connect
        HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
        // Get data
        BufferedReader rd=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        // Read data
        tmp.append("SEARCHING FOR :\t"+go+"\n");
        if (index<this.term_go_info.size()) {
             tmp.append(term_go_info.get(index)+"\n");
        }
        tmp.append(u.toString()+"\n");
        tmp.append(rd.readLine()+"\n");
        String line="";
        int count =0;
        while ((line=rd.readLine())!=null) {     
            tmp.append(line+"\n");
            count++;
        }
        setStatus(status_running,"Total:\t"+count);
        tmp.append("Total:\t"+count+"\n");
        // close input when finished
        rd.close();
        // Write out the unique terms
      
       } catch(Exception e) {
           //--Save current
          setStatus(status_running,"Error with :"+go);           
       }
        index++;
    }
    //--Save result
    result.setText(tmp.toString());
    result.setNote("");
    result.saveToDatabase();
    this.addOutput(result);
    properties.setOutput(result); 
            
    Util.CleanMemory();
    return true;
 }

  
} //--End class


