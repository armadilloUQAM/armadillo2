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

// For the moment, all programs should be in the programs package
package programs;

////////////////////////////////////////////////////////////////////////////////
///
/// This is an example program class to use with the Armadillo Workflow Platform
/// Note: There is no System.out with If because there is no constructor (If is static)
/// TO have it, use println();
///
/// Etienne Lord 2011


import Class.JavaProgramClassLoader;
import biologic.Results;
import configuration.Config;
import configuration.Util;

import javax.tools.JavaCompiler.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.*;
import javax.tools.JavaFileObject.Kind;

import javax.tools.ToolProvider;
import program.RunProgram;
import workflows.workflow_properties;

@Deprecated
public class IfProgramClass extends RunProgram {
    //--Use the Light Class instead

    ////////////////////////////////////////////////////////////////////////////
    /// Input / Output
    ///
    /// All internal variables should be declared here for ease of use.
       private String output="output.";  //File for stdout and stderr ...
       boolean debug=false;
       int ecount=0;

    ////////////////////////////////////////////////////////////////////////////
    /// Constructor
    ///
    /// Current program class will be use only

    /**
     * Main default constructor
     * @param properties
     */
    public IfProgramClass(workflow_properties properties) {
       super(properties);
       //--Unique output buffer
       output+=Util.returnTimeCode();
       ecount=Util.returnCount();
       execute();
    }


   @Override
    public boolean init_checkRequirements() {
        return true;
    }

    @Override
    public void init_createInput() {
    }

    @Override
    public String[] init_createCommandLine() {
        return new String[0];
    }


    @Override
    public boolean do_run() throws Exception {
        //--Update the output of the If to nothing...
         properties.remove("IfStatus");
        if (!config.isCompilerFound()) {
            setStatus(status_error,"Error: We are running from the JRE, not the JDK. Compilation is not available in this case.\nYou need to run Armadillo from the Java SDK.\njava -jar -Xms128m -Xmx512m Armadillo.jar");
            return false;
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        compiler.getStandardFileManager(diagnostics, Locale.ENGLISH, null);
      
        CompilationTask task = compiler.getTask(null, null, diagnostics, null, null,Arrays.asList(getSourceCode()));
        if (!task.call()) {
            for(Diagnostic dm : diagnostics.getDiagnostics()) {
                setStatus(status_running,"Error: "+dm.toString());
                setStatus(status_running,getLine(dm.toString()));                
            }

            setStatus(status_error,"Error: see the above compilation error.");
            return false;
        } else {
          JavaProgramClassLoader local_class=new JavaProgramClassLoader();
          Class classdata = local_class.loadClass("source.class");
          if (classdata==null) {
                            properties.remove("Status");
                            Config.log("Unable to initialize class (+"+properties.get("ClassName")+") for "+properties.getName()+"");
                            setStatus(status_error,"Unable to find code.source");
                            return false;
                          }
         for (Method m:classdata.getDeclaredMethods()) {
             //--Exclude println function
             if (!m.getName().equals("println")) {
                 setStatus(status_running,"Executing function "+m.getName());
                  m.setAccessible(true);
                                   try {
                                       // TO DO : Find a better way...
                                       Object[] null_obj={};
                                       //null_obj[0]=null;
//                                       Object run=m.invoke(m.getName(), new String[0]);
//                                       if (run!=null) {
//                                           if (run instanceof java.lang.Boolean) {
//                                               properties.put("IfStatus", (Boolean)run);
//                                               setStatus(status_done,"Return value: "+(Boolean)run);
//                                           }
//                                           return true;
//                                        }
                                       setStatus(status_error, "Unexpected error in If: function is correct?");
                                       return false;
                                   } catch(Exception e) {setStatus(status_error,"If error: "+(e.getMessage()==null?"Exit without answer...":e.getMessage()));return false;}
                              } //--End not println
            } //--End for method
        } //--End no compilation error
        return true;
    }


    @Override
    public boolean do_run_withoutWait() throws Exception {
        return super.do_run_withoutWait();
    }


    @Override
    public void post_parseOutput() {
         if (Util.FileExists(output)) {
            Results text=new Results(output);
            text.setName(properties.getName()+" output created on "+Util.returnCurrentDateAndTime());
            msg(text.getText());
            addOutput(text);
            text.saveToDatabase();
            properties.put("output_results_id", text.getId());
            if (!debug) Util.deleteFile(output);
        }
        Util.deleteFile("source.class");
        setStatus(status_done,"");
    }

    private URI toURI(String filename) {
        try {
            return new URI(filename);
        } catch (Exception e) {return null;}
    }

    /**
     * Return the line of the error in the Java code
     * Note: VERIFY THAT THE BELOW SOURCE CODE MATCH THE ONE IN getSourceCode()
     * @param str
     * @return String with the compilation error...
     */
    private String getLine(String str) {
        Pattern line_error_pattern=Pattern.compile("source.java.([0-9]*)",Pattern.CASE_INSENSITIVE);
        Matcher m=line_error_pattern.matcher(str);
        String source=           "package code;\n" +
                                    (properties.isSet("import")?properties.get("import").replaceAll("\"", "\\\""):"")+
                                  "class source {\n"+
                                  "//Internal variables : must be static \n"+
                                  "\n"+
                                  "public static workflow_properties properties=programs.runningProperties;\n"+
                                  "public static Workflows workflows=programs.workflows;\n"+
                                  "public static long time_running=programs.timerunning;\n"+
                                  "\n"+
                                  "\n"+
                                  "/**\n" +
                                  " *  Default constructor\n"+
                                  " */\n"+
                                 "public source() {\n"+                                
                                  "}\n"+                                  
                                   (properties.isSet("script")?properties.get("script").replaceAll("\"", "\\\""):"public static void noFunction() {System.out.println(\"No static function found\");}")+
                                   "public static void println(String s) {"+
                                    "//Redirect to file stdout et stderr (note need import java.io.*)\n"+
                                  " try {\nPrintStream output=new PrintStream(new FileOutputStream(\""+output+"\",true));\n"+
                                  "  System.setOut(output);\n"+
                                  "  System.setErr(output);\n"+
                                  "  System.out.println(s);\n"+
                                  " } catch (Exception e"+ecount+"){e"+ecount+".getMessage();}\n"+
                                  "}"+
                                  "\n"+
                                  "}\n";
        if (m.find()) {
            try {

                int count=1;
                int line=Integer.valueOf(m.group(1));
                for (String st:source.split("\n")) {
                    if (line==count) return st;
                    count++;
                }
            } catch(Exception e) {e.printStackTrace();return "";}
        }
        return "";
    }

    private JavaFileObject getSourceCode() {
            final String source = "package code;\n" +
                                    (properties.isSet("import")?properties.get("import").replaceAll("\"", "\\\""):"")+
                                  "class source {\n"+
                                  "//Internal variables : must be static \n"+
                                  "\n"+
                                  "public static workflow_properties properties=programs.runningProperties;\n"+
                                  "public static Workflows workflows=programs.workflows;\n"+
                                  "public static long time_running=programs.timerunning;\n"+
                                  "\n"+
                                  "\n"+
                                  "/**\n" +
                                  " *  Default constructor\n"+
                                  " */\n"+
                                "public source() {\n"+
                                   "}\n"+                                  
                                   (properties.isSet("script")?properties.get("script").replaceAll("\"", "\\\""):"public static void noFunction() {System.out.println(\"No static function found\");}")+
                                   "public static void println(String s) {"+
                                    "//Redirect to file stdout et stderr (note need import java.io.*)\n"+
                                  " try {\nPrintStream output=new PrintStream(new FileOutputStream(\""+output+"\",true));\n"+
                                  "  System.setOut(output);\n"+
                                  "  System.setErr(output);\n"+
                                  "  System.out.println(s);\n"+
                                  " } catch (Exception e"+ecount+"){e"+ecount+".getMessage();}\n"+
                                  "}"+
                                  "\n"+
                                  "}\n";

            return new SimpleJavaFileObject(URI.create("string:///" + "source" + Kind.SOURCE.extension),
                     Kind.SOURCE) {
                @Override
                public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                    return source;
                }
    };
}

}





