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
import biologic.BlastHit;
import biologic.Blast;
import configuration.Util;
import java.util.Vector;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple Structure for a Blast file
 * @author Etienne Lord
 */
public class blastParser {

    ////////////////////////////////////////////////////////////////////////////
    /// Constant

    //--Search mode
    public final int MODE_ID=0;                                                 
    public final int MODE_ACCESSION=1;
    public final int MODE_DESC=2;
    public final int MODE_ALIASES=3;
    public final int MODE_ALL=4;
    public final int MODE_LENMORE=6;
    public final int MODE_LENLESS=7;
    public final int MODE_MAXBITSCORE=8;
    public final int MODE_EVALUELESS=9;
    public final int MODE_EVALUEMORE=10;
    String lastSearch="";
    //--Results
    static String str_query="Query=";
    static String str_length="Length=";
    static String str_nohits="***** No hits found *****";
    static String str_nohit2="No significant similarity found.";

    static Pattern pat_query;
    static Pattern pat_query_embl;
    static Pattern pat_length;
    static Pattern pat_score;
    static Pattern pat_expect;
    static Pattern pat_gap;
    static Pattern pat_strand;
    static Pattern pat_method;
    static Pattern pat_identities;
    static Pattern pat_positives;
    static Pattern pat_database;
    static Pattern pat_blasthit;
    static Pattern pat_blasthit2;
    static Pattern pat_blasthit_embl;
    static Pattern pat_blasthit_embl2;
    static Pattern pat_bitscore;
    static Pattern pat_query_seq;
    static Pattern pat_subject_seq;

    boolean embl=false;

    //--Tag
    static String str_insequence="Sequences producing significant alignments:";
    static String str_indatabase="Database:";    
   
    //--Debug
    static boolean debug =false;
    static boolean initialized=false;
    ////////////////////////////////////////////////////////////////////////////
    /// Variables

    private String dbname="";
    private String dbinfo = "";
    private String queryname = "";
    private String querylength = "";
    private String filename = "";
    private double max_bitscore = 0;
    private double min_bitscore = 0;
    //--Temporary arrayList containing the blasthit for each query
    private LinkedList<BlastHit> blasthit_list = new LinkedList<BlastHit>();
    //--List containing the various query and results
    private Vector<Blast> list=new Vector<Blast>();
    
    ////////////////////////////////////////////////////////////////////////////
    /// Constructor

    /**
     * Structure to hold and load a text blast file - Default constructor
     *
     */
    public blastParser() {
        //--Initialized some regex
        if (!initialized) {                     
              initialized();
            initialized=true;
        }
    }
    
    public blastParser(String filename) {
          //--Initialized some regex
        if (!initialized) {                     
                initialized();
            initialized=true;
        }
        load(filename);        
    }

    public void initialized() {
          try {
                    
                    pat_query=Pattern.compile("Query=(.*)Length=([0-9]*)",Pattern.CASE_INSENSITIVE);
                    pat_query_embl=Pattern.compile("Query=(.*)\\(([0-9]*).letters", Pattern.CASE_INSENSITIVE);
                    //sp|Q15848.1|ADIPO_HUMAN  RecName: Full=Adiponectin; AltName: F...    457   6e-163
                    pat_blasthit=Pattern.compile("(.*)\\|(.*)\\|\\s*(.*)\\s{2,}([0-9]*|[0-9]*\\.[0-9]*).*([0-9]*.[0-9]*|[0-9]*e-[0-9]*)",Pattern.CASE_INSENSITIVE);
                    pat_blasthit2=Pattern.compile("(.*)\\|(.*)\\s{2,}([0-9]*|[0-9]*\\.[0-9]*).*([0-9]*.[0-9]*|[0-9]*e-[0-9]*)",Pattern.CASE_INSENSITIVE);
                    pat_blasthit_embl=Pattern.compile("(EM.*):(.*);(.*)",Pattern.CASE_INSENSITIVE);
                    //lcl|TR:B1Q3K7_9PRIM  B1Q3K7 Adiponectin OS=Macaca fuscata GN=Adip...    516   0.0   
                    
                    pat_blasthit_embl2=Pattern.compile("(.*)\\|(.*?)\\s{1,}(.*?)\\s{1,}(.*)\\s{1,}([0-9]*|[0-9]*\\.[0-9]*)\\s*([0-9]*.[0-9]*|[0-9]*e-[0-9]*)",Pattern.CASE_INSENSITIVE);
                    
                    pat_bitscore=Pattern.compile("([0-9]*|[0-9]*.[0-9]*).*([0-9]*\\.[0-9]*|[0-9]*e-[0-9]*)");
                    //--EM_PAT:EA066503; EA066503 Sequence 5 from patent US 7176292.
                   
                    pat_database=Pattern.compile("^Database:(.*)sequences;(.*)total",Pattern.CASE_INSENSITIVE);
                    pat_length= Pattern.compile("Length\\s{0,1}=\\s*([0-9]*)", Pattern.CASE_INSENSITIVE);
                    pat_score= Pattern.compile("Score\\s{0,1}=\\s*([0-9]*|[0-9]*\\.[0-9]*)\\s*bits.*[(]([0-9]*|[0-9]*\\.[0-9]*)[)]", Pattern.CASE_INSENSITIVE);
                    pat_identities=Pattern.compile("Identities\\s{0,1}=\\s*([0-9]*)/([0-9]*)", Pattern.CASE_INSENSITIVE);
                    pat_gap=Pattern.compile("Gaps\\s{0,1}=\\s*([0-9]*)/([0-9]*)", Pattern.CASE_INSENSITIVE);
                    pat_positives=Pattern.compile("Positives\\s{0,1}=\\s*([0-9]*)/([0-9]*)", Pattern.CASE_INSENSITIVE);
                    pat_expect= Pattern.compile("Expect\\s{0,1}=\\s*([0-9]*\\.[0-9]*|[0-9]*e-[0-9]*)", Pattern.CASE_INSENSITIVE);
                    pat_strand= Pattern.compile("Strand\\s{0,1}=\\s*(Plus|Minus)/(Plus|Minus)", Pattern.CASE_INSENSITIVE);
                    pat_query_seq=Pattern.compile("Query\\s{0,1}([0-9]*)\\s*(\\D*)\\s*([0-9]*)$",Pattern.CASE_INSENSITIVE);
                    pat_subject_seq=Pattern.compile("Sbjct\\s{0,1}([0-9]*)\\s*(\\D*)\\s*([0-9]*)$",Pattern.CASE_INSENSITIVE);

                } catch(java.util.regex.PatternSyntaxException e) {Config.log("Error in regex");}
    }


    ////////////////////////////////////////////////////////////////////////////
    /// Main loading function

    /**
     * Load the specified TXT blast file...
     * Note: currently, Ncbi supported, Ebi : partial.
     * @param filename
     * @return
     */
    public boolean load(String filename) {
       //--Extract earch block and pass to the good function
       boolean first_block=true;
       boolean flag_finish=false;
       this.setFilename(filename);

        try {
            BufferedReader in=new BufferedReader(new FileReader(new File(filename)));
            String stri="";
            StringBuilder st=new StringBuilder();
            while(in.ready()) {
                stri=in.readLine();                
                if (stri==null) {
                     stri="";
                     flag_finish=true;
                 }
                if (stri.startsWith(str_query)||flag_finish) {
                    //--CASE 1. First block, getDBinfo
                    if (first_block) {
                        this.extractDBInfo(st.toString());                       
                        st.setLength(0);
                        first_block=false;                        
                    } else {
                    //--CASE 2. Gt the query results
                        //--Extract each results of each query                     
                        this.extractBLastHit(st.toString());
                       //--Save information
                        Blast bhl=new Blast();
                        bhl.setDbname(dbname);
                        bhl.setDbinfo(dbinfo);
                        bhl.setFilename(filename);
                        bhl.setMax_bitscore(max_bitscore);
                        bhl.setMin_bitscore(min_bitscore);
                        bhl.setQueryname(queryname);
                        bhl.setQuerylength(querylength);
                        bhl.blasthit_list.addAll(this.blasthit_list);
                        getList().add(bhl);
                        st.setLength(0);                         
                    }
                }
                if (!stri.isEmpty()) st.append(stri+"\n");
            }
           
            //--Load last block
            if (st.length()>0) {
                Config.log(String.valueOf(st.length()));
                this.extractBLastHit(st.toString());
               //--Save information
                Blast bhl=new Blast();
                bhl.setDbname(dbname);
                bhl.setDbinfo(dbinfo);
                bhl.setFilename(filename);
                bhl.setMax_bitscore(max_bitscore);
                bhl.setMin_bitscore(min_bitscore);
                bhl.setQueryname(queryname);
                bhl.setQuerylength(querylength);
                bhl.blasthit_list.addAll(this.blasthit_list);
                this.getList().add(bhl);
            }
        } catch(Exception e) {e.printStackTrace();}

           if (debug) for (Blast bhl:getList()) {
                 Config.log("Query="+bhl.queryname+" Length="+bhl.querylength);
                 Config.log("Total Hit: "+bhl.getBlasthit_list().size());
                 for (BlastHit b:bhl.getBlasthit_list()) {
                     Config.log(b.toString());
                 }
            }

       return true;
    }

     public boolean loadText_debug(String str) {
        String outfilename=""+this.hashCode();
        Util u = new Util();
        u.open(outfilename);
        u.println(str);
        u.close();
        return this.load(outfilename);
     }


     /**
     * Load the specified TXT blast file...
     * Note: currently, Ncbi supported, Ebi : partial.
     * @param filename
     * @return
     */
    public boolean loadText(String str) {
       //--Extract earch block and pass to the good function
       boolean first_block=true;
       boolean flag_finish=false;
       this.setFilename("");
        try {
            BufferedReader in=new BufferedReader(new StringReader(str));
            String stri="";
            StringBuilder st=new StringBuilder();
            while(in.ready()&&!flag_finish) {
                stri=in.readLine();
                if (stri==null) {
                    stri="";
                    flag_finish=true;
                }
                if (stri.startsWith(str_query)||flag_finish) {
                    //--CASE 1. First block, getDBinfo
                    if (first_block) {
                        this.extractDBInfo(st.toString());
                        st.setLength(0);
                        first_block=false;
                    } else {
                    //--CASE 2. Gt the query results
                        //--Extract each results of each query
                        this.extractBLastHit(st.toString());
                       //--Save information
                        Blast bhl=new Blast();
                        bhl.setDbname(dbname);
                        bhl.setDbinfo(dbinfo);
                        bhl.setFilename(filename);
                        bhl.setMax_bitscore(max_bitscore);
                        bhl.setMin_bitscore(min_bitscore);
                        bhl.setQueryname(queryname);
                        bhl.setQuerylength(querylength);
                        bhl.blasthit_list.addAll(this.blasthit_list);
                        this.getList().add(bhl);
                        st.setLength(0);
                    }
                }
                if (!stri.isEmpty()) st.append(stri+"\n");
            }
            //--Load last block
            if (st.length()>0) {
                Config.log(st.toString());
                this.extractBLastHit(st.toString());
               //--Save information
                Blast bhl=new Blast();
                bhl.setDbname(dbname);
                bhl.setDbinfo(dbinfo);
                bhl.setFilename(filename);
                bhl.setMax_bitscore(max_bitscore);
                bhl.setMin_bitscore(min_bitscore);
                bhl.setQueryname(queryname);
                bhl.setQuerylength(querylength);
                bhl.blasthit_list.addAll(this.blasthit_list);
                this.getList().add(bhl);
            }       
        } catch(Exception e) {e.printStackTrace();return false;}
       if (debug) {
           for (Blast bhl:getList()) {
                 Config.log("Query="+bhl.queryname+" Length="+bhl.querylength);
                 Config.log("Total Hit: "+bhl.getBlasthit_list().size());
                 for (BlastHit b:bhl.getBlasthit_list()) {
                     Config.log(b.getString());
                 }
            }
       }
        return true;
    }

    private boolean extractBLastHit(String block) {
        boolean flag_query=false;
        boolean flag_length=false;
        boolean flag_insequence=false;        
        boolean flag_infasta=false;
        boolean flag_inalignment=false;
        boolean flag_finish=false;
        try {
             String buffer=""; //--Buffer for sequence info
            this.setFilename(filename);
            getBlasthit_list().clear();
            BlastHit tmp=null;  //current blasthit
            BlastHit blasthit_buffer=new BlastHit();
            String stri="";
            BufferedReader in2=new BufferedReader(new StringReader(block));
            while (in2.ready()&&!flag_finish) {
                  stri=in2.readLine();
                  if (stri==null) {
                      flag_finish=true;
                      stri="";
                  }
                  stri=stri.trim();
                  //Config.log(stri);
                    if (stri.startsWith(str_query)) flag_query=true;
                    if (stri.startsWith(str_length)) flag_length=true;
                    if (stri.startsWith(str_insequence)) {
                        flag_query=false;
                        flag_insequence=true;
                        //--skip 1 line - Not needed - Etienne
//                        in2.readLine();
//                        stri=in2.readLine();
                    }
                   
                    if (stri.startsWith(">")||stri.startsWith("ALIGNMENTS")) {
                        flag_insequence=false;
                        flag_infasta=true;
                        flag_inalignment=false;
                    }
                    
                    //--Description of query
                    if (flag_query&&!flag_infasta) {
                            buffer+=stri;
                            //Found length
                            if (flag_length) {
                              try {
                                    Matcher m=pat_query.matcher(buffer);
                                    //--Normal                                    
                                    if (m.find()) {
                                        //--Save current blasthit_lost                                        
                                        this.setQueryname(m.group(1).trim());
                                        this.setQuerylength(m.group(2).trim());                                                                                
                                        flag_query=false;
                                    } else {
                                    //--EMBL
                                        m=pat_query_embl.matcher(buffer);
                                         if (m.find()) {                                                                                         
                                            this.setQueryname(m.group(1).trim());
                                            this.setQuerylength(m.group(2).trim());                                            
                                            flag_query=false;
                                        }
                                    }                                    
                                } catch(Exception e) {}
                              buffer="";
                            }
                    }

                    //--Resume of blast hit found
                    if (flag_insequence) {
                        if (!stri.isEmpty()) {
                            tmp=new BlastHit();
                            //--Ncbi, etc...                           
                                try {
                                     Matcher m=pat_blasthit.matcher(stri);
                                     Matcher m2=pat_blasthit2.matcher(stri);
                                     Matcher m3=pat_blasthit_embl.matcher(stri);
                                     Matcher m4=pat_blasthit_embl2.matcher(stri);

                                     boolean m1m=m.find();
                                     boolean m2m=m2.find();
                                     boolean m3m=m3.find();
                                     boolean m4m=m4.find();
                                     
                                     if (m1m&&m.groupCount()>3) {
                                         System.out.println("1");
                                        tmp.setSubject_accession_referree(m.group(1).trim());
                                        tmp.setSubject_accession(m.group(2).trim());
                                        tmp.setSubject_name(m.group(3).trim());

                                        m=pat_bitscore.matcher(stri.substring(tmp.getSubject_name().length()+tmp.getSubject_accession_referree().length()+tmp.getSubject_accession().length()));
                                        //--manual load info
                                        loadBlastHit(tmp, stri);
    //                                    tmp.bitscore=Double.valueOf(m.group(1));
    //                                    tmp.evalue=Double.valueOf(m.group(2));
                                        getBlasthit_list().add(tmp);
                                    }  else if (m4m) {   
                                             
                                           // embl=true;
                                            tmp.setSubject_accession_referree(m4.group(1));
                                            tmp.setSubject_accession(m4.group(3));
                                            tmp.setSubject_name(m4.group(2).trim()+" "+ tmp.getSubject_accession()+" "+m4.group(4).trim());
                                            //--manual load info
                                            //System.out.println(stri);
                                            loadBlastHit(tmp, stri);
                                            getBlasthit_list().add(tmp);
                                      } else if (m2m&&m2.groupCount()>1){
                                        
                                        tmp.setSubject_accession_referree(m2.group(1).trim());
                                        tmp.setSubject_name(m2.group(2).trim());
                                        m=pat_bitscore.matcher(stri.substring(tmp.getSubject_name().length()+tmp.getSubject_accession_referree().length()+tmp.getSubject_accession().length()));
                                        //--manual load info
                                        loadBlastHit(tmp, stri);
    //                                    tmp.bitscore=Double.valueOf(m.group(1));
    //                                    tmp.evalue=Double.valueOf(m.group(2));
                                        getBlasthit_list().add(tmp);
                                        } else if (m3m) {
                                            
                                            embl=true;
                                            tmp.setSubject_accession_referree(m3.group(1));
                                            tmp.setSubject_accession(m3.group(2));
                                            tmp.setSubject_name(m3.group(3).trim());
                                            //--manual load info
                                            loadBlastHit(tmp, stri);
                                            getBlasthit_list().add(tmp);
                                        } 
                                        else
                                        {
                                           
                                            if (debug) Config.log("not found="+stri+" "+m1m+"\t"+m2m+"\t"+m3m+"\t"+m4m+"\t");
                                        }

                                } catch(Exception e) {if (debug) e.printStackTrace();}                           
                        } //else if (dbname.startsWith("embl")) flag_insequence=false;
                    }
                    //--Description of blast hit
                    if (flag_infasta&&!buffer.isEmpty()&&flag_inalignment) {
                        this.loadInfo(buffer, blasthit_buffer);
                        blasthit_buffer=new BlastHit();
                        buffer="";
                    }

                    //--buffer some information
                    if (flag_infasta) {
                        //--Load sequence here
                        if (stri.startsWith("Query")) {
                            Matcher m=pat_query_seq.matcher(stri);
                            if (m.find()) {
                                if (blasthit_buffer.getQstart()==0) blasthit_buffer.setQstart((int) Integer.valueOf(m.group(1)));
                                    blasthit_buffer.setQend((int) Integer.valueOf(m.group(3)));
                                    blasthit_buffer.query_sequence+=(m.group(2).trim());
                            }
                            flag_inalignment=true;
                        }
                        else if (stri.startsWith("Sbjct")) {
                            Matcher m=pat_subject_seq.matcher(stri);
                            if (m.find()) {
                                if (blasthit_buffer.getSstart()==0) blasthit_buffer.setSstart((int) Integer.valueOf(m.group(1)));
                                    blasthit_buffer.setSend((int) Integer.valueOf(m.group(3)));
                                    blasthit_buffer.subject_sequence+=(m.group(2).trim());
                            }
                            //flag_inalignment=false;
                        } else if (stri.isEmpty()) {}
                        else if (!flag_inalignment) buffer+=stri+" ";

                    }
                }
                in2.close();
                //------------------------------------------------------------------
               
            //--calculate min - max bitsocre
            try {
            this.setMax_bitscore(this.getBlasthit_list().get(0).getBitscore());
            this.setMin_bitscore(this.getBlasthit_list().get(this.getBlasthit_list().size() - 1).getBitscore());
            } catch(Exception e) {}
           } catch(Exception e) {e.printStackTrace();}
           if (debug) {
               Config.log("ExtractDBExtract: "+block);
               //Config.log("Total hits="+this.getBlasthit_list().size()+" max score: "+this.getMax_bitscore()+" min score: "+this.getMin_bitscore());
               //for (BlastHit b:getBlasthit_list()) Config.log(b);
               //Config.log("Finish ExtractBlastHit: "+block);
           }
        return true;
    }

    private boolean extractDBInfo(String block) {
    boolean flag_indatabase=false;
    boolean flag_finish=false;
        try {
        getBlasthit_list().clear();
        BlastHit tmp=null;  //current blasthit
        BlastHit blasthit_buffer=new BlastHit();
        String stri="";
            BufferedReader in2=new BufferedReader(new StringReader(block));
                    while (in2.ready()&&!flag_finish) {
                    stri=in2.readLine();
                    if (stri==null) {
                        flag_finish=true;
                        stri="";
                    }
                    if (stri.startsWith(str_indatabase)) flag_indatabase=true;
                    //--Database
                    if (flag_indatabase) {
                        //--Database id
                        if (stri.startsWith(str_indatabase)) {
                            setDbname(getDbname() + stri.substring(str_indatabase.length() + 1));
                        } else if (!stri.isEmpty()){
                            setDbinfo(getDbinfo() + stri.trim());
                        } else {
                          //--Not anymore in database
                            if (debug) Config.log("Database="+getDbname()+"\t"+getDbinfo()+"\t");
                            flag_indatabase=false;
                        }
                    }
                }
                  
           } catch(Exception e) {e.printStackTrace();}
           if (debug) Config.log("ExtractDBInfo block: "+block);
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Internal function in loading blasthit

    /**
     * This load the bitscore, and evalue of a blasthit from the string...
     * @param b
     * @param s
     */
    private void loadBlastHit(BlastHit b, String s) {
        String[] str=s.split(" ");
        boolean next_is_bitscore=false;
        for (int i=str.length-1;i>-1; i--) {
            str[i]=str[i].trim();
            if (!str[i].isEmpty()) {
                if (next_is_bitscore) {
                        b.setBitscore((double) Double.valueOf(str[i]));
                   return;
                } else {
                        b.setEvalue((double) Double.valueOf(str[i]));
                    next_is_bitscore=true;
                }
            }
        }
    }

    /**
     * This load the information found in the alignment description line
     * Note: Required the blasthit_buffer containing the query start, query_end...
     * @param buffer
     * @param blasthit_buffer
     */
    private void loadInfo(String buffer, BlastHit blasthit_buffer) {
      BlastHit tmp;
        if (!buffer.trim().isEmpty()) {
            String str[]=buffer.toString().split("\\x7C");
            if (str.length==1) {
                str=buffer.toString().split(" ");
                //System.out.println(Util.toString(str));
            }
            int index=0;
            int index_end=0;
            String accession="";
            String full="";
            //System.out.println(Util.toString(str));
            //System.out.println(str.length);
            
            
            if (str.length>=3) {
                //NCBI FORMAT
                accession=str[1].trim();
                full=str[2].trim();
            } else if (str.length==2) {
               //--EBI FORMAT   
                String[] stri2=str[1].split(" ");                               
                 accession=stri2[1].trim();
                 full=str[1].trim();
            } else {
                 full=buffer.substring(1, buffer.indexOf(this.str_length)).trim();                
            }
            //------------------------------------------------------
            // Process each hit here
            //--debug System.out.println(accession+"\n"+full);
            if ((index=indexOf(accession, full))!=-1) {               
                tmp=this.getBlasthit_list().get(index);
                //--Put query info
                tmp.setSubject_accession(accession);
                tmp.setQuery_name(this.getQueryname().trim());

                index_end=(full.indexOf(str_length));
                if (index_end>full.length()||index_end<0) index_end=full.length()-1;
                tmp.setSubject_name(full.substring(0, index_end).trim());
                //--Score
                try {
                    Matcher m=pat_score.matcher(full);
                    if (m.find()) {
                        tmp.setBitscore((double) Double.valueOf(m.group(1)));
                        tmp.setScore((double) Double.valueOf(m.group(2)));
                    }
                } catch(Exception e) {if (debug) e.printStackTrace();}
                //--Expect (evalue)
                try {
                    Matcher m=pat_expect.matcher(full);
                    if (m.find()) tmp.setEvalue((double) Double.valueOf(m.group(1)));
                } catch(Exception e) {if (debug) e.printStackTrace();}
                //--Identity
                try {
                    Matcher m=pat_identities.matcher(full);
                    if (m.find()) {
                        tmp.setIdentity((float) Float.valueOf(m.group(1)));
                        tmp.setAlignment_length((float) Float.valueOf(m.group(2)));
                    }
                } catch(Exception e) {if (debug) e.printStackTrace();}
                 //--Subject Length
                try {
                    Matcher m=pat_length.matcher(full);
                    if (m.find()) {tmp.setSubject_length((float) Float.valueOf(m.group(1)));
                    }
                } catch(Exception e) {if (debug) e.printStackTrace();}
                //--Gaps
                try {
                    Matcher m=pat_gap.matcher(full);
                    if (m.find()) {tmp.setGap((float) Float.valueOf(m.group(1)));}
                } catch(Exception e) {if (debug) e.printStackTrace();}
                //--Positives
                try {
                    Matcher m=pat_positives.matcher(full);
                    if (m.find()) {tmp.setPositives((float) Float.valueOf(m.group(1)));}
                } catch(Exception e) {if (debug) e.printStackTrace();}
                //--Strands
                try {
                    Matcher m=pat_strand.matcher(full);
                    if (m.find()) {
                        tmp.setQstrand(m.group(1));
                        tmp.setSstrand(m.group(2));
                    }
                } catch(Exception e) {if (debug) e.printStackTrace();}
                //this.blasthit_list.add(tmp);
                if (debug) Config.log(tmp.toString());
                //--Clear buffer
                buffer="";
                tmp.query_sequence=blasthit_buffer.query_sequence;
                tmp.subject_sequence=blasthit_buffer.subject_sequence;
                tmp.setQstart(blasthit_buffer.getQstart());
                tmp.setQend(blasthit_buffer.getQend());
                tmp.setSstart(blasthit_buffer.getSstart());
                tmp.setSend(blasthit_buffer.getSend());                
            }
        }
    }


    /**
     * This return the blasthit index
     * @param
     * @return the index or -1 if not found
     */
    public int indexOf(String subject_accession, String subject_name) {
        BlastHit b=new BlastHit(); //--Not, blasthit are found by accession
        b.setSubject_accession(subject_accession);
        b.setSubject_name(subject_name);
        //--Note: name is everything... including sequence info...
       
        if (debug) Config.log(subject_name);
       // System.out.println(subject_name+"\t"+subject_accession);
        int index=this.getBlasthit_list().indexOf(b);
            if (index!=-1) return index;
            for (BlastHit bh:this.getBlasthit_list()) {
                if (bh.getQuery_name().startsWith(subject_name)) return index;
                if (bh.getSubject_accession().startsWith(subject_accession)) return index;
                //if (bh.getSubject_accession().startsWith(subject_accession)) return index;
                index++;
            } 

         if (debug) Config.log("Name not found in previous blasthit?...");
         return -1;
    }

    
   /**
    * Search this blastlist 
    * @return A vector of the Index position of Matching result in the blast_list Array
    */
    public Vector<Integer> search (String regex, int mode) {
        Vector<Integer> returnArray = new Vector<Integer>();
        Pattern p;
        try {
            p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        } catch(java.util.regex.PatternSyntaxException e) {return returnArray;}
        float search_len=0;
        try {
            search_len=Float.valueOf(regex);
        } catch (Exception e) {search_len=0;}
        switch (mode) {
            case MODE_ID:       lastSearch="Id with: "+regex;
                                for (int i=0; i<this.getBlasthit_list().size();i++) {
                                      BlastHit data=this.getBlasthit_list().get(i);
                                      Matcher m = p.matcher(data.getSubject_id_gi());
                                      if (m.find()) returnArray.add(i);
                                }
                                break;
            case MODE_ACCESSION:lastSearch="Accession with: "+regex;
                                for (int i=0; i<this.getBlasthit_list().size();i++) {
                                     BlastHit data=this.getBlasthit_list().get(i);
                                      Matcher m = p.matcher(data.getSubject_accession());
                                      if (m.find()) returnArray.add(i);
                                }
                                break;
            case MODE_DESC:  lastSearch="Description with: "+regex;
                                for (int i=0; i<this.getBlasthit_list().size();i++) {
                                      BlastHit data=this.getBlasthit_list().get(i);
                                       Matcher m = p.matcher(data.getSubject_name());
                                      if (m.find()) returnArray.add(i);
                                }
                                break;
            case MODE_LENMORE: lastSearch="Len(bp) greater: "+regex;
                                for (int i=0; i<this.getBlasthit_list().size();i++) {
                                    BlastHit data=this.getBlasthit_list().get(i);
                                    try {

                                        if (data.getSubject_length()>=search_len) returnArray.add(i);
                                    } catch(Exception e) {}
                                }
                                break;
             case MODE_LENLESS: lastSearch="Len(bp) less: "+regex;
                                for (int i=0; i<this.getBlasthit_list().size();i++) {
                                    BlastHit data=this.getBlasthit_list().get(i);
                                    try {
                                        if (data.getSubject_length()<=search_len) returnArray.add(i);
                                    } catch(Exception e) {}
                                }
                                break;
             case MODE_MAXBITSCORE: lastSearch="MAX BITSCORE: "+regex;
                                BlastHit tmp=null;
                                for (int i=0; i<this.getBlasthit_list().size();i++) {
                                    BlastHit data=this.getBlasthit_list().get(i);
                                    if (tmp==null) {
                                        tmp=data;
                                    } else
                                    try {
                                        if (tmp.getBitscore()<data.getBitscore()) tmp=data;
                                    } catch(Exception e) {}
                                }
                                returnArray.add(this.getBlasthit_list().indexOf(tmp));
                                break;
            case MODE_EVALUELESS:lastSearch="Evalue less: "+regex;
                                for (int i=0; i<this.getBlasthit_list().size();i++) {
                                    BlastHit data=this.getBlasthit_list().get(i);
                                    try {
                                        if (data.getEvalue()<=search_len) returnArray.add(i);
                                    } catch(Exception e) {}
                                }
                                break;
            case MODE_EVALUEMORE:lastSearch="Evalue mode: "+regex;
                                for (int i=0; i<this.getBlasthit_list().size();i++) {
                                    BlastHit data=this.getBlasthit_list().get(i);
                                    try {
                                        if (data.getEvalue()>=search_len) returnArray.add(i);
                                    } catch(Exception e) {}
                                }
                                break;
            case MODE_ALL:      lastSearch="All with: "+regex;
                                for (int i=0; i<this.getBlasthit_list().size();i++) {
                                    BlastHit data=this.getBlasthit_list().get(i);
                                    Matcher m = p.matcher(data.toString());
                                    if (m.find()) returnArray.add(i);
                                }
        } //end switch
        if (debug) Config.log("Searching for "+lastSearch+"\n found "+returnArray.size()+" result(s)");
        return returnArray;
      }

    /**
     * @return the dbname
     */
    public String getDbname() {
        return dbname;
    }

    /**
     * @param dbname the dbname to set
     */
    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    /**
     * @return the dbinfo
     */
    public String getDbinfo() {
        return dbinfo;
    }

    /**
     * @param dbinfo the dbinfo to set
     */
    public void setDbinfo(String dbinfo) {
        this.dbinfo = dbinfo;
    }

    /**
     * @return the queryname
     */
    public String getQueryname() {
        return queryname;
    }

    /**
     * @param queryname the queryname to set
     */
    public void setQueryname(String queryname) {
        this.queryname = queryname;
    }

    /**
     * @return the querylength
     */
    public String getQuerylength() {
        return querylength;
    }

    /**
     * @param querylength the querylength to set
     */
    public void setQuerylength(String querylength) {
        this.querylength = querylength;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the max_bitscore
     */
    public double getMax_bitscore() {
        return max_bitscore;
    }

    /**
     * @param max_bitscore the max_bitscore to set
     */
    public void setMax_bitscore(double max_bitscore) {
        this.max_bitscore = max_bitscore;
    }

    /**
     * @return the min_bitscore
     */
    public double getMin_bitscore() {
        return min_bitscore;
    }

    /**
     * @param min_bitscore the min_bitscore to set
     */
    public void setMin_bitscore(double min_bitscore) {
        this.min_bitscore = min_bitscore;
    }

    /**
     * @return the blasthit_list
     */
    public LinkedList<BlastHit> getBlasthit_list() {
        return blasthit_list;
    }

    /**
     * @param blasthit_list the blasthit_list to set
     */
    public void setBlasthit_list(ArrayList<BlastHit> blasthit_list) {
        this.setBlasthit_list(blasthit_list);
    }

    public static void ouputBetterBlastFile(String filename, String output) {
    //--Load blast file
    blastParser b=new blastParser(filename);
    //--Create output
    Util u=new Util();
    u.open(output);
    u.println("#Query");
    u.println(b.getQueryname()+"\t"+b.getQuerylength()+"\t"+b.getDbname()+"\t"+b.getDbinfo());
    u.println("subject_name"+"\t"+"subject_accession"+"\t"+"bitscore"+"\t"+"evalue"+"\t"+"gap"+"\t"+"identity"+"\t"+"missmatches"+"\t"+"query_length"+"\t"+"subject_length");
    for (BlastHit bh:b.getBlasthit_list()) {
        String s=bh.getSubject_name()+"\t"+bh.getSubject_accession()+"\t"+bh.getBitscore()+"\t"+bh.getEvalue()+"\t"+bh.getGap()+"\t"+bh.getIdentity()+"\t"+bh.getMissmatches()+"\t"+bh.getQuery_length()+"\t"+bh.getSubject_length();
        u.println(s);
    }
    u.close();
}

    /**
     * @return the list
     */
    public Vector<Blast> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(Vector<Blast> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        String str="";
        BlastHit dummy=new BlastHit();
        for (Blast bhl:getList()) {
                 str+="Query="+bhl.queryname+" Length="+bhl.querylength+"\n";
                 str+="Total Hit: "+bhl.getBlasthit_list().size()+"\n";
                 str+=dummy.getToStringParam()+"\n"; //--Id of column
                for (BlastHit b:bhl.getBlasthit_list()) {
                             str+=b.getString()+"\n";
                         }

            }
        return str;
    }


    public String toStringMinimum() {
        String str="";
        for (Blast bhl:getList()) {
                 str+="Query="+bhl.queryname+" Length="+bhl.querylength+"\n";
                 str+="Total Hit: "+bhl.getBlasthit_list().size()+"\n";
            }
        return str;
    }

    public int getTotalHits() {
        int count=0;
        for (Blast bhl:getList()) {
             count+=bhl.getBlasthit_list().size();
            }
        return count;
    }
}
