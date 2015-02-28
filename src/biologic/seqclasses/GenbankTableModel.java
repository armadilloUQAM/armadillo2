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

import biologic.InfoSequence;
import javax.swing.table.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Table Model to Handle InfoSummary and Other Sequence Download
 * @author Etienne Lord
 * @since Avril 2009
 */
public class GenbankTableModel extends AbstractTableModel {
   public Vector<InfoSequence> data = new Vector<InfoSequence>();
   String[] qualifier={"Selected for download","Accession", "Gi or Database", "Description", "bp"};
   int countSelected=0;    //Fast way of keeping track of selection

   public void setColumnName(String[] qualifier) {
       this.qualifier=qualifier;
   }

   public void setData(Vector<InfoSequence> data) {
         this.data=data;
         countSelected=0;
         for (InfoSequence i:this.data) if (i.isSelected()) countSelected++;
   }

   public void setData(ArrayList<InfoSequence> data) {
         this.data.addAll(data);
         countSelected=0;
         for (InfoSequence i:this.data) if (i.isSelected()) countSelected++;
   }

   /**
    * Update the data if the esummary is loaded ONLY
    * @param data
    * @return
    */
   public boolean updateData(InfoSequence data) {
       int index=this.data.indexOf(data);
       if (index==-1) return false;
       InfoSequence tmp=this.data.get(index);
       //ugly but fast
       tmp.setType(data.getType());
       tmp.setHgncid(data.getHgncid());
       tmp.setEnsemblid(data.getEnsemblid());
       tmp.setEmsembldb(data.getEmsembldb());
       tmp.setGi(data.getGi());
       tmp.setAccession(data.getAccession());
       tmp.setDescription(data.getDescription());
       tmp.setLen(data.getLen());
       tmp.setLoaded(true);
       return true;
   }

   public void addData(InfoSequence data) {
       this.data.add(data);
       if (data.isSelected()) countSelected++;
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
         InfoSequence info = data.get(row);
          if (info!=null) {
               try {
                   switch (col) {
                       case 0: return info.isSelected();
                       case 1: switch (info.getType()) {
                                case InfoSequence.type_Ensembl: return (info.getEnsemblid());
                                case InfoSequence.type_HGNC:return (info.getEnsemblid());
                                case InfoSequence.type_Ncbi:return (info.getAccession());
                                }
                       case 2: switch (info.getType()) {
                                case InfoSequence.type_Ensembl: return (info.getEmsembldb());
                                case InfoSequence.type_HGNC:return (info.getGi());
                                case InfoSequence.type_Ncbi:return (info.getGi());

                                }
                       case 3: return info.getDescription();
                       case 4: return info.getLen();
                      default: return "";
                   }
             } catch(Exception e) {return null;}
         }
        return null;
    }

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
             case 1 : return true;
             case 5: return true;
             default: return false;
         }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
         InfoSequence info = data.get(row);
          if (info!=null) {
                switch(col) {
                    case 0 :  boolean v=(Boolean)value;
                              info.setSelected(v);
                              countSelected+=(v?1:-1); //Update the count value
                              break;
                }
         } //End if
    }

    public int getNbSelected() {
        return countSelected;
    }

    public InfoSequence getInfoSummary(int row) {
        return data.get(row);
    }
    
    public int updateNbSelected() {
        countSelected=0;
        for (InfoSequence i:data) if (i.isSelected()) countSelected++;
        return countSelected;
    }
}//End class
