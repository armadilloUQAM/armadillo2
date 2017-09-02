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
/// Create a Thread to run phyloNet
///
/// Etienne Lord 2010

import biologic.Alignment;
import biologic.MultipleTrees;
import biologic.Results;
import biologic.Tree;
import configuration.Config;
import configuration.Util;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import program.RunProgram;
import workflows.workflow_properties;


public class PhyloNet extends RunProgram {
    private String infile="infile";               // Unique infile  : Must be deleted at the end
    private String outfile=Config.currentPath+File.separator+"outfile.phylonet";             // Unique outfile : Must be deleted at the end
    private File infileFile=new File(infile);
    // Debug and programming variables
    public static boolean debug=true;
    // For the Thread version    
    String path="";
   
    /**
     * 
     * Note: il n'y a pas présentement de test d'erreur
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant les séquences à partir desquelles l'arbre est généré
     */
    public PhyloNet(workflow_properties properties) {        
        this.properties=properties;        
        execute();
    }

    @Override
    public boolean init_checkRequirements() {
         Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);
        Vector<Integer>multipletreesDOWN=properties.getInputID("multipletrees",PortInputDOWN);
        Vector<Integer>treeUP=properties.getInputID("tree",PortInputUP);
          if (treeUP.size()==0) {
            setStatus(status_BadRequirements, "Error: We need a species tree...");
            return false;
        }
        if (treeDOWN.size()==0&&multipletreesDOWN.size()==0) {
            setStatus(status_BadRequirements, "Error: We need 2 input trees");
            return false;
        }
        return true;
    }

    @Override
    public void init_createInput() {        
         File f=new File(properties.getExecutable());
        path=f.getAbsolutePath().substring(0,f.getAbsolutePath().lastIndexOf(File.separator));

       try {
        PrintWriter util=new PrintWriter(new FileWriter(infileFile));
        Vector<Integer>MultipleTreesDOWN=properties.getInputID("multipletrees",PortInputDOWN);
        Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);
        Vector<Integer>treeUP=properties.getInputID("tree",PortInputUP);
        //--We assume that the tree are first
        for (int ids:treeUP) {
            Tree tree=new Tree(ids);
            addInput(tree);
            if (properties.getBoolean("abbreviate")) {
                util.println(tree.getTreeAbbreviate());
            } else util.println(tree.getTree());
        }
        for (int ids:treeDOWN) {
            Tree tree=new Tree(ids);
            addInput(tree);
             if (properties.getBoolean("abbreviate")) {
                util.println(tree.getTreeAbbreviate());
            } else util.println(tree.getTree());
        }
         for (int ids:MultipleTreesDOWN) {
            MultipleTrees trees=new  MultipleTrees(ids);
            addInput(trees);
            for (Tree tree:trees.getTree()) util.println(tree.getTree());
        }
         util.flush();
         util.close();
       } catch(Exception e) {setStatus(status_error,"Ùnable to create infile.");}
        //--
       setStatus(status_idle,"");
    }


    @Override
    public String[] init_createCommandLine() {
        String[] com=new String[30];        
        for (int i=0; i<com.length;i++) com[i]="";
        int index=3;
           com[0]="java";
           com[1]="-jar";
           File f=new File(properties.getExecutable());
           com[2]=f.getName();
           //==Program to run
           if (properties.isSet("program")) {
               //==riata-HGT
               if (properties.get("program").equals("riatahgt")) {
                    com[index++]="riatahgt";
                    com[index++]="-i";
                    com[index++]=infileFile.getAbsolutePath(); //==This is required for java                    
                    com[index++]="-o";
                    com[index++]=outfile;
                    com[index++]="-b";
               }
           }
        return com;
    }

    @Override
    public void post_parseOutput() {
        Pattern consensus_tree=Pattern.compile("Consensus network for this set of gene trees(.*)", Pattern.UNIX_LINES);
        if (Util.FileExists(outfile)) {
            Results text=new Results(outfile);
            addOutput(text);
            text.setName(properties.getName()+"_"+outfile);
            text.saveToDatabase();
            properties.put("output_results_id", text.getId());
//            Matcher m=consensus_tree.matcher(text.getText());
//            //System.out.println(text.getText());
//            if (m.find()) {
//                System.out.println("->"+m.group(1));
////                String treetext=m.group(1)+";";
////                Tree tree=new Tree();
////                tree.setName(properties.getName()+"_"+Util.returnCurrentDateAndTime());
////                tree.setNote("Created on "+Util.returnCurrentDateAndTime());
////                tree.setTree(treetext);
////                tree.saveToDatabase();
////                properties.put("output_tree_id", tree.getId());
//            }
        }
            if (!properties.getBoolean(debug)) {
               Util.deleteFile(infile);
               Util.deleteFile(outfile);             
            }
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
