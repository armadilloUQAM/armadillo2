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

import configuration.Config;
import configuration.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interface EBI REST Interface
 * Note: DOES NTO WORK FOR NOW
 * http://www.ebi.ac.uk/Tools/services/rest/ncbiblast/parameters/
 * http://www.ebi.ac.uk/Tools/webservices/services/sss/ncbi_blast_rest
 * @author Etienne Lord
 * @since June 2014 
 */
//

////////////////////////////////////////////////////////////////////////////////
// VARIOUS INFO 
// For example, to search with short nucleotide queries, 
// we often need to disable the low complexity filter and loosen the Expect cutoff 
// so that we can see the short matches normally not reported by BLAST. 
// We can accomplish this by using 'FILTER=F&EXPECT=1000'. Another example is searching with a human genomic query, 
// there we need to activate the human repeat filter to mask the repeats present in the query and reduce 
// the number of spurious matches to similar repeats found in the database. 
// To do so, we will need to add another FILTER parameter 'FILTER=R'.

public class ebiblast {

    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    public static Config config=new Config();
    private BufferedReader in_connection;
    private boolean in_connection_open = false;
    private URL url=null;
    public StringBuilder buffer=new StringBuilder(); //--Return_buffer from the last Qblast
    boolean debug=true;
    private String RID="";

    ////////////////////////////////////////////////////////////////////////////
    /// Constant

   // public static String debugStringPUT="&QUERY=MRGSVGFLLCSLLLALSGTEMADQADQSDPKMSCANWMGGAPGHPGHNGLPGRDGKDGKDGQKGDKGEPGLQGVKGDTGEKGATGAEGPRGFPGHMGMKGQKGESSYVYRSAFSVGLTERAPHPNVPIRFTKIFYNEQNHYDSSTGKFLCSIPGTYFFAYHLTVYMTDVKVSLYKKDKAVIFTYDQFQENNVDQASGSVLLHLSLGDEVWLQVYGEGNNNGVYADNINDSTFMGFLLYPDTDDR&DATABASE=nr&PROGRAM=blastp&FILTER=L&HITLIST_SZE=500";
    public static String PUT="http://www.ebi.ac.uk/Tools/services/rest/ncbiblast/run/";
    public static String GET="http://www.ebi.ac.uk/Tools/services/rest/ncbiblast/status/";
    public static String INFO="http://www.ebi.ac.uk/Tools/services/rest/ncbiblast/result/";
   // public static String DELETE="http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?CMD=Delete";

    //--e.g. Parameter details :: database :: http://www.ebi.ac.uk/Tools/services/rest/ncbiblast//parameterdetails/database
    
    public static String WAITING="WAITING";
    public static String READY="READY";
    public static String UNKNOWN="UNKNOWN";

    ////////////////////////////////////////////////////////////////////////////
    /// Blast Specific Variables

        private String AUTO_FORMAT="off";//Off, Semiauto, Fullauto
        private int ALIGNMENTS=500;
        private String ALIGNMENT_VIEW="Pairwise"; //Pairwise, PairwiseWithIdentities, QueryAnchored, QueryAnchoredNoIdentities, FlatQueryAnchored, FlatQueryAnchoredNoIdentities, Tabular
        private int COMPOSITION_BASED_STATISTICS=0; //COMPOSITION_BASED_STATISTICS: 0, 1 , 2 , 3
        // DAtabase
        // http://www.ebi.ac.uk/Tools/services/rest/ncbiblast/parameterdetails/database
        private String DATABASE="uniprotkb_swissprot";
        private int DB_GENETIC_CODE=1;
        private int DESCRIPTIONS=500; //num_descriptions
        private boolean ENDPOINTS=false; //MegaBlasts
        // Specifies the Expect value (significance) cutoff (EXPECT=1e-10)
        private Double EXPECT=0.0; // expected value 1000.0; default 10;        
        private String FILTER="F"; //L : Low, R Human repeat, m, Mask for Lookup, F filter
        private String FORMAT_OBJECT="Alignment"; //Neighbors, PSSM, SearchInfo, TaxBlast, TaxblastParent, TaxBlastMultiFrame
        private String FORMAT_TYPE="Text"; //HTML, ASN.1m, XML
        //private String GAPCOSTS="5 2"; //11 1 Prot. -G -E
        private String GOP=""; //5
        private String GEP=""; //2
        private int GENETIC_CODE=1; //Query genetic code
        private int HITLIST_SIZE=500;
        private String ENTREZ_QUERY=""; // Limit to organism
        private float I_TRESH=0.001f; //PSI-BLAST
        //his parameter instructs BLAST to skip the regions of the query that are in lowercase during hit table construction, so that matches to the remainder of the query can be better evaluated. Same as in wwwblas
        private Boolean LCASE_MASK=false; 
        private Boolean MEGABLAST=false;
        private String MATRIX_NAME="BLOSUM62"; //Protein: PAM30, PAM70, BLOSUM45, BLOSUM42, BLOSUM80, 62 default
        private Boolean NCBI_GI=false; //Displays the gi numbers for the database matches
        private String NUCL_PENALTY=""; //-3 //--sc-mismatch negative integer value
        private String NUCL_REWARD=""; // 1  //--sc-match integer value
        private String OTHER_ADVANCED="";
        private int PERC_IDENT=0; //integer between 0 (no cut-off) and 100 (exact matches only)
        private String PROGRAM="blastn"; //blastn, MegaBlast, discoMegablast; blastp, psiBlast, phiBlast; blastx, tblastn, tblastx
        private String SERVICE="plain"; //psi, phi, rpsblast, megablast
        private Boolean UNGAPPED_ALIGNMENT=false;
        private int WORD_SIZE=11; //3 Prot, 28 mega...11 nucleotides
        private String QUERY=""; //Fasta file
       
   /////////////////////////////////////////////////////////////////////////////
   /// String for qblast        
        private String encoded_put_query=""; //Encoded String for PUT
        private String encoded_get_query=""; //Encoded String for GET

    /**      
     * send a query to ncbi for blast
     * @param encoded_url
     * @return a RID
     */
    private String  EBIGet(String COMMAND, String search_string)  {
                String composite_url=COMMAND+search_string;
                buffer=new StringBuilder();
                try {
                    if (debug) Config.log("Blast command:"+composite_url);
                    url= new URL(composite_url);
		        
		} catch (MalformedURLException e) {Config.log("Error : URL is not good.");}
		try {
			if (in_connection_open) in_connection.close();
			in_connection = new BufferedReader(new InputStreamReader(url.openStream()));
			if (in_connection!=null) in_connection_open=true;
                        String inputLine="";
                        while ((inputLine = in_connection.readLine()) != null) {
			  	buffer.append(inputLine+"\n");                                
                        }
                        
		} catch (IOException e) {Config.log("Unable to get QBlast..."+composite_url);}
	return buffer.toString();
     }


    ////////////////////////////////////////////////////////////////////////////
    /// Public function

    /**
     * Create the blast POST for QUERY
     * @return RID for this QBlast
     */
    public String QBlast_PUT() {
        //--POST
        www_post post=new www_post();
        if (!config.isSet("email")) {
            System.out.println("You must set the email in preference -> email. ");
            return "";
        }
        post.put("email", config.get("email"));
        post.put("title", "1");
        post.put("sequence", QUERY);
        post.put("database", DATABASE);
        post.put("matrix", MATRIX_NAME);
        post.put("program", PROGRAM);      
//        post.put("request_fulluri",true);
//        post.put("timeout", "300");
//        post.put("header", "Content-type: application/x-www-form-urlencoded\r\n");
//        post.put("User-agent", "Mozilla");
//        post.put("content", "Mozilla");
        
//         'method'  => 'POST',
//				  'request_fulluri' => true,
//				  'timeout'         => 300,
//				  'header'  => 'Content-type: application/x-www-form-urlencoded' . "\r\n" .
//				  'User-agent: ' . $this->getUserAgent() . "\r\n",
//				  'content' => $postdata
        
        
//        	# Database(s) to search
//	my (@dbList) = split /[ ,]/, $params{'database'};
//	$tool_params{'database'} = \@dbList;
//
//	# Match/missmatch
//	if ( $params{'match'} && $params{'missmatch'} ) {
//		$tool_params{'match_scores'} =
//		  $params{'match'} . ',' . $params{'missmatch'};
//	}
//	
//	# Compatability options, old command-line
//	if(!$tool_params{'alignments'} && $params{'numal'}) {
//		$tool_params{'alignments'} = $params{'numal'};
//	}
//	if(!$tool_params{'gapopen'} && $params{'opengap'}) {
//		$tool_params{'gapopen'} = $params{'opengap'};
//	}
//	if(!$tool_params{'gapext'} && $params{'extendgap'}) {
//		$tool_params{'gapext'} = $params{'extendgap'};
//	}
//        
        
        
        //if (this.getEncoded_put_query().isEmpty()) this.createPUTQuery();
        
        post.do_post(this.PUT, "");
        System.out.println(post.buffer.toString());
        // EBIGet(PUT, this.getEncoded_put_query());
        this.setRID(post.buffer.toString());
        //return RID;
        return post.buffer.toString();
    }

    /**
     * Return Status for this Blast Job
     * @return
     */
    public String QBlast_GET() {
        Config.log("GET "+getRID());
        if (this.getEncoded_get_query().isEmpty()) createGETQuery();
        return returnStatus( EBIGet(GET, this.getEncoded_get_query()));
    }


    public boolean saveToFile(String filename) {
        try {
            PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
            pw.println(buffer.toString());
            pw.close();
            return true;
        } catch(Exception e) {return false;}
    }

      public boolean appendToFile(String filename) {
        try {
            PrintWriter pw=new PrintWriter(new FileWriter(new File(filename), true));
            pw.println(buffer.toString());
            pw.close();
            return true;
        } catch(Exception e) {return false;}
    }

    public String getResult() {
        return buffer.toString();
    }

    public void createPUTQuery() {
        StringBuilder put=new StringBuilder();
        
        put.append("&QUERY="+encode(QUERY));
        put.append("&AUTO_FORMAT="+encode(AUTO_FORMAT));
        if ( COMPOSITION_BASED_STATISTICS!=0) put.append("&COMPOSITION_BASE_STATISTICS="+this.COMPOSITION_BASED_STATISTICS);
        put.append("&DATABASE="+encode(this.DATABASE));
        if (this.PROGRAM.equals("tblatn")||this.PROGRAM.equals("tblastx")) {
            put.append("&DB_GENETIC_CODE="+this.DB_GENETIC_CODE);
        }
        put.append("&HITLIST_SIZE="+this.HITLIST_SIZE);
        
        if (this.MEGABLAST) {
            put.append("&ENDPOINTS="+booleanToString(this.ENDPOINTS));
            put.append("&MEGABLAST=yes");
            put.append("&PERC_IDENT="+this.getPERC_IDENT());
        }
        if (this.EXPECT!=0) put.append("&EXPECT="+this.EXPECT.floatValue());
        
        if (!FILTER.isEmpty()) {
            put.append("&FILTER="+encode(this.FILTER));
        }
        //--Note, only if set...
        if (!(GOP.equals("") &&GEP.equals(""))) {
            String gaps=GOP+" "+GEP;
            put.append("&GAPCOSTS="+encode(gaps));
        //put.append("&GAPCOSTS="+encode("5 2"));
        }
        if (this.GENETIC_CODE!=1) {
            put.append("&GENETIC_CODE="+this.GENETIC_CODE);
        }
        put.append("&LCASE_MASK="+booleanToString(this.LCASE_MASK));
        //I_TRESH
        if (this.PROGRAM.equals("blastp")) {
            put.append("&MATRIX_NAME="+encode(this.MATRIX_NAME));
        }        
        if (this.PROGRAM.equals("blastn")) {
            if (!this.NUCL_PENALTY.isEmpty()) put.append("&NUCL_PENALTY="+this.NUCL_PENALTY);
            if (!this.NUCL_REWARD.isEmpty()) put.append("&NUCL_REWARD="+this.NUCL_REWARD);
        }
        if (!OTHER_ADVANCED.isEmpty()) {
            put.append("&OTHER_ADVANCED="+encode(this.OTHER_ADVANCED));
        }
        put.append("&PROGRAM="+encode(this.PROGRAM));
        //put.append("&QUERY="+encode(this.QUERY));
        //put.append("&QUERY_BELIEVE_DEFLINE=yes"));
        put.append("&SERVICE="+encode(this.SERVICE));
        put.append("&UNGAPPED_ALIGNMENT="+booleanToString(this.UNGAPPED_ALIGNMENT));
        put.append("&WORD_SIZE="+this.WORD_SIZE);
        if (!ENTREZ_QUERY.isEmpty()) {
            put.append("&ENTREZ_QUERY="+encode(this.ENTREZ_QUERY));            
        }
        this.setEncoded_put_query(put.toString());
        if (debug) System.out.println(put.toString());
    }

    public void createGETQuery() {
        StringBuilder get=new StringBuilder();
        get.append("&RID="+this.getRID());
        get.append("&ALIGNMENTS="+this.getALIGNMENTS());
        get.append("&ALIGNMENT_VIEW="+encode(this.ALIGNMENT_VIEW));
        get.append("&DESCRIPTIONS="+this.DESCRIPTIONS);
        get.append("&FORMAT_OBJECT="+encode(this.FORMAT_OBJECT));
        get.append("&FORMAT_TYPE="+encode(this.FORMAT_TYPE));
        get.append("&SHOW_OVERVIEW=no");
        if (this.NCBI_GI) get.append("&NCBI_GI=true");
        this.setEncoded_get_query(get.toString());
    }

    //
//            <!--
//        QBlastInfoBegin
//                Status=INFO_DB
//        # Number of databases
//        23
//
//        # exlclusive databases
//
//        nr              1       TRUE
//        nr              2       FALSE
//        est_human       3       FALSE
//        est_mouse       4       FALSE
//        est_others      5       FALSE
//        htg             6       FALSE
//        gss             7       FALSE
//        sts             8       FALSE
//        pataa                9        TRUE
//        patnt                10        FALSE
//
//        # non-exlclusive databases
//
//        swissprot       11      TRUE
//        est             12      FALSE
//        pdb             13      TRUE
//        pdb             14      FALSE
//        month           15      TRUE
//        month.nt        16      FALSE
//        month.est       17      FALSE
//        month.gss       18      FALSE
//        month.htgs      19      FALSE
//        month.sts       20      FALSE
//        month.pataa     21      TRUE
//        month.patnt     22      FALSE
//
//        # new
//
//        chromosome      23      TRUE
//
//        # end of the file
//
//        QBlastInfoEnd
//        -->
    public String QBlast_INFO(String target) {
        //databases or Qblast
        Config.log("INFO "+target);
        return  EBIGet(INFO,target);
    }

     public String QBlast_DELETE(String RID) {
         //Config.log("DELETE "+RID);
        // return QBlast(DELETE,"&RID="+RID);
         return "false";
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Help function

    public String returnRID() {
        if (buffer.length()==0) return "";
        String tmp_RID="";
        Pattern PUT_RID=Pattern.compile("RID.=.(.*)");       
         Matcher m = PUT_RID.matcher(buffer.toString());
         if (m.find()) {
             tmp_RID = m.group(1);
         }                 
        return tmp_RID;
    }

     public String returnMessage() {
        if (buffer.length()==0) return "";
        String tmp_RID="";
        Pattern PUT_RID=Pattern.compile("<p class=\"error\">(.*)</p>");
         Matcher m = PUT_RID.matcher(buffer.toString());
         if (m.find()) {
             tmp_RID = m.group(1);
         }
        return tmp_RID;
    }

    /**
     * Return the requested time for the blast search
     * in ms
     * @return 0 if not found, else time in ms
     */
    public int returnRTOE() {
       if (buffer.length()==0) return 0;
        String RTOE="";
        int time=0;
        Pattern PUT_RID=Pattern.compile("RTOE.=.(.*)");       
         Matcher m = PUT_RID.matcher(buffer.toString());
         if (m.find()) {
             RTOE = m.group(1);
             try {
                 time=Integer.valueOf(RTOE)*1000;
            } catch (Exception e) {time=0;}
         }         
        return time;
    }

    public String returnStatus(String str) {
         if (str==null) return "";
        String STATUS="";
        Pattern PUT_RID=Pattern.compile("Status=(.*)", Pattern.CASE_INSENSITIVE);
         Matcher m = PUT_RID.matcher(str);
         if (m.find()) {
             STATUS = m.group(1);
             return STATUS;
         } else {             
             return READY;
         }
        
    }
    
    public String encode(String url_to_encode) {
        try {
            return URLEncoder.encode(url_to_encode, "UTF-8");
        }catch(Exception e) {return "";}
    }

    public String booleanToString(Boolean b) {
        return (b?"yes":"no");
    }

     
    public void init_createCommandLine() {
//        blastp -query query.txt -db influenzaAA -out blast_all.txt
//       String[] com=new String[40];
//       int index=9;
//       for (int i=0; i<com.length;i++) com[i]="";
//       com[0]="cmd.exe";
//       com[1]="/C";
//       com[2]=properties.getExecutable();
//       com[2]=properties.get("Executable");
//       com[3]="-query";
//       com[4]="query.txt";
//       com[5]="-db";
//       com[6]="\""+db.getBlastDB()+"\"";
//       com[7]="-out";
//       com[8]="\""+properties.get("outfile")+"\"";
//       if (properties.isSet("word_size")) {
//            com[index++]="-word_size";
//            com[index++]=properties.get("word_size");
//       }
//       if (properties.isSet("num_descriptions")) {
//            com[index++]="-num_descriptions";
//            com[index++]=properties.get("num_descriptions");
//       }
//       if (properties.isSet("num_alignments")) {
//            com[index++]="-num_alignments";
//            com[index++]=properties.get("num_alignments");
//       }
//       if (properties.isSet("task")) {
//           com[index++]="-task";
//           com[index++]=properties.get("task");
//       }
//       if (properties.isSet("strand")) {
//            com[index++]="-strand";
//            com[index++]=properties.get("strand");
//       }
//       if (properties.isSet("wordsize")) {
//            com[index++]="-word_size";
//            com[index++]=properties.get("wordsize");
//       }
//       if (properties.isSet("penalty")) {
//           com[index++]="-penalty";
//           com[index++]=properties.get("penalty");
//       }
//       if (properties.isSet("reward")) {
//           com[index++]="-reward";
//           com[index++]=properties.get("reward");
//       }
//       if (properties.isSet("gapextend")) {
//           com[index++]="-gapextend";
//           com[index++]=properties.get("gapextend");
//       }
//       if (properties.isSet("gapopen")) {
//            com[index++]="-gapopen";
//           com[index++]=properties.get("gapopen");
//       }
//       if (properties.getBoolean("ungapped")) {
//           com[index++]="-ungapped";
//       }
//       if (properties.getBoolean("dust")) {
//           com[index++]="-dust";
//           com[index++]="yes";
//       }
//       if (!format.isEmpty()) {
//         com[index++]="-outfmt";
//         com[index++]="\""+format+"\"";
//       }
//       com[index++]="-export_search_strategy";
//       com[index++]="strategy.txt";
//       if (debug) Config.log(Util.toString(com));
//       return com;
    }

    /**
     * @return the AUTO_FORMAT
     */
    public String getAUTO_FORMAT() {
        return AUTO_FORMAT;
    }

    /**
     * @param AUTO_FORMAT the AUTO_FORMAT to set
     */
    public void setAUTO_FORMAT(String AUTO_FORMAT) {
        this.AUTO_FORMAT = AUTO_FORMAT;
    }

    /**
     * @return the DATABASE
     */
    public String getDATABASE() {
        return DATABASE;
    }

    /**
     * @param DATABASE the DATABASE to set
     */
    public void setDATABASE(String DATABASE) {
        this.DATABASE = DATABASE;
    }

    /**
     * @return the DB_GENETIC_CODE
     */
    public int getDB_GENETIC_CODE() {
        return DB_GENETIC_CODE;
    }

    /**
     * @param DB_GENETIC_CODE the DB_GENETIC_CODE to set
     */
    public void setDB_GENETIC_CODE(int DB_GENETIC_CODE) {
        this.DB_GENETIC_CODE = DB_GENETIC_CODE;
    }

    /**
     * @return the DESCRIPTIONS
     */
    public int getDESCRIPTIONS() {
        return DESCRIPTIONS;
    }

    /**
     * @param DESCRIPTIONS the DESCRIPTIONS to set
     */
    public void setDESCRIPTIONS(int DESCRIPTIONS) {
        this.DESCRIPTIONS = DESCRIPTIONS;
    }

    /**
     * @return the ENDPOINTS
     */
    public Boolean getENDPOINTS() {
        return ENDPOINTS;
    }

    /**
     * @param ENDPOINTS the ENDPOINTS to set
     */
    public void setENDPOINTS(Boolean ENDPOINTS) {
        this.ENDPOINTS = ENDPOINTS;
    }

    /**
     * @return the EXPECT
     */
    public double getEXPECT() {
        return EXPECT;
    }

    /**
     * @param EXPECT the EXPECT to set
     */
    public void setEXPECT(double EXPECT) {
        this.EXPECT = EXPECT;
    }

    /**
     * @return the FILTER
     */
    public String getFILTER() {
        return FILTER;
    }

    /**
     * @param FILTER the FILTER to set
     */
    public void setFILTER(String FILTER) {
        this.FILTER = FILTER;
    }

    /**
     * @return the FORMAT_OBJECT
     */
    public String getFORMAT_OBJECT() {
        return FORMAT_OBJECT;
    }

    /**
     * @param FORMAT_OBJECT the FORMAT_OBJECT to set
     */
    public void setFORMAT_OBJECT(String FORMAT_OBJECT) {
        this.FORMAT_OBJECT = FORMAT_OBJECT;
    }

    /**
     * @return the FORMAT_TYPE
     */
    public String getFORMAT_TYPE() {
        return FORMAT_TYPE;
    }

    /**
     * @param FORMAT_TYPE the FORMAT_TYPE to set
     */
    public void setFORMAT_TYPE(String FORMAT_TYPE) {
        this.FORMAT_TYPE = FORMAT_TYPE;
    }

    
    /**
     * @return the GENETIC_CODE
     */
    public int getGENETIC_CODE() {
        return GENETIC_CODE;
    }

    /**
     * @param GENETIC_CODE the GENETIC_CODE to set
     */
    public void setGENETIC_CODE(int GENETIC_CODE) {
        this.GENETIC_CODE = GENETIC_CODE;
    }

    /**
     * @return the HITLIST_SIZE
     */
    public int getHITLIST_SIZE() {
        return HITLIST_SIZE;
    }

    /**
     * @param HITLIST_SIZE the HITLIST_SIZE to set
     */
    public void setHITLIST_SIZE(int HITLIST_SIZE) {
        this.HITLIST_SIZE = HITLIST_SIZE;
    }

    /**
     * @return the I_TRESH
     */
    public float getI_TRESH() {
        return I_TRESH;
    }

    /**
     * @param I_TRESH the I_TRESH to set
     */
    public void setI_TRESH(float I_TRESH) {
        this.I_TRESH = I_TRESH;
    }

    /**
     * @return the LCASE_MASK
     */
    public Boolean getLCASE_MASK() {
        return LCASE_MASK;
    }

    /**
     * @param LCASE_MASK the LCASE_MASK to set
     */
    public void setLCASE_MASK(Boolean LCASE_MASK) {
        this.LCASE_MASK = LCASE_MASK;
    }

    /**
     * @return the MEGABLAST
     */
    public Boolean getMEGABLAST() {
        return MEGABLAST;
    }

    public void setMATCH(String n) {
        this.NUCL_REWARD=n;
    }
    
    public void setMISMATCH(String n) {
        this.NUCL_PENALTY=n;
    }
    
    /**
     * @param MEGABLAST the MEGABLAST to set
     */
    public void setMEGABLAST(Boolean MEGABLAST) {
        this.MEGABLAST = MEGABLAST;
    }

    /**
     * @return the MATRIX_NAME
     */
    public String getMATRIX_NAME() {
        return MATRIX_NAME;
    }

    /**
     * @param MATRIX_NAME the MATRIX_NAME to set
     */
    public void setMATRIX_NAME(String MATRIX_NAME) {
        this.MATRIX_NAME = MATRIX_NAME;
    }

   

    /**
     * @return the NUCL_PENALTY
     */
    public String getNUCL_PENALTY() {
        return NUCL_PENALTY;
    }

    /**
     * @param NUCL_PENALTY the NUCL_PENALTY to set
     */
    public void setNUCL_PENALTY(String NUCL_PENALTY) {
        this.NUCL_PENALTY = NUCL_PENALTY;
    }

    /**
     * @return the NUCL_REWARD
     */
    public String getNUCL_REWARD() {
        return NUCL_REWARD;
    }

    /**
     * @param NUCL_REWARD the NUCL_REWARD to set
     */
    public void setNUCL_REWARD(String NUCL_REWARD) {
        this.NUCL_REWARD = NUCL_REWARD;
    }

    /**
     * @return the OTHER_ADVANCED
     */
    public String getOTHER_ADVANCED() {
        return OTHER_ADVANCED;
    }

    /**
     * @param OTHER_ADVANCED the OTHER_ADVANCED to set
     */
    public void setOTHER_ADVANCED(String OTHER_ADVANCED) {
        this.OTHER_ADVANCED = OTHER_ADVANCED;
    }

    /**
     * @return the PROGRAM
     */
    public String getPROGRAM() {
        return PROGRAM;
    }

    /**
     * @param PROGRAM the PROGRAM to set
     */
    public void setPROGRAM(String PROGRAM) {
        this.PROGRAM = PROGRAM;
    }

    /**
     * @return the SERVICE
     */
    public String getSERVICE() {
        return SERVICE;
    }

    /**
     * @param SERVICE the SERVICE to set
     */
    public void setSERVICE(String SERVICE) {
        this.SERVICE = SERVICE;
    }

    /**
     * @return the UNGAPPED_ALIGNMENT
     */
    public Boolean getUNGAPPED_ALIGNMENT() {
        return UNGAPPED_ALIGNMENT;
    }

    /**
     * @param UNGAPPED_ALIGNMENT the UNGAPPED_ALIGNMENT to set
     */
    public void setUNGAPPED_ALIGNMENT(Boolean UNGAPPED_ALIGNMENT) {
        this.UNGAPPED_ALIGNMENT = UNGAPPED_ALIGNMENT;
    }

    /**
     * @return the WORD_SIZE
     */
    public int getWORD_SIZE() {
        return WORD_SIZE;
    }

    /**
     * @param WORD_SIZE the WORD_SIZE to set
     */
    public void setWORD_SIZE(int WORD_SIZE) {
        this.WORD_SIZE = WORD_SIZE;
    }

    /**
     * @return the QUERY
     */
    public String getQUERY() {
        return QUERY;
    }

    /**
     * @param QUERY the QUERY to set
     */
    public void setQUERY_FASTA(String QUERY) {
        this.QUERY = QUERY;
    }

    /**
     * @return the gop
     */
    public String getGOP() {
        return GOP;
    }

    /**
     * @param gop the gop to set
     */
    public void setGOP(String gop) {
        this.GOP = gop;
    }

    /**
     * @return the gep
     */
    public String getGEP() {
        return GEP;
    }

    /**
     * @param gep the gep to set
     */
    public void setGEP(String gep) {
        this.GEP = gep;
    }

    /**
     * @return the ALIGNMENTS
     */
    public int getALIGNMENTS() {
        return ALIGNMENTS;
    }

    /**
     * @param ALIGNMENTS the ALIGNMENTS to set
     */
    public void setALIGNMENTS(int ALIGNMENTS) {
        this.ALIGNMENTS = ALIGNMENTS;
    }

    /**
     * @return the RID
     */
    public String getRID() {
        return RID;
    }

    /**
     * @param RID the RID to set
     */
    public void setRID(String RID) {
        this.RID = RID;
    }

    /**
     * @return the PERC_IDENT
     */
    public int getPERC_IDENT() {
        return PERC_IDENT;
    }

    /**
     * @param PERC_IDENT the PERC_IDENT to set
     */
    public void setPERC_IDENT(int PERC_IDENT) {
        this.PERC_IDENT = PERC_IDENT;
    }

    /**
     * @return the encoded_put_query
     */
    public String getEncoded_put_query() {
        return encoded_put_query;
    }

    /**
     * @param encoded_put_query the encoded_put_query to set
     */
    public void setEncoded_put_query(String encoded_put_query) {
        this.encoded_put_query = encoded_put_query;
    }

    /**
     * @return the encoded_get_query
     */
    public String getEncoded_get_query() {
        return encoded_get_query;
    }

    /**
     * @param encoded_get_query the encoded_get_query to set
     */
    public void setEncoded_get_query(String encoded_get_query) {
        this.encoded_get_query = encoded_get_query;
    }

    /**
     * @return the ENTREZ_QUERY
     */
    public String getENTREZ_QUERY() {
        return ENTREZ_QUERY;
    }

    /**
     * @param ENTREZ_QUERY the ENTREZ_QUERY to set
     */
    public void setENTREZ_QUERY(String ENTREZ_QUERY) {
        this.ENTREZ_QUERY = ENTREZ_QUERY;
    }

    /**
     * @return the COMPOSITION_BASED_STATISTICS
     */
    public int getCOMPOSITION_BASED_STATISTICS() {
        return COMPOSITION_BASED_STATISTICS;
    }

    /**
     * @param COMPOSITION_BASED_STATISTICS the COMPOSITION_BASED_STATISTICS to set
     */
    public void setCOMPOSITION_BASED_STATISTICS(int COMPOSITION_BASED_STATISTICS) {
        this.COMPOSITION_BASED_STATISTICS = COMPOSITION_BASED_STATISTICS;
    }

    /**
     * @return the NCBI_GI
     */
    public Boolean getNCBI_GI() {
        return NCBI_GI;
    }

    /**
     * @param NCBI_GI the NCBI_GI to set
     */
    public void setNCBI_GI(Boolean NCBI_GI) {
        this.NCBI_GI = NCBI_GI;
    }

}
