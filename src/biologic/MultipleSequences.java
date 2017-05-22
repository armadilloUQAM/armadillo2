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
import database.databaseFunction;
import iubio.readseq.BioseqFormats;
import iubio.readseq.BioseqWriterIface;
import iubio.readseq.Readseq;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import results.report;
import workflows.workflow_properties;

/**
 * 
 * @author Etienne Lord
 * @since Mars 2009
 */
public class MultipleSequences extends ListSequence implements Serializable, Biologic, Iterator {

    private static final long serialVersionUID = 200904263L;
    protected String note="";     //--Note in database
    protected int id=0;           //--Id in database
    private int runProgram_id=0;
        
    public static databaseFunction df=new databaseFunction();


    ////////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTOR
    
    public MultipleSequences() {        
        name="UnknownMultipleSequences";
    }

    public MultipleSequences(int id) {      
        loadFromDatabase(id);
    }

    public MultipleSequences(String filename) {       
        loadSequences(filename);
    }

    
    
////////////////////////////////////////////////////////////////////////////////
/// Database function

    public boolean loadFromDatabase(int id) {
        MultipleSequences multi=df.getMultipleSequence(id);
        if (multi.id>0) {
            this.name=multi.name;
            this.id=multi.id;
            this.note=multi.note;
            this.seq.clear(); 
            this.seq.addAll(multi.getSequences());
            return true;
        } else return false;
    }

    public boolean saveToDatabase() {
        id=0;
        id=df.addMultipleSequences(this);
        return (id==0?false:true);
    }

    public boolean removeFromDatabase() {
       return df.removeMultipleSequences(this);
    }

    public boolean updateDatabase() {
        return df.updateMultipleSequences(this);
    }

    /**
     * This remove a sequence from this group
     * @param id
     * @return
     */
    public boolean removeSequence(int id) {
        return df.removeSequencesFromMultipleSequences(this, id);
    }

    
    public boolean loadFromFile(String filename) {
       return this.loadSequences(filename);
    }

////////////////////////////////////////////////////////////////////////////////
/// Iterator

    Vector<Integer>next=new Vector<Integer>();
    int counter=0;
    int maxid=-1;
    public boolean hasNext() {
       if (next.size()==0) {
           next=df.getAllMultipleSequencesID();
           maxid=next.size();
       }
       return (this.counter<maxid);
    }

    public Object next() {
        return new MultipleSequences(next.get(counter++));
    }

    public void remove() {
        Sequence s=new Sequence(counter-1);
        s.removeFromDatabase();
    }

   @Override
    public Vector<Integer> getAllId() {
        return next;
    }

   /**
    * Waening, high cost...
    * @param id
    * @return
    */
   public boolean exists(Integer id) {
        return df.existsMultipleSequences(id);
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Main Function to load an Unknown sequence
     * 
     * Use http://iubio.bio.indiana.edu/soft/molbio/readseq/java/
     * at last...
     *
     * Don Gilbert 
     * software@bio.indiana.edu, May 2001 
     * Bioinformatics group, Biology Department & Cntr. Genomics & Bioinformatics, 
     * Indiana University, Bloomington, Indiana
     *
     * @param filename
     */

    public boolean loadSequences(String filename) {
            boolean loaded=false;
            //--CASE 1. Load using my function...
            if (!Util.FileExists(filename)) return false;
            String file_to_read=filename;
            //--For testing purpose
            filename=filename.toUpperCase();
            String infile="infile"; //--For format conversion;

            this.getSequences().clear();             
            
            if (filename.endsWith("FASTA")||filename.endsWith("TXT")||filename.endsWith("FAS")||filename.endsWith("FAA")||filename.endsWith("FNA")||filename.endsWith("FFA")||filename.endsWith("FRN")||filename.endsWith("FFN")) {
                if (this.readSequenceFromFasta(file_to_read)&&this.getNbSequence()>0) {
                    loaded=true;
                }
            } else  
            if(filename.endsWith("FASTQ") || filename.endsWith("fq")||filename.endsWith("txt")){
                if(this.readSequenceFromFastq(file_to_read)&&this.getNbSequence() >0){
                    loaded=true;
                }
            } else
                
            if (filename.endsWith("PHY")||filename.endsWith("PHYLIP")) {
                if (this.readSequenceFromPhylip(file_to_read)&&this.getNbSequence()>0) {
                    loaded=true;
                }
            } else
            if (filename.endsWith("ALN")||filename.endsWith("CLU")||filename.endsWith("CLUSTAL")||filename.endsWith("CLUSTALW")||filename.endsWith("CLUSTALW2")||filename.endsWith("TXT")) {
                Config.log("reading from clustal...");
                if (this.readSequenceFromClustalW2(file_to_read)&&this.getNbSequence()>0) {
                   loaded=true;
                }
            } 
            if (!loaded) {
                //--read using ReadSeq
                //--Remove []
                Util.deleteFile(infile);
                String[] o=Util.InputFile(file_to_read);
                Util outfile=new Util();
                outfile.open(infile);
                for (String s:o) {
                    Matcher m=remove_backet.matcher(s);
                    if (m.find()) {
                        outfile.println(m.group(1));
                    } else {
                        outfile.println(s);
                    }
                }
                outfile.close();
                //--CASE LAST. Convert to FASTA using readseq
                Config.log("Converting to FASTA... "+file_to_read);
                    try {                         
                        int outid= BioseqFormats.formatFromName("fasta");
                            BioseqWriterIface seqwriter= BioseqFormats.newWriter(outid);
                            seqwriter.setOutput(new FileWriter("out.fasta"));
                            seqwriter.writeHeader();
                            Readseq rd= new Readseq();
                             rd.setInputObject(infile);
                             if (rd.isKnownFormat() && rd.readInit())
                             rd.readTo( seqwriter);
                            seqwriter.writeTrailer();
                            if (this.readSequenceFromFasta("out.fasta")&&this.getNbSequence()>0) {
                                Util.deleteFile("out.fasta");
                                //--Remove Sequence size if found

                                for (Sequence s:this.seq) {
                                    System.out.println(s.getName());
                                    Matcher m=seq_size_artefact.matcher(s.getName());
                                    if (m.find()) {
                                        String tmp_name=m.group(1); //--Note: because of Java regex
                                        Config.log("Renaming "+s.getName()+" to "+tmp_name);
                                        s.setName(tmp_name);
                                    }
                                }
                               loaded=true;
                            }
                    } catch(Exception e) {
                        Config.log("Unable to read using ReadSeq...");
                    }
            } //--End load using ReadSeq

            if (!loaded||this.getNbSequence()==0) {
                Config.log("Unable to load sequence(s) from "+file_to_read);
               return false;
            } else {
                //--Default, DNA
                //--TO DO, Guess here
               //Config.log(this.toString());
                for (Sequence s:this.seq) {
                    s.setSequence_type("dna");
                    //--Remove sequence size artefact
                    try {
                        Matcher m=seq_size_artefact.matcher(s.getName());
                        if (m.find()) s.getName().replace(m.group(1), "");
                    } catch(Exception e){}
                }
                Config.log("Successfully imported "+this.getNbSequence()+" sequence(s) from "+file_to_read+" to database");
                return true;
            }            
    } //--End loadSequences

     ////////////////////////////////////////////////////////////////////////////
    /**
     * Main Function to load an Unknown sequence
     * 
     * Use http://iubio.bio.indiana.edu/soft/molbio/readseq/java/
     *
     * Don Gilbert 
     * software@bio.indiana.edu, May 2001 
     * Bioinformatics group, Biology Department & Cntr. Genomics & Bioinformatics, 
     * Indiana University, Bloomington, Indiana
     *
     * @param filename
     */

//    public boolean loadSequencesToDatabase(String filename) {
//            this.filename=filename;
//            //--CASE 0. Already in database
//            if (this.getId()!=0) return false;
//            //--CASE 1. Convert to FASTA using readseq
//            if (!filename.endsWith("FASTA")) {
//                Config.log("Converting to FASTA...");
//                try {
//                     int outid= BioseqFormats.formatFromName("fasta");
//                        BioseqWriterIface seqwriter= BioseqFormats.newWriter(outid);
//                        seqwriter.setOutput(new FileWriter("out.fasta"));
//                        seqwriter.writeHeader();
//                        Readseq rd= new Readseq();
//                         rd.setInputObject(filename);
//                         if (rd.isKnownFormat() && rd.readInit())
//                         rd.readTo( seqwriter);
//                        seqwriter.writeTrailer();
//                        this.filename="out.fasta";
//                } catch(Exception e) {
//                    Config.log("Unable to read using ReadSeq...");
//                    e.printStackTrace();
//                    return false;
//                }
//            } //--End not ending with "Fasta"
//            //--CASE 2. Read fata
//               open(this.filename);
//               seq.clear();
//               Config.log("Reading fasta:"+filename+":");
//               while(this.hasNextSequence()) {
//                   Sequence sequence=this.getNextSequence();
//                   if (sequence!=null) {
//                       seq.add(sequence);
//                   }
//                   //--Counter
//                   if ((this.file_read_length*100/this.file_length)%5==0) Config.log("*");
//                   //--Save to each 20000 line... or when sequence is null
//                   if (this.file_current_line_number%20000==0||sequence==null) {
//                      if (this.getId()!=0) {
//                         this.updateDatabase();
//                      } else {
//                          this.saveToDatabase();
//                      }
//                   }
//               }
//       Config.log("done.");
//       //return true;
//
//
//
//
//
//
//            if (this.getNbSequence()==0) {
//                Config.log("Unable to load sequence(s) from "+filename);
//               return false;
//            } else {
//                Config.log("Successfully imported "+this.getNbSequence()+" sequence(s) from "+filename+" to database");
//                return true;
//            }
//    } //--End loadSequences

  
    
    
    /**
     * Generate Random non aligned sequence
     * @param len (sequence length)
     * @param number (number of sequence)
     */
    public MultipleSequences(int len, int number) {
        for (int i=0; i<number; i++) {
            Sequence tmp = new Sequence();
            tmp.setSequence(Sequence.randomDNA(len));
            tmp.setName("RAND"+i);
            seq.add(tmp);
        }
    }
    
    /**
     * Add a Sequence to the Alignment.
     * Note: this doesn't change the database state...
     * @param s
     * @return false if there is an error
     */
     public boolean add(Sequence s) {
       return seq.add(s);
    }

     /**
      * Concatenate two MultipleSequences based on sequence name
      * @param s
      * @return 
      */
     public boolean concat(MultipleSequences s) {
         return this.add(s);
     }

    /**
     * Concatenation of sequence
      * Note: by default, Name of sequences must be equals
     * @param multi
     * @return true if Success
     */
    public boolean add(MultipleSequences multi) {        
        int original_len=this.getSize();

        for (Sequence s:multi.getSequences()) {
            boolean found=false;
            //--CASE 1. Sequence with same name found. we add the sequence
            for (Sequence o:this.getSequences()) {
                if (s.name.equals(o.name)) {
                    // Create paddling
                    String stri="";
                    if (o.getSize()<original_len) {
                        for (int i=o.getSize(); i<original_len;i++) stri+="-";;
                    }
                    String str=o.getSequence()+stri+s.getSequence();
                    o.setSequence(str);
                    found=true;
                }
            }
            if (!found) this.add(s);
        }
        return true;
    }
   
    /**
     * Concatenation of sequences
      * Note: Based on sequences ordering...
     * @param multi
     * @return 
     */
    public boolean add_ByPosition(MultipleSequences multi) {        
        int original_len=this.getSize();                    
            //--CASE 1. Sequence with same name found. we add the sequence
            for (int i=0;i<multi.getNbSequence();i++) {
                Sequence s=multi.getSequences().get(i);
                if (i<this.getNbSequence()) {
                    Sequence o=this.getSequences().get(i);  
                    // Create paddling
                    String stri="";
                    if (o.getSize()<original_len) {
                        for (int j=o.getSize(); j<original_len;j++) stri+="-";;
                    }
                    String str=o.getSequence()+stri+s.getSequence();
                    o.setSequence(str);                   
                } else {                    
                    this.add(s);
                }
            } //--End for                  
        return true;
    }

    public Vector<Sequence> remove_duplicates() {
        Vector<Sequence>duplicates=new Vector<Sequence>();
        //--Iterate over previous sequences.. If sequence egal, remove
       for (int i=this.getNbSequence();i>-1;i--) {
           Sequence si=this.getSequences().get(i);
           for (int j=i-1;j>-1;j--) {
               Sequence sj=this.getSequences().get(j);
               if (sj.getSequence().equals(si.getSequence())) {
                  this.getSequences().remove(i);
                  duplicates.add(si);
                  break;
               }
           }
       }
        return duplicates;
    }


    public void setName(String name) {
        this.name=name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note the note to set
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the runProgram_id
     */
    public int getRunProgram_id() {
        return runProgram_id;
    }

    /**
     * @param runProgram_id the runProgram_id to set
     */
    public void setRunProgram_id(int runProgram_id) {
        this.runProgram_id = runProgram_id;
    }

    @Override
    public String toString() {
        String s="["+this.getName()+"]\n";
        //--Render a ladder (top)
        String stri_number=""; 
        String stri_ladder="";
        String stri_info="";
        for (int i=0; i<45;i++)  {
            stri_number+=" ";
            stri_ladder+=" ";
            stri_info+=" ";
        }        
        for(int i=0; i<getSequenceSize();i++) {
            if (i%10==0) {                
                stri_number+=String.format("%-10d",i+1+positionAdjuster); 
                stri_ladder+="|         ";
            }
        }
       s+=stri_number+"\n"+stri_ladder+"\n";
       
       //--Render sequences
        for (Sequence S:this.getSequences()) {
            s+=S.toString()+"\n";
        }
        //--Render information
       if (!this.informationString.equals("")) s+=stri_info+informationString+"\n";
       //--Sender bottom layer
        s+=stri_ladder+"\n"+stri_number+"\n";          
        //--Render info
        s+="#Total "+this.getNbSequence()+" sequence(s) with a length of "+this.getSequenceSize()+"\n";
        
        //   "#Note: You can edit this group of sequences directly and click the Save button below\n";
        return s;
    }

    public void outputPhylipInterleaveWithSequenceID(String filename) {
          outputPhylipInterleaveWithSequenceID(filename, "\n");
    }
    
    public void outputPhylipInterleaveWithSequenceID(String filename, String param) {
        for (Sequence s:getSequences()) s.setName("AZ"+s.getId());
        modePhylip=MultipleSequences.modePhylip_beginning;
        outputPhylipInterleave(filename,param);
    }

     public void outputFastaWithSequenceID(String filename) {
        for (Sequence s:getSequences()) s.setName("AZ"+s.getId());
        outputFasta(filename);
    }

     public String ouputFastaWithSequenceID() {
         for (Sequence s:getSequences()) s.setName("AZ"+s.getId());
         return outputFasta(); 
     }
     
     
         public void outputFastqWithSequenceID(String filename) {
        for (Sequence s:getSequences()) s.setName("AQ"+s.getId());
        outputFasta(filename);
    }

     public String ouputFastqWithSequenceID() {
         for (Sequence s:getSequences()) s.setName("AQ"+s.getId());
         return outputFasta(); 
     }
     
     
     
     
     
     


    public workflow_properties returnProperties() {
         workflow_properties tmp=new workflow_properties();
         if (id==0) this.saveToDatabase();
             tmp.put("input_multiplesequences_id", this.getId());
             tmp.put("output_multiplesequences_id", this.getId());
         return tmp;
     }

    public String getBiologicType() {
        return "MultipleSequences";
    }

    public String toHtml() {
        StringBuilder st=new StringBuilder();
        report r = new report();
        st.append("<b>MultipleSequences "+getName()+" with ID ["+getId()+"]</b><br>");
        char nuc=':'; //--Not used caracter for starting point...
        char oldnuc=':'; //--Not used caracter for starting point...
        st.append("<table border=\"0\" bgcolor=\"#FFFFFF\"><thead><tr><td><b>Name                    </b></td><td><b>Sequences</b></td></tr></thead><tbody>");
        st.append("<tr><td>"+"                                                       "+"</td><td>");
        for(int i=0; i<this.getSequenceSize();i++) {
            if (i%10==0) {
                String str=String.format("%-10d",i+1);
                st.append("<span class=\"paml\">"+str.replaceAll(" ", "&nbsp;")+"</span>");
            }
        }
        st.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");
        for (Sequence S:this.getSequences()) {
          String tmp=S.getSequence();
          st.append("<tr><td>"+S.getName().substring(0,Math.min(50, S.getName().length()))+"</td><td>");
          st.append("<span class=\"paml\">");
          if (isAA()){
              for (int i=0;i<tmp.length();i++){
                   nuc=tmp.charAt(i);
                  if (oldnuc!=nuc) {
                      oldnuc=nuc;
                      st.append("</span>");
                      st.append("<span class=\"prot"+nuc+"\">");
                  }
                  st.append(nuc);
              }
              st.append("</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
          } else {
              for (int i=0;i<tmp.length();i++){
              nuc=tmp.charAt(i);
               if (oldnuc!=nuc) {
                      oldnuc=nuc;
                      st.append("</span>");
                      st.append("<span class=\"nuc"+nuc+"\">");
                  }
                  st.append(nuc);
              }
              st.append("</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");           
          }
          st.append("</td></tr>");
        } //--End for
        st.append("</tbody></table>");
        st.append("Total "+this.getNbSequence()+" sequence(s) with len "+this.getSequenceSize()+"\n");
        return st.toString();
    }

     public String getNameId(int id) {
        return df.getMultipleSequencesName(id);
    }

    public String getFileNameId(int id) {
        return "";
    }
    
     

      public void setData(String data) {
        this.getSequences().clear();
        Util u=new Util("temp.txt");
        u.println(data);
        u.close();
        this.loadSequences("temp.txt");
        Util.deleteFile("temp.txt");
    }

    public String getFasta() {
        return this.outputFasta();
    }
    
    public String getFastq() {
        return this.outputFastq();
    }

    public String getPhylip() {
        return this.outputPhylip();
    }


    public String getExtendedString() {
        String s="["+this.getName()+"];\n";
        for (Sequence S:this.getSequences()) {
            s+=S.getExtendedString()+"\n";
        }
        s+="#Total "+this.getNbSequence()+" sequence(s) with len "+this.getSequenceSize()+"\n";
        return s;
    }
    
    //--In development
    //--Note: starts at 1
    public String getColumn(int number) {
        String column="";
        if (number-1>this.getSequenceSize()||getNbSequence()==0) return column;                        
        for(int j=0; j<this.getNbSequence();j++) {
            Sequence s=this.getSequences().get(j);
            if (number-1>s.getSize()) {
                column+=" ";                
            } else {
                column+=s.getSequence().charAt(number-1);
            }
        }        
        return column;
    }
    
    /**
     * Un-optimized way to extract part of a MultipleSequence
     * Note: start with 0
     * @param start (inclusive)
     * @param stop (exclusive)
     * @return 
     */
    public MultipleSequences extractColumn(int start, int stop) {
           MultipleSequences m=new MultipleSequences();
           if (start-1>this.getSequenceSize()||getNbSequence()==0) return m;               
           if (stop-1>this.getSequenceSize()||stop<start) return m;               
            for(int j=0; j<this.getNbSequence();j++) {
                Sequence s=this.getSequences().get(j).clone();
                s.setSequence(s.getSequenceClipping(start,stop));
                m.add(s);
            }                              
           return m;
    }

     /**
     * Get the next valid Id found in the database
     * @param start the current id...
     * @return the next id or 0 if not found
     */
    public int getNextValidId() {
        if (!hasNext()) return 0;
        for (int i=0;i<next.size();i++) {
            if (next.get(i)==this.id) return next.get(i+1);
        }
        //--Unable to find the current ID? -> Find the first higher...
         for (int i=0;i<next.size();i++) {
            if (next.get(i)>this.id) return next.get(i);
        }
        //--If not, return 0; 
        return 0;
    }

    public Biologic getParent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
