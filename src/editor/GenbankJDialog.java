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

/**
 * Simple dialog to show each program ouput
 * @author Etienne Lord, Mickael Leclercq, François-Christophe Marois-Blanchet
 * @since May 2009
 */
import configuration.Config;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.*;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.ListModel;

public class GenbankJDialog extends javax.swing.JDialog {
    static Vector <String> msg=new Vector<String>();


    public GenbankJDialog() {}

    /** Creates new form OutputJDialog */
    public GenbankJDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        this.jList1.setListData(msg);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = getSize();
        setLocation((screenSize.width-d.width)/2,
					(screenSize.height-d.height)/2);
        setVisible(true);
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
        jPanel3 = new javax.swing.JPanel();
        Done_JButton = new javax.swing.JButton();
        SaveSearch_JButton = new javax.swing.JButton();
        OutputjPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        Done_JButton.setText("Done");
        Done_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Done_JButtonActionPerformed(evt);
            }
        });

        SaveSearch_JButton.setText("Save Output Text");
        SaveSearch_JButton.setToolTipText("Save current displayed results to a text file");
        SaveSearch_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveSearch_JButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SaveSearch_JButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 424, Short.MAX_VALUE)
                .addComponent(Done_JButton, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(Done_JButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(SaveSearch_JButton))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 717, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 72, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        OutputjPanel.setBackground(new java.awt.Color(255, 255, 255));
        OutputjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Genbank"));

        jList1.setModel(new DefaultListModel());
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout OutputjPanelLayout = new javax.swing.GroupLayout(OutputjPanel);
        OutputjPanel.setLayout(OutputjPanelLayout);
        OutputjPanelLayout.setHorizontalGroup(
            OutputjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(OutputjPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                .addContainerGap())
        );
        OutputjPanelLayout.setVerticalGroup(
            OutputjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(OutputjPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(OutputjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(OutputjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Done_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Done_JButtonActionPerformed
       this.setVisible(false);
}//GEN-LAST:event_Done_JButtonActionPerformed

    private void SaveSearch_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveSearch_JButtonActionPerformed
        JFileChooser jf=new JFileChooser();

        int result=jf.showOpenDialog(this);
            //CAS 1: On reussi a choisir un fichier
        if (result==JFileChooser.APPROVE_OPTION) {
            File file=jf.getSelectedFile();
            try {
                PrintWriter pw=new PrintWriter(new FileWriter(file));
                for (String s:msg) pw.println(s);
                pw.flush();
                pw.close();
            } catch(Exception e) {System.err.println("Error in saving logfile to "+file.getAbsolutePath());}
            Config.log("Save succesfull of logfile to "+file.getName());
        }
}//GEN-LAST:event_SaveSearch_JButtonActionPerformed

    /**
     * Add a String of text to the Output text
     * @param t
     */
    public void AddText(String t) {
        msg.add(t);
        
    }

    /**
     * Clear the Output text
     */
    public void ClearText() {
        msg.clear();
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Done_JButton;
    private javax.swing.JPanel OutputjPanel;
    private javax.swing.JButton SaveSearch_JButton;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}