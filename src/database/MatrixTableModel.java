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
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// QueryTableModel
/// Table model to handle ResultSet of a SQL Query
/// Etienne Lord 2009
/// Find in a database Primary ID associated with a Term

import configuration.Config;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class MatrixTableModel extends AbstractTableModel {
   ArrayList<String[]> data = new ArrayList<String[]>();  // The data for easy access
   String[] qualifier;                  //SQL Column name (cache)
   public String SQLquery;                     //SQL Query
   public ResultSet SQLrs;                     //SQL Results
   public ResultSetMetaData SQLrsMetaData;     //SQL To retreive the column name
   public int numberOfColumns;                 //SQL Number of Columns
   public int numberOfRows;                    //SQL Number of Rows


   public MatrixTableModel() {
   }

   public void setData(ResultSet rs) {
       this.SQLrs=rs;
       //CASE 1: We have a ResultSet
       if (this.SQLrs!=null) {
           try {
               // GET COLUMN NAME
                  this.SQLrsMetaData=rs.getMetaData();
                  setColumnName();
               // SET THE DATA BY LOADING INTO THE data ArrayList<String[]>
                   data.clear();
                   while (SQLrs.next()) {
                        String[] tmpstr=new String[numberOfColumns];
                        for (int i=0; i<numberOfColumns;i++) {
                            tmpstr[i]=SQLrs.getString(i+1); //+1 beacause first column is 1
                        }
                        data.add(tmpstr);
                   }
                numberOfRows=data.size();
            } catch (SQLException sqle) {Config.log("Error");sqle.printStackTrace();}
       //CASE 2: SQLrs == null, Unlikeky but..
       } else {
           this.SQLrsMetaData=null;          
       }
   }

   private void setColumnName() {
      //Case 1: We dont have data
       if (SQLrsMetaData==null) {
           qualifier=null;
           numberOfColumns=0;
       } else {
       // Case 2: We have data!
           try {
                this.numberOfColumns = this.SQLrsMetaData.getColumnCount();
                qualifier = new String[numberOfColumns];
                 for (int i=1; i<numberOfColumns+1;i++) {
                    String columnName = this.SQLrsMetaData.getColumnName(i);
                    qualifier[i-1]=columnName;
                }
            } catch (SQLException ex) {ex.printStackTrace();}
       } //End else
   }

    public int getRowCount() {
        return data.size();
     }

    public int getColumnCount() {
        if (qualifier==null) return 0;
        return qualifier.length;
    }

    public Object getValueAt(int row, int col) {

               try {
                    String[] tmp=this.data.get(row);
                    return (Object) tmp[col];
               } catch(Exception e) {return null;}

    }

    @Override
     public Class getColumnClass(int c) {
        return String.class;
    }


    @Override
     public String getColumnName(int c) {
         if (qualifier!=null) {
         return qualifier[c]; //-- Numbering +" "+String.valueOf(c);
         }
         return "";
     }

    /**
     *
     * @param row
     * @param col
     * @return Always True--For easy select...
     */
    @Override
    public boolean isCellEditable(int row, int col)
        { return false; }//For easy select, set as true

    @Override
    public void setValueAt(Object value, int row, int col) {
        //At the moment, we can't modify directly the database
    }

    public boolean saveData(String filename) {
        try {
           PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
           //Output qulifier
           for (String s:qualifier) pw.print(s+"\t");
           pw.println();
           for (String[] s:data) {
               for (String s2:s) pw.print(s2+"\t");
               pw.println();
           }
           pw.flush();
           pw.close();
        } catch(Exception e) {return false;}
        return true;
    }

    /**
     * Return the number of data in the query
     * @return
     */
    public int getSize() {
        return data.size();
    }


}
