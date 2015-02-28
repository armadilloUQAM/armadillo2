/*
 *  Armadillo Workflow Platform v2.0
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



package results;

import biologic.Biologic;
import biologic.ImageFile;
import biologic.MultipleTrees;
import biologic.Output;
import biologic.Text;
import biologic.Tree;
import biologic.Workflows;
import biologic.seqclasses.parserNewick.newick_tree;
import configuration.Config;
import configuration.Util;
import database.Project;
import database.databaseFunction;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import org.apache.commons.lang.SystemUtils;
import program.RunProgram;
import programs.convertSVGtoPNG;
import programs.scriptree;
import tools.Toolbox;
import tools.ToolboxMutableTreeNode;
import workflows.Workbox;
import workflows.armadillo_workflow;
import workflows.armadillo_workflow.workflow_object;
import workflows.workflow_properties;

/**
 * This is a new version / in development / of the report generator
 * Simple class to generate report results
 * @author Leclercq Mickael
 * @autor Etienne Lord, 2010
 */
public class report {

 


    //////////////////////////
    //Colors (UNUSED, see css)
    public String nucA = "#ff0000";
    public String nucU = "#00ff00";
    public String nucT = "#00ff00";
    public String nucG = "#0000ff";
    public String nucC = "#ffff00";
    public String nucN = "#fffeff";
    public String nucGap = "#ffffff";
    public String protG = "#ebebeb";
    public String protP = "#dc9682";
    public String protA = "#c8c8c8";
    public String protV = "#0f820f";
    public String protL = "#0f820f";
    public String protI = "#0f820f";
    public String protM = "#e6e600";
    public String protC = "#e6e600";
    public String protF = "#3232aa";
    public String protY = "#3232aa";
    public String protW = "#b45ab4";
    public String protH = "#8282d2";
    public String protK = "#145aff";
    public String protR = "#145aff";
    public String protQ = "#00dcdc";
    public String protN = "#00dcdc";
    public String protE = "#e60a0a";
    public String protD = "#e60a0a";
    public String protS = "#fa960a";
    public String protT = "#fa960a";
    public String defau = "#000000";
    
   static final int Mb=1048576;

    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    
    Workbox work=new Workbox();
    Config config = new Config();
    Util util = new Util();

    ////////////////////////////////////////////////////////////////////////////
    /// Internal report variables

    int alignmentsCount=0;
    int treesCount=0;
    int workflowId=0;
    Project project;
    public static String projectName="";
    String author="";
    String institution="";
    String email="";
    Vector <Integer> alignmentsIds;         //--Used for report
    Vector <Integer> treesIds;              //--Used for report
    
    private static volatile int progress=0; //--This is a progress indicator if needed

    ////////////////////////////////////////////////////////////////////////////
    /// Main function

    /**
     * This Generate a "Run" report of the Armadillo Workflow
     * @param project
     * @return The path to the generated report
     */
    public String generate_Report(Project project, Workflows workflow){
        this.project=project;
        projectName="Execution_at_"+Util.returnCurrentDateAndTime();        
        projectName=projectName.replaceAll(":", "_").replaceAll(" ", ".");
        CreateResultsFolders();
        generateReport(project, workflow, config.resultsDir()+File.separator+projectName+File.separator+"index.html");     
        Config.log("Generating report for "+projectName);        
        return config.resultsDir()+File.separator+projectName+File.separator+"index.html";
    }

    /**
     * This Generate a "Run" Application report of the Armadillo Workflow
     * --It will execute all the sample workflows in the help folder...
     * 
     * @param project
     * @return The path to the generated report
     */
    public String generate_ApplicationRun_Report(){
        
        projectName="ApplicationRunReport_at_"+Util.returnCurrentDateAndTime();        
        projectName=projectName.replaceAll(":", "_").replaceAll(" ", ".");
        long time_start=System.currentTimeMillis();
        long time_end=0;
        String output=config.resultsDir()+File.separator+projectName+File.separator+"index.html";
        CreateResultsFolders();
        //--Run each workflow and print results...
        Vector<String> files = Config.listDirWithFullPath(config.dataPath()+File.separator+"help");        
        //--Local version of the database function for easy access
        databaseFunction df=new databaseFunction();    
        //--Display in the main workbox...
        Workbox workbox=new Workbox();
        workbox.Message("Generating Application Run report in "+output,"");
        try {
            //--Create the output file (index.html) for the report...
              //--HTML
            
            Util report=new Util(output);
            Config.log("Generating Application Run report in "+output);
            Config.log("Started at "+Util.returnCurrentDateAndTime());
          
            report.println("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>");
            report.println(this.generateTopApplicationReport("Application Run Report - "+Util.returnCurrentDateAndTime()));
            //--Report style 1
            //-- Table (1)            
            report.println("<table style='text-align: left; width: 100%;' border='1' cellpadding='0' cellspacing='0'>"+                            
                            printSystemInfo()+
                            "<br ><tbody><tr>"+
                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Application</span></big></td>"+
                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Success</span></big></td>"+
//                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Name</span></big></td>"+
//                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Version</span></big></td>"+
//                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Website</span></big></td>"+
//                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Help</span></big></td>"+
//                    "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Sample</span></big></td>"+
//                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'><a title='Description'>Desc</a></span></big></td>"+
//                    "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'><a title='Publication'>Publ</a></span></big></td>"+
//                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Windows</span></big></td>"+
                           "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Total time</span></big></td>"+
                           "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Report</span></big></td>");
            report.println("</tr>"); 
                      
                    
                        

                   
           
            
            //--Iterate over each file in the help folder
            int total_count=files.size(); //--Total file count... (for percent)
            for (int i=0; i<files.size();i++) {
                workbox.setProgress((i*100)/total_count);
                String filename=files.get(i); 
                //--Note: we skip BaliPhy...
                if (filename.endsWith(".db")) {
                   
                    //--1. Copy to a new file
                     File file=new File(filename);
                     Config.log("Testing run application for "+file.getName());
                    String destination=config.resultsDir()+File.separator+projectName+File.separator+file.getName();
                    //--2. Verify that we have an unique name 
                    int count=0;
                    while(Util.FileExists(destination)) {
                      destination=config.resultsDir()+File.separator+projectName+File.separator+(count++)+file.getName();
                    }  
                    try {
                        Util.copy(filename, destination);
                    } catch(Exception e) {
                       Config.log("Unable to copy workflow (.db) - "+filename+" to "+destination); 
                    }
                   //--3. Open the workflow and do the test run...
                                    if (df.Open(destination)) {                                        
                                        Config.log("Opened filename: "+destination);                                        
                                        workbox.Message("Running "+file.getName(), "");
                                        int workflow_id=df.getNextWorkflowsID()-1;
                                         if (workflow_id>0) {                                                                       
                                             System.out.println("Loading last workflow with ID ["+workflow_id+"].");
                                              Workflows run = new Workflows(new armadillo_workflow());
                                               long st=System.currentTimeMillis(); 
                                              run.loadFromDatabase(workflow_id);
                                                run.StringToWorkflow();
                                                program.programs torun = new program.programs(run);
                                                if (torun != null) {
                                                    torun.Run();
                                                    //--Wait till its done...
                                                    while (!torun.isDone()) {
                                                    }
                                                    run.saveToDatabase();
                                                    //--Get the last workflow 
                                                    workflow_id=df.getNextWorkflowsID()-1;
                                                    run.loadFromDatabase(workflow_id);
                                                    run.StringToWorkflow();                                
                                                    
                                                    run.generateStatistics();
                                                    
//                                                    for (workflow_object object:run.workflow.workflow.work) {
//                                                        workflow_properties obj_propertiesj=object.getProperties();
//                                                         workflow_object tmp=null;
//                                                    if (!obj_propertiesj.get("ObjectType").equals(workflow_properties.NotSet)) {
//                                                        // Test for some object for the report...
//                                                        if (obj_propertiesj.get("ObjectType").equals("For")||obj_propertiesj.get("ObjectType").equals("While")) {
//                                                        } else
//                                                        if (obj_propertiesj.get("ObjectType").equals("If")) {
//                                                        } else
//                                                         if (obj_propertiesj.get("ObjectType").equals("Begin")) {
//                                                        } else
//                                                         if (obj_propertiesj.get("ObjectType").equals("End")) {
//                                                        } else
//                                                          if (obj_propertiesj.get("ObjectType").equals("Script")) {
//                                                        } else
//                                                         if (obj_propertiesj.get("ObjectType").equals("ScriptBig")) {
//                                                        } else
//                                                         if (obj_propertiesj.get("ObjectType").equals("Output")) {
//                                                        } else
//                                                         if (obj_propertiesj.get("ObjectType").equals("OutputDatabase")) {
//                                                        } else
//                                                         if (obj_propertiesj.get("ObjectType").equals("Variable")) {
//                                                        } else
//                                                        {
//                                                            //By default a Program...
//                                                            if (obj_propertiesj.getStatus()==RunProgram.status_error) {
//                                                                correct=false;
//                                                            }                                        
//
//                                                        }
//                                                    } //--End type of object  
//                                                   } //--End for workflow properties
                                                    
                                                    
                                                    //--Print report... 
                                                    Util u=new Util(destination+".txt");
                                                    //RunProgram runprogram=new RunProgram(run.getRunProgram_id());
                                                    u.println(run.workflow_output);
                                                    u.close();
                                                    
                                                    report.println("<TR style='background-color: white; text-align: center; color: black;'><TD>"+file.getName()+"</TD>"+                                      
                                                    (run.completed?"<TD BGCOLOR='lime'>":"<TD BGCOLOR='red'>") +(run.completed?"X":"See log")+"</TD>"+
//                                       (sampleWorkflowFound(prop)?"<TD BGCOLOR='lime'>":"<TD BGCOLOR='red'>") +(sampleWorkflowFound(prop)?"<a title='"+prop.get("SampleWorkflow")+"'>X</a>":"")+"</TD>"+
//                                       (prop.isSet("Description")?"<TD BGCOLOR='lime'>":"<TD BGCOLOR='red'>") +(prop.isSet("Description")?"<a title='"+prop.get("Description")+"'>X</a>":"")+"</TD>"+
                                                    "<TD>"+Util.msToString(torun.getRunningTime()) +"</TD>"+
                                                            
                                          (Util.FileExists(destination+".txt")?"<TD BGCOLOR='lime'>":"<TD BGCOLOR='red'>")+"<a href='"+destination+".txt"+"'>"+destination+".txt</a>"+"</TD>"+
                                                    "</TR>");
                                                    
                                                }
                                                //--Make sure we clean the memory...
                                                Util.CleanMemory();

                                         } else {
                                             Config.log("Warning. File "+destination+" do not have any workflows.");
                                         }
                } else {
                        System.out.println("Unable to load workflow in db");
                        Config.log("Error. Unable to load workflow "+filename);
                } //--End else loaing workflow
                //break; //--debug
                } //--End if filename endsWith db
                
               } //--End for filename
             Config.log("Generating report for "+projectName);  
            Config.log("End: "+Util.returnCurrentDateAndTime());
            time_end=System.currentTimeMillis();
            long total_time=time_end-time_start;        
              Config.log("Total time:"+Util.msToString(total_time));
            report.println("</tbody></table>");
            report.println("<br >Total time:"+Util.msToString(total_time)+"<br></div></div></div>");
            report.println(this.foot());
            report.println("</body></html>");              
            report.close();
            workbox.setProgress(100);

        } catch(Exception e) {
            Config.log("Error. Unable to generate application run report at "+Util.returnCurrentDateAndTime());           
            workbox.MessageError("Error. Unable to generate application run report at "+Util.returnCurrentDateAndTime(), "");                        
        }
        return output; //--Return the generated output ...
    }
    
    
    
    /**
     * This Generate a Report but also include all the `Results`
     * in in (instead of different files).
     * @param project
     * @param workflow
     * @return String containing the path to the report
     */
    public String generate_Long_Report(Project project, Workflows workflow){
        this.project=project;
        projectName="Execution_at_"+Util.returnCurrentDateAndTime();
        projectName=projectName.replaceAll(":", "_").replaceAll(" ", ".");
        CreateResultsFolders();
        generateLongReport(project, workflow, config.resultsDir()+File.separator+projectName+File.separator+"index.html");
        Config.log("Generating long report for "+projectName);
        return config.resultsDir()+File.separator+projectName+File.separator+"index.html";
    }

    /**
     * Note: This is an *IN DEVELOPMENT* applications report generator
     * @return String containing the path to the report
     */
    public String generate_Application_Report() {
        projectName= "Application_report_at_"+Util.returnCurrentDateAndTime();
        projectName=projectName.replaceAll(":", "_").replaceAll(" ", ".");
         CreateResultsFolders();
         if (!generateApplicationsReport(config.resultsDir()+File.separator+projectName+File.separator+"included.html")) {
             System.out.println("Unable to generate Applications report... (1)");
         }
         if (!this.generateApplicationsReportComplex(config.resultsDir()+File.separator+projectName+File.separator+"included_os.html")) {
             System.out.println("Unable to generate Applications report... (2)");
         }
         Config.log("Generating Application report for "+projectName);
         return  config.resultsDir()+File.separator+projectName+File.separator+"included.html";
    }
    
    /**
     * Create Results folder with css and images
     */
    private void CreateResultsFolders(){
        config.createDir(config.resultsDir()+File.separator+projectName);
        this.work.getCurrentArmadilloWorkflow().saveImage(config.resultsDir()+File.separator+projectName+File.separator+"images"+File.separator+"Workflow.png");
        //--Results (for ZIP)
        config.createDir(config.resultsDir()+File.separator+projectName+File.separator+"results");
        //--HTML info
        config.createDir(config.resultsDir()+File.separator+projectName+File.separator+"images");
        config.createDir(config.resultsDir()+File.separator+projectName+File.separator+"css");
        try {
            Util.copy(new File(config.dataPath() + File.separator+"site"+File.separator+"images"+File.separator+"logo.png"), new File(
                    config.resultsDir() + File.separator + projectName + File.separator+"images"+File.separator+"logo.png"));

            Util.copy(new File(config.dataPath() + File.separator+"site"+File.separator+"images"+File.separator+"img01.jpg"), new File(
                    config.resultsDir() + File.separator + projectName + File.separator+"images"+File.separator+"img01.jpg"));

            Util.copy(new File(config.dataPath() + File.separator+"site"+File.separator+"images"+File.separator+"img02.jpg"), new File(
                    config.resultsDir() + File.separator + projectName + File.separator+"images"+File.separator+"img02.jpg"));
            
            Util.copy(new File(config.dataPath() + File.separator+"site"+File.separator+"images"+File.separator+"img03.jpg"), new File(
                    config.resultsDir() + File.separator + projectName + File.separator+"images"+File.separator+"img03.jpg"));
            
            Util.copy(new File(config.dataPath() + File.separator+"site"+File.separator+"images"+File.separator+"spacer.gif"), new File(
                    config.resultsDir() + File.separator + projectName + File.separator+"images"+File.separator+"spacer.gif"));
            
            Util.copy(new File(config.dataPath() + File.separator+"site"+File.separator+"css"+File.separator+"styles.css"), new File(
                    config.resultsDir() + File.separator + projectName + File.separator+"css"+File.separator+"styles.css"));

            Util.copy(new File(config.dataPath() + File.separator+"site"+File.separator+"favicon.ico"), new File(
                    config.resultsDir() + File.separator + projectName + File.separator+"favicon.ico"));
        } catch (IOException ex) {
            Config.log("Unable to copy files...");
        }
    }

    private String generateWorkflowInfo() {
        return  "\n"+
                "	<div id=\"content\">\n"+
                "		<div class=\"post\">\n"+
                "               <a href=\"images"+File.separator+"Workflow.png"+"\" target=\"_blank\">" +
                "                       <img src=\"images"+File.separator+"Workflow.png.thumb.png"+"\"/></a>\n"+
                "                       <p><div style=\"text-align:center\"><b><i>Click on this image to open it in a new unique webpage in higher resolution</i></b></div><p>\n"+
                 "<div align=\"left\" style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\"></div><br>"+
                "			<h2 class=\"title\">Worflow Informations </h2>\n"+
                "			<div class=\"entry\">\n"+
                "				<table width=\"841\" border=\"0\">\n"+
                "				<tr>\n"+
                "					<td width=\"174\">Project name:</td>\n"+
                "					<td width=\"651\">"+project.getName()+"</td>\n"+
                "				</tr>\n"+
                "				<tr>\n"+
                "					<td>Workflow name:</td>\n"+
                "					<td>"+work.getCurrentWorkflows().getName()+"</td>\n"+
                "                               <tr>\n"+
                "					<td>RunWorkflow :</td>\n"+
                "					<td>"+(work.getRunWorkflowForWorkflows()==null?"Not Executed":work.getRunWorkflowForWorkflows().toString().replaceAll("\n", "<br>"))+"</td>\n"+
                "				</tr>\n"+
                "				<tr>\n"+
                "					<td>Creation date:</td>\n"+
                "					<td>"+work.getCurrentWorkflows().getDate_created()+"</td>\n"+
                "				</tr>\n"+
                "				<tr>\n"+
                "					<td>Database location: </td>\n"+
                "					<td>"+work.getCurrentWorkflowFilename() +"</td>\n"+
                "				</tr>\n"+
                "				<tr>\n"+
                "					<td>Other informations (notes): </td>\n"+
                "					<td>"+work.getCurrentWorkflows().getNote()+"</td>\n"+
                "				</tr>\n"+
                "                               </table>\n"+
                "                           <br>\n"+
                "			</div>\n"+
                //"		</div>\n"+
                "		</div>\n";
    }

    /**
     * Private function to generate the application report
     * @param filename
     * @return 
     */
     private boolean generateApplicationsReport(String filename) {
        //--Note: generate also a paste to wiki report
        //--Sample..
         //  Multiple Sequence Alignment (MSA)
        // *test - v 1.0 - [http://www.example.com link title]
         try {
            //--The tools tree: easier because it's already shorted
            Toolbox toolbox=new Toolbox();
            JTree applications_tree=toolbox.getApplicationTree();
            DefaultTreeModel mode=(DefaultTreeModel) applications_tree.getModel();
            ToolboxMutableTreeNode treeroot=(ToolboxMutableTreeNode) mode.getRoot();   
            
            //--Exceptions (do not put in the report)
                String[] exception_name={"Blast Download","Blast (Web Ncbi)","Create Local BlastDB",
                                         "LocalBlast","dbFetch (Web EBI)","EB-Eye (Web EBI)",
                                         "Ncbi eUtils","Fetch Sequences Ncbi","DNAML-Erate",
                                         "Custom Program _Old_","Kalign - Custom Program"};
                HashMap<String,Boolean> dict_exception_name=new HashMap <String,Boolean>();
                for (String s:exception_name) dict_exception_name.put(s, Boolean.TRUE);

                for (int i=0; i<treeroot.getChildCount();i++) {
                    ToolboxMutableTreeNode cat=(ToolboxMutableTreeNode) treeroot.getChildAt(i);
                    //--Get applications
                    if (cat.getProperties().getName().equals("Alignment")) {
                          for (int j=0; j<cat.getChildCount();j++) {
                            ToolboxMutableTreeNode appli=(ToolboxMutableTreeNode) cat.getChildAt(j);
                            workflow_properties prop=appli.getProperties();
                            if (dict_exception_name.containsKey(prop.getName().trim())) {
                                prop.put("done_report", true);
                            }
                          }
                    }
                } //--End exception
                
            //--HTML
            Util report=new Util(filename);
            Util report_wiki=new Util(filename+".wiki");
            System.out.println(filename);
            report.println("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>");
            report.println(this.generateTopApplicationReport("Included Applications"));            
            //--Report style 1
            //-- Table (1)            
            report.println("<table style='text-align: left; width: 100%; font-size: 14px;' border='1' cellpadding='0' cellspacing='0' >"+
                            "<tbody><tr>"+
                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Nucleic or Proteic Sequences</span></big></td>"+
                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Phylogenetic Trees</span></big></td>"+
                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Model of Evolutionary Pressure</span></big></td>");                            
            report.println("</tr>"); 
            
            //--wiki report
            report_wiki.println("<!-- Application report created on "+Util.returnCurrentDateAndTime()+" -->");
            
            //--Print MSA 
            report.println("<td style='vertical-align: top; text-align: center;'><span style='font-weight: bold; '><br />Multiple Sequence Alignment (MSA)</span><br /><br />");
            report_wiki.println("\nMultiple Sequence Alignment (MSA)\n"); 
            for (int i=0; i<treeroot.getChildCount();i++) {
                ToolboxMutableTreeNode cat=(ToolboxMutableTreeNode) treeroot.getChildAt(i);
                //--Get applications
                if (cat.getProperties().getName().equals("Alignment")) {
                      for (int j=0; j<cat.getChildCount();j++) {
                        ToolboxMutableTreeNode appli=(ToolboxMutableTreeNode) cat.getChildAt(j);
                        workflow_properties prop=appli.getProperties();
                        prop.put("done_report", true);
                        report.println(pretty_prop(prop));
                        report_wiki.println(pretty_wiki_prop(prop));
                      }
                }
             }   
             
            //--Print HGT
             report.println("<span style='font-weight: bold;'><br />Detection of Horizontal Gene Transfers</span><br /><br />");
             report_wiki.println("\nDetection of Horizontal Gene Transfers\n");
             for (int i=0; i<treeroot.getChildCount();i++) {
                ToolboxMutableTreeNode cat=(ToolboxMutableTreeNode) treeroot.getChildAt(i);
                //--Get applications
                if (cat.getProperties().getName().startsWith("Horizontal Gene")) {
                      for (int j=0; j<cat.getChildCount();j++) {
                        ToolboxMutableTreeNode appli=(ToolboxMutableTreeNode) cat.getChildAt(j);
                        workflow_properties prop=appli.getProperties();
                        prop.put("done_report", true);
                        report.println(pretty_prop(prop));
                        report_wiki.println(pretty_wiki_prop(prop));
                      }
                }
             }   
            report.println("</td>");
            report.println("<td style='vertical-align: top; text-align: center;'><span style='font-weight: bold;'><br />Phylogenetic Trees</span><br /><br />");
            report_wiki.println("\nPhylogenetic Trees\n");  
            //--Print Phylogenetic Trees
             for (int i=0; i<treeroot.getChildCount();i++) {
                ToolboxMutableTreeNode cat=(ToolboxMutableTreeNode) treeroot.getChildAt(i);
                //--Get applications
                if (cat.getProperties().getName().equals("Tree")) {
                      for (int j=0; j<cat.getChildCount();j++) {
                        ToolboxMutableTreeNode appli=(ToolboxMutableTreeNode) cat.getChildAt(j);
                        workflow_properties prop=appli.getProperties();
                        if (!prop.getName().contains("Viewer")) {
                            report.println(pretty_prop(prop));
                            report_wiki.println(pretty_wiki_prop(prop));
                            prop.put("done_report", true);
                        }
                      }
                }
             }
             
             report.println("<br><span style='font-style: italic;'><span style='font-style: italic;'></span>Felsenstein's Lab<br /></span><a href='http://evolution.genetics.washington.edu/phylip.html'>Phylip package</a><br /><br />");
             report_wiki.println("\nFelsenstein's Lab [http://evolution.genetics.washington.edu/ link]\n");
             for (int i=0; i<treeroot.getChildCount();i++) {
                ToolboxMutableTreeNode cat=(ToolboxMutableTreeNode) treeroot.getChildAt(i);
                //--Get applications
                if (cat.getProperties().getName().startsWith("Tree - Phylip")) {
                      for (int j=0; j<cat.getChildCount();j++) {
                        ToolboxMutableTreeNode appli=(ToolboxMutableTreeNode) cat.getChildAt(j);
                        workflow_properties prop=appli.getProperties();
                        if (prop.getName().contains("Phylip")) {
                            report.println(pretty_prop(prop));
                            report_wiki.println(pretty_wiki_prop(prop));
                            prop.put("done_report", true);
                        }
                      }
                }
             } 
             //--Special dnaml-erate...
             report.println("<br><span style='font-style: italic;'>Eddy's Lab</span><br><a href='http://selab.janelia.org/software.html'>DNAML-Erate&nbsp;</a><a href='http://selab.janelia.org/software.html'>v1.0</a><br /></div><br />");
             //--Note--special for Eddy's Lab
             report_wiki.println("\nEddy's Lab\n");
             report_wiki.println("* [http://selab.janelia.org/software.html DNAML-Erate]  v1.0\n");
             
             report.println("<span style='font-weight: bold;'><br />Tree Distance</span><br /><br />");
             report_wiki.println("\nTree Distance\n");
            for (int i=0; i<treeroot.getChildCount();i++) {
                ToolboxMutableTreeNode cat=(ToolboxMutableTreeNode) treeroot.getChildAt(i);
                //--Get applications
                if (cat.getProperties().getName().startsWith("Tree - Distance")) {
                      for (int j=0; j<cat.getChildCount();j++) {
                        ToolboxMutableTreeNode appli=(ToolboxMutableTreeNode) cat.getChildAt(j);
                        workflow_properties prop=appli.getProperties();
                        prop.put("done_report", true);
                        report.println(pretty_prop(prop));
                        report_wiki.println(pretty_wiki_prop(prop));
                      }
                }
             } 
             report.println("<span style='font-weight: bold;'><br />Tree Visualization</span><br /><br />");
             report_wiki.println("\nTree Visualization\n");
            for (int i=0; i<treeroot.getChildCount();i++) {
                ToolboxMutableTreeNode cat=(ToolboxMutableTreeNode) treeroot.getChildAt(i);
                //--Get applications
                if (cat.getProperties().getName().equals("Tree")) {
                      for (int j=0; j<cat.getChildCount();j++) {
                        ToolboxMutableTreeNode appli=(ToolboxMutableTreeNode) cat.getChildAt(j);
                        workflow_properties prop=appli.getProperties();
                        if (prop.getName().contains("Viewer")) {
                            prop.put("done_report", true);
                            report.println(pretty_prop(prop));
                            report_wiki.println(pretty_wiki_prop(prop));
                        }
                      }
                }
             } 
            report.println("</td>");
            // Evolutionnary model
            report.println("<td style='vertical-align: top; text-align: center;'><span style='font-weight: bold;'><br />Evolutionary Model Inference</span><br /><br />");
            report_wiki.println("\nEvolutionary Model Inference\n");
             for (int i=0; i<treeroot.getChildCount();i++) {
                ToolboxMutableTreeNode cat=(ToolboxMutableTreeNode) treeroot.getChildAt(i);
                //--Get applications
                if (cat.getProperties().getName().startsWith("Evolutionary Model Testing")) {
                      for (int j=0; j<cat.getChildCount();j++) {
                        ToolboxMutableTreeNode appli=(ToolboxMutableTreeNode) cat.getChildAt(j);
                        workflow_properties prop=appli.getProperties();
                        prop.put("done_report", true);
                        report.println(pretty_prop(prop));
                        report_wiki.println(pretty_wiki_prop(prop));
                      }
                }
             }   
            report.println("<span style='font-weight: bold;'><br />Selective Pressure</span><br /><br />");
            report_wiki.println("\nSelective Pressure\n");
            for (int i=0; i<treeroot.getChildCount();i++) {
                ToolboxMutableTreeNode cat=(ToolboxMutableTreeNode) treeroot.getChildAt(i);
                //--Get applications
                if (cat.getProperties().getName().startsWith("Selective Pressure")) {
                      for (int j=0; j<cat.getChildCount();j++) {
                        ToolboxMutableTreeNode appli=(ToolboxMutableTreeNode) cat.getChildAt(j);
                        workflow_properties prop=appli.getProperties();
                        prop.put("done_report", true);
                        report.println(pretty_prop(prop));
                        report_wiki.println(pretty_wiki_prop(prop));
                      }
                }
             }
            report.println("<span style='font-weight: bold;'><br />Ancestral Reconstruction</span><br /><br />");
            report_wiki.println("\nAncestral Reconstruction\n");
            for (int i=0; i<treeroot.getChildCount();i++) {
                ToolboxMutableTreeNode cat=(ToolboxMutableTreeNode) treeroot.getChildAt(i);
                //--Get applications
                if (cat.getProperties().getName().startsWith("Ancestral Reconstruction")) {
                      for (int j=0; j<cat.getChildCount();j++) {
                        ToolboxMutableTreeNode appli=(ToolboxMutableTreeNode) cat.getChildAt(j);
                        workflow_properties prop=appli.getProperties();
                        prop.put("done_report", true);
                        report.println(pretty_prop(prop));
                        report_wiki.println(pretty_wiki_prop(prop));
                      }
                }
             }   
            report.println("<span style='font-weight: bold;'><br />Others</span><br /><br />");
            report_wiki.println("\nOthers\n");
            for (int i=0; i<treeroot.getChildCount();i++) {
                ToolboxMutableTreeNode cat=(ToolboxMutableTreeNode) treeroot.getChildAt(i);
                //--Get applications other
                      for (int j=0; j<cat.getChildCount();j++) {
                        ToolboxMutableTreeNode appli=(ToolboxMutableTreeNode) cat.getChildAt(j);
                        workflow_properties prop=appli.getProperties();                        
                        if (!prop.getBoolean("done_report")&&!prop.getBoolean("InternalArmadilloApplication")&&!prop.getName().equalsIgnoreCase("DNAML-Erate")) {
                            //System.out.println(prop.getBoolean("InternalArmadilloApplication"));
                            report.println(pretty_prop(prop));
                            report_wiki.println(pretty_wiki_prop(prop));
                        }
                      }
                
             }   
            report.println("</td>"); 
            //--Close the table (1)
            //report.println("</tr></tbody></table></span><a href='included_os.html'>(extented report including OS specific)</a><br /><br />");
            //--Table (2) - static
            // BLAST
            report.println("<table style='text-align: left; width: 100%; font-size: 14px;' border='1' cellpadding='0' cellspacing='0'><tbody><tr>"+
                           "<span style='border-collapse: separate; color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; font-size: medium;' class='Apple-style-span'></span><td style='background-color: silver; text-align: center; color: black;'><big><big><span style='font-weight: bold;'>BLAST</span></big></big></td>"+
                            "<span class='Apple-style-span' style='border-collapse: separate; color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; font-size: medium;'></span><td style='vertical-align: top; text-align: center;'><span style='font-weight: bold;'><br /></span>"+
                            "<div style='text-align: left;'><span class='Apple-style-span' style='border-collapse: separate; color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; font-size: medium;'>&nbsp;&nbsp;&nbsp;"+
                            "<a href='http://eutils.ncbi.nlm.nih.gov/'>NCBI** BLAST</a><br />&nbsp;&nbsp;&nbsp; <a href='http://www.ebi.ac.uk/Tools/blastall/index.html'>EBI BLAST</a> &nbsp;&nbsp;&nbsp;<a href='http://www.ebi.ac.uk/inc/help/search_help.html'></a><br />"+
                            "<span style='font-weight: bold;'></span></span><br />"+
                            "<span class='Apple-style-span' style='border-collapse: separate; color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; font-size: medium;'><span style='font-weight: bold;'></span></span></div>"+
                            "</td></tr></tbody></table><br /><br />");
            //--Web database
            report.println("<table style='text-align: left; width: 100%; font-size: 14px;' border='1' cellpadding='0' cellspacing='0'><tbody><tr>"+
                           "<span style='border-collapse: separate; color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; font-size: medium;' class='Apple-style-span'></span><td style='background-color: silver; text-align: center; color: black;'><big><big><span style='font-weight: bold;'>Database services</span></big></big></td>"+
                           "<span class='Apple-style-span' style='border-collapse: separate; color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; font-size: medium;'></span><td style='vertical-align: top; text-align: center;'><span style='font-weight: bold;'><br /></span>"+
                           "<div style='text-align: left;'><span class='Apple-style-span' style='border-collapse: separate; color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; font-size: medium;'>&nbsp;&nbsp;&nbsp;"+
                           "<a href='http://eutils.ncbi.nlm.nih.gov/'>NCBI Entrez Programming Utilities</a><br />"+
                            "&nbsp;&nbsp;&nbsp;&nbsp;<a href='http://www.ebi.ac.uk/inc/help/search_help.html'>EBI EB-eye database&nbsp;search services</a><br />"+
                            "&nbsp;&nbsp;&nbsp;<a href='http://www.ebi.ac.uk/cgi-bin/dbfetch'> EBI dbFetch</a> <br />"+
                            "<span style='font-weight: bold;'></span></span><br /><span class='Apple-style-span' style='border-collapse: separate; color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; font-size: medium;'><span style='font-weight: bold;'></span></span></div></td></tr>"+
                            "</tbody></table><br /></div></div><div style='clear: both;'><span class='Apple-style-span' style='border-collapse: separate; color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; font-size: medium;'><span class='Apple-style-span' style='font-family: helvetica,'trebuchet MS',arial,sans-serif; font-size: 11px; text-align: left; white-space: nowrap;'></span></span><span class='Apple-style-span' style='border-collapse: separate; color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; font-size: medium;'><span class='Apple-style-span' style='font-family: helvetica,'trebuchet MS',arial,sans-serif; font-size: 11px; text-align: left; white-space: nowrap;'>*&nbsp;<span class='Apple-style-span' style='border-collapse: separate; color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; font-size: medium;'><span class='Apple-style-span' style='font-family: helvetica,'trebuchet MS',arial,sans-serif; font-size: 11px; text-align: left; white-space: nowrap;'><a target='_top' href='http://www.ebi.ac.uk/' title='European Bioinformatics Institute Home Page' style='color: rgb(64, 64, 64); background-color: rgb(222, 222, 222); text-decoration: underline;'>European            Bioinformatics Institute</a><br /></span></span></span></span><span class='Apple-style-span' style='border-collapse: separate; color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; font-size: medium;'><span class='Apple-style-span' style='font-family: helvetica,'trebuchet MS',arial,sans-serif; font-size: 11px; text-align: left; white-space: nowrap;'>**</span></span> &nbsp;<span class='Apple-style-span' style='border-collapse: separate; color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; font-size: medium;'><span class='Apple-style-span' style='font-family: helvetica,'trebuchet MS',arial,sans-serif; font-size: 11px; text-align: left; white-space: nowrap;'><a target='_top' href='http://www.ncbi.nlm.nih.gov/' title='European Bioinformatics Institute Home Page' style='color: rgb(64, 64, 64); background-color: rgb(222, 222, 222); text-decoration: underline;'>National Center for Biotechnology Information </a></span></span><br /></div></div>");

            report.println(this.foot());
            report.println("</body></html>");    
            report.close();
            report_wiki.close();
           
        } catch(Exception e) {
           e.printStackTrace();
            Config.log("Error in generation application report (1) "+filename+"\n"+e.getMessage()+"\n"+e.getLocalizedMessage());            
            return false;
        }
        return true;
    }
     
    private boolean generateApplicationsReportComplex(String filename) {
        try {
            //--The tools tree: easier beacause it's already shorted
            Toolbox toolbox=new Toolbox();
            JTree applications_tree=toolbox.getApplicationTree();
            DefaultTreeModel mode=(DefaultTreeModel) applications_tree.getModel();
            ToolboxMutableTreeNode treeroot=(ToolboxMutableTreeNode) mode.getRoot();   
            
            //--Exceptions (do not put in the report)
                String[] exception_name={"Blast Download","Blast (Web Ncbi)","Create Local BlastDB",
                                         "LocalBlast","dbFetch (Web EBI)","EB-Eye (Web EBI)",
                                         "Ncbi eUtils","Fetch Sequences Ncbi","DNAML-Erate",
                                         "Custom Program _Old_","Kalign - Custom Program"};
                HashMap<String,Boolean> dict_exception_name=new HashMap <String,Boolean>();
                for (String s:exception_name) dict_exception_name.put(s, Boolean.TRUE);

                for (int i=0; i<treeroot.getChildCount();i++) {
                    ToolboxMutableTreeNode cat=(ToolboxMutableTreeNode) treeroot.getChildAt(i);
                    //--Get applications
                    if (cat.getProperties().getName().equals("Alignment")) {
                          for (int j=0; j<cat.getChildCount();j++) {
                            ToolboxMutableTreeNode appli=(ToolboxMutableTreeNode) cat.getChildAt(j);
                            workflow_properties prop=appli.getProperties();
                            if (dict_exception_name.containsKey(prop.getName())) {
                                prop.put("done_report", true);
                            }
                          }
                    }
                } //--End exception
                
            //--HTML
            Util report=new Util(filename);
            System.out.println(filename);
            report.println("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>");
            report.println(this.generateTopApplicationReport("Included Applications - (Extended)"));
            //--Report style 1
            //-- Table (1)            
            report.println("<table style='text-align: left; width: 100%;' border='1' cellpadding='0' cellspacing='0'>"+
                            "<tbody><tr>"+
                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Categories</span></big></td>"+
                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Internal</span></big></td>"+
                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Name</span></big></td>"+
                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Version</span></big></td>"+
                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Website</span></big></td>"+
                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Help</span></big></td>"+
                    "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Sample</span></big></td>"+
                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'><a title='Description'>Desc</a></span></big></td>"+
                    "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'><a title='Publication'>Publ</a></span></big></td>"+
                            "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Windows</span></big></td>"+
                           "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>MacOSX</span></big></td>"+
                           "<td style='background-color: silver; text-align: center; color: black;'><big><span style='font-weight: bold;'>Linux</span></big></td>");
            report.println("</tr>"); 
                      
            //--Report style 2
            //report.println("<table border='1'><thead> <TR><TH SCOPE=colgroup COLSPAN=4>Applications</TH><TH SCOPE=colgroup COLSPAN=3>Operating system</TH></TR><TR><TH SCOPE=col ROWSPAN=2>Type</TH><TH SCOPE=col ROWSPAN=2>Name</TH><TH SCOPE=col ROWSPAN=2>Version</TH><TH SCOPE=col ROWSPAN=2>Website</TH>      <TH SCOPE=col>Windows</TH>      <TH SCOPE=col>MAC OSX</TH>      <TH SCOPE=col>Centos (Linux)</TH></TR></thead>");
            //report.println("<tbody>");
            
                  
           //--Get categories
           if (config.getBoolean("debug")) System.out.println("Generating applications reports");             
           for (int i=0; i<treeroot.getChildCount();i++) {
                ToolboxMutableTreeNode cat=(ToolboxMutableTreeNode) treeroot.getChildAt(i);
                //--Get applications
                if (config.getBoolean("debug"))System.out.println(cat.getProperties().getName());
                
                
                for (int j=0; j<cat.getChildCount();j++) {
                    ToolboxMutableTreeNode appli=(ToolboxMutableTreeNode) cat.getChildAt(j);
                    workflow_properties prop=appli.getProperties();
                    //--Report only the external program for now...
                    
                        
                        report.println("<TR style='background-color: white; text-align: center; color: black;'><TD>"+cat.getProperties().getName()+"</TD>"+                                      
                                       "<TD>"+(prop.getBoolean("InternalArmadilloApplication")?"X":" ")+"</TD>"+
                                       "<TD>"+prop.getName()+"</TD>"+                                       
                                        "<TD>"+(prop.isSet("Version")?prop.get("Version"):prop.getBoolean("InternalArmadilloApplication")?"NA":"")+"</TD>"+
                                       "<TD>"+(prop.getBoolean("InternalArmadilloApplication")?"":prop.isSet("Website")?"<a href='"+prop.get("Website")+"'>web</a>":"")+"</TD>"+
                                       (helpFound(prop)?"<TD BGCOLOR='lime'>":"<TD BGCOLOR='red'>") +(helpFound(prop)?"<a title='"+config.dataPath()+File.separator+"help"+File.separator+prop.getName()+".html"+"' href='"+config.dataPath()+File.separator+"help"+File.separator+prop.getName()+".html"+"'>X</a>":"")+"</TD>"+
                                       (sampleWorkflowFound(prop)?"<TD BGCOLOR='lime'>":"<TD BGCOLOR='red'>") +(sampleWorkflowFound(prop)?"<a title='"+prop.get("SampleWorkflow")+"'>X</a>":"")+"</TD>"+
                                       (prop.isSet("Description")?"<TD BGCOLOR='lime'>":"<TD BGCOLOR='red'>") +(prop.isSet("Description")?"<a title='"+prop.get("Description")+"'>X</a>":"")+"</TD>"+
                                       (prop.isSet("Publication")?"<TD BGCOLOR='lime'>":"<TD BGCOLOR='red'>") +(prop.isSet("Publication")?"<a title='"+prop.get("Publication")+"'>X</a>":"")+"</TD>"+
                                        (Util.FileExists(prop.get("Executable"))?"<TD BGCOLOR='lime'>":"<TD BGCOLOR='red'>") +(prop.isSet("Executable")?"<a title='"+prop.get("Executable")+"'>X</a>":"")+"</TD>"+
                                       (Util.FileExists(prop.get("ExecutableMACOSX"))?"<TD BGCOLOR='lime'>":"<TD BGCOLOR='red'>")+(prop.isSet("ExecutableMacOSX")?"<a title='"+prop.get("ExecutableMacOSX")+"'>X</a>":"")+"</TD>"+
                                       (Util.FileExists(prop.get("ExecutableLinux"))?"<TD BGCOLOR='lime'>":"<TD BGCOLOR='red'>")+(prop.isSet("ExecutableLinux")?"<a title='"+prop.get("ExecutableLinux")+"'>X</a>":"")+"</TD>"
                                      +"</TR>");
                       
                    if (config.getBoolean("debug")) System.out.println("\t"+appli.getProperties().getName()+"\t"+(prop.isSet("version")?prop.get("version"):"")+"\t"+(prop.isSet("Website")?prop.get("Website"):"")+"\t"+(prop.isSet("Executable")?prop.getExecutable():""));
                }
            }          
            report.println("</tbody></table></div></div></div>");
            report.println(this.foot());
            report.println("</body></html>");    
            report.close();   
            report.close();
        } catch(Exception e) {
           e.printStackTrace();
            Config.log("Error in generation application report (2) "+filename+"\n"+e.getMessage()+"\n"+e.getLocalizedMessage());            
            return false;
        }
        return true;
    }
     
    /**
     * Search if there is help found for this program
     * @param prop
     * @return 
     */
    public boolean helpFound(workflow_properties prop) {
        //--See also editors/HelpEditor
        return Util.FileExists(config.dataPath()+File.separator+"help"+File.separator+prop.getName()+".html");     
    }
    
       /**
     * Search if there is sample workflow found for this program
     * @param prop
     * @return 
     */
    public boolean sampleWorkflowFound(workflow_properties prop) {
        if (!prop.isSet("SampleWorkflow")) return false;
        String filename=prop.get("SampleWorkflow");
        File f=new File(filename);                            
        filename=config.dataPath()+File.separator+"help"+File.separator+f.getName();
        return (Util.FileExists(filename));        
    }
    
    
     /**
      * Function to print, when need an url from the properties
      * @param prop
      * @return A string with or without html link...
      */
     public String pretty_prop(workflow_properties prop) {
         if (prop.isSet("Website")) {
                           return ("<div style='text-align: left;'><a href='"+prop.get("Website")+"' target='_blank'>"+prop.getName()+(prop.isSet("Version")?"&nbsp;v"+prop.get("Version"):"")+"</a>"+((!prop.isSet("ExecutableMacOSX")||prop.getExecutableMacOSX().isEmpty())?"&nbsp;(Windows only)":"")+"</div>");                           
                        } else {
                            return ("<div style='text-align: left;'>"+prop.getName()+(prop.isSet("Version")?"&nbsp;v"+prop.get("Version"):"")+((!prop.isSet("ExecutableMacOSX")||prop.getExecutableMacOSX().isEmpty())?"&nbsp;(Windows only)":"")+"</div>");
                        }
     }
    
     /**
      * Function to print to the wiki, when need an url from the properties
      * *test - v 1.0 - [http://www.example.com link title]
      * @param prop
      * @return A string with or without html link...
      */
     public String pretty_wiki_prop(workflow_properties prop) {
         if (prop.isSet("Website")) {
                            String website=prop.get("Website");   
                            if (website.startsWith("https")) website=website.replaceAll("https", "http");
                            if (!website.startsWith("http")) website="http://"+website;
                            return ("* ["+website+" "+prop.getName()+"] "+(prop.isSet("Version")?" v"+prop.get("Version"):"")+((!prop.isSet("ExecutableMacOSX")||prop.getExecutableMacOSX().isEmpty())?" (Windows only)":"")+"\n");                           
                        } else {
                            return ("* "+prop.getName()+(prop.isSet("Version")?" v"+prop.get("Version"):"")+((!prop.isSet("ExecutableMacOSX")||prop.getExecutableMacOSX().isEmpty())?" (Windows only)":"")+"\n");
                        }
     }
     
     /**
      * Return an HTML String with System information...
      * @return 
      */ 
     public String printSystemInfo() {
        StringBuilder st=new StringBuilder();
        st.append("<br>Compiler found: "+config.isCompilerFound()+"<br>");
        st.append("Developper mode: "+config.isDevelopperMode()+"<br>");
        st.append("Windows :"+SystemUtils.IS_OS_WINDOWS+"<br>");
        st.append("MacOSX :"+SystemUtils.IS_OS_MAC_OSX+"<br>");
        st.append("Linux :"+SystemUtils.IS_OS_LINUX+"<br>");        
        st.append("Memory: "+PrintMemory()+"<br >");         
        return st.toString();
    }    
     
     public String PrintMemory() {
        String stri="Armadillo version "+config.get("version")+"\n "+"\n"+
                    "System allocated memory: "+Runtime.getRuntime().totalMemory()/Mb+" MB\nSystem free memory: "+Runtime.getRuntime().freeMemory()/Mb+" MB\n"+
                    "System total core: "+Runtime.getRuntime().availableProcessors()+"\n";
         //Config.log(stri);
         return stri;
    } 
     
    /**
     * Generate index, with a summary of the content of the project
     * Head+body+foot
     */
    private void generateReport(Project project, Workflows workflow, String filename){
        //==1. Is workflow loaded?
        if (workflow.getWorkflow()==null) return;

        File index = new File (config.resultsDir()+File.separator+projectName+File.separator+"index.html");
        try{
            PrintWriter printInFile = new PrintWriter(new BufferedWriter(new FileWriter (filename)));
            printInFile.print(headIndex());
                printInFile.println("<body>");               
                printInFile.println(util.getRessource("style.css"));
                printInFile.print(
                generateTopIndex("")+
                generateWorkflowInfo());
                printInFile.println("<div class=\"post\">\n"+
                                    "<div class=\"entry\">\n"+
                                    "<div align=\"left\" style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\"></div><br>"+
                                    "			<h2 class=\"title\">Workflow Execution</h2>");
                //--Generate files (Input-Output)
                this.generateWorkflowExecutionFile(workflow.getWorkflow(), workflow);
                //--Generates HTML files
                if (work.getRunWorkflowForWorkflows()==null||work.getRunWorkflowForWorkflows().getExecution_workflow_id().size()==1) {
                    printInFile.print(generateWorkflowExecution(workflow.getWorkflow(), workflow));
                } else {
                    //==original
                    Workflows workflow_to_generate=new Workflows(new armadillo_workflow());
                        workflow_to_generate.loadFromDatabase(work.getRunWorkflowForWorkflows().getOriginal_workflow_id());
                        workflow_to_generate.StringToWorkflow();
                        printInFile.println("<table style=\"text-align: left; width: 100%;\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\"><thead>Original Workflow</thead><tbody><tr><td>");
                        printInFile.println("<br>");
                        printInFile.print(generateWorkflowExecution(workflow_to_generate.workflow, workflow_to_generate));
                        printInFile.println("</td></tr><tbody></table><br>");

                    //==other
                    for (int id:work.getRunWorkflowForWorkflows().getExecution_workflow_id()) {
                        Workflows workflow_to_generate2=new Workflows(new armadillo_workflow());
                        workflow_to_generate2.loadFromDatabase(id);
                        workflow_to_generate2.StringToWorkflow();
                        if (id==work.getCurrentWorkflows().getId()) {
                            printInFile.println("<table style=\"text-align: left; width: 100%;\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\"><thead>Currently Displayed Workflow</thead><tbody><tr><td>");
                            printInFile.println("<br>");
                        }
                            printInFile.print(generateWorkflowExecution(workflow_to_generate2.workflow, workflow_to_generate2));
                         if (id==work.getCurrentWorkflows().getId()) {
                            printInFile.println("</td></tr><tbody></table><br>");
                        }

                    }
                }
                printInFile.println(   "<div style=\"clear: both;\">&nbsp;</div>\n"+"</div>\n");
                printInFile.print(generateUserInfo(project)+ foot());
          printInFile.close();
        } catch(Exception e) {e.printStackTrace();}
    }

      /**
     * Generate index, with a summary of the content of the project
     * Head+body+foot
     */
    private void generateLongReport(Project project, Workflows workflow, String filename){
        //==1. Is workflow loaded?
        if (workflow.getWorkflow()==null) return;

        File index = new File (config.resultsDir()+File.separator+projectName+File.separator+"index.html");
        try{
            PrintWriter printInFile = new PrintWriter(new BufferedWriter(new FileWriter (filename)));
            printInFile.print(headIndex());
                printInFile.println("<body>");
                printInFile.println(util.getRessource("style.css"));
                printInFile.print(
                generateTopIndex("")+
                generateWorkflowInfo());
                printInFile.println("<div class=\"post\">\n"+
                                    "<div class=\"entry\">\n"+
                                    "<div align=\"left\" style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\"></div><br>"+
                                    "			<h2 class=\"title\">Workflow Execution</h2>");
                //--Generate files (Input-Output)
                this.generateWorkflowExecutionFile(workflow.getWorkflow(), workflow);
                //--Generates HTML files
                if (work.getRunWorkflowForWorkflows()==null||work.getRunWorkflowForWorkflows().getExecution_workflow_id().size()==1) {
                    printInFile.print(generateLongWorkflowExecution(workflow.getWorkflow(), workflow));
                } else {
                    //==original
                    Workflows workflow_to_generate=new Workflows(new armadillo_workflow());
                        workflow_to_generate.loadFromDatabase(work.getRunWorkflowForWorkflows().getOriginal_workflow_id());
                        workflow_to_generate.StringToWorkflow();
                        printInFile.println("<table style=\"text-align: left; width: 100%;\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\"><thead>Original Workflow</thead><tbody><tr><td>");
                        printInFile.println("<br>");
                        printInFile.print(generateLongWorkflowExecution(workflow_to_generate.workflow, workflow_to_generate));
                        printInFile.println("</td></tr><tbody></table><br>");

                    //==other
                    for (int id:work.getRunWorkflowForWorkflows().getExecution_workflow_id()) {
                        Workflows workflow_to_generate2=new Workflows(new armadillo_workflow());
                        workflow_to_generate2.loadFromDatabase(id);
                        workflow_to_generate2.StringToWorkflow();
                        if (id==work.getCurrentWorkflows().getId()) {
                            printInFile.println("<table style=\"text-align: left; width: 100%;\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\"><thead>Currently Displayed Workflow</thead><tbody><tr><td>");
                            printInFile.println("<br>");
                        }
                            printInFile.print(generateLongWorkflowExecution(workflow_to_generate2.workflow, workflow_to_generate2));
                         if (id==work.getCurrentWorkflows().getId()) {
                            printInFile.println("</td></tr><tbody></table><br>");
                        }

                    }
                }
                printInFile.println(   "<div style=\"clear: both;\">&nbsp;</div>\n"+"</div>\n");
                printInFile.print(generateUserInfo(project)+ foot());
          printInFile.close();
        } catch(Exception e) {e.printStackTrace();}
    }

    public String generateUserInfo(Project project) {
        return "		<div class=\"post\">\n"+
                "			<div class=\"entry\">\n"+
                "<div align=\"left\" style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\"></div><br>"+
                "				<h2 class=\"title\">User Informations</h2>\n"+                
                "				<table width=\"841\" border=\"0\">\n"+
                "					<tr>\n"+
                "                                           <td width=\"174\">Author:</td>\n"+
                "                                           <td width=\"651\">"+project.getAuthor()+"</td>\n"+
                "                                       </tr>\n"+
                "					<tr>\n"+
                "                                           <td width=\"174\">Email:</td>\n"+
                "                                           <td width=\"651\"><a href=mailto:"+project.getEmail()+">"+project.getEmail()+"</a></td>\n"+
                "                                       </tr>\n"+
                "					<tr>\n"+
                "                                           <td width=\"174\">Institution:</td>\n"+
                "                                           <td width=\"651\">"+project.getInstitution()+"</td>\n"+
                "                                       </tr>\n"+
                "                                            "+(project.getAuthor().isEmpty()?"<tr><td></td><td><small><b>[Note: this can be set in the File -> Preference menu]</b></small></td></tr>":"")+
                "				</table>\n"+
                "           </div>\n"+
                "	</div>\n";
    }


    public String generateWorkflowExecution(armadillo_workflow armadillo, Workflows workflow) {
        StringBuilder st=new StringBuilder();
         st.append(
                 //"<div id=\"page\">\n"+
                "\n"+
//                "	<div id=\"content\">\n"+
//                "		<div class=\"post\">\n"+
                "&nbsp;&nbsp;[ "+workflow.getName()+" ]"+
                "				<ul>\n");

         int count=0;
            for (workflow_object obj:armadillo.workflow.outputExecution()) {
                workflow_properties properties=obj.getProperties();

                if (properties.get("ObjectType").equals("Program")) {
                    count++;
                    properties.getStatus();
                  
                            st.append("					<li><strong>"+count+" "+properties.getName()+" ["+getHtmlStatus(properties)+"]"+"</strong>\n"+
                           "						<ul>\n"
                    );
                    for (String output_type:properties.Outputed()) {
                        Vector<Integer> ids=properties.getOutputID(output_type, null);
                        for (int id:ids) {
                            if (id!=0) {
                                 st.append(
                                    "                                                       <li><a href=\""+output_type+properties.getID().replaceAll(" ", "_")+".html\">"+(output_type.equals("OutputText")?"Software output":output_type)+"</a></li>\n");
                                Output output=new Output();
                                output.setType(output_type);
                                output.setTypeid(id);
                                Object bio=output.getBiologic();
                                generateBiologicalContentPage(output_type+properties.getID(),bio);
                            }
                        }
                    }
                    if (properties.Outputed().size()==0) {
                                st.append(
                                    "                                                       <li>No outputs</li>\n");
                        }
                    st.append(
//                        "                                                   </ol>\n"+
//                        "                                                   </li>\n"+
                        "						</ul>\n");
                        //"					</li>\n");
               }
                 if (properties.get("ObjectType").equals("OutputDatabase")) {
                    count++;
                     properties.getStatus();
                            st.append("					<li><strong>"+count+" "+properties.getName()+" ["+properties.getDescription()+"]</strong>\n"+
                           "						<ul>\n");
                        //"                                                   </li>\n"+
                        //"                                                   <li>Outputs</li>\n"+
                        //"                                                   <ol>\n");
                    for (String output_type:properties.Outputed()) {
                        Vector<Integer> ids=properties.getOutputID(output_type, null);
                        for (int id:ids) {
                            if (id!=0) {
                                 st.append(
                                    "                                                       <li><a href=\""+output_type+properties.getID().replaceAll(" ", "_")+".html\">"+output_type+"</a></li>\n");
                                Output output=new Output();
                                output.setType(output_type);
                                output.setTypeid(id);
                                Object bio=output.getBiologic();
                                generateBiologicalContentPage(output_type+properties.getID(),bio);
                            }
                        }
                    }
                    if (properties.Outputed().size()==0) {
                                st.append(
                                    "                                                       <li>No outputs</li>\n");
                        }
                    st.append(
//                        "                                                   </ol>\n"+
//                        "                                                   </li>\n"+
                        "						</ul>\n");

//                        "					</li>\n");
               }
           }
           st.append(
                "				</ul>\n"+
                //"		</div>\n"+
                //"	</div>\n"+
                //"	</div>\n"+
                "\n");
           return st.toString();
    }

     public String generateLongWorkflowExecution(armadillo_workflow armadillo, Workflows workflow) {
        StringBuilder st=new StringBuilder();
         st.append(             
                "\n"+
                "&nbsp;&nbsp;[ "+workflow.getName()+" ]"+
                "				<ul>\n");

         int count=0;
            for (workflow_object obj:armadillo.workflow.outputExecution()) {
                workflow_properties properties=obj.getProperties();

                if (properties.get("ObjectType").equals("Program")) {
                    count++;
                    properties.getStatus();
                            st.append("					<li><strong>"+count+" "+properties.getName()+" ["+getHtmlStatus(properties)+"]"+"</strong>\n"+
                           "						<ul>\n"
                    );
                    for (String output_type:properties.Outputed()) {
                        Vector<Integer> ids=properties.getOutputID(output_type, null);
                        for (int id:ids) {
                            if (id!=0) {
                                 st.append(
                                    "                                                       <li><a href=\""+output_type+properties.getID().replaceAll(" ", "_")+".html\">"+(output_type.equals("OutputText")?"Software output":output_type)+"</a></li>\n");
                                Output output=new Output();
                                output.setType(output_type);
                                output.setTypeid(id);
                                Object bio=output.getBiologic();
                                generateBiologicalContentPage(output_type+properties.getID(),bio);
                                st.append(generateLongBiologicalContent(bio));
                                
                                
                            }
                        }
                    }
                    if (properties.Outputed().size()==0) {
                                st.append(
                                    "                                                       <li>No outputs</li>\n");
                        }
                    st.append(
//                        "                                                   </ol>\n"+
//                        "                                                   </li>\n"+
                        "						</ul>\n");
                        //"					</li>\n");
               }
                 if (properties.get("ObjectType").equals("OutputDatabase")) {
                    count++;
                     properties.getStatus();
                            st.append("					<li><strong>"+count+" "+properties.getName()+" ["+properties.getDescription()+"]</strong>\n"+
                           "						<ul>\n");
                        //"                                                   </li>\n"+
                        //"                                                   <li>Outputs</li>\n"+
                        //"                                                   <ol>\n");
                    for (String output_type:properties.Outputed()) {
                        Vector<Integer> ids=properties.getOutputID(output_type, null);
                        for (int id:ids) {
                            if (id!=0) {
                                 st.append(
                                    "                                                       <li><a href=\""+output_type+properties.getID().replaceAll(" ", "_")+".html\">"+output_type+"</a></li>\n");
                                Output output=new Output();
                                output.setType(output_type);
                                output.setTypeid(id);
                                Object bio=output.getBiologic();
                                generateBiologicalContentPage(output_type+properties.getID(),bio);
                                st.append(generateLongBiologicalContent(bio));
                            }
                        }
                    }
                    if (properties.Outputed().size()==0) {
                                st.append(
                                    "                                                       <li>No outputs</li>\n");
                        }
                    st.append(
//                        "                                                   </ol>\n"+
//                        "                                                   </li>\n"+
                        "						</ul>\n");

//                        "					</li>\n");
               }
           }
           st.append(
                "				</ul>\n"+
                "\n");
           return st.toString();
    }

      /**
       * This generate all the input-output files for the workflow and a web page
       * @param armadillo
       * @param workflow
       * @return
       */
      public String generateWorkflowExecutionFile(armadillo_workflow armadillo, Workflows workflow) {
        StringBuilder st=new StringBuilder();
         int count=0;
            for (workflow_object obj:armadillo.workflow.outputExecution()) {
                workflow_properties properties=obj.getProperties();

                if (properties.get("ObjectType").equals("Program")) {
                    count++;
                    properties.getStatus();
                            st.append(count+" "+properties.getName()+" "+getHtmlStatus(properties));

                    //--
                    String filename= count+"_"+properties.getName()+"_"+properties.getID();
                    filename = filename.replaceAll(" ", "_");
                    String results_dir=config.resultsDir()+File.separator+projectName+File.separator+"results"+File.separator+filename;
                    config.createDir(results_dir);
                    config.createDir(results_dir+File.separator+"input");
                    config.createDir(results_dir+File.separator+"output");
                    //--Note: this might failed if we output All!
                    for (String output_type:properties.Outputed()) {
                        Vector<Integer> ids=properties.getOutputID(output_type, null);
                        for (int id:ids) {
                            if (id!=0) {
                                //st.append(" "+output_type+properties.getID().replaceAll(" ", "_")+".html\">"+(output_type.equals("OutputText")?"Software output":output_type)+"</a></li>\n");
                                Output output=new Output();
                                output.setType(output_type);
                                output.setTypeid(id);
                                Biologic bio=output.getBiologic();
                                String filename2=results_dir+File.separator+"output"+File.separator+output_type+properties.getID().replaceAll(" ", "_")+".txt";
                                this.generateBiologicalContentFile(filename2, bio);
                            }
                        }
                    }
                    //--Note: this might failed if we input All!
                      for (String input_type:properties.Inputed()) {
                        Vector<Integer> ids=properties.getInputID(input_type, null);
                        for (int id:ids) {
                            if (id!=0) {
                                 //st.append(" "+input_type+properties.getID().replaceAll(" ", "_")+".html\">"+(input_type.equals("OutputText")?"Software output":input_type)+"</a></li>\n");
                                Output output=new Output();
                                output.setType(input_type);
                                output.setTypeid(id);
                                Biologic bio=output.getBiologic();
                                String filename2=results_dir+File.separator+"input"+File.separator+input_type+properties.getID().replaceAll(" ", "_")+".txt";
                                this.generateBiologicalContentFile(filename2, bio);
                                
                            }
                        }
                    }
                }
//                 if (properties.get("ObjectType").equals("OutputDatabase")) {
//                    count++;
//                    for (String output_type:properties.Outputed()) {
//                        Vector<Integer> ids=properties.getOutputID(output_type, null);
//                        for (int id:ids) {
//                            if (id!=0) {
//                                 st.append(output_type+properties.getID().replaceAll(" ", "_")+".html\">"+output_type+"</a></li>\n");
//                                Output output=new Output();
//                                output.setType(output_type);
//                                output.setTypeid(id);
//                                Object bio=output.getBiologic();
//                                generateBiologicalContentPage(output_type+properties.getID(),bio);
//                            }
//                        }
//                    }
//                 }

            } //--End Workflow List
           return st.toString();
    }

     public void generateProjectContentPage(armadillo_workflow armadillo, String filename){

        File index = new File (filename);
        try{
            PrintWriter printInFile = new PrintWriter(new BufferedWriter(new FileWriter (index,false)));
            printInFile.print(
                headIndex()+
                generateTopIndex("Project content")+
                generateWorkflowInfo()+
                generateWorkflowExecution(armadillo, armadillo.workbox.getCurrentWorkflows())+
                foot()
                );
            printInFile.close();
        }
        catch (java.io.IOException e){
            Config.log("Unable to write in file");
        }
    }

/**
     * Generate index, with a summary of the content of the project
     * Head+body+foot
     */
    private void generateWorkflowPage(){

        File index = new File (config.resultsDir()+File.separator+projectName+File.separator+"workflow.html");
        try{
            PrintWriter printInFile = new PrintWriter(new BufferedWriter(new FileWriter (index,false)));
            printInFile.print(
                headIndex()+
                generateTopIndex("Workflow")+
                foot()
                );
            printInFile.close();
        }
        catch (java.io.IOException e){
            Config.log("Unable to write in file");
        }
    }

    /**
     * This generate a string (html) describing the stated of the properties object
     * @param prop
     * @return
     */
    private String getHtmlStatus(workflow_properties prop) {
        int status=prop.getStatus();
        switch(status) {
            case RunProgram.status_nothing:         return "idle";
            case RunProgram.status_done:            return "<span style=\"color: rgb(51, 204, 0);\">done</span>";
            case RunProgram.status_BadRequirements: return "<span style=\"color: red;\">Error: "+prop.getStatusString()+"</span>";
            case RunProgram.status_error: return "<span style=\"color: red;\">Error: "+prop.getStatusString()+"</span>";
            case RunProgram.status_programnotfound: return "<span style=\"color: red;\">Error: "+prop.getStatusString()+"</span>";
            case RunProgram.status_runningclassnotfound: return "<span style=\"color: red;\">Error: "+prop.getStatusString()+"</span>";
            case RunProgram.status_idle: return "idle";
            case RunProgram.status_running: return "<span style=\"color: rgb(51, 204, 0);\">running</span>";
            default : return "idle";
        }      
    }

    /**
     * Generate new page, without menus
     * @param filename
     * @param content
     */
    public String generateBiologicalContentPage(String file, Object obj){
        file = file.replaceAll(" ", "_");
        File f2 = new File (config.resultsDir()+File.separator+projectName+File.separator+file+".html");
        try{
            PrintWriter printInFile = new PrintWriter(new BufferedWriter(new FileWriter (f2,false)));
            printInFile.print(
                        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"+
                        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"+
                        "<head>\n"+
                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"+
                        "<title>Armadillo Report : "+projectName+"</title>\n"+
                        "<link rel=\"shortcut icon\" href=\"favicon.ico\"/>"+
                        "</head>\n"+util.getRessource("style.css")+"<div id=\"bordure\" style=\"margin-left: 1cm; margin-top: 1cm; margin-bottom: 1cm;\">"+
                        "<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">"+(((Biologic)obj).getBiologicType().equals("OutputText")?"Software output":((Biologic)obj).getBiologicType())+"&nbsp;|&nbsp;"+((Biologic)obj).getName()+"</div>"+
                        "<span class=\"paml\">"+
                        ((Biologic)obj).toHtml().replaceAll("\n", "<br>").replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")+"</span><br><br>"
                    ); //Mettre contenu ici      
               
            
            
                    String type=((Biologic)obj).getBiologicType();
                    if (type.equalsIgnoreCase("Sequence")||type.equalsIgnoreCase("MultipleSequences")||type.equalsIgnoreCase("Alignment")) {
                        printInFile.println("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">Fasta</div>");
                        printInFile.println(((Biologic)obj).getFasta().replaceAll("\n", "<br>").replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")+"<br>");
                        //printInFile.println("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">Phylip</div>");
                        //printInFile.println(((Biologic)obj).getPhylip().replaceAll("\n", "<br>").replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")+"<br>");
                    }
                    if (type.equalsIgnoreCase("Tree")) {
                        scriptree scr=new scriptree();
                        convertSVGtoPNG cct=new convertSVGtoPNG();
                        Tree t=(Tree)((Biologic)obj);
                        newick_tree tree=new newick_tree();
                        try {
                            tree.parseNewick(t.getTree());
                            printInFile.println("Total species : "+tree.getTotalLeaf());
                        } catch(Exception e){}
                        ImageFile img=scr.scriptree(t, tree.getTotalLeaf());
                        String imgfilename=config.resultsDir()+File.separator+projectName+File.separator+"tree_"+img.hashCode()+".svg";                                                
                        img.Output(config.resultsDir()+File.separator+projectName+File.separator+"tree_"+img.hashCode()+".svg");
                        cct.convert(imgfilename);
                        printInFile.println("<a href='tree_"+img.hashCode()+".svg.png'>Tree in PNG format</a><div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">Input tree</div>");
                        printInFile.println("<br>Created with ScripTree v1.7 <a href=\"http://phylo.lirmm.fr/scriptree/\">Website</a>");
                        printInFile.println("<embed id='svg' src=\"tree_"+img.hashCode()+".svg"+"\" width=\"600\" height=\"1200\" type=\"image/svg+xml\" pluginspage=\"http://www.adobe.com/svg/viewer/install/\" />");                       
                        
                    }
                    if (type.equalsIgnoreCase("MultipleTrees")) {
                        for (Tree t:((MultipleTrees)((Biologic)obj)).getTree()) {
                            scriptree scr=new scriptree();
                            ImageFile img=scr.scriptree(t);
                              newick_tree tree=new newick_tree();
                            try {
                                tree.parseNewick(t.getTree());
                                printInFile.println(tree.PrintOut());
                            } catch(Exception e){}
                            img.Output(config.resultsDir()+File.separator+projectName+File.separator+"tree_"+img.hashCode()+".svg");
                            printInFile.println("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">Input tree</div>");
                            printInFile.println("<br>Created with ScripTree v1.7 <a href=\"http://phylo.lirmm.fr/scriptree/\">Website</a>");
                            printInFile.println("<embed src=\""+config.resultsDir()+File.separator+projectName+File.separator+"tree_"+img.hashCode()+".svg"+"\" width=\"600\" height=\"1200\" type=\"image/svg+xml\" pluginspage=\"http://www.adobe.com/svg/viewer/install/\" />");
                        }
                    }

                    printInFile.println("</div>\n");
            printInFile.close();
            StringBuilder st=new StringBuilder();
            String[] str=Util.InputFile(config.resultsDir()+File.separator+projectName+File.separator+file+".html");
            for (String str2:str) st.append(str2);
            return st.toString();

        }
        catch (java.io.IOException e){
            Config.log("Unable to write in file");
            return "";
        }
    }

      /**
     * Generate new page, without menus
     * @param filename
     * @param content
     */
    public String generateLongBiologicalContent(Object obj){
        StringBuilder st=new StringBuilder();
        try{           
            st.append(                      
                        ((Biologic)obj).toHtml().replaceAll("\n", "<br>").replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")+"<br><br>"
                    ); //Mettre contenu ici
                    String type=((Biologic)obj).getBiologicType();
                    if (type.equalsIgnoreCase("Sequence")||type.equalsIgnoreCase("MultipleSequences")||type.equalsIgnoreCase("Alignment")) {
                        st.append("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">Fasta</div>");
                        st.append(((Biologic)obj).getFasta().replaceAll("\n", "<br>").replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")+"<br>");
                        //st.append("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">Phylip</div>");
                        //st.append(((Biologic)obj).getPhylip().replaceAll("\n", "<br>").replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")+"<br>");
                    }
                    if (type.equalsIgnoreCase("Tree")) {
                        scriptree scr=new scriptree();
                        Tree t=(Tree)((Biologic)obj);
                        newick_tree tree=new newick_tree();
                        try {
                            tree.parseNewick(t.getTree());
                           st.append("Total species : "+tree.getTotalLeaf());
                        } catch(Exception e){}
                        ImageFile img=scr.scriptree(t);
                        img.Output(config.resultsDir()+File.separator+projectName+File.separator+"tree_"+img.hashCode()+".svg");

                        st.append("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">Input tree</div>");
                        st.append("<br>Created with ScripTree v1.7 <a href=\"http://phylo.lirmm.fr/scriptree/\">Website</a>");
                        st.append("<embed src=\""+config.resultsDir()+File.separator+projectName+File.separator+"tree_"+img.hashCode()+".svg"+"\" width=\"600\" height=\"1200\" type=\"image/svg+xml\" pluginspage=\"http://www.adobe.com/svg/viewer/install/\" />");

                    }
                    if (type.equalsIgnoreCase("MultipleTrees")) {
                        for (Tree t:((MultipleTrees)((Biologic)obj)).getTree()) {
                            scriptree scr=new scriptree();
                            ImageFile img=scr.scriptree(t);
                              newick_tree tree=new newick_tree();
                            try {
                                tree.parseNewick(t.getTree());
                                st.append(tree.PrintOut());
                            } catch(Exception e){}
                            img.Output(config.resultsDir()+File.separator+projectName+File.separator+"tree_"+img.hashCode()+".svg");
                            st.append("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">Input tree</div>");
                            st.append("<br>Created with ScripTree v1.7 <a href=\"http://phylo.lirmm.fr/scriptree/\">Website</a>");
                            st.append("<embed src=\""+config.resultsDir()+File.separator+projectName+File.separator+"tree_"+img.hashCode()+".svg"+"\" width=\"600\" height=\"1200\" type=\"image/svg+xml\" pluginspage=\"http://www.adobe.com/svg/viewer/install/\" />");
                        }
                    }

                    //st.append("</div>\n");
            return st.toString();        
        } catch (Exception e){
            Config.log("Unable create report for "+obj.toString());
            return "";
        }
    }

    public void generateBiologicalContentFile(String filename, Biologic obj){
        filename = filename.replaceAll(" ", "_");
        
        File f2 = new File (filename);
        try{
            PrintWriter printInFile = new PrintWriter(new BufferedWriter(new FileWriter (f2,false)));
            printInFile.print(obj.toString()); //Mettre contenu ici
            printInFile.close();
        }
        catch (java.io.IOException e){
            Config.log("Unable to write in file");
        }

    }

    /**
     * Head of each page
     * @return
     */
    private static String headIndex(){
        String head =
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"+
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"+
                "<head>\n"+
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"+
                "<title>Armadillo Report : "+projectName+"</title>\n"+
                "<meta name=\"keywords\" content=\"\" />\n"+
                "<meta name=\"description\" content=\"\" />\n"+
                //"<link href=\"css/styles.css\" rel=\"stylesheet\" type=\"text/css\" />\n"+
                "<link rel=\"shortcut icon\" href=\"favicon.ico\"/>"+
                "</head>\n"+
                "\n";
        return head;
    }

    /**
     * Banner and menu
     * @return
     */
    public String generateTopIndex(String type){
        String top =
                "<div id=\"logo\">\n"+
                "<h1><img src=\"images/logo.png\" alt=\"\" width=\"200\" height=\"109\" />" +                
                "<a href=\"index.html\">Armadillo Report </a></h1>\n"+
                "<br><br>\n"+
                "</div>\n"+                
                "<div id=\"page\">\n"+                
                "<div align=\"left\" style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\"></div><br>";
        return top;
    }

    public String generateTopApplicationReport(String title) {
        String top=
                "<html xmlns='http://www.w3.org/1999/xhtml'><head>"+
                "<meta http-equiv='Content-Type' content='text/html; charset=utf-8' /><title>Armadillo v1.1 : "+title+"</title>"+
                "<meta name='keywords' content='' />"+
                "<meta name='description' content='' />"+
                "<link href='css/styles.css' rel='stylesheet' type='text/css' />"+
                "<link rel='shortcut icon' href='favicon.ico' /></head>"+
                "<script type='text/javascript' src='/armadillo/js/jquery-1.7.1.min.js'></script><script type='text/javascript' src='/armadillo/js/jquery-ui-1.8.17.custom.min.js'></script/><link type='text/css' href='/armadillo/css/flick/jquery-ui-1.8.17.custom.css' rel='stylesheet' />"+
                "<script type='text/javascript'>  $(document).ready(function(){ $( '#dialog' ).dialog({resizable: false, width: 350, height: 220, position: [10,210]}); }); </script>"+
                "<body><div id='wrapper'><div id='logo'><div id='logo'><h1><a href='http://adn.bioinfo.uqam.ca/armadillo/index.html'>Armadillo </a></h1>"+
                "<h2> v1.1 &nbsp;</h2><br /><br /><div align='right'><img src='images/logo.png' alt='' height='109' width='200' /></div></div></div>"+
                "<div id='header'><div id='menu'><ul>"+
                "<li><a href='http://adn.bioinfo.uqam.ca/armadillo/index.html'>HOMEPAGE<br /></a></li>"+
                "<li><a href='http://adn.bioinfo.uqam.ca/armadillo/included.html'>APPLICATIONS<br /></a></li>"+
                "<li><a href='http://adn.bioinfo.uqam.ca/armadillo/tutorials.html'>TUTORIALS<br /></a></li>"+
                "<li><a href='http://adn.bioinfo.uqam.ca/armadillo/wiki/index.php'>WIKI<br /></a></li>"+
                "<li class='last'><a href='http://adn.bioinfo.uqam.ca/armadillo/download.html'>DOWNLOAD</a></li></ul></div></div></div>"+
                "<div id='page'><div id='content'><div class='post'><br />"+
                "<h1><span style='color: black;'>"+title+"</span></h1><br />"+
                "<span class='Apple-style-span' style='border-collapse: separate; color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; font-size: medium;'>";
        return top;
    }
    
    /**
     * Head of a new page (non-index page) without metas
     * @return
     */
    private static String headNewPage(){
        String head =
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"+
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"+
                "<head>\n"+
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"+
                "<title>Armadillo Report : "+projectName+"</title>\n"+
                "<link href=\"css/styles.css\" rel=\"stylesheet\" type=\"text/css\" />\n"+
                "<link rel=\"shortcut icon\" href=\"favicon.ico\"/>"+
                "</head>\n"                
                ;
        return head;
    }

    /**
     *
     * @param object : Alignment, tree ...
     * @return
     */
    private String topNewPage(String object){
        String top =
                "<div id=\"logo\">\n"+
                "	<h1><a href=\"index.html\">Armadillo </a></h1>\n"+
                "	<h2> &raquo;&nbsp;&nbsp;&nbsp;"+object+"</h2>    \n"+
                "	<<img src=\"images/logo.png\" alt=\"\" width=\"200\" height=\"109\" /></div>\n"+
                "</div>\n"+
                "<div id=\"page\">\n";
        return top;
    }

    /**
     * foot of all pages
     * @return
     */
    public String foot(){
       
        String foot=
                "\n" +                
                "<div id=\"footer\">\n"+
                "	<p id=\"legal\">Automatically generated at : "+Util.returnCurrentDateAndTime()+
                "<br>( c ) 2010-"+Util.returnCurrentYear()+". Armadillo Workflow Systems <img src=\"images/logo.png\" alt=\"\" width=\"24\" height=\"13\" /></p>\n"+
                "</div>\n"+
                "</body>\n"+
                "</html>";
        return foot;
    }



    /**
     * This return the Style with the correct color associated
     * UNUSED, sytles are in the css
     * @return
     */
    public static String getColorsNucProtStyle() {
        return "<style type=\"text/css\">"+
                ".SeqName {background-color: #ff0000;}"+
                ".nucA {background-color: #ff0000;}"+
                ".nucT {background-color: #00ff00;}"+
                ".nucG {background-color: #0000ff;}"+
                ".nucC {background-color: #ffff00;}"+
                ".nucU {background-color: #00ff00;}"+
                ".nucN {background-color: #ff0000;}"+
                ".nuc- {background-color: #fffeff;}"+
                ".protG {background-color: #ebebeb;}"+
                ".protP {background-color: #dc9682;}"+
                ".protA {background-color: #c8c8c8;}"+
                ".protV {background-color: #0f820f;}"+
                ".protL {background-color: #0f820f;}"+
                ".protI {background-color: #0f820f;}" +
                ".protM {background-color: #e6e600;}"+
                ".protC {background-color: #e6e600;}"+
                ".protF {background-color: #3232aa;}"+
                ".protY {background-color: #3232aa;}"+
                ".protW {background-color: #b45ab4;}"+
                ".protH {background-color: #8282d2;}"+
                ".protK {background-color: #145aff;}"+
                ".protR {background-color: #145aff;}"+
                ".protQ {background-color: #00dcdc;}"+
                ".protN {background-color: #00dcdc;}"+
                ".protE {background-color: #e60a0a;}"+
                ".protD {background-color: #e60a0a;}"+
                ".protS {background-color: #fa960a;}"+
                ".protT {background-color: #fa960a;}"+
                "</style>";
    }
    
     public String getprojectName(){
        return projectName;
    }

    public static String getHead() {
        return headIndex();
    }

    public String propertiesHTML(String properties_name) {
        StringBuilder st=new StringBuilder();
        
         st.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");   
         st.append("<html>");
         //--Head
         st.append("<head>");
         st.append("<meta content=\"text/html; charset=ISO-8859-1\" http-equiv=\"content-type\">");
         st.append("<title>Reference | ");
         st.append("saveStrings()");
         st.append("</title></head>");
         //--Body
         st.append("<body>");
         //--Body Title 
         st.append("<big style=\"font-weight: bold; font-family: Courier New,Courier,monospace;\"><big><big><span style=\"color: rgb(102, 102, 102);\">Reference Armadillo v1.1 </span></big></big></big><br><br>");
         //--Table
         st.append("<table style=\"text-align: left; width: 1156px; height: 300px;\" border=\"0\" cellpadding=\"2\" cellspacing=\"2\">");
        //--Applications
         st.append("<tbody><tr><td></td><td style=\"width: 215px;\">Name</td><td style=\"width: 1004px;\"><big><span style=\"font-weight: bold;\">saveStrings()</span></big></td></tr>");
        //--Image
         st.append("<tr><td></td><td>Exemple</td><td><small><span style=\"font-family: Courier New,Courier,monospace;\">&lt;image here&gt;</span></small></td></tr>");
         //--Description
         st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Description</td><td style=\"width: 1004px;\"><small>Save an array of strings to filename<br><br></small></td></tr>");
         //--Input
         st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Input</td><td style=\"width: 1004px;\"><small><span style=\"font-family: Courier New,Courier,monospace;\"><br></span><br></small></td></tr>");
         //Output
         st.append("<tr><td></td><td>Output</td><td></td></tr><tr><td></td><td style=\"vertical-align: top;\">Control</td><td><small><br><image here><br></small></td></tr>");
         //--Syntaxe
         st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Syntaxe</td><td style=\"width: 1004px;\"><small><span style=\"font-weight: bold;\">saveStrings(const char* filename, string *array);<br></span></small></td></tr>");
         //--Parametes
         st.append("<tr><td></td><td>Supported parameters</td><td><small><br></small></td></tr><tr><td></td><td style=\"width: 215px; vertical-align: top;\">Return <br></td><td style=\"width: 1004px;\"><small>true if success<br></small></td></tr>");
         //--Related
         st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Related</td><td style=\"width: 1004px;\"><small><a href=\"loadStrings.html\">loadStrings</a><br></small></td></tr>");
         st.append("</tbody></table><br>");
         st.append("</body>");        
         st.append("</html>");
        return st.toString();
    }
    
    
       /**
     * @return the progress
     */
    public static int getProgress() {
        return progress;
    }

    /**
     * @param aProgress the progress to set
     */
    public static void setProgress(int aProgress) {
        progress = aProgress;
    }
}
