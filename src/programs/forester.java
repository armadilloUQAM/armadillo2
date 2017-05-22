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
/// Create a Thread to launch forester
///
/// Etienne Lord 2010


import biologic.Alignment;
import biologic.MultipleTrees;
import biologic.Tree;
import configuration.Config;
import configuration.Util;
import java.io.*;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;

public class forester extends RunProgram {
    private String infile="infile";               //Unique infile : Must be deleted at the end
    private String Executable="forester.jar";      //default Executable

    //Outgroup sequence
    public static int outgroup=0;
    // Debug and programming variables
    public static boolean debug=true;
    
    public forester (workflow_properties properties) {
        super(properties);
        //--Load the forester properties file
        workflow_properties forest=new workflow_properties(config.propertiesPath()+File.separator+"forester.properties");
        //--Copy current forester properties to this properties
        properties.setExecutable(forest.getExecutable());
        properties.setExecutableLinux(forest.getExecutableLinux());
        properties.setExecutableMacOSX(forest.getExecutableMacOSX());
        properties.put("RuntimeMacOSX",forest.get("RuntimeMacOSX"));
        if (!RunProgram.isExecutableFound(forest)) {
            System.out.println("Unable to run forester for "+properties.getName());
        }
        //if (!properties.isSet("Executable")) properties.setExecutable("executable//forester.jar");
        executeWithoutWait();
    }




   ////////////////////////////////////////////////////////////////////////////
   // FUNCTIONS




    @Override
    public boolean init_checkRequirements() {
        int tree_id=properties.getInputID("input_tree_id");
        //--Special case for output tree
        if (tree_id==0&&properties.getOutputID("tree")==0) {
            setStatus(status_error, "No Tree ID found");
            return false;
        }
        return true;
    }


    @Override
    public void init_createInput() {
         //--Pre-Run initialization
       Util.deleteFile(infile);
       int tree_id=properties.getInputID("input_tree_id");
       if (tree_id==0) tree_id=properties.getOutputID("tree");
       Tree tree=new Tree(tree_id);
       if (tree.getName().isEmpty()) tree.setName("Untitled");       
       tree.outputNewick(tree.getCompatibleName()+".nh");
       infile=tree.getCompatibleName()+".nh";
       properties.put("Status", status_idle);
       properties.put("debug", debug);
    }

    @Override
    public String[] init_createCommandLine() {
        String[] com=new String[11];
        for (int i=0; i<com.length;i++) com[i]="";
        com[0]="cmd.exe";
        com[1]="/C";
        com[2]="java";
        com[3]="-jar";
        com[4]=properties.getExecutable();
        com[5]=infile;
        return com;
    }


    @Override
    public void post_parseOutput() {
        
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
            //pw.println("I");
            if (outgroup>0) {
                //Sequence number valid?
                    pw.println("O");
                    pw.println(outgroup);
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
         //long time=System.currentTimeMillis();
         //return BigInteger.valueOf(time).intValue();
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
        final forester other = (forester) obj;
        return true;
    }

    

}
