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

//////////////////////////////////////////////////////////////////////////////////////////////33
///
/// Create a Thread to run an internal verify tree
///
/// Etienne Lord 2010

import configuration.Config;
import biologic.MultipleTrees;
import biologic.Results;
import biologic.Tree;
import biologic.seqclasses.parserNewick.newick_tree;
import configuration.Util;
import java.io.*;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;

public class verify_tree extends RunProgram {
    private String infile="intree";               //Unique infile : Must be deleted at the end
    private String outfile="outtree";              //Unique outfile: Must be deleted at the end

    //Outgroup sequence
    public static int outgroup=0;
    // Debug and programming variables
    public static boolean debug=true;
    
    
    public verify_tree (workflow_properties properties) {
        super(properties);        
        execute();
    }

   ////////////////////////////////////////////////////////////////////////////
   // FUNCTIONS


    @Override
    public boolean init_checkRequirements() {
      int tree_id=properties.getInputID("tree");

        if (tree_id==0) {
            setStatus(this.status_BadRequirements,"No tree found.");
            return false;
        }
    
        return true;
    }

    @Override
    public void init_createInput() {
        //--Pre-Run initialization
       Util.deleteFile(infile);
       Util.deleteFile(outfile);      
       //--Create infile
       int tree_id=properties.getInputID("tree");
    
        if (tree_id!=0) {
            Tree tree=new Tree(tree_id);
            tree.outputNewick(infile);
        }
      
       //--Create infile
       //createConfigFile("retree.params");
    }



    @Override
    public String[] init_createCommandLine() {
        //--Dummy
        String[] com=new String[11];        
        return com;
    }

     @Override
    public boolean do_run() throws Exception {
        Vector<Integer>tree=properties.getInputID("tree",null);
        Vector<newick_tree>trees=new Vector<newick_tree>();
         Results r=new Results();
         String tree_name="";
         
         for (int treeids:tree) {
            Tree t=new Tree(treeids);
            newick_tree n=new newick_tree();
            n.parseNewick(t.getTree());
            n.setName(t.getName()); 
            //--Output some information and new trees - without bootstrap                        
            Tree newtree=new Tree();
            newtree.setName(t.getName()+"_without bootstrap");
            newtree.setNote("Created on "+Util.returnCurrentDateAndTime());
            newtree.setTree(n.PrintNewick());
            newtree.saveToDatabase();
            properties.put("output_tree_id",newtree.getId());
            r.setText(r.getText()+"\n"+n.PrintInfo());
            tree_name+=t.getName()+" ";
            trees.add(n);
        }
         //--Compare all the trees node name         
         if (tree.size()>1) {
             
             
             
         }
         
            r.setName("Verify Newick Tree - "+tree_name);            
            r.setNote("Created on "+Util.returnCurrentDateAndTime());
            r.saveToDatabase();
            properties.put("output_results_id",r.getId());             
         
        return true;
    }

    @Override
    public void post_parseOutput() {
            
    }


   

    

}
