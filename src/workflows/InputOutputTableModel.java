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

package workflows;

import javax.swing.table.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Table Model to Handle Properties and Input or Output
 * @author Etienne Lord
 * @since Avril 2009 (revised July 2009)
 */
public class InputOutputTableModel extends AbstractTableModel {
   public Vector<InputOutput> data = new Vector<InputOutput>();
   workflow_properties properties=null;
   String[] qualifier={"Input or Output","Connector or Condition"};
   int countSelected=0;    //Fast way of keeping track of selection

   public void setColumnName(String[] qualifier) {
       this.qualifier=qualifier;
   }

   public void setData(Vector<InputOutput> data) {
         this.data=data;
                
   }

   public void setData(ArrayList<InputOutput> data) {
         this.data.addAll(data);
                
   }

   public void setProperties(workflow_properties properties) {
       this.properties=properties;
   }

   public void addData(InputOutput data) {
       this.data.add(data);
   }

    public int getRowCount() {
        int number=data.size();
        if (number<0) number=0;
        return number;
    }

    public int getColumnCount() {
        if (qualifier!=null) return qualifier.length;
        return 0;
    }

    public Object getValueAt(int row, int col) {
         InputOutput info = data.get(row);
          if (info!=null) {
               try {
                   switch (col) {
                       case 0: return info.getInputOutput();
                       case 1: return info.getConnector();
                       default: return "";
                   }
             } catch(Exception e) {return null;}
         }
        return null;
    }

    @Override
     public Class getColumnClass(int c) {
         switch(c) {
             case 0: return String.class;
             case 1: return String.class;
             default:return String.class;
         }
    }


     public String getColumnName(int c) {
         if (qualifier!=null) {
         return qualifier[c];
         }
         return "";
     }

    @Override
    public boolean isCellEditable(int row, int col) {
         switch(col) {
             case 1: return true;
             default: return false;
         }
    }

    /**
     * Set the properties and value in the table
     * @param value
     * @param row
     * @param col
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        InputOutput info = data.get(row);
        String V=(String)value;
        info.setConnector(V);
        if (properties!=null) {
            if (V.equals("False")||V.equals("No connector")) {
                properties.remove(info.getInputOutput());
            } else {
                properties.put(info.getInputOutput(), info.getConnector());
            }
        }
    }

  
        
    }//End class
