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
import java.util.LinkedList;
import java.util.Vector;
import workflows.armadillo_workflow;
import workflows.workflow_properties;

/**
 * This is an instance of a workflow for running purpose...
 * This also list the Workflows associated with this execution...
 * @author Etienne Lord
 * @since 2010
 */
public class RunWorkflow implements Biologic, Serializable {

    private int id=0;
    private LinkedList<workflow_properties> list=new LinkedList<workflow_properties>();
    private LinkedList<workflow_properties> for_list = new LinkedList<workflow_properties>();
    public  HashMap<Integer, Workflows>execution_workflow=new HashMap<Integer, Workflows>();
    private int original_workflow_id = 0;
    private Vector<Integer> execution_workflow_id = new Vector<Integer>();
    private boolean completed = false;
    private String name="UnknownRunWorkflow";
    private String note = "";
    private Workflows workflow=new Workflows(); //--workflow associated with this runworkflow
    private armadillo_workflow armadillo=new armadillo_workflow();

     public static databaseFunction df = new databaseFunction();

////////////////////////////////////////////////////////////////////////////////
/// CONSTRUCTOR

    public RunWorkflow() {} //Default constructor

    public RunWorkflow(int id) {
        loadFromDatabase(id);
    }

////////////////////////////////////////////////////////////////////////
/// Database function


     public boolean loadExecutionWorkflows() {
        if (id!=0) {
            this.workflow=new Workflows(id);
            workflow.workflow=new armadillo_workflow();
            workflow.StringToWorkflow();
            if (execution_workflow_id.size()>0) {
               for (int ids:execution_workflow_id) {
                   //--Load associated workflows if not already loaded
                   if (!this.execution_workflow.containsKey(ids)) {
                        Workflows workflow2=new Workflows(id);
                        workflow2.workflow=new armadillo_workflow();
                        workflow2.StringToWorkflow();
                        this.execution_workflow.put(ids, workflow2);
                    }
               }
            }        
        } else {
            return false;
        }
         
         return true;
     }

     public boolean loadFromDatabase(int id) {
        RunWorkflow run=df.getRunWorkflow(id);
        if (run.getId()>0) {
           this.setName(run.getName());
           this.setNote(run.getNote());
           this.setOriginal_workflow_id(run.getOriginal_workflow_id());
           this.setCompleted(run.isCompleted());
           this.execution_workflow_id.clear();
           this.execution_workflow_id.addAll(run.getExecution_workflow_id());
           this.id=id;
        return true;
        } else return false;
    }

    public boolean saveToDatabase() {
        id=0;
        id=df.addRunWorkflow(this);
        return (id==0?false:true);
    }

    public boolean removeFromDatabase() {
        return df.removeRunWorkflow(this);
    }

    public boolean updateDatabase() {
        return df.updateRunWorkflow(this);
    }

    //--Dummy for now
    public boolean loadFromFile(String filename) {
        return false;
    }
    
////////////////////////////////////////////////////////////////////////
/// Iterator

    Vector<Integer>next=new Vector<Integer>();
    int counter=0;
    int maxid=-1;
    public boolean hasNext() {
       //next=df.getAllSequenceID();
       if (next.size()==0) {
           next=df.getAllRunWorkflowID();           
           maxid=next.size();
       }
       return (this.counter<maxid);
    }

    public Object next() {
        return new RunWorkflow(next.get(counter++));
    }

    public void remove() {
        RunWorkflow s=new RunWorkflow(counter-1);
        s.removeFromDatabase();
    }

    @Override
    public Vector<Integer> getAllId() {
        return next;
    }

    public boolean exists(Integer id) {
        return df.existsRunWorkflow(id);
    }

////////////////////////////////////////////////////////////////////////
/// Getter / Setter

    /**
     * @return the list
     */
    public LinkedList<workflow_properties> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(LinkedList<workflow_properties> list) {
        this.list = list;
    }

    /**
     * @return the for_list
     */
    public LinkedList<workflow_properties> getFor_list() {
        return for_list;
    }

    /**
     * @param for_list the for_list to set
     */
    public void setFor_list(LinkedList<workflow_properties> for_list) {
        this.for_list = for_list;
    }

    /**
     * @return the original_workflow_id
     */
    public int getOriginal_workflow_id() {
        return original_workflow_id;
    }

    /**
     * @param original_workflow_id the original_workflow_id to set
     */
    public void setOriginal_workflow_id(int original_workflow_id) {
        this.original_workflow_id = original_workflow_id;
    }

    /**
     * @return the execution_workflow_id
     */
    public Vector<Integer> getExecution_workflow_id() {
        return execution_workflow_id;
    }

    /**
     * @param execution_workflow_id the execution_workflow_id to set
     */
    public void setExecution_workflow_id(Vector<Integer> execution_workflow_id) {
        this.execution_workflow_id = execution_workflow_id;
    }

    /**
     * @return the completed
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * @param completed the completed to set
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
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
     * Return return a new workflow_properties()...
     * @return 
     */
    public workflow_properties returnProperties() {
        //--Not usefull
        return new workflow_properties();
    }

    public String getBiologicType() {
        return "RunWorkflow";
    }

    public int getRunProgram_id() {
        return 0;
    }

    public String getNameId(int id) {
       return df.getRunWorkflowName(id);
    }

    public String getFileNameId(int id) {
        return "";
    }
   
    @Override
    public String toString() {
        String str="Workflow execution of "+this.getName()+" was "+(isCompleted()?"completed":"not completed")+"\nTotal execution "+this.getExecution_workflow_id().size()+"\n";
        for (int id:this.getExecution_workflow_id()) {
            str+="\t"+df.getWorkflowsName(id)+"\n";
        }
        return str;
    }

    public String toHtml() {
        return toString();
    }

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
