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

import biologic.seqclasses.GeneticCode;
import biologic.seqclasses.Translator;
import workflows.workflow_properties;
import configuration.Config;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.File;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import editor.EditorInterface;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import program.*;
import workflows.armadillo_workflow;
import workflows.workflow_properties_dictionnary;

/**
 * Editor of the object properties in the Main Workflow
 * Note: Only called if object doesnt have a Custum Editor
 * @author Etienne Lord
 * @since July 2009
 */
public class BlastEditor extends javax.swing.JDialog implements EditorInterface {

    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES

    Config config=new Config();
    //ConnectorInfoBox connectorinfobox;
    workflow_properties_dictionnary dict=new workflow_properties_dictionnary();
    String selected="";             // Selected properties
    Frame frame;
    workflow_properties properties;
    armadillo_workflow parent_workflow;
    Translator translator=new Translator();

    ////////////////////////////////////////////////////////////////////////////
    //// HASHMAPS
    public static String [] dbCommande = {"embl","em_rel","emnew","emcds","em_rel_env","em_rel_est_env","em_rel_gss_env","em_rel_htg_env","em_rel_pat_env","em_rel_std_env","em_rel_fun","em_rel_est_fun","em_rel_gss_fun","em_rel_htc_fun","em_rel_htg_fun","em_rel_pat_fun","em_rel_std_fun","em_rel_sts_fun","em_rel_tpa_fun","em_rel_hum","em_rel_est_hum","em_rel_gss_hum","em_rel_htc_hum","em_rel_htg_hum","em_rel_pat_hum","em_rel_std_hum","em_rel_sts_hum","em_rel_tpa_hum","em_rel_inv","em_rel_est_inv","em_rel_gss_inv","em_rel_htc_inv","em_rel_htg_inv","em_rel_pat_inv","em_rel_std_inv","em_rel_sts_inv","em_rel_tpa_inv","em_rel_mam","em_rel_est_mam","em_rel_gss_mam","em_rel_htc_mam","em_rel_htg_mam","em_rel_pat_mam","em_rel_std_mam","em_rel_sts_mam","em_rel_tpa_mam","em_rel_mus","em_rel_est_mus","em_rel_gss_mus","em_rel_htc_mus","em_rel_htg_mus","em_rel_pat_mus","em_rel_std_mus","em_rel_sts_mus","em_rel_tpa_mus","em_rel_phg","em_rel_gss_phg","em_rel_htg_phg","em_rel_pat_phg","em_rel_std_phg","em_rel_tpa_phg","em_rel_pln","em_rel_est_pln","em_rel_gss_pln","em_rel_htc_pln","em_rel_htg_pln","em_rel_pat_pln","em_rel_std_pln","em_rel_sts_pln","em_rel_tpa_pln","em_rel_pro","em_rel_est_pro","em_rel_gss_pro","em_rel_htc_pro","em_rel_htg_pro","em_rel_pat_pro","em_rel_std_pro","em_rel_sts_pro","em_rel_tpa_pro","em_rel_rod","em_rel_est_rod","em_rel_gss_rod","em_rel_htc_rod","em_rel_htg_rod","em_rel_pat_rod","em_rel_std_rod","em_rel_sts_rod","em_rel_tpa_rod","em_rel_syn","em_rel_pat_syn","em_rel_std_syn","em_rel_tpa_syn","em_rel_tgn","em_rel_std_tgn","em_rel_unc","em_rel_pat_unc","em_rel_std_unc","em_rel_vrl","em_rel_gss_vrl","em_rel_htg_vrl","em_rel_pat_vrl","em_rel_std_vrl","em_rel_tpa_vrl","em_rel_vrt","em_rel_est_vrt","em_rel_gss_vrt","em_rel_htc_vrt","em_rel_htg_vrt","em_rel_pat_vrt","em_rel_std_vrt","em_rel_sts_vrt","em_rel_tpa_vrt","em_rel_est","em_rel_gss","em_rel_htc","em_rel_htg","em_rel_pat","em_rel_std","em_rel_sts","em_rel_tpa","emall","evec","imgtligm","imgthla"};
    public static String [] dbName = {"EMBL Database","EMBL Release","EMBL Updates","EMBL Coding Sequence","EMBL Environmental","EMBL EST Environmental","EMBL GSS Environmental","EMBL HTG Environmental","EMBL Patent Environmental","EMBL Standard Environmental","EMBL Fungi","EMBL EST Fungi","EMBL GSS Fungi","EMBL HTC Fungi","EMBL HTG Fungi","EMBL Patent Fungi","EMBL Standard Fungi","EMBL STS Fungi","EMBL TPA Fungi","EMBL Human","EMBL EST Human","EMBL GSS Human","EMBL HTC Human","EMBL HTG Human","EMBL Patent Human","EMBL Standard Human","EMBL STS Human","EMBL TPA Human","EMBL Invertebrate","EMBL EST Invertebrate","EMBL GSS Invertebrate","EMBL HTC Invertebrate","EMBL HTG Invertebrate","EMBL Patent Invertebrate","EMBL Standard Invertebrate","EMBL STS Invertebrate","EMBL TPA Invertebrate","EMBL Mammal","EMBL EST Mammal","EMBL GSS Mammal","EMBL HTC Mammal","EMBL HTG Mammal","EMBL Patent Mammal","EMBL Standard Mammal","EMBL STS Mammal","EMBL TPA Mammal","EMBL Mouse","EMBL EST Mouse","EMBL GSS Mouse","EMBL HTC Mouse","EMBL HTG Mouse","EMBL Patent Mouse","EMBL Standard Mouse","EMBL STS Mouse","EMBL TPA Mouse","EMBL Phage","EMBL GSS Phage","EMBL HTG Phage","EMBL Patent Phage","EMBL Standard Phage","EMBL TPA Phage","EMBL Plant","EMBL EST Plant","EMBL GSS Plant","EMBL HTC Plant","EMBL HTG Plant","EMBL Patent Plant","EMBL Standard Plant","EMBL STS Plant","EMBL TPA Plant","EMBL Prokaryote","EMBL EST Prokaryote","EMBL GSS Prokaryote","EMBL HTC Prokaryote","EMBL HTG Prokaryote","EMBL Patent Prokaryote","EMBL Standard Prokaryote","EMBL STS Prokaryote","EMBL TPA Prokaryote","EMBL Rodent","EMBL EST Rodent","EMBL GSS Rodent","EMBL HTC Rodent","EMBL HTG Rodent","EMBL Patent Rodent","EMBL Standard Rodent","EMBL STS Rodent","EMBL TPA Rodent","EMBL Synthetic","EMBL Patent Synthetic","EMBL Standard Synthetic","EMBL TPA Synthetic","EMBL Transgenic","EMBL Standard Transgenic","EMBL Unclassified","EMBL Patent Unclassified","EMBL Standard Unclassified","EMBL Viral","EMBL GSS Viral","EMBL HTG Viral","EMBL Patent Viral","EMBL Standard Viral","EMBL TPA Viral","EMBL Vertibrate","EMBL EST Vertebrate","EMBL GSS Vertibrate","EMBL HTC Vertibrate","EMBL HTG Vertibrate","EMBL Patent Vertibrate","EMBL Standard Vertibrate","EMBL STS Vertibrate","EMBL TPA Vertibrate","EMBL Expressed Sequence Tag","EMBL Genome Survey Sequence","EMBL High Throughput cDNA","EMBL High Throughput Genome","EMBL Patent","EMBL Standard","EMBL Sequence Tagged Site","EMBL Third Party Annotation","EMBL Release and Updates","EMBL Vectors","IMGT/LIGM-DB","IMGT/HLA"};
    public HashMap<String,String> databases=new HashMap<String,String>();

    public static String[] matrix = {"BLOSUM45","BLOSUM50","BLOSUM62","BLOSUM80","BLOSUM90","PAM30","PAM70","PAM250"};
    public static String[] matrixgop = {"14","13","11","10","10","9","10","14"};
    public static String[] matrixgep = {"2","2","1","1","1","1","1","2"};
    //public static String[] threshold={"1000","100","10","1.0","0.1","0.01","0.001","0.0001","0.00001","0.0000000001","0.00000000000000000000000000000000000000000000000001","0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001","0.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001"};
    public static String[] treshold_desc={"Default","1000","100","10","1.0","1.0e-1","1.0e-2","1.0e-3","1.0e-4","1.0e-5","1.0e-10","1.0e-50","1.0e-200"};
    public HashMap<String,String> matrixDatasGop=new HashMap<String,String>();
    public HashMap<String,String> matrixDatasGep=new HashMap<String,String>();

    ////////////////////////////////////////////////////////////////////////////
    /// CONSTANT

    public final String defaultNameString="Name";

       
    /** Creates new form propertiesJDialog */
    public BlastEditor(java.awt.Frame parent, armadillo_workflow parent_workflow) {
        super(parent, false);
        this.parent_workflow=parent_workflow;
        //--Set variables and init
        frame=parent;
    }

    public void setDefaultValues(){
        properties.load();             //--reload current properties from file
        this.setProperties(properties);//--Update current field
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
        SubBlastnjComboBox = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        gop_jComboBox = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        gep_jComboBox = new javax.swing.JComboBox();
        reward_jComboBox1 = new javax.swing.JComboBox();
        penalty_jComboBox2 = new javax.swing.JComboBox();
        wordsize_jComboBox3 = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        percent_jComboBox4 = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        StrandjComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        ExpectedTresholdjComboBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        ResultsjComboBox = new javax.swing.JComboBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        DUSTjCheckBox = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        outfmt_jComboBox = new javax.swing.JComboBox();
        outformat_jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        database_jComboBox = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        db_genetic_code_jComboBox1 = new javax.swing.JComboBox();
        query_genetic_codejComboBox2 = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        Filter_jComboBox3 = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        Filename_jTextField = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
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
                .addComponent(NamejTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
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

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setText("BLAST Type");

        blastProgram_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "blastn (Search a DNA sequence against a DNA databank)", "blastp (Search a protein sequence against a protein databank)", "blastx (Search protein database using a translated nucleotide query)", "tblastn (Search translated nucleotide database using a protein query)", "tblastx (Search translated nucleotide database using a translated nucleotide query)", "psiblast " }));
        blastProgram_jComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blastProgram_jComboBoxActionPerformed(evt);
            }
        });
        blastProgram_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                blastProgram_jComboBoxFocusLost(evt);
            }
        });

        SubBlastnjComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "blastn", "blastn-short", "dc-megablast", "megablast", "vecscreen" }));
        SubBlastnjComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubBlastnjComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(blastProgram_jComboBox, 0, 512, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(SubBlastnjComboBox, 0, 491, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(449, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(blastProgram_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SubBlastnjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Advanced options"));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Penalty and Reward"));

        jLabel16.setText("Penalty for a nucleotide");

        jLabel15.setText("Reward for a nucleotide");

        jLabel11.setText("Gap open penalty");

        gop_jComboBox.setEditable(true);
        gop_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "1", "3", "5", "7", "9", "10", "11", "12", "13", "14", "15", "16", "17" }));
        gop_jComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gop_jComboBoxActionPerformed(evt);
            }
        });

        jLabel12.setText("Gap extension penalty");

        gep_jComboBox.setEditable(true);
        gep_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "1", "2", "3", "4", " " }));
        gep_jComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gep_jComboBoxActionPerformed(evt);
            }
        });

        reward_jComboBox1.setEditable(true);
        reward_jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "1", "3", "5", "7", "9", "10", "11", "12", "13", "14", "15", "16", "17" }));
        reward_jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reward_jComboBox1ActionPerformed(evt);
            }
        });

        penalty_jComboBox2.setEditable(true);
        penalty_jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "1", "3", "5", "7", "9", "10", "11", "12", "13", "14", "15", "16", "17" }));
        penalty_jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                penalty_jComboBox2ActionPerformed(evt);
            }
        });

        wordsize_jComboBox3.setEditable(true);
        wordsize_jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "4", "5", "7", "9", "10", "11", "12", "13", "14", "15", "16", "17", "20" }));
        wordsize_jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wordsize_jComboBox3ActionPerformed(evt);
            }
        });

        jLabel17.setText("Wordsize");

        percent_jComboBox4.setEditable(true);
        percent_jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "4", "5", "7", "9", "10", "11", "12", "13", "14", "15", "16", "17", "20" }));
        percent_jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                percent_jComboBox4ActionPerformed(evt);
            }
        });

        jLabel18.setText("Percent Identity (minimum)");

        jButton1.setText("View info");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel18)
                    .addComponent(jLabel16)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addComponent(jLabel15)
                    .addComponent(jLabel12)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(gop_jComboBox, 0, 93, Short.MAX_VALUE)
                    .addComponent(gep_jComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 93, Short.MAX_VALUE)
                    .addComponent(reward_jComboBox1, 0, 93, Short.MAX_VALUE)
                    .addComponent(percent_jComboBox4, 0, 93, Short.MAX_VALUE)
                    .addComponent(penalty_jComboBox2, 0, 93, Short.MAX_VALUE)
                    .addComponent(wordsize_jComboBox3, 0, 93, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(gop_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(gep_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(reward_jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(penalty_jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(wordsize_jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(percent_jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setText("Strand to search");

        StrandjComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "both (Default)", "plus", "minus" }));
        StrandjComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StrandjComboBoxActionPerformed(evt);
            }
        });

        jLabel3.setText("Expected threshold ");

        ExpectedTresholdjComboBox.setEditable(true);
        ExpectedTresholdjComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExpectedTresholdjComboBoxActionPerformed(evt);
            }
        });

        jLabel5.setText("Number of results");

        ResultsjComboBox.setEditable(true);
        ResultsjComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "20", "50", "100", "250", "500", "1000", "2000" }));
        ResultsjComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResultsjComboBoxActionPerformed(evt);
            }
        });

        jCheckBox1.setText("Ungapped only");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        DUSTjCheckBox.setText("DUST Filter");
        DUSTjCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DUSTjCheckBoxActionPerformed(evt);
            }
        });

        jLabel7.setText("Output");

        outfmt_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", " 0 = pairwise", " 1 = query-anchored showing identities", " 2 = query-anchored no identities", " 3 = flat query-anchored, show identities", " 4 = flat query-anchored, no identities", " 5 = XML Blast output", " 6 = tabular", " 7 = tabular with comment lines", " 8 = Text ASN.1", " 9 = Binary ASN.1", "10 = Comma-separated values" }));
        outfmt_jComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outfmt_jComboBoxActionPerformed(evt);
            }
        });

        outformat_jTextField1.setEnabled(false);
        outformat_jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                outformat_jTextField1FocusLost(evt);
            }
        });

        jButton2.setText("View info");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2)))
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ResultsjComboBox, 0, 226, Short.MAX_VALUE)
                            .addComponent(StrandjComboBox, 0, 226, Short.MAX_VALUE)
                            .addComponent(ExpectedTresholdjComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 226, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(DUSTjCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(outfmt_jComboBox, 0, 351, Short.MAX_VALUE))))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(outformat_jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(ResultsjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StrandjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(DUSTjCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(ExpectedTresholdjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(outfmt_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(outformat_jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        database_jComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-NCBI Nucleotide Sequence Databases-", "nr", "refseq_rna", "refseq_genomic", "est", "est_human", "est_mouse", "est_others", "gss", "htgs", "pat", "pdb", "month", "dbsts", "chromosome", "wgs", "env_nt", " ", "-NCBI Protein Sequence Databases-", "nr", "refseq", "swissprot", "pat", "pdb", "month", "env_nr", " ", "-EMBL databases-", " ", "- See Nucleotide : http://www.ebi.ac.uk/Tools/sss/ncbiblast/help/index-nucleotide.html#database", "embl", "em_rel", "emnew", "emcds", "em_rel_env", "em_rel_est_env", "em_rel_gss_env", "em_rel_htg_env", "em_rel_pat_env", "em_rel_std_env", "em_rel_fun", "em_rel_est_fun", "em_rel_gss_fun", "em_rel_htc_fun", "em_rel_htg_fun", "em_rel_pat_fun", "em_rel_std_fun", "em_rel_sts_fun", "em_rel_tpa_fun", "em_rel_hum", "em_rel_est_hum", "em_rel_gss_hum", "em_rel_htc_hum", "em_rel_htg_hum", "em_rel_pat_hum", "em_rel_std_hum", "em_rel_sts_hum", "em_rel_tpa_hum", "em_rel_inv", "em_rel_est_inv", "em_rel_gss_inv", "em_rel_htc_inv", "em_rel_htg_inv", "em_rel_pat_inv", "em_rel_std_inv", "em_rel_sts_inv", "em_rel_tpa_inv", "em_rel_mam", "em_rel_est_mam", "em_rel_gss_mam", "em_rel_htc_mam", "em_rel_htg_mam", "em_rel_pat_mam", "em_rel_std_mam", "em_rel_sts_mam", "em_rel_tpa_mam", "em_rel_mus", "em_rel_est_mus", "em_rel_gss_mus", "em_rel_htc_mus", "em_rel_htg_mus", "em_rel_pat_mus", "em_rel_std_mus", "em_rel_sts_mus", "em_rel_tpa_mus", "em_rel_phg", "em_rel_gss_phg", "em_rel_htg_phg", "em_rel_pat_phg", "em_rel_std_phg", "em_rel_tpa_phg", "em_rel_pln", "em_rel_est_pln", "em_rel_gss_pln", "em_rel_htc_pln", "em_rel_htg_pln", "em_rel_pat_pln", "em_rel_std_pln", "em_rel_sts_pln", "em_rel_tpa_pln", "em_rel_pro", "em_rel_est_pro", "em_rel_gss_pro", "em_rel_htc_pro", "em_rel_htg_pro", "em_rel_pat_pro", "em_rel_std_pro", "em_rel_sts_pro", "em_rel_tpa_pro", "em_rel_rod", "em_rel_est_rod", "em_rel_gss_rod", "em_rel_htc_rod", "em_rel_htg_rod", "em_rel_pat_rod", "em_rel_std_rod", "em_rel_sts_rod", "em_rel_tpa_rod", "em_rel_syn", "em_rel_pat_syn", "em_rel_std_syn", "em_rel_tpa_syn", "em_rel_tgn", "em_rel_std_tgn", "em_rel_unc", "em_rel_pat_unc", "em_rel_std_unc", "em_rel_vrl", "em_rel_gss_vrl", "em_rel_htg_vrl", "em_rel_pat_vrl", "em_rel_std_vrl", "em_rel_tpa_vrl", "em_rel_vrt", "em_rel_est_vrt", "em_rel_gss_vrt", "em_rel_htc_vrt", "em_rel_htg_vrt", "em_rel_pat_vrt", "em_rel_std_vrt", "em_rel_sts_vrt", "em_rel_tpa_vrt", "em_rel_est", "em_rel_gss", "em_rel_htc", "em_rel_htg", "em_rel_pat", "em_rel_std", "em_rel_sts", "em_rel_tpa", "emall", "evec", "imgtligm", "imgthla", " ", "- See Protein : http://www.ebi.ac.uk/Tools/sss/ncbiblast/help/index-protein.html#database", " ", "uniprotkb_swissprot", "uniprotkb_trembl", "uniprotkb_archaea", "uniprotkb_arthropoda", "uniprotkb_bacteria", "uniprotkb_complete_microbial_proteomes", "uniprotkb_fungi", "uniprotkb_human", "uniprotkb_mammals", "uniprotkb_nematoda", "uniprotkb_rodents", "uniprotkb_vertebrates", "uniprotkb_viridiplantae", "uniprotkb_viruses", "uniref100", "uniref100_seg", "uniref90", "uniref50", "epop", "jpop", "kpop", "uspop", "nrpl1", "nrpl2", "pdb", "sgt", "uniparc", "ipi", "intact", "imgthlap", " ", "- See Vector : http://www.ebi.ac.uk/Tools/sss/ncbiblast/help/index-vectors.html#database", " ", "emvec" }));
        //database_jComboBox.setRenderer(new BlastDBComboBoxCellRenderer());
        database_jComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                database_jComboBoxActionPerformed(evt);
            }
        });
        database_jComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                database_jComboBoxFocusLost(evt);
            }
        });

        jLabel6.setText("Database");

        jButton5.setText("?");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Others"));

        jLabel8.setText("Database Genetic code");

        db_genetic_code_jComboBox1.setModel(new DefaultComboBoxModel());
        db_genetic_code_jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                db_genetic_code_jComboBox1ActionPerformed(evt);
            }
        });

        query_genetic_codejComboBox2.setModel(new DefaultComboBoxModel());
        query_genetic_codejComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                query_genetic_codejComboBox2ActionPerformed(evt);
            }
        });

        jLabel9.setText("Query Genetic code");

        jLabel10.setText("Filter ");

        Filter_jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "L Low,", "R Human repeat, ", "M Mask for Lookup" }));
        Filter_jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter_jComboBox3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addContainerGap(379, Short.MAX_VALUE))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addContainerGap())
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addContainerGap(463, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(139, Short.MAX_VALUE)
                .addComponent(db_genetic_code_jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(141, Short.MAX_VALUE)
                .addComponent(query_genetic_codejComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(139, Short.MAX_VALUE)
                .addComponent(Filter_jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(db_genetic_code_jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(query_genetic_codejComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Filter_jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(database_jComboBox, 0, 463, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(database_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Blast output filename"));

        jButton10.setText("...");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(Filename_jTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(Filename_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton10))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 200, Short.MAX_VALUE)
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(run_jButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ClosejButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ClosejButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(run_jButton)
                    .addComponent(jButton8)
                    .addComponent(jButton6))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Blast Ncbi", jPanel9);

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
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
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
        properties.put("Status", RunProgram.status_nothing);
        properties.killThread();
}//GEN-LAST:event_jButton8ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        setDefaultValues();
}//GEN-LAST:event_jButton6ActionPerformed

    private void blastProgram_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_blastProgram_jComboBoxFocusLost
        properties.put("blastProgram", String.valueOf(this.blastProgram_jComboBox.getSelectedItem()));
    }//GEN-LAST:event_blastProgram_jComboBoxFocusLost

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
      HelpEditor help = new HelpEditor(this.frame, false, properties);
        help.setVisible(true);
}//GEN-LAST:event_jButton3ActionPerformed

    private void ExpectedTresholdjComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExpectedTresholdjComboBoxActionPerformed
        properties.put("expected", (String)this.ExpectedTresholdjComboBox.getSelectedItem());
    }//GEN-LAST:event_ExpectedTresholdjComboBoxActionPerformed

    private void SubBlastnjComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubBlastnjComboBoxActionPerformed
      properties.put("task", (String)this.SubBlastnjComboBox.getSelectedItem());
    }//GEN-LAST:event_SubBlastnjComboBoxActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
       properties.put("ungapped", this.jCheckBox1.isSelected());
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void blastProgram_jComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blastProgram_jComboBoxActionPerformed
       String program=(String)this.blastProgram_jComboBox.getSelectedItem();
       
       int index=program.indexOf(" ");
       properties.put("program",program.substring(0, index));
       program=program.substring(0, index)+".exe";

       if (program.equals("blastn.exe")) {
           this.SubBlastnjComboBox.setEnabled(true);
       } else {
           this.SubBlastnjComboBox.setEnabled(false);
           properties.remove("task");
       }
       //--Complicated function to set the right program to run
       File f=new File(properties.getExecutable());
        String executable=f.getName();
        String path=f.getAbsolutePath();
        if (path.indexOf("executable")>-1) {
            path=path.substring(path.indexOf("executable"),path.length());
        }
        path=path.replaceAll(executable, program);

            properties.setExecutable(path);

    }//GEN-LAST:event_blastProgram_jComboBoxActionPerformed

    private void ResultsjComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResultsjComboBoxActionPerformed
       String value=(String)this.ResultsjComboBox.getSelectedItem();
       if (value.equals("Default")) {
            properties.remove("num_alignments");
            properties.remove("num_descriptions");
       } else {
            properties.put("num_alignments", value);
            properties.put("num_descriptions", value);
       }
    }//GEN-LAST:event_ResultsjComboBoxActionPerformed

    private void StrandjComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StrandjComboBoxActionPerformed
        String value=(String)this.StrandjComboBox.getSelectedItem();
       if (value.startsWith("both")) {
            properties.remove("strand");
       } else {
            properties.put("strand", value);
       }
    }//GEN-LAST:event_StrandjComboBoxActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        JFileChooser jf;
        if (this.Filename_jTextField.getText().isEmpty()) {
            jf=new JFileChooser(config.getExplorerPath());
        } else {
            jf=new JFileChooser(this.Filename_jTextField.getText());
        }
        //jf.setFileFilter(new BlastDBFilter());
        int result=jf.showOpenDialog(this);
        //CAS 1: On reussi a choisir un fichier
        if (result==JFileChooser.APPROVE_OPTION) {
            //--Save new filepath
            String path=jf.getSelectedFile().getPath();
            config.setExplorerPath(path);
            config.Save();
            //--Save BlastDB
            String filename=jf.getSelectedFile().getAbsolutePath().toLowerCase();
            this.Filename_jTextField.setText(filename);
            properties.put("outfile", filename);
        }
}//GEN-LAST:event_jButton10ActionPerformed

    private void database_jComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_database_jComboBoxFocusLost
        String value=this.database_jComboBox.getSelectedItem().toString();
        if (!value.isEmpty()&&!value.startsWith("-")) properties.put("database",value);
}//GEN-LAST:event_database_jComboBoxFocusLost

    private void DUSTjCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DUSTjCheckBoxActionPerformed
        properties.put("dust",this.DUSTjCheckBox.isSelected());
    }//GEN-LAST:event_DUSTjCheckBoxActionPerformed

    private void outfmt_jComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outfmt_jComboBoxActionPerformed
       int value=this.outfmt_jComboBox.getSelectedIndex();
       if (value==0) {
            properties.remove("outfmt");
            this.outformat_jTextField1.setEnabled(false);
       } else {          
                   properties.put("outfmt", value-1);
                   this.outformat_jTextField1.setEnabled(true);
       }
    }//GEN-LAST:event_outfmt_jComboBoxActionPerformed

    private void database_jComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_database_jComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_database_jComboBoxActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
      HelpEditor help = new HelpEditor(this.frame, false, properties.get("Name"), "http://www.ncbi.nlm.nih.gov/BLAST/blastcgihelp.shtml");
      help.setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void gop_jComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gop_jComboBoxActionPerformed
       String value=this.gop_jComboBox.getSelectedItem().toString();
           if (value.equals("Default")) {
                properties.remove("gapopen");
           } else {
                properties.put("gapopen", value);
           }
    }//GEN-LAST:event_gop_jComboBoxActionPerformed

    private void gep_jComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gep_jComboBoxActionPerformed
        String value=this.gep_jComboBox.getSelectedItem().toString();
           if (value.equals("Default")) {
                properties.remove("gapextend");
           } else {
                properties.put("gapextend", value);
           }
    }//GEN-LAST:event_gep_jComboBoxActionPerformed

    private void reward_jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reward_jComboBox1ActionPerformed
        String value=this.reward_jComboBox1.getSelectedItem().toString();
           if (value.equals("Default")) {
                properties.remove("reward");
           } else {
                properties.put("reward", value);
           }
    }//GEN-LAST:event_reward_jComboBox1ActionPerformed

    private void penalty_jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_penalty_jComboBox2ActionPerformed
        String value=this.penalty_jComboBox2.getSelectedItem().toString();
           if (value.equals("Default")) {
                properties.remove("penalty");
           } else {
                properties.put("penalty", value);
           }
    }//GEN-LAST:event_penalty_jComboBox2ActionPerformed

    private void wordsize_jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordsize_jComboBox3ActionPerformed
        String value=this.wordsize_jComboBox3.getSelectedItem().toString();
           if (value.equals("Default")) {
                properties.remove("wordsize");
           } else {
                properties.put("wordsize", value);
           }
    }//GEN-LAST:event_wordsize_jComboBox3ActionPerformed

    private void db_genetic_code_jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_db_genetic_code_jComboBox1ActionPerformed
        //--Note, we assume a static non changing order...
        int index=this.db_genetic_code_jComboBox1.getSelectedIndex();
        properties.put("db_genetic_code", translator.code.get(index).getId());
}//GEN-LAST:event_db_genetic_code_jComboBox1ActionPerformed

    private void query_genetic_codejComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_query_genetic_codejComboBox2ActionPerformed
        //Query genetic code
        int index=this.query_genetic_codejComboBox2.getSelectedIndex();
        properties.put("genetic_code", translator.code.get(index).getId());
    }//GEN-LAST:event_query_genetic_codejComboBox2ActionPerformed

    private void Filter_jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Filter_jComboBox3ActionPerformed
       String value=this.Filter_jComboBox3.getSelectedItem().toString();
           if (value.equals("Default")) {
                properties.remove("filter");
           } else {
                properties.put("filter", value.charAt(0));
           }
    }//GEN-LAST:event_Filter_jComboBox3ActionPerformed

    private void percent_jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_percent_jComboBox4ActionPerformed
        String value=this.percent_jComboBox4.getSelectedItem().toString();
           if (value.equals("Default")) {
                properties.remove("percent_identity");
           } else {
                properties.put("percent_identity", value);
           }
    }//GEN-LAST:event_percent_jComboBox4ActionPerformed

    private void outformat_jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_outformat_jTextField1FocusLost
        properties.put("outfmt_string", this.outformat_jTextField1.getText());
    }//GEN-LAST:event_outformat_jTextField1FocusLost

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        BlastInfoJDialog b=new BlastInfoJDialog(frame, false);
        b.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed
  
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
           if (properties.isSet("dust")) this.DUSTjCheckBox.setSelected(properties.getBoolean("dust"));
           if (properties.isSet("database")) this.database_jComboBox.setSelectedItem(properties.get("database"));
           if (properties.isSet("wordsize")) this.wordsize_jComboBox3.setSelectedItem(properties.get("wordsize"));
           if (properties.isSet("strand")) this.StrandjComboBox.setSelectedItem(properties.get("strand"));
           if (properties.isSet("num_alignments")) this.ResultsjComboBox.setSelectedItem(properties.get("num_alignments"));
           if (properties.isSet("gapopen")) this.gop_jComboBox.setSelectedItem(properties.get("gapopen"));
           if (properties.isSet("gapextend")) this.gep_jComboBox.setSelectedItem(properties.get("gapextend"));
           if (properties.isSet("expected")) this.ExpectedTresholdjComboBox.setSelectedItem(properties.get("expected"));
           if (properties.isSet("percent_identity")) this.percent_jComboBox4.setSelectedItem(properties.get("percent_identity"));
           if (properties.isSet("outfmt")) {
               this.outfmt_jComboBox.setSelectedIndex(properties.getInt("outfmt")+1);
               this.outformat_jTextField1.setEnabled(true);
           }
           if (properties.isSet("genetic_code")) {
               int code=properties.getInt("genetic_code");              
               this.query_genetic_codejComboBox2.setSelectedIndex(translator.getIndex(code));
           }
           if (properties.isSet("db_genetic_code")) {
               int code=properties.getInt("db_genetic_code");              
               this.db_genetic_code_jComboBox1.setSelectedIndex(translator.getIndex(code));
           }

           if (properties.isSet("filter")) {
              String filter=properties.get("filter");
              if (filter.equals("L")) this.Filter_jComboBox3.setSelectedIndex(1);
              if (filter.equals("R")) this.Filter_jComboBox3.setSelectedIndex(2);
              if (filter.equals("M")) this.Filter_jComboBox3.setSelectedIndex(3);
           }
           //--TASK and Program
//           System.out.println(properties);
//           String program=(String)this.blastProgram_jComboBox.getSelectedItem();
//
//       int index=program.indexOf(" ");
//       properties.put("program",program.substring(0, index));
//       program=program.substring(0, index)+".exe";
//
//       if (program.equals("blastn.exe")) {
//           this.SubBlastnjComboBox.setEnabled(true);
//       } else {
//           this.SubBlastnjComboBox.setEnabled(false);
//           properties.remove("task");
        if (properties.isSet("blastProgram")) {
            this.blastProgram_jComboBox.setSelectedItem(properties.get("blastProgram"));
        }      

           if (properties.isSet("outfmt_string")) {
            this.outformat_jTextField1.setText(properties.get("outfmt_string"));
        }

        if (properties.isSet("task")) {
            this.SubBlastnjComboBox.setSelectedItem(properties.get("task"));
            this.SubBlastnjComboBox.setEnabled(true);
        } else {
            this.SubBlastnjComboBox.setEnabled(false);        
        }
           //       }
//           
     }

    ///////////////////////////////////////////////////////////////////////////
    /// DISPLAY MAIN FUNCTION

    public void display(workflow_properties properties) {        
        initComponents();
        setIconImage(Config.image);
        this.setTitle("Blast configuration");
        //if (properties.isSet("Description")) this.Notice.setText(properties.get("Description"));
        this.properties=new workflow_properties();//--Hack so we don't interfer with new properties
        DefaultComboBoxModel model=(DefaultComboBoxModel) this.query_genetic_codejComboBox2.getModel();
        DefaultComboBoxModel model2=(DefaultComboBoxModel) this.db_genetic_code_jComboBox1.getModel();
        for (GeneticCode code:Translator.code) {
            model.addElement(code.getName());
            model2.addElement(code.getName());
        }
        this.query_genetic_codejComboBox2.setModel(model);
        this.db_genetic_code_jComboBox1.setModel(model2);        
        DefaultComboBoxModel cbModel = new DefaultComboBoxModel();
        for (int i=0;i<BlastEditor.treshold_desc.length;i++) cbModel.addElement(this.treshold_desc[i]);
        this.ExpectedTresholdjComboBox.setModel(cbModel);
       
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
    private javax.swing.JCheckBox DUSTjCheckBox;
    private javax.swing.JComboBox ExpectedTresholdjComboBox;
    private javax.swing.JTextField Filename_jTextField;
    private javax.swing.JComboBox Filter_jComboBox3;
    private javax.swing.JTextField NamejTextField;
    private javax.swing.JComboBox ResultsjComboBox;
    private javax.swing.JComboBox StrandjComboBox;
    private javax.swing.JComboBox SubBlastnjComboBox;
    private javax.swing.JComboBox blastProgram_jComboBox;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox database_jComboBox;
    private javax.swing.JComboBox db_genetic_code_jComboBox1;
    private javax.swing.JComboBox gep_jComboBox;
    private javax.swing.JComboBox gop_jComboBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox outfmt_jComboBox;
    private javax.swing.JTextField outformat_jTextField1;
    private javax.swing.JComboBox penalty_jComboBox2;
    private javax.swing.JComboBox percent_jComboBox4;
    private javax.swing.JComboBox query_genetic_codejComboBox2;
    private javax.swing.JComboBox reward_jComboBox1;
    private javax.swing.JButton run_jButton;
    private javax.swing.JComboBox wordsize_jComboBox3;
    // End of variables declaration//GEN-END:variables



}
