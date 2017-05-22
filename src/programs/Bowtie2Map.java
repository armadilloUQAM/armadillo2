/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package programs;

import biologic.FastqFile;
import biologic.GenomeFile;
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
 * @date Aout 2015
 *
 */
public class Bowtie2Map extends RunProgram {
    
    private String fastqFile1    ="";
    private String fastqFile1Name="";
    private String fastqFile2    ="";
    private String genomeFile    ="";
    private String genomeFileName="";
    private String outputFile    ="";
    private static final String outputPath = "."+File.separator+"results"+File.separator+"bowtie2";
    
    //private String[] optionsTab  = {"bowtie2IndexGenome_button","bowtie2Inspect_button","bowtie2Mapping_button"};
    private static final String[] pairedEndTab = {
        "M_PE_I_value",
        "M_PE_X_value",
        "M_PE_dovetail_box",
        "M_PE_ff_button",
        "M_PE_fr_button",
        "M_PE_noContain_box",
        "M_PE_noDiscordant_box",
        "M_PE_noMixed_box",
        "M_PE_noOverlap_box",
        "M_PE_rf_button"
    };
    
    private static final String[] optionsOutputTab = {
        "O_alANDalConc_box",
        "O_quiet_box",
        "O_unANDunConc_box"
    };
    
    private static final String[] customMapTab   = {
        "CM_A_L_value",
        "CM_A_N_value",
        "CM_A_dpad_value",
        "CM_A_endToEnd_text",
        "CM_A_gbar_value",
        "CM_A_i_text",
        "CM_A_ignoreQuals_box",
        "CM_A_local_button",
        "CM_A_local_text",
        "CM_A_nCeil_text",
        "CM_A_no1mmUpfront_box",
        "CM_A_nofw_box",
        "CM_A_norc_box",
        "CM_E_D_value",
        "CM_E_R_value",
        "CM_I_3_value",
        "CM_I_5_value",
        "CM_I_c_box",
        "CM_I_f_box",
        "CM_I_intQuals_box",
        "CM_I_phred33_box",
        "CM_I_phred64_box",
        "CM_I_q_box",
        "CM_I_qseq_box",
        "CM_I_r_box",
        "CM_I_s_value",
        "CM_I_solexaQuals_box",
        "CM_I_u_value",
        "CM_OO_nonDeterminist_box",
        "CM_OO_qcFilter_box",
        "CM_OO_seed_value",
        "CM_P_mm_box",
        "CM_P_o_text",
        "CM_P_p_value",
        "CM_P_reorder_box",
        "CM_R_a_button",
        "CM_R_k_value",
        "CM_R_noset_button",
        "CM_SAM_noHd_box",
        "CM_SAM_noSq_box",
        "CM_SAM_noUnal_box",
        "CM_SAM_omitSecSeq_box",
        "CM_SAM_rgId_text",
        "CM_SAM_rg_text",
        "CM_S_ma_value",
        "CM_S_mp_value",
        "CM_S_np_value",
        "CM_S_rdg1_value",
        "CM_S_rdg2_value",
        "CM_S_rfg1_value",
        "CM_S_rfg2_value"
    };
    
    private static final Hashtable<String,String> optionsHash = new Hashtable<String,String>() {{
        put("M_ETE_F_button","--fast");
        put("M_ETE_S_button","--sensitive");
        put("M_ETE_VF_button","--very-fast");
        put("M_ETE_VS_button","--very-sensitive");
        put("M_L_F_button","--fast-local");
        put("M_L_S_button","--sensitive-local");
        put("M_L_VF_button","--very-fast-local");
        put("M_L_VS_button","--very-sensitive-local");
        put("M_default_button","");
        put("M_CM_button","");
    }};
    
    public Bowtie2Map(workflow_properties properties) {
        this.properties=properties;
        execute();
    }
    
    @Override
    public boolean init_checkRequirements() {
        Vector<Integer>Fastq1    = properties.getInputID("FastqFile",PortInputUP);
        Vector<Integer>Fastq2    = properties.getInputID("FastqFile",PortInputDOWN);
        Vector<Integer>GenomeRef = properties.getInputID("GenomeFile",PortInputDOWN2);
        String s1 = Util.getFileName(FastqFile.getVectorFilePath(Fastq1));
        String s2 = Util.getFileName(FastqFile.getVectorFilePath(Fastq2));
        
        // In case program is started without edition
        pgrmStartWithoutEdition(Fastq2);
        
        //Boolean Setting
        boolean b1 = properties.isSet("M_IDG_workflow_button");
        boolean b2 = properties.isSet("M_IDG_directory_button");
        boolean b3 = properties.get("IDG_selected_ComboBox").equals("Choose_an_indexed_Genome");
        boolean b4 = properties.get("M_PE_button").equals("true"); // Stupid ?
        
        if (Fastq1.isEmpty()||s1.equals("Unknown")||s1.equals("")) {
            setStatus(status_BadRequirements,"No sequence found.");
            return false;
        } else if ((Fastq2.isEmpty()||s2.equals("Unknown")||s2.equals("")) &&
                properties.isSet("M_PE_button")) {
            properties.remove("M_PE_button");
            properties.put("M_SE_button","true");
            setStatus(status_BadRequirements,"The program will work with single end option.");
        } else if ( GenomeRef.isEmpty() && b1) {
            setStatus(status_BadRequirements,"Need a Genome Reference");
            return false;
        } else if ( b2 && b3) {
            setStatus(status_BadRequirements,"Choose a Genome Reference");
            return false;
        } else if (!Fastq2.isEmpty() && b4) {
            if (!s1.contains("<>") && !s2.contains("<>")) {
                int gn = FastqFile.goodFastqFileNumber(s1,s2);
                int sn = FastqFile.sameFastqFileName(s1,s2);
                if (sn==0 && gn==0) {
                    setStatus(status_BadRequirements,"It looks that Fastq paired are not compatible.\n"
                            + "Check your files they need to have the same name and finish by _1 and _2,\n"
                            + "or it's due to Armadillo process, and let it go !");
                    return false;
                }
            } else {
                // Do something if all in one shot.
                // At least contain the same number of files inf fastq1 an fastq2
            } 
        }
        return true;
        
    
    }
        // Sub functions for init_checkRequirements
        private void pgrmStartWithoutEdition (Vector<Integer> Fastq2) {
            // In case program is started without edition
            if (!properties.isSet("M_IDG_directory_button")) properties.put("M_IDG_workflow_button","true");
            if (!properties.isSet("Options")) properties.put("Options","M_default_button");
            if (!properties.isSet("M_PE_button")) properties.put("M_SE_button","true");
            if (!Fastq2.isEmpty() && properties.isSet("M_SE_button")){
                properties.put("M_PE_button","true");
                properties.remove("M_SE_button");
            }
        }
        
    @Override
    public String[] init_createCommandLine() {
        
        // Inputs
        Vector<Integer>Fastq1    = properties.getInputID("FastqFile",PortInputUP);
        Vector<Integer>Fastq2    = properties.getInputID("FastqFile",PortInputDOWN);
        Vector<Integer>GenomeRef = properties.getInputID("GenomeFile",PortInputDOWN2);
        
        fastqFile1 = FastqFile.getVectorFilePath(Fastq1);
        if (!Fastq2.isEmpty()) fastqFile2 = FastqFile.getVectorFilePath(Fastq2);
        
        // Genome File source
        if (!GenomeRef.isEmpty()){
            genomeFile = GenomeFile.getVectorFilePath(GenomeRef);
            genomeFile = genomeFile.replaceAll("\\.\\d\\.bt2l?$","");
            genomeFile = genomeFile.replaceAll("\\.rev$","");
        } else {
            String genomeChoosed = properties.get("IDG_selected_ComboBox");
            genomeChoosed = genomeChoosed.replaceAll("\\_long$","");
            String genomePath    = properties.get("IDG_r_text");
            genomeFile = genomePath+File.separator+genomeChoosed;
        }
        
        // Get Name to create ouput
        fastqFile1Name = Util.getFileName(fastqFile1);
        genomeFileName = Util.getFileName(genomeFile);
        outputFile = outputPath+File.separator+fastqFile1Name+"_"+genomeFileName+".sam";
        
        // Programme et options
        String preset  = "";
        String opH     = optionsHash.get(properties.get("Options"));
        if (!opH.equals("")) preset = ""+opH+" ";
        String options = optionsChoosed(properties.get("Options"));
        
        String[] com = new String[30];
        for (int i=0; i<com.length;i++) com[i]="";
        
        com[0]="cmd.exe";
        com[1]="/C";
        com[2]=properties.getExecutable();
        com[3]=preset;
        com[4]=options;
        if (!genomeFile.equals("")) com[5]="-x \""+genomeFile+"\"";
        if (!fastqFile1.equals("") && fastqFile2.equals("")) {
            com[6]="\""+fastqFile1+"\"";
        }
        if (!fastqFile1.equals("") && !fastqFile2.equals("")) {
            com[6]="-1 \""+fastqFile1+"\"";
            com[7]="-2 \""+fastqFile2+"\"";
        }
        if (!outputFile.equals(""))    com[8]="-S "+outputFile+"";
        
        return com;
    }
    
        /*
        * Function to return options choosed
        */
        private String optionsChoosed(String optionsChoosed){
            String t  = optionsChoosed;

            // Custom made options
            String s  = "";
            if (t.equals("M_CM_button")) s = findOptions(customMapTab);

            // Paired end
            String pe = "";
            if (properties.isSet("M_PE_button")) pe = findOptions(pairedEndTab);
            
            String outputOp = findOptions(optionsOutputTab);
            
            s = outputOp+" "+pe+" "+s;

            return s;
        }
    
        /*
        * Function to find options choosed
        * Use the propertie name to create the command
        */
        private String findOptions(String[] tab) {
            String s = "";
            for (String op:tab) {
                if (properties.isSet(op)) {
                    String t = "";
                    if (op.contains("AND")){
                        t = t.replaceAll("_[a-z]*$","");
                        t = t.replaceAll("([A-Z]*_)*","");
                        String[] stab = t.split("AND");
                        String sa = "";
                        for (String st:stab) {
                            st = st.replaceAll("([A-Z])","-$1");
                            st = st.toLowerCase();
                            sa += " --"+st+" "+outputPath;
                        }
                        t = sa;
                    } else {
                        t = t.replaceAll("_[a-z]*$","_");
                        t = t.replaceAll(".*_(.*)_$","$1");
                        // Extract the command line operator
                        if (t.length()>1) {
                            t = t.replaceAll("([A-Z])","-$1");
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
                    }
                    s = s+" "+t;
                }
            }
            return s;
        }
        
//        private String findOutputOptions(String[] tab) {
//            String s = "";
//            for (String op:tab) {
//                if (properties.isSet(op)){
//                    String t = "";
//                    if (op.contains("AND")){
//                        t = op;
//                        t = t.replaceAll("_[a-z]*$","");
//                        t = t.replaceAll("([A-Z]*_)*","");
//                        String[] stab = t.split("AND");
//                        String sa = "";
//                        for (String st:stab) {
//                            st = st.replaceAll("([A-Z])","-$1");
//                            st = st.toLowerCase();
//                            sa += " --"+st+" "+outputPath;
//                        }
//                        t = sa;
//                    } else {
//                        t = op;
//                        t = t.replaceAll("_[a-z]*$","");
//                        t = t.replaceAll("([A-Z]*_)*","");
//                        t = " --"+t;
//                    }
//                    s = s+" "+t;
//                } 
//            }
//            return s;
//        }

    /*
    * Output Parsing
    */
    @Override
    public void post_parseOutput() {
        SamFile.saveFile(properties,outputFile,"Bowtie2_map","SamFile");
        Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"Bowtie2_map");
    }
    
}
