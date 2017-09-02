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
/// Create a Thread to run neighbor¸
/// Etienne Lord 2009


import biologic.Alignment;
import biologic.MultipleTrees;
import biologic.Phylip_Distance;
import biologic.Results;
import biologic.Tree;
import configuration.Config;
import configuration.Util;
import java.io.*;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;


public class weighbor extends RunProgram {

  
/**
     *
     * Note: il n'y a pas présentement de test d'erreur
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant les séquences à partir desquelles l'arbre est généré
     */
    public weighbor(workflow_properties properties) {
        super(properties);
        execute();
    }

   @Override
    public boolean init_checkRequirements() {      
        int dnadist_id=properties.getInputID("Phylip_Distance");
        int alignment_id=properties.getInputID("Alignment");
        if (dnadist_id==0) {
            setStatus(status_BadRequirements, "Error: We need a Phylip_Distance matrix");
            return false;
        }
        if (alignment_id==0) {
            setStatus(status_BadRequirements, "Error: We need the original alignment...");
            return false;
        }
        return true;
    }

   @Override
    public void init_createInput() {
       this.suredelete();
       
       int PhylipDistance_id=properties.getInputID("Phylip_Distance");       
        if (PhylipDistance_id!=0) {
            Phylip_Distance align=new Phylip_Distance(PhylipDistance_id);
            align.Output("infile");
            addInput(align);
        }
       this.createConfigFile("weighbor.params");
    }

    @Override
    public String[] init_createCommandLine() {        
        String[] com=new String[11];
           for (int i=0; i<com.length;i++) com[i]="";
           com[0]="cmd.exe";
           com[1]="/C";
           com[2]=properties.getExecutable();
           com[3]="<weighbor.params";
           return com;
    }

   ////////////////////////////////////////////////////////////////////////////
   // FUNCTIONS

    /**
     * Create a Phylip configurationFile [param] (Override this method to change param file)
     * @param filename
     * @return true if success!
     */
    public boolean createConfigFile(String filename) {
        int alignment_id=properties.getInputID("Alignment");
        Alignment a=new Alignment(alignment_id);
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            pw.println("0");
             if (properties.isSet("CustomPhylipCommand")) {
                pw.println(properties.get("CustomPhylipCommand"));
            }
            pw.println("infile");
            pw.println("outfile");
            pw.println(""+a.getSequenceSize());
            pw.println("4.0");
            pw.flush();
            pw.close();
        } catch (Exception e) {Config.log("Error in creating ConfigFile "+filename);return false;}
        return true;
    }

@Override
    public void post_parseOutput() {        
       MultipleTrees tree=new MultipleTrees();
       tree.setName("Weighbor ("+Util.returnCurrentDateAndTime()+")");
       tree.readNewick("outfile");
       tree.setAlignment_id(properties.getInputID("input_alignment_id"));      
       tree.setNote("Weighbor at "+Util.returnCurrentDateAndTime()+"");
       tree.replaceSequenceIDwithNames();
       tree.setRunProgram_id(this.getId());
       tree.saveToDatabase();
       for (Tree t:tree.getTree()) {
           properties.put("output_tree_id", t.getId());
           t.setRunProgram_id(this.getId());
       }
       addOutput(tree);
        //--Note we only return the last tree_id
      // for (Tree t:tree.getTree()) properties.put("output_tree_id", t.getId());
      properties.put("output_multipletrees_id",tree.getId());
        if (!properties.getBoolean("debug")) {                     
            Util.deleteFile("outfile");
            Util.deleteFile("infile");
            Util.deleteFile("weighbor.params");
        }
    }
  
  private boolean suredelete() {
        try {
            File outtree=new File("outtree");
            File out=new File("outfile");
                if (outtree.exists()||out.exists()) {
                    outtree.delete();
                    out.delete();
                }

        } catch(Exception e) {Config.log("Problem in suredelete()");return false;}
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
        final weighbor other = (weighbor) obj;       
        return true;
    }

}
