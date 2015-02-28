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


package database; 

 
import configuration.Config;
import java.awt.Color;  
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.ResultSet;  
import javax.swing.JFileChooser;

public class DatabaseQueryFrame extends javax.swing.JDialog {

    ////////////////////////////////////////////////////////////////////////////
    /// Variables

    public static databaseFunction df=new databaseFunction();
 
    Config config=new Config(); 
     //Workbox workbox=new Workbox();      //--Debug;
 
    /** Creates new form DatabaseQuery */
    public DatabaseQueryFrame() {
        initComponents();
        this.setTitle("Database Query");
        this.setIconImage(Config.icon.getImage());
        this.setResizable(true);      
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = getSize();
        setLocation((screenSize.width-d.width)/2,(screenSize.height-d.height)/2);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel22 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jButton7 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        query_jTextArea = new javax.swing.JTextArea();
        jPanel24 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel27 = new javax.swing.JPanel();
        jButton11 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jStatusMessage = new javax.swing.JTextField();

        jPanel22.setBackground(new java.awt.Color(255, 255, 255));

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));
        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder("Enter a SQLite (SQL92) query..."));

        jButton7.setText("Execute");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        query_jTextArea.setColumns(20);
        query_jTextArea.setRows(5);
        jScrollPane1.setViewportView(query_jTextArea);

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addComponent(jButton7)
                .addContainerGap(45, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
        );

        jPanel24.setBackground(new java.awt.Color(255, 255, 255));
        jPanel24.setBorder(javax.swing.BorderFactory.createTitledBorder("Results"));

        jTable2.setAutoCreateRowSorter(true);
        jTable2.setModel(new QueryTableModel());
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jTable2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable2KeyPressed(evt);
            }
        });
        jScrollPane4.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
        );

        jPanel27.setBackground(new java.awt.Color(255, 255, 255));
        jPanel27.setBorder(javax.swing.BorderFactory.createTitledBorder("Quick access functions"));

        jButton11.setText("Save Results");
        jButton11.setToolTipText("This will save the current results to a text file (tab-separated values).");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton8.setText("View available tables");
        jButton8.setToolTipText("This will list the current database tables.");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton3.setText("Clean Up");
        jButton3.setToolTipText("This will clean-up the database of unused records.");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                .addContainerGap(237, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                .addComponent(jButton8)
                .addComponent(jButton3))
        );

        jStatusMessage.setBorder(null);
        jStatusMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStatusMessageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jStatusMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jStatusMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        String query=this.query_jTextArea.getText();                
        ExecuteQuery(query);
}//GEN-LAST:event_jButton7ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        if (evt.getClickCount()<2) return;
        QueryTableModel tm=(QueryTableModel)this.jTable2.getModel();
        if (tm.getColumnCount()>0 && tm.qualifier[0].equals("TableName")) {
            String TableName=(String) tm.getValueAt(this.jTable2.getSelectedRow(), 0);
            String query="SELECT *  FROM "+TableName+";";
            this.query_jTextArea.setText(query);
            ExecuteQuery(query);
            Message("This is a list of all data in table "+TableName);
        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void jTable2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable2KeyPressed
        // TODO add your handling code here:
}//GEN-LAST:event_jTable2KeyPressed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        JFileChooser jf=new JFileChooser();
        String tmpfile="";
        jf.setName("Saving list...");
        int result=jf.showSaveDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            tmpfile=jf.getSelectedFile().getAbsolutePath();
            QueryTableModel ql=(QueryTableModel)this.jTable2.getModel();
            if (ql.saveData(tmpfile)) {
                Message("Save successfull of data to "+tmpfile);
            } else {
                MessageError("Unable to save data to "+tmpfile);
            }
        }
}//GEN-LAST:event_jButton11ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        String query="SELECT name AS TableName FROM sqlite_master WHERE type = 'table';";
        this.query_jTextArea.setText(query);
        ExecuteQuery(query);
        Message("This is a list of all available tables in database " + df.getDatabase().dbFileName);
}//GEN-LAST:event_jButton8ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
       
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jStatusMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStatusMessageActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_jStatusMessageActionPerformed

    //////////////////////////////////////////////////////////////////////////
    /// TAB Database Query
     /**
     * This execute a SQL query and display the result in jTable2
     * @param query
     */
    void ExecuteQuery(String query) {
       if (!query.equals("")) {
           System.out.println(query);
           try {
           if (query.lastIndexOf(';')!=query.length()-1) query+=";";
            Message("Query of database for "+query);
            Config.log(query);
            ResultSet rs=null;
                rs=df.getDatabase().executeQuery(query);
              //if (!sp.isCancel()) {
                  if (rs!=null) {
                      QueryTableModel qt=(QueryTableModel)this.jTable2.getModel();
                      qt.SQLquery=query;
                      qt.setData(rs);
                      qt.fireTableStructureChanged();
                      qt.fireTableDataChanged();
                      this.jTable2.setModel(qt);
                      Message("Found "+qt.getSize()+" results");
                  }  else if (!df.getDatabase().dbSQLerror.equals("")){
                     MessageError("Error with "+query+" "+df.getDatabase().dbSQLerror);
                  } else {
                      MessageError("No results for query "+query);
                  }
              //} else { Message("Cancelled query for "+query, jInfo);}
            } catch(Exception e) {
               e.printStackTrace();
               MessageError("Error in query of database...");
            }             
        } //End if query=""

    }


    ////////////////////////////////////////////////////////////////////////////
    /// HELPER FUNCTION

            /**
     * Affiche un message dans la status bar
     * La provenance peut être mise dans un tooltip
     * @param text Le texte
     * @param tooltip Le tooltip texte
     */
    void Message(String text) {
        this.jStatusMessage.setForeground(new java.awt.Color(0, 51, 153));
        this.jStatusMessage.setText(text);
    }
    
    void MessageError(String txt) {
        this.jStatusMessage.setForeground(Color.RED);
        this.jStatusMessage.setText(txt);
    }
  

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jStatusMessage;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea query_jTextArea;
    // End of variables declaration//GEN-END:variables

}