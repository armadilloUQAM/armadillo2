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
import biologic.BlastHit;
import biologic.MultipleSequences;
import biologic.Sequence;
import biologic.seqclasses.blastParser;
import biologic.seqclasses.qblast;
import configuration.Config;
import configuration.Util;
import workflows.workflow_properties;
import configuration.Util.*;
import database.databaseFunction;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import program.RunProgram;


/**
 *
 * @author Etienne Lord
 * @since 2010
 */
public class BlastEBI extends RunProgram {


    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES

   String JobId="";
   String infile="";
   String outfile="";
   String executable="executable\\WSBlastEBI\\BlastClientEBI.jar";
   MultipleSequences multitmp=new MultipleSequences();
   
   
   /////////////////////////////////////////////////////////////////////////////
   /// CONSTANT

    public static String RUNNING="RUNNING";
    public static String FINISHED="FINISHED";
    public static String ERROR="ERROR";
    public static String FAILURE="FAILURE";
   public static String NOT_FOUND="NOT_FOUND";
           
    ////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTOR

    public BlastEBI(workflow_properties properties) {
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
        //--Job id
        //this.JobId=(properties.isSet("JobId")?properties.get("JobId"):"");
        
        int multiplesequences_id=properties.getInputID("multiplesequences");
        int sequence_id=properties.getInputID("sequence");
        int alignment_id=properties.getInputID("alignment");
        //--No sequence, return false;
         if (multiplesequences_id==0&&sequence_id==0&&JobId.equals("")) {
            return false;
        } else if (!JobId.isEmpty()) {
            setStatus(status_running,"Retrieving result for "+JobId);
            return true;           
        } else {       
           multitmp=new MultipleSequences();
           if (sequence_id!=0) {
               Sequence s=new Sequence(sequence_id);
               multitmp.add(s);
               multitmp.setName(s.getName());

           } else if (multiplesequences_id!=0) {
               multitmp=new MultipleSequences(multiplesequences_id);
           } else if (alignment_id!=0) {
                multitmp=new Alignment(alignment_id);
           }           
           
           if (multitmp.getNbSequence()==0) {
            setStatus(status_BadRequirements,"Error: No Sequences found...");
            return false;
           }
           
           //--Output sequence file to blast
           infile=config.temporaryDir()+File.separator+"input"+Util.returnCount()+".fasta";
           outfile=config.temporaryDir()+File.separator+"output"+Util.returnCount();
           multitmp.outputFasta(infile);
        }        
        return true;
    }

    

    @Override
    public String[] init_createCommandLine() {
        return new String[0];
    }
    
      

      @Override
    public boolean do_run() throws Exception {
       
      if (this.JobId.isEmpty()) {
            this.RunNormalWebService();
        } else {
            //this.RunRetreiveWebService();
        }
        
        
        
        
        
//       if (this.JobId.isEmpty()) {
//           this.doBlast();
//       } else {
//            getRunStatus();
//       }
       //this.RunNormalWebService();    
        //this.listInfo();
       return true;
    }

  
 public boolean RunNormalWebService() throws Exception {
     
     
     setStatus(status_running,"Blast on EBI "+multitmp.getNbSequence()+" sequence(s)\n");
      long MaxBlastTime=0;
      if (properties.isSet("MaxBlastTime")) {
          MaxBlastTime=properties.getLong("MaxBlastTime");
      }     
     
            LinkedList<Sequence> to_blast=new LinkedList<Sequence>();
           for (Sequence s:multitmp.getSequences()) to_blast.add(s);
      
        
         Blast result=new Blast();
        result.setName("Blast for "+multitmp.getName());
        result.setNote("Created on "+Util.returnCurrentDateAndTime());
        result.setRunProgram_id(this.getId());
    
      for (int count=0; count<to_blast.size();count++) {
        Sequence s=to_blast.get(count);

        setStatus(status_running,"Sequences to blast:\n"+s.outputFasta()+" ["+(count+1)+"/"+to_blast.size()+"]\n");
         infile=config.temporaryDir()+File.separator+"input"+Util.returnCount()+".fasta";
         Util u=new Util();
         u.open(infile);
         u.println(s.outputFasta());
         u.close();         
         String wsstatus=NOT_FOUND;
           //--Blast
           doBlast();
          int retry=0;
          boolean wsdone=false;
           long time_ref=System.currentTimeMillis();
          while(!wsdone&&retry<3) {
              if (MaxBlastTime!=0&&getThisBlastTime(time_ref)>MaxBlastTime) {
                  setStatus(Config.status_error,"***Warning. MaxBlastTime elapsed "+MaxBlastTime+" ms - Ending blast...");
                  retry=3;
              }

              wsstatus = getRunStatus();
              //--Output information
              setStatus(Config.status_running,"Running "+properties.getName()+" for "+getRunningTime()+" ms status: "+wsstatus+" jobib: "+JobId+"\n");
              //--CASE 1: Still computing
             if (wsstatus.equals(RUNNING)) Thread.sleep(10000);
             //--CASE 2: Error
              if (wsstatus.equals(ERROR) || wsstatus.equals(FAILURE)) {
                      setStatus(Config.status_error,"***Error with "+properties.getName()+" at "+getRunningTime()+" ms status: "+wsstatus+" jobib: "+JobId+"\n");
                      wsdone=true;
             }
             //--CASE 3: Done
             if (wsstatus.equals(FINISHED)) {
                 //--Retreive the result
                  result.setText(result.getText()+getResultOutfile());
            
                      wsdone=true;
                      //}
              } //--End DONE

            } //-- End while
      } //--End each sequence
      result.saveToDatabase();
       this.addOutput(result);
       properties.setOutput(result); 
    return true;
  }

   public long getThisBlastTime(long ref) {
        return System.currentTimeMillis()-ref;
    }

   /**
    * Blast the sequence and return the JobId
    * Note: this is a new way of calling the commandline
    * @return 
    */
   public String doBlast() {
       
           //java -jar <jarFile> --async --email <your@email> [options...] seqFile
           String cmd="java -jar "+executable+" --async --email "+config.get("email");
           if (properties.isSet("database")) {
               cmd+=" --database "+properties.get("database");
           }  
            if (properties.isSet("program")) {
                 cmd+=" --program "+properties.get("program");
            } else {
                 cmd+=" --program blastn";
            }
            if (properties.isSet("matrix")) {
                  cmd+=" --matrix "+properties.get("matrix");
            }
      if (properties.isSet("gapextend")) cmd+=" --gapext "+properties.getInt("gapextend");
      if (properties.isSet("gapopen")) cmd+=" --gapopen "+properties.getInt("gapopen");
           
        
          File f=new File(infile);
            cmd+=" "+f.getAbsolutePath();
            setStatus(status_running, "Executing: "+cmd);
           ArrayList<String> str=Util.runUnixCommand(cmd,Config.currentPath);
            if (str.size()>0) {
                this.JobId=str.get(0);
                if (this.JobId.startsWith("ERROR")) {
                     this.JobId="";
                      setStatus(status_error, "Some error in blasting file...");
                }
                properties.put("JobId", this.JobId);
            }          
            for (String s:str) setStatus(status_running, s);
         
       return this.JobId;
   }
   
    public String getRunStatus() {
       if (!JobId.isEmpty()) {
           ArrayList<String> str=Util.runUnixCommand("java -jar "+executable+" --status --jobid "+JobId,Config.currentPath);
           //--Do better parsing here           
           return str.get(0);
           //--See: http://www.ebi.ac.uk/Tools/webservices/services/sss/ncbi_blast_soap#wsresulttype
           //--RUNNING: the job is currently being processed.
//            FINISHED: job has finished, and the results can then be retrieved.
//            ERROR: an error occurred attempting to get the job status.
//            FAILURE: the job failed.
//            NOT_FOUND: the job cannot be found           
       } else {
        return FAILURE;
       }
    }
    
    public String getResultOutfile() {
         if (!JobId.isEmpty()) {
            ArrayList<String> str=Util.runUnixCommand("java -jar "+executable+" --polljob --outformat out --outfile "+outfile+" --jobid "+JobId,Config.currentPath); 
             for (String s:str) setStatus(status_running, s);
            Blast b=new Blast();
            b.loadFromFile(outfile+".out.txt");
            b.setName("Blast Result (EBI) for "+multitmp.getName());
            b.setNote("Created on "+Util.returnCurrentDateAndTime());  
             b.saveToDatabase();
            this.addOutput(b);
            properties.setOutput(b);
            try {
             BlastHit blast=new BlastHit();            
            
             blastParser bp=new blastParser();
             bp.loadText(b.getText());                                
             blast.setText(bp.toString());             
            blast.setName("BlastHit (EBI) for "+multitmp.getName());
            blast.setNote("Created on "+Util.returnCurrentDateAndTime());            
            blast.setRunProgram_id(id);
            blast.saveToDatabase();
            this.addOutput(blast);
            properties.setOutput(blast);
            return bp.toString();
            } catch(Exception e) {}                        
         }
         return "";
    }
    
    /**
     * Display information 
     */
    public void listInfo() {
        ArrayList<String> str=Util.runUnixCommand("java -jar "+executable,Config.currentPath);
       for (String s:str) setStatus(status_running, s);
    }

  
}
