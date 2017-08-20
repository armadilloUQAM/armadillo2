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
public class CramCraiFile extends Text implements Serializable{
    
    public CramCraiFile()                  {super();}
    public CramCraiFile(int id)            {super(id);}
    public CramCraiFile(String filename)   {super(filename);}
    
    public String[] getExtensionTab() {
        String[] t = {".cram.crai"};
        return t;
    }
    
    @Override
    public String getBiologicType() {
        return "CramCraiFile";
    }
}
