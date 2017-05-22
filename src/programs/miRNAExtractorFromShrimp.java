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

////////////////////////////////////////////////////////////////////////////////
///
/// Create a Thread to run  mirnatools - miRNAExtractorFromShrimp
///
/// Etienne Lord 2010

import biologic.Alignment;
import biologic.FastaFile;
import biologic.MultipleSequences;
import biologic.Results;
import biologic.SOLIDFile;
import biologic.Sequence;
import biologic.Text;
import biologic.TextFile;
import biologic.Tree;
import configuration.Util;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;


public class miRNAExtractorFromShrimp extends RunProgram {
    
    
    ////////////////////////////////////////////////////////////////////////////
    /// Input / Output
    
    public miRNAExtractorFromShrimp(workflow_properties properties) {
        super(properties);
        execute();
    }
    
    @Override
    public boolean init_checkRequirements() {
        Vector<Integer>fastafile_idUP=properties.getInputID("Fasta", PortInputUP);
        Vector<Integer>fastafile_idDOWN=properties.getInputID("Fasta", PortInputDOWN);
        if (fastafile_idUP.size()==0) {
            setStatus(this.status_BadRequirements,"No Contig fasta file found in input");
            return false;
        }
        if (fastafile_idDOWN.size()==0) {
            setStatus(this.status_BadRequirements,"No SHRIMP fasta output file found in input");
            return false;
        }
        return true;
    }
    
    @Override
    public void init_createInput() {
        
    }
    
    @Override
    public String[] init_createCommandLine() {
        int textfile_id=properties.getInputID("TextFile");
        TextFile text=new TextFile(textfile_id);
        
        String[] com=new String[20];
        for (int i=0; i<com.length;i++) com[i]="";
        int index=7;
        //---P reads INFILE > OUTFILE
        com[0]="java";
        com[1]="-Xmx256m";
//          com[2]="lib";
        com[2]="-jar";
        com[3]="executable/mirna/mirna.jar";
        com[4]="-P";
        com[5]=text.getFile();
        com[6]=text.getFile()+".shrimp.fasta";
        return com;
    }
    
    @Override
    public void post_parseOutput() {
        int textfile_id=properties.getInputID("TextFile");
        TextFile tex=new TextFile(textfile_id);
        FastaFile.saveFile(properties,tex.getFile()+".shrimp.fasta","Shrimp To Fasta","FastaFile");
    }
    
    @Override
    public int hashCode() {
        return Util.returnCount();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final muscle other = (muscle) obj;
        return true;
    }
}
