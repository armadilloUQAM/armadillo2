/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package programs;

import biologic.Alignment;
import biologic.FastqFile;
import biologic.Genome;
import biologic.Results;
import biologic.Sequence;
import biologic.Text;
import configuration.Util;
import java.util.Vector;
import program.RunProgram;
import static program.RunProgram.PortInputUP;
import static program.RunProgram.df;
import static program.RunProgram.status_error;
import workflows.workflow_properties;


/**
 *
 * @author Brisée-pas-morte
 */
public class Bowtie2 extends RunProgram{
    
        private String infile="infile";  
           private String outfile="outfile.fasq"; 
            private String inputfilename1="";
        private int input_genome_id1=0;

    
    public Bowtie2(workflow_properties properties) {
       this.properties=properties;
       execute();
    }
     public void init_createInput() {
         FastqFile f;
            f = new FastqFile("C:Users\\Brise-pas-morte\\Desktop\\UQAM\\documents\\sample1.fastq");
     f.Output(infile);
   this.addInput(f);   
     
     }
     public String[] init_createCommandLine() {
      
     
     
     
       String[] com=new String[30];
       for (int i=0; i<com.length;i++) com[i]="";
       int index=5;
       com[0]="cmd.exe";
       com[1]="/C";
       com[2]=properties.getExecutable();
    // com[4]=infile;
       com[5]="-q";  //read Fastqfile
       com[6]="-x";
       com[7]="-U <r>";
       com[8]="-S";
      
     
       return com;
    }
      public void post_parseOutput() {
          
          Alignment align=new Alignment();
             align.loadFromFile("outfile");
             align.setName("Bowtie ("+Util.returnCurrentDateAndTime()+")");
             align.setNote("Created on "+Util.returnCurrentDateAndTime());
             align.saveToDatabase();
             properties.put("output_alignment_id", align.getId());
             this.addOutput(align);
             
             
             Text lk=new Text(infile+"_bowti2_lk.txt");

       Results text=new Results(infile+"_bowti2_stats.txt");
       text.setText(text.getText()+"\n"+lk.getText());
       text.setNote("Bowti2_stats ("+Util.returnCurrentDateAndTime()+")");
       text.setName("Bowtie2_stats ("+Util.returnCurrentDateAndTime()+")");

       text.saveToDatabase();
       addOutput(text);
       properties.put("output_results_id",text.getId());
      
      
    }

    
}
