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
/// Create a Thread to run  Varna
/// 
/// Etienne Lord December 2011

import biologic.Alignment;
import biologic.Results;
import configuration.Util;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import program.RunProgram;
import workflows.workflow_properties;


public class Varna extends RunProgram {
    private String infile="infile";               //Unique infile : Must be deleted at the end
    private String outfile="outfile";             //Unique outfile : Must be deleted at the end

    // Debug and programming variables
    public static boolean debug=true;
    // For the Thread version
    private int status=0;           //status code
    String path="";
   
    /**
     *
     * Note: il n'y a pas présentement de test d'erreur
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant les séquences à partir desquelles l'arbre est généré
     */
    public Varna(workflow_properties properties) {        
        this.properties=properties;        
        execute();
    }

    @Override
    public boolean init_checkRequirements() {
        //--Check if input contains alignment
        if (properties.getInputID("alignment")==0) {
            setStatus(status_BadRequirements,"Error: Unable to find alignment.");
            //msg(properties.getProperties());
            return false;
        }
        File f=new File(properties.getExecutable());
        path=f.getAbsolutePath().substring(0,f.getAbsolutePath().lastIndexOf(File.separator));
        Alignment a=new Alignment(properties.getInputID("alignment"));
        a.outputPhylipInterleaveWithSequenceID(path+File.separator+infile,"");
        addInput(a);
        return true;
    }

    @Override
    public void init_createInput() {        
       properties.put("Status", status_idle);
       properties.put("debug", debug);       
    }


    /**
     * $proj_png="java -cp usagers/VARNAv3-7.jar fr.orsay.lri.varna.applications.VARNAcmd -sequenceDBN \"$precursor_seq\" -structureDBN \"$precursor_struct\" -highlightRegion \"$mirna_start-$mirna_end:fill=#808CF0;$mirna_star_start-$mirna_star_end:fill=#626262\" -algorithm naview -o $rendering_image_png >> $tmp";
     * @return 
     */
    @Override
    public String[] init_createCommandLine() {
        String[] com=new String[30];        
        for (int i=0; i<com.length;i++) com[i]="";
        int index=6;
           com[0]="java"; //--Minus the .exe
           com[1]="-cp";
           File f=new File(properties.getExecutable());
           com[2]=f.getName();
           com[3]="fr.orsay.lri.varna.applications.VARNAcmd";           
           if (properties.isSet("sequenceDBN")) {
               com[6]=properties.get("-sequenceDBN");
           } else com[6]="3";
//           if (properties.isSet("tree_building")) {
//               com[index++]="-t";
//               com[index++]=properties.get("tree_building");
//           }
//           if (properties.getBoolean("test_unequals")) {
//                com[index++]="-f";
//           }
//           if (properties.getBoolean("test_invariable")) {
//                com[index++]="-i";
//           }
//           if (properties.getBoolean("test_variation")) {
//                com[index++]="-g";
//                com[index++]="8";
//           }
//           if (properties.getBoolean("AIC")) {
//                com[index++]="-AIC";
//           }
//           if (properties.getBoolean("AICi")) {
//                com[index++]="-AICi";
//           }
//          if (properties.getBoolean("BIC")) {
//                com[index++]="-BIC";
//           }
//           if (properties.getBoolean("DT")) {
//                com[index++]="-DT";
//           }
//           if (properties.getBoolean("dLTR")) {
//                com[index++]="-dLTR";
//           }
//           if (properties.getBoolean("hLRT")) {
//                com[index++]="-hLRT";
//           }
        return com;
    }

   

    @Override
    public void post_parseOutput() {
            loadModelTest();
            //--CheckPoint
            if (!properties.getBoolean(debug)) {
               Util.deleteFile(infile);
               Util.deleteFile(outfile);             
            }
    }

    /**
      * This load the result vector
      */
     public void loadModelTest() {
       
    }

//     @Override
//      public boolean do_run() throws Exception {
//           //--Run the thread and catch stdout and stderr
//           setStatus(status_running, "\tRunning program...");
//           //--Linux?
//           ProcessBuilder pb=new ProcessBuilder(commandline);
//           pb.directory(new File(path));
//           p = pb.start();
//           InputStreamThread stderr = new InputStreamThread(p.getErrorStream());
//           InputStreamThread  stdout = new InputStreamThread(p.getInputStream());
//           int exitvalue=p.waitFor();
//           properties.put("ExitValue", exitvalue);
//           msg("\tProgram Exit Value: "+getExitVal());
//
//           return true;
//     }
 
}
