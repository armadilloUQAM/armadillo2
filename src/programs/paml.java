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
/// Create a Thread to run  paml
///
/// Etienne Lord 2009

import biologic.Alignment;
import biologic.Results;
import biologic.Sequence;
import biologic.Text;
import biologic.Tree;
import biologic.seqclasses.GeneticCode;
import biologic.seqclasses.Translator;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;


public class paml extends RunProgram {

    private String infile="infile.fasta";             //Unique infile : Must be deleted at the end
    private String infiletree="infile.trees";
    private String outfile="outfile.txt";              //Unique outfile: Must be deleted at the end

    ////////////////////////////////////////////////////////////////////////////
    /// Input / Output

    private Alignment align;
    Translator translator=new Translator();
    GeneticCode code;

    public paml(workflow_properties properties) {
       super(properties);
       execute();
    }

    @Override
    public boolean init_checkRequirements() {
        int alignment_id=properties.getInputID("alignment");
        int tree_id=properties.getInputID("tree");
        if (alignment_id==0) {
            setStatus(status_BadRequirements,"No sequence found.");
            return false;
        }
        if (tree_id==0&&!properties.getBoolean("yn00")) {
            setStatus(status_BadRequirements,"No tree found.");
            return false;
        }
        if (properties.getBoolean("preprocess_alignment")) {
            code=Translator.code.get(properties.getInt("genetic_code"));
            if (code==null) {
                setStatus(status_BadRequirements,"Unable to set the GeneticCode...");
                return false;
            }
        }
        return true;
    }

    @Override
    public void init_createInput() {
         int alignment_id=properties.getInputID("input_alignment_id");
         int tree_id=properties.getInputID("tree");
         if (properties.getBoolean("preprocess_alignment")) {
             setStatus(status_running,"PAML (making compatible codon alignment)...");
             //--Run internal properties
             if (alignment_id!=0) {
                Alignment original=new Alignment(alignment_id);
                Alignment protein=new Alignment();
                protein.setName("Protein from "+original.getName());
                protein.setNote("Convertion of Alignment to Protein at "+Util.returnCurrentDateAndTime());
                for (Sequence s:original.getSequences()) {
                    String aa="";
                    aa=translator.translate_without_stop(s, code);
                    Sequence s2=s.clone();
                    s2.setId(0);
                    s2.setSequence(aa);
                    s2.setSequence_type("aa");
                    protein.add(s2);
                }
                Alignment output=new Alignment();
                output.setName("PAML (make-compatible) Created on "+Util.returnCurrentDateAndTime());
                for (int i=0; i<protein.getSequences().size();i++) {
                    Sequence prot=protein.getSequences().get(i);
                    for (Sequence s:original.getSequences()) {
                        if (prot.getName().equals(s.getName())) {
                            Sequence n=new Sequence();
                            n.setName(prot.getName());
                            n.setSequence(translator.aa_to_dna(s, prot, code));
                            n.setSequence_type("dna");
                            output.add(n);
                        }
                    }
                }
                output.saveToDatabase();               
                alignment_id=output.getId();
            }
         }
         Alignment multi=new Alignment(alignment_id);
         if (!properties.isSet("seqtype")||properties.getInt("seqtype")==1||properties.getInt("seqtype")==3) {
             //--Calculate the number of gap to add at the end
             //--Get the first sequence             
             Sequence s=multi.getSequences().get(0);
             int len_to_add=s.getLen()%3;
             System.out.println(len_to_add);
             String str_to_add=(len_to_add==0?"":len_to_add==1?"--":"-");
             for (Sequence s2:multi.getSequences()) {
                 s2.setSequence(s2.getSequence()+str_to_add);
             }            
         }

         //--remove _ in name if found
//         for (Sequence s:multi.getSequences()) {
//             //s.setName(s.getName().replace('_','u'));
//         }


         multi.outputFasta(infile);
         addInput(multi);

         Tree tree=new Tree(tree_id);
         tree.removeBootstrap(); //--remove bootstrap
         //Config.log(tree);
         tree.outputNewick(infiletree);
         addInput(tree);
         if (properties.getBoolean("baseml")){
            createbasemlCTL();
            Text text=new Text("baseml.ctl");
            text.setName("baseml.ctl");
            text.setNote("Created on "+Util.returnCurrentDateAndTime());
            text.saveToDatabase();
            properties.put("output_text_id", text.getId());
            addInput(text);
         } else if (properties.getBoolean("yn00")){
            createyn00CTL();
            Text text=new Text("yn00.ctl");
            text.setName("yn00.ctl");
            text.setNote("Created on "+Util.returnCurrentDateAndTime());
            text.saveToDatabase();
            properties.put("output_text_id", text.getId());
            addInput(text);
         }
         else {
            createcodemlCTL();
            Text text=new Text("codeml.ctl");
            text.setName("codeml.ctl");
            text.setNote("Created on "+Util.returnCurrentDateAndTime());
            text.saveToDatabase();
            properties.put("output_text_id", text.getId());
            addInput(text);
         }
    }

    public void createcodemlCTL() {
        Util ctl=new Util();
        ctl.open("codeml.ctl");
        ctl.println("seqfile = "+infile);
        ctl.println("treefile = "+this.infiletree);
        ctl.println("outfile = "+this.outfile);
        ctl.println("noisy = 9");
        ctl.println("verbose = 1");
        ctl.println("runmode = 0");
        if (properties.isSet("seqtype")) {
            ctl.println("seqtype = "+properties.getInt("seqtype"));
        } else ctl.println("seqtype = 1"); //Default codons
        //--Option
        ctl.println(properties.get("codeml.ctl"));
        ctl.close();
    }

     public void createbasemlCTL() {
        Util ctl=new Util();
        ctl.open("baseml.ctl");
        ctl.println("seqfile = "+infile);
        ctl.println("treefile = "+this.infiletree);
        ctl.println("outfile = "+this.outfile);
        ctl.println("noisy = 9");
        ctl.println("verbose = 1");
        ctl.println("runmode = 0");       
        //--Option
        ctl.println(properties.get("codeml.ctl"));
        ctl.close();
    }

      public void createyn00CTL() {
        Util ctl=new Util();
        ctl.open("yn00.ctl");
        ctl.println("seqfile = "+infile);
        ctl.println("outfile = "+this.outfile);
        //--Option
        ctl.println(properties.get("codeml.ctl"));
        ctl.close();
    }

    @Override
    public void post_parseOutput() {
        Results text=new Results(outfile);
        text.setName("PAML outfile (MLC) created on "+Util.returnCurrentDateAndTime());
        text.setNote("PAML outfile (MLC) created on "+Util.returnCurrentDateAndTime());
        text.saveToDatabase();
        properties.put("output_results_id", text.getId());
        Util.deleteFile(infile);
        //Util.deleteFile(infiletree);
        Util.deleteFile(outfile);
    }

    @Override
    public String[] init_createCommandLine() {
          String[] com=new String[20];
           for (int i=0; i<com.length;i++) com[i]="";
          int index=7;
          com[0]="cmd.exe";
           com[1]="/C";
           com[2]=properties.getExecutable();           
           return com;
    }


    private String uniqueInfileOutfile(String filename) {
        if (infile.equals("")) this.infile=filename+this.hashCode();
        if (outfile.equals("")) this.outfile=filename+this.hashCode();
        return infile;
    }  

}
