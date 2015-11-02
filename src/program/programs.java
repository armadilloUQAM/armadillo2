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


package program;

import biologic.RunWorkflow;
import Class.Classdata;
import biologic.Biologic;
import biologic.Input;
import biologic.Output;
import biologic.Unknown;
import biologic.Workflows;
import configuration.Config;
import configuration.Util;
import database.databaseFunction;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import tools.Toolbox;
import workflows.Workbox;
import workflows.armadillo_workflow;
import workflows.armadillo_workflow.workflow_object;
import workflows.workflow_properties;
import workflows.workflow_properties_dictionnary;

/**
 *
 * @author Etienne Lord
 * @since July 2009
 */
public class programs implements ActionListener {
    ////////////////////////////////////////////////////////////////////////////
    /// Variables

    public static long timerunning=0;   //time running
    public static int retry=0;          //Retry

    // Database and Class variables (static)
    public static workflow_properties_dictionnary dict=new workflow_properties_dictionnary();
    public static databaseFunction df=new databaseFunction();
    public static Workbox work=new Workbox();
    public static armadillo_workflow armadillo;
    public static Workflows workflows;
    public static Toolbox tool=new Toolbox();
    public static Config config=new Config();


    public static RunProgram runningObject=null;               //Current running Object
    
    public static workflow_properties runningProperties=null;
    //static RunProgram run=null;                     //Database object

    static Thread thread=null;                      //Thread to run program...
    public RunWorkflow run_workflow;                //RunWorkflow
    //static Vector<Thread> thread_vector=new Vector<Thread>(); //v3
    static Boolean thread_vector_running_NoThread=false; //--Running No Thread Version
    SwingWorker<Integer, String> RunningSwingWorker=null;
    public static SwingWorker updateThread=null;                                //--Timer for AutoUpdate of the workflow

    public boolean update=false;
    public static boolean running=false;
    public boolean cancel=false;
    public boolean done = false; //Are we running?
    
    Frame frame;                                  //MainFrame
   
     /////////////////////////////////////////////////////////////////////////
    /// THREAD

    public programs(Workflows workflows) {
        try {
        programs.armadillo=workflows.workflow;
        programs.workflows=workflows;
        } catch(Exception e) {
            Config.log("Error in programs: no armadillo_workflow in the workflow...");
        }
     }

//     public programs(Workflows workflows, String... filename) {
//        try {
//        programs.armadillo=workflows.workflow;
//        programs.workflows=workflows;
//        } catch(Exception e) {
//            Config.log("Error in programs: no armadillo_workflow in the workflow...");
//        }
//
//     }
    //public programs() {}  //--Only for debug

    /**
     * This stop the current running program...
     */
    public void Stop() {
        cancel=true;
        Config.log("Cancelling...");
        killer k=new killer(runningProperties);
        if (RunningSwingWorker!=null) RunningSwingWorker.cancel(true);
    }

      /**
       * Main Thread for Running Program in GUI mode
       */
      private void runthread() {

      done=false;          //reset state
      //--1. creation of the running list
       final List<workflow_properties> queue=(List<workflow_properties>) Collections.synchronizedList(new LinkedList<workflow_properties>());
       for (workflow_properties tmp:run_workflow.getList()) queue.add(tmp);
       final int totalToRun=queue.size();
       timerunning=System.currentTimeMillis();
       running=true;
       //--2. Clean Running Vector
       //--Update every 10 sec...
       armadillo.startAutoUpdate();

       RunningSwingWorker=new SwingWorker<Integer, String>()  {

       @Override
        protected Integer doInBackground() throws Exception {

           while (!isCancelled()&&!cancel&&(queue.size()>0||isRunning())) {
                    //CASE 1: Running queue object if no Running object

                   if (runningObject==null&&queue.size()>0) {
                       try {
                       //Build a search String
                        if (!cancel) runningProperties=queue.remove(0);
                        publish("Running "+runningProperties.getName()+"...\n");
                        if (Running(runningProperties)) {
                            armadillo.workbox.addOutput("Running "+runningProperties.getName()+"...\n");
                        } else {
                           if (armadillo.workflow.updateDependance()) {
                            //DO NOTHING BUT WAIT SHYNCHRONISATION...
                            };
                           //armadillo.force_redraw=true;
                           armadillo.redraw();
                        }
                       } catch(Exception e1) {Config.log("Error : Program run unable to create >Running object (1) "+e1.getMessage());}
                    }

                    //CASE 2: Running the current object...
                    if (runningObject!=null&&runningObject.isDone()) {
                            try {
                                setProgress((totalToRun-queue.size())*100/totalToRun);
                                //runningObject.setWorkflows_id(programs.armadillo.workbox.getCurrentWorkflows().getId());
                                runningObject.setWorkflows_id(programs.workflows.getId());
                                // Test June 2010
                                //runningObject.setWorkflows_id(programs.armadillo.workbox.getCurrentWorkflows().getId());
                                runningObject.setRunProgramOutput(runningObject.getOutputText());
                                //--Test to see if it work...
                                //armadillo.workbox.addOutput(runningObject.getOutputText());
                                publish("Done "+runningObject.getName()+" in "+Util.msToString(runningObject.getRunningTime()));
                                runningObject.properties.put("TimeRunning",runningObject.getRunningTime());
                                if (armadillo.workflow.updateDependance()) {
                                     //DO NOTHING BUT WAIT SHYNCHRONISATION...
                                    };
                                //armadillo.force_redraw=true;
                                armadillo.redraw();
                                setProgress((totalToRun-queue.size())*100/totalToRun);
                                if (runningObject.getId()!=0) {
                                    runningObject.update();
                                } else {
                                    runningObject.saveToDatabase();
                                }
                            } catch(Exception e2) {Config.log("Error : Program Run Module unable to save to workflow (2) "+e2.getMessage());}
                            runningObject=null;
                    }
                    //CASE 3: Normal operation, update the workflow if we just finish a step
                   if (runningObject==null) {
                        try {
                        if (armadillo.workflow.updateDependance()) {
                            //DO NOTHING BUT WAIT SHYNCHRONISATION...
                        };
                        //armadillo.force_redraw=true;
                        armadillo.redraw();
                        setProgress((totalToRun-queue.size())*100/totalToRun);
                        } catch(Exception e3) {Config.log("Error : Program run unable to normal update (3) "+e3.getMessage());}
                   }
                  //CASE 4: Workflow is done
                  if (queue.size()==0&&runningObject==null) {
                      running=false;
                  }
                  //CASE 5: We receive a  cancel call!
                   if (cancel) {
                           try {
                               queue.clear();
                               if (runningObject!=null) {
                                   runningObject.KillThread();
                                   runningObject=null;
                                    if (armadillo.workflow.updateDependance()) {
                                        //DO NOTHING BUT WAIT SHYNCHRONISATION...
                                     };
                                   //armadillo.force_redraw=true;
                                   armadillo.redraw();
                               }
                           } catch(Exception e5) {Config.log("Error : Program run unable to cancel (5) "+e5.getMessage());}
                     } //--End cancel
                   //CASE 6: Update of the workflow receive (currently not active)
                   if (update) {
                       update=false;
                       try {
                           if (runningProperties!=null&&runningProperties!=null) {
                               publish("Running "+runningProperties.getName()+" (elapsed time: "+Util.msToString(runningObject.getRunningTime())+")\n");
                               armadillo.redraw();
                           }
                       } catch(Exception e6) {Config.log("Error : Program run unable to update (6) "+e6.getMessage());}
                   }//--End update
               } //--End while
               return 0;
            }

            //On update notre Table avec les resultats partiels
            @Override
            protected void process(List<String> chunks) {
               for(String s:chunks) {
                   if (s.startsWith("Error")||s.startsWith("Cancelled")) {
                      armadillo.workbox.MessageError(s, "");
                   } else {
                      armadillo.workbox.Message(s, "");
                   }
                    
                   //--Here too much info...
                   //workflows.setWorkflows_outputText(armadillo.workbox.getOutput());
                   // June 2010
                    //armadillo.workbox.getCurrentWorkflows().setWorkflows_outputText(armadillo.workbox.getOutput());
               }
            }


           @Override
           protected void done(){
               //--Replace sequence name...
                //--TO DO...workflows.ReplaceResultsWithSequenceName();               
               
               //--Do any more work...
               if (isCancelled()||cancel) {
                   queue.clear();
                   if (runningObject!=null) {
                       runningObject.KillThread();
                   }
                        armadillo.workflow.updateDependance();
                        //Save what was done to date...
                        //work.saveWorkflowToDatabaseWOSW(work.getCurrentWorkflows().getName()+" - Cancelled at "+Util.returnCurrentDateAndTime());
                        work.saveWorkflowToDatabaseWOSW(workflows.getName()+" - Cancelled at "+Util.returnCurrentDateAndTime());
                        run_workflow.getExecution_workflow_id().add(workflows.getId());
                        //
                        armadillo.workbox.setProgress(0);
                        run_workflow.setName(workflows.getName()+" - Cancelled at "+Util.returnCurrentDateAndTime());
                        run_workflow.setCompleted(false);
                        run_workflow.saveToDatabase();
                        armadillo.workbox.addOutput("**************************************************************************************************************\n");
                        armadillo.workbox.addOutput(" Cancelled at "+Util.returnCurrentDateAndTime()+"\n");
                        armadillo.workbox.addOutput("**************************************************************************************************************\n");
                        Config.log("**************************************************************************************************************");
                        Config.log(" Cancelled at "+Util.returnCurrentDateAndTime()+"");
                        Config.log("**************************************************************************************************************");
                       runningObject=null;
                       armadillo.workflow.updateDependance();
                       running=false;
                       tool.reloadDatabaseTree();
                   publish("Cancelled at "+Util.returnCurrentDateAndTime());
                    armadillo.stopAutoUpdate();
                    armadillo.force_redraw=true;
                    armadillo.redraw();
               setDone(true);
               } else {
                   timerunning=System.currentTimeMillis()-timerunning;
                   armadillo.workbox.setProgress(100);
                   armadillo.workbox.addOutput("**************************************************************************************************************\n");
                   armadillo.workbox.Message("Computation finished in "+Util.msToString(timerunning),"");
                   armadillo.workbox.addOutput("Computation finished in "+Util.msToString(timerunning));
                   armadillo.workbox.addOutput("Ended at "+Util.returnCurrentDateAndTime()+"\n");
                   armadillo.workbox.addOutput("**************************************************************************************************************\n");
                   Config.log("**************************************************************************************************************");
                   Config.log("Computation finished in "+timerunning+" s.");
                   Config.log("Ended at "+Util.returnCurrentDateAndTime()+"");
                   Config.log("**************************************************************************************************************");
                   run_workflow.setCompleted(true);
                   run_workflow.setNote("Done "+armadillo.getName()+" in "+Util.msToString(timerunning)+"\n");
                   run_workflow.saveToDatabase();                 
                   armadillo.stopAutoUpdate();
                   armadillo.force_redraw=true;
                   armadillo.redraw();
                     tool.reloadDatabaseTree();
               setDone(true);
               }

           }

        }; //End SwingWorker declaration

        RunningSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                  if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                armadillo.workbox.setProgress(progress);
                                ////armadillo.force_redraw=true;
                                armadillo.redraw();
                            }
                     } //End populateNetworkPropertyChange
                    }
                 });
         armadillo.workbox.Message("Starting...", "");
         armadillo.workbox.setProgress(0); //Put 0% as the start progress
         try {
         RunningSwingWorker.execute();
         } catch(Exception e7) {Config.log("Error : in runthread (7) "+e7.getMessage());}
    }

     /**
      * This start a thread to run the current workflow_properties
      */

    public void Run(workflow_properties properties) {
        //--Reset properties state
        properties.removeStatus();
        if (armadillo!=null&&armadillo.isInitialized()) {
             armadillo.workflow.updateDependance();
             armadillo.redraw();
        }
        if (!isRunning()) {
            run_workflow=new RunWorkflow();
            //--Add save_point
            workflow_properties save=new workflow_properties();
            save.put("Name", "Save - Single Object Execution");
            save.put("SaveOriginal",true);
            run_workflow.getList().add(save);
            //--Add properties
            run_workflow.getList().add(properties);
            //--Add final save_point
            save=new workflow_properties();
            save.put("Name", "Execution of "+properties.getName());
            save.put("Save",true);
            run_workflow.getList().add(save);
            if (armadillo!=null&&armadillo.isInitialized()) {
                armadillo.workbox.ClearOutput();
                armadillo.workbox.addOutput("**************************************************************************************************************\n");
                armadillo.workbox.addOutput(" Armadillo v"+config.get("version")+"\n");
                armadillo.workbox.addOutput(" New Execution started \n");
                armadillo.workbox.addOutput(" -Running a single Object .\n");
                armadillo.workbox.addOutput(" -Object: "+properties.getName()+"\n");
                armadillo.workbox.addOutput(" -Started at "+Util.returnCurrentDateAndTime()+"\n");
                armadillo.workbox.addOutput("**************************************************************************************************************\n");
                Config.log("**************************************************************************************************************");
                Config.log(" Armadillo v"+config.get("version"));
                Config.log(" New Execution started ");
                Config.log(" -Running a single Object .");
                Config.log(" -Object: "+properties.getName()+"");
                Config.log(" -Started at "+Util.returnCurrentDateAndTime()+"");
                Config.log("**************************************************************************************************************");
                runthread();
            } else {


                runthread_text();
            }
        }
     }


     /**
      * This start a thread to run the current armadillo workflow
      */
    public void Run() {
        if (armadillo!=null&&armadillo.isInitialized()) {
            armadillo.workflow.updateDependance();
            armadillo.redraw();
        } else {            
            armadillo.workflow.updateDependance();            
        }
        if (!isRunning()&&isValidWorkflow()) {
           publish_text("Creating runnning workflow...");
           if (createWorkflows()) {
               execute();
           } else {
               publish_text("Error in creating running workflow - stoppping execution.");
               this.setDone(true);
               return;
           }
        } else {
            if (isRunning()) Config.log("Workflow already running: "+running);
            if (!isValidWorkflow()) Config.log("Invalid workflow... Please restart the application.");
        }
    }

    public void execute() {
         if (armadillo.isInitialized()) {
                armadillo.workbox.ClearOutput();
                armadillo.workbox.addOutput("**************************************************************************************************************\n");
                armadillo.workbox.addOutput(" Armadillo v"+config.get("version")+"\n");
                armadillo.workbox.addOutput(" New Execution started \n");
                armadillo.workbox.addOutput(" -Running "+armadillo.getName()+" workflow.\n");
                armadillo.workbox.addOutput(" -Started at "+Util.returnCurrentDateAndTime()+"\n");
                armadillo.workbox.addOutput("**************************************************************************************************************\n");
                Config.log("**************************************************************************************************************");
                Config.log(" Armadillo v"+config.get("version"));
                Config.log(" New Execution started ");
                Config.log(" -Running "+armadillo.getName()+" workflow.");
                Config.log(" -Started at "+Util.returnCurrentDateAndTime()+"\n");
                Config.log("**************************************************************************************************************");
                runthread();
        } else {
                publish_text("**************************************************************************************************************");
                publish_text(" Armadillo v"+config.get("version"));
                publish_text(" New Execution started ");
                publish_text(" -Running "+armadillo.getName()+" workflow.");
                publish_text(" -Started at "+Util.returnCurrentDateAndTime()+"\n");
                publish_text("**************************************************************************************************************");
               // publish_text("Feature in Beta testing. Please run in graphic mode.");
                //publish_text("**************************************************************************************************************");
                Config.log("**************************************************************************************************************");
                Config.log(" Armadillo v1.1 ");
                Config.log(" New Execution started ");
                Config.log(" -Running "+armadillo.getName()+" workflow.");
                Config.log(" -Started at "+Util.returnCurrentDateAndTime()+"\n");
                Config.log("**************************************************************************************************************");
               // Config.log("Feature in Beta testing. Please run in graphic mode.");
                //Config.log("**************************************************************************************************************");
                runthread_text();
        }

    }

    /**
     * Main procedure to create workflow EXECUTION LIST
     * Not. This is a FIFO file.
     */
    public boolean createWorkflows() {
            ////////////////////////////////////////////////////////////////////
            /// Variables
            boolean foundFor=false;
            boolean noSave=false;           
            workflow_properties save=new workflow_properties();
           //--Create a new list
           run_workflow=new RunWorkflow();
         
           //--Remove any programs output before we start...
           //--Note: should not be done for program already done...
           //--A test is made in this function to be certains...
           armadillo.workflow.resetProgramOutput();

           //--Any undefined object
          Vector<workflow_object> undefined=armadillo.workflow.findAllUndefinedObjects();
         if (config.isDevelopperMode()) publish_text("Finding undefined object...");
          int count_loaded=0;
          if (undefined.size()>0) {
              String undefineds="";
              int count=1;
              for (workflow_object obj:undefined) {                  
                  int id=loadObject(obj, count);
                  //--Have children? Dependance?
                  //--If true, we add to the list and the dependance
                  Vector<workflow_object>children=armadillo.workflow.findOutput(obj);                  
                  if (children.size()>0) {
                    undefineds+="["+count+"] "+obj.getProperties().get("outputType")+" required for:\n";
                    for (workflow_object objch:children) {
                        undefineds+="\t->"+objch.getProperties().getName()+"\n";
                        
                    }
                  }
                  if (id>0) {
                      //--Set the id to the good object
                      obj.getProperties().put("output_"+obj.getProperties().get("outputType").toLowerCase()+"_id",id);
                      undefineds+="\t[Ok - loaded from "+this.returnArg(Config.cmdArgs, count)+"\n";
                      count_loaded++;
                  }
                  count++;
              }
              //--Did we load all objects from files?
              if (count_loaded!=undefined.size()) {
                  //--No...
                  if (armadillo.isInitialized()) {
                      JOptionPane.showMessageDialog(this.frame,"There are some undefined object in workflow!\n\n"+undefineds+"\n Please select a dataset before running the workflow.\n\n(Right-click on the undefined object in the workflow\nand choose: \"Select Dataset\" from the menu.)","Warning!",JOptionPane.ERROR_MESSAGE);
                      publish_text("Error : There are some undefined object in workflow!\n\n"+undefineds+"\n Please select a dataset before running the workflow.\n\n(Right-click on the undefined object in the workflow\n and choose: \"Select Dataset\" from the menu while you are in graphic mode\nor execute Armadillo with some files as input)");
                      return false;
                  } else {
                      publish_text("Error : There are some undefined object in workflow!\n\n"+undefineds+"\n Please select a dataset before running the workflow.\n\n(Right-click on the undefined object in the workflow\n and choose: \"Select Dataset\" from the menu while you are in graphic mode\nor execute Armadillo with some files as input)");
                      return false;
                  }
              }
          }

          if (config.isDevelopperMode()) publish_text("Finding no save object...");
          //--Find no save object
          for (workflow_object obj:armadillo.workflow.work) {
            if (isNoSave(obj)) {
                noSave=true;
            }
          }

          if (config.isDevelopperMode()) publish_text("Finding For each and Repeat...");
           //--Find For-Loop-Repeat
           for (workflow_object obj:armadillo.workflow.work) {
                if (armadillo.workflow.isFor(obj)) {
                    foundFor=true;
                    //--Ok For object...
                    //--Generate a list of the For properties...
                    Vector<workflow_properties>list=generateForProperties(obj.getProperties());
                    //--Clear the current For list and add the new List
                    this.run_workflow.getFor_list().clear();
                    for (workflow_properties prop:list) {
                        this.run_workflow.getFor_list().add(prop);
                    }
                }
           }

           //--Save before the run...
            // CASE 1. For Found.. create first iteration
           if (foundFor) {
                save.put("Name", workflows.getName()+" - Save before execution");
                save.put("SaveAndResetOriginal",true);
                run_workflow.getList().add(save);
           } else {
            //CASE 2. Normal save
                save.put("Name", workflows.getName()+" - Save before execution");
                save.put("SaveOriginal",true);
                run_workflow.getList().add(save);
           }

           //Config.log("Total workflows to run"+run_workflow.getFor_list().size());
           int total=(run_workflow.getFor_list().size()>0?run_workflow.getFor_list().size():1);

           //--Loop to create workflow (we repeat the same workflow but update it at different time point
           //--This is necessary to keep the visual (object in the workflow) in sync with
           //--TO DO: Better run model here... compilation like.
           //--The current properties
           for (int i=0;i<total;i++) {
               for (workflow_object obj:armadillo.workflow.outputExecution()) {
                   if (obj!=null) {
                       workflow_properties properties=obj.getProperties();
                       run_workflow.getList().add(properties);
                   }
               }
               //--Add save point after each run...
               //--This is the final save
               if (i==(total-1)) {
                    save=new workflow_properties();
                    save.put("Name", "Execution "+(i+1)+" of "+total);
                    save.put("Save",true);
                    run_workflow.getList().add(save);
               } else {
               //--This is an internal save.
                    if (noSave) {
                        save=new workflow_properties();
                        save.put("Name", "Execution "+(i+1)+" of "+total);
                        save.put("Reset",true);
                        run_workflow.getList().add(save);
                    } else {
                        save=new workflow_properties();
                        save.put("Name", "Execution "+(i+1)+" of "+total);
                        save.put("SaveAndReset",true);
                        run_workflow.getList().add(save);
                    }
               }
           }
           return true;
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Function to load object


    public int loadObject(workflow_object obj, int count) {
        Input in=new Input();
        in.setType(obj.getProperties().get("outputType"));
        Object bio=in.getBiologic();
        //--Load the object and save to db
        String filename=this.returnArg(Config.cmdArgs, count);
        if (filename.isEmpty()) return -1;
        ((Biologic)bio).loadFromFile(filename);
        ((Biologic)bio).saveToDatabase();
        int id=((Biologic)bio).getId();
        if (id==0) return -1;
        return id;
    }



    ////////////////////////////////////////////////////////////////////////////
    /// Functions to ID object

//     private boolean isFor(workflow_object obj) {
//     if (obj instanceof armadillo_workflow.workflow_object_output_database) {
//             workflow_properties prop=obj.getProperties();
//             for (Object k:prop.keySet()) {
//                if (((String)k).startsWith("For_")) return true;
//             }
//         }
//      if (obj.getProperties().isSet("ForObjectID")) {
//         workflow_properties prop=obj.getProperties();
//          for (Object k:prop.keySet()) {
//                if (((String)k).startsWith("For_")) return true;
//         }
//      }
//      return false;
//  }

/**
 *
 * @param obj
 * @return
 */
  private boolean isNoSave(workflow_object obj) {
      return obj.getProperties().getBoolean("NoSave");
  }



    /**
     * This will return a list of properties generated for the specified workflow_properties
     * identified as a For each loop
     * Note: will also
     * @param properties
     * @return
     */
     public Vector<workflow_properties> generateForProperties(workflow_properties properties) {
         Pattern key_value=Pattern.compile("For_(.*)", Pattern.CASE_INSENSITIVE);
         Vector<workflow_properties>tmp=new Vector<workflow_properties>();
         workflow_properties w=new workflow_properties();
         String ObjectID=properties.getID();
         //--Handle program for loop
         if (properties.isSet("ForObjectID")) {
             ObjectID=properties.get("ForObjectID");
             if (!ObjectID.equals(properties.getID())) ObjectID=properties.getID();
         }

         //--Duplicate list and add new properties
         //--TO DO: more compiler like behavior
         if (run_workflow.getFor_list().size()==0) {
              for (Object k:properties.keySet()) {
                 Matcher m=key_value.matcher((String)k);
                 if (m.find()) {
                    String key=properties.get(k);
                    String value=m.group(1);
                    w=new workflow_properties();
                    //--Put 1. ObjectID for this For-Loop
                    //--Put 2. Variable->value
                    w.put(ObjectID, key+"&"+value);
                    //w.put(key, value);
                    tmp.add(w);
                 }
             }
         } else {
             for (workflow_properties oldprop:run_workflow.getFor_list()) {
                for (Object k:properties.keySet()) {
                 Matcher m=key_value.matcher((String)k);
                 if (m.find()) {
                    String key=properties.get(k);
                    String value=m.group(1);
                    w=new workflow_properties();
                    //Duplicate prop
                    w.deserializeFromString(oldprop.serializeToString());
                    //--Put 1. ObjectID for this For-Loop
                    //--Put 2. Variable->value
                    w.put(ObjectID, key+"&"+value);
                    //w.put(key, value);
                    tmp.add(w);
                  }
                }
             }
         }
         return tmp;
     }


    boolean isValidWorkflow() {
        return (armadillo.workflow.outputExecution()==null?false:true);
    }

    /**
     * Warning, should only be called by the SwingWorker!
     * @param properties
     */
    private boolean Running(workflow_properties properties) {
        //Util.CleanMemory();
        //--CASE 0. Not running? we exit.
        if (cancel) return false;

        //--CASE 1.0. Special object?    
        //--Handle each of the possible case here
        //--Initial Save and Reset Workflow
        if (properties.isSet("SaveAndReset")) {
             work.saveWorkflowToDatabaseWOSW(properties.getName());
             this.run_workflow.getExecution_workflow_id().add(workflows.getId());
             this.resetWorkflow();
             return false;
         }
         //--Only reset the Workflow, no save
         //--This will reset the rest of the workflow
         if (properties.isSet("Reset")) {
             this.run_workflow.setName(properties.getName());
             this.resetWorkflow();
             return false;
         }

        //--This is a special car to set the Original workflow for the RunWorkflow object
        if (properties.isSet("SaveAndResetOriginal")) {
             //1, Save workflow
             work.saveWorkflowToDatabaseWOSW(properties.getName());
             this.run_workflow.setOriginal_workflow_id(workflows.getId());
             this.run_workflow.setName(workflows.getName());             
             this.resetWorkflow();
             return false;
         }

         if (properties.isSet("Save")) {
             work.saveWorkflowToDatabaseWOSW(properties.getName()+ " at "+Util.returnCurrentDateAndTime());
             this.run_workflow.getExecution_workflow_id().add(workflows.getId());
             runningObject=null;
             return false;
         }

          if (properties.isSet("SaveOriginal")) {
             work.saveWorkflowToDatabaseWOSW(properties.getName()+ " at "+Util.returnCurrentDateAndTime());
             this.run_workflow.setOriginal_workflow_id(workflows.getId());
             this.run_workflow.setName(workflows.getName());
             runningObject=null;
             return false;
         }

         //--Do not run if already done or we are not executing (in case of IF, partial execution...)
        int status=properties.getInt("Status");
        if (status==RunProgram.status_done) {            
            Config.log("Already done.");
            runningObject=null;
            return false;
        }
        //--Flag if we are not reexecuting this par tof the program
        if (properties.getBoolean("DoNotExecute")) {            
            //--Remove the flag before we save
            properties.remove("DoNotExecute");
            runningObject=null;
            return false;
        }
          //--If Object
        if (properties.get("ObjectType").equals("If")) {
            //--If obj
            workflow_object if_obj=getObjectFromProperties(properties);
           //--Verify condition here           
            Running_If(properties);            
            //suppose its false -> We don't execute the true
            if (properties.isSet("IfStatus")) {
                this.setIfForWorkflow(armadillo.workflow.getIfExecution(if_obj, !properties.getBoolean("IfStatus")));
            } else {
                //--Otherwise, we don't execute the dependance
                this.setIfForWorkflow(armadillo.workflow.getIfExecution(if_obj, true));
                this.setIfForWorkflow(armadillo.workflow.getIfExecution(if_obj, false));
            }
            return true;
        }
        //--Otherwise, start initialization
        if (properties.isSet("ClassName")) {
             if (properties.get("ObjectType").equals("Program")) {
            // CASE 2. We have a new editor set and its valid -> We try to run it
                if (dict.isValidValue("ClassName",properties.get("ClassName"))) {
                properties.put("Status", RunProgram.status_idle);
                //--This will clear the programs output only if its not a dummy program (like an input box)
                if (!properties.get("ClassName").equals("programs.dummy")) {                    
                    properties.removeOutput();
                }
                armadillo.workflow.updateDependance();
                //armadillo.force_redraw=true;
                armadillo.redraw();
                try {
                    Classdata classdata=new Classdata(properties.get("ClassName"));
                    // CASE 2.1 -> Error, we are unable to initialize it!
                         if (classdata==null) {
                            properties.remove("Status");
                            Config.log("Unable to initialize class (+"+properties.get("ClassName")+") for "+properties.getName()+"");
                            return false;
                          }
                          // Call the first constructor! This MUST be unique!
                          // AND in the form public foo(java.awt.Frame frame)...
                          //--Create a new RunProgramId
                          df.addProperties(properties);
                          int id=properties.getProperties_id();
                          //--Ugly!
                           //--Clear program output
                          //--This circle through available contructor
                          for (Constructor C:classdata.constructor) {
                               RunProgram run=null;
                               try {
                                   
                                  //--WE need to used this method... 
//                                   ClassLoader loader = URLClassLoader.newInstance(
//                                    new URL[] { yourURL },
//                                    getClass().getClassLoader()
//                                
//                                Class<?> clazz = Class.forName("mypackage.MyClass", true, loader);
//                                Class<? extends Runnable> runClass = clazz.asSubclass(Runnable.class);
//                                // Avoid Class.newInstance, for it is evil.
//                                Constructor<? extends Runnable> ctor = runClass.getConstructor();
//                                Runnable doRun = ctor.newInstance();
//                                doRun.run();
                                   
                                   run=(RunProgram)C.newInstance(properties);
                                   if (run!=null) {
                                       //properties.removeOutput();
                                       runningObject=run;
                                       return true;
                                    }
                               } catch(Exception e) {}
                          }
                   //CASE 2.2 ->Big Error -> Run Default..
                } catch(Exception e) {e.printStackTrace();}
                } else {
                    properties.remove("Status");
                    properties.setStatus(RunProgram.status_runningclassnotfound, "Running class not found.");                    
                    Config.log("Unable to find running class (+"+properties.get("ClassName")+") for "+properties.getName()+"");
                    Config.log("The config.dat is probably corrupted. Please delete it and restart Armadillo. ");
                    return false;
                }
            } else {
                //--Not a programm
                 properties.remove("Status");
                return false;
            }
          } else {
                properties.remove("Status");
                properties.setStatus(RunProgram.status_error, "No Class is set to run...");
                return false;
          }
        return true;
    }

     /**
     * Warning, should only be called in text mode!
      * @param properties
     */
    private boolean Running_text(workflow_properties properties) {
        //Util.CleanMemory();
        if (cancel) return false;        
        //--Handle each of the possible case here
        //--Initial Save and Reset Workflow

        if (properties.isSet("SaveAndReset")) {
             Config.log("Saving workflow successfull->"+saveWorkflowToDatabase(properties.getName()));
             this.run_workflow.getExecution_workflow_id().add(workflows.getId());             
             this.resetWorkflow();
             return false;
         }
         //--Only reset the Workflow, no save
         //--This will reset the rest of the workflow
         if (properties.isSet("Reset")) {
             this.run_workflow.setName(properties.getName());
             this.resetWorkflow();
             return false;
         }

        //--This is a special car to set the Original workflow for the RunWorkflow object
        if (properties.isSet("SaveAndResetOriginal")) {
             //1, Save workflow
             Config.log("Saving workflow successfull->"+saveWorkflowToDatabase(properties.getName()));
             this.run_workflow.setOriginal_workflow_id(workflows.getId());
             this.run_workflow.setName(workflows.getName());             
             this.resetWorkflow();
             return false;
         }

         if (properties.isSet("Save")) {
             Config.log("Saving workflow successfull->"+saveWorkflowToDatabase(properties.getName()+ " at "+Util.returnCurrentDateAndTime()));
             this.run_workflow.getExecution_workflow_id().add(workflows.getId());             
             runningObject=null;
             return false;
         }

          if (properties.isSet("SaveOriginal")) {
             Config.log("Saving workflow successfull->"+saveWorkflowToDatabase(properties.getName()+ " at "+Util.returnCurrentDateAndTime()));
             this.run_workflow.setOriginal_workflow_id(workflows.getId());
             this.run_workflow.setName(workflows.getName());           
             runningObject=null;
             return false;
         }

         //--Do not run if already done
        int status=properties.getInt("Status");
        if (status==RunProgram.status_done) {
            Config.log("Already done");
            runningObject=null;
            return false;
        }
         if (properties.getBoolean("DoNotExecute")) {
            properties.put("Status",RunProgram.status_done);
            properties.put("StatusString", "No executing.");
            Config.log("Not Executing (If)");
            runningObject=null;
            return false;
        }
        //--If Object
        if (properties.get("ObjectType").equals("If")) {
            //--If obj
            workflow_object if_obj=getObjectFromProperties(properties);
           //--Verify condition here
            Running_If(properties);
            //suppose its false -> We don't execute the true
            if (properties.isSet("IfStatus")) {
                this.setIfForWorkflow(armadillo.workflow.getIfExecution(if_obj, !properties.getBoolean("IfStatus")));
            } else {
                //--Otherwise, we don't execute the dependance
                this.setIfForWorkflow(armadillo.workflow.getIfExecution(if_obj, true));
                this.setIfForWorkflow(armadillo.workflow.getIfExecution(if_obj, false));
            }
            return true;
        }
        //--Otherwise, start initialization
        if (properties.isSet("ClassName")) {
             if (properties.get("ObjectType").equals("Program")) {
            // CASE 2. We have a new editor set and its valid -> We try to run it
                if (dict.isValidValue("ClassName",properties.get("ClassName"))) {
                properties.put("Status", RunProgram.status_idle);
                //--This will clear the programs output only if its not a dummy program (like an input box)
                if (!properties.get("ClassName").equals("programs.dummy")) {                   
                    properties.removeOutput();
                }
                armadillo.workflow.updateDependance();
                //armadillo.force_redraw=true;               
                try {
                    Classdata classdata=new Classdata(properties.get("ClassName"));
                    // CASE 2.1 -> Error, we are unable to initialize it!
                         if (classdata==null) {
                            properties.remove("Status");
                            Config.log("Unable to initialize class (+"+properties.get("ClassName")+") for "+properties.getName()+"");
                            return false;
                          }
                          // Call the first constructor! This MUST be unique!
                          // AND in the form public foo(java.awt.Frame frame)...
                          //--Create a new RunProgramId
                          df.addProperties(properties);
                          int id=properties.getProperties_id();
                          //--Ugly!
                           //--Clear program output
                          //--This circle through available contructor
                          for (Constructor C:classdata.constructor) {
                               RunProgram run=null;
                               try {
                                   run=(RunProgram)C.newInstance(properties);
                                   if (run!=null) {
                                       //properties.removeOutput();
                                       runningObject=run;
                                       return true;
                                    }
                               } catch(Exception e) {}
                          }
                   //CASE 2.2 ->Big Error -> Run Default..
                } catch(Exception e) {e.printStackTrace();}
                } else {
                    properties.remove("Status");
                    properties.setStatus(RunProgram.status_error, "Running class not found.");
                    return false;
                }
            } else {
                //--Not a programm
                 properties.remove("Status");
                return false;
            }
          } else {
                properties.remove("Status");
                properties.setStatus(RunProgram.status_error, "No Class is set to run...");
                return false;
          }
        return true;
    }

    public boolean Running_If(workflow_properties properties) {        
        if (properties.isSet("ClassName")) {
            // CASE 2. We have a new editor set and its valid -> We try to run it
                if (dict.isValidValue("ClassName",properties.get("ClassName"))) {
                properties.put("Status", RunProgram.status_idle);
                //--This will clear the programs output only if its not a dummy program (like an input box)                          
                try {
                    Classdata classdata=new Classdata(properties.get("ClassName"));
                    // CASE 2.1 -> Error, we are unable to initialize it!
                         if (classdata==null) {
                            properties.remove("Status");
                            Config.log("Unable to initialize class for If (+"+properties.get("ClassName")+") for "+properties.getName()+"");
                            return false;
                          }                          
                          for (Constructor C:classdata.constructor) {
                               RunProgram run=null;
                               try {
                                   run=(RunProgram)C.newInstance(properties);
                                   if (run!=null) {
                                       //--Wait for the execution to finish or status finish.
                                       while(!run.isDone()&&!(properties.getStatus()==RunProgram.status_done||properties.getStatus()==RunProgram.status_error)) {}
                                    }
                               } catch(Exception e) {}
                          }
                   //CASE 2.2 ->Big Error -> Run Default..
                } catch(Exception e) {e.printStackTrace();}
                } else {
                    properties.remove("Status");
                    properties.setStatus(RunProgram.status_error, "Running class for If object not found.");
                    return false;
                }
          } else {
                properties.remove("Status");
                properties.setStatus(RunProgram.status_error, "No Class is set to run for If object...");
                return false;
          }
          //--Everything was ok, good... we allow the output
         return true;
    }

    /**
     * This is the procedure call to reset the workflow state
     * i.e. the state of the object in the workflow to allow
     * a for-loop
     */
    public void resetWorkflow() {
         workflow_properties toChange=run_workflow.getFor_list().poll();
             // 4. Change the rest of the queue...
             for (workflow_properties prop:this.run_workflow.getList()) {
                 prop.remove("IfStatus");
                 prop.remove("Status");
                 prop.remove("StatusString");
                 prop.remove("DoNotExecute"); //--If
                 prop.remove("Running_CommandLine");
                 //--Clean-UP : Remove For condition
                 prop.remove("ForObjectID");
                 Vector<String>keys=new Vector<String>();
                 for (Object k2:prop.keySet()) keys.add((String)k2);
                 for(String key2:keys) {
                     if (key2.startsWith("For_")) prop.remove(key2);
                 }
                 if (toChange!=null) {
                     for (Object k:toChange.keySet()) {
                        if (((String)k).equals(prop.getID())) {
                            String keystring=toChange.get(k);
                            int index=keystring.indexOf("&");
                            String key=keystring.substring(0,index);
                            String value=keystring.substring(index+1);
                            //--debug Config.log(key+" "+value);
                            //--Add new key
                            prop.put(key, value);
                        }
                    }
                } //toChange not null
             }
             runningObject=null;
         }

    /**
     * This is the procedure call to set the DoNotExecute flag on some properties
     * i.e. the state of the object in the workflow to allow
     * a false If
     */
    public void setIfForWorkflow(LinkedList<workflow_properties> list_toDoNotExecute) {
             // 1. Add "do not execute flag"
             for (workflow_properties prop:this.run_workflow.getList()) {                 
                 if (list_toDoNotExecute.contains(prop)) {                    
                     prop.put("DoNotExecute", true);
                 }
             }           
         }

    /**
     * Thread to run program in text mode...
     * @param list
     */
    public void runthread_text(){
        if (running) return; //safety to prevent deadlock

        ////////////////////////////////////////////////////////////////////////
        /// Initialisation

        final String started="Started at "+Util.returnCurrentDateAndTime();
        publish_text("Running in Normal text mode...");       
        publish_text(started);

       //--List to run
       final List<workflow_properties> queue=(List<workflow_properties>) Collections.synchronizedList(new LinkedList<workflow_properties>());
       //--List and add queue       
       for (workflow_properties tmp:run_workflow.getList()) {
       //  publish_text("["+tmp.getInt("Order")+"]"+tmp.getName());
           queue.add(tmp);
       }
       final int totalToRun=queue.size();
       timerunning=System.currentTimeMillis();
       running=true;     
       

       timerunning=System.currentTimeMillis();
       running=true;

       thread=new Thread(){

            @Override
             public void run() {
                    while (!cancel&&(queue.size()>0||isRunning())) {
                    //CASE 1: Running queue object if no Running object

                   if (runningObject==null&&queue.size()>0) {
                       try {
                           //Build a search String
                            if (!cancel) runningProperties=queue.remove(0);
                            if (runningProperties==null) Config.log("Error : runningProperties is null.");
                            if (Running_text(runningProperties)) {
                                publish_text("Running "+runningProperties.getName()+"...");
                            } else {
                                //System.out.println("Unable to initialize running object: "+runningProperties.getName());
                                armadillo.workflow.updateDependance();
                            }
                       } catch(Exception e1) {Config.log("Error : Program run unable to create >Running object (1) "+e1.getMessage());}
                    }

                    //CASE 2: Running the current object...
                    if (runningObject!=null&&runningObject.isDone()) {
                            try {
                                //publish_text("Progress ["+(totalToRun-queue.size())*100/totalToRun+"%]");
                                //runningObject.setWorkflows_id(programs.armadillo.workbox.getCurrentWorkflows().getId());
                                runningObject.setWorkflows_id(programs.workflows.getId());
                                // Test June 2010
                                //runningObject.setWorkflows_id(programs.armadillo.workbox.getCurrentWorkflows().getId());
                                runningObject.setRunProgramOutput(runningObject.getOutputText());
                                //--Test to see if it work...
                                //armadillo.workbox.addOutput(runningObject.getOutputText());
                                publish_text("[done] "+runningObject.getName()+" in "+Util.msToString(runningObject.getRunningTime()));
                                runningObject.properties.put("TimeRunning",runningObject.getRunningTime());
                                armadillo.workflow.updateDependance();
                                //armadillo.force_redraw=true;
                                //armadillo.redraw();
                                publish_text("Progress ["+(totalToRun-queue.size())*100/totalToRun+"%]");
                                if (runningObject.getId()!=0) {
                                    runningObject.update();
                                } else {
                                    runningObject.saveToDatabase();
                                }
                            } catch(Exception e2) {Config.log("Error : Program Run Module unable to save to workflow (2) "+e2.getMessage());}
                            runningObject=null;
                    }
                    //CASE 3: Normal operation, update the workflow if we just finish a step
                   if (runningObject==null) {
                        try {
                        armadillo.workflow.updateDependance();
                        //publish_text("Progress ["+(totalToRun-queue.size())*100/totalToRun+"%]");
                        } catch(Exception e3) {Config.log("Error : Program run unable to normal update (3) "+e3.getMessage());}
                   }
                  //CASE 4: Workflow is done
                  if (queue.size()==0&&runningObject==null) {
                      running=false;
                  }
                  //CASE 5: We receive a  cancel call!
                   if (cancel) {
                           try {
                               queue.clear();
                               if (runningObject!=null) {
                                   runningObject.KillThread();
                                   runningObject=null;
                                   armadillo.workflow.updateDependance();
                               }
                           } catch(Exception e5) {Config.log("Error : Program run unable to cancel (5) "+e5.getMessage());}
                     } //--End cancel
                   //CASE 6: Update of the workflow receive (currently not active)
                   if (update) {
                       update=false;
                       try {
                           if (runningProperties!=null&&runningProperties!=null) {
                               publish_text("Running "+runningProperties.getName()+" (elapsed time: "+Util.msToString(runningObject.getRunningTime())+")\n");
                           }
                       } catch(Exception e6) {Config.log("Error : Program run unable to update (6) "+e6.getMessage());}
                   }//--End update
               } //--End while                 
                 timerunning=System.currentTimeMillis()-timerunning;
                   
                   publish_text("**************************************************************************************************************");
                   publish_text("Computation finished in "+Util.msToString(timerunning));                   
                   publish_text(started);
                   publish_text("Ended   at "+Util.returnCurrentDateAndTime());
                   publish_text("**************************************************************************************************************");                   
                   run_workflow.setCompleted(true);
                   run_workflow.setNote("Done "+armadillo.getName()+" in "+Util.msToString(timerunning)+"\n");
                   run_workflow.saveToDatabase();
                   setDone(true);
                return;
            }



//                   while (isRunning()) {
//                         if (runningObject==null&&queue.size()>0) {
//                             // CASE 1. Do we have something to do
//                              try {
//                           //Build a search String
//                            if (!cancel) runningProperties=queue.poll();
//                                  Config.log("Running "+runningProperties.getName()+"...\n");
//                            if (Running(runningProperties)) {
//                                armadillo.workbox.addOutput("Running "+runningProperties.getName()+"...\n");
//                            } else {
//                               armadillo.workflow.updateDependance();
//                               //armadillo.force_redraw=true;
//                               armadillo.redraw();
//                            }
//                       } catch(Exception e1) {Config.log("Error : Program run unable to create >Running object (1) "+e1.getMessage());}
//
//                                runningProperties=queue.poll();
//                                 Running(runningProperties);
//                                 if (runningObject==null&&armadillo!=null) armadillo.workflow.updateDependance();
//                         } else {                             // CASE 2. Finish current object?
//                             if (runningObject instanceof RunProgram) {
//                                if (((runningThreadInterface)runningObject).isDone()) {
//                                    RunProgram run=(RunProgram)runningObject;
//                                    long ttimerunning=run.getRunningTime();
//                                    String tname=run.getName();
//                                    String msg="Done "+tname+" in "+ttimerunning+" ms.";
//                                    Vector<String> msgt=((runningThreadInterface)runningObject).getOutputTXT();
//                                    StringBuilder strb=new StringBuilder();
//                                    for (String s:msgt) strb.append(s+"\n");
//                                    strb.append(msg+"\n");
//                                    run.setProgramTimeEnd(Util.returnCurrentDateAndTime());
//                                    run.setRunProgramOutput(strb.toString());
//                                    run.update();
//                                    Config.log(msg);
//                                    //Print the program output...
//                                    //for (String s:msg) Config.log(s);
//                                    if (armadillo!=null) armadillo.workflow.updateDependance();
//                                    runningObject=null;
//                                } else if (((runningThreadInterface)runningObject).getStatus()==Config.status_error) {
//                                    RunProgram run=(RunProgram)runningObject;
//                                    boolean tdone=((runningThreadInterface)runningObject).isDone();
//                                    long ttimerunning=((runningThreadInterface)runningObject).getRunningTime();
//                                    int ExitVal=((runningThreadInterface)runningObject).getExitVal();
//                                    String tname=((runningThreadInterface)runningObject).getName();
//                                    Vector<String>msgt=((runningThreadInterface)runningObject).getOutputTXT();
//                                    String msg="***Error with "+tname;
//                                    StringBuilder strb=new StringBuilder();
//                                    for (String s:msgt) strb.append(s+"\n");
//                                    strb.append(msg+"\n");
//                                    run.setProgramTimeEnd(Util.returnCurrentDateAndTime());
//                                    run.setRunProgramOutput(strb.toString());
//                                    run.update();
//                                    Config.log(msg);
//                                    if (armadillo!=null) armadillo.workflow.updateDependance();
//                                    runningObject=null;
//                                }
//                                } //End
//                            }
//                             // CASE 3. Finished queue?
//                             if (queue.size()==0&&runningObject==null) running=false;
//                      }//--End while running
//                     done=true;
//                     timerunning=System.currentTimeMillis()-timerunning;
//                     Config.log("Done in "+timerunning+" ms");
//                     Config.log("Ended at "+Util.returnCurrentDateAndTime());
//                }
             };
        thread.start();
  }

    ////////////////////////////////////////////////////////////////////////////
    /// Helper

    private workflow_object getObjectFromProperties(workflow_properties prop) {
        for (workflow_object obj:armadillo.workflow.work) {
            if (obj.getProperties().equals(prop)) return obj;
        }
        return null;
    }


    ////////////////////////////////////////////////////////////////////////////
    /// Other functions

    /**
     * @return the running
     */
    public boolean isRunning() {
        synchronized(this) {
            return running;
        }
    }

    /**
     * @return the done
     */
    public boolean isDone() {
        synchronized(this) {        
            return done;
        }
    }

    public void setDone(boolean b) {
        synchronized(this) {
            this.done=b;
        }
    }

    public void publish_text(String str) {
        System.out.println(str);
        Config.log(str);
    }
    
    public long getRunningTime() {
        return System.currentTimeMillis()-this.timerunning;
    }

    public workflow_properties getRunningProperties() {
        return runningProperties;
    }

    public void actionPerformed(ActionEvent e) {
    //If still loading, can't animate.
    //           update=true; //--Flag to display update
    }

    /**
     * Save the current workflow to the database (Use in text mode)
     * @param WorkflowName
     * @return true if saved, false otherwise
     */
    public boolean saveWorkflowToDatabase(String WorkflowName) {
        workflows.setName(WorkflowName);
        publish_text("Saving workflow "+WorkflowName+" to database...");
         workflows.setId(0); //--Reset ID for a new workflow
         boolean saved=workflows.saveToDatabase();
         if (saved) {
              publish_text("Successfully saved workflow to database ("+Util.returnCurrentDateAndTime()+")");
         }
         return saved;
     }

    /**
     * Return the non empty String at the argsNumber
     * Note: number are from [1..to infinity]
     * @param str
     * @param argsNumber
     * @return the string arg or empty if not found
     */
    public String returnArg(String[] str, int argsNumber) {
        int count=0;
        for (String stri:str) {
            if (!stri.isEmpty()) {
                count++;
                if (count==argsNumber) return stri;
            }
        }
        return "";
    }
//    public void startUpdate() {
//        updateThread=new Thread() {
//            @Override
//            public void run() {
//                update=true;
//                try {
//                    wait(1000);
//                } catch(Exception e) {}
//            }
//        };
//        updateThread.run();
//    }


}//--End program
