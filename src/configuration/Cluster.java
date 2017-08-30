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

/**
 * Collection of util command
 * @author Jérémy Goimard
 */
public class Cluster {

    public static int count=0; //--Internal variable for returnCount...
    public static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat dateyear = new SimpleDateFormat("yyyy");
    //--Logging function
    public PrintWriter log;
    
    public Cluster() {}
    
    /***************************************************************************
     * CLUSTER FUNCTIONS
     **************************************************************************/
    
    /**
     * Test if python script is here
     */
    public static boolean isClusterPythonScriptHere(workflow_properties properties) {
        File f = new File("./cluster.py");
        return f.exists();
    }
    
    /**
     * Test if cluster program is installed
     */
    public static boolean isClusterPgrmNameHere(workflow_properties properties) {
        if (properties.isSet("ClusterPgrmName"))
            return true;
        return false;
    }
    
    public static String ClusterAccessAddress(Workbox workbox) {
        workflow_properties p2 = workbox.getWorkFlowJInternalFrame().getProperties();
        String clusterAA       = " -clus "+p2.get("ClusterAccessAddress");
        return clusterAA;
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

    public static boolean isDockerHere(workflow_properties properties) {
        return true;
    }
    
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
        workflow_properties p2 = workbox.getWorkFlowJInternalFrame().getProperties();
        String clusterAA       = " -clus "+p2.get("ClusterAccessAddress");
        String str =properties.getPropertiesToVarStringWithEOL();
        str = "\'"+str+"\'";
        str = str.replaceAll(" ", "_____");
        String commands = " -obj "+str+clusterAA;
        return commands;
    }
    

}
