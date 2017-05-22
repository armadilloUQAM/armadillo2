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
/// Create a Thread to run garli v2.0
///
/// Etienne Lord 2011

import biologic.Alignment;
import biologic.MultipleTrees;
import biologic.Results;
import biologic.Text;
import biologic.Tree;
import biologic.seqclasses.GeneticCode;
import biologic.seqclasses.Translator;
import configuration.Util;
import java.io.File;
import java.util.regex.Pattern;
import org.apache.commons.lang.SystemUtils;
import program.RunProgram;
import workflows.workflow_properties;


public class garli extends RunProgram {

    private String infile="infile.phylip";             //Unique infile : Must be deleted at the end   
    private String outfile="outfile.best.tre";              //Unique outfile: Must be deleted at the end
    boolean error=false;                               //Flag for error in the program
    String error_msg="";
   

    ////////////////////////////////////////////////////////////////////////////
    /// Input / Output

    private Alignment align;
    Translator translator=new Translator();
    GeneticCode code;

    public garli(workflow_properties properties) {
       super(properties);
       //--Set en end pattern beacuase the program expect a key to finish...
       Pattern end_pattern=Pattern.compile("Saving final tree", Pattern.CASE_INSENSITIVE);
       this.setNormalEndRegex(end_pattern);
       this.execute();
    }

    @Override
    public boolean init_checkRequirements() {
        int alignment_id=properties.getInputID("alignment");
        
        if (alignment_id==0) {
            setStatus(status_BadRequirements,"No sequence found.");
            return false;
        }
        
        //--Verification of garli options before run.
        if (!properties.isSet("ratematrix")) {
            setStatus(status_BadRequirements, "Please set the Rate Matrix in the Garli options.");
            return false;
        }
        if (!properties.isSet("ratehetmodel")) {
            setStatus(status_BadRequirements, "Please set the Rate heterogeneity Model in the Garli options.");
            return false;
        }
        //State frequency
        if (!properties.isSet("statefrequencies")) {
            setStatus(status_BadRequirements, "Please set the State frequency in the Garli options.");
            return false;
        }        
        return true;
    }

   
    
    @Override
    public void init_createInput() {
        
        
        ////////////////////////////////////////////////////////////////////////
        /// Delete all files...
        Util.deleteFile(infile);
        Util.deleteFile("outfile.best.tre");
        Util.deleteFile("outfile.best.all.tre");
        Util.deleteFile("outfile.log00.log");
        Util.deleteFile("outfile.screen.log");
        Util.deleteFile(outfile);
        
        ////////////////////////////////////////////////////////////////////////
        /// Create new configuration and input files...
                
         int alignment_id=properties.getInputID("input_alignment_id");         
         Alignment align=new Alignment(alignment_id);
         align.outputPhylip(infile);
         addInput(align);
        
            createGarliConf();
            Text text=new Text("garli.conf");
            text.setName("garli.conf");
            text.setNote("Created on "+Util.returnCurrentDateAndTime());
            text.saveToDatabase();
            properties.put("output_text_id", text.getId());
            addInput(text);
        
    }


    
// Commented out - March 2012 - Etienne Lord
    /**
     * Special version because the program will exit only when ask for a key...
     * @return
     * @throws Exception 
     */
//    @Override
//    public boolean do_run() throws Exception {
//     
//    
////           //--Run the thread and catch stdout and stderr
////           setStatus(status_running, "\tRunning program...");         
////           r = Runtime.getRuntime();
////           p = r.exec(commandline);
////           InputStreamThread stderr = new InputStreamThread(p.getErrorStream());
////           InputStreamThread  stdout = new InputStreamThread(p.getInputStream());
////           //--Wait for program termination
////           while(!isDone()) {}
//        
//        
//            //--Run the thread and catch stdout and stderr
//           setStatus(status_running, "\tRunning program...");          
//           System.out.println(Util.toString(commandline));
//           ProcessBuilder pb=new ProcessBuilder(commandline);
//           if (properties.isSet("RunningDirectory")) {
//               pb.directory(new File(properties.get("RunningDirectory")));
//           }
//           
//           r = Runtime.getRuntime();
//           //--Test August 2011 - For Mac OS X
//           if ((config.getBoolean("MacOSX")||SystemUtils.IS_OS_MAC_OSX)) {
//               //System.out.println("MacOSX"); 
//             if (properties.isSet("RuntimeMacOSX")) {
//             
//                 String execution_type=properties.get("RuntimeMacOSX");
//                 
//                 //--Default
//                 if (execution_type.startsWith("default")) {
//                     //? Not suppose to exists...
//                     p = pb.start();
//                 }
//                 
//                 //--Runtime (r.exec)
//                 if (execution_type.startsWith("runtime")) {
//                     System.out.println("Running by runtime..."); 
//                     //--IF MAC_OSX, group option if UseRuntimeMacOSX
//                     String cmdm="";
//                      for (int i=0; i<commandline.length;i++) {
//                          cmdm+=commandline[i]+" ";
//                      }
//                          commandline=new String[1];
//                          commandline[0]=cmdm;                                       
//                     p = r.exec(Util.toString(commandline));
//                 }
//                 
//                 //--Bash...
//                 if (execution_type.startsWith("bash (.sh)")) {
//                     //System.out.println("Running from bash...");
//                     //--Create a new bash file
//                     Util u = new Util("RunProgram.sh");
//                     u.println("#!/bin/sh");
//                     u.println("echo \"Executing by bash command: "+properties.getName()+"\"");
//                     u.println(Util.toString(commandline));
//                     //--Return the application error code
//                     u.println("exit $?");
//                     u.close();
//                     p=r.exec("sh RunProgram.sh");
//                  }
//                
//             
//                 
//               } //--End RuntimeMacOSX
//           } else {
//               p = pb.start();
//           }
//           
//           //pb.redirectErrorStream(true)
//           InputStreamThread stderr = new InputStreamThread(p.getErrorStream());
//           InputStreamThread  stdout = new InputStreamThread(p.getInputStream());
//                    
//           while(!isDone()) {}         
//          
//           properties.put("ExitValue", 0);
//           if (error) {              
//                   properties.put("ExitValue", -1);
//                   //this.setStatus(status_error, error_msg);                                     
//           }
//           msg("\tProgram Exit Value: "+getExitVal());
//           return true;
//     
//    }


     public void createGarliConf() {
        Util ctl=new Util();
        ctl.open("garli.conf");
        String conf=properties.get("garli.conf");
        //--Option - Note: order important
        //--General unmovable option (for now)
        //--See http://molecularevolution.org/molevolfiles/garli/Garli0.96cheatsheet.pdf
        //   and https://www.nescent.org/wg_garli/GARLI_Configuration_Settings
        
        ctl.println("[general]");
        ctl.println("datafname = "+infile);        
        ctl.println("ofprefix = outfile");
        ctl.println("streefname = stepwise");
        ctl.println("constraintfile = none");
        ctl.println("outputeachbettertopology = 0");
        ctl.println("outputcurrentbesttree = 0");
        ctl.println("enforcetermconditions = 1");//--Enforce ending conditions
        ctl.println("writecheckpoints = 0");
        ctl.println("restart = 0");
        ctl.println("refinestart = 1");
        ctl.println("outputphyliptree = 0");
        ctl.println("outputmostlyuselessfiles = 0");
        ctl.println("constraintfile = none");
        
        //--TO DO 
        //ctl.println("resampleproportion = 1.0");
        //--ctl.println("collapsebranches = 1"); //collapse zero branch length
        
        //--General setting      
        //--Note: some are hard coded here 
        if (properties.isSet("outgroup")) {
            ctl.println("outgroup = "+properties.get("outgroup"));
        }
        if (properties.isSet("bootstrapreps")) {
            ctl.println("bootstrapreps = "+properties.get("bootstrapreps"));
        }  
        if (properties.isSet("attachmentspertaxon")) {
            ctl.println("attachmentspertaxon = "+properties.get("attachmentspertaxon"));
        } else {
            ctl.println("attachmentspertaxon = 50");
        }
        if (properties.isSet("searchreps")) {
            ctl.println("searchreps = "+properties.get("searchreps") );
        }  else {            
            ctl.println("searchreps = 1");
        }      
        //--Ending condition
        if (properties.isSet("genthreshfortopoterm")) {
            ctl.println("genthreshfortopoterm = "+properties.get("genthreshfortopoterm") );
        } else {            
            ctl.println("genthreshfortopoterm = 10000");
        }
         if (properties.isSet("scorethreshforterm")) {
            ctl.println("scorethreshforterm = "+properties.get("scorethreshforterm") );
        } else {
             ctl.println("scorethreshforterm = 0.05");
        }
          if (properties.isSet("significanttopochange")) {
            ctl.println("significanttopochange = "+properties.get("significanttopochange") );
        }else {
             ctl.println("significanttopochange = 0.01");
        }
        //--Not in menu
        if (properties.isSet("randseed")) {
            ctl.println("randseed = "+properties.get("randseed"));
        } else ctl.println("randseed = -1");
        if (properties.isSet("logevery")) {
            ctl.println("logevery = "+properties.get("logevery"));
        } else ctl.println("logevery = 10");
          if (properties.isSet("saveevery")) {
            ctl.println("saveevery = "+properties.get("saveevery"));
        } else ctl.println("saveevery = 100");
        
        if (properties.isSet("availablememory")) {
            ctl.println("availablememory = "+properties.get("availablememory"));
        } else ctl.println("availablememory = 128");  
        
        
        
        
        
          //--Model (currently we support only one model)
        ctl.println("[model1]");
        if (properties.isSet("datatype")) {
            ctl.println("datatype = "+properties.get("datatype"));          
        } else {
            ctl.println("datatype = dna");          
        }
        if (properties.isSet("geneticcode")) {
            ctl.println("geneticcode = "+properties.get("geneticcode"));
        } else {
            ctl.println("geneticcode = standard");                    
        }
        ctl.println("ratematrix = "+properties.get("ratematrix"));
        ctl.println("statefrequencies = "+properties.get("statefrequencies"));
        if (properties.isSet("ratehetmodel")) {
            ctl.println("ratehetmodel = "+properties.get("ratehetmodel"));
        } else ctl.println("ratehetmodel = 4");
        ctl.println("numratecats = "+properties.get("numratecats")); 
        //--Master -- various default option are defined here (see website)
        ctl.println("[master]");
        if (properties.isSet("nindivs")) {
            ctl.println("nindivs = "+properties.get("nindivs"));          
        } else ctl.println("nindivs = 4");
        if (properties.isSet("holdover")) {            
            ctl.println("holdover = "+properties.get("holdover"));
        } else {
            ctl.println("holdover = 1");
        }
        if (properties.isSet("selectionintensity")) {
            ctl.println("selectionintensity = "+properties.get("selectionintensity")); 
        } else {
             ctl.println("selectionintensity = 0.5"); 
        }
        if (properties.isSet("holdoverpenalty")) {
            ctl.println("holdoverpenalty = "+properties.get("holdoverpenalty")); 
        } else ctl.println("holdoverpenalty = 0");
        if (properties.isSet("stopgen")) {
             ctl.println("stopgen = "+properties.get("stopgen"));
        } else ctl.println("stopgen = 5000000");
        if (properties.isSet("stoptime")) {
             ctl.println("stoptime = "+properties.get("stoptime"));
        } else ctl.println("stoptime = 5000000");
        if (properties.isSet("startoptprec")) {
             ctl.println("startoptprec = "+properties.get("startoptprec"));
        } else ctl.println("startoptprec = 0.5");
        if (properties.isSet("minoptprec")) {
             ctl.println("minoptprec = "+properties.get("minoptprec"));
        } else ctl.println("minoptprec =  0.01");
        if (properties.isSet("numberofprecreductions")) {
             ctl.println("numberofprecreductions = "+properties.get("numberofprecreductions"));
        } else ctl.println("numberofprecreductions =  10");
        if (properties.isSet("treerejectionthreshold")) {
             ctl.println("treerejectionthreshold = "+properties.get("treerejectionthreshold"));
        } else ctl.println("treerejectionthreshold =  50"); 
         
        //--Settings controlling the proportions of the mutation types
        if (properties.isSet("topoweight")) {
             ctl.println("topoweight = "+properties.get("topoweight"));
        } else ctl.println("topoweight = 1.0");
        if (properties.isSet("modweight")) {
             ctl.println("modweight = "+properties.get("modweight"));
        } else ctl.println("modweight = 0.05");
        if (properties.isSet("brlenweight")) {
             ctl.println("brlenweight = "+properties.get("brlenweight"));
        } else ctl.println("brlenweight = 0.2");
        if (properties.isSet("randnniweight")) {
             ctl.println("randnniweight = "+properties.get("randnniweight"));
        } else ctl.println("randnniweight = 0.1"); 
        if (properties.isSet("randsprweight")) {
             ctl.println("randsprweight = "+properties.get("randsprweight"));
        } else ctl.println("randsprweight = 0.3"); 
        if (properties.isSet("limsprweight")) {
             ctl.println("limsprweight = "+properties.get("limsprweight"));
        } else ctl.println("limsprweight = 0.6"); 
        if (properties.isSet("intervallength")) {
             ctl.println("intervallength = "+properties.get("intervallength"));
        } else ctl.println("intervallength = 100"); 
        
        if (properties.isSet("intervalstostore")) {
             ctl.println("intervalstostore = "+properties.get("intervalstostore"));
        } else ctl.println("intervalstostore = 5"); 
        if (properties.isSet("limsprrange")) {
             ctl.println("limsprrange = "+properties.get("limsprrange"));
        } else ctl.println("limsprrange = 6"); 
        if (properties.isSet("meanbrlenmuts")) {
             ctl.println("meanbrlenmuts = "+properties.get("meanbrlenmuts"));
        } else ctl.println("meanbrlenmuts  = 5"); 
        if (properties.isSet("gammashapebrlen")) {
             ctl.println("gammashapebrlen = "+properties.get("gammashapebrlen"));
        } else ctl.println("gammashapebrlen = 1000"); 
         if (properties.isSet("gammashapemodel")) {
             ctl.println("gammashapemodel = "+properties.get("gammashapemodel"));
        } else ctl.println("gammashapemodel = 1000"); 
        
         if (properties.isSet("uniqueswapbias")) {
             ctl.println("uniqueswapbias = "+properties.get("uniqueswapbias"));
        } else ctl.println("uniqueswapbias = 0.1"); 
          if (properties.isSet("distanceswapbias")) {
             ctl.println("distanceswapbias = "+properties.get("distanceswapbias"));
        } else ctl.println("distanceswapbias = 1.0"); 
         //ctl.println(conf);        
        ctl.close();
    }

     

    @Override
    public void msg(String msg) {
        if (msg.startsWith("ERROR")) {           
           error=true;
           error_msg=msg;
           setStatus(status_error,"Warning. Kill switch found.\nProgram will be automatically terminated.\n"); 
                  
        }                   
        super.msg(msg);
    }

     

    @Override
    public void post_parseOutput() {                
        if (Util.FileExists("outfile.best.tre")) {
            Tree tree=new Tree("outfile.best.tre");
            tree.setNote("Garli best tree created on "+Util.returnCurrentDateAndTime());
            tree.saveToDatabase();        
            addOutput(tree);
            properties.put("output_tree_id", tree.getId());        
        } else {
            System.out.println("Unable to find tree...");
        }
       
        if (Util.FileExists("outfile.best.all.tre")) {
            MultipleTrees multi=new MultipleTrees("outfile.best.all.tre");
            multi.setName("Garli all tree created on "+Util.returnCurrentDateAndTime());
            multi.setNote("Garli all tree created on "+Util.returnCurrentDateAndTime());
            multi.saveToDatabase();
            addOutput(multi);        
            properties.put("output_multipletrees_id", multi.getId());

        }
        if (Util.FileExists(outfile)) {
            Results text=new Results(outfile);
            text.setName("Garli outfile created on "+Util.returnCurrentDateAndTime());
            text.setNote("Garli outfile created on "+Util.returnCurrentDateAndTime());
            text.saveToDatabase();        
            properties.put("output_results_id", text.getId());
        }
        ////////////////////////////////////////////////////////////////////////
        /// Clean up
        Util.deleteFile(infile);
        Util.deleteFile("garli.conf");
        Util.deleteFile("outfile.best.tre");
        Util.deleteFile("outfile.best.all.tre");
        Util.deleteFile("outfile.log00.log");
        Util.deleteFile("outfile.screen.log");
        Util.deleteFile(outfile);
    }

    @Override
    public String[] init_createCommandLine() {
           String[] com=new String[20];
           for (int i=0; i<com.length;i++) com[i]="";         
           com[0]="cmd.exe";
           com[1]="/C";
           com[2]=properties.getExecutable();
           com[3]="-b"; // batch mode -- see https://www.nescent.org/wg_garli/FAQ#Can_I_use_GARLI_to_do_batches_of_runs.2C_one_after_another.3F
           com[4]="garli.conf";
           return com;
    }


}
