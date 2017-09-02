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
/// Create a Thread to run consense
/// Note: DnaDist is Special since a filename shoud alwaqys be associated with it
/// Note: We rename the outfile to dnadist.hashcode
/// Etienne Lord 2009

import configuration.Config;
import biologic.MultipleTrees;
import biologic.Text;
import biologic.Tree;
import configuration.Util;
import java.io.*;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;


public class consense extends RunProgram {

   
    private String infile="";               //Unique infile : Must be deleted at the end
    private String outfile="";              //Unique outfile: Must be deleted at the end
    private String filename="";
    public String name="Consense";
    private Vector<String> output=new Vector<String>();
    public int exitVal=0; //Program exit value

    public static int outgroup=0;     //Default species 1 (else specicy number)
    // seqboot mode


    /**
     *
     * Note: il n'y a pas présentement de test d'erreur
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant les séquences à partir desquelles l'arbre est généré
     */
    public consense(workflow_properties properties) {
        super(properties);
        execute();
    }

    

    @Override
    public boolean init_checkRequirements() {
       Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);
        Vector<Integer>multipletreesDOWN=properties.getInputID("multipletrees",PortInputDOWN);

        if (treeDOWN.size()==0&&multipletreesDOWN.size()==0) {
            setStatus(status_BadRequirements, "Error: We need 2 input trees");
            return false;
        }
       return true;
    }



    @Override
    public void init_createInput() {
          suredelete();
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
         this.createConfigFile("consense.params");
        util.close();
    }


    @Override
    public String[] init_createCommandLine() {
       String[] com=new String[11];
       for (int i=0; i<com.length;i++) com[i]="";
       com[0]="cmd.exe";
       com[1]="/C";
       com[2]=properties.getExecutable();
       com[3]="<"+"consense.params"; //Contient : 0 Y
       return com;
    }



    @Override
    public void post_parseOutput() {
       MultipleTrees multi=new MultipleTrees();
       multi.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
       multi.readNewick("outtree");
       multi.replaceSequenceIDwithNames();      
       multi.setNote("Created on "+Util.returnCurrentDateAndTime());
       multi.saveToDatabase();
       addOutput(multi);
       for (Tree t:multi.getTree()) properties.put("output_tree_id", t.getId());
       Text text=new Text("outfile");
       text.setName("Consense outfile on "+Util.returnCurrentDateAndTime());
       text.setNote("Created on "+Util.returnCurrentDateAndTime());
       text.saveToDatabase();
       properties.put("output_text_id", text.getId());
       suredelete();
       Util.deleteFile("consense.params");
    }
    
   ////////////////////////////////////////////////////////////////////////////
   // FUNCTIONS

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
            pw.println("Y");
            pw.flush();
            pw.close();
        } catch (Exception e) {Config.log("Error in creating ConfigFile "+filename);return false;}
        return true;
    }

  

  private boolean suredelete() {
        try {
            File outtree=new File("outtree");
            File out=new File("outfile");
                if (outtree.exists()||out.exists()) {
                    out.delete();
                    outtree.delete();
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
        final consense other = (consense) obj;
        if ((this.filename == null) ? (other.filename != null) : !this.filename.equals(other.filename)) {
            return false;
        }
        return true;
    }


 
 


}
