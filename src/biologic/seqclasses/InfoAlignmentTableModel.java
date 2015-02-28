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

import biologic.*;
import javax.swing.table.*;
import java.util.Vector;

public class InfoAlignmentTableModel extends AbstractTableModel {
   public Vector<InfoAlignment> data=new Vector<InfoAlignment>();
   
    String[] qualifier={"Selected for Grouping","Name", "ID", "# sequence(s)"};

   public InfoAlignmentTableModel() {
   }

   public void setColumnName(String[] qualifier) {
       this.qualifier=qualifier;
   }

   public void setData( Vector<InfoAlignment> data) {
       this.data.clear();
       this.data.addAll(data);
   }

    public void addData(InfoAlignment data) {
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

    /**
     * Return the sequence at the selected row
     * Since the displayed sequence might be in wrong order because of
     * Reordegin
     * @param row
     * @return
     */
    public InfoAlignment getInfoAlignment(int row) {
        return data.get(row);
    }

    public Object getValueAt(int row, int col) {
        // We need to get to the displayed sequence

         InfoAlignment d = data.get(row);

           if (d!=null) {
               try {
                   switch (col) {
                       case 0: return d.isSelected();
                       case 1: return d.getName();
                       case 2: return d.getId();
                       case 3: return d.getSequence_id().size();
                       default: return "";
                   }
             } catch(Exception e) {return null;}
           }
           return null;

    }

    @Override
     public Class getColumnClass(int c) {
         switch(c) {
             case 0: return Boolean.class;
             case 4: return Integer.class;
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
             case 0 : return true;
             case 1: return true;            
             default: return false;
         }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {

        InfoAlignment d = data.get(row);

         switch(col) {
             case 0 : boolean v=(Boolean)value;
                      d.setSelected(v);
                      d.setSelected_for_grouping(v);
                      break;
             case 1 : String s=(String)value;
                      if (!s.equals(d.getName())) {
                        d.setName(s);
                        d.setModified(true);
                      }
                      break;          
         }
    }

}
