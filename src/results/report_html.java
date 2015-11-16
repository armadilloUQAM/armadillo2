package results;

import biologic.Workflows;
import database.Project;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * This will create a new simple report table 
 * @author Etienne Lord
 * @since November 2015
 */
public class report_html extends report {
	//--This is what is generally called by the button in the MainFrame
	//result.generate_Report(workbox.getProject(), workbox.getCurrentWorkflows())
	//    |
	//    +
	//projectName="Execution_at_"+Util.returnCurrentDateAndTime();        
   //     projectName=projectName.replaceAll(":", "_").replaceAll(" ", ".");
   //     CreateResultsFolders();
   //     generateReport(project, workflow, config.resultsDir()+File.separator+projectName+File.separator+"index.html");     
   //     Config.log("Generating report for "+projectName);        
   //     return config.resultsDir()+File.separator+projectName+File.separator+"index.html";
   //java -jar ModularityOptimizer.jar network.txt communities.txt 1 1.0 3 10 10 0 0


//--This is the main loop
//- This is a new vesion to create the big table object-variables
//private void generateReport(Project project, Workflows workflow, String filename){
//        //==1. Is workflow loaded?
//        if (workflow.getWorkflow()==null) return;
//
//        File index = new File (config.resultsDir()+File.separator+projectName+File.separator+"index.html");
//        try{
//            PrintWriter printInFile = new PrintWriter(new BufferedWriter(new FileWriter (filename)));
//            //printInFile.print(headIndex());
//                printInFile.println("<body>");               
//                printInFile.println(util.getRessource("style.css"));
//                printInFile.print(
//               // generateTopIndex("")+generateWorkflowInfo());
//                printInFile.println("<div class=\"post\">\n"+
//                                    "<div class=\"entry\">\n"+
//                                    "<div align=\"left\" style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\"></div><br>"+
//                                    "			<h2 class=\"title\">Workflow Execution</h2>");
//                //--Generate files (Input-Output)
//                this.generateWorkflowExecutionFile(workflow.getWorkflow(), workflow);
//                //--Generates HTML files
//                if (work.getRunWorkflowForWorkflows()==null||work.getRunWorkflowForWorkflows().getExecution_workflow_id().size()==1) {
//                    printInFile.print(generateWorkflowExecution(workflow.getWorkflow(), workflow));
//                } else {
//                    //==original
//                    Workflows workflow_to_generate=new Workflows(new armadillo_workflow());
//                        workflow_to_generate.loadFromDatabase(work.getRunWorkflowForWorkflows().getOriginal_workflow_id());
//                        workflow_to_generate.StringToWorkflow();
//						//--Analise this workflow to get the different component
//						
//                        printInFile.println("<table style=\"text-align: left; width: 100%;\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\"><thead>Original Workflow</thead><tbody><tr><td>");
//                        printInFile.println("<br>");
//                        printInFile.print(generateWorkflowExecution(workflow_to_generate.workflow, workflow_to_generate));
//                        printInFile.println("</td></tr><tbody></table><br>");
//
//                    //==other
//                    for (int id:work.getRunWorkflowForWorkflows().getExecution_workflow_id()) {
//                        Workflows workflow_to_generate2=new Workflows(new armadillo_workflow());
//                        workflow_to_generate2.loadFromDatabase(id);
//                        workflow_to_generate2.StringToWorkflow();
//                        if (id==work.getCurrentWorkflows().getId()) {
//                            printInFile.println("<table style=\"text-align: left; width: 100%;\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\"><thead>Currently Displayed Workflow</thead><tbody><tr><td>");
//                            printInFile.println("<br>");
//                        }
//						//(generateWorkflowExecution(workflow_to_generate2.workflow, workflow_to_generate2)
//                            printInFile.print(generateWorkflowExecution(workflow_to_generate2.workflow, workflow_to_generate2));
//                         if (id==work.getCurrentWorkflows().getId()) {
//                            printInFile.println("</td></tr><tbody></table><br>");
//                        }
//
//                    }
//                }
//                printInFile.println(   "<div style=\"clear: both;\">&nbsp;</div>\n"+"</div>\n");
//                printInFile.print(generateUserInfo(project)+ foot());
//          printInFile.close();
//        } catch(Exception e) {e.printStackTrace();}
//    }
	
//	  public String generateWorkflowExecution(armadillo_workflow armadillo, Workflows workflow) {
//        //--This will create a string in the for <tr><td>workflow_name</td>...
//		
//		StringBuilder st=new StringBuilder();
//         st.append(               
//                "<tr><td>"+workflow.getName()+"</td>";                
//         int count=0;
//		 //--First, list the input
//		   for (workflow_object obj:armadillo.workflow.outputExecution()) {
//			    workflow_properties properties=obj.getProperties();
//				 if (properties.get("ObjectType").equals("OutputDatabase")) {                                      
//                     st.append("<td>"+properties.getName+"<br>");                       
//					 
//                    for (String output_type:properties.Outputed()) {
//                        Vector<Integer> ids=properties.getOutputID(output_type, null);
//                        for (int id:ids) {
//                            if (id!=0) {
//                                 // st.append(
//                                    // "                                                       <li><a href=\""+output_type+properties.getID().replaceAll(" ", "_")+".html\">"+output_type+"</a></li>\n");
//                                // Output output=new Output();
//                                // output.setType(output_type);
//                                // output.setTypeid(id);
//                                // Object bio=output.getBiologic();
//                                // TO DO here: get the result...
//								//generateBiologicalContentPage(output_type+properties.getID(),bio);
//                            }
//                        }
//                    }                    
//                    st.append("</td>");
//               }
//		   }
//		 
//		   //--Then the ouput
//            for (workflow_object obj:armadillo.workflow.outputExecution()) {
//                workflow_properties properties=obj.getProperties();
//
//                if (properties.get("ObjectType").equals("Program")) {
//                    count++;
//                    properties.getStatus();
//                  
//                    st.append("<td>"+properties.getName()+" "+getHtmlStatus(properties)+"<br>");
//                    for (String output_type:properties.Outputed()) {
//                        Vector<Integer> ids=properties.getOutputID(output_type, null);
//                        for (int id:ids) {
//                            if (id!=0) {
//                                 st.append(
//                                    "<li><a href=\""+output_type+properties.getID().replaceAll(" ", "_")+".html\">"+(output_type.equals("OutputText")?"Software output":output_type)+"</a></li>\n");
//                                Output output=new Output();
//                                output.setType(output_type);
//                                output.setTypeid(id);
//                                Object bio=output.getBiologic();
//                                generateBiologicalContentPage(output_type+properties.getID(),bio);
//                            }
//                        }
//                    }
//                    if (properties.Outputed().size()==0) {
//                                st.append("<li>No outputs</li>\n");
//                       }
//					t.append("</td>");	
//				}
//			}
//           st.append("</tr>");
//           return st.toString();
//    }
}