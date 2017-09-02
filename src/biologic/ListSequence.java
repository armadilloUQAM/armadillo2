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

package biologic;

import configuration.Config;
import configuration.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
/**
 * Sequence List
 * @author Etienne Lord
 * @since Mars 2009
 */
public abstract class ListSequence implements Serializable {
    private static final long serialVersionUID = 200904262L;

    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    
    protected Vector<Sequence> seq=new Vector<Sequence>();
    private int limit_sequence_begin=0;                  //limiter to the what we output to disk
    private int limit_sequence_end=0;                    //limiter to what we output to disk
    private String Evolutionary_Model="";                             //Evolutionnary_model
    
    private BufferedReader file;                  //--The file
    boolean file_open=false;                      //--Status
    int file_current_line_number=0;               //--Current position in file
    String file_current_line="";                   //--Curent read line
    long file_read_length=0;                       //--bytes read to date
    long file_length=0;                            //--total bytes
    public String filename="";                           //--Current filename
    
    ////////////////////////////////////////////////////////////////////////////
    /// Display modifier
    
    public String informationString=""; //--String put below sequences representation 
                                        //  to indicate feature                            
    public int    positionAdjuster=0;   //--Change the numerology of sequences
                                        //  (0 mean that we display 1 as first position)
    
    // Search
    String lastSearch="";
    
    ////////////////////////////////////////////////////////////////////////////
    /// Flag and mode
    
    public static final int MODE_ID=0;
    public static final int MODE_ACCESSION=1;
    public static final int MODE_DESC=2;
    public static final int MODE_ALIASES=3;
    public static final int MODE_ALL=4;
    public static final int MODE_LENMORE=6;
    public static final int MODE_LENLESS=7;
    public static final int MODE_SEQUENCE=8;

    public static int modePhylip=0;
    //Mode Phylip
    public static final int modePhylip_beginning=0; //On prend le début du nom
    public  static final int modePhylip_end=1;       //On prend la fin du nom
    public static final int modePhylip_Accesion=2;  //On cherche un numero d'accession
    public  static final int modePhylip_LongName=3;  //We put the whole name

    private int max_len=0;                         //Max sequences length
    private int max_seq_name_len=0;                // Max sequence name length
    protected String name="Unknown";               // Default name
    
    ////////////////////////////////////////////////////////////////////////////
    //Debug
    private static boolean debug=false;
    public static Pattern regex_clustalw2;
    public static Pattern regex_clustalw;
    public static Pattern regex_quality;
    public static Pattern remove_backet;
    public static Pattern seq_size_artefact;

////////////////////////////////////////////////////////////////////////////////
/// CONSTRUCTOR

    public ListSequence() {
        if (regex_clustalw2==null) regex_clustalw2=Pattern.compile("^(\\w*)\\s{2,}(\\D*)\\s{1,}([0-9]+)");
        if (regex_clustalw==null) regex_clustalw=Pattern.compile("^(\\w*)\\s{2,}(\\D*)");
        if (regex_quality==null) regex_quality=Pattern.compile("[*]");
        if (remove_backet==null) remove_backet=Pattern.compile("(.*)\\[.*\\]", Pattern.CASE_INSENSITIVE);
        if (seq_size_artefact==null) seq_size_artefact=Pattern.compile("(.*)\\s[0-9]*+\\s+[bp|kb|aa]",Pattern.CASE_INSENSITIVE);
    } //Just in case someone need to override

 ////////////////////////////////////////////////////////////////////////////////
/// FUNCTIONS

 /////////////////////////////////////////////////////////////////////////
     // File Functions

       public void open(String filename)  {
           this.setFilename(filename);
           try {
                File tmp=new File(filename);
                file=new BufferedReader(new FileReader(tmp));
                if (file!=null) {
                    file_length=tmp.length();
                    file_open=true;
                    file_current_line_number=0;
                }
           } catch(Exception e) {Config.log("*** Error: Unable to open "+filename);file_open=false;}
       }

       public void close() {
           try {
                if (file_open) {
                    file.close();
                    file_open=false;
                }
           } catch(Exception e){Config.log("*** Error: Unable to close "+getFilename());}
       }

       public void reopen() {
           open(getFilename());
       }


      /**
       * get the Next Sequence in the MultipleSequences files
       * @return the next mafcontig or null if not found
       */
       public Sequence getNextSequence() {
           boolean sequenceMode=false;
           try {
                if (!file_open) return null;
                Sequence tmp = new Sequence();                        //Temp sequence
                StringBuilder tmpsequence=new StringBuilder();        //Temp sequence string

                while(file.ready()) {
                     if (sequenceMode&&(file_current_line.equals("")||file_current_line.startsWith(">"))) {
                          tmp.setSequence(tmpsequence.toString());
                          tmp.loadInfoFromName();
                          tmpsequence=new StringBuilder();
                          sequenceMode=false;
                          return tmp;
                      }
                      if (sequenceMode) {
                          tmpsequence.append(file_current_line);
                      }
                      if (file_current_line.startsWith(">")) {
                            //We have a fasta definition
                            tmp.setName(file_current_line.substring(1)); //remove >
                            sequenceMode=true;
                      }
              file_current_line=file.readLine();
              this.file_read_length=file_current_line.length();
              file_current_line_number++;
          } //end while
           close(); //close MAF file
           return null;
          } catch(Exception e) {if (isDebug()) e.printStackTrace();return null;}
       }

       public boolean hasNextSequence() {
           return file_open;
       }

////////////////////////////////////////////////////////////////////////////////

  /**
   * Use the MAF Structure instead
   * @param filename
   * @return
   */
    @Deprecated
   public boolean readSequenceFromMaf (String filename) {
       this.name=filename;
       try {
           BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
           boolean inContig=false;
           StringBuilder sequence=new StringBuilder();  //Temporary sequence
           String orientation="";
           String desc="";   //Description :: name de la sequence
           int position=0;  //position de la sequence en cours
            //REad the number of line in file
            //On lit toute les ligne
            //HACK: On tient juste compe de la sequence finamenent
            getSequences().clear(); //On vide la liste de sequence
            while (br.ready()) {
                String stri=br.readLine();
                if (stri.startsWith("s")) {
                    String [] striv=stri.split(" ");
                    for (int i=0; i<striv.length;i++) striv[i]=striv[i].trim(); //remove space
                    int index=1;
                    desc=striv[index].toUpperCase();
                    for (int i=2;i<striv.length;i++) {
                        if (striv[i].equals("+")||striv[i].equals("-")) {
                            index=i;
                            break;
                        }
                    }
                    orientation=striv[index];
                    sequence.append(striv[striv.length-1]);
                    Sequence tmp = new Sequence();
                    tmp.setName(desc);
                    tmp.setOrientation(orientation);
                    tmp.setSequence(sequence.toString());
                    getSequences().add(tmp);
                 }
               } //End while
           br.close();
       } catch(Exception e) {return false;}
       return true;
   } //end readSeqFromMaf

   
   public boolean readSequenceFromClustalW2_old(String filename) {
       this.name=filename;
       try {
         seq.clear(); //Clear the sequence vector
         BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
         String stri="";     //line buffer
         int line_to_skip=3; //line to skip before reading
         //debug
         int count=0;         //Number of line

         if (isDebug()) Config.log("Reading ClustalW2:"+filename+":");
         while (br.ready()) {
              stri=br.readLine();
              // We skip 3 line of description
              if (line_to_skip>0) {
//                  if (stri.toUpperCase().indexOf("CLUSTALW")<0) {
//                      if (debug) Config.log("not clustalw...");
//                      return false;
//                  }
                  line_to_skip--;
              } else {   
                 if (stri.indexOf("*")>-1||stri.startsWith(" ")||stri.equals("")) {
                  //If we find * we * we skip
                  } else {
                  int indexspace=stri.indexOf(' ');
                  String sname=stri.substring(0, indexspace).trim();
                  int index=findIndex(sname);
                  if (index==-1) {
                      Sequence tmp=new Sequence();
                      tmp.setName(sname);
                      seq.add(tmp);
                  }
                  index=findIndex(sname);
                  //we have to check for trailing number!!!
                  int pos=stri.length();
                  if (stri.lastIndexOf(" ")>36) pos=stri.lastIndexOf(" ");
                      Sequence tmp=seq.get(index);
                      //Note :: we have a pointer to the sequence!!!
                      //HACK
                      tmp.addToSequence(stri.substring(indexspace,pos).trim());
                      count++;
                      if (isDebug()&&count%10000==0) Config.log("*");
                      //tmp.setSequence(tmp.getSequence()+stri.substring(16,pos).trim());
                  }
              }
          }//end while
          br.close();
          if (seq.size()==0) {
              if (isDebug()) Config.log("not clustalw...");
              return false;
          }
      } catch(Exception e) {return false;}
   return true;
   }

   public boolean readSequenceFromClustalW2(String filename) {
       this.name=filename;
       boolean clustalw2=false;
       try {
         seq.clear(); //Clear the sequence vector
         BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
         String stri="";     //line buffer
         int line_to_skip=3; //line to skip before reading
         //debug
         int count=0;         //Number of line

         if (isDebug()) Config.log("Reading ClustalW2:"+filename+":");
         while (br.ready()) {
              stri=br.readLine();
              // We skip 3 line of description
              if (line_to_skip>0) {//                 
                  line_to_skip--;
              } else {
                 Matcher match=regex_clustalw2.matcher(stri);   
                 Matcher match2=regex_clustalw.matcher(stri);
                 Matcher quality=regex_quality.matcher(stri);
                 if (quality.find()) {
                     //This is not important at the moment...
                 } else if (match.find()) {
                     String sname=match.group(1);                     
                     int index=findIndex(sname);
                     if (index==-1) {
                          Sequence tmp=new Sequence();                          
                          tmp.setName(sname);                         
                          seq.add(tmp);
                      }
                      index=findIndex(sname);
                      //we have to check for trailing number!!!
                      Sequence tmp=seq.get(index);
                      //Note :: we have a pointer to the sequence!!!
                      //HACK
                      //Config.log(match.group(2)+"*");
                      tmp.addToSequence(match.group(2));
                      count++;
                      
                      //tmp.setSequence(tmp.getSequence()+stri.substring(16,pos).trim());
                  } else if (match2.find()) {
                       String sname=match2.group(1);                     
                     int index=findIndex(sname);
                     if (index==-1) {
                          Sequence tmp=new Sequence();
                          tmp.setName(sname);                         
                          seq.add(tmp);
                      }
                      index=findIndex(sname);
                      //we have to check for trailing number!!!
                      Sequence tmp=seq.get(index);
                      //Note :: we have a pointer to the sequence!!!
                      //HACK
                      //Config.log(match.group(2)+"*");
                      tmp.addToSequence(match2.group(2));
                      count++;
                  }

                 if (isDebug()&&count%10000==0) Config.log("*");
              }
          }//end while
          br.close();
          if (seq.size()==0) {
              Config.log("not clustalw...");
              return false;
          } 
      } catch(Exception e) {e.printStackTrace();return false;}
   
   return true;
   }

   public boolean readSequenceFromNexus(String filename) {
      this.name=filename;
       try {
         seq.clear(); //Clear the sequence vector
         BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
         String stri="";
         boolean begin = false;
         int index=0;
         //Read the file in a buffer
         while (br.ready()) {
              stri=br.readLine();
              //remove tab tab
               stri=stri.replaceAll("\t", " ");
               stri=stri.trim();
              if (begin) {
                 if (stri.indexOf("*")>-1||stri.startsWith(" ")||stri.equals("")) {
                  //If we find * we * we skip
                  } else {
                  String name=stri.substring(0, 16).trim();
                  index=findIndex(name);
                  if (index==-1) {
                      Sequence tmp=new Sequence();
                      tmp.setName(name);
                      seq.add(tmp);
                  }
                  index=findIndex(name);
                  //we have to check for trailing number!!!
                  int pos=stri.length();
                  if (stri.lastIndexOf(" ")>36) pos=stri.lastIndexOf(" ");
                      Sequence tmp=seq.get(index);
                      //Note :: we have a pointer to the sequence!!!
                      //HACK
                      tmp.addToSequence(stri.substring(16,pos).trim());
                      //tmp.setSequence(tmp.getSequence()+stri.substring(16,pos).trim());
                  }
              } // end begin true
              if (stri.startsWith("matrix")) {
                  begin=true;
              }
          } //End while
          br.close();
      } catch(Exception e) {return false;}
   return true;   
   }

   public boolean readSequenceFromPhylip(String filename) {
      this.name=filename;
       //WARNING: DOESN'T HANDLE MULTIPHYLIP
      try {
         seq.clear(); //Clear the sequence vector
         BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
         String stri="";     //line buffer
         int line_to_skip=1; //line to skip before reading
                            //skip first line phylip number
         int index=0;      //Index de la ligne so of sequence
         int numberSeq=0;  //index numbre sequence
         if (isDebug()) Config.log("Reading Phylip:"+filename+":");
         while (br.ready()) {
              stri=br.readLine();
              if (line_to_skip>0) {
                  Scanner sc=new Scanner(stri);
                  try {
                  int seq_number=sc.nextInt();
                  int seq_len=sc.nextInt();
                  } catch(Exception e2) {
                    if (isDebug()) Config.log("not phylip...");
                    return false;
                  }
                  line_to_skip--;
              } else {
                   
                    //CASE 1: Ligne non null :: Donc sequence identifié ou non
                    if (!stri.equals("")||stri.startsWith("#")) {
                        if (stri.startsWith(" ")) {
                            //CASE 2: We have a sequence line add to appropriate sequence
                            Sequence tmp=seq.get(index);
                            //Note :: we have a pointer to the sequence!!!
                            //HACK
                            tmp.addToSequence(stri.replaceAll(" ",""));
                            //tmp.setSequence(tmp.getSequence()+stri.replaceAll(" ",""));
                            //increase sequence index
                            index++;
                            if (index>numberSeq) index=0; //Security but should never append!!!
                        } else {
                            //CASE 3: Not a start of line so new sequence
                            String sname=stri.substring(0,10); //Normal phylip file
                            String sequ=stri.substring(10).trim();
                            sequ=sequ.replaceAll(" ",""); //remove space
                            sname=sname.trim();
                            Sequence tmp=new Sequence();
                            tmp.setSequence(sequ);
                            tmp.setName(sname);
                            seq.add(tmp);
                            numberSeq++;
                        }
                      } else {
                        //CASE 4: empty line! Not finished... reset sequence index
                        index=0;
                      }
              }//End line to skip
          } //end while
          br.close();
         if (seq.size()==0) {
              if (isDebug()) Config.log("not phylip...");
              return false;
          }
       } catch(Exception e) {Config.log("Error with "+filename); return false;}
      Config.log("done");
      return true;       
  }

    /**
    * Return the sequence index 
    * @param name
    * @return
    */
   public int findIndex(String name) {
       int index=0;
       for (Sequence s:seq) {
          if (s.getName().equals(name)) return index;
          index++;
       }
       return -1;
   }

    /**
    * This is the new read sequence from fasta,
     * faster and better, and read all file length...
    * @param filename
    * @return
    */
    public boolean readSequenceFromFastaNew(String filename) {
        open(filename);
        seq.clear();
        Config.log("Reading fasta:"+filename+":");
        while(this.hasNextSequence()) {
            Sequence sequence=this.getNextSequence();
            if (sequence!=null) {
                seq.add(sequence);
            }
            //--Counter
            if ((this.file_read_length*100/this.file_length)%5==0) Config.log("*");
        }
        Config.log("done.");
        return true;
    }

   /**
    *
    * @param filename
    * @return
    */
   public boolean readSequenceFromFasta(String filename) {
      //Test if the sequence are to big
//      if (tooBigFasta(filename)) {
//          if (debug) Config.log("Fasta file "+filename+" is too big");
//          return false;
//      }
        this.name=filename;
        try {
            seq.clear(); //Clear the sequence vector and open the file
            BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
            //VARIABLES
            boolean sequenceMode=false;   
            Sequence tmp = new Sequence();                        //Temp sequence
            StringBuilder tmpsequence=new StringBuilder();        //Temp sequence string
            String stri="";                                       //Temp read line
            boolean fastq_info=false;
            //Read the file in a buffer an parse at the same time
            //Process :: We read like a fasta file
            Config.log("Reading fasta:"+filename+":");
            int count=0;
            while (br.ready()) {
                stri=br.readLine();
                //Config.log(stri);
                //--Special tag for fastq
                if (stri.equals("+")) {
                    fastq_info=true;                
                } else if (sequenceMode&&(stri.equals("")||stri.startsWith(">"))) {
                    tmp.setSequence(tmpsequence.toString());
                    tmp.loadInfoFromName();
                    //Add sequence if not empty
                    if (tmp.getSequence().length()>0) seq.add(tmp);
                    tmp=new Sequence();
                    tmpsequence=new StringBuilder();
                    sequenceMode=false;
                } 
                if (sequenceMode&&!fastq_info) {
                    tmpsequence.append(stri);
                    count++;
                    if (count%10000==0) Config.log("*");
                } 
                if (stri.startsWith(">")) {
                    //We have a fasta definition
                    tmp.setName(stri.substring(1)); //remove >
                    sequenceMode=true;
                    fastq_info=false;
                }
            } //end while
            //Add last read
            if (sequenceMode) {
                tmp.setSequence(tmpsequence.toString());
                tmp.loadInfoFromName();
                if (tmp.getSequence().length()>0) seq.add(tmp);
                tmp=new Sequence();
            }
            br.close();
            if (seq.size()==0) {
                if (isDebug()) Config.log("not fasta...");
                return false;
            }
        } catch(Exception e) { if (isDebug()) e.printStackTrace();Config.log("Error with "+filename); return false;}
        if (isDebug()) Config.log("done");
        return true;
    }

    public boolean readFasta(String fasta) {
        Util u=new Util();
        String tmpfilename="tmp"+Util.returnCount();
        u.open(tmpfilename);
        u.println(fasta);
        u.close();
        this.readSequenceFromFasta(tmpfilename);
        u.deleteFile(tmpfilename);
        return true;
    }
  
  
   public boolean readSequenceFromFastq(String filename) {
        this.name=filename;
        try {
            seq.clear(); //Clear the sequence vector and open the file
            BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
            //VARIABLES
            boolean sequenceMode=false;   
            Sequence tmp = new Sequence();                        //Temp sequence
            StringBuilder tmpsequence=new StringBuilder();        //Temp sequence string
            String stri="";                                       //Temp read line

            //Read the file in a buffer an parse at the same time
            //Process :: We read like a fasta file
            Config.log("Reading fastq:"+filename+":");
            int count=0;
            while (br.ready()) {
                stri=br.readLine();
                //Config.log(stri);
                if (sequenceMode&&(stri.equals("")||stri.startsWith("@"))) {
                    tmp.setSequence(tmpsequence.toString());
                    tmp.loadInfoFromName();
                    //Add sequence if not empty
                    if (tmp.getSequence().length()>0) seq.add(tmp);
                    tmp=new Sequence();
                    tmpsequence=new StringBuilder();
                    sequenceMode=false;
                } 
                if (sequenceMode) {
                    tmpsequence.append(stri);
                    count++;
                    if (count%10000==0) Config.log("*");
                } 
                if (stri.startsWith("@")) {
                    //We have a fasta definition
                    tmp.setName(stri.substring(1)); //remove >
                    sequenceMode=true;
                }
            } //end while
            //Add last read
            if (sequenceMode) {
                tmp.setSequence(tmpsequence.toString());
                tmp.loadInfoFromName();
                if (tmp.getSequence().length()>0) seq.add(tmp);
                tmp=new Sequence();
            }
            br.close();
            if (seq.size()==0) {
                if (isDebug()) Config.log("not fasta...");
                return false;
            }
        } catch(Exception e) { if (isDebug()) e.printStackTrace();Config.log("Error with "+filename); return false;}
        if (isDebug()) Config.log("done");
        return true;
    }

    public boolean readPhylip(String phylip) {
        Util u=new Util();
        String tmpfilename="tmp"+Util.returnCount();
        u.open(tmpfilename);
        u.println(phylip);
        u.close();
        readSequenceFromPhylip(tmpfilename);
        u.deleteFile(tmpfilename);
        return true;
    }

   /**
    * We read the sequence but we replace then the sequence Name, Gi, etc.
    * with the correct one
    * @param Vector<Sequence> 
    * @param filename
    * @return True if suceed
    */
   public boolean readSequenceFromFastaWithSynonym(String filename, Vector<Sequence>sequenceName) {
      //Test if the sequence are to big
//      if (tooBigFasta(filename)) {
//          if (debug) Config.log("Fasta file "+filename+" is too big");
//          return false;
//      }
       this.name=filename;
        try {
            seq.clear(); //Clear the sequence vector and open the file
            BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
    
            //VARIABLES
            boolean sequenceMode=false;   
            Sequence tmp = new Sequence();                        //Temp sequence
            StringBuilder tmpsequence=new StringBuilder();        //Temp sequence string
            String stri="";                                       //Temp read line

            //Read the file in a buffer an parse at the same time
            //Process :: We read like a fasta file
            if (isDebug()) Config.log("Reading fasta:"+filename+":");
            int count=0;
            while (br.ready()) {
                stri=br.readLine();
                if (sequenceMode&&(stri.equals("")||stri.startsWith(">"))) {
                    tmp.setSequence(tmpsequence.toString());
                    tmp.loadInfoFromName();
                    //Add sequence
                    seq.add(tmp);
                    tmp=new Sequence();
                    tmpsequence=new StringBuilder();
                    sequenceMode=false;
                }
                if (sequenceMode) {
                    tmpsequence.append(stri);
                    count++;
                    if (isDebug()&&count%10000==0) Config.log("*");
                }
                if (stri.startsWith(">")) {
                    //We have a fasta definition
                    
                    tmp.setName(stri.substring(1)); //remove >
                    sequenceMode=true;
                }

            } //end while
            //Add last read
            if (sequenceMode) {
                tmp.setSequence(tmpsequence.toString());
                tmp.loadInfoFromName();
                seq.add(tmp);
                tmp=new Sequence();
            }
            br.close();
            //Replace tmpname with full name and other annotation

            for (int j=0; j<seq.size();j++) {
                Sequence tmpseq=seq.get(j);
                String tmpname=tmpseq.getName().trim();
                if (tmpname.startsWith("AZ")) {
                    //Get the sequenceNumber
                    Config.log(tmpname+" ");
                    tmpname=tmpname.substring(2);
                    try {
                        int i=Integer.valueOf(tmpname);
                        Config.log(tmpname+" "+i);
                        Sequence seqname=sequenceName.get(i);
                        tmpseq.setName(seqname.getName());
                        tmpseq.setGi(seqname.getGi());
                        tmpseq.setAccession(seqname.getAccession());
                        tmpseq.setAccession_referee(seqname.getAccession_referee());
                    } catch(Exception ex) {}
                }
            }
        } catch(Exception e) {e.printStackTrace();Config.log("Error with "+filename); return false;}
        if (isDebug()) Config.log("done");
        return true;
    }

   /**
    *
    * @param filename
    * @return
    */
   public boolean readSequenceFromPIR(String filename) {
      this.name=filename;
      try {
         seq.clear(); //Clear the sequence vector
         BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
         //VARIABLES
         Sequence tmp = new Sequence();//Temp sequence
         StringBuilder tmpsequence=new StringBuilder();        //Temp sequence string
         String stri="";               //Temp read line
         boolean sequenceMode=false;

         //Read the file and parse
         while (br.ready()) {
              stri=br.readLine();
          
              if (sequenceMode&&(stri.equals("*")||stri.startsWith(">"))) {
                  tmp.setSequence(tmpsequence.toString());
                  tmpsequence=new StringBuilder();
                  //Add sequence
                  seq.add(tmp);
                  tmp=new Sequence();
                  sequenceMode=false;
              }
              if (sequenceMode&&!stri.equals("")) {
                  tmpsequence.append(stri);
              }
              if (stri.startsWith(">DL;")) {
                  //We have a fasta definition
                    tmp.setName(stri.substring(4)); //remove >DL;
                    sequenceMode=true;
              }

          }//End file
          br.close();
          //Add last read
          if (sequenceMode) {
              tmp.setSequence(tmpsequence.toString());
              seq.add(tmp);
          tmp=new Sequence();
          }
          
       } catch(Exception e) {Config.log("Error with "+filename); return false;}
        
      return true;
  }

   /**
    *
    * @param filename
    * @return
    */
   public boolean readSequenceFromGDE(String filename) {
     this.name=filename;
      try {
         seq.clear(); //Clear the sequence vector and open the file
         BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
          boolean sequenceMode=false;
          Sequence tmp = new Sequence();//Temp sequence
          StringBuilder tmpsequence=new StringBuilder();        //Temp sequence string
          String stri="";               //Temp read line
         
         while (br.ready()) {
              stri=br.readLine();
         
              if (sequenceMode&&stri.startsWith("#")) {
                  tmp.setSequence(tmpsequence.toString());
                  tmpsequence=new StringBuilder();
                  //Add sequence
                  seq.add(tmp);
                  tmp=new Sequence();
                  sequenceMode=false;
              }
              if (sequenceMode&&!stri.equals("")) {
                  tmpsequence.append(stri);
              }
              if (stri.startsWith("#")) {
                  //We have a fasta definition
                    tmp.setName(stri.substring(1)); //remove >DL;
                    sequenceMode=true;
              }
          }
          br.close(); //Close the file
          //Add last read
          if (sequenceMode) {
              tmp.setSequence(tmpsequence.toString());
              seq.add(tmp);
          tmp=new Sequence();
          }
          
       } catch(Exception e) {Config.log("Error with "+filename); return false;}
        
      return true;
  }


    public boolean outputPhylip(String filename) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
           
            //Hack, ideally we must compute the exact number
            pw.print(this.getNbSequence()+"        "+this.getSequenceSize()+"\n");
            for (Sequence s:seq) {
                String name="";
                if (modePhylip==modePhylip_beginning) {
                    name=s.getName()+"             *";
                    name=name.substring(0,9);
                } else 
                    if (modePhylip==modePhylip_end) {
                    name=s.getName();
                        if (name.length()>9) {
                            name=name.substring(name.length()-9, name.length());
                        } else {
                            name+="           *";
                            name=name.substring(0,9);
                        }
                }  else 
                    if (modePhylip==modePhylip_Accesion) {
                    name=s.getAccession();
                } else
                    if (modePhylip== modePhylip_LongName) {
                    name=s.getName()+" ";
                }
                
                pw.print(name+" "+s.getSequence()+"\n");//+distance(stri));
            }
            pw.flush();
            pw.close();
        } catch (Exception e) {return false;}
        return true;
    }

    

     /**
     * Demo try to output Phylip interlieve
     * @param filename
      * @param param (Phylip parameter if needed)
     * @return
     */
    public boolean outputPhylipInterleave(String filename, String param) {
        Vector<Sequence> tmp=new Vector<Sequence>();
        //Clone the sequence into a tmp vector
        for (Sequence s:seq) {
            Sequence ts=new Sequence();
            ts.setName(s.getName());
            ts.setSequence(s.getSequence());
            tmp.add(ts);
        }
        boolean nameOutput=false; //Is it the first lines....
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            //1. Output the number of sequence and size
            if (param.isEmpty()) param="\n";
            pw.print(this.getNbSequence()+"        "+this.getSequenceSize()+param);
            if (tmp.isEmpty()||tmp.get(0)==null) return false; //--No sequence(?)
            while (tmp.get(0).getSize()>0) {
            for (Sequence s:tmp) {
                String sname="";
                if (modePhylip==0) {
                    sname=s.getName()+"             *";
                    sname=sname.substring(0,9);
                } else if (modePhylip==1) {
                    sname=s.getName();
                    if (sname.length()>9) {
                        sname=sname.substring(sname.length()-9, sname.length());
                    } else {
                        sname+="           *";
                        sname=sname.substring(0,9);
                    }
                }
                //2. First time we output this sequence? YES: print id
                if (!nameOutput) {
                    pw.print(sname+" "+s.Remove(0,10)+" "+s.Remove(0,10)+" "+s.Remove(0,10)+" "+s.Remove(0,10)+" "+s.Remove(0,10)+"\n");
                }  else {
                //3. Else, output space at first...
                    pw.print("          "+s.Remove(0,10)+" "+s.Remove(0,10)+" "+s.Remove(0,10)+" "+s.Remove(0,10)+" "+s.Remove(0,10)+"\n");
                }
                }//+distance(stri));
            pw.print("\n"); //Space
            nameOutput=true;
            }//End while

            pw.flush();
            pw.close();
        } catch (Exception e) {e.printStackTrace();return false;}
        return true;
    }

    /**
     * Output Phylip Interleave
     * @param filename
     * @return A Vector with the sequence to keep the name, etc.
     */
    Vector<Sequence> outputPhylipInterleaveWithSynonym(String filename) {
        Vector<Sequence> tmp=new Vector<Sequence>();
        //Clone the sequence into a tmp vector
        int count=0;
        for (Sequence s:seq) {
            Sequence ts=new Sequence();
            //WE REPLACE THE NAME WITH A NUMBER
            ts.setName(String.valueOf("AZ"+count++));
            ts.setSequence(s.getSequence());
            tmp.add(ts);
        }
        boolean nameOutput=false; //Is it the first lines....
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            //1. Output the number of sequence and size
            pw.print(this.getNbSequence()+"        "+this.getSequenceSize()+"\n");
            while(tmp.get(0).getSize()>0) {


            for (Sequence s:tmp) {
                String name="";
                if (modePhylip==0) {
                    name=s.getName()+"             *";
                    name=name.substring(0,9);
                } else if (modePhylip==1) {
                    name=s.getName();
                    if (name.length()>9) {
                        name=name.substring(name.length()-9, name.length());
                    } else {
                        name+="           *";
                        name=name.substring(0,9);
                    }
                }
                //2. First time we output this sequence? YES: print id
                if (!nameOutput) {
                    pw.print(name+" "+s.Remove(0,10)+" "+s.Remove(0,10)+" "+s.Remove(0,10)+" "+s.Remove(0,10)+" "+s.Remove(0,10)+"\n");
                }  else {
                //3. Else, output space at first...
                    pw.print("          "+s.Remove(0,10)+" "+s.Remove(0,10)+" "+s.Remove(0,10)+" "+s.Remove(0,10)+" "+s.Remove(0,10)+"\n");
                }
                }//+distance(stri));
            pw.print("\n"); //Space
            nameOutput=true;
            }//End while

            pw.flush();
            pw.close();
        } catch (Exception e) {return tmp;}
        return seq;
    }

    /**
     * Demo try to output Phylip interlieve for MrBayes
     * Datatype = Dna: DNA states (A,C,G,T,R,Y,M,K,S,W,H,B,V,D,N)
     * Datatype = Rna: DNA states (A,C,G,U,R,Y,M,K,S,W,H,B,V,D,N)
     * Datatype = Protein: Amino acid states (A,R,N,D,C,Q,E,G,H,I,L,K,M,F,P,S,T,W,Y,V)
     * Datatype = Restriction: Restriction site (0,1) states
     * Datatype = Standard: Morphological (0,1) states
     * Datatype = Continuous: Real number valued states
     * @param filename
     * @return
     */
    public boolean outputNexus(String filename, String datatype) {
        Vector<Sequence> tmp=new Vector<Sequence>();
        //Clone the sequence into a tmp vector
        for (Sequence s:seq) {
            Sequence ts=new Sequence();
            ts.setName(s.getName());
            ts.setSequence(s.getSequence());
            tmp.add(ts);
        }
        boolean nameOutput=false; //Is it the first lines....
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            //1. Output header
            pw.println("#NEXUS");
            pw.println("begin data;");
            pw.println("dimensions ntax="+this.getNbSequence()+" nchar="+this.getSequenceSize()+";");
            //--TO DO missing = ?
            //pw.println("format interleave=no datatype=dna gap=-;");
            
            modePhylip=modePhylip_LongName;
            pw.println("format interleave=yes datatype="+datatype+" gap=-;");
            pw.println("matrix");                   
             while(tmp.get(0).getSize()>0) {          
            for (Sequence s:tmp) {
                String name=s.getName();                
                //2. Output information
               
                 pw.print(name+" "+s.Remove(0,50)+"\n");
                
                }//+distance(stri));
            pw.print("\n"); //Space
          
            }//End while
            pw.println(";\nend;");
            pw.flush();
            pw.close();
        } catch (Exception e) {return false;}
        return true;
    }

    /**
     * Demo try to output Phylip interlieve
     * @param filename
     * @return
     */
    boolean outputNexusOld(String filename) {
        Vector<Sequence> tmp=new Vector<Sequence>();
        //Clone the sequence into a tmp vector
        for (Sequence s:seq) {
            Sequence ts=new Sequence();
            ts.setName(s.getName());
            ts.setSequence(s.getSequence());
            tmp.add(ts);
        }
        boolean nameOutput=false; //Is it the first lines....
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            //1. Output header
            pw.print("#NEXUS"+"\n");
            pw.print("BEGIN DATA;"+"\n");
            pw.print("dimensions ntax="+this.getNbSequence()+" nchar="+this.getSequenceSize()+"\n");
            pw.print("format missing=?"+"\n");
            pw.print("symbols=\"ABCDEFGHIKLMNOPQRSTUVWXYZ\""+"\n");
            pw.print("interleave datatype=DNA gap= -;"+"\n\n");
            pw.print("matrix"+"\n");
            while(tmp.get(0).getSize()>0) {          
            for (Sequence s:tmp) {
                String name="";
                if (modePhylip==0) {
                    name=s.getName()+"             *";
                    name=name.substring(0,9);
                } else if (modePhylip==1) {
                    name=s.getName();
                    if (name.length()>9) {
                        name=name.substring(name.length()-9, name.length());
                    } else {
                        name+="           *";
                        name=name.substring(0,9);
                    }
                }
                //2. Output information
               
                 pw.print(name+" "+s.Remove(0,50)+"\n");
                
                }//+distance(stri));
            pw.print("\n"); //Space
          
            }//End while
            pw.print(";\nend;\n");
            pw.flush();
            pw.close();
        } catch (Exception e) {return false;}
        return true;
    }
    
        public boolean outputFasta(String filename) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            char special='|';
            for (Sequence s:seq) {
                String sname=">";
                if (isInteger(s.getName())||s.getName().startsWith("AZ")) {
                    sname+=s.getName();
                } else {
                    if (!s.getGi().equals("")) sname+="gi"+special+s.getGi()+special;
                    if (!s.getAccession().equals("")) sname+=s.getAccession_referee()+special+s.getAccession()+special;
                    sname+=s.getName();
                }
                pw.print(sname+"\n");
                pw.print(s.getSequence()+"\n");
            }
            pw.flush();
            pw.close();
        } catch (Exception e) {return false;}
        return true;
    }  

        
         public boolean outputFastq(String filename) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            char special='|';
            for (Sequence s:seq) {
                String sname="@";
                if (isInteger(s.getName())||s.getName().startsWith("AQ")) {
                    sname+=s.getName();
                } else {
                    if (!s.getGi().equals("")) sname+="gi"+special+s.getGi()+special;
                    if (!s.getAccession().equals("")) sname+=s.getAccession_referee()+special+s.getAccession()+special;
                    sname+=s.getName();
                }
                pw.print(sname+"\n");
                pw.print(s.getSequence()+"\n");
            }
            pw.flush();
            pw.close();
        } catch (Exception e) {return false;}
        return true;
    }
         
         
    public String outputFasta() {
        StringBuffer pw=new StringBuffer();
            
            char special='|';
            for (Sequence s:seq) {
                String sname=">";
                if (isInteger(s.getName())||s.getName().startsWith("AZ")) {
                    sname+=s.getName();
                } else {
                    if (!s.getGi().equals("")) sname+="gi"+special+s.getGi()+special;
                    if (!s.getAccession().equals("")) sname+=s.getAccession_referee()+special+s.getAccession()+special;
                    sname+=s.getName();
                }
                pw.append(sname+"\n");
                pw.append(s.getSequence()+"\n");
            }
           return pw.toString();
    }
    
    
    
    public String outputFastq() {
        StringBuffer pw=new StringBuffer();
            
            char special='|';
            for (Sequence s:seq) {
                String sname="@";
                if (isInteger(s.getName())||s.getName().startsWith("AQ")) {
                    sname+=s.getName();
                } else {
                    if (!s.getGi().equals("")) sname+="gi"+special+s.getGi()+special;
                    if (!s.getAccession().equals("")) sname+=s.getAccession_referee()+special+s.getAccession()+special;
                    sname+=s.getName();
                }
                pw.append(sname+"\n");
                pw.append(s.getSequence()+"\n");
            }
           return pw.toString();
    }


    public boolean outputMeg(String filename) {
      try {
        PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));        
        //--Test for the mega sequences
            //--Mega header
            pw.append("#mega\n!Title: "+filename+";\n\n");                      
            for (Sequence s:seq) {
                Sequence a=s.clone();
                String sname="#"+a.getName();               
                pw.append(sname+"\n");
                pw.append(a.getSequence()); 
                //--Note: not strickly needed
//                while (!a.getSequence().isEmpty())
//                    pw.append(a.Remove(0, 60) +"\n");
                pw.append("\n");
            }
               pw.flush();
            pw.close();
        } catch (Exception e) {return false;}
        return true;
    }
    
    public String outputMeg() {
        StringBuffer pw=new StringBuffer();
        
        if (filename.isEmpty()) filename="Unknown";
        //--Test for the mega sequences
            //--Mega header
            pw.append("#mega\n!Title: "+filename+";\n\n");                      
            for (Sequence s:seq) {                
                Sequence a=s.clone();
                String sname="#"+a.getName();               
                pw.append(sname+"\n");
                pw.append(a.getSequence());
                //--Note: not strickly needed
//                while (!a.getSequence().isEmpty())
//                    pw.append(a.Remove(0, 60) +"\n");
                pw.append("\n");
            }
           return pw.toString();
    }
    Vector<Sequence> outputFastaWithSynonym(String filename) {
        int count=0;
        Vector<Sequence> tmp = new Vector<Sequence>();
        for (Sequence s:seq) {
            Sequence ts=new Sequence();
            //WE REPLACE THE NAME WITH A NUMBER
            ts.setName(String.valueOf("AZ"+count++));
            ts.setSequence(s.getSequence());
            tmp.add(ts);
        }
        
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            char special='|';
            for (Sequence s:tmp) {
               
                String name=">";
                name+=s.getName();
                pw.print(name+"\n");
                pw.print(s.getSequence()+"\n");
            }
            pw.flush();
            pw.close();
        } catch (Exception e) {return seq;}
        return seq;
    }

//       public String getFasta() {
//       StringBuilder output=new StringBuilder();
//           try {
//
//            char special='|';
//            for (Sequence s:seq) {
//
//                String name=">";
//                if (!s.getGi().equals("")) name+="gi"+special+s.getGi()+special;
//                if (!s.getAccession().equals("")) name+=s.getAccession_referee()+special+s.getAccession()+special;
//                name+=s.getName();
//                output.append(name+"\n");
//                output.append(s.getSequenceClipping(this.limit_sequence_begin, this.limit_sequence_end)+"\n");
//            }
//
//        } catch (Exception e) {return "";}
//        return output.toString();
//    }

//     public String getSequences() {
//       StringBuilder output=new StringBuilder();
//           try {
//
//            char special='|';
//            for (Sequence s:seq) {
//
//                String sname=String.format("%s                                                                       ", s.getName());
//                sname=sname.substring(0, 40);
//                //if (!s.getGi().equals("")) name+=" gi"+special+s.getGi()+special;
//                //if (!s.getAccession().equals("")) name+=s.getAccession_referee()+special+s.getAccession()+special;
//                //name+=s.getName();
//                output.append(sname+" ");
//                output.append(s.getSequenceClipping(this.limit_sequence_begin, this.limit_sequence_end)+"\n");
//            }
//
//        } catch (Exception e) {return "";}
//        return output.toString();
//    }

        /**
         * Special output with Phylip name
         * @param filename
         * @return
         */
        boolean outputSpecialFasta(String filename) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            for (Sequence s:seq) {
                String name="";
                if (modePhylip==0) {
                    name=s.getName()+"             *";
                    name=name.substring(0,9);
                } else if (modePhylip==1) {
                    name=s.getName();
                    if (name.length()>9) {
                        name=name.substring(name.length()-9, name.length());
                    } else {
                        name+="           *";
                        name=name.substring(0,9);
                    }
                }
                pw.print(">"+name+"\n");
                pw.print(s.getSequence()+"\n");
            }
            pw.flush();
            pw.close();
        } catch (Exception e) {return false;}
        return true;
    }

	/**
	* Print A Phylip like représentation of the current sequence list on the screen
	*/
     public void PrintPhylip() {
            //            10     20
            //        seq0       -GTA-GCGCT -T--C-----
            //        seq1       TGTA-TTGC- CTG--C-T--
           
            Config.log(this.getNbSequence()+"        "+this.getSequenceSize());
            for (Sequence s:seq) {
                String sname="";
                if (modePhylip==modePhylip_beginning) {
                    sname=s.getName()+"             *";
                    sname=sname.substring(0,9);
                } else if (modePhylip==modePhylip_end) {
                    sname=s.getName();
                    if (sname.length()>9) {
                        sname=sname.substring(sname.length()-9, sname.length());
                    } else {
                        sname=s.getAccession();
                        sname+="           *";
                        sname=sname.substring(0,9);
                    }
                } else if (modePhylip==modePhylip_Accesion) {
                    sname=s.getAccession();
                }
                
                Config.log(sname+" "+s.getSequence());
            }
    }

     /**
	* Print A Phylip like représentation of the current sequence list on the screen
	*/
     public String outputPhylip() {
            //            10     20
            //        seq0       -GTA-GCGCT -T--C-----
            //        seq1       TGTA-TTGC- CTG--C-T--
           StringBuffer pw=new StringBuffer();
           
           pw.append(this.getNbSequence()+"        "+this.getSequenceSize()+"\n");
            for (Sequence s:seq) {
                String sname="";
                if (modePhylip==modePhylip_beginning) {
                    sname=s.getName()+"             *";
                    sname=sname.substring(0,9);
                } else if (modePhylip==modePhylip_end) {
                    sname=s.getName();
                    if (sname.length()>9) {
                        sname=sname.substring(sname.length()-9, sname.length());
                    } else {
                        sname=s.getAccession();
                        sname+="           *";
                        sname=sname.substring(0,9);
                    }
                } else if (modePhylip==modePhylip_Accesion) {
                    sname=s.getAccession();
                }
                
                pw.append(sname+" "+s.getSequence()+"\n");
            }
            return pw.toString();
    }

//-- Information from http://www.ncbi.nlm.nih.gov/staff/tao/URLAPI/blastdb.html    
// Table 5. Defline Identifier Syntax for Different Databases
//Source Database               Defline Identifier Syntax
//GenBank                       gb|accession|locus
//EMBL Data Library             emb|accession|locus
//DDBJ, DNA Database of Japan	dbj|accession|locus
//NBRF PIR                      pir||entry
//Protein Research Foundation	prf||name
//SWISS-PROT                    sp|accession|entry name
//Brookhaven Protein Data Bank	pdb|entry|chain
//Patents                       pat|country|number
//GenInfo Backbone Id 1         bbs|number
//General database identifier 2	gnl|database|identifier
//NCBI Reference Sequence	ref|accession|locus
//Local Sequence identifier 2	lcl|identifier
     
 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 /// Informations biologiques diverses
 /// Note: On pourrait tirer plus d'informations (pI, etc.)

        /**
         * Methode renvoyant des statistiques sur la Alignment.
         * Note: Utilisé dans la vue principale de TP2
         * @return une String sous la forme (100% A 100% T %100 G 100% C)
         *         representant le % de chaque nucleotide dans la Alignment
         */
        public String getStat(String seq) {
               int a=0;
               int t=0;
               int g=0;
               int c=0;

               int total=seq.length();
               for (char ch:seq.toCharArray()) {
                   switch (ch) {
                       case 'A':a++; break;
                       case 'T':t++; break;
                       case 'G':g++; break;
                       case 'C':c++; break;    //LOL
                       //On ne calcul pas les autres caractères car ils sont normalement absent.
                   }
               } //end for
               a*=100;t*=100;g*=100;c*=100;
               a/=total;t/=total;g/=total;c/=total;
               return String.format("Stats: %d pb (%2d%% A %2d%% T %2d%% G %2d%% C)",total,a,t,g,c);
        }

/////////////////////////////////////////////////////////////////////////////////
// Helper

   private boolean tooBigFasta(String filename) {
       //Variable
       final int Mb=1048576;
       final int MaxMb=104857600; //Max 10Mb per sequence
                      
       
       File file=new File(filename);
       //1. Is-it multi-fasta?
       int count=0;
       int countMb=0;
       try {
           BufferedReader br=new BufferedReader(new FileReader(file));
           while (br.ready()) {
                String stri=br.readLine();
               if (stri.startsWith(">")) {
                   count++;
                   if (isDebug()&&countMb>0) Config.log(String.valueOf(countMb/Mb));
                   if (countMb>MaxMb) return false;
                   countMb=0;
               } else {
                   countMb+=stri.length();
               }
           }
           br.close();
       } catch(Exception e) {return true;}


       return false;
   }

///////////////////////////////////////////////////////////////////////////////
/// Search function
   /**
    * Fonction mode text et graphique
    * Note: Par design, on retourne les sequences dont la recherhce (voir mode)
    * "matches" un certains regex (pratique pour des applications de type AJAX )
    * Note2: On assigne aussi a la variable lastSearch des informations sur la recherche
    * @return A vector of the Index position of Matching result
    */
    public Vector<Integer> search (String regex, int mode) {
        Vector<Integer> returnArray = new Vector<Integer>();
        Pattern p;
        try {
            p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        } catch(java.util.regex.PatternSyntaxException e) {return returnArray;}
        int search_len=0;
        try {
            search_len=Integer.valueOf(regex);
        } catch (Exception e) {search_len=0;}
        switch (mode) {
            case MODE_ID:       lastSearch="Id with: "+regex;
                                for (int i=0; i<seq.size();i++) {
                                      Sequence data=seq.get(i);
                                      Matcher m = p.matcher(data.getGi());
                                      if (m.find()) returnArray.add(i);
                                }
                                break;
            case MODE_ACCESSION:lastSearch="Accession with: "+regex;
                                for (int i=0; i<seq.size();i++) {
                                     Sequence data=seq.get(i);
                                      Matcher m = p.matcher(data.getAccession());
                                      if (m.find()) returnArray.add(i);
                                }
                                break;
            case MODE_DESC:  lastSearch="Description with: "+regex;
                                for (int i=0; i<seq.size();i++) {
                                      Sequence data=seq.get(i);
                                       Matcher m = p.matcher(data.getName());
                                      if (m.find()) returnArray.add(i);
                                }
                                break;
            case MODE_LENMORE: lastSearch="Len(bp) greater: "+regex;
                                for (int i=0; i<seq.size();i++) {
                                    Sequence data=seq.get(i);
                                    try {
                                        int len=data.getLen();
                                        if (len>=search_len) returnArray.add(i);
                                    } catch(Exception e) {}
                                }
                                break;
             case MODE_LENLESS: lastSearch="Len(bp) greater: "+regex;
                                for (int i=0; i<seq.size();i++) {
                                    Sequence data=seq.get(i);
                                    try {
                                        int len=Integer.valueOf(data.getLen());
                                        if (len<=search_len) returnArray.add(i);
                                    } catch(Exception e) {}
                                }
                                break;
            case MODE_ALL:      lastSearch="All with: "+regex;
                                for (int i=0; i<seq.size();i++) {
                                    Sequence data=seq.get(i);
                                    Matcher m = p.matcher(data.toString());
                                    if (m.find()) returnArray.add(i);
                                }

            case MODE_SEQUENCE: lastSearch="Sequence with: "+regex;
                                for (int i=0; i<seq.size();i++) {
                                    Sequence data=seq.get(i);
                                    Matcher m = p.matcher(data.getSequence());
                                    if (m.find()) returnArray.add(i);
                                }
        } //end switch
        Config.log("Searching for "+lastSearch);
        System.out.printf(" found %d result(s)\n", returnArray.size());
        return returnArray;
      }

    
 ////////////////////////////////////////////////////////////////////////////////
/// Getter / Setter

    /**
     *
     * @return the number of Selected Sequence
     */
    public int getNumberSelected() {
        int number=0;
        for (Sequence s:seq) {
            if (s.isSelected()) number++;
        }
        return number;
    }

      /**
     *
     * @return the number of Selected Sequence
     */
    public Vector<Sequence> getSelected() {
        Vector<Sequence> tmpseq=new Vector<Sequence>();
        int number=0;
        for (Sequence s:seq) {
            if (s.isSelected()) tmpseq.add(s.clone());                
        }
        return tmpseq;
    }



     /**
     *
     * @return the number of Selected Sequence
     */
    public int getNumberDisplayedSequence() {
        int number=0;
        for (Sequence s:seq) {
            if (s.isDisplayed()) number++;
        }
        return number;
    }

    /**
     * @return the vector of Sequence
     */
    public Vector<Sequence> getSequences() {
        return seq;
    }

    /**
     * @return le nombre de sequence
     */
    public int getNbSequence() {
        return seq.size();
    }

    public int getMaxNameSize() {
        if (max_seq_name_len==0) {
             for (Sequence s:seq) {
                max_seq_name_len= (s.getName().length()>max_seq_name_len?s.getName().length():max_seq_name_len);
            }
        }
        return this.max_seq_name_len;
    }

    /**
     * @return return the longuest Sequence size
     */
    public int getSequenceSize() {
        max_len=0;
        for (Sequence s:seq) {
                max_len=(s.getSize()>max_len?s.getSize():max_len);
            }
        return max_len;
    }

    /**
     * @return return the longuest Sequence size
     */
    public int getSize() {
        return getSequenceSize();
    }

    /**
     * @param limit_sequence_begin the limit_sequence_begin to set
     */
    public void setLimit_sequence_begin(int limit_sequence_begin) {
        this.limit_sequence_begin = limit_sequence_begin;
    }

    /**
     * @param limit_sequence_end the limit_sequence_end to set
     */
    public void setLimit_sequence_end(int limit_sequence_end) {
        this.limit_sequence_end = limit_sequence_end;
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Helper

    private boolean isInteger(String value) {
        try {
            int i=Integer.valueOf(value);
            return true;
        } catch(Exception e) {return false;}
    }

     public boolean isRNA() {
        for (Sequence s:seq) {
            if (s.getSequence_type().equalsIgnoreCase("AA")||s.getSequence_type().equalsIgnoreCase("DNA")) return false;
        }
        return true;
     }

     public boolean isDNA() {
        for (Sequence s:seq) {
            if (s.getSequence_type().equalsIgnoreCase("AA")||s.getSequence_type().equalsIgnoreCase("RNA")) return false;
        }
        return true;
     }

     public boolean isAA() {
         for (Sequence s:seq) {
            if (s.getSequence_type().equalsIgnoreCase("DNA")||s.getSequence_type().equalsIgnoreCase("RNA")||s.getSequence_type().isEmpty()) return false;
        }
        return true;
     }

    /**
     * @return the Evolutionary_Model
     */
    public String getEvolutionary_Model() {
        return Evolutionary_Model;
    }

    /**
     * @param Evolutionary_Model the Evolutionary_Model to set
     */
    public void setEvolutionary_Model(String Evolutionary_Model) {
        this.Evolutionary_Model = Evolutionary_Model;
    }
    
     /**
     * @return the debug
     */
    public static boolean isDebug() {
        return debug;
    }

    /**
     * @param aDebug the debug to set
     */
    public static void setDebug(boolean aDebug) {
        debug = aDebug;
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
    
}
