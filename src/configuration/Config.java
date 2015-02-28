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

import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.apache.commons.lang.SystemUtils;
import workflows.workflow_properties;


/**
 * Class Config : Class containing the main program configuration path, filename, icon, etc.
 *
 * @author Alix Boc, Etienne Lord, Mickael Leclercq, Abdoulaye Baniré Diallo
 */
public class Config {

    public static workflow_properties properties;
    
    ////////////////////////////////////////////////////////////////////////////
    /// Global constant

    //-- Mode : allow stdout 
    public static boolean library_mode=false; //Flag to tell armadillo as a library
    //-- Mode : in memory database
    public static boolean nodatabase=false;   //Flag to tell armadillo oto use an in memory database
                                              // Usefull for website...
    
    
    //-- Default log file (can be changed) January 2011
    //-- Required to used as Library sometimes...
    public static String log_file="armadillo.log";
    public static boolean nolog=false; //--do not log to file
    
    
    //-- Status code of the various programs

    public static final int status_nothing=0;
    public static final int status_idle=1;
    public static final int status_changed=10;
    public static final int status_done=100;
    public static final int status_error=404; 
    public static final int status_running=500;

    public static String currentPath="";

    //////////////////////////////////////////////////////////////////////////
    /// Command-line Arguments
    ///
    /// Note: this can be used in commandLine mode to pass files as STDINPUT
    public static String[] cmdArgs={};

    ///////////////////////////////////////////////////////////////////////////
    //Clustering Option
    public static String[] ClusteringOption={"ALL","RefSeq","cDNA","EST","mRNA","CDS","Homo Sapiens","Mus musculus","<1000",">500",""};

    ////////////////////////////////////////////////////////////////////////
    /// Interface for Search
    public static String[] ensembldb={"c_elegans","c_intestinalis","chicken","chimp","cow","dog","fruitfly","fugu","honeybee","human","macaque","mosquito","mouse","opossum","rat","s_cerevisiae","tetraodon","x_tropicalis","zebrafish"}; //List of database used by the All ensmbl option in Search

   
    ////////////////////////////////////////////////////////////////////////////
    /// Application icon
   
    public static Image image=null;
    public static ImageIcon icon=null;
    public String defaultSaveFile="config.dat";
    public static PrintWriter log=null;

    public Config()  {
       if (properties==null) {
           
           
            currentPath=new File("").getAbsolutePath();
            properties=new workflow_properties();            
            if (!Load()) {
                 setDefaultProperties();
                Save();
            } else {
                //--Test path
                if (!Util.FileExists(this.databasePath())) {
                    //--Case 1. test default path
                    if (Util.FileExists( currentPath+File.separator+"projects"+File.separator+"Untitled.db")) {
                        //Reset path to database                    
                         setDefaultProperties();
                        Save();
                    }
                }
            }
            //--Set compiler properties
            try {
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                set("CompilerFound", (compiler==null?false:true));
               // System.out.println("Testing Java compiler: "+isCompilerFound());
            } catch(Exception e) {
                Config.log("Unable to determine compiler: "+e.getMessage());
            }            
            // -- Get armadillo icon
           if (image==null) loadIcon();
            if (log==null) {
                try {
                log=new PrintWriter(new FileWriter(new File(log_file),true));
                } catch(Exception e) {
                    //Config.log("Unable to open log file: "+log_file);
                    return;}
            }       
       }
    }
 
    /**
     * Log to the log file (armadillo.log) some information
     * Note: not boolean beacause of compilation error (?)
     * @param s 
     */
    public static void log(String s) {
        if (nolog) {
            System.out.println(s);
            return; //--Nothing else to do
        }
        if (log==null) {
                try {
                log=new PrintWriter(new FileWriter(new File(log_file),true));
                log.flush();
                } catch(Exception e) {
                    //System.out.println("Unable to open log file: "+log_file);
                    return ;}
            }        
        try {
            log.println(s);
            if (library_mode) System.out.println(s);
            log.flush();            
        } catch(Exception e) {
            try {
            log=new PrintWriter(new FileWriter(new File(log_file),true));
            if (library_mode) System.err.println(e);
            log.println(e);
            log.flush();
            } catch(Exception e2) {return ;}
        }        
    }

    public void loadIcon() {
        //-- Load Armadillo Icon
        File tmp=new File(get("smallIconPath"));
                    try {
                    icon = new ImageIcon(tmp.toURI().toURL());
                    image = icon.getImage();
         
        //--Load Database Tree Explorer icon
             String[] iconName=loadImageslisting(iconsPath());
             for (String filename:iconName) {
                 if (filename.indexOf("icons")>-1) {
                     tmp=new File(iconsPath()+File.separator+filename);
                     ImageIcon tmpImage=new ImageIcon(tmp.toURI().toURL());
                     filename=filename.substring(filename.indexOf(".")+1,filename.indexOf("_"));
                     properties.put(filename, tmpImage);
                 }
             }
                        
        } catch(Exception e) {library_mode=true;}
    } 
  
     /**
     * Load a properties file
     * Note: The Name of this properties will be set to the key("Name")
     *
     * @param filename
     * @return true if success
     */
    public boolean Load(String filename) {
    try {
        BufferedReader br=new BufferedReader(new FileReader(new File(filename)));
        properties.load(br);
        if (isSet("log")) log_file=get("log");
        br.close();
        //Config.log("Loading of config file "+filename+" successfull.");
    } catch(Exception e) {
        return false;}
    return true;
    }

    public boolean Load() {
        return Load(defaultSaveFile);
    }

    /**
     * Save a properties file
     * @param filename
     * @return true if success, false otherwise
     */
    public boolean Save(String filename) {
    try {
        properties.save(filename);
//        BufferedWriter bw=new BufferedWriter(new FileWriter(new File(filename)));
//            properties.store(bw, get("applicationName")+" "+get("version")+" (c) "+get("authorArmadillo"));
//        bw.flush();
//        bw.close();
        //Config.log("Saving of config file "+filename+" successfull.");
        } catch(Exception e) {return false;}
    return true;
    }

    public boolean Save() {
        return Save(defaultSaveFile);
    }

     /**
      * This set the default configuration properties
      */
     public void setDefaultProperties() {  
        //--Ask to set Email and other helpfull


         //--Path
         set("Name","Configuration file (config.dat)");
         set("currentPath", currentPath);
         //==
         //==Note:
         //==This databasePath represent the default project filename
         set("databasePath", currentPath+File.separator+"projects"+File.separator+"New_Untitled.db");
         set("propertiesPath",currentPath+File.separator+"data"+File.separator+"properties"); // Reflect armadillo properties path
         set("classPath",currentPath+File.separator+"build"+File.separator+"classes"+File.separator+"programs"); //Running program class
         set("editorPath",currentPath+File.separator+"build"+File.separator+"classes"+File.separator+"editors"); //editor class
         set("dataPath",currentPath+File.separator+"data");
         set("ExecutableDir",currentPath+File.separator+"Executable");
         set("tmpDir", currentPath+File.separator+"tmp");
         set("temporaryDir", currentPath+File.separator+"tmp"+File.separator+"temporary");
         set("resultsDir", currentPath+File.separator+"results");
         set("testDir", currentPath+File.separator+"test");
         set("iconsPath",currentPath+File.separator+"data"+File.separator+"icons");
         set("projectsDir", currentPath+File.separator+"projects"); // Projects Directory
         set("hgncDataPath", dataPath()+File.separator+"hgnc.txt"); //default hgnc database...
         set("hgncWebDownload", "http://www.genenames.org/cgi-bin/hgnc_downloads.cgi?title=HGNC+output+data&hgnc_dbtag=on&preset=all&status=Approved&status=Entry+Withdrawn&status_opt=2&level=pri&=on&where=&order_by=gd_app_sym_sort&limit=&format=text&submit=submit&.cgifields=&.cgifields=level&.cgifields=chr&.cgifields=status&.cgifields=hgnc_dbtag");
         set("log",currentPath+File.separator+"armadillo.log");
         
         //--Try to create the tempDir (temporary file directory) if doesn't exists
         createDir(get("tmpDir"));
         createDir(get("resultsDir"));
         createDir(get("projectsDir"));
         try {
             if (Util.FileExists(dataPath("New_Untitled_default.db"))) {
                Util.copy(dataPath("New_Untitled_default.db"), this.projectsDir()+File.separator+"New_Untitled.db");
             } else {
                 log("Unable to find the default project file...");                 
             }
         } catch(Exception e) {}
         createDir(get("temporaryDir"));

         //--Version and Other
         set("authorArmadillo","Etienne Lord, Mickael Leclercq, Alix Boc,  Abdoulaye Baniré Diallo, Vladimir Makarenkov");
         set("version","2.0");
         set("applicationName","Armadillo Workflow Platform");
         set("webpageArmadillo","http://adn.bioinfo.uqam.ca/armadillo/"); //--Armadillo default webpage
         set("webserverArmadillo","http://trex.uqam.ca/armadillo/");      //--Armadillo webserver
         set("helpArmadillo","http://adn.bioinfo.uqam.ca/armadillo/wiki/index.php/Main_Page"); //--Armadillo default help page
         set("getting_startedArmadillo", dataPath()+File.separator+"Documents"+File.separator+"getting_started.html"); //--Getting started page shown at start up
         set("splashIconPath",currentPath+File.separator+"data"+File.separator+"splash1.png");
         set("smallIconPath",currentPath+File.separator+"data"+File.separator+"armadillo.png");
         set("imagePath",currentPath+File.separator+"data"+File.separator+"images");
         set("imageNcbiLoading",currentPath+File.separator+"data"+File.separator+"LoadingNcbi.jpg");
         set("imageEnsemblLoading",currentPath+File.separator+"data"+File.separator+"LoadingEnsembl.jpg");
         set("imageSequenceLoading",currentPath+File.separator+"data"+File.separator+"LoadingSequence.jpg");

         //--Editor version and name
         set("editorApplicationName","Armadillo Editor");
         set("editorVersion","0.1");
         set("editorAuthor","Etienne Lord, Mickael Leclercq");         
         set("workflow_w",2000); //Initial workflow size
         set("workflow_h",600);
         set("font_size_adjuster",0); //--Increase of decrease workflow font size;
         set("FirstTime",1);

         //--Special (Tree Editor, etc...)
         set("LoadPhyloWidget", false);
         //--By default, display the Start page
         set("DisplayStartPage", true);
         
         //--Programs
          currentPath=new File("").getAbsolutePath();
        //Databases used
        properties.put("HGNCData",currentPath+File.separator+"data"+File.separator+"hgnc.txt");
        properties.put("Tooltip",currentPath+File.separator+"data"+File.separator+"tooltips.csv");
        //properties.put("iso",currentPath+"//data//iso.txt");
        properties.put("EnsemblDb",currentPath+File.separator+"data"+File.separator+"EnsemblDB.tsv");
        //Others
        properties.put("defaultSplashPath",currentPath+File.separator+"data"+File.separator+"splash"+File.separator);
     
        properties.put("urlToModelTest","http://www.hiv.lanl.gov/content/sequence/findmodel/findmodel.html");
        properties.put("download_genbank", false);
        properties.put("debugEditor", false);
        
        //--System environment specific options
        if(SystemUtils.IS_OS_MAC_OSX||SystemUtils.IS_OS_MAC) {
            properties.put("MacOSX", true);
            properties.put("font_size_adjuster", -1);
        } else
        if(SystemUtils.IS_OS_LINUX||SystemUtils.IS_OS_UNIX) {
            properties.put("Linux", true);
        }
        
        
     }

 

 ///////////////////////////////////////////////////////////////////////////////
 /// Defautl Getter / Setter

 public String dataPath() {
     return this.get("dataPath");
 }

  public String dataPath(String filename) {
     if (!Util.DirExists("data")) {
         this.createDir("data");
         set("dataPath","data");         
     }
      return this.get("dataPath")+File.separator+filename;
 }
  
 public String propertiesPath() {
     return this.get("propertiesPath");
 }
 public String iconsPath() {
     return this.get("iconsPath");
 }
 public String imagePath() {
     return this.get("imagePath");
 }

 public String tmpDir() {
     return this.get("tmpDir");
 }

 public String resultsDir() {
     return this.get("resultsDir");
 }

  public String ExecutableDir() {
     String dir= this.get("ExecutableDir");
     return dir;
 }

 public String testDir(){
     return this.get("testDir");
 }

 public String projectsDir(){
     return this.get("projectsDir");
 }

public String databasePath() {
    return this.get("databasePath");
}
public String temporaryDir() {
     return this.get("temporaryDir");
 }
public void temporaryDir(String dir) {
    //--Delete old if exists
    if (Util.FileExists(temporaryDir())) {
        Util.deleteFile(temporaryDir());
    }
    String directory="tmp"+File.separator+dir;
    if (!Util.FileExists(directory)) this.createDir(directory);
    set("temporaryDir",directory);
}

public boolean isDevelopperMode() {
    return this.getBoolean("DeveloperMode");
}

public String currentPath() {
    return this.get("currentPath");
}

public boolean isCompilerFound() {
    return this.getBoolean("CompilerFound");
}

  /**
   * Normal getter for config option
   * @param key
   * @return
   */
   public String get(Object key) {
        String value=(String) properties.get(key);
        return (value==null?"Key: "+key+" not found.":value);
      }

   public ImageIcon getIcon(Object key) {
       return properties.getImageIcon(key);
   }

   public Boolean getBoolean(Object key) {
       return properties.getBoolean(key);
   }

   public Integer getInt(Object key) {
       return properties.getInt(key);
   }

   /**
    * Normal setter for config option
    * @param key
    * @param value
    */
   public void set(Object key, Object value) {
       try {
        properties.put(key, value);
       } catch(Exception e) {}
   }

   /**
    * Normal remove
    * @param key
    *
    */
   public void remove(Object key) {
       try {
        properties.remove(key);
       } catch(Exception e){}
   }

   public void createDir(String path) {
       File dir=new File(path);
       //1.5 created if
       try {
        if (!dir.exists()||!dir.isDirectory()) {
           if (!dir.mkdirs()) {
               Config.log("Unable to create "+path);
            }
        }
       } catch(Exception e) {Config.log("Unable to create "+path+ " directory");}
   }

//   public static boolean changeDir(String path) {
//       try {
//           String[] command={"cmd.exe","/C","CD","\""+path+"\""};
//           for(String s:command) Config.log(s+"\t");
//           Runtime r = Runtime.getRuntime();
//           Process p = r.exec(command);
//           int result=p.waitFor();
//           File f=new File("");
//           Config.log(path+" : "+f.getAbsolutePath());
//           return result==0;
//       } catch(Exception e) {e.printStackTrace();return false;}
//   }

      /**
 * Return a string array of the PNG in the specify directory or null if not found
 */
    public String[] loadImageslisting (String path) {
      FilenameFilter filter=new FilenameFilter() {
      public boolean accept(File dir, String name) {
      if (name.charAt(0) == '.') return false;
      if (name.toLowerCase().endsWith(".png")) return true;
      return false;
      }
      };
      File dataFolder = new File(path);
      String[] names = dataFolder.list(filter);
      return names;
    }

    /**
     * List the files in directory
     * @return
     */
    public static Vector<String> listDir(String dir) {
        Vector<String> tmp=new Vector<String>();
        try {
            File f=new File(dir);
            if (!f.isDirectory()) {
                dir+=File.separator;
                f=new File(dir);
            }
            for (String filename:f.list()) tmp.add(filename);
        } catch(Exception e) {}
        return tmp;
    }

    /**
     * List the files in directory and return filename with the full path
     * @return
     */
    public static Vector<String> listDirWithFullPath(String dir) {
        Vector<String> tmp=new Vector<String>();
        try {
            File f=new File(dir);
            if (!f.isDirectory()) {
                dir+=File.separator;
                f=new File(dir);
            }
            for (String filename:f.list()) tmp.add(f.getAbsolutePath()+File.separator+filename);
        } catch(Exception e) {}
        return tmp;
    }
    
    /**
     * List the files in the current directory
     * @return
     */
    public static Vector<String> listCurrentDir() {
        String filename=(new File("").getAbsolutePath())+File.separator;
        return listDir(filename);
    }

     /**
     * @return the explorerPath
     */
    public String getExplorerPath() {
        return (properties.isSet("explorerPath")?properties.get("explorerPath"):"");
    }

    /**
     * @param aExplorerPath the explorerPath to set
     */
    public void setExplorerPath(String aExplorerPath) {
        properties.put("explorerPath",aExplorerPath);
        this.Save();
    }
    
    /**
     * This get the last Workflow Database loaded...
     * @return the explorerPath
     */
    public String getLastProject() {
        return (properties.isSet("lastWorkflow")?properties.get("lastWorkflow"):"");
    }

    /**
     * This get the last Workflow Database loaded...
     * @param aExplorerPath the explorerPath to set
     */
    public void setLastWorkflow(String aLastWorkflow) {
        properties.put("lastWorkflow",aLastWorkflow);
        this.Save();
    }

    public boolean isSet(Object key) {
        return properties.isSet(key);
    }
}
