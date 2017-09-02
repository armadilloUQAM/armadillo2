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

import Class.Classdata;
import configuration.Config;
import configuration.Util;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarFile;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 * This is a repository listing KeyWord and default values
 * This also handle how specific properties are handled
 * This also handle what a connector can accept or not...
 * @author Etienne Lord
 * @since July 2009
 */
public class workflow_properties_dictionnary extends SecurityManager {
    
    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    
    public static HashMap<String,Method> keyword=new HashMap<String,Method>();
    
    
    public static Vector<String> programs; //--This is a list of the valid programs class
    public static Vector<String> editors; //--This is a list of the valid editors class
    public static Vector<String> classname=new Vector<String>(); //--Valid class
    
    
    static JComboBox TrueFalsecomboBox = new JComboBox();
    static DefaultCellEditor TrueFalseCellEditor;
    static DefaultCellEditor TextCellEditor;
    static boolean initialize=false;
    Config config=new Config();
    
    //-- Method
    Method isPositiveInteger;
    Method isPositiveFloat;
    Method isInteger;
    Method isBoolean;
    Method isValidClass;
    Method isValidEditor;
    Method isValidExecutable;
    Method isValidOS;
    Method isText;
    Method isPath;
    Method nbInput;
    Method isValidColor;
    Method isValidConnector;
    Method isValidObjectType;
    Method isValidInputOutputType;
    
    ////////////////////////////////////////////////////////////////////////////
    /// Constants
    /// Warning: Some constant are set in the constructor
    public static final String NotSet="Not Set";
    public final static String[] colorMode={"BLUE","RED","GREEN","ORANGE","PURPLE","CYAN"};
    //--This represent the input-output type (this need to be loaded in config...)
    public static String[] InputOutputType={
        "Alignment","Ancestor","ArffFile",
        "BamFile","BananaFile","Blast","BlastDB","BlastHit",
        "ChipsFile","CsvFile",
        "DatFile","Database","DiffseqFile",
        "EinvertedFile","EmblFile","Est2genomeFile",
        "FastaFile","FastqFile","FileFile","File",
        "GcgFile","Genome","GenomeFile",
        "HTML",
        "ImageFile",
        "List",
        "MAF","MAQ","MultipleSequences","MultipleTrees","Matrix",
        "NbrfFile",
        "OutputText",
        "PdbFile","Phylip_Seqboot","Phylip_Distance","PirFile","Position",
        "Results","RootedTree",
        "Sequence","SOLIDFile","SamFile","SwissprotFile",
        "Text","TextFile","Tree",
        "Unknown","UnrootedTree",
        "WekaModelFile"
    };
    public static HashMap<String,String> lowercaseInputOutputType=new HashMap<String,String>(); // Match between lowercase type (used in workflow_properties) and actual InputOutputType
    
    //--This represent ObjectType
    //public final static String[] ObjectType={"Program", "ProgramPhylip","Script","ScriptBig","Begin", "End","While","For","If"};
    //--Strip down - Etienne October 2009
    public final static String[] ObjectType={"Program", "Script","ScriptBig","Begin","For","OutputDatabase","If","Aggregate"};
    //--This represent the Number of Input (for editor)
    //--Note: Order is important for InputNumber since we use the index number in the interface
    public final static String[] InputNumber={"No Input","1 Input (default)","2 Input"};
    
    ////////////////////////////////////////////////////////////////////////////
    /// Constructor
    
    public workflow_properties_dictionnary() {
        if (!initialize) {
            //--Initialize class listing (Should not be changed during this program execution)
            programs=getClasslistingString("programs"); //--Running class
            editors=getClasslistingString("editors"); //--Running editors
            //--Initialize comparative types for matching InputOutput in properties
            for (String s:InputOutputType) {
                lowercaseInputOutputType.put(s.toLowerCase(),s);
            }
            
            try {
                //--Debug listDeclaredMethod(workflow_properties_dictionnary.class);
                isText=getDeclaredMethod(workflow_properties_dictionnary.class,"isText");
                isPositiveInteger=getDeclaredMethod(workflow_properties_dictionnary.class,"isPositiveInteger");
                isPositiveFloat=getDeclaredMethod(workflow_properties_dictionnary.class,"isPositiveFloat");
                isInteger=getDeclaredMethod(workflow_properties_dictionnary.class,"isInteger");
                isBoolean=getDeclaredMethod(workflow_properties_dictionnary.class,"isBoolean");
                isValidClass=getDeclaredMethod(workflow_properties_dictionnary.class,"isValidClass");
                isValidEditor=getDeclaredMethod(workflow_properties_dictionnary.class,"isValidEditor");
                isValidExecutable=getDeclaredMethod(workflow_properties_dictionnary.class,"isValidExecutable");
                isPath=getDeclaredMethod(workflow_properties_dictionnary.class,"isPath");
                nbInput=getDeclaredMethod(workflow_properties_dictionnary.class,"nbInput");
                isValidColor=getDeclaredMethod(workflow_properties_dictionnary.class,"isValidColor");
                isValidConnector=getDeclaredMethod(workflow_properties_dictionnary.class,"isValidConnector");
                isValidObjectType=getDeclaredMethod(workflow_properties_dictionnary.class,"isValidObjectType");
                isValidInputOutputType=getDeclaredMethod(workflow_properties_dictionnary.class,"isValidInputOutputType");
                isValidOS=getDeclaredMethod(workflow_properties_dictionnary.class,"isValidOS");
            } catch(Exception e) {e.printStackTrace();Config.log("Error in declaring mathods!");}
            
            ////////////////////////////////////////////////////////////////////////
            // Keyword definition
            //--This is a list of common properties
            //-- Default Properties
            keyword.put("Name", isText);        //Object (config or workflow object name)
            keyword.put("x", isPositiveFloat);//Position in x and y
            keyword.put("y", isPositiveFloat);
            keyword.put("x2", isPositiveFloat);//Position in x2 and y2
            keyword.put("y2", isPositiveFloat);
            keyword.put("w", isPositiveFloat);//Object width (w) or height (h)
            keyword.put("h", isPositiveFloat);
            keyword.put("Icon", isPath);                   //Icon assossiated?
            keyword.put("ObjectType", isValidObjectType);  //ObjectType (see ObjectType array for valid)
            keyword.put("outputType", isValidInputOutputType);  //ObjectType (see ObjectType array for valid)
            keyword.put("ObjectID", isText);               //Internal workflowID
            keyword.put("Type", isText);                   //Categories in the workflow toolbox list
            keyword.put("nbInput", nbInput);
            keyword.put("properties_id", isInteger);
            keyword.put("Website", isText);
            keyword.put("HelpSupplementary", isText);
            keyword.put("Tooltip", isText);
            
            keyword.put("Object", isText); //--This tell Armadillo in the saving phase that
            //--This is an Object
            
            keyword.put("Order", isInteger); //--Presently in debug, Order of execution...
            
            //--Running class and Editor for this object
            keyword.put("ClassName", isValidClass);
            //--Executable and version
            keyword.put("EditorClassName", isValidEditor);
            keyword.put("Executable", isValidExecutable);
            keyword.put("ExecutableLinux", isValidExecutable);
            keyword.put("ExecutableMacOSX", isValidExecutable);
            keyword.put("AlternativeExecutable", isValidExecutable);
            keyword.put("AlternativeExecutableOS", isValidOS);
            keyword.put("UseAlternativeExecutable", isBoolean);
            keyword.put("Version", isText);
            keyword.put("Version_Linux", isText);
            keyword.put("Version_MacOSX", isText);
            keyword.put("Publication", isText);
            keyword.put("SampleWorkflow", isText); //--A sample workflow for the tools
            keyword.put("RuntimeMacOSX", isText); //--Runtime mode for Mac OS X
            keyword.put("RunningDirectory", isText); //--Define a running directory for the appication (e.g. see hgt)
            
            keyword.put("InternalArmadilloApplication", isBoolean);
            
            
            keyword.put("CommandLine", isText);
            keyword.put("Commandline_Running", isText);
            keyword.put("ExitValue",isInteger);
            keyword.put("NormalExitValue",isInteger); //--Program Normal exitValue (default=0);
            keyword.put("Status", isInteger);
            keyword.put("IfStatus", isBoolean); //--Normal return value for If
            keyword.put("StatusString", isText);
            keyword.put("WebServices", isBoolean);
            keyword.put("debug", isBoolean);
            keyword.put("VerifyExitValue", isText); //--Verify the ExitValue (default=true);
            keyword.put("Email", isText);
            keyword.put("NoDraw", isBoolean); //--Object inside a For-Loop migth not be display
            keyword.put("InsideFor", isBoolean); //--inside a For-Loop
            keyword.put("ForObjectID", isText);
            //--If
            keyword.put("IfObjectID", isText); //--Selected variable to test...
            keyword.put("IfObjectMin", isText); //--Min Range
            keyword.put("IfObjectMax", isText); //--Max Range
            keyword.put("IfObjectValue", isText); //--Value
            keyword.put("IfValueTest", isText); //--Selected If value test to run (ValueEqual, InMaxMinRange, Contains, Equals, SmallerOrEquals...)
            keyword.put("IfTest", isText); //--Selected If test
            //--Repeat
            keyword.put("Repeat", isText);
            keyword.put("Ntime", isText);
            keyword.put("TimeRunning", isInteger); //--Time Running
            //--Thread and debug
            keyword.put("NoThread", isBoolean); //--Set this to True if normal app
            //-- Else, we return and the application is run
            //--In thread mode
            
            //--Other
            keyword.put("Description",isText);  //--This Object description
            keyword.put("help",isText);         //--Url or name for this properties
            keyword.put("filename",isText);     //--Properties filename
            
            //--Path
            keyword.put("editorPath", isPath);
            keyword.put("imagePath", isPath);
            keyword.put("classPath", isPath);
            keyword.put("propertiesPath", isPath);
            keyword.put("dataPath", isPath);
            
            //--If
            keyword.put("modeSide", isBoolean);
            
            //JG2016
            keyword.put("OneConnectorOnlyFor", isText); // Accept only one connector ex: 2<>3<>4
            keyword.put("defaultPgrmValues", isText);   // Default programs values
            keyword.put("SolelyConnectors", isText);    // Solely Connectors (other connectors are blocked if this one is selected) ex: 2<>3<>4
            //--Connector
            for (int i=0; i<5; i++) {
                //--Type
                keyword.put("Connector"+i+"Output", isBoolean);
                keyword.put("Connector"+i+"Conditional", isBoolean);
                //Name override...
                keyword.put("Connector"+i, isText);
            }
            
            //--Keywords
            for (int i=0; i<100; i++) {
                //--Type
                keyword.put("Keyword"+i, isText);
            }
            
            //--Conditions
            for (int i=0; i<100; i++) {
                //--Type
                keyword.put("IfCondition"+i, isText);
            }
            
            //--Related
            for (int i=0; i<100; i++) {
                
                //Name override...
                keyword.put("Similar"+i, isText);
            }
            
            //-- X
            keyword.put("ConnectorX0", isPositiveInteger);
            
            //-- Y
            keyword.put("ConnectorY0", isPositiveInteger);
            
            //--color
            keyword.put("colorMode", isValidColor);
            keyword.put("defaultColor", isValidColor);
            
            //--Input and Output keyword
            for (String s:InputOutputType) {
                keyword.put("Input"+s, isValidConnector);
                keyword.put("Output"+s, isValidConnector);
            }
            keyword.put("InputAll", isValidConnector);
            keyword.put("OutputAll", isValidConnector);
            
            //--Port
            for (String s:InputOutputType) {
                //--MultipleInput
                //--Connector number
                for (int i=0;i<4;i++) {
                    //--Object in connector
                    for (int j=0; j<10;j++) {
                        keyword.put("input_"+s.toLowerCase()+"_id"+i+j, isPositiveInteger);
                    }
                }
                //--Single output...
                keyword.put("output_"+s.toLowerCase()+"_id", isPositiveInteger);
            }
            //-- End initialize
            initialize=true;
        }
        
    } //--End constructor
    
    /**
     *
     * @return a list of default
     */
    public String[] getDefaultProperties() {
        String[] defaultProperties=
        {
            "Name",
            "x",
            "y",
            "ObjectType",
            "nbConnector"
        };
        
        return defaultProperties;
    }
    
    public String[] getKeyword() {
        Set<String> E=keyword.keySet();
        String[] dummy={}; //dummy array to cast to String array
        return E.toArray(dummy);
    }
    
    /**
     * Test if the value is compatible with the key
     * @param key
     * @param value
     * @return empty String if everything Ok, Warning comment otherwise
     */
    public String isValid(String key, String value) {
        Method M=keyword.get(key);
        if (M==null) return "Keyword not found";
        try {
            Object t = workflow_properties_dictionnary.class.newInstance();
            String rt=(String)M.invoke(t, value);
            //return rt.toString();
            return rt;
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return "";
    }
    
    private String isPositiveFloat(String value) {
        try {
            float i=Float.valueOf(value);
            return (i>-1?"":"Must be set to a POSITIVE Float (ex. 128.0).");
        } catch(Exception e) {return "Must be set to a POSITIVE Float (ex. 128.0).";}
    }
    
    private String isPositiveInteger(String value) {
        try {
            int i=Integer.valueOf(value);
            return (i>-1?"":"Must be set to a POSITIVE Integer (ex. 128).");
        } catch(Exception e) {return "Must be set to a POSITIVE Integer (ex. 128).";}
    }
    
    private String isInteger(String value) {
        try {
            int i=Integer.valueOf(value);
            return "";
        } catch(Exception e) {return "Must be set to an Integer (ex. 128).";}
    }
    
    private String isBoolean(String value) {
        try {
            if (value.trim().toLowerCase().equals("true")||value.trim().toLowerCase().equals("true")) return "";
            boolean b=Boolean.valueOf(value);
            return (b?"":"Must be set to either True or False.");
        } catch(Exception e) {return "Must be set to True or False.";}
    }
    
    private String isValidClass(String value) {
        return (programs.indexOf(value)>-1?"":"Must be a valid class name in uppercase (ex. CLUSTALW). Class was not found.");
    }
    
    private String isValidEditor(String value) {
        return (editors.indexOf(value)>-1?"":"Must be a valid class name in uppercase (ex. CLUSTALEDITOR). Class was not found.");
    }
    
    private String isValidObjectType (String value) {
        return (Util.indexOf(value,ObjectType)>-1?"":"Must be a valid ObjectType: "+Util.toString(ObjectType));
    }
    
    private String isValidInputOutputType (String value) {
        return (Util.indexOf(value,InputOutputType)>-1?"":"Must be a valid ObjectType: "+Util.toString(InputOutputType));
    }
    
    ///////////////////////////////////////////////////////////////////////////
    /// PUBLIC Helper
    public boolean isNumeric(String value) {
        try {
            int i=Integer.valueOf(value);
            return true;
        } catch(Exception e1) {
            try {
                Float i2=Float.valueOf(value);
                return true;
            } catch(Exception e2) {
                try {
                    Double i3=Double.valueOf(value);
                    return true;
                } catch(Exception e3) {
                    return false;
                } //--end e3
            } //--end e2
        } //--end e1
    }
    
    /**
     *
     * @param value
     * @return
     */
    public String isValidExecutable(String value) {
        value=value.toLowerCase();
        File file=new File(value);
        if (!file.exists()) {
            file=new File(value);
            if (!file.exists()) return "File must exists...";
        }
        return "";
        //--Valid an endswith .com or .exe, .jar ... (not valid for MAC OSX and Linux...)
        //if (value.endsWith(".com")||value.endsWith(".exe")||value.endsWith(".jar")||value.endsWith(".sh")||value.endsWith(".bat")||value.endsWith(".pl")||value.endsWith(".py")) return "";
        //return (file.isDirectory()?"":"File must exists and be Executable...");
    }
    
    private String isText(String value) {
        return "";
    }
    
    public String isPath(String value) {
        File file=new File(value);
        return (file.isDirectory()?"":"Must be a valid file path");
    }
    
    public String isValidColor(String value) {
        return(Util.indexOf(value,colorMode)>-1?"":"Must be a valid color (BLUE, RED, GREEN, ORANGE, CYAN)");
    }
    
    public String nbInput(String value) {
        if (isInteger(value).equals("")) {
            int i=Integer.valueOf(value);
            if (i>-1&&i<3) return "";
        }
        return "Must be a number between 0 and 2";
    }
    
    public String isValidConnector(String value) {
        if (value.equals("True")||value.equals("False")) return "";
        if (value.startsWith("Connector")) return "";
        return "Must be either 'True' or set to 'Connector0', 'Connector1'...";
    }
    
    public String isValidOS(String value) {
        if (value.equals("Windows")||value.equals("MacOSX")||value.equals("Linux")) return "";
        return "Must be either 'Windows', 'MacOSX' or 'Linux' ...";
    }
    
    /**
     * Determine if the value associated with this key is Valid
     * @param key
     * @return True or False
     */
    public boolean isValidValue(String key, String value) {
        String valid=isValid(key, value);
        return (valid.equals("")?true:false);
    }
    
///**
// * Return a Hashmap<String ClassName, Classdata obj> of the Class in the specify directory or null if not found
// * Note: filename will not include path
// */
//public static HashMap<String,Classdata> loadClasslisting (String path) {
//  FilenameFilter filter=new FilenameFilter() {
//    public boolean accept(File dir, String name) {
//    if (name.charAt(0) == '.') return false;
//    if (name.indexOf('$')>-1) return false; //--We don't want partial build
//    if (name.toLowerCase().endsWith(".class")) return true;
//     return false;
//    }
//  };
//
//    HashMap<String,Classdata>tmp=new HashMap<String,Classdata>();
//    File dataFolder = new File(path);
//    String[] names = dataFolder.list(filter);
//    if (names==null) names=new String[0];
//    for (String name:names) {
//        Classdata c=new Classdata(name);
//        tmp.put(name, c);
//    }
//  return tmp;
//}
    
    
    public static boolean getArmadilloClassListing() {
        //TO DO: Add a plugins directory
        if (classname.isEmpty()) {
            try {
                //--Default name: Armadillo
                JarFile jarfile = new JarFile("Armadillo.jar");
                
                Enumeration i=jarfile.entries();
                //Case 1. --Error, maybe not the good name
                if (!i.hasMoreElements()) {
                    jarfile = new JarFile("armadillo.jar");
                    i=jarfile.entries();
                }
                //Case 2. --Error, unable to get the program...
                if (!i.hasMoreElements()) {
                    System.out.println("Error. Unable to get Armadillo class listing...");
                    return false;
                }
                while(i.hasMoreElements()) {
                    String name=i.nextElement().toString();
                    if (accept(name,"")) classname.add(name);
                }
            } catch(Exception e) {}
        }
        return (classname.isEmpty());
    }
    
    public static Vector<String> getClasslistingString (String filter) {
        Vector<String> V=new Vector<String>();
        getArmadilloClassListing();
        for (String name:classname) {
            if (name.startsWith(filter+"/"))  {
                name=name.replaceAll(filter+"/", "");
                // Special case for programs.local
                if (name.startsWith("local")) {
                    name=name.replaceAll("local/", "local.");
                }
                try {
                    Classdata c=new Classdata(name);
                    V.add(c.getName());//ex. programs.CLUSTALW
                    V.add(name.substring(0,name.indexOf(".class"))); //ex. CLUSTALW
                } catch(Exception e){
                    Config.log("Error in creating Classdata for "+name+" in getClasslistingString.");
                }
                //System.out.println(c.getName());
            }
        }
        return V;
    }
    
    public static Vector<Classdata> getClasslisting (String filter) {
        Vector<Classdata> V=new Vector<Classdata>();
        getArmadilloClassListing();
        for (String name:classname) {
            if (name.startsWith(filter+"/"))  {
                name=name.replaceAll(filter+"/", "");
                // Special case for programs.local
                if (name.startsWith("local")) {
                    name=name.replaceAll("local/", "local.");
                }
                if (!name.equals("")) {
                    Classdata c=new Classdata(name);
                    V.add(c);
                }
            }
        }
        return V;
    }
    
    /**
     * This get the class listing in the armadillo package...
     * @return
     */
    public static Vector<Classdata> getClasslisting () {
        Vector<Classdata> V=new Vector<Classdata>();
        getArmadilloClassListing();
        for (String name:classname) {
            if (!name.equals("")) {
                Classdata c=new Classdata(name);
                V.add(c);
            }
        }
        return V;
    }
    
    static boolean accept(String name, String filter) {
        if (name.charAt(0) == '.') return false;
        if (name.indexOf('$')>-1) return false; //--We don't want partial build
        if (filter.isEmpty()) {
            if (name.toLowerCase().endsWith(".class")) return true;
        } else {
            if (name.toLowerCase().endsWith(".class")&&name.startsWith(filter)) return true;
        }
        return false;
    }
    
    public Method getDeclaredMethod(Class c, String name) {
        Method[] M=c.getDeclaredMethods();
        for (Method m:M) {
            if (m.getName().equals(name)) return m;
        }
        return null;
    }
    
    public void listDeclaredMethod(Class c) {
        Method[] M=c.getDeclaredMethods();
        for (Method m:M) {
            System.out.println(m.getName());
        }
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// Search function
    
    
    public ArrayList<String> getSearchKeyword(String expression) {
        ArrayList<String>tmp=new ArrayList<String>();
        if (expression!=null) {
            expression=expression.toLowerCase();
            //expression=expression.toUpperCase();
            for (String s:getKeyword()) {
                String ls=s.toLowerCase();
                if (ls.startsWith(expression)) {
                    tmp.add(s);
                    //System.out.println(s);
                }
            }
        }
        return tmp;
    }
    
    /**
     * Create a vector of all accepted input and output
     * @return Vector of InputOutput
     */
    public Vector<InputOutput> getInputOutput() {
        Vector<InputOutput> tmp=new Vector<InputOutput>();
        //--Input
        for (String s:InputOutputType) {
            InputOutput I=new InputOutput();
            I.setInputOutput("Input"+s);
            I.setConnector(NotSet);
            tmp.add(I);
        }
        //--Output
        for (String s:InputOutputType) {
            InputOutput O=new InputOutput();
            O.setInputOutput("Output"+s);
            O.setConnector(NotSet);
            tmp.add(O);
        }
        return tmp;
    }
    
    
    
    
//    ///////////////////////////////////////////////////////////////////////////
//    /// Search position in a String Array
//
//    /**
//     * Search the position of a key in an Array
//     * Note: We handle null case and length case with exception
//     * @param key
//     * @param ArrayToSearch
//     * @return the key index or -1 if not found
//     */
//    public int indexOf(Object key, Object[] ArrayToSearch) {
//        try {
//            if (key.equals(NotSet)) return -1; //--We handle this special case
//            for (int i=0; i<ArrayToSearch.length;i++) {
//                if (ArrayToSearch[i].equals(key)) return i;
//            }
//        } catch(Exception e) {return -1;} //--Error
//        return -1; //Not found
//    }
//
//   /**
//    * Transforme a String Vector to a String representation
//    * @param V
//    * @return a String representing the elements of the Vector<String>
//    */
//       private String toString(Vector<String> V) {
//           String s="";
//           for (String stri:V) s+=stri+", ";
//           return s;
//       }
//
//       /**
//    * Transforme a String Vector to a String representation
//    * @param V
//    * @return a String representing the elements of the Vector<String>
//    */
//       private String toString(String[] V) {
//           String s="";
//           for (String stri:V) s+=stri+", ";
//           return s;
//       }
//
    
    public boolean isKeyword(Object key) {
        return keyword.containsKey(key);
    }
}
