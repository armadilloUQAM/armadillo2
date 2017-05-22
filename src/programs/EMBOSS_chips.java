/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
* Author : JG
* Date   : Feb 2016
*/

package programs;

import biologic.FastaFile;
import biologic.ChipsFile;
import biologic.Results;
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
public class EMBOSS_chips extends RunProgram {
    // CREATE VARIABLES HERE
    private String doImage        = "jego/emboss";
    private String doPgrmPath     = "chips --auto";
    private String doSharedFolder = "/data";
    private String doName         = "emboss_EMBOSS_chips_armadilloWF_0";
    //INPUTS
    private String input1       ="";
    private String inputPath1   ="";
    private String inputInDo1   ="";
    private String inputPathDo1 ="";
    //OUTPUTS
    private String output1       ="";
    private String outputInDo1   ="";
    private String outputPathDo1 ="";
    //PATHS
    private static final String outputPath = "."+File.separator+"results"+File.separator+"EMBOSS"+File.separator+"chips"+File.separator+"";
    private static final String inputPath  = outputPath+File.separator+"INPUTS";

    private static final String[] Sq_panel = {
        "Sq_nosum_Box",
        "Sq_sum_Box"
    };


    public EMBOSS_chips(workflow_properties properties) {
        this.properties=properties;
        execute();
    }

    @Override
    public boolean init_checkRequirements() {
        // TEST INPUT VARIABLES HERE les ports sont PortInputUp, PortInputDOWN, PortInputDOWN2

        Vector<Integer>FastaFile_1    = properties.getInputID("FastaFile",PortInputDOWN);
        inputPath1 = FastaFile.getVectorFilePath(FastaFile_1);
        input1     = Util.getFileNameAndExt(inputPath1);

        //INSERT YOUR TEST HERE
        if (FastaFile_1.isEmpty()||input1.equals("Unknown")||input1.equals("")) {
            setStatus(status_BadRequirements,"No FastaFile found.");
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
        output1 = outputPath+File.separator+"OutpuOf_"+input1+".chips";
        outputInDo1 = doSharedFolder+File.separator+"OutpuOf_"+input1+".chips";
        
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
        com[5]= "--seqall "+inputInDo1;
        com[6]= "-outfile "+outputInDo1;
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
        ChipsFile.saveFile(properties,output1,"EMBOSS_chips","ChipsFile");
        Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"EMBOSS_chips");
    }
}
