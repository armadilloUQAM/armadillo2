/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
* Author : JG
* Date   : Feb 2016
*/

package programs;

import biologic.FastaFile;
import biologic.GenomeFile;
import biologic.EmblFile;
import biologic.Results;
import biologic.Est2genomeFile;
import configuration.Docker;
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
 * @author JG
 * @date Feb 2016
 *
 */
public class EMBOSS_est2genome extends RunProgram {
    // CREATE VARIABLES HERE
    private String doImage        = "jego/emboss";
    private String doPgrmPath     = "est2genome --auto";
    private String doSharedFolder = "/data";
    private String doName         = "emboss_EMBOSS_est2genome_armadilloWF_0";
    //INPUTS
    private String input1       ="";
    private String inputPath1   ="";
    private String inputInDo1   ="";
    private String inputPathDo1 ="";
    private String input2       ="";
    private String inputPath2   ="";
    private String inputInDo2   ="";
    private String inputPathDo2 ="";
    private String input3       ="";
    private String inputPath3   ="";
    private String inputInDo3   ="";
    private String inputPathDo3 ="";
    //OUTPUTS
    private String output1       ="";
    private String outputInDo1   ="";
    private String outputPathDo1 ="";
    //PATHS
    private static final String outputPath = "."+File.separator+"results"+File.separator+"EMBOSS"+File.separator+"est2genome"+File.separator+"";
    private static final String inputPath  = outputPath+File.separator+"INPUTS";

    private static final String[] Sq_panel = {
        "Sq_match_Box",
        //"Sq_match_Box_IntValue",
        "Sq_mismatch_Box",
        //"Sq_mismatch_Box_IntValue",
        "Sq_gappenalty_Box",
        //"Sq_gappenalty_Box_IntValue",
        "Sq_intronpenalty_Box",
        //"Sq_intronpenalty_Box_IntValue",
        "Sq_splicepenalty_Box",
        //"Sq_splicepenalty_Box_IntValue",
        "Sq_minscore_Box",
        //"Sq_minscore_Box_IntValue",
        "Sq_reverse_Box",
        //"Sq_reverse_Box_BooValue",
        "Sq_usesplice_Box",
        //"Sq_usesplice_Box_BooValue",
        "Sq_mode_Box",
        //"Sq_mode_Box_List",
        "Sq_best_Box",
        //"Sq_best_Box_BooValue",
        "Sq_space_Box",
        //"Sq_space_Box_FloValue",
        "Sq_shuffle_Box",
        //"Sq_shuffle_Box_IntValue",
        "Sq_seed_Box",
        //"Sq_seed_Box_IntValue",
        "Sq_align_Box",
        //"Sq_align_Box_BooValue",
        "Sq_width_Box"//,
        //"Sq_width_Box_IntValue"
    };


    public EMBOSS_est2genome(workflow_properties properties) {
        this.properties=properties;
        execute();
    }

    @Override
    public boolean init_checkRequirements() {
        // TEST INPUT VARIABLES HERE les ports sont PortInputUp, PortInputDOWN, PortInputDOWN2

        Vector<Integer>FastaFile_1    = properties.getInputID("FastaFile",PortInputDOWN);
        inputPath1 = FastaFile.getVectorFilePath(FastaFile_1);
        input1     = Util.getFileNameAndExt(inputPath1);

        Vector<Integer>EmblFile_2    = properties.getInputID("EmblFile",PortInputDOWN);
        inputPath2 = EmblFile.getVectorFilePath(EmblFile_2);
        input2     = Util.getFileNameAndExt(inputPath2);

        Vector<Integer>GenomeFile_3    = properties.getInputID("GenomeFile",PortInputUP);
        inputPath3 = GenomeFile.getVectorFilePath(GenomeFile_3);
        input3     = Util.getFileNameAndExt(inputPath3);

        //INSERT YOUR TEST HERE
        if (FastaFile_1.isEmpty()||input1.equals("Unknown")||input1.equals("")) {
            setStatus(status_BadRequirements,"No FastaFile found.");
            return false;
        }
        else if (EmblFile_2.isEmpty()||input2.equals("Unknown")||input2.equals("")) {
            setStatus(status_BadRequirements,"No EmblFile found.");
            return false;
        }
        else if (GenomeFile_3.isEmpty()||input3.equals("Unknown")||input3.equals("")) {
            setStatus(status_BadRequirements,"No GenomeFile found.");
            return false;
        }

        //INSERT DOCKER SHARED FILES COPY HERE
        if (!Util.CreateDir(inputPath) && !Util.DirExists(inputPath)){
            setStatus(status_BadRequirements,"Not able to create INPUTS directory files");
            return false;
        }
        if (!Util.CreateDir(outputPath) && !Util.DirExists(outputPath)){
            setStatus(status_BadRequirements,"Not able to create OUTPUTS directory files");
            return false;
        }

        inputPathDo1 = outputPath+File.separator+"INPUTS"+File.separator+input1;
        if (!(Util.copy(inputPath1,inputPathDo1))) {
            setStatus(status_BadRequirements,"Not able to copy files");
            return false;
        }
        inputInDo1 = doSharedFolder+File.separator+"INPUTS"+File.separator+input1;
        input1 = Util.getFileName(inputPath1);

        inputPathDo2 = outputPath+File.separator+"INPUTS"+File.separator+input2;
        if (!(Util.copy(inputPath2,inputPathDo2))) {
            setStatus(status_BadRequirements,"Not able to copy files");
            return false;
        }
        inputInDo2 = doSharedFolder+File.separator+"INPUTS"+File.separator+input2;
        input2 = Util.getFileName(inputPath2);

        inputPathDo3 = outputPath+File.separator+"INPUTS"+File.separator+input3;
        if (!(Util.copy(inputPath3,inputPathDo3))) {
            setStatus(status_BadRequirements,"Not able to copy files");
            return false;
        }
        inputInDo3 = doSharedFolder+File.separator+"INPUTS"+File.separator+input3;
        input3 = Util.getFileName(inputPath3);

        // TEST Docker initialisation
        doName = Docker.getContainersVal(doName);
        if (!dockerInit(outputPath,doSharedFolder,doName,doImage)) {
            Docker.cleanContainer(doName);
            return false;
        } else {
            properties.put("DOCKERName",doName);
        }

        return true;
    }

    @Override
    public String[] init_createCommandLine() {

        // In case program is started without edition
        pgrmStartWithoutEdition(properties);

        //Create ouputs
        output1 = outputPath+File.separator+"OutpuOf_"+input1+".est2genome";
        outputInDo1 = doSharedFolder+File.separator+"OutpuOf_"+input1+".est2genome";
        
        // Program and Options
        String options = "";
        if (!properties.isSet("default_RButton")) {
            options += Util.findOptionsNew(Sq_panel,properties);
        }
        
        // Command line creation
        String[] com = new String[30];
        for (int i=0; i<com.length;i++) com[i]="";
        
        com[0]="cmd.exe"; // Windows will de remove if another os is used
        com[1]="/C";      // Windows will de remove if another os is used
        com[2]=properties.getExecutable();
        com[3]= "exec "+doName+" "+doPgrmPath ;
        com[4]=options;
        com[5]= "-estsequence "+inputInDo1;
        com[6]= "-estsequence "+inputInDo2;
        com[7]= "-genomesequence "+inputInDo3;
        com[8]= "-outfile "+outputInDo1;
        return com;
    }

        // Sub functions for init_createCommandLine
        // In case program is started without edition and params need to be setted
        private void pgrmStartWithoutEdition (workflow_properties properties) {
            if (!(properties.isSet("default_RButton"))
                && !(properties.isSet("Advanced_Options_RButton"))
            ) {
                Util.getDefaultPgrmValues(properties,false);
            }
        }

    /*
    * Output Parsing
    */

    @Override
    public void post_parseOutput() {
        Util.deleteDir(inputPath);
        Docker.cleanContainer(doName);
        Est2genomeFile.saveFile(properties,output1,"EMBOSS_est2genome","Est2genomeFile");
        Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"EMBOSS_est2genome");
    }
}
