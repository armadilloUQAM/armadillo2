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
/// Create a Thread to run phylip treedist
///
/// Etienne Lord 2010

import configuration.Config;
import biologic.MultipleTrees;
import biologic.Results;
import biologic.Tree;
import configuration.Util;
import java.io.*;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;

public class treedist extends RunProgram {
    private String infile="infile";               //Unique infile : Must be deleted at the end
    private String outfile="outfile";              //Unique outfile: Must be deleted at the end

    //Outgroup sequence
    public static int outgroup=0;
    // Debug and programming variables
    public static boolean debug=true;
    
    
    public treedist (workflow_properties properties) {
        super(properties);        
        execute();
    }

   ////////////////////////////////////////////////////////////////////////////
   // FUNCTIONS

    @Override
    public boolean init_checkRequirements() {
        Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);
        Vector<Integer>multipletreesDOWN=properties.getInputID("multipletrees",PortInputDOWN);

        //--TO DO: better test
        if (treeDOWN.size()==0&&multipletreesDOWN.size()==0) {
            setStatus(status_BadRequirements, "Error: We need 2 input trees");
            return false;
        }
        return true;
    }

    @Override
    public void init_createInput() {
        //--Pre-Run initialization
       Util.deleteFile(infile);
       Util.deleteFile(outfile);
       Util.deleteFile("outtree");
       //--Create infile
        Util util=new Util("intree");
        Vector<Integer>MultipleTreesDOWN=properties.getInputID("multipletrees",PortInputDOWN);
        Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);
        //--We assume that the tree are first
        for (int ids:treeDOWN) {
            Tree tree=new Tree(ids);
            addInput(tree);
            util.println(tree.getTree());
        }
         for (int ids:MultipleTreesDOWN) {
            MultipleTrees trees=new  MultipleTrees(ids);
            addInput(trees);
            for (Tree tree:trees.getTree()) util.println(tree.getTree());
        }
        util.close();
       //--Create infile
       createConfigFile("treedist.params");
    }



    @Override
    public String[] init_createCommandLine() {
        String[] com=new String[11];
        for (int i=0; i<com.length;i++) com[i]="";
        com[0]="cmd.exe";
        com[1]="/C";
        com[2]=properties.getExecutable();
        com[3]="<"+"treedist.params"; //Contient : 0 Y
        return com;
    }


    @Override
    public void post_parseOutput() {
        Results text=new Results("outfile");
        text.setRunProgram_id(this.getId());
        text.saveToDatabase();
        addOutput(text);
        properties.put("output_results_id", text.getId());       
        //
        if (!properties.getBoolean("debug")) {
                Util.deleteFile("intree");
                Util.deleteFile(outfile);
                Util.deleteFile("outtree");
                Util.deleteFile("treedist.params");
         }
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
            if (properties.isSet("CustomPhylipCommand")) {
                pw.println(properties.get("CustomPhylipCommand"));
            }
            if (properties.isSet("rooted")) {
                pw.println("R");
            }
            pw.println("Y");
            pw.println("R");
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
        final treedist other = (treedist) obj;
        return true;
    }

    

}
