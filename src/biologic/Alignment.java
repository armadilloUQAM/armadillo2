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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import results.report;
import workflows.workflow_properties;

/** 
 * Alignment class
 *
 * @author Etienne Lord
 * @since Mars 2009
 */
    public class Alignment extends MultipleSequences implements Serializable, Biologic, Iterator {

    //--Sequence creation tool
    protected String seqAncestrale="";      //La Sequence ancestrale
    protected String seqConsensus="";      //La sequence consensus :: Note: très partielle
    // Random Attribute
    private String[] seqr={};             //Buffer for sequence
    private int seqNumber=10;             //Nb sequence to create
    private int seqMaxLength=100;         //La longueur (pb) des sequences a generer
    private boolean selected=false;
    private boolean selected_for_remove=false;   //mark for remove from project
    private boolean selected_for_grouping=false;   //mark for remove from project

    //String[] seqname={"AAAA","BBBB","CCCC","DDDD","EEEE","FFFF","GGGG","HHHH","IIII","JJJJ","KKKK"};   // random name

    // Debug and programming variables
    private boolean debug=false;                    //Debug mode?
    private static final long serialVersionUID = 5333996282545799141L; //Chaque classe serializable devrait definir son UID
    
    //Switch MODE:
        // 1: INSERTION seulement
        // 2: INSERTION+DELETION
        // 3: INSERTION+DELETION+SUBSTITUTION
        // 4: INSERTION+DELETION+SUBSTITUTION+GAP (random)
    public static final int mode_INSERTION=1;
    public static final int mode_INSERTION_DELETION=2;
    public static final int mode_INDEL_SUBSTITUTION=3;
    public static final int mode_ALL=4;



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// Constructor

    /**
     * Default contructor
     */
    public Alignment() {name="UnknownAlignment";}

    public Alignment(int id) {
        this.loadFromDatabase(id);
    }

    public Alignment(String filename) {
        loadSequences(filename);
    }

    public Alignment(int len, int nombre) {
        name="UnknownAlignment";
        seqNumber=nombre;
        seqMaxLength=(len>10?len:11);
        generateRandomSequence(mode_INDEL_SUBSTITUTION); //Used default parameter
    }

    /**
     * Generate random sequences
     * @param len (in bp)
     * @param mode (refers to the different mode of INDEL and SUBSTITUTION)
     */
    public Alignment(int len, int nombre, int mode) {
        seqNumber=nombre;
        seqMaxLength=len;
        generateRandomSequence(mode);
    }

 ////////////////////////////////////////////////////////////////////////////////
    // Database function

    @Override
    public boolean loadFromDatabase(int id) {
        Alignment alignment=df.getAlignment(id);
        if (alignment.getId()>0) {
        this.name=alignment.getName();
        this.id=id;
        this.note=alignment.note;
        this.seq.clear();
        this.seq.addAll(alignment.getSequences());
        return true;
        } else return false;
    }

    @Override
    public boolean saveToDatabase() {
        id=0;
        id=df.addAlignment(this);
        return (id==0?false:true);
    }

    @Override
    public boolean removeFromDatabase() {
       return df.removeAlignment(this);
    }

    @Override
    public boolean updateDatabase() {
        return df.updateAlignment(this);
    }


    /**
     * Special function to get the original sequence_id and name from the database
     * when sequence_id are the sequence name...
     * note: sequence should start with ax
     * OTHERWISE You should use the common function;
     * @param filename
     * @return
     */
    public boolean loadFromFile(String filename) {
        loadSequences(filename);
        //--Remove empty sequences
        for (int i=this.getNbSequence()-1;i>-1;i--) {
            Sequence s=this.getSequences().get(i);
            if (s.getName().trim().isEmpty()&&s.getSequence().trim().isEmpty()) this.getSequences().remove(i);
        }        
        if (getNbSequence()>0) {
            for (Sequence s:getSequences()) {
                try {
                    String sname=(s.getName().startsWith("AZ")?s.getName().substring(2):s.getName());
                    int original_sequence_id=Integer.valueOf(sname);
                    InfoSequence info=df.getInfoSequence(original_sequence_id);
                    //System.out.println(info);
                    s.setName(info.getName());
                    s.setGi(info.getGi());
                    s.setAccession(info.getAccession());
                    s.setAccession_referee(info.getAccession_referee());
                    s.setAbbreviate(info.getAbbreviate());
                    s.setSequence_type(info.getSequence_type());
                    s.setOriginal_id(original_sequence_id);
                    //System.out.println(s.getAbbreviate());
                } catch(Exception e) {}
            }
            return true;
        } else
            return false;
    }

      /**
     * Special function to get the original sequence_id and name from the database
     * when sequence_id are the sequence name...
     * note: sequence should start with ax
     * OTHERWISE You should use the common function;
     * @param filename
     * @return
     */
    public boolean loadFromClustalW2File(String filename) {
        readSequenceFromClustalW2(filename);
        if (getNbSequence()>0) {
            for (Sequence s:getSequences()) {
                try {
                    String sname=(s.getName().startsWith("AZ")?s.getName().substring(2):s.getName());
                    int original_sequence_id=Integer.valueOf(sname);
                    InfoSequence info=df.getInfoSequence(original_sequence_id);
                    //System.out.println(info);
                    s.setName(info.getName());
                    s.setGi(info.getGi());
                    s.setAccession(info.getAccession());
                    s.setAccession_referee(info.getAccession_referee());
                    s.setAbbreviate(info.getAbbreviate());
                    s.setSequence_type(info.getSequence_type());
                    s.setOriginal_id(original_sequence_id);
                    //System.out.println(s.getAbbreviate());
                } catch(Exception e) {}
            }
            return true;
        } else
            return false;
    }

////////////////////////////////////////////////////////////////////////////////
/// Iterator


    @Override
    public boolean hasNext() {
       //next=df.getAllSequenceID();
       if (next.size()==0) {
           next=df.getAllAlignmentID();
           maxid=next.size();
       }
       return (this.counter<maxid);
    }

    @Override
    public Object next() {
        return new Alignment(next.get(counter++));
    }

    @Override
    public void remove() {
        Alignment s=new Alignment(counter-1);
        s.removeFromDatabase();
    }

    @Override
     public boolean exists(Integer id) {
        return (df.getAllAlignmentID().contains(id));
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// Output function and Getter/Setter

    /**
     * Liste les sequence et la distance de la sequence ancestrale si presente
     */
    void PrintSequence() {
        Config.log(getSeqAncestrale());
        int index=0;
        for (Sequence s:seq) {
            Config.log(s.getSequence()+" "+distance(s.getSequence()));
            index++;
        }
    }

    void PrintConcensus() {
        if (seqConsensus.equals("")) computeConcensus();
            Config.log("\t"+seqConsensus+" "+distance(seqConsensus)); 
    }

    public String getConcensus() {
        if (seqConsensus.equals("")) computeConcensus();
            return seqConsensus;
    }
  
    public String readAncestorFromFasta(String filename) {
        String ancestor=""; //the returned ancestor
        String ids="";      //Not returned but the parent sequence
        try {
            BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
            Vector<String> stri=new Vector<String>();
            //Read the file
            while (br.ready()) {
                stri.add(br.readLine());
            }
            br.close();
            //Process :: We read like a fasta file
            int len=0; //length of the greatest fasta identifier::Should be the one
            for (int i=0; i<stri.size(); i++) {
                //We have a fasta definition, we check the len
                //Bigger than before, we add...
                if (stri.get(i).startsWith(">")
                    && stri.get(i).length()>len) {
                    len=stri.get(i).length();
                    ids=stri.get(i);
                    i++;
                    ancestor=stri.get(i);
                }
            } //end for
        } catch(Exception e) {Config.log("Error with "+filename); return "";} 
        return ancestor;
  }


    boolean outputAncestor(String filename) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            pw.println(getSeqAncestrale());
            pw.flush();
            pw.close();
        } catch (Exception e) {return false;}
        return true;
    }

    boolean outputConcensus(String filename) {
        if (seqConsensus.equals("")) computeConcensus();
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            pw.println(seqConsensus);
            pw.flush();
            pw.close();
        } catch (Exception e) {return false;}
        return true;
    }

    ////////////////////////////////////////////////////////////////////
    /// Random Alignment fonction

    /**
     * Main function to generate the random Alignment
     */
    public void generateRandomSequence(int mode) {
        if (debug) Config.log("Creating "+seqNumber+" random sequence of "+seqMaxLength+" bp. ");
      
        if (seqMaxLength<11) {
            System.out.println("Warnning. Unable to generate for sequences smaller than 11.");
            return;
        }
        seqAncestrale=Sequence.randomDNA(seqMaxLength-10);            
       seq.clear();
       seqr=new String[seqNumber];
       //Config.log(this.seqNumber+" "+this.seqMaxLength);
       //Seq
        for (int i=0; i<seqNumber;i++) {
            seqr[i]=seqAncestrale;
        }
       while (seqr[0].length()!=seqMaxLength) {
            randomINDELSUB(mode);
       }

       for (int index=0; index<seqr.length;index++) {
            Sequence tmp=new Sequence();
            tmp.setName("RAND"+index);                
            tmp.setAbbreviate("RAND"+index);
            tmp.setSequence(seqr[index]);
            tmp.setOrientation("+");
            seq.add(tmp);
       }
       //Destroy tmp array
       seqr=new String[0];
       if (debug) Config.log("done. "+seqAncestrale+" "+seqAncestrale.length()+" bp");
        
    }

    /**
     * Add a random INDEL ou SUB ou GAP
     * to some of the Alignment
     */
    void randomINDELSUB(int mode) {
        Random r = new Random();
        String mask=randomMask(); //LES SEQUENCE A EDITÉ
        int position_in_sequence=r.nextInt(seqr[0].length()); //LA POSITION DANS LES SEQUENCE
        //Switch MODE:
        // 1: INSERTION seulement
        // 2: INSERTION+DELETION
        // 3: INSERTION+DELETION+SUBSTITUTION
        // 4: INSERTION+DELETION+SUBSTITUTION+GAP (random)
          
        switch(r.nextInt(mode)) {
            case 0:       //INSERTION
                    String IN=Sequence.randomDNA(1);
                    int index=0;        //index de la Alignment
                    for (char ch:mask.toCharArray()) {
                        try {
                        String before=seqr[index].substring(0,position_in_sequence);
                        String after=seqr[index].substring(position_in_sequence);
                        seqr[index]=before+(ch=='.'?IN:"-")+after;
                        } catch (Exception e) {}
                        index++;

                    }
                    break;
            case 1: //DELETION
                    index=0;        //index de la Alignment
                    for (char ch:mask.toCharArray()) {
                        try {
                        String before=seqr[index].substring(0,position_in_sequence);
                        String after=seqr[index].substring(position_in_sequence);
                        if (ch=='.') seqr[index]=before+after.substring(1)+"-";
                        }catch (Exception e) {}
                        index++;
                    }
                    break;
            case 2:       //SUBSITUTION SANS MATRICE!
                    String SUB=Sequence.randomDNA(1);
                    index=0;        //index de la Sequence
                    for (char ch:mask.toCharArray()) {
                        try {
                        String before=seqr[index].substring(0,position_in_sequence);
                        String after=seqr[index].substring(position_in_sequence);
                        seqr[index]=before+(ch=='.'?SUB:after.charAt(0))+after.substring(1);
                        } catch(Exception e) {}
                        index++;
                    }
                    break;
            case 3:       //GAP
                    index=0;        //index de la Sequence
                    for (char ch:mask.toCharArray()) {
                        try {
                        String before=seqr[index].substring(0,position_in_sequence);
                        String after=seqr[index].substring(position_in_sequence);
                        seqr[index]=before+"-"+after;
                        } catch (Exception e) {}
                        index++;
                    }
                    break;
        } //End switch mode

    }

    /**
     * Generate a random mask of editing Sequence
     * we have a . if we need to edit this Sequence
     * USED BY randomINDELSUB
     * @return a mask String
     */
    String randomMask() {
        Random r = new Random();
        String mask="";
        for (int i=0; i<seqNumber;i++) {
            mask+=(r.nextInt(2)==0?" ":".");
        }
        return  mask;
    }

    ////////////////////////////////////////////////////////////////////
    /// Distance d'édition (en programmation dynamique)

    /**
     * Calculate the distance from ancestral Alignment
     * @param seqTrouve
     * @return
     */
    int distance(String seqTrouve) {
        String seq1=getSeqAncestrale();
        String seq2=seqTrouve;
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
                if (seq1.charAt(i-1)!=seq2.charAt(j-1)&&seq2.charAt(j-1)!='N') { //I added N since its ambigious
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
     * Calculate the distance d'étition de différentes séquences
     * @param seqSource 
     * @param seqTrouve
     * @return distance calculated
     */
    int distance(String seqSource, String seqTrouve) {
        String seq1=seqSource;
        String seq2=seqTrouve;
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
                if (seq1.charAt(i-1)!=seq2.charAt(j-1)&&seq2.charAt(j-1)!='N') { //I added N since its ambigious
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

    void computeConcensus() {
        seqConsensus="";
        //Iterate over each character of eachs sequences
        for (int seqchar=0; seqchar<getSequenceSize();seqchar++) {
            float A=0;
            float T=0;
            float G=0;
            float C=0;
            float gap=0;
            for (int seqn=0; seqn<getNbSequence();seqn++) {
                char c=seq.get(seqn).getSequence().charAt(seqchar);
                //We omit gap and N
                switch(c) {
                    case 'A': A++; break;
                    case 'T': T++; break;
                    case 'G': G++; break;
                    case 'C': C++; break;
                    case '-': gap++; break;
                }
            }
        //stat
            A=A/seqMaxLength;
            T=T/seqMaxLength;
            G=G/seqMaxLength;
            C=C/seqMaxLength;
            gap=gap/seqMaxLength;
            char ret='N';
            if (A>T&&A>G&&A>C) ret='A';
            if (T>A&&T>G&&T>C) ret='T';
            if (G>T&&G>A&&G>C) ret='G';
            if (C>T&&C>G&&C>A) ret='C';
            if (gap>A&&gap>T&&gap>G&&gap>C) ret='-'; //by default, gap is less important
            seqConsensus+=ret;
        }
    }

    /**
     * @return the seqAncestrale
     */
    public String getSeqAncestrale() {
        return seqAncestrale;
    }

    ////////////////////////////////////////////////////////////////////
    /// Load function

    boolean readAncestor (String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
            String stri=br.readLine();
            seqAncestrale=stri;
            br.close();
        } catch(Exception e) {return false;}
        return true;
    } //end 

    @Override
    public workflow_properties returnProperties() {
        workflow_properties tmp=new workflow_properties();
        if (id==0) this.saveToDatabase();
        tmp.put("input_alignment_id", this.getId()); 
        tmp.put("output_alignment_id", this.getId());
        return tmp;
     }

    public String getBiologicType() {
        return "Alignment";
    }

    public String getNameId(int id) {
        return df.getAlignmentName(id);
    }
    
    public String getFileNameId(int id) {
        return "";
    }
    
    /**
     * This will delete all colonne containning the number of minimum Gap
     * ex. 0: remove all colonne containing gap, 1: permit 1 gap...
     */
    public void removeGapFromAlignment(int minimumGap) {
        //--Compute length        
    }

    public void setData(String data) {
        this.getSequences().clear();
        Util u=new Util("temp.txt");
        u.println(data);
        u.close();
        this.loadFromFile("temp.txt");
        Util.deleteFile("temp.txt");
    }

    @Override
    public String toHtml() {
        StringBuilder st=new StringBuilder();
        report r = new report();

        st.append("<b>Alignment "+getName()+" with ID ["+getId()+"]</b><br>");
        char nuc=':'; //--Not used caracter for starting point...
        char oldnuc=':'; //--Not used caracter for starting point...

        st.append("<table border=\"0\" bgcolor=\"#FFFFFF\"><thead><tr><td><b>Name</b></td><td><b>Sequences</b></td></tr></thead><tbody>");
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
            st.append("<tr><td>"+S.getName().substring(0,Math.min(55, S.getName().length()))+"</td><td>");
            st.append("<span class=\"paml\">");
            if (S.getSequence_type().equals("AA")){
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

    /**
     * Return a fasta representation of the alignment
     * @return
     */
    public String getFasta() {
        return this.outputFasta();
    }

   /**
    * Return a phylip representation of the alignment
    * @return
    */
    public String getPhylip() {
        return this.outputPhylip();
    }

    /**
     * TO DO
     * Calculate average per-column percentage identity of alignment.
     * sum_columns (# identical characters in column / total # non-gap characters in column) / num_columns
     * Implementation of Patchet L and Schwartz AS C++ code
     * see: Multiple Alignment by Sequence Annealing
     * ECCB 2006:(23)e24-e29
     * @return
     */
    public double percent_id() {
//        double id=0;
//        int number_of_cols=0;
//
//        for (int c=0; c<this.getSequenceSize();c++) {
//            HashMap<Integer, Integer>count_char_column=new HashMap<Integer, Integer>();
//
//        }
        return 0.0d;
    }

    
    //--See getColumn in MultipleSequences
//    /**
//     * Return all the columns in the alignment
//     * @param number
//     * @return
//     */
//    public String getColumn(int number) {
//        Vector<String>column=new Vector<String>();
//        //--Iterate over the Size of Sequence and allocate memory
//        for (int n=0; n<this.getSequenceSize();n++) {
//           String col="";
//            for (int i=0; i<this.getNbSequence();i++) {
//                Sequence s=this.getSequences().get(i);
//                if (s.getSize()>n) {
//                    col+=s.getSequence().charAt(n);
//                } else {
//                    col+=" ";
//                }
//            }
//            column.add(col);
//        }
//        return column.toArray();
//    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
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

//double Alignment::percent_id (const unsigned start, const unsigned end) const {
//
//  double id = 0.;   // sum of per-column percent IDs
//  size_t cols = 0;  // number of columns used in calculation
//
//  for (size_t c = start; c <= end; ++c) {
//
//    // count the (maximal) fraction of identical characters in the column
//    std::map<char, size_t> char_count;
//    size_t colsize = 0; // # of non-gap characters in column
//    for (size_t r = 0; r < rows(); ++r) {
//      if (!is_gapped (r, c)) {
//	++char_count[static_cast<char> (tolower (get_char (r, c)))];
//	++colsize;
//      }
//    }
//
//    // only count columns with > 1 non-gap character
//    if (colsize < 2)
//      continue;
//
//    // find the most common character
//    std::map<char, size_t>::const_iterator max = std::max_element (char_count.begin(), char_count.end(),
//								   Util::Map_value_less<std::map<char, size_t> >());
//    // if no character appears more than once,
//    // then percent id = 0
//    if (max->second > 1)
//      id += static_cast<double> (max->second) / colsize;
//
//    // increment columns counter
//    ++cols;
//
//  }
//
//  if (cols == 0)
//    return 0;
//
//  return (id / cols);
//
//}

} //END ALIGNMENT

