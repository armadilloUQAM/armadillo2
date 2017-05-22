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

import Class.JavaProgramClassLoader;
import biologic.Results;
import configuration.Config;
import configuration.Util;
import java.io.File;

import javax.tools.JavaCompiler.*;
import java.lang.reflect.Constructor;
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


/**
 * Simple class to run a java program
 * @author Etienne Lord
 * @since August 2010
 */
public class JavaProgramClass extends RunProgram {  
  
    
    ////////////////////////////////////////////////////////////////////////////
    /// Input / Output
    ///
    /// All internal variables should be declared here for ease of use.
    private String output="output.";  //File for stdout and stderr ... 
                                      //coded to output.+Util.returnTimeCode();

    ////////////////////////////////////////////////////////////////////////////
    /// Constructor
    ///
    /// Current program class will be use only    
    /// NOTE: TODO Use eclypse ecj.jar for the compilation:
    /// See: https://github.com/processing/processing/blob/40dfdabe7a6ce935d12c1b84b1bd149df244c343/pdex/src/processing/mode/experimental/Compiler.java
    ///      http://help.eclipse.org/indigo/index.jsp?topic=/org.eclipse.jdt.doc.isv/guide/jdt_api_compile.htm
    ///      http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftask-using_batch_compiler.htm
    

    /**
     * Main default constructor
     * @param properties
     */
    public JavaProgramClass(workflow_properties properties) {        
       super(properties); 
       output+=Util.returnTimeCode();
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
        
        if (!config.isCompilerFound()) {
            setStatus(status_error,"Error: We are running from the JRE, not the JDK. Compilation is not available in this case.");
            return false;
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager=null;
        try {
         fileManager= compiler.getStandardFileManager(diagnostics,null, null);
        } catch(Exception e) {e.printStackTrace();}
        CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null,Arrays.asList(getSourceCode()));
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
         for (Constructor C:classdata.getConstructors()) {
                C.setAccessible(true);
                               try {
                                   Object run=C.newInstance();
                                   if (run!=null) {                                        
                                       post_parseOutput();
                                       return true;
                                    }
                               } catch(Exception e) {e.printStackTrace();}
                          }
        }
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
            Util.deleteFile(output);
        }
        Util.deleteFile("source.class");
    }

    protected URI toURI(String filename) {
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
    protected String getLine(String str) {
        Pattern line_error_pattern=Pattern.compile("source.java.([0-9]*)",Pattern.CASE_INSENSITIVE);
        Matcher m=line_error_pattern.matcher(str);
        String code ="package code;\n" +
                                    (properties.isSet("import")?properties.get("import").replaceAll("\"", "\\\""):"")+
                                  "class source {\n"+
                                   "//Internal variables : must be static \n"+
                                  "\n"+
                                  "public static workflow_properties properties=programs.runningProperties;\n"+
                                  "public static Workflows workflows=programs.workflows;\n"+
                                  "public static long time_running=programs.timerunning;\n"+
                                  "\n"+
                                  "public source() {\n"+
                                  "//Redirect to file stdout et stderr (note need import java.io.*)\n"+
                                  "try {\nPrintStream output=new PrintStream(new FileOutputStream(\""+output+"\",true));\n"+
                                  "System.setOut(output);\n"+
                                  "System.setErr(output);\n"+
                                  "\n"+
                                   (properties.isSet("script")?properties.get("script").replaceAll("\"", "\\\""):"System.out.println(\"+No script found+\");")+
                                  "\n"+
                                  "} catch (Exception e"+Util.returnCount()+"){}\n"+
                                  "}//End source()\n"+

                                  "}//End class\n";
        if (m.find()) {
            try {

                int count=1;
                int line=Integer.valueOf(m.group(1));
                for (String st:code.split("\n")) {
                    if (line==count) return st;
                    count++;
                }
            } catch(Exception e) {e.printStackTrace();return "";}
        } 
        return "";
    }


    protected JavaFileObject getSourceCode() {
            
           //--Note: import are imported from the properties 
           final String code = "package code;\n" +
                                    (properties.isSet("import")?properties.get("import").replaceAll("\"", "\\\""):"")+
                                  "class source {\n"+
                                   "//Internal variables : must be static \n"+
                                  "\n"+
                                  "public static workflow_properties properties=programs.runningProperties;\n"+
                                  "public static Workflows workflows=programs.workflows;\n"+
                                  "public static long time_running=programs.timerunning;\n"+
                                  "\n"+
                                  "public source() {\n"+
                                  "//Redirect to file stdout et stderr (note need import java.io.*)\n"+
                                  "try {\nPrintStream output=new PrintStream(new FileOutputStream(\""+output+"\",true));\n"+
                                  "System.setOut(output);\n"+
                                  "System.setErr(output);\n"+
                                  "\n"+
                                   (properties.isSet("script")?properties.get("script").replaceAll("\"", "\\\""):"System.out.println(\"+No script found+\");")+
                                  "\n"+
                                  "} catch (Exception e"+Util.returnCount()+"){}\n"+
                                  "}//End source()\n"+
                                  "}//End class\n";
                  
            return new SimpleJavaFileObject(URI.create("string:///" + "source" + Kind.SOURCE.extension),
                     Kind.SOURCE) {
                @Override
                public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                    return code;
                }
    };
}

        /**
        * A file object used to represent source coming from a string.
         * From:http://download.oracle.com/javase/6/docs/api/javax/tools/JavaCompiler.html
        */
       public class JavaSourceFromString extends SimpleJavaFileObject {
           /**
            * The source code of this "file".
            */
           final String code;

           /**
            * Constructs a new JavaSourceFromString.
            * @param name the name of the compilation unit represented by this file object
            * @param code the source code for the compilation unit represented by this file object
            */
           JavaSourceFromString(String name, String code) {
               super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),
                     Kind.SOURCE);
               this.code = code;
           }

           @Override
           public CharSequence getCharContent(boolean ignoreEncodingErrors) {
               return code;
           }
       }


       //--Sample code from processing.org
       
//         ClassLoader loader = build.getMode().getJavaModeClassLoader();
//      //ClassLoader loader = build.getMode().getClassLoader();
//      try {
//        Class batchClass =
//          Class.forName("org.eclipse.jdt.core.compiler.batch.BatchCompiler", false, loader);
//        Class progressClass =
//          Class.forName("org.eclipse.jdt.core.compiler.CompilationProgress", false, loader);
//        Class[] compileArgs =
//          new Class[] { String[].class, PrintWriter.class, PrintWriter.class, progressClass };
//        Method compileMethod = batchClass.getMethod("compile", compileArgs);
//        success = (Boolean)
//          compileMethod.invoke(null, new Object[] { command, outWriter, writer, null });
//      } catch (Exception e) {
//        e.printStackTrace();
//        throw new SketchException("Unknown error inside the compiler.");
//      }

       
       
}





