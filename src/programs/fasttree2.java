/*
 *  Armadillo Workflow Platform v1.0
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
/// Create a Thread to run raxml
///
/// Etienne Lord 2013

import configuration.Config;
import biologic.Alignment;
import biologic.MultipleTrees;
import biologic.Results;
import biologic.Tree;
import biologic.Unknown;
import configuration.Util;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import program.RunProgram;
import static program.RunProgram.config;
import workflows.workflow_properties;


public class fasttree2 extends RunProgram {
  
   private String infile="infile_fasttree2";               //Unique infile : Must be deleted at the end
    private String outfile="outfile_fasttree2";              //Unique outfile: Must be deleted at the end
    private String tmp_dir="";

    public static boolean debug=true;
    // For the Thread version
    private int status=0;           //status code
   
   
    /**
     *
     * Note: il n'y a pas présentement de test d'erreur
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant les séquences à partir desquelles l'arbre est généré
     */
    public fasttree2(workflow_properties properties) {        
        this.properties=properties;        
        execute();
    }

    @Override
    public boolean init_checkRequirements() {
      int alignment_id=properties.getInputID("alignment");
      
        if (alignment_id==0) {
            setStatus(status_error,"Error: No alignment found.");
            return false;
        } 
        
        //--Minimum 3 sequence
        if (alignment_id!=0) {
            Alignment a=new Alignment(alignment_id);
            if (a.getNbSequence()<4) {
                setStatus(status_error,"Error: Not enough sequences (<3) in alignment found.");
                return false;
            }
        }
        return true;
    }

    @Override
    public void init_createInput() {        
       config.temporaryDir(""+Util.returnCount());
         tmp_dir=config.temporaryDir();
          infile=config.temporaryDir()+File.separator+"input"+Util.returnCount()+"_fasttree2";
          //--Outfile
          outfile=config.temporaryDir()+File.separator+"output"+Util.returnCount()+"_fasttree2";        
        
         int alignment_id=properties.getInputID("alignment");        
       
         if (alignment_id!=0) {
             Alignment align=new Alignment(alignment_id); 
             align.outputFasta(infile);      
             this.addInput(align);
         } 
    }


    @Override
    public String[] init_createCommandLine() {
        String[] com=new String[10];
        int index=3;
        for (int i=0; i<com.length;i++) com[i]="";
                       com[0]="cmd.exe";
                       com[1]="/C";
                       com[2]=properties.getExecutable();                       
                       if (properties.isSet("data_type")&&properties.get("data_type").equals("nt")) {
                           com[index++]="-nt";
                       }
                       if (properties.getBoolean("fasttest")) {
                           com[index++]="-fastest";
                       }
                       com[index++]=infile;                       
                       com[index++]=">"+outfile;
        return com;
    }

   

    @Override
    public void post_parseOutput() {
          String filenameIndex="";
            int index=0; //index de la ligne
            //--Find stdout in outfile...
           
           MultipleTrees tree=new MultipleTrees();
       tree.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
       tree.setNote("FastTree2 ("+Util.returnCurrentDateAndTime()+")");
       tree.setRunProgram_id(this.getId());
       tree.readNewick(outfile);
       //--Replace number with ,
         Pattern replaceVirValue=Pattern.compile("(0,[0-9]*)");   
          for (Tree t:tree.getTree()) {
              String tt=t.getTree();
              Matcher m=replaceVirValue.matcher(tt); 
                    while(m.find()) {
                       String data= m.group(1);
                       String data2=data.replaceAll(",", ".");
                        tt=tt.replaceAll(data,data2);
                    } 
                    t.setTree(tt);
          }
       
         
       if (properties.getBoolean("remove_bootstrap")) {
          for (Tree t:tree.getTree()) t.removeBootstrap();
       }
       tree.setAlignment_id(properties.getInputID("input_alignment_id"));       
       tree.replaceSequenceIDwithNames();
       tree.saveToDatabase();
        //--Note we only return the last tree_id
       for (Tree t:tree.getTree()) properties.put("output_tree_id", t.getId());
              
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
