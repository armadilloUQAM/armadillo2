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


import database.databaseFunction;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import results.report;
import workflows.workflow_properties;
 

/**
 * Object sequence
 * USE BY: Various loading sequence routine (ex. readFasta, ...)
 * @author Etienne Lord
 * @since July 2009
 */

public class Sequence implements Serializable, Biologic, Iterator  {

    public StringBuilder sequence=new StringBuilder();
    public StringBuilder quality=new StringBuilder(); //Found in sequence_stats
    protected String name = "UnknownSequence";
    protected String orientation=""; /// 5' ou 3'
    protected String gi="";
    protected String accession="";
    protected String accession_referee=""; //Ex. ref, gb, ebj
    protected int len=0;
    protected String abbreviate="";
    protected String note="";
    protected String timeAdded="";
    protected String sequence_type="DNA"; //AA, DNA, RNA... default dna
    private int RunProgram_id=0;

    public int id=0;                          //Sequence id in database
    protected int original_id=0;              //Original sequence id for this sequence
    //protected int sequenceStats_id=0;
    //
    protected boolean selected=false;//is selected for analysis
    protected boolean outgroup=false;//do we use for outgroup
    protected boolean selected_for_remove=false;   //mark for remove from project
    private boolean selected_for_grouping=false;   //mark for remove from project
    protected boolean displayed=true;     // Currently not used

    public static databaseFunction df = new databaseFunction();

////////////////////////////////////////////////////////////////////////////////
/// CONSTRUCTOR
    
    public Sequence() {} //Default constructor

    public Sequence(int id) {
        loadFromDatabase(id);
    }

    public boolean loadFromDatabase(int id) {
        Sequence seq=df.getSequence(id);
        if (seq.getId()>0) {
            updateSequenceInfos(this);
//           this.setSequenceStats_id(seq.getSequenceStats_id());
            this.id=id;
            return true;
        } else return false;
    }

    public boolean saveToDatabase() {
        id=0;
        id=df.addSequence(this);
        return (id==0?false:true);
    }

    public boolean removeFromDatabase() {       
        return df.removeSequence(this);
    }

    public boolean updateDatabase() {
        return df.updateSequence(this);
    }


    private void updateSequenceInfos (Sequence seq) {
        this.setName(seq.getName());
        this.setGi(seq.getGi());
        this.setAccession(seq.getAccession());
        this.setAccession_referee(seq.getAccession_referee());
        this.setAbbreviate(seq.getAbbreviate());
        this.setSequence(seq.getSequence());
        this.setLen(seq.getLen());
        this.setNote(seq.getNote());
        this.setOriginal_id(seq.getOriginal_id());
        this.setTimeAdded(seq.getTimeAdded());
        this.setSequence_type(seq.getSequence_type());
    }
    /**
     * 
     * @param filename
     * @return
     */
     
    public boolean loadFromFile(String filename) {
        MultipleSequences multi=new MultipleSequences();
        boolean b=multi.loadSequences(filename);
        if (b) {
            Sequence seq=multi.getSequences().get(0);
            if (seq!=null) {
                updateSequenceInfos(this);
            }
              return true;
        } else {
            return false;
        }
    }

////////////////////////////////////////////////////////////////////////////////
/// Iterator

    Vector<Integer>next=new Vector<Integer>();
    int counter=0;
    int maxid=-1;
    public boolean hasNext() {        
        //next=df.getAllSequenceID();
       if (next.size()==0) {
           next=df.getAllSequenceID();           
           maxid=next.size();
       }
       return (this.counter<maxid);
    }

    public Object next() {
        return new Sequence(next.get(counter++));
    }

    public void remove() {
        Sequence s=new Sequence(counter-1);
        s.removeFromDatabase();
    }

    @Override
    public Vector<Integer> getAllId() {
        return next;
    }

    public boolean exists(Integer id) {
        return df.existsSequence(id);
    }

////////////////////////////////////////////////////////////////////////////////
/// HELPER

        /**
         * Fonction accessoire servant faire une fausse Alignment d'ADN de longueur size
         * Cette longueur est la taille de départ de la séquence
         * Utilisé pour faire la séquence ancestrale
         * @param size, longueur de la chaine, normalement seqMaxLength/2
         * @return Une Alignment comprenant {ATGC} de longueur size
         */
        public static String randomDNA(int size) {
            String DNA="";
            Random r = new Random();
            for (int i=0; i<size;i++) {
                switch (r.nextInt(4)) {
                    case 0:DNA+="A";break;
                    case 1:DNA+="T";break;
                    case 2:DNA+="G";break;
                    case 3:DNA+="C";break;
                }
            }
            return DNA;
        }
        
        /**
         * Helper method to try to load information from the sequence name
         * Ex. When we have fasta from Ncbi and name is:
         * gi|45551206|gb|AY523637.1| Gallus gallus adiponectin mRNA, complete cds
         */
    
        public void loadInfoFromName() {
            //Loading variable
            boolean nextGi=false;
            boolean nextAccession=false;
            //1. replace | char
            String tmp=replaceByTab(this.name, '|');
            //2. We found id?
            String[] data=tmp.split("\t"); //split with tab
            if (data!=null&&data.length>1) {
               //HANDLE ENsembl
                //Config.log(data[0]+data[1]+data[2]);
                if (data[1].startsWith("ENS")) {
                    this.setAccession_referee(data[0]);
                    this.setAccession(data[1]);
                    this.setName(data[0]+" "+data[2]); //We want the db in the name
                    this.setGi(data[1]);
                } else {
                    //Note assume the name is the last token
                    this.name=data[data.length-1].trim(); //We remove starting and ending space
                    for (int i=0; i<data.length-1;i++) {
                        if (nextGi) {
                            nextGi=false;
                            this.setGi(data[i]);
                        }
                        if (nextAccession) {
                            nextAccession=false;
                            this.setAccession(data[i]);
                        }
                        
                        if (data[i].equals("gi")) nextGi=true;
                        
                        if (data[i].equals("gb")||data[i].equals("dbj")||data[i].equals("ref")||data[i].equals("emb")||data[i].equals("lcl")||data[i].equals("embl")||data[i].equals("ejb")) {
                            this.setAccession_referee(data[i]);
                            nextAccession=true;
                        }
                    }
                }
            }
            //--This will try to generate an abbreviation from the name
            if (this.getAbbreviate().isEmpty()) this.GenerateAbbreviate();
        }

            /**
             * Helper methode to replace a char by a tab
             * @param stri the String to parse
             * @param toReplace the char to replace by a TAB (\t)
             * @return a String containing tab inst
             */
            public static String replaceByTab(String stri, char toReplace) {
                String tmp="";
                for (int i=0; i<stri.length();i++) {
                    char c=stri.charAt(i);
                    if (c==toReplace) {
                        tmp+="\t";
                    } else {
                        tmp+=c;
                    }
                }
                return tmp;
            }

           /**
             * Helper methode to replace a char by a tab
             * @param stri the String to parse
             * @param toReplace the char to replace by toChar
             * @param toChar
             * @return a String containing tab inst
             */
            public static String replaceChar(String stri, char toReplace, char toChar) {
                if (stri.isEmpty()||stri==null) return stri;
                String tmp="";
                for (int i=0; i<stri.length();i++) {
                    char c=stri.charAt(i);
                    if (c==toReplace) {
                        tmp+=toChar;
                    } else {
                        tmp+=c;
                    }
                }
                return tmp;
            }

////////////////////////////////////////////////////////////////////////////////
/// Getter / Setter


    /**
     * Return the size of the Sequence StringBuilder
     * @return the len of this sequence in bp (including gap)
     */
    public int getSize() {
        return sequence.length();
    }

    /**
     * Return the length of the sequence in bp as found in the len variable (ex. from Ncbi)
     * or if 0 the length of the Sequence StringBuilder
     * @return the len of this sequence in bp (including gap)
     */
    public int getLen() {
        if (len==0) setLen(sequence.length());
        return len;
    }
    
    /**
     * @return The name of the ADN sequence
     */
    public String getSequence() {
        return sequence.toString();
    }    
    
    /**
     * @return The name of the ADN sequence
     */
    public String getSequenceName() {
        return name;
    }
    
    /**
     * Add directly a string to the String buffer
     * @param stri
     */
    public void addToSequence(String stri) {
        //stri=replaceChar(stri, '.', 'N');
        //stri=replaceChar(stri, '?', 'N');
        this.sequence.append(stri);
    }

    public void addToQuality(String stri) {
        //stri=replaceChar(stri, '.', 'N');
        //stri=replaceChar(stri, '?', 'N');
        this.quality.append(stri);
    }

    /**
     * Special getter for the sequence with simple clipping
     * @param start (in bp)
     * @param end (in bp)
     * @return string representation of sequence
     */
    public String getSequenceClipping(int start, int end) {
        //String tmpseq=sequence.toString();
        if (start!=0&&end==0&&start>-1&&start<sequence.length()) {
            return sequence.substring(start,sequence.length());
        } else if (start==0&&end!=0&&end>-1&&end<sequence.length()) {
            return sequence.substring(0,end);
        } else if (start!=0&&end!=0&&start<end&&start>-1&&end<sequence.length()) {
            return sequence.substring(start,end);
        } else if (start<sequence.length()&&end>sequence.length()) {
            return sequence.substring(start,sequence.length());
        } else {
            return sequence.toString();
        }
        //return tmpseq;
    }
       
   /**
     * Special getter for the sequence with simple clipping but remove at
    *  the same time the segment between start and end
     * @param start (in bp)
     * @param end (in bp)
     * @return string representation of the removed sequence
     */
    public String Remove(int start, int end) {
        try {
        String tmpseq="";       //returned removed segment
        String newsequence="";  //temporary holder for the new sequence
                                //without segement

        if (end<start) {
            int t=end;
            end=start; start=t;
        }
        if (start==0) {
           if (end>sequence.length()) end=sequence.length();
           tmpseq=sequence.substring(start,end);
           newsequence=sequence.substring(end,sequence.length());
           sequence=new StringBuilder();
           sequence.append(newsequence);
            return tmpseq;
        } else
          if (start>-1) {
            tmpseq=sequence.substring(start,end);
            if (end>sequence.length()) end=sequence.length();
            newsequence=sequence.substring(end,sequence.length());
            sequence=new StringBuilder();
            sequence.append(newsequence);
            return tmpseq;
          }
        } catch(Exception e) {e.printStackTrace();}
        return "";
    }

    
    /**
     * @param sequence the sequence to set
     */
    public void setSequence(String sequence) {
        try {
            this.sequence = new StringBuilder();
            this.sequence.append(sequence);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //TO DO ADD SEQUENCE CHAR RECOGNITION
        //addToSequence(sequence);
    }

     /**
     * @param sequence the sequence to set
     */
    public void setQuality(String quelity) {
        this.quality=new StringBuilder();
        this.quality.append(quality);
        //TO DO ADD SEQUENCE CHAR RECOGNITION
        //addToSequence(sequence);
    }

    public String getQuality() {
        return this.quality.toString();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        //We remove non-tree safe char
        name=replaceChar(name, '(', '[');
        name=replaceChar(name, ')', ']');
        name=replaceChar(name, ',', ' ');
        this.name=name.trim();
        this.name = name;
    }

    /**
     * @return the orientation
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * @param orientation the orientation to set
     */
    public void setOrientation(String orientation) {
        this.orientation = orientation;
        if (orientation.equalsIgnoreCase("plus")||orientation.equalsIgnoreCase("5'")) this.orientation="+";
        if (orientation.equalsIgnoreCase("minus")||orientation.equalsIgnoreCase("3'")) this.orientation="-";

    }
    
    public boolean isSelected() {
        return this.selected;
    }

     public boolean isOutgroup() {
        return this.outgroup;
    }
    
    public void setSelected(boolean b) {
        this.selected=b;
    }

     public void setOutgroup(boolean b) {
        this.outgroup=b;
    }

    
    @Override
    public String toString() {
        //return this.getSequence();
        String sname=name;
        if (sname.length()>38) sname=sname.substring(0, 38);
        String str=String.format("%40s     %s",sname, getSequence());
        return str;

    }

    public String toStringDetails() {
        //HashMap and rapid calculation of caracter
        HashMap<Character, Float> component=new HashMap<Character, Float>();
        //For the order of the first character
        if (getUnit().equals("bp")) {
            component.put('A', 0.0f);
            component.put('T', 0.0f);
            component.put('G', 0.0f);
            component.put('C', 0.0f);
            component.put('U', 0.0f);
            component.put('-', 0.0f);
        }
        for (Character c:this.getSequence().toCharArray()) {
             if (component.containsKey(c)) {
                 Float i=component.get(c)+1; //Lazy for
                 component.put(c, i);
             } else component.put(c, 1.0f);
        }
        //--Create a dummy copy for the sequence
        Sequence s=this.clone();
         //--return String
        String stri="";
        char special='|';
        String sname=">";
        if (!getGi().equals("")) sname+="gi"+special+getGi()+special;
        if (!getAccession().equals("")) sname+=getAccession_referee()+special+getAccession()+special;
        sname+=getName();
        stri+=sname+"\n";
        int count=0;
        while (s.getSize()>0) {
            stri+=s.Remove(0, 10)+" "+s.Remove(0,10)+" "+s.Remove(0, 10)+" "+s.Remove(0, 10)+" "+s.Remove(0, 10)+" "+(getSize()-s.getSize())+"\n";
        }
        stri+="Total "+getLen()+" "+getUnit()+"\n";
        for (Character c:component.keySet()) {
            stri+=String.format("\t %s : (%f%%) %f \n",c,component.get(c)/getSize(),component.get(c));
        }
        return stri;
    }

    /**
     * @return the gi
     */
    public String getGi() {
        return gi;
    }

    /**
     * @param gi the gi to set
     */
    public void setGi(String gi) {
        this.gi = gi;
    }

    /**
     * @return the accession
     */
    public String getAccession() {
        return accession;
    }

    /**
     * @param accession the accession to set
     */
    public void setAccession(String accession) {
        this.accession = accession;
    }

    /**
     * @return the accession_referee
     */
    public String getAccession_referee() {
        return accession_referee;
    }

    /**
     * @param accession_referee the accession_referee to set
     */
    public void setAccession_referee(String accession_referee) {
        this.accession_referee = accession_referee;
    }

    /**
     * @param len the len to set
     */
    public void setLen(int len) {
        this.len = len;
    }

    /**
     * @return the selected_for_remove
     */
    public boolean isSelected_for_remove() {
        return selected_for_remove;
    }

    /**
     * @param selected_for_remove the selected_for_remove to set
     */
    public void setSelected_for_remove(boolean selected_for_remove) {
        this.selected_for_remove = selected_for_remove;
    }

    /**
     * @return the displayed
     */
    public boolean isDisplayed() {
        return displayed;
    }

    /**
     * @param displayed the displayed to set
     */
    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

////////////////////////////////////////////////////////////////////////
/// Biological information

    /**
     * Methode renvoyant des statistiques sur la sequence.
     * 
     * @return une String sous la forme (100% A 100% T %100 G 100% C)
     *         representant le % de chaque nucleotide dans la sequence
     */
    String getStat() {
       int a=0;
       int t=0;
       int g=0;
       int c=0;
       int n=0;

       int total=sequence.length();
       for (char ch:sequence.toString().toUpperCase().toCharArray()) {
           switch (ch) {
               case 'A':a++; break;
               case 'T':t++; break;
               case 'G':g++; break;
               case 'C':c++; break;    //LOL
               case 'N':n++; break;
               case '-':n++; break;
               //On ne calcul pas les autres caractères car ils sont normalement absent.
           }
       } //end for
       a*=100;t*=100;g*=100;c*=100;n*=100;
       a/=total;t/=total;g/=total;c/=total;n/=total;
       return String.format("Stats: %d pb (%2d%% A %2d%% T %2d%% G %2d%% C %2d%% N)",total,a,t,g,c,n);
    }

////////////////////////////////////////////////////////////////////////
/// Clone

    @Override
    public Sequence clone() {
        Sequence tmp=new Sequence();
        tmp.setName(this.name);
        tmp.setGi(this.gi);
        tmp.setAccession(this.accession);
        tmp.setOrientation(this.orientation);
        tmp.setAccession_referee(this.accession_referee);
        tmp.setSequence(this.sequence.toString());
        tmp.setDisplayed(this.displayed);
        tmp.setSelected(this.selected);
        tmp.setSelected_for_remove(this.selected_for_remove);
        return tmp;
    }

    /**
     * Helper function to generate a Sequence abbreviate
     */
    public String GenerateAbbreviate() {
        String[] s=this.getName().split(" ");
        String n="";
        int count=0;
        try {
        for (String S:s) {
            Character c=S.charAt(0);
            n+=(Character.isLetter(c)?c:"");
            count++;
            if (count==10) break;
        }
        } catch(Exception e){}
        String acc=this.getAccession();
        if (acc.length()==0) {
            n=n.substring(0, Math.min(2, n.length()));
        }
        //--Replace . by _
        acc=replaceChar(acc, '.', '_');
        n+="_"+acc;
        this.setAbbreviate(n);
        return n;
    }

    /**
     * @return the abbreviate
     */
    public String getAbbreviate() {
        return abbreviate;
    }

    /**
     * @param abbreviate the abbreviate to set
     */
    public void setAbbreviate(String abbreviate) {
        this.abbreviate = abbreviate;
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
     * @return the timeAdded
     */
    public String getTimeAdded() {
        return timeAdded;
    }

    /**
     * @param timeAdded the timeAdded to set
     */
    public void setTimeAdded(String timeAdded) {
        this.timeAdded = timeAdded;
    }

    /**
     * @return the original_id
     */
    public int getOriginal_id() {
        return original_id;
    }

    /**
     * @param original_id the original_id to set
     */
    public void setOriginal_id(int original_id) {
        this.original_id = original_id;
    }

//    /**
//     * @return the sequenceStats_id
//     */
//    public int getSequenceStats_id() {
//        return sequenceStats_id;
//    }
//
//    /**
//     * @param sequenceStats_id the sequenceStats_id to set
//     */
//    public void setSequenceStats_id(int sequenceStats_id) {
//        this.sequenceStats_id = sequenceStats_id;
//    }

    /**
     * @return the sequence_type
     */
    public String getSequence_type() {
        return sequence_type;
    }

    /**
     * @param sequence_type the sequence_type to set
     */
    public void setSequence_type(String sequence_type) {
        this.sequence_type = sequence_type;
    }

    /**
     * @return the selected_for_grouping
     */
    public boolean isSelected_for_grouping() {
        return selected_for_grouping;
    }

    /**
     * @param selected_for_grouping the selected_for_grouping to set
     */
    public void setSelected_for_grouping(boolean selected_for_grouping) {
        this.selected_for_grouping = selected_for_grouping;
    }

    /**
     * Return the UNIT of this sequence (aa or bp)
     * @return
     */
     public String getUnit() {
        return (this.getSequence_type().equals("AA")?"aa":"bp");
    }


     public workflow_properties returnProperties() {
         workflow_properties tmp=new workflow_properties();
         if (id==0) this.saveToDatabase();
             tmp.put("input_sequence_id", this.getId());
             tmp.put("output_sequence_id", this.getId());
         return tmp;
     }

    /**
    * Output a fasta representation of this sequence
    */
    public String outputFasta() {
        char special='|';
        String sname=">";
        if (!getGi().equals("")) sname+="gi"+special+getGi()+special;
        if (!getAccession().equals("")) sname+=getAccession_referee()+special+getAccession()+special;
        sname+=getName()+"\n"+getSequence();
        return sname;
    }

    public int distance(String sequence2) {
        String seq1=this.getSequence();
        String seq2=sequence2;
        int len1;
        int len2;
        int[][] m; //matrice edition
        len1=seq1.length();
        len2=seq2.length();

        m = new int[len1+1][len2+1];

        for (int i=0; i<len1+1; i++) {
            m[i][0]=i;
        }
        for (int j=0; j<len2+1; j++) {
            m[0][j]=j;
        }

        for (int i=1; i<len1+1;i++) {
            for (int j=1; j<len2+1; j++) {
                int d=m[i-1][j-1];
                if (seq1.charAt(i-1)!=seq2.charAt(j-1)) {
                    d++;
                }
                int u=m[i][j-1]+1;
                int l=m[i-1][j ]+1;
                int value=Math.min(Math.min(d,u), l);
                m[i][j]=value;
            }
        }
        return m[len1][len2];
    }//End distance

    /**
     * @return the RunProgram_id
     */
    public int getRunProgram_id() {
        return RunProgram_id;
    }

    /**
     * @param RunProgram_id the RunProgram_id to set
     */
    public void setRunProgram_id(int RunProgram_id) {
        this.RunProgram_id = RunProgram_id;
    }

    public String getBiologicType() {
        return "Sequence";
    }

    @Override
    public String toHtml() {
        StringBuilder st=new StringBuilder();
        report r = new report();
        char nuc=':'; //--Not used caracter for starting point...
        char oldnuc=':'; //--Not used caracter for starting point...
        String tmp=getSequence();
        //st.append(results.getColorsNucProtStyle());
        //st.append("<span class=\"SeqName"+getName()+"</span><br>");
        //st.append("<b>"+">"+getName()+"</b><br>\n");

        st.append("<br><b>"+"Lenght: "+this.getLen()+" "+this.getUnit()+"</b><br>\n");
        st.append("<span class=\"paml\">");
        if (this.getSequence_type().equals("AA")){
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
        return st.toString();
    }

    public String getNameId(int id) {
        return df.getSequenceName(id);
    }
    
    public String getFileNameId(int id) {
        return "";
    }
    
    public void setData(String data) {
        this.setSequence(data);
     }

     public String outputPhylip() {
        //            10     20
        //        seq0       -GTA-GCGCT -T--C-----
        //        seq1       TGTA-TTGC- CTG--C-T--
        StringBuffer pw=new StringBuffer();
        pw.append("1"+"        "+this.getSize()+"\n");
        String sname="";
        sname=getName()+"             *";
        sname=sname.substring(0,9);
        pw.append(sname+" "+this.getSequence()+"\n");
        return pw.toString();
    }

    public String getFasta() {
        return this.outputFasta();
    }

    public String getPhylip() {
        return this.outputPhylip();
    }

    public String getExtendedString() {
        return this.getId()+"; "+this.getAbbreviate()+"; "+this.getAccession()+"; "+this.getAccession_referee()+"; "+this.getGi()+"; "+this.getName()+"; "+this.getSequence()+"; "+this.getOrientation()+"; "+this.getLen()+"; "+this.getSequence_type()+"; ";
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
