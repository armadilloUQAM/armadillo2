/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package biologic;

import configuration.Util;
import java.io.Serializable;

/**
 *
 * @author Brisée-pas-morte
 */
public class FastqFile extends Text implements Serializable{
    
     public FastqFile() {super();}
    public FastqFile(int id) {super(id);}
    public FastqFile(String filename) {super(filename);}
     public String getBiologicType() {
        return "FastqFile";
    }
     public void setFastqFile(String filename) {
        this.setFilename(filename);
        this.setUnknownType("Fastq");
       //--Note: we have a properties in the Fastq file
        //  this.setText("Fastq : "+filename+"\nSelected on: "+Util.returnCurrentDateAndTime());
    }
    public String getFastqFile(){
        return this.getFilename();
    }

    public String getExtendedString() {
        return toString();
    }  
}
