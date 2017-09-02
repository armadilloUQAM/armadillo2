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
/// Create a Thread to run fastdnaml
///
/// Etienne Lord 2009

import configuration.Config;
import biologic.Alignment;
import biologic.MultipleTrees;
import biologic.Results;
import biologic.Tree;
import biologic.Unknown;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;


public class fastdnaml extends RunProgram {
    private String infile="infile";               //Unique infile : Must be deleted at the end


     private String Executable="fastDNAml.exe"; //default Executable
    // Debug and programming variables
    public static boolean debug=true;
    // For the Thread version
    private int status=0;           //status code
   
   
    /**
     *
     * Note: il n'y a pas présentement de test d'erreur
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant les séquences à partir desquelles l'arbre est généré
     */
    public fastdnaml(workflow_properties properties) {        
        this.properties=properties;        
        execute();
    }

    @Override
    public boolean init_checkRequirements() {
        //--Check if input contains alignment
        if (properties.getInputID("alignment")==0) {
            setStatus(status_BadRequirements,"Error: Unable to find alignment.");
            //msg(properties.getProperties());
            return false;
        }
        Alignment a=new Alignment(properties.getInputID("alignment"));
        if (a.getNbSequence()<4) {
            setStatus(status_BadRequirements,"Error: We need at least 4 aligned sequences.");
            return false;
        }
        //--FastDNAml param
        String params="";
        boolean outgroup=properties.isSet("outgroup");
        boolean bootstrap=properties.getBoolean("bootstrap");
        if (outgroup||bootstrap) {
            if (outgroup) params+="O ";
            if (bootstrap) params+="B ";
            params+="\n";
            if (outgroup) params+="O "+properties.get("outgroup")+"\n";
            if (bootstrap) params+="B "+properties.get("bootstrap_number")+"\n";
        }
        a.outputPhylipInterleaveWithSequenceID(infile,params);
        addInput(a);
        return true;
    }

    @Override
    public void init_createInput() {        
       properties.put("Status", status_idle);
       properties.put("debug", debug);       
    }


    @Override
    public String[] init_createCommandLine() {
        String[] com=new String[10];
        for (int i=0; i<com.length;i++) com[i]="";
                       com[0]="cmd.exe";
                       com[1]="/C";
                       com[2]=properties.getExecutable();
                       com[3]="<"+infile+">outfile";
        return com;
    }

   

    @Override
    public void post_parseOutput() {
          String filenameIndex="";
            int index=0; //index de la ligne
            //--Find stdout in outfile...
            for (String s:Util.InputFile("outfile")) {
               
                index=s.indexOf("treefile.");
                if (index>-1) {
                    filenameIndex=s.substring(index+9);
                }
            }
            //--stdout of fastdnaml...
            Results text=new Results("outfile");
            text.saveToDatabase();
            properties.put("output_results_id",text.getId());
            //--CheckPoint
            Unknown file=new Unknown("checkpoint."+filenameIndex);
            file.saveToDatabase();
            properties.put("output_unknown_id",file.getId());

            MultipleTrees multi=new MultipleTrees();
            if (!filenameIndex.equals("")) {
                multi.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
                multi.readNewick("treefile."+filenameIndex);
                //Config.log(properties.getInputID("alignment"));
                multi.setAlignment_id(properties.getInputID("alignment"));
                multi.replaceSequenceIDwithNames();
                Alignment aling=new Alignment(multi.getAlignment_id());
                Config.log(aling.toString());                
                multi.setNote("fastDNAml ("+Util.returnCurrentDateAndTime()+")"); //Also set for tree
                multi.saveToDatabase();
                addOutput(multi);

                //--Note we only return the last tree_id
                for (Tree tree:multi.getTree()) {
                    tree.removeSpaceBeforeLength();
                    properties.put("output_tree_id", tree.getId());
                    //properties.put("output_unrootedtree_id", tree.getId());
                }
            }
               Util.deleteFile(infile);
               Util.deleteFile("outfile");
               Util.deleteFile("treefile."+filenameIndex);
               Util.deleteFile("checkpoint."+filenameIndex);
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
        return true;
    }
  

}
