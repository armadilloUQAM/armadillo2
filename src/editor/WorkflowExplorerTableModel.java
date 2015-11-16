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

package editor;

import biologic.BlastHit;
import biologic.Output;
import biologic.Workflows;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.table.*;
import java.util.Vector;
import workflows.armadillo_workflow;
import workflows.workflow_properties;

public class WorkflowExplorerTableModel extends AbstractTableModel {
   public LinkedList<Workflows>data=new LinkedList<Workflows>();
  
   
   //--
   String[] qualifier={"Selected","Workflows"};
  //--Special cached data
  public ArrayList<ArrayList<String>> data_cached= new ArrayList<ArrayList<String>>();
  

   public WorkflowExplorerTableModel() {
   }

   
   
   
   public void setData( LinkedList<Workflows> data) {
       this.data.clear();
       this.data_cached.clear();
     for (Workflows b:data) addData(b);
   }

   public void getAllWorkflowObject() {
       //--This will create a XML of a workflows
       
   }
   
    public void addData(Workflows data) {
       this.data.add(data);
       //--Also cached data
       
        data.workflow=new armadillo_workflow();
        data.StringToWorkflow();
        //--Extract object in workflow
       ArrayList<String> tmp=new ArrayList<String>();
        
        for (armadillo_workflow.workflow_object obj:data.workflow.workflow.outputExecution()) {            
            workflow_properties properties=obj.getProperties();                        
            if (properties.get("ObjectType").equals("OutputDatabase")) {  
               
                 tmp.add(properties.getDescription());
            } else 
            if (properties.get("ObjectType").equals("Program")) {  
                tmp.add(properties.getName());
                
            } else {
                tmp.add(properties.getID());
            }
            //Output output=new Output();
             //output.setType(properties.output_type);
//                                output.setTypeid(id);
//                                Object bio=output.getBiologic();
           
        }
        data.workflow=null;
        data_cached.add(tmp);
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
    public Workflows getWorkflows(int row) {
        return data.get(row);
    }

    public Object getValueAt(int row, int col) {
        // We need to get to the displayed sequence

         Workflows d = data.get(row);
         
         d.workflow=new armadillo_workflow();
         d.StringToWorkflow();
           if (d!=null) {
               try {
                   if (col==0 )  return d.isSelected(); 
                   if (col==1 )  return d.getName(); 
                   if (col>1) {
                       ArrayList<String> tmp=data_cached.get(row);
                       //workflow_properties p=tmp.get(col-2);
                       return tmp.get(col-2);
                   }
//                   switch (col) {
//                       case 0:
//                       case 1: return d.getName();                               
//                       //case 1: return d.getSubject_accession();
//                       //case 2: return d.getSubject_name();
//                       //case 3: return d.getEvalue();
//
//                       default: return "";
//                   }
                   
                   
             } catch(Exception e) {return null;}
           }
           return null;

    }

    @Override
     public Class getColumnClass(int c) {
         switch(c) {
             case 0: return Boolean.class;             
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
             default: return false;
         }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {

        Workflows d = data.get(row);

         switch(col) {
             case 0 : boolean v=(Boolean)value;
                      d.setSelected(v);
                      break;             
         }
    }

}
