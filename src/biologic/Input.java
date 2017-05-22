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

import Class.Classdata;
import database.databaseFunction;
import java.io.Serializable;
import java.util.Vector;
import workflows.workflow_properties;

/**
 * This represent an input for a RunProgram
 * @author Etienne Lord
 * @since Janvier 2010
 */
public class Input implements Biologic, Serializable {
    private int id=0;
    private int RunProgram_id = 0;
    private String type = "";  //Exemple Biologic.getBiologicType();
    private int typeid = 0;    //Exemple sequence_id=0;
    public static databaseFunction df=new databaseFunction();
    
    public Input() {}
    
    public Input(int id) {
        this.loadFromDatabase(id);
    }
    
    public Input(Biologic b) {
        this.setRunProgram_id(b.getRunProgram_id());
        this.setType(b.getBiologicType());
        this.setTypeid(b.getId());
    }
    
    public boolean loadFromFile(String filename) {
        return false;
    }
    
    //////////////////////////////////////////////////////////////////////////////
    /// Database function
    
    public boolean loadFromDatabase(int id) {
        Input in=df.getInput(id);
        if (in.id>0) {
            this.setRunProgram_id(in.getRunProgram_id());
            this.setType(in.getType());
            this.setTypeid(in.getTypeid());
            this.id=id;
            return true;
        } else {
            return false;
        }
    }
    
    public boolean saveToDatabase() {
        id=0;
        id=df.addInput(this);
        return (id==0?false:true);
    }
    
    public boolean removeFromDatabase() {
        return df.removeInput(this);
    }
    
    public boolean updateDatabase() {
        return df.updateInput(this);
    }
    
    
////////////////////////////////////////////////////////////////////////////////
/// Iterator
    
    Vector<Integer>next=new Vector<Integer>();
    int counter=0;
    int maxid=-1;
    public boolean hasNext() {
        //next=df.getAllSequenceID();
        if (next.size()==0) {
            next=df.getAllInputID();
            maxid=next.size();
        }
        return (this.counter<maxid);
    }
    
    public Object next() {
        return new Input(next.get(counter++));
    }
    
    public void remove() {
        Input s=new Input(counter-1);
        s.removeFromDatabase();
    }
    
    @Override
    public Vector<Integer> getAllId() {
        return next;
    }
    
    public boolean exists(Integer id) {
        return df.getAllInputID().contains(id);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    
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
     * @return the type
     */
    public String getType() {
        return type;
    }
    
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * @return the typeid
     */
    public int getTypeid() {
        return typeid;
    }
    
    /**
     * @param typeid the typeid to set
     */
    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }
    
    /**
     * This try to return a Biologic object from this object
     * @return
     */
    public Biologic getBiologic() {
        Classdata c=new Classdata("biologic."+this.getType());
        Object o=c.newObject();
        if (o==null) return null;
        //--Load Object
        if (this.getTypeid()>0) {
            ((Biologic)o).loadFromDatabase(this.getTypeid());
        }
        return  ((Biologic)o);
    }
    
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
    
    
    public workflow_properties returnProperties() {
        return null;
    }
    
    public String getName() {
        return this.getBiologic().getName();
    }
    
    public String getBiologicType() {
        return this.getType();
    }
    
    public String toHtml() {
        return toString();
    }
    
    public String getNameId(int id) {
        return df.getInputName(id);
    }
    
    public String getFileNameId(int id) {
        return "";
    }
    
    public void setName(String name) {
        //--Do nothing
    }
    
    public void setNote(String note) {
        //--Do nothing
    }
    public String getNote() {return "";}
    
    public void setData(String data) {
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
    
    public Biologic getParent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
