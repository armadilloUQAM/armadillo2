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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package programs;
import configuration.Config;
import biologic.Alignment;
import biologic.MultipleSequences;
import configuration.Config;
import configuration.Util;
import uk.ac.ebi.webservices.WSMafftClient;
import uk.ac.ebi.webservices.wsmafft.*;
import workflows.workflow_properties;
import program.RunProgram;


public class MafftWebEBI extends RunProgram {
  
    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES

   private String JobId="";
   MultipleSequences multitmp=new MultipleSequences();

    ////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTOR

    public MafftWebEBI(workflow_properties properties) {
        super(properties);
        execute();
   }


     @Override
    public boolean init_checkRequirements() {
         if (config.get("email").equals("")) {
            setStatus(status_BadRequirements,"Error: Please set you email in Preferences->Email");
            return false;
        }
         int multiplesequences_id=properties.getInputID("input_multiplesequences_id");
        JobId=(properties.isSet("JobId")?properties.get("JobId"):"");
         Config.log(String.valueOf(multiplesequences_id));
        if (multiplesequences_id==0&&JobId.equals("")) {
            return false;
        } else if (!JobId.isEmpty()) {
            setStatus(status_running,"Retrieving result for "+JobId);
            return true;
        } else {
           multitmp=new MultipleSequences(multiplesequences_id);
          
           addInput(multitmp);
           if (multitmp.getNbSequence()==0) {
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
                       WSMafftClient client = new WSMafftClient();
                      InputParams param =new InputParams();

                      //--Specific Client variables from properties

                      param.setAsync(true);
                      param.setEmail(config.get("email"));

                      float gapopen=1.23f;
                      float gapextension=0.125f;
                      int  tree_rebuilding=1;
                      int maxiters=0;

                      if (properties.isSet("gop")) gapopen=properties.getFloat("gop");
                      if (properties.isSet("gep")) gapextension=properties.getFloat("gep");
                      if (properties.isSet("tree_rebuilding")) tree_rebuilding=properties.getInt("tree_rebuilding");
                      if (properties.isSet("maxiters")) maxiters=properties.getInt("maxiters");
                      
                      param.setGapopen(gapopen);
                      param.setGepen(gapextension);
                      param.setRetree(tree_rebuilding);
                      param.setMaxiterate(maxiters);
                      if (properties.isSet("matrix")) param.setPair(properties.get("matrix"));
                      String fft=properties.get("fft");
                      if (fft.equals("localpair")) param.setLocalpair(true);
                      if (fft.equals("genafpair")) param.setGenafpair(true);
                      if (fft.equals("globalpair")) param.setGlobalpair(true);
                      param.setClustalout(false);
                      param.setReorder(false);

                      //--Data
                      Data[] input=new Data[1];
                      input[0]=new Data();
                      input[0].setType("sequence");
                      input[0].setContent(multitmp.ouputFastaWithSequenceID());

                       //--Run the Job with 3 retry
                      String wsJobId=client.runApp(param,input);
                      setStatus(status_running,"JobId: "+wsJobId);
                      String wsstatus="PENDING";
                      int retry=0;
                      boolean wsdone=false;
                      while(!wsdone&&retry<3) {
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
                                            Alignment newmulti=new Alignment();
                                            newmulti.loadFromFile(filename);
                                            newmulti.setName(properties.getName()+" ("+configuration.Util.returnCurrentDateAndTime()+")");
                                            newmulti.setNote(filename);
                                            newmulti.saveToDatabase();
                                            addOutput(newmulti);
                                            Util.deleteFile(filename);
                                            properties.put("output_alignment_id", newmulti.getId());
                                        }
                                    }
                                    setStatus(Config.status_done,"");
                                    wsdone=true;
                                    }
                            } //--End DONE
                          } //-- End while

  }

  public void RunRetreiveWebService() throws Exception {

       //--Initialize some variables here
          WSMafftClient client = new WSMafftClient();
          String wsJobId = properties.get("JobId");

          //-- Retrieve the Alignment
          setStatus(Config.status_running,"Retrieving JobID "+wsJobId);
          String wsstatus="PENDING";
          int retry=0;
          boolean wsdone=false;
          while(!wsdone&&retry<3) {
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
                                Alignment newmulti=new Alignment();
                                newmulti.loadFromFile(filename);
                                newmulti.setName(properties.getName()+" ("+configuration.Util.returnCurrentDateAndTime()+")");
                                newmulti.setNote(filename);
                                newmulti.saveToDatabase();
                                //Util.deleteFile(filename);
                                properties.put("output_alignment_id", newmulti.getId());
                            }
                        }
                         setStatus(Config.status_done,"Done");
                         wsdone=true;
                        }
                } //--End Done
              } //-- End while
  }
       
}
