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


package program;

import biologic.Alignment;
import biologic.Biologic;
import biologic.Input;
import biologic.MultipleSequences;
import biologic.Output;
import biologic.OutputText;
import biologic.Sequence;
import biologic.Tree;
import biologic.Unknown;
import configuration.Cluster;
import configuration.Config;
import static configuration.Docker.kword;
import configuration.Docker;
import configuration.Util;
import database.databaseFunction;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import workflows.Workbox;
import workflows.workflow_properties;
import org.apache.commons.lang.SystemUtils;


/**
 * This is an Object reflecting the database object
 * IT IS ALSO THE MAIN RUNNABLE TYPE FOR ARMADILLO...
 * @author Etienne Lord
 */
public class RunProgram implements runningThreadInterface {
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    /// Database variables
    protected int id=0;
    protected int properties_id = 0;
    private int workflows_id = 0;
    protected String note = "";
    protected String programTimeStart = "";
    protected String programTimeEnd = "";
    protected String runProgramOutput="";
    
    //--Note: the databaseFunction might be redefine with the workflow_properties
    public static databaseFunction df = new databaseFunction();
    public static Config config=new Config();
    public static Workbox workbox=new Workbox();
    
    ////////////////////////////////////////////////////////////////////////////
    /// Constant for properties PORT
    
    public static Integer PortInputUP      = 3;
    public static Integer PortInputDOWN    = 2;
    public static Integer PortInputDOWN2   = 4;
    public static Integer PortInput        = null;
    public static Integer PortOutput       = 0;
    
    ////////////////////////////////////////////////////////////////////////////
    /// Constant Verification
    
    
    ////////////////////////////////////////////////////////////////////////////
    /// Running program
    
    //--Input / Output
    private final ArrayList<String> outputText = new ArrayList<String>();
    private ArrayList<Input> input             = new ArrayList<Input>();
    private ArrayList<Output> output           = new ArrayList<Output>();
    
    ////////////////////////////////////////////////////////////////////////////
    /// STDIN
    
//    private Alignment alignment=new Alignment();
//    private MultipleAlignments multiplealignments=new MultipleAlignments();
//    private Sequence  sequence =new Sequence();
//    private MultipleSequences multiplesequences=new MultipleSequences();
//    private Tree tree=new Tree();
//    private MultipleTrees multipletrees=new MultipleTrees();
    
    ////////////////////////////////////////////////////////////////////////////
    ///
    
    protected long timerunning = 0;           //time running
    protected Thread thread;                  //Thread
    protected Process p;                      //Process
    protected static Runtime r;               //Runtime object
    protected HashMap<String,String> inputFilenames = new HashMap<String,String>();
    protected workflow_properties properties        = new workflow_properties();
    public Vector<String> cleanfilelist             = new Vector<String>(); //--List of files to compare to output file
    protected String[] commandline                  = {};
    
    ////////////////////////////////////////////////////////////////////////////
    /// End Regex -> If detected in the stdout or stderror, will stop the program
    /// *Note* Will be verify in the msg(String str) function
    
    //--Exemple for PhyML
    private Pattern ErrorEndRegex  = Pattern.compile("Type any key to exit");
    private Pattern NormalEndRegex = Pattern.compile("Type any key to exit");
    
    //-- Status code (This is the most important table )
    public static final int status_nothing  = 0;
    public static final int status_idle     = 1;
    public static final int status_changed  = 10;
    public static final int status_done     = 100;
    public static final int status_error    = 404; //:)
    public static final int status_warning  = 900;
    public static final int status_running  = 500;
    public static final int status_BadRequirements      = 450;
    public static final int status_runningclassnotfound = 408;
    public static final int status_programnotfound      = 410;
    
    ////////////////////////////////////////////////////////////////////////////
    /// Kill Thread Java
    protected boolean cancel=false; //obsolete
    
    
    public RunProgram() {
        properties.put("NoThread", true); //--By default, all program are run as Thread
        //-- Meaning, we don't return until it finish
        if (r==null) r=Runtime.getRuntime();
    }
    
    public RunProgram(int id) {
        loadFromDatabase(id);
        if (r==null) r=Runtime.getRuntime();
    }
    
    public RunProgram(String command) {
        properties.put("CommandLine", command);
        if (r==null) r=Runtime.getRuntime();
    }
    
    public RunProgram(workflow_properties properties) {
        this.properties=properties;
        this.setProperties_id(properties.getProperties_id());
        if (r==null) r=Runtime.getRuntime();
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    // Database function
    
    public boolean loadFromDatabase(int id) {
        RunProgram run=df.getRunProgram(id);
        if (run.id>0) {
            this.id=id;
            this.note=run.note;
            this.runProgramOutput=run.runProgramOutput;
            this.properties_id=run.properties_id;
            this.programTimeStart=run.programTimeStart;
            this.programTimeEnd=run.programTimeEnd;
            this.workflows_id=run.workflows_id;
            return true;
        } else return false;
    }
    
    public boolean saveToDatabase() {
        id=df.addRunProgram(this);
        return (id==0?false:true);
    }
    
    public boolean update() {
        return df.updateRunProgram(this);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    /// Execution
    
    /**
     * Main function to call
     */
    public void execute() {
//        if (workbox.isWorkboxATest()) {// JG 2016
//            properties.put("THISISCLUSTERTEST",true);
//            try {
//                if (init_runTest()) {
//                    do_runTest();
//                }
//                Docker.CleanContainerName(properties);
//                if (getStatus()!=status_error&&getStatus()!=status_BadRequirements&&getStatus()!=status_runningclassnotfound&&getStatus()!=status_programnotfound) {
//                    saveOutput(getStatus());
//                    post_runTest();
//                    setStatus(status_done,"");
//                }
//            } catch (Exception ex) {
//                if (properties.getBoolean("debug")) ex.printStackTrace();
//                if (!cancel) {
//                    Docker.CleanContainerName(properties);
//                    setStatus(status_error,"Error in test running... \n"+ex.getMessage());
//                }
//            }
//        } else {
        runthread();
//        }
        //--Note: removed since its handle in the programs class
        //if (properties.getBoolean("NoThread")) while(!isDone()){}
    }
    
    /**
     * Main function to call if we want to run something...
     * Without waiting for it to terminate
     * Example: External editor
     */
    public void executeWithoutWait() {
        runthread_withoutWait();
        //--Note: removed since its handle in the programs class
        //if (properties.getBoolean("NoThread")) while(!isDone()){}
    }
    
    /**
     * @return the workflows_id
     */
    public int getWorkflows_id() {
        return workflows_id;
    }
    
    /**
     * @param workflows_id the workflows_id to set
     */
    public void setWorkflows_id(int workflows_id) {
        this.workflows_id = workflows_id;
    }
    
    /**
     * @return the EndRegex
     */
    public Pattern getErrorEndRegex() {
        return ErrorEndRegex;
    }
    
    /**
     * @param EndRegex the EndRegex to set
     */
    public void setErrorEndRegex(Pattern EndRegex) {
        this.ErrorEndRegex = EndRegex;
    }
    
    /**
     * @return the NormalEndRegex
     */
    public Pattern getNormalEndRegex() {
        return NormalEndRegex;
    }
    
    /**
     * @param NormalEndRegex the NormalEndRegex to set
     */
    public void setNormalEndRegex(Pattern NormalEndRegex) {
        this.NormalEndRegex = NormalEndRegex;
    }
    
    
    
    ///////////////////////////////////////////////////////////////////////////
    ///
    
    public class InputStreamThread {
        //--TO DO: create a file handler here...
        InputStream is;
        boolean debug=false;
        // private Vector<String> output=new Vector<String>();
        
        public InputStreamThread(InputStream is) {
            this.is=is;
            runthread();
        }
        
        public void runthread() {
            Thread thread=new Thread(){
                
                @Override
                public void run() {
                    try {
                        BufferedReader br=new BufferedReader(new InputStreamReader(is));
                        String line=null;
                        while ((line=br.readLine())!=null) {
                            //--Hack for
                            
                            msg(line);
                            
                        }
                    } catch(Exception e){e.printStackTrace();}
                }
            };
            thread.start();
        }
    }
    
    
    
    /**
     * Standard thread definition with various level of overriding
     */
    protected void runthread() {
        thread=new Thread(){
            
            @Override
            public void run() {
                //--Cascade run...
                try {
                    //--Clean memory
                    Util.CleanMemory();
                    //--pre run initialization
                    setStatus(status_running,"Initialization...");
                    if (init_run()&&!isInterrupted()) {
                        // JG 2015 Start
                        if (workbox.isWorkboxOnCLuster()) {
                            if (do_runOnCluster()&&!isInterrupted()) {
                                // JG 2015 Start
                                setStatus(status_running,"<-End Program Output ->");
                                msg("\tProgram Exit Value: "+getExitVal());
                            }
                        } else if (do_run()&&!isInterrupted()) {
                            setStatus(status_running,"<-End Program Output ->");
                            msg("\tProgram Exit Value: "+getExitVal());
                        }
                        // JG 2015 End
                    }
                    //--Note: work Even if not set because We return 0...
                    //setStatus(getStatus(), "Total running time: "+Util.msToString(getRunningTime()));
                    if ((properties.getBoolean("VerifyExitValue")&&getExitVal()!=properties.getInt("NormalExitValue"))) {
                        setStatus(status_error,"***Error with at "+getRunningTime()+" ms ExitValue: "+properties.get("ExitValue")+"\n");
                        Docker.cleanContainer(properties);
                        post_run_clean();
                    }  else {
                        if (getStatus()!=status_error&&getStatus()!=status_BadRequirements&&getStatus()!=status_runningclassnotfound&&getStatus()!=status_programnotfound) {
                            // JG 2015 Start
                            // Cool closed to status done !
                            saveOutput(getStatus());
                            // We can make the post run
                            post_run_clean();
                            post_run_parseOutput();
                            // We can change the status to status_done
                            setStatus(status_done,"");
                            // JG 2015 End
                        }
                    }
                    
                } catch (Exception ex) {
                    if (properties.getBoolean("debug")) ex.printStackTrace();
                    if (!cancel) {
                        setStatus(status_error,"Error in running... \n"+ex.getMessage());
                        Docker.cleanContainer(properties);
                    }
                }
                programTimeEnd=Util.returnCurrentDateAndTime();
                
            }
            
            @Override
            public void destroy() {
                if (p!=null) p.destroy();
                this.interrupt();
                thread.currentThread().interrupt();
            }
            
            
            
        };
        
        timerunning=System.currentTimeMillis();
        properties.put("TimeRunning", timerunning);
        programTimeStart=Util.returnCurrentDateAndTime();
        //thread.setPriority(Thread.NORM_PRIORITY);
        //System.out.println(properties.getExecutable()+" "+thread.getPriority());
        thread.start();
        
    }
    
    /**
     * Standard thread definition with various level of overriding
     */
    protected void runthread_withoutWait() {
        
        thread=new Thread(){
            @Override
            public void run() {
                //--Cascade run...
                try {
                    //--pre run initialization
                    setStatus(status_running,"Initialization...");
                    if (init_run()&&!isInterrupted()) {
                        //--actual run
                        setStatus(status_running, "\tRunning program...");
                        // Print the command line
                        String s = Util.toString(commandline);
                        
                        Util.pl(s);
                                
                                
                        //if (!s.contains("Not Set")) System.out.println(s);
                        
                        setStatus(status_running,"<-Program Output->");
                        
                        if (do_run_withoutWait()&&!isInterrupted()) {
                            setStatus(status_running,"<-End Program Output ->");
                            msg("\tProgram Exit Value: "+getExitVal());
                        }
                    }
                    //--Note: work Even if not set because We return 0...
                    //setStatus(getStatus(), "Total running time: "+Util.msToString(getRunningTime()));
                    if (properties.getBoolean("VerifyExitValue")&&getExitVal()!=properties.getInt("NormalExitValue")) {
                        setStatus(status_error,"***Error with at "+getRunningTime()+" ms ExitValue: "+properties.get("ExitValue")+"\n");
                        Docker.cleanContainer(properties);
                        post_run_clean();
                    } else {
                        if (getStatus()!=status_error&&getStatus()!=status_BadRequirements&&getStatus()!=status_runningclassnotfound&&getStatus()!=status_programnotfound) {
                            // Cool closed to status done !
                            saveOutput(getStatus());
                            // We can make the post run
                            post_run_clean();
                            post_run_parseOutput();
                            // We can change the status to status_done
                            setStatus(status_done,"");
                        }
                    }
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    setStatus(status_error,"Error in running... \n"+ex.getMessage());
                    //if (properties.getBoolean("debug"))
                }
                programTimeEnd=Util.returnCurrentDateAndTime();
            }
        };
        timerunning=System.currentTimeMillis();
        properties.put("TimeRunning", timerunning);
        programTimeStart=Util.returnCurrentDateAndTime();
        thread.start();
    }
    
    
    public static boolean isExecutableFound(workflow_properties prop) {
        
        if (config.getBoolean("UseAlternative")) {
            //-CASE Alternative
            String executable=prop.getAlternative();
            if (!prop.isSet("AlternativeExecutable")||executable.equals("")) return false;
            return (Util.FileExists(executable));
        }
        //--CASE WebServices  or internal application (java code)
        if (prop.getBoolean("WebServices")||prop.getBoolean("InternalArmadilloApplication")) {
            return true;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            //--Windows
            String executable=prop.getExecutable();
            if (!prop.isSet("Executable")||executable.equals("")) return false;
            return (Util.FileExists(executable));
        } else if (config.getBoolean("MacOSX")||SystemUtils.IS_OS_MAC_OSX) {
            //--CASE MAC OS X
            String executable=prop.getExecutableMacOSX();
            if (!prop.isSet("ExecutableMacOSX")||executable.equals("")) return false;
            return (Util.FileExists(executable));
        } else if (config.getBoolean("Linux")||SystemUtils.IS_OS_LINUX||SystemUtils.IS_OS_UNIX) {
            //--CASE Linux?
            String executable=prop.getExecutableLinux();
            if (!prop.isSet("ExecutableLinux")||executable.equals("")) return false;
            return (Util.FileExists(executable));
        }
        
        return false;
    }
    
    public String[] updateCommandLine(String[] cli) {
        //--Note: 50 is arbitrary number large enough...
        String[] command= new String[50];
        //--Initialization
        for (int i=0; i<50;i++) {
            if (i<cli.length) {
                command[i]=cli[i];
            } else command[i]="";
        }
        
        //--CASE 1. MacOSX (Warning, 1st because MAC_OSX is Linux (lol)
        if (config.getBoolean("MacOSX")||SystemUtils.IS_OS_MAC_OSX) {
            if (command[0].equalsIgnoreCase("cmd.exe"))
                for (int i=0; i<command.length-2;i++) command[i]=command[i+2];
            
            //--Hack
            if (command[0].equalsIgnoreCase("java")) {
                //--Any extra command?
                //--We locate the good spot
                if (command[2].startsWith("-Xmx")||command[2].startsWith("-classpath")) {
                    command[3]=properties.getExecutableMacOSX();
                } else {
                    command[2]=properties.getExecutableMacOSX();
                }
            } else {
                command[0]=properties.getExecutableMacOSX();
            }
            // return command;
        } else if (config.getBoolean("Linux")||SystemUtils.IS_OS_LINUX||SystemUtils.IS_OS_UNIX) {
            //--or CASE 2.Linux?
            //--Test if we included the cmd.exe /C
            if (command[0].equalsIgnoreCase("cmd.exe"))
                for (int i=0; i<command.length-2;i++) command[i]=command[i+2];
            //--Hack
            //--Hack
            if (command[0].equalsIgnoreCase("java")) {
                command[2]=properties.getExecutableLinux();
            } else {
                command[0]=properties.getExecutableLinux();
            }
        }
        
        //--Change the - to -- if found...
//               for (int i=0; i<command.length;i++) {
//                   if (command[i].startsWith("-")) command[i]="-"+command[i];
//               }
        
        //--CASE 3. Use Alternative^
        if (properties.getBoolean("UseAlternative")) {
            command[0]=properties.getAlternative();
        }
        
        // Finally return the command
        return command;
    }
    
    /**
     * This is the initialization step of this particular program:
     * Note: Normally, we shouldn't overide directly this method
     *
     * Overidable:
     *    init_checkRequirements
     *    init_checkCreateInput
     *    init_createCommandline
     *
     * 1-Verification of the specific requierement
     * 2-Verification of the input
     * 3-Creation of the Running commandline
     * @throws Exception
     */
    public boolean init_run() throws Exception {
        if (getId()==0) {
            this.saveToDatabase();
        }
        setStatus(status_idle,"");
        if (properties.isSet("Name")) {
            setStatus(status_running,"Running ["+properties.getName()+"]");
        } else {
            setStatus(status_running,"Running ["+properties.get("Executable")+"]");
        }
        //--CASE 1. Check if program is found on this system
        if (isExecutableFound(properties)) {
            //--ok... Everything is in order...
        } else {
            //--CASE 2. Ok, executable not found, maybe we need to update properties with new executable
            //--Update (?)
            String filename=properties.get("filename");
            //--Take only the filename
            File f=new File(filename);
            filename=f.getName();
            //--New - November 2011 -- Scan for filename
            try {
                Pattern p=Pattern.compile("properties.((.*).properties)");
                
                Matcher m=p.matcher(filename);
                
                if (m.find()) {
                    //System.out.println("found");
                    filename=m.group(1);
                }
            } catch(Exception e) {}
            workflow_properties newprop=new workflow_properties();
            newprop.load(filename, config.propertiesPath());
            //System.out.println(newprop.filename+" "+filename+" "+config.propertiesPath());
            if (isExecutableFound(newprop)) {
                setStatus(status_running,"Updating executable for "+properties.getName()+" from "+properties.getExecutable()+" to "+newprop.getExecutable());
                properties.setExecutable(newprop.getExecutable());
                properties.setExecutableMacOSX(newprop.getExecutableMacOSX());
                properties.setExecutableLinux(newprop.getExecutableLinux());
                if (newprop.isSet("RunningDirectory")) properties.put("RunningDirectory", newprop.get("RunningDirectory"));
                if (newprop.isSet("RuntimeMacOSX")) properties.put("RuntimeMacOSX", newprop.get("RuntimeMacOSX")) ;
            } else {
                //--CASE 3. Ok, nothing found... report...
                setStatus(status_programnotfound,"Executable not found: "+properties.getExecutable());
                return false;
            }
        }
        
        //--Use alternative
        
        
        //--Check the program requirement
        setStatus(status_running,"\tChecking program requirements...");
        if (!init_checkRequirements()) {
            //--
            setStatus(status_BadRequirements,"Some requirements not found.");
            return false;
        }
        
        //--Create the input
        setStatus(status_running,"\tCreating inputs...");
        init_createInput();
        
        //--Create the commandline and save to properties and Commandline_Running
        setStatus(status_running,"\tCreating commandline...");
        commandline = init_createCommandLine();
        //--Update command line with the good executable
        commandline = updateCommandLine(commandline);
        properties.put("Commandline_Running", Util.toString(commandline));
        //--Output commmand line
        if (Util.toString(commandline).indexOf("Not Set")==-1) {
            setStatus(status_running,properties.get("Commandline_Running"));
        } else if (!properties.getBoolean("InternalArmadilloApplication")&&!properties.getBoolean("WebServices")) {
            Config.log("Warning: Not Set in commandline:"+Util.toString(commandline));
            setStatus(status_warning,"Error: Not Set in commandline:"+properties.get("Commandline_Running"));
        }
        return true;
    }
    
    /**
     * This is the the actual run of the program
     * 1-Execute in the thread the commandline
     * 2-Catch both stderr and stdout
     * 3-Put the program ExitValue in properties->ExitValue
     * 4-Put  both stderr and stdout in the output vector and in the "output"properties
     * @throws Exception
     */
    public boolean do_run() throws Exception {
        
        setStatus(status_running, "\tRunning program...");
        setStatus(status_running,"<-Program Output->");
        //--Run the thread and catch stdout and stderr
        ProcessBuilder pb=new ProcessBuilder(commandline);
        if (properties.isSet("RunningDirectory")) {
            pb.directory(new File(properties.get("RunningDirectory")));
        }

        r = Runtime.getRuntime();
        //--Test August 2011 - For Mac OS X
        if ((config.getBoolean("MacOSX")||SystemUtils.IS_OS_MAC_OSX)) {
            if (properties.isSet("RuntimeMacOSX")) {
                String execution_type=properties.get("RuntimeMacOSX");
                //--Default
                if (execution_type.startsWith("default")) {
                    //? Not suppose to exists...
                    p = pb.start();
                }
                //--Runtime (r.exec)
                if (execution_type.startsWith("runtime")) {
                    //--IF MAC_OSX, group option if UseRuntimeMacOSX
                    String cmdm = Util.toString(commandline);
                    cmdm = Util.replaceMultiSpacesByOne(cmdm);
                    cmdm = Util.removeTrailingSpace(cmdm);
                    /*
                    for (int i=0; i<commandline.length;i++) {
                        if (!commandline[i].equals(""))
                            cmdm+=commandline[i]+" ";
                    }
                    commandline=new String[1];
                    commandline[0]=cmdm;
                    */
                    p = r.exec(cmdm);
                }
                //--Bash...
                if (execution_type.startsWith("bash (.sh)")) {
                    //--Create a new bash file
                    Util u = new Util("RunProgram.sh");
                    u.println("#!/bin/sh");
                    u.println("echo \"Executing by bash command: "+properties.getName()+"\"");
                    u.println(Util.toString(commandline));
                    //--Return the application error code
                    u.println("exit $?");
                    u.close();
                    p=r.exec("sh RunProgram.sh");
                }
            } //--End RuntimeMacOSX
            //--Run time
            else {
                //--Create a new bash file
                Util u = new Util("RunProgram.sh");
                u.println("#!/bin/sh");
                u.println("echo \"Executing by bash command: "+properties.getName()+"\"");
                u.println(Util.toString(commandline));
                //--Return the application error code
                u.println("exit $?");
                u.close();
                p=r.exec("sh RunProgram.sh");
            }
        } else  if ((config.getBoolean("Linux")||SystemUtils.IS_OS_LINUX)) {
//                 Util u = new Util("RunProgram"+Util.returnTimeCode()+".sh");
//                 u.println("#!/bin/sh");
//                 u.println("echo \"Executing by bash command: "+properties.getName()+"\"");
//                 u.println(Util.toString(commandline));
//                 //--Return the application error code
//                 u.println("exit $?");
//                 u.close();
//                 p=r.exec("sh "+u.log_filename);
//                 Util.deleteFile(u.log_filename);
            String cli = Util.toString(commandline).replace("\\s+"," ");
            p=r.exec(cli); // JG 2015
        }
        else {
            p = pb.start();
        }

        //pb.redirectErrorStream(true)
        InputStreamThread stderr = new InputStreamThread(p.getErrorStream());
        InputStreamThread stdout = new InputStreamThread(p.getInputStream());

        int exitvalue=p.waitFor();
        properties.put("ExitValue", exitvalue);
        
        return true;
    }
    
    /**
     * This is the the actual run of the program
     * 1-Execute in the thread the commandline
     * 2-Catch both stderr and stdout
     * 3-Put the program exitValue in properties->exitValue
     * 4-Put  both stderr and stdout in the output vector and in the "output"properties
     * @throws Exception
     */
    public boolean do_run_withoutWait() throws Exception {
        //--Run the thread and catch stdout and stderr
        //--Use alternative?
        
        r = Runtime.getRuntime();
        if (config.getBoolean("MacOSX")||SystemUtils.IS_OS_MAC_OSX) {
            //--Test August 2011 - For Mac OS X
            if (properties.isSet("RuntimeMacOSX")) {
                String execution_type=properties.get("RuntimeMacOSX");
                //--Runtime (r.exec)
                if (execution_type.startsWith("runtime")) {
                    //System.out.println("Running by runtime...");
                    //--IF MAC_OSX, group option if UseRuntimeMacOSX
                    String cmdm = Util.toString(commandline);
                    cmdm = Util.replaceMultiSpacesByOne(cmdm);
                    cmdm = Util.removeTrailingSpace(cmdm);
                    /*
                    for (int i=0; i<commandline.length;i++) {
                        cmdm+=commandline[i]+" ";
                    }
                    commandline=new String[1];
                    commandline[0]=cmdm;
                    */
                    p = r.exec(cmdm);
                }
                
                //--Bash...
                if (execution_type.startsWith("bash (.sh)")) {
                    //System.out.println("Running from bash...");
                    //--Create a new bash file
                    Util u = new Util("RunProgram.sh");
                    u.println("#!/bin/sh");
                    u.println("echo \"Executing by bash command: "+properties.getName()+"\"");
                    u.println(Util.toString(commandline));
                    //--Return the application error code
                    u.println("exit $?");
                    u.close();
                    p=r.exec("sh RunProgram.sh");
                }
            } //--End RuntimeMacOSX
        } else {
            p = r.exec(commandline);
        }
        
        InputStreamThread stderr = new InputStreamThread(p.getErrorStream());
        InputStreamThread  stdout = new InputStreamThread(p.getInputStream());
        
        //--Wait for the exitValue
        properties.put("ExitValue", 0);
        return true;
    }
    
    
    /**
     * This is the post run part of the program
     * 1-Clean the inputFilename
     * 2-Set some status (by default: success required an exitValue of 0)
     * @throws Exception
     */
    
    public void post_run_clean() throws Exception {
        if (Util.FileExists("RunProgram.sh")) {
            setStatus(status_running,"\tDeleting RunProgram.sh... ");
            Util.deleteFile("RunProgram.sh");
        }
    }
    /**
     * This is the post run parsing part of the program
     */
    public void post_run_parseOutput() {
        setStatus(status_running,"\tParsing outputs... ");
        post_parseOutput();
        setStatus(status_running,"\n******************************\n");
    }

    
    /**
     * This is the Main procedure responsable for:
     * 1. Separate the commandline
     * 2. Replace some parameters
     * @return
     */
    public String[] init_createCommandLine() {
        
        // Initialize the String[]
        
        String[] com=new String[30];
        for (int i=0; i<com.length;i++) com[i]="";
        
        String Executable=properties.get("Executable");
        String cmdline=properties.get("CommandLine");
        
        
        if (Executable.endsWith(".jar")) {
            com[0]="java.exe";
            com[1]="-jar";
            com[2]=Executable;
        } else {
            //CASE 1. Simple command line, we need to parse it...
            int index=0;
            com[index++]="cmd.exe";
            com[index++]="/C";
            com[index++]=Executable;
            Vector<String>commands=getCommands(cmdline);
            for (String command:commands) {
                com[index++]=command;
            }
        }
        
        return com;
    }
    
    
    //////////////////////////////////////////////////////////////////////////
    /// Experimental command building
    
    public Vector<String> getCommands(String commandline) {
        Vector<String>commands=new Vector<String>();
        String current="";
        boolean compositeString=false;
        //--Add a space at the end for safety
        commandline+=' ';
        for (Character c:commandline.toCharArray()) {
            //CASE 1. Composite String flag
            if (c=='"') {
                if (compositeString) {
                    //-*-match and replace here
                    current=match(current);
                    commands.add(current);
                    current="";
                } else compositeString=true;
            } //CASE 2. Space ... the normal spliter
            else if (c==' ') {
                if (compositeString) {
                    current+=c;
                } else {
                    //--match and replace here
                    current=match(current);
                    commands.add(current);
                    current="";
                }
            } //CASE 3. Normal character, we add...
            else {
                current+=c;
            }
        }
        return commands;
    }
    
    //////////////////////////////////////////////////////////////////////////
    /// Part of experimental command building
    
    public String match(String current) {
        //CASE 1. Match filename
        for (String key:this.inputFilenames.keySet()) {
            int index=current.indexOf(key);
            if (index>-1) {
                //Config.log("MATCH: "+(String)key+" "+current);
                current=current.substring(0,index);
                String inputfilename=inputFilenames.get(key);
                if (inputfilename.indexOf(" ")>-1) inputfilename="\""+inputfilename+"\"";
                current+=inputfilename;
            }
        }
        //CASE 2. Match properties
        for (Object key:properties.UniqueKeyword()) {
            int index=current.indexOf((String)key);
            if (index>-1) {
                //Config.log("MATCH: "+(String)key+" "+current);
                current=current.substring(0,index);
                //current=current.replaceAll((String)key, (String)properties.get(key));
                String in=properties.get(key);
                if (in.indexOf(" ")>-1) in="\""+in+"\"";
                current+=in;
            }
        }
        //CASE 3. Match some keyword
        
        //Temporary directory
        int index=current.indexOf("temporary");
        if (index>-1) {
            String tmp=current.substring(index+9,current.length());
            String temporary=config.temporaryDir()+tmp;
            if (temporary.indexOf(" ")>-1) temporary="\""+temporary+"\"";
            current=current.substring(0, index)+temporary;
            //current=current.replaceAll("temporary", config.temporaryDir());
        }
        return current;
    }
    
    /**
     * This is the function called before we start the program
     */
    public void init_createInput() {
        
        // 1. Create the list of current file --Clean path
        config.temporaryDir(""+Util.returnCount());
        for (String filename:Config.listDir(config.temporaryDir())) cleanfilelist.add(filename);
        
        // 2. Create the output file
        
        this.inputFilenames.clear();
        for (Object k:properties.keySet()) {
            String key=(String)k;
            String filename=config.temporaryDir()+"/input"+Util.returnCount();
            if (key.startsWith("input_")) {
                if (key.startsWith("input_sequence_id")) {
                    MultipleSequences multi=new MultipleSequences();
                    Sequence s=new Sequence(properties.getInt(key));
                    multi.add(s);
                    if (properties.isSet("OutputPhylip")) {
                        multi.outputPhylipInterleaveWithSequenceID(filename);
                    } else {
                        multi.outputFastaWithSequenceID(filename);
                    }
                }
                if (key.startsWith("input_multiplesequences_id")) {
                    MultipleSequences multi=new MultipleSequences(properties.getInt(key));
                    if (properties.isSet("OutputPhylip")) {
                        multi.outputPhylipInterleaveWithSequenceID(filename);
                    } else {
                        multi.outputFastaWithSequenceID(filename);
                    }
                }
                if (key.startsWith("input_alignment_id")) {
                    Alignment multi=new Alignment(properties.getInt(key));
                    if (properties.isSet("OutputPhylip")) {
                        multi.outputPhylipInterleaveWithSequenceID(filename);
                    } else {
                        multi.outputFastaWithSequenceID(filename);
                    }
                }
                if (key.startsWith("input_tree_id")) {
                    Tree multi=new Tree(properties.getInt(key));
                    multi.outputNewickWithSequenceID(filename);
                }
                if (key.startsWith("input_unknown_id")) {
                    Unknown unknowntmp=new Unknown(properties.getInt(key));
                    unknowntmp.Output(filename);
                }
                this.inputFilenames.put(key,filename);
            }
        }
    }
    
    /**
     * this is the function called after we run the program
     */
    public void post_parseOutput() {
        // Need to be overrided in the ./src/programs/program.java
    }
    
    public boolean init_checkRequirements() {
        //Get require keywords;
        //workflow_properties_dictionnary dict =new  workflow_properties_dictionnary();
        if (properties.isSet("RequiredParameter")) {
            String[] params=properties.get("RequiredParameter").split(",");
            for (String s:params) {
                if (!properties.isSet(s)) return false;
            }
        }
        return true;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    /// Getter / Setter
    
    
    /**
     * @return the runProgram_id
     */
    public int getId() {
        return id;
    }
    
    /**
     * @param runProgram_id the runProgram_id to set
     */
    public void setId(int runProgram_id) {
        this.id = runProgram_id;
    }
    
    /**
     * @return the properties_id
     */
    public int getProperties_id() {
        return properties_id;
    }
    
    /**
     * @param properties_id the properties_id to set
     */
    public void setProperties_id(int properties_id) {
        this.properties_id = properties_id;
    }
    
    /**
     * @return the note
     */
    public String getNote() {
        return note;
    }
    
    /**
     * @param note the note to set
     */
    public void setNote(String note) {
        this.note = note;
    }
    
    /**
     * @return the programTimeStart
     */
    public String getProgramTimeStart() {
        return programTimeStart;
    }
    
    /**
     * @param programTimeStart the programTimeStart to set
     */
    public void setProgramTimeStart(String programTimeStart) {
        this.programTimeStart = programTimeStart;
    }
    
    /**
     * @return the programTimeEnd
     */
    public String getProgramTimeEnd() {
        return programTimeEnd;
    }
    
    /**
     * @param programTimeEnd the programTimeEnd to set
     */
    public void setProgramTimeEnd(String programTimeEnd) {
        this.programTimeEnd = programTimeEnd;
    }
    
    @Override
    public String toString() {
        if (this.getProperties_id()==0) return "RunProgram is not set";
        workflow_properties pro=df.getProperties(this.getProperties_id());
        String s  = "RunProgram for "+pro.getName()+"\n";
        s        += "Start: "+this.getProgramTimeStart()+"\n";
        s        += "End: "  +this.getProgramTimeEnd()+"\n";
        s        += "Note: " +this.getNote()+"\n";
        s        += this.getRunProgramOutput()+"\n";
        return s;
    }
    
    /**
     * @return the runProgramOutput
     */
    public String getRunProgramOutput() {
        return runProgramOutput;
    }
    
    /**
     * @param runProgramOutput the runProgramOutput to set
     */
    public void setRunProgramOutput(String runProgramOutput) {
        this.runProgramOutput = runProgramOutput;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// Thread
    
    /**
     *
     * @return the timerunning
     */
    public long getRunningTime() {
        return System.currentTimeMillis()-timerunning;
    }
    
    /**
     * Kill the current thread
     * @return True if Succes
     */
    public boolean KillThread() {
        try {
            //--Set flag to tell the thread that we cancel
            cancel=true;
            //--Try to kill this program if it is still running...
            //--Note: duplicate from what we found in programs class
            if (properties.getStatus()==status_running) {
                killer k=new killer(properties);
            }
            //--Lower this thread properties... So that that the system become more responsible
            thread.setPriority(Thread.MIN_PRIORITY);
            //--Destroy any remaining process
            if (p!=null) p.destroy();
            if (thread!=null) thread.interrupt();
        } catch(Exception e) {e.printStackTrace();}
        return true;
    }
    
    
    public int getExitVal() {
        return properties.getInt("ExitValue");
    }
    
    public Object getItself() {
        return this;
    }
    
    public String getName() {
        return properties.getName();
    }
    
    public String getInformation() {
        return properties.getDescription();
    }
    
    
    public void msg(String msg) {
        //System.out.println(msg);
        //TO DO: save that to a file...
        //--Good for text version...
        outputText.add(msg+"\n");
        
        //--
        if (workbox!=null) workbox.addOutput(msg+"\n");
        Config.log(msg);
        //--Kill Switch
        Matcher m_error_end  = ErrorEndRegex.matcher(msg);
        Matcher m_normal_end = NormalEndRegex.matcher(msg);
        if (m_error_end.find()) {
            setStatus(status_error,"Warning. Kill switch found.\nProgram will be automatically terminated.\n");
        }
        if (m_normal_end.find()) {
            setStatus(status_done,"Done.\nProgram will be automatically terminated.\n");
        }
    }
    
    public workflow_properties getProperties() {
        return this.properties;
    }
    
    /**
     * Set the status of the RunProgram
     * Note: we catch Exception since might block will the thread is ended...
     * @param statusCode (see list on top)
     * @param msg
     */
    public void setStatus(int statusCode, String msg) {
        synchronized(this) {
            if (config.isDevelopperMode()) {
                switch(statusCode) {
                    case status_nothing:               Config.log(statusCode+"(nothing): "+msg); break;
                    case status_idle:                  Config.log(statusCode+"(idle): "+msg); break;
                    case status_changed:               Config.log(statusCode+"(changed): "+msg); break;
                    case status_done:                  Config.log(statusCode+"(done): "+msg); break;
                    case status_error:                 Config.log(statusCode+"(error): "+msg); break;
                    case status_warning:               Config.log(statusCode+"(warning): "+msg); break;
                    case status_running:               Config.log(statusCode+"(running): "+msg); break;
                    case status_BadRequirements:       Config.log(statusCode+"(bad requirements): "+msg); break;
                    case status_runningclassnotfound:  Config.log(statusCode+"(running class not found): "+msg); break;
                    case status_programnotfound:       Config.log(statusCode+"(program not found): "+msg); break;
                    default:                           Config.log(statusCode+"(unknown code!): "+msg);
                }
            }
            try {
                if (!msg.isEmpty()) msg(msg);
                //--Save output if not status_done // JG 2015
                if (statusCode==status_error||statusCode==status_BadRequirements||statusCode==status_programnotfound||statusCode==status_runningclassnotfound) {
                    saveOutput(statusCode);// JG 2015
                }
                properties.setStatus(statusCode, msg);
            } catch(Exception e) {
                Config.log(e.getMessage());
            }
        }
    }
    
    // Save the programme output in an Output Text
    // JG 2015 just extract the saving function
    protected void saveOutput (int statusCode){
        OutputText out=new OutputText();
        out.setText(outputText); //--Note, this also set a note
        out.setName(properties.getName()+" -software output ("+Util.returnCurrentDateAndTime()+")");
        out.saveToDatabase();
        if (out.getId()==0) Config.log("Unable to save software ouput with program status "+statusCode);
        else properties.put("output_outputtext_id",out.getId());
    }
    
    /**
     * @return the status
     */
    public int getStatus() {
        synchronized(this) {
            return properties.getStatus();
        }
    }
    
    /**
     * Return the state of the current RunProgram
     * @return True if done OR status is Error
     */
    public boolean isDone() {
        synchronized(this) {
            int status=getStatus();
            return (status==status_error||status==status_done||status==status_BadRequirements||status==status_programnotfound||status==status_runningclassnotfound);
        }
    }
    
    /**
     * Return the logged program output
     * @return
     */
    public ArrayList<String> getOutputTXT() {
        //return new ArrayList<String>();
        return this.outputText;
    }
    
    public String getOutputText() {
        String s="";
        try {
            synchronized(outputText) {
                for (String si:outputText) s+=(si);
            }
        } catch(Exception e) {}
        return s;
    }
    // JG 2015
    // Added to find the exact stdout of the program
    //
    public String getPgrmOutput (){
        String s = getOutputText();
        String t = "";
        String lines[] = s.split("\\r?\\n|\\r");
        
        int start = 0;
        int end   = 0;
        int out   = 0;
        
        boolean b = false;
        int i = 0;
        for (i =0; i<lines.length && b==false ;i++){
            if (lines[i].contains("<-Program Output->")) start = i+1; b=true;
        }
        String seq = "";
        if (out>end+1) {
            end = out;
        }
        for (i = start; i< lines.length;i++){
            String l = lines[i];
            if ( !(
                    l.contains("<-End Program Output ->")||
                    l.contains("Program Exit Value")||
                    l.contains("Parsing outputs...")||
                    l.matches("^>.*")||
                    l.matches("^[A-Z]*$")
                    )) {
                t = t+lines[i]+System.lineSeparator();
            }
            
            if (l.matches("^>.*"))seq = l+System.lineSeparator();
            if (l.matches("^[A-Z]*$"))seq=seq+l+System.lineSeparator();
        }
        t+=seq;
        return t;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // HELPER FUNCTIONS
    
    /**
     * Output to stdout the current program output associated with this program
     */
    public void PrintOutput() {
        //for (String stri:this.outputText) Config.log(stri);
    }
    
    /**
     * This delete the file created by Phylip
     */
    public boolean sureDelete(String path) {
        try {
            String[] files_to_delete={"outtree","outfile","infile","intree"};
            if (!path.isEmpty()&&!path.endsWith("\\")) path+="\\"; //Add in windows the dir separator
            
            for (String filename:files_to_delete) {
                Util.deleteFile(path+filename);
            }
        } catch(Exception e) {Config.log("Problem in suredelete()");return false;}
        return true;
    }
    
    public void setExecutable(String Executable) {
        properties.setExecutable(Executable);
    }
    
    public String getExecutable() {
        return properties.getExecutable();
    }
    
    /**
     * @return the commandline
     */
    public String[] getCommandline() {
        return commandline;
    }
    
    /**
     * @param commandline the commandline to set
     */
    public void setCommandline(String[] commandline) {
        this.commandline = commandline;
    }
    
    public void addOutput(Biologic b) {
        Output o=new Output(b);
        addOutput(o);
    }
    
    public void addOutput(Output output) {
        getOutput().add(output);
    }
    
    public void addInput(Biologic b) {
        Input o=new Input(b);
        addInput(o);
    }
    
    public void addInput(Input input) {
        getInput().add(input);
    }
    
//    /**
//     * @return the alignment
//     */
//    public Alignment getAlignment() {
//        return alignment;
//    }
//
//    /**
//     * @param alignment the alignment to set
//     */
//    public void setAlignment(Alignment alignment) {
//        this.alignment = alignment;
//    }
//
//    /**
//     * @return the multiplealignments
//     */
//    public MultipleAlignments getMultiplealignments() {
//        return multiplealignments;
//    }
//
//    /**
//     * @param multiplealignments the multiplealignments to set
//     */
//    public void setMultiplealignments(MultipleAlignments multiplealignments) {
//        this.multiplealignments = multiplealignments;
//    }
//
//    /**
//     * @return the sequence
//     */
//    public Sequence getSequence() {
//        return sequence;
//    }
//
//    /**
//     * @param sequence the sequence to set
//     */
//    public void setSequence(Sequence sequence) {
//        this.sequence = sequence;
//    }
//
//    /**
//     * @return the multiplesequences
//     */
//    public MultipleSequences getMultipleSequences() {
//        return multiplesequences;
//    }
//
//    /**
//     * @param multiplesequences the multiplesequences to set
//     */
//    public void setMultipleSequences(MultipleSequences multiplesequences) {
//        this.multiplesequences = multiplesequences;
//    }
//
//    /**
//     * @return the tree
//     */
//    public Tree getTree() {
//        return tree;
//    }
//
//    /**
//     * @param tree the tree to set
//     */
//    public void setTree(Tree tree) {
//        this.tree = tree;
//    }
//
//    /**
//     * @return the multipletrees
//     */
//    public MultipleTrees getMultipletrees() {
//        return multipletrees;
//    }
//
//    /**
//     * @param multipletrees the multipletrees to set
//     */
//    public void setMultipletrees(MultipleTrees multipletrees) {
//        this.multipletrees = multipletrees;
//    }
    
    /**
     * @return the input
     */
    public ArrayList<Input> getInput() {
        return input;
    }
    
    /**
     * @return the output
     */
    public ArrayList<Output> getOutput() {
        return output;
    }
    
    /**
     * @return the r
     */
    public static Runtime getRuntime() {
        return r;
    }
    
    /**
     * Note: this method should be override in all subsequent child
     * @return
     */
    public boolean test() {
        return false;
    };
    
    ////////////////////////////////////////////////////////////////////////////
    /// Cluster ZONE
    /*
     * Cluster ZONE
     */
    public boolean do_runOnCluster() throws IOException, InterruptedException {
        //--Test August 2011 - For Mac OS X
        if ((config.getBoolean("MacOSX")||SystemUtils.IS_OS_MAC_OSX)) {
            String cmdm = Cluster.macOSX_cmd_Modifications(properties, commandline);
            properties.put("Commandline_Running",cmdm);
        }
        
        boolean runLocal = false;
        boolean isRunning = false;
        boolean cantDownload = false;
        
        boolean b = Cluster.isClusterNeedInfoHere(workbox,properties);
        if (b){
            runLocal = true;
            setStatus(status_running, "\tNot enougth information to run on Cluster");
        }
        
        if (!runLocal)
            if (!Cluster.getAccessToCluster(workbox,properties)){
                runLocal = true;
                setStatus(status_running, "\tUnable to access to the server");
            }
        
        if (Cluster.isAClusterTasksNumberHere(properties)&&!runLocal) {
            isRunning = true;
        }
            
        if(!isRunning){
            if (!runLocal)
                if (!Cluster.isTheProgramOnCluster(workbox,properties)){
                    runLocal = true;
                    setStatus(status_running, "\tThe program and it's version has not been found online. Check the program properties");
                } else {
                    setStatus(status_running,"\t<-The program is available on the server->");
                }
            if (!runLocal)
                if (!Cluster.createClusterDir(workbox,properties)) {
                    runLocal = true;
                    setStatus(status_running, "\tNot able to create a directory on the server.");
                } else {
                    setStatus(status_running,"\t<-Directory created on the server->");
                }

            if (!runLocal)
                if (!Cluster.sendFilesOnCluster(workbox,properties)) {
                    runLocal = true;
                    setStatus(status_running, "\tNot able to send files to the server.");
                } else {
                    setStatus(status_running,"\t<-Files sended->");
                }

            if (!runLocal)
                if (!Cluster.clusterPbs(workbox,properties)) {
                    runLocal = true;
                    setStatus(status_running, "\tNot able to create and send the pbs file to the server.");
                } else {
                    setStatus(status_running, "\tRunning program on cluster...");
                    setStatus(status_running,"\t<-Program Cluster Status->");
                }
        }
        
        if (!runLocal)
            if (!Cluster.isStillRunning(workbox,properties)) {
                setStatus(status_BadRequirements, "\tThe program is still running. The workflow will stop and you will be able to test it later.");
                return false;
            }
        
        if (!runLocal)
            if (!Cluster.downloadResults(workbox,properties)) {
                cantDownload = true;
                setStatus(status_BadRequirements, "\tNot able to download results from the server.");
                return false;
            } else {
                setStatus(status_running,"\t<-Results downloaded from server->");
            }

        if (runLocal){
            setStatus(status_running, "\tRunning will done on the local machine...");
            try {
                if (do_run()) {
                    setStatus(status_running,"\t<-End Program Output ->");
                    msg("\tProgram Exit Value: "+getExitVal());
                }
            } catch (Exception ex) {
                Logger.getLogger(RunProgram.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            String stdOut = Cluster.getPgrmStdoutOutput(workbox,properties);
            String stdErr = Cluster.getPgrmStderrorOutput(workbox,properties);
            properties.put("SDOUT",stdOut);
            properties.put("STDERROR",stdErr);
            outputText.add(stdOut+"\n");
            outputText.add(stdErr+"\n");
        }
        if (!cantDownload){
            properties.remove("ClusterTasksNumber");
        }
            
        if (properties.isSet("ClusterDeleteAllFiles"))
            if (Boolean.parseBoolean((properties.get("ClusterDeleteAllFiles")))){
                Cluster.removeFilesFromCluster(workbox,properties);
                setStatus(status_running,"\t<-Sorry, Deleted files on cluster is not yet available->");
            } else {
                Cluster.savePathOfFilesOnCluster(properties);
                setStatus(status_running,"\t<-Sorry, Keep files on cluster is not yet available->");
            }
            /*
            
            REMOVE FILES ON CLUSTER !
            
            */
        int exitvalue=0;
        if (properties.isSet("NormalExitValue"))
            exitvalue=Integer.parseInt(properties.get("NormalExitValue"));
        properties.put("ExitValue", exitvalue);
        return true;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// Docker ZONE
    /*
    * Docker initialisation
    * @Obsolete Prefer using dockerInitContainer
    */
    public boolean dockerInit(String localpath, String dockerpath, String name, String img) {
        if (Docker.isDockerHere(properties) && Docker.isDockerNameWellWritten(name)) {
            boolean b = Docker.launchDockerImage(properties,localpath,dockerpath,name,img);
            if (!b) {
                setStatus(status_BadRequirements,"Not able to initiate the docker container");
                return false;
            }
        }
        return true;
    }

    /*
    * Docker Container initialisation
    * Use -v to share files
    */
    public boolean dockerInitContainer(workflow_properties properties, HashMap<String,String> sharedFolders, String doName, String doImage) {
        if (Docker.isDockerNameWellWritten(doName)){
            if (Docker.launchDockerContainer(properties,sharedFolders,doName,doImage)){
                if (properties.isSet("CliDockerInit"))
                    setStatus(status_running,"DockerInitCommandLine: $\n "+properties.get("CliDockerInit"));
                properties.put("DOCKERName",doName);
                return true;
            } else {
                Docker.cleanContainer(properties,doName);
                setStatus(status_BadRequirements,"Not able to initiate the docker container");
            }
        } else {
            setStatus(status_BadRequirements,"Bad Requirement, Already 100 containers have been send with this name. Please remove few of them to continue");
            setStatus(status_BadRequirements,"Or the name is not written well");
        }
        return false;
    }
}
