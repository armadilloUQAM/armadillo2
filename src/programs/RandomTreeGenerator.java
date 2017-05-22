/*
 *  Armadillo Workflow Platform v1.1
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

////////////////////////////////////////////////////////////////////////////////
///
/// Create a Thread to run muscle
///
/// Etienne Lord 2009

import biologic.Alignment;
import biologic.MultipleTrees;
import biologic.Tree;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;


public class RandomTreeGenerator extends RunProgram {
   
    private String outfile="outtree";              //Unique outfile: Must be deleted at the end
    
    ////////////////////////////////////////////////////////////////////////////
    /// Input / Output

    private MultipleTrees trees;

    public RandomTreeGenerator(workflow_properties properties) {        
       super(properties);
        
        execute();
    }

    @Override
    public boolean init_checkRequirements() {      
        return true;
    }

    @Override
    public void init_createInput() {
       if (Util.FileExists(outfile)) Util.deleteFile(outfile);
    }

    
    @Override
    public void post_parseOutput() {
        trees=new MultipleTrees();        
        trees.loadFromFile(outfile);
      if (trees.getNbTrees()>0) {
          trees.setName("Random Tree - (n:"+properties.get("trees")+" leaves:"+properties.get("taxa")+ " branch length:"+properties.get("scaling")+")");  
          trees.setNote("Random Tree Generator  ("+Util.returnCurrentDateAndTime()+")");
            for (Tree tree:trees.getTree()) {
                tree.setName("Random Tree (leaves:"+properties.get("taxa")+ " branch length:"+properties.get("scaling")+")");
                tree.setNote("Random Tree Generator  ("+Util.returnCurrentDateAndTime()+")");
            }
            //trees.getTree().get(0).setName();
            trees.saveToDatabase();
            properties.put("output_multipletrees_id", trees.getId());

            properties.put("output_tree_id", trees.getTree().get(0).getId());
            addOutput(trees);        
        }
        Util.deleteFile(outfile);
    }

    @Override
    public String[] init_createCommandLine() {
          String[] com=new String[20];
           for (int i=0; i<com.length;i++) com[i]="";
          int index=3;
          com[0]="cmd.exe";
           com[1]="/C";
           com[2]=properties.getExecutable();
           
           if (properties.isSet("taxa")) {
               com[index++]=""+properties.get("taxa");
           } else {
                 com[index++]=""+10;
           }
           if (properties.isSet("trees")) {
               com[index++]=""+properties.get("trees");
           } else {
               com[index++]=""+1;
           }
           if (properties.isSet("scaling")) {
               com[index++]=""+properties.get("scaling");
              
           } else {
               com[index++]="1.0";
           }
           com[index++]=outfile;
           
           return com;
    }
 
}
