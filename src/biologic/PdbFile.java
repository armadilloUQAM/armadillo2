/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package biologic;

import configuration.Util;
import java.io.File;
import java.io.Serializable;
import java.util.Vector;
import workflows.workflow_properties;

/**
 * @author JG 2016
 */

public class PdbFile extends Text implements Serializable{
    
    public PdbFile()                 {super();}
    public PdbFile(int id)           {super(id);}
    public PdbFile(String filename)  {super(filename);}

    public String[] getExtensionTab() {
        String[] t = {".pdb",".pdbseq",".pdbnuc",".pdbnucseq"};
        return t;
    }
    
    @Override
    public String getBiologicType() {
        return "PdbFile";
    }
}
