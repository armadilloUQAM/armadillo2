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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import workflows.workflow_properties;

/**
 * This is a simple Matrix
 * Note: matrix are stored as text
 *       Tranformation from one form to another is posible
 *
 * @author Etienne Lord
 * @since 2010
 * Updated 2014
 */
public class Matrix  extends Phylip implements Biologic, Iterator, Serializable {
    
    
    ////////////////////////////////////////////////////////////////////////////
    /// Code from new datamatrix
    
    static Pattern p_w=Pattern.compile("(.*?)\\s{1,}", Pattern.CASE_INSENSITIVE);
    static char[] sep={'\t',';',',',' '};
    
    //public String[][] matrix; -> now data
    public ArrayList<String>workflow_name=new ArrayList<String>();
    public HashMap<String, Integer>properties_name=new HashMap<String, Integer>();
    public ArrayList<Integer>groups=new ArrayList<Integer>();
    
    
    boolean inverse_display=false; //--
    
    public static final int type_unknown=-1;
    public static final int type_phylip=0;
    public static final int type_col_row=1;
    public static final int type_col_row2=12;
    public static final int type_triangular=2;
    public static final int type_rectangular=3;
    
    public String separator="\t";
    public String escape_char="#";
    
    int last_row_total=0;
    
    
    private int id=0;
    private String matrix=""; //--A matrix
    public String[][] data;
    private String name="UnkwnownMatrix";
    private String note="";
    private int runProgram_id=0;
    private int matrix_type=type_unknown;
    public Double[] weight; //--weight vector to add
    
    public int n=0;     // Number of objects
    public int p=0;     // Number of variables
    public int row=0;   //--Synonymn for p;
    public int column=0; //--Synonymn for n;
    
    public static databaseFunction df=new databaseFunction();
    
    public Matrix() {}
    
    public Matrix(int id) {
        this.loadFromDatabase(id);
    }
    
    /**
     * This
     * @param filename
     */
    public Matrix(String filename) {
        try {
            //String[] stri=Util.InputFile(filename);
            this.setName(filename);
            this.setNote("Loaded "+filename+" on "+Util.returnCurrentDateAndTime());
            
            ArrayList<String> data=loadStrings(filename);
            for (String s:data) {
                matrix+=s+"\n";
            }
            
            if (data.size()<=2) {
                matrix="";
                Config.log("No data in matrix: "+filename);
            }
            
        } catch(Exception e) {e.printStackTrace();matrix="";}
        Util.CleanMemory();
    }
    
    public boolean loadFromFile(String filename) {
        try {
            String[] stri=Util.InputFile(filename);
            for (String s:stri) {
                matrix+=s+"\n";
            }
        } catch(Exception e) {e.printStackTrace();matrix="";}
        return true;
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    // Database function
    
    public boolean loadFromDatabase(int id) {
        Matrix un=df.getMatrix(id);
        if (un.id>0) {
            this.setMatrix(un.getMatrix());
            this.setName(un.getName());
            this.setNote(un.getNote());
            this.setRunProgram_id(un.getRunProgram_id());
            this.id=id;
            return true;
        } else {
            return false;
        }
    }
    
    public boolean saveToDatabase() {
        id=0;
        id=df.addMatrix(this);
        return (id==0?false:true);
    }
    
    public boolean removeFromDatabase() {
        return df.removeMatrix(this);
    }
    
    public boolean updateDatabase() {
        return df.updateMatrix(this);
    }
    
////////////////////////////////////////////////////////////////////////////////
/// Iterator
    
    Vector<Integer>next=new Vector<Integer>();
    int counter=0;
    int maxid=-1;
    public boolean hasNext() {
        //next=df.getAllSequenceID();
        if (next.size()==0) {
            next=df.getAllMatrixID();
            maxid=next.size();
        }
        return (this.counter<maxid);
    }
    
    public Object next() {
        return new Matrix(next.get(counter++));
    }
    
    public void remove() {
        Matrix s=new Matrix(counter-1);
        s.removeFromDatabase();
    }
    
    @Override
    public Vector<Integer> getAllId() {
        return next;
    }
    
    public boolean exists(Integer id) {
        return df.existsMatrix(id);
    }
    
    /**
     * Load data from the text representation into the
     * inline buffer..
     */
    public void decodeMatrix() {
        Scanner s = new Scanner(matrix).useDelimiter("\\w");
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * This save the matrix data to a file
     * @param filename
     * @return True if successfull
     */
    public boolean Output(String filename) {
        try {
            PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
            pw.print(matrix);
            pw.flush();
            pw.close();
            return true;
        } catch(Exception e) {return false;}
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
     * @return the matrix
     */
    public String getMatrix() {
        return matrix;
    }
    
    /**
     * @param matrix the matrix to set
     */
    public void setMatrix(String matrix) {
        this.matrix = matrix;
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
        this.name = name;
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
    
    public String getBiologicType() {
        return "Matrix";
    }
    
    public workflow_properties returnProperties() {
        workflow_properties tmp=new workflow_properties();
        if (id==0) this.saveToDatabase();
        tmp.put("input_matrix_id", this.getId());
        tmp.put("output_matrix_id", this.getId());
        return tmp;
    }
    
    @Override
    public String toString() {
        return this.getMatrix()+"\n"+this.getNote();
    }
    
    
    int position_matrix(int row, int col) {
        int number_rows=10;
        int matrix_size=10;
        if (row<col)
            return row*(number_rows-1) - (row-1)*((row-1) + 1)/2 + col - row - 1;
        else if (col<row)
            return col*(number_rows-1) - (col-1)*((col-1) + 1)/2 + row - col - 1;
        else return matrix_size;
    }
    
//-- Not implemented
//    Vector<Vector<Object>> matrix=new Vector<Vector<Object>>();
//    Vector<String>         column_name=new Vector<String>();
//    Vector<String>         row_name=new Vector<String>();
//
//    Vector current_line;
//
//    public void AddLine() {
//         Vector<Object> line;
//        if (current_line!=null) {
//             line=new Vector<Object>(current_line.size());
//        } else  {
//             line=new Vector<Object>();
//        }
//        matrix.add(line);
//        current_line=line;
//    };
//
//    /**
//     * This add a Colum to the present line
//     * The
//     * @param obj
//     */
//    public void AddColumn(Object obj) {
//        if (current_line!=null) {
//            current_line.add(obj);
//        } else {
//           AddLine();
//           AddColumn(obj);
//        }
//    };
    
    public String toHtml() {
        String str=this.matrix.replaceAll("\n", "<br>");
        return str;
    }
    
    public String getNameId(int id) {
        return df.getMatrixName(id);
    }
    
    public void setData(String data) {
        this.setMatrix(data);
    }
    
    public String getFasta() {
        return "";
    }
    
    public String getPhylip() {
        return "";
    }
    
    public String getExtendedString() {
        return toString();
    }
    
    /**
     * Get the next valid Id found in the database
     * @param start the current workflow id...
     * @return the next workflow id or 0 if not found
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
    
    /**
     * This replace any reference found in the text with the correct
     * Sequence name
     */
    public void ReplaceResultsWithSequenceName() {
        //--Replace name with correct sequence name...
        String text=getMatrix();
        Pattern find_sequence=Pattern.compile("AZ([0-9]*)");
        Matcher m=find_sequence.matcher(text);
        while(m.find()) {
            try {
                Sequence sequence=new Sequence(Integer.valueOf(m.group(1)));
                if (sequence!=null) {
                    String group=m.group();
                    text=text.replace(group, sequence.getName());
                }
            } catch(Exception e) {}
        }
        setMatrix(text);
    }
    
    public Biologic getParent() {
        return null;
    }
    
    
    
    ///////////////////////////////////////////////////////////////////////////
    /// Function
    
    //public Double min_m=Double.MAX_VALUE;
    //public Double max_m=Double.MIN_VALUE;
    
    //--Add the metric method here...
    Float[][] getEucledianDistance() {
//         if (i==j) return 0.0;
//
//          double sumSq=0.0;			//D1=0.0
//	  for (int a=1;a<=p;a++)		// do 10 j=1,p
//	  {
//		  //sumSq+=pow((w[ishort[a]]*mat[i][ishort[a]]-w[ishort[a]]*mat[j][ishort[a]]),2);
//                  sumSq+=pow((w[ishort[a]]*mat[i][ishort[a]]-w[ishort[a]]*mat[j][ishort[a]]),2);
//	  }
//      return sqrt(sumSq);
        
        return new Float[0][0];
    }
    
    //--Add the metric method here...
    Float[][] getManathanDistance() {
        double sumSq=0.0;			//D1=0.0
//	  for (int a=1;a<=p;a++)		// do 10 j=1,p
//	  {
//		  //sumSq+=pow((w[ishort[a]]*mat[i][ishort[a]]-w[ishort[a]]*mat[j][ishort[a]]),2);
//                  sumSq+=pow((w[ishort[a]]*mat[i][ishort[a]]-w[ishort[a]]*mat[j][ishort[a]]),2);
//	  }
//      return sqrt(sumSq);
        
        
        return new Float[0][0];
    }
    
    //--Add the metric method here...
    Float[][] getMahalanobisDistance() {
        return new Float[0][0];
    }
    
    //--Add the metric method here...
    Float[][] getCosineDistance() {
        int l=0;
        //--sumSq+=(p[i]-q[i])^2
        //--return sqrt(sumSq)
//    if (i==j) return 0.0;
//
//       double dot=0.0;
//          //--1. Calculate the dot product for i to centroid k
//        //printf("\tdot t 1 2 mi mj\n");
//       for (l=1; l<=p;l++)   {
//
//             //double t=((w[ishort[l]]*(mat[i][ishort[l]]))*(w[ishort[l]]*mat[j][ishort[l]]));
//              //printf("\t%f %f %f %f %f %f\n",dot, t, w[ishort[l]]*(mat[i][ishort[l]]), w[ishort[l]]*(mat[j][ishort[l]]), mat[i][ishort[l]],mat[j][ishort[l]]);
//              dot=dot+((w[ishort[l]]*(mat[i][ishort[l]]))*(w[ishort[l]]*mat[j][ishort[l]]));
//          }
//          //--2. Square root
//          double sqr_mat=0.0;
//          double sqr_centroid=0.0;
//
//          for (l=1;l<=p;l++) {
//                  sqr_mat=sqr_mat+((w[ishort[l]]*(mat[i][ishort[l]]))*(w[ishort[l]]*(mat[i][ishort[l]])));
//              }
//          sqr_mat=sqrt(sqr_mat);
//
//          for (l=1;l<=p;l++) {
//                  sqr_centroid=sqr_centroid+((w[ishort[l]]*(mat[j][ishort[l]]))*(w[ishort[l]]*(mat[j][ishort[l]])));
//              }
//          sqr_centroid=sqrt(sqr_centroid);
//          if (idebug==1) printf("%f %f %f %d<->%d\n", sqr_mat, sqr_centroid, dot, i, j);
//          if ((1-(dot / (sqr_mat*sqr_centroid)))<epsilon) return 0.0;
//          return 1-(dot / (sqr_mat*sqr_centroid));
        
        return new Float[0][0];
    }
    
    //--Add the metric method here...
    Float[][] getTanimotoDistance() {
        ////////////////////////////////////////////////////////////////////////////
        /// Tanimoto
        // public float tanimoto(ArrayList<String> a, ArrayList<String> b) {
        ArrayList<String>c=new ArrayList<String>();
//          for (String t:a) {
//              if (b.contains(t)) c.add(t);
//          }
//
//          return (c.size()/(a.size()+b.size()-c.size()));
        //}
        
        return new Float[0][0];
    }
    
    Float[][] getJaccardDistance() {
        //http://en.wikipedia.org/wiki/Jaccard_index
        return new Float[0][0];
        
    }
    
    //--Add the metric method here...
    Float[][] getTverskyIndex() {
        return new Float[0][0];
    }
    
    Float[][] getSørensenDiceIndex() {
        return new Float[0][0];
    }
    /**
     * This ouput the basic matrix (my format)
     *
     * @param filename
     */
    public void ourputRowColumn(String filename) {
        if (data==null||data.length==0) {
            //--Load the data into memory
            estimateSeparator(loadStrings());
        }
        
        Util u=new Util(filename);
        u.println(p+"\t"+n);
        for (int j=0; j<p;j++) {
            for (int i=0; i<n;i++) {
                u.print(data[i][j]+"\t");
            }
            u.println("");
        }
        u.println("=== info ===");
        u.println(this.getMatrixType());
        u.println(this.getNote());
        u.close();
    }
    
    public void ourputColumnRow(String filename) {
        if (data==null||data.length==0) {
            //--Load the data into memory
            estimateSeparator(loadStrings());
        }
        
        Util u=new Util(filename);
        u.println(p+"\t"+n);
        for (int i=0; i<n;i++) {
            for (int j=0; j<p;j++) {
                u.print(data[i][j]+"\t");
            }
            u.println("");
        }
        u.println("=== info ===");
        u.println(this.getMatrixType());
        u.println(this.getNote());
        u.close();
    }
    
    public void outputPhylip(String filename) {
        if (data==null||data.length==0) {
            //--Load the data into memory
            estimateSeparator(loadStrings());
        }
        try {
            Util u=new Util(filename);
            Config.log("Output Phylip matrix: "+filename);
            System.out.println(n+" "+p);
            u.println(""+n);
            
            for (int i=0; i<n;i++) {
                String wname="D"+(i+1);
                if (workflow_name.size()==n) {
                    wname=workflow_name.get(i-1);
                }
                if (wname.length()>8) wname=wname.substring(0, 8);
                u.print(wname+"  ");
                for (int j=0; j<p;j++) {
                    u.print(data[i][j]+" ");
                }
                u.println("");
            }
            u.println("");
            u.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void outputRMatrix(String filename) {
        if (data==null||data.length==0) {
            //--Load the data into memory
            estimateSeparator(loadStrings());
        }
        Util u=new Util(filename);
        Config.log("Output R data matrix: "+filename);
        for (int j=0; j<p;j++) {
            for (int i=0; i<n;i++) {
                u.print(data[i][j]+"\t");
            }
            u.println("");
        }
        u.close();
    }
    
    public void outputContent(String filename) {
//        normalizer norm=new normalizer();
//        //--Initialize matrix
//        ArrayList<String>pos=new ArrayList<String>();
//         for (String k:properties_name.keySet()) pos.add(k);
//        //--Add 0 to matrix
//         for (int j=0; j<properties_name.size()+1;j++) {
//             for (int i=0; i<workflow_name.size()+1;i++) matrix[j][i]=0;
//         }
//
//         System.out.println("Creating matrix normal... :"+filename);
//         for (String workflow_name:work.keySet()) {
//             WorkflowGraph w=work.get(workflow_name);
//             //--Iterate over workflow node
//             for (node n:w.node_list) {
//                     //--More stringeant String name=norm.normalize(returnProcess(n.getName()))+"["+norm.getType(n.notes)+"]";
//                    String name=norm.normalize(returnProcess(n.getName()));
//                    if (!name.isEmpty()) {
//                        int posj=pos.indexOf(name);
//                         int posi=workflow_name.indexOf(workflow_name);
//                         matrix[posj][posi]++;
//                    }
//             }
//
//         }
//
//        Util u=new Util();
//         u.open(base_dir+File.separator+filename);
//         //u.println(workflow_name.size()+"\t"+pos.size());
//         for (int i=0; i<workflow_name.size();i++) {
//         for (int j=0; j<properties_name.size();j++)  u.print(matrix[i][j]+"\t");
//             u.println("");
//         }
//         u.close();
    }
    
    
    public String toMatrixString() {
        StringBuilder st=new StringBuilder();
        st.append(this.name).append("\nn:").append(n).append(" p:").append(p).append("\n");
        for (int i=0; i<n;i++) {
            if (i<workflow_name.size()) {
                if (workflow_name.get(i).length()<20) {
                    String s=workflow_name.get(i)+"                           ";
                    st.append(s.substring(0,20)+"\t");
                }  else {
                    st.append(workflow_name.get(i).substring(0,17)+"...\t");
                }
                
            }
            for (int j=0; j<p;j++) {
                st.append(data[i][j]+"\t");
            }
            st.append("\n");
        }
        return st.toString();
    }
    
    /**
     * This return an array of n row containing the number of item in row[x]
     * @return
     */
    public int[] rowTotal() {
        int tmp[]=new int[this.n];
        
        for (int i=0; i<n;i++) {
            tmp[i]=0;
            for (int j=0; j<p;j++) {
                if (!data[i][j].isEmpty()) tmp[i]++;
            }
        }
        return tmp;
    }
    
    public Double[] rowSum() {
        Double[] tmp=new Double[this.n];
        
        for (int i=0; i<n;i++) {
            tmp[i]=0.0;
            for (int j=0; j<p;j++) {
                Double value=0.0;
                try { value=Double.valueOf(data[i][j]);} catch(Exception e){}
                tmp[i]+=value;
            }
        }
        return tmp;
    }
    
    public int[] columnTotal() {
        int tmp[]=new int[this.p];
        for (int j=0; j<p;j++) {
            tmp[j]=0;
            for (int i=0; i<n;i++) {
                if (!data[i][j].isEmpty()) tmp[j]++;
            }
        }
        return tmp;
    }
    
    public Double[] columnSum() {
        Double tmp[]=new Double[this.p];
        for (int j=0; j<p;j++) {
            tmp[j]=0.0;
            for (int i=0; i<n;i++) {
                Double value=0.0;
                try { value=Double.valueOf(data[i][j]);} catch(Exception e){}
                tmp[j]+=value;
            }
        }
        return tmp;
    }
    
    /**
     * Return each row similar to row_number
     * @param row_number
     * @return
     */
    public ArrayList<Integer> returnSimilarRow(int row_number) {
        ArrayList<Integer>tmp=new ArrayList<Integer>();
        if (row_number>this.row||row_number<0) return tmp;
        
        for (int i=0; i<n;i++) {
            if (i!=row_number) {
                boolean similar=true;
                int j=0;
                while (similar&&j<p) {
                    if (data[i][j]!=data[row_number][j++]) similar=false;
                }
                if (similar&&j==p) tmp.add(i);
            }
        }
        return tmp;
    }
    
    /**
     * Return each row similar to row_number
     * @param row_number
     * @return
     */
    public ArrayList<Integer> returnSimilarColumn(int column_number) {
        ArrayList<Integer>tmp=new ArrayList<Integer>();
        if (column_number>this.column||column_number<0) return tmp;
        
        for (int j=0; j<p;j++) {
            if (j!=column_number) {
                boolean similar=true;
                int i=0;
                while (similar&&i<n) {
                    if (data[i][j]!=data[i++][column_number]) similar=false;
                }
                if (similar&&i==n) tmp.add(j);
            }
        }
        return tmp;
    }
    
    ArrayList<String> loadStrings(String filename) {
        ArrayList<String> tmp=new ArrayList<String>();
        try {
            //Change to read UTF-8 here
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename)),"ISO-8859-1"));
            while (br.ready()) {
                tmp.add(br.readLine());
            }
            br.close();
        } catch(Exception e) {}
        return tmp;
    }
    
    public String getMatrixType() {
        switch (matrix_type) {
            case type_col_row: return "Column-Row matrix";
            case type_col_row2: return "Column-Row matrix";
            case type_phylip: return "Phylip matrix";
            case type_rectangular: return "Rectangular matrix";
            case type_triangular: return "Triangular matrix";
            case type_unknown: return  "Unknown matrix type";
            default: return "Unknown matrix type";
        }
        
    }
    
    /**
     * Use the internal data
     * @return this matrix line
     */
    ArrayList<String> loadStrings() {
        ArrayList<String> tmp=new ArrayList<String>();
        String[] tm=this.matrix.split("\n");
        tmp.addAll(Arrays.asList(tm));
        Util.CleanMemory();
        return tmp;
    }
    
    /**
     * This is the main function to read the data for the matrix.
     * It will automatically convert a matrix in text format
     * to a data representation
     * @param tmp_data
     * @return
     */
    public int estimateSeparator(ArrayList<String> tmp_data) {
        
        int max_s1=0;
        int max_s2=0;
        int max_s3=0;
        int type_s1=-1;
        int type_s2=-1;
        int type_s3=-1;
        
        String s1=tmp_data.get(0).trim();
        String s2=tmp_data.get(1).trim();
        String s3="";
        if (tmp_data.size()<3) {
            s3=s2;
        } else {
            s3=tmp_data.get(2).trim();
        }
        //--Determine type
        for (int i=0; i<sep.length;i++) {
            String str[] = s1.split(""+sep[i]);
            //count the number of blank
            int blank=0;
            for (String s:str) {
                if (s.trim().isEmpty()) blank++;
            }
            
            if (str.length-blank>max_s1) {
                type_s1=i;
                max_s1=str.length-blank;
            }
        }
        
        for (int i=0; i<sep.length;i++) {
            String str[] = s2.split(""+sep[i]);
            //count the number of blank
            int blank=0;
            for (String s:str) {
                if (s.trim().isEmpty()) blank++;
            }
            if (str.length-blank>max_s2) {
                type_s2=i;
                max_s2=str.length-blank;
            }
        }
        
        for (int i=0; i<sep.length;i++) {
            String str[] = s3.split(""+sep[i]);
            //count the number of blank
            int blank=0;
            for (String s:str) {
                if (s.trim().isEmpty()) blank++;
            }
            if (str.length-blank>max_s3) {
                type_s3=i;
                max_s3=str.length-blank;
            }
        }
        
        //CASE retangular
        if (max_s1>2&&max_s2==max_s1) {
            this.p=max_s1;
            //--We dont no n at this point...
            ArrayList<ArrayList<String>> matrix_tmp=new ArrayList<ArrayList<String>>();
            String[] data2;
            for (int i=0;i<tmp_data.size();i++) {
                ArrayList<String> tmp=new  ArrayList<String>();
                data2=getData(tmp_data.get(i), type_s2);
                for (int j=0; j<data2.length;j++)  tmp.add(data2[j]);
                matrix_tmp.add(tmp);
            }
            this.n=matrix_tmp.size();
            this.row=n;
            this.column=p;
            this.data=new String[n+1][p+1];
            
            for (int i=0;i<n;i++) {
                for (int j=0; j<p;j++) {
                    this.data[i][j]=matrix_tmp.get(i).get(j);
                    // if (matrix.matrix[i][j]<matrix.min_m) matrix.min_m=matrix.matrix[i][j];
                    // if (matrix.matrix[i][j]>matrix.max_m) matrix.max_m=matrix.matrix[i][j];
                }
            }
            return type_rectangular;
        }
        
        //Case Triangular
        if (max_s1==1&&max_s2==1&&max_s3==2) {
            //--Test more
            //--Read matrix here
            String[] data2=getData(tmp_data.get(0), type_s1);
            if (data2[0].isEmpty()&&!data2[1].isEmpty()) data2[0]=data2[1];
            this.p=Integer.valueOf(data2[0]);
            this.n=p;
            this.row=n;
            this.column=p;
            this.data=new String[n+1][p+1];
            for (int j=0; j<p;j++) {
                for (int i=0;i<n;i++) {
                    this.data[i][j]="0";
                }
            }
            
            //--We expect square
            for (int i=0; i<this.n;i++) {
                data2=getData(tmp_data.get(i+1), type_s2);
                this.workflow_name.add(data2[0]);
                for (int j=1; j<data2.length;j++) {
                    this.data[i][j-1]=data2[j];
                }
            }
            //--Copy the inverse
            for (int i=0; i<this.n;i++) {
                for (int j=i; j<this.p;j++) {
                    this.data[i][j]=this.data[j][i];
                }
            }
            
            return type_triangular;
        }
        
        // CASE Phylip
        if (max_s1==1&&max_s2>2) {
            //--Read matrix here.
            String[] data2=getData(tmp_data.get(0), type_s1);
            if (data2[0].isEmpty()&&!data2[1].isEmpty()) data2[0]=data2[1];
            this.p=Integer.valueOf(data2[0]);
            //--We dont no n at this point...
            ArrayList<ArrayList<String>> matrix_tmp=new ArrayList<ArrayList<String>>();
            //--WE expect name ... p data on one or more line...
            for (int i=1;i<tmp_data.size();i++) {
                ArrayList<String> tmp=new  ArrayList<String>();
                data2=getData(tmp_data.get(i), type_s2);
                //System.out.println(data2.length+" "+type_s2);
                //--Add name
                this.workflow_name.add(data2[0]);
                //System.out.println(data2.length+" "+data.get(i));
                int d=1;
                int pos=0;
                try {
                    while (pos<p) {
                        if (!data2[d].isEmpty()) {
                            tmp.add(data2[d]);
                            //System.out.println(data2[d]);
                        }
                        d++;
                        pos++;
                        if (d==data2.length&&pos!=p){
                            d=0;
                            i++;
                            data2=getData(tmp_data.get(i), type_s2);
                            // System.out.println("*");
                        }
                    }
                    matrix_tmp.add(tmp);
                } catch(Exception e2) {
                    //--Do not add line if error...
                    Config.log("Warning. Matrix truncated...");
                }
                //--Add the data to matrix
                
            }
            this.n=matrix_tmp.size();
            this.row=n;
            this.column=p;
            //System.out.println("Size n:"+n+" p:"+p);
            this.data=new String[n+1][p+1];
//                            for (int j=0; j<p;j++) {
//                                 for (int i=0;i<n;i++) {
//                                  matrix.matrix[i][j]=0.0;
//                                 }
//                            }
            for (int i=0;i<n;i++) {
                for (int j=0; j<p;j++) {
                    this.data[i][j]=matrix_tmp.get(i).get(j);
                    // if (matrix.matrix[i][j]<matrix.min_m) matrix.min_m=matrix.matrix[i][j];
                    // if (matrix.matrix[i][j]>matrix.max_m) matrix.max_m=matrix.matrix[i][j];
                }
            }
            return type_phylip;
        }
        
        
        // CASE Table row-column
        if (max_s1==2) {
            //System.out.println("Row-column");
            //--Read matrix here
            String[] data2=getData(tmp_data.get(0), type_s1);
            this.n=Integer.valueOf(data2[0]);
            this.p=Integer.valueOf(data2[1]);
            this.row=n;
            this.column=p;
            
            this.data=new String[n+1][p+1];
            for (int j=0; j<p;j++) {
                for (int i=0;i<n;i++) {
                    this.data[i][j]="";
                }
            }
            for (int j=0; j<p;j++) {
                data2=getData(tmp_data.get(j+1), type_s2);
                for (int i=0;i<n;i++) {
                    this.data[i][j]=data2[i];
                    // if (matrix.matrix[i][j]<matrix.min_m) matrix.min_m=matrix.matrix[i][j];
                    // if (matrix.matrix[i][j]>matrix.max_m) matrix.max_m=matrix.matrix[i][j];
                }
                //if (j%100==0) System.out.println(j);
            }
            return type_col_row;
        }
        return type_unknown;
    }
    
    public String[] getData(String tmp_data, int type) {
        if (type<3) {
            return tmp_data.split(""+sep[type]);
        } else {
            //System.out.println(data);
            String[] s=tmp_data.split(" ");
            int count=0;
            for (int i=0; i<s.length;i++) {
                if (!s[i].isEmpty()) count++;
            }
            String[] str=new String[count];
            count=0;
            for (int i=0; i<s.length;i++) {
                if (!s[i].isEmpty()) str[count++]=s[i];
            }
            return str;
        }
    }
    
}
