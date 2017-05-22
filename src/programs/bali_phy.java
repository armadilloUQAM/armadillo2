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
/// Create a Thread to run probcons
///
/// Etienne Lord 2009

import biologic.Alignment;
import biologic.InfoSequence;
import biologic.MultipleSequences;
import biologic.MultipleTrees;
import biologic.Results;
import biologic.Sequence;
import configuration.Config;
import configuration.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import program.RunProgram;
import workflows.workflow_properties;


public class bali_phy extends RunProgram {

    private String infile="infile.fasta";               //Unique infile : Must be deleted at the end
    private String outfile="outfile";              //Unique outfile: Must be deleted at the end
    
    ////////////////////////////////////////////////////////////////////////////
    /// Input / Output

    private MultipleSequences multiplesequences;

    public bali_phy(workflow_properties properties) {        
       super(properties);        
       execute();
    }

    @Override
    public boolean init_checkRequirements() {
        int multiplesequences_id=properties.getInputID("multiplesequences");
        if (multiplesequences_id==0) {
            setStatus(status_BadRequirements,"No sequence found.");
            return false;
        }
        return true;
    }

    @Override
    public void init_createInput() {
         //int alignment_id=properties.getInputID("alignment");
         int multiplesequences_id=properties.getInputID("multiplesequences");
         multiplesequences=new MultipleSequences(multiplesequences_id);
         multiplesequences.outputFastaWithSequenceID(infile);
         //--Remove dir if found
         if (Util.DirExists("infile-1")) {
             Util.deleteDir("infile-1");
         }
    }

    
    @Override
    public void post_parseOutput() {
        if (properties.getBoolean("AlignmentInfo")) {
           Results results=new Results(outfile);
            results.setName("BAli-phy results for "+multiplesequences.getName()+" at "+Util.returnCurrentDateAndTime());
            results.setNote("Created at "+Util.returnCurrentDateAndTime());
            results.saveToDatabase();
            properties.put("output_results_id", results.getId()); 
        } else {
            //--Bali-Phy output
            Results results=new Results("infile-1/C1.out");
            results.setName("BAli-phy ("+Util.returnCurrentDateAndTime()+")");
            results.setNote("Created at "+Util.returnCurrentDateAndTime());
            results.saveToDatabase();
            properties.put("output_results_id", results.getId());
            //--Trees
            MultipleTrees trees=new MultipleTrees();
            trees.readNewick("infile-1/C1.trees");
            trees.replaceSequenceIDwithNames();
            trees.setName("BAli-phy ("+Util.returnCurrentDateAndTime()+")");
            trees.setNote("Created at "+Util.returnCurrentDateAndTime());
            trees.saveToDatabase();
            properties.put("output_multipletrees_id", trees.getId());
            //--Alignment
            Alignment alignment=new Alignment();
            alignment.loadFromFile("infile-1/C1.P1.fastas");
            alignment.setName("BAli-phy All ("+Util.returnCurrentDateAndTime()+")");
            //Remove not good
            for (int i=alignment.getSequences().size()-1;i>-1;i--) {
                Sequence s=alignment.getSequences().get(i);
                if (s.getName().matches("A[0-9]*")) {
                    alignment.getSequences().remove(i);
                } 
            }
            alignment.setNote("This represent alls the iterations performed by BAli-Phy\nCreated at "+Util.returnCurrentDateAndTime());
            alignment.saveToDatabase();            
            alignment.setName("BAli-phy ("+Util.returnCurrentDateAndTime()+")");            
            alignment.setId(0);
            //--Remove all except last
             int multiplesequences_id=properties.getInputID("multiplesequences");
             multiplesequences=new MultipleSequences(multiplesequences_id);
            int number_sequences_initial=multiplesequences.getNbSequence();
            int current_sequences=alignment.getNbSequence();
            if (current_sequences>number_sequences_initial) {
                //--Remove sequences
                while(alignment.getNbSequence()!=number_sequences_initial) alignment.getSequences().remove(0);
            }
            alignment.saveToDatabase();
            properties.put("output_alignment_id", alignment.getId());           
        }
        Util.deleteFile(infile);
        Util.deleteFile(outfile);
    }

    @Override
    public String[] init_createCommandLine() {
          String[] com=new String[20];
           for (int i=0; i<com.length;i++) com[i]="";
          int index=3;
           com[0]="cmd.exe";
           com[1]="/C";
           com[2]=properties.getExecutable();
           //--Display sequence info (1)
           if (properties.getBoolean("AlignmentInfo")) {
               com[index++]=infile;
               com[index++]=">"+outfile;
           } else
           //--Display sequence info (2)
           if (properties.getBoolean("ShowOnly")) {
               com[index++]="--show-only";
               com[index++]=infile;
               com[index++]=">"+outfile;
           } else {             
              //--Normal baliphy
               if (properties.isSet("iterations")) {
               com[index++]="-i";
               com[index++]=properties.get("iterations");
              }
            if (properties.isSet("preburning")) {
               com[index++]="--pre-burnin";
               com[index++]=properties.get("preburning");
            }
            if (properties.isSet("subsample")) {
               com[index++]="--subsample";
               com[index++]=properties.get("subsample");
            }
            if (properties.isSet("alphabet")) {
               com[index++]="--alphabet";
               com[index++]=properties.get("alphabet");
            }
             if (properties.isSet("prior")) {
               com[index++]="--branch-prior";
               com[index++]=properties.get("prior");
            }
            if (properties.isSet("imodel")) {
               com[index++]="--imodel";
               com[index++]=properties.get("imodel");
            }
            if (properties.isSet("smodel")) {
               com[index++]="--smodel";
               com[index++]=properties.get("smodel");
            }
              com[index++]=infile;
           }
           return com;
    }


   /**
    * Special class to load alignment
    */
   class BalyAlignment extends Alignment {

       Vector<Integer>Alignment_id=new Vector<Integer>();
        /**
         * We read file as fasta but we group sequences by iteration
         * @param filename
         * @return
         */
        @Override
        public boolean loadSequences(String filename) {

       this.name=filename;
              try {
                  seq.clear(); //Clear the sequence vector and open the file
                  BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
                  //VARIABLES
                  boolean sequenceMode=false;
                  Sequence tmp = new Sequence();                        //Temp sequence
                  StringBuilder tmpsequence=new StringBuilder();        //Temp sequence string
                  String stri="";                                       //Temp read line

                  //Read the file in a buffer an parse at the same time
                 //Process :: We read like a fasta file
                  Config.log("Reading fasta:"+filename+":");
                  int count=0;
                  String buffer_iteration=""; //Counter for iterations;
                  Pattern iter=Pattern.compile("iterations = ([0-9]*)");
                  while (br.ready()) {
                      stri=br.readLine();
                      Matcher m=iter.matcher(stri);
                      if (m.find()) {
                          //--Save current Alignment and add to group
                          if (!buffer_iteration.isEmpty()) {
                              //--Replace name
                              if (getNbSequence()>0) {
                                    for (Sequence s:getSequences()) {
                                        try {
                                           String sname=(s.getName().startsWith("AZ")?s.getName().substring(2):s.getName());
                                           int original_sequence_id=Integer.valueOf(sname);
                                           InfoSequence info=df.getInfoSequence(original_sequence_id);
                                           //System.out.println(info);
                                           s.setName(info.getName());
                                           s.setGi(info.getGi());
                                           s.setAccession(info.getAccession());
                                           s.setAccession_referee(info.getAccession_referee());
                                           s.setAbbreviate(info.getAbbreviate());
                                           s.setSequence_type(info.getSequence_type());
                                           s.setOriginal_id(original_sequence_id);
                                           //System.out.println(s.getAbbreviate());
                                         } catch(Exception e) {}
                                    }
                                    this.setName("Iterations_"+m.group(1)+"_"+name);
                                    this.setNote("Created on "+Util.returnCurrentDateAndTime());
                                    this.saveToDatabase();
                                    this.setId(0);
                                    this.getSequences().removeAllElements();
                           }
                          buffer_iteration=stri;
                      }
                      if (sequenceMode&&(stri.equals("")||stri.startsWith(">"))) {
                          tmp.setSequence(tmpsequence.toString());
                          tmp.loadInfoFromName();
                          //Add sequence if not empty
                          if (tmp.getSequence().length()>0) seq.add(tmp);
                          tmp=new Sequence();
                          tmpsequence=new StringBuilder();
                          sequenceMode=false;
                      }
                      if (sequenceMode) {
                          tmpsequence.append(stri);
                          count++;
                          if (count%10000==0) Config.log("*");
                      }
                      if (stri.startsWith(">")) {
                            //We have a fasta definition
                            tmp.setName(stri.substring(1)); //remove >
                            sequenceMode=true;
                      }

                  } //end while
                  //Add last read
                  if (sequenceMode) {
                      tmp.setSequence(tmpsequence.toString());
                      tmp.loadInfoFromName();
                      if (tmp.getSequence().length()>0) seq.add(tmp);
                      tmp=new Sequence();
                  }
                  br.close();
                  if (seq.size()==0) {
                      if (isDebug()) Config.log("not fasta...");
                      return false;
                  }
                  }
               } catch(Exception e) {e.printStackTrace();Config.log("Error with "+filename); return false;}
                if (isDebug()) Config.log("done");


            return true;
        }


   }

 
}
