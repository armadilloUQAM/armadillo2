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
package demo;

import Class.Classdata;
import biologic.*;
import biologic.seqclasses.GeneticCode;
import biologic.seqclasses.Translator;
import biologic.seqclasses.blastParser;
import biologic.seqclasses.ebiblast;
import biologic.seqclasses.eutil;
import biologic.seqclasses.parserNewick.newick_tree;
import biologic.seqclasses.parserNewick.node;
import biologic.seqclasses.qblast;
import biologic.seqclasses.usewsdl;
import com.google.gson.Gson;
import com.ibm.wsdl.PortTypeImpl;
import configuration.Browser;
import configuration.Config;
import configuration.MemoryTest;
import configuration.Timer;
import configuration.Util;
import database.Project;
import database.database;
import database.databaseFunction;
import database.database_mysql;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import programs.SOAPWeb;
//--BioJava 3 test
//import org.biojava3.core.sequence.ChromosomeSequence;
//import org.biojava3.core.sequence.ProteinSequence;
//import org.biojava3.core.sequence.io.FastaWriterHelper;
//import org.biojava3.genome.GeneFeatureHelper;
//import org.biojava3.genome.parsers.gff.Feature;
//import org.biojava3.genome.parsers.gff.FeatureI;
//import org.biojava3.genome.parsers.gff.FeatureList;
//import org.biojava3.genome.parsers.gff.GFF3Reader;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.apache.axis.client.Call;
import program.RunProgram;
import program.programs;
import programs.JavaProgramClass;
import programs.SOAPWeb;
import uk.ac.ebi.webservices.WSEmbossClient;
import uk.ac.ebi.webservices.WSWUBlastClient;
import uk.ac.ebi.webservices.wswublast.*;
//import uk.org.taverna.scufl2.api.container.WorkflowBundle;
//import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
//import uk.org.taverna.scufl2.api.io.WorkflowBundleWriter;
//import uk.org.taverna.scufl2.translator.t2flow.T2FlowReader;
//import uk.org.taverna.scufl2.ucfpackage.UCFPackage;
import workflows.armadillo_workflow;
import workflows.armadillo_workflow.workflow_connector;
import workflows.armadillo_workflow.workflow_object;
import workflows.workflow_properties;
import workflows.workflow_properties_dictionnary;

/**
 * Text and other fucntion to test the program and some functions indevelopment
 * Note: this is really just a test bed function for debugging...
 * This is a sandbox... in text mode...
 * @author Etienne Lord
 * @since June 2009-2010
 */
public class Demo {

    public static databaseFunction df = new databaseFunction();
    public static Config config = new Config();

    public static void main(String[] args) {
        Demo.RunDemo();
    }

    public static void testDatabase_REGEX_SQLquery() {
        System.out.println("Testing database with PL/SQL regular expression (REGEX) functions");
        database db = df.getDatabase();
        System.out.println("Database " + db.dbFileName);
        System.out.println("Creating pseudo-random 10000 nucleic acid sequences of 50 bp ...");
        Alignment alignment_1 = new Alignment(50, 10000);
        System.out.println("Saving...");
        alignment_1.saveToDatabase();
        System.out.println("Looking up... for sequence : ATG[AG]AT");
        try {
            ResultSet rs = db.executeQuery("SELECT Sequence_ID,Name,Sequence,Sequence_len FROM Sequence WHERE REGEXP('ATG[AG]AT',Sequence);");
            while (rs.next()) {
                //--Note: sequence data not shown.
                System.out.println(rs.getInt(1) + ":" + rs.getString(2) + ":" + " " + rs.getInt(4) + " bp ");
            }
            rs = db.executeQuery("SELECT COUNT(*), MAX(Sequence_len) FROM Sequence WHERE REGEXP('ATG[AG]AT',Sequence);");
            while (rs.next()) {
                System.out.println("Total :" + rs.getInt(1) + " sequence(s) of max_length(" + rs.getInt(2) + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Timer.reset();
        System.out.println("Searching using built-in function alignment.search (Java)");
        Timer.reset();
        Vector<Integer> ids = alignment_1.search("ATG", Alignment.MODE_SEQUENCE);
        System.out.println("Search in :" + Util.msToString(Timer.timeMS()));
        System.out.println("Done. Found " + ids.size() + " sequence(s) matching.");
        Timer.reset();
        System.out.println("Search alignment using SQLite function");
        try {
            ResultSet rs = df.getDatabase().executeQuery("SELECT Sequence_id FROM Sequence WHERE "
                    + "REGEXP('ATG',Sequence) AND Sequence_id IN "
                    + "(SELECT Sequence_id FROM Alignment WHERE Alignment_id = " + alignment_1.getId() + ");");
            ids.clear();
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
            System.out.println("Search in :" + Util.msToString(Timer.timeMS()));
            System.out.println("Done. Found " + ids.size() + " sequence(s) matching.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testReportGenerator() {
//         report result=new report();
//         Project project=new Project();
//         project.loadFromDatabase(1);
//         Workflows workflows=new Workflows(df.getNextWorkflowsID()-1);
        //final String reportFile=result.generateResults();
    }

    
    public static void testMega() {
       String directory=Config.currentPath;
        for (String filename:Util.recursive_list_dir(directory)) {
            if (filename.toLowerCase().endsWith("fasta")||filename.toLowerCase().endsWith("fas")) {
                MultipleSequences multi=new MultipleSequences(filename);
                //--Filter out HXB2...
                for (int i=multi.getSequences().size()-1;i>-1;i--) {
                    Sequence s=multi.getSequences().get(i);
                    if (s.getName().contains("HXB2")) multi.getSequences().remove(i);
                }
                multi.outputMeg(filename+".meg");
            }
            
        }
//        MultipleSequences multi=new MultipleSequences("1005C1NA.fas");
//        Alignment multi=new Alignment(250,10);
//        System.out.println(multi.outputMeg());
//        System.out.println(multi.outputFasta());
//        multi.outputMeg("test.meg");
        System.exit(0);
    }
    
     
    public static void testRename() {
      String directory=Config.currentPath;
        for (String filename:Util.recursive_list_dir(directory)) {
            if (filename.toLowerCase().endsWith("fasta")||filename.toLowerCase().endsWith("fas")) {
                MultipleSequences multi=new MultipleSequences(filename);
                //--Filter out HXB2...
                for (int i=multi.getSequences().size()-1;i>-1;i--) {
                    Sequence s=multi.getSequences().get(i);
                    //--Remove HXB2
                    if (s.getName().contains("HXB2")) multi.getSequences().remove(i);
                }
                //--Rename Here
                File f=new File(filename);
                String name=f.getName().substring(0,7); 
                Util u=new Util(filename+"renamed.txt");
                for (int i=0; i<multi.getSequences().size();i++) {
                    Sequence s=multi.getSequences().get(i);
                    u.println(s.getName()+"\t"+name+"-"+i);
                    s.setName(name+"-"+i);                    
                }                
                u.close();
                //--Output as Fasta
                multi.outputFasta(filename+".renamed.fasta");
            }
            
        }
//        MultipleSequences multi=new MultipleSequences("1005C1NA.fas");
//        Alignment multi=new Alignment(250,10);
//        System.out.println(multi.outputMeg());
//        System.out.println(multi.outputFasta());
//        multi.outputMeg("test.meg");
        System.exit(0);
    }
    
    
    public static void testMySQLConnect() {
       database_mysql dbm=new database_mysql();
       dbm.setDebug(true);
       dbm.setServer("localhost");
       dbm.setUsername("Mirna"); 
       dbm.setPassword("Mirna123");
       //dbm.setDatabase("mirnas");
       dbm.setDatabase("Mirna");//--Note: databse name is not the same!!!

        System.out.println(dbm.connect());
        System.out.println(dbm.isConnected());
        //if (dbm.isConnected()) {

        
        //--Create Table 
        dbm.execute("DROP TABLE Article;");
        String table="CREATE TABLE Article (mirna varchar(31), identification TEXT);";
	
        dbm.execute(table);
       
        dbm.setAutoCommit(false);
        
        //--Load the id 
       try {
           dbm.execute("BEGIN;");
           BufferedReader br=new BufferedReader(new FileReader(new File("idMirna.txt")));
           while(br.ready()) {
           String st=br.readLine();
           if (!st.isEmpty()) {
               String[] a=st.split("\t");
               String sql=String.format("INSERT INTO Article(mirna, identification) VALUES ('%s', '%s');", a[1], a[0]);
               dbm.execute(sql);
           }
           }
           dbm.execute("COMMIT;");
           br.close();
       } catch(Exception e) {}
        dbm.setAutoCommit(true);
        //--Test >Query               
        try {
            
            ResultSet rs = dbm.executeQuery("SELECT count(precursor_seq) FROM mirnas_details;");
            if (rs!=null&&rs.next()) {
                  System.out.println(rs.getInt(1));
                  rs.close();
                  
            }
         } catch(Exception e) {System.out.println("Error DB : "+e.getMessage());}
        //}
        System.exit(0);
    }

     public static void createClusterTableDatabase() {
       database_mysql dbm=new database_mysql();
       dbm.setDebug(true);
       dbm.setServer("localhost");
       dbm.setUsername("Mirna"); //--Note: databse name is not the same!!!
       dbm.setPassword("Mirna123");
       dbm.setDatabase("Mirna");
        System.out.println("Connecting to databse "+dbm.getDbFileName()+": "+dbm.connect());
        System.out.println("We are connected: "+dbm.isConnected());
        //if (dbm.isConnected()) {



        //  dbm.execute("ALTER TABLE mirnas_details ADD Cluster INT NOT NULL ;");
        try {
            HashMap<Integer, Integer>m=new HashMap<Integer,Integer>();
            BufferedReader br=new BufferedReader(new FileReader(new File("results.uc")));
            while(br.ready()) {
             String str=br.readLine();
             //--SeqID, cluster
          
             if (!str.startsWith("#")) {
                String[] uc=str.split("\t");
                if (uc.length>3) {
                    
                     int i=Integer.valueOf(uc[1]);
                     int j=Integer.valueOf(uc[8]);
                        //System.out.println(i+ " "+j);
                        m.put(j, i);
                  
                }                
             } //--End startsWith *
            }
            br.close();
            dbm.setAutoCommit(false);
            dbm.execute("BEGIN;");
            for (int id:m.keySet()) {
                String str=String.format("UPDATE mirnas_details SET Cluster='%d' WHERE id='%d';",m.get(id),id);
                dbm.execute(str);
            }
            dbm.execute("COMMIT;");
            
            
         } catch(Exception e) {System.out.println("Error DB : "+e.getMessage());}
        //}
        System.exit(0);
    }

    public static void getMirnaPrecursorSeq() {
       database_mysql dbm=new database_mysql();
       dbm.setDebug(true);
       dbm.setServer("localhost");
       dbm.setUsername("Mirna"); //--Note: databse name is not the same!!!
       dbm.setPassword("Mirna123");
       dbm.setDatabase("Mirna");
        System.out.println("Connecting to databse "+dbm.getDbFileName()+": "+dbm.connect());
        System.out.println("We are connected: "+dbm.isConnected());
        //if (dbm.isConnected()) {
        try {

            ResultSet rs = dbm.executeQuery("SELECT id, mirna, identification, precursor_seq, precursor_struct FROM mirnas_details JOIN article using(mirna);");
            if (rs!=null) {
                MultipleSequences multi=new MultipleSequences();
                MultipleSequences multi_mirna=new MultipleSequences();
                MultipleSequences multi_precursor=new MultipleSequences();
                while (rs.next()) {
                    
                    Sequence s=new Sequence();  //--Sequence precursor
                    Sequence mi=new Sequence(); //--Sequence mirna
                    Sequence pre=new Sequence();//--Sequence precursor_struct

                    int id=rs.getInt(1);
                    String mirna=rs.getString(2);
                    String identification=rs.getString(3);
                    String precursor_seq=rs.getString(4);
                    String precursor_struct=rs.getString(5);
                    String ids=id+"|"+mirna+"|"+identification;
                    //--Comment below to have the true id...
                    ids=""+id;
                    s.setName(ids);mi.setName(ids);pre.setName(ids);
                    s.setSequence(precursor_seq);mi.setSequence(mirna);
                    pre.setSequence(precursor_struct);
                    //System.out.println(rs.getString(1)+" "+rs.getString(2));
                    multi.add(s);
                    multi_mirna.add(mi);
                    multi_precursor.add(pre);
                }
                multi.outputFasta("mirna_precursor.fasta");
                multi_mirna.outputFasta("mirna.fasta");
                multi_precursor.outputFasta("precursor_struct.fasta");
            }

            rs.close();
         } catch(Exception e) {System.out.println("Error DB : "+e.getMessage());}
        //}
        System.exit(0);
    }

     public static void extractSequencePrecursorSeq() {
       database_mysql dbm=new database_mysql();
       dbm.setDebug(true);
       dbm.setServer("localhost");
       dbm.setUsername("Mirna");
       dbm.setPassword("Mirna123");
       dbm.setDatabase("Mirnas");  //--Note: databse name is not the same!!!

    
        System.out.println("Connecting to databse "+dbm.getDbFileName()+": "+dbm.connect());
        System.out.println("We are connected: "+dbm.isConnected());
        //if (dbm.isConnected()) {
           //int ClusterID=134;
        try {
            //--Get a listt of all cluster ID
            ArrayList<Integer>list=new ArrayList<Integer>();
            ResultSet rs1 = dbm.executeQuery("SELECT DISTINCT(Cluster) FROM mirnas_details;");
            while(rs1.next()) {
                list.add(rs1.getInt(1));
            }

            for (int ClusterID:list) {
            ResultSet rs = dbm.executeQuery("SELECT id, mirna, identification, precursor_seq, precursor_struct FROM mirnas_details JOIN article using(mirna) WHERE Cluster='"+ClusterID+"';");
            if (rs!=null) {
                MultipleSequences multi=new MultipleSequences();
                MultipleSequences multi_mirna=new MultipleSequences();
                MultipleSequences multi_precursor=new MultipleSequences();
                String mccons="";
                while (rs.next()) {

                    Sequence s=new Sequence();  //--Sequence precursor
                    Sequence mi=new Sequence(); //--Sequence mirna
                    Sequence pre=new Sequence();//--Sequence precursor_struct

                    int id=rs.getInt(1);
                    String mirna=rs.getString(2);
                    String identification=rs.getString(3);
                    String precursor_seq=rs.getString(4);
                    String precursor_struct=rs.getString(5);
                    String ids=id+"|"+mirna+"|"+identification;
                    //--Comment below to have the true id...
                    //ids=""+id;
                    mccons+=">"+ids+"\n"+precursor_seq+"\n"+precursor_struct+"\n";
                    s.setName(ids);mi.setName(ids);pre.setName(ids);
                    s.setSequence(precursor_seq);mi.setSequence(mirna);
                    pre.setSequence(precursor_struct);
                    //System.out.println(rs.getString(1)+" "+rs.getString(2));
                    multi.add(s);
                    multi_mirna.add(mi);
                    multi_precursor.add(pre);
                }
                multi.outputFasta("mirna_precursor"+ClusterID+".fasta");
                multi_mirna.outputFasta("mirna"+ClusterID+".fasta");
                multi_precursor.outputFasta("precursor_struct"+ClusterID+".fasta");
                //multi.PrintPhylip();
                Util u=new Util("mccons"+ClusterID+".fasta");
                u.println(mccons);
                u.close();
                
            }

            rs.close();
            } //--End cluster
         } catch(Exception e) {System.out.println("Error DB : "+e.getMessage());}
        //}
        System.exit(0);
    }

      public static void extractAllPrecursorStruct() {
       database_mysql dbm=new database_mysql();
       dbm.setDebug(true);
       dbm.setServer("localhost");
       dbm.setUsername("Mirna");
       dbm.setPassword("Mirna123");
       dbm.setDatabase("Mirnas");  //--Note: databse name is not the same!!!

    
        System.out.println("Connecting to databse "+dbm.getDbFileName()+": "+dbm.connect());
        System.out.println("We are connected: "+dbm.isConnected());
        //if (dbm.isConnected()) {
           //int ClusterID=134;
        try {
          int ids=0;
            ResultSet rs = dbm.executeQuery("SELECT distinct(precursor_struct), mirna  FROM mirnas_details JOIN article using(mirna);");
            if (rs!=null) {
               
                MultipleSequences multi_precursor=new MultipleSequences();
                String mccons="";
                while (rs.next()) {

                    Sequence s=new Sequence();  //--Sequence precursor
                    Sequence mi=new Sequence(); //--Sequence mirna
                    Sequence pre=new Sequence();//--Sequence precursor_struct

//                    int id=rs.getInt(2);
                    String mirna=rs.getString(2);
//                    String identification=rs.getString(3);                   
                    String precursor_struct=rs.getString(1);
                    //String ids=id+"|"+mirna+"|"+identification;
                    //--Comment below to have the true id...
                    
                    pre.setName(mirna);                    
                    pre.setSequence(precursor_struct);                    
                    multi_precursor.add(pre);
                }
                
                multi_precursor.outputFasta("precursor_struct.all.fasta");                                
            }
            rs.close();            
         } catch(Exception e) {System.out.println("Error DB : "+e.getMessage());}
        //}
        System.exit(0);
    }
     
    public static void testDatabaseMultipleSequences() {
        MultipleSequences multi = new MultipleSequences();
        if (!Util.FileExists(config.currentPath + "//examples//hiv.fasta")) {
            System.out.println("Error: " + config.currentPath + "//examples//hiv.fasta" + " don't exists.");
            return;
        }
        multi.readSequenceFromFasta(config.currentPath + "//examples//hiv.fasta");
        multi.PrintPhylip();
        for (Sequence s : multi.getSequences()) {
            //--Note: we dont de
            System.out.println(s.toString().substring(0, 50) + "...");
        }
        System.out.println("Saving to database");
        if (multi.saveToDatabase()) {
            System.out.println("Loading to database");
            multi.loadFromDatabase(multi.getId());
            System.out.println("Done loading");
        }
        System.out.println("Listing multipleSequences...");
        for (int id : df.getAllMultipleSequencesID()) {
            InfoMultipleSequences info = new InfoMultipleSequences(id);
            System.out.println(info);
        }
    }

//    public static void test_generatePAMLreport_Beta() {
//        Scanner sc = new Scanner(System.in);
//        System.out.print("Enter mlc (results) directory (ex. c:\\mlc) : ");
//        String dir=sc.nextLine();
//        System.out.print("Enter results files extension (ex. .mlc) : ");
//        String extension=sc.nextLine();
//        Vector<String>files=Config.listDir(dir);
//        Util u=new Util();
//        results r=new results();
//        u.open(dir+File.separator+"index.html");
//        u.println( r.getHead());
//        u.println("<div id=\"page\">\n"+
//                "\n"+
//                "	<div id=\"content\">\n"+
//                "		<div class=\"post\">\n"+
//                "			<h2 class=\"title\">PAML Results index</h2>\n"+
//                "			<div class=\"entry\">\n"+
//                "				\n");
//        for (String filename:files) {
//            if (filename.endsWith(extension)) {
//                LoadPAML p=new LoadPAML();
//                if (p.generateResultFor(dir+File.separator+filename, dir+File.separator+filename+".html")) {
//                    u.println("<a href=\""+filename+".html\">"+filename+"</a>  Original file <a href=\""+filename+"\">"+filename+"</a><br>");
//                } else {
//                    u.println("No results for "+filename+" <a href=\""+filename+"\">"+filename+"</a><br>");
//                }
//            }
//        }
//        u.println("</div></div></div>");
//        u.println( r.foot());
//        u.close();
//        System.exit(0);
//
//    }
    
        
    /**
     * Fonction use to calculate the conservation score in contrast to the first sequence...
     */
    public static void CalculateConservationScore() {
        Alignment align=new Alignment("alignements.na.all.wHXB2.Etienne.Lord.fasta");
        Translator trans=new Translator();
        GeneticCode code=trans.code.get(0);
        
        Util u=new Util("output.txt");
        u.println("Alignment: "+align.filename);
        String entete=String.format("Position\tA\tT\tG\tC\tGAP\tTotal ATGC");
        u.println(entete);
        Util seqname_all=new Util("seqname_all.txt"); 
        for (int i=1; i<align.getSequenceSize();i++) {
             seqname_all.println(align.getSequences().get(i).getName().toUpperCase());             
         }
        seqname_all.close();
        for (int i=1; i<align.getSequenceSize();i++) {
            //--:ATGC-
            Float dA=0.0f;
            Float dG=0.0f;
            Float dT=0.0f;
            Float dC=0.0f;
            Float dGAP=0.0f;
            Float dConserve=0.0f;
            
            MultipleSequences s=align.extractColumn(i-1, i);                        
            //s.PrintPhylip();
            //--Calculate statistic for each            
            char HXB2=s.getSequences().get(0).getSequence().toUpperCase().charAt(0);
            for (int j=1; j<s.getNbSequence();j++) {
                char ch=s.getSequences().get(j).getSequence().toUpperCase().charAt(0);
                //System.out.print(ch);
                switch(ch) {
                    case 'A': dA++; break;
                    case 'T': dT++; break;
                    case 'G': dG++; break;
                    case 'C': dC++; break;    
                    case '-': dGAP++; break;
                }
                if (ch==HXB2) dConserve++; 
            }
            //System.out.println("");
            float totalATGC=dA+dT+dG+dC;
            //float pourcentage_corige=dConserve/totalATGC;
            float pourcentage=dConserve/s.getNbSequence();
            String st=String.format("%d\t%f\t%f\t%f\t%f\t%f\t%f\t%f\t%f\t",i, dA, dT, dG, dC, dGAP, totalATGC,dConserve, pourcentage);
            u.println(st);
        }
        
        //--Translate the sequences...
        Alignment AA=new Alignment();
        for (int i=0; i<align.getNbSequence();i++) {
          Sequence s=align.getSequences().get(i);
          String aa=trans.translate_without_stop(s, code);
          Sequence as=new Sequence();
          as.setName(s.getName());
          as.setSequence(aa);
          AA.add(as);
          //System.out.println(aa);
          //if (s.getName().contains("HXB2")) align.getSequences().remove(i);
      }
        
        u.println("===================================");
        u.println("AA conservation (Position AA)");
        u.println("===================================");
         for (int i=1; i<AA.getSequenceSize();i++) {
            //--:ATGC-           
            Float dConserve=0.0f;
            
            MultipleSequences s=AA.extractColumn(i-1, i);                        
            //s.PrintPhylip();
            //--Calculate statistic for each            
            char HXB2=s.getSequences().get(0).getSequence().toUpperCase().charAt(0);
            for (int j=1; j<s.getNbSequence();j++) {
                char ch=s.getSequences().get(j).getSequence().toUpperCase().charAt(0);
                //System.out.print(ch);               
                if (ch==HXB2) dConserve++; 
            }
            //System.out.println("");            
            //float pourcentage_corige=dConserve/totalATGC;
            float pourcentage=dConserve/s.getNbSequence();
            String st=String.format("%d\t%f\t%f",i,dConserve, pourcentage);
            u.println(st);
        }
        u.println("===================================");
        u.println("Nb sequence IJK");
        u.println("===================================");
       
        
        //--Remove HXB2 if found
      
      for (int i=align.getNbSequence()-1; i>-1;i--) {
          Sequence s=align.getSequences().get(i);
          if (s.getName().contains("HXB2")) align.getSequences().remove(i);
      }
          
      //--Note: group 0: I, group 1: J, group 2: K
      Vector<Alignment> groups=Create_Group_Sequences(align);
        //--Create intergroups
      u.println("I: "+groups.get(0).getNbSequence());
      u.println("J: "+groups.get(1).getNbSequence());
      u.println("K: "+groups.get(2).getNbSequence());
        groups.get(0).PrintPhylip();
      
      u.close();
    }
    
   //--Calculate rate for each files...    
   
    
    
  //--Calculate the number of sequences in each group 
  //--Note: WE need the 22dec2011
public static void CalculateNumberOfSequence() {
    
    //--- Statistic class
    class re {
        String filename;
        int i;
        int j;
        int k;
        int total;
    }
    //--Results
    ArrayList<re> array=new ArrayList<re>();
    
    //--Load the files...
    String path="C:\\Documents and Settings\\lore26107809\\Mes documents\\Dropbox\\22dec2011";
    Vector<String>filenames=Config.listDirWithFullPath(path);
    Util seqname=new Util("seqname_file.txt");
    for (String filename:filenames) {
        re e=new re();
        e.filename=filename;                
        Alignment align=new Alignment(filename);
        //--Remove HXB2 -- first sequences of each...
        align.getSequences().remove(0);
        Vector<Alignment> groups=Create_Group_Sequences(align);
        e.i=groups.get(0).getNbSequence();
        e.j=groups.get(1).getNbSequence();
        e.k=groups.get(2).getNbSequence();
        e.total=align.getNbSequence();
        array.add(e);
        for (Sequence s:align.getSequences()) {
           seqname.println(s.getName().toUpperCase());
        }        
    }
    seqname.close();
    Util u=new Util("resultx.txt");
    u.print("\t");
    for (int i=0; i<array.size();i++) {        
        re e=array.get(i);
        System.out.println(e.filename);
        File f=new File(e.filename);                
        u.print(f.getName()+"\t");
    }
    u.println("");
    u.print("i\t");
    for (int i=0; i<array.size();i++) {        
        re e=array.get(i);                
        u.print(e.i+"\t");
    }
    u.println("");
    u.print("j\t");
    for (int i=0; i<array.size();i++) {        
        re e=array.get(i);             
        u.print(e.j+"\t");
    }
    u.println("");
    u.print("k\t");
    for (int i=0; i<array.size();i++) {        
        re e=array.get(i);            
        u.print(e.k+"\t");
    }
    u.println("");
    u.print("total\t");
    for (int i=0; i<array.size();i++) {        
        re e=array.get(i);        
        u.print(e.total+"\t");
    }
    u.println("");
    u.close();
}
     
    


 /**
  * This will split the alignment into 3 group according the the sequence name
  * @param align
  * @return
  */
public static Vector<Alignment> Create_Group_Sequences(Alignment align) {
    Alignment I=new Alignment();
    Alignment J=new Alignment();
    Alignment K=new Alignment();
    Alignment Attention=new Alignment();
    I.setName("I");
    J.setName("J");
    K.setName("K");
    Attention.setName("Attention");
    Vector<Alignment> tmp=new Vector<Alignment>();
    if (align.getNbSequence()!=0) {
        for (Sequence s:align.getSequences()) {
            int indexI=s.getName().toUpperCase().indexOf("I");
            int indexJ=s.getName().toUpperCase().indexOf("J");
            int indexK=s.getName().toUpperCase().indexOf("K");
            if (indexI==-1) indexI=s.getName().length();
            if (indexJ==-1) indexJ=s.getName().length();
            if (indexK==-1) indexK=s.getName().length();
            
            if (indexI>-1&&indexI<indexJ&&indexI<indexK) {
                I.add(s);
            } else if (indexJ>-1&&indexJ<indexI&&indexJ<indexK) {
                J.add(s);
            } else if (indexK>-1&&indexK<indexI&&indexK<indexJ)  {
                K.add(s);
            } else {
                Attention.add(s);
            }
        }
        tmp.add(I);
        tmp.add(J);
        tmp.add(K);
        if (Attention.getSequences().size()>0) tmp.add(Attention);
    }
    return tmp;
}

    
    
    public static void testDatabase_Trees() {
        Tree tree = new Tree();
        tree.setTree("((((((((((((((ATV11_35:0.017047,ATV11_45:0.015235)0.815000:0.003524,((((A1161_23:0.005396,A1161I_2:0.003619)0.738000:0.001502,ATV11_30:0.013357)0.131000:0.001708,(ATV11_18:0.003313,ATV11_10:0.021422)0.935000:0.007511)0.881000:0.004032,ATV11_11:0.011147)0.000000:0.000001)0.742000:0.001990,(A1161I_4:0.002151,ATV11_31:0.009044)0.731000:0.005509)0.863000:0.005216,(((ATV116_7:0.007537,(A1161I_3:0.013414,ATV11_49:0.013317)0.933000:0.009379)0.000000:0.000001,((ATV11_20:0.009033,A1161_25:0.014480)0.000000:0.000001,(((ATV11_38:0.000001,ATV11_62:0.015854)0.874000:0.003971,((ATV11_43:0.006140,ATV11_48:0.016459)0.868000:0.008071,ATV11_17:0.007835)0.841000:0.004149)0.860000:0.004090,(ATV11_61:0.030603,ATV11_46:0.027377)0.891000:0.007638)0.684000:0.005830)0.997000:0.016498)0.000000:0.001701,ATV11_60:0.020824)0.873000:0.003914)0.962000:0.009029,(A1161_26:0.021583,(((ATV11_64:0.027413,(((((ATV11_65:0.014124,(ATV11_63:0.029719,ATV11_33:0.011458)0.902000:0.010797)0.635000:0.009458,(ATV116_8:0.009550,ATV11_67:0.013236)0.904000:0.008043)0.839000:0.004504,(ATV11_16:0.013871,ATV11_14:0.002824)0.941000:0.008190)0.000000:0.000001,((ATV11_41:0.016458,ATV11_52:0.022184)0.000000:0.000001,ATV11_57:0.184977)0.722000:0.001423)0.988000:0.019312,ATV11_21:0.030847)0.727000:0.002016)0.975000:0.021184,((((((((ATV11_32:0.009212,ATV11_53:0.019944)0.802000:0.003828,A1161_24:0.009023)0.638000:0.001715,(ATV11_28:0.001847,ATV11_15:0.006155)0.989000:0.017865)0.860000:0.009295,(((ATV11_12:0.006038,A1161_44:0.006048)0.000000:0.000001,ATV11_58:0.022974)0.707000:0.003616,A1161I_6:0.002436)0.895000:0.009103)0.408000:0.007135,(ATV11_54:0.026478,ATV11_50:0.031455)0.762000:0.005423)0.979000:0.014694,ATV11_56:0.023407)0.745000:0.002235,ATV11_47:0.025472)0.840000:0.007627,(ATV11_40:0.031597,ATV11_59:0.013534)0.408000:0.006496)0.995000:0.033930)0.000000:0.000001,(ATV11_51:0.021389,ATV116_9:0.016076)0.963000:0.016085)1.000000:0.079108)0.000000:0.001444)0.918000:0.008327,(A1161I_5:0.019442,ATV11_39:0.035999)0.922000:0.012256)1.000000:0.029585,(ATV11_29:0.017022,((ATV11_37:0.023041,ATV11_34:0.012291)0.961000:0.020051,ATV11_36:0.043484)0.894000:0.014574)0.975000:0.017701)0.000000:0.000611,ATV11_66:0.026944)0.330000:0.003367,ATV11_55:0.007950)0.882000:0.008654,ATV11_19:0.035108)0.717000:0.009006,ATV11_13:0.014969)0.000000:0.000007,A1161_22:0.049398)0.921000:37.633050,A1161I_1:100.000000)0.903000:30.932937,ATV11_42:10.914678,A1161_27:10.587583);");
        tree.removeBootstrap();
        //--Internal newick parser
        newick_tree t = new newick_tree();
        t.parseNewick(tree.getTree());        
        System.out.println(t.PrintOut());

    }

    /**
     * This will create the sample workfloww in the /samples_workflows/ directory
     * 
     */
    public static void Create_the_sample_workflow() {
        boolean create_sample_workflow=true;
        String output_dir=config.currentPath() + File.separator+"sample_workflows";
        workflow_properties_dictionnary dic = new workflow_properties_dictionnary();        
        System.out.println("Creating sample workflows for the included applications (Etienne Lord - September 2011)");
        System.out.println("Note: If Error, make sure to delete the sample_workflows folder...");
        //--Test if the directory exists       
        if (Util.DirExists(output_dir)) {
            Util.deleteDir(output_dir);
        } else {
            //--Create it...
            try {
                File f=new File(output_dir);
                f.mkdir();
            } catch(Exception e) {
                System.out.println("Error. Cannot create the "+output_dir+" directory. ");
            }
            
        }
        
        
        for (String filename : workflow_properties.loadPropertieslisting(config.propertiesPath())) {
            workflow_properties properties = new workflow_properties(config.propertiesPath() + File.separator + filename);
            System.out.println(properties.getName());
            System.out.println("\t" + properties.getDescription());

            //--Create sample workflow
            if (create_sample_workflow) {
                String path=output_dir+File.separator+filename+".db";                 
                if (!df.New(path)) {
                     //--Error?
                     System.out.println("Unable to create sample workflow "+path);
                 } else {
                     //--Save some data in the workflow
                      //--Load sample tree and alignment
                MultipleSequences multi=new MultipleSequences(config.currentPath()+File.separator+"examples"+File.separator+"Mycobacteriophage.fasta");
                Alignment align=new Alignment(config.currentPath()+File.separator+"examples"+File.separator+"Mycobacteriophage.fasta");
                align.setName("Mycobacteriophage");
                Tree tree=new Tree(config.currentPath()+File.separator+"examples"+File.separator+"Mycobacteriophage.newick");
                tree.setName("Mycobacteriophage");          
                Tree species=new Tree(config.currentPath()+File.separator+"examples"+File.separator+"Woese _et_al_2000_gene_pheRS_32_species_speciestree.nh");
                species.setName("Species tree - Woese _et_al_2000_gene_pheRS");  
                Tree genes=new Tree(config.currentPath()+File.separator+"examples"+File.separator+"Woese_et_al_2000_gene_pheRS_32_species.nh");
                genes.setName("Genes tree - Woese _et_al_2000_gene_pheRS");   
                Text text=new Text();
                text.appendText("AF022214\nDQ398043\nJF937092\nJN049605\nJN408461\nJF704110\nEU744250\nAF271693\nJN408460\nGU339467\nZ18946\nJF937094\nJF704097\nJN561150\nJN243856\nJF792674\nHM152766\nGQ303263");
                text.setName("Mycobacterium accession list.");
                text.setNote("Obtained by BLAST of Mycobacteriophage D29 sub-sequence.");
                 //--Put in database.                 
                 multi.saveToDatabase();
                 align.saveToDatabase();
                 tree.saveToDatabase();
                 genes.saveToDatabase();
                 species.saveToDatabase();
                 text.saveToDatabase();
                     //--Create the workflow
                     if (df.getNextProjectID()<2) {
                         Project project=new Project();                         
                         project.setName("Demo project for "+properties.getName());
                         project.setAuthor("Armadillo team 2011");
                         project.setEmail("armadillo.workflow@gmail.com");
                         project.setInstitution("UQAM");
                         project.setNote("Demonstration workflow for "+properties.getName());
                         project.setDateCreated(Util.returnCurrentDateAndTime());
                         project.saveToDatabase();
                     }                                          
                     armadillo_workflow arm=new armadillo_workflow();
                     Workflows workflow = new Workflows(arm);
                     workflow.setName("Sample "+properties.getName()); 
                     properties.put("x", 330);
                     properties.put("y", 210); 
                     //--Insert the properties
                     workflow_object created_workflow_object=arm.createObjectWorkflow(properties);                     
                     if (created_workflow_object==null) {
                         System.out.println("Unable to create object from properties");
                     }
                     //--Create the output object
                     arm.workflow.createOutput_Objects(created_workflow_object.returnConnector(workflow_object.OUTPUT));
                     //--Comment
                     workflow_properties comment=new workflow_properties();
                     comment.load("COMMENTS.properties", config.propertiesPath());
                     comment.put("x",200);
                     comment.put("y", 30);
                     comment.setName("Sample");
                     comment.put("defaultColor", "GREEN");
                     comment.put("colorMode", "GREEN");
                     if (properties.isSet("Website")) {
                         comment.put("Description", "\nSample execution of "+properties.getName()+"\n"+"Website:\n"+properties.get("Website"));
                     } else {
                         comment.put("Description", "\nSample execution of "+properties.getName()+"\n");
                     }
                     
                      workflow_object comments_object=arm.createObjectWorkflow(comment); 
                      if (comments_object==null) {
                         System.out.println("Unable to create object from comment properties");
                     }
//                     System.out.println(comment);
                     //--Create undefined input object and connect them to the good port...
                     //--TO DO: Special input for some?
                     int x=100;
                     int y=180;                     
                     int i=0;
                     for (String str:properties.Inputed()) {                        
                        workflow_object wo=arm.createObject(arm.createTypeObject(str, x, y+=40));                       
                        //--Dummy test each connector starting at bottom...
                        if (!arm.workflow.addConnection(wo.returnConnector(1), created_workflow_object.returnConnector(2), ""+i++)) {
                            arm.workflow.addConnection(wo.returnConnector(1), created_workflow_object.returnConnector(3), ""+i++);
                        }
                      }
                     
                     //--Convert the Armadillo workflow to is string representation...
                     workflow.workflowToString();
                     //--Save the workflow
                     if (!workflow.saveToDatabase()) {
                         System.out.println("Unable to save workflow...");
                     }
                 } //--Project created? (db)           
         } //--Created workflow?
            
            
        } //--End for properties
    }    
    
    
     public static void SearchWorkflow_byTools() {
        Vector<String> dir=Config.listDirWithFullPath(config.projectsDir());
        armadillo_workflow a=new armadillo_workflow();
        for (String filename:dir) {
            if (filename.endsWith(".db")) {
                System.out.println(filename);
                df.Open(filename);
                Workflows w=new Workflows();                
                int number=df.getNextWorkflowsID()-1;
                for (int i=0; i<number;i++) {    
                boolean found=false;
                    Workflows work=new Workflows(a);
                    if (work.loadFromDatabase(i)) {
                        work.StringToWorkflow();
                        ArrayList<workflow_properties> tmp=new  ArrayList<workflow_properties>();
                        for (workflow_object o:work.workflow.workflow.work) {
                            if (o.getProperties().getName().startsWith("RNAFold")) {
                                tmp.add(o.getProperties());
                                found=true;
                            }
                        }
                        if (found) {
                            System.out.println(a.workflow.filename+" ID: "+i+"\t"+work.getName()+"\t");
                            //System.out.println("\t"+work.getName());
                            //for (workflow_properties p:tmp) System.out.println(p.get("Name")+"\t"+p.get("Description"));
                        }
                    }
                }
            }
        }
        
    }
    
    
    public static void web_listWorkflow(String workflowfile) {
        //--Test
        if (!Util.FileExists(workflowfile)) {
            System.out.println("File not found - "+workflowfile);
            System.exit(0);
        }
        if (!workflowfile.endsWith(".db")) {
            System.out.println("File must be a collection of workflow- "+workflowfile);
            System.exit(0);             
         }
        
        //armadillo_workflow a=new armadillo_workflow();
         if (!df.Open(workflowfile)) {
             
         } else {
             Workflows w=new Workflows();                
                Vector<Integer>ids=df.getAllWorkflowsID();
               System.out.println("<option>Choose Workflow</option>");
                for (int id:ids) {    
               
                    Workflows work=new Workflows(null);
                    if (work.loadFromDatabase(id)) {
                        //--Load
                        //work.StringToWorkflow();
                        System.out.println("<option value='"+id+"'>"+work.getName()+" ("+work.getDate_created()+") </option>");
                        //for (workflow_object o:work.workflow.workflow.work) {
                    }
                }
         }
            //--End
    }
    
     public static void web_CreateWorkflow(String workflowfile, String workflow_to_load) {
        //--Test
        if (!Util.FileExists(workflowfile)) {
           
            df.New(workflowfile);
            //armadillo_workflow arm=new armadillo_workflow();
            Workflows workflow = new Workflows(null);
            workflow.loadFromFile(workflow_to_load);
            workflow.saveToDatabase();
            System.exit(0);
            
        } else {
       
            System.out.println("File already exists - "+workflowfile);
            System.exit(0);
            
        }
        
    }
    
     public static void web_list(String workflowfile,String type,String output_file) {
        //--Test
        if (!Util.FileExists(workflowfile)) {
            System.out.println("File not found - "+workflowfile);
            System.exit(0);
        }
        if (!workflowfile.endsWith(".db")) {
            System.out.println("File must be a collection of workflow- "+workflowfile);
            System.exit(0);             
         }
               
         df.Open(workflowfile);
         
         Util u=new Util();
         u.open(output_file);
         
         if (type.equals("MultipleSequences")) {                  
            
             Vector<Integer> ids=df.getAllMultipleSequencesID();
                    
                      for (int id:ids) {
                            InfoMultipleSequences ms=df.getInfoMultipleSequence(id);                    
                            u.println("<option value='"+id+"'>"+ms.getName()+" ("+ms.getNbSequence()+" sequences) </option>");
                    }

              }              
               if (type.equals("Alignment")) {
                   Vector<Integer> ids=df.getAllAlignmentID();
                 System.out.println("<option value='0'>Choose aligned sequences</option>");
                   for (int id:ids) {
                    InfoAlignment ms=df.getInfoAlignment(id);
                    u.println("<option value='"+id+"'>"+ms.getName()+" ("+ms.getNbSequence()+"sequences) </option>");
                  }
              }               
              if (type.equals("Tree")) {
                   Vector<Integer> ids=df.getAllTreeID();
                     u.println("<option value='0'>Choose phylogenetic tree</option>");
                  for (int id:ids) {
                    Tree ms=df.getTree(id);
                    u.println("<option value='"+id+"'>"+ms.getName()+" ("+ms.getNote()+") </option>");
                  }                  
              }
              if (type.equals("Matrix")) {
                   Vector<Integer> ids=df.getAllMatrixID();
                  u.println("<option value='0'>Choose Matrix</option>");
                  for (int id:ids) {
                    Matrix ms=df.getMatrix(id);
                    u.println("<option value='"+id+"'>"+ms.getName()+" ("+ms.getNote()+") </option>");
                  }                  
              }
               if (type.equals("Workflow")) {
                   Vector<Integer> ids=df.getAllWorkflowsID();
                   int last=ids.lastElement();
                   u.println("<option>Choose Workflow</option>");
                  for (int id:ids) {
                        Workflows work=new Workflows(null);
                    if (work.loadFromDatabase(id)) {
               
                       if (id!=last) {
                          u.println("<option value='"+id+"'>"+work.getName() +" ("+work.getDate_created()+") </option>");
                       } else {
                           u.println("<option value='"+id+"' selected=selected>"+work.getName()+" ("+work.getDate_created()+") </option>");
                       }
                    }
                    
                  }                  
              }
            u.close();   
            //--End
    }
     
     public static void web_list_json(String workflowfile,String type,String output_file) {
        //--Test
        if (!Util.FileExists(workflowfile)) {
//            System.out.println("File not found - "+workflowfile);
//            System.exit(0);
        }
        if (!workflowfile.endsWith(".db")) {
//            System.out.println("File must be a collection of workflow- "+workflowfile);
//            System.exit(0);             
         }
               
         df.Open(workflowfile);
         //--class for tmp array
         
         ArrayList<web_type>tmp=new ArrayList<web_type>();
         
         Util u=new Util();
         u.open(output_file);
         
         if (type.equals("MultipleSequences")) {                  
            
             Vector<Integer> ids=df.getAllMultipleSequencesID();
                    
                      for (int id:ids) {
                            InfoMultipleSequences ms=df.getInfoMultipleSequence(id);                    
                            web_type n=new web_type();
                            n.id=id;
                            n.name=ms.getName();
                            n.info="("+ms.getNbSequence()+" sequences)";
                            tmp.add(n);
                            //u.println("<option value='"+id+"'>"+ms.getName()+" ("+ms.getNbSequence()+" sequences) </option>");
                    }

              }              
               if (type.equals("Alignment")) {
                   Vector<Integer> ids=df.getAllAlignmentID();
             
                   for (int id:ids) {
                    InfoAlignment ms=df.getInfoAlignment(id);
                    web_type n=new web_type();        
                    n.id=id;
                    n.name=ms.getName();
                    n.info="("+ms.getNbSequence()+" sequences)";
                    tmp.add(n);
                    //u.println("<option value='"+id+"'>"+ms.getName()+" ("+ms.getNbSequence()+"sequences) </option>");
                  }
              }               
              if (type.equals("Tree")) {
                   Vector<Integer> ids=df.getAllTreeID();
                   
                  for (int id:ids) {
                    Tree ms=df.getTree(id);
                    web_type n=new web_type();
                    n.id=id;
                    n.name=ms.getName();
                    n.info="("+ms.getNote()+")";
                    tmp.add(n);
                    //u.println("<option value='"+id+"'>"+ms.getName()+" ("+ms.getNote()+") </option>");
                  }                  
              }
              if (type.equals("Matrix")) {
                   Vector<Integer> ids=df.getAllMatrixID();
                  
                  for (int id:ids) {
                    Matrix ms=df.getMatrix(id);
                     web_type n=new web_type();
                    n.id=id;
                    n.name=ms.getName();
                    n.info="("+ms.getNote()+")";
                    tmp.add(n);
                    //u.println("<option value='"+id+"'>"+ms.getName()+" ("+ms.getNote()+") </option>");
                  }                  
              }
               if (type.equals("Workflow")) {
                   Vector<Integer> ids=df.getAllWorkflowsID();
                   int last=ids.lastElement();
                  
                  for (int id:ids) {
                        Workflows work=new Workflows(null);
                    if (work.loadFromDatabase(id)) {
                            web_type n=new web_type();
                            n.id=id;
                            n.name=work.getName();
                            n.info="("+work.getDate_created()+")";
                            tmp.add(n);
                       if (id!=last) {
                          //u.println("<option value='"+id+"'>"+work.getName() +" ("+work.getDate_created()+") </option>");
                       } else {
                           //u.println("<option value='"+id+"' selected=selected>"+work.getName()+" ("+work.getDate_created()+") </option>");
                       }
                    }
                    
                  }                  
              }
             
            String json = new Gson().toJson(tmp);               
            u.println(json);
            u.close();   
            
            
            //--End
    }
     
     
      public static void web_get(String workflowfile,String type,String number,String output_file) {
        //--Test
        int id=Integer.valueOf(number);  
          
        if (!Util.FileExists(workflowfile)) {
            System.out.println("File not found - "+workflowfile);
            System.exit(0);
        }
        if (!workflowfile.endsWith(".db")) {
            System.out.println("File must be a collection of workflow- "+workflowfile);
            System.exit(0);             
         }
        
              df.Open(workflowfile);
              if (type.equals("MultipleSequences")) {
                  MultipleSequences m=new MultipleSequences(id);
                  Util u=new Util();
                  u.open(output_file);
                  u.println(m.outputFasta());
                  u.close();
              }
              if (type.equals("Alignment")) {
                  Alignment m=new Alignment(id);
                  Util u=new Util();
                  u.open(output_file);
                  u.println(m.outputFasta());
                  u.close();
              }
              if (type.equals("Tree")) {
                   Tree m=new Tree(id);
                   Util u=new Util();
                  u.open(output_file);
                  u.println(m.getTree());
                  u.close();
              }
              if (type.equals("Matrix")) {
                  Matrix m=new Matrix(id);
                  Util u=new Util();
                  u.open(output_file);
                  u.println(m.getMatrix());
                  u.close();
              } 
               if (type.equals("OutputText")) {
                  OutputText m=new OutputText(id);
                  Util u=new Util();
                  u.open(output_file);
                  u.println(m.toString());
                  u.close();
              } 
              if (type.equals("Workflow")) {
                  Workflows f=new Workflows(id);
                  Util u=new Util();
                  u.open(output_file);
                  u.println(f.getWorkflow_in_txt());
                  u.close();                  
              }
               if (type.equals("Latest")) {
                  int last_id=df.getNextWorkflowsID()-1; 
                  Workflows f=new Workflows(last_id);
                  Util u=new Util();
                  u.open(output_file);
                  u.println(f.getWorkflow_in_txt());
                  u.close();
                  
              }
            //--End
    }
    
      public static void web_getHtml(String workflowfile,String type,String number,String output_file) {
        //--Test
        int id=Integer.valueOf(number);  
          
        if (!Util.FileExists(workflowfile)) {
            System.out.println("File not found - "+workflowfile);
            System.exit(0);
        }
        if (!workflowfile.endsWith(".db")) {
            System.out.println("File must be a collection of workflow- "+workflowfile);
            System.exit(0);             
         }
        
      
         df.Open(workflowfile);
              if (type.equals("MultipleSequences")) {
                  MultipleSequences m=new MultipleSequences(id);
                  Util u=new Util();
                  u.open(output_file);
                  u.println(m.toHtml());
                  u.close();
              }
               if (type.equals("Alignment")) {
                  Alignment m=new Alignment(id);
                  Util u=new Util();
                  u.open(output_file);
                  u.println(m.toHtml());
                  u.close();
              }
              if (type.equals("Tree")) {
                   Tree m=new Tree(id);
                   Util u=new Util();
                  u.open(output_file);
                  u.println(m.toHtml());
                  u.close();
              }
              if (type.equals("Matrix")) {
                  Matrix m=new Matrix(id);
                  Util u=new Util();
                  u.open(output_file);
                  u.println(m.toHtml());
                  u.close();
              } 
               if (type.equals("OutputText")) {
                  OutputText m=new OutputText(id);
                  Util u=new Util();
                  u.open(output_file);
                  u.println(m.toHtml());
                  u.close();
              } 
              if (type.equals("Workflow")) {
                  Workflows f=new Workflows(id);
                  Util u=new Util();
                  u.open(output_file);
                  u.println(f.toHtml());
                  u.close();                  
              }             
            //--End
    }
      
       public static int web_put(String workflowfile,String type,String input_file) {
        //--Test
        int id=0;
          
        if (!Util.FileExists(workflowfile)) {
            System.out.println("File not found - "+workflowfile);
            System.exit(0);
        }
        if (!workflowfile.endsWith(".db")) {
            System.out.println("File must be a collection of workflow- "+workflowfile);
            System.exit(0);             
         }
        
              df.Open(workflowfile);
              if (type.equals("MultipleSequences")) {
                                    
                  MultipleSequences m=new MultipleSequences();
                  m.loadFromFile(input_file);                  
                  if (m.saveToDatabase()) {
                      id=m.getId();
                  }                  
              }
              if (type.equals("Alignment")) {
                  Alignment m=new Alignment();                  
                  if (!m.loadFromFile(input_file) ) return 0; 
                  m.saveToDatabase();
                  if (m.saveToDatabase()) {
                      id=m.getId();
                  }    
              }
              if (type.equals("Tree")) {
                   Tree m=new Tree();
                  if (!m.loadFromFile(input_file)) return 0;                
                 if (m.saveToDatabase()) {
                      id=m.getId();
                  }    
              }
              if (type.equals("Matrix")) {
                  Matrix m=new Matrix();
                 if (!m.loadFromFile(input_file) ) return 0; 
                
                if (m.saveToDatabase()) {
                      id=m.getId();
                  }    

              } 
               if (type.equals("Text")) {
                  Text m=new Text();
                 if (!m.loadFromFile(input_file) ) return 0; 
                  if (m.saveToDatabase()) {
                      id=m.getId();
                  }    
              }              
            //--End
             return id; 
    }
      
      
     public static void SearchWorkflow_AnalysisForProgram() {
        //--Create a list of all the program to find         
         
         HashMap<String, Vector<String>>ProgramToFind=new HashMap<String, Vector<String>>(); //--WE look for class name...
         Vector<String> dir_properties=Config.listDirWithFullPath(config.propertiesPath());
         //-Create a list of all the program to find and an example of workflow?
         for (String properties_filename:dir_properties) {
             workflow_properties pro=new workflow_properties(properties_filename);
             ProgramToFind.put(pro.get("ClassName"),new Vector<String>());
         }        
         Vector<String> dir=Config.listDirWithFullPath(config.projectsDir());
        armadillo_workflow a=new armadillo_workflow();
        for (String filename:dir) {
            if (filename.endsWith(".db")) {
                System.out.println(filename);
                df.Open(filename);
                Workflows w=new Workflows();                
                int number=df.getNextWorkflowsID()-1;
                for (int i=0; i<number;i++) {    
                boolean found=false;
                    Workflows work=new Workflows(a);
                    if (work.loadFromDatabase(i)) {                        
                        work.StringToWorkflow();
                        //--Note: we take only the completed items... To reduce the number to calculate...
                        if (work.isCompleted()) {
                            ArrayList<workflow_properties> tmp=new  ArrayList<workflow_properties>();
                            for (workflow_object o:work.workflow.workflow.work) {
                                if (o.getProperties().get("ObjectType").equals("Program")) {
                                    Vector<String>v= ProgramToFind.get(o.getProperties().get("ClassName"));
                                    if (v!=null) {
                                        v.add(filename+"-"+work.getId());                                
                                    } 
                                } 
                            }
                        }
                    } //-End if loaded from database...
                } //--End for workflows in filename 
            } //--End load workflow
        } //--End filename        
        //--Write summary file....
        Util u=new Util("ResultsProgram.txt");
        for (String name:ProgramToFind.keySet()) {
            Vector<String>results=ProgramToFind.get(name);
            u.println(name);
            for (String r:results) u.println("\t"+r);            
        }        
        u.close();
    }
    
     public static void SearchWorkflow_forError() {
        Vector<String> dir=Config.listDirWithFullPath(config.projectsDir());
        armadillo_workflow a=new armadillo_workflow();        
        //--Statistics about the workflows in the current project directory
        for (String filename:dir) {
            if (filename.endsWith(".db")) {
                System.out.println(filename);
                df.Open(filename);
                Workflows w=new Workflows();                
                int number=df.getNextWorkflowsID()-1;
                for (int i=0; i<number;i++) {    
                boolean found_error=false;
                boolean found_executablenotfound=false;
                    Workflows work=new Workflows(a);
                    if (work.loadFromDatabase(i)) {
                        work.StringToWorkflow();
                        ArrayList<workflow_properties> tmp=new  ArrayList<workflow_properties>();
                        for (workflow_object o:work.workflow.workflow.work) {
                            if (o.getProperties().isProgram()) {
                                if (o.getProperties().getStatus()==RunProgram.status_error) found_error=true;
                                if (o.getProperties().getStatus()==RunProgram.status_programnotfound) found_executablenotfound=true;
                            }                            
                        }
                        if (found_error) {
                            System.out.println(a.workflow.filename+" ID: "+i+"\t"+work.getName()+"\t have error...");
                            //System.out.println("\t"+work.getName());
                            //for (workflow_properties p:tmp) System.out.println(p.get("Name")+"\t"+p.get("Description"));
                        }
                        if (found_executablenotfound) {
                            System.out.println(a.workflow.filename+" ID: "+i+"\t"+work.getName()+"\t executable not found...");
                        }
                    }
                }
            }
        }        
    }
    
    public static void testSOAPWSDL() {
        SOAPWeb soap=new SOAPWeb();
        soap.setUrlString("http://xml.nig.ac.jp/wsdl/GetEntry.wsdl");
        try {
            soap.do_run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void test_Ncbi_Eutils() {
        //--Test pype Pubmed  
        eutil e1 = new eutil();
        eutil e2 = new eutil();
        eutil e3 = new eutil();
        eutil e4 = new eutil();


        //--Test pype nucleotide
        e3.setEngine("esearch");
        e3.setDatabase("nucleotide");
        e3.setUseHistory(true);
        e3.setTerm("adipo");
        e3.execute();
        System.out.println(e3);
        e4.setEngine("efetch");
        e4.setDatabase("nucleotide");
        e4.setRettype("fasta");
        e4.setRetmax(2);
        e4.setOutputToDisk(true); //--bypass buffer
        e4.setWebEnv(e3.returnWebEnv());
        e4.setQuery_key(e3.returnQueryKey());
        e4.execute("output.txt");
        System.out.println(e4);
    }
    
     public static void test_download_multipleGi() {
        //--Test pype Pubmed  
        Util u=new Util();
        Vector<String>data=u.read("gi.txt"); 
        int i=0;
        int count=data.size();
        for (String stri:data) {
            String gi=stri.trim();
            if (!stri.isEmpty()&&!Util.FileExists(gi+".gb")) {
               try {
                System.out.print("Downloading "+gi+" "+i+"/"+count);
                eutil e4 = new eutil();
                e4.setEngine("efetch");
                e4.setDatabase("nucleotide");
                e4.setRettype("gb");
                e4.setRetmax(2);
                e4.setTerm("");
                e4.setId(gi);
                e4.setOutputToDisk(true); //--bypass buffer                
                e4.execute(gi+".gb");            
                   System.out.println("done.");
                   i++;
               } catch(Exception e) {
                   e.printStackTrace();
               }
            }
        //System.out.println(e4);
        }
    }
    
    public static void load_Nexus() {
        Config.library_mode=true;                
        Tree t=new Tree();      
//        Newick string without branch lengths specified: 
//(mammal,(turtle,rayfinfish,(frog,salamander)));
//Newick string with branch lengths specified: 
//(mammal:0.14,turtle:0.02,(rayfinfish:0.25,(frog:0.01,salamander:0.01):0.12):0.09);
//Newick string with branch lengths and root specified: 
//(mammal:0.14,(turtle:0.02,(rayfinfish:0.25,(frog:0.01,salamander:0.01):0.12):0.09):0.03);
//Newick string with branch lengths and bootstrap scores specified: 
//(mammal:0.14,turtle:0.02,(rayfinfish:0.25,(frog:0.01,salamander:0.01)50:0.12)95:0.09);
        
        
//        t.readNexus(config.currentPath()+"\\executable\\Ktreedist_v1\\Examples\\big\\nexus\\A_Cyprinidae_reference.tree"); 
//        MultipleTrees multi=new MultipleTrees(config.currentPath()+"\\executable\\Ktreedist_v1\\Examples\\big\\nexus\\B_Cyprinidae_mit_genes.tree");        
//        System.out.println(t);      
//        System.out.println(multi);
        newick_tree nt=new newick_tree();
//        nt.parseNewick(t.getTree());
        //nt.setName();
        System.out.println(nt.replaceRef_by_Number());
        nt.parseNewick("(mammal:0.14,turtle:0.02,(rayfinfish:0.25,(frog:0.01,salamander:0.01)50:0.12)95:0.09);");
        System.out.println(nt.PrintInfo());
        //System.out.println(nt.PrintOut());
        for (node n:nt.node_list) {            
            System.out.println(n+" "+nt.getDegree(n));
        }
        
        nt.parseNewick("(mammal:0.14,(turtle:0.02,(rayfinfish:0.25,(frog:0.01,salamander:0.01):0.12):0.09):0.03);");
        for (node n:nt.node_list) {            
            System.out.println(n+" "+nt.getDegree(n));
        }
        System.out.println(nt.PrintInfo());
        System.exit(0);
    }
    

    public static void test_Ncbi_QBlast() {
        //--Note: use the blast_and_download class        
        qblast q = new qblast();
       
        System.out.println("Testing Ncbi QBLAST");
        System.out.println("Generating 5 random sequences of len 100 bp...(DNA)");
        Alignment s = new Alignment(100, 5);
        System.out.println("Running blast...");
        q.setQUERY_FASTA(s.outputFasta());
        q.QBlast_PUT();

        q.setFILTER("L");
        try {
            String status = "";
            while (!status.equals(q.READY) || status.equals(q.UNKNOWN)) {
                if (status.equals(q.UNKNOWN)) {
                    System.out.println("Error with this QBLAST. Please retry.");
                    return;
                }
                status = q.QBlast_GET();
                System.out.println("QBLAST RID :" + q.returnRID() + " Status: " + status);
                if (!status.equals(q.READY)) {
                    Thread.sleep(3000);
                }
            }
            q.saveToFile("out.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        // Parsing Blast
        //
        System.out.println("Parsing blast file...");

        blastParser bp = new blastParser("out.txt");
        System.out.println(bp);
        Util.deleteFile("out.txt");
    }

    public static void test_EBI_Blast() {
        //--Note: use the blast_and_download class        
        blastParser bp=new blastParser();
        Text ebi=new Text(config.currentPath()+File.separator+"examples"+File.separator+"blast_ebi.txt");
        Text ncbi=new Text(config.currentPath()+File.separator+"examples"+File.separator+"blast_ncbi.txt");
        //Pattern pat_blasthit_embl2=Pattern.compile("(.*)\\|(.*?)\\s{1,}(.*?)\\s{1,}(.*)\\s{1,}([0-9]*|[0-9]*\\.[0-9]*)\\s*([0-9]*.[0-9]*|[0-9]*e-[0-9]*)",Pattern.CASE_INSENSITIVE);
        //Matcher m=pat_blasthit_embl2.matcher("lcl|TR:Q6Q2K6_PIG  Q6Q2K6 Adiponectin (Fragment) OS=Sus scrofa PE...    427   7e-149");
//        if (m.find()) {
//            System.out.println(m.groupCount());
//            for (int i=0; i<m.groupCount();i++) {
//                System.out.println(m.group(i));
//            }
//        }
        //bp.loadText(ncbi.getText());
        //System.out.println(bp);
       bp.loadText(ebi.getText());
        System.out.println(bp);
    }
    
    public static void Mickael_Ncbi_QBlast() {

        //--Current data
        MultipleSequences ms=new MultipleSequences("all_lib.mirnas.pred.analyses.com.uni.fasta");
         //Alignment ms=new Alignment(100,5);
         LinkedList<Sequence> to_download=new java.util.LinkedList<Sequence>();
         for (Sequence s:ms.getSequences()) {
             to_download.add(s);
         }
         //--while(to_download.size()>2) to_download.pollFirst();
                
        LinkedList<Demo_blast_and_download> workers=new LinkedList<Demo_blast_and_download>();           
                
            while(!to_download.isEmpty()) {
                    System.out.println(to_download.size());
                     //Sequence n=to_download.pollFirst();
                    MultipleSequences ms2=new MultipleSequences();                    
                    for(int j=0; j<100;j++) {
                        Sequence s=to_download.pollFirst();
                        if (s!=null) ms2.add(s);
                    }                    
                     Demo_blast_and_download d=new Demo_blast_and_download(ms2.getFasta(), ms2.getSequences().get(0).getSequenceName()+".xml");
                }               
    }

    public static void listSequence() {
//        int max=df.getNextSequenceID();
//        for (int i=1; i<max;i++) {
//            System.out.println(new InfoSequence(i));
//        }
        Sequence iter = new Sequence();
        while (iter.hasNext()) {
            Sequence s = (Sequence) iter.next();
            System.out.println(s.getId() + ": " + s.getName() + ":" + s.getLen());
        }
    }

    public static void testWDSL() {
        usewsdl u = new usewsdl();

        //u.getInfo("http://biomoby.org/services/wsdl/mpiz-koeln.mpg.de/BuildPhylogeneticTreeFromFastaAlignment");
        //u.loadWSDL("http://mrs.cmbi.ru.nl/mrsws/blast/wsdl");
        //u.loadWSDL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/soap/eutils.wsdl");
        //u.loadWSDL("http://www.ebi.ac.uk/ws/services/urn:Dbfetch?wsdl");
        //u.loadWSDL("http://xml.nig.ac.jp/wsdl/GTPS.wsdl");
        //u.loadWSDL("http://ws.apache.org:5049/axis/services/echo");
       // u.loadWSDL("http://www.ebi.ac.uk/Tools/services/soap/clustalw2?wsdl");
        u.callwstojava("http://www.ebi.ac.uk/Tools/services/soap/ncbiblast?wsdl", "ncbiblast.java");
        //u.loadWSDL("http://www.ebi.ac.uk/Tools/services/soap/ncbiblast?wsdl");
        
       // u.printInfo();
        //u.createWSDL(5); 
//        System.out.println("Service:");
//        for (Service s:u.return_WSDL_Service()) {
//            System.out.println(s);
//            System.out.println("");
//        }
//        
//         System.out.println("Port types:");
//        for (PortTypeImpl p:u.return_WSDL_Port()) {
//            System.out.println(p.getOperations());
//            System.out.println("");
//        }
//        
//        System.out.println("EndPoint URI:");
//        System.out.println(u.return_WSDL_endpointURI());
        
        //System.out.println(u.return_WSDL_InputPart());
        //--Test demo
        //--This wiil test all WSDL operation -> likely, it will not work...
        //for (int i = 0; i < u.return_WSDL_Operation().size(); i++) {
        //    u.createWSDL(i);
        //}
        
        //u.createWSDL(5); 
        
        //u.getInfo("http://xml.nig.ac.jp/wsdl/GetEntry.wsdl");
        
        
        //--Call
//         Service service = new Service();
//       Call call = (Call) service.createCall();
//
//        call.setTargetEndpointAddress(new java.net.URL(endpoint));
//        call.setOperationName("greet");
//        call.addParameter("name", XMLType.XSD_STRING, ParameterMode.IN);
//        call.setReturnType(XMLType.XSD_STRING);
//
//        String msg = (String)call.invoke(new Object[] {args[0]});
//        call.setTargetEndpointAddress("http://localhost:8080/axis/services/WebService");  
//        call.setOperationStyle(Style.RPC);  
//        call.setOperationName(new QName(NAMESPACE, "addPDFFile"));  
//        call.addParameter("code",Constants.XSD_STRING, ParameterMode.IN);  
//        call.addParameter("pdfFile", qnameAttachment, ParameterMode.IN);   
//        call.setReturnType(Constants.XSD_BOOLEAN);  
        
        
        
        System.exit(0);
    }

    public static void generate_list_Application_by_Categories() {
        //--Generate the report
//        Util report=new Util("list_application.html");
//      
//        //--Note: we use already available structures
//         Vector<workflow_properties>program=new Vector<workflow_properties>();
//        HashMap<String,ToolboxMutableTreeNode> ListCategoryNode=new HashMap<String,ToolboxMutableTreeNode>();
//        for (String filename:workflow_properties.loadPropertieslisting(config.get("propertiesPath"))) {
//            workflow_properties tmp=new workflow_properties();
//            tmp.load(filename, config.get("propertiesPath"));
//            program.add(tmp);
//        }
//        workflow_properties rootnode=new workflow_properties();
//        rootnode.setName("Armadillo");
//        ToolboxMutableTreeNode treeroot=new ToolboxMutableTreeNode(rootnode);
//        for (workflow_properties lnode:program) {
//           String category=lnode.get("Type");
//           if (category.equals(lnode.NotSet)) {
//               Config.log("Not Set: "+lnode+"\n"+lnode.filename);
//           } 
//           ToolboxMutableTreeNode newNode=new ToolboxMutableTreeNode(lnode);
//           ToolboxMutableTreeNode rootNode=ListCategoryNode.get(category);           
//           if (category.equals("For")) {
//                //--Not needed for now..
//               //category.equals("Begin")||
//               treeroot.add(newNode);
//           } else
//               if (rootNode==null) {
//                  workflow_properties newnode_properties=new workflow_properties();
//                  newnode_properties.put("Type",category);
//                  newnode_properties.setName(category);
//                  ToolboxMutableTreeNode newRootNode=new ToolboxMutableTreeNode(newnode_properties);
//                  newRootNode.add(newNode);
//                  ListCategoryNode.put(category, newRootNode);
//               } else {
//                  rootNode.add(newNode);
//               }
//       }
//       LinkedList<ToolboxMutableTreeNode> list=new LinkedList<ToolboxMutableTreeNode>();
//       list.addAll(ListCategoryNode.values());
//       Collections.sort(list);
//
//       for (ToolboxMutableTreeNode lnode:list) treeroot.add(lnode);
//
//       jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeroot));
// 
      
      
        
        
    }
    
    public static void generate_html_for_Workflow_Alpha() {
        //--Test Etienne
//        Workflows w=new Workflows();
//        while(w.hasNext())
//        {
//            Workflows new_workflow=(Workflows) w.next();
//            armadillo_workflow a=new armadillo_workflow();
//            new_workflow.workflow=a;
//            new_workflow.StringToWorkflow();
//            test.add(a);
//            results r=new results();
//            r.generateProjectContentPage(a, "c:\\"+new_workflow.getId()+".html");
//            System.out.println(Util.PrintMemory());
//        }
    }

    public static void demo_db() {

        df.getDatabase().execute("CREATE TABLE pdf (Name TEXT, value BLOB);");

        try {
            File f = new File("1.pdf");
            BufferedReader bf = new BufferedReader(new FileReader(f));
            FileWriter fw = new FileWriter(new File("test.pdf"));
            PreparedStatement ps = df.getDatabase().conn.prepareStatement("INSERT INTO pdf (Name, value) VALUES (?, ?);");
            ps.setString(1, "test");
            ps.execute();

            System.out.println(df.getDatabase().conn.getCatalog());

            ResultSet rs = df.getDatabase().executeQuery("SELECT * FROM pdf;");
            fw.close();

        } catch (Exception e) {
        }
    }

    public static void test_WSDLdbfetch() {
//        WSDbfetchClient client = new WSDbfetchClient();
//        try {
//        System.out.println(client.getSupportedDBs());
//        } catch(Exception e){}
        workflow_properties prop = new workflow_properties();
        prop.load("dbFetchEBI.properties", config.propertiesPath());
        programs p = new programs(new Workflows(new armadillo_workflow()));
        p.Run(prop);

//
//        //System.out.println(prop.getProperties());
//        prop.put("debug", true);
//        dbFetchWebEBI dbf=new dbFetchWebEBI(prop);
//        System.out.println(dbf.getOutputTXT().size());
//        System.out.println(dbf.getOutputText());
//        System.exit(-1);
    }

    public static void testJavaProgramClass() {
        JavaProgramClass c = new JavaProgramClass(new workflow_properties());
    }

    public static void Emboss() {
        WSEmbossClient client = new WSEmbossClient();

        System.out.println("Getting list of tools from EMBOSS at EBI...");

        try {
            for (String s : client.getTools()) {

                System.out.println(s);
                //System.out.println(client.getToolInfo(s.substring(0, s.indexOf(":"))));
            }
        } catch (Exception e) {
        }
    }

    /**
     * Demo program to extract data from the WsWuBlastClient at EBI
     */
    public static void WuBlast() {
        WSWUBlastClient client = new WSWUBlastClient();

        try {
            System.out.println("Getting list of databases from WuBlast at EBI...");
            for (OutData s : client.getDatabases()) {
                System.out.println(s.getPrint_name() + "\t" + s.getName());
            }
            System.out.println("Getting list of programs from WuBlast at EBI...");
            for (OutData s : client.getPrograms()) {
                System.out.println(s.getPrint_name() + "\t" + s.getName());
            }
            System.out.println("Getting list of matrices from WuBlast at EBI...");
            for (OutData s : client.getMatrices()) {
                System.out.println(s.getPrint_name() + "\t" + s.getName());
            }
            System.out.println("Getting list of sorts from WuBlast at EBI...");
            for (OutData s : client.getSort()) {
                System.out.println(s.getPrint_name() + "\t" + s.getName());
            }
            System.out.println("Getting list of stats from WuBlast at EBI...");
            for (OutData s : client.getStats()) {
                System.out.println(s.getPrint_name() + "\t" + s.getName());
            }
            System.out.println("Getting list of sensitivity from WuBlast at EBI...");
            for (OutData s : client.getSensitivity()) {
                System.out.println(s.getPrint_name() + "\t" + s.getName());
            }
        } catch (Exception e) {
        }
    }

    public static void Create_Properties_List_CSV() {
        System.out.println("This will create a file : results.csv with the current Armadillo properties");
        Vector<workflow_properties> program = new Vector<workflow_properties>();
        //--Load the list of properties found in the propertiesPath
        StringBuilder st = new StringBuilder();
        for (String filename : workflow_properties.loadPropertieslisting(config.get("propertiesPath"))) {
            workflow_properties tmp = new workflow_properties();
            tmp.load(filename, config.get("propertiesPath"));
            program.add(tmp);
        }
        for (workflow_properties lnode : program) {
            String category = lnode.get("Type");
            st.append(lnode.getName() + "\t" + category + "\n");
        }
        Text text = new Text();
        text.setText(st.toString());
        text.Output("results.csv");
    }
    
     public static void Create_Properties_List_Command() {
        System.out.println("This will create a file : results.csv with the current Armadillo properties");       
        //--Load the list of properties found in the propertiesPath
        StringBuilder st = new StringBuilder();
       //--Create a big workflow with all the command line...
        armadillo_workflow a=new armadillo_workflow();
        
        //--Run.
        //--Ge tthe information after...
        
        
        int x=50;
            int y=50;
        for (String filename : workflow_properties.loadPropertieslisting(config.get("propertiesPath"))) {
            workflow_properties properties = new workflow_properties();
            properties.load(filename, config.get("propertiesPath"));
            
            //--Load the associated class?
            RunProgram r=null;
            a.createObject(properties, x, y); 
            x+=50;
            if (x>1200) {x=50;y+=50;}
            //--Code from programs           
           
           }
       
            
//        for (workflow_properties lnode : program) {
//            String category = lnode.get("Type");
//            st.append(lnode.getName() + "\t" + category + "\n");
//        }
//        Text text = new Text();
//        text.setText(st.toString());
//        text.Output("results.csv");
    }

      public static void Create_Properties_Excel_CSV() {
        System.out.println("This will create a file : results.csv with the current Armadillo properties");
        Vector<workflow_properties> program = new Vector<workflow_properties>();
        //--Load the list of properties found in the propertiesPath
        StringBuilder st = new StringBuilder();
        for (String filename : workflow_properties.loadPropertieslisting(config.get("propertiesPath"))) {
            workflow_properties tmp = new workflow_properties();
            tmp.load(filename, config.get("propertiesPath"));
            program.add(tmp);
        }
        //--Create a HashOf the properties
        HashMap<String, Integer>Key_number=new HashMap<String, Integer>(); //-HashMap with all key for all properties
        for (workflow_properties lnode : program) {
            for (Object key:lnode.keySet()) {
                String obString=(String)key;
                if(!Key_number.containsKey(obString)) {
                    Key_number.put(obString,1);
                } else {
                    Integer number=Key_number.get(obString);
                    Key_number.put(obString, number+1);
                }
            }       
        }
        //--Create the header in the CSV
        LinkedList<String>header=new LinkedList<String>();
        for (String k:Key_number.keySet()) {
            header.add(k);
        }
        Collections.sort(header);
        String hdr="Name,";
        for (String key:header) {
            hdr+=key+",";
            }
        st.append(hdr+"\n");
        for (workflow_properties lnode : program) {
            st.append(lnode.getName()+",");
            for (String key:header) {
                if (lnode.isSet(key)) {
                    st.append("X,");
                } else st.append(" ,");
            }
            st.append("\n");
        }
        Text text = new Text();
        text.setText(st.toString());
        text.Output("results_properties.csv");
    }
    
    public static void Create_Class_info() {
       
        workflow_properties_dictionnary dict=new workflow_properties_dictionnary();
        dict.getArmadilloClassListing();
        for (String program:dict.classname) {
              
            if (program.startsWith("programs")) {
                System.out.println(program); 
                Classdata c=new Classdata(program.replaceAll("/", "."));
                 
                  System.out.println(c);
                    for (String s:c.All) System.out.println(s);
            } 
        }
       
    }
    
    public static void Test_Running_Workflow() {
        ////////////////////////////////////////////////////////////////////////
        //
        //         df.Open(config.getLastProject());
        System.out.println("Inserting workflows into this database");
        MemoryTest m=new MemoryTest();
//        Vector<String> files = Config.listDir(Config.currentPath + "//examples//");
//        for (String filename : files) {
//            if (filename.endsWith(".txt")) {
//                Workflows workflow = new Workflows(new armadillo_workflow());
//                boolean b = workflow.loadWorkflow(Config.currentPath + "//examples//" + filename);
//                workflow.setName(filename);
//                workflow.setDate_created(Util.returnCurrentDateAndTime());
//                workflow.setNote("Loaded on " + Util.returnCurrentDateAndTime());
//                System.out.println("Loading " + filename + " " + b);
//                workflow.saveToDatabase();
//            }
//        }
          
       
                Workflows workflow = new Workflows(new armadillo_workflow());
                boolean b = workflow.loadWorkflow(Config.currentPath + "simulation.txt");
                workflow.setName("simulation");
                workflow.setDate_created(Util.returnCurrentDateAndTime());
                workflow.setNote("Loaded on " + Util.returnCurrentDateAndTime());
                //System.out.println("Loading " + filename + " " + b);
                workflow.saveToDatabase();
        
        boolean done = false;
        Scanner sc = new Scanner(System.in);
        while (!done) {
            Workflows w = new Workflows();
            w.hasNext();
            for (int id : w.getAllId()) {
                System.out.println("[" + id + "] " + w.getNameId(id));
            }
            System.out.println("0.  Exit");
            String stri = sc.next();
            //Try to have a number
            int choice = 255;
            try {
                choice = Integer.valueOf(stri);
            } catch (Exception e) {
                choice = 255;
            }
            //--Very bad programmind
            switch (choice) {
                case 0:
                    System.exit(0);
                    break;
                default:
                    break;
            }
            if (choice != 255) {
                Workflows run = new Workflows(new armadillo_workflow());
                run.loadFromDatabase(choice);
                run.StringToWorkflow();
                program.programs torun = new program.programs(run);
                if (torun != null) {
                    torun.Run();
                    //--Wait till its done...
                    while (!torun.isDone()) {
                    }
                }
                MemoryTest m2=new MemoryTest();

            }
        }
    }
    
    
     public static void Test_Running_Sample_Workflow() {
        ////////////////////////////////////////////////////////////////////////
        //
        //         df.Open(config.getLastProject());
      
        //MemoryTest m=new MemoryTest();
        //--Select the file in the help directory
         
       Vector<String> files = Config.listDirWithFullPath(config.dataPath()+File.separator+"help");
        
        
        
        for (String filename : files) {
            if (filename.endsWith(".db")) {
                System.out.println(filename);
                
                if (df.Open(filename)) {
                    System.out.println(df.db.dbFileName);
                    System.out.println("Opened filename: "+filename);
                    System.out.println(df.isDatabaseConstainsTables());
                    int workflow_id=df.getNextWorkflowsID()-1;
                     if (workflow_id>0) {                         
                          System.out.println("Loading last workflow with ID ["+workflow_id+"].");
                          Workflows run = new Workflows(new armadillo_workflow());
                            run.loadFromDatabase(workflow_id);
                            run.StringToWorkflow();
                            program.programs torun = new program.programs(run);
                            if (torun != null) {
                                torun.Run();
                                //--Wait till its done...
                                while (!torun.isDone()) {
                                }
                                run.saveToDatabase();
                                //--Get the last workflow 
                                workflow_id=df.getNextWorkflowsID()-1;
                                run.loadFromDatabase(workflow_id);
                                run.StringToWorkflow();                                
                                boolean correct=true;                                
                                for (workflow_object object:run.workflow.workflow.work) {
                                    workflow_properties obj=object.getProperties();
                                     workflow_object tmp=null;
                                if (!obj.get("ObjectType").equals(workflow_properties.NotSet)) {

                                    // CASE 2: Else, we create objects
                                    if (obj.get("ObjectType").equals("For")||obj.get("ObjectType").equals("While")) {

                                    } else
                                    if (obj.get("ObjectType").equals("If")) {

                                    } else
                                     if (obj.get("ObjectType").equals("Begin")) {

                                    } else
                                        if (obj.get("ObjectType").equals("End")) {

                                        } else
                                          if (obj.get("ObjectType").equals("Script")) {

                                         } else
                                    if (obj.get("ObjectType").equals("ScriptBig")) {

                                    } else
                                    if (obj.get("ObjectType").equals("Output")) {

                                    } else
                                    if (obj.get("ObjectType").equals("OutputDatabase")) {

                                    } else
                                    if (obj.get("ObjectType").equals("Variable")) {

                                    } else
                                    {
                                        //By default a Program...
                                        if (obj.getStatus()==RunProgram.status_error) {
                                            correct=false;
                                        }                                        
                                        
                                    }
                                } //--End type of object  
                                } //--End for workflow properties
                                System.out.println("Ok? "+correct);
                            }
                            //MemoryTest m2=new MemoryTest(); 
                          
                     }
                    
                
            } else {
                    System.out.println("Unable to load workflow in db");
            }
        }
//        boolean done = false;
//        Scanner sc = new Scanner(System.in);
//        while (!done) {
//            Workflows w = new Workflows();
//            w.hasNext();
//            for (int id : w.getAllId()) {
//                System.out.println("[" + id + "] " + w.getNameId(id));
//            }
//            System.out.println("0.  Exit");
//            String stri = sc.next();
//            //Try to have a number
//            int choice = 255;
//            try {
//                choice = Integer.valueOf(stri);
//            } catch (Exception e) {
//                choice = 255;
//            }
//            //--Very bad programmind
//            switch (choice) {
//                case 0:
//                    System.exit(0);
//                    break;
//                default:
//                    break;
//            }
//            if (choice != 255) {
//                Workflows run = new Workflows(new armadillo_workflow());
//                run.loadFromDatabase(choice);
//                run.StringToWorkflow();
//                program.programs torun = new program.programs(run);
//                if (torun != null) {
//                    torun.Run();
//                    //--Wait till its done...
//                    while (!torun.isDone()) {
//                    }
//                }
//                MemoryTest m2=new MemoryTest();
//
//            }
//        }
    }
  }

     
    public static void stat_data_herve() {
        
        
    } 

    public static void Test_Running_Workflow_From_currentDB() {
        //////////////////////////////////////////////////////////////////////
        ///   Load the current project file
        df.Open(config.getLastProject());
//        System.out.println("Inserting workflows into this database");
//        workflow_properties p=new workflow_properties();
//        Vector<String>files=Config.listDir(Config.currentPath+"//examples//");
//        for (String filename:files) {
//            if (filename.endsWith(".txt")) {
//                Workflows workflow=new Workflows(new armadillo_workflow());
//                boolean b=workflow.loadWorkflow(Config.currentPath+"//examples//"+filename);
//                workflow.setName(filename);
//                workflow.setDate_created(Util.returnCurrentDateAndTime());
//                workflow.setNote("Loaded on "+Util.returnCurrentDateAndTime());
//                System.out.println("Loading "+filename+" "+b);
//                workflow.saveToDatabase();
//            }
//        }
        boolean done = false;
        Scanner sc = new Scanner(System.in);
        while (!done) {
            Workflows w = new Workflows();
            w.hasNext();
            for (int id : w.getAllId()) {
                System.out.println("[" + id + "] " + w.getNameId(id));
            }
            System.out.println("0.  Exit");
            String stri = sc.next();
            //Try to have a number
            int choice = 255;
            try {
                choice = Integer.valueOf(stri);
            } catch (Exception e) {
                choice = 255;
            }
            switch (choice) {
                case 0:
                    System.exit(0);
                    break;
                default:
                    break;
            }
            if (choice != 255) {


                Workflows workflow = new Workflows(new armadillo_workflow());
                workflow.loadWorkflow("Wongetal2008.txt");
                workflow.run();



//                Workflows workflow=new Workflows(new armadillo_workflow());
//                workflow.loadFromDatabase(choice);
//                workflow.StringToWorkflow();
//                workflow.run();
                //program.programs torun=new program.programs(run);
                //torun.Run();
            }
        }
    }

    /**
     * This is a test to see if the sequences and tree are correct for ancestor
     */
    public static void testSequenceAndTree_for_AncestorsWebsite() {
        MultipleSequences m=new MultipleSequences("sequences2.fasta");
        newick_tree n=new newick_tree();
        File f=new File("tree2");
        n.parseNewick(f);
        System.out.println("Total taxon: "+n.getTotalLeaf());
        System.out.println("Total sequences: "+m.getNbSequence()+" size ("+m.getSize()+")");
        HashMap<String, Integer> tree_node=new HashMap<String, Integer>();
        HashMap<String, Integer> sequence_node=new HashMap<String, Integer>();
        for (node nod:n.node_list) {
            if (nod.isleaf) {
                String name=nod.getName().trim();
                if (tree_node.containsKey(name)) {
                Integer i=tree_node.get(name);
                tree_node.put(name, i);
            } else {
                tree_node.put(name, 1);
            }
            }
        }
        for (Sequence seq:m.getSequences()) {
            String name=seq.getName().trim();
            if (sequence_node.containsKey(name)) {
                Integer i=sequence_node.get(name);
                sequence_node.put(name, i);
            } else {
                sequence_node.put(name, 1);
            }
        }
        
        //--Statistique
        for (String nods:tree_node.keySet()) {
            if (!sequence_node.containsKey(nods)) {
                System.out.println("Warning. Sequence with name "+nods+" not found in sequence file, but found in tree file");
            } 
        }
         for (String nods:sequence_node.keySet()) {
            if (!tree_node.containsKey(nods)) {
                System.out.println("Warning. Tree leaf with name "+nods+" not found in tree file, but found in sequence file.");
            } 
        }
    }
    
    public static void testGFF() {

        Sequence s = new Sequence();
        s.setSequence("gactgtcactcggtcccagacaccagagcaagctcaagacccagcagtgggacagccagacagacggcacgatggcactgagctcccagatctgggccgcttgcctcctgctcctcctcc");
        HashMap<String, Integer> hash = new HashMap<String, Integer>();
        for (int i = 0; i < s.getLen(); i += 2) {
            String cat = s.getSequence().substring(i, i + 2);

            Integer j = hash.get(cat);
            if (j == null) {
                hash.put(cat, 1);
            } else {
                hash.put(cat, j + 1);
            }
        }
        for (String s2 : hash.keySet()) {
            System.out.println(s2 + " " + hash.get(s2));
        }

        //--Note need biojava3
//    try {
//       FeatureList f=GFF3Reader.read(config.currentPath+File.separator+"examples"+File.separator+"gff_sample.txt");
//       for (FeatureI feature:f) {
//           System.out.println(feature.type());
//       }
//        System.out.println(f.size());
//        //GFFTools.readGFF(new BufferedReader(new FileReader(new File(config.currentPath+File.separator+"examples"+File.separator+"gff_sample.txt"))), GFFRecordFilter.AcceptAll );
//
//
//        } catch(Exception e){e.printStackTrace();}
    }

    //--We run a workflow with argument...
    //  Possible cases:
    //  armadillo.jar workflow.db workflow_id [input1] [input2] ...
    //  armadillo.jar workflow.txt [input1] [input2] ...
    public static void Run() {
        Workflows workflow = new Workflows();
        System.out.println("Armadillo (Text Mode Command-Line) : " + Util.toString(Config.cmdArgs));
        //--Database or workflow?
        //--CASE 1. Database
        if (Config.cmdArgs[0].toLowerCase().endsWith(".txt")) {
            System.out.println("Using " + Config.cmdArgs[0] + ".db");
            df.Open(Config.cmdArgs[0] + ".db");
            if (!df.isDatabaseConstainsTables()) {
                Config.log("Default database not created. Creating tables...");
                boolean done = df.createTables();
                if (!done) {
                    Config.log("Problem in creating database...");
                    Config.log("Exiting...");
                    System.exit(-1);
                }
            }
            
            workflow.loadWorkflow(Config.cmdArgs[0]);
            //--Remove args
            Config.cmdArgs[0] = "";
            //--Run             
            if (workflow.run()) {
                System.exit(0);
            }
            //--CASE 2. Workflow
        } else {
            //--Open database
            if (!df.Open(Config.cmdArgs[0])) {
                System.out.println("Unable to connect to database " + Config.cmdArgs[0]);
                System.exit(-1);
            }
            Config.cmdArgs[0] = "";
            //--Load workflow number
            try {
                Integer number = Integer.valueOf(Config.cmdArgs[1]);
                Config.cmdArgs[1] = "";

                workflow.loadFromDatabase(number);
            } catch (Exception e) {
                System.out.println("Unable to load workflow number...");
                System.exit(-1);
            }
            //--Execute with args....
            if (workflow.run()) {
                System.exit(0);
            }
        }
    }
    
    public static void list_data_images_file() {
        ArrayList<String>files=Util.recursive_list_dir("data/images");
        for (String f:files) {
            if (f.endsWith(".png")) {
                File fi=new File(f);
                System.out.println(fi.getName());
            }
        }
    }
    
    public static void testXML() {
        workflow_properties w=new workflow_properties("data/properties/HGT32.properties");
        w.SaveAsXML("test.txt");
    }
    
	public static void Luis_createGroup1() {
        //-- Load the big sequence files
        MultipleSequences m=new MultipleSequences("combined_trimmed.QC.fasta");

        HashMap<String,MultipleSequences> data=new HashMap<String,MultipleSequences>();
        //--Iterate over eawch sequences
        int count=0;
        for (Sequence s:m.getSequences()) {
            //--Extract tag...
            String tag=s.getSequence().substring(0, 12).toUpperCase();            
            if (data.containsKey(tag)) {
                //--Add and replace
                MultipleSequences tmp=data.get(tag);
                tmp.add(s);
                data.put(tag, tmp);
            } else {
                //--Create new
                MultipleSequences tmp=new MultipleSequences();
                tmp.setName(tag);
                tmp.add(s);
                data.put(tag, tmp);
            }
            count++;
            if (count%100==0) System.out.println(count);
        }
        //--Write to file
        for (String tag:data.keySet()) {
            MultipleSequences ms=data.get(tag);
            ms.outputFasta("C:\\"+tag+".fasta");
        }
        System.out.println("done");

    }
	
    public static void testTaverna() {
        //uk.org.taverna.scufl2.translator.t2flo
//        WorkflowBundleIO io = new WorkflowBundleIO();  
//        
//        try {
//            File t2File = new File(config.currentPath()+File.separator+"examples"+File.separator+"t2flow_sampleworkflow"+File.separator+"fetch_today_s_xkcd_comic_568671.t2flow");
//            File scuflFile = new File(config.currentPath()+File.separator+"test.scufl2.zip");
//            WorkflowBundle workflow=io.readBundle(t2File, null);
//            //if (workflow!=null) System.out.println(workflow);
//            //uk.org.taverna.scufl2.api.io.WorkflowBundleIO
//            workflow.getMainWorkflow().setName("test");
////            for (WorkflowBundleWriter s:io.getWriters()) {
////               for (String s2:s.getMediaTypes()) System.out.println(s2);
////            }
//            //System.out.println( workflow.getMainWorkflow().toString());
//            
//            //io.writeBundle(workflow, scuflFile, "text/vnd.taverna.scufl2.structure");
//            //System.out.println(workflow.getResources().getPackageMediaType());
//            //workflow.getResources().setPackageMediaType(UCFPackage.MIME_RDF);
//            //workflow.getResources().save(new File("test.txt"));
//            //uk.org.taverna.scufl2.api.io.structure.StructureReader.
//            //TEXT_VND_TAVERNA_SCUFL2_STRUCTURE
//            
//            //io.writeBundle(workflow, scuflFile, "application/vnd.taverna.t2flow+xml");
//            io.writeBundle(workflow, scuflFile, "application/vnd.taverna.scufl2.workflow-bundle");
//            
//            UCFPackage u=workflow.getResources();
//            //System.out.println(u.);
//            //--Get the XML output
//            for (String s:workflow.getResources().listAllResources().keySet()) System.out.println(s);
//            System.out.println(u.getResourceAsString("workflow/test.rdf")); 
//            
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//                
//        System.exit(0); 
////        try {
////                JAXBContext jc = JAXBContext.newInstance(WorkflowBundle.class );
////                Marshaller marshaller = jc.createMarshaller();
////                marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT,
////                                Boolean.TRUE );
////                marshaller.marshal( io, new FileOutputStream("foo.xml") );
////        } catch(Exception e2) {
////            e2.printStackTrace();
////        }
//        
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    /// CREATE A STATIC LIST OF PROPERTIES FOR THE WEBSITE
    /// Based on a tutorial: http://www.thecssninja.com/css/css-tree-menu
    public static void create_Armadillo2_propertiesList() {
        Util u=new Util("properties.html");
        //--Add properties
        u.println("<script type='text/javascript' src='jquery-1.8.2.js'></script>");
	u.println("<script type='text/javascript' src='jquery-ui-1.9.0.custom.js'></script>");
        u.println("<script>$(function(){$('.file').draggable({ revert:true,  proxy:'clone',  onStartDrag:function(){ $(this).draggable('proxy').css('z-index',10); },  onStopDrag:function(){ $(this).draggable('options').cursor='move';  }});});</script>");
        HashMap<String,ArrayList<String>>prop=new HashMap<String,ArrayList<String>>();  
        for (String filename : workflow_properties.loadPropertieslisting(config.propertiesPath())) {
            workflow_properties properties = new workflow_properties(config.propertiesPath() + File.separator + filename);
            if (prop.containsKey(properties.get("Type"))) {
                ArrayList<String>tmp=prop.get(properties.get("Type"));
                tmp.add(properties.getName()+"\t"+filename);
                prop.put(properties.get("Type"), tmp);
            } else {
                ArrayList<String>tmp=new ArrayList<String>();                
                tmp.add(properties.getName()+"\t"+filename);
                prop.put(properties.get("Type"), tmp);
            }            
           
          }
        //--Get the list of name in the good order
        ArrayList<String>list=new ArrayList<String>();
        list.addAll(prop.keySet());
       Collections.sort(list);
       u.println("<link rel='stylesheet' type='text/css' href='style2.css' >");
       u.println("<ol class='tree'>");
       for (String type:list) {
           ArrayList<String>tmp=prop.get(type);
           System.out.println(type);
           u.println("<li>");
           u.println("<label for='folder'>"+type+"</label><input type='checkbox' id='"+type+"'/>");
           u.println("<ol>");           
           Collections.sort(tmp);
            for (String s:tmp) {
                String[] cl=s.split("\t");
                u.println("<li class='file' id='"+cl[1]+"'>"+cl[0]+"</li>");
                System.out.println(s);
            }
            u.println("</ol>");
            u.println("</li>");
       }
       u.println("</ol>");
       u.close();
    }
    
    public static void test_loadMatrix() {
        Matrix m=new Matrix();
        m.loadFromFile("c:\\workflows\\matrix\\original_matrix1.txt");
        m.decodeMatrix();
    }
    
    //////////////////////////////////////////////////////////////////////////////
    /// Test web site   

    public static void get_website_test() {
        Browser b = new Browser();
        b.load("http://www.ncbi.nlm.nih.gov/pubmed/21748361");
        b.outputFile("test.html");
        b.load("http://www.springerlink.com/content/v12j6688308l734u/");
        b.outputFile("test2.html");

    }
    
    public static void testHXB2() {
        MultipleSequences m=new MultipleSequences();

    }
    
      public static void Luis_createGroup2() {
                   
        System.out.println("Luis_create_group()");
        
        //-- Load the big sequence files
        MultipleSequences mR1 =new MultipleSequences("examples\\1__R1_NA_66seq.fasta");
        MultipleSequences mR2 =new MultipleSequences("examples\\1__R2_NA_61seq.fasta");

		MultipleSequences mR1_in_mR2= new MultipleSequences();
		mR1_in_mR2.setName("mR1_in_mR2");		
		MultipleSequences mR2_in_mR1= new MultipleSequences();
		mR2_in_mR1.setName("mR2_in_mR1");
		MultipleSequences common_mR1_and_mR2= new MultipleSequences();
		common_mR1_and_mR2.setName("common_mR1_and_mR2");
                MultipleSequences uncommon_mR1_and_mR2= new MultipleSequences();
		uncommon_mR1_and_mR2.setName("uncommon_mR1_and_mR2");
                
        HashMap<String,Sequence> dataR1=new HashMap<String,Sequence>();
        HashMap<String,Sequence> dataR2=new HashMap<String,Sequence>();
        
             //Collection names=org.apache.commons.collections.CollectionUtils.intersection(dataR1.keySet(), dataR2.keySet());
             //System.out.println(names);
             
		//--Add each tags to HashMap
		for (Sequence s:mR1.getSequences()) dataR1.put(s.getName().substring(0,43).toUpperCase(), s);
		for (Sequence s:mR2.getSequences()) dataR2.put(s.getName().substring(0,43).toUpperCase(), s);
	
                	Collection names_intersect=org.apache.commons.collections.CollectionUtils.intersection(dataR1.keySet(), dataR2.keySet());
                        System.out.println("Intersect:");
                        System.out.println(names_intersect);
                        Collection names_disjunction=org.apache.commons.collections.CollectionUtils.disjunction(dataR1.keySet(), dataR2.keySet());
                        System.out.println("Disjunction:");
                        System.out.println(names_disjunction);
                        
		//--Compage the 2 set 
		for (Sequence s:mR1.getSequences()) {
                   
                    if (dataR2.containsKey(s.getName().substring(0,43))) mR1_in_mR2.add(s);
		}
		//--Compage the 2 set 
		for (Sequence s:mR2.getSequences()) {
			  if (dataR1.containsKey(s.getName().substring(0,43))) mR2_in_mR1.add(s);
		}
	
                //--Get the sequences where name is common to both set
                for (String name:dataR1.keySet()) {
			  if (dataR2.containsKey(name)) common_mR1_and_mR2.add(dataR1.get(name));
		}
                
                
		// Output	
		//mR1_in_mR2.outputFasta("c:\\1%_NA_61seq_mr1_in_mr2.fasta");
		//mR2_in_mR1.outputFasta("c:\\1%_NA_61seq_mr2_in_mr1.fasta");
		
		// Output stats
		//#Total "+this.getNbSequence()+" sequence(s) with len "+this.getSequenceSize()+"\n";
		System.out.println("Total mr1:"+mR1.getNbSequence());
		System.out.println("Total mr2:"+mR2.getNbSequence());
		System.out.println("Total mr1 in mr2:"+mR1_in_mR2.getNbSequence());
		System.out.println("Total mr2 in mr1:"+mR2_in_mR1.getNbSequence());
                System.out.println("Total common:"+common_mR1_and_mR2.getNbSequence());
       
            }
      
          public static void Luis_createGroup() {
        
            
        System.out.println("hello world");
        
        //-- Load the big sequence files
        MultipleSequences mR1 =new MultipleSequences("1%_R1_NA_66seq.fasta");
        MultipleSequences mR2 =new MultipleSequences("1%_R2_NA_61seq.fasta");

        HashMap<String,MultipleSequences> dataR1=new HashMap<String,MultipleSequences>();
        HashMap<String,MultipleSequences> dataR2=new HashMap<String,MultipleSequences>();
        
        int countR1=0;
        int countR2=0;
        
        //--Iterate over each sequences
        for (Sequence sR1:mR1.getSequences()) {
            //--Extract tag for R1
            String tagR1=sR1.getSequence().substring(0,43).toUpperCase();
            
            for (Sequence sR2:mR2.getSequences()) {
                //--Extract tag for R2
                String tagR2=sR2.getSequence().substring(0,43).toUpperCase(); 
               
               //compare if tagR1 = TagR2 and vice versa 
               if (dataR1.containsKey(tagR2));{
                                
                MultipleSequences finalR1 =new MultipleSequences();
                finalR1.add(sR1);
                dataR1.put(tagR1, finalR1);
                //write to new multisequences
                System.out.println(sR1);
                //finalR1.outputFasta("C:\\" +"finalR1.fasta");
                
               } 
               
               if (dataR2.containsKey(tagR1));{ 
               //write to new multisequences 
               MultipleSequences finalR2 =new MultipleSequences();
               finalR2.add(sR2);
               dataR2.put(tagR2, finalR2);
               //finalR2.outputFasta("C:\\" +"finalR2.fasta");
                System.out.println(sR2);
               }
               countR2++;
            }        
countR1++;       
}
System.out.println("done");
System.out.println(countR1+countR2);
}

    //////////////////////////////////////////////////////////////////////////////
    //// Main Run Demo Menu
    public static void RunDemo() {
        Config.library_mode=true;
        boolean done = false;
        Util.deleteFile(config.tmpDir() + "//demo.db");
        System.out.print("Creating demo database...");
        //--Connect to a etst database
        df.New(config.tmpDir() + "//demo.db");
        //--Load all the demo
        Classdata current_demo = new Classdata("demo.Demo");
        System.out.println("done.");

        Config.log("Armadillo Workflow Platform Demo Mode...(Internal Beta test)");

        Scanner sc = new Scanner(System.in);
        while (!done) {
            // Display option screen
            System.out.println("Armadillo Workflow Platform Demo Mode...(Internal Beta test)");
            System.out.println("Please choose a demo");

           HashMap<String, Integer> correspondence =new  HashMap<String, Integer>();
           ArrayList<String>correspondence_name=new  ArrayList<String>();
           
           
            for (int i = 0; i < current_demo.getNbMethods(); i++) {
                Method m = current_demo.method[i];
                String test_name = m.getName();                
                if (!test_name.equalsIgnoreCase("Main") && !test_name.equalsIgnoreCase("demo.Demo") && !test_name.equalsIgnoreCase("RunDemo")) {
                    correspondence.put(test_name, i);
                    correspondence_name.add(test_name);
                    //System.out.println((i + 1) + ". " + test_name);
                }
            }
            //--Order    
            Collections.sort(correspondence_name);
            for (int i=0; i<correspondence_name.size();i++) {
                System.out.println((i + 1) + ". " + correspondence_name.get(i));
            }
            
            System.out.println("0.  Exit");
            String stri = sc.next();
            //Try to have a number
            int choice = 255;
            try {
                choice = Integer.valueOf(stri);
            } catch (Exception e) {
                choice = 255;
            }
            switch (choice) {
                case 0:
                    done = true;
                    break;
                default:
                    try {
                        String name=correspondence_name.get(choice-1);
                        int pos=correspondence.get(name);
                        current_demo.method[pos].invoke(new Object[]{}, new Object[]{});
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error... Please try again.");
                    }
            }
        } //End While !done        
        Util.deleteFile(config.tmpDir() +File.separator+"demo.db");
        System.exit(0);
    } //End rundemo
}
