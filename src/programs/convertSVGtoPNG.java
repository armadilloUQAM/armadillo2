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

//////////////////////////////////////////////////////////////////////////////////////////////33
///
/// Create a Thread to launch scriptree
///
/// Etienne Lord 2010


import biologic.ImageFile;
import biologic.Results;
import biologic.TextFile;
import biologic.Tree;
import biologic.Unknown;
import configuration.Config;
import configuration.Util;
import java.io.*;
import program.RunProgram;
import workflows.workflow_properties;

public class convertSVGtoPNG extends RunProgram {
    private String infile="infile";               //Unique infile : Must be deleted at the end
    private String outfile="outfile";

    //Outgroup sequence
    public static int outgroup=0;
    // Debug and programming variables
    public static boolean debug=true;
    
    public convertSVGtoPNG(){};

    public convertSVGtoPNG (workflow_properties properties) {
        super(properties);
        execute();
    }

    public ImageFile convert(String filename) {
        //--Load the scriptTree file...        
        properties.load("convertSVGtoPNG.properties", config.propertiesPath());
        infile=filename;
        outfile=filename+".png";        
        //init_createInput();        
        super.commandline=init_createCommandLine();

        try {            
            super.do_run();
            post_parseOutput();
            ImageFile img=new ImageFile(properties.getInt("output_imagefile_id"));
            return img;
        } catch(Exception e) {e.printStackTrace();System.out.println("Error");}
        return new ImageFile();
    }

   ////////////////////////////////////////////////////////////////////////////
   // FUNCTIONS

    @Override
    public boolean init_checkRequirements() {
          int textfile_id=properties.getInputID("input_textfile_id");
            if (textfile_id==0) {
             textfile_id=properties.getInputID("input_imagefile_id");
            }
        if (textfile_id==0) {
            setStatus(status_BadRequirements, "No input found");
            return false;
        }
         TextFile t=new TextFile(textfile_id);
       //--Put filename in the correct properties
       File f=new File(t.getFilename());
      if (!f.exists()) {
          setStatus(status_BadRequirements, "Input file "+t.getFilename()+" not found");
          return false;
      }
        infile=f.getAbsolutePath();
        outfile=f.getAbsolutePath()+".png";
        return true;
    }


    @Override
    public void init_createInput() {
         //--Pre-Run initialization       
     
      
       
       
    }

    @Override
    public String[] init_createCommandLine() {
        String[] com=new String[15];
        for (int i=0; i<com.length;i++) com[i]="";
        int index=7;
       //--This is good for ImageMagick 
//        com[0]="cmd.exe";
//        com[1]="/C";
//        com[2]=properties.getExecutable();         
//        //-density 1200 -resize 200x200
//        com[3]="-density";
//        com[4]="1200";
//        com[5]="-resize";
//        com[6]="200x200";
//        if (properties.isSet("no-background")) {
//            com[index++]="-background";
//            com[index++]="none";
//        }                
//        com[index++]=infile;                
//        com[index++]=outfile;
        
        com[0]="java";
        com[1]="-jar";
        com[2]=properties.getExecutable();         
        //-density 1200 -resize 200x200
        //--Note: rendering big tree
        //--Note: lest then 10 species -> 800x800
        
        com[3]="-w";
        com[4]="1000";
        com[5]="-h";
        com[6]="1000";        
        com[index++]=infile;                
        com[index++]="-d";
        com[index++]=outfile;
        
        
        return com;
    }

    @Override
    public void post_parseOutput() {
        File fi=new File(outfile);
        if (fi.exists()) {
            ImageFile.saveFile(properties, outfile, "PNG file created from SVG"+infile,"ImageFile");
        } else System.out.println("WARNING : Image file "+outfile+" not found");
    }

 


    @Override
    public int hashCode() {
         //long time=System.currentTimeMillis();
         //return BigInteger.valueOf(time).intValue();
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
        final convertSVGtoPNG other = (convertSVGtoPNG) obj;
        return true;
    }

    

}
