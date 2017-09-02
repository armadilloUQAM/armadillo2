/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
* Author : Jérémy Goimard
* Date   : 0 116
*/

package programs;


import biologic.Results;
import biologic.FastaFile;
import biologic.Input;
import biologic.TextFile;
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


/**
 *
 * @author Jérémy Goimard
 * @date 0 116
 *
 */
public class miRdup extends RunProgram {
    // CREATE VARIABLES HERE
    private String input1     ="";
    private String inputPath1 ="";
    private String input2     ="";
    private String inputPath2 ="";
    private String input3     ="";
    private String inputPath3 ="";
    private String output1     ="";
    private String output2     ="";
    private static final String outputPath = ".."+File.separator+"source"+File.separator+"mir";

    private static final String[] O_VO_panel = {
        "O_VO_c_Box",
        "O_VO_c_Box_Text",
        "O_VO_p_Box",
        "O_VO_p_Box_Text"
    };

    private static final String[] O_TO_panel = {
        "O_TO_k_Box",
        "O_TO_k_Box_DirFiles",
        "O_TO_m_Box",
        "O_TO_m_Box_DirRep",
        "O_TO_h_Box",
        "O_TO_h_Box_Text",
        "O_TO_o_Box",
        "O_TO_o_Box_Text",
        "O_TO_s_Box",
        "O_TO_s_Box_Text",
        "O_TO_v_Box",
        "O_TO_v_Box_Text"
    };

    private static final String[] O_PO_panel = {
        "O_PO_predict_Box",
        "O_PO_predict_Box_Text",
        "O_PO_u_Box",
        "O_PO_u_Box_Text",
        "O_PO_d_Box",
        "O_PO_d_Box_Text",
        "O_PO_f_Box",
        "O_PO_f_Box_Text",
        "O_PO_i_Box",
        "O_PO_i_Box_Text"
    };

    private static final String[] O_C_panel = {
        "O_C_r_Box",
        "O_C_r_Box_DirFile"
    };


    public miRdup(workflow_properties properties) {
        this.properties=properties;
        execute();
    }
    
    @Override
    public boolean init_checkRequirements() {
        // TEST INPUT VARIABLES HERE les ports sont PortInputUp, PortInputDOWN, PortInputDOWN2
        Vector<Integer>TextFile_1    = properties.getInputID("TextFile",PortInputUP);
        inputPath1 = TextFile.getVectorFilePath(TextFile_1);
        input1     = Util.getFileName(inputPath1);
        Vector<Integer>FastaFile_2    = properties.getInputID("FastaFile",PortInputDOWN);
        inputPath2 = FastaFile.getVectorFilePath(FastaFile_2);
        input2     = Util.getFileName(inputPath2);
        Vector<Integer>FastaFile_3    = properties.getInputID("FastaFile",PortInputDOWN2);
        inputPath3 = FastaFile.getVectorFilePath(FastaFile_3);
        input3     = Util.getFileName(inputPath3);

        //INSERT YOUR TEST HERE
        if (TextFile_1.isEmpty()||input1.equals("Unknown")||input1.equals("")) {
            setStatus(status_BadRequirements,"No TextFile found.");
            return false;
        }
        else if (FastaFile_2.isEmpty()||input2.equals("Unknown")||input2.equals("")) {
            setStatus(status_BadRequirements,"No FastaFile found.");
            return false;
        }
        else if (FastaFile_3.isEmpty()||input3.equals("Unknown")||input3.equals("")) {
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
        output1 = outputPath+File.separator+input1+".";
        output2 = outputPath+File.separator+input2+".";
        //output3 = outputPath+File.separator+input3+".";
        
        // Program and Options
        String options = "";
        options += Util.findOptions(O_TO_panel,properties);
        options += Util.findOptions(O_PO_panel,properties);
        options += Util.findOptions(O_C_panel,properties);
        
        // Command line creation
        String[] com = new String[30];
        for (int i=0; i<com.length;i++) com[i]="";
        
        com[0]="cmd.exe"; // Windows will de remove if another os is used
        com[1]="/C";      // Windows will de remove if another os is used
        com[2]=properties.getExecutable();
        com[3]=options;
        
        return com;
    }

        // Sub functions for init_createCommandLine
        // In case program is started without edition and params need to be setted
        private void pgrmStartWithoutEdition (workflow_properties properties) {
           if (!properties.isSet("")) Util.getDefaultPgrmValues(properties, true);
        }

    /*
    * Output Parsing
    */

    @Override
    public void post_parseOutput() {
        TextFile.saveFile(properties,output1,"miRdup","TextFile");
        //ArffFile.saveArffFile(properties,output2,"miRdup");
        Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"miRdup");
    }
}
