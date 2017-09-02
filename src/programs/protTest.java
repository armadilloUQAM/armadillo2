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
/// Create a Thread to run protTest
///
/// PROTTEST (ModelTest's relative) is a program for selecting the model of protein evolution
/// that best fits a given set of sequences (alignment). This java program is based on the Phyml program
/// (for maximum likelihood calculations and optimization of parameters) and uses the PAL library as well.
/// Models included are empirical substitution matrices (such as WAG, LG, mtREV, Dayhoff, DCMut, JTT, VT,
/// Blosum62, CpREV, RtREV, MtMam, MtArt, HIVb, and HIVw) that indicate relative rates of amino acid replacement,
/// and specific improvements (+I:invariable sites, +G: rate heterogeneity among sites, +F: observed amino acid frequencies)
/// to account for the evolutionary constraints impossed by conservation of protein structure and function.
/// ProtTest uses the Akaike Information Criterion (AIC) and other statistics (AICc and BIC)
/// to find which of the candidate models best fits the data at hand.
/// (From Abascal F, Zardoya R, Posada, D. 2005. ProtTest: Selection of best-fit models of protein evolution. Bioinformatics: 21(9):2104-2105.)
/// http://darwin.uvigo.es/software/prottest.html
///
/// Etienne Lord 2010

import biologic.Alignment;
import biologic.Results;
import biologic.Tree;
import configuration.Util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import program.RunProgram;
import workflows.workflow_properties;


public class protTest extends RunProgram {
    private String infile="infile";               //Unique infile : Must be deleted at the end
    private String outfile="outfile";             //Unique outfile : Must be deleted at the end

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
    public protTest(workflow_properties properties) {        
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
        a.outputPhylipInterleaveWithSequenceID(infile,"");
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
        // Basic usage: java -Xmx250m -classpath ProtTest.jar prottest.ProtTest -i alignm_file
        // Note: the do_run method is overrided with the correct path
        for (int i=0; i<com.length;i++) com[i]="";
           com[0]="java";
           com[1]="-Xmx250m";
           com[2]="-classpath";
           com[3]=properties.getExecutable();
           com[4]="prottest.ProtTest";
           com[5]="-i";
           com[6]=infile;
        return com;
    }

   

    @Override
    public void post_parseOutput() {          
          loadProtTest();
            //--CheckPoint          
            if (!properties.getBoolean(debug)) {
               Util.deleteFile(infile);
               Util.deleteFile(outfile);             
            }
    }

     /**
      * This load the result vector 
      */
     public void loadProtTest() {
        Pattern pm=Pattern.compile("Best model according to (.*): (.*)");
        Pattern t=Pattern.compile("Tree according to best model");
        String tree="";
        String model="";
        String results="";
        boolean found_model=false;
        boolean found_tree=false;

        for (String s:this.getOutputTXT()) {
            Matcher m=pm.matcher(s);
            Matcher m2=t.matcher(s);

            //--Warning order is important - Etienne
            if (found_tree&&s.startsWith("*")) found_tree=false;
            if (found_tree) tree+=s.replaceAll("\n", "");
            if (m2.find()) found_tree=true;

            if (m.find()) {
                model=m.group(2);
                found_model=true;
            }
            if (m2.find()) {
                found_tree=true;
            }

            if (found_model&&s.isEmpty()) found_model=false;
            if (found_model) results+=s;
        }
        Results r=new Results();
        r.setText(results);
        r.setRunProgram_id(this.getId());
        r.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
        r.setRunProgram_id(this.getId());
        r.setNote("ProtTest results created at "+Util.returnCurrentDateAndTime());
        r.saveToDatabase();
        properties.put("output_results_id", r.getId());
        Tree tree2=new Tree();
        tree2.setName("ProtTest tree with model "+model);
        tree2.setNote("ProtTest tree created at "+Util.returnCurrentDateAndTime());
        tree2.setTree(tree);
        tree2.setAlignment_id(properties.getInputID("alignment"));
        tree2.setTreeSequenceID(tree);
        tree2.replaceSequenceIdWithNames();
        tree2.saveToDatabase();
        properties.put("output_tree_id", tree2.getId());
    }

   


    @Override
    public int hashCode() {
         return Util.returnCount();
     }

}
