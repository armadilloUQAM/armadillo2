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

/**
 * Collection of util command
 * @author Jérémy Goimard
 */
public class Docker {
    public static int count=0; //--Internal variable for returnCount...
    public static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat dateyear = new SimpleDateFormat("yyyy");
    //--Logging function
    public PrintWriter log;
    public static String kword = "armadilloWF";
    
    public Docker() {}
    
    /***************************************************************************
     * DOCKER FUNCTIONS
     **************************************************************************/
    
    ////////////////////////////////////////////////////////////////////////////

    
    public static boolean isDockerHereStatic(workflow_properties properties) {
        String dockerCommand = getOSCommandLine(properties);
        ArrayList<String> s = Util.runSilentUnixCommand(dockerCommand+" --version","./");
        for (String st:s)
            if (st.contains("Docker version")) {
                return true;
            }
        return false;
    }

    /**
     * Test if docker image is already present in docker
     */
    public static boolean isImageNotHere(workflow_properties properties, String img) {
        ArrayList<String> s = getImages(properties);
        for (String st:s)
            if (st.contains(img))
                return false;
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Docker launchers

    public static String getOSCommandLine(workflow_properties properties) {
        if (config.getBoolean("MacOSX")||SystemUtils.IS_OS_MAC_OSX) {
            return properties.getExecutableMacOSX();
        } else if (config.getBoolean("Linux")||SystemUtils.IS_OS_LINUX||SystemUtils.IS_OS_UNIX) {
            return properties.getExecutableLinux();
        }
        return properties.getExecutable();        
    }
    
    
    /**
     * Launch and set docker image
     */
    public static void prepareDockerBashFile(workflow_properties properties, String doName, String dockerCli) {
        String dockerCommand = getOSCommandLine(properties);
        String s = Util.getCurrentJarPath()+File.separator+"tmp"+File.separator+"dockerBash.sh";
        Util.CreateFile(s);
        Path file = Paths.get(s);
        String userName = Util.getOwnerJar();
        String grpName = Util.getGroupJar();
        String dockerAddUser = "useradd "+userName+"";
        String dockerAddGrp = "groupadd "+grpName+"";
        String dockerChangeOwner = "chown -R "+userName+":"+grpName+" /data/outputs/";
        List<String> lines = Arrays.asList("#!/bin/bash", dockerCli, dockerAddUser, dockerAddGrp, dockerChangeOwner, "exit $?");
        try {
            Files.write(file, lines);
        } catch (IOException ex) {
            Logger.getLogger(Docker.class.getName()).log(Level.SEVERE, null, ex);
        }
        String c2 = dockerCommand+" cp "+s+" "+doName+":/data/dockerBash.sh";
        Util.runSilentUnixCommand(c2,"./");
        Util.deleteFile(s);
        String c1b = dockerCommand+" exec -i "+doName+" chmod 775 dockerBash.sh";
        Util.runSilentUnixCommand(c1b,"./");
    }

    /**
     * Launch and set docker image
     */
    public static boolean launchDockerImage(workflow_properties properties, String localpath, String dockerpath, String name, String img) {
        String dockerCommand = getOSCommandLine(properties);
        localpath = Util.getCanonicalPath(localpath);
        String c = dockerCommand+" run -v "+localpath+":"+dockerpath+" --name "+name+" -di "+img;
        ArrayList<String> sl = Util.runSilentUnixCommand(c,"./");
        if (sl.size()==1 && sl.get(0).matches("\\w+")) {
            return true;
        } else if (sl.size()>1 && sl.get(sl.size()-1).matches("\\w+")){
            return true;
        }
        return false;
    }

    /**
     * Launch and set docker image with shared folder
     */
    public static boolean launchDockerContainerTest(workflow_properties properties, HashMap<String,String> sharedFolders, String doName, String doImg) {
        String dockerCommand = getOSCommandLine(properties);
        String pull = dockerCommand+" pull " +doImg+"";
        ArrayList<String> slpull = Util.runSilentUnixCommand(pull,"./");
        HashMap<Integer,String> sharedF = new HashMap<Integer,String>();
        if (!sharedFolders.isEmpty()){
            int i = 0;
            sharedF.put(i,"docker run ");
            for (String k:sharedFolders.keySet()){
                i+=1;
                String p = Util.getCanonicalPath(k);
                //p = Util.escapeSpaceFromCanonicalPath(p);
                sharedF.put(i," -v \'"+p+"\':"+sharedFolders.get(k)+"");
            }
            sharedF.put(i+=1," --name "+doName);
            sharedF.put(i+=1," -di "+doImg);
            String[] b = sharedF.values().toArray(new String[sharedF.values().size()]);
            for (int y=0;y<b.length;y+=1){
                b[y]=sharedF.get(y);
            }
            Util.pl(Arrays.toString(b));
            try {
                boolean sl = runCommand4Docker(b);
                return sl;
            } catch (Exception ex) {
                Logger.getLogger(Docker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    
    /**
     * Launch and set docker image with shared folder
     */
    public static boolean launchDockerContainer(workflow_properties properties,HashMap<String,String> sharedFolders, String doName, String doImg) {
        String dockerCommand = getOSCommandLine(properties);
        String pull = dockerCommand+" pull " +doImg+"";
        ArrayList<String> slpull = Util.runSilentUnixCommand(pull,"./");
        for (String stmp:slpull)
            if (stmp.contains("Error response"))
                return false;
        String sharedF = "";
        if (!sharedFolders.isEmpty()){
            for (String k:sharedFolders.keySet()){
                String p = Util.getCanonicalPath(k);
                //p = Util.escapeSpaceFromCanonicalPath(p);
                sharedF += " -v "+p+":"+sharedFolders.get(k)+"";
            }
        }
        String c = dockerCommand+" run " + sharedF
                + " --name "+doName+" -di "+doImg;
        properties.put("CliDockerInit", c);
        ArrayList<String> sl = Util.runSilentUnixCommand(c,"./");
        if ((sl.size()==1 && sl.get(0).matches("\\w+"))||
            (sl.size()> 1 && sl.get(sl.size()-1).matches("\\w+"))) {
            return true;
        }
        return false;
    }

    /**
     * Test if docker container is already launch with the same name
     */
    public static boolean isContainersAlreadyUsed(workflow_properties properties, String name) {
        String dockerCommand = getOSCommandLine(properties);
        String c = dockerCommand+" ps --filter name="+name+
                   " --format {{.Names}}";
        ArrayList<String> ls = Util.runSilentUnixCommand(c,"./");
        if (ls.size()>=1)
            for (String s:ls)  
                if (s.contains(name))
                    return true;
        return false;
    }

    /**
     * Get the container name value
     */
    public static String getContainersVal(workflow_properties properties, String name) {
        if (isContainersAlreadyUsed(properties,name)) {
            String si = name;
            si = si.replaceAll(".*_(\\d+)$","$1");
            int i = Integer.parseInt(si);
            si = name.replaceAll("(.*_)\\d+$","$1");
            name = getContainersNextVal(properties,si,i);
            return name;
        } else 
            return name;
    }
    
    /**
     * Get the container name next value
     */
    public static String getContainersNextVal(workflow_properties properties, String name, int i) {
        if (i>100) {
            //System.out.println("Warnings already 100 containers have been send with this name. Please remove few of them to continue");
            return name+"_OUT";
        } else {
            if (!isContainersAlreadyUsed(properties,name+i)) {
                return name+Integer.toString(i);
            } else {
                i=i+1;
                return getContainersNextVal(properties,name,i);
            }
        }
    }
    
    /**
     * Get docker images
     */
    public static ArrayList<String> getImages(workflow_properties properties) {
        String dockerCommand = getOSCommandLine(properties);
        ArrayList<String> s = Util.runSilentUnixCommand(dockerCommand+" images","./");
        ArrayList<String> l = new ArrayList<String>();
        for (String st : s)
            if (!(st.contains("REPOSITORY")))// && st.contains(kword))
                l.add(st);
        return l;
    }

    /**
     * Get docker active containers infos ID Names Image
     */
    public static ArrayList<String> getActivesContainers(workflow_properties properties) {
        String dockerCommand = getOSCommandLine(properties);
        String c = dockerCommand+" ps --format {{.ID}}<>{{.Names}}<>{{.Image}}";
        ArrayList<String> s = Util.runSilentUnixCommand(c,"./");
        ArrayList<String> val = new ArrayList<String>();
        for(String sa:s)
            if (sa.contains(kword))
                val.add(sa.replaceAll("<>",", "));
        return val;
    }

    /**
     * Get all docker containers infos ID Names Image
     */
    public static ArrayList<String> getAllContainers(workflow_properties properties) {
        String dockerCommand = getOSCommandLine(properties);
        String c = dockerCommand+" ps -a --format {{.ID}}<>{{.Names}}<>{{.Image}}";
        ArrayList<String> s = Util.runSilentUnixCommand(c,"./");
        ArrayList<String> val = new ArrayList<String>();
        for(String sa:s)
            if (sa.contains(kword))
                val.add(sa.replaceAll("<>",", "));
        return val;
    }

    /**
     * Get docker active containers infos ID
     */
    public static ArrayList<String> getActivesContainersID(workflow_properties properties) {
        String dockerCommand = getOSCommandLine(properties);
        String c = dockerCommand+" ps --format {{.ID}}<>{{.Names}}";
        ArrayList<String> s = Util.runSilentUnixCommand(c,"./");
        ArrayList<String> val = new ArrayList<String>();
        for(String sa:s)
            if (sa.contains(kword))
                val.add(sa.replaceAll("^\\w+<>(\\w+)","$1"));
        return val;
    }

    /**
     * Get docker active containers infos ID
     */
    public static ArrayList<String> getActivesContainersName(workflow_properties properties) {
        String dockerCommand = getOSCommandLine(properties);
        String c = dockerCommand+" ps --format {{.ID}}<>{{.Names}}";
        ArrayList<String> s = Util.runSilentUnixCommand(c,"./");
        ArrayList<String> val = new ArrayList<String>();
        for(String sa:s)
            if (sa.contains(kword))
                val.add(sa);
        return val;
    }

    /**
     * Get all docker containers infos ID
     */
    public static ArrayList<String> getAllContainersID(workflow_properties properties) {
        String dockerCommand = getOSCommandLine(properties);
        String c = dockerCommand+" ps -a --format {{.ID}}<>{{.Names}}";
        ArrayList<String> s = Util.runSilentUnixCommand(c,"./");
        ArrayList<String> val = new ArrayList<String>();
        for(String sa:s)
            if (sa.contains(kword))
                val.add(sa.replaceAll("^(\\w+)<>\\w+","$1"));
        return val;
    }

    /**
     * Get all docker containers infos ID
     */
    public static ArrayList<String> getAllContainersName(workflow_properties properties) {
        String dockerCommand = getOSCommandLine(properties);
        String c = dockerCommand+" ps -a --format {{.Names}}";
        ArrayList<String> s = Util.runSilentUnixCommand(c,"./");
        ArrayList<String> val = new ArrayList<String>();
        for(String sa:s)
            if (sa.contains(kword))
                val.add(sa);
        return val;
    }

    /**
     * Clean (stop and remove) Docker Containers List
     */
    public static boolean cleanContainer(workflow_properties properties, String s) {
        ArrayList<String> a = new ArrayList<String>();
        a.add(s);
        return cleanContainers(properties, a);
    }
    
    /**
     * Clean (stop and remove) Docker Containers List
     */
    public static boolean cleanContainers(workflow_properties properties, ArrayList<String> l) {
        if (!l.isEmpty() && stopContainers(properties,l)){
            ArrayList<String> all = getAllContainersName(properties);
            ArrayList<String> p = new ArrayList<String>();
            boolean b = false;
            for (String sa : all) {
                b = false;
                for (String ls : l) {
                    if (sa.contains(ls)) {
                        b =true;
                    }
                }
                if (b)
                    p.add(sa);
            }
            if (p.size()>0) {
                String dockerCommand = getOSCommandLine(properties);
                String s = dockerCommand+" rm "+p.toString().replaceAll("[\\[,\\]]"," ");
                ArrayList<String> v = Util.runSilentUnixCommand(s,"./");
                return Util.equalArrayLists(v,p);
            } else 
                return true;
        } else
            return false;
    }
    
    /**
     * Stop docker Container List
     */
    private static boolean stopContainers(workflow_properties properties, ArrayList<String> l) {
        
        ArrayList<String> ia  = getActivesContainersID(properties);
        ArrayList<String> p = new ArrayList<String>();
        boolean b = false;
        for (String sa : ia) {
            b = false;
            for (String ls : l) {
                if (sa.contains(ls)) {
                    b =true;
                }
            }
            if (b)
                p.add(sa);
        }
        if (p.size()>0) {
            String dockerCommand = getOSCommandLine(properties);
            String s = dockerCommand+" stop "+p.toString().replaceAll("[\\[,\\]]"," ");
            ArrayList<String> v = Util.runSilentUnixCommand(s,"./");
            return Util.equalArrayLists(v,p);
        } else 
            return true;
    }
    
    /**
     * Clean Inactives Armadillo containers
     */
    public static boolean cleanInactiveContainers(workflow_properties properties) {
        System.out.println("Clean Inactive Container");
        ArrayList<String> all = getAllContainersID(properties);
        ArrayList<String> ia  = getActivesContainersID(properties);
        
        ArrayList<String> ina = new ArrayList<String>();
        boolean b = false;
        for (String sa : all) {
            b = false;
            for (String si : ia) {
                if (sa.equals(si)) {
                    b =true;
                }
            }
            if (!b)
                ina.add(sa);
        }
        if (ina.size()>0 && cleanContainers(properties,ina)) return true;
        else return false;
    }
    
    
    /**
     * Clean Inactives Armadillo containers
     */
    public static void removeImages(workflow_properties properties, ArrayList<String> l) {
        //Find container which used image names and stop, remove them
        ArrayList<String> cont = Docker.getAllContainers(properties);
        ArrayList<String> cImg = new ArrayList<String>();
        for (String s:cont) {
            for (String imgName : l) {
                if (s.contains(imgName)){
                    cImg.add(s.substring(0,3));
                }
            }
        }
        cleanContainers(properties,cImg);
        for (String imgName:l) {
            String dockerCommand = getOSCommandLine(properties);
            ArrayList<String> st = Util.runSilentUnixCommand(dockerCommand+" rmi "+imgName,"./");
            if (st.toString().contains("Error") && st.toString().contains("container")) {
                System.out.println("This images is already used out of Armadillo");
            }
        }
    }
    
    /**
     * Clean A specific container name
     */
    public static boolean CleanContainerName(workflow_properties properties) {
        if (properties.isSet("DOCKERName")) {
            return cleanContainer(properties,properties.get("DOCKERName"));
        }
        return true;
    }
    
    /*
    @obsolete : instead use -v and double exchange folders
                one for inputs, one for outputs
                then run changeOwnerOutputDir()
    
    String sd, // Shared directory
    String dd, // Docker directory
    String doName // Docker name
    */
    public static boolean copyDockerDirToSharedDir (workflow_properties properties, String sd, String dd, String doName) {
        String dockerCommand = getOSCommandLine(properties);
        String s = dockerCommand+" exec -ti "+doName+" mkdir "+sd+" "
                + "&& "+dockerCommand+" exec -ti "+doName+" cp -r "+dd+" "+sd+" "
                + "&& "+dockerCommand+" exec -ti "+doName+" chmod -R 777 "+sd+" "
                ;
        ArrayList<String> v = Util.runSilentUnixCommand(s,"./");
        boolean b = false;
        if (v.isEmpty()){
            b = Util.copyDirectory(sd,sd+"_bd");
            if (b)
                b = Util.deleteDir(sd);
            if (b)
                b = Util.copyDirectory(sd+"_bd",sd);
        }
        return b;
    }
    
    /*
        Change output directory owner files
    */
    public static boolean changeOwnerOutputDir(workflow_properties properties, String doName) {
        String userName = Util.getOwnerJar();
        String s = "docker exec -i "+doName+""
                + " sh -c \"chown -R "+userName+" /data/output/ \"";
        ArrayList<String> v = Util.runSilentUnixCommand(s,"./");
        if (v.isEmpty()){
            return false;
        }
        return true;
    }
    
    public static boolean runCommand4Docker(String[] commandline) throws Exception {
        //--Run the thread and catch stdout and stderr
        ProcessBuilder pb=new ProcessBuilder(commandline);
        Runtime r = Runtime.getRuntime();
        Process p;
        p = r.exec(Util.toString(commandline));
        int exitvalue=p.waitFor();
        Util.pl("int>"+p.getInputStream().toString());
        Util.pl("int>"+p.getErrorStream().toString());
        Util.pl("int>"+p.getOutputStream().toString());
        Util.pl("int>"+Integer.toString(exitvalue));
        return true;
    }
    
}
