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
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import workflows.workflow_properties;


public class MultipleTrees implements Biologic, Serializable {
    private Vector<Tree> trees=new Vector<Tree>();
    private int id=0;
    private int alignment_id=0;
    private int multiplesequences_id=0;
    private String name="UnknownMultipleTrees";
    private String note="";
    private int runProgram_id=0;
    public static databaseFunction df=new databaseFunction();
    
    public MultipleTrees() {}
    
    public MultipleTrees(int id) {
        this.loadFromDatabase(id);
    }
    
    public MultipleTrees(String filename) {
        loadFromFile(filename);
    }
    
    public boolean loadFromFile(String filename) {
        if (!Util.FileExists(filename)) return false;
        if (!readNewick(filename)) {
            return readNexus(filename);
        }
        return true;
    }
    
////////////////////////////////////////////////////////////////////////////////
/// Database function
    
    public boolean loadFromDatabase(int id) {
        MultipleTrees tree=df.getMultipleTrees(id);
        if (tree.getId()>0) {
            this.setId(tree.getId());
            this.setName(tree.getName());
            this.setNote(tree.getNote());
            this.setRunProgram_id(tree.getRunProgram_id());
            this.trees.clear();
            this.trees.addAll(tree.getTree());
            return true;
        } else return false;
    }
    
    public boolean saveToDatabase() {
        id=0;
        return (df.addMultipleTrees(this)==0?false:true);
    }
    
////////////////////////////////////////////////////////////////////////////////
/// Iterator
    
    Vector<Integer>next=new Vector<Integer>();
    int counter=0;
    int maxid=-1;
    public boolean hasNext() {
        //next=df.getAllSequenceID();
        if (next.size()==0) {
            next=df.getAllMultipleTreesID();
            maxid=next.size();
        }
        return (this.counter<maxid);
    }
    
    public Object next() {
        return new MultipleTrees(next.get(counter++));
    }
    
    @Override
    public Vector<Integer> getAllId() {
        return next;
    }
    
    public boolean exists(Integer id) {
        return df.existsMultipleTrees(id);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// Database Function
    
    public void remove() {
        MultipleTrees s=new MultipleTrees(counter-1);
        s.removeFromDatabase();
    }
    public boolean readNewick(String filename) {
        if (Config.library_mode) System.out.println("Read Nexus tree from "+filename);
        try {
            filename=filename.trim();
            BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
            String Treea="";
            int index_tree=0;
            while (br.ready()) {
                String stri=br.readLine();
                Treea+=stri.trim();
                if (stri.startsWith("#NEXUS")) {
                    if (Config.library_mode) System.out.println("Wrong. Tree is Nexus");
                    //--Wrong read nexus..
                    return false;
                }
                
                if (stri.endsWith(";")) {
                    int index=Treea.indexOf("[");
                    if (index>-1) Treea=Treea.substring(0,index); //Remove annotation
                    Treea=Treea.replaceAll(";", "");              //Remove ; Ending
                    Tree t=new Tree();
                    t.setName(name+"_"+index_tree++);
                    t.setNote("From "+filename+" on "+Util.returnCurrentDateAndTime());
                    t.setTree(Treea+";");
                    t.setTreeAbbreviate(Treea+";");
                    t.setTreeSequenceID(Treea+";");
                    t.replaceSequenceIdWithNames();
                    this.trees.add(t);
                    Treea=""; //--Remove buffer
                }
            }
            br.close();
            this.replaceSequenceIDwithNames();
            return true;
        } catch(Exception e) {
            return false;}
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
        if (Config.library_mode) System.out.println("Read Nexus tree(s) from "+filename);
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
                        Tree tmp=new Tree();
                        tmp.setNote("Loaded from "+filename);
                        tmp.setTree(n.PrintNewick());
                        tmp.replaceSequenceIdWithNames();
                        tmp.setName(m_tree.group(1));
                        this.trees.add(tmp);
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
    
    /**
     * Note: special function to read NewickTree from MrBayes
     * @author Med Amine Remita
     * @since Mars 2011
     * @param filename
     * @return
     */
    public boolean readNewickTreeFromMrBayes(String filename){
        //--Variables
        String Tree1=""; //--buffer
        String Tree2="";
        //--MrBayes ID -> Taxon name
        HashMap<Integer, String>translate=new  HashMap<Integer, String>(); //--Translation table
        //--Constant
        boolean in_translate=false;
        Pattern p_tree_name=Pattern.compile("([0-9]*)\\S*(.*)");
        try {
            BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
            
            
            while (br.ready()) {
                String stri=br.readLine();
                //1er arbre
                Tree1=stri.trim();
                //--1. Found a translation block
                if (Tree1.startsWith("translate")) {
                    in_translate=true;
                    //--2. End a translation bloc
                } else if (Tree1.startsWith(";")&&in_translate) {
                    in_translate=false;
                    //--3. Identity
                } else if (in_translate) {
                    Matcher m_tree_name= p_tree_name.matcher(Tree1);
                    if (m_tree_name.find()) {
                        String key=m_tree_name.group(1).trim();
                        String value=m_tree_name.group(2).trim();
                        if (value.endsWith(",")) value=value.substring(0,value.length()-1);
                        translate.put(Integer.valueOf(key), value);
                    }
                }
                //--4. Found tree
                else if (Tree1.contains("tree con_50_majrule")) {
                    int index1=Tree1.indexOf("(");
                    if (index1>-1) Tree1=Tree1.substring(index1);
                    this.trees.add(createTreeFromMrBayes(Tree1,filename, translate));       //fonction is below
                    //2eme arbre
                    while (br.ready()){
                        stri=br.readLine();
                        Tree2=stri.trim();
                        if(Tree2.contains("tree con_50_majrule")){
                            int index2=Tree2.indexOf("(");
                            if (index2>-1) Tree2=Tree2.substring(index2);
                            this.trees.add(createTreeFromMrBayes(Tree2,filename, translate));
                        }
                    }
                }
            }
            br.close();
            //--Replace mrbayes ID->taxon_ID
            
            return true;
        } catch(Exception e) {return false;}
    }
    
    /**
     * Helper function to readNewickTreeFromMrBayes
     * This remove annotation in [] found in the tree
     * @author Med Amine Remita
     * @since Mars 2011
     * @param filename
     * @return
     */
    private Tree createTreeFromMrBayes (String NewickTree, String filename, HashMap<Integer,String> translation_map ){
        Tree treea = new Tree ();
        treea.setNote("From "+filename+" on "+Util.returnCurrentDateAndTime());
        //--Clean the NewickTree
        //--System.out.println("createTreeFromMrBayes Before: "+NewickTree);
        NewickTree=NewickTree.replaceAll("[\\[](.*?)[\\]]", "");
        //--System.out.println("createTreeFromMrBayes After: "+NewickTree);
        //--Replace in inverse order
        ArrayList<Integer>values=new ArrayList<Integer>();
        for (Integer key:translation_map.keySet()) values.add(key);
        Collections.sort(values);
        
        for (int i=values.size()-1;i>-1;i--) {
            int key=values.get(i);
            //--debug System.out.println(key+"|"+translation_map.get(key));
            NewickTree=NewickTree.replaceAll(key+":", translation_map.get(key)+":");
        }
        treea.setTree(NewickTree);
        treea.setTreeAbbreviate(NewickTree);
        treea.setTreeSequenceID(NewickTree);
        treea.replaceSequenceIdWithNames();
        return treea;
    }
    
    
    /**
     * @return the tree
     */
    public Vector<Tree> getTree() {
        return trees;
    }
    
    
    public int getNbTrees() {
        return trees.size();
    }
    
    public boolean add(Tree tree) {
        return trees.add(tree);
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
        for (Tree tree:trees) {
            tree.setAlignment_id(this.alignment_id);
        }
    }
    
    /**
     * @return the runProgram_id
     */
    public int getRunProgram_id() {
        return runProgram_id;
    }
    
    public void setNote(String note) {
        this.note=note;
    }
    
    /**
     * @param runProgram_id the runProgram_id to set
     */
    public void setRunProgram_id(int runProgram_id) {
        this.runProgram_id = runProgram_id;
        for (Tree tree:trees) tree.setRunProgram_id(runProgram_id);
    }
    
    public void replaceSequenceIDwithNames() {
        for (Tree tree:trees) tree.replaceSequenceIdWithNames();
    }
    
    public void checkNewick() {
        for (Tree tree:trees) tree.removeBootstrap();
    }
    
    @Override
    public String toString() {
        String s="MultipleTrees with "+trees.size()+" tree(s)\n";
        for (Tree t:trees) s+=t+"\n";
        return s;
    }
    
    public boolean outputTrees (String filename) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            //Ugly, need better id of trees
            for (Tree st:this.trees) pw.println(st.getTreeSequenceID()+";");
            pw.flush();
            pw.close();
        } catch (Exception e) {return false;}
        return true;
    }
    
    
//     #NEXUS
//    Begin trees;
//            TREE 12S = ((((((((((Gobio_gobio:0.026771,Biwia_zezera:0.055974):0.008391,((Gnathopogon_elongatus:0.024070,Coreoleuciscus_splendidus:0.027791):0.006814,((Pungtungia_herzi:0.032874,Pseudorasbora_pumila:0.043372):0.016078,Sarcocheilichthys_variegatus:0.054302):0.004194):0.006059):0.006922,(Hemibarbus_longirostris:0.019741,(Hemibarbus_mylodon:0.024963,(Hemibarbus_labeo:0.001253,Hemibarbus_barbus:0.001293):0.011948):0.002606):0.020972):0.005417,(Zacco_sieboldii:0.035942,(Opsariichthys_uncirostris:0.010914,Opsariichthys_bidens:0.072080):0.018215):0.008109):0.005375,Tinca_tinca:0.024492):0.005532,((((Notemigonus_crysoleucas:0.027296,(Iberochondrostoma_lemmingii:0.035470,Alburnus_alburnus:0.015719):0.001951):0.007786,Pelecus_cultratus:0.014796):0.008986,Gila_robusta:0.025796):0.000692,(((Phenacobius_mirabilis:0.104502,(Notropis_stramineus:0.037613,(Cyprinella_spiloptera:0.042944,Cyprinella_lutrensis:0.031698):0.015889):0.010725):0.011599,Campostoma_anomalum:0.062827):0.015874,((Tribolodon_nakamurai:0.012640,Pseudaspius_leptocephalus:0.010028):0.011823,Phoxinus_perenurus:0.012232):0.024198):0.003392):0.019695):0.009213,(Ischikauia_steenackeri:0.006139,((Aphyocypris_chinensis:0.042027,Xenocypris_argentea:0.013723):0.001719,Chanodichthys_mongolicus:0.011978):0.001860):0.004992):0.011780,((Labeo_senegalensis:0.053271,Labeo_batesii:0.042980):0.026259,(((Gymnocypris_przewalskii:0.079663,Barbodes_gonionotus:0.034922):0.009486,Barbus_barbus:0.049212):0.002130,((Puntius_ticto:0.131424,Barbus_trimaculatus:0.220672):0.032330,(Cyprinus_carpio:0.012205,(Carassius_carassius:0.004578,Carassius_auratus:0.005896):0.011586):0.010457):0.005230):0.009381):0.052331):0.010765,((Rhodeus_uyekii:0.130365,Rhodeus_ocellatus:0.061435):0.019321,Acheilognathus_typus:0.073952):0.060434):0.092966,Esomus_metallicus:0.393058,Danio_rerio:0.229420);
//            TREE 16S = (((((((((((Biwia_zezera:0.064483,(((Hemibarbus_labeo:0.003324,Hemibarbus_barbus:0.006662):0.026760,Hemibarbus_longirostris:0.068001):0.007442,Hemibarbus_mylodon:0.043317):0.024887):0.004287,((Sarcocheilichthys_variegatus:0.066726,Gobio_gobio:0.043263):0.007857,(Gnathopogon_elongatus:0.047840,Coreoleuciscus_splendidus:0.059783):0.007999):0.005714):0.004020,(Pungtungia_herzi:0.048474,Pseudorasbora_pumila:0.051835):0.028613):0.008200,((Zacco_sieboldii:0.038399,(Opsariichthys_uncirostris:0.015518,Opsariichthys_bidens:0.038021):0.068060):0.012302,((Xenocypris_argentea:0.009722,Ischikauia_steenackeri:0.010016):0.001688,Chanodichthys_mongolicus:0.014265):0.004863):0.005509):0.005489,(Tinca_tinca:0.044740,(((Rhodeus_uyekii:0.134139,Rhodeus_ocellatus:0.085296):0.023718,Acheilognathus_typus:0.103513):0.086043,(((Puntius_ticto:0.090839,Barbus_trimaculatus:0.171460):0.023653,((Labeo_senegalensis:0.033177,Labeo_batesii:0.053425):0.029101,((Barbus_barbus:0.075715,Barbodes_gonionotus:0.050987):0.006823,(Cyprinus_carpio:0.032099,(Gymnocypris_przewalskii:0.079944,(Carassius_carassius:0.008787,Carassius_auratus:0.006470):0.026773):0.003774):0.001625):0.004597):0.011155):0.030390,(Esomus_metallicus:0.309994,Danio_rerio:0.222055):0.032534):0.015594):0.010478):0.002745):0.002898,Aphyocypris_chinensis:0.043152):0.025855,((((Tribolodon_nakamurai:0.017842,Pseudaspius_leptocephalus:0.020392):0.022775,Phoxinus_perenurus:0.031951):0.019523,Gila_robusta:0.072731):0.004494,(Pelecus_cultratus:0.025457,((Notemigonus_crysoleucas:0.034281,Alburnus_alburnus:0.035439):0.005074,Iberochondrostoma_lemmingii:0.047991):0.012464):0.008200):0.007005):0.018934,Campostoma_anomalum:0.090162):0.015375,Phenacobius_mirabilis:0.111018):0.019900,Notropis_stramineus:0.042255):0.026867,Cyprinella_spiloptera:0.034514,Cyprinella_lutrensis:0.026486);
//            TREE ND6 = ((((((((((((Acheilognathus_typus:0.496970,Rhodeus_ocellatus:0.534330):0.196121,Rhodeus_uyekii:0.547560):0.308233,(Sarcocheilichthys_variegatus:0.327164,Biwia_zezera:0.257470):0.092891):0.036638,((((Phenacobius_mirabilis:0.474847,((Notropis_stramineus:0.166999,(Cyprinella_spiloptera:0.170192,Cyprinella_lutrensis:0.121417):0.171803):0.062615,Campostoma_anomalum:0.464225):0.072765):0.236573,((Notemigonus_crysoleucas:0.231152,(Iberochondrostoma_lemmingii:0.205083,Alburnus_alburnus:0.234614):0.127563):0.107374,((Tribolodon_nakamurai:0.102514,Pseudaspius_leptocephalus:0.132138):0.108759,Phoxinus_perenurus:0.246041):0.185937):0.010545):0.005674,Pelecus_cultratus:0.212297):0.007694,Gila_robusta:0.242311):0.074646):0.025835,(((Puntius_ticto:0.716720,(Barbus_barbus:0.176620,((Barbodes_gonionotus:0.233724,((Cyprinus_carpio:0.084252,(Carassius_carassius:0.031669,Carassius_auratus:0.046981):0.237327):0.038179,Gymnocypris_przewalskii:0.558520):0.058554):0.029221,(Labeo_senegalensis:0.094790,Labeo_batesii:0.066164):0.144158):0.048922):0.043522):0.000000,Barbus_trimaculatus:1.016206):0.063862,(Esomus_metallicus:1.218351,Danio_rerio:2.453158):0.146347):0.277610):0.035132,(Tinca_tinca:0.195667,Gnathopogon_elongatus:0.312321):0.022963):0.036420,((Xenocypris_argentea:0.055249,(Ischikauia_steenackeri:0.068753,Chanodichthys_mongolicus:0.066789):0.077063):0.044435,Aphyocypris_chinensis:0.257852):0.095124):0.048051,(((Pungtungia_herzi:0.185592,Pseudorasbora_pumila:0.271794):0.095522,Coreoleuciscus_splendidus:0.245883):0.015455,(Zacco_sieboldii:0.138536,(Opsariichthys_uncirostris:0.158497,Opsariichthys_bidens:0.101893):0.239977):0.122212):0.043351):0.056233,Gobio_gobio:0.331265):0.087893,Hemibarbus_barbus:0.217855):0.064556,Hemibarbus_mylodon:0.080052):0.123429,Hemibarbus_longirostris:0.082698,Hemibarbus_labeo:0.026609);
//    End;
    public boolean outputTreesNexus (String filename) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            pw.println("#NEXUS");
            pw.println("Begin trees;");
            //Ugly, need better id of trees
            int count=0;
            for (Tree st:this.trees) {
                if (st.getName().equals("UnknownTree")) {
                    st.setName("UnknownTree"+(count++));
                }
                pw.println("\tTREE "+st.getName()+" = "+st.getTree());
            }
            pw.println("End;");
            pw.flush();
            pw.close();
        } catch (Exception e) {return false;}
        return true;
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
    
    /**
     * @return the note
     */
    public String getNote() {
        return note;
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
        for (Tree tree:trees) {
            tree.setMultiplesequences_id(multiplesequences_id);
        }
    }
    
    public boolean removeFromDatabase() {
        return df.removeMultipleTrees(this);
    }
    
    public boolean updateDatabase() {
        return this.updateDatabase();
    }
    
    public workflow_properties returnProperties() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public String getBiologicType() {
        return "MultipleTrees";
    }
    
    public String toHtml() {
        return toString();
    }
    
    public String getNameId(int id) {
        return df.getMultipleTreesName(id);
    }
    
    public String getFileNameId(int id) {
        return "";
    }
    
    public void setData(String data) {
        this.getTree().clear();
        Util u=new Util("temp.txt");
        u.println(data);
        u.close();
        this.readNewick("temp.txt");
        this.replaceSequenceIDwithNames();
        Util.deleteFile("temp.txt");
    }
    
    public String getFasta() {
        return "";
    }
    
    public String getPhylip() {
        return "";
    }
    
    public String getExtendedString() {
        return toString();
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
