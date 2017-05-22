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


import biologic.Matrix;
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


public class neighbor extends RunProgram {

    public static int mode=0;          //Default mode Neighbor-Joining
    public static int outgroup=0;     //Default species 1 (else specicy number)
    // seqboot mode
    public static final int modeNeighborJoining=0; //default
    public static final int modeUPGMA=1;

    public static final int outgroupDefault1=0;
/**
     *
     * Note: il n'y a pas présentement de test d'erreur
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant les séquences à partir desquelles l'arbre est généré
     */
    public neighbor(workflow_properties properties) {
        super(properties);
        execute();
    }

   @Override
    public boolean init_checkRequirements() {
        Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);
        Vector<Integer>multipletreesDOWN=properties.getInputID("multipletrees",PortInputDOWN);
        Vector<Integer>matrixDOWN=properties.getInputID("matrix",PortInputDOWN);
        int dnadist_id=properties.getInputID("Phylip_Distance");
       
        if (treeDOWN.size()==0&&multipletreesDOWN.size()==0&&dnadist_id==0&&matrixDOWN.size()==0) {
            setStatus(status_BadRequirements, "Error: We need input trees, Phylip_Distance matrix, or a distance matrix.");
            return false;
        }
        if (matrixDOWN.size()>0&&(treeDOWN.size()>0||multipletreesDOWN.size()>0||dnadist_id!=0)) {
            setStatus(status_BadRequirements, "Error: Use either a distance matrix or trees as input, but not both at the same time.");
            return false;
        }
        if (dnadist_id!=0&&(treeDOWN.size()>0||multipletreesDOWN.size()>0||matrixDOWN.size()>0)) {
            setStatus(status_BadRequirements, "Error: Use either a distance matrix or trees as input, but not both at the same time.");
            return false;
        }
        if (matrixDOWN.size()>0) {
              Matrix matrix=new Matrix(matrixDOWN.get(0));                                                   
//            if (!matrix.isDistanceMatrix()) {
//                setStatus(status_BadRequirements, "Error: We expect a distance matrix...");
//                return false;
//            }
            
        }
        return true;
    }

   @Override
    public void init_createInput() {
       this.suredelete();
       
       int PhylipDistance_id=properties.getInputID("Phylip_Distance");
       
        if (PhylipDistance_id!=0) {
            System.out.println("Phylip");
            Phylip_Distance align=new Phylip_Distance(PhylipDistance_id);
            align.Output("infile");
            addInput(align);
        } else {
            Util util=new Util("infile");
             System.out.println("Infile");
            Vector<Integer>MultipleTreesDOWN=properties.getInputID("multipletrees",PortInputDOWN);
            Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);
            Vector<Integer>matrixDOWN=properties.getInputID("matrix",PortInputDOWN);
            //--We assume that the tree are first
            boolean found_tree=false;
            for (int ids:treeDOWN) {
                Tree tree=new Tree(ids);
                addInput(tree);
                util.println(tree.getTree());
                found_tree=true;
            }
             for (int ids:MultipleTreesDOWN) {
                MultipleTrees trees=new  MultipleTrees(ids);
                addInput(trees);
                for (Tree tree:trees.getTree()) util.println(tree.getTree());
                found_tree=true;
            }
            if (matrixDOWN.size()==1) {                 
                util.close();
                System.out.println("Infile matrix");
                System.out.println(matrixDOWN.get(0));
                Matrix matrix=new Matrix(matrixDOWN.get(0));                               
                matrix.outputPhylip("infile");              
                //--Final test : distance matrix?              
                addInput(matrix);
             }            
         util.close();
        }
       this.createConfigFile("neighbor.params");
    }

    @Override
    public String[] init_createCommandLine() {
         String[] com=new String[11];
           for (int i=0; i<com.length;i++) com[i]="";
           com[0]="cmd.exe";
           com[1]="/C";
           com[2]=properties.getExecutable();
           com[3]="<"+"neighbor.params"; //Contient : 0 Y
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
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            pw.println("0");
            if (properties.getBoolean("UPGMA")){
                pw.println("N");
            }
            if (properties.isSet("outgroup")){
                pw.println("O");
                pw.println(properties.get("outgroup"));
            }
            pw.println("Y");
            pw.println("R");
            pw.flush();
            pw.close();
        } catch (Exception e) {Config.log("Error in creating ConfigFile "+filename);return false;}
        return true;
    }

@Override
    public void post_parseOutput() {
        Results unknown=new Results("outfile");
        unknown.setRunProgram_id(super.getId());
        unknown.setName("Neighbor-Joining of Trees at "+Util.returnCurrentDateAndTime());
        unknown.setNote("Created at "+Util.returnCurrentDateAndTime());
        unknown.saveToDatabase();
        unknown.ReplaceResultsWithSequenceName();
        addOutput(unknown);
        if (unknown.getId()!=0) {
            properties.put("output_results_id", unknown.getId());
        }

       MultipleTrees tree=new MultipleTrees();
       tree.setName("Neighbor "+(properties.getBoolean("UPGMA")?"UPGMA":"NJ")+" ("+Util.returnCurrentDateAndTime()+")");
       tree.readNewick("outtree");
       tree.setAlignment_id(properties.getInputID("input_alignment_id"));       
       tree.setNote("Neighbor at "+Util.returnCurrentDateAndTime()+"");
       tree.replaceSequenceIDwithNames();
       tree.saveToDatabase();
       for (Tree t:tree.getTree()) {
           properties.put("output_tree_id", t.getId());
       }
       addOutput(tree);
        //--Note we only return the last tree_id
      // for (Tree t:tree.getTree()) properties.put("output_tree_id", t.getId());
      properties.put("output_multipletrees_id",tree.getId());
        if (!properties.getBoolean("debug")) {
            Util.deleteFile("neighbor.params");
            Util.deleteFile("outtree");
            Util.deleteFile("outfile");
            Util.deleteFile("infile");
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
        final neighbor other = (neighbor) obj;       
        return true;
    }

}
