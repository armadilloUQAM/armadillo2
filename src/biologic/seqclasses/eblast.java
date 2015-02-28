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

package biologic.seqclasses;

//
//Etienne Lord 2008
// Blast a file on Ncbi..
//
// Info at http://www.ncbi.nlm.nih.gov/blast/Doc/node5.html
// and
// http://www.ncbi.nlm.nih.gov/blast/Doc/urlapi.html
// Current databases include
//
//# gene
//# genome
//# nucleotide
//#  nuccore
//#  nucest
//#  nucgss
//# protein
//# popset
//# snp
// sequences: nucleotide, protein, popset and genome
// taxonomy
// can also http://eutils.ncbi.nlm.nih.gov/entrez/query/static/efetchlit_help.html
// db=[pubmed|pmc|journals|omim]
//
// TO DO REFORMAT TO THE STANDARD FORM:
// efetch("database", "term", "filename", "outputform");

import biologic.InfoSequence;
import biologic.Sequence;
import biologic.*;
import configuration.Config;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import org.ensembl.datamodel.Gene;
import org.ensembl.driver.CoreDriver;
import org.ensembl.driver.CoreDriverFactory;
import org.ensembl.driver.impl.CoreDriverImpl;
import org.ensembl.registry.Registry;


public class eblast {
    public static Config config=new Config();
    private BufferedReader in_connection;
    private boolean in_connection_open = false;
    private PrintWriter output;
    private boolean output_open = false;
    public static boolean force_download = false; //By default, do not download a file if it exist EXCEPTION: default_ouput
    public  boolean success=false;                 //to verify if we have to wait 3 sec
    URL url;
    private String term="";
    private String tool="efetchdevapplet";   				//ID for the tool required for development
    private String email=config.get("email");  //required if there is a problem
    private String rettype="fasta"; //xml, gb or other depending of the database
    public static String database="nucleotide";
    private String max_result;
    private String composite_url="";
    static String default_database = "nucleotide";
    static String default_max_result = "500";
    static String default_output="efetch.txt";
    final static String default_url="http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?";
    static String default_relentrezdate = ""; 				//day relative to Pubmed entr
    // For the Thread version
    private int status=0;           //status code
    private long timerunning=0;     //time running
    Thread efetch_thread;           //Thread
    public int retry=0;             //Retry
    private InfoSequence info;       //InfoSummary that can be associated
    MultipleSequences nas;         //Sequence from the file
                                    //Note: If we are in the thre,ad version and
                                    // it is a fasta file, we read the file
                                    //in the thread
    // status code
    public final static int status_done=100;
    public final static int status_running=200;
    public final static int status_running_query_done=201;
    public final static int status_error=999;
    //debug
    public static boolean debug=true;
    public int loaded_byte=0;
    public String filename;
    /**
     * Thread efetch
     * @param search_term
     * @param filename
     * @param mode
     */
    public eblast(String search_term, String filename, String mode) {
        
    	 max_result=new String(default_max_result);
         this.filename=filename;
         rettype=mode;
         if (!force_download&&FileExists(filename)) {
               if (debug) Config.log("EFectch file exists: file not downloaded (use force_download) : "+filename);
               this.success=false;
         } else
         if (mode.equals("gb")||mode.equals("fasta")){
             runthread(search_term, filename);
         } else {
             runthreadEnsembl(search_term, mode, filename);
         }
        }
 
    public eblast(String search_term) {
    	 this.filename=default_output;
    	 max_result=new String(default_max_result);
         search_ncbi(search_term);
         write_to_file(default_output);
         }

 	public eblast(String search_term, String filename) {
    	this.filename=filename;
    	 max_result=new String(default_max_result);
       	 if (!force_download&&FileExists(filename)) {
               if (debug) Config.log("EFectch file exists: file not downloaded (use force_download) : "+filename);
             } else {
               search_ncbi(search_term);
       	       write_to_file(filename);
            }
        }
        
//        public efetch(String search_term, String filename, String mode, boolean b) {
//    	 today = new Date();
//    	 max_result=new String(default_max_result);
//         rettype=mode;
//        if (!force_download&&FileExists(filename)) {
//               if (debug) Config.log("EFectch file exists: file not downloaded (use force_download) : "+filename);
//               this.success=false;
//             } else {
//               runthread(search_term, filename);
//            }
//        }
    
    

	public eblast(String[] id, String path) {
	this.filename="Multiple id";
    database=new String(default_database);
    max_result=new String(default_max_result);
	if (id.length>0) {
		for (int i=0;i<id.length;i++) {
			 if (!force_download&&FileExists(path+"\\"+id[i])) {
                           if (debug)  Config.log("EFectch file exists: file not downloaded (use force_download) : "+path+"\\"+id[i]);
                          } else {
                            search_ncbi(id[i]);
                            write_to_file(path+"\\"+id[i]);
                        }
		}
	}
	}

 	public boolean efetch(String search_term, String filename) {
                this.filename=filename;
                if (!force_download&&FileExists(filename)) {
                           if (debug) Config.log("EFectch file exists: file not downloaded (use force_download) : "+filename);
                     } else {
                       search_ncbi(search_term);
	               return  write_to_file(filename);
                     }
                     return false;
	   	}

 	/**
	* create a Bufferedreader that read from the open url
	* The parameter of the search term research can be setup using the
	* various command
	*/

private String search_ncbi(String search_term)  {
		String search_string=new String(search_term);
		search_string=search_string.trim(); // Remove space at the beginning or end of term
		search_string=search_string.replace(' ','+'); // Replace all space witing search term by + sign
		term = search_string;
		//TODO: complete the composite_url term to include more option
		composite_url=default_url+"db="+database+"&id="+search_string+"&rettype="+getRettype()+"&tool="+tool+"&email="+email;
		try {
			url= new URL(composite_url);
		        if (debug) Config.log("EFetch for: "+search_term);
		} catch (MalformedURLException e) {Config.log("Error EFetch: URL is not good.");}
		try {
			if (in_connection_open) in_connection.close();
			in_connection = new BufferedReader(new InputStreamReader(url.openStream()));
			if (in_connection!=null) in_connection_open=true;
		} catch (IOException e) {Config.log("Unable to get ESearch for:"+search_term);}
		return search_term;
		}


	private boolean write_to_file (String out_filename) {
		String inputLine;
		if (debug) Config.log("Search finish. Creating output file: "+out_filename);
		try {
				 if (output_open) output.close();
				 output= new PrintWriter(new FileOutputStream (out_filename));
				 if (output!=null) output_open=true;

				while ((inputLine = in_connection.readLine()) != null) {
			  	           	output.println(inputLine);
				}


		    if (in_connection_open) in_connection.close();
			if (output_open) output.close();
            success=true;
			} catch (IOException e) {Config.log("Error in closing stream");success=false;status=status_error;}
		    return true;
		}

		public void setMaxResult(String m) {
		 max_result=m;
		 }

          boolean FileExists(String filename) {
             File f = new File(filename);
             return f.exists();
          }
    /**
     * Runthread version
     * @param m
     */
    public void runthread(final String search_term, final String filename) {

         efetch_thread= new Thread(){

            @Override
             public void run() {
             status=status_running;
             try {
                String search_string=new String(search_term);
                search_string=search_string.trim(); // Remove space at the beginning or end of term
                search_string=search_string.replace(' ','+'); // Replace all space witing search term by + sign
                term = search_string;
                //TODO: complete the composite_url term to include more option
                composite_url=default_url+"db="+database+"&id="+search_string+"&rettype="+getRettype()+"&tool="+tool+"&email="+email;
                
                    url= new URL(composite_url);
                    if (debug) Config.log("EFetch for: "+term);
                
                    if (in_connection_open) in_connection.close();
                    in_connection = new BufferedReader(new InputStreamReader(url.openStream()));
                    if (in_connection!=null) in_connection_open=true;
                     if (debug) Config.log("Search finish. Creating output file: "+filename);
                     status=status_running_query_done;
                    if (output_open) output.close();
                    output= new PrintWriter(new FileOutputStream (filename));
                    if (output!=null) output_open=true;
    				String inputLine;
                    while ((inputLine = in_connection.readLine()) != null) {
			  	           	loaded_byte+=inputLine.length();
                            output.println(inputLine);
        			}
                    if (in_connection_open) in_connection.close();
                    if (output_open) output.close();
                     if (rettype.equals("fasta")) {
                         //--TO DO :: Done in SwingWorker
                         //nas=new MultipleSequence();
                         //if (debug) Config.log("Parsing fasta "+filename);
                         //nas.readSequenceFromFasta(filename);
                     }
                     status=status_done;
                     success=true;
                 } catch(Exception e) {e.printStackTrace();status=status_error;}
            }
         };
         timerunning=System.currentTimeMillis();
         efetch_thread.start(); //start thread
    }//End runthread
    
    /**
     * Runthread version
     * @param m
     */
    public void runthreadEnsembl(final String search_term, final String database, final String filename) {

         efetch_thread= new Thread(){

            @Override
             public void run() {
             status=status_running;
             try {
                String search_string=new String(search_term);
                search_string=search_string.trim(); // Remove space at the beginning or end of term
                search_string=search_string.replace(' ','+'); // Replace all space witing search term by + sign
                term = search_string;
                //TODO: complete the composite_url term to include more option
                Registry registry;
                CoreDriver coreDriver;

                String ensembldb=database;
              //Wrapper for common name
              if (ensembldb.equalsIgnoreCase("homo sapiens")) ensembldb="human";
                //WARNING LONG STRING...
                if (ensembldb.indexOf("_core")==-1&&ensembldb.indexOf("_vega")==-1&&ensembldb.indexOf("_variation")==-1&&ensembldb.indexOf("_otherfeatures")==-1&&ensembldb.indexOf("_go")==-1&&ensembldb.indexOf("_ancestral")==-1&&ensembldb.indexOf("_website")==-1&&ensembldb.indexOf("_cdna")==-1&&ensembldb.indexOf("_funcgen")==-1) {
                  registry = Registry.createDefaultRegistry();
                  coreDriver = registry.getGroup(ensembldb).getCoreDriver();
                } else {
                    //Failed : we try to access all
                    coreDriver=CoreDriverFactory.createCoreDriver("ensembldb.ensembl.org",5306, ensembldb, "anonymous",null);
                 }
                //coreDriver = registry.getGroup(ensembldb).getCoreDriver();
                  Gene g = coreDriver.getGeneAdaptor().fetch(search_term);
                 
                status=status_running_query_done;
                nas=new MultipleSequences();
                if (g!=null) {
                    Sequence s = new Sequence();
                    s.setAccession(g.getAccessionID());
                    s.setAccession_referee(ensembldb);
                    s.setSequence(g.getSequence().getString());
                    s.setName(g.getDescription()+" "+g.getDisplayName());
                    nas.getSequences().add(s);
                    if (debug) System.out.printf("Found %s matching for %s to %s\n",s.getName(), search_term, filename);
                if (debug) Config.log("Creating "+filename);
                nas.outputFasta(filename);
                success=true;
                }
                status=status_done;
                 } catch(Exception e) {e.printStackTrace();status=status_error;}
            }
         };
         timerunning=System.currentTimeMillis();
         efetch_thread.start(); //start thread
    }//End runthread

   /**
     * @return the status
     */
    public int getStatus() {
        return status;
    } 
    
    /**
     * @return the timerunning
     */
    public long getRunningTime() {
        return System.currentTimeMillis()-timerunning;
    }
    
    

    public boolean KillThread() {
     try {
        if (timerunning!=0&&efetch_thread!=null) {
            if (efetch_thread.isAlive()) efetch_thread.interrupt();
        }
     } catch(Exception e) {return false;}
     return true;
    }

    /**
     * @return the infoSummary associated with this efetch or null
     */
    public InfoSequence getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(InfoSequence info) {
        this.info = info;
    }

    /**
     * @return the rettype
     */
    public String getRettype() {
        return rettype;
    }

    /**
     * @return the loaded byt
     */
    public int getLoaded_Byte() {
        return this.loaded_byte;
    }

    public MultipleSequences getNas() {
        return this.nas;
    }

   
}
