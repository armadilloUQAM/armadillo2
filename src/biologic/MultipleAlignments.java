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
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Vector;
import workflows.workflow_properties;


public class MultipleAlignments implements Biologic, Serializable {
    private Vector<Alignment> alignments=new Vector<Alignment>();
    private int id=0;
    private String name="UnknownMultipleAlignments";
    private String note="";
    private int runProgram_id=0;
    public static databaseFunction df=new databaseFunction();
    
    public MultipleAlignments() {}
    
    public MultipleAlignments(int id) {
        this.loadFromDatabase(id);
    }
    
    public MultipleAlignments(String filename) {
        loadFromFile(filename);
    }
    
    public boolean loadFromFile(String filename) {
        //--TO DO
        return false;
    }
    
////////////////////////////////////////////////////////////////////////////////
/// Database function
    
    public boolean loadFromDatabase(int id) {
        MultipleAlignments align=df.getMultipleAlignments(id);
        if (align.getId()>0) {
            this.setId(align.getId());
            this.setName(align.getName());
            this.setNote(align.getNote());
            this.setRunProgram_id(align.getRunProgram_id());
            this.alignments.clear();
            this.alignments.addAll(align.getAlignments());
            return true;
        } else return false;
    }
    
    public boolean saveToDatabase() {
        id=0;
        return (df.addMultipleAlignments(this)==0?false:true);
    }
    
////////////////////////////////////////////////////////////////////////////////
/// Iterator
    
    Vector<Integer>next=new Vector<Integer>();
    int counter=0;
    int maxid=-1;
    public boolean hasNext() {
        //next=df.getAllSequenceID();
        if (next.size()==0) {
            next=df.getAllMultipleAlignmentsID();
            maxid=next.size();
        }
        return (this.counter<maxid);
    }
    
    public Object next() {
        return new MultipleAlignments(next.get(counter++));
    }
    
    @Override
    public Vector<Integer> getAllId() {
        return next;
    }
    
    public boolean exists(Integer id) {
        return df.existsMultipleAlignments(id);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// Database Function
    
    public void remove() {
        MultipleAlignments s=new MultipleAlignments(counter-1);
        s.removeFromDatabase();
    }
    
    
    
    /**
     * @return the tree
     */
    public Vector<Alignment> getAlignments() {
        return alignments;
    }
    
    
    public int getNbAlignments() {
        return alignments.size();
    }
    
    public boolean add(Alignment alignment) {
        return alignments.add(alignment);
    }
    
    public boolean removeAlignment(int alignment_id) {
        return df.removeAlignmentFromMultipleAlignments(this, id);
    }
    
    /**
     * @return the runProgram_id
     */
    public int getRunProgram_id() {
        return runProgram_id;
    }
    
    public void setNote(String note) {
        this.note=note;
    }
    
    /**
     * @param runProgram_id the runProgram_id to set
     */
    public void setRunProgram_id(int runProgram_id) {
        this.runProgram_id = runProgram_id;
        for (Alignment a:alignments) a.setRunProgram_id(runProgram_id);
    }
    
    @Override
    public String toString() {
        String s="MultipleAlignments with "+alignments.size()+" alignments(s)\n";
        for (Alignment t:alignments) s+=t+"\n";
        return s;
    }
    
    /**
     * Note: by defautl: phylip format
     * @param filename
     * @return
     */
    public boolean outputAlignments (String filename) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            //Ugly, need better id of trees
            for (Alignment st:this.alignments) pw.println(st.outputPhylip()+"\n");
            pw.flush();
            pw.close();
        } catch (Exception e) {return false;}
        return true;
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
    
    public boolean removeFromDatabase() {
        return df.removeMultipleAlignments(this);
    }
    
    public boolean updateDatabase() {
        return this.updateDatabase();
    }
    
    public workflow_properties returnProperties() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public String getBiologicType() {
        return "MultipleAlignments";
    }
    
    public String toHtml() {
        return toString();
    }
    
    public String getNameId(int id) {
        return df.getMultipleAlignmentsName(id);
    }
    
    public String getFileNameId(int id) {
        return "";
    }
    
    public void setData(String data) {
        //--TO DO
    }
    
    public String getFasta() {
        String s="";
        for (Alignment t:alignments) s+=t.getFasta()+"\n\n";
        return s;
        
    }
    
    public String getPhylip() {
        String s="";
        for (Alignment t:alignments) s+=t.getPhylip();
        return s;
    }
    
    public String getExtendedString() {
        return toString();
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



