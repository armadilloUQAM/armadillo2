/*
 *  Armadillo Workflow Platform v2.0
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

package programs;

import biologic.Matrix;
import biologic.MultipleTrees;
import biologic.Results;
import biologic.Text;
import biologic.Tree;
import biologic.seqclasses.parserNewick.newick_tree;
import configuration.Util;
import java.io.File;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;


public class sprit extends RunProgram {      

    String path="";

    public sprit(workflow_properties properties) {
        super(properties);       
        execute();       
    }

    @Override
    public boolean init_checkRequirements() {       
        Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);      
        Vector<Integer>treeUP=properties.getInputID("tree",PortInputUP);
          if (treeUP.size()==0) {
            setStatus(status_BadRequirements, "Error: We need a species tree...");
            return false;
        }
        if (treeDOWN.size()==0) {
            setStatus(status_BadRequirements, "Error: We need 2 input trees");
            return false;
        }
        return true;
    }

    
    @Override
    public void init_createInput() {
        //--Clean-up
        Util.deleteFile(path+File.separator+"speciesTree.txt");        
        Util.deleteFile(path+File.separator+"geneTree.txt");        
        //--Normal
        File f=new File(properties.getExecutable());
        if (properties.getBoolean("MacOSX")) f=new File(properties.getExecutableMacOSX());
        if (properties.getBoolean("Linux")) f=new File(properties.getExecutableLinux());
        path=f.getAbsolutePath().substring(0,f.getAbsolutePath().lastIndexOf(File.separator));
        properties.put("RunningDirectory", path);
        Util tree1=new Util(path+File.separator+"speciesTree.txt");        
        Util tree2=new Util(path+File.separator+"geneTree.txt");        
        Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);       
        Vector<Integer>treeUP=properties.getInputID("tree",PortInputUP);
        
        Tree t1=new Tree(treeUP.get(0));
        Tree t2=new Tree(treeDOWN.get(0));
        addInput(t1);
        addInput(t2);   
        newick_tree n1=new newick_tree();
        n1.parseNewick(t1.getTree());
        newick_tree n2=new newick_tree();
        n2.parseNewick(t2.getTree());
        tree1.println(n1.PrintNewickWithoutBranchLength());
        tree2.println(n2.PrintNewickWithoutBranchLength());
       
        tree1.close();
        tree2.close();
        //--
       setStatus(status_idle,"");           
    }

    
//    java -jar sprit.jar treeFile1 treeFile1 X Y Z W
// 
//X, execution time in seconds, unlimited = 0
//Y, number of cpus for the software to use, all present = 0
//Z, verbose, none (except SPR distance) = 0, a little (intermediate trees) = 1,
//   a lot (the kitchen zink) = 2
//W, mode of operation, Linz = 0, hybrid = 1, exhaustive = 2, incorrect conjecture = 3
//- Conjectured uses the minimal solvable common cluster conjecture from
//  SPRIT: Identifying horizontal gene transfer in rooted phylogenetic trees.
//- Hybrid uses the minimal common cluster reduction which might be 
//  significantly quicker in some cases but does not guarantee finding the 
//  minimum rSPR distance. 
//- Exhaustive means that the whole tree is used when searching for the min rSPR.
//  This will take a long time and does not as far as we can tell produce solutions
//  better than conjectured mode
// 
    @Override
    public String[] init_createCommandLine() {
         String[] com=new String[30];
        for (int i=0; i<com.length;i++) com[i]="";       
           com[0]="java"; //--Minus the .exe
           com[1]="-jar";
           File f=new File(properties.getExecutable());
           com[2]=f.getName();
           com[3]="speciesTree.txt";
           com[4]="geneTree.txt";
           
           if (properties.isSet("execution_time")){
            com[5]=properties.get("execution_time");
          } else com[5]="0";
          com[6]="0";
          com[7]="0";  //--Verbose 0                           
          if (properties.isSet("mode")){
            com[8]=properties.get("mode");
          } else com[8]="0";
        return com;
    }
    

    @Override
    public void post_parseOutput() {
       Results text=new Results();
            text.setName(properties.getName()+"_result");            
            addOutput(text);
            for (int i=0; i<this.getOutputTXT().size();i++) {
                String s=this.getOutputTXT().get(i);
                if (s.contains("Running program...")) {
                    text.setText(this.getOutputTXT().get(i+1).split(",")[2]);                    
                }                
            }
            text.saveToDatabase();
            text.setRunProgram_id(this.getId());
            text.setNote("Sprit results created at "+Util.returnCurrentDateAndTime());
            properties.put("output_results_id", text.getId());
        //--Clean-up
        Util.deleteFile(path+File.separator+"speciesTree.txt");        
        Util.deleteFile(path+File.separator+"geneTree.txt");  
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
