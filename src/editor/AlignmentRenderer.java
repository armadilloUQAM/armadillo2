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

import biologic.MultipleSequences;
import biologic.Sequence;
import java.util.HashMap;
import java.util.Set;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import processing.core.*;

/**
 * Beta alignment renderer
 * @author Lorde
 */
public class AlignmentRenderer extends PApplet implements ChangeListener {

  static PFont font;
  private boolean initialized=false;

  public static final int VIEW_NORMAL=0;
  public static final int VIEW_BIG=1;
  public static final int VIEW_SMALL=2;
  PImage background=null;         // Background workflow image created in draw

  private int view_mode=VIEW_BIG; //Normal display mode
  private int position=0;       //position in the sequence through ChangeListener
  private int wsize=0;          //Applet width size (set in init())
  private boolean protein=false;
  private String  identifier=""; //Identifier displayed on screen
  public MultipleSequences sequences=new MultipleSequences(); //The displayed sequences
  public boolean force_redraw=false;
  //--Saving function
  private boolean save_image=false;
  private boolean changed=false;
  private String save_filename="";
  public static HashMap<Character, Integer>nucleic_color=new HashMap<Character, Integer>();
  public static HashMap<Character, Integer>protein_color=new HashMap<Character, Integer>();

  public AlignmentRenderer(int w, int h) {

  }


  @Override
  public void setup() {
   setupColor();
   //int w=(sequences.getSequenceSize()*10)+(sequences.getMaxNameSize()*10)-50;
   int w=wsize;
   int h=sequences.getNbSequence()*10+70;
//   if (sequences.getNbSequence()>0) {
//       identifier=String.format("%s - %d sequence(s) - Total - %d %s", sequences.getName(), sequences.getNbSequence(), sequences.getSequenceSize(), (isProtein()?"aa":"bp"));
//   }
   if (w<400) w=400;
   if (h<120) h=120;
   size(w, h);
   if (font==null) font=loadFont(dataPath("Times.vlw"));
   textFont(font);
   cursor(CROSS);
   initialized=true;
   force_redraw=true;
   redraw();
   //draw();
}

 @Override
 public void draw() {
      if (!force_redraw&&!isChanged()&&background!=null&&!save_image&&background.width==width&&background.height==height) {
            image(background,0,0);
      } else {
      noStroke();
      background(255);
      if (sequences.getNbSequence()>0) {
            switch(view_mode) {
                case AlignmentRenderer.VIEW_NORMAL: drawNormal(); break;
                case AlignmentRenderer.VIEW_BIG: drawDetails(); break;
                case AlignmentRenderer.VIEW_SMALL: drawSmall();break;
            }
      } else {
          textAlign(CENTER);
          text(" - No sequence to display - ", width/2, height/2);
      }

      //////////////////////////////////////////////
      /// Save background

      background=get();
      force_redraw=false;
      changed=false;

      //////////////////////////////////////////////
      /// Saving image
      fill(0);
      //text(str(position), 10,10);
        if (isSave_image()) {
               save(getSave_filename());
                setSave_image(false);
        }

      }
  }

 ///////////////////////////////////////////////////////////////////////////////
 /// Setup color

 /**
  * Color from RASMol  (http://life.nthu.edu.tw/~fmhsu/rasframe/COLORS.HTM)
  * ASP,GLU   bright red [230,10,10]     CYS,MET     yellow [230,230,0]
  * LYS,ARG   blue       [20,90,255]     SER,THR     orange [250,150,0]
  * PHE,TYR   mid blue   [50,50,170]     ASN,GLN     cyan   [0,220,220]
  * GLY       light grey [235,235,235]   LEU,VAL,ILE green  [15,130,15]
  * ALA       dark grey  [200,200,200]   TRP         pink   [180,90,180]
  * HIS       pale blue  [130,130,210]   PRO         flesh  [220,150,130]
  *
  *     * G - Glycine (Gly)
    * P - Proline (Pro)
    * A - Alanine (Ala)
    * V - Valine (Val)
    * L - Leucine (Leu)
    * I - Isoleucine (Ile)
    * M - Methionine (Met)
    * C - Cysteine (Cys)
    * F - Phenylalanine (Phe)
    * Y - Tyrosine (Tyr)
    * W - Tryptophan (Trp)
    * H - Histidine (His)
    * K - Lysine (Lys)
    * R - Arginine (Arg)
    * Q - Glutamine (Gln)
    * N - Asparagine (Asn)
    * E - Glutamic Acid (Glu)
    * D - Aspartic Acid (Asp)
    * S - Serine (Ser)
    * T - Threonine (Thr)
  */
 private void setupColor() {
     protein_color.put('G',color(235,235,235));
     protein_color.put('P',color(220,150,130));
     protein_color.put('A',color(200,200,200));
     protein_color.put('V',color(15,130,15));
     protein_color.put('L',color(15,130,15));
     protein_color.put('I',color(15,130,15));
     protein_color.put('M',color(230,230,0));
     protein_color.put('C',color(230,230,0));
     protein_color.put('F',color(50,50,170));
     protein_color.put('Y',color(50,50,170));
     protein_color.put('W',color(180,90,180));
     protein_color.put('H',color(130,130,210));
     protein_color.put('K',color(20,90,255));
     protein_color.put('R',color(20,90,255));
     protein_color.put('Q',color(0,220,220));
     protein_color.put('N',color(0,220,220));
     protein_color.put('E',color(230,10,10));
     protein_color.put('D',color(230,10,10));
     protein_color.put('S',color(250,150,0));
     protein_color.put('T',color(250,150,0));
     protein_color.put('-',color(255,255,255));
     protein_color.put('?',color(255,255,255));
     protein_color.put('.',color(255,255,255));

     nucleic_color.put('A',color(255,0,0));
     nucleic_color.put('U',color(0,255,0));
     nucleic_color.put('T',color(0,255,0));
     nucleic_color.put('G',color(0,0,255));
     nucleic_color.put('C',color(255,255,0));
     nucleic_color.put('N',color(255,254,255));
     nucleic_color.put('-',color(255,255,255));
     nucleic_color.put('.',color(255,255,255));
     nucleic_color.put('?',color(255,255,255));

 }

 ///////////////////////////////////////////////////////////////////////////////
 /// Drawing functions

 public void drawDetails() {
    //int startx=sequences.getMaxNameSize()*7;
   int startx=250;
     int maxpos=(width-(startx+50))/10;
   //--Draw graduation
     int size=sequences.getSequenceSize();
   for (int i=0; i<size; i++) {
      if (i%20==0) {
          fill(192);
          rect(startx+(i)*10, 48, 1,sequences.getNbSequence()*10+5);
          fill(0);
          text(str(i+position+1), startx+(i)*10, 48);
          text(str(i+position+1), startx+(i)*10, 65+(sequences.getNbSequence())*10);
      }
      if (i>maxpos) break;
      if (i+position>size) break;
    }

    //--Draw sequences
     for (int i=0; i<sequences.getNbSequence(); i++) {
       Sequence seq=sequences.getSequences().get(i);
       textAlign(LEFT);
       fill(220);
       rect(10,50+(i*10), 225, 9);
       fill(0);
       //--Sequence name
       String name=seq.getName();
       name=(name.length()>35?name.substring(0,32)+"...":name);
       text(name,10, 58+(i)*10);
       //--Sequence info
       for (int j=position; j<seq.getSize();j++) {
           char ca=seq.getSequence().charAt(j);
              int c = returnColor(ca);
           fill(c);
           rect(startx+(j-position)*10, 50+(i)*10, 9,9);
           fill(0);
           text(ca,startx+1+(j-position)*10, 58+(i)*10);
           if (j-position>maxpos) break;
        }
    } //End sequence len

    draw_legend();
  }

  public void drawNormal() {
    int startx=250;
    int maxpos=(width-(startx+100))/3;

  //--draw graduation

   int size=sequences.getSequenceSize();
   for (int i=0; i<size; i++) {
      if (i%20==0) {
          fill(192);
          rect(startx+(i)*3, 48, 1,sequences.getNbSequence()*3+5);
          fill(0);
          text(str(i+position+1), startx+(i)*3, 48);
          text(str(i+position+1), startx+(i)*3, 65+(sequences.getNbSequence())*3);
      }
      if (i>maxpos) break;
      if (i+position>size) break;
    }
    //--Draw sequences
    fill(0);
    for (int i=0; i<sequences.getNbSequence(); i++) {
       Sequence seq=sequences.getSequences().get(i);
       //--Sequence name
       textAlign(LEFT);
       fill(220);
       rect(10,50+(i)*10, 225, 9);
       stroke(220);
       line(235,50+(i*10),startx, 50+(i)*3);
       noStroke();
       fill(0);
       String name=seq.getName();
       name=(name.length()>35?name.substring(0,32)+"...":name);
       text(name,10, 58+(i)*10);
       //Sequence rendering
       int pos=0;
       for (int j=position; j<seq.getSize();j++) {
          char ca=seq.getSequence().charAt(j);
          int c = returnColor(ca);
          fill(c);
          rect(startx+(pos*3), 50+(i)*3, 3,3);
           if (ca=='-'||ca=='X'||ca=='?') {
               fill(128);
               rect(startx+(pos*3), 50+(i)*3+1, 3,1);
           }
          pos++;
          if (pos>maxpos) break;
        }
   } //End sequence len

    draw_legend();
 }

 public void drawSmall() {
    int startx=250;
    int maxpos=(width-(startx+100));

  //--draw graduation

   int size=sequences.getSequenceSize();
   for (int i=0; i<size; i++) {
      if (i%50==0) {
          fill(192);
          rect(startx+(i), 48, 1,sequences.getNbSequence()*3+5);
          fill(0);
          text(str(i+position+1), startx+(i), 48);
          text(str(i+position+1), startx+(i), 65+(sequences.getNbSequence())*3);
      }
      if (i>maxpos) break;
      if (i+position>size) break;
    }
    //--Draw sequences
    fill(0);
    for (int i=0; i<sequences.getNbSequence(); i++) {
       Sequence seq=sequences.getSequences().get(i);
       //--Sequence name
       textAlign(LEFT);
       fill(220);
       rect(10,50+(i)*10, 225, 9);
       stroke(220);
       line(235,50+(i*10),startx, 50+(i)*3);
       noStroke();
       fill(0);
       String name=seq.getName();
       name=(name.length()>35?name.substring(0,32)+"...":name);
       text(name,10, 58+(i)*10);
       //Sequence rendering
       int pos=0;
       for (int j=position; j<seq.getSize();j++) {
          char ca=seq.getSequence().charAt(j);
          int c = returnColor(ca);
          fill(c);
          rect(startx+(pos), 50+(i), 1,1);
//           if (ca=='-'||ca=='X'||ca=='?') {
//               fill(128);
//               rect(startx+(pos), 50+(i)+1, 1,1);
//           }
          pos++;
          if (pos>maxpos) break;
        }
   } //End sequence len

    draw_legend();
 }

 public void draw_legend() {
   //--Sequence(s) info
   fill(0);
   rect(5,26,600,1);
   text(identifier, 5, 36);
   //--Legend
   Set<Character> letters= (isProtein()?protein_color.keySet():nucleic_color.keySet());

    int i=0;
    for (Character c:letters) {
        fill(returnColor(c));
        rect(50+(i*20), 10, 16,14);
        fill(0);
        text(c, 55+(i*20), 20);
        i++;
    }
    text("Legend",5,20);
 }

 public void saveImage(String filename) {
        this.save_filename=filename;
        this.save_image=true;
        force_redraw=true;
        redraw();
 }

 public void setViewDetails() {
     this.view_mode++;
     if (view_mode>2) view_mode=0;
     changed=true;
     force_redraw=true;
     redraw();
 }

 public void setViewDetails(int view_mode) {
     this.view_mode=view_mode;
     if (view_mode>2||view_mode<0) view_mode=this.VIEW_NORMAL;
     changed=true;
     force_redraw=true;
     redraw();
 }


public void mouseOver() {
//    // ARROW, CROSS, HAND, MOVE, TEXT, WAIT
    force_redraw=true;
   redraw();
}


@Override
public void mouseMoved() {
//    redraw();
}



public void init(MultipleSequences sequences, int w) {
    this.sequences=sequences;
    this.setProtein(sequences.isAA());
    //this.wsize=w-20;
    this.wsize=400;
    this.init();
}

public int returnColor(char i) {
 Integer return_color=0;
  if (  isProtein()) {
     return_color=protein_color.get(i);
  } else {
     return_color=nucleic_color.get(i);
  }
  if (return_color==null) return color(128,128,128);
 return return_color;
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
     * @return the save_image
     */
    public boolean isSave_image() {
        return save_image;
    }

    /**
     * @param save_image the save_image to set
     */
    public void setSave_image(boolean save_image) {
        this.save_image = save_image;
    }

    /**
     * @return the save_filename
     */
    public String getSave_filename() {
        return save_filename;
    }

    /**
     * @param save_filename the save_filename to set
     */
    public void setSave_filename(String save_filename) {
        this.save_filename = save_filename;
    }

    public void stateChanged(ChangeEvent e) {
        JSlider tmp=(JSlider)e.getSource();
        position=tmp.getValue();
        //force_redraw=true;
        changed=true;
        redraw();
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
        this.changed = changed;
    }

    public void setProtein(boolean b) {
        this.protein=b;
        this.changed=true;
        this.redraw=true;
        identifier=String.format("%s - %d sequence(s) - Total - %d %s", sequences.getName(), sequences.getNbSequence(), sequences.getSequenceSize(), (isProtein()?"aa":"bp"));
    }

    /**
     * @return the protein
     */
    public boolean isProtein() {
        return protein;
    }


}
