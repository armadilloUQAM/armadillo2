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
import editor.ConnectorInfoBox;
import editor.DatabaseSQLite3_cellRendererInputOnly;
import editor.EditorInterface;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListModel;
import program.*;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import workflows.armadillo_workflow;
import workflows.armadillo_workflow.Workflow;
import workflows.workflow_properties_dictionnary;

/**
 * Editor of the object properties in the Main Workflow
 * Note: Only called if object doesnt have a Custum Editor
 * @author Etienne Lord
 * @since July 2009
 */
public class AncestorCCEditor extends javax.swing.JDialog implements EditorInterface {

    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES

    Config config=new Config();
    //ConnectorInfoBox connectorinfobox;
    workflow_properties_dictionnary dict=new workflow_properties_dictionnary();
    String selected="";             // Selected properties
    Frame frame;
    workflow_properties properties;
    armadillo_workflow parent_workflow;
   
    ////////////////////////////////////////////////////////////////////////////
    /// CONSTANT

    public final String defaultNameString=" Name";


    /////////////////////////////////////////////////////////////////////////
    /// Default Options
    static final boolean default_ancestor_exact_scenario=false;
    static final boolean default_ancestor_heuristic_scenario=true;
    static final boolean default_ancestor_heuristic_scenario_absolute=true;
    static final double default_ancestor_heuristic_scenario_absolute_data=5.0;
    static final boolean default_ancestor_heuristic_scenario_proportional=true;
    static final double default_ancestor_heuristic_scenario_proportional_data=0.0;
    static final boolean default_ancestor_exact_posterior=false;
    static final boolean default_ancestor_heuristic_posterior=false;
    static final boolean default_ancestor_heuristic_posterior_absolute=true;
    static final double default_ancestor_heuristic_posterior_absolute_data=5.0;
    static final boolean default_ancestor_heuristic_posterior_proportional=false;
    static final double default_ancestor_heuristic_posterior_proportional_data=0.0;

    static final double default_ancestor_insertion_start=0.01;
    static final double default_ancestor_insertion_extension=0.1;
    static final double default_ancestor_deletion_start=0.01;
    static final double default_ancestor_deletion_extension=0.1;

    /** Creates new form propertiesJDialog */
    public AncestorCCEditor(java.awt.Frame parent, armadillo_workflow parent_workflow) {
        super(parent, false);
        this.parent_workflow=parent_workflow;
        //--Set variables and init
        frame=parent;
        //connectorinfobox=new ConnectorInfoBox(parent); //--Used to display Connector info
        //--Initialize component
       
    }

    public void setDefaultValues() {
        ancestorExactScenario_jRadioButton.setSelected(default_ancestor_exact_scenario);
        ancestorHeuristicScenario_jRadioButton.setEnabled(default_ancestor_heuristic_scenario);
        ancestorHeuristicScenarioAbs_jRadioButton.setEnabled(default_ancestor_heuristic_scenario_absolute);
        ancestorHeuristicScenarioAbs_jTextField.setText(""+default_ancestor_heuristic_scenario_absolute_data);
        ancestorHeuristicScenarioProp_jRadioButton.setEnabled(default_ancestor_heuristic_scenario_proportional);
        ancestorHeuristicScenarioProp_jTextField.setText(""+default_ancestor_heuristic_scenario_proportional_data);
        ancestorExactPosterior_jRadioButton.setSelected(default_ancestor_exact_posterior);
        ancestorHeuristicPosterior_jRadioButton.setSelected(default_ancestor_heuristic_posterior);
        ancestorHeuristicPosteriorAbs_jRadioButton.setSelected(default_ancestor_heuristic_posterior_absolute);
        ancestorHeuristicPosteriorAbs_jTextField.setText(""+default_ancestor_heuristic_posterior_absolute_data);
        ancestorHeuristicPosteriorProp_jRadioButton.setSelected(default_ancestor_heuristic_posterior_proportional);
        ancestorHeuristicPosteriorProp_jTextField.setText(""+default_ancestor_heuristic_posterior_proportional_data);

        ancestorInsertionStart_jTextField.setText(""+default_ancestor_insertion_start);
        ancestorInsertionExt_jTextField.setText(""+default_ancestor_insertion_extension);
        ancestorDeletionStart_jTextField.setText(""+default_ancestor_deletion_start);
        ancestorDeletionExt_jTextField.setText(""+default_ancestor_deletion_extension);
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
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        NamejTextField = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        ancestorExactScenario_jRadioButton = new javax.swing.JRadioButton();
        ancestorHeuristicScenario_jRadioButton = new javax.swing.JRadioButton();
        ancestorHeuristicScenarioAbs_jTextField = new javax.swing.JTextField();
        ancestorHeuristicScenarioProp_jTextField = new javax.swing.JTextField();
        ancestorExactPosterior_jRadioButton = new javax.swing.JRadioButton();
        ancestorHeuristicPosterior_jRadioButton = new javax.swing.JRadioButton();
        ancestorHeuristicPosteriorAbs_jTextField = new javax.swing.JTextField();
        ancestorHeuristicPosteriorProp_jTextField = new javax.swing.JTextField();
        ancestorHeuristicScenarioAbs_jRadioButton = new javax.swing.JRadioButton();
        ancestorHeuristicScenarioProp_jRadioButton = new javax.swing.JRadioButton();
        ancestorHeuristicPosteriorAbs_jRadioButton = new javax.swing.JRadioButton();
        ancestorHeuristicPosteriorProp_jRadioButton = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        ancestorInsertionStart_jTextField = new javax.swing.JTextField();
        ancestorInsertionExt_jTextField = new javax.swing.JTextField();
        ancestorDeletionStart_jTextField = new javax.swing.JTextField();
        ancestorDeletionExt_jTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ClosejButton = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        jLabel2.setText("jLabel2");

        setTitle("Properties");

        jTabbedPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jTabbedPane1ComponentShown(evt);
            }
        });

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setText("Name");

        jButton4.setText("Rename");
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
                .addComponent(NamejTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Insertion and Deletion scenario"));

        jLabel5.setText("Computation parameters");

        buttonGroup1.add(ancestorExactScenario_jRadioButton);
        ancestorExactScenario_jRadioButton.setText(" The best exact scenario");
        ancestorExactScenario_jRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ancestorExactScenario_jRadioButtonActionPerformed(evt);
            }
        });
        ancestorExactScenario_jRadioButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorExactScenario_jRadioButtonFocusLost(evt);
            }
        });

        buttonGroup1.add(ancestorHeuristicScenario_jRadioButton);
        ancestorHeuristicScenario_jRadioButton.setSelected(true);
        ancestorHeuristicScenario_jRadioButton.setText("The best heuristic scenario");
        ancestorHeuristicScenario_jRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ancestorHeuristicScenario_jRadioButtonActionPerformed(evt);
            }
        });
        ancestorHeuristicScenario_jRadioButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorHeuristicScenario_jRadioButtonFocusLost(evt);
            }
        });

        ancestorHeuristicScenarioAbs_jTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ancestorHeuristicScenarioAbs_jTextField.setText("5.0");
        ancestorHeuristicScenarioAbs_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorHeuristicScenarioAbs_jTextFieldFocusLost(evt);
            }
        });

        ancestorHeuristicScenarioProp_jTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ancestorHeuristicScenarioProp_jTextField.setText("0.0");
        ancestorHeuristicScenarioProp_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorHeuristicScenarioProp_jTextFieldFocusLost(evt);
            }
        });

        buttonGroup1.add(ancestorExactPosterior_jRadioButton);
        ancestorExactPosterior_jRadioButton.setText("The exact posterior decoding");
        ancestorExactPosterior_jRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ancestorExactPosterior_jRadioButtonActionPerformed(evt);
            }
        });
        ancestorExactPosterior_jRadioButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorExactPosterior_jRadioButtonFocusLost(evt);
            }
        });

        buttonGroup1.add(ancestorHeuristicPosterior_jRadioButton);
        ancestorHeuristicPosterior_jRadioButton.setText("The heuristic posterior decoding");
        ancestorHeuristicPosterior_jRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ancestorHeuristicPosterior_jRadioButtonActionPerformed(evt);
            }
        });
        ancestorHeuristicPosterior_jRadioButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorHeuristicPosterior_jRadioButtonFocusLost(evt);
            }
        });

        ancestorHeuristicPosteriorAbs_jTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ancestorHeuristicPosteriorAbs_jTextField.setText("5.0");
        ancestorHeuristicPosteriorAbs_jTextField.setEnabled(false);
        ancestorHeuristicPosteriorAbs_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorHeuristicPosteriorAbs_jTextFieldFocusLost(evt);
            }
        });

        ancestorHeuristicPosteriorProp_jTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ancestorHeuristicPosteriorProp_jTextField.setText("0.0");
        ancestorHeuristicPosteriorProp_jTextField.setEnabled(false);
        ancestorHeuristicPosteriorProp_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorHeuristicPosteriorProp_jTextFieldFocusLost(evt);
            }
        });

        buttonGroup2.add(ancestorHeuristicScenarioAbs_jRadioButton);
        ancestorHeuristicScenarioAbs_jRadioButton.setSelected(true);
        ancestorHeuristicScenarioAbs_jRadioButton.setText("Absolute");
        ancestorHeuristicScenarioAbs_jRadioButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorHeuristicScenarioAbs_jRadioButtonFocusLost(evt);
            }
        });

        buttonGroup2.add(ancestorHeuristicScenarioProp_jRadioButton);
        ancestorHeuristicScenarioProp_jRadioButton.setText("Proportional");
        ancestorHeuristicScenarioProp_jRadioButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorHeuristicScenarioProp_jRadioButtonFocusLost(evt);
            }
        });

        buttonGroup3.add(ancestorHeuristicPosteriorAbs_jRadioButton);
        ancestorHeuristicPosteriorAbs_jRadioButton.setSelected(true);
        ancestorHeuristicPosteriorAbs_jRadioButton.setText("Absolute");
        ancestorHeuristicPosteriorAbs_jRadioButton.setEnabled(false);
        ancestorHeuristicPosteriorAbs_jRadioButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorHeuristicPosteriorAbs_jRadioButtonFocusLost(evt);
            }
        });

        buttonGroup3.add(ancestorHeuristicPosteriorProp_jRadioButton);
        ancestorHeuristicPosteriorProp_jRadioButton.setText("Proportional");
        ancestorHeuristicPosteriorProp_jRadioButton.setEnabled(false);
        ancestorHeuristicPosteriorProp_jRadioButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorHeuristicPosteriorProp_jRadioButtonFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ancestorExactPosterior_jRadioButton)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(ancestorHeuristicPosteriorAbs_jRadioButton)
                                .addComponent(ancestorHeuristicPosteriorProp_jRadioButton))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(ancestorHeuristicPosteriorProp_jTextField)
                                .addComponent(ancestorHeuristicPosteriorAbs_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ancestorExactScenario_jRadioButton, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ancestorHeuristicScenario_jRadioButton, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ancestorHeuristicScenarioAbs_jRadioButton)
                                    .addComponent(ancestorHeuristicScenarioProp_jRadioButton))
                                .addGap(10, 10, 10)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(ancestorHeuristicScenarioProp_jTextField)
                                    .addComponent(ancestorHeuristicScenarioAbs_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(ancestorHeuristicPosterior_jRadioButton, javax.swing.GroupLayout.Alignment.LEADING))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ancestorExactScenario_jRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ancestorHeuristicScenario_jRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ancestorHeuristicScenarioAbs_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ancestorHeuristicScenarioAbs_jRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ancestorHeuristicScenarioProp_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ancestorHeuristicScenarioProp_jRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ancestorExactPosterior_jRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ancestorHeuristicPosterior_jRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(ancestorHeuristicPosteriorAbs_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ancestorHeuristicPosteriorProp_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(ancestorHeuristicPosteriorAbs_jRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ancestorHeuristicPosteriorProp_jRadioButton)))
                .addGap(10, 10, 10))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Tree Hidden Markov Model probabilities"));

        ancestorInsertionStart_jTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ancestorInsertionStart_jTextField.setText("0.01");
        ancestorInsertionStart_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorInsertionStart_jTextFieldFocusLost(evt);
            }
        });

        ancestorInsertionExt_jTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ancestorInsertionExt_jTextField.setText("0.1");
        ancestorInsertionExt_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorInsertionExt_jTextFieldFocusLost(evt);
            }
        });

        ancestorDeletionStart_jTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ancestorDeletionStart_jTextField.setText("0.01");
        ancestorDeletionStart_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorDeletionStart_jTextFieldFocusLost(evt);
            }
        });

        ancestorDeletionExt_jTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ancestorDeletionExt_jTextField.setText("0.1");
        ancestorDeletionExt_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                ancestorDeletionExt_jTextFieldFocusLost(evt);
            }
        });

        jLabel7.setText("Insertion Start");

        jLabel8.setText("Insertion Extension");

        jLabel9.setText("Deletion Start");

        jLabel10.setText("Deletion Extension");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(51, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ancestorInsertionStart_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ancestorInsertionExt_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ancestorDeletionStart_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ancestorDeletionExt_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ancestorInsertionStart_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ancestorInsertionExt_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ancestorDeletionStart_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ancestorDeletionExt_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap(108, Short.MAX_VALUE))
        );

        jLabel6.setText("DNA substitution probabilities model:  HKY");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addContainerGap())
        );

        ClosejButton.setText("<html><b>Close</b></html>");
        ClosejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClosejButtonActionPerformed(evt);
            }
        });

        jButton5.setText("Run");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
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
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jButton6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 149, Short.MAX_VALUE)
                                .addComponent(jButton8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ClosejButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(31, 31, 31))))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(jButton8)
                    .addComponent(jButton5)
                    .addComponent(ClosejButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane1.addTab("AncestorCC", jPanel9);

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
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(553, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ClosejButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClosejButtonActionPerformed
        this.setVisible(false);
}//GEN-LAST:event_ClosejButtonActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if(properties.isSet("ClassName")) {
           this.parent_workflow.workflow.updateDependance();
           programs prog=new programs(parent_workflow.workbox.getCurrentWorkflows());
           prog.Run(properties);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        properties.put("Status", Config.status_nothing);
        properties.killThread();
}//GEN-LAST:event_jButton8ActionPerformed

    private void jTabbedPane1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentShown

}//GEN-LAST:event_jTabbedPane1ComponentShown

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        properties.put("Name", this.NamejTextField.getText());
        parent_workflow.updateCurrentWorkflow(properties);
}//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        properties.load();             //--reload current properties from file
        this.setProperties(properties);//--Update current field
}//GEN-LAST:event_jButton6ActionPerformed

    private void ancestorHeuristicScenario_jRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ancestorHeuristicScenario_jRadioButtonActionPerformed
        buttonsCheck();
    }//GEN-LAST:event_ancestorHeuristicScenario_jRadioButtonActionPerformed

    private void ancestorHeuristicPosterior_jRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ancestorHeuristicPosterior_jRadioButtonActionPerformed
        buttonsCheck();
    }//GEN-LAST:event_ancestorHeuristicPosterior_jRadioButtonActionPerformed

    private void ancestorExactPosterior_jRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ancestorExactPosterior_jRadioButtonActionPerformed
        buttonsCheck();
    }//GEN-LAST:event_ancestorExactPosterior_jRadioButtonActionPerformed

    private void ancestorExactScenario_jRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ancestorExactScenario_jRadioButtonActionPerformed
        buttonsCheck();
    }//GEN-LAST:event_ancestorExactScenario_jRadioButtonActionPerformed

    private void ancestorExactScenario_jRadioButtonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorExactScenario_jRadioButtonFocusLost
        properties.put("exactScenario", ancestorExactScenario_jRadioButton.isSelected());
    }//GEN-LAST:event_ancestorExactScenario_jRadioButtonFocusLost

    private void ancestorHeuristicScenario_jRadioButtonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorHeuristicScenario_jRadioButtonFocusLost
        properties.put("heuristicScenario", ancestorHeuristicScenario_jRadioButton.isSelected());
    }//GEN-LAST:event_ancestorHeuristicScenario_jRadioButtonFocusLost

    private void ancestorHeuristicScenarioAbs_jTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorHeuristicScenarioAbs_jTextFieldFocusLost
        properties.put("heuristicScenarioAbsData", ancestorHeuristicScenarioAbs_jTextField.getText());
    }//GEN-LAST:event_ancestorHeuristicScenarioAbs_jTextFieldFocusLost

    private void ancestorHeuristicScenarioProp_jTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorHeuristicScenarioProp_jTextFieldFocusLost
        properties.put("heuristicScenarioPropData", ancestorHeuristicScenarioProp_jTextField.getText());
    }//GEN-LAST:event_ancestorHeuristicScenarioProp_jTextFieldFocusLost

    private void ancestorExactPosterior_jRadioButtonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorExactPosterior_jRadioButtonFocusLost
        properties.put("exactPosterior", ancestorExactPosterior_jRadioButton.isSelected());
    }//GEN-LAST:event_ancestorExactPosterior_jRadioButtonFocusLost

    private void ancestorHeuristicPosterior_jRadioButtonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorHeuristicPosterior_jRadioButtonFocusLost
        properties.put("heuristicPosterior", ancestorHeuristicPosterior_jRadioButton.isSelected());
    }//GEN-LAST:event_ancestorHeuristicPosterior_jRadioButtonFocusLost

    private void ancestorHeuristicPosteriorAbs_jTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorHeuristicPosteriorAbs_jTextFieldFocusLost
        properties.put("heuristicPosteriorAbsData", ancestorHeuristicPosteriorAbs_jTextField.getText());
    }//GEN-LAST:event_ancestorHeuristicPosteriorAbs_jTextFieldFocusLost

    private void ancestorHeuristicPosteriorProp_jTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorHeuristicPosteriorProp_jTextFieldFocusLost
        properties.put("heuristicPosteriorPropData", ancestorHeuristicPosteriorProp_jTextField.getText());
    }//GEN-LAST:event_ancestorHeuristicPosteriorProp_jTextFieldFocusLost

    private void ancestorInsertionStart_jTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorInsertionStart_jTextFieldFocusLost
        properties.put("insertionStart", ancestorInsertionStart_jTextField.getText());
    }//GEN-LAST:event_ancestorInsertionStart_jTextFieldFocusLost

    private void ancestorInsertionExt_jTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorInsertionExt_jTextFieldFocusLost
        properties.put("insertionExt", ancestorInsertionExt_jTextField.getText());
    }//GEN-LAST:event_ancestorInsertionExt_jTextFieldFocusLost

    private void ancestorDeletionStart_jTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorDeletionStart_jTextFieldFocusLost
        properties.put("deletionStart", ancestorDeletionStart_jTextField.getText());
    }//GEN-LAST:event_ancestorDeletionStart_jTextFieldFocusLost

    private void ancestorDeletionExt_jTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorDeletionExt_jTextFieldFocusLost
        properties.put("deletionExt", ancestorDeletionExt_jTextField.getText());
    }//GEN-LAST:event_ancestorDeletionExt_jTextFieldFocusLost

    private void ancestorHeuristicScenarioAbs_jRadioButtonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorHeuristicScenarioAbs_jRadioButtonFocusLost
        properties.put("HeuristicScenarioAbs", ancestorHeuristicScenarioAbs_jRadioButton.isSelected());
    }//GEN-LAST:event_ancestorHeuristicScenarioAbs_jRadioButtonFocusLost

    private void ancestorHeuristicScenarioProp_jRadioButtonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorHeuristicScenarioProp_jRadioButtonFocusLost
        properties.put("HeuristicScenarioProp", ancestorHeuristicScenarioProp_jRadioButton.isSelected());
    }//GEN-LAST:event_ancestorHeuristicScenarioProp_jRadioButtonFocusLost

    private void ancestorHeuristicPosteriorAbs_jRadioButtonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorHeuristicPosteriorAbs_jRadioButtonFocusLost
        properties.put("HeuristicPosteriorAbs", ancestorHeuristicPosteriorAbs_jRadioButton.isSelected());
    }//GEN-LAST:event_ancestorHeuristicPosteriorAbs_jRadioButtonFocusLost

    private void ancestorHeuristicPosteriorProp_jRadioButtonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ancestorHeuristicPosteriorProp_jRadioButtonFocusLost
        properties.put("HeuristicPosteriorProp", ancestorHeuristicPosteriorProp_jRadioButton.isSelected());
    }//GEN-LAST:event_ancestorHeuristicPosteriorProp_jRadioButtonFocusLost

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        HelpEditor help = new HelpEditor(this.frame, false, properties);
        help.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    public void buttonsCheck(){
        if (ancestorHeuristicScenario_jRadioButton.isSelected()){
            ancestorHeuristicScenarioAbs_jTextField.setEnabled(true);
            ancestorHeuristicScenarioProp_jTextField.setEnabled(true);
            ancestorHeuristicScenarioAbs_jRadioButton.setEnabled(true);
            ancestorHeuristicScenarioProp_jRadioButton.setEnabled(true);
        } else {
            ancestorHeuristicScenarioAbs_jTextField.setEnabled(false);
            ancestorHeuristicScenarioProp_jTextField.setEnabled(false);
            ancestorHeuristicScenarioAbs_jRadioButton.setEnabled(false);
            ancestorHeuristicScenarioProp_jRadioButton.setEnabled(false);
        }
        if (ancestorHeuristicPosterior_jRadioButton.isSelected()){
            ancestorHeuristicPosteriorAbs_jTextField.setEnabled(true);
            ancestorHeuristicPosteriorProp_jTextField.setEnabled(true);
            ancestorHeuristicPosteriorAbs_jRadioButton.setEnabled(true);
            ancestorHeuristicPosteriorProp_jRadioButton.setEnabled(true);
        } else {
            ancestorHeuristicPosteriorAbs_jTextField.setEnabled(false);
            ancestorHeuristicPosteriorProp_jTextField.setEnabled(false);
            ancestorHeuristicPosteriorAbs_jRadioButton.setEnabled(false);
            ancestorHeuristicPosteriorProp_jRadioButton.setEnabled(false);
        }
    }
  
    /**
     * This set the Properties
     * @param properties
     */
    public void setProperties(workflow_properties properties) {
        this.properties=properties;
        setTitle(properties.getName());      
        this.NamejTextField.setText(properties.getName());
        //if (properties.isSet("Description")) this.Notice.setText(properties.get("Description"));
    }      

    ///////////////////////////////////////////////////////////////////////////
    /// DISPLAY MAIN FUNCTION

    public void display(workflow_properties properties) {
        this.properties=properties;
        initComponents();
        setProperties(properties);
        setIconImage(Config.image);
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
    private javax.swing.JTextField ancestorDeletionExt_jTextField;
    private javax.swing.JTextField ancestorDeletionStart_jTextField;
    private javax.swing.JRadioButton ancestorExactPosterior_jRadioButton;
    private javax.swing.JRadioButton ancestorExactScenario_jRadioButton;
    private javax.swing.JRadioButton ancestorHeuristicPosteriorAbs_jRadioButton;
    private javax.swing.JTextField ancestorHeuristicPosteriorAbs_jTextField;
    private javax.swing.JRadioButton ancestorHeuristicPosteriorProp_jRadioButton;
    private javax.swing.JTextField ancestorHeuristicPosteriorProp_jTextField;
    private javax.swing.JRadioButton ancestorHeuristicPosterior_jRadioButton;
    private javax.swing.JRadioButton ancestorHeuristicScenarioAbs_jRadioButton;
    private javax.swing.JTextField ancestorHeuristicScenarioAbs_jTextField;
    private javax.swing.JRadioButton ancestorHeuristicScenarioProp_jRadioButton;
    private javax.swing.JTextField ancestorHeuristicScenarioProp_jTextField;
    private javax.swing.JRadioButton ancestorHeuristicScenario_jRadioButton;
    private javax.swing.JTextField ancestorInsertionExt_jTextField;
    private javax.swing.JTextField ancestorInsertionStart_jTextField;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables



}
