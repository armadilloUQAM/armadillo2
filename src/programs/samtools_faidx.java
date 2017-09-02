/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
* Author : J-G
* Date   : 20-08-2017
*/

package programs;

import biologic.FastaFile;
import biologic.FaidxFile;
import configuration.Docker;
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
 * @date 20-08-2017
 *
 */
public class samtools_faidx extends RunProgram {
    // CREATE VARIABLES HERE
    private String doImage        = "jego/samtools";
    private String doPgrmPath     = "samtools faidx";
    private String doSharedFolder = "/data";
    private String doName         = "jego/samtools";
    //INPUTS
    private String input1       = "";
    private String inputPath1   = "";
    private String inputInDo1   = "";
    private String inputPathDo1 = "";
    //OUTPUTS
    private String output1       = "";
    private String outputInDo1   = "";
    private String outputPathDo1 = "";
    //PATHS
    private static final String outputPath = "."+File.separator+"results"+File.separator+"SAMTOOLS"+File.separator+"faidx"+File.separator+"";
    private static final String inputPath  = outputPath+File.separator+"INPUTS";


    public samtools_faidx(workflow_properties properties){
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
        //INSERT YOUR INPUT TEST HERE
        if (FastaFile1.isEmpty()||input1.equals("Unknown")||input1.equals("")){
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
        if (!(Util.copy(inputPath1,inputPathDo1))){
            setStatus(status_BadRequirements,"Not able to copy files used by docker container");
            return false;
        }
        inputInDo1 = doSharedFolder+File.separator+"INPUTS"+File.separator+input1;
        input1 = Util.getFileName(inputPath1);

        // Launch Docker
        if (Docker.isDockerHere(properties)){
            doName = Docker.getContainerName(properties,doName);
            if (!dockerInit(outputPath,doSharedFolder,doName,doImage))
                return false;
        } else {
            setStatus(status_BadRequirements,"Docker is not found. Please install docker");
            return false;
        }
        return true;
    }
    @Override
    public String[] init_createCommandLine() {

        // In case program is started without edition
        pgrmStartWithoutEdition(properties);

        //Create ouputs
        output1 = outputPath+File.separator+"OutpuOf_"+input1+".fa.fai";
        outputInDo1 = doSharedFolder+File.separator+"OutpuOf_"+input1+".fa.fai";
        
        // Program and Options
        String options = "";
        
        
        // Command line creation
        String[] com = new String[30];
        for (int i=0; i<com.length;i++) com[i]="";
        
        com[0]= "cmd.exe"; // Windows will de remove if another os is used
        com[1]= "/C";      // Windows will de remove if another os is used
        com[2]= properties.getExecutable();
        com[3]= "exec "+doName+" "+doPgrmPath;
        com[4]= options;
        com[5]= inputInDo1;
        com[6]= outputInDo1;
        return com;
    }

        // def functions for init_createCommandLine
        // In case program is started without edition and params need to be setted
        private void pgrmStartWithoutEdition (workflow_properties properties){
            if (!(properties.isSet("Default_Options"))
            ){
                Util.getDefaultPgrmValues(properties,false);
            }
        }
        

    /*
    * Output Parsing
    */

    @Override
    public void post_parseOutput(){
        Docker.cleanContainer(properties,doName);
        FaidxFile.saveFile(properties,output1,"samtools_faidx","FaidxFile");
        Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"samtools_faidx");
    }
}
