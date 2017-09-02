/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
* Author : J-G
* Date   : 25-08-2017
*/

package programs;

import biologic.BamCsiFile;
import biologic.CramFile;
import biologic.BamBaiFile;
import biologic.BamFile;
import biologic.CramCraiFile;
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
import java.util.HashMap;


/**
 *
 * @author J-G
 * @date 25-08-2017
 *
 */
public class samtools_index extends RunProgram {
    // CREATE VARIABLES HERE
    private String doImage        = "jego/samtools";
    private String doPgrmPath     = "samtools index";
    private String doName         = "samtools_index_armadilloWF_0";
    private String doInputs       = "/data/inputs/";
    private String doOutputs      = "/data/outputs/";
    private HashMap<String,String> sharedFolders = new HashMap<String,String>();
    //INPUTS
    private String input1       = "";
    private String inputPath1   = "";
    private String input2       = "";
    private String inputPath2   = "";
    //OUTPUTS
    private String output1       = "";
    private String outputInDo1   = "";
    //PATHS
    private static final String outputsPath = "."+File.separator+"results"+File.separator+"SAMTOOLS"+File.separator+"index"+File.separator+"";
    private static final String[] Advanced_Options = {
        "AO_AO1_b_box",
        "AO_AO1_c_box",
        "AO_AO1_m_box"//,
        //"AO_AO1_m_JSpinnerValue"
    };


    public samtools_index(workflow_properties properties){
        this.properties=properties;
        execute();
    }

    @Override
    public boolean init_checkRequirements(){

        // TEST OUTPUT PATH
        String specificId = Util.returnRandomAndDate();
        if (properties.isSet("ObjectID")) {
            String oId = properties.get("ObjectID");
            oId = Util.replaceSpaceByUnderscore(oId);
            specificId = specificId+"_"+oId;
        }
        String specificPath = outputsPath+specificId;
        if (!Util.CreateDir(specificPath) && !Util.DirExists(specificPath)){
            setStatus(status_BadRequirements,"Not able to access or create OUTPUTS directory files");
            return false;
        }

        
        // TEST INPUT VARIABLES HERE
        // ports are 3-PortInputUp, 2-PortInputDOWN, 4-PortInputDOWN2

        Vector<Integer>BamFile1    = properties.getInputID("BamFile",PortInputDOWN);
        inputPath1 = BamFile.getVectorFilePath(BamFile1);
        input1     = Util.getFileNameAndExt(inputPath1);

        Vector<Integer>CramFile2    = properties.getInputID("CramFile",PortInputDOWN);
        inputPath2 = CramFile.getVectorFilePath(CramFile2);
        input2     = Util.getFileNameAndExt(inputPath2);
        //Create ouputs
        String outputFinal="OutputOf_"+input1+".bam";
        if (properties.isSet("AO_AO1_c_box")||properties.isSet("AO_AO1_m_box"))
            outputFinal+=".csi";
        else
            outputFinal+=".bai";
        if (input2!="")
            outputFinal="OutputOf_"+input2+".cram.crai";
        output1 = specificPath+File.separator+outputFinal;
        outputInDo1 = doOutputs+outputFinal;
        output1 = Util.onlyOneOutputOf(output1);
        outputInDo1 = Util.onlyOneOutputOf(outputInDo1);

        //INSERT YOUR INPUT TEST HERE
        if ((BamFile1.isEmpty()||input1.equals("Unknown")||input1.equals("")) &&
            (CramFile2.isEmpty()||input2.equals("Unknown")||input2.equals(""))){
            setStatus(status_BadRequirements,"No CramFile found.");
            return false;
        }

        //PREPARE DOCKER SHARED FILES
        String[] allInputsPath = {inputPath1,inputPath2};
        sharedFolders = Docker.createSharedFolders(allInputsPath,doInputs);
        sharedFolders.put(specificPath,doOutputs);

        // Launch Docker
        if (Docker.isDockerHere(properties)){
            doName = Docker.getContainerName(properties,doName);
            if (!dockerInitContainer(properties,sharedFolders, doName, doImage))
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

        
        // Program and Options
        String options = "";
        if (properties.isSet("Advanced_Options")&&input2=="")
            options += Util.findOptionsNew(Advanced_Options,properties);
        
        
        // Docker command line
        String[] allInputsPathOrder = {inputPath1,inputPath2};
        HashMap<String,String> allInputsPath = new HashMap<String,String>();
        allInputsPath.put(inputPath1,"");
        allInputsPath.put(inputPath2,"");
        
        String allDockerInputs = Docker.createAllDockerInputs(allInputsPath,allInputsPathOrder,doInputs);
        String dockerCli = doPgrmPath+" "+options + allDockerInputs +  outputInDo1;
        Docker.prepareDockerBashFile(properties,doName,dockerCli);

        setStatus(status_running,"DockerRunningCommandLine: \n$ "+dockerCli+"\n");
        String dockerBashCli = "exec -i "+doName+" sh -c './dockerBash.sh'";
        
        // Command line creation
        String[] com = new String[30];
        for (int i=0; i<com.length;i++) com[i]="";
        
        com[0]= "cmd.exe"; // Windows will de remove if another os is used
        com[1]= "/C";      // Windows will de remove if another os is used
        com[2]= properties.getExecutable();
        com[3]= dockerBashCli;
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
        Docker.cleanContainer(properties,doName);
        if (output1.endsWith("bai"))
            BamBaiFile.saveFile(properties,output1,"samtools_index","BamBaiFile");
        if (output1.endsWith("crai"))
            CramCraiFile.saveFile(properties,output1,"samtools_index","CramCraiFile");
        if (output1.endsWith("csi"))
            BamCsiFile.saveFile(properties,output1,"samtools_index","BamCsiFile");
        Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"samtools_index");
    }
}
