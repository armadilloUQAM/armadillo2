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

import biologic.MultipleTrees;
import biologic.Results;
import biologic.Tree;
import biologic.seqclasses.parserNewick.ParserNewickEtienne;
import biologic.seqclasses.parserNewick.newick_tree;
import biologic.seqclasses.parserNewick.node;
import configuration.Util;
import java.io.File;
import java.util.HashMap;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;


public class latrans extends RunProgram {      
     //==HashMap for tree name conversion
     HashMap<Integer, String>number_to_names=new HashMap<Integer, String>();
     HashMap<String, Integer>names_to_number=new HashMap<String, Integer>();

    String path="";

    public latrans(workflow_properties properties) {
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
        File f=new File(properties.getExecutable());
        path=f.getAbsolutePath().substring(0,f.getAbsolutePath().lastIndexOf(File.separator));
        
        Util util=new Util("input_trees.txt");
        util.println("");
        Vector<Integer>MultipleTreesDOWN=properties.getInputID("multipletrees",PortInputDOWN);
        Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);       
        Vector<Integer>treeUP=properties.getInputID("tree",PortInputUP);
        //--We assume that the species tree are first
        //--We need to load the species tree
        Tree tree=new Tree(treeUP.get(0));
        newick_tree species_tree=new newick_tree();
        species_tree.parseNewick(tree.getTree());
        //--Create an hashmap of the tree_name and Rename the species tree       
        int i=1;
        for (node n:species_tree.node_list) {
            if (n.isleaf) {
                number_to_names.put(i, n.name);
                names_to_number.put(n.name, i);
                n.setName(""+i);
                i++;
            }
        }
        //==format of LaTrans is "Special"
        if (treeDOWN.size()>0) {
            for (int ids:treeDOWN) {
                Tree tree2=new Tree(ids);
                newick_tree gene_tree=new newick_tree();
                gene_tree.parseNewick(tree2.getTree());
                for (node n:gene_tree.node_list) {
                    if (n.isleaf) n.setName(""+names_to_number.get(n.getName()));
                }
                util.println(ParserNewickEtienne.createLatransInputTree(species_tree,gene_tree));
            }
        }
        if (MultipleTreesDOWN.size()>0) {
             for (int ids:MultipleTreesDOWN) {
                MultipleTrees trees=new  MultipleTrees(ids);
                addInput(trees);
                for (Tree tree2:trees.getTree()) {
                         newick_tree gene_tree=new newick_tree();
                         gene_tree.parseNewick(tree2.getTree());
                    for (node n:gene_tree.node_list) {
                        if (n.isleaf) n.setName(""+names_to_number.get(n.getName()));
                    }
                util.println(ParserNewickEtienne.createLatransInputTree(species_tree,gene_tree));
                }
            }
        }
        util.close();
        //--
       setStatus(status_idle,"");           
    }

    @Override
    public String[] init_createCommandLine() {
          String[] com=new String[30];
        for (int i=0; i<com.length;i++) com[i]="";
        int index=4;
           com[0]="java";
           com[1]="-jar";
           com[2]="-Xmx256m";
           File f=new File(properties.getExecutable());
           com[3]=f.getName();
           //==Program to run
            com[index++]="-m";
            com[index++]=config.currentPath;
            com[index++]="input_trees.txt"; //==This is required for java
            com[index++]="10"; //--Max transfert to infers... Need to be in a menu?
        return com;
    }
    

    @Override
    public void post_parseOutput() {
        if (Util.FileExists("input_trees.txt")) {
            Results text=new Results("input_trees.txt");
            text.setNote("Created on "+Util.returnCurrentDateAndTime());
            text.setName(properties.getName()+"_input_trees.txt");
            addOutput(text);
            text.saveToDatabase();
            if (!properties.getBoolean("debug")) Util.deleteFile("input_trees.txt");
            properties.put("output_textfile_id", text.getId());
            Util.deleteFile("input_trees.txt");
        }
        if (Util.FileExists("input_trees.txt.out")) {
            Results text=new Results("input_trees.txt.out");
            text.setNote("Created on "+Util.returnCurrentDateAndTime());
            text.setName(properties.getName()+"_Results_"+Util.returnCurrentDateAndTime());
            addOutput(text);
            text.saveToDatabase();
            if (!properties.getBoolean("debug")) Util.deleteFile("input_trees.txt.out");
            properties.put("output_results_id", text.getId());
            Util.deleteFile("input_trees.txt.out");
        }
    }


    @Override
      public boolean do_run() throws Exception {
           //--Run the thread and catch stdout and stderr
           setStatus(status_running, "\tRunning program...");
           //--MacOSX
//           if ((config.getBoolean("MacOSX")||SystemUtils.IS_OS_MAC_OSX)) {
//                              
//           }
          
           ProcessBuilder pb=new ProcessBuilder(commandline);
           pb.directory(new File(path));
           p = pb.start();           
           InputStreamThread stderr = new InputStreamThread(p.getErrorStream());
           InputStreamThread  stdout = new InputStreamThread(p.getInputStream()); 
           int exitvalue=p.waitFor();
           properties.put("ExitValue", exitvalue);
           msg("\tProgram Exit Value: "+getExitVal());

           return true;
     }
}
