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


package programs;

import biologic.Alignment;
import biologic.Tree;
import configuration.Config;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;
import program.RunProgram;
import program.runningThreadInterface;
import workflows.workflow_properties;

/**
 *
 * @author Etienne
 */
public class sbinferAncestors extends RunProgram {
    private Vector<String> output=new Vector<String>();
    private int exitVal=0;
    private boolean done=false;
    private String pathTosbInferAncestors="";
    private String pathToExecutable="";
    private String ancestorCC_filename;
    private String name="outsbancertor"; //default out filename
    private static boolean debug=false;
    // For the Thread version
    private int status=0;           //status code
    private long timerunning=0;     //time running
    Thread thread;                  //Thread
    Process p;                      //Process
    public int retry=0;             //Retry

    Config config=new Config();
    workflow_properties properties;

    public sbinferAncestors(workflow_properties properties) {
        this.properties=properties;
        pathTosbInferAncestors=config.get("SbAncestors");
                // We assume that we have
        // infile.seq infile.tre out.pres
         int alignment_id=properties.getInputID("input_alignment_id");
         int tree_id=properties.getInt("input_tree_id");
         if (alignment_id==0) {
            setStatus(Config.status_error, "No Alignment found!");
            return;
         }
        if (tree_id==0) {
            setStatus(Config.status_error, "No Tree found!");
            return;
         }
        //create infile
        Alignment align=new Alignment(alignment_id);
        align.outputFasta("infile.seq");
        Tree tree=new Tree(tree_id);
        tree.outputNewick("infile.tre");
        runthread();
        if (properties.getBoolean("NoThread")) while(!done){}
       

    }


     public void runthread() {
        //Note requiert le fichier infile

         new Thread(){

            @Override
             public void run() {
                 try {
                      Runtime r = Runtime.getRuntime();
                       String[] com=new String[11];
                       for (int i=0; i<com.length;i++) com[i]="";
                       com[0]="cmd.exe";
                       com[1]="/C";
                       com[2]=pathTosbInferAncestors;
                       // infile.seq infile.tre out.pres out.ancestors out.nucConf out.post
                       com[3]="infile.seq";
                       com[4]="infile.tre";
                       com[5]=name+".ancestors";
                       com[6]=name+".nucConf";
                       com[7]=name+".ansConf";
                       com[8]=name+".prob";
                       Process p = r.exec(com);
                       InputStream stderr = p.getInputStream();
                       InputStream stdoutput = p.getErrorStream();
                       InputStreamReader isr = new InputStreamReader(stdoutput);
                       BufferedReader br = new BufferedReader(isr);
                       String line = null;
                       while ( (line = br.readLine()) != null) {
                           msg(line);
                           if (line.endsWith(";")) {
                           //save the Tree for further use
                               Tree tree=new Tree();
                               tree.setTree(line);
                               tree.saveToDatabase();
                               properties.put("output_tree_id", tree.getId());
                           }
                       }
                           exitVal = p.waitFor();
                       if (exitVal==0) {
                            deleteFile("infile.seq");
                            deleteFile("infile.tre");
                        } else {
                           if (debug) Config.log("Error code: "+exitVal);
                       }
                       //done=true;
                } catch (Exception ex) {
                    Config.log("Error");
                    ex.printStackTrace();
                }
                done=true;
             }
        }.start();

  }
        ///////////////////////////////////////////////////////////////////////
        // HELPER FUNCTIONS

   /**
    * Delete a file
    * @param filename (to delete)
    * @return true if success
    */
    public boolean deleteFile(String filename) {
        try {
            File outtree=new File(filename);
            outtree.delete();
        } catch(Exception e) {Config.log("Unable to delete file "+filename);return false;}
        return true;
    }

    @Override
    public void PrintOutput() {
         for (String stri:output) Config.log(stri);
    }

   
}
