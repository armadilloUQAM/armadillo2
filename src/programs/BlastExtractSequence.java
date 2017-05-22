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
import configuration.Config;
import biologic.Blast;
import biologic.BlastHit;
import biologic.InfoSequence;
import biologic.MultipleSequences;
import biologic.Sequence;
import biologic.seqclasses.Downloader;
import biologic.seqclasses.blastParser;
import biologic.seqclasses.efetch;
import biologic.seqclasses.qblast;
import configuration.Util;
import workflows.workflow_properties;
import configuration.Util.*;
import java.io.File;
import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import program.RunProgram;

/**
 *
 * @author Etienne Lord
 * @since 2010
 */
public class BlastExtractSequence extends RunProgram {


    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES
    blastParser blast=new blastParser();
    MultipleSequences output=new MultipleSequences();
   
    ////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTOR

    public BlastExtractSequence(workflow_properties properties) {
        super(properties);
        execute();
    }

    ////////////////////////////////////////////////////////////////////////////
    /// FUNCTIONS
       @Override
    public boolean init_checkRequirements() {
        //RID=(properties.isSet("RID")?properties.get("RID"):"");
        int blast_id=properties.getInputID("blast");        
        if (blast_id==0) {
            setStatus(this.status_BadRequirements,"No blast to extract");
            return false;
        }
            return true;
    }


      @Override
    public void init_createInput() {
        int blast_id=properties.getInputID("blast");
        if (blast_id>0) {
            Blast bh=new Blast(blast_id);
            blast.loadText(bh.getText());
        }
    }

     @Override
    public boolean do_run() throws Exception {
            this.RunNormalWebService();
        return true;
    }

  public void RunNormalWebService() throws Exception {
    setStatus(status_running,"Running "+properties.getName());
    ////////////////////////////////////////////////////////////////////////////
    //--Configuration
     efetch.force_download=properties.getBoolean("force_download");
    ////////////////////////////////////////////////////////////////////////////
     //--Variable to handle the download
    LinkedList<InfoSequence>queue=new LinkedList<InfoSequence>();
    efetch fasta=null;
    efetch genbank=null;
    Vector<efetch> download=new Vector<efetch>();
    final int maxload=5; //Max number of loading thread
    InfoSequence search=null; //search item
    int downloaded=0;        //numnber of downloaded item
    LinkedList<efetch> toload=new LinkedList<efetch>();
    boolean finish=false;
    MultipleSequences multi=new MultipleSequences();
    ////////////////////////////////////////////////////////////////////////////
     //--Look for selection :: For_Accession
    String accession="";
    Pattern key_value=Pattern.compile("For_(.*)", Pattern.CASE_INSENSITIVE);
         //--Handle program for loop
         BlastHit b;      
         Downloader downloader=new Downloader();
         //--Found selected object?         
              for (Object k:properties.keySet()) {
                 Matcher m=key_value.matcher((String)k);
                 if (m.find()) {
                    String key=properties.get(k);
                    String value=m.group(1);
                    accession+=value+",";
                 }
             }
        //--No?
              if (accession.isEmpty()) {
                for (Blast b2:blast.getList()) {
                    for (BlastHit bh:b2.getBlasthit_list()) {
                       accession+=bh.getSubject_accession()+",";
                    }
                }
             }
        //--Download
        
        if (properties.getBoolean("download_best")) {
            msg("Downloading the BEST hits only...");
            accession="";
            for (Blast b2:blast.getList()) {
                    if (b2.getBlasthit_list().size()>0) {
                        accession+=b2.getBlasthit_list().get(0).getSubject_accession()+",";
                    }                    
                }           
        }
        //--esearh
        Config.log("Search Gi for Accession : "+accession);
        if (downloader.eSearch(accession)) {
                    queue.addAll(downloader.readEsearchFile("esearch.txt"));
                    if (properties.getBoolean("DevelopperMode")) {
                        Config.log(Util.toString(Util.InputFile("esearch.txt")));
                    }
                    Util.deleteFile("esearch.txt");
        }
        //--Verify queue->if
       //--Set filename
        for (InfoSequence s:queue) {
            s.setFastaFilename(config.tmpDir()+File.separator+s.getGi()+".fasta");
            s.setGenbankFilename(config.tmpDir() + File.separator + s.getGi() + ".gb");
        }
        for (int i=queue.size()-1; i>-1; i--) {
             InfoSequence s2=queue.get(i);
             s2.findFile();
             if (!efetch.force_download&&s2.isFound()) {
                    msg("File already downloaded "+s2.getFastaFilename());
                    MultipleSequences tmpm=new MultipleSequences();
                    tmpm.loadSequences(s2.getFastaFilename());
                    //--Add the sequence and set a name with accession number
                   for (Sequence S:tmpm.getSequences()) {
                       output.add(S);
                   }
                    queue.remove(i);
                 }
       }
        Config.log("done.");
        
        int total_to_download=queue.size()*2;          //Number of file to download

        while(queue.size()>0||download.size()>0) {
            if (download.size()<maxload&&queue.size()>0) {
                search=queue.pollFirst();
                       Config.log(search.getAccession()+" "+queue.size());
                       Downloader bio=new Downloader();
                       // TO DO HANDLE ENSEMBL HERE!
                       Config.log(search.getDescription()+" "+search.getGi()+" "+search.getAccession());
                       fasta=bio.getFastaFile(search.getGi(), search.getFastaFilename());
                       fasta.setInfo(search);
                       genbank=bio.getGenbankFile(search.getGi(), search.getGenbankFilename() );
                       genbank.setInfo(search);
                       if (properties.getBoolean("download_genbank")) download.add(genbank);
                       download.add(fasta);
            }

             // 2: VERIFICATION DES DIFFERENT OBJECT {
                    Vector<efetch> mark_for_destroy=new Vector<efetch>(); // liste des efetch a detruite
                    for (efetch tmp:download) {
                        //CAS 1: FINI
                        if (tmp.getStatus()==efetch.status_done) {
                                mark_for_destroy.add(tmp);
                                //CASE 1. FASTA
                                if (tmp.filename.endsWith("fasta")) toload.add(tmp);
                                //TO DO HERE ADD gb to database
                           } //end done
                        //CAS 2: Problem?
                        if (tmp.getStatus()==efetch.status_error) {
                            tmp.retry++;
                            if (tmp.retry<6) {
                                InfoSequence info=tmp.getInfo();
                                if (tmp.getRettype().equals("fasta")||tmp.getRettype().equals("gb")) {
                                    tmp.runthread(info.getGi(),(tmp.getRettype().equals("fasta")?info.getFastaFilename():info.getGenbankFilename()));
                                } else {
                                    tmp.runthreadEnsembl(info.getEnsemblid(),info.getEmsembldb(), info.getEnsemblFilename());
                                }
                            } else {
                                //WE FORGET IT AND THERE WILL BE AN ERRO
                                //publish("Unable to load"+tmp.getInfo().getDescription());
                                mark_for_destroy.add(tmp);
                            }
                        }
                        //CAS 3: Running, too long
                        if (tmp.getStatus()==efetch.status_running||tmp.getStatus()==efetch.status_running_query_done) {
                            // 10 sec running? -> Print a message
                            if (tmp.getRunningTime()%10000==0) {
                                String acc=tmp.filename;
                                String msg="Still searching for "+ acc+". Searching for [ "+(tmp.getRunningTime()/1000)+" sec ] ["+(tmp.getLoaded_Byte()/Util.Mb)+" megabytes ]";
                                msg(msg);
                                properties.put("StatusProgress",((downloaded)*100/total_to_download));
                            }
                            // 12 Min running?-> Restart thread
                            if (tmp.getRunningTime()>720000) {
                                tmp.retry++;
                            if (tmp.retry<3) {
                                InfoSequence info=tmp.getInfo();
                                if (tmp.getRettype().equals("fasta")||tmp.getRettype().equals("gb")) {
                                    tmp.runthread(info.getGi(),(tmp.getRettype().equals("fasta")?info.getFastaFilename():info.getGenbankFilename()));
                                } else {
                                    tmp.runthreadEnsembl(info.getEnsemblid(),info.getEmsembldb(), info.getEnsemblFilename());
                                }
                            } else {
                                //WE FORGET IT AND THERE WILL BE AN ERRO
                                //publish(tmp.getInfo());
                                mark_for_destroy.add(tmp);
                            }
                            } //End time too long
                        }
                    } //End check each thread
                    // 3. UPDATE THE PROGRESS AND DESTROY THE FINISHED THREAD
                    if (mark_for_destroy.size()>0) {
                        downloaded+=mark_for_destroy.size();
                        properties.put("StatusProgress",((downloaded)*100/total_to_download));
                        Util.CleanMemory(); //--Remove extra memory...
                    }
                    for (efetch tmp:mark_for_destroy) {
                        download.remove(tmp);
                    } //End destroy thread

            } //End while not cancelled
            //--Load sequence
            while (toload.size()>0) {
                    efetch tmp=toload.poll();
                    Config.log("Loading into database "+tmp.filename);
                    //--Here we load and add directly to database
                    //CASE 1.1 Are we able to load the sequence
                   long timestat=System.currentTimeMillis();
                   MultipleSequences tmpm=new MultipleSequences();
                   tmpm.loadSequences(tmp.getInfo().getFastaFilename());
                   //--Add the sequence and set a name with accession number
                   for (Sequence S:tmpm.getSequences()) {                       
                       output.add(S);
                   }
                    //int sequence_id=LoadSequence.readSequenceFromFastaAndAddToDB(tmp.getInfo().getFastaFilename(), MultipleSequenceId, groupName,Util.returnCurrentDateAndTime());
                    if (tmpm.getNbSequence()!=0) {
                           Config.log("Done loading in "+(System.currentTimeMillis()-timestat)+" ms");
                    } else {
                    //CASE 1.2. Error
                        Config.log("Error loading from "+tmp.getInfo().getFastaFilename());
                    }
                    Util.CleanMemory();
                    total_to_download--;
                    properties.put("StatusProgress",((downloaded)*100/total_to_download));
                    //setProgress((downloaded+(toload_size_start-toload.size()))*100/total_to_download);
               } //--End loading
               properties.remove("StatusProgress");
  }

    @Override
    public void post_parseOutput() {
         if (output.getNbSequence()>0) {
            output.setName("Blast sequences from "+blast.getQueryname()+" downloaded on "+Util.returnCurrentDateAndTime());
            output.setNote("Downloaded on "+Util.returnCurrentDateAndTime());
            output.saveToDatabase();
            properties.put("output_multiplesequences_id", output.getId());
        } else {
            setStatus(status_error,"No sequence found");
        }
    }



}
