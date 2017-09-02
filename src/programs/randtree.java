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


import biologic.Alignment;
import biologic.MultipleTrees;
import biologic.Tree;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;

/**
 * Create a random tree using randTree.exe from the Phylip package
 * @author Etienne Lord
 * @since Mars 2009
 */
public class randtree extends RunProgram {
 

    /**
     * Main contructor
     */
    public randtree(workflow_properties properties) {
        super(properties);
        execute();
    }


    @Override
    public boolean init_checkRequirements() {
        int alignment_id=properties.getInputID("alignment");
        if (alignment_id==0) {
            setStatus(this.status_BadRequirements,"No Alignment found. An alignment is needed to generate this tree");
            return false;
        }
        return true;
    }



    @Override
    public void init_createInput() {
        int alignment_id=properties.getInputID("alignment");
        Alignment align=new Alignment(alignment_id);
        align.outputFasta("output");
    }


    @Override
    public String[] init_createCommandLine() {
         String[] com=new String[11];
           for (int i=0; i<com.length;i++) com[i]="";
           com[0]="cmd.exe";
           com[1]="/C";
           com[2]=properties.getExecutable();
           com[3]="output";
           com[4]=">"+"outfile";
        return com;
    }

    @Override
    public void post_parseOutput() {
        MultipleTrees multi=new MultipleTrees();
        multi.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
        multi.readNewick("outfile");
        multi.setRunProgram_id(this.getId());        
        multi.setNote("Generated at "+Util.returnCurrentDateAndTime());
        multi.saveToDatabase();
        for (Tree t:multi.getTree()) {
            properties.put("output_tree_id", t.getId());
        }       
        Util.deleteFile("outfile");
        Util.deleteFile("output");
    }

    
    

  

    
   



}
