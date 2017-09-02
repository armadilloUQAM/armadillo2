/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
* Author : J-G
* Date   : 26-08-2017
*/

package programs;

import biologic.BCFFile;
import biologic.VCFFile;
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
 * @date 26-08-2017
 *
 */
public class bcftools_call extends RunProgram {
    // CREATE VARIABLES HERE
    private String doImage        = "jego/samtools";
    private String doPgrmPath     = "bcftools call";
    private String doName         = "bcftools_call_armadilloWF_0";
    private String doInputs       = "/data/inputs/";
    private String doOutputs      = "/data/outputs/";
    private HashMap<String,String> sharedFolders = new HashMap<String,String>();
    //INPUTS
    private String input0       = "";
    private String input1       = "";
    private String inputPath1   = "";
    private String input2       = "";
    private String inputPath2   = "";
    //OUTPUTS
    private String output1       = "";
    private String outputInDo1   = "";
    //PATHS
    private static final String outputsPath = "."+File.separator+"results"+File.separator+"BCFTOOLS"+File.separator+"call"+File.separator+"";
    private static final String[] Advanced_Options = {
        "AO_IOO__variantsHYPHENSYMBOLonly_box",
        "AO_FFO__outputHYPHENSYMBOLtype_box",
        //"AO_FFO__outputHYPHENSYMBOLtype_JComboBoxValue",
        "AO_CVCO__multiallelicHYPHENSYMBOLcaller_box"
    };


    public bcftools_call(workflow_properties properties){
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

        Vector<Integer>VCFFile1    = properties.getInputID("VCFFile",PortInputDOWN);
        inputPath1 = VCFFile.getVectorFilePath(VCFFile1);
        input1     = Util.getFileNameAndExt(inputPath1);
        
        Vector<Integer>BCFFile2    = properties.getInputID("BCFFile",PortInputDOWN);
        inputPath2 = BCFFile.getVectorFilePath(BCFFile2);
        input2     = Util.getFileNameAndExt(inputPath2);
        
        //Create ouputs
        input0 = input1;
        if (input1=="")
            input0 = input2;
        output1 = specificPath+File.separator+"OutputOf_"+input0+".vcf";
        output1 = Util.onlyOneOutputOf(output1);
        outputInDo1 = " --output "+doOutputs+"OutputOf_"+input0+".vcf";
        outputInDo1 = Util.onlyOneOutputOf(outputInDo1);
        if (properties.isSet("AO_FFO__outputHYPHENSYMBOLtype_box")){
            String bu = properties.get("AO_FFO__outputHYPHENSYMBOLtype_box");
            if (bu=="u"){
                output1 = specificPath+File.separator+"OutputOf_"+input0+".bcf";
                output1 = Util.onlyOneOutputOf(output1);
                outputInDo1 = " --output "+doOutputs+"OutputOf_"+input0+".bcf";
                outputInDo1 = Util.onlyOneOutputOf(outputInDo1);
            }
        }
        
        //INSERT YOUR INPUT TEST HERE
        if ((VCFFile1.isEmpty()||input1.equals("Unknown")||input1.equals("")) && 
            (BCFFile2.isEmpty()||input2.equals("Unknown")||input2.equals(""))){
            setStatus(status_BadRequirements,"No Inputs File found.");
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
        if (properties.isSet("Advanced_Options"))
            options += Util.findOptionsNew(Advanced_Options,properties);
        
        
        // Docker command line
        String[] allInputsPathOrder = {inputPath1,inputPath2};
        HashMap<String,String> allInputsPath =  new HashMap<String,String>();
        allInputsPath.put(inputPath1,"");
        allInputsPath.put(inputPath2,"");
        String allDockerInputs = Docker.createAllDockerInputs(allInputsPath,allInputsPathOrder,doInputs);
        String dockerCli = doPgrmPath+" "+options + allDockerInputs + outputInDo1;
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
        if (output1.endsWith("vcf"))
            VCFFile.saveFile(properties,output1,"bcftools_call","VCFFile");
        if (output1.endsWith("bcf"))
            BCFFile.saveFile(properties,output1,"bcftools_call","BCFFile");
        Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"bcftools_call");
    }
}
