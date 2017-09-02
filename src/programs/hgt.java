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

import biologic.Matrix;
import biologic.MultipleTrees;
import biologic.Results;
import biologic.Text;
import biologic.Tree;
import configuration.Util;
import java.io.File;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;


public class hgt extends RunProgram {      

    String path="";

    public hgt(workflow_properties properties) {
        super(properties);       
        execute();       
    }

    @Override
    public boolean init_checkRequirements() {       
        Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);
        Vector<Integer>multipletreesDOWN=properties.getInputID("multipletrees",PortInputDOWN);
        Vector<Integer>treeUP=properties.getInputID("tree",PortInputUP);
          if (treeUP.size()==0) {
            setStatus(status_BadRequirements, "Error: We need a species tree...");
            return false;
        }
        if (treeDOWN.size()==0&&multipletreesDOWN.size()==0) {
            setStatus(status_BadRequirements, "Error: We need 2 input trees");
            return false;
        }
        return true;
    }

    
    @Override
    public void init_createInput() {
        //--Clean-up
        Util.deleteFile(path+File.separator+"speciesTreeWeb.txt");
        Util.deleteFile(path+File.separator+"speciesRoot.txt");
        Util.deleteFile(path+File.separator+"geneTreeWeb.txt");
        Util.deleteFile(path+File.separator+"geneRoot.txt");
        Util.deleteFile(path+File.separator+"infile.txt");
        //--Normal
        File f=new File(properties.getExecutable());
        if (properties.getBoolean("MacOSX")) f=new File(properties.getExecutableMacOSX());
        if (properties.getBoolean("Linux")) f=new File(properties.getExecutableLinux());
        path=f.getAbsolutePath().substring(0,f.getAbsolutePath().lastIndexOf(File.separator));
        properties.put("RunningDirectory", path);
        Util util=new Util(path+File.separator+"input.txt");
        Vector<Integer>MultipleTreesDOWN=properties.getInputID("multipletrees",PortInputDOWN);
        Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);       
        Vector<Integer>treeUP=properties.getInputID("tree",PortInputUP);
        //--We assume that the tree are first
        for (int ids:treeUP) {
            Tree tree=new Tree(ids);
            addInput(tree);
            if (properties.getBoolean("abbreviate")) {
                util.println(tree.getTreeAbbreviate());
            } else util.println(tree.getTree());
        }
        for (int ids:treeDOWN) {
            Tree tree=new Tree(ids);
            addInput(tree);
             if (properties.getBoolean("abbreviate")) {
                util.println(tree.getTreeAbbreviate());
            } else util.println(tree.getTree());
        }
         for (int ids:MultipleTreesDOWN) {
            MultipleTrees trees=new  MultipleTrees(ids);
            addInput(trees);
            for (Tree tree:trees.getTree()) util.println(tree.getTree());
        }
        util.close();
        //--
       setStatus(status_idle,"");           
    }

    @Override
    public String[] init_createCommandLine() {
        String[] com=new String[10];
        int index=5;
        for (int i=0; i<com.length;i++) com[i]="";
           com[0]="cmd.exe";
           com[1]="/C";
          File f=new File(properties.getExecutable());
          
           com[2]=f.getName();
           com[3]="-inputfile=input.txt";
           com[4]="-version=web";
           if (properties.isSet("criterion")){
            com[index++]="-criterion="+properties.get("criterion");
          }
          if (properties.isSet("scenario")){
            com[index++]="-scenario="+properties.get("scenario");
          }
          if (properties.isSet("nbhgt")){
            com[index++]="-nbhgt="+properties.get("nbhgt");
          }
          if (properties.isSet("subtree")){
            com[index++]="-subtree="+(properties.getBoolean("subtree")?"yes":"no");
          }
        return com;
    }
    

    @Override
    public void post_parseOutput() {
        if (Util.FileExists(path+File.separator+"results.txt")) {
            Results text=new Results(path+File.separator+"results.txt");
            text.setName(properties.getName()+"_results.txt");
            addOutput(text);
            text.saveToDatabase();
            properties.put("output_results_id", text.getId());
        }

        if (Util.FileExists(path+File.separator+"output.txt")) {
            Results text=new Results(path+File.separator+"output.txt");
            text.setName(properties.getName()+"_output.txt");
            addOutput(text);
            text.saveToDatabase();
            properties.put("output_results_id", text.getId());
        }
        
        Matrix matrix=new Matrix(path+File.separator+"input_.txt");
        matrix.setName("HGT matrix "+Util.returnCurrentDateAndTime());
        matrix.saveToDatabase();
        addOutput(matrix);
        properties.put("output_matrix_id",matrix.getId());
        //
        Util.deleteFile(path+File.separator+"speciesTreeWeb.txt");
        Util.deleteFile(path+File.separator+"speciesRoot.txt");
        Util.deleteFile(path+File.separator+"geneTreeWeb.txt");
        Util.deleteFile(path+File.separator+"geneRoot.txt");
        Util.deleteFile(path+File.separator+"infile.txt");
    }


//    @Override
//      public boolean do_run() throws Exception {
//           --Run the thread and catch stdout and stderr
//           setStatus(status_running, "\tRunning program...");
//           --Linux?
//           if (config.getBoolean("MacOSX")) {
//                com[2]=
//           }
//           ProcessBuilder pb=new ProcessBuilder(commandline);
//           pb.directory(new File(path));
//           p = pb.start();           
//           InputStreamThread stderr = new InputStreamThread(p.getErrorStream());
//           InputStreamThread  stdout = new InputStreamThread(p.getInputStream()); 
//           int exitvalue=p.waitFor();
//           properties.put("ExitValue", exitvalue);
//           msg("\tProgram Exit Value: "+getExitVal());
//
//           return true;
//     }
}
