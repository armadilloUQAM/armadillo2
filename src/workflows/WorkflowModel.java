/*
 *  Armadillo Workflow Platform v1.2
 *  A simple pipeline system for phylogenetic analysis
 *  
 *  Copyright (C) 2009-2012  Etienne Lord, Mickael Leclercq
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

package workflows;

import Scripts.Scripts_conversion;
import biologic.Biologic;
import biologic.Output;
import biologic.Workflows;
import editor.ConnectorInfoBox;
import configuration.Config;
import configuration.Util;
import processing.core.*;

import java.awt.*;
import java.util.*;

import editor.propertiesEditorBox;
import editors.SimplePhyloEditor;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import tools.Toolbox;




/**
 * This is a representation of the workflow without the PApplet part
 * @author Etienne Lord
 * @since 2011
 */
public class WorkflowModel  {

////////////////////////////////////////////////////////////////////////////////
/// Variables

//public armadillo_workflow current; //--Pointer to the Armadill_workflow_visual
//-- Database (for workflow saving and item)

////////////////////////////////////////////////////////////////////////////////
//-- Properties and editor
public workflow_image filedata=new workflow_image(); //Hold all the icon 
private workflow_properties properties=new workflow_properties();
//private static Menu program_menu=null;

Config config=new Config();

public Workbox workbox=new Workbox();
public propertiesEditorBox propertieseditorbox;
public ConnectorInfoBox connectorinfobox;
JInternalFrame parent;

////////////////////////////////////////////////////////////////////////////////
//--GUI VARIABLE
PImage workflow_background=null;         // Background workflow image created in draw
public Workflow workflow;                // Workflow object
PFont smallfont,font,boldfont;           // Fonts
PShape armadillo;                        // Logo Armadillo 0.1
PGraphics bufConnectorEdge;              // Back buffer for Arrow selection
private int hashCount=0;                 // Unique number if we need a hashcode
PopupMenu popupObject;                   // Popup Menu
public static int count=0;
public static SimplePhyloEditor simplephylo;

////////////////////////////////////////////////////////////////////////////////
//--Flag
public boolean small_editor_mode=false;
public boolean name_displayed=false;
public boolean debug=false;       // Debug version (true or false)
public boolean force_redraw=false;// Might be needed if we need to redraw
public boolean force_nodraw=false;// Might be needed if we nned
public boolean save_image=false;  // To save the current workflow as an image
public boolean save_image_pdf=false;  // To save the current workflow as an pdf
public boolean draw_grid=true;    // Do we draw a grid?
public boolean simplegraph=false; // Do we draw as simple graph

private String save_image_filename="";  //Name of filename to save to an image

public boolean initialized=false;   // Flag to know if we have run setup (graphic mode) or not(text mode)
private boolean movingFlag=false;     // Flag Set if we are moving (we don't drwa to back buffer)

////////////////////////////////////////////////////////////////////////////////
/// Workflow changed

private boolean changed=false;

////////////////////////////////////////////////////////////////////////////////
/// Cut and Paste

public Workflow CopyPaste_selection=new Workflow(); // Current CopyPaste Selection
public int Paste_count=0;                           // Number of Copy/Paste Done
public workflow_object Popup_selection;             // Selection in Popup
public boolean Popup_Out=false;                     // Flag to catch Popup
private boolean auto_update=false;                  // Flag for updating the drawing aread
public LinkedList<Workflows>UndoRedo=new LinkedList<Workflows>(); //In development
// To save the "object" state of the workflow - January 2011
public HashMap<workflow_object, workflow_properties> state=new HashMap<workflow_object,workflow_properties>(); 

////////////////////////////////////////////////////////////////////////////////
/// Main Setup function

/**
 * public default constructor
 */
public WorkflowModel() {
    workflow=new Workflow();
   // workflow.setArmadillo(this.current); //--Set pointer to the armadillo_workflow
}

    public void convertToFor(workflow_object obj) {
        workflow_properties prop=obj.getProperties();
        if (prop.get("ObjectType").equals("Program")) {
            prop.put("ForObjectID", prop.getID());
        } else {
            String outputType=prop.get("outputType");
            //prop.put("ForObjectID", prop.getID());
            int id=prop.getOutputID(outputType);
            prop.put("output_"+outputType.toLowerCase()+"_id", 0);
            prop.put("For_"+id,properties.getDescription());
            prop.put("Description", "For each");
        }
    }

    public void resetState() {
        for (workflow_object obj:workflow.work) {
            obj.getProperties().remove("Status");
            obj.getProperties().remove("StatusString");
            obj.getProperties().remove("IfStatus");
            obj.getProperties().remove("CommandLine_Running");           
        }
        force_redraw=true;
//        redraw();
    }

    /**
     * Save the object state before using the PDF
     */
    public void saveandresetState(){
        //--1. Clear the state Vector
        state.clear();
        for (workflow_object obj:workflow.work) {
            workflow_properties p=(workflow_properties)obj.getProperties().clone();
            state.put(obj, p);
        }
         for (workflow_object obj:workflow.work) {
            obj.getProperties().remove("Status");
            obj.getProperties().remove("StatusString");
            obj.getProperties().remove("IfStatus");
            obj.getProperties().remove("CommandLine_Running");           
        }
    }
    
    /**
     * Load the object state after saving to PDF     
     */
    public void loadState() {
        for (workflow_object obj:workflow.work) {
            workflow_properties p=state.get(obj);
            obj.properties=p;
        }
    }
    
   
    public void setName(String name) {
        this.workflow.name=name;                
    }
    
    public String getName() {
        return this.workflow.name;
    }

///////////////////////////////////////////////////////////////////////////////
 /// DataPath

   
 public String dataPath(String filename) {
     return config.get("dataPath")+File.separator+filename;
 }

////////////////////////////////////////////////////////////////////////////////
/// Object creation

/**
 * This is a special fucntion for the workflow_preview
 * This destroy all object and create this one
 * @param obj
 */
public void updateWorkflow(workflow_properties obj) {
    //--Get current x and y position if foung
    if (!isInitialized()) {
        while(!isInitialized()){}; //Hack: do better latter
    }
    if (workflow.work.size()>0) {
        workflow_object tmp=workflow.work.get(0);
        obj.put("x", tmp.getProperties().get("x"));
        obj.put("y", tmp.getProperties().get("y"));
    }
    //--Remove workflow element
    workflow.selectAll();
    workflow.deleteSelected();
    //--Re-add object
    createObject(obj);
    force_redraw=true;    
}

/**
 * This is a special fucntion for the workflow_preview
 * This destroy all object and create this one
 * @param obj
 */
public void updateCurrentWorkflow(workflow_properties obj) {
    //--Get current x and y position if foung
    if (!isInitialized()) {
        while(!isInitialized()){}; //Hack: do better latted
    }
    if (workflow.work.size()>0) {
        for (workflow_object o:workflow.work) {
            //--TO DO: Be sure we have a good id...
            if (o.properties.getID().equals(obj.getID())) {
                o.properties=obj;
            }
        }
    }
    force_redraw=true;
    
}
/**
 * This is a special function for the workflow_preview
 * This create the obj without destroying other object
 * @param obj
 */
public void createObjectWorkflow(workflow_properties obj) {
    createObject(obj);
    //We can used the workflow even if it is not a graphic workflow...
    if (isInitialized()) {
        force_redraw=true;
    
    }
    
}

/**
 * Class used to put new object on the workflow
 * @param object_name (String)
 */
public workflow_object createObject(workflow_properties obj, Point location) {
    //System.out.println(obj);
    setChanged(true);

    workflow_object tmp=null;
    if (!obj.get("ObjectType").equals(workflow_properties.NotSet)) {
       
        // CASE 2: Else, we create objects
        if (obj.get("ObjectType").equals("For")||obj.get("ObjectType").equals("While")) {
            tmp=new workflow_object_aggregator(obj,(int)location.getX(), (int)location.getY());
        } else
        if (obj.get("ObjectType").equals("If")) {
            tmp=new workflow_object_if(obj,(int)location.getX(), (int)location.getY());
        } else
         if (obj.get("ObjectType").equals("Begin")) {
             //--Note, we only want 1 begin object
             workflow_object dummy=new workflow_object(obj,(int)location.getX(), (int)location.getY());
             if (nbBeginObject()==0) {
                 tmp=new workflow_object_BeginEnd(obj,(int)location.getX(), (int)location.getY());
             } else {
                 if (parent!=null) {
                    //--Note: deprecated
                     JOptionPane.showMessageDialog(parent, "Warning! You can only have one Begin in your workflow");
                 }
             }
        } else
            if (obj.get("ObjectType").equals("End")) {
                tmp=new workflow_object_BeginEnd(obj,(int)location.getX(), (int)location.getY());
        } else
         if (obj.get("ObjectType").equals("Script")) {
            tmp=new workflow_object_script(obj,(int)location.getX(), (int)location.getY());
        } else
        if (obj.get("ObjectType").equals("ScriptBig")) {
            tmp=new workflow_object_script_big(obj,(int)location.getX(), (int)location.getY());
        } else
        if (obj.get("ObjectType").equals("Output")) {
             tmp=workflow.createOutput_Object(obj,(int)location.getX(), (int)location.getY());
        } else
        if (obj.get("ObjectType").equals("OutputDatabase")) {
             tmp=new workflow_object_output_database(obj,(int)location.getX(), (int)location.getY());
        } else
        if (obj.get("ObjectType").equals("Variable")) {
             tmp=new workflow_object_variable(obj,(int)location.getX(), (int)location.getY());
        } else
        {
            //By default a Program...
            tmp=new workflow_object(obj, (int)location.getX(), (int)location.getY());
        }
        //--Finally, add to workflow        
        if (tmp!=null) workflow.add(tmp);        
    }
    return tmp;
}
/**
 * This will create the specified variable on the workflow or update it.
 * @param name
 * @param value
 * @param visible
 * @return
 */
public workflow_properties setVariable(String name, String value, boolean visible) {
    workflow_properties tmp=null;
    //1. Find possible variable
 
    //2. No variable found in the workflow, add one...
         tmp.setName("Undefined Variable");
         tmp.put("colorMode","GREEN");
         tmp.put("defaultColor","GREEN");
         tmp.put("Output"+"Text", "True");
         tmp.put("outputType", "Text");
         tmp.put("InputAll","Connector0");
         tmp.put("Connector1Output", "True");
         //tmp.put("Connector0Conditional", "True");
         tmp.put("ObjectType", "Variable");
         //tmp.put("output_"+"variable"+"_id", id);
         tmp.put("EditorClassName","editors.VariableEditor");

    return tmp;
}

/**
 * Class used to put new object on the workflow
 * 
 */
public workflow_object createObject(workflow_properties obj, int x, int y) {
    Point p=new Point(x,y);
    return createObject(obj,p);
}

/**
 * Class used to put new object on the workflow
 * @param object_name (String)
 */
public workflow_object createObject(workflow_properties obj) {
    Point p=new Point(obj.getInt("x"),obj.getInt("y"));
    if (p.x==0&&p.y==0||p.x<0||p.y<0) {
        p.x=135;
        p.y=20;
    }
    return createObject(obj,p);
}
////////////////////////////////////////////////////////////////////////////////
/// Verification fucntion

/**
 * Count the number of begin object
 * @return
 */
public int nbBeginObject() {
    int i=0;
    for (workflow_object o:workflow.work) {
         if (o.getProperties().get("ObjectType").equals("Begin")) {
             //-- Is it in a for loop? If not increase, if True don't count...
             i++;
         }
    }
    return i;
}

////////////////////////////////////////////////////////////////////////////////
/// Various functions

/**
 * Function to return a unique number
 * @return a unique number 
 */
public int getHashcode() {
    return this.hashCount++;
}

/**
 *
 * @return true if some object are selected
 */
public boolean isSelection() {
    for (workflow_object obj:workflow.work) {
        if (obj.selected) return true;
    }
    return false;        
}

public void resetSelectionState() {
    for (workflow_object obj:this.workflow.work) {
        if (obj.selected) {
            obj.getProperties().remove("Status");
            obj.getProperties().remove("StatusString");
            obj.getProperties().remove("CommandLine_Running");
        }
    }
}

/**
 * Copy current selected object into a buffer for Copy&Paste
 */
public void copySelection() {
    //--clear selection
    CopyPaste_selection.work.clear();
    CopyPaste_selection.work_connection.clear();
    Paste_count=0;

    for (workflow_object obj:this.workflow.work) {
        if (obj.selected) {
            CopyPaste_selection.work.add(obj);
        }
    }
    for (workflow_connector_edge obj:this.workflow.work_connection) {
        if (CopyPaste_selection.work.contains(obj.getDestinationParent())||CopyPaste_selection.work.contains(obj.getSourceParent())) {
            CopyPaste_selection.work_connection.add(obj);
        }
    }
}

/**
 * Paste the previously copySelection into the workflow
 */
public void pasteSelection() {
    HashMap<workflow_object,workflow_object>oldnew=new HashMap<workflow_object,workflow_object>();
    Paste_count++;
    for (workflow_object old_object:this.CopyPaste_selection.work) {
        //--Create a new properties
        workflow_properties prop=old_object.getProperties();
        workflow_properties newprop=new workflow_properties();
        newprop.deserializeFromString(prop.serializeToString());
        //--Create and select object
        int x=newprop.getInt("x")+(30*Paste_count);
        int y= newprop.getInt("y")+(30*Paste_count);
        // TO DO CLipping here
        workflow_object new_object=createObject(newprop, x,y);
        new_object.selected=true;
        oldnew.put(old_object,new_object);
    }
    
    for (workflow_connector_edge obj:this.CopyPaste_selection.work_connection) {
            workflow_properties prop=obj.getProperties();            
            workflow_object source=oldnew.get(obj.getSourceParent());
            workflow_object dest=oldnew.get(obj.getDestinationParent());
            //--Handle case when source/dest is not in selection
            if (source==null) source=obj.getSourceParent();
            if (dest==null) dest=obj.getDestinationParent();
            //--Get the actual connector
            int source_connectorNb=obj.source.number;
            int dest_connectorNb=obj.dest.number;
             workflow_connector source_connector=source.connection[source_connectorNb];
             workflow_connector dest_connector=dest.connection[dest_connectorNb];
            if (source_connector!=null&&dest_connector!=null) {
                boolean notDeletabled=false;
                if (prop.isSet("notDeletabled")) notDeletabled=prop.getBoolean("notDeletabled");
                workflow.addConnector(source_connector, dest_connector, "",notDeletabled);
            } else {
//                Config.log(source+" "+source_connector);
//                Config.log(dest+" "+dest_connector);
            }
    }
    
}


    /**
     * @return the save_filename
     */
    public String getSave_filename() {
        return save_image_filename;
    }

    /**
     * @param save_filename the save_filename to set
     */
    public void setSave_filename(String save_filename) {
        this.save_image_filename = save_filename;
    }

    /**
     * @return the initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @param initialized the initialized to set
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * @return the changed
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * @param changed the changed to set
     */
    public void setChanged(boolean changed) {
        //-- debug System.out.println("Workflow changed "+changed);
        this.changed = changed;
    }
      

///**
// * Simple vertex class used by the workflow_object for position and detection
// * of insideness
// */
//class pvertex {
//  public float x;
//  public float y;
//  
//
//  public pvertex(float x, float y) {
//  this.x=x;
//  this.y=y;
//  }
//
//  public pvertex(int x, int y) {
//    this.x=(float)x;
//    this.y=(float)y;
//    }
//
//  }


/**
* The main workflow representation
*/
public class Workflow {

  //////////////////////////////////////////////////////////////////////////////
  /// Variables
  public Vector<workflow_object> work=new Vector<workflow_object>();           //List of workflow_object
  public Vector<workflow_connector_edge> work_connection=new Vector<workflow_connector_edge>();//List of workflow_connector_edge
  Object selected=null;               //Selected object (workflow_connector or workflow_object)
  boolean selectedWasConnected=false; //Just to be sure we don't loose the state of the object

  private armadillo_workflow armadillo; //pointer to the Armadillo_workflow

  //////////////////////////////////////////////////////////////////////////////
  /// Database needed variables (TO DO : Getter/ Setter)
  public int workflow_id=0;
  public String name=""; //This workflow_name
  public String filename="";                         //This workflow filename

  //- Debug
  int count=0;                        //counter for edge display numbering
   

////////////////////////////////////////////////////////////////////////////////
// Some functions by Alix and Etienne for tree building

  public int get_nb_workflow_object(){
    return work.size();
  }
  public String get_workflow_object_name(int index){
    workflow_object wo =  (workflow_object)work.get(index);
    return wo.getProperties().getName();
  }
  public int get_nb_workflow_connector_edge(){
    return work_connection.size();
  }
  public String get_workflow_connector_edge_source_name(int index){
    workflow_connector_edge wce= (workflow_connector_edge)work_connection.get(index);
    return wce.source.parent.getProperties().getID();
  }
  public String get_workflow_connector_edge_dest_name(int index){
    workflow_connector_edge wce= (workflow_connector_edge)work_connection.get(index);
    return wce.dest.parent.getProperties().getID();
  }

  /**
   * Note: FOR THIS FUNCTION
   * Undefined == 0 value
   * @return
   */
  public Vector<workflow_properties>findAllUndefined() {
     Vector<workflow_properties>tmp=new Vector<workflow_properties>();
     for (workflow_object obj:work) {
         if (obj instanceof workflow_object_output_database) {
             workflow_properties prop=obj.getProperties();
             String outputType=prop.get("outputType");
             if (prop.getInt("output_"+outputType.toLowerCase()+"_id")==0) tmp.add(prop);
         }
     }
     return tmp;
 }

    public Vector<workflow_object>findAllUndefinedObjects() {
     Vector<workflow_object>tmp=new Vector<workflow_object>();
     for (workflow_object obj:work) {
         if (isUndefined(obj)) {             
             tmp.add(obj);
         }
     }
     return tmp;
 }

  public boolean isUndefined(workflow_object obj) {
     if (obj instanceof workflow_object_output_database) {
             workflow_properties prop=obj.getProperties();             
             if (prop.getDescription().equals("Undefined")) return true;
             //--Otherwise, it is if no output is found... Except for for object
             if (prop.getInt("output_"+prop.get("outputType").toLowerCase()+"_id")==0&&!isFor(obj))  return true;
         }
      return false;
  }

 public boolean isFor(workflow_object obj) {
     if (obj instanceof WorkflowModel.workflow_object_output_database) {
             workflow_properties prop=obj.getProperties();
             for (Object k:prop.keySet()) {
                if (((String)k).startsWith("For_")) return true;
             }
         }
      if (obj.getProperties().isSet("ForObjectID")) {
         workflow_properties prop=obj.getProperties();
          for (Object k:prop.keySet()) {
                if (((String)k).startsWith("For_")) return true;
         }
      }
      return false;
  }

  /**
   * this is the main function creating the basic execution tree
   * @return
   */
  public LinkedList<workflow_object> outputExecution() {
      LinkedList<workflow_object>Execution=new LinkedList<workflow_object>();
      //--Use the script TriTopo to find the best path to run
      Scripts_conversion script=new Scripts_conversion();
      script.TriTopo(this, Execution);      
      return Execution;
  }

  /**
   * This function will return the workflow_object attached to either the True or the False branch of a If object
   * @param condition (either the True or False "port")
   * @return A list of workflow_object attached to either branch or nothing if it's not a If object
   */
  public LinkedList<workflow_properties> getIfExecution(workflow_object obj, boolean condition) {
      LinkedList<workflow_object>list_of_objects=new LinkedList<workflow_object>();
      LinkedList<workflow_properties>list_of_properties=new LinkedList<workflow_properties>();
      //--Test if we have a If Object, return otherwise
      if (!(obj instanceof workflow_object_if)) return list_of_properties;
      if (condition) {
        //--Get the first(s) child
         for (int i=0; i<get_nb_workflow_connector_edge();i++) {
           workflow_connector_edge edge=work_connection.get(i);
           if (edge.source.parent==obj&&edge.source.number==workflow_object.UP) list_of_objects.add(edge.dest.parent);
           
         }
      } else {
         //--Get the first(s) child
         for (int i=0; i<get_nb_workflow_connector_edge();i++) {
           workflow_connector_edge edge=work_connection.get(i);
           if (edge.source.parent==obj&&edge.source.number==workflow_object.DOWN) list_of_objects.add(edge.dest.parent);
         }
      }
      //--Then, find their child, etc....
      Vector<workflow_object>tmp=new Vector<workflow_object>();
      for (workflow_object p:list_of_objects) {         
          findAllOutput(p,tmp);
      }
      //--Put in the linkedList
      for (workflow_object p:tmp) {          
          list_of_objects.add(p);
      }
      //--Get the properties
      for (workflow_object p:list_of_objects) {
        list_of_properties.add(p.getProperties());
      }
      return list_of_properties;
  }

  public workflow_object findBegin() {
      for(int i=0; i<work.size();i++) {
            workflow_object obj=work.get(i);            
            if (obj.getProperties().get("ObjectType").equals("Begin")) {
                //--debug Config.log(obj.getName());
                return obj;
            } 
       }
      return null;
  }

  public Vector<workflow_object> findIf() {
      Vector<workflow_object>tmp=new Vector<workflow_object>();
      for(int i=0; i<work.size();i++) {
            workflow_object obj=work.get(i);            
            if (obj.getProperties().get("ObjectType").equals("If")) {
                //--debug Config.log(obj.getName());
                tmp.add(obj);
            } 
       }
      return tmp;
  }

  /**
   * Return the object needed for this object (parents)
   * @param obj
   * @return
   */
  public Vector<workflow_object> findInput(workflow_object source) {
     Vector<workflow_object>tmp=new Vector<workflow_object>();
      for (int i=0; i<get_nb_workflow_connector_edge();i++) {
           workflow_connector_edge edge=work_connection.get(i);
           if (edge.dest.parent==source) tmp.add(edge.source.parent);
      }
      return tmp;
  }
 
  public Vector<workflow_object> findConnectorInput(workflow_connector source) {
       Vector<workflow_object>tmp=new Vector<workflow_object>();
             //--Find all connection linking to the connector
              for (int i=0; i<get_nb_workflow_connector_edge();i++) {
                workflow_connector_edge edge=work_connection.get(i);
                //--Edge is connected to this connector
                //-- We add if we don't already have the parent
                if (edge.dest==source&&!tmp.contains(edge.source.parent)) {
                    tmp.add(edge.source.parent);
                }
            }
        return tmp;
  }
  
  /**
   * Return the imediate object descending from this object (children)
   * @param source
   * @return
   */
  public Vector<workflow_object> findOutput(workflow_object source) {
      Vector<workflow_object>tmp=new Vector<workflow_object>();
      for (int i=0; i<get_nb_workflow_connector_edge();i++) {
           workflow_connector_edge edge=work_connection.get(i);
           if (edge.source.parent==source) tmp.add(edge.dest.parent);
      }
      return tmp;
  }

  /**
   * Recursive fonction to Return All object descending from this object (children)
   * @param source
   * @return
   */
  public Vector<workflow_object> findAllOutput(workflow_object source, Vector<workflow_object> tmp) {
      if (findOutput(source).size()==0) return tmp;            
      for (int i=0; i<get_nb_workflow_connector_edge();i++) {
           workflow_connector_edge edge=work_connection.get(i);
           if (edge.source.parent==source&&!tmp.contains(edge.dest.parent)) {
               tmp.add(edge.dest.parent);
               findAllOutput(edge.dest.parent, tmp);
           }
      }      
      return tmp;
  }

  /**
   * This reset the Program Object output
   */
  public void resetProgramOutput() {
       for (workflow_object obj:work) {
          if (obj instanceof workflow_object_output&&!(obj instanceof workflow_object_output_database)) {
              obj.getProperties().removeInput();
              obj.getProperties().removeOutput();
          }
          if (obj.getProperties().get("ObjectType").equals("Program")) {
              obj.getProperties().removeOutput();
          }
      }
  }

 /**
  * This is the MAIN function which update the workflow object dependance
  * Ex. obj -> obj_output_object ->...
  * TO DO: Limit to 1 by collector
  */
  public void updateDependance() {     
      
      for (workflow_object obj:work) {
          if (obj instanceof workflow_object_output
              ||obj instanceof workflow_object_output_database
              ||obj instanceof workflow_object_if
              ) {
              Vector<workflow_object> input=findInput(obj);
               if (input.size()>0) {
                   String type=obj.getProperties().get("outputType").toLowerCase();                                      
                   for (workflow_object o:input) {                   
                       int id=o.getProperties().getOutputID(type);
                       //--Handle case of concatenation (NOTE: WARNING!!! Don't work...
//                       if (obj instanceof workflow_object_output_database && o instanceof workflow_object_output_database) {
//                            // TO DO : HANDLE CONCATENATION?
//                       } else {
//                           obj.getProperties().put("output_"+type+"_id", id);
//                       }
                       obj.getProperties().put("output_"+type+"_id", id);
                    } //End for input
              }
               if (obj.getProperties().get("ObjectType").equals("OutputDatabase")||obj.getProperties().get("ObjectType").equals("Output")) {
                      String type=obj.getProperties().get("outputType");
                       int id=obj.getProperties().getOutputID(type);
                       //--This might failed ?
                       if (id!=0) {
                           Output out=new Output();
                           out.setType(type);
                           Object bio=out.getBiologic();                                                     
                           obj.getProperties().put("Description", ((Biologic)bio).getNameId(id));                          
                       }
                }
              //--Variable
               if (obj instanceof workflow_object_variable ){
                   Vector<workflow_object> inputs=findInput(obj);
                  //--Array for the numbering of input
                   int connector_next_indice[]={0,0,0,0};
                   //--Clear input
                   obj.getProperties().removeInput();
                   if (inputs.size()>0) {
                       for (workflow_object o:inputs) {
                           String type=o.getProperties().get("outputType").toLowerCase();
                           int id=o.getProperties().getOutputID(type);
                           for (workflow_connector c:findConnection(o,obj)) {
                                   obj.getProperties().put("input_"+type+"_id"+c.number+(connector_next_indice[c.number]++),id);                                  
                           }
                        } //End for input
                   }
               }
               //--If
               if (obj instanceof workflow_object_if ){                 
                   //--Find input if any
                   Vector<workflow_object> inputs=findInput(obj);
                   obj.getProperties().removeOutput();
                   if (inputs.size()>0) {
                       for (workflow_object o:inputs) {
                           String type=o.getProperties().get("outputType").toLowerCase();
                           obj.getProperties().put("Output"+o.getProperties().get("outputType"), true);
                           obj.getProperties().put("outputType",o.getProperties().get("outputType"));                    
                           //--This should be set in the If program...                          
                           int id=o.getProperties().getOutputID(type);
                            obj.getProperties().put("output_"+type+"_id",id);
                            obj.getProperties().put("input_"+type+"_id00",id);
                        } //End for input
                   }
               }
          } else {            
              Vector<workflow_object> input=findInput(obj);              
              //--Array for the numbering of input
               int connector_next_indice[]={0,0,0,0};
               //--Clear input
               obj.getProperties().removeInput();
               if (input.size()>0) {
                   for (workflow_object o:input) {
                       String type=o.getProperties().get("outputType").toLowerCase();
                       int id=o.getProperties().getOutputID(type);                       
                       for (workflow_connector c:findConnection(o,obj)) {
                               obj.getProperties().put("input_"+type+"_id"+c.number+(connector_next_indice[c.number]++),id);
                               //Config.log(o+"UpdateDependance : "+id+c);
                               //--delete next input
                               //obj.getProperties().remove("input_"+type+"_id"+c.number+(connector_next_indice[c.number]));
                       }                           
                    } //End for input
               }
          } //End else
      } //End for workflow_object
 }

 Vector<workflow_connector> findConnection(workflow_object source, workflow_object dest) {
     Vector<workflow_connector>tmp=new Vector<workflow_connector>();
     //--check if we really have an input
     if (!findInput(dest).contains(source)) return tmp;
     //--WE really have an input
     for (workflow_connector_edge e:work_connection) {
         //--We have a connection
         if (e.source.parent==source&&e.dest.parent==dest) {
            //--We add the destination
             tmp.add(e.dest);
         }
     }
     return tmp;
 }

 ///////////////////////////////////////////////////////////////////////////////
 /// Object creation - deletion into the workflow

 /**
  * Add an object to the work Vector before any aggregator object
  * Note: normally called by createObject() in the main PApplet
  * @param a (workflow_object)
  */
  public boolean add(workflow_object a) {
    //--Create an ID for this object
      a.getProperties().setID(a.toString());
    // CASE 1: Aggregator, we add at the end
    if (a instanceof workflow_object_aggregator) {
        return work.add(a);
    } else {
    // CASE 2: We add before the first aggregator
        //--Set good color
        checkforAggregation(a);
        //--Insert at the good place in the list
        for (int i=0;i<work.size();i++) {
            Object tmp=work.get(i);
            if (tmp instanceof workflow_object_aggregator) {
               // First element is aggregator? No insert before
                if (i>0) {
                   work.insertElementAt(a, i-1);
                   return true;
               } else {
               // Else insert at first element
                  work.insertElementAt(a, 0);
                  return true;
               }
            }
        }
        // CASE 3: No aggregator, insert at end
        return work.add(a);
    }
  }

  /**
   * Add a connection between two workflow_connector
   * Note: Verify if we have already have a connection between the parent
   * Note: Also verify if connection already exists
   * Note: Also special case for If object
   * @param source
   * @param dest
   * @param desc
   */
 public boolean addConnection(workflow_connector source, workflow_connector dest, String desc) {
     
     //--Determine if source is not a dest and dest is not a source
     //CASE 0. source or dest==null
     if (source==null) return false;
     if (dest==null) return false;
    //CASE 1. Source and Dest are output but not conditional
     if ((source.output&&dest.output)&&!(source.conditional&&dest.conditional)) return false;
     //CASE 1.1: Do not allow connection between non conditional object and conditionnal
     //Septembre 9 2010
     //if ((dest.conditional&&!source.conditional)||(source.conditional&&!dest.conditional)) return false;

    //CASE 2. Source is an output dest is not? Inverse
     if (!source.output&&dest.output) {
         return addConnection(dest, source, desc);
      }
     //--If we have no connection and its compatible add...
     if (!existsConnection(source, dest)&&isCompatible(source, dest)) {
         //source.parent.getClass()==workflow_object_output.class
         //||source.parent.getClass()==workflow_object_output_big.class||source.parent.getClass()==workflow_object_output_database.class

         //1. Is it already a object_output or we have conditional object
         // -> Then we create a connection and add it to the list
         if ((source.conditional&&dest.conditional)||source.parent instanceof workflow_object_output||source.parent instanceof workflow_object_if) {
            workflow_connector_edge tmp=new workflow_connector_edge(source, dest,desc);
            work_connection.add(tmp);
            //--Test if we have and IfObject
            if (dest.parent instanceof workflow_object_if) {
                //--We have one, we need to change the ouput of the outputType
                dest.parent.getProperties().put("Output"+source.parent.getProperties().get("outputType"), true);
            }
            return true;
         } 
         return true;
     }
     return false;
  }
  
 /**
  * HELPER FONCTION TO RETURN THE CONNECTOR OF A PARENT...
  * @param parentID
  * @param number
  * @return
  */
  public workflow_connector getConnector(int parentID, int number) {
      for (workflow_object o:work) {
          //--Debug Config.log(parentID+" "+o.getProperties().getName()+" "+o.getProperties().getProperties_id());
          if (o.properties.getProperties_id()==parentID) {
              if (number>o.connection.length-1) return null;
              return o.connection[number];
          }
      }
      return null;
  }

  /**
   * This is the method which add a connector (edge) from source to destination
   * @param source
   * @param dest
   * @param desc
   * @param notDeletable
   */
  public void addConnector(workflow_connector source, workflow_connector dest, String desc, boolean notDeletable) {
      workflow_connector_edge tmp=new workflow_connector_edge(source, dest,desc);
            tmp.setNotDeletabled(notDeletable);
      work_connection.add(tmp);
  }

 /**
  * Determine if the source connector is compatible with the destination
  * Warning: don't check if the parent object are compatible....
  * Note: You can get the compatible type using getCompatibleType()
  * @param source
  * @param dest
  * @return true if compatible
  */
 boolean isCompatible(workflow_connector source, workflow_connector dest) {   
     // CASE 0. Connector accept another input?

     //CASE 1. Source already connected to destination...
     //--Example two tree connected to the same input
     //--Find all input for this connector
     Vector<workflow_object> dest_input_objects=findConnectorInput(dest);
     //--Find source type and id
     workflow_object source_object=source.parent;
     String source_type=source_object.getProperties().get("outputType");
     
     String source_Object_id=source_object.getProperties().getID();
     int source_id=source_object.getProperties().getOutputID(source_type);
      
     for (workflow_object dest_input_object:dest_input_objects) {
         String dest_type=dest_input_object.getProperties().get("outputType");
         String dest_Object_id=dest_input_object.getProperties().getID();
          int dest_id=dest_input_object.getProperties().getOutputID(source_type);
          if (source_type.equals(dest_type)&&source_id==dest_id&&source_Object_id.equals(dest_Object_id)) return false;
     }      
     // CASE 2. Two conditional object? (workflow_path) return true
     if (source.conditional&&dest.conditional&&source.isOutput()) {
         return true;
     }
    // CASE 3. Source can outputAll and dest can inputAll return true;
     if (source.isOutputAll()&&dest.isInputAll()) {
           return true;
     }

     // CASE 4. Try to return the first compatible type
     for (String type:source.getOutput()) {
        if (dest.input(type)) return true;
     }
    
     //--End--Probably not compatible
     return false;
 }

 /**
  * Return if a workflow_connector already ave a connection
  * @param source
  * @return
  */
 public boolean haveConnection(workflow_connector source) {
     for (workflow_connector_edge edge:this.work_connection) {
         if (edge.dest.equals(source)) return true;     
     }
     return false;
 }

 /**
  * Return a compatible connector to attach the source in the dest object...
  * @param source
  * @param dest
  * @return a compatible connector or null if not found
  */
 public workflow_connector findCompatible(workflow_connector source, workflow_object dest) {
    //Case 0. If object
     if (source.parent instanceof workflow_object_if) {
          for (workflow_connector c:dest.connection) {
            if (c.conditional) return c;
         }
         for (workflow_connector c:dest.connection) {
             if (isCompatible(source, c)) return c;
         }
    }
     //Case 1. Conditional source
     if (source.conditional) {
         for (workflow_connector c:dest.connection) {
            if (c.conditional) return c;
         }
     } else {
     //CASE 2. normal object
         for (workflow_connector c:dest.connection) {
             if (isCompatible(source, c)) return c;
         }
     }
     return null;
 }



/**
 * This find a compatible source from the source.parent to the dest
 * Note: Usefull if source can outputAll
 * Warning: No test here if the source and dest are compatible
 * @param source
 * @param dest
 * @return
 */
  public String getCompatibleType(workflow_connector source, workflow_connector dest) {          
    // CASE 1. Source outputALL
    for (String input:source.getOutput()) {
        if (dest.input(input)) return input;
    }
    return "";
  }

 /**
  * Verify if a connection already exists between two objects
  * A connection exists if:
  *     - The source and dest parent are the same
  *     - A connection already exists between the source parent->dest parent
  * @param source
  * @param dest
  * @return
  */
  private boolean existsConnection(workflow_connector source, workflow_connector dest) {
     workflow_object source_parent=source.parent;
     workflow_object dest_parent=dest.parent;
     if (source_parent==dest_parent) return true;
     //--Verify if we already connect to this connector
     for (int i=0; i<work_connection.size();i++) {
         workflow_connector_edge tmp=(workflow_connector_edge)work_connection.get(i);
         //CASE 0: Same source and Same connector?
         if (tmp.source==source&&tmp.dest==dest) return true;
         //CASE 1: SAME Connection exists (bidirectional)
         
         //if (tmp.source==source&&tmp.dest==dest) return true;
         //if (tmp.dest==source&&tmp.source==dest) return true;
         //CASE 2: SAME PARENT?
         if (tmp.source.parent==source_parent&&tmp.dest.parent==dest_parent) return true;
         if (tmp.source.parent==dest_parent&&tmp.dest.parent==source_parent) return true;
    }
     //CASE 3: COnnection don't exists...
     return false;
  }

 ///////////////////////////////////////////////////////////////////////////////
 /// DELETION

  /**
   * Handle the different deletion cae
   * @param selection
   */
  private void delete(Object o) {
      //CASE 1: We have some object
      if (o!=null) {
         if (o instanceof workflow_connector_edge) safelyDeleteConnector((workflow_connector_edge)o);
         if (o instanceof workflow_object_aggregator) safelyDeleteAggregator((workflow_object_aggregator)o); // TO DO
         if (o instanceof workflow_object) safelyDelete((workflow_object)o);
      }
//      } else {
//      //CASE 2: Any selected multiple object?
//        for (int i=work.size()-1; i>-1;i--) {
//            workflow_object tmp=(workflow_object)work.get(i);
//            if (tmp.selected) delete(tmp);
//        }
//      }
  }

  /**
   * This is the safe way to delete an Object
   * ->Select the object
   * ->deleteSelected
   * @return
   */
  public boolean deleteSelected() {
      //-- Delete selection (because we want to delete a selected aggregator object)
      // delete(selected);
      //--Delete all selected object
      setChanged(true);
      //--Save a copy to the undo pile
      //saveToUndo("Delete selection");

      for (int i=work.size()-1;i>-1;i--) {
          workflow_object tmp=(workflow_object)work.get(i);
          if (tmp.selected) delete(tmp);
      }
      //--Delete all selected connection
      for (int i=work_connection.size()-1;i>-1;i--) {
          workflow_connector_edge tmp=(workflow_connector_edge)work_connection.get(i);
          if (tmp.selected) delete(tmp);
      }
     
      return true;
  }

  /**
   * This safety delete a workflow_object from the workflow
   * @param o
   * @return
   */
  public boolean safelyDelete(workflow_object o) {
      // Special case for FOR and WHILE
       //-- Delete all external connection
            RemoveAllConnection(o);            
            return(work.remove(o));
  }

  public boolean safelyDeleteAggregator(workflow_object_aggregator o) {
         RemoveAllConnection(o);
         work.remove(o);
         resetColor();
         return true;
     }

  /**
   * This safety delete a workflow_connector_edge from the workflow
   * @param o
   * @return
   */
  public boolean safelyDeleteConnector(workflow_connector_edge o) {
      //--Buffering of parent and dest connector
      //--And removing of selection for parent and dest connector
      // 1. Check if we can delete?
      if (  o.isNotDeletabled()) return false;
      // 2. deletable->Yes
      workflow_connector source=o.source;
      workflow_connector dest=o.dest;
      source.selected=false;
      dest.selected=false;
      dest.destination=false;
      //--Removing of object
      if (!work_connection.remove(o)) {
          if (debug) Config.log("Unable to delete connector_edge "+o);
          return false;
      }
      //--We then verify if parent and dest have other connection
       source.selected=isConnected(source);
       dest.selected=isConnected(dest);
       //--Verify is dest is still a destination node (no drawing)
       if (dest.selected) {
           dest.destination=isDestination(dest);
       }

       return true;
  }

  /**
   * Return if a connector is connected to another (or anything)
   * @param connector
   * @return true or false
   */
  public boolean isConnected(workflow_connector connector) {
    for (int i=0; i<work_connection.size();i++) {
        workflow_connector_edge con=(workflow_connector_edge)work_connection.get(i);
        if (con.source==connector||con.dest==connector) return true;
     }
  return false;
 }

 public boolean isDestination(workflow_connector connector) {
    for (int i=0; i<work_connection.size();i++) {
        workflow_connector_edge con=(workflow_connector_edge)work_connection.get(i);
        if (con.dest==connector) return true;
     }
  return false;
 }

  /**
   * Delete all the connection to an object
   * @param o
   * @return
   */
 public boolean RemoveAllConnection(workflow_object o) {
    for (int i=work_connection.size()-1; i>-1;i--) {
        workflow_connector_edge con=(workflow_connector_edge)work_connection.get(i);
        if (con.source.parent==o||con.dest.parent==o) {
                    con.setNotDeletabled(false);
            safelyDeleteConnector(con);
        }
     }  
    return true;
 }

// /**
//  * Helper function to create a workflow_object_output with correct size
//  * @param s
//  */
// private workflow_object_output createOutput_Object(String outputType, int x, int y) {
//        if (outputType.length()>10) {
//            return new workflow_object_output_big(outputType, x,y);
//        } else {
//            return new workflow_object_output(outputType, x,y);
//        }
// }

 /**
  * Helper function to create a workflow_object_output with correct size
  * @param s
  */
 private workflow_object_output createOutput_Object(workflow_properties properties, int x, int y) {
        if (properties.getName().length()>10) {
            return new workflow_object_output_big(properties, x,y);
        } else {
            return new workflow_object_output(properties, x,y);
        }
 }

 public void selectAll() {
        for (int i=0;i<work.size(); i++) {
          workflow_object tmp=(workflow_object)work.get(i);
          tmp.selected=true;

        }
        for (int i=0; i<work_connection.size();i++) {
            workflow_connector_edge tmp=(workflow_connector_edge)work_connection.get(i);
            tmp.selected=true;
        }
 }

 public void selectNone() {
        for (int i=0;i<work.size(); i++) {
          workflow_object tmp=(workflow_object)work.get(i);
          tmp.selected=false;

        }
        for (int i=0; i<work_connection.size();i++) {
            workflow_connector_edge tmp=(workflow_connector_edge)work_connection.get(i);
            tmp.selected=false;
        }
 }

  public void inverseSelection() {
        for (int i=0;i<work.size(); i++) {
          workflow_object tmp=(workflow_object)work.get(i);
          tmp.selected=!tmp.selected;

        }
        for (int i=0; i<work_connection.size();i++) {
            workflow_connector_edge tmp=(workflow_connector_edge)work_connection.get(i);
            tmp.selected=!tmp.selected;
        }
 }

  /**
   * This reset the colorMode of a workflow_object an account for aggregation
   * object (for, while...)
   * @param o
   */
   public void checkforAggregation(workflow_object o) {
//        o.resetColorMode();
//        o.getProperties().remove("InsideFor");
//        //--Iterate over each object to find aggregator
//        //-- Note: we should create a special list for them? or an index of position?
//        for (int i=0;i<work.size();i++) {
//             workflow_object tmp=(workflow_object)work.get(i);
//              if (tmp instanceof workflow_object_aggregator) {
//                 //--Create a selection_object (easier to check for insideness
//                  workflow_object_aggregator tmp2=(workflow_object_aggregator)tmp;
//                  workflow_selection select=new workflow_selection(tmp2.x1,tmp2.y1, tmp2.x2, tmp2.y2);
//                  if(select.inside(o.x, o.y)) {
//                      o.setColorMode(((workflow_object_aggregator)tmp).getColorMode());
//                      o.getProperties().put("InsideFor", true);
//                      return;
//                  }
//              }
//        }
   }

  
 /**
  * This reset the selection/moving state of each workflow_object
  */
  public void resetSelected() {
           //--Reset Selected Object
           for (int i=0;i<work.size();i++) {
              workflow_object tmp=(workflow_object)work.get(i);
              tmp.selected=false;
              tmp.moving=false;
           }
           //Reset Selected Connection
           for (int i=0;i<work_connection.size();i++) {
              workflow_connector_edge tmp=(workflow_connector_edge)work_connection.get(i);
              tmp.selected=false;
           }
           //--No selection
            //workbox.setSelectedObject(null);
  }

  public void resetColor() {
      for (int i=0;i<work.size();i++) {
            workflow_object tmp=(workflow_object)work.get(i);
            if (!(tmp instanceof workflow_object_aggregator)) checkforAggregation(tmp);
      }
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////
  /// Draw Arrow -- Duplicate ot workflow_connector_edge

  public void drawArrow(float x1, float y1, float x2, float y2, int lineColor, int fillColor) {
//    float angle=atan2(y2-y1,x2-x1);
//    stroke(lineColor); //Line color
//    fill(fillColor);   //fill color
//    strokeWeight(2.0f);
//    line(x1,y1,x2,y2);
//    pushMatrix();
//    translate(x2,y2);
//    rotate(angle);
//    beginShape();
//      vertex(-12,-4);//Far Outside UP
//      vertex(0,0);   //Middle point
//      vertex(-12,4); //Far Outside Bottom
//      vertex(-10,-1); //Indide Middle point
//    endShape(CLOSE);
//    popMatrix();
}

  public void drawBigArrow(float x1, float y1, float x2, float y2, int lineColor, int fillColor) {
//    float angle=atan2(y2-y1,x2-x1);
//    stroke(lineColor); //Line color
//    fill(fillColor);   //fill color
//    strokeWeight(5.0f);
//    line(x1,y1,x2,y2);
//    pushMatrix();
//    translate(x2,y2);
//    rotate(angle);
//    beginShape();
//      vertex(-15,-5);//Far Outside UP
//      vertex(0,0);   //Middle point
//      vertex(-15,5); //Far Outside Bottom
//      vertex(-12,-1); //Indide Middle point
//    endShape(CLOSE);
//    popMatrix();
}

  /////////////////////////////////////////////////////////////////////////////////////////////////////
  /// miscellaneous Fonction -- Mostly for debug

  public void createRandomConnection() {
     workflow_object source=(workflow_object)work.get((int)Math.random()*work.size());
     workflow_object dest=(workflow_object)work.get((int)Math.random()*work.size());
     if (source!=dest) {
          //-- Choose a connector (Normally we should choose the closest
          workflow_connector source_connector=source.connection[(int)Math.random()*4];
          workflow_connector dest_connector=dest.connection[(int)Math.random()*4];
          work_connection.add(new workflow_connector_edge(source_connector, dest_connector,""+count++));
     }
  }

  public void displayEdge() {
     for (int i=0; i<work_connection.size();i++) {
        workflow_connector_edge con=(workflow_connector_edge)work_connection.get(i);
        con.displayEdge=true;
     }
  }

        /**
         * @return the armadillo
         */
        public armadillo_workflow getArmadillo() {
            return armadillo;
        }

        /**
         * @param armadillo the armadillo to set
         */
        public void setArmadillo(armadillo_workflow armadillo) {
            this.armadillo = armadillo;
        }

 } //End workflow

/**
 * A Connection object on a workflow_object
 * This in fact represent one point witch migh be connected to annother point
 * Etienne Lord
 */
public class workflow_connector {
   //--Connection specific
   int number=0;                            //This connector number
   private boolean output=false;            //Is is an output connector?
   private boolean conditional=false;       //Is is a conditional input//output
   private boolean accept_multiple_input=true;//Can the connector accept multiple inputs? (default: true)
   workflow_object parent=null;
   String outputType;                       //Connector type of Output
   //--Position
   int x=0;
   int y=0;
   //--Interface variables
   public boolean selected=false;
   public boolean destination=false; // -- Is it a destination node: If true, we don't draw the black node

   /////////////////////////////////////////////////////////////////////////////
   /// Constructor
   public workflow_connector(workflow_object parent) {
    this.parent=parent;
   }

   /**
    *
    * @param type
    * @return can this connector accept the specified input
    */
   public boolean input(String type) {
       if (this.output) return false;
       if (isInputAll()) return true;
       if (outputType!=null&&outputType.equals(type)) return true;
       if (outputType!=null&&outputType.equals("Outgroup")&&type.equals("Sequence")) return true;
       String keyConnector="Connector"+number;
       String value=parent.properties.get("Input"+type);
       if (value.equalsIgnoreCase("True")) return true;
       if (value.equals(keyConnector)) return true;
       return false;
   }

   public Vector<String> getOutput() {
       Vector<String>tmp=new Vector<String>();
       //--Case 1. Not an output... return empty array
       if (!isOutput()||isInputAll()) return tmp;
       String keyConnector="Connector"+number;
       //--Case 2. OutputAll ->Return Everything
       if (isOutputAll()) {
           for (String S:workflow_properties_dictionnary.InputOutputType) tmp.add("Output"+S);
           return tmp;
       }
       //--Case 3. Return output
       if (outputType!=null) {
          tmp.add(outputType);
          return tmp;
       }

         for (String key:workflow_properties_dictionnary.InputOutputType) {
             String value=parent.properties.get("Output"+key);
             if (value.equals(keyConnector)||Boolean.valueOf(value)) {
                 tmp.add(key);
             }
         }

       return tmp;
   }
   
   public Vector<String> getInput() {
       Vector<String>tmp=new Vector<String>();
       //--Case 1. Not an output... return empty array
       if (isOutput()) return tmp;
       String keyConnector="Connector"+number;
       //--Case 2. OutputAll ->Return Everything
       if (isInputAll()) {
           for (String S:workflow_properties_dictionnary.InputOutputType) tmp.add("Input"+S);
           return tmp;
       }
       //--Case 3. Return output
       if (outputType!=null) {
          tmp.add(outputType);
          return tmp;
       }

         for (String key:workflow_properties_dictionnary.InputOutputType) {
             String value=parent.properties.get("Input"+key);
             if (value.equals(keyConnector)||value.equals("True")) {
                 tmp.add(key);
             }
         }
       
       return tmp;
   }

   public boolean isOutputAll() {
       String value=parent.getProperties().get("OutputAll");
       boolean b=(value.equals("True")||value.equals("Connector"+number));
       return b;
   }
      
   public boolean isInputAll() {
       String value=parent.getProperties().get("InputAll");
       boolean b=(value.equals("True")||value.equals("Connector"+number));
       return b;
   }


   public void setOutput(boolean b) {
       this.output=b;
   }

   public boolean isOutput() {
       return this.output;
   }

    public boolean isConditional() {
       return this.conditional;
   }

    public void setConditional(boolean b) {
        this.conditional=b;
    }

        /**
         * @return the accept_multiple_input
         */
        public boolean isAccept_multiple_input() {
            return accept_multiple_input;
        }

        /**
         * @param accept_multiple_input the accept_multiple_input to set
         */
        public void setAccept_multiple_input(boolean accept_multiple_input) {
            this.accept_multiple_input = accept_multiple_input;
        }

}//--End connector

/**
 * A Drag connector :: Used to redimension some component
 * This in fact represent one point witch migh be connected to annother point
 * Etienne Lord
 */
public class workflow_drag_connector {
   ////////////////////////////////////////////////////////////////////////////
   /// VARIABLES
    String name="";
   workflow_object parent=null;
   int x=0;
   int y=0;
   public int mode=UPRIGHT;
   public boolean selected=false;

   ////////////////////////////////////////////////////////////////////////////
   /// CONSTANT
   public final static int UPLEFT=3;  //Strange numbering but easier for resize
   public final static int UPRIGHT=0;
   public final static int DOWNLEFT=2;
   public final static int DOWNRIGHT=1;

}


/**
 * This represent a Edge (connection) between to workflow_connector
 * In reality, we use this object to draw an Arrow
 *
 */
public class workflow_connector_edge {
    /////////////////////////////////////////////////////////////////////////////////
    /// Variables
    public workflow_connector source=null;   // Source
    public workflow_connector dest=null;     // Destination
    String desc="";                          // Edge description
    int color = 0;                           // Color (TO DO : PUT SOME COLOR IN CONFIG)
    public boolean displayEdge=false;        // Do we display edge
    private boolean notDeletabled=false;      // Can we deleted it?
    int hashcode=0;                          // hashcode used for arrow selection
    public boolean selected=false;           // Selected?
    //////////////////////////////////////////////////////////////////////////////
    /// Database info
    public workflow_properties properties=new workflow_properties();
    public int connectors_id=0;
    
    /////////////////////////////////////////////////////////////////////////////////
    /// Constructor

    public workflow_connector_edge(workflow_connector source,workflow_connector dest, String desc) {
   //-- Warning, check must be made before to check if source!=dest;
     this.source=source;
     this.dest=dest;
     this.desc=desc;
     this.dest.destination=true;
     source.selected=true;
     dest.selected=true;
     //hashcode=color(getHashcode());
     properties.setName("connector_"+hashcode);
     properties.put("source", source.number);
     properties.put("destination", dest.number);
     properties.put("notDeletabled", notDeletabled);
     properties.put("displayEdge", displayEdge);
   }

  public workflow_properties getProperties() {
      return this.properties;
  }


        /**
         * @param desc the desc to set
         */
        public void setDesc(String desc) {
            this.desc = desc;
        }

        /**
         * @return the color
         */
        public int getColor() {
            return color;
        }

        /**
         * @param color the color to set
         */
        public void setColor(int color) {
            this.color = color;
        }


        public workflow_object getSourceParent() {
            return this.source.parent;
        }

        public workflow_object getDestinationParent() {
            return this.dest.parent;
        }

        /**
         * @return the notDeletabled
         */
        public boolean isNotDeletabled() {
            return notDeletabled;
        }

        /**
         * @param notDeletabled the notDeletabled to set
         */
        public void setNotDeletabled(boolean notDeletabled) {
            this.notDeletabled = notDeletabled;
            properties.put("notDeletabled", notDeletabled);
        }


} //End class


/**
* Idealement, une repr\u00e9sentation graphique est seulement une suite de feature
*
* Chaque feature une methode drawFeature. Cette partie du code provient de :
*
* using-awt-s-polygon-class-to-test-for-insideness taken from http://processinghacks.com/hacks:using-awt-s-polygon-class-to-test-for-insideness
* author Andreas K\u00f6berle
*
*
*/
public class workflow_object {
  // A la base, la fearure n'est qu'un poligon former de vertex
  // TO DO: Put the pvertex in a Vector Collection
  //pvertex[] Vertex;
  int Vertex_count=0;
  //Vector<String>input=new Vector<String>();
  //Vector<String>output=new Vector<String>();
  //public int nbInput=0;             //TO DO JUST FOR TEST  Warning debug
  //String colorMode=defaultColorMode;
  public int x=0;                      // this is the object center X
  public int y=0;                      // this is the object center Y
  public boolean moving=false;       // Are we moving
  public boolean selected=false;     // Selected?
  public long time_over=0;            //over_time represent the start time in millisecond we are over an object

  workflow_connector[] connection;    //Connector to this object
  int test=0;
  
  //////////////////////////////////////////////////////////////////////////////
  /// Database info
  public workflow_properties properties=new workflow_properties();
  public int objects_id=0;

  // CONSTANT
  public final static int LEFT=0; //BECAUSE OF IF OBJECT... EASIER
  public final static int UP=1;
  public final static int DOWN=2;
  public final static int RIGHT=3;
  public final static int UPLEFT=3;  //Strange numbering but easier for resize
  public final static int UPRIGHT=0;
  public final static int DOWNLEFT=2;
  public final static int DOWNRIGHT=1;
  public final static int OUTPUT=0;
  public final static int INPUT1=2;
  public final static int INPUT2=3;
  public final static int TOP=1;


  /**
  * Constructor
  */
  public workflow_object() {}

  /**
   * Alternative constructor to put description
   */
  public workflow_object(workflow_properties obj) {
    this.properties=obj;
  }

  /**
   * Default constructor for drag and drop
   */
  public workflow_object(workflow_properties obj, int x2, int y2) {
    this.properties=obj;
  }

  /**
   *
   * @return this Object properties
   */
  public workflow_properties getProperties() {
      return this.properties;
  }

  @Override
  public String toString() {
      return properties.getName()+"_"+Util.returnCount();
  }

  /**
   * Generic method to create a connector object
   */
  public workflow_connector createConnector(int number,int x, int y, String type) {
    workflow_connector tmp=new workflow_connector(this);
    tmp.x=x;
    tmp.y=y;
    tmp.number=number;
    String keyConnector="Connector"+number;
    String keyOutput="Connector"+number+"Output";
    String keyConditional="Connector"+number+"Conditional";
    tmp.output=(properties.isSet(keyOutput));
    tmp.conditional=properties.isSet(keyConditional);
    tmp.outputType=type;
    return tmp;
  }

  /**
   * Generic method to update a connector object (position, properties...)
   */
  public void updateConnector(workflow_connector tmp, int number,int x, int y, String type) {
    tmp.x=x;
    tmp.y=y;
    tmp.number=number;
    String keyConnector="Connector"+number;
    String keyOutput="Connector"+number+"Output";
    String keyConditional="Connector"+number+"Conditional";
    tmp.output=(properties.isSet(keyOutput));
    tmp.conditional=properties.isSet(keyConditional);
    tmp.outputType=type;
 }

  /**
   * This is needed to recalculate the object position if it's moved
   * Note: it also set its properties
   */
  public void recalculatePosition() {
  }

  public String getName() {
     return properties.getName();
  }

  

  }//End workflow_object

class workflow_object_script extends workflow_object {

    public workflow_object_script(workflow_properties obj) {
       super();
       this.properties=obj;  
    }

    public workflow_object_script(workflow_properties obj, int x2, int y2) {
        super();
        this.properties=obj;
    }

 } //--End script

class workflow_object_script_big extends workflow_object {

public workflow_object_script_big(workflow_properties obj) {
   super();
   this.properties=obj;   
}

    public workflow_object_script_big(workflow_properties obj, int x2, int y2) {
        super();
        this.properties=obj;
    }
} //--End script_big


class workflow_object_BeginEnd extends workflow_object {

    public workflow_object_BeginEnd(workflow_properties obj) {
        super();
        this.properties=obj;     
    }

    public workflow_object_BeginEnd(workflow_properties obj, int x1, int y1) {
        super();
        this.properties=obj;    
    }
} //--End object BeginEnd

/**
 * This represent an output object
 * used to represent one type of output
 */
class workflow_object_output extends workflow_object {

    float step=1;        //Current step for animation
    float stepNb=20;     //Number of step
//    pvertex source;       //Source Spline vertex
//    pvertex destination; //Destination Spline vertex
    String description="";

    ////////////////////////////////////////////////////////////////////////////
    /// Constants
    final static int Output_LEFT=0;
    final static int Output_RIGHT=1;
    
    ////////////////////////////////////////////////////////////////////////////
    /// Default constructor for other objects
    
    public workflow_object_output() {}

    ////////////////////////////////////////////////////////////////////////////
    /// Normal default constructor

     public workflow_object_output(workflow_properties properties, int x, int y) {
        super();
        this.properties=properties;     
    }

}


/**
 * Bigger object 
 */
class workflow_object_output_big extends workflow_object_output {   

    public workflow_object_output_big(workflow_properties properties, int x, int y) {
         super();
         this.properties=properties;               
    }

}

/**
 * Variable object
 */
public class workflow_object_variable extends workflow_object_output {

      public workflow_object_variable (workflow_properties properties, int x, int y) {
         super();
         this.properties=properties;       
    }

}


/**
 * Bigger object
 */
public class workflow_object_output_database extends workflow_object_output {

      public workflow_object_output_database (workflow_properties properties, int x, int y) {
         super();
         this.properties=properties;      
    }

}


public class workflow_object_aggregator extends workflow_object {
      public int x1,y1,x2,y2;
      int w=0;
      int h=0;
      int min_w=100;
      int min_h=100;
      public workflow_drag_connector[] drag; //Drag connector for redimension
     
     public workflow_object_aggregator(workflow_properties obj, int x, int y, int w, int h) {
       super();
       this.properties=obj;  
     }

     public workflow_object_aggregator(workflow_properties obj, int x, int y) {
       super();
       this.properties=obj;  
    } //End aggregator_object

}

class workflow_object_if extends workflow_object {

    public workflow_object_if(workflow_properties obj_properties) {
      super();
      this.properties=obj_properties; 
    }

    public workflow_object_if(workflow_properties obj_properties, int x1, int y1) {
      super();
      this.properties=obj_properties; 
    } //--End workflow_object_if
}

/**
 * Selection object
 */
public class workflow_selection {

}

} //End workflow

