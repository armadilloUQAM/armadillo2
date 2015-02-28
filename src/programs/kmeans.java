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

////////////////////////////////////////////////////////////////////////////////
///
/// Create a Thread to run Weighted K-means 
///

import biologic.Matrix;
import biologic.Phylip_Distance;
import biologic.Results;
import biologic.Text;
import configuration.Util;
import java.io.File;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;


public class kmeans extends RunProgram {
    private String filename="";
    private String infile="input.txt";               //Unique infile : Must be deleted at the end
    private String infile_weight_matrix="weight.txt";
    private String outfile="input.txt";
    private String outfile_stats=".statistics.txt";             //Unique outfile : Must be deleted at the end
    private String outfile_groups=".groups.txt";                 //Unique outfile : Must be deleted at the end
    private String outfile_bestgroups=".best.groups.txt";                 //Unique outfile : Must be deleted at the end
    private String outfile_matrix=".matrix.txt";                 //Unique outfile : Must be deleted at the end
    private int exitVal=0;
 
    private boolean use_weight=false;
    // Debug and programming variables
    public static boolean debug=true;
     
    /**
     *
     * Note: il n'y a pas présentement de test d'erreur
     *
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant 
     */
    public kmeans(workflow_properties properties) {        
        super(properties);
        execute();       
    }

    @Override
    public boolean init_checkRequirements() {
            Vector<Integer>matrix_id=properties.getInputID("matrix",PortInputUP);
            
            
          if (matrix_id.size()==0) {
            setStatus(status_BadRequirements, "Error: no input matrix found.");
            return false;
        }

        return true;
    }

    @Override
    public void init_createInput() {
         config.temporaryDir(""+Util.returnCount());
          infile=config.temporaryDir()+File.separator+"input"+Util.returnCount()+"_matrix";
          //--Outfile
          outfile=config.temporaryDir()+File.separator+"output"+Util.returnCount();          
          outfile_stats=outfile+outfile_stats;
          outfile_groups=outfile+outfile_groups;
          outfile_bestgroups=outfile+outfile_bestgroups;
          outfile_matrix=outfile+outfile_matrix;
          
          infile_weight_matrix=infile+"weigth.txt";
          infile+=".txt";
          
         Vector<Integer>matrix_id=properties.getInputID("matrix",PortInputUP);         
         Vector<Integer>weight_matrix_id=properties.getInputID("matrix",PortInputDOWN);         
         int id=matrix_id.get(0);
         Matrix matrix=new Matrix(id);
         //--Convert to compatible matrix here
         matrix.Output(infile);
           addInput(matrix); 
         //--Weight matrix here
         
         if (weight_matrix_id.size()>0) {
           int w_id=weight_matrix_id.get(0);
            Matrix weightMatrix=new Matrix(id);         
            weightMatrix.Output(infile_weight_matrix); 
            use_weight=true;
            addInput(weightMatrix);
         }
         
         
    }


    @Override
    public String[] init_createCommandLine() {
        //--Sample
        //exec/kmeans -inputfile=$matrix $str_w -replicate=1000 -kmax=20 -distance=tanimoto -type=kmedoids -outputfile=$simulation_directory_kmedoids/ref1000 >>$log
       int c=6;
        String[] com=new String[20];
                       for (int i=0; i<com.length;i++) com[i]="";  //--Be sure not null value...                     
                       com[0]="cmd.exe";
                       com[1]="/C";                       
                       com[2]=properties.getExecutable();
                       //--Input file
                       com[3]="-inputfile="+infile;
                       //--OutputPrefix
                       com[4]="-outputfile="+outfile;
                        //type
                       if (properties.isSet("type")) {
                          com[5]="-type="+properties.get("type"); 
                       } else {
                          com[5]="-type=kmeans";
                        }
                       //--replicate  (random start)                     
                      if (properties.isSet("nstart")) {
                          com[c++]="-replicate="+properties.getInt("nstart");                                             
                      }                      
                       //--kmax
                        if (properties.isSet("kmax")) {
                          com[c++]="-kmax="+properties.getInt("kmax");                                             
                      }   
                       //--distance
                       if (properties.isSet("distance")) {
                            com[c++]="-distance="+properties.get("distance");   
                       } 
                       if (use_weight) {
                            com[c++]="-weight="+infile_weight_matrix;   
                       }                       
                       //--outfile
                       return com;
    }
   

    @Override
    public void post_parseOutput() {
        
        if (properties.get("type").equals("matrice")) {             
            //--Output a phylip distance compatible file 
            Phylip_Distance phylip=new Phylip_Distance(outfile_matrix);
            String dist=properties.get("distance");            
            phylip.setName("Phylip distance : "+dist+" - "+Util.returnCurrentDateAndTime());
            phylip.setRunProgram_id(id);
            phylip.saveToDatabase();
            properties.setOutput(phylip);
            addOutput(phylip);
            //--Also add matrix 
            Matrix matrix=new Matrix();            
            matrix.loadFromFile(outfile_matrix);
            matrix.setName("Matrix distance : "+dist+" - "+Util.returnCurrentDateAndTime());
            matrix.setRunProgram_id(id);             
            matrix.saveToDatabase();
            properties.setOutput(matrix);
            addOutput(matrix);
        } else {
           Results stats=new Results(outfile_stats);
            stats.setName("Clustering statistics - "+Util.returnCurrentDateAndTime());
            stats.setRunProgram_id(id);
            stats.saveToDatabase();
            properties.setOutput(stats);

            Text groups=new Text(outfile_groups);
            groups.setName("Clustering groups - "+Util.returnCurrentDateAndTime());
            groups.setRunProgram_id(id);
            groups.saveToDatabase();
            properties.setOutput(groups); 
            
            addOutput(stats);
            addOutput(groups);
        }
        
       
        
        
        

//             --Clean UP
       
            // deleteFile(outfile_groups);        
             //deleteFile(outfile_stats);
             //deleteFile(infile);
    }

 
   ////////////////////////////////////////////////////////////////////////////
   // FUNCTIONS


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

    @Override
    public int hashCode() {
        return Util.returnCount();
    }
      
}
