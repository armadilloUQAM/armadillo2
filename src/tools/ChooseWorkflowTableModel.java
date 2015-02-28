/*
 *  Armadillo Workflow Platform v2.0
 *  A simple pipeline system for phylogenetic analysis
 *  
 *  Copyright (C) 2009-2013  Etienne Lord, Mickael Leclercq
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

package tools;
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// ChooseWorkfloiwTableModel
/// Table model to handle ResultSet of a SQL Query
/// Etienne Lord 2013
/// Choose a workflow associated from the workflow 

import biologic.Workflows;
import database.*;
import javax.swing.table.*;
import java.util.ArrayList;
import java.util.Vector;

public class ChooseWorkflowTableModel extends AbstractTableModel {
  
    ArrayList<Workflows> data=new ArrayList<Workflows>();
      String[] qualifier={"Selected","Workflow","Group"};                 
    databaseFunction df=new databaseFunction();
    
   public ChooseWorkflowTableModel() {  
//       if (data.size()!=0) {
//            data.clear();         
//       }
//       this.setData(); 
//       this.fireTableStructureChanged();
//       this.fireTableDataChanged();
   }

   public void setData( ArrayList<Workflows> data) {
      //---This will load the workflow from database
     this.data=data;      
       this.fireTableStructureChanged();
       this.fireTableDataChanged();
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
                    Workflows tmp=this.data.get(row);
                    switch (col) {
                        case 0: return tmp.isSelected();
                        case 1: return tmp.getName();
                        case 2: return tmp.clustering_group;    
                    }                    
               } catch(Exception e) {return null;} 
               return null;
    }

    @Override
     public Class getColumnClass(int c) {
        switch (c){
            case 0:  return Boolean.class;
            case 1:  return String.class; 
            case 2:  return Integer.class; 
            default: return String.class;
        }            
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
        {
           switch (col){
            case 0:  return true;
            case 1:  return false; 
            case 2:  return false; 
            default: return false;
        }          
        
        
        }//For easy select, set as true

    @Override
    public void setValueAt(Object value, int row, int col) {
       try {                                      
            Workflows tmp=this.data.get(row);                    
            data.get(row).setSelected((Boolean)value);               
       } catch(Exception e) {} 
        
    }

    public boolean saveData(String filename) {
//        try {
//           PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
//           //Output qulifier
//           for (String s:qualifier) pw.print(s+"\t");
//           pw.println();
//           for (String[] s:data) {
//               for (String s2:s) pw.print(s2+"\t");
//               pw.println();
//           }
//           pw.flush();
//           pw.close();
//        } catch(Exception e) {return false;}
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
