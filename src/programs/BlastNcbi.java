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
import java.util.ArrayList;
import java.util.LinkedList;
import program.RunProgram;


/**
 *
 * @author Etienne Lord
 * @since 2010
 */
public class BlastNcbi extends RunProgram {


    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES

   String RID="";
   qblast QBLAST;
   MultipleSequences multitmp=new MultipleSequences();

    ////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTOR

    public BlastNcbi(workflow_properties properties) {
        super(properties);
        execute();
    }

    ////////////////////////////////////////////////////////////////////////////
    /// FUNCTIONS
       @Override
    public boolean init_checkRequirements() {
        //RID=(properties.isSet("RID")?properties.get("RID"):"");
        int multiplesequences_id=properties.getInputID("multiplesequences");
        int sequence_id=properties.getInputID("sequence");
        int alignment_id=properties.getInputID("alignment");
        //--No sequence, return false;
        if (multiplesequences_id==0&&sequence_id==0&&alignment_id==0&&RID.equals("")) {
            return false;
        } else {
            RID="";
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
            
           //--verify length < 7500
           for (int i=multitmp.getNbSequence()-1; i>-1; i--) {
               Sequence s=multitmp.getSequences().get(i);
               if (s.getLen()>7500) {
                   setStatus(status_idle,"Warning: Sequence "+s.getName()+" length "+s.getLen() +" > 7500 ... (it will not be blasted)...");
                   multitmp.getSequences().remove(i);
               }
           }
           
           if (multitmp.getNbSequence()==0) {
            setStatus(status_BadRequirements,"Error: No Sequences found...");
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
            return this.RunNormalWebService();
    }

  public boolean RunNormalWebService() {
    setStatus(status_running,"Running "+properties.getName());

    // Split sequences in multipleBlast here
    LinkedList<Sequence> to_blast=new LinkedList<Sequence>();
    for (Sequence s:multitmp.getSequences()) to_blast.add(s);
    //--Clear output_file;
        Util u=new Util(properties.get("outfile"));
        u.close();
     //--Create results...
     //--Note: Multiple id will be associated because we want to keep everythin...
        Blast result=new Blast();
        result.setName("Blast for "+multitmp.getName());
        result.setNote("Created on "+Util.returnCurrentDateAndTime());
        result.setRunProgram_id(this.getId());

   setStatus(status_running,"Blast on NBCI "+multitmp.getNbSequence()+" sequence(s)\n");
   for (int count=0; count<to_blast.size();count++) {
        Sequence s=to_blast.get(count);

                    QBLAST=new qblast();
                    //--Set option here
                       QBLAST.setQUERY_FASTA(s.outputFasta());
                       setStatus(status_running,"Sequences to blast:\n"+s.outputFasta()+" ["+(count+1)+"/"+to_blast.size()+"]\n");
                       String program=properties.get("program");
                       String task=properties.get("task");
                       if (properties.isSet("wordsize")) {
                            QBLAST.setWORD_SIZE(properties.getInt("wordsize"));
                        } else {
                           if (task.equals("megablast"))  QBLAST.setWORD_SIZE(28);
                           if (program.equals("blastn"))  QBLAST.setWORD_SIZE(11);
                           if (program.equals("blastp"))  QBLAST.setWORD_SIZE(3);
                        }
                        if (properties.isSet("num_descriptions")) QBLAST.setDESCRIPTIONS(properties.getInt("num_descriptions"));
                        if (properties.isSet("num_alignments")) QBLAST.setALIGNMENTS(properties.getInt("num_alignments"));
                        if (properties.isSet("penalty")) QBLAST.setNUCL_PENALTY(properties.get("penalty"));
                        if (properties.isSet("reward")) QBLAST.setNUCL_REWARD(properties.get("reward"));
                        if (properties.isSet("gapextend")) QBLAST.setGEP(properties.get("gapextend"));
                        if (properties.isSet("gapopen")) QBLAST.setGOP(properties.get("gapopen"));
                        if (properties.isSet("expect")) QBLAST.setEXPECT(properties.getFloat("expect"));
                        if (properties.isSet("database")) QBLAST.setDATABASE(properties.get("database"));
                      QBLAST.setUNGAPPED_ALIGNMENT(properties.getBoolean("ungapped"));
                        if (properties.isSet("percent_identity")) QBLAST.setPERC_IDENT(properties.getInt("percent_identity"));
                        if (properties.isSet("program")) {
                         
                        if (program.equals("psiblast")) {
                            QBLAST.setPROGRAM("blastn");
                            QBLAST.setSERVICE("psi");
                        } else if (task.equals("megablast")){
                            QBLAST.setMEGABLAST(true);
                            QBLAST.setPROGRAM("blastn");
                            QBLAST.setSERVICE("megablast");                            
                        } else {
                            QBLAST.setPROGRAM(program);
                        }
                       }

                       //--Run the Job with 3 retry                  
                       properties.put("RID", QBLAST.QBlast_PUT());
//                      } else {
//                          QBLAST.setRID(properties.get("RID"));
//                      }
                       int retry_count=3;
                          while(QBLAST.getRID().isEmpty()&&retry_count>0) {
                                  setStatus(this.status_idle,"Unable to BLAST...No RID returned...Retry "+retry_count);
                                  setStatus(this.status_idle,QBLAST.returnMessage());
                                  //--Note, there will be multiple RID
                                 properties.put("RID", QBLAST.QBlast_PUT());
                                 properties.put("RID"+count, QBLAST.QBlast_PUT());
                                 retry_count--;
                          }
                      
                       if (retry_count==0&&QBLAST.getRID().isEmpty()) {
                           setStatus(Config.status_error,"Unable to BLAST...No RID returned...");
//                              setStatus(status_error,"Blast Query: "+QBLAST.getEncoded_put_query());
//                              setStatus(status_error,"<- Web Page Output Below ->");
//                              setStatus(status_error, QBLAST.buffer.toString());
                             // return false;
                           
                       } else {
                              int retry=0;
                              String wsstatus="PENDING";
                              int RTOE=0;
                              boolean wsdone=false;
                                long MaxBlastTime=0;
                                if (properties.isSet("MaxBlastTime")) {
                                    MaxBlastTime=properties.getLong("MaxBlastTime");
                                }

                              long time_ref=System.currentTimeMillis();
                              while(!wsdone&&retry<3) {
                                    wsstatus = QBLAST.QBlast_GET();
                                    RTOE=QBLAST.returnRTOE();
                                     if (MaxBlastTime!=0&&getThisBlastTime(time_ref)>MaxBlastTime) {
                                            setStatus(Config.status_running,"Warning. MaxBlastTime elapsed "+MaxBlastTime+" ms - Ending blast...");
                                            retry=3;
                                        }
                                    
                                    //--Output information
                                    setStatus(status_running,"Running "+properties.getName()+" for "+getRunningTime()+" ms status: "+wsstatus+" RID: "+QBLAST.getRID());
                                    //--CASE 1: Still computing
                                   try {
                                    if (wsstatus.equals(qblast.WAITING) || wsstatus.equals("PENDING")) Thread.sleep((RTOE>3000?RTOE:3000));
                                   } catch(Exception e2) { e2.printStackTrace();}
                                   //--CASE 2: Error
                                    if (wsstatus.equals(qblast.UNKNOWN)) {
                                            //--Error...
                                            setStatus(status_running,"***Error with "+properties.getName()+" at "+getRunningTime()+" ms status: "+wsstatus+" RID: "+QBLAST.getRID());
                                            wsdone=true;
                                   }
                                   //--CASE 3: Done
                                   if (wsstatus.equals(qblast.READY)) {                            
                                       result.appendText(QBLAST.getResult());
                                       result.saveToDatabase();
                                       properties.put("output_blast_id", result.getId());
                                       setStatus(status_running,"[Done with sequence "+s.getName()+"]");
                                       if (properties.isSet("outfile")) {
                                           QBLAST.appendToFile(properties.get("outfile"));
                                           result.setFilename(properties.get("outfile"));
                                       }
                                       wsdone=true;
                                      } //--End ready?                                                          
                                     } //--while not done...
                                        if (retry ==3) {
                                              setStatus(Config.status_error,"Number of retry > 3 : Unable to BLAST");                                
                                              return false;
                                          } 
                                  } //--We have RID
                             
                            } //-- End while (we have sequence)
                            
                                  
   
                           //--Parse the result
                            try {
                                blastParser bp=new blastParser();
                                bp.loadText(result.getText());
                                BlastHit b2=new BlastHit();
                                b2.setText(bp.toString());
                                b2.setName("BlastHit for "+multitmp.getName());
                                b2.setNote("Created on "+Util.returnCurrentDateAndTime());
                                b2.saveToDatabase();
                                properties.put("output_blasthit_id", b2.getId());
                              } catch(Exception e) {e.printStackTrace();}

                            setStatus(status_done,"");
                             
                      return true;

  }

   public long getThisBlastTime(long ref) {
        return System.currentTimeMillis()-ref;
    }
  
}
