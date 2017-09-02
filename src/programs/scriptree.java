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
/// Create a Thread to launch scriptree
///
/// Etienne Lord 2010


import biologic.ImageFile;
import biologic.Results;
import biologic.Tree;
import biologic.Unknown;
import configuration.Config;
import configuration.Util;
import java.io.*;
import program.RunProgram;
import workflows.workflow_properties;

public class scriptree extends RunProgram {
    private String infile="infile";               //Unique infile : Must be deleted at the end
    private String outfile="outfile";

    //Outgroup sequence
    public static int outgroup=0;
    // Debug and programming variables
    public static boolean debug=true;
    
    public scriptree(){};

    public scriptree (workflow_properties properties) {
        super(properties);
        execute();
    }

    public ImageFile scriptree(Tree tree) {
        //--Load the scriptTree file...
        //workflow_properties scripttree_prop=new workflow_properties();
        properties.load("Scriptree.properties", config.propertiesPath());
        properties.put("input_tree_id", tree.getId());
        //properties.setExecutable("executable\\scriptree\\scriptree.exe");
        
        properties.put("script", "tree -height 600 -font {Arial 5 normal} -conformation 1 esa -what :x -leaf 1");
        init_createInput();        
        super.commandline=init_createCommandLine();

        try {            
            super.do_run();
            post_parseOutput();
            ImageFile img=new ImageFile(properties.getInt("output_imagefile_id"));
            return img;
        } catch(Exception e) {e.printStackTrace();System.out.println("Error");}
        return new ImageFile();
    }

      public ImageFile scriptree(Tree tree, int number_of_species) {
        //--Load the scriptTree file...
        //workflow_properties scripttree_prop=new workflow_properties();
        properties.load("Scriptree.properties", config.propertiesPath());
        properties.put("input_tree_id", tree.getId());
        //properties.setExecutable("executable\\scriptree\\scriptree.exe");
        if (number_of_species<20) {
            //--600 7
            properties.put("script", "tree -height 500 -font {Arial 9 normal} -conformation 1 esa -what :x -leaf 1");
        } else {
            properties.put("script", "tree -height 800 -font {Arial 5 normal} -conformation 1 esa -what :x -leaf 1");
        }
        init_createInput();        
        super.commandline=init_createCommandLine();

        try {            
            super.do_run();
            post_parseOutput();
            ImageFile img=new ImageFile(properties.getInt("output_imagefile_id"));
            return img;
        } catch(Exception e) {e.printStackTrace();System.out.println("Error");}
        return new ImageFile();
    }

   ////////////////////////////////////////////////////////////////////////////
   // FUNCTIONS

    @Override
    public boolean init_checkRequirements() {
        int tree_id=properties.getInputID("input_tree_id");
        if (tree_id==0) {
            setStatus(status_BadRequirements, "No Tree ID found");
            return false;
        }
        return true;
    }


    @Override
    public void init_createInput() {
         //--Pre-Run initialization
       Util.deleteFile(infile);
       int tree_id=properties.getInputID("input_tree_id");
       Tree tree=new Tree(tree_id);
       tree.outputNewick(tree.getCompatibleName()+".nh");
       infile=tree.getCompatibleName()+".nh";
       outfile=tree.getCompatibleName()+".out";
       properties.put("Status", status_idle);
       properties.put("debug", debug);
       //--Special, copy some config file to current path
       try {
           File f=new File(properties.getExecutable());
             Util.copy(f.getAbsolutePath().replaceAll(f.getName(), "")+"scriptreepath.tcl", "scriptreepath.tcl");
       }catch(Exception e) {setStatus(status_error,"Unable to copy scriptreepath.tcl");}
    }

    @Override
    public String[] init_createCommandLine() {
        String[] com=new String[15];
        for (int i=0; i<com.length;i++) com[i]="";
        int index=10;
        com[0]="cmd.exe";
        com[1]="/C";
        com[2]=properties.getExecutable();
        com[4]="-tree";
        com[5]=infile;
        com[6]="-out";
        com[7]=outfile;
        com[8]="-format";
        com[9]="SVG";
        if (properties.isSet("script")) {
            Util u=new Util();
            u.open("script");
            u.println(properties.get("script"));
            u.close();
            com[index++]="-script";
            com[index++]="script";
        }
        if (properties.isSet("annotation")) {
            Util u=new Util();
            u.open("annotation");
            u.println(properties.get("annotation"));
            u.close();
            com[index++]="-annotation";
            com[index++]="annotation";
        }

        return com;
    }

    @Override
    public void post_parseOutput() {
        Util.deleteFile("scriptreepath.tcl");       
        try {
            ImageFile f=new ImageFile(outfile+".svg");
            Unknown unk=new Unknown(outfile+".svg");            
            f.setText(unk.getUnknown());
            f.setRunProgram_id(this.getId());
            f.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
            f.setNote("SVG file generated by Scriptree at "+Util.returnCurrentDateAndTime());
            f.saveToDatabase();
            properties.put("output_imagefile_id", f.getId());            
        } catch(Exception e) {setStatus(status_error,"Unable to save image to database");}
        Results result=new Results(outfile+".ps");
        result.setNote("PS file generated by Scriptree at "+Util.returnCurrentDateAndTime());
        result.setRunProgram_id(this.getId());
        result.saveToDatabase();
        properties.put("output_results_id",result.getId());
        //--Delete not needed files
        Util.deleteFile(outfile+".ps");
        Util.deleteFile(outfile+".nh");
        Util.deleteFile(outfile+".out.svg");
        if (Util.FileExists("script")) Util.deleteFile("script");
        if (Util.FileExists("annotation")) Util.deleteFile("annotation");
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
        final scriptree other = (scriptree) obj;
        return true;
    }

    

}
