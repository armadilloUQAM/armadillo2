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
/// Create a Thread to run MAVID
///
/// Etienne Lord 2009


import biologic.MultipleSequences;
import biologic.Tree;
import program.RunProgram;
import workflows.workflow_properties;

/**
 * Note: currently not supported...
 * @author Etienne Lord
 * @since 2009
 */
public class mavid extends RunProgram {

    public mavid(workflow_properties properties) {
        super(properties);               
        this.execute();
    }

    @Override
    public boolean init_checkRequirements() {
           int multiplesequences_id=properties.getInputID("multiplesequences");
           int tree_id=properties.getInputID("tree");
           if (multiplesequences_id==0) {
                setStatus(status_error,"Error: No MultipleSequences found.");
                return false;
           }
            if (tree_id==0) {
                setStatus(status_error,"Error: No tree found.");
                return false;
           }      
        return true;
    }

    @Override
    public String[] init_createCommandLine() {
         String[] com=new String[15];
           for (int i=0; i<com.length;i++) com[i]="";
           // mavid [args] tree_file seq_file
           com[0]="cmd.exe";
           com[1]="/C";
           com[2]=properties.getExecutable();
           com[3]="tree.nh";
           com[4]="input.fasta";
          return com;
    }

    @Override
    public void init_createInput() {
       int multiplesequences_id=properties.getInputID("multiplesequences");
       int tree_id=properties.getInputID("tree");
       MultipleSequences multi=new MultipleSequences(multiplesequences_id);
       multi.outputFastaWithSequenceID("input.fasta");
       Tree tree=new Tree(tree_id);
       tree.outputNewickWithSequenceID("tree.nh");
    }

    @Override
    public void post_parseOutput() {
        //--TO DO
    }
 
}

