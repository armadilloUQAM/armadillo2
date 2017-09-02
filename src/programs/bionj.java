/*
 *  Armadillo Workflow Platform v2.0
 *  A simple pipeline system for phylogenetic analysis
 *  
 *  Copyright (C) 2009-2014  Etienne Lord, Mickael Leclercq
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


public class bionj extends RunProgram {

/**
     *
     * Note: il n'y a pas présentement de test d'erreur
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant les séquences à partir desquelles l'arbre est généré
     */
    public bionj(workflow_properties properties) {
        super(properties);
        execute();
    }

   @Override
    public boolean init_checkRequirements() {
        
        Vector<Integer>matrixDOWN=properties.getInputID("matrix",PortInputDOWN);
        int dnadist_id=properties.getInputID("Phylip_Distance");
       
        if (dnadist_id==0&&matrixDOWN.size()==0) {
            setStatus(status_BadRequirements, "Error: We needPhylip_Distance matrix, or a distance matrix.");
            return false;
        }
        if (matrixDOWN.size()>0&&(dnadist_id!=0)) {
            setStatus(status_BadRequirements, "Error: Use only one distance matrix as input.");
            return false;
        }
        if (dnadist_id!=0&&(matrixDOWN.size()>0)) {
            setStatus(status_BadRequirements, "Error: Use only one distance matrix as input.");
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
            
       int PhylipDistance_id=properties.getInputID("Phylip_Distance");
       
        if (PhylipDistance_id!=0) {
            System.out.println("Phylip");
            Phylip_Distance align=new Phylip_Distance(PhylipDistance_id);
            align.Output("infile_bionj");
            addInput(align);
        } else {
            Util util=new Util("infile_bionj");
             //System.out.println("Infile");
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
                //System.out.println("Infile matrix");
                //System.out.println(matrixDOWN.get(0));
                Matrix matrix=new Matrix(matrixDOWN.get(0));                               
                matrix.outputPhylip("infile_bionj");              
                //--Final test : distance matrix?              
                addInput(matrix);
             }            
         util.close();
        }
       //this.createConfigFile("neighbor.params");
    }

    @Override
    public String[] init_createCommandLine() {
         String[] com=new String[11];
           for (int i=0; i<com.length;i++) com[i]="";
           com[0]="cmd.exe";
           com[1]="/C";
           com[2]=properties.getExecutable();
           com[3]="infile_bionj";
           com[4]="outfile_bionj";
           return com;
    }

   ////////////////////////////////////////////////////////////////////////////
   // FUNCTIONS

   

@Override
    public void post_parseOutput() {
//        Results unknown=new Results("outfile_bionj");
//        unknown.setRunProgram_id(super.getId());
//        unknown.setName("BioNJ of Trees at "+Util.returnCurrentDateAndTime());
//        unknown.setNote("Created at "+Util.returnCurrentDateAndTime());
//        unknown.saveToDatabase();
//        unknown.ReplaceResultsWithSequenceName();
//        addOutput(unknown);
//        if (unknown.getId()!=0) {
//            properties.put("output_results_id", unknown.getId());
//        }

       MultipleTrees tree=new MultipleTrees();
       tree.setName("BioNJ ("+Util.returnCurrentDateAndTime()+")");
       tree.readNewick("outfile_bionj");
       tree.setAlignment_id(properties.getInputID("input_alignment_id"));       
       tree.setNote("BioNJ at "+Util.returnCurrentDateAndTime()+"");
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
            Util.deleteFile("outfile_bionj");
            Util.deleteFile("infile_bionj");
        }
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
        final bionj other = (bionj) obj;       
        return true;
    }

}
