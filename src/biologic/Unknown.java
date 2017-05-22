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

import configuration.Util;
import database.databaseFunction;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import workflows.workflow_properties;

/**
 * This represent an Unknown file type
 * @author Etienne Lord
 * @since 2010
 */
public class Unknown implements Biologic, Iterator, Serializable {
    
    private int id=0;
    private StringBuilder unknown=new StringBuilder();
    private String UnknownType="";
    private String note="";
    private String name="Unknown";
    private String filename="";
    private int runProgram_id=0;
    public static databaseFunction df=new databaseFunction();
    
    public Unknown() {}
    
    public Unknown(int id) {
        this.loadFromDatabase(id);
    }
    
    public Unknown(String filename) {
        loadFromFile(filename);
    }
    
    public boolean loadFromFile(String filename) {
        try {
            String[] stri=Util.InputFile(filename);
            unknown=new StringBuilder();
            for (String s:stri) {
                unknown.append(s+"\n");
            }
            this.setFilename(filename);
            this.setUnknownType("Text");
            if (filename.endsWith("fasta")||filename.endsWith("phylip")||filename.endsWith(".phy")) this.setUnknownType("MultipleSequences");
            if (filename.endsWith("gb")) this.setUnknownType("Genbank");
            if (filename.endsWith("newick")) this.setUnknownType("Tree");
            if (filename.endsWith("anc")) this.setUnknownType("Ancestor");
        } catch(Exception e) {e.printStackTrace();unknown=new StringBuilder();return false;}
        return true;
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    // Database function
    
    public boolean loadFromDatabase(int id) {
        Unknown un=df.getUnknown(id);
        if (un.id>0) {
            this.setFilename(un.getFilename());
            this.setName(un.getName());
            this.setNote(un.getNote());
            this.setRunProgram_id(un.getRunProgram_id());
            this.setUnknown(un.getUnknownST());
            this.setUnknownType(un.getUnknownType());
            this.id=id;
            return true;
        } else return false;
    }
    
    public boolean saveToDatabase() {
        id=0;
        id=df.addUnknown(this);
        return (id==0?false:true);
    }
    
    public boolean removeFromDatabase() {
        return df.removeUnknown(this);
    }
    
    public boolean updateDatabase() {
        return df.updateUnknown(this);
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    /// Iterator
    
    Vector<Integer>next=new Vector<Integer>();
    int counter=0;
    int maxid=-1;
    public boolean hasNext() {
        //next=df.getAllSequenceID();
        if (next.size()==0) {
            next=df.getAllUnknownID();
            maxid=next.size();
        }
        return (this.counter<maxid);
    }
    
    public Object next() {
        return new Unknown(next.get(counter++));
    }
    
    public void remove() {
        Unknown s=new Unknown(counter-1);
        s.removeFromDatabase();
    }
    
    @Override
    public Vector<Integer> getAllId() {
        return next;
    }
    
    public boolean exists(Integer id) {
        return df.existsUnknown(id);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// OUTPUT
    
    public boolean Output(String filename) {
        try {
            PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
            pw.print(unknown.toString());
            pw.flush();
            pw.close();
            return true;
        } catch(Exception e) {return false;}
    }
    
    public boolean Output() {
        if (filename.isEmpty()) return false;
        return Output(filename);
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
     * TO DO Create a Newick parser here!
     * @return
     */
    @Override
    public String toString() {
        return this.getUnknown();
    }
    
    /**
     * @return the unknown
     */
    public String getUnknown() {
        return unknown.toString();
    }
    
    public StringBuilder getUnknownST() {
        return unknown;
    }
    
    /**
     * @param unknown the unknown to set
     */
    public void setUnknown(StringBuilder unknown) {
        this.unknown = unknown;
    }
    
    /**
     * @return the UnknownType
     */
    public String getUnknownType() {
        return UnknownType;
    }
    
    /**
     * @param UnknownType the UnknownType to set
     */
    public void setUnknownType(String UnknownType) {
        this.UnknownType = UnknownType;
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
    
    public workflow_properties returnProperties() {
        workflow_properties tmp=new workflow_properties();
        if (id==0) this.saveToDatabase();
        tmp.put("input_unknown_id", this.getId());
        tmp.put("output_unknown_id", this.getId());
        return tmp;
    }
    
    public String getBiologicType() {
        return "Unknown";
    }
    
    public String toHtml() {
        String str=getUnknown().toString().replaceAll("\n", "<br>");
        return str;
    }
    
    public String getNameId(int id) {
        return df.getUnknownName(id);
    }
    
    public String getFileNameId(int id) {
        return df.getUnknownFileName(id);
    }
    
    public void setData(String data) {
        this.getUnknownST().setLength(0);
        this.getUnknownST().append(data);
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
    
    /**
     * This replace any reference found in the text with the correct
     * Sequence name
     */
    public void ReplaceResultsWithSequenceName() {
        //--Replace name with correct sequence name...
        String text=this.getUnknown();
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
        this.setUnknown(new StringBuilder(text));
    }
    
    public Biologic getParent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public workflow_properties getProperties() {
        workflow_properties tmp=new workflow_properties();
        String outputType="Unknown";
        tmp.setName(outputType);
        tmp.put("colorMode","GREEN");
        tmp.put("defaultColor","GREEN");
        tmp.put("Output"+outputType, "True");
        tmp.put("outputType", outputType);
        tmp.put("Connector1Output","True");
        tmp.put("Connector0Output", "True");
        tmp.put("Connector0Conditional", "True");
        tmp.put("ObjectType", "OutputDatabase");
        tmp.put("editorClass", "editors.OutputEditor");
        tmp.put("Description", this.filename);
        tmp.put("output_"+outputType.toLowerCase()+"_id", this.getId());
        return tmp;
    }
    
    public static String getVectorFilePath(Vector<Integer> f){
        String s = "";
        for (int ids:f) {
            if (ids!=0) {
                Unknown fas =new Unknown(ids);
                s = fas.getFilename();
            }
        }
        return s;
    }
    
}
