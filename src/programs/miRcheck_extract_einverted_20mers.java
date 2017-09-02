/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package programs;

import biologic.RNAFoldFile;
import biologic.TextFile;
import biologic.Results;
import biologic.Text;
import configuration.Docker;
import configuration.Util;
import java.util.Vector;
import program.RunProgram;
import static program.RunProgram.PortInputUP;
import static program.RunProgram.df;
import static program.RunProgram.status_error;
import workflows.workflow_properties;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static program.RunProgram.PortInputDOWN;
import static program.RunProgram.status_BadRequirements;
/**
 *
 * @author Jérémy Goimard
 * @date Aout 2015
 *
 */
public class miRcheck_extract_einverted_20mers extends RunProgram{
    // CREATE VARIABLES HERE
    private String doImage        = "jego/mircheck";
    private String doPgrmPath     = "../miRcheck/extract_einverted_20mers.pl";
    private String doSharedFolder = "/data";
    private String doName         = "mircheck_MIRCHECK_extract_einverted_20mers_armadilloWF_0";
    private String optionsChoosed = "";
    
    //INPUTS
    private String input1       ="";
    private String inputPath1   ="";
    private String inputInDo1   ="";
    private String inputPathDo1 ="";
    //OUTPUTS
    private String output1       ="";
    private String outputInDo1   ="";
    private String outputPathDo1 ="";

    private static final String outputPath = "."+File.separator+"results"+File.separator+"miRcheck"+File.separator+"extract_einverted_20mers"+File.separator+"";
    private static final String inputPath  = outputPath+File.separator+"INPUTS";

    private static final String[] advanced_options = {
        "C_block_box",
        "C_ass_box",
        "C_bpExt_box",
        "C_fbackMin_box",
        "C_minUnpair_box",
        "C_mirBulge_box",
        "C_sizeDiff_box",
        "C_starBulge_box",
        "C_starUnpair_box",
        "C_unpair_box",
        "C_maxOneNt_box",
        "C_maxTwoNt_box"
    };
        

    public miRcheck_extract_einverted_20mers(workflow_properties properties) {
        this.properties=properties;
        execute();
    }
    
    @Override
    public boolean init_checkRequirements() {
        // Inputs
        Vector<Integer>InputFile_1    = properties.getInputID("RNAFoldFile",PortInputDOWN);
        inputPath1 = RNAFoldFile.getVectorFilePath(InputFile_1);
        input1     = Util.getFileNameAndExt(inputPath1);

        //INSERT YOUR TEST HERE
        if (InputFile_1.isEmpty()||input1.equals("Unknown")||input1.equals("")) {
            setStatus(status_BadRequirements,"No RNAFoldFile found.");
            return false;
        }

        //INSERT DOCKER SHARED FILES COPY HERE
        if (!Util.CreateDir(outputPath) && !Util.DirExists(outputPath)){
            setStatus(status_BadRequirements,"Not able to create OUTPUTS directory files");
            return false;
        }
        if (!Util.CreateDir(inputPath) && !Util.DirExists(inputPath)){
            setStatus(status_BadRequirements,"Not able to create INPUTS directory files");
            return false;
        }

        inputPathDo1 = inputPath+File.separator+input1;
        if (!(Util.copy(inputPath1,inputPathDo1))) {
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
        output1 = outputPath+File.separator+"OutpuOf_"+input1+".fold";
        outputInDo1 = doSharedFolder+File.separator+"OutpuOf_"+input1+".fold";
        
        // Program and Options
        if (properties.get("advanced_options_jbutton").equals("true")){
            optionsChoosed = Util.findOptionsNew(advanced_options,properties);
        }
        
        String[] com = new String[30];
        for (int i=0; i<com.length;i++) com[i]="";
        
        com[0]="cmd.exe"; // Windows will de remove if another os is used
        com[1]="/C";      // Windows will de remove if another os is used
        com[2]=properties.getExecutable();
        com[3]= "exec "+doName+" "+doPgrmPath ;
        com[4]= " "+inputInDo1;
        com[5]= " "+outputInDo1;
        com[6]= " "+optionsChoosed;
        return com;
    }
            // Sub functions for init_createCommandLine
        // In case program is started without edition and params need to be setted
        private void pgrmStartWithoutEdition (workflow_properties properties) {
            if (!(properties.isSet("default_options_jbutton"))
                && !(properties.isSet("advanced_options_jbutton"))
            ) {
                Util.getDefaultPgrmValues(properties,false);
            }
        }
        
    @Override
    public void post_parseOutput() {
        Util.deleteDir(inputPath);
        Docker.cleanContainer(properties,doName);
        RNAFoldFile.saveFile(properties,output1,"MIRCHECK_extract_einverted_20mers","RNAFoldFile");
        Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"MIRCHECK_extract_einverted_20mers");
    }
    
    
}
