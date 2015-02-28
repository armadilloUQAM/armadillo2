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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DatabaseExplorerDisplayDialog.java
 *
 * Created on 15 juil. 2009, 16:14:40
 */

package database;

import configuration.Config;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JTextField;
import workflows.workflow_properties;

/**
 *
 * @author Leclercq Mickael
 */
public class DatabaseExplorerDisplayDialog extends javax.swing.JDialog {
    Frame  frame;
    String tableName = null;
    ArrayList<String> tables = null;

    String[] ids = {"program_id", "runProgram_id","sequence_id","sequenceStats_id",
    "genbankfile_id","MultipleSequences_id",
    "alignment_id","alignment_id","tree_id","Alignment_id","ancestor_id","tree_id","properties_id",
    "properties_id","workflows_id","objects_id","connectors_id"};

    workflow_properties data=new workflow_properties();
    databaseFunction df = new databaseFunction();
    static int x=10;
    static int y=10;

    /** Creates new form DatabaseExplorerDisplayDialog */
    public DatabaseExplorerDisplayDialog(java.awt.Frame parent, boolean modal, ResultSet rs) {
        super(parent, modal);
        this.setIconImage(Config.icon.getImage());
        frame = parent;
        initComponents();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = getSize();
        setLocation((screenSize.width-d.width)/x,(screenSize.height-d.height)/y);
        x-=1;y-=1;
        if (x==0||y==0){x=9;y=9;}
        try {
            ResultSetMetaData rsmd=rs.getMetaData();
            int colNumber = rsmd.getColumnCount();
            tableName = rsmd.getTableName(1);
            jTextField1.setText(rsmd.getColumnName(1)+" "+rs.getString(1));
            for (int i=1;i<colNumber+1;i++) {                
                String value=rs.getString(i);
                value=(value==null?"":value);
                data.put(rsmd.getColumnName(i),value);                
            }
        } catch(Exception e) {
            jTextField1.setText("Nothing in Database for selected item");
        }
        tables = df.getTables();
        DisplayDialogTableModel tm = (DisplayDialogTableModel) jTable1.getModel();
        tm.setData(data);
        tm.fireTableStructureChanged();
        jTable1.setModel(tm);
    }


    public void updateDatabase(){
        MessageErreur("Updating...", "", this.infos_jTextField);
        for (int i=0; i<tables.size(); i++) {
            if (tables.get(i).equals(tableName)){
                String idstr = ids[i];
                String id = data.get(idstr);
                Set<String> keys = data.stringPropertyNames();
                for (String key : keys) {
                    if (!key.equals(idstr)) {
                        String S = String.format("UPDATE %s SET %s='%s' WHERE %s='%s'",
                                tableName, key, data.get(key), idstr, id);                        
                        df.execute(S);
                    }
                }
                if (id.matches("Not Set")){
                    MessageErreur("Error during Update!", "", this.infos_jTextField);
                } else this.Message("Informations updated!", "", this.infos_jTextField);
            }
        }
        
    }


        /**
     * Affiche un message dans la status bar
     * La provenance peut être mise dans un tooltip
     * @param text Le texte
     * @param tooltip Le tooltip texte
     */
    void Message(String text, String tooltip, JTextField jStatusMessage) {
        jStatusMessage.setForeground(new java.awt.Color(0, 51, 153));
        jStatusMessage.setToolTipText(tooltip);
        jStatusMessage.setText(text);
    }

    /**
     * Affiche un message d'erreur en rouge dans la status bar
     * La provenance peut être mise dans un tooltip
     * @param text Le texte
     * @param tooltip Le tooltip texte
     */
    void MessageErreur(String text, String tooltip, JTextField jStatusMessage) {
        jStatusMessage.setForeground(Color.RED);
        jStatusMessage.setToolTipText(tooltip);
        jStatusMessage.setText(text);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        close_jButton = new javax.swing.JButton();
        updateDatabase_jButton = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        infos_jTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Details");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel8.setText("Information");

        jTextField1.setEditable(false);
        jTextField1.setText("Sequence 1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addGap(58, 58, 58))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        close_jButton.setText("Close");
        close_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_jButtonActionPerformed(evt);
            }
        });

        updateDatabase_jButton.setText("Update database");
        updateDatabase_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateDatabase_jButtonActionPerformed(evt);
            }
        });

        jButton3.setText("Save as...");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 135, Short.MAX_VALUE)
                .addComponent(updateDatabase_jButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(close_jButton))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(close_jButton)
                .addComponent(updateDatabase_jButton)
                .addComponent(jButton3))
        );

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new DisplayDialogTableModel());
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        infos_jTextField.setBackground(new java.awt.Color(236, 233, 216));
        infos_jTextField.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(infos_jTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                        .addContainerGap())))
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infos_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void close_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_jButtonActionPerformed
        this.setVisible(false);
}//GEN-LAST:event_close_jButtonActionPerformed

    private void updateDatabase_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateDatabase_jButtonActionPerformed
        MessageErreur("Updating...", "", this.infos_jTextField);
        updateDatabase();
    }//GEN-LAST:event_updateDatabase_jButtonActionPerformed

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            updateDatabase();
        }
    }//GEN-LAST:event_jTable1KeyPressed


    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ResultSet rs =null;
                DatabaseExplorerDisplayDialog dialog = new DatabaseExplorerDisplayDialog(new javax.swing.JFrame(), true, rs);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton close_jButton;
    private javax.swing.JTextField infos_jTextField;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton updateDatabase_jButton;
    // End of variables declaration//GEN-END:variables

}
