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


import Class.Classdata;
import workflows.workflow_properties_TableModel;
import workflows.workflow_properties;
import configuration.Config;
import configuration.ProgramsFilter;
import configuration.Util;
import configuration.WorkflowFilter;
import editors.HelpEditor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.text.Position.Bias;
import results.report;
import tools.Toolbox;
import workflows.InputOutput;
import workflows.InputOutputCellEditor;
import workflows.InputOutputTableModel;
import workflows.InputOutput_cellRenderer;
import workflows.armadillo_workflow;
import workflows.armadillo_workflow.workflow_object;
import workflows.workflow_properties_cellRenderer;
import workflows.workflow_properties_dictionnary;

/**
 * Editor of object properties for the Object Editor
 * @author Etienne Lord
 * @since July 2009
 */
public class propertiesEditorSmallWorkflowJDialog extends javax.swing.JDialog {

    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES

    Config config=new Config();
    ConnectorInfoBox connectorinfobox;
    workflow_properties_dictionnary dict=new workflow_properties_dictionnary();
    String selected="";             // Selected properties
    Frame frame;
    workflow_properties properties;
    public static armadillo_workflow workflow=null;
    LinkedList<String>ListType=new LinkedList<String>(); //Liste des type pour Tree Toolbox

    ////////////////////////////////////////////////////////////////////////////
    /// CONSTANT

    public final String defaultNameString=" Name";
    public final String defaultDescriptionString=" Description";
    public final String TooltipDescriptionString=" Tooltip";
    public final String WebsiteString=" Website";
    public final String PublicationsString=" Publication(s)";

    /** Creates new form propertiesJDialog */
    public propertiesEditorSmallWorkflowJDialog(java.awt.Frame parent,workflow_properties properties, String title) {
        super(parent, true);
        //--Set variables and init
        frame=parent;
        this.properties=properties;
        connectorinfobox=new ConnectorInfoBox(parent); //--Used to display Connector info
        //--Initialize component
        if (workflow==null) {
            workflow=new armadillo_workflow();
            workflow.setSmallEditorMode(true);
            workflow.init();
        }
        
        
        //--This is called after because we need to add the editor...
        initComponents();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                close();
            }});
        
        
        //--Add the workflow to the jPanel2
        jPanel2.add(workflow);
        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 635, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 146, Short.MAX_VALUE));
        
        //--TO DO - Find why we can't have the menu at first glance...
        

        Message("Developper editor - 2009-2011","");
        setIconImage(Config.image); //--Set the icon in the top corner
        setComboBoxData();     //--Set all the interface type and current data
        setPropertiesList();   //--Set a list of properties files int testJPanel
        setPropertiesTable();  //--Set current properties in JTable 
        
        // Set position windows position relative to screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = getSize();
        setLocation((screenSize.width-d.width)/2,
					(screenSize.height-d.height)/2);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        WebsitejTextField3 = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        Publication_jTextArea3 = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        InputOutputjTable = new javax.swing.JTable();
        jPanel12 = new javax.swing.JPanel();
        ConnectorjComboBox = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        OutputjCheckBox = new javax.swing.JCheckBox();
        ConditionnaljCheckBox = new javax.swing.JCheckBox();
        ConnectorNamejCheckBox1 = new javax.swing.JCheckBox();
        ConnectorNamejTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        ObjectTypejComboBox = new javax.swing.JComboBox();
        ColorModejComboBox = new javax.swing.JComboBox();
        IfSidejCheckBox = new javax.swing.JCheckBox();
        InputNbjComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        TypejComboBox = new javax.swing.JComboBox();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanel10 = new javax.swing.JPanel();
        ProgramjTextField = new javax.swing.JTextField();
        LocateWindowsjButton = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        HelpSuppljTextArea3 = new javax.swing.JTextArea();
        jLabel19 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        CommandjTextField = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        WebServcejCheckBox = new javax.swing.JCheckBox();
        NoThreadjCheckBox = new javax.swing.JCheckBox();
        DebugjCheckBox = new javax.swing.JCheckBox();
        NormalExitValuejTextField = new javax.swing.JTextField();
        VerifyExitValuejCheckBox = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        LinuxProgramjTextField = new javax.swing.JTextField();
        LocateLinuxjButton = new javax.swing.JButton();
        ArmadilloApplicationsjCheckBox = new javax.swing.JCheckBox();
        JavajCheckBox2 = new javax.swing.JCheckBox();
        MACOSXProgramjTextField = new javax.swing.JTextField();
        LocateMacOXjButton = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        version_windows = new javax.swing.JTextField();
        version_linux = new javax.swing.JTextField();
        version_macosx = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        SamplejTextField1 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        LocateSamplejButton1 = new javax.swing.JButton();
        KeywordjTextField2 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        RuntimeMacOSXjComboBox1 = new javax.swing.JComboBox();
        SpecificjCheckBox1 = new javax.swing.JCheckBox();
        SpecificPathjTextField1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        ClassListjComboBox = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        EditorClassNamejComboBox = new javax.swing.JComboBox();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        AddjButton = new javax.swing.JButton();
        DeletejButton = new javax.swing.JButton();
        TestjPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel18 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        InsertDefaultPropertiesjMenu1 = new javax.swing.JMenu();
        SavejMenuItem1 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        UpdateWorkflowjMenuItem1 = new javax.swing.JMenuItem();
        InsertPropertiesjMenuItem2 = new javax.swing.JMenuItem();
        ReloadPropertiesjMenuItem1 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        ClosejMenuItem2 = new javax.swing.JMenuItem();
        EditjMenu2 = new javax.swing.JMenu();
        ShowHideNumberjMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        GenerateHTMLjMenuItem3 = new javax.swing.JMenuItem();
        SaveImagejMenuItem4 = new javax.swing.JMenuItem();

        jLabel2.setText("jLabel2");

        setTitle("Properties");
        setBackground(new java.awt.Color(102, 204, 255));
        setResizable(false);

        jTabbedPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jTabbedPane1ComponentShown(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jTextField1.setForeground(java.awt.Color.lightGray);
        jTextField1.setText(" Name");
        jTextField1.setToolTipText("Application displayed name.");
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });

        jTextField2.setForeground(java.awt.Color.lightGray);
        jTextField2.setText(" Tooltip");
        jTextField2.setToolTipText("Tooltip displayed when mouse is over.");
        jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField2FocusLost(evt);
            }
        });
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField2KeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField2KeyTyped(evt);
            }
        });

        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Tahoma", 0, 11));
        jTextArea2.setForeground(new java.awt.Color(204, 204, 204));
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setText(" Description");
        jTextArea2.setToolTipText("Description of the application (displayed in the editor, etc.)");
        jTextArea2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextArea2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextArea2FocusLost(evt);
            }
        });
        jTextArea2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextArea2KeyTyped(evt);
            }
        });
        jScrollPane4.setViewportView(jTextArea2);

        WebsitejTextField3.setForeground(java.awt.Color.lightGray);
        WebsitejTextField3.setText(" Website");
        WebsitejTextField3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                WebsitejTextField3FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                WebsitejTextField3FocusLost(evt);
            }
        });
        WebsitejTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                WebsitejTextField3KeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                WebsitejTextField3KeyTyped(evt);
            }
        });

        Publication_jTextArea3.setColumns(20);
        Publication_jTextArea3.setFont(new java.awt.Font("Tahoma", 0, 11));
        Publication_jTextArea3.setForeground(new java.awt.Color(204, 204, 204));
        Publication_jTextArea3.setLineWrap(true);
        Publication_jTextArea3.setRows(5);
        Publication_jTextArea3.setText(" Publication(s)");
        Publication_jTextArea3.setToolTipText("Publication(s) associated with this application.");
        Publication_jTextArea3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                Publication_jTextArea3FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                Publication_jTextArea3FocusLost(evt);
            }
        });
        Publication_jTextArea3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                Publication_jTextArea3KeyTyped(evt);
            }
        });
        jScrollPane7.setViewportView(Publication_jTextArea3);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
            .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
            .addComponent(WebsitejTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(WebsitejTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Input Output"));

        InputOutputjTable.setModel(new InputOutputTableModel());
        InputOutputjTable.setAutoCreateRowSorter(true);
        InputOutputCellEditor editor=new InputOutputCellEditor();
        InputOutput_cellRenderer renderer=new InputOutput_cellRenderer();
        TableColumn column = null;
        for (int i = 0; i < InputOutputjTable.getColumnCount(); i++) {
            column = InputOutputjTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(50);
            }
            if (i == 1) {
                column.setCellEditor(new DefaultCellEditor(editor));
            }
            column.setCellRenderer(renderer);
        }
        InputOutputjTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                InputOutputjTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(InputOutputjTable);
        InputOutputjTable.getColumnModel().getColumn(0).setResizable(false);
        InputOutputjTable.getColumnModel().getColumn(1).setResizable(false);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        ConnectorjComboBox.setModel(new DefaultComboBoxModel());
        ConnectorjComboBox.setToolTipText("Manual configuration of the connector (port input) of the application.");
        ConnectorjComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConnectorjComboBoxActionPerformed(evt);
            }
        });

        jLabel8.setText("Connector");

        OutputjCheckBox.setText("Output");
        OutputjCheckBox.setToolTipText("Is it an output port? (default: the port is an input)");
        OutputjCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OutputjCheckBoxActionPerformed(evt);
            }
        });

        ConditionnaljCheckBox.setText("Conditionnal (workflow path)");
        ConditionnaljCheckBox.setToolTipText("Is this a data-flow port (deprecated).");
        ConditionnaljCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConditionnaljCheckBoxActionPerformed(evt);
            }
        });

        ConnectorNamejCheckBox1.setText("Name");
        ConnectorNamejCheckBox1.setToolTipText("Specific name of this connector (override defaut port name e.g. MultipleSequences...)");
        ConnectorNamejCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConnectorNamejCheckBox1ActionPerformed(evt);
            }
        });

        ConnectorNamejTextField.setEnabled(false);
        ConnectorNamejTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ConnectorNamejTextFieldFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ConnectorjComboBox, 0, 244, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(OutputjCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ConditionnaljCheckBox))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(ConnectorNamejCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ConnectorNamejTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(ConditionnaljCheckBox)
                    .addComponent(OutputjCheckBox)
                    .addComponent(ConnectorjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ConnectorNamejCheckBox1)
                    .addComponent(ConnectorNamejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jLabel1.setText("ObjectType");

        ObjectTypejComboBox.setModel(new DefaultComboBoxModel());
        ObjectTypejComboBox.setToolTipText("Specify to Armadillo what this application internal mode is.");
        ObjectTypejComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ObjectTypejComboBoxItemStateChanged(evt);
            }
        });
        ObjectTypejComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ObjectTypejComboBoxActionPerformed(evt);
            }
        });

        ColorModejComboBox.setModel(new DefaultComboBoxModel());
        ColorModejComboBox.setToolTipText("The displayed color in the workflow.");
        ColorModejComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox4ItemStateChanged(evt);
            }
        });
        ColorModejComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ColorModejComboBoxActionPerformed(evt);
            }
        });

        IfSidejCheckBox.setText("modeSide (If)");
        IfSidejCheckBox.setToolTipText("Display the If on the side or not.");
        IfSidejCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                IfSidejCheckBoxItemStateChanged(evt);
            }
        });

        InputNbjComboBox.setModel(new DefaultComboBoxModel());
        InputNbjComboBox.setToolTipText("The number of input port in the workflow.");
        InputNbjComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                InputNbjComboBoxItemStateChanged(evt);
            }
        });

        jLabel3.setText("# Input");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(InputNbjComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ObjectTypejComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ColorModejComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(IfSidejCheckBox))
                .addContainerGap(205, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(ObjectTypejComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ColorModejComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(InputNbjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(IfSidejCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Object Properties", jPanel9);

        jLabel7.setText("Application Category");
        jLabel7.setToolTipText("Specify were to put it in the Application category");

        TypejComboBox.setEditable(true);
        TypejComboBox.setModel(new DefaultComboBoxModel());
        TypejComboBox.setToolTipText("Specify were to put it in the Application category");
        TypejComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TypejComboBoxItemStateChanged(evt);
            }
        });
        TypejComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TypejComboBoxActionPerformed(evt);
            }
        });

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Program configuration"));

        ProgramjTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ProgramjTextFieldFocusLost(evt);
            }
        });

        LocateWindowsjButton.setText("Locate");
        LocateWindowsjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LocateWindowsjButtonActionPerformed(evt);
            }
        });

        jLabel11.setText("Windows");

        jLabel10.setForeground(new java.awt.Color(255, 51, 51));
        jLabel10.setText("Executable: Must be located in the armadillo/Executable path");

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Help"));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextArea1FocusLost(evt);
            }
        });
        jScrollPane6.setViewportView(jTextArea1);

        jLabel12.setText("Help text or URL (file or starting with http://)");

        HelpSuppljTextArea3.setColumns(20);
        HelpSuppljTextArea3.setRows(5);
        HelpSuppljTextArea3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                HelpSuppljTextArea3FocusLost(evt);
            }
        });
        jScrollPane8.setViewportView(HelpSuppljTextArea3);

        jLabel19.setText("Help supplementary (will be put in the html help - can include html code)");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addContainerGap())
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel19)
                .addContainerGap())
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
        );

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Quick Configuration"));

        jLabel9.setText("Normal Command-line");

        CommandjTextField.setEnabled(false);
        CommandjTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CommandjTextFieldFocusLost(evt);
            }
        });
        CommandjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                CommandjTextFieldKeyTyped(evt);
            }
        });

        jButton5.setText("Test Run");
        jButton5.setEnabled(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addContainerGap(471, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addComponent(CommandjTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton5))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CommandjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5))
                .addGap(36, 36, 36))
        );

        WebServcejCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        WebServcejCheckBox.setText("WebService");
        WebServcejCheckBox.setToolTipText("Is it a WebServices (or an API to it)");
        WebServcejCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                WebServcejCheckBoxItemStateChanged(evt);
            }
        });
        WebServcejCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WebServcejCheckBoxActionPerformed(evt);
            }
        });

        NoThreadjCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        NoThreadjCheckBox.setText("Thread wait");
        NoThreadjCheckBox.setToolTipText("Specific to some software.");
        NoThreadjCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                NoThreadjCheckBoxItemStateChanged(evt);
            }
        });
        NoThreadjCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NoThreadjCheckBoxActionPerformed(evt);
            }
        });

        DebugjCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        DebugjCheckBox.setText("Debug");
        DebugjCheckBox.setToolTipText("Specify, if needed, a better print out of informations.");
        DebugjCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DebugjCheckBoxItemStateChanged(evt);
            }
        });
        DebugjCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DebugjCheckBoxActionPerformed(evt);
            }
        });

        NormalExitValuejTextField.setText("0");
        NormalExitValuejTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                NormalExitValuejTextFieldFocusLost(evt);
            }
        });

        VerifyExitValuejCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        VerifyExitValuejCheckBox.setText("Exit Value");
        VerifyExitValuejCheckBox.setToolTipText("Verity the exit value for error code.");
        VerifyExitValuejCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                VerifyExitValuejCheckBoxItemStateChanged(evt);
            }
        });
        VerifyExitValuejCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VerifyExitValuejCheckBoxActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Lucida Grande", 2, 13));
        jLabel13.setText("Linux");

        LinuxProgramjTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                LinuxProgramjTextFieldFocusLost(evt);
            }
        });

        LocateLinuxjButton.setText("Locate");
        LocateLinuxjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LocateLinuxjButtonActionPerformed(evt);
            }
        });

        ArmadilloApplicationsjCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        ArmadilloApplicationsjCheckBox.setText(" Internal Application");
        ArmadilloApplicationsjCheckBox.setToolTipText("Specify that the application don't have a software associated to it.");
        ArmadilloApplicationsjCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ArmadilloApplicationsjCheckBoxItemStateChanged(evt);
            }
        });

        JavajCheckBox2.setBackground(new java.awt.Color(255, 255, 255));
        JavajCheckBox2.setText("Java");
        JavajCheckBox2.setToolTipText("Specify a Java source code.");
        JavajCheckBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                JavajCheckBox2ItemStateChanged(evt);
            }
        });
        JavajCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JavajCheckBox2ActionPerformed(evt);
            }
        });

        MACOSXProgramjTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                MACOSXProgramjTextFieldFocusLost(evt);
            }
        });

        LocateMacOXjButton.setText("Locate");
        LocateMacOXjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LocateMacOXjButtonActionPerformed(evt);
            }
        });

        jLabel14.setText("Mac OSX");

        version_windows.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                version_windowsFocusLost(evt);
            }
        });

        version_linux.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                version_linuxFocusLost(evt);
            }
        });

        version_macosx.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                version_macosxFocusLost(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("version");

        SamplejTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                SamplejTextField1FocusLost(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel16.setText("Sample workflows");
        jLabel16.setToolTipText("Sample workflow (must be in the data/help/ folder)");

        LocateSamplejButton1.setText("Locate");
        LocateSamplejButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LocateSamplejButton1ActionPerformed(evt);
            }
        });

        KeywordjTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                KeywordjTextField2FocusLost(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel17.setText("Keywords");
        jLabel17.setToolTipText("Sample workflow (must be in the data/help/ folder)");

        RuntimeMacOSXjComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "default", "runtime", "bash (.sh)" }));
        RuntimeMacOSXjComboBox1.setToolTipText("<html>This is the execution mode of the Mac application.<br>\n<b>Default</b> like in windows (Process Builder)<br>\n<b>runtime</b> using the runtime java command<br>\n<b>bash</b> running from a new bash command...<br>\n</html>\n");
        RuntimeMacOSXjComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RuntimeMacOSXjComboBox1ActionPerformed(evt);
            }
        });

        SpecificjCheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        SpecificjCheckBox1.setText("Specific running path");
        SpecificjCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SpecificjCheckBox1ActionPerformed(evt);
            }
        });

        SpecificPathjTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                SpecificPathjTextField1FocusLost(evt);
            }
        });

        jLabel5.setText("Mac OSX Running mode");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel17))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(KeywordjTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addComponent(SamplejTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(LocateSamplejButton1))))))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(NoThreadjCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ArmadilloApplicationsjCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(WebServcejCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(DebugjCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(JavajCheckBox2))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RuntimeMacOSXjComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(VerifyExitValuejCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(NormalExitValuejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(SpecificjCheckBox1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(SpecificPathjTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel11)
                                            .addComponent(jLabel13)
                                            .addComponent(jLabel14))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(LinuxProgramjTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                                            .addComponent(ProgramjTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                                            .addComponent(MACOSXProgramjTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)))
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(version_windows, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                                    .addComponent(version_linux, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                                    .addComponent(version_macosx, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(LocateWindowsjButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(LocateLinuxjButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(LocateMacOXjButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(49, 49, 49)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(ProgramjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LocateWindowsjButton)
                    .addComponent(version_windows, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(LinuxProgramjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LocateLinuxjButton)
                    .addComponent(version_linux, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(LocateMacOXjButton)
                    .addComponent(MACOSXProgramjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(version_macosx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RuntimeMacOSXjComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(VerifyExitValuejCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NormalExitValuejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SpecificjCheckBox1)
                    .addComponent(SpecificPathjTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NoThreadjCheckBox)
                    .addComponent(ArmadilloApplicationsjCheckBox)
                    .addComponent(WebServcejCheckBox)
                    .addComponent(DebugjCheckBox)
                    .addComponent(JavajCheckBox2))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(KeywordjTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(SamplejTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LocateSamplejButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jScrollPane5.setViewportView(jPanel10);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        ClassListjComboBox.setEditable(true);
        ClassListjComboBox.setModel(new DefaultComboBoxModel());
        ClassListjComboBox.setToolTipText("Running class to execute in the workflow.");
        ClassListjComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ClassListjComboBoxItemStateChanged(evt);
            }
        });

        jLabel4.setText("Running Class Name");
        jLabel4.setToolTipText("Running class to execute in the workflow.");

        jLabel6.setText("Editor Class Name");
        jLabel6.setToolTipText("Specify the editor class to display in the workflow.");

        EditorClassNamejComboBox.setEditable(true);
        EditorClassNamejComboBox.setModel(new DefaultComboBoxModel());
        EditorClassNamejComboBox.setToolTipText("Specify the editor class to display in the workflow.");
        EditorClassNamejComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                EditorClassNamejComboBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(14, 14, 14))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ClassListjComboBox, 0, 463, Short.MAX_VALUE)
                    .addComponent(EditorClassNamejComboBox, 0, 463, Short.MAX_VALUE)))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ClassListjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EditorClassNamejComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TypejComboBox, 0, 480, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(TypejComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 105, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(46, 46, 46)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(376, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Executable Properties", jPanel6);

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new workflow_properties_TableModel());
        for (int i=0; i<jTable1.getColumnCount();i++) {
            TableColumn tm=jTable1.getColumnModel().getColumn(i);
            tm.setCellRenderer(new workflow_properties_cellRenderer());
        }
        jScrollPane1.setViewportView(jTable1);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        AddjButton.setText("Add Key");
        AddjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddjButtonActionPerformed(evt);
            }
        });

        DeletejButton.setText("Delete Key");
        DeletejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeletejButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(AddjButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DeletejButton)
                .addContainerGap(420, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(AddjButton)
                .addComponent(DeletejButton))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Properties List", jPanel7);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jList1FocusLost(evt);
            }
        });
        jScrollPane3.setViewportView(jList1);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
        );

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel18.setText("Select similar software or function... ");

        javax.swing.GroupLayout TestjPanelLayout = new javax.swing.GroupLayout(TestjPanel);
        TestjPanel.setLayout(TestjPanelLayout);
        TestjPanelLayout.setHorizontalGroup(
            TestjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TestjPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TestjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18))
                .addContainerGap())
        );
        TestjPanelLayout.setVerticalGroup(
            TestjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TestjPanelLayout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Similar", TestjPanel);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 635, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 146, Short.MAX_VALUE)
        );

        InsertDefaultPropertiesjMenu1.setText("File");

        SavejMenuItem1.setText("Save");
        SavejMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SavejMenuItem1ActionPerformed(evt);
            }
        });
        InsertDefaultPropertiesjMenu1.add(SavejMenuItem1);
        InsertDefaultPropertiesjMenu1.add(jSeparator2);

        UpdateWorkflowjMenuItem1.setText("Update main workflow");
        UpdateWorkflowjMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdateWorkflowjMenuItem1ActionPerformed(evt);
            }
        });
        InsertDefaultPropertiesjMenu1.add(UpdateWorkflowjMenuItem1);

        InsertPropertiesjMenuItem2.setText("Insert default properties");
        InsertPropertiesjMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InsertPropertiesjMenuItem2ActionPerformed(evt);
            }
        });
        InsertDefaultPropertiesjMenu1.add(InsertPropertiesjMenuItem2);

        ReloadPropertiesjMenuItem1.setText("Reload properties");
        ReloadPropertiesjMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReloadPropertiesjMenuItem1ActionPerformed(evt);
            }
        });
        InsertDefaultPropertiesjMenu1.add(ReloadPropertiesjMenuItem1);
        InsertDefaultPropertiesjMenu1.add(jSeparator3);

        ClosejMenuItem2.setText("Close");
        ClosejMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClosejMenuItem2ActionPerformed(evt);
            }
        });
        InsertDefaultPropertiesjMenu1.add(ClosejMenuItem2);

        jMenuBar1.add(InsertDefaultPropertiesjMenu1);

        EditjMenu2.setText("Edit");

        ShowHideNumberjMenuItem.setText("Show/Hide number");
        ShowHideNumberjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowHideNumberjMenuItemActionPerformed(evt);
            }
        });
        EditjMenu2.add(ShowHideNumberjMenuItem);

        jMenuBar1.add(EditjMenu2);

        jMenu2.setText("Tools");

        GenerateHTMLjMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        GenerateHTMLjMenuItem3.setText("Generate HTML documentation");
        GenerateHTMLjMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GenerateHTMLjMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(GenerateHTMLjMenuItem3);

        SaveImagejMenuItem4.setText("Save object image");
        SaveImagejMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveImagejMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(SaveImagejMenuItem4);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void AddjButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddjButtonActionPerformed
       AddKeyJDialog ak=new AddKeyJDialog(frame, properties);
       setPropertiesTable();
       
}//GEN-LAST:event_AddjButtonActionPerformed

    private void DeletejButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeletejButtonActionPerformed
        int selectedRow=this.jTable1.getSelectedRow();
        if (selectedRow!=-1) {
            String keyToDelete=(String)this.jTable1.getValueAt(selectedRow, 0);
            properties.remove(keyToDelete);
            setPropertiesTable();
            
        }
    }//GEN-LAST:event_DeletejButtonActionPerformed

    private void jComboBox4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox4ItemStateChanged
        String color=(String)ColorModejComboBox.getSelectedItem();
        properties.put("defaultColor", color);
        if (color.equals("BLACK")) {
          MessageError("Warning. Black color is normally only use for selection.","");  
        } Message("defaultColor "+color+" set for this object.","");

        workflow.updateWorkflow(properties);
}//GEN-LAST:event_jComboBox4ItemStateChanged

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        if (jTextField1.getText().equals(defaultNameString)) {
            jTextField1.setText("");
            jTextField1.setForeground(Color.BLACK);
        } // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1FocusGained

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
       if (jTextField1.getText().trim().equals("")) {
            this.jTextField1.setText(defaultNameString);
            this.jTextField1.setForeground(Color.LIGHT_GRAY);
        } else {
            String name=this.jTextField1.getText();
            properties.put("Name", name.trim());
            workflow.updateWorkflow(properties);
        }
    }//GEN-LAST:event_jTextField1FocusLost

    private void ColorModejComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ColorModejComboBoxActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_ColorModejComboBoxActionPerformed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        

    }//GEN-LAST:event_jTextField1KeyPressed

    private void ObjectTypejComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ObjectTypejComboBoxActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_ObjectTypejComboBoxActionPerformed

    private void ObjectTypejComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ObjectTypejComboBoxItemStateChanged
        String ObjectType=(String)this.ObjectTypejComboBox.getSelectedItem();
        properties.put("ObjectType", ObjectType);
        //--If not Program, bloc some input
        if (ObjectType.equals("Program")||ObjectType.equals("ProgramPhylip")) {
            this.InputNbjComboBox.setEnabled(true);
        } else this.InputNbjComboBox.setEnabled(false);
        if (ObjectType.equals("If")) {
            this.IfSidejCheckBox.setEnabled(true);
        } else IfSidejCheckBox.setEnabled(false);
        workflow.updateWorkflow(properties);
    }//GEN-LAST:event_ObjectTypejComboBoxItemStateChanged

    private void ClassListjComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ClassListjComboBoxItemStateChanged
        String ClassName=(String)this.ClassListjComboBox.getSelectedItem();
        properties.put("ClassName", ClassName);
        workflow.updateWorkflow(properties);
    }//GEN-LAST:event_ClassListjComboBoxItemStateChanged

    private void InputNbjComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_InputNbjComboBoxItemStateChanged
        Integer nbinput=this.InputNbjComboBox.getSelectedIndex();
        properties.put("nbInput", nbinput.toString());
        workflow.updateWorkflow(properties);
    }//GEN-LAST:event_InputNbjComboBoxItemStateChanged

    private void OutputjCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OutputjCheckBoxActionPerformed
        boolean b=this.OutputjCheckBox.isSelected();
        int index=this.ConnectorjComboBox.getSelectedIndex();
        String C="Connector"+index+"Output";
        if (b) {
           properties.put(C, "True");
       } else {
           properties.remove(C);
       }
       workflow.updateWorkflow(properties);
}//GEN-LAST:event_OutputjCheckBoxActionPerformed

    private void ConditionnaljCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConditionnaljCheckBoxActionPerformed
        boolean b=this.ConditionnaljCheckBox.isSelected();
        int index=this.ConnectorjComboBox.getSelectedIndex();
        String C="Connector"+index+"Conditional";
        if (b) {
           properties.put(C, "True");
       } else {
           properties.remove(C);
       }
       workflow.updateWorkflow(properties);
    }//GEN-LAST:event_ConditionnaljCheckBoxActionPerformed

    /**
     * Set the properties of the Connector if found
     * @param evt
     */
    private void ConnectorjComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConnectorjComboBoxActionPerformed
       int index=this.ConnectorjComboBox.getSelectedIndex();
       String keyOutput="Connector"+index+"Output";
       String keyConditional="Connector"+index+"Conditional";
       String keyConnectorName="Connector"+index;
       this.OutputjCheckBox.setSelected(properties.isSet(keyOutput));
       this.ConditionnaljCheckBox.setSelected(properties.isSet(keyConditional));
       this.ConnectorNamejCheckBox1.setSelected(properties.isSet(keyConnectorName));
       this.ConnectorNamejTextField.setEnabled(properties.isSet(keyConnectorName));
       if (properties.isSet(keyConnectorName)) {
                this.ConnectorNamejTextField.setText(properties.get(keyConnectorName));
            } else {
                this.ConnectorNamejTextField.setText("");
       }
    }//GEN-LAST:event_ConnectorjComboBoxActionPerformed

    private void IfSidejCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_IfSidejCheckBoxItemStateChanged
        boolean b=this.IfSidejCheckBox.isSelected();
        if (b) {
            properties.put("modeSide", "True");
        } else {
            properties.remove("modeSide");
        }
        workflow.updateWorkflow(properties);
    }//GEN-LAST:event_IfSidejCheckBoxItemStateChanged

    private void jTabbedPane1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentShown
      
    }//GEN-LAST:event_jTabbedPane1ComponentShown

    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyTyped
        String name=this.jTextField1.getText();
         properties.put("Name", name.trim());
         workflow.updateWorkflow(properties);
    }//GEN-LAST:event_jTextField1KeyTyped

    private void InputOutputjTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InputOutputjTableMouseClicked
       updatePropertiesTable();
       workflow.updateWorkflow(properties);
    }//GEN-LAST:event_InputOutputjTableMouseClicked

    private void EditorClassNamejComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_EditorClassNamejComboBoxItemStateChanged
        String editorClass=(String)this.EditorClassNamejComboBox.getSelectedItem();
        properties.put("EditorClassName", editorClass);
        workflow.updateWorkflow(properties);
}//GEN-LAST:event_EditorClassNamejComboBoxItemStateChanged

    private void jTextField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusGained
        if (jTextField2.getText().equals(TooltipDescriptionString)) {
            jTextField2.setText("");
            jTextField2.setForeground(Color.BLACK);
        } // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2FocusGained

    private void jTextField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusLost
         if (jTextField2.getText().trim().equals("")) {
            this.jTextField2.setText(TooltipDescriptionString);
            this.jTextField2.setForeground(Color.LIGHT_GRAY);
        } else {
            String tooltip=this.jTextField2.getText();
            properties.put("Tooltip", tooltip.trim());
            workflow.updateWorkflow(properties);
        }
    }//GEN-LAST:event_jTextField2FocusLost

    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2KeyPressed

    private void jTextField2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyTyped
        String tooltip=this.jTextField2.getText();
         properties.put("Tooltip", tooltip.trim());
         workflow.updateWorkflow(properties);
    }//GEN-LAST:event_jTextField2KeyTyped

    private void TypejComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TypejComboBoxItemStateChanged
       String Type=(String)this.TypejComboBox.getSelectedItem();
       properties.put("Type", Type);
        
}//GEN-LAST:event_TypejComboBoxItemStateChanged

    private void TypejComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TypejComboBoxActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_TypejComboBoxActionPerformed

    private void CommandjTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CommandjTextFieldFocusLost
        properties.setCommandline(this.CommandjTextField.getText());
}//GEN-LAST:event_CommandjTextFieldFocusLost

    private void LocateWindowsjButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LocateWindowsjButtonActionPerformed
        JFileChooser jf=new JFileChooser("Executable"+File.separator);
        jf.addChoosableFileFilter(new ProgramsFilter());
        jf.setAcceptAllFileFilterUsed(false);
        jf.setName("Locate program Executable ...");
        int result=jf.showOpenDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            String filename=jf.getSelectedFile().getName();
            String path=jf.getSelectedFile().getAbsolutePath();
            if (path.indexOf("executable")>-1) {
                path=path.substring(path.indexOf("executable"),path.length());
            }
            //runprogram.setPath(path);
            properties.setExecutable(path);
            this.ProgramjTextField.setText(path);
           
        }
}//GEN-LAST:event_LocateWindowsjButtonActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
//        RunProgram runprogram=new RunProgram(properties);
//        runprogram.getProperties().put("debug",true);
//        runprogram.getProperties().put("NoThread",true);
//        runprogram.execute();
//        StringBuilder st=new StringBuilder();
//        for (String s:runprogram.getOutputTXT()) Config.log(s);
        //this.OutputjTextArea.setText("Finished running "+runprogram.getExecutable()+" with status : "+runprogram.getProperties().get("Status")+"\n "+runprogram.getProperties().get("StatusString"));
        //       runprogram.getOutput().clear();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void CommandjTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CommandjTextFieldKeyTyped

    }//GEN-LAST:event_CommandjTextFieldKeyTyped

    private void jTextArea1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextArea1FocusLost
       properties.put("help", this.jTextArea1.getText());
    }//GEN-LAST:event_jTextArea1FocusLost

    private void ProgramjTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ProgramjTextFieldFocusLost
        properties.put("Executable", this.ProgramjTextField.getText());
        workflow.updateWorkflow(properties);
    }//GEN-LAST:event_ProgramjTextFieldFocusLost

    private void WebServcejCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_WebServcejCheckBoxItemStateChanged
        if (WebServcejCheckBox.isSelected()) {
            properties.put("WebServices", true);
        } else {
            properties.remove("WebServices");
        }
        //--Update View
        this.ProgramjTextField.setEnabled(!properties.getBoolean("WebServices"));
        this.LocateWindowsjButton.setEnabled(!properties.getBoolean("WebServices"));
        this.LocateLinuxjButton.setEnabled(!properties.getBoolean("WebServices"));
        this.LocateMacOXjButton.setEnabled(!properties.getBoolean("WebServices"));
    }//GEN-LAST:event_WebServcejCheckBoxItemStateChanged

    private void NoThreadjCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_NoThreadjCheckBoxItemStateChanged
          properties.put("NoThread",NoThreadjCheckBox.isSelected());
    }//GEN-LAST:event_NoThreadjCheckBoxItemStateChanged

    private void NoThreadjCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoThreadjCheckBoxActionPerformed
        properties.put("NoThread",NoThreadjCheckBox.isSelected());
    }//GEN-LAST:event_NoThreadjCheckBoxActionPerformed

    private void DebugjCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DebugjCheckBoxItemStateChanged
        properties.put("debug",DebugjCheckBox.isSelected());
    }//GEN-LAST:event_DebugjCheckBoxItemStateChanged

    private void DebugjCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DebugjCheckBoxActionPerformed
        properties.put("debug",DebugjCheckBox.isSelected());
    }//GEN-LAST:event_DebugjCheckBoxActionPerformed

    private void NormalExitValuejTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_NormalExitValuejTextFieldFocusLost
        int value=0;
        try {
            value=Integer.valueOf(this.NormalExitValuejTextField.getText());
        } catch(Exception e) {value=0;}
        properties.put("NormalExitValue", value);
    }//GEN-LAST:event_NormalExitValuejTextFieldFocusLost

    private void VerifyExitValuejCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VerifyExitValuejCheckBoxActionPerformed
       properties.put("VerifyExitValue",VerifyExitValuejCheckBox.isSelected());
        //--Update View
       this.NormalExitValuejTextField.setEnabled(properties.getBoolean("VerifyExitValue"));
    }//GEN-LAST:event_VerifyExitValuejCheckBoxActionPerformed

    private void VerifyExitValuejCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_VerifyExitValuejCheckBoxItemStateChanged
      properties.put("VerifyExitValue",VerifyExitValuejCheckBox.isSelected());
        //--Update View
       this.NormalExitValuejTextField.setEnabled(properties.getBoolean("VerifyExitValue"));
    }//GEN-LAST:event_VerifyExitValuejCheckBoxItemStateChanged

    private void ConnectorNamejCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConnectorNamejCheckBox1ActionPerformed
        int index=this.ConnectorjComboBox.getSelectedIndex();       
        String keyConnectorName="Connector"+index;
        if (this.ConnectorNamejCheckBox1.isSelected()) {
            this.ConnectorNamejTextField.setEnabled(true);
            if (properties.isSet(keyConnectorName)) this.ConnectorNamejTextField.setText(properties.get(keyConnectorName));
        } else {
            properties.remove(keyConnectorName);
            this.ConnectorNamejTextField.setEnabled(false);
        }
    }//GEN-LAST:event_ConnectorNamejCheckBox1ActionPerformed

    private void ConnectorNamejTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ConnectorNamejTextFieldFocusLost
        int index=this.ConnectorjComboBox.getSelectedIndex();       
        String keyConnectorName="Connector"+index;
        if (this.ConnectorNamejCheckBox1.isSelected()) {
            properties.put(keyConnectorName,this.ConnectorNamejTextField.getText());
        } 
    }//GEN-LAST:event_ConnectorNamejTextFieldFocusLost

    private void LinuxProgramjTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_LinuxProgramjTextFieldFocusLost
      properties.put("ExecutableLinux", this.LinuxProgramjTextField.getText());
      workflow.updateWorkflow(properties);
    }//GEN-LAST:event_LinuxProgramjTextFieldFocusLost

    private void LocateLinuxjButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LocateLinuxjButtonActionPerformed
       JFileChooser jf=new JFileChooser("Executable"+File.separator);
        jf.addChoosableFileFilter(new ProgramsFilter());
        jf.setAcceptAllFileFilterUsed(false);
        jf.setName("Locate program Executable ...");
        int result=jf.showOpenDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {            
            String path=jf.getSelectedFile().getAbsolutePath();
            int index=path.indexOf("executable");
            //??? 
            int index2=path.indexOf("Executable");
            if (index>-1) {
                path=path.substring(path.indexOf("executable"),path.length());
            } else if (index2>-1) {
                path=path.substring(path.indexOf("Executable"),path.length());
                path.replace("Executable", "executable");
            } 
             //--Replace any \ by /
            char a1='\\';
            char a2='/';
            path=path.replace(a1, a2);
            
            properties.put("ExecutableLinux",path);
            this.LinuxProgramjTextField.setText(path);
            workflow.updateWorkflow(properties);
        }
    }//GEN-LAST:event_LocateLinuxjButtonActionPerformed

    private void ArmadilloApplicationsjCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ArmadilloApplicationsjCheckBoxItemStateChanged
         if (ArmadilloApplicationsjCheckBox.isSelected()) {
            properties.put("InternalArmadilloApplication", true);
        } else {
            properties.remove("InternalArmadilloApplication");
        }
        //--Update View
        this.ProgramjTextField.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
        this.LinuxProgramjTextField.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
        this.MACOSXProgramjTextField.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
        this.LocateWindowsjButton.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
        this.LocateLinuxjButton.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
        this.LocateMacOXjButton.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
        
    }//GEN-LAST:event_ArmadilloApplicationsjCheckBoxItemStateChanged

    private void JavajCheckBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_JavajCheckBox2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_JavajCheckBox2ItemStateChanged

    private void WebServcejCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WebServcejCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_WebServcejCheckBoxActionPerformed

    private void jTextArea2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextArea2FocusLost
          if (jTextArea2.getText().trim().equals("")) {
            this.jTextArea2.setText(this.defaultDescriptionString);
            this.jTextArea2.setForeground(Color.LIGHT_GRAY);
        } else {
            String description=this.jTextArea2.getText();
            properties.put("Description", description.trim());
            workflow.updateWorkflow(properties);
        }
    }//GEN-LAST:event_jTextArea2FocusLost

    private void jTextArea2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextArea2KeyTyped
         String description=this.jTextArea2.getText();
         properties.put("Description", description.trim());
         workflow.updateWorkflow(properties);
    }//GEN-LAST:event_jTextArea2KeyTyped

    private void jTextArea2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextArea2FocusGained
        if (jTextArea2.getText().equals(this.defaultDescriptionString)) {
            jTextArea2.setText("");
            jTextArea2.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_jTextArea2FocusGained

    private void WebsitejTextField3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_WebsitejTextField3FocusGained
          if (WebsitejTextField3.getText().equals(this.WebsiteString)) {
            WebsitejTextField3.setText("");
            WebsitejTextField3.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_WebsitejTextField3FocusGained

    private void WebsitejTextField3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_WebsitejTextField3FocusLost
          if (WebsitejTextField3.getText().trim().equals("")) {
            this.WebsitejTextField3.setText(this.WebsiteString);
            this.WebsitejTextField3.setForeground(Color.LIGHT_GRAY);
        } else {
            String description=this.WebsitejTextField3.getText();
            properties.put("Website", description.trim());
            workflow.updateWorkflow(properties);
        }
    }//GEN-LAST:event_WebsitejTextField3FocusLost

    private void WebsitejTextField3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_WebsitejTextField3KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_WebsitejTextField3KeyPressed

    private void WebsitejTextField3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_WebsitejTextField3KeyTyped
       String description=this.WebsitejTextField3.getText();
         properties.put("Website", description.trim());         
    }//GEN-LAST:event_WebsitejTextField3KeyTyped

    private void MACOSXProgramjTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_MACOSXProgramjTextFieldFocusLost
        properties.put("ExecutableMacOSX",this.MACOSXProgramjTextField.getText());
        workflow.updateWorkflow(properties);
       
    }//GEN-LAST:event_MACOSXProgramjTextFieldFocusLost

    private void LocateMacOXjButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LocateMacOXjButtonActionPerformed
        JFileChooser jf=new JFileChooser("Executable"+File.separator);
        jf.addChoosableFileFilter(new ProgramsFilter());
        jf.setAcceptAllFileFilterUsed(false);
        jf.setName("Locate program Executable ...");
        int result=jf.showOpenDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {            
            String path=jf.getSelectedFile().getAbsolutePath();
            int index=path.indexOf("executable");
            //??? 
            int index2=path.indexOf("Executable");
            if (index>-1) {
                path=path.substring(path.indexOf("executable"),path.length());
            } else if (index2>-1) {
                path=path.substring(path.indexOf("Executable"),path.length());
                path.replace("Executable", "executable");
            } 
            //--Replace any \ by /
            char a1='\\';
            char a2='/';
            path=path.replace(a1, a2);
            
            properties.put("ExecutableMacOSX",path);
            this.MACOSXProgramjTextField.setText(path);

        }
    }//GEN-LAST:event_LocateMacOXjButtonActionPerformed

    private void Publication_jTextArea3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_Publication_jTextArea3FocusGained
        if (Publication_jTextArea3.getText().equals(this.PublicationsString)) {
            Publication_jTextArea3.setText("");
            Publication_jTextArea3.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_Publication_jTextArea3FocusGained

    private void Publication_jTextArea3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_Publication_jTextArea3FocusLost
        if (Publication_jTextArea3.getText().trim().equals("")) {
            this.Publication_jTextArea3.setText(this.PublicationsString);
            this.Publication_jTextArea3.setForeground(Color.LIGHT_GRAY);
        } else {
            String description=this.Publication_jTextArea3.getText();
            properties.put("Publication", description.trim());
            workflow.updateWorkflow(properties);
        }
    }//GEN-LAST:event_Publication_jTextArea3FocusLost

    private void Publication_jTextArea3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Publication_jTextArea3KeyTyped
       String description=this.Publication_jTextArea3.getText();
         properties.put("Publication", description.trim());
         workflow.updateWorkflow(properties);
    }//GEN-LAST:event_Publication_jTextArea3KeyTyped

    private void version_windowsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_version_windowsFocusLost
      properties.put("Version",this.version_windows.getText());
    }//GEN-LAST:event_version_windowsFocusLost

    private void version_macosxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_version_macosxFocusLost
      properties.put("Version_MacOSX",this.version_macosx.getText());
    }//GEN-LAST:event_version_macosxFocusLost

    private void version_linuxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_version_linuxFocusLost
        properties.put("Version_Linux",this.version_macosx.getText());
    }//GEN-LAST:event_version_linuxFocusLost

    private void SamplejTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_SamplejTextField1FocusLost
       properties.put("SampleWorkflow",this.SamplejTextField1.getText());
    }//GEN-LAST:event_SamplejTextField1FocusLost

    private void LocateSamplejButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LocateSamplejButton1ActionPerformed
        JFileChooser jf=new JFileChooser(config.dataPath()+File.separator+"help"+File.separator);
        jf.addChoosableFileFilter(new WorkflowFilter());
        jf.setAcceptAllFileFilterUsed(false);
        jf.setName("Locate sample workflow...");
        int result=jf.showOpenDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {            
            String path=jf.getSelectedFile().getAbsolutePath();
            if (path.indexOf(config.dataPath())>-1) {
                path=jf.getSelectedFile().getName();              
            }            
            properties.put("SampleWorkflow",path);
            this.SamplejTextField1.setText(path);

        }
    }//GEN-LAST:event_LocateSamplejButton1ActionPerformed

    private void KeywordjTextField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_KeywordjTextField2FocusLost
        //--TO DO - split keywords - July 2011
        properties.put("Keyword0", this.KeywordjTextField2.getText());
    }//GEN-LAST:event_KeywordjTextField2FocusLost

    private void jList1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jList1FocusLost
       int i=0;
       for (Object properties_obj:this.jList1.getSelectedValues()) {
           properties.put("Similar"+i, (String)properties_obj);
           i++;
       }
    }//GEN-LAST:event_jList1FocusLost

    private void HelpSuppljTextArea3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_HelpSuppljTextArea3FocusLost
       properties.put("HelpSupplementary", this.HelpSuppljTextArea3.getText());
    }//GEN-LAST:event_HelpSuppljTextArea3FocusLost

    private void RuntimeMacOSXjComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RuntimeMacOSXjComboBox1ActionPerformed
       String item=(String)RuntimeMacOSXjComboBox1.getSelectedItem();
       //--Set the Mac OS X execution mode...
       if (RuntimeMacOSXjComboBox1.getSelectedIndex()==0) {
           properties.remove("RuntimeMacOSX");
       } else {
           properties.put("RuntimeMacOSX",item);
       } 
    }//GEN-LAST:event_RuntimeMacOSXjComboBox1ActionPerformed

    private void SpecificPathjTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_SpecificPathjTextField1FocusLost
        if (properties.isSet("RunningDirectory")) properties.put("RunningDirectory", this.SpecificPathjTextField1.getText());
    }//GEN-LAST:event_SpecificPathjTextField1FocusLost

    private void ShowHideNumberjMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowHideNumberjMenuItemActionPerformed
        boolean b=config.getBoolean("displayConnector");
       if (b) {
           config.set("displayConnector", "True");
       } else {
           config.remove("displayConnector");
       }
       config.Save();
       workflow.redraw();
    }//GEN-LAST:event_ShowHideNumberjMenuItemActionPerformed

    private void GenerateHTMLjMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GenerateHTMLjMenuItem3ActionPerformed
       this.createHTMLHelpForProgram(config.dataPath()+File.separator+"help");
        HelpEditor help_generator=new HelpEditor(this.frame, false,properties.getName(),config.dataPath()+File.separator+"help"+File.separator+properties.getName()+".html");
    }//GEN-LAST:event_GenerateHTMLjMenuItem3ActionPerformed

    private void SaveImagejMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveImagejMenuItem4ActionPerformed
        this.workflow.saveImage(this.properties.getName()+".png");
        JOptionPane.showMessageDialog(null, "Current object image was save to :\n"+this.properties.getName()+".png", "Saving images", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_SaveImagejMenuItem4ActionPerformed

    private void ClosejMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClosejMenuItem2ActionPerformed
        close();
    }//GEN-LAST:event_ClosejMenuItem2ActionPerformed

    private void ReloadPropertiesjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReloadPropertiesjMenuItem1ActionPerformed
        if(properties.load()) {
            //Message("Successfull reload of "+properties.getName(),"");
            setComboBoxData();     //--Set all the interface type and current data
            setPropertiesList();   //--Set a list of properties files int testJPanel
            setPropertiesTable();  //--Set current properties in JTable
        } else {
            //MessageError("Unable to reload "+properties.getName(),"");
        }
    }//GEN-LAST:event_ReloadPropertiesjMenuItem1ActionPerformed

    private void SavejMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SavejMenuItem1ActionPerformed
        if(properties.save()) {
            Message("Successfull saving of "+properties.getName(),"");
            //--Reset flag on properties
        } else {
            MessageError("Unable to save "+properties.getName(),"");
        }
    }//GEN-LAST:event_SavejMenuItem1ActionPerformed

    private void UpdateWorkflowjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateWorkflowjMenuItem1ActionPerformed
          workflow.updateWorkflow(properties);
    }//GEN-LAST:event_UpdateWorkflowjMenuItem1ActionPerformed

    private void InsertPropertiesjMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InsertPropertiesjMenuItem2ActionPerformed
        workflow_properties_dictionnary dic=new workflow_properties_dictionnary();
       for (String key:dic.getDefaultProperties()) {
           if (!properties.get(key).equals("Not Set")) properties.put(key, "");
       }
       setPropertiesTable();
    }//GEN-LAST:event_InsertPropertiesjMenuItem2ActionPerformed

    private void SpecificjCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SpecificjCheckBox1ActionPerformed
        Boolean b=this.SpecificjCheckBox1.isSelected();
        if (b) {
            properties.put("RunningDirectory", this.SpecificPathjTextField1.getText());
        } else {
            properties.remove("RunningDirectory");
        }
    }//GEN-LAST:event_SpecificjCheckBox1ActionPerformed

    private void JavajCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JavajCheckBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_JavajCheckBox2ActionPerformed

    /**
     * This duplicate a properties to a new name
     * @param sourceName
     * @param newName
     * @return True if success
     */
    private boolean duplicateProperties(String sourceName, String newName) {
        workflow_properties tmp=new workflow_properties();
        if (tmp.load(sourceName, config.get("propertiesPath"))) {
            if (tmp.save(newName, config.get("propertiesPath"))) return true;
        }
        MessageError("Unable to duplicate "+sourceName,"");
        return false;
    }

    /**
     * This delete from disk a properties in the propertiesPath
     * @param propertiesName
     * @return True if success
     */
    private boolean deleteProperties(String propertiesName) {
        File fileToDelete=new File(config.get("propertiesPath")+File.separator+propertiesName);
        if (fileToDelete.delete()) return true;
        MessageError("Unable to delete "+propertiesName+" from "+config.get("propertiesPath"),"");
        return false;
    }

    /**
     * This list the current properties in the properties JTable
     */
    private void setPropertiesTable() {
       workflow_properties_TableModel tm=(workflow_properties_TableModel)this.jTable1.getModel();
       tm.setData(properties);
       tm.fireTableDataChanged();
       this.jTable1.setModel(tm);
    }

    /**
     * This set the list of the properties File found in the propertiesPath
     * to use in the TestJPanel
     */
    private void setPropertiesList() {
        String[] propertiesList=workflow_properties.loadPropertieslisting(config.get("propertiesPath"));
        this.jList1.setListData(propertiesList);
    }

    /**
     * This set the Properties
     * @param properties
     */
    public void setProperties(workflow_properties properties) {
        this.properties=properties;
        setTitle(properties.getName());
        setPropertiesTable();
        workflow.updateWorkflow(properties);
    }

     /**
     * This set the Properties (this is the main calling routine)
     * @param properties
     */
    public void setProperties(String filename, String path) {
        workflow_properties tmp=new workflow_properties();
        //--Load from file the properties
        tmp.load(filename, path);
        tmp.put("x", 120);
        tmp.put("y",40);
        this.properties=tmp;
        setTitle(properties.getName());
        setPropertiesName();
        setPropertiesTable();
        setSettingForProperties();        
        workflow_object created_workflow_object =workflow.updateWorkflow(tmp);
        workflow.workflow.createOutput_Objects(created_workflow_object.returnConnector(workflow_object.OUTPUT));
    }

    private void setPropertiesName() {
        //--Name
        if (properties.isSet("Name")&&!properties.getName().trim().isEmpty()) {
             this.jTextField1.setText(properties.getName());
            this.jTextField1.setForeground(Color.BLACK);
            this.setTitle(properties.getName());
        } else {
            this.jTextField1.setText(defaultNameString);
            this.jTextField1.setForeground(Color.LIGHT_GRAY);           
        }
        //--Description
        if (properties.isSet("Description")&&!properties.getDescription().trim().isEmpty()) {
            this.jTextArea2.setText(properties.getDescription());
            this.jTextArea2.setForeground(Color.BLACK);
        } else {
            this.jTextArea2.setText(this.defaultDescriptionString);
            this.jTextArea2.setForeground(Color.LIGHT_GRAY);            
        }
         //--Tooltip   
        if (properties.isSet("Tooltip")&&!properties.getTooltip().trim().isEmpty()) {
            this.jTextField2.setText(properties.getTooltip());
            this.jTextField2.setForeground(Color.BLACK);
        } else {
            this.jTextField2.setText(this.TooltipDescriptionString);
            this.jTextField2.setForeground(Color.LIGHT_GRAY);            
        }
        
        //--HELP
        if (properties.isSet("help")&&!properties.get("help").trim().isEmpty()) {
              this.jTextArea1.setText(properties.get("help"));
              this.jTextArea1.setForeground(Color.BLACK);
          } else {
               this.jTextArea1.setText("help");
               this.jTextArea1.setForeground(Color.LIGHT_GRAY);
          }        
         properties.put("HelpSupplementary", this.HelpSuppljTextArea3.getText());
           if (properties.isSet("HelpSupplementary")&&!properties.get("HelpSupplementary").trim().isEmpty()) {
              this.jTextArea1.setText(properties.get("HelpSupplementary"));             
          } else {
               this.jTextArea1.setText("");
               
          }
        //--Publication  
        if (properties.isSet("Publication")&&!properties.get("Publication").trim().isEmpty()) {
              this.Publication_jTextArea3.setText(properties.get("Publication"));
              this.Publication_jTextArea3.setForeground(Color.BLACK);
          } else {
              this.Publication_jTextArea3.setText("");
              this.Publication_jTextArea3.setForeground(Color.LIGHT_GRAY);              
          }
        //--Website
        if (properties.isSet("Website")&&!properties.get("Website").trim().isEmpty()) {
              this.WebsitejTextField3.setText(properties.get("Website"));
              this.WebsitejTextField3.setForeground(Color.BLACK);
          } else {
              this.WebsitejTextField3.setText("");
              this.WebsitejTextField3.setForeground(Color.LIGHT_GRAY);              
          }
        //--Sample
        if (properties.isSet("SampleWorkflow")) {
            this.SamplejTextField1.setText(properties.get("SampleWorkflow"));
        } else {
            this.SamplejTextField1.setText("");
        }
        //--Keywords
         if (properties.isSet("Keyword0")) {
            this.KeywordjTextField2.setText(properties.get("Keyword0"));
        } else {
            this.KeywordjTextField2.setText("");
        }
         
         //--Set selected similar item
          LinkedList<String> similar=new LinkedList<String>();
          LinkedList<Integer> indice=new LinkedList<Integer>();
         for (int i=0; i<100;i++) if (properties.isSet("Similar"+i)) similar.add(properties.get("Similar"+i));
         
         if (similar.size()>0) {             
            for (String str:similar) {
                indice.add(jList1.getNextMatch(str, 0, Bias.Forward));                   
                 }            
         }
         int[] indices=new int[indice.size()];
         for (int j=0; j<indice.size();j++) {
             indices[j]=indice.get(j);
         }
         jList1.setSelectedIndices(indices);
         
    }

    /**
     * This set the different combobox in the editor
     *
     */
    public void setComboBoxData() {
        //--Color
        DefaultComboBoxModel cm=(DefaultComboBoxModel)this.ColorModejComboBox.getModel();
        cm.removeAllElements();
        for (String color:workflow_properties_dictionnary.colorMode) cm.addElement(color);
        this.ColorModejComboBox.setModel(cm);
        //--ObjectType
        DefaultComboBoxModel cm2=(DefaultComboBoxModel)this.ObjectTypejComboBox.getModel();
        cm2.removeAllElements();
        for (String type:workflow_properties_dictionnary.ObjectType) cm2.addElement(type);
        this.ObjectTypejComboBox.setModel(cm2);
        //--InputNb
        DefaultComboBoxModel cm3=(DefaultComboBoxModel)this.InputNbjComboBox.getModel();
        cm3.removeAllElements();
        for (String input:workflow_properties_dictionnary.InputNumber) cm3.addElement(input);
        this.InputNbjComboBox.setModel(cm3);
        //--Available Class
        DefaultComboBoxModel cm4=(DefaultComboBoxModel)this.ClassListjComboBox.getModel();
        cm4.removeAllElements();
        cm4.addElement(""); //We can have no class for now
        for (Classdata c:workflow_properties_dictionnary.getClasslisting("programs")) cm4.addElement(c.getName());
        this.ClassListjComboBox.setModel(cm4);
        //--Connector
        DefaultComboBoxModel cm5=(DefaultComboBoxModel)this.ConnectorjComboBox.getModel();
        cm5.removeAllElements();
        for (Integer i=0;i<4;i++) cm5.addElement(i.toString());
        this.ConnectorjComboBox.setModel(cm5);
         //--Available Class Edtor
        DefaultComboBoxModel cm6=(DefaultComboBoxModel)this.EditorClassNamejComboBox.getModel();
        cm6.removeAllElements();
        cm6.addElement(""); //We can have no class for now
        for (Classdata c:workflow_properties_dictionnary.getClasslisting("editors")) cm6.addElement(c.getName());
        this.EditorClassNamejComboBox.setModel(cm6);
        //--Tree Toolbox
        Vector<workflow_properties>program=new Vector<workflow_properties>();
            for (String filename:workflow_properties.loadPropertieslisting(config.get("propertiesPath"))) {
                workflow_properties tmp=new workflow_properties();
                tmp.load(filename, config.get("propertiesPath"));
                program.add(tmp);
            }
            for (workflow_properties node:program) {
               String category=node.get("Type");
               if (ListType.indexOf(category)<0) ListType.add(category);
            }
            Collections.sort(ListType);
            DefaultComboBoxModel cm7=(DefaultComboBoxModel)this.TypejComboBox.getModel();
            cm7.removeAllElements();
            cm7.addElement(""); //We can have no class for now
            for (String s:ListType) cm7.addElement(s);
            this.TypejComboBox.setModel(cm7);

    }

    /**
     * This set the different setting corresponding to the current properties
     */
    public void setSettingForProperties() {
        //--Set Show Hide toggleButton
            
        //--If side or not
            if (properties.isSet("modeSide")) {
                this.IfSidejCheckBox.setSelected(true);
            } else {
                this.IfSidejCheckBox.setSelected(false);
            } 
        //--Color
            int index=Util.indexOf(properties.get("defaultColor"), workflow_properties_dictionnary.colorMode);
            if (index>-1) this.ColorModejComboBox.setSelectedIndex(index);
        //--ObjectType
            index=Util.indexOf(properties.get("ObjectType"), workflow_properties_dictionnary.ObjectType);
            if (index>-1) this.ObjectTypejComboBox.setSelectedIndex(index);
        //--InputNb
            InputNbjComboBox.setSelectedIndex(properties.getInt("nbInput"));
        //--Class (Running)
            this.ClassListjComboBox.setSelectedItem(properties.get("ClassName"));
        //--Editor
            this.EditorClassNamejComboBox.setSelectedItem(properties.get("EditorClassName"));
        //--Connector
            //--Checkbox
            index=this.ConnectorjComboBox.getSelectedIndex();
            String keyOutput="Connector"+index+"Output";
            String keyConditional="Connector"+index+"Conditional";
            String keyConnectorName="Connector"+index;
            this.ConnectorNamejCheckBox1.setSelected(properties.isSet(keyConnectorName));
            this.ConnectorNamejTextField.setEnabled(properties.isSet(keyConnectorName));
            if (properties.isSet(keyConnectorName)) {
                this.ConnectorNamejTextField.setText(properties.get(keyConnectorName));
            } else {
                this.ConnectorNamejTextField.setText("");
            }
            this.OutputjCheckBox.setSelected(properties.isSet(keyOutput));
            this.ConditionnaljCheckBox.setSelected(properties.isSet(keyConditional));
            //--Table Input-Output
            InputOutputTableModel tm=(InputOutputTableModel)this.InputOutputjTable.getModel();
            //--Reset the table data
            tm.setData(dict.getInputOutput());
            //--Set properties
            tm.setProperties(properties);
            //--Table value
            for (InputOutput I:tm.data) {
                if (properties.isSet(I.getInputOutput())) {
                    String connector=properties.get(I.getInputOutput());
                    I.setConnector(connector);
                } else I.setConnector("No connector");
            tm.fireTableDataChanged();
            InputOutputjTable.setModel(tm);
            }

         //--Type in ToolboxTree -- Warning we rebuild everything to be sure we have the last update
           
          this.TypejComboBox.setSelectedItem(properties.get("Type"));

          //--RunProgram Menu
          //--Clear the jTextFields
          
          this.CommandjTextField.setText("");
          this.ProgramjTextField.setText("");
          this.LinuxProgramjTextField.setText("");
          this.MACOSXProgramjTextField.setText("");
          this.version_windows.setText("");
          this.version_linux.setText("");
          this.version_macosx.setText("");
         
          
          if (properties.isSet("CommandLine")) this.CommandjTextField.setText(properties.getCommandline());
          if (properties.isSet("Executable")) this.ProgramjTextField.setText(properties.getExecutable());
          if (properties.isSet("ExecutableLinux")) this.LinuxProgramjTextField.setText(properties.get("ExecutableLinux"));
          if (properties.isSet("ExecutableMacOSX")) this.MACOSXProgramjTextField.setText(properties.get("ExecutableMacOSX"));
          if (properties.isSet("Version")) this.version_windows.setText(properties.get("Version"));
          if (properties.isSet("Version_Linux")) this.version_linux.setText(properties.get("Version_Linux"));
          if (properties.isSet("Version_MacOSX")) this.version_macosx.setText(properties.get("Version_MacOSX"));
          if (properties.isSet("RuntimeMacOSX")) {
              this.RuntimeMacOSXjComboBox1.setSelectedItem(properties.get("RuntimeMacOSX"));
          } else {
              this.RuntimeMacOSXjComboBox1.setSelectedIndex(0);
          }
          this.WebServcejCheckBox.setSelected(properties.getBoolean("WebServices"));
          this.ProgramjTextField.setEnabled(!properties.getBoolean("WebServices"));
          this.LocateWindowsjButton.setEnabled(!properties.getBoolean("WebServices"));
          this.LocateLinuxjButton.setEnabled(!properties.getBoolean("WebServices"));
          this.LocateMacOXjButton.setEnabled(!properties.getBoolean("WebServices"));
          this.WebServcejCheckBox.setSelected(properties.getBoolean("InternalArmadilloApplication"));
          this.ProgramjTextField.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
          this.LocateWindowsjButton.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
          this.LocateLinuxjButton.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
          this.LocateMacOXjButton.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
          this.VerifyExitValuejCheckBox.setSelected(properties.getBoolean("VerifyExitValue"));
          this.NormalExitValuejTextField.setEnabled(properties.getBoolean("VerifyExitValue"));
          this.NoThreadjCheckBox.setSelected(properties.getBoolean("NoThread"));
          this.DebugjCheckBox.setSelected(properties.getBoolean("debug"));
          this.NormalExitValuejTextField.setText(""+properties.getInt("NormalExitValue"));
                    
            //--Update View
         this.ArmadilloApplicationsjCheckBox.setSelected(properties.getBoolean("InternalArmadilloApplication"));
        this.ProgramjTextField.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
        this.LinuxProgramjTextField.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
        this.MACOSXProgramjTextField.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
        this.LocateWindowsjButton.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
        this.LocateLinuxjButton.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
        this.LocateMacOXjButton.setEnabled(!properties.getBoolean("InternalArmadilloApplication"));
       if (properties.isSet("RunningDirectory")) {
           this.SpecificjCheckBox1.setSelected(true);
           this.SpecificPathjTextField1.setText(properties.get("RunningDirectory"));
       } else {
           this.SpecificjCheckBox1.setSelected(false);
           this.SamplejTextField1.setText("");
       }

    }

    public void updatePropertiesTable() {
        workflow_properties_TableModel tm=(workflow_properties_TableModel) this.jTable1.getModel();
        tm.setData(properties);
        tm.fireTableDataChanged();
        this.jTable1.setModel(tm);
    }

    public void close() {
          //-- 1. Did we change the properties? If, yes ask for direction
          workflow_properties_TableModel tm=(workflow_properties_TableModel)this.jTable1.getModel();
          if (properties.isModified()) {
            String msg="<html><br>The properties were changed. Save them to disk?</html>";
            Object[] options = {"Yes","No", "Cancel"};
            int n = JOptionPane.showOptionDialog(this,msg,"Warning! Properties changed. Save?",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options, options[0]);
            switch (n) {
                case 0: properties.remove("x"); //-- This is set by the object
                        properties.remove("y"); //-- This is set by the object
                        properties.save();
                        break;
                case 1: break;
                case 2: return;
            }
          }
        //-- 2. Close dialog
        Toolbox tool=new Toolbox();
        tool.reloadToolTree();
        this.setVisible(false);
    }


        public String createHTMLHelpForProgram(String dir) {
         report re=new report(); //--Needed later
        //--Create the images in the temp directory
        Config config=new Config();         
       
        workflow.saveImage(dir+File.separator+properties.getName()+".png");
        propertiesEditorBox propertieseditorbox = new propertiesEditorBox(frame);
        propertieseditorbox.display(properties, workflow);        
        propertieseditorbox.saveImage(dir+File.separator+properties.getName()+"_editor.png");        
                
        Util u=new Util(dir+File.separator+properties.getName()+".html");        
        
        StringBuilder st=new StringBuilder();
        
         st.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n");   
         st.append("<html>\n");
         //--Head
         st.append("<head>\n");
         st.append("<meta content=\"text/html; charset=ISO-8859-1\" http-equiv=\"content-type\">\n");
         st.append("<title>Reference | ");
         st.append(properties.getName());
         st.append("</title></head>\n");
         //--Body
         st.append("<body>\n");
         //--Body Title 
         st.append("<big style=\"font-weight: bold; font-family: Courier New,Courier,monospace;\"><big><big><span style=\"color: rgb(102, 102, 102);\">Reference Armadillo v1.0 </span></big></big></big><br><br>\n");
         //--Table
         st.append("<table style=\"text-align: left; width: 800px; height: 300px;\" border=\"0\" cellpadding=\"2\" cellspacing=\"2\">\n");
        //--Applications
         st.append("<tbody><tr><td></td><td style=\"width: 215px;\">Name</td><td style=\"width: 1004px;\"><big><span style=\"font-weight: bold;\">"+properties.getName()+"</span></big></td></tr>\n");
        //--Image
         st.append("<tr><td></td><td>View</td><td><span style=\"font-family: Courier New,Courier,monospace;\"><img src=\""+properties.getName()+".png"+"\"></img></span></td></tr>\n");
         //--Publication
         if (properties.isSet("Publication")) {
            st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Publication(s)</td><td style=\"width: 1004px; align:justify \">"+(properties.isSet("Publication")?properties.get("Publication"):"")+"<br><br></td></tr>\n");
         }
         //--Website
         if (properties.isSet("Website")) {
             st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Website</td><td style=\"width: 1004px; align:justify \">"+"<b><a href='"+properties.get("Website")+"'>"+properties.get("Website")+"</b></a>"+"<br><br></td></tr>\n");
         }
         //--Description
         st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Description</td><td style=\"width: 1004px; align:justify \">"+(properties.isSet("Description")?properties.getDescription():"")+"<br><br></td></tr>\n");
         
         //--Help (supplementary)
         if (properties.isSet("HelpSupplementary")&&!properties.get("HelpSupplementary").isEmpty()) {
            st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Help</td><td style=\"width: 1004px; align:justify \">"+(properties.isSet("HelpSupplementary")?properties.getDescription():"")+"<br><br></td></tr>\n");
         }
         
         //--Input
         
         st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Input (<strong>"+properties.Inputed().size()+"</strong>)</td><td style=\"width: 215px; vertical-align: top;\">\n");
                 for (String str:properties.Inputed()) {
                     st.append(str+"<br>");
                 }
                 
        st.append("</td></tr>");
         //Output
         st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Output (<strong>"+properties.Outputed().size()+"</strong>)</td><td style=\"width: 215px; vertical-align: top;\">\n");
         for (String str:properties.Outputed()) {
                     st.append(str+"<br>\n");
                 }
         st.append("</td></tr>\n");
         //--Control (image)
         st.append("<tr><td></td><td style=\"vertical-align: top;\">Control</td><td><br><img src=\""+properties.getName()+"_editor.png\"></img><br>\n"); 
         //--Syntaxe
         //st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Syntaxe</td><td style=\"width: 1004px;\"><small><span style=\"font-weight: bold;\">saveStrings(const char* filename, string *array);<br></span></small></td></tr>");
         //--Parametert
         //st.append("<tr><td></td><td>Supported parameters</td><td><small><br></small></td></tr><tr><td></td><td style=\"width: 215px; vertical-align: top;\">Return <br></td><td style=\"width: 1004px;\"><small>true if success<br></small></td></tr>");
         
         //--Image
         //st.append("<tr><td></td><td>Exemple application to build</td><td><small><span style=\"font-family: Courier New,Courier,monospace;\"><img src=\""+properties.getName()+".png"+"\"></img></span></small></td></tr>");
         //--Tag (any?)
         LinkedList<String> keywords=new LinkedList<String>();
         for (int i=0; i<100;i++) if (properties.isSet("Keyword"+i)) keywords.add(properties.get("Keyword"+i));
         if (keywords.size()>0) {
             st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Keywords (<strong>"+keywords.size()+"</strong>)</td><td style=\"width: 215px; vertical-align: top;\">\n");         
            for (String str:keywords) {
                     st.append(str+" &nbsp");
                 }
            st.append("</td></tr>\n");
         }
           //--Related
          LinkedList<String> similar=new LinkedList<String>();
         for (int i=0; i<100;i++) if (properties.isSet("Similar"+i)) similar.add(properties.get("Similar"+i));
         if (similar.size()>0) {
             st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Related (<strong>"+similar.size()+"</strong>)</td><td style=\"width: 215px; vertical-align: top;\">\n");         
            for (String str:similar) {
                        workflow_properties prop=new workflow_properties();
                        prop.load(str, config.propertiesPath());
                        st.append("<a href='"+prop.getName()+".html'"+">"+prop.getName()+"</a>"+" &nbsp");
                 }
            st.append("</td></tr>\n");
         }
            
         //st.append("</small></td></tr>");
         //--SAMPLE WORKFLOW
         if (properties.isSet("SampleWorkflow")) {
             //--Description
         st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Sample workflow</td><td style=\"width: 1004px; align:justify \"><a href='"+properties.get("SampleWorkflow")+"'>"+properties.get("SampleWorkflow")+"</a><br><br></td></tr>\n");
         }
         st.append("</tbody></table><br>");
         st.append("</body>");        
         st.append("</html>");
         u.println(st.toString());
         u.close();
         return u.log_filename;
     }
    
    

    ///////////////////////////////////////////////////////////////////////////
    /// MESSAGE FONCTION

    /**
     * Affiche un message dans la status bar
     * La provenance peut être mise dans un tooltip
     * @param text Le texte
     * @param tooltip Le tooltip texte
     */
    void Message(String text, String tooltip) {
        //this.jStatusMessage.setEnabled(true);
        //this.jStatusMessage.setForeground(new java.awt.Color(0, 51, 153));
        //this.jStatusMessage.setBackground(Color.WHITE);
        //this.jStatusMessage.setToolTipText(tooltip);
        //this.jStatusMessage.setText(text);
    }

    /**
     * Affiche un message d'erreur en rouge dans la status bar
     * La provenance peut être mise dans un tooltip
     * @param text Le texte
     * @param tooltip Le tooltip texte
     */
    void MessageError(String text, String tooltip) {
        //this.jStatusMessage.setEnabled(true);
        //this.jStatusMessage.setForeground(Color.RED);
        //this.jStatusMessage.setBackground(Color.WHITE);
        //this.jStatusMessage.setToolTipText(tooltip);
       // this.jStatusMessage.setText(text);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddjButton;
    private javax.swing.JCheckBox ArmadilloApplicationsjCheckBox;
    private javax.swing.JComboBox ClassListjComboBox;
    private javax.swing.JMenuItem ClosejMenuItem2;
    private javax.swing.JComboBox ColorModejComboBox;
    private javax.swing.JTextField CommandjTextField;
    private javax.swing.JCheckBox ConditionnaljCheckBox;
    private javax.swing.JCheckBox ConnectorNamejCheckBox1;
    private javax.swing.JTextField ConnectorNamejTextField;
    private javax.swing.JComboBox ConnectorjComboBox;
    private javax.swing.JCheckBox DebugjCheckBox;
    private javax.swing.JButton DeletejButton;
    private javax.swing.JMenu EditjMenu2;
    private javax.swing.JComboBox EditorClassNamejComboBox;
    private javax.swing.JMenuItem GenerateHTMLjMenuItem3;
    private javax.swing.JTextArea HelpSuppljTextArea3;
    private javax.swing.JCheckBox IfSidejCheckBox;
    private javax.swing.JComboBox InputNbjComboBox;
    private javax.swing.JTable InputOutputjTable;
    private javax.swing.JMenu InsertDefaultPropertiesjMenu1;
    private javax.swing.JMenuItem InsertPropertiesjMenuItem2;
    private javax.swing.JCheckBox JavajCheckBox2;
    private javax.swing.JTextField KeywordjTextField2;
    private javax.swing.JTextField LinuxProgramjTextField;
    private javax.swing.JButton LocateLinuxjButton;
    private javax.swing.JButton LocateMacOXjButton;
    private javax.swing.JButton LocateSamplejButton1;
    private javax.swing.JButton LocateWindowsjButton;
    private javax.swing.JTextField MACOSXProgramjTextField;
    private javax.swing.JCheckBox NoThreadjCheckBox;
    private javax.swing.JTextField NormalExitValuejTextField;
    private javax.swing.JComboBox ObjectTypejComboBox;
    private javax.swing.JCheckBox OutputjCheckBox;
    private javax.swing.JTextField ProgramjTextField;
    private javax.swing.JTextArea Publication_jTextArea3;
    private javax.swing.JMenuItem ReloadPropertiesjMenuItem1;
    private javax.swing.JComboBox RuntimeMacOSXjComboBox1;
    private javax.swing.JTextField SamplejTextField1;
    private javax.swing.JMenuItem SaveImagejMenuItem4;
    private javax.swing.JMenuItem SavejMenuItem1;
    private javax.swing.JMenuItem ShowHideNumberjMenuItem;
    private javax.swing.JTextField SpecificPathjTextField1;
    private javax.swing.JCheckBox SpecificjCheckBox1;
    private javax.swing.JPanel TestjPanel;
    private javax.swing.JComboBox TypejComboBox;
    private javax.swing.JMenuItem UpdateWorkflowjMenuItem1;
    private javax.swing.JCheckBox VerifyExitValuejCheckBox;
    private javax.swing.JCheckBox WebServcejCheckBox;
    private javax.swing.JTextField WebsitejTextField3;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField version_linux;
    private javax.swing.JTextField version_macosx;
    private javax.swing.JTextField version_windows;
    // End of variables declaration//GEN-END:variables

}
