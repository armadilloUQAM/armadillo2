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


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// Workflow_properties_TableModel
/// Table model to handle ResultSet of a SQL Query
/// Etienne Lord 2009
/// Find in a database Primary ID associated with a Term


import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Set;
import javax.swing.table.*;

public class workflow_properties_TableModel extends AbstractTableModel {
    workflow_properties data=new workflow_properties();
    String[] qualifier={"Key","Value"};
    //private boolean flagChanged=false;                    //Flag to know if this table was changed
    workflow_properties_dictionnary dict=new workflow_properties_dictionnary();
    
    public workflow_properties_TableModel() {
    }
    
    public void setData(workflow_properties work) {
        this.data=work;
        //setFlagChanged(false); // Reset the changed flag
    }
    
    private void setColumnName() {
        
    }
    
    public int getRowCount() {
        return data.size();
    }
    
    public int getColumnCount() {
        if (qualifier==null) return 0;
        return qualifier.length;
    }
    
    public Object getValueAt(int row, int col) {
        
        Enumeration keys=data.keys();
        String key="";
        
        try {
            //locate the good key
            int index=0;
            while (index<=row) {
                if (!keys.hasMoreElements()) {
                    return null;
                } else {
                    key=(String) keys.nextElement();
                    index++;
                }
            }
            
            switch (col) {
                case 0:return key;
                case 1: return data.get(key);
                case 2: return dict.isValid(key, data.get(key));
                default:return null;
            }
        } catch(Exception e) {return null;}
        
    }
    
    @Override
    public Class getColumnClass(int c) {
        return String.class;
    }
    
    
    @Override
    public String getColumnName(int c) {
        if (qualifier!=null) {
            return qualifier[c];
            //-- For debug : +" "+String.valueOf(c);
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
    public boolean isCellEditable(int row, int col) {
        switch (col) {
            case 1:  return true;
            default: return false;
        }
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        //At the moment, we can't modify directly the database
        switch (col) {
            case 1: String key=(String)getValueAt(row, 0);
            data.put(key, value);
            //--If data is color, set both
            if (key.equals("defaultColor")||key.equals("colorMode")) {
                data.put("defaultColor", value);
                data.put("colorMode", value);
            }
            
            this.fireTableDataChanged();
            break;
        }
        
    }
    
    /**
     * Independant TSV (TAB) Save file option
     * @param filename
     * @return True if success
     */
    public boolean saveData(String filename) {
        try {
            PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
            //Output qualifier
            pw.println("# Properties for "+data.filename);
            for (String s:qualifier) pw.print(s+"\t");
            pw.println();
            String[] keySet= (String[])data.keySet().toArray();
            for (String key:keySet) {
                pw.println(key+"\t"+data.get(key));
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
    
    /**
     * Return a pointer to this data
     * Note: WE SHOULD ALWAYS CHANGE THE DATA DIRECTLY TO THIS WORKFLOW_PROPERTIES?
     *
     * @return
     */
    public workflow_properties getData() {
        return data;
    }
    
    /**
     * @return the flagChanged
     */
    public boolean isModified() {
        return data.isModified();
    }
}
