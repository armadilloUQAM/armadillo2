

package programs.local;

import biologic.Results;
import biologic.Tree;
import biologic.seqclasses.parserNewick.newick_tree;
import configuration.Util;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;

/**
 * This list the file in a directory
 * 
 * Note: this will generate a repeat object so the file will be static...
 *       This will emit TextFile
 * 
 * @author Etienne Lord
 */
public class List_Files extends RunProgram {
    
     public List_Files (workflow_properties properties) {
        super(properties);        
        execute();
    }
    
    @Override
    public boolean init_checkRequirements() {
   
        return true;
    }
    
        @Override
    public boolean do_run() throws Exception {
        //1. Get current file item (iteration)
        //   from the properties 
        //   output the file...
        
        
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
    
    
     
}
