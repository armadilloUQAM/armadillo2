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
import biologic.Blast;
import biologic.MultipleSequences;
import biologic.MultipleTrees;
import biologic.Sequence;
import biologic.Text;
import biologic.Tree;
import configuration.Config;
import configuration.Util;
import workflows.workflow_properties;
import configuration.Util.*;
import program.RunProgram;
import uk.ac.ebi.webservices.WSNCBIBlastClient;
import uk.ac.ebi.webservices.wsncbiblast.*;

@Deprecated
public class ncbiblastWebEBI extends RunProgram {

    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES

    public static boolean debug=false;
    private String JobId="";
    MultipleSequences multi=new MultipleSequences();

    ////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTOR

    public ncbiblastWebEBI(workflow_properties properties) {
        super(properties);
        execute();
    }

    ////////////////////////////////////////////////////////////////////////////
    /// FUNCTIONS

    @Override
    public boolean init_checkRequirements() {
        if (config.get("email").equals("")) {
            setStatus(status_BadRequirements,"Error: Please set you email in Preferences->Email");
            return false;
        }
        int multiplesequences_id=properties.getInputID("input_multiplesequences_id");
        int sequence_id=properties.getInputID("sequence");
        JobId=(properties.isSet("JobId")?properties.get("JobId"):"");
        if (multiplesequences_id==0&&sequence_id==0&&JobId.equals("")) {
            return false;
        } else if (!JobId.isEmpty()) {
            setStatus(status_running,"Retrieving result for "+JobId);
            return true;           
        } else {
           if (sequence_id!=0) {
            Sequence sequence=new Sequence(sequence_id);           
            multi.add(sequence);
            multi.saveToDatabase();
           } else {
            multi=new MultipleSequences(multiplesequences_id);            
            }
           addInput(multi);
           if (multi.getNbSequence()==0) {
            setStatus(status_BadRequirements,"Error: No Sequences found..");
            return false;
           }
        }
        return true;
    }

    @Override
    public void init_createInput() {
        //--Do notthing
    }

    @Override
    public boolean do_run() throws Exception {
        if (this.JobId.isEmpty()) {
            this.RunNormalWebService();
        } else {
            this.RunRetreiveWebService();
        }
        return true;
    }


  public void RunNormalWebService() throws Exception {
    setStatus(Config.status_running,"Running "+properties.getName());
    //--Initialize some variables here
    WSNCBIBlastClient client = new WSNCBIBlastClient();
    InputParams param =new InputParams();

    //--Specific Client variables from properties
    param.setAsync(true);
    param.setEmail(config.get("email"));
      param.setAlign(0);
      param.setAsync(true);
      param.setEmail(config.get("email"));
      param.setDatabase(properties.get("database"));
      if (properties.isSet("num_descriptions")) param.setNumal(properties.getInt("num_alignments"));
      if (properties.isSet("gapextend")) param.setExtendgap(properties.getInt("gapextend"));
      if (properties.isSet("gapopen")) param.setOpengap(properties.getInt("gapopen"));
      if (properties.isSet("expect")) param.setExp(properties.getFloat("expect"));
      if (properties.isSet("program")) {
          param.setProgram(properties.get("program"));
      } else param.setProgram("blastn");
      if (properties.isSet("filter")) param.setFilter(properties.get("filter"));
      param.setMatrix(properties.get("matrix"));
//      param.set
//      param.setAlign(100);
      Data[] input=new Data[1];
      input[0]=new Data();
      input[0].setType("sequence");
      input[0].setContent(multi.ouputFastaWithSequenceID());
      long MaxBlastTime=0;
      if (properties.isSet("MaxBlastTime")) {
          MaxBlastTime=properties.getLong("MaxBlastTime");
      }

    //--Run the Job with 3 retry
    String wsJobId=client.runApp(param,input);
    setStatus(status_running,"JobId: "+wsJobId);
    String wsstatus="PENDING";
    int retry=0;
    boolean wsdone=false;
     long time_ref=System.currentTimeMillis();
    while(!wsdone&&retry<3) {
        if (MaxBlastTime!=0&&getThisBlastTime(time_ref)>MaxBlastTime) {
            setStatus(Config.status_error,"***Warning. MaxBlastTime elapsed "+MaxBlastTime+" ms - Ending blast...");
            retry=3;
        }
        
        wsstatus = client.checkStatus(wsJobId);
        //--Output information
        setStatus(Config.status_running,"Running "+properties.getName()+" for "+getRunningTime()+" ms status: "+wsstatus+" jobib: "+wsJobId+"\n");
        //--CASE 1: Still computing
       if (wsstatus.equals("RUNNING") || wsstatus.equals("PENDING")) Thread.sleep(10000);
       //--CASE 2: Error
        if (wsstatus.equals("ERROR") || wsstatus.equals("NOT)FOUND")) {
                setStatus(Config.status_error,"***Error with "+properties.getName()+" at "+getRunningTime()+" ms status: "+wsstatus+" jobib: "+wsJobId+"\n");
                wsdone=true;
       }
       //--CASE 3: Done
       if (wsstatus.equals("DONE")) {
           String[] outfiles=client.getResults(wsJobId, null, null);
            if (outfiles==null||outfiles[0]==null) {
                retry++;
                setStatus(Config.status_running,"Warning : No results retreive from EBI Website. Retry "+retry+"\n");
                Thread.sleep(15000);
            } else {
                for (String filename:outfiles) {
                    if (filename!=null) {                       
                        if (filename.endsWith(".txt")) {
                            //--This is a running description with score
                            Blast unknown=new Blast(filename);
                            unknown.setNote("Running information from "+properties.getName());
                            unknown.setRunProgram_id(id);
                            unknown.saveToDatabase();
                            properties.put("output_blast_id", unknown.getId());
                        }
                        Config.log(filename);
                        if (!properties.getBoolean("debug")) Util.deleteFile(filename);
                    }
                }               
                wsdone=true;
                }
        } //--End DONE
       
      } //-- End while
  }

  public void RunRetreiveWebService() throws Exception {
       //--Initialize some variables here
                      WSNCBIBlastClient client = new WSNCBIBlastClient();
                      String wsJobId = properties.get("JobId");

                      //-- Retrieve the Alignment
                      setStatus(Config.status_running,"Retrieving JobID "+wsJobId);
                      String wsstatus="PENDING";
                      int retry=0;
                      boolean wsdone=false;
                       long MaxBlastTime=0;
                        if (properties.isSet("MaxBlastTime")) {
                            MaxBlastTime=properties.getLong("MaxBlastTime");
                        }

                        long time_ref=System.currentTimeMillis();
                      while(!wsdone&&retry<3) {
                          if (MaxBlastTime!=0&&getThisBlastTime(time_ref)>MaxBlastTime) {
                                setStatus(Config.status_error,"***Warning. MaxBlastTime elapsed "+MaxBlastTime+" ms - Ending blast...");
                                retry=3;
                            }
                          
                           wsstatus = client.checkStatus(wsJobId);
                            //--Output information
                            setStatus(Config.status_running,"Running "+properties.getName()+" for "+getRunningTime()+" ms status: "+wsstatus+" jobib: "+wsJobId+"\n");
                            //--CASE 1: Still computing
                           if (wsstatus.equals("RUNNING") || wsstatus.equals("PENDING")) Thread.sleep(10000);
                            //--CASE 2: Error
                           if (wsstatus.equals("ERROR") || wsstatus.equals("NOT)FOUND")) {
                                    setStatus(Config.status_error,"***Error with "+properties.getName()+" at "+getRunningTime()+" ms status: "+wsstatus+" jobib: "+wsJobId+"\n");
                                    wsdone=true;
                           }
                            //--CASE 3: Done
                           if (wsstatus.equals("DONE")) {
                                 String[] outfiles=client.getResults(wsJobId, null, null);
                                if (outfiles==null||outfiles[0]==null) {
                                    retry++;
                                    setStatus(Config.status_running,"Warning : No results retreive from EBI Website. Retry "+retry+"\n");
                                    Thread.sleep(15000);
                                } else {
                                    for (String filename:outfiles) {
                                        if (filename!=null) {
                                            if (filename.endsWith(".aln")) {
                                                Alignment newmulti=new Alignment();
                                                newmulti.loadFromFile(filename);
                                                newmulti.setName(properties.getName()+" ("+configuration.Util.returnCurrentDateAndTime()+")");
                                                newmulti.setNote(filename);
                                                newmulti.saveToDatabase();
                                                properties.put("output_alignment_id", newmulti.getId());
                                                addOutput(newmulti);
                                            }
                                            if (filename.endsWith(".dnd")) {
                                                //--This si the guide tree
                                                 MultipleTrees multi=new MultipleTrees();
                                                 multi.readNewick(filename);
                                                 multi.setMultiplesequences_id(properties.getInputID("input_multiplesequences_id"));
                                                 multi.replaceSequenceIDwithNames();
                                                 multi.setNote(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
                                                 multi.saveToDatabase();
                                                 for (Tree tree:multi.getTree()) properties.put("output_tree_id", tree.getId());
                                                 addOutput(multi);
                                            }
                                            if (filename.endsWith(".txt")) {
                                                //--This is a running description with score
                                               Text unknown=new Text(filename);
                                                unknown.setNote("Running information from "+properties.getName());
                                                unknown.setRunProgram_id(id);
                                                unknown.saveToDatabase();
                                                properties.put("output_text_id", unknown.getId());
                                                addOutput(unknown);
                                            }
                                            if (!properties.getBoolean("debug")) Util.deleteFile(filename);
                                        }
                                    }
                                }
                            } //--End Done
                          } //-- End while
  }

    public long getThisBlastTime(long ref) {
        return System.currentTimeMillis()-ref;
    }

}
