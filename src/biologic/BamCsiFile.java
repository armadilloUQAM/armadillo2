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
 * @author JG 2017
 */
public class BamCsiFile extends Text implements Serializable{
    
    public BamCsiFile()                  {super();}
    public BamCsiFile(int id)            {super(id);}
    public BamCsiFile(String filename)   {super(filename);}
    
    public String[] getExtensionTab() {
        String[] t = {".bam.csi"};
        return t;
    }
    
    @Override
    public String getBiologicType() {
        return "BamCsiFile";
    }
}
