package programs;

import biologic.Alignment;
import biologic.FastqFile;
import biologic.Genome;
import biologic.MultipleSequences;
import biologic.Results;
import biologic.Sequence;
import biologic.Text;
import biologic.TextFile;
import configuration.Util;
import java.util.Vector;
import program.RunProgram;
import static program.RunProgram.df;
import static program.RunProgram.status_error;
import workflows.workflow_properties;


/**
 *
 * 
 * @author Fatoumata Sylla	
​ * @since July 2014
 * 
 */
public class Maq extends RunProgram{
        private String infile="infile";  
        private String outfile="outfile.map"; 
        
        private String inputfilename1="";
        private String inputfilename2=""; //--In case of paired file
        private int input_genome_id1=0;
        private int input_genome_id2=0;
        
        private String referencefilename=""; //--In case of paired file
        
    
    public Maq(workflow_properties properties) {
       this.properties=properties;
       execute();
    }
    
    

    @Override
    public boolean init_checkRequirements() {
        //--Read what we have 
         //--Input 1. Fastq 
            Vector<Integer> genome_id=properties.getInputID("Genome", PortInputUP);
         //--For the moment, only handle single entry
            Vector<Integer> genome_id2=properties.getInputID("Genome", PortInputDOWN);                  
            Vector<Integer> multiplesequence_id2=properties.getInputID("MultipleSequences", PortInputDOWN);              
         
         if (genome_id.size()>0) {             
             Genome genome=new Genome(genome_id.get(0));   
            input_genome_id1=genome.getId();
            //--We need the properties for this genome: for the moment only the filename
             workflow_properties genome_prop=new workflow_properties();
             genome_prop.deserializeFromString(genome.getText());               
             //--Get the filename 
             if (genome_prop.isSet("inputname"))  inputfilename1=genome_prop.get("inputname");
             if (genome_prop.isSet("inputname2")) inputfilename2=genome_prop.get("inputname2");  
           
             if (inputfilename1.isEmpty()||!Util.FileExists(inputfilename1)) {
                 this.setStatus(RunProgram.status_BadRequirements,"Unable to find the first reads file: "+inputfilename1);
                 return false;
             }
             if (!inputfilename2.isEmpty()&&!Util.FileExists(inputfilename2)) {
                 this.setStatus(RunProgram.status_BadRequirements,"Unable to find the paired-reads file: "+inputfilename2);
                 return false;
             }
             
         } else if (!properties.getBoolean("convert")){
             this.setStatus(RunProgram.status_BadRequirements,"No entry genome found...");
             return false;
         }
         //--Do we need a reference genome (? We assume a bfq file...
         //--Se readme...
         if (genome_id2.size()>0) {
             Genome genome=new Genome(genome_id2.get(0)); 
              input_genome_id2=genome.getId();
             //--We need the properties for this genome: for the moment only the filename
             workflow_properties genome_prop=new workflow_properties();
             genome_prop.deserializeFromString(genome.getText());               
             //--Get the filename              
             referencefilename=genome_prop.get("inputname");             
         }
         //--Will need to maq fastq2bfq reads.fastq reads.bfa
             //maq fasta2bfa ref.fasta ref.bfa
         if (multiplesequence_id2.size()>0) {
             MultipleSequences multi=new MultipleSequences(multiplesequence_id2.get(0));
             //--Create a first fasta for the input
             multi.outputFasta(multi.getName());
             referencefilename=multi.getName();
             //--WE will need to convert it 
         }
         //--Verify that the reference if a true reference
         if (properties.getBoolean("map")&&!referencefilename.endsWith(".bfa")) {
             this.setStatus(RunProgram.status_BadRequirements,"Error. We expect the reference genome to be a .bfa file.\nTo create one, use the option 'convert' from a multiple sequences file.");
             return false;
         }
         
         return true;
    }
    
     public String[] init_createCommandLine() {
     
       String[] com=new String[30];
       for (int i=0; i<com.length;i++) com[i]="";
       int index=3;
       com[0]="cmd.exe";
       com[1]="/C";
       com[2]=properties.getExecutable();     
       if (properties.getBoolean("map")) {
           //--This is a little special since multiple option can append
           //maq fastq2bfq reads.fastq reads.bfq
           //--First, we migth need to convert the input fastq file into bfq
           //-- if its paired, we need to 
           
           setStatus(status_running, "Mapping with :"+inputfilename1+"\nReference:"+referencefilename);
                     if (inputfilename1.endsWith(".bfq")) {
                        System.out.println("Ok for reads: "+inputfilename1);               
                    } else {
                        //convert
                        Maq maq=new Maq(properties);
                        Genome reads1=maq.maq_convert(inputfilename1);
                        if (reads1.getId()==0) {
                            this.setStatus(status_error, "Unable to convert reads to MAQ format for "+inputfilename1);
                        } else {
                           //--Update the name 
                            inputfilename1=reads1.getFile();                            
                        }              
                    }
           //--Check for input 2
                    if (!inputfilename2.isEmpty()&&inputfilename2.endsWith(".bfq")) {
                        System.out.println("Ok for paired-reads: "+inputfilename2);               
                    } else if (!inputfilename2.isEmpty()){
                        //convert
//                        Maq maq=new Maq(properties);
//                        Genome reads2=maq.maq_convert(inputfilename2);
//                        if (reads2.getId()==0) {
//                            this.setStatus(status_error, "Unable to convert reads to MAQ format for "+inputfilename2);
//                        } else {
//                            inputfilename2=reads2.getGenomeFile();                            
//                        }              
                    }

            //-- CASE 1. Not paired file           
//           if (inputfilename2.isEmpty()) {
//              //maq match out.map ref.bfa reads.bfq
               com[index++]="match";
               com[index++]=inputfilename1+".map";
               com[index++]=referencefilename;
               com[index++]=inputfilename1;                
//           } else {
             //-- CASE 2. Paired files   
             //maq match out.map ref.bfa reads1.bfq reads2.bfq
//               com[index++]="match";
//               com[index++]=inputfilename1+".map";
//                com[index++]=referencefilename;
//               com[index++]=inputfilename1;        
               //com[index++]=inputfilename2;  
          // }         
           
           
           
//           com[index++]="-n 2";
//           com[index++]="-a";
//           com[index++]="-A";
//           com[index++]="-c";
//           com[index++]="-1";
//           com[index++]="-2";
//           com[index++]="-m";
//           com[index++]="-e";
//           com[index++]="-C";
//                   properties.get("map");
                      
       } else
       if (properties.getBoolean("mapcheck")) {
//          com[index++]="-s";
//          com[index++]="-q";
//          properties.get("mapcheck");
          
       } else
       if (properties.getBoolean("mapview")) {
                  com[index++]="mapview";
                 com[index++]=inputfilename1;
                 com[index++]=">mapview.log";       
       } else      
       if (properties.getBoolean("assemble")) {
       //maq assemble consensus.cns ref.bfa reads-1.map 2>assemble.log
              com[index++]="assemble";
              com[index++]=inputfilename1+".cns";
              com[index++]=referencefilename;
               com[index++]=inputfilename1;
               com[index++]="2>assemble.log";           
       } else      
            if (properties.getBoolean("cns2fq")) {
       //maq cns2fq consensus.cns >cns.fq
              com[index++]="cns2fq";
              com[index++]=inputfilename1;             
               com[index++]=">"+inputfilename1+".fastq";
               
       }  else
       if (properties.getBoolean("convert")) {
          //maq fasta2bfa ref.fasta ref.bfa
        setStatus(status_running, "Converting reference :"+inputfilename1+" to MAQ format ("+inputfilename1+".bfa)");
           com[index++]="fasta2bfa";
          com[index++]=referencefilename;          
          com[index++]=referencefilename+".bfa";
          //properties.get("mapview");          
       } else
        if (properties.getBoolean("convert_maq")) {
          //maq fastq2bfq reads.fastq reads.bfq
            setStatus(status_running, "Converting reads :"+inputfilename1+" to MAQ format ("+inputfilename1+".bfq)");
         com[index++]="fastq2bfq";
          com[index++]=inputfilename1;          
          com[index++]=inputfilename1+".bfq";
          //properties.get("mapview");          
       }
      // com[index++]="--print_site_lnl";
       return com;
    }
     
      public void post_parseOutput() {
          
          //--We converted a fasta to reference genome
          if (properties.getBoolean("convert")) {
              //--Save the reference as genome reference
              Genome genome=new Genome();
              workflow_properties prop=new workflow_properties();
              prop.put("inputname", referencefilename+".bfa");
              prop.put("type", 3);
               prop.put("Description", "MAQ reference genome");  
               genome.setGenomeFile(referencefilename+".bfa");    
               genome.setName(referencefilename+"(MAQ BFA)");
               genome.setName("MAQ reference genome (bfa) from "+referencefilename);
               genome.setText(prop.serializeToString());
               genome.setNote("Reference genome created "+Util.returnCurrentDateAndTime());
               genome.setRunProgram_id(id);      
               genome.saveToDatabase();
               properties.setOutput(genome);
               addOutput(genome);               
          } else if (properties.getBoolean("convert_maq")) {
              //--Save the reference as genome reference
              Genome genome=new Genome();
              workflow_properties prop=new workflow_properties();
              prop.put("inputname", inputfilename1+".bfq");
              prop.put("type", 3);
               prop.put("Description", "MAQ fastq file");  
               genome.setGenomeFile(inputfilename1+".bfq");
               genome.setName(inputfilename1+"(MAQ BFQ)");
               genome.setText(prop.serializeToString());
               genome.setNote("Reads conversion to MAQ (bfq) created "+Util.returnCurrentDateAndTime());
               genome.setRunProgram_id(id);      
               genome.saveToDatabase();
               properties.setOutput(genome);
               addOutput(genome);               
          } 
          else if (properties.getBoolean("map")){
               Genome genome=new Genome();
              workflow_properties prop=new workflow_properties();
              prop.put("inputname", inputfilename1+".map");
              prop.put("type", 3);
               prop.put("Description", "MAQ mapping file");  
               genome.setName("MAQ Mapping ("+inputfilename1+")");
               genome.setGenomeFile(inputfilename1+".map");
               genome.setText(prop.serializeToString());
               genome.setNote("Mapping of "+inputfilename1+" created "+Util.returnCurrentDateAndTime());
               genome.setRunProgram_id(id);      
               genome.saveToDatabase();
               properties.setOutput(genome);
               addOutput(genome);                            
          }   else if (properties.getBoolean("mapview")){
              Results text =new Results("mapview.log");
              text.setRunProgram_id(id);
              text.setName("Mapview of "+inputfilename1);
              text.setNote("Mapview of "+inputfilename1 + " created on "+Util.returnCurrentDateAndTime());
              text.saveToDatabase();
              properties.setOutput(text);
              addOutput(text);            
               
          } else if (properties.getBoolean("assemble")){
              Results text =new Results("assemble.log");
              text.setRunProgram_id(id);
              text.setName("Assembly of "+inputfilename1);
              text.setNote("Log of  "+inputfilename1 + " created on "+Util.returnCurrentDateAndTime());
              text.saveToDatabase();
              properties.setOutput(text);
              addOutput(text);         
                Genome genome=new Genome();
              workflow_properties prop=new workflow_properties();
              prop.put("inputname", inputfilename1+".cns");
              prop.put("type", 3);
               prop.put("Description", "Consensus mapping file");  
               genome.setName("MAQ Assembling ("+inputfilename1+")");
               genome.setGenomeFile(inputfilename1+".cns");
               genome.setText(prop.serializeToString());
               genome.setNote("Assembling of "+inputfilename1+" created "+Util.returnCurrentDateAndTime());
               genome.setRunProgram_id(id);      
               genome.saveToDatabase();
               properties.setOutput(genome);
               addOutput(genome);          
          } else if (properties.getBoolean("cns2fq")){
//              Results text =new Results("assemble.log");
//              text.setRunProgram_id(id);
//              text.setName("Assembly of "+inputfilename1);
//              text.setNote("Log of  "+inputfilename1 + " created on "+Util.returnCurrentDateAndTime());
//              text.saveToDatabase();
//              properties.setOutput(text);
//              addOutput(text);         
//                Genome genome=new Genome();
//              workflow_properties prop=new workflow_properties();
//              prop.put("inputname", inputfilename1+".cns");
//              prop.put("type", 3);
//               prop.put("Description", "Consensus mapping file");  
//               genome.setGenomeFile(inputfilename1+".cns");
//               genome.setText(prop.serializeToString());
//               genome.setNote("Consensus of "+inputfilename1+" created "+Util.returnCurrentDateAndTime());
//               genome.setRunProgram_id(id);      
//               genome.saveToDatabase();
//               properties.setOutput(genome);
//               addOutput(genome);          
             //  MultipleSequences multi=new MultipleSequences();
              // Note: on doit refaire la fonction pour prendre en compte la qualité qui va 
               // se retrouver ap`res une ligne contenant un +
               
               //--This is a realy big file... Don't load it 
                 Genome genome=new Genome();
              workflow_properties prop=new workflow_properties();
              prop.put("inputname", inputfilename1+".fastq");
              prop.put("type", 3);
               prop.put("Description", "Result");  
               genome.setGenomeFile(inputfilename1+".cns");
               genome.setName("MAQ Consensus ("+inputfilename1+")");
               genome.setText(prop.serializeToString());
               genome.setNote("Consensus of "+inputfilename1+" created "+Util.returnCurrentDateAndTime());
               genome.setRunProgram_id(id);      
               genome.saveToDatabase();
               properties.setOutput(genome);
               addOutput(genome);         
               
//              multi.readSequenceFromFasta(inputfilename1+  ".fastq");
//              multi.setRunProgram_id(id);
//              multi.setName("Sequences from "+inputfilename1);
//              multi.setNote("Loaded on "+Util.returnCurrentDateAndTime());
//              multi.saveToDatabase();
//              properties.setOutput(multi);
//              addOutput(multi);
            
          }
           //--Sequence in Fastq
             
//          Alignment align=new Alignment();
//             align.loadFromFile(outfile);
//             align.setName("Maq ("+Util.returnCurrentDateAndTime()+")");
//             align.setNote("Created on "+Util.returnCurrentDateAndTime());
//             
//        
//        Sequence s=align.getSequences().get(0);
//        s.saveToDatabase();
//         properties.put("output_sequence_id", s.getId());
//         df.addMultipleSequences(align);
//        align.saveToDatabase();
//             properties.put("output_alignment_id", align.getId());
//             addOutput(align);
//             
//             
//             Text lk=new Text(infile+"_maq_lk.txt");
//
//     
//       properties.put("output_results_id",text.getId());
         // }
      
    }

        //---Helper fonction to convert a fastq to bfq
      public Genome maq_convert(String filename) {
                  
        properties.load("Maq.properties", config.propertiesPath());
        properties.put("inputfilename", filename);        
        properties.put("convert_maq", true);
        
        init_createInput();        
        super.commandline=init_createCommandLine();

        try {            
            super.do_run();
            post_parseOutput();
            Genome g=new Genome(properties.getInt("output_genome_id"));            
            return g;
        } catch(Exception e) {e.printStackTrace();System.out.println("Error in converting fastq to bfq");}
        return new Genome();
    }
    
}
