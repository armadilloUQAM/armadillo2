package demo;

import biologic.Results;
import biologic.Text;
import biologic.Workflows;
import configuration.Config;
import configuration.Util;
import database.databaseFunction;

import com.google.gson.Gson;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingWorker;
import program.RunProgram;
import programs.kmeans;
import static tools.ChooseWorkflowJDialog.config;
import static tools.ChooseWorkflowJDialog.df;
import static tools.ChooseWorkflowJDialog.km;
import static tools.ChooseWorkflowJDialog.toolbox;
import tools.ChooseWorkflowMatrixTableModel;
import tools.ChooseWorkflowTableModel;
import workflows.armadillo_workflow.workflow_object;
import workflows.armadillo_workflow;
import workflows.workflow_properties;

/**
 *
 * @author lore26107809
 */
public class myExperiment_clustering {
    
     HashMap<String,String> OutputObjectType=new HashMap<String,String>(); //--Override some type
    
    public static HashMap<String, Integer>hash=new HashMap<String, Integer>();
    public static Config config=new Config();
    databaseFunction df=new databaseFunction();
    
    //--Mine workflow
    public static      Pattern pname=Pattern.compile("<name>(.*?)</name>");   
    public static      Pattern processor_start=Pattern.compile("<processor>");   
    public static      Pattern processor_end=Pattern.compile("</processor>");   
    public static      Pattern datalink_start=Pattern.compile("<datalink>|<link>");   
    public static      Pattern datalink_end=Pattern.compile("</datalink>|</link>");   
    public static      Pattern sink=Pattern.compile("<sink><node>(.*?)</node>");   
    public static    Pattern link_type=Pattern.compile("<sink><node>.*?<port>(.*?)</port>"); 
    public static      Pattern source=Pattern.compile("<source><node>(.*?)</node>"); 
    public static      Pattern processor_type=Pattern.compile("<type>(.*?)</type>"); //--Processor type
    public static     Pattern processor_script=Pattern.compile("<script>(.*?)</script>"); //--Processor type
   
    //--Workflow information
//             <id>100</id>
//  <title>TestIterator</title>
//  <description>Workflow to experiment with list iteration strategies. Look at metadata of nested workflow 'Concatenate' to see the current iteration strategy.</description>
//  <type uri="http://www.myexperiment.org/type.xml?id=1">Taverna 1</type>
//  <uploader resource="http://www.myexperiment.org/users/18" uri="http://www.myexperiment.org/user.xml?id=18">Marco Roos</uploader>
//  <created-at>Wed Nov 28 15:28:43 +0000 2007</created-at>
//  <updated-at>Wed Nov 28 15:28:43 +0000 2007</updated-at>
//    
    public static    Pattern w_id=Pattern.compile("<id>(.*?)</id>");   
    public static    Pattern w_title=Pattern.compile("<title>(.*?)</title>");   
    public static    Pattern w_description=Pattern.compile("<description>(.*?)</description>");  
    public static    Pattern w_type=Pattern.compile("<type.*>(.*?)</type>");  
    public static    Pattern w_created=Pattern.compile("<created-at>(.*?)</created-at>");     
    
    public myExperiment_clustering() {
            //--Variable
        OutputObjectType.put("Filter Sequences", "MultipleSequences");
        OutputObjectType.put("PhyML", "Tree");
        OutputObjectType.put("HGT Detector 3.2", "Distance data");
        OutputObjectType.put("LatTrans", "Distance data");
        OutputObjectType.put("Seq-Gen", "Alignment");
        OutputObjectType.put("Robinson&Fould", "Distance data");
        OutputObjectType.put("Random Tree Generator", "Tree");
        OutputObjectType.put("MAFFT", "Alignment");
        OutputObjectType.put("Muscle", "Alignment");
        OutputObjectType.put("Concatenate sequences", "MultipleSequences");
        OutputObjectType.put("RAxML", "Tree");
         OutputObjectType.put("Compare Distance", "Distance data");
    }
    
    public void Run(String workflow_path, String workflow_selection_file) {
       //--Create a new workflow output
        // Util.deleteFile(config.tmpDir() + "//myExperiment_protein.db");
        //df.New(config.tmpDir() + "//myExperiment_protein.db");
        //System.out.println("loading all workflow");
        System.out.println("Create matrix from workflow file...");
        df.Close();
        df.Open(workflow_path);    
        workflow_path=config.currentPath();
        System.out.println("Creating map...");
        cluster(0,workflow_path);
        cluster(1,workflow_path);
        cluster(2,workflow_path);
        cluster(3,workflow_path);
        cluster(4,workflow_path);
            cluster(6,workflow_path);
            cluster(7,workflow_path);
        System.out.println("done");
        System.exit(0);
    }
    
    public void Cluster(String workflow_path) {
       //--Create a new workflow output
        if (!Util.deleteFile(config.tmpDir() + "//myExperiment_cluster.db")) {
            System.out.println("Error!");
            System.exit(0);
        }
        df.New(config.tmpDir() + "//myExperiment_cluster.db");
        System.out.println("Loading all workflow in : "+workflow_path);      
       LinkedList<File> to_mine=new java.util.LinkedList<File>();
       Util u=new Util();       
        File data_directory=new File(workflow_path);
        //Test
        if (data_directory.listFiles()==null) {
            System.out.println("Unable to open datadir "+workflow_path);
            System.exit(0);
        } else {
            for (File f:data_directory.listFiles()) {
                if (f.getName().endsWith("workflow.txt")) to_mine.add(f);                
            }
        }   
        int total=to_mine.size();
        int current=0;
        for (File f:to_mine) {
            System.out.print("Mining ["+current+"/"+total+"] ");
            mine3(f);
            current++;
        }
        df.Close();
        df.Open(config.tmpDir() + "//myExperiment_cluster.db");   
        System.out.println("Creating map...");
        cluster(0,workflow_path);
        cluster(1,workflow_path);
        cluster(2,workflow_path);
        cluster(3,workflow_path);
        cluster(4,workflow_path);     
        //cluster1(workflow_path);
        //cluster2(workflow_path);
        //cluster3(workflow_path);
        System.out.println("done");
        System.exit(0);
    }
    
    public void mine3(File f) {
        Workflows tmp=new Workflows(new armadillo_workflow());
        tmp.loadmyExperiment(f.getAbsolutePath());
        tmp.getStatistics();
        if (tmp.workflow.workflow.work.size()>2) {            
           tmp.setName(f.getName());
           tmp.setNote(tmp.getNote()+"\nFilename:"+f.getAbsolutePath());
            tmp.saveToDatabase();
        }
    }
    
    /**
     * This will mine a workflow from myExperiment 
     * @param f workflow file
     */
     public void mine2(File f) {
      
          
          Workflows w=new Workflows(new armadillo_workflow());
          
            String buffer_processor="";
            String data_processor="";
            
            boolean in_processor=false;
            boolean in_datalink=false;
     
       try {
           BufferedReader br=new BufferedReader(new FileReader(f));
           StringBuilder buffer=new StringBuilder();
           while (br.ready()) {
               String stri=br.readLine().trim();
             
               Matcher m2=processor_start.matcher(stri);
               Matcher m3=processor_end.matcher(stri);               
               Matcher m4=datalink_start.matcher(stri);
               Matcher m5=datalink_end.matcher(stri);
               Matcher m_id=w_id.matcher(stri);
               Matcher m_title=w_title.matcher(stri);
               Matcher m_description=w_description.matcher(stri);
               Matcher m_type=w_type.matcher(stri);
               Matcher m_created=w_created.matcher(stri);
               
               if (m_id.find()) {
                   w.setNote(w.getNote()+"myExperiment id:"+m_id.group(1)+"\n");
               }
               if (m_title.find()) {
                   w.setNote(w.getNote()+"Title:"+m_title.group(1)+"\n");
               }
                if (m_description.find()) {
                   w.setNote(w.getNote()+"Description:"+m_description.group(1)+"\n");
               }               
                if (m_created.find()) {
                   w.setDate_created(m_created.group(1));
               }
               
              //--datalink
                if (m4.find()) {
                   data_processor+=stri;
                   in_datalink=true;
               } else 
               if (m5.find()) {
                   data_processor+=stri;
                   //System.out.println(data_processor);
                   String input="";
                   String output="";
                   String type="";
                   
                   //--Process
                   Matcher m_sink=sink.matcher(data_processor);
                   Matcher m_source=source.matcher(data_processor);
                   Matcher m_link_type=link_type.matcher(data_processor);
                   if (m_link_type.find()) {
                       type=m_link_type.group(1);
                       //System.out.println("type:"+type);
                   }
                    if (m_sink.find()) {
                        String name=m_sink.group(1).trim();
                        //System.out.println("sink:"+name);  
                        input=name;
                    }
                     if (m_source.find()) {
                        String name=m_source.group(1).trim();
                        //System.out.println("source:"+name);                        
                        output=name;
                    }      
                     
                   //--Create connector
                     if (!input.isEmpty()&&!output.isEmpty()) {
                         //--Locate object input
                         armadillo_workflow.workflow_connector output_connector=null;
                         armadillo_workflow.workflow_connector input_connector=null;
                         for (workflow_object wo:w.workflow.workflow.work) {
                             if (wo.properties.getName().equalsIgnoreCase(output)) {
                                 //System.out.println("Output:"+wo.properties.getName());
                                 if (wo.properties.isProgram()) {
                                     output_connector=wo.returnConnector(workflow_object.OUTPUT); 
                                 } else {
                                    output_connector=wo.returnConnector(1); 
                                 }
                             }
                         }
                         if (output_connector==null) {
                             //--Probably an input from the workflow
                             workflow_properties tmp=new workflow_properties();
                                tmp.put("ObjectType","OutputDatabase");  
                                tmp.put("Connector1Output","true");  
                                tmp.put("Connector1Output","true");  
                                tmp.put("outputType","Text");     
                                tmp.put("OutputText",true);     
                                tmp.put("InputText",true);     
                                tmp.put("defaultColor", "GREEN");
                                tmp.put("colorMode", "GREEN");   
                                
                                tmp.setName(output);
                                workflow_object o=w.workflow.createObject(tmp);                               
                                output_connector=o.returnConnector(1);
                         }                                                  
                         if (output_connector!=null) {
                            for (workflow_object wo:w.workflow.workflow.work) {
                               if (wo.properties.getName().equalsIgnoreCase(input)) {
                                   //System.out.println("Input:"+wo.properties.getName());
                                   input_connector=w.workflow.workflow.findCompatible(output_connector, wo);                                   
                               }
                           }
                         }

                         
                         if (input_connector!=null&&output_connector!=null) {
                             w.workflow.workflow.addConnection(output_connector, input_connector, type);
                         } else {
//                             System.out.println("Error "+output+"->"+input);
//                             System.out.println("input:"+input_connector+"output:"+output_connector);
                              //--Probably a workflow output
                             //-- Add to output
                              for (workflow_object wo:w.workflow.workflow.work) {
                             if (wo.properties.getName().equalsIgnoreCase(output)) {
                                 System.out.println("Output:"+wo.properties.getName());
                                 if (wo.properties.isProgram()) {
                                     wo.properties.put("Output"+input,true);
                                     w.workflow.workflow.createOutput_Objects(wo.returnConnector(1));
                                 } 
                             }
                         }
                             
                         }
                     }
                   //--Clear buffer 
                     data_processor="";
                   in_datalink=false;
               } else  if (in_datalink) data_processor+=stri;
               
               //--Processor
               if (m2.find()) {
                   buffer_processor+=stri;
                   in_processor=true;
               } else 
               if (m3.find()) {
                   buffer_processor+=stri;                   
                   //--Process
                   w.workflow.createObject(createWorkflow_object(buffer_processor));                 
                   //--Clear buffer 
                   buffer_processor="";
                   in_processor=false;
               } else  if (in_processor) buffer_processor+=stri;
               //buffer.append(br.readLine());                                 
           }
           //--
           br.close();
             } catch(Exception e) {
           System.out.println("Unable to mine "+f.getName());
            }
          //--Order object by execution order
          LinkedList <workflow_object> ex=w.workflow.workflow.outputExecution();
          int x=100;
          int y=65;
          for (workflow_object e:ex) {
              int tx=x;
              int ty=y;
              ArrayList<workflow_object> parent=findImmediateParent(w,e);
              if (parent.size()>0) {                                                                        
                  int max_x=0;
                  int max_y=0;
                  for (workflow_object wp:parent) {
                      if (wp.getProperties().getInt("x")>max_x) {
                          max_x=wp.getProperties().getInt("x");
                      }
                      if (wp.getProperties().getInt("y")>max_y) {
                          max_y=wp.getProperties().getInt("y");
                      }
                  }
                  tx=max_x+200;
                  ty=max_y;
              }              
              e.getProperties().put("x",tx);
              e.getProperties().put("y",ty);
              y+=75;
          }          
          System.out.println(w.workflowToString());       
    }
    

    workflow_properties createWorkflow_object(String buffer_processor) {
            //--Variable
            workflow_properties tmp=new workflow_properties();
            Matcher p_name=pname.matcher(buffer_processor);                   
            Matcher p_type=processor_type.matcher(buffer_processor);            
            Matcher p_script=processor_script.matcher(buffer_processor);            
            String name="Unknown";
            String type="Program";
            String script="";
            
             if (p_name.find()) name=p_name.group(1);
             if (p_type.find()) type=p_type.group(1);
             if (p_script.find()) {
                 script=p_script.group(1);
                 tmp.put("script",script);  
             }        
             
             tmp.setName(name);        
             tmp.put("ObjectType","Program");  
             tmp.put("myExperiment_type",type);
             
             if (type.equals("stringconstant")) {
                  tmp.put("ObjectType","OutputDatabase");  
                  tmp.put("Connector1Output","true");  
                  tmp.put("Connector1Output","true");  
                  tmp.put("outputType","Text");     
                  tmp.put("defaultColor", "GREEN");
                  tmp.put("colorMode", "GREEN");
                  
             } else {
                tmp.put("Connector0Output","True");  
                tmp.put("Connector1Conditional","True");  
                tmp.put("InputText","Connector2");              
                tmp.put("nbInput",1);     
                tmp.put("defaultColor", "BLUE");
                tmp.put("colorMode", "BLUE");
             }
             tmp.put("OutputText",true);                  
             tmp.put("output_text_id",0);
   
        return tmp;
    }
    
    public ArrayList<workflow_object> findImmediateParent(Workflows w, workflow_object o) {
        ArrayList<workflow_object>tmp=new ArrayList<workflow_object>();
        Vector<workflow_object> parents=w.workflow.workflow.findInput(o);
        if (parents==null||parents.size()==0) return tmp;        
        //--CAS 1. Immediate parent
        for (workflow_object input:parents) {         
                           
                  tmp.add(input);               
        }
        //--CAS 2. Recurse one level        
        return tmp;
     }
    
     public ArrayList<workflow_object> findImmediateParentDatabase(Workflows w, workflow_object o) {
        ArrayList<workflow_object>tmp=new ArrayList<workflow_object>();
        Vector<workflow_object> parents=w.workflow.workflow.findInput(o);
        if (parents==null||parents.size()==0) return tmp;        
        //--CAS 1. Immediate parent
        for (workflow_object input:parents) {         
              if (input.getProperties().get("ObjectType").equals("OutputDatabase")) {                  
                  tmp.add(input);
              } 
        }
        //--CAS 2. Recurse one level        
        return tmp;
     }
  
     public ArrayList<workflow_object> findImmediateChildObject(Workflows w, workflow_object o) {
        ArrayList<workflow_object>tmp=new ArrayList<workflow_object>();
        Vector<workflow_object> childs=w.workflow.workflow.findOutput(o);
        if (childs==null||childs.size()==0) return tmp;        
        //--CAS 1. Immediate parent
        for (workflow_object input:childs) {         
              if (input.getProperties().isProgram()) {                  
                  tmp.add(input);
              } 
        }
        //--CAS 2. Recurse one level
        if (tmp.size()==0) {
            for (workflow_object input:childs) {
                tmp.addAll(findImmediateChildObject(w, input));
            }
        }
        return tmp;
     }
     
     public ArrayList<workflow_object> findWorkflowInput(Workflows w) {
         ArrayList<workflow_object>tmp=new ArrayList<workflow_object>();
         //--Get all objects of type OutputDatabase
           for (workflow_object input:w.workflow.workflow.work) {         
              if (input.getProperties().get("ObjectType").equals("OutputDatabase")) {                  
                  tmp.add(input);
              } 
        }         
         return tmp;
     }
     
      public ArrayList<workflow_object> findWorkflowOutput(Workflows w) {
         ArrayList<workflow_object>tmp=new ArrayList<workflow_object>();         
         //--Find all program that have no child program
         for (workflow_object o:w.workflow.workflow.work) {
             //--If no child, add
             if (findImmediateChildObject(w,o).size()==0&&o.getProperties().isProgram()) {
                 tmp.add(o);
             }             
         }         
         return tmp;
     }
      
      /**
       * Find the time needed to get to this object
       */
      public int findTimeBefore(Workflows w, workflow_object o, int level) {
          ArrayList<workflow_object> tmp=findImmediateParent(w, o);
          level++;
          if (tmp.size()==0) return 0;
          //--Find the maximum time of child
          int max=0;
          for (workflow_object o2:tmp) {              
              int current_max=findTimeBefore(w, o2,level);                            
              if (current_max>max) max=current_max;
          }          
          if (level!=0) max+=o.getProperties().getInt("TimeRunning");
          return max;
      }
    
      /**
       * 
       * This will create some file with all the type of clustering 
       * @param workflow_path 
       * @param outputfilename
       */
     public void cluster(int mode, String workflow_path) {
         ////////////////////////////////////////////////////////////////////////
         /// VARIABLES               
                final int replicate=100;
                 Text text=null;   //--Output file                 
                 Text time_matrix=null;   //--Output file for weight matrix
                 int total_time=0;
                 String note="";  //--Output text note
                  StringBuilder data=new StringBuilder();
                 //String data="";   //--Output data
                 String data2="";  //--Output data for weight matrix 
                 int total_workflow=0; //--Total selected workflows
                 HashMap<String,Integer>propertiesClassName_Number=new HashMap<String,Integer>(); //--Program and count       
                 ArrayList<String>Ordering=new ArrayList<String>(); //--Ordering of the program
                 ArrayList<Workflows> workflow_names=new ArrayList<Workflows>();
                 Integer[][] matrix=null;  //--Output matrix
                 Integer[][] before_matrix=null;  //--Output matrix 
                 Float [] weight_vector=null;
                 
           ////////////////////////////////////////////////////////////////////////
                /// MAIN FUNCTION

                note+="**************************************************************************************************************\n";
                switch(mode) {
                    case 0:  
                            note+="Clustering workflow binary matrix";
                            text=new Text("binary.txt");
                            time_matrix=new Text("binarytm.txt");
                            text.setName("Binary clustering - "+Util.returnCurrentDateAndTime());
                            break;
                    case 1:  note+="Clustering workflow numerical matrix";
                            text=new Text("numerical.txt");
                            time_matrix=new Text("numericaltm.txt");
                            text.setName("Numerical clustering - "+Util.returnCurrentDateAndTime());            
                            break;
                    case 2:  note+="Clustering workflow time matrix";
                            text=new Text("time.txt");                            
                            text.setName("Time matrix clustering - "+Util.returnCurrentDateAndTime());            
                            break;
                     case 3:  note+="Clustering pair list";
                            text=new Text("pair.txt");
                             time_matrix=new Text("pairtm.txt");
                            text.setName("Pair list clustering - "+Util.returnCurrentDateAndTime());            
                            break;   
                     case 4:  note+="Clustering time order";
                            text=new Text("timeorder.txt");
                            text.setName("Time order list clustering - "+Util.returnCurrentDateAndTime());            
                            break;       
                       case 6:  note+="Clustering pair without keyword";
                            text=new Text("pairwo.txt");
                            time_matrix=new Text("pairwotm.txt");
                            text.setName("Pair list without keyword clustering - "+Util.returnCurrentDateAndTime());            
                            break;           
                         case 7:  note+="Clustering binary without keyword";
                            text=new Text("binarywo.txt");
                             time_matrix=new Text("binarywotm.txt");
                            text.setName("Cbinary list without keyword clustering - "+Util.returnCurrentDateAndTime());            
                            break;            
                }
                note+=" - "+Util.returnCurrentDateAndTime()+"\n";
                note+="**************************************************************************************************************\n";
                // 1. get the model (the workflow)
                System.out.println(text.getName());
         
      
          final ArrayList<Workflows> workflow_data=new ArrayList<Workflows>();
            Vector<Integer> workflow_ids=df.getAllWorkflowsID();            
             for (int id:workflow_ids) {           
                Workflows tmp=df.getWorkflows(id);  
                tmp.setSelected(true);
                workflow_data.add(tmp);
             }          
          int cou=workflow_data.size();
      
          //
          // Calculate time
          //
          
           //--Get the total
          HashMap<String,Integer>unique_name_time=new HashMap<String,Integer>(); 
          ArrayList<String> Ordering_time=new ArrayList<String>();
          HashMap<String, Integer>name_time=new  HashMap<String, Integer>();
          for (Workflows w:workflow_data) {
                            if (w.isSelected()) {                              
                                Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());
                                //--Get this workflow program 
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        //String name=o.getProperties().get("ClassName");
                                        String name=o.getProperties().getName().trim();
                                        unique_name_time.put(name,1);
                                                                     
                                    }
                                 }        
                            }   
                       }
                        Ordering.clear();
                      for (String name:unique_name_time.keySet()) {
                          Ordering_time.add(name);
                          //System.out.println(name);
                      }
                      Collections.sort(Ordering_time);
                      // Second iteration, create the matrix since we don't save the state

                                          
                      for (Workflows w:workflow_data) {
                            if (w.isSelected()) {
                                //System.out.println(w.getName());                             
                                  Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());
                                //--Get this workflow program 
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        //String name=o.getProperties().get("ClassName");
                                         String name=o.getProperties().getName().trim();
                                           Integer j=Ordering_time.indexOf(name);                                         
                                          
                                             int time=o.getProperties().getInt("TimeRunning");
                                              if (time==0) time=1;                                             
                                             if (name_time.containsKey(name)) {
                                                 name_time.put(name,(name_time.get(name)+time)/2);
                                             } else {
                                                 name_time.put(name, time);
                                             }
                                                                                           
                                    }
                                 }                                                      
                            } //--End workflow selected                               
                       } //--End workflow         
                      for (int i=0; i<Ordering_time.size();i++) {
                            total_time+=name_time.get(Ordering_time.get(i));                            
                        }
                      
                      Util u3=new Util();  
                      u3=new Util();
                        u3.open("realtime.txt");
                        for (int i=0; i<Ordering_time.size();i++) {
                            u3.println(""+name_time.get(Ordering_time.get(i))+"\t"+Ordering_time.get(i));                          
                        }
                        u3.close();
                         u3.open("meantime.txt");
                        for (int i=0; i<Ordering_time.size();i++) {
                            u3.println(""+(new Float(name_time.get(Ordering_time.get(i)))*10)/total_time);                          
                        }
                        u3.close();   
                 
          //--End calculate    
          
          
          //
          // Mode 0 - binary (without keyword)
          //
          if (mode==0) {
          total_workflow=0;
          for (Workflows w:workflow_data) {
                            if (w.isSelected()) {
                                workflow_names.add(w);
                                HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
                                 total_workflow++;
                                Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());                                
                                ArrayList<workflow_object> outputs=findWorkflowOutput(tmp_workflow);
                                ArrayList<workflow_object> inputs=findWorkflowInput(tmp_workflow);
                                 for (workflow_object o:inputs) {
                                    propertiesClassName_Number.put(" INPUT_"+o.properties.getName().trim().toLowerCase(), 1);
                                    //unique_name.put(" INPUT_"+o.properties.getName(),-1);
                                }
                                  //--Add to list the output using the predefined name matrix
//                                for (workflow_object o:outputs) { 
//                                    String name=o.properties.getName();                 
//                                    String output_name=OutputObjectType.get(name);
//                                    //keywords.put(o.properties.getName(), "");
//                                    if (output_name==null) {
//                                        System.out.println("Null name for "+name);
//                                        output_name=name;
//                                    }
//                                    //unique_name.put(" OUTPUT_"+output_name,-1);
//                                    propertiesClassName_Number.put(" OUTPUT_"+output_name,1);
//                                }
                                //--Get this workflow program 
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        
                               
                                        
                                        
                                        //String name=o.getProperties().get("ClassName");
                                         String name=" "+o.getProperties().getName();
                                        //if (name.indexOf("programs")>0) name=name.substring(9);

                                        //if (name.isEmpty()) name="*"+o.getProperties().getName();
                                        // Find unique name
                                         if (unique_name.containsKey(name)) {
                                            int i=1;
                                            while(unique_name.containsKey(name+i)) i++;
                                            name=name+i;
                                         } 
                                         unique_name.put(name, 1);
                                         propertiesClassName_Number.put(name, 1);
                                         //--keyword
                                         for (int i=0; i<100;i++) if (o.getProperties().isSet("Keyword"+i))  {
                                             String str[]=o.getProperties().get("Keyword"+i).split(",");
                                             for (String s:str) propertiesClassName_Number.put("Keyword_"+s.trim(),1);
                                         }
                                    } else if (o.getProperties().get("ObjectType").equals("OutputDatabase")) {                                                                                                                             
                                             //propertiesClassName_Number.put("INPUT_"+o.properties.getName().trim().toLowerCase(), 1);                                            
                                   }
                                 }        
                                 System.out.println("1-"+total_workflow*100/cou+"%");
                            }
                           
                       } 

                      for (String name:propertiesClassName_Number.keySet()) {
                          Ordering.add(name);                
                      }
                      Collections.sort(Ordering);
                      
                      //--Arbritrary weight                      
                      weight_vector=new Float[Ordering.size()];
                    
                      int indexf=0;
                      for (String s:Ordering) {
                          Float f=1.0f;
                          if (s.startsWith("Keyword"))  {
                              f=0.1f;
                          }                         
                          if (s.startsWith("INPUT"))  {
                              f=0.1f;
                          }                         
                          weight_vector[indexf++]=f;
                      }
                      Util u2=new Util();
                      u2.open("wb.txt");
                      for (Float f:weight_vector) {
                          u2.println(""+f);
                      }
                      u2.close();
                      // Second iteration, create the matrix since we don't save the state

                        matrix=new Integer[Ordering.size()][total_workflow];
                        for (int i=0;i<total_workflow;i++) { 
                            for (int j=0; j<Ordering.size();j++) {
                                matrix[j][i]=new Integer(0);
                            }
                        }

                      int index=0;
                      for (Workflows w:workflow_data) {
                            if (w.isSelected()) {                        
                                 HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
                                  Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());
                                //--Get this workflow program 
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        //String name=o.getProperties().get("ClassName");
                                         String name=" "+o.getProperties().getName();
                                        //if (name.indexOf("programs")>0) name=name.substring(9);
                                         //if (name.isEmpty()) name="*"+o.getProperties().getName();
                                        // Find unique name
                                         if (unique_name.containsKey(name)) {
                                            int i=1;
                                            while(unique_name.containsKey(name+i)) i++;  
                                            name=name+i;
                                         } 
                                         //--keyword
                                         for (int i=0; i<100;i++) if (o.getProperties().isSet("Keyword"+i))  {
                                             //--get the matrix position
                                                
                                             String str[]=o.getProperties().get("Keyword"+i).split(",");
                                             for (String s:str) {                                                 
                                                 Integer j=Ordering.indexOf("Keyword_"+s.trim());                                                  
                                                 matrix[j][index]++; 
                                             }
                                         
                                         }
                                        //--Add to list the output using the predefined name matrix
//                                        for (workflow_object o2:outputs) {                                    
//                                            String name2=o2.properties.getName();  
//                                            String output_name=OutputObjectType.get(name2);
//                                            if (OutputObjectType.get(name)==null) {
//                                                output_name=name;
//                                            }
//                                            Integer j=Ordering.indexOf(" OUTPUT_"+output_name);                                                 
//                                            if (j==-1) {
//                                                System.out.println(" OUTPUT_"+output_name);
//                                            } else 
//                                            matrix[j][index]++;  
//                                        }
                                         
                                         
                                         // We have the name, put the data in the matrix
                                          unique_name.put(name, 1);
                                          Integer j=Ordering.indexOf(name);                                
                                          matrix[j][index]++;  
                                          //--Calculate the time vector
                                          
                                          
                                          
                                          
                                    } else if (o.getProperties().get("ObjectType").equals("OutputDatabase")) {                                                                                 
                                            String name2=o.properties.getName().trim().toLowerCase();
                                            Integer j=Ordering.indexOf("INPUT_"+name2);                                             
                                            //System.out.println(j);
                                            if (j!=-1)
                                            matrix[j][index]++;                                          
                                    }
                                 }
                                 index++;
                                  System.out.println("2-"+index*100/cou+"%");
                            }   
                       }                     
                      
                        note+="Total workflows: "+total_workflow+"\n";                
                        note+="Distinct program: "+propertiesClassName_Number.size()+"\n";   
                      
     } //--End mode 0
           //
          // Mode 7 - binary (without keyword)
          //
          if (mode==7) {
              System.out.println("total workflow: "+workflow_data.size());
           total_workflow=0;
              for (Workflows w:workflow_data) {
                            if (w.isSelected()) {
                                workflow_names.add(w);
                                HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
                                 total_workflow++;
                                Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());                                
                              
                                //--Get this workflow program 
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                         String name=o.getProperties().getName().trim();
                                         if (unique_name.containsKey(name)) {
                                            int i=1;
                                            while(unique_name.containsKey(name+i)) i++;
                                            name=name+i;
                                         } 
                                         unique_name.put(name, 1);
                                         propertiesClassName_Number.put(name, 1);
                                        
                                    } 
                                 }        
                                 System.out.println("1-"+total_workflow*100/cou+"%");
                            }
                           
                       } 

                      for (String name:propertiesClassName_Number.keySet()) {
                          Ordering.add(name);                
                      }
                      Collections.sort(Ordering);
                      
                      //--Arbritrary weight                      
                      weight_vector=new Float[Ordering.size()];
                     
                      // Second iteration, create the matrix since we don't save the state

                        matrix=new Integer[Ordering.size()][total_workflow];
                        for (int i=0;i<total_workflow;i++) { 
                            for (int j=0; j<Ordering.size();j++) {
                                matrix[j][i]=new Integer(0);
                            }
                        }

                      int index=0;
                      
                      for (Workflows w:workflow_data) {
                            if (w.isSelected()) {                        
                                  HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
                                  Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());
                                //--Get this workflow program 
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        //String name=o.getProperties().get("ClassName");
                                         String name=o.getProperties().getName();
                                        //if (name.indexOf("programs")>0) name=name.substring(9);
                                         //if (name.isEmpty()) name="*"+o.getProperties().getName();
                                        // Find unique name
                                        if (unique_name.containsKey(name)) {
                                            int i=1;
                                            while(unique_name.containsKey(name+i)) i++;
                                            name=name+i;
                                         } 
                                         //--keyword
                                          //unique_name.put(name, 1);
                                          Integer j=Ordering.indexOf(name);     
                                          System.out.println(name + "\t"+j);
                                          matrix[j][index]++;  
                                    } 
                                 }
                                 index++;
                                  System.out.println("2-"+index*100/cou+"%");
                            }   
                       }
                        note+="Total workflows: "+total_workflow+"\n";                
                        note+="Distinct program: "+propertiesClassName_Number.size()+"\n";   
                      
     } //--End mode 7
          
          //  
          // MODE 1 Numerical
          //
                if (mode==1) {
                      HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
                       HashMap<String,String> keywords=new HashMap<String,String>();
                       
                        for (Workflows w:workflow_data) {
                            if (w.isSelected()) {
                                 workflow_names.add(w);
                                total_workflow++;                     
                                //--Get information
                                Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());
                                ArrayList<workflow_object> outputs=findWorkflowOutput(tmp_workflow);
                                ArrayList<workflow_object> inputs=findWorkflowInput(tmp_workflow);
                                
                                 for (workflow_object o:inputs) {
                                    keywords.put(o.properties.getName(), "");
                                    unique_name.put(" INPUT_"+o.properties.getName(),-1);
                                }
                                //--Add to list the output using the predefined name matrix
                                for (workflow_object o:outputs) { 
                                    String name=o.properties.getName();                 
                                    String output_name=OutputObjectType.get(name);
                                    //keywords.put(o.properties.getName(), "");
                                    if (output_name==null) {
                                        System.out.println("Null name for "+name);
                                        output_name=name;
                                    }
                                    unique_name.put(" OUTPUT_"+output_name,-1);
                                    keywords.put(output_name,"");
                                }
                                
                                
                                //--Get this workflow program 
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        String name=o.getProperties().getName();
                                        Integer count=propertiesClassName_Number.get(name);
                                        if (count==null) { count=1; } else { count++; }
                                        propertiesClassName_Number.put(name, count);
                                    }
                                 }               
                            }            
                        }             
                        Ordering.clear();
                        for (String program_name:propertiesClassName_Number.keySet()) {
                            Ordering.add(program_name);
                        }
                        Collections.sort(Ordering);
                        
                        matrix=new Integer[Ordering.size()][total_workflow];
                        for (int i=0;i<total_workflow;i++) { 
                            for (int j=0; j<Ordering.size();j++) {
                                matrix[j][i]=new Integer(0);
                            }
                        }

                        //--Create binary matrixwith count       
                        int index=0;
                       for (Workflows w:workflow_data) {
                            if (w.isSelected()) {               
                                //--Get information
                                Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());

                                //--Get this workflow program 
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        String name=o.getProperties().getName();
                                        int j=Ordering.indexOf(name);
                                        matrix[j][index]++;
                                    }
                                 }                 
                                 index++;
                                 System.out.println((index*100)/total_workflow);
                            }            
                        }    
                } //--End mode 1

                //
                // MODE 2 Time + keyword 
                //
                if (mode==2) {
                      //First iteration, create the name map

                      for (Workflows w:workflow_data) {
                            if (w.isSelected()) {
                                 workflow_names.add(w);
                                HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
                                 total_workflow++;
                                     Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());
                                //--Get this workflow program 
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        //String name=o.getProperties().get("ClassName");
                                         String name=o.getProperties().getName();
                                        //if (name.indexOf("programs")>0) name=name.substring(9);

                                        //if (name.isEmpty()) name="*"+o.getProperties().getName();
                                        // Find unique name
                                         if (unique_name.containsKey(name)) {
                                            int i=1;
                                            while(unique_name.containsKey(name+i)) i++;
                                            name=name+i;
                                         } 
                                         unique_name.put(name, 1);
                                         propertiesClassName_Number.put(name, 1);
                                         //--keyword
                                         
                                         
                                    }
                                 }        
                            }   
                       }
                      for (String name:propertiesClassName_Number.keySet()) {
                          Ordering.add(name);
                          //System.out.println(name);
                      }
                      Collections.sort(Ordering);
                      // Second iteration, create the matrix since we don't save the state

                        matrix=new Integer[Ordering.size()][total_workflow];
                        before_matrix=new Integer[Ordering.size()][total_workflow];
                        for (int i=0;i<total_workflow;i++) { 
                            for (int j=0; j<Ordering.size();j++) {
                                matrix[j][i]=new Integer(0);
                                  before_matrix[j][i]=new Integer(0);
                            }
                        }

                      int index=0;
                      for (Workflows w:workflow_data) {
                            if (w.isSelected()) {
                                //System.out.println(w.getName());
                                 HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
                                  Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());
                                //--Get this workflow program 
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        //String name=o.getProperties().get("ClassName");
                                         String name=o.getProperties().getName();
                                        //if (name.indexOf("programs")>0) name=name.substring(9);
                                         //if (name.isEmpty()) name="*"+o.getProperties().getName();
                                        // Find unique name
                                         if (unique_name.containsKey(name)) {
                                            int i=1;
                                            while(unique_name.containsKey(name+i)) i++;  
                                            name=name+i;
                                         } 
                                         // We have the name, put the data in the matrix
                                           unique_name.put(name, 1);
                                           Integer j=Ordering.indexOf(name);
                                              int time=o.getProperties().getInt("TimeRunning")+1;
                                            if (time<0) time=1;

                                           matrix[j][index]= (matrix[j][index]+time)/2;
                                           before_matrix[j][index]=findTimeBefore(tmp_workflow, o,0);                                   
                                    }
                                 }
                                 index++;
                                   System.out.println((index*100)/total_workflow);
                            }   
                       }
                        note+="Total workflows: "+total_workflow+"\n";                
                        note+="Distinct program: "+propertiesClassName_Number.size()+"\n";
                        note+="Summary:\n"; 
               } //--End 2
                //
                // Mode 3 pairlist
                //
                
                if (mode==3) {
                      //First iteration, create the name map
                      HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
                      HashMap<String,String> keywords=new HashMap<String,String>();
                              
                       for (Workflows w:workflow_data) {
                            if (w.isSelected()) {
                                workflow_names.add(w);                                                               
                                total_workflow++;
                                Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());     
                                //--Get a list of input/output
                                ArrayList<workflow_object> outputs=findWorkflowOutput(tmp_workflow);
//                                ArrayList<workflow_object> inputs=findWorkflowInput(tmp_workflow);
//                                //--Add to list the input
//                                for (workflow_object o:inputs) {
//                                    keywords.put(o.properties.getName(), "");
//                                    unique_name.put(" INPUT_"+o.properties.getName(),-1);
//                                }
//                                //--Add to list the output using the predefined name matrix
                                for (workflow_object o:outputs) { 
                                    String name=o.properties.getName();                 
                                    String output_name=OutputObjectType.get(name);
                                    //keywords.put(o.properties.getName(), "");
                                    if (output_name==null) {
                                        System.out.println("Null name for "+name);
                                        output_name=name;
                                    }
                                    unique_name.put("OUTPUT_"+output_name,-1);
                                    keywords.put(output_name,"");
                                }
                                //--Get this workflow program 
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        //String name=o.getProperties().get("ClassName");
                                        
                                        //-- Create class parent, enfant if not exist
                                        //--Class is concatenation of name
                                        String name=o.getProperties().getName();
                                        keywords.put(name,"");
                                        //--get previous and following parent-child
                                        ArrayList<workflow_object>parent=findImmediateParentDatabase(tmp_workflow,o);
                                        ArrayList<workflow_object>child=findImmediateChildObject(tmp_workflow,o);
                                        //--Construct
                                        String new_name="";                                        
//                                        for (workflow_object tm:parent) {                                                                                      
//                                            new_name=tm.properties.getName()+"->"+name;
//                                              keywords.put(tm.properties.getName()+"->"+name,"");
//                                            if (unique_name.containsKey(new_name)) {
//                                                unique_name.put(new_name, unique_name.get(new_name)+1); 
//                                            } else {
//                                                unique_name.put(new_name, 1); 
//                                            }
//                                        }
                                         if (child.size()==0) {
                                             keywords.put(" "+o.getProperties().getName(),"");
                                              unique_name.put(" "+o.getProperties().getName(), 1); 
                                        } else  
                                        for (workflow_object tm:child) {                                            
                                            new_name=" "+name+"->"+tm.properties.getName();
                                             keywords.put(new_name,"");
                                            if (unique_name.containsKey(new_name)) {
                                                unique_name.put(new_name, unique_name.get(new_name)+1); 
                                            } else {
                                                unique_name.put(new_name, 1); 
                                            }
                                        }                                    
                                    } else if (o.getProperties().get("ObjectType").equals("OutputDatabase")) {                                                                                 
                                            keywords.put(o.properties.getName(), "");
                                           unique_name.put("INPUT_"+o.properties.getName(),-1);                                     
                                    }
                                 }        
                            }   
                       }

                      for (String name:unique_name.keySet()) {
                          Ordering.add(name);                               
                      }
                       Collections.sort(Ordering);
                      // Second iteration, create the matrix since we don't save the state

                        matrix=new Integer[Ordering.size()][total_workflow];
                        for (int i=0;i<total_workflow;i++) { 
                            for (int j=0; j<Ordering.size();j++) {
                                matrix[j][i]=new Integer(0);
                            }
                        }

                      int index=0;
                      unique_name_time.clear();
                      for (Workflows w:workflow_data) {
                            if (w.isSelected()) {                                                       
                                 Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());                              
                                //--Get a list of input/output
                                ArrayList<workflow_object> outputs=findWorkflowOutput(tmp_workflow);
                                ArrayList<workflow_object> inputs=findWorkflowInput(tmp_workflow);
                                //--Add to list the input
//                                for (workflow_object o:inputs) {
//                                    String name=o.properties.getName();
//                                    Integer j=Ordering.indexOf(" INPUT_"+name);  
//                                    keywords.put("INPUT:"+name,"");
//                                    matrix[j][index]++;  
//                                }
//                                //--Add to list the output using the predefined name matrix
                                for (workflow_object o:outputs) {                                    
                                    String name=o.properties.getName();  
                                    String output_name=OutputObjectType.get(name);
                                    if (OutputObjectType.get(name)==null) {
                                        output_name=name;
                                    }
                                    Integer j=Ordering.indexOf("OUTPUT_"+output_name);     
                                    keywords.put("OUTPUT:"+name,"");
                                    matrix[j][index]++;  
                                }
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        //String name=o.getProperties().get("ClassName");
                                         String name=o.getProperties().getName();
                                         keywords.put(name, "");
                                            //--get previous and following parent-child
                                        ArrayList<workflow_object>parent=findImmediateParentDatabase(tmp_workflow,o);
                                        ArrayList<workflow_object>child=findImmediateChildObject(tmp_workflow,o);
                                        //--Construct
                                        String new_name="";
//                                            for (workflow_object tm:parent) {
//                                                new_name=tm.properties.getName()+"->"+name;
//                                                Integer j=Ordering.indexOf(new_name);                                
//                                                matrix[j][index]++;  
//                                            }
                                          if (child.size()==0) {
                                             new_name=" "+o.getProperties().getName();
                                              Integer j=Ordering.indexOf(new_name);                                
                                              //--
                                              unique_name_time.put(new_name,name_time.get(o.getProperties().getName()));
                                              matrix[j][index]++;  
                                        } else     
                                        for (workflow_object tm:child) {
                                                new_name=" "+name+"->"+tm.properties.getName();
                                                Integer j=Ordering.indexOf(new_name);  
                                                unique_name_time.put(new_name,name_time.get(name)+name_time.get(tm.properties.getName()));
                                                matrix[j][index]++;  
                                            }                                                    
                                    } else if (o.getProperties().get("ObjectType").equals("OutputDatabase")) {                                                                                 
                                            String name2=o.properties.getName();
                                            Integer j=Ordering.indexOf("INPUT_"+name2);                                             
                                           
                                            matrix[j][index]++;                                          
                                    }
                                 }
                                 index++;
                                 System.out.println((index*100)/total_workflow);
                            }   
                       }
                        note+="Total workflows: "+total_workflow+"\n";                
                        note+="Distinct program: "+propertiesClassName_Number.size()+"\n";       
                        //--Output keyword to file
                        Util u=new Util();
                        u.open("keyword.txt");
                        LinkedList<String> stri=new LinkedList<String>();
                        stri.addAll(keywords.keySet());
                        Collections.sort(stri);
                        //--Create a json                        
                        class cl {   }
                        ArrayList<myExperiment_clustering_data>output=new ArrayList<myExperiment_clustering_data>();
                       int i=0;
                        for (String s:stri) {
                            output.add(new myExperiment_clustering_data(i,s));
                            i++;
                        }
                        System.out.println(output.size());
                        String json = new Gson().toJson(output); // anyObject = List<Bean>, Map<K, Bean>, Bean, String, etc..                        
                        u.println(json);                     
                        u.close();
                         u=new Util();
                         u.open("pairtimeweight.txt");
                         for (i=0; i<Ordering.size();i++) {
                             String name=Ordering.get(i);
                            Integer time=unique_name_time.get(name);
                             if (time==null) time=0;
                              Float time2=new Float(time);
                             time2=time2*10/total_time;
                             u.println(""+time2);
                         }
                          for (i=0; i<Ordering.size();i++) {
                                 u.println(Ordering.get(i));                             
                          }
                         u.close();
                        
                        
                        
                        
               } //--End 3

                //
                // MODE 4 TimeOrder
                //
                if (mode==4) {
                      //First iteration, create the name map

                      for (Workflows w:workflow_data) {
                            if (w.isSelected()) {
                               
                                 workflow_names.add(w);
                                HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
                                 total_workflow++;
                                     Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());
                                //--Get this workflow program 
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        //String name=o.getProperties().get("ClassName");
                                         String name=o.getProperties().getName();
                                        //if (name.indexOf("programs")>0) name=name.substring(9);

                                        //if (name.isEmpty()) name="*"+o.getProperties().getName();
                                        // Find unique name
                                         //--Unique 
//                                         if (unique_name.containsKey(name)) {
//                                            int i=1;
//                                            while(unique_name.containsKey(name+i)) i++;
//                                            name=name+i;
//                                         } 
//                                         unique_name.put(name, 1);
//                                         propertiesClassName_Number.put(name, 1);
                                         //Ordering.add(name);
                                             propertiesClassName_Number.put(name, 0);
                                    }
                                 }        
                            }   
                       }
                      for (String name:propertiesClassName_Number.keySet()) {
                          Ordering.add(name);
                          //System.out.println(name);
                      }
                      // Second iteration, create the matrix since we don't save the state

                        matrix=new Integer[Ordering.size()+1][total_workflow+1];
                        before_matrix=new Integer[Ordering.size()+1][total_workflow+1];
                        for (int i=0;i<total_workflow;i++) { 
                            for (int j=0; j<Ordering.size();j++) {
                                matrix[j][i]=new Integer(0);
                                  before_matrix[j][i]=new Integer(0);
                            }
                        }

                      int index=0;
                      for (Workflows w:workflow_data) {
                            if (w.isSelected()) {
                                 
                                //--Get execution order
                              
                                //System.out.println(w.getName());
                                 HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
                                  Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());
                                //--Order the element tritopo
                                tmp_workflow.workflow.workflow.outputExecution();
                                //--Get this workflow program 
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        //String name=o.getProperties().get("ClassName");
                                         String name=o.getProperties().getName();
                                        //if (name.indexOf("programs")>0) name=name.substring(9);
                                         //if (name.isEmpty()) name="*"+o.getProperties().getName();
                                        // Find unique name
//                                         if (unique_name.containsKey(name)) {
//                                            int i=1;
//                                            while(unique_name.containsKey(name+i)) i++;  
//                                            name=name+i;
//                                         } 
//                                         // We have the name, put the data in the matrix
//                                           unique_name.put(name, 1);
                                           Integer j=Ordering.indexOf(name);
                                            int time=o.getProperties().getInt("Order");
                                            //if (time<0) time=1;
                                           if (matrix[j][index]!=0) {
                                               matrix[j][index]=(matrix[j][index]+time)/2;
                                           } else 
                                            matrix[j][index]=time;
                                           before_matrix[j][index]=0;
                                           //findTimeBefore(tmp_workflow, o);                                   
                                    }
                                 }
                                 index++;
                                   System.out.println((index*100)/total_workflow);
                            }   
                       }
                        note+="Total workflows: "+total_workflow+"\n";                
                        note+="Distinct program: "+propertiesClassName_Number.size()+"\n";
                        note+="Summary:\n"; 
               } //--End 4
          
                //
                // Mode 5 pairlist -> time
                //
                
                if (mode==5) {
                      //First iteration, create the name map
                       HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
                      HashMap<String,String> keywords=new HashMap<String,String>();
                              
                       for (Workflows w:workflow_data) {
                            if (w.isSelected()) {
                                workflow_names.add(w);                                                               
                                total_workflow++;
                                Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());     
                                //--Get a list of input/output
                                ArrayList<workflow_object> outputs=findWorkflowOutput(tmp_workflow);
                                ArrayList<workflow_object> inputs=findWorkflowInput(tmp_workflow);
                                //--Add to list the input
                                for (workflow_object o:inputs) {
                                    keywords.put(o.properties.getName(), "");
                                    unique_name.put(" INPUT_"+o.properties.getName(),-1);
                                }
                                //--Add to list the output using the predefined name matrix
                                for (workflow_object o:outputs) { 
                                    String name=o.properties.getName();                 
                                    String output_name=OutputObjectType.get(name);
                                    //keywords.put(o.properties.getName(), "");
                                    if (output_name==null) {
                                        System.out.println("Null name for "+name);
                                        output_name=name;
                                    }
                                    unique_name.put(" OUTPUT_"+output_name,-1);
                                    keywords.put(output_name,"");
                                }
                                //--Get this workflow program 
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        //String name=o.getProperties().get("ClassName");
                                        
                                        //-- Create class parent, enfant if not exist
                                        //--Class is concatenation of name
                                        String name=o.getProperties().getName();
                                        keywords.put(name,"");
                                        //--get previous and following parent-child
                                        ArrayList<workflow_object>parent=findImmediateParentDatabase(tmp_workflow,o);
                                        ArrayList<workflow_object>child=findImmediateChildObject(tmp_workflow,o);
                                        //--Construct
                                        String new_name="";                                        
                                        for (workflow_object tm:parent) {                                                                                      
                                            new_name=tm.properties.getName()+"->"+name;
                                              keywords.put(tm.properties.getName()+"->"+name,"");
                                            if (unique_name.containsKey(new_name)) {
                                                unique_name.put(new_name, unique_name.get(new_name)+1); 
                                            } else {
                                                unique_name.put(new_name, 1); 
                                            }
                                        }
                                        for (workflow_object tm:child) {                                            
                                            new_name=name+"->"+tm.properties.getName();
                                             //keywords.put(tm.properties.getName(),"");
                                            if (unique_name.containsKey(new_name)) {
                                                unique_name.put(new_name, unique_name.get(new_name)+1); 
                                            } else {
                                                unique_name.put(new_name, 1); 
                                            }
                                        }                                    
                                    }
                                 }        
                            }   
                       }

                      for (String name:unique_name.keySet()) {
                          Ordering.add(name);                               
                      }
                       Collections.sort(Ordering);
                      // Second iteration, create the matrix since we don't save the state

                        matrix=new Integer[Ordering.size()][total_workflow];
                        for (int i=0;i<total_workflow;i++) { 
                            for (int j=0; j<Ordering.size();j++) {
                                matrix[j][i]=new Integer(0);
                            }
                        }

                      int index=0;
                      for (Workflows w:workflow_data) {
                            if (w.isSelected()) {                                                       
                                 Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());  
                                //--Order the element tritopo
                                tmp_workflow.workflow.workflow.outputExecution();
                                //--Get a list of input/output
                                ArrayList<workflow_object> outputs=findWorkflowOutput(tmp_workflow);
                                ArrayList<workflow_object> inputs=findWorkflowInput(tmp_workflow);
                                //--Add to list the input
                                for (workflow_object o:inputs) {
                                    String name=o.properties.getName();
                                    Integer j=Ordering.indexOf(" INPUT_"+name);  
                                    keywords.put("INPUT:"+name,"");
                                    matrix[j][index]++;  
                                }
                                //--Add to list the output using the predefined name matrix
                                for (workflow_object o:outputs) {                                    
                                    String name=o.properties.getName();  
                                    String output_name=OutputObjectType.get(name);
                                    if (OutputObjectType.get(name)==null) {
                                        output_name=name;
                                    }
                                    Integer j=Ordering.indexOf(" OUTPUT_"+output_name);     
                                    keywords.put("OUTPUT:"+name,"");
                                    matrix[j][index]++;  
                                }
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        //String name=o.getProperties().get("ClassName");
                                         String name=o.getProperties().getName();
                                         keywords.put(name, "");
                                            //--get previous and following parent-child
                                        ArrayList<workflow_object>parent=findImmediateParentDatabase(tmp_workflow,o);
                                        ArrayList<workflow_object>child=findImmediateChildObject(tmp_workflow,o);
                                        //--Construct
                                        String new_name="";
                                            for (workflow_object tm:parent) {
                                                new_name=tm.properties.getName()+"->"+name;
                                                Integer j=Ordering.indexOf(new_name);                                                                                
                                                matrix[j][index]++;  
                                            }
                                            for (workflow_object tm:child) {
                                                new_name=name+"->"+tm.properties.getName();
                                                Integer j=Ordering.indexOf(new_name);                                
                                                matrix[j][index]++;  
                                            }                                                    
                                    }
                                 }
                                 index++;
                                 System.out.println((index*100)/total_workflow);
                            }   
                       }
                        note+="Total workflows: "+total_workflow+"\n";                
                        note+="Distinct program: "+propertiesClassName_Number.size()+"\n";       
                        //--Output keyword to file
                        Util u=new Util();
                        u.open("keyword.txt");
                        LinkedList<String> stri=new LinkedList<String>();
                        stri.addAll(keywords.keySet());
                        Collections.sort(stri);
                        //--Create a json                        
                        class cl {   }
                        ArrayList<myExperiment_clustering_data>output=new ArrayList<myExperiment_clustering_data>();
                       int i=0;
                        for (String s:stri) {
                            output.add(new myExperiment_clustering_data(i,s));
                            i++;
                        }
                        System.out.println(output.size());
                        String json = new Gson().toJson(output); // anyObject = List<Bean>, Map<K, Bean>, Bean, String, etc..                        
                        u.println(json);                     
                        u.close();
                        
               } //--End 5
                 //
                // Mode 6 pairlist
                //
                
                if (mode==6) {
                      //First iteration, create the name map
                      HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
                      HashMap<String,String> keywords=new HashMap<String,String>();
                              
                       for (Workflows w:workflow_data) {
                            if (w.isSelected()) {
                                workflow_names.add(w);                                                               
                                total_workflow++;
                                Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());     
                                //--Get a list of input/output
                                ArrayList<workflow_object> outputs=findWorkflowOutput(tmp_workflow);
//                                ArrayList<workflow_object> inputs=findWorkflowInput(tmp_workflow);
//                                //--Add to list the input
//                                for (workflow_object o:inputs) {
//                                    keywords.put(o.properties.getName(), "");
//                                    unique_name.put(" INPUT_"+o.properties.getName(),-1);
//                                }
//                                //--Add to list the output using the predefined name matrix
                                
                                //--Get this workflow program 
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        //String name=o.getProperties().get("ClassName");
                                        
                                        //-- Create class parent, enfant if not exist
                                        //--Class is concatenation of name
                                        String name=o.getProperties().getName();
                                        keywords.put(name,"");
                                        //--get previous and following parent-child
                                        ArrayList<workflow_object>parent=findImmediateParentDatabase(tmp_workflow,o);
                                        ArrayList<workflow_object>child=findImmediateChildObject(tmp_workflow,o);
                                        //--Construct
                                        String new_name="";                                        
//                                        for (workflow_object tm:parent) {                                                                                      
//                                            new_name=tm.properties.getName()+"->"+name;
//                                              keywords.put(tm.properties.getName()+"->"+name,"");
//                                            if (unique_name.containsKey(new_name)) {
//                                                unique_name.put(new_name, unique_name.get(new_name)+1); 
//                                            } else {
//                                                unique_name.put(new_name, 1); 
//                                            }
//                                        }
                                        if (child.size()==0) {
                                             keywords.put(" "+o.getProperties().getName(),"");
                                              unique_name.put(" "+o.getProperties().getName(), 1); 
                                        } else                                         
                                        for (workflow_object tm:child) {                                            
                                            new_name=name+"->"+tm.properties.getName();
                                             keywords.put(tm.properties.getName()+"->"+name,"");
                                            if (unique_name.containsKey(new_name)) {
                                                unique_name.put(new_name, unique_name.get(new_name)+1); 
                                            } else {
                                                unique_name.put(new_name, 1); 
                                            }
                                        }                                    
                                    } 
                                 }        
                            }   
                       }

                      for (String name:unique_name.keySet()) {
                          Ordering.add(name);                               
                      }
                       Collections.sort(Ordering);
                      // Second iteration, create the matrix since we don't save the state
                    
                     //--Calculate the time vector
                         Float[] time_vector=new Float[Ordering.size()]; 
                      
                       
                        matrix=new Integer[Ordering.size()][total_workflow];
                        for (int i=0;i<total_workflow;i++) { 
                            for (int j=0; j<Ordering.size();j++) {
                                matrix[j][i]=new Integer(0);
                            }
                        }

                      int index=0;
                      for (Workflows w:workflow_data) {
                            if (w.isSelected()) {                                                       
                                 Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
                                //--Load a copy of the workflow 
                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());                              
                                //--Get a list of input/output
                                ArrayList<workflow_object> outputs=findWorkflowOutput(tmp_workflow);
                                ArrayList<workflow_object> inputs=findWorkflowInput(tmp_workflow);
                                //--Add to list the input
//                                for (workflow_object o:inputs) {
//                                    String name=o.properties.getName();
//                                    Integer j=Ordering.indexOf(" INPUT_"+name);  
//                                    keywords.put("INPUT:"+name,"");
//                                    matrix[j][index]++;  
//                                }
//                                //--Add to list the output using the predefined name matrix
                              
                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
                                    //--We have a program
                                    if (o.getProperties().isProgram()) {
                                        //String name=o.getProperties().get("ClassName");
                                         String name=o.getProperties().getName();
                                         int time=o.getProperties().getInt("time");
                                         keywords.put(name, "");
                                            //--get previous and following parent-child
                                        ArrayList<workflow_object>parent=findImmediateParentDatabase(tmp_workflow,o);
                                        ArrayList<workflow_object>child=findImmediateChildObject(tmp_workflow,o);
                                        //--Construct
                                        String new_name="";
//                                            for (workflow_object tm:parent) {
//                                                new_name=tm.properties.getName()+"->"+name;
//                                                Integer j=Ordering.indexOf(new_name);                                
//                                                matrix[j][index]++;  
//                                            }
                                          if (child.size()==0) {
                                             new_name=" "+o.getProperties().getName();
                                              Integer j=Ordering.indexOf(new_name);                                
                                               matrix[j][index]++;  
                                        } else  
                                            for (workflow_object tm:child) {
                                                new_name=name+"->"+tm.properties.getName();
                                                Integer j=Ordering.indexOf(new_name);                                
                                                matrix[j][index]++;  
                                            }                                                    
                                    } 
                                 }
                                 index++;
                                 System.out.println((index*100)/total_workflow);
                            }   
                       }
                        note+="Total workflows: "+total_workflow+"\n";                
                        note+="Distinct program: "+propertiesClassName_Number.size()+"\n";       
                        //--Output keyword to file
                        Util u=new Util();
                        u.open("keyword.txt");
                        LinkedList<String> stri=new LinkedList<String>();
                        stri.addAll(keywords.keySet());
                        Collections.sort(stri);
                        //--Create a json                        
                        class cl {   }
                        ArrayList<myExperiment_clustering_data>output=new ArrayList<myExperiment_clustering_data>();
                       int i=0;
                        for (String s:stri) {
                            output.add(new myExperiment_clustering_data(i,s));
                            i++;
                        }
                        System.out.println(output.size());
                        String json = new Gson().toJson(output); // anyObject = List<Bean>, Map<K, Bean>, Bean, String, etc..                        
                        u.println(json);                     
                        u.close();
                        
               } //--End 6
                
                
                 ///////////////////////////////////////////////////////////////////////
                 /// SAVE DATA
                    data.append(total_workflow+"\t"+Ordering.size()+"\n");
                    data2+=total_workflow+"\t"+Ordering.size()+"\n";
                   
                     for (int j=0; j<Ordering.size();j++) {
                        String buffer="";   
                        String buffer2=""; 
                         for (int i=0;i<total_workflow;i++) {        
                             buffer+=new Float(matrix[j][i]).toString()+"\t";                     
                             if (mode==2) buffer2+=new Float(before_matrix[j][i]).toString()+"\t";      
                         }
                        data.append(buffer+"\n");
                        //msg(buffer);
                         if (mode==2) {
                             data2+=buffer2+"\n";
                         }
                      }
                     note+="Workflows:\n";
                  for (Workflows w:workflow_names) note+="ID"+w.getId()+" :\t"+w.getName()+"\n";
                     note+="Programs:\n";
                  for (int j=0; j<Ordering.size();j++) {
                         //System.out.println(j+"\t"+Ordering.get(j)+"\t");
                      //--Count for this id
                      int xou=0;
                      for (int k=0;k<total_workflow;k++) xou+=matrix[j][k];
                      note+=j+"\t"+Ordering.get(j)+"\t"+xou+"\n";
                  }  
                 System.out.println("Saving...");                 
                 Util u=new Util();
                 System.out.println(workflow_path+File.separator+text.getFilename());
                 u.open(workflow_path+File.separator+text.getFilename());
                 u.println(data.toString()+"\n"+note);
                 u.close();   
                 u.open(workflow_path+File.separator+text.getFilename()+".stats.txt");
                 u.println(note);
                 u.close();   
                 
               
                   
                 
                 
//                 text.setNote(note);
//                 text.setText(data.toString());
//                 text.saveToDatabase();                 
                
                 
//                 note+="**************************************************************************************************************\n";
                 //msg(note);
                 if (mode==2) {
                      System.out.println("Saving timeweight.txt ...");    
                      u.open(workflow_path+File.separator+"timeweight.txt");                      
                      u.println(data2.toString());
                      u.close();   
                 }    
                   //System.exit(0);     
                 //--Saving the matrix json
                 myExperiment_clustering_datamatrix ma=new myExperiment_clustering_datamatrix();
                 ma.matrix=matrix;
                 ma.matrix_name=text.getName();
                  for (Workflows w:workflow_names) ma.workflow_name.add(w.getName());
                 ma.properties_name=Ordering;
             
//                 u.open(workflow_path+File.separator+text.getFilename()+".stats.txt.json");
//                   String json = new Gson().toJson(ma); // anyObject = List<Bean>, Map<K, Bean>, Bean, String, etc..                        
//                        u.println(json);                     
//                        u.close();
     
    
     }
     
       /////////////////////////////////////////////////////////////////////////////
   /// Worker   
//   private void clusterWorkflow() {
//      
//      final int mode=this.ClusteringMode_jComboBox.getSelectedIndex();
//      final int replicate=Integer.valueOf((String)this.ReplicatejComboBox.getSelectedItem());
//       ChooseWorkflowTableModel qt = (ChooseWorkflowTableModel) this.jTable1.getModel();
//      final ArrayList<Workflows> workflow_data=qt.data;
//      //--BE sure we have at least 4 workflow selected
//      int count=0;
//      for (Workflows w:workflow_data) {
//          if (w.isSelected()) count++;
//      }
//      if (count<6) {
//          MessageError("Warning, you must select at least 6 workflows.", "");
//          return;
//      }
//      Message("Clustering "+count+" workflows.","");
//      
//      
//      SwingWorker<Integer, ArrayList<Workflows>> clusterWorker=new SwingWorker<Integer, ArrayList<Workflows>>()  {
//
//         
//       @Override
//        protected Integer doInBackground() throws Exception {
//            try {
//                setProgress(2);
//                Config.library_mode=true;
//                 ////////////////////////////////////////////////////////////////////////
//                /// VARIABLES
//                 Text text=null;   //--Output file
//                 Text time_matrix=null;   //--Output file for weight matrix
//                 String note="";  //--Output text note
//                 String data="";   //--Output data
//                 String data2="";  //--Output data for weight matrix 
//                 int total_workflow=0; //--Total selected workflows
//                 HashMap<String,Integer>propertiesClassName_Number=new HashMap<String,Integer>(); //--Program and count       
//                 ArrayList<String>Ordering=new ArrayList<String>(); //--Ordering of the program
//                 ArrayList<Workflows> workflow_names=new ArrayList<Workflows>();
//                 Integer[][] matrix=null;  //--Output matrix
//                 Integer[][] before_matrix=null;  //--Output matrix
//
//                ////////////////////////////////////////////////////////////////////////
//                /// MAIN FUNCTION
//
//                note+="**************************************************************************************************************\n";
//                switch(mode) {
//                    case 0:  
//                            note+="Clustering workflow binary matrix";
//                            text=new Text("binary.txt");
//                            text.setName("Binary clustering - "+Util.returnCurrentDateAndTime());
//                            break;
//                    case 1:  note+="Clustering workflow numerical matrix";
//                            text=new Text("numerical.txt");
//                            text.setName("Numerical clustering - "+Util.returnCurrentDateAndTime());            
//                            break;
//                    case 2:  note+="Clustering workflow time matrix";
//                            text=new Text("time.txt");
//                            text.setName("Time matrix clustering - "+Util.returnCurrentDateAndTime());            
//                            break;
//                     case 3:  note+="Clustering pair list";
//                            text=new Text("pair.txt");
//                            text.setName("Pair list clustering - "+Util.returnCurrentDateAndTime());            
//                            break;    
//                }
//                note+=" - "+Util.returnCurrentDateAndTime()+"\n";
//                note+="**************************************************************************************************************\n";
//                // 1. get the model (the workflow)
//                System.out.println(text.getName());
//
//                //
//               // MODE 0 
//               // 
//               if (mode==0) {
//                      //First iteration, create the name map
//                      int cou=workflow_data.size();
//                      for (Workflows w:workflow_data) {
//                            if (w.isSelected()) {
//                                workflow_names.add(w);
//                                HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
//                                 total_workflow++;
//                                Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
//                                //--Load a copy of the workflow 
//                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());
//                                //--Get this workflow program 
//                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
//                                    //--We have a program
//                                    if (o.getProperties().isProgram()) {
//                                        //String name=o.getProperties().get("ClassName");
//                                         String name=o.getProperties().getName();
//                                        //if (name.indexOf("programs")>0) name=name.substring(9);
//
//                                        //if (name.isEmpty()) name="*"+o.getProperties().getName();
//                                        // Find unique name
//                                         if (unique_name.containsKey(name)) {
//                                            int i=1;
//                                            while(unique_name.containsKey(name+i)) i++;
//                                            name=name+i;
//                                         } 
//                                         unique_name.put(name, 1);
//                                         propertiesClassName_Number.put(name, 1);
//                                         //Ordering.add(name);
//                                    }
//                                 }        
//                                  setProgress(total_workflow*100/cou);
//                            }
//                           
//                       }
//
//                      for (String name:propertiesClassName_Number.keySet()) {
//                          Ordering.add(name);                
//                      }
//                      // Second iteration, create the matrix since we don't save the state
//
//                        matrix=new Integer[Ordering.size()+1][total_workflow+1];
//                        for (int i=0;i<total_workflow;i++) { 
//                            for (int j=0; j<Ordering.size();j++) {
//                                matrix[j][i]=new Integer(0);
//                            }
//                        }
//
//                      int index=0;
//                      for (Workflows w:workflow_data) {
//                            if (w.isSelected()) {                        
//                                 HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
//                                  Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
//                                //--Load a copy of the workflow 
//                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());
//                                //--Get this workflow program 
//                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
//                                    //--We have a program
//                                    if (o.getProperties().isProgram()) {
//                                        //String name=o.getProperties().get("ClassName");
//                                         String name=o.getProperties().getName();
//                                        //if (name.indexOf("programs")>0) name=name.substring(9);
//                                         //if (name.isEmpty()) name="*"+o.getProperties().getName();
//                                        // Find unique name
//                                         if (unique_name.containsKey(name)) {
//                                            int i=1;
//                                            while(unique_name.containsKey(name+i)) i++;  
//                                            name=name+i;
//                                         } 
//                                         // We have the name, put the data in the matrix
//                                         unique_name.put(name, 1);
//                                          Integer j=Ordering.indexOf(name);                                
//                                          matrix[j][index]++;  
//                                    }
//                                 }
//                                 index++;
//                                  setProgress((index*20)/total_workflow);
//                            }   
//                       }
//                        note+="Total workflows: "+total_workflow+"\n";                
//                        note+="Distinct program: "+propertiesClassName_Number.size()+"\n";                
//               }
//
//
//                // MODE 1
//                if (mode==1) {
//                        for (Workflows w:workflow_data) {
//                            if (w.isSelected()) {
//                                 workflow_names.add(w);
//                               total_workflow++;                     
//                                //--Get information
//                                Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
//                                //--Load a copy of the workflow 
//                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());
//                                //--Get this workflow program 
//                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
//                                    //--We have a program
//                                    if (o.getProperties().isProgram()) {
//                                        String name=o.getProperties().getName();
//                                        Integer count=propertiesClassName_Number.get(name);
//                                        if (count==null) { count=1; } else { count++; }
//                                        propertiesClassName_Number.put(name, count);
//                                    }
//                                 }    
//                //                System.out.println(tmp_workflow.getStatistics());
//                //                System.out.println(tmp_workflow.number_of_object);
//                            }            
//                        }             
//
//
//                        note+="Total workflows: "+total_workflow+"\n";                
//                        note+="Distinct program: "+propertiesClassName_Number.size()+"\n";
//                        //note+="Summary:\n";
//
//                        for (String program_name:propertiesClassName_Number.keySet()) {
//                            //note+=program_name+"\t"+propertiesClassName_Number.get(program_name);
//                            Ordering.add(program_name);
//                        }
//                        //note+="Ordering:\n";
//                        //for (int i=0; i<Ordering.size();i++) note+=(i+"\t"+Ordering.get(i));
//
//                        matrix=new Integer[Ordering.size()+1][total_workflow+1];
//                        for (int i=0;i<total_workflow;i++) { 
//                            for (int j=0; j<Ordering.size();j++) {
//                                matrix[j][i]=new Integer(0);
//                            }
//                        }
//
//                        //--Create binary matrixwith count       
//                        int index=0;
//                       for (Workflows w:workflow_data) {
//                            if (w.isSelected()) {               
//                                //--Get information
//                                Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
//                                //--Load a copy of the workflow 
//                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());
//
//                                //--Get this workflow program 
//                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
//                                    //--We have a program
//                                    if (o.getProperties().isProgram()) {
//                                        String name=o.getProperties().getName();
//                                        int j=Ordering.indexOf(name);
//                                        matrix[j][index]++;
//                                    }
//                                 }                 
//                                 index++;
//                                 setProgress((index*20)/total_workflow);
//                            }            
//                        }    
//                }
//
//                //
//                // MODE 2
//                //
//                if (mode==2) {
//                      //First iteration, create the name map
//
//                      for (Workflows w:workflow_data) {
//                            if (w.isSelected()) {
//                                 workflow_names.add(w);
//                                HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
//                                 total_workflow++;
//                                     Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
//                                //--Load a copy of the workflow 
//                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());
//                                //--Get this workflow program 
//                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
//                                    //--We have a program
//                                    if (o.getProperties().isProgram()) {
//                                        //String name=o.getProperties().get("ClassName");
//                                         String name=o.getProperties().getName();
//                                        //if (name.indexOf("programs")>0) name=name.substring(9);
//
//                                        //if (name.isEmpty()) name="*"+o.getProperties().getName();
//                                        // Find unique name
//                                         if (unique_name.containsKey(name)) {
//                                            int i=1;
//                                            while(unique_name.containsKey(name+i)) i++;
//                                            name=name+i;
//                                         } 
//                                         unique_name.put(name, 1);
//                                         propertiesClassName_Number.put(name, 1);
//                                         //Ordering.add(name);
//                                    }
//                                 }        
//                            }   
//                       }
//                      for (String name:propertiesClassName_Number.keySet()) {
//                          Ordering.add(name);
//                          //System.out.println(name);
//                      }
//                      // Second iteration, create the matrix since we don't save the state
//
//                        matrix=new Integer[Ordering.size()+1][total_workflow+1];
//                        before_matrix=new Integer[Ordering.size()+1][total_workflow+1];
//                        for (int i=0;i<total_workflow;i++) { 
//                            for (int j=0; j<Ordering.size();j++) {
//                                matrix[j][i]=new Integer(0);
//                                  before_matrix[j][i]=new Integer(0);
//                            }
//                        }
//
//                      int index=0;
//                      for (Workflows w:workflow_data) {
//                            if (w.isSelected()) {
//                                //System.out.println(w.getName());
//                                 HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
//                                  Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
//                                //--Load a copy of the workflow 
//                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());
//                                //--Get this workflow program 
//                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
//                                    //--We have a program
//                                    if (o.getProperties().isProgram()) {
//                                        //String name=o.getProperties().get("ClassName");
//                                         String name=o.getProperties().getName();
//                                        //if (name.indexOf("programs")>0) name=name.substring(9);
//                                         //if (name.isEmpty()) name="*"+o.getProperties().getName();
//                                        // Find unique name
//                                         if (unique_name.containsKey(name)) {
//                                            int i=1;
//                                            while(unique_name.containsKey(name+i)) i++;  
//                                            name=name+i;
//                                         } 
//                                         // We have the name, put the data in the matrix
//                                           unique_name.put(name, 1);
//                                           Integer j=Ordering.indexOf(name);
//                                              int time=o.getProperties().getInt("TimeRunning")+1;
//                                            if (time<0) time=1;
//
//                                           matrix[j][index]=time;
//                                           before_matrix[j][index]=findTimeBefore(tmp_workflow, o);                                   
//                                    }
//                                 }
//                                 index++;
//                                   setProgress((index*20)/total_workflow);
//                            }   
//                       }
//                        note+="Total workflows: "+total_workflow+"\n";                
//                        note+="Distinct program: "+propertiesClassName_Number.size()+"\n";
//                        note+="Summary:\n"; 
//               } //--End 2
//                   if (mode==3) {
//                      //First iteration, create the name map
//                       HashMap<String,Integer>unique_name=new HashMap<String,Integer>(); 
//                       for (Workflows w:workflow_data) {
//                            if (w.isSelected()) {
//                                workflow_names.add(w);                                                               
//                                total_workflow++;
//                                Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
//                                //--Load a copy of the workflow 
//                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());     
//                                //--Get a list of input/output
//                                ArrayList<workflow_object> outputs=findWorkflowOutput(tmp_workflow);
//                                ArrayList<workflow_object> inputs=findWorkflowInput(tmp_workflow);
//                                //--Add to list the input
//                                for (workflow_object o:inputs) unique_name.put("INPUT_"+o.properties.getName(),-1);
//                                //--Add to list the output using the predefined name matrix
//                                for (workflow_object o:outputs) { 
//                                    String name=o.properties.getName();                                    
//                                    unique_name.put("OUTPUT_"+OutputObjectType.get(name),-1);
//                                }
//                                //--Get this workflow program 
//                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
//                                    //--We have a program
//                                    if (o.getProperties().isProgram()) {
//                                        //String name=o.getProperties().get("ClassName");
//                                        
//                                        //-- Create class parent, enfant if not exist
//                                        //--Class is concatenation of name
//                                        String name=o.getProperties().getName();
//                                        
//                                        //--get previous and following parent-child
//                                        ArrayList<workflow_object>parent=findImmediateParentDatabase(tmp_workflow,o);
//                                        ArrayList<workflow_object>child=findImmediateChildObject(tmp_workflow,o);
//                                        //--Construct
//                                        String new_name="";
//                                        for (workflow_object tm:parent) {
//                                            new_name=tm.properties.getName()+"_"+name;
//                                            if (unique_name.containsKey(new_name)) {
//                                                unique_name.put(new_name, unique_name.get(new_name)+1); 
//                                            } else {
//                                                unique_name.put(new_name, 1); 
//                                            }
//                                        }
//                                        for (workflow_object tm:child) {
//                                            new_name=name+"_"+tm.properties.getName();
//                                            if (unique_name.containsKey(new_name)) {
//                                                unique_name.put(new_name, unique_name.get(new_name)+1); 
//                                            } else {
//                                                unique_name.put(new_name, 1); 
//                                            }
//                                        }                                    
//                                    }
//                                 }        
//                            }   
//                       }
//
//                      for (String name:unique_name.keySet()) {
//                          Ordering.add(name);                
//                      }
//                      // Second iteration, create the matrix since we don't save the state
//
//                        matrix=new Integer[Ordering.size()+1][total_workflow+1];
//                        for (int i=0;i<total_workflow;i++) { 
//                            for (int j=0; j<Ordering.size();j++) {
//                                matrix[j][i]=new Integer(0);
//                            }
//                        }
//
//                      int index=0;
//                      for (Workflows w:workflow_data) {
//                            if (w.isSelected()) {                                                       
//                                 Workflows tmp_workflow=new Workflows(new armadillo_workflow());                
//                                //--Load a copy of the workflow 
//                                tmp_workflow.StringToWorkflow(w.getWorkflow_in_txt());                              
//                                //--Get a list of input/output
//                                ArrayList<workflow_object> outputs=findWorkflowOutput(tmp_workflow);
//                                ArrayList<workflow_object> inputs=findWorkflowInput(tmp_workflow);
//                                //--Add to list the input
//                                for (workflow_object o:inputs) {
//                                    String name=o.properties.getName();
//                                    Integer j=Ordering.indexOf("INPUT_"+name);                                
//                                    matrix[j][index]++;  
//                                }
//                                //--Add to list the output using the predefined name matrix
//                                for (workflow_object o:outputs) {                                    
//                                    String name=o.properties.getName();                                    
//                                    if (OutputObjectType.get(name)==null) System.out.println(name);
//                                    Integer j=Ordering.indexOf("OUTPUT_"+OutputObjectType.get(name));                                
//                                    matrix[j][index]++;  
//                                }
//                                 for (workflow_object o:tmp_workflow.workflow.workflow.work) {            
//                                    //--We have a program
//                                    if (o.getProperties().isProgram()) {
//                                        //String name=o.getProperties().get("ClassName");
//                                         String name=o.getProperties().getName();
//                                            //--get previous and following parent-child
//                                        ArrayList<workflow_object>parent=findImmediateParentDatabase(tmp_workflow,o);
//                                        ArrayList<workflow_object>child=findImmediateChildObject(tmp_workflow,o);
//                                        //--Construct
//                                        String new_name="";
//                                            for (workflow_object tm:parent) {
//                                                new_name=tm.properties.getName()+"_"+name;
//                                                Integer j=Ordering.indexOf(new_name);                                
//                                                matrix[j][index]++;  
//                                            }
//                                            for (workflow_object tm:child) {
//                                                new_name=name+"_"+tm.properties.getName();
//                                                Integer j=Ordering.indexOf(new_name);                                
//                                                matrix[j][index]++;  
//                                            }                                                    
//                                    }
//                                 }
//                                 index++;
//                                 setProgress((index*20)/total_workflow);
//                            }   
//                       }
//                        note+="Total workflows: "+total_workflow+"\n";                
//                        note+="Distinct program: "+propertiesClassName_Number.size()+"\n";                
//               }
//
//                //note+=total_workflow+"\t"+Ordering.size()+"\n";
//                //Note: 
//                data+=total_workflow+"\t"+Ordering.size()+"\n";
//                data2+=total_workflow+"\t"+Ordering.size()+"\n";
//
//                     for (int j=0; j<Ordering.size();j++) {
//                        String buffer="";   
//                        String buffer2=""; 
//                         for (int i=0;i<total_workflow;i++) {        
//                             buffer+=new Float(matrix[j][i]).toString()+"\t";                     
//                             if (mode==2) buffer2+=new Float(before_matrix[j][i]).toString()+"\t";      
//                         }
//                        data+=buffer+"\n";
//                        //msg(buffer);
//                         if (mode==2) {
//                             data2+=buffer2+"\n";
//                         }
//                      }
//                     note+="Workflows:\n";
//                  for (Workflows w:workflow_names) note+="ID"+w.getId()+" :\t"+w.getName()+"\n";
//                     note+="Programs:\n";
//                  for (int j=0; j<Ordering.size();j++) {
//                         //System.out.println(j+"\t"+Ordering.get(j)+"\t");
//                         note+=j+"\t"+Ordering.get(j)+"\t\n";
//                  }
//                 ///////////////////////////////////////////////////////////////////////
//                 /// SAVE DATA
//                 text.setNote(note);
//                 text.setText(data);
//                 text.saveToDatabase();             
//                 note+="**************************************************************************************************************\n";
//                 msg(note);
//                 if (mode==2) {
//                     time_matrix=new Text("weight.txt");
//                     time_matrix.setName("Time weight matrix - "+Util.returnCurrentDateAndTime());
//                     time_matrix.setText(data2);
//                     time_matrix.saveToDatabase();
//                 }                          
//                
//                 
//                 ///////////////////////////////////////////////////////////////////////
//                 /// Run k-means
//                 workflow_properties properties=new workflow_properties(config.propertiesPath()+File.separator+"kmeans.properties");                 
//                 properties.put("replicate", replicate);
//                 properties.setInput(text,RunProgram.PortInputDOWN);
//                 if (mode==2) {
//                     properties.setInput(time_matrix,RunProgram.PortInputUP);
//                 }
//                                  
//                 //--Execute external kmeans
//                km=new kmeans(properties);                
//                 Pattern p=Pattern.compile("Currently.working.on.random.start.no..([0-9]*)");  
//                 //--Test progress
//                 while(!km.isDone()) {                   
//                     String output = km.getOutputText();
//                     Matcher m=p.matcher(output);
//                     if (m.find()) {                         
//                         int y=Integer.valueOf(m.group(1));
//                         setProgress(y*100/replicate);
//                     }
//                 }
//                 //--Kmeans done
//                 if (km.isDone()) {                                                       
//                     setProgress(100);
//                     Results stats=new Results(Integer.valueOf(properties.getOutputID("Results")));
//                     Text groups=new Text(Integer.valueOf(properties.getOutputID("Text")));
//                     resultsTextArea.setText(stats.getText());
//                     //--Parse groups and update 
//                     Pattern best=Pattern.compile("Best.is.for.([0-9]*)",Pattern.CASE_INSENSITIVE);
//                     Matcher mbest=best.matcher(stats.getText());
//                     if (mbest.find()) {
//                         String results=mbest.group(1);
//                         //System.out.println("Best: "+results);
//                         int no_group=Integer.valueOf(results);
//                         Message("Best cluster: "+no_group+" groups","");                         
//                         //--Parse results
//                         int no_group_tabs_index=no_group-2;
//                         int index=0;
//                         HashMap<Integer,Integer>workflow_group=new HashMap<Integer,Integer>();
//                         
//                         for (String stri:groups.getText().split("\n")) {
//                             String[] tabs=stri.split("\t");                                                          
//                             if (index>0) {
//                                try {
//                                 Integer clustering_group=Integer.valueOf(tabs[no_group_tabs_index].trim());
//                                    workflow_group.put(index-1,clustering_group);
//                                } catch(Exception e) {}                                
//                             }
//                             index++;
//                         }
//                         //--Update table 
//                         ChooseWorkflowTableModel qt = (ChooseWorkflowTableModel)jTable1.getModel();
//                         index=0; 
//                         for (Workflows w:qt.data) {
//                              if (w.isSelected()) {
//                                  w.clustering_group=workflow_group.get(index);
//                                  index++;
//                              } else {
//                                   w.clustering_group=0;
//                              }                             
//                          }    
//                         qt.fireTableDataChanged();
//                         qt.fireTableStructureChanged();
//                          jTable1.setModel(qt);
//                     } else {
//                         System.out.println("not found");
//                         Message("No cluster found","");
//                     }                     
//                 } //--End update kmeans
//                 ///////////////////////////////////////////////////////////////
//                 /// PUT MATRIX IN THE TABLE
//                 ChooseWorkflowMatrixTableModel qt2=( ChooseWorkflowMatrixTableModel)MatrixjTable.getModel();
//                 qt2.setData(matrix, Ordering.size(), workflow_names, Ordering);
//                 //--Set column width                
//                 MatrixjTable.setAutoResizeMode(MatrixjTable.AUTO_RESIZE_OFF);
//                 for (int i=0; i<MatrixjTable.getColumnCount();i++) {
//                     MatrixjTable.getColumnModel().getColumn(i).setMinWidth(130);
//                     MatrixjTable.getColumnModel().getColumn(i).setPreferredWidth(130);
//                 }
//                 
//            } catch(Exception e) {e.printStackTrace();}
//                    return 0;
//                }
//
////           @Override
////            protected void process(List<Workflows> chunks) {
////                
////            }
//
//
//           @Override
//           protected void done(){
//                Config.library_mode=false;
//                 toolbox.reloadDatabaseTree();
//           }
//
//        }; //End SwingWorker declaration
//
//        clusterWorker.addPropertyChangeListener(
//                 new PropertyChangeListener() {
//                    public  void propertyChange(PropertyChangeEvent evt) {
//                     if ("progress".equals(evt.getPropertyName())) {
//                            SwingWorker o = (SwingWorker)evt.getSource();
//                            int progress=(Integer)evt.getNewValue();
//                            progressBar.setValue(progress);
//                            progressLabel.setText(progress+"%");
//                            //System.out.println(progress);
//                            if (!o.isDone()) {
//                             //
//                            }
//                            else if (o.isDone()&&!o.isCancelled()) {
//                               //Handled in done() fucntion in SwingWorker
//                            }
//                        }//End progress update
//                 } //End populateNetworkPropertyChange
//                 }); 
//         progressBar.setValue(1);
//          progressLabel.setText("1%");
//        clusterWorker.execute();
//    }   

   
    
    public static void main(String args[]) {
        //myExperiment_clustering my=new myExperiment_clustering();
        
        //my.Run("C:\\Documents and Settings\\lore26107809\\Mes documents\\Dropbox\\These\\Présentation SFC- IFCS - July 2013\\zhao_2012\\protein_tag", "");
        //my.cluster(config.currentPath()+File.separator+"myExperiment.db");
    }
    
}
