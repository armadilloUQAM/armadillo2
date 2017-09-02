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
import program.RunProgram;
import workflows.workflow_properties;


public class phyml extends RunProgram {
    private String infile="infile";               //Unique infile : Must be deleted at the end
    private String outfile="";              //Unique outfile: Must be deleted at the end
    // Debug and programming variables
    public static boolean debug=true;
    
  

  
    /**
     *
     * Note: il n'y a pas présentement de test d'erreur
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant les séquences à partir desquelles l'arbre est généré
     */
    public phyml(workflow_properties properties) {
       this.properties=properties;
       execute();
    }

    @Override
    public boolean init_checkRequirements() {
        int alignment_id=properties.getInputID("alignment");
        int seqboot_id=properties.getInputID("Phylip_Seqboot");
        if (alignment_id==0&&seqboot_id==0) {
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
         int alignment_id=properties.getInputID("alignment");        
        int seqboot_id=properties.getInputID("Phylip_Seqboot");
         if (alignment_id!=0) {
             Alignment align=new Alignment(alignment_id); 
             align.outputPhylipInterleaveWithSequenceID(infile);      
             this.addInput(align);
         } else {
            Phylip_Seqboot align=new Phylip_Seqboot(seqboot_id);
            align.Output(infile);
            addInput(align);
         }
       
        //--Properties
    }


    @Override
    public String[] init_createCommandLine() {
       // ./phyml [-i seq_file_name] [-d data_type] [-q] [-n nb_data_sets] [-b int] [-m model] [-f e | d | 'fA fC fG fT'] [-t ts/tv_ratio] [-v prop_invar] [-c nb_subst_cat] [-a gamma] [-s move] [-u user_tree_file] [-o 'tlr' | 'tl' | 'tr' | 'l' | 'r' | 'n'] [--rand_start] [--n_rand_starts num] [--r_seed num] [--print_site_lnl] [--print_trace]

       String[] com=new String[30];
       for (int i=0; i<com.length;i++) com[i]="";
       int index=5;
       com[0]="cmd.exe";
       com[1]="/C";
       com[2]=properties.getExecutable();
       com[3]="-i";
       com[4]=infile;
       if (properties.isSet("data_type")) {
           com[index++]="-d";
           com[index++]=properties.get("data_type");
       }
       if (properties.isSet("bootstrap")) {
           com[index++]="-b";
           com[index++]=""+properties.getInt("bootstrap");
       }
       if (properties.isSet("model")) {
           com[index++]="-m";
           com[index++]=properties.get("model");
       }
      if (properties.isSet("search")) {
           com[index++]="-s";
           com[index++]=properties.get("search");
       }
      if (properties.isSet("tv_ratio")) {
           com[index++]="-t";
           com[index++]=properties.get("tv_ratio");
       }
       if (properties.isSet("prop_invar")) {
           com[index++]="-v";
           com[index++]=properties.get("prop_invar");
       }
       if (properties.isSet("gamma")) {
           com[index++]="-a";
           com[index++]=properties.get("gamma");
       }
       com[index++]="--print_site_lnl";
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
