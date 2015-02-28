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

import biologic.Blast;
import biologic.MultipleSequences;
import biologic.Sequence;
import biologic.seqclasses.blastParser;
import configuration.Config;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;

/**
 *
 * @author Etienne Lord
 * @since Novembre 2009
 */
public class ExtractBlast extends RunProgram {

    public static String path_executable="executable\\blast-2.2.22+\\bin\\blastdbcmd.exe";
    public static String filename="";
    public static String outfile="out.txt";
    public static String path_db="c://flu//data//InfluenzaBefore2008AA"; //Exemple
    public static boolean NoThread=true;
    public static boolean debug=true;
    public static String strand="plus"; // or "minus"
    public static int start=0;
    public static int end=40;
    public static String format="";
    //Example format (fmt): "7 qacc sacc evalue bitscore length gaps gapopen";
    
    

    //  num_descriptions <Integer, >=0>
    // Number of database sequences to show one-line descriptions for
    // Default = `500'
    //num_alignments <Integer, >=0>
    // Number of database sequences to show alignments for
    // Default = `250'
    

   Sequence sequence;
   
    public ExtractBlast(workflow_properties properties) {
        super(properties);
       
        //--Run thread
     execute();
    }

//    public ExtractBlast(Sequence sequence) {
//       this.sequence=sequence;
//        Config.log(sequence.getAccession()+" "+sequence.getName());
//        this.properties.setName("ExtractBlast");
//        this.properties.put("strand", strand);
//        if (start!=0||end!=0) {
//            if (end<1) end=sequence.getLen();
//            if (start<1) start=0;
//            properties.put("start", start);
//            properties.put("end", end);
//        }
//        this.properties.put("executable", path_executable);
//        this.properties.put("NoThread", NoThread);
//        this.properties.put("debug", debug);
//       execute();
//    }
//
//     public ExtractBlast(Sequence sequence, String out_filename) {
//        outfile=out_filename;
//       this.sequence=sequence;
//        Config.log(sequence.getAccession()+" "+sequence.getName());
//        this.properties.setName("ExtractBlast");
//        this.properties.put("strand", strand);
//        if (start!=0||end!=0) {
//            if (end<1) end=sequence.getLen();
//            if (start<1) start=0;
//            properties.put("start", start);
//            properties.put("end", end);
//        }
//        this.properties.put("executable", path_executable);
//        this.properties.put("NoThread", NoThread);
//        this.properties.put("debug", debug);
//        execute();
//    }




    @Override
    public boolean init_checkRequirements() {
        int sequence_id=properties.getInputID("input_sequence_id");
        int multiplesequences_id=properties.getInputID("input_multiplesequences_id");
        int blastbd_id=properties.getInputID("BlastDB");
        int blasthitlist_id=properties.getInputID("BlastHitList");
        if (blasthitlist_id==0) {
            return false;
        }

        return true;
    }



    @Override
    public void init_createInput() {
        int blasthitlist_id=properties.getInputID("BlastHitList");
        Blast bhl=new Blast(blasthitlist_id);
        bhl.Output("outfile.txt");
        blastParser bl=new blastParser();
        bl.load("outfile.txt");
        
    }




    @Override
    public String[] init_createCommandLine() {

       String[] com=new String[20];
       for (int i=0; i<com.length;i++) com[i]="";
       com[0]="cmd.exe";
       com[1]="/C";
       com[2]=properties.get("executable");
       com[3]="-entry";
       com[4]=sequence.getAccession();
       com[5]="-db";
       com[6]=path_db;
       com[7]="-out";
       com[8]=outfile;
       com[9]="-strand";
       com[10]=properties.get("strand");
       if (properties.isSet("start")&&properties.isSet("end")) {
            com[11]="-range";
            com[12]=""+properties.getInt("start")+"-"+properties.getInt("end");
       }

       if (!format.isEmpty()) {
         com[14]="-outfmt";
         com[15]="\""+format+"\"";
       }
       if (debug) Config.log(Util.toString(com));
       return com;
    }

   
}
