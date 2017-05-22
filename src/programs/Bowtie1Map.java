/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package programs;

import biologic.FastqFile;
import biologic.FileFile;
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
import editors.Bowtie1MapEditors;


/**
 *
 * @author Jérémy Goimard
 * @date Aout 2015
 *
 */
public class Bowtie1Map extends RunProgram {
    
    private String fastqFile1    ="";
    private String fastqFile1Name="";
    private String fastqFile2    ="";
    private String fastqFile2Name="";
    private String genomeFile    ="";
    private String genomeFileName="";
    private String outputFile    ="";
    private static final String outPutPath = "."+File.separator+"results"+File.separator+"bowtie";
    
    //private String[] optionsTab  = {"bowtie2IndexGenome_button","bowtie2Inspect_button","bowtie2Mapping_button"};
    private static final String[] pairedEndTab = {
        "M_PE_I_value",
        "M_PE_X_value",
        "M_PE_ff_button",
        "M_PE_fr_button",
        "M_PE_pairtries_box",
        //"M_PE_pairtries_value",
        "M_PE_rf_button",
        "M_PE_tryhard_box"
    };
    
    private static final String[] optionsOutputTab = {
        "O_alPATH_box",
        "O_fullref_box",
        "O_offbase_box",
        //"O_offbase_value",
        "O_quiet_box",
        "O_refidx_box",
        "O_suppress_box",
        //"O_suppress_value",
        "O_time_box",
        "O_unPATH_box"
    };
    
    private static final String[] optionsSam = {
        "O_SAM_mapq_box",
        //"O_SAM_mapq_value",
        "O_SAM_samNosq_box",
        "O_SAM_samNohead_box",
        "O_SAM_samRG_box",
        //"O_SAM_samRG_text"
    };
    
    private static final String[] customMapTab   = {
	"CM_A_chunkmbs_box",
	//"CM_A_chunkmbs_value",
	"CM_A_maqerr_box",
	//"CM_A_maqerr_value",
	"CM_A_maxbts_box",
	"CM_A_nofw_box",
	"CM_A_nomaqround_box",
	"CM_A_norc_box",
	"CM_A_seedlen_box",
	//"CM_A_seedlen_value",
	"CM_A_seedmms_box",
	//"CM_A_seedmms_value",
	"CM_A_v_box",
	//"CM_A_v_value",
	"CM_C_colCqual_box",
	"CM_C_colCseq_box",
	"CM_C_colKeepends_box",
	"CM_C_snpfrac_box",
	//"CM_C_snpfrac_value",
	"CM_C_snpphred_box",
	//"CM_C_snpphred_value",
	"CM_I_3_value",
	"CM_I_5_value",
	//"CM_I_c_box", // Not Set
	//"CM_I_f_box", // Not Set
	"CM_I_intQuals_box",
	"CM_I_phred33Quals_box",
	"CM_I_phred64Quals_box",
	//"CM_I_q_box", // Not Set
	//"CM_I_qseq_box", // Not Set
	//"CM_I_r_box", // Not Set
	"CM_I_s_value",
	"CM_I_solexa1DOT3Quals_box",
	"CM_I_solexaQuals_box",
	"CM_I_u_value",
	"CM_OO_seed_value",
	"CM_P_mm_box",
	"CM_P_o_text",
	"CM_P_p_box",
	//"CM_P_p_value",
	"CM_P_shmem_box",
	"CM_R_MMAJ_box",
	//"CM_R_MMAJ_value",
	"CM_R_all_box",
	"CM_R_best_box",
	"CM_R_k_box",
	//"CM_R_k_value",
	"CM_R_m_box",
	//"CM_R_m_value",
	"CM_R_maxPATH_box",
	"CM_R_strata_box"
    };
    
    public Bowtie1Map(workflow_properties properties) {
        this.properties=properties;
        execute();
    }
    
    @Override
    public boolean init_checkRequirements() {
        Vector<Integer>Fastq1    = properties.getInputID("FastqFile",PortInputUP);
        fastqFile1     = FastqFile.getVectorFilePath(Fastq1);
        fastqFile1Name = Util.getFileName(fastqFile1);
        
        Vector<Integer>Fastq2    = properties.getInputID("FastqFile",PortInputDOWN);
        if (!Fastq2.isEmpty()) fastqFile2 = FastqFile.getVectorFilePath(Fastq2);
        fastqFile2Name = Util.getFileName(fastqFile2);
        
        Vector<Integer>GenomeRef = properties.getInputID("GenomeFile",PortInputDOWN2);
        if (!GenomeRef.isEmpty()){
            genomeFile = GenomeFile.getVectorFilePath(GenomeRef);
            genomeFile = genomeFile.replaceAll("\\.\\d\\.ebwtl?$","");
            genomeFile = genomeFile.replaceAll("\\.rev$","");
        }
        // In case program is started without edition
        pgrmStartWithoutEdition(Fastq2);
        
        // Boolean setting
        boolean b1 = properties.isSet("M_IDG_workflow_button");
        boolean b2 = properties.isSet("M_IDG_directory_button");
        boolean b3 = properties.get("IDG_selected_ComboBox").equals("Choose_an_indexed_Genome");
        boolean b4 = properties.get("M_PE_button").equals("true"); // Stupid ?
            
        if (Fastq1.size()==0||fastqFile1Name.equals("Unknown")||fastqFile1Name.equals("")) {
            setStatus(status_BadRequirements,"No sequence found.");
            return false;
        } else if ((Fastq2.isEmpty()||fastqFile2Name.equals("Unknown")||fastqFile2Name.equals("")) &&
                properties.isSet("M_PE_button")) {
            properties.remove("M_PE_button");
            properties.put("M_SE_button","true");
            setStatus(status_BadRequirements,"The program will work on single end.");
            return true;
        } else if (GenomeRef.isEmpty() && b1) {
            setStatus(status_BadRequirements,"Need a Genome Reference");
            return false;
        } else if ( b2 && b3) {
            setStatus(status_BadRequirements,"Choose a Genome Reference");
            return false;
        } else if (!Fastq2.isEmpty() && b4) {
            if (!fastqFile1Name.contains("<>") && !fastqFile2Name.contains("<>")) {
                if (fastqGoodNumber(fastqFile1Name,fastqFile2Name) && fastqSameName(fastqFile1Name,fastqFile2Name)) {
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
            if (!properties.isSet("M_CM_button")||!properties.isSet("M_default_button")) properties.put("M_default_button","true");
            if (!properties.isSet("M_PE_button")) properties.put("M_SE_button","true");
            if (!Fastq2.isEmpty() && properties.isSet("M_SE_button")){
                properties.put("M_PE_button","true");
                properties.remove("M_SE_button");
            }
        }
        
        private boolean fastqSameName (String s1,String s2) {
            boolean b = false;
            s1 = s1.replaceAll("_\\d$","");
            s2 = s2.replaceAll("_\\d$","");
            if (s2.equals(s1)) b=true;
            return b;
        }

        private boolean fastqGoodNumber (String s1,String s2) {
            boolean b = false;
            int val1 = Integer.parseInt(s1.replaceAll(".*_(\\d)$","$1"));
            int val2 = Integer.parseInt(s2.replaceAll(".*_(\\d)$","$1"));
            if (val1==1 && val2==2) b=true;
            return b;
        }

    @Override
    public String[] init_createCommandLine() {
        
        // Genome File source
        if (genomeFile.equals("")){
            String genomeChoosed = properties.get("IDG_selected_ComboBox");
            genomeChoosed        = genomeChoosed.replaceAll("\\_long$","");
            String genomePath    = properties.get("IDG_r_text");
            genomeFile = genomePath+File.separator+genomeChoosed;
        }
        
        genomeFileName = Util.getFileName(genomeFile);
        outputFile = outPutPath+File.separator+fastqFile1Name+"_"+genomeFileName;
        if (properties.isSet("O_SAM_sam_box"))
            outputFile = outputFile+".sam";
        
        // Programme et options
        String options = optionsChoosed(properties.get("Options"));
        
        String[] com = new String[30];
        for (int i=0; i<com.length;i++) com[i]="";
        
        com[0]="cmd.exe";
        com[1]="/C";
        com[2]=properties.getExecutable();
        com[3]=options;
        if (!genomeFile.equals("")) com[4]="-x \""+genomeFile+"\"";
        if (!fastqFile1.equals("") && fastqFile2.equals("")) {
            com[5]="\""+fastqFile1+"\"";
        }
        if (!fastqFile1.equals("") && !fastqFile2.equals("")) {
            com[5]="-1 \""+fastqFile1+"\"";
            com[6]="-2 \""+fastqFile2+"\"";
        }
        if (!outputFile.equals("")) {
            String samOp = "";
            if (properties.isSet("O_SAM_sam_box")) samOp = "-S ";
            else samOp = ">";
            com[7]= samOp+outputFile+"";
        }
        
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
            
            if (properties.isSet("O_SAM_sam_box"))
                outputOp += findOptions(optionsSam);
            
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
                    String t = op;
                    boolean path = false;
                    // Extract the command line operator
                    if (op.equals("CM_R_maxPATH_box")){
                        t = "--max "+outPutPath+File.separator+fastqFile1Name+"_"+genomeFileName+"_maxFile.fq";
                    } else {
                        t = t.replaceAll("_[a-z]*$","_");
                        t = t.replaceAll("(.*_(.*)_$","$1");
                        if (t.length()>1) {
                            t = t.replaceFirst("([A-Z])","-$1");
                            if (t.contains("DOT")) t = t.replaceAll("DOT",".");
                            if (!t.matches(".*-[A-Z]{2}")) t = t.toLowerCase();
                            if (t.matches("[a-z]+[0-9][a-z]+"))
                                t = t.replaceAll("([a-z]+)([0-9])([a-z]+)","$1-$2$3");
                            t = " --"+t;
                        } else {
                            t = " -"+t;
                        }
                        // Add the value if needed
                        if (!properties.get(op).matches("[true]?[false]")) {
                            t = t+" " + properties.get(op);
                        }
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
        if (properties.isSet("O_SAM_sam_box"))
            SamFile.saveFile(properties,outputFile,"Bowtie1_map","SamFile");
        else
            FileFile.saveFile(properties,outputFile,"Bowtie1_map","FileFile");
        Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"Bowtie1_map");
    }
    
}
