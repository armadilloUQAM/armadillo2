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
/// Create a Thread to run MAVID root_tree (root a tree by midpoint)
///
/// Etienne Lord 2010

import biologic.MultipleTrees;
import biologic.Tree;
import configuration.Util;
import org.apache.regexp.RE;
import program.RunProgram;
import workflows.workflow_properties;

public class root_tree extends RunProgram {
    private String infile="intree";               //Unique infile : Must be deleted at the end
    private String outfile="outtree";              //Unique outfile: Must be deleted at the end

    //Outgroup sequence
    public static int outgroup=0;
    // Debug and programming variables
    public static boolean debug=true;
    
    
    public root_tree (workflow_properties properties) {
        super(properties);        
        execute();
    }

   ////////////////////////////////////////////////////////////////////////////
   // FUNCTIONS


    @Override
    public boolean init_checkRequirements() {
      int tree_id=properties.getInputID("tree");

        if (tree_id==0) {
            setStatus(status_BadRequirements,"No tree to root found.");
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
            
    }

    @Override
    public String[] init_createCommandLine() {
        String[] com=new String[11];
        for (int i=0; i<com.length;i++) com[i]="";
        com[0]="cmd.exe";
        com[1]="/C";
        com[2]=properties.getExecutable();
        com[3]=infile;
        com[4]=">"+outfile;
        return com;
    }


    @Override
    public void post_parseOutput() {

        RE error=new RE("Error");
        if (error.grep(Util.InputFile(outfile)).length>0) {
            setStatus(status_error,"Error in processing tree. Probably bad format.");
            return;
        } else {
            MultipleTrees multi=new MultipleTrees();
            multi.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
            multi.setRunProgram_id(this.getId());
            multi.readNewick(outfile);
            multi.setAlignment_id(properties.getInputID("input_alignment_id"));
            multi.setNote("Root_Tree (using MidPoint) ("+Util.returnCurrentDateAndTime()+")");
            multi.replaceSequenceIDwithNames();
            multi.saveToDatabase();
            for (Tree tree:multi.getTree()) {                
                properties.put("output_tree_id", tree.getId());
            }
        }
            //--delete not needed files
            Util.deleteFile(infile);
            Util.deleteFile(outfile);
    }

}
