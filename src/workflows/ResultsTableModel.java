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
/// Results TableModel
/// Table model to handle ResultSet of a SQL Query
/// Etienne Lord 2009
/// Find in a database Primary ID associated with a Term

import biologic.RunWorkflow;
import biologic.Workflows;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.table.*;
import workflows.armadillo_workflow.Workflow;
import workflows.armadillo_workflow.workflow_object;

public class ResultsTableModel extends AbstractTableModel {
   private RunWorkflow data = new RunWorkflow();  // The data for easy access
   String[] qualifier;                  //SQL Column name (cache)
   int RowCount=0;

   public ResultsTableModel() {    
   }


    public int getRowCount() {
        //--Max of all Runworkflow associated workflow.work
        if (data.execution_workflow.isEmpty()) data.loadExecutionWorkflows();
        if (data.execution_workflow.isEmpty()) {
            return 0;
        } else {
           if (RowCount==0) {
              //--For each Workflows               
               for (Object key:data.execution_workflow.keySet()) {                    
                  Workflows w=(Workflows)data.execution_workflow.get(key);
                   if (RowCount<w.workflow.workflow.get_nb_workflow_object()) {
                       RowCount=w.workflow.workflow.get_nb_workflow_object();
                   }
              }
           }             
        }
        return RowCount;
    }

    public int getColumnCount() {        
        return 1+getData().getExecution_workflow_id().size();
    }

    public Object getValueAt(int row, int col) {
    //--Row if the object
    //--Col is execution

               try {
                int ids=this.data.getExecution_workflow_id().get(col);
                Workflows w=this.data.execution_workflow.get(ids);
                workflow_object obj=w.workflow.workflow.work.get(row);
                return obj.getName()+" "+obj.getProperties().getStatus();
               } catch(Exception e) {return null;}   
    }

    @Override
     public Class getColumnClass(int c) {
        return String.class;
    }


    @Override
     public String getColumnName(int c) {
        if (qualifier==null) {
            qualifier=new String[1+getData().getExecution_workflow_id().size()];
            qualifier[0]="Program / Results";
            Workflows w=new Workflows();            
            for (int i=0; i<getData().getExecution_workflow_id().size();i++) {
                int ids=getData().getExecution_workflow_id().get(i);
                qualifier[1+i]=w.getNameId(ids);
            }
        }
         return qualifier[c];
         //return "";
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

   
    /**
     * @return the data
     */
    public RunWorkflow getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(RunWorkflow data) {
        this.data = data;
        //Reset row cound...
        this.RowCount=0;
        qualifier=null;
        System.out.println(data);
    }

    


}
