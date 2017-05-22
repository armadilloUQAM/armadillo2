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
/// Create a Thread to run clustalW
///
/// Etienne Lord 2010


import biologic.Alignment;
import biologic.MultipleSequences;
import biologic.MultipleTrees;
import biologic.Tree;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;


public class clustalw extends RunProgram {   
    private String infile="infile";               //Unique infile : Must be deleted at the end
    private String outfile="outfile.fasta";       //Unique outfile : Must be deleted at the end 
    public static String outputType="fasta";       //default output type clustal :: debug only
                                                   // valid value: fasta, clu, aln, msf...       

    //outputType
    public static final String outputTypeClustal="";
    public static final String outputTypeFasta="fasta";
    public static final String outputTypeNexus="nexus";
    public static final String outputTypeMAF="gcg";
    public static final String outputTypeGDE="gde";
    public static final String outputTypePIR="pir";
    public static final String outputTypePhylip="phylip";

    public clustalw(workflow_properties properties) {        
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
                       // clustalw -ALIGN -INFILE=test.fasta -OUTFILE=outfile -NEWTREE=outfile.tre -OUTPUT=PHYLIP
                       com[0]="cmd.exe";
                       com[1]="/C";
                       com[2]=properties.getExecutable();
                       com[3]="-align";
                       com[4]="-infile="+infile;
                       com[5]="-outfile="+outfile; //outfile is in fasta format (CAN BE CHANGE IN PARAM -OUTPUT: DEFAULT=CLUSTAL)
                       com[6]="-newtree="+outfile+".tre";
                       int index=7;
                       if (properties.isSet("gop")) com[index++]="-gapopen="+properties.get("gop");
                       if (properties.isSet("gep")) com[index++]="-gapext="+properties.get("gep");
                       if (properties.isSet("transition_weight")) com[index++]="-transweight="+properties.get("transition_weight");
                       if (properties.isSet("moltype")) com[index++]="-type="+(properties.get("moltype").equals("N")?"dna":"protein");
                       
                       if (!outputType.equals("")) com[index++]="-output="+outputType.toUpperCase();
                       return com;
    }

    

    @Override
    public void post_parseOutput() {
             Alignment align=new Alignment();
             align.loadFromFile(outfile);
             align.setName("ClustalW ("+Util.returnCurrentDateAndTime()+")");
             align.saveToDatabase();
             properties.put("output_alignment_id", align.getId());
             addOutput(align);

//             MultipleTrees multi=new MultipleTrees();
//             multi.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
//             multi.readNewick(outfile+".tre");
//             multi.setMultiplesequences_id(properties.getInputID("input_multiplesequences_id"));
//             multi.replaceSequenceIDwithNames();
//             multi.setNote("ClustalW ("+Util.returnCurrentDateAndTime()+")");
//             multi.saveToDatabase();
//             addOutput(multi);             
             //for (Tree tree:multi.getTree()) properties.put("output_tree_id", tree.getId());
             //--Clean UP
             //Util.deleteFile(outfile+".tre");
             Util.deleteFile(outfile);
             Util.deleteFile(infile);
    }

 

      
}
