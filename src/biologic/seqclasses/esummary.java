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
//Download a short summary of an id
// Info at http://eutils.ncbi.nlm.nih.gov/entrez/query/static/efetchseq_help.html#SequenceDatabases
// Exemple
//<?xml version="1.0" encoding="ISO-8859-1"?>
//<!DOCTYPE eSummaryResult PUBLIC "-//NLM//DTD eSummaryResult, 11 May 2002//EN" "http://www.ncbi.nlm.nih.gov/entrez/query/DTD/eSummary_041029.dtd">
//<eSummaryResult>
//
//<DocSum>
//<Id>99963902</Id>
//<Item Name="Caption" Type="String">DQ514530</Item>
//<Item Name="Title" Type="String">SARS coronavirus ES260 spike glycoprotein gene, complete cds</Item>
//<Item Name="Extra" Type="String">gi|99963902|gb|DQ514530.1|[99963902]</Item>
//<Item Name="Gi" Type="Integer">99963902</Item>
//<Item Name="CreateDate" Type="String">2007/04/30</Item>
//
//<Item Name="UpdateDate" Type="String">2007/04/30</Item>
//<Item Name="Flags" Type="Integer">0</Item>
//<Item Name="TaxId" Type="Integer">385686</Item>
//<Item Name="Length" Type="Integer">4234</Item>
//<Item Name="Status" Type="String">live</Item>
//<Item Name="ReplacedBy" Type="String"></Item>
//<Item Name="Comment" Type="String"><![CDATA[  ]]></Item>
//</DocSum>
//
//</eSummaryResult>
// can also http://eutils.ncbi.nlm.nih.gov/entrez/query/static/efetchlit_help.html
// db=[einfo database: http://www.ncbi.nlm.nih.gov/entrez/eutils/einfo.fcgi?]
// id=[GI number, MMDB ID (Structure database) OR TAX ID (Taxonomy database)]
//


import biologic.InfoSequence;
import configuration.Config;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.text.DateFormat;


public class esummary {
    public static Config config=new Config();
    private BufferedReader in_connection;
    private boolean in_connection_open = false;
    private PrintWriter output;
    private boolean output_open = false;
    public  boolean success=false;                 //to verify if we have to wait 3 sec
    public static boolean  force_download = false; //By default, do not download a file if it exist EXCEPTION: default_ouput
    URL url;
    private String term="";
    private String tool="esummarydevapplet";   				//ID for the tool required for development
    private String email=config.get("email");  //required if there is a problem
    private String rettype="xml"; //xml
    public static String database="nucleotide";
    private String max_result;
    private String composite_url="";
    static String default_database = "nucleotide";
    static String default_max_result = "500";
    static String default_output="esummary.txt";
    final static String default_url="http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?";
    static String default_relentrezdate = "720"; 				//day relative to Pubmed entr
    // For the Thread version
    private int status=0;           //status code
    private long timerunning=0;     //time running
    private String search="";       //in case we need to restart
    public int retry=0;
    Thread esummary_thread;
    // status code
    public final static int status_done=100;
    public final static int status_running=200;
    public final static int status_running_query_done=201;
    public final static int status_error=999;
    private boolean debug=false;
	private boolean inBody=false;
    public Vector<InfoSequence> data=new Vector<InfoSequence>();

    public esummary(String search_term) {
    	
    	 max_result=new String(default_max_result);
         search_ncbi(search_term);
         write_to_file(default_output);
         status=status_done;
         }

    /**
     * Thread VERSION!!!
     * @param search_term
     * @param t
     */
    public esummary(String search_term, boolean t) {
    	
    	 max_result=new String(default_max_result);
         this.search=search_term;
         //runthread(search_term, "esummary.txt");
         runthread2(search_term);
         }

 	public esummary(String search_term, String filename) {
    	
    	  max_result=new String(default_max_result);
       	  if (!force_download&&FileExists(filename)) {
              Config.log("ESummary file exists: file not downloaded (use force_download) : "+filename);
            } else {
              search_ncbi(search_term);
       	      write_to_file(filename);
             }
        }

	public esummary(String[] id, String path, String database) {
	
    this.database=new String(database);

    max_result=new String(default_max_result);
	if (id.length>0) {
		for (int i=0;i<id.length;i++) {
			 if (!force_download&&FileExists(path+"\\"+id[i])) {
                                Config.log("ESummary file exists: file not downloaded (use force_download) : "+path+"\\"+id[i]);
                        } else {
                                search_ncbi(id[i]);
			        write_to_file(path+"\\"+id[i]);
                        }
		}
	}
	}

 	public boolean esummary(String search_term, String filename) {
	         if (!force_download&&FileExists(filename)) {
                        Config.log("ESummary file exists: file not downloaded (use force_download) : "+filename);
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
		composite_url=default_url+"db="+database+"&id="+search_string+"&rettype="+rettype+"&tool="+tool+"&email="+email;
		try {
			url= new URL(composite_url);
		        if (debug) Config.log("ESummary for: "+search_term);
		} catch (MalformedURLException e) {Config.log("Error ESummary: URL is not good.");}
		try {
			if (in_connection_open) in_connection.close();
			in_connection = new BufferedReader(new InputStreamReader(url.openStream()));
			if (in_connection!=null) in_connection_open=true;
		} catch (IOException e) {Config.log("Unable to get ESummary:"+search_term);}
		return search_term;
		}


	private boolean write_to_file (String out_filename) {
		String inputLine;
		if (debug) Config.log("Search finish. Creating output file: "+out_filename);
		try {
				 if (output_open) output.close();
				 output= new PrintWriter(new FileOutputStream (out_filename, true));
				 if (output!=null) output_open=true;

				while ((inputLine = in_connection.readLine()) != null) {
			  	           	output.println(inputLine);
				}


		    if (in_connection_open) in_connection.close();
			if (output_open) output.close();
			} catch (IOException e) {Config.log("Error in closing stream");}
		success=true;
                return true;
		}

    /**
     * Runthread version
     * @param m
     */
    public void runthread(final String search_term, final String filename) {

    esummary_thread=new Thread(){

            @Override
             public void run() {
             status=status_running;
             try {
                String search_string=new String(search_term);
                search_string=search_string.trim(); // Remove space at the beginning or end of term
                search_string=search_string.replace(' ','+'); // Replace all space witing search term by + sign
                term = search_string;
                //TODO: complete the composite_url term to include more option
                composite_url=default_url+"db="+database+"&id="+search_string+"&rettype="+rettype+"&tool="+tool+"&email="+email;
                
                    url= new URL(composite_url);
                     if (debug) Config.log("ESummary for: "+search_term);
                
                    if (in_connection_open) in_connection.close();
                        in_connection = new BufferedReader(new InputStreamReader(url.openStream()));
                    if (in_connection!=null) in_connection_open=true;
                        String inputLine;
                        if (debug) Config.log("Search finish. Creating output file: "+filename);
                        status=status_running_query_done;
                    if (output_open) output.close();
                        output= new PrintWriter(new FileOutputStream (filename));
                    if (output!=null) output_open=true;
    				while ((inputLine = in_connection.readLine()) != null) {
			  	           	output.println(inputLine);
        			}
                    if (in_connection_open) in_connection.close();
                    if (output_open) output.close();
                     status=status_done;
                 } catch(Exception e) {e.printStackTrace();status=status_error;}
            }
    };
    timerunning=System.currentTimeMillis();
    esummary_thread.start();
    }//End runthread

    /**
     * Runthread version
     * @param m
     */
    public void runthread2(final String search_term) {

    esummary_thread=new Thread(){

            @Override
             public void run() {
             status=status_running;
             try {
                String search_string=new String(search_term);
                search_string=search_string.trim(); // Remove space at the beginning or end of term
                search_string=search_string.replace(' ','+'); // Replace all space witing search term by + sign
                term = search_string;
                //TODO: complete the composite_url term to include more option
                composite_url=default_url+"db="+database+"&id="+search_string+"&rettype="+rettype+"&tool="+tool+"&email="+email;

                    url= new URL(composite_url);
                     if (debug) Config.log("ESummary for: "+search_term);

                    if (in_connection_open) in_connection.close();
                        in_connection = new BufferedReader(new InputStreamReader(url.openStream()));
                    if (in_connection!=null) in_connection_open=true;
                        
                        status=status_running_query_done;
                    if (output_open) output.close();
                        
                    if (output!=null) output_open=true;
                        //We process local variable
                        InfoSequence infos = new InfoSequence();      //Temp holder for InfoSummary
                        String stri;
                     while ((stri = in_connection.readLine()) != null) {
                            int index=0;
                           //numéro accession
                           if ((index=stri.indexOf("Caption"))!=-1) {
                              try {
                                 infos.setAccession(stri.substring(index+23, stri.indexOf("</Item>")));
                              } catch(Exception e){if (debug) e.printStackTrace();}
                              //Config.log("accessNumber :"+accessNumber);
                           }
                           //id
                           if ((index=stri.indexOf("Gi"))!=-1) {
                                  try {
                                  infos.setGi(stri.substring(index+19, stri.indexOf("</Item>")));
                                  } catch(Exception e2) {if (debug) e2.printStackTrace();}
                                  //Config.log("Gi :"+gi);
                               }
                           //description
                           if ((index=stri.indexOf("Title"))!=-1) {
                               try {
                                  infos.setDescription(stri.substring(index+21, stri.indexOf("</Item>")));
                               } catch(Exception e3) {if (debug) e3.printStackTrace();}
                              //Config.log("Title :"+Title);
                           }
                           //longueur sequence
                           if ((index=stri.indexOf("Length"))!=-1) {
                              try {
                               String l=stri.substring(index+23, stri.indexOf("</Item>"));
                               infos.setLen(Integer.valueOf(l));
                              } catch(Exception e4) {if (debug) e4.printStackTrace();}
                              //Config.log("Length :"+Length);
                           }
                           if ((index=stri.indexOf("</DocSum>"))!=-1) {
                               infos.setType(InfoSequence.type_Ncbi);
                               data.add(infos);
                               infos=new InfoSequence();
                           }
  
        			} //end while
                    if (in_connection_open) {
                        if (debug) Config.log("Closing esummary connection.");
                        in_connection.close();
                    }
               
                     status=status_done;
                 } catch(Exception e) {if (debug) e.printStackTrace();status=status_error;}
            }
    };
    timerunning=System.currentTimeMillis();
    esummary_thread.start();
    }//End runthread

		public void setMaxResult(String m) {
		 max_result=m;
		 }

                static boolean FileExists(String filename) {
                  File f = new File(filename);
                  return f.exists();
                 }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    public Vector<InfoSequence> getData() {
        return data;
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

    /**
     * @return the search
     */
    public String getSearch() {
        return search;
    }
               
}
