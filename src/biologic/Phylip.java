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
import workflows.workflow_properties;

/**
 *
 * @author Etienne Lord
 * @since 2010
 */
public class Phylip extends Unknown implements Biologic, Iterator, Serializable {
    
    private int id=0;
    private StringBuilder phylip_data=new StringBuilder();
    private String phylip_datatype="";
    private String note="";
    private String name="UnknownPhylip";
    private int runProgram_id=0;
    public static databaseFunction df=new databaseFunction();
    
    public Phylip() {}
    
    public Phylip(int id) {
        this.loadFromDatabase(id);
    }
    
    /**
     * This
     * @param filename
     * @param type ("Name of the program")
     */
    public Phylip(String filename, String type) {
        loadFromFile(filename);
        phylip_datatype=type;
    }
    
    public boolean loadFromFile(String filename) {
        try {
            String[] stri=Util.InputFile(filename);
            phylip_data=new StringBuilder();
            for (String s:stri) {
                phylip_data.append(s+"\n");
            }
            phylip_datatype="";
        } catch(Exception e) {e.printStackTrace();phylip_data=new StringBuilder();return false;}
        return true;
    }
    
////////////////////////////////////////////////////////////////////////////////
/// Iterator
    
    Vector<Integer>next=new Vector<Integer>();
    int counter=0;
    int maxid=-1;
    public boolean hasNext() {
        //next=df.getAllSequenceID();
        if (next.size()==0) {
            next=df.getAllPhylipID();
            maxid=next.size();
        }
        return (this.counter<maxid);
    }
    
    public Object next() {
        return new Phylip(next.get(counter++));
    }
    
    public void remove() {
        Phylip s=new Phylip(counter-1);
        s.removeFromDatabase();
    }
    
    @Override
    public Vector<Integer> getAllId() {
        return next;
    }
    
    public boolean exists(Integer id) {
        return df.existsPhylip(id);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // Database function
    
    @Override
    public boolean loadFromDatabase(int id) {
        Phylip un=df.getPhylip(id);
        if (un.id>0) {
            this.setPhylip_data(un.getPhylip_data());
            this.setPhylip_datatype(un.getPhylip_datatype());
            this.setName(un.getName());
            this.setNote(un.getNote());
            this.setRunProgram_id(un.getRunProgram_id());
            this.id=id;
            return true;
        } else return false;
    }
    
    @Override
    public boolean saveToDatabase() {
        //--Note: if there is nothing to save, dont save!
        if (phylip_data.toString().isEmpty()) return false;
        id=0;
        id=df.addPhylip(this);
        return (id==0?false:true);
    }
    
    @Override
    public boolean removeFromDatabase() {
        return df.removePhylip(this);
    }
    
    @Override
    public boolean updateDatabase(){
        return df.updatePhylip(this);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    ///
    
    /**
     * This save the phylip data to a file
     * @param filename
     * @return True if successfull
     */
    @Override
    public boolean Output(String filename) {
        try {
            PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
            pw.print(getPhylip_data().toString());
            pw.flush();
            pw.close();
            return true;
        } catch(Exception e) {return false;}
    }
    
    
    /**
     * @return the id
     */
    @Override
    public int getId() {
        return id;
    }
    
    /**
     * @param id the id to set
     */
    @Override
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * @return the runProgram_id
     */
    @Override
    public int getRunProgram_id() {
        return runProgram_id;
    }
    
    /**
     * @param runProgram_id the runProgram_id to set
     */
    @Override
    public void setRunProgram_id(int runProgram_id) {
        this.runProgram_id = runProgram_id;
    }
    
    
    
    /**
     * @return the note
     */
    @Override
    public String getNote() {
        return note;
    }
    
    /**
     * @param note the note to set
     */
    @Override
    public void setNote(String note) {
        this.note = note;
    }
    
    /**
     * @return
     */
    @Override
    public String toString() {
        return "Phylip with ID ["+this.getId()+"] for "+this.getPhylip_datatype()+"\n"+this.getPhylip_data()+"\n"+this.getNote();
    }
    
    
    
    
    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the phylip_data
     */
    public String getPhylip_data() {
        return phylip_data.toString();
    }
    
    /**
     * @param phylip_data the phylip_data to set
     */
    public void setPhylip_data(String phylip_data) {
        this.phylip_data=new StringBuilder();
        this.phylip_data.append(phylip_data);
    }
    
    /**
     * @return the phylip_datatype
     */
    public String getPhylip_datatype() {
        return phylip_datatype;
    }
    
    /**
     * @param phylip_datatype the phylip_datatype to set
     */
    public void setPhylip_datatype(String phylip_datatype) {
        this.phylip_datatype = phylip_datatype;
    }
    
    @Override
    public workflow_properties returnProperties() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String getBiologicType() {
        return "Phylip";
    }
    
    @Override
    public String toHtml() {
        return toString();
    }
    
    @Override
    public String getNameId(int id) {
        return df.getPhylipName(id);
    }
    
    @Override
    public void setData(String data) {
        this.setPhylip_data(data);
    }
    
    @Override
    public String getFasta() {
        return "";
    }
    
    @Override
    public String getPhylip() {
        return this.getPhylip_data();
    }
    
    @Override
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
    
}
