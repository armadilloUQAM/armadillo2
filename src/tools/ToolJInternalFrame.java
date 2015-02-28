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

package tools;

import biologic.Alignment;
import biologic.Biologic;
import biologic.Genome;
import biologic.InfoAlignment;
import biologic.InfoMultipleSequences;
import biologic.seqclasses.InformationJDialog;
import biologic.MultipleSequences;
import biologic.MultipleTrees;
import biologic.RunWorkflow;
import biologic.Sequence;
import biologic.Tree;
import biologic.Workflows;
import biologic.seqclasses.CommentsSequenceJDialog;
import configuration.*;
import database.AbstractTreeModel;
import database.CorrespondencesMaps;
import database.ExplorerTableModel;
import database.ExplorerTreeCellRenderer;
import database.ExplorerTreeMutableTreeNode;
import database.databaseFunction;
import editor.RenameBiologicJDialog;
import editors.BiologicEditor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.apache.regexp.RE;
import workflows.Workbox;
import workflows.armadillo_workflow;
import workflows.armadillo_workflow.workflow_object;
import workflows.workflow_properties;
import workflows.workflow_properties_dictionnary;

/**
 * ToolBox
 * This is the toolbox displayed on screen
 * @author Alix Boc, Etienne Lord, Mickael Leclercq, Abdoulaye Baniré Diallo
 */
public class ToolJInternalFrame extends javax.swing.JInternalFrame implements ActionListener  {

   

    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES
    Config config=new Config();
    public static databaseFunction df=new databaseFunction();
    boolean debug = false;             //--debug flag...
    boolean expandToolTree=true;       //--Flag to expand or collapse the tool tree
    boolean expandDatabaseTree=true;
    LinkedList<ExplorerTreeMutableTreeNode>toload=new LinkedList<ExplorerTreeMutableTreeNode>();
    static String empty_string="                                                  "; //For node display
    private static armadillo_workflow current_workflow=null;
    Workbox workbox=new Workbox();

    ////////////////////////////////////////////////////////////////////////////
    /// Automatic saving SwingWorker
     SwingWorker<Integer, Integer> savingSwingWorker=null;
    

    ////////////////////////////////////////////////////////////////////////////
    /// Database Explorer
    
    CorrespondencesMaps cm = new CorrespondencesMaps();
    static ExplorerTreeMutableTreeNode node = null;
    TreeNode root;
    TreePath path;
    public static boolean treeSearch=false;
    public static boolean treeSearchComplete=false;
    String default_search_string=" Enter search query and press Enter";
    String default_search_label="Search current project ";
    SwingWorker<Integer, Integer> searchSwingWorker=null;
    SwingWorker<Integer, Integer> search_newSwingWorker=null;
    boolean search_flag=false;
 
    String[] tableToDisplay={"Sequence","MultipleSequences","Alignment","Ancestor","Tree","MultipleTrees","Matrix","Genome","Workflows","RunWorkflow","Text"}; //This is the table that you want to display
    String[] tableToSearch={"Sequence","MultipleSequences","Alignment","Ancestor","Tree","MultipleTrees","Matrix","Text","Workflows","RunWorkflow"}; //This is the table that you want to display
   
    ////////////////////////////////////////////////////////////////////////////
    //////// popup menu variables
    /// Applications tree
    ExplorerTreeMutableTreeNode selectedNode=null;
    public JPopupMenu popup;
    public JMenuItem item;
    public boolean overRoot = false;
    public Point loc;
    private InformationJDialog loading;
    private Frame frame;

    ////////////////////////////////////////////////////////////////////////
    /// MAIN CONSTRUCTOR

    public ToolJInternalFrame() {
        initComponents();
        frame=javax.swing.JOptionPane.getFrameForComponent(this);
        loading=new InformationJDialog(frame, false, null,"Loading...",false);       
        //--Initialise some GUI item with enhanced properties
        this.setTitle("Workflow Tools");
        this.setFrameIcon(Config.icon);         //Default armadillo icon 
        this.setLocation(10,10);                //Default location
        Message(default_search_label);
        RechercheJTextField.setText(default_search_string);
        RechercheJTextField.setForeground(Color.LIGHT_GRAY);
       //--Set the program found currently (We will subdivise later)
        loadProgram();
        createDatabaseTree(); //--Done in reload
    }

    /**
     * Common function of JInternalFrame responsable to resize the JInternalFrame to
     * a maximum size in relation to the JDesktopPane size
     */
     public void mazimizeSize() {
        Dimension d = this.getDesktopPane().getSize();
        this.setSize(this.getWidth(),d.height);
        this.setLocation(0,0);
     }
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Tools_jTabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Applications_jTree = new javax.swing.JTree();
        jLabel2 = new javax.swing.JLabel();
        expandCollapseTool_jButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        RechercheJTextField = new javax.swing.JTextField();
        refresh_jButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        Database_jTree = new javax.swing.JTree();
        expandCollapseTool_jButton1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        CurrentWorkflow_jTree = new javax.swing.JTree();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Armadillo");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Logical conditions");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Begin");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("End");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("For");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("If");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("While");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Phylogenic tools");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Alignment");
        javax.swing.tree.DefaultMutableTreeNode treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("ClustalW");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("ClustalW2");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("DiAlign");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("KAlign");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Mavid");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Muscle");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("TBA");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Tree");
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("DNAPars");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("DNAml");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Robinson&Fould");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("SeqBoot");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("RaxML");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Random Sequences and Tree");
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("SeqGen");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("RanTree");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("AncestorCC");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("HGT (Horizontal Gene Transfert)");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Input");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Armadillo Database");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("List All File in directory");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Load Sequences");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Load Sequence From");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Load Fasta");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Sequence>100bp");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Output");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Results");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Save to file");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Conversion");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Sequences to Alignments");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Comments");
        treeNode1.add(treeNode2);
        Applications_jTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        Applications_jTree.setCellRenderer(new ToolboxTreeCellRenderer());
        Applications_jTree.setDragEnabled(true);
        jScrollPane1.setViewportView(Applications_jTree);

        jLabel2.setForeground(java.awt.Color.blue);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Drag and Drop to the Workflow");

        expandCollapseTool_jButton.setText("Expand / Collapse toolbox");
        expandCollapseTool_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandCollapseTool_jButtonActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        RechercheJTextField.setText(" Type search string here");
        RechercheJTextField.setToolTipText("<html>Enter a search term or a short sequence<br>\nNote that you can enter a regular expression* as a query</html>");
        RechercheJTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RechercheJTextFieldActionPerformed(evt);
            }
        });
        RechercheJTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                RechercheJTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                RechercheJTextFieldFocusLost(evt);
            }
        });
        RechercheJTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RechercheJTextFieldKeyPressed(evt);
            }
        });

        refresh_jButton.setBackground(new java.awt.Color(255, 255, 255));
        refresh_jButton.setForeground(new java.awt.Color(255, 255, 255));
        refresh_jButton.setIcon(new javax.swing.ImageIcon(config.imagePath()+"//reload.png"));
        refresh_jButton.setToolTipText("Reload the database");
        refresh_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refresh_jButtonActionPerformed(evt);
            }
        });

        jScrollPane2.setPreferredSize(new java.awt.Dimension(80, 322));

        Database_jTree.setModel(new AbstractTreeModel(null));
        Database_jTree.setCellRenderer(new ExplorerTreeCellRenderer());
        Database_jTree.setDoubleBuffered(true);
        Database_jTree.setDragEnabled(true);
        Database_jTree.setMaximumSize(new java.awt.Dimension(300, 64));
        Database_jTree.addTreeWillExpandListener(new javax.swing.event.TreeWillExpandListener() {
            public void treeWillCollapse(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {
            }
            public void treeWillExpand(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {
                Database_jTreeTreeWillExpand(evt);
            }
        });
        Database_jTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Database_jTreeMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Database_jTreeMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                Database_jTreeMouseReleased(evt);
            }
        });
        Database_jTree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
            }
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                Database_jTreeTreeExpanded(evt);
            }
        });
        Database_jTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                Database_jTreeValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(Database_jTree);

        expandCollapseTool_jButton1.setText("Expand / Collapse datasets");
        expandCollapseTool_jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandCollapseTool_jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(RechercheJTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refresh_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(expandCollapseTool_jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(RechercheJTextField)
                    .addComponent(refresh_jButton, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(expandCollapseTool_jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .addGap(6, 6, 6))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(expandCollapseTool_jButton, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(expandCollapseTool_jButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        Tools_jTabbedPane.addTab("Tools and datasets", jPanel1);

        jScrollPane3.setPreferredSize(new java.awt.Dimension(80, 322));

        CurrentWorkflow_jTree.setModel(new AbstractTreeModel(null));
        CurrentWorkflow_jTree.setCellRenderer(new ExplorerTreeCellRenderer());
        CurrentWorkflow_jTree.setDoubleBuffered(true);
        CurrentWorkflow_jTree.setDragEnabled(true);
        CurrentWorkflow_jTree.setMaximumSize(new java.awt.Dimension(300, 64));
        CurrentWorkflow_jTree.addTreeWillExpandListener(new javax.swing.event.TreeWillExpandListener() {
            public void treeWillCollapse(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {
            }
            public void treeWillExpand(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {
                CurrentWorkflow_jTreeTreeWillExpand(evt);
            }
        });
        CurrentWorkflow_jTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                CurrentWorkflow_jTreeMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                CurrentWorkflow_jTreeMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CurrentWorkflow_jTreeMouseClicked(evt);
            }
        });
        CurrentWorkflow_jTree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
            }
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                CurrentWorkflow_jTreeTreeExpanded(evt);
            }
        });
        CurrentWorkflow_jTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                CurrentWorkflow_jTreeValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(CurrentWorkflow_jTree);

        Tools_jTabbedPane.addTab("Current Workflow", jScrollPane3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tools_jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tools_jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void expandCollapseTool_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandCollapseTool_jButtonActionPerformed
        this.expandToolTree(null);
}//GEN-LAST:event_expandCollapseTool_jButtonActionPerformed

    private void Database_jTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Database_jTreeMouseClicked
        this.selectedNode = (ExplorerTreeMutableTreeNode) getDatabaseTree().getLastSelectedPathComponent();
        if((evt.getClickCount()>1||config.getBoolean("MacOSX"))&&selectedNode!=null&&selectedNode.isLeaf()) {
            DetailsSW();
        } else {
            treePopup(evt);
        }
}//GEN-LAST:event_Database_jTreeMouseClicked

    private void Database_jTreeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Database_jTreeMousePressed

}//GEN-LAST:event_Database_jTreeMousePressed

    private void Database_jTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Database_jTreeMouseReleased
        treePopup(evt);
}//GEN-LAST:event_Database_jTreeMouseReleased

    private void Database_jTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_Database_jTreeValueChanged
        selectedNode = (ExplorerTreeMutableTreeNode) getDatabaseTree().getLastSelectedPathComponent();
        if (selectedNode!=null)
            if (selectedNode.isLeaf()&&!selectedNode.isTable()){               
                node = (ExplorerTreeMutableTreeNode) selectedNode.getParent();                
            } else {
                node = (ExplorerTreeMutableTreeNode)selectedNode;
            
            }
}//GEN-LAST:event_Database_jTreeValueChanged

    private void RechercheJTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_RechercheJTextFieldFocusGained
        if (RechercheJTextField.getText().equals(default_search_string)) {
            this.RechercheJTextField.setText("");
            this.RechercheJTextField.setForeground(Color.BLACK);
        } 
}//GEN-LAST:event_RechercheJTextFieldFocusGained

    private void RechercheJTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_RechercheJTextFieldFocusLost
        // Si on a pas de terme de recherche, on remet un texte d'information
        if (RechercheJTextField.getText().trim().equals("")) {
            RechercheJTextField.setText(default_search_string);
            RechercheJTextField.setForeground(Color.LIGHT_GRAY);
            createDatabaseTree();
        } else {
            //On ajoute un espace avant la recherche (juste pour faire beau)
            //this.RechercheJTextField.setForeground(Color.BLACK);
            String stri=RechercheJTextField.getText();
            //if (!stri.startsWith(" ")) RechercheJTextField.setText(" "+stri);
            //if (!stri.equals(default_search_string)) searchSW(stri);
           
        }
       
}//GEN-LAST:event_RechercheJTextFieldFocusLost

    private void RechercheJTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RechercheJTextFieldKeyPressed
        char c=evt.getKeyChar(); //Hack to catch V_ENTER key;
        if (c==KeyEvent.VK_ENTER) {
        String stri=RechercheJTextField.getText();   
            if (debug) System.out.println("Enter to search :"+stri);
            if (RechercheJTextField.getText().trim().equals("")) {              
                createDatabaseTree();
            } else {                
                if (!stri.equals(default_search_string)) {                                       
                    if (!search_flag) {                       
                        searchSW(stri);
                    } else {
                        // TO DO, cancel search and retry
                    }
                }
            }
        } //End ENTER CLICKED
}//GEN-LAST:event_RechercheJTextFieldKeyPressed

    private void Database_jTreeTreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_Database_jTreeTreeExpanded
     //--Not used
//       TreePath treepath=evt.getPath();
//       this.setTreeNodeContentSW((ExplorerTreeMutableTreeNode) treepath.getLastPathComponent());
       
    }//GEN-LAST:event_Database_jTreeTreeExpanded

    private void refresh_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refresh_jButtonActionPerformed
       this.reloadTree();
    }//GEN-LAST:event_refresh_jButtonActionPerformed

    private void RechercheJTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RechercheJTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RechercheJTextFieldActionPerformed

    private void Database_jTreeTreeWillExpand(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {//GEN-FIRST:event_Database_jTreeTreeWillExpand
        TreePath treepath=evt.getPath();
       this.setTreeNodeContentSW((ExplorerTreeMutableTreeNode) treepath.getLastPathComponent());
    }//GEN-LAST:event_Database_jTreeTreeWillExpand

    private void CurrentWorkflow_jTreeTreeWillExpand(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {//GEN-FIRST:event_CurrentWorkflow_jTreeTreeWillExpand
         TreePath treepath=evt.getPath();
       this.setCurrentWorkflowTreeNodeContentSW((ExplorerTreeMutableTreeNode) treepath.getLastPathComponent());
    }//GEN-LAST:event_CurrentWorkflow_jTreeTreeWillExpand

    private void CurrentWorkflow_jTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CurrentWorkflow_jTreeMouseClicked
        this.selectedNode = (ExplorerTreeMutableTreeNode) CurrentWorkflow_jTree.getLastSelectedPathComponent();
        if(evt.getClickCount()>1&&selectedNode!=null&&selectedNode.isLeaf()) {
            DetailsSW();
        } 
//        else {
//            treePopup(evt);
//        }
    }//GEN-LAST:event_CurrentWorkflow_jTreeMouseClicked

    private void CurrentWorkflow_jTreeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CurrentWorkflow_jTreeMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_CurrentWorkflow_jTreeMousePressed

    private void CurrentWorkflow_jTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CurrentWorkflow_jTreeMouseReleased
          treePopup2(evt);
    }//GEN-LAST:event_CurrentWorkflow_jTreeMouseReleased

    private void CurrentWorkflow_jTreeTreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_CurrentWorkflow_jTreeTreeExpanded
        // TODO add your handling code here:
    }//GEN-LAST:event_CurrentWorkflow_jTreeTreeExpanded

    private void CurrentWorkflow_jTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_CurrentWorkflow_jTreeValueChanged
       selectedNode = (ExplorerTreeMutableTreeNode) CurrentWorkflow_jTree.getLastSelectedPathComponent();
        if (selectedNode!=null)
            if (selectedNode.isLeaf()&&!selectedNode.isTable()){
                node = (ExplorerTreeMutableTreeNode) selectedNode.getParent();
            } else {
                node = (ExplorerTreeMutableTreeNode)selectedNode;
             }
    }//GEN-LAST:event_CurrentWorkflow_jTreeValueChanged

    private void expandCollapseTool_jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandCollapseTool_jButton1ActionPerformed
        expandDatabaseTree(null);
    }//GEN-LAST:event_expandCollapseTool_jButton1ActionPerformed

    /**
     *  This load the armadillo program list (/data/properties) into the tool jTree
     */
    public void loadProgram() {
        Vector<workflow_properties>program=new Vector<workflow_properties>();
        HashMap<String,ToolboxMutableTreeNode> ListCategoryNode=new HashMap<String,ToolboxMutableTreeNode>();
        for (String filename:workflow_properties.loadPropertieslisting(config.get("propertiesPath"))) {
            workflow_properties tmp=new workflow_properties();
            tmp.load(filename, config.get("propertiesPath"));
            program.add(tmp);
        }
        workflow_properties rootnode=new workflow_properties();
        rootnode.setName("Armadillo");
        ToolboxMutableTreeNode treeroot=new ToolboxMutableTreeNode(rootnode);
        for (workflow_properties lnode:program) {
           String category=lnode.get("Type");
           if (category.equals(lnode.NotSet)) {
               Config.log("Not Set: "+lnode+"\n"+lnode.filename);
           } 
           ToolboxMutableTreeNode newNode=new ToolboxMutableTreeNode(lnode);
           ToolboxMutableTreeNode rootNode=ListCategoryNode.get(category);           
           if (category.equals("For")) {
                //--Not needed for now..
               //category.equals("Begin")||
               treeroot.add(newNode);
           } else
               if (rootNode==null) {
                  workflow_properties newnode_properties=new workflow_properties();
                  newnode_properties.put("Type",category);
                  newnode_properties.setName(category);
                  ToolboxMutableTreeNode newRootNode=new ToolboxMutableTreeNode(newnode_properties);
                  newRootNode.add(newNode);
                  ListCategoryNode.put(category, newRootNode);
               } else {
                  rootNode.add(newNode);
               }
       }
       LinkedList<ToolboxMutableTreeNode> list=new LinkedList<ToolboxMutableTreeNode>();
       list.addAll(ListCategoryNode.values());
       Collections.sort(list);

       for (ToolboxMutableTreeNode lnode:list) treeroot.add(lnode);

       Applications_jTree.setModel(new javax.swing.tree.DefaultTreeModel(treeroot));
       //--Enable tooltip
       ToolTipManager.sharedInstance().registerComponent(Applications_jTree);
       ToolTipManager.sharedInstance().registerComponent(CurrentWorkflow_jTree);
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Tree

     ///////////////////////////////////////////////////////////////////////////
    /// TAB Database explorer


    /**
     * Create the tree model from the database table.
     * tree1 is the first parent, tree2 the first child, etc..
     */
    public void createDatabaseTree(){
        //--Set the tree and selection model
        ExplorerTreeMutableTreeNode tree1 = new ExplorerTreeMutableTreeNode(" Database Content","",0);
        
        //--Don't crete if database is Unchanged... Unsafe for now...
        //if (!df.isDatabaseChanged()) return;
        for (String s:this.tableToDisplay) {
            try {
            ExplorerTreeMutableTreeNode parent=new ExplorerTreeMutableTreeNode(s,s,0);
            tree1.add(parent);
            parent.setIsTable(true); //--Set as parent node            
            //--WE just set the number of item
            //--The loading will be done in setTreeNodeContentSW() upon Expanding
            //--This 
            this.setPartialTreeNodeContent((ExplorerTreeMutableTreeNode) tree1.getLastLeaf());
            } catch(Exception e){
                System.out.println("Unable to get "+s);}
        }
        
        if (config.isDevelopperMode()) {
 //-- Add Variable caterogies to Database table (just for easy drag and drop)
         //ExplorerTreeMutableTreeNode variable=new ExplorerTreeMutableTreeNode("Variable","Variable",0);         
         //variable.setTooltip("Variable/Filter for your output.");
         //tree1.add(variable);
        }
        AbstractTreeModel treeModel = new AbstractTreeModel(tree1);
        treeModel.setRoot(tree1);
        treeModel.reload();
        getDatabaseTree().setModel(treeModel);
        getDatabaseTree().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
   }

     /**
     * Create the tree model for the current workflow (if not null)
     * tree1 is the first parent, tree2 the first child, etc..
     */
    public void createCurrentWorkflowDatabaseTree(){
        //--Set the tree and selection model
        ExplorerTreeMutableTreeNode tree2 = new ExplorerTreeMutableTreeNode(" Workflow Content","",0);

        //--Don't create if database is Unchanged... Unsafe for now...
        //if (!df.isDatabaseChanged()) return;
        //--Get a list of all the objects in the workflow

        //--1. Start with programs and their outputs
        int count=0;
        if (current_workflow!=null) {
            for (workflow_object obj:current_workflow.workflow.work) {
                try {
                Vector<ExplorerTreeMutableTreeNode> list=getWorkflowObjectOuput(obj.getProperties());
                      count+=list.size();
                      for (ExplorerTreeMutableTreeNode newnode:list) tree2.add(newnode);
                      tree2.setCountResult(count);
                } catch(Exception e) {}
            }
        }
        //--2. Add OutputDatabase         
        if (current_workflow!=null) {
            for (workflow_object obj:current_workflow.workflow.work) {
                try {
                    if (obj instanceof armadillo_workflow.workflow_object_output_database) {
                        String output_type=obj.getProperties().get("outputType");
                        int output_id=obj.getProperties().getOutputID(output_type);
                        ExplorerTreeMutableTreeNode newnode=new ExplorerTreeMutableTreeNode("",output_type,output_id);
                        tree2.add(newnode);
                    }
//                    Vector<ExplorerTreeMutableTreeNode> list=getWorkflowObjectOuput(obj.getProperties());
//                      count+=list.size();
//                      for (ExplorerTreeMutableTreeNode newnode:list) tree2.add(newnode);
//                      tree2.setCountResult(count);
                } catch(Exception e) {}
            }
        }

        //-- Add Variable caterogies to Database table
//         ExplorerTreeMutableTreeNode variable=new ExplorerTreeMutableTreeNode("Variable","Variable",0);
//         variable.setTooltip("Variable in the current workflow.");
//         tree2.add(variable);
         //--List variables in the workflow here
          try {
                Vector<ExplorerTreeMutableTreeNode> list=this.getWorkflowVariables();
                      count+=list.size();
                      for (ExplorerTreeMutableTreeNode newnode:list) tree2.add(newnode);
                      tree2.setCountResult(count);
                } catch(Exception e) {}
         

        AbstractTreeModel treeModel = new AbstractTreeModel(tree2);
        treeModel.setRoot(tree2);
        treeModel.reload();
        try {
            this.CurrentWorkflow_jTree.setModel(treeModel);
            CurrentWorkflow_jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            this.setCurrentWorkflowTreeNodeContentSW(tree2);
        } catch(Exception e) {}
   }

    /**
     * Recreate the Workflows tree
     * tree1 is the first parent, tree2 the first child, etc..
     */
    public void createTreeToolbox(){
        //--Set the tree and selection model
        ExplorerTreeMutableTreeNode tree1=(ExplorerTreeMutableTreeNode) getDatabaseTree().getModel().getRoot();
        //--Iterate over tree content;
        for (int i=0; i<tree1.getChildCount();i++) {
           ExplorerTreeMutableTreeNode tree2=(ExplorerTreeMutableTreeNode) tree1.getChildAt(i);
           //--If node is Workflow : remove Child
           if (tree2.getName().equals("Workflows")) {
               tree2.removeAllChildren();
               this.setTreeNodeContentSW(tree2);
            }
        }              
         DefaultTreeModel treeModel = new  DefaultTreeModel(tree1);
        treeModel.setRoot(tree1);
        treeModel.reload();
        getDatabaseTree().setModel(treeModel);
        getDatabaseTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
   }
 
    /**
     * This create the tree content linked to this node
     * @param Starting node (parent)
     */
    public void setPartialTreeNodeContent(ExplorerTreeMutableTreeNode node){        
        //1. Load the correct object
        Object obj=node.getBiologic();
        ((Biologic)obj).hasNext();
        Vector<Integer>ids=(Vector<Integer>)((Biologic) obj).getAllId();
           String ObjectType=((Biologic)obj).getBiologicType();    
        //--Filter by the real id
        if (ObjectType.equals("Genome")) {
            for (int i=ids.size()-1;i>-1;i--) {
                Genome g=new Genome(ids.get(i));
                if (!(g.getUnknownType().equals("Genome")||g.getUnknownType().equals("Fastq")||g.getUnknownType().equals("SOLIDFile"))) {
                    ids.remove(i);
                }                 
            }            
        } 
        //2. iterate and load everything exept the name
     
        for (Integer id:ids) {
            node.add(new ExplorerTreeMutableTreeNode(empty_string,ObjectType,id));
        }
        node.setCountResult(ids.size());
        //Config.log(node.getBiologic().getAllId().size());
    }

    /////////////////////////////////////////////////////////////////////////
    ////// SEARCH FUNCTION

     private void searchSW_old(final String keyword) {

      searchSwingWorker=new SwingWorker<Integer, Integer>()  {
      
       @Override
        protected Integer doInBackground() throws Exception {
           
       
        //final String[] tableList={"RunPrograms","Sequence","MultipleSequences","Alignment","Tree","Ancestor","Workflows"}; //This is the table that you want to display
        int results=0;
        try {
            if (!keyword.isEmpty()&&!keyword.equals(default_search_string)) {
                ExplorerTreeMutableTreeNode tree1 = new ExplorerTreeMutableTreeNode(" Search results","",0);
                ExplorerTreeCellRenderer renderer = new ExplorerTreeCellRenderer();
                 DefaultTreeModel treeModel = new  DefaultTreeModel(tree1);
                tree1.removeAllChildren();
                int k=0;
                int l=0;
                int max=tableToDisplay.length;
                int i=0;
                boolean nextTable= false;
                //--Pattern to match
                Pattern  p;
                try {
                    p= Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
                }  catch(java.util.regex.PatternSyntaxException e) {return -1;}

                while(i<max&&!isCancelled()) {
                    int countResult=0;      //--Count Search result
                    setProgress(1);
                    //--Parent node (each database table)
                    ExplorerTreeMutableTreeNode parent=new ExplorerTreeMutableTreeNode(tableToDisplay[i],tableToDisplay[i],0);
                    parent.setIsTable(true); //--Met que ce parent est une table
                    nextTable = true;        //--Set that we need to create a new node if the table has results
                    String query = String.format("select * from %s", tableToDisplay[i]);
                    ResultSet rs = df.getDatabase().executeQuery(query);
                    while (rs.next()) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        
                        int id=rs.getInt(1);
                        for (int j = 1; j < rsmd.getColumnCount(); j++) {
                            String result = rs.getString(j);
                            if (result!=null){
                                Matcher m = p.matcher(result);
                                if (m.find()) {
                                    if (nextTable){
                                        tree1.add(parent);
                                        l++;
                                        if (l>1) k++;
                                    }
                                    countResult++; results++;
                                    ExplorerTreeMutableTreeNode tree2 = (ExplorerTreeMutableTreeNode) tree1.getChildAt(k);
                                    String value = cm.treeNode.get(tree2.getType());
                                    String selectField=(value==null?"":value);
                                    tree2.add(new ExplorerTreeMutableTreeNode(df.decode(rs.getString(selectField)),tree2.getType(), id));
                                    nextTable = false;
                                }
                            }
                        }
                    }
                    parent.setCountResult(countResult);
                    i++;
                }
                if (results==0) {
                    tree1 = new ExplorerTreeMutableTreeNode(" No results found.","",0);
                }
                //--Affiche l'arbre
                treeModel.setRoot(tree1);
                treeModel.reload();
                getDatabaseTree().setModel(treeModel);
                getDatabaseTree().setCellRenderer(renderer);
                treeSearch=true;
            } else reloadTree();
        } catch (Exception e) {
            e.printStackTrace();
            Config.log("Unable to get tables");
            reloadTree();
        }
        return 0;
        }

            @Override
            protected void process(List<Integer> chunks) {

            }

           @Override
           protected void done(){
               Message(default_search_label);
               search_flag=false;
           }

        }; //End SwingWorker declaration

        searchSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();                               
                                Message("Searching...");
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
         search_flag=true;   
        searchSwingWorker.execute();
    }

     private void searchSW(final String keyword) {
     try {
      search_newSwingWorker=new SwingWorker<Integer, Integer>()  {

       @Override
        protected Integer doInBackground() throws Exception {
       
        //final String[] tableList={"RunPrograms","Sequence","MultipleSequences","Alignment","Tree","Ancestor","Workflows"}; //This is the table that you want to display
        int results=0;
        try {
            if (!keyword.isEmpty()&&!keyword.equals(default_search_string)) {               
                System.out.println("Database searching for "+keyword+" "+Util.returnCurrentDateAndTime());
                Config.log("Database searching for "+keyword+" "+Util.returnCurrentDateAndTime());
                ExplorerTreeMutableTreeNode tree1 = new ExplorerTreeMutableTreeNode(" Search results","",0);
                ExplorerTreeCellRenderer renderer = new ExplorerTreeCellRenderer();
                DefaultTreeModel treeModel = new  DefaultTreeModel(tree1);
//                treeModel.setRoot(tree1);
//                treeModel.reload();
                int max=tableToSearch.length;
                int i=0;                

                //--Pattern to match
                RE regex=new RE(keyword, RE.MATCH_CASEINDEPENDENT);
                search_flag=true;
                tree1.setName("Searching ("+(i*100/max)+"%)"); 
                treeModel.setRoot(tree1);
                treeModel.reload();
                getDatabaseTree().setModel(treeModel);
                getDatabaseTree().setCellRenderer(renderer);
                getDatabaseTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                LinkedList<Integer>match_sequence_ids=new LinkedList<Integer>();
                while(i<max) {
                    int countResult=0;      //--Count Search result                    
                    ExplorerTreeMutableTreeNode parent=new ExplorerTreeMutableTreeNode(tableToSearch[i],tableToSearch[i],0);
                    parent.setIsTable(true); //--Add what we are seaching now...                   
                    tree1.add(parent);

                    //--Search Child node (iterator)
                    ExplorerTreeMutableTreeNode tree2 = new ExplorerTreeMutableTreeNode(tableToSearch[i],tableToSearch[i],0);                       
                    Iterator iter=tree2.getBiologic();
                            
                            if (debug) System.out.println("Searching "+tableToSearch[i]);
                            if (!tableToSearch[i].equals("MultipleSequences")&&!tableToSearch[i].equals("Alignment")&&!tableToSearch[i].equals("Ancestor")) {
                                int it=0;
                                while(iter.hasNext()) {
                                    Object obj=iter.next();
                                    //--Regex match (toString)                                   
                                        if (obj!=null)
                                            if (regex.match(((Biologic)obj).toString())) {
                                                //System.out.println(((Biologic)obj).getName());
                                                parent.add(new ExplorerTreeMutableTreeNode(((Biologic)obj).getName(),((Biologic)obj).getBiologicType(),((Biologic)obj).getId()));
                                                if (tableToSearch[i].equals("Sequence")) match_sequence_ids.add(((Biologic)obj).getId());
                                                countResult++;
                                                results++;

                                            } 
                                    //--Counter...    
                                        if (countResult%100==1) {
                                                tree1.setName("Searching ("+(countResult-1)+" results)");
                                                treeModel.reload();
                                        } else                                  
                                        if (it%2000==1) {
                                                tree1.setName("Still Searching ("+(it-1) +" processed elements)");
                                                treeModel.reload();
                                        } 
                                        it++;
                                        
                                }
                                // HACK FOR SPEED!
                            } else if (tableToSearch[i].equals("MultipleSequences")) {
                                //--Create an inverse look-up of MultipleSequences ID
                                HashMap<Integer,Integer>hash_ids=new HashMap<Integer,Integer>();                                
                                int j=0;
                                for (Integer id:match_sequence_ids) {
                                    Vector<Integer>ids=df.getMultipleSequencesIdContainingSequenceId(id);
                                    for (Integer multi_id:ids) {
                                        hash_ids.put(multi_id,0);
                                        j++;
                                        if (j%50==0) {
                                           tree1.setName("Searching ("+j+" possible results)"); 
                                            treeModel.reload();
                                        }
                                    }
                                    
                                }
                                //--Add the MultipleSequences to the results...
                                    for (Integer ids:hash_ids.keySet()) {
                                            parent.add(new ExplorerTreeMutableTreeNode("","MultipleSequences",ids));
                                             countResult++;
                                            results++;                                            
                                       }                                                                                                   
                            } else if (tableToSearch[i].equals("Alignment")) {
                                 //--Create an inverse look-up of MultipleSequences ID
                                HashMap<Integer,Integer>hash_ids=new HashMap<Integer,Integer>();                                
                                int j=0;
                                for (Integer id:match_sequence_ids) {
                                    Vector<Integer>ids=df.getAlignmentIdContainingSequenceId(id);
                                    for (Integer multi_id:ids) {
                                        hash_ids.put(multi_id,0);
                                         j++;
                                        if (j%50==0) {
                                            
                                            tree1.setName("Searching ("+j+" possible results)");
                                           
                                            treeModel.reload();
                                        }
                                    }
                                }
                                //--Add theAlignment to the results...
                                     for (Integer ids:hash_ids.keySet()) {
                                            parent.add(new ExplorerTreeMutableTreeNode("","Alignment",ids));
                                            countResult++;
                                            results++;
                                            if (countResult%100==0) tree1.setName("Still searching ("+(i*100/max)+"%)"); 
                                       }                                                                    
                            } else if (tableToSearch[i].equals("Ancestor")) {
                                 //--Create an inverse look-up of MultipleSequences ID
                                HashMap<Integer,Integer>hash_ids=new HashMap<Integer,Integer>();                                
                                int j=0;
                                for (Integer id:match_sequence_ids) {
                                    Vector<Integer>ids=df.getAncestorIdContainingSequenceId(id);
                                    for (Integer multi_id:ids) {
                                        hash_ids.put(multi_id,0);
                                         j++;
                                        if (j%50==0) {                                            
                                            tree1.setName("Searching ("+j+" possible results)");                                           
                                            treeModel.reload();
                                        }
                                    }
                                }
                                //--Add theAlignment to the results...
                                     for (Integer ids:hash_ids.keySet()) {
                                            parent.add(new ExplorerTreeMutableTreeNode("","Alignment",ids));
                                            countResult++;
                                            results++;
                                            if (countResult%100==1) tree1.setName("Still searching ("+(i*100/max)+"%)"); 
                                       }                                                                    
                            }
                            
                            
                       
                   
                    //setProgress(i*100/max);
                    i++;       
                    tree1.setName("Searching ("+(i*100/max)+"%)"); 
                    parent.setCountResult(countResult);
                    treeModel.reload();
                    getDatabaseTree().setModel(treeModel);
                    getDatabaseTree().setCellRenderer(renderer);
                    getDatabaseTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                    if (debug) System.out.println("Searching ("+(i*100/max)+"%)");
                }
                
                if (results==0) {
                    tree1 = new ExplorerTreeMutableTreeNode(" No results found.","",0);
                } else tree1.setName(" Search results.");
                //--Affiche l'arbre
                treeModel.setRoot(tree1);
                treeModel.reload();
                getDatabaseTree().setModel(treeModel);
                getDatabaseTree().setCellRenderer(renderer);
                getDatabaseTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                treeSearch=true;
            } else reloadTree();
        } catch (Exception e) {
            e.printStackTrace();
            reloadTree();
        }
        return 0;
        }

            @Override
            protected void process(List<Integer> chunks) {
                
//                    tree1.setName("Searching ("+(i*100/max)+"%)");
//                    System.out.println("Searching ("+(i*100/max)+"%)");
//                    treeModel.reload();
//                    getDatabaseTree().setModel(treeModel);
            }

           @Override
           protected void done(){
               Message(default_search_label);
               search_flag=false;              
           }

        }; //End SwingWorker declaration

        search_newSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                Message("Searching...");
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
         search_flag=true;
        search_newSwingWorker.execute();
     }catch (Exception e) {e.printStackTrace();}
    }

     public void setTreeNodeContentSW(final ExplorerTreeMutableTreeNode node_to_change){
      if (node_to_change.isLoaded()) return;
      searchSwingWorker=new SwingWorker<Integer, Integer>()  {

       @Override
        protected Integer doInBackground() throws Exception {
           setProgress(0);
//           toload.add(node);
//           while(!this.isCancelled()&&toload.size()>0) {
//                ExplorerTreeMutableTreeNode toloadnode=toload.getFirst();
               int i=0;
                int total=node_to_change.getChildCount();
               while(!this.isCancelled()&&i<total) {
                   try {
                     ExplorerTreeMutableTreeNode child=(ExplorerTreeMutableTreeNode) node_to_change.getChildAt(i++);
                      child.getBiologicName();
                      child.setLoaded(false);
                      //==Test : Handle group
                      String type=child.getType();
                      if (type.equals("MultipleSequences")) {
                          try {
                              InfoMultipleSequences info=new InfoMultipleSequences(child.getId());
                              int count=0;
                              for (Sequence s:info.getSequences()) {
                                  if (s.exists(s.getId())) {
                                      child.add(new ExplorerTreeMutableTreeNode(empty_string,"Sequence",s.getId()));
                                      count++;
                                  }
                              }
                              child.setCountResult(count);
                          } catch(Exception e) {Config.log(e.getMessage());}
                      }
                      if (type.equals("Alignment")) {
                          try {
                              InfoAlignment info=new InfoAlignment(child.getId());
                              Sequence s=new Sequence();
                              int count=0;
                              for (int id:info.getSequence_id()) {
                                  if (s.exists(id)) {
                                      child.add(new ExplorerTreeMutableTreeNode(empty_string,"Sequence",id));
                                      count++;
                                  }
                              }
                              child.setCountResult(count);
                          } catch(Exception e) {Config.log(e.getMessage());}
                      }
                       if (type.equals("MultipleTrees")) {
                          try {
                              MultipleTrees info=new MultipleTrees(child.getId());
                              int count=0;
                              for (Tree s:info.getTree()) {
                                  if (s.exists(s.getId())) {
                                      child.add(new ExplorerTreeMutableTreeNode(empty_string,"Tree",s.getId()));
                                      count++;
                                  }
                              }
                              child.setCountResult(count);
                          } catch(Exception e) {Config.log(e.getMessage());}
                      }
                      if (type.equals("Workflows")) {
                          try {
                              //armadillo_workflow work=new armadillo_workflow();
                              //Workflows info=new Workflows(work);
                              Workflows info=new Workflows(child.getId());
                              //child.setName(child.getName();
//                              info.loadFromDatabase(child.getId());
//                              if (info.StringToWorkflow()) {
//                                  int count=0;
//                                  //==Scanner for output_id
//                                  for (workflow_object obj:info.workflow.workflow.work) {
//                                      Vector<ExplorerTreeMutableTreeNode> list=getWorkflowObjectOuput(obj.getProperties());
//                                      count+=list.size();
//                                      for (ExplorerTreeMutableTreeNode newnode:list) child.add(newnode);
//                                  }
//                                  child.setCountResult(count);
//                              }
                          } catch(Exception e) {Config.log(e.getMessage());}
                      }                      
                      if (type.equals("RunWorkflow")) {
                          try {

                              RunWorkflow info=new RunWorkflow(child.getId());
                              //==Completed?
                              if (info.isCompleted()) {
                                  child.setName(child.getName()+" [finish]");
                              }
                              int count=0;
                              for (int ids:info.getExecution_workflow_id()) {
                                 child.add(new ExplorerTreeMutableTreeNode(empty_string,"Workflows",ids));
                                  count++;
                              }
                              child.setCountResult(count);

                          } catch(Exception e) {Config.log(e.getMessage());}
                      }
                      setProgress((i*100)/total);
                    } catch(Exception e) {Config.log("Exception in toolbox "+e.getMessage());}
                }
                node_to_change.setLoaded(true);
//                toload.poll();
//            }
            return 0;
            }

            @Override
            protected void process(List<Integer> chunks) {

            }

           @Override
           protected void done(){

                getDatabaseTree().updateUI();                         
                loading.setVisible(false);
                loading.setProgress(0);
                search_flag=false;
               Message("");
           }

        }; //End SwingWorker declaration

         searchSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
         search_flag=true;
         loading.setSwingWorker(searchSwingWorker);
         loading.setTitle("Loading...");
         loading.setProgress(0); //Put 0% as the start progress
         loading.Message("Loading "+node_to_change.getType()+"...", "");
         loading.setVisible(true);
         searchSwingWorker.execute();
    }

      public void setCurrentWorkflowTreeNodeContentSW(final ExplorerTreeMutableTreeNode node_to_change){
      if (node_to_change.isLoaded()) return;
      searchSwingWorker=new SwingWorker<Integer, Integer>()  {

       @Override
        protected Integer doInBackground() throws Exception {
           setProgress(0);
//           toload.add(node);
//           while(!this.isCancelled()&&toload.size()>0) {
//                ExplorerTreeMutableTreeNode toloadnode=toload.getFirst();
            //armadillo_workflow dummy=new armadillo_workflow();
               int i=0;
                int total=node_to_change.getChildCount();
               while(!this.isCancelled()&&i<total) {
                   try {
                     ExplorerTreeMutableTreeNode child=(ExplorerTreeMutableTreeNode) node_to_change.getChildAt(i++);
                     child.getBiologicName();
                      child.setLoaded(false);
                      //==Test : Handle group
                      String type=child.getType();
                      if (child.getName().isEmpty()) child.setName("Undefined");
                      if (type.equals("MultipleSequences")) {
                          try {
                              InfoMultipleSequences info=new InfoMultipleSequences(child.getId());
                              int count=0;
                              for (Sequence s:info.getSequences()) {
                                  if (s.exists(s.getId())) {
                                      child.add(new ExplorerTreeMutableTreeNode(empty_string,"Sequence",s.getId()));
                                      count++;
                                  }
                              }
                              child.setCountResult(count);
                          } catch(Exception e) {Config.log(e.getMessage());}
                      }
                      if (type.equals("Alignment")) {
                          try {
                              InfoAlignment info=new InfoAlignment(child.getId());
                              Sequence s=new Sequence();
                              int count=0;
                              for (int id:info.getSequence_id()) {
                                  if (s.exists(id)) {
                                      child.add(new ExplorerTreeMutableTreeNode(empty_string,"Sequence",id));
                                      count++;
                                  }
                              }
                              child.setCountResult(count);
                          } catch(Exception e) {Config.log(e.getMessage());}
                      }
                       if (type.equals("MultipleTrees")) {
                          try {
                              MultipleTrees info=new MultipleTrees(child.getId());
                              int count=0;
                              for (Tree s:info.getTree()) {
                                  if (s.exists(s.getId())) {
                                      child.add(new ExplorerTreeMutableTreeNode(empty_string,"Tree",s.getId()));
                                      count++;
                                  }
                              }
                              child.setCountResult(count);
                          } catch(Exception e) {Config.log(e.getMessage());}
                      }
                      if (type.equals("Workflows")) {
                          try {
                              System.out.println(child.getId());
                              //armadillo_workflow work=new armadillo_workflow();
                              Workflows info=new Workflows(child.getId());                            
                              System.out.println(info);
                              child.setName(info.getName());
//                              if (info.StringToWorkflow()) {
//                                  int count=0;
//                                  //==Scanner for output_id
//                                  for (workflow_object obj:info.workflow.workflow.work) {
//                                      Vector<ExplorerTreeMutableTreeNode> list=getWorkflowObjectOuput(obj.getProperties());
//                                      count+=list.size();
//                                      for (ExplorerTreeMutableTreeNode newnode:list) child.add(newnode);
//                                  }
//                                  child.setCountResult(count);
//                              }
                          } catch(Exception e) {Config.log(e.getMessage());}
                      }
                      if (type.equals("RunWorkflow")) {
                          try {

                              RunWorkflow info=new RunWorkflow(child.getId());
                              //==Completed?
                              if (info.isCompleted()) {
                                  child.setName(child.getName()+" [finish]");
                              }
                              int count=0;
                              for (int ids:info.getExecution_workflow_id()) {
                                 child.add(new ExplorerTreeMutableTreeNode(empty_string,"Workflows",ids));
                                  count++;
                              }
                              child.setCountResult(count);

                          } catch(Exception e) {Config.log(e.getMessage());}
                      }
                      setProgress((i*100)/total);
                    } catch(Exception e) {}
                }
                node_to_change.setLoaded(true);
//                toload.poll();
//            }
            return 0;
            }

            @Override
            protected void process(List<Integer> chunks) {

            }

           @Override
           protected void done(){
                CurrentWorkflow_jTree.updateUI();             
//                loading.setVisible(false);
//                loading.setProgress(0);
                search_flag=false;
               Message("");
           }

        }; //End SwingWorker declaration

         searchSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
//                                loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
         search_flag=true;
//         loading.setSwingWorker(searchSwingWorker);
//         loading.setTitle("Loading...");
//         loading.setProgress(0); //Put 0% as the start progress
//         loading.Message("Loading "+node_to_change.getType()+"...", "");
//         loading.setVisible(true);
         searchSwingWorker.execute();
    }

    public void search (String keyword){
        ArrayList <String> tableList = new ArrayList <String>();
        tableList = df.getTablesAtoZforTree();
        keyword=keyword.trim();
        int results=0;
        try {
            if (!keyword.isEmpty()&&!keyword.equals(default_search_string)) {
                ExplorerTreeMutableTreeNode tree1 = new ExplorerTreeMutableTreeNode(" Search results","",0);
                ExplorerTreeCellRenderer renderer = new ExplorerTreeCellRenderer();
                 DefaultTreeModel treeModel = new  DefaultTreeModel(tree1);
                tree1.removeAllChildren();
                int k=0;
                int l=0;
                boolean nextTable= false;

                for (int i = 0; i < tableList.size(); i++) {
                    int countResult=0;      //--Count Search result
                    //--Parent node (each database table)
                    ExplorerTreeMutableTreeNode parent=new ExplorerTreeMutableTreeNode(tableList.get(i),tableList.get(i),0);
                    parent.setIsTable(true); //--Met que ce parent est une table
                    nextTable = true;        //--Set that we need to create a new node if the table has results
                    String query = String.format("select * from %s", tableList.get(i));
                    ResultSet rs = df.getDatabase().executeQuery(query);
                    while (rs.next()) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        Pattern  p;
                        try {
                            p= Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
                        }  catch(java.util.regex.PatternSyntaxException e) {return;}
                        int id=rs.getInt(1);
                        for (int j = 1; j < rsmd.getColumnCount(); j++) {
                            String result = rs.getString(j);
                            if (result!=null){
                                Matcher m = p.matcher(result);
                                if (m.find()) {
                                    if (nextTable){
                                        tree1.add(parent);
                                        l++;
                                        if (l>1) k++;
                                    }
                                    countResult++; results++;
                                    ExplorerTreeMutableTreeNode tree2 = (ExplorerTreeMutableTreeNode) tree1.getChildAt(k);
                                    String value = cm.treeNode.get(tree2.getType());
                                    String selectField=(value==null?"":value);
                                    tree2.add(new ExplorerTreeMutableTreeNode(df.decode(rs.getString(selectField)),tree2.getType(), id));
                                    nextTable = false;
                                }
                            }
                        }
                    }
                    parent.setCountResult(countResult);

                }
                if (results==0) {
                    tree1 = new ExplorerTreeMutableTreeNode(" No results found.","",0);
                }
                //--Affiche l'arbre
                treeModel.setRoot(tree1);
                treeModel.reload();
                getDatabaseTree().setModel(treeModel);
                getDatabaseTree().setCellRenderer(renderer);
                treeSearch=true;
            } else reloadTree();
        } catch (Exception e) {
            e.printStackTrace();
            Config.log("Unable to get tables");
            reloadTree();
        }
    }

    public void reloadTree (){
        //->TO DO
        treeSearch=false;        
        DefaultTreeModel d=( DefaultTreeModel)getDatabaseTree().getModel();
        d.nodeStructureChanged(root);
        d.reload();
        getDatabaseTree().expandPath(path);
        getDatabaseTree().makeVisible(path);        
        createDatabaseTree();
    }

    /**
     * This will expand/collapse the tool tree
     * @param expand (set to null, it will do the opposite of the current tree state, set to true, it will expand the ToolTree, set to false, it will collapse
     */
    public void expandToolTree(Boolean expand) {
        if (expand!=null) expandToolTree=expand;
        for (int i=1;i<this.Applications_jTree.getRowCount();i++) {
            if (expandToolTree) {
                this.Applications_jTree.expandRow(i);
            } else this.Applications_jTree.collapseRow(i);
        }
        expandToolTree=!expandToolTree;
    }

    /**
     * This will expand/collapse the tool tree
     * @param expand (set to null, it will do the opposite of the current tree state, set to true, it will expand the ToolTree, set to false, it will collapse
     */
    public void expandDatabaseTree(Boolean expand) {
        if (expand!=null) expandDatabaseTree=expand;
        for (int i=1;i<this.Database_jTree.getRowCount();i++) {
            if (expandDatabaseTree) {
                this.Database_jTree.expandRow(i);
            } else this.Database_jTree.collapseRow(i);
        }
        expandDatabaseTree=!expandDatabaseTree;
    }


    ///////////////////////////////////////////////////////////////////////////
    /// TREE POPUP MENU (RIGHT CLIC)
    public void treePopup(MouseEvent e){
        popup = new JPopupMenu();
        JMenuItem display=new JMenuItem("Details");
        display.setFont(display.getFont().deriveFont(Font.BOLD));
        display.addActionListener(this);

        JMenuItem note=new JMenuItem("Notes");
        note.addActionListener(this);
        note.setIcon(config.getIcon("Comments"));
      
        JMenuItem rename=new JMenuItem("Rename");
        rename.addActionListener(this);

        JMenuItem open=new JMenuItem("Open Workflow");
        open.addActionListener(this);
        
         JMenuItem update=new JMenuItem("Update Workflow");
        update.addActionListener(this);
         
        if(e.isPopupTrigger()) {
            loc = e.getPoint();
            ExplorerTreeMutableTreeNode s=(ExplorerTreeMutableTreeNode)getDatabaseTree().getLastSelectedPathComponent();
            if (s!=null&&s.getType().equals("Workflows")&&s.getId()!=0) {
                popup.add(open);
                popup.add(note);
                popup.addSeparator();
                popup.add(rename);
                popup.add(update);
            }
            if (s!=null&&s.getId()!=0&&!s.getType().equals("Workflows")) {
                popup.add(display);
                popup.add(note);
                popup.addSeparator();
                popup.add(rename);               
            }
            if (s!=null) {
                JMenuItem remove=new JMenuItem("Remove "+s.getType());
                if (s.isTable()) remove=new JMenuItem("Remove All "+s.getType());
                if (s.getType().equals("MultipleSequences")||s.getType().equals("Alignment")) {
                    remove.setToolTipText("This will remove "+s.getType()+" from the database, but will not delete the associated sequences");
                } else remove.setToolTipText("This will permanently remove this "+s.getType()+" from the database");
                remove.addActionListener(this);
                popup.add(remove);
            }
//            overRoot = lpath.getLastPathComponent() == root;
            popup.show(getDatabaseTree(), 50, loc.y);
        }
    }
    
     ///////////////////////////////////////////////////////////////////////////
    /// TREE POPUP MENU 2 (RIGHT CLIC)
    public void treePopup2(MouseEvent e){
        popup = new JPopupMenu();
        JMenuItem display=new JMenuItem("Details");
        display.setFont(display.getFont().deriveFont(Font.BOLD));
        display.addActionListener(this);

        JMenuItem note=new JMenuItem("Notes");
        note.addActionListener(this);
        note.setIcon(config.getIcon("Comments"));
      
        JMenuItem rename=new JMenuItem("Rename");
        rename.addActionListener(this);

        JMenuItem open=new JMenuItem("Open Workflow");
        open.addActionListener(this);
        
        JMenuItem update=new JMenuItem("Update Workflow");
        update.addActionListener(this);
        
        if(e.isPopupTrigger()) {
            loc = e.getPoint();
            ExplorerTreeMutableTreeNode s=(ExplorerTreeMutableTreeNode)this.CurrentWorkflow_jTree.getLastSelectedPathComponent();
            if (s!=null&&s.getType().equals("Workflows")&&s.getId()!=0) {
                popup.add(open);
                popup.add(note);
                popup.addSeparator();
                popup.add(rename);
                popup.add(update);
            }
            if (s!=null&&s.getId()!=0&&!s.getType().equals("Workflows")) {
                popup.add(display);
                popup.add(note);
                popup.addSeparator();
                popup.add(rename);               
            }
            if (s!=null) {
                JMenuItem remove=new JMenuItem("Remove "+s.getType());
                if (s.isTable()) remove=new JMenuItem("Remove All "+s.getType());
                if (s.getType().equals("MultipleSequences")||s.getType().equals("Alignment")) {
                    remove.setToolTipText("This will remove "+s.getType()+" from the database, but will not delete the associated sequences");
                } else remove.setToolTipText("This will permanently remove this "+s.getType()+" from the database");
                remove.addActionListener(this);
                popup.add(remove);
            }
//            overRoot = lpath.getLastPathComponent() == root;
            popup.show(this.CurrentWorkflow_jTree, 50, loc.y);
        }
    }

//    private JMenuItem getMenuItem(String s) {
//        JMenuItem menuItem = new JMenuItem(s);
//        //menuItem.addActionListener((ActionListener) this);
//        return menuItem;
//    }


    public boolean Remove() {
        ExplorerTreeMutableTreeNode n=(ExplorerTreeMutableTreeNode)getDatabaseTree().getLastSelectedPathComponent();
        if (n==null) return false;
        Config.log("Removing "+n);

        //--Case 1: Remove all?
        if (n.getId()==0) {
            String msg="<html>Are you sure you want to delete <b>all "+n.getType()+"</b>?</html>";
                    Object[] options = {"Cancel","Delete All "+n.getType()};
                    int o = JOptionPane.showOptionDialog(this,msg,"Warning! Deleting All "+n.getType(),JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options, options[0]);
                    switch (o) {
                        //0. Cancel
                        case 0: return false;
                        //1. Delete All
                        case 1:
                            //--HACK FOR SPEED...                            
                            if (n.getType().equals("Genome")) {
                                String query="DELETE FROM Unknown WHERE UnknownType = 'Genome';";
                                df.getDatabase().execute(query);
                                query="DELETE FROM Unknown WHERE UnknownType = 'Fastq';";
                                df.getDatabase().execute(query);
                                query="DELETE FROM Unknown WHERE UnknownType = 'SOLIDFile';";
                                df.getDatabase().execute(query);
                            } else {
                                String query="DELETE FROM "+n.getType()+";";
                                df.getDatabase().execute(query);
                            }
                                                     
                      }
                      //--Update workflow
                     Workbox workbox=new Workbox();
                     workbox.getCurrentArmadilloWorkflow().workflow.updateDependance();
                     workbox.getCurrentArmadilloWorkflow().force_redraw=true;
                     workbox.getCurrentArmadilloWorkflow().redraw();
                     reloadTree();

            return true;
        } else {
        
        //-- Case 2: Remove only this one      
            //Config.log(n.getBiologic().toString());
            this.RemoveSW();
            
            return true;
        } 
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
     * Simple thread to load file into the project
     */
    public void RemoveSW() {

        SwingWorker<Integer, Object> loadSwingWorker2=new SwingWorker<Integer, Object>() {
            
            @Override
            protected Integer doInBackground() throws Exception {
                //We dont check for cancelled
                TreePath[] path=getDatabaseTree().getSelectionModel().getSelectionPaths();

                 int count=path.length;
                 int i=0;
                 while (!isCancelled()&&i<count) {
                     TreePath p=path[i];
                     ExplorerTreeMutableTreeNode n=(ExplorerTreeMutableTreeNode)p.getLastPathComponent();
                     n.getBiologic().removeFromDatabase();
                     setProgress(i*100/count);
                     i++;
                 }
                return 0;
            }

            @Override
            public void process(List<Object> chunk) {
                for (Object o:chunk) {
                    if (o instanceof String)  {
                        String s=(String)o;
                        if (s.startsWith("Unable")||s.startsWith("Error")) {
                            loading.MessageErreur(s, "");
                        } else {
                            loading.Message(s,"");
                        } //--End Unable
                     } //--End instance of String
                } //--End list of Object
            } //End process

            @Override
            public void done() {
                loading.setVisible(false);
                loading.setProgress(0);
                //Update UI by reloading alll sequences... (to be sure)...
                 Workbox workbox=new Workbox();
                 workbox.getCurrentArmadilloWorkflow().workflow.updateDependance();
                 workbox.getCurrentArmadilloWorkflow().force_redraw=true;
                 workbox.getCurrentArmadilloWorkflow().redraw();
                 reloadTree();
            }

        }; //End SwingWorker definition

        loadSwingWorker2.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                        if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
        //Finally. Show a load dialog :: Warning Work-In-Progress
        loading.setSwingWorker(loadSwingWorker2);
        loading.setTitle("Removing...");
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Removing element(s)","");
        loading.setVisible(true);
        loadSwingWorker2.execute();
    }

    public void Rename() {
        ExplorerTreeMutableTreeNode n=(ExplorerTreeMutableTreeNode)getDatabaseTree().getLastSelectedPathComponent();
        //--Valid node? If yes, try to rename
        if (n.getId()>0) {
            RenameBiologicJDialog d=new RenameBiologicJDialog(frame, n, "Rename "+n.getType());
            //--REloda partial tree
            ExplorerTreeMutableTreeNode parent=(ExplorerTreeMutableTreeNode) n.getParent();
            parent.setLoaded(false);
            Workbox workbox=new Workbox();
            workbox.getCurrentArmadilloWorkflow().workflow.updateDependance();
            workbox.getCurrentArmadilloWorkflow().force_redraw=true;
            workbox.getCurrentArmadilloWorkflow().redraw();
            this.setTreeNodeContentSW(parent);
        }
    }

    public void Comments() {

        ExplorerTreeMutableTreeNode n=(ExplorerTreeMutableTreeNode)getDatabaseTree().getLastSelectedPathComponent();
        //--Valid node? If yes, try to rename
        if (n.getId()>0) {
            if (n.getType().equals("Sequence")||n.getType().equals("MultipleSequences")||n.getType().equals("Alignment")) {
                CommentsSequenceJDialog s = new CommentsSequenceJDialog(frame, n, "");
            } else {
                AddCommentsBiologicJDialog d=new AddCommentsBiologicJDialog(frame, n, "Add Comments");
            }
            //--Reload partial tree
            ExplorerTreeMutableTreeNode parent=(ExplorerTreeMutableTreeNode) n.getParent();
            parent.setLoaded(false);
            this.setTreeNodeContentSW(parent);
            //getJTree().setSelectionPath(n.getPath());
        }
    }

    public void UpdateWorkflow() {

        ExplorerTreeMutableTreeNode n=(ExplorerTreeMutableTreeNode)getDatabaseTree().getLastSelectedPathComponent();
        //--Valid node? If yes, try to rename
        if (n.getId()>0) {
            if (n.getType().equals("Workflows")) {
                Workflows current=workbox.getCurrentWorkflows();              
                current.setNote(current.getNote()+"\nUpdated "+Util.returnCurrentDateAndTime()+"\n");
                current.setDate_modified(Util.returnCurrentDateAndTime());                
                current.setWorkflow_in_txt(current.workflowToString());
                current.updateDatabase();
            }
            //--Reload partial tree
            ExplorerTreeMutableTreeNode parent=(ExplorerTreeMutableTreeNode) n.getParent();
            parent.setLoaded(false);
            this.setTreeNodeContentSW(parent);
            //getJTree().setSelectionPath(n.getPath());
        }
    }
    
    public void DetailsSW(){
      //final ExplorerTreeMutableTreeNode n=(ExplorerTreeMutableTreeNode)getJTree().getLastSelectedPathComponent();
      final ExplorerTreeMutableTreeNode n=this.selectedNode;
        searchSwingWorker=new SwingWorker<Integer, Integer>()  {

       @Override
        protected Integer doInBackground() throws Exception {

            //==SPEED UP HACK FOR MULTIPLE SEQUENCES AND ALIGNMENT
             if (n.getType().equals("MultipleSequences")) {
                 MultipleSequences multi=new MultipleSequences();
                Vector<Integer> sequences_id=new Vector<Integer>();
                //--1. Get some information
                 try {
                    //TO DO: CREATE A ONE QUERY SYSTEM... CHANGE OF DATABASE SCHEMA...
                    String query = String.format("SELECT name, note, RunProgram_id FROM MultipleSequences WHERE MultipleSequences_id='%d';", n.getId());
                    ResultSet rs = df.executeQuery(query);
                    if (rs!=null&&rs.next()) {
                         multi.setName(df.decode(rs.getString(1)));
                         multi.setNote(df.decode(rs.getString(2)));
                         multi.setRunProgram_id(rs.getInt(3));
                    }
                   
                    String query2 = String.format("SELECT sequence_id FROM MultipleSequences WHERE MultipleSequences_id='%d';", n.getId());
                    rs = df.executeQuery(query2);
                    while (rs.next()) {
                          sequences_id.add(rs.getInt(1));
                         
                    }
                 } catch(Exception e) {}
                 //--2. Get the sequences
                 int total=sequences_id.size();
                 int count=0;
                  for (int sequence_id:sequences_id) {
                        if (df.existsSequence(sequence_id)) {
                            Sequence s=df.getSequence(sequence_id);
                            multi.add(s);
                        }
                        count++;
                        setProgress(count*100/total);
                   }
                //--3. Set the MultipleSequences_id and return
                  multi.setId(n.getId());
                  BiologicEditor bio=new BiologicEditor(frame);
                  bio.display(multi);
             } else if (n.getType().equals("Alignment")) {
                 Alignment multi=new Alignment();
                 Vector<Integer> sequences_id=new Vector<Integer>();
                //--1. Get some information
                 try {
                    //TO DO: CREATE A ONE QUERY SYSTEM... CHANGE OF DATABASE SCHEMA...
                    String query = String.format("SELECT name, note, RunProgram_id FROM Alignment WHERE Alignment_id='%d';", n.getId());
                    ResultSet rs = df.executeQuery(query);
                    if (rs!=null&&rs.next()) {
                         multi.setName(df.decode(rs.getString(1)));
                         multi.setNote(df.decode(rs.getString(2)));
                         multi.setRunProgram_id(rs.getInt(3));
                    }
                   
                    String query2 = String.format("SELECT sequence_id FROM Alignment WHERE Alignment_id='%d';", n.getId());
                    rs = df.executeQuery(query2);
                    while (rs.next()) {
                          sequences_id.add(rs.getInt(1));
                         
                    }
                 } catch(Exception e) {}
                 //--2. Get the sequences
                 int total=sequences_id.size();
                 int count=0;
                 for (int sequence_id:sequences_id) {
                        if (df.existsSequence(sequence_id)) {
                            Sequence s=df.getSequence(sequence_id);
                            multi.add(s);
                        }
                        count++;
                        setProgress(count*100/total);
                   }
                //--3. Set the Alignment_id and return
                  multi.setId(n.getId());
                  BiologicEditor bio=new BiologicEditor(frame);
                  bio.display(multi);
             }

             else {
                setProgress(50);
                BiologicEditor bio=new BiologicEditor(frame);
                bio.display(n.getBiologic());
             }
            return 0;
            }

            @Override
            protected void process(List<Integer> chunks) {

            }

           @Override
           protected void done(){
               loading.setProgress(100);
               loading.setVisible(false);
               loading.setProgress(0);
               Message("");
           }

        }; //End SwingWorker declaration

        searchSwingWorker.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                     if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
         search_flag=true;
         loading.setSwingWorker(searchSwingWorker);
         loading.setTitle("Loading");
         loading.setProgress(0); //Put 0% as the start progress
         if (node!=null) loading.Message("Loading "+node.getType()+"...", "");
         loading.setVisible(true);
         searchSwingWorker.execute();
    }

    /**
     * Debug version to load multiple workflof
     */
    public void OpenWorkflow() {
        final ExplorerTreeMutableTreeNode n=(ExplorerTreeMutableTreeNode)getDatabaseTree().getLastSelectedPathComponent();
        Workbox work=new Workbox();
        if (n.getId()>0) {
            work.loadWorkflowFromDatabase(n.getId());
        }
    }

    /**
     * This extract the output object from an workflow_object properties
     * @param prop
     * @return
     */
    public Vector<ExplorerTreeMutableTreeNode> getWorkflowObjectOuput(workflow_properties prop) {
      Vector<ExplorerTreeMutableTreeNode>nodelist=new Vector<ExplorerTreeMutableTreeNode>();
      //==1. Program? No. We return empty list
      if (!prop.get("ObjectType").equals("Program")) return nodelist;
      //--Uniform search pattern
      Pattern search=Pattern.compile("output_(.*)_id.*", Pattern.CASE_INSENSITIVE);
      //--Search
      Object[] o=prop.keySet().toArray();
      ExplorerTreeMutableTreeNode program=new ExplorerTreeMutableTreeNode(prop.getName(),"",0);        
      program.setLoaded(true);
      int count=0;
      for (int i=0; i<o.length; i++) {
          Object k=o[i];
          String key=(String)k;
          Matcher m=search.matcher(key);
          if (m.find()&&prop.getInt(key)!=0) {
              String type=workflow_properties_dictionnary.lowercaseInputOutputType.get(m.group(1));              
              ExplorerTreeMutableTreeNode node_to_add=new ExplorerTreeMutableTreeNode("",type,prop.getInt(key));
              program.add(node_to_add);
              count++;
            }
      }
      program.setCountResult(count);
      nodelist.add(program);
      return nodelist;
    }

     /**
     * This extract the variable from the current workflow
     * @param prop
     * @return
     */
    private Vector<ExplorerTreeMutableTreeNode> getWorkflowVariables() {
      Vector<ExplorerTreeMutableTreeNode>nodelist=new Vector<ExplorerTreeMutableTreeNode>();
    
      //--Uniform search pattern
      Pattern search=Pattern.compile("output_(.*)_id.*", Pattern.CASE_INSENSITIVE);
      for (workflow_object obj: current_workflow.workflow.work) {
          if (obj instanceof armadillo_workflow.workflow_object_variable) {
            ExplorerTreeMutableTreeNode new_variable=new ExplorerTreeMutableTreeNode(obj.getProperties().getName(),"Variable",0);
            nodelist.add(new_variable);
          }
      }
     
      return nodelist;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    /// Action performed
    /**
     * Main Popup Action Performed
     * @param e
     */

    @Override
    public void actionPerformed(ActionEvent e) {
       
        String ac=e.getActionCommand();
        //--debug Config.log(ac);
       if(ac.equals("Details")){
            DetailsSW();
       }
        if(ac.startsWith("Remove")){
               Remove();
        }

        if(ac.equals("Rename")){
                Rename();
        }

        if(ac.equals("Notes")){
               Comments();
        }

        if(ac.equals("Open Workflow")){
                OpenWorkflow();
        }
        
         if(ac.equals("Update Workflow")){
                UpdateWorkflow();
        }

    }


    protected javax.swing.JTree getDatabaseTree() {
        return Database_jTree;
    }
   
     protected javax.swing.JTree getApplicationsTree() {
        return Applications_jTree;
    }
    
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
    public void popupMenuCanceled(PopupMenuEvent e) {}
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

     /**
     * @return the current_workflow
     */
    public static armadillo_workflow getCurrent_workflow() {
        return current_workflow;
    }

    /**
     * @param aCurrent_workflow the current_workflow to set
     */
    public static void setCurrent_workflow(armadillo_workflow aCurrent_workflow) {
        current_workflow = aCurrent_workflow;
    }
    
     ///////////////////////////////////////////////////////////////////////////
    /// MESSAGE FONCTION

   
    void Message(String text) {
//        this.SearchLabel.setForeground(new java.awt.Color(0, 51, 153));
//        this.SearchLabel.setBackground(Color.WHITE);
//        this.SearchLabel.setText(text);
    }

    void MessageError(String text) {
//        this.SearchLabel.setForeground(Color.RED);
//        this.SearchLabel.setBackground(Color.WHITE);
//        this.SearchLabel.setText(text);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree Applications_jTree;
    private javax.swing.JTree CurrentWorkflow_jTree;
    private javax.swing.JTree Database_jTree;
    private javax.swing.JTextField RechercheJTextField;
    private javax.swing.JTabbedPane Tools_jTabbedPane;
    private javax.swing.JButton expandCollapseTool_jButton;
    private javax.swing.JButton expandCollapseTool_jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton refresh_jButton;
    // End of variables declaration//GEN-END:variables

}
