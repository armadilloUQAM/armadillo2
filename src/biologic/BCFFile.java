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
public class BamFile extends Text implements Serializable{
    
    public BamFile()                  {super();}
    public BamFile(int id)            {super(id);}
    public BamFile(String filename)   {super(filename);}
    
    public String[] getExtensionTab() {
        String[] t = {".bam"};
        return t;
    }
    
    @Override
    public String getBiologicType() {
        return "BamFile";
    }
}
