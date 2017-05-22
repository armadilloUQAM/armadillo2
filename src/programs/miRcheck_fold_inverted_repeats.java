/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package programs;

import biologic.RNAFoldFile;
import biologic.FastaFile;
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
public class miRcheck_fold_inverted_repeats extends RunProgram{
    
    // CREATE VARIABLES HERE
    private String doImage        = "jego/mircheck";
    private String doPgrmPath     = "../miRcheck/fold_inverted_repeats.pl";
    private String doSharedFolder = "/data";
    private String doName         = "mircheck_MIRCHECK_fold_inverted_repeats_armadilloWF_0";
    private String optionsChoosed = "";
    
    //INPUTS
    private String input1       ="";
    private String inputPath1   ="";
    private String inputInDo1   ="";
    private String inputPathDo1 ="";
    private String input2       ="";
    private String inputPath2   ="";
    private String inputInDo2   ="";
    private String inputPathDo2 ="";
    //OUTPUTS
    private String output1       ="";
    private String outputInDo1   ="";
    private String outputPathDo1 ="";

    private static final String outputPath = "."+File.separator+"results"+File.separator+"miRcheck"+File.separator+"fold_inverted_repeats"+File.separator+"";
    private static final String inputsdir  = File.separator+"INPUTS";
    private static final String inputPath  = outputPath+inputsdir;

    private static final String[] advanced_options = {
        "C_flankingNt_box"
    };
        
    public miRcheck_fold_inverted_repeats(workflow_properties properties) {
        this.properties=properties;
        execute();
    }
    
    @Override
    public boolean init_checkRequirements() {
        // Inputs
        Vector<Integer>InputFile_1    = properties.getInputID("RNAFoldFile",PortInputDOWN);
        inputPath1 = RNAFoldFile.getVectorFilePath(InputFile_1);
        input1     = Util.getFileNameAndExt(inputPath1);

        Vector<Integer>InputFile_2    = properties.getInputID("FastaFile",PortInputUP);
        inputPath2 = FastaFile.getVectorFilePath(InputFile_2);
        input2     = Util.getFileNameAndExt(inputPath2);

        //INSERT YOUR TEST HERE
        if (InputFile_1.isEmpty()||input1.equals("Unknown")||input1.equals("")) {
            setStatus(status_BadRequirements,"No RNAFoldFile found.");
            return false;
        }
        if (InputFile_2.isEmpty()||input2.equals("Unknown")||input2.equals("")) {
            setStatus(status_BadRequirements,"No FastaFile Genome found.");
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
        inputInDo1 = doSharedFolder+inputsdir+File.separator+input1;
        input1 = Util.getFileName(inputPath1);

        inputPathDo2 = inputPath+File.separator+input2;
        if (!(Util.copy(inputPath2,inputPathDo2))) {
            setStatus(status_BadRequirements,"Not able to copy files used by docker container");
            return false;
        }
        inputInDo2 = doSharedFolder+inputsdir+File.separator+input2;
        input2 = Util.getFileName(inputPath2);

        // TEST Docker initialisation
        doName = Docker.getContainersVal(doName);
        if (!dockerInit(outputPath,doSharedFolder,doName,doImage)) {
            Docker.cleanContainer(doName);
            setStatus(status_BadRequirements,"Not able to initiate docker container");
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
        com[5]= " "+inputInDo2;
        com[6]= " "+outputInDo1;
        com[7]= " "+optionsChoosed;
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
        Docker.cleanContainer(doName);
        RNAFoldFile.saveFile(properties,output1,"MIRCHECK_fold_inverted_repeats","RNAFoldFile");
        Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"MIRCHECK_fold_inverted_repeats");
    }
    
}
