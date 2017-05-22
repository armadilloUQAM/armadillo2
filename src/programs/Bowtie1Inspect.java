/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package programs;

import biologic.GenomeFile;
import biologic.Results;
import biologic.Text;
import configuration.Util;
import java.io.File;
import java.util.Vector;
import program.RunProgram;
import static program.RunProgram.PortInputDOWN;
import static program.RunProgram.PortInputUP;
import static program.RunProgram.df;
import static program.RunProgram.status_BadRequirements;
import static program.RunProgram.status_error;
import workflows.workflow_properties;


/**
 *
 * @author Jérémy Goimard
 * @date Aout 2015
 *
 */
public class Bowtie1Inspect extends RunProgram {
    
    private String genomeFile ="";
    private String outputFile ="";
    
    private String[] inspectTab = {"I_a_box","I_n_box","I_s_box","I_v_box"};
    
    public Bowtie1Inspect(workflow_properties properties) {
        this.properties=properties;
        execute();
    }
    
    @Override
    public boolean init_checkRequirements() {
        Vector<Integer> GenomeRef = properties.getInputID("GenomeFile",PortInputDOWN);
        genomeFile = Util.getFileName(GenomeFile.getVectorFilePath(GenomeRef));
        genomeFile = genomeFile.replaceAll("\\.\\d.ebwt$","");
        genomeFile = genomeFile.replaceAll("\\.rev$","");

        if (GenomeRef.isEmpty()||genomeFile.equals("Unknown")) {
            setStatus(status_BadRequirements,"No Genome found.");
            return false;
        }
        return true;
    }
    
    @Override
    public String[] init_createCommandLine() {
        // Programme et options
        String optionsChoosed    = "";
        if (properties.get("I_AO_button").equals("true")) {
            optionsChoosed = Util.findOptions(inspectTab,properties);
        }
        
        String[] com = new String[30];
        for (int i=0; i<com.length;i++) com[i]="";
        
        com[0]="cmd.exe";
        com[1]="/C";
        com[2]=properties.getExecutable();
        com[3]=optionsChoosed;
        com[4]=genomeFile;
        return com;
    }
    
    /*
    * Output Parsing
    */
    @Override
    public void post_parseOutput() {
        Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"Bowtie1_Inspect");
    }
}
