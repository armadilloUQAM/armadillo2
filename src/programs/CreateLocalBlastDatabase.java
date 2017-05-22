/*
 *  Armadillo Workflow Platform v1.0
 *  A simple pipeline system for phylogenetic analysis
 *  
 *  Copyright (C) 2009-2011  Etienne Lord, Mickael Leclercq
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package programs;
import configuration.Config;
import biologic.BlastDB;
import biologic.MultipleSequences;
import biologic.Sequence;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;

/**
 *
 * @author Etienne
 */
public class CreateLocalBlastDatabase extends RunProgram {

    

    public CreateLocalBlastDatabase(workflow_properties properties) {
       super(properties);       
       execute();
    }       

    @Override
    public boolean init_checkRequirements() {
        int mid=properties.getInputID("MultipleSequences");
        if (mid==0) {
            setStatus(status_BadRequirements,"No MultipleSequences found...");
            return false;
        }
         if (!properties.isSet("outputfilename")) {
            setStatus(status_BadRequirements,"No output filename for blastDB found...");
             return false;
        }
        if (!properties.isSet("type")) {
            setStatus(status_BadRequirements,"No type (DNA, protein, ...) was set...");
            return false;
        }
        if (!properties.isSet("title")) {
            setStatus(status_BadRequirements,"Database title was not set...");
            return false;
        }
        return true;
    }



    @Override
    public void init_createInput() {
        int mid=properties.getInputID("MultipleSequences");
        MultipleSequences multi=new MultipleSequences(mid);
        //--if there is no Accession, create fake...
        for (Sequence s:multi.getSequences()) {
            if (s.getAccession().isEmpty()) {
                s.setAccession("AZ"+s.getId());
                s.setAccession_referee("gb");
            }
        }
        //--
        addInput(multi);
        multi.outputFasta("infile.fasta");
    }

    @Override
    public String[] init_createCommandLine() {
      String[] com=new String[20];
       for (int i=0; i<com.length;i++) com[i]="";
       //-in c:\flu\data\Influenza_prot_Before2008.fasta -dbtype prot -parse_seqids -title "Influenza_Before2008" -out InfluenzaBefore2008AA
       com[0]="cmd.exe";
       com[1]="/C";
       com[2]=properties.getExecutable();
       com[3]="-in";
       com[4]="\"infile.fasta\""; //--fasta_file
       com[5]="-dbtype";
       com[6]=properties.get("type");
       com[7]="-title";
       com[8]=properties.get("title");
       com[9]="-out";
       com[10]="\""+properties.get("outputfilename")+"\"";
       if (properties.getBoolean("parseSeqID")) com[11]="-parse_seqids";
        Config.log(Util.toString(com));
       return com;
    }

    @Override
    public void post_parseOutput() {
        BlastDB.saveFile(properties,properties.get("outputfilename"),"BlastDBEditor","BlastDB");
    }


}
