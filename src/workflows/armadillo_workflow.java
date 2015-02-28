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

import Scripts.Scripts_conversion;
import biologic.Biologic;
import biologic.Genome;
import biologic.RunWorkflow;
import biologic.seqclasses.InformationJDialog;
import biologic.MultipleSequences;
import biologic.MultipleTrees;
import biologic.Output;
import biologic.Results;
import biologic.Sequence;
import biologic.Text;
import biologic.seqclasses.StandardInputSequenceJDialog;
import biologic.seqclasses.RenameMultipleTreesJDialog;
import biologic.Tree;
import biologic.Workflows;
import editor.ConnectorInfoBox;
import configuration.Config;
import configuration.ImageFilter;
import configuration.Util;
import database.ExplorerTreeMutableTreeNode;
import editor.RenameBiologicJDialog;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import processing.core.*;
import processing.pdf.*;
import editor.propertiesEditorJDialog;
import biologic.seqclasses.CommentsSequenceJDialog;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;
import java.util.*;

//--From the frame conversion
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import editor.propertiesEditorBox;
import editors.AlignmentEditor;
import editors.BiologicEditor;
import editors.BlastViewEditor;
import editors.SimplePhyloEditor;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import static processing.core.PConstants.HALF_PI;
import program.RunProgram;
import program.UseAlternativeExecutableJDialog;
import programs.forester;
import tools.Toolbox;
import tools.AddCommentsBiologicJDialog;



/**
 * Main Workflow Visualisation
 *
 * @author Etienne Lord
 * @since June 2009-2011
 * 
 * Note: We should implements:
 * 
 *      WorkflowModel      model      = new WorkflowModel();
 *      WorkflowView       view       = new WorkflowView(model);
 *      WorkflowController controller = new WorkflowController(model, view);
 * 
 * Note: for the Applet, we need  -Djava.security.policy=applet.policy
 * where the file (text) applet.poliocy contain:
 *  grant {
 *   permission java.security.AllPermission;
 *   };
 * 
 */
public class armadillo_workflow extends PApplet implements ActionListener {

////////////////////////////////////////////////////////////////////////////////
/// Variables

public armadillo_workflow current=this; //--Pointer on itself for some access
//-- Database (for workflow saving and item)

////////////////////////////////////////////////////////////////////////////////
//-- Properties and editor
public workflow_image filedata=new workflow_image(); //Hold all the icon
private workflow_properties properties=new workflow_properties();
private static Menu program_menu=null;

Config config=new Config();

public Workbox workbox=new Workbox();
public propertiesEditorBox propertieseditorbox;
public ConnectorInfoBox connectorinfobox;
JInternalFrame parent;

////////////////////////////////////////////////////////////////////////////////
//--GUI VARIABLE
PImage workflow_background=null;         // Background workflow image created in draw
public Workflow workflow;                // Workflow object
PFont smallfont,font,boldfont;           // Fonts
PShape armadillo;                        // Logo Armadillo 0.1
PGraphics bufConnectorEdge;              // Back buffer for Arrow selection
private int hashCount=0;                 // Unique number if we need a hashcode
InformationJDialog loading;              // Use getHashcode();
PopupMenu popupObject;                   // Popup Menu
public static int count=0;
public static SimplePhyloEditor simplephylo;
public int click_x=0;                   //--Clicked x position (needed for input )
public int click_y=0;                   //--Clicked y postion
public int images_counter=0;            //--Counter for the image snapshot
Button button;
public int idebug=1;                        //--Debug flag for special function


////////////////////////////////////////////////////////////////////////////////
//--Flag
public boolean small_editor_mode=false;
public boolean name_displayed=false;
public boolean debug=false;      // Debug version (true or false)
public boolean force_redraw=false;// Might be needed if we need to redraw
public boolean force_nodraw=false;// Might be needed if we nned
public boolean save_image=false;  // To save the current workflow as an image
public boolean save_image_pdf=false;  // To save the current workflow as an pdf
public boolean draw_grid=true;    // Do we draw a grid?
public boolean simplegraph=false; // Do we draw as simple graph

private String save_image_filename="";  //Name of filename to save to an image

public boolean initialized=false;   // Flag to know if we have run setup (graphic mode) or not(text mode)
private boolean movingFlag=false;     // Flag Set if we are moving (we don't drwa to back buffer)

////////////////////////////////////////////////////////////////////////////////
/// Workflow changed

private boolean changed=false;

////////////////////////////////////////////////////////////////////////////////
/// Cut and Paste

public Workflow CopyPaste_selection=new Workflow(); // Current CopyPaste Selection
public int Paste_count=0;                           // Number of Copy/Paste Done
public workflow_object Popup_selection;             // Selection in Popup
public boolean Popup_Out=false;                     // Flag to catch Popup
private boolean auto_update=false;                  // Flag for updating the drawing aread
public LinkedList<Workflows>UndoRedo=new LinkedList<Workflows>(); //In development
// To save the "object" state of the workflow - January 2011
public HashMap<workflow_object, workflow_properties> state=new HashMap<workflow_object,workflow_properties>(); 

// color space
int color_red=color(255,186,185);
int color_cyan=color(180,238,255); //--Aread (cyan)
int color_green=color(224,253,183); //--Aread (green)
int color_blue=color(168,199,255); //--Aread (blue)
int color_purple=color(210,194,235); //--Aread (purple)
int color_black=color(180,180,180); //--Aread (black)
int color_orange=color(255,193,140);//--Aread (orange)

int red_border=color(190,75,72); //--red border
int green_border=color(152,185,84); //--green border
int cyan_border=color(70,170,197); //--cyan border
int purple_border=color(125,96,160); //--purple border
int blue_border=color(74,126,187); //--blue border
int orange_border=color(246,146,64); //--orange border
int black_border=color(0); //--black border

//  graph.noSmooth(); 
//graph.background(255);
//graph.strokeWeight(1.0f);
//
//graph.fill(170); //--shadow
//graph.stroke(170);
//graph.rect(12,12,136,86,0,0,4,4);
//graph.stroke(172);
//graph.fill(color(245,246,246)); //--Aread
//graph.fill(color(180,238,255)); //--Aread
//graph.stroke(color(70,170,197)); //--Cyan
////graph.rect(10,26,140,70,6,6,6,6);
//graph.rect(10,10,140,85,6,6,6,6);
//graph.fill(color(237,238,239));//--Title
//graph.fill(color(250));//--Title
//graph.stroke(172);
////graph.rect(10,10,140,20,6,6,0,0); //--Title
//graph.fill(0);
//graph.text("ClustalW", 12,25);
//graph.fill(255);
//
////--Input
//graph.ellipse(10, 56, 10,10); //14?
//graph.ellipse(10, 76, 10,10);
////--Output
//graph.ellipse(150, 66, 14,14);
//graph.fill(0);
//graph.stroke(255);
////graph.line(11,31,150,31); //--line
////text("Tree", 22,50);
////text("Tree", 120,100);
// //svg.recordFrame(dataPath("Sequence-###.svg"));
// 
////--Second (big)
//graph.fill(170); //--shadow 100
//graph.stroke(170);
//graph.rect(212,14,143,35,0,0,4,4); 
//graph.stroke(172);
////graph.stroke(color(190,75,72)); //--red border
//graph.stroke(color(152,185,84)); //--green border
////graph.stroke(color(70,170,197)); //--cyan border
////graph.stroke(color(125,96,160)); //--purple border
////graph.stroke(color(74,126,187)); //--blue border
////graph.stroke(0); //--black border
//
// graph.fill(color(180,238,255)); //--Aread (cyan)
// graph.fill(color(255,186,185)); //--Aread (red)
//  graph.fill(color(224,253,183)); //--Aread (green)
//   //graph.fill(color(168,199,255)); //--Aread (blue)
//     //graph.fill(color(210,194,235)); //--Aread (purple)
//     //graph.fill(color(150,150,150)); //--Aread (black)
//graph.rect(210,12,145,35,4,4,4,4);
//graph.fill(0);
//graph.text("12345678901234567890", 212,25);
//graph.fill(255);
////graph.ellipse(210, 36, 14,14);
////graph.ellipse(355, 36, 14,14);
//
////--Third
//graph.fill(170); //--shadow
//graph.stroke(170);
//graph.rect(212,101,59,18,0,0,4,4); 
// graph.fill(color(224,253,143)); //--Aread (green)
//graph.rect(210,100,61,18,4,4,4,4);
//graph.fill(255);
////graph.ellipse(210, 110, 10,10);
////graph.ellipse(271, 110, 10,10);
////--cut
//graph.fill(255);
//graph.stroke(255);
//graph.rect(205,100,4,18);
//graph.fill(0);
//  graph.textFont(smallg);
// // graph.text("MultipleSequences", 220,125);
////graph.text("1234567890", 220,125);
//graph.text("Sequence", 216,112);
////--Forth
//graph.fill(170); //--shadow
//graph.stroke(170);
//graph.rect(312,101,59,26,0,0,4,4); 
// graph.fill(color(224,253,143)); //--Aread (green)
//graph.rect(310,100,61,26,4,4,4,4);
//graph.fill(255);
////graph.ellipse(310, 110, 10,10);
////graph.ellipse(371, 110, 10,10);
//graph.fill(0);
//  graph.textFont(smallg);
// // graph.text("MultipleSequences", 220,125);
////graph.text("1234567890", 220,125);
//graph.text("Multiple", 316,110);
//graph.text("Sequences", 316,120);


////////////////////////////////////////////////////////////////////////////////
/// Main Setup function

/**
 * public default constructor
 */
public armadillo_workflow() {
    workflow=new Workflow(); //--Note: this is the Workflow support object and not
    workflow.setArmadillo(this); //--Set pointer to the armadillo_workflow
}



/**
 * Applet Setup :: Called by init()
 */
@Override
public void setup() {
 
  //--Initialize visual components
  //--Warning: Must be factor 10 number (ex. 240, 320...)
  //--Note: We use JAVA 2D for the higher graphics quality
  try {
    if (small_editor_mode) {
         Config.log("Initialisation small editor..."); 
          size(490,140);        //--Small editor size
          //--Warning, se resize function also 
          bufConnectorEdge=createGraphics(490, 140, JAVA2D);
         
  } else {
       System.out.println("Waiting for workflow to initialize...");  
        if (config.isSet("workflow_w")&&config.isSet("workflow_h")){
            if (config.getBoolean("LowResolution")) {
                size(config.getInt("workflow_w"),config.getInt("workflow_h"));
            } else {
                size(config.getInt("workflow_w"),config.getInt("workflow_h"),JAVA2D);    
            }
            bufConnectorEdge=createGraphics(config.getInt("workflow_w"),config.getInt("workflow_h"),  JAVA2D);
            Config.log("Initialisation of workflow with size ("+config.getInt("workflow_w")+","+config.getInt("workflow_h")+")");
        } else {
            //--Normal size
            if (config.getBoolean("LowResolution")) {
                 size(2000,600);
            } else {
                 size(2000,600, JAVA2D);   
            }
            size(2000,600, JAVA2D);
            bufConnectorEdge=createGraphics(2000,600,  JAVA2D);
            Config.log("Initialisation of workflow with size (2000,600)");
        }
        
        
  }   //--Normal size
  } catch(Exception e) {
        e.printStackTrace();
        Config.log("Unable to initialize Armadillo Workflow Graphics...");
        System.exit(-1);
  }
  try {
  //--Buffer for Arrow Selection
  
  //--Font
  //smallfont=createFont(dataPath("Calibri-10.vlw"));
  //font=loadFont(dataPath("Calibri-12.vlw"));
  //boldfont=loadFont(dataPath("Calibri-Bold-14.vlw"));
  
  //--Select font list, put in preference...
  //String[] fontList = PFont.list();
  //println(fontList
  int font_adjuster=config.getInt("font_size_adjuster");
      
  smallfont=createFont("Calibri",10+font_adjuster);
  font=createFont("Calibri",12+font_adjuster);
  boldfont=createFont("Calibri-Bold",14+font_adjuster);
  armadillo=loadShape(dataPath("logo.final.svg"));
  textAlign(CENTER);
  textFont(boldfont);
  fill(0);
  //--4 January 2011 - test without smooth, not good!
  smooth();
  noLoop();

  //--Initialization of some object
  if (!Config.library_mode&&!small_editor_mode) {
        //--Initialize program menu iff null
        if (program_menu==null) {
            program_menu=loadProgram();
        }      
        propertieseditorbox=new propertiesEditorBox();
        
        if (simplephylo==null&&config.getBoolean("LoadPhyloWidget")) {
              Config.log("Loading PhyloWidget...");
              simplephylo=new SimplePhyloEditor(this.frame, this);
        }
          
         if (config.getBoolean("Applet")) {
            //--Add a test button.
            //--Bonus, it will stop the key manager?
            //button =new Button("Play");
            //add(button, FlowLayout.CENTER);               
           if (propertieseditorbox.frame==null) {
        
            //--Add a test button.
            //--Bonus, it will stop the key manager?
            //button =new Button("Play");
            //add(button, FlowLayout.CENTER);               
//           this.propertieseditorbox.frame=(Frame) SwingUtilities.getAncestorOfClass(Frame.class,this);  
//            try {                
//                 //UIManager.setLookAndFeel(ch.randelshofer.quaqua.QuaquaManager.getLookAndFeel());
//            } catch (Exception e2) {}
          }
         }
  }
  if (!Config.library_mode) {
        connectorinfobox=new ConnectorInfoBox();
  }
  //--Initialization done...
  
  setInitialized(true);
  System.out.println("Done initializing..."+(small_editor_mode?"small editor...":"main workflow builder..."));
  force_redraw=true;
  redraw();
  
  } catch(Exception e) {
      System.out.println("Fatal exception. Please restart.");
      System.out.println(e.getMessage()+"\n"+e.getLocalizedMessage());
      Config.log(e.getMessage()+"\n"+e.getLocalizedMessage());
      
  }
}

    
     private void createPopup(workflow_object selection) {
           
            MenuItem mi;
            //Popup Simple Object
            popupObject = new PopupMenu("Edit");
            popupObject.addActionListener((ActionListener)this);

            if (program_menu!=null&&selection==null) {               
                popupObject.add(program_menu);
                popupObject.addSeparator();
            }

            mi = new MenuItem("Copy");
            mi.addActionListener((ActionListener) this);
            
            popupObject.add(mi);
    	    mi = new MenuItem("Cut");
            
            mi.addActionListener((ActionListener) this);
            popupObject.add(mi);
            mi = new MenuItem("Paste");
            //--Enable paste
            mi.setEnabled(this.CopyPaste_selection.work.size()==0?false:true);
            mi.addActionListener((ActionListener) this);
            popupObject.add(mi);

            mi = new MenuItem("Select All");
            popupObject.add(mi);           
            mi.addActionListener((ActionListener) this);
            
            popupObject.addSeparator();           
            mi = new MenuItem("Delete Selection");
            mi.setEnabled(this.isSelection());
            mi.addActionListener((ActionListener) this);
            popupObject.add(mi);
            
            //popupWorkflow.add(mi);
            if (selection !=null&&(selection.getProperties().get("ObjectType").equals("Program")||selection.getProperties().get("ObjectType").equals("If"))) {
                popupObject.addSeparator();
                mi = new MenuItem("Edit Options");
                mi.addActionListener((ActionListener) this);
                popupObject.add(mi);
                if (selection.getProperties().get("ObjectType").equals("Program")) {
                    mi = new MenuItem("Repeat...");
                    mi.setLabel("Repeat...");
                    mi.addActionListener((ActionListener) this);
                    popupObject.add(mi);
                }
                popupObject.addSeparator();
                mi = new MenuItem("Run");
                mi.addActionListener((ActionListener) this);
                popupObject.add(mi);
                mi = new MenuItem("Stop");
                mi.addActionListener((ActionListener) this);
                popupObject.add(mi);
                mi = new MenuItem("Reset State");
                mi.addActionListener((ActionListener) this);
                popupObject.add(mi);
                
                mi = new MenuItem("Use Alternative Executable");
                mi.addActionListener((ActionListener) this);
                popupObject.add(mi);
                
                
                if (selection.getProperties().get("outputType").equals("Workflows")) {
                    mi = new MenuItem("Open Workflow");
                    mi.addActionListener((ActionListener) this);
                    popupObject.add(mi);
                 }
            } else if (selection !=null&&selection.getProperties().get("ObjectType").equals("Output")) {
                popupObject.addSeparator();
                mi = new MenuItem("View");
                 mi.setLabel("View");
//                 mi.setFont(mi.getFont().deriveFont(Font.BOLD));
                 mi.addActionListener((ActionListener) this);
                 popupObject.add(mi);
                 if (selection.getProperties().get("outputType").equals("Sequence")||
                     selection.getProperties().get("outputType").equals("MultipleSequences")||
                     selection.getProperties().get("outputType").equals("Alignment")) {
                    mi = new MenuItem("View Graphic");
                    mi.addActionListener((ActionListener) this);
                    popupObject.add(mi);
                 }
                 mi = new MenuItem("Notes");
                          mi.addActionListener((ActionListener) this);
                          popupObject.add(mi);
                   mi = new MenuItem("Rename");
                          mi.addActionListener((ActionListener) this);
                          popupObject.add(mi);

//                          mi = new MenuItem("Note");
//                          mi.addActionListener((ActionListener) this);
//                          popupObject.add(mi);
                 if (selection.getProperties().get("outputType").equals("Tree")) {
                    if (config.getBoolean("LoadPhyloWidget")) {
                        mi = new MenuItem ("View Tree in PhyloWidget");
                        mi.addActionListener((ActionListener) this);
                        popupObject.add(mi);
                    }
                        mi = new  MenuItem("View Tree in Archaeopteryx");
                        mi.addActionListener((ActionListener) this);
                        popupObject.add(mi);
                        
                        mi = new  MenuItem("View Newick String");
                        mi.addActionListener((ActionListener) this);
                        popupObject.add(mi);
                 }
                 if (selection.getProperties().get("outputType").equals("Blast")) {
                    mi = new MenuItem("View BlastHit");
                    mi.addActionListener((ActionListener) this);
                    popupObject.add(mi);
                 }
                 mi = new MenuItem("Save...");
                 //mi.setLabel("<html")
//                 mi.setFont(mi.getFont().deriveFont(Font.BOLD));
                 mi.addActionListener((ActionListener) this);
                 popupObject.add(mi);

             } else if (selection !=null&&selection.getProperties().get("ObjectType").equals("OutputDatabase")) {
                    popupObject.addSeparator();
                    String type=selection.getProperties().get("outputType");
                     int id=selection.getProperties().getOutputID(type);
                     if (id!=0) {
                         mi = new MenuItem("Change Dataset");
                          mi.addActionListener((ActionListener) this);
                          popupObject.add(mi);
                          mi = new MenuItem("Rename");
                          mi.addActionListener((ActionListener) this);
                          popupObject.add(mi);
                          mi = new MenuItem("View");
//                          mi.setFont(mi.getFont().deriveFont(Font.BOLD));
                          mi.addActionListener((ActionListener) this);                          
                          popupObject.add(mi);
                           if (selection.getProperties().get("outputType").equals("Sequence")||
                             selection.getProperties().get("outputType").equals("MultipleSequences")||
                             selection.getProperties().get("outputType").equals("Alignment")) {
                             mi = new MenuItem("View Graphic");
                             mi.addActionListener((ActionListener) this);
                             popupObject.add(mi);
                            }
                          mi = new MenuItem("Notes");
                          mi.addActionListener((ActionListener) this);
                          popupObject.add(mi);
                          if (selection.getProperties().get("outputType").equals("Tree")) {
                            if (config.getBoolean("LoadPhyloWidget")) {
                                mi = new MenuItem ("View Tree in PhyloWidget");                                
                                mi.addActionListener((ActionListener) this);
                                popupObject.add(mi);
                            }
                                mi = new  MenuItem("View Tree in Archaeopteryx");
                                mi.addActionListener((ActionListener) this);
                                popupObject.add(mi);

                                mi = new  MenuItem("View Newick String");
                                mi.addActionListener((ActionListener) this);
                                popupObject.add(mi);
                         }
//                          mi = new MenuItem("Save...");
//                          mi.addActionListener((ActionListener) this);
//                          popupObject.add(mi);
                          mi = new MenuItem("Repeat...");
                          mi.addActionListener((ActionListener) this);
                          popupObject.add(mi);
                     } else {
                         mi = new MenuItem("Select Dataset");
                         mi.addActionListener((ActionListener) this);
                         popupObject.add(mi);
                }                
            } else if (selection==null) {
                popupObject.addSeparator();
                mi = new MenuItem("Edit Preferences");
                mi.addActionListener((ActionListener) this);
                popupObject.add(mi);
                mi = new MenuItem("Save Workflow image");
                mi.addActionListener((ActionListener) this);
                popupObject.add(mi);
            }
            //--Show Software's ouput log if found
            if (selection!=null&&selection.getProperties().isSet("output_outputtext_id")&&selection.getProperties().getInt("output_outputtext_id")!=0) {
                mi = new MenuItem("Show software output");
                mi.addActionListener((ActionListener) this);
                popupObject.add(mi);

            }

            //--DeveloperMode
            if (selection!=null&&config.isDevelopperMode()) {
                popupObject.addSeparator();
                mi = new MenuItem("Object Properties");
                mi.addActionListener((ActionListener) this);
                popupObject.add(mi);
            }
            
            add(popupObject); // add popup menu to applet
            
          
            //enableEvents(AWTEvent.MOUSE_EVENT_MASK);
            //--See below for Event
     }

      private void createPopupManyOutput(workflow_object selection) {
           
            MenuItem mi;
            //Popup Simple Object
            popupObject = new PopupMenu("All");
            popupObject.addActionListener((ActionListener)this);

              mi = new MenuItem("List all output types");
                 mi.addActionListener((ActionListener) this);
           popupObject.add(mi);
         
             popupObject.addSeparator();         
            //--Iterate over many option
            for (String out:selection.getProperties().Outputed()) {
                mi = new MenuItem("output_type_"+out);
                 mi.setLabel(out);
                 mi.addActionListener((ActionListener) this);
                  popupObject.add(mi);
            }
           
        
            add(popupObject); // add popup menu to applet
           
     }
     
     /**
      * This is an helper function to create the "Add Program" to the Popup-Menu
      */
     public Menu loadProgram() {         
         Vector<workflow_properties>program=new Vector<workflow_properties>();
        HashMap<String,Menu> ListCategoryNode=new HashMap<String,Menu>();
        for (String filename:workflow_properties.loadPropertieslisting(config.get("propertiesPath"))) {
            workflow_properties tmp=new workflow_properties();
            tmp.load(filename, config.get("propertiesPath"));
            program.add(tmp);
        }
        workflow_properties rootnode=new workflow_properties();
        rootnode.setName("Armadillo");
        Menu menu=new Menu("Add Program");
        //menu.setFont(menu.getFont().deriveFont(Font.BOLD));
        
        for (workflow_properties lnode:program) {
           String category=lnode.get("Type");
           MenuItem newNode=new MenuItem(lnode.getName());
           newNode.setActionCommand(lnode.filename);
           newNode.addActionListener((ActionListener) this);
           Menu rootNode=ListCategoryNode.get(category);
           //--Config.log("Index "+index+" "+category+" "+node);
           if (category.equals("For each")) {
                //--Not needed for now..
               //category.equals("Begin")||
               menu.add(newNode);
           } else
               if (rootNode==null) {
                  workflow_properties newnode_properties=new workflow_properties();
                  newnode_properties.put("Type",category);
                  newnode_properties.setName(category);
                  Menu newRootNode=new Menu(newnode_properties.get("Type"));
                  newRootNode.add(newNode);
                  ListCategoryNode.put(category, newRootNode);
               } else {
                  rootNode.add(newNode);
               }
       }
       LinkedList<Menu> list=new LinkedList<Menu>();
       list.addAll(ListCategoryNode.values());
       Collections.sort(list, new MenuComparator());    
       for (Menu lnode:list) menu.add(lnode);        
       return menu;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        
        String ac=e.getActionCommand();

        //Config.log(e.paramString());
        for (String outs:workflow_properties_dictionnary.InputOutputType) {
          
            if (ac.equals(outs)) {                         
                if (this.Popup_selection!=null) {
                       workflow_connector s=this.Popup_selection.returnConnector(workflow_object.OUTPUT);
                       
                       int posy=s.parent.y-20; //Middle pint
                    
                     //--output All?                                                       
                     
                            Boolean found=false;
                            //Check if output already exist for this object
                            for (workflow_object w:workflow.findOutput(s.parent)) {
                                if (w.getProperties().Outputed().contains(outs)) found=true;
                            }
                            //--If not found, create it...
                            if (!found) {
                                workflow_object_output o=null;
                               //--Debug (Warning Length <5)
                                //if (type.length()<5) {
                                o=workflow.createOutput_Object(outs,s.x+50,posy+=20);
                                if (o instanceof workflow_object_output_big) posy+=10;
                                o.getProperties().put("ParentID", s.parent.getProperties().getID());
                               //} else {
                               // o=new workflow_object_output_big(type, s.x+50,posy+=20);
                               //}

                               workflow.add(o);
                               //--create a connection
                               workflow_connector_edge oc=new workflow_connector_edge(s, o.returnNearestConnector(s), "");
                               oc.setNotDeletabled(true);
                               workflow.work_connection.add(oc);
                               workflow.updateDependance();
                               return;
                            }
                        }
                   }
                
             } //-End output type
        
        if (ac.equals("List all output types")) {
             if (this.Popup_selection!=null) {
                 workflow_connector s=this.Popup_selection.returnConnector(workflow_object.OUTPUT);
                  workflow.createOutput_Objects(s);
                 workflow.updateDependance();
             }            
        }
        
        if(ac.equals("Edit Options")||ac.equals("View")||ac.equals("Select Dataset")||ac.equals("View Newick String")){
           if (this.Popup_selection!=null) leftClick(this.Popup_selection);
           workflow.resetSelected();
       } else
       if (ac.equals("Show software output")) {
           if (this.Popup_selection!=null) {
               BiologicEditor pro=new BiologicEditor(this.frame, this);
               //--Build a temporary properties object
               workflow_properties tmp=new workflow_properties();
                 tmp.put("output_outputtext_id", this.Popup_selection.getProperties().getInt("output_outputtext_id"));
                 tmp.put("colorMode","GREEN");
                 tmp.put("defaultColor","GREEN");
                 tmp.put("OutputOutputText", "True");
                 tmp.put("outputType", "OutputText");
                 tmp.put("Connector1Output","True");
                 tmp.put("Connector0Output", "True");
                 tmp.put("Connector0Conditional", "True");
                 tmp.put("ObjectType", "OutputDatabase");
               //--Display
               pro.display(tmp);
               
           }
           workflow.resetSelected();
       } else
       if (ac.equals("View Graphic")) {
           if (this.Popup_selection!=null) {
               AlignmentEditor pro=new AlignmentEditor(this.frame, this);

               pro.display(this.Popup_selection.getProperties());
           }
           workflow.resetSelected();
       } else
       if (ac.equals("Save Workflow image")) {
           workflow.resetSelected();
           SaveWorkflowImage();
       } else
       if (ac.equals("View Tree in PhyloWidget")) {
           if (this.Popup_selection!=null) {               
               simplephylo.display(this.Popup_selection.getProperties());
           }
           workflow.resetSelected();
       } else
       if (ac.equals("View Tree in Archaeopteryx")) {
           if (this.Popup_selection!=null) {
               forester f=new forester(this.Popup_selection.getProperties());

           }
           workflow.resetSelected();
       } else
           
       if (ac.equals("Open Workflow")) {
           LoadCurrent();
       } else

        if (ac.equals("Save...")) {
           SaveCurrent();
       } else

       if (ac.equals("Rename")) {
           RenameCurrent();
       } else
       if (ac.equals("Notes")) {
           DisplayNotesCurrent();
       } else
       if (ac.equals("Reset State")) {
           if (this.Popup_selection!=null&&!this.Popup_selection.selected) {
               this.Popup_selection.selected=true;
           }
           resetSelectionState();
       } else
        

        if (ac.equals("Use Alternative Executable")) {            
            UseAlternativeExecutableJDialog alter=new UseAlternativeExecutableJDialog(this.frame,this.Popup_selection.getProperties());
            
            workflow.resetSelected();
        } else   
         if (ac.equals("Change Dataset")) {
            this.convertToFor(this.Popup_selection);
            if (this.Popup_selection!=null) leftClick(this.Popup_selection);
            workflow.resetSelected();
        } else

        if (ac.equals("Repeat...")) {
            this.convertToFor(this.Popup_selection);
            if (this.Popup_selection!=null) leftClick(this.Popup_selection);
            workflow.resetSelected();
        } else

        if (ac.equals("Edit Preferences")) {
            this.workbox.ShowPreferences();           
         }else 
       if(ac.equals("Copy")){
           //Selection?
           if (this.Popup_selection!=null&&!this.Popup_selection.selected) {
               this.Popup_selection.selected=true;
           }
            this.copySelection();
            workflow.resetSelected();            
       } else
       if(ac.equals("Cut")){
            if (this.Popup_selection!=null&&!this.Popup_selection.selected) {
               this.Popup_selection.selected=true;
           }
           this.copySelection();
           this.workflow.deleteSelected();
       } else          
       if(ac.equals("Paste")){
            this.pasteSelection();         
       } else if(ac.equals("Select All")){
            workflow.selectAll();       
       } else
       if(ac.equals("Delete Selection")){           
           if (this.Popup_selection!=null&&!this.Popup_selection.selected) {
               this.Popup_selection.selected=true;
           }
           this.workflow.deleteSelected();
       } else        
       if (ac.equals("Stop")) {
           this.Popup_selection.getProperties().put("Status", RunProgram.status_nothing);
           this.Popup_selection.getProperties().killThread();
           workflow.resetSelected();
       } else
        if (ac.equals("Run")) {
           if(this.Popup_selection.getProperties().isSet("ClassName")) {
           this.workflow.updateDependance();          
           workbox.Run(this.Popup_selection.getProperties());
           workflow.resetSelected();
        }
       } else
        if (ac.equals("View BlastHit")) {
            BlastViewEditor pro=new BlastViewEditor(this.frame, this);
            pro.display(this.Popup_selection.getProperties());
            workflow.resetSelected();
        } else
        if (ac.equals("Object Properties")) {
            propertiesEditorJDialog pro=new propertiesEditorJDialog(this.frame, this.Popup_selection.getProperties(),"Object Properties");
            pro.setVisible(true);
            workflow.resetSelected();
        } else
        if (ac.equals("Redraw")) {
            redraw();
        } else
        //--Object to add (actually the action command is the filename)
        if (Util.FileExists(ac)) {
            //--Load the Object and drop at this point
            workflow_properties tmp=new workflow_properties();
            tmp.load(ac);
            //--Save to Undo list
            workflow.saveToUndo("Inserting "+tmp.getName());            
            createObject(tmp, new Point(this.click_x,this.click_y));
            //--Old..
            //createObject(tmp, new Point(15,15));
            force_redraw=true;
            redraw();
        }    
        else {
          //--Reset selection
           workflow.resetSelected();
        }
    }


    public void convertToFor(workflow_object obj) {
        workflow_properties prop=obj.getProperties();
        if (prop.get("ObjectType").equals("Program")) {
            prop.put("ForObjectID", prop.getID());
        } else {
            String outputType=prop.get("outputType");
            //prop.put("ForObjectID", prop.getID());
            int id=prop.getOutputID(outputType);
            prop.put("output_"+outputType.toLowerCase()+"_id", 0);
            prop.put("For_"+id,properties.getDescription());
            prop.put("Description", "For each");
        }
        this.force_redraw=true;
        this.redraw();
    }

    public void LoadCurrent() {
        String msg="<html>This will erase current workflow. Continue?</html>";
                    Object[] options = {"No","Yes, Load"};
                    int o = JOptionPane.showOptionDialog(this,msg,"Load Workflow?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options, options[0]);
                    switch (o) {
                        //0. Cancel
                        case 0: break;
                        //1. Delete All
                        case 1:
                             int id=Popup_selection.getProperties().getOutputID("Workflows");
                             this.workbox.loadWorkflowFromDatabase(id);
                        break;
                      }

    }

    public void SaveCurrent() {
         JFileChooser jf=new JFileChooser(config.getExplorerPath());

        jf.setName("Saving as text file to ...");
        int result=jf.showSaveDialog(this);
           if (result==JFileChooser.APPROVE_OPTION) {
               String filename=jf.getSelectedFile().getAbsolutePath();
               String path=jf.getSelectedFile().getPath();
               config.setExplorerPath(path);
               config.Save();
               //--Output
               String type=this.Popup_selection.getProperties().get("outputType");
               int id=this.Popup_selection.getProperties().getOutputID(type.toLowerCase());
               Output out=new Output();
               out.setType(type);
               out.setTypeid(id);
               Util file=new Util();
               file.open(filename);
               file.println(((Biologic)out.getBiologic()).toString());
               file.close();
               workbox.Message("Successfull saving to "+filename,"");
            }

    }

    public void SaveWorkflowImage() {
        JFileChooser jf=new JFileChooser(config.getExplorerPath());
        jf.addChoosableFileFilter(new ImageFilter());
        jf.setAcceptAllFileFilterUsed(false);
        jf.setName("Saving Image to ...");
        int result=jf.showSaveDialog(this);
           if (result==JFileChooser.APPROVE_OPTION) {
               String filename=jf.getSelectedFile().getAbsolutePath();
               String path=jf.getSelectedFile().getPath();
               config.setExplorerPath(path);
               config.Save();
               if (filename.toLowerCase().endsWith("pdf")) {
                 workbox.getCurrentArmadilloWorkflow().savePDF(filename);  
               } else 
                workbox.getCurrentArmadilloWorkflow().saveImage(filename);
            }
    }
    
     public void SaveWorkflowPDF() {
        JFileChooser jf=new JFileChooser(config.getExplorerPath());
        jf.addChoosableFileFilter(new ImageFilter());
        jf.setAcceptAllFileFilterUsed(false);
        jf.setName("Saving PDF to ...");
        int result=jf.showSaveDialog(this);
           if (result==JFileChooser.APPROVE_OPTION) {
               String filename=jf.getSelectedFile().getAbsolutePath();
               String path=jf.getSelectedFile().getPath();
               config.setExplorerPath(path);
               config.Save();
               workbox.getCurrentArmadilloWorkflow().savePDF(filename);
            }
    }

    /**
      * This will recreate the font 
      */
    public void recreateFont() {
        int font_adjuster=config.getInt("font_size_adjuster");
        smallfont=createFont("Calibri",10+font_adjuster);
        font=createFont("Calibri",12+font_adjuster);
        boldfont=createFont("Calibri-Bold",14+font_adjuster);
        force_redraw=true;
        redraw();
    } 
     
    public void RenameCurrent() {
               String type=this.Popup_selection.getProperties().get("outputType");
               int id=this.Popup_selection.getProperties().getOutputID(type.toLowerCase());
               if (id>0) {
                   Output out=new Output();
                    out.setType(type);
                    out.setTypeid(id);
                    String name=((Biologic)out.getBiologic()).getName();
                    ExplorerTreeMutableTreeNode node=new ExplorerTreeMutableTreeNode(name, type, id);
                    RenameBiologicJDialog d=new RenameBiologicJDialog(frame, node, "Rename "+type);
                    workflow.updateDependance();
                    force_redraw=true;
                    redraw();
                    workbox.Message("Successfull rename of "+name,"");

               }
    }

     public void DisplayNotesCurrent() {
               String type=this.Popup_selection.getProperties().get("outputType");
               int id=this.Popup_selection.getProperties().getOutputID(type.toLowerCase());
               if (id>0) {
                   Output out=new Output();
                    out.setType(type);
                    out.setTypeid(id);
                    String name=((Biologic)out.getBiologic()).getName();
                    ExplorerTreeMutableTreeNode node=new ExplorerTreeMutableTreeNode(name, type, id);
                        if (node.getType().equals("Sequence")||node.getType().equals("MultipleSequences")||node.getType().equals("Alignment")) {
                            CommentsSequenceJDialog s = new CommentsSequenceJDialog(frame, node, "");
                        } else {
                            AddCommentsBiologicJDialog d=new AddCommentsBiologicJDialog(frame, node, "Add Comments");
                        }
                    workflow.updateDependance();
                    force_redraw=true;
                    redraw();                    
               }
    }

///////////////////////////////////////////////////////////////////////////////
/// Initialization override and saving as image


    /**
     * This is the preferred way to initialize the Main Workflow since we can have 
     * the parent frame (for JOptionPane.Message)
     * @param parent
     * @param small (small editor mode?) - Dafault false
     */
    public void init(JInternalFrame parent, boolean small) {
        this.parent=parent;
        this.small_editor_mode=small;
        super.init();
    }
    
    /**
     * Decompose the workflow into its simple elements
     */
    public void decompose() {
        int x=100;
        int y=75;
        HashMap<String,Integer>obj_names=new HashMap<String, Integer>();
        for (int i=workflow.work.size()-1; i>-1;i--) {
            workflow_object o=workflow.work.get(i);
            this.workflow.RemoveAllConnection(o);   
            if (!o.getProperties().get("ObjectType").equals("Program")) {
                 this.workflow.delete(o);
            } else {
                //--Verify if we have the name to remove duplicate
                if (obj_names.containsKey(o.getName())) {
                    this.workflow.delete(o);
                } else {
                    o.getProperties().removeStatus();
                    o.getProperties().put("x", x);
                    o.getProperties().put("y", x);
                    o.x=x;
                    o.y=y;
                   x+=200;
                   if (x>500) {
                       x=100;
                       y+=100;
                   }
                   obj_names.put(o.getName(), 1);
                   workflow.draw();
                } //--End contains obj name
            }
        }
        
    }
    
    public void saveImage(String filename) {
        this.save_image_filename=filename;
        this.save_image=true;
        redraw();
    }
    
    public void savePDF(String filename) {
        this.save_image_filename=filename;
        this.save_image_pdf=true;
        redraw();
    }

    public void resetState() {
        for (workflow_object obj:workflow.work) {
            obj.getProperties().remove("Status");
            obj.getProperties().remove("StatusString");
            obj.getProperties().remove("IfStatus");
            obj.getProperties().remove("CommandLine_Running");                       
        }        
        force_redraw=true;
        redraw();
    }

    /**
     * This is lioke resetState but also remove any reference to ouput id...
     */
        public void resetAllState() {
        for (workflow_object obj:workflow.work) {
            obj.getProperties().remove("Status");
            obj.getProperties().remove("StatusString");
            obj.getProperties().remove("IfStatus");
            obj.getProperties().remove("CommandLine_Running");  
            if (obj.getProperties().get("ObjectType").equals("Output")) {
                obj.getProperties().removeOutput();
            }
        }        
        force_redraw=true;
        redraw();
    }

    
    /**
     * Save the object state before using the PDF
     */
    public void saveandresetState(){
        //--1. Clear the state Vector
        state.clear();
        for (workflow_object obj:workflow.work) {
            workflow_properties p=(workflow_properties)obj.getProperties().clone();
            state.put(obj, p);
        }
         for (workflow_object obj:workflow.work) {
            obj.getProperties().remove("Status");
            obj.getProperties().remove("StatusString");
            obj.getProperties().remove("IfStatus");
            obj.getProperties().remove("CommandLine_Running");           
        }
    }
    
    /**
     * Load the object state after saving to PDF     
     */
    public void loadState() {
        for (workflow_object obj:workflow.work) {
            workflow_properties p=state.get(obj);
            obj.properties=p;
        }
    }
    
    @Override
    public void setName(String name) {
        this.workflow.name=name;
        this.force_redraw=true;
        this.redraw();
        //super.setName(name);
    }



    @Override
    public String getName() {
        return this.workflow.name;
    }

 

////////////////////////////////////////////////////////////////////////////////
/// SET SMALL EDITOR MODE

/**
 * Set this workflow as a small editor
 * Note: MUST BE CALLED BEFORE initialisation! (.init())
 * @param b
 */
public void setSmallEditorMode(boolean b) {
    this.small_editor_mode=true;
    this.name_displayed=false;
}


////////////////////////////////////////////////////////////////////////////////
/// Main drawing functions

@Override
public void draw() {
  //--saving not initialize view?
  //--TODO 
  //--Are we drawing
  if (! isInitialized()) return;
  
  if (save_image_pdf) {
      //--Begin recording of pdf      
      PGraphicsPDF pdfe = (PGraphicsPDF)  createGraphics(width, height, PDF, getSave_filename());     
      beginRecord(pdfe);
     //  pdf.nextPage();
      
      //--Reset state -- Required since no transparent image in pdf!
      saveandresetState();      
  }
  //--Reset any selection if saving...
  if (save_image||save_image_pdf) workflow.selectNone();

  if (auto_update) special_draw();
  


  //if (auto_update) return; //--For now, don't update...
  if (!force_nodraw) {
    //--Clear Arrow buffer
  bufConnectorEdge.background(255);  
  //-- Draw background
  // CASE 1. We already have a good background
 if (!save_image_pdf&&!force_redraw&&workflow_background!=null&&!save_image&&workflow_background.width==width&&workflow_background.height==height) {
     image(workflow_background,0,0);
  } else {
     // CASE 2. We redraw the background
    background(255);
    //--Line in the background
    stroke(128);
    if (save_image_pdf) stroke(200);
    strokeWeight(0.1f);
      //CASE 1. Small_editor_mode
   if (small_editor_mode) {
       if (draw_grid) {
            for (int x=10; x<=width; x+=10) line(x, 10, x, height-10);
            for (int y=10; y<=height; y+=10) line(10, y, width-10, y);
       }
        shape(armadillo,15,5,armadillo.width/10, armadillo.height/10);
   } else {
       // CASE 2. Normal mode
       // Added January 2011 - Stroke weight change each 50;
       int maxheight=(height%10==0?height-10:height-10-(height%10));
       int maxwidth=(width%10==0?width-10:width-10-(width%10));
           strokeWeight(0.5f);
           line(10,10, 10, maxheight);
           line(maxwidth,10, maxwidth, maxheight);
           line(10, 10, maxwidth, 10);
           line(10, maxheight, maxwidth, maxheight);
           strokeWeight(0.1f);
       if (draw_grid&&!save_image_pdf) {
            for (int x=10; x<=maxwidth; x+=10) {
                if (x%100==0) {
                    strokeWeight(0.5f);
                } else {
                    strokeWeight(0.1f);
                }
                line(x, 10, x, maxheight);
            }
            for (int y=10; y<=maxheight; y+=10) {
                if (y%100==0) {
                    strokeWeight(0.5f);
                } else {
                    strokeWeight(0.1f);
                }
                line(10, y, maxwidth, y);
            }
       }
       
    //--Workflow name now in the bottom of the workflow area
            stroke(128);
            fill(128);
            textFont(boldfont);
            textAlign(LEFT);
            //--DOES NOT WORK IN APPLET...
           // text(workflow.name + " [id "+this.workbox.getCurrentWorkflows().getId()+"]",15,maxheight-10);
        fill(0);
        shape(armadillo,15,10,armadillo.width/10, armadillo.height/10);
    }
    //-- Save background for futher use and   
    workflow_background=get();
    force_redraw=false;
  }
   //--Draw workflow
    workflow.draw();
  //-- Save workflow image?
  if (save_image) {

      PGraphics img = createGraphics((int)(width/2.0f), (int)(height/2.0f),JAVA2D);
      img.background(255);
      //PGraphics save_img=createGraphics((int)this.max_object_width()+50, (int)this.max_object_height()+50,this.JAVA2D);
      //save_img.image(get(), 0, 0);
      img.image(get(),0,0,(int)width/2.0f, (int)height/2.0f);
      if (getSave_filename().endsWith("jpg")||getSave_filename().endsWith("jpeg")) {
          System.out.println("Saving jpg...");
          saveBytes(getSave_filename(), bufferImage(get(0, 0, width, height)));
       } else {      
          save(getSave_filename());
       }
      img.save(getSave_filename()+".thumb.png");
      save_image=false;
  }
  if (save_image_pdf) {
      endRecord();
      loadState();
      save_image_pdf=false;
  }
  
  } //--End force_nodraw
  //--Debugging information

  if (debug) {
          fill(0);
          textFont(font);
          textAlign(LEFT);
          text("DEBUG Information: width: "+str(width)+" height: "+str(height)+" mx: "+str(mouseX)+" my: "+str(mouseY), 200,25);
          //--Bigger image if needed... 
          //image(get(),0,0,(int)(width*1.2), (int)(height*1.2));
  }
}

public void special_draw() {
    image(workflow_background,0,0);   
    workflow.draw();
}

public float max_object_width() {
    float max=0;
    //--Iterate over object
    for (workflow_object obj:workflow.work) {
        //--max x
        
        if (obj.Vertex[obj.DOWNLEFT].x>max) max=obj.Vertex[obj.DOWNLEFT].x;
        
    }
    return max;
}

public float max_object_height() {
    float max=0;
    //--Iterate over object
    for (workflow_object obj:workflow.work) {
        //--max x
        if (obj.Vertex[workflow_object.DOWNLEFT].y>max) max=obj.Vertex[obj.DOWNLEFT].y;
    }
    return max;
}

 @Override
  public void resize(int width, int height) {
   try {
   if (small_editor_mode) {         
         bufConnectorEdge=createGraphics(490, 140, JAVA2D);
         super.resize(490,140);        //--Small editor size
         //Config.log("resize Small editor");
  } else {        
        bufConnectorEdge=createGraphics(2000, 600, JAVA2D);
        super.resize(2000,600);     //--Normal size
        //Config.log("resize normal workflow");
  }   //--Normal size
  } catch(Exception e) {
        e.printStackTrace();
        System.err.println("Unable to initialize Armadillo Workflow Graphics...");
        System.exit(-1);
  } 
      force_redraw=true;
      redraw();
    }

 ///////////////////////////////////////////////////////////////////////////////
 /// DataPath

    @Override
 public String dataPath(String filename) {
     return config.get("dataPath")+File.separator+filename;
 }

////////////////////////////////////////////////////////////////////////////////
/// Mouse Function

@Override
public void mouseDragged() {
      int px=mouseX-pmouseX;
      int py=mouseY-pmouseY;
      workflow.move(px,py);
      redraw();
} //End mouse dragged

@Override
public void mouseClicked() {
 //--do we have a selection
  
    Object selection=workflow.select(mouseX, mouseY);   
    //--Save position (if needed)
    this.click_x=mouseX;
    this.click_y=mouseY;

    //--Case 1. Rigth click
    if (mouseButton==RIGHT) {
             //Popup(mouseEvent); //--Debug problem
             rightClick(selection);             
    } else if (selection!=null) {
    //--Case 2. Left click
        if (mouseButton==LEFT&&mouseEvent.getClickCount()==2) {
            leftClick(selection);
            workflow.resetSelected();
        }
     }
    //--Case 3. No selection;
    else {
        workflow.resetSelected();
     }
 redraw();  
}

public void rightClick(Object o) {
    workflow.updateDependance(); //Be sure we are up to date
    
    //--Case 1. Small editor mode
    if (small_editor_mode&&o!=null) {
        if (o instanceof workflow_connector&&small_editor_mode) connectorinfobox.display((workflow_connector)o);
        if (o instanceof workflow_object&&!small_editor_mode) {
                workflow_properties tmp=((workflow_object)o).getProperties();
                this.propertieseditorbox.display(tmp, this);
        }
        workflow.resetSelected();
    }
    //--Case 2. Normal workflow
    //--Note: selection can be null (o!=null&&) since we want to allow insertion of object
    else if (!(o instanceof workflow_connector)&&!(o instanceof workflow_connector_edge)) {
            workflow_object obj=(workflow_object)o;
            Popup_selection=obj;
            createPopup(Popup_selection);
            popupObject.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            if (Popup_selection!=null) Popup_selection.selected=false;
    } 
}

public void leftClick(Object o) {
    if (o!=null) {
        workflow.updateDependance(); //Be sure we are up to date
        if (o instanceof workflow_connector) {
              if (((workflow_connector)o).isOutputAll()||((workflow_connector)o).getOutput().size()>4) {
                           workflow_object obj=((workflow_connector)o).parent;
                            Popup_selection=obj;
                            createPopupManyOutput(Popup_selection);
                            popupObject.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                            if (Popup_selection!=null) Popup_selection.selected=false;
                     } else {      
                            workflow.createOutput_Objects((workflow_connector)o);
                      }       
              }
              
        if (o instanceof workflow_object&&!small_editor_mode) {
            workflow_properties tmp=((workflow_object)o).getProperties();
            //--DEbug Config.log(tmp.getProperties());
            //workbox.Message("Loading "+tmp.getName(), "");
            this.propertieseditorbox.display(tmp, this);
            //workbox.Message("Idle", "");
        }
    }
}

@Override
public void mouseReleased() {
      workflow.notMoving();
      redraw();
  } //End mouse dragged

@Override
public void mouseMoved() {
     redraw();
  } //End mouse moved



////////////////////////////////////////////////////////////////////////////////
/// Object creation

/**
 * This is a special fucntion for the workflow_preview
 * This destroy all object and create this one
 * @param obj
 */
public workflow_object updateWorkflow(workflow_properties obj) {
    //--Get current x and y position if foung
    if (!isInitialized()) {
        while(!isInitialized()){}; //Hack: do better latter
    }
    if (workflow.work.size()>0) {
        workflow_object tmp=workflow.work.get(0);
        obj.put("x", tmp.getProperties().get("x"));
        obj.put("y", tmp.getProperties().get("y"));
    }
    //--Remove workflow element
    workflow.selectAll();
    workflow.deleteSelected();
    //--Re-add object
    workflow_object o=createObject(obj);
    force_redraw=true;
    redraw();
    return o;
}

/**
 * This is a special fucntion for the workflow_preview
 * This destroy all object and create this one
 * @param obj
 */
public void updateCurrentWorkflow(workflow_properties obj) {
    //--Get current x and y position if foung
    if (!isInitialized()) {
        while(!isInitialized()){}; //Hack: do better latted
    }
    if (workflow.work.size()>0) {
        for (workflow_object o:workflow.work) {
            //--TO DO: Be sure we have a good id...
            if (o.properties.getID().equals(obj.getID())) {
                //--We need to delete some connector? -- Invalidate to edge...
                if (o.properties.getInt("NbInput")!=obj.getInt("NbInput")) {                
                    //--WE need to create some input for the object
                    if (obj.getInt("NbInput")>o.properties.getInt("NbInput")) {                        
                        //--Note: since the connector are finally just an array
                    }
                    //--WE need to delete some input and some connected edge...
                    if (obj.getInt("NbInput")<o.properties.getInt("NbInput")) {
                        //--Delete all the edge connected to this object trough the port    
                    }
                }   
                o.properties=obj;
                
                
            }
        }
    }
    force_redraw=true;
    redraw();
}
/**
 * This is a special function for the workflow_preview
 * This create the obj without destroying other object
 * @param obj
 */
public workflow_object createObjectWorkflow(workflow_properties obj) {
    workflow_object tmp=createObject(obj);
    //We can used the workflow even if it is not a graphic workflow...
    if (isInitialized()) {
        force_redraw=true;
        redraw();
    }
    return tmp;
}

/**
 * Class used to put new object on the workflow
 * @param object_name (String)
 */
public workflow_object createObject(workflow_properties obj, Point location) {
    //System.out.println(obj);
    setChanged(true);
    //--Set properties x and y
    obj.put("x", location.x);
    obj.put("y", location.y);
    //--Switch for the differents object...
    workflow_object tmp=null;
    if (!obj.get("ObjectType").equals(workflow_properties.NotSet)) {
       
        // CASE 2: Else, we create objects
        if (obj.get("ObjectType").equals("For")||obj.get("ObjectType").equals("While")) {
            tmp=new workflow_object_aggregator(obj,(int)location.getX(), (int)location.getY());
        } else
        if (obj.get("ObjectType").equals("If")) {
            tmp=new workflow_object_if(obj,(int)location.getX(), (int)location.getY());
        } else
         if (obj.get("ObjectType").equals("Begin")) {
             //--Note, we only want 1 begin object
             workflow_object dummy=new workflow_object(obj,(int)location.getX(), (int)location.getY());
             if (nbBeginObject()==0||workflow.isInsideAggregation(dummy)) {
                 tmp=new workflow_object_BeginEnd(obj,(int)location.getX(), (int)location.getY());
             } else {
                 if (parent!=null) {
                    //--Note: deprecated
                     JOptionPane.showMessageDialog(parent, "Warning! You can only have one Begin in your workflow");
                 }
             }
        } else
            if (obj.get("ObjectType").equals("End")) {
                tmp=new workflow_object_BeginEnd(obj,(int)location.getX(), (int)location.getY());
        } else
         if (obj.get("ObjectType").equals("Script")) {
            tmp=new workflow_object_script(obj,(int)location.getX(), (int)location.getY());
        } else
        if (obj.get("ObjectType").equals("ScriptBig")) {
            tmp=new workflow_object_script_big(obj,(int)location.getX(), (int)location.getY());
        } else
        if (obj.get("ObjectType").equals("Output")) {
             tmp=workflow.createOutput_Object(obj,(int)location.getX(), (int)location.getY());
        } else
        if (obj.get("ObjectType").equals("OutputDatabase")) {
             tmp=new workflow_object_output_database(obj,(int)location.getX(), (int)location.getY());
        } else
        if (obj.get("ObjectType").equals("Variable")) {
             tmp=new workflow_object_variable(obj,(int)location.getX(), (int)location.getY());
        } else
        {
            //By default a Program...
            tmp=new workflow_object(obj, (int)location.getX(), (int)location.getY());
        }
        //--Finally, add to workflow        
        if (tmp!=null) workflow.add(tmp);        
    }
    return tmp;
}
/**
 * This will create the specified variable on the workflow or update it.
 * @param name
 * @param value
 * @param visible
 * @return
 */
public workflow_properties setVariable(String name, String value, boolean visible) {
    workflow_properties tmp=null;
    //1. Find possible variable
 
    //2. No variable found in the workflow, add one...
         tmp.setName("Undefined Variable");
         tmp.put("colorMode","GREEN");
         tmp.put("defaultColor","GREEN");
         tmp.put("Output"+"Text", "True");
         tmp.put("outputType", "Text");
         tmp.put("InputAll","Connector0");
         tmp.put("Connector1Output", "True");
         //tmp.put("Connector0Conditional", "True");
         tmp.put("ObjectType", "Variable");
         //tmp.put("output_"+"variable"+"_id", id);
         tmp.put("EditorClassName","editors.VariableEditor");

    return tmp;
}

/**
 * Class used to put new object on the workflow
 * 
 */
public workflow_object createObject(workflow_properties obj, int x, int y) {
    Point p=new Point(x,y);
    return createObject(obj,p);
}

/**
 * Class used to put new object on the workflow
 * @param object_name (String)
 */
public workflow_object createObject(workflow_properties obj) {
    Point p=new Point(obj.getInt("x"),obj.getInt("y"));
    if (p.x==0&&p.y==0||p.x<0||p.y<0) {
        p.x=135;
        p.y=20;
    }
    return createObject(obj,p);
}
////////////////////////////////////////////////////////////////////////////////
/// Verification fucntion

/**
 * Count the number of begin object
 * @return
 */
public int nbBeginObject() {
    int i=0;
    for (workflow_object o:workflow.work) {
         if (o.getProperties().get("ObjectType").equals("Begin")) {
             //-- Is it in a for loop? If not increase, if True don't count...
             if (!workflow.isInsideAggregation(o)) i++;
         }
    }
    return i;
}

////////////////////////////////////////////////////////////////////////////////
/// Drag and drop

DropTarget dt = new DropTarget(this, new DropTargetListener() {
  public void dragEnter(DropTargetDragEvent event) {event.acceptDrag(DnDConstants.ACTION_COPY);}
  public void dragExit(DropTargetEvent event) {}
  public void dragOver(DropTargetDragEvent event) {event.acceptDrag(DnDConstants.ACTION_COPY);}
  public void dropActionChanged(DropTargetDragEvent event) {}
  public void drop(DropTargetDropEvent event) {
      event.acceptDrop(DnDConstants.ACTION_COPY);
      Transferable transferable = event.getTransferable();
     
      DataFlavor flavors[] = transferable.getTransferDataFlavors();
      Point loc=event.getLocation();
      //--Buffer to know the already loaded file
//      Vector<String>already_loaded=new Vector<String>();
//      already_loaded.clear();
      boolean fileLoaded=false;
      workflow.resetSelected(); //--Reset Selection
      int count=0;              //--count the actual number of object dropped
      for (int i = 0; i < flavors.length; i++) {
          try {
                //--Drag and drop a file?
                if (flavors[i].isMimeTypeEqual(DataFlavor.javaFileListFlavor)) {
                   Object o = transferable.getTransferData(flavors[i]);
                   if (o instanceof java.util.List) {
                        workflow.saveToUndo("Insert some files into Workflow");
                        //--Get the file list
                        java.util.List<File> list=(java.util.List<File>)o;
                        //--Ok, we have files, we create a new DATAFILES.properties object
                        for (File file:list) {
                           String filename=file.getAbsolutePath().toLowerCase();
                           //--Note, we shoud ask for each file or do a multiple load dialog here... 
                           if (filename.endsWith("fasta")||filename.endsWith("fa")||filename.endsWith("phy")||filename.endsWith("phylip")) {
                                     StandardInputSequenceJDialog jd=new StandardInputSequenceJDialog(frame, filename,"Import file", "");
                                    jd.setVisible(true);
                                    if (jd.getStatus()==Config.status_done) {                                       
                                       loadSequences(file,jd.getCollectionName(),jd.getComments(), loc, jd.getSequenceType());//load Files using a Swing worker
                                    }
                               } else
                               if (filename.endsWith("new")||filename.endsWith("newick")||filename.endsWith("tree")||filename.endsWith("tre")||filename.endsWith("nh")||filename.endsWith("nhx")) {
                                     RenameMultipleTreesJDialog jd=new RenameMultipleTreesJDialog(frame, filename,"Import tree");
                                    jd.setVisible(true);
                                    if (jd.getStatus()==Config.status_done) {                                        
                                        loadTree(file,jd.getCollectionName(),jd.getComments(), loc);//load Files using a Swing worker                                      
                                    }
                               } else
                                 //--Workflow? //Beta
                                if (filename.endsWith(".db")) {
                                    //--Load project... Note: no save verification
                                    workbox.loadProject(filename);                                      
                               } else
                               if (testWorkflow(filename)) {
                                   //--Load file as workflow
                                   workbox.loadWorkflowAsTxt(filename);                               
                               } else 
                                   if (testmyExperiment(filename)) {
                                   //--Load file as workflow
                                   workbox.loadWorkflowAsmyExperiment(filename);                               
                               } else { 
                               //--Load file as TextFile
                                    loadTextFile(file, loc);                                
                               }

                        }
                         
                    }
                }
                //--Drag and drop a class

                if (flavors[i].isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType)) {
                    Object o = transferable.getTransferData(flavors[i]);
                    //-- Debug Config.log(o.getClass());

                    String object_string=(String)transferable.getTransferData(flavors[i]);
                    //--Debug
                    // System.out.println(object_string);
                    if (object_string.startsWith("properties")) {
                        
                        //--Load the file
                        String object_filename=object_string.substring(object_string.indexOf("\t")+1);
                        workflow_properties tmp=new workflow_properties();
                        tmp.load(object_filename);                        
                        //--Save to Undo list
                        workflow.saveToUndo("Inserting "+tmp.getName());
                        Point newloc=new Point((int)loc.getX()+count*10, (int)loc.getY()+count*10);
                        createObject(tmp, newloc);
                    } else
                      //--Workflow drag and drop
                    if (object_string.startsWith("Workflows")) {
                        String data[]=object_string.split("\t");
                        //String outputType=data[0];
                        String id=data[1];
                        //String desc=data[2];
                         if (id.isEmpty()||id.equals("0")) {
                              try {
                                workflow_properties tmp=new workflow_properties();
                                String description=data[2];
                                        //object_string.substring(object_string.indexOf("\t")+1);
                                //if (id.isEmpty()||id.equals("0")) return;
                                 tmp.setName("Workflows");
                                 tmp.put("colorMode","GREEN");
                                 tmp.put("defaultColor","GREEN");
                                 tmp.put("Output"+"Workflows", "True");
                                 tmp.put("outputType", "Workflows");
                                 tmp.put("Connector1Output","True");
                                 tmp.put("Connector0Output", "True");
                                 tmp.put("Connector0Conditional", "True");
                                 tmp.put("ObjectType", "OutputDatabase");
                                 tmp.put("output_workflows_id", id);
                                 if (id.isEmpty()||id.equals("0")) {
                                        tmp.put("Description", "Undefined");
                                        tmp.put("output_workflows_id", 0);
                                 } else {
                                        tmp.put("Description", description);
                                 }                             
                                 Point newloc=new Point((int)loc.getX()+count*10, (int)loc.getY()+count*10);
                                 createObject(tmp, newloc);
                                 count++;
                            } catch(Exception e) {Config.log("Error drag and drop to workflow object : "+object_string);}
                         } else {
                             //--Try to load workflow
                             int workflow_id=Integer.valueOf(id);
                             if (workflow_id>0) {
                                 workbox.loadWorkflowFromDatabase(workflow_id);
                             }
                         }
                    } else if (object_string.startsWith("RunWorkflow")) {
                        String data[]=object_string.split("\t");
                        //String outputType=data[0];
                        String id=data[1];
                        //String desc=data[2];
                         if (id.isEmpty()||id.equals("0")) return;
                         int RunWorkflow_id=Integer.valueOf(id);
                         RunWorkflow run=new RunWorkflow(RunWorkflow_id);
                         workbox.setRunWorkflow(run);
                         if (run.getOriginal_workflow_id()>0) {
                             workbox.loadWorkflowFromDatabase(run.getOriginal_workflow_id());
                         }
                    } else
                        if (object_string.startsWith("Variable")) {
                            try {
                            workflow_properties tmp=new workflow_properties();
                            String data[]=object_string.split("\t");
                            String outputType=data[0];
                                    //object_string.substring(0,object_string.indexOf("\t"));
                            String id=data[1];
                            String description=data[2];
                                    //object_string.substring(object_string.indexOf("\t")+1);
                            //if (id.isEmpty()||id.equals("0")) return;
                             tmp.setName("Undefined Variable");
                             tmp.put("colorMode","GREEN");
                             tmp.put("defaultColor","GREEN");
                             tmp.put("Output"+"Text", "True");
                             tmp.put("outputType", "Text");
                             tmp.put("InputAll","Connector0");
                             tmp.put("Connector1Output", "True");
                             //tmp.put("Connector0Conditional", "True");
                             tmp.put("ObjectType", "Variable");
                             tmp.put("output_"+"variable"+"_id", id);
                             tmp.put("EditorClassName","editors.VariableEditor");
//                             if (id.isEmpty()||id.equals("0")) {
//                                    tmp.put("Description", "Undefined");
//                                    tmp.put("output_"+outputType.toLowerCase()+"_id", 0);
//                             } else {
//                                    tmp.put("Description", description);
//                             }
                            Point newloc=new Point((int)loc.getX()+count*10, (int)loc.getY()+count*10);
                            // newloc.translate(i*10, i*10);
                             createObject(tmp, newloc);
                             count++;
                            } catch(Exception e) {Config.log("Error drag and drop to workflow object : "+object_string);}
                        }
                    else
                   //--Output database
                     for (String s:workflow_properties_dictionnary.InputOutputType) {
                        if (object_string.startsWith(s)) {
                            try {
                            workflow_properties tmp=new workflow_properties();
                            String data[]=object_string.split("\t");
                            String outputType=data[0];
                                    //object_string.substring(0,object_string.indexOf("\t"));
                            String id=data[1];
                            String description=data[2];
                                    //object_string.substring(object_string.indexOf("\t")+1);
                            //if (id.isEmpty()||id.equals("0")) return;
                             tmp.setName(outputType);
                             tmp.put("colorMode","GREEN");
                             tmp.put("defaultColor","GREEN");
                             tmp.put("Output"+outputType, "True");
                             tmp.put("outputType", outputType);
                             tmp.put("InputAll","Connector0");
                             tmp.put("Connector1Output","True");
//                             tmp.put("Connector0Output", "True");
//                             tmp.put("Connector0Conditional", "True");
                             tmp.put("ObjectType", "OutputDatabase");                             
                             tmp.put("output_"+outputType.toLowerCase()+"_id", id);
                             if (id.isEmpty()||id.equals("0")) {
                                    tmp.put("Description", "Undefined");
                                    tmp.put("output_"+outputType.toLowerCase()+"_id", 0);
                             } else {
                                    tmp.put("Description", description);
                             }
                            Point newloc=new Point((int)loc.getX()+count*10, (int)loc.getY()+count*10);
                            // newloc.translate(i*10, i*10);
                             createObject(tmp, newloc);
                             count++;
                            } catch(Exception e) {Config.log("Error drag and drop to workflow object : "+object_string);}
                        }
                    }
                }
          } catch (Exception e) {e.printStackTrace();}
          force_redraw=true;
          redraw();
        } // End for flavors
     if (fileLoaded) {
      
     }
      Toolbox tool=new Toolbox();
      tool.reloadCurrentWorkflowsTree(current);
   } //--End drop
}
    
);

/**
 * Create a workflow properties for the specified type 
 * 
 * @param type (MultipleSequences, etc...)
 * @param x
 * @param y
 * @return a new workflow_properties
 */
public workflow_properties createTypeObject(String type, int x, int y) {
          
    workflow_properties tmp=new workflow_properties();
    //--Variables 
    String outputType=type;
    String id="0";
    String description=type;

     tmp.setName(outputType);
     tmp.put("colorMode","GREEN");
     tmp.put("defaultColor","GREEN");
     tmp.put("Output"+outputType, "True");
     tmp.put("outputType", outputType);
     tmp.put("InputAll","Connector0");
     tmp.put("Connector1Output","True");
     tmp.put("ObjectType", "OutputDatabase");                             
     tmp.put("output_"+outputType.toLowerCase()+"_id", id);
     if (id.isEmpty()||id.equals("0")) {
            tmp.put("Description", "Undefined");
            tmp.put("output_"+outputType.toLowerCase()+"_id", 0);
     } else {
            tmp.put("Description", description);
     }
    tmp.put("x", x);
    tmp.put("y", y); 
  
    return tmp;
    
}

/**
 * Ttest if a file is an Armadillo workflow
 *  @param filename
 * @return Test
 */
public boolean testWorkflow(String filename) {
    try {
       Results results=new Results(filename);
        if (results.getText().startsWith("# Armadillo workflow")) return true;        
    } catch(Exception e) {
        return false;
    }
    return false;    
}

/**
 * Ttest if a file is an myExperiment workflow
 *  @param filename
 * @return Test
 */
public boolean testmyExperiment(String filename) {
    try {
       Results results=new Results(filename);        
       
       if (results.getText().contains("<workflow uri=\"http://www.myexperiment.org/workflow.xml")) return true;        
    } catch(Exception e) {
        return false;
    }
    return false;    
}

   /**
     * Simple thread to load file (sequences, multiplesequences, alignment) into the project
     */
    protected void loadSequences(final File file, final String groupname, final String note, final Point loc, final String type) {
        final LinkedList<File> toLoad=new LinkedList<File>();
        final int totalToLoad=1;
        toLoad.add(file);

        SwingWorker<Integer, Integer> loadSwingWorker2=new SwingWorker<Integer, Integer>() {
            String filename="";

            @Override
            protected Integer doInBackground() throws Exception {
                //We dont check for cancelled

                while (!isCancelled()&&toLoad.size()>0) {
                    File f=toLoad.pollFirst();
                    filename=f.getAbsolutePath();
                    MultipleSequences multi=new MultipleSequences(filename);
                    for (Sequence s:multi.getSequences()) s.setSequence_type(type);
                    multi.setName(groupname);
                    multi.setNote(note);
                    multi.saveToDatabase();
                    setProgress((totalToLoad-toLoad.size())*100/totalToLoad);
                   publish(multi.getId());
                 }
                return 0;
            }

            @Override
            public void process(java.util.List<Integer> chunk) {
                for (Integer o:chunk) {

                        if (o==0) {
                            loading.MessageErreur("Unable to load file "+file.getName(), "");
                        } else {
                            loading.Message("Loading successfull of "+file.getName(),"");
                            workflow_properties tmp=new workflow_properties();
                            String outputType="MultipleSequences";
                             tmp.setName(outputType);
                             tmp.put("colorMode","GREEN");
                             tmp.put("defaultColor","GREEN");
                             tmp.put("Output"+outputType, "True");
                             tmp.put("outputType", outputType);
                             tmp.put("Connector1Output","True");
                             tmp.put("Connector0Output", "True");
                             tmp.put("Connector0Conditional", "True");
                             tmp.put("ObjectType", "OutputDatabase");
                             tmp.put("editorClass", "editors.OutputEditor");
                             tmp.put("Description", file.getName());
                             tmp.put("output_"+outputType.toLowerCase()+"_id", o);
                             loc.setLocation(loc.x, loc.y+30);
                             createObject(tmp, loc);
                             }
                } //--End list of Object
            } //End process

            @Override
            public void done() {
                Toolbox tool=new Toolbox();
                tool.reloadDatabaseTree();
                loading.setVisible(false);
            }
        }; //End SwingWorker definition

        loadSwingWorker2.addPropertyChangeListener(
                 new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                        if ("progress".equals(evt.getPropertyName())) {
                            SwingWorker o = (SwingWorker)evt.getSource();
                            if (!o.isDone()) {
                                int progress=(Integer)evt.getNewValue();
                                loading.setProgress(progress);
                            }
                            else if (o.isDone()&&!o.isCancelled()) {
                               //Handled in done() fucntion in SwingWorker
                            }
                        }//End progress update
                 } //End populateNetworkPropertyChange
                 });
        //Finally. Show a load dialog :: Warning Work-In-Progress
        loading=new InformationJDialog(frame, false, loadSwingWorker2,"");
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Loading sequence(s) file...", "");
        loadSwingWorker2.execute();
    }


  /**
     * Simple thread to load file (sequences, multiplesequences, alignment) into the project
     */
    protected void loadTree(final File file, final String groupname, final String note, final Point loc) {
        final LinkedList<File> toLoad=new LinkedList<File>();
        final int totalToLoad=1;
        toLoad.add(file);

        SwingWorker<Integer, Integer> loadSwingWorker2=new SwingWorker<Integer, Integer>() {
            String filename="";

            @Override
            protected Integer doInBackground() throws Exception {
                //We dont check for cancelled
                while (!isCancelled()&&toLoad.size()>0) {
                    File f=toLoad.pollFirst();
                    filename=f.getAbsolutePath();
                    MultipleTrees multi=new MultipleTrees();
                    multi.readNewick(filename);
                    multi.replaceSequenceIDwithNames();
                    multi.setName(groupname);
                    multi.setNote(note);
                    multi.saveToDatabase();
                    setProgress((totalToLoad-toLoad.size())*100/totalToLoad);
                    for (Tree t:multi.getTree()) publish(t.getId());
                 }
                return 0;
            }

            @Override
            public void process(java.util.List<Integer> chunk) {
                for (Integer o:chunk) {
                        if (o==0) {
                            loading.MessageErreur("Unable to load file "+file.getName(), "");
                        } else {
                            loading.Message("Loading successfull of "+file.getName(),"");
                            workflow_properties tmp=new workflow_properties();
                            String outputType="Tree";
                             tmp.setName(outputType);
                             tmp.put("colorMode","GREEN");
                             tmp.put("defaultColor","GREEN");
                             tmp.put("Output"+outputType, "True");
                             tmp.put("outputType", outputType);
                             tmp.put("Connector1Output","True");
                             tmp.put("Connector0Output", "True");
                             tmp.put("Connector0Conditional", "True");
                             tmp.put("ObjectType", "OutputDatabase");
                             tmp.put("editorClass", "editors.OutputEditor");
                             tmp.put("Description", file.getName());
                             tmp.put("output_"+outputType.toLowerCase()+"_id", o);
                             loc.setLocation(loc.x, loc.y+30);
                             createObject(tmp, loc);
                             }
                } //--End list of Object
            } //End process

            @Override
            public void done() {
               Toolbox tool=new Toolbox();
               tool.reloadDatabaseTree();
               loading.setVisible(false);

            }            
        }; //End SwingWorker definition
        loading=new InformationJDialog(frame, false, loadSwingWorker2,"");
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Loading tree(s) file...", "");
        loadSwingWorker2.execute();
    }

    /**
     * Simple thread to load file (sequences, multiplesequences, alignment) into the project
     */
    protected void loadTextFile(final File file, final Point loc) {
        final LinkedList<File> toLoad=new LinkedList<File>();

        toLoad.add(file);

        SwingWorker<Integer, Integer> loadSwingWorker2=new SwingWorker<Integer, Integer>() {
            String filename="";

            @Override
            protected Integer doInBackground() throws Exception {
                //We dont check for cancelled
                while (!isCancelled()&&toLoad.size()>0) {
                    File f=toLoad.pollFirst();
                    filename=f.getAbsolutePath();
                    Text text=new Text(filename);                    
                    text.setName(f.getName()+" loaded at "+Util.returnCurrentDateAndTime());
                    text.setNote("Full path: "+filename+"\nLoaded on "+Util.returnCurrentDateAndTime());
                    text.saveToDatabase();
                    publish(text.getId());
                 }
                return 0;
            }

            @Override
            public void process(java.util.List<Integer> chunk) {
                for (Integer o:chunk) {
                        if (o==0) {
                            loading.MessageErreur("Unable to load file "+file.getName(), "");
                        } else {
                            loading.Message("Loading successfull of "+file.getName(),"");
                            workflow_properties tmp=new workflow_properties();
                            String outputType="Text";
                             tmp.setName(outputType);
                             tmp.put("colorMode","GREEN");
                             tmp.put("defaultColor","GREEN");
                             tmp.put("Output"+outputType, "True");
                             tmp.put("outputType", outputType);
                             tmp.put("Connector1Output","True");
                             tmp.put("Connector0Output", "True");
                             tmp.put("Connector0Conditional", "True");
                             tmp.put("ObjectType", "OutputDatabase");
                             tmp.put("editorClass", "editors.OutputEditor");
                             tmp.put("Description", file.getName());
                             tmp.put("output_"+outputType.toLowerCase()+"_id", o);
                             loc.setLocation(loc.x, loc.y+30);
                             createObject(tmp, loc);
                             }
                } //--End list of Object
            } //End process

            @Override
            public void done() {
               Toolbox tool=new Toolbox();
               tool.reloadDatabaseTree();
               loading.setVisible(false);

            }
        }; //End SwingWorker definition
        loading=new InformationJDialog(frame, false, loadSwingWorker2,"");
        loading.setProgress(0); //Put 0% as the start progress
        loading.Message("Loading text file...", "");
        loadSwingWorker2.execute();
    }


////////////////////////////////////////////////////////////////////////////////
/// Various functions

/**
 * Function to return a unique number
 * @return a unique number 
 */
public int getHashcode() {
    return this.hashCount++;
}

/**
 *
 * @return true if some object are selected
 */
public boolean isSelection() {
    for (workflow_object obj:workflow.work) {
        if (obj.selected) return true;
    }
    return false;        
}

public void resetSelectionState() {
    for (workflow_object obj:this.workflow.work) {
        if (obj.selected) {
            obj.getProperties().remove("Status");
            obj.getProperties().remove("StatusString");
            obj.getProperties().remove("CommandLine_Running");
        }
    }
}

/**
 * Copy current selected object into a buffer for Copy&Paste
 */
public void copySelection() {
    //--clear selection
    CopyPaste_selection.work.clear();
    CopyPaste_selection.work_connection.clear();
    Paste_count=0;

    for (workflow_object obj:this.workflow.work) {
        if (obj.selected) {
            CopyPaste_selection.work.add(obj);
        }
    }
    for (workflow_connector_edge obj:this.workflow.work_connection) {
        if (CopyPaste_selection.work.contains(obj.getDestinationParent())||CopyPaste_selection.work.contains(obj.getSourceParent())) {
            CopyPaste_selection.work_connection.add(obj);
        }
    }
}

/**
 * Paste the previously copySelection into the workflow
 */
public void pasteSelection() {
    HashMap<workflow_object,workflow_object>oldnew=new HashMap<workflow_object,workflow_object>();
    Paste_count++;
    for (workflow_object old_object:this.CopyPaste_selection.work) {
        //--Create a new properties
        workflow_properties prop=old_object.getProperties();
        workflow_properties newprop=new workflow_properties();
        newprop.deserializeFromString(prop.serializeToString());
        //--Create and select object
        int x=newprop.getInt("x")+(30*Paste_count);
        int y= newprop.getInt("y")+(30*Paste_count);
        // TO DO CLipping here
        workflow_object new_object=createObject(newprop, x,y);
        new_object.selected=true;
        oldnew.put(old_object,new_object);
    }
    
    for (workflow_connector_edge obj:this.CopyPaste_selection.work_connection) {
            workflow_properties prop=obj.getProperties();            
            workflow_object source=oldnew.get(obj.getSourceParent());
            workflow_object dest=oldnew.get(obj.getDestinationParent());
            //--Handle case when source/dest is not in selection
            if (source==null) source=obj.getSourceParent();
            if (dest==null) dest=obj.getDestinationParent();
            //--Get the actual connector
            int source_connectorNb=obj.source.number;
            int dest_connectorNb=obj.dest.number;
             workflow_connector source_connector=source.connection[source_connectorNb];
             workflow_connector dest_connector=dest.connection[dest_connectorNb];
            if (source_connector!=null&&dest_connector!=null) {
                boolean notDeletabled=false;
                if (prop.isSet("notDeletabled")) notDeletabled=prop.getBoolean("notDeletabled");
                workflow.addConnector(source_connector, dest_connector, "",notDeletabled);
            } else {
//                Config.log(source+" "+source_connector);
//                Config.log(dest+" "+dest_connector);
            }
    }
    
}

/**
 * Handle keypressed: presently only in debug mode
 */

////////////////////////////////////////////////////////////////////////////////
/// Keyboard

    @Override
    public void keyPressed(KeyEvent arg0) {
        super.keyPressed(arg0);
        int keycode=arg0.getKeyCode();
        
       switch(keycode) {
           //--Fast display current and next workflow 
           case KeyEvent.VK_LEFT :
              workbox.loadPreviousWorkflow();
            break;
            case KeyEvent.VK_RIGHT  :
                workbox.loadNextWorkflow();
            break;
           //--Snapshot (fast screenshot)     
            case KeyEvent.VK_F1  :
                while(Util.FileExists(Config.currentPath+File.separator+"image"+images_counter+".png")) images_counter++;
                System.out.println("saving "+Config.currentPath+File.separator+"image"+images_counter+".png");
                saveImage(Config.currentPath+File.separator+"image"+images_counter+".png");
                images_counter++;
            break;    
           //--Decompose component
            case KeyEvent.VK_F2  :
                 decompose();
            break;         
                
           //--Display edge informations
           case KeyEvent.VK_SPACE:  workflow.displayEdge(); break;
           //--Delete current selection
           case KeyEvent.VK_DELETE: workflow.deleteSelected();break;
               case KeyEvent.VK_BACK_SPACE: workflow.deleteSelected();break;
            //--CTRL-A (Select All Objects)
            case KeyEvent.VK_A :
                if ((arg0.getModifiers() & InputEvent.CTRL_MASK) !=0)workflow.selectAll();
            break;
            //--Ctrl-Ins (Copy)
            case KeyEvent.VK_INSERT:
                if ((arg0.getModifiers() & InputEvent.CTRL_MASK) !=0) {
                    //--Save selection into buffer
                    copySelection();
                } 
                 //--Shift-Ins (Paste)
                if ((arg0.getModifiers() & InputEvent.SHIFT_MASK) !=0) {
                    //--Paste selection into buffer
                     pasteSelection();
                }
                break;
           //--Ctrl-C (Copy)
          case KeyEvent.VK_C:
                if ((arg0.getModifiers() & InputEvent.CTRL_MASK) !=0) {
                    //--Save selection into buffer
                    copySelection();
                }
                break;
         //--Ctrl-V (Paste)
         case KeyEvent.VK_V:
                if ((arg0.getModifiers() & InputEvent.CTRL_MASK) !=0) {
                    //--Save selection into buffer
                    pasteSelection();
                }
                break;
          //--Ctrl-S (Save current workflow)
         case KeyEvent.VK_S:
                if ((arg0.getModifiers() & InputEvent.CTRL_MASK) !=0) {
                    //--Save workflow
                    workbox.getCurrentWorkflows().setId(0);
                    workbox.saveWorkflowToDatabase("Saved on "+Util.returnCurrentDateAndTime());
                }
                break;
         //--CTRL-N (New workflow)
            case KeyEvent.VK_N :
                if ((arg0.getModifiers() & InputEvent.CTRL_MASK) !=0) {
                    workbox.newWorkflow();
                }
            break;
           //--CTRL-P (Test, save pdf)
            case KeyEvent.VK_P :
                if ((arg0.getModifiers() & InputEvent.CTRL_MASK) !=0) {
                    this.SaveWorkflowPDF();
                    
                }
            break;     
                
            //--CTRL-Z (Undo) - Deta
            case KeyEvent.VK_Z :
                if ((arg0.getModifiers() & InputEvent.CTRL_MASK) !=0)workflow.Undo();
            break;
            case KeyEvent.VK_R :
                if ((arg0.getModifiers() & InputEvent.CTRL_MASK) !=0) {
                    workbox.Run();
                }
            break;    
       }
       redraw();
        //--Debug
       if (debug) Config.log("keycode: "+keycode+"keyEvent: "+arg0);
    }

////////////////////////////////////////////////////////////////////////////////
/// Various helper (getter/setter)    
    
    /**
     * @return the save_filename
     */
    public String getSave_filename() {
        return save_image_filename;
    }

    /**
     * @param save_filename the save_filename to set
     */
    public void setSave_filename(String save_filename) {
        this.save_image_filename = save_filename;
    }

    /**
     * @return the initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @param initialized the initialized to set
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * @return the changed
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * @param changed the changed to set
     */
    public void setChanged(boolean changed) {
        //-- debug System.out.println("Workflow changed "+changed);
        this.changed = changed;
    }

    /**
saveasjpg taken from http://wiki.processing.org/index.php/Save_as_JPEG
@author Yonas Sandbæk
*/ 
byte[] bufferImage(PImage srcimg) {
//  ByteArrayOutputStream out = new ByteArrayOutputStream();
//  BufferedImage img = new BufferedImage(srcimg.width, srcimg.height, 2);
//  img = (BufferedImage) createImage(srcimg.width,srcimg.height);
//  for (int i = 0; i < srcimg.width; i++)
//    for (int j = 0; j < srcimg.height; j++)
//      img.setRGB(i, j, srcimg.pixels[j * srcimg.width + i]);
//  try {
//    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//    JPEGEncodeParam encpar = encoder.getDefaultJPEGEncodeParam(img);
//
//    encpar.setQuality(1.0f, false);
//    encoder.setJPEGEncodeParam(encpar);
//    encoder.encode(img);
//  }
//  catch (Exception ie) {
//    System.out.println(ie);
//  }
//  return out.toByteArray();
    System.out.println("Deprecated save to jpeg");
    return null;
}
    

/**
 * Simple vertex class used by the workflow_object for position and detection
 * of insideness
 */
class pvertex {
  public float x;
  public float y;
  

  public pvertex(float x, float y) {
  this.x=x;
  this.y=y;
  }

  public pvertex(int x, int y) {
    this.x=(float)x;
    this.y=(float)y;
    }

  }


/**
* The main workflow representation
*/
public class Workflow {

  //////////////////////////////////////////////////////////////////////////////
  /// Variables
  public Vector<workflow_object> work=new Vector<workflow_object>();           //List of workflow_object
  public Vector<workflow_connector_edge> work_connection=new Vector<workflow_connector_edge>();//List of workflow_connector_edge
  Object selected=null;               //Selected object (workflow_connector or workflow_object)
  boolean selectedWasConnected=false; //Just to be sure we don't loose the state of the object

  private armadillo_workflow armadillo; //pointer to the Armadillo_workflow (parent Armadillo workflow)

  //////////////////////////////////////////////////////////////////////////////
  /// Database needed variables (TO DO : Getter/ Setter)
  public int workflow_id=0;
  public String name=""; //This workflow_name
  public String filename="";                         //This workflow filename

  //- Debug
  int count=0;                        //counter for edge display numbering


////////////////////////////////////////////////////////////////////////////////
/// Save and Load function

//   public boolean saveWorkflow(String filename) {
//        try {
//            //df.createWorkflow(workflow); //We create a database copy to have the properties_id;
//            int next=df.getNextPropertiesID();
//            PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
//            pw.println("#Armadillo workflow "+config.get("version"));
//            for(workflow_object obj:work) {
//                 if (obj.getProperties().getProperties_id()==0) {
//                     obj.getProperties().put("properties_id",next);
//                     next++;
//                }
//                pw.println("Object");
//                pw.println(obj.getProperties().serializeToString());
//            }
//            for( workflow_connector_edge connector:work_connection) {
//                pw.println("Connector");
//                //
//                // # Hack to save the source and dest properties id...
//                connector.getProperties().put("source_properties_id", connector.getSourceParent().getProperties().getProperties_id());
//                connector.getProperties().put("dest_properties_id", connector.getDestinationParent().getProperties().getProperties_id());
//                pw.println(connector.getProperties().serializeToString());
//            }
//            pw.flush();
//            pw.close();
//            return true;
//        } catch(Exception e) {if (debug) e.printStackTrace(); return false;}
//    }
//
//    public boolean loadWorkflow(String filename) {
//        try {
//            StringBuffer st=new StringBuffer();
//            boolean modeObject=false;
//            boolean modeConnector=false;
//            //--Delete current workflow
//            this.selectAll();
//            this.deleteSelected();
//            //--load
//            BufferedReader br=new BufferedReader(new FileReader(new File(filename)));
//            while(br.ready()) {
//                String stri=br.readLine();
//                //Config.log(stri+" "+modeObject+" "+modeConnector);
//                //Skip comments
//                if (!stri.startsWith("#")) {
//                    //--Find mode
//                    if (stri.equals("Object")) modeObject=true;
//                    if (stri.equals("Connector")) modeConnector=true;
//                    //--Process String
//                    if (stri.equals("")) {
//                        if (modeObject) {
//                            modeObject=false;
//                            workflow_properties prop=new workflow_properties();
//                            prop.deserializeFromString(st.toString());
//                            createObjectWorkflow(prop);
//                            st.setLength(0);
//                        }
//                        if (modeConnector) {
//                            modeConnector=false;
//                            workflow_properties prop=new workflow_properties();
//                            prop.deserializeFromString(st.toString());
//                            int source_connectorNb=prop.getInt("source");
//                            int dest_connectorNb=prop.getInt("destination");
//                            int source_properties_id=prop.getInt("source_properties_id");
//                            int dest_properties_id=prop.getInt("dest_properties_id");
//                            workflow_connector source=this.getConnector(source_properties_id, source_connectorNb);
//                            workflow_connector dest=this.getConnector(dest_properties_id, dest_connectorNb);
//                            boolean notDeletabled=false;
//                            if (prop.isSet("notDeletabled")) notDeletabled=prop.getBoolean("notDeletabled");
//                            this.addConnector(source, dest, "",notDeletabled);
//                            st.setLength(0);
//                        }
//                    } else {
//                          st.append(stri+"\n");
//                    }
//                } //--End startWith #
//            } //--End while br.ready
//            force_redraw=true;
//            redraw();
//            br.close();
//            return true;
//        } catch(Exception e) {if (debug) e.printStackTrace();return false;}
//    }
//
//    public String workflowToString() {
//        StringBuilder pw=new StringBuilder();
//        try {
//            //df.createWorkflow(workflow); //We create a database copy to have the properties_id;
//            pw.append("#Armadillo workflow "+config.get("version")+"\n");
//            int next=df.getNextPropertiesID();
//            for(workflow_object obj:work) {
//                //We add new properties_id
//                if (obj.getProperties().getProperties_id()==0) {
//                     obj.getProperties().put("properties_id",next);
//                     next++;
//                }
//                pw.append("Object"+"\n");
//                pw.append(obj.getProperties().serializeToString()+"\n");
//            }
//            for( workflow_connector_edge connector:work_connection) {
//                pw.append("Connector"+"\n");
//                // # Hack to save the source and dest properties id...
//                connector.getProperties().put("source_properties_id", connector.getSourceParent().getProperties().getProperties_id());
//                connector.getProperties().put("dest_properties_id", connector.getDestinationParent().getProperties().getProperties_id());
//                pw.append(connector.getProperties().serializeToString()+"\n");
//            }
//            pw.append("\n");
//        } catch(Exception e) {if (debug) e.printStackTrace(); return "";}
//
//        return df.filter(pw.toString());
//    }
//
//     public boolean StringToWorkflow(String str) {
//        StringBuilder tmp=new StringBuilder();
//         str=df.defilter(str)+"\n";
//
//        try {
//            StringBuffer st=new StringBuffer();
//            boolean modeObject=false;
//            boolean modeConnector=false;
//            String[] stri_array=str.split("\n");
//            for (String stri:stri_array) {
//
//                //Skip comments
//                if (!stri.startsWith("#")) {
//                    //--Find mode
//                    if (stri.equals("Object")) modeObject=true;
//                    if (stri.equals("Connector")) modeConnector=true;
//                    //--Process String
//                    if (stri.equals("")) {
//                        if (modeObject) {
//                            modeObject=false;
//                            workflow_properties prop=new workflow_properties();
//                            prop.deserializeFromString(st.toString());
//                            createObjectWorkflow(prop);
//                            st.setLength(0);
//                        }
//                        if (modeConnector) {
//                            modeConnector=false;
//                            workflow_properties prop=new workflow_properties();
//                            prop.deserializeFromString(st.toString());
//                            int source_connectorNb=prop.getInt("source");
//                            int dest_connectorNb=prop.getInt("destination");
//                            int source_properties_id=prop.getInt("source_properties_id");
//                            int dest_properties_id=prop.getInt("dest_properties_id");
//                            workflow_connector source=this.getConnector(source_properties_id, source_connectorNb);
//                            workflow_connector dest=this.getConnector(dest_properties_id, dest_connectorNb);
//                            boolean notDeletabled=false;
//                            if (prop.isSet("notDeletabled")) notDeletabled=prop.getBoolean("notDeletabled");
//                            this.addConnector(source, dest, "",notDeletabled);
//                            //Config.log("Creating connector for ->"+source_properties_id+" to "+dest_properties_id);
//                            st.setLength(0);
//                        }
//                    } else {
//                            st.append(stri+"\n");
//                    }
//                } //--End startWith #
//            } //--End while br.ready
//             //--Hack final add in case we are short
//                if (modeObject) {
//                            modeObject=false;
//                            workflow_properties prop=new workflow_properties();
//                            prop.deserializeFromString(st.toString());
//                            createObjectWorkflow(prop);
//                            st.setLength(0);
//                        }
//                        if (modeConnector) {
//                            modeConnector=false;
//                            workflow_properties prop=new workflow_properties();
//                            prop.deserializeFromString(st.toString());
//                            int source_connectorNb=prop.getInt("source");
//                            int dest_connectorNb=prop.getInt("destination");
//                            int source_properties_id=prop.getInt("source_properties_id");
//                            int dest_properties_id=prop.getInt("dest_properties_id");
//                            workflow_connector source=this.getConnector(source_properties_id, source_connectorNb);
//                            workflow_connector dest=this.getConnector(dest_properties_id, dest_connectorNb);
//                            boolean notDeletabled=false;
//                            if (prop.isSet("notDeletabled")) notDeletabled=prop.getBoolean("notDeletabled");
//                            this.addConnector(source, dest, "",notDeletabled);
//                            //Config.log("Creating connector for ->"+source_properties_id+" to "+dest_properties_id);
//                            st.setLength(0);
//                        }
//            force_redraw=true;
//            redraw();
//            return true;
//        } catch(Exception e) {e.printStackTrace();return false;}
//    }

////////////////////////////////////////////////////////////////////////////////
// Some functions by Alix and Etienne for tree building

  public int get_nb_workflow_object(){
    return work.size();
  }
  public String get_workflow_object_name(int index){
    workflow_object wo =  (workflow_object)work.get(index);
    return wo.getProperties().getName();
  }
  public int get_nb_workflow_connector_edge(){
    return work_connection.size();
  }
  public String get_workflow_connector_edge_source_name(int index){
    workflow_connector_edge wce= (workflow_connector_edge)work_connection.get(index);
    return wce.source.parent.getProperties().getID();
  }
  public String get_workflow_connector_edge_dest_name(int index){
    workflow_connector_edge wce= (workflow_connector_edge)work_connection.get(index);
    return wce.dest.parent.getProperties().getID();
  }

//  /**
//   * This is a printout function of the execution tree
//   * @return
//   */
//  public String outputExecutionTree() {
//      LinkedList<workflow_object>Execution=new LinkedList<workflow_object>();
//      //--Find begin object
//      workflow_object begin=findBegin();
//      if (begin==null) return "Please, Insert a Begin object into the workflow...";
//      Execution.add(begin);
//      findObject(begin, Execution, 0);
//      String tmp="";
//      for (workflow_object obj:Execution) {
//          if (obj!=null) tmp+=obj.getProperties().getName()+"->";
//      }
//      return tmp;
//  }

  /**
   * Note: FOR THIS FUNCTION
   * Undefined == 0 value
   * @return
   */
  public Vector<workflow_properties>findAllUndefined() {
     Vector<workflow_properties>tmp=new Vector<workflow_properties>();
     for (workflow_object obj:work) {
         if (obj instanceof workflow_object_output_database) {
             workflow_properties prop=obj.getProperties();
             String outputType=prop.get("outputType");
             if (prop.getInt("output_"+outputType.toLowerCase()+"_id")==0) tmp.add(prop);
         }
     }
     return tmp;
 }

    public Vector<workflow_object>findAllUndefinedObjects() {
     Vector<workflow_object>tmp=new Vector<workflow_object>();
     for (workflow_object obj:work) {
         if (isUndefined(obj)) {             
             tmp.add(obj);
         }
     }
     return tmp;
 }

  public boolean isUndefined(workflow_object obj) {
     if (obj instanceof workflow_object_output_database) {
             workflow_properties prop=obj.getProperties();             
             if (prop.getDescription().equals("Undefined")) return true;
             //--Otherwise, it is if no output is found... Except for for object
             if (prop.getInt("output_"+prop.get("outputType").toLowerCase()+"_id")==0&&!isFor(obj))  return true;
         }
      return false;
  }

 public boolean isFor(workflow_object obj) {
     if (obj instanceof armadillo_workflow.workflow_object_output_database) {
             workflow_properties prop=obj.getProperties();
             for (Object k:prop.keySet()) {
                if (((String)k).startsWith("For_")) return true;
             }
         }
      if (obj.getProperties().isSet("ForObjectID")) {
         workflow_properties prop=obj.getProperties();
          for (Object k:prop.keySet()) {
                if (((String)k).startsWith("For_")) return true;
         }
      }
      return false;
  }

  /**
   * this is the main function creating the basic execution tree
   * @return
   */
  public LinkedList<workflow_object> outputExecution() {
      LinkedList<workflow_object>Execution=new LinkedList<workflow_object>();
      //--Use the script TriTopo to find the best path to run
      Scripts_conversion script=new Scripts_conversion();
      script.TriTopo(this, Execution);      
      return Execution;
  }

  /**
   * This function will return the workflow_object attached to either the True or the False branch of a If object
   * @param condition (either the True or False "port")
   * @return A list of workflow_object attached to either branch or nothing if it's not a If object
   */
  public LinkedList<workflow_properties> getIfExecution(workflow_object obj, boolean condition) {
      LinkedList<workflow_object>list_of_objects=new LinkedList<workflow_object>();
      LinkedList<workflow_properties>list_of_properties=new LinkedList<workflow_properties>();
      //--Test if we have a If Object, return otherwise
      if (!(obj instanceof workflow_object_if)) return list_of_properties;
      if (condition) {
        //--Get the first(s) child
         for (int i=0; i<get_nb_workflow_connector_edge();i++) {
           workflow_connector_edge edge=work_connection.get(i);
           if (edge.source.parent==obj&&edge.source.number==workflow_object.UP) list_of_objects.add(edge.dest.parent);
           
         }
      } else {
         //--Get the first(s) child
         for (int i=0; i<get_nb_workflow_connector_edge();i++) {
           workflow_connector_edge edge=work_connection.get(i);
           if (edge.source.parent==obj&&edge.source.number==workflow_object.DOWN) list_of_objects.add(edge.dest.parent);
         }
      }
      //--Then, find their child, etc....
      Vector<workflow_object>tmp=new Vector<workflow_object>();
      for (workflow_object p:list_of_objects) {         
          findAllOutput(p,tmp);
      }
      //--Put in the linkedList
      for (workflow_object p:tmp) {          
          list_of_objects.add(p);
      }
      //--Get the properties
      for (workflow_object p:list_of_objects) {
        list_of_properties.add(p.getProperties());
      }
      return list_of_properties;
  }

  public workflow_object findBegin() {
      for(int i=0; i<work.size();i++) {
            workflow_object obj=work.get(i);            
            if (obj.getProperties().get("ObjectType").equals("Begin")) {
                //--debug Config.log(obj.getName());
                return obj;
            } 
       }
      return null;
  }

  public Vector<workflow_object> findIf() {
      Vector<workflow_object>tmp=new Vector<workflow_object>();
      for(int i=0; i<work.size();i++) {
            workflow_object obj=work.get(i);            
            if (obj.getProperties().get("ObjectType").equals("If")) {
                //--debug Config.log(obj.getName());
                tmp.add(obj);
            } 
       }
      return tmp;
  }

  /**
   * Return the object needed for this object (parents)
   * @param obj
   * @return
   */
  public Vector<workflow_object> findInput(workflow_object source) {
     Vector<workflow_object>tmp=new Vector<workflow_object>();
      for (int i=0; i<get_nb_workflow_connector_edge();i++) {
           workflow_connector_edge edge=work_connection.get(i);
           if (edge.dest.parent==source) tmp.add(edge.source.parent);
      }
      return tmp;
  }
 
  public Vector<workflow_object> findConnectorInput(workflow_connector source) {
       Vector<workflow_object>tmp=new Vector<workflow_object>();
             //--Find all connection linking to the connector
              for (int i=0; i<get_nb_workflow_connector_edge();i++) {
                workflow_connector_edge edge=work_connection.get(i);
                //--Edge is connected to this connector
                //-- We add if we don't already have the parent
                if (edge.dest==source&&!tmp.contains(edge.source.parent)) {
                    tmp.add(edge.source.parent);
                }
            }
        return tmp;
  }
  
  /**
   * Return the imediate object descending from this object (children)
   * @param source
   * @return
   */
  public Vector<workflow_object> findOutput(workflow_object source) {
      Vector<workflow_object>tmp=new Vector<workflow_object>();
      for (int i=0; i<get_nb_workflow_connector_edge();i++) {
           workflow_connector_edge edge=work_connection.get(i);
           if (edge.source.parent==source) tmp.add(edge.dest.parent);
      }
      return tmp;
  }

  /**
   * Recursive fonction to Return All object descending from this object (children)
   * @param source
   * @return
   */
  public Vector<workflow_object> findAllOutput(workflow_object source, Vector<workflow_object> tmp) {
      if (findOutput(source).size()==0) return tmp;            
      for (int i=0; i<get_nb_workflow_connector_edge();i++) {
           workflow_connector_edge edge=work_connection.get(i);
           if (edge.source.parent==source&&!tmp.contains(edge.dest.parent)) {
               tmp.add(edge.dest.parent);
               findAllOutput(edge.dest.parent, tmp);
           }
      }      
      return tmp;
  }

  /**
   * This reset the Program Object output
   */
  public void resetProgramOutput() {
       for (workflow_object obj:work) {
          if (obj instanceof workflow_object_output&&!(obj instanceof workflow_object_output_database)) {
              obj.getProperties().removeInput();
              obj.getProperties().removeOutput();
          }
          if (obj.getProperties().get("ObjectType").equals("Program")&&obj.getProperties().getStatus()!=RunProgram.status_done) {
              obj.getProperties().removeOutput();
          }
      }
  }

 /**
  * This is the MAIN function which update the workflow object dependance
  * Ex. obj -> obj_output_object ->...
  * TO DO: Limit to 1 by collector
  * Note: updated in July 2011 - Etienne
  */
  public boolean updateDependance() {     
      synchronized(this) {          
      for (workflow_object obj:work) {
          //--debug System.out.println(obj);   
          if (obj instanceof workflow_object_output
              ||obj instanceof workflow_object_output_database
              ||obj instanceof workflow_object_if
              ) {
              Vector<workflow_object> inputs=findInput(obj);
               if (inputs.size()>0) {
                   String type=obj.getProperties().get("outputType").toLowerCase();                                      
                   for (workflow_object o:inputs) {                   
                       int id=o.getProperties().getOutputID(type);
                       //--Handle case of concatenation (NOTE: WARNING!!! Don't work...
//                       if (obj instanceof workflow_object_output_database && o instanceof workflow_object_output_database) {
//                            // TO DO : HANDLE CONCATENATION?
//                       } else {
//                           obj.getProperties().put("output_"+type+"_id", id);
//                       }
                       obj.getProperties().put("output_"+type+"_id", id);
                    } //End for input
              }
               if (obj.getProperties().get("ObjectType").equals("OutputDatabase")||obj.getProperties().get("ObjectType").equals("Output")) {
                      String type=obj.getProperties().get("outputType");
                       int id=obj.getProperties().getOutputID(type);
                       //--This might failed ?
                       if (id!=0) {
                           Output out=new Output();
                           out.setType(type);
                           Object bio=out.getBiologic();                                                     
                           obj.getProperties().put("Description", ((Biologic)bio).getNameId(id));                          
                           if (type.equals("Genome")) {
                                    Genome g=new Genome(id);  
                                    workflow_properties t=new workflow_properties();
                                    t.deserializeFromString(g.getText());
                                    obj.getProperties().put("inputname",t.get("inputname"));
                                    obj.getProperties().put("inputname2",t.get("inputname2"));
                                    obj.getProperties().put("type",t.get("type"));
                                    obj.getProperties().put("name",t.get("name"));
                                    obj.getProperties().put("Description",t.get("Description"));                                     
                          }
                       }
                       
                }
              //--Variable
               //--This is wahat make it not reliable if we run only some object
               if (obj instanceof workflow_object_variable ){
                   //Vector<workflow_object> inputs=findInput(obj);
                  //--Array for the numbering of input
                   int connector_next_indice[]={0,0,0,0};
                   //--Clear input
                   //--But first verify if the input is done...
                   //--
                   
                   if (inputs.size()>0) {
                       boolean reset=true;
                       //--Don't update if the input is already done and don't have the output?
                       // No: done in programs instead
//                       for (workflow_object o:inputs) {
//                           if (o.getProperties().getStatus()==RunProgram.status_done) {}
//                       }
                       
                       //--Always true for now...
                       if (reset) obj.getProperties().removeInput();
                       
                       for (workflow_object o:inputs) {
                           String type=o.getProperties().get("outputType").toLowerCase();
                           int id=o.getProperties().getOutputID(type);
                           for (workflow_connector c:findConnection(o,obj)) {
                                   obj.getProperties().put("input_"+type+"_id"+c.number+(connector_next_indice[c.number]++),id);                                  
                           }
                        } //End for input
                   }
               }
               //--If
               if (obj instanceof workflow_object_if ){                 
                   //--Find input if any
                   //Vector<workflow_object> inputs=findInput(obj);
                   obj.getProperties().removeOutput();
                   obj.getProperties().removeOutputType();
                   obj.getProperties().removeInput();
//                   obj.updateConnectorType(1,"");
//                   obj.updateConnectorType(2,"");
                   if (inputs.size()>0) {
                       for (workflow_object o:inputs) {                                                    
                           String type=o.getProperties().get("outputType").toLowerCase();
                           obj.getProperties().put("Output"+o.getProperties().get("outputType"), "True");
                           obj.getProperties().put("outputType",o.getProperties().get("outputType"));     
                          
//--Update the output connectors
//                           obj.updateConnectorType(1,o.getProperties().get("outputType"));
//                           obj.updateConnectorType(2,o.getProperties().get("outputType"));
//                           
                           //--This should be set in the If program...                          
                           int id=o.getProperties().getOutputID(type);
                             if (type.equals("Genome")) {
                                    Genome g=new Genome(id);  
                                    workflow_properties t=new workflow_properties();
                                    t.deserializeFromString(g.getText());
                                    obj.getProperties().put("inputname",t.get("inputname"));
                                    obj.getProperties().put("inputname2",t.get("inputname2"));
                                    obj.getProperties().put("type",t.get("type"));
                                    obj.getProperties().put("name",t.get("name"));
                                    obj.getProperties().put("Description",t.get("Description"));
                          }
                           
                           
                            obj.getProperties().put("output_"+type+"_id",id);
                            obj.getProperties().put("input_"+type+"_id00",id);                          
                        } //End for input
                   }
               }
               
          } 
          //--Object is not a variable....
          else {            
              Vector<workflow_object> input=findInput(obj);              
              //--Array for the numbering of input
               int connector_next_indice[]={0,0,0,0};
               //--Clear input
               obj.getProperties().removeInput();
               if (input.size()>0) {
                   for (workflow_object o:input) {
                        //System.out.println("\t"+o);
                        //--Special case for If (which might have more than 1 inputs...)
                        if (o instanceof workflow_object_if) {
                              for (String ifo:o.getProperties().Outputed()) {     
                                   String type=ifo.toLowerCase();
                                  int id=o.getProperties().getOutputID(type);                       
                                    for (workflow_connector c:findConnection(o,obj)) {
                                            obj.getProperties().put("input_"+type+"_id"+c.number+(connector_next_indice[c.number]++),id);                                         
                                    }                                      
                              }    
                        } else {   
                                String type=o.getProperties().get("outputType").toLowerCase();
                               int id=o.getProperties().getOutputID(type);                       
                               for (workflow_connector c:findConnection(o,obj)) {
                                       obj.getProperties().put("input_"+type+"_id"+c.number+(connector_next_indice[c.number]++),id);
                                       //Config.log(o+"UpdateDependance : "+id+c);
                                       //--delete next input
                                       //obj.getProperties().remove("input_"+type+"_id"+c.number+(connector_next_indice[c.number]));
                               }  
                        }
                    } //End for input
               }
          } //End else
      } //--End synchronisation
      
         //--If object AS no Valid Input -> remove status
//        if (!obj.getProperties().isAllValidInput()) {
//            obj.getProperties().remove("Status");
//        }
      } //End for workflow_object
      return true;
 }

 Vector<workflow_connector> findConnection(workflow_object source, workflow_object dest) {
     Vector<workflow_connector>tmp=new Vector<workflow_connector>();
     //--check if we really have an input
     if (!findInput(dest).contains(source)) return tmp;
     //--WE really have an input
     for (workflow_connector_edge e:work_connection) {
         //--We have a connection
         if (e.source.parent==source&&e.dest.parent==dest) {
            //--We add the destination
             tmp.add(e.dest);
         }
     }

     return tmp;
 }

////////////////////////////////////////////////////////////////////////////////
/// Main Drawing routine

  public void draw() {      
    //--Draw in vector mode if it's set or if we are in low res. and moving...
    if (simplegraph||(movingFlag&&config.getBoolean("LowResolution"))) {
       draw_simplegraph();
    } else {
        draw_normal();
    }
  }

  public void draw_simplegraph() {
      for (int i=0; i<work_connection.size();i++) {
        workflow_connector_edge con=(workflow_connector_edge)work_connection.get(i);
        con.drawSimpleFeature();
      }

     //-- Draw selected connector if moving (workflow_connector)
     if (selected!=null&&selected instanceof workflow_connector) {
               workflow_connector tmp=(workflow_connector)selected;
               stroke(192);
               strokeWeight(2.0f);
               // -- Debug
               // line(tmp.x+5, tmp.y+5, mouseX, mouseY);
              drawBigArrow(tmp.x+5, tmp.y+5, mouseX, mouseY,color(192), color(192));

     }
     if (selected!=null&&selected instanceof workflow_selection) {
         ((workflow_selection)selected).drawFeature();
     }

     //-- Draw object (workflow_object)
     for (int i=0; i<work.size();i++) {
       workflow_object tmp=(workflow_object)work.get(i);
        if (!(tmp instanceof workflow_object_aggregator)) tmp.drawSimpleFeature();
     }
  }

   public void draw_normal() {
         //-- Draw workflow_object_aggregator
     for (int i=0; i<work.size();i++) {
       workflow_object tmp=(workflow_object)work.get(i);
       if (tmp instanceof workflow_object_aggregator) tmp.drawFeature();
     }

     //-- Draw connection (workflow_connector_edge)
      for (int i=0; i<work_connection.size();i++) {
        workflow_connector_edge con=(workflow_connector_edge)work_connection.get(i);
        con.drawFeature();
      }

     //-- Draw selected connector if moving (workflow_connector)
     if (selected!=null&&selected instanceof workflow_connector) {
               workflow_connector tmp=(workflow_connector)selected;
               stroke(192);
               strokeWeight(2.0f);
               // -- Debug
               // line(tmp.x+5, tmp.y+5, mouseX, mouseY);
              drawBigArrow(tmp.x+5, tmp.y+5, mouseX, mouseY,color(192), color(192));

     }
     if (selected!=null&&selected instanceof workflow_selection) {
         ((workflow_selection)selected).drawFeature();
     }

     //-- Draw object (workflow_object)
     for (int i=0; i<work.size();i++) {
       workflow_object tmp=(workflow_object)work.get(i);
        if (!(tmp instanceof workflow_object_aggregator)) tmp.drawFeature();
     }
   }

   public void draw_update() {
         //-- Draw workflow_object_aggregator
     for (int i=0; i<work.size();i++) {
       workflow_object tmp=(workflow_object)work.get(i);
       if (tmp instanceof workflow_object_aggregator) tmp.drawFeature();
     }

     //-- Draw connection (workflow_connector_edge)
      for (int i=0; i<work_connection.size();i++) {
        workflow_connector_edge con=(workflow_connector_edge)work_connection.get(i);
        con.drawFeature();
      }

     //-- Draw selected connector if moving (workflow_connector)
     if (selected!=null&&selected instanceof workflow_connector) {
               workflow_connector tmp=(workflow_connector)selected;
               stroke(192);
               strokeWeight(2.0f);
               // -- Debug
               // line(tmp.x+5, tmp.y+5, mouseX, mouseY);
              drawBigArrow(tmp.x+5, tmp.y+5, mouseX, mouseY,color(192), color(192));

     }
     if (selected!=null&&selected instanceof workflow_selection) {
         ((workflow_selection)selected).drawFeature();
     }

     //-- Draw object (workflow_object)
     for (int i=0; i<work.size();i++) {
       workflow_object tmp=(workflow_object)work.get(i);
        if (!(tmp instanceof workflow_object_aggregator)) tmp.drawFeature();
     }
   }

 ///////////////////////////////////////////////////////////////////////////////
 /// Object creation - deletion into the workflow

 /**
  * Add an object to the work Vector before any aggregator object
  * Note: normally called by createObject() in the main PApplet
  * @param a (workflow_object)
  */
  public boolean add(workflow_object a) {
    //--Create an ID for this objecte
      a.getProperties().setID(a.toString());
    // CASE 1: Aggregator, we add at the end
    if (a instanceof workflow_object_aggregator) {
        return work.add(a);
    } else {
    // CASE 2: We add before the first aggregator
        //--Set good color
        checkforAggregation(a);
        //--Insert at the good place in the list
        for (int i=0;i<work.size();i++) {
            Object tmp=work.get(i);
            if (tmp instanceof workflow_object_aggregator) {
               // First element is aggregator? No insert before
                if (i>0) {
                   work.insertElementAt(a, i-1);
                   return true;
               } else {
               // Else insert at first element
                  work.insertElementAt(a, 0);
                  return true;
               }
            }
        }
        // CASE 3: No aggregator, insert at end
        return work.add(a);
    }
  }

  /**
   * Add a connection between two workflow_connector
   * Note: Verify if we have already have a connection between the parent
   * Note: Also verify if connection already exists
   * Note: Also special case for If object
   * @param source
   * @param dest
   * @param desc
   */
 public boolean addConnection(workflow_connector source, workflow_connector dest, String desc) {
     
     //--Determine if source is not a dest and dest is not a source
     //CASE 0. source or dest==null
     if (source==null) return false;
     if (dest==null) return false;
    //CASE 1. Source and Dest are output but not conditional
     if ((source.output&&dest.output)&&!(source.conditional&&dest.conditional)) return false;
     //CASE 1.1: Do not allow connection between non conditional object and conditionnal
     //Septembre 9 2010
     //if ((dest.conditional&&!source.conditional)||(source.conditional&&!dest.conditional)) return false;

    //CASE 2. Source is an output dest is not? Inverse
     if (!source.output&&dest.output) {
         return addConnection(dest, source, desc);
      }

     //--If we have no connection and its compatible add...
     if (!existsConnection(source, dest)&&isCompatible(source, dest)) {
         //source.parent.getClass()==workflow_object_output.class
         //||source.parent.getClass()==workflow_object_output_big.class||source.parent.getClass()==workflow_object_output_database.class

         //1. Is it already a object_output or we have conditional object
         // -> Then we create a connection and add it to the list
         if ((source.conditional&&dest.conditional)||source.parent instanceof workflow_object_output||source.parent instanceof workflow_object_if) {
            workflow_connector_edge tmp=new workflow_connector_edge(source, dest,desc);
            work_connection.add(tmp);
            //--Test if we have and IfObject
            if (dest.parent instanceof workflow_object_if) {
                //--We have one, we need to change the ouput of the outputType
                dest.parent.getProperties().put("Output"+source.parent.getProperties().get("outputType"), true);
            }
            return true;
         } else {
            //2.We need to create a new output object?
            // TO DO: Make a create object function here....
            //-- We look for name
             workflow_object_output o=null;
            
             String type=getCompatibleType(source, dest);
              o=createOutput_Object(type,source.x+50,source.parent.y);
              this.add(o);
              //--create a connection
              workflow_connector_edge oc=new workflow_connector_edge(source, o.returnNearestConnector(source), "");
                    oc.setNotDeletabled(true);
              this.work_connection.add(oc);
              workflow_connector_edge tmp=new workflow_connector_edge(o.connection[1], dest,desc);
              work_connection.add(tmp);

         }
         return true;
     }
     return false;
  }
  
 /**
  * HELPER FONCTION TO RETURN THE CONNECTOR OF A PARENT...
  * @param parentID
  * @param number
  * @return
  */
  public workflow_connector getConnector(int parentID, int number) {
      for (int i=work.size()-1; i>-1; i--) {
      workflow_object o=work.get(i); 
          //--Debug Config.log(parentID+" "+o.getProperties().getName()+" "+o.getProperties().getProperties_id());
          if (o.properties.getProperties_id()==parentID) {
              if (number>o.connection.length-1) return null;
              return o.connection[number];
          }
      }
      return null;
  }

  /**
   * This is the method which add a connector (edge) from source to destination
   * @param source
   * @param dest
   * @param desc
   * @param notDeletable
   */
  public void addConnector(workflow_connector source, workflow_connector dest, String desc, boolean notDeletable) {
      workflow_connector_edge tmp=new workflow_connector_edge(source, dest,desc);
            tmp.setNotDeletabled(notDeletable);
      work_connection.add(tmp);
  }

 /**
  * Determine if the source connector is compatible with the destination
  * Warning: don't check if the parent object are compatible....
  * Note: You can get the compatible type using getCompatibleType()
  * @param source
  * @param dest
  * @return true if compatible
  */
 boolean isCompatible(workflow_connector source, workflow_connector dest) {   
     // CASE 0. Connector accept another input?
     
    
     //CASE 1. Source already connected to destination...
     //--Example two tree connected to the same input
     //--Find all input for this connector
     Vector<workflow_object> dest_input_objects=findConnectorInput(dest);
     //--Find source type and id
     workflow_object source_object=source.parent;
     String source_type=source_object.getProperties().get("outputType");
     if (debug) Config.log("(isCompatible) source_type:"+source_type);
     String source_Object_id=source_object.getProperties().getID();
     int source_id=source_object.getProperties().getOutputID(source_type);
     if (debug) Config.log("(isCompatible) source_type_id:"+source_id);     
     for (workflow_object dest_input_object:dest_input_objects) {
         String dest_type=dest_input_object.getProperties().get("outputType");
         String dest_Object_id=dest_input_object.getProperties().getID();
         if (debug) Config.log(dest_type+" "+dest_Object_id);
          int dest_id=dest_input_object.getProperties().getOutputID(source_type);
          //--Don't connect to yourself...
          if (source_type.equals(dest_type)&&source_id==dest_id&&source_Object_id.equals(dest_Object_id)) return false;
     }      
     // CASE 2. Two conditional object? (workflow_path) return true
     if (source.conditional&&dest.conditional&&source.isOutput()) {
         return true;
     }
    // CASE 3. Source can outputAll and dest can inputAll return true;
     if (source.isOutputAll()&&dest.isInputAll()) {
           return true;
     }
     if (debug) Config.log("(isCompatible) Source output "+source.getOutput().size());
     //--Test for if, special exception...
      //--Test for if, special exception...
     // CASE 4.0 - Aggregator (new)
     if (source.parent.getProperties().get("ObjectType").equals("Output")&& 
		  dest.parent.getProperties().get("ObjectType").equals("OutputDatabase")) {
		for (String type:source.getOutput()) {                       
             if (dest.input(type)) {
				dest.parent.getProperties().put("AggregateObjectID",0);
				dest.parent.getProperties().put("Description","Aggregate");
				return true;
			 }
         } 
		 
	} 
     // CASE 4.1 If
     if (source.parent.getProperties().get("ObjectType").equals("If")) {
         
          // CASE 4.1 Try to return the first compatible type
         //--Note: If have just one output...
         //--We have output
         if (source.parent.getProperties().Outputed().size()>0) {
             for (String IfOutput:source.parent.getProperties().Outputed()) {
                 //System.out.println(IfOutput);
                 //System.out.println();
                 if (dest.input(IfOutput)) return true;
             }
         }
     } else {
         // CASE 4.2 Try to return the first compatible type
         for (String type:source.getOutput()) {             
             if (debug) Config.log("(isCompatible) Testing "+type+" is compatible "+dest.input(type));
             if (dest.input(type)) return true;
         }
     }//--End else
     if (debug) Config.log("(isCompatible) - found nothing...");
     //--End--Probably not compatible
     return false;
 }

 /**
  * Return if a workflow_connector already ave a connection
  * @param source
  * @return
  */
 public boolean haveConnection(workflow_connector source) {
     for (workflow_connector_edge edge:this.work_connection) {
         if (edge.dest.equals(source)) return true;     
     }
     return false;
 }

 /**
  * Return a compatible connector to attach the source in the dest object...
  * @param source
  * @param dest
  * @return a compatible connector or null if not found
  */
 public workflow_connector findCompatible(workflow_connector source, workflow_object dest) {
    //Case 0. If object
     if (source.parent instanceof workflow_object_if) {
//         for (workflow_connector c:dest.connection) {
//            if (c.conditional) return c;
//         }
         for (workflow_connector c:dest.connection) {
             if (debug) Config.log("Testing compatible "+source.parent.getName()+" -> "+c.parent.getName());
             
             if (isCompatible(source, c)) {
                 if (debug) Config.log("Compatible "+c.outputType+" - "+c.parent.getName());
                 return c;
             }
         }
    }

     //Case 1. Conditional source
     if (source.conditional) {
         for (workflow_connector c:dest.connection) {
            if (c.conditional) return c;
         }
     } else {
     //CASE 2. normal object
         for (workflow_connector c:dest.connection) {
             if (debug) Config.log("Testing normal object "+source.parent.getName()+" -> "+c.parent.getName());
             if (isCompatible(source, c)) {
                 if (debug) Config.log("Compatible "+c.outputType+" - "+c.parent.getName());
                 return c;
             }
         }
     }
     return null;
 }



/**
 * This find a compatible source from the source.parent to the dest
 * Note: Usefull if source can outputAll
 * Warning: No test here if the source and dest are compatible
 * @param source
 * @param dest
 * @return
 */
  public String getCompatibleType(workflow_connector source, workflow_connector dest) {          
    // CASE 1. Source outputALL
    for (String input:source.getOutput()) {
        if (dest.input(input)) return input;
    }
    return "";
  }

 /**
  * Verify if a connection already exists between two objects
  * A connection exists if:
  *     - The source and dest parent are the same
  *     - A connection already exists between the source parent->dest parent
  * @param source
  * @param dest
  * @return
  */
  private boolean existsConnection(workflow_connector source, workflow_connector dest) {
     workflow_object source_parent=source.parent;
     workflow_object dest_parent=dest.parent;
     if (source_parent==dest_parent) return true;
     //--Verify if we already connect to this connector
     for (int i=0; i<work_connection.size();i++) {
         workflow_connector_edge tmp=(workflow_connector_edge)work_connection.get(i);
         //CASE 0: Same source and Same connector?
         if (tmp.source==source&&tmp.dest==dest) return true;
         //CASE 1: SAME Connection exists (bidirectional)
         
         //if (tmp.source==source&&tmp.dest==dest) return true;
         //if (tmp.dest==source&&tmp.source==dest) return true;
         //CASE 2: SAME PARENT?
         if (tmp.source.parent==source_parent&&tmp.dest.parent==dest_parent) return true;
         if (tmp.source.parent==dest_parent&&tmp.dest.parent==source_parent) return true;
    }
     //CASE 3: COnnection don't exists...
     return false;
  }

 ///////////////////////////////////////////////////////////////////////////////
 /// DELETION

  /**
   * Handle the different deletion cae
   * @param selection
   */
  private void delete(Object o) {
      //CASE 1: We have some object
      if (o!=null) {
         if (o instanceof workflow_connector_edge) safelyDeleteConnector((workflow_connector_edge)o);
         if (o instanceof workflow_object_aggregator) safelyDeleteAggregator((workflow_object_aggregator)o); // TO DO
         if (o instanceof workflow_object) safelyDelete((workflow_object)o);
      }
//      } else {
//      //CASE 2: Any selected multiple object?
//        for (int i=work.size()-1; i>-1;i--) {
//            workflow_object tmp=(workflow_object)work.get(i);
//            if (tmp.selected) delete(tmp);
//        }
//      }
  }

  /**
   * This is the safe way to delete an Object
   * ->Select the object
   * ->deleteSelected
   * @return
   */
  public boolean deleteSelected() {
      //-- Delete selection (because we want to delete a selected aggregator object)
      // delete(selected);
      //--Delete all selected object
      setChanged(true);
      //--Save a copy to the undo pile
      //saveToUndo("Delete selection");

      for (int i=work.size()-1;i>-1;i--) {
          workflow_object tmp=(workflow_object)work.get(i);
          if (tmp.selected) delete(tmp);
      }
      //--Delete all selected connection
      for (int i=work_connection.size()-1;i>-1;i--) {
          workflow_connector_edge tmp=(workflow_connector_edge)work_connection.get(i);
          if (tmp.selected) delete(tmp);
      }
      if (!Config.library_mode) {          
          Toolbox tool=new Toolbox();
          tool.reloadCurrentWorkflowsTree(current);
      }
       //--Update the dependance.
       this.updateDependance();
      return true;
  }

  /**
   * This safety delete a workflow_object from the workflow
   * @param o
   * @return
   */
  public boolean safelyDelete(workflow_object o) {
      // Special case for FOR and WHILE
       //-- Delete all external connection
            RemoveAllConnection(o);            
            return(work.remove(o));
  }

  public boolean safelyDeleteAggregator(workflow_object_aggregator o) {
         RemoveAllConnection(o);
         work.remove(o);
         resetColor();
         return true;
     }

  /**
   * This safety delete a workflow_connector_edge from the workflow
   * @param o
   * @return
   */
  public boolean safelyDeleteConnector(workflow_connector_edge o) {
      //--Buffering of parent and dest connector
      //--And removing of selection for parent and dest connector
      // 1. Check if we can delete?
      if (  o.isNotDeletabled()) return false;
      // 2. deletable->Yes
      workflow_connector source=o.source;
      workflow_connector dest=o.dest;
      source.selected=false;
      dest.selected=false;
      dest.destination=false;
      //--Removing of object
      if (!work_connection.remove(o)) {
          if (debug) Config.log("Unable to delete connector_edge "+o);
          return false;
      }
      //--We then verify if parent and dest have other connection
       source.selected=isConnected(source);
       dest.selected=isConnected(dest);
       //--Verify is dest is still a destination node (no drawing)
       if (dest.selected) {
           dest.destination=isDestination(dest);
       }
       //--Update the workflow (handle If, etc..) - September 2011
       updateDependance();
       return true;
  }

  /**
   * Return if a connector is connected to another (or anything)
   * @param connector
   * @return true or false
   */
  public boolean isConnected(workflow_connector connector) {
    for (int i=0; i<work_connection.size();i++) {
        workflow_connector_edge con=(workflow_connector_edge)work_connection.get(i);
        if (con.source==connector||con.dest==connector) return true;
     }
  return false;
 }

 public boolean isDestination(workflow_connector connector) {
    for (int i=0; i<work_connection.size();i++) {
        workflow_connector_edge con=(workflow_connector_edge)work_connection.get(i);
        if (con.dest==connector) return true;
     }
  return false;
 }

  /**
   * Delete all the connection to an object
   * @param o
   * @return
   */
 public boolean RemoveAllConnection(workflow_object o) {
    for (int i=work_connection.size()-1; i>-1;i--) {
        workflow_connector_edge con=(workflow_connector_edge)work_connection.get(i);
        if (con.source.parent==o||con.dest.parent==o) {
                    con.setNotDeletabled(false);
            safelyDeleteConnector(con);
        }
     }  
    return true;
 }

 ///////////////////////////////////////////////////////////////////////////////
 /// Mouse Movement and Connection related

 
 public Object select(int mx, int my) {
    //-- First check for Arrow (connection)
     for (int i=0; i<work_connection.size();i++) {
            workflow_connector_edge tmp=(workflow_connector_edge)work_connection.get(i);
            if (tmp.inside()) {
                tmp.selected=true;
                selected=tmp;
                return selected; // We don't want another selection for this mouseClicked
            }
        }
     //--Check for object
     for (int i=0;i<work.size(); i++) {
          workflow_object tmp=(workflow_object)work.get(i);
          //--Case 1. Object
          if (tmp.inside()) {
              tmp.selected=true;
              selected=tmp;
              //workbox.setSelectedObject(tmp);
              // --Change selected object position in workflow
             //   In fact, we remove then add back the element to the list
             work.remove(tmp);
             this.add(tmp);
             return selected; // We don't want another selection for this mouseClicked
           }
           //--Case 2. Connection
           for (int j=0; j<tmp.connection.length;j++) {
             if (tmp.connection[j].inside()) {
                 //selected=tmp.connection[j]; //No real selection
                 return tmp.connection[j];
             }
           }
        }
     return null; //No selection
 }

 /**
  * Create All the appropriate Output Object from a Connector (expaqnd the connection)
  * @param s
  */
 public void createOutput_Objects(workflow_connector s) {
                //TO DO: Determine is this connector is really an output
                 int nb_output=s.getOutput().size();
                 if (!s.isOutput()||nb_output==0||s.parent.getProperties().get("ObjectType").equals("Output")||s.parent.getProperties().get("ObjectType").equals("OutputDatabase")) return;
                 //this.add(new workflow_aggregator_output(tmp));
                     int posy=s.parent.y-((nb_output*20)/2)+20; //Middle pint
                    
                     //--output All?                                                       
                        for (String type:s.getOutput()) {
                            Boolean found=false;
                            //Check if output already exist for this object
                            for (workflow_object w:this.findOutput(s.parent)) {
                                if (w.getProperties().Outputed().contains(type)) found=true;
                            }
                            //--If not found, create it...
                            if (!found) {
                                workflow_object_output o=null;
                               //--Debug (Warning Length <5)
                                //if (type.length()<5) {
                                o=createOutput_Object(type,s.x+50,posy+=20);
                                if (o instanceof workflow_object_output_big) posy+=10;
                                o.getProperties().put("ParentID", s.parent.getProperties().getID());
                               //} else {
                               // o=new workflow_object_output_big(type, s.x+50,posy+=20);
                               //}

                               this.add(o);
                               //--create a connection
                               workflow_connector_edge oc=new workflow_connector_edge(s, o.returnNearestConnector(s), "");
                               oc.setNotDeletabled(true);
                               this.work_connection.add(oc);
                               this.updateDependance();
                            }
                        }
                     
                 
 }

 /**
  * Helper function to create a workflow_object_output with correct size
  * @param s
  */
 private workflow_object_output createOutput_Object(String outputType, int x, int y) {
        if (outputType.length()>10) {
            return new workflow_object_output_big(outputType, x,y);
        } else {
            return new workflow_object_output(outputType, x,y);
        }
 }

 /**
  * Helper function to create a workflow_object_output with correct size
  * @param s
  */
 private workflow_object_output createOutput_Object(workflow_properties properties, int x, int y) {
        if (properties.getName().length()>10) {
            return new workflow_object_output_big(properties, x,y);
        } else {
            return new workflow_object_output(properties, x,y);
        }
 }

 public void Undo() {
     System.out.println("Undo: "+UndoRedo.size());
     for (Workflows w:UndoRedo) {
         System.out.println(w.getName());
     }
     if (UndoRedo.size()>0) {
        Workflows undo=UndoRedo.getLast();
        UndoRedo.removeLast();
        //--Set the current workflow in txt as the current workflow
        workbox.getCurrentWorkflows().setWorkflow_in_txt(undo.getWorkflow_in_txt());
        workbox.getCurrentWorkflows().StringToWorkflow();
        UndoRedo.removeLast();
     }
 }

 public void ResetUndo() {
     UndoRedo.clear();
 }

 private void saveToUndo(String info) {
      //--Save a copy to the undo pile<
     //--Note: this doesn't work for now...
//    Workflows undo=new Workflows();
//    undo.setWorkflow_in_txt(this.workflowToString());
//    undo.setName(info);
//    UndoRedo.add(undo);
     //System.out.println("saveToUndo"+info+":"+UndoRedo.size());
     //for (Workflows w:UndoRedo) System.out.println(w);
 }

 public void selectAll() {
        for (int i=0;i<work.size(); i++) {
          workflow_object tmp=(workflow_object)work.get(i);
          tmp.selected=true;

        }
        for (int i=0; i<work_connection.size();i++) {
            workflow_connector_edge tmp=(workflow_connector_edge)work_connection.get(i);
            tmp.selected=true;
        }
 }

 public void selectNone() {
        for (int i=0;i<work.size(); i++) {
          workflow_object tmp=(workflow_object)work.get(i);
          tmp.selected=false;

        }
        for (int i=0; i<work_connection.size();i++) {
            workflow_connector_edge tmp=(workflow_connector_edge)work_connection.get(i);
            tmp.selected=false;
        }
 }

  public void inverseSelection() {
        for (int i=0;i<work.size(); i++) {
          workflow_object tmp=(workflow_object)work.get(i);
          tmp.selected=!tmp.selected;

        }
        for (int i=0; i<work_connection.size();i++) {
            workflow_connector_edge tmp=(workflow_connector_edge)work_connection.get(i);
            tmp.selected=!tmp.selected;
        }
 }


  public void move(int px, int py) {
    //--Workflow changed flag
      setChanged(true);
    //--Put undo
     //saveToUndo("Move ");
    //CASE 1: We already have a selection and it is a workflow_object
      if (selected!=null) {
          if (selected instanceof workflow_object) {

            for (int i=0;i<work.size(); i++) {
              workflow_object tmp=(workflow_object)work.get(i);
              if (tmp.selected||tmp.moving) tmp.featureTranslate(px,py);
            }
            //((workflow_object)selected).featureTranslate(px,py);
          }

          if (selected instanceof workflow_drag_connector) {
              workflow_object_aggregator tmp=(workflow_object_aggregator) ((workflow_drag_connector)selected).parent;
              tmp.update(tmp.x2+px, tmp.y2+py);
          }

          if (selected instanceof workflow_selection) {
            //--Update position
            ((workflow_selection)selected).update(mouseX, mouseY);
            //--Update selected object
            for (int i=0;i<work.size(); i++) {
              workflow_object tmp=(workflow_object)work.get(i);
              tmp.selected=((workflow_selection)selected).inside(tmp.x, tmp.y);
              // Debug -- println(tmp.name+" "+tmp.selected);
            }
          }
          // Note: moving of workflow_connector is done in the draw() and notMoving() fucntions;
       } else {
    ///////////////////////////////////////////////////////////////////////////
    //CASE 2.1: No selection, we look for one....in workflow_object
        //-- Set global moving flag if its a real move :)
        movingFlag=true;
        //--
        for (int i=0;i<work.size(); i++) {
          workflow_object tmp=(workflow_object)work.get(i);
           if (tmp.inside()) {
             tmp.moving=true;
             //tmp.selected=true;
             selected=tmp;
             // --Change selected object position in workflow
             //   In fact, we remove then add back the element to the list
             //workbox.setSelectedObject(tmp);
             work.remove(tmp);
             this.add(tmp);
             //--Handle Aggregator selection if move
             if (selected instanceof workflow_object_aggregator) {
                 workflow_object_aggregator tmp2=(workflow_object_aggregator)selected;
                 workflow_selection select=new workflow_selection(tmp2.x1,tmp2.y1, tmp2.x2, tmp2.y2);
                  for (int k=0;k<work.size(); k++) {
                    workflow_object tmp3=(workflow_object)work.get(k);
                    tmp3.selected=select.inside(tmp3.x, tmp3.y);
                    


                 }
             }
             return; //Hack: We don't want another selection
           }
      //CASE 2.2: We have a connector?

          for (int j=0; j<tmp.connection.length;j++) {
             if (tmp.connection[j].inside()) {
                 selected=tmp.connection[j];
                 tmp.connection[j].selected=true;
                 // Determine if it was connected
                 selectedWasConnected=isConnected(tmp.connection[j]);
                 return; //Hack: We don't want another selection
             }
           }

      // CASE 2.3: We have a drag object?
           if (tmp instanceof workflow_object_aggregator)
                    for (int j=0; j<((workflow_object_aggregator)tmp).drag.length;j++) {
                    if (((workflow_object_aggregator)tmp).drag[j].inside()) {
                        selected=((workflow_object_aggregator)tmp).drag[j];
                        ((workflow_object_aggregator)tmp).drag[j].selected=true;
                         return; //Hack: We don't want another selection
                    }
                }

     
         } // End for
        //CASE 2.4: No object, no connector, selection...
        for (int i=0; i<work_connection.size();i++) {
            workflow_connector_edge tmp=(workflow_connector_edge)work_connection.get(i);
            if (tmp.inside()) {
                tmp.selected=true;
                selected=tmp;
                return;
            }
        }

        //CASE 2.5: Nothing? We create a selection box
       if (px!=0&&py!=0) selected=new workflow_selection(mouseX, mouseY);
       }
  }

  /**
   * We stop moving or deselect
   */
  public void notMoving() {
    //-- Set global moving flag to false
    movingFlag=false;
   // - Debug Reset state for selected if we are not mowing
    if (selected instanceof workflow_object) {
        ((workflow_object)selected).moving=false;
         //-- Check if in one of the aggregator
        checkforAggregation(((workflow_object)selected));
    }
    if (selected instanceof workflow_drag_connector) {
        ((workflow_drag_connector)selected).selected=false;
        for (int i=0; i<work.size(); i++) {
            workflow_object tmp=(workflow_object)work.get(i);
            checkforAggregation(tmp);
        }
        
    }
    if (selected instanceof workflow_object_aggregator) {
        for (int i=0; i<work.size(); i++) {
            workflow_object tmp=(workflow_object)work.get(i);
            if (tmp.selected) checkforAggregation(tmp);
        }
        resetSelected();
    }
    if (selected instanceof workflow_connector_edge) {
        // Do nothing
    }

    if (selected instanceof workflow_connector) {
        if (debug) Config.log("Testing drop connector...");
        //CASE 1: Are we over another connector?
         // Iterate over all workflow_object and workflow_connector
         for (int i=0;i<work.size();i++) {
              workflow_object tmp=(workflow_object)work.get(i);
              
              // CASE 1.1: Check for connector (workflow_connector) (prefered)
              workflow_connector source_connector=(workflow_connector)selected;
              workflow_connector dest_connector=tmp.returnConnector();
              if (dest_connector!=null) {
                if (addConnection(source_connector, dest_connector, str(count++))) {
                        //TO DO in addConnection: dest_connector.selected=true;
                } else {
                       //TO DO: Put in the addConnection?
                       source_connector.selected=isConnected(source_connector);
                }                
                selected=null;
                return;
               }
               } //End for

         // CASE 1.2: Check for workflow_object
           //Iterate over all workflow_object and workflow_connector
           for (int i=0;i<work.size();i++) {
              workflow_object tmp=(workflow_object)work.get(i);
              if (tmp.inside()) {
                  if (debug) Config.log("Inside "+tmp.getName());                    
                  workflow_connector source_connector=(workflow_connector)selected;
                    workflow_connector dest_connector=findCompatible(source_connector, tmp);
                    if (addConnection(source_connector, dest_connector, str(count++))) {
                           dest_connector.selected=true;                           
                       } else {          
                            source_connector.selected=isConnected(source_connector);
                       }
                        selected=null;
                       return;
                     }
                  }//End for
         //((workflow_connector)selected).selected=selectedWasConnected;
        //CASE 2: We are not over another connector or workflow_object
        workflow_connector source_connector=(workflow_connector)selected;
        source_connector.selected=isConnected(source_connector);        
    } //End wortflow_connector
    //--Added for If...
    updateDependance();
  selected=null;
  }

  /**
   * This reset the colorMode of a workflow_object an account for aggregation
   * object (for, while...)
   * @param o
   */
   public void checkforAggregation(workflow_object o) {
        o.resetColorMode();
        o.getProperties().remove("InsideFor");
        //--Iterate over each object to find aggregator
        //-- Note: we should create a special list for them? or an index of position?
        for (int i=0;i<work.size();i++) {
             workflow_object tmp=(workflow_object)work.get(i);
              if (tmp instanceof workflow_object_aggregator) {
                 //--Create a selection_object (easier to check for insideness
                  workflow_object_aggregator tmp2=(workflow_object_aggregator)tmp;
                  workflow_selection select=new workflow_selection(tmp2.x1,tmp2.y1, tmp2.x2, tmp2.y2);
                  if(select.inside(o.x, o.y)) {
                      o.setColorMode(((workflow_object_aggregator)tmp).getColorMode());
                      o.getProperties().put("InsideFor", true);
                      return;
                  }
              }
        }
   }

  /**
   * Find if a workflow_object is inside a Aggregator object (While, For)
   * @param o
   * @return True if inside an aggregator object
   */
   public boolean isInsideAggregation(workflow_object o) {
        //--Iterate over each object to find aggregator
        //-- Note: we should create a special list for them? or an index of position?
        for (int i=0;i<work.size();i++) {
             workflow_object tmp=(workflow_object)work.get(i);
              if (tmp instanceof workflow_object_aggregator) {
                 //--Create a selection_object (easier to check for insideness
                  workflow_object_aggregator tmp2=(workflow_object_aggregator)tmp;
                 workflow_selection select=new workflow_selection(tmp2.x1,tmp2.y1, tmp2.x2, tmp2.y2);
                  if(select.inside(o.x, o.y)) {
                          return true;
                  }
              }
        }
        return false;
   }


  
 /**
  * This reset the selection/moving state of each workflow_object
  */
  public void resetSelected() {
           //--Reset Selected Object
           for (int i=0;i<work.size();i++) {
              workflow_object tmp=(workflow_object)work.get(i);
              tmp.selected=false;
              tmp.moving=false;
           }
           //Reset Selected Connection
           for (int i=0;i<work_connection.size();i++) {
              workflow_connector_edge tmp=(workflow_connector_edge)work_connection.get(i);
              tmp.selected=false;
           }
           //--No selection
            //workbox.setSelectedObject(null);
  }

  public void resetColor() {
      for (int i=0;i<work.size();i++) {
            workflow_object tmp=(workflow_object)work.get(i);
            if (!(tmp instanceof workflow_object_aggregator)) checkforAggregation(tmp);
      }
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////
  /// Draw Arrow -- Duplicate ot workflow_connector_edge

  public void drawArrow(float x1, float y1, float x2, float y2, int lineColor, int fillColor) {
    float angle=atan2(y2-y1,x2-x1);
    stroke(lineColor); //Line color
    fill(fillColor);   //fill color
    strokeWeight(2.0f);
    line(x1,y1,x2,y2);
    pushMatrix();
    translate(x2,y2);
    rotate(angle);
    beginShape();
      vertex(-12,-4);//Far Outside UP
      vertex(0,0);   //Middle point
      vertex(-12,4); //Far Outside Bottom
      vertex(-10,-1); //Indide Middle point
    endShape(CLOSE);
    popMatrix();
}

  public void drawBigArrow(float x1, float y1, float x2, float y2, int lineColor, int fillColor) {
    float angle=atan2(y2-y1,x2-x1);
    stroke(lineColor); //Line color
    fill(fillColor);   //fill color
    strokeWeight(5.0f);
    line(x1,y1,x2,y2);
    pushMatrix();
    translate(x2,y2);
    rotate(angle);
    beginShape();
      vertex(-15,-5);//Far Outside UP
      vertex(0,0);   //Middle point
      vertex(-15,5); //Far Outside Bottom
      vertex(-12,-1); //Indide Middle point
    endShape(CLOSE);
    popMatrix();
}

  /////////////////////////////////////////////////////////////////////////////////////////////////////
  /// miscellaneous Fonction -- Mostly for debug

  public void createRandomConnection() {
     workflow_object source=(workflow_object)work.get((int)random(work.size()));
     workflow_object dest=(workflow_object)work.get((int)random(work.size()));
     if (source!=dest) {
          //-- Choose a connector (Normally we should choose the closest
          workflow_connector source_connector=source.connection[(int)random(4)];
          workflow_connector dest_connector=dest.connection[(int)random(4)];
          work_connection.add(new workflow_connector_edge(source_connector, dest_connector,str(count++)));
     }
  }

  public void displayEdge() {
     for (int i=0; i<work_connection.size();i++) {
        workflow_connector_edge con=(workflow_connector_edge)work_connection.get(i);
        con.displayEdge=true;
     }
  }

        /**
         * @return the armadillo
         */
        public armadillo_workflow getArmadillo() {
            return armadillo;
        }

        /**
         * @param armadillo the armadillo to set
         */
        public void setArmadillo(armadillo_workflow armadillo) {
            this.armadillo = armadillo;
        }

 } //End workflow

/**
 * A Connection object on a workflow_object
 * This in fact represent one point witch migh be connected to annother point
 * Etienne Lord
 */
public class workflow_connector {
   //--Connection specific
   int number=0;                            //This connector number
   private boolean output=false;            //Is is an output connector?
   private boolean conditional=false;       //Is is a conditional input//output
   private boolean accept_multiple_input=true;//Can the connector accept multiple inputs? (default: true)
   workflow_object parent=null;
   String outputType;                       //Connector type of Output
   //--Position
   int x=0;
   int y=0;
   //--Interface variables
   public boolean selected=false;
   public boolean destination=false; // -- Is it a destination node: If true, we don't draw the black node

   /////////////////////////////////////////////////////////////////////////////
   /// Constructor
   public workflow_connector(workflow_object parent) {
    this.parent=parent;
   }

   public void drawFeature() {
     PImage displayImage=null;
     //--Debug
    
     
      //--Draw the inside area
      if (debug) rect(this.x-5, this.y-5, 20,20);
         
       if (inside()||destination) {
//          displayImage=(PImage)filedata.get("red_dot.png");
//          image(displayImage,this.x, this.y);
            strokeWeight(0.5f);
            //--shadow
//            stroke(170);
//            fill(170);
//            ellipse(this.x+4, this.y+4, 7,7); 
            //--ball
            //stroke(color(150,46,44));
            fill(color(210,82,72));
            stroke(color(210,82,72));
            ellipse(this.x+5, this.y+5, 7,7); 
            stroke(color(210,82,72));
            fill(200);
            ellipse(this.x+5, this.y+3, 2,2); 

       } else
       if (selected&&!destination) {
          //displayImage=(PImage)filedata.get("black_dot.png");
          //image(displayImage,this.x, this.y);
            //BLACK
            strokeWeight(0.5f);
            stroke(0);
            fill(0);
            ellipse(x+5, y+5, 7,7); 
            fill(128);
            ellipse(x+5, y+3, 2,2); 

       } else
       if (parent!=null && parent.inside()) {
           //RED
            strokeWeight(0.5f);
            //--shadow
//            stroke(170);
//            fill(170);
//            ellipse(this.x+4, this.y+4, 7,7); 
            //--ball
            //stroke(color(150,46,44));
            stroke(color(210,82,72));
            fill(color(210,82,72));
            ellipse(this.x+5, this.y+5, 7,7); 
            stroke(color(210,82,72));
            fill(200);
            ellipse(this.x+4, this.y+3, 2,2); 
       }
 }

   public boolean inside() {
       int size=20;
       //--Size from the middle of the connector
       return (mouseX > this.x-5 && mouseX < this.x-5+size && mouseY > this.y-5 && mouseY < this.y-5+size);
   }

   /**
    *
    * @param type
    * @return can this connector accept the specified input
    */
   public boolean input(String type) {
       if (debug) Config.log("this.output:" + this.output);
       if (this.output) return false;
       if (debug) Config.log("isInputAll" + isInputAll());
       if (isInputAll()) return true;
       if (debug)System.out.println("outputType:"+outputType);
       if (outputType!=null&&outputType.equals(type)) return true;
       //--Exceptions
       if (outputType!=null&&outputType.equals("Outgroup")&&type.equals("Sequence")) return true;
       if (outputType!=null&&outputType.equals("Matrix")&&type.equals("Phylip_Distance")) return true;
       
       String keyConnector="Connector"+number;
       //if (debug) Config.log("keyConnector:"+keyConnector);
       String value=parent.properties.get("Input"+type);
       if (debug) Config.log("value:"+value);
       //--Added true in lowercase - Septembre 2011       
       if (value.equalsIgnoreCase("True")||value.equalsIgnoreCase("true")) return true;
       if (value.equals(keyConnector)) return true;       
       return false;
   }

   public Vector<String> getOutput() {
       Vector<String>tmp=new Vector<String>();
       //--Case 1. Not an output... return empty array
       if (!isOutput()||isInputAll()) return tmp;
       String keyConnector="Connector"+number;
       //--Case 2. OutputAll ->Return Everything
       if (isOutputAll()) {
           for (String S:workflow_properties_dictionnary.InputOutputType) tmp.add("Output"+S);
           return tmp;
       }
       //--Case 3. Return output
       if (outputType!=null) {
          tmp.add(outputType);
          return tmp;
       }
       
        //--Look for other type
         for (String s:this.parent.getProperties().Outputed()) {
              tmp.add(s);
         }
       
         //--DEprecated           
//         for (String key:workflow_properties_dictionnary.InputOutputType) {
//             String value=parent.properties.get("Output"+key);
//             boolean valueb=parent.properties.getBoolean("Output"+key);
//             if (value.equals(keyConnector)||valueb) {                 
//                 tmp.add(key);
//             } 
//         }
      
       return tmp;
   }
   
   public Vector<String> getInput() {
       Vector<String>tmp=new Vector<String>();
       //--Case 1. Not an output... return empty array
       if (isOutput()) return tmp;
       String keyConnector="Connector"+number;
       //--Case 2. OutputAll ->Return Everything
       if (isInputAll()) {
           for (String S:workflow_properties_dictionnary.InputOutputType) tmp.add("Input"+S);
           return tmp;
       }
       //--Case 3. Return output
       if (outputType!=null) {
          tmp.add(outputType);
          return tmp;
       }

         for (String key:workflow_properties_dictionnary.InputOutputType) {
             String value=parent.properties.get("Input"+key);
             if (value.equals(keyConnector)||value.equals("True")) {
                 tmp.add(key);
             }
         }
       
       return tmp;
   }

   public boolean isOutputAll() {
       String value=parent.getProperties().get("OutputAll");
       boolean b=(value.equals("True")||value.equals("Connector"+number));
       return b;
   }
      
   public boolean isInputAll() {
       String value=parent.getProperties().get("InputAll");
       boolean b=(value.equals("True")||value.equals("Connector"+number));
       return b;
   }


   public void setOutput(boolean b) {
       this.output=b;
   }

   public boolean isOutput() {
       return this.output;
   }

    public boolean isConditional() {
       return this.conditional;
   }

    public void setConditional(boolean b) {
        this.conditional=b;
    }

        /**
         * @return the accept_multiple_input
         */
        public boolean isAccept_multiple_input() {
            return accept_multiple_input;
        }

        /**
         * @param accept_multiple_input the accept_multiple_input to set
         */
        public void setAccept_multiple_input(boolean accept_multiple_input) {
            this.accept_multiple_input = accept_multiple_input;
        }

}//--End connector

/**
 * A Drag connector :: Used to redimension some component
 * This in fact represent one point witch migh be connected to annother point
 * Etienne Lord
 */
public class workflow_drag_connector {
   ////////////////////////////////////////////////////////////////////////////
   /// VARIABLES
    String name="";
   workflow_object parent=null;
   int x=0;
   int y=0;
   public int mode=UPRIGHT;
   public boolean selected=false;

   ////////////////////////////////////////////////////////////////////////////
   /// CONSTANT
   public final static int UPLEFT=3;  //Strange numbering but easier for resize
   public final static int UPRIGHT=0;
   public final static int DOWNLEFT=2;
   public final static int DOWNRIGHT=1;

   public void draw() {
     PImage displayImage=null;
     //boolean inside=inside();

       if (inside()) {
          switch (mode) {
              case UPRIGHT   : displayImage=(PImage)filedata.get("arrow_left_red.png"); break;
              case DOWNRIGHT:  displayImage=(PImage)filedata.get("arrow_down_red.png"); break;
          }
          image(displayImage,this.x, this.y);
       } else
       if (selected) {
          switch (mode) {
              case UPRIGHT   : displayImage=(PImage)filedata.get("arrow_left_red.png"); break;
              case DOWNRIGHT:  displayImage=(PImage)filedata.get("arrow_down_red.png"); break;
          }
          image(displayImage,this.x, this.y);
       } else
       if (parent.inside()) {
          switch (mode) {
              case UPRIGHT   : displayImage=(PImage)filedata.get("arrow_left_blue.png"); break;
              case DOWNRIGHT:  displayImage=(PImage)filedata.get("arrow_down_blue.png"); break;
          }
          image(displayImage,this.x, this.y);
        }

 }

   public boolean inside() {
     switch (mode) {
              case UPRIGHT   : return (mouseX > x-8 && mouseX < x+8 && mouseY > y-8 && mouseY < y+8);
              case DOWNRIGHT : return (mouseX > x-8 && mouseX < x+8 && mouseY > y-8 && mouseY < y+8);
          }
      return false;
   }

}


/**
 * This represent a Edge (connection) between to workflow_connector
 * In reality, we use this object to draw an Arrow
 *
 */
public class workflow_connector_edge {
    /////////////////////////////////////////////////////////////////////////////////
    /// Variables
    public workflow_connector source=null;   // Source
    public workflow_connector dest=null;     // Destination
    String desc="";                          // Edge description
    int color = 0;                           // Color (TO DO : PUT SOME COLOR IN CONFIG)
    public boolean displayEdge=false;        // Do we display edge
    private boolean notDeletabled=false;      // Can we deleted it?
    int hashcode=0;                          // hashcode used for arrow selection
    public boolean selected=false;           // Selected?
    //////////////////////////////////////////////////////////////////////////////
    /// Database info
    public workflow_properties properties=new workflow_properties();
    public int connectors_id=0;
    
    /////////////////////////////////////////////////////////////////////////////////
    /// Constructor

    /**
     * Note: from the original Processing 1.2.1 source code
     */
    public final int armadillo_color(int c) {       
      
        return 0xff000000 | (c << 16) | (c << 8) | c;
     
    }
    
    public workflow_connector_edge(workflow_connector source,workflow_connector dest, String desc) {
   //-- Warning, check must be made before to check if source!=dest;
     this.source=source;
     this.dest=dest;
     this.desc=desc;
     this.dest.destination=true;
     source.selected=true;
     dest.selected=true;
     //--Changed October 2011
     //hashcode=color(getHashcode());
     //int tmp=color(getHashcode());
     hashcode=color(getHashcode());;
     properties.setName("connector_"+hashcode);
     properties.put("source", source.number);
     properties.put("destination", dest.number);
     properties.put("notDeletabled", notDeletabled);
     properties.put("displayEdge", displayEdge);
   }

   public void drawFeature() {
     if (properties.getBoolean("NoDraw")) return;
     if (source!=null&&dest!=null) {
        // -- Get source and destination
        workflow_connector tmp_source=(workflow_connector)source;
        workflow_connector tmp_dest=(workflow_connector)dest;
        //-- draw buffer Arrow
        //-- Note: we check if parent are moving to increase redraw time
        //--Note: we don't redraw if the workflow is in auto_update...
        if (!movingFlag&&!isNotDeletabled()&&!auto_update) drawBufferArrow(tmp_source.x+4, tmp_source.y+4, tmp_dest.x+4, tmp_dest.y+4);
        //--draw Arrow -- Handle inside and other condition
        //-- TO DO Simplify this logic choice?
        if (selected) {
           if (!isNotDeletabled()) {
               drawBigArrow(tmp_source.x+4, tmp_source.y+4, tmp_dest.x+4, tmp_dest.y+4, color(222,43,54), color(222,43,54));
           } else {
               drawArrow(tmp_source.x+4, tmp_source.y+4, tmp_dest.x+4, tmp_dest.y+4);
           }
        } else 
        if (inside()) {
            if (!isNotDeletabled()) {
               drawBigArrow(tmp_source.x+4, tmp_source.y+4, tmp_dest.x+4, tmp_dest.y+4, color(222,43,54), color(222,43,54));
           } else {
               drawArrow(tmp_source.x+4, tmp_source.y+4, tmp_dest.x+4, tmp_dest.y+4);
           }
        } else {
          drawArrow(tmp_source.x+4, tmp_source.y+4, tmp_dest.x+4, tmp_dest.y+4);
        }
        //--Write 
        if (selected) {
            noFill();
            strokeWeight(1.0f);
            stroke(128);
            //image((PImage)filedata.get("big_close.png"), tmp_source.x+(tmp_dest.x-tmp_source.x)/2+20,tmp_source.y+(tmp_dest.y-tmp_source.y)/2+20);
//            int w=(int)abs(tmp_source.x-tmp_dest.x);
//            int h=(int)abs(tmp_source.y-tmp_dest.y);
//            rect(tmp_source.x+4,tmp_source.y+4,w+4, h+4);
        }

        //-- Write Edge information
        textAlign(CENTER);
        fill(0);
        if (displayEdge) text(desc,tmp_source.x+(tmp_dest.x-tmp_source.x)/2,tmp_source.y+(tmp_dest.y-tmp_source.y)/2);
      }
     
   }

   public void drawSimpleFeature() {

     if (source!=null&&dest!=null) {
        // -- Get source and destination
        workflow_connector tmp_source=(workflow_connector)source;
        workflow_connector tmp_dest=(workflow_connector)dest;
        //-- draw buffer Arrow
        //-- Note: we check if parent are moving to increase redraw time
        drawArrow(tmp_source.x+4, tmp_source.y+4, tmp_dest.x+4, tmp_dest.y+4);  
        if (selected) {
            noFill();
            strokeWeight(1.0f);
            stroke(128);
        }
        
        //-- Write Edge information
        textAlign(CENTER);
        fill(0);
        //text(desc,tmp_source.x+(tmp_dest.x-tmp_source.x)/2,tmp_source.y+(tmp_dest.y-tmp_source.y)/2);
      }
     
   }

  public workflow_properties getProperties() {
      return this.properties;
  }



public void drawArrow(float x1, float y1, float x2, float y2) {
     drawArrow(x1,y1,x2,y2,color, color);
}

public void drawArrow(float x1, float y1, float x2, float y2, int lineColor, int fillColor) {
    stroke(lineColor); //Line color
    strokeWeight(2.0f);
    noFill();
    //--Calculate angle for a proximal bezier point for Arrow direction
    //float dist_x=abs(x2-x1);
    //float dist_y=abs(y2-y1);
    float px=0, py=0;
    //dist_x>dist_y&&
    if (x2>=x1) {
        bezier(x1,y1,x1+50,y1,x2-50,y2,x2,y2);
        px = bezierPoint(x1,x1+50, x2-50, x2, 0.90f);
        py = bezierPoint(y1, y1, y2, y2, 0.90f);
    } else
    if (y2>y1) {
        bezier(x1,y1,x1,y1+50,x2,y2-50,x2,y2);
        px = bezierPoint(x1,x1, x2, x2, 0.90f);
        py = bezierPoint(y1, y1+50, y2-50, y2, 0.90f);
    } else
   if (x1>x2) {
        bezier(x1,y1,x1-50,y1,x2+50,y2,x2,y2);
        px = bezierPoint(x1,x1-50, x2+50, x2, 0.90f);
        py = bezierPoint(y1, y1, y2, y2, 0.90f);
    } else
    if (y1>y2) {
        bezier(x1,y1,x1,y1-50,x2,y2+50,x2,y2);
        px = bezierPoint(x1,x1, x2, x2, 0.90f);
        py = bezierPoint(y1, y1-50, y2-50, y2, 0.90f);
    }
    if (!   isNotDeletabled()) {
    float angle=atan2(y2-py,x2-px);
    //--Debug draw control point and vector
    //line(x1,y1,x2,y2);
    //rect(px,py,5,5);
    //fill(color(255,0,0));
    //stroke(color(255,0,0));
    //line(x1,y1,x1+50,y1);
    //line(x2-50,y2,x2,y2);
    //--End Debug
    stroke(lineColor); //Line color
    fill(fillColor);   //fill color
    strokeWeight(2.0f);
    pushMatrix();
    translate(x2,y2);
    rotate(angle);
    beginShape();
      vertex(-12,-4);//Far Outside UP
      vertex(0,0);   //Middle point
      vertex(-12,4); //Far Outside Bottom
      vertex(-10,-1); //Indide Middle point
    endShape(CLOSE);
    popMatrix();
    }
}

/**
 * Draw a selection arrow to the back_buffer
 * Warning, it is bigger than the real arrow for easier selection
 * Mimick draw big Arrow
 * @param x1
 * @param y1
 * @param x2
 * @param y2
 */
private void  drawBufferArrow(float x1, float y1, float x2, float y2) {
     
    //--Code for selection of Arrow using a back_buffer
     bufConnectorEdge.stroke(hashcode); //Line color
     bufConnectorEdge.fill(hashcode);   //fill color
     bufConnectorEdge.strokeWeight(5.0f);
    
     //bufConnectorEdge.bezier(x1,y1,x1+50,y1,x2-50,y2,x2,y2);
     //--Calculate angle for a proximal bezier point for Arrow direction
    
    float px=0, py=0;
    if (x2>=x1) {
        bufConnectorEdge.bezier(x1,y1,x1+50,y1,x2-50,y2,x2,y2);
        px = bezierPoint(x1,x1+50, x2-50, x2, 0.90f);
        py = bezierPoint(y1, y1, y2, y2, 0.90f);
    } else
    if (y2>y1) {
        bufConnectorEdge.bezier(x1,y1,x1,y1+50,x2,y2-50,x2,y2);
        px = bezierPoint(x1,x1, x2, x2, 0.90f);
        py = bezierPoint(y1, y1+50, y2-50, y2, 0.90f);
    } else
   if (x1>x2) {
        bufConnectorEdge.bezier(x1,y1,x1-50,y1,x2+50,y2,x2,y2);
        px = bezierPoint(x1,x1-50, x2+50, x2, 0.90f);
        py = bezierPoint(y1, y1, y2, y2, 0.90f);
    } else
    if (y1>y2) {
        bufConnectorEdge.bezier(x1,y1,x1,y1-50,x2,y2+50,x2,y2);
        px = bezierPoint(x1,x1, x2, x2, 0.90f);
        py = bezierPoint(y1, y1-50, y2-50, y2, 0.90f);
    } 
         float angle=atan2(y2-py,x2-px);
         bufConnectorEdge.fill(hashcode);   //fill color
         bufConnectorEdge.strokeWeight(5.0f);
         bufConnectorEdge.pushMatrix();
         bufConnectorEdge.translate(x2,y2);
         bufConnectorEdge.rotate(angle);
         bufConnectorEdge.beginShape();
         bufConnectorEdge.vertex(-15,-5);//Far Outside UP
         bufConnectorEdge.vertex(0,0);   //Middle point
         bufConnectorEdge.vertex(-15,5); //Far Outside Bottom
         bufConnectorEdge.vertex(-12,-1); //Indide Middle point
         bufConnectorEdge.endShape(CLOSE);
         bufConnectorEdge.popMatrix();
    
}

public void drawBigArrow(float x1, float y1, float x2, float y2, int lineColor, int fillColor) {
    stroke(lineColor); //Line color
    noFill();    
    strokeWeight(5.0f);

    
    //--Calculate angle for a proximal bezier point for Arrow direction
//    float dist_x=abs(x2-x1);
//    float dist_y=abs(y2-y1);
    float px=0, py=0;
     if (x2>=x1) {
        bezier(x1,y1,x1+50,y1,x2-50,y2,x2,y2);
        px = bezierPoint(x1,x1+50, x2-50, x2, 0.90f);
        py = bezierPoint(y1, y1, y2, y2, 0.90f);
    } else
    if (y2>y1) {
        bezier(x1,y1,x1,y1+50,x2,y2-50,x2,y2);
        px = bezierPoint(x1,x1, x2, x2, 0.90f);
        py = bezierPoint(y1, y1+50, y2-50, y2, 0.90f);
    } else
   if (x1>x2) {
        bezier(x1,y1,x1-50,y1,x2+50,y2,x2,y2);
        px = bezierPoint(x1,x1-50, x2+50, x2, 0.90f);
        py = bezierPoint(y1, y1, y2, y2, 0.90f);
    } else
    if (y1>y2) {
        bezier(x1,y1,x1,y1-50,x2,y2+50,x2,y2);
        px = bezierPoint(x1,x1, x2, x2, 0.90f);
        py = bezierPoint(y1, y1-50, y2-50, y2, 0.90f);
    }
    float angle=atan2(y2-py,x2-px);
    fill(fillColor);   //fill color
    pushMatrix();
    translate(x2,y2);
    rotate(angle);
    beginShape();
      vertex(-15,-5);//Far Outside UP
      vertex(0,0);   //Middle point
      vertex(-15,5); //Far Outside Bottom
      vertex(-12,-1); //Indide Middle point
    endShape(CLOSE);
    popMatrix();
}


public boolean inside() {
    if (debug) {
        int c=bufConnectorEdge.get(mouseX, mouseY);
       System.out.println("Color in buffer:"+c+" Color for connector:"+hashcode+" "+(bufConnectorEdge.get(mouseX, mouseY)==hashcode));   
    }
    return (bufConnectorEdge.get(mouseX, mouseY)==hashcode);
}

        /**
         * @param desc the desc to set
         */
        public void setDesc(String desc) {
            this.desc = desc;
        }

        /**
         * @return the color
         */
        public int getColor() {
            return color;
        }

        /**
         * @param color the color to set
         */
        public void setColor(int color) {
            this.color = color;
        }


        public workflow_object getSourceParent() {
            return this.source.parent;
        }

        public workflow_object getDestinationParent() {
            return this.dest.parent;
        }

        /**
         * @return the notDeletabled
         */
        public boolean isNotDeletabled() {
            return notDeletabled;
        }

        /**
         * @param notDeletabled the notDeletabled to set
         */
        public void setNotDeletabled(boolean notDeletabled) {
            this.notDeletabled = notDeletabled;
            properties.put("notDeletabled", notDeletabled);
        }


} //End class


/**
* Idealement, une repr\u00e9sentation graphique est seulement une suite de feature
*
* Chaque feature une methode drawFeature. Cette partie du code provient de :
*
* using-awt-s-polygon-class-to-test-for-insideness taken from http://processinghacks.com/hacks:using-awt-s-polygon-class-to-test-for-insideness
* author Andreas K\u00f6berle
*
*
*/
public class workflow_object extends java.awt.Polygon {
  // A la base, la fearure n'est qu'un poligon former de vertex
  // TO DO: Put the pvertex in a Vector Collection
  pvertex[] Vertex;
  int Vertex_count=0;
  //Vector<String>input=new Vector<String>();
  //Vector<String>output=new Vector<String>();
  //public int nbInput=0;             //TO DO JUST FOR TEST  Warning debug
  //String colorMode=defaultColorMode;
  public int x=0;                      // this is the object center X
  public int y=0;                      // this is the object center Y
  public boolean moving=false;       // Are we moving
  public boolean selected=false;     // Selected?
  public long time_over=0;            //over_time represent the start time in millisecond we are over an object

  workflow_connector[] connection;    //Connector to this object
  int test=0;
  
  //////////////////////////////////////////////////////////////////////////////
  /// Database info
  public workflow_properties properties=new workflow_properties();
  public int objects_id=0;

  // CONSTANT
  public final static int LEFT=0; //BECAUSE OF IF OBJECT... EASIER
  public final static int UP=1;
  public final static int DOWN=2;
  public final static int RIGHT=3;
  public final static int UPLEFT=3;  //Strange numbering but easier for resize
  public final static int UPRIGHT=0;
  public final static int DOWNLEFT=2;
  public final static int DOWNRIGHT=1;
  public final static int OUTPUT=0;
  public final static int INPUT1=2;
  public final static int INPUT2=3;
  public final static int TOP=1;


  /**
  * Constructor
  */
  public workflow_object() {
  }

  /**
   * Alternative constructor to put description
   */
  public workflow_object(workflow_properties obj) {
    this.properties=obj;
    resetColorMode();
    Vertex=new pvertex[4];
    connection=new workflow_connector[properties.getInt("nbInput")+2];
    //connection=new workflow_connector[properties.getInt("nbInput")+2];
    float x1=properties.getInt("x");
    float y1=properties.getInt("y");
    float w=149;
    float h=86;
    // -- Make the vertex needed for object selection
    addVertex(x1, y1);
    addVertex(x1+w,y1);
    addVertex(x1+w, y1+h);
    addVertex(x1, y1+h);
     //--Set the input and output name
    //setInputOutput();
    // -Create the connector
    
    for (int i=0; i<connection.length;i++) {
      connection[i]=new workflow_connector(this);
    }
    // - Calculate their position
    recalculatePosition();

  }

  

  /**
   * Default constructor for drag and drop
   */
  public workflow_object(workflow_properties obj, int x2, int y2) {
    this.properties=obj;
    resetColorMode();
    Vertex=new pvertex[4];
    connection=new workflow_connector[properties.getInt("nbInput")+2];    
    float w=149;
    float h=86;
    properties.put("x",x2);
    properties.put("y",y2);
    // -- Make the vertex needed
    addVertex(x2, y2);
    addVertex(x2+w,y2);
    addVertex(x2+w, y2+h);
    addVertex(x2, y2+h);
    // -Create the connector
    for (int i=0; i<connection.length;i++) {
      connection[i]=new workflow_connector(this);
    }
    // - Calculate their position
    recalculatePosition();   
  }

    public void featureTranslate(float x2, float y2) {
    super.reset();
    super.invalidate();
    // - For debug
    //println(name);
     
        
      for (int i=0; i<Vertex_count;i++) {
        Vertex[i].x+=x2;
        Vertex[i].y+=y2;
        super.addPoint((int)Vertex[i].x,(int)Vertex[i].y);
      }
      //--Set new position in properties
      properties.put("x",Vertex[0].x);
      properties.put("y",Vertex[0].y);
      //--debug Config.log(properties.getProperties());
      recalculatePosition();
  }


  public boolean inside() {
      try {
        if (this.xpoints.length>0) return (super.contains(mouseX,mouseY));
      } catch(Exception e) {return false;}
      return false;
    }

  public boolean inside(float x, float y) {
    return (super.contains(x,y));
    }

  public void addVertex(float x, float y) {
    pvertex v = new pvertex(x,y);
    if (Vertex_count == Vertex.length) {
      Vertex = (pvertex[]) expand(Vertex)  ;
    }
      super.addPoint((int)x,(int)y);
      Vertex[Vertex_count++] = v;
    }




  /**
   * Set the color mode
   * Refer to workflow_object color (red, blue, green, orange, (black))
   * @param colorMode
   */
  public void setColorMode(String colorMode) {
      this.properties.put("colorMode",colorMode);
  }

  
  /**
   *
   * @return this Object properties
   */
  public workflow_properties getProperties() {
      return this.properties;
  }

  public String toString() {
      return properties.getName()+"_"+Util.returnCount();
  }


  /**
   * Put back the object to is default color mode
   */
  public void resetColorMode() {
     this.properties.put("colorMode",this.properties.get("defaultColor"));
  }
 
  public String getColorMode() {
      return this.properties.get("colorMode");
  }

  public String getDefautlColorMode() {
      return this.properties.get("defaultColor");
  }

  /**
   * Generic method to create a connector object
   */
  public workflow_connector createConnector(int number,int x, int y, String type) {
    workflow_connector tmp=new workflow_connector(this);
    tmp.x=x;
    tmp.y=y;
    tmp.number=number;
    String keyConnector="Connector"+number;
    String keyOutput="Connector"+number+"Output";
    String keyConditional="Connector"+number+"Conditional";
    tmp.output=(properties.isSet(keyOutput));
    tmp.conditional=properties.isSet(keyConditional);
    tmp.outputType=type;
    return tmp;
  }

  /**
   * Generic method to update a connector object (position, properties...)
   */
  public void updateConnector(workflow_connector tmp, int number,int x, int y, String type) {
    tmp.x=x;
    tmp.y=y;
    tmp.number=number;
    String keyConnector="Connector"+number;
    String keyOutput="Connector"+number+"Output";
    String keyConditional="Connector"+number+"Conditional";
    tmp.output=(properties.isSet(keyOutput));
    tmp.conditional=properties.isSet(keyConditional);
    tmp.outputType=type;
 }

   /**
   * Generic method to update a connector object type (needed for If)...
   */
  public void updateConnectorType(int connector_id,String type) {
    this.connection[connector_id].outputType=type;
 }
  
  /**
   * This is needed to recalculate the object position if it's moved
   * Note: it also set its properties
   */
  public void recalculatePosition() {
      properties.put("x", Vertex[0].x); //Thit is the left-top corner
      properties.put("y", Vertex[0].y);
      //--debug if (debug) Config.log("Moving "+properties.getName()+" to (x,y):"+properties.get("x")+" "+properties.get("y"));
      //--This represent the middle of the object
      this.x=PApplet.parseInt(Vertex[0].x+(Vertex[1].x-Vertex[0].x)/2);
      this.y=PApplet.parseInt(Vertex[0].y+(Vertex[1].y-Vertex[0].y)/2);
      //-- Initialise Connector
    for (int i=0; i<connection.length; i++) {
       switch(i) {
         case OUTPUT   : 
                        updateConnector(connection[i],i,this.x+73,this.y+56,null);
                        //connection[i].x=this.x+73;connection[i].y=this.y+56;
                        //Note: output not needed since we create connector
                        //connection[i].setOutput(true);
                         break;
         case TOP:      updateConnector(connection[i],i,this.x,this.y-9,null);
//                        connection[i].x=this.x;connection[i].y=this.y-9;
//                        connection[i].conditional=true;
//                        connection[i].output=true;
                         break;
         case INPUT1 :  updateConnector(connection[i],i,this.x-82,this.y+56,null);
//                              connection[i].x=this.x-82;connection[i].y=this.y+56;
//                              connection[i].addInput(getInput1());
//                              connection[i].setOutput(false);
                         break;
         case INPUT2 : updateConnector(connection[i],i,this.x-82,this.y+37,null);
//                              connection[i].x=this.x-82;connection[i].y=this.y+37;
//                              connection[i].addInput(getInput2());
//                              connection[i].setOutput(false);
                         break;
         
       }
    }
      //Config.log(properties.getProperties());
  }

  /**
   * Return the nearest connector to the mouse position for this object
   * Note: Override this methode for particular object
   * @return
   */
  public workflow_connector returnNearestConnector() {
        workflow_connector dest_connector=this.connection[0];
        float currentDist=dist(mouseX,mouseX, dest_connector.x, dest_connector.y);
        //Search for the closest work_connector
        for (int j=1; j<connection.length;j++) {
            float newDist=dist(mouseX, mouseY, connection[j].x, connection[j].y);
            if (newDist<currentDist) {
            currentDist=newDist;
            dest_connector=connection[j];
          }
        }
      return dest_connector;
  }
 
  /**
   * Return the nearest COMPATIBLE connector to the mouse position for this object
   * Note: Override this methode for particular object
   * @return
   */
  public workflow_connector returnNearestConnector(workflow_connector source) {
        workflow_connector dest_connector=this.connection[0];
        float currentDist=dist(source.x,source.y, dest_connector.x, dest_connector.y);
        //Search for the closest work_connector
        for (int j=1; j<connection.length;j++) {
            float newDist=dist(source.x, source.y, connection[j].x, connection[j].y);
            if (newDist<currentDist) {
            currentDist=newDist;
            dest_connector=connection[j];
          }
        }
      return dest_connector;
  }

  /**
   * Search for a connector at mouseX, mouseY
   * if found return it
   * @return return a connector or null if not found
   */
  public workflow_connector returnConnector() {
     for (int i=0; i<connection.length;i++) {
         if (connection[i].inside()) return connection[i];
     }
     return null;
  }

   /**
   * Return the corresponding connector   
   * @return return a connector or null if not found
   */
  public workflow_connector returnConnector(int connector_number) {
     if (connector_number<connection.length) {
      return connection[connector_number];     
     } else {
        return null;
     }
  }


////////////////////////////////////////////////////////////////////////////////
/// Draw feature

  public void drawFeature(){
    if (properties.get("outputType").equals("Workflows")) {
       drawWorkflow();
    } else {
    //--Detect if we are inside the object
    boolean inside=inside();
    //--Draw this object
    strokeWeight(0.1f);
    PImage displayImage=null;
     int color_border=red_border;
    int color_object=color_red;
        
      
    
    if (properties.getBoolean("NoDraw")) return;
    int nbInput=properties.getInt("nbInput");
    if (inside||selected) {
        if (nbInput==0) displayImage=(PImage)filedata.get("workflow_black.png");
        if (nbInput==1) displayImage=(PImage)filedata.get("workflow_black_single.png");
        if (nbInput==2)  displayImage=(PImage)filedata.get("workflow_black_double.png");
        color_border=black_border;
        color_object=color_black;      
    } else {
        if(properties.get("colorMode").equals("RED")) {
                        if (nbInput==0) displayImage=(PImage)filedata.get("workflow_red.png");
                        if (nbInput==1) displayImage=(PImage)filedata.get("workflow_red_single.png");
                        if (nbInput==2) displayImage=(PImage)filedata.get("workflow_red_double.png");
                       color_border=red_border;
                       color_object=color_red;  
        }
        if(properties.get("colorMode").equals("CYAN")) {
                      if (nbInput==0) displayImage=(PImage)filedata.get("workflow_cyan.png");
                      if (nbInput==1) displayImage=(PImage)filedata.get("workflow_cyan_single.png");
                      if (nbInput==2) displayImage=(PImage)filedata.get("workflow_cyan_double.png");
                        color_border=cyan_border;
                       color_object=color_cyan;  
        }
        if(properties.get("colorMode").equals("BLUE")){
                      if (nbInput==0) displayImage=(PImage)filedata.get("workflow_blue.png");
                      if (nbInput==1) displayImage=(PImage)filedata.get("workflow_blue_single.png");
                      if (nbInput==2) displayImage=(PImage)filedata.get("workflow_blue_double.png");
                      color_border=blue_border;
                      color_object=color_blue;  
        }
        if(properties.get("colorMode").equals("ORANGE")) {
                      if (nbInput==0) displayImage=(PImage)filedata.get("workflow_orange.png");
                      if (nbInput==1) displayImage=(PImage)filedata.get("workflow_orange_single.png");
                      if (nbInput==2)displayImage=(PImage)filedata.get("workflow_orange_double.png");
                      color_border=orange_border;
                      color_object=color_orange;  
        }
        if(properties.get("colorMode").equals("PURPLE")) {
                      if (nbInput==0) displayImage=(PImage)filedata.get("workflow_purple.png");
                      if (nbInput==1) displayImage=(PImage)filedata.get("workflow_purple_single.png");
                      if (nbInput==2)displayImage=(PImage)filedata.get("workflow_purple_double.png");
                      color_border=purple_border;
                      color_object=color_purple;  
        }
        if(properties.get("colorMode").equals("GREEN")) {
                      if (nbInput==0)displayImage=(PImage)filedata.get("workflow_green.png");
                      if (nbInput==1)displayImage=(PImage)filedata.get("workflow_green_single.png");
                      if (nbInput==2)displayImage=(PImage)filedata.get("workflow_green_double.png");
                      color_border=green_border;
                      color_object=color_green;  
        }
        if (displayImage==null) {
                        if (nbInput==0) displayImage=(PImage)filedata.get("workflow_red.png");
                        if (nbInput==1) displayImage=(PImage)filedata.get("workflow_red_single.png");
                        if (nbInput==2) displayImage=(PImage)filedata.get("workflow_red_double.png");
                        color_border=red_border;
                        color_object=color_red;  
        }
    } //--End else
    //if (displayImage!=null) image(displayImage,this.x-(displayImage.width/2), this.y);
    

    fill(170); //--shadow
    stroke(170);
    rect(this.x-(156/2),this.y+5,156,83,0,0,15,15);
    fill(color_object);
    stroke(color_border);
    strokeWeight(1.2f);
    rect(this.x-(160/2),this.y,160,85,15,15,15,15);
    fill(255);

    //--Draw the input x+65, y+65
    if (nbInput==1) arc(x-(160/2), y+61, 16, 12, -HALF_PI, HALF_PI);
    if (nbInput==2) {
        arc(x-(160/2), y+61, 16, 12, -HALF_PI, HALF_PI);
        arc(x-(160/2), y+42, 16, 12, -HALF_PI, HALF_PI);
    }
    //--Draw the output
    arc(x+(160/2)+1, y+61, 19, 13, HALF_PI+0.02f, TWO_PI-HALF_PI-0.01f);
    
   strokeWeight(0.1f);
    // - draw connector
    for (int i=0; i<connection.length;i++) {
        connection[i].drawFeature();
    }
    // - draw text
    fill(0);
    textFont(font);
    textAlign(CENTER);
    String name=properties.getName();
    if (properties.isSet("ForObjectID")) {
        name="(For each) "+name;
    }
    text(name, this.x, this.y+10);
    fill(128);
//    --Note description now in tooltip
//    text(properties.getDescription(), this.x,this.y+20);
    textFont(smallfont);
    textAlign(PApplet.LEFT);
    text("Input", x-65, y+19);
    textAlign(PApplet.RIGHT);
    text("Output", x+65, y+19);
    fill(0);
    //--Output OUTPUT
    int nb_output=connection[OUTPUT].getOutput().size();
    if (properties.isSet("Connector0")) {
        text(properties.get("Connector0"), x+65, y+65);
    } else
    if (connection[OUTPUT].isOutputAll()) {
        text("All", x+65, y+65);
    } else if (nb_output>4) {
        text("Many", x+65, y+65);
    }
    else {
        int start_y=(nb_output==4?60:65)-(10*nb_output)/2;
        for (int j=0; j<nb_output;j++) {
            text(connection[OUTPUT].getOutput().get(j),x+65, y+start_y+(j*10));
        }
    }
    //--CASE 1: One input Input 1
    textAlign(PApplet.LEFT);
    if (nbInput==1)
    {
        int nb_input=connection[INPUT1].getInput().size();
        int start_y=65-(10*nb_input)/2;
        
        if (properties.isSet("Connector2")) {
            text(properties.get("Connector2"), x-65, y+65);
        } else
        if (connection[INPUT1].isInputAll()) {
           text("All", x-65, y+65);
        } else if (nb_input>5) {
           text("Many", x-65, y+65);
        }
        else {
             for (int j=0; j<nb_input;j++) {
                text(connection[INPUT1].getInput().get(j),x-65, y+start_y+(j*10));
             }
        }
        //text(connection[INPUT1].getInput().get(0),x-60, y+65);
    } //--End connection==1
    //--CASE 2: More than 1 input
    if (nbInput>1) {
        int nb_input1=connection[INPUT1].getInput().size();
        int nb_input2=connection[INPUT2].getInput().size();
        int start_y1=65-(10*nb_input1)/2;
        int start_y2=45-(10*nb_input2)/2;
        if (properties.isSet("Connector2")) {
            text(properties.get("Connector2"), x-65, y+65);
        } else
        if (connection[INPUT1].isInputAll()) {
            text("All",x-65, y+65);
        } else if (nb_input1>2) {
            text("Many",x-65, y+65);
        }
        else for (int j=0; j<nb_input1;j++) {
                    text(connection[INPUT1].getInput().get(j),x-65, y+start_y1+(j*10));
                }

        if (properties.isSet("Connector3")) {
            text(properties.get("Connector3"), x-65, y+45);
        } else
        if (connection[INPUT2].isInputAll()) {
            text("All",x-65, y+45);
        } else if (nb_input2>2) {
            text("Many",x-65, y+45);
        }
        else
            for (int j=0; j<nb_input2;j++) {
                text(connection[INPUT2].getInput().get(j),x-65, y+start_y2+(j*10));
            }
    } //--End connection >1

    
    //--Draw a progress box
    if (properties.isSet("StatusProgress")) drawProgress(properties.getInt("StatusProgress"));
    //--Show status
        int statuscode=properties.getInt("Status");
        String status="";        
        fill(128);
        switch(statuscode) {
            case RunProgram.status_nothing:
                                    fill(128);
                                    status="";
                                    //--Alternative...
                                    if (properties.getBoolean("UseAlternative")) {
                                      displayImage=(PImage)filedata.get("backet_work2.png");
                                        if (displayImage!=null) image(displayImage,this.x+40, this.y-40);  
                                    }
//                                    displayImage=(PImage)filedata.get("Button.Green-Stop-16x16.png");
//                                    if (displayImage!=null) image(displayImage,this.x+45, this.y-8);
                                    break;   

            case RunProgram.status_done:                                                                       
                                    String timeRunning="Done in : "+Util.msToString(properties.getLong("TimeRunning"));                                                                        
                                    if (config.getBoolean("Display_elapsed_time")) {
                                    //--Display Running box
                                        textFont(font);
                                        float w = textWidth(timeRunning) + 10;
                                        float h = textAscent() + textDescent() + 4;
                                        fill (255);
                                        stroke(1);
                                        strokeWeight(1.5f);
                                        rectMode(CORNER);
                                        rect (this.x-w/2,this.y-15 - h/2, w, h);
                                        fill(0);
                                        textAlign(CENTER, CENTER);
                                        text(timeRunning,this.x,this.y-15);
                                        strokeWeight(0.1f);
                                    }
                                     fill(0xff0E15C1);
                                    status="Done";
                                    displayImage=(PImage)filedata.get("backet_ok.png");
                                    if (displayImage!=null) image(displayImage,this.x+30, this.y-30);
                                    break;
            case RunProgram.status_runningclassnotfound:
                                    fill(0xffFA0D0D);
                                    status="No class found";
                                    displayImage=(PImage)filedata.get("small_warning.png");
                                    if (displayImage!=null) image(displayImage,this.x+24, this.y-20);
                                    break;
            case RunProgram.status_programnotfound:
                                    fill(0xffFA0D0D);
                                    status="Program not found";
                                    displayImage=(PImage)filedata.get("small_warning.png");
                                    if (displayImage!=null) image(displayImage,this.x+24, this.y-20);
                                    break;
            case RunProgram.status_error:
                                    fill(0xffFA0D0D);
                                    status="Error: ";
                                    displayImage=(PImage)filedata.get("small_warning.png");
                                    if (displayImage!=null) image(displayImage,this.x+24, this.y-20);
                                    break;
              case RunProgram.status_BadRequirements:
                                    fill(0xffFA0D0D);
                                    status=properties.getStatusString();
                                    displayImage=(PImage)filedata.get("small_warning.png");
                                    if (displayImage!=null) image(displayImage,this.x+24, this.y-20);
                                    break;
             case RunProgram.status_idle:
                                    fill(128);
                                    status="Waiting to run...";
                                    displayImage=(PImage)filedata.get("pause.png");
                                    if (displayImage!=null) image(displayImage,this.x+45, this.y-4);
                                    break;    
             case RunProgram.status_running: 
                                    if (config.getBoolean("Display_elapsed_time")) {
                                    long time=System.currentTimeMillis()-properties.getLong("TimeRunning");
                                    timeRunning="Elapsed: "+Util.msToString(time);

                                    //--Display Running box                                       
                                        textFont(font);
                                        float w = textWidth(timeRunning) + 10;
                                        float h = textAscent() + textDescent() + 4;
                                        fill (255);
                                        stroke(1);
                                        strokeWeight(1.5f);
                                        rectMode(CORNER);
                                        rect (this.x-w/2,this.y-15 - h/2, w, h);
                                        fill(0);
                                        textAlign(CENTER, CENTER);                                       
                                        text(timeRunning,this.x,this.y-15);                                      
                                        strokeWeight(0.1f);
                                    }
                                        fill(0xff0E15C1);
                                        status="Running";
                                        displayImage=(PImage)filedata.get("play.png");
                                        if (displayImage!=null) image(displayImage,this.x+45, this.y-4);
                                    break;
        }
        textAlign(CENTER);
        textFont(smallfont);
        if (status.length()>0) text(status,this.x, this.y+79);
    
    //if (test>100) test=0;

    //--For small_preview editor
    if (config.getBoolean("displayConnector")) {
       textFont(font);
        fill(0);
        for (int i=0; i<connection.length;i++) {
         switch(i) {
            case OUTPUT: text(str(i),connection[i].x+10, connection[i].y); break;
            case TOP:    text(str(i),connection[i].x-5, connection[i].y-2); break;
            case INPUT1: text(str(i),connection[i].x-5, connection[i].y); break;
            case INPUT2: text(str(i),connection[i].x-5, connection[i].y); break;
        }
        }
    }


        //--Detect object over
    if (inside) {
        if (time_over==0) {
            time_over=System.currentTimeMillis();
        }
    } else {
        time_over=0;
    }

    String description=properties.getTooltip();
    boolean alternative=properties.getBoolean("UseAlternative");
    //--Set the display time of tooltip  to 300 ms
    if (inside&&description.length()>0&&(System.currentTimeMillis()-time_over)>300) {
       fill(0);
       textFont(font);       
       if (alternative) description+="Beware: Using alternative executable not part of Armadillo!"; 
       float w = textWidth(description) + 10;
        float h = textAscent() + textDescent() + 4;
        if (alternative) {
          fill(0xffF71919); 
        } else fill (0xff79D1F0);
        //stroke(0xff79D1F0);
        stroke(255);
        rectMode(CORNER);
        rect (mouseX-w/2, mouseY - h/2, w, h);
        fill(0);
        textAlign(CENTER, CENTER);
        
        text(description,mouseX,mouseY);
    }

    }
  }

  /**
   * Specilaa draw if we have a workflow object
   */
  public void drawWorkflow() {
    //--Detect if we are inside the object
    boolean inside=inside();
    //--Draw this object
    strokeWeight(0.1f);
    PImage displayImage=null;
    if (properties.getBoolean("NoDraw")) return;
    int nbInput=properties.getInt("nbInput");
    if (inside||selected) {
        if (nbInput==0) displayImage=(PImage)filedata.get("workflow_black.png");
        if (nbInput==1) displayImage=(PImage)filedata.get("workflow_black_single.png");
        if (nbInput==2)  displayImage=(PImage)filedata.get("workflow_black_double.png");
    } else {
        if(properties.get("colorMode").equals("RED")) {
                        if (nbInput==0) displayImage=(PImage)filedata.get("workflow_red.png");
                        if (nbInput==1) displayImage=(PImage)filedata.get("workflow_red_single.png");
                        if (nbInput==2) displayImage=(PImage)filedata.get("workflow_red_double.png");
        }
        if(properties.get("colorMode").equals("CYAN")) {
                      if (nbInput==0) displayImage=(PImage)filedata.get("workflow_cyan.png");
                      if (nbInput==1) displayImage=(PImage)filedata.get("workflow_cyan_single.png");
                      if (nbInput==2) displayImage=(PImage)filedata.get("workflow_cyan_double.png");
        }
        if(properties.get("colorMode").equals("BLUE")){
                      if (nbInput==0) displayImage=(PImage)filedata.get("workflow_blue.png");
                      if (nbInput==1) displayImage=(PImage)filedata.get("workflow_blue_single.png");
                      if (nbInput==2) displayImage=(PImage)filedata.get("workflow_blue_double.png");
        }
        if(properties.get("colorMode").equals("ORANGE")) {
                      if (nbInput==0) displayImage=(PImage)filedata.get("workflow_orange.png");
                      if (nbInput==1) displayImage=(PImage)filedata.get("workflow_orange_single.png");
                      if (nbInput==2)displayImage=(PImage)filedata.get("workflow_orange_double.png");
        }
        if(properties.get("colorMode").equals("PURPLE")) {
                      if (nbInput==0) displayImage=(PImage)filedata.get("workflow_purple.png");
                      if (nbInput==1) displayImage=(PImage)filedata.get("workflow_purple_single.png");
                      if (nbInput==2)displayImage=(PImage)filedata.get("workflow_purple_double.png");
        }
        if(properties.get("colorMode").equals("GREEN")) {
                      if (nbInput==0)displayImage=(PImage)filedata.get("workflow_green.png");
                      if (nbInput==1)displayImage=(PImage)filedata.get("workflow_green_single.png");
                      if (nbInput==2)displayImage=(PImage)filedata.get("workflow_green_double.png");
        }
        if (displayImage==null) {
                        if (nbInput==0) displayImage=(PImage)filedata.get("workflow_red.png");
                        if (nbInput==1) displayImage=(PImage)filedata.get("workflow_red_single.png");
                        if (nbInput==2) displayImage=(PImage)filedata.get("workflow_red_double.png");
        }
    } //--End else
    if (displayImage!=null) image(displayImage,this.x-(displayImage.width/2), this.y);
    // - draw connector
    for (int i=0; i<connection.length;i++) {
        connection[i].drawFeature();
    }
    // - draw text
    fill(0);
    textFont(font);
    textAlign(CENTER);
    text(properties.getName(), this.x, this.y+10);
    textFont(smallfont);
    text(properties.getDescription(), this.x,this.y+20);
    fill(128);
    textAlign(PApplet.LEFT);
    text("Input", x-65, y+39);
    textAlign(PApplet.RIGHT);
    text("Output", x+65, y+39);
    fill(0);
    //--Output OUTPUT
    int nb_output=connection[OUTPUT].getOutput().size();
    if (connection[OUTPUT].isOutputAll()) {
        text("All", x+65, y+65);
    } else if (nb_output>4) {
        text("Many", x+65, y+65);
    }
    else {
        int start_y=(nb_output==4?60:65)-(10*nb_output)/2;
        for (int j=0; j<nb_output;j++) {
            text(connection[OUTPUT].getOutput().get(j),x+65, y+start_y+(j*10));
        }
    }
    //--CASE 1: One input Input 1
    textAlign(PApplet.LEFT);
    if (nbInput==1)
    {
        int nb_input=connection[INPUT1].getInput().size();
        int start_y=65-(10*nb_input)/2;
        if (connection[INPUT1].isInputAll()) {
           text("All", x-65, y+65);
        } else if (nb_input>5) {
           text("Many", x-65, y+65);
        }
        else {
             for (int j=0; j<nb_input;j++) {
                text(connection[INPUT1].getInput().get(j),x-65, y+start_y+(j*10));
             }
        }
        //text(connection[INPUT1].getInput().get(0),x-60, y+65);
    } //--End connection==1
    //--CASE 2: More than 1 input
    if (nbInput>1) {
        int nb_input1=connection[INPUT1].getInput().size();
        int nb_input2=connection[INPUT2].getInput().size();
        int start_y1=65-(10*nb_input1)/2;
        int start_y2=45-(10*nb_input2)/2;
        if (connection[INPUT1].isInputAll()) {
            text("All",x-65, y+65);
        } else if (nb_input1>2) {
            text("Many",x-65, y+65);
        }
        else for (int j=0; j<nb_input1;j++) {
                    text(connection[INPUT1].getInput().get(j),x-65, y+start_y1+(j*10));
                }

        if (connection[INPUT2].isInputAll()) {
            text("All",x-65, y+45);
        } else if (nb_input2>2) {
            text("Many",x-65, y+45);
        }
        else
            for (int j=0; j<nb_input2;j++) {
                text(connection[INPUT2].getInput().get(j),x-65, y+start_y2+(j*10));
            }
    } //--End connection >1


    //--Draw a progress box
    if (properties.isSet("statusProgress")) drawProgress(properties.getInt("statusProgress"));
    //--Show status
        int statuscode=properties.getInt("Status");
        String status="";
        textAlign(CENTER);
        textFont(smallfont);
        fill(128);
        switch(statuscode) {
            case RunProgram.status_nothing:
                                    fill(128);
                                    status="";
//                                    displayImage=(PImage)filedata.get("Button.Green-Stop-16x16.png");
//                                    if (displayImage!=null) image(displayImage,this.x+45, this.y-8);
                                    break;

            case RunProgram.status_done:
                                    fill(0xff0E15C1);
                                    status="Done";
                                    displayImage=(PImage)filedata.get("backet_ok.png");
                                    if (displayImage!=null) image(displayImage,this.x+30, this.y-30);
                                    break;
             case RunProgram.status_error:
                                    fill(0xffFA0D0D);
                                    status="Error";
                                    displayImage=(PImage)filedata.get("small_warning.png");
                                    if (displayImage!=null) image(displayImage,this.x+24, this.y-20);
                                    break;
              case RunProgram.status_BadRequirements:
                                    fill(0xffFA0D0D);
                                    status="Some requirements not found.";
                                    displayImage=(PImage)filedata.get("small_warning.png");
                                    if (displayImage!=null) image(displayImage,this.x+24, this.y-20);
                                    break;
             case RunProgram.status_idle:
                                    fill(128);
                                    status="Waiting to run...";
                                    displayImage=(PImage)filedata.get("pause.png");
                                    if (displayImage!=null) image(displayImage,this.x+45, this.y-4);
                                    break;
             case RunProgram.status_running:
                                    fill(0xff0E15C1);
                                    status="Running";
                                    displayImage=(PImage)filedata.get("play.png");
                                    if (displayImage!=null) image(displayImage,this.x+45, this.y-4);
                                    break;
        }

        if (status.length()>0) text(status,this.x, this.y+79);

    //if (test>100) test=0;

    //--For small_preview editor
    if (config.getBoolean("displayConnector")) {
       textFont(font);
        fill(0);
        for (int i=0; i<connection.length;i++) {
         switch(i) {
            case OUTPUT: text(str(i),connection[i].x+10, connection[i].y); break;
            case TOP:    text(str(i),connection[i].x-5, connection[i].y-2); break;
            case INPUT1: text(str(i),connection[i].x-5, connection[i].y); break;
            case INPUT2: text(str(i),connection[i].x-5, connection[i].y); break;
        }
        }
    }
  }

   public void drawSimpleFeature(){
    //--Detect if we are inside the object
    boolean inside=inside();
    // - Inside of object
    //-- This draw a shape for debug
          fill(192);
        if (inside) fill(0xff54B5E8);
        if (this.moving||this.selected) fill(0xff4ED34D);

        stroke(255);
        beginShape(QUADS);
        for(int i=0;i<Vertex_count;i++){
          vertex(Vertex[i].x,Vertex[i].y);
        }
        endShape();

    // - draw text
    fill(0);
    textFont(font);
    textAlign(CENTER);
    text(properties.getName(), this.x, this.y+11);
    if (properties.isSet("Order")&&!movingFlag) text(properties.get("Order"),this.x, this.y+23);
    fill(128);

    //--For small_preview editor
    if (config.get("displayConnector").equals("True")) {
       textFont(font);
        fill(255);
        for (int i=0; i<connection.length;i++) {
         switch(i) {
            case OUTPUT: text(str(i),connection[i].x+10, connection[i].y); break;
            case TOP:    text(str(i),connection[i].x-5, connection[i].y-2); break;
            case INPUT1: text(str(i),connection[i].x-5, connection[i].y); break;
            case INPUT2: text(str(i),connection[i].x-5, connection[i].y); break;
          }
        }
    }


        //--Detect object over
   

  }
  
  public void drawProgress(int progress) {
      if (progress>100) progress=100;
      if (progress<0) progress=0;
      stroke(0);
      noFill();
      rect(this.x-60, this.y+75, 101,4);
      fill(60);
      text(str(progress)+" %",this.x+45, this.y+80);
      fill(color(0,progress*255/100,128));
      noStroke();
      rect(this.x-59, this.y+76, progress,3);
  }

  public String getName() {
     return properties.getName();
  }

  /**
   * 
   * @param object_name
   * @return true if success
   */
//  public boolean loadProperties(String object_name) {
//      //Try to load the properties for this object
//      //If not found, we assign defaultProgram.properties
//      //if(!properties.loadFromDatabase(object_name)) {
//      //   properties.loadFromDatabase("Program");
//      //} else Config.log(properties.getProperties());
//      properties.setName(properties.getName()+"_"+this.hashCode());
//      return true;
//  }


  }//End workflow_object

class workflow_object_script extends workflow_object {

public workflow_object_script(workflow_properties obj) {
   super();
   this.properties=obj;
    Vertex=new pvertex[4];
    connection=new workflow_connector[4];
    //--Object width and height (to do load from bitmap/properties?)
    int x2=properties.getInt("x");
    int y2=properties.getInt("y");
    float w=159;
    float h=33;
    // -- Make the vertex needed
    addVertex(x2, y2);
    addVertex(x2+w,y2);
    addVertex(x2+w,y2+h);
    addVertex(x2, y2+h);
    // -Create the connector
    for (int i=0; i<connection.length;i++) {
      connection[i]=new workflow_connector(this);
    }
    recalculatePosition();
}

public workflow_object_script(workflow_properties obj, int x2, int y2) {
    super();
    this.properties=obj;
    Vertex=new pvertex[4];
    connection=new workflow_connector[4];
    //--Object width and height (to do load from bitmap/properties?)
    float w=159;
    float h=33;
    // -- Make the vertex needed
    addVertex(x2, y2);
    addVertex(x2+w,y2);
    addVertex(x2+w,y2+h);
    addVertex(x2, y2+h);
    // -Create the connector
    for (int i=0; i<connection.length;i++) {
      connection[i]=new workflow_connector(this);
    }
    recalculatePosition();
}

 @Override
 public void drawFeature(){
    if (properties.getBoolean("NoDraw")) return;
    noStroke();
    noFill();
    beginShape(QUADS);
    for(int i=0;i<Vertex_count;i++){
      vertex(Vertex[i].x,Vertex[i].y);
    }
    endShape();
    strokeWeight(0.1f);
    PImage displayImage=null;
    if (inside()||selected) {
        displayImage=(PImage)filedata.get("big_black.png");
    } else {
              if (properties.get("colorMode").equals("RED")) displayImage=(PImage)filedata.get("big_red.png");
              if (properties.get("colorMode").equals("BLUE"))displayImage=(PImage)filedata.get("big_blue.png");
              if (properties.get("colorMode").equals("ORANGE"))displayImage=(PImage)filedata.get("big_orange.png");
              if (properties.get("colorMode").equals("GREEN"))displayImage=(PImage)filedata.get("big_green.png");
              if (displayImage==null) displayImage=(PImage)filedata.get("big_red.png");
    }
    if (displayImage!=null) image(displayImage,this.x-(displayImage.width/2), this.y);
    // - draw connector
    for (int i=0; i<connection.length;i++) {
        connection[i].drawFeature();
    }
    // - draw text
    fill(0);
    textAlign(CENTER);
    textFont(font);
    text(properties.getName(), (Vertex[1].x-Vertex[0].x)/2+Vertex[0].x, (Vertex[2].y-Vertex[0].y)/2+Vertex[0].y);
    fill(128);
    text(properties.getTooltip(), (Vertex[1].x-Vertex[0].x)/2+Vertex[0].x, (Vertex[2].y-Vertex[0].y)/2+Vertex[0].y+12);
     //--For small_preview editor
    if (config.getBoolean("displayConnector")) {
        fill(0);
        for (int i=0; i<connection.length;i++) {
         switch(i) {
            case UP:   text(str(i),connection[i].x-5, connection[i].y+5); break;
            case DOWN: text(str(i),connection[i].x-5, connection[i].y+15); break;
            case LEFT: text(str(i),connection[i].x-5, connection[i].y+5); break;
            case RIGHT:text(str(i),connection[i].x+15, connection[i].y+5); break;
            }
        }
     }
   }

      @Override
  public void recalculatePosition() {
      properties.put("x", Vertex[0].x); //Thit is the left-top corner
      properties.put("y", Vertex[0].y);
      this.x=PApplet.parseInt(Vertex[0].x+(Vertex[1].x-Vertex[0].x)/2);
      this.y=PApplet.parseInt(Vertex[0].y+(Vertex[1].y-Vertex[0].y)/2);
      //-- Initialise Connector
    for (int i=0; i<connection.length; i++) {
       connection[i].parent=this;
       switch(i) {
         case UP   : updateConnector(connection[i], i, this.x, this.y-10, null);
                    break;
         case DOWN : updateConnector(connection[i], i, this.x, this.y+32, null);
                    break;
         case LEFT : updateConnector(connection[i], i, this.x-88, this.y+10, null);
                    break;
         case RIGHT: updateConnector(connection[i], i, this.x+79, this.y+10, null);
                    break;
       }
    }
  }

 } //--End script

class workflow_object_script_big extends workflow_object {

public workflow_object_script_big(workflow_properties obj) {
   super();
   this.properties=obj;
    Vertex=new pvertex[4];
    connection=new workflow_connector[4];
    //--Object width and height (to do load from bitmap/properties?)
    int x2=properties.getInt("x");
    int y2=properties.getInt("y");
    float w=249;
    float h=71;
    // -- Make the vertex needed
    addVertex(x2, y2);
    addVertex(x2+w,y2);
    addVertex(x2+w,y2+h);
    addVertex(x2, y2+h);
    // -Create the connector
    for (int i=0; i<connection.length;i++) {
      connection[i]=new workflow_connector(this);
    }
    recalculatePosition();
}

public workflow_object_script_big(workflow_properties obj, int x2, int y2) {
    super();
    this.properties=obj;
    Vertex=new pvertex[4];
    connection=new workflow_connector[4];
    //--Object width and height (to do load from bitmap/properties?)
    float w=249;
    float h=71;
    // -- Make the vertex needed
    addVertex(x2, y2);
    addVertex(x2+w,y2);
    addVertex(x2+w,y2+h);
    addVertex(x2, y2+h);
    // -Create the connector
    for (int i=0; i<connection.length;i++) {
      connection[i]=new workflow_connector(this);
    }
    recalculatePosition();
}

 @Override
 public void drawFeature(){
    if (properties.getBoolean("NoDraw")) return;
    noStroke();
    noFill();
    beginShape(QUADS);
    for(int i=0;i<Vertex_count;i++){
      vertex(Vertex[i].x,Vertex[i].y);
    }
    endShape();
    strokeWeight(0.1f);
    PImage displayImage=null;
      //--default color
    int color_border=red_border;
    int color_object=color_red;
    
     
    if (inside()||selected) {
        displayImage=(PImage)filedata.get("search_black.png");
         color_border=black_border;
        color_object=color_black;      
    } else {
              if (properties.get("colorMode").equals("RED")) {
                  displayImage=(PImage)filedata.get("search_red.png");
                   color_border=red_border;
                    color_object=color_red;      
              }
              if (properties.get("colorMode").equals("BLUE")) {
                  displayImage=(PImage)filedata.get("search_blue.png");
                  color_border=blue_border;
                    color_object=color_blue;  
              }
              if (properties.get("colorMode").equals("ORANGE")) {
                  displayImage=(PImage)filedata.get("search_orange.png");
                  color_border=orange_border;
                    color_object=color_orange;  
              }
              if (properties.get("colorMode").equals("GREEN")) {
                  displayImage=(PImage)filedata.get("search_green.png");
                  color_border=green_border;
                    color_object=color_green;  
              }
              if (properties.get("colorMode").equals("CYAN")) {
                  displayImage=(PImage)filedata.get("search_cyan.png");
                  color_border=cyan_border;
                    color_object=color_cyan;  
              }
              if (properties.get("colorMode").equals("PURPLE")) {
                  displayImage=(PImage)filedata.get("search_purple.png");
                  color_border=purple_border;
                    color_object=color_purple;  
              }
              if (displayImage==null) {
                  displayImage=(PImage)filedata.get("big_red.png");
                  color_border=red_border;
                  color_object=color_red;  
              }
    }
    //if (displayImage!=null) image(displayImage,this.x-(displayImage.width/2), this.y);
    fill(170); //--shadow
    stroke(170);
    rect(this.x-(246/2),this.y+6,246,66,15,15,15,15);
    fill(color_object);
    stroke(color_border);
    strokeWeight(1.2f);
    rect(this.x-(250/2),this.y,250,71,15,15,15,15);
    strokeWeight(0.5f);
        
    // - draw connector 
    for (int i=0; i<connection.length;i++) {
        connection[i].drawFeature();
    }
    // - draw text
    String name=properties.getName();
    String desc=properties.getDescription();
    int len=desc.length();
    int start_y=this.y+5;
    fill(0);
    textAlign(CENTER);
    textFont(font);
    text(name, this.x, start_y+=10);
    textFont(smallfont);
    String[] s=desc.split("\n");
    for (int i=0; i<s.length;i++) {
        text(s[i], this.x, start_y+=10);
    }
      
     //--For small_preview editor
    if (config.get("displayConnector").equals("True")) {
        fill(0);
        for (int i=0; i<connection.length;i++) {
         switch(i) {
            case UP:   text(str(i),connection[i].x-5, connection[i].y+5); break;
            case DOWN: text(str(i),connection[i].x-5, connection[i].y+15); break;
            case LEFT: text(str(i),connection[i].x-5, connection[i].y+5); break;
            case RIGHT:text(str(i),connection[i].x+15, connection[i].y+5); break;
            }
        }
    }

 }

     @Override
  public void recalculatePosition() {
      this.x=PApplet.parseInt(Vertex[0].x+(Vertex[1].x-Vertex[0].x)/2);
      this.y=PApplet.parseInt(Vertex[0].y+(Vertex[1].y-Vertex[0].y)/2);
//      properties.put("x", this.x);
//      properties.put("y", this.y);
      //-- Initialise Connector
    for (int i=0; i<connection.length; i++) {
       connection[i].parent=this;
       switch(i) {
         case UP   : updateConnector(connection[i], i, this.x, this.y-9, null);
                    break;
         case DOWN : updateConnector(connection[i], i, this.x, this.y+70, null);
                    break;
         case LEFT : updateConnector(connection[i], i, this.x-133, this.y+30, null);
                    break;
         case RIGHT: updateConnector(connection[i], i, this.x+125, this.y+30, null);
                    break;
       }
    }
  }
} //--End script_big


class workflow_object_BeginEnd extends workflow_object {

public workflow_object_BeginEnd(workflow_properties obj) {
    super();
    this.properties=obj;
    Vertex=new pvertex[4];
    connection=new workflow_connector[4];
    //--Object width and height (to do load from bitmap/properties?)
    float x1=properties.getInt("x");
    float y1=properties.getInt("y");
    float w=76;
    float h=30;
    // -- Make the vertex needed
    addVertex(x1, y1);
    addVertex(x1+w,y1);
    addVertex(x1+w,y1+h);
    addVertex(x1, y1+h);
    // -Create the connector
    for (int i=0; i<connection.length;i++) {
      connection[i]=new workflow_connector(this);
    }
    // - Calculate their position
    recalculatePosition();
   
}

public workflow_object_BeginEnd(workflow_properties obj, int x1, int y1) {
    super();
    this.properties=obj;
    Vertex=new pvertex[4];
    connection=new workflow_connector[4];
      //--Object width and height (to do load from bitmap/properties?)
    float w=76;
    float h=30;
    // -- Make the vertex needed
    addVertex(x1, y1);
    addVertex(x1+w,y1);
    addVertex(x1+w, y1+h);
    addVertex(x1, y1+h);
    // -Create the connector
    for (int i=0; i<connection.length;i++) {
      connection[i]=new workflow_connector(this);
//      connection[i].conditional=true;
//      if (properties.get("ObjectType").equals("Begin")) connection[i].output=true;
    }
    // - Calculate their position
    recalculatePosition();
 
  }



        @Override
 public void drawFeature(){
   
//    noFill();
//    noStroke();
//    beginShape(QUADS);
//    for(int i=0;i<Vertex_count;i++){
//      vertex(Vertex[i].x,Vertex[i].y);
//    }
//    endShape();
    boolean inside=inside();
    strokeWeight(0.1f);
    PImage displayImage=null;
    if (inside||selected) {
        displayImage=(PImage)filedata.get("small_black.png");
    } else {
      
          if (properties.get("colorMode").equals("RED"))  displayImage=(PImage)filedata.get("small_red.png");
          if (properties.get("colorMode").equals("BLUE")) displayImage=(PImage)filedata.get("small_blue.png");
          if (properties.get("colorMode").equals("ORANGE")) displayImage=(PImage)filedata.get("small_orange.png");
          if (properties.get("colorMode").equals("GREEN")) displayImage=(PImage)filedata.get("small_green.png");
          if (displayImage==null) displayImage=(PImage)filedata.get("small_red.png");
     
    }
    if (displayImage!=null) image(displayImage,this.x-(displayImage.width/2), this.y-2);
    // - draw connector
    for (int i=0; i<connection.length;i++) {
        connection[i].drawFeature();
    }
    // - draw text
    fill(0);
    textAlign(CENTER);
    textFont(font);
    text(properties.getName(), (Vertex[1].x-Vertex[0].x)/2+Vertex[0].x, (Vertex[2].y-Vertex[0].y)/2+Vertex[0].y);
//    fill(128);
//    text(properties.getDescription(), (Vertex[1].x-Vertex[0].x)/2+Vertex[0].x, (Vertex[2].y-Vertex[0].y)/2+Vertex[0].y+12);
     //--For small_preview editor
    if (config.get("displayConnector").equals("True")) { 
        fill(0);
        for (int i=0; i<connection.length;i++) {
         switch(i) {
            case UP:   text(str(i),connection[i].x-5, connection[i].y+5); break;
            case DOWN: text(str(i),connection[i].x-5, connection[i].y+15); break;
            case LEFT: text(str(i),connection[i].x-5, connection[i].y+5); break;
            case RIGHT:text(str(i),connection[i].x+15, connection[i].y+5); break;
            }
        }
    }
     //--Detect object over
    if (inside) {
        if (time_over==0) {
            time_over=System.currentTimeMillis();
        }
    } else {
        time_over=0;
    }
     String description=properties.getDescription();
     if (inside&&description.length()>0&&(System.currentTimeMillis()-time_over)>2000) {
       fill(0);
       textFont(smallfont);

        float w = textWidth(description) + 10;
        float h = textAscent() + textDescent() + 4;
        fill (0xff79D1F0);
        stroke(0xff79D1F0);
        rectMode(CORNER);
        rect (mouseX-w/2, mouseY - h/2, w, h);
        fill(0);
        textAlign(CENTER, CENTER);
        text(description,mouseX,mouseY);
    }

  }

  /**
   * Calculate object_connector location
   */
  @Override
  public void recalculatePosition() {
      properties.put("x", str(Vertex[0].x)); //Thit is the left-top corner
      properties.put("y", str(Vertex[0].y));
      this.x=PApplet.parseInt(Vertex[0].x+(Vertex[1].x-Vertex[0].x)/2);
      this.y=PApplet.parseInt(Vertex[0].y+(Vertex[1].y-Vertex[0].y)/2);
      //-- Initialise Connector
    for (int i=0; i<connection.length; i++) {
       switch(i) {
         case UP   :updateConnector(connection[i], i, this.x, this.y-10, null);
                    break;
         case DOWN : updateConnector(connection[i], i, this.x, this.y+30, null);
                     break;
         case LEFT : updateConnector(connection[i], i, this.x-47, this.y+10, null);
                     break;
         case RIGHT:updateConnector(connection[i], i, this.x+38, this.y+10, null);
                    break;
       }
    }

  }


}

/**
 * This represent an output object
 * used to represent one type of output
 */
class workflow_object_output extends workflow_object {

    float step=1;        //Current step for animation
    float stepNb=20;     //Number of step
    pvertex source;       //Source Spline vertex
    pvertex destination; //Destination Spline vertex
    String description="";

    ////////////////////////////////////////////////////////////////////////////
    /// Constants
    final static int Output_LEFT=0;
    final static int Output_RIGHT=1;
    
    ////////////////////////////////////////////////////////////////////////////
    /// Default constructor for other objects
    
    public workflow_object_output() {}

    ////////////////////////////////////////////////////////////////////////////
    /// Normal default constructor

    public workflow_object_output(String outputType, int x, int y) {
        super();
        //--Set the object properties
        properties.setName(outputType);
        properties.put("colorMode","GREEN");
        properties.put("defaultColor","GREEN");
        properties.put("Output"+outputType, "True");
        properties.put("outputType", outputType);
        properties.put("Connector1Output","True");
        properties.put("ObjectType", "Output");
        properties.put("editorClass", "editors.OutputEditor");
        //this.outputType=outputType;
        Vertex=new pvertex[4];
        connection=new workflow_connector[2];
        // -- Weight and height
        float w = 61;
        float h = 14;
               // -- Make the vertex needed
        addVertex(x, y);
        addVertex(x+w,y);
        addVertex(x+w, y+h);
        addVertex(x, y+h);
        // -Create the connector
        for (int i=0; i<connection.length;i++) {
          connection[i]=new workflow_connector(this);
        }
        // - Calculate their position
        recalculatePosition();
 }


     public workflow_object_output(workflow_properties properties, int x, int y) {
        super();
        this.properties=properties;
        Vertex=new pvertex[4];
        connection=new workflow_connector[2];
        // -- Weight and height
        float w = 61;
        float h = 14;
         // -- Make the vertex needed
        addVertex(x, y);
        addVertex(x+w,y);
        addVertex(x+w, y+h);
        addVertex(x, y+h);
        // -Create the connector
        for (int i=0; i<connection.length;i++) {
          connection[i]=new workflow_connector(this);
        }
        // - Calculate their position
        recalculatePosition();
 }

        @Override
 public void drawFeature(){
    if (properties.getBoolean("NoDraw")) return;
    // - Inside of object
    boolean inside=inside();
    //-- Draw shape this draw a very basic shape in case we don't have the image
    //-- Note also used for the inside procedure...
    strokeWeight(0.1f);
    noStroke();
    noFill();
    beginShape(QUADS);
    for(int i=0;i<Vertex_count;i++){
      vertex(Vertex[i].x,Vertex[i].y);
    }
    endShape();

    PImage displayImage=null;
    
    //--default color
    int color_border=red_border;
    int color_object=color_red;
        
    if (inside||selected) {
        displayImage=(PImage)filedata.get("link_black.png");
          color_border=black_border;
        color_object=color_black;      
    } else if (properties.getOutputID(properties.get("outputType"))==0) {
        displayImage=(PImage)filedata.get("link_cyan.png");
         color_border=cyan_border;
        color_object=color_cyan;     
    } else {
          if (properties.get("colorMode").equals("RED")) {
              displayImage=(PImage)filedata.get("link_red.png");
               color_border=red_border;
                 color_object=color_red;   
          }
          if (properties.get("colorMode").equals("BLUE")) {
              displayImage=(PImage)filedata.get("link_blue.png");
               color_border=blue_border;
               color_object=color_blue;   
          }
          if (properties.get("colorMode").equals("ORANGE")) {
              displayImage=(PImage)filedata.get("link_orange.png");
               color_border=orange_border;
               color_object=color_orange;   
          }
          if (properties.get("colorMode").equals("GREEN"))  {
              displayImage=(PImage)filedata.get("link_green.png");
              color_border=green_border;
              color_object=color_green;  
          }
          if (displayImage==null) {              
              displayImage=(PImage)filedata.get("link_green.png");
              color_border=green_border;
              color_object=color_green;  
          }
    }
    
    //if (displayImage!=null) image(displayImage,this.x-(displayImage.width/2), this.y-2);
    fill(170); //--shadow
    stroke(170);
    rect(this.x-(61/2),this.y+2,59,18,0,0,4,4); 
    strokeWeight(1.2f);
    stroke(color_border); 
    fill(color_object); //--Aread (green)
    rect(this.x-(61/2),this.y,61,18,4,4,4,4);
    strokeWeight(0.5f);

// - draw connector
    for (int i=0; i<connection.length;i++) {
        if (i==Output_RIGHT) connection[i].drawFeature();
    }
    // - draw text
    fill(0);
    textAlign(CENTER);
    textFont(smallfont);
    text(properties.getName(), (Vertex[1].x-Vertex[0].x)/2+Vertex[0].x, (Vertex[2].y-Vertex[0].y)/2+Vertex[0].y+2);
    fill(128);
    textFont(font);

    //--Detect object over
    if (inside) {
        if (time_over==0) {
            time_over=System.currentTimeMillis();
        }
    } else {
        time_over=0;
        description="";
    }
     String type=properties.get("outputType");
     int output_id=properties.getInt("output_"+type.toLowerCase()+"_id");

     

     if (inside&&output_id>0&&(System.currentTimeMillis()-time_over)>10) {
        fill(0);
        textFont(font);
        Output output=new Output();
        output.setType(type);
        //--This will try to load the description
        //--Update: No, must be done in updateDependance
//        if (description.isEmpty()) description=output.getBiologic().getNameId(output_id);
        if (properties.isSet("Description")) description=properties.get("Description");
        if (description==null) description="";
        float w = textWidth(description) + 10;
        float h = textAscent() + textDescent() + 4;
        fill (0xff79D1F0);
        stroke(255);
        //stroke(0xff79D1F0);
        rectMode(CORNER);
        rect (mouseX-w/2, mouseY - h/2, w, h);
        fill(0);
        textAlign(CENTER, CENTER);
        text(description,mouseX,mouseY);
    }

  }

  /**
   * Calculate object_connector location
   */
  @Override
  public void recalculatePosition() {
      properties.put("x", Vertex[0].x); //Thit is the left-top corner
      properties.put("y", Vertex[0].y);
      this.x=PApplet.parseInt(Vertex[0].x+(Vertex[1].x-Vertex[0].x)/2);
      this.y=PApplet.parseInt(Vertex[0].y+(Vertex[1].y-Vertex[0].y)/2);
      //-- Initialise Connector
    for (int i=0; i<connection.length; i++) {
        switch(i) {
          case Output_LEFT : updateConnector(connection[i],i,this.x-35,this.y+2,null);
                             break;
          case Output_RIGHT: updateConnector(connection[i],i,this.x+30,this.y+2,null);
                             break;
       }
    }
  }

        @Override
  public String getName() {
      return properties.get("outputType");
  }

}


/**
 * Bigger object 
 */
class workflow_object_output_big extends workflow_object_output {

    public workflow_object_output_big(String outputType, int x, int y) {
         super();
         properties.setName(outputType);
         properties.put("colorMode","GREEN");
         properties.put("defaultColor","GREEN");
         properties.put("Output"+outputType, "True");
         properties.put("outputType", outputType);
         properties.put("Connector1Output","True");
         properties.put("ObjectType", "Output");
         properties.put("editorClass", "editors.OutputEditor");
         Vertex=new pvertex[4];
         connection=new workflow_connector[2];
            //-- Object w and h
            float w=61;
            float h=21;
            // -- Make the vertex needed
            addVertex(x, y);
            addVertex(x+w,y);
            addVertex(x+w, y+h);
            addVertex(x, y+h);
            // -Create the connector
            for (int i=0; i<connection.length;i++) {
              connection[i]=new workflow_connector(this);
            }
            // - Calculate their position
            recalculatePosition();
             
    }

    public workflow_object_output_big(workflow_properties properties, int x, int y) {
         super();
         this.properties=properties;
         Vertex=new pvertex[4];
         connection=new workflow_connector[2];
            //-- Object w and h
            float w=61;
            float h=21;
            // -- Make the vertex needed
            addVertex(x, y);
            addVertex(x+w,y);
            addVertex(x+w, y+h);
            addVertex(x, y+h);
            // -Create the connector
            for (int i=0; i<connection.length;i++) {
              connection[i]=new workflow_connector(this);
            }
            // - Calculate their position
            recalculatePosition();
             
    }

 @Override
 public void drawFeature(){
   if (properties.getBoolean("NoDraw")) return;
    boolean inside=inside();
    strokeWeight(0.1f);
    noStroke();
    noFill();
    beginShape(QUADS);
    for(int i=0;i<Vertex_count;i++){
      vertex(Vertex[i].x,Vertex[i].y);
    }
    endShape();

    
    PImage displayImage=null;
     //--default color
    int color_border=red_border;
    int color_object=color_red;
    
    if (inside||selected) {
        displayImage=(PImage)filedata.get("link_big_black.png");
        color_border=black_border;
        color_object=color_black;      
    } else if (properties.getOutputID(properties.get("outputType"))==0) {
        displayImage=(PImage)filedata.get("link_big_cyan.png");
          color_border=cyan_border;
        color_object=color_cyan;   
    } else {
         if (properties.get("colorMode").equals("RED")) {
             displayImage=(PImage)filedata.get("link_big_red.png");
              color_border=red_border;
              color_object=color_red;   
         }
         if (properties.get("colorMode").equals("BLUE")) {
             displayImage=(PImage)filedata.get("link_big_blue.png");
              color_border=blue_border;
              color_object=color_blue;   
         }
         if (properties.get("colorMode").equals("ORANGE")){
             displayImage=(PImage)filedata.get("link_big_orange.png");
              color_border=orange_border;
              color_object=color_orange;   
         }
         if (properties.get("colorMode").equals("GREEN")) {
             displayImage=(PImage)filedata.get("link_big_green.png");
              color_border=green_border;
              color_object=color_green;   
         }
         if (displayImage==null) {
             displayImage=(PImage)filedata.get("link_big_green.png");
             color_border=green_border;
              color_object=color_green;   
         }
      
    }
    //if (displayImage!=null) image(displayImage,this.x-(displayImage.width/2), this.y-2);
    
    ////--Forth
fill(170); //--shadow
stroke(170);
rect(x-(59/2),this.y,59,26,0,0,4,4); 
fill(color_object); 
stroke(color_border);
strokeWeight(1.2f);
rect(x-(61/2),this.y,61,26,4,4,4,4);
strokeWeight(0.5f);


    
    
    // - draw connector
    for (int i=0; i<connection.length;i++) {
        if (i==Output_RIGHT) connection[i].drawFeature();
    }
    // - draw text
    fill(0);
    textAlign(CENTER);
    textFont(smallfont);
    String name=properties.getName();
    String name0="";
    String name1="";
    if (name.startsWith("Multiple")) {
        name0="Multiple";
        name1=name.substring(8);
    } else {
        name0=name.substring(0,name.length()/2);
        name1=name.substring(name.length()/2);
    }
    text(name0, (Vertex[1].x-Vertex[0].x)/2+Vertex[0].x, (Vertex[2].y-Vertex[0].y)/2+Vertex[0].y-2);
    text(name1, (Vertex[1].x-Vertex[0].x)/2+Vertex[0].x, (Vertex[2].y-Vertex[0].y)/2+Vertex[0].y+8);
    fill(128);
    textFont(font);
    //--Detect object over
    if (inside) {
        if (time_over==0) {
            time_over=System.currentTimeMillis();
        }
    } else {
        time_over=0;
        description="";
    }
     String type=properties.get("outputType");
     int output_id=properties.getInt("output_"+type.toLowerCase()+"_id");
     if (inside&&output_id>0&&(System.currentTimeMillis()-time_over)>10) {
        fill(0);
        textFont(font);
        Output output=new Output();
        output.setType(type);
        //--This will try to load the description
        //No: must be done in update dependance
//        if (description.isEmpty()) description=output.getBiologic().getNameId(output_id);
        if (properties.isSet("Description")) description=properties.get("Description");
        if (description==null) description="";
        float w = textWidth(description) + 10;
        float h = textAscent() + textDescent() + 4;
        fill (0xff79D1F0);
        stroke(255);
        //stroke(0xff79D1F0);
        rectMode(CORNER);
        rect (mouseX-w/2, mouseY - h/2, w, h);
        fill(0);
        textAlign(CENTER, CENTER);
        text(description,mouseX,mouseY);
    }
  }

 /**
   * Calculate object_connector location
   */
  @Override
  public void recalculatePosition() {
      properties.put("x", Vertex[0].x); //Thit is the left-top corner
      properties.put("y", Vertex[0].y);
      this.x=PApplet.parseInt(Vertex[0].x+(Vertex[1].x-Vertex[0].x)/2);
      this.y=PApplet.parseInt(Vertex[0].y+(Vertex[1].y-Vertex[0].y)/2);
      //-- Initialise Connector
    for (int i=0; i<connection.length; i++) {
       connection[i].parent=this;
       switch(i) {
          case Output_LEFT : updateConnector(connection[i],i,this.x-35,this.y+2,null);
                             break;
          case Output_RIGHT: updateConnector(connection[i],i,this.x+28,this.y+2,null);
                             break;
       }
    }

  }

}

/**
 * Bigger object
 */
public class workflow_object_variable extends workflow_object_output {

      public workflow_object_variable (workflow_properties properties, int x, int y) {
         super();
         this.properties=properties;
         Vertex=new pvertex[4];
         connection=new workflow_connector[2];
         //--Object width and height (to do load from bitmap/properties?)
         int x2=x;
         int y2=y;
         float w=159;
         float h=33;
         // -- Make the vertex needed
         addVertex(x2, y2);
         addVertex(x2+w,y2);
         addVertex(x2+w,y2+h);
         addVertex(x2, y2+h);
         // -Create the connector
         for (int i=0; i<connection.length;i++) {
           connection[i]=new workflow_connector(this);
         }
         recalculatePosition();

    }

 @Override
 public void drawFeature(){
   //--Draw the object on screen?
   if (properties.getBoolean("NoDraw")) return;
   //--Load information


   //--Load the flag
   boolean inside=inside();

   //--Actual draw
   strokeWeight(0.1f);
    noStroke();
    noFill();

    beginShape(QUADS);
    for(int i=0;i<Vertex_count;i++){
      vertex(Vertex[i].x,Vertex[i].y);
    }
    endShape();



    PImage displayImage=null;
     if (inside()||selected) {
        displayImage=(PImage)filedata.get("big_black.png");
    } else if (properties.get("Description").equals("Undefined")) {
        displayImage=(PImage)filedata.get("big_blue.png");
    } else if (properties.get("Description").equals("For each")) {
        displayImage=(PImage)filedata.get("big_green.png");
    }
    else {
              if (properties.get("colorMode").equals("RED")) displayImage=(PImage)filedata.get("big_red.png");
              if (properties.get("colorMode").equals("BLUE"))displayImage=(PImage)filedata.get("big_blue.png");
              if (properties.get("colorMode").equals("ORANGE"))displayImage=(PImage)filedata.get("big_orange.png");
              if (properties.get("colorMode").equals("GREEN"))displayImage=(PImage)filedata.get("big_green.png");
              if (displayImage==null) displayImage=(PImage)filedata.get("big_red.png");
    }
    if (displayImage!=null) image(displayImage,this.x-(displayImage.width/2), this.y-2);
    // - draw connector
    for (int i=0; i<connection.length;i++) {
       connection[i].drawFeature();
    }
    // - draw text
    fill(0);
    textAlign(CENTER);
    textFont(font);
    String name=properties.getName();
    String name1=properties.getDescription();
    //Clip name
    name1=(name1.length()>30?name1.substring(0, 30)+"...":name1);
    text(name, (Vertex[1].x-Vertex[0].x)/2+Vertex[0].x, (Vertex[2].y-Vertex[0].y)/2+Vertex[0].y-5);
    textFont(smallfont);
    text(name1, (Vertex[1].x-Vertex[0].x)/2+Vertex[0].x, (Vertex[2].y-Vertex[0].y)/2+Vertex[0].y+8);
    fill(128);
    textFont(font);
    
    //--Detect object over
    if (inside) {
        if (time_over==0) {
            time_over=System.currentTimeMillis();
        }
    } else {
        time_over=0;
        description="";
    }
    if (description.isEmpty()) {
        String type=properties.get("outputType");
         int type_id=properties.getInt("output_"+type.toLowerCase()+"_id");
         Output out=new Output();
         out.setType(type);

         if (type_id==0) {
             description="Undefined";
             if (properties.isSet("ForObjectID")) {
                 int count=0;
                 Object[] o=properties.keySet().toArray();
                  for (int i=0; i<o.length; i++) {
                      Object k=o[i];
                      if (((String)k).startsWith("For_")) count++;
                  }

//                 for (Object o:properties.keySet()) {
//                     if (((String)o).startsWith("For_")) count++;
//                 }
                 description="For each for "+count+" "+type;
             }
             //--Handle For loop here...
         } else {
            //description= out.getBiologic().getNameId(type_id);
             if (properties.isSet("Description")) description=properties.get("Description");
         }
    }
     if (inside&&description.length()>0&&(System.currentTimeMillis()-time_over)>10) {
        fill(0);
        textFont(font);
        float w = textWidth(description) + 10;
        float h = textAscent() + textDescent() + 4;
        fill (0xff79D1F0);
        stroke(255);
        rectMode(CORNER);
        rect (mouseX-w/2, mouseY - h/2, w, h);
        fill(0);
        textAlign(CENTER, CENTER);
        text(description,mouseX,mouseY);
    }
  }

 /**
   * Calculate object_connector location
   */
  @Override
  public void recalculatePosition() {
      properties.put("x", Vertex[0].x); //Thit is the left-top corner
      properties.put("y", Vertex[0].y);
      this.x=PApplet.parseInt(Vertex[0].x+(Vertex[1].x-Vertex[0].x)/2);
      this.y=PApplet.parseInt(Vertex[0].y+(Vertex[1].y-Vertex[0].y)/2);
      //-- Initialise Connector

    for (int i=0; i<connection.length; i++) {
       connection[i].parent=this;
       switch(i) {
         case Output_LEFT : updateConnector(connection[i], i, this.x-88, this.y+10, null);
                    break;
         case Output_RIGHT: updateConnector(connection[i], i, this.x+79, this.y+10, null);
                    break;
       }
    }
    }
}


/**
 * Bigger object
 */
public class workflow_object_output_database extends workflow_object_output {

      public workflow_object_output_database (workflow_properties properties, int x, int y) {
         super();
         this.properties=properties;
         //--Hack for new genome
         if (properties.get("outputType").equals("Genome")) {
             Genome g=new Genome(properties.getInt("output_genome_id"));  
             workflow_properties t=new workflow_properties();
             t.deserializeFromString(g.getText());
             properties.put("inputname",t.get("inputname"));
             properties.put("inputname2",t.get("inputname2"));
             properties.put("type",t.get("type"));
             properties.put("name",t.get("name"));
             properties.put("Description",t.get("Description"));
         }
         
         Vertex=new pvertex[4];
         connection=new workflow_connector[2];
         //--Object width and height (to do load from bitmap/properties?)
         int x2=x;
         int y2=y;
         float w=159;
         float h=33;
         // -- Make the vertex needed
         addVertex(x2, y2);
         addVertex(x2+w,y2);
         addVertex(x2+w,y2+h);
         addVertex(x2, y2+h);
         // -Create the connector
         for (int i=0; i<connection.length;i++) {
           connection[i]=new workflow_connector(this);
         }
         recalculatePosition();

    }

 @Override
 public void drawFeature(){
   //--Draw the object on screen?
   if (properties.getBoolean("NoDraw")) return;
   //--Load information


   //--Load the flag
   boolean inside=inside();

   //--Actual draw
   strokeWeight(0.1f);
    noStroke();
    noFill();

    beginShape(QUADS);
    for(int i=0;i<Vertex_count;i++){
      vertex(Vertex[i].x,Vertex[i].y);
    }
    endShape();



    PImage displayImage=null;
    //--default color
    int color_border=red_border;
    int color_object=color_red;
    
     if (inside()||selected) {
        displayImage=(PImage)filedata.get("big_black.png");
        color_border=black_border;
        color_object=color_black;        
    } else if (properties.get("Description").equals("Undefined")) {
        displayImage=(PImage)filedata.get("big_blue.png");
        color_border=blue_border;
        color_object=color_blue;        
        
    } else if (properties.get("Description").equals("For each")) {
        displayImage=(PImage)filedata.get("big_green.png");
        color_border=green_border;
        color_object=color_green;        
    }
    else {
              if (properties.get("colorMode").equals("RED")) {
                  displayImage=(PImage)filedata.get("big_red.png");
                   color_border=red_border;
                   color_object=color_red;
              }
              if (properties.get("colorMode").equals("BLUE")){
                  displayImage=(PImage)filedata.get("big_blue.png");
                   color_border=blue_border;
                   color_object=color_blue; 
              }
              if (properties.get("colorMode").equals("ORANGE")){
                  displayImage=(PImage)filedata.get("big_orange.png");
                   color_border=orange_border;
                   color_object=color_orange; 
              }
              if (properties.get("colorMode").equals("GREEN")){
                  displayImage=(PImage)filedata.get("big_green.png");
                   color_border=green_border;
                     color_object=color_green;    
              }
              if (displayImage==null) {                  
                  displayImage=(PImage)filedata.get("big_red.png");
                   color_border=red_border;
                   color_object=color_red;
              }
    }
    //if (displayImage!=null) image(displayImage,this.x-(displayImage.width/2), this.y-2);
     //-*-New vectorial object
     fill(170); //--shadow 100
     stroke(170);
     rect(this.x-(158/2),this.y+3,158,35,0,0,4,4);   
     strokeWeight(1.2f);   
     stroke(color_border); //--green border
     fill(color_object); //--Aread (green)
     rect(this.x-(160/2),this.y,160,35,4,4,4,4);
     strokeWeight(0.1f);
    
     // - draw connector
    for (int i=0; i<connection.length;i++) {
       connection[i].drawFeature();
    }
    
    if ( properties.isSet("AggregateObjectID")) {
		//--Add paralel information here		
					noFill();
					strokeWeight(2.0f);
					stroke(128);
					strokeCap(ROUND);
					rect(this.x-100, this.y-25, 200, 75);		
					strokeWeight(0.1f);
					fill(0);
					textAlign(CENTER);
					textFont(boldfont);
					text("Parallel section (Aggregate)",this.x,this.y-10);
					textFont(font);
					fill(0);				
	
	} else
	if (properties.isSet("ForObjectID")) {                     				
					//description="For each for "+count+" "+type;
				 //--Add paralel information here		
					noFill();
					strokeWeight(2.0f);
					stroke(128);
					strokeCap(ROUND);
					rect(this.x-100, this.y-25, 200, 75);		
					strokeWeight(0.1f);
					fill(0);
					textAlign(CENTER);
					textFont(boldfont);
					text("Parallel section (Repetition)",this.x,this.y-10);
					textFont(font);
					fill(0);					
	  }
    
    // - draw text
    fill(0);
    textAlign(CENTER);
    textFont(font);
    String name=properties.getName();
    String name1=properties.getDescription();
    //Clip name
    name1=(name1.length()>30?name1.substring(0, 30)+"...":name1);
    text(name, (Vertex[1].x-Vertex[0].x)/2+Vertex[0].x, (Vertex[2].y-Vertex[0].y)/2+Vertex[0].y-5);
    textFont(smallfont);
    text(name1, (Vertex[1].x-Vertex[0].x)/2+Vertex[0].x, (Vertex[2].y-Vertex[0].y)/2+Vertex[0].y+8);
    fill(128);
    textFont(font);
    
    //--Detect object over
    if (inside) {
        if (time_over==0) {
            time_over=System.currentTimeMillis();
        }
    } else {
        time_over=0;
        description="";
    }
    if (description.isEmpty()) {
        String type=properties.get("outputType");
         int type_id=properties.getInt("output_"+type.toLowerCase()+"_id");
         Output out=new Output();
         out.setType(type);

         if (type_id==0) {
             description="Undefined";
             if (properties.isSet("ForObjectID")) {
                 int count=0;
                 Object[] o=properties.keySet().toArray();
                  for (int i=0; i<o.length; i++) {
                      Object k=o[i];
                      if (((String)k).startsWith("For_")) count++;
                  }

//                 for (Object o:properties.keySet()) {
//                     if (((String)o).startsWith("For_")) count++;
//                 }
                 description="For each for "+count+" "+type;
             }
             //--Handle For loop here...
         } else {
            //description= out.getBiologic().getNameId(type_id);
             if (properties.isSet("Description")) description=properties.get("Description");
         }
    }
     if (inside&&description.length()>0&&(System.currentTimeMillis()-time_over)>10) {
        fill(0);
        textFont(font);
        float w = textWidth(description) + 10;
        float h = textAscent() + textDescent() + 4;
        fill (0xff79D1F0);
        stroke(255);
        rectMode(CORNER);
        rect (mouseX-w/2, mouseY - h/2, w, h);
        fill(0);
        textAlign(CENTER, CENTER);
        text(description,mouseX,mouseY);
    }
  }

 /**
   * Calculate object_connector location
   */
  @Override
  public void recalculatePosition() {
      properties.put("x", Vertex[0].x); //Thit is the left-top corner
      properties.put("y", Vertex[0].y);
      this.x=PApplet.parseInt(Vertex[0].x+(Vertex[1].x-Vertex[0].x)/2);
      this.y=PApplet.parseInt(Vertex[0].y+(Vertex[1].y-Vertex[0].y)/2);
      //-- Initialise Connector

    for (int i=0; i<connection.length; i++) {
       connection[i].parent=this;
       switch(i) {
         case Output_LEFT : updateConnector(connection[i], i, this.x-88, this.y+10, null);
                    break;
         case Output_RIGHT: updateConnector(connection[i], i, this.x+79, this.y+10, null);
                    break;
       }
    }
    }
}


public class workflow_object_aggregator extends workflow_object {
  public int x1,y1,x2,y2;
  int w=0;
  int h=0;
  int min_w=100;
  int min_h=100;
  public workflow_drag_connector[] drag; //Drag connector for redimension

  /**
 *
 * @param name
 * @param x
 * @param y
 * @param w
 * @param h
 */
 public workflow_object_aggregator(workflow_properties obj, int x, int y, int w, int h) {
   super();
   this.properties=obj;
   this.resetColorMode();
   this.x1=x;
   this.y1=y;
   this.w=w;
   this.h=h;
   this.resetColorMode();
   drag = new workflow_drag_connector[2]; //Drag connector for redimension
   connection=new workflow_connector[4];  //Connector
   // --Initialise connector and drag
   for (int i=0; i<connection.length;i++) {
     connection[i]=new workflow_connector(this);
    }
   for (int i=0; i<drag.length;i++) {
     drag[i]=new workflow_drag_connector();
     drag[i].parent=this;
   }

    min_w=(int) (textWidth(properties.getName()+" ( "+properties.getDescription()+" )") + 20);
    min_h=20;
    update(this.x1+this.w, this.y1+this.h);
 }

 /**
 *
 * @param name
 * @param x
 * @param y
 * @param w
 * @param h
 */
 public workflow_object_aggregator(workflow_properties obj, int x, int y) {
   super();
   this.properties=obj;
   this.resetColorMode();
   this.x1=x;
   this.y1=y;
   this.x2=properties.getInt("x2");
   this.y2=properties.getInt("y2");
   if (this.x2-this.x1<100) this.x2=this.x1+100;
   if (this.y2-this.y1<100) this.y2=this.y1+100;
   this.w=abs(x2-x1);
   this.h=abs(y2-y1);
   this.resetColorMode();
   drag = new workflow_drag_connector[2]; //Drag connector for redimension
   connection=new workflow_connector[4];  //Connector
   // --Initialise connector and drag
   for (int i=0; i<connection.length;i++) {
     connection[i]=new workflow_connector(this);
    }
   for (int i=0; i<drag.length;i++) {
     drag[i]=new workflow_drag_connector();
     drag[i].parent=this;
   }

   if (initialized) min_w=(int) (textWidth(properties.getName()+" ( "+properties.getDescription()+" )") + 20);
    min_h=20;
    update(this.x1+this.w, this.y1+this.h);
 }

 public void update(int x2, int y2) {
     this.x2=x2;
     this.y2=y2;
     this.w=abs(this.x2-this.x1);
     this.h=abs(this.y2-this.y1);

     if (w<min_w) {
         this.x2=x1+min_w;
         this.w=min_w;
         //String s=String.format("x1 %d, y1 %d, x2 %d, y2 %d, w %d, h %d", x1,y1,this.x2,this.y2,w,h);
         //Config.log(s);
     }
     if (h<min_h) {
         this.y2=y1+min_h;
         this.h=min_h;
     }
    for (int i=0; i<connection.length;i++) {
       switch(i) {
         case UP   : updateConnector(connection[i], i, x1+w/2-5, y1-14, null);
                     //connection[i].x=x1+w/2-5;connection[i].y=y1-14;
                     break;
         case DOWN : updateConnector(connection[i], i, x1+w/2-5, y2+5, null);
                     //connection[i].x=x1+w/2-5;connection[i].y=this.y2+5;
                     break;
         case LEFT : updateConnector(connection[i], i, x1-14,y1+h/2-5, null);
                     //connection[i].x=x1-14;connection[i].y=y1+h/2-5;
                     break;
         case RIGHT: updateConnector(connection[i], i, x2+5,y1+h/2-5, null);
                     //connection[i].x=this.x2+5;connection[i].y=y1+h/2-5;
                     break;
     }
   }

   for (int i=0; i<drag.length;i++) {
       switch(i) {
         //case UPLEFT:   drag[i].x=x1-14; drag[i].y=y1-10;
         //               break;
         case UPRIGHT:  drag[i].x=this.x2+5; drag[i].y=y1-10;
                        drag[i].mode=workflow_drag_connector.UPRIGHT;
                        break;
         //case DOWNLEFT: drag[i].x=x1-14; drag[i].y=y2;
         //               break;
         case DOWNRIGHT:drag[i].x=this.x2+5; drag[i].y=y2;
                        drag[i].mode=workflow_drag_connector.DOWNRIGHT;
                        break;
     }
    } //End for drag
     properties.put("x", x1); //Thit is the left-top corner
     properties.put("y", y1);
     properties.put("x2", x2); //Thit is the right-bottom corner
     properties.put("y2", y2);

} //End update

        @Override
 public void drawFeature() {
 
  //-- Debug draw box and text
    if (inside()||selected) {
     drawBox(x1,y1,x2,y2, 220);
    } else drawBox(x1,y1,x2,y2, color(189,212,240));

    // --Draw connector
   for (int i=0; i<connection.length;i++) {
       connection[i].drawFeature();
    }

  // --Draw drag
    for (int i=0; i<drag.length;i++) {
        drag[i].draw();
    }
 
      //--For small_preview editor
    if (config.get("displayConnector").equals("True")) { 
        fill(0);
        for (int i=0; i<connection.length;i++) {
         switch(i) {
            case UP:   text(str(i),connection[i].x-5, connection[i].y+5); break;
            case DOWN: text(str(i),connection[i].x-5, connection[i].y+15); break;
            case LEFT: text(str(i),connection[i].x-5, connection[i].y+5); break;
            case RIGHT:text(str(i),connection[i].x+15, connection[i].y+5); break;
            }
        }
    }


 }

 void drawBox(int x1, int y1, int x2, int y2, int target_color) {
       //Default color
       fill(target_color);
       stroke(target_color);
       rect(x1,y1-5, w, h+10);
       rect(x1-5,y1,w+10,h);
       ellipse(x1,y1,10,10);
       ellipse(x1,y1+h,10,10);
       ellipse(x1+w,y1+h,10,10);
       ellipse(x1+w,y1,10,10);    
       fill(0);
       textAlign(LEFT);
       textFont(font);
       text(properties.getName()+" ( "+properties.getDescription()+" )", x1, y1+7);
       //textFont(font);
}


  public boolean inside() {
       return (inside(mouseX, mouseY));
    }


 public boolean inside(int x, int y) {
 //--Note: the else case is to handle if the selected point is higher
   if (x1<x2&&y1<y2) {
      return (x > x1 && x < x2 && y > y1 && y < y2);
    } else return (x > x2 && x < x1 && y > y2 && y < y1);
 }

 public void featureTranslate(float x, float y) {
    this.x1+=x;
    this.y1+=y;
    this.x2+=x;
    this.y2+=y;
    properties.put("x", str(this.x1));
    properties.put("y", str(this.y1));
    properties.put("x2", str(this.x2));
    properties.put("y2", str(this.y2));

    // --Update connector and drag item
    for (int i=0; i<connection.length;i++) {
        connection[i].x+=x;
        connection[i].y+=y;
    }
    for (int i=0; i<drag.length;i++) {
        drag[i].x+=x;
        drag[i].y+=y;
    }
  }

} //End aggregator_object


class workflow_object_if extends workflow_object {

public workflow_object_if(workflow_properties obj_properties) {
  super();
  this.properties=obj_properties;
  this.resetColorMode();
  Vertex=new pvertex[3];
  connection=new workflow_connector[3]; //0, LEFT; 1, UP; 2, DOWN; 3, RIGHT (TO DO CHECK CONVENTION)
      // -- Random position
    float x1 = properties.getInt("x");
    float y1 = properties.getInt("y");
    // -- Make the vertex needed
    if (properties.getBoolean("modeSide")) {
        addVertex(x1+40, y1);
        addVertex(x1+40,y1+51);
        addVertex(x1, y1+25);
    } else {
        addVertex(x1+25, y1);
        addVertex(x1+50,y1+42);
        addVertex(x1, y1+42);
    }
    // -Create the connector
    for (int i=0; i<connection.length;i++) {
      connection[i]=new workflow_connector(this);
      connection[i].conditional=true;
    }
    // - Calculate their position
    recalculatePosition();
}

public workflow_object_if(workflow_properties obj_properties, int x1, int y1) {
  super();
  this.properties=obj_properties;
  Vertex=new pvertex[3];
  connection=new workflow_connector[3]; //0, LEFT; 1, UP; 2, DOWN; 3, RIGHT (TO DO CHECK CONVENTION)
    // -- Make the vertex needed
    if (properties.getBoolean("modeSide")) {
        addVertex(x1+40, y1);
        addVertex(x1+40,y1+51);
        addVertex(x1, y1+25);
    } else {
        addVertex(x1+25, y1);
        addVertex(x1+50,y1+42);
        addVertex(x1, y1+42);
    }
    // -Create the connector
    for (int i=0; i<connection.length;i++) {
      connection[i]=new workflow_connector(this);
      //connection[i].conditional=true;
    }
    // - Calculate their position
    recalculatePosition();
}

       @Override
        public void drawSimpleFeature() {
              // - Inside of object

    //-- Draw shape this draw a very basic shape in case we don't have the image
    //-- Note also used for the inside procedure...
    strokeWeight(1);

     if (inside()||selected) {
           fill(0xff54B5E8);        
         stroke(0);
        } else {
          fill(0xffE87ADB);        
         stroke(0);
        }
     
        stroke(255);
        beginShape(TRIANGLES);
        for(int i=0;i<Vertex_count;i++){
          vertex(Vertex[i].x,Vertex[i].y);
        }
        endShape();

    ////////////////////////////////////////////////////////////////////////////
    /// Draw SIDE

    if (properties.getBoolean("modeSide")) {

            // - draw text
        fill(0);
        textAlign(CENTER, CENTER);
        textFont(font);
        text(properties.getName(), this.x+7, this.y);
        text(properties.getDescription(), this.x, this.y+35);
        fill(128);
        text("true",this.x+40, this.y-10);
        text("false",this.x+40, this.y+10);
        fill(0);
        if (properties.isSet("Order")) text(properties.get("Order"),this.x, this.y+35);
    ////////////////////////////////////////////////////////////////////////////
    /// Draw UP (default)
    } else {
        fill(0);
        textAlign(CENTER, CENTER);
        textFont(font);
        text(properties.getName(), this.x, this.y+24);
        text(properties.getDescription(), this.x+35, this.y+24);
        fill(128);
        text("true",this.x-45, this.y+42);
        text("false",this.x+45, this.y+42);
          fill(0);
        if (properties.isSet("Order")) text(properties.get("Order"),this.x, this.y+35);
    }

    // - draw connector
    for (int i=0; i<connection.length;i++) {
        connection[i].drawFeature();
    }
    if (config.getBoolean("displayConnector")) {
        fill(0);
        for (int i=0; i<connection.length;i++) {
         //CASE 1. ModeSide?
         if (properties.getBoolean("modeSide")) {
          switch(i) {
            case UP:   text(str(i),connection[i].x-5, connection[i].y-5); break; //TRUE
            case DOWN: text(str(i),connection[i].x-5, connection[i].y+15); break; //FALSE
            case LEFT: text(str(i),connection[i].x-5, connection[i].y+5); break; //CONDITION
            }
         //CASE 2. No...
         } else {
             switch(i) {
                case UP:   text(str(i),connection[i].x-5, connection[i].y+15); break; //TRUE
                case DOWN: text(str(i),connection[i].x-5, connection[i].y+15); break;//FALSE
                case LEFT: text(str(i),connection[i].x-5, connection[i].y+5); break; //Condition
            }
         }
        }
    }
        }


        @Override
 public void drawFeature(){
    // - Inside of object

    //-- Draw shape this draw a very basic shape in case we don't have the image
    //-- Note also used for the inside procedure...
    //drawSimpleFeature();

    PImage displayImage=null;

    //--Color
     int color_border=red_border;
    int color_object=color_red;
        
      

    ////////////////////////////////////////////////////////////////////////////
    /// Draw SIDE

    if (properties.getBoolean("modeSide")) {
        if (inside()||selected) {
            displayImage=(PImage)filedata.get("ifSIDE_black.png");
            color_object=color_black;
            color_border=black_border;        
        } else {
         
             if (properties.get("colorMode").equals("RED"))  {
                 displayImage=(PImage)filedata.get("ifSIDE_red.png");
                 color_object=color_red;
                 color_border=red_border;        
             }
              if (properties.get("colorMode").equals("BLUE"))  {
                  displayImage=(PImage)filedata.get("ifSIDE_blue.png");
                  color_object=color_blue;
                    color_border=blue_border;        
              }
              if (properties.get("colorMode").equals("ORANGE")) {
                  displayImage=(PImage)filedata.get("ifSIDE_orange.png");
                  color_object=color_orange;
                  color_border=orange_border;        
              }
              if (properties.get("colorMode").equals("GREEN")) {
                  displayImage=(PImage)filedata.get("ifSIDE_green.png");
                  color_object=color_green;
                  color_border=green_border;        
              }
              if (properties.get("colorMode").equals("PURPLE")) {
                  displayImage=(PImage)filedata.get("ifSIDE_purple.png");
                  color_object=color_purple;
                  color_border=purple_border;        
              }
             if (displayImage==null) {                 
                 displayImage=(PImage)filedata.get("ifSIDE_red.png");
                 color_object=color_red;
                 color_border=red_border;  
             }
          
        }
            //if (displayImage!=null) image(displayImage,this.x-18, this.y-24);
         strokeWeight(1.2f);
    
        stroke(color_border);
        fill(color_object);
        beginShape(TRIANGLES);
        for(int i=0;i<Vertex_count;i++){
          vertex(Vertex[i].x,Vertex[i].y);
        }
        endShape();
        
        // - draw text
        fill(0);
        textAlign(CENTER, CENTER);
        textFont(font);
        text(properties.getName(), this.x+7, this.y);
        text(properties.getDescription(), this.x, this.y+35);
        fill(128);
       
        text("true",this.x+40, this.y-10);
        text("false",this.x+40, this.y+10);
        if (properties.isSet("IfStatus")) {
            fill(0);            
            if (properties.getBoolean("IfStatus")) {
               text("true",this.x+40, this.y-10);
            }  else {
                text("false",this.x+40, this.y+10);
            }
        }

    ////////////////////////////////////////////////////////////////////////////
    /// Draw UP (default)
    } else {
        if (inside()||selected) {
            displayImage=(PImage)filedata.get("ifUP_black.png");
            color_object=color_black;
            color_border=black_border;        
        } else {
            if (properties.get("colorMode").equals("RED")) {
                displayImage=(PImage)filedata.get("ifUP_red.png");
                color_object=color_red;
                 color_border=red_border;      
            }
            if (properties.get("colorMode").equals("BLUE")) {
                displayImage=(PImage)filedata.get("ifUP_blue.png");
                color_object=color_blue;
                 color_border=blue_border;      
            }
            if (properties.get("colorMode").equals("ORANGE")) {
                displayImage=(PImage)filedata.get("ifUP_orange.png");
                color_object=color_orange;
                 color_border=orange_border;      
            }
            if (properties.get("colorMode").equals("GREEN")) {
                displayImage=(PImage)filedata.get("ifUP_green.png");
                color_object=color_green;
                 color_border=green_border;      
            }
            if (properties.get("colorMode").equals("PURPLE")) {
                displayImage=(PImage)filedata.get("ifUP_purple.png");
                color_object=color_purple;
                 color_border=purple_border;      
            }
            if (displayImage==null) {
                displayImage=(PImage)filedata.get("ifUP_red.png");
                color_object=color_red;
                 color_border=red_border;      
            } 
        }
           // if (displayImage!=null) image(displayImage,this.x-25, this.y+2);
            
        strokeWeight(1.2f);
        stroke(color_border);
        fill(color_object);
        beginShape(TRIANGLES);
        for(int i=0;i<Vertex_count;i++){
          vertex(Vertex[i].x,Vertex[i].y);
        }
        endShape();
            
            
            
            // - draw text
        fill(0);
        textAlign(CENTER, CENTER);
        textFont(font);
        text(properties.getName(), this.x, this.y+24);
        text(properties.getDescription(), this.x+35, this.y+24);
        fill(128);
        text("true",this.x-45, this.y+42);
        text("false",this.x+45, this.y+42);
        if (properties.isSet("IfStatus")) {
            fill(0);           
            if (properties.getBoolean("IfStatus")) {
               text("true",this.x-45, this.y+42);
            }  else {
                 text("false",this.x+45, this.y+42);
            }
        }
    }
    // - draw connector
    for (int i=0; i<connection.length;i++) {
        connection[i].drawFeature();
    }
    if (config.getBoolean("displayConnector")) {
        fill(0);
        for (int i=0; i<connection.length;i++) {
         //CASE 1. ModeSide?
         if (properties.getBoolean("modeSide")) {
          switch(i) {
            case UP:   text(str(i),connection[i].x-5, connection[i].y-5); break; //TRUE
            case DOWN: text(str(i),connection[i].x-5, connection[i].y+15); break; //FALSE
            case LEFT: text(str(i),connection[i].x-5, connection[i].y+5); break; //CONDITION
            }
         //CASE 2. No...
         } else {
             switch(i) {
                case UP:   text(str(i),connection[i].x-5, connection[i].y+15); break; //TRUE
                case DOWN: text(str(i),connection[i].x-5, connection[i].y+15); break;//FALSE
                case LEFT: text(str(i),connection[i].x-5, connection[i].y+5); break; //Condition
            }
         }
        }
    }
    int statuscode=properties.getInt("Status");
    switch(statuscode) {
        case RunProgram.status_error:
           displayImage=(PImage)filedata.get("small_warning.png");
           image(displayImage,this.x-40, this.y-50);
           break;
        case RunProgram.status_done:
           displayImage=(PImage)filedata.get("backet_ok.png");
           image(displayImage,this.x-40, this.y-50);
           break;
         case RunProgram.status_nothing:
           break;
         case RunProgram.status_runningclassnotfound:
           displayImage=(PImage)filedata.get("small_warning.png");
           image(displayImage,this.x-40, this.y-50);
           break;
         case RunProgram.status_programnotfound:
           displayImage=(PImage)filedata.get("small_warning.png");
           image(displayImage,this.x-40, this.y-50);
            break;
         case RunProgram.status_BadRequirements:
            displayImage=(PImage)filedata.get("small_warning.png");
            image(displayImage,this.x-40, this.y-50);
            break;
        case RunProgram.status_idle:
            displayImage=(PImage)filedata.get("pause.png");
            image(displayImage,this.x-20, this.y-20);
            break;
         case RunProgram.status_running:
            displayImage=(PImage)filedata.get("play.png");
           image(displayImage,this.x-20, this.y-20);
           break;
        } //--End switch(status_code)
 }


  public void recalculatePosition() {          
      if (properties.getBoolean("modeSide")) {

      //--Calculate middle
      this.x=PApplet.parseInt((Vertex[1].x-Vertex[2].x)/2+Vertex[2].x);
      this.y=PApplet.parseInt(Vertex[0].y+25);
      //-- Initialise Connector
        for (int i=0; i<3; i++) {
           //connection[i].parent=this;
           //connection[i].conditional=true;
           switch(i) {
             case LEFT :updateConnector(connection[i], i, this.x-30,this.y-5, null);
                        //connection[i].x=this.x-30;connection[i].y=this.y-5;break; //Condition
                        break;
               case UP   :updateConnector(connection[i], i, this.x+30,this.y-25, null);
                         //connection[i].x=this.x+30;connection[i].y=this.y-25;
                         //connection[i].output=true;
                         break;   //TRUE
             case DOWN : updateConnector(connection[i], i, this.x+30,this.y+20, null);
                         //connection[i].x=this.x+30;connection[i].y=this.y+20;
                         //connection[i].output=true;
                         break;   //FALSE
           }
        }
      } else {
          this.x=PApplet.parseInt(Vertex[0].x);
          this.y=PApplet.parseInt(Vertex[0].y);
           for (int i=0; i<3; i++) {
           connection[i].parent=this;
           switch(i) {
             case LEFT :updateConnector(connection[i], i, this.x-3,this.y-10, null);
                        //connection[i].x=this.x-3;connection[i].y=this.y-10;break; //Condition
                        break;
             case UP   : updateConnector(connection[i], i, this.x-30,this.y+42, null);
                         //connection[i].x=this.x-30;connection[i].y=this.y+42;
                         //connection[i].output=true;
                         break;   //TRUE
             case DOWN : updateConnector(connection[i], i, this.x+20,this.y+42, null);
                         //connection[i].x=this.x+20;connection[i].y=this.y+42;
                         //connection[i].output=true;
                         break;   //FALSE
           }
        }
      }
   
  } //End recalculate position

  
        @Override
        public workflow_connector returnNearestConnector() {
            return connection[LEFT];
        }

        @Override
        public workflow_connector returnNearestConnector(workflow_connector source) {
            return connection[LEFT];
        }



}
/**
 * Selection object
 */
public class workflow_selection {
 int x1,y1,x2,y2;

 /**
  * Default constructor
  * @param x1
  * @param y1
  */
 public workflow_selection(int x1, int y1) {
   this.x1=x1;
   this.y1=y1;
   this.x2=x1;
   this.y2=y1;
 }

 public workflow_selection(int x1, int y1, int x2, int y2) {
   this.x1=x1;
   this.y1=y1;
   this.x2=x2;
   this.y2=y2;
 }

 public void update(int x2, int y2) {
     this.x2=x2;
     this.y2=y2;
 }

 public void drawFeature() {
   float[] dashes = { 16.0f, 8.0f, 4.0f, 8.0f };
  
   BasicStroke pen=new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER,1.0f, dashes, 0.0f);
  
   noFill();
   stroke(0);
   strokeWeight(2.0f);   
   Graphics2D g2 = ((PGraphicsJava2D) g).g2;
   BasicStroke old=(BasicStroke) g2.getStroke();
   g2.setStroke(pen);
   //--Note: the else case is to handle if the selected point is higher
   if (x1<x2&&y1<y2) {
     rect(x1,y1,abs(x2-x1), abs(y2-y1));
   }
   else if (x2<x1&&y2<y1)
   {
     rect(x2,y2,abs(x1-x2), abs(y1-y2));
   }
   else if (x2<x1&&y1<y2)
   {
     rect(x2,y1,abs(x1-x2), abs(y1-y2));
   }
   else if (x1<x2&&y2<y1)
   {
     rect(x1,y2,abs(x1-x2), abs(y1-y2));
   }
   g2.setStroke(old);
 }

 public boolean inside(int x, int y) {
 //--Note: the else case is to handle if the selected point is higher
   int ax1=min(x1, x2);
   int ax2=max(x1, x2);
   int ay1=min(y1, y2);
   int ay2=max(y1, y2); 
   return (x > ax1 && x < ax2 && y > ay1 && y < ay2);
 }
}


////////////////////////////////////////////////////////////////////////////////
/// Other dialog

  /**
   * Thiis place a special flag in the rendering loop to draw only what is neccessary...
   */
  public void startAutoUpdate() {
      auto_update=true;      
      //runthread();
  }

  public void stopAutoUpdate() {
      
      auto_update=false;
  }

  /**
   * Note: This was an attempt to update the workflow at some define point
   * but, unfortunately, it make the whole program sllloooowwww....
   */
   public void runthread() {
//             Thread thread=new Thread(){
//             long count=System.currentTimeMillis();
//
//             @Override
//             public void run() {
//                try {
//                  while(auto_update) {
//                    //--Update each second..
//                       if ((System.currentTimeMillis()-count)%5000==0) {
//                        force_redraw=true;
//                        redraw();
//                       }
//
//                  }
//                } catch(Exception e){}
//             }
//             };
//             System.out.println ("Starting thread");
//             thread.start();
    }

 
} //End armadillo workflow

