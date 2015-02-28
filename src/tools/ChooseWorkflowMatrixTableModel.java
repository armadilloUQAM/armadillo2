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

public class ChooseWorkflowMatrixTableModel extends AbstractTableModel {
  
    Integer[][] data=null;
    String[] qualifier={};                 
    ArrayList<String> objects_name=new ArrayList<String>();
    int total_column=0;
    int total_variables=0;
    databaseFunction df=new databaseFunction();
    
   public ChooseWorkflowMatrixTableModel() {  
//       if (data.size()!=0) {
//            data.clear();         
//       }
//       this.setData(); 
//       this.fireTableStructureChanged();
//       this.fireTableDataChanged();
   }

   public void setData(Integer[][] matrix, int variable_nb, ArrayList<Workflows> workflow_name, ArrayList<String> objects_name) {
      //---This will load the workflow from database
       ArrayList<String> names=new ArrayList<String>();       
       names.add("Variables");
       for (Workflows w:workflow_name) {            
           String name=w.getName();
           if (w.clustering_group!=0) name+=" ("+w.clustering_group+")";           
           names.add(name);
       }
       qualifier=names.toArray(new String[0]);
       total_column=names.size();
       total_variables=variable_nb;
       data=matrix;
       this.objects_name=objects_name;
       this.fireTableStructureChanged();
       this.fireTableDataChanged();
   }

   

    public int getRowCount() {
       return total_variables;
     }

    public int getColumnCount() {
      if (qualifier==null) return 0;
     return qualifier.length;
    }

    public Object getValueAt(int row, int col) {
               try {                                      
                   //data type
                   if (col==0) {
                       return objects_name.get(row);
                   } else {
                       return data[row][col-1];
                   }                    
               } catch(Exception e) {return null;}                
    }

    @Override
     public Class getColumnClass(int c) {
        switch (c){
            case 0:  return String.class;            
            default: return Integer.class;
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
         return false;           
        }//For easy select, set as true

    @Override
    public void setValueAt(Object value, int row, int col) {
//       try {                                      
//            Workflows tmp=this.data.get(row);                    
//            data.get(row).setSelected((Boolean)value);               
//       } catch(Exception e) {} 
        
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
}
