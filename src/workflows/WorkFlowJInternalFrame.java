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

import biologic.RunWorkflow;
import configuration.Config;
import java.awt.Dimension;
import biologic.Workflows;
import configuration.Util;
import database.ExplorerTreeMutableTreeNode;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import program.CancelJDialog;
import program.programs;
import tools.AddCommentsBiologicJDialog;
import tools.Toolbox;
import workflows.armadillo_workflow.*;

/**
 * This is a WorkFlow Displayed...
 * @author Etienne Lord, Mickael Leclercq
 */
public class WorkFlowJInternalFrame extends javax.swing.JInternalFrame {
    
    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    private armadillo_workflow work;
    private Workflows database_workflow;
    public RunWorkflow runworkflow=new RunWorkflow();                //--This is the Runworkflow in results
    private workflow_properties selection=new workflow_properties(); //Selected object
    public WorkFlowPreferenceJDialog preferences;                    //Workflows and Program preferences
    
    private String defaultNoteString=" Note";
    
    public programs program;
    Config config=new Config();
    Frame frame;
    CancelJDialog cancel;
    
    ////////////////////////////////////////////////////////////////////////////////
    /// WorkFlowJInternalFrame tested JG 2016
    
    private boolean cluster;
    
    //Tested Functions
    public boolean isCluster() {
        return cluster;
    }
    public void setCluster(boolean clustured) {
        this.cluster = clustured;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// Main constructor
    public WorkFlowJInternalFrame(Frame frame, Workflows workflow) {
        //--Set variable
        this.database_workflow=workflow;
        work=workflow.getWorkflow();
        //-- Initialize the workflow
        //-- Note: It is added to the jPanel1 in initComponents
        work.init(this, false);
        initComponents();
        preferences=new WorkFlowPreferenceJDialog(frame);
        //this.jProgressBar1.setEnabled(false);
        cancel=new CancelJDialog(frame,false,"Stopping");
        //this.workflow_name_JTextfield.setText(work.getName());
        this.setFrameIcon(Config.icon);
        this.setResizable(true);
        this.jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //this.jTextArea1.setText(this.defaultNoteString);
        //this.Workflow_jTabbedPane.setEnabledAt(2, false); //--Results pane in progresss
        this.frame=frame;
        setSelectedWay(RunOptions_jComboBox);
    }
    
    /**
     * Text mode constructor
     */
    public WorkFlowJInternalFrame() {}
    
    public void setRunWorkflow(RunWorkflow run) {
        this.runworkflow=run;
//        ResultsTableModel model=(ResultsTableModel)this.jTable1.getModel();
//        model.setData(run);
//        model.fireTableDataChanged();
//        model.fireTableStructureChanged();
//        this.jTable1.setModel(model);

    }
    
    /**
     * Common function of JInternalFrame responsable to resize the JInternalFrame to
     * a maximum size in relation to the JDesktopPane size
     */
    public void mazimizeSize() {
        Dimension d = this.getDesktopPane().getSize();
        Toolbox toolbox=new Toolbox();
        int toolbox_width=toolbox.getJInternalFrame().getWidth();
        
        Dimension newsize=new Dimension(d.width-toolbox_width,d.height);
        //Dimension newsize=new Dimension(d.width-toolbox.getDimension().width,d.height);
        //--Debug
        //Config.log("**"+toolbox.getDimension().width);
        this.setSize(newsize);
        this.setPreferredSize(newsize);
        //this.setLocation(toolbox.getDimension().width,0);
        this.setLocation(toolbox_width,0);
        this.repaint();
        work.force_redraw=true;
        work.redraw();
        this.setResizable(true);
        this.jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    }
    
    
    /**
     * Set the visible jPanel to the WorkflowJPanel
     */
    public void setWorkflowSelected() {
        this.Workflow_jTabbedPane.setSelectedIndex(0);
        this.work.redraw();
        this.setResizable(true);
        setSelectedWay(RunOptions_jComboBox);
    }
    
    /**
     * Set the visible jPanel to the ScriptJPanel
     */
    public void setScriptSelected(Workflow wf) {
        this.Workflow_jTabbedPane.setSelectedIndex(1);
        //this.Output_jTextArea.setText(Scripts_conversion.workflowToScript(work.workflow));
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        Workflow_jTabbedPane = new javax.swing.JTabbedPane();
        WorkflowJPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        scrollPane1 = new java.awt.ScrollPane();
        jPanel1 = new javax.swing.JPanel();
        WorkflowsName = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        AddNotejButton = new javax.swing.JButton();
        ScriptJPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Output_jTextArea = new javax.swing.JTextArea();
        ClearjButton = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jStatusMessage = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        ExecuteAllWorkflow_jButton = new javax.swing.JButton();
        ProgressjLabel = new javax.swing.JLabel();
        RunOptions_jComboBox = new javax.swing.JComboBox();

        jTextField1.setText("jTextField1");

        setBackground(new java.awt.Color(255, 255, 255));
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setTitle("Workflow");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        Workflow_jTabbedPane.setBackground(new java.awt.Color(255, 255, 255));
        Workflow_jTabbedPane.setToolTipText("Current workflow.");
        Workflow_jTabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Workflow_jTabbedPaneMouseClicked(evt);
            }
        });

        WorkflowJPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        scrollPane1.add(work);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        WorkflowsName.setFont(new java.awt.Font("Tahoma", 1, 11));
        WorkflowsName.setToolTipText("Name of this Workflow.");
        WorkflowsName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                WorkflowsNameFocusLost(evt);
            }
        });

        jButton4.setText("Rename");
        jButton4.setToolTipText("Rename the current workflow.");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        AddNotejButton.setIcon(config.getIcon("Comments"));
        AddNotejButton.setToolTipText("Add comments tu the current workflow.");
        AddNotejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddNotejButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(WorkflowsName, javax.swing.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(AddNotejButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(WorkflowsName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddNotejButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout WorkflowJPanelLayout = new javax.swing.GroupLayout(WorkflowJPanel);
        WorkflowJPanel.setLayout(WorkflowJPanelLayout);
        WorkflowJPanelLayout.setHorizontalGroup(
            WorkflowJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        WorkflowJPanelLayout.setVerticalGroup(
            WorkflowJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Workflow_jTabbedPane.addTab("WorkFlow", WorkflowJPanel);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel5.setToolTipText("Workflow output.");

        jScrollPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        Output_jTextArea.setColumns(80);
        Output_jTextArea.setLineWrap(true);
        Output_jTextArea.setRows(5);
        jScrollPane1.setViewportView(Output_jTextArea);

        ClearjButton.setText("Clear");
        ClearjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearjButtonActionPerformed(evt);
            }
        });

        jButton2.setText("Save as Text");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 626, Short.MAX_VALUE)
                .addComponent(ClearjButton))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 772, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ClearjButton)
                    .addComponent(jButton2)))
        );

        javax.swing.GroupLayout ScriptJPanelLayout = new javax.swing.GroupLayout(ScriptJPanel);
        ScriptJPanel.setLayout(ScriptJPanelLayout);
        ScriptJPanelLayout.setHorizontalGroup(
            ScriptJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ScriptJPanelLayout.setVerticalGroup(
            ScriptJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Workflow_jTabbedPane.addTab("Output", ScriptJPanel);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jStatusMessage.setEditable(false);
        jStatusMessage.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jStatusMessage.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jStatusMessage.setText("Not Running");
        jStatusMessage.setBorder(null);
        jStatusMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStatusMessageActionPerformed(evt);
            }
        });

        jButton1.setIcon(config.getIcon("stopred"));
        jButton1.setText("Stop");
        jButton1.setToolTipText("This will cancel the current workflow execution.");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        ExecuteAllWorkflow_jButton.setIcon(config.getIcon("play"));
        ExecuteAllWorkflow_jButton.setText("Run");
        ExecuteAllWorkflow_jButton.setToolTipText("This will execute the current workflow.");
        ExecuteAllWorkflow_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExecuteAllWorkflow_jButtonActionPerformed(evt);
            }
        });

        ProgressjLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        ProgressjLabel.setForeground(new java.awt.Color(153, 153, 153));
        ProgressjLabel.setText("100%");

        RunOptions_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Local", "Cluster" }));
        RunOptions_jComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunOptions_jComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jStatusMessage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ProgressjLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RunOptions_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ExecuteAllWorkflow_jButton, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(ExecuteAllWorkflow_jButton)
                .addGap(2, 2, 2)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(RunOptions_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jStatusMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ProgressjLabel)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Workflow_jTabbedPane)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(Workflow_jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
    }//GEN-LAST:event_formComponentResized
    
    private void Workflow_jTabbedPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Workflow_jTabbedPaneMouseClicked
        // TODO add your handling code here:
        //this.Output_jTextArea.setText(Scripts_conversion.workflowToScript(work.workflow));
    }//GEN-LAST:event_Workflow_jTabbedPaneMouseClicked
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        StopSW();
    }//GEN-LAST:event_jButton1ActionPerformed
    
    private void ExecuteAllWorkflow_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExecuteAllWorkflow_jButtonActionPerformed
        //--Note: To execute only the non-finished object
        //--      Do not reset the state
        work.resetState();
        this.Run();
    }//GEN-LAST:event_ExecuteAllWorkflow_jButtonActionPerformed
    
    private void ClearjButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearjButtonActionPerformed
        ClearOuput();
    }//GEN-LAST:event_ClearjButtonActionPerformed
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        JFileChooser jf=new JFileChooser(config.getExplorerPath());
        jf.setName("Saving as text file to ...");
        int result=jf.showSaveDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            String filename=jf.getSelectedFile().getAbsolutePath();
            String path=jf.getSelectedFile().getPath();
            config.setExplorerPath(path);
            config.Save();
            Util file=new Util();
            file.open(filename);
            file.println(this.Output_jTextArea.getText());
            file.close();
            Message("Successfull saving to "+filename,"");
        }
    }//GEN-LAST:event_jButton2ActionPerformed
    
    private void WorkflowsNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_WorkflowsNameFocusLost
        
    }//GEN-LAST:event_WorkflowsNameFocusLost
    
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        
        //--Update this Workflow name
        String newname=this.WorkflowsName.getText();
        if (!newname.equals(this.database_workflow.getName())) {
            this.database_workflow.setName(newname);
            this.database_workflow.updateDatabase();
            Workbox.toolbox.reloadDatabaseTree();
        }
}//GEN-LAST:event_jButton4ActionPerformed
    
    private void AddNotejButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddNotejButtonActionPerformed
        //--Hack, because we don't want duplicate code
        ExplorerTreeMutableTreeNode this_workflow=new ExplorerTreeMutableTreeNode(this.database_workflow.getName(), "Workflows",this.database_workflow.getId());
        AddCommentsBiologicJDialog d=new AddCommentsBiologicJDialog(frame, this_workflow, "Add Comments");
    }//GEN-LAST:event_AddNotejButtonActionPerformed
    
    private void RunOptions_jComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunOptions_jComboBoxActionPerformed
        getSelectedWay(RunOptions_jComboBox);
    }//GEN-LAST:event_RunOptions_jComboBoxActionPerformed
    
    private void jStatusMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStatusMessageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jStatusMessageActionPerformed
    
    /**
     * This set the workflow output in the correct JTextArea
     * @param t
     */
    public void setOutput(String t) {
        try {
            this.Output_jTextArea.setText(t);
        } catch(Exception e){}
    }
    
    public void appendOutput(String t) {
        try {
            //--Only display the last 500 lines
            //--Refactoring needed here - Etienne June 2010
            this.database_workflow.appendWorkflows_outputText(t);
            String buffer=database_workflow.workflows_outputText.toString();
            int buffer_total_line=0;
            int current_index=0;
            for (int i=0; i<buffer.length();i++) {
                if (buffer.charAt(i)=='\n') buffer_total_line++;
            }
            
            int tfind=0;
            for (int i=0; i<buffer.length();i++) {
                if (buffer.charAt(i)=='\n') tfind++;
                if (tfind==(buffer_total_line-250)) {
                    tfind=i;
                    break;
                }
            }
            String str="";
            if (buffer_total_line>250) str+="... ("+(buffer_total_line-250)+" ommited lines) ...\n";
            this.Output_jTextArea.setText(str+buffer.substring(tfind));
        } catch(Exception e){}
    }
    
    void Message(String text, String tooltip) {
        this.jStatusMessage.setEnabled(true);
        this.jStatusMessage.setForeground(new java.awt.Color(0, 51, 153));
        this.jStatusMessage.setToolTipText(tooltip);
        this.jStatusMessage.setText(text);
    }
    
    
    void MessageError(String text, String tooltip) {
        this.jStatusMessage.setEnabled(true);
        this.jStatusMessage.setForeground(Color.RED);
        this.jStatusMessage.setToolTipText(tooltip);
        this.jStatusMessage.setText(text);
    }
    
    public void setProgress(int progress) {
        if (progress>100) progress=100;
        if (progress<0) progress=0;
        this.ProgressjLabel.setText(progress+"%");
        this.jProgressBar1.setValue(progress);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddNotejButton;
    private javax.swing.JButton ClearjButton;
    private javax.swing.JButton ExecuteAllWorkflow_jButton;
    private javax.swing.JTextArea Output_jTextArea;
    private javax.swing.JLabel ProgressjLabel;
    private javax.swing.JComboBox RunOptions_jComboBox;
    private javax.swing.JPanel ScriptJPanel;
    private javax.swing.JPanel WorkflowJPanel;
    private javax.swing.JTabbedPane Workflow_jTabbedPane;
    private javax.swing.JTextField WorkflowsName;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jStatusMessage;
    private javax.swing.JTextField jTextField1;
    private java.awt.ScrollPane scrollPane1;
    // End of variables declaration//GEN-END:variables
    
    
    public void setWorkflowName(String name,String tooltip) {
        this.WorkflowsName.setText(name);
        this.setToolTipText(tooltip);
    }
    
    @Deprecated
    public void setWorkflowNote(String note) {
        //--Color
//          if (note.trim().equals("")||note.equals(defaultNoteString)) {
//            this.jTextArea1.setForeground(Color.LIGHT_GRAY);
//           } else {
//               this.jTextArea1.setForeground(Color.BLACK);
//           }
//           this.jTextArea1.setText(note);
    }
    
    /**
     * Simple thread to cancel the run
     */
    public void StopSW() {
        
        SwingWorker<Integer, Object> loadSwingWorker2=new SwingWorker<Integer, Object>() {
            
            @Override
            protected Integer doInBackground() throws Exception {
                //--Some message...
                setProgress(0);
                if (program!=null) program.Stop();
                return 0;
            }
            
            @Override
            public void process(List<Object> chunk) {
                for (Object o:chunk) {
                    if (o instanceof String)  {
                        String s=(String)o;
                        if (s.startsWith("Unable")||s.startsWith("Error")) {
//                            loading.MessageErreur(s, "");
                        } else {
//                            loading.Message(s,"");
                        } //--End Unable
                    } //--End instance of String
                } //--End list of Object
            } //End process
            
            @Override
            public void done() {
                JOptionPane.showMessageDialog(frame, "<html><b>Warning!</b><br><br>Execution was cancelled.<br>The system might be unstable.<br><b><br>You should save your work...</b></html>","Warning", JOptionPane.WARNING_MESSAGE);
            }
            
        }; //End SwingWorker definition
        
        loadSwingWorker2.addPropertyChangeListener(
                new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                        if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                                //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                    } //End populateNetworkPropertyChange
                });
        //Finally. Show a load dialog :: Warning Work-In-Progress
        MessageError("Stopping execution, please wait...","");
        loadSwingWorker2.execute();
    }
    
    
    /**
     * Execute a "Run" of the current workflow
     */
    public void Run() {
        getSelectedWay(RunOptions_jComboBox);
        program=new programs(database_workflow);
        program.Run();
    }
    
    /**
     * Execute a "Run" of the current workflow
     */
    public void Run(int start,int end) {
        getSelectedWay(RunOptions_jComboBox);
        program=new programs(database_workflow);
        program.Run(start,end);
    }
    
    public void getSelectedWay(javax.swing.JComboBox j) {
        String s = (String)j.getModel().getElementAt(j.getSelectedIndex());
        if (s.equalsIgnoreCase("local")) {
            cluster = false;
            selection.put("WF_tested",false);
        } else if (s.equalsIgnoreCase("cluster")) {
            cluster = true;
            File dir = new File("./tmp/cluster/");
            if (!dir.exists())
                dir.mkdir();
//            String wid = String.valueOf(database_workflow.getId());
//            String sb = "./tmp/cluster/export_num_workflow_before"+wid+".txt";
//            database_workflow.updateCurrentWorkflow();
//            database_workflow.saveWorkflow(sb);
selection.put("WF_tested",true);
insertClusterObject();
        }
        updateClusterObject();
    }
    
    public void insertClusterObject() {
        boolean b = work.getWorkFlow().testClusterPresence();
        if (!b) {
            workflow_properties tmp=new workflow_properties();
            tmp.load("./src/configuration/CLUSTER.properties");
            work.createObject(tmp,new Point(95,5));
            if (tmp==null) {
                System.out.println("Unable to create object from cluster properties");
            }
        }
        work.force_redraw=true;
        work.redraw();
    }
    
    public void loadFromSavedCluster(){
        boolean b = work.getWorkFlow().testClusterPresence();
        if (b) {
            workflow_object tmp;
            tmp = work.getWorkFlow().getClusterObject();
            if (tmp!=null){
                workflow_properties properties = tmp.getProperties();
                boolean b1 = properties.isSet("clusterEnabled");
                selection.put("ClusterAccessAddress",properties.get("Description"));
                if (b1){
                    RunOptions_jComboBox.setSelectedIndex(1);
                    selection.put("WF_tested",true);
                    cluster=true;
                } else {
                    RunOptions_jComboBox.setSelectedIndex(0);
                    selection.put("WF_tested",false);
                    cluster=false;
                }
            } else {
                System.out.println("Impossible to load Cluster object");
            }
        }
        work.force_redraw=true;
        work.redraw();
    }
    
    public void updateClusterObject(){
        boolean b = work.getWorkFlow().testClusterPresence();
        if (b) {
            workflow_object tmp;
            tmp = work.getWorkFlow().getClusterObject();
            //System.out.println(tmp.getProperties().getPropertiesToString());
            if (tmp!=null){
                boolean b1 = Boolean.parseBoolean(selection.get("WF_tested"));
                if (b1){
                    tmp.getProperties().put("clusterEnabled",true);
                    tmp.move(225,10);
                } else {
                    tmp.getProperties().remove("clusterEnabled");
                    tmp.move(-25000,0);
                }
            } else {
                System.out.println("Impossible to load Cluster object");
            }
        }
        work.force_redraw=true;
        work.redraw();
    }
    
    
    public void setSelectedWay(javax.swing.JComboBox j) {
        if (selection.isSet("WF_tested")){
            boolean b = Boolean.parseBoolean(selection.get("WF_tested"));
            if (b) {
                j.setSelectedIndex(1);
                File dir = new File("./tmp/cluster/");
                if (!dir.exists())
                    dir.mkdir();
//                String wid = String.valueOf(database_workflow.getId());
//                String sb = "./tmp/cluster/export_num_workflow_before"+wid+".txt";
//                database_workflow.updateCurrentWorkflow();
//                database_workflow.saveWorkflow(sb);
            } else {
                notATest(j);
            }
        } else {
            notATest(j);
        }
    }
    
    private void notATest(javax.swing.JComboBox j){
        selection.put("WF_tested",false);
        j.setSelectedIndex(0);
    }
    
    public void resetWorkflowStatus(armadillo_workflow work) {
        work.resetState();
        work.force_redraw=true;
        work.redraw();
    }
    
    /**
     * Run a particular program of the workflow
     * @param properties
     */
    public void Run(workflow_properties properties) {
        getSelectedWay(RunOptions_jComboBox);
        program=new programs(database_workflow);
        program.Run(properties);
    }
    
    /**
     * Clear the Ouput Text of the current Workflow
     */
    public void ClearOuput() {
        database_workflow.setWorkflows_outputText("");
        this.Output_jTextArea.setText("");
    }
    
    /**
     * Clear the Ouput Text of the current Workflow
     */
    public workflow_properties getProperties() {
        return selection;
    }
    
}
