/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package programs;

import biologic.Alignment;
import biologic.FastqFile;
import biologic.Genome;
import biologic.MultipleSequences;
import biologic.Results;
import biologic.Sequence;
import biologic.Text;
import configuration.Util;
import java.util.Vector;
import program.RunProgram;
import static program.RunProgram.PortInputDOWN;
import static program.RunProgram.PortInputUP;
import static program.RunProgram.df;
import static program.RunProgram.status_error;
import workflows.workflow_properties;


/**
 *
 * @author Brisée-pas-morte
 */
public class Bwa extends RunProgram{     
        private String infile="infile";  
        private String outfile="outfile.sam"; 
        private String inputfilename1="";
        private int input_genome_id1=0;

    
    public Bwa(workflow_properties properties) {
       this.properties=properties;
       execute();
    }
    
     public boolean init_checkRequirements() {
    
            Vector<Integer> genome_id=properties.getInputID("Genome", PortInputUP);            
         
         if (genome_id.size()>0) {             
             Genome genome=new Genome(genome_id.get(0));   
            input_genome_id1=genome.getId();
            //--We need the properties for this genome: for the moment only the filename
             workflow_properties genome_prop=new workflow_properties();
             genome_prop.deserializeFromString(genome.getText());               
             //--Get the filename 
             if (genome_prop.isSet("inputname"))  inputfilename1=genome_prop.get("inputname");
            
             if (inputfilename1.isEmpty()||!Util.FileExists(inputfilename1)) {
                 this.setStatus(RunProgram.status_BadRequirements,"Unable to find the first reads file: "+inputfilename1);
                 return false;
             }
          
             
         }
      
       return true;
     }
         
        
    
     public String[] init_createCommandLine() {
         
       String[] com=new String[30];
       for (int i=0; i<com.length;i++) com[i]="";
 
       com[0]="cmd.exe";
       com[1]="/C";
       com[2]=properties.getExecutable();     
  
        com[4]="mem";
         com[5]="infile";               
           com[6]="outfile"; 
       

       return com;
    }
      public void post_parseOutput() {
          
          Alignment align=new Alignment();
             align.loadFromFile(outfile);
             align.setName("Bwa ("+Util.returnCurrentDateAndTime()+")");
             align.setNote("Created on "+Util.returnCurrentDateAndTime());
             align.saveToDatabase();
             properties.put("output_alignment_id", align.getId());
             addOutput(align);
             
             
             Text lk=new Text(infile+"bwa.txt");

       Results text=new Results(infile+"_bwa_stats.txt");
       text.setText(text.getText()+"\n"+lk.getText());
       text.setNote("Bwa_stats ("+Util.returnCurrentDateAndTime()+")");
       text.setName("Bwa_stats ("+Util.returnCurrentDateAndTime()+")");

       text.saveToDatabase();
       addOutput(text);
       properties.put("output_results_id",text.getId());
      
      
    }

    
}
