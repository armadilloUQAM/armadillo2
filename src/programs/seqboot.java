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

////////////////////////////////////////////////////////////////////////////////
///
/// Create a Thread to run seqboot
/// Note: Seqboot is Special since a filename shoud alwaqys be associated with it
/// Note: We rename the outfile to seqboot.hashcode
/// Etienne Lord 2009


import biologic.Alignment;
import biologic.Phylip;
import biologic.Phylip_Seqboot;
import biologic.Unknown;
import configuration.Config;
import configuration.Util;
import java.io.*;
import program.RunProgram;
import workflows.workflow_properties;


public class seqboot extends RunProgram {

    public String filename="";
    public String infile="";
    public String outfile="";
    Alignment multi;
    public static int mode=0;          //Default mode
    public static int replicate=100;   //Default replicate
    

    // seqboot mode
    public static final int modeBootstrap=0; //default
    public static final int modeJackKnife=1;
    public static final int modePermuteSpecies=2;
    public static final int modePermuteCharOrder=3;
    public static final int modePermuteSwithinSpecies=4;

    private boolean debug;

    /**
     *
     * Note: il n'y a pas présentement de test d'erreur
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant les séquences à partir desquelles l'arbre est généré
     */
    public seqboot(workflow_properties properties) { 
        super(properties);        
       execute();
    }

    @Override
    public String[] init_createCommandLine() {
       String[] com=new String[11];
       for (int i=0; i<com.length;i++) com[i]="";
       com[0]="cmd.exe";
       com[1]="/C";
       com[2]=properties.getExecutable();
       com[3]="<seqboot.params"; //Contient : 0 Y
       return com;
    }



    @Override
    public boolean init_checkRequirements() {
        int alignment_id=properties.getInputID("input_alignment_id");
        if (alignment_id==0) {
            setStatus(status_BadRequirements,"No Aligment_id found");
            return false;
        }
        return true;
    }



    @Override
    public void init_createInput() {
        int alignment_id=properties.getInputID("input_alignment_id");
        //--Delete Infile and Outfile
        suredelete();
        multi=new Alignment(alignment_id);
        multi.outputPhylipInterleaveWithSequenceID("infile");
        //--WE don't resave the input - Etienne
        //        multi.saveToDatabase();
        //        addInput(multi);
        createConfigFile("seqboot.params");
    }


    @Override
    public void post_parseOutput() {
        Phylip_Seqboot unknown=new Phylip_Seqboot("outfile");        
        unknown.setRunProgram_id(super.getId());       
        unknown.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
        unknown.setNote("Generated at "+Util.returnCurrentDateAndTime());
        unknown.saveToDatabase();        
        addOutput(unknown);
        
        if (unknown.getId()!=0) {
            properties.put("output_Phylip_Seqboot_id", unknown.getId());
        }
        
        Util.deleteFile("seqboot.params");
        Util.deleteFile("outfile");
        Util.deleteFile("infile");
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
            pw.println("0"); //Default option
            //Default is bootstrap and we do nothing
            int type=(properties.isSet("type")?properties.getInt("type"):0);
            if (properties.getInt("type")>0) {
                for (int i=0; i<properties.getInt("type");i++) pw.println("D");
            }  
            int mode=(properties.isSet("mode")?properties.getInt("mode"):properties.getInt("default_mode"));
            if (properties.getInt("mode")>0) {
                for (int i=0; i<properties.getInt("mode");i++) pw.println("J");
            }   
            //Output the replicate
            pw.println("R");
            pw.println(properties.isSet("replicate")?properties.getInt("replicate"):properties.getInt("default_replicate"));
            //We run and we output the seed
            pw.println("Y");
            int seed=(properties.isSet("seed")?properties.getInt("seed"):properties.getInt("default_seed"));
            if (seed==0) seed=4533;
            if (seed%2==0) seed+=1;
            pw.println(seed);
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
        final seqboot other = (seqboot) obj;
        if ((this.filename == null) ? (other.filename != null) : !this.filename.equals(other.filename)) {
            return false;
        }
        return true;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    private String uniqueInfileOutfile(String filename) {
        if (infile.equals("")) this.infile=filename+this.hashCode();
        if (outfile.equals("")) this.outfile=filename+"outfile"+this.hashCode();
        return infile;
    }



}
