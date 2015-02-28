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



package results;

import biologic.Biologic;
import biologic.ImageFile;
import biologic.Input;
import biologic.MultipleTrees;
import biologic.Output;
import biologic.Tree;
import biologic.Workflows;
import biologic.seqclasses.parserNewick.ParserNewickEtienne;
import biologic.seqclasses.parserNewick.newick_tree;
import configuration.Config;
import configuration.Util;
import database.Project;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import program.RunProgram;
import programs.scriptree;
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
public class report_list_workflows {

    //static databaseFunction df = new databaseFunction();
    Workbox work=new Workbox();
    Config config = new Config();
    Util util = new Util();

    int alignmentsCount=0;
    int treesCount=0;
    int workflowId=0;
    Project project;
    public static String projectName="";
    String author="";
    String institution="";
    String email="";
    Vector <Integer> alignmentsIds;
    Vector <Integer> treesIds;

    public String getprojectName(){
        return projectName;
    }
    /**
     * This Generate a "Run" report of the Armadillo Workflow
     * @param project
     * @return The path to the generated report
     */
    public String generateResults(Project project, Workflows workflow){
        this.project=project;
        projectName="Execution_at_"+Util.returnCurrentDateAndTime();        
        projectName=projectName.replaceAll(":", "_").replaceAll(" ", ".");
        CreateResultsFolders();
        generateReport(project, workflow, config.resultsDir()+File.separator+projectName+File.separator+"index.html");     
        Config.log("Generating workflow report page");
        return config.resultsDir()+File.separator+projectName+File.separator+"index.html";
    }

    /**
     * Create Results folder with css and images
     */
    public void CreateResultsFolders(){
        config.createDir(config.resultsDir()+File.separator+projectName);
        //this.work.getCurrentArmadilloWorkflow().saveImage(config.resultsDir()+File.separator+projectName+File.separator+"images"+File.separator+"Workflow.png");
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
        String return_str="";
        //--String first part
        return_str="\n"+
                "	<div id=\"content\">\n"+
                "		<div class=\"post\">\n";
        //--Generate image for all the workflow in the database
        Workflows nwork=new Workflows(new armadillo_workflow());
        Workflows to_picture=new Workflows(new armadillo_workflow());
        int i=0;
        while(nwork.hasNext()) {
            Workflows to_picture_tmp=((Workflows)nwork.next());
            work.database_workflow.loadFromDatabase(to_picture_tmp.getId());
            work.database_workflow.StringToWorkflow();
            work.database_workflow.getWorkflow().saveImage(config.resultsDir()+File.separator+projectName+File.separator+"images"+File.separator+"Workflow"+i+".png");
            //--Force redraw
            work.database_workflow.getWorkflow().force_redraw=true;
            work.database_workflow.getWorkflow().redraw();
            
            //--Generate HTML
            return_str+="               <a href=\"images"+File.separator+"Workflow"+i+".png"+"\" target=\"_blank\">" +
                "                       <img src=\"images"+File.separator+"Workflow"+i+".png.thumb.png"+"\"/></a>\n"+
                to_picture_tmp.getName()+
                "                       <p><div style=\"text-align:center\"><b>Click on this image to open it in a new unique window</b></div><p>\n";
            i++;
        }
        return  return_str;
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
                Util util=new Util();
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
//                           "                                                   <li>Inputs</li>\n"+
//                           "                                                   <ol>\n");
//                    for (String input_type:properties.Accepted()) {
//                        Vector<Integer> ids=properties.getInputID(input_type, null);
//                        for (int id:ids) {
//                            if (id!=0) {
//                                 st.append(
//                                    "                                                       <li><a href=\""+input_type+properties.getID().replaceAll(" ", "_")+".html\">"+input_type+"</a></li>\n");
//                                Input input=new Input();
//                                input.setType(input_type);
//                                input.setTypeid(id);
//                                Object bio=input.getBiologic();
//                                generateNewPage(input_type+properties.getID(),(String)((Biologic)bio).toHtml());
//                            }
//                        }
//                    }
//                    if (properties.Accepted().size()==0) {
//                             st.append(
//                                    "                                                       <li>No inputs</li>\n");
//                    }
//                     st.append(
//                        "                                                   </ol>\n"+
//                        "                                                   </li>\n");
                        //"                                                   <li>Outputs</li>\n"+
                        //"                                                   <ol>\n"
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
    public void generateBiologicalContentPage(String file, Object obj){
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
                        printInFile.println("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">Phylip</div>");
                        printInFile.println(((Biologic)obj).getPhylip().replaceAll("\n", "<br>").replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")+"<br>");
                    }
                    if (type.equalsIgnoreCase("Tree")) {
                        scriptree scr=new scriptree();
                        Tree t=(Tree)((Biologic)obj);
                        newick_tree tree=new newick_tree();
                        try {
                            tree.parseNewick(t.getTree());
                            printInFile.println("Total species : "+tree.getTotalLeaf());
                        } catch(Exception e){}
                        ImageFile img=scr.scriptree(t);
                        img.Output(config.resultsDir()+File.separator+projectName+File.separator+"tree_"+img.hashCode()+".svg");

                        printInFile.println("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">Input tree</div>");
                        printInFile.println("<br>Created with ScripTree v1.7 <a href=\"http://phylo.lirmm.fr/scriptree/\">Website</a>");
                        printInFile.println("<embed src=\""+config.resultsDir()+File.separator+projectName+File.separator+"tree_"+img.hashCode()+".svg"+"\" width=\"600\" height=\"1200\" type=\"image/svg+xml\" pluginspage=\"http://www.adobe.com/svg/viewer/install/\" />");
                        
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
        }
        catch (java.io.IOException e){
            Config.log("Unable to write in file");
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
                "	<p id=\"legal\">Report automatically generated at : "+Util.returnCurrentDateAndTime()+
                "<br>( c ) 2010. Armadillo Workflow Systems <img src=\"images/logo.png\" alt=\"\" width=\"24\" height=\"13\" /></p>\n"+
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
    
    public static String getHead() {
        return headIndex();
    }
    
    public static String getTableCSS() {
        return "<style>body, textarea{ font-family: Helvetica, Arial, Sans-serif;font-size: 13px;}\n"+
               "body { padding: 0 20px;}\n"+
               "textarea { width: 600px; height: 100px;	padding: 4px;	margin-bottom: 8px;}\n"+
               "table.dataframe { border-collapse: collapse; background: #efefef; }\n"+
               "table.dataframe td, table.dataframe th { text-align: left; padding: 0.4em 3em 0.4em 0.8em; }\n"+
               "table.dataframe td { border-top: 1px solid #fff; }\n"+
               "table.dataframe th { background: #ccc;}</style>";
    }
}
