/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package programs;

import biologic.FastqFile;
import biologic.GenomeFile;
import biologic.BamFile;
import biologic.Results;
import biologic.SamFile;
import configuration.Util;
import java.io.File;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Map;
import java.util.Iterator;
import program.RunProgram;
import static program.RunProgram.PortInputUP;
import static program.RunProgram.df;
import static program.RunProgram.status_error;
import workflows.workflow_properties;


/**
 *
 * @author Jérémy Goimard
 * @date Sept 2015
 *
 */
public class BwaMap extends RunProgram {
    
    private String file1Name     ="";
    private String file2Name     = "";
    private String fastqFile1    ="";
    private String fastqFile2    ="";
    private String bamFile1      ="";
    private String bamFile2      ="";
    private String genomeFile    ="";
    private String genomeFileName="";
    private String outputFile       ="";
    private String outputFileSE     ="";
    private String outputFilePE     ="";
    private static final String outPutPath = "."+File.separator+"results"+
            File.separator+"bwa"+
            File.separator+Util.returnTimeCode();
    
    private static final String[] alnANDbwaswTab = {
        "ALN_B_box",
        "ALN_E_box",
        "ALN_I_box",
        "ALN_M_box",
        "ALN_N_box",
        "ALN_O_box",
        "ALN_R_box",
        "ALN_b1_box",
        "ALN_b2_box",
        "ALN_b_box",
        "ALN_c_box",
        "ALN_d_box",
        "ALN_e_box",
        "ALN_i_box",
        "ALN_k_box",
        "ALN_l_box",
        "ALN_n_box",
        "ALN_o_box",
        "ALN_q_box",
        "ALN_t_box"
    };
    
    private static final String[] memTab = {
        "MEM_A_box",
        "MEM_B_box",
        "MEM_C_box",
        "MEM_E_box",
        "MEM_H_box",
        "MEM_L_box",
        "MEM_M_box",
        "MEM_O_box",
        "MEM_P_box",
        "MEM_R_box",
        "MEM_T_box",
        "MEM_U_box",
        "MEM_a_box",
        "MEM_c_box",
        "MEM_d_box",
        "MEM_k_box",
        "MEM_p_box",
        "MEM_r_box",
        "MEM_t_box",
        "MEM_v_box",
        "MEM_w_box"
    };
    
    private static final String[] sampeTab   = {
        "SAMPE_N_box",
        "SAMPE_P_box",
        "SAMPE_a_box",
        "SAMPE_n_box",
        "SAMPE_o_box",
        "SAMPE_r_box"
    };
    
    private static final String[] samseTab   = {
        "SAMSE_n_box",
        "SAMSE_r_box"
    };
    
    private static final String[] mapTab   = {
        "O_aln_button",
        "O_bwasw_button",
        "O_mem_button"
    };
    
    public BwaMap(workflow_properties properties) {
        this.properties=properties;
        execute();
    }
    
    @Override
    public boolean init_checkRequirements() {
        
        pgrmStartWithoutEdition();
        boolean bGen = checkGenomeRequirements();
        if (checkFastqRequirements()&&bGen)
            return true;
        else if (checkBamRequirements()&&bGen)
            return true;
        // In case program is started without edition
        return false;
    }
    
    // Sub functions for init_checkRequirements
    private void pgrmStartWithoutEdition () {
        // In case program is started without edition
        if (!properties.isSet("M_IDG_directory_button"))
            properties.put("M_IDG_workflow_button","true");
        
        boolean bMap = true;
        for (String sT:mapTab)
            if (properties.isSet(sT))
                bMap=false;
        if (bMap) properties.put("O_mem_button","true");
        if (!properties.isSet("ALN_PT_sampe_button")||
                !properties.isSet("ALN_PT_samse_button")
                )
            properties.put("ALN_PT_NOTUSED_button","true");
    }
    
    private boolean checkGenomeRequirements() {
        Vector<Integer>GenomeRef = properties.getInputID("GenomeFile",PortInputDOWN2);
        String s1 = Util.getFileName(GenomeFile.getVectorFilePath(GenomeRef));
        //Boolean Setting
        boolean b1 = properties.isSet("M_IDG_workflow_button");
        boolean b2 = properties.isSet("M_IDG_directory_button");
        boolean b3 = properties.get("IDG_selected_ComboBox").equals("Choose_an_indexed_Genome");
        
        if ((GenomeRef.isEmpty()||s1.equals("Unknown")||s1.equals("")) && b1) {
            setStatus(status_BadRequirements,"Need a Genome Reference");
            return false;
        } else if (b2 && b3) {
            setStatus(status_BadRequirements,"Choose a Genome Reference");
            return false;
        }
        return true;
    }
    
    private boolean checkFastqRequirements() {
        Vector<Integer>Fastq1    = properties.getInputID("FastqFile",PortInputUP);
        Vector<Integer>Fastq2    = properties.getInputID("FastqFile",PortInputDOWN);
        
        String s1 = Util.getFileName(FastqFile.getVectorFilePath(Fastq1));
        String s2 = Util.getFileName(FastqFile.getVectorFilePath(Fastq2));
        
        if (Fastq1.isEmpty()||s1.equals("Unknown")||s1.equals("")) {
            setStatus(status_BadRequirements,"No sequence found.");
            return false;
        } else if ((Fastq2.isEmpty()||s2.equals("Unknown")||s2.equals("")) &&
                properties.isSet("O_aln_button")
                ){
            properties.remove("ALN_PT_sampe_button");
            properties.put("ALN_PT_samse_button","true");
            setStatus(status_BadRequirements,"If bwa aln is choosen, the program will work with single end option.");
        }
        return true;
    }
    
    private boolean checkBamRequirements() {
        Vector<Integer>Bam1    = properties.getInputID("BamFile",PortInputUP);
        Vector<Integer>Bam2    = properties.getInputID("BamFile",PortInputDOWN);
        
        String s1 = Util.getFileName(BamFile.getVectorFilePath(Bam1));
        String s2 = Util.getFileName(BamFile.getVectorFilePath(Bam2));
                
        if (Bam2.isEmpty()||s1.equals("Unknown")||s1.equals("")) {
            setStatus(status_BadRequirements,"No sequence found.");
            return false;
        } else if ((Bam2.isEmpty()||s2.equals("Unknown")||s2.equals("")) &&
                properties.isSet("O_aln_button")){
            properties.remove("ALN_PT_sampe_button");
            properties.put("ALN_PT_samse_button","true");
            setStatus(status_BadRequirements,"If bwa aln is choosen, the program will work with single end option.");
        }
        if (!properties.isSet("ALN_b_box")&&!properties.isSet("ALN_b1_box")&&
                !properties.isSet("ALN_b2_box")&&properties.isSet("O_aln_button")){
            setStatus(status_BadRequirements,"If bwa aln is choosen and you use a BAM file, Please select a b option.");
            return false;
        }
        return true;
    }
    
    @Override
    public String[] init_createCommandLine() {
        
        // Inputs
        Vector<Integer>Fastq1    = properties.getInputID("FastqFile",PortInputUP);
        Vector<Integer>Fastq2    = properties.getInputID("FastqFile",PortInputDOWN);
        Vector<Integer>Bam1      = properties.getInputID("BamFile",PortInputUP);
        Vector<Integer>Bam2      = properties.getInputID("BamFile",PortInputDOWN);
        Vector<Integer>GenomeRef = properties.getInputID("GenomeFile",PortInputDOWN2);
        
        fastqFile1 = FastqFile.getVectorFilePath(Fastq1);
        if (!Fastq2.isEmpty()) fastqFile2 = FastqFile.getVectorFilePath(Fastq2);
        
        bamFile1 = BamFile.getVectorFilePath(Bam1);
        if (!Bam2.isEmpty()) bamFile2 = BamFile.getVectorFilePath(Bam2);
        
        // Genome File source
        if (!GenomeRef.isEmpty()){
            genomeFile = GenomeFile.getVectorFilePath(GenomeRef);
            genomeFile = genomeFile.replaceAll("(\\.fa|\\.fasta)$","");
        } else {
            String genomeChoosed = properties.get("IDG_selected_ComboBox");
            String genomePath    = properties.get("IDG_r_text");
            genomeFile = genomePath+File.separator+genomeChoosed+".fa";
        }
        
        // Get Name to create ouput
        file1Name        = Util.getFileName(fastqFile1);
        String file2Name = Util.getFileName(fastqFile2);
        genomeFileName   = Util.getFileName(genomeFile);
        if (!properties.isSet("O_aln_button")) {
            outputFile = outPutPath+File.separator+file1Name+"_"+genomeFileName+".sam";
        } else if  (properties.isSet("O_aln_button")){
            outputFileSE = outPutPath+File.separator+file1Name+"_"+genomeFileName+".sai";
            if (properties.isSet("ALN_PT_sampe_button"))
                outputFilePE = outPutPath+File.separator+file2Name+"_"+genomeFileName+".sai";
        }
        
        // Programme et options
        String options = "";
        String optionsSAMSE = "";
        String optionsSAMPE = "";
        if (properties.isSet("O_mem_button"))   options = "mem"+findOptions(memTab);
        if (properties.isSet("O_bwasw_button")) options = "bwasw"+findOptions(alnANDbwaswTab);
        if (properties.isSet("O_aln_button")) {
            options = "aln"+findOptions(alnANDbwaswTab);
            if (properties.isSet("ALN_PT_sampe_button"))
                optionsSAMSE = "samse"+findOptions(sampeTab);
            if (properties.isSet("ALN_PT_samse_button"))
                optionsSAMPE = "sampe"+findOptions(samseTab);
        }
        
        String[] com = new String[30];
        for (int i=0; i<com.length;i++) com[i]="";
        
        // Faire les com en fonction des inputs
        
        com[0]="cmd.exe";
        com[1]="/C";
        com[2]=properties.getExecutable();
        com[3]=options;
        com[4]="\""+genomeFile+"\"";
        com[5]="\""+fastqFile1+"\"";
        if (properties.isSet("O_mem_button")||properties.isSet("O_bwasw_button")) {
            if (!fastqFile2.equals("")) {
                com[6]="\""+fastqFile2+"\"";
            }
            com[7]="> "+outputFile+"";
        }
        if (properties.isSet("O_aln_button")) {
            com[6]="> "+outputFileSE+"";
            com[7] ="&&";
            com[8] ="cmd.exe";
            com[9] ="/C";
            com[10]=properties.getExecutable();
            if (properties.isSet("ALN_PT_samse_button")){
                com[11]=optionsSAMSE;
                com[12]="\""+genomeFile+"\"";
                com[13]="\""+outputFileSE+"\"";
                com[14]="\""+fastqFile1+"\"";
                com[15]="> "+outputFile+"";
            }
            if (properties.isSet("ALN_PT_sampe_button")){
                com[11]=options;
                com[12]="\""+genomeFile+"\"";
                com[14]="\""+fastqFile2+"\"";
                com[15]="> "+outputFilePE+"";
                com[16] ="&&";
                com[17] ="cmd.exe";
                com[18] ="/C";
                com[19]=properties.getExecutable();
                com[20]=optionsSAMPE;
                com[21]="\""+genomeFile+"\"";
                com[22]="\""+outputFileSE+"\"";
                com[23]="\""+outputFilePE+"\"";
                com[24]="\""+fastqFile1+"\"";
                com[25]="\""+fastqFile2+"\"";
                com[26]="> "+outputFile+"";
            }
        }
        return com;
    }
    
    /*
    * Function to find options choosed
    * Use the propertie name to create the command
    */
    private String findOptions(String[] tab) {
        String s = "";
        for (String op:tab) {
            if (properties.isSet(op)) {
                String t = op;
                t = t.replaceAll("_[a-z]*$","");
                t = t.replaceAll("([A-Z]*_)*","");
                t = t.replaceAll("([A-Z])","-$1");
                // Extract the command line operator
                if (t.length()>1) {
                    t = t.toLowerCase();
                    t = t.replaceAll("([a-z]+)([0-9])([a-z]+)","$1-$2$3");
                    t = " --"+t;
                } else {
                    t = " -"+t;
                }
                // Add the value if needed
                if (op.contains("_value") || op.contains("_text")) {
                    t = t+" " + properties.get(op);
                }
                s = s+" "+t;
            }
        }
        return s;
    }
    
    /*
    * Output Parsing
    */
    @Override
    public void post_parseOutput() {
        SamFile.saveFile(properties,outputFile,"Bwa_map","SamFile");
        
        String s = this.getPgrmOutput();
        if (!outputFileSE.equals("")) s+="\nintermediate files :"+outputFileSE+"\n";
        if (!outputFilePE.equals("")) s+="\nintermediate files :"+outputFilePE+"\n";
        Results.saveResultsPgrmOutput(properties,s,"Bwa_map");
    }
    
}
