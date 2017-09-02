/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
* Author : J-G
* Date   : 11-02-2017
*/

package programs;

import biologic.FastaFile;
import biologic.CatFile;
import biologic.alertFile;
import biologic.tblFile;
import biologic.alignFile;
import biologic.EmblFile;
import biologic.TextFile;
import biologic.maskedFile;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author J-G
 * @date 11-02-2017
 *
 */
public class RepeatMasker extends RunProgram {
    // CREATE VARIABLES HERE
    //INPUTS
    private String input1       = "";
    private String inputPath1   = "";
    private String input2       = "";
    private String inputPath2   = "";
    private String input3       = "";
    private String inputPath3   = "";
    //OUTPUTS
    private String output1       = "";
    private String output2       = "";
    private String output3       = "";
    private String output4       = "";
    private String output5       = "";
    private String output6       = "";
    private String output7       = "";
    private String output8       = "";
    private String output9       = "";
    //PATHS
    private static final String outputPath = "."+File.separator+"results"+File.separator+"RepeatMasker"+File.separator+"";
    private static final String inputPath  = outputPath+File.separator+"INPUTS";

    private static final String[] Options_speed_and_search = {
        "AO_OSAS_engine_box",
        //"AO_OSAS_engine_JComboBoxValue",
        "AO_OSAS_parallel_box",
        //"AO_OSAS_parallel_JSpinnerValue",
        "AO_OSAS_s_box",
        //"AO_OSAS_s_JSpinnerValue",
        "AO_OSAS_q_box",
        //"AO_OSAS_q_JSpinnerValue",
        "AO_OSAS_qq_box"//,
        //"AO_OSAS_qq_JSpinnerValue"
    };

    private static final String[] Species_options = {
        "AO_SO_lib_box",
        "AO_SO_species_box"//,
        //"AO_SO_species_JComboBoxValue"
    };

    private static final String[] Masking_options = {
        "AO_MO_cutoff_box",
        //"AO_MO_cutoff_JSpinnerValue",
        "AO_MO_nolow_box",
        "AO_MO_low_box",
        "AO_MO_noint_box",
        "AO_MO_int_box",
        "AO_MO_norna_box",
        "AO_MO_alu_box",
        "AO_MO_div_box"//,
        //"AO_MO_div_JSpinnerValue"
    };

    private static final String[] Contamination_options = {
        "AO_CO_is_Only_box",
        "AO_CO_is_Clip_box",
        "AO_CO_no_Is_box"
    };

    private static final String[] Running_options = {
        "AO_RO_gc_box",
        //"AO_RO_gc_JSpinnerValue",
        "AO_RO_gccalc_box",
        "AO_RO_frag_box",
        //"AO_RO_frag_JSpinnerValue",
        "AO_RO_nocut_box",
        "AO_RO_noisy_box",
        "AO_RO_nopost_box"
    };


    public RepeatMasker(workflow_properties properties){
        this.properties=properties;
        execute();
    }

    @Override
    public boolean init_checkRequirements(){

        // TEST INPUT VARIABLES HERE
        // ports are 3-PortInputUp, 2-PortInputDOWN, 4-PortInputDOWN2

        Vector<Integer>FastaFile1    = properties.getInputID("FastaFile",PortInputDOWN);
        inputPath1 = FastaFile.getVectorFilePath(FastaFile1);
        input1     = Util.getFileNameAndExt(inputPath1);

        Vector<Integer>EmblFile2    = properties.getInputID("EmblFile",PortInputUP);
        inputPath2 = EmblFile.getVectorFilePath(EmblFile2);
        input2     = Util.getFileNameAndExt(inputPath2);

        Vector<Integer>FastaFile3    = properties.getInputID("FastaFile",PortInputUP);
        inputPath3 = FastaFile.getVectorFilePath(FastaFile3);
        input3     = Util.getFileNameAndExt(inputPath3);
        //INSERT YOUR INPUT TEST HERE
        if (FastaFile1.isEmpty()||input1.equals("Unknown")||input1.equals("")){
            setStatus(status_BadRequirements,"No FastaFile found.");
            return false;
        }
        //Check if it's else or not
        //else 
        if (EmblFile2.isEmpty()||input2.equals("Unknown")||input2.equals("")){
            setStatus(status_BadRequirements,"No EmblFile found.");
            return false;
        }
        //Check if it's else or not
        //else 
        if (FastaFile3.isEmpty()||input3.equals("Unknown")||input3.equals("")){
            setStatus(status_BadRequirements,"No FastaFile found.");
            return false;
        }

        return true;
    }
    @Override
    public String[] init_createCommandLine() {

        // In case program is started without edition
        pgrmStartWithoutEdition(properties);

        //Create ouputs
        output1 = outputPath+File.separator+"OutpuOf_"+input1+"_"+input2+"_"+input3+".cat";
        output2 = outputPath+File.separator+"OutpuOf_"+input1+"_"+input2+"_"+input3+".stderr";
        output3 = outputPath+File.separator+"OutpuOf_"+input1+"_"+input2+"_"+input3+".out";
        output4 = outputPath+File.separator+"OutpuOf_"+input1+"_"+input2+"_"+input3+".masked";
        output5 = outputPath+File.separator+"OutpuOf_"+input1+"_"+input2+"_"+input3+".tbl";
        output6 = outputPath+File.separator+"OutpuOf_"+input1+"_"+input2+"_"+input3+".cut";
        output7 = outputPath+File.separator+"OutpuOf_"+input1+"_"+input2+"_"+input3+".align";
        output8 = outputPath+File.separator+"OutpuOf_"+input1+"_"+input2+"_"+input3+".alert";
        output9 = outputPath+File.separator+"OutpuOf_"+input1+"_"+input2+"_"+input3+".withoutIS";
        
        // Program and Options
        String options = "";
        if (properties.isSet("Options_speed_and_search"))
            options += Util.findOptionsNew(Options_speed_and_search,properties);
        if (properties.isSet("Species_options"))
            options += Util.findOptionsNew(Species_options,properties);
        if (properties.isSet("Masking_options"))
            options += Util.findOptionsNew(Masking_options,properties);
        if (properties.isSet("Contamination_options"))
            options += Util.findOptionsNew(Contamination_options,properties);
        if (properties.isSet("Running_options"))
            options += Util.findOptionsNew(Running_options,properties);
        
        
        // Command line creation
        String[] com = new String[30];
        for (int i=0; i<com.length;i++) com[i]="";
        
        com[0]= "cmd.exe"; // Windows will de remove if another os is used
        com[1]= "/C";      // Windows will de remove if another os is used
        com[2]= properties.getExecutable();
        com[3]= options;
        if (inputPath1 != ("Unknown") || inputPath1.isEmpty()) {
            com[4]= "-estsequence "+inputPath1;
        }
        if (inputPath2 != ("Unknown") || inputPath2.isEmpty()) {
            com[5]= "-lib "+inputPath2;
        }
        if (inputPath3 != ("Unknown") || inputPath3.isEmpty()) {
            com[6]= "-lib "+inputPath3;
        }
        com[7]= output1;
        com[8]= output2;
        com[9]= output3;
        com[10]= output4;
        com[11]= output5;
        com[12]= output6;
        com[13]= output7;
        com[14]= output8;
        com[15]= output9;
        return com;
    }

        // def functions for init_createCommandLine
        // In case program is started without edition and params need to be setted
        private void pgrmStartWithoutEdition (workflow_properties properties){
            if (!(properties.isSet("Default_Options"))
                && !(properties.isSet("Advanced_Options"))
            ){
                Util.getDefaultPgrmValues(properties,false);
            }
        }
        

    /*
    * Output Parsing
    */

    @Override
    public void post_parseOutput(){
        CatFile.saveFile(properties,output1,"RepeatMasker","CatFile");
        TextFile.saveFile(properties,output2,"RepeatMasker","textFile");
        TextFile.saveFile(properties,output3,"RepeatMasker","textFile");
        maskedFile.saveFile(properties,output4,"RepeatMasker","maskedFile");
        tblFile.saveFile(properties,output5,"RepeatMasker","tblFile");
        TextFile.saveFile(properties,output6,"RepeatMasker","textFile");
        alignFile.saveFile(properties,output7,"RepeatMasker","alignFile");
        alertFile.saveFile(properties,output8,"RepeatMasker","alertFile");
        alertFile.saveFile(properties,output9,"RepeatMasker","alertFile");
        Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"RepeatMasker");
    }
}
