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

package biologic.seqclasses;

///
/// Sequence Table Model
/// Etienne Lord Avril 2009
///

import biologic.Tree;
import javax.swing.table.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Vector;

class TreeTableModel extends AbstractTableModel {
   Vector<Tree> data=new Vector<Tree>();
   
   String[] qualifier={"Selected","Name","Note","Tree"};

   public TreeTableModel() {
   }

   public void setColumnName(String[] qualifier) {
       this.qualifier=qualifier;
   }

   public void setData(Vector<Tree> data) {
       this.data.addAll(data);
   }

    public void addData(Tree data) {
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
        // We need to get to the displayed sequence

         Tree d = data.get(row);

           if (d!=null) {
               try {
                   switch (col) {
                       case 0: return d.isSelected();
                       case 1: return d.getName();
                       case 2: return d.getNote();
                       case 3: return d.getTree();
                       default: return "";
                   }
             } catch(Exception e) {return null;}
           }
           return null;

    }

     public Class getColumnClass(int c) {
         switch(c) {
             case 0: return Boolean.class;
             default: return String.class;
         }
    }


    @Override
     public String getColumnName(int c) {
         if (qualifier!=null) {
         return qualifier[c];
         }
         return "";
     }

    @Override
    public boolean isCellEditable(int row, int col) {
         switch(col) {
             case 0 : return true;
             case 1 : return true;
             case 2 : return true;
             default: return false;
         }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {

        Tree d = data.get(row);
        d.setModified(true);
         switch(col) {
             case 0 : boolean v=(Boolean)value;
                      d.setSelected(v);
                      break;
             case 1 : String v2=(String)value;
                      d.setName(v2);
                      break;
             case 2 : String v1=(String)value;
                      d.setNote(v1);
                      break;
         }
    }

}
