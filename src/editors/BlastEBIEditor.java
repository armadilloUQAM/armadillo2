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

//http://www.ebi.ac.uk/Tools/webservices/services/ncbiblast

import workflows.workflow_properties;
import configuration.Config;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import editor.EditorInterface;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import program.*;
import workflows.armadillo_workflow;
import workflows.workflow_properties_dictionnary;

/**
 * Editor of the object properties in the Main Workflow
 * Note: Only called if object doesnt have a Custum Editor
 * @author Etienne Lord
 * @since July 2009
 */
public class BlastEBIEditor extends javax.swing.JDialog implements EditorInterface {

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
    //// HASHMAPS
    public static String [] dbCommande = {"embl","em_rel","emnew","emcds","em_rel_env","em_rel_est_env","em_rel_gss_env","em_rel_htg_env","em_rel_pat_env","em_rel_std_env","em_rel_fun","em_rel_est_fun","em_rel_gss_fun","em_rel_htc_fun","em_rel_htg_fun","em_rel_pat_fun","em_rel_std_fun","em_rel_sts_fun","em_rel_tpa_fun","em_rel_hum","em_rel_est_hum","em_rel_gss_hum","em_rel_htc_hum","em_rel_htg_hum","em_rel_pat_hum","em_rel_std_hum","em_rel_sts_hum","em_rel_tpa_hum","em_rel_inv","em_rel_est_inv","em_rel_gss_inv","em_rel_htc_inv","em_rel_htg_inv","em_rel_pat_inv","em_rel_std_inv","em_rel_sts_inv","em_rel_tpa_inv","em_rel_mam","em_rel_est_mam","em_rel_gss_mam","em_rel_htc_mam","em_rel_htg_mam","em_rel_pat_mam","em_rel_std_mam","em_rel_sts_mam","em_rel_tpa_mam","em_rel_mus","em_rel_est_mus","em_rel_gss_mus","em_rel_htc_mus","em_rel_htg_mus","em_rel_pat_mus","em_rel_std_mus","em_rel_sts_mus","em_rel_tpa_mus","em_rel_phg","em_rel_gss_phg","em_rel_htg_phg","em_rel_pat_phg","em_rel_std_phg","em_rel_tpa_phg","em_rel_pln","em_rel_est_pln","em_rel_gss_pln","em_rel_htc_pln","em_rel_htg_pln","em_rel_pat_pln","em_rel_std_pln","em_rel_sts_pln","em_rel_tpa_pln","em_rel_pro","em_rel_est_pro","em_rel_gss_pro","em_rel_htc_pro","em_rel_htg_pro","em_rel_pat_pro","em_rel_std_pro","em_rel_sts_pro","em_rel_tpa_pro","em_rel_rod","em_rel_est_rod","em_rel_gss_rod","em_rel_htc_rod","em_rel_htg_rod","em_rel_pat_rod","em_rel_std_rod","em_rel_sts_rod","em_rel_tpa_rod","em_rel_syn","em_rel_pat_syn","em_rel_std_syn","em_rel_tpa_syn","em_rel_tgn","em_rel_std_tgn","em_rel_unc","em_rel_pat_unc","em_rel_std_unc","em_rel_vrl","em_rel_gss_vrl","em_rel_htg_vrl","em_rel_pat_vrl","em_rel_std_vrl","em_rel_tpa_vrl","em_rel_vrt","em_rel_est_vrt","em_rel_gss_vrt","em_rel_htc_vrt","em_rel_htg_vrt","em_rel_pat_vrt","em_rel_std_vrt","em_rel_sts_vrt","em_rel_tpa_vrt","em_rel_est","em_rel_gss","em_rel_htc","em_rel_htg","em_rel_pat","em_rel_std","em_rel_sts","em_rel_tpa","emall","evec","imgtligm","imgthla"};
    public static String [] dbName = {"EMBL Database","EMBL Release","EMBL Updates","EMBL Coding Sequence","EMBL Environmental","EMBL EST Environmental","EMBL GSS Environmental","EMBL HTG Environmental","EMBL Patent Environmental","EMBL Standard Environmental","EMBL Fungi","EMBL EST Fungi","EMBL GSS Fungi","EMBL HTC Fungi","EMBL HTG Fungi","EMBL Patent Fungi","EMBL Standard Fungi","EMBL STS Fungi","EMBL TPA Fungi","EMBL Human","EMBL EST Human","EMBL GSS Human","EMBL HTC Human","EMBL HTG Human","EMBL Patent Human","EMBL Standard Human","EMBL STS Human","EMBL TPA Human","EMBL Invertebrate","EMBL EST Invertebrate","EMBL GSS Invertebrate","EMBL HTC Invertebrate","EMBL HTG Invertebrate","EMBL Patent Invertebrate","EMBL Standard Invertebrate","EMBL STS Invertebrate","EMBL TPA Invertebrate","EMBL Mammal","EMBL EST Mammal","EMBL GSS Mammal","EMBL HTC Mammal","EMBL HTG Mammal","EMBL Patent Mammal","EMBL Standard Mammal","EMBL STS Mammal","EMBL TPA Mammal","EMBL Mouse","EMBL EST Mouse","EMBL GSS Mouse","EMBL HTC Mouse","EMBL HTG Mouse","EMBL Patent Mouse","EMBL Standard Mouse","EMBL STS Mouse","EMBL TPA Mouse","EMBL Phage","EMBL GSS Phage","EMBL HTG Phage","EMBL Patent Phage","EMBL Standard Phage","EMBL TPA Phage","EMBL Plant","EMBL EST Plant","EMBL GSS Plant","EMBL HTC Plant","EMBL HTG Plant","EMBL Patent Plant","EMBL Standard Plant","EMBL STS Plant","EMBL TPA Plant","EMBL Prokaryote","EMBL EST Prokaryote","EMBL GSS Prokaryote","EMBL HTC Prokaryote","EMBL HTG Prokaryote","EMBL Patent Prokaryote","EMBL Standard Prokaryote","EMBL STS Prokaryote","EMBL TPA Prokaryote","EMBL Rodent","EMBL EST Rodent","EMBL GSS Rodent","EMBL HTC Rodent","EMBL HTG Rodent","EMBL Patent Rodent","EMBL Standard Rodent","EMBL STS Rodent","EMBL TPA Rodent","EMBL Synthetic","EMBL Patent Synthetic","EMBL Standard Synthetic","EMBL TPA Synthetic","EMBL Transgenic","EMBL Standard Transgenic","EMBL Unclassified","EMBL Patent Unclassified","EMBL Standard Unclassified","EMBL Viral","EMBL GSS Viral","EMBL HTG Viral","EMBL Patent Viral","EMBL Standard Viral","EMBL TPA Viral","EMBL Vertibrate","EMBL EST Vertebrate","EMBL GSS Vertibrate","EMBL HTC Vertibrate","EMBL HTG Vertibrate","EMBL Patent Vertibrate","EMBL Standard Vertibrate","EMBL STS Vertibrate","EMBL TPA Vertibrate","EMBL Expressed Sequence Tag","EMBL Genome Survey Sequence","EMBL High Throughput cDNA","EMBL High Throughput Genome","EMBL Patent","EMBL Standard","EMBL Sequence Tagged Site","EMBL Third Party Annotation","EMBL Release and Updates","EMBL Vectors","IMGT/LIGM-DB","IMGT/HLA"};
    public HashMap<String,String> databases=new HashMap<String,String>();

    public static String[] matrix = {"BLOSUM45","BLOSUM50","BLOSUM62","BLOSUM80","BLOSUM90","PAM30","PAM70","PAM250"};
    public static String[] matrixgop = {"14","13","11","10","10","9","10","14"};
    public static String[] matrixgep = {"2","2","1","1","1","1","1","2"};
    public static String[] threshold={"1000","100","10","1.0","0.1","0.01","0.001","0.0001","0.00001","0.0000000001","0.00000000000000000000000000000000000000000000000001","0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001","0.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001"};
    public static String[] treshold_desc={"1000","100","10","1.0","1.0e-1","1.0e-2","1.0e-3","1.0e-4","1.0e-5","1.0e-10","1.0e-50","1.0e-200"};
    public HashMap<String,String> matrixDatasGop=new HashMap<String,String>();
    public HashMap<String,String> matrixDatasGep=new HashMap<String,String>();

    ////////////////////////////////////////////////////////////////////////////
    /// CONSTANT

    public final String defaultNameString="Name";

    /////////////////////////////////////////////////////////////////////////
    /// Default Options
        static final String default_blast_program = "blastn";
        static final String default_blast_database = "EMBL Database";
        static final String default_blast_matrix = "BLOSUM62";
        static final String default_blast_gop = "Default";
        static final String default_blast_gep = "Default";
        static final String default_blast_alignment_output = "100";
        static final String default_blast_score_output = "100";
        static final String default_blast_mask = "False";
        static final String default_blast_dropoff = "Default";
        static final String default_blast_optimise_alignment = "True";
        static final String default_blast_reward_match = "2";
        static final String default_blast_penalty_mismatch = "-3";

    /** Creates new form propertiesJDialog */
    public BlastEBIEditor(java.awt.Frame parent, armadillo_workflow parent_workflow) {
        super(parent, false);
        this.parent_workflow=parent_workflow;
        //--Set variables and init
        frame=parent;
    }

    public void setDefaultValues(){
        blastProgram_jComboBox.setSelectedItem(default_blast_program);
        database_jComboBox.setSelectedItem(default_blast_database);
        matrix_jComboBox.setSelectedItem(default_blast_matrix);
        gop_jComboBox.setSelectedItem(default_blast_gop);
        gep_jComboBox.setSelectedItem(default_blast_gep);
        alignmentOutput_jComboBox.setSelectedItem(default_blast_alignment_output);
        scoreOutput_jComboBox.setSelectedItem(default_blast_score_output);
        mask_jComboBox.setSelectedItem(default_blast_mask);
        dropOff_jComboBox.setSelectedItem(default_blast_dropoff);
        optimise_jComboBox.setSelectedItem(default_blast_optimise_alignment);
        rewardMatch_jTextField.setText(default_blast_reward_match);
        penaltyMismatch_jTextField.setText(default_blast_penalty_mismatch);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        NamejTextField = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        blastProgram_jComboBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        database_jComboBox = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        penaltyMismatch_jTextField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        rewardMatch_jTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        gop_jComboBox = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        gep_jComboBox = new javax.swing.JComboBox();
        matrix_jComboBox = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ExpectedTresholdjComboBox = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        optimise_jComboBox = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        dropOff_jComboBox = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        mask_jComboBox = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        alignmentOutput_jComboBox = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        scoreOutput_jComboBox = new javax.swing.JComboBox();
        ClosejButton = new javax.swing.JButton();
        run_jButton = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

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
                .addComponent(NamejTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(jButton4)
                .addComponent(NamejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setText("BLAST Type");

        blastProgram_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "blastn (Search a DNA sequence against a DNA databank)", "blastp (Search a protein sequence against a protein databank)", "blastx (Translate a nucleic acid sequence and search protein databank)" }));
        blastProgram_jComboBox.setSelectedIndex(2);
        blastProgram_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                blastProgram_jComboBoxFocusLost(evt);
            }
        });

        jLabel5.setText("Database");

        database_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                database_jComboBoxFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addContainerGap(666, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(blastProgram_jComboBox, 0, 734, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addContainerGap())
            .addComponent(database_jComboBox, 0, 738, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(blastProgram_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(database_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Advanced options"));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Penalty and Reward"));

        penaltyMismatch_jTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        penaltyMismatch_jTextField.setText("-3");
        penaltyMismatch_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                penaltyMismatch_jTextFieldFocusLost(evt);
            }
        });

        jLabel16.setText("<html>Penalty for a nucleotide<br> mismatch (blastn only) :</html>");

        jLabel15.setText("<html>Reward for a nucleotide<br> match (blastn only) :<html>");

        rewardMatch_jTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        rewardMatch_jTextField.setText("2");
        rewardMatch_jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                rewardMatch_jTextFieldFocusLost(evt);
            }
        });

        jLabel11.setText("Gap open penalty :");

        gop_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "9", "10", "11", "12", "13", "14", "15", "16", "17" }));
        gop_jComboBox.setSelectedItem(0);
        gop_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                gop_jComboBoxFocusLost(evt);
            }
        });

        jLabel12.setText("Gap extension penalty :");

        gep_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "1", "2" }));
        gep_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                gep_jComboBoxFocusLost(evt);
            }
        });

        matrix_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BLOSUM45", "BLOSUM62", "BLOSUM50", "BLOSUM80", "BLOSUM90", "PAM30", "PAM70", "PAM250" }));
        matrix_jComboBox.setSelectedItem("BLOSUM62");
        matrix_jComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matrix_jComboBoxActionPerformed(evt);
            }
        });
        matrix_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                matrix_jComboBoxFocusLost(evt);
            }
        });

        jLabel6.setText("Scoring matrix to use");

        jLabel3.setText("Expected threshold (E-value)");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rewardMatch_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
                                .addComponent(gep_jComboBox, 0, 106, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(gop_jComboBox, 0, 109, Short.MAX_VALUE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(matrix_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ExpectedTresholdjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(penaltyMismatch_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(matrix_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(ExpectedTresholdjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11)
                    .addComponent(gop_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(penaltyMismatch_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rewardMatch_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(gep_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Treshold and Results"));

        optimise_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "True", "False" }));
        optimise_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                optimise_jComboBoxFocusLost(evt);
            }
        });

        jLabel14.setText("<html>Perform optimised alignments<br> within regions involving gaps :</html>");

        dropOff_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "0", "2", "4", "6", "8", "10" }));
        dropOff_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                dropOff_jComboBoxFocusLost(evt);
            }
        });

        jLabel13.setText("Drop-off :");

        jLabel10.setText("Mask low complexity sequence :");

        mask_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "False", "True" }));
        mask_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                mask_jComboBoxFocusLost(evt);
            }
        });

        jLabel7.setText("<html>Maximum number <br>of alignments to output :</html>");

        alignmentOutput_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "5", "10", "20", "50", "100", "150", "200", "250", "500", "750", "1000" }));
        alignmentOutput_jComboBox.setSelectedIndex(4);
        alignmentOutput_jComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alignmentOutput_jComboBoxActionPerformed(evt);
            }
        });
        alignmentOutput_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                alignmentOutput_jComboBoxFocusLost(evt);
            }
        });

        jLabel8.setText("<html>Maximum number <br>of scores to output :</html>");

        scoreOutput_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "5", "10", "20", "50", "100", "150", "200", "250", "500", "750", "1000" }));
        scoreOutput_jComboBox.setSelectedIndex(4);
        scoreOutput_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                scoreOutput_jComboBoxFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(dropOff_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(optimise_jComboBox, 0, 275, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mask_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(alignmentOutput_jComboBox, 0, 217, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(scoreOutput_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scoreOutput_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(alignmentOutput_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addComponent(mask_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel13)
                                .addComponent(dropOff_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(optimise_jComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane3.setViewportView(jPanel3);

        ClosejButton.setText("<html><b>Close</b></html>");
        ClosejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClosejButtonActionPerformed(evt);
            }
        });

        run_jButton.setText("Run");
        run_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                run_jButtonActionPerformed(evt);
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
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 207, Short.MAX_VALUE)
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(run_jButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ClosejButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ClosejButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(run_jButton)
                    .addComponent(jButton8)
                    .addComponent(jButton6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Blast Web EBI", jPanel9);

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
                        .addGap(17, 17, 17))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ClosejButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClosejButtonActionPerformed
        this.setVisible(false);
}//GEN-LAST:event_ClosejButtonActionPerformed

    private void jTabbedPane1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentShown
      
}//GEN-LAST:event_jTabbedPane1ComponentShown

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       properties.put("Name", this.NamejTextField.getText());
       parent_workflow.updateCurrentWorkflow(properties);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void run_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_run_jButtonActionPerformed
        if(properties.isSet("ClassName")) {
           this.parent_workflow.workflow.updateDependance();
           programs prog=new programs(parent_workflow.workbox.getCurrentWorkflows());
           prog.Run(properties);
        }
}//GEN-LAST:event_run_jButtonActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        properties.put("Status", Config.status_nothing);
        properties.killThread();
}//GEN-LAST:event_jButton8ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        setDefaultValues();
}//GEN-LAST:event_jButton6ActionPerformed

    private void blastProgram_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_blastProgram_jComboBoxFocusLost
        properties.put("blastProgram", String.valueOf(this.blastProgram_jComboBox.getSelectedItem()));
    }//GEN-LAST:event_blastProgram_jComboBoxFocusLost

    private void database_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_database_jComboBoxFocusLost
        properties.put("database", databases.get(String.valueOf(this.database_jComboBox.getSelectedItem())));
    }//GEN-LAST:event_database_jComboBoxFocusLost

    private void matrix_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_matrix_jComboBoxFocusLost
        properties.put("matrix", String.valueOf(this.matrix_jComboBox.getSelectedItem()));
    }//GEN-LAST:event_matrix_jComboBoxFocusLost

    private void alignmentOutput_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_alignmentOutput_jComboBoxFocusLost
        properties.put("alignmentOuput", String.valueOf(alignmentOutput_jComboBox.getSelectedItem()));
    }//GEN-LAST:event_alignmentOutput_jComboBoxFocusLost

    private void scoreOutput_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_scoreOutput_jComboBoxFocusLost
        properties.put("scoreOutput", String.valueOf(this.scoreOutput_jComboBox.getSelectedItem()));
    }//GEN-LAST:event_scoreOutput_jComboBoxFocusLost

    private void mask_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_mask_jComboBoxFocusLost
        properties.put("maskComplexity", this.mask_jComboBox.getSelectedItem().toString());
}//GEN-LAST:event_mask_jComboBoxFocusLost

    private void gop_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_gop_jComboBoxFocusLost
        properties.put("gapOpenPenalty", this.gop_jComboBox.getSelectedItem().toString());
    }//GEN-LAST:event_gop_jComboBoxFocusLost

    private void gep_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_gep_jComboBoxFocusLost
        properties.put("gapExtensionPenalty", this.gep_jComboBox.getSelectedItem().toString());
    }//GEN-LAST:event_gep_jComboBoxFocusLost

    private void dropOff_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dropOff_jComboBoxFocusLost
        properties.put("dropOff", this.dropOff_jComboBox.getSelectedItem().toString());
    }//GEN-LAST:event_dropOff_jComboBoxFocusLost

    private void optimise_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_optimise_jComboBoxFocusLost
        properties.put("OptimiseAlignments", this.optimise_jComboBox.getSelectedItem().toString());
    }//GEN-LAST:event_optimise_jComboBoxFocusLost

    private void rewardMatch_jTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rewardMatch_jTextFieldFocusLost
        properties.put("rewardNucleotideMatch", this.rewardMatch_jTextField.getText());
    }//GEN-LAST:event_rewardMatch_jTextFieldFocusLost

    private void penaltyMismatch_jTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_penaltyMismatch_jTextFieldFocusLost
        properties.put("penaltyNucleotideMismatch", this.penaltyMismatch_jTextField.getText());
}//GEN-LAST:event_penaltyMismatch_jTextFieldFocusLost

    private void alignmentOutput_jComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alignmentOutput_jComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_alignmentOutput_jComboBoxActionPerformed

    private void matrix_jComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matrix_jComboBoxActionPerformed

        for (int i=0;i<matrix.length;i++){
            if (this.matrix_jComboBox.getSelectedItem().toString().matches(matrix[i].toString())){
                Config.log(matrix[i].toString());
                this.gop_jComboBox.setSelectedItem(matrixDatasGop.get(matrix[i].toString()));
                Config.log(matrixDatasGop.get(matrix[i].toString()));
                this.gep_jComboBox.setSelectedItem(matrixDatasGep.get(matrix[i].toString()));
                Config.log(matrixDatasGep.get(matrix[i].toString()));
            }
        }
    }//GEN-LAST:event_matrix_jComboBoxActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        HelpEditor help = new HelpEditor(this.frame, false, properties);
        help.setVisible(true);
}//GEN-LAST:event_jButton3ActionPerformed



    

  
    /**
     * This set the Properties
     * @param properties
     */
    public void setProperties(workflow_properties properties) {
        this.properties=properties;
        setTitle(properties.getName());                
    }

     /**
     * This set the Properties
     * @param properties
     */
    public void setProperties(String filename, String path) {
        workflow_properties tmp=new workflow_properties();
        tmp.load(filename, path);
        this.properties=tmp;
        setTitle(properties.getName());      
     
    }

    ///////////////////////////////////////////////////////////////////////////
    /// DISPLAY MAIN FUNCTION

    public void display(workflow_properties properties) {
        this.properties=properties;
        initComponents();
        setIconImage(Config.image);
        this.setTitle("BLAST");
        // Set properties
        //if (properties.isSet("Description")) this.Notice.setText(properties.get("Description"));
        this.NamejTextField.setText(properties.getName());
        //--Initialize component
        for (int i=0;i<dbName.length;i++) databases.put(dbName[i], dbCommande[i]);
        for (int i=0;i<matrix.length;i++) matrixDatasGop.put(matrix[i], matrixgop[i]);
        for (int i=0;i<matrix.length;i++) matrixDatasGep.put(matrix[i], matrixgep[i]);
        
        //Add elements in databaseComboBox
        DefaultComboBoxModel cbModel = new DefaultComboBoxModel();
        for (int i=0;i<dbName.length;i++) cbModel.addElement(dbName[i]);
        database_jComboBox.setModel(cbModel);

        // Set position 
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = getSize();
        setLocation((screenSize.width-d.width)/2,
					(screenSize.height-d.height)/2);
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
    private javax.swing.JComboBox ExpectedTresholdjComboBox;
    private javax.swing.JTextField NamejTextField;
    private javax.swing.JComboBox alignmentOutput_jComboBox;
    private javax.swing.JComboBox blastProgram_jComboBox;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox database_jComboBox;
    private javax.swing.JComboBox dropOff_jComboBox;
    private javax.swing.JComboBox gep_jComboBox;
    private javax.swing.JComboBox gop_jComboBox;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox mask_jComboBox;
    private javax.swing.JComboBox matrix_jComboBox;
    private javax.swing.JComboBox optimise_jComboBox;
    private javax.swing.JTextField penaltyMismatch_jTextField;
    private javax.swing.JTextField rewardMatch_jTextField;
    private javax.swing.JButton run_jButton;
    private javax.swing.JComboBox scoreOutput_jComboBox;
    // End of variables declaration//GEN-END:variables



}
