
package biologic.seqclasses.parserNewick;

import java.io.*;
import java.util.ArrayList;
import java.util.Vector;




/**
 * Note-This should be incorporated in the Tree class...
 * @author Etienne Lord
 * @since 2009-2011
 * 
 * 
 * Info: (FROM: http://www.megasoftware.net/WebHelp/glossary/rh_newick_format.htm)
 * ((raccoon, bear),((sea_lion,seal),((monkey,cat), weasel)),dog);
 * The above tree with branch lengths will look as follows:
 * ((raccoon:19.19959,bear:6.80041):0.84600,((sea_lion:11.99700, seal:12.00300):7.52973,((monkey:100.85930,cat:47.14069):20.59201, weasel:18.87953):2.09460):3.87382,dog:25.46154);
 * If you wish to specify bootstrap values then they could appear before the branch lengths (e.g., in .dnd files produced by CLUSTAL) or after the branch lengths (e.g., in .PHB files produced by CLUSTAL). In these cases, the format might look like:
 * ((raccoon:19.19959,bear:6.80041)50:0.84600,((sea_lion:11.99700, seal:12.00300)100:7.52973,((monkey:100.85930,cat:47.14069)80:20.59201, weasel:18.87953)75:2.09460)50:3.87382,dog:25.46154);
 *    or
 * ((raccoon:19.19959,bear:6.80041):0.84600[50],((sea_lion:11.99700, seal:12.00300):7.52973[100],((monkey:100.85930,cat:47.14069):20.59201[80], weasel:18.87953):2.09460[75]):3.87382[50],dog:25.46154);
 *
 *  Tree from http://packages.python.org/ete2/tutorial/tutorial_trees.html#root-node-on-unrooted-trees
    FORMAT	DESCRIPTION	SAMPLE
    0	flexible with support values	((D:0.723274,F:0.567784)1.000000:0.067192,(B:0.279326,H:0.756049)1.000000:0.807788);
    1	flexible with internal node names	((D:0.723274,F:0.567784)E:0.067192,(B:0.279326,H:0.756049)B:0.807788);
    2	all branches + leaf names + internal supports	((D:0.723274,F:0.567784)1.000000:0.067192,(B:0.279326,H:0.756049)1.000000:0.807788);
    3	all branches + all names	((D:0.723274,F:0.567784)E:0.067192,(B:0.279326,H:0.756049)B:0.807788);
    4	leaf branches + leaf names	((D:0.723274,F:0.567784),(B:0.279326,H:0.756049));
    5	internal and leaf branches + leaf names	((D:0.723274,F:0.567784):0.067192,(B:0.279326,H:0.756049):0.807788);
    6	internal branches + leaf names	((D,F):0.067192,(B,H):0.807788);
    7	leaf branches + all names	((D:0.723274,F:0.567784)E,(B:0.279326,H:0.756049)B);
    8	all names	((D,F)E,(B,H)B);
    9	leaf names	((D,F),(B,H));
    100	topology only	((,),(,));
 * 
 * 
 * 
 */
public class newick_tree {

    public ArrayList<node>node_list=new ArrayList<node>();   //list of node (leaf or internal)
    public ArrayList<edge>edge_list=new ArrayList<edge>();   //list of connection between node
    public int index=0;                         //char index
    public int level=0;
    String newick_string="";                    //--Originale newick string
    public String name="";                             // name of this tree (either filename or unknown)
    long ttime=0;                               //time to load

     public int[] ARETE;
      public float[] LONGUEUR;
      public int[] tableau={};
      public String[] lesNoms;
      public int nl; //--Nombre de feuilles
      public int kt;
      public String[] bootstrap;
      int nbBranches=0;
    
    /**
     * Node exists in the list (check by id)
     * @param n
     * @return
     */
    public boolean exists (node n) {
        return getNode_list().contains(n);
    }

    /**
     * Edge exists (currently, we check by node name...? should be id?)
     * @param e
     * @return
     */
    public boolean exists(edge e) {
        return getEdge_list().contains(e);
    }

    /**
     * Add a branch to the tree
     * @param source
     * @param dest
     * @return
     */
//    public edge addBranch(node source, node dest) {
//        return addBranch(source,dest,1.0);
//    }

    public void addRoot(node root) {
        this.getNode_list().add(root);
    }

    /**
     * Add a branch to the tree with lenght value
     * Note: bootstrap value is added directly to the edge
     * @param source
     * @param dest
     * @param lenght
     * @return
     */
    public edge addBranch(node source, node dest, double lenght, int level) {
        //--Not for now since we don't check for duplicate
        //if (!exists(source)) node_list.add(source);
        //if (!exists(dest)) node_list.add(dest);
        dest.setParent(source);
        source.setIsleaf(false);
        dest.depth=level;
        getNode_list().add(dest);
        edge e=new edge(source, dest);
        e.lenght=lenght;
        
        getEdge_list().add(e);
        return e;
    }

    /**
     * 
     * @param From
     * @param To
     * @return The number of replacement
     */
    public int replaceEdgeLength(double From, double To) {
        int count=0; 
        for (edge e:this.edge_list) {
                                if (e.lenght==From) {
                                    e.lenght=To;                    
                                    count++;
                                }
                            }
        return count;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    /// Parser of the Newick String

    /**
     * Parse a newick file
     * @param file
     */
    public void parseNewick(File file) {
        try {
            BufferedReader in=new BufferedReader(new FileReader(file));
            StringBuilder st=new StringBuilder();
            while(in.ready()) {
                st.append(in.readLine());
            }
            in.close();            
            parseNewick(st.toString());
            setName(file.getAbsolutePath());
        } catch(Exception e) {}
    }


    /**
     * The main recursive parser...
     * @param newick
     */
    public void parseNewick(String newick) {
        getNode_list().clear();
        getEdge_list().clear();
        index=0;
        this.newick_string=newick;
        node.index=0;
        node root=new node("root");
        root.setIsleaf(false);
        getNode_list().add(root);
        ttime=System.nanoTime();
        parseNewick(root,newick);
        ttime=System.nanoTime()-ttime;        
        setName("Unknown");
    }


    public void parseNewick(node head, String newick) {
        String buffer="";
        String buffer_bootstrap="";
        boolean flag_bootstrap=false;       
        edge current=null;
        char last_char=' ';
        char last_ctl_char=' ';
        while(index<newick.length()) {
            char current_char=newick.charAt(index);
            if (current_char=='['||current_char==']') {
                flag_bootstrap=!flag_bootstrap;
            } else
            if (current_char=='(') {               
                node newhead=new node("");
                current=addBranch(head, newhead,1.0, level);
                index++;
                level++;
                parseNewick(newhead, newick);                
                last_ctl_char=current_char;
            } else
            if (current_char==',') {
                
                double lenght=1.0;
                int bootstrap=0;
                if (buffer.length()>0) {                    
                    //CASE 1. --Bootstrap and branch lenght
                    if (buffer.startsWith(":")) {
                        //--Length
                        String len=buffer.substring(buffer.indexOf(":")+1);
                        try {lenght=Double.valueOf(len);}catch(Exception e){e.printStackTrace();}
                        //--bootstrap
                        if (buffer_bootstrap.length()>0) {
                            try {bootstrap=Integer.valueOf(buffer_bootstrap);}catch(Exception e){e.printStackTrace();}
                            buffer_bootstrap="";
                        }
                        if (current!=null) {
                            current.lenght=lenght;
                            current.bootstrap=bootstrap;
                        }
                     //CASE 2. --   New node which can also have bootstrap...
                    } else {
                      if (buffer.indexOf(":")>-1) {
                        String len=buffer.substring(buffer.indexOf(":")+1);                       
                        try {lenght=Double.valueOf(len);}catch(Exception e){}
                        buffer=buffer.substring(0, buffer.indexOf(":"));
                         if (buffer_bootstrap.length()>0) {
                            try {bootstrap=Integer.valueOf(buffer_bootstrap);}catch(Exception e){}
                            buffer_bootstrap="";
                         }
                       }
                       if (last_ctl_char==','||current==null) {
                            node offspring=new node(buffer);
                            current=addBranch(head, offspring,lenght,level);
                            current.bootstrap=bootstrap;
                        } else {
                            current.lenght=lenght;
                            current.bootstrap=bootstrap;
                        }
                    }
                } else if (isTopology()) {
                    node offspring=new node("");
                    current=addBranch(head, offspring,lenght,level);
                    current.bootstrap=bootstrap;
                }   //--End buffer !=empty
                buffer="";
                last_ctl_char=current_char;
            } else
            if (current_char==')') {             
                //--CASE 1. --New node?
                if (buffer.length()>0) {
                    double lenght=1.0;
                    double bootstrap=0;
                    //-- No. just some bootstrap info for previous group
                     if (buffer.startsWith(":")) {
                        //--Length
                        String len=buffer.substring(buffer.indexOf(":")+1);
                        try {lenght=Double.valueOf(len);}catch(Exception e){e.printStackTrace();}
                        //--bootstrap
                        if (buffer_bootstrap.length()>0) {
                            try {bootstrap=Double.valueOf(buffer_bootstrap);}catch(Exception e){e.printStackTrace();}
                            buffer_bootstrap="";
                        }
                        if (current!=null) {
                            current.lenght=lenght;
                            current.bootstrap=bootstrap;
                        }
                    //--Yes. Get bootstrap and add the new node
                    } else {                                   
                        if (buffer.indexOf(":")>-1) {
                            //System.out.println(current+"\t"+buffer+"\t"+ last_ctl_char);
                            String len=buffer.substring(buffer.indexOf(":")+1);
                            try {lenght=Double.valueOf(len);}catch(Exception e){e.printStackTrace();}
                            buffer=buffer.substring(0, buffer.indexOf(":"));
                             if (buffer_bootstrap.length()>0) {
                                try {bootstrap=Integer.valueOf(buffer_bootstrap);}catch(Exception e){e.printStackTrace();}
                                buffer_bootstrap="";
                            }
                        } 
                        if (last_ctl_char==',') {
                            node offspring=new node(buffer);
                            current=addBranch(head, offspring,lenght,level);
                            current.bootstrap=bootstrap;
                        } else {
                            //if (buffer.length()>0&&current.source.name.equals("")) current.source.name=buffer;
                            current.lenght=lenght;
                            current.bootstrap=bootstrap;
                        }
                    }
                    level--;
                    //--In anycase, we need to return to the previous level (parent node)
                    return;
                //CASE 2. Buffer empty, return to previous level
                } else {
                    if (isTopology()) {
                        node offspring=new node(buffer);
                        current=addBranch(head, offspring,1.0,level);                        
                    }
                    return;
                }               
            } else {
             if (!flag_bootstrap) {
                 buffer+=current_char;
             } else buffer_bootstrap+=current_char;
            }
            index++;
            last_char=current_char;
        }
        //--Collapse unused node
        
    } //--End parse

    public void collapse() {
//       for (int i=edge_list.size()-1;i>-1;i--) {
//            edge e=edge_list.get(i);
//            if (e.source.name.isEmpty()&&e.dest.name.isEmpty()) {
//               //--find all dest
//                for (edge t:edge_list) {
//                    if (e.dest.equals(t.source)) {
//                        t.source=e.source;
//                    }
//                }
//            }
//        }
    }

    public void PrintTrex() {
        //for(i=1;i<=2*n-3-kt;i++) System.out.println(ARETE[2*i-1]+"--"+ARETE[2*i-2]+" : "+LONGUEUR[i-1]+" ("+bootstrap[i-1]+")");
        for (node n:node_list) {
            if (n.isleaf) System.out.println(n);
        }
        for(edge e:edge_list) {
            System.out.println(e.source+" "+e.dest+" ("+e.lenght+")");
        }
        
        
    }
    
    public String PrintOut() {
        System.out.println("Tree: "+getName());
//        double total_height=this.getHeight(node_list.get(0)); //--Height of the root
//        for (node n:node_list) {
//            //System.out.println(e.source+"-"+e.lenght+"->"+e.dest);
//            //for (int i=0; i<total_height-this.getHeight(n);i++) System.out.print(" ");
//
//                //for (int i=0; i<total_height-this.getHeight(n);i++) System.out.print(" ");
//                for (int i=0; i<this.getDistance(n);i++) System.out.print(" ");
//                System.out.println(n+" - "+this.getDistance(n));
//
//
//        }
        StringBuilder st=new StringBuilder();
         this.drawTree3(getNode_list().get(1),0,st);
         System.out.println(st.toString());
         System.out.println("Time to load "+getName()+" : "+ttime/1000+" ms");
         System.out.println("Total node : "+getNode_list().size()+" total edge : "+getEdge_list().size()+" total leaf : "+getTotalLeaf());
         return st.toString();
    }

     public String PrintNewick() {
         StringBuilder st=new StringBuilder();
         if (getNode_list().size()<2) return "";
         
         this.drawTreeNewick(getNode_list().get(1),0,st);
         //--Add last add ;
         st.append(";");
         //System.out.println(st.toString());
         //System.out.println("Time to load "+name+" : "+ttime+" ms");
         //System.out.println("Total node : "+getNode_list().size()+" total edge : "+getEdge_list().size()+" total leaf : "+getTotalLeaf());
         return removeSpace(st.toString());
    }

     public String PrintInfo() {         
          StringBuilder st=new StringBuilder();
          st.append("Information for "+this.getName()+"\n");
          st.append("--------------------------------------\n");
          st.append("Original tree:\n");
          st.append(this.newick_string+"\n");
          st.append("Tree without bootstrap:\n");
          st.append(this.PrintNewick()+"\n");
          st.append("Tree without branch-length:\n");
          st.append(this.PrintNewickWithoutBranchLength()+"\n");
          
          st.append("Terminal node (name|encoded distance):\n");
          st.append("--------------------------------------\n");
          for (node n:this.node_list) {
             st.append(n.name+" "+(n.isleaf?"leaf":""+n.id+" internal")+"\n");
          }          
          st.append("Terminal node without numbered (name|encoded distance):\n");
          st.append("--------------------------------------\n");
          this.removeAllNumberedNodes();
          for (node n:this.node_list) {
              if (n.isleaf) st.append(n.name+"\n");
          }
           st.append("Tree without bootstrap:\n");
          st.append(this.PrintNewick()+"\n");
          st.append("--------------------------------------\n");
          st.append("Total node : "+getNode_list().size()+"\nTotal edge : "+getEdge_list().size()+"\nTotal leaf : "+getTotalLeaf()+"\n");          
          return st.toString();
     }
     
     
     /**
      * Warning, really important for LatTrans, do not edit...
      * @return 
      */
      public String PrintNewickWithoutBranchLength() {
         StringBuilder st=new StringBuilder();
         if (getNode_list().size()<2) return "";
         
         this.drawTreeNewickWithoutBranchLength(getNode_list().get(1),0,st);
         //--Add last add ;
         st.append(";");
         //System.out.println(st.toString());
         //System.out.println("Time to load "+name+" : "+ttime+" ms");
         //System.out.println("Total node : "+getNode_list().size()+" total edge : "+getEdge_list().size()+" total leaf : "+getTotalLeaf());
         return removeSpace(st.toString());
    }
      
    public void CompactPrintOut() {
        System.out.println("Tree: "+getName());
        for (edge e:getEdge_list()) {
            //--We dont list empty node...
          if (e.dest.name.length()>0) System.out.println(e.source+"->"+e.dest);
        }
    }

    public String outputGraphViz() {
        StringBuilder graph=new StringBuilder();
        graph.append("digraph G {\n");
        for (edge e:getEdge_list()) {
            String source=""+e.source.id;
            String dest=""+e.dest.id;
            if (e.source.name.length()>0) source=e.source.getName();
            if (e.dest.name.length()>0) dest=e.dest.getName();
            graph.append(source+" -> "+dest+";\n");
        }
        graph.append("}\n");
        return graph.toString();
    }

    public void outputGraphViz(String filename) {
        try {
            PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
            pw.println(outputGraphViz());
            pw.flush();
            pw.close();
        } catch(Exception e) {}
    }

    public String replace(String str, char source, char dest) {
        String tmp="";
        for (char c:str.toCharArray()) tmp+=(c==source?dest:c);
        return tmp;
    }

    public int getTotalLeaf() {
        int count=0;
        for (node e:getNode_list()) {
            if (e.isIsleaf()) count++;
        }
        return count;
    }
    
    /**
     * This replace the name of each node with the numbering relative to the source_node
     * i.e. we look for the name in the source node and if found, we use this node ID
     * @param source_node
     * @return a new string
     */
    public String replaceRef_by_Number(newick_tree source_node) {
        // 1. rename this node to source node numbering
        // N^2
        int count=0;
        //--Note we use the node node.color as number...
        for (int i=0; i<this.node_list.size();i++) {
            node tmp=node_list.get(i);
            if (tmp.isleaf) {
                for (node tmp_source:source_node.node_list) {
                    if (tmp_source.name.toLowerCase().trim().equals(tmp.name.toLowerCase().trim())) {
                        if (tmp_source.color==-1) {
                            count++;
                            tmp.name=""+count;
                            tmp.color=count;
                            tmp_source.color=count;
                        } else {
                            tmp.name=""+tmp_source.color;
                            tmp.color=tmp_source.color;                            
                        }
                        node_list.remove(i);
                        node_list.add(i, tmp);
                    }                
                }            
            }
        }
        if (count!=this.getTotalLeaf()) {
            System.out.println("Error with one leaf in replacing with number...");
        }
        String tmp="tree "+this.getName()+" "+this.PrintNewickWithoutBranchLength();
        return tmp;
    }
    
     public String replaceRef_by_Number() {
        // 1. rename this node to source node numbering
        // N^2
        for (int i=0; i<this.node_list.size();i++) {
            node tmp=node_list.get(i);    
            if (tmp.isleaf) {
                    tmp.name=""+tmp.color;
                    node_list.remove(i);
                    node_list.add(i, tmp);
            }       
                               
            }                    
        String tmp="tree "+this.getName()+" "+this.PrintNewick();
        return tmp;
    }
    
    public String outputNewus() {
        return "TREE "+this.getName()+"  "+this.PrintNewick();
    } 

    public String outputNewus_withoutBranchLength() {
        return "TREE "+this.getName()+"  "+this.PrintNewickWithoutBranchLength();
    } 
    
   public void drawTree(node n, double distance_parent) {
        double distance=this.getDistance(n);

        Vector<node> child=this.getChild(n);
        if (n.isIsleaf()) {
            for (int i=0;i<distance_parent;i++) System.out.print(" ");
            System.out.print("|");
            for (double i=distance_parent;i<distance;i++) System.out.print("-");
            System.out.println("-"+n.getName()+" "+distance);
        } else {
            drawTree(child.get(0), distance);
            for (int i=0;i<distance_parent;i++) System.out.print(" ");
            for (double i=distance_parent;i<distance;i++) System.out.print("-");
            System.out.println("| "+distance);
            for (int i=1; i<child.size();i++) {
                 drawTree(child.get(i), distance);
            }
        }

    }


    /**
     *
     * @param n
     * @param distance_parent position de '|'
     */
     public void drawTree3(node n, double distance_parent) {
        StringBuilder st=new StringBuilder();
         //double distance=this.getDistance(n);
        //double depth=this.getDistance(n);       
        Vector<node> child=this.getChild(n);
        //for (node c:child) c.distance=this.getDistance(c);
        if (n.isIsleaf()) {
            for (double i=0;i<n.depth;i++) System.out.print(" ");
            String tt="";
            for (double i=0; i<getDistance(n);i++) tt+="-";
            System.out.println("|"+tt+n.getName()+" ");
            //System.out.println(previous+n.name);
        } else {
            for (int i=0;i<n.depth;i++) System.out.print(" ");
            System.out.println(n.getParent().id);
            for (int i=0;i<child.size();i++) {
                 drawTree3(child.get(i),0);
            }    
        }
        
    }

     /**
     *
     * @param n
     * @param distance_parent position de '|'
     */
     public void drawTree3(node n, double distance_parent, StringBuilder st) {
        Vector<node> child=this.getChild(n);
        if (n.isIsleaf()) {
            for (double i=0;i<n.depth;i++) st.append(" ");
            String tt="";
            double distance=getDistance(n);
            String tt2=""+distance;
            for (double i=0; i<distance;i++) tt+="-";            
            st.append("|"+tt+n.getName()+" ("+tt2+")\n");
        } else {
            for (int i=0;i<n.depth;i++) st.append(" ");
            st.append(n.getParent().getName()+"\n");
            for (int i=0;i<child.size();i++) {
                 drawTree3(child.get(i),0,st);
            }
        }
    }

       /**
     *
     * @param n
     * @param distance_parent position de '|'
     */
     public void drawTreeNewick(node n, double distance_parent, StringBuilder st) {        
        Double distance=getEdgeDistance(n);
        String distance_string=String.format("%6f", distance).replace(',', '.');
        //if (n.name.equals("root")) System.out.println(n);
        
        if (n.isIsleaf()) {            
            //String tt2="1.0";
            st.append(n.getName());            
            //--Draw the distace
           
            st.append(":"+distance_string);
        } else {
            Vector<node> child=this.getChild(n);
            st.append("(");
            //System.out.println(child.size());
            for (int i=0; i<child.size();i++) {
                drawTreeNewick(child.get(i),0, st);
                if (i!=child.size()-1) st.append(",");
            }
            st.append(")");            
            //--Don't print ending if it's the first node...
            if (n.id!=1) st.append(":"+distance_string);
           
        }
        //distance=getEdgeDistance(n);
        //if (n.getName().equals("root")) st.append(":"+distance+"\n");
    }
     /**
      * Warning, important for LatTrans, do not edit...
      * @param n
      * @param distance_parent
      * @param st 
      */
       /**
     *
     * @param n
     * @param distance_parent position de '|'
     */
     public void drawTreeNewickWithoutBranchLength(node n, double distance_parent, StringBuilder st) {        
        //double distance=getEdgeDistance(n);
        if (n.isIsleaf()) {            
            //String tt2="1.0";
            st.append(n.getName());
        } else {
            Vector<node> child=this.getChild(n);
            st.append("(");
            //System.out.println(child.size());
            for (int i=0; i<child.size();i++) {
                drawTreeNewickWithoutBranchLength(child.get(i),0, st);
                if (i!=child.size()-1) st.append(", ");
            }
            st.append(")");
        }
        double distance=getEdgeDistance(n);
        //if (n.getName().equals("root")) st.append(":"+distance+"\n");
    }
     
      
      
      /**
     *
     * @param n
     * @param distance_parent position de '|'
     */
     public void drawTree4(node n, double distance_parent, StringBuilder st) {
        Vector<node> child=this.getChild(n);
        if (n.isIsleaf()) {
            for (double i=0;i<n.depth;i++) st.append(" ");
            st.append("|"+n.getName()+" "+"\n");
        } else {
            for (int i=0;i<n.depth;i++) st.append(" ");
            st.append(n.getParent().id+"\n");
            for (int i=0;i<child.size();i++) {
                 drawTree4(child.get(i),0,st);
            }
        }
    }

     public void drawTree2(node n) {
        double distance=this.getDistance(n);       
        if (n.isIsleaf()) {
            System.out.println(n.getName()+" "+distance);
        } else System.out.println(" ");
        for (node c:this.getChild(n)) {
             for (int i=0;i<distance;i++) System.out.print(" "); System.out.print("|-");
            drawTree2(c);
        }
    }
    
    public Vector<node> getChild(node n) {
        Vector<node>tmp=new Vector<node>(2);
        for (edge e:getEdge_list()) {
            if (e.source.equals(n)) tmp.add(e.dest);
        }
        return tmp;
    }

     public Vector<node> getParent(node n) {
        Vector<node>tmp=new Vector<node>(1);
        for (edge e:getEdge_list()) {
            if (e.dest.equals(n)) tmp.add(e.source);
        }
        return tmp;
    }

     public double getEdgeDistance(node source, node dest) {
         edge tmp=new edge(source, dest);
         int in=0;
         if ((in=getEdge_list().indexOf(tmp))>-1) {
             edge e=getEdge_list().get(in);
             //System.out.println(e.lenght);
             return e.lenght;
         }
         return 0.0;
     }

    /**
     * This return the distance with branch length from the root to this node
     * @param n
     * @return
     */
    public double getDistance(node n) {
        if (n.getName().equals("root")) return 0;
        double count=0;
        for (node c:getParent(n)) {
            count+=getEdgeDistance(c,n)+getDistance(c);
        }
        return (count);
    }

    public double getEdgeDistance(node n) {
        for (edge e:edge_list) {
            if (e.dest==n) return e.lenght;
        }
        return 1.0;
    }


     /**
     * This return the distance with branch length from the root to this node
     * @param n
     * @return
     */
    public double getDepth(node n) {
         if (n.getName().equals("root")) return 0;
        double count=0;
        for (node c:getParent(n)) {
            count+=1+getDepth(c);
        }
        return (count);
    }

     /**
     * This return the distance with branch length from the root to this node
     * @param n
     * @return
     */
    public double getErrorDepth(node n) {
        if (n.isIsleaf()) return 0;
        double max=0;
        for (node c:getChild(n)) {
            double tmp=getDistance(c);
            if (tmp>max) max=tmp;
        }
        return (max+getDistance(n));
    }


    /**
     * This return the Depth of this branch in the tree
     * Note: if we give the root, it give the total height of the tree
     * @param n
     * @return
     */
    public double getHeight(node n) {
        if (n.isIsleaf()) return 1;
        double count=0;
        for (node c:getChild(n)) count+=getHeight(c);
        return (count);
    }

    /**
     * @return the node_list
     */
    public ArrayList<node> getNode_list() {
        return node_list;
    }

    /**
     * @return the edge_list
     */
    public ArrayList<edge> getEdge_list() {
        return edge_list;
    }
    
    //--TO DO
   private void randomTree(int leaf) {
        
//        if (leaf<1) return;
//        this.edge_list.clear();
//        this.node_list.clear();
//        node root=new node("root");
//        
//        for (int i=0; i<leaf; i++) {
//           
//        }        
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public int removeAllNumberedNodes() {
        int count=0;
        for (int i=this.node_list.size()-1; i>-1; i--) {
            node n=node_list.get(i);
            if (n.isleaf) {
                try {
                    Double d=Double.valueOf(n.getName());
                    node_list.remove(i);
                    for (edge e:this.edge_list) {
                        if (e.dest==n) {
                            count++;
                            edge_list.remove(n);
                            break;
                        }
                        
                    }
                    
                } catch (Exception e) {}
            }
        }
        return count;
        
    }

    /**
     * This check if we have numeric value as leaf or not
     * @return 
     */  
    public boolean isNumberedSpecies() {
        int count=0;
        int count_leaf=getTotalLeaf();
        for (int i=this.node_list.size()-1; i>-1; i--) {
            node n=node_list.get(i);
            if (n.isleaf) {
                try {
                    Double d=Double.valueOf(n.getName());                    
                    count++;
                } catch (Exception e) {}
            }
        }
        //--Numbered Species if count > 95
        return (count*100/count_leaf) > 95;        
    }
    
    String removeSpace(String s) {
        return s.replaceAll(" ", "");
    }

    public String getNameMatrix() {
        StringBuilder st=new StringBuilder();
        st.append("Leaf name\tNumbered leaf names\n");
        for (node n:this.node_list) {
            if (n.isleaf) st.append(n.name.trim()+"\t"+n.color+"\n");
        }
        
        
        return st.toString();
    }
    
      /**
     * Retourne le degree d'un noeud
     * @param n
     * @return the degree of the node n
     */
    public int getDegree(node n) {
        int count=0;
        for (edge e:this.edge_list) {
            if (e.source==n) count++;
        }        
        return count;
    }
    
    public boolean isRooted() {
       //--Look at the degree of the node 1
        if (node_list.size()<3) return false;
        //--Is one child of first node is a leaf?
        boolean found_one_child=false;
        for (node n: this.getChild(node_list.get(1))) {
         if (n.isleaf) found_one_child=true;
        }        
        return (getDegree(node_list.get(1))==3)&&found_one_child;                  
    }
    
    public boolean isBinary() {
        for (node n:node_list) {
            if (getDegree(n)>2) return false;
        }
        return true;
    }

    /**
     * Determine if a tree is only topology i.e. ((,),(,));
     * @return true or false
     */
    public boolean isTopology() {
        for (char c:this.newick_string.toCharArray()) {
            if (c!=','||c!='('||c!=')'||c!=' '||c!=';') return false;
        }
        return true;
    }
    
     public static void main(String[] args) {
         newick_tree n=new newick_tree();
//         n.parseNewick("((raccoon, bear),((sea_lion,seal),((monkey,cat), weasel)),dog);");         
//         System.out.println(n.PrintInfo()+"\nbinary:"+n.isBinary()+"\trooted:"+n.isRooted());
//         n.parseNewick("((D:0.723274,F:0.567784)1.000000:0.067192,(B:0.279326,H:0.756049)1.000000:0.807788);");
//         System.out.println(n.PrintInfo()+"\nbinary:"+n.isBinary()+"\trooted:"+n.isRooted());         
//         n.parseNewick("((raccoon:19.19959,bear:6.80041):0.84600,((sea_lion:11.99700, seal:12.00300):7.52973,((monkey:100.85930,cat:47.14069):20.59201, weasel:18.87953):2.09460):3.87382,dog:25.46154);");
//         System.out.println(n.PrintInfo()+"\nbinary:"+n.isBinary()+"\trooted:"+n.isRooted());
//         
//         n.parseNewick("((raccoon:19.19959,bear:6.80041)50:0.84600,((sea_lion:11.99700, seal:12.00300)100:7.52973,((monkey:100.85930,cat:47.14069)80:20.59201, weasel:18.87953)75:2.09460)50:3.87382,dog:25.46154);");
//         System.out.println(n.PrintInfo()+"\nbinary:"+n.isBinary()+"\trooted:"+n.isRooted());         
//         n.parseNewick("((raccoon:19.19959,bear:6.80041):0.84600[50],((sea_lion:11.99700, seal:12.00300):7.52973[100],((monkey:100.85930,cat:47.14069):20.59201[80], weasel:18.87953):2.09460[75]):3.87382[50],dog:25.46154);");
//         System.out.println(n.PrintInfo()+"\nbinary:"+n.isBinary()+"\trooted:"+n.isRooted());
         n.parseNewick("(A,B,(C,D)E)F;");
         System.out.println(n.PrintInfo()+"\nbinary:"+n.isBinary()+"\trooted:"+n.isRooted());
//         n.parseNewick("(mammal:0.14,(turtle:0.02,(rayfinfish:0.25,(frog:0.01,salamander:0.01):0.12):0.09):0.03);");
//         System.out.println(n.PrintInfo()+"\nbinary:"+n.isBinary()+"\trooted:"+n.isRooted());
         n.parseNewick("(mammal:0.14,turtle:0.02,(rayfinfish:0.25,(frog:0.01,salamander:0.01)50:0.12)95:0.09);");
         System.out.println(n.PrintInfo()+"\nbinary:"+n.isBinary()+"\trooted:"+n.isRooted());
     }
    
} //--End newick_tree class
