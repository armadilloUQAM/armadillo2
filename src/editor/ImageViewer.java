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

public class ImageViewer extends PApplet implements ChangeListener {

  static PFont font;
  private boolean initialized=false;

  
  PImage background=null;         // Background workflow image created in draw


  public boolean force_redraw=false;
  //--Saving function
  private boolean save_image=false;
  private boolean changed=false;
  private String save_filename="";
  String filename="";
  boolean image_mode=true;
  private PImage image;
  private PShape shape;

  @Override
  public void setup() {  
  if (!filename.isEmpty()) {
      if (filename.endsWith(".svg")||filename.endsWith(".SVG")) {
          // TO DO
          //          image_mode=false;
          //          SVGtoJPEG h=new SVGtoJPEG();
          //          if (h.convert(filename, filename+".jpg")) image=loadImage(filename+".jpg");
      } else {
          try {
          image=loadImage(filename);
          } catch(Exception e) {}
      }
  }
   if (shape!=null) {
       int x=max((int)shape.getWidth(),1200);
       int y=max((int)shape.getHeight(),1200);
       size(x,y);
       System.out.println("svg :"+x+" "+y);
   } else if (image!=null) {
       size(image.width,image.height);
        System.out.println("image "+image.width+" "+image.height);
   } else size(1200, 1200);
   if (font==null) font=loadFont(dataPath("Times.vlw"));
   textFont(font);
   cursor(CROSS);
   setInitialized(true);
   force_redraw=true;
   redraw();
   //draw();
}

 @Override
 public void draw() {
     
      
      background(255);
      if (image!=null) {
          image(image,0,0);
      } else if (shape!=null) {
          shape(shape,0,0);
      } else {
          fill(0);
          textFont(font);
          text("Unable to find image...",width/2, height/2);
      }

      //////////////////////////////////////////////
      /// Save background

      background=get();
      force_redraw=false;
      changed=false;

      //////////////////////////////////////////////
      /// Saving image
     
      //text(str(position), 10,10);
//        if (isSave_image()) {
//               save(getSave_filename());
//                setSave_image(false);
//        }

  }
  

 
 public void saveImage(String filename) {
        this.save_filename=filename;
        this.save_image=true;
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



public void init(String filename) {
   this.filename=filename;
    this.init();
}

   

   


    public void stateChanged(ChangeEvent e) {
        JSlider tmp=(JSlider)e.getSource();
        //position=tmp.getValue();
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

}
