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

////////////////////////////////////////////////////////////////////////////////
///
/// Create a Thread to run a CustomProgram
///
/// Etienne Lord 2009


import biologic.Alignment;
import biologic.Ancestor;
import biologic.Matrix;
import biologic.MultipleSequences;
import biologic.MultipleTrees;
import biologic.Output;
import biologic.Results;
import biologic.Sequence;
import biologic.Text;
import biologic.TextFile;
import biologic.Tree;
import biologic.Unknown;
import configuration.Config;
import configuration.Util;
import java.io.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import program.RunProgram;
import workflows.workflow_properties;


public class CustomProgram extends RunProgram {
        
    /**
     *
     * Note: il n'y a pas présentement de test d'erreur
     *
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant 
     */
    public CustomProgram(workflow_properties properties) {        
        super(properties);
        properties.put("NoThread", true);
        execute();       
    }

    @Override
    public boolean init_checkRequirements() {
        try {
            //--Old
            
            for (int i=1; i<8;i++) {
            if (properties.getBoolean("input"+i+"required")) {
                //--Port
                String port=properties.get("input"+i+"port");
                Integer port_id=null;
                if (port.equals("Both")) port_id=CustomProgram.PortInput;
                if (port.equals("Up")) port_id=CustomProgram.PortInputUP;
                if (port.equals("Down")) port_id=CustomProgram.PortInputDOWN;
                //--Type
                String type=properties.get("input"+i+"type").trim();
                if (type.indexOf("-")>-1) {
                    type=type.substring(0, type.indexOf("-")-1).trim();
                }
                System.out.println(port+" "+type);

                Vector<Integer>ids=properties.getInputID(type, PortInput);
                if (ids.size()==0) {
                    setStatus(this.status_BadRequirements, "Coundn't find required "+type+" at port "+port);
                    return false;
                }
                Output output=new Output();
                output.setType(type);
                for (Integer id:ids) {
                    try {
                        output.setTypeid(id);

                    if (output.getBiologic().getName().isEmpty()) {
                        setStatus(this.status_BadRequirements, "Internal: Coundn't find required "+type+" with id "+id);
                    }
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        } catch(Exception e) {e.printStackTrace();}
        return true;
    }

    @Override
    public void init_createInput() {
        
        //--Type
        //        Simple option (text)
        //File (Up input port)
        //File (Down input port)
        //File (Output port)
        //Option=File (Up input port)
        //Option=File (Down input port)
        //Option=File (Output port)
        //<File (Up input port)
        //<File (Down input port)
        //>File (Output port)
        //        
        
        try {
        for (int i=1; i<21;i++) {          
            
            //String port=properties.get("input"+i+"port");
            String option=properties.get("input"+i+"option");
            String args=properties.get("execute"+i);
            Pattern p_option=Pattern.compile("(.*)=(.*)");
            Matcher m_option=p_option.matcher(args);
            Pattern p_option1=Pattern.compile("(.*)<(.*)");
            Matcher m_option1=p_option.matcher(args);
            Pattern p_option2=Pattern.compile("(.*)>(.*)");
            Matcher m_option2=p_option.matcher(args);
            
            Integer port_id=null;
            port_id=CustomProgram.PortInput; //--Default any port
            
            String filename="infile";  //--default filename          
            boolean output=false;
            
            //--Process only if we have some args and it's not a simple option
            if (!option.equalsIgnoreCase("Simple option (text)")&&!args.equals("")) {
            
                //--TO DO 
                //--Screen for the option here
                if (option.equals("File (Up input port)")) {
                    port_id=CustomProgram.PortInputUP;
                    filename=args;
                } else 
                if (option.equals("File (Down input port)")) {
                    port_id=CustomProgram.PortInputDOWN;
                    filename=args;
                } else 
                    if (option.equals("File (Both input ports)")) {
                    port_id=CustomProgram.PortInput;
                    filename=args;
                } else 
                if (option.equals("File (Output port)")) {
                    filename=args;
                } else 
                if (option.equals("Option=File (Up input port)")) {
                    if (m_option.find()) {
                        filename=m_option.group(2);
                        port_id=CustomProgram.PortInputUP;
                    }                    
                } else 
                if (option.equals("Option=File (Down input port)")) {
                    if (m_option.find()) {
                        port_id=CustomProgram.PortInputDOWN;
                        filename=m_option.group(2);
                    }
                } else 
                if (option.equals("Option=File (Both input ports)")) {
                    if (m_option.find()) {
                        port_id=CustomProgram.PortInput;
                        filename=m_option.group(2);
                    }
                } else 
                if (option.equals("Option=File (Output port)")) {
                    if (m_option.find()) {
                        filename=m_option.group(2);
                    }
                } else     
                if (option.equals("<File (Up input port)")) {
                     if (m_option1.find()) {
                        filename=m_option1.group(2);
                        port_id=CustomProgram.PortInputUP;
                    }
                } else 
                if (option.equals("<File (Down input port)")) {
                     if (m_option1.find()) {
                        filename=m_option1.group(2);
                         port_id=CustomProgram.PortInputDOWN;
                    }
                } else 
                if (option.equals("<File (Both input ports)")) {
                     if (m_option1.find()) {
                        filename=m_option1.group(2);
                         port_id=CustomProgram.PortInput;
                    }
                } else 
                if (option.equals(">File (Output port)")) {
                     if (m_option2.find()) {
                        filename=m_option2.group(2);
                    }
                }     
               if (properties.getBoolean("debug"))  System.out.println(filename);

                String type=properties.get("input"+i+"type").trim();
                String typelong=properties.get("input"+i+"type").trim();
                if (type.indexOf("-")>-1) {
                    type=type.substring(0, type.indexOf("-")).trim();
                }
                 Vector<Integer>ids=properties.getInputID(type, PortInput);
                for (int id:ids) {
                //--Ugly but easy :)
                if (type.equals("Sequence")) {
                    Sequence s=new Sequence(id);
                    MultipleSequences m=new MultipleSequences();
                    m.add(s);
                    if (typelong.indexOf("Fasta")>-1) {
                        if (typelong.indexOf("Use sequence name")>-1) {
                           m.outputFasta(filename);
                        }
                        if (typelong.indexOf("Use internal sequence id")>-1) {
                            m.outputFastaWithSequenceID(filename);
                        }
                        if (typelong.indexOf("Use abbreviation")>-1)   {
                            for (Sequence t:m.getSequences()) {
                                t.setName(t.getAbbreviate());
                                m.outputFasta(filename);
                            }
                        }
                    } else {
                        if (typelong.indexOf("Use sequence name")>-1) {
                            m.outputPhylipInterleave(filename,"");
                        }
                        if (typelong.indexOf("Use internal sequence id")>-1) {
                            m.outputPhylipInterleaveWithSequenceID(filename);
                        }
                        if (typelong.indexOf("Use abbreviation")>-1)   {
                            for (Sequence t:m.getSequences()) {
                                t.setName(t.getAbbreviate());
                                m.outputPhylipInterleave(filename,"");
                            }
                        }
                    }
                }
                if (type.equals("MultipleSequences")) {
                     MultipleSequences m=new MultipleSequences(id);
                    if (typelong.indexOf("Fasta")>-1) {
                        if (typelong.indexOf("Use sequence name")>-1) {
                           m.outputFasta(filename);
                        }
                        if (typelong.indexOf("Use internal sequence id")>-1) {
                            m.outputFastaWithSequenceID(filename);
                        }
                        if (typelong.indexOf("Use abbreviation")>-1)   {
                            for (Sequence t:m.getSequences()) {
                                t.setName(t.getAbbreviate());
                                m.outputFasta(filename);
                            }
                        }
                    } else {
                        if (typelong.indexOf("Use sequence name")>-1) {
                            m.outputPhylipInterleave(filename,"");
                        }
                        if (typelong.indexOf("Use internal sequence id")>-1) {
                            m.outputPhylipInterleaveWithSequenceID(filename);
                        }
                        if (typelong.indexOf("Use abbreviation")>-1)   {
                            for (Sequence t:m.getSequences()) {
                                t.setName(t.getAbbreviate());
                                m.outputPhylipInterleave(filename,"");
                            }
                        }
                    }
                }
                if (type.equals("Alignment")) {
                    Alignment m=new Alignment(id);
                    if (typelong.indexOf("Fasta")>-1) {
                        if (typelong.indexOf("Use sequence name")>-1) {
                           m.outputFasta(filename);
                        }
                        if (typelong.indexOf("Use internal sequence id")>-1) {
                            m.outputFastaWithSequenceID(filename);
                        }
                        if (typelong.indexOf("Use abbreviation")>-1)   {
                            for (Sequence t:m.getSequences()) {
                                t.setName(t.getAbbreviate());
                                m.outputFasta(filename);
                            }
                        }
                    } else {
                        if (typelong.indexOf("Use sequence name")>-1) {
                            m.outputPhylipInterleave(filename,"");
                        }
                        if (typelong.indexOf("Use internal sequence id")>-1) {
                            m.outputPhylipInterleaveWithSequenceID(filename);
                        }
                        if (typelong.indexOf("Use abbreviation")>-1)   {
                            for (Sequence t:m.getSequences()) {
                                t.setName(t.getAbbreviate());
                                m.outputPhylipInterleave(filename,"");
                            }
                        }
                    }
                }
                 if (type.equals("Ancestor")) {
                    Ancestor m=new Ancestor(id);
                    if (typelong.indexOf("Fasta")>-1) {
                        if (typelong.indexOf("Use sequence name")>-1) {
                           m.outputFasta(filename);
                        }
                        if (typelong.indexOf("Use internal sequence id")>-1) {
                            m.outputFastaWithSequenceID(filename);
                        }
                        if (typelong.indexOf("Use abbreviation")>-1)   {
                            for (Sequence t:m.getSequences()) {
                                t.setName(t.getAbbreviate());
                                m.outputFasta(filename);
                            }
                        }
                    } else {
                        if (typelong.indexOf("Use sequence name")>-1) {
                            m.outputPhylipInterleave(filename,"");
                        }
                        if (typelong.indexOf("Use internal sequence id")>-1) {
                            m.outputPhylipInterleaveWithSequenceID(filename);
                        }
                        if (typelong.indexOf("Use abbreviation")>-1)   {
                            for (Sequence t:m.getSequences()) {
                                t.setName(t.getAbbreviate());
                                m.outputPhylipInterleave(filename,"");
                            }
                        }
                    }

                }
                 if (type.equals("Matrix")) {
                    Matrix t=new Matrix(id);
                    t.Output(filename);
                }
                 if (type.equals("Tree")) {
                    Tree t=new Tree(id);
                     if (typelong.indexOf("Use original tree")>-1) {
                          t.outputNewick(filename);
                        }
                        if (typelong.indexOf("Use sequence_id")>-1) {
                           t.outputNewickWithSequenceID(filename);
                        }
                        if (typelong.indexOf("Use abbreviation")>-1)   {
                            t.outputNewickAbbreviate(filename);
                        }
                }
                 if (type.equals("MultipleTrees")) {
                    MultipleTrees t=new MultipleTrees(id);
                     if (typelong.indexOf("Use original tree")>-1) {
                         t.outputTrees(filename);                      ;
                        }
                        if (typelong.indexOf("Use sequence_id")>-1) {
                           for (Tree n:t.getTree()) {
                               n.setTree(n.getTreeSequenceID());
                           }
                           t.outputTrees(filename);
                        }
                        if (typelong.indexOf("Use abbreviation")>-1)   {
                           for (Tree n:t.getTree()) {
                               n.setTree(n.getTreeAbbreviate());
                           }
                            t.outputTrees(filename);
                        }
                  }
                if (type.equals("Results")) {
                    Results t=new Results(id);
                    t.Output(filename);
                }
                if (type.equals("Text")) {
                    Text t=new Text(id);
                    t.Output(filename);
                }
                if (type.equals("Unknown")) {
                    Unknown t=new Unknown(id);
                    t.Output(filename);
                }
                if (type.equals("TextFile")) {
                    TextFile t=new TextFile(id);
                    t.Output(filename);
                }
            } //--End option
            } //--for id
         }//--for input
        } catch(Exception e) {e.printStackTrace();}
    }


    @Override
    public String[] init_createCommandLine() {
          
           String[] com=new String[24];
           for (int i=0; i<com.length;i++) com[i]="";
           try {
           com[0]="cmd.exe";
           com[1]="/C";
           //com[2]=properties.getExecutable();
           int index=2;
           for (int i=0;i<21;i++) {
               if (properties.isSet("execute"+i)) {
                   com[index++]=properties.get("execute"+i);
               }
           }
           } catch(Exception e){e.printStackTrace();}
           return com;
    }

    

    @Override
    public void post_parseOutput() {
        for (int i=1;i<8;i++) {
            String type=properties.get("output"+i+"type");
            String filename=properties.get("output"+i+"name");
            if (Util.FileExists(filename)&&!filename.isEmpty()) {
                if (type.equals("Sequence")) {
                    MultipleSequences m=new MultipleSequences();
                    m.setName("Sequence from "+filename);
                    m.setNote("Created on "+Util.returnCurrentDateAndTime());
                    m.loadSequences(filename);
                    m.saveToDatabase();
                    for (Sequence s:m.getSequences()) {
                        properties.put("output_sequence_id", s.getId());
                    }
                }
                if (type.equals("MultipleSequences")) {
                    MultipleSequences m=new MultipleSequences();
                    m.setName("MultipleSequences from "+filename);
                    m.setNote("Created on "+Util.returnCurrentDateAndTime());
                    m.loadSequences(filename);
                    m.saveToDatabase();
                    properties.put("output_multiplesequences_id", m.getId());
                   
                }
                if (type.equals("Alignment")) {
                    Alignment m=new Alignment();
                    m.setName("Alignment from "+filename);
                    m.setNote("Created on "+Util.returnCurrentDateAndTime());
                    m.loadSequences(filename);
                    m.saveToDatabase();
                    properties.put("output_alignment_id", m.getId());
                }
                 if (type.equals("Ancestor")) {
                    Ancestor m=new Ancestor();
                    m.setName("Ancestor from "+filename);
                    m.setNote("Created on "+Util.returnCurrentDateAndTime());
                    m.loadSequences(filename);
                    m.saveToDatabase();
                    properties.put("output_ancestor_id", m.getId());

                }
                 if (type.equals("Matrix")) {
                    Matrix t=new Matrix(filename);
                    t.setName("Matrix from "+filename);
                    t.setNote("Created on "+Util.returnCurrentDateAndTime());
                    properties.put("output_matrix_id", t.getId());
                }
                 if (type.equals("Tree")) {
                   MultipleTrees m=new MultipleTrees();
                   m.setName("Tree from "+filename);
                   m.setNote("Created on "+Util.returnCurrentDateAndTime());
                   m.readNewick(filename);
                    m.saveToDatabase();
                    for (Tree t:m.getTree()) {
                        properties.put("output_tree_id", t.getId());
                    }
                }
                 if (type.equals("MultipleTrees")) {
                    MultipleTrees m=new MultipleTrees();
                    m.setName("MultipleTrees from "+filename);
                    m.setNote("Created on "+Util.returnCurrentDateAndTime());
                    m.readNewick(filename);
                    m.saveToDatabase();
                    properties.put("output_multipletrees_id", m.getId());
                  }
                if (type.equals("Results")) {
                    Results t=new Results(filename);
                    t.setName("Results from "+filename);
                    t.setNote("Created on "+Util.returnCurrentDateAndTime());
                    properties.put("output_results_id", t.getId());
                }
                if (type.equals("Text")) {
                    Text t=new Text(filename);
                    t.setName("Text from "+filename);
                    t.setNote("Created on "+Util.returnCurrentDateAndTime());
                    properties.put("output_text_id", t.getId());
                }
                if (type.equals("Unknown")) {
                    Unknown t=new Unknown(filename);
                    t.setName("Unknown from "+filename);
                    t.setNote("Created on "+Util.returnCurrentDateAndTime());
                    properties.put("output_unknown_id", t.getId());
                }

            }
        }

        //--Delete file
        for (int i=1; i<8;i++) {
            if (properties.getBoolean("input"+i+"delete")) {
                String filename=properties.get("input"+i+"name");
                if (!filename.isEmpty()) Util.deleteFile(filename);
            }
            if (properties.getBoolean("output"+i+"delete")) {
                String filename=properties.get("output"+i+"name");
                if (!filename.isEmpty()) Util.deleteFile(filename);
            }
        }
    }

 
   ////////////////////////////////////////////////////////////////////////////
   // FUNCTIONS


     @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }           
        return true;
    }

  
    ///////////////////////////////////////////////////////////////////////////
    ///GETTER/SETTERS
    
    
    @Override
    public int hashCode() {
        return Util.returnCount();
    }
      
}
