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

package workflows;

import biologic.RunWorkflow;
import biologic.seqclasses.InformationJDialog;
import biologic.Workflows;
import configuration.Config;
import configuration.Util;
import database.Project;
import database.databaseFunction;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import tools.Toolbox;

/**
 * This is a container to the WorkflowJInternalFrame to ensure there is only one
 * Note: This is the current operating workflow
 * @author Etienne Lord
 * @since 2009
 */
public class Workbox {

   /////////////////////////////////////////////////////////////////////////////
   /// Variables

    private static WorkFlowJInternalFrame workbox=null;
    public static Toolbox toolbox;
    public static Frame frame;
    public static InformationJDialog loading;
    public static InformationJDialog loading2; //Avoid deadlock...
    public static Project project;
    public static Config config=new Config();
    public static armadillo_workflow work=null;
    //=new armadillo_workflow();--Debug
    
    //--This ultimately hold the reference to the current armadillo_workflow...
    public static Workflows database_workflow=null;
    
    ////////////////////////////////////////////////////////////////////////////
    /// Constructor

    /**
     * Default constructor for mainframe
     * @param frame
     */
    public Workbox(Frame frame, String WorkflowName, Project project) {
        Workbox.frame=frame;
        if (project!=null) {
            Workbox.project=project;
        } else {
            Config.log("Workbox->Project is null?");
            // Debug
            //            Project o=new Project();
            //            while(o.hasNext()) {
            //                Project p=(Project)o.next();
            //                Config.log(p.toString());
            //            }
        }
        if (workbox==null) {
            try {
                toolbox=new Toolbox();
            } catch(Exception e) {Config.log("Unable to initialize Workbox->toolbox");}
            try {
                //--Creation of the main armadillo_workflow 0bject
                work=new armadillo_workflow();
                database_workflow=new Workflows(work);
                workbox=new WorkFlowJInternalFrame(frame, database_workflow);
            } catch(Exception e) {
               Config.log("Unable to initialize Workbox->workbox");
            }
        }        
        this.getCurrentArmadilloWorkflow().setName(WorkflowName);
    }

    /**
     * Default constructor for other
     */
    public Workbox(){};

    /**
     * Default constructor when in text mode;
     * @param workflow
     */
    public Workbox(Workflows workflow) {            
            if (project==null) {
                Workbox.project=project;
            }
            workbox=new WorkFlowJInternalFrame(frame, workflow);           
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Functions

    /**
     * Function to create a New Empty workflow
     * @return true if successfull, false otherwise
     */
    public boolean newWorkflow() {
        //--Save workflow before?
        if (getCurrentWorkflows().isChanged()) {
        String msg="<html>Warning, workflow not saved.<br><br>Do you want to save the workflow before creating another?</html>";
                Object[] options = {"Yes","No","Cancel"};
                int n = JOptionPane.showOptionDialog(frame,msg,"Save workflow...",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options, options[2]);
                switch(n) {
                    case 0:  saveWorkflowToDatabase("Save on "+Util.returnCurrentDateAndTime());break;
                    case 1:   break;
                    case 2:   return false;
                }
        }
         database_workflow.setId(0);
         database_workflow.workflow.workflow.selectAll();
         database_workflow.workflow.workflow.deleteSelected();
         getCurrentArmadilloWorkflow().setName("");
         workbox.setWorkflowName("","");
         database_workflow.setWorkflows_outputText("");
         workbox.setOutput("");
         database_workflow.setName("New Workflow - "+Util.returnCurrentDateAndTime());
         database_workflow.setDate_created(Util.returnCurrentDateAndTime());
         database_workflow.setNote("Created on "+Util.returnCurrentDateAndTime());
        return true;
    }

    /**
     * Getter for the JInternalFrame
     * @return
     */
    public JInternalFrame getJInternalFrame() {
        return workbox;
    }

    /**
     * Return the currently displayed project
     * @return
     */
    public Project getProject() {
        return project;
    }

    /**
     *
     * @return The Armadillo_workflow
     */
    public armadillo_workflow getCurrentArmadilloWorkflow() {
        return work;
    }
    

    public Workflows getCurrentWorkflows() {
        return database_workflow;
    }

    public RunWorkflow getCurrentRunWorkflow() {
          return workbox.runworkflow;
    }

    /**
     *
     * @return
     */
    public RunWorkflow getRunWorkflowForWorkflows() {
        databaseFunction df=new databaseFunction();
        return df.getRunWorkflowFromWorkflows(this.getCurrentWorkflows().getId());
    }

    /**
     * Set visible this Workbox
     * @param b
     */
    public void setVisible(boolean b) {
       getJInternalFrame().setVisible(b);
    }

    /**
     * Maximize the size of the workflow/script JInternalFrame
     */
    public void mazimizeSize() {
        workbox.mazimizeSize();
    }

     /**
       * Set the visible jPanel to the WorkflowJPanel and set the workflow visible
       */
    public void setScriptVisible() {
       workbox.setScriptSelected(this.getCurrentArmadilloWorkflow().workflow);
       setVisible(true);
    }

    /**
       * Set the visible jPanel to the ScriptJPanel and set the workflow visible
       */
      public void setWorkflowVisible() {
       workbox.setWorkflowSelected();
       this.getCurrentArmadilloWorkflow().redraw();
       setVisible(true);
    }
    

    public void addOutput(String t) {
        if (workbox!=null) workbox.appendOutput(t);
    }

    public String getOutput() {
        if (workbox!=null) {
            return database_workflow.getWorkflows_outputText();
        } return "";
    }

    public void Message(String msg, String tooltip) {
        if (workbox!=null) workbox.Message(msg, tooltip);
    }

     public void MessageError(String msg, String tooltip) {
        if (workbox!=null) workbox.MessageError(msg, tooltip);
    }

     public void setProgress(int progress) {
         if (workbox!=null) workbox.setProgress(progress);
     }

//     /**
//      * Note: USED ONLY IN Programs...
//      * @param worker
//      */
//     public void setSwingWorker(SwingWorker worker) {
//         if (workbox!=null) workbox.setSwingWorker(worker);
//     }
     
     public boolean saveWorkflowAsTxt(String filename) {
//
//         workbox.database_workflow.updateCurrentWorkflow();
//         boolean saved=workbox.database_workflow.saveWorkflow(filename);
//         if (saved) {
//            workbox.Message("Successfully saved workflow to "+filename+" at ("+Util.returnCurrentDateAndTime()+")", "");
//         } else workbox.MessageError("Unable to save workflow to "+filename, "");
         this.saveWorkflowAsTxtSW(filename);
         return true;
     }

     public boolean loadWorkflowAsTxt(String filename) {
         this.loadWorkflowAsTxtSW(filename);
         return true;
     }
     
      public boolean loadWorkflowAsmyExperiment(String filename) {
         this.loadWorkflowAsmyExperimentSW(filename);
         return true;
     }
     
     
     public boolean loadProject(String filename) {
         this.loadProjectSW(filename);
         return true;
     }

      public boolean newWorkflowWithMigration(String filename, String newworkflowname) {
         this.newWorkflowWithMigrationSW(filename, newworkflowname);
         return true;
     }

     public boolean saveWorkflowAs(String filename, String newworkflowname) {
         this.saveWorkflowAsSW(filename, newworkflowname);
         return true;
     }

     public boolean saveWorkflowToDatabase() {
         this.saveWorkflowToDatabaseSW();
         return true;
     }

     public boolean saveWorkflowToDatabaseWOSW(String WorkflowName) {
         //--TO DO: Find why it bug!
         database_workflow.setName(WorkflowName);
         workbox.Message("Saving workflow "+WorkflowName+" to database...","");
         //workbox.database_workflow.updateCurrentWorkflow();
         database_workflow.setId(0); //--Reset ID for a new workflow
         boolean saved=database_workflow.saveToDatabase();
         if (saved) {
              workbox.Message("Successfully saved workflow to database ("+Util.returnCurrentDateAndTime()+")","");
              workbox.setWorkflowName(database_workflow.getName(), "");
              toolbox.reloadCurrentWorkflowsTree(getCurrentArmadilloWorkflow());
         }
         return saved;
     }

       public boolean saveWorkflowToDatabase(String WorkflowName) {
           database_workflow.setName(WorkflowName);
           this.saveWorkflowToDatabaseSW();         
         return true;
     }

     public boolean loadWorkflowFromDatabase(int id) {
         //boolean loaded=workbox.database_workflow.loadFromDatabase(id);
//         boolean loaded=
//         if (loaded) {
//            workbox.Message("Successfully loaded workflow from database...", "");
//         } else workbox.MessageError("Unable to load workflow from database...", "");
          loadWorkflowFromDatabaseSW(id);
         return true;
     }

     ///////////////////////////////////////////////////////////////////////////
     /// SwingWorker to load and save the workflow
     /// Warning, no return for those... To implement


      private void loadWorkflowFromDatabaseSW(final int id) {
      SwingWorker<Boolean, String> infoSwingWorker=new SwingWorker<Boolean, String>()  {

       @Override
        protected Boolean doInBackground() throws Exception {                
                 setProgress(50);
                 //--Get from database
                 boolean loaded=database_workflow.loadFromDatabase(id);
                 setProgress(75);
                 if (loaded) {
                     //--Update OutputText JTextArea...
                     workbox.setOutput(database_workflow.getWorkflows_outputText());                    
                     //--SimpleGraph and DisplayLine
                     getCurrentArmadilloWorkflow().simplegraph=database_workflow.isSimpleGraph();
                     getCurrentArmadilloWorkflow().draw_grid=database_workflow.isDisplayLINE();
                     workbox.setWorkflowName(database_workflow.getName(), "");
                     workbox.setWorkflowNote(database_workflow.getNote());
                     //Config.log(workbox.database_workflow.isDisplayLINE()+" "+workbox.database_workflow.isSimpleGraph());
                     boolean success=database_workflow.StringToWorkflow(database_workflow.getWorkflow_in_txt());
                     setProgress(100);
                     if (success) {
                          toolbox.reloadCurrentWorkflowsTree(getCurrentArmadilloWorkflow());
                          publish("Successfully loaded workflow from database...");
                          getCurrentArmadilloWorkflow().workflow.updateDependance();
                          getCurrentArmadilloWorkflow().force_redraw=true;
                          getCurrentArmadilloWorkflow().redraw();
                     } else {
                         toolbox.reloadCurrentWorkflowsTree(getCurrentArmadilloWorkflow());
                         System.out.println("Unable to load workflow from database... (Error 1)");
                         publish("Unable to load workflow from database... (Error 1)");
                         JOptionPane.showMessageDialog(frame,"Unable to load Workflow from "+config.getLastProject(),"Warning!",JOptionPane.ERROR_MESSAGE);
                     }
                 } else {                     
                     System.out.println("Unable to load workflow from database... (Error 2)");
                     publish("Unable to load workflow from database... (Error 2)");
                     JOptionPane.showMessageDialog(frame,"Unable to load Workflow from "+config.getLastProject(),"Warning!",JOptionPane.ERROR_MESSAGE);
                 }
                 getCurrentWorkflows().setChanged(false);
                 return true;
            }

            //On update notre Table avec les resultats partiels
            @Override
            protected void process(List<String> chunks) {
                for (String data:chunks) {
                    if (data.startsWith("Error")||data.startsWith("Unable")) {
                        workbox.MessageError(data,"");
                    } else {
                        workbox.Message(data,"");
                    }
                }
            }

           @Override
           protected void done(){               
                //loading.setVisible(false);
           }

        }; //End SwingWorker declaration

        infoSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                if (loading!=null) loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
        //loading=new InformationJDialog(frame, false, infoSwingWorker, "Loading workflow");
        //loading.setProgress(0); //Put 0% as the start progress
        //loading.Message("Loading workflow...", "");
        infoSwingWorker.execute();
   }

    public void loadNextWorkflow() {
        int next_workflow_id=Workbox.database_workflow.getNextValidId();
        if (next_workflow_id!=0) {
            this.loadWorkflowFromDatabase(next_workflow_id);
        }
    }
     
     public void loadPreviousWorkflow() {
        int next_workflow_id=Workbox.database_workflow.getPreviousValidId();
        if (next_workflow_id!=0) {
            this.loadWorkflowFromDatabase(next_workflow_id);
        }
    }
    
    private void loadProjectSW(final String filename) {
      SwingWorker<Boolean, String> infoSwingWorker=new SwingWorker<Boolean, String>()  {

       @Override
        protected Boolean doInBackground() throws Exception {                                 
                  setProgress(50);
                 //--Get from database
                  databaseFunction df=new databaseFunction();               
                  if (df.Open(filename)) {
                            getCurrentArmadilloWorkflow().setName(filename);
                            //--Enable saving to the Save-Menu                    
                            config.setLastWorkflow(filename);
                             config.Save();     
                            setProgress(100);                           
                             toolbox.reloadDatabaseTree();                          
                            //--Close and reload the loading frame                            
                            int workflow_id=df.getNextWorkflowsID()-1;
                            if (workflow_id>0) {
                                  loadWorkflowFromDatabase(workflow_id);
                                 //workbox.getCurrentArmadilloWorkflow().setName(workflowname);                                 
                            }
                  }               
                 return true;
            }

            //On update notre Table avec les resultats partiels
            @Override
            protected void process(List<String> chunks) {
                for (String data:chunks) {
                    if (data.startsWith("Error")||data.startsWith("Unable")) {
                        workbox.MessageError(data,"");
                    } else {
                        workbox.Message(data,"");
                    }
                }
            }

           @Override
           protected void done(){               
                loading2.setVisible(false);
           }

        }; //End SwingWorker declaration

        infoSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                loading2.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
        loading2=new InformationJDialog(frame, false, infoSwingWorker, "Project workflow");
        loading2.setProgress(0); //Put 0% as the start progress
        loading2.Message("Loading Project "+filename+"...", "");
        infoSwingWorker.execute();
   }   
   
    private boolean saveWorkflowImage(final String filename) {
        TimerTask  updateCursorAction= new TimerTask() {
            public void run() {
                    try {                        
                        
                        if (getCurrentArmadilloWorkflow()!=null) getCurrentArmadilloWorkflow().saveImage(filename);
                        
                    }
                    catch (Exception e2) {
                        return;
                    }
                }            
        };
        new Timer().schedule(updateCursorAction,0);
        return true;
    }
    
    /**
     * Create the images of the workflow in the current project in the directory...
     * @param directory 
     */
    public void CreateScreenShot(String directory) {
        this.CreateScreenShotSW(directory);
    }
    
     private void CreateScreenShotSW(final String directory) {
      SwingWorker<Boolean, String> infoSwingWorker=new SwingWorker<Boolean, String>()  {

       @Override
        protected Boolean doInBackground() throws Exception {                
                 setProgress(0);
                 //--Get from database
                 
                 Workflows w=new Workflows();
                 int total=w.df.getNextWorkflowsID()-1;
                 
                for (int i=0; i<total;i++) {
                    publish("Processing Workflow ["+i+"/"+total+"]");    
                    boolean loaded=database_workflow.loadFromDatabase(i);
                    if (loaded) {
                        getCurrentArmadilloWorkflow().simplegraph=database_workflow.isSimpleGraph();
                         getCurrentArmadilloWorkflow().draw_grid=database_workflow.isDisplayLINE();
                       workbox.setWorkflowName(database_workflow.getName(), "");
                        workbox.setWorkflowNote(database_workflow.getNote());
                     //Config.log(workbox.database_workflow.isDisplayLINE()+" "+workbox.database_workflow.isSimpleGraph());
                       boolean success=database_workflow.StringToWorkflow(database_workflow.getWorkflow_in_txt());
                        getCurrentArmadilloWorkflow().workflow.updateDependance();
                        getCurrentArmadilloWorkflow().force_redraw=true;
                        getCurrentArmadilloWorkflow().redraw();
                        //getCurrentArmadilloWorkflow().saveImage(directory+File.separator+i+".png");
                        saveWorkflowImage(directory+File.separator+i+".png");
                        
                    }
                    setProgress(i*100/total);    
                }
                 return true;
            }

            //On update notre Table avec les resultats partiels
            @Override
            protected void process(List<String> chunks) {
                for (String data:chunks) {
                    if (data.startsWith("Error")||data.startsWith("Unable")) {
                        workbox.MessageError(data,"");
                    } else {
                        workbox.Message(data,"");
                    }
                }
            }

           @Override
           protected void done(){               
                loading.setVisible(false);
           }

        }; //End SwingWorker declaration

        infoSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
        loading=new InformationJDialog(frame, false, infoSwingWorker, "Creating workflow image in "+directory);
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Creating workflow images...", "");
        infoSwingWorker.execute();
   }
      
    private void loadWorkflowAsTxtSW(final String filename) {
      SwingWorker<Boolean, String> infoSwingWorker=new SwingWorker<Boolean, String>()  {

       @Override
        protected Boolean doInBackground() throws Exception {
                 boolean status=false;
                 setProgress(50);
                 boolean loaded=database_workflow.loadWorkflow(filename);
                 setProgress(100);
                 if (loaded) {
                     getCurrentArmadilloWorkflow().simplegraph=database_workflow.isSimpleGraph();
                     getCurrentArmadilloWorkflow().draw_grid=database_workflow.isDisplayLINE();
                     workbox.setWorkflowName(database_workflow.getName(), "");
                     publish("Successfully loaded workflow from database...");
                     database_workflow.setId(0);
                     database_workflow.setName("Loaded from "+filename+" at "+Util.returnCurrentDateAndTime());
                     database_workflow.setNote("");
                     boolean saved=database_workflow.saveToDatabase();
                     toolbox.reloadCurrentWorkflowsTree(getCurrentArmadilloWorkflow());
                     if (saved) {
                         toolbox.reloadDatabaseTree();
                     }
                     getCurrentArmadilloWorkflow().force_redraw=true;
                     getCurrentArmadilloWorkflow().redraw();
                 } else publish("Unable to load workflow from "+filename+"...");
                 getCurrentWorkflows().setChanged(false);
                 return true;
            }

            //On update notre Table avec les resultats partiels
            @Override
            protected void process(List<String> chunks) {
                for (String data:chunks) {
                    if (data.startsWith("Error")||data.startsWith("Unable")) {
                        workbox.MessageError(data,"");
                    } else {
                        workbox.Message(data,"");
                    }
                }
            }


           @Override
           protected void done(){
                loading.setVisible(false);
           }

        }; //End SwingWorker declaration

        infoSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
        loading=new InformationJDialog(frame, false, infoSwingWorker, "Loading workflow from "+filename);
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Loading workflow...", "");
        infoSwingWorker.execute();
      }

    private void loadWorkflowAsmyExperimentSW(final String filename) {
      SwingWorker<Boolean, String> infoSwingWorker=new SwingWorker<Boolean, String>()  {

       @Override
        protected Boolean doInBackground() throws Exception {
                 boolean status=false;
                 database_workflow.workflow.workflow.selectAll();
                 database_workflow.workflow.workflow.deleteSelected();
                 setProgress(50);                 
                 boolean loaded=database_workflow.loadmyExperiment(filename);
                 setProgress(100);
                 if (loaded) {
                     getCurrentArmadilloWorkflow().simplegraph=database_workflow.isSimpleGraph();
                     getCurrentArmadilloWorkflow().draw_grid=database_workflow.isDisplayLINE();
                     workbox.setWorkflowName(database_workflow.getName(), "");
                     publish("Successfully loaded workflow from database...");
                     database_workflow.setId(0);
                     database_workflow.setName("Loaded from "+filename+" at "+Util.returnCurrentDateAndTime());
                     database_workflow.setNote("");
                     boolean saved=database_workflow.saveToDatabase();
                     toolbox.reloadCurrentWorkflowsTree(getCurrentArmadilloWorkflow());
                     if (saved) {
                         toolbox.reloadDatabaseTree();
                     }
                     getCurrentArmadilloWorkflow().force_redraw=true;
                     getCurrentArmadilloWorkflow().redraw();
                 } else publish("Unable to load workflow from "+filename+"...");
                 getCurrentWorkflows().setChanged(false);
                 return true;
            }

            //On update notre Table avec les resultats partiels
            @Override
            protected void process(List<String> chunks) {
                for (String data:chunks) {
                    if (data.startsWith("Error")||data.startsWith("Unable")) {
                        workbox.MessageError(data,"");
                    } else {
                        workbox.Message(data,"");
                    }
                }
            }


           @Override
           protected void done(){
                loading.setVisible(false);
           }

        }; //End SwingWorker declaration

        infoSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
        loading=new InformationJDialog(frame, false, infoSwingWorker, "Loading workflow from "+filename);
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Loading workflow...", "");
        infoSwingWorker.execute();
      }
    
      private void  saveWorkflowToDatabaseSW() {
      SwingWorker<Boolean, String> infoSwingWorker=new SwingWorker<Boolean, String>()  {

       @Override
        protected Boolean doInBackground() throws Exception {
                 int retry=3;
                 boolean saved=false;
                 while(!saved&&retry!=0) {
                 setProgress(25);
                 //done in saveToDatabase
                 //workbox.database_workflow.updateCurrentWorkflow();
                 //workbox.database_workflow.setWorkflows_outputText(workbox.getOutput());
                 setProgress(50);
                 database_workflow.setId(0); //--Reset ID for a new workflow
                 saved=database_workflow.saveToDatabase();
                 setProgress(75);
                 if (saved) {
                     publish("Successfully saved workflow to database ("+Util.returnCurrentDateAndTime()+")");
                     workbox.setWorkflowName(database_workflow.getName(), "");
                     toolbox.reloadDatabaseTree();
                     toolbox.reloadCurrentWorkflowsTree(getCurrentArmadilloWorkflow());
                     setProgress(100);
                 } else                                          
                     publish("Unable to save workflow to database... Retrying...");
                     retry--;
                 }
                 if (retry==0) publish("Unable to save workflow to database.");
                 getCurrentWorkflows().setChanged(false);
                 return true;
            }

            //On update notre Table avec les resultats partiels
            @Override
            protected void process(List<String> chunks) {
                for (String data:chunks) {
                    if (data.startsWith("Error")||data.startsWith("Unable")) {
                        workbox.MessageError(data,"");
                    } else {
                        workbox.Message(data,"");
                    }
                }
            }


           @Override
           protected void done(){
                loading.setVisible(false);
               
           }

        }; //End SwingWorker declaration

        infoSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
        loading=new InformationJDialog(frame, false, infoSwingWorker, "Saving workflow to database");
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Saving workflow...", "");
        infoSwingWorker.execute();
      }

      private void  saveWorkflowAsTxtSW(final String filename) {
      SwingWorker<Boolean, String> infoSwingWorker=new SwingWorker<Boolean, String>()  {

       @Override
        protected Boolean doInBackground() throws Exception {
                 boolean status=false;
                 setProgress(25);
                 database_workflow.updateCurrentWorkflow();
                 setProgress(50);
                 boolean saved=database_workflow.saveWorkflow(filename);
                 setProgress(100);
                 if (saved) {
                     publish("Successfully saved workflow to "+filename+" at ("+Util.returnCurrentDateAndTime()+")");
                 } else publish("Unable to save workflow to "+filename+"...");
                 getCurrentWorkflows().setChanged(false);
                 return true;
            }

            //On update notre Table avec les resultats partiels
            @Override
            protected void process(List<String> chunks) {
                for (String data:chunks) {
                    if (data.startsWith("Error")||data.startsWith("Unable")) {
                        workbox.MessageError(data,"");
                    } else {
                        workbox.Message(data,"");
                    }
                }
            }


           @Override
           protected void done(){
                loading.setVisible(false);
           }

        }; //End SwingWorker declaration

        infoSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
        loading=new InformationJDialog(frame, false, infoSwingWorker, "Saving workflow as txt to "+filename);
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Saving workflow...", "");
        infoSwingWorker.execute();
      }

      private void  saveWorkflowAsSW(final String newName, final String workflowname) {
      SwingWorker<Boolean, String> infoSwingWorker=new SwingWorker<Boolean, String>()  {

       @Override
        protected Boolean doInBackground() throws Exception {
                 setProgress(25);
                 database_workflow.updateCurrentWorkflow();
                 setProgress(50);
                 boolean saved=database_workflow.saveToDatabase();
                 setProgress(75);
                 databaseFunction df=new databaseFunction();
                 if (saved&&df.SaveAs(newName)) {
                     getCurrentArmadilloWorkflow().setName(workflowname);
                     workbox.setWorkflowName(workflowname, "");
                     setProgress(90);
                     publish("Successfully saved workflow to "+newName+" at ("+Util.returnCurrentDateAndTime()+")");
                 } else publish("Unable to save workflow as "+newName+"...");
                 getCurrentWorkflows().setChanged(false);
                 return true;
            }

            //On update notre Table avec les resultats partiels
            @Override
            protected void process(List<String> chunks) {
                for (String data:chunks) {
                    if (data.startsWith("Error")||data.startsWith("Unable")) {
                        workbox.MessageError(data,"");
                    } else {
                        workbox.Message(data,"");
                    }
                }
            }


           @Override
           protected void done(){
               setProgress(100);
               loading.setVisible(false);

           }

        }; //End SwingWorker declaration

        infoSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
        loading=new InformationJDialog(frame, false, infoSwingWorker, "Saving workflow to "+newName);
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Saving workflow...", "");
        infoSwingWorker.execute();
      }

      

      private void  newWorkflowWithMigrationSW(final String newName, final String workflowname) {
      SwingWorker<Boolean, String> infoSwingWorker=new SwingWorker<Boolean, String>()  {

       @Override
        protected Boolean doInBackground() throws Exception {                 
                 setProgress(25);
                 database_workflow.updateCurrentWorkflow();
                 setProgress(50);
                 boolean saved=database_workflow.saveToDatabase();
                 setProgress(75);
                 databaseFunction df=new databaseFunction();
                 if (saved&&df.SaveAs(newName)) {
                     getCurrentArmadilloWorkflow().setName(workflowname);
                     setProgress(100);
                     publish("Successfully saved workflow to "+newName+" at ("+Util.returnCurrentDateAndTime()+")");
                 } else publish("Unable to save workflow as "+newName+"...");
                 getCurrentWorkflows().setChanged(false);
                 return true;
            }

            //On update notre Table avec les resultats partiels
            @Override
            protected void process(List<String> chunks) {
                for (String data:chunks) {
                    if (data.startsWith("Error")||data.startsWith("Unable")) {
                        workbox.MessageError(data,"");
                    } else {
                        workbox.Message(data,"");
                    }
                }
            }


           @Override
           protected void done(){
                loading.setVisible(false);
           }

        }; //End SwingWorker declaration

        infoSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
        loading=new InformationJDialog(frame, false, infoSwingWorker, "Creating new workflow to "+newName);
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Creating workflow...", "");
        infoSwingWorker.execute();
      }

       public void outputExecution() {
      SwingWorker<Boolean, String> infoSwingWorker=new SwingWorker<Boolean, String>()  {

       @Override
        protected Boolean doInBackground() throws Exception {
                 boolean status=false;
                 setProgress(50);
                 getCurrentArmadilloWorkflow().workflow.outputExecution();
                 //Workflows.workflow.workflow.outputExecution();
                 setProgress(100);

                 return true;
            }

            //On update notre Table avec les resultats partiels
            @Override
            protected void process(List<String> chunks) {
                for (String data:chunks) {
                    if (data.startsWith("Error")||data.startsWith("Unable")) {
                        workbox.MessageError(data,"");
                    } else {
                        workbox.Message(data,"");
                    }
                }
            }


           @Override
           protected void done(){
                loading.setVisible(false);
           }

        }; //End SwingWorker declaration

        infoSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
        loading=new InformationJDialog(frame, false, infoSwingWorker, "Computing execution path...");
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Computing execution path...", "");
        infoSwingWorker.execute();
      }

       public void ShowPreferences() {
           workbox.preferences.display();
       }

       
    public boolean isRunning() {
        return workbox.program.running;
    }   
       
     public void Run() {
       workbox.Run();
    }
     
      public void Run(int start, int end) {
       workbox.Run(start,end);
    }

    public void Run(workflow_properties properties) {
        workbox.Run(properties);
    }
    
    public void Stop() {
        workbox.StopSW();
    }
    
    public void ClearOutput() {
        workbox.ClearOuput();
    }

    public void setRunWorkflow(RunWorkflow run) {
        workbox.setRunWorkflow(run);
    }
    
    /**
     * Reset the state of all object in workflow
     */
    public void resetState() {
        this.getCurrentArmadilloWorkflow().resetState();
    }
    
    /**
     * Return the current Workflow Filename (db)
     * @return 
     */
    public String getCurrentWorkflowFilename() {       
        return databaseFunction.db.dbFileName;
    }
}
