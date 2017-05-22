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

import biologic.Alignment;
import biologic.Matrix;
import biologic.MultipleSequences;
import biologic.MultipleTrees;
import biologic.Sequence;
import biologic.Unknown;
import biologic.seqclasses.LoadSequenceFrame;
import biologic.seqclasses.LoadTreeFrame;
import configuration.Config;
import configuration.ImageFilter;
import configuration.Util;
import configuration.WorkflowFilter;
import database.*;
import editor.propertiesEditorBox;
import java.awt.Desktop;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import splash.AboutJDialog;
import biologic.seqclasses.InformationJDialog;
import biologic.seqclasses.LoadAlignmentFrame;
import biologic.seqclasses.StandardInputSequenceJDialog;
import configuration.SequenceFilter;
import configuration.TreeFilter;
import configuration.WorkflowImportExportFilter;
import editor.WorkflowExplorerJFrame;
import editors.HelpEditor;
import iubio.readseq.BioseqFormats;
import iubio.readseq.BioseqWriterIface;
import iubio.readseq.Readseq;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import javax.swing.SwingWorker;
import java.util.List;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URI;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.tree.DefaultTreeModel;
import program.StartIterationJDialog;
import results.report;
import results.report_list_workflows;
import workflows.*;
import tools.*;
import tutorial.Ancestrale_Sequence_JIternalFrame;

/**
 * MainFrame : Main GUI Frame of the program
 * @author Alix Boc, Etienne Lord, Mickael Leclercq, Abdoulaye Baniré Diallo, Vladimir Makarenkov
 * @since July 2009
 */
public class MainFrame extends javax.swing.JFrame implements WindowListener,
        WindowStateListener {
    
    Config config;                  // Note: Contain some icon, etc.
    Toolbox toolbox;                //Note: Main toolbox with database
    Databasebox2 databasebox;       //Note: Database editor
    Databasebox2 databasebox2;      //Note: Database query box
//       FilesJInternalFrame filebox;
    editor.MainFrame editor;        // Note: object editor
    Workbox workbox;                // Note: currently only one workflow object per Project
    Project project;                // Note: Current loaded project
    propertiesEditorBox editorbox;  // Note: Editor for the workflow object (available in developper mode)
    LoadSequenceFrame loadSequence; // Note: LoadSequence Popup
    LoadTreeFrame loadTree;         // Note: LoadTree Popup
    LoadAlignmentFrame loadAlignment; //Note:LoadAlingment Popup
    ChooseWorkflowJDialog chooseWorkflowJDialog;
    WorkflowExplorerJFrame workflowexplorer;

    //--Tutorial
    Ancestrale_Sequence_JIternalFrame tutorial_ancestrale_sequence;
    
    
    public static InformationJDialog loading; //--Loading JDialog when generating report
    //resultsSaveFrame results = new resultsSaveFrame();
    boolean defaultWorkflow=true; //Tag to know is we can directly save the workflow...
    JFrame frame;
    WelcomeMessage_JInternalFrame wmessage; //Welcome message
    
    /** Creates new form MainFrame */
    public MainFrame() {
        
        frame=this;
        initComponents();
        
        
        //0. Entry message
        Config.log("\nArmadillo Workflow Platform (c) 2009-"+Util.returnCurrentYear());
        Config.log("Starting "+Util.returnCurrentDateAndTime());
        Config.log("Initializing...");
        System.out.println("Armadillo Workflow Platform (c) 2009-"+Util.returnCurrentYear());
        System.out.println("Initializing...");
        //--Initialise some icons, path...
        config=new Config();
        
        //-- Setting some icons
        Config.log("Loading icons.");
        this.GenerateReportjButton.setIcon(config.getIcon("Report"));
        this.ReportjMenuItem14.setIcon(config.getIcon("Report"));
        this.ImportAlignment_jMenuItem18.setIcon(config.getIcon("Alignment"));
        this.AlignmentManagerjMenuItem.setIcon(config.getIcon("Alignment"));
        this.AlignmentManager_jButton.setIcon(config.getIcon("Alignment"));
        
        this.ImportSequences_jMenuItem10.setIcon(config.getIcon("Sequence"));
        this.SequenceManagerjMenuItem.setIcon(config.getIcon("Sequence"));
        this.SequenceManager_jButton.setIcon(config.getIcon("Sequence"));
        
        this.ImportTrees_jMenuItem9.setIcon(config.getIcon("Tree"));
        this.TreeManagerjMenuItem.setIcon(config.getIcon("Tree"));
        this.TreeManager_jButton.setIcon(config.getIcon("Tree"));
        
        this.ImportMatrix_jMenuItem7.setIcon(config.getIcon("Matrix"));
        
        this.NewWorkflow_jMenuItem11.setIcon(config.getIcon("Workflows"));
        this.NewProject_jMenuItem8.setIcon(config.getIcon("Project"));
        this.ImportTextResultsjMenuItem.setIcon(config.getIcon("Text"));
        this.Run_jMenuItem.setIcon(config.getIcon("play"));
        this.RunSelected_jMenuItem7.setIcon(config.getIcon("play"));
        this.Stop_jMenuItem.setIcon(config.getIcon("stopred"));
        this.chooseWorkflowJDialog=new ChooseWorkflowJDialog(this.frame);
        
        //1. Database and Project creation and Armadillo_workflow
        //-- Note: The method create the database connection
        Config.log("Getting last project path.");
        String projectpath=config.getLastProject();
        if (Util.FileExists(projectpath)) {
            Config.log("Loading project "+projectpath);
            NewProject(projectpath);
        } else {
            Config.log("Creating new project.");
            projectpath=NewProject("");
        }
        Config.log("Loading workbox.");
        //2. Workbox
        //--Note: Contains the armadillo_workflow...
        workbox=new Workbox(this, projectpath,project);
        //3. Toolbox
        Config.log("Loading toolbox.");
        toolbox=new Toolbox();
        //4. Misc.
        Config.log("Loading databasebox.");
        databasebox=new Databasebox2(); //--Test my table Query
        //databasebox2=new Databasebox2();
        Config.log("Loading Editors.");
        Config.log("Creating propertiesEditorBox.");
        editorbox=new  propertiesEditorBox(this);
        Config.log("Creating Main Armadillo Workflow.");
        editor=new editor.MainFrame();
        this.jDesktopPane.setDesktopManager(new armadilloDesktopManager());
        workflowexplorer=new WorkflowExplorerJFrame(frame);
        this.jDesktopPane.add(toolbox.getJInternalFrame());
//        this.jDesktopPane.add(filebox);
//        this.jDesktopPane.add(databasebox.getJFrame());
//        this.jDesktopPane.add(databasebox2.getJFrame());
        this.jDesktopPane.add(workbox.getJInternalFrame());
        //--Note: not needed since it's done in project...
        //         Config.log("Connection to database.");
        //        databaseFunction df=new databaseFunction();
        Config.log("Loading database.");
        toolbox.reloadDatabaseTree();
        
        ///////////////////////////////////////////////////////////////////////
        //--Workflow found?
        if (!config.getBoolean("ForceOpen")) {
            int workflow_id=project.df.getNextWorkflowsID()-1;
            if (workflow_id>0) {
                Config.log("Loading last workbox with ID ["+workflow_id+"].");
                workbox.loadWorkflowFromDatabase(workflow_id);
            }
        } else {
            Config.log("Warning. Force Open of Armadillo found. Will skip loading workflow...");
        }
        
        
        ///////////////////////////////////////////////////////////////////////
        //--Some menus...
        Config.log("Loading sequence manager.");
        loadSequence= new LoadSequenceFrame(LoadSequenceFrame.MODE_NORMAL);
        Config.log("Loading tree manager.");
        loadTree=new LoadTreeFrame();
        Config.log("Loading alignment manager.");
        loadAlignment=new LoadAlignmentFrame(LoadAlignmentFrame.MODE_NORMAL);
        this.DatabaseExplorer_jMenuItem.setEnabled(true);
        ////////////////////////////////////////////////////////////////////////
        /// Developper mode
        
        if (config.isDevelopperMode()) {
            //--DeveloperMode?
            this.ObjectEditor_jMenuItem.setEnabled(true);
            this.Generate_applications_report_jMenuItem.setEnabled(true);
            this.ResetDevelopperStatejMenuItem5.setEnabled(true);
            this.Generate_applications_run_report_jMenuItem.setEnabled(true);
        }
        
        ////////////////////////////////////////////////////////////////////////
        /// Enable some informations frames...
        
        Config.log("done loading...");
        System.out.println("done loading...");
        //--Display Welcome Message + Information
        if (config.isSet("FirstTime")) {
            //this.jDesktopPane.add(new FirstTime_JIternalFrame(this));
            //--Removed November 2011
            config.remove("FirstTime");
        }
        Config.log("Setting icon images.");
        this.setIconImage(Config.image);
        this.setTitle(config.get("applicationName")+" "+config.get("version")+" - "+projectpath);
        
        ////////////////////////////////////////////////////////////////////////
        /// Tutorial (2011) - TO DO
        
        tutorial_ancestrale_sequence=new Ancestrale_Sequence_JIternalFrame(this);
        this.jDesktopPane.add(tutorial_ancestrale_sequence);
        
        ////////////////////////////////////////////////////////////////////////
        /// Sample
        Vector<JMenuItem>sample_menu=new Vector<JMenuItem>();
        //--Create menu item for each sample
        for (String filename:Config.listDir("examples")) {
            if (filename.endsWith("db")) {
                File f=new File(filename);
                if (config.getBoolean("MacOSX")&&f.getName().startsWith("LocalBlast.db")) {
                    //--TO DO... Special exclustion here...
                } else {
                    JMenuItem sample=new JMenuItem(f.getName());
                    sample.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            SampleMenuActionPerformed(evt);
                        }
                    });
                    sample_menu.add(sample);
                    this.Sample_jMenu.add(sample);
                }
            }
        }
        
        ////////////////////////////////////////////////////////////////////////
        /// Add some Windows listener
        addWindowListener(this);
        addWindowStateListener(this);
        //ava.awt.Frame parent, boolean modal, String title, String page
        wmessage=new WelcomeMessage_JInternalFrame(this, true, "text", "");
        this.jDesktopPane.add(wmessage);
        //--We display a Start Page?
        if (!config.getBoolean("DisplayStartPage"))  showWorkflow_and_Toolbox();
        
        ////////////////////////////////////////////////////////////////////////
        /// Cluster (cloud) options
        Config.log("Setting Cluster options if present.");
        System.out.println("Setting Cluster options if present.");
        workbox.getWorkFlowJInternalFrame().loadFromSavedCluster();
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jDesktopPane = new javax.swing.JDesktopPane();
        jToolBar1 = new javax.swing.JToolBar();
        Toolbox_jButton = new javax.swing.JButton();
        Workflow_jButton = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        SequenceManager_jButton = new javax.swing.JButton();
        TreeManager_jButton = new javax.swing.JButton();
        AlignmentManager_jButton = new javax.swing.JButton();
        GenerateReportjButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        ImportAlignmentjMenu1 = new javax.swing.JMenu();
        NewProject_jMenuItem8 = new javax.swing.JMenuItem();
        OpenWorkflow_jMenuItem = new javax.swing.JMenuItem();
        SaveAs_jMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        NewWorkflow_jMenuItem11 = new javax.swing.JMenuItem();
        Save_jMenuItem = new javax.swing.JMenuItem();
        SaveImagejMenuItem = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        ImportWorkflow_jMenuItem19 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        ImportSequences_jMenuItem10 = new javax.swing.JMenuItem();
        ImportAlignment_jMenuItem18 = new javax.swing.JMenuItem();
        ImportTrees_jMenuItem9 = new javax.swing.JMenuItem();
        ImportMatrix_jMenuItem7 = new javax.swing.JMenuItem();
        ImportTextResultsjMenuItem = new javax.swing.JMenuItem();
        ReportjMenuItem14 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem12 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        SequenceManagerjMenuItem = new javax.swing.JMenuItem();
        TreeManagerjMenuItem = new javax.swing.JMenuItem();
        AlignmentManagerjMenuItem = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        Run_jMenuItem = new javax.swing.JMenuItem();
        RunOnCHP_jMenuItem = new javax.swing.JMenuItem();
        RunSelected_jMenuItem7 = new javax.swing.JMenuItem();
        RunIterationjMenuItem = new javax.swing.JMenuItem();
        Stop_jMenuItem = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItem5 = new javax.swing.JMenuItem();
        ObjectEditor_jMenuItem = new javax.swing.JMenuItem();
        ResetDevelopperStatejMenuItem5 = new javax.swing.JMenuItem();
        Generate_applications_report_jMenuItem = new javax.swing.JMenuItem();
        Generate_applications_run_report_jMenuItem = new javax.swing.JMenuItem();
        DatabaseExplorer_jMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        Sample_jMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jPanel1.setName("jPanel1"); // NOI18N

        jDesktopPane.setBackground(new java.awt.Color(204, 204, 255));
        jDesktopPane.setName("jDesktopPane"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1012, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
        );

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        Toolbox_jButton.setText("Toolbox");
        Toolbox_jButton.setToolTipText("Show the toolbox.");
        Toolbox_jButton.setFocusable(false);
        Toolbox_jButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Toolbox_jButton.setName("Toolbox_jButton"); // NOI18N
        Toolbox_jButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Toolbox_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Toolbox_jButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(Toolbox_jButton);

        Workflow_jButton.setText("Workflow");
        Workflow_jButton.setToolTipText("Show the workflow creation area.");
        Workflow_jButton.setFocusable(false);
        Workflow_jButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Workflow_jButton.setName("Workflow_jButton"); // NOI18N
        Workflow_jButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Workflow_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Workflow_jButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(Workflow_jButton);

        jButton3.setText("Output");
        jButton3.setToolTipText("Show the software's outputs.");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setName("jButton3"); // NOI18N
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jToolBar1.add(jSeparator6);

        SequenceManager_jButton.setText("Sequences");
        SequenceManager_jButton.setToolTipText("Open the sequence manager");
        SequenceManager_jButton.setName("SequenceManager_jButton"); // NOI18N
        SequenceManager_jButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        SequenceManager_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SequenceManager_jButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(SequenceManager_jButton);

        TreeManager_jButton.setText("Trees");
        TreeManager_jButton.setToolTipText("Open the tree manager");
        TreeManager_jButton.setName("TreeManager_jButton"); // NOI18N
        TreeManager_jButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        TreeManager_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TreeManager_jButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(TreeManager_jButton);

        AlignmentManager_jButton.setText("Alignments");
        AlignmentManager_jButton.setToolTipText("Open the alignment manager");
        AlignmentManager_jButton.setName("AlignmentManager_jButton"); // NOI18N
        AlignmentManager_jButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        AlignmentManager_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AlignmentManager_jButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(AlignmentManager_jButton);

        GenerateReportjButton.setText("Generate Report");
        GenerateReportjButton.setToolTipText("<html>\nThis will generate a report for the current workflow.<br>\nNote: Only the current visible objects will be in the report!<br>\n</html>");
        GenerateReportjButton.setName("GenerateReportjButton"); // NOI18N
        GenerateReportjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GenerateReportjButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(GenerateReportjButton);

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        ImportAlignmentjMenu1.setText("File");
        ImportAlignmentjMenu1.setName("ImportAlignmentjMenu1"); // NOI18N
        ImportAlignmentjMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportAlignmentjMenu1ActionPerformed(evt);
            }
        });

        NewProject_jMenuItem8.setText("New Project");
        NewProject_jMenuItem8.setName("NewProject_jMenuItem8"); // NOI18N
        NewProject_jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewProject_jMenuItem8ActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(NewProject_jMenuItem8);

        OpenWorkflow_jMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        OpenWorkflow_jMenuItem.setText("Open Project");
        OpenWorkflow_jMenuItem.setName("OpenWorkflow_jMenuItem"); // NOI18N
        OpenWorkflow_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenWorkflow_jMenuItemActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(OpenWorkflow_jMenuItem);

        SaveAs_jMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        SaveAs_jMenuItem.setText("Save Project as...");
        SaveAs_jMenuItem.setName("SaveAs_jMenuItem"); // NOI18N
        SaveAs_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveAs_jMenuItemActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(SaveAs_jMenuItem);

        jSeparator5.setName("jSeparator5"); // NOI18N
        ImportAlignmentjMenu1.add(jSeparator5);

        NewWorkflow_jMenuItem11.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        NewWorkflow_jMenuItem11.setText("New Workflow in Project");
        NewWorkflow_jMenuItem11.setName("NewWorkflow_jMenuItem11"); // NOI18N
        NewWorkflow_jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewWorkflow_jMenuItem11ActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(NewWorkflow_jMenuItem11);

        Save_jMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        Save_jMenuItem.setText("Save current Workflow");
        Save_jMenuItem.setName("Save_jMenuItem"); // NOI18N
        Save_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Save_jMenuItemActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(Save_jMenuItem);

        SaveImagejMenuItem.setText("Save current Workflow image");
        SaveImagejMenuItem.setName("SaveImagejMenuItem"); // NOI18N
        SaveImagejMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveImagejMenuItemActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(SaveImagejMenuItem);

        jSeparator7.setName("jSeparator7"); // NOI18N
        ImportAlignmentjMenu1.add(jSeparator7);

        ImportWorkflow_jMenuItem19.setText("Import Workflow in Project");
        ImportWorkflow_jMenuItem19.setName("ImportWorkflow_jMenuItem19"); // NOI18N
        ImportWorkflow_jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportWorkflow_jMenuItem19ActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(ImportWorkflow_jMenuItem19);

        jMenuItem3.setText("Export current Workflow");
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(jMenuItem3);

        jSeparator4.setName("jSeparator4"); // NOI18N
        ImportAlignmentjMenu1.add(jSeparator4);

        ImportSequences_jMenuItem10.setText("Import Sequences");
        ImportSequences_jMenuItem10.setName("ImportSequences_jMenuItem10"); // NOI18N
        ImportSequences_jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportSequences_jMenuItem10ActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(ImportSequences_jMenuItem10);

        ImportAlignment_jMenuItem18.setText("Import Alignment");
        ImportAlignment_jMenuItem18.setName("ImportAlignment_jMenuItem18"); // NOI18N
        ImportAlignment_jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportAlignment_jMenuItem18ActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(ImportAlignment_jMenuItem18);

        ImportTrees_jMenuItem9.setText("Import Trees");
        ImportTrees_jMenuItem9.setName("ImportTrees_jMenuItem9"); // NOI18N
        ImportTrees_jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportTrees_jMenuItem9ActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(ImportTrees_jMenuItem9);

        ImportMatrix_jMenuItem7.setText("Import Matrix");
        ImportMatrix_jMenuItem7.setName("ImportMatrix_jMenuItem7"); // NOI18N
        ImportMatrix_jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportMatrix_jMenuItem7ActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(ImportMatrix_jMenuItem7);

        ImportTextResultsjMenuItem.setText("Import Text (Results)");
        ImportTextResultsjMenuItem.setName("ImportTextResultsjMenuItem"); // NOI18N
        ImportTextResultsjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportTextResultsjMenuItemActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(ImportTextResultsjMenuItem);

        ReportjMenuItem14.setText("Generate Report");
        ReportjMenuItem14.setName("ReportjMenuItem14"); // NOI18N
        ReportjMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReportjMenuItem14ActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(ReportjMenuItem14);

        jSeparator3.setName("jSeparator3"); // NOI18N
        ImportAlignmentjMenu1.add(jSeparator3);

        jMenuItem12.setText("Preferences");
        jMenuItem12.setName("jMenuItem12"); // NOI18N
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(jMenuItem12);

        jSeparator2.setName("jSeparator2"); // NOI18N
        ImportAlignmentjMenu1.add(jSeparator2);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Quit");
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        ImportAlignmentjMenu1.add(jMenuItem1);

        jMenuBar1.add(ImportAlignmentjMenu1);

        jMenu5.setText("Edit");
        jMenu5.setName("jMenu5"); // NOI18N

        jMenuItem15.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem15.setText("Copy");
        jMenuItem15.setName("jMenuItem15"); // NOI18N
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem15);

        jMenuItem16.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem16.setText("Paste");
        jMenuItem16.setName("jMenuItem16"); // NOI18N
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem16);

        jMenuItem17.setText("Clear Workflow");
        jMenuItem17.setName("jMenuItem17"); // NOI18N
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem17);

        jMenuItem9.setText("No Undo");
        jMenuItem9.setToolTipText("Currently, there is no Undo/Redo supported by design. ");
        jMenuItem9.setEnabled(false);
        jMenuItem9.setName("jMenuItem9"); // NOI18N
        jMenu5.add(jMenuItem9);

        jMenuBar1.add(jMenu5);

        jMenu3.setText("Manager");
        jMenu3.setName("jMenu3"); // NOI18N

        SequenceManagerjMenuItem.setText("Sequences");
        SequenceManagerjMenuItem.setToolTipText("Open the sequences manager.");
        SequenceManagerjMenuItem.setName("SequenceManagerjMenuItem"); // NOI18N
        SequenceManagerjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SequenceManagerjMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(SequenceManagerjMenuItem);

        TreeManagerjMenuItem.setText("Trees");
        TreeManagerjMenuItem.setToolTipText("Open the trees manager.");
        TreeManagerjMenuItem.setName("TreeManagerjMenuItem"); // NOI18N
        TreeManagerjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TreeManagerjMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(TreeManagerjMenuItem);

        AlignmentManagerjMenuItem.setText("Alignments");
        AlignmentManagerjMenuItem.setToolTipText("Open the alignments manager.");
        AlignmentManagerjMenuItem.setName("AlignmentManagerjMenuItem"); // NOI18N
        AlignmentManagerjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AlignmentManagerjMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(AlignmentManagerjMenuItem);

        jMenuItem7.setText("Workflows");
        jMenuItem7.setName("jMenuItem7"); // NOI18N
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem7);

        jMenuBar1.add(jMenu3);

        jMenu1.setText("Execution");
        jMenu1.setName("jMenu1"); // NOI18N

        Run_jMenuItem.setText("Run");
        Run_jMenuItem.setToolTipText("This is the default execution. Run all program on  the workflow even if already executed.");
        Run_jMenuItem.setName("Run_jMenuItem"); // NOI18N
        Run_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Run_jMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(Run_jMenuItem);

        RunOnCHP_jMenuItem.setText("Run on CHP");
        RunOnCHP_jMenuItem.setToolTipText("This is the default execution. Run all program on  the workflow even if already executed.");
        RunOnCHP_jMenuItem.setName("RunOnCHP_jMenuItem"); // NOI18N
        RunOnCHP_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunOnCHP_jMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(RunOnCHP_jMenuItem);

        RunSelected_jMenuItem7.setText("Run (unfinished only)");
        RunSelected_jMenuItem7.setToolTipText("Run only the un-executed programs or those with error state.");
        RunSelected_jMenuItem7.setName("RunSelected_jMenuItem7"); // NOI18N
        RunSelected_jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunSelected_jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu1.add(RunSelected_jMenuItem7);

        RunIterationjMenuItem.setText("Run (from iteration)");
        RunIterationjMenuItem.setName("RunIterationjMenuItem"); // NOI18N
        RunIterationjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunIterationjMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(RunIterationjMenuItem);

        Stop_jMenuItem.setText("Stop");
        Stop_jMenuItem.setToolTipText("Stop the currently executing workflow. Note that some errors might occur if you stop some programs before the end of their normal execution.");
        Stop_jMenuItem.setName("Stop_jMenuItem"); // NOI18N
        Stop_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Stop_jMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(Stop_jMenuItem);

        jMenuItem13.setText("Reset Workflow State");
        jMenuItem13.setName("jMenuItem13"); // NOI18N
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem13);

        jMenuBar1.add(jMenu1);

        jMenu4.setText("Tools");
        jMenu4.setName("jMenu4"); // NOI18N

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuItem4.setText("Show Toolbox");
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem4);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        jMenuItem6.setText("Show WorkFlow");
        jMenuItem6.setName("jMenuItem6"); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem6);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jMenu4.add(jSeparator1);

        jMenuItem5.setText("Workflow clustering");
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem5);

        ObjectEditor_jMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
        ObjectEditor_jMenuItem.setText("ObjectEditor");
        ObjectEditor_jMenuItem.setEnabled(false);
        ObjectEditor_jMenuItem.setName("ObjectEditor_jMenuItem"); // NOI18N
        ObjectEditor_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ObjectEditor_jMenuItemActionPerformed(evt);
            }
        });
        jMenu4.add(ObjectEditor_jMenuItem);

        ResetDevelopperStatejMenuItem5.setText("Reset All Workflow state");
        ResetDevelopperStatejMenuItem5.setEnabled(false);
        ResetDevelopperStatejMenuItem5.setName("ResetDevelopperStatejMenuItem5"); // NOI18N
        ResetDevelopperStatejMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResetDevelopperStatejMenuItem5ActionPerformed(evt);
            }
        });
        jMenu4.add(ResetDevelopperStatejMenuItem5);

        Generate_applications_report_jMenuItem.setText("Generate Application Report");
        Generate_applications_report_jMenuItem.setToolTipText("This is an internal test for the installed application. It will create a report in the results directory.");
        Generate_applications_report_jMenuItem.setEnabled(false);
        Generate_applications_report_jMenuItem.setName("Generate_applications_report_jMenuItem"); // NOI18N
        Generate_applications_report_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Generate_applications_report_jMenuItemActionPerformed(evt);
            }
        });
        jMenu4.add(Generate_applications_report_jMenuItem);

        Generate_applications_run_report_jMenuItem.setText("Generate Application Run Report");
        Generate_applications_run_report_jMenuItem.setEnabled(false);
        Generate_applications_run_report_jMenuItem.setName("Generate_applications_run_report_jMenuItem"); // NOI18N
        Generate_applications_run_report_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Generate_applications_run_report_jMenuItemActionPerformed(evt);
            }
        });
        jMenu4.add(Generate_applications_run_report_jMenuItem);

        DatabaseExplorer_jMenuItem.setText("DatabaseExplorer");
        DatabaseExplorer_jMenuItem.setEnabled(false);
        DatabaseExplorer_jMenuItem.setName("DatabaseExplorer_jMenuItem"); // NOI18N
        DatabaseExplorer_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DatabaseExplorer_jMenuItemActionPerformed(evt);
            }
        });
        jMenu4.add(DatabaseExplorer_jMenuItem);

        jMenuBar1.add(jMenu4);

        jMenu2.setText("Help");
        jMenu2.setToolTipText("Help menu");
        jMenu2.setName("jMenu2"); // NOI18N

        jMenuItem2.setText("About");
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuItem8.setText("Online Help");
        jMenuItem8.setName("jMenuItem8"); // NOI18N
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem8);

        Sample_jMenu.setText("Samples");
        Sample_jMenu.setName("Sample_jMenu"); // NOI18N
        jMenu2.add(Sample_jMenu);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 1012, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        if (workbox.getCurrentWorkflows().isChanged()) {
            String msg="<html>Warning, workflow not saved.<br><br>Do you want to save the workflow before exiting?</html>";
            Object[] options = {"Yes","No","Cancel"};
            int n = JOptionPane.showOptionDialog(this,msg,"Save workflow before exit...",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options, options[2]);
            switch(n) {
                case 0:  workbox.saveWorkflowToDatabase("Save on "+Util.returnCurrentDateAndTime());
                //A pause so user can see the message before
                //the window actually closes.
                ActionListener task = new ActionListener() {
                    boolean alreadyDisposed = false;
                    public void actionPerformed(ActionEvent e) {
                        if (!alreadyDisposed) {
                            alreadyDisposed = true;
                            frame.dispose();
                            System.exit(0);
                        }
                    }
                };
                Timer timer = new Timer(500, task); //fire every half second
                timer.setInitialDelay(0);        //first delay 2 seconds
                timer.setRepeats(false);
                timer.start();
                break;
                case 1:
                    ActionListener task2 = new ActionListener() {
                        boolean alreadyDisposed = false;
                        public void actionPerformed(ActionEvent e) {
                            if (!alreadyDisposed) {
                                alreadyDisposed = true;
                                frame.dispose();
                                System.exit(0);
                            }
                        }
                    };
                    Timer timer2 = new Timer(500, task2); //fire every half second
                    timer2.setInitialDelay(0);        //first delay 2 seconds
                    timer2.setRepeats(false);
                    timer2.start();
                    break;
                default:
                    
            }
        } else {
            frame.dispose();
            System.exit(0);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed
    
    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.toolbox.mazimizeSize();
        this.toolbox.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed
    
    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        this.workbox.mazimizeSize();
        this.workbox.setWorkflowVisible();
        this.workbox.getCurrentArmadilloWorkflow().redraw();
    }//GEN-LAST:event_jMenuItem6ActionPerformed
    
    private void Toolbox_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Toolbox_jButtonActionPerformed
        this.toolbox.mazimizeSize();
        this.toolbox.setVisible(true);
    }//GEN-LAST:event_Toolbox_jButtonActionPerformed
    
    private void Workflow_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Workflow_jButtonActionPerformed
        this.workbox.mazimizeSize();
        this.workbox.setWorkflowVisible();
        this.workbox.getCurrentArmadilloWorkflow().redraw();
    }//GEN-LAST:event_Workflow_jButtonActionPerformed
    
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.workbox.mazimizeSize();
        workbox.setScriptVisible();
    }//GEN-LAST:event_jButton3ActionPerformed
    
    private void ObjectEditor_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ObjectEditor_jMenuItemActionPerformed
        editor.setVisible(true); 
    }//GEN-LAST:event_ObjectEditor_jMenuItemActionPerformed
    
    private void SequenceManagerjMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SequenceManagerjMenuItemActionPerformed
        loadSequence.display();
    }//GEN-LAST:event_SequenceManagerjMenuItemActionPerformed
    
    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        workbox.ShowPreferences();
    }//GEN-LAST:event_jMenuItem12ActionPerformed
    
    private void ReportjMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReportjMenuItem14ActionPerformed
        //--TO DO: Menu with application
        generateReport();
    }//GEN-LAST:event_ReportjMenuItem14ActionPerformed
    
    private void SaveAs_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveAs_jMenuItemActionPerformed
        JFileChooser jf=new JFileChooser(config.getExplorerPath());
        jf.setFileFilter(new WorkflowFilter());
        String tmpfile="";
        jf.setName("Saving Workflow...");
        int result=jf.showSaveDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            tmpfile=jf.getSelectedFile().getAbsolutePath();
            String workflowname=jf.getSelectedFile().getName();
            String path=jf.getSelectedFile().getPath();
            config.setExplorerPath(path);
            config.setLastWorkflow(tmpfile);
            config.Save();
            //Workflows workflows=new Workflows(workbox.getCurrentArmadilloWorkflow());
            workbox.saveWorkflowAs(tmpfile, workflowname);
            workbox.getCurrentArmadilloWorkflow().setName(tmpfile);
            //--Set Window name
            this.setTitle(config.get("applicationName")+" "+config.get("version")+" - "+tmpfile);
            //--Enable saving to the Save-Menu
            this.Save_jMenuItem.setEnabled(true);
//            databaseFunction df=new databaseFunction();
//             if (!df.saveAs(tmpfile)) {
//                    JOptionPane.showMessageDialog(this,"Unable to save Workflow to "+tmpfile,"Warning!",JOptionPane.ERROR_MESSAGE);
//            } else {
//                workbox.getCurrentArmadilloWorkflow().setName(workflowname);
//            }
        }
        //
}//GEN-LAST:event_SaveAs_jMenuItemActionPerformed
    
    private void OpenWorkflow_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenWorkflow_jMenuItemActionPerformed
        JFileChooser jf=new JFileChooser(config.projectsDir());
        jf.setFileFilter(new WorkflowFilter());
        String tmpfile="";
        jf.setName("Loading Workflow...");
        int result=jf.showOpenDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            tmpfile=jf.getSelectedFile().getAbsolutePath();
            String path=jf.getSelectedFile().getPath();
            config.setExplorerPath(path);
            config.Save();
            databaseFunction df=new databaseFunction();
            
            //--CASE 1. Test file type
            if (isTextFile(tmpfile)) {
                if (workbox.loadWorkflowAsTxt(tmpfile)) {
                    //workbox.getCurrentArmadilloWorkflow().setName(workflowname);
                    workbox.getCurrentArmadilloWorkflow().setName(tmpfile);
                    //this.setTitle(config.get("applicationName")+" "+config.get("version")+" - "+tmpfile);
                    //--Enable saving to the Save-Menu
                    this.Save_jMenuItem.setEnabled(true);
                } else  {
                    JOptionPane.showMessageDialog(this,"Unable to load Workflow from "+tmpfile,"Warning!",JOptionPane.ERROR_MESSAGE);
                }
                //--CASE 2. Normal db
            } else {
                if (df.Open(tmpfile)) {
                    toolbox.reloadDatabaseTree();
                    int workflow_id=df.getNextWorkflowsID()-1;
                    if (workflow_id>0) {
                        workbox.loadWorkflowFromDatabase(workflow_id);
                        //workbox.getCurrentArmadilloWorkflow().setName(workflowname);
                        workbox.getCurrentArmadilloWorkflow().setName(tmpfile);
                        
                        setTitle(config.get("applicationName")+" "+config.get("version")+" - "+tmpfile);
                        
                        
                        //--Enable saving to the Save-Menu
                        this.Save_jMenuItem.setEnabled(true);
                        config.setLastWorkflow(tmpfile);
                        config.Save();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,"Unable to load Workflow from "+tmpfile,"Warning!",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        //
}//GEN-LAST:event_OpenWorkflow_jMenuItemActionPerformed
    
    public boolean isTextFile(String filename) {
        try {
            BufferedReader b=new BufferedReader(new FileReader(new File(filename)));
            String line=b.readLine();
            if (line==null) return false;
            if (!line.startsWith("#")) return false;
        } catch(Exception e) {return false;}
        return true;
    }
    
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        AboutJDialog about=new AboutJDialog(this, true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed
    
    private void Save_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Save_jMenuItemActionPerformed
        //--TO do embed in correct method
        workbox.getCurrentWorkflows().setId(0);
        workbox.saveWorkflowToDatabase("Save on "+Util.returnCurrentDateAndTime());
}//GEN-LAST:event_Save_jMenuItemActionPerformed
    
    private void SaveImagejMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveImagejMenuItemActionPerformed
        JFileChooser jf=new JFileChooser(config.getExplorerPath());
        jf.addChoosableFileFilter(new ImageFilter());
        jf.setAcceptAllFileFilterUsed(false);
        jf.setName("Saving Image to ...");
        int result=jf.showSaveDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            String filename=jf.getSelectedFile().getAbsolutePath();
            String path=jf.getSelectedFile().getPath();
            config.setExplorerPath(path);
            config.Save();
            if (filename.toLowerCase().endsWith("pdf")) {
                workbox.getCurrentArmadilloWorkflow().savePDF(filename);
            } else workbox.getCurrentArmadilloWorkflow().saveImage(filename);
        }
    }//GEN-LAST:event_SaveImagejMenuItemActionPerformed
    
    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        JFileChooser jf=new JFileChooser(config.getExplorerPath());
        jf.setFileFilter(new WorkflowImportExportFilter());
        jf.setName("Saving Workflow as text file to ...");
        int result=jf.showSaveDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            String filename=jf.getSelectedFile().getAbsolutePath();
            String path=jf.getSelectedFile().getPath();
            config.setExplorerPath(path);
            config.Save();
            workbox.saveWorkflowAsTxt(filename);
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed
    
    private void TreeManagerjMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TreeManagerjMenuItemActionPerformed
        loadTree.display();
    }//GEN-LAST:event_TreeManagerjMenuItemActionPerformed
    
    private void NewProject_jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewProject_jMenuItem8ActionPerformed
        
        Object[] options = {"Yes","No","Cancel"};
        int result=JOptionPane.showOptionDialog(this, "Do you want to migrate everything to your new project?","", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,null,options, options[2]);
        switch(result) {
            case JOptionPane.YES_OPTION:
                //--Same as SaveAs...+Load
                CreateOrRenameWorkflowJDialog rp=new CreateOrRenameWorkflowJDialog(this, "New_Untitled","Create New Project");
                rp.setVisible(true);
                if (!rp.projectName.equals("")) {
                    //workbox.newWorkflowWithMigration(config.projectsDir()+File.separator+rp.projectName, rp.projectName);
                    //--Copy to new filename
                    databaseFunction df=new databaseFunction();
                    df.Close();
                    try {
                        String destination=config.projectsDir()+File.separator+rp.projectName;
                        String source=this.workbox.getCurrentWorkflowFilename();
                        Util.copy(source, destination);
                        config.setLastWorkflow(destination);
                        config.Save();
                        if (df.Open(destination)) {
                            toolbox.reloadDatabaseTree();
                            toolbox.reloadWorkflowsTree();
                            int workflow_id=df.getNextWorkflowsID()-1;
                            if (workflow_id>0) {
                                workbox.loadWorkflowFromDatabase(workflow_id);
                                workbox.getCurrentArmadilloWorkflow().setName(destination);
                                //--Enable saving to the Save-Menu
                                this.Save_jMenuItem.setEnabled(true);
                                setTitle(destination);
                            }
                        }
                    } catch(Exception e) {
                        workbox.MessageError("Unable to create new project as "+rp.projectName+"...","");
                        
                    }
                    
//
                    //--Enable saving to the Save-Menu
                    this.Save_jMenuItem.setEnabled(true);
                }
                break;
            case JOptionPane.NO_OPTION:
                rp=new CreateOrRenameWorkflowJDialog(this, "New_Untitled","Create New Project");
                rp.setVisible(true);
                if (!rp.projectName.equals("")) {
                    //workbox.newWorkflowWithMigration(config.projectsDir()+File.separator+rp.projectName, rp.projectName);
                    //--Copy to new filename
                    databaseFunction df=new databaseFunction();
                    df.Close();
                    try {
                        String destination=config.projectsDir()+File.separator+rp.projectName;
                        String source=config.dataPath()+File.separator+"New_Untitled_default.db";
                        Util.copy(source, destination);
                        config.setLastWorkflow(destination);
                        config.Save();
                        if (df.Open(destination)) {
                            toolbox.reloadDatabaseTree();
                            int workflow_id=df.getNextWorkflowsID()-1;
                            if (workflow_id>0) {
                                workbox.loadWorkflowFromDatabase(workflow_id);
                                workbox.getCurrentArmadilloWorkflow().setName(destination);
                                //--Enable saving to the Save-Menu
                                this.Save_jMenuItem.setEnabled(true);
                                setTitle(destination);
                            }
                        }
                        
                    } catch(Exception e) {
                        workbox.MessageError("Unable to create new project as "+rp.projectName+"...","");
                    }
                }
                break;
                
            default: return;
        }
        //--Update the visual information...
        
        
        
        
    }//GEN-LAST:event_NewProject_jMenuItem8ActionPerformed
    
    private void DatabaseExplorer_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DatabaseExplorer_jMenuItemActionPerformed
        this.databasebox.setVisible(true);
    }//GEN-LAST:event_DatabaseExplorer_jMenuItemActionPerformed
    
    private void GenerateReportjButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GenerateReportjButtonActionPerformed
        generateReport();
    }//GEN-LAST:event_GenerateReportjButtonActionPerformed
    
    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        String msg="<html>Are you sure you want to delete <b>all objects in workflow</b>?</html>";
        Object[] options = {"Cancel","Delete All"};
        int o = JOptionPane.showOptionDialog(this,msg,"Warning! Deleting All objects in workflow",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options, options[0]);
        switch (o) {
            //0. Cancel
            case 0: break;
                //1. Delete All
            case 1:
                workbox.getCurrentArmadilloWorkflow().workflow.selectAll();
                workbox.getCurrentArmadilloWorkflow().workflow.deleteSelected();
                workbox.getCurrentArmadilloWorkflow().force_redraw=true;
                workbox.getCurrentArmadilloWorkflow().redraw();
                break;
        }
        
        
    }//GEN-LAST:event_jMenuItem17ActionPerformed
    
    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        workbox.getCurrentArmadilloWorkflow().copySelection();
    }//GEN-LAST:event_jMenuItem15ActionPerformed
    
    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        workbox.getCurrentArmadilloWorkflow().pasteSelection();
        workbox.getCurrentArmadilloWorkflow().force_redraw=true;
        workbox.getCurrentArmadilloWorkflow().redraw();
    }//GEN-LAST:event_jMenuItem16ActionPerformed
    
    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        workbox.getCurrentArmadilloWorkflow().resetState();
        workbox.getCurrentArmadilloWorkflow().force_redraw=true;
        workbox.getCurrentArmadilloWorkflow().redraw();
    }//GEN-LAST:event_jMenuItem13ActionPerformed
    
    private void ImportSequences_jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportSequences_jMenuItem10ActionPerformed
        this.importSequenceFromDisk();
    }//GEN-LAST:event_ImportSequences_jMenuItem10ActionPerformed
    
    private void ImportTrees_jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportTrees_jMenuItem9ActionPerformed
        this.importTreeFromDisk();
    }//GEN-LAST:event_ImportTrees_jMenuItem9ActionPerformed
    
    private void ImportAlignmentjMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportAlignmentjMenu1ActionPerformed
        this.importAlignmentFromDisk();
    }//GEN-LAST:event_ImportAlignmentjMenu1ActionPerformed
    
    private void ImportAlignment_jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportAlignment_jMenuItem18ActionPerformed
        this.importAlignmentFromDisk();
    }//GEN-LAST:event_ImportAlignment_jMenuItem18ActionPerformed
    
    private void NewWorkflow_jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewWorkflow_jMenuItem11ActionPerformed
        workbox.newWorkflow();
    }//GEN-LAST:event_NewWorkflow_jMenuItem11ActionPerformed
    
    private void ImportWorkflow_jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportWorkflow_jMenuItem19ActionPerformed
        JFileChooser jf=new JFileChooser(config.projectsDir());
        jf.setFileFilter(new WorkflowImportExportFilter());
        String tmpfile="";
        jf.setName("Loading Workflow...");
        int result=jf.showOpenDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            tmpfile=jf.getSelectedFile().getAbsolutePath();
            String workflowname=jf.getSelectedFile().getName();
            String path=jf.getSelectedFile().getPath();
            config.setExplorerPath(path);
            config.Save();
            databaseFunction df=new databaseFunction();
            
            //--CASE 1. Test file type
            if (isTextFile(tmpfile)) {
                if (workbox.loadWorkflowAsTxt(tmpfile)) {
                    //workbox.getCurrentArmadilloWorkflow().setName(workflowname);
                    workbox.getCurrentArmadilloWorkflow().setName(tmpfile);
                    this.setTitle(config.get("applicationName")+" "+config.get("version")+" - "+tmpfile);
                    //--Enable saving to the Save-Menu
                    this.Save_jMenuItem.setEnabled(true);
                } else  {
                    JOptionPane.showMessageDialog(this,"Unable to load Workflow from "+tmpfile,"Warning!",JOptionPane.ERROR_MESSAGE);
                }
                //--CASE 2. Normal db
            } else {
                if (df.Open(tmpfile)) {
                    toolbox.reloadDatabaseTree();
                    int workflow_id=df.getNextWorkflowsID()-1;
                    if (workflow_id>0) {
                        workbox.loadWorkflowFromDatabase(workflow_id);
                        //workbox.getCurrentArmadilloWorkflow().setName(workflowname);
                        workbox.getCurrentArmadilloWorkflow().setName(tmpfile);
                        //--Enable saving to the Save-Menu
                        this.Save_jMenuItem.setEnabled(true);
                        config.setLastWorkflow(tmpfile);
                        config.Save();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,"Unable to load Workflow from "+tmpfile,"Warning!",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_ImportWorkflow_jMenuItem19ActionPerformed
    
    private void ImportTextResultsjMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportTextResultsjMenuItemActionPerformed
        this.importTextFromDisk();
    }//GEN-LAST:event_ImportTextResultsjMenuItemActionPerformed
    
    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        displayHTML( "http://bioinfo.uqam.ca/armadillo/");
    }//GEN-LAST:event_jMenuItem8ActionPerformed
    
    private void AlignmentManagerjMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AlignmentManagerjMenuItemActionPerformed
        loadAlignment.display();
    }//GEN-LAST:event_AlignmentManagerjMenuItemActionPerformed
    
    private void Run_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Run_jMenuItemActionPerformed
        this.workbox.resetState();
        this.workbox.setWorkboxAsTest(false);
        this.workbox.Run();
    }//GEN-LAST:event_Run_jMenuItemActionPerformed
    
    private void Stop_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Stop_jMenuItemActionPerformed
        this.workbox.Stop();
    }//GEN-LAST:event_Stop_jMenuItemActionPerformed
    
    private void Generate_applications_report_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Generate_applications_report_jMenuItemActionPerformed
        generateApplicationsReport();
        //this.workbox.CreateScreenShot(config.resultsDir());
    }//GEN-LAST:event_Generate_applications_report_jMenuItemActionPerformed
    
    private void RunSelected_jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunSelected_jMenuItem7ActionPerformed
        this.workbox.Run();
    }//GEN-LAST:event_RunSelected_jMenuItem7ActionPerformed
    
    private void SequenceManager_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SequenceManager_jButtonActionPerformed
        loadSequence.display();
    }//GEN-LAST:event_SequenceManager_jButtonActionPerformed
    
    private void TreeManager_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TreeManager_jButtonActionPerformed
        loadTree.display();
    }//GEN-LAST:event_TreeManager_jButtonActionPerformed
    
    private void AlignmentManager_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AlignmentManager_jButtonActionPerformed
        loadAlignment.display();
    }//GEN-LAST:event_AlignmentManager_jButtonActionPerformed
    
    private void ResetDevelopperStatejMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResetDevelopperStatejMenuItem5ActionPerformed
        workbox.getCurrentArmadilloWorkflow().resetAllState();
        workbox.getCurrentArmadilloWorkflow().force_redraw=true;
        workbox.getCurrentArmadilloWorkflow().redraw();
    }//GEN-LAST:event_ResetDevelopperStatejMenuItem5ActionPerformed
    
    private void Generate_applications_run_report_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Generate_applications_run_report_jMenuItemActionPerformed
        generateApplicationRunReport();
        
    }//GEN-LAST:event_Generate_applications_run_report_jMenuItemActionPerformed
    
    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        chooseWorkflowJDialog.display();
    }//GEN-LAST:event_jMenuItem5ActionPerformed
    
    private void ImportMatrix_jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportMatrix_jMenuItem7ActionPerformed
        this.importMatrixFromDisk();
    }//GEN-LAST:event_ImportMatrix_jMenuItem7ActionPerformed
    
    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
       this.workflowexplorer.display();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void RunIterationjMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunIterationjMenuItemActionPerformed
        StartIterationJDialog dialog=new StartIterationJDialog(this.frame,true,"");
        dialog.setVisible(true);
        
    }//GEN-LAST:event_RunIterationjMenuItemActionPerformed

    private void RunOnCHP_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunOnCHP_jMenuItemActionPerformed
        this.workbox.resetState();
        this.workbox.setWorkboxAsTest(true);
        this.workbox.Run();
    }//GEN-LAST:event_RunOnCHP_jMenuItemActionPerformed
    
    ////////////////////////////////////////////////////////////////////////////
    /// Generate Application report
    
    private void generateApplicationsReport() {
        report re=new report();
        displayHTML(re.generate_Application_Report());
    }
    
    //--See the swing workfer...
//     private void generateApplicationsRunReport() {
//        report re=new report();
//        displayHTML(re.generate_ApplicationRun_Report());
//    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// SAMPLE Menu
    
    /**
     * Open the sample in the menu...
     * @param evt
     */
    private void  SampleMenuActionPerformed(java.awt.event.ActionEvent evt) {
        String command=evt.getActionCommand();
        String filename=config.currentPath()+File.separator+"examples"+File.separator+command;
        String destination=config.projectsDir()+File.separator+command;
        int count=0;
        while(Util.FileExists(destination)) {
            destination=config.projectsDir()+File.separator+(count++)+command;
        }
        //filename=config.dataPath()+File.separator+"help"+File.separator+f.getName();
        //--Open the workflow
        try {
            Util.copy(filename, destination);
        } catch(Exception e) {
            Config.log("Unable to copy workflow (.db) - "+filename+" to "+destination);
        }
        Config.log("Loading the workflow "+filename);
        
        databaseFunction df=new databaseFunction();
        if (df.Open(destination)) {
            setTitle(config.get("applicationName")+" "+config.get("version")+" - "+destination);
            toolbox.reloadDatabaseTree();
            int workflow_id=df.getNextWorkflowsID()-1;
            if (workflow_id>0) {
                workbox.loadWorkflowFromDatabase(workflow_id);
                workbox.getCurrentArmadilloWorkflow().setName(destination);
                config.setLastWorkflow(destination);
                config.Save();
            }
        } else {
            Config.log("Unable to open workflow (.db) - "+filename);
        }
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    /// Project creation
    
    /**
     * This will either load or create a new Project...
     * @param database
     * @return
     */
    private String NewProject(String databaseFilename) {
        
        
        //--Load normally...
        this.Save_jMenuItem.setEnabled(true);
        //--Set the database to be loaded
        if (!databaseFilename.isEmpty()) config.set("databasePath", databaseFilename);
        //--This is the main loading of the database
        databaseFunction df=new databaseFunction();
        //--Load the project description
        int projectid=df.getNextProjectID()-1;
        if (projectid>0) {
            project=new Project(projectid);
        } else {
            project=new Project();
            project.saveToDatabase();
        }
        databaseFilename=config.get("databasePath");
        //--Return the dataseFilename
        return databaseFilename;
    }
    
    /**
     * This display the workflow...
     */
    public void showWorkflow() {
        this.toolbox.mazimizeSize();
        this.toolbox.setVisible(true);
        this.workbox.mazimizeSize();
        this.workbox.setWorkflowVisible();
    }
    
    /**
     * This will create a report of the currently displayed workflow and
     * displayed it in the "Report" Pane of the WorkflowJInternalPane
     */
    private void generateReport() {
        
        SwingWorker<Boolean, String> infoSwingWorker=new SwingWorker<Boolean, String>()  {
            
            @Override
            protected Boolean doInBackground() throws Exception {
                boolean status=false;
                //--TO DO... Divide the report generator in different part...
                setProgress(25);
                setProgress(50);
                setProgress(75);
                //--Generate report
                //results result=new results();
                report result=new report();
                final String reportFile=result.generate_Report(workbox.getProject(), workbox.getCurrentWorkflows());
                //--Try to display it in Browser...
                displayHTML(reportFile);
                setProgress(100);
                return true;
            }
            
            //On update notre Table avec les resultats partiels
            @Override
            protected void process(List<String> chunks) {
                for (String data:chunks) {
                    if (data.startsWith("Error")||data.startsWith("Unable")) {
                        // TO DO
                    } else {
                        // TO DO
                    }
                }
            }
            
            
            @Override
            protected void done(){
                setProgress(100);
                loading.setVisible(false);
            }
            
        }; //End SwingWorker declaration
        
        infoSwingWorker.addPropertyChangeListener(
                new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                        if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                                //Handled in done() function in SwingWorker
                            }
                        }//End progress update
                    } //End populateNetworkPropertyChange
                });
        loading=new InformationJDialog(this, false, infoSwingWorker, "Generating Report for current workflow");
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Generating...", "");
        infoSwingWorker.execute();
    }
    
    /**
     * This will create a report of the currently displayed workflow and
     * displayed it in the "Report" Pane of the WorkflowJInternalPane
     */
    private void generateWorkflow_List_Report() {
        
        SwingWorker<Boolean, String> infoSwingWorker=new SwingWorker<Boolean, String>()  {
            
            @Override
            protected Boolean doInBackground() throws Exception {
                boolean status=false;
                //--TO DO... Divide the report generator in different part...
                setProgress(25);
                setProgress(50);
                setProgress(75);
                //--Generate report
                //results result=new results();
                report_list_workflows result=new report_list_workflows();
                final String reportFile=result.generateResults(workbox.getProject(), workbox.getCurrentWorkflows());
                //--Try to display it in Browser...
                displayHTML(reportFile);
                setProgress(100);
                
                return true;
            }
            
            //On update notre Table avec les resultats partiels
            @Override
            protected void process(List<String> chunks) {
                for (String data:chunks) {
                    if (data.startsWith("Error")||data.startsWith("Unable")) {
                        
                    } else {
                        
                    }
                }
            }
            
            
            @Override
            protected void done(){
                setProgress(100);
                loading.setVisible(false);
            }
            
        }; //End SwingWorker declaration
        
        infoSwingWorker.addPropertyChangeListener(
                new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                        if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                                //Handled in done() function in SwingWorker
                            }
                        }//End progress update
                    } //End populateNetworkPropertyChange
                });
        loading=new InformationJDialog(this, false, infoSwingWorker, "Generating Report for current workflow");
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Generating...", "");
        infoSwingWorker.execute();
    }
    
    
    
    /**
     * This will create a report of the currently displayed workflow and
     * displayed it in the "Report" Pane of the WorkflowJInternalPane
     */
    private void generateApplicationRunReport() {
        
        SwingWorker<Boolean, String> infoSwingWorker=new SwingWorker<Boolean, String>()  {
            
            @Override
            protected Boolean doInBackground() throws Exception {
                //--TO DO... Divide the report generator in different part...
                report result=new report();
                final String reportFile=result.generate_ApplicationRun_Report();
                //--Try to display it in Browser...
                displayHTML(reportFile);
                
                return true;
            }
            
            //On update notre Table avec les resultats partiels
            @Override
            protected void process(List<String> chunks) {
                for (String data:chunks) {
                    
                }
            }
            
            
            @Override
            protected void done(){
                setProgress(100);
                //loading.setVisible(false);
            }
            
        }; //End SwingWorker declaration
        
        infoSwingWorker.addPropertyChangeListener(
                new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                        if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                //loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                                //Handled in done() function in SwingWorker
                            }
                        }//End progress update
                    } //End populateNetworkPropertyChange
                });
//        loading=new InformationJDialog(this, false, infoSwingWorker, "Generating Report for current workflow");
//        loading.setProgress(0); //Put 0% as the start progress
//        loading.Message("Generating...", "");
        infoSwingWorker.execute();
    }
    
    public void displayHTML(final String reportFile) {
        if (!reportFile.startsWith("http")) workbox.Message("Report generated in "+reportFile, reportFile);
        try{
            if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)){
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            if (reportFile.startsWith("http")) {
                                Desktop.getDesktop().browse(URI.create(reportFile));
                            } else {
                                File f=new File(reportFile);
                                Desktop.getDesktop().browse(f.toURI());
                            }
                        } catch (Exception ex) {
                        }
                    }
                }.start();
            } else {
                JOptionPane.showMessageDialog(this,"Unable to display report : "+reportFile,"",JOptionPane.ERROR_MESSAGE);
            }
        }catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Unable to display report : "+reportFile,"",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Simple thread to load file into the project
     */
    private void loadTreeFiles(final File[] files, final String groupname, final String note) {
        final LinkedList<File> toLoad=new LinkedList<File>();
        final int totalToLoad=files.length;
        for(File f:files) toLoad.add(f);
        
        SwingWorker<Integer, Object> loadSwingWorker2=new SwingWorker<Integer, Object>() {
            String filename="";
            
            @Override
            protected Integer doInBackground() throws Exception {
                //We dont check for cancelled
                
                while (!isCancelled()&&toLoad.size()>0) {
                    File f=toLoad.pollFirst();
                    filename=f.getName();
                    publish("Loading "+filename);
                    MultipleTrees multi=new MultipleTrees();
                    multi.setName(groupname+"_"+filename);
                    multi.readNewick(f.getAbsolutePath());
                    multi.setNote(note);
                    multi.saveToDatabase();
                    setProgress((totalToLoad-toLoad.size())*100/totalToLoad);
                    if (multi.getId()==0) {
                        publish("Unable to load tree(s) from "+filename);
                    } else {
                        publish("Successfully imported tree(s) from "+filename);
                    }
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
                //Update UI by reloading alll sequences... (to be sure)...
                toolbox.reloadDatabaseTree();
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
                                //Handled in done() function in SwingWorker
                            }
                        }//End progress update
                    } //End populateNetworkPropertyChange
                });
        //Finally. Show a load dialog :: Warning Work-In-Progress
        loading=new InformationJDialog(this, false, loadSwingWorker2,"");
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Loading "+files.length+" files...", "");
        loadSwingWorker2.execute();
    }
    
    /**
     * Simple thread to load file into the project
     */
    public void loadSequenceFiles(final File[] files, final String groupname, final String note, final String type) {
        final LinkedList<File> toLoad=new LinkedList<File>();
        final int totalToLoad=files.length;
        for(File f:files) toLoad.add(f);
        
        SwingWorker<Integer, Object> loadSwingWorker2=new SwingWorker<Integer, Object>() {
            String filename="";
            
            @Override
            protected Integer doInBackground() throws Exception {
                //We dont check for cancelled
                setProgress(0);
                
                int currentSize=1;
                while (!isCancelled()&&toLoad.size()>0) {
                    
                    File f=toLoad.pollFirst();
                    filename=f.getAbsolutePath();
                    publish("Loading MultipleSequences from "+filename+"... ["+currentSize+"/"+totalToLoad+"]");
                    //--This is a duplicate of the code from MultipleSequence.loadSequenceFromFasta...
                    //--First. Conversion to fasta if not using ReadSeq
                    
                    MultipleSequences multi=new MultipleSequences();
                    try {
                        multi.loadFromFile(filename);
                        if (groupname.length()>1) {
                            multi.setName(groupname+"_"+filename);
                        } else {
                            multi.setName(filename);
                        }
                        multi.setNote(note);
                    } catch(Exception e) {
                        Config.log("Error. Unable to load MultipleSequence(s) from "+filename);
                    }
                    
//                    if (!filename.toLowerCase().endsWith("fasta")) {
//                        publish("Converting to fasta...");
//                        try {
//                         int outid= BioseqFormats.formatFromName("fasta");
//                            BioseqWriterIface seqwriter= BioseqFormats.newWriter(outid);
//                            seqwriter.setOutput(new FileWriter("out.fasta"));
//                            seqwriter.writeHeader();
//                            Readseq rd= new Readseq();
//                             rd.setInputObject(filename);
//                             if (rd.isKnownFormat() && rd.readInit())
//                             rd.readTo( seqwriter);
//                            seqwriter.writeTrailer();
//                        } catch(Exception e) {
//                            Config.log("Unable to read "+filename+" using ReadSeq...");
//                        }
//                        filename="out.fasta";
//                    }
                    //--Second. Actual reading
//                  try {
//                      multi.getSequences().clear(); //Clear the sequence vector and open the file
//                      File file=new File(filename);
//                      BufferedReader br =new BufferedReader(new FileReader(file));
//                      long filesize=file.length();
//                      long countsize=0;
//
//                      //VARIABLES
//                      boolean sequenceMode=false;
//                      Sequence tmp = new Sequence();                        //Temp sequence
//                      StringBuilder tmpsequence=new StringBuilder();        //Temp sequence string
//                      String stri="";                                       //Temp read line
//
//                      //Read the file in a buffer an parse at the same time
//                     //Process :: We read like a fasta file
//                      Config.log("Reading fasta:"+filename+":");
//
//                      while (br.ready()) {
//                          stri=br.readLine();
//                          countsize+=stri.length();
//                          //Config.log(stri);
//                          if (sequenceMode&&(stri.equals("")||stri.startsWith(">"))) {
//                              tmp.setSequence(tmpsequence.toString());
//                              tmp.loadInfoFromName();
//                              tmp.setSequence_type(type);
//                              //Add sequence if not empty
//                              if (tmp.getSequence().length()>0) multi.getSequences().add(tmp);
//                              tmp=new Sequence();
//                              tmpsequence=new StringBuilder();
//                              sequenceMode=false;
//                          }
//                          if (sequenceMode) {
//                              tmpsequence.append(stri);
//                              //--5% countter
//                              int percent=(int) (countsize*100/filesize);
//                              if (percent%5==0) {
//                                  //Config.log("*");
//                                  setProgress(percent);
//                              }
//                          }
//                          if (stri.startsWith(">")) {
//                                //We have a fasta definition
//                                tmp.setName(stri.substring(1)); //remove >
//                                sequenceMode=true;
//                          }
//
//                      } //end while
//                      //Add last read
//                      if (sequenceMode) {
//                          tmp.setSequence(tmpsequence.toString());
//                          tmp.loadInfoFromName();
//                          if (tmp.getSequence().length()>0) multi.getSequences().add(tmp);
//                          tmp=new Sequence();
//                      }
//                      br.close();
//                   } catch(Exception e) {e.printStackTrace();Config.log("Error with "+filename);}
                    Config.log("done");
                    publish("Saving to database...");
                    multi.saveToDatabase();
                    setProgress((totalToLoad-toLoad.size())*100/totalToLoad);
                    if (multi.getId()==0) {
                        publish("Unable to load MultipleSequence(s) from "+filename);
                    } else {
                        publish("Successfully imported MultipleSequence(s) from "+filename);
                    }
                    currentSize++;
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
                //Update UI by reloading alll sequences... (to be sure)...
                toolbox.reloadDatabaseTree();
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
                                //Handled in done() function in SwingWorker
                            }
                        }//End progress update
                    } //End populateNetworkPropertyChange
                });
        //Finally. Show a load dialog :: Warning Work-In-Progress
        loading=new InformationJDialog(this, false, loadSwingWorker2,"Loading files...");
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Loading "+files.length+" files...", "");
        loadSwingWorker2.execute();
    }
    
    /**
     * Simple thread to load file into the project
     */
    public void loadAlignmentFiles(final File[] files, final String groupname, final String note, final String type) {
        final LinkedList<File> toLoad=new LinkedList<File>();
        final int totalToLoad=files.length;
        for(File f:files) toLoad.add(f);
        
        SwingWorker<Integer, Object> loadSwingWorker2=new SwingWorker<Integer, Object>() {
            String filename="";
            
            @Override
            protected Integer doInBackground() throws Exception {
                //We dont check for cancelled
                
                while (!isCancelled()&&toLoad.size()>0) {
                    File f=toLoad.pollFirst();
                    filename=f.getAbsolutePath();
                    publish("Loading Alignment "+filename+"...");
                    //--This is a duplicate of the code from MultipleSequence.loadSequenceFromFasta...
                    //--First. Conversion to fasta if not using ReadSeq
                    setProgress(0);
                    Alignment multi=new Alignment();
                    try {
                        multi.loadFromFile(filename);
                        if (groupname.length()>1) {
                            multi.setName(groupname+"_"+filename);
                        } else {
                            multi.setName(filename);
                        }
                        multi.setNote(note);
                    } catch(Exception e) {
                        Config.log("Error. Unable to load Alignment sequence(s) from "+filename);
                    }
                    
                    
                    
                    
                    
//                    if (!filename.toLowerCase().endsWith("fasta")) {
//                        publish("Converting to fasta...");
//                        try {
//                         int outid= BioseqFormats.formatFromName("fasta");
//                            BioseqWriterIface seqwriter= BioseqFormats.newWriter(outid);
//                            seqwriter.setOutput(new FileWriter("out.fasta"));
//                            seqwriter.writeHeader();
//                            Readseq rd= new Readseq();
//                             rd.setInputObject(filename);
//                             if (rd.isKnownFormat() && rd.readInit())
//                             rd.readTo( seqwriter);
//                            seqwriter.writeTrailer();
//                        } catch(Exception e) {
//                            Config.log("Unable to read "+filename+" using ReadSeq...");
//                        }
//                        filename="out.fasta";
//                    }
//                    //--Second. Actual reading
//                  try {
//                      multi.getSequences().clear(); //Clear the sequence vector and open the file
//                      File file=new File(filename);
//                      BufferedReader br =new BufferedReader(new FileReader(file));
//                      long filesize=file.length();
//                      long countsize=0;
//
//                      //VARIABLES
//                      boolean sequenceMode=false;
//                      Sequence tmp = new Sequence();                        //Temp sequence
//                      StringBuilder tmpsequence=new StringBuilder();        //Temp sequence string
//                      String stri="";                                       //Temp read line
//
//                      //Read the file in a buffer an parse at the same time
//                     //Process :: We read like a fasta file
//                      Config.log("Reading fasta:"+filename+":");
//
//                      while (br.ready()) {
//                          stri=br.readLine();
//                          countsize+=stri.length();
//                          //Config.log(stri);
//                          if (sequenceMode&&(stri.equals("")||stri.startsWith(">"))) {
//                              tmp.setSequence(tmpsequence.toString());
//                              tmp.loadInfoFromName();
//                              tmp.setSequence_type(type);
//                              //Add sequence if not empty
//                              if (tmp.getSequence().length()>0) multi.getSequences().add(tmp);
//                              tmp=new Sequence();
//                              tmpsequence=new StringBuilder();
//                              sequenceMode=false;
//                          }
//                          if (sequenceMode) {
//                              tmpsequence.append(stri);
//                              //--5% countter
//                              int percent=(int) (countsize*100/filesize);
//                              if (percent%5==0) {
//                                  //Config.log("*");
//                                  setProgress(percent);
//                              }
//                          }
//                          if (stri.startsWith(">")) {
//                                //We have a fasta definition
//                                tmp.setName(stri.substring(1)); //remove >
//                                sequenceMode=true;
//                          }
//
//                      } //end while
//                      //Add last read
//                      if (sequenceMode) {
//                          tmp.setSequence(tmpsequence.toString());
//                          tmp.loadInfoFromName();
//                          if (tmp.getSequence().length()>0) multi.getSequences().add(tmp);
//                          tmp=new Sequence();
//                      }
//                      br.close();
//                   } catch(Exception e) {e.printStackTrace();Config.log("Error with "+filename);}
                    Config.log("done");
                    publish("Saving to database...");
                    multi.saveToDatabase();
                    setProgress((totalToLoad-toLoad.size())*100/totalToLoad);
                    if (multi.getId()==0) {
                        publish("Unable to load Alignment sequence(s) from "+filename);
                    } else {
                        publish("Successfully imported Alignment sequence(s) from "+filename);
                    }
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
                //Update UI by reloading alll sequences... (to be sure)...
                toolbox.reloadDatabaseTree();
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
                                //Handled in done() function in SwingWorker
                            }
                        }//End progress update
                    } //End populateNetworkPropertyChange
                });
        //Finally. Show a load dialog :: Warning Work-In-Progress
        loading=new InformationJDialog(this, false, loadSwingWorker2,"Loading files...");
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Loading "+files.length+" files...", "");
        loadSwingWorker2.execute();
    }
    
    /**
     * Simple thread to load file into the project
     */
    private void loadTextFiles(final File[] files, final String groupname, final String note) {
        final LinkedList<File> toLoad=new LinkedList<File>();
        final int totalToLoad=files.length;
        for(File f:files) toLoad.add(f);
        
        SwingWorker<Integer, Object> loadSwingWorker2=new SwingWorker<Integer, Object>() {
            String filename="";
            
            @Override
            protected Integer doInBackground() throws Exception {
                //We dont check for cancelled
                
                while (!isCancelled()&&toLoad.size()>0) {
                    File f=toLoad.pollFirst();
                    filename=f.getName();
                    publish("Loading "+filename);
                    Unknown multi=new Unknown(f.getAbsolutePath());
                    multi.setName(groupname+"_"+filename);
                    multi.setNote(note);
                    multi.saveToDatabase();
                    setProgress((totalToLoad-toLoad.size())*100/totalToLoad);
                    if (multi.getId()==0) {
                        publish("Unable to load text(s) from "+filename);
                    } else {
                        publish("Successfully imported text from "+filename);
                    }
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
                //Update UI by reloading alll sequences... (to be sure)...
                toolbox.reloadDatabaseTree();
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
                                //Handled in done() function in SwingWorker
                            }
                        }//End progress update
                    } //End populateNetworkPropertyChange
                });
        //Finally. Show a load dialog :: Warning Work-In-Progress
        loading=new InformationJDialog(this, false, loadSwingWorker2,"");
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Loading "+files.length+" files...", "");
        loadSwingWorker2.execute();
    }
    
    /**
     * Simple thread to load file into the project
     */
    private void loadMatrixFiles(final File[] files, final String groupname, final String note) {
        final LinkedList<File> toLoad=new LinkedList<File>();
        final int totalToLoad=files.length;
        for(File f:files) toLoad.add(f);
        
        SwingWorker<Integer, Object> loadSwingWorker2=new SwingWorker<Integer, Object>() {
            String filename="";
            
            @Override
            protected Integer doInBackground() throws Exception {
                //We dont check for cancelled
                
                while (!isCancelled()&&toLoad.size()>0) {
                    File f=toLoad.pollFirst();
                    filename=f.getName();
                    publish("Loading "+filename);
                    Matrix multi=new Matrix(f.getAbsolutePath());
                    multi.setName(groupname+"_"+filename);
                    multi.setNote(note);
                    multi.saveToDatabase();
                    setProgress((totalToLoad-toLoad.size())*100/totalToLoad);
                    if (multi.getId()==0) {
                        publish("Unable to load matrix from "+filename);
                    } else {
                        publish("Successfully imported matrix from "+filename);
                    }
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
                //Update UI by reloading alll sequences... (to be sure)...
                toolbox.reloadDatabaseTree();
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
                                //Handled in done() function in SwingWorker
                            }
                        }//End progress update
                    } //End populateNetworkPropertyChange
                });
        //Finally. Show a load dialog :: Warning Work-In-Progress
        loading=new InformationJDialog(this, false, loadSwingWorker2,"");
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Loading "+files.length+" files...", "");
        loadSwingWorker2.execute();
    }
    
    public void importSequenceFromDisk(){
        JFileChooser jf=new JFileChooser(config.getExplorerPath());
        jf.setMultiSelectionEnabled(true);
        jf.addChoosableFileFilter(new SequenceFilter());
        jf.setAcceptAllFileFilterUsed(false);
        int result=jf.showOpenDialog(this);
        
        //CAS 1: We have a file
        if (result==JFileChooser.APPROVE_OPTION) {
            String path=jf.getSelectedFile().getPath();
            config.setExplorerPath(path);
            File[] files=jf.getSelectedFiles();
            if (files.length>0) {
                String filename="";
                if (files.length==1) {
                    filename=files[0].getName()+"_"+this.hashCode();
                } else {
                    filename="MultipleSequences_"+this.hashCode();
                }
                StandardInputSequenceJDialog jd=new StandardInputSequenceJDialog(this, filename,"Import","MultipleSequences");
                jd.setVisible(true);
                if (jd.getStatus()==Config.status_done) {
                    loadSequenceFiles(files,jd.getCollectionName(),jd.getComments(), jd.getSequenceType());//load Files using a Swing worker
                }
            }
        } //End file
    }
    
    public void importAlignmentFromDisk(){
        JFileChooser jf=new JFileChooser(config.getExplorerPath());
        jf.setMultiSelectionEnabled(true);
        jf.addChoosableFileFilter(new SequenceFilter());
        jf.setAcceptAllFileFilterUsed(false);
        int result=jf.showOpenDialog(this);
        
        //CAS 1: We have a file
        if (result==JFileChooser.APPROVE_OPTION) {
            String path=jf.getSelectedFile().getPath();
            config.setExplorerPath(path);
            File[] files=jf.getSelectedFiles();
            if (files.length>0) {
                String filename="";
                if (files.length==1) {
                    filename=files[0].getName()+"_"+this.hashCode();
                } else {
                    filename="Alignment_"+this.hashCode();
                }
                StandardInputSequenceJDialog jd=new StandardInputSequenceJDialog(this, filename,"Import","Alignment");
                jd.setVisible(true);
                if (jd.getStatus()==Config.status_done) {
                    loadAlignmentFiles(files,jd.getCollectionName(),jd.getComments(), jd.getSequenceType());//load Files using a Swing worker
                }
            }
        } //End file
    }
    
    
    public void importTreeFromDisk() {
        JFileChooser jf=new JFileChooser(config.getExplorerPath());
        jf.setMultiSelectionEnabled(true);
        jf.addChoosableFileFilter(new TreeFilter());
        jf.setAcceptAllFileFilterUsed(false);
        int result=jf.showOpenDialog(this);
        //CAS 1: On reussi a choisir un fichier
        if (result==JFileChooser.APPROVE_OPTION) {
            String path=jf.getSelectedFile().getPath();
            config.setExplorerPath(path);
            File[] files=jf.getSelectedFiles();
            if (files.length>0) {
                String filename="";
                if (files.length==1) {
                    filename=files[0].getName()+"_"+this.hashCode();
                } else {
                    filename="Multiple_files_"+this.hashCode();
                }
                StandardInputSequenceJDialog jd=new StandardInputSequenceJDialog(this, filename,"Import Tree", "Tree");
                jd.setVisible(true);
                if (jd.getStatus()==Config.status_done) {
                    loadTreeFiles(files,jd.getCollectionName(),jd.getComments());//load Files using a Swing worker
                }
            }
        }
    }
    
    public void importTextFromDisk() {
        JFileChooser jf=new JFileChooser(config.getExplorerPath());
        jf.setMultiSelectionEnabled(true);
        int result=jf.showOpenDialog(this);
        //CAS 1: On reussi a choisir un fichier
        if (result==JFileChooser.APPROVE_OPTION) {
            String path=jf.getSelectedFile().getPath();
            config.setExplorerPath(path);
            File[] files=jf.getSelectedFiles();
            if (files.length>0) {
                String filename="";
                if (files.length==1) {
                    filename=files[0].getName()+"_"+this.hashCode();
                } else {
                    filename="Multiple_files_"+this.hashCode();
                }
                StandardInputSequenceJDialog jd=new StandardInputSequenceJDialog(this, filename,"Import Text", "Text");
                jd.setVisible(true);
                if (jd.getStatus()==Config.status_done) {
                    loadTextFiles(files,jd.getCollectionName(),jd.getComments());//load Files using a Swing worker
                }
            }
        }
    }
    
    public void importMatrixFromDisk() {
        JFileChooser jf=new JFileChooser(config.getExplorerPath());
        jf.setMultiSelectionEnabled(true);
        int result=jf.showOpenDialog(this);
        //CAS 1: On reussi a choisir un fichier
        if (result==JFileChooser.APPROVE_OPTION) {
            String path=jf.getSelectedFile().getPath();
            config.setExplorerPath(path);
            File[] files=jf.getSelectedFiles();
            if (files.length>0) {
                String filename="";
                if (files.length==1) {
                    filename=files[0].getName()+"_"+this.hashCode();
                } else {
                    filename="Multiple_files_"+this.hashCode();
                }
                StandardInputSequenceJDialog jd=new StandardInputSequenceJDialog(this, filename,"Import Matrix", "Matrix");
                jd.setVisible(true);
                if (jd.getStatus()==Config.status_done) {
                    loadMatrixFiles(files,jd.getCollectionName(),jd.getComments());//load Files using a Swing worker
                }
            }
        }
    }
    
    /**
     * This show the toolbox and workbox
     */
    public void showWorkflow_and_Toolbox() {
        ////////////////////////////////////////////////////////////////////////
        /// Fire action to open workflow
        Toolbox_jButton.doClick(1);
        Workflow_jButton.doClick(1);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AlignmentManager_jButton;
    private javax.swing.JMenuItem AlignmentManagerjMenuItem;
    private javax.swing.JMenuItem DatabaseExplorer_jMenuItem;
    private javax.swing.JButton GenerateReportjButton;
    private javax.swing.JMenuItem Generate_applications_report_jMenuItem;
    private javax.swing.JMenuItem Generate_applications_run_report_jMenuItem;
    private javax.swing.JMenuItem ImportAlignment_jMenuItem18;
    private javax.swing.JMenu ImportAlignmentjMenu1;
    private javax.swing.JMenuItem ImportMatrix_jMenuItem7;
    private javax.swing.JMenuItem ImportSequences_jMenuItem10;
    private javax.swing.JMenuItem ImportTextResultsjMenuItem;
    private javax.swing.JMenuItem ImportTrees_jMenuItem9;
    private javax.swing.JMenuItem ImportWorkflow_jMenuItem19;
    private javax.swing.JMenuItem NewProject_jMenuItem8;
    private javax.swing.JMenuItem NewWorkflow_jMenuItem11;
    private javax.swing.JMenuItem ObjectEditor_jMenuItem;
    private javax.swing.JMenuItem OpenWorkflow_jMenuItem;
    private javax.swing.JMenuItem ReportjMenuItem14;
    private javax.swing.JMenuItem ResetDevelopperStatejMenuItem5;
    private javax.swing.JMenuItem RunIterationjMenuItem;
    private javax.swing.JMenuItem RunOnCHP_jMenuItem;
    private javax.swing.JMenuItem RunSelected_jMenuItem7;
    private javax.swing.JMenuItem Run_jMenuItem;
    private javax.swing.JMenu Sample_jMenu;
    private javax.swing.JMenuItem SaveAs_jMenuItem;
    private javax.swing.JMenuItem SaveImagejMenuItem;
    private javax.swing.JMenuItem Save_jMenuItem;
    private javax.swing.JButton SequenceManager_jButton;
    private javax.swing.JMenuItem SequenceManagerjMenuItem;
    private javax.swing.JMenuItem Stop_jMenuItem;
    private javax.swing.JButton Toolbox_jButton;
    private javax.swing.JButton TreeManager_jButton;
    private javax.swing.JMenuItem TreeManagerjMenuItem;
    private javax.swing.JButton Workflow_jButton;
    private javax.swing.JButton jButton3;
    private javax.swing.JDesktopPane jDesktopPane;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
    
    public void windowOpened(WindowEvent e) {
        
    }
    
    public void windowClosing(WindowEvent e) {
        if (workbox.getCurrentWorkflows().isChanged()) {
            String msg="<html>Warning, workflow not saved.<br><br>Do you want to save the workflow before exiting?</html>";
            Object[] options = {"Yes","No","Cancel"};
            int n = JOptionPane.showOptionDialog(this,msg,"Save workflow before exit...",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options, options[2]);
            switch(n) {
                case 0:  workbox.saveWorkflowToDatabase("Save on "+Util.returnCurrentDateAndTime());
                //A pause so user can see the message before
                //the window actually closes.
                ActionListener task = new ActionListener() {
                    boolean alreadyDisposed = false;
                    public void actionPerformed(ActionEvent e) {
                        if (!alreadyDisposed) {
                            alreadyDisposed = true;
                            frame.dispose();
                            System.exit(0);
                        }
                    }
                };
                Timer timer = new Timer(500, task); //fire every half second
                timer.setInitialDelay(0);        //first delay 2 seconds
                timer.setRepeats(false);
                timer.start();
                break;
                case 1:
                    ActionListener task2 = new ActionListener() {
                        boolean alreadyDisposed = false;
                        public void actionPerformed(ActionEvent e) {
                            if (!alreadyDisposed) {
                                alreadyDisposed = true;
                                frame.dispose();
                                System.exit(0);
                            }
                        }
                    };
                    Timer timer2 = new Timer(500, task2); //fire every half second
                    timer2.setInitialDelay(0);        //first delay 2 seconds
                    timer2.setRepeats(false);
                    timer2.start();
                    break;
                default:
                    break;
            }
        } else {
            frame.dispose();
            System.exit(0);
        }
    }
    
    public void windowClosed(WindowEvent e) {
        
    }
    
    public void windowIconified(WindowEvent e) {
        
    }
    
    public void windowDeiconified(WindowEvent e) {
        
    }
    
    public void windowActivated(WindowEvent e) {
        
    }
    
    public void windowDeactivated(WindowEvent e) {
        
    }
    
    public void windowStateChanged(WindowEvent e) {
        
    }
    
}
