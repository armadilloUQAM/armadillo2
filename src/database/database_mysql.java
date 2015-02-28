/*
 *  Armadillo Workflow System v1.0
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

public class database_mysql extends database {


   
    private String mysql_dbFileName="";                     // Fichier par defaut
    private Connection mysql_conn;                          // Connection a la database
    private Statement mysql_stmt;                           // Classe interface a la connection
    private Statement mysql_batch_stmt;                    // Batch statement
    private PreparedStatement mysql_pre_stmt;
    private ResultSet mysql_rs;                            // Pour acc�s facile, on permet public
    private ResultSetMetaData mysql_rsMetaData;            // Pour acc�s facile, on permet public
    private boolean mysql_autocommit=true;                 // Valeur par default
    private String mysql_dbSQLerror="";
    private static boolean mysql_debug=false;
    private static boolean mysql_changed=true;
    private static boolean mysql_ready=true;
    private String mysql_server="Localhost";
    private String mysql_username="";
    private String mysql_password="";
    
    public database_mysql() {
        if (!mysql_username.isEmpty()&&!dbFileName.isEmpty())
                connect();
    }

    /**
     *
     * @param autocommit_mode
     */
    public database_mysql(boolean autocommit_mode) {
       connect();
       this.setAutoCommit(autocommit_mode);
    }

    public database_mysql(String dbname) {
        this.mysql_dbFileName=dbname;
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
    
    @Override
    public boolean connect() {
        boolean flag=true;
//        if (!FileExists(dbFileName)) {
//            //Database don't exists print message
//            if (isDebug()) print_status("Warning. File "+dbFileName+" not found. \n It will be created but will be empty.");
//            flag=false;
//        }
        //--Ensure only one database object
        if (mysql_conn!=null) return true;
        try {
            if (isDebug()) print_status("Connection to : " + mysql_dbFileName);
            //mysql
            Class.forName("com.mysql.jdbc.Driver");
            String str_connect=String.format("jdbc:mysql://%s/%s", mysql_server, this.mysql_dbFileName, mysql_username, mysql_password);
            
            mysql_conn = DriverManager.getConnection(str_connect, mysql_username, mysql_password);
            
            //postGreSQL
//            Class.forName("org.postgresql.Driver");
//            conn = DriverManager.getConnection("jdbc:postgresql://localhost/mirnas",
//                                    "postgres", "1a2z3e");

            mysql_stmt = mysql_conn.createStatement();
            mysql_batch_stmt = mysql_conn.createStatement();
            mysql_pre_stmt = mysql_conn.prepareStatement(""); //dummy
            mysql_conn.setAutoCommit(mysql_autocommit);
           //--Create REGEXP Functiom
           
            if (mysql_conn==null) {
                Config.log("Warning. Unable to create connection driver to "+getDbFileName());
                flag=false;
            }
        } catch (Exception e) {
               print_status("Error: unable to connect to: "+getDbFileName()+" "+e.getMessage());
                e.printStackTrace();
               flag=false;
        }
        return flag;
    }

//    public boolean connectInMemory() {
//        try {
//            Class.forName("org.sqlite.JDBC");
//            if (isDebug()) print_status("Creating in memory db : ");
//            conn = DriverManager.getConnection("jdbc:sqlite:memory");
//            stmt = conn.createStatement();
//            conn.setAutoCommit(autocommit);
//            //--Create REGEXP Functiom
//            if (!createRegexp()) {
//                if (isDebug()) Config.log("Warning: Unable to implement REGEXP(regex, string);");
//            }
//        } catch (Exception e) {
//            if (isDebug()) print_status("Error: unable to create memory database");
//            return false;
//        }
//        return true;
//    }

    @Override
    public void setAutoCommit(boolean value) {
        mysql_autocommit=value;
        try {
            mysql_conn.setAutoCommit(mysql_autocommit);
        } catch (Exception e) {
            if (isDebug()) print_status("Unable to set AutoCommit");}
    }

    @Override
    public boolean commit() {
        try {
            mysql_conn.commit();
        } catch (Exception e) {
             mysql_dbSQLerror=e.getMessage();
            if (isDebug()&&!mysql_dbSQLerror.equals("not an error")) Config.log(mysql_dbSQLerror);
            return false;
        }
        return true;
    }

   
   
    
    @Override
    public boolean close() {
        if (isDebug()) print_status("Closing "+ dbFileName+" ");
        try {
            mysql_stmt.close();
            mysql_conn.close();
            mysql_conn=null;
        } catch(Exception e) {
            if (isDebug()) print_status("Error in closing database!");return false;}
            return true;
    }
    
    @Override
    public boolean execute(String command) {
        dbSQLerror="";
        mysql_changed=true;
        Statement stmt_local=null;
        try {
             stmt_local=mysql_conn.createStatement();
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
                     b=stmt_local.execute(command);
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
            mysql_ready=true;
            dbSQLerror=ex.getMessage();
            if (isDebug()) Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExecute error: "+dbSQLerror);
            if (isDebug()) Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExecute command: "+command);
            //if (isDebug()) ex.printStackTrace();
            return false;
        }
    }
    
    @Override
    public void preparedStatement(String command) {
        try {
            mysql_pre_stmt=conn.prepareStatement(command);
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
            if (isDebug()) print_status(command);
        }
    }
    
    @Override
    public void executepreparedStatement() {
        dbSQLerror="";
        try {
            mysql_pre_stmt.execute();
        } catch (Exception e) {
            dbSQLerror=e.getMessage();
            if (isDebug()) Config.log(dbSQLerror);
        }
    }
    
    @Override
    public void addBatchpreparedStatement() {
        try {
            mysql_pre_stmt.addBatch();
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
        }
    }
    
    @Override
    public void commitBatchpreparedStatement() {
        try {
            mysql_pre_stmt.executeBatch();
            this.commit();
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
        }
    }
    
    @Override
    public void addBatchStatement(String command) {
        try {
            mysql_batch_stmt.addBatch(command);
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
            if (isDebug()) print_status(command);
        }
    }
    
    @Override
    public void clearBatchStatement() {
        try {
            mysql_batch_stmt.clearBatch();
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
        }
    }
    
    @Override
    public void commitBatchStatement() {
        try {
             mysql_batch_stmt.executeBatch();
            this.commit();
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
        }
    }
    
    @Override
    public ResultSet executeQuery(String query) {
         mysql_changed=false;       
        ResultSet rsq=null;
        Statement stmt_local=null;
        try {
            stmt_local= mysql_conn.createStatement();
         if (debug) Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+query);
            //if (rs!=null&&!rs.isClosed()) while(!rs.isClosed()) {}
            int i=0;
              //--Blocking / Waiting for its turn
//            if (!ready) while(!ready){
            boolean continueTrying=true;
            while(continueTrying) {
                try {
                    
                    rsq=stmt_local.executeQuery(query);
                    continueTrying=false;
                } catch(Exception e) {
                      dbSQLerror=e.getMessage();
                      if (debug) Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tError "+dbSQLerror+" with query : "+query);                      
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
              mysql_ready=true;
            if (rsq==null&&debug) Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\trs was null on : "+query);
            if (stmt_local==null&&debug) Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tstmt was null on : "+query);
            if (query==null&&debug) Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tQuery was null");
             dbSQLerror=ex.getMessage();
             if (debug) Config.log("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tError "+dbSQLerror+" with query : "+query);
            return null;
        }
        //--Note: was usefull for table column id but not in armadillo
        try {
            if (rsq!=null)  mysql_rsMetaData =  rsq.getMetaData();
        } catch(Exception e) {
            if (isDebug()) e.printStackTrace();
             mysql_rsMetaData=null; return null;}        
       return rsq;
    }
    


    ////////////////////////////////////////////////////////////////////////////
    /// REGEX Function

   /**
    * This create the SQLite Function
    * REGEXP(regex, string) returning a value of True or False
    * The REGEX Function used is from the apache.jakarta.regexp
    * @return
    */


    /**
     * @return the debug
     */
    public static boolean isDebug() {
        return mysql_debug;
    }

    /**
     * @param aDebug the debug to set
     */
    public static void setDebug(boolean aDebug) {
        mysql_debug = aDebug;
        debug=aDebug;
    }

    /**
     * @return the dbName
     */
    public String getDbName() {
        return mysql_dbFileName;
    }

    /**
     * @return the dbFileName
     */
    public String getDbFileName() {
        return mysql_dbFileName;
    }

    /**
     * @param dbFileName the dbFileName to set
     */
    public void setDbFileName(String dbFileName) {
        this.mysql_dbFileName = dbFileName;
    }

    /**
     * @return the server
     */
    public String getServer() {
        return mysql_server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        this.mysql_server = server;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return mysql_password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.mysql_password = password;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return mysql_username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.mysql_username = username;
    }
    
    /**
     * Set the MySQL database
     * @param db 
     */
    public void setDatabase(String db) {
        this.mysql_dbFileName=db;
    }
   
    
   @Override
   public boolean isConnected() {
         return (mysql_conn!=null);
    }

}
