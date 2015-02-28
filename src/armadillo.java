/*
 *  Armadillo Workflow Platform v1.0
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


import configuration.Config;
import configuration.Util;
import demo.Demo;
import demo.myExperiment_clustering;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.UIManager;
import splash.SplashWindow;

/**
 * Armadillo v1.0
 *
 * @author Alix Boc, Etienne Lord, Mickael Leclercq, Abdoulaye Baniré Diallo. Vladimir Makarenkov
 * @since May 2009
 *
 */
public class armadillo {

        static MainFrame main;

      ///////////////////////////////////////////////////////////////////////////
      /// MAIN PROGRAM
      /// TO DO: Command-line version to run server, etc.

      public static void main(String args[]) {
         //-- save command arguments
          Config.cmdArgs=args;

        //CASE 1: DEMO Mode? (Development mode)
        if (args.length>0) {
            if (args[0].equalsIgnoreCase("-h")||args[0].equalsIgnoreCase("help")||args[0].equalsIgnoreCase("-help")||args[0].equalsIgnoreCase("--help")) {
                System.out.println("Usage: java -jar armadillo.jar demo");
                System.out.println("Usage: java -jar armadillo.jar clusterWorkflow [directory|workflow.db]");
                System.exit(0);
            }
            
            if (args[0].equalsIgnoreCase("demo")) {
                Demo.RunDemo();
            }  
            ////////////////////////////////////////////////////////////////////
            /// WORKFLOW CLUSTERING
            if (args[0].equalsIgnoreCase("clusterWorkflow")) {
                if (args[1].isEmpty()) {
                    System.out.println("Usage: java -jar armadillo.jar clusterWorkflow [directory|workflow.db]");
                    System.exit(0);
                }
                myExperiment_clustering myex=new myExperiment_clustering();
                String work=args[1];
                File f=new File(work);
                if (f.isDirectory()) {
                    myex.Cluster(work);
                } else if (work.endsWith(".db")) {
                    myex.Run(work,"");
                } else {
                    System.out.println("Usage: java -jar armadillo.jar clusterWorkflow [directory|workflow.db]");
                }
            }
            
            
            ////////////////////////////////////////////////////////////////////
            // WEB SERVER            
            //0. get workflow state
            if (args[0].equalsIgnoreCase("webstate")) {
               //webstate workflow_file
                if (args.length==2) {
                    if (!Util.FileExists(args[1])) {
                        System.out.print("no workflow");
                        System.exit(0);
                    } 
                    System.out.print("ready");
                    //Demo.web_ListWorkflow(args[1]);
                } else {
                    System.out.print("error");
                    System.exit(0);
                    
                }             
            } 
            //1. Create the workflows 
            else if (args[0].equalsIgnoreCase("webcreate")) {
                //webcreate workflow_file workflow_to_load
                if (args.length==3) {
                    Demo.web_CreateWorkflow(args[1],args[2]);
                     System.exit(0);
                }             
            } 
                
            //2. List the workflows 
            else if (args[0].equalsIgnoreCase("webinfo")) {
              //webinfo workflow_file workflow_output
                if (args.length>1) {
                    Demo.web_listWorkflow(args[1]);
                }             
            }
            // 3. List type
            else if (args[0].equalsIgnoreCase("listMultipleSequences")) {
                //listMultipleSequences workflow_file                
                Demo.web_list(args[1], "MultipleSequences", args[2]);
                             
            }
            else if (args[0].equalsIgnoreCase("listAlignment")) {                
                Demo.web_list(args[1], "Alignment", args[2]);                       
            }
            else if (args[0].equalsIgnoreCase("listMatrix")) {
                //System.out.println("webinfo");
                     Demo.web_list(args[1], "Matrix", args[2]);
            }
            else if (args[0].equalsIgnoreCase("listTree")) {
                //System.out.println("webinfo");             
                     Demo.web_list(args[1], "Tree", args[2]);

            }
            else if (args[0].equalsIgnoreCase("listWorkflow")) {
                //System.out.println("webinfo");             
                     Demo.web_list(args[1], "Workflow", args[2]);

            }
             // 4. List type in njson
            else if (args[0].equalsIgnoreCase("listMultipleSequencesJSON")) {
                //listMultipleSequences workflow_file                
                Demo.web_list_json(args[1], "MultipleSequences", args[2]);
                             
            }
            else if (args[0].equalsIgnoreCase("listAlignmentJSON")) {                
                Demo.web_list_json(args[1], "Alignment", args[2]);                       
            }
            else if (args[0].equalsIgnoreCase("listMatrixJSON")) {
                //System.out.println("webinfo");
                     Demo.web_list_json(args[1], "Matrix", args[2]);
            }
            else if (args[0].equalsIgnoreCase("listTreeJSON")) {
                //System.out.println("webinfo");             
                     Demo.web_list_json(args[1], "Tree", args[2]);

            }
            else if (args[0].equalsIgnoreCase("listWorkflowJSON")) {
                //System.out.println("webinfo");             
                     Demo.web_list_json(args[1], "Workflow", args[2]);

            }
            // 3. put Type
            else if (args[0].startsWith("put")) {
                //putType workflow_file input_file
                //--Return id
                //System.out.println("webinfo");
                // 0. get workflow filename 
                // 1. get type
                // 2. get Next id
                // 2. get filename
                // 3. Try to run
                // 4. return the id...
                String type=args[0].substring(3);            
                if (args.length==3) {
                    Demo.web_put(args[1], type, args[2]);
                }             
            }
           
               // 4. Get Type Html
            else if (args[0].startsWith("getHtml")) {
                //getWorklow workflow_file workflow_id output_file
                //getLatest workflow_file workflow_id output_file
                String type=args[0].substring(7);                
                 Demo.web_getHtml(args[1], type, args[2],args[3]);
                
            }        
                // 5. Get Type
            else if (args[0].startsWith("get")) {
                //getWorklow workflow_file workflow_id output_file
                //getLatest workflow_file workflow_id output_file
                String type=args[0].substring(3);                
                Demo.web_get(args[1], type, args[2],args[3]);                
            }       
            else {
                //--We run a workflow with argument...
                //  Possible cases:
                //  armadillo.jar workflow.db workflow_id [input1] [input2] ...
                //  armadillo.jar workflow.txt [input1] [input2] ...
                Demo.Run();               
            } 
        } 
        else
        {
           final SplashWindow splash=new SplashWindow();           
           // CASE 2: Normal GUI Mode
            // OPtionnal : Set look and Feel
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                //UIManager.setLookAndFeel(ch.randelshofer.quaqua.QuaquaManager.getLookAndFeel());
            } catch (Exception e) {}
             
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {                   
                
                    main=new MainFrame();
                    splash.setVisible(false);
                    main.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    main.setVisible(true);
                  
                // This don't work
                // main.workbox.mazimizeSize();
                // main.workbox.setWorkflowVisible();
                // main.toolbox.mazimizeSize();
                // main.toolbox.setVisible(true);
                }
            });
            }
    }

}
