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

//
//
//package programs;
//
///**
// *
// * @author Etienne Lord
// * @sine Mars 2009
// */
//
//
// import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.util.Vector;
///**
// *
// * @author Etienne
// */
//public class sbinferIndels {
//   private Vector<String> output=new Vector<String>();
//   private int exitVal=0;
//   private boolean done=false;
//   private String pathTosbInferIndels="";
//   private String pathToExecutable="";
//
//    /**
//     *
//     */
//    public sbinferIndels(Config config, sequence seq, Tree rtree) {
//        pathTosbInferIndels=config.getPathToSbInferIndels();
//        pathToExecutable=config.getPathToExecutable();
//        //create infile.seq and infile.tre
//        Config.log("Computing sbinferIndels for "+seq.getSeqAncestrale()+" ");
//        //
//        seq.outputFasta("infile.seq");
//        //
//        rtree.outputNewick("infile.tre", 0); //On prend le premier arbre
//        //Note: a tree file must have been generated in infile.tre!!!
//        runthread();
//        while (!done){}
//        deleteFile("infile.tree");
//        deleteFile("infile.seq");
//        Config.log("done.");
//
//    }
//
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
//                       com[2]=pathTosbInferIndels;
//                       com[3]="infile.seq";
//                       com[4]="infile.tre";
//                       com[5]="out.pres";
//                       com[6]="out.presConf";
//                       Process p = r.exec(com);
//                       InputStream stderr = p.getInputStream();
//                       InputStream stdoutput = p.getErrorStream();
//                       InputStreamReader isr = new InputStreamReader(stdoutput);
//                       BufferedReader br = new BufferedReader(isr);
//                       String line = null;
//                       while ( (line = br.readLine()) != null) {
//                         output.add(line);
//                       }
//                           exitVal = p.waitFor();
//                       if (exitVal==0) {
//                           done=true;
//                       } else {
//                           done=true;
//                           Config.log("Error code: "+exitVal);
//                       }
//                } catch (Exception ex) {
//                    Config.log("Error");
//                    ex.printStackTrace();
//                }
//             }
//        }.start();
//  } //End runthread
//
//////////////////////////////////////////////////////////////////////////////
//   // HELPER FUNCTIONS
//
//   /**
//    * Delete a file
//    * @param filename (to delete)
//    * @return true if success
//    */
//    public boolean deleteFile(String filename) {
//        try {
//            File outtree=new File(filename);
//            outtree.delete();
//        } catch(Exception e) {Config.log("Unable to delete file "+filename);return false;}
//        return true;
//    }
//
//    void PrintOutput() {
//         for (String stri:output) Config.log(stri);
//    }
//
//}
