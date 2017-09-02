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
/// Create a Thread to run dnaml
///
/// Etienne Lord 2010

import configuration.Config;
import biologic.Alignment;
import biologic.MultipleTrees;
import biologic.Phylip_Seqboot;
import biologic.Results;
import biologic.Tree;
import configuration.Util;
import java.io.*;
import program.RunProgram;
import workflows.workflow_properties;

public class dnaml extends RunProgram {
    private String infile="infile";               //Unique infile : Must be deleted at the end
    private String outfile="outfile";              //Unique outfile: Must be deleted at the end

    //Outgroup sequence
    public static int outgroup=0;
    // Debug and programming variables
    public static boolean debug=true;
    
    
    public dnaml (workflow_properties properties) {
        super(properties);        
        execute();
    }

   ////////////////////////////////////////////////////////////////////////////
   // FUNCTIONS



    @Override
    public boolean init_checkRequirements() {
        int align_id=properties.getInputID("alignment");
       int seqboot_id=properties.getInputID("Phylip_Seqboot");

        if (align_id==0&&seqboot_id==0) {
            setStatus(this.status_BadRequirements,"No sequence found.");
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
       int align_id=properties.getInputID("alignment");
       int seqboot_id=properties.getInputID("Phylip_Seqboot");
        if (align_id!=0) {
            Alignment align=new Alignment(align_id);
            align.outputPhylipInterleaveWithSequenceID("infile");
            addInput(align);
        }
        if (seqboot_id!=0) {
            Phylip_Seqboot align=new Phylip_Seqboot(seqboot_id);
            align.Output("infile");
            addInput(align);
        }
       //--Create infile
       createConfigFile("dnaml.params");
    }



    @Override
    public String[] init_createCommandLine() {
        String[] com=new String[11];
        for (int i=0; i<com.length;i++) com[i]="";
        com[0]="cmd.exe";
        com[1]="/C";
        com[2]=properties.getExecutable();
        com[3]="<"+"dnaml.params"; //Contient : 0 Y
        return com;
    }


    @Override
    public void post_parseOutput() {
            MultipleTrees multi=new MultipleTrees();
            multi.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
            multi.readNewick("outtree");
            multi.setAlignment_id(properties.getInputID("input_alignment_id"));
            multi.setNote(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
            multi.replaceSequenceIDwithNames();            
            multi.saveToDatabase();
            for (Tree tree:multi.getTree()) {
                properties.put("output_tree_id", tree.getId());
            }
            
            Results text=new Results("outfile");
            text.setName("DNAml outfile at "+Util.returnCurrentDateAndTime());
            text.setNote("Generated at "+Util.returnCurrentDateAndTime());
            text.ReplaceResultsWithSequenceName();
            text.saveToDatabase();
            properties.put("output_results_id",text.getId());
            //--delete
            if (!properties.getBoolean("debug")) {
                Util.deleteFile(infile);
                Util.deleteFile(outfile);
                Util.deleteFile("outtree");
                Util.deleteFile("dnaml.params");
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
  

}
