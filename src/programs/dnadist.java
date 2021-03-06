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
/// Create a Thread to run dnadist
///Note: DnaDist is Special since a filename shoud alwaqys be associated with it
/// Note: We rename the outfile to dnadist.hashcode
/// Etienne Lord 2009


import biologic.Alignment;
import biologic.Matrix;
import biologic.Phylip_Distance;
import biologic.Phylip_Seqboot;
import biologic.Sequence;
import configuration.Config;
import configuration.Util;
import java.io.*;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import program.RunProgram;
import workflows.workflow_properties;


public class dnadist extends RunProgram {

    
    private String infile="";               //Unique infile : Must be deleted at the end
    private String outfile="";              //Unique outfile: Must be deleted at the end

    public static int mode=0;          //Default mode F84
    public static int gamma=0;         //Gamma distribution: default No
    
//    private static boolean inference_dnadist=false;
//    private static boolean evolmodel_f84 = true;
//    private static boolean evolmodel_kimura = false;
//    private static boolean evolmodel_jukes_cantor = false;
//    private static boolean dnadist_nogamma = true;
//    private static boolean dnadist_gamma = false;
//    private static boolean dnadist_gamma_invariant = false;
//    private static String dnadist_trans_transv_ratio = "2.0";
//    private static boolean dnadist_gamma_coefficient = false;
//    private static String dnadist_gamma_coefficient_value = "4.0";
//    private static boolean dnadist_empirical_bases_choice = false;
//    private static String dnadist_empirical_baseA = "0.25";
//    private static String dnadist_empirical_baseC = "0.25";
//    private static String dnadist_empirical_baseG = "0.25";
//    private static String dnadist_empirical_baseT = "0.25";
    
    
    // seqboot mode
    public static final int modeF84=0; //default
    public static final int modeKimura=1;
    public static final int modeJukesCantor=2;
    public static final int modeLogDet=3;

    public static final int gammaNo=0;//default
    public static final int gammaYes=1;
    public static final int gammaYesAndInvatiant=2;

    // Debug and programming variables
    public static boolean debug=true;
    // For the Thread version
    private int status=0;           //status code
 
    public dnadist(workflow_properties properties) {
       super(properties);
       execute();
       
    }

    @Override
    public boolean init_checkRequirements() {
       int align_id=properties.getInputID("alignment");
       int seqboot_id=properties.getInputID("Phylip_Seqboot");

        if (align_id==0&&seqboot_id==0) {
            setStatus(this.status_BadRequirements,"No sequence found.");
            return false;
        }
        //--Test protein
       Alignment a=new Alignment(align_id);
       if (a.getId()>0&&!a.isDNA()) {
           setStatus(this.status_BadRequirements,"Warning. Sequences might not be nucleic acid.");
       }
       return true;
    }

    @Override
    public void init_createInput() {
        this.suredelete();
       int align_id=properties.getInputID("alignment");
       int seqboot_id=properties.getInputID("Phylip_Seqboot");
        if (align_id!=0) {
            Alignment align=new Alignment(align_id);
            align.outputPhylipInterleaveWithSequenceID("infile");
            addInput(align);
        }
        if (seqboot_id!=0) {
            Phylip_Seqboot align=new Phylip_Seqboot(seqboot_id);
            align.Output("infile");
            addInput(align);
        }
       this.createConfigFile("dnadist.params");
    }


    @Override
    public String[] init_createCommandLine() {
         String[] com=new String[11];
           for (int i=0; i<com.length;i++) com[i]="";
           com[0]="cmd.exe";
           com[1]="/C";
           com[2]=properties.getExecutable();
           com[3]="<"+"dnadist.params"; //Contient : 0 Y
           return com;
    }

    

    @Override
    public void post_parseOutput() {
        Phylip_Distance unknown=new Phylip_Distance("outfile");
        unknown.setRunProgram_id(super.getId());
        unknown.setNote("Generated at "+Util.returnCurrentDateAndTime());
        unknown.saveToDatabase();
        addOutput(unknown);
        if (unknown.getId()!=0) {
            properties.put("output_Phylip_Distance_id", unknown.getId());
        }
        Matrix matrix=new Matrix("outfile");
        matrix.setNote("Generated by DnaDist on "+Util.returnCurrentDateAndTime());
        matrix.setName("Generated by DnaDist on "+Util.returnCurrentDateAndTime());
        //--Replace name with correct sequence name...        
        matrix.ReplaceResultsWithSequenceName();
        matrix.saveToDatabase();
        addOutput(matrix);
        if (matrix.getId()!=0) {
            properties.put("output_matrix_id", matrix.getId());
        }
        if (!properties.getBoolean("debug")) {
            Util.deleteFile("dnadist.params");
            Util.deleteFile("outfile");
            Util.deleteFile("infile");
        }
    }

  
   ////////////////////////////////////////////////////////////////////////////
   // FUNCTIONS

    /**
     * Create a Phylip configurationFile [param] (Override this method to change param file)
     * @param filename
     * @return true if success!
     */
    public boolean createConfigFile(String filename) {
        try {
           
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            pw.println("0");
            if (properties.isSet("CustomPhylipCommand")) {
                pw.println(properties.get("CustomPhylipCommand"));
            } 
            if (properties.isSet("model")) {
                for (int i=0; i<properties.getInt("model");i++) pw.println("D");
            }
           if (properties.isSet("gamma")) {
               for (int i=0; i<properties.getInt("gamma");i++) pw.println("G");
           }
           if (properties.isSet("transversion")) {
                pw.println("T");
                pw.println(properties.getFloat("transversion"));
           }
            pw.println("Y");
            if (properties.isSet("gamma")&&properties.getInt("gamma")==1){
                //--This is the calculated CV for DnaDist (1/sqrt(a))
                pw.println((1/Math.sqrt(properties.getDouble("alpha_gamma_rate"))));
            }
            if (properties.isSet("gamma")&&properties.getInt("gamma")==2) {
                pw.println((1/Math.sqrt(properties.getDouble("alpha_gamma_rate"))));
                pw.println(properties.getDouble("gamma_invariant_site"));
            }
            pw.flush();
            pw.close();

        } catch (Exception e) {Config.log("Error in creating ConfigFile "+filename);return false;}
        return true;
    }

    
    

    private boolean suredelete() {
        try {
            File outtree=new File("outtree");
            File out=new File("outfile");
            int index=0;

                if (outtree.exists()||out.exists()) {
                    if (!outtree.delete()) if (debug) Config.log("outtree delete problem");
                    if (!out.delete()) if (debug) Config.log("outfile delete problem");
                }

        } catch(Exception e) {Config.log("Problem in suredelete()");return false;}
        return true;
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
        final dnadist other = (dnadist) obj;       
        return true;
    }

    private String uniqueInfileOutfile(String filename) {
        if (infile.equals("")) this.infile=filename+this.hashCode();
        if (outfile.equals("")) this.outfile=filename+"outfile"+this.hashCode();
        return infile;
    }

  

}
