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
import configuration.Config;
import java.sql.*;
import org.sqlite.*;
import java.io.*;
import org.apache.regexp.RE;

/**
 * This is a version of the database use if we need more than the static database
 *
 * @author Etienne Lord
 * @since June 2010
 */
public class database_local {
      
    public String dbFileName="";                     // Fichier par defaut
    public Connection conn;                          // Connection a la database
    public Statement stmt;                           // Classe interface a la connection
    public Statement batch_stmt;                    // Batch statement
    public PreparedStatement pre_stmt;
    public ResultSet rs;                            // Pour acc�s facile, on permet public
    public ResultSetMetaData rsMetaData;            // Pour acc�s facile, on permet public
    public boolean autocommit=true;                 // Valeur par default
    public String dbSQLerror="";
    private static boolean debug=false;
    private boolean changed=true;
    private static boolean ready=true;

    public database_local() {
        connect();
    }
        
    /**
     * 
     * @param autocommit_mode
     */
    public database_local(boolean autocommit_mode) {
       connect();
       this.setAutoCommit(autocommit_mode);
    }
    
    public database_local(String dbname) {
        this.dbFileName=dbname;
        connect();
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// Display status on screen or console
    private void print_status(String stri) {
        Config.log(stri);
    }
    
    private void print_status() {
        Config.log("\n");
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// CONNECTION FUNCTIONS
    public boolean connect() {
        boolean flag=true;
        if (!FileExists(dbFileName)) {
            //Database don't exists print message
            if (isDebug()) print_status("Warning. File "+dbFileName+" not found. \n It will be created but will be empty.");
            flag=false;
        }
        //--Ensure only one database object
        if (conn!=null) return true;
        try {
            Class.forName("org.sqlite.JDBC");
            if (isDebug()) print_status("Connection to : " + dbFileName);
            conn = DriverManager.getConnection("jdbc:sqlite:"+dbFileName);
            stmt = conn.createStatement();
            batch_stmt = conn.createStatement();
            pre_stmt = conn.prepareStatement(""); //dummy
            conn.setAutoCommit(autocommit);
           //--Create REGEXP Functiom
           if (!createRegexp()) {
                if (isDebug()) Config.log("Warning: Unable to implement REGEXP(regex, string);");
            }
            if (conn==null) {
                Config.log("Warning. Unable to create connection driver to "+dbFileName);
            }
        } catch (Exception e) {
               print_status("Error: unable to connect to: "+dbFileName+" "+e.getMessage());
                e.printStackTrace();
               flag=false;
        }
        return flag;
    }
    
    public boolean connectInMemory() {
        try {
            Class.forName("org.sqlite.JDBC");
            if (isDebug()) print_status("Creating in memory db : ");
            conn = DriverManager.getConnection("jdbc:sqlite:memory");
            stmt = conn.createStatement();
            conn.setAutoCommit(autocommit);
            //--Create REGEXP Functiom
            if (!createRegexp()) {
                if (isDebug()) Config.log("Warning: Unable to implement REGEXP(regex, string);");
            }
        } catch (Exception e) {
            if (isDebug()) print_status("Error: unable to create memory database");
            return false;
        }
        return true;
    }
    
    public void setAutoCommit(boolean value) {
        autocommit=value;
        try {
            conn.setAutoCommit(autocommit);
        } catch (Exception e) {
            if (isDebug()) print_status("Unable to set AutoCommit");}
    }
    
    public boolean commit() {
        try {
            conn.commit();
        } catch (Exception e) {
             dbSQLerror=e.getMessage();
            if (isDebug()&&!dbSQLerror.equals("not an error")) Config.log(dbSQLerror);
            return false;
        }
        return true;
    }
    
    public boolean close() {
        if (isDebug()) print_status("Closing "+ dbFileName+" ");
        try {
            stmt.close();
            conn.close();
            conn=null;
        } catch(Exception e) {
            if (isDebug()) print_status("Error in closing database!");return false;}
            return true;
    }
    
    public boolean execute(String command) {
        dbSQLerror="";
        changed=true;
        try {
            if (debug) {
//                if (command.startsWith("INSERT")) {
//                    int command_len=command.length();
//                    print_status(command.substring(0, (command_len>254?254:command_len))); //Not too long line...
//                } else
                    print_status(command);
            }            
            boolean b=false;
            int i=0;
            //--Blocking / Waiting for its turn
//            if (!ready) while(!ready){
////                if (debug) {
////                    i++;
////                     if (i%1000000==0&&i!=0) {
////                        Config.log("db lock in not ready? "+i);
////                        Config.log("execute:"+command);
////                    }
////                }
//            }
//            if (debug&&i>10) Config.log("db execute delay"+i++);
                boolean continueTrying=true;
            while(continueTrying) {
                try {
                     b=stmt.execute(command);
                    continueTrying=false;
                } catch(Exception e) {
                      dbSQLerror=e.getMessage();
                      Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tError "+dbSQLerror+" with query : "+command);                      
                      Thread.currentThread().sleep(100);
                      i++;
                      if (i>10) continueTrying=false;
                }
            }
            return b;
        }       
        catch (Exception ex) {
            ready=true;
            dbSQLerror=ex.getMessage();
            if (isDebug()) Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExecute error: "+dbSQLerror);
            if (isDebug()) Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExecute command: "+command);
            //if (isDebug()) ex.printStackTrace();
            return false;
        }
    }
    
    public void preparedStatement(String command) {
        try {
            pre_stmt=conn.prepareStatement(command);
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
            if (isDebug()) print_status(command);
        }
    }
    
    public void executepreparedStatement() {
        dbSQLerror="";
        try {
            pre_stmt.execute();
        } catch (Exception e) {
            dbSQLerror=e.getMessage();
            if (isDebug()) Config.log(dbSQLerror);
        }
    }
    
    public void addBatchpreparedStatement() {
        try {
            pre_stmt.addBatch();
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
        }
    }
    
    public void commitBatchpreparedStatement() {
        try {
            pre_stmt.executeBatch();
            this.commit();
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
        }
    }
    
    public void addBatchStatement(String command) {
        try {
            batch_stmt.addBatch(command);
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
            if (isDebug()) print_status(command);
        }
    }
    
    public void clearBatchStatement() {
        try {
            batch_stmt.clearBatch();
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
        }
    }
    
    public void commitBatchStatement() {
        try {
            batch_stmt.executeBatch();
            this.commit();
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
        }
    }
    
    public ResultSet executeQuery(String query) {
        changed=false;       
        ResultSet rsq=null;
        try {
         if (debug) Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+query);
            //if (rs!=null&&!rs.isClosed()) while(!rs.isClosed()) {}
            int i=0;
              //--Blocking / Waiting for its turn
//            if (!ready) while(!ready){
            boolean continueTrying=true;
            while(continueTrying) {
                try {
                    rsq=stmt.executeQuery(query);
                    continueTrying=false;
                } catch(Exception e) {
                      dbSQLerror=e.getMessage();
                      Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tError "+dbSQLerror+" with query : "+query);                      
                      Thread.currentThread().sleep(100);
                      i++;
                      if (i>10) continueTrying=false;
                }
            }

//                //               if (debug) {
////                    i++;
////                     if (i%1000000==0&&i!=0) {
////                        Config.log("db lock in not ready? "+i);
////                        Config.log("executeQuery:"+query);
////                    }
////                }
//            }
//            if (debug&&i>10)Config.log("db executeQuery delay"+i++);
//            ready=false;
//            rsq=stmt.executeQuery(query);
//            ready=true;
         } catch (Exception ex) {
             ready=true;
            if (rsq==null&&debug) Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\trs was null on : "+query);
            if (stmt==null&&debug) Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tstmt was null on : "+query);
            if (query==null&&debug) Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tQuery was null");
             dbSQLerror=ex.getMessage();
             Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tError "+dbSQLerror+" with query : "+query);
            return null;
        }
        //--Note: was usefull for table column id but not in armadillo
        try {
            if (rsq!=null) rsMetaData = rsq.getMetaData();
        } catch(Exception e) {
            if (isDebug()) e.printStackTrace();
            rsMetaData=null; return null;}        
       return rsq;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// HELPER FUNCTIONS
    
    boolean FileExists(String filename) {
        File f = new File(filename);
        return f.exists();
    }

    public boolean isConnected() {
         return (conn==null);
    }

    ////////////////////////////////////////////////////////////////////////////
    /// REGEX Function

   /**
    * This create the SQLite Function
    * REGEXP(regex, string) returning a value of True or False
    * The REGEX Function used is from the apache.jakarta.regexp
    * @return
    */
   private boolean createRegexp() {
       try {
       // CREATE REGEXP FUNCTION
            //
            Function.create(conn, "REGEXP", new Function() {
            //--Regexp W(regex, string)
            //--Example: SELECT Name FROM Sequence WHERE REGEXP('ATG[AG]T',Sequence);
            String last_regexp="";
            RE regex;
            protected void xFunc() throws SQLException {
                if (!value_text(0).equals(last_regexp)) {
                    regex=new RE(value_text(0),RE.MATCH_CASEINDEPENDENT);
                    last_regexp=value_text(0);
                }
                result((regex.match(value_text(1))?1:0));
            }
            });
            return true;
       } catch(Exception e) {e.printStackTrace();return false;}
   }

   public boolean isChanged() {
       return changed;
   }

    /**
     * @return the debug
     */
    public static boolean isDebug() {
        return debug;
    }

    /**
     * @param aDebug the debug to set
     */
    public static void setDebug(boolean aDebug) {
        debug = aDebug;
    }

}
