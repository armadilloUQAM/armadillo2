/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
* Author : J-G
* Date   : 25-08-2017
*/

package programs;

import biologic.CramFile;
import biologic.SamFile;
import biologic.BamFile;
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
public class samtools_sort extends RunProgram {
    // CREATE VARIABLES HERE
    private String doImage        = "jego/samtools";
    private String doPgrmPath     = "samtools sort";
    private String doName         = "samtools_sort_armadilloWF_0";
    private String doInputs       = "/data/inputs/";
    private String doOutputs      = "/data/outputs/";
    private HashMap<String,String> sharedFolders = new HashMap<String,String>();
    //INPUTS
    private String input1       = "";
    private String inputPath1   = "";
    private String input2       = "";
    private String inputPath2   = "";
    private String input3       = "";
    private String inputPath3   = "";
    //OUTPUTS
    private String output1       = "";
    private String outputInDo1   = "";
    //PATHS
    private static final String outputsPath = "."+File.separator+"results"+File.separator+"SAMTOOLS"+File.separator+"sort"+File.separator+"";
    private static final String[] Advanced_Options_1 = {
        "AO_AO1_T_box",
        //"AO_AO1_T_JTextFieldValue",
        "AO_AO1_H_box",
        "AO_AO1_O_box"//,
        //"AO_AO1_O_JComboBoxValue"
    };


    public samtools_sort(workflow_properties properties){
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

        Vector<Integer>SamFile1    = properties.getInputID("SamFile",PortInputDOWN);
        inputPath1 = SamFile.getVectorFilePath(SamFile1);
        input1     = Util.getFileNameAndExt(inputPath1);

        Vector<Integer>BamFile2    = properties.getInputID("BamFile",PortInputDOWN);
        inputPath2 = BamFile.getVectorFilePath(BamFile2);
        input2     = Util.getFileNameAndExt(inputPath2);

        Vector<Integer>CramFile3    = properties.getInputID("CramFile",PortInputDOWN);
        inputPath3 = CramFile.getVectorFilePath(CramFile3);
        input3     = Util.getFileNameAndExt(inputPath3);
        //Create ouputs
        String outputFinal="OutputOf_"+input1;
        if (input2!="")
            outputFinal="OutputOf_"+input2;
        if (input3!="")
            outputFinal="OutputOf_"+input3;
        if (properties.isSet("AO_AO1_O_box")){
            if (properties.get("AO_AO1_O_box")=="bam")
                outputFinal = outputFinal+".bam";
            if (properties.get("AO_AO1_O_box")=="sam")
                outputFinal = outputFinal+".sam";
            if (properties.get("AO_AO1_O_box")=="cram")
                outputFinal = outputFinal+".cram";
        }
        output1 = specificPath+File.separator+outputFinal;
        outputInDo1 = doOutputs+outputFinal;
        output1 = Util.onlyOneOutputOf(output1);
        outputInDo1 = Util.onlyOneOutputOf(outputInDo1);
        
        //INSERT YOUR INPUT TEST HERE
        if ((SamFile1.isEmpty()||input1.equals("Unknown")||input1.equals("")) && 
            (BamFile2.isEmpty()||input2.equals("Unknown")||input2.equals("")) &&
            (CramFile3.isEmpty()||input3.equals("Unknown")||input3.equals(""))){
            setStatus(status_BadRequirements,"No Input File found.");
            return false;
        }

        //PREPARE DOCKER SHARED FILES
        String[] allInputsPath = {inputPath1,inputPath2,inputPath3};
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
        if (properties.isSet("Advanced_Options"))
            options += Util.findOptionsNew(Advanced_Options_1,properties);
        
        
        // Docker command line
        String[] allInputsPathOrder = {inputPath1,inputPath2,inputPath3};
        HashMap<String,String> allInputsPath = new HashMap<String,String>();
        allInputsPath.put(inputPath1,"");
        allInputsPath.put(inputPath2,"");
        allInputsPath.put(inputPath3,"");
        
        String allDockerInputs = Docker.createAllDockerInputs(allInputsPath,allInputsPathOrder,doInputs);
        String dockerCli = doPgrmPath+" "+options + allDockerInputs +  " -o " +  outputInDo1 ;
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
        if (output1.endsWith("bam"))
            BamFile.saveFile(properties,output1,"samtools_sort","BamFile");
        if (output1.endsWith("Cram"))
            CramFile.saveFile(properties,output1,"samtools_sort","CramFile");
        if (output1.endsWith("sam"))
            SamFile.saveFile(properties,output1,"samtools_sort","SamFile");
        Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"samtools_sort");
    }
}
