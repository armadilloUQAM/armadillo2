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

package editors;


import editor.DatabaseSQLite3_cellRenderer;
import workflows.workflow_properties;
import configuration.Config;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.File;
import editor.EditorInterface;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import program.programs;
import workflows.armadillo_workflow;
import workflows.workflow_properties_dictionnary;

/**
 * Editor of the object properties in the Main Workflow
 * Note: Only called if object doesnt have a Custum Editor
 * @author Etienne Lord
 * @since July 2009
 */
public class ProtParsEditor extends javax.swing.JDialog implements EditorInterface {

    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES

    Config config=new Config();
    //ConnectorInfoBox connectorinfobox;
    workflow_properties_dictionnary dict=new workflow_properties_dictionnary();
    String selected="";             // Selected properties
    Frame frame;
    workflow_properties properties;
    armadillo_workflow parent_workflow;

    ///////////////////////////////////////////////////////////////////////////
    //// Default Values

   
    ////////////////////////////////////////////////////////////////////////////
    /// CONSTANT

    public final String defaultNameString=" Name";

    /////////////////////////////////////////////////////////////////////////
    /// Default Options
    static final String default_dnapars_searchbesttree="Yes";
    static final String default_dnapars_compute_searchoption="More Thorough Search";
    static final int default_dnapars_compute_numbertrees=1;
    static final String default_dnapars_compute_parsimony="No (use ordinary parsimony)";
    static final int default_dnapars_compute_parsimony_count=1;
    static final String default_dnapars_outgrooproot="No (use as outgroup species 1)";
    static final String default_dnapars_outgroupchoice="";
    static final String default_dnapars_weights="No";
    static final String default_dnapars_bootstrap_datasets="No (use import order)";
    static final int default_dnapars_bootstrap_numberdatasets=1;
    static final String default_dnapars_inputorder="No (use input order)";
    static final int default_dnapars_inputorder_seed=1;
    static final int default_dnapars_inputorder_jumble=1;
    static final String default_dnapars_printsteps="No";
    static final String default_dnapars_printsequences="No";


    /** Creates new form propertiesJDialog */
    public ProtParsEditor(java.awt.Frame parent, armadillo_workflow parent_workflow) {
        super(parent, false);
        this.parent_workflow=parent_workflow;
        //--Set variables and init
        frame=parent;
        //connectorinfobox=new ConnectorInfoBox(parent); //--Used to display Connector info
        //--Initialize component
       
    }

    public void setDefaultValues() {
        //searchBestTree_jComboBox.setSelectedItem(default_dnapars_searchbesttree);
        //computeSearchOption_jComboBox.setSelectedItem(default_dnapars_compute_searchoption);
        //computeNumberTrees_jTextField.setText(""+default_dnapars_compute_numbertrees);
        computeParsimony_jComboBox.setSelectedItem(default_dnapars_compute_parsimony);
        computeParsimonyCount_jTextField.setText(""+default_dnapars_compute_parsimony_count);
        try {
            computeOutgroopChoice_jComboBox.setSelectedItem(default_dnapars_outgroupchoice);
        } catch (Exception e) {
            computeOutgroopChoice_jComboBox.addItem(default_dnapars_outgroupchoice);
        }
        //weights_jComboBox.setSelectedItem(default_dnapars_weights);
        bootsrapDataSets_jComboBox.setSelectedItem(default_dnapars_bootstrap_datasets);
        bootstrapDataSetsNumbers_jTextField.setText(""+default_dnapars_bootstrap_numberdatasets);
        bootstrapInputOrder_jComboBox.setSelectedItem(default_dnapars_inputorder);
        bootstrapInputOrderSeed_jTextField.setText(""+default_dnapars_inputorder_seed);
        bootstrapInputOrderJumble_jTextField.setText(""+default_dnapars_inputorder_jumble);
        
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        NamejTextField = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        computeParsimony_jComboBox = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        computeParsimonyCount_jTextField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        computeOutgroopChoice_jComboBox = new javax.swing.JComboBox();
        bootsrapDataSets_jComboBox = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        bootstrapDataSetsNumbers_jTextField = new javax.swing.JTextField();
        bootstrapInputOrder_jComboBox = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        bootstrapInputOrderSeed_jTextField = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        bootstrapInputOrderJumble_jTextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        code_jComboBox1 = new javax.swing.JComboBox();
        ClosejButton = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        jLabel2.setText("jLabel2");

        setTitle("Properties");
        setResizable(false);

        jTabbedPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jTabbedPane1ComponentShown(evt);
            }
        });

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setText("Name");

        jButton4.setText("Rename");
        jButton4.setToolTipText("Rename the Object on the current Workflow.");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NamejTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(NamejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton4))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel5.setText("Compute options");

        jLabel8.setText("Use Threshold parsimony ?");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel12.setText("Bootstrap options");

        jLabel13.setText("Analyze multiple data sets ?");
        jLabel13.setEnabled(false);

        jLabel14.setText("Randomize input order of sequences ?");

        computeParsimony_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No (use ordinary parsimony)", "Yes" }));
        computeParsimony_jComboBox.setEnabled(false);
        computeParsimony_jComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                computeParsimony_jComboBoxActionPerformed(evt);
            }
        });
        computeParsimony_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                computeParsimony_jComboBoxFocusLost(evt);
            }
        });

        jLabel18.setText("Count steps up to");

        computeParsimonyCount_jTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        computeParsimonyCount_jTextField.setText("1");
        computeParsimonyCount_jTextField.setEnabled(false);
        computeParsimonyCount_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                computeParsimonyCount_jTextFieldFocusLost(evt);
            }
        });

        jLabel19.setText("per site");

        jLabel20.setText("Outgroup sequence number:");

        computeOutgroopChoice_jComboBox.setEditable(true);
        computeOutgroopChoice_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No outgroup", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30" }));
        computeOutgroopChoice_jComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                computeOutgroopChoice_jComboBoxActionPerformed(evt);
            }
        });
        computeOutgroopChoice_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                computeOutgroopChoice_jComboBoxFocusLost(evt);
            }
        });

        bootsrapDataSets_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No (use import order)", "Yes (multiple data sets)", "Yes (multiple sets of weight)" }));
        bootsrapDataSets_jComboBox.setEnabled(false);
        bootsrapDataSets_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                bootsrapDataSets_jComboBoxFocusLost(evt);
            }
        });

        jLabel21.setText("Number of sets:");
        jLabel21.setEnabled(false);

        bootstrapDataSetsNumbers_jTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        bootstrapDataSetsNumbers_jTextField.setEnabled(false);
        bootstrapDataSetsNumbers_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                bootstrapDataSetsNumbers_jTextFieldFocusLost(evt);
            }
        });

        bootstrapInputOrder_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No (use input order)", "Yes" }));
        bootstrapInputOrder_jComboBox.setEnabled(false);
        bootstrapInputOrder_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                bootstrapInputOrder_jComboBoxFocusLost(evt);
            }
        });

        jLabel22.setText("Random number seed :");

        bootstrapInputOrderSeed_jTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        bootstrapInputOrderSeed_jTextField.setText("1");
        bootstrapInputOrderSeed_jTextField.setEnabled(false);
        bootstrapInputOrderSeed_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                bootstrapInputOrderSeed_jTextFieldFocusLost(evt);
            }
        });

        jLabel23.setText("Number of times to jumble :");

        bootstrapInputOrderJumble_jTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        bootstrapInputOrderJumble_jTextField.setText("1");
        bootstrapInputOrderJumble_jTextField.setEnabled(false);
        bootstrapInputOrderJumble_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                bootstrapInputOrderJumble_jTextFieldFocusLost(evt);
            }
        });

        jLabel9.setText("Genetic code");

        code_jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "U           Universal", "M           Mitochondrial", "V           Vertebrate mitochondrial", "F           Fly mitochondrial", "Y           Yeast mitochondrial" }));
        code_jComboBox1.setEnabled(false);
        code_jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                code_jComboBox1ActionPerformed(evt);
            }
        });
        code_jComboBox1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                code_jComboBox1FocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(computeParsimonyCount_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addContainerGap(641, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bootstrapInputOrderSeed_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bootstrapInputOrderJumble_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(604, 604, 604))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                                .addComponent(bootstrapDataSetsNumbers_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                                .addComponent(bootsrapDataSets_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 142, Short.MAX_VALUE)
                                .addComponent(computeOutgroopChoice_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(bootstrapInputOrder_jComboBox, 0, 174, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 271, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9))
                                .addGap(78, 78, 78)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(code_jComboBox1, 0, 0, Short.MAX_VALUE)
                                    .addComponent(computeParsimony_jComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(447, 447, 447))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(code_jComboBox1))
                .addGap(9, 9, 9)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(computeParsimony_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel18)
                    .addComponent(computeParsimonyCount_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(computeOutgroopChoice_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(bootsrapDataSets_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(bootstrapDataSetsNumbers_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(bootstrapInputOrder_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(bootstrapInputOrderSeed_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(bootstrapInputOrderJumble_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jScrollPane3.setViewportView(jPanel3);

        ClosejButton.setText("<html><b>Close</b></html>");
        ClosejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClosejButtonActionPerformed(evt);
            }
        });

        jButton7.setText("Run");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Stop");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton6.setText("Reset default values");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 130, Short.MAX_VALUE)
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ClosejButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ClosejButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7)
                    .addComponent(jButton8)
                    .addComponent(jButton6))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("ProtPars (Phylip)", jPanel9);

        jButton3.setText("?");
        jButton3.setToolTipText("Help / Informations");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                    .addComponent(jButton3))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ClosejButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClosejButtonActionPerformed
        //-- 1. Did we change the properties? If, yes ask for direction
         parent_workflow.updateCurrentWorkflow(properties);
        //-- 2. Close dialog
        this.setVisible(false);
}//GEN-LAST:event_ClosejButtonActionPerformed

    private void jTabbedPane1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentShown
      
}//GEN-LAST:event_jTabbedPane1ComponentShown

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       properties.put("Name", this.NamejTextField.getText());
       parent_workflow.updateCurrentWorkflow(properties);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        setDefaultValues();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
       HelpEditor help = new HelpEditor(this.frame, false, properties);
        help.setVisible(true);
}//GEN-LAST:event_jButton3ActionPerformed

    private void computeParsimony_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_computeParsimony_jComboBoxFocusLost
        properties.put("computeParsimony", this.computeParsimony_jComboBox.getSelectedItem().toString());
}//GEN-LAST:event_computeParsimony_jComboBoxFocusLost

    private void computeParsimonyCount_jTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_computeParsimonyCount_jTextFieldFocusLost
        properties.put("computeParsimonyCount", this.computeParsimonyCount_jTextField.getText());
}//GEN-LAST:event_computeParsimonyCount_jTextFieldFocusLost

    private void computeOutgroopChoice_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_computeOutgroopChoice_jComboBoxFocusLost
        properties.put("computeOutgroopChoice", this.computeOutgroopChoice_jComboBox.getSelectedItem().toString());
    }//GEN-LAST:event_computeOutgroopChoice_jComboBoxFocusLost

    private void bootsrapDataSets_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bootsrapDataSets_jComboBoxFocusLost
        properties.put("bootsrapDataSets", this.bootsrapDataSets_jComboBox.getSelectedItem().toString());
    }//GEN-LAST:event_bootsrapDataSets_jComboBoxFocusLost

    private void bootstrapDataSetsNumbers_jTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bootstrapDataSetsNumbers_jTextFieldFocusLost
        properties.put("bootstrapDataSetsNumbers", this.bootstrapDataSetsNumbers_jTextField.getText());
    }//GEN-LAST:event_bootstrapDataSetsNumbers_jTextFieldFocusLost

    private void bootstrapInputOrder_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bootstrapInputOrder_jComboBoxFocusLost
        properties.put("bootstrapInputOrder", this.bootstrapInputOrder_jComboBox.getSelectedItem().toString());
    }//GEN-LAST:event_bootstrapInputOrder_jComboBoxFocusLost

    private void bootstrapInputOrderSeed_jTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bootstrapInputOrderSeed_jTextFieldFocusLost
        properties.put("bootstrapInputOrderSeed", this.bootstrapInputOrderSeed_jTextField.getText());
    }//GEN-LAST:event_bootstrapInputOrderSeed_jTextFieldFocusLost

    private void bootstrapInputOrderJumble_jTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bootstrapInputOrderJumble_jTextFieldFocusLost
        properties.put("bootstrapInputOrderJumble", this.bootstrapInputOrderJumble_jTextField.getText());
    }//GEN-LAST:event_bootstrapInputOrderJumble_jTextFieldFocusLost

    private void computeOutgroopChoice_jComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_computeOutgroopChoice_jComboBoxActionPerformed
        String item=(String)this.computeOutgroopChoice_jComboBox.getSelectedItem();
        if (item.startsWith("No")) {
            properties.remove("outgroup");
        } else properties.put("outgroup", item);
    }//GEN-LAST:event_computeOutgroopChoice_jComboBoxActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        if(properties.isSet("ClassName")) {
            this.parent_workflow.workflow.updateDependance();
            programs prog=new programs(parent_workflow.workbox.getCurrentWorkflows());
            prog.Run(properties);
        }
}//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        properties.put("Status", Config.status_nothing);
        properties.killThread();
}//GEN-LAST:event_jButton8ActionPerformed

    private void code_jComboBox1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_code_jComboBox1FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_code_jComboBox1FocusLost

    private void code_jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_code_jComboBox1ActionPerformed
      properties.put("geneticCode", (String)this.code_jComboBox1.getSelectedItem());
    }//GEN-LAST:event_code_jComboBox1ActionPerformed

    private void computeParsimony_jComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_computeParsimony_jComboBoxActionPerformed
        properties.put("computeParsimony", this.computeParsimony_jComboBox.getSelectedItem().toString());
    }//GEN-LAST:event_computeParsimony_jComboBoxActionPerformed

    /**
    

  
    /**
     * This set the Properties
     * @param properties
     */
    public void setProperties(workflow_properties properties) {
        this.properties=properties;
        setTitle(properties.getName());       
        setSettingForProperties();
        
    }

    /**
     * This set the different setting corresponding to the current properties
     */
    public void setSettingForProperties() {
           this.NamejTextField.setText(properties.getName());
           if (properties.isSet("outgroup")) {
               this.computeOutgroopChoice_jComboBox.setSelectedItem(properties.get("outgroup"));
           }
           if (properties.isSet("geneticCode")) {
               this.code_jComboBox1.setSelectedItem(properties.get("geneticCode"));
           }
           if (properties.isSet("bootstrapInputOrder")) {
               this.bootstrapInputOrder_jComboBox.setSelectedItem(properties.get("bootstrapInputOrder"));
           }
           // if (properties.isSet("Description")) this.Notice.setText(properties.get("Description"));

     }

    ///////////////////////////////////////////////////////////////////////////
    /// DISPLAY MAIN FUNCTION

    public void display(workflow_properties properties) {
        this.properties=properties;
        initComponents();
        setIconImage(Config.image);
        // Set position 
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = getSize();
        setLocation((screenSize.width-d.width)/2,
					(screenSize.height-d.height)/2);
        this.setProperties(properties);
        this.setAlwaysOnTop(true);
        this.setVisible(true);
    }

    public void saveImage(String filename) {
        BufferedImage bi;
        try {
            bi = new Robot().createScreenCapture(this.getBounds()); 
            ImageIO.write(bi, "png", new File(filename));
            this.setVisible(false);
        } catch (Exception ex) {
           Config.log("Unable to save "+filename+" dialog image");
        }            
    }
      
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ClosejButton;
    private javax.swing.JTextField NamejTextField;
    private javax.swing.JComboBox bootsrapDataSets_jComboBox;
    private javax.swing.JTextField bootstrapDataSetsNumbers_jTextField;
    private javax.swing.JTextField bootstrapInputOrderJumble_jTextField;
    private javax.swing.JTextField bootstrapInputOrderSeed_jTextField;
    private javax.swing.JComboBox bootstrapInputOrder_jComboBox;
    private javax.swing.JComboBox code_jComboBox1;
    private javax.swing.JComboBox computeOutgroopChoice_jComboBox;
    private javax.swing.JTextField computeParsimonyCount_jTextField;
    private javax.swing.JComboBox computeParsimony_jComboBox;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables



}
