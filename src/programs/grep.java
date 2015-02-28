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

package programs;

import biologic.Output;
import biologic.Results;
import configuration.Util;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.regexp.RE;
import program.RunProgram;
import workflows.workflow_properties;
import workflows.workflow_properties_dictionnary;

/**
 * This is a GREP extractor used to process some file,
 * data, etc. and ouput a list (for-loop) or a text
 * @author Etienne Lord
 */


public class grep extends RunProgram {

    ////////////////////////////////////////////////////////////////////////////
    /// Variables 
    String separator="NEWLINE"; //--Constant for \n (default separator)

 public grep(workflow_properties properties) {
        super(properties);
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
        this.setStatus(this.status_running, "Grep");
        Output inputDOWN=null;
        //--Look for input
        for (String inputtype:workflow_properties_dictionnary.InputOutputType) {
        Vector<Integer>ids_number=properties.getInputID(inputtype, PortInputDOWN);
        if (ids_number.size()>0) {
               int idt=ids_number.get(0); //--We care only for the first one
               inputDOWN=new Output();
               inputDOWN.setType(inputtype);
               inputDOWN.setTypeid(idt);              
            }
         }
         //--Check if we have any input - Otherwise quit...
        if (inputDOWN==null||inputDOWN.getBiologic().toString().isEmpty()) {
            setStatus(status_done,"No input text found. Nothing to do.");
            return true;
        }

        try {
            //--Results datatype
            Pattern p=Pattern.compile(".*");
            Results results=new Results();
            results.setName("Grep - "+inputDOWN.getName());
            results.setNote("Grep used:\n"+properties.get("Regex")+"\n"+"Done at "+Util.returnCurrentDateAndTime());
            setStatus(status_running,"Grep on "+inputDOWN.getName());
            setStatus(status_running,"Grep used:\n"+properties.get("Regex"));
            //--Compile the regex
            RE regex=null;
            if (properties.isSet("Separator")) separator=properties.get("Separator");
            if (properties.isSet("Modifier")) {
                switch (properties.getInt("Modifier")) {
                    case 1: p=Pattern.compile(properties.get("Regex"), Pattern.CANON_EQ);
                            regex=new RE(properties.get("Regex"));
                            setStatus(status_running,"Modifier:\n"+"CANON_EQ");
                            break;
                    case 2: p=Pattern.compile(properties.get("Regex"), Pattern.CASE_INSENSITIVE);
                            regex=new RE(properties.get("Regex"), RE.MATCH_CASEINDEPENDENT);
                            setStatus(status_running,"Modifier:\n"+"MATCH_CASEINDEPENDENT");
                            break;
                    case 3: p=Pattern.compile(properties.get("Regex"), Pattern.COMMENTS);
                            regex=new RE(properties.get("Regex"));
                            setStatus(status_running,"Modifier:\n"+"COMMENTS");
                            break;
                    case 4: p=Pattern.compile(properties.get("Regex"),Pattern.DOTALL);
                            regex=new RE(properties.get("Regex"));
                            setStatus(status_running,"Modifier:\n"+"DOTALL");
                            break;
                    case 5: p=Pattern.compile(properties.get("Regex"),Pattern.LITERAL);
                            regex=new RE(properties.get("Regex"));
                            setStatus(status_running,"Modifier:\n"+"LITTERAL");
                            break;
                    case 6: p=Pattern.compile(properties.get("Regex"),Pattern.MULTILINE);
                            regex=new RE(properties.get("Regex"), RE.MATCH_MULTILINE);
                            setStatus(status_running,"Modifier:\n"+"MULTILINE");
                            break;
                    case 7: p=Pattern.compile(properties.get("Regex"),Pattern.UNICODE_CASE);
                            regex=new RE(properties.get("Regex"));
                            setStatus(status_running,"Modifier:\n"+"UNICODE_CASE");
                            break;
                    case 8: p=Pattern.compile(properties.get("Regex"),Pattern.UNIX_LINES);
                            regex=new RE(properties.get("Regex"));
                            setStatus(status_running,"Modifier:\n"+"UNIX_LINES");
                            break;
                }
            } else {
                p=Pattern.compile(properties.get("Regex"));
                regex=new RE(properties.get("Regex"));
            }           
            //--Get the file or text
            
            
            //--Apache or Java?
            if (properties.getBoolean("ApacheStyle")) {
                //--Apache

                //--Debug String[] grep=regex.grep(inputDOWN.getBiologic().toString().split("\n"));
                //for (String str:grep) System.out.println(str);
                   //--break by line...
                String[] str=inputDOWN.getBiologic().toString().split("\n");
                //System.out.println("Input length:"+str.length);
                for (String s:str) {
                
                 if (regex.match(s)) {
                     System.out.println(regex.getParen(0));
                     System.out.println(regex.getParenCount());
                       for (int i=0; i<regex.getParenCount();i++) {
                           System.out.println(regex.getParen(i));
                        }
                
                    if (separator.equals("Use Format Group")) {
                        Vector<String>tmp=new Vector<String>();
                        String toFormat=properties.get("FormatGroup");
                        int groupCount=regex.getParenCount();
                        for (int i=0; i<groupCount;i++) {                             
                            tmp.add(regex.getParen(i));
                        }                        
//                        for (String str:tmp) {
//                            System.out.println(str);
//                        }
                        for (int i=0; i<groupCount;i++) {
                             toFormat=toFormat.replaceAll("/"+(i+1),tmp.get(i));
                        }
                        toFormat=toFormat.replace('\n', '\n');
                        toFormat=toFormat.replaceAll("NEWLINE", "\n");
                        toFormat=toFormat.replaceAll("TAB", "\t");
                        results.getUnknownST().append(toFormat);
                    } else {
                        for (int i=0; i<regex.getParenCount();i++) {
                            if (separator.equals("NEWLINE")) {
                                results.getUnknownST().append(regex.getParen(i).trim()+"\n");
                            } else if (separator.equals("TAB")) {
                                results.getUnknownST().append(regex.getParen(i).trim()+"\t");
                            } else results.getUnknownST().append(regex.getParen(i+1).trim()+separator);
                        }
                    }
                }
               }  
                
            } else {
                //--Java Regex                
                
                int count=0;
                //--break by line...
                String[] str=inputDOWN.getBiologic().toString().split("\n");
                //System.out.println("Input length:"+str.length);
                for (String s:str) {
                    Matcher m=p.matcher(s);
                    while(m.find()) {
                        
                        count++;
                        int groupCount=m.groupCount();
                        //System.out.println(groupCount);
                        Vector<String>tmp=new Vector<String>();
                        //--We have group?
                        if (groupCount==0) {
                            //--No. We display only the match string
                            results.getUnknownST().append("True\n");
                        } else {
                            //--Yes. We display only the match
                            if (separator.equals("Use Format Group")) {
                                String toFormat=properties.get("FormatGroup");
                                groupCount=m.groupCount();
                                for (int i=0; i<groupCount;i++) {
                                     tmp.add(m.group(i+1));
                                }
                                for (int i=0; i<groupCount;i++) {
                                     toFormat=toFormat.replaceAll("/"+(i+1),tmp.get(i));
                                }
                                toFormat=toFormat.replace('\n', '\n');
                                toFormat=toFormat.replaceAll("NEWLINE", "\n");
                                toFormat=toFormat.replaceAll("TAB", "\t");
                                results.getUnknownST().append(toFormat);
                            } else {
                                for (int i=0; i<m.groupCount();i++) {
                                    if (separator.equals("NEWLINE")) {
                                        results.getUnknownST().append(m.group(i).trim()+"\n");
                                    } else if (separator.equals("TAB")) {
                                        results.getUnknownST().append(m.group(i).trim()+"\t");
                                    } else results.getUnknownST().append(m.group(i+1).trim()+separator);
                                }
                            }
                        }
                    }
                }
            }
            results.saveToDatabase();
            properties.put("output_results_id", results.getId());
        } catch(Exception e) {
            setStatus(status_error,"Regex error:"+e.getLocalizedMessage());
            return false;}
        return true;
    }





    @Override
    public void post_parseOutput() {}



  }
    

