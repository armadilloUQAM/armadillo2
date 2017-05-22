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
/// Create a Thread to run Phylip ReTree
///
/// Etienne Lord 2010

import configuration.Config;
import biologic.MultipleTrees;
import biologic.Tree;
import configuration.Util;
import java.io.*;
import program.RunProgram;
import workflows.workflow_properties;

public class retree extends RunProgram {
    private String infile="intree";               //Unique infile : Must be deleted at the end
    private String outfile="outtree";              //Unique outfile: Must be deleted at the end

    //Outgroup sequence
    public static int outgroup=0;
    // Debug and programming variables
    public static boolean debug=true;
    
    
    public retree (workflow_properties properties) {
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
       createConfigFile("retree.params");
    }



    @Override
    public String[] init_createCommandLine() {
        String[] com=new String[11];
        for (int i=0; i<com.length;i++) com[i]="";
        com[0]="cmd.exe";
        com[1]="/C";
        com[2]=properties.getExecutable();
        com[3]="<"+"retree.params"; //Contient : 0 Y
        return com;
    }


    @Override
    public void post_parseOutput() {
            MultipleTrees multi=new MultipleTrees();
            multi.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
            multi.setRunProgram_id(this.getId());
            multi.readNewick("outtree");
            multi.setAlignment_id(properties.getInputID("input_alignment_id"));
            multi.setNote("ReTree ("+Util.returnCurrentDateAndTime()+")");
            multi.replaceSequenceIDwithNames();
            multi.saveToDatabase();
            for (Tree tree:multi.getTree()) {                
                properties.put("output_tree_id", tree.getId());
            }            
            //--delete not needed files
            Util.deleteFile(infile);
            Util.deleteFile(outfile);
            Util.deleteFile("outtree");
            Util.deleteFile("retree.params");

    }


    /**
     * Create a Phylip configurationFile [param] (Override this method to change param file)
     * @param filename
     * @return true if success!
     */
    public boolean createConfigFile(String filename) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            pw.println("0");            
            pw.println("Y");
             if (properties.isSet("CustomPhylipCommand")) {
                pw.println(properties.get("CustomPhylipCommand"));
            }
            if (properties.get("method").equals("Midpoint")) {
                pw.println("M");
            } else {
                pw.println("O");
                pw.println(properties.get("outgroup"));
            }
            pw.println("Q");
            pw.println("Y");
            if (properties.getBoolean("rooted")) {
                pw.println("R");
            } else {
                pw.println("U");
            }
            pw.flush();
            pw.close();

        } catch (Exception e) {Config.log("Error in creating ConfigFile "+filename);return false;}
        return true;
    }

     


    @Override
    public int hashCode() {     
        return Util.returnCount();
     }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final retree other = (retree) obj;
        return true;
    }

    

}
