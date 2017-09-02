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
import biologic.TextFile;
import biologic.Tree;
import biologic.seqclasses.parserNewick.newick_tree;
import configuration.Util;
import java.io.File;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;


public class PIRN extends RunProgram {      

    String path="";
    Matrix matrix=null; //--For the name change...
    
    public PIRN(workflow_properties properties) {
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
        Util.deleteFile(path+File.separator+"speciesTree.txt");        
        Util.deleteFile(path+File.separator+"geneTree.txt");        
        //--Normal
        File f=new File(properties.getExecutable());
        if (properties.getBoolean("MacOSX")) f=new File(properties.getExecutableMacOSX());
        if (properties.getBoolean("Linux")) f=new File(properties.getExecutableLinux());
        path=f.getAbsolutePath().substring(0,f.getAbsolutePath().lastIndexOf(File.separator));
        properties.put("RunningDirectory", path);
        Util tree1=new Util(path+File.separator+"speciesTree.txt");        
        String tree2="";
        
        Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);       
        Vector<Integer>treeUP=properties.getInputID("tree",PortInputUP);
        Vector<Integer>MultipleTreesDOWN=properties.getInputID("multipletrees",PortInputDOWN);
        
        Tree t1=new Tree(treeUP.get(0));
        newick_tree n_species=new newick_tree();
        n_species.parseNewick(t1.getTree());
        
          boolean change_name=false;  
          if (!n_species.isNumberedSpecies()) {
              change_name=true;
          }
          
          
//          n_species.replaceRef_by_Number(n2);
//            //--Output the new node name 
//            msg("Changed the terminal leaf names to numbers as follow:\n");
//            msg(n2.getNameMatrix());   
//            matrix=new Matrix();
//            matrix.setMatrix(n2.getNameMatrix());
//            matrix.setName("SPRDist name tree names");
//            matrix.setNote("SPRDist name change created at "+Util.returnCurrentDateAndTime());
//            matrix.saveToDatabase();
//            n2.replaceRef_by_Number();        
//        } 
        
        for (int ids:treeDOWN) {
            Tree tree=new Tree(ids);
             addInput(tree);
              newick_tree n2=new newick_tree();
              n2.parseNewick(tree.getTree());
              if (change_name) {
                  n2.replaceRef_by_Number(n_species);
              }
              tree2+=(n2.PrintNewickWithoutBranchLength())+"\n";
        }
         for (int ids:MultipleTreesDOWN) {
            MultipleTrees trees=new  MultipleTrees(ids);
            addInput(trees);
            for (Tree tree:trees.getTree()) {
                 addInput(tree);
                  newick_tree n2=new newick_tree();
                  n2.parseNewick(tree.getTree());
                  if (change_name) {
                      n2.replaceRef_by_Number(n_species);
                  }
                  tree2+=(n2.PrintNewickWithoutBranchLength())+"\n";
            }
        }
        
        //--Make sure we have number for name
        //--Replace name in tree n1 with number of node in 
        //--n2 
       
        if (change_name) {            
            //--Output the new node name 
            msg("Changed the terminal leaf names to numbers as follow:\n");
            msg(n_species.getNameMatrix());   
            matrix=new Matrix();
            matrix.setMatrix(n_species.getNameMatrix());
            matrix.setName("PIRN name tree names");
            matrix.setNote("PIRN name change created at "+Util.returnCurrentDateAndTime());
            matrix.saveToDatabase();
            n_species.replaceRef_by_Number();        
        } 
     
        tree1.println(n_species.PrintNewickWithoutBranchLength());     
        tree1.println(tree2);
        tree1.close();
        
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
        int index=4;
         for (int i=0; i<com.length;i++) com[i]="";       
           com[0]="cmd.exe";
           com[1]="/C";
           File f=new File(properties.getExecutable());
           com[2]=f.getName();
           com[3]="-C";
           if (properties.isSet("reticulation_events")) {
               if (properties.getInt("reticulation_events")>0) {
                   com[index++]="-r";
                   com[index++]=""+properties.getInt("reticulation_events");
               }
           }
           if (properties.isSet("mode")&&properties.getInt("mode")!=0) {
            //               Exact
            //Heuristique (1,000 levels)
            //Heuristique (10,000 levels)
            //Heuristique (100,000 levels)
               switch (properties.getInt("mode")) {
                   case 1: com[index++]="-n"; com[index++]="1000"; break;
                   case 2: com[index++]="-n"; com[index++]="10000"; break;   
                   case 3: com[index++]="-n"; com[index++]="100000"; break;
               }
           }
           com[index]="speciesTree.txt";          
        return com;
    }
    

    @Override
    public void post_parseOutput() {
        if (Util.FileExists(path+File.separator+"HybridizationNetwork.gml")) {
             Results text=new Results(path+File.separator+"HybridizationNetwork.gml");
            text.setName(properties.getName()+"_HybridizationNetwork.gml");            
            addOutput(text);           
            text.saveToDatabase();
            text.setRunProgram_id(this.getId());
            text.setNote("PIRN results created at "+Util.returnCurrentDateAndTime());
            properties.put("output_results_id", text.getId());        
        }       
        
        //--TextFile Tree
        TextFile text=new TextFile(path+File.separator+"speciesTree.txt");
        text.setName("Trees for network");
        text.setNote("PIRN Trees for network created at "+Util.returnCurrentDateAndTime());
        text.saveToDatabase();
        addOutput(text);
        properties.put("output_textfile_id", text.getId());
        //--Matrix    
            if (matrix!=null) {
                addOutput(matrix);
                properties.put("output_matrix_id", matrix.getId());
            }
            
        //--Clean-up
        Util.deleteFile(path+File.separator+"speciesTree.txt");        
 
    }

}
