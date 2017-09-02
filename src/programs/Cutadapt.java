/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package programs;

import biologic.FastqFile;
import biologic.FastaFile;
import biologic.TextFile;
import biologic.Results;
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
public class Cutadapt extends RunProgram {
    
    private String file1Name ="";
    private String fileRead1 ="";
    private String options   = "";
    // A FAIRE CREER LE RÉPERTOIRE AVANT UTILISATION !
    private static final String outPutPath = "."+File.separator+"results"+
            File.separator+"cutadapt"+
            File.separator+Util.returnTimeCode();
    private String outputFile                  = "";
    private String outputFileTSO = outPutPath+File.separator+"outputFileTooShortOutput";
    private String outputFileTLO = outPutPath+File.separator+"outputFileTooLongOutput";
    private String outputFileUO  = outPutPath+File.separator+"outputFileUntrimmedOutput";
    
    private static final String[] boxTab = {
        "BO_ANCH_a_box",
        "BO_ANCH_g_box",
        "BO_N_box",
        "BO_a_box",
        "BO_anywhere_box",
        "BO_e_box",
        "BO_g_box",
        "BO_matchReadWildcards_box",
        "BO_noIndels_box",
        "BO_overlap_box",
        "BO_times_box",
        "F_discardUntrimmed_box",
        "F_discard_box",
        "F_maskAdapter_box",
        "F_maxN_box",
        "F_maximumLength_box",
        "F_minimumLength_box",
        "F_noTrim_box",
        "O_infoFile_box",
        "O_name_box",
        "O_quiet_box",
        "O_restFile_box",
        "O_tooLongOutput_box",
        "O_tooShortOutput_box",
        "O_untrimmedOutput_box",
        "O_wildcardFile_box",
        "RM_C_bwa_box",
        "RM_C_doubleEncode_box",
        "RM_C_stripF3_box",
        "RM_C_trimPrimer_box",
        "RM_colorspace_box",
        "RM_cut_box",
        "RM_lengthTag_box",
        "RM_noZeroCap_box",
        "RM_prefix_box",
        "RM_qualityBase33_box",
        "RM_qualityBase64_box",
        "RM_stripSuffix_box",
        "RM_suffix_box",
        "RM_trimN_box",
        "RM_trimQualities_box",
        "RM_zeroCap_box"
    };
    
    public Cutadapt(workflow_properties properties) {
        this.properties=properties;
        execute();
    }
    
    @Override
    public boolean init_checkRequirements() {
        pgrmStartWithoutEdition();
        if (checkFastqRequirements())
            return true;
        else if (checkFastaRequirements())
            return true;
        // In case program is started without edition
        return false;
    }
    
    // Sub functions for init_checkRequirements
    private void pgrmStartWithoutEdition () {
        // In case program is started without edition
    }
    
    private boolean checkFastaRequirements() {
        Vector<Integer>Fasta1    = properties.getInputID("FastaFile",PortInputDOWN);
        String s1 = Util.getFileName(FastaFile.getVectorFilePath(Fasta1));
        if (Fasta1.isEmpty()||s1.equals("Unknown")||s1.equals("")) {
            setStatus(status_BadRequirements,"No sequence 1 found.");
            return false;
        }
        return true;
    }
    
    private boolean checkFastqRequirements() {
        Vector<Integer>Fastq1    = properties.getInputID("FastqFile",PortInputDOWN);
        String s1 = Util.getFileName(FastqFile.getVectorFilePath(Fastq1));
        if (Fastq1.isEmpty()||s1.equals("Unknown")||s1.equals("")) {
            setStatus(status_BadRequirements,"No sequence 1 found.");
            return false;
        }
        return true;
    }
    
    @Override
    public String[] init_createCommandLine() {
        // TO IMPROVE REMOVE THE DOUBLE INPUTS VECTOR REQ AND HERE
        // Inputs
        Vector<Integer>Fastq1 = properties.getInputID("FastqFile",PortInputDOWN);
        Vector<Integer>Fasta1 = properties.getInputID("FastaFile",PortInputDOWN);
        
        if (!Fastq1.isEmpty()){
            fileRead1  = FastqFile.getVectorFilePath(Fastq1);
            outputFile = outPutPath+File.separator+Util.getFileName(fileRead1)+".fastq";
            outputFileTSO += ".fastq";
            outputFileTLO += ".fastq";
            outputFileUO  += ".fastq";
        } else {
            fileRead1 = FastaFile.getVectorFilePath(Fasta1);
            outputFile = outPutPath+File.separator+Util.getFileName(fileRead1)+".fasta";
            outputFileTSO += ".fasta";
            outputFileTLO += ".fasta";
            outputFileUO  += ".fasta";
        }
        Util.CreateDir(outPutPath);
        
        options = findOptions(boxTab);
        // Programme et options
        String[] com = new String[30];
        for (int i=0; i<com.length;i++) com[i]="";
        
        // Faire les com en fonction des inputs
        com[0]="cmd.exe";
        com[1]="/C";
        com[2]=properties.getExecutable();
        com[3]=options;
        com[4]="-o "+outputFile+"";
        com[5]=fileRead1;
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
                t = t.replaceAll("_[a-z]*$","_");
                t = t.replaceAll(".*_(.*)_$","$1");
                // Extract the command line operator
                if (t.length()>1) {
                    t = t.replaceAll("([A-Z])","-$1");
                    t = t.toLowerCase();
                    if (t.matches("quality-base64"))        t= "--quality-base=64";
                    else if (t.matches("quality-base33"))   t= "--quality-base=33";
                    else if (t.matches("too-long-output"))  t= "--too-long-output="+outputFileTSO;
                    else if (t.matches("too-short-output")) t= "--too-short-output="+outputFileTLO;
                    else if (t.matches("untrimmed-output")) t= "--untrimmed-output="+outputFileUO;
                    else if (t.equals("info-file"))         t = "--info-file="+outputFile+"_infoFile.txt";
                    else if (s.equals("rest-file"))         t = "--rest-file="+outputFile+"_restFile.txt";
                    else if (s.equals("wildcard-file"))     t = "--wildcard-file="+outputFile+"_wildcardFile.txt";
                    else if (s.equals("discard"))           t = "--discard="+outputFile+"_discardFile.txt";
                    else t = " --"+t;
                } else {
                    t = " -"+t;
                }
                // Add the value if needed
                if (!properties.get(op).equals("true")) {
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
        if (outputFile.matches(".*\\.fastq$")){
            FastqFile.saveFile(properties,outputFile,"CutAdapt","FastqFile");
        } else if (outputFile.matches(".*\\.fasta$")) {
            FastaFile.saveFile(properties,outputFile,"CutAdapt","FastaFile");
        } else if (outputFile.matches(".*\\.txt$")) {
            TextFile.saveFile(properties,outputFile,"CutAdapt","TextFile");
        }
        
        Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"CutAdapt");
    }
    
}
