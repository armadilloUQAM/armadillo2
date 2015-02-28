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
// Etienne Lord 2010
// A class to query the NCBI eutils tools
// http://eutils.ncbi.nlm.nih.gov/
//
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
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.regexp.RE;


public class eutil {
    public static Config config=new Config();

    ////////////////////////////////////////////////////////////////////////////
    /// Internal vatiables
    
    public StringBuilder buffer=new StringBuilder(); //--Return_buffer from the last Qblast
    private BufferedReader in_connection;
    private boolean in_connection_open = false;
    private PrintWriter output;
    private boolean output_open = false;
    private boolean force_download = true; //By default, do not download a file if it exist EXCEPTION: default_ouput
    private String composite_url="";
    URL url;
    // For the Thread version
    private int status=0;           //status code
    private long timerunning=0;     //time running
    Thread esummary_thread;

    ////////////////////////////////////////////////////////////////////////////
    /// Constant

    final static String default_url="http://eutils.ncbi.nlm.nih.gov/entrez/eutils/"; //add the good eutils here...
    private String tool="esearchdevapplet";   				//ID for the tool required for development
    private String email=(config.isSet("email")?config.get("email"):"anonymous");              //required if there is a problem
    public static Pattern pCount=Pattern.compile("<Count>(.*)</Count>");
    public static Pattern pQueryKey=Pattern.compile("<QueryKey>(.*)</QueryKey>");
    public static Pattern pWebEnv=Pattern.compile("<WebEnv>(.*)</WebEnv>");
    public static Pattern pRetMax=Pattern.compile("<RetMax>(.*)</RetMax>");
    public static Pattern pRetStart=Pattern.compile("<RetStart>(.*)</RetStart>");
    public static Pattern pID=Pattern.compile("<Id>(.*)</Id>");
    public static Pattern pDbFrom=Pattern.compile("<DbFrom>(.*)</DbFrom>");    
    public static Pattern pDbTo=Pattern.compile("<DbTo>(.*)</DbTo>");


    ////////////////////////////////////////////////////////////////////////////
    /// Variables

    private String retmode=""; //    retmode=output format : xml (not journals), html, text, asn.1 (not journals)
    private String rettype=""; //    uilist,abstract (not omim), citation (not omim), medline (not omim), full  (journals and omim), xml, fasta, gb, xml
    private String database="nucleotide";
    private String databaseFrom="";
    private boolean useHistory=false;
    private String WebEnv="";
    private String Query_key="";
    private String Engine="esearch";
    private String term="";
    private String id="";
    private boolean Custom=false; //--If we are in custom mode, we use the term variable as the default url, see below.
    private boolean outputToDisk=false; //--Setting to true will bypass saving to buffer : Note: valid only if execute(filename) is called, otherwise no result
    private String filename="";

    ////////////////////////////////////////////////////////////////////////////
    /// Limit variables
    
    private String retmax = "50000";                            //--Default, 500000
    private String retstart="";                                 //--Where we start
    private String entrezdate = ""; 				//Limit items a number of days immediately preceding today's date.
    private String minentrezdate = "";
    private String maxentrezdate = "";
    private String sort="";                                    //--Sort for ESummary and EFetch. like: author, last+author, journal, pub+date

    ////////////////////////////////////////////////////////////////////////////
    /// Sequences variables
    private String PLUS="1";
    private String MINUS="2";

    private String strand="";     //(1 = plus or 2 = minus)
    private String seq_start="";
    private String seq_stop="";

    ////////////////////////////////////////////////////////////////////////////
    /// Constructor


    public eutil() {}

    ////////////////////////////////////////////////////////////////////////////
    /// Execute the eutil query on Ncbi

    /**
     * Execute the eUtils query
     * @return a String representing the eUtil results
     */
    public String execute() {
           return request_ncbi();
    }

    /**
     * Execute the eUtils query and save to filename
     * @param filename
     */
    public void execute(String filename) {
             this.filename=filename;
             request_ncbi();
             if (!this.outputToDisk) {
                 saveToFile(filename);
             }
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Internal functions

 	/**
	* create a Bufferedreader that read from the open url
	* The parameter of the search term research can be setup using the
	* various command
	*/

        private String request_ncbi()  {
                  buffer=new StringBuilder();
                //--1. Construct the search string here
                String start_url=default_url+this.getEngine().toLowerCase()+".fcgi?";
                //--2. Create the default url
                if (Custom) {
                     composite_url=start_url+this.getTerm()+"&tool="+tool+"&email="+email;
                } else {
                    composite_url=start_url+"db="+getDatabase()+"&tool="+tool+"&email="+email;
                    
                }

//                else {
//                    composite_url=start_url+"db="+getDatabase()+"&tool="+tool+"&email="+email;
//                }
                //--
                if (!this.getTerm().isEmpty()) composite_url+="&term="+this.getTerm();
                if (!this.getId().isEmpty()) composite_url+="&id="+this.getId();
                //--3. Add some options here
                //--Rettype (see above)
                if (!this.getRettype().isEmpty()) {
                    composite_url+="&rettype="+getRettype();
                }
                 //--Retmode (see above)
                if (!this.getRetmode().isEmpty()) {
                    composite_url+="&retmode="+getRetmode();
                }
                //--WebEnv
                if (!WebEnv.isEmpty()) {
                    composite_url+="&WebEnv="+this.getWebEnv();
                }
                //--Query_key
                if (!Query_key.isEmpty()) {
                    composite_url+="&query_key="+this.getQuery_key().toLowerCase();
                }
                //--Use History
                if (this.useHistory) {
                    composite_url+="&usehistory=y";
                }
                //--MaxResult
                if (!this.retmax.isEmpty()) {
                    composite_url+="&retmax="+this.getRetmax();
                }
                if (!this.getRetstart().isEmpty()) {
                    composite_url+="&retstart="+this.getRetstart();
                }
                if (!this.getRelEntrezdate().isEmpty()) {
                    composite_url+="&reldate="+getRelEntrezdate();
                }
                if (!this.getMinentrezdate().isEmpty()) {
                    composite_url+="&mindate="+this.getMinentrezdate();
                }
                if (!this.getMaxentrezdate().isEmpty()) {
                    composite_url+="&maxdate="+this.getMaxentrezdate();
                }
                  if (!this.getSort().isEmpty()) {
                    composite_url+="&sort="+this.getSort();
                }
                  if (!this.getStrand().isEmpty()) {
                    composite_url+="&strand="+this.getStrand();
                }
                  if (!this.getSeq_start().isEmpty()) {
                    composite_url+="&seq_start="+this.getSeq_start();
                }
                  if (!this.getSeq_stop().isEmpty()) {
                    composite_url+="&seq_stop="+this.getSeq_stop();
                }
                //--Rettype (see above)
                if (!this.getDatabaseFrom().isEmpty()) {
                    composite_url+="&dbfrom="+this.getDatabaseFrom();
                }
                //--4. Do the work
		try {
			url= new URL(composite_url);
		        //Config.log("ESearch for: "+search_term);
		} catch (MalformedURLException e) {Config.log("Error "+this.getEngine()+": URL is not good.\n"+composite_url);}
		try {
			if (in_connection_open) in_connection.close();
			in_connection = new BufferedReader(new InputStreamReader(url.openStream()));
			if (in_connection!=null) in_connection_open=true;
                        String inputLine="";
                        if (outputToDisk) output=new PrintWriter(new FileWriter(new File(filename)));
                        while ((inputLine = in_connection.readLine()) != null) {
			  	if (!this.outputToDisk) {
                                    buffer.append(inputLine+"\n");
                                } else {
                                    output.println(inputLine);
                                    //--Special case when we search...
                                    if (this.getEngine().equalsIgnoreCase("esearch")) {
                                        buffer.append(inputLine+"\n");
                                    }

                                }
                        }
                        if (outputToDisk) {
                            output.flush();
                            output.close();
                        }
		} catch (IOException e) {Config.log("Unable to get EUtils from Ncbi..."+composite_url);}
                    return buffer.toString();
		}
		
                 public String getResult() {
                    return buffer.toString();
                }

                boolean FileExists(String filename) {
                 File f = new File(filename);
                 return f.exists();
                }

////////////////////////////////////////////////////////////////////////////////
/// Regex fucntion to get some variable

      /**
       * Find  the WebEnv
       * @return the WebEnv or empty String if not found
       */
      public String returnWebEnv() {
        if (buffer.length()==0) return "";
         String tmp_WebEnv="";
         Matcher m = pWebEnv.matcher(buffer.toString());
         if (m.find()) {
             tmp_WebEnv = m.group(1);
         }       
        return tmp_WebEnv;
    }

       /**
       * Find  the WebEnv
       * @return the WebEnv or empty String if not found
       */
      public String returnWebEnv(String buffer) {
        if (buffer.length()==0) return "";
         String tmp_WebEnv="";
         Matcher m = pWebEnv.matcher(buffer);
         if (m.find()) {
             tmp_WebEnv = m.group(1);
         }
        return tmp_WebEnv;
    }

        /**
       * Find the QueryKey
       * @return the QueryKey or empty String if not found
       */
      public String returnQueryKey() {
        if (buffer.length()==0) return "";
        String tmp_QueryKey="";        
         Matcher m = pQueryKey.matcher(buffer.toString());
         if (m.find()) {
             tmp_QueryKey = m.group(1);
         }
        
        return tmp_QueryKey;
    }

       /**
       * Find the QueryKey
       * @return the QueryKey or empty String if not found
       */
      public String returnQueryKey(String buffer) {
        if (buffer.length()==0) return "";
        String tmp_QueryKey="";
         Matcher m = pQueryKey.matcher(buffer);
         if (m.find()) {
             tmp_QueryKey = m.group(1);
         }

        return tmp_QueryKey;
    }
       /**
       * Find the Total from the query result
       * @return the count
       */
      public int returnCount() {
        if (buffer.length()==0) return 0;
        String tmp_Count="";        
         Matcher m = pCount.matcher(buffer.toString());
         if (m.find()) {
             tmp_Count = m.group(1);
         }        
        try {
            return Integer.valueOf(tmp_Count);
        } catch(Exception e) {return 0;}
       }

       /**
       * Find the Total from the query result
       * @return the count
       */
      public int returnCount(String buffer) {
        if (buffer.length()==0) return 0;
        String tmp_Count="";        
         Matcher m = pCount.matcher(buffer);
         if (m.find()) {
             tmp_Count = m.group(1);
         }        
        try {
            return Integer.valueOf(tmp_Count);
        } catch(Exception e) {return 0;}
       }

        /**
       * Find the RetMax from the query result
       * @return the count
       */
      public int returnRetMax() {
        if (buffer.length()==0) return 0;
        String tmp_RetMax="";        
         Matcher m = pRetMax.matcher(buffer.toString());
         if (m.find()) {
             tmp_RetMax = m.group(1);
         }        
        try {
            return Integer.valueOf(tmp_RetMax);
        } catch(Exception e) {return 0;}
       }


       /**
       * Find the RetMax from the query result
       * @return the count
       */
      public int returnRetMax(String buffer) {
        if (buffer.length()==0) return 0;
        String tmp_RetMax="";
         Matcher m = pRetMax.matcher(buffer);
         if (m.find()) {
             tmp_RetMax = m.group(1);
         }
        try {
            return Integer.valueOf(tmp_RetMax);
        } catch(Exception e) {return 0;}
       }

        /**
       * Find the RetStart from the query result
       * @return the count
       */
      public int returnRetStart() {
        if (buffer.length()==0) return 0;
        String tmp_RetStart="";        
         Matcher m = pRetStart.matcher(buffer.toString());
         if (m.find()) {
             tmp_RetStart = m.group(1);
         }        
        try {
            return Integer.valueOf(tmp_RetStart);
        } catch(Exception e) {return 0;}
       }

      /**
       * Find the RetStart from the query result
       * @return the count
       */
      public int returnRetStart(String buffer) {
        if (buffer.length()==0) return 0;
        String tmp_RetStart="";        
         Matcher m = pRetStart.matcher(buffer);
         if (m.find()) {
             tmp_RetStart = m.group(1);
         }        
        try {
            return Integer.valueOf(tmp_RetStart);
        } catch(Exception e) {return 0;}
       }

      /**
       * This will return an ID list found in the results of a query
       * @return a Vector of String containing the ID.
       */
      public Vector<String>returnIDList() {
          Vector<String>tmp=new Vector<String>();
          Matcher m = pID.matcher(buffer.toString());
          Matcher DbFrom = pDbFrom.matcher(buffer.toString());
          
          //Case One, linked db -> Extract only the new link set
          if (DbFrom.find()) {
            Matcher DbTo = pDbTo.matcher(buffer.toString());
              if (DbTo.find()) {
                  m=pID.matcher(buffer.toString().substring(DbTo.end()));
              }
          }
          while (m.find()) {
             tmp.add(m.group(1));
          }
          return tmp;
      }

      /**
       * This will return an ID list found in the results of a query
       * Note: WArning, check for DbFlag (DBFrom)
       * @return a Vector of String containing the ID.
       */
      public Vector<String>returnIDList(String buffer) {
        Vector<String>tmp=new Vector<String>();
          Matcher m = pID.matcher(buffer);
          Matcher DbFrom = pDbFrom.matcher(buffer);

          //Case One, linked db -> Extract only the new link set
          if (DbFrom.find()) {
              Matcher DbTo = pDbTo.matcher(buffer);
              if (DbTo.find()) {                  
                  //--We start only from the new beginning (Match DbTo)
                  buffer=buffer.substring(DbTo.end());               
                  m=pID.matcher(buffer);
              }
          } 
          while (m.find()) {
             tmp.add(m.group(1));
          }
          return tmp;
      }

      /**
       * This will set in the term variable a list of ID in batch
       * @param ID
       */
      public void setIDList(Vector<String>ID) {
          String tmp=""; //--We add the &id= in the execute phase
          for (String stri:ID) {
              tmp+=stri+",";
          }
          //--remove last ,
          if (tmp.endsWith(",")) tmp=tmp.substring(0, tmp.length()-1);
          this.setId(tmp);
      }

       /**
       * This will set in the term variable a list of ID
       * @param ID
       */
      public void setIndividualIDList(Vector<String>ID) {
            String tmp="";//--We add the &id= in the execute phase
          for (String stri:ID) {
              tmp+=stri+",&id=";
          }
          //--remove last ,
          if (tmp.endsWith(",&id=")) tmp=tmp.substring(0, tmp.length()-5);
          this.setId(tmp);
      }



    @Override
    public String toString() {
        String str="URL:"+composite_url+"\n";
        str+="=================================================\n";
        str+="Engine  : "+this.getEngine()+"\n";
        str+="Term    : "+this.getTerm()+"\n";
        str+="ID(s)   : "+this.getId()+"\n";
        str+="Database: "+database+"\n";
        str+="DatabaseFrom (Advanced) : "+databaseFrom+"\n";
        str+="=================================================\n";
        str+="Count   (Total results found)          : "+returnCount()+"\n";
        str+="RetMax  (Number of results retreive)   : "+returnRetMax()+"\n";
        str+="RetStart(Start of results to retreive) : "+returnRetStart()+"\n";
        str+="=================================================\n";
        str+="Retmode : "+retmode+"\n";
        str+="Rettype : "+rettype+"\n";
        str+="Relative Entrez Date : "+this.entrezdate+"\n";
        str+="Minimum Entrez Date  : "+this.minentrezdate+"\n";
        str+="Maximum Entrez Date  : "+this.maxentrezdate+"\n";
        str+="=================================================\n";
        str+="Strand : "+(this.strand.equals(PLUS)?"PLUS":this.strand.equals(MINUS)?"MINUS":"Default")+"\n";
        str+="Start  : "+seq_start+"\n";
        str+="Stop   : "+seq_stop+"\n";
        str+="Sort   : "+sort+"\n";
        str+="=================================================\n";
        str+="Use History   : "+this.useHistory+"\n";
        str+="WebEnv        : "+returnWebEnv()+"\n";
        str+="QueryKey      : "+returnQueryKey()+"\n";
        str+="OutputToDisk  : "+this.outputToDisk+"\n";
        str+="OutputFilename: "+this.filename+"\n";
        return str;
    }

    /**
     * Return a summary of this eutils 
     * @return
     */
    public String toStringResults() {
        String str="URL:"+composite_url+"\n";
        str+="=================================================\n";
        str+="Engine  : "+this.getEngine()+"\n";
        str+="Database: "+database+"\n";
        str+="DatabaseFrom (Advanced) : "+databaseFrom+"\n";
        str+="Term    : "+this.getTerm()+"\n";
        str+="ID(s)   : "+this.getId()+"\n";
        str+="Count   (Total results found)          : "+returnCount()+"\n";
        str+="RetMax  (Number of results retreive)   : "+returnRetMax()+"\n";
        str+="RetStart(Start of results to retreive) : "+returnRetStart()+"\n";
        str+="=================================================\n";
        return str;
    }

//    /**
//     * Runthread version
//     * @param m
//     */
//    public void runthread(final String search_term, final String filename) {
//        timerunning=System.currentTimeMillis();
//         new Thread(){
//
//            @Override
//             public void run() {
//             status=status_running;
//             try {
//                String search_string=new String(search_term);
//                search_string=search_string.trim(); // Remove space at the beginning or end of term
//                search_string=search_string.replace(' ','+'); // Replace all space witing search term by + sign
//                    setTerm(search_string);
//                //TODO: complete the composite_url term to include more option
//                composite_url=default_url+"db="+getDatabase()+"&term="+search_string+"&rettype="+getRettype()+"&tool="+tool+"&email="+email+"&retmax="+default_max_result;
//                    url= new URL(composite_url);
//                     if (debug) Config.log("ESearch for: "+search_term);
//
//                    if (in_connection_open) in_connection.close();
//                    in_connection = new BufferedReader(new InputStreamReader(url.openStream()));
//                    if (in_connection!=null) in_connection_open=true;
//                     String inputLine;
//                     if (debug) Config.log("Search finish. Creating output file: "+filename);
//                     status=status_running_query_done;
//                    if (output_open) output.close();
//                    output= new PrintWriter(new FileOutputStream (filename, true));
//                    if (output!=null) output_open=true;
//    				while ((inputLine = in_connection.readLine()) != null) {
//			  	           	output.println(inputLine);
//        			}
//                    if (in_connection_open) in_connection.close();
//                    if (output_open) output.close();
//                     status=status_done;
//
//                 } catch(Exception e) {e.printStackTrace();status=status_error;}
//            }
//         }.start(); //start thread
//    }//End runthread

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

    /**
     * Replace the String with a normal string
     * @param str
     * @return the transformed string
     */
    public String transform(String str) {
        String transform=str.toLowerCase();
        transform=transform.replaceAll(", ", ",");
        transform=transform.replace(' ', '+');
        transform=transform.replaceAll("#", "%23");
        //--HANDLE special case
        transform=transform.replaceAll("[+]and[+]","+AND+");
        transform=transform.replaceAll("[+]not[+]","+NOT+");
        transform=transform.replaceAll("[+]or[+]","+OR+");
        return transform;
    }

    public boolean saveToFile(String filename) {
        try {
            PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
            pw.println(buffer.toString());
            pw.close();
            return true;
        } catch(Exception e) {return false;}
    }


    ////////////////////////////////////////////////////////////////////////////
    /// Getter / Setter

    /**
     * @return the term
     */
    public String getTerm() {
        return term;
    }

    /**
     * @param term the term to set
     */
    public void setTerm(String term) {
        this.term = transform(term);
    }

    /**
     * @return the useHistory
     */
    public boolean isUseHistory() {
        return useHistory;
    }

    /**
     * @param useHistory the useHistory to set
     */
    public void setUseHistory(boolean useHistory) {
        this.useHistory = useHistory;
    }

    /**
     * @return the WebEnv
     */
    public String getWebEnv() {
        return WebEnv;
    }

    /**
     * @param WebEnv the WebEnv to set
     */
    public void setWebEnv(String WebEnv) {
        this.WebEnv = WebEnv;
    }

    /**
     * @return the Query_key
     */
    public String getQuery_key() {
        return Query_key;
    }

    /**
     * @param Query_key the Query_key to set
     */
    public void setQuery_key(String Query_key) {
        this.Query_key = Query_key;
    }

    /**
     * @return the Engine
     */
    public String getEngine() {
        return Engine;
    }

    /**
     * @param Engine the Engine to set
     */
    public void setEngine(String Engine) {
        this.Engine = Engine;
    }

    /**
     * @return the databaseFrom
     */
    public String getDatabaseFrom() {
        return databaseFrom;
    }

    /**
     * @param databaseFrom the databaseFrom to set
     */
    public void setDatabaseFrom(String databaseFrom) {
        this.databaseFrom = databaseFrom;
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * @return the Custom
     */
    public boolean isCustom() {
        return Custom;
    }

    /**
     * @param Custom the Custom to set
     */
    public void setCustom(boolean Custom) {
        this.Custom = Custom;
    }

    /**
     * @return the rettype
     */
    public String getRettype() {
        return rettype;
    }

    /**
     * @param rettype the rettype to set
     */
    public void setRettype(String rettype) {
        this.rettype = rettype;
    }

    /**
     * @return the retmax
     */
    public String getRetmax() {
        return retmax;
    }

    /**
     * @param retmax the retmax to set
     */
    public void setRetmax(String retmax) {
        this.retmax = retmax;
    }

    /**
     * @param retmax the retmax to set
     */
    public void setRetmax(int retmax) {
        this.retmax = ""+retmax;
    }

    /**
     * @return the force_download
     */
    public boolean isForce_download() {
        return force_download;
    }

    /**
     * @param force_download the force_download to set
     */
    public void setForce_download(boolean force_download) {
        this.force_download = force_download;
    }

    /**
     * @return the entrezdate
     */
    public String getRelEntrezdate() {
        return entrezdate;
    }

    /**
     * @param entrezdate the entrezdate to set
     */
    public void setRelEntrezdate(String entrezdate) {
        this.entrezdate = entrezdate;
    }
    
    /**
     * @param entrezdate the entrezdate to set
     */
    public void setRelEntrezdate(int entrezdate) {
        this.entrezdate = ""+entrezdate;
    }

    /**
     * @return the minentrezdate
     */
    public String getMinentrezdate() {
        return minentrezdate;
    }

    /**
     * @param minentrezdate the minentrezdate to set
     */
    public void setMinentrezdate(String minentrezdate) {
        this.minentrezdate = minentrezdate;
    }

    /**
     * @return the maxentrezdate
     */
    public String getMaxentrezdate() {
        return maxentrezdate;
    }

    /**
     * @param maxentrezdate the maxentrezdate to set
     */
    public void setMaxentrezdate(String maxentrezdate) {
        this.maxentrezdate = maxentrezdate;
    }

    /**
     * @return the retstart
     */
    public String getRetstart() {
        return retstart;
    }

    /**
     * @param retstart the retstart to set
     */
    public void setRetstart(String retstart) {
        this.retstart = retstart;
    }

    /**
     * @return the sort
     */
    public String getSort() {
        return sort;
    }

    /**
     * @param sort the sort to set
     */
    public void setSort(String sort) {
        this.sort = sort;
    }

    /**
     * @return the retmode
     */
    public String getRetmode() {
        return retmode;
    }

    /**
     * @param retmode the retmode to set
     */
    public void setRetmode(String retmode) {
        this.retmode = retmode;
    }

    /**
     * @return the outputToDisk
     */
    public boolean isOutputToDisk() {
        return outputToDisk;
    }

    /**
     * @param outputToDisk the outputToDisk to set
     */
    public void setOutputToDisk(boolean outputToDisk) {
        this.outputToDisk = outputToDisk;
    }

    /**
     * @return the strand
     */
    public String getStrand() {
        return strand;
    }

    /**
     * @param strand the strand to set
     */
    public void setStrand(String strand) {
        this.strand = strand;
    }

    /**
     * @return the seq_start
     */
    public String getSeq_start() {
        return seq_start;
    }

    /**
     * @param seq_start the seq_start to set
     */
    public void setSeq_start(String seq_start) {
        this.seq_start = seq_start;
    }

    /**
     * @return the seq_stop
     */
    public String getSeq_stop() {
        return seq_stop;
    }

    /**
     * @param seq_stop the seq_stop to set
     */
    public void setSeq_stop(String seq_stop) {
        this.seq_stop = seq_stop;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
}
