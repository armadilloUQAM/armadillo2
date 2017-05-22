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
/// Create a Thread to run phyML
///
/// "A simple, fast, and accurate algorithm to estimate large phylogenies by maximum likelihood."
/// Guindon S., Gascuel O.
/// Systematic Biology, 52(5):696-704, 2003.
/// Etienne Lord 2009


import biologic.Alignment;
import biologic.MultipleTrees;
import biologic.Phylip_Seqboot;
import biologic.Results;
import biologic.Text;
import biologic.Tree;
import configuration.Util;
import java.io.File;
import program.RunProgram;
import workflows.workflow_properties;


public class oblong extends RunProgram {
    private String infile="infile_ninga";               //Unique infile : Must be deleted at the end
    private String outfile="outfile_ninja";              //Unique outfile: Must be deleted at the end
    private String tmp_dir="";
    // Debug and programming variables
    public static boolean debug=true;
    
  

  
    /**
     *
     * Note: il n'y a pas présentement de test d'erreur
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant les séquences à partir desquelles l'arbre est généré
     */
    public oblong(workflow_properties properties) {
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
          infile=config.temporaryDir()+File.separator+"input"+Util.returnCount()+"_ninja";
          //--Outfile
          outfile=config.temporaryDir()+File.separator+"output"+Util.returnCount()+"_ninja";        
        
         int alignment_id=properties.getInputID("alignment");        
       
         if (alignment_id!=0) {
             Alignment align=new Alignment(alignment_id); 
             align.outputFasta(infile);      
             this.addInput(align);
         } 
       
        //--Properties
    }


    @Override
    public String[] init_createCommandLine() {
      //--  java -server -Xmx2G -jar Ninja.jar --in alignment.fasta --out tree.newick
        
       String memory="1G"; 
       if (properties.isSet("memory")) {
           memory=properties.get("memory");
       }
       String[] com=new String[30];
       for (int i=0; i<com.length;i++) com[i]="";       
       int index=9;
          com[0]="java"; //--Minus the .exe           
          com[1]="-Xmx"+memory;
           com[2]="-jar";
           //File f=new File(properties.getExecutable());
           com[3]=properties.getExecutable();
           com[4]="--in";
           com[4]=infile;
           com[5]="--out";
           com[6]=outfile;
           com[7]="--tmp_dir";
           com[8]=tmp_dir;
           if (properties.getBoolean("output_matrix")) {
               com[index++]="--out_type";
               com[index++]="d"; //--Phylip distance
            }
           if (properties.isSet("corr_type")) {
//               --corr_type type [n | j | k | s]
//    Correction for multiple same-site substitutions.
//    'n' no correction
//    'j' jukes-cantor correction  { dist = -3/4 * ln (1 - 4/3 * dist ) }
//    'k' kimura 2-parameter correction { dist = -1/2 * ln ( (1-2p-q)*sqrt(1-2q) ) }    
//    's' FastTree's scoredist-like correction { dist = -1.3 * ln (1.0 - dist) }
//    Default: 'k' for DNA, 's' for amino acid
               
               
               com[index++]="--corr_type";
               com[index++]=properties.get("corr_type"); 
               
           }
           
          if (properties.isSet("clust_size"))  {
            //  --clust_size (or -s)  
            // See paper for details.  Default = 30.
                 com[index++]="--clust_size";
                 com[index++]=properties.get("clust_size"); 
          }
           if (properties.isSet("rebuild_step_ratio"))  {
           //--rebuild_step_ratio (or -r)  
            //See paper for details.  Default = 0.5.
               com[index++]="--clust_size";
                 com[index++]=properties.get("rebuild_step_ratio"); 
           }
           
           
    
       return com;
    }

    @Override
    public void post_parseOutput() {
       //
       // Note: Phylip tree include bootstrap and
       //  branch length as )bootstrap:branch length..
       // which is not valid newick...
       MultipleTrees tree=new MultipleTrees();
       tree.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
       tree.setNote("phyML ("+Util.returnCurrentDateAndTime()+")");
       tree.setRunProgram_id(this.getId());
       tree.readNewick(infile+"_phyml_tree.txt");
       if (properties.getBoolean("remove_bootstrap")) {
          for (Tree t:tree.getTree()) t.removeBootstrap();
       }
       tree.setAlignment_id(properties.getInputID("input_alignment_id"));       
       tree.replaceSequenceIDwithNames();
       tree.saveToDatabase();
        //--Note we only return the last tree_id
       for (Tree t:tree.getTree()) properties.put("output_tree_id", t.getId());
       //--
       Text lk=new Text(infile+"_phyml_lk.txt");

       Results text=new Results(infile+"_phyml_stats.txt");
       text.setText(text.getText()+"\n"+lk.getText());
       text.setNote("PhyML_stats ("+Util.returnCurrentDateAndTime()+")");
       text.setName("PhyML_stats ("+Util.returnCurrentDateAndTime()+")");

       text.saveToDatabase();
       addOutput(text);
       properties.put("output_results_id",text.getId());

       Util.deleteFile(infile);
       Util.deleteFile(infile+"_phyml_stats.txt");
       Util.deleteFile(infile+"_phyml_tree.txt");
       Util.deleteFile(infile+"_phyml_boot_stats.txt");
       Util.deleteFile(infile+"_phyml_boot_trees.txt");
       Util.deleteFile(infile+"_phyml_lk.txt");
    }

}
