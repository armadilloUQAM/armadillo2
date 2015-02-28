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

package workflows;

import biologic.Biologic;
import configuration.Config;
import configuration.Util;
import java.util.Enumeration;
import java.util.Properties;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import program.RunProgram;
import program.runningThreadInterface;

/**
 * Properties of diff objects
 * Note: the properties name doesn't need to be absolutly set
 * Note: The properties_id is return by the database
 * @author Etienne Lorddel
 */
public class workflow_properties extends Properties implements Comparable {

    ///////////////////////////////////////////////////////////////////////////
    /// Variables

       private int properties_id=0;                        //Id in the database
       public String filename="";                         //Properties default filename
       public boolean modified=false;
       private Object thread=null;
       public boolean debug=true;

    ///////////////////////////////////////////////////////////////////////////
    /// Constant

       public static final String NotSet="Not Set";
       public static ArrayList ExcludeFromModified;

   ////////////////////////////////////////////////////////////////////////////
    /// Constant for properties PORT


    ///////////////////////////////////////////////////////////////////////////
    /// Database access for load and save
    //databaseFunction df=new databaseFunction(); //Database

       ///////////////////////////////////////////////////////////////////////////
    /// Constructor

    public workflow_properties() {
        super();        
        if (ExcludeFromModified==null) {
            ExcludeFromModified=new ArrayList();
            ExcludeFromModified.add("x");
            ExcludeFromModified.add("y");
        }
    }

    public workflow_properties(String filename) {
        super();
        this.load(filename);
    }

    public workflow_properties(String filename, String path) {
        super();
        this.load(filename, path);
    }

    //////////////////////////////////////////////////////////////////////////
    /// Methods

    /**
     * Load a properties file
     * Note: The Name of this properties will be set to the key("Name")
     * 
     * @param filename
     * @return true if success
     */
    public boolean load(String filename) {
    if (!Util.FileExists(filename)) return false;
    try {
        super.clear();
        this.filename=filename;
        BufferedReader br=new BufferedReader(new FileReader(new File(filename)));
        super.load(br);
        //this.name=this.get("Name"); //Get this PropertiesName from file
        br.close();
        this.put("filename", filename);
    } catch(Exception e) {
        if (debug) e.printStackTrace();
        Config.log("Error in loading properties from filename "+filename);
        return false;}
    return true;
    }


    public boolean load(String filename, String path) {
        return load(path+File.separator+filename);
    }

    /**
     * Save to defautlfilename (filename)
     * @return True if success
     */
    public boolean load() {
        String file=filename;
        if (file.isEmpty()) {
            if (this.isSet("filename")) file=this.get("filename");
        }      
        return load(file);
    }

    /**
     * Save a properties file
     * @param filename
     * @return true if success, false otherwise
     */
    public boolean save(String filename) {
    try {
        this.filename=filename;
        BufferedWriter bw=new BufferedWriter(new FileWriter(new File(filename)));
            Config config=new Config();
            store0(bw, config.get("applicationName")+" "+config.get("version")+" (c) "+config.get("authorArmadillo"), false);
            //super.store(bw, config.get("applicationName")+" "+config.get("version")+" (c) "+config.get("authorArmadillo"));
        bw.flush();
        bw.close();
        } catch(Exception e) {
            if (debug) e.printStackTrace();
            Config.log("Error in saving properties from filename "+filename);
            return false;
        }
    this.modified=false; //--Reset the changed state
    return true;
    }

     public boolean save(String filename, String path) {
        return save(path+File.separator+filename);
    }

    public boolean save() {
        if (filename.equals("")) return false;
        return save(filename);
    }

    public String serializeToString() {         
        StringWriter st=new StringWriter();
        try  {
            this.store(st, "");     //Note: No custom header for small footprint
            return st.toString();
        } catch(Exception e) {e.printStackTrace();return "";}
        
    }
    
    public void deserializeFromString(String st) {
        this.clear();
        StringReader str=new StringReader(st);
        try {
            this.load(str);
        } catch(Exception e) {e.printStackTrace();}    
    }


    /**
     * Get the value associated with a key
     * @param key
     * @return a String value or the NotSet String
     */
    @Override
    public String get(Object key) {
        try {
        String value=(String) super.get(key);
        return (value==null?NotSet:value);
        } catch(Exception e) {return super.get(key).toString();}
      }

    /**
     * Get an int value from a properties
     * @param key
     * @return the Integer value or 0 (zero) if not found.
     */
    public int getInt(Object key) {
        //--CASE 1. Already a Int in the database
        try {
          int i=(Integer)super.get(key);
            return i;
        } catch(Exception e) {
        //--CASE 2. We have a Float? Convert to int...
            try {
              int i=(int)getFloat(key);
            return i;
            } catch(Exception e3) {
             //--CASE 3. Try to convert a String to Integer
                  try {
                    int i=Integer.valueOf(((String)super.get(key)));
                    return i;
                  } catch(Exception e2) {return 0;}
                }
        }
    }

     public boolean getBoolean(Object key) {
        //--CASE 1. Already a Boolean in the database
         try {
          boolean b=(Boolean)super.get(key);
            return b;
        } catch(Exception e) {
             //--CASE 2. We have a String? Convert to boolean...
            try {
                    boolean b=Boolean.valueOf(((String)super.get(key)));
                    return b;
            } catch(Exception e2) {return false;}
        }
    }

     /**
     * Get an float value from a properties
     * @param key
     * @return the float value or 0 (zero) if not found.
     */
    public float getFloat(Object key) {
        //--CASE 1. Already a number in the database
        try {
          float i=(Float)super.get(key);
            return i;
        } catch(Exception e) {

         //--CASE 2. Try to convert a String to Float
              try {
                float i=Float.valueOf(((String)super.get(key)));
                return i;
              } catch(Exception e2) {return 0;}
        }
    }

     /**
     * Get an Double value from a properties
     * @param key
     * @return the double value or 0 (zero) if not found.
     */
    public double getDouble(Object key) {
        //--CASE 1. Already a number in the database
        try {
          double i=(Double)super.get(key);
            return i;
        } catch(Exception e) {

         //--CASE 2. Try to convert a String to Double
              try {
                double i=Double.valueOf(((String)super.get(key)));
                return i;
              } catch(Exception e2) {return 0;}
        }
    }
    
      /**
     * Get an int value from a properties
     * @param key
     * @return the Float value or 0 (zero) if not found.
     */
    public long getLong(Object key) {
        //--CASE 1. Already a number in the database
        try {
          long i=(Long)super.get(key);
            return i;
        } catch(Exception e) {

         //--CASE 2. Try to convert a String to Integer
              try {
                long i=Long.valueOf(((String)super.get(key)));
                return i;
              } catch(Exception e2) {return 0;}
        }
    }

    public ImageIcon getImageIcon(Object key) {
        if (super.get(key)==null) return null;
        return (ImageIcon)super.get(key);
    }

    /**
     * Return if a key is set
     * @param key
     * @return True if set...
     */
    public boolean isSet(Object key) {
        return (super.get(key)==null?false:true);
    }
    
    public boolean isProgram() {
        if (!isSet("ObjectType")) return false;
        return get("ObjectType").equals("Program");                
    }

    @Override
    public synchronized Object put(Object key, Object value) {
       //--Verify if the key or value is null
       if (key==null||value==null) return false;
        //--Set the changed state
        if (ExcludeFromModified.indexOf(key)==-1) modified=true;
        return super.put(key, value);
    }

    public boolean isModified() {
        return modified;
    }

      
    /**
     * Return this properties name (screen name)
     */
    public String getName() {
        return this.get("Name");
    }

     /**
      * Set this properties name (screen name)
      */

    public void setName(String name) {
        this.put("Name", name);
    }

    /**
     * Return this properties ObjectID
     */
    public String getID() {
        return this.get("ObjectID");
    }

     /**
      * Set this properties name (screen name)
      */

    public void setID(String ID) {
        this.put("ObjectID", ID);
    }


    @Override
    public String toString() {
        return "properties\t"+this.filename;
    }

    /**
     * Return this properties description
     */
    public String getDescription() {
        String desc=this.get("Description");
        if (desc.equals(NotSet)) desc="";
        return desc;
    }

     /**
     * Return this properties descrtiption
     */
    public String getTooltip() {
        String desc=this.get("Tooltip");
        if (desc.equals(NotSet)) desc="";
        return desc;
    }

    /**
     * Return a Sring representation of the properties
     * @return a String of this workflow_properties
     */
    public String getPropertiesToString() {
        StringBuilder tmp=new StringBuilder();
        Enumeration<Object> e=this.keys();
        tmp.append(this.getName()+" has "+this.size()+" key(s)\n");
        while(e.hasMoreElements()) {
              String key=(String)e.nextElement();
              tmp.append(key+"->"+get(key)+"\n");

         }
        return tmp.toString();
    }

     public static String getPropertiesToString(Properties prop) {
        StringBuilder tmp=new StringBuilder();
        Enumeration<Object> e=prop.keys();
        tmp.append(" Total: "+prop.size()+" key(s)\n");
        while(e.hasMoreElements()) {
              String key=(String)e.nextElement();
              tmp.append(key+"->"+prop.get(key)+"\n");

         }
        return tmp.toString();
    }

 /**
  * 
  * @return the number of input
  */
  public int getNbInput() {
      int nb=0;
      Enumeration<Object> e=this.keys();
       while(e.hasMoreElements()) {
              String key=(String)e.nextElement();
              //By default if its AcceptAll return 3
              if (key.equalsIgnoreCase("InputAll")) return 3;
              if (key.startsWith("Input")&&this.get(key).equalsIgnoreCase("TRUE")) nb++;
         }
       return nb;
  }

 /**
  *
  * @return the number of output
  */
  public int getNbOutput() {
      int nb=0;
      Enumeration<Object> e=this.keys();
       while(e.hasMoreElements()) {
              String key=(String)e.nextElement();
              //By default if its AcceptAll return 3
              if (key.equalsIgnoreCase("OutputAll")) return 3;
              if (key.startsWith("Output")&&this.get(key).equalsIgnoreCase("TRUE")) nb++;
         }
       return nb;
  }

  /**
   * Test if this properties can accept this input
   * @param input (ex. Alignment)
   * @return true of false
   */
  public boolean Accept(String input) {
      if (this.get("InputAll").equalsIgnoreCase("True")) return true;
      if (this.get("Input"+input).equalsIgnoreCase("True")) return true;
      if (this.get(input).equalsIgnoreCase("True")) return true;
      //--Alias --Results
      if (input.equalsIgnoreCase("text")) {
          return (Accept("Unknown")||Accept("Results"));
      } 
      return false;
  }

  /**
   * Test if this properties output this  objectt
   * @param outputtype (ex. Alignment)
   * @return true or false
   */
  public boolean Output(String output) {
      if (this.get("OutputAll").equalsIgnoreCase("True")) return true;
      if (this.get("Output"+output).equalsIgnoreCase("True")) return true;
      if (this.get(output).equalsIgnoreCase("True")) return true;
      return false;
  }
 
  /**
   * Return the String of accepted input for this properties
   * @return Vector of String of accepted input
   */
  public Vector<String> Accepted() {
      Vector<String>tmp=new Vector<String>();

      Enumeration<Object> e=this.keys();
       while(e.hasMoreElements()) {
              String key=(String)e.nextElement();
              if (key.equalsIgnoreCase("InputAll")) {
                  tmp.add("All");
              } else
              if (key.startsWith("Input")&&this.get(key).equalsIgnoreCase("TRUE")) {
                  tmp.add(key.substring(5));
              } else
               if (key.startsWith("Input")&&this.get(key).startsWith("Connector")) {
                  tmp.add(key.substring(5));
              }
         }
      
      return tmp;
  }


   /**
   * Return the string of the accepted output for this properties
   * @return Vector of String of accepted output
   */
  public Vector<String> Outputed() {
      Vector<String>tmp=new Vector<String>();
      //--Note: Special case for If object
//      if (this.get("ObjectType").equals("If")) {
//           Enumeration<Object> e=this.keys();
//           while(e.hasMoreElements()) {
//           String key=(String)e.nextElement();   
//               if (key.startsWith("output_")&&key.endsWith("_id")) {
//                   tmp.add(key.substring(7, key.length()-3));
//               }
//           }
//      } else {      
        Enumeration<Object> e=this.keys();
         while(e.hasMoreElements()) {
                String key=(String)e.nextElement();
                if (key.equalsIgnoreCase("OutputAll")) {
                    tmp.add("All");
                } else
                if (key.startsWith("Output")&&this.get(key).equalsIgnoreCase("TRUE")) {
                    tmp.add(key.substring(6));
                } else
                if (key.startsWith("Output")&&this.get(key).startsWith("Connector")) {
                    tmp.add(key.substring(6));
                } 
           }       
//      }
      return tmp;
  }

   /**
   * Return the string of the accepted input for this properties
   * @return Vector of String of accepted input
   */
  public Vector<String> Inputed() {     
      return Accepted();
  }

  /**
   * Return the value for this input 
   * @param filter (ex. Alignment)
   * @return the value associated in the properties
   */
  public String getInput(String filter) {
      return (this.get("Input"+filter));
  }

   /**
   * Return the value for this output 
   * @param filter (ex. Alignment)
   * @return the value associated in the properties
   */
  public String getOutput(String filter) {
      return (this.get("Output"+filter));
  }

  /**
   * Prefered way for and object to set an output_id
   * @param b
   * @param indice
   */

  public void setOutput(Biologic b, String indice) {
      this.put("output_"+b.getBiologicType()+"_id"+indice, b.getId());
  }

  public void setOutput(Biologic b) {
      this.put("output_"+b.getBiologicType()+"_id", b.getId());
  }

/**
 * Pefered way to get and input ID
 * Possible input (#1->port)
 *      
 *      MultipleSequences 
 *      input_multiplesequences_id
 *      input_multiplesequence_id#1
 * 
 * @param filter
 * @param port
 * @return
 */
  public Vector<Integer> getInputID(String filter, Integer port) {
      Vector<Integer>ids=new Vector<Integer>();      
      //--Uniform search pattern
      Pattern search;
      if (!Pattern.matches("input_(.*)_id.*", filter)) {
           search=Pattern.compile("input_"+filter+"_id"+(port==null?"":port), Pattern.CASE_INSENSITIVE);
      } else {
           search=Pattern.compile(filter+(port==null?"":port), Pattern.CASE_INSENSITIVE);
      }
      //--Search    
      Object[] o=keySet().toArray();
      for (int i=0; i<o.length; i++) {
          Object k=o[i];
          String key=(String)k;
          Matcher m=search.matcher(key);
          if (m.find()) ids.add(getInt(key));
      }
      return ids;
  }

  private  Vector<Integer> getInputID() {
      Vector<Integer>ids=new Vector<Integer>();      
      //--Uniform search pattern
      Pattern search;
      search=Pattern.compile("input_(.*)_id.*", Pattern.CASE_INSENSITIVE);
      //--Search
      Object[] o=keySet().toArray();
      for (int i=0; i<o.length; i++) {
          Object k=o[i];
          String key=(String)k;
          Matcher m=search.matcher(key);
          if (m.find()) ids.add(getInt(key));
      }

      return ids;
  }

  public boolean isAllValidInput() {
      for (int id:getInputID()) {
          if (id==0) return false;
      }
      return true;
  }

  public Vector<Integer> getOutputID(String filter, Integer port) {
      Vector<Integer>ids=new Vector<Integer>();
      //--Uniform search pattern
      Pattern search;
      if (!Pattern.matches("output_(.*)_id.*", filter)) {
           search=Pattern.compile("output_"+filter+"_id"+(port==null?"":port), Pattern.CASE_INSENSITIVE);
      } else {
           search=Pattern.compile(filter+(port==null?"":port), Pattern.CASE_INSENSITIVE);
      }
      Object[] o=keySet().toArray();
      if (o.length==0) System.out.println(keySet());
      //--Search
      for (int i=0; i<o.length; i++) {
          Object k=o[i];
          String key=(String)k;
          Matcher m=search.matcher(key);
          if (m.find()) ids.add(getInt(key));
      }

      return ids;
  }

  /**
   * This remove all output from this properties
   * ex. output_multiplesequences_id0, output_sequence_id0....
   * 
   */
  public void removeOutput() {
      Pattern search=Pattern.compile("output_(.*)_id.*", Pattern.CASE_INSENSITIVE);
      Vector<String> skeySet=new Vector<String>();
       for (Object k:this.keySet()) skeySet.add((String)k);
      for (String k:skeySet) {
          String key=(String)k;
          Matcher m=search.matcher(key);
          if (m.find()) remove(key);
      }
  }
  
   /**
   * This remove all output type from this properties
   * ex. OutputMultiplesequences
   * Note: needed by If..
   */
  public void removeOutputType() {
      Pattern search=Pattern.compile("Output*", Pattern.CASE_INSENSITIVE);
      Vector<String> skeySet=new Vector<String>();
       for (Object k:this.keySet()) skeySet.add((String)k);
      for (String k:skeySet) {
          String key=(String)k;
          Matcher m=search.matcher(key);
          if (m.find()) remove(key);
      }
  }

  /**
   * This remove all output from this properties
   * ex. output_multiplesequences_id0, output_sequence_id0....
   */
  public void removeInput() {
      Pattern search=Pattern.compile("input_(.*)_id.*", Pattern.CASE_INSENSITIVE);
      Vector<String> skeySet=new Vector<String>();
       for (Object k:this.keySet()) skeySet.add((String)k);
      for (String k:skeySet) {
          String key=(String)k;
          Matcher m=search.matcher(key);
          if (m.find()) remove(key);
      }
  }

   /**
   * Normal fucntion to get the output in a program
   * @param filter (ex. output_multipletrees_id)
   * @return
   */
  public int getOutputID(String filter) {
      Vector<Integer> id=this.getOutputID(filter,null);
      if (id.size()==0) return 0;
      return id.get(0);
  }


  /**
   * Normal fucntion to get the input in a program
   * Example: .getInputID("input_multiplesequences_id");
   * @param filter (ex. input_multipletrees_id)
   *
   * @return
   */
  public int getInputID(String filter) {
      Vector<Integer> id=this.getInputID(filter,null);
      if (id.size()==0) return 0;
      return id.get(0);
  }

  public void setInput(Biologic bio) {
      String type=bio.getBiologicType();
      int id=bio.getId();
      this.put("input_"+type.toLowerCase()+"_id",id);      
  }
  
  public void setInput(Biologic bio, int port) {
      String type=bio.getBiologicType();
      int id=bio.getId();
      this.put("input_"+type.toLowerCase()+"_id"+port,id);      
  }
  
  //////////////////////////////////////////////////////////////////////////////
  ///  getter / setter 

 
   public void setExecutable(String Executable) {
        put("Executable", Executable);
    }
   
   public void setExecutableMacOSX(String Executable) {
        put("ExecutableMacOSX", Executable); 
    }
   
   public void setExecutableLinux(String Executable) {
       put("ExecutableLinux", Executable); 
   }
   
    /**
    * This set the Alternative executable      
    */
   public void setAlternative(String alternative) {
        put("AlternativeExecutable", alternative);
    }    
 
    /**
    * This get the Alternative executable
    * @return 
    */
    public String getAlternative() {
        return get("AlternativeExecutable");
    }
      
    public String getExecutable() {
        return get("Executable");
    }

    public String getExecutableLinux() {
        return get("ExecutableLinux");
    }
    
    public void setLinux(String Executable) {
        put("ExecutableLinux", Executable);
    }
    
    public String getExecutableMacOSX() {
        return get("ExecutableMacOSX");
    }
    
    public void setMACOSX(String Executable) {
        put("ExecutableMacOSX", Executable);
    }
    
    public void setCommandline(String commandline) {
        put("CommandLine", commandline);
    }

    public String getCommandline() {
        return get("CommandLine");
    }

    public void setPath(String path) {
        put("path", path);
    }

    public String getPath() {
        return get("path");
    }
 

////////////////////////////////////////////////////////////////////////////////
/// Helper file functions

/**
 * Return a string array of the Properties in the specify directory or null if not found
 */
public static String[] loadPropertieslisting (String path) {
  FilenameFilter filter=new FilenameFilter() {
  public boolean accept(File dir, String name) {
  if (name.charAt(0) == '.') return false;
  if (name.toLowerCase().endsWith(".properties")) return true;
  return false;
  }
  };
  File dataFolder = new File(path);
  String[] names = dataFolder.list(filter);
  if (names==null) names=new String[0];
  return names;
}

/**
 * Return a string array of the Class in the specify directory or null if not found
 * Note: filename will not include path
 */
public static String[] loadClasslisting (String path) {
  FilenameFilter filter=new FilenameFilter() {
  public boolean accept(File dir, String name) {
  if (name.charAt(0) == '.') return false;
  if (name.toLowerCase().endsWith(".class")) return true;
  return false;
  }
  };
  File dataFolder = new File(path);
  String[] names = dataFolder.list(filter);
  if (names==null) names=new String[0];
  return names;
}

public Vector<String> UniqueKeyword() {
    workflow_properties_dictionnary dict=new workflow_properties_dictionnary();
    Vector<String>keyword=new Vector<String>();
    for (Object key:this.keySet()) {
        if (!dict.isKeyword(key)) keyword.add((String)key);
    }
    return keyword;
}

public static Object newObject(String ClassName) {
    Object T=null;
    try {
    Class n=Class.forName(ClassName);
    T=n.newInstance();
    return T;
    } catch(Exception e) {
        Config.log("Unable to find "+ClassName);
        return null;}

}

    /**
     * @return the properties_id
     */
    public int getProperties_id() {
        return this.getInt("properties_id");
    }

    /**
     * @param properties_id the properties_id to set
     */
    public void setProperties_id(int properties_id) {
        this.put("properties_id", properties_id);
    }

    public void killThread() {
        try {
            if (getThread()!=null&&getThread() instanceof runningThreadInterface) {
                ((RunProgram)thread).KillThread();
            }
        } catch(Exception e) {Config.log("Error in closing thread for "+this.getName());}     
    }

    /**
     * @return the thread
     */
    public Object getThread() {
        return thread;
    }

    /**
     * @param thread the thread to set
     */
    public void setThread(Object thread) {
        this.thread = thread;
    }

    public ArrayList<String>getThreadOutput() {
        ArrayList<String>tmp=new ArrayList<String>();
        try {
            if (getThread()!=null&&getThread() instanceof runningThreadInterface) {
                tmp=((runningThreadInterface)thread).getOutputTXT();
            }
        } catch(Exception e) {Config.log("Unable to get thread for properties "+getName());}
        return tmp;
    }

    public int compareTo(Object o) {
        if (!(o instanceof workflow_properties)) return -1;
        workflow_properties p=(workflow_properties)o;
        return this.filename.compareTo(p.filename);
    }

     /**
     * Set the status of the RunProgram
     * @param statusCode (see list on top)
     * @param msg
     */
     public void setStatus(int statusCode, String msg) {
          synchronized(this) {
            put("StatusString", msg);
            put("Status", statusCode);
          }
    }

     public int getStatus() {
         synchronized(this) {
            return getInt("Status");
         }
     }

     public String getStatusString() {
         return get("StatusString");
     }

     public void removeStatus() {
         synchronized(this) {
            remove("Status");
            remove("StatusString");
            remove("ExitValue");
         }
     }

////////////////////////////////////////////////////////////////////////////////
///
///  IMPORTANT    IMPORTANT     IMPORTANT       IMPORTANT       IMPORTANT
///  All the following code is from:    
///     
///   * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
///   * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
///   
///   It comes from Properties.java     
////////////////////////////////////////////////////////////////////////////////
     
    /**
     * From java source code
     * (c) Oracle 2006
     *    
     * Convert a nibble to a hex character
     * @param	nibble	the nibble to convert.
     */
    private static char toHex(int nibble) {
	return hexDigit[(nibble & 0xF)];
    }
    
    /**
     * From java source code
     * (c) Oracle 2006
     */    
      /** A table of hex digits */
    private static final char[] hexDigit = {
	'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };
    
    /**
     * From java source code
     * (c) Oracle 2006
     */    
    private static void writeComments(BufferedWriter bw, String comments) 
        throws IOException {
        bw.write("#");
        int len = comments.length();  
        int current = 0;
        int last = 0;
        char[] uu = new char[6];
        uu[0] = '\\';
        uu[1] = 'u';
        while (current < len) {
            char c = comments.charAt(current);
	    if (c > '\u00ff' || c == '\n' || c == '\r') {
	        if (last != current) 
                    bw.write(comments.substring(last, current));
                if (c > '\u00ff') {
                    uu[2] = toHex((c >> 12) & 0xf);
                    uu[3] = toHex((c >>  8) & 0xf);
                    uu[4] = toHex((c >>  4) & 0xf);
                    uu[5] = toHex( c        & 0xf);
                    bw.write(new String(uu));
                } else {
                    bw.newLine();
                    if (c == '\r' && 
			current != len - 1 && 
			comments.charAt(current + 1) == '\n') {
                        current++;
                    }
                    if (current == len - 1 ||
                        (comments.charAt(current + 1) != '#' &&
			comments.charAt(current + 1) != '!'))
                        bw.write("#");
                }
                last = current + 1;
	    } 
            current++;
	}
        if (last != current) 
            bw.write(comments.substring(last, current));
        bw.newLine();
    }
    
     /**
     * From java source code
     * (c) Oracle 2006
     * Converts unicodes to encoded &#92;uxxxx and escapes
     * special characters with a preceding slash
     */
    private String saveConvert(String theString,
			       boolean escapeSpace,
			       boolean escapeUnicode) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuffer outBuffer = new StringBuffer(bufLen);

        for(int x=0; x<len; x++) {
            char aChar = theString.charAt(x);
            // Handle common case first, selecting largest block that
            // avoids the specials below
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\'); outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch(aChar) {
		case ' ':
		    if (x == 0 || escapeSpace) 
			outBuffer.append('\\');
		    outBuffer.append(' ');
		    break;
                case '\t':outBuffer.append('\\'); outBuffer.append('t');
                          break;
                case '\n':outBuffer.append('\\'); outBuffer.append('n');
                          break;
                case '\r':outBuffer.append('\\'); outBuffer.append('r');
                          break;
                case '\f':outBuffer.append('\\'); outBuffer.append('f');
                          break;
                case '=': // Fall through
                case ':': // Fall through
                case '#': // Fall through
                case '!':
                    outBuffer.append('\\'); outBuffer.append(aChar);
                    break;
                default:
                    if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode ) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >>  8) & 0xF));
                        outBuffer.append(toHex((aChar >>  4) & 0xF));
                        outBuffer.append(toHex( aChar        & 0xF));
                    } else {
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }

     /**
     * From java source code
     * (c) Oracle 2006     
     */
      public void store(Writer writer, String comments)
        throws IOException
    {
        store0((writer instanceof BufferedWriter)?(BufferedWriter)writer
	                                         : new BufferedWriter(writer),
	       comments,
	       false);
    }
    
    /**
     * From java source code
     * (c) Oracle 2006
     * @param bw
     * @param comments
     * @param escUnicode
     * @throws IOException 
     */        
    private void store0(BufferedWriter bw, String comments, boolean escUnicode)
        throws IOException
    {
        if (comments != null) {
            writeComments(bw, comments);
        }
        bw.write("#" + new Date().toString());
        bw.newLine();
	synchronized (this) {
            ArrayList l=new ArrayList(this.keySet());
            Collections.sort(l);            
            for (Object keyO:l) {
                String key = (String)keyO;
		String val = (String)get(key);
                key = saveConvert(key, true, escUnicode);
		/* No need to escape embedded and trailing spaces for value, hence
		 * pass false to flag.
		 */
		val = saveConvert(val, false, escUnicode);
		bw.write(key + "=" + val);
                bw.newLine();
            }            
	}
        bw.flush();
    }
     
    /**
      * Debug in properties
      * @return 
      */
     public boolean isDebug() {
         return getBoolean("debug");
     }
     
     public boolean SaveAsXML(String filename) {
         try {
             FileOutputStream out=new FileOutputStream(new File(filename));
             this.storeToXML(out, "");
             return true;
         } catch(Exception e) {return false;}
     }
     
} //End workflow_properties

