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



import biologic.*;
import program.RunProgram;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import workflows.workflow_properties;
import workflows.workflow_properties_dictionnary;


public class IfProgramClassLight extends RunProgram {


    ////////////////////////////////////////////////////////////////////////////
    /// Input / Output
    ///
    /// All internal variables should be declared here for ease of use.
      
       boolean debug=true;       
       boolean WeHaveResultsTest=true;
       
   /////////////////////////////////////////////////////////////////////////////
   /// CONSTANT    
       public static int FALSE=0;
       public static int TRUE=1;
       public static int NORESULTS=2;

    ////////////////////////////////////////////////////////////////////////////
    /// Constructor
    ///
    /// Current program class will be use only

    /**
     * Main default constructor
     * @param properties
     */
    public IfProgramClassLight(workflow_properties properties) {
       super(properties);       
       execute();
    }


   @Override
    public boolean init_checkRequirements() {
        //--Check if we have an input... 
        //--In fact, we verify the output...
        if (properties.Outputed().size()<1) {
            setStatus(status_BadRequirements,"Error. No input given for the If control.");
            return false;
        }
        
        if (!properties.isSet("IfTest")||properties.get("IfTest").isEmpty()) {
            setStatus(status_BadRequirements,"Error. No test selected for If (1).");
            return false;  
        }
        
        //--Verify the test data...
        //--CASE 1. Same test data as If        
        
        String IfTest=properties.get("IfTest");
            //--CASE 0. Value to test?
            if (IfTest.startsWith("Test for value")) {
                if (!properties.isSet("IfObjectID")||properties.get("IfObjectID").isEmpty()) {
                setStatus(status_BadRequirements,"Error. No variable to test. Please define one.");
                return false;  
            }
            //--CASE 1. Verify that we have a 
            if (!properties.isSet("IfObjectValue")) {
                 setStatus(status_BadRequirements,"Error. No value to test selected.");
                return false;  
            }
            //String IfObjectValue=properties.get("IfObjectValue");
        }
        
        if (IfTest.startsWith("Test for range")) {
            if (!properties.isSet("IfObjectMin")||!properties.isSet("IfObjectMax")) {
                 setStatus(status_BadRequirements,"Error. No range  test selected.");
                return false;  
            }
            
           if (properties.getInt("IfObjectMin")==0&&properties.getInt("IfObjectMax")==0) {
                setStatus(status_BadRequirements,"Error. Min and Max range are zero (0).");
                return false;  
           }
        }

        if (IfTest.startsWith("Test for results")) {
            if (!properties.isSet("IfObjectTestForResults")) {
                setStatus(status_BadRequirements,"Error. No test selected for the If value (2).");
                return false;  
            }
            String IfObjectTestForResults=properties.get("IfObjectTestForResults");
            //--Set variables for later use...
            if (IfObjectTestForResults.startsWith("We don")) {
               WeHaveResultsTest =false;
            } else {
                WeHaveResultsTest=true;
            }
        }
        //--Default return 
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
        String IfTest=properties.get("IfTest");  
        //--CASE 1. Test for value        
        //--CASE 2. Test for Range
         if (IfTest.startsWith("Test for value")||IfTest.startsWith("Test for range")) {
             int testresults=testForValueOrRange();
             if (testresults==NORESULTS) {
               setStatus(status_error,"Unable to test for results ("+IfTest+"). This is normally associated with format error.");    
               return false;
           }
            System.out.println(testresults);            
            boolean return_value=(testresults==TRUE);
            properties.put("IfStatus", return_value);
            setStatus(status_done,"Return value: "+return_value);                
         }
        //--CASE 3. Test for results
        if (IfTest.startsWith("Test for results")) {    
            System.out.println("Testing for result...");
            int testresults=testForResults();
           if (testresults==NORESULTS) {
               setStatus(status_error,"Unable to test for results. ");    
               return false;
           }
            System.out.println(testresults);
           boolean testresults_value=(testresults==TRUE);           
            boolean return_value=(WeHaveResultsTest==testresults_value); 
           properties.put("IfStatus", return_value);
           setStatus(status_done,"Return value: "+return_value);            
        }
        return true;
    }

    /**
     * Test for a results found or not.
     * @return (TRUE, FALSE or NORESULTS - constant define in this class)
     */
    int testForResults() {
       //--Verify the number of REAL input?
        int number_input=properties.Outputed().size();
        if (number_input==0)  {
            setStatus(status_BadRequirements,"Error. No input found (test for results).");
            return NORESULTS;
        }            
        if (number_input>1) {
            if (!properties.isSet("IfObjectID")) {
                 setStatus(status_BadRequirements,"Error. Multiple input for If, but no test variable selected"); 
                 return NORESULTS;
            }                        
        }        
        //--CASE 1 input...
        if (number_input==1)    {
             //...User defined a specific type... 
            if (properties.isSet("IfObjectID")) {
               String IfObjectID=properties.get("IfObjectID");
               String typeo=IfObjectID.substring(0, IfObjectID.indexOf("."));
               String variableo=IfObjectID.substring(IfObjectID.indexOf(".")+1); 
               //--CASE 1. Test only for an ID in the datatype specified
               if (typeo.startsWith("Possible variables.")) {                                     
                   Vector<Integer>idt=properties.getInputID(variableo.toLowerCase(), null);                   
                   if (idt.get(0)>0) {
                        return TRUE;
                   } else {
                      return FALSE;
                   }      
               } else {                   
                    //--More complicated tests, tests for 
                   Vector<Integer>idt=properties.getInputID(typeo.toLowerCase(), null);
                   if (idt.get(0)>0) {
                        return TRUE;
                   } else {
                      return FALSE;
                   }      
               }
            }
             //...User specified nothing...  
             String type=properties.Outputed().get(0);
             Integer id=properties.getInputID(type.toLowerCase());
             Vector<Integer>ids=properties.getInputID(type, null);
             //--Look for input
             //--No ID? return false;
             if (ids.size()<1) {
                 return FALSE;
             }
             if (ids.get(0)>0) {
                 return TRUE;
             } else {
                 return FALSE;
             }                       
        } else {
           //-- CASE 2, Multiple input... Check for specific...
            
           if (properties.isSet("IfObjectID")) {
             
               String IfObjectID=properties.get("IfObjectID");
               String typeo=IfObjectID.substring(0, IfObjectID.indexOf("."));
               String variableo=IfObjectID.substring(IfObjectID.indexOf(".")+1); 
              // System.out.println("CASE 2:" +IfObjectID);
              // System.out.println("CASE 2 typeo:" +typeo);
             //    System.out.println("CASE 2 variableo:" +variableo);               
               //--CASE 1. Test only for an ID in the datatype specified
               if (typeo.startsWith("Possible")) {                      
                   Vector<Integer>idt=properties.getInputID(variableo.toLowerCase(), null);                   
                   if (idt.get(0)>0) {
                        return TRUE;
                   } else {
                      return FALSE;
                   }      
               } else {  
                   System.out.println("CASE B");
                    //--More complicated tests, tests for 
                   Vector<Integer>idt=properties.getInputID(typeo.toLowerCase(), null);
                   if (idt.get(0)>0) {
                        return TRUE;
                   } else {
                      return FALSE;
                   }      
               }
            } 
            
        }
        //--By default
        return NORESULTS;
    }

    /**
     *  Test for a value or range
     * @return  (TRUE, FALSE or NORESULTS - constant define in this class)
     */
    int testForValueOrRange() {
        //--1. Get the package and variables...
        workflow_properties_dictionnary dict=new  workflow_properties_dictionnary();
        String IfTestValue=properties.get("IfValueTest");
        String IfObjectMin=properties.get("IfObjectMin");
        String IfObjectMax=properties.get("IfObjectMax");
        String IfObjectValue = properties.get("IfObjectValue");
        String IfTest=properties.get("IfTest");
        String IfObjectID=properties.get("IfObjectID");
        String typeo=IfObjectID.substring(0, IfObjectID.indexOf("."));
        String variableo=IfObjectID.substring(IfObjectID.indexOf(".")+1); 
        //--1.1 Debug value...
        if (debug) {
             
             System.out.println("IfTest:"+IfTest);
             System.out.println("IfObjectMin:"+IfObjectMin);
             System.out.println("IfObjectMax:"+IfObjectMax);
             System.out.println("IfTestValue:"+IfTestValue);
             System.out.println("IfObjectValue:"+IfObjectValue);
            
            
        }               
        //--2. Verify that we can test (verify the test parameters)
         if (IfTest.startsWith("Test for value")) {
              if (!properties.isSet("IfValueTest")||properties.get("IfValueTest").isEmpty()) {
               setStatus(status_BadRequirements,"Error. No equality test variable selected. (test value)");
               return NORESULTS;
              } 
         } else if (IfTest.startsWith("Test for range")) {
              if (!properties.isSet("IfObjectMin")||properties.get("IfObjectMin").isEmpty()) {
                    setStatus(status_BadRequirements,"Error. No minimum range found. (test value)");           
              }
              if (!properties.isSet("IfObjectMax")||properties.get("IfObjectMax").isEmpty()) {
                    setStatus(status_BadRequirements,"Error. No maximum range found. (test value)");
                    return NORESULTS;
              } 
              if (properties.getInt("IfObjectMin")==0&&properties.getInt("IfObjectMax")==0) {
                  setStatus(status_BadRequirements,"Error. No range equal 0 to 0. (test value)");           
                  return NORESULTS;
              }
              if (!dict.isNumeric(properties.get("IfObjectMax"))) {
                  setStatus(status_BadRequirements,"Error. Maximum range value is not numeric. (test value)");           
                  return NORESULTS;
              }
              if (!dict.isNumeric(properties.get("IfObjectMin"))) {
                  setStatus(status_BadRequirements,"Error. Minimum range value is not numeric. (test value)");
                  return NORESULTS;
              }                            
         } //--End test for range... 
         //--Verify the number of REAL input?
        int number_input=properties.Outputed().size();
        if (number_input==0)  setStatus(status_BadRequirements,"Error. No input found (test for value).");            
        if (number_input>1) {
            if (!properties.isSet("IfObjectID")) {
                 setStatus(status_BadRequirements,"Error. Multiple input for If, but no test variable selected (test for value)"); 
                 return NORESULTS;
            }                        
        }        
        //--3. Verify that we have a value to test
        String to_test_value=returnValue();
        if (debug) {
            System.out.println("value to evaluate in :"+typeo+"."+variableo+"->"+to_test_value);
        }
        if (to_test_value.isEmpty()) {
            setStatus(status_BadRequirements,"Error. No value to test. (test for value)");           
            return NORESULTS;
        }                
        //-- 4. Run  the differents tests...
        if (IfTest.startsWith("Test for value")) {                       
            //--4.1 test numeric
            boolean numeric_value=dict.isNumeric(IfObjectValue);                        
            boolean numeric_value_test=dict.isNumeric(to_test_value);                        
            Double value=0.0;
            Double value_test=0.0;
            if (numeric_value&&numeric_value_test) {
                 try {
                    value=Double.valueOf(IfObjectValue); 
                    value_test=Double.valueOf(to_test_value); 
                 } catch(Exception e){
                    setStatus(status_BadRequirements,"Error. Unable to convert value to test to numeric. Not numeric? (test for value)");           
                 return NORESULTS;
            }
            }
            String s=properties.get("IfValueTest");
            //--4.2 test equality 
           if (s.startsWith("Equals (=)")) {
               //--Test non numeric
               if (!numeric_value||!numeric_value_test) return (IfObjectValue.equals(to_test_value)?TRUE:FALSE);
               //--Test numeric
               return ((value==value_test)?TRUE:FALSE);
           }             
           
           if (s.startsWith("Smaller (<)")) {
                //--Test non numeric
               if (!numeric_value||!numeric_value_test) {
                   setStatus(status_BadRequirements,"Error. Non numeric value to test "+s+" (test for value)");           
                   return NORESULTS;
               }
               //--Test numeric
               return ((value_test<value)?TRUE:FALSE);
           }
           if (s.startsWith("Bigger (>)"))  {
             //--Test non numeric
               if (!numeric_value||!numeric_value_test) {
                   setStatus(status_BadRequirements,"Error. Non numeric value to test "+s+" (test for value)");           
                   return NORESULTS;
               }
               //--Test numeric
               return ((value_test>value)?TRUE:FALSE);
           }           
           if (s.startsWith("Smaller or equals (<=)")){
             //--Test non numeric
               if (!numeric_value||!numeric_value_test) {
                   setStatus(status_BadRequirements,"Error. Non numeric value to test "+s+" (test for value)");           
                   return NORESULTS;
               }
               //--Test numeric
               return ((value_test<=value)?TRUE:FALSE);
           } 
           if (s.startsWith("Bigger or equals (>=)")) {
           //--Test non numeric
               if (!numeric_value||!numeric_value_test) {
                   setStatus(status_BadRequirements,"Error. Non numeric value to test "+s+" (test for value)");           
                   return NORESULTS;
               }
               //--Test numeric
               return ((value_test>=value)?TRUE:FALSE);
           }  
           if (s.startsWith("Contains"))  {
           //--Test non numeric
               //--Test for regular expression and also for exact match
               //-- We expect the regular expression as in Java
                return (to_test_value.contains(IfObjectValue)?TRUE:FALSE);               
           }   
            if (s.startsWith("Contains (regular expression)"))  {
                   //--Test for regular expression and also for exact match
               //-- We expect the regular expression as in Java
                try {                                        
                    Pattern p=Pattern.compile(IfObjectValue);
                    Matcher m=p.matcher(to_test_value);
                    return (m.find()?TRUE:FALSE);                
                } catch(Exception e) {
                    setStatus(status_BadRequirements,"Error. Unable to compile regular expression: "+IfObjectValue+" (test for constains)\n"+e.getMessage());           
                    return NORESULTS;
                }
              } 
            if (s.startsWith("Equals (regular expression)")) {
                IfObjectValue=IfObjectValue.trim();
                  try {                                        
                    Pattern p=Pattern.compile(IfObjectValue);
                    Matcher m=p.matcher(to_test_value);
                   if(m.find()) {
                       String r=m.group();
                       return  (r.equalsIgnoreCase(IfObjectValue)?TRUE:FALSE);                        
                   } else {
                       return FALSE;
                   }
                } catch(Exception e) {
                    setStatus(status_BadRequirements,"Error. Unable to compile regular expression: "+IfObjectValue+" (test for constains)\n"+e.getMessage());           
                    return NORESULTS;
                }
                
            }
           //--Error?
          return  NORESULTS;
        } //--End test for value
        if (IfTest.startsWith("Test for range")) {            
            boolean numeric_value=dict.isNumeric(to_test_value);  
            if (!numeric_value) {
                 setStatus(status_BadRequirements,"Error. Value to test range is not numeric(test for value)");           
                 return NORESULTS;
            }
            //--Get the numeric value...
            Double min=properties.getDouble("IfObjectMin");
            Double max=properties.getDouble("IfObjectMax");
            Double value=0.0;
            try {
               value=Double.valueOf(to_test_value); 
            } catch(Exception e){
                 setStatus(status_BadRequirements,"Error. Unable to convert value to test range. Not numeric? (test for value)");           
                 return NORESULTS;
            }
            //--Actual test...
            if (value<=max&&value>=min) {
                return TRUE;
            } else {
                return FALSE;
            }
            
            
        }
        
        
        
        //--By default
        return NORESULTS;
    }
    
    public String returnValue() {
       int id=0;                 
       //--Get the variables 
       String IfObjectID=properties.get("IfObjectID");
       String typeo=IfObjectID.substring(0, IfObjectID.indexOf("."));
       String variableo=IfObjectID.substring(IfObjectID.indexOf(".")+1); 
       //--CASE 1. Test only for an ID in the datatype specified
       if (typeo.startsWith("Possible variables.")) {                                     
           setStatus(status_BadRequirements,"Error. No object selected to test value. (return value)");           
            return "";
       } else {                   
            //--More complicated tests, tests for 
           Vector<Integer>idt=properties.getInputID(typeo.toLowerCase(), null);
           id=idt.get(0);
           if (id==0) {
            setStatus(status_BadRequirements,"Error. No object ID found. (return value)");           
            return "";
           }
       }
       //--We have an ID, now we have to get the value...
                Biologic obj=null;
                 if (typeo.equalsIgnoreCase("Sequence")) {
                   obj=new Sequence(id);
                 }
                  if (typeo.equalsIgnoreCase("MultipleSequences")) {
                     obj=new MultipleSequences(id);
                  }
                  if (typeo.equalsIgnoreCase("Alignment")) {
                     obj=new Alignment(id);
                  }
                   if (typeo.equalsIgnoreCase("Ancestor")) {
                     obj=new Ancestor(id);
                   }
                   if (typeo.equalsIgnoreCase("Tree")) {
                    obj=new Tree(id);
                   }
                   if (typeo.equalsIgnoreCase("MultipleTrees")) {
                    obj=new MultipleTrees(id);
                   }
                   if (typeo.equalsIgnoreCase("Text")) {
                    obj=new Text(id);
                   }
                   
                    //tree2.add(new ForMutableTreeNode(properties,"this",obj.toString()));
                    if (variableo.equals("Name")) return obj.getName();
                    if (variableo.equals("Note")) return obj.getNote();
                    if (variableo.equals("this")) return obj.toString();
                    if (variableo.equals("Length")) {
                        if (obj instanceof Sequence) return String.valueOf(((Sequence)obj).getLen());
                        if (obj instanceof MultipleSequences) return String.valueOf(((MultipleSequences)obj).getSize());
                        if (obj instanceof Alignment) return String.valueOf(((Alignment)obj).getSize());
                        if (obj instanceof Ancestor) return String.valueOf(((Ancestor)obj).getSize());                        
                     }
                    if (variableo.equals("Sequence")) {
                         if (obj instanceof Sequence) return ((Sequence)obj).getSequence();
                    }
                    if (variableo.equals("Type")) {
                        if (obj instanceof Sequence) return ((Sequence)obj).getSequence_type();
                        if (obj instanceof MultipleSequences) return (((MultipleSequences)obj).isAA()?"AA":((MultipleSequences)obj).isDNA()?"DNA":((MultipleSequences)obj).isRNA()?"RNA":"Undefined");
                        if (obj instanceof Alignment) return (((Alignment)obj).isAA()?"AA":((Alignment)obj).isDNA()?"DNA":((Alignment)obj).isRNA()?"RNA":"Undefined");
                        if (obj instanceof Ancestor) return (((Ancestor)obj).isAA()?"AA":((Ancestor)obj).isDNA()?"DNA":((Ancestor)obj).isRNA()?"RNA":"Undefined");
                    }
                    if (variableo.equals("Fasta")) {
                         return obj.getFasta();
                    }
                    if (variableo.equals("Phylip")) {
                        return obj.getPhylip();                        
                    }
                    if (variableo.equals("Number of sequences")) {
                        if (obj instanceof MultipleSequences) return String.valueOf(((MultipleSequences)obj).getNbSequence());
                        if (obj instanceof Alignment) return String.valueOf(((Alignment)obj).getNbSequence());
                        if (obj instanceof Ancestor) return String.valueOf(((Ancestor)obj).getNbSequence());
                    }
                    

//                    tree2.add(new ForMutableTreeNode(properties,"Inverse (complement)",""));
//                    tree2.add(new ForMutableTreeNode(properties,"Inverse",""));
//                    //tree2.add(new ForMutableTreeNode(properties,"Number of gap",""));
//                    tree2.add(new ForMutableTreeNode(properties,"Composition (%)",""));
//                    //tree2.add(new ForMutableTreeNode(properties,"Number of intact column",""));
//                    tree2.add(new ForMutableTreeNode(properties,"Number of sequences",""+obj.getNbSequence()));
                    //tree2.add(new ForMutableTreeNode(properties,"Sequence with name",""));

         //--Default choice...
         return "";
    } //--End getValue

   
} //--End class





