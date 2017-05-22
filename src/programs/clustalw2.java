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

////////////////////////////////////////////////////////////////////////////////
///
/// Create a Thread to run clustalW2
///
/// Etienne Lord 2009


import biologic.Alignment;
import biologic.MultipleSequences;
import biologic.MultipleTrees;
import biologic.Tree;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;


public class clustalw2 extends RunProgram {
    private String filename="";
    private String infile="infile";               //Unique infile : Must be deleted at the end
    private String outfile="outfile.fasta";             //Unique outfile : Must be deleted at the end
  
    private int exitVal=0;
    private String pathClustal="";
    private String pathToOutput="";
    public static String outputType="fasta";       //default output type clustal :: debug only
                                                   // valid value: fasta, clu, aln, msf...       
    private static boolean clustalw2_choice=false;
    private static boolean clustalw2_pair_aln = false;
    private static boolean clustalw2_pair_aln_slow = false;
    private static String clustalw2_pair_aln_slow_gop = "15.0";
    private static String clustalw2_pair_aln_slow_gep = "6.66";
    private static boolean clustalw2_pair_aln_fast = false;
    private static String clustalw2_pair_aln_fast_gp = "5.0";
    private static String clustalw2_pair_aln_fast_k = "2.0";
    private static boolean clustalw2_multi_aln = false;
    private static String clustalw2_multi_aln_gop = "10.0";
    private static String clustalw2_multi_aln_gep = "0.2";
    private static String clustalw2_multi_aln_dnatrans = "0.5";

    //outputType
    public static final String outputTypeClustal="";
    public static final String outputTypeFasta="fasta";
    public static final String outputTypeNexus="nexus";
    public static final String outputTypeMAF="gcg";
    public static final String outputTypeGDE="gde";
    public static final String outputTypePIR="pir";
    public static final String outputTypePhylip="phylip";

    // Debug and programming variables
    public static boolean debug=true;
     
    /**
     *
     * Note: il n'y a pas présentement de test d'erreur
     *
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant 
     */
    public clustalw2(workflow_properties properties) {        
        super(properties);
        execute();       
    }

    @Override
    public boolean init_checkRequirements() {
          int multiplesequences_id=properties.getInputID("multiplesequences"); 
           if (multiplesequences_id==0) {
            setStatus(status_BadRequirements,"Error no multiplesequences_id");
            return false;
           }
          MultipleSequences multitmp=new MultipleSequences(multiplesequences_id);
          if (multitmp.getNbSequence()==0) {
            setStatus(status_BadRequirements,"Error: No Sequences found...");
            return false;
           }
        return true;
    }

    @Override
    public void init_createInput() {
        MultipleSequences multi=new MultipleSequences(properties.getInputID("multiplesequences"));
        multi.PrintPhylip();
        multi.outputFastaWithSequenceID(infile);
        addInput(multi);
    }


    @Override
    public String[] init_createCommandLine() {
          String[] com=new String[20];
                       for (int i=0; i<com.length;i++) com[i]="";
                       // clustalw2 -ALIGN -INFILE=test.fasta -OUTFILE=outfile -NEWTREE=outfile.tre -OUTPUT=PHYLIP

                       com[0]="cmd.exe";
                       com[1]="/C";
                       com[2]=properties.getExecutable();
                       com[3]="-ALIGN";
                       com[4]="-INFILE="+infile;
                       com[5]="-OUTFILE="+outfile; //outfile is in Phylip format (CAN BE CHANGE IN PARAM -OUTPUT: DEFAULT=CLUSTAL)
                       com[6]="-NEWTREE="+outfile+".tre";
                       com[7]="-OUTORDER=INPUT";
                       int index=8;
                       if (properties.isSet("gop")) com[index++]="-GAPOPEN="+properties.get("gop");
                       if (properties.getBoolean("PairwiseAlignment")){
                           if (properties.getBoolean("PairwiseAlignmentSlow")){
                               com[index++]="-PWGAPOPEN="+properties.get("PairwiseAlignmentSlowGop");
                               com[index++]="-PWGAPEXT="+properties.get("PairwiseAlignmentSlowGep");
                           }
                           if (properties.getBoolean("PairwiseAlignmentFast")){
                               com[index++]="-PAIRGAP="+properties.get("PairwiseAlignmentFastGp");
                               com[index++]="-KTUPLE="+properties.get("PairwiseAlignmentFastKtuple");
                           }
                       }
                       if (properties.getBoolean("MultiAlignment")){
                           com[index++]="-GAPOPEN="+properties.get("MultipleAlignmentGop");
                           com[index++]="-GAPEXT="+properties.get("MultipleAlignmentGep");
                           com[index++]="-TRANSWEIGHT="+properties.get("MultipleAlignmentDnaTransition");
                       }

                       if (!outputType.equals("")) com[index++]="-OUTPUT="+outputType.toUpperCase();
                       
                       return com;
    }

    

    @Override
    public void post_parseOutput() {
             Alignment align=new Alignment();
             align.loadFromFile(outfile);
             align.setName("ClustalW2 ("+Util.returnCurrentDateAndTime()+")");
             align.setNote("Created on "+Util.returnCurrentDateAndTime());
             align.saveToDatabase();
             properties.put("output_alignment_id", align.getId());
             addOutput(align);

             MultipleTrees multi=new MultipleTrees();
             multi.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
             multi.readNewick(outfile+".tre");
             multi.setMultiplesequences_id(properties.getInputID("input_multiplesequences_id"));
             multi.replaceSequenceIDwithNames();
             multi.setNote("ClustalW2 ("+Util.returnCurrentDateAndTime()+")");
             multi.saveToDatabase();
             addOutput(multi);
             
             for (Tree tree:multi.getTree()) properties.put("output_tree_id", tree.getId());
             //--Clean UP
             Util.deleteFile(outfile+".tre");
             //Util.deleteFile(outfile);
             Util.deleteFile(infile);
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

  
    ///////////////////////////////////////////////////////////////////////////
    ///GETTER/SETTERS
    
    public static boolean getClustalw2_choice() {
        return clustalw2_choice;
    }

    public static void setClustalw2_choice(boolean aClustalw2_choice) {
        clustalw2_choice = aClustalw2_choice;
    }

    public static boolean getClustalw2_pair_aln() {
        return clustalw2_pair_aln;
    }

    public static void setClustalw2_pair_aln(boolean aClustalw2_pair_aln) {
        clustalw2_pair_aln = aClustalw2_pair_aln;
    }

    public static boolean getClustalw2_pair_aln_slow() {
        return clustalw2_pair_aln_slow;
    }

    public static void setClustalw2_pair_aln_slow(boolean aClustalw2_pair_aln_slow) {
        clustalw2_pair_aln_slow = aClustalw2_pair_aln_slow;
    }

    public static String getClustalw2_pair_aln_slow_gop() {
        return clustalw2_pair_aln_slow_gop;
    }

    public static void setClustalw2_pair_aln_slow_gop(String aClustalw2_pair_aln_slow_gop) {
        clustalw2_pair_aln_slow_gop = aClustalw2_pair_aln_slow_gop;
    }

    public static String getClustalw2_pair_aln_slow_gep() {
        return clustalw2_pair_aln_slow_gep;
    }

    public static void setClustalw2_pair_aln_slow_gep(String aClustalw2_pair_aln_slow_gep) {
        clustalw2_pair_aln_slow_gep = aClustalw2_pair_aln_slow_gep;
    }

    public static boolean getClustalw2_pair_aln_fast() {
        return clustalw2_pair_aln_fast;
    }

    public static void setClustalw2_pair_aln_fast(boolean aClustalw2_pair_aln_fast) {
        clustalw2_pair_aln_fast = aClustalw2_pair_aln_fast;
    }

    public static String getClustalw2_pair_aln_fast_gp() {
        return clustalw2_pair_aln_fast_gp;
    }

    public static void setClustalw2_pair_aln_fast_gp(String aClustalw2_pair_aln_fast_gp) {
        clustalw2_pair_aln_fast_gp = aClustalw2_pair_aln_fast_gp;
    }

    public static String getClustalw2_pair_aln_fast_k() {
        return clustalw2_pair_aln_fast_k;
    }

    public static void setClustalw2_pair_aln_fast_k(String aClustalw2_pair_aln_fast_k) {
        clustalw2_pair_aln_fast_k = aClustalw2_pair_aln_fast_k;
    }

    public static boolean getClustalw2_multi_aln() {
        return clustalw2_multi_aln;
    }

    public static void setClustalw2_multi_aln(boolean aClustalw2_multi_aln) {
        clustalw2_multi_aln = aClustalw2_multi_aln;
    }

    public static String getClustalw2_multi_aln_gop() {
        return clustalw2_multi_aln_gop;
    }

    public static void setClustalw2_multi_aln_gop(String aClustalw2_multi_aln_gop) {
        clustalw2_multi_aln_gop = aClustalw2_multi_aln_gop;
    }

    public static String getClustalw2_multi_aln_gep() {
        return clustalw2_multi_aln_gep;
    }

    public static void setClustalw2_multi_aln_gep(String aClustalw2_multi_aln_gep) {
        clustalw2_multi_aln_gep = aClustalw2_multi_aln_gep;
    }

    public static String getClustalw2_multi_aln_dnatrans() {
        return clustalw2_multi_aln_dnatrans;
    }

    public static void setClustalw2_multi_aln_dnatrans(String aClustalw2_multi_aln_dnatrans) {
        clustalw2_multi_aln_dnatrans = aClustalw2_multi_aln_dnatrans;
    }



    @Override
    public int hashCode() {
        return Util.returnCount();
    }
      
}
