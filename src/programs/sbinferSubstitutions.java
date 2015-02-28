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

import configuration.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;

/**
 *
 * @author Etienne
 */
public class sbinferSubstitutions extends RunProgram {
    

    public sbinferSubstitutions(workflow_properties properties) {
       this.properties=properties;
       execute();
    }

//    public sbinferSubstitutions(Config config, sequence seq, Tree rtree) {
//        pathTosbInferSubstitutions=config.getPathToSbInferSubstitutions();
//        pathToExecutable=config.getPathToExecutable();
//        // We assume that we have
//        // infile.seq infile.tre out.pres
//        Config.log("Computing sbinferSubstritutions for "+seq.getSeqAncestrale()+" ");
//        seq.outputFasta("infile.seq");
//        rtree.outputNewick("infile.tre", 0);
//        runthread();
//        while (!done){}
//        deleteFile("infile.seq");
//        deleteFile("infile.tre");
//        Config.log("done.");
//
//    }

//    public sbinferSubstitutions(Config config, sequence seq, Tree rtree, String filename) {
//        pathTosbInferSubstitutions=config.getPathToSbInferSubstitutions();
//        pathToExecutable=config.getPathToExecutable();
//        name=filename;
//        // We assume that we have
//        // infile.seq infile.tre out.pres
//        Config.log("Computing sbinferSubstritutions for "+seq.getSeqAncestrale()+" ");
//        seq.outputFasta("infile.seq");
//        rtree.outputNewick("infile.tre", 0);
//        runthread();
//        while (!done){}
//        deleteFile("infile.seq");
//        deleteFile("infile.tre");
//        Config.log("done.");
//
//    }

     /*public sbinferSubstitutions(Config config, sequence seq, randtree rtree, String ancestorCC_filename) {
        pathTosbInferSubstitutions=config.getPathToSbInferSubstitutions();
        pathToExecutable=config.getPathToExecutable();
        this.ancestorCC_filename=ancestorCC_filename;
        // We assume that we have
        // infile.seq infile.tre out.pres
        Config.log("Computing sbinferSubstitutions with AncestorCC prediction for "+seq.seqAncestrale+" ");
        seq.outputFasta("infile.seq");
        rtree.outputNewick("infile.tre", 0);
        runthread_with_ancestor();
        while (!done){}
        deleteFile("infile.seq");
        deleteFile("infile.tre");
        Config.log("done.");

    }*/



    @Override
    public boolean init_checkRequirements() {

         int alignment_id=properties.getInputID("input_alignment_id");
         int tree_id=properties.getInputID("input_tree_id");
         if (alignment_id==0) {
            setStatus(this.status_BadRequirements, "No Alignment found!");
            return false;
         }
        if (tree_id==0) {
            setStatus(this.status_BadRequirements, "No Tree found!");
            return false;
         }
        return true;
    }



    @Override
    public String[] init_createCommandLine() {
        //--This should run after AncestorCC to get the Ancestrale Sequence
         String[] com=new String[11];
          for (int i=0; i<com.length;i++) com[i]="";
           com[0]="cmd.exe";
           com[1]="/C";
           com[2]="executable\\sbInferSubstitutions.exe";
           // infile.seq infile.tre out.pres out.ancestors out.nucConf out.post
           // usagers/sbInferSubstitutions $sequenceFile $arbreFile $anc2 $anc $conf $prob >> $tmp";
          //  $anc                  = $rep."/outfile_ancestrale.fasta";	//= fichier d'ancetres (ancestorSub ou sbInferSubstitutions)
          //  $anc2                 = $rep."/outfile.seq.anc";			//= fichier de sequences ancestrale en sortie (ancestorCC) - v1.0

           com[3]="infile.seq";
           com[4]="infile.tre";           
           com[5]=properties.get("ancestral_filename")+".anc";
           com[6]=properties.get("ancestral_filename");
           com[7]=properties.get("ancestral_filename")+".nucConf_CC";
           com[8]=properties.get("ancestral_filename")+".post_CC";          
           return com;
    }

    
//     public void runthread() {
//        //Note requiert le fichier infile
//
//         new Thread(){
//
//            @Override
//             public void run() {
//                 try {
//                      Runtime r = Runtime.getRuntime();
//                       String[] com=new String[11];
//                       for (int i=0; i<com.length;i++) com[i]="";
//                       com[0]="cmd.exe";
//                       com[1]="/C";
//                       //com[2]=;
//                       // infile.seq infile.tre out.pres out.ancestors out.nucConf out.post
//                       com[3]="infile.seq";
//                       com[4]="infile.tre";
//                       com[5]="out.pres";
//                       com[6]=name+".ancestors";
//                       com[7]=name+".nucConf";
//                       com[8]=name+".post";
//                       Process p = r.exec(com);
//                       InputStream stderr = p.getInputStream();
//                       InputStream stdoutput = p.getErrorStream();
//                       InputStreamReader isr = new InputStreamReader(stdoutput);
//                       BufferedReader br = new BufferedReader(isr);
//                       String line = null;
//                       while ( (line = br.readLine()) != null) {
//                           output.add(line);
//                       }
//                           exitVal = p.waitFor();
//                       if (exitVal==0) {
//                           done=true;
//                       } else {
//                           done=true;
//                           Config.log("Error code: "+exitVal);
//                       }
//                       //done=true;
//                } catch (Exception ex) {
//                    Config.log("Error");
//                    ex.printStackTrace();
//                }
//             }
//        }.start();
//
//  }

    
 

}
