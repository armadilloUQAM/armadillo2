/*
 *  Armadillo Workflow Platform v1.0
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

package Scripts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import workflows.armadillo_workflow;
import workflows.WorkflowModel;
import workflows.WorkflowModel.Workflow;
import workflows.WorkflowModel.workflow_connector;
import workflows.WorkflowModel.workflow_object;
//mport workflows.WorkflowModel.workflow_object;
import workflows.workflow_properties;

/**
 * This object manage transformation from workflow to script format.
 *
 * @author Alix Boc, Etienne Lord
 * @since June 2009
 */

public class Scripts_conversion {

    HashMap<String, workflow_properties>Visited=new HashMap<String, workflow_properties>();
    HashMap<WorkflowModel.workflow_object,String> color=new  HashMap<WorkflowModel.workflow_object,String>();
    HashMap<WorkflowModel.workflow_object,WorkflowModel.workflow_object> predecessor=new  HashMap<WorkflowModel.workflow_object,WorkflowModel.workflow_object>();
    HashMap<WorkflowModel.workflow_object,Integer>d=new HashMap<WorkflowModel.workflow_object,Integer>();
    HashMap<WorkflowModel.workflow_object,Integer>f=new HashMap<WorkflowModel.workflow_object,Integer>();
    //--List fo visited path
    ArrayList<LinkedList<armadillo_workflow.workflow_object>> path=new ArrayList<LinkedList<armadillo_workflow.workflow_object>>();
    
    int time=0;
    ////////////////////////////////////////////////////////////////////////////
    /// Armadillo Workflow
    
    public void TriTopo(armadillo_workflow.Workflow G, LinkedList<armadillo_workflow.workflow_object> Sorted) {
          //--Reset find flag
         Visited.clear();
         for (armadillo_workflow.workflow_object obj:G.work) obj.getProperties().remove("Order");
         //--Tri Topo
         for (armadillo_workflow.workflow_object obj:G.work) {
             Visit(G, obj, Sorted);
          }
          //--Inverse list and add Order...
         Collections.reverse(Sorted);
         for (int i=0; i<Sorted.size();i++) {
             armadillo_workflow.workflow_object o=Sorted.get(i);
             o.getProperties().put("Order", i);
         }
          
    }

      private void Visit(armadillo_workflow.Workflow G, armadillo_workflow.workflow_object Sommet,LinkedList<armadillo_workflow.workflow_object> Sorted) {
         if (!Visited.containsKey(Sommet.getProperties().getID())) {
             Visited.put(Sommet.getProperties().getID(), Sommet.getProperties());
             for (armadillo_workflow.workflow_object obj:ChercherNextVoisin(G,Sommet)) {
                 Visit(G,obj,Sorted);                
             }
              Sorted.add(Sommet);
         }
      }

    private Vector<armadillo_workflow.workflow_object> ChercherNextVoisin(armadillo_workflow.Workflow G, armadillo_workflow.workflow_object Sommet) {
        Vector<armadillo_workflow.workflow_object>tmp=new Vector<armadillo_workflow.workflow_object>();      
        for (armadillo_workflow.workflow_object obj:G.findOutput(Sommet)) tmp.add(obj);
        return tmp;
    }

    ////////////////////////////////////////////////////////////////////////////
    /// workflow (new version)
    
    //--Convert one workflow model to the other
    public WorkflowModel.Workflow convert(armadillo_workflow.Workflow G) {
        workflows.WorkflowModel w=new workflows.WorkflowModel();   
        //--Copy the object 
        for (armadillo_workflow.workflow_object o:G.work) {        
           w.createObject(o.getProperties());
        }
        
         //--Copy the object an edge from one 
        for (armadillo_workflow.workflow_connector_edge g:G.work_connection) {
            //--Find the connector
            workflow_properties prop=g.getProperties();
            int source_connectorNb=prop.getInt("source");
            int dest_connectorNb=prop.getInt("destination");
            int source_properties_id=prop.getInt("source_properties_id");
            int dest_properties_id=prop.getInt("dest_properties_id");                            
            workflow_connector source=w.workflow.getConnector(source_properties_id, source_connectorNb);
            workflow_connector dest=w.workflow.getConnector(dest_properties_id, dest_connectorNb);
            if (source!=null&&dest!=null) {
                boolean notDeletabled=false;
                if (prop.isSet("notDeletabled")) notDeletabled=prop.getBoolean("notDeletabled");
                w.workflow.addConnector(source, dest, "",notDeletabled);                              
             }            
        }
        return w.workflow;
    }
    
    public void test_dfs(armadillo_workflow workflow) {
        WorkflowModel.Workflow G=convert(workflow.workflow);
        LinkedList<WorkflowModel.workflow_object> visited = new LinkedList<WorkflowModel.workflow_object>();
        DFS(G, visited);                
    }
    
    
    
    public void TriTopo(WorkflowModel.Workflow G, LinkedList<WorkflowModel.workflow_object> Sorted) {
          //--Reset find flag
         Visited.clear();
         for (WorkflowModel.workflow_object obj:G.work) obj.getProperties().remove("Order");
         //--Tri Topo
         for (WorkflowModel.workflow_object obj:G.work) {
             Visit(G, obj, Sorted);
          }
          //--Inverse list and add Order...
         Collections.reverse(Sorted);
         for (int i=0; i<Sorted.size();i++) {
             WorkflowModel.workflow_object o=Sorted.get(i);
             o.getProperties().put("Order", i);
         }
          
    }

      private void Visit(WorkflowModel.Workflow G, WorkflowModel.workflow_object Sommet,LinkedList<WorkflowModel.workflow_object> Sorted) {
         if (!Visited.containsKey(Sommet.getProperties().getID())) {
             Visited.put(Sommet.getProperties().getID(), Sommet.getProperties());
             for (WorkflowModel.workflow_object obj:ChercherNextVoisin(G,Sommet)) {
                 Visit(G,obj,Sorted);                
             }
              Sorted.add(Sommet);
         }
      }

    private Vector<WorkflowModel.workflow_object> ChercherNextVoisin(WorkflowModel.Workflow G, WorkflowModel.workflow_object Sommet) {
        Vector<WorkflowModel.workflow_object>tmp=new Vector<WorkflowModel.workflow_object>();      
        for (WorkflowModel.workflow_object obj:G.findOutput(Sommet)) tmp.add(obj);
        return tmp;
    }
    
    private Vector<WorkflowModel.workflow_object> getParent(WorkflowModel.Workflow G, WorkflowModel.workflow_object Sommet) {
        Vector<WorkflowModel.workflow_object>tmp=new Vector<WorkflowModel.workflow_object>();      
        //--Iterate over edge to find connected object
        for (WorkflowModel.workflow_object obj:G.findInput(Sommet)) {            
            tmp.add(obj);
        }
        return tmp;
    }
     
    /**
     * Find all path between Source and Dest
     * @param Source
     * @param Dest
     * @return 
     */
    public ArrayList<ArrayList<WorkflowModel.workflow_object>> getAllPath(WorkflowModel.workflow_object Source, WorkflowModel.workflow_object Dest) {
        
        
        return null;
    }
            
    
    private Vector<WorkflowModel.workflow_object> Adgacent(WorkflowModel.Workflow G, WorkflowModel.workflow_object Sommet) {
          Vector<WorkflowModel.workflow_object>tmp=new Vector<WorkflowModel.workflow_object>();  
        HashMap<WorkflowModel.workflow_object,Integer>tmp2=new HashMap<WorkflowModel.workflow_object,Integer>();      
        for (WorkflowModel.workflow_object o:getParent(G, Sommet)) {
           tmp2.put(o, 1); 
        }
         for (WorkflowModel.workflow_object o:ChercherNextVoisin(G, Sommet)) {
           tmp2.put(o, 1); 
        }
        for (WorkflowModel.workflow_object o:tmp2.keySet()) tmp.add(o);         
        return tmp;
    }
  
    //The sub-algorithm DFS_visit(u) is as follows:
//DFS_visit(u)
//color(u) ← GRAY
//d[u] ← time ← time + 1
//for each v adjacent to u
//    do
//if color[v] ← GRAY and Predecessor[u] ≠ v                 return
//"cycle exists"
//         if color[v] ← WHITE
//                do
//predecessor[v] ← u
//                     recursively DFS_visit(v)
//color[u] ← BLACK
//f[u] ← time ← time + 1
     private void DFS_visit(WorkflowModel.Workflow G, WorkflowModel.workflow_object u) {
        color.put(u, "GREY");
        d.put(u, time++); 
        for (WorkflowModel.workflow_object v: Adgacent(G,u)) {
            if (color.get(v)=="GREY"&&predecessor.get(u)!=v) {
                System.out.println("Cycle found");
                return;
            }
            if (color.get(u)=="WHITE") {
                predecessor.put(v, u);
                DFS_visit(G,v);
            }
            color.put(u, "BLACK");
            f.put(u,time++);
        }
      }
     
     private void DFS(WorkflowModel.Workflow G, LinkedList<WorkflowModel.workflow_object> vertex) {
        time=0;
        d.clear();
        color.clear();
        predecessor.clear();
        
         for (WorkflowModel.workflow_object u:vertex) {
            color.put(u, "WHITE");
            predecessor.put(u, null);            
            
        }        
         for (WorkflowModel.workflow_object u:vertex) {
             if (color.get(u)=="WHITE") {
                  DFS_visit(G, u);                  
             }
         }

     }
     
     private void DFS_Sedgewick(int k) {
         if (k==0) {
             Visited.clear();                          
         }
         //Visit(null, null, null);
         //visit(k);
         //visited[k]=true;
//         for (Node t= adj[k]; t!=null; t=t.next) {
//             if (!visited[t.v]) DFS_Sedgewick(t.v);
//         }
     }
     
     static class Item {int size; int val;}
     static int N=10; //--Item
     static Item[] Items=new  Item[N]; //--List of items
     static Integer[] maxKnown=new  Integer[N]; //--maxKnown to N
      static Item[] itemKnown=new  Item[N];// known items
     
     static int knap_iter (int cap) {
         int i, space, max, maxi=0,t;
         for (i=0, max=0; i<N;i++) {
             if ((space= cap -Items[i].size) >=0) 
                 if ((t=knap_iter(space) + Items[i].val) > max)
                     max =t;
         }
         return max;
     }
     
     static int knap_dyn(int M) {
         int i, space, max, maxi=0,t;
         if (maxKnown[M]!=null) return maxKnown[M];
          for (i=0, max=0; i<N;i++) 
              if ((space= M -Items[i].size) >=0) 
                if ((t=knap_dyn(space) + Items[i].val) > max)
                { max=t; maxi=i;}
                maxKnown[M]=max;
                itemKnown[M]=Items[maxi];
         return max;
     }
             
     
//     ALGORITHM DFS_DETECT_CYCLES [G]
//for each vertex u in V[G]
//    do color[u] ← WHITE
//predecessor[u] ← NIL
//time ← 0
//for each vertex u in V[G]
//    do if
//color[u] ← WHITE
//            DFS_visit(u)
//The sub-algorithm DFS_visit(u) is as follows:
//DFS_visit(u)
//color(u) ← GRAY
//d[u] ← time ← time + 1
//for each v adjacent to u
//    do
//if color[v] ← GRAY and Predecessor[u] ≠ v                 return
//"cycle exists"
//         if color[v] ← WHITE
//                do
//predecessor[v] ← u
//                     recursively DFS_visit(v)
//color[u] ← BLACK
//f[u] ← time ← time + 1
    
}
