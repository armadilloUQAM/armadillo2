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

public class GenomeFile extends Text implements Serializable{
    
    public GenomeFile()                 {super();}
    public GenomeFile(int id)           {super(id);}
    public GenomeFile(String filename)  {super(filename);}

    public String[] getExtensionTab() {
        String[] t = {".edt",".gct",".res",".atr",".gff",".asn",".fna",".gbk",".gff",".gtf",".ebwt",".bt2"};
        return t;
    }
    
    @Override
    public String getBiologicType() {
        return "GenomeFile";
    }
}
