/*
 *  Armadillo Workflow Platform v1.5
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


public class hybridinterleave extends RunProgram {      

    String path="";

    public hybridinterleave(workflow_properties properties) {
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
      
        //--Normal
        File f=new File(properties.getExecutable());
        if (properties.getBoolean("MacOSX")) f=new File(properties.getExecutableMacOSX());
        if (properties.getBoolean("Linux")) f=new File(properties.getExecutableLinux());
        path=f.getAbsolutePath().substring(0,f.getAbsolutePath().lastIndexOf(File.separator));
        properties.put("RunningDirectory", path);        
     
      
       setStatus(status_idle,"");           
    }

    @Override
    public String[] init_createCommandLine() {
         //--Load the Tree
        Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);       
        Vector<Integer>treeUP=properties.getInputID("tree",PortInputUP);
        Tree t1=new Tree(treeUP.get(0));
        Tree t2=new Tree(treeDOWN.get(0));
        newick_tree n1=new newick_tree();
        n1.parseNewick(t1.getTree());
        newick_tree n2=new newick_tree();
        n2.parseNewick(t2.getTree());
        
        String[] com=new String[30];
        for (int i=0; i<com.length;i++) com[i]="";       
           com[0]="java"; //--Minus the .exe
           com[1]="-jar";
           File f=new File(properties.getExecutable());
           com[2]=f.getName();
           com[3]="\""+n1.PrintNewickWithoutBranchLength()+"\""; //Tree1
           com[4]="\""+n2.PrintNewickWithoutBranchLength()+"\""; //Tree2         
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
                    text.setText(this.getOutputTXT().get(i+1));
                }                
            }
            text.saveToDatabase();
            text.setRunProgram_id(this.getId());
            text.setNote("HybridInterleave results created at "+Util.returnCurrentDateAndTime());
            properties.put("output_results_id", text.getId());
        
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
