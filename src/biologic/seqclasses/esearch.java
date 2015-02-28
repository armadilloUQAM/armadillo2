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
//Etienne Lord 2009
// Find in a database Primary ID associated with a term
// Info at http://www.ncbi.nlm.nih.gov/entrez/query/static/esearch_help.html
// WARNING : Blocking class
// NOTE: Run in a thread
//
// Current databases include
//
// db=[[einfo database: http://www.ncbi.nlm.nih.gov/entrez/eutils/einfo.fcgi?]
// db=pubmed,protein,nucleotide,nuccore,nucgss,nucest,structure,genome,books,cancerchromosomes,cdd,gap,domains,gene,genomeprj,gensat,geo,gds,homologene,journals,mesh
//    ncbisearch,nlmcatalog,omia,omim,pmc,popset,probe,proteinclusters,pcassay,pccompound,pcsubstance,snp,taxonomy,toolkit,unigene,unists
// field=[PubMed fields: affl, auth, ecno, jour, iss, mesh, majr, mhda, page, pdat, ptyp, si, subs, subh, tiab, word, titl, lang, uid, fltr, vol]
// reldate= Limit items a number of days immediately preceding today's date.
// mindate=2002/01/01
// maxdate=2001
// retmode=xml
// rettype=count OR uilist (default)
//  sort=
//  PubMed values:
//  author, last+author, journal, pub+date
//  Gene values:  Weight, Name

import configuration.Config;
import java.io.*;
import java.net.*;


public class esearch {
    public static Config config=new Config();
    private BufferedReader in_connection;
    private boolean in_connection_open = false;
    private PrintWriter output;
    private boolean output_open = false;
    public static boolean force_download = true; //By default, do not download a file if it exist EXCEPTION: default_ouput
    URL url;
    private String term="";
    private String tool="esearchdevapplet";   				//ID for the tool required for development
    private String email=config.get("email");              //required if there is a problem
    private String rettype="xml";
    public static String database="nucleotide";
    private String max_result;
    private String composite_url="";
    static String default_database = "pubmed";
    static String default_max_result = "50000";
    static String default_output="esearch.txt";
    final static String default_url="http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?";
    static String default_relentrezdate = "720"; 				//Limit items a number of days immediately preceding today's date.
    static String default_minentrezdate = "";
    static String default_maxentrezdate = "";
    // For the Thread version
    private int status=0;           //status code
    private long timerunning=0;     //time running
    Thread esummary_thread;
    // status code
    public final static int status_done=100;
    public final static int status_running=200;
    public final static int status_running_query_done=201;
    public final static int status_error=999;
    private boolean debug=false;


	private boolean inBody=false;

    public esearch(String search_term) {
    	
    	 max_result=new String(default_max_result);
         search_ncbi(search_term);
         write_to_file(default_output);
    }
    
    /**
     * Thread VERSION!!!
     * @param search_term
     * @param t dummy not used
     */
    public esearch(String search_term, boolean b) {
    	 
    	 max_result=new String(default_max_result);
         runthread(search_term, default_output);

    }

 	public esearch(String search_term, String filename) {
    
    	 database=new String(default_database);
    	 max_result=new String(default_max_result);
         if (!force_download&&FileExists(filename)) {
              Config.log("ESearch file exists: file not downloaded (use force_download) : "+filename);
            } else {
              search_ncbi(search_term);
       	      write_to_file(filename);
            }
        }

	public esearch(String[] id, String path) {
	
        database=new String(default_database);
        max_result=new String(default_max_result);
        if (id.length>0) {
            for (int i=0;i<id.length;i++) {
                      if (!force_download&&FileExists(path+"\\"+id[i])) {
                        Config.log("ESearch file exists: file not downloaded (use force_download) : "+path+"\\"+id[i]);
                       } else {
                          search_ncbi(id[i]);
                          write_to_file(path+"\\"+id[i]);
                       }
            } //End for
        } //End if length>0
	}

 	public boolean esearch(String search_term, String filename) {
           //default pubmed database;
           this.database=new String(default_database);
           if (!force_download&&FileExists(filename)) {
              Config.log("ESearch file exists: file not downloaded (use force_download) : "+filename);
            } else {
   	       search_ncbi(search_term);
	       return write_to_file(filename);
	    }
            return false;
            }

       public boolean esearch(String search_term, String filename, String database) {
	 this.database=new String(database);
         if (!force_download&&FileExists(filename)) {
              Config.log("ESearch file exists: file not downloaded (use force_download) : "+filename);
            } else {
              search_ncbi(search_term);
	      return write_to_file(filename);
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
		composite_url=default_url+"db="+database+"&term="+search_string+"&rettype="+rettype+"&tool="+tool+"&email="+email+"&retmax="+default_max_result;
		if (email.isEmpty()) {
                    System.out.println("Warning: set email for more than 20 results...");
                }
                try {
                        //--debug System.out.println(composite_url);
			url= new URL(composite_url);
		        Config.log("ESearch for: "+search_term);
		} catch (MalformedURLException e) {Config.log("Error ESearch: URL is not good.");}
		try {
			if (in_connection_open) in_connection.close();
			in_connection = new BufferedReader(new InputStreamReader(url.openStream()));
			if (in_connection!=null) in_connection_open=true;
		} catch (IOException e) {Config.log("Unable to get ESearch for:"+search_term);}
		return search_term;
		}


	private boolean write_to_file (String out_filename) {
		String inputLine;
		Config.log("Search finish. Creating output file: "+out_filename);
		try {
				 if (output_open) output.close();
				 output= new PrintWriter(new FileOutputStream (out_filename));
				 if (output!=null) output_open=true;

				while ((inputLine = in_connection.readLine()) != null) {
			  	           	output.println(inputLine);
				}


		    if (in_connection_open) in_connection.close();
			if (output_open) output.close();
			} catch (Exception e) {Config.log("Error in closing stream");};
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
        timerunning=System.currentTimeMillis();
         new Thread(){

            @Override
             public void run() {
             status=status_running;
             try {
                String search_string=new String(search_term);
                search_string=search_string.trim(); // Remove space at the beginning or end of term
                search_string=search_string.replace(' ','+'); // Replace all space witing search term by + sign
                term = search_string;
                //TODO: complete the composite_url term to include more option
                composite_url=default_url+"db="+database+"&term="+search_string+"&rettype="+rettype+"&tool="+tool+"&email="+email+"&retmax="+default_max_result;
                
                    url= new URL(composite_url);
                     if (debug) Config.log("ESearch for: "+search_term);
                
                    if (in_connection_open) in_connection.close();
                    in_connection = new BufferedReader(new InputStreamReader(url.openStream()));
                    if (in_connection!=null) in_connection_open=true;
                     String inputLine;
                     if (debug) Config.log("Search finish. Creating output file: "+filename);
                     status=status_running_query_done;
                    if (output_open) output.close();
                    output= new PrintWriter(new FileOutputStream (filename, true));
                    if (output!=null) output_open=true;
    				while ((inputLine = in_connection.readLine()) != null) {
			  	           	output.println(inputLine);
        			}
                    if (in_connection_open) in_connection.close();
                    if (output_open) output.close();
                     status=status_done;

                 } catch(Exception e) {e.printStackTrace();status=status_error;}
            }
         }.start(); //start thread
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
        if (timerunning!=0&&esummary_thread!=null) {
            if (esummary_thread.isAlive()) esummary_thread.interrupt();
        }
     } catch(Exception e) {return false;}
     return true;
    }

}
