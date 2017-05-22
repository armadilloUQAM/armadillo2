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
/// Create a Thread to run protTest
///
/// Etienne Lord 2010

import biologic.Alignment;
import biologic.Results;
import configuration.Util;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import program.RunProgram;
import workflows.workflow_properties;


public class jModelTest extends RunProgram {
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
    public jModelTest(workflow_properties properties) {        
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


    @Override
    public String[] init_createCommandLine() {
        String[] com=new String[30];
        
        for (int i=0; i<com.length;i++) com[i]="";
        int index=7;
           com[0]="java"; //--Minus the .exe
           com[1]="-jar";
           File f=new File(properties.getExecutable());
           com[2]=f.getName();
           com[3]="-d";
           com[4]=infile;
           com[5]="-s";
           if (properties.isSet("schemes")) {
               com[6]=properties.get("schemes");
           } else com[6]="3";
           if (properties.isSet("tree_building")) {
               com[index++]="-t";
               com[index++]=properties.get("tree_building");
           }
           if (properties.getBoolean("test_unequals")) {
                com[index++]="-f";
           }
           if (properties.getBoolean("test_invariable")) {
                com[index++]="-i";
           }
           if (properties.getBoolean("test_variation")) {
                com[index++]="-g";
                com[index++]="8";
           }
           if (properties.getBoolean("AIC")) {
                com[index++]="-AIC";
           }
           if (properties.getBoolean("AICi")) {
                com[index++]="-AICi";
           }
          if (properties.getBoolean("BIC")) {
                com[index++]="-BIC";
           }
           if (properties.getBoolean("DT")) {
                com[index++]="-DT";
           }
           if (properties.getBoolean("dLTR")) {
                com[index++]="-dLTR";
           }
           if (properties.getBoolean("hLRT")) {
                com[index++]="-hLRT";
           }
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
        Pattern p_model=Pattern.compile("Model.=.(.*)");
        Pattern p_partition=Pattern.compile("partition.=.(.*)");
        Pattern p_lnL=Pattern.compile("lnL.=.(.*)");
        Pattern p_K=Pattern.compile("K.=.(.*)");
        Pattern p_kappa=Pattern.compile("kappa.=.(.*)");
        Pattern p_AIC=Pattern.compile("AIC.=.(.*)");
        //-- Model = K80
        //   partition = 010010
        //   -lnL = 1347.6976
        //   K = 13
        //   kappa = 3.0602 (ti/tv = 1.5301)
        String model="";
        String partition="";
        String lnL="";
        String K="";
        String kappa="";
        String AIC="";
        StringBuilder st=new StringBuilder();
        st.append("model\tpartition\t\tlnL\tAIC\tK\tkappa\n");

        for (String s:this.getOutputTXT()) {
            Matcher m1=p_model.matcher(s);
            Matcher m2=p_partition.matcher(s);
            Matcher m3=p_lnL.matcher(s);
            Matcher m4=p_K.matcher(s);
            Matcher m5=p_kappa.matcher(s);
            Matcher m6=p_AIC.matcher(s);
            if (m1.find()) {
                if (!model.isEmpty()) {
                     st.append(model+"\t"+partition+"\t\t"+lnL+"\t"+AIC+"\t"+K+"\t"+kappa+"\n");
                     model="";
                     partition="";
                     lnL="";
                     K="";
                     kappa="";
                     AIC="";
                }
                model=m1.group(1);
            }
            if (m2.find()) partition=m2.group(1);
            if (m3.find()) lnL=m3.group(1);
            if (m4.find()) K=m4.group(1);
            if (m5.find()) kappa=m5.group(1);
             if (m6.find()) AIC=m6.group(1);
        }
        //st.append("\n#Note: Best model is the lowest lnL");
        Results r=new Results();
        r.setText(st.toString());
        r.setName("jModelTest results "+Util.returnCurrentDateAndTime());
        r.setRunProgram_id(this.getId());
        r.setNote("jModelTest results created at "+Util.returnCurrentDateAndTime());
        r.saveToDatabase();
        properties.put("output_results_id", r.getId());
    }

     @Override
      public boolean do_run() throws Exception {
           //--Run the thread and catch stdout and stderr
           setStatus(status_running, "\tRunning program...");
           //--Linux?
           ProcessBuilder pb=new ProcessBuilder(commandline);
           pb.directory(new File(path));
           p = pb.start();
           InputStreamThread stderr = new InputStreamThread(p.getErrorStream());
           InputStreamThread  stdout = new InputStreamThread(p.getInputStream());
           int exitvalue=p.waitFor();
           properties.put("ExitValue", exitvalue);
           msg("\tProgram Exit Value: "+getExitVal());

           return true;
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
        return true;
    }
  

}
