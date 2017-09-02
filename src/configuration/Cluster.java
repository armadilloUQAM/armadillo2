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
package configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
import workflows.workflow_properties;
import configuration.Util;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.SystemUtils;
import program.RunProgram;
import static program.RunProgram.config;
import static program.RunProgram.status_running;
import static program.RunProgram.workbox;
import workflows.Workbox;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import workflows.armadillo_workflow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.apache.commons.io.IOUtils;

/**
 * Collection of util command
 * @author Jérémy Goimard
 */
public class Cluster {
    /**
     * TO BE USED IN CLUSTER the program properties must contain
     * ClusterProgramName=
     * In general it's the same as name, but sometimes not
     * and
     * Version=
     * It need to match the exact cluster name, or the name's search will fail.
     */
    public static int count=0; //--Internal variable for returnCount...
    public static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat dateyear = new SimpleDateFormat("yyyy");
    //--Logging function
    public PrintWriter log;
    
    public Cluster() {}
    
    /***************************************************************************
     * CLUSTER FUNCTIONS
     **************************************************************************/
    
    public static boolean getAccessToCluster(Workbox workbox, workflow_properties properties){
        String c = "pwd";
        String p2rsa = getP2Rsa(workbox);
        Util.pl("youyou");
        ArrayList<String> tab = runSshCommand(workbox,p2rsa,c);
        if (tab.size()==2 && isASimpleUnixPath(tab.get(0))){
            properties.put("ClusterPWD",tab.get(0));
            Util.pl("youyou");
            return true;
        }
        return false;
    }
    
    /**
     * Get the cluster access address
     * @param workbox
     * @return 
     */
    public static String clusterAccessAddress(Workbox workbox) {
        workflow_properties p2 = workbox.getWorkFlowJInternalFrame().getProperties();
        return p2.get("ClusterAccessAddress");
    }
    
    public static boolean clusterAccessAddressHere(Workbox workbox) {
        workflow_properties p2 = workbox.getWorkFlowJInternalFrame().getProperties();
        return p2.isSet("ClusterAccessAddress");
    }
    
    public static String clusterSeverAddress(Workbox workbox) {
        String s = clusterAccessAddress(workbox);
        int i = s.lastIndexOf("@");
        return s.substring(i+1,s.length());
    }
    
    public static String clusterSeverUserName(Workbox workbox) {
        String s = clusterAccessAddress(workbox);
        int i = s.lastIndexOf("@");
        return s.substring(0,i);
    }
    
    /**
     * Test if needed informations are here
     */
    public static boolean isClusterNeedInfoHere(Workbox workbox, workflow_properties properties) {
        boolean b1 = clusterAccessAddressHere(workbox);
        boolean b2 = isP2RsaHere(workbox);
        if (b1&&b2){
            List<String> lines = Arrays.asList("ClusterLocalOutput_","ClusterLocalInput_", "ClusterProgramName", "Version", "ObjectID", "ExecutableCluster");
            for (String l :lines){
                Enumeration<Object> e = properties.keys();
                boolean b = true;
                while(e.hasMoreElements()&&b==true) {
                    String key=(String)e.nextElement();
                    if (key.contains(l))
                        if (properties.get(key)!="")
                            b = false;
                }
                if (b){
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Test if there is a cluster task number
     * Added by JG 2017
     * @param s path ex: ./path/to/file/file.f or \sdfkbgk\ls sldkf\
     * @return true or false
     */
    public static boolean isAClusterTasksNumberHere(workflow_properties properties) {
        if(properties.isSet("ClusterTasksNumber")){
            return true;
        }
        return false;
    }
    
    public static String getP2Rsa(Workbox workbox){
        workflow_properties p2 = workbox.getWorkFlowJInternalFrame().getProperties();
        return p2.get("PathToRSAFile");
    }

    public static boolean isP2RsaHere(Workbox workbox){
        workflow_properties p2 = workbox.getWorkFlowJInternalFrame().getProperties();
        if (p2.isSet("PathToRSAFile")){
            String s = p2.get("PathToRSAFile");
            if (!s.contains("path to private key")){
                return true;
            }
        }
        return false;
    }

    /**
     * Useless?
     * @param properties
     * @param commandline
     * @return 
     */
    public static String macOSX_cmd_Modifications(workflow_properties properties, String[] commandline) {
        String cmdm = Util.toString(commandline);
        String execution_type=properties.get("RuntimeMacOSX");
        if (execution_type.startsWith("runtime")) {
            cmdm = Util.replaceMultiSpacesByOne(cmdm);
            cmdm = Util.removeTrailingSpace(cmdm);
        }
        return cmdm;
    }
    
    public static ArrayList<String>  runSshCommand(Workbox workbox,String p2rsa, String command) {
        ArrayList<String> tab = new ArrayList<String>();
        String hostName = clusterSeverAddress(workbox);
        String userName = clusterSeverUserName(workbox);
        try {
            Connection conn = new Connection(hostName);
            conn.connect();
            File f = new File(p2rsa);
            boolean isAuthenticated = conn.authenticateWithPublicKey(userName,f,"");
            if (isAuthenticated == false)
                    throw new IOException("Authentication failed.");
            /* Create a session */
            Session sess = conn.openSession();
            sess.execCommand(command);
            InputStream stdout = new StreamGobbler(sess.getStdout());
            InputStream stderr = new StreamGobbler(sess.getStderr());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                tab.add(line);
            }
            br = new BufferedReader(new InputStreamReader(stderr));
            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                tab.add(line);
            }
            tab.add("ExitCode: >" + sess.getExitStatus()+"<");
            sess.close();
            conn.close();
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
            System.exit(2);
        }
        return tab;
    }

    public static boolean runUploadFile(Workbox workbox, String p2rsa, String local, String remote) {
        String hostName = clusterSeverAddress(workbox);
        String userName = clusterSeverUserName(workbox);
        try {
            Connection conn = new Connection(hostName);
            conn.connect();
            /* Now connect */
            File f = new File(p2rsa);
            boolean isAuthenticated = conn.authenticateWithPublicKey(userName, f,"");

            if (isAuthenticated == false) {
                throw new IOException("Authentication failed.");
            }
            SCPClient scp = conn.createSCPClient();
            scp.put(local, remote);
            conn.close();
        } catch (IOException ex) {
            Logger.getLogger(Cluster.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public static boolean runDownloadFile(Workbox workbox, String p2rsa, String local, String remote) {
        String hostName = clusterSeverAddress(workbox);
        String userName = clusterSeverUserName(workbox);
        try {
            Connection conn = new Connection(hostName);
            conn.connect();
            /* Now connect */
            File f = new File("/home/truc/.ssh/id_rsa");
            boolean isAuthenticated = conn.authenticateWithPublicKey(userName, f, "");
            if (isAuthenticated == false) {
                throw new IOException("Authentication failed.");
            }
            SCPClient scp = conn.createSCPClient();
            scp.get(remote, local);
            conn.close();
        } catch (IOException ex) {
            Logger.getLogger(Cluster.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public static boolean runDownloadDir(Workbox workbox, String p2rsa, String local, String[] remote) {
        String hostName = clusterSeverAddress(workbox);
        String userName = clusterSeverUserName(workbox);
        try {
            Connection conn = new Connection(hostName);
            conn.connect();
            /* Now connect */
            File f = new File("/home/truc/.ssh/id_rsa");
            boolean isAuthenticated = conn.authenticateWithPublicKey(userName, f, "");
            if (isAuthenticated == false) {
                throw new IOException("Authentication failed.");
            }
            SCPClient scp = conn.createSCPClient();
            scp.get(remote, local);
            conn.close();
        } catch (IOException ex) {
            Logger.getLogger(Cluster.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    /**
     * Tested on Cluster MP2 only to be validated in the others
     * @param workbox
     * @param pgName
     * @param pgVersion
     * @return 
     */
    public static String getModule(Workbox workbox, String pgName, String pgVersion){
        String c = "module avail";
        ArrayList<String> tab = runSshCommand(workbox,"/home/truc/.ssh/id_rsa",c);
        //ArrayList<String> tab = runSshCommand(workbox,c);
        Pattern pat1 = Pattern.compile(pgName+"\\/"+pgVersion);
        Pattern pat2 = Pattern.compile("\\s*[(]\\w[)]");
        if (tab.size()>0){
            for (String s:tab){
                Matcher mat1 = pat1.matcher(s);
                Matcher mat2 = pat2.matcher(s);
                if (mat1.find()){
                    if (mat2.find())
                        s = mat2.replaceAll("\\s*[(]\\w[)]");
                    return Util.removeTrailingSpace(s);
                }
            }
        }
        return "";
    }
    
    public static boolean isTheProgramOnCluster(Workbox workbox, workflow_properties properties){
        String pgName    = properties.get("ClusterProgramName");
        String pgVersion = properties.get("Version");
        String s = getModule(workbox,pgName,pgVersion);
        if (s!="") {
            properties.put("ClusterModuleIs", s);
            return true;
        }
        return false;
    }
    
    public static String getCommandLineRunning(workflow_properties properties) {
        if (properties.isSet("Commandline_Running"))
            return properties.get("Commandline_Running");
        return "";
    }
    
    /**
     * Add Specific properties that will be used in bashfile preparation
     */
    public static void addSpecificClusterProperties(armadillo_workflow.workflow_object source, armadillo_workflow.workflow_object dest, Integer i) {
        workflow_properties pSource = source.getProperties();
        workflow_properties pDest = dest.getProperties();
        if (pSource.isSet("FileNameFromId")){
            String s = pSource.get("FileNameFromId");
            pDest.put("ClusterLocalInput_"+Integer.toString(i), s);
        }
    }
    
    /**
     * Prepare the remote cluster file name
     */
    public static String getClusterDirPath(workflow_properties properties) {
        String clusterPWD = "";
        String prgmName = properties.get("ClusterProgramName");
        prgmName = Util.replaceMultiSpacesByOne(prgmName);
        prgmName = Util.replaceSpaceByUnderscore(prgmName);
        String prgmVersion = properties.get("Version");
        String prgmOrder = properties.get("ObjectID");
        
        if (properties.isSet("ClusterPWD"))
            clusterPWD = properties.get("ClusterPWD");
        if (clusterPWD!=""){
            return clusterPWD+"/"+prgmName+"_"+prgmVersion+"/"+prgmOrder;
        }
        return "";
    }
    
    public static String getClusterFilePath(workflow_properties properties, String localPath) {
        String clusterDir = getClusterDirPath(properties);
        String fName = Util.getFileNameAndExt(localPath);
        if (clusterDir!=""){
            return clusterDir+"/"+fName;
        }
        return "";
    }

    /**
     * Prepare, send and execute the cluster bash file
     */
    public static boolean clusterPbs(Workbox workbox, workflow_properties properties) {
        // Server name
        String serverName = getClusterServerName(clusterAccessAddress(workbox));
        // Prepare variables
        String c = getCommandLineRunning(properties);
        Enumeration<Object> e = properties.keys();
        while(e.hasMoreElements()) {
            String key=(String)e.nextElement();
            if (key.contains("ClusterLocalInput_")){
                String fLocal = properties.get(key);
                String fRem  = getClusterFilePath(properties,fLocal);
                c = c.replace(fLocal, fRem);
            }
            if (key.contains("ClusterLocalOutput_")){
                String fLocal = properties.get(key);
                String fName = Util.getFileNameAndExt(fLocal);
                String fRem = getClusterDirPath(properties)+"/outputs/"+fName;
                c = c.replace(fLocal, fRem);
            }
            if (key.contains("ExecutableCluster")){
                String pLoc = Util.getOSCommandLine(properties);
                String pRem = properties.get(key);
                c = c.replace(pLoc, pRem);
            }
        }
        String stdOut = getClusterFilePath(properties,"stdOutFile");
        String stdErr = getClusterFilePath(properties,"stdErrFile");
        
        
        String hours = "00";
        String minutes = "05";
        String seconds = "00";
        String nodesV = "1";
        String ppn = "1";
        String emailV = "";
        if (properties.isSet("WalltimeHours_box"))
            hours = properties.get("WalltimeHours_box");
        if (properties.isSet("WalltimeMinutes_box"))
            minutes = properties.get("WalltimeMinutes_box");
        if (properties.isSet("WalltimeSeconds_box"))
            seconds = properties.get("WalltimeSeconds_box");
        if (properties.isSet("BasicNodes_box"))
            nodesV = properties.get("BasicNodes_box");
        if (properties.isSet("BasicPPN_box"))
            ppn = properties.get("BasicPPN_box");
        if (properties.isSet("ContactEmail_box"))
            emailV = properties.get("ContactEmail_box");
        if (properties.isSet("ContactEmail_box"))
            seconds = properties.get("ContactEmail_box");
                
        
        String walltime = "#PBS -l walltime="+hours+":"+minutes+":"+seconds+"";
        String nodes    = "#PBS -l nodes="+nodesV+":ppn="+ppn+"";
        // Need to be setted depending on cluster choosed
        String qwork    = "#PBS -q qwork@mp2";
        String r        = "#PBS -r n";
        String stdDoc   = "#PBS -o "+stdOut+"";
        String stdDec   = "#PBS -e "+stdErr+"";
        String email    = "#PBS -M "+emailV+"";
        /*
        WARNINGS ITS ONLY FOR ONE MODULE
        */
        String module   = "module load "+properties.get("ClusterModuleIs")+"";
        
        // Prepare lines
        List<String> lines = new ArrayList<String>();
        lines.add("#!/bin/bash");
        lines.add(walltime);
        lines.add(nodes);
        lines.add(qwork);
        if (!serverName.contains("mp2")){
            lines.add(r);
            if (emailV!="")
                lines.add(email);
        }
        lines.add(stdDoc);
        lines.add(stdDec);
        lines.add(email);
        lines.add(module);
        lines.add(c);
        
        // Prepare bash file
        String fBash = Util.getCurrentJarPath()+File.separator+"tmp"+File.separator+"clusterPbs.sh";
        Util.CreateFile(fBash);
        Path file = Paths.get(fBash);
        try {
            Files.write(file, lines);
        } catch (IOException ex) {
            Logger.getLogger(Docker.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        String p2rsa = getP2Rsa(workbox);
        String clusterDir = getClusterDirPath(properties);
        boolean b = runUploadFile(workbox,p2rsa,fBash,clusterDir);
        if (!b){
            return false;
        }
        String tasksNum = executePbsOnCluster(workbox,p2rsa,clusterDir+"/clusterPbs.sh");
        if (tasksNum!=""){
            properties.put("ClusterTasksNumber",tasksNum);
            return true;
        }
        return false;
    }

    /**
     * Execute bash file on Cluster
     * Added by JG 2017
     * @param s path ex: ./path/to/file/file.f or \sdfkbgk\ls sldkf\
     * @return true or false
     */
    public static String executePbsOnCluster(Workbox workbox, String p2rsa, String file) {
        String c = "qsub ";
        ArrayList<String> tab = runSshCommand(workbox,p2rsa,c+" "+file);
        if (tab.size()==2){
            if (tab.get(0).contains(".mp2.m"))
                return tab.get(0);
        }
        return "";
    }
    
    /**
     * Create the directory in cluster
     * Added by JG 2017
     * @param s path ex: ./path/to/file/file.f or \sdfkbgk\ls sldkf\
     * @return true or false
     */
    public static boolean createClusterDir(Workbox workbox, workflow_properties properties) {
        String clusterDir = getClusterDirPath(properties)+"/outputs";
        String program = "mkdir -p";
        String p2rsa = getP2Rsa(workbox);
        ArrayList<String> tab = runSshCommand(workbox,p2rsa, program+" "+clusterDir);
        if (tab.size()==1)
            if(tab.get(0).contains("ExitCode: >0<")||tab.get(0).contains("ExitCode: >null<"))
                return true;
        return false;
    }
    
    /**
     * Create the directory in cluster
     * Added by JG 2017
     * @param s path ex: ./path/to/file/file.f or \sdfkbgk\ls sldkf\
     * @return true or false
     */
    public static boolean sendFilesOnCluster(Workbox workbox, workflow_properties properties) {
        String clusterDir = getClusterDirPath(properties);
        String p2rsa = getP2Rsa(workbox);
        Enumeration<Object> e = properties.keys();
        boolean b = true;
        while(e.hasMoreElements() && b==true) {
            String k = (String)e.nextElement();
            if (k.contains("ClusterLocalInput_")){
                String file = properties.get(k);
                boolean b2 = runUploadFile(workbox,p2rsa,file,clusterDir);
                if (!b2)
                    return false;
            }
        }
        return true;
    }
    
    /**
     * Wait output file
     * Added by JG 2017
     * @param 
     * @return true or false
     */
    public static boolean isStillRunning(Workbox workbox, workflow_properties properties) {
        Enumeration<Object> e = properties.keys();
        String taskId  = "";
        while(e.hasMoreElements()) {
            String key=(String)e.nextElement();
            if (key.contains("ClusterTasksNumber")){
                taskId  = properties.get(key);
            }
        }
        if (taskId!=""){
            Integer[] l = {60,60,60,60,60,60,120,240,480,960,1920};
            String p2rsa = getP2Rsa(workbox);
            String command = "qstat -f "+taskId+"";
            boolean b = false;
            int i = 0;
            while(b == false && i<l.length) {
                ArrayList<String> tab = runSshCommand(workbox,p2rsa,command);
                if (tab.size()==2 && tab.get(0).contains("qstat: Unknown Job Id")){
                    b = true;
                } else {
                    try {
                        TimeUnit.SECONDS.sleep(l[i]);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Cluster.class.getName()).log(Level.SEVERE, null, ex);
                        i = l.length;
                    }
                    i+=1;
                }
            }
            return b;
        }
        return false;
    }
    
    /**
     * Test if the output is here path is a Unix path
     * Added by JG 2017
     * @param 
     * @return true or false
     */
    public static boolean downloadResults(Workbox workbox, workflow_properties properties) {
        String p2rsa = getP2Rsa(workbox);
        Enumeration<Object> e = properties.keys();
        while(e.hasMoreElements()) {
            String key=(String)e.nextElement();
            if (key.contains("ClusterLocalOutput_")){
                String fLoc = properties.get(key);
                String fDir = fLoc;
                if(Util.testIfFile(fLoc))
                    fDir = Util.getParentOfFile(fLoc);
                String fClus = getClusterDirPath(properties)+"/outputs/";
                ArrayList<String> tab = runSshCommand(workbox,p2rsa,"ls "+fClus);
                if (tab.size()>=2 && !tab.get(0).contains("ls")){
                    tab.remove(tab.size()-1);
                    for (int i=0; i<tab.size(); i++){
                        tab.set(i,fClus+tab.get(i));
                    }
                    String[] tabTmp = tab.toArray(new String[tab.size()]);
                    boolean b2 = runDownloadDir(workbox,p2rsa,fDir,tabTmp);
                    if(!b2){
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static String getPgrmStdoutOutput(Workbox workbox, workflow_properties properties){
        String p2rsa = getP2Rsa(workbox);
        String fClus = getClusterFilePath(properties,"stdOutFile");
        ArrayList<String> tab = runSshCommand(workbox,p2rsa,"cat "+fClus);
        if (tab.size()==1 && tab.get(0).contains("cat"))
            return "";
        else {
            String output = "";
            for (String s:tab){
                output+=s+"\n";
            }
            return output;
        }
    }
    
    public static String getPgrmStderrorOutput(Workbox workbox, workflow_properties properties){
        String p2rsa = getP2Rsa(workbox);
        String fClus  = getClusterFilePath(properties,"stdErrFile");
        ArrayList<String> tab = runSshCommand(workbox,p2rsa,"cat "+fClus);
        if (tab.size()==1 && tab.get(0).contains("cat"))
            return "";
        else {
            String output = "";
            for (String s:tab){
                output+=s+"\n";
            }
            return output;
        }
    }
    
    /**
     * Test if the path is a Unix path
     * Added by JG 2017
     * @param s path ex: ./path/to/file/file.f or \sdfkbgk\ls sldkf\
     * @return true or false
     */
    public static boolean isASimpleUnixPath(String s) {
        return (s.matches("(/\\w*)+"));
    }
    
    /**
     * Remove the cluster task number
     * Added by JG 2017
     * @param s path ex: ./path/to/file/file.f or \sdfkbgk\ls sldkf\
     * @return true or false
     */
    public static boolean removeClusterTasksNumber(workflow_properties properties) {
        if(properties.isSet("ClusterTasksNumber")){
            properties.remove("ClusterTasksNumber");
            return true;
        }
        return false;
    }
    
    public static boolean removeFilesFromCluster(Workbox workbox, workflow_properties properties){
        
        return true;
    }
    
    public static boolean savePathOfFilesOnCluster(workflow_properties properties){
        
        return true;
    }
    
    /**
     * 
     * @param s
     * @return 
     */
    private static String getClusterServerName(String s){
        if (s.contains("briaree.calculquebec.ca"))
            return "biraree";
        if (s.contains("colosse.calculquebec.ca"))
            return "colosse";
        if (s.contains("cottos.calculquebec.ca"))
            return "cottos";
        if (s.contains("guillimin.hpc.mcgill.ca"))
            return "guillimin";
        if (s.contains("mp2.ccs.usherbrooke.ca"))
            return "mp2";
        if (s.contains("ms.ccs.usherbrooke.ca"))
            return "ms";
        if (s.contains("psi.concordia.ca"))
            return "psi";
    return "";
    }

    
    // OBSOLETE FUNCTIONS
    public static String[] call_Python_Process(String script, String commands)
            throws IOException, InterruptedException {
        //System.out.println("Commands>"+commands);
        String STDoutput = "";
        String STDError  = "";
        
        Process p = Runtime.getRuntime().exec("python "+script+" "+commands);
        BufferedReader bri = new BufferedReader
                    (new InputStreamReader(p.getInputStream()));
        BufferedReader bre = new BufferedReader
                    (new InputStreamReader(p.getErrorStream()));
        String line = "";

        // Check Output
        while ((line = bri.readLine()) != null) {
            STDoutput += line;
        }
        bri.close();
        while ((line = bre.readLine()) != null) {
            STDError += line;
        }
        bre.close();
        p.waitFor();
        
        String[] tab = {STDoutput,STDError};
        return tab;
    }

    public static String get_commands(workflow_properties properties, Workbox workbox) {
        String clusterAA = clusterAccessAddress(workbox);
        String str =properties.getPropertiesToVarStringWithEOL();
        str = "\'"+str+"\'";
        str = str.replaceAll(" ", "_____");
        String commands = " -obj "+str+clusterAA;
        return commands;
    }

}
