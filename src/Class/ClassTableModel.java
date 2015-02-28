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

package Class;



import Class.Classdata;
import javax.swing.table.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Table Model to Handle InfoSummary and Other Sequence Download
 * @author Etienne Lord
 * @since Avril 2009
 */
public class ClassTableModel extends AbstractTableModel {
   public Vector<Classdata> data = new Vector<Classdata>();
   String[] qualifier={"Class","Nb Method", "Nb Fields"};
   int countSelected=0;    //Fast way of keeping track of selection

   public void setColumnName(String[] qualifier) {
       this.qualifier=qualifier;
   }

   public void setData(Vector<Classdata> data) {
         this.data=data;
                
   }

   public void setData(ArrayList<Classdata> data) {
         this.data.addAll(data);
                
   }

   /**
    * Update the data if the esummary is loaded ONLY
    * @param data
    * @return
    */
   public boolean updateData(Classdata data) {
       return true;
   }

   public void addData(Classdata data) {
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
         Classdata info = data.get(row);
          if (info!=null) {
               try {
                   switch (col) {
                       case 0: return info.getName();
                       case 1: return info.getNbMethods();
                       case 2: return info.getNbFields();
                       default: return "";
                   }
             } catch(Exception e) {return null;}
         }
        return null;
    }

     public Class getColumnClass(int c) {
         switch(c) {
             case 0: return String.class;
             case 1: return Integer.class;
             case 2: return Integer.class;
             default: return String.class;
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
             
             default: return false;
         }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        
    }

  
    public Classdata getClassdata(int row) {
        return data.get(row);
    }
    
    }//End class
