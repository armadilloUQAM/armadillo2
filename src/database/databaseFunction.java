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

package database;
import biologic.Alignment;
import biologic.InfoAlignment;
import biologic.InfoMultipleSequences;
import biologic.Sequence;
import biologic.InfoSequence;
import biologic.Input;
import biologic.Matrix;
import biologic.MultipleAlignments;
import biologic.MultipleSequences;
import biologic.MultipleTrees;
import biologic.Output;
import biologic.Phylip;
import biologic.Tree;
import biologic.Unknown;
import biologic.Workflows;
import configuration.Util;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import program.RunProgram;
import biologic.RunWorkflow;
import configuration.Config;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import workflows.workflow_properties;

/**
 * Hold some helper function to database access (we should use this class to
 * access the database)
 * @author Etienne Lord, Leclercq Mickael
 * @since June 2009
 * @version 2010
 */
public class databaseFunction {

    ////////////////////////////////////////////////////////////////////////////
    /// Variables

    Config config=new Config();                   //Configuration file    
    public static database db;                  //application specific database
    public SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static boolean debug = false;        //Deubg (set to true will send message to stdout)
    public static boolean initialized=false;    //Flag for initialisation of the class to have only one instance
    public static int counter=0;                //Number of the class initialized
    public static int retry=3;                  //Number of retry to write to database
    public static HashMap<String, Boolean>tables_found=new HashMap<String, Boolean>();
    
    ////////////////////////////////////////////////////////////////////////////
    /// Constructor

    /**
     * Main constructor
     */
    public databaseFunction() {
        //Don't do anything
       
        
        if (!isDebug()&&config.isDevelopperMode()) {
            setDebug(true);            
        }
        database.setDebug(config.getBoolean("LogDatabaseEvents"));
        if (isDebug()) {
            counter++;            
            //Config.log("number of initialized databaseFunction: "+counter);
        }
        if (!initialized) {
           initialized=true;
           if (Config.nodatabase) {
               //--Create in memory database
               db=new database();
           } else {
                db=new database(config.databasePath());
           }
           if (db==null) {
               Config.log("Unable to connect/create project file "+config.databasePath());
               Config.log("System halted...");
//               System.out.println("Warning. Not able to connect to "+config.databasePath());
//               System.out.println("Will exit. Please delete your config.dat file.");
               System.exit(-1);
           }
           if (!isDatabaseContainsAvailableTables()) {
               Config.log("Default database not created. Creating tables...") ;               
               boolean done=createTables();
               if (!done) {
                   Config.log("Problem in creating database...");
                   Config.log("Exiting...");
                   System.exit(-1);
               }
               ;
           }
        }        
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Open and SaveAs function

    /**
     * Main function to Open a database...
     * IMPORTANT NOTE: This doesn't check if the database is valid i.e. 
     * if the filename doesn't exists it will be created...
     * @param dbfilename
     * @return true if a new database is created, false otherwise
     */
    public boolean Open(String dbfilename) {
        try {            
            db.close();
            db.dbFileName=dbfilename;
            db.connect();
            isDatabaseContainsAvailableTables();
            return true;
        } catch(Exception e) {
            if (debug) Config.log("Error DB : "+e.getMessage());
            return false;}
    }

    /**
     * Main function to Open a database...
     * IMPORTANT NOTE: This doesn't check if the database is valid i.e. 
     * if the filename doesn't exists it will be created...
     * @param dbfilename
     * @return true if a new database is created, false otherwise
     */
    public boolean Close() {
        try {            
            db.close();                     
            return true;
        } catch(Exception e) {
            if (debug) Config.log("Error DB : "+e.getMessage());
            return false;}
    }
    
    
    /**
     * Note: We must rename the workflow to reflect the change...
     * @param dbfilename
     * @return
     */
    public boolean SaveAs(String dbfilename) {
        try {
            db.close();
            Util.copy(db.dbFileName, dbfilename);
            db.dbFileName=dbfilename;
            db.connect();            
            return true;
        } catch(Exception e) {
            if (debug) Config.log("Error DB : "+e.getMessage());
            return false;}
    }

     /**
     * Note: WE must rename the workflow to reflect the change...
     * @param dbfilename
     * @return
     */
    public boolean New(String dbfilename) {
        try {
            //--Save the current project information...
            Project project;
            int projectid=this.getNextProjectID()-1;
             if (projectid>0) {
                    project=new Project(projectid);
            } else {
                project=new Project();
            }
            //--Close the current database
            db.close();
            //--Open the new database file
            db.dbFileName=dbfilename;
            db.connect();
            tables_found.clear();
            //--Create the new table (SQL statement)           
            //--We already have the table?
            boolean TableCreated=isDatabaseContainsAvailableTables();
            //--Otherwise, create...
            if (!TableCreated) TableCreated=createTables();
            //--Copy the project informations
            if (TableCreated) {
                //if (project.getId()==0) project.setId(this.getNextProjectID());
                project.saveToDatabase();
            }
            return TableCreated;
        } catch(Exception e) {
            Config.log("Error DB : "+e.getMessage());
            return false;}
    }

      /**
     * Note: WE must rename the workflow to reflect the change...
     * @param dbfilename
     * @param sqlfilename
     * @return
     */
    public boolean New(String dbfilename, String sqlfilename) {
        try {
            //--Save the current project information...
            Project project;
            int projectid=this.getNextProjectID()-1;
             if (projectid>0) {
                    project=new Project(projectid);
            } else {
                project=new Project();
            }
            //--Close the current database
            db.close();
            //--Open the new database file
            db.dbFileName=dbfilename;
            tables_found.clear();
            db.connect();
            //--Create the new table (SQL statement)
             boolean TableCreated=isDatabaseContainsAvailableTables();
            //--Otherwise, create...
            if (!TableCreated) TableCreated=createTables(sqlfilename);           
            //--Copy the project informations
            if (TableCreated) {
                //if (project.getId()==0) project.setId(this.getNextProjectID());
                project.saveToDatabase();
            }
            return TableCreated;
        } catch(Exception e) {
            Config.log("Error DB : "+e.getMessage());
            return false;}
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// Encoding / Decoding String

      public String encode (String string){
        //String S="";
        //for (Character c:string.toCharArray()) {
        //    String value = remplacementIso.get(c);
        //    S+=(value==null?c:value);
        //}
          if (string==null||string.isEmpty()) return "";
        try {
              return URLEncoder.encode(string, "UTF-8");
        } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return "";}

    }

     public String decode (String string){
         if (string==null||string.isEmpty()) return "";
         try {
                return URLDecoder.decode(string, "UTF-8");
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return "";}
//         String S="";
//        char[] str=string.toCharArray();
//        int len=str.length;
//        int index=0;
//        while(index<len) {
//            char c=str[index++];
//            if (c=='&'&&str[index]=='#') {
//               int nextindex=string.indexOf('&', index)+1;
//               String code="&"+string.substring(index, nextindex);
//               index=nextindex;
//               S+=reverseRemplacementIso.get(code);
//            } else {
//              S+=c;
//            }
//        }
//        return S;
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Create or Delete database tables

    /**
     * Get present time from SQLITE in the format : "yyyy-MM-dd HH:mm:ss"
     * @return a date, in String
     */
    @Deprecated
    public String getTimeNowFromSQLITE (){
        return Util.returnCurrentDateAndTime();
    }

    /**
     * Main function to create the database table
     * This required function retuired the file armadillo.sql in the dataPath
     */
    public boolean createTables() {
        //--DELETE ALL TABLES IN THE DATABASE IF ANY (TO DO: WE should ask first...)
        // -- NOT NEEDED deleteAllTables();
        //-- Load the creation statement       
        Vector<String> creation_statement=new Vector<String>();
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(database.class.getResourceAsStream("armadillo.sql")));
            String buffer=""; //Line buffer till we read ';'
            String stri="";   //current line
            while(br.ready()) {
                stri=br.readLine().trim();
                if (!stri.startsWith("--")&&stri.length()!=0) { //Ignore SQL comment --
                    buffer+=stri;
                    if (buffer.indexOf(';')==buffer.length()-1) { // Concatenate statement
                        creation_statement.add(buffer);
                        buffer="";
                    }
                }
            }
            br.close();
        } catch(Exception e) {
            Config.log("Error DB : "+e.getMessage());
            if (isDebug()) {
                Config.log("Unable to load creation statements.");
            }
            return false;
        }
        // --Create the tables
        db.setAutoCommit(false);
        for (String statement:creation_statement) {
               db.execute(statement);
        }
        db.setAutoCommit(true);
       //Config.log("done.");       
       return true;
    }

    /**
     * Main function to create the database table
     * @param filename : sql file with create tables
     * @return
     */
    public boolean createTables(String filename) {
        //--DELETE ALL TABLES IN THE DATABASE IF ANY (TO DO: WE should ask first...)
        // -- NOT NEEDED deleteAllTables();
        //-- Load the creation statement
        Vector<String> creation_statement=new Vector<String>();
        try {
            BufferedReader br=new BufferedReader(new FileReader(filename));
            String buffer=""; //Line buffer till we read ';'
            String stri="";   //current line
            while(br.ready()) {
                stri=br.readLine().trim();
                if (!stri.startsWith("--")&&stri.length()!=0) { //Ignore SQL comment --
                    buffer+=stri;
                    if (buffer.indexOf(';')==buffer.length()-1) { // Concatenate statement
                        creation_statement.add(buffer);
                        buffer="";
                    }
                }
            }
            br.close();
        } catch(Exception e) {
            Config.log("Error DB : "+e.getMessage());
            if (isDebug()) {
                Config.log("Unable to load creation statements.");
            }
            return false;
        }
        // --Create the tables
        db.setAutoCommit(false);
        for (String statement:creation_statement) {
               db.execute(statement);
        }
        db.setAutoCommit(true);
       Config.log("done.");
       return true;
    }

    /**
     * Delete All tables found in the database
     * @return true if all tables have been deleted from database
     */
    public boolean deleteAllTables() {
        Config.log("Deleting all database "+db.dbFileName+" tables.");
        Vector<String> tables_to_delete=new Vector<String>();
        String query="SELECT name FROM sqlite_master WHERE type = 'table';";
        try {
            ResultSet rs=db.executeQuery(query);
            while(rs.next()) {
                tables_to_delete.add(rs.getString(1));
            }
            rs.close();
            if (tables_to_delete.size()>0) {
                for(String table:tables_to_delete) {
                    Config.log("Deleting "+table);
                    String delete_query=String.format("DROP TABLE %s;", table);
                    db.execute(delete_query);
                    Config.log(" done.");
                }               
            }
        } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return false;}
         Config.log("Done deleteAllTables");
        return true;
    }

    /**
     * Check if database contains tables
     * @return True if tables exist, False otherwise
     */
    @Deprecated
    public boolean isDatabaseConstainsTables() {
        String query="SELECT name FROM sqlite_master WHERE type = 'table';";
        boolean containTables=false;
        try {
            ResultSet rs=db.executeQuery(query);
            if (rs.next()) containTables=true;
            rs.close();
        } catch(Exception e) {return false;}
        return containTables;
     }

   ///////////////////////////////////////////////////////////////////////////
   /// VERIFICATION

    /**
     * Internal table verification - Check if database contains tables
     * Note: duplicate of isDatabaseContainsTables()
     * @return True if the current database contain some tables, false otherwise;
     */  
  public boolean isDatabaseContainsAvailableTables() {
       boolean containTables=false;
      if (tables_found.size()>0) return true;
       String query="SELECT name FROM sqlite_master WHERE type = 'table';";       
        try {
            ResultSet rs=db.executeQuery(query);
            while (rs.next()) {
                String name=rs.getString(1); 
                tables_found.put(name, true);
                containTables=true;
            }
            rs.close();
        } catch(Exception e) {return false;}      
      return containTables;
  }
     
    ////////////////////////////////////////////////////////////////////////////

     public Vector<Integer> getAllUnknownID() {
         Vector<Integer>tmp=new Vector<Integer>();
          if (!tables_found.containsKey("Unknown")) return tmp;
         try {
            String query = "SELECT Unknown_Id FROM Unknown;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int id=rs.getInt(1);
                  tmp.add(id);
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }

       public boolean existsUnknown(int id) {
       try {
            String query = String.format("SELECT Unknown_id FROM Unknown WHERE Unknown_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                rs.close();
                return true;
            }
         } catch(Exception e) {return false;}
       return false;
    }

      public int getNextUnknownID() {
         //--Get the highest MultipleSequences_id
        int Unknown_id=0;
        while(Unknown_id==0) {
            try {
                String query="SELECT MAX(Unknown_id) FROM Unknown;";
                ResultSet rs=db.executeQuery(query);
                if (rs==null) {
                    System.err.println("ERROR: Unable to get next Unknown id");
                    return 0;
                }   
                if (rs.next()) {
                    Unknown_id=rs.getInt(1);
                    Unknown_id++; //Increase id by 1 to get the next one
                }
                rs.close();
            } catch(Exception e) {
            e.printStackTrace();}

        }
        return Unknown_id;
    }

     /**
     * Add a parameter in the Params table. Parameters are represented by command lines, as Strings.
     * If a parameter already exist, entry won't be added.
     *
     * @param param : parameters command line
     * @param note : notes
     */
    public int addUnknown (Unknown unknown){
       int id=0;
        if (unknown.getId()==0) {
            id=this.getNextUnknownID();
        } else {
            id=unknown.getId();
        }
        try {
             String i = String.format("INSERT INTO Unknown (Unknown_id, Unknown, Name, filename, Note, UnknownType, runprogram_id) VALUES('%d','%s','%s','%s','%s','%s','%d');",id, encode(unknown.getUnknown()), encode(unknown.getName()), encode(unknown.getFilename()),encode(unknown.getNote()), unknown.getUnknownType(), unknown.getRunProgram_id());
             db.execute(i);
             unknown.setId(id);
        } catch (Exception e) {
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            Config.log("Unable to add the Unknown "+unknown);
            return 0;
        }
        return id;
    }


    public String getUnknownName(int id) {
       try {
            String query = String.format("SELECT name FROM Unknown WHERE Unknown_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {                  
                String name=decode(rs.getString(1));
                rs.close();
                return name;
            }
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return "";}
       return "";
    }
    
    public String getUnknownFileName(int id) {
       try {
            String query = String.format("SELECT filename FROM Unknown WHERE Unknown_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {                  
                String name=decode(rs.getString(1));
                rs.close();
                return name;
            }
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return "";}
       return "";
    }
    
     public Unknown getUnknown(int unknown_id) {
         Unknown unknown=new Unknown();
         try {
            String query = String.format("SELECT unknown_id, unknown, name, filename,note, unknowntype, runProgram_id FROM Unknown WHERE unknown_id='%d';", unknown_id);
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                unknown.setId(rs.getInt(1));
                StringBuilder st=new StringBuilder(0);
                st.append(decode(rs.getString(2)));
                unknown.setUnknown(st);
                unknown.setName(decode(rs.getString(3)));
                unknown.setFilename(decode(rs.getString(4)));
                unknown.setNote(decode(rs.getString(5)));
                unknown.setUnknownType(rs.getString(6));
                unknown.setRunProgram_id(rs.getInt(7));
            }
            rs.close();
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());}
        return unknown;
    }
         
     
    public Vector<Integer> getUnknownFromRunProgramID(int runprogram_id) {
        Vector<Integer>tmp=new Vector<Integer>();
        
         try {
            String query = String.format("SELECT unknown_id FROM Unknown WHERE runprogram_id='%d';", runprogram_id);
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  tmp.add(rs.getInt(1));
            }
            rs.close();
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());}
        return tmp;
    }

     /**
     * Remove a phylip data from Phylip table by its id
     * @param phylipId
     */
    public boolean  removeUnknown (Unknown unknown){
        if (unknown.getId()==0) return false;
        try {
              //--Remove from Sequence
              String rm = String.format("DELETE FROM Unknown WHERE unknown_id='%d';", unknown.getId());
              db.execute(rm);
        } catch (Exception e) {
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            return false;
        }
        return false;
    }

    public boolean updateUnknown(Unknown unknown) {
        removeUnknown(unknown);
        return (addUnknown(unknown)==0?false:true);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// Note: we should remove this class for Phylip

     public boolean existsPhylip(int id) {
       try {
            String query = String.format("SELECT Phylip_id FROM Phylip WHERE Phylip_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                rs.close();
                return true;
            }
         } catch(Exception e) {return false;}
       return false;
    }

    public Vector<Integer> getAllPhylipID() {
         Vector<Integer>tmp=new Vector<Integer>();
          if (!tables_found.containsKey("Phylip")) return tmp;
         try {
            String query = "SELECT Phylip_id FROM Phylip;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int id=rs.getInt(1);
                  tmp.add(id);
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }


    /**
     * Get the next free Phylip ID
     * @return the next viable Phylip ID
     */
    public int getNextPhylipID() {
       //--Get the highest properties_id
        int id=0;
        while(id==0) {
            try {
                String query="SELECT MAX(Phylip_id) FROM Phylip;";
                ResultSet rs=db.executeQuery(query);
                if (rs==null) {
                    System.err.println("ERROR: Unable to get next Phylip id");
                    return 0;
                }                   
                if (rs.next()) {
                    id=rs.getInt(1);
                    id++; //Increase id by 1 to get the next
                }
                rs.close();
            } catch(Exception e) {}
        }
    return id;
    }

    public String getPhylipName(int id) {
       try {
            String query = String.format("SELECT name FROM Phylip WHERE Phylip_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                  String name=decode(rs.getString(1));
                  rs.close();
                  return name;
            }
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return "";}
       return "";
    }

    public int addPhylip (Phylip phylip){
     int id=0;
        if (phylip.getId()==0) {
            id=this.getNextPhylipID();
        } else {
            id=phylip.getId();
        }
        //int id=(phylip.getId()==0?this.getNextPhylipID():phylip.getId());
        try {
             String i = String.format("INSERT INTO Phylip (Phylip_id, Phylip_data, Phylip_datatype, Note, Name, runprogram_id) VALUES('%d','%s','%s','%s','%s','%d');",id, encode(phylip.getPhylip_data()), encode(phylip.getPhylip_datatype()), encode(phylip.getNote()), encode(phylip.getName()),phylip.getRunProgram_id());
             db.execute(i);
             phylip.setId(id);
        } catch (Exception e) {
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            Config.log("Unable to add the Phylip "+phylip);
            return 0;
        }
        return id;
    }

    public Phylip getPhylip(int phylip_id) {
        Phylip tmp=new Phylip();
         try {
            String query = String.format("SELECT phylip_data, phylip_datatype, name, note, runprogram_id FROM Phylip WHERE phylip_id='%d';", phylip_id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                tmp.setPhylip_data(decode(rs.getString(1)));
                tmp.setPhylip_datatype(decode(rs.getString(2)));
                tmp.setName(decode(rs.getString(3)));
                tmp.setNote(decode(rs.getString(4)));
                tmp.setRunProgram_id(rs.getInt(5));
                tmp.setId(phylip_id);
            }
            rs.close();
        } catch (Exception e) {
            Config.log("Error DB : "+e.getMessage());
         }        
        return tmp;
    }

    /**
     * This update a Phylip record
     * @param phylip
     * @return
     */
    public boolean updatePhylip(Phylip phylip) {
        if (phylip.getId()==0) return false;
        removePhylip(phylip);
        return (addPhylip(phylip)==0?false:true);
    }
    
     /**
     * Remove a phylip data from Phylip table by its id
     * @param phylipId
     */
    public boolean  removePhylip (Phylip phylip){
        if (phylip.getId()==0) return false;
        try {
              //--Remove from Sequence
              String rm = String.format("DELETE FROM Phylip WHERE phylip_id='%d';", phylip.getId());
              db.execute(rm);
        } catch (Exception e) {
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            return false;
        }
        return true;
    }

    
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Get the next free Matrix ID
     * @return the next viable Matrix ID
     */
    public int getNextMatrixID() {
       //--Get the highest properties_id
        int id=0;
        while(id==0) {
        try {
            String query="SELECT MAX(Matrix_id) FROM Matrix;";
            ResultSet rs=db.executeQuery(query);
             if (rs==null) {
                    System.err.println("ERROR: Unable to get next Matrix id");
                    return 0;
                }      
            if (rs.next()) {
                id=rs.getInt(1);
                id++; //Increase id by 1 to get the next
            }
            rs.close();
        } catch(Exception e) {}
        }
    return id;
    }

     public boolean existsMatrix(int id) {
       try {
            String query = String.format("SELECT Matrix_id FROM Matrix WHERE Matrix_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                rs.close();
                return true;
            }
         } catch(Exception e) {return false;}
       return false;
    }


      public Vector<Integer> getAllMatrixID() {
         Vector<Integer>tmp=new Vector<Integer>();
          if (!tables_found.containsKey("Matrix")) return tmp;
         try {
            String query = "SELECT Matrix_id FROM Matrix;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int id=rs.getInt(1);
                  tmp.add(id);
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }

     public String getMatrixName(int id) {
       try {
            String query = String.format("SELECT name FROM Matrix WHERE Matrix_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                  String name= decode(rs.getString(1));
                  rs.close();
                  return name;
            }
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return "";}
       return "";
    }

    /**
     *  Add a Matrix to the database
     * @param matrix
     * @return new matrix id
     */
     public int addMatrix (Matrix matrix){
        //int id=(matrix.getId()==0?this.getNextMatrixID():matrix.getId());
        int id=0;
        if (matrix.getId()==0) {
            id=this.getNextMatrixID();
        } else {
            id=matrix.getId();
        }
        try {
             String i = String.format("INSERT INTO Matrix (Matrix_id, matrix, Name,Note, runprogram_id) VALUES('%d','%s','%s','%s','%d');",
                                       id, encode(matrix.getMatrix()), encode(matrix.getName()), encode(matrix.getNote()), matrix.getRunProgram_id());
             db.execute(i);
             matrix.setId(id);
        } catch (Exception e) {
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            Config.log("Unable to add the Matrix "+matrix);
            return 0;
        }
        return id;
    }

    /**
     * Get a matrix from the database
     * @param matrix_id
     * @return
     */
    public Matrix getMatrix(int matrix_id) {
        Matrix tmp=new Matrix();
         try {
            String query = String.format("SELECT matrix, name, note, runprogram_id FROM Matrix WHERE matrix_id='%d';", matrix_id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                tmp.setMatrix(decode(rs.getString(1)));
                tmp.setName(decode(rs.getString(2)));
                tmp.setNote(decode(rs.getString(3)));
                tmp.setRunProgram_id(rs.getInt(4));
                tmp.setId(matrix_id);
                rs.close();
            }
            
        } catch (Exception e) {
            Config.log("Error DB : "+e.getMessage());
         }        
        return tmp;
    }

    public boolean updateMatrix(Matrix matrix) {
        if (matrix.getId()==0) return false;
        removeMatrix(matrix);
        return (addMatrix(matrix)==0?false:true);
    }
    
     /**
     * Remove a matrix data from Matrix table by its id
     * @param matrixId
     */
    public boolean  removeMatrix (Matrix matrix){
        if (matrix.getId()==0) return false;
        try {
              //--Remove from Sequence
              String rm = String.format("DELETE FROM Matrix WHERE matrix_id='%d';",matrix.getId());
              db.execute(rm);
              return true;
        } catch (Exception e) {
            if (isDebug()) {
                Config.log("Error DB : "+e.getMessage());
            }
            return false;
        }
       
    }
    
////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the next free RunProgram ID
     * @return the next viable RunProgram ID
     */
    public int getNextRunProgramID() {
       //--Get the highest properties_id
        int id=0;
        while(id==0) {
            try {
                String query="SELECT MAX(RunProgram_id) FROM RunPrograms;";
                ResultSet rs=db.executeQuery(query);                
                if (rs==null) {
                    System.err.println("ERROR: Unable to get next Runprogram id");
                    return 0;
                }      
                if (rs.next()) {
                    id=rs.getInt(1);
                    id++; //Increase id by 1 to get the next
                    rs.close();
                }
            } catch(Exception e) {}
        }
    return id;
    }


    public int addRunProgram(RunProgram run) {       
        //--Test for the update RunProgram...
        //int id=(run.getId()==0?this.getNextRunProgramID():run.getId());
        int id=0;
        if (run.getId()==0) {
            id=this.getNextRunProgramID();
        } else {
            id=run.getId();
        }
        try {
            String i = String.format("INSERT INTO RunPrograms (Runprogram_id, properties_id, workflows_id, note, programTimeStart, programTimeEnd, runProgramOutput) VALUES ('%d','%d','%d','%s','%s','%s','%s');",
                                     id, run.getProperties_id(), run.getWorkflows_id(), encode(run.getNote()), run.getProgramTimeStart(),run.getProgramTimeEnd(), encode(run.getRunProgramOutput()));
            db.execute(i);
            run.setId(id);
            //--Save Input
            for (Input in:run.getInput()) {
                in.setRunProgram_id(id);
                addInput(in);
            }
            
            //--Save Output
             for (Output out:run.getOutput()) {
                out.setRunProgram_id(id);
                addOutput(out);
            }
           
            return id;

        } catch (Exception e) {
             if (isDebug()) Config.log("Error DB : "+e.getMessage());
             return 0;
        }
    }

    public boolean updateRunProgram(RunProgram run) {               
        removeRunProgram(run);
        //stem.out.println(this.getNextRunProgramID());
        return (addRunProgram(run)==0?false:true);
    }

     /**
     * Remove all entries of a programID in the RunProgram table
     * @param ProgramId
     */
    public void removeRunProgram (RunProgram run){
        try {
            String rm = String.format("DELETE FROM RunPrograms WHERE runProgram_id='%d';",run.getId());
            db.execute(rm);
        } catch (Exception e) {
           Config.log("Error DB : "+e.getMessage());
        }
    }

    /**
     * Get informations about a RunProgramID.
     * @param id
     * @return Results of all colums will be display in this form :
     */
    public RunProgram getRunProgram (Integer RunProgramId){
        RunProgram run=new RunProgram();
        try {
            String query = String.format("SELECT Runprogram_id, properties_id, workflows_id, note, programTimeStart, programTimeEnd, runProgramOutput FROM RunPrograms WHERE RunProgram_id='%d';",RunProgramId);
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                run.setId(rs.getInt(1));
                run.setProperties_id(rs.getInt(2));
                run.setWorkflows_id((rs.getInt(3)));
                run.setNote(decode(rs.getString(4)));
                run.setProgramTimeStart(rs.getString(5));
                run.setProgramTimeEnd(rs.getString(6));
                run.setRunProgramOutput(decode(rs.getString(7)));
            }
            rs.close();
            //--LoadInput
            for (Input in:getInput(run)) run.addInput(in);
            //--LoadOutput
            for (Output out:getOutput(run)) run.addOutput(out);
            
        } catch (Exception e) {
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
        }
        return run;
    }

    public Vector<Integer> getRunProgramsIds (Workflows work){
        //--Get the highest Sequence_id
       Vector<Integer>v=new Vector<Integer>();
        try {
            String query="SELECT runProgram_id FROM runPrograms where workflows_id='"+work.getId()+"';";
            ResultSet rs=db.executeQuery(query);
            while (rs.next()) {
                v.add(rs.getInt(1));
            }
            rs.close();
        } catch(Exception e) {
            Config.log("Error DB : "+e.getMessage());
        }
         return v;
    }


    ////////////////////////////////////////////////////////////////////////////

    public boolean updateProperties(workflow_properties properties) {

        try {
           //-- 1. DElete previous entries
           String s=String.format("DELETE FROM PropertiesValues WHERE properties_id='%d';",properties.getProperties_id());
           db.execute(s);
           //-- 2. Reenter the key, values
           Enumeration<Object> e=properties.keys();
            while(e.hasMoreElements()) {
                  String key=(String)e.nextElement();

                 String ins=String.format("INSERT INTO PropertiesValues(properties_id, Key_name, Key_value) VALUES ('%s','%s','%s');",properties.getProperties_id(), key, properties.get(key));
                 db.execute(ins);
             }
        } catch(Exception e) {
            if (isDebug()) {
                Config.log("Error in updating properties " + properties.getName());
            }
            return false;
        }

        return true;
    }

    
    
    ////////////////////////////////////////////////////////////////////////////

     /**
     * Get the next free Sequence_id
     * @return the next viable Sequence_id
     */
    public int getNextSequenceID() {
       //--Get the highest Sequence_id
        int Sequence_id=0;
         while(Sequence_id==0) {
        try {
                String query="SELECT MAX(Sequence_id) FROM Sequence;";
                ResultSet rs=db.executeQuery(query);
                if (rs==null) {
                    System.err.println("ERROR: Unable to get next Sequence id");
                    return 0;
                }      
                if (rs.next()) {
                    Sequence_id=rs.getInt(1);
                    Sequence_id++; //Increase id by 1 to get the next one
                }
                rs.close();
            } catch(Exception e) {}
         }
         return Sequence_id;
    }

     public Vector<Integer> getAllSequenceID() {
         Vector<Integer>tmp=new Vector<Integer>();
          if (!tables_found.containsKey("Sequence")) return tmp;
         try {
            String query = "SELECT sequence_id FROM Sequence;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int id=rs.getInt(1);
                  tmp.add(id);
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }

   public String getSequenceName(int id) {
       try {
            String query = String.format("SELECT name FROM Sequence WHERE sequence_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                  String name=decode(rs.getString(1));
                  rs.close();
                  return name;
            }
         } catch(Exception e) {if (debug) Config.log("Error DB : "+e.getMessage());return "";}
       return "";
    }

    public Integer addSequence (Sequence s) {

        //--Test for good sequence
        if (s.getSequence().isEmpty()) return 0;
        if (s.getName().isEmpty()) return 0;
            
        int id=0;
        if (s.getId()==0) {
            id=this.getNextSequenceID();
        } else {
            id=s.getId();
        }
        //--Test
        //if (s.exists(id)) id=this.getNextSequenceID();
        // --Prepare the sequence for the database
        String name = encode(s.getName());
        String accession = s.getAccession().toUpperCase();
        String accessionReferee = s.getAccession_referee();
        String gi=s.getGi();
        String sequence = s.getSequence().toUpperCase();
        String note = encode(s.getNote());
        String abbreviate=s.getAbbreviate();
        String quality=encode(s.getQuality());

        try {
            //--First add stats
            //addSequenceStats(id, sequence); Not anymore
            //--Add sequence in database
            StringBuilder stri=new StringBuilder();
            stri.append("INSERT INTO Sequence(sequence_id, name, accession,accessionReferee, gi,sequence,note, abbreviate, sequence_len, sequence_type, timeAdded, quality, runProgram_id) VALUES (");
            stri.append("'").append(id).append("',");
            stri.append("'").append(name).append("',");
            stri.append("'").append(accession).append("',");
            stri.append("'").append(accessionReferee).append("',");
            stri.append("'").append(gi).append("',");
            stri.append("'").append(sequence).append("',");
            stri.append("'").append(note).append("',");
            stri.append("'").append(abbreviate).append("',");
            stri.append("'").append(s.getLen()).append("',");
            stri.append("'").append(s.getSequence_type()).append("',");
            stri.append("'"+Util.returnCurrentDateAndTime()+"',");
            stri.append("'"+quality+"',");
            stri.append("'"+s.getRunProgram_id()+"'");
            stri.append(");");
            db.execute(stri.toString());
            //--Create a genbank entry just in case
            //int genbankfile_id=getNextGenbankFileID();
            //String i=String.format("INSERT INTO GenbankFile(genbankfile_id,sequence_id) VALUES ('%d','%d');",genbankfile_id,id);
            //db.execute(i);
            s.setId(id);
            return id;
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer addSequenceMik (Sequence s) {       
        // --Prepare the sequence for the database
        String name = encode(s.getName());
        String accession = s.getAccession().toUpperCase();
        String accessionReferee = s.getAccession_referee();
        String gi=s.getGi();
        String sequence = s.getSequence().toUpperCase();
        String note = encode(s.getNote());
        String abbreviate=s.getAbbreviate();
        String quality=encode(s.getQuality());

        try {
            //--First add stats
            //addSequenceStats(id, sequence); Not anymore
            //--Add sequence in database
            StringBuilder stri=new StringBuilder();
            stri.append("INSERT INTO Sequence(name, accession,accessionReferee, gi,sequence,note, abbreviate, sequence_len, sequence_type, timeAdded, quality, runProgram_id) VALUES (");          
            stri.append("'"+name+"',");
            stri.append("'"+accession+"',");
            stri.append("'"+accessionReferee+"',");
            stri.append("'"+gi+"',");
            stri.append("'"+sequence+"',");
            stri.append("'"+note+"',");
            stri.append("'"+abbreviate+"',");
            stri.append("'"+s.getLen()+"',");
            stri.append("'"+s.getSequence_type()+"',");
            stri.append("'"+Util.returnCurrentDateAndTime()+"',");
            stri.append("'"+quality+"',");
            stri.append("'"+s.getRunProgram_id()+"'");
            stri.append(");");
            db.execute(stri.toString());
            //--Create a genbank entry just in case
            //int genbankfile_id=getNextGenbankFileID();
            //String i=String.format("INSERT INTO GenbankFile(genbankfile_id,sequence_id) VALUES ('%d','%d');",genbankfile_id,id);
            //db.execute(i);           
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

      public Sequence getSequence(int sequence_id) {
        Sequence tmp=new Sequence();
         try {
            String query = String.format("SELECT name, sequence, abbreviate, accession, accessionReferee, gi, note, timeAdded, sequence_type, sequence_len, quality, runprogram_id FROM Sequence WHERE sequence_id='%d';", sequence_id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                tmp.setName(decode(rs.getString(1)));
                tmp.setSequence(rs.getString(2));
                tmp.setAbbreviate(decode(rs.getString(3)));
                tmp.setAccession(rs.getString(4));
                tmp.setAccession_referee(rs.getString(5));
                tmp.setGi(rs.getString(6));
                tmp.setNote(decode(rs.getString(7)));
                tmp.setTimeAdded(rs.getString(8));
                tmp.setSequence_type(rs.getString(9));
                tmp.setLen(rs.getInt(10));
                tmp.setQuality(decode(rs.getString(11)));
                tmp.setRunProgram_id(rs.getInt(12));
                tmp.setId(sequence_id);
                rs.close();
            }
        } catch (Exception e) {
            if (debug) Config.log("Error DB : "+e.getMessage());
         }        
        return tmp;
    }

       public boolean existsSequence(int id) {
       try {
            String query = String.format("SELECT Sequence_id FROM Sequence WHERE Sequence_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                rs.close();
                return true;
            }
         } catch(Exception e) {return false;}
       return false;
    }

       public boolean removeSequence (Sequence sequence){
        if (sequence.getId()==0) return false;
        try {
                String rm = String.format("DELETE FROM Sequence WHERE sequence_id='%d';", sequence.getId());
                String rm2 = String.format("DELETE FROM MultipleSequences WHERE sequence_id='%d';", sequence.getId());
                String rm3 = String.format("DELETE FROM Alignment WHERE sequence_id='%d';", sequence.getId());
                String rm4 = String.format("DELETE FROM Ancestor WHERE sequence_id='%d';", sequence.getId());
                db.execute(rm);
                db.execute(rm2);
                db.execute(rm3);
                db.execute(rm4);
                return true;
        } catch (Exception e) {
            if (isDebug()) {
                System.out.printf("Unable to remove  %d : %s\n", sequence.getId(), e.getMessage());
            }
            return false;
        }
    }

     public boolean updateSequence(InfoSequence info) {
        try {
            String query = String.format("UPDATE Sequence SET name='%s',abbreviate='%s', note='%s', sequence_type='%s' WHERE sequence_id='%d';",
                                          encode(info.getName()),encode(info.getAbbreviate()), encode(info.getNote()), info.getSequence_type(),info.getId());
            db.execute(query);
        } catch (Exception e) {
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            return false;
         }
         return true;
    }

    public boolean updateSequence(Sequence sequence) {
        if (sequence.getId()==0) return false;
        removeSequence(sequence);
        return (addSequence(sequence)==0?false:true);
    }

    ////////////////////////////////////////////////////////////////////////////


     /**
     * Get a sequence id by its accession
     * @param name
     * @return the sequence_id (primary key value in the table)
     */
    public Vector<Integer> getSequenceIDFromAccession (String accession){
        Vector<Integer>id=new Vector<Integer>();
        try {
            String query = "SELECT sequence_id FROM Sequence WHERE accession ='"+accession+"';";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                id.add(rs.getInt(1));
             }
            rs.close();
        } catch (Exception e) {
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
        }
        return id;
    }

    ////////////////////////////////////////////////////////////////////////////

     /**
     * Get the next free MultipleSequences_id
     * @return the next viable MultipleSequences_id
     */
    public int getNextMultipleSequencesID() {
       //--Get the highest MultipleSequences_id
        int MultipleSequences_id=0;
        while (MultipleSequences_id==0) {
            try {
                String query="SELECT MAX(multipleSequences_id) FROM MultipleSequences;";
                ResultSet rs=db.executeQuery(query);
                 if (rs==null) {
                    System.err.println("ERROR: Unable to get next MultipleSequences id");
                    return 0;
                }      
                if (rs.next()) {
                    MultipleSequences_id=rs.getInt(1);
                    MultipleSequences_id++; //Increase id by 1 to get the next one
                }
                rs.close();
            } catch(Exception e) {}
        }
        return MultipleSequences_id;
    }

     public Vector<Integer> getAllMultipleSequencesID() {
         Vector<Integer>tmp=new Vector<Integer>();
         if (!tables_found.containsKey("MultipleSequences")) return tmp;
         try {
            String query = "SELECT distinct(MultipleSequences_id) FROM MultipleSequences;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                   tmp.add(rs.getInt(1));
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }

     /**
      * Add a MultipleSequences object to the database
      * @param multi
      * @return 0 (failed) or the corresponding id
      */
    public int addMultipleSequences (MultipleSequences multi) {        
        //int id=(multi.getId()==0?this.getNextMultipleSequencesID():multi.getId());
         int id=0;
        if (multi.getId()==0) {
            id=this.getNextMultipleSequencesID();
        } else {
            id=multi.getId();
        }
        int start_sequence_id=this.getNextSequenceID();
        db.setAutoCommit(false);
        for (Sequence s:multi.getSequences()) {            
            int sequence_id=0;
            if (s.getId()==0) {
                s.setId(start_sequence_id++);
                int count=retry;
                //--Retry 3 time
                while(sequence_id==0&&count>0) {
                    sequence_id=addSequence(s);
                    count--;
                }
                if (count==0) {
                   if (isDebug()) Config.log("Error DB : Unable to save one sequence to the database: "+s.toString());
                   return 0;
                }
            } else {
                sequence_id=s.getId(); 
            }
            //--Note: if sequence_id is 0, it will not be inserted
            if (sequence_id!=0) {
                try {
                    String i = String.format("INSERT INTO MultipleSequences (MultipleSequences_id, sequence_id, name, note, runprogram_id) VALUES('%d','%d','%s','%s','%s');",
                                              id,sequence_id, encode(multi.getName()), encode(multi.getNote()), multi.getRunProgram_id());
                    db.execute(i);
                 } catch (Exception e) {if (isDebug()) Config.log("Error DB : "+e.getMessage());}
            }
         multi.setId(id);
        }
        db.commit();
        db.setAutoCommit(true);
        return id;
    }

    /**
     * This is an internal testing function (Not to be used)
     * @param multi
     * @return
     */
      public int addMultipleSequencesMik (MultipleSequences multi) {        
        int id=(multi.getId()==0?this.getNextMultipleSequencesID():multi.getId());
        db.setAutoCommit(false);
        for (Sequence s:multi.getSequences()) {            
            addSequenceMik(s);
        }
        db.commit();
        db.setAutoCommit(true);
        return id;
    }

     public int addMultipleSequencesUpdate (MultipleSequences multi) {
        int id=(multi.getId()==0?this.getNextMultipleSequencesID():multi.getId());
        db.setAutoCommit(false);
        for (Sequence s:multi.getSequences()) {                        
            int sequence_id=s.getId();
            try {
                String i = String.format("INSERT INTO MultipleSequences (MultipleSequences_id, sequence_id, name, note, runprogram_id) VALUES('%d','%d','%s','%s','%s');",
                                          id,sequence_id, encode(multi.getName()), encode(multi.getNote()), multi.getRunProgram_id());
                db.execute(i);              
             } catch (Exception e) {if (isDebug()) Config.log("Error DB : "+e.getMessage());}
         multi.setId(id);
        }
        db.commit();
        db.setAutoCommit(true);
        return id;
    }

     /**
     * Remove a MultipleSequenceID group from MultipleSequences table
     * Note: will not remove the sequences in the database
     * @param AlignmentID
     */
    public boolean  removeMultipleSequences (MultipleSequences multi){
        if (multi.getId()==0) return false;
        try {
               String rm = String.format("DELETE FROM MultipleSequences WHERE MultipleSequences_id='%d';", multi.getId());
                db.execute(rm);
        } catch (Exception e) {
            if (isDebug()) System.out.printf("Unable to remove MultipleSequenceID %d : %s\n", multi.getId(), e.getMessage());
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Remove a MultipleSequenceID group from MultipleSequences table
     * Note: will not remove the sequences in the database
     * @param AlignmentID
     */
    public boolean  removeSequencesFromMultipleSequences (MultipleSequences multi, int id){
        if (multi.getId()==0) return false;
        try {
               String rm = String.format("DELETE FROM MultipleSequences WHERE MultipleSequences_id='%d' AND Sequence_id='%d';", multi.getId(), id);
                db.execute(rm);
        } catch (Exception e) {
            if (isDebug()) System.out.printf("Unable to remove MultipleSequenceID %d : %s\n", multi.getId(), e.getMessage());
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            return false;
        }
        return true;
    }

     /**
     * Remove a MultipleSequenceID group from MultipleSequences table
     * Note: will not remove the sequences in the database
     * @param AlignmentID
     */
    public boolean  removeAlignmentFromMultipleAlignments (MultipleAlignments multi, int id){
        if (multi.getId()==0) return false;
        try {
               String rm = String.format("DELETE FROM MultipleAlignments WHERE MultipleAlignments_id='%d' AND Alignment_id='%d';", multi.getId(), id);
                db.execute(rm);
        } catch (Exception e) {
            if (isDebug()) System.out.printf("Unable to remove MultipleAlignmentID %d : %s\n", multi.getId(), e.getMessage());
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            return false;
        }
        return true;
    }

    public String getMultipleSequencesName(int id) {
       try {
            String query = String.format("SELECT name FROM MultipleSequences WHERE MultipleSequences_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                  String name=decode(rs.getString(1));
                  rs.close();
                  return name;
            }

         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return "";}
       return "";
    }

     public boolean existsMultipleSequences(int id) {
       try {
            String query = String.format("SELECT MultipleSequences_id FROM MultipleSequences WHERE MultipleSequences_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                rs.close();
                return true;
            }
         } catch(Exception e) {return false;}
       return false;
    }

    public MultipleSequences getMultipleSequence(int multiplesequences_id) {
        MultipleSequences multi=new MultipleSequences();
        Vector<Integer> sequences_id=new Vector<Integer>();
        //--1. Get some information
         try {
            //TO DO: CREATE A ONE QUERY SYSTEM... CHANGE OF DATABASE SCHEMA...
            String query = String.format("SELECT name, note, RunProgram_id FROM MultipleSequences WHERE MultipleSequences_id='%d';", multiplesequences_id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                 multi.setName(decode(rs.getString(1)));
                 multi.setNote(decode(rs.getString(2)));
                 multi.setRunProgram_id(rs.getInt(3));
                 rs.close();
            }
            String query2 = String.format("SELECT sequence_id FROM MultipleSequences WHERE MultipleSequences_id='%d';", multiplesequences_id);
            rs = db.executeQuery(query2);
            while (rs.next()) {
                  sequences_id.add(rs.getInt(1));
            }
            rs.close();
         } catch(Exception e) {if (debug)Config.log("Error DB : "+e.getMessage());return multi;}
         //--2. Get the sequences
          for (int sequence_id:sequences_id) {
                Sequence s=getSequence(sequence_id);
                multi.add(s);
           }
        //--3. Set the MultipleSequences_id and return
          multi.setId(multiplesequences_id);
        return multi;
    }

    public boolean updateMultipleSequences(MultipleSequences multi) {
        if (multi.getId()==0) return false;
        removeMultipleSequences(multi);
        return (addMultipleSequencesUpdate(multi)==0?false:true);
    }

    /**
     * Search for MultipleSequences containing sequences od
     * @param id
     * @return a LinkedList of the found multipleSequences ID..
     */
    public Vector<Integer> getMultipleSequencesIdContainingSequenceId(int id) {
        Vector<Integer>tmp=new Vector<Integer>();
        try {
            String query = String.format("SELECT MultipleSequences_id FROM MultipleSequences WHERE sequence_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null) {
                while (rs.next()) {
                    tmp.add(rs.getInt(1));
                }
            }
             rs.close();               
         } catch(Exception e) {return tmp;}
        return tmp;
    }
     

    ////////////////////////////////////////////////////////////////////////////

     /**
     * Get the next free RunWorkflow_id
     * @return the next viable RunWorkflow_id
     */
    public int getNextRunWorkflowID() {
       //--Get the highest MultipleSequences_id
        int runworkflow_id=0;
        while (runworkflow_id==0) {
            try {
                String query="SELECT MAX(RunWorkflow_id) FROM RunWorkflow;";
                ResultSet rs=db.executeQuery(query);
                if (rs==null) {
                    System.err.println("ERROR: Unable to get next RunWorkflow id");
                    return 0;
                }      
                if (rs.next()) {
                    runworkflow_id=rs.getInt(1);
                    runworkflow_id++; //Increase id by 1 to get the next one
                    rs.close();
                }
            } catch(Exception e) {}
        }
        return runworkflow_id;
    }

     public boolean existsRunWorkflow(int id) {
       try {
            String query = String.format("SELECT RunWorkflow_id FROM RunWorkflow WHERE RunWorkflow_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                rs.close();
                return true;
            }
         } catch(Exception e) {return false;}
       return false;
    }

     public Vector<Integer> getAllRunWorkflowID() {
         Vector<Integer>tmp=new Vector<Integer>();
         if (!tables_found.containsKey("RunWorkflow")) return tmp;
         try {
            String query = "SELECT distinct(RunWorkflow_id) FROM RunWorkflow;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                   tmp.add(rs.getInt(1));
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }

    public int addRunWorkflow (RunWorkflow runworkflow) {
        int id=(runworkflow.getId()==0?this.getNextRunWorkflowID():runworkflow.getId());
        String name=encode(runworkflow.getName());
        String note=encode(runworkflow.getNote());
        db.setAutoCommit(false);
        try {
            for (Integer execution_id:runworkflow.getExecution_workflow_id()) {                       

                    String i = String.format("INSERT INTO RunWorkflow (RunWorkflow_id, original_id, execution_id, complete,name, note) VALUES('%d','%d','%d','%s','%s','%s');",
                                              id, runworkflow.getOriginal_workflow_id(), execution_id, (runworkflow.isCompleted()?"True":"False"),name, note);
                    db.execute(i);              

            }
            runworkflow.setId(id);
        } catch (Exception e) {if (isDebug()) Config.log("Error DB : "+e.getMessage());}
        db.commit();
        db.setAutoCommit(true);
        return id;
    }

     /**
     * Remove a MultipleSequenceID group from MultipleSequences table
     * Note: will not remove the sequences in the database
     * @param AlignmentID
     */
    public boolean  removeRunWorkflow (RunWorkflow runworkflow){
        if (runworkflow.getId()==0) return false;
        try {
               String rm = String.format("DELETE FROM RunWorkflow WHERE RunWorkflow_id='%d';", runworkflow.getId());
                db.execute(rm);
        } catch (Exception e) {
            if (isDebug()) System.out.printf("Unable to remove RunWorkflow ID %d : %s\n", runworkflow.getId(), e.getMessage());
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            return false;
        }
        return true;
    }

    public String getRunWorkflowName(int id) {

        try {            
            String query = String.format("SELECT name FROM RunWorkflow WHERE RunWorkflow_id ='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                  String name=decode(rs.getString(1));
                  rs.close();
                  return name;
            }
         } catch(Exception e) {if (debug) Config.log("Error DB : "+e.getMessage());return "";}
       return "";
    }

  
    public RunWorkflow getRunWorkflowFromWorkflows(int workflows_id) {
         try {
            //TO DO: CREATE A ONE QUERY SYSTEM... CHANGE OF DATABASE SCHEMA...
            int runworkflow_id=0;
             String query = String.format("SELECT RunWorkflow_id FROM RunWorkflow WHERE execution_id='%d' OR original_id='%d';", workflows_id,workflows_id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                 runworkflow_id=rs.getInt(1);
            }
            rs.close();            
            if (runworkflow_id!=0) return getRunWorkflow(runworkflow_id);
         } catch(Exception e) {if (debug) Config.log("Error DB : "+e.getMessage());return null;}
         return null;
    }

    public RunWorkflow getRunWorkflow(int runworkflow_id) {
        RunWorkflow tmp=new RunWorkflow();       
        //--1. Get some information
         try {
            //TO DO: CREATE A ONE QUERY SYSTEM... CHANGE OF DATABASE SCHEMA...
            String query = String.format("SELECT name, note, original_id, complete FROM RunWorkflow WHERE RunWorkflow_id='%d';", runworkflow_id);
            ResultSet rs = db.executeQuery(query);
            while (rs!=null&&rs.next()) {
                 tmp.setName(decode(rs.getString(1)));
                 tmp.setNote(decode(rs.getString(2)));
                 tmp.setOriginal_workflow_id(rs.getInt(3));
                 tmp.setCompleted(Boolean.valueOf(rs.getString(4)));
            }
            rs.close();
            String query2 = String.format("SELECT execution_id FROM RunWorkflow WHERE RunWorkflow_id='%d';", runworkflow_id);
            rs = db.executeQuery(query2);
            while (rs.next()) {
                  tmp.getExecution_workflow_id().add(rs.getInt(1));
            }
            rs.close();
         } catch(Exception e) {if (debug) Config.log("Error DB : "+e.getMessage());return tmp;}
          tmp.setId(runworkflow_id);
        return tmp;
    }

    public boolean updateRunWorkflow(RunWorkflow run_workflow) {
        if (run_workflow.getId()==0) return false;
        removeRunWorkflow(run_workflow);
        return (addRunWorkflow(run_workflow)==0?false:true);
    }

    
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Add an alignment to the database
     * @param alignment
     * @return the new alignment id
     */
      public int addAlignment(Alignment alignment) {
       //int id=(alignment.getId()==0?this.getNextAlignmentID():alignment.getId());
           int id=0;
        if (alignment.getId()==0) {
            id=this.getNextAlignmentID();
        } else {
            id=alignment.getId();
        }
       int start_sequence_id=this.getNextSequenceID();
       db.setAutoCommit(false);
        for (Sequence s:alignment.getSequences()) {
            int sequence_id=0;
            if (s.getId()==0) {
                s.setId(start_sequence_id++);
                 //--Retry 3 time
                int count=retry;
                while(sequence_id==0&&count>0) {
                    sequence_id=addSequence(s);
                    count--;
                }
                if (count==0) {
                   if (isDebug()) Config.log("Error DB : Unable to save one sequence to the database: "+s.toString());
                   return 0;
                }
            } else {
                sequence_id=s.getId();
            }             
            if (sequence_id!=0) {
                try {
                    String i = String.format("INSERT INTO Alignment (Alignment_id, sequence_id, original_sequence_id, runProgram_id, name, note) VALUES('%d','%d','%d','%d','%s','%s');",
                                              id, sequence_id,s.getOriginal_id(), alignment.getRunProgram_id(), encode(alignment.getName()), encode(alignment.getNote()));
                      db.execute(i);
                 } catch (Exception e) {if (isDebug()) Config.log("Unable to add alignment for "+s);}
            }
        }
        db.commit();
        db.setAutoCommit(true);
        return id;
    }

     /**
     * Remove an Alignment from Alignment table
     * @param Alignment
     */
    public boolean removeAlignment (Alignment alignment){
        if (alignment.getId()==0) return false;
        try {
                String rm = String.format("DELETE FROM Alignment WHERE Alignment_id='%d';", alignment.getId());
                db.execute(rm);
                return true;
        } catch (Exception e) {
            if (isDebug()) System.out.printf("Unable to remove Alignment : %s\n", alignment.toString());
            if (isDebug()) {
                Config.log("Error DB : "+e.getMessage());
            }
            return false;
        }
    }

    public String getAlignmentName(int alignment_id) {
       try {
            String query = String.format("SELECT name FROM Alignment WHERE alignment_id='%d';", alignment_id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                  String name=decode(rs.getString(1));
                  rs.close();
                  return name;
            }
         } catch(Exception e) {if (debug) Config.log("Error DB : "+e.getMessage());return "";}
       return "";
    }

     public Alignment getAlignment(int alignment_id) {
        Alignment align=new Alignment();
        Vector<Integer> sequences_id=new Vector<Integer>();
        Vector<Integer> original_sequences_id=new Vector<Integer>();
        //--1. Get some information
         try {
            String query = String.format("SELECT sequence_id, original_sequence_id, runprogram_id, name, note FROM Alignment WHERE alignment_id='%d';", alignment_id);
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  sequences_id.add(rs.getInt(1));
                  original_sequences_id.add(rs.getInt(2));
                  align.setRunProgram_id(rs.getInt(3));
                  align.setName(decode(rs.getString(4)));
                  align.setNote(decode(rs.getString(5)));
            }
            rs.close();
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return align;}
         //--2. Get the sequences and set information
         for (int i=0;i<sequences_id.size();i++) {
                Sequence s=getSequence(sequences_id.get(i));
                s.setOriginal_id(original_sequences_id.get(i));
                align.add(s);
           }
        //--3. Set the alignment_id and return
          align.setId(alignment_id);
        return align;
    }

    public boolean updateAlignment(Alignment alignment) {
        if (alignment.getId()==0) return false;
        removeAlignment(alignment);
        addAlignment(alignment);
        return (alignment.getId()==0?false:true);
    }

    
    public Vector<Integer> getAlignmentIdContainingSequenceId(int id) {
        Vector<Integer>tmp=new Vector<Integer>();
        try {
            String query = String.format("SELECT Alignment_id FROM Alignment WHERE sequence_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null) {
                while (rs.next()) {
                    tmp.add(rs.getInt(1));
                }
            }
             rs.close();               
         } catch(Exception e) {return tmp;}
        return tmp;
    }
    
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Get the next free Project_id
     * @return the next viable Project_id
     */
    public int getNextProjectID() {
       //--Get the highest Project_od
        int Project_id=0;
        int retry=0;
            try {
                String query="SELECT MAX(Project_id) FROM Project;";
                ResultSet rs=db.executeQuery(query);
                 if (rs==null) {
                    System.err.println("ERROR: Unable to get next Project id");
                    return 0;
                }      
                if (rs.next()) {
                    Project_id=rs.getInt(1);
                    Project_id++; //Increase id by 1 to get the next one
                }
                rs.close();
            } catch(Exception e) {}
         return Project_id;
    }

    public int addProject (Project project){
        //int id=(project.getId()==0?this.getNextProjectID():project.getId());
        int id=0;
        if (project.getId()==0) {
            id=this.getNextProjectID();
        } else {
            id=project.getId();
        }
        try {
             String i = String.format("INSERT INTO Project (Project_id, ProjectName, Institution, Note, Author, DateCreated, Email) VALUES('%d','%s','%s','%s','%s','%s','%s');",
                        id, encode(project.getName()), encode(project.getInstitution()),encode(project.getNote()), encode(project.getAuthor()), project.getDateCreated(), encode(project.getEmail()));
             db.execute(i);
             project.setId(id);
        } catch (Exception e) {
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            Config.log("Unable to add the Project "+project);
            return 0;
        }
        return id;
    }

    public Vector<Integer> getAllProjectID() {
         Vector<Integer>tmp=new Vector<Integer>();
          if (!tables_found.containsKey("Project")) return tmp;
         try {
            String query = "SELECT Project_Id FROM Project;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int id=rs.getInt(1);
                  tmp.add(id);
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }

       public boolean existsProject(int id) {
       try {
            String query = String.format("SELECT Project_id FROM Project WHERE Project_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                rs.close();
                return true;
            }
         } catch(Exception e) {return false;}
       return false;
    }

    public Project getProject(int project_id) {
        Project tmp=new Project();
         try {
            String query = String.format("SELECT Project_id, ProjectName, Institution, Note, Author, DateCreated, Email FROM Project WHERE project_id='%d';", project_id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                    tmp.setId(rs.getInt(1));
                    tmp.setName(decode(rs.getString(2)));
                    tmp.setInstitution(decode(rs.getString(3)));
                     tmp.setNote(decode(rs.getString(4)));
                     tmp.setAuthor(decode(rs.getString(5)));
                     tmp.setDateCreated(rs.getString(6));
                     tmp.setEmail(decode(rs.getString(7)));
                     rs.close();
            }
         } catch(Exception e) {
            if (debug) Config.log("Error DB : "+e.getMessage());
         }
         return tmp;
    }

     /**
     * Remove an Project from Project table
     * @param Project
     */
    public boolean removeProject (Project project){
        if (project.getId()==0) return false;
        try {
                String rm = String.format("DELETE FROM Project WHERE Project_id='%d';", project.getId());
                db.execute(rm);
        } catch (Exception e) {
            if (isDebug()) {
                System.out.printf("Unable to remove Project : %s\n", project.toString());
                Config.log("Error DB : "+e.getMessage());
            }
            return false;
        }
        return true;
    }

    public boolean updateProject(Project project) {
        if (project.getId()==0) return false;
        removeProject(project);
        return (addProject(project)==0?false:true);
    }

    ////////////////////////////////////////////////////////////////////////////

      /**
     * Get the next free MultipleSequences_id
     * @return the next viable MultipleSequences_id
     */
    public int getNextMultipleTreesID() {
       //--Get the highest MultipleSequences_id
        int MultipleTrees_id=0;
        while(MultipleTrees_id==0) {
            try {
                String query="SELECT MAX(MultipleTrees_id) FROM MultipleTrees;";
                ResultSet rs=db.executeQuery(query);
                 if (rs==null) {
                    System.err.println("ERROR: Unable to get next MultipleTrees id");
                    return 0;
                }      
                if (rs.next()) {
                    MultipleTrees_id=rs.getInt(1);
                    MultipleTrees_id++; //Increase id by 1 to get the next one
                }
                rs.close();
            } catch(Exception e) {}
        }
        return MultipleTrees_id;
    }

    public String getMultipleTreesName(int id) {
       try {
            String query = String.format("SELECT name FROM MultipleTrees WHERE MultipleTrees_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                  String name=decode(rs.getString(1));
                  rs.close();
                  return name;
            }
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return "";}
       return "";
    }

    public MultipleTrees getMultipleTreesFromAlignment(int alignment_id) {
        MultipleTrees multi=new MultipleTrees();
        Vector<Integer>tree_ids=new Vector<Integer>();
        int runProgram_id=0;
         try {
            String query = String.format("SELECT tree_id FROM TreesSequences WHERE alignment_id='%d';", alignment_id);
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  tree_ids.add(rs.getInt(1));
            }
            rs.close();
            for (int id:tree_ids) {
                Tree tree=getTree(id);
                multi.add(tree);
                runProgram_id=tree.getRunProgram_id();
            }
            multi.setAlignment_id(alignment_id);
            multi.setRunProgram_id(runProgram_id);
            return multi;
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return multi;}
    }

    public boolean existsMultipleTrees(int id) {
       try {
            String query = String.format("SELECT MultipleTrees_id FROM MultipleTrees WHERE MultipleTrees_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                rs.close();
                return true;
            }
         } catch(Exception e) {return false;}
       return false;
    }

      public Vector<Integer> getAllMultipleTreesID() {
         Vector<Integer>tmp=new Vector<Integer>();
        if (!tables_found.containsKey("MultipleTrees")) return tmp;
         try {
            String query = "SELECT distinct( MultipleTrees_id) FROM  MultipleTrees;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int id=rs.getInt(1);
                  tmp.add(id);
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }

    public int addMultipleTrees(MultipleTrees multi) {

        db.setAutoCommit(false);
        int multipletrees_id=(multi.getId()==0?this.getNextMultipleTreesID():multi.getId());
        try {
        for (Tree tree:multi.getTree()) {
            int id=0;
            if (tree.getId()==0) {
                id=addTree(tree);
            } else {
                id=tree.getId(); 
            }
            String i = String.format("INSERT INTO MultipleTrees (tree_id, MultipleTrees_id, Name, Note, Runprogram_id) VALUES('%d','%d','%s','%s','%d');",id, multipletrees_id , encode(multi.getName()),encode(multi.getNote()), multi.getRunProgram_id());
            db.execute(i);            
            db.commit();
        }
        } catch(Exception e) {
            return 0;
        }
        db.setAutoCommit(true);
        multi.setId(multipletrees_id);
        return multipletrees_id;
    }

    public int addMultipleTreesUpdate(MultipleTrees multi) {

        db.setAutoCommit(false);
        int multipletrees_id=(multi.getId()==0?this.getNextMultipleTreesID():multi.getId());
        try {
        for (Tree tree:multi.getTree()) {
            int id=tree.getId();
            String i = String.format("INSERT INTO MultipleTrees (tree_id, MultipleTrees_id, Name, Note, Runprogram_id) VALUES('%d','%d','%s','%s','%d');",id, multipletrees_id , encode(multi.getName()),encode(multi.getNote()), multi.getRunProgram_id());
            db.execute(i);
            db.commit();
        }
        } catch(Exception e) {
            return 0;
        }
        db.setAutoCommit(true);
        multi.setId(multipletrees_id);
        return multipletrees_id;
    }


    /**
     * Remove a MultipleSequenceID group from MultipleSequences table
     * Note: will not remove the sequences in the database
     * @param AlignmentID
     */
    public boolean  removeMultipleTrees (MultipleTrees multi){
        if (multi.getId()==0) return false;
        try {
               String rm = String.format("DELETE FROM MultipleTrees WHERE MultipleTrees_id='%d';", multi.getId());
                db.execute(rm);
        } catch (Exception e) {
            if (isDebug()) System.out.printf("Unable to remove MultipleTreesID %d : %s\n", multi.getId(), e.getMessage());
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            return false;
        }
        return true;
    }

     public MultipleTrees getMultipleTrees(int multipletrees_id) {
         MultipleTrees tmp=new MultipleTrees();
         Vector<Integer> trees_id=new Vector<Integer>();

         try {
            String query = String.format("SELECT tree_id, name, note, runprogram_id FROM MultipleTrees WHERE multipleTrees_id='%d';", multipletrees_id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null)
                while (rs.next()) {
                      trees_id.add(rs.getInt(1));
                     tmp.setName(decode(rs.getString(2)));
                     tmp.setNote(decode(rs.getString(3)));
                     tmp.setRunProgram_id(rs.getInt(4));
                }
                rs.close();
            for (int id:trees_id) {
                tmp.add(new Tree(id));
            }
            tmp.setId(multipletrees_id);
         } catch(Exception e) {
            if (debug) Config.log("Error DB : "+e.getMessage());
         }
         return tmp;
     }

      public boolean updateMultipleTrees(MultipleTrees multi) {
        if (multi.getId()==0) return false;
        this.removeMultipleTrees(multi);
        return (addMultipleTreesUpdate(multi)==0?false:true);
    }

    ////////////////////////////////////////////////////////////////////////////
      /**
     * Get the next free MultipleAlignments_id
     * @return the next viable MultipleAlignments_id
     */
    public int getNextMultipleAlignmentsID() {
       //--Get the highest MultipleSequences_id
        int MultipleAlignments_id=0;
        while(MultipleAlignments_id==0) {
            try {
                String query="SELECT MAX(MultipleAlignments_id) FROM MultipleAlignments;";
                ResultSet rs=db.executeQuery(query);
                 if (rs==null) {
                    System.err.println("ERROR: Unable to get next MultipleAlignments id");
                    return 0;
                }      
                if (rs.next()) {
                    MultipleAlignments_id=rs.getInt(1);
                    MultipleAlignments_id++; //Increase id by 1 to get the next one
                }
                rs.close();
            } catch(Exception e) {}
        }
        return MultipleAlignments_id;
    }

    public String getMultipleAlignmentsName(int id) {
       try {
            String query = String.format("SELECT name FROM MultipleAlignments WHERE MultipleAlignments_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                  String name=decode(rs.getString(1));
                  rs.close();
                  return name;
            }
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return "";}
       return "";
    }

   

    public boolean existsMultipleAlignments(int id) {
       try {
            String query = String.format("SELECT MultipleAlignments_id FROM MultipleAlignments WHERE MultipleAlignments_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                rs.close();
                return true;
            }
         } catch(Exception e) {return false;}
       return false;
    }

      public Vector<Integer> getAllMultipleAlignmentsID() {
         Vector<Integer>tmp=new Vector<Integer>();
         if (!tables_found.containsKey("MultipleAlignments")) return tmp;
         try {
            String query = "SELECT distinct( MultipleAlignments_id) FROM  MultipleAlignments;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int id=rs.getInt(1);
                  tmp.add(id);
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }

    public int addMultipleAlignments(MultipleAlignments multi) {

        db.setAutoCommit(false);
        int multiplealignments_id=(multi.getId()==0?this.getNextMultipleAlignmentsID():multi.getId());
        try {
        for (Alignment alignment:multi.getAlignments()) {
            int id=0;
            if (alignment.getId()==0) {
                id=addAlignment(alignment);
            } else {
                id=alignment.getId();
            }
            String i = String.format("INSERT INTO MultipleAlignments (alignment_id, MultipleAlignments_id, Name, Note, Runprogram_id) VALUES('%d','%d','%s','%s','%d');",id, multiplealignments_id , encode(multi.getName()),encode(multi.getNote()), multi.getRunProgram_id());
            db.execute(i);            
            db.commit();
        }
        } catch(Exception e) {
            return 0;
        }
        db.setAutoCommit(true);
        multi.setId(multiplealignments_id);
        return multiplealignments_id;
    }

    public int addMultipleAlignmentsUpdate(MultipleAlignments multi) {

        db.setAutoCommit(false);
        int multiplealignments_id=(multi.getId()==0?this.getNextMultipleAlignmentsID():multi.getId());
        try {
        for (Alignment alignment:multi.getAlignments()) {
            int id=alignment.getId();
            String i = String.format("INSERT INTO MultipleAlignments (alignment_id, MultipleAlignments_id, Name, Note, Runprogram_id) VALUES('%d','%d','%s','%s','%d');",id, multiplealignments_id , encode(multi.getName()),encode(multi.getNote()), multi.getRunProgram_id());
            db.execute(i);
            db.commit();
        }
        } catch(Exception e) {
            return 0;
        }
        db.setAutoCommit(true);
        multi.setId(multiplealignments_id);
        return multiplealignments_id;
    }


    /**
     * Remove a MultipleSequenceID group from MultipleSequences table
     * Note: will not remove the sequences in the database
     * @param AlignmentID
     */
    public boolean  removeMultipleAlignments (MultipleAlignments multi){
        if (multi.getId()==0) return false;
        try {
               String rm = String.format("DELETE FROM MultipleAlignments WHERE MultipleAlignments_id='%d';", multi.getId());
                db.execute(rm);
        } catch (Exception e) {
            if (isDebug()) System.out.printf("Unable to remove MultipleAlignmentsID %d : %s\n", multi.getId(), e.getMessage());
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            return false;
        }
        return true;
    }

     public MultipleAlignments getMultipleAlignments(int multiplealignments_id) {
         MultipleAlignments tmp=new MultipleAlignments();
         Vector<Integer> alignments_id=new Vector<Integer>();

         try {
            String query = String.format("SELECT alignment_id, name, note, runprogram_id FROM MultipleAlignments WHERE multipleAlignments_id='%d';", multiplealignments_id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null)
                while (rs.next()) {
                     alignments_id.add(rs.getInt(1));
                     tmp.setName(decode(rs.getString(2)));
                     tmp.setNote(decode(rs.getString(3)));
                     tmp.setRunProgram_id(rs.getInt(4));
                }
                rs.close();
            for (int id:alignments_id) {
                tmp.add(new Alignment(id));
            }
            tmp.setId(multiplealignments_id);
         } catch(Exception e) {
            if (debug) Config.log("Error DB : "+e.getMessage());
         }
         return tmp;
     }

      public boolean updateMultipleAlignments(MultipleAlignments multi) {
        if (multi.getId()==0) return false;
        this.removeMultipleAlignments(multi);
        return (addMultipleAlignmentsUpdate(multi)==0?false:true);
    }


    ////////////////////////////////////////////////////////////////////////////

    /**
     * Get the next free tree_id
     * @return the next viable tree_id
     */
    public int getNextTreeID() {
       //--Get the highest tree_id
        int id=0;
        while(id==0) {
            try {
                String query="SELECT MAX(Tree_id) FROM Tree;";
                ResultSet rs=db.executeQuery(query);
                if (rs==null) {
                    System.err.println("ERROR: Unable to get next Tree id");
                    return 0;
                }   
                if (rs.next()) {
                    id=rs.getInt(1);
                    id++; //Increase id by 1 to get the next one
                }
                rs.close();
            } catch(Exception e) {}
        }
         return id;
    }

    /**
     * Add a tree in Tree table.
     * If a tree already exist, it won't be added
     * @param runProgram_id : program id used to make the tree
     * @param tree : tree in nexus format
     * @param note : notes
     */
    public int addTree (Tree atree){
        String tree = encode(atree.getTree());
        String treeSequenceID = encode(atree.getTreeSequenceID());
        String treeAbbreviate = encode(atree.getTreeAbbreviate());
        String note = encode(atree.getNote());
        String name = encode (atree.getName());
        int runProgram_id=atree.getRunProgram_id();
         int id=0;
        if (atree.getId()==0) {
            id=this.getNextTreeID();
        } else {
            id=atree.getId();
        }
        try {
             String i = String.format("INSERT INTO Tree (Tree_id, runProgram_id, tree, note, treeSequenceID, treeAbbreviate,name) VALUES('%d','%d','%s','%s','%s','%s','%s');",id, runProgram_id,tree, note, treeSequenceID, treeAbbreviate,name);
             db.execute(i);
             atree.setId(id);
        } catch (Exception e) {
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            Config.log("Unable to add the tree "+atree);
            return 0;
        }
        return id;
    }

    public String getTreeName(int id) {
       try {
            String query = String.format("SELECT name FROM Tree WHERE tree_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                  String name=decode(rs.getString(1));
                  rs.close();
                  return name;
            }
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return "";}
       return "";
    }

    /**
     * Get a  Tree from Tree
     * @param treeId
     * @return AlignmentId
     */
    public Tree getTree(Integer treeId){
        Tree tree=new Tree();
        try {
            String query = String.format("SELECT Tree, RunProgram_id, note, treeSequenceID, treeAbbreviate, rooted, name FROM Tree WHERE tree_id='%d';", treeId);
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                tree.setId(treeId);
                tree.setTree(decode(rs.getString(1)));
                tree.setRunProgram_id(rs.getInt(2));
                tree.setNote(decode(rs.getString(3)));
                tree.setTreeSequenceID(decode(rs.getString(4)));
                tree.setTreeAbbreviate(decode(rs.getString(5)));
                tree.setRooted(Boolean.valueOf(rs.getString(6)));
                tree.setName(decode(rs.getString(7)));
           }
            rs.close();
        } catch (Exception e) {
            if (isDebug()) {
                Config.log("Error DB : "+e.getMessage());
                System.out.printf("Unable to get Tree with treeId %d \n", treeId);
            }
        }
        return tree;
    }

     /**
     * Remove a tree
     * @param treeId
     */
    public boolean removeTree (Tree tree){
        if (tree.getId()==0) return false;
        try {
               String rm = String.format("DELETE FROM Tree WHERE tree_id='%d';", tree.getId());
               db.execute(rm);
        } catch (Exception e) {
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            return false;
        }
        return true;
    }

    public boolean updateTree(Tree tree) {
        if (tree.getId()==0) return false;
        removeTree(tree);
        return (addTree(tree)==0?false:true);
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
     * Get the next free Ancestor_id
     * @return the next viable Ancestor_id
     */
    public int getNextAncestorID() {
       
        int id=0;
        while(id==0) {
            try {
                String query="SELECT MAX(Ancestor_id) FROM Ancestor;";
                ResultSet rs=db.executeQuery(query);
                 if (rs==null) {
                    System.err.println("ERROR: Unable to get next Ancestor id");
                    return 0;
                }      
                if (rs.next()) {
                    id=rs.getInt(1);
                    id++; //Increase id by 1 to get the next one
                }
                rs.close();
            } catch(Exception e) {}
        }
         return id;
    }

     public Vector<Integer> getAllAncestorID() {
         Vector<Integer>tmp=new Vector<Integer>();
         if (!tables_found.containsKey("Ancestor")) return tmp;
         try {
            String query = "SELECT Ancestor_id FROM Ancestor;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int id=rs.getInt(1);
                  tmp.add(id);
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }


//    CREATE TABLE Ancestor
//(
//	ancestor_id INTEGER NOT NULL,
//        sequence_id INTEGER NOT NULL,
//	alignment_id INTEGER,
//        tree_id INTEGER,
//	runProgram_id INTEGER DEFAULT 0,
//	ancestor TEXT,
//	note TEXT
//);
//    public void addAncestor (Ancestor ancestor){
//
//        try {
//            String i = String.format("INSERT INTO Ancestor (Alignment_id, " +
//                        " runProgram_id, ancestor, note) VALUES('%d','%d','%s','%s');", AlignmentId, runProgramId, encode(ancestor), encode(note));
//              if (db.execute(i)&&isDebug()) Config.log("Ancestor added!");
//        } catch (Exception e) {
//            if (isDebug()) {
//                Config.log("Error DB : "+e.getMessage());
//                Config.log("Unable to add Ancestor");
//            }
//            return id;
//        }
//    }

    /**
     * Remove and Ancestor by its ID
     * @param ancestorId
     */
    public void  removeAncestor (Integer ancestorId){
        try {            
            String rm = String.format("DELETE FROM Ancestor WHERE ancestor_id='%d';", ancestorId);
             db.execute(rm);            
        } catch (Exception e) {
            if (isDebug()) System.out.printf("Unable to remove ancestorId %s : %s\n", ancestorId, e.getMessage());
            if (isDebug()) {
                Config.log("Error DB : "+e.getMessage());
            }
        }
    }

    /**
     * Get an ancestorId from an AlignmentId
     * @param Alignment_id
     * @return AncestorId
     */
    public Integer getAncestor (Integer AlignmentId){
        int id = 0;
        try {
            String query = String.format("SELECT ancestor_id FROM Ancestor WHERE Alignment_id='%s';", AlignmentId);
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                id = rs.getInt(1);
                if (isDebug()) System.out.printf("AncestorId found at AlignmentId %d : %d\n", AlignmentId, id);
            }
            rs.close();
        } catch (Exception e) {
            if (isDebug()) {
                Config.log("Error DB : "+e.getMessage());
            }
            if (isDebug()) System.out.printf("Unable to get AncestorId with the AlignmentId %s : %s\n", AlignmentId, e.getMessage());
            id=0;
        }
        if (id==0) {
            if (isDebug()) System.out.printf("AlignmentId %d not found in database\n", AlignmentId);
        }
        return id;
    }

     /**
     * Get id's where a subString of an ancestor will be found in the Ancestor table
     * @param AncestorSubString
     * @return ancestorId
     */
    public String getSearchAncestor (String AncestorSubString){
        AncestorSubString = AncestorSubString.toUpperCase();
        int id=0;
        String ids="";
        try {
            String query = "SELECT ancestor_id FROM Ancestor WHERE " +
                    "ancestor LIKE '%"+AncestorSubString+"%';";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()){
                id = rs.getInt(1);
                if (isDebug()) System.out.printf("SubString '%s' found at id %s\n", AncestorSubString, id);
                ids = String.format("%s\n%s", ids, id);
            }
            rs.close();
        } catch (Exception e) {
            if (isDebug()) {
                Config.log("Error DB : "+e.getMessage());
            }
            if (isDebug()) System.out.printf("Unable to get AncestorSubString pattern (%s) : %s\n", AncestorSubString, e.getMessage());
            ids=null;
            id=0;
        }
        if (ids==null||id==0) {
            if (isDebug()) System.out.printf("AncestorSubString %s not found in database\n", AncestorSubString);
        }
        if (isDebug()) Config.log(ids);
        return ids;
    }
    
    ////////////////////////////////////////////////////////////////////////////
     /**
     * Search for MultipleSequences containing sequences od
     * @param id
     * @return a LinkedList of the found multipleSequences ID..
     */
    public Vector<Integer> getAncestorIdContainingSequenceId(int id) {
        Vector<Integer>tmp=new Vector<Integer>();
        try {
            String query = String.format("SELECT  Ancestor_id FROM  Ancestor WHERE sequence_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null) {
                while (rs.next()) {
                    tmp.add(rs.getInt(1));
                }
            }
             rs.close();               
         } catch(Exception e) {return tmp;}
        return tmp;
    }
    
 
     /**
      * Get a list of tables who are within database in alphabetic order
      * @return a list of tables in database except 'Connectors', 'Objects',
      * 'PropertiesValues', 'Properties'
      */
    public ArrayList<String> getTablesAtoZforTree (){
        ArrayList <String> strList = new ArrayList <String>();
       
        String query ="";
        try {
           query = "SELECT name FROM sqlite_master WHERE type = 'table' " +
                   "AND name NOT IN ('Connectors', 'Objects', 'PropertiesValues', 'Properties', 'GenBankFile')" +
                   " ORDER BY name;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()){
                strList.add(rs.getString(1));
            }
            rs.close();
        } catch (Exception e) {
            if (isDebug()) {
                Config.log(query);
                Config.log("Error DB : "+e.getMessage());
                Config.log("Unable to get tables");
            }
        }
        return strList;
    }


     /**
      * Get a list of tables who are within database
      * @return a list of tables in database
      */
    public ArrayList<String> getTables (){
        ArrayList <String> strList = new ArrayList <String>();
        try {
            String query = "SELECT name FROM sqlite_master WHERE type = 'table';";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()){
                strList.add(rs.getString(1));
            }
            rs.close();
        } catch (Exception e) {
            if (isDebug()) {
                Config.log("Error DB : "+e.getMessage());
                Config.log("Unable to get tables");
            }    
        }       
        return strList;
    }

      /**
      * Return if a table is not empty
      * @return true if table contains table or false, if it's not
      */
    public boolean checkTableContent (String tableName){
//        try {
//            String query = String.format("SELECT * FROM '%s';", tableName);
//            ResultSet rs = db.executeQuery(query);
//            return (rs.next());
//        } catch (Exception e) {}
        return true;
    }
 

     ///////////////////////////////////////////////////////////////////////////

     public boolean isDatabaseConstainsWorkflows(int workflow_id) {
        String query=String.format("SELECT * FROM workflows WHERE workflows_id='%d';",workflow_id);
        boolean containsWorkflow=false;
        try {
            ResultSet rs=db.executeQuery(query);
            if (rs.next()) containsWorkflow=true;
            rs.close();
        } catch(Exception e) {return false;}
        return containsWorkflow;
     }

      public boolean existsWorkflows(int id) {
       try {
            String query = String.format("SELECT workflows_id FROM Workflows WHERE workflows_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                rs.close();
                return true;
            }
         } catch(Exception e) {return false;}
       return false;
    }


     /**
      * 
      * @return a list of valid workflow id
      */
     public Vector<Integer> getAllWorkflowsID() {
         Vector<Integer>tmp=new Vector<Integer>();
           if (!tables_found.containsKey("Workflows")) return tmp;
         try {
            String query = "SELECT workflows_id FROM Workflows;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int id=rs.getInt(1);
                  tmp.add(id);
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }

     /**
     * Get the next free workflows_ID
     * @return the next viable workflows_ID
     */
    public int getNextWorkflowsID() {
       //--Get the highest properties_id
        int id=0;
        try {
            String query="SELECT MAX(workflows_id) FROM Workflows;";
            ResultSet rs=db.executeQuery(query);
            
            if (rs==null) {
                System.err.println("ERROR: Unable to get next Workflow id");
                return 0;
            }
            if (rs.next()) {
                id=rs.getInt(1);
                id++; //Increase id by 1 to get the next
            }
            rs.close();
        } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return 0;}
    return id;
    }

    public String getWorkflowsName(int id) {
       try {
            String query = String.format("SELECT workflows_name FROM Workflows WHERE workflows_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                  String name=decode(rs.getString(1));
                  rs.close();
                  return name;
            }
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return "";}
       return "";
    }

     /**
      * This create a workflow in the database
      * @param work
      * @return true if the workflows was created
      */
     public boolean addWorkflows(Workflows work) {
         //if (work.getWorkflow()==null) return false; //No workflow, return
         //int id=(work.getId()==0?this.getNextWorkflowsID():work.getId());
          int id=0;
            if (work.getId()==0) {
                id=this.getNextWorkflowsID();
            } else {
                id=work.getId();
            }
         try {
             //--The workflow should already be in work.getWorkflow_in_txt()
             if (work.getDate_modified()==null) work.setDate_modified(Util.returnCurrentDateAndTime());
             if (work.getDate_created()==null) work.setDate_created(Util.returnCurrentDateAndTime());
             String query=String.format("INSERT INTO Workflows(workflows_id,workflows_name, workflow_in_txt,date_created, note, workflows_filename, displayLINE, simpleGraph, workflows_outputText) VALUES ('%d','%s','%s','%s', '%s','%s','%s','%s','%s');",id,work.getName(),encode(work.getWorkflow_in_txt()),Util.returnCurrentDateAndTime(),encode(work.getNote()), work.getWorkflows_filename(), (work.isDisplayLINE()?"True":"False"), (work.isSimpleGraph()?"True":"False"), encode(work.getWorkflows_outputText()));
             db.execute(query);
             work.setId(id);
         } catch (Exception e) {
             if (isDebug()) Config.log("Unable to create workflow in database");
             Config.log("Error DB : "+e.getMessage());
             return false;
         }         
         return true;
     }

     public boolean removeWorkflows(Workflows work) {
        if (work.getId()==0) return false;
        try {
              String rm = String.format("DELETE FROM Workflows WHERE workflows_id='%d';", work.getId());
              db.execute(rm);
        } catch (Exception e) {
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            return false;
        }
        return true;
    }

      /**
      * This load to the workflow the current workflow
       * Also create the workflows object
       * Note: the wofkflow arg. can be set to null to only load the information
      * @param workflow
      * @return 
      */
     public Workflows getWorkflows(int workflows_id) {
         Workflows tmp=new Workflows();
         
         try {
                String query=String.format("SELECT workflows_id,workflows_name, workflow_in_txt,date_created, date_modified, note, workflows_filename, displayLINE, simpleGraph,workflows_outputText FROM Workflows WHERE workflows_id='%d';", workflows_id);
                ResultSet rs=db.executeQuery(query);
                if (rs.next()) {
                   tmp.setId(rs.getInt(1));
                   tmp.setName(rs.getString(2));
                   tmp.setWorkflow_in_txt(decode(rs.getString(3)));
                   tmp.setDate_created(rs.getString(4));
                   tmp.setDate_modified(rs.getString(5));
                   tmp.setNote(decode(rs.getString(6)));
                   tmp.setWorkflows_filename(rs.getString(7));
                   tmp.setDisplayLINE(Boolean.valueOf(rs.getString(8)));
                   tmp.setSimpleGraph(Boolean.valueOf(rs.getString(9)));
                   tmp.setWorkflows_outputText(decode(rs.getString(10)));
                }
                rs.close();
         } catch (Exception e) {
             if (debug) {
                 Config.log("Unable to load workflow in database : "+workflows_id);
                 Config.log("Error DB : "+e.getMessage());
             }
             
         }
         return tmp;
     }

     public boolean updateWorkflows(Workflows work) {
         if (work.getId()==0) return false;
         removeWorkflows(work);
         return addWorkflows(work);
     }

     ///////////////////////////////////////////////////////////////////////////


     @Deprecated
     public String getSequenceInfo(int sequenceID) {
         InfoSequence tmp=getInfoSequence(sequenceID);
         return (tmp==null?"":tmp.toString());
     }

     public InfoSequence getInfoSequence(int sequence_id) {
        InfoSequence tmp=new InfoSequence();
        try {
            String query = String.format("SELECT name,abbreviate, accession, accessionReferee, gi, sequence_type,sequence_len FROM Sequence WHERE sequence_id='%d';", sequence_id);
            ResultSet rs = db.executeQuery(query);
            if (rs.next()) {
                tmp.setName(decode(rs.getString(1)));
                tmp.setAbbreviate(decode(rs.getString(2)));
                tmp.setAccession(rs.getString(3));
                tmp.setAccession_referee(rs.getString(4));
                tmp.setGi(rs.getString(5));
                //tmp.setNote(defilter(rs.getString(6)));
                //tmp.setTimeAdded(rs.getString(6));
                tmp.setSequence_type(rs.getString(6));
                tmp.setLen(rs.getInt(7));
                tmp.setId(sequence_id);
            }
            rs.close();
        } catch (Exception e) {
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
         }
        return tmp;
    }

     public InfoMultipleSequences getInfoMultipleSequence(int MultipleSequenceID) {
         InfoMultipleSequences tmp=new InfoMultipleSequences();
         Vector<Integer>Sequence_id=new Vector<Integer>();
         try {
            String query = String.format("SELECT name, note, sequence_id FROM MultipleSequences WHERE MultipleSequences_id='%d';", MultipleSequenceID);
            ResultSet rs = db.executeQuery(query);
            if (rs.next()) {
                 tmp.setName(decode(rs.getString(1)));
                 tmp.setNote(decode(rs.getString(2)));
            }
            rs.close();
            String query2 = String.format("SELECT sequence_id FROM MultipleSequences WHERE MultipleSequences_id='%d';", MultipleSequenceID);
            rs = db.executeQuery(query2);
            while (rs.next()) {   
                  Sequence_id.add(rs.getInt(1));
            }
            rs.close();
          } catch(Exception e) {Config.log("Error DB : "+e.getMessage());}
         for (Integer id:Sequence_id) {
             if (this.existsSequence(id)) tmp.add(this.getInfoSequence(id));
         }
         tmp.setId(MultipleSequenceID);
         return tmp;
     }

     public Vector<Integer> getSequenceIDinMultipleSequence(int MultipleSequenceID) {
         Vector<Integer>info=new Vector<Integer>();         //return variable
         int count=0;            //Counter for the number of Sequence
         
         try {
            String query = String.format("SELECT sequence_id FROM MultipleSequences WHERE MultipleSequences_id='%d';", MultipleSequenceID);
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int sequenceID=rs.getInt(1);
                  info.add(sequenceID);
                  count++;
            }
            rs.close();
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return info;}
         return info;
     }

     public Vector<Integer> getSequenceIDinAlignment(int AlignmentID) {
         Vector<Integer>info=new Vector<Integer>();         //return variable
         int count=0;            //Counter for the number of Sequence
         
         try {
            String query = String.format("SELECT sequence_id FROM Alignment WHERE alignment_id='%d';", AlignmentID);
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int sequenceID=rs.getInt(1);
                  info.add(sequenceID);
                  count++;
            }
            rs.close();
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return info;}
         return info;
     }

     public Vector<Integer> getAlignmentIDinMultipleAlignments(int MultipleAlignmentsID) {
         Vector<Integer>info=new Vector<Integer>();         //return variable
         int count=0;            //Counter for the number of Sequence

         try {
            String query = String.format("SELECT alignment_id FROM MultipleAlignments WHERE multiplealignments_id='%d';", MultipleAlignmentsID);
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int alignmentID=rs.getInt(1);
                  info.add(alignmentID);
                  count++;
            }
            rs.close();
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return info;}
         return info;
     }

     ///////////////////////////////////////////////////////////////////////////


      public boolean existsTree(int id) {
       try {
            String query = String.format("SELECT tree_id FROM Tree WHERE tree_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                rs.close();
                return true;
            }
         } catch(Exception e) {return false;}
       return false;
    }

     /**
      * Return a list of all Tree ID
      * @return
      */
     public Vector<Integer> getAllTreeID() {
         Vector<Integer>tmp=new Vector<Integer>();
         if (!tables_found.containsKey("Tree")) return tmp;
         try {
            String query = "SELECT distinct(tree_id) FROM Tree;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                   tmp.add(rs.getInt(1));
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }
      
    

     /**
      *
      * @return The name of all the Group (MultipleSequence)
      */
     public Vector<String> getGroup() {
         Vector<String>tmp=new Vector<String>();
         try {
            String query = "SELECT MultipleSequences_id, Name, Note,  COUNT(Sequence_id) FROM MultipleSequences GROUP BY MultipleSequences_id;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  String S=String.format("MultipleSequences: %d\t %s %s - %d Sequence(s)", rs.getInt(1), decode(rs.getString(2)), decode(rs.getString(3)), rs.getInt(4));
                  tmp.add(S);
            }
            rs.close();
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return tmp;}
         return tmp;
     }

       /**
      *
      * @return The name of all the Group (MultipleSequence)
      */
     public Vector<String> getAlignmentGroup() {
         Vector<String>tmp=new Vector<String>();
         try {
            String query = "SELECT Alignment_id, Name, Note,  COUNT(Sequence_id) FROM Alignment GROUP BY Alignment_id;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  String S=String.format("Alignment: %d\t %s %s - %d Sequence(s)", rs.getInt(1), decode(rs.getString(2)), decode(rs.getString(3)), rs.getInt(4));
                  tmp.add(S);
            }
            rs.close();
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return tmp;}
         return tmp;
     }

        /**
      *
      * @return The name of all the Group (MultipleSequence)
      */
     public Vector<String> getMultipleAlignmentsGroup() {
         Vector<String>tmp=new Vector<String>();
         try {
            String query = "SELECT MultipleAlignments_id, Name, Note,  COUNT(Alignment_id) FROM MultipleAlignments GROUP BY MultipleAlignments_id;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  String S=String.format("MultipleAlignments: %d\t %s %s - %d Alignment(s)", rs.getInt(1), decode(rs.getString(2)), decode(rs.getString(3)), rs.getInt(4));
                  tmp.add(S);
            }
            rs.close();
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return tmp;}
         return tmp;
     }

      /**
      *
      * @return The name of all the Group (MultipleSequence)
      */
     @Deprecated
     public Vector<String> getTreeGroup() {
         Vector<String>tmp=new Vector<String>();
         try {
            String query = "SELECT MultipleTrees_id, Name, Note,  COUNT(Tree_id) FROM MultipleTrees GROUP BY MultipleTrees_id;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  String S=String.format("Group: %d\t %s %s - %d Tree(s)", rs.getInt(1), decode(rs.getString(2)), decode(rs.getString(3)), rs.getInt(4));
                  tmp.add(S);
            }
            rs.close();
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return tmp;}
         return tmp;
     }


     ///////////////////////////////////////////////////////////////////////////

     public int getNextAlignmentID() {
         //--Get the highest MultipleSequences_id
        int Alignment_id=0;       
        while(Alignment_id==0) {
            try {
                String query="SELECT MAX(Alignment_id) FROM Alignment;";
                ResultSet rs=db.executeQuery(query);
                 if (rs==null) {
                    System.err.println("ERROR: Unable to get next Alignment id");
                    return 0;
                }      
                if (rs.next()) {
                    Alignment_id=rs.getInt(1);
                    Alignment_id++; //Increase id by 1 to get the next one
                }
                rs.close();
            } catch(Exception e) {}

        }
        return Alignment_id;
    }
          
     public Vector<Integer> getAllAlignmentID() {
         Vector<Integer>tmp=new Vector<Integer>();
         if (!tables_found.containsKey("Alignment")) return tmp;
         try {
            String query = "SELECT DISTINCT(Alignment_id) FROM Alignment;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int id=rs.getInt(1);
                  tmp.add(id);
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }

     public Vector<Integer> getAllAlignmentID(String search) {
         Vector<Integer>tmp=new Vector<Integer>();
         if (!tables_found.containsKey("Alignment")) return tmp;
         try {
            String query = "SELECT DISTINCT(alignment_id) FROM alignment WHERE REGEXP('"+search+"',name);";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int id=rs.getInt(1);
                  tmp.add(id);
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }



    /**
      *
      * @return The name of all the Alignment
      */
     @Deprecated
     public Vector<String> getAlignmentNames() {
         Vector<String>tmp=new Vector<String>();
         if (!tables_found.containsKey("Alignment")) return tmp;
         try {
            String query = "SELECT Alignment_id, Name, Note, COUNT(Sequence_id) FROM Alignment GROUP BY Alignment_id;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  String S=String.format("Alignment: %d\t %s %s - %d Sequence", rs.getInt(1), rs.getString(2), decode(rs.getString(3)), rs.getInt(4));
                  tmp.add(S);
            }
            rs.close();
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return tmp;}
         return tmp;
     }

//        public InfoAlignment getInfoAlignment(int AlignmentID) {
//         InfoAlignment tmp=new InfoAlignment();
//         Vector<Integer>Sequence_id=new Vector<Integer>();
//         try {
//            String query = String.format("SELECT name, note, sequence_id FROM Alignment WHERE Alignment_id='%d';", AlignmentID);
//            ResultSet rs = db.executeQuery(query);
//            if (rs.next()) {
//                 tmp.setName(decode(rs.getString(1)));
//                 tmp.setNote(decode(rs.getString(2)));
//            }
//            rs.close();
//            String query2 = String.format("SELECT sequence_id FROM Alignment WHERE Alignment_id='%d';", AlignmentID);
//            rs = db.executeQuery(query2);
//            while (rs.next()) {
//                  Sequence_id.add(rs.getInt(1));
//            }
//            rs.close();
//          } catch(Exception e) {Config.log("Error DB : "+e.getMessage());}
//         for (Integer id:Sequence_id) {
//             if (this.existsSequence(id)) tmp.add(this.getInfoSequence(id));
//         }
//         tmp.setId(AlignmentID);
//         return tmp;
//     }

     /**
      *
      * @return The name of all the Alignment
      */
     public InfoAlignment getInfoAlignment(int alignment_id) {
         InfoAlignment tmp=new InfoAlignment();
          tmp.setId(alignment_id);
         try {
            String query = String.format("SELECT Name, Note, original_sequence_id, sequence_id,runProgram_id FROM Alignment WHERE Alignment_id='%d';",alignment_id);
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                 tmp.setName(decode(rs.getString(1)));
                 tmp.getOriginalSequence_id().add(rs.getInt(3));
                 tmp.setNote(decode(rs.getString(2)));
                 tmp.getSequence_id().add(rs.getInt(4));
                 tmp.setProgram_id(rs.getInt(5));
            }
            rs.close();
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());return tmp;}
         return tmp;
     }

     /**
      *
      * @return a Vector with a lot of informations for the results page
      */
     public Vector getAlignmentResultsInfos(Integer alignmentId){
         Vector v = new Vector();

         try{
             String query = String.format("SELECT * from", alignmentId);

         } catch(Exception e){
             if (isDebug()) Config.log("Error DB : "+e.getMessage());
         }

         return v;
     }
     
    
     ///////////////////////////////////////////////////////////////////////////


     ///////////////////////////////////////////////////////////////////////////


    /**
     * Get the next free properties ID
     * @return the next viable properties ID
     */
    public int getNextPropertiesID() {
       //--Get the highest properties_id
        int id=0;
        while(id==0) {
            try {
                String query="SELECT MAX(properties_id) FROM Properties;";
                ResultSet rs=db.executeQuery(query);
                if (rs==null) {
                    System.err.println("ERROR: Unable to get next Properties id");
                    return 0;
                }      
                if (rs.next()) {
                    id=rs.getInt(1);
                    id++; //Increase id by 1 to get the next
                }
                rs.close();
            } catch(Exception e) {}
        }
    return id;
    }

     
    public boolean addProperties(workflow_properties properties) {
        StringWriter st=new StringWriter();
        try  {
            properties.store(st, "");                 //Note: No custom header for small footprint
            properties.setProperties_id(saveProperties(properties, properties.getName()));
        } catch(Exception e) {Config.log("Unable to save properties to database");return false;}
        return (properties.getProperties_id()==0?false:true);
    }


    /**
     * loadProprieties
     * @param id
     * @param tmp (this is a pointer to the workflow_properties object!)
     * @return true if succesfull
     */
    public boolean loadProperties(int id, workflow_properties tmp) {
        //workflow_properties tmp=new workflow_properties();
        tmp.clear();
        tmp.setName("");
        tmp.setProperties_id(id);
        if (id<1) return false;
        try {
            String query=String.format("SELECT properties_name, key_name, key_value FROM Properties JOIN PropertiesValues USING (Properties_id) WHERE properties_id='%d';", id);
            ResultSet rs=db.executeQuery(query);
            //--Return only the properties
            while(rs.next()) {
                if (tmp.getName().equals("")) tmp.setName(rs.getString(1));
                tmp.put(decode(rs.getString(2)), decode(rs.getString(3)));
            }
            rs.close();
        } catch(Exception e) {return false;}
        return true; //--Normal ending
    }

    /**
     * Return the FIRST properties associated with a properties name
     * @param properties_name
     * @param tmp
     * @return true if succesfull
     */
     public boolean loadProperties(String name, workflow_properties tmp) {
        if (name.equals("")) return false;
        tmp.clear();
        tmp.setProperties_id(0);
        //tmp.setName(properties_name);
        try {
            //--Select the first id associated with this properties_name
            //-- If not found, return false;
            String query=String.format("SELECT properties_id FROM PropertiesValues WHERE Key_name='Name' AND Key_value='%s';", name);
             ResultSet rs=db.executeQuery(query);
            if (rs.next()) {
                tmp.setProperties_id(rs.getInt(1));
                rs.close();
               //tmp.name=name;
            } else return false;
           //--Get the properties associated
            if (tmp.getProperties_id()<1) return false;
            query=String.format("SELECT properties_name, key_name, key_value FROM Properties JOIN PropertiesValues USING (Properties_id) WHERE properties_id='%d';",tmp.getProperties_id());
            rs=db.executeQuery(query);
            //--Return only the properties
            while(rs.next()) {
                if (tmp.getName().equals("")) tmp.setName(rs.getString(1));
                tmp.put(decode(rs.getString(2)), decode(rs.getString(3)));
            }
            rs.close();
        } catch(Exception e) {return false;}
        return true; //--Normal ending
    }

    public workflow_properties getProperties(int properties_id) {
       try  {
           workflow_properties properties=new workflow_properties();
           if (loadProperties(properties_id, properties)) {
               return properties;
           } else return null;         
        } catch(Exception e) {Config.log("Unable to load properties from database");return null;}
    }


      /**
     * Save a properties String to the database
     * @param properties
     * @param properties_name
     * @return a properties_id
     */
    private int saveProperties(workflow_properties properties, String properties_name) {
       //--Get the highest properties_id
        int id=0;
        id= getNextPropertiesID();
        if (id==0) return 0;
        //--Insert properties
        db.setAutoCommit(false);
        String s=String.format("INSERT INTO Properties(properties_id, properties_name) VALUES ('%s','%s');",id, properties.getName());
        db.execute(s);

        Enumeration<Object> e=properties.keys();
        while(e.hasMoreElements()) {
              String key=(String)e.nextElement();
             //-- Debug Config.log(key+" \t "+properties.get(key));
             String ins=String.format("INSERT INTO PropertiesValues(properties_id, Key_name, Key_value) VALUES ('%s','%s','%s');",id, encode(key), encode(properties.get(key)));
             db.execute(ins);
         }
         db.commit();
        db.setAutoCommit(true);
        properties.setProperties_id(id);
        return id;
    }

     /**
     * Load all the .properties files from the speccified path into the database
     * Warning: Since there is no check for idem name in the database for properties
     * this should be called immediately after createDatabase
     * @param path
     */
    public void createProperties(String path) {
//        String[] filename=workflow_properties.loadPropertieslisting(path);
//        for (String f:filename) {
//            workflow_properties tmp=new workflow_properties();
//            tmp.load(path+f);
////            tmp.setName(f);
////            tmp.saveToDatabase();
//        }
    }

    ////////////////////////////////////////////////////////////////////////////


     public Vector<Integer> getAllInputID() {
         Vector<Integer>tmp=new Vector<Integer>();
          if (!tables_found.containsKey("Input")) return tmp;
         try {
            String query = "SELECT input_id FROM Input;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int id=rs.getInt(1);
                  tmp.add(id);
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }


    /**
     * Get the next free Input ID
     * @return the next viable Input ID
     */
    public int getNextInputID() {
       //--Get the highest properties_id
        int id=0;
        while(id==0) {
            try {
                String query="SELECT MAX(Input_id) FROM Input;";
                ResultSet rs=db.executeQuery(query);
                 if (rs==null) {
                    System.err.println("ERROR: Unable to get next Input id");
                    return 0;
                }      
                if (rs.next()) {
                    id=rs.getInt(1);
                    id++; //Increase id by 1 to get the next
                }
                rs.close();
            } catch(Exception e) {}
        }
    return id;
    }
    
    public int addInput (Input input){
      int id=(input.getId()==0?this.getNextInputID():input.getId());
        try {
             String i = String.format("INSERT INTO Input (Input_id, runProgram_id, type, typeid) VALUES('%d','%d','%s','%d');",id,input.getRunProgram_id(),input.getType(),input.getTypeid());
             db.execute(i);
             input.setId(id);
        } catch (Exception e) {
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            Config.log("Unable to add the Input  "+input);
            return 0;
        }
        return id;
    }

    public String getInputName(int id) {
       return "";
    }

     public Input getInput(int input_id) {
         Input input=new Input();
         try {
            String query = String.format("SELECT input_id, runProgram_id, type, typeid FROM Input WHERE input_id='%d';", input_id);
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                input.setId(rs.getInt(1));
                input.setRunProgram_id(rs.getInt(2));
                input.setType(rs.getString(3));
                input.setTypeid(rs.getInt(4));
            }
            rs.close();
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());}
        return input;
    }

     public boolean updateInput(Input input) {
        if (input.getId()==0) return false;
        removeInput(input);
        return (addInput(input)==0?false:true);
    }

     /**
     * Remove a input data from Input table by its id
     */
    public boolean  removeInput (Input input){
        if (input.getId()==0) return false;
        try {
              //--Remove from Sequence
              String rm = String.format("DELETE FROM Input WHERE input_id='%d';",input.getId());
              db.execute(rm);
              return true;
        } catch (Exception e) {
            if (isDebug()) {
                Config.log("Error DB : "+e.getMessage());
            }
            return false;
        }
    }

     public Vector<Input>getInput(RunProgram run) {
        Vector<Input>tmp=new Vector<Input>();        
        Vector<Integer>ids=new Vector<Integer>();
        try {
            //--Get a list of ids
            String query = String.format("SELECT input_id FROM Input WHERE RunProgram_id='%d';", run.getId());
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {ids.add(rs.getInt(1));}
            rs.close();
            //--Load output
            for (Integer id:ids) tmp.add(new Input(id));
        } catch(Exception e) {Config.log("Error DB : "+e.getMessage());}
        return tmp;
    }


    ////////////////////////////////////////////////////////////////////////////

     public boolean existsOutput(int id) {
       try {
            String query = String.format("SELECT Output_id FROM Output WHERE Output_id='%d';", id);
            ResultSet rs = db.executeQuery(query);
            if (rs!=null&&rs.next()) {
                rs.close();
                return true;
            }
         } catch(Exception e) {return false;}
       return false;
    }

     public Vector<Integer> getAllOutputID() {
         Vector<Integer>tmp=new Vector<Integer>();
          if (!tables_found.containsKey("Output")) return tmp;
         try {
            String query = "SELECT Output_Id FROM Output;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int id=rs.getInt(1);
                  tmp.add(id);
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }

    /**
     * Get the next free Output ID
     * @return the next viable Output ID
     */
    public int getNextOutputID() {
       //--Get the highest properties_id
        int id=0;
        while(id==0) {
            try {
                String query="SELECT MAX(Output_id) FROM Output;";
                ResultSet rs=db.executeQuery(query);
                 if (rs==null) {
                    System.err.println("ERROR: Unable to get next Output id");
                    return 0;
                }      
                if (rs.next()) {
                    id=rs.getInt(1);
                    id++; //Increase id by 1 to get the next
                }
                rs.close();
            } catch(Exception e) {}
        }
    return id;
    }

     public int addOutput (Output output){
      int id=(output.getId()==0?this.getNextOutputID():output.getId());
        try {
             String i = String.format("INSERT INTO Output (output_id, runProgram_id, type, typeid) VALUES('%d','%d','%s','%d');",id,output.getRunProgram_id(),output.getType(),output.getTypeid());
             db.execute(i);
             output.setId(id);
        } catch (Exception e) {
            if (isDebug()) Config.log("Error DB : "+e.getMessage());
            Config.log("Unable to add the Unknown "+output);
            return 0;
        }
        return id;
    }

     public String getOutputName(int id) {       
       return "";
    }

     public Output getOutput(int output_id) {
         Output output=new Output();
         try {
            String query = String.format("SELECT output_id, runProgram_id, type, typeid FROM Output WHERE output_id='%d';", output_id);
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                output.setId(rs.getInt(1));
                output.setRunProgram_id(rs.getInt(2));
                output.setType(rs.getString(3));
                output.setTypeid(rs.getInt(4));
            }
            rs.close();
         } catch(Exception e) {Config.log("Error DB : "+e.getMessage());}
        return output;
    }

     public boolean updateOutput(Output output) {
        if (output.getId()==0) return false;
        removeOutput(output);
        return (addOutput(output)==0?false:true);
    }

     /**
     * Remove an output data from Output table by its id
     */
    public boolean  removeOutput (Output output){
        if (output.getId()==0) return false;
        try {
              //--Remove from Sequence
              String rm = String.format("DELETE FROM Output WHERE output_id='%d';",output.getId());
              db.execute(rm);
              return true;
        } catch (Exception e) {
            if (isDebug()) {
                Config.log("Error DB : "+e.getMessage());
            }
            return false;
        }
    }


    public Vector<Output>getOutput(RunProgram run) {
        Vector<Output>tmp=new Vector<Output>();
        Vector<Integer>ids=new Vector<Integer>();
        try {
            //--Get a list of ids
            String query = String.format("SELECT output_id FROM Output WHERE RunProgram_id='%d';", run.getId());
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {ids.add(rs.getInt(1));}
            rs.close();
            //--Load output
            for (Integer id:ids) tmp.add(new Output(id));
        } catch(Exception e) {Config.log("Error DB : "+e.getMessage());}
        return tmp;
    }


     ///////////////////////////////////////////////////////////////////////////

     public int getNextBlastHitID() {
         //--Get the highest MultipleSequences_id
        int Alignment_id=0;
        while(Alignment_id==0) {
            try {
                String query="SELECT MAX(BlastHIt_id) FROM BlastHit;";
                ResultSet rs=db.executeQuery(query);
                if (rs==null) {
                    System.err.println("ERROR: Unable to get next BlastHit id");
                    return 0;
                }      
                if (rs.next()) {
                    Alignment_id=rs.getInt(1);
                    Alignment_id++; //Increase id by 1 to get the next one
                }
                rs.close();
            } catch(Exception e) {}

        }
        return Alignment_id;
    }

     public Vector<Integer> getAllBlastHitID(String search) {
         Vector<Integer>tmp=new Vector<Integer>();
          if (!tables_found.containsKey("BlastHit")) return tmp;
         try {
            String query = "SELECT BlastHit_id FROM BlastHit;";
            ResultSet rs = db.executeQuery(query);
            while (rs.next()) {
                  int id=rs.getInt(1);
                  tmp.add(id);
            }
            rs.close();
         } catch(Exception e) {return tmp;}
         return tmp;
     }

//     public int addBlastHit (BlastHit bh){
//      int id=(bh.getId()==0?this.getNextBlastHitID():bh.getId());
//        try {
//            String i = String.format("INSERT INTO BlastHit (BlastHit_id,BlastHitList_id,dbname,subject_id,subject_id_gi,subject_accession,subject_accession_referree,subject_name,subject_length,"+
//                                                            "sstart,ssend,subject_sequence,query_id,query_name,qstrand,sstrand,qstart,qend,evalue,bitscore,identify,alignment_length,query_length,query_sequence,"+
//                                                            "positives,missmatches,gap,score,runProgram_id)"+
//                                                            " VALUES "+
//                                                            "('%d','%d','%s','%d','%s', ;",
//                     
//
//
//
//
//                     id,output.getRunProgram_id(),output.getType(),output.getTypeid());
//             db.execute(i);
//             output.setId(id);
//        } catch (Exception e) {
//            if (isDebug()) Config.log("Error DB : "+e.getMessage());
//            Config.log("Unable to add the Unknown "+output);
//            return 0;
//        }
//        return id;
//    }







     ///////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////
    /// Various functions
    /// Note: All access to the database should be via databaseFunction
    /// However, if needed, a pointer to the database can be found using:
    /// getDatabase()


    @Deprecated
    public void setAutoCommit(boolean b) {
        this.db.setAutoCommit(b);
    }

    @Deprecated
    public ResultSet executeQuery(String query) {
        return db.executeQuery(query);
    }

    @Deprecated
    public boolean execute(String query) {
        return db.execute(query);
    }

    
    /**
     *
     * @return
     */
    public database getDatabase() {
        return db;
    }   

    /**
     * @return the mode of the databaseFunction
     */
    public static boolean isDebug() {
        return debug;
    }

    /**
     * Set the database in debugMode
     * @param true or false
     */
    public static void setDebug(boolean aDebug) {
        debug = aDebug;
    }

    public boolean isDatabaseChanged() {
        return db.isChanged();
    }
} //End database function class
