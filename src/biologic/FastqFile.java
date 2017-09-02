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

public class FastqFile extends Text implements Serializable{
    
    public FastqFile()                  {super();}
    public FastqFile(int id)            {super(id);}
    public FastqFile(String filename)   {super(filename);}
    
    public static String[] getFastqFileExtension() {
        String[] t = {".fastq",".fq"};
        return t;
    }
    
    public static int sameFastqFileName (String s1,String s2) {
        int b = 0;
        s1 = s1.replaceAll("_\\d$","");
        s2 = s2.replaceAll("_\\d$","");
        if (s2.equals(s1)) b=1;
        return b;
    }

    public static int goodFastqFileNumber (String s1,String s2) {
        int b = 0;
        int val1 = Integer.parseInt(s1.replaceAll(".*_(\\d)$","$1"));
        int val2 = Integer.parseInt(s2.replaceAll(".*_(\\d)$","$1"));
        if (val1==1 && val2==2) b=1;
        return b;
    }

    @Override
    public String getBiologicType() {
        return "FastqFile";
    }
}
