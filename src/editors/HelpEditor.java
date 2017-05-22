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

import configuration.Config;
import configuration.Util;
import database.databaseFunction;
import editor.propertiesEditorBox;
import editor.propertiesEditorSmallWorkflowJDialog;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import results.report;
import tools.Toolbox;
import workflows.Workbox;
import workflows.workflow_properties;

/**
 * This display an Help Dialog Box and can load HTML files
 * @author Mickael Leclercq
 * @since 2009, 2011
 */
public class HelpEditor extends javax.swing.JDialog implements HyperlinkListener {
    
    
    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES
    
    java.awt.Frame frame;           //--Main windows Frame...
    workflow_properties properties; //--properties to generate help for...
    Config config=new Config();
    
    
    ////////////////////////////////////////////////////////////////////////////
    /// Constructor
    
    /**
     * Main constructor
     * @param parent
     * @param modal : should be false
     * @param title : title of the page
     * @param page : help page (URL) or help text (String)
     * @param textType : true for an URL, false for a String
     */
    @Deprecated
    public HelpEditor(java.awt.Frame parent, boolean modal, String title, String page) {
        super(parent, modal);
        this.frame=parent;
        initComponents();
        //--1. Test if we have it in data path... display this one
        
//        if (Util.FileExists(config.dataPath()+File.separator+"help"+File.separator+properties.getName()+".html")) {
//            try {
//                jEditorPane1.setText("Loading...");
//                jEditorPane1.setPage(config.dataPath()+File.separator+"help"+File.separator+properties.getName()+".html");
//            } catch (IOException e) {
//              Config.log("Error: Unable to display internal help in Help browser...\n"+e.getMessage());
//            }
//            this.URL_JTextField.setText(page);
//        } else
        //--Case 2: outside html...
        if (page.toLowerCase().startsWith("http")) {
            try {
                jEditorPane1.setText("Loading...");
                jEditorPane1.setPage(page);
            } catch (IOException e) {
                Config.log("Error: Unable to display "+page+" in Help browser...\n"+e.getMessage());
                jEditorPane1.setText("Unable to find help...");
            }
            this.URL_JTextField.setText(page);
        } else if (page.equals(workflow_properties.NotSet)) {
            jEditorPane1.setText("No help found...");
//            HTMLEditorKit kit=(HTMLEditorKit) jEditorPane1.getEditorKit();
//            //new HTMLEditorKit.FontSizeAction(String.valueOf(fontSizes[i]), fontSizes[i])
//            kit.getStyleSheet().setBaseFontSize(+7);
            //this.URL_JTextField.setText(properties.get("Website"));
        } else if (Util.FileExists(page)) {
            try {
                jEditorPane1.setText("Loading...");
                jEditorPane1.setPage(new File(page).toURI().toURL());
            } catch (IOException e) {
                Config.log("Error: Unable to display "+page+" in Help browser...\n"+e.getMessage());
                jEditorPane1.setText("Unable to find help...");
            }
        } else {
            try {
                jEditorPane1.setText(page);
            } catch (Exception e) {}
            this.URL_JTextField.setText("local help");
        }
        this.setTitle(title+" help");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = getSize();
        this.setAlwaysOnTop(true);
        setLocation((screenSize.width-d.width)/2,(screenSize.height-d.height)/2);
    }
    /**
     * This is the new way to load the help...
     * @param parent
     * @param modal : should be false
     * @param title : title of the page
     * @param properties : workflow_properties to display help
     * @param workflow : this is the propertiesEditorSmallWorkflowJDialog workflow to display...
     */
    public HelpEditor(java.awt.Frame parent, boolean modal,workflow_properties properties) {
        super(parent, modal);
        this.frame=parent;
        this.properties=properties;
        initComponents();
        //--1. Test if we have it in data path... display this one
        jEditorPane1.addHyperlinkListener(this);
        String page=properties.get("Name");
        if (Util.FileExists(config.dataPath()+File.separator+"help"+File.separator+properties.getName()+".html")) {
            try {
                jEditorPane1.setText("Loading...");
                jEditorPane1.setPage(new File(config.dataPath()+File.separator+"help"+File.separator+properties.getName()+".html").toURI().toURL());
            } catch (IOException e) {
                Config.log("Error: Unable to display internal help in Help browser...\n"+e.getMessage());
            }
            this.URL_JTextField.setText(page);
        } else if (page.toLowerCase().startsWith("http")) {
            try {
                jEditorPane1.setText("Loading...");
                jEditorPane1.setPage(page);
            } catch (IOException e) {
                Config.log("Error: Unable to display "+page+" in Help browser...\n"+e.getMessage());
                jEditorPane1.setText("Unable to find help...");
            }
            this.URL_JTextField.setText(page);
        } else if (page.equals(workflow_properties.NotSet)) {
            jEditorPane1.setText("No help found...");
//            HTMLEditorKit kit=(HTMLEditorKit) jEditorPane1.getEditorKit();
//            //new HTMLEditorKit.FontSizeAction(String.valueOf(fontSizes[i]), fontSizes[i])
//            kit.getStyleSheet().setBaseFontSize(+7);
            //this.URL_JTextField.setText(properties.get("Website"));
        } else if (Util.FileExists(page)) {
            try {
                jEditorPane1.setText("Loading...");
                jEditorPane1.setPage(new File(page).toURI().toURL());
            } catch (IOException e) {
                Config.log("Error: Unable to display "+page+" in Help browser...\n"+e.getMessage());
                jEditorPane1.setText("Unable to find help...");
            }
        } else {
            try {
                jEditorPane1.setText(page);
            } catch (Exception e) {}
            this.URL_JTextField.setText("local help");
        }
        this.setTitle(properties.getName()+" help");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = getSize();
        this.setAlwaysOnTop(true);
        setLocation((screenSize.width-d.width)/2,(screenSize.height-d.height)/2);
    }
    
    
    /**
     * See the propertiesEditorSmallWorkflowJDialog class for the most recent...
     * @param dir
     * @return the filename of the resulting file...
     * @deprecated
     */
    @Deprecated
    public String createHTMLHelpForProgram(String dir) {
        report re=new report(); //--Needed later
        //--Create the images in the temp directory
        Config config=new Config();
        propertiesEditorSmallWorkflowJDialog smlEditor=new propertiesEditorSmallWorkflowJDialog(frame,properties, "temporary");
        smlEditor.workflow.saveImage(dir+File.separator+properties.getName()+properties.getName()+".png");
        propertiesEditorBox propertieseditorbox = new propertiesEditorBox(frame);
        propertieseditorbox.display(properties, smlEditor.workflow);
        propertieseditorbox.saveImage(dir+File.separator+properties.getName()+properties.getName()+"_editor.png");
        
        Util u=new Util(dir+File.separator+properties.getName()+".html");
        
        StringBuilder st=new StringBuilder();
        
        st.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
        st.append("<html>");
        //--Head
        st.append("<head>");
        st.append("<meta content=\"text/html; charset=ISO-8859-1\" http-equiv=\"content-type\">");
        st.append("<title>Reference | ");
        st.append(properties.getName());
        st.append("</title></head>");
        //--Body
        st.append("<body>");
        //--Body Title
        st.append("<big style=\"font-weight: bold; font-family: Courier New,Courier,monospace;\"><big><big><span style=\"color: rgb(102, 102, 102);\">Reference Armadillo v1.0 </span></big></big></big><br><br>");
        //--Table
        st.append("<table style=\"text-align: left; width: 800px; height: 300px;\" border=\"0\" cellpadding=\"2\" cellspacing=\"2\">");
        //--Applications
        st.append("<tbody><tr><td></td><td style=\"width: 215px;\">Name</td><td style=\"width: 1004px;\"><big><span style=\"font-weight: bold;\">"+properties.getName()+"</span></big></td></tr>");
        //--Image
        st.append("<tr><td></td><td>View</td><td><span style=\"font-family: Courier New,Courier,monospace;\"><img src=\""+properties.getName()+".png"+"\"></img></span></td></tr>");
        //--Description
        st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Description</td><td style=\"width: 1004px; align:justify \">"+properties.getDescription()+"<br><br></td></tr>");
        //--Input
        st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Input (<strong>"+properties.Inputed().size()+"</strong>)</td><td style=\"width: 215px; vertical-align: top;\">");
        for (String str:properties.Inputed()) {
            st.append(str+"<br>");
        }
        
        st.append("</td></tr>");
        //Output
        st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Output (<strong>"+properties.Outputed().size()+"</strong>)</td><td style=\"width: 215px; vertical-align: top;\">");
        for (String str:properties.Outputed()) {
            st.append(str+"<br>");
        }
        st.append("</td></tr>");
        //--Control (image)
        st.append("<tr><td></td><td style=\"vertical-align: top;\">Control</td><td><br><img src=\""+properties.getName()+"_editor.png\"></img><br>");
        //--Syntaxe
        //st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Syntaxe</td><td style=\"width: 1004px;\"><small><span style=\"font-weight: bold;\">saveStrings(const char* filename, string *array);<br></span></small></td></tr>");
        //--Parametert
        //st.append("<tr><td></td><td>Supported parameters</td><td><small><br></small></td></tr><tr><td></td><td style=\"width: 215px; vertical-align: top;\">Return <br></td><td style=\"width: 1004px;\"><small>true if success<br></small></td></tr>");
        
        //--Image
        //st.append("<tr><td></td><td>Exemple application to build</td><td><small><span style=\"font-family: Courier New,Courier,monospace;\"><img src=\""+properties.getName()+".png"+"\"></img></span></small></td></tr>");
        //--Related
        st.append("<tr><td></td><td style=\"width: 215px; vertical-align: top;\">Related</td><td style=\"width: 1004px;\">");
        //st.append("<a href=\"loadStrings.html\">loadStrings</a><br>");
        st.append("</td></tr>");
        st.append("</tbody></table><br>");
        st.append("</body>");
        st.append("</html>");
        u.println(st.toString());
        u.close();
        return u.log_filename;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        URL_JTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jEditorPane1.setEditable(false);
        jEditorPane1.setText("Loading help...");
        jScrollPane1.setViewportView(jEditorPane1);

        URL_JTextField.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        URL_JTextField.setText("URL:");
        URL_JTextField.setBorder(null);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 882, Short.MAX_VALUE)
            .addComponent(URL_JTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 882, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(URL_JTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField URL_JTextField;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
    
//-- Etienne
//    public void hyperlinkUpdate(HyperlinkEvent e) {
//     if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
//         System.out.println("Trying "+e.getURL());
//      try {
//        this.jEditorPane1.setPage(e.getURL());
//      } catch(Exception ex) {}
//      }
//    }
    
    /**
     * Code from http://blogs.oracle.com/scblog/entry/tip_displaying_rich_messages_using
     * Copyright :
     * TO DO: Implement all the options: follow db, follow local in the editorPane...
     * @since October 2011
     *
     * @param e
     */
    public void hyperlinkUpdate(final HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    SwingUtilities.getWindowAncestor(jEditorPane1).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    try {
                        String filename=e.getURL().toString();
                        File f=new File(filename);
                        
                        filename=config.dataPath()+File.separator+"help"+File.separator+f.getName();
                        if (filename.endsWith(".db")&&Util.FileExists(filename)) {
                            jEditorPane1.setToolTipText("<html>Load Sample workflow.<br><b>Warning. Will close the current workflow...</b></html>");
                        } else
                            jEditorPane1.setToolTipText(e.getURL().toExternalForm());
                    } catch(Exception e){}
                }
            });
        } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    SwingUtilities.getWindowAncestor(jEditorPane1).setCursor(Cursor.getDefaultCursor());
                    jEditorPane1.setToolTipText(null);
                }
            });
        } else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                String filename=e.getURL().toString();
                
                File f=new File(filename);
                filename=config.dataPath()+File.separator+"help"+File.separator+f.getName();
                
                //--CASE 1. We have a workflow? (.db file)
                if (filename.endsWith(".db")&&Util.FileExists(filename)) {
                    //--We limit ourselves to our local directory
                    //String filename=config.currentPath()+File.separator+"examples"+File.separator+command;
                    String destination=config.projectsDir()+File.separator+f.getName();
                    int count=0;
                    while(Util.FileExists(destination)) {
                        destination=config.projectsDir()+File.separator+(count++)+f.getName();
                    }
                    //filename=config.dataPath()+File.separator+"help"+File.separator+f.getName();
                    //--Open the workflow
                    try {
                        Util.copy(filename, destination);
                    } catch(Exception e2) {
                        Config.log("Unable to copy workflow (.db) - "+filename+" to "+destination);
                    }
                    //--Open the workflow
                    Config.log("Loading the workflow "+destination);
                    databaseFunction df=new databaseFunction();
                    Workbox workbox=new Workbox();
                    if (df.Open(destination)) {
                        Toolbox toolbox=new Toolbox();
                        toolbox.reloadDatabaseTree();
                        int workflow_id=df.getNextWorkflowsID()-1;
                        if (workflow_id>0) {
                            workbox.loadWorkflowFromDatabase(workflow_id);
                            //workbox.getCurrentArmadilloWorkflow().setName(workflowname);
                            workbox.getCurrentArmadilloWorkflow().setName(destination);
                            //--Enable saving to the Save-Menu
                            //this.Save_jMenuItem.setEnabled(true);
                            config.setLastWorkflow(destination);
                            config.Save();
                        }
                        //--Close the help
                        this.setVisible(false);
                        frame.setTitle(config.get("applicationName")+" "+config.get("version")+" - "+destination);
                    } else {
                        Config.log("Unable to open workflow (.db) - "+destination);
                    }
                    
                    
//                            workbox.loadProject(filename);
                } else
                    //--CASE 2. We have a local file... (Armadillo help)
                    if (filename.startsWith("file:")) {
                        //--Open the file in the editorPane
                        try {
                            jEditorPane1.setPage(e.getURL());
                        } catch(Exception ex1) { Config.log("Error following "+e.getDescription()+" (ex1) in browser.");}
                    } else
                        //--CASE 3. We have html file? (We try to follow the link in an outside browser...
                        if (filename.startsWith("http")) {
                            //--We have an extern url: try to open in Browser (safer...)
                            if (Desktop.isDesktopSupported()) {
                                try {
                                    Desktop.getDesktop().browse(e.getURL().toURI());
                                } catch (Exception ex3) { Config.log("Error following "+e.getDescription()+" (ex3) in browser.");}
                            } else {
                                //--Not able to open in the outside... Display the link for Cut&Paste...
                            }
                        }
            } catch(Exception ex2) {
                Config.log("Error following "+e.getDescription()+" (ex2) in browser.");
            }
        }
    }
    
    /**
     *
     * @param filename
     */
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
    
}
