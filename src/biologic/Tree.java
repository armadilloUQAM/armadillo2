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



package biologic;

import biologic.seqclasses.parserNewick.newick_tree;
import biologic.seqclasses.parserNewick.node;
import configuration.Config;
import configuration.Util;
import database.databaseFunction;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import workflows.workflow_properties;

/**
 *
 * @author Etienne Lord
 */
public class Tree implements Biologic, Iterator, Serializable {
    
    private int id=0;
    private int runProgram_id=0;
    private String tree="";
    private String name="UnknownTree";
    private String treeSequenceID=""; //This is the tree with sequences id
    private String treeAbbreviate="";
    private String note="";
    private int alignment_id=0;
    private int multiplesequences_id=0;
    private boolean rooted=false;
    private boolean selected=false;
    private boolean modified=false;
    
    private boolean selected_for_remove=false;   //mark for remove from project
    private boolean selected_for_grouping=false;   //mark for remove from project
    
    public static databaseFunction df=new databaseFunction();
    
    public Tree()                {}
    public Tree(int id)          {this.loadFromDatabase(id);}
    public Tree(String filename) {this.loadFromFile(filename);}
    
    ////////////////////////////////////////////////////////////////////
    /// Database function
    
    public boolean loadFromDatabase(int id) {
        Tree newtree=df.getTree(id);
        if (newtree.id>0) {
            this.setAlignment_id(newtree.getAlignment_id());
            this.setTree(newtree.getTree());
            this.setTreeAbbreviate(newtree.getTreeAbbreviate());
            this.setTreeSequenceID(newtree.getTreeSequenceID());
            this.setName(newtree.getName());
            this.setNote(newtree.getNote());
            this.setId(newtree.getId());
            this.setRunProgram_id(newtree.getRunProgram_id());
            this.id=id;
            return true;
        } else return false;
    }
    
    public boolean saveToDatabase() {
        id=df.addTree(this);
        return (id==0?false:true);
    }
    
    public boolean removeFromDatabase() {
        return df.removeTree(this);
    }
    
    public boolean updateDatabase() {
        return df.updateTree(this);
    }
    
    public boolean loadFromFile(String filename) {
        if (!Util.FileExists(filename)) return false;
        if (!readNewick(filename)) {
            return readNexus(filename);
        }
        return true;
    }
    
////////////////////////////////////////////////////////////////////////
/// Iterator
    
    Vector<Integer>next=new Vector<Integer>();
    int counter=0;
    int maxid=-1;
    public boolean hasNext() {
        //next=df.getAllSequenceID();
        if (next.size()==0) {
            next=df.getAllTreeID();
            maxid=next.size();
        }
        return (this.counter<maxid);
    }
    
    public Object next() {
        return new Tree(next.get(counter++));
    }
    
    public void remove() {
        Tree s=new Tree(counter-1);
        s.removeFromDatabase();
    }
    
    @Override
    public Vector<Integer> getAllId() {
        return next;
    }
    
    public boolean exists(Integer id) {
        return df.existsTree(id);
    }
    
    ////////////////////////////////////////////////////////////////////
    // Tree functions
    
    public boolean replaceSequenceIdWithNames() {
        //--Construct hashmap
        Pattern find_sequence=Pattern.compile("AZ([0-9]*)");
        Matcher m=find_sequence.matcher(tree);
        while(m.find()) {
            try {
                Sequence sequence=new Sequence(Integer.valueOf(m.group(1)));
                if (sequence!=null) {
                    String group=m.group();
                    tree=tree.replace(group, sequence.name);
                    treeAbbreviate=treeAbbreviate.replace(group, sequence.getAbbreviate());
                }
            } catch(Exception e) {}
        }
        return true;
    }
    
    
    /**
     * This is a general procedure to remove artifact found in newick tress
     */
    public void removeBootstrap() {
        //--remove bootstrap from PhyML tree
        
        Pattern bootstrap_phylip=Pattern.compile("[)]([0-9]*[.][0-9]*):([0-9]*[.][0-9]*)");
        Matcher m=bootstrap_phylip.matcher(tree);
        
        while(m.find()) {
//              Config.log(m.group(1)+" "+m.group(2));
//              Config.log(m.start(1)+" "+m.end(1));
            tree=tree.replace(m.group(), "):"+m.group(2));
        }
    }
    
    /**
     * This is a general procedure to remove artifact found in newick tress
     */
    public void removeSpaceBeforeLength() {
        //--remove bootstrap from fastDNAml tree
        
        Pattern bootstrap_phylip=Pattern.compile("[)]\\s:([0-9]*[.][0-9]*)");
        Matcher m=bootstrap_phylip.matcher(tree);
        
        while(m.find()) {
            
//              Config.log(m.group(1)+" "+m.group(2));
//              Config.log(m.start(1)+" "+m.end(1));
            tree=tree.replace(m.group(), "):"+m.group(1));
        }
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * @return the runProgram_id
     */
    public int getRunProgram_id() {
        return runProgram_id;
    }
    
    /**
     * @param runProgram_id the runProgram_id to set
     */
    public void setRunProgram_id(int runProgram_id) {
        this.runProgram_id = runProgram_id;
    }
    
    /**
     * @return the tree
     */
    public String getTree() {
        return tree;
    }
    
    /**
     * @param tree the tree to set
     */
    public void setTree(String tree) {
        this.tree = tree;
    }
    
    /**
     * @return the note
     */
    public String getNote() {
        return note;
    }
    
    /**
     * @param note the note to set
     */
    public void setNote(String note) {
        this.note = note;
    }
    
    /**
     * TO DO Create a Newick parser here!
     * @return
     */
    @Override
    public String toString() {
        //Alignment align=new Alignment(this.getAlignment_id());
        return this.getTree();
    }
    
    /**
     * @return the alignment_id
     */
    public int getAlignment_id() {
        return alignment_id;
    }
    
    /**
     * @param alignment_id the alignment_id to set
     */
    public void setAlignment_id(int alignment_id) {
        this.alignment_id = alignment_id;
    }
    
    /**
     * @return the treeAbbreviate
     */
    public String getTreeAbbreviate() {
        return treeAbbreviate;
    }
    
    /**
     * @param treeAbbreviate the treeAbbreviate to set
     */
    public void setTreeAbbreviate(String treeAbbreviate) {
        this.treeAbbreviate = treeAbbreviate;
    }
    
    /**
     * @return the treeSequenceID
     */
    public String getTreeSequenceID() {
        return treeSequenceID;
    }
    
    /**
     * @param treeSequenceID the treeSequenceID to set
     */
    public void setTreeSequenceID(String treeSequenceID) {
        this.treeSequenceID = treeSequenceID;
    }
    
    public boolean outputNewickWithSequenceID(String filename) {
        try {
            PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
            pw.println(this.getTreeSequenceID());
            pw.flush();
            pw.close();
            return true;
        }catch(Exception e) {return false;}
    }
    
    public boolean outputNewickAbbreviate(String filename) {
        try {
            PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
            pw.println(this.getTreeAbbreviate());
            pw.flush();
            pw.close();
            return true;
        }catch(Exception e) {return false;}
    }
    
    public boolean outputNewick(String filename) {
        try {
            PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
            pw.println(this.getTree());
            pw.flush();
            pw.close();
            return true;
        }catch(Exception e) {return false;}
    }
    
    /**
     * Warning, read newick tree but only the first one
     * *Better use MultipleTrees.readNewick(String filename)
     * @param filename
     * @return
     */
    public boolean readNewick(String filename) {
        if (Config.library_mode) System.out.println("Read Newick tree from "+filename);
        try {
            BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
            String Treea="";
            
            while (br.ready()) {
                String stri=br.readLine();
                Treea+=stri.trim();
                if (stri.startsWith("#NEXUS")) {
                    if (Config.library_mode) System.out.println("Tree is Nexus");
                    //--Wrong read nexus..
                    return false;
                }
                if (stri.endsWith(";")) {
                    int index=Treea.indexOf("[");
                    if (index>-1) Treea=Treea.substring(0,index); //Remove annotation
                    Treea=Treea.replaceAll(";", "");              //Remove ; Ending
                    setNote("From "+filename+" on "+Util.returnCurrentDateAndTime());
                    setTree(Treea+";");
                    setTreeAbbreviate(Treea+";");
                    setTreeSequenceID(Treea+";");
                    replaceSequenceIdWithNames();
                    br.close();
                    return true;
                }
            }
            
        } catch(Exception e) {e.printStackTrace();return false;}
        return false;
    }
    
    /**
     * Warning, read nexus tree but only the first one
     * *Better use MultipleTrees.readNexus(String filename)
     * See http://hydrodictyon.eeb.uconn.edu/eebedia/index.php/Phylogenetics:_NEXUS_Format
     * and http://molecularevolution.org/resources/treeformats for format
     * @param filename
     * @return
     */
    public boolean readNexus(String filename) {
        if (Config.library_mode) System.out.println("Read Nexus tree from "+filename);
        try {
            BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
            String Treea="";
            boolean nexus_flag=false;
            boolean inside_tree_block=false;
            Pattern p_taxon=Pattern.compile("([0-9]{1,})\\s+(\\S+)[,;]", Pattern.CASE_INSENSITIVE);
            Pattern p_tree=Pattern.compile("tree\\s+(\\S+)\\s+[=]+\\s+(.*)", Pattern.CASE_INSENSITIVE);
            Pattern p_parameters=Pattern.compile("\\[(.*?)\\]");
            ArrayList<String>parameters=new ArrayList<String>();
            HashMap<String,String>taxon_names=new HashMap<String,String>();
            
            
            while (br.ready()) {
                String stri=br.readLine().trim();
                if (stri.startsWith("#NEXUS")) {
                    nexus_flag=true;
                }
                if (stri.toLowerCase().startsWith("begin trees;")) {
                    //-- debug System.out.println("Inside trees");
                    inside_tree_block=true;
                }
                if (stri.toLowerCase().startsWith("end;")) {
                    taxon_names.clear();
                    
                    inside_tree_block=false;
                }
                if (nexus_flag&&inside_tree_block) {
                    if (stri.toLowerCase().startsWith("translate")) {} //--Do nothing
                    Matcher m_taxon=p_taxon.matcher(stri);
                    Matcher m_tree=p_tree.matcher(stri);
                    if (m_tree.find()) {
                        //-- debug System.out.println("tree find "+m_tree.group());
                        parameters.clear();
                        Treea=m_tree.group(2);
                        
                        //--Remove the parameters from tree...
                        Matcher m_parameters=p_parameters.matcher(Treea);
                        while (m_parameters.find()) {
                            parameters.add(m_parameters.group(1));
                        }
                        for (String param:parameters) Treea=Treea.replaceAll("\\["+param+"\\]", "");
                        //--Replace all taxon (number)->name...
                        newick_tree n=new newick_tree();
                        n.parseNewick(Treea);
                        for(node nod:n.node_list) {
                            if (nod.isleaf&&(taxon_names.containsKey(nod.getName()))) {
                                nod.setName(taxon_names.get(nod.getName()));
                            }
                        }
                        this.tree=n.PrintNewick();
                        this.replaceSequenceIdWithNames();
                        this.name=m_tree.group(1);
                    } else
                        if (m_taxon.find()) {
                            //-- debug System.out.println(m_taxon.group());
                            taxon_names.put(m_taxon.group(1), m_taxon.group(2));
                        }
                    
                } //--End nexus block
                
            }
            br.close();
        } catch(Exception e) {e.printStackTrace();return false;}
        return true;
    }
    
    
    public workflow_properties returnProperties() {
        workflow_properties tmp=new workflow_properties();
        if (id==0) this.saveToDatabase();
        tmp.put("input_tree_id", this.getId());
        tmp.put("output_tree_id", this.getId());
        return tmp;
    }
    
    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    /**
     * @return the selected_for_remove
     */
    public boolean isSelected_for_remove() {
        return selected_for_remove;
    }
    
    /**
     * @param selected_for_remove the selected_for_remove to set
     */
    public void setSelected_for_remove(boolean selected_for_remove) {
        this.selected_for_remove = selected_for_remove;
    }
    
    /**
     * @return the selected_for_grouping
     */
    public boolean isSelected_for_grouping() {
        return selected_for_grouping;
    }
    
    /**
     * @param selected_for_grouping the selected_for_grouping to set
     */
    public void setSelected_for_grouping(boolean selected_for_grouping) {
        this.selected_for_grouping = selected_for_grouping;
    }
    
    /**
     * @return the multiplesequences_id
     */
    public int getMultiplesequences_id() {
        return multiplesequences_id;
    }
    
    /**
     * @param multiplesequences_id the multiplesequences_id to set
     */
    public void setMultiplesequences_id(int multiplesequences_id) {
        this.multiplesequences_id = multiplesequences_id;
    }
    
    /**
     * @return the rooted
     */
    public boolean isRooted() {
        return rooted;
    }
    
    /**
     * @param rooted the rooted to set
     */
    public void setRooted(boolean rooted) {
        this.rooted = rooted;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getCompatibleName() {
        char sep=File.separatorChar;
        return this.name.replace(" ","_").replace("(", "_").replace(")", "_").replace("[", "_").replace("]", "_").replace(':','.').replace(sep,'_');
    }
    
    
    public String getBiologicType() {
        return "Tree";
    }
    
    public String toHtml() {
        String str ="";
        //str = "<div class=\"tree\">\n"+this.getTree()+"</div>\n";
        str = this.getTree();
        return str;
    }
    
    public String getNameId(int id) {
        return df.getTreeName(id);
    }
    
    public String getFileNameId(int id) {
        return "";
    }
    
    public void setName(String name) {
        this.name=name;
    }
    
    public void setData(String data) {
        this.setTree(data);
        this.setTreeAbbreviate(data);
        this.setTreeSequenceID(data);
    }
    
    /**
     * @return the modified
     */
    public boolean isModified() {
        return modified;
    }
    
    /**
     * @param modified the modified to set
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }
    
    public String getFasta() {
        return "";
    }
    
    public String getPhylip() {
        return "";
    }
    
    public String getExtendedString() {
        return this.getId()+"; "+this.getName()+"; "+this.getNote()+"; "+this.getTree()+"; "+this.getTreeAbbreviate()+"; "+this.getTreeSequenceID();
    }
    
    public void generateHGT() {
        //--Get the tree
        newick_tree t=new newick_tree();
        t.parseNewick(this.getTree());
        //--Find two non adjacent node
        //--Find a leaf
        int i=(int) ((Math.random())*t.getTotalLeaf());
        while (!t.getNode_list().get(i).isleaf) {
            i=(int) ((Math.random())*t.getTotalLeaf());
        }
        //--find next leaf
        int j=(int) ((Math.random())*t.getTotalLeaf());
        while (!t.getNode_list().get(j).isleaf||i==j) {
            j=(int) ((Math.random())*t.getTotalLeaf());
        }
        String name_tmp=t.getNode_list().get(i).getName();
        //--Exchange name
        t.getNode_list().get(i).setName(t.getNode_list().get(j).getName());
        t.getNode_list().get(j).setName(name_tmp);
        this.tree=t.PrintNewick();
    }
    
    public void generateRandomTree(int leaf) {
//        newick_tree t=new newick_tree();
//        //t.randomTree(leaf);
//        this.tree=t.PrintNewick();
    }
    
    /**
     * Get the next valid Id found in the database
     * @param start the current id...
     * @return the next id or 0 if not found
     */
    public int getNextValidId() {
        if (!hasNext()) return 0;
        for (int i=0;i<next.size();i++) {
            if (next.get(i)==this.id) return next.get(i+1);
        }
        //--Unable to find the current ID? -> Find the first higher...
        for (int i=0;i<next.size();i++) {
            if (next.get(i)>this.id) return next.get(i);
        }
        //--If not, return 0;
        return 0;
    }
    
    public Biologic getParent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
