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
import biologic.HTML;
import biologic.ImageFile;
import configuration.Config;
import configuration.Util;
import editors.BiologicEditor;
import editors.ForEditor;
import editors.UndefinedEditorBiologic;
import editors.defaultEditor;
import editors.loadGenomeEditor;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import workflows.armadillo_workflow;
import workflows.workflow_properties;
import workflows.workflow_properties_dictionnary;

/**
 * This is a container to the propertiesEditorWorkflowDialog to ensure there is only one
 *  IT IS ALSO USE TO RUN ANY CUSTOM EDITOR!!!
 * @author Etienne Lord
 * @since July 2009
 */
public class propertiesEditorBox {

    ///////////////////////////////////////////////////////////////////////////
    /// VARIABLES

    public static workflow_properties_dictionnary dict=new workflow_properties_dictionnary();
    public static Frame frame;        //--parent frame requiered to run dialog
    static defaultEditor defaulteditor;
    public Object editor=null;        //--current displayed editor 
    Config config=new Config();

    ////////////////////////////////////////////////////////////////////////////
    /// Constructor
   
    public propertiesEditorBox(Frame frame) {
        propertiesEditorBox.frame=frame;        
     }

    /**
     * This is the DEFAULT constructor called by instance
     */
    public propertiesEditorBox() {}

    public void display(workflow_properties properties, armadillo_workflow parent_workflow) {        
        // CASE 1. This class is initialized!!!
        if (frame!=null) {
            //--Is it Running
            if (properties.getThread()!=null) {
//                Workbox work=new Workbox();
//                work.addOutput(((runningThreadInterface)properties.getThread()).getOutput().toString());
//                return;
            }
            if (config.getBoolean("debugEditor")) {
                RunDefaultEditor(properties,parent_workflow);
                return;
            }
            // Loop?
            if (properties.get("ObjectType").equals("Program")&&properties.isSet("ForObjectID")) {
                 ForEditor bio=new ForEditor(frame, parent_workflow);
                 bio.display(properties);
            } else
            //--Verify if a new editor is set for this properties
            if (properties.isSet("EditorClassName")) {
                // CASE 2. We have a new editor set and its valid -> We try to run it
                if (dict.isValidValue("EditorClassName",properties.get("EditorClassName"))) {
                try {
                    Classdata classdata=new Classdata(properties.get("EditorClassName"));
                    // CASE 2.1 -> Error, we are unable to initialize it!
                       if (classdata==null) {
                            ErrorRunDefaultEditor(properties,parent_workflow);
                            return;
                          }
                          // Call the first constructor! This MUST be unique!
                          // AND in the form public foo(java.awt.Frame frame)...
                          Constructor C=classdata.constructor[0];
                          editor=C.newInstance(frame, parent_workflow);
                          if (getEditor() instanceof EditorInterface) {
                              //--Object changed-- Delete current output
                              //--And reset object status
                             //-- properties.removeOutput();
                              //--Remove for Mikael,
                              //properties.remove("Status");
                              //parent_workflow.workflow.updateDependance();
                              //--
                              ((EditorInterface)getEditor()).display(properties);                           
                               Util.CleanMemory();
                            return;
                          }
                   //CASE 2.2 ->Big Error -> Run Default..
                } catch(Exception e) {e.printStackTrace();ErrorRunDefaultEditor(properties, parent_workflow);}
                } else {
                  //CASE 3. Class name is not valid...
                    ErrorRunDefaultEditor(properties,parent_workflow);
                    return;
                }
                // CASE 4. No editor Set.. Run default
            } else {
                if (properties.get("ObjectType").equals("Output")||properties.get("ObjectType").equals("OutputDatabase")) {
                    String outputType=properties.get("outputType");
                    String ObjectType=properties.get("ObjectType");
                    int id=properties.getOutputID(outputType);
                    //--Genomre 
                    if (properties.get("outputType").equals("Genome")) {
                         loadGenomeEditor bio=new loadGenomeEditor(frame, parent_workflow);
                          bio.display(properties);                                                      
//                            ImageEditor align=new ImageEditor(frame, parent_workflow);
//                            align.display(properties);
                     } else                    
                    if (ObjectType.equals("OutputDatabase")&&id==0) {
                        UndefinedEditorBiologic editor=new UndefinedEditorBiologic(frame, parent_workflow);
                        editor.display(properties);                        
                    } else {
                        //--Alignment editor -- Moved to Right click only for speed
//                        if (properties.get("outputType").equals("Alignment")||properties.get("outputType").equals("MultipleSequences")||properties.get("outputType").equals("Sequence")) {
//                            AlignmentEditor align=new AlignmentEditor(frame, parent_workflow);
//                            align.display(properties);
//                        } else
                            if (properties.get("outputType").equals("ImageFile")||properties.get("outputType").equals("HTML")) {
                                DisplayInBrowser(properties);                            
//                            ImageEditor align=new ImageEditor(frame, parent_workflow);
//                            align.display(properties);
                        } else
                        {
                          //--Biologic
                          BiologicEditor bio=new BiologicEditor(frame, parent_workflow);
                          bio.display(properties);
                        }
                    }
                } else RunDefaultEditor(properties, parent_workflow);
                return;
            }
        } else {
            System.out.println("No frame?");
        }
    }
    
    public void saveImage(final String filename) {        
           TimerTask  updateCursorAction= new TimerTask() {
            public void run() {
                    try {                        
                        Thread.sleep(500); //--Wait t'ill the editor is fully displayed on screen...
                        if (editor!=null) ((EditorInterface)getEditor()).saveImage(filename);
                    }
                    catch (Exception e2) {
                        return;
                    }
                }            
        };
        new Timer().schedule(updateCursorAction,0);
    }
    
    private void ErrorRunDefaultEditor(workflow_properties properties, armadillo_workflow parent_workflow) {
        // Will show that only if we debug the application 
        // String msg="<html>Warning! Unable to initialize Editor "+properties.get("EditorClassName")+"<br> for "+properties.getName()+". Loading default...</html>";
        // JOptionPane.showMessageDialog(frame, msg, "Error!", JOptionPane.ERROR_MESSAGE);
        RunDefaultEditor(properties, parent_workflow);
     }
     
    private void RunDefaultEditor(workflow_properties properties, armadillo_workflow parent_workflow ) {
        editor=new defaultEditor(frame, parent_workflow);
        if (getEditor()!=null) ((EditorInterface)getEditor()).display(properties); 
    }
    
    private void DisplayInBrowser(final workflow_properties properties) {
         try{
                if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)){
                    new Thread() {
                        @Override
                     public void run() {
                                try {
                                    int ImageID=properties.getOutputID("imagefile");
                                    if (ImageID!=0) {
                                        ImageFile f=new ImageFile(ImageID);
                                        f.Output(f.getFilename());
                                        File f2=new File(f.getFilename());
                                        Desktop.getDesktop().browse(f2.toURI());
                                    }
                                    int HTMLID=properties.getOutputID("HTML");
                                    if (HTMLID!=0) {
                                        HTML f=new HTML(HTMLID);
                                        f.Output(f.getFilename());
                                        File f2=new File(f.getFilename());
                                        Desktop.getDesktop().browse(f2.toURI());
                                    } 
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                        }
                    }.start();
                    } else {
                        JOptionPane.showMessageDialog(frame,"Unable to display image","",JOptionPane.ERROR_MESSAGE);
                    }
            }catch (Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame,"Unable to display image","",JOptionPane.ERROR_MESSAGE);
            }   
     }

    /** 
     * @return the editor
     */
    public Object getEditor() {
        return editor;
    }

}
