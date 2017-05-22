package programs;

import biologic.Alignment;
import biologic.FastaFile;
import biologic.FastqFile;
import biologic.Genome;
import biologic.MultipleSequences;
import biologic.Results;
import biologic.Sequence;
import biologic.Text;
import biologic.HTML;
import biologic.TextFile;
import configuration.Util;
import java.io.File;
import java.util.Vector;
import program.RunProgram;
import static program.RunProgram.df;
import static program.RunProgram.status_error;
import workflows.workflow_properties;


/**
 * @author Jérémy Goimard
 * @date Juin 2015
 */
public class FastQC extends RunProgram{
    private String filename="";
    private String infile="infile";
    private String outfile="";
    private String outdir = "."+File.separator+"results"+File.separator+"FastQC"+File.separator;
        
    public FastQC(workflow_properties properties) {
       //this.properties=properties;
        super(properties);
        execute();
    }

    @Override
    public boolean init_checkRequirements() {
        Util.CreateDir (outdir);
        //properties.put("Directory",outdir);
        int fastqfile_id=properties.getInputID("FastqFile");
        if (fastqfile_id==0) {
            setStatus(this.status_BadRequirements,"No file found in input");
            return false;
        }
        return true;
    }
    
    public String[] init_createCommandLine() {
        // ligne de commande fonctionnelle mais pas dynamique
        int fastqfile_id=properties.getInputID("FastqFile");
        FastqFile file=new FastqFile(fastqfile_id);
        
        if (properties.isSet("Directory")) {
            outdir = properties.get("Directory");
        }
        
        //Recherche du nom sans extension pour le post_parseOutput
        outfile = file.getName();
        
        int pos = outfile.lastIndexOf(".");
        if (pos > 0) {
            outfile = outfile.substring(0, pos);
        }
        
        // création de la commande
        String[] com=new String[30];
        for (int i=0; i<com.length;i++) com[i]="";
        int index=3;
        com[0]="cmd.exe";
        com[1]="/C";
        com[2]=properties.getExecutable();
        com[3]=file.getFile();
        com[4]="--outdir="+outdir;
        return com;
        
    }
     
    public void post_parseOutput() {
        HTML.saveFile(properties,outdir, "FastQC", "FastQC");
     }  
}
