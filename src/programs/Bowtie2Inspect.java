/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package programs;

import biologic.Results;
import biologic.GenomeFile;
import biologic.TextFile;
import configuration.Util;
import java.util.Vector;
import program.RunProgram;
import static program.RunProgram.PortInputDOWN;
import static program.RunProgram.status_BadRequirements;
import workflows.workflow_properties;


/**
 *
 * @author Jérémy Goimard
 * @date Aout 2015
 *
 */
public class Bowtie2Inspect extends RunProgram {
    
    private String genomeFile ="";
    
    private final String[] inspectTab = {"I_a_box","I_n_box","I_s_box","I_v_box"};
    
    public Bowtie2Inspect(workflow_properties properties) {
        this.properties=properties;
        execute();
    }
    
    @Override
    public boolean init_checkRequirements() {
        int GenomeRef = properties.getInputID("GenomeFile");
        if (GenomeRef==0) {
            setStatus(status_BadRequirements,"No Genome found.");
            return false;
        }
        // TO BE COMPLETELY IDIOT PROOF NEED TO TEST EXISTENCE OF BOWTIE2's FILES
        return true;
    }
    
    @Override
    public String[] init_createCommandLine() {
        
        // Inputs
        Vector<Integer> GenomeRef = properties.getInputID("GenomeFile",PortInputDOWN);
        String optionsChoosed    = "";
        
        genomeFile = GenomeFile.getVectorFilePath(GenomeRef);
        if (genomeFile.matches("\\.\\d.bt2l?$")) {
            genomeFile = Util.getFileName(genomeFile);
            genomeFile = genomeFile.replaceAll("\\.\\d.bt2l?$","");
            genomeFile = genomeFile.replaceAll("\\.rev$","");
        }
        
        // Programme et options
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
        Results.saveResultsPgrmOutput(properties,this.getPgrmOutput(),"Bowtie2_Inspect");
        //TextFile.saveTextFile(properties,this.getPgrmOutput(),"Bowtie2_Inspect");
    }
    
}
