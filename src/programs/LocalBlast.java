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
import configuration.Config;
import biologic.BlastDB;
import biologic.Blast;
import biologic.BlastHit;
import biologic.MultipleSequences;
import biologic.Results;
import biologic.Sequence;
import biologic.seqclasses.blastParser;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;

/**
 *
 * @author Etienne Lord
 * @since Novembre 2009
 */
public class LocalBlast extends RunProgram {
    
    public static String path_Executable="";
    public static String filename="";
    public static String outfile="blast.results.txt";
    public static String path_db=""; //Exemple
    public static boolean NoThread=true;
    public static boolean debug=false;
    public static String format="";
    //Example format (fmt): "7 qacc sacc evalue bitscore length gaps gapopen";
    public static int num_description=200;
    public static int num_alignments=200;
    public static int word_size=1;
    public static int penalty=0;
    public static int reward=0;
    public static int gapextend=0;
    public static int gapopen=0;
    //  num_descriptions <Integer, >=0>
    // Number of database sequences to show one-line descriptions for
    // Default = `500'
    //num_alignments <Integer, >=0>
    // Number of database sequences to show alignments for
    // Default = `250'
    
    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES
    MultipleSequences multi=new MultipleSequences();
    BlastDB db;
    
    public LocalBlast(workflow_properties properties) {
        super(properties);
        
        //--Run thread
        execute();
    }
    
//    public LocalBlast(Sequence sequence) {
//        multi.add(sequence);
//        Config.log(sequence.getAccession()+" "+sequence.getName());
//        this.properties.setName("LocalBlast");
//        this.properties.put("Executable", path_Executable);
//        this.properties.put("NoThread", NoThread);
//        this.properties.put("debug", debug);
//        execute();
//    }
//
//     public LocalBlast(Sequence sequence, String out_filename) {
//        outfile=out_filename;
//        multi.add(sequence);
//        Config.log(sequence.getAccession()+" "+sequence.getName());
//        this.properties.setName("LocalBlast");
//        this.properties.put("Executable", path_Executable);
//        this.properties.put("NoThread", NoThread);
//        this.properties.put("debug", debug);
//        execute();
//    }
//
//    public LocalBlast(MultipleSequences multi) {
//        this.multi=multi;
//        //multi.saveToDatabase();
//        this.properties.setName("LocalBlast");
//        this.properties.put("Executable", path_Executable);
//        this.properties.put("NoThread", NoThread);
//        this.properties.put("debug", debug);
//        execute();
//    }
    
    
    
    @Override
    public boolean init_checkRequirements() {
        int sequence_id=properties.getInputID("sequence");
        int multiplesequences_id=properties.getInputID("multiplesequences");
        int blastdb_id=properties.getInputID("blastdb");
        if (blastdb_id==0) {
            setStatus(this.status_BadRequirements,"No BlastDB specified...");
            return false;
        }
        db=new BlastDB(blastdb_id);
//        if (!Util.FileExists(db.getBlastDB())) {
//            setStatus(this.status_BadRequirements,"Unable to find blastDB file "+db.getBlastDB());
//            return false;
//        }
        if (sequence_id==0&&multiplesequences_id==0) {
            setStatus(status_BadRequirements,"No sequence to blast");
            return false;
        }
        if (sequence_id!=0) {
            Sequence sequence=new Sequence(sequence_id);
            multi.add(sequence);
        } else {
            multi=new MultipleSequences(multiplesequences_id);
        }
        return true;
    }
    
    
    @Override
    public String[] init_createCommandLine() {
        // blastp -query query.txt -db influenzaAA -out blast_all.txt
        String[] com=new String[50];
        int index=9;
        for (int i=0; i<com.length;i++) com[i]="";
        com[0]="cmd.exe";
        com[1]="/C";
        com[2]=properties.getExecutable();
        //com[2]=properties.get("Executable");
        com[3]="-query";
        com[4]="query.txt";
        com[5]="-db";
        com[6]="\""+db.getFile()+"\"";
        com[7]="-out";
        com[8]="\""+properties.get("outfile")+"\"";
        if (properties.isSet("num_descriptions")) {
            com[index++]="-num_descriptions";
            com[index++]=properties.get("num_descriptions");
        }
        if (properties.isSet("num_alignments")) {
            com[index++]="-num_alignments";
            com[index++]=properties.get("num_alignments");
        }
        if (properties.isSet("task")) {
            com[index++]="-task";
            com[index++]=properties.get("task");
        }
        if (properties.isSet("strand")) {
            com[index++]="-strand";
            com[index++]=properties.get("strand");
        }
        if (properties.isSet("wordsize")) {
            com[index++]="-word_size";
            com[index++]=properties.get("wordsize");
        }
        if (properties.isSet("penalty")) {
            com[index++]="-penalty";
            com[index++]=properties.get("penalty");
        }
        if (properties.isSet("reward")) {
            com[index++]="-reward";
            com[index++]=properties.get("reward");
        }
        if (properties.isSet("gapextend")) {
            com[index++]="-gapextend";
            com[index++]=properties.get("gapextend");
        }
        if (properties.isSet("gapopen")) {
            com[index++]="-gapopen";
            com[index++]=properties.get("gapopen");
        }
        if (properties.getBoolean("ungapped")) {
            com[index++]="-ungapped";
        }
        if (properties.getBoolean("dust")) {
            com[index++]="-dust";
            com[index++]="yes";
        }
        if (properties.isSet("outfmt")) {
            if (properties.isSet("outfmt_string")) {
                com[index++]="-outfmt";
                com[index++]="\""+properties.get("outfmt")+" "+properties.get("outfmt_string")+"\"";
            } else {
                com[index++]="-outfmt";
                com[index++]="\""+properties.get("outfmt")+"\"";
            }
        }
        if (properties.isSet("percent_identity")) {
            com[index++]="-perc_identity";
            com[index++]=properties.get("percent_identity");
        }
        com[index++]="-export_search_strategy";
        com[index++]="strategy.txt";
        if (debug) Config.log(Util.toString(com));
        return com;
    }
    
    
    
    @Override
    public void init_createInput() {
        if (!properties.isSet("outfile")) {
            properties.put("outfile", outfile);
        }
        if (properties.get("outfile").isEmpty()) {
            properties.put("outfile", outfile);
        }
        multi.outputFasta("query.txt");
    }
    
    @Override
    public void post_parseOutput() {
        //--BlastHitList
        Blast bh=new Blast(properties.get("outfile"));
        bh.setName("Blast performed on "+Util.returnCurrentDateAndTime());
        bh.setNote("Blast performed on "+Util.returnCurrentDateAndTime());
        bh.saveToDatabase();
        this.addOutput(bh);
        properties.put("output_blast_id", bh.getId());
        //--BlastHit
        if (!properties.isSet("outfmt")) {
            try {
                blastParser bp=new blastParser();
                bp.load(properties.get("outfile"));
                BlastHit b2=new BlastHit();
                b2.setText(bp.toString());
                b2.setName("BlastHit performed on "+Util.returnCurrentDateAndTime());
                b2.setNote("Created on "+Util.returnCurrentDateAndTime());
                b2.saveToDatabase();
                properties.put("output_blasthit_id", b2.getId());
            } catch(Exception e) {}
        }
        //--Strategy
        Results text=new Results("strategy.txt");
        text.setName("Blast Search Strategy");
        text.setNote("Performed on "+Util.returnCurrentDateAndTime());
        text.setUnknownType("Blast");
        text.saveToDatabase();
        this.addOutput(text);
        properties.put("output_results_id",text.getId());
        //--CleanUP
        Util.deleteFile("query.txt");
        Util.deleteFile("stragety.txt");
        Util.deleteFile(properties.get("outfile"));
    }
    
    
    public void setOutfile(String filename) {
        this.outfile=filename;
    }
}
