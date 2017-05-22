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
import static demo.myExperiment_clustering.datalink_end;
import static demo.myExperiment_clustering.datalink_start;
import static demo.myExperiment_clustering.link_type;
import static demo.myExperiment_clustering.pname;
import static demo.myExperiment_clustering.processor_end;
import static demo.myExperiment_clustering.processor_script;
import static demo.myExperiment_clustering.processor_start;
import static demo.myExperiment_clustering.processor_type;
import static demo.myExperiment_clustering.sink;
import static demo.myExperiment_clustering.source;
import static demo.myExperiment_clustering.w_created;
import static demo.myExperiment_clustering.w_description;
import static demo.myExperiment_clustering.w_id;
import static demo.myExperiment_clustering.w_title;
import static demo.myExperiment_clustering.w_type;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import program.RunProgram;
import program.programs;
import workflows.armadillo_workflow;
import workflows.armadillo_workflow.workflow_connector;
import workflows.armadillo_workflow.workflow_connector_edge;
import workflows.armadillo_workflow.workflow_object;
import workflows.armadillo_workflow.workflow_object_output_database;
import workflows.workflow_properties;

/**
 * Holder for workflow
 * Note: this encapsulate a version of the armadillo_workflow
 * @author Etienne Lord
 */
public class Workflows implements Biologic, Iterator, Serializable, Comparator {
    
    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLE IN DATABASE
    private String name="UnknownWorkflow";
    private int id = 0;
    private String workflows_filename = "";
    private String workflow_in_txt = ""; //note: in filter form
    private String note = "";
    private String date_created = "";
    private String date_modified = "";
    private Boolean simpleGraph=false;
    private Boolean displayLINE=true;
    
    
    ////////////////////////////////////////////////////////////////////////////
    /// LOCAL OBJECT VARIABLES
    private StringBuilder workflows_outputText=new StringBuilder();
    //--Note: needed for convertion... we can have multiple armadillo
    public armadillo_workflow workflow=null;
    public static Config config=new Config();
    public static databaseFunction df = new databaseFunction();
    
    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES FOR AND FROM Report
    
    public int number_of_error=0;
    public int number_of_executablenotfound=0;
    public int number_of_object=0;
    public int number_of_program=0;
    public int number_of_if_object=0; //TO DO
    public int number_of_script=0;    //TO DO
    public int number_of_undefined=0; //TO DO
    public int number_of_variables=0; //TO DO
    public boolean completed=false;   //TO DO
    public String workflow_output="";
    
    public int clustering_group=0;
    private Boolean selected=false;
    
////////////////////////////////////////////////////////////////////////////////
/// CONSTRUCTOR
    
    public Workflows() {} //Default constructor for object
    
    public Workflows(armadillo_workflow workflow) {
        this.workflow=workflow;
        //this.workflow_in_txt=workflowToString();
    }
    
    /**
     * Note: a New armadillo workflow must be allocated before
     * if we need to used it:
     * ex.
     * Workflow dummy=new Workflows(new Armadillo_workflow());
     * dummy.loadFromDatabase(id)
     * @param id
     */
    public Workflows(int id) {
        loadFromDatabase(id);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// Database
    
    public boolean saveToDatabase() {
        //We need to set a workflow first
        if (this.workflow==null) return false;
        note="Saved on "+Util.returnCurrentDateAndTime();
        if (id==0) date_created=Util.returnCurrentDateAndTime();
        updateCurrentWorkflow();
        id=0;
        df.addWorkflows(this);
        setChanged(false);
        return (id==0?false:true);
    }
    
    public boolean loadFromDatabase(int id) {
        Workflows work=df.getWorkflows(id);
        if (work.getId()>0) {
            this.setName(work.getName());
            this.setNote(work.getNote());
            this.setDate_created(work.getDate_created());
            this.setDate_modified(work.getDate_modified());
            this.setWorkflow_in_txt(work.getWorkflow_in_txt());
            this.setWorkflows_filename(work.getWorkflows_filename());
            this.setDisplayLINE(work.isDisplayLINE());
            this.setSimpleGraph(work.isSimpleGraph());
            this.setWorkflows_outputText(work.getWorkflows_outputText());
            this.id=id;
            this.setChanged(false);
            return true;//
        } else return false;
    }
    
    public boolean removeFromDatabase() {
        return df.removeWorkflows(this);
    }
    
    public boolean updateDatabase() {
        System.out.println("Updating this workflow: "+this.id);
        return df.updateWorkflows(this);
    }
    
    public boolean loadFromFile(String filename) {
        return this.loadWorkflow(filename);
    }
    
    /**
     * Load a workflow from a myExperiment download
     * @param filename
     * @return
     */
    public boolean loadmyExperiment(String filename) {
        return this.mineWorkflow(filename);
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    /// Iterator
    
    Vector<Integer>next=new Vector<Integer>();
    int counter=0;
    int maxid=-1;
    public boolean hasNext() {
        //next=df.getAllSequenceID();
        if (next.size()==0) {
            next=df.getAllWorkflowsID();
            maxid=next.size();
        }
        return (this.counter<maxid);
    }
    
    public Object next() {
        int i=counter++;
        if (i<next.size()) return new Workflows(next.get(i));
        return new Workflows();
    }
    
    public void remove() {
        Workflows w=new Workflows(counter-1);
        w.removeFromDatabase();
    }
    
    @Override
    public Vector<Integer> getAllId() {
        return next;
    }
    
    public boolean exists(Integer id) {
        return df.existsWorkflows(id);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// Run interface
    
    public boolean run() {
        try {
            //--Initialize a workflow if the current is null
            if (this.getWorkflow()==null) {
                this.workflow=new armadillo_workflow();
                //--Make sure we have a good version of the workflow
                this.StringToWorkflow();
            }
            
            //--Execute
            program.programs to_run=new program.programs(this);
            to_run.Run();
            //--Wait until it's done
            while (!to_run.isDone()) {}
        } catch(Exception e) {
            Config.log("Error in Running workflow : "+this.getName()+"\n"+e.getMessage());
            return false;}
        return true;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// Output execution
    
    public LinkedList<workflow_properties> output_execution() {
        try {
            //--Initialize a workflow if the current is null
            if (this.getWorkflow()==null) {
                this.workflow=new armadillo_workflow();
                this.StringToWorkflow();
            }
            //--Execute
            program.programs to_run=new program.programs(this);
            to_run.createWorkflows();
            for (workflow_properties obj:to_run.run_workflow.getList()) {
                System.out.println(obj.getName());
            }
            return to_run.run_workflow.getList();
            //for (workflow_object obj:to_run.run_workflow.)
        } catch(Exception e) {
            Config.log("Error in Running workflow : "+this.getName()+"\n"+e.getMessage());
            return new LinkedList<workflow_properties>();
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// Saving / Loading function
    
    public boolean saveWorkflow(String filename) {
        try {
            PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
            pw.print(workflowToString());
            pw.flush();
            pw.close();
            setChanged(false);
            return true;
        } catch(Exception e) {return false;}
    }
    
    public boolean loadWorkflow(String filename) {
        if (workflow==null) {
            workflow=new armadillo_workflow();
        }
        if (!Util.FileExists(filename)) return false;
        try {
            //Set informations
            setName(filename);
            setDate_created(Util.returnCurrentDateAndTime());
            setNote("Loaded on "+Util.returnCurrentDateAndTime());
            //Load
            StringBuffer st=new StringBuffer();
            BufferedReader br=new BufferedReader(new FileReader(new File(filename)));
            while(br.ready()) {
                String stri=br.readLine();
                //--Test: remove statusString, status and database information
                if (stri.matches("StatusString=.*")||
                        stri.matches("Status=.*")||
                        stri.matches("output_.*_id=.*")
                        ) {}
                else {
                    st.append(stri+"\n");
                }
            }
            setChanged(false);
            br.close();
            this.workflow_in_txt=st.toString();
            return this.StringToWorkflow(st.toString());
        } catch(Exception e) {return false;}
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    /// Internal function
    
    /**
     * Serialize to String a workflow
     * @return the serialized workflow (use in the database)...
     */
    public String workflowToString() {
        if (workflow==null) return "";
        StringBuilder pw=new StringBuilder();
        try {
            //df.createWorkflow(workflow); //We create a database copy to have the properties_id;
            pw.append("# Armadillo workflow\n");
            pw.append("# Note: "+this.getNote().replaceAll("\n", "\n#")+"\n");
            pw.append("# Created on :"+this.date_created+"\n");
            pw.append("# Modified on:"+this.date_modified+"\n");
            pw.append("# This file created on :"+Util.returnCurrentDateAndTime()+"\n");
            int next_id=df.getNextPropertiesID();
            for(workflow_object obj:workflow.workflow.work) {
                //We add new properties_id
                //if (obj.getProperties().getProperties_id()==0) {
                obj.getProperties().put("properties_id",next_id);
                next_id++;
                //}
                pw.append("Object"+"\n");
                pw.append(obj.getProperties().serializeToString()+"\n");
            }
            for( workflow_connector_edge connector:workflow.workflow.work_connection) {
                pw.append("Connector"+"\n");
                // # Hack to save the source and dest properties id...
                connector.getProperties().put("source_properties_id", connector.getSourceParent().getProperties().getProperties_id());
                connector.getProperties().put("dest_properties_id", connector.getDestinationParent().getProperties().getProperties_id());
                pw.append(connector.getProperties().serializeToString()+"\n");
            }
            //--We need to put an export flag
//            pw.append("Database"+"\n");
//            pw.append(config.properties.serializeToString());
            pw.append("\n");
        } catch(Exception e) {return "";}
        //Config.log(pw.toString());
        return pw.toString();
    }
    
    /**
     * Special version supporting the data serialization to String a workflow
     * @return the serialized workflow (use in the database)...
     */
    public String workflowToStringForExport() {
        if (workflow==null) return "";
        StringBuilder pw=new StringBuilder();
        try {
            //df.createWorkflow(workflow); //We create a database copy to have the properties_id;
            pw.append("# Armadillo workflow\n");
            pw.append("# Created on "+Util.returnCurrentDateAndTime()+"\n");
            int next_id=df.getNextPropertiesID();
            for(workflow_object obj:workflow.workflow.work) {
                //We add new properties_id
                //if (obj.getProperties().getProperties_id()==0) {
                obj.getProperties().put("properties_id",next_id);
                next_id++;
                //}
                pw.append("Object"+"\n");
                pw.append(obj.getProperties().serializeToString()+"\n");
            }
            for( workflow_connector_edge connector:workflow.workflow.work_connection) {
                pw.append("Connector"+"\n");
                // # Hack to save the source and dest properties id...
                connector.getProperties().put("source_properties_id", connector.getSourceParent().getProperties().getProperties_id());
                connector.getProperties().put("dest_properties_id", connector.getDestinationParent().getProperties().getProperties_id());
                pw.append(connector.getProperties().serializeToString()+"\n");
            }
            
            //--Serialize objects
            for(workflow_object obj:workflow.workflow.work) {
                if (obj instanceof workflow_object_output_database) {
                    pw.append("Database"+"\n");
                    String outputType=obj.getProperties().get("OutputType");
                    Output out=new Output();
                    out.setType(outputType);
                    out.setTypeid(obj.getProperties().getOutputID(outputType));
                    //--Initialize the object
                    Object bio=out.getBiologic();
                    //((Biologic)bio).serialize();
                }
                
            }
            
//            pw.append(config.properties.serializeToString());
            pw.append("\n");
        } catch(Exception e) {return "";}
        //Config.log(pw.toString());
        return pw.toString();
    }
    
    /**
     * Load from a string a workflow
     *
     * Workflow.workflow...
     * Note: we need to check for depreciated class...
     * And inform the user...
     * @param str
     * @return
     */
    public boolean StringToWorkflow(String str) {
        if (workflow==null) return false;
        //--Add a ending line to be sure we process everything.
        str=str+"\n";
        //--Clear the current workflow
        workflow.workflow.selectAll();
        workflow.workflow.deleteSelected();
        try {
            StringBuffer st=new StringBuffer();
            boolean modeObject=false;   //--Current Object
            boolean modeConnector=false;//--Connector between object (Arrow)
            boolean modeDatabase=false; //--Data to be loaded (other properties - in developpermode only)
            
            //--Note: Will be added directly to the database
            //--Split Workflow by line
            String[] stri_array=str.split("\n");
            for (String stri:stri_array) {
                //--Skip comments
                if (!stri.startsWith("#")) {
                    //--Find mode
                    if (stri.equals("Object")) modeObject=true;
                    if (stri.equals("Connector")) modeConnector=true;
                    if (stri.equals("Database")) modeDatabase=true;
                    
                    //--Process String
                    if (stri.equals("")) {
                        if (modeDatabase) {
                            modeDatabase=false;
                            //--Look  how we can serialize the data...
                            
//                            workflow_properties prop=new workflow_properties();
//                            prop.deserializeFromString(st.toString());
//                            workflow.createObjectWorkflow(prop);
//                            st.setLength(0);
                        }
                        if (modeObject) {
                            modeObject=false;
                            workflow_properties prop=new workflow_properties();
                            prop.deserializeFromString(st.toString());
                            workflow.createObjectWorkflow(prop);
                            st.setLength(0);
                        }
                        if (modeConnector) {
                            modeConnector=false;
                            workflow_properties prop=new workflow_properties();
                            prop.deserializeFromString(st.toString());
                            int source_connectorNb=prop.getInt("source");
                            int dest_connectorNb=prop.getInt("destination");
                            int source_properties_id=prop.getInt("source_properties_id");
                            int dest_properties_id=prop.getInt("dest_properties_id");
                            workflow_connector source=workflow.workflow.getConnector(source_properties_id, source_connectorNb);
                            workflow_connector dest=workflow.workflow.getConnector(dest_properties_id, dest_connectorNb);
                            if (source!=null&&dest!=null) {
                                boolean notDeletabled=false;
                                if (prop.isSet("notDeletabled")) notDeletabled=prop.getBoolean("notDeletabled");
                                workflow.workflow.addConnector(source, dest, "",notDeletabled);
                                st.setLength(0);
                            } else Config.log("Error for Source_id: "+source_properties_id+"("+source_connectorNb+") "+"dest_id: "+dest_properties_id+"("+dest_connectorNb+")" );
                        }
                    } else {
                        st.append(stri+"\n");
                    }
                } //--End startWith #
            } //--End while br.ready
            ////////////////////////////////////////////////////////////////////
            //--Hack final add in case we are short
            if (modeObject) {
                modeObject=false;
                workflow_properties prop=new workflow_properties();
                prop.deserializeFromString(st.toString());
                workflow.createObjectWorkflow(prop);
                st.setLength(0);
            }
            if (modeConnector) {
                modeConnector=false;
                workflow_properties prop=new workflow_properties();
                prop.deserializeFromString(st.toString());
                int source_connectorNb=prop.getInt("source");
                int dest_connectorNb=prop.getInt("destination");
                int source_properties_id=prop.getInt("source_properties_id");
                int dest_properties_id=prop.getInt("dest_properties_id");
                workflow_connector source=workflow.workflow.getConnector(source_properties_id, source_connectorNb);
                workflow_connector dest=workflow.workflow.getConnector(dest_properties_id, dest_connectorNb);
                if (source==null||dest==null) {
                    Config.log("*** Error : src or dest null:"+source_properties_id+" "+dest_properties_id);
                    for (String s:stri_array) Config.log(s);
                }
                boolean notDeletabled=false;
                if (prop.isSet("notDeletabled")) notDeletabled=prop.getBoolean("notDeletabled");
                if (source!=null&&dest!=null) workflow.workflow.addConnector(source, dest, "",notDeletabled);
                //Config.log("Creating connector for ->"+source_properties_id+" to "+dest_properties_id);
                st.setLength(0);
            }
            
            //if (workflow.simplegraph) workflow.workflow.outputExecution();
            workflow.force_redraw=true;
            workflow.redraw();
            return true;
        } catch(Exception e) {e.printStackTrace();return false;}
    }
    
    
    /**
     * This load the current workflow
     * @return
     */
    public boolean StringToWorkflow() {
        return this.StringToWorkflow(this.getWorkflow_in_txt());
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
     * @return the workflows_filename
     */
    public String getWorkflows_filename() {
        return workflows_filename;
    }
    
    /**
     * @param workflows_filename the workflows_filename to set
     */
    public void setWorkflows_filename(String workflows_filename) {
        this.workflows_filename = workflows_filename;
    }
    
    /**
     * @return the workflow_in_txt
     */
    public String getWorkflow_in_txt() {
        return workflow_in_txt;
    }
    
    /**
     * @param workflow_in_txt the workflow_in_txt to set
     */
    public void setWorkflow_in_txt(String workflow_in_txt) {
        this.workflow_in_txt = workflow_in_txt;
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
     * @return the date_created
     */
    public String getDate_created() {
        return date_created;
    }
    
    /**
     * @param date_created the date_created to set
     */
    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
    
    /**
     * @return the date_modified
     */
    public String getDate_modified() {
        return date_modified;
    }
    
    /**
     * @param date_modified the date_modified to set
     */
    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }
    
    /**
     * @return the workflow
     */
    public armadillo_workflow getWorkflow() {
        return workflow;
    }
    
    public void updateCurrentWorkflow() {
        this.workflow_in_txt=workflowToString();
        this.date_modified=Util.returnCurrentDateAndTime();
    }
    
    public workflow_properties returnProperties() {
        return null;
    }
    
    public String getBiologicType() {
        return "Workflows";
    }
    
    @Override
    public String toString() {
        return "Workflows with ID ["+this.getId()+"] : "+this.getName()+"\n "+this.getNote()+"\n"+
                "Modified on "+this.getDate_modified()+"\n"+
                "Created on "+this.getDate_created()+"\n";
    }
    
    
    public int getRunProgram_id() {
        return 0;
    }
    
    public String toHtml() {
        return toString();
    }
    
    /**
     * @return the simpleGraph
     */
    public Boolean isSimpleGraph() {
        return simpleGraph;
    }
    
    /**
     * @param simpleGraph the simpleGraph to set
     */
    public void setSimpleGraph(boolean simpleGraph) {
        this.simpleGraph = simpleGraph;
    }
    
    /**
     * @return the displayLINE
     */
    public Boolean isDisplayLINE() {
        return displayLINE;
    }
    
    /**
     * @param displayLINE the displayLINE to set
     */
    public void setDisplayLINE(boolean displayLINE) {
        this.displayLINE = displayLINE;
    }
    
    /**
     * @return the workflows_outputText
     */
    public String getWorkflows_outputText() {
        return workflows_outputText.toString();
    }
    
    /**
     * @param workflows_outputText the workflows_outputText to set
     */
    public void setWorkflows_outputText(String workflows_outputText) {
        this.workflows_outputText=new StringBuilder();
        this.workflows_outputText.append(workflows_outputText);
    }
    
    /**
     * @param workflows_outputText the workflows_outputText to set
     */
    public void appendWorkflows_outputText(String workflows_outputText) {
        this.workflows_outputText.append(workflows_outputText);
    }
    
    public String getNameId(int id) {
        return df.getWorkflowsName(id);
    }
    
    public String getFileNameId(int id) {
        Workflows work=df.getWorkflows(id);
        if (work.getId()>0) {
            return work.getWorkflows_filename();
        } else return "";
    }
    
    public void setData(String data) {
        this.setWorkflow_in_txt(data);
    }
    
    public String getFasta() {
        return "";
    }
    
    public String getPhylip() {
        return "";
    }
    
    public String getExtendedString() {
        return this.getId()+"; "+this.getName()+"; "+this.getWorkflow_in_txt()+"; "+this.getWorkflows_filename()+"; "+this.getWorkflows_outputText();
    }
    
    public boolean isChanged() {
        return this.workflow.isChanged();
    }
    
    public void setChanged(Boolean b) {
        if (workflow!=null) this.workflow.setChanged(b);
    }
    
    /**
     * Get the next valid Id found in the database
     * @param start the current  id...
     * @return the next  id or 0 if not found
     */
    public int getNextValidId() {
        if (next.isEmpty()) hasNext();
        for (int i=0;i<next.size();i++) {
            if (next.get(i)==this.id&&(i+1)<next.size()) return next.get(i+1);
        }
        //--Unable to find the current ID? -> Find the first higher...
        for (int i=0;i<next.size();i++) {
            if (next.get(i)>this.id) return next.get(i);
        }
        //--If not, return 0;
        return 0;
    }
    
    /**
     * Get the previous valid Id found in the database
     * @param start the current workflow id...
     * @return the next workflow id or 0 if not found
     */
    public int getPreviousValidId() {
        if (next.isEmpty()) hasNext();
        for (int i=0;i<next.size();i++) {
            if (next.get(i)==this.id&&(i-1)>=0) return next.get(i-1);
        }
        //--Unable to find the current ID? -> Find the first higher...
        for (int i=0;i<next.size();i++) {
            if (next.get(i)>this.id&&(i-1)>=0) return next.get(i-1);
        }
        //--If not, return 0;
        return 0;
    }
    
    /**
     * Generate some statistic for this workflow
     */
    public void generateStatistics() {
        //--Generate some statistics for this Workflows
        ////////////////////////////////////////////////////////////////////////
        /// RESET THE VARIABLES
        number_of_error=0;
        number_of_executablenotfound=0;
        number_of_object=0;
        number_of_program=0;
        number_of_if_object=0; //TO DO
        number_of_script=0;    //TO DO
        number_of_undefined=0; //TO DO
        number_of_variables=0; //TO DO
        completed=false;       //TO DO
        workflow_output="";    //--Current workflow output...
        
        
        ////////////////////////////////////////////////////////////////////////
        /// GET THE STATS
        //ArrayList<workflow_properties> tmp=new  ArrayList<workflow_properties>();
        //HashMap<workflow_properties, Integer>outputTextIDs=new HashMap<workflow_properties, Integer>();
        
        int number_of_completed_program=0;
        for (workflow_object o:workflow.workflow.work) {
            number_of_object++;
            //--We have a program
            if (o.getProperties().isProgram()) {
                if (o.getProperties().isSet("Status")) number_of_completed_program++;
                if (o.getProperties().getStatus()==RunProgram.status_error) number_of_error++;
                if (o.getProperties().getStatus()==RunProgram.status_programnotfound) number_of_executablenotfound++;
                number_of_program++;
            }
            //--We have something else...
            if (o.getProperties().isSet("ObjectType")&&o.getProperties().get("ObjectType").equals("If")) number_of_if_object++;
            if (o.getProperties().isSet("ObjectType")&&o.getProperties().get("ObjectType").equals("OutputDatabase")) this.number_of_variables++;
            //--Get all output text ids...
            if (o.getProperties().isSet("output_outputtext_id")) {
                //outputTextIDs.put(o.getProperties(),o.getProperties().getInt("output_outputtext_id"));
                Unknown unknown = new Unknown(o.getProperties().getInt("output_outputtext_id"));
                workflow_output+=unknown.getUnknown();
            }
            if (o.getProperties().isSet("ObjectType")&&o.getProperties().get("ObjectType").equals("Output")) this.number_of_variables++;
        }
        
        ////////////////////////////////////////////////////////////////////////
        ///CALCULATE SOME STATS...
        completed=(number_of_program==number_of_completed_program&&number_of_error==0&&number_of_executablenotfound==0);
        
        //--Get A string of the stdout/stderr
        
        
        ////////////////////////////////////////////////////////////////////////
        /// PRINT SOME STATS
//        if (number_of_error>0) {
//            System.out.println(this.workflows_filename+" ID: "+this.getId()+"\t"+this.getName()+"\t have "+number_of_error+" error...");
//        }
//        if (number_of_executablenotfound>0) {
//            System.out.println(this.workflows_filename+" ID: "+this.getId()+"\t"+this.getName()+"\t have "+number_of_executablenotfound+" executable not found...");
//        }
        
    }
    
    public String getStatistics() {
        ////////////////////////////////////////////////////////////////////////
        /// PRINT SOME STATS
        String info=this.workflows_filename+" ID: "+this.getId()+"\t"+this.getName()+"\t have "+number_of_error+" error... \nPrograms"+number_of_program;
        return info;
//        if (number_of_executablenotfound>0) {
//           return ""+this.workflows_filename+" ID: "+this.getId()+"\t"+this.getName()+"\t have "+number_of_executablenotfound+" executable not found... \nPrograms"+number_of_program;
//        }
    }
    
    public boolean isCompleted() {
        //--We could do a look in the RunWorkflow in the db...
        //--But, we can just iterate over all object
        if (this.number_of_object>0) {
            return completed;
        } else {
            generateStatistics();
            return completed;
        }
    }
    
    /**
     * This replace any reference found in the text with the correct
     * Sequence name
     */
    public void ReplaceResultsWithSequenceName() {
        //--Replace name with correct sequence name...
        try {
            String text=this.workflows_outputText.toString();
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
            this.setWorkflows_outputText(text);
        } catch(Exception e) {Config.log("Error in replacing sequence name in workflow output...");}
    }
    
    public Biologic getParent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * @return the selected
     */
    public Boolean isSelected() {
        return selected;
    }
    
    /**
     * @param selected the selected to set
     */
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// Function to load myExperiment workflow
    
    /**
     * This will mine a workflow from myExperiment
     * @param f workflow file
     */
    public boolean mineWorkflow(String filename) {
        if (workflow==null) {
            workflow=new armadillo_workflow();
        }
        if (!Util.FileExists(filename)) return false;
        System.out.println("Loading "+filename);
        
        setName(filename);
        setDate_created(Util.returnCurrentDateAndTime());
        setNote("Loaded on "+Util.returnCurrentDateAndTime());
        
        
        //Workflows w=new Workflows(new armadillo_workflow());
        
        //////////////////////////////////////////////////////////////////////
        /// Variable
        String buffer_processor="";
        String data_processor="";
        String title="";
        String description="";
        String id="";
        
        boolean in_processor=false;
        boolean in_datalink=false;
        
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(filename)));
            StringBuilder buffer=new StringBuilder();
            while (br.ready()) {
                String stri=br.readLine().trim();
                
                Matcher m2=processor_start.matcher(stri);
                Matcher m3=processor_end.matcher(stri);
                Matcher m4=datalink_start.matcher(stri);
                Matcher m5=datalink_end.matcher(stri);
                Matcher m_id=w_id.matcher(stri);
                Matcher m_title=w_title.matcher(stri);
                Matcher m_description=w_description.matcher(stri);
                Matcher m_type=w_type.matcher(stri);
                Matcher m_created=w_created.matcher(stri);
                
                if (m_id.find()) {
                    id="myExperiment id:"+m_id.group(1);
                }
                if (m_title.find()) {
                    title=m_title.group(1);
                }
                if (m_description.find()) {
                    description=m_description.group(1);
                }
                if (m_created.find()) {
                    //this.setDate_created(m_created.group(1));
                }
                
                //--datalink
                if (m4.find()) {
                    data_processor+=stri;
                    in_datalink=true;
                } else
                    if (m5.find()) {
                        data_processor+=stri;
                        //System.out.println(data_processor);
                        String input="";
                        String output="";
                        String type="";
                        
                        //--Process
                        Matcher m_sink=sink.matcher(data_processor);
                        Matcher m_source=source.matcher(data_processor);
                        Matcher m_link_type=link_type.matcher(data_processor);
                        if (m_link_type.find()) {
                            type=m_link_type.group(1);
                            //System.out.println("type:"+type);
                        }
                        if (m_sink.find()) {
                            String name=m_sink.group(1).trim();
                            //System.out.println("sink:"+name);
                            input=name;
                        }
                        if (m_source.find()) {
                            String name=m_source.group(1).trim();
                            //System.out.println("source:"+name);
                            output=name;
                        }
                        
                        //--Create connector
                        if (!input.isEmpty()&&!output.isEmpty()) {
                            //--Locate object input
                            armadillo_workflow.workflow_connector output_connector=null;
                            armadillo_workflow.workflow_connector input_connector=null;
                            for (workflow_object wo:this.workflow.workflow.work) {
                                if (wo.properties.getName().equalsIgnoreCase(output)) {
                                    //System.out.println("Output:"+wo.properties.getName());
                                    if (wo.properties.isProgram()) {
                                        output_connector=wo.returnConnector(workflow_object.OUTPUT);
                                    } else {
                                        output_connector=wo.returnConnector(1);
                                    }
                                }
                            }
                            if (output_connector==null) {
                                //--Probably an input from the workflow
                                workflow_properties tmp=new workflow_properties();
                                tmp.put("ObjectType","OutputDatabase");
                                tmp.put("Connector1Output","true");
                                tmp.put("Connector1Output","true");
                                tmp.put("outputType","Text");
                                tmp.put("OutputText",true);
                                tmp.put("InputText",true);
                                tmp.put("defaultColor", "GREEN");
                                tmp.put("colorMode", "GREEN");
                                
                                tmp.setName(output);
                                workflow_object o=this.workflow.createObject(tmp);
                                output_connector=o.returnConnector(1);
                            }
                            if (output_connector!=null) {
                                for (workflow_object wo:this.workflow.workflow.work) {
                                    if (wo.properties.getName().equalsIgnoreCase(input)) {
                                        //System.out.println("Input:"+wo.properties.getName());
                                        input_connector=this.workflow.workflow.findCompatible(output_connector, wo);
                                    }
                                }
                            }
                            
                            
                            if (input_connector!=null&&output_connector!=null) {
                                this.workflow.workflow.addConnection(output_connector, input_connector, type);
                            } else {
//                             System.out.println("Error "+output+"->"+input);
//                             System.out.println("input:"+input_connector+"output:"+output_connector);
                                //--Probably a workflow output
                                //-- Add to output
                                for (workflow_object wo:this.workflow.workflow.work) {
                                    if (wo.properties.getName().equalsIgnoreCase(output)) {
                                        //System.out.println("Output:"+wo.properties.getName());
                                        if (wo.properties.isProgram()) {
                                            wo.properties.put("Output"+input,true);
                                            this.workflow.workflow.createOutput_Objects(wo.returnConnector(1));
                                        }
                                    }
                                }
                                
                            }
                        }
                        //--Clear buffer
                        data_processor="";
                        in_datalink=false;
                    } else  if (in_datalink) data_processor+=stri;
                
                //--Processor
                if (m2.find()) {
                    buffer_processor+=stri;
                    in_processor=true;
                } else
                    if (m3.find()) {
                        buffer_processor+=stri;
                        //--Process
                        this.workflow.createObject(createWorkflow_object(buffer_processor));
                        //--Clear buffer
                        buffer_processor="";
                        in_processor=false;
                    } else  if (in_processor) buffer_processor+=stri;
                //buffer.append(br.readLine());
            }
            //--
            br.close();
        } catch(Exception e) {
            Config.log("Unable to load myExperiment workflow: "+filename);
            Config.log(e.getMessage());
            return false;
        }
        //--Order object by execution order
        LinkedList <workflow_object> ex=this.workflow.workflow.outputExecution();
        int x=100;
        int y=65;
        int prev_x=0;
        int prev_y=0;
        String parent_type="";
        for (workflow_object e:ex) {
            int tx=x;
            int ty=y;
            ArrayList<workflow_object> parent=findImmediateParent(this,e);
            if (parent.size()>0) {
                int max_x=x;
                int max_y=y;
                for (workflow_object wp:parent) {
                    if (wp.getProperties().getInt("x")>max_x) {
                        max_x=wp.getProperties().getInt("x");
                        parent_type=wp.getProperties().get("ObjectType");
                    }
                    if (wp.getProperties().getInt("y")>max_y) {
                        max_y=wp.getProperties().getInt("y");
                    }
                }
                if (parent_type.equals("Output")) {
                    tx=max_x+125;
                } else {
                    tx=max_x+200;
                }
                ty=max_y;
            }
            if (tx>prev_x&&ty>prev_y) {
                ty=65;
            }
            e.getProperties().put("x",tx);
            e.getProperties().put("y",ty);
            prev_x=tx;
            prev_y=ty;
            if (ty > 400) {
                x+=180;
                y=65;
            } else {
                y+=50;
            }
            //--Update the current workflow
//              for (workflow_object wob:this.workflow.workflow.work) {
//                  if (wob.objects_id==e.objects_id) {
//                       wob.getProperties().put("x",tx);
//                       wob.getProperties().put("y",ty);
//                       //this.workflow.updateCurrentWorkflow(wob.getProperties());
//                  }
//              }
            
//              e.x=tx;
//              e.y=ty;
            //y+=50;
        }
        //--Add comment the last object in workflow
        workflow_properties comments=new workflow_properties();
        //"Note: this is a workflow from myExperiment.org\n[!] It is not functional (executable)"
        comments.put("Description", splitBy55(description+" "+id));
        comments.put("ObjectType", "ScriptBig");
        comments.put("Name",title);
        comments.put("colorMode", "RED");
        comments.put("defaultColor", "RED");
        comments.put("x", 20);
        comments.put("y", 486);
        this.workflow.createObject(comments);
        this.workflow_in_txt=this.workflowToString();
        //--Hack
        this.StringToWorkflow(this.workflow_in_txt);
        // this.workflow.redraw();
        
        //System.out.println(this.workflowToString());
        return true;
    }
    
    
    workflow_properties createWorkflow_object(String buffer_processor) {
        //--Variable
        workflow_properties tmp=new workflow_properties();
        Matcher p_name=pname.matcher(buffer_processor);
        Matcher p_type=processor_type.matcher(buffer_processor);
        Matcher p_script=processor_script.matcher(buffer_processor);
        String name="Unknown";
        String type="Program";
        String script="";
        
        if (p_name.find()) name=p_name.group(1);
        if (p_type.find()) type=p_type.group(1);
        if (p_script.find()) {
            script=p_script.group(1);
            tmp.put("script",script);
        }
        
        tmp.setName(name);
        tmp.put("ObjectType","Program");
        tmp.put("myExperiment_type",type);
        
        if (type.equals("stringconstant")) {
            tmp.put("ObjectType","OutputDatabase");
            tmp.put("Connector1Output","true");
            tmp.put("Connector1Output","true");
            tmp.put("outputType","Text");
            tmp.put("defaultColor", "GREEN");
            tmp.put("colorMode", "GREEN");
            
        } else {
            tmp.put("Connector0Output","True");
            tmp.put("Connector1Conditional","True");
            tmp.put("InputText","Connector2");
            tmp.put("nbInput",1);
            tmp.put("defaultColor", "BLUE");
            tmp.put("colorMode", "BLUE");
        }
        tmp.put("OutputText",true);
        tmp.put("output_text_id",0);
        
        return tmp;
    }
    
    public ArrayList<workflow_object> findImmediateParent(Workflows w, workflow_object o) {
        ArrayList<workflow_object>tmp=new ArrayList<workflow_object>();
        Vector<workflow_object> parents=w.workflow.workflow.findInput(o);
        if (parents==null||parents.size()==0) return tmp;
        //--CAS 1. Immediate parent
        for (workflow_object input:parents) {
            
            tmp.add(input);
        }
        //--CAS 2. Recurse one level
        return tmp;
    }
    
    public ArrayList<workflow_object> findImmediateParentDatabase(Workflows w, workflow_object o) {
        ArrayList<workflow_object>tmp=new ArrayList<workflow_object>();
        Vector<workflow_object> parents=w.workflow.workflow.findInput(o);
        if (parents==null||parents.size()==0) return tmp;
        //--CAS 1. Immediate parent
        for (workflow_object input:parents) {
            if (input.getProperties().get("ObjectType").equals("OutputDatabase")) {
                tmp.add(input);
            }
        }
        //--CAS 2. Recurse one level
        return tmp;
    }
    
    public ArrayList<workflow_object> findImmediateChildObject(Workflows w, workflow_object o) {
        ArrayList<workflow_object>tmp=new ArrayList<workflow_object>();
        Vector<workflow_object> childs=w.workflow.workflow.findOutput(o);
        if (childs==null||childs.size()==0) return tmp;
        //--CAS 1. Immediate parent
        for (workflow_object input:childs) {
            if (input.getProperties().isProgram()) {
                tmp.add(input);
            }
        }
        //--CAS 2. Recurse one level
        if (tmp.size()==0) {
            for (workflow_object input:childs) {
                tmp.addAll(findImmediateChildObject(w, input));
            }
        }
        return tmp;
    }
    
    public ArrayList<workflow_object> findWorkflowInput(Workflows w) {
        ArrayList<workflow_object>tmp=new ArrayList<workflow_object>();
        //--Get all objects of type OutputDatabase
        for (workflow_object input:w.workflow.workflow.work) {
            if (input.getProperties().get("ObjectType").equals("OutputDatabase")) {
                tmp.add(input);
            }
        }
        return tmp;
    }
    
    public ArrayList<workflow_object> findWorkflowOutput(Workflows w) {
        ArrayList<workflow_object>tmp=new ArrayList<workflow_object>();
        //--Find all program that have no child program
        for (workflow_object o:w.workflow.workflow.work) {
            //--If no child, add
            if (findImmediateChildObject(w,o).size()==0&&o.getProperties().isProgram()) {
                tmp.add(o);
            }
        }
        return tmp;
    }
    
    public String splitBy55(String stri) {
        String output="";
        while (stri.length()>55) {
            output+=stri.substring(0, 55)+"\n";
            stri=stri.substring(55);
        }
        output+=stri;
        return output;
    }
    
    public int compare(Object o1, Object o2) {
        try {
            Workflows w1=(Workflows)o1;
            Workflows w2=(Workflows)o2;
            return w2.clustering_group-w1.clustering_group;
        } catch(Exception e) {
            return 0;
        }
    }
    
}

