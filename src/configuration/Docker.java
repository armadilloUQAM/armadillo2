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

    /**
     * Test if docker program is installed
     */
    public static boolean isDockerHere() {
        ArrayList<String> s = Util.runSilentUnixCommand("docker --version","./");
        for (String st:s)
            if (st.contains("Docker version")) {
                return true;
            }
        return false;
    }

    /**
     * Test if docker image is already present in docker
     */
    public static boolean isImageNotHere(String img) {
        ArrayList<String> s = getImages();
        for (String st:s)
            if (st.contains(img))
                return false;
        return true;
    }

    /**
     * Test if docker image is already present in docker
     */
    public static boolean isNameWellWritten(String name) {
        if (name.contains(kword) && name.matches("\\w*_"+kword+"_\\d+"))
            return true;
        else
            return false;
    }

    /**
     * Launch and set docker image
     */
    public static boolean launchDockerImage(String localpath, String dockerpath, String name, String img) {
        File f= new File(localpath);
        try {
            localpath = f.getCanonicalPath();
            if (Util.DirExists(localpath)) Util.CreateDir(localpath);
        }catch (IOException e) {
            System.out.println("Get Canonical Path Failed!");
            System.out.println(e);
            e.printStackTrace();
        }
        String c = "docker run -v "+localpath+":"+dockerpath+" --name "+name+" -di "+img;
        ArrayList<String> sl = Util.runSilentUnixCommand(c,"./");
        if (sl.size()==1 && sl.get(0).matches("\\w+")) {
            return true;
        } else if (sl.size()>1 && sl.get(sl.size()-1).matches("\\w+")){
            return true;
        }
        return false;
    }

    /**
     * Get the container name value
     */
    public static String getContainersVal(String name) {
        if (isContainersAlreadyUsed(name)) {
            String si = name;
            si = si.replaceAll(".*_(\\d+)$","$1");
            Integer i = Integer.parseInt(si);
            si = name.replaceAll("(.*_)\\d+$","$1");
            name = getContainersNextVal(si,i);
        }
        return name;
    }
    
    /**
     * Get the container name next value
     */
    public static String getContainersNextVal(String name, int i) {
        if (i>100) {
            //System.out.println("Warnings already 1000 containers have been send with this name. Please remove few of them to continue");
            return name+"_OUT";
        } else {
            if (!isContainersAlreadyUsed(name+i)) {
                return name+i;
            } else {
                i=i+1;
                return getContainersNextVal(name,i);
            }
        }
    }
    
    /**
     * Test if docker container is already launch with the same name
     */
    public static boolean isContainersAlreadyUsed(String name) {
        String c = "docker ps --filter \"name="+name+
                   "\" --format {{.Names}}";
        ArrayList<String> ls = Util.runSilentUnixCommand(c,"./");;
//        if (ls.size()==1)
            for (String s:ls)
                if (s.contains(name))
                    return true;
        return false;
    }

    /**
     * Get docker images
     */
    public static ArrayList<String> getImages() {
        ArrayList<String> s = Util.runSilentUnixCommand("docker images","./");
        ArrayList<String> l = new ArrayList<String>();
        for (String st : s)
            if (!(st.contains("REPOSITORY")))// && st.contains(kword))
                l.add(st);
        return l;
    }

    /**
     * Get docker active containers infos ID Names Image
     */
    public static ArrayList<String> getActivesContainers() {
        String c = "docker ps --format {{.ID}}<>{{.Names}}<>{{.Image}}";
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
    public static ArrayList<String> getAllContainers() {
        String c = "docker ps -a --format {{.ID}}<>{{.Names}}<>{{.Image}}";
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
    public static ArrayList<String> getActivesContainersID() {
        String c = "docker ps --format {{.ID}}<>{{.Names}}";
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
    public static ArrayList<String> getAllContainersID() {
        String c = "docker ps -a --format {{.ID}}<>{{.Names}}";
        ArrayList<String> s = Util.runSilentUnixCommand(c,"./");
        ArrayList<String> val = new ArrayList<String>();
        for(String sa:s)
            if (sa.contains(kword))
                val.add(sa.replaceAll("^(\\w+)<>\\w+","$1"));
        return val;
    }

    /**
     * Clean (stop and remove) Docker Containers List
     */
    public static boolean cleanContainers(ArrayList<String> l) {
        if (l.isEmpty())
            return false;
        else if (stopContainers(l)){
            ArrayList<String> all = getAllContainersID();
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
                String s = "docker rm "+p.toString().replaceAll("[\\[,\\]]"," ");
                ArrayList<String> v = Util.runSilentUnixCommand(s,"./");
                return Util.equalArrayLists(v,p);
            } else 
                return true;
        } else
            return false;
    }
    
    /**
     * Clean (stop and remove) Docker Containers List
     */
    public static boolean cleanContainer(String s) {
        ArrayList<String> a = new ArrayList<String>();
        a.add(s);
        return cleanContainers(a);
    }
    
    /**
     * Stop docker Container List
     */
    private static boolean stopContainers(ArrayList<String> l) {
        ArrayList<String> ia  = getActivesContainersID();
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
            String s = "docker stop "+p.toString().replaceAll("[\\[,\\]]"," ");
            ArrayList<String> v = Util.runSilentUnixCommand(s,"./");
            return Util.equalArrayLists(v,p);
        } else 
            return true;
    }
    
    /**
     * Clean Inactives Armadillo containers
     */
    public static boolean cleanInactiveContainers() {
        System.out.println("Clean Inactive Container");
        ArrayList<String> all = getAllContainersID();
        ArrayList<String> ia  = getActivesContainersID();
        
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
        if (ina.size()>0 && cleanContainers(ina)) return true;
        else return false;
    }
    
    
    /**
     * Clean Inactives Armadillo containers
     */
    public static void removeImages(ArrayList<String> l) {
        //Find container which used image names and stop, remove them
        ArrayList<String> cont = Docker.getAllContainers();
        ArrayList<String> cImg = new ArrayList<String>();
        for (String s:cont) {
            for (String imgName : l) {
                if (s.contains(imgName)){
                    cImg.add(s.substring(0,3));
                }
            }
        }
        cleanContainers(cImg);
        for (String imgName:l) {
            ArrayList<String> st = Util.runSilentUnixCommand("docker rmi "+imgName,"./");
            if (st.toString().contains("Error") && st.toString().contains("container")) {
                System.out.println("This images is already used out of Armadillo");
            }
        }
    }
    
    
    /*
            String sd, // Shared directory
            String dd, // Docker directory
            String doName // Docker name
    */
    public static boolean copyDockerDirToSharedDir (String sd, String dd, String doName) {
        String s = "docker exec -ti "+doName+" mkdir "+sd+" "
                + "&& docker exec -ti "+doName+" cp -r "+dd+" "+sd+" "
                + "&& docker exec -ti "+doName+" chmod -R 777 "+sd+" "
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
    
    public static boolean CleanContainerName(workflow_properties properties) {
        if (properties.isSet("DOCKERName")) {
            return cleanContainer(properties.get("DOCKERName"));
        }
        return true;
    }
    
}
